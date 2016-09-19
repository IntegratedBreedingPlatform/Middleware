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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.generationcp.middleware.dao.CropTypeDAO;
import org.generationcp.middleware.dao.IbdbUserMapDAO;
import org.generationcp.middleware.dao.PersonDAO;
import org.generationcp.middleware.dao.ProjectActivityDAO;
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
import org.generationcp.middleware.pojos.ErrorCode;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.presets.StandardPreset;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.IbdbUserMap;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
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
import org.generationcp.middleware.pojos.workbench.UserRole;
import org.generationcp.middleware.pojos.workbench.WorkbenchDataset;
import org.generationcp.middleware.pojos.workbench.WorkbenchRuntimeData;
import org.generationcp.middleware.pojos.workbench.WorkbenchSetting;
import org.generationcp.middleware.pojos.workbench.WorkbenchSidebarCategory;
import org.generationcp.middleware.pojos.workbench.WorkbenchSidebarCategoryLink;
import org.generationcp.middleware.pojos.workbench.WorkflowTemplate;
import org.generationcp.middleware.service.api.user.UserDto;
import org.generationcp.middleware.util.Util;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the WorkbenchDataManager interface. To instantiate this class, a Hibernate Session must be passed to its constructor.
 */
@Transactional
public class WorkbenchDataManagerImpl implements WorkbenchDataManager {

	private static final Logger LOG = LoggerFactory.getLogger(WorkbenchDataManagerImpl.class);

	private HibernateSessionProvider sessionProvider;

	private Project currentlastOpenedProject;

	private String installationDirectory;

	public WorkbenchDataManagerImpl() {
		super();
	}

	public WorkbenchDataManagerImpl(HibernateSessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
	}

	public Session getCurrentSession() {
		return this.sessionProvider.getSession();
	}

	private CropTypeDAO getCropTypeDao() {
		CropTypeDAO cropTypeDao = new CropTypeDAO();
		cropTypeDao.setSession(this.getCurrentSession());
		return cropTypeDao;
	}

	private IbdbUserMapDAO getIbdbUserMapDao() {
		IbdbUserMapDAO ibdbUserMapDao = new IbdbUserMapDAO();
		ibdbUserMapDao.setSession(this.getCurrentSession());
		return ibdbUserMapDao;
	}

	private PersonDAO getPersonDao() {
		PersonDAO personDao = new PersonDAO();
		personDao.setSession(this.getCurrentSession());
		return personDao;
	}

	private ProjectActivityDAO getProjectActivityDao() {
		ProjectActivityDAO projectActivityDao = new ProjectActivityDAO();
		projectActivityDao.setSession(this.getCurrentSession());
		return projectActivityDao;
	}

	private ProjectUserMysqlAccountDAO getProjectUserMysqlAccountDAO() {

		ProjectUserMysqlAccountDAO projectUserMysqlAccountDAO = new ProjectUserMysqlAccountDAO();
		projectUserMysqlAccountDAO.setSession(this.getCurrentSession());
		return projectUserMysqlAccountDAO;
	}

	private ProjectDAO getProjectDao() {

		ProjectDAO projectDao = new ProjectDAO();
		projectDao.setSession(this.getCurrentSession());
		return projectDao;
	}

	private ProjectUserMysqlAccountDAO getProjectUserMysqlAccountDao() {

		ProjectUserMysqlAccountDAO projectUserMysqlAccountDao = new ProjectUserMysqlAccountDAO();
		projectUserMysqlAccountDao.setSession(this.getCurrentSession());
		return projectUserMysqlAccountDao;
	}

	@Override
	public ProjectUserInfoDAO getProjectUserInfoDao() {

		ProjectUserInfoDAO projectUserInfoDao = new ProjectUserInfoDAO();
		projectUserInfoDao.setSession(this.getCurrentSession());
		return projectUserInfoDao;
	}

	@Override
	public void updateProjectsRolesForProject(Project project, List<ProjectUserRole> newRoles) throws MiddlewareQueryException {
		List<ProjectUserRole> oldRoles = this.getProjectUserRolesByProject(project);

		List<ProjectUserRole> toDeleteRoles = this.getUniqueUserRolesFrom(oldRoles, newRoles);
		List<ProjectUserRole> toAddRoles = this.getUniqueUserRolesFrom(newRoles, oldRoles);

		this.deleteProjectUserRoles(toDeleteRoles);
		this.addProjectUserRole(toAddRoles);
	}

	private List<ProjectUserRole> getUniqueUserRolesFrom(List<ProjectUserRole> list1, List<ProjectUserRole> list2) {
		List<ProjectUserRole> uniqueRoles = new ArrayList<>();
		for (ProjectUserRole role : list1) {
			if (!this.projectRoleContains(role, list2)) {
				uniqueRoles.add(role);
			}
		}
		return uniqueRoles;
	}

	private boolean projectRoleContains(ProjectUserRole oldRole, List<ProjectUserRole> roles) {
		for (ProjectUserRole role : roles) {
			if (oldRole.getUserId().equals(role.getUserId()) && oldRole.getRole().getRoleId().equals(role.getRole().getRoleId())) {
				return true;
			}
		}

		return false;
	}

	private ProjectUserRoleDAO getProjectUserRoleDao() {
		ProjectUserRoleDAO projectUserRoleDao = new ProjectUserRoleDAO();
		projectUserRoleDao.setSession(this.getCurrentSession());
		return projectUserRoleDao;
	}

	private RoleDAO getRoleDao() {

		RoleDAO roleDao = new RoleDAO();
		roleDao.setSession(this.getCurrentSession());
		return roleDao;
	}

	private SecurityQuestionDAO getSecurityQuestionDao() {

		SecurityQuestionDAO securityQuestionDao = new SecurityQuestionDAO();
		securityQuestionDao.setSession(this.getCurrentSession());
		return securityQuestionDao;
	}

	private ToolConfigurationDAO getToolConfigurationDao() {

		ToolConfigurationDAO toolConfigurationDao = new ToolConfigurationDAO();
		toolConfigurationDao.setSession(this.getCurrentSession());
		return toolConfigurationDao;
	}

	@Override
	public ToolDAO getToolDao() {

		ToolDAO toolDao = new ToolDAO();
		toolDao.setSession(this.getCurrentSession());
		return toolDao;
	}

	private UserDAO getUserDao() {

		UserDAO userDao = new UserDAO();
		userDao.setSession(this.getCurrentSession());
		return userDao;
	}

	private UserInfoDAO getUserInfoDao() {

		UserInfoDAO userInfoDao = new UserInfoDAO();
		userInfoDao.setSession(this.getCurrentSession());
		return userInfoDao;
	}

	private WorkbenchDatasetDAO getWorkbenchDatasetDao() {

		WorkbenchDatasetDAO workbenchDatasetDao = new WorkbenchDatasetDAO();
		workbenchDatasetDao.setSession(this.getCurrentSession());
		return workbenchDatasetDao;
	}

	private WorkbenchRuntimeDataDAO getWorkbenchRuntimeDataDao() {

		WorkbenchRuntimeDataDAO workbenchRuntimeDataDao = new WorkbenchRuntimeDataDAO();
		workbenchRuntimeDataDao.setSession(this.getCurrentSession());
		return workbenchRuntimeDataDao;
	}

	private WorkbenchSettingDAO getWorkbenchSettingDao() {

		WorkbenchSettingDAO workbenchSettingDao = new WorkbenchSettingDAO();
		workbenchSettingDao.setSession(this.getCurrentSession());
		return workbenchSettingDao;
	}

	private WorkflowTemplateDAO getWorkflowTemplateDao() {

		WorkflowTemplateDAO workflowTemplateDao = new WorkflowTemplateDAO();
		workflowTemplateDao.setSession(this.getCurrentSession());
		return workflowTemplateDao;
	}

	private WorkbenchSidebarCategoryDAO getWorkbenchSidebarCategoryDao() {

		WorkbenchSidebarCategoryDAO workbenchSidebarCategoryDAO = new WorkbenchSidebarCategoryDAO();
		workbenchSidebarCategoryDAO.setSession(this.getCurrentSession());
		return workbenchSidebarCategoryDAO;
	}

	private WorkbenchSidebarCategoryLinkDAO getWorkbenchSidebarCategoryLinkDao() {

		WorkbenchSidebarCategoryLinkDAO workbenchSidebarCategoryLinkDAO = new WorkbenchSidebarCategoryLinkDAO();

		workbenchSidebarCategoryLinkDAO.setSession(this.getCurrentSession());
		return workbenchSidebarCategoryLinkDAO;
	}

	private TemplateSettingDAO getTemplateSettingDao() {

		TemplateSettingDAO templateSettingDAO = new TemplateSettingDAO();
		templateSettingDAO.setSession(this.getCurrentSession());
		return templateSettingDAO;
	}

	@Override
	public StandardPresetDAO getStandardPresetDAO() {
		final StandardPresetDAO standardPresetDAO = new StandardPresetDAO();
		standardPresetDAO.setSession(this.getCurrentSession());
		return standardPresetDAO;
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

		try {
			this.getProjectDao().merge(project);
		} catch (Exception e) {

			this.logAndThrowException(
					"Cannot save Project: WorkbenchDataManager.saveOrUpdateProject(project=" + project + "): " + e.getMessage(), e);
		}

		return project;
	}

	@Override
	public ProjectUserInfo saveOrUpdateProjectUserInfo(ProjectUserInfo projectUserInfo) throws MiddlewareQueryException {

		try {
			this.getProjectUserInfoDao().merge(projectUserInfo);
		} catch (Exception e) {

			this.logAndThrowException("Cannot save ProjectUserInfo: WorkbenchDataManager.saveOrUpdateProjectUserInfo(project="
					+ projectUserInfo + "): " + e.getMessage(), e);
		}

		return projectUserInfo;
	}

	@Override
	public Project addProject(Project project) throws MiddlewareQueryException {

		try {
			project.setUniqueID(UUID.randomUUID().toString());
			this.getProjectDao().save(project);
		} catch (Exception e) {
			this.logAndThrowException("Cannot save Project: WorkbenchDataManager.addProject(project=" + project + "): " + e.getMessage(), e);
		}
		return project;
	}

	@Override
	public Project mergeProject(Project project) throws MiddlewareQueryException {
		try {
			this.getProjectDao().merge(project);
		} catch (Exception e) {
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

	public List<IbdbUserMap> getIbdbUserMapsByProjectId(Long projectId) throws MiddlewareQueryException {
		return this.getIbdbUserMapDao().getIbdbUserMapByID(projectId);
	}

	public void deleteProjectUserInfoDao(ProjectUserInfo projectUserInfo) throws MiddlewareQueryException {

		try {

			this.getProjectUserInfoDao().makeTransient(projectUserInfo);

		} catch (Exception e) {

			this.logAndThrowException("Cannot delete ProjectUserInfo: WorkbenchDataManager.deleteProjectUserInfoDao(projectUserInfo="
					+ projectUserInfo + "): " + e.getMessage(), e);
		}
	}

	public void deleteProjectUserMysqlAccount(ProjectUserMysqlAccount mysqlaccount) throws MiddlewareQueryException {

		try {

			this.getProjectUserMysqlAccountDAO().makeTransient(mysqlaccount);

		} catch (Exception e) {

			this.logAndThrowException(
					"Cannot delete ProjectUserMysqlAccount: WorkbenchDataManager.deleteProjectUserMysqlAccount(mysqlaccount="
							+ mysqlaccount + "): " + e.getMessage(), e);
		}
	}

	@Override
	public void deleteProject(Project project) throws MiddlewareQueryException {

		try {

			this.getProjectDao().deleteProject(project.getProjectName());

		} catch (Exception e) {

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
	public boolean isPersonExists(String firstName, String lastName) throws MiddlewareQueryException {
		return this.getPersonDao().isPersonExists(firstName, lastName);
	}

	@Override
	public boolean isPersonWithEmailExists(String email) throws MiddlewareQueryException {
		return this.getPersonDao().isPersonWithEmailExists(email);
	}

	@Override
	public Person getPersonByEmail(String email) throws MiddlewareQueryException {
		return this.getPersonDao().getPersonByEmail(email);
	}

	@Override
	public Person getPersonByEmailAndName(String email, String firstName, String lastName) throws MiddlewareQueryException {
		return this.getPersonDao().getPersonByEmailAndName(email, firstName, lastName);
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

		Integer idPersonSaved = null;
		try {

			Person recordSaved = this.getPersonDao().saveOrUpdate(person);
			idPersonSaved = recordSaved.getId();

		} catch (Exception e) {

			this.logAndThrowException(
					"Error encountered while saving Person: WorkbenchDataManager.addPerson(person=" + person + "): " + e.getMessage(), e);
		}
		return idPersonSaved;
	}

	@Override
	public Integer addUser(User user) throws MiddlewareQueryException {

		Integer idUserSaved = null;
		try {

			User recordSaved = this.getUserDao().saveOrUpdate(user);
			idUserSaved = recordSaved.getUserid();

		} catch (Exception e) {

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
	public Project getProjectByNameAndCrop(final String projectName, final CropType cropType) throws MiddlewareQueryException {
		return this.getProjectDao().getProjectByNameAndCrop(projectName, cropType);
	}

	@Override
	public Project getProjectByUuid(String projectUuid) throws MiddlewareQueryException {
		return this.getProjectDao().getByUuid(projectUuid);
	}

	@Override
	public Integer addWorkbenchDataset(WorkbenchDataset dataset) throws MiddlewareQueryException {

		Integer workbenchDatasetSaved = null;
		try {

			WorkbenchDataset datasetSaved = this.getWorkbenchDatasetDao().saveOrUpdate(dataset);
			workbenchDatasetSaved = datasetSaved.getDatasetId().intValue();

		} catch (Exception e) {

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

		try {

			this.getWorkbenchDatasetDao().makeTransient(dataset);

		} catch (Exception e) {

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

		try {

			this.getUserDao().makeTransient(user);

		} catch (Exception e) {

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

		try {

			this.getPersonDao().makeTransient(person);

		} catch (Exception e) {

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

		Integer idSaved = null;
		try {

			ProjectUserRole recordSaved = this.getProjectUserRoleDao().saveOrUpdate(projectUserRole);
			idSaved = recordSaved.getProjectUserId();

		} catch (Exception e) {

			this.logAndThrowException(
					"Error encountered while saving ProjectUserRole: WorkbenchDataManager.addProjectUserRole(projectUserRole="
							+ projectUserRole + "): " + e.getMessage(), e);
		}

		return idSaved;
	}

	@Override
	public void deleteProjectUserRolesByProject(Project project) throws MiddlewareQueryException {
		// remove all previous roles
		this.deleteProjectUserRoles(this.getProjectUserRolesByProject(project));
	}

	@Override
	public void deleteProjectUserRoles(List<ProjectUserRole> oldRoles) {
		// remove all previous roles
		try {
			for (ProjectUserRole projectUserRole : oldRoles) {
				this.getCurrentSession().delete(projectUserRole);
			}
		} catch (Exception e) {
			this.logAndThrowException("Error encountered while deleting ProjectUser: WorkbenchDataManager.deleteProjectUserRoles(oldRoles="
					+ oldRoles + "): " + e.getMessage(), e);
		}
	}

	@Override
	public List<Integer> addProjectUserRole(List<ProjectUserRole> projectUserRoles) throws MiddlewareQueryException {

		List<Integer> idsSaved = new ArrayList<Integer>();
		try {

			ProjectUserRoleDAO dao = this.getProjectUserRoleDao();

			for (ProjectUserRole projectUser : projectUserRoles) {
				ProjectUserRole recordSaved = dao.saveOrUpdate(projectUser);
				idsSaved.add(recordSaved.getProjectUserId());
			}

		} catch (Exception e) {

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

		try {
			this.getProjectUserRoleDao().makeTransient(projectUserRole);
		} catch (Exception e) {
			this.logAndThrowException("Error encountered while deleting ProjectUser: WorkbenchDataManager.deleteProjectUser(projectUser="
					+ projectUserRole + "): " + e.getMessage(), e);
		}
	}

	@Override
	public List<User> getUsersByProjectId(final Long projectId) {
		return this.getProjectUserRoleDao().getUsersByProjectId(projectId);
	}

	@Override
	public Map<Integer, Person> getPersonsByProjectId(final Long projectId) {
		return this.getProjectUserRoleDao().getPersonsByProjectId(projectId);
	}

	@Override
	public long countUsersByProjectId(Long projectId) throws MiddlewareQueryException {
		return this.getProjectUserRoleDao().countUsersByProjectId(projectId);
	}

	@Override
	public List<CropType> getInstalledCropDatabses() {
		return this.getCropTypeDao().getAll();
	}

	@Override
	public CropType getCropTypeByName(String cropName) throws MiddlewareQueryException {
		return this.getCropTypeDao().getByName(cropName);
	}

	@Override
	public String addCropType(CropType cropType) throws MiddlewareQueryException {

		CropTypeDAO dao = this.getCropTypeDao();
		if (this.getCropTypeDao().getByName(cropType.getCropName()) != null) {
			this.logAndThrowException("Crop type already exists.");
		}

		String idSaved = null;
		try {

			CropType recordSaved = dao.saveOrUpdate(cropType);
			idSaved = recordSaved.getCropName();

		} catch (Exception e) {

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

		return !ids.isEmpty() ? ids.get(0) : null;
	}

	@Override
	public List<Integer> addProjectActivity(List<ProjectActivity> projectActivityList) throws MiddlewareQueryException {

		return this.addOrUpdateProjectActivityData(projectActivityList, Operation.ADD);
	}

	private List<Integer> addOrUpdateProjectActivityData(List<ProjectActivity> projectActivityList, Operation operation)
			throws MiddlewareQueryException {

		List<Integer> idsSaved = new ArrayList<Integer>();
		try {

			ProjectActivityDAO dao = this.getProjectActivityDao();

			for (ProjectActivity projectActivityListData : projectActivityList) {
				ProjectActivity recordSaved = dao.save(projectActivityListData);
				idsSaved.add(recordSaved.getProjectActivityId());
			}

		} catch (Exception e) {

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

		try {

			this.getProjectActivityDao().makeTransient(projectActivity);

		} catch (Exception e) {

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

		Integer idSaved = null;
		try {

			ToolConfiguration recordSaved = this.getToolConfigurationDao().saveOrUpdate(toolConfig);
			idSaved = recordSaved.getConfigId();

		} catch (Exception e) {

			this.logAndThrowException(
					"Error encountered while saving ToolConfiguration: WorkbenchDataManager.addOrUpdateToolConfiguration(toolConfig="
							+ toolConfig + ", operation=" + op + "): " + e.getMessage(), e);
		}
		return idSaved;
	}

	@Override
	public void deleteToolConfiguration(ToolConfiguration toolConfig) throws MiddlewareQueryException {

		try {

			this.getToolConfigurationDao().makeTransient(toolConfig);

		} catch (Exception e) {

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


		try {
			IbdbUserMap existingMapping = this.getIbdbUserMap(userMap.getWorkbenchUserId(), userMap.getProjectId());
			if (existingMapping == null) {
				this.getIbdbUserMapDao().saveOrUpdate(userMap);
				return userMap.getIbdbUserMapId().intValue();
			} else {
				return existingMapping.getIbdbUserMapId().intValue();
			}
		} catch (Exception e) {
			String message =
					"Error encountered while adding IbdbUserMap (linking workbench user id to crop database user): WorkbenchDataManager.addIbdbUserMap(userMap="
							+ userMap + "): " + e.getMessage();
			LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
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

		IbdbUserMap bbdbUserMap = null;
		try {

			bbdbUserMap = this.getIbdbUserMapDao().getIbdbUserMapByUserAndProjectID(workbenchUserId, projectId);

		} catch (Exception e) {

			this.logAndThrowException(
					"Error encountered while retrieving Local IbdbUserMap: WorkbenchDataManager.getIbdbUserMap(workbenchUserId="
							+ workbenchUserId + ", projectId=" + projectId + "): " + e.getMessage(), e);
		}

		return bbdbUserMap;
	}

	@Override
	public Integer getLocalIbdbUserId(Integer workbenchUserId, Long projectId) {

		Integer ibdbUserId = null;
		try {

			ibdbUserId = this.getIbdbUserMapDao().getLocalIbdbUserId(workbenchUserId, projectId);

		} catch (Exception e) {

			this.logAndThrowException(
					"Error encountered while retrieving Local IBDB user id: WorkbenchDataManager.getLocalIbdbUserId(workbenchUserId="
							+ workbenchUserId + ", projectId=" + projectId + "): " + e.getMessage(), e);
		}

		return ibdbUserId;
	}

	@Override
	public Integer getWorkbenchUserIdByIBDBUserIdAndProjectId(Integer ibdbUserId, Long projectId) throws MiddlewareQueryException {
		return this.getIbdbUserMapDao().getWorkbenchUserId(ibdbUserId, projectId);
	}

	@Override
	public Integer updateWorkbenchRuntimeData(WorkbenchRuntimeData workbenchRuntimeData) throws MiddlewareQueryException {

		try {

			this.getWorkbenchRuntimeDataDao().saveOrUpdate(workbenchRuntimeData);

		} catch (Exception e) {

			this.logAndThrowException(
					"Error encountered while adding IbdbUserMap: WorkbenchDataManager.updateWorkbenchRuntimeData(workbenchRuntimeData="
							+ workbenchRuntimeData + "): " + e.getMessage(), e);
		}

		return workbenchRuntimeData.getId();
	}

	@Override
	public WorkbenchRuntimeData getWorkbenchRuntimeData() throws MiddlewareQueryException {
		List<WorkbenchRuntimeData> list = this.getWorkbenchRuntimeDataDao().getAll(0, 1);
		return !list.isEmpty() ? list.get(0) : null;
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

		try {

			this.getSecurityQuestionDao().saveOrUpdate(securityQuestion);

		} catch (Exception e) {

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

		List<Integer> idsSaved = new ArrayList<Integer>();
		try {
			ProjectUserMysqlAccountDAO dao = this.getProjectUserMysqlAccountDao();

			for (ProjectUserMysqlAccount record : records) {
				ProjectUserMysqlAccount recordSaved = dao.saveOrUpdate(record);
				idsSaved.add(recordSaved.getId());
			}
		} catch (Exception e) {

			this.logAndThrowException(
					"Error encountered while adding ProjectUserMysqlAccount: WorkbenchDataManager.addProjectUserMysqlAccount(records="
							+ records + "): " + e.getMessage(), e);
		}

		return idsSaved;
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
	public User getUserByUsername(String userName) throws MiddlewareQueryException {
		return this.getUserDao().getUserByUserName(userName);
	}

	@Override
	public UserInfo getUserInfoByResetToken(String token) throws MiddlewareQueryException {
		return this.getUserInfoDao().getUserInfoByToken(token);
	}

	@Override
	public UserInfo updateUserInfo(UserInfo userInfo) throws MiddlewareQueryException {

		try {

			this.getUserInfoDao().update(userInfo);

		} catch (Exception e) {

			this.logAndThrowException("Cannot update userInfo =" + userInfo.getUserId() + "): " + e.getMessage(), e);

		}
		return userInfo;
	}

	@Override
	public void incrementUserLogInCount(int userId) throws MiddlewareQueryException {

		try {

			UserInfo userdetails = this.getUserInfoDao().getUserInfoByUserId(userId);
			if (userdetails != null) {
				this.getUserInfoDao().updateLoginCounter(userdetails);
			}

		} catch (Exception e) {

			this.logAndThrowException("Cannot increment login count for user_id =" + userId + "): " + e.getMessage(), e);
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

		try {

			// Save if non-existing
			if (this.getTemplateSettings(templateSetting).isEmpty()) {

				this.updateIsDefaultOfSameProjectAndToolTemplateSetting(templateSetting);
				this.getTemplateSettingDao().save(templateSetting);

			} else {
				throw new MiddlewareQueryException("Template setting already exists.");
			}

		} catch (Exception e) {

			this.logAndThrowException("Error encountered while adding Template Setting: "
					+ "WorkbenchDataManager.addTemplateSetting(templateSetting=" + templateSetting + "): " + e.getMessage(), e);
		}
		return templateSetting.getTemplateSettingId();
	}

	@Override
	public void updateTemplateSetting(TemplateSetting templateSetting) throws MiddlewareQueryException {

		try {

			this.updateIsDefaultOfSameProjectAndToolTemplateSetting(templateSetting);
			this.getTemplateSettingDao().merge(templateSetting);

		} catch (Exception e) {

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

			if (!sameProjectAndToolSettings.isEmpty()) {
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

		try {

			this.getTemplateSettingDao().makeTransient(templateSetting);

		} catch (Exception e) {

			this.logAndThrowException("Cannot delete TemplateSetting: WorkbenchDataManager.deleteTemplateSetting(templateSetting="
					+ templateSetting + "): " + e.getMessage(), e);
		}

	}

	@Override
	public void deleteTemplateSetting(Integer id) throws MiddlewareQueryException {

		try {

			TemplateSetting templateSettingsFilter = new TemplateSetting(id, null, null, null, null, null);
			templateSettingsFilter.setIsDefaultToNull();
			List<TemplateSetting> settings = this.getTemplateSettings(templateSettingsFilter);

			if (settings.size() == 1) {
				this.getTemplateSettingDao().makeTransient(settings.get(0));
			} else {
				this.logAndThrowException("Cannot delete TemplateSetting: WorkbenchDataManager.deleteTemplateSetting(id=" + id + ")");
			}

		} catch (Exception e) {

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
		try {
			return this.getStandardPresetDAO().saveOrUpdate(standardPreset);

		} catch (HibernateException e) {
			this.logAndThrowException(
					"Cannot perform: WorkbenchDataManager.saveOrUpdateStandardPreset(standardPreset=" + standardPreset.getName() + "): "
							+ e.getMessage(), e);
		}

		return null;
	}

	@Override
	public void deleteStandardPreset(int standardPresetId) throws MiddlewareQueryException {
		try {
			StandardPreset preset = this.getStandardPresetDAO().getById(standardPresetId);
			this.getCurrentSession().delete(preset);
		} catch (HibernateException e) {
			this.logAndThrowException("Cannot delete preset: WorkbenchDataManager.deleteStandardPreset(standardPresetId="
					+ standardPresetId + "): " + e.getMessage(), e);
		}
	}

	@Override
	public void close() {
		if (this.sessionProvider != null) {
			this.sessionProvider.close();
		}
	}
	
	@Override
	public List<UserDto> getAllUserDtosSorted() throws MiddlewareQueryException {
		return this.getUserDao().getAllUserDtosSorted();

	}

	@Override
	public Integer createUser(final UserDto userDto) throws MiddlewareQueryException {

		Integer idUserSaved = null;
		// user.access = 0 - Default User
		// user.instalid = 0 - Access all areas (legacy from the ICIS system) (not used)
		// user.status = 0 - Unassigned
		// user.type = 0 - Default user type (not used)

		Integer currentDate = Util.getCurrentDateAsIntegerValue();
		Person person = this.createPerson(userDto);

		User user = new User();
		user.setPersonid(person.getId());
		user.setPerson(person);
		user.setName(userDto.getUsername());
		user.setPassword(userDto.getPassword());
		user.setAccess(0);
		user.setAssignDate(currentDate);
		user.setCloseDate(currentDate);
		user.setInstalid(0);
		user.setStatus(userDto.getStatus());
		user.setType(0);

		// add user roles to the particular user
		user.setRoles(Arrays.asList(new UserRole(user, userDto.getRole().toUpperCase())));

		try {

			User recordSaved = this.getUserDao().saveOrUpdate(user);
			idUserSaved = recordSaved.getUserid();

		} catch (Exception e) {

			this.logAndThrowException(
					"Error encountered while saving User: WorkbenchDataManager.addUser(user=" + user + "): " + e.getMessage(), e);
		}

		UserInfo userInfo = new UserInfo();
		userInfo.setUserId(user.getUserid());
		userInfo.setLoginCount(0);
		this.getUserInfoDao().insertOrUpdateUserInfo(userInfo);

		return idUserSaved;

	}

	@Override
	public Integer updateUser(final UserDto userDto) throws MiddlewareQueryException {
		Integer currentDate = Util.getCurrentDateAsIntegerValue();
		User user = null;
		Integer idUserSaved = null;

		try {
			user = this.getUserDao().getById(userDto.getUserId());
			updatePerson(userDto, user.getPerson());

			user.setName(userDto.getUsername());
			user.setAssignDate(currentDate);
			user.setCloseDate(currentDate);
			user.setStatus(userDto.getStatus());
			
			// update user roles to the particular user
			UserRole role =	user.getRoles().get(0);
			if(!role.getRole().equals(userDto.getRole().toUpperCase())){
				role.setRole(userDto.getRole().toUpperCase());				
			}
			
			this.getUserDao().saveOrUpdate(user);
			idUserSaved = user.getUserid();
		} catch (final Exception e) {

			throw new MiddlewareQueryException(
					"Error encountered while saving User: UserDataManager.addUser(user=" + user + "): " + e.getMessage(), e);
		}

		return idUserSaved;
	}

	private Person createPerson(final UserDto userDto) {
		Person person = new Person();

		person.setFirstName(userDto.getFirstName());
		person.setMiddleName("");
		person.setLastName(userDto.getLastName());
		person.setEmail(userDto.getEmail());
		person.setTitle("-");
		person.setContact("-");
		person.setExtension("-");
		person.setFax("-");
		person.setInstituteId(0);
		person.setLanguage(0);
		person.setNotes("-");
		person.setPositionName("-");
		person.setPhone("-");
		this.addPerson(person);

		return person;
	}

	private Person updatePerson(final UserDto userDto, final Person person) {
		person.setFirstName(userDto.getFirstName());
		person.setMiddleName("");
		person.setLastName(userDto.getLastName());
		person.setEmail(userDto.getEmail());
		person.setTitle("-");
		person.setContact("-");
		person.setExtension("-");
		person.setFax("-");
		person.setInstituteId(0);
		person.setLanguage(0);
		person.setNotes("-");
		person.setPositionName("-");
		person.setPhone("-");
		this.addPerson(person);

		return person;
	}

}
