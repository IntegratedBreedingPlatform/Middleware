package org.generationcp.middleware.manager;

import org.generationcp.middleware.dao.WorkbenchUserDAO;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;

public class WorkbenchDaoFactory {

	private HibernateSessionProvider sessionProvider;

	public WorkbenchDaoFactory() {
		// no-arg constuctor is required by CGLIB proxying used by Spring 3x and older.
	}

	public WorkbenchDaoFactory(final HibernateSessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
	}

	public WorkbenchUserDAO getWorkbenchUserDAO() {
		final WorkbenchUserDAO workbenchUserDAO = new WorkbenchUserDAO();
		workbenchUserDAO.setSession(this.sessionProvider.getSession());
		return workbenchUserDAO;
	}

}
