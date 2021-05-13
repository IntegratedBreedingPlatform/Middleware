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

import org.generationcp.middleware.dao.CropTypeDAO;
import org.generationcp.middleware.dao.ProjectActivityDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.presets.StandardPreset;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.RoleType;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolType;
import org.generationcp.middleware.pojos.workbench.UserRole;
import org.generationcp.middleware.pojos.workbench.WorkbenchSidebarCategory;
import org.generationcp.middleware.pojos.workbench.WorkbenchSidebarCategoryLink;
import org.generationcp.middleware.service.api.program.ProgramSearchRequest;
import org.generationcp.middleware.service.api.user.RoleSearchDto;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of the WorkbenchDataManager interface. To instantiate this class, a Hibernate Session must be passed to its constructor.
 */
@Transactional
public class WorkbenchDataManagerImpl implements WorkbenchDataManager {

	private static final Logger LOG = LoggerFactory.getLogger(WorkbenchDataManagerImpl.class);

	private HibernateSessionProvider sessionProvider;

	private WorkbenchDaoFactory workbenchDaoFactory;

	private Project currentlastOpenedProject;

	public WorkbenchDataManagerImpl() {
		super();
	}

	public WorkbenchDataManagerImpl(final HibernateSessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
		this.workbenchDaoFactory = new WorkbenchDaoFactory(sessionProvider);
	}

	public Session getCurrentSession() {
		return this.sessionProvider.getSession();
	}

	@Override
	public List<Project> getProjects() {
		return this.workbenchDaoFactory.getProjectDAO().getAll();
	}

	@Override
	public List<Project> getProjects(final int start, final int numOfRows) {
		return this.workbenchDaoFactory.getProjectDAO().getAll(start, numOfRows);
	}

	@Override
	public List<Project> getProjects(final Pageable pageable, final ProgramSearchRequest programSearchRequest) {
		return this.workbenchDaoFactory.getProjectDAO().getProjectsByFilter(pageable, programSearchRequest);
	}

	@Override
	public long countProjectsByFilter(final ProgramSearchRequest programSearchRequest) {
		return this.workbenchDaoFactory.getProjectDAO().countProjectsByFilter(programSearchRequest);
	}

	@Override
	public List<Project> getProjectsByCrop(final CropType cropType) {
		return this.workbenchDaoFactory.getProjectDAO().getProjectsByCrop(cropType);
	}

	@Override
	public List<Project> getProjectsByCropName(final String cropName) {
		return this.workbenchDaoFactory.getProjectDAO().getProjectsByCropName(cropName);
	}

	@Override
	public Project saveOrUpdateProject(final Project project) {

		try {
			this.workbenchDaoFactory.getProjectDAO().merge(project);
		} catch (final Exception e) {
			throw new MiddlewareQueryException(
				"Cannot save Project: WorkbenchDataManager.saveOrUpdateProject(project=" + project + "): " + e.getMessage(), e);
		}

		return project;
	}

	@Override
	public Project addProject(final Project project) {

		try {
			project.setUniqueID(UUID.randomUUID().toString());
			this.workbenchDaoFactory.getProjectDAO().save(project);
		} catch (final Exception e) {
			throw new MiddlewareQueryException(
				"Cannot save Project: WorkbenchDataManager.addProject(project=" + project + "): " + e.getMessage(), e);
		}
		return project;
	}

	@Override
	public Project mergeProject(final Project project) {
		try {
			this.workbenchDaoFactory.getProjectDAO().merge(project);
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
				this.deleteProjectUserInfo(projectUserInfo);
			}

			final List<UserRole> userRolesPerProgram = this.workbenchDaoFactory.getUserRoleDao().getByProgramId(projectId);
			for (final UserRole userRole : userRolesPerProgram) {
				userRole.getUser().getRoles().remove(userRole);
				this.workbenchDaoFactory.getUserRoleDao().delete(userRole);
			}

		} catch (final Exception e) {
			throw new MiddlewareQueryException(
				"Cannot delete Project Dependencies: WorkbenchDataManager.deleteProjectDependencies(project=" + project + "): " + e
					.getMessage(), e);
		}
	}

	public void deleteProjectUserInfo(final ProjectUserInfo projectUserInfo) {

		try {

			this.workbenchDaoFactory.getProjectUserInfoDAO().makeTransient(projectUserInfo);

		} catch (final Exception e) {

			throw new MiddlewareQueryException(
				"Cannot delete ProjectUserInfo: WorkbenchDataManager.deleteProjectUserInfoDao(projectUserInfo=" + projectUserInfo
					+ "): " + e.getMessage(), e);
		}
	}

	@Override
	public void deleteProject(final Project project) {

		try {

			this.workbenchDaoFactory.getProjectDAO().deleteProject(project.getProjectName());

		} catch (final Exception e) {

			throw new MiddlewareQueryException(
				"Cannot delete Project: WorkbenchDataManager.deleteProject(project=" + project + "): " + e.getMessage(), e);
		}
	}

	@Override
	public List<Tool> getAllTools() {
		return this.workbenchDaoFactory.getToolDAO().getAll();
	}

	@Override
	public Tool getToolWithName(final String toolId) {
		return this.workbenchDaoFactory.getToolDAO().getByToolName(toolId);
	}

	@Override
	public List<Tool> getToolsWithType(final ToolType toolType) {
		return this.workbenchDaoFactory.getToolDAO().getToolsByToolType(toolType);
	}

	@Override
	public Project getProjectById(final Long projectId) {
		return this.workbenchDaoFactory.getProjectDAO().getById(projectId);
	}

	@Override
	public Project getProjectByNameAndCrop(final String projectName, final CropType cropType) {
		return this.workbenchDaoFactory.getProjectDAO().getProjectByNameAndCrop(projectName, cropType);
	}

	@Override
	public Project getProjectByUuidAndCrop(final String projectUuid, final String cropType) {
		return this.workbenchDaoFactory.getProjectDAO().getByUuid(projectUuid, cropType);
	}

	@Override
	public Project getLastOpenedProject(final Integer userId) {
		return this.workbenchDaoFactory.getProjectDAO().getLastOpenedProject(userId);
	}

	@Override
	public List<CropType> getInstalledCropDatabses() {
		return this.workbenchDaoFactory.getCropTypeDAO().getAll();
	}

	@Override
	public List<CropType> getAvailableCropsForUser(final int workbenchUserId) {
		return this.workbenchDaoFactory.getCropTypeDAO().getAvailableCropsForUser(workbenchUserId);
	}

	@Override
	public CropType getCropTypeByName(final String cropName) {
		return this.workbenchDaoFactory.getCropTypeDAO().getByName(cropName);
	}

	@Override
	public String addCropType(final CropType cropType) {

		final CropTypeDAO dao = this.workbenchDaoFactory.getCropTypeDAO();
		if (this.workbenchDaoFactory.getCropTypeDAO().getByName(cropType.getCropName()) != null) {
			throw new MiddlewareQueryException("Crop type already exists.");
		}

		final String idSaved;
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
		return this.workbenchDaoFactory.getProjectUserInfoDAO().getByProjectId(projectId);

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

			final ProjectActivityDAO dao = this.workbenchDaoFactory.getProjectActivityDAO();

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
		return this.workbenchDaoFactory.getProjectActivityDAO().getByProjectId(projectId, start, numOfRows);
	}

	@Override
	public void deleteProjectActivity(final ProjectActivity projectActivity) {

		try {

			this.workbenchDaoFactory.getProjectActivityDAO().makeTransient(projectActivity);

		} catch (final Exception e) {

			throw new MiddlewareQueryException(
				"Error encountered while deleting ProjectActivity: WorkbenchDataManager.deleteProjectActivity(projectActivity="
					+ projectActivity + "): " + e.getMessage(), e);
		}
	}

	@Override
	public long countProjectActivitiesByProjectId(final Long projectId) {
		return this.workbenchDaoFactory.getProjectActivityDAO().countByProjectId(projectId);
	}

	@Override
	public List<WorkbenchSidebarCategory> getAllWorkbenchSidebarCategory() {
		return this.workbenchDaoFactory.getWorkbenchSidebarCategoryDAO().getAll();
	}

	@Override
	public List<WorkbenchSidebarCategoryLink> getAllWorkbenchSidebarLinks() {
		return this.workbenchDaoFactory.getWorkbenchSidebarCategoryLinkDAO().getAll();
	}

	@Override
	public List<WorkbenchSidebarCategoryLink> getAllWorkbenchSidebarLinksByCategoryId(final WorkbenchSidebarCategory category) {
		return this.workbenchDaoFactory.getWorkbenchSidebarCategoryLinkDAO()
			.getAllWorkbenchSidebarLinksByCategoryId(category, 0, Integer.MAX_VALUE);
	}

	@Override
	public Project getLastOpenedProjectAnyUser() {
		return this.workbenchDaoFactory.getProjectDAO().getLastOpenedProjectAnyUser();
	}

	@Override
	public Boolean isLastOpenedProjectChanged() {

		final Project project = this.workbenchDaoFactory.getProjectDAO().getLastOpenedProjectAnyUser();

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
	public List<StandardPreset> getStandardPresetFromCropAndToolByName(
		final String presetName, final String cropName, final int toolId,
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
			return this.workbenchDaoFactory.getStandardPresetDAO().saveOrUpdate(standardPreset);

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				"Cannot perform: WorkbenchDataManager.saveOrUpdateStandardPreset(standardPreset=" + standardPreset.getName() + "): " + e
					.getMessage(), e);
		}
	}

	@Override
	public void deleteStandardPreset(final int standardPresetId) {
		try {
			final StandardPreset preset = this.workbenchDaoFactory.getStandardPresetDAO().getById(standardPresetId);
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
	public StandardPreset getStandardPresetById(final Integer presetId) {
		return this.workbenchDaoFactory.getStandardPresetDAO().getById(presetId);
	}

	@Override
	public Project getProjectByUuid(final String projectUuid) {
		return this.workbenchDaoFactory.getProjectDAO().getByUuid(projectUuid);
	}

	@Override
	public List<Role> getRoles(final RoleSearchDto roleSearchDto) {
		return this.workbenchDaoFactory.getRoleDao().getRoles(roleSearchDto);
	}

	@Override
	public List<RoleType> getRoleTypes() {
		return this.workbenchDaoFactory.getRoleTypeDAO().getRoleTypes();
	}

	@Override
	public RoleType getRoleType(final Integer id) {
		return this.workbenchDaoFactory.getRoleTypeDAO().getById(id);
	}

	@Override
	public WorkbenchSidebarCategoryLink getWorkbenchSidebarLinksByCategoryId(final Integer workbenchSidebarCategoryLink) {
		return this.workbenchDaoFactory.getWorkbenchSidebarCategoryLinkDao().getById(workbenchSidebarCategoryLink);
	}

	@Override
	public void saveOrUpdateUserRole(final UserRole userRole) {
		this.workbenchDaoFactory.getUserRoleDao().saveOrUpdate(userRole);
	}

	@Override
	public Role saveRole(final Role role) {

		try {
			this.workbenchDaoFactory.getRoleDao().saveOrUpdate(role);
		} catch (final Exception e) {
			throw new MiddlewareQueryException(
				"Cannot save Role: WorkbenchDataManager.saveRole(role=" + role + "): " + e.getMessage(), e);
		}

		return role;
	}

	@Override
	public Role getRoleByName(final String name) {
		final RoleSearchDto roleSearchDto = new RoleSearchDto();
		roleSearchDto.setName(name);
		final List<Role> roles = this.workbenchDaoFactory.getRoleDao().getRoles(roleSearchDto);
		return roles.isEmpty() ? null : roles.get(0);
	}

	@Override
	public Role getRoleById(final Integer id) {
		return this.workbenchDaoFactory.getRoleDao().getRoleById(id);
	}

	@Override
	public List<CropType> getCropsWithAddProgramPermission(final int workbenchUserId) {

		final boolean hasInstanceRoleWithAddProgramPermission = this.workbenchDaoFactory.getUserRoleDao().hasInstanceRoleWithAddProgramPermission(workbenchUserId);
		if (hasInstanceRoleWithAddProgramPermission) {
			return this.getAvailableCropsForUser(workbenchUserId);
		} else {
			return new ArrayList<>(this.workbenchDaoFactory.getUserRoleDao().getCropsWithAddProgramPermissionForCropRoles(workbenchUserId));
		}
	}
}
