package org.generationcp.middleware.api.germplasm.pedigree.cop;

import com.google.common.collect.TreeBasedTable;
import org.generationcp.middleware.api.germplasm.pedigree.GermplasmPedigreeService;
import org.generationcp.middleware.api.germplasm.pedigree.GermplasmPedigreeServiceAsyncImpl;
import org.generationcp.middleware.api.germplasm.pedigree.GermplasmTreeNode;
import org.generationcp.middleware.exceptions.MiddlewareRequestException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.CopMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

import static java.time.Duration.between;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.time.DurationFormatUtils.formatDurationHMS;
import static org.generationcp.middleware.util.Debug.debug;
import static org.generationcp.middleware.util.Debug.info;

@Service
@Transactional
public class CopServiceAsyncImpl implements CopServiceAsync {

	private static final Logger LOG = LoggerFactory.getLogger(CopServiceAsyncImpl.class);

	public static final int COP_MAX_JOB_COUNT;
	private static final int COP_MAX_JOB_COUNT_DEFAULT = 1;
	static {
		final String envVar = System.getenv("COP_MAX_JOB_COUNT");
		COP_MAX_JOB_COUNT = !isBlank(envVar) ? Integer.parseInt(envVar) : COP_MAX_JOB_COUNT_DEFAULT;
	}
	public static final Semaphore semaphore = new Semaphore(COP_MAX_JOB_COUNT);
	/**
	 * Map gid -> bool (finished, not finished)
	 */
	private static final Map<Integer, Boolean> gidProcessingQueue = new ConcurrentHashMap<>();

	private final GermplasmPedigreeService germplasmPedigreeService;
	private final DaoFactory daoFactory;

	public CopServiceAsyncImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
		this.germplasmPedigreeService = new GermplasmPedigreeServiceAsyncImpl(sessionProvider);
	}

	/*
	 * TODO
	 *  - keep Future task list, Future.cancel (https://stackoverflow.com/a/38882801/1384743) by api
	 *  - api to retrieve current progress
	 *  - email on finish/error
	 *  - progress considering tree height
	 */
	@Override
	@Async
	public void calculateAsync(
		final Set<Integer> gids,
		final TreeBasedTable<Integer, Integer, Double> matrix,
		final TreeBasedTable<Integer, Integer, Double> matrixNew
	) {

		try {
			// Avoid query multiple times
			final Map<Integer, GermplasmTreeNode> nodes = new HashMap<>();

			// matrix copy because CopCalculation also stores intermediate results
			final CopCalculation copCalculation = new CopCalculation(TreeBasedTable.create(matrix));

			// TODO verify/improve perf
			for (final Integer gid1 : gids) {
				for (final Integer gid2 : gids) {
					if (!(matrix.contains(gid1, gid2) || matrix.contains(gid2, gid1))) {

						final GermplasmTreeNode gid1Tree;
						if (!nodes.containsKey(gid1)) {
							info("retrieving pedigree: gid=%d", gid1);
							final Instant start = Instant.now();
							gid1Tree = this.germplasmPedigreeService.getGermplasmPedigreeTree(gid1, null, true);
							final Instant end = Instant.now();
							debug("pedigree retrieved: gid=%d, Duration: %s", gid1, formatDurationHMS(between(start, end).toMillis()));
							trackNodes(gid1Tree, nodes);
						} else {
							gid1Tree = nodes.get(gid1);
						}

						final GermplasmTreeNode gid2Tree;
						if (!nodes.containsKey(gid2)) {
							info("retrieving pedigree: gid=%d", gid2);
							final Instant start = Instant.now();
							gid2Tree = this.germplasmPedigreeService.getGermplasmPedigreeTree(gid2, null, true);
							final Instant end = Instant.now();
							debug("pedigree retrieved: gid=%d, Duration: %s", gid2, formatDurationHMS(between(start, end).toMillis()));
							trackNodes(gid2Tree, nodes);
						} else {
							gid2Tree = nodes.get(gid2);
						}

						final double cop = copCalculation.coefficientOfParentage(gid1Tree, gid2Tree);
						matrixNew.put(gid1, gid2, cop);
						matrix.put(gid1, gid2, cop);
					}
				}
				// track progress
				gidProcessingQueue.put(gid1, Boolean.TRUE);
			}

			for (final Map.Entry<Integer, Map<Integer, Double>> rowEntrySet : matrixNew.rowMap().entrySet()) {
				for (final Integer column : rowEntrySet.getValue().keySet()) {
					final Integer row = rowEntrySet.getKey();
					final CopMatrix copMatrix = new CopMatrix(row, column, matrixNew.get(row, column));
					this.daoFactory.getCopMatrixDao().save(copMatrix);
				}
			}
		} catch (final RuntimeException ex) {
			LOG.error("Error in CopServiceAsyncImpl.calculateAsync(), gids=" + gids + ", message: " + ex.getMessage(), ex);
		} finally {
			gids.forEach(gidProcessingQueue::remove);
			semaphore.release();
		}

	}

	@Override
	public void prepareExecution(final Set<Integer> gids) {
		debug("%s", semaphore.availablePermits());
		if (!semaphore.tryAcquire()) {
			throw new MiddlewareRequestException("", "cop.max.thread.error", COP_MAX_JOB_COUNT);
		}
		for (final Integer gid : gids) {
			if (null != gidProcessingQueue.putIfAbsent(gid, Boolean.FALSE)) {
				gids.forEach(gidProcessingQueue::remove);
				semaphore.release();
				throw new MiddlewareRequestException("", "cop.gids.in.queue", getProgress());
			}
		}
	}

	@Override
	public void checkIfThreadExists(final Set<Integer> gids) {
		for (final Integer gid : gids) {
			if (null != gidProcessingQueue.get(gid)) {
				throw new MiddlewareRequestException("", "cop.gids.in.queue", getProgress());
			}
		}
	}

	private static double getProgress() {
		return gidProcessingQueue.values().stream().mapToInt(aBoolean -> Boolean.TRUE.equals(aBoolean) ? 1 : 0)
			.summaryStatistics()
			.getAverage() * 100;
	}

	private static void trackNodes(final GermplasmTreeNode gid1Tree, final Map<Integer, GermplasmTreeNode> nodes) {
		nodes.put(gid1Tree.getGid(), gid1Tree);
		GermplasmTreeNode femaleParentNode = gid1Tree.getFemaleParentNode();
		while (femaleParentNode != null) {
			nodes.put(femaleParentNode.getGid(), femaleParentNode);
			femaleParentNode = femaleParentNode.getFemaleParentNode();
		}
		GermplasmTreeNode maleParentNode = gid1Tree.getMaleParentNode();
		while (maleParentNode != null) {
			nodes.put(maleParentNode.getGid(), maleParentNode);
			maleParentNode = maleParentNode.getMaleParentNode();
		}
	}
}
