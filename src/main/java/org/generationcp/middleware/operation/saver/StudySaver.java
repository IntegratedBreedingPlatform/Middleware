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


import org.generationcp.middleware.domain.dms.StudyValues;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.dms.DmsProject;

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

	/**
	 * Saves a study. Creates an entry in project, projectprop,
	 * project_relationship, nd_experiment and nd_experiment_project tables.
	 */
	public DmsProject saveStudy(int parentId, VariableTypeList variableTypeList, StudyValues studyValues) throws Exception{
        requireLocalDatabaseInstance();
        DmsProject project = getProjectSaver().create(studyValues);
        
        try {
            project = getProjectSaver().save(project);
            getProjectPropertySaver().saveProjectProperties(project, variableTypeList);
            getProjectRelationshipSaver().saveProjectParentRelationship(project, parentId, true);
            getExperimentModelSaver().addExperiment(project.getProjectId(), studyValues);            
        } catch (Exception e) {
            throw e;
        }
        return project;

    }


}
