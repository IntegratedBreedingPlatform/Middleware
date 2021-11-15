package org.generationcp.middleware.api.germplasm.pedigree.cop;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import org.generationcp.middleware.api.germplasm.pedigree.GermplasmPedigreeService;
import org.generationcp.middleware.api.germplasm.pedigree.GermplasmTreeNode;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.CopMatrix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.time.Duration.between;
import static org.apache.commons.lang3.time.DurationFormatUtils.formatDurationHMS;
import static org.generationcp.middleware.util.Debug.debug;

@Service
@Transactional
public class CopServiceImpl implements CopService {

	@Autowired
	private GermplasmPedigreeService germplasmPedigreeService;

	private DaoFactory daoFactory;

	public CopServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public double coefficientOfParentage(final int gid1, final int gid2) {
		final GermplasmTreeNode gid1Tree = this.germplasmPedigreeService.getGermplasmPedigreeTree(gid1, null, true);
		final GermplasmTreeNode gid2Tree = this.germplasmPedigreeService.getGermplasmPedigreeTree(gid2, null, true);
		final CopCalculation copCalculation = new CopCalculation();
		return copCalculation.coefficientOfParentage(gid1Tree, gid2Tree);
	}

	@Override
	public double coefficientOfInbreeding(final int gid) {
		final GermplasmTreeNode gidTree = this.germplasmPedigreeService.getGermplasmPedigreeTree(gid, null, true);
		final CopCalculation copCalculation = new CopCalculation();
		return copCalculation.coefficientOfInbreeding(gidTree);
	}

	@Override
	public Table<Integer, Integer, Double> coefficientOfParentage(final Set<Integer> gids) {
		final TreeBasedTable<Integer, Integer, Double> matrix = this.daoFactory.getCopMatrixDao().getByGids(gids);
		final TreeBasedTable<Integer, Integer, Double> matrixNew = TreeBasedTable.create();

		// matrix copy because CopCalculation also stores intermediate results
		final CopCalculation copCalculation = new CopCalculation(TreeBasedTable.create(matrix));

		// Avoid query multiple times
		final Map<Integer, GermplasmTreeNode> nodes = new HashMap<>();

		// TODO verify/improve perf

		for (final Integer gid1 : gids) {
			for (final Integer gid2 : gids) {
				if (!(matrix.contains(gid1, gid2) || matrix.contains(gid2, gid1))) {

					final GermplasmTreeNode gid1Tree;
					if (!nodes.containsKey(gid1)) {
						debug("retrieving pedigree: gid=%d", gid1);
						final Instant start = Instant.now();
						gid1Tree = this.germplasmPedigreeService.getGermplasmPedigreeTree(gid1, null, true);
						final Instant end = Instant.now();
						debug("pedigree retrieved: gid=%d, Duration: %s", gid1, formatDurationHMS(between(start, end).toMillis()));
						this.trackNodes(gid1Tree, nodes);
					} else {
						gid1Tree = nodes.get(gid1);
					}

					final GermplasmTreeNode gid2Tree;
					if (!nodes.containsKey(gid2)) {
						debug("retrieving pedigree: gid=%d", gid2);
						final Instant start = Instant.now();
						gid2Tree = this.germplasmPedigreeService.getGermplasmPedigreeTree(gid2, null, true);
						final Instant end = Instant.now();
						debug("pedigree retrieved: gid=%d, Duration: %s", gid2, formatDurationHMS(between(start, end).toMillis()));
						this.trackNodes(gid2Tree, nodes);
					} else {
						gid2Tree = nodes.get(gid2);
					}

					final double cop = copCalculation.coefficientOfParentage(gid1Tree, gid2Tree);
					matrixNew.put(gid1, gid2, cop);
					matrix.put(gid1, gid2, cop);
				}
			}
		}

		for (final Map.Entry<Integer, Map<Integer, Double>> rowEntrySet : matrixNew.rowMap().entrySet()) {
			for (final Integer column : rowEntrySet.getValue().keySet()) {
				final Integer row = rowEntrySet.getKey();
				final CopMatrix copMatrix = new CopMatrix(row, column, matrixNew.get(row, column));
				this.daoFactory.getCopMatrixDao().save(copMatrix);
			}
		}

		return matrix;
	}

	private void trackNodes(final GermplasmTreeNode gid1Tree, final Map<Integer, GermplasmTreeNode> nodes) {
		// TODO stream.iterate() ... limit()
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
