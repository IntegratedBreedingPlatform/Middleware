/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.middleware.manager.api;

import java.util.List;

import org.generationcp.middleware.dao.ProjectUserInfoDAO;
import org.generationcp.middleware.dao.ToolDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.*;

// TODO: Auto-generated Javadoc
/**
 * This is the API used by the Workbench to retrieve Workbench project
 * information.
 * 
 */
public interface WorkbenchDataManager {

    /**
     * Gets the projects.
     *
     * @return the projects
     * @throws MiddlewareQueryException the middleware query exception
     */
    public List<Project> getProjects()  throws MiddlewareQueryException;

    /**
     * Gets the projects.
     *
     * @param start - the starting record
     * @param numOfRows - the number of rows to retrieve
     * @return the projects
     * @throws MiddlewareQueryException the middleware query exception
     */
    public List<Project> getProjects(int start, int numOfRows)  throws MiddlewareQueryException;
    
    /**
     * Gets the list of Projects that the specified User is associated with.
     *
     * @param user - the User associated with the projects to be retrieved
     * @return the projects which the specified user is involved
     * @throws MiddlewareQueryException the middleware query exception
     */
    public List<Project> getProjectsByUser(User user)  throws MiddlewareQueryException;

    /**
     * Save or update project.
     *
     * @param project - the project to save
     * @return the project saved
     * @throws MiddlewareQueryException the middleware query exception
     */
    public Project saveOrUpdateProject(Project project) throws MiddlewareQueryException;

    /**
     * Save a project.
     *
     * @param project the project
     * @return The Project added
     * @throws MiddlewareQueryException the middleware query exception
     */
    public Project addProject(Project project) throws MiddlewareQueryException;
    
    /**
     * Update a project using Hibernate's Session.merge() method.
     *
     * @param project the project
     * @return The merged Project.
     * @throws MiddlewareQueryException the middleware query exception
     */
    public Project mergeProject(Project project) throws MiddlewareQueryException;
    
    /**
     * Delete project.
     *
     * @param project - the project to delete
     * @throws MiddlewareQueryException the middleware query exception
     */
    public void deleteProject(Project project) throws MiddlewareQueryException;

    /**
     * drop the projects local database.
     * 
     * Drops the database of the given project.
     *
     * @param project - the project to delete the database
     * @throws MiddlewareQueryException the middleware query exception
     */
    public void dropLocalDatabase(Project project) throws MiddlewareQueryException;
    
    /**
     * Delete project dependencies.
     *
     * @param project - the project to delete dependencies
     * @throws MiddlewareQueryException the middleware query exception
     */
    public void deleteProjectDependencies(Project project) throws MiddlewareQueryException;
    
    /**
     * Gets the workflow templates.
     *
     * @return the workflow templates
     * @throws MiddlewareQueryException the middleware query exception
     */
    public List<WorkflowTemplate> getWorkflowTemplates() throws MiddlewareQueryException;

    /**
     * Gets the workflow templates.
     *
     * @param start - the starting record
     * @param numOfRows - the number of rows to retrieve
     * @return the workflow templates
     * @throws MiddlewareQueryException the middleware query exception
     */
    public List<WorkflowTemplate> getWorkflowTemplates(int start, int numOfRows) throws MiddlewareQueryException;

    /**
     * Gets the workflow templates based on the given name.
     *
     * @param name - the name of the workflow template
     * @return the workflow templates
     * @throws MiddlewareQueryException the middleware query exception
     */
    public List<WorkflowTemplate> getWorkflowTemplateByName(String name) throws MiddlewareQueryException;

    
    /**
     * Gets the tool DAO directly.
     *
     * @return the tool with the given name
     */
    public ToolDAO getToolDao();

    
    /**
     * Get all tools.
     *
     * @return The list of all tools.
     * @throws MiddlewareQueryException the middleware query exception
     */
    public List<Tool> getAllTools() throws MiddlewareQueryException;

    /**
     * Gets the tool with the given name.
     *
     * @param toolName - the tool name to match
     * @return the tool with the given name
     * @throws MiddlewareQueryException the middleware query exception
     */
    public Tool getToolWithName(String toolName) throws MiddlewareQueryException;
    
    /**
     * Get the list of tools with the specified type.
     *
     * @param toolType the tool type
     * @return the list of matching tools
     * @throws MiddlewareQueryException the middleware query exception
     */
    public List<Tool> getToolsWithType(ToolType toolType) throws MiddlewareQueryException;
    
    /**
     * Checks if is valid user login.
     *
     * @param username - the username
     * @param password - the password
     * @return true, if is valid user login
     * @throws MiddlewareQueryException the middleware query exception
     */
    public boolean isValidUserLogin(String username, String password) throws MiddlewareQueryException;
    
    /**
     * Changes the password of the user.
     *
     * @param username - the username
     * @param password - the new password
     * @return true, if is user login is completed
     * @throws MiddlewareQueryException the middleware query exception
     */
    public boolean changeUserPassword(String username, String password) throws MiddlewareQueryException;
    
    /**
     * Checks if is person exists.
     *
     * @param firstName - the first name
     * @param lastName - the last name
     * @return true, if is person exists
     * @throws MiddlewareQueryException the middleware query exception
     */
    public boolean isPersonExists(String firstName, String lastName) throws MiddlewareQueryException;
    
    /**
     * Checks if a username exists.
     *
     * @param userName - the user name to check
     * @return true, if is username exists
     * @throws MiddlewareQueryException the middleware query exception
     */
    public boolean isUsernameExists(String userName) throws MiddlewareQueryException;
    
    /**
     * Get the user info record for the specified user.
     *
     * @param userId the user id
     * @return the user info
     * @throws MiddlewareQueryException the middleware query exception
     */
    public UserInfo getUserInfo(int userId) throws MiddlewareQueryException;
    
    /**
     * Increments the log in count.
     *
     * @param userId the user id
     * @throws MiddlewareQueryException the middleware query exception
     */
    public void incrementUserLogInCount(int userId) throws MiddlewareQueryException;
    
    /**
     * Insert or update the specified {@link UserInfo} record.
     *
     * @param userDetails the user details
     * @throws MiddlewareQueryException the middleware query exception
     */
    public void insertOrUpdateUserInfo(UserInfo userDetails) throws MiddlewareQueryException;
    
    /**
     * Adds the person.
     *
     * @param person - the Person to add
     * @return Returns the id of the {@code Person} record added
     * @throws MiddlewareQueryException the middleware query exception
     */
    public Integer addPerson(Person person) throws MiddlewareQueryException;
    
    /**
     * Adds a user.
     *
     * @param user - the user to add
     * @return Returns the id of the {@code User} record added
     * @throws MiddlewareQueryException the middleware query exception
     */
    public Integer addUser(User user) throws MiddlewareQueryException;
    
    /**
     * Gets a project by id.
     *
     * @param projectId - the project id to match
     * @return the project matching the given id
     * @throws MiddlewareQueryException the middleware query exception
     */
    public Project getProjectById(Long projectId) throws MiddlewareQueryException;
    
    /**
     * Gets a project by name. Should return only one value.
     *
     * @param projectName - the project name to match
     * @return the project matching the given name
     * @throws MiddlewareQueryException the middleware query exception
     */
    public Project getProjectByName(String projectName) throws MiddlewareQueryException;

    /**
     * Updates all the project roles for a project.
     *
     * @param project - the project to use
     * @param newRoles - the new roles to add
     * @return void
     * @throws MiddlewareQueryException the middleware query exception
     */
    public void updateProjectsRolesForProject(Project project, List<ProjectUserRole> newRoles) throws MiddlewareQueryException;
    
    /**
     * Registers a workbench dataset.
     *
     * @param dataset - the workbench dataset to save
     * @return Returns the id of the {@code WorkbenchDataset} record added
     * @throws MiddlewareQueryException the middleware query exception
     */
    public Integer addWorkbenchDataset(WorkbenchDataset dataset) throws MiddlewareQueryException;
    
    /**
     * Gets the workbench dataset by id.
     *
     * @param datasetId the dataset id
     * @return the workench dataset matching the given id
     * @throws MiddlewareQueryException the middleware query exception
     */
    public WorkbenchDataset getWorkbenchDatasetById(Long datasetId) throws MiddlewareQueryException;
    
    /**
     * Delete a workbench dataset.
     *
     * @param dataset the dataset to delete
     * @throws MiddlewareQueryException the middleware query exception
     */
    public void deleteWorkbenchDataset(WorkbenchDataset dataset)  throws MiddlewareQueryException;


    /**
     * Returns all the Workbench users.
     *
     * @return A {@code List} of all the {@code User}s in the Workbench database.
     * @throws MiddlewareQueryException the middleware query exception
     */   	
    public List<User> getAllUsers() throws MiddlewareQueryException;
    
    /**
     * Returns all the Workbench users ordered by First Name then Last Name.
     *
     * @return A {@code List} of all the {@code User}s in the Workbench database.
     * @throws MiddlewareQueryException the middleware query exception
     */
    public List<User> getAllUsersSorted() throws MiddlewareQueryException;
    
    /**
     * Returns number of all Users.
     *
     * @return the number of all Users
     * @throws MiddlewareQueryException the middleware query exception
     */   
    public long countAllUsers() throws MiddlewareQueryException;
    
    /**
     * Gets the user by id.
     *
     * @param id - the user id to match
     * @return the user matching the given id
     * @throws MiddlewareQueryException the middleware query exception
     */
    public User getUserById(int id) throws MiddlewareQueryException;
    
    /**
     * Gets the user by name.
     *
     * @param name - the name to match
     * @param start - the starting record
     * @param numOfRows - the number of rows to retrieve
     * @param op the op
     * @return the user by name
     * @throws MiddlewareQueryException the middleware query exception
     */
    public List<User> getUserByName(String name, int start, int numOfRows, Operation op) throws MiddlewareQueryException;
    
    /**
     * Deletes a user.
     *
     * @param user - the User to delete
     * @throws MiddlewareQueryException the middleware query exception
     */
    public void deleteUser(User user) throws MiddlewareQueryException;
    
    /**
     * Returns all Persons.
     *
     * @return all Persons
     * @throws MiddlewareQueryException the middleware query exception
     */   
    public List<Person> getAllPersons() throws MiddlewareQueryException;
    
    /**
     * Returns number of all Persons.
     *
     * @return the number of all Persons
     * @throws MiddlewareQueryException the middleware query exception
     */   
    public long countAllPersons() throws MiddlewareQueryException;
    
    /**
     * Gets the person by id.
     *
     * @param id - the id to match
     * @return the person matching the given id
     * @throws MiddlewareQueryException the middleware query exception
     */
    public Person getPersonById(int id) throws MiddlewareQueryException;
    
    /**
     * Deletes a person.
     *
     * @param person - the Person to delete
     * @throws MiddlewareQueryException the middleware query exception
     */
    public void deletePerson(Person person) throws MiddlewareQueryException; 
    
    /**
     * Returns the project last accessed by the user.
     *
     * @param userId - the user id to match
     * @return the last Project opened by the given user
     * @throws MiddlewareQueryException the middleware query exception
     */
    public Project getLastOpenedProject(Integer userId) throws MiddlewareQueryException;
    
    /**
     * Returns a list of {@link WorkbenchDataset} records by project id.
     *
     * @param projectId - the project id
     * @param start - the starting record
     * @param numOfRows - the number of rows to retrieve
     * @return the list of {@link WorkbenchDataset}s
     * @throws MiddlewareQueryException the middleware query exception
     */
    public List<WorkbenchDataset> getWorkbenchDatasetByProjectId(Long projectId, int start, int numOfRows) throws MiddlewareQueryException;
    
    /**
     * Returns the number of {@link WorkbenchDataset} records by project id.
     *
     * @param projectId the project id
     * @return the number of {@link WorkbenchDataset} records
     * @throws MiddlewareQueryException the middleware query exception
     */
    public long countWorkbenchDatasetByProjectId(Long projectId) throws MiddlewareQueryException;
    
    /**
     * Returns a list of {@link WorkbenchDataset} by name.
     *
     * @param name - the {@link WorkbenchDataset} name
     * @param op - the operator; EQUAL, LIKE
     * @param start - the starting record
     * @param numOfRows - the number of rows to retrieve
     * @return the list of {@link WorkbenchDataset}
     * @throws MiddlewareQueryException the middleware query exception
     */
    public List<WorkbenchDataset> getWorkbenchDatasetByName(String name, Operation op, int start, int numOfRows) throws MiddlewareQueryException;
    
    /**
     * Returns the number of {@link WorkbenchDataset} by name.
     *
     * @param name - the {@link WorkbenchDataset} name
     * @param op - the operator; EQUAL, LIKE
     * @return the number of {@link WorkbenchDataset}
     * @throws MiddlewareQueryException the middleware query exception
     */
    public long countWorkbenchDatasetByName(String name, Operation op) throws MiddlewareQueryException;
    
    /**
     * Returns a list of {@link Location} ids by project id.
     *
     * @param projectId - the project id to match
     * @param start - the starting record
     * @param numOfRows - the number of rows to retrieve
     * @return the list of {@link Location} ids
     * @throws MiddlewareQueryException the middleware query exception
     */
    public List<Long> getLocationIdsByProjectId(Long projectId, int start, int numOfRows) throws MiddlewareQueryException;
    
    /**
     * Returns the number of {@link Location} ids by project id.
     *
     * @param projectId - the project id to match
     * @return the number of {@link Location} ids
     * @throws MiddlewareQueryException the middleware query exception
     */
    public long countLocationIdsByProjectId(Long projectId) throws MiddlewareQueryException;

    /**
     * Returns a list of method id by project id.
     *
     * @param projectId - the project id to match
     * @param start - the starting record
     * @param numOfRows - the number of rows to retrieve
     * @return the list of method ids
     * @throws MiddlewareQueryException the middleware query exception
     */
    public List<Integer> getMethodIdsByProjectId(Long projectId, int start, int numOfRows) throws MiddlewareQueryException;

    /**
     * Returns the number of method ids by project id.
     *
     * @param projectId - the project id to match
     * @return the number of method ids
     * @throws MiddlewareQueryException the middleware query exception
     */
    public long countMethodIdsByProjectId(Long projectId) throws MiddlewareQueryException;
    
    
    /**
     * Adds a single project user given a Project object, a User object, and a Role object.
     *
     * @param project the project
     * @param user the user
     * @param role the role of the user in the project
     * @return Returns the id of the {@code ProjectUserRole} record added
     * @throws MiddlewareQueryException the middleware query exception
     */
    public Integer addProjectUserRole(Project project, User user, Role role) throws MiddlewareQueryException;

    /**
     * Adds a single workbench_project_user_role record.
     *
     * @param projectUserRole - the ProjectUserRole to save
     * @return Returns the id of the {@code ProjectUserRole} record added
     * @throws MiddlewareQueryException the middleware query exception
     */
    public Integer addProjectUserRole(ProjectUserRole projectUserRole) throws MiddlewareQueryException;

    /**
     * Adds multiple workbench_project_user_role records.
     *
     * @param projectUserRoles - the records to add
     * @return Returns the ids of the {@code ProjectUserRole} records added
     * @throws MiddlewareQueryException the middleware query exception
     */
    public List<Integer> addProjectUserRole(List<ProjectUserRole> projectUserRoles) throws MiddlewareQueryException;

    /**
     * Adds a project location.
     *
     * @param projectLocationMap - the project location map
     * @return the id of the {@code ProjectLocationMap} record inserted
     * in the database.
     * @throws MiddlewareQueryException the middleware query exception
     */
    public Integer addProjectLocationMap(ProjectLocationMap projectLocationMap) throws MiddlewareQueryException;
    
    /**
     * Adds project locations.
     *
     * @param projectLocationMapList - the project location map list
     * @return Returns the ids of the {@code ProjectLocationMap} record added
     * @throws MiddlewareQueryException the middleware query exception
     */
    public List<Integer> addProjectLocationMap(List<ProjectLocationMap> projectLocationMapList) throws MiddlewareQueryException;
    
    
    /**
     * Gets the project location map by project id.
     *
     * @param projectId - the project id
     * @param start - the start row
     * @param numOfRows - the number of rows to retrieve
     * @return the project location map by project id
     * @throws MiddlewareQueryException the MiddlewareQueryException
     */
    public List<ProjectLocationMap> getProjectLocationMapByProjectId(Long projectId, int start, int numOfRows) throws MiddlewareQueryException;
    

    /**
     * Delete project location map.
     *
     * @param projectLocationMap the project location map
     * @throws MiddlewareQueryException the middleware query exception
     */
    public void deleteProjectLocationMap(ProjectLocationMap projectLocationMap) throws MiddlewareQueryException;

    /**
     * Adds project method.
     *
     * @param projectMethod - the project method
     * @return Returns the id of the {@code ProjectMethod} record added
     * @throws MiddlewareQueryException the middleware query exception
     */
    public Integer addProjectMethod(ProjectMethod projectMethod) throws MiddlewareQueryException;
    
    /**
     * Adds project methods.
     *
     * @param projectMethodList - the project method list
     * @return Returns the ids of the {@code ProjectUserRole} records added
     * @throws MiddlewareQueryException the middleware query exception
     */
    public List<Integer> addProjectMethod(List<ProjectMethod> projectMethodList) throws MiddlewareQueryException;

    
    /**
     * Gets the project method by project.
     *
     * @param project - the project to match
     * @param start - the start row
     * @param numOfRows - the number of rows to retrieve
     * @return the list of ProjectMethod records retrieved
     * @throws MiddlewareQueryException the middleware query exception
     */
    public List<ProjectMethod> getProjectMethodByProject(Project project, int start, int numOfRows) throws MiddlewareQueryException;
    

    /**
     * Delete project method.
     *
     * @param projectMethod - the project method
     * @throws MiddlewareQueryException the middleware query exception
     */
    public void deleteProjectMethod(ProjectMethod projectMethod) throws MiddlewareQueryException;
    
    /**
     * Adds a project activity.
     *
     * @param projectActivity - the project activity
     * @return Returns the id of the {@code ProjectActivity} record added
     * @throws MiddlewareQueryException the middleware query exception
     */
    public Integer addProjectActivity(ProjectActivity projectActivity) throws MiddlewareQueryException;
    
    /**
     * Adds project activities.
     *
     * @param projectActivityList - the project activity list
     * @return Returns the ids of the {@code ProjectActivity} records added
     * @throws MiddlewareQueryException the middleware query exception
     */
    public List<Integer> addProjectActivity(List<ProjectActivity> projectActivityList) throws MiddlewareQueryException;
    
    /**
     * Retrieves a workbench_project_user_role record by id.
     *
     * @param id - the ProjectUserRole id
     * @return the associated ProjectUser
     * @throws MiddlewareQueryException the middleware query exception
     */
    public ProjectUserRole getProjectUserRoleById(Integer id) throws MiddlewareQueryException;
    
    /**
     * Retrieves the workbench_project_user_role records based on the given project.
     *
     * @param project - the Project to match
     * @return the associated list of ProjectUser
     * @throws MiddlewareQueryException the middleware query exception
     */
    public List<ProjectUserRole> getProjectUserRolesByProject(Project project) throws MiddlewareQueryException;
    
    /**
     * Deletes the given ProjectUserRole.
     *
     * @param projectUserRole - the ProjectUserRole to delete
     * @throws MiddlewareQueryException the middleware query exception
     */
    public void deleteProjectUserRole(ProjectUserRole projectUserRole) throws MiddlewareQueryException;
    
    /**
     * Return a List of {@link User} records associated with a {@link Project}.
     *
     * @param projectId - the project id
     * @return the List of {@link User} records
     * @throws MiddlewareQueryException the middleware query exception
     */
    public List<User> getUsersByProjectId(Long projectId) throws MiddlewareQueryException;

    /**
     * Returns the number of {@link User} records associated with a {@link Project}.
     *
     * @param projectId - the project id
     * @return the number of {@link User} records
     * @throws MiddlewareQueryException the middleware query exception
     */
    public long countUsersByProjectId(Long projectId) throws MiddlewareQueryException;
    
    /**
     * Get the list of all installed central crop databases.
     *
     * @return the installed central crops
     * @throws MiddlewareQueryException the middleware query exception
     */
    public List<CropType> getInstalledCentralCrops() throws MiddlewareQueryException;
    
    /**
     * Get the crop type corresponding to the given name.
     *
     * @param cropName - the crop name to match
     * @return the CropType retrieved
     * @throws MiddlewareQueryException the middleware query exception
     */
    public CropType getCropTypeByName(String cropName) throws MiddlewareQueryException;

    /**
     * Add a crop type to the database.
     *
     * @param cropType - the crop type to add
     * @return Returns the id of the {@code CropType} record added
     * @throws MiddlewareQueryException the middleware query exception
     */
    public String addCropType(CropType cropType) throws MiddlewareQueryException;

    /**
     * Return a List of {@link ProjectActivity} records associated with a {@link Project}.
     *
     * @param projectId - the project id
     * @param start - the starting record
     * @param numOfRows - the number of rows to retrieve
     * @return the List of {@link ProjectActivity} records
     * @throws MiddlewareQueryException the middleware query exception
     */
    public List<ProjectActivity> getProjectActivitiesByProjectId(Long projectId, int start, int numOfRows) throws MiddlewareQueryException;

    /**
     * Delete a project activity.
     *
     * @param projectActivity - the project activity to delete
     * @throws MiddlewareQueryException the middleware query exception
     */
    public void deleteProjectActivity(ProjectActivity projectActivity) throws MiddlewareQueryException;

    /**
     * Returns the number of {@link ProjectActivity} records associated with a {@link Project}.
     *
     * @param projectId - the project id to match
     * @return the number of {@link ProjectActivity} records associated to the given project
     * @throws MiddlewareQueryException the middleware query exception
     */
    public long countProjectActivitiesByProjectId(Long projectId) throws MiddlewareQueryException;
    
    /**
     * Adds the tool configuration.
     *
     * @param toolConfig - the tool config to add
     * @return Returns the id of the {@code ToolConfiguration} record added
     * @throws MiddlewareQueryException the middleware query exception
     */
    public Integer addToolConfiguration(ToolConfiguration toolConfig) throws MiddlewareQueryException;
    
    /**
     * Update tool configuration.
     *
     * @param toolConfig - the tool config to update
     * @return Returns the id of the updated {@code ToolConfiguration} record
     * @throws MiddlewareQueryException the middleware query exception
     */
    public Integer updateToolConfiguration(ToolConfiguration toolConfig) throws MiddlewareQueryException;
    
    /**
     * Delete tool configuration.
     *
     * @param toolConfig - the tool config to delete
     * @throws MiddlewareQueryException the middleware query exception
     */
    public void deleteToolConfiguration(ToolConfiguration toolConfig) throws MiddlewareQueryException;
    
    /**
     * Gets the list of {@link ToolConfiguration} records by tool id.
     *
     * @param toolId - the tool id
     * @return the list of tool configurations by tool id
     * @throws MiddlewareQueryException the middleware query exception
     */
    public List<ToolConfiguration> getListOfToolConfigurationsByToolId(Long toolId) throws MiddlewareQueryException;
    
    /**
     * Gets the {@link ToolConfiguration} by tool id and config key.
     *
     * @param toolId - the tool id
     * @param configKey - the config key
     * @return the tool configuration by tool id and config key
     * @throws MiddlewareQueryException the middleware query exception
     */
    public ToolConfiguration getToolConfigurationByToolIdAndConfigKey(Long toolId, String configKey) throws MiddlewareQueryException;
    
    /**
     * Add a Workbench User and IBDB local database User mapping.
     *
     * @param userMap the user map
     * @return Returns the id of the {@code IbdbUserMap} record added
     * @throws MiddlewareQueryException the middleware query exception
     */
    public Integer addIbdbUserMap(IbdbUserMap userMap) throws MiddlewareQueryException;
    
    /**
     * Returns the Local IBDB User ID given a combination of a Workbench User ID and a Project ID.
     *
     * @param workbenchUserId - the specified Workbench User ID
     * @param projectId - the specified Project ID
     * @return Returns the IBDB User ID associated with the specified Workbench User ID and Project ID.
     * Returns null when there is no IBDB User ID matching the specified Workbench User ID and Project ID.
     * @throws MiddlewareQueryException the middleware query exception
     */
    public Integer getLocalIbdbUserId(Integer workbenchUserId, Long projectId) throws MiddlewareQueryException;
    
    /**
     * Save or update the specified {@link WorkbenchRuntimeData}.
     *
     * @param workbenchRuntimeData the workbench runtime data
     * @return Returns the id of the updated {@code WorkbenchRuntimeData} record
     * @throws MiddlewareQueryException the middleware query exception
     */
    public Integer updateWorkbenchRuntimeData(WorkbenchRuntimeData workbenchRuntimeData) throws MiddlewareQueryException;
    
    /**
     * Gets the workbench runtime data.
     *
     * @return The WorkbenchRuntimeData
     * @throws MiddlewareQueryException the middleware query exception
     */
    public WorkbenchRuntimeData getWorkbenchRuntimeData() throws MiddlewareQueryException;
    
    /**
     * Gets the role by id.
     *
     * @param id - the role id to match
     * @return the role matching the given id
     * @throws MiddlewareQueryException the middleware query exception
     */
    public Role getRoleById(Integer id) throws MiddlewareQueryException;

    /**
     * Gets the role by name and workflow template.
     *
     * @param name - the role name to match
     * @param workflowTemplate - the workflow template to match
     * @return the role matching the given name and workflow template
     * @throws MiddlewareQueryException the middleware query exception
     */
    public Role getRoleByNameAndWorkflowTemplate(String name, WorkflowTemplate workflowTemplate) throws MiddlewareQueryException;

    /**
     * Gets the roles by workflow template.
     *
     * @param workflowTemplate - the workflow template to match
     * @return the role matching the given workflow template
     * @throws MiddlewareQueryException the middleware query exception
     */
    public List<Role> getRolesByWorkflowTemplate(WorkflowTemplate workflowTemplate) throws MiddlewareQueryException;

    /**
     * Gets the workflow template of the given role.
     *
     * @param role - the role to match
     * @return the workflow template matching the given role
     * @throws MiddlewareQueryException the middleware query exception
     */
    public WorkflowTemplate getWorkflowTemplateByRole(Role role) throws MiddlewareQueryException;
    
    /**
     * Gets the roles given the project and user.
     *
     * @param project - the project to match
     * @param user - the user to match
     * @return the list of roles matching the given workflow template
     * @throws MiddlewareQueryException the middleware query exception
     */
    public List<Role> getRolesByProjectAndUser(Project project, User user) throws MiddlewareQueryException;

    /**
     * Returns all records from the workbench_role table,.
     *
     * @return List of Role objects
     * @throws MiddlewareQueryException the middleware query exception
     */
    public List<Role> getAllRoles() throws MiddlewareQueryException;
    
    /**
     * Returns all records from the workbench_role table, ordered by descending role_id.
     *
     * @return List of Role objects
     * @throws MiddlewareQueryException the middleware query exception
     */
    public List<Role> getAllRolesDesc() throws MiddlewareQueryException;
    
    /**
     * Returns all records from the workbench_role table, ordered by ascending label_order.
     *
     * @return List of Role objects
     * @throws MiddlewareQueryException the middleware query exception
     */
    public List<Role> getAllRolesOrderedByLabel() throws MiddlewareQueryException;

    /**
     * Get the workbench setting object.
     *
     * @return The WorkbenchSetting
     * @throws MiddlewareQueryException the middleware query exception
     */
    public WorkbenchSetting getWorkbenchSetting() throws MiddlewareQueryException;

    /**
     * Saves the specified SecurityQuestion object.
     *
     * @param securityQuestion - the Security Question object to be saved.
     * @throws MiddlewareQueryException the middleware query exception
     */
    public void addSecurityQuestion(SecurityQuestion securityQuestion) throws MiddlewareQueryException;

    /**
     * Gets all the Security Questions associated with the specified User ID.
     *
     * @param userId - User ID of the user to get security questions.
     * @return -  a {@code List} of {@code SecurityQuestion} objects associated with the given User.
     * @throws MiddlewareQueryException the middleware query exception
     */
    public List<SecurityQuestion> getQuestionsByUserId(Integer userId) throws MiddlewareQueryException;
    
    /**
     * Returns the ProjectUserMysqlAccount record identified by the given project id and user id.
     *
     * @param projectId the project id
     * @param userId the user id
     * @return The ProjectUserMysqlAccount of the given project id and user id.
     * @throws MiddlewareQueryException the middleware query exception
     */
    public ProjectUserMysqlAccount getProjectUserMysqlAccountByProjectIdAndUserId(Integer projectId, Integer userId) throws MiddlewareQueryException;
    
    /**
     * Stores a list of ProjectUserMysqlAccount records in the database.
     *
     * @param records the records
     * @return List of ids of the records saved
     * @throws MiddlewareQueryException the middleware query exception
     */
    public List<Integer> addProjectUserMysqlAccounts(List<ProjectUserMysqlAccount> records) throws MiddlewareQueryException;
    
    /**
     * Stores a ProjectUserMysqlAccount record in the database.
     *
     * @param record the record
     * @return id of the record saved
     * @throws MiddlewareQueryException the middleware query exception
     */
    public Integer addProjectUserMysqlAccount(ProjectUserMysqlAccount record) throws MiddlewareQueryException;

    /**
     * Gets the backup projects.
     *
     * @return the backup projects
     * @throws MiddlewareQueryException the middleware query exception
     */
    public List<ProjectBackup> getProjectBackups()  throws MiddlewareQueryException;
    
    /**
     * Gets the backup of the specified projects.
     *
     * @param project the project
     * @return the backup projects
     * @throws MiddlewareQueryException the middleware query exception
     */
    public List<ProjectBackup> getProjectBackups(Project project)  throws MiddlewareQueryException;

    /**
     * Save or update backup project.
     *
     * @param projectBackup - the project backup to save
     * @return the project backup saved
     * @throws MiddlewareQueryException the middleware query exception
     */
    public ProjectBackup saveOrUpdateProjectBackup(ProjectBackup projectBackup) throws MiddlewareQueryException;

    /**
     * remove backup project.
     *
     * @param projectBackup - the project backup to be deleted
     * @throws MiddlewareQueryException the middleware query exception
     */
    public void deleteProjectBackup(ProjectBackup projectBackup) throws MiddlewareQueryException;
    
    /**
     * Gets the ProjectUserInfoDAO.
     *
     * @return ProjectUserInfoDAO
     * @throws MiddlewareQueryException the middleware query exception
     */
    public ProjectUserInfoDAO getProjectUserInfoDao() throws MiddlewareQueryException;
    
    /**
     * Saves or updates the ProjectUserInfo.
     *
     * @param projectUserInfo the project user info
     * @return ProjectUserInfo
     * @throws MiddlewareQueryException the middleware query exception
     */
    public ProjectUserInfo saveOrUpdateProjectUserInfo(ProjectUserInfo projectUserInfo) throws MiddlewareQueryException;
    
    /**
     * Retrieves ids of the breeding methods selected for a given workbench project.
     *
     * @param projectId the project id
     * @return List of breeding method IDs
     * @throws MiddlewareQueryException the middleware query exception
     */
    public List<Integer> getBreedingMethodIdsByWorkbenchProjectId(Integer projectId) throws MiddlewareQueryException;

    /**
     * Gets the favorite project location ids.
     *
     * @param projectId the project id
     * @param start the start
     * @param numOfRows the num of rows
     * @return the favorite project location ids
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Long> getFavoriteProjectLocationIds(Long projectId, int start, int numOfRows) throws MiddlewareQueryException;

    /**
     * Gets the favorite project methods.
     *
     * @param project the project
     * @param start the start
     * @param numOfRows the num of rows
     * @return the favorite project methods
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Integer> getFavoriteProjectMethods(Project project, int start, int numOfRows) throws  MiddlewareQueryException;

    /**
     * Gets the all workbench sidebar category.
     *
     * @return the all workbench sidebar category
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<WorkbenchSidebarCategory> getAllWorkbenchSidebarCategory() throws MiddlewareQueryException;

    /**
     * Gets the all workbench sidebar links.
     *
     * @return the all workbench sidebar links
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<WorkbenchSidebarCategoryLink> getAllWorkbenchSidebarLinks() throws MiddlewareQueryException;

    /**
     * Gets the all workbench sidebar links by category id.
     *
     * @param category the category
     * @return the all workbench sidebar links by category id
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<WorkbenchSidebarCategoryLink> getAllWorkbenchSidebarLinksByCategoryId(WorkbenchSidebarCategory category) throws MiddlewareQueryException;
}
