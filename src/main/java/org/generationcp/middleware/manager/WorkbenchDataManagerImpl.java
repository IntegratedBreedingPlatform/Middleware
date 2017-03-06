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
import org.generationcp.middleware.pojos.workbench.ToolLicenseInfo;
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

	public WorkbenchDataManagerImpl(final HibernateSessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
	}

	public Session getCurrentSession() {
		return this.sessionProvider.getSession();
	}

	private CropTypeDAO getCropTypeDao() {
		final CropTypeDAO cropTypeDao = new CropTypeDAO();
		cropTypeDao.setSession(this.getCurrentSession());
		return cropTypeDao;
	}

	private IbdbUserMapDAO getIbdbUserMapDao() {
		final IbdbUserMapDAO ibdbUserMapDao = new IbdbUserMapDAO();
		ibdbUserMapDao.setSession(this.getCurrentSession());
		return ibdbUserMapDao;
	}

	private PersonDAO getPersonDao() {
		final PersonDAO personDao = new PersonDAO();
		personDao.setSession(this.getCurrentSession());
		return personDao;
	}

	private ProjectActivityDAO getProjectActivityDao() {
		final ProjectActivityDAO projectActivityDao = new ProjectActivityDAO();
		projectActivityDao.setSession(this.getCurrentSession());
		return projectActivityDao;
	}

	private ProjectUserMysqlAccountDAO getProjectUserMysqlAccountDAO() {

		final ProjectUserMysqlAccountDAO projectUserMysqlAccountDAO = new ProjectUserMysqlAccountDAO();
		projectUserMysqlAccountDAO.setSession(this.getCurrentSession());
		return projectUserMysqlAccountDAO;
	}

	private ProjectDAO getProjectDao() {

		final ProjectDAO projectDao = new ProjectDAO();
		projectDao.setSession(this.getCurrentSession());
		return projectDao;
	}

	private ProjectUserMysqlAccountDAO getProjectUserMysqlAccountDao() {

		final ProjectUserMysqlAccountDAO projectUserMysqlAccountDao = new ProjectUserMysqlAccountDAO();
		projectUserMysqlAccountDao.setSession(this.getCurrentSession());
		return projectUserMysqlAccountDao;
	}

	@Override
	public ProjectUserInfoDAO getProjectUserInfoDao() {

		final ProjectUserInfoDAO projectUserInfoDao = new ProjectUserInfoDAO();
		projectUserInfoDao.setSession(this.getCurrentSession());
		return projectUserInfoDao;
	}

	@Override
	public void updateProjectsRolesForProject(final Project project, final List<ProjectUserRole> newRoles) {
		final List<ProjectUserRole> oldRoles = this.getProjectUserRolesByProject(project);

		final List<ProjectUserRole> toDeleteRoles = this.getUniqueUserRolesFrom(oldRoles, newRoles);
		final List<ProjectUserRole> toAddRoles = this.getUniqueUserRolesFrom(newRoles, oldRoles);

		this.deleteProjectUserRoles(toDeleteRoles);
		this.addProjectUserRole(toAddRoles);
	}

	private List<ProjectUserRole> getUniqueUserRolesFrom(final List<ProjectUserRole> list1, final List<ProjectUserRole> list2) {
		final List<ProjectUserRole> uniqueRoles = new ArrayList<>();
		for (final ProjectUserRole role : list1) {
			if (!this.projectRoleContains(role, list2)) {
				uniqueRoles.add(role);
			}
		}
		return uniqueRoles;
	}

	private boolean projectRoleContains(final ProjectUserRole oldRole, final List<ProjectUserRole> roles) {
		for (final ProjectUserRole role : roles) {
			if (oldRole.getUserId().equals(role.getUserId()) && oldRole.getRole().getRoleId().equals(role.getRole().getRoleId())) {
				return true;
			}
		}

		return false;
	}

	private ProjectUserRoleDAO getProjectUserRoleDao() {
		final ProjectUserRoleDAO projectUserRoleDao = new ProjectUserRoleDAO();
		projectUserRoleDao.setSession(this.getCurrentSession());
		return projectUserRoleDao;
	}

	private RoleDAO getRoleDao() {

		final RoleDAO roleDao = new RoleDAO();
		roleDao.setSession(this.getCurrentSession());
		return roleDao;
	}

	private SecurityQuestionDAO getSecurityQuestionDao() {

		final SecurityQuestionDAO securityQuestionDao = new SecurityQuestionDAO();
		securityQuestionDao.setSession(this.getCurrentSession());
		return securityQuestionDao;
	}

	private ToolConfigurationDAO getToolConfigurationDao() {

		final ToolConfigurationDAO toolConfigurationDao = new ToolConfigurationDAO();
		toolConfigurationDao.setSession(this.getCurrentSession());
		return toolConfigurationDao;
	}

	@Override
	public ToolDAO getToolDao() {

		final ToolDAO toolDao = new ToolDAO();
		toolDao.setSession(this.getCurrentSession());
		return toolDao;
	}

	private UserDAO getUserDao() {

		final UserDAO userDao = new UserDAO();
		userDao.setSession(this.getCurrentSession());
		return userDao;
	}

	private UserInfoDAO getUserInfoDao() {

		final UserInfoDAO userInfoDao = new UserInfoDAO();
		userInfoDao.setSession(this.getCurrentSession());
		return userInfoDao;
	}

	private WorkbenchDatasetDAO getWorkbenchDatasetDao() {

		final WorkbenchDatasetDAO workbenchDatasetDao = new WorkbenchDatasetDAO();
		workbenchDatasetDao.setSession(this.getCurrentSession());
		return workbenchDatasetDao;
	}

	private WorkbenchRuntimeDataDAO getWorkbenchRuntimeDataDao() {

		final WorkbenchRuntimeDataDAO workbenchRuntimeDataDao = new WorkbenchRuntimeDataDAO();
		workbenchRuntimeDataDao.setSession(this.getCurrentSession());
		return workbenchRuntimeDataDao;
	}

	private WorkbenchSettingDAO getWorkbenchSettingDao() {

		final WorkbenchSettingDAO workbenchSettingDao = new WorkbenchSettingDAO();
		workbenchSettingDao.setSession(this.getCurrentSession());
		return workbenchSettingDao;
	}

	private WorkflowTemplateDAO getWorkflowTemplateDao() {

		final WorkflowTemplateDAO workflowTemplateDao = new WorkflowTemplateDAO();
		workflowTemplateDao.setSession(this.getCurrentSession());
		return workflowTemplateDao;
	}

	private WorkbenchSidebarCategoryDAO getWorkbenchSidebarCategoryDao() {

		final WorkbenchSidebarCategoryDAO workbenchSidebarCategoryDAO = new WorkbenchSidebarCategoryDAO();
		workbenchSidebarCategoryDAO.setSession(this.getCurrentSession());
		return workbenchSidebarCategoryDAO;
	}

	private WorkbenchSidebarCategoryLinkDAO getWorkbenchSidebarCategoryLinkDao() {

		final WorkbenchSidebarCategoryLinkDAO workbenchSidebarCategoryLinkDAO = new WorkbenchSidebarCategoryLinkDAO();

		workbenchSidebarCategoryLinkDAO.setSession(this.getCurrentSession());
		return workbenchSidebarCategoryLinkDAO;
	}

	private TemplateSettingDAO getTemplateSettingDao() {

		final TemplateSettingDAO templateSettingDAO = new TemplateSettingDAO();
		templateSettingDAO.setSession(this.getCurrentSession());
		return templateSettingDAO;
	}

	@Override
	public StandardPresetDAO getStandardPresetDAO() {
		final StandardPresetDAO standardPresetDAO = new StandardPresetDAO();
		standardPresetDAO.setSession(this.getCurrentSession());
		return standardPresetDAO;
	}

	private void logAndThrowException(final String message, final Exception e) {
		WorkbenchDataManagerImpl.LOG.error(e.getMessage(), e);
		throw new MiddlewareQueryException(message + e.getMessage(), e);
	}

	private void logAndThrowException(final String message) {
		WorkbenchDataManagerImpl.LOG.error(message);
		throw new MiddlewareQueryException(message);
	}

	@Override
	public List<Project> getProjects() {
		return this.getProjectDao().getAll();
	}

	@Override
	public List<Project> getProjects(final int start, final int numOfRows) {
		return this.getProjectDao().getAll(start, numOfRows);
	}

	@Override
	public List<Project> getProjectsByCrop(final CropType cropType) {
		return this.getProjectDao().getProjectsByCrop(cropType);
	}

	@Override
	public List<Project> getProjectsByUser(final User user) {
		return this.getProjectUserRoleDao().getProjectsByUser(user);
	}

	@Override
	public Project saveOrUpdateProject(final Project project) {

		try {
			this.getProjectDao().merge(project);
		} catch (final Exception e) {
			throw new MiddlewareQueryException("Cannot save Project: WorkbenchDataManager.saveOrUpdateProject(project=" + project + "): " + e.getMessage(), e);
		}

		return project;
	}

	@Override
	public ProjectUserInfo saveOrUpdateProjectUserInfo(final ProjectUserInfo projectUserInfo) {

		try {
			this.getProjectUserInfoDao().merge(projectUserInfo);
		} catch (final Exception e) {
			throw new MiddlewareQueryException("Cannot save ProjectUserInfo: WorkbenchDataManager.saveOrUpdateProjectUserInfo(project="
					+ projectUserInfo + "): " + e.getMessage(), e);
		}

		return projectUserInfo;
	}

	@Override
	public Project addProject(final Project project) {

		try {
			project.setUniqueID(UUID.randomUUID().toString());
			this.getProjectDao().save(project);
		} catch (final Exception e) {
			throw new MiddlewareQueryException("Cannot save Project: WorkbenchDataManager.addProject(project=" + project + "): " + e.getMessage(),
					e);
		}
		return project;
	}

	@Override
	public Project mergeProject(final Project project) {
		try {
			this.getProjectDao().merge(project);
		} catch (final Exception e) {
			throw new MiddlewareQueryException("Cannot save Project: WorkbenchDataManager.updateProject(project=" + project + "): " + e.getMessage(),
					e);
		}
		return project;
	}

	@Override
	public void deleteProjectDependencies(final Project project) {

		try {
			final Long projectId = project.getProjectId();
			final List<ProjectActivity> projectActivities =
					this.getProjectActivitiesByProjectId(projectId, 0, (int) this.countProjectActivitiesByProjectId(projectId));
			for (final ProjectActivity projectActivity : projectActivities) {
				this.deleteProjectActivity(projectActivity);
			}

			final List<ProjectUserRole> projectUsers = this.getProjectUserRolesByProject(project);
			for (final ProjectUserRole projectUser : projectUsers) {
				this.deleteProjectUserRole(projectUser);
			}

			final List<ProjectUserMysqlAccount> mysqlaccounts =
					this.getProjectUserMysqlAccountDAO().getByProjectId(project.getProjectId().intValue());
			if (mysqlaccounts != null) {
				for (final ProjectUserMysqlAccount mysqlaccount : mysqlaccounts) {
					this.deleteProjectUserMysqlAccount(mysqlaccount);
				}
			}

			final List<WorkbenchDataset> datasets =
					this.getWorkbenchDatasetByProjectId(projectId, 0, (int) this.countWorkbenchDatasetByProjectId(projectId));
			for (final WorkbenchDataset dataset : datasets) {
				this.deleteWorkbenchDataset(dataset);
			}

			final List<ProjectUserInfo> projectUserInfos = this.getByProjectId(projectId.intValue());
			for (final ProjectUserInfo projectUserInfo : projectUserInfos) {
				this.deleteProjectUserInfoDao(projectUserInfo);
			}

			// remove template settings per project
			final TemplateSetting setting = new TemplateSetting();
			setting.setProjectId(projectId.intValue());

			final List<TemplateSetting> templateSettings = this.getTemplateSettings(setting);
			for (final TemplateSetting templateSetting : templateSettings) {
				this.deleteTemplateSetting(templateSetting);
			}

		} catch (final Exception e) {
			throw new MiddlewareQueryException("Cannot delete Project Dependencies: WorkbenchDataManager.deleteProjectDependencies(project="
					+ project + "): " + e.getMessage(), e);
		}
	}

	public List<IbdbUserMap> getIbdbUserMapsByProjectId(final Long projectId) {
		return this.getIbdbUserMapDao().getIbdbUserMapByID(projectId);
	}

	public void deleteProjectUserInfoDao(final ProjectUserInfo projectUserInfo) {

		try {

			this.getProjectUserInfoDao().makeTransient(projectUserInfo);

		} catch (final Exception e) {

			this.logAndThrowException("Cannot delete ProjectUserInfo: WorkbenchDataManager.deleteProjectUserInfoDao(projectUserInfo="
					+ projectUserInfo + "): " + e.getMessage(), e);
		}
	}

	public void deleteProjectUserMysqlAccount(final ProjectUserMysqlAccount mysqlaccount) {

		try {

			this.getProjectUserMysqlAccountDAO().makeTransient(mysqlaccount);

		} catch (final Exception e) {

			this.logAndThrowException(
					"Cannot delete ProjectUserMysqlAccount: WorkbenchDataManager.deleteProjectUserMysqlAccount(mysqlaccount=" + mysqlaccount
							+ "): " + e.getMessage(),
					e);
		}
	}

	@Override
	public void deleteProject(final Project project) {

		try {

			this.getProjectDao().deleteProject(project.getProjectName());

		} catch (final Exception e) {

			this.logAndThrowException(
					"Cannot delete Project: WorkbenchDataManager.deleteProject(project=" + project + "): " + e.getMessage(), e);
		}
	}

	@Override
	public List<WorkflowTemplate> getWorkflowTemplates() {
		return this.getWorkflowTemplateDao().getAll();
	}

	@Override
	public List<WorkflowTemplate> getWorkflowTemplateByName(final String name) {
		return this.getWorkflowTemplateDao().getByName(name);
	}

	@Override
	public List<WorkflowTemplate> getWorkflowTemplates(final int start, final int numOfRows) {
		return this.getWorkflowTemplateDao().getAll(start, numOfRows);
	}

	@Override
	public List<Tool> getAllTools() {
		return this.getToolDao().getAll();
	}

	@Override
	public Tool getToolWithName(final String toolId) {
		return this.getToolDao().getByToolName(toolId);
	}

	@Override
	public List<Tool> getToolsWithType(final ToolType toolType) {
		return this.getToolDao().getToolsByToolType(toolType);
	}

	@Override
	public boolean isPersonExists(final String firstName, final String lastName) {
		return this.getPersonDao().isPersonExists(firstName, lastName);
	}

	@Override
	public boolean isPersonWithEmailExists(final String email) {
		return this.getPersonDao().isPersonWithEmailExists(email);
	}

	@Override
	public Person getPersonByEmail(final String email) {
		return this.getPersonDao().getPersonByEmail(email);
	}

	@Override
	public Person getPersonByEmailAndName(final String email, final String firstName, final String lastName) {
		return this.getPersonDao().getPersonByEmailAndName(email, firstName, lastName);
	}

	@Override
	public boolean isUsernameExists(final String userName) {
		return this.getUserDao().isUsernameExists(userName);
	}

	@Override
	public boolean isPersonWithUsernameAndEmailExists(final String username, final String email) {
		return this.getPersonDao().isPersonWithUsernameAndEmailExists(username, email);
	}

	@Override
	public Integer addPerson(final Person person) {

		Integer idPersonSaved = null;
		try {

			final Person recordSaved = this.getPersonDao().saveOrUpdate(person);
			idPersonSaved = recordSaved.getId();

		} catch (final Exception e) {

			this.logAndThrowException(
					"Error encountered while saving Person: WorkbenchDataManager.addPerson(person=" + person + "): " + e.getMessage(), e);
		}
		return idPersonSaved;
	}

	@Override
	public Integer addUser(final User user) {

		Integer idUserSaved = null;
		try {

			final User recordSaved = this.getUserDao().saveOrUpdate(user);
			idUserSaved = recordSaved.getUserid();

		} catch (final Exception e) {

			this.logAndThrowException(
					"Error encountered while saving User: WorkbenchDataManager.addUser(user=" + user + "): " + e.getMessage(), e);
		}

		return idUserSaved;

	}

	@Override
	public Project getProjectById(final Long projectId) {
		return this.getProjectDao().getById(projectId);
	}

	@Override
	public Project getProjectByNameAndCrop(final String projectName, final CropType cropType) {
		return this.getProjectDao().getProjectByNameAndCrop(projectName, cropType);
	}

	@Override
	public Project getProjectByUuidAndCrop(final String projectUuid, final String cropType) {
		return this.getProjectDao().getByUuid(projectUuid, cropType);
	}

	@Override
	public Integer addWorkbenchDataset(final WorkbenchDataset dataset) {

		Integer workbenchDatasetSaved = null;
		try {

			final WorkbenchDataset datasetSaved = this.getWorkbenchDatasetDao().saveOrUpdate(dataset);
			workbenchDatasetSaved = datasetSaved.getDatasetId().intValue();

		} catch (final Exception e) {

			this.logAndThrowException("Error encountered while saving workbench dataset: WorkbenchDataManager.addWorkbenchDataset(dataset="
					+ dataset + "): " + e.getMessage(), e);
		}

		return workbenchDatasetSaved;
	}

	@Override
	public WorkbenchDataset getWorkbenchDatasetById(final Long datasetId) {
		return this.getWorkbenchDatasetDao().getById(datasetId);
	}

	@Override
	public void deleteWorkbenchDataset(final WorkbenchDataset dataset) {

		try {

			this.getWorkbenchDatasetDao().makeTransient(dataset);

		} catch (final Exception e) {

			this.logAndThrowException("Cannot delete WorkbenchDataset: WorkbenchDataManager.deleteWorkbenchDataset(dataset=" + dataset
					+ "):  " + e.getMessage(), e);
		}
	}

	@Override
	public List<User> getAllUsers() {
		return this.getUserDao().getAll();
	}

	@Override
	public List<User> getAllUsersSorted() {
		return this.getUserDao().getAllUsersSorted();
	}

	@Override
	public long countAllUsers() {
		return this.getUserDao().countAll();
	}

	@Override
	public User getUserById(final int id) {
		return this.getUserDao().getById(id, false);
	}

	@Override
	public List<User> getUserByName(final String name, final int start, final int numOfRows, final Operation op) {
		final UserDAO dao = this.getUserDao();
		List<User> users = new ArrayList<User>();
		if (op == Operation.EQUAL) {
			users = dao.getByNameUsingEqual(name, start, numOfRows);
		} else if (op == Operation.LIKE) {
			users = dao.getByNameUsingLike(name, start, numOfRows);
		}
		return users;
	}

	@Override
	public void deleteUser(final User user) {

		try {

			this.getUserDao().makeTransient(user);

		} catch (final Exception e) {

			this.logAndThrowException(
					"Error encountered while deleting User: WorkbenchDataManager.deleteUser(user=" + user + "):  " + e.getMessage(), e);
		}
	}

	@Override
	public List<Person> getAllPersons() {
		return this.getPersonDao().getAll();
	}

	@Override
	public long countAllPersons() {
		return this.getPersonDao().countAll();
	}

	@Override
	public Person getPersonById(final int id) {
		return this.getPersonDao().getById(id, false);
	}

	@Override
	public void deletePerson(final Person person) {

		try {

			this.getPersonDao().makeTransient(person);

		} catch (final Exception e) {

			this.logAndThrowException(
					"Error encountered while deleting Person: WorkbenchDataManager.deletePerson(person=" + person + "): " + e.getMessage(),
					e);
		}
	}

	@Override
	public Project getLastOpenedProject(final Integer userId) {
		return this.getProjectDao().getLastOpenedProject(userId);
	}

	@Override
	public List<WorkbenchDataset> getWorkbenchDatasetByProjectId(final Long projectId, final int start, final int numOfRows) {
		return this.getWorkbenchDatasetDao().getByProjectId(projectId, start, numOfRows);
	}

	@Override
	public long countWorkbenchDatasetByProjectId(final Long projectId) {
		return this.getWorkbenchDatasetDao().countByProjectId(projectId);
	}

	@Override
	public List<WorkbenchDataset> getWorkbenchDatasetByName(final String name, final Operation op, final int start, final int numOfRows)
			throws MiddlewareQueryException {
		return this.getWorkbenchDatasetDao().getByName(name, op, start, numOfRows);
	}

	@Override
	public long countWorkbenchDatasetByName(final String name, final Operation op) {
		return this.getWorkbenchDatasetDao().countByName(name, op);
	}

	@Override
	public Integer addProjectUserRole(final Project project, final User user, final Role role) {
		final ProjectUserRole projectUserRole = new ProjectUserRole();
		projectUserRole.setProject(project);
		projectUserRole.setUserId(user.getUserid());
		projectUserRole.setRole(role);
		return this.addProjectUserRole(projectUserRole);
	}

	@Override
	public Integer addProjectUserRole(final ProjectUserRole projectUserRole) {

		Integer idSaved = null;
		try {

			final ProjectUserRole recordSaved = this.getProjectUserRoleDao().saveOrUpdate(projectUserRole);
			idSaved = recordSaved.getProjectUserId();

		} catch (final Exception e) {

			this.logAndThrowException(
					"Error encountered while saving ProjectUserRole: WorkbenchDataManager.addProjectUserRole(projectUserRole="
							+ projectUserRole + "): " + e.getMessage(),
					e);
		}

		return idSaved;
	}

	@Override
	public void deleteProjectUserRolesByProject(final Project project) {
		// remove all previous roles
		this.deleteProjectUserRoles(this.getProjectUserRolesByProject(project));
	}

	@Override
	public void deleteProjectUserRoles(final List<ProjectUserRole> oldRoles) {
		// remove all previous roles
		try {
			for (final ProjectUserRole projectUserRole : oldRoles) {
				this.getCurrentSession().delete(projectUserRole);
			}
		} catch (final Exception e) {
			this.logAndThrowException("Error encountered while deleting ProjectUser: WorkbenchDataManager.deleteProjectUserRoles(oldRoles="
					+ oldRoles + "): " + e.getMessage(), e);
		}
	}

	@Override
	public List<Integer> addProjectUserRole(final List<ProjectUserRole> projectUserRoles) {

		final List<Integer> idsSaved = new ArrayList<Integer>();
		try {

			final ProjectUserRoleDAO dao = this.getProjectUserRoleDao();

			for (final ProjectUserRole projectUser : projectUserRoles) {
				final ProjectUserRole recordSaved = dao.saveOrUpdate(projectUser);
				idsSaved.add(recordSaved.getProjectUserId());
			}

		} catch (final Exception e) {

			this.logAndThrowException(
					"Error encountered while saving ProjectUserRoles: WorkbenchDataManager.addProjectUserRoles(projectUserRoles="
							+ projectUserRoles + "): " + e.getMessage(),
					e);
		}

		return idsSaved;
	}

	@Override
	public List<ProjectUserRole> getProjectUserRolesByProject(final Project project) {
		return this.getProjectUserRoleDao().getByProject(project);
	}

	@Override
	public void deleteProjectUserRole(final ProjectUserRole projectUserRole) {

		try {
			this.getProjectUserRoleDao().makeTransient(projectUserRole);
		} catch (final Exception e) {
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
	public long countUsersByProjectId(final Long projectId) {
		return this.getProjectUserRoleDao().countUsersByProjectId(projectId);
	}

	@Override
	public List<CropType> getInstalledCropDatabses() {
		return this.getCropTypeDao().getAll();
	}

	@Override
	public CropType getCropTypeByName(final String cropName) {
		return this.getCropTypeDao().getByName(cropName);
	}

	@Override
	public String addCropType(final CropType cropType) {

		final CropTypeDAO dao = this.getCropTypeDao();
		if (this.getCropTypeDao().getByName(cropType.getCropName()) != null) {
			this.logAndThrowException("Crop type already exists.");
		}

		String idSaved = null;
		try {

			final CropType recordSaved = dao.saveOrUpdate(cropType);
			idSaved = recordSaved.getCropName();

		} catch (final Exception e) {

			this.logAndThrowException("Error encountered while adding crop type: WorkbenchDataManager.addCropType(cropType=" + cropType
					+ "): " + e.getMessage(), e);
		}

		return idSaved;
	}

	public List<ProjectUserInfo> getByProjectId(final Integer projectId) {
		return this.getProjectUserInfoDao().getByProjectId(projectId);

	}

	@Override
	public Integer addProjectActivity(final ProjectActivity projectActivity) {
		final List<ProjectActivity> list = new ArrayList<ProjectActivity>();
		list.add(projectActivity);

		final List<Integer> ids = this.addProjectActivity(list);

		return !ids.isEmpty() ? ids.get(0) : null;
	}

	@Override
	public List<Integer> addProjectActivity(final List<ProjectActivity> projectActivityList) {

		return this.addOrUpdateProjectActivityData(projectActivityList, Operation.ADD);
	}

	private List<Integer> addOrUpdateProjectActivityData(final List<ProjectActivity> projectActivityList, final Operation operation)
			throws MiddlewareQueryException {

		final List<Integer> idsSaved = new ArrayList<Integer>();
		try {

			final ProjectActivityDAO dao = this.getProjectActivityDao();

			for (final ProjectActivity projectActivityListData : projectActivityList) {
				final ProjectActivity recordSaved = dao.save(projectActivityListData);
				idsSaved.add(recordSaved.getProjectActivityId());
			}

		} catch (final Exception e) {

			this.logAndThrowException(
					"Error encountered while adding addProjectActivity: WorkbenchDataManager.addOrUpdateProjectActivityData(projectActivityList="
							+ projectActivityList + ", operation=" + operation + "): " + e.getMessage(),
					e);
		}

		return idsSaved;
	}

	@Override
	public List<ProjectActivity> getProjectActivitiesByProjectId(final Long projectId, final int start, final int numOfRows) {
		return this.getProjectActivityDao().getByProjectId(projectId, start, numOfRows);
	}

	@Override
	public void deleteProjectActivity(final ProjectActivity projectActivity) {

		try {

			this.getProjectActivityDao().makeTransient(projectActivity);

		} catch (final Exception e) {

			this.logAndThrowException(
					"Error encountered while deleting ProjectActivity: WorkbenchDataManager.deleteProjectActivity(projectActivity="
							+ projectActivity + "): " + e.getMessage(),
					e);
		}
	}

	@Override
	public long countProjectActivitiesByProjectId(final Long projectId) {
		return this.getProjectActivityDao().countByProjectId(projectId);
	}

	@Override
	public Integer addToolConfiguration(final ToolConfiguration toolConfig) {
		return this.addOrUpdateToolConfiguration(toolConfig, Operation.ADD);
	}

	@Override
	public Integer updateToolConfiguration(final ToolConfiguration toolConfig) {
		return this.addOrUpdateToolConfiguration(toolConfig, Operation.UPDATE);
	}

	private Integer addOrUpdateToolConfiguration(final ToolConfiguration toolConfig, final Operation op) {

		Integer idSaved = null;
		try {

			final ToolConfiguration recordSaved = this.getToolConfigurationDao().saveOrUpdate(toolConfig);
			idSaved = recordSaved.getConfigId();

		} catch (final Exception e) {

			this.logAndThrowException(
					"Error encountered while saving ToolConfiguration: WorkbenchDataManager.addOrUpdateToolConfiguration(toolConfig="
							+ toolConfig + ", operation=" + op + "): " + e.getMessage(),
					e);
		}
		return idSaved;
	}

	@Override
	public void deleteToolConfiguration(final ToolConfiguration toolConfig) {

		try {

			this.getToolConfigurationDao().makeTransient(toolConfig);

		} catch (final Exception e) {

			this.logAndThrowException(
					"Error encountered while deleting ToolConfiguration: WorkbenchDataManager.deleteToolConfiguration(toolConfig="
							+ toolConfig + "): " + e.getMessage(),
					e);
		}
	}

	@Override
	public List<ToolConfiguration> getListOfToolConfigurationsByToolId(final Long toolId) {
		return this.getToolConfigurationDao().getListOfToolConfigurationsByToolId(toolId);
	}

	@Override
	public ToolConfiguration getToolConfigurationByToolIdAndConfigKey(final Long toolId, final String configKey) {
		return this.getToolConfigurationDao().getToolConfigurationByToolIdAndConfigKey(toolId, configKey);
	}

	@Override
	public Integer addIbdbUserMap(final IbdbUserMap userMap) {

		try {
			final IbdbUserMap existingMapping = this.getIbdbUserMap(userMap.getWorkbenchUserId(), userMap.getProjectId());
			if (existingMapping == null) {
				this.getIbdbUserMapDao().saveOrUpdate(userMap);
				return userMap.getIbdbUserMapId().intValue();
			} else {
				return existingMapping.getIbdbUserMapId().intValue();
			}
		} catch (final Exception e) {
			final String message =
					"Error encountered while adding IbdbUserMap (linking workbench user id to crop database user): WorkbenchDataManager.addIbdbUserMap(userMap="
							+ userMap + "): " + e.getMessage();
			WorkbenchDataManagerImpl.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	@Override
	public Integer getCurrentIbdbUserId(final Long projectId, final Integer workbenchUserId) {
		Integer ibdbUserId = null;
		final IbdbUserMap userMapEntry = this.getIbdbUserMap(workbenchUserId, projectId);
		if (userMapEntry != null) {
			ibdbUserId = userMapEntry.getIbdbUserId();
		}
		return ibdbUserId;
	}

	@Override
	public IbdbUserMap getIbdbUserMap(final Integer workbenchUserId, final Long projectId) {

		IbdbUserMap bbdbUserMap = null;
		try {

			bbdbUserMap = this.getIbdbUserMapDao().getIbdbUserMapByUserAndProjectID(workbenchUserId, projectId);

		} catch (final Exception e) {

			this.logAndThrowException(
					"Error encountered while retrieving Local IbdbUserMap: WorkbenchDataManager.getIbdbUserMap(workbenchUserId="
							+ workbenchUserId + ", projectId=" + projectId + "): " + e.getMessage(),
					e);
		}

		return bbdbUserMap;
	}

	@Override
	public Integer getLocalIbdbUserId(final Integer workbenchUserId, final Long projectId) {

		Integer ibdbUserId = null;
		try {

			ibdbUserId = this.getIbdbUserMapDao().getLocalIbdbUserId(workbenchUserId, projectId);

		} catch (final Exception e) {

			this.logAndThrowException(
					"Error encountered while retrieving Local IBDB user id: WorkbenchDataManager.getLocalIbdbUserId(workbenchUserId="
							+ workbenchUserId + ", projectId=" + projectId + "): " + e.getMessage(),
					e);
		}

		return ibdbUserId;
	}

	@Override
	public Integer getWorkbenchUserIdByIBDBUserIdAndProjectId(final Integer ibdbUserId, final Long projectId) {
		return this.getIbdbUserMapDao().getWorkbenchUserId(ibdbUserId, projectId);
	}

	@Override
	public Integer updateWorkbenchRuntimeData(final WorkbenchRuntimeData workbenchRuntimeData) {

		try {

			this.getWorkbenchRuntimeDataDao().saveOrUpdate(workbenchRuntimeData);

		} catch (final Exception e) {

			this.logAndThrowException(
					"Error encountered while adding IbdbUserMap: WorkbenchDataManager.updateWorkbenchRuntimeData(workbenchRuntimeData="
							+ workbenchRuntimeData + "): " + e.getMessage(),
					e);
		}

		return workbenchRuntimeData.getId();
	}

	@Override
	public WorkbenchRuntimeData getWorkbenchRuntimeData() {
		final List<WorkbenchRuntimeData> list = this.getWorkbenchRuntimeDataDao().getAll(0, 1);
		return !list.isEmpty() ? list.get(0) : null;
	}

	@Override
	public Role getRoleById(final Integer id) {
		return this.getRoleDao().getById(id);
	}

	@Override
	public Role getRoleByNameAndWorkflowTemplate(final String name, final WorkflowTemplate workflowTemplate) {
		return this.getRoleDao().getByNameAndWorkflowTemplate(name, workflowTemplate);
	}

	@Override
	public List<Role> getRolesByWorkflowTemplate(final WorkflowTemplate workflowTemplate) {
		return this.getRoleDao().getByWorkflowTemplate(workflowTemplate);
	}

	@Override
	public WorkflowTemplate getWorkflowTemplateByRole(final Role role) {
		return role.getWorkflowTemplate();
	}

	@Override
	public List<Role> getRolesByProjectAndUser(final Project project, final User user) {
		return this.getProjectUserRoleDao().getRolesByProjectAndUser(project, user);
	}

	@Override
	public List<Role> getAllRoles() {
		return this.getRoleDao().getAll();
	}

	@Override
	public List<Role> getAllRolesDesc() {
		return this.getRoleDao().getAllRolesDesc();
	}

	@Override
	public List<Role> getAllRolesOrderedByLabel() {
		try {
			return this.getRoleDao().getAllRolesOrderedByLabel();
		} catch (final Exception e) {
			this.logAndThrowException("Error encountered while getting all roles (sorted): " + e.getMessage(), e);
		}
		return null;
	}

	@Override
	public WorkbenchSetting getWorkbenchSetting() {
		try {
			final List<WorkbenchSetting> list = this.getWorkbenchSettingDao().getAll();
			final WorkbenchSetting setting = list.isEmpty() ? null : list.get(0);

			if (setting != null && this.installationDirectory != null) {
				setting.setInstallationDirectory(this.installationDirectory);
			}

			return setting;
		} catch (final Exception e) {
			this.logAndThrowException("Error encountered while getting workbench setting: " + e.getMessage(), e);
		}
		return null;
	}

	@Override
	public void addSecurityQuestion(final SecurityQuestion securityQuestion) {

		try {

			this.getSecurityQuestionDao().saveOrUpdate(securityQuestion);

		} catch (final Exception e) {

			this.logAndThrowException("Error encountered while adding Security Question: "
					+ "WorkbenchDataManager.addSecurityQuestion(securityQuestion=" + securityQuestion + "): " + e.getMessage(), e);
		}
	}

	@Override
	public List<SecurityQuestion> getQuestionsByUserId(final Integer userId) {
		try {
			return this.getSecurityQuestionDao().getByUserId(userId);
		} catch (final Exception e) {
			this.logAndThrowException("Error encountered while getting Security Questions: "
					+ "WorkbenchDataManager.getQuestionsByUserId(userId=" + userId + "): " + e.getMessage(), e);
		}
		return new ArrayList<SecurityQuestion>();
	}

	@Override
	public ProjectUserMysqlAccount getProjectUserMysqlAccountByProjectIdAndUserId(final Integer projectId, final Integer userId)
			throws MiddlewareQueryException {
		return this.getProjectUserMysqlAccountDao().getByProjectIdAndUserId(projectId, userId);
	}

	@Override
	public Integer addProjectUserMysqlAccount(final ProjectUserMysqlAccount record) {
		final List<ProjectUserMysqlAccount> tosave = new ArrayList<ProjectUserMysqlAccount>();
		tosave.add(record);
		final List<Integer> idsOfRecordsSaved = this.addProjectUserMysqlAccount(tosave);
		if (!idsOfRecordsSaved.isEmpty()) {
			return idsOfRecordsSaved.get(0);
		} else {
			return null;
		}
	}

	@Override
	public List<Integer> addProjectUserMysqlAccounts(final List<ProjectUserMysqlAccount> records) {
		return this.addProjectUserMysqlAccount(records);
	}

	private List<Integer> addProjectUserMysqlAccount(final List<ProjectUserMysqlAccount> records) {

		final List<Integer> idsSaved = new ArrayList<Integer>();
		try {
			final ProjectUserMysqlAccountDAO dao = this.getProjectUserMysqlAccountDao();

			for (final ProjectUserMysqlAccount record : records) {
				final ProjectUserMysqlAccount recordSaved = dao.saveOrUpdate(record);
				idsSaved.add(recordSaved.getId());
			}
		} catch (final Exception e) {

			this.logAndThrowException(
					"Error encountered while adding ProjectUserMysqlAccount: WorkbenchDataManager.addProjectUserMysqlAccount(records="
							+ records + "): " + e.getMessage(),
					e);
		}

		return idsSaved;
	}

	@Override
	public UserInfo getUserInfo(final int userId) {
		try {
			return this.getUserInfoDao().getUserInfoByUserId(userId);
		} catch (final Exception e) {
			this.logAndThrowException("Cannot increment login count for user_id =" + userId + "): " + e.getMessage(), e);
		}

		return new UserInfo();
	}

	@Override
	public UserInfo getUserInfoByUsername(final String username) {
		final User user = this.getUserByName(username, 0, 1, Operation.EQUAL).get(0);

		return this.getUserInfo(user.getUserid());
	}

	@Override
	public User getUserByUsername(final String userName) {
		return this.getUserDao().getUserByUserName(userName);
	}

	@Override
	public UserInfo getUserInfoByResetToken(final String token) {
		return this.getUserInfoDao().getUserInfoByToken(token);
	}

	@Override
	public UserInfo updateUserInfo(final UserInfo userInfo) {

		try {

			this.getUserInfoDao().update(userInfo);

		} catch (final Exception e) {

			this.logAndThrowException("Cannot update userInfo =" + userInfo.getUserId() + "): " + e.getMessage(), e);

		}
		return userInfo;
	}

	@Override
	public void incrementUserLogInCount(final int userId) {

		try {

			final UserInfo userdetails = this.getUserInfoDao().getUserInfoByUserId(userId);
			if (userdetails != null) {
				this.getUserInfoDao().updateLoginCounter(userdetails);
			}

		} catch (final Exception e) {

			this.logAndThrowException("Cannot increment login count for user_id =" + userId + "): " + e.getMessage(), e);
		}
	}

	@Override
	public void insertOrUpdateUserInfo(final UserInfo userDetails) {
		this.getUserInfoDao().insertOrUpdateUserInfo(userDetails);
	}

	@Override
	public boolean changeUserPassword(final String username, final String password) {
		return this.getUserDao().changePassword(username, password);
	}

	@Override
	public List<WorkbenchSidebarCategory> getAllWorkbenchSidebarCategory() {
		return this.getWorkbenchSidebarCategoryDao().getAll();
	}

	@Override
	public List<WorkbenchSidebarCategoryLink> getAllWorkbenchSidebarLinks() {
		return this.getWorkbenchSidebarCategoryLinkDao().getAll();
	}

	@Override
	public List<WorkbenchSidebarCategoryLink> getAllWorkbenchSidebarLinksByCategoryId(final WorkbenchSidebarCategory category)
			throws MiddlewareQueryException {
		return this.getWorkbenchSidebarCategoryLinkDao().getAllWorkbenchSidebarLinksByCategoryId(category, 0, Integer.MAX_VALUE);
	}

	public String getInstallationDirectory() {
		return this.installationDirectory;
	}

	public void setInstallationDirectory(final String installationDirectory) {
		this.installationDirectory = installationDirectory;
	}

	@Override
	public List<TemplateSetting> getTemplateSettings(final TemplateSetting templateSettingFilter) {
		return this.getTemplateSettingDao().get(templateSettingFilter);
	}

	@Override
	public Integer addTemplateSetting(final TemplateSetting templateSetting) {

		try {

			// Save if non-existing
			if (this.getTemplateSettings(templateSetting).isEmpty()) {

				this.updateIsDefaultOfSameProjectAndToolTemplateSetting(templateSetting);
				this.getTemplateSettingDao().save(templateSetting);

			} else {
				throw new MiddlewareQueryException("Template setting already exists.");
			}

		} catch (final Exception e) {

			this.logAndThrowException("Error encountered while adding Template Setting: "
					+ "WorkbenchDataManager.addTemplateSetting(templateSetting=" + templateSetting + "): " + e.getMessage(), e);
		}
		return templateSetting.getTemplateSettingId();
	}

	@Override
	public void updateTemplateSetting(final TemplateSetting templateSetting) {

		try {

			this.updateIsDefaultOfSameProjectAndToolTemplateSetting(templateSetting);
			this.getTemplateSettingDao().merge(templateSetting);

		} catch (final Exception e) {

			this.logAndThrowException("Cannot update TemplateSeting: WorkbenchDataManager.updateTemplateSetting(templateSetting="
					+ templateSetting + "): " + e.getMessage(), e);
		}
	}

	/**
	 * If the new template setting's isDefault == true, set all others with the same project id and tool to isDefault = false
	 */
	private void updateIsDefaultOfSameProjectAndToolTemplateSetting(final TemplateSetting templateSetting) {
		if (templateSetting.isDefault()) {
			final TemplateSetting templateSettingFilter =
					new TemplateSetting(null, templateSetting.getProjectId(), null, templateSetting.getTool(), null, null);

			final List<TemplateSetting> sameProjectAndToolSettings = this.getTemplateSettings(templateSettingFilter);

			if (!sameProjectAndToolSettings.isEmpty()) {
				for (final TemplateSetting setting : sameProjectAndToolSettings) {
					if (!setting.getTemplateSettingId().equals(templateSetting.getTemplateSettingId()) && setting.isDefault()) {
						setting.setIsDefault(Boolean.FALSE);
						this.getTemplateSettingDao().merge(setting);
					}
				}
			}
		}
	}

	@Override
	public void deleteTemplateSetting(final TemplateSetting templateSetting) {

		try {

			this.getTemplateSettingDao().makeTransient(templateSetting);

		} catch (final Exception e) {

			this.logAndThrowException("Cannot delete TemplateSetting: WorkbenchDataManager.deleteTemplateSetting(templateSetting="
					+ templateSetting + "): " + e.getMessage(), e);
		}

	}

	@Override
	public void deleteTemplateSetting(final Integer id) {

		try {

			final TemplateSetting templateSettingsFilter = new TemplateSetting(id, null, null, null, null, null);
			templateSettingsFilter.setIsDefaultToNull();
			final List<TemplateSetting> settings = this.getTemplateSettings(templateSettingsFilter);

			if (settings.size() == 1) {
				this.getTemplateSettingDao().makeTransient(settings.get(0));
			} else {
				this.logAndThrowException("Cannot delete TemplateSetting: WorkbenchDataManager.deleteTemplateSetting(id=" + id + ")");
			}

		} catch (final Exception e) {

			this.logAndThrowException(
					"Cannot delete TemplateSetting: WorkbenchDataManager.deleteTemplateSetting(id=" + id + "): " + e.getMessage(), e);
		}

	}

	@Override
	public Project getLastOpenedProjectAnyUser() {
		return this.getProjectDao().getLastOpenedProjectAnyUser();
	}

	@Override
	public Boolean isLastOpenedProjectChanged() {

		final Project project = this.getProjectDao().getLastOpenedProjectAnyUser();

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
	public List<StandardPreset> getStandardPresetFromCropAndTool(final String cropName, final int toolId) {

		try {
			final Criteria criteria = this.getCurrentSession().createCriteria(StandardPreset.class);

			criteria.add(Restrictions.eq("cropName", cropName));
			criteria.add(Restrictions.eq("toolId", toolId));

			return criteria.list();
		} catch (final HibernateException e) {
			this.logAndThrowException(
					"error in: WorkbenchDataManager.getAllProgramPresetFromProgram(cropName=" + cropName + "): " + e.getMessage(), e);
		}

		return new ArrayList<StandardPreset>();
	}

	@Override
	public List<StandardPreset> getStandardPresetFromCropAndTool(final String cropName, final int toolId, final String toolSection)
			throws MiddlewareQueryException {

		try {
			final Criteria criteria = this.getCurrentSession().createCriteria(StandardPreset.class);

			criteria.add(Restrictions.eq("cropName", cropName));
			criteria.add(Restrictions.eq("toolId", toolId));
			criteria.add(Restrictions.eq("toolSection", toolSection));

			return criteria.list();
		} catch (final HibernateException e) {
			this.logAndThrowException(
					"error in: WorkbenchDataManager.getAllProgramPresetFromProgram(cropName=" + cropName + "): " + e.getMessage(), e);
		}

		return new ArrayList<StandardPreset>();
	}

	@Override
	public List<StandardPreset> getStandardPresetFromCropAndToolByName(final String presetName, final String cropName, final int toolId,
			final String toolSection) throws MiddlewareQueryException {

		try {
			final Criteria criteria = this.getCurrentSession().createCriteria(StandardPreset.class);

			criteria.add(Restrictions.eq("cropName", cropName));
			criteria.add(Restrictions.eq("toolId", toolId));
			criteria.add(Restrictions.eq("toolSection", toolSection));
			criteria.add(Restrictions.like("name", presetName));

			return criteria.list();
		} catch (final HibernateException e) {
			this.logAndThrowException(
					"error in: WorkbenchDataManager.getAllProgramPresetFromProgram(cropName=" + cropName + "): " + e.getMessage(), e);
		}

		return new ArrayList<StandardPreset>();
	}

	@Override
	public StandardPreset saveOrUpdateStandardPreset(final StandardPreset standardPreset) {
		try {
			return this.getStandardPresetDAO().saveOrUpdate(standardPreset);

		} catch (final HibernateException e) {
			this.logAndThrowException("Cannot perform: WorkbenchDataManager.saveOrUpdateStandardPreset(standardPreset="
					+ standardPreset.getName() + "): " + e.getMessage(), e);
		}

		return null;
	}

	@Override
	public void deleteStandardPreset(final int standardPresetId) {
		try {
			final StandardPreset preset = this.getStandardPresetDAO().getById(standardPresetId);
			this.getCurrentSession().delete(preset);
		} catch (final HibernateException e) {
			this.logAndThrowException("Cannot delete preset: WorkbenchDataManager.deleteStandardPreset(standardPresetId=" + standardPresetId
					+ "): " + e.getMessage(), e);
		}
	}

	@Override
	public void close() {
		if (this.sessionProvider != null) {
			this.sessionProvider.close();
		}
	}

	@Override
	public List<UserDto> getAllUsersSortedByLastName() throws MiddlewareQueryException {
		return this.getUserDao().getAllUsersSortedByLastName();

	}

	@Override
	public Integer createUser(final UserDto userDto) throws MiddlewareQueryException {

		Integer idUserSaved = null;
		// user.access = 0 - Default User
		// user.instalid = 0 - Access all areas (legacy from the ICIS system) (not used)
		// user.status = 0 - Unassigned
		// user.type = 0 - Default user type (not used)

		final Integer currentDate = Util.getCurrentDateAsIntegerValue();
		final Person person = this.setPerson(userDto, new Person());

		final User user = new User();
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

			final User recordSaved = this.getUserDao().saveOrUpdate(user);
			idUserSaved = recordSaved.getUserid();

		} catch (final Exception e) {

			this.logAndThrowException(
					"Error encountered while saving User: WorkbenchDataManager.addUser(user=" + user + "): " + e.getMessage(), e);
		}

		final UserInfo userInfo = new UserInfo();
		userInfo.setUserId(user.getUserid());
		userInfo.setLoginCount(0);
		this.getUserInfoDao().insertOrUpdateUserInfo(userInfo);

		return idUserSaved;

	}

	@Override
	public Integer updateUser(final UserDto userDto) throws MiddlewareQueryException {
		final Integer currentDate = Util.getCurrentDateAsIntegerValue();
		User user = null;
		Integer idUserSaved = null;

		try {
			user = this.getUserById(userDto.getUserId());
			this.setPerson(userDto, user.getPerson());

			user.setName(userDto.getUsername());
			user.setAssignDate(currentDate);
			user.setCloseDate(currentDate);
			user.setStatus(userDto.getStatus());

			// update user roles to the particular user
			final UserRole role = user.getRoles().get(0);
			if (!role.getRole().equals(userDto.getRole().toUpperCase())) {
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

	private Person setPerson(final UserDto userDto, final Person person) {

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
