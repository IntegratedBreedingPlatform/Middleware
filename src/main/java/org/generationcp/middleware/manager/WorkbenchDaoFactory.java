package org.generationcp.middleware.manager;

import org.generationcp.middleware.dao.PersonDAO;
import org.generationcp.middleware.dao.ProjectUserInfoDAO;
import org.generationcp.middleware.dao.RoleDAO;
import org.generationcp.middleware.dao.UserInfoDAO;
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

	public PersonDAO getPersonDAO() {
		final PersonDAO personDAO = new PersonDAO();
		personDAO.setSession(this.sessionProvider.getSession());
		return personDAO;
	}

	public UserInfoDAO getUserInfoDAO() {
		final UserInfoDAO userInfoDAO = new UserInfoDAO();
		userInfoDAO.setSession(this.sessionProvider.getSession());
		return userInfoDAO;
	}

	public ProjectUserInfoDAO getProjectUserInfoDAO() {
		final ProjectUserInfoDAO projectUserInfoDAO = new ProjectUserInfoDAO();
		projectUserInfoDAO.setSession(this.sessionProvider.getSession());
		return projectUserInfoDAO;
	}

	public RoleDAO getRoleDAO() {
		final RoleDAO roleDAO = new RoleDAO();
		roleDAO.setSession(this.sessionProvider.getSession());
		return roleDAO;
	}

}
