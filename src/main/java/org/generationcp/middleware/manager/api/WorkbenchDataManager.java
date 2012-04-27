package org.generationcp.middleware.manager.api;

import java.util.List;

import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.WorkflowTemplate;

/**
 * This is the API used by the Workbench to retrieve Workbench project information. 
 * 
 */
public interface WorkbenchDataManager {

    List<Project> getProjects();

    List<Project> getProjects(int start, int numOfRows);

    Project saveOrUpdateProject(Project project);

    void deleteProject(Project project);
    
    List<WorkflowTemplate> getWorkflowTemplates();
    
    List<WorkflowTemplate> getWorkflowTemplates(int start, int numOfRows);
    
    Tool getToolWithName(String toolName);
}
