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

    List<Project> getProjects();

    List<Project> getProjects(int start, int numOfRows);

    Project saveOrUpdateProject(Project project);

    void deleteProject(Project project);

    List<WorkflowTemplate> getWorkflowTemplates();

    List<WorkflowTemplate> getWorkflowTemplates(int start, int numOfRows);

    Tool getToolWithName(String toolName);
}
