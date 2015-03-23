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
package org.generationcp.middleware.operation.builder;


import org.generationcp.middleware.domain.dms.Experiment;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.dms.DmsProject;

public class StudyBuilder extends Builder {

	public StudyBuilder(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	public Study createStudy(int studyId) throws MiddlewareQueryException {
		Study study = null;
		DmsProject project = getDmsProjectDao().getById(studyId);
		if (project != null) {
			study = createStudy(project);
		}
		return study;
	}
	
	public Study createStudy(int studyId, boolean hasVariabletype) throws MiddlewareQueryException {
		Study study = null;
		DmsProject project = getDmsProjectDao().getById(studyId);
		if (project != null) {
			study = createStudy(project, hasVariabletype);
		}
		return study;
	}

	public Study createStudy(DmsProject project) throws MiddlewareQueryException {
		Study study = new Study();
		study.setId(project.getProjectId());
		
		VariableTypeList variableTypes = getVariableTypeBuilder().create(project.getProperties());
		VariableTypeList conditionVariableTypes = variableTypes.getFactors();
		VariableTypeList constantVariableTypes = variableTypes.getVariates();
		
		Experiment experiment = getExperimentBuilder().buildOne(project.getProjectId(), TermId.STUDY_EXPERIMENT, variableTypes);
		
		study.setConditions(getStudyVariableBuilder().create(project, experiment, conditionVariableTypes));
		study.setConstants(getStudyVariableBuilder().create(project, experiment, constantVariableTypes));
		
		return study;
	}
	
	public Study createStudy(DmsProject project, boolean hasVariableType) throws MiddlewareQueryException {
		Study study = new Study();
		study.setId(project.getProjectId());
		
		VariableTypeList variableTypes = getVariableTypeBuilder().create(project.getProperties());
		VariableTypeList conditionVariableTypes = variableTypes.getFactors();
		VariableTypeList constantVariableTypes = variableTypes.getVariates();
		
		Experiment experiment = getExperimentBuilder().buildOne(project.getProjectId(), TermId.STUDY_EXPERIMENT, variableTypes, hasVariableType);
		
		study.setConditions(getStudyVariableBuilder().create(project, experiment, conditionVariableTypes, hasVariableType));
		study.setConstants(getStudyVariableBuilder().create(project, experiment, constantVariableTypes, hasVariableType));
		
		return study;
	}
}
