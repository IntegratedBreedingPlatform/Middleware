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

import org.generationcp.middleware.domain.dms.*;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ProjectProperty;

import java.util.List;

public class StudyVariableBuilder extends Builder {

	protected StudyVariableBuilder(HibernateSessionProvider sessionProviderForLocal,
			                  HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	public VariableList create(DmsProject project, Experiment experiment, VariableTypeList variableTypes) throws MiddlewareQueryException {
		VariableList variables = new VariableList();
		
		for (VariableType variableType : variableTypes.getVariableTypes()) {
			variables.add(createVariable(variableType, project, experiment));
		}
		
		return variables.sort();
	}
	
	public VariableList create(DmsProject project, Experiment experiment, VariableTypeList variableTypes, boolean hasVariabletype) throws MiddlewareQueryException {
		VariableList variables = new VariableList();
		
		for (VariableType variableType : variableTypes.getVariableTypes()) {
			Variable var = createVariable(variableType, project, experiment, hasVariabletype);
			if(var.getVariableType() != null) {
                variables.add(var);
            }
		}
		
		return variables.sort();
	}

	private Variable createVariable(VariableType variableType, DmsProject project, Experiment experiment) throws MiddlewareQueryException {
		Variable variable = new Variable();
		variable.setVariableType(variableType);

		if (storedIn(variableType, TermId.STUDY_NAME_STORAGE)) {
			variable.setValue(project.getName());
		}
		else if (storedIn(variableType, TermId.STUDY_TITLE_STORAGE)) {
			variable.setValue(project.getDescription());
		}
		else if (storedIn(variableType, TermId.STUDY_INFO_STORAGE)) {
			variable.setValue(getPropertyValue(variableType.getId(), project.getProperties()));//revert - it needs to set the cvterm_id always 
		}
		else {
			if(experiment!=null) {
				Variable factor = experiment.getFactors().findById(variableType.getId());
				if (factor != null) {
					variable.setValue(factor.getValue());
				}
				else {
					Variable variate = experiment.getVariates().findById(variableType.getId());
					if (variate != null) {
						variable.setValue(variate.getValue());
					}
				}
			}
		}
		return variable;
	}
	
	private Variable createVariable(VariableType variableType, DmsProject project, Experiment experiment, boolean hasVariableType) throws MiddlewareQueryException {
		Variable variable = new Variable();
		variable.setVariableType(variableType);
		
		if(variableType == null && hasVariableType == false){
			return variable;
		}
		
		if (storedIn(variableType, TermId.STUDY_NAME_STORAGE)) {
			variable.setValue(project.getName());
		}
		else if (storedIn(variableType, TermId.STUDY_TITLE_STORAGE)) {
			variable.setValue(project.getDescription());
		}
		else if (storedIn(variableType, TermId.STUDY_INFO_STORAGE)) {
			variable.setValue(getPropertyValue(variableType.getId(), project.getProperties()));//revert - it needs to set the cvterm_id always 
		}
		else {
			if(experiment!=null) {
				Variable factor = experiment.getFactors().findById(variableType.getId());
				if (factor != null) {
					variable.setValue(factor.getValue());
				}
				else {
					Variable variate = experiment.getVariates().findById(variableType.getId());
					if (variate != null) {
						variable.setValue(variate.getValue());
					}
				}
			}
		}
		return variable;
	}

	private boolean storedIn(VariableType variableType, TermId termId) {
		if (variableType == null){
			return false;
		}
		return variableType.getStandardVariable().getStoredIn().getId() == termId.getId();
	}

	private boolean hasId(VariableType variableType, TermId termId) {
		return variableType.getId() == termId.getId();
	}
	
	private String getPropertyValue(int id, List<ProjectProperty> properties) {
		for (ProjectProperty property : properties) {
			if (property.getTypeId() == id) {
				return property.getValue();
			}
		}
		return null;
	}
}
