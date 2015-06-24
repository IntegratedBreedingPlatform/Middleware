/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.generationcp.middleware.dao.CropTypeDAO;
import org.generationcp.middleware.dao.IbdbUserMapDAO;
import org.generationcp.middleware.dao.PersonDAO;
import org.generationcp.middleware.dao.ProjectActivityDAO;
import org.generationcp.middleware.dao.ProjectBackupDAO;
import org.generationcp.middleware.dao.ProjectDAO;
import org.generationcp.middleware.dao.ProjectUserInfoDAO;
import org.generationcp.middleware.dao.ProjectUserMysqlAccountDAO;
import org.generationcp.middleware.dao.ProjectUserRoleDAO;
import org.generationcp.middleware.dao.RoleDAO;
import org.generationcp.middleware.dao.SecurityQuestionDAO;
import org.generationcp.middleware.dao.StandardPresetDAO;
import org.generationcp.middleware.dao.TemplateSettingDAO;
import org.generationcp.middleware.dao.ToolConfigurationDAO;
import org.generationcp.middleware.dao.ToolDAO;
import org.generationcp.middleware.dao.UserDAO;
import org.generationcp.middleware.dao.UserInfoDAO;
import org.generationcp.middleware.dao.WorkbenchDatasetDAO;
import org.generationcp.middleware.dao.WorkbenchRuntimeDataDAO;
import org.generationcp.middleware.dao.WorkbenchSettingDAO;
import org.generationcp.middleware.dao.WorkbenchSidebarCategoryDAO;
import org.generationcp.middleware.dao.WorkbenchSidebarCategoryLinkDAO;
import org.generationcp.middleware.dao.WorkflowTemplateDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.presets.StandardPreset;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.IbdbUserMap;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.generationcp.middleware.pojos.workbench.ProjectBackup;
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.generationcp.middleware.pojos.workbench.ProjectUserMysqlAccount;
import org.generationcp.middleware.pojos.workbench.ProjectUserRole;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.SecurityQuestion;
import org.generationcp.middleware.pojos.workbench.TemplateSetting;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolConfiguration;
import org.generationcp.middleware.pojos.workbench.ToolType;
import org.generationcp.middleware.pojos.workbench.UserInfo;
import org.generationcp.middleware.pojos.workbench.WorkbenchDataset;
import org.generationcp.middleware.pojos.workbench.WorkbenchRuntimeData;
import org.generationcp.middleware.pojos.workbench.WorkbenchSetting;
import org.generationcp.middleware.pojos.workbench.WorkbenchSidebarCategory;
import org.generationcp.middleware.pojos.workbench.WorkbenchSidebarCategoryLink;
import org.generationcp.middleware.pojos.workbench.WorkflowTemplate;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the WorkbenchDataManager interface. To instantiate this class, a Hibernate Session must be passed to its constructor.
 */
public class WorkbenchDataManagerImpl implements WorkbenchDataManager {

	private static final Logger LOG = LoggerFactory.getLogger(WorkbenchDataManagerImpl.class);

	private final HibernateSessionProvider sessionProvider;

	private Project currentlastOpenedProject;

	private CropTypeDAO cropTypeDao;
	private IbdbUserMapDAO ibdbUserMapDao;
	private PersonDAO personDao;
	private ProjectActivityDAO projectActivityDao;
	private ProjectDAO projectDao;
	private ProjectUserMysqlAccountDAO projectUserMysqlAccountDao;
	private ProjectUserRoleDAO projectUserRoleDao;
	private ProjectUserMysqlAccountDAO projectUserMysqlAccountDAO;
	private ProjectUserInfoDAO projectUserInfoDao;
	private RoleDAO roleDao;
	private SecurityQuestionDAO securityQuestionDao;
	private ToolConfigurationDAO toolConfigurationDao;
	private ToolDAO toolDao;
	private UserDAO userDao;

	private UserInfoDAO userInfoDao;

	private WorkbenchDatasetDAO workbenchDatasetDao;
	private WorkbenchRuntimeDataDAO workbenchRuntimeDataDao;
	private WorkbenchSettingDAO workbenchSettingDao;
	private WorkflowTemplateDAO workflowTemplateDao;
	private ProjectBackupDAO projectBackupDao;
	private WorkbenchSidebarCategoryDAO workbenchSidebarCategoryDAO;
	private WorkbenchSidebarCategoryLinkDAO workbenchSidebarCategoryLinkDAO;
	private TemplateSettingDAO templateSettingDAO;

	private String installationDirectory;
	private StandardPresetDAO standardPresetDAO;

	public WorkbenchDataManagerImpl(HibernateSessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
	}

	public Session getCurrentSession() {
		return this.sessionProvider.getSession();
	}

	private CropTypeDAO getCropTypeDao() {
		if (this.cropTypeDao == null) {
			this.cropTypeDao = new CropTypeDAO();
		}
		this.cropTypeDao.setSession(this.getCurrentSession());
		return this.cropTypeDao;
	}

	private IbdbUserMapDAO getIbdbUserMapDao() {
		if (this.ibdbUserMapDao == null) {
			this.ibdbUserMapDao = new IbdbUserMapDAO();
		}
		this.ibdbUserMapDao.setSession(this.getCurrentSession());
		return this.ibdbUserMapDao;
	}

	private PersonDAO getPersonDao() {
		if (this.personDao == null) {
			this.personDao = new PersonDAO();
		}
		this.personDao.setSession(this.getCurrentSession());
		return this.personDao;
	}

	private ProjectActivityDAO getProjectActivityDao() {
		if (this.projectActivityDao == null) {
			this.projectActivityDao = new ProjectActivityDAO();
		}
		this.projectActivityDao.setSession(this.getCurrentSession());
		return this.projectActivityDao;
	}

	private ProjectUserMysqlAccountDAO getProjectUserMysqlAccountDAO() {
		if (this.projectUserMysqlAccountDAO == null) {
			this.projectUserMysqlAccountDAO = new ProjectUserMysqlAccountDAO();
		}
		this.projectUserMysqlAccountDAO.setSession(this.getCurrentSession());
		return this.projectUserMysqlAccountDAO;
	}

	private ProjectDAO getProjectDao() {
		if (this.projectDao == null) {
			this.projectDao = new ProjectDAO();
		}
		this.projectDao.setSession(this.getCurrentSession());
		return this.projectDao;
	}

	private ProjectUserMysqlAccountDAO getProjectUserMysqlAccountDao() {
		if (this.projectUserMysqlAccountDao == null) {
			this.projectUserMysqlAccountDao = new ProjectUserMysqlAccountDAO();
		}
		this.projectUserMysqlAccountDao.setSession(this.getCurrentSession());
		return this.projectUserMysqlAccountDao;
	}

	@Override
	public ProjectUserInfoDAO getProjectUserInfoDao() {
		if (this.projectUserInfoDao == null) {
			this.projectUserInfoDao = new ProjectUserInfoDAO();
		}
		this.projectUserInfoDao.setSession(this.getCurrentSession());
		return this.projectUserInfoDao;
	}

	@Override
	public void updateProjectsRolesForProject(Project project, List<ProjectUserRole> newRoles) throws MiddlewareQueryException {
		List<ProjectUserRole> deleteRoles = this.getProjectUserRolesByProject(project);

		// remove all previous roles
		for (ProjectUserRole projectUserRole : deleteRoles) {
			this.deleteProjectUserRole(projectUserRole);
		}

		// add the new roles
		for (ProjectUserRole projectUserRole : newRoles) {
			User user = new User();
			user.setUserid(projectUserRole.getUserId());
			this.addProjectUserRole(project, user, projectUserRole.getRole());
		}
	}

	private ProjectUserRoleDAO getProjectUserRoleDao() {
		if (this.projectUserRoleDao == null) {
			this.projectUserRoleDao = new ProjectUserRoleDAO();
		}
		this.projectUserRoleDao.setSession(this.getCurrentSession());
		return this.projectUserRoleDao;
	}

	private RoleDAO getRoleDao() {
		if (this.roleDao == null) {
			this.roleDao = new RoleDAO();
		}
		this.roleDao.setSession(this.getCurrentSession());
		return this.roleDao;
	}

	private SecurityQuestionDAO getSecurityQuestionDao() {
		if (this.securityQuestionDao == null) {
			this.securityQuestionDao = new SecurityQuestionDAO();
		}
		this.securityQuestionDao.setSession(this.getCurrentSession());
		return this.securityQuestionDao;
	}

	private ToolConfigurationDAO getToolConfigurationDao() {
		if (this.toolConfigurationDao == null) {
			this.toolConfigurationDao = new ToolConfigurationDAO();
		}
		this.toolConfigurationDao.setSession(this.getCurrentSession());
		return this.toolConfigurationDao;
	}

	@Override
	public ToolDAO getToolDao() {
		if (this.toolDao == null) {
			this.toolDao = new ToolDAO();
		}
		this.toolDao.setSession(this.getCurrentSession());
		return this.toolDao;
	}

	private UserDAO getUserDao() {
		if (this.userDao == null) {
			this.userDao = new UserDAO();
		}
		this.userDao.setSession(this.getCurrentSession());
		return this.userDao;
	}

	private UserInfoDAO getUserInfoDao() {
		if (this.userInfoDao == null) {
			this.userInfoDao = new UserInfoDAO();
		}
		this.userInfoDao.setSession(this.getCurrentSession());
		return this.userInfoDao;
	}

	private WorkbenchDatasetDAO getWorkbenchDatasetDao() {
		if (this.workbenchDatasetDao == null) {
			this.workbenchDatasetDao = new WorkbenchDatasetDAO();
		}
		this.workbenchDatasetDao.setSession(this.getCurrentSession());
		return this.workbenchDatasetDao;
	}

	private WorkbenchRuntimeDataDAO getWorkbenchRuntimeDataDao() {
		if (this.workbenchRuntimeDataDao == null) {
			this.workbenchRuntimeDataDao = new WorkbenchRuntimeDataDAO();
		}
		this.workbenchRuntimeDataDao.setSession(this.getCurrentSession());
		return this.workbenchRuntimeDataDao;
	}

	private WorkbenchSettingDAO getWorkbenchSettingDao() {
		if (this.workbenchSettingDao == null) {
			this.workbenchSettingDao = new WorkbenchSettingDAO();
		}
		this.workbenchSettingDao.setSession(this.getCurrentSession());
		return this.workbenchSettingDao;
	}

	private WorkflowTemplateDAO getWorkflowTemplateDao() {
		if (this.workflowTemplateDao == null) {
			this.workflowTemplateDao = new WorkflowTemplateDAO();
		}
		this.workflowTemplateDao.setSession(this.getCurrentSession());
		return this.workflowTemplateDao;
	}

	private ProjectBackupDAO getProjectBackupDao() {
		if (this.projectBackupDao == null) {
			this.projectBackupDao = new ProjectBackupDAO();
		}
		this.projectBackupDao.setSession(this.getCurrentSession());
		return this.projectBackupDao;
	}

	private WorkbenchSidebarCategoryDAO getWorkbenchSidebarCategoryDao() {
		if (this.workbenchSidebarCategoryDAO == null) {
			this.workbenchSidebarCategoryDAO = new WorkbenchSidebarCategoryDAO();
		}
		this.workbenchSidebarCategoryDAO.setSession(this.getCurrentSession());
		return this.workbenchSidebarCategoryDAO;
	}

	private WorkbenchSidebarCategoryLinkDAO getWorkbenchSidebarCategoryLinkDao() {
		if (this.workbenchSidebarCategoryLinkDAO == null) {
			this.workbenchSidebarCategoryLinkDAO = new WorkbenchSidebarCategoryLinkDAO();
		}
		this.workbenchSidebarCategoryLinkDAO.setSession(this.getCurrentSession());
		return this.workbenchSidebarCategoryLinkDAO;
	}

	private TemplateSettingDAO getTemplateSettingDao() {
		if (this.templateSettingDAO == null) {
			this.templateSettingDAO = new TemplateSettingDAO();
		}
		this.templateSettingDAO.setSession(this.getCurrentSession());
		return this.templateSettingDAO;
	}

	@Override
	public StandardPresetDAO getStandardPresetDAO() {
		if (this.standardPresetDAO == null) {
			this.standardPresetDAO = new StandardPresetDAO();
		}

		this.standardPresetDAO.setSession(this.getCurrentSession());
		return this.standardPresetDAO;
	}

	private void rollbackTransaction(Transaction trans) {
		if (trans != null) {
			trans.rollback();
		}
	}

	private void logAndThrowException(String message, Exception e) throws MiddlewareQueryException {
		WorkbenchDataManagerImpl.LOG.error(e.getMessage(), e);
		throw new MiddlewareQueryException(message + e.getMessage(), e);
	}

	private void logAndThrowException(String message) throws MiddlewareQueryException {
		WorkbenchDataManagerImpl.LOG.error(message);
		throw new MiddlewareQueryException(message);
	}

	@Override
	public List<Project> getProjects() throws MiddlewareQueryException {
		return this.getProjectDao().getAll();
	}

	@Override
	public List<Project> getProjects(int start, int numOfRows) throws MiddlewareQueryException {
		return this.getProjectDao().getAll(start, numOfRows);
	}

	@Override
	public List<Project> getProjectsByUser(User user) throws MiddlewareQueryException {
		return this.getProjectUserRoleDao().getProjectsByUser(user);
	}

	@Override
	public Project saveOrUpdateProject(Project project) throws MiddlewareQueryException {
		Transaction trans = null;
		Session session = this.getCurrentSession();

		try {
			trans = session.beginTransaction();
			this.getProjectDao().merge(project);

			// TODO: copy the workbench template created by the project into the
			// project_workflow_step table

			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException(
					"Cannot save Project: WorkbenchDataManager.saveOrUpdateProject(project=" + project + "): " + e.getMessage(), e);
		}

		return project;
	}

	@Override
	public ProjectUserInfo saveOrUpdateProjectUserInfo(ProjectUserInfo projectUserInfo) throws MiddlewareQueryException {
		Transaction trans = null;
		Session session = this.getCurrentSession();

		try {
			trans = session.beginTransaction();
			this.getProjectUserInfoDao().merge(projectUserInfo);

			// TODO: copy the workbench template created by the project into the
			// project_workflow_step table

			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Cannot save ProjectUserInfo: WorkbenchDataManager.saveOrUpdateProjectUserInfo(project="
					+ projectUserInfo + "): " + e.getMessage(), e);
		}

		return projectUserInfo;
	}

	@Override
	public Project addProject(Project project) throws MiddlewareQueryException {
		Session session = this.getCurrentSession();
		Transaction trans = null;

		try {
			trans = session.beginTransaction();
			project.setUniqueID(UUID.randomUUID().toString());
			this.getProjectDao().save(project);
			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Cannot save Project: WorkbenchDataManager.addProject(project=" + project + "): " + e.getMessage(), e);
		}

		return project;
	}

	@Override
	public Project mergeProject(Project project) throws MiddlewareQueryException {
		Session session = this.getCurrentSession();
		Transaction trans = null;

		try {
			trans = session.beginTransaction();
			this.getProjectDao().merge(project);
			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException(
					"Cannot save Project: WorkbenchDataManager.updateProject(project=" + project + "): " + e.getMessage(), e);
		}

		return project;
	}

	@Override
	public void deleteProjectDependencies(Project project) throws MiddlewareQueryException {

		try {
			Long projectId = project.getProjectId();
			List<ProjectActivity> projectActivities =
					this.getProjectActivitiesByProjectId(projectId, 0, (int) this.countProjectActivitiesByProjectId(projectId));
			for (ProjectActivity projectActivity : projectActivities) {
				this.deleteProjectActivity(projectActivity);
			}

			List<ProjectUserRole> projectUsers = this.getProjectUserRolesByProject(project);
			for (ProjectUserRole projectUser : projectUsers) {
				this.deleteProjectUserRole(projectUser);
			}

			List<ProjectUserMysqlAccount> mysqlaccounts =
					this.getProjectUserMysqlAccountDAO().getByProjectId(project.getProjectId().intValue());
			if (mysqlaccounts != null) {
				for (ProjectUserMysqlAccount mysqlaccount : mysqlaccounts) {
					this.deleteProjectUserMysqlAccount(mysqlaccount);
				}
			}

			List<WorkbenchDataset> datasets =
					this.getWorkbenchDatasetByProjectId(projectId, 0, (int) this.countWorkbenchDatasetByProjectId(projectId));
			for (WorkbenchDataset dataset : datasets) {
				this.deleteWorkbenchDataset(dataset);
			}

			List<ProjectUserInfo> projectUserInfos = this.getByProjectId(projectId.intValue());
			for (ProjectUserInfo projectUserInfo : projectUserInfos) {
				this.deleteProjectUserInfoDao(projectUserInfo);
			}

			List<ProjectBackup> projectBackups = this.getProjectBackups(project);
			for (ProjectBackup projectBackup : projectBackups) {
				this.deleteProjectBackup(projectBackup);
			}

			List<IbdbUserMap> ibdbUserMaps = this.getIbdbUserMapsByProjectId(project.getProjectId());
			for (IbdbUserMap ibdbUserMap : ibdbUserMaps) {
				this.deleteIbdbProjectBackup(ibdbUserMap);
			}

			// remove template settings per project
			TemplateSetting setting = new TemplateSetting();
			setting.setProjectId(projectId.intValue());

			List<TemplateSetting> templateSettings = this.getTemplateSettings(setting);
			for (TemplateSetting templateSetting : templateSettings) {
				this.deleteTemplateSetting(templateSetting);
			}

		} catch (Exception e) {

			this.logAndThrowException("Cannot delete Project Dependencies: WorkbenchDataManager.deleteProjectDependencies(project="
					+ project + "): " + e.getMessage(), e);
		}
	}

	public void deleteIbdbProjectBackup(IbdbUserMap ibdbUserMap) throws MiddlewareQueryException {
		Session session = this.getCurrentSession();
		Transaction trans = null;
		try {
			trans = session.beginTransaction();
			this.getIbdbUserMapDao().makeTransient(ibdbUserMap);
			trans.commit();

		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Cannot delete Project: WorkbenchDataManager.deleteIbdbProjectBackup(ibdbUserMap=" + ibdbUserMap
					+ "): " + e.getMessage(), e);
		}
	}

	public List<IbdbUserMap> getIbdbUserMapsByProjectId(Long projectId) throws MiddlewareQueryException {
		return this.getIbdbUserMapDao().getIbdbUserMapByID(projectId);
	}

	public void deleteProjectUserInfoDao(ProjectUserInfo projectUserInfo) throws MiddlewareQueryException {
		Session session = this.getCurrentSession();
		Transaction trans = null;
		try {
			trans = session.beginTransaction();
			this.getProjectUserInfoDao().makeTransient(projectUserInfo);
			trans.commit();

		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Cannot delete ProjectUserInfo: WorkbenchDataManager.deleteProjectUserInfoDao(projectUserInfo="
					+ projectUserInfo + "): " + e.getMessage(), e);
		}
	}

	public void deleteProjectUserMysqlAccount(ProjectUserMysqlAccount mysqlaccount) throws MiddlewareQueryException {
		Session session = this.getCurrentSession();
		Transaction trans = null;
		try {
			trans = session.beginTransaction();
			this.getProjectUserMysqlAccountDAO().makeTransient(mysqlaccount);
			trans.commit();

		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException(
					"Cannot delete ProjectUserMysqlAccount: WorkbenchDataManager.deleteProjectUserMysqlAccount(mysqlaccount="
							+ mysqlaccount + "): " + e.getMessage(), e);
		}
	}

	@Override
	public void deleteProject(Project project) throws MiddlewareQueryException {
		Transaction trans = null;
		Session session = this.getCurrentSession();
		try {
			trans = session.beginTransaction();
			this.getProjectDao().deleteProject(project.getProjectName());
			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException(
					"Cannot delete Project: WorkbenchDataManager.deleteProject(project=" + project + "): " + e.getMessage(), e);
		}
	}

	@Override
	public List<WorkflowTemplate> getWorkflowTemplates() throws MiddlewareQueryException {
		return this.getWorkflowTemplateDao().getAll();
	}

	@Override
	public List<WorkflowTemplate> getWorkflowTemplateByName(String name) throws MiddlewareQueryException {
		return this.getWorkflowTemplateDao().getByName(name);
	}

	@Override
	public List<WorkflowTemplate> getWorkflowTemplates(int start, int numOfRows) throws MiddlewareQueryException {
		return this.getWorkflowTemplateDao().getAll(start, numOfRows);
	}

	@Override
	public List<Tool> getAllTools() throws MiddlewareQueryException {
		return this.getToolDao().getAll();
	}

	@Override
	public Tool getToolWithName(String toolId) throws MiddlewareQueryException {
		return this.getToolDao().getByToolName(toolId);
	}

	@Override
	public List<Tool> getToolsWithType(ToolType toolType) throws MiddlewareQueryException {
		return this.getToolDao().getToolsByToolType(toolType);
	}

	@Override
	public boolean isValidUserLogin(String username, String password) throws MiddlewareQueryException {
		if (this.getUserDao().getByUsernameAndPassword(username, password) != null) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isPersonExists(String firstName, String lastName) throws MiddlewareQueryException {
		return this.getPersonDao().isPersonExists(firstName, lastName);
	}

	@Override
	public boolean isPersonWithEmailExists(String email) throws MiddlewareQueryException {
		return this.getPersonDao().isPersonWithEmailExists(email);
	}

	@Override
	public boolean isUsernameExists(String userName) throws MiddlewareQueryException {
		return this.getUserDao().isUsernameExists(userName);
	}

	@Override
	public boolean isPersonWithUsernameAndEmailExists(String username, String email) throws MiddlewareQueryException {
		return this.getPersonDao().isPersonWithUsernameAndEmailExists(username, email);
	}

	@Override
	public Integer addPerson(Person person) throws MiddlewareQueryException {
		Transaction trans = null;
		Session session = this.getCurrentSession();

		Integer idPersonSaved = null;
		try {
			// begin save transaction
			trans = session.beginTransaction();

			SQLQuery q = session.createSQLQuery("SELECT MAX(personid) FROM persons");
			Integer personId = (Integer) q.uniqueResult();

			if (personId == null || personId.intValue() < 0) {
				person.setId(1);
			} else {
				person.setId(personId + 1);
			}

			Person recordSaved = this.getPersonDao().saveOrUpdate(person);
			idPersonSaved = recordSaved.getId();

			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException(
					"Error encountered while saving Person: WorkbenchDataManager.addPerson(person=" + person + "): " + e.getMessage(), e);
		}
		return idPersonSaved;
	}

	@Override
	public Integer addUser(User user) throws MiddlewareQueryException {
		Transaction trans = null;
		Session session = this.getCurrentSession();

		Integer idUserSaved = null;
		try {
			// begin save transaction
			trans = session.beginTransaction();

			SQLQuery q = session.createSQLQuery("SELECT MAX(userid) FROM users");
			Integer userId = (Integer) q.uniqueResult();

			if (userId == null || userId.intValue() < 0) {
				user.setUserid(1);
			} else {
				user.setUserid(userId + 1);
			}

			User recordSaved = this.getUserDao().saveOrUpdate(user);
			idUserSaved = recordSaved.getUserid();

			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException(
					"Error encountered while saving User: WorkbenchDataManager.addUser(user=" + user + "): " + e.getMessage(), e);
		}

		return idUserSaved;

	}

	@Override
	public Project getProjectById(Long projectId) throws MiddlewareQueryException {
		return this.getProjectDao().getById(projectId);
	}

	@Override
	public Project getProjectByName(String projectName) throws MiddlewareQueryException {
		return this.getProjectDao().getByName(projectName);
	}

	@Override
	public Integer addWorkbenchDataset(WorkbenchDataset dataset) throws MiddlewareQueryException {
		Session session = this.getCurrentSession();
		Transaction trans = null;

		Integer workbenchDatasetSaved = null;
		try {
			// begin save transaction
			trans = session.beginTransaction();
			WorkbenchDataset datasetSaved = this.getWorkbenchDatasetDao().saveOrUpdate(dataset);
			workbenchDatasetSaved = datasetSaved.getDatasetId().intValue();
			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Error encountered while saving workbench dataset: WorkbenchDataManager.addWorkbenchDataset(dataset="
					+ dataset + "): " + e.getMessage(), e);
		}

		return workbenchDatasetSaved;
	}

	@Override
	public WorkbenchDataset getWorkbenchDatasetById(Long datasetId) throws MiddlewareQueryException {
		return this.getWorkbenchDatasetDao().getById(datasetId);
	}

	@Override
	public void deleteWorkbenchDataset(WorkbenchDataset dataset) throws MiddlewareQueryException {
		Transaction trans = null;
		Session session = this.getCurrentSession();

		try {
			trans = session.beginTransaction();
			this.getWorkbenchDatasetDao().makeTransient(dataset);
			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Cannot delete WorkbenchDataset: WorkbenchDataManager.deleteWorkbenchDataset(dataset=" + dataset
					+ "):  " + e.getMessage(), e);
		}
	}

	@Override
	public List<User> getAllUsers() throws MiddlewareQueryException {
		return this.getUserDao().getAll();
	}

	@Override
	public List<User> getAllUsersSorted() throws MiddlewareQueryException {
		return this.getUserDao().getAllUsersSorted();
	}

	@Override
	public long countAllUsers() throws MiddlewareQueryException {
		return this.getUserDao().countAll();
	}

	@Override
	public User getUserById(int id) throws MiddlewareQueryException {
		return this.getUserDao().getById(id, false);
	}

	@Override
	public List<User> getUserByName(String name, int start, int numOfRows, Operation op) throws MiddlewareQueryException {
		UserDAO dao = this.getUserDao();
		List<User> users = new ArrayList<User>();
		if (op == Operation.EQUAL) {
			users = dao.getByNameUsingEqual(name, start, numOfRows);
		} else if (op == Operation.LIKE) {
			users = dao.getByNameUsingLike(name, start, numOfRows);
		}
		return users;
	}

	@Override
	public void deleteUser(User user) throws MiddlewareQueryException {
		Transaction trans = null;
		Session session = this.getCurrentSession();

		try {
			trans = session.beginTransaction();
			this.getUserDao().makeTransient(user);
			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException(
					"Error encountered while deleting User: WorkbenchDataManager.deleteUser(user=" + user + "):  " + e.getMessage(), e);
		}
	}

	@Override
	public List<Person> getAllPersons() throws MiddlewareQueryException {
		return this.getPersonDao().getAll();
	}

	@Override
	public long countAllPersons() throws MiddlewareQueryException {
		return this.getPersonDao().countAll();
	}

	@Override
	public Person getPersonById(int id) throws MiddlewareQueryException {
		return this.getPersonDao().getById(id, false);
	}

	@Override
	public void deletePerson(Person person) throws MiddlewareQueryException {
		Transaction trans = null;
		Session session = this.getCurrentSession();

		try {
			trans = session.beginTransaction();
			this.getPersonDao().makeTransient(person);
			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Error encountered while deleting Person: WorkbenchDataManager.deletePerson(person=" + person + "): "
					+ e.getMessage(), e);
		}
	}

	@Override
	public Project getLastOpenedProject(Integer userId) throws MiddlewareQueryException {
		return this.getProjectDao().getLastOpenedProject(userId);
	}

	@Override
	public List<WorkbenchDataset> getWorkbenchDatasetByProjectId(Long projectId, int start, int numOfRows) throws MiddlewareQueryException {
		return this.getWorkbenchDatasetDao().getByProjectId(projectId, start, numOfRows);
	}

	@Override
	public long countWorkbenchDatasetByProjectId(Long projectId) throws MiddlewareQueryException {
		return this.getWorkbenchDatasetDao().countByProjectId(projectId);
	}

	@Override
	public List<WorkbenchDataset> getWorkbenchDatasetByName(String name, Operation op, int start, int numOfRows)
			throws MiddlewareQueryException {
		return this.getWorkbenchDatasetDao().getByName(name, op, start, numOfRows);
	}

	@Override
	public long countWorkbenchDatasetByName(String name, Operation op) throws MiddlewareQueryException {
		return this.getWorkbenchDatasetDao().countByName(name, op);
	}

	@Override
	public Integer addProjectUserRole(Project project, User user, Role role) throws MiddlewareQueryException {
		ProjectUserRole projectUserRole = new ProjectUserRole();
		projectUserRole.setProject(project);
		projectUserRole.setUserId(user.getUserid());
		projectUserRole.setRole(role);
		return this.addProjectUserRole(projectUserRole);
	}

	@Override
	public Integer addProjectUserRole(ProjectUserRole projectUserRole) throws MiddlewareQueryException {
		Session session = this.getCurrentSession();
		Transaction trans = null;

		Integer idSaved = null;
		try {
			// begin save transaction
			trans = session.beginTransaction();
			ProjectUserRole recordSaved = this.getProjectUserRoleDao().saveOrUpdate(projectUserRole);
			idSaved = recordSaved.getProjectUserId();
			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException(
					"Error encountered while saving ProjectUserRole: WorkbenchDataManager.addProjectUserRole(projectUserRole="
							+ projectUserRole + "): " + e.getMessage(), e);
		}

		return idSaved;
	}

	@Override
	public List<Integer> addProjectUserRole(List<ProjectUserRole> projectUserRoles) throws MiddlewareQueryException {
		Session session = this.getCurrentSession();
		if (session == null) {
			return new ArrayList<Integer>();
		}
		Transaction trans = null;

		List<Integer> idsSaved = new ArrayList<Integer>();
		try {
			// begin save transaction
			trans = session.beginTransaction();
			ProjectUserRoleDAO dao = this.getProjectUserRoleDao();

			for (ProjectUserRole projectUser : projectUserRoles) {
				ProjectUserRole recordSaved = dao.saveOrUpdate(projectUser);
				idsSaved.add(recordSaved.getProjectUserId());
			}

			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException(
					"Error encountered while saving ProjectUserRoles: WorkbenchDataManager.addProjectUserRoles(projectUserRoles="
							+ projectUserRoles + "): " + e.getMessage(), e);
		}

		return idsSaved;
	}

	@Override
	public List<ProjectUserRole> getProjectUserRolesByProject(Project project) throws MiddlewareQueryException {
		return this.getProjectUserRoleDao().getByProject(project);
	}

	@Override
	public void deleteProjectUserRole(ProjectUserRole projectUserRole) throws MiddlewareQueryException {
		Session session = this.getCurrentSession();
		if (session == null) {
			return;
		}

		Transaction trans = null;

		try {
			trans = session.beginTransaction();
			this.getProjectUserRoleDao().makeTransient(projectUserRole);
			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Error encountered while deleting ProjectUser: WorkbenchDataManager.deleteProjectUser(projectUser="
					+ projectUserRole + "): " + e.getMessage(), e);
		}
	}

	@Override
	public List<User> getUsersByProjectId(Long projectId) throws MiddlewareQueryException {
		return this.getProjectUserRoleDao().getUsersByProjectId(projectId);
	}

	@Override
	public long countUsersByProjectId(Long projectId) throws MiddlewareQueryException {
		return this.getProjectUserRoleDao().countUsersByProjectId(projectId);
	}

	@Override
	public List<CropType> getInstalledCentralCrops() throws MiddlewareQueryException {
		return this.getCropTypeDao().getAll();

	}

	@Override
	public CropType getCropTypeByName(String cropName) throws MiddlewareQueryException {
		return this.getCropTypeDao().getByName(cropName);
	}

	@Override
	public String addCropType(CropType cropType) throws MiddlewareQueryException {
		Session session = this.getCurrentSession();
		if (session == null) {
			return null;
		}

		CropTypeDAO dao = this.getCropTypeDao();
		if (this.getCropTypeDao().getByName(cropType.getCropName()) != null) {
			this.logAndThrowException("Crop type already exists.");
		}

		Transaction trans = null;
		String idSaved = null;
		try {
			// begin save transaction
			trans = session.beginTransaction();
			CropType recordSaved = dao.saveOrUpdate(cropType);
			idSaved = recordSaved.getCropName();
			// end transaction, commit to database
			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Error encountered while adding crop type: WorkbenchDataManager.addCropType(cropType=" + cropType
					+ "): " + e.getMessage(), e);
		}

		return idSaved;
	}

	public List<ProjectUserInfo> getByProjectId(Integer projectId) throws MiddlewareQueryException {
		return this.getProjectUserInfoDao().getByProjectId(projectId);

	}

	@Override
	public Integer addProjectActivity(ProjectActivity projectActivity) throws MiddlewareQueryException {
		List<ProjectActivity> list = new ArrayList<ProjectActivity>();
		list.add(projectActivity);

		List<Integer> ids = this.addProjectActivity(list);

		return ids.size() > 0 ? ids.get(0) : null;
	}

	@Override
	public List<Integer> addProjectActivity(List<ProjectActivity> projectActivityList) throws MiddlewareQueryException {

		return this.addOrUpdateProjectActivityData(projectActivityList, Operation.ADD);
	}

	private List<Integer> addOrUpdateProjectActivityData(List<ProjectActivity> projectActivityList, Operation operation)
			throws MiddlewareQueryException {

		Session session = this.getCurrentSession();
		if (session == null) {
			return new ArrayList<Integer>();
		}

		Transaction trans = null;

		List<Integer> idsSaved = new ArrayList<Integer>();
		try {

			trans = session.beginTransaction();
			ProjectActivityDAO dao = this.getProjectActivityDao();

			for (ProjectActivity projectActivityListData : projectActivityList) {
				ProjectActivity recordSaved = dao.save(projectActivityListData);
				idsSaved.add(recordSaved.getProjectActivityId());
			}
			trans.commit();

		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException(
					"Error encountered while adding addProjectActivity: WorkbenchDataManager.addOrUpdateProjectActivityData(projectActivityList="
							+ projectActivityList + ", operation=" + operation + "): " + e.getMessage(), e);
		}

		return idsSaved;
	}

	@Override
	public List<ProjectActivity> getProjectActivitiesByProjectId(Long projectId, int start, int numOfRows) throws MiddlewareQueryException {
		return this.getProjectActivityDao().getByProjectId(projectId, start, numOfRows);
	}

	@Override
	public void deleteProjectActivity(ProjectActivity projectActivity) throws MiddlewareQueryException {
		Session session = this.getCurrentSession();
		Transaction trans = null;

		try {
			trans = session.beginTransaction();
			this.getProjectActivityDao().makeTransient(projectActivity);
			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException(
					"Error encountered while deleting ProjectActivity: WorkbenchDataManager.deleteProjectActivity(projectActivity="
							+ projectActivity + "): " + e.getMessage(), e);
		}
	}

	@Override
	public long countProjectActivitiesByProjectId(Long projectId) throws MiddlewareQueryException {
		return this.getProjectActivityDao().countByProjectId(projectId);
	}

	@Override
	public Integer addToolConfiguration(ToolConfiguration toolConfig) throws MiddlewareQueryException {
		return this.addOrUpdateToolConfiguration(toolConfig, Operation.ADD);
	}

	@Override
	public Integer updateToolConfiguration(ToolConfiguration toolConfig) throws MiddlewareQueryException {
		return this.addOrUpdateToolConfiguration(toolConfig, Operation.UPDATE);
	}

	private Integer addOrUpdateToolConfiguration(ToolConfiguration toolConfig, Operation op) throws MiddlewareQueryException {
		Session session = this.getCurrentSession();
		Transaction trans = null;

		Integer idSaved = null;
		try {
			trans = session.beginTransaction();
			ToolConfiguration recordSaved = this.getToolConfigurationDao().saveOrUpdate(toolConfig);
			idSaved = recordSaved.getConfigId();

			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException(
					"Error encountered while saving ToolConfiguration: WorkbenchDataManager.addOrUpdateToolConfiguration(toolConfig="
							+ toolConfig + ", operation=" + op + "): " + e.getMessage(), e);
		}
		return idSaved;
	}

	@Override
	public void deleteToolConfiguration(ToolConfiguration toolConfig) throws MiddlewareQueryException {
		Session session = this.getCurrentSession();
		Transaction trans = null;

		try {
			trans = session.beginTransaction();
			this.getToolConfigurationDao().makeTransient(toolConfig);
			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException(
					"Error encountered while deleting ToolConfiguration: WorkbenchDataManager.deleteToolConfiguration(toolConfig="
							+ toolConfig + "): " + e.getMessage(), e);
		}
	}

	@Override
	public List<ToolConfiguration> getListOfToolConfigurationsByToolId(Long toolId) throws MiddlewareQueryException {
		return this.getToolConfigurationDao().getListOfToolConfigurationsByToolId(toolId);
	}

	@Override
	public ToolConfiguration getToolConfigurationByToolIdAndConfigKey(Long toolId, String configKey) throws MiddlewareQueryException {
		return this.getToolConfigurationDao().getToolConfigurationByToolIdAndConfigKey(toolId, configKey);
	}

	@Override
	public Integer addIbdbUserMap(IbdbUserMap userMap) throws MiddlewareQueryException {
		Session session = this.getCurrentSession();
		Transaction trans = null;

		try {
			trans = session.beginTransaction();
			this.getIbdbUserMapDao().saveOrUpdate(userMap);
			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Error encountered while adding IbdbUserMap: WorkbenchDataManager.addIbdbUserMap(userMap=" + userMap
					+ "): " + e.getMessage(), e);
		}

		return userMap.getIbdbUserId();
	}

	@Override
	public Integer getCurrentIbdbUserId(Long projectId, Integer workbenchUserId) throws MiddlewareQueryException {
		Integer ibdbUserId = null;
		IbdbUserMap userMapEntry = this.getIbdbUserMap(workbenchUserId, projectId);
		if (userMapEntry != null) {
			ibdbUserId = userMapEntry.getIbdbUserId();
		}
		return ibdbUserId;
	}

	@Override
	public IbdbUserMap getIbdbUserMap(Integer workbenchUserId, Long projectId) throws MiddlewareQueryException {
		Session session = this.getCurrentSession();
		Transaction trans = null;

		IbdbUserMap bbdbUserMap = null;
		try {
			trans = session.beginTransaction();
			bbdbUserMap = this.getIbdbUserMapDao().getIbdbUserMapByUserAndProjectID(workbenchUserId, projectId);
			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException(
					"Error encountered while retrieving Local IbdbUserMap: WorkbenchDataManager.getIbdbUserMap(workbenchUserId="
							+ workbenchUserId + ", projectId=" + projectId + "): " + e.getMessage(), e);
		}

		return bbdbUserMap;
	}

	@Override
	public Integer getLocalIbdbUserId(Integer workbenchUserId, Long projectId) throws MiddlewareQueryException {
		Session session = this.getCurrentSession();
		Transaction trans = null;

		Integer ibdbUserId = null;
		try {
			trans = session.beginTransaction();
			ibdbUserId = this.getIbdbUserMapDao().getLocalIbdbUserId(workbenchUserId, projectId);
			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException(
					"Error encountered while retrieving Local IBDB user id: WorkbenchDataManager.getLocalIbdbUserId(workbenchUserId="
							+ workbenchUserId + ", projectId=" + projectId + "): " + e.getMessage(), e);
		}

		return ibdbUserId;
	}

	@Override
	public Integer getWorkbenchUserId(Integer ibdbUserId, Long projectId) throws MiddlewareQueryException {
		Session session = this.getCurrentSession();
		Transaction trans = null;

		Integer workbenchUserId = null;
		try {
			trans = session.beginTransaction();
			workbenchUserId = this.getIbdbUserMapDao().getWorkbenchUserId(ibdbUserId, projectId);
			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException(
					"Error encountered while retrieving Local IBDB user id: WorkbenchDataManager.getLocalIbdbUserId(workbenchUserId="
							+ workbenchUserId + ", projectId=" + projectId + "): " + e.getMessage(), e);
		}

		return workbenchUserId;
	}

	@Override
	public Integer updateWorkbenchRuntimeData(WorkbenchRuntimeData workbenchRuntimeData) throws MiddlewareQueryException {
		Session session = this.getCurrentSession();
		Transaction trans = null;

		try {
			trans = session.beginTransaction();
			this.getWorkbenchRuntimeDataDao().saveOrUpdate(workbenchRuntimeData);
			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException(
					"Error encountered while adding IbdbUserMap: WorkbenchDataManager.updateWorkbenchRuntimeData(workbenchRuntimeData="
							+ workbenchRuntimeData + "): " + e.getMessage(), e);
		}

		return workbenchRuntimeData.getId();
	}

	@Override
	public WorkbenchRuntimeData getWorkbenchRuntimeData() throws MiddlewareQueryException {
		List<WorkbenchRuntimeData> list = this.getWorkbenchRuntimeDataDao().getAll(0, 1);
		return list.size() > 0 ? list.get(0) : null;
	}

	@Override
	public Role getRoleById(Integer id) throws MiddlewareQueryException {
		return this.getRoleDao().getById(id);
	}

	@Override
	public Role getRoleByNameAndWorkflowTemplate(String name, WorkflowTemplate workflowTemplate) throws MiddlewareQueryException {
		return this.getRoleDao().getByNameAndWorkflowTemplate(name, workflowTemplate);
	}

	@Override
	public List<Role> getRolesByWorkflowTemplate(WorkflowTemplate workflowTemplate) throws MiddlewareQueryException {
		return this.getRoleDao().getByWorkflowTemplate(workflowTemplate);
	}

	@Override
	public WorkflowTemplate getWorkflowTemplateByRole(Role role) throws MiddlewareQueryException {
		return role.getWorkflowTemplate();
	}

	@Override
	public List<Role> getRolesByProjectAndUser(Project project, User user) throws MiddlewareQueryException {
		return this.getProjectUserRoleDao().getRolesByProjectAndUser(project, user);
	}

	@Override
	public List<Role> getAllRoles() throws MiddlewareQueryException {
		return this.getRoleDao().getAll();
	}

	@Override
	public List<Role> getAllRolesDesc() throws MiddlewareQueryException {
		return this.getRoleDao().getAllRolesDesc();
	}

	@Override
	public List<Role> getAllRolesOrderedByLabel() throws MiddlewareQueryException {
		try {
			return this.getRoleDao().getAllRolesOrderedByLabel();
		} catch (Exception e) {
			this.logAndThrowException("Error encountered while getting all roles (sorted): " + e.getMessage(), e);
		}
		return null;
	}

	@Override
	public WorkbenchSetting getWorkbenchSetting() throws MiddlewareQueryException {
		try {
			List<WorkbenchSetting> list = this.getWorkbenchSettingDao().getAll();
			WorkbenchSetting setting = list.isEmpty() ? null : list.get(0);

			if (setting != null && this.installationDirectory != null) {
				setting.setInstallationDirectory(this.installationDirectory);
			}

			return setting;
		} catch (Exception e) {
			this.logAndThrowException("Error encountered while getting workbench setting: " + e.getMessage(), e);
		}
		return null;
	}

	@Override
	public void addSecurityQuestion(SecurityQuestion securityQuestion) throws MiddlewareQueryException {
		Session session = this.getCurrentSession();
		Transaction trans = null;

		try {
			trans = session.beginTransaction();
			this.getSecurityQuestionDao().saveOrUpdate(securityQuestion);
			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Error encountered while adding Security Question: "
					+ "WorkbenchDataManager.addSecurityQuestion(securityQuestion=" + securityQuestion + "): " + e.getMessage(), e);
		}
	}

	@Override
	public List<SecurityQuestion> getQuestionsByUserId(Integer userId) throws MiddlewareQueryException {
		try {
			return this.getSecurityQuestionDao().getByUserId(userId);
		} catch (Exception e) {
			this.logAndThrowException("Error encountered while getting Security Questions: "
					+ "WorkbenchDataManager.getQuestionsByUserId(userId=" + userId + "): " + e.getMessage(), e);
		}
		return new ArrayList<SecurityQuestion>();
	}

	@Override
	public ProjectUserMysqlAccount getProjectUserMysqlAccountByProjectIdAndUserId(Integer projectId, Integer userId)
			throws MiddlewareQueryException {
		return this.getProjectUserMysqlAccountDao().getByProjectIdAndUserId(projectId, userId);
	}

	@Override
	public Integer addProjectUserMysqlAccount(ProjectUserMysqlAccount record) throws MiddlewareQueryException {
		List<ProjectUserMysqlAccount> tosave = new ArrayList<ProjectUserMysqlAccount>();
		tosave.add(record);
		List<Integer> idsOfRecordsSaved = this.addProjectUserMysqlAccount(tosave);
		if (!idsOfRecordsSaved.isEmpty()) {
			return idsOfRecordsSaved.get(0);
		} else {
			return null;
		}
	}

	@Override
	public List<Integer> addProjectUserMysqlAccounts(List<ProjectUserMysqlAccount> records) throws MiddlewareQueryException {
		return this.addProjectUserMysqlAccount(records);
	}

	private List<Integer> addProjectUserMysqlAccount(List<ProjectUserMysqlAccount> records) throws MiddlewareQueryException {
		Session session = this.getCurrentSession();
		if (session == null) {
			return new ArrayList<Integer>();
		}

		Transaction trans = null;

		List<Integer> idsSaved = new ArrayList<Integer>();
		try {
			// begin save transaction
			trans = session.beginTransaction();

			ProjectUserMysqlAccountDAO dao = this.getProjectUserMysqlAccountDao();

			for (ProjectUserMysqlAccount record : records) {
				ProjectUserMysqlAccount recordSaved = dao.saveOrUpdate(record);
				idsSaved.add(recordSaved.getId());
			}

			// end transaction, commit to database
			trans.commit();

			// remove ProjectUserMysqlAccount data from session cache
			for (ProjectUserMysqlAccount record : records) {
				session.evict(record);
			}
			session.evict(records);

		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException(
					"Error encountered while adding ProjectUserMysqlAccount: WorkbenchDataManager.addProjectUserMysqlAccount(records="
							+ records + "): " + e.getMessage(), e);
		}

		return idsSaved;
	}

	@Override
	public List<ProjectBackup> getProjectBackups() throws MiddlewareQueryException {
		return this.getProjectBackupDao().getAllProjectBackups();
	}

	@Override
	public List<ProjectBackup> getProjectBackups(Project project) throws MiddlewareQueryException {
		if (project == null || project.getProjectId() == null) {
			return null;
		}

		return this.getProjectBackupDao().getProjectBackups(project.getProjectId());
	}

	@Override
	public ProjectBackup saveOrUpdateProjectBackup(ProjectBackup projectBackup) throws MiddlewareQueryException {

		Transaction trans = null;
		Session session = this.getCurrentSession();

		try {
			trans = session.beginTransaction();

			if (projectBackup.getBackupPath() != null) {

				List<ProjectBackup> result = this.getProjectBackupDao().getProjectBackupByBackupPath(projectBackup.getBackupPath());

				if (result != null && result.size() > 0) {
					result.get(0).setBackupTime(projectBackup.getBackupTime());
					projectBackup = result.get(0);
				}
			}

			projectBackup = this.getProjectBackupDao().saveOrUpdate(projectBackup);

			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Cannot save ProjectBackup: WorkbenchDataManager.saveOrUpdateProjectBackup(projectBackup="
					+ projectBackup + "): " + e.getMessage(), e);
		}

		return projectBackup;
	}

	@Override
	public void deleteProjectBackup(ProjectBackup projectBackup) throws MiddlewareQueryException {
		Transaction trans = null;
		Session session = this.getCurrentSession();

		try {
			trans = session.beginTransaction();
			this.getProjectBackupDao().makeTransient(projectBackup);
			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Cannot delete Project: WorkbenchDataManager.deleteProjectBackup(projectBackup=" + projectBackup
					+ "): " + e.getMessage(), e);
		}
	}

	@Override
	public UserInfo getUserInfo(int userId) throws MiddlewareQueryException {
		try {
			return this.getUserInfoDao().getUserInfoByUserId(userId);
		} catch (Exception e) {
			this.logAndThrowException("Cannot increment login count for user_id =" + userId + "): " + e.getMessage(), e);
		}

		return new UserInfo();
	}

	@Override
	public UserInfo getUserInfoByUsername(String username) throws MiddlewareQueryException {
		User user = this.getUserByName(username, 0, 1, Operation.EQUAL).get(0);

		return this.getUserInfo(user.getUserid());
	}

	@Override
	public UserInfo getUserInfoByResetToken(String token) throws MiddlewareQueryException {
		return this.getUserInfoDao().getUserInfoByToken(token);
	}

	@Override
	public UserInfo updateUserInfo(UserInfo userInfo) throws MiddlewareQueryException {
		Transaction trans = null;
		Session session = this.getCurrentSession();

		try {
			trans = session.beginTransaction();

			this.getUserInfoDao().update(userInfo);

			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);

			this.logAndThrowException("Cannot update userInfo =" + userInfo.getUserId() + "): " + e.getMessage(), e);

		} finally {
			session.flush();
		}

		return userInfo;
	}

	@Override
	public void incrementUserLogInCount(int userId) throws MiddlewareQueryException {
		Transaction trans = null;
		Session session = this.getCurrentSession();

		try {
			trans = session.beginTransaction();

			UserInfo userdetails = this.getUserInfoDao().getUserInfoByUserId(userId);
			if (userdetails != null) {
				this.getUserInfoDao().updateLoginCounter(userdetails);
			}

			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Cannot increment login count for user_id =" + userId + "): " + e.getMessage(), e);
		} finally {
			session.flush();
		}
	}

	@Override
	public void insertOrUpdateUserInfo(UserInfo userDetails) throws MiddlewareQueryException {
		this.getUserInfoDao().insertOrUpdateUserInfo(userDetails);
	}

	@Override
	public boolean changeUserPassword(String username, String password) throws MiddlewareQueryException {
		return this.getUserDao().changePassword(username, password);
	}

	@Override
	public List<WorkbenchSidebarCategory> getAllWorkbenchSidebarCategory() throws MiddlewareQueryException {
		return this.getWorkbenchSidebarCategoryDao().getAll();
	}

	@Override
	public List<WorkbenchSidebarCategoryLink> getAllWorkbenchSidebarLinks() throws MiddlewareQueryException {
		return this.getWorkbenchSidebarCategoryLinkDao().getAll();
	}

	@Override
	public List<WorkbenchSidebarCategoryLink> getAllWorkbenchSidebarLinksByCategoryId(WorkbenchSidebarCategory category)
			throws MiddlewareQueryException {
		return this.getWorkbenchSidebarCategoryLinkDao().getAllWorkbenchSidebarLinksByCategoryId(category, 0, Integer.MAX_VALUE);
	}

	public String getInstallationDirectory() {
		return this.installationDirectory;
	}

	public void setInstallationDirectory(String installationDirectory) {
		this.installationDirectory = installationDirectory;
	}

	@Override
	public List<TemplateSetting> getTemplateSettings(TemplateSetting templateSettingFilter) throws MiddlewareQueryException {
		return this.getTemplateSettingDao().get(templateSettingFilter);
	}

	@Override
	public Integer addTemplateSetting(TemplateSetting templateSetting) throws MiddlewareQueryException {
		Session session = this.getCurrentSession();
		Transaction trans = null;

		try {

			// Save if non-existing
			if (this.getTemplateSettings(templateSetting).isEmpty()) {
				trans = session.beginTransaction();
				this.updateIsDefaultOfSameProjectAndToolTemplateSetting(templateSetting);
				this.getTemplateSettingDao().save(templateSetting);
				trans.commit();
			} else {
				throw new MiddlewareQueryException("Template setting already exists.");
			}

		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Error encountered while adding Template Setting: "
					+ "WorkbenchDataManager.addTemplateSetting(templateSetting=" + templateSetting + "): " + e.getMessage(), e);
		}
		return templateSetting.getTemplateSettingId();
	}

	@Override
	public void updateTemplateSetting(TemplateSetting templateSetting) throws MiddlewareQueryException {
		Transaction trans = null;
		Session session = this.getCurrentSession();

		try {
			trans = session.beginTransaction();
			this.updateIsDefaultOfSameProjectAndToolTemplateSetting(templateSetting);
			this.getTemplateSettingDao().merge(templateSetting);
			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Cannot update TemplateSeting: WorkbenchDataManager.updateTemplateSetting(templateSetting="
					+ templateSetting + "): " + e.getMessage(), e);
		}
	}

	/**
	 * If the new template setting's isDefault == true, set all others with the same project id and tool to isDefault = false
	 */
	private void updateIsDefaultOfSameProjectAndToolTemplateSetting(TemplateSetting templateSetting) throws MiddlewareQueryException {
		if (templateSetting.isDefault()) {
			TemplateSetting templateSettingFilter =
					new TemplateSetting(null, templateSetting.getProjectId(), null, templateSetting.getTool(), null, null);

			List<TemplateSetting> sameProjectAndToolSettings = this.getTemplateSettings(templateSettingFilter);

			if (sameProjectAndToolSettings.size() > 0) {
				for (TemplateSetting setting : sameProjectAndToolSettings) {
					if (!setting.getTemplateSettingId().equals(templateSetting.getTemplateSettingId()) && setting.isDefault()) {
						setting.setIsDefault(Boolean.FALSE);
						this.getTemplateSettingDao().merge(setting);
					}
				}
			}
		}
	}

	@Override
	public void deleteTemplateSetting(TemplateSetting templateSetting) throws MiddlewareQueryException {
		Transaction trans = null;
		Session session = this.getCurrentSession();

		try {
			trans = session.beginTransaction();
			this.getTemplateSettingDao().makeTransient(templateSetting);
			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Cannot delete TemplateSetting: WorkbenchDataManager.deleteTemplateSetting(templateSetting="
					+ templateSetting + "): " + e.getMessage(), e);
		}

	}

	@Override
	public void deleteTemplateSetting(Integer id) throws MiddlewareQueryException {
		Transaction trans = null;
		Session session = this.getCurrentSession();

		try {
			trans = session.beginTransaction();
			TemplateSetting templateSettingsFilter = new TemplateSetting(id, null, null, null, null, null);
			templateSettingsFilter.setIsDefaultToNull();
			List<TemplateSetting> settings = this.getTemplateSettings(templateSettingsFilter);

			if (settings.size() == 1) {
				this.getTemplateSettingDao().makeTransient(settings.get(0));
			} else {
				this.logAndThrowException("Cannot delete TemplateSetting: WorkbenchDataManager.deleteTemplateSetting(id=" + id + ")");
			}

			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException(
					"Cannot delete TemplateSetting: WorkbenchDataManager.deleteTemplateSetting(id=" + id + "): " + e.getMessage(), e);
		}

	}

	@Override
	public Project getLastOpenedProjectAnyUser() throws MiddlewareQueryException {
		return this.getProjectDao().getLastOpenedProjectAnyUser();
	}

	@Override
	public Boolean isLastOpenedProjectChanged() throws MiddlewareQueryException {

		Project project = this.getProjectDao().getLastOpenedProjectAnyUser();

		if (this.currentlastOpenedProject == null) {
			this.currentlastOpenedProject = project;
			return false;
		}

		if (this.currentlastOpenedProject.getProjectId().equals(project.getProjectId())) {
			return false;
		} else {
			this.currentlastOpenedProject = project;
			return true;
		}

	}

	@Override
	public List<StandardPreset> getStandardPresetFromCropAndTool(String cropName, int toolId) throws MiddlewareQueryException {

		try {
			Criteria criteria = this.getCurrentSession().createCriteria(StandardPreset.class);

			criteria.add(Restrictions.eq("cropName", cropName));
			criteria.add(Restrictions.eq("toolId", toolId));

			return criteria.list();
		} catch (HibernateException e) {
			this.logAndThrowException(
					"error in: WorkbenchDataManager.getAllProgramPresetFromProgram(cropName=" + cropName + "): " + e.getMessage(), e);
		}

		return new ArrayList<StandardPreset>();
	}

	@Override
	public List<StandardPreset> getStandardPresetFromCropAndTool(String cropName, int toolId, String toolSection)
			throws MiddlewareQueryException {

		try {
			Criteria criteria = this.getCurrentSession().createCriteria(StandardPreset.class);

			criteria.add(Restrictions.eq("cropName", cropName));
			criteria.add(Restrictions.eq("toolId", toolId));
			criteria.add(Restrictions.eq("toolSection", toolSection));

			return criteria.list();
		} catch (HibernateException e) {
			this.logAndThrowException(
					"error in: WorkbenchDataManager.getAllProgramPresetFromProgram(cropName=" + cropName + "): " + e.getMessage(), e);
		}

		return new ArrayList<StandardPreset>();
	}

	@Override
	public List<StandardPreset> getStandardPresetFromCropAndToolByName(String presetName, String cropName, int toolId, String toolSection)
			throws MiddlewareQueryException {

		try {
			Criteria criteria = this.getCurrentSession().createCriteria(StandardPreset.class);

			criteria.add(Restrictions.eq("cropName", cropName));
			criteria.add(Restrictions.eq("toolId", toolId));
			criteria.add(Restrictions.eq("toolSection", toolSection));
			criteria.add(Restrictions.like("name", presetName));

			return criteria.list();
		} catch (HibernateException e) {
			this.logAndThrowException(
					"error in: WorkbenchDataManager.getAllProgramPresetFromProgram(cropName=" + cropName + "): " + e.getMessage(), e);
		}

		return new ArrayList<StandardPreset>();
	}

	@Override
	public StandardPreset saveOrUpdateStandardPreset(StandardPreset standardPreset) throws MiddlewareQueryException {

		Transaction transaction = this.getCurrentSession().beginTransaction();

		try {
			StandardPreset result = this.getStandardPresetDAO().saveOrUpdate(standardPreset);

			transaction.commit();

			return result;

		} catch (HibernateException e) {
			this.rollbackTransaction(transaction);
			this.logAndThrowException(
					"Cannot perform: WorkbenchDataManager.saveOrUpdateStandardPreset(standardPreset=" + standardPreset.getName() + "): "
							+ e.getMessage(), e);
		} finally {
			this.getCurrentSession().flush();
		}

		return null;
	}

	@Override
	public void deleteStandardPreset(int standardPresetId) throws MiddlewareQueryException {

		Transaction transaction = this.getCurrentSession().beginTransaction();

		try {
			StandardPreset preset = this.getStandardPresetDAO().getById(standardPresetId);
			this.getCurrentSession().delete(preset);
			transaction.commit();

		} catch (HibernateException e) {
			this.rollbackTransaction(transaction);
			this.logAndThrowException("Cannot delete preset: WorkbenchDataManager.deleteStandardPreset(standardPresetId="
					+ standardPresetId + "): " + e.getMessage(), e);
		} finally {
			this.getCurrentSession().flush();
		}
	}

	@Override
	public void close() {
		if (this.sessionProvider != null) {
			this.sessionProvider.close();
		}
	}

}
