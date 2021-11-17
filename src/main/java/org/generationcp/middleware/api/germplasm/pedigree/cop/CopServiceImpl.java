package org.generationcp.middleware.api.germplasm.pedigree.cop;

import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import org.generationcp.middleware.api.germplasm.pedigree.GermplasmPedigreeService;
import org.generationcp.middleware.api.germplasm.pedigree.GermplasmTreeNode;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

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
	public double coefficientOfParentage(final int gid1, final int gid2) {
		final GermplasmTreeNode gid1Tree = this.germplasmPedigreeService.getGermplasmPedigreeTree(gid1, null, true);
		final GermplasmTreeNode gid2Tree = this.germplasmPedigreeService.getGermplasmPedigreeTree(gid2, null, true);
		final CopCalculation copCalculation = new CopCalculation();
		return copCalculation.coefficientOfParentage(gid1Tree, gid2Tree);
	}

	// FIXME used for testing. refactor tests
	@Override
	public double coefficientOfInbreeding(final int gid) {
		final GermplasmTreeNode gidTree = this.germplasmPedigreeService.getGermplasmPedigreeTree(gid, null, true);
		final CopCalculation copCalculation = new CopCalculation();
		return copCalculation.coefficientOfInbreeding(gidTree);
	}

	// TODO split into view/calculate
	@Override
	public Table<Integer, Integer, Double> coefficientOfParentage(final Set<Integer> gids) {
		final TreeBasedTable<Integer, Integer, Double> matrix = this.daoFactory.getCopMatrixDao().getByGids(gids);
		final TreeBasedTable<Integer, Integer, Double> matrixNew = TreeBasedTable.create();

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
			this.copServiceAsync.checkIfThreadExists(gids);

			this.copServiceAsync.prepareExecution(gids);
			this.copServiceAsync.calculateAsync(gids, matrix, matrixNew);
			return ImmutableTable.of();
		}

		return matrix;
	}

}
