package org.generationcp.middleware.manager;

import org.generationcp.middleware.dao.FormulaDAO;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;

public class DaoFactory {
	
	private HibernateSessionProvider sessionProvider;

	public DaoFactory(HibernateSessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
	}
	
	public FormulaDAO getFormulaDAO() {
		final FormulaDAO formulaDAO = new FormulaDAO();
		formulaDAO.setSession(this.sessionProvider.getSession());
		return formulaDAO;
	}

}
