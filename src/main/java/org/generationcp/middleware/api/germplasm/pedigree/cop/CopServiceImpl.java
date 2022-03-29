package org.generationcp.middleware.api.germplasm.pedigree.cop;

import com.google.common.collect.Table;
import org.generationcp.middleware.api.germplasm.pedigree.GermplasmPedigreeService;
import org.generationcp.middleware.api.germplasm.pedigree.GermplasmTreeNode;
import org.generationcp.middleware.exceptions.MiddlewareRequestException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Future;

@Service
@Transactional
public class CopServiceImpl implements CopService {

	@Autowired
	private GermplasmPedigreeService germplasmPedigreeService;

	@Autowired
	private CopServiceAsync copServiceAsync;

	private DaoFactory daoFactory;

	public CopServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	// FIXME used for testing. refactor tests
	@Override
	public double coefficientOfParentage(final int gid1, final int gid2, final BTypeEnum btype) {
		final GermplasmTreeNode gid1Tree = this.germplasmPedigreeService.getGermplasmPedigreeTree(gid1, null, true);
		final GermplasmTreeNode gid2Tree = this.germplasmPedigreeService.getGermplasmPedigreeTree(gid2, null, true);
		final CopCalculation copCalculation = new CopCalculation(btype);
		copCalculation.populateOrder(gid1Tree, 0);
		copCalculation.populateOrder(gid2Tree, 0);
		return copCalculation.coefficientOfParentage(gid1Tree, gid2Tree);
	}

	// FIXME used for testing. refactor tests
	@Override
	public double coefficientOfInbreeding(final int gid, final BTypeEnum btype) {
		final GermplasmTreeNode gidTree = this.germplasmPedigreeService.getGermplasmPedigreeTree(gid, null, true);
		final CopCalculation copCalculation = new CopCalculation(btype);
		copCalculation.populateOrder(gidTree, 0);
		return copCalculation.coefficientOfInbreeding(gidTree);
	}

	@Override
	public CopResponse coefficientOfParentage(Set<Integer> gids, final Integer listId,
		final HttpServletRequest request, final HttpServletResponse response) throws IOException {

		if (listId != null) {
			gids = new LinkedHashSet<>(this.daoFactory.getGermplasmListDataDAO().getGidsByListId(listId));
		}

		if (this.copServiceAsync.threadExists(gids)) {
			return new CopResponse(this.copServiceAsync.getProgress(gids));
		}

		if (listId != null) {
			final File csv = getCsv(listId);
			if (csv.exists()) {
				return new CopResponse(true);
			} else {
				throw new MiddlewareRequestException("", "cop.csv.not.exists");
			}
		}

		final Table<Integer, Integer, Double> matrix = this.daoFactory.getCopMatrixDao().getByGids(gids);

		// if all cop values are calculated, return them
		boolean someCopValuesExists = false;
		for (final Integer gid1 : gids) {
			for (final Integer gid2 : gids) {
				if (matrix.contains(gid1, gid2) || matrix.contains(gid2, gid1)) {
					someCopValuesExists = true;
				}
			}
		}
		if (someCopValuesExists) {
			return new CopResponse(matrix);
		}

		// no thread nor matrix for gids
		throw new MiddlewareRequestException("", "cop.no.queue.error");
	}

	@Override
	public CopResponse calculateCoefficientOfParentage(final Set<Integer> gids, final Integer listId) {
		final Table<Integer, Integer, Double> matrix = this.daoFactory.getCopMatrixDao().getByGids(gids);

		// if all cop values are calculated, return them
		boolean requiresProcessing = false;
		for (final Integer gid1 : gids) {
			for (final Integer gid2 : gids) {
				if (!(matrix.contains(gid1, gid2) || matrix.contains(gid2, gid1))) {
					requiresProcessing = true;
				}
			}
		}

		if (requiresProcessing) {
			if (this.copServiceAsync.threadExists(gids)) {
				throw new MiddlewareRequestException("", "cop.gids.in.queue", this.copServiceAsync.getProgress(gids));
			}

			this.copServiceAsync.prepareExecution(gids);
			final Future<Boolean> booleanFuture = this.copServiceAsync.calculateAsync(gids, matrix, listId);
			this.copServiceAsync.trackFutureTask(gids, booleanFuture);
			return new CopResponse(this.copServiceAsync.getProgress(gids));
		}

		return new CopResponse(matrix);
	}

	@Override
	public CopResponse calculateCoefficientOfParentage(final Integer listId) {
		final Set<Integer> gids = new LinkedHashSet<>(this.daoFactory.getGermplasmListDataDAO().getGidsByListId(listId));
		return this.calculateCoefficientOfParentage(gids, listId);
	}

	@Override
	public void cancelJobs(Set<Integer> gids, final Integer listId) {
		if (listId != null) {
			gids = new LinkedHashSet<>(this.daoFactory.getGermplasmListDataDAO().getGidsByListId(listId));
		}
		this.copServiceAsync.cancelJobs(gids);
	}

	@Override
	public File downloadCoefficientOfParentage(final Integer listId) {
		return getCsv(listId);
	}

	private static File getCsv(final Integer listId) {
		final String fileFullPath = CopUtils.getFileFullPath(listId);
		final File file = new File(fileFullPath);
		return file;
	}

}
