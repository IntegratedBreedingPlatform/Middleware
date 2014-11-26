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

import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.domain.dms.StudyValues;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.dms.DmsProject;

public class ProjectSaver extends Saver {

	public ProjectSaver(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	public DmsProject save(DmsProject project) throws MiddlewareQueryException{
		requireLocalDatabaseInstance();
        DmsProjectDao projectDao = getDmsProjectDao();
        Integer generatedId = projectDao.getNegativeId("projectId");
        project.setProjectId(generatedId);
        return projectDao.save(project);
	}

	public DmsProject create(StudyValues studyValues)  throws MiddlewareException{ 
		DmsProject project = null;
		
		if (studyValues != null) {
			project = new DmsProject();
			Integer studyId = null;
			String name = getStringValue(studyValues, TermId.STUDY_NAME.getId()) ; 
			String description = getStringValue(studyValues, TermId.STUDY_TITLE.getId());
			mapStudytoProject(studyId, name, description, project);
		}
		
		return project;
	}
	
	private void mapStudytoProject(Integer id, String name, String description, DmsProject project) throws MiddlewareException{
		StringBuffer errorMessage = new StringBuffer("");

		project.setProjectId(id);
		
		if (name != null && !name.equals("")){
			project.setName(name);
		} else {
			errorMessage.append("\nname is null");
		}
			
		if (description != null && !description.equals("")){
			project.setDescription(description);
		} else {
			errorMessage.append("\nprojectKey is null");
		}
		
		if (errorMessage.length() > 0){
			throw new MiddlewareException(errorMessage.toString());
		}

	}
	
	private String getStringValue(StudyValues studyValues, int termId) {
		return studyValues.getVariableList().findById(termId).getValue();
	}
	
	/**
	 * Saves a folder. Creates an entry in project and project_relationship
	 */
	public DmsProject saveFolder(int parentId, String name, String description) throws Exception{
        requireLocalDatabaseInstance();
        DmsProject project = new DmsProject();
        mapStudytoProject(null, name, description, project);
        
        try {
            project = save(project);
            getProjectRelationshipSaver().saveProjectParentRelationship(project, parentId, false);  
        } catch (Exception e) {
            throw e;
        }
        return project;

    }
	
}
