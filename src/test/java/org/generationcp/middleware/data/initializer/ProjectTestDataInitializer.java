package org.generationcp.middleware.data.initializer;

import java.util.Date;

import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;

public class ProjectTestDataInitializer {

	public Project createProject() {
		final Project project = new Project();
		project.setProjectId((long) 1);
		project.setProjectName("Project");
		project.setStartDate(new Date());
		project.setUniqueID("UNIQUEID00001");
		project.setLastOpenDate(new Date());
		project.setCropType(new CropType("maize"));
		return project;
	}
}