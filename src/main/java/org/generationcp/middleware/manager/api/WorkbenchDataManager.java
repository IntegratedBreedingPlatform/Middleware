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

import org.generationcp.middleware.exceptions.QueryException;
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
import org.generationcp.middleware.pojos.workbench.ProjectUser;
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
     * @throws QueryException
     */
    public List<Project> getProjects()  throws QueryException;

    /**
     * Gets the projects.
     *
     * @param start - the starting record
     * @param numOfRows - the number of rows to retrieve
     * @return the projects
     * @throws QueryException
     */
    public List<Project> getProjects(int start, int numOfRows)  throws QueryException;

    /**
     * Save or update project.
     *
     * @param project - the project to save
     * @return the project saved
     * @throws QueryException
     */
    public Project saveOrUpdateProject(Project project) throws QueryException;

    /**
     * Delete project.
     *
     * @param project - the project to delete
     * @throws QueryException
     */
    public void deleteProject(Project project) throws QueryException;

    /**
     * Gets the workflow templates.
     *
     * @return the workflow templates
     * @throws QueryException
     */
    public List<WorkflowTemplate> getWorkflowTemplates() throws QueryException;

    /**
     * Gets the workflow templates.
     *
     * @param start - the starting record
     * @param numOfRows - the number of rows to retrieve
     * @return the workflow templates
     * @throws QueryException
     */
    public List<WorkflowTemplate> getWorkflowTemplates(int start, int numOfRows) throws QueryException;

    /**
     * Gets the tool with the given name.
     *
     * @param toolName - the tool name to match
     * @return the tool with the given name
     * @throws QueryException
     */
    public Tool getToolWithName(String toolName) throws QueryException;
    
    /**
     * Get the list of tools with the specified type.
     *
     * @param toolType the tool type
     * @return the list of matching tools
     * @throws QueryException
     */
    public List<Tool> getToolsWithType(ToolType toolType) throws QueryException;
    
    /**
     * Checks if is valid user login.
     *
     * @param username - the username
     * @param password - the password
     * @return true, if is valid user login
     * @throws QueryException
     */
    public boolean isValidUserLogin(String username, String password) throws QueryException;
    
    /**
     * Checks if is person exists.
     *
     * @param firstName - the first name
     * @param lastName - the last name
     * @return true, if is person exists
     * @throws QueryException
     */
    public boolean isPersonExists(String firstName, String lastName) throws QueryException;
    
    /**
     * Checks if a username exists.
     *
     * @param userName - the user name to check
     * @return true, if is username exists
     * @throws QueryException
     */
    public boolean isUsernameExists(String userName) throws QueryException;
    
    /**
     * Adds the person.
     *
     * @param person - the Person to add
     * @throws QueryException
     */
    public void addPerson(Person person) throws QueryException;
    
    /**
     * Adds a user.
     *
     * @param user - the user to add
     * @throws QueryException
     */
    public void addUser(User user) throws QueryException;
    
    /**
     * Gets a project by id.
     *
     * @param projectId - the project id to match
     * @return the project matching the given id
     * @throws QueryException
     */
    public Project getProjectById(Long projectId) throws QueryException;
    
    /**
     * Registers a workbench dataset.
     *
     * @param dataset - the workbench dataset to save
     * @return the number of records saved (0 or 1)
     * @throws QueryException
     */
    public int addWorkbenchDataset(WorkbenchDataset dataset) throws QueryException;
    
    /**
     * Gets the workbench dataset by id.
     *
     * @param datasetId 
     * @return the workench dataset matching the given id
     * @throws QueryException
     */
    public WorkbenchDataset getWorkbenchDatasetById(Long datasetId) throws QueryException;
    
    /**
     * Delete a workbench dataset.
     *
     * @param dataset the dataset to delete
     * @throws QueryException
     */
    public void deleteWorkbenchDataset(WorkbenchDataset dataset)  throws QueryException;


    /**
     * Returns all Persons.
     *
     * @return all Persons
     */   	
    public List<User> getAllUsers();
    
    /**
     * Returns number of all Users.
     *
     * @return the number of all Users
     */   
    public int countAllUsers();
    
    /**
     * Gets the user by id.
     *
     * @param id - the user id to match
     * @return the user matching the given id
     */
    public User getUserById(int id);
    
    /**
     * Gets the user by name.
     *
     * @param name - the name to match
     * @param start - the starting record
     * @param numOfRows - the number of rows to retrieve
     * @param op the op
     * @return the user by name
     * @throws QueryException
     */
    public List<User> getUserByName(String name, int start, int numOfRows, Operation op) throws QueryException;
    
    /**
     * Deletes a user.
     *
     * @param user - the User to delete
     * @throws QueryException
     */
    public void deleteUser(User user) throws QueryException;
    
    /**
     * Returns all Persons.
     *
     * @return all Persons
     */   
    public List<Person> getAllPersons();
    
    /**
     * Returns number of all Persons.
     *
     * @return the number of all Persons
     */   
    public int countAllPersons();
    
    /**
     * Gets the person by id.
     *
     * @param id - the id to match
     * @return the person matching the given id
     */
    public Person getPersonById(int id);
    
    /**
     * Deletes a person.
     *
     * @param person - the Person to delete
     * @throws QueryException
     */
    public void deletePerson(Person person) throws QueryException; 
    
    /**
     * Returns the project last accessed by the user.
     *
     * @param userId - the user id to match
     * @return the last Project opened by the given user
     * @throws QueryException
     */
    public Project getLastOpenedProject(Integer userId) throws QueryException;
    
    /**
     * Returns a list of {@link WorkbenchDataset} records by project id.
     *
     * @param projectId - the project id
     * @param start - the starting record
     * @param numOfRows - the number of rows to retrieve
     * @return the list of {@link WorkbenchDataset}s
     * @throws QueryException
     */
    public List<WorkbenchDataset> getWorkbenchDatasetByProjectId(Long projectId, int start, int numOfRows) throws QueryException;
    
    /**
     * Returns the number of {@link WorkbenchDataset} records by project id.
     *
     * @param projectId the project id
     * @return the number of {@link WorkbenchDataset} records
     * @throws QueryException
     */
    public Long countWorkbenchDatasetByProjectId(Long projectId) throws QueryException;
    
    /**
     * Returns a list of {@link WorkbenchDataset} by name.
     *
     * @param name - the {@link WorkbenchDataset} name
     * @param op - the operator; EQUAL, LIKE
     * @param start - the starting record
     * @param numOfRows - the number of rows to retrieve
     * @return the list of {@link WorkbenchDataset}
     * @throws QueryException
     */
    public List<WorkbenchDataset> getWorkbenchDatasetByName(String name, Operation op, int start, int numOfRows) throws QueryException;
    
    /**
     * Returns the number of {@link WorkbenchDataset} by name.
     *
     * @param name - the {@link WorkbenchDataset} name
     * @param op - the operator; EQUAL, LIKE
     * @return the number of {@link WorkbenchDataset}
     * @throws QueryException
     */
    public Long countWorkbenchDatasetByName(String name, Operation op) throws QueryException;
    
    /**
     * Returns a list of {@link Location} ids by project id.
     *
     * @param projectId - the project id to match
     * @param start - the starting record
     * @param numOfRows - the number of rows to retrieve
     * @return the list of {@link Location} ids
     * @throws QueryException
     */
    public List<Long> getLocationIdsByProjectId(Long projectId, int start, int numOfRows) throws QueryException;
    
    /**
     * Returns the number of {@link Location} ids by project id.
     *
     * @param projectId - the project id to match
     * @return the number of {@link Location} ids
     * @throws QueryException
     */
    public Long countLocationIdsByProjectId(Long projectId) throws QueryException;

    /**
     * Returns a list of method id by project id.
     *
     * @param projectId - the project id to match
     * @param start - the starting record
     * @param numOfRows - the number of rows to retrieve
     * @return the list of method ids
     * @throws QueryException
     */
    public List<Integer> getMethodIdsByProjectId(Long projectId, int start, int numOfRows) throws QueryException;

    /**
     * Returns the number of method ids by project id.
     *
     * @param projectId - the project id to match
     * @return the number of method ids
     * @throws QueryException
     */
    public Long countMethodIdsByProjectId(Long projectId) throws QueryException;
    
    
    /**
     * Adds a single project user given a Project object and a User object.
     *
     * @param project the project
     * @param user the user
     * @return the number of records inserted
     * @throws QueryException
     */
    public int addProjectUser(Project project, User user) throws QueryException;

    /**
     * Adds a single project user.
     *
     * @param projectUser - the ProjectUser to save
     * @return the number of records inserted
     * @throws QueryException
     */
    public int addProjectUser(ProjectUser projectUser) throws QueryException;

    /**
     * Adds multiple project users.
     *
     * @param projectUsers - the project users to add
     * @return the number of records inserted
     * @throws QueryException
     */
    public int addProjectUsers(List<ProjectUser> projectUsers) throws QueryException;

    /**
     * Adds a project location.
     *
     * @param projectLocationMap - the project location map
     * @return the number of {@code ProjectLocationMap} records inserted
     * in the database.
     * @throws QueryException
     */
    public int addProjectLocationMap(ProjectLocationMap projectLocationMap) throws QueryException;
    
    /**
     * Adds project locations.
     *
     * @param projectLocationMapList - the project location map list
     * @return the number of {@code ProjectLocationMap} records inserted
     * in the database.
     * @throws QueryException
     */
    public int addProjectLocationMap(List<ProjectLocationMap> projectLocationMapList) throws QueryException;
    
    
    /**
     * Gets the project location map by project id.
     *
     * @param projectId - the project id
     * @param start - the start row
     * @param numOfRows - the number of rows to retrieve
     * @return the project location map by project id
     * @throws QueryException the query exception
     */
    public List<ProjectLocationMap> getProjectLocationMapByProjectId(Long projectId, int start, int numOfRows) throws QueryException;
    

    public void deleteProjectLocationMap(ProjectLocationMap projectLocationMap) throws QueryException;

    /**
     * Adds project method.
     *
     * @param projectMethod - the project method
     * @return the number of {@code ProjectMethod} records inserted
     * in the database.
     * @throws QueryException
     */
    public int addProjectMethod(ProjectMethod projectMethod) throws QueryException;
    
    /**
     * Adds project methods.
     *
     * @param projectMethodList - the project method list
     * @return the number of {@code ProjectMethods} records inserted
     * in the database.
     * @throws QueryException
     */
    public int addProjectMethod(List<ProjectMethod> projectMethodList) throws QueryException;

    
    /**
     * Gets the project method by project.
     *
     * @param project - the project to match
     * @param start - the start row
     * @param numOfRows - the number of rows to retrieve
     * @return the list of ProjectMethod records retrieved
     * @throws QueryException 
     */
    public List<ProjectMethod> getProjectMethodByProject(Project project, int start, int numOfRows) throws QueryException;
    

    /**
     * Delete project method.
     *
     * @param projectMethod - the project method
     * @throws QueryException 
     */
    public void deleteProjectMethod(ProjectMethod projectMethod) throws QueryException;
    
    /**
     * Adds a project activity.
     *
     * @param projectActivity - the project activity
     * @return the number of {@code ProjectActivity} records inserted
     * in the database.
     * @throws QueryException
     */
    public int addProjectActivity(ProjectActivity projectActivity) throws QueryException;
    
    /**
     * Adds project activities.
     *
     * @param projectActivityList - the project activity list
     * @return the number of {@code ProjectActivity} records inserted
     * in the database.
     * @throws QueryException
     */
    public int addProjectActivity(List<ProjectActivity> projectActivityList) throws QueryException;
    
    /**
     * Retrieves a project user by id.
     *
     * @param id - the ProjectUser id
     * @return the associated ProjectUser
     * @throws QueryException
     */
    public ProjectUser getProjectUserById(Integer id) throws QueryException;
    
    /**
     * Retrieves project user by project and user.
     *
     * @param project - the project
     * @param user - the user
     * @return the associated ProjectUser
     * @throws QueryException
     */
    public ProjectUser getProjectUserByProjectAndUser(Project project, User user) throws QueryException;
    
    /**
     * Retrieves project users by project.
     *
     * @param project - the project
     * @param user - the user
     * @return the associated ProjectUser
     * @throws QueryException
     */
    public List<ProjectUser> getProjectUserByProject(Project project) throws QueryException;

    /**
     * Deletes the given ProjectUser.
     *
     * @param projectUser - the ProjectUser to delete
     * @throws QueryException
     */
    public void deleteProjectUser(ProjectUser projectUser) throws QueryException;
    
    /**
     * Return a List of {@link User} records associated with a {@link Project}.
     *
     * @param projectId - the project id
     * @return the List of {@link User} records
     * @throws QueryException
     */
    public List<User> getUsersByProjectId(Long projectId) throws QueryException;

    /**
     * Returns the number of {@link User} records associated with a {@link Project}.
     *
     * @param projectId - the project id
     * @return the number of {@link User} records
     * @throws QueryException
     */
    public Long countUsersByProjectId(Long projectId) throws QueryException;
    
    /**
     * Get the list of all installed central crop databases.
     *
     * @return the installed central crops
     * @throws QueryException
     */
    public List<CropType> getInstalledCentralCrops() throws QueryException;
    
    /**
     * Get the crop type corresponding to the given name.
     *
     * @param cropName - the crop name to match
     * @return the CropType retrieved
     * @throws QueryException
     */
    public CropType getCropTypeByName(String cropName) throws QueryException;

    /**
     * Add a crop type to the database.
     *
     * @param cropType - the crop type to add
     * @return the number of records added
     * @throws QueryException
     */
    public int addCropType(CropType cropType) throws QueryException;

    /**
     * Return a List of {@link ProjectActivity} records associated with a {@link Project}.
     *
     * @param projectId - the project id
     * @param start - the starting record
     * @param numOfRows - the number of rows to retrieve
     * @return the List of {@link ProjectActivity} records
     * @throws QueryException
     */
    public List<ProjectActivity> getProjectActivitiesByProjectId(Long projectId, int start, int numOfRows) throws QueryException;

    /**
     * Delete a project activity.
     *
     * @param projectActivity - the project activity to delete
     * @throws QueryException
     */
    public void deleteProjectActivity(ProjectActivity projectActivity) throws QueryException;

    /**
     * Returns the number of {@link ProjectActivity} records associated with a {@link Project}.
     *
     * @param projectId - the project id to match
     * @return the number of {@link ProjectActivity} records associated to the given project
     * @throws QueryException
     */
    public Long countProjectActivitiesByProjectId(Long projectId) throws QueryException;
    
    /**
     * Adds the tool configuration.
     *
     * @param toolConfig - the tool config to add
     * @throws QueryException
     */
    public void addToolConfiguration(ToolConfiguration toolConfig) throws QueryException;
    
    /**
     * Update tool configuration.
     *
     * @param toolConfig - the tool config to update
     * @throws QueryException
     */
    public void updateToolConfiguration(ToolConfiguration toolConfig) throws QueryException;
    
    /**
     * Delete tool configuration.
     *
     * @param toolConfig - the tool config to delete
     * @throws QueryException
     */
    public void deleteToolConfiguration(ToolConfiguration toolConfig) throws QueryException;
    
    /**
     * Gets the list of {@link ToolConfiguration} records by tool id.
     *
     * @param toolId - the tool id
     * @return the list of tool configurations by tool id
     * @throws QueryException
     */
    public List<ToolConfiguration> getListOfToolConfigurationsByToolId(Long toolId) throws QueryException;
    
    /**
     * Gets the {@link ToolConfiguration} by tool id and config key.
     *
     * @param toolId - the tool id
     * @param configKey - the config key
     * @return the tool configuration by tool id and config key
     * @throws QueryException
     */
    public ToolConfiguration getToolConfigurationByToolIdAndConfigKey(Long toolId, String configKey) throws QueryException;
    
    /**
     * Add a Workbench User and IBDB local database User mapping.
     *
     * @param userMap the user map
     * @return the ibdb user map
     * @throws QueryException
     */
    public IbdbUserMap addIbdbUserMap(IbdbUserMap userMap) throws QueryException;
    
    /**
     * Returns the Local IBDB User ID given a combination of a Workbench User ID and a Project ID.
     * 
     * @param workbenchUserId - the specified Workbench User ID
     * @param projectId - the specified Project ID
     * @return Returns the IBDB User ID associated with the specified Workbench User ID and Project ID. 
     * Returns null when there is no IBDB User ID matching the specified Workbench User ID and Project ID.
     * @throws QueryException
     */
    public Integer getLocalIbdbUserId(Integer workbenchUserId, Long projectId) throws QueryException;
    
    /**
     * Save or update the specified {@link WorkbenchRuntimeData}.
     * 
     * @param workbenchRuntimeData
     * @return
     * @throws QueryException
     */
    public WorkbenchRuntimeData updateWorkbenchRuntimeData(WorkbenchRuntimeData workbenchRuntimeData) throws QueryException;
    
    /**
     * 
     * @return
     * @throws QueryException
     */
    public WorkbenchRuntimeData getWorkbenchRuntimeData() throws QueryException;
}
