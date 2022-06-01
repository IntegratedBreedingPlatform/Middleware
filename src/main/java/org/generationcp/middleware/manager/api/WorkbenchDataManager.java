/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 * <p/>
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.middleware.manager.api;

import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.RoleType;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.service.api.program.ProgramSearchRequest;
import org.generationcp.middleware.service.api.user.RoleSearchDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * This is the API used by the Workbench to retrieve Workbench project information.
 */
public interface WorkbenchDataManager {

	/**
	 * Gets the projects.
	 *
	 * @return the projects
	 */
	List<Project> getProjects();

	/**
	 * Gets the projects.
	 *
	 * @param pageable             - the starting record and number of page
	 * @param programSearchRequest - the filters that to be included in the query
	 * @return All projects based on the given start, numOfRows and filters Map
	 */
	List<Project> getProjects(final Pageable pageable, final ProgramSearchRequest programSearchRequest);

	/**
	 * Gets a project by Uuid. Should return only one value.
	 *
	 * @param projectUuid - the project Uuid to match
	 * @return the project matching the given Uuid
	 */
	Project getProjectByUuid(String projectUuid);

	/**
	 * Gets the list of Projects that the specified User is associated with.
	 *
	 * @param cropName - the Crop Name associated with the projects to be retrieved
	 * @return the projects which the specified user is involved
	 */
	List<Project> getProjectsByCropName(final String cropName);

	/**
	 * Get all tools.
	 *
	 * @return The list of all tools.
	 */
	List<Tool> getAllTools();

	/**
	 * Gets the tool with the given name.
	 *
	 * @param toolName - the tool name to match
	 * @return the tool with the given name
	 */
	Tool getToolWithName(String toolName);

	/**
	 * Gets a project by id.
	 *
	 * @param projectId - the project id to match
	 * @return the project matching the given id
	 */
	Project getProjectById(Long projectId);

	/**
	 * Gets a project by Uuid and CropType. Should return only one value.
	 *
	 * @param projectUuid - the project Uuid to match (uuid is unique per crop type)
	 * @param cropType    - the crop type to match
	 * @return the project matching the given Uuid and crop type
	 */
	Project getProjectByUuidAndCrop(String projectUuid, String cropType);

	/**
	 * Adds a project activity.
	 *
	 * @param projectActivity - the project activity
	 * @return Returns the id of the {@code ProjectActivity} record added
	 */
	Integer addProjectActivity(ProjectActivity projectActivity);

	/**
	 * Adds project activities.
	 *
	 * @param projectActivityList - the project activity list
	 * @return Returns the ids of the {@code ProjectActivity} records added
	 */
	List<Integer> addProjectActivity(List<ProjectActivity> projectActivityList);

	/**
	 * Get the list of all installed central crop databases.
	 *
	 * @return the installed central crops
	 */
	List<CropType> getInstalledCropDatabses();

	List<CropType> getAvailableCropsForUser(int workbenchUserId);

	/**
	 * Get the crop type corresponding to the given name.
	 *
	 * @param cropName - the crop name to match
	 * @return the CropType retrieved
	 */
	CropType getCropTypeByName(String cropName);

	/**
	 * Add a crop type to the database.
	 *
	 * @param cropType - the crop type to add
	 * @return Returns the id of the {@code CropType} record added
	 */
	String addCropType(CropType cropType);

	/**
	 * Returns the project last accessed regardless of user.
	 *
	 * @return the last Project opened by the given user
	 */

	Project getLastOpenedProjectAnyUser();

	/**
	 * Close the sessionProvider
	 */
	void close();

	/**
	 * Returns list of roles filtered according to roleSearchDto
	 *
	 * @return
	 */
	List<Role> getRoles(RoleSearchDto roleSearchDto);

	/**
	 * Returns list of roleTypes
	 *
	 * @return
	 */
	List<RoleType> getRoleTypes();

	// TODO Move role methods to RoleServiceImp

	/**
	 * Get role type
	 *
	 * @param id
	 * @return ROle Type
	 */
	RoleType getRoleType(Integer id);

	/**
	 * Save role
	 *
	 * @param role
	 * @return Role
	 */
	Role saveRole(Role role);

	/**
	 * Get role by name
	 *
	 * @param name
	 * @return Role
	 */
	Role getRoleByName(String name);

	/**
	 * Get role by id
	 *
	 * @param id
	 * @return Role
	 */
	Role getRoleById(Integer id);

}
