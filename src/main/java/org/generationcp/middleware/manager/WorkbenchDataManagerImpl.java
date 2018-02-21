/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 * <p/>
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
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
import org.generationcp.middleware.dao.StandardPresetDAO;
import org.generationcp.middleware.dao.ToolDAO;
import org.generationcp.middleware.dao.UserDAO;
import org.generationcp.middleware.dao.UserInfoDAO;
import org.generationcp.middleware.dao.WorkbenchSidebarCategoryDAO;
import org.generationcp.middleware.dao.WorkbenchSidebarCategoryLinkDAO;
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
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolType;
import org.generationcp.middleware.pojos.workbench.UserInfo;
import org.generationcp.middleware.pojos.workbench.UserRole;
import org.generationcp.middleware.pojos.workbench.WorkbenchSidebarCategory;
import org.generationcp.middleware.pojos.workbench.WorkbenchSidebarCategoryLink;
import org.generationcp.middleware.service.api.program.ProgramFilters;
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

	private ProjectDAO getProjectDao() {

		final ProjectDAO projectDao = new ProjectDAO();
		projectDao.setSession(this.getCurrentSession());
		return projectDao;
	}

	@Override
	public ProjectUserInfoDAO getProjectUserInfoDao() {

		final ProjectUserInfoDAO projectUserInfoDao = new ProjectUserInfoDAO();
		projectUserInfoDao.setSession(this.getCurrentSession());
		return projectUserInfoDao;
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

	@Override
	public StandardPresetDAO getStandardPresetDAO() {
		final StandardPresetDAO standardPresetDAO = new StandardPresetDAO();
		standardPresetDAO.setSession(this.getCurrentSession());
		return standardPresetDAO;
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
	public List<Project> getProjects(final int start, final int numOfRows, final Map<ProgramFilters, Object> filters) {
		return this.getProjectDao().getProjectsByFilter(start, numOfRows, filters);
	}

	@Override
	public long countProjectsByFilter(final Map<ProgramFilters, Object> filters) {
		return this.getProjectDao().countProjectsByFilter(filters);
	}

	@Override
	public List<Project> getProjectsByCrop(final CropType cropType) {
		return this.getProjectDao().getProjectsByCrop(cropType);
	}

	@Override
	public List<Project> getProjectsByUser(final User user) {
		return this.getProjectUserInfoDao().getProjectsByUser(user);
	}

	@Override
	public Project saveOrUpdateProject(final Project project) {

		try {
			this.getProjectDao().merge(project);
		} catch (final Exception e) {
			throw new MiddlewareQueryException(
					"Cannot save Project: WorkbenchDataManager.saveOrUpdateProject(project=" + project + "): " + e.getMessage(), e);
		}

		return project;
	}

	@Override
	public ProjectUserInfo saveOrUpdateProjectUserInfo(final ProjectUserInfo projectUserInfo) {

		try {
			this.getProjectUserInfoDao().merge(projectUserInfo);
		} catch (final Exception e) {
			throw new MiddlewareQueryException(
					"Cannot save ProjectUserInfo: WorkbenchDataManager.saveOrUpdateProjectUserInfo(project=" + projectUserInfo + "): " + e
							.getMessage(), e);
		}

		return projectUserInfo;
	}

	@Override
	public Project addProject(final Project project) {

		try {
			project.setUniqueID(UUID.randomUUID().toString());
			this.getProjectDao().save(project);
		} catch (final Exception e) {
			throw new MiddlewareQueryException(
					"Cannot save Project: WorkbenchDataManager.addProject(project=" + project + "): " + e.getMessage(), e);
		}
		return project;
	}

	@Override
	public Project mergeProject(final Project project) {
		try {
			this.getProjectDao().merge(project);
		} catch (final Exception e) {
			throw new MiddlewareQueryException(
					"Cannot save Project: WorkbenchDataManager.updateProject(project=" + project + "): " + e.getMessage(), e);
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

			final List<ProjectUserInfo> projectUserInfos = this.getByProjectId(projectId);
			for (final ProjectUserInfo projectUserInfo : projectUserInfos) {
				this.deleteProjectUserInfoDao(projectUserInfo);
			}

		} catch (final Exception e) {
			throw new MiddlewareQueryException(
					"Cannot delete Project Dependencies: WorkbenchDataManager.deleteProjectDependencies(project=" + project + "): " + e
							.getMessage(), e);
		}
	}

	public List<IbdbUserMap> getIbdbUserMapsByProjectId(final Long projectId) {
		return this.getIbdbUserMapDao().getIbdbUserMapByID(projectId);
	}

	public void deleteProjectUserInfoDao(final ProjectUserInfo projectUserInfo) {

		try {

			this.getProjectUserInfoDao().makeTransient(projectUserInfo);

		} catch (final Exception e) {

			throw new MiddlewareQueryException(
					"Cannot delete ProjectUserInfo: WorkbenchDataManager.deleteProjectUserInfoDao(projectUserInfo=" + projectUserInfo
							+ "): " + e.getMessage(), e);
		}
	}

	@Override
	public void deleteProject(final Project project) {

		try {

			this.getProjectDao().deleteProject(project.getProjectName());

		} catch (final Exception e) {

			throw new MiddlewareQueryException(
					"Cannot delete Project: WorkbenchDataManager.deleteProject(project=" + project + "): " + e.getMessage(), e);
		}
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

			throw new MiddlewareQueryException(
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

			throw new MiddlewareQueryException(
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
	public List<User> getAllUsers() {
		return this.getUserDao().getAll();
	}

	@Override
	public List<User> getAllActiveUsersSorted() {
		return this.getUserDao().getAllActiveUsersSorted();
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
		List<User> users = new ArrayList<>();
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

			throw new MiddlewareQueryException(
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

			throw new MiddlewareQueryException(
					"Error encountered while deleting Person: WorkbenchDataManager.deletePerson(person=" + person + "): " + e.getMessage(),
					e);
		}
	}

	@Override
	public Project getLastOpenedProject(final Integer userId) {
		return this.getProjectDao().getLastOpenedProject(userId);
	}

	@Override
	public List<User> getUsersByProjectId(final Long projectId) {
		return this.getProjectUserInfoDao().getUsersByProjectId(projectId);
	}
	
	@Override
	public Map<Integer, Person> getPersonsByProjectId(final Long projectId) {
		return this.getProjectUserInfoDao().getPersonsByProjectId(projectId);
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
			throw new MiddlewareQueryException("Crop type already exists.");
		}

		String idSaved = null;
		try {

			final CropType recordSaved = dao.saveOrUpdate(cropType);
			idSaved = recordSaved.getCropName();

		} catch (final Exception e) {

			throw new MiddlewareQueryException(
					"Error encountered while adding crop type: WorkbenchDataManager.addCropType(cropType=" + cropType + "): " + e
							.getMessage(), e);
		}

		return idSaved;
	}

	public List<ProjectUserInfo> getByProjectId(final Long projectId) {
		return this.getProjectUserInfoDao().getByProjectId(projectId);

	}

	@Override
	public Integer addProjectActivity(final ProjectActivity projectActivity) {
		final List<ProjectActivity> list = new ArrayList<>();
		list.add(projectActivity);

		final List<Integer> ids = this.addProjectActivity(list);

		return !ids.isEmpty() ? ids.get(0) : null;
	}

	@Override
	public List<Integer> addProjectActivity(final List<ProjectActivity> projectActivityList) {

		return this.addOrUpdateProjectActivityData(projectActivityList, Operation.ADD);
	}

	private List<Integer> addOrUpdateProjectActivityData(final List<ProjectActivity> projectActivityList, final Operation operation) {

		final List<Integer> idsSaved = new ArrayList<>();
		try {

			final ProjectActivityDAO dao = this.getProjectActivityDao();

			for (final ProjectActivity projectActivityListData : projectActivityList) {
				final ProjectActivity recordSaved = dao.save(projectActivityListData);
				idsSaved.add(recordSaved.getProjectActivityId());
			}

		} catch (final Exception e) {

			throw new MiddlewareQueryException(
					"Error encountered while adding addProjectActivity: WorkbenchDataManager.addOrUpdateProjectActivityData(projectActivityList="
							+ projectActivityList + ", operation=" + operation + "): " + e.getMessage(), e);
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

			throw new MiddlewareQueryException(
					"Error encountered while deleting ProjectActivity: WorkbenchDataManager.deleteProjectActivity(projectActivity="
							+ projectActivity + "): " + e.getMessage(), e);
		}
	}

	@Override
	public long countProjectActivitiesByProjectId(final Long projectId) {
		return this.getProjectActivityDao().countByProjectId(projectId);
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

			throw new MiddlewareQueryException(
					"Error encountered while retrieving Local IbdbUserMap: WorkbenchDataManager.getIbdbUserMap(workbenchUserId="
							+ workbenchUserId + ", projectId=" + projectId + "): " + e.getMessage(), e);
		}

		return bbdbUserMap;
	}

	@Override
	public Integer getLocalIbdbUserId(final Integer workbenchUserId, final Long projectId) {

		Integer ibdbUserId = null;
		try {

			ibdbUserId = this.getIbdbUserMapDao().getLocalIbdbUserId(workbenchUserId, projectId);

		} catch (final Exception e) {

			throw new MiddlewareQueryException(
					"Error encountered while retrieving Local IBDB user id: WorkbenchDataManager.getLocalIbdbUserId(workbenchUserId="
							+ workbenchUserId + ", projectId=" + projectId + "): " + e.getMessage(), e);
		}

		return ibdbUserId;
	}

	@Override
	public Integer getWorkbenchUserIdByIBDBUserIdAndProjectId(final Integer ibdbUserId, final Long projectId) {
		return this.getIbdbUserMapDao().getWorkbenchUserId(ibdbUserId, projectId);
	}

	@Override
	public UserInfo getUserInfo(final int userId) {
		try {
			return this.getUserInfoDao().getUserInfoByUserId(userId);
		} catch (final Exception e) {
			throw new MiddlewareQueryException("Cannot increment login count for user_id =" + userId + "): " + e.getMessage(), e);
		}

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

			throw new MiddlewareQueryException("Cannot update userInfo =" + userInfo.getUserId() + "): " + e.getMessage(), e);

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

			throw new MiddlewareQueryException("Cannot increment login count for user_id =" + userId + "): " + e.getMessage(), e);
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
	public List<WorkbenchSidebarCategoryLink> getAllWorkbenchSidebarLinksByCategoryId(final WorkbenchSidebarCategory category) {
		return this.getWorkbenchSidebarCategoryLinkDao().getAllWorkbenchSidebarLinksByCategoryId(category, 0, Integer.MAX_VALUE);
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
			throw new MiddlewareQueryException(
					"error in: WorkbenchDataManager.getStandardPresetFromCropAndTool(cropName=" + cropName + "): " + e.getMessage(), e);
		}
	}

	@Override
	public List<StandardPreset> getStandardPresetFromCropAndTool(final String cropName, final int toolId, final String toolSection) {

		try {
			final Criteria criteria = this.getCurrentSession().createCriteria(StandardPreset.class);

			criteria.add(Restrictions.eq("cropName", cropName));
			criteria.add(Restrictions.eq("toolId", toolId));
			criteria.add(Restrictions.eq("toolSection", toolSection));

			return criteria.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
					"error in: WorkbenchDataManager.getStandardPresetFromCropAndTool(cropName=" + cropName + "): " + e.getMessage(), e);
		}
	}

	@Override
	public List<StandardPreset> getStandardPresetFromCropAndToolByName(final String presetName, final String cropName, final int toolId,
			final String toolSection) {

		try {
			final Criteria criteria = this.getCurrentSession().createCriteria(StandardPreset.class);

			criteria.add(Restrictions.eq("cropName", cropName));
			criteria.add(Restrictions.eq("toolId", toolId));
			criteria.add(Restrictions.eq("toolSection", toolSection));
			criteria.add(Restrictions.like("name", presetName));

			return criteria.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
					"error in: WorkbenchDataManager.getStandardPresetFromCropAndToolByName(cropName=" + cropName + "): " + e.getMessage(),
					e);
		}
	}

	@Override
	public StandardPreset saveOrUpdateStandardPreset(final StandardPreset standardPreset) {
		try {
			return this.getStandardPresetDAO().saveOrUpdate(standardPreset);

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
					"Cannot perform: WorkbenchDataManager.saveOrUpdateStandardPreset(standardPreset=" + standardPreset.getName() + "): " + e
							.getMessage(), e);
		}
	}

	@Override
	public void deleteStandardPreset(final int standardPresetId) {
		try {
			final StandardPreset preset = this.getStandardPresetDAO().getById(standardPresetId);
			this.getCurrentSession().delete(preset);
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
					"Cannot delete preset: WorkbenchDataManager.deleteStandardPreset(standardPresetId=" + standardPresetId + "): " + e
							.getMessage(), e);
		}
	}

	@Override
	public void close() {
		if (this.sessionProvider != null) {
			this.sessionProvider.close();
		}
	}

	@Override
	public List<UserDto> getAllUsersSortedByLastName() {
		return this.getUserDao().getAllUsersSortedByLastName();

	}

	@Override
	public List<UserDto> getUsersByProjectUuid(final String projectUuid) {
		return this.getUserDao().getUsersByProjectUUId(projectUuid);

	}

	@Override
	public Integer createUser(final UserDto userDto) {

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

			throw new MiddlewareQueryException(
					"Error encountered while saving User: WorkbenchDataManager.addUser(user=" + user + "): " + e.getMessage(), e);
		}

		final UserInfo userInfo = new UserInfo();
		userInfo.setUserId(user.getUserid());
		userInfo.setLoginCount(0);
		this.getUserInfoDao().insertOrUpdateUserInfo(userInfo);

		return idUserSaved;

	}

	@Override
	public Integer updateUser(final UserDto userDto) {
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
			if (!role.getRole().equalsIgnoreCase(userDto.getRole())) {
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

	@Override
	public void updateUser(final User user) {
		this.getUserDao().saveOrUpdate(user);
		this.getPersonDao().saveOrUpdate(user.getPerson());
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

	@Override
	public Project getProjectByUuid(final String projectUuid) {
		return this.getProjectDao().getByUuid(projectUuid);
	}

	@Override
	public List<Integer> getActiveUserIDsByProjectId(Long projectId) {
		return this.getProjectUserInfoDao().getActiveUserIDsByProjectId(projectId);
	}

}
