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
package org.generationcp.middleware.v2.factory;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.pojos.Study;
import org.generationcp.middleware.v2.domain.CVTermId;
import org.generationcp.middleware.v2.domain.StudyPropertyConstants;
import org.generationcp.middleware.v2.pojos.DmsProject;
import org.generationcp.middleware.v2.pojos.ProjectProperty;

import static org.generationcp.middleware.v2.domain.StudyPropertyConstants.*;

/**
 * Creates ProjectProperty entries given a Study
 * 
 * @author Joyce Avestro
 *
 */
public class ProjectPropertyFactory {
	
	private static final ProjectPropertyFactory instance = new ProjectPropertyFactory();
	
	public static ProjectPropertyFactory getInstance() {
		return instance;
	}
	
	public List<ProjectProperty> createProjectProperties(Study study, DmsProject project) throws MiddlewareException{
		ArrayList<ProjectProperty> properties = null;
		if (study != null) {
			properties = mapStudyToProperties(study, project);
		}
		return properties;
	}
		
	private ArrayList<ProjectProperty>  mapStudyToProperties(Study study, DmsProject project) throws MiddlewareException{
		ArrayList<ProjectProperty> properties = new ArrayList<ProjectProperty>();
		
		String errorMessage = "";
		
		if ( study.getProjectKey() != null){
			properties.add(new ProjectProperty(0, project, CVTermId.PM_KEY.getId(), study.getProjectKey().toString(), 0));
		} else {
			errorMessage += "\nprojectKey is null";
		}

		if ( study.getObjective() != null){
			properties.add(new ProjectProperty(0, project, CVTermId.STUDY_OBJECTIVE.getId(), study.getObjective(), 1));
		} else {
			errorMessage += "\nobjective is null";
		}

		if ( study.getPrimaryInvestigator() != null){
			properties.add(new ProjectProperty(0, project, CVTermId.PI_ID.getId(), study.getPrimaryInvestigator().toString(), 2));
		}

		if ( study.getType() != null){
		properties.add(new ProjectProperty(0, project, CVTermId.STUDY_TYPE.getId(), study.getType(), 3));
		} else {
			errorMessage += "\ntype is null";
		}

		if ( study.getStartDate() != null){
		properties.add(new ProjectProperty(0, project, CVTermId.START_DATE.getId(), study.getStartDate().toString(), 4));
		} else {
			errorMessage += "\nstartDate is null";
		}

		if ( study.getEndDate() != null){
			properties.add(new ProjectProperty(0, project, CVTermId.END_DATE.getId(), study.getEndDate().toString(), 5));
		} else {
			errorMessage += "\nendDate is null";
		}

		if ( study.getUser() != null){
			createPropertiesFromStudyField(project, USER_ID, study.getUser().toString(), 6);
		} 
		
		if ( study.getStatus() != null){
			properties.add(new ProjectProperty(0, project, CVTermId.STUDY_IP.getId(), study.getStatus().toString(), 7));
		}
		
		if ( study.getCreationDate() != null){
			properties.add(new ProjectProperty(0, project, CVTermId.CREATION_DATE.getId(), study.getCreationDate().toString(), 8));
		} 
		
		if (!errorMessage.equals("")){
			throw new MiddlewareException(errorMessage);
		}
		
		return properties;
		
	}
	
	private List<ProjectProperty> createPropertiesFromStudyField(DmsProject project, StudyPropertyConstants field, String value, int rank) {
		List<ProjectProperty> properties = new ArrayList<ProjectProperty>();
		
		properties.add(new ProjectProperty(0, project, CVTermId.STUDY_INFORMATION.getId(), field.getName(), rank));
		properties.add(new ProjectProperty(0, project, CVTermId.VARIABLE_DESCRIPTION.getId(), field.getDescription(), rank));
		properties.add(new ProjectProperty(0, project, CVTermId.STANDARD_VARIABLE.getId(), field.getCvTermId().toString(), rank));
		properties.add(new ProjectProperty(0, project, field.getCvTermId(), value, rank));
		
		return properties;
	}

	
}
