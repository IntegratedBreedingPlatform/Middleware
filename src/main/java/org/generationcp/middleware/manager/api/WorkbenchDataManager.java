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

import java.util.List;
import java.util.Map;

import org.generationcp.middleware.dao.ProjectUserInfoDAO;
import org.generationcp.middleware.dao.StandardPresetDAO;
import org.generationcp.middleware.dao.ToolDAO;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.presets.StandardPreset;
import org.generationcp.middleware.pojos.workbench.BrapiCall;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.IbdbUserMap;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolType;
import org.generationcp.middleware.pojos.workbench.UserInfo;
import org.generationcp.middleware.pojos.workbench.WorkbenchSidebarCategory;
import org.generationcp.middleware.pojos.workbench.WorkbenchSidebarCategoryLink;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.program.ProgramFilters;
import org.generationcp.middleware.service.api.user.UserDto;

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
	 * @param start - the starting record
	 * @param numOfRows - the number of rows to retrieve
	 * @return the projects
	 */
	List<Project> getProjects(int start, int numOfRows);

	/**
	 * Gets count projects.
	 *
	 * @param filters - the number of rows to retrieve
	 * @return the number of all the projects
	 */
	long countProjectsByFilter(final Map<ProgramFilters, Object> filters);

	/**
	 * Gets the projects.
	 *
	 * @param start - the starting record
	 * @param numOfRows - the number of rows to retrieve
	 * @param filters - the filters that to be included in the query
	 * @return All projects based on the given start, numOfRows and filters Map
	 */
	List<Project> getProjects(final int start, final int numOfRows, final Map<ProgramFilters, Object> filters);

	/**
	 * Gets a project by Uuid. Should return only one value.
	 *
	 * @param projectUuid - the project Uuid to match
	 * @return the project matching the given Uuid
	 */
	Project getProjectByUuid(String projectUuid);

	/**
	 * Gets the list of projects for specified crop.
	 *
	 * @param cropType - the crop for which its program will be retrieved
	 * @return the projects for given crop type
	 */
	List<Project> getProjectsByCrop(CropType cropType);

	/**
	 * Gets the list of Projects that the specified User is associated with.
	 *
	 * @param user - the User associated with the projects to be retrieved
	 * @return the projects which the specified user is involved
	 */
	List<Project> getProjectsByUser(WorkbenchUser user);

	/**
	 * Save or update project.
	 *
	 * @param project - the project to save
	 * @return the project saved
	 */
	Project saveOrUpdateProject(Project project);

	/**
	 * Save a project.
	 *
	 * @param project the project
	 * @return The Project added
	 */
	Project addProject(Project project);

	/**
	 * Update a project using Hibernate's Session.merge() method.
	 *
	 * @param project the project
	 * @return The merged Project.
	 */
	Project mergeProject(Project project);

	/**
	 * Delete project.
	 *
	 * @param project - the project to delete
	 */
	void deleteProject(Project project);

	/**
	 * Delete project dependencies.
	 *
	 * @param project - the project to delete dependencies
	 */
	void deleteProjectDependencies(Project project);

	/**
	 * Gets the tool DAO directly.
	 *
	 * @return the tool with the given name
	 */
	ToolDAO getToolDao();

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
	 * Get the list of tools with the specified type.
	 *
	 * @param toolType the tool type
	 * @return the list of matching tools
	 */
	List<Tool> getToolsWithType(ToolType toolType);

	/**
	 * Changes the password of the user.
	 *
	 * @param username - the username
	 * @param password - the new password
	 * @return true, if is user login is completed
	 */
	boolean changeUserPassword(String username, String password);

	/**
	 * Checks if is person exists.
	 *
	 * @param firstName - the first name
	 * @param lastName - the last name
	 * @return true, if is person exists
	 */
	boolean isPersonExists(String firstName, String lastName);

	/**
	 * Checks if person with specified email exists.
	 *
	 * @param email
	 * @return
	 */
	boolean isPersonWithEmailExists(String email);

	/**
	 *
	 * @param email
	 * @return
	 */
	Person getPersonByEmail(String email);

	Person getPersonByEmailAndName(String email, String firstName, String lastName);

	/**
	 * Checks if person with specified username AND email exists.
	 *
	 * @param username
	 * @param email
	 * @return
	 */
	boolean isPersonWithUsernameAndEmailExists(String username, String email);

	/**
	 * Checks if a username exists.
	 *
	 * @param userName - the user name to check
	 * @return true, if is username exists
	 */
	boolean isUsernameExists(String userName);

	/**
	 * Get the user info record for the specified user.
	 *
	 * @param userId the user id
	 * @return the user info
	 */
	UserInfo getUserInfo(int userId);

	/**
	 * Get the user info record given the username, not that the username must exist else we'll have null exceptions
	 *
	 * @param username
	 * @return
	 */
	UserInfo getUserInfoByUsername(String username);

	WorkbenchUser getUserByUsername(String userName);

	UserInfo getUserInfoByResetToken(String token);

	UserInfo updateUserInfo(UserInfo userInfo);

	/**
	 * Increments the log in count.
	 *
	 * @param userId the user id
	 */
	void incrementUserLogInCount(int userId);

	/**
	 * Insert or update the specified {@link WorkbenchUserInfo} record.
	 *
	 * @param userDetails the user details
	 */
	void insertOrUpdateUserInfo(UserInfo userDetails);

	/**
	 * Adds the person.
	 *
	 * @param person - the Person to add
	 * @return Returns the id of the {@code Person} record added
	 */
	Integer addPerson(Person person);

	/**
	 * Adds a user.
	 *
	 * @param user - the user to add
	 * @return Returns the id of the {@code User} record added
	 */
	Integer addUser(WorkbenchUser user);

	/**
	 * Gets a project by id.
	 *
	 * @param projectId - the project id to match
	 * @return the project matching the given id
	 */
	Project getProjectById(Long projectId);

	/**
	 * Gets a project by name. Should return only one value.
	 *
	 * @param projectName - the project name to match
	 * @param cropType - the crop type to search for a name (name is unique per crop type)
	 * @return the project matching the given name
	 */
	Project getProjectByNameAndCrop(String projectName, CropType cropType);

	/**
	 * Gets a project by Uuid and CropType. Should return only one value.
	 *
	 * @param projectUuid - the project Uuid to match (uuid is unique per crop type)
	 * @param cropType - the crop type to match
	 * @return the project matching the given Uuid and crop type
	 */
	Project getProjectByUuidAndCrop(String projectUuid, String cropType);

	/**
	 * Returns all the Workbench users.
	 *
	 * @return A {@code List} of all the {@code WorkbenchUser}s in the Workbench database.
	 */
	List<WorkbenchUser> getAllUsers();

	/**
	 * Returns all the Workbench users ordered by First Name then Last Name.
	 *
	 * @return A {@code List} of all the {@code WorkbenchUser}s in the Workbench database.
	 */
	List<WorkbenchUser> getAllActiveUsersSorted();

	/**
	 * Returns number of all Users.
	 *
	 * @return the number of all Users
	 */
	long countAllUsers();

	/**
	 * Gets the user by id.
	 *
	 * @param id - the user id to match
	 * @return the user matching the given id
	 */
	WorkbenchUser getUserById(int id);

	/**
	 * Gets the user by name.
	 *
	 * @param name - the name to match
	 * @param start - the starting record
	 * @param numOfRows - the number of rows to retrieve
	 * @param op the op
	 * @return the user by name
	 */
	List<WorkbenchUser> getUserByName(String name, int start, int numOfRows, Operation op);

	/**
	 * Deletes a user.
	 *
	 * @param user - the Workbench User to delete
	 */
	void deleteUser(WorkbenchUser user);

	/**
	 * Returns all Persons.
	 *
	 * @return all Persons
	 */
	List<Person> getAllPersons();

	/**
	 * Returns number of all Persons.
	 *
	 * @return the number of all Persons
	 */
	long countAllPersons();

	/**
	 * Gets the person by id.
	 *
	 * @param id - the id to match
	 * @return the person matching the given id
	 */
	Person getPersonById(int id);

	/**
	 * Deletes a person.
	 *
	 * @param person - the Person to delete
	 */
	void deletePerson(Person person);

	/**
	 * Returns the project last accessed by the user.
	 *
	 * @param userId - the user id to match
	 * @return the last Project opened by the given user
	 */
	Project getLastOpenedProject(Integer userId);

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
	 * Retrieves the user ids of the program members using the project id
	 * 
	 * @param projectId
	 * @return
	 */
	List<Integer> getActiveUserIDsByProjectId(final Long projectId);
	
	/**
	 * Return a List of {@link WorkbenchUser} records associated with a {@link Project}.
	 *
	 * @param projectId - the project id
	 * @return the List of {@link WorkbenchUser} records
	 */
	List<WorkbenchUser> getUsersByProjectId(Long projectId);
	
	/**
	 * Return a Map of {@link Person} records identified by {@link WorkbenchUser} ids associated with a {@link Project}.
	 *
	 * @param projectId - the project id
	 * @return the Maps of {@link Person} records identified by {@link WorkbenchUser} ids
	 */
	Map<Integer, Person> getPersonsByProjectId(final Long projectId);

	/**
	 * Get the list of all installed central crop databases.
	 *
	 * @return the installed central crops
	 */
	List<CropType> getInstalledCropDatabses();

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
	 * Return a List of {@link ProjectActivity} records associated with a {@link Project}.
	 *
	 * @param projectId - the project id
	 * @param start - the starting record
	 * @param numOfRows - the number of rows to retrieve
	 * @return the List of {@link ProjectActivity} records
	 */
	List<ProjectActivity> getProjectActivitiesByProjectId(Long projectId, int start, int numOfRows);

	/**
	 * Delete a project activity.
	 *
	 * @param projectActivity - the project activity to delete
	 */
	void deleteProjectActivity(ProjectActivity projectActivity);

	/**
	 * Returns the number of {@link ProjectActivity} records associated with a {@link Project}.
	 *
	 * @param projectId - the project id to match
	 * @return the number of {@link ProjectActivity} records associated to the given project
	 */
	long countProjectActivitiesByProjectId(Long projectId);

	/**
	 * Add a mapping between Workbench user record and the crop databse user record, if it does not already exist.
	 *
	 * @param userMap the user map
	 * @return Returns the id of the {@code IbdbUserMap} record added or that of the existing one if it is already there.
	 */
	Integer addIbdbUserMap(IbdbUserMap userMap);

	Integer getCurrentIbdbUserId(Long projectId, Integer workbenchUserId);

	/**
	 * Returns the IbdbUserMap object given a combination of a Workbench User ID and a Project ID.
	 *
	 * @param workbenchUserId - the specified Workbench User ID
	 * @param projectId - the specified Project ID
	 * @return Returns the IbdbUserMap object associated with the specified Workbench User ID and Project ID. Returns null when there is no
	 *         IbdbUserMap matching the specified Workbench User ID and Project ID.
	 */
	IbdbUserMap getIbdbUserMap(Integer workbenchUserId, Long projectId);

	/**
	 * Returns the Local IBDB User ID given a combination of a Workbench User ID and a Project ID.
	 *
	 * @param workbenchUserId - the specified Workbench User ID
	 * @param projectId - the specified Project ID
	 * @return Returns the IBDB User ID associated with the specified Workbench User ID and Project ID. Returns null when there is no IBDB
	 *         User ID matching the specified Workbench User ID and Project ID.
	 */
	Integer getLocalIbdbUserId(Integer workbenchUserId, Long projectId);

	/**
	 * Gets the ProjectUserInfoDAO.
	 *
	 * @return ProjectUserInfoDAO
	 */
	ProjectUserInfoDAO getProjectUserInfoDao();

	/**
	 * Saves or updates the ProjectUserInfo.
	 *
	 * @param projectUserInfo the project user info
	 * @return ProjectUserInfo
	 */
	ProjectUserInfo saveOrUpdateProjectUserInfo(ProjectUserInfo projectUserInfo);

	/**
	 * Gets the all workbench sidebar category.
	 *
	 * @return the all workbench sidebar category
	 */
	List<WorkbenchSidebarCategory> getAllWorkbenchSidebarCategory();

	/**
	 * Gets the all workbench sidebar links.
	 *
	 * @return the all workbench sidebar links
	 */
	List<WorkbenchSidebarCategoryLink> getAllWorkbenchSidebarLinks();

	/**
	 * Gets the all workbench sidebar links by category id.
	 *
	 * @param category the category
	 * @return the all workbench sidebar links by category id
	 */
	List<WorkbenchSidebarCategoryLink> getAllWorkbenchSidebarLinksByCategoryId(WorkbenchSidebarCategory category);

	/**
	 * Returns the project last accessed regardless of user.
	 *
	 * @return the last Project opened by the given user
	 */

	Project getLastOpenedProjectAnyUser();

	/**
	 * Detects whether the selected project in Workbench has changed
	 *
	 * @return True if the project has changed, otherwise false
	 */
	Boolean isLastOpenedProjectChanged();

	/**
	 * Retrive all standard presets with specific crop + tool
	 *
	 * @param cropName
	 * @param toolId
	 * @return
	 */
	List<StandardPreset> getStandardPresetFromCropAndTool(String cropName, int toolId);

	/**
	 * Returns the DAO object for standard preset
	 *
	 * @return StandardPresetDAO
	 */
	StandardPresetDAO getStandardPresetDAO();

	List<StandardPreset> getStandardPresetFromCropAndTool(String cropName, int toolId, String toolSection);

	List<StandardPreset> getStandardPresetFromCropAndToolByName(String presetName, String cropName, int toolId, String toolSection);

	/**
	 * save or update a standard preset
	 *
	 * @param standardPreset
	 * @return
	 */
	StandardPreset saveOrUpdateStandardPreset(StandardPreset standardPreset);

	/**
	 * delete a standard preset by id
	 *
	 * @param standardPresetId
	 */
	void deleteStandardPreset(int standardPresetId);

	/**
	 * Close the sessionProvider
	 */
	void close();

	/**
	 * Returns the correspoding workbench user id.
	 *
	 * @param ibdbUserId the ibdb user id
	 * @param projectId - the specified Project ID
	 * @return Returns the IBDB User ID associated with the specified Workbench User ID and Project ID. Returns null when there is no IBDB
	 *         User ID matching the specified Workbench User ID and Project ID.
	 */
	Integer getWorkbenchUserIdByIBDBUserIdAndProjectId(Integer ibdbUserId, Long projectId);

	/**
	 * Gets the all Users Sorted
	 *
	 * @return
	 */
	List<UserDto> getAllUsersSortedByLastName();

	/**
	 * create the user.
	 *
	 * @param user the user
	 * @return Returns the id of the {@code UserDto} record added
	 */
	public Integer createUser(UserDto userDto);

	/**
	 * Updates the user.
	 *
	 * @param user the user to update
	 * @return Returns the id of the {@code UserDto} record added
	 */
	public Integer updateUser(UserDto userDto);

	/**
	 * Updates the user.
	 *
	 * @param user the user to update
	 * @return Returns the id of the {@code User} record updated
	 */
	public void updateUser(WorkbenchUser user);

	/**
	 * Gets the user by project_uuid.
	 *
	 * @param projectUuid
	 * @return the user matching the given project_uuid
	 */
	List<UserDto> getUsersByProjectUuid(final String projectUuid);
	
	/**
	 * Returns list of roles that can be assigned to a new user
	 * 
	 * @return
	 */
	List<Role> getAssignableRoles();
	
	/**
	 * Returns list of roles
	 * 
	 * @return
	 */
	List<Role> getAllRoles();
	
	/**
	 * Return users with SUPERADMIN role
	 * @return
	 */
	List<WorkbenchUser> getSuperAdminUsers();
	
	/**
	 * Returns true if user has SUPERADMIN role assigned
	 * @param userId
	 * @return
	 */
	boolean isSuperAdminUser(Integer userId);

	List<BrapiCall> getBrapiCalls(String dataType, Integer pageSize, Integer pageNumber);
}
