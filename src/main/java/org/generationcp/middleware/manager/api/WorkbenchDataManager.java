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

import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Tool;
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
    public List<Project> getProjects();

    /**
     * Gets the projects.
     *
     * @param start the start
     * @param numOfRows the num of rows
     * @return the projects
     */
    public List<Project> getProjects(int start, int numOfRows);

    /**
     * Save or update project.
     *
     * @param project the project
     * @return the project
     */
    public Project saveOrUpdateProject(Project project);

    /**
     * Delete project.
     *
     * @param project the project
     */
    public void deleteProject(Project project);

    /**
     * Gets the workflow templates.
     *
     * @return the workflow templates
     */
    public List<WorkflowTemplate> getWorkflowTemplates();

    /**
     * Gets the workflow templates.
     *
     * @param start the start
     * @param numOfRows the num of rows
     * @return the workflow templates
     */
    public List<WorkflowTemplate> getWorkflowTemplates(int start, int numOfRows);

    /**
     * Gets the tool with name.
     *
     * @param toolName the tool name
     * @return the tool with name
     */
    public Tool getToolWithName(String toolName);
    
    
    /**
     * Gets the project by id.
     *
     * @param projectId the project id
     * @return the project by id
     */
    public Project getProjectById(Long projectId);
    
}
