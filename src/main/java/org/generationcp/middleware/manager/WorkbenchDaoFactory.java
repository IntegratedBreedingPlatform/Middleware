package org.generationcp.middleware.manager;

import org.generationcp.middleware.dao.CropPersonDAO;
import org.generationcp.middleware.dao.CropTypeDAO;
import org.generationcp.middleware.dao.OneTimePasswordDAO;
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
import org.generationcp.middleware.dao.UserDeviceMetaDataDAO;
import org.generationcp.middleware.dao.UserInfoDAO;
import org.generationcp.middleware.dao.UserRoleDao;
import org.generationcp.middleware.dao.WorkbenchSidebarCategoryLinkDAO;
import org.generationcp.middleware.dao.WorkbenchUserDAO;
import org.generationcp.middleware.dao.feedback.FeedbackDAO;
import org.generationcp.middleware.dao.feedback.FeedbackUserDAO;
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
		return new WorkbenchUserDAO(this.sessionProvider.getSession());
	}

	public PersonDAO getPersonDAO() {
		return new PersonDAO(this.sessionProvider.getSession());
	}

	public CropPersonDAO getCropPersonDAO() {
		return new CropPersonDAO(this.sessionProvider.getSession());
	}

	public UserInfoDAO getUserInfoDAO() {
		return new UserInfoDAO(this.sessionProvider.getSession());
	}

	public ProjectUserInfoDAO getProjectUserInfoDAO() {
		return new ProjectUserInfoDAO(this.sessionProvider.getSession());
	}

	public RoleDAO getRoleDAO() {
		return new RoleDAO(this.sessionProvider.getSession());
	}

	public CropTypeDAO getCropTypeDAO() {
		return new CropTypeDAO(this.sessionProvider.getSession());
	}

	public ProjectActivityDAO getProjectActivityDAO() {
		return new ProjectActivityDAO(this.sessionProvider.getSession());
	}

	public ProjectDAO getProjectDAO() {
		return new ProjectDAO(this.sessionProvider.getSession());
	}

	public ToolDAO getToolDAO() {
		return new ToolDAO(this.sessionProvider.getSession());
	}

	public RoleTypeDAO getRoleTypeDAO() {
		return new RoleTypeDAO(this.sessionProvider.getSession());
	}

	public PermissionDAO getPermissionDAO() {
		return new PermissionDAO(this.sessionProvider.getSession());
	}

	public RoleTypePermissionDAO getRoleTypePermissionDAO() {
		return new RoleTypePermissionDAO(this.sessionProvider.getSession());
	}

	public WorkbenchSidebarCategoryLinkDAO getWorkbenchSidebarCategoryLinkDao() {
		return new WorkbenchSidebarCategoryLinkDAO(this.sessionProvider.getSession());
	}

	public UserRoleDao getUserRoleDao() {
		return new UserRoleDao(this.sessionProvider.getSession());
	}

	public RoleDAO getRoleDao() {
		return new RoleDAO(this.sessionProvider.getSession());
	}

	public RPackageDAO getRPackageDao() {
		return new RPackageDAO(this.sessionProvider.getSession());
	}

	public RCallDAO getRCallDao() {
		return new RCallDAO(this.sessionProvider.getSession());
	}

	public RCallParameterDAO getRCallParameterDao() {
		return new RCallParameterDAO(this.sessionProvider.getSession());
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

	public FeedbackUserDAO getFeedbackUserDAO() {
		return new FeedbackUserDAO(this.sessionProvider.getSession());
	}

	public OneTimePasswordDAO getOneTimePasswordDAO() {
		return new OneTimePasswordDAO(this.sessionProvider.getSession());
	}

	public UserDeviceMetaDataDAO getUserDeviceMetaDataDAO() {
		return new UserDeviceMetaDataDAO(this.sessionProvider.getSession());
	}

}
