package org.generationcp.middleware.manager.derived_variables;

import org.generationcp.middleware.dao.FormulaDAO;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;

public class FormulaDaoFactory {
	
	private HibernateSessionProvider sessionProvider;

	public FormulaDaoFactory(HibernateSessionProvider sessionProvider) {
		super();
		this.sessionProvider = sessionProvider;
	}
	
	public FormulaDAO getFormulaDAO() {
		final FormulaDAO formulaDAO = new FormulaDAO();
		formulaDAO.setSession(this.sessionProvider.getSession());
		return formulaDAO;
	}

}
