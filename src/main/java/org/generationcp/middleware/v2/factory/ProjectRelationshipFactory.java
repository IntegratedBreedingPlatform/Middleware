package org.generationcp.middleware.v2.factory;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.pojos.Study;
import org.generationcp.middleware.v2.domain.TermId;
import org.generationcp.middleware.v2.pojos.DmsProject;
import org.generationcp.middleware.v2.pojos.ProjectRelationship;

public class ProjectRelationshipFactory {
	
	private static final ProjectRelationshipFactory instance = new ProjectRelationshipFactory();
	
	public static ProjectRelationshipFactory getInstance() {
		return instance;
	}
	
	public List<ProjectRelationship> createProjectRelationship(Study study, DmsProject parent) throws MiddlewareException {
		ArrayList<ProjectRelationship> relationships = null;

		if (study != null) {
			relationships = mapStudyToRelationships(study, parent);
		}
		return relationships;
	}
	
	private ArrayList<ProjectRelationship>  mapStudyToRelationships(Study study, DmsProject parent) throws MiddlewareException {
		ArrayList<ProjectRelationship> relationships = new ArrayList<ProjectRelationship>();
		
		DmsProject project = ProjectFactory.getInstance().createProject(study);
		
		 // parent
		 relationships.add(new ProjectRelationship(0, project, parent, TermId.HAS_PARENT_FOLDER.getId()));
		 
		 // is study
		 relationships.add(new ProjectRelationship(0, project, parent, TermId.IS_STUDY.getId()));

		return relationships;
		
	}

}
