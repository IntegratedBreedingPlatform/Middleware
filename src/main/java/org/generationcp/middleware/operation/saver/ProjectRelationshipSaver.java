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
package org.generationcp.middleware.operation.saver;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.dao.dms.ProjectRelationshipDao;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ProjectRelationship;

public class ProjectRelationshipSaver extends Saver {

	public ProjectRelationshipSaver(
			HibernateSessionProvider sessionProviderForLocal,
			HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	public void saveProjectParentRelationship(DmsProject project, int parentId, boolean isAStudy) throws MiddlewareQueryException, MiddlewareException{
		requireLocalDatabaseInstance();
        DmsProjectDao projectDao = getDmsProjectDao();
        
        DmsProject parent = projectDao.getById(parentId); 
        if (parent == null){
        	// Make the new study a root study
        	parent = projectDao.getById(DmsProject.SYSTEM_FOLDER_ID);  
        }
        List<ProjectRelationship> relationships = mapProjectParentRelationships(project, parent, isAStudy);

        ProjectRelationshipDao projectRelationshipDao = getProjectRelationshipDao();
        int index = 0;
        for (ProjectRelationship relationship : relationships){
        	Integer generatedId = projectRelationshipDao.getNegativeId("projectRelationshipId");
            relationship.setProjectRelationshipId(generatedId);
            relationship.setObjectProject(parent);
            relationship.setSubjectProject(project);
            projectRelationshipDao.save(relationship);
            relationships.set(index, relationship);
            index++;            
        }
        project.setRelatedTos(relationships);
	}	
	
	private List<ProjectRelationship> mapProjectParentRelationships(DmsProject project, DmsProject parent, boolean isAStudy) throws MiddlewareException {
		ArrayList<ProjectRelationship> relationships = new ArrayList<ProjectRelationship>();
		
		if (project != null) {
			 if(isAStudy) {
				 relationships.add(new ProjectRelationship(0, project, parent, TermId.IS_STUDY.getId()));
			 } else {
				 relationships.add(new ProjectRelationship(0, project, parent, TermId.HAS_PARENT_FOLDER.getId()));
			 }
		}
		return relationships;
	}
	
	public void saveOrUpdateStudyToFolder(int studyId, int folderId) throws MiddlewareQueryException {
		setWorkingDatabase(studyId);
		ProjectRelationship relationship = getProjectRelationshipDao().getParentFolderRelationship(studyId);
		if (relationship != null && relationship.getObjectProject().getProjectId() != null 
				&& !relationship.getObjectProject().getProjectId().equals(folderId)) {
			relationship.setObjectProject(getDmsProjectDao().getById(folderId));
			getProjectRelationshipDao().saveOrUpdate(relationship);
		}
	}

	
}
