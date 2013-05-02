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

import java.util.List;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.Study;
import org.generationcp.middleware.v2.dao.DmsProjectDao;
import org.generationcp.middleware.v2.dao.ExperimentDao;
import org.generationcp.middleware.v2.dao.ProjectPropertyDao;
import org.generationcp.middleware.v2.dao.ProjectRelationshipDao;
import org.generationcp.middleware.v2.factory.ProjectFactory;
import org.generationcp.middleware.v2.factory.ProjectPropertyFactory;
import org.generationcp.middleware.v2.factory.ProjectRelationshipFactory;
import org.generationcp.middleware.v2.pojos.DmsProject;
import org.generationcp.middleware.v2.pojos.ExperimentModel;
import org.generationcp.middleware.v2.pojos.ProjectProperty;
import org.generationcp.middleware.v2.pojos.ProjectRelationship;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Saves a study (the corresponding Project, ProjectProperty,
 * ProjectRelationship and Experiment entries) to the database.
 * 
 * @author Joyce Avestro
 * 
 */
public class StudySaver extends Saver{
	
	public StudySaver(HibernateSessionProvider sessionProviderForLocal,
            HibernateSessionProvider sessionProviderForCentral) {
				super(sessionProviderForLocal, sessionProviderForCentral);
	}

	public Integer saveStudy(Study study, DmsProject parent) throws Exception{
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;
        Integer id = null;

        try {
            trans = session.beginTransaction();
            
            // Save project
            DmsProject project = ProjectFactory.getInstance().createProject(study);
            DmsProjectDao projectDao = getDmsProjectDao();
            Integer generatedId = projectDao.getNegativeId("projectId");
            project.setProjectId(generatedId);
            DmsProject savedProject = projectDao.save(project);
            
            // Save project properties
            List<ProjectProperty> properties = ProjectPropertyFactory.getInstance().
            										createProjectProperties(study, project);
            ProjectPropertyDao projectPropertyDao = getProjectPropertyDao();
            
            for (ProjectProperty property : properties){
                generatedId = projectPropertyDao.getNegativeId("projectPropertyId");
                property.setProjectPropertyId(generatedId);
                 property.setProject(savedProject);
                 projectPropertyDao.save(property);
            }
            savedProject.setProperties(properties);
           
            // Save the relationship to parent and add relationship is_study
            if (parent == null){
            	parent = projectDao.getById(DmsProject.SYSTEM_FOLDER_ID); // Make the new study a root study 
            }
            List<ProjectRelationship> relationships = ProjectRelationshipFactory.getInstance().
            											createProjectRelationship(study, parent);
            ProjectRelationshipDao projectRelationshipDao = getProjectRelationshipDao();
            
            for (ProjectRelationship relationship : relationships){
                generatedId = projectRelationshipDao.getNegativeId("projectRelationshipId");
                relationship.setProjectRelationshipId(generatedId);
                relationship.setObjectProject(savedProject);
                relationship.setSubjectProject(parent);
                projectRelationshipDao.save(relationship);
            }
            savedProject.setRelatedTos(relationships);
            
            
            //TODO Add an entry to nd_experiment
            ExperimentDao experimentDao = getExperimentDao();
            generatedId = experimentDao.getNegativeId("ndExperimentId");
            ExperimentModel experiment = new ExperimentModel();
            
            
            
            id = savedProject.getProjectId();

            trans.commit();
            
        } catch (Exception e) {
        	rollbackTransaction(trans);
            throw e;
        }
        return id;
		
	}

}
