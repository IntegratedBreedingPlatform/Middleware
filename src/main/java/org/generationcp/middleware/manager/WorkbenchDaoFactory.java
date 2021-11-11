package org.generationcp.middleware.manager;

import org.generationcp.middleware.dao.CropPersonDAO;
import org.generationcp.middleware.dao.CropTypeDAO;
import org.generationcp.middleware.dao.PermissionDAO;
import org.generationcp.middleware.dao.PersonDAO;
import org.generationcp.middleware.dao.ProjectActivityDAO;
import org.generationcp.middleware.dao.ProjectDAO;
import org.generationcp.middleware.dao.ProjectUserInfoDAO;
import org.generationcp.middleware.dao.RCallDAO;
import org.generationcp.middleware.dao.RCallParameterDAO;
import org.generationcp.middleware.dao.RPackageDAO;
import org.generationcp.middleware.dao.RoleDAO;
import org.generationcp.middleware.dao.RoleTypeDAO;
import org.generationcp.middleware.dao.RoleTypePermissionDAO;
import org.generationcp.middleware.dao.ToolDAO;
import org.generationcp.middleware.dao.UserInfoDAO;
import org.generationcp.middleware.dao.UserRoleDao;
import org.generationcp.middleware.dao.WorkbenchSidebarCategoryDAO;
import org.generationcp.middleware.dao.WorkbenchSidebarCategoryLinkDAO;
import org.generationcp.middleware.dao.WorkbenchUserDAO;
import org.generationcp.middleware.dao.feedback.FeedbackDAO;
import org.generationcp.middleware.dao.releasenote.ReleaseNoteDAO;
import org.generationcp.middleware.dao.releasenote.ReleaseNoteUserDAO;
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

	public RoleTypeDAO getRoleTypeDAO() {
		final RoleTypeDAO roleTypeDAO = new RoleTypeDAO();
		roleTypeDAO.setSession(this.sessionProvider.getSession());
		return roleTypeDAO;
	}

	public PermissionDAO getPermissionDAO() {
		final PermissionDAO permissionDAO = new PermissionDAO();
		permissionDAO.setSession(this.sessionProvider.getSession());
		return permissionDAO;
	}

	public RoleTypePermissionDAO getRoleTypePermissionDAO() {
		final RoleTypePermissionDAO roleTypePermissionDAO = new RoleTypePermissionDAO();
		roleTypePermissionDAO.setSession(this.sessionProvider.getSession());
		return roleTypePermissionDAO;
	}

	public WorkbenchSidebarCategoryDAO getWorkbenchSidebarCategoryDao() {

		final WorkbenchSidebarCategoryDAO workbenchSidebarCategoryDAO = new WorkbenchSidebarCategoryDAO();
		workbenchSidebarCategoryDAO.setSession(this.sessionProvider.getSession());
		return workbenchSidebarCategoryDAO;
	}

	public WorkbenchSidebarCategoryLinkDAO getWorkbenchSidebarCategoryLinkDao() {

		final WorkbenchSidebarCategoryLinkDAO workbenchSidebarCategoryLinkDAO = new WorkbenchSidebarCategoryLinkDAO();

		workbenchSidebarCategoryLinkDAO.setSession(this.sessionProvider.getSession());
		return workbenchSidebarCategoryLinkDAO;
	}

	public UserRoleDao getUserRoleDao() {
		final UserRoleDao userRoleDao = new UserRoleDao();
		userRoleDao.setSession(this.sessionProvider.getSession());
		return userRoleDao;
	}

	public RoleDAO getRoleDao() {
		final RoleDAO roleDao = new RoleDAO();
		roleDao.setSession(this.sessionProvider.getSession());
		return roleDao;
	}

	public RPackageDAO getRPackageDao() {
		final RPackageDAO rPackageDAO = new RPackageDAO();
		rPackageDAO.setSession(this.sessionProvider.getSession());
		return rPackageDAO;
	}

	public RCallDAO getRCallDao() {
		final RCallDAO rCallDAO = new RCallDAO();
		rCallDAO.setSession(this.sessionProvider.getSession());
		return rCallDAO;
	}

	public RCallParameterDAO getRCallParameterDao() {
		final RCallParameterDAO rCallParameterDAO = new RCallParameterDAO();
		rCallParameterDAO.setSession(this.sessionProvider.getSession());
		return rCallParameterDAO;
	}

	public ReleaseNoteDAO getReleaseNoteDAO() {
		return new ReleaseNoteDAO(this.sessionProvider.getSession());
	}

	public ReleaseNoteUserDAO getReleaseNoteUserDAO() {
		return new ReleaseNoteUserDAO(this.sessionProvider.getSession());
	}

	public FeedbackDAO getFeedbackDAO() {
		return new FeedbackDAO(this.sessionProvider.getSession());
	}

}
