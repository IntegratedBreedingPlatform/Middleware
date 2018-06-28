package org.generationcp.middleware.service.impl.derived_variables;

import org.generationcp.middleware.dao.FormulaDAO;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.derived_variables.Formula;
import org.generationcp.middleware.service.api.derived_variables.FormulaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class FormulaServiceImpl implements FormulaService {

	private final HibernateSessionProvider sessionProvider;

	public FormulaServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
	}

	private FormulaDAO getFormulaDAO() {
		final FormulaDAO dao = new FormulaDAO();
		dao.setSession(this.sessionProvider.getSession());
		return dao;
	}

	// TODO return DTO outside Transactional, otherwise we'll get LazyInitializationException when accessing lazy collections
	@Override
	public Formula getByTargetId(final Integer targetId) {
		return this.getFormulaDAO().getByTargetVariableId(targetId);
	}
}
