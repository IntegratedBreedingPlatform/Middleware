package org.generationcp.middleware.manager;

import org.generationcp.middleware.dao.CropPersonDAO;
import org.generationcp.middleware.dao.CropTypeDAO;
import org.generationcp.middleware.dao.PersonDAO;
import org.generationcp.middleware.dao.ProjectActivityDAO;
import org.generationcp.middleware.dao.ProjectDAO;
import org.generationcp.middleware.dao.ProjectUserInfoDAO;
import org.generationcp.middleware.dao.RoleDAO;
import org.generationcp.middleware.dao.StandardPresetDAO;
import org.generationcp.middleware.dao.ToolDAO;
import org.generationcp.middleware.dao.UserInfoDAO;
import org.generationcp.middleware.dao.WorkbenchSidebarCategoryDAO;
import org.generationcp.middleware.dao.WorkbenchSidebarCategoryLinkDAO;
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

	public CropPersonDAO getCropPersonDAO() {
		final CropPersonDAO cropPersonDAO = new CropPersonDAO();
		cropPersonDAO.setSession(this.sessionProvider.getSession());
		return cropPersonDAO;
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

	public CropTypeDAO getCropTypeDAO() {
		final CropTypeDAO cropTypeDAO = new CropTypeDAO();
		cropTypeDAO.setSession(this.sessionProvider.getSession());
		return cropTypeDAO;
	}

	public ProjectActivityDAO getProjectActivityDAO() {
		final ProjectActivityDAO projectActivityDAO = new ProjectActivityDAO();
		projectActivityDAO.setSession(this.sessionProvider.getSession());
		return projectActivityDAO;
	}

	public ProjectDAO getProjectDAO() {
		final ProjectDAO projectDAO = new ProjectDAO();
		projectDAO.setSession(this.sessionProvider.getSession());
		return projectDAO;
	}

	public ToolDAO getToolDAO() {
		final ToolDAO toolDAO = new ToolDAO();
		toolDAO.setSession(this.sessionProvider.getSession());
		return toolDAO;
	}

	public StandardPresetDAO getStandardPresetDAO() {
		final StandardPresetDAO standardPresetDAO = new StandardPresetDAO();
		standardPresetDAO.setSession(this.sessionProvider.getSession());
		return standardPresetDAO;
	}

	public WorkbenchSidebarCategoryDAO getWorkbenchSidebarCategoryDAO() {
		final WorkbenchSidebarCategoryDAO workbenchSidebarCategoryDAO = new WorkbenchSidebarCategoryDAO();
		workbenchSidebarCategoryDAO.setSession(this.sessionProvider.getSession());
		return workbenchSidebarCategoryDAO;
	}

	public WorkbenchSidebarCategoryLinkDAO getWorkbenchSidebarCategoryLinkDAO() {
		final WorkbenchSidebarCategoryLinkDAO workbenchSidebarCategoryLinkDAO = new WorkbenchSidebarCategoryLinkDAO();
		workbenchSidebarCategoryLinkDAO.setSession(this.sessionProvider.getSession());
		return workbenchSidebarCategoryLinkDAO;
	}

}
