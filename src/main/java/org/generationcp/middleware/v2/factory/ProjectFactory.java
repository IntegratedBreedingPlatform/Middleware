package org.generationcp.middleware.v2.factory;


import org.generationcp.middleware.pojos.Study;
import org.generationcp.middleware.v2.domain.StudyDetails;
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
			mapStudytoProject(study.getId(), study.getName(), 
					study.getTitle(), project);
		}
		
		return project;
	}
	
	public DmsProject createProject(StudyDetails studyDetails) { 
		DmsProject project = null;

		if (studyDetails != null) {
			project = new DmsProject();
			mapStudytoProject(studyDetails.getId(), studyDetails.getName(), 
					studyDetails.getTitle(), project);
		}
		
		return project;
	}

	private void mapStudytoProject(Integer id, String name, String description, DmsProject project) {
		project.setProjectId(id);
		project.setName(name);
		project.setDescription(description);
	}

}
