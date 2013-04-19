package org.generationcp.middleware.v2.factory;


import org.generationcp.middleware.pojos.Study;
import org.generationcp.middleware.v2.pojos.DmsProject;

public class ProjectFactory {
	
	private static final ProjectFactory instance = new ProjectFactory();
	
	public static ProjectFactory getInstance() {
		return instance;
	}
	
	public DmsProject createProject(Study study) { 
		DmsProject project = null;

		if (study != null) {
			project = new DmsProject();
			mapStudytoProject(study, project);
		}
		
		return project;
	}
	
	private void mapStudytoProject(Study study, DmsProject project) {
		project.setProjectId(Integer.valueOf(study.getId().intValue()));
		project.setName(study.getName());
		project.setDescription(study.getTitle());
	}
	
}
