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

import org.generationcp.middleware.dao.ProjectActivityDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.RoleType;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.service.api.user.RoleSearchDto;
import org.hibernate.Session;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the WorkbenchDataManager interface. To instantiate this class, a Hibernate Session must be passed to its constructor.
 */
@Transactional
public class WorkbenchDataManagerImpl implements WorkbenchDataManager {

	private HibernateSessionProvider sessionProvider;

	private WorkbenchDaoFactory workbenchDaoFactory;

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
	public Tool getToolWithName(final String toolId) {
		return this.workbenchDaoFactory.getToolDAO().getByToolName(toolId);
	}

	@Override
	public Project getProjectById(final Long projectId) {
		return this.workbenchDaoFactory.getProjectDAO().getById(projectId);
	}

	@Override
	public Project getProjectByUuidAndCrop(final String projectUuid, final String cropType) {
		return this.workbenchDaoFactory.getProjectDAO().getByUuid(projectUuid, cropType);
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
	public Integer addProjectActivity(final ProjectActivity projectActivity) {
		final List<ProjectActivity> list = new ArrayList<>();
		list.add(projectActivity);

		final List<Integer> ids = this.addProjectActivity(list);

		return !ids.isEmpty() ? ids.get(0) : null;
	}

	private List<Integer> addProjectActivity(final List<ProjectActivity> projectActivityList) {

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
	public Project getLastOpenedProjectAnyUser() {
		return this.workbenchDaoFactory.getProjectDAO().getLastOpenedProjectAnyUser();
	}

	@Override
	public void close() {
		if (this.sessionProvider != null) {
			this.sessionProvider.close();
		}
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
}
