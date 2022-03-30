package org.generationcp.middleware.api.germplasm.pedigree.cop;

import com.google.common.collect.Table;
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

	private DaoFactory daoFactory;

	public CopServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	/**
	 * used for integration testing
	 */
	@Override
	public double coefficientOfParentage(final int gid1, final int gid2, final BTypeEnum btype) {
		final GermplasmTreeNode gid1Tree = this.germplasmPedigreeService.getGermplasmPedigreeTree(gid1, null, true);
		final GermplasmTreeNode gid2Tree = this.germplasmPedigreeService.getGermplasmPedigreeTree(gid2, null, true);
		final CopCalculation copCalculation = new CopCalculation(btype);
		copCalculation.populateOrder(gid1Tree, 0);
		copCalculation.populateOrder(gid2Tree, 0);
		return copCalculation.coefficientOfParentage(gid1Tree, gid2Tree);
	}

	/**
	 * used for integration testing
	 */
	@Override
	public double coefficientOfInbreeding(final int gid, final BTypeEnum btype) {
		final GermplasmTreeNode gidTree = this.germplasmPedigreeService.getGermplasmPedigreeTree(gid, null, true);
		final CopCalculation copCalculation = new CopCalculation(btype);
		copCalculation.populateOrder(gidTree, 0);
		return copCalculation.coefficientOfInbreeding(gidTree);
	}
	@Override
	public Table<Integer, Integer, Double> getCopMatrixByGids(final Set<Integer> gids) {
		return this.daoFactory.getCopMatrixDao().getByGids(gids);
	}

}
