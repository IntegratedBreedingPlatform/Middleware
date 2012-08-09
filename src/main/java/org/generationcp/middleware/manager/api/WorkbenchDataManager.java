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
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolType;
import org.generationcp.middleware.pojos.workbench.WorkbenchDataset;
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
     */
    public List<Project> getProjects()  throws QueryException;

    /**
     * Gets the projects.
     *
     * @param start the start
     * @param numOfRows the num of rows
     * @return the projects
     */
    public List<Project> getProjects(int start, int numOfRows)  throws QueryException;

    /**
     * Save or update project.
     *
     * @param project the project
     * @return the project
     */
    public Project saveOrUpdateProject(Project project) throws QueryException;

    /**
     * Delete project.
     *
     * @param project the project
     */
    public void deleteProject(Project project) throws QueryException;

    /**
     * Gets the workflow templates.
     *
     * @return the workflow templates
     */
    public List<WorkflowTemplate> getWorkflowTemplates() throws QueryException;

    /**
     * Gets the workflow templates.
     *
     * @param start the start
     * @param numOfRows the num of rows
     * @return the workflow templates
     */
    public List<WorkflowTemplate> getWorkflowTemplates(int start, int numOfRows) throws QueryException;

    /**
     * Gets the tool with name.
     *
     * @param toolName the tool name
     * @return the tool with name
     */
    public Tool getToolWithName(String toolName) throws QueryException;
    
    /**
     * Get the list of tools with the specified type.
     * 
     * @param toolType
     * @return
     * @throws QueryException
     */
    public List<Tool> getToolsWithType(ToolType toolType) throws QueryException;
    
    /**
     * Checks if is valid user login.
     *
     * @param username the username
     * @param password the password
     * @return true, if is valid user login
     * @throws QueryException the query exception
     */
    public boolean isValidUserLogin(String username, String password) throws QueryException;
    
    /**
     * Checks if is person exists.
     *
     * @param firstName the first name
     * @param lastName the last name
     * @return true, if is person exists
     * @throws QueryException the query exception
     */
    public boolean isPersonExists(String firstName, String lastName) throws QueryException;
    
    /**
     * Checks if is username exists.
     *
     * @param userName the user name
     * @return true, if is username exists
     * @throws QueryException the query exception
     */
    public boolean isUsernameExists(String userName) throws QueryException;
    
    /**
     * Adds the person.
     *
     * @param person the person
     * @throws QueryException the query exception
     */
    public void addPerson(Person person) throws QueryException;
    
    /**
     * Adds the user.
     *
     * @param user the user
     * @throws QueryException the query exception
     */
    public void addUser(User user) throws QueryException;
    
    /**
     * Gets the project by id.
     *
     * @param projectId the project id
     * @return the project by id
     */
    public Project getProjectById(Long projectId) throws QueryException;
    
    /**
     * Registers a dataset. Returns the id of the newly created dataset.
     *
     * @param dataset the dataset
     * @return the id of the newly created dataset
     */
    public WorkbenchDataset addDataset(WorkbenchDataset dataset) throws QueryException;
    
    /**
     * Returns all Persons.
     *
     * @return gets all Persons
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
     * @param id the id
     * @return the user by id
     */
    public User getUserById(int id);
    
    /**
     * Gets the user by name.
     *
     * @param name the name
     * @param start the starting record
     * @param numRows the totalRows
     * @param operation the operation
     * @return the user by name
     */
    public List<User> getUserByName(String name, int start, int numOfRows, Operation op) throws QueryException;
    
    /**
     * Delete user.
     *
     * @param user the user
     * @throws QueryException the query exception
     */
    public void deleteUser(User user) throws QueryException;
    
    /**
     * Returns all Persons.
     *
     * @return gets all Persons
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
     * @param id the id
     * @return the person by id
     */
    public Person getPersonById(int id);
    
    /**
     * Delete person.
     *
     * @param person the person
     * @throws QueryException the query exception
     */
    public void deletePerson(Person person) throws QueryException; 
    
    /**
     * Returns the project last accessed by the user.
     * @param userId
     * @return
     * @throws QueryException
     */
    public Project getLastOpenedProject(Integer userId) throws QueryException;
    
    /**
     * Returns a list of {@link WorkbenchDataset} records by project id.
     *
     * @param projectId the project id
     * @param start the start
     * @param numOfRows the num of rows
     * @return the list of {@link WorkbenchDataset}s
     * @throws QueryException the query exception
     */
    public List<WorkbenchDataset> getWorkbenchDatasetByProjectId(Long projectId, int start, int numOfRows) throws QueryException;
    
    /**
     * Returns the number of {@link WorkbenchDataset} records by project id.
     *
     * @param projectId the project id
     * @return the number of {@link WorkbenchDataset} records
     * @throws QueryException the query exception
     */
    public Long countWorkbenchDatasetByProjectId(Long projectId) throws QueryException;
}
