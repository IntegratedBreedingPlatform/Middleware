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
package org.generationcp.middleware.v2.domain.saver;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.v2.dao.DmsProjectDao;
import org.generationcp.middleware.v2.dao.ProjectRelationshipDao;
import org.generationcp.middleware.v2.domain.TermId;
import org.generationcp.middleware.v2.pojos.DmsProject;
import org.generationcp.middleware.v2.pojos.ProjectRelationship;

public class ProjectRelationshipSaver extends Saver {

	public ProjectRelationshipSaver(
			HibernateSessionProvider sessionProviderForLocal,
			HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	public void saveProjectParentRelationship(DmsProject project, int parentId) throws MiddlewareQueryException, MiddlewareException{
		requireLocalDatabaseInstance();
        DmsProjectDao projectDao = getDmsProjectDao();
        
        DmsProject parent = projectDao.getById(parentId); 
        if (parent == null){
        	// Make the new study a root study
        	parent = projectDao.getById(DmsProject.SYSTEM_FOLDER_ID);  
        }
        List<ProjectRelationship> relationships = mapProjectParentRelationships(project, parent);

        ProjectRelationshipDao projectRelationshipDao = getProjectRelationshipDao();
        int index = 0;
        for (ProjectRelationship relationship : relationships){
        	Integer generatedId = projectRelationshipDao.getNegativeId("projectRelationshipId");
            relationship.setProjectRelationshipId(generatedId);
            relationship.setObjectProject(project);
            relationship.setSubjectProject(parent);
            projectRelationshipDao.save(relationship);
            relationships.set(index, relationship);
            index++;            
        }
        project.setRelatedTos(relationships);
	}	
	
	private List<ProjectRelationship> mapProjectParentRelationships(DmsProject project, DmsProject parent) throws MiddlewareException {
		ArrayList<ProjectRelationship> relationships = new ArrayList<ProjectRelationship>();
		
		if (project != null) {
			 relationships.add(new ProjectRelationship(0, project, parent, TermId.HAS_PARENT_FOLDER.getId()));
			 relationships.add(new ProjectRelationship(0, project, parent, TermId.IS_STUDY.getId()));
		}
		return relationships;
	}

	
}
