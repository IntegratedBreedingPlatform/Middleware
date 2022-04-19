package org.generationcp.middleware.api.germplasm.pedigree.cop;

import org.generationcp.middleware.api.germplasm.pedigree.GermplasmPedigreeService;
import org.generationcp.middleware.api.germplasm.pedigree.GermplasmPedigreeServiceAsyncImpl;
import org.generationcp.middleware.api.germplasm.pedigree.GermplasmTreeNode;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.CopMatrix;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class CopServiceAsyncImpl implements CopServiceAsync {

	GermplasmPedigreeService germplasmPedigreeService;
	DaoFactory daoFactory;

	public CopServiceAsyncImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
		this.germplasmPedigreeService = new GermplasmPedigreeServiceAsyncImpl(sessionProvider);
	}

	@Override
	public GermplasmTreeNode getGermplasmPedigreeTree(final Integer gid, final Integer level, final boolean includeDerivativeLines) {
		return this.germplasmPedigreeService.getGermplasmPedigreeTree(gid, level, includeDerivativeLines);
	}

	@Override
	public void saveOrUpdate(final CopMatrix copMatrix) {
		this.daoFactory.getCopMatrixDao().saveOrUpdate(copMatrix);
	}
}
