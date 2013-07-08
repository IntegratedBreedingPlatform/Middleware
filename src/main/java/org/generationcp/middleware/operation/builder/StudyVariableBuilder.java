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

import java.util.List;

import org.generationcp.middleware.domain.Experiment;
import org.generationcp.middleware.domain.TermId;
import org.generationcp.middleware.domain.Variable;
import org.generationcp.middleware.domain.VariableList;
import org.generationcp.middleware.domain.VariableType;
import org.generationcp.middleware.domain.VariableTypeList;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ProjectProperty;

public class StudyVariableBuilder extends Builder {

	protected StudyVariableBuilder(HibernateSessionProvider sessionProviderForLocal,
			                  HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	public VariableList create(DmsProject project, Experiment experiment, VariableTypeList variableTypes) {
		VariableList variables = new VariableList();
		
		for (VariableType variableType : variableTypes.getVariableTypes()) {
			variables.add(createVariable(variableType, project, experiment));
		}
		
		return variables.sort();
	}

	private Variable createVariable(VariableType variableType, DmsProject project, Experiment experiment) {
		Variable variable = new Variable();
		variable.setVariableType(variableType);

		if (storedIn(variableType, TermId.STUDY_NAME_STORAGE)) {
			variable.setValue(project.getName());
		}
		else if (storedIn(variableType, TermId.STUDY_TITLE_STORAGE)) {
			variable.setValue(project.getDescription());
		}
		else if (storedIn(variableType, TermId.STUDY_INFO_STORAGE)) {
			variable.setValue(getPropertyValue(variableType.getId(), project.getProperties()));
		}
		else {
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
		return variable;
	}

	private boolean storedIn(VariableType variableType, TermId termId) {
		return variableType.getStandardVariable().getStoredIn().getId() == termId.getId();
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
