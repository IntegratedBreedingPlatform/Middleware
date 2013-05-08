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


import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.v2.domain.StudyValues;
import org.generationcp.middleware.v2.domain.VariableTypeList;
import org.generationcp.middleware.v2.pojos.DmsProject;

/**
 * Saves a study (the corresponding Project, ProjectProperty,
 * ProjectRelationship entries) to the database.
 * 
 * @author Joyce Avestro
 * 
 */
public class StudySaver extends Saver{
	
	public StudySaver(HibernateSessionProvider sessionProviderForLocal,
            HibernateSessionProvider sessionProviderForCentral) {
				super(sessionProviderForLocal, sessionProviderForCentral);
	}

	
	public Integer saveStudy(int parentId, VariableTypeList variableTypeList, StudyValues studyValues, DmsProject project) throws Exception{
        requireLocalDatabaseInstance();

        try {
            project = getProjectSaver().save(project);
            
            project.setProperties(getProjectPropertySaver().create(project, variableTypeList));

            getProjectRelationshipSaver().saveProjectParentRelationship(project, parentId);
            
        } catch (Exception e) {
            throw e;
        }
        return project.getProjectId();

    }


}
