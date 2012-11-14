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

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.IbdbUserMap;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.generationcp.middleware.pojos.workbench.ProjectLocationMap;
import org.generationcp.middleware.pojos.workbench.ProjectMethod;
import org.generationcp.middleware.pojos.workbench.ProjectUserRole;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolConfiguration;
import org.generationcp.middleware.pojos.workbench.ToolType;
import org.generationcp.middleware.pojos.workbench.WorkbenchDataset;
import org.generationcp.middleware.pojos.workbench.WorkbenchRuntimeData;
import org.generationcp.middleware.pojos.workbench.WorkflowTemplate;

/**
 * This is the API used by the Workbench to retrieve Workbench project
 * information.
 * 
 */
public interface WorkbenchDataManager{

    /**
     * Gets the projects.
     *
     * @return the projects
     * @throws MiddlewareQueryException
     */
    public List<Project> getProjects()  throws MiddlewareQueryException;

    /**
     * Gets the projects.
     *
     * @param start - the starting record
     * @param numOfRows - the number of rows to retrieve
     * @return the projects
     * @throws MiddlewareQueryException
     */
    public List<Project> getProjects(int start, int numOfRows)  throws MiddlewareQueryException;
    
    /**
     * Gets the list of Projects that the specified User is associated with.
     *
     * @param user - the User associated with the projects to be retrieved
     * @return the projects which the specified user is involved
     * @throws MiddlewareQueryException
     */
    public List<Project> getProjectsByUser(User user)  throws MiddlewareQueryException;

    /**
     * Save or update project.
     *
     * @param project - the project to save
     * @return the project saved
     * @throws MiddlewareQueryException
     */
    public Project saveOrUpdateProject(Project project) throws MiddlewareQueryException;

    /**
     * Delete project.
     *
     * @param project - the project to delete
     * @throws MiddlewareQueryException
     */
    public void deleteProject(Project project) throws MiddlewareQueryException;

    /**
     * Gets the workflow templates.
     *
     * @return the workflow templates
     * @throws MiddlewareQueryException
     */
    public List<WorkflowTemplate> getWorkflowTemplates() throws MiddlewareQueryException;

    /**
     * Gets the workflow templates.
     *
     * @param start - the starting record
     * @param numOfRows - the number of rows to retrieve
     * @return the workflow templates
     * @throws MiddlewareQueryException
     */
    public List<WorkflowTemplate> getWorkflowTemplates(int start, int numOfRows) throws MiddlewareQueryException;

    /**
     * Gets the workflow templates based on the given name.
     *
     * @param name - the name of the workflow template
     * @return the workflow templates
     * @throws MiddlewareQueryException
     */
    public List<WorkflowTemplate> getWorkflowTemplateByName(String name) throws MiddlewareQueryException;

    /**
     * Get all tools.
     * 
     * @return
     * @throws MiddlewareQueryException
     */
    public List<Tool> getAllTools() throws MiddlewareQueryException;

    /**
     * Gets the tool with the given name.
     *
     * @param toolName - the tool name to match
     * @return the tool with the given name
     * @throws MiddlewareQueryException
     */
    public Tool getToolWithName(String toolName) throws MiddlewareQueryException;
    
    /**
     * Get the list of tools with the specified type.
     *
     * @param toolType the tool type
     * @return the list of matching tools
     * @throws MiddlewareQueryException
     */
    public List<Tool> getToolsWithType(ToolType toolType) throws MiddlewareQueryException;
    
    /**
     * Checks if is valid user login.
     *
     * @param username - the username
     * @param password - the password
     * @return true, if is valid user login
     * @throws MiddlewareQueryException
     */
    public boolean isValidUserLogin(String username, String password) throws MiddlewareQueryException;
    
    /**
     * Checks if is person exists.
     *
     * @param firstName - the first name
     * @param lastName - the last name
     * @return true, if is person exists
     * @throws MiddlewareQueryException
     */
    public boolean isPersonExists(String firstName, String lastName) throws MiddlewareQueryException;
    
    /**
     * Checks if a username exists.
     *
     * @param userName - the user name to check
     * @return true, if is username exists
     * @throws MiddlewareQueryException
     */
    public boolean isUsernameExists(String userName) throws MiddlewareQueryException;
    
    /**
     * Adds the person.
     *
     * @param person - the Person to add
     * @return Returns the id of the {@code Person} record added
     * @throws MiddlewareQueryException
     */
    public Integer addPerson(Person person) throws MiddlewareQueryException;
    
    /**
     * Adds a user.
     *
     * @param user - the user to add
     * @return Returns the id of the {@code User} record added
     * @throws MiddlewareQueryException
     */
    public Integer addUser(User user) throws MiddlewareQueryException;
    
    /**
     * Gets a project by id.
     *
     * @param projectId - the project id to match
     * @return the project matching the given id
     * @throws MiddlewareQueryException
     */
    public Project getProjectById(Long projectId) throws MiddlewareQueryException;
    
    /**
     * Registers a workbench dataset.
     *
     * @param dataset - the workbench dataset to save
     * @return Returns the id of the {@code WorkbenchDataset} record added
     * @throws MiddlewareQueryException
     */
    public Integer addWorkbenchDataset(WorkbenchDataset dataset) throws MiddlewareQueryException;
    
    /**
     * Gets the workbench dataset by id.
     *
     * @param datasetId 
     * @return the workench dataset matching the given id
     * @throws MiddlewareQueryException
     */
    public WorkbenchDataset getWorkbenchDatasetById(Long datasetId) throws MiddlewareQueryException;
    
    /**
     * Delete a workbench dataset.
     *
     * @param dataset the dataset to delete
     * @throws MiddlewareQueryException
     */
    public void deleteWorkbenchDataset(WorkbenchDataset dataset)  throws MiddlewareQueryException;


    /**
     * Returns all the Workbench users.
     *
     * @return A {@code List} of all the {@code User}s in the Workbench database.
     * @throws MiddlewareQueryException
     */   	
    public List<User> getAllUsers() throws MiddlewareQueryException;
    
    /**
     * Returns all the Workbench users ordered by First Name then Last Name.
     * 
     * @return A {@code List} of all the {@code User}s in the Workbench database.
     * @throws MiddlewareQueryException
     */
    public List<User> getAllUsersSorted() throws MiddlewareQueryException;
    
    /**
     * Returns number of all Users.
     *
     * @return the number of all Users
     */   
    public long countAllUsers() throws MiddlewareQueryException;
    
    /**
     * Gets the user by id.
     *
     * @param id - the user id to match
     * @return the user matching the given id
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
     * @throws MiddlewareQueryException
     */
    public List<User> getUserByName(String name, int start, int numOfRows, Operation op) throws MiddlewareQueryException;
    
    /**
     * Deletes a user.
     *
     * @param user - the User to delete
     * @throws MiddlewareQueryException
     */
    public void deleteUser(User user) throws MiddlewareQueryException;
    
    /**
     * Returns all Persons.
     *
     * @return all Persons
     */   
    public List<Person> getAllPersons() throws MiddlewareQueryException;
    
    /**
     * Returns number of all Persons.
     *
     * @return the number of all Persons
     */   
    public long countAllPersons() throws MiddlewareQueryException;
    
    /**
     * Gets the person by id.
     *
     * @param id - the id to match
     * @return the person matching the given id
     */
    public Person getPersonById(int id) throws MiddlewareQueryException;
    
    /**
     * Deletes a person.
     *
     * @param person - the Person to delete
     * @throws MiddlewareQueryException
     */
    public void deletePerson(Person person) throws MiddlewareQueryException; 
    
    /**
     * Returns the project last accessed by the user.
     *
     * @param userId - the user id to match
     * @return the last Project opened by the given user
     * @throws MiddlewareQueryException
     */
    public Project getLastOpenedProject(Integer userId) throws MiddlewareQueryException;
    
    /**
     * Returns a list of {@link WorkbenchDataset} records by project id.
     *
     * @param projectId - the project id
     * @param start - the starting record
     * @param numOfRows - the number of rows to retrieve
     * @return the list of {@link WorkbenchDataset}s
     * @throws MiddlewareQueryException
     */
    public List<WorkbenchDataset> getWorkbenchDatasetByProjectId(Long projectId, int start, int numOfRows) throws MiddlewareQueryException;
    
    /**
     * Returns the number of {@link WorkbenchDataset} records by project id.
     *
     * @param projectId the project id
     * @return the number of {@link WorkbenchDataset} records
     * @throws MiddlewareQueryException
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
     * @throws MiddlewareQueryException
     */
    public List<WorkbenchDataset> getWorkbenchDatasetByName(String name, Operation op, int start, int numOfRows) throws MiddlewareQueryException;
    
    /**
     * Returns the number of {@link WorkbenchDataset} by name.
     *
     * @param name - the {@link WorkbenchDataset} name
     * @param op - the operator; EQUAL, LIKE
     * @return the number of {@link WorkbenchDataset}
     * @throws MiddlewareQueryException
     */
    public long countWorkbenchDatasetByName(String name, Operation op) throws MiddlewareQueryException;
    
    /**
     * Returns a list of {@link Location} ids by project id.
     *
     * @param projectId - the project id to match
     * @param start - the starting record
     * @param numOfRows - the number of rows to retrieve
     * @return the list of {@link Location} ids
     * @throws MiddlewareQueryException
     */
    public List<Long> getLocationIdsByProjectId(Long projectId, int start, int numOfRows) throws MiddlewareQueryException;
    
    /**
     * Returns the number of {@link Location} ids by project id.
     *
     * @param projectId - the project id to match
     * @return the number of {@link Location} ids
     * @throws MiddlewareQueryException
     */
    public long countLocationIdsByProjectId(Long projectId) throws MiddlewareQueryException;

    /**
     * Returns a list of method id by project id.
     *
     * @param projectId - the project id to match
     * @param start - the starting record
     * @param numOfRows - the number of rows to retrieve
     * @return the list of method ids
     * @throws MiddlewareQueryException
     */
    public List<Integer> getMethodIdsByProjectId(Long projectId, int start, int numOfRows) throws MiddlewareQueryException;

    /**
     * Returns the number of method ids by project id.
     *
     * @param projectId - the project id to match
     * @return the number of method ids
     * @throws MiddlewareQueryException
     */
    public long countMethodIdsByProjectId(Long projectId) throws MiddlewareQueryException;
    
    
    /**
     * Adds a single project user given a Project object, a User object, and a Role object
     *
     * @param project the project
     * @param user the user
     * @param role the role of the user in the project
     * @return Returns the id of the {@code ProjectUserRole} record added
     * @throws MiddlewareQueryException
     */
    public Integer addProjectUserRole(Project project, User user, Role role) throws MiddlewareQueryException;

    /**
     * Adds a single workbench_project_user_role record.
     *
     * @param projectUserRole - the ProjectUserRole to save
     * @return Returns the id of the {@code ProjectUserRole} record added
     * @throws MiddlewareQueryException
     */
    public Integer addProjectUserRole(ProjectUserRole projectUserRole) throws MiddlewareQueryException;

    /**
     * Adds multiple workbench_project_user_role records.
     *
     * @param projectUserRoles - the records to add
     * @return Returns the ids of the {@code ProjectUserRole} records added
     * @throws MiddlewareQueryException
     */
    public List<Integer> addProjectUserRole(List<ProjectUserRole> projectUserRoles) throws MiddlewareQueryException;

    /**
     * Adds a project location.
     *
     * @param projectLocationMap - the project location map
     * @return the id of the {@code ProjectLocationMap} record inserted
     * in the database.
     * @throws MiddlewareQueryException
     */
    public Integer addProjectLocationMap(ProjectLocationMap projectLocationMap) throws MiddlewareQueryException;
    
    /**
     * Adds project locations.
     *
     * @param projectLocationMapList - the project location map list
     * @return Returns the ids of the {@code ProjectLocationMap} record added
     * @throws MiddlewareQueryException
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
    

    public void deleteProjectLocationMap(ProjectLocationMap projectLocationMap) throws MiddlewareQueryException;

    /**
     * Adds project method.
     *
     * @param projectMethod - the project method
     * @return Returns the id of the {@code ProjectMethod} record added
     * @throws MiddlewareQueryException
     */
    public Integer addProjectMethod(ProjectMethod projectMethod) throws MiddlewareQueryException;
    
    /**
     * Adds project methods.
     *
     * @param projectMethodList - the project method list
     * @return Returns the ids of the {@code ProjectUserRole} records added
     * @throws MiddlewareQueryException
     */
    public List<Integer> addProjectMethod(List<ProjectMethod> projectMethodList) throws MiddlewareQueryException;

    
    /**
     * Gets the project method by project.
     *
     * @param project - the project to match
     * @param start - the start row
     * @param numOfRows - the number of rows to retrieve
     * @return the list of ProjectMethod records retrieved
     * @throws MiddlewareQueryException 
     */
    public List<ProjectMethod> getProjectMethodByProject(Project project, int start, int numOfRows) throws MiddlewareQueryException;
    

    /**
     * Delete project method.
     *
     * @param projectMethod - the project method
     * @throws MiddlewareQueryException 
     */
    public void deleteProjectMethod(ProjectMethod projectMethod) throws MiddlewareQueryException;
    
    /**
     * Adds a project activity.
     *
     * @param projectActivity - the project activity
     * @return Returns the id of the {@code ProjectActivity} record added
     * @throws MiddlewareQueryException
     */
    public Integer addProjectActivity(ProjectActivity projectActivity) throws MiddlewareQueryException;
    
    /**
     * Adds project activities.
     *
     * @param projectActivityList - the project activity list
     * @return Returns the ids of the {@code ProjectActivity} records added
     * @throws MiddlewareQueryException
     */
    public List<Integer> addProjectActivity(List<ProjectActivity> projectActivityList) throws MiddlewareQueryException;
    
    /**
     * Retrieves a workbench_project_user_role record by id.
     *
     * @param id - the ProjectUserRole id
     * @return the associated ProjectUser
     * @throws MiddlewareQueryException
     */
    public ProjectUserRole getProjectUserRoleById(Integer id) throws MiddlewareQueryException;
    
    /**
     * Retrieves the workbench_project_user_role records based on the given project.
     *
     * @param project - the Project to match
     * @return the associated list of ProjectUser
     * @throws MiddlewareQueryException
     */
    public List<ProjectUserRole> getProjectUserRolesByProject(Project project) throws MiddlewareQueryException;
    
    /**
     * Deletes the given ProjectUserRole.
     *
     * @param projectUserRole - the ProjectUserRole to delete
     * @throws MiddlewareQueryException
     */
    public void deleteProjectUserRole(ProjectUserRole projectUserRole) throws MiddlewareQueryException;
    
    /**
     * Return a List of {@link User} records associated with a {@link Project}.
     *
     * @param projectId - the project id
     * @return the List of {@link User} records
     * @throws MiddlewareQueryException
     */
    public List<User> getUsersByProjectId(Long projectId) throws MiddlewareQueryException;

    /**
     * Returns the number of {@link User} records associated with a {@link Project}.
     *
     * @param projectId - the project id
     * @return the number of {@link User} records
     * @throws MiddlewareQueryException
     */
    public long countUsersByProjectId(Long projectId) throws MiddlewareQueryException;
    
    /**
     * Get the list of all installed central crop databases.
     *
     * @return the installed central crops
     * @throws MiddlewareQueryException
     */
    public List<CropType> getInstalledCentralCrops() throws MiddlewareQueryException;
    
    /**
     * Get the crop type corresponding to the given name.
     *
     * @param cropName - the crop name to match
     * @return the CropType retrieved
     * @throws MiddlewareQueryException
     */
    public CropType getCropTypeByName(String cropName) throws MiddlewareQueryException;

    /**
     * Add a crop type to the database.
     *
     * @param cropType - the crop type to add
     * @return Returns the id of the {@code CropType} record added
     * @throws MiddlewareQueryException
     */
    public String addCropType(CropType cropType) throws MiddlewareQueryException;

    /**
     * Return a List of {@link ProjectActivity} records associated with a {@link Project}.
     *
     * @param projectId - the project id
     * @param start - the starting record
     * @param numOfRows - the number of rows to retrieve
     * @return the List of {@link ProjectActivity} records
     * @throws MiddlewareQueryException
     */
    public List<ProjectActivity> getProjectActivitiesByProjectId(Long projectId, int start, int numOfRows) throws MiddlewareQueryException;

    /**
     * Delete a project activity.
     *
     * @param projectActivity - the project activity to delete
     * @throws MiddlewareQueryException
     */
    public void deleteProjectActivity(ProjectActivity projectActivity) throws MiddlewareQueryException;

    /**
     * Returns the number of {@link ProjectActivity} records associated with a {@link Project}.
     *
     * @param projectId - the project id to match
     * @return the number of {@link ProjectActivity} records associated to the given project
     * @throws MiddlewareQueryException
     */
    public long countProjectActivitiesByProjectId(Long projectId) throws MiddlewareQueryException;
    
    /**
     * Adds the tool configuration.
     *
     * @param toolConfig - the tool config to add
     * @return Returns the id of the {@code ToolConfiguration} record added
     * @throws MiddlewareQueryException
     */
    public Integer addToolConfiguration(ToolConfiguration toolConfig) throws MiddlewareQueryException;
    
    /**
     * Update tool configuration.
     *
     * @param toolConfig - the tool config to update
     * @return Returns the id of the updated {@code ToolConfiguration} record
     * @throws MiddlewareQueryException
     */
    public Integer updateToolConfiguration(ToolConfiguration toolConfig) throws MiddlewareQueryException;
    
    /**
     * Delete tool configuration.
     *
     * @param toolConfig - the tool config to delete
     * @throws MiddlewareQueryException
     */
    public void deleteToolConfiguration(ToolConfiguration toolConfig) throws MiddlewareQueryException;
    
    /**
     * Gets the list of {@link ToolConfiguration} records by tool id.
     *
     * @param toolId - the tool id
     * @return the list of tool configurations by tool id
     * @throws MiddlewareQueryException
     */
    public List<ToolConfiguration> getListOfToolConfigurationsByToolId(Long toolId) throws MiddlewareQueryException;
    
    /**
     * Gets the {@link ToolConfiguration} by tool id and config key.
     *
     * @param toolId - the tool id
     * @param configKey - the config key
     * @return the tool configuration by tool id and config key
     * @throws MiddlewareQueryException
     */
    public ToolConfiguration getToolConfigurationByToolIdAndConfigKey(Long toolId, String configKey) throws MiddlewareQueryException;
    
    /**
     * Add a Workbench User and IBDB local database User mapping.
     *
     * @param userMap the user map
     * @return Returns the id of the {@code IbdbUserMap} record added
     * @throws MiddlewareQueryException
     */
    public Integer addIbdbUserMap(IbdbUserMap userMap) throws MiddlewareQueryException;
    
    /**
     * Returns the Local IBDB User ID given a combination of a Workbench User ID and a Project ID.
     * 
     * @param workbenchUserId - the specified Workbench User ID
     * @param projectId - the specified Project ID
     * @return Returns the IBDB User ID associated with the specified Workbench User ID and Project ID. 
     * Returns null when there is no IBDB User ID matching the specified Workbench User ID and Project ID.
     * @throws MiddlewareQueryException
     */
    public Integer getLocalIbdbUserId(Integer workbenchUserId, Long projectId) throws MiddlewareQueryException;
    
    /**
     * Save or update the specified {@link WorkbenchRuntimeData}.
     * 
     * @param workbenchRuntimeData
     * @return Returns the id of the updated {@code WorkbenchRuntimeData} record
     * @throws MiddlewareQueryException
     */
    public Integer updateWorkbenchRuntimeData(WorkbenchRuntimeData workbenchRuntimeData) throws MiddlewareQueryException;
    
    /**
     * 
     * @return
     * @throws MiddlewareQueryException
     */
    public WorkbenchRuntimeData getWorkbenchRuntimeData() throws MiddlewareQueryException;
    
    /**
     * Gets the role by id.
     *
     * @param id - the role id to match
     * @return the role matching the given id
     */
    public Role getRoleById(Integer id) throws MiddlewareQueryException;

    /**
     * Gets the role by name and workflow template.
     *
     * @param name - the role name to match
     * @param workflowTemplate - the workflow template to match
     * @return the role matching the given name and workflow template
     */
    public Role getRoleByNameAndWorkflowTemplate(String name, WorkflowTemplate workflowTemplate) throws MiddlewareQueryException;

    /**
     * Gets the roles by workflow template.
     *
     * @param workflowTemplate - the workflow template to match
     * @return the role matching the given workflow template
     */
    public List<Role> getRolesByWorkflowTemplate(WorkflowTemplate workflowTemplate) throws MiddlewareQueryException;

    /**
     * Gets the workflow template of the given role.
     *
     * @param role - the role to match
     * @return the workflow template matching the given role
     */
    public WorkflowTemplate getWorkflowTemplateByRole(Role role) throws MiddlewareQueryException;
    
    /**
     * Gets the roles given the project and user.
     *
     * @param project - the project to match
     * @param user - the user to match
     * @return the list of roles matching the given workflow template
     */
    public List<Role> getRolesByProjectAndUser(Project project, User user) throws MiddlewareQueryException;

    /**
     * Returns all records from the workbench_role table,
     * 
     * @return List of Role objects
     * @throws MiddlewareQueryException
     */
    public List<Role> getAllRoles() throws MiddlewareQueryException;
}
