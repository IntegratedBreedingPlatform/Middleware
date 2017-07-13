/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.operation.builder;

import java.util.List;

import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.Experiment;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ProjectProperty;

public class StudyVariableBuilder extends Builder {

	protected StudyVariableBuilder(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	public VariableList create(DmsProject project, Experiment experiment, VariableTypeList variableTypes) throws MiddlewareQueryException {
		VariableList variables = new VariableList();

		for (DMSVariableType variableType : variableTypes.getVariableTypes()) {
			variables.add(this.createVariable(variableType, project, experiment));
		}

		return variables.sort();
	}

	public VariableList create(DmsProject project, Experiment experiment, VariableTypeList variableTypes, boolean hasVariabletype)
			throws MiddlewareQueryException {
		VariableList variables = new VariableList();

		for (DMSVariableType variableType : variableTypes.getVariableTypes()) {
			Variable var = this.createVariable(variableType, project, experiment, hasVariabletype);
			if (var.getVariableType() != null) {
				variables.add(var);
			}
		}

		return variables.sort();
	}

	private Variable createVariable(DMSVariableType variableType, DmsProject project, Experiment experiment)
			throws MiddlewareQueryException {
		return createVariable(variableType,project,experiment,false);
	}

	private Variable createVariable(DMSVariableType variableType, DmsProject project, Experiment experiment, boolean hasVariableType)
			throws MiddlewareQueryException {
		Variable variable = new Variable();
		variable.setVariableType(variableType);

		if (variableType == null && hasVariableType == false) {
			return variable;
		}

		if (variableType.getId() == TermId.STUDY_NAME.getId()) {
			variable.setValue(project.getName());
		} else if (variableType.getId() == TermId.STUDY_TITLE.getId()) {
			variable.setValue(project.getDescription());
		} else {
			String projectPropValue = getPropertyValue(variableType.getId(), project.getProperties());
			if (projectPropValue != null) {
				variable.setValue(projectPropValue);
			} else if (experiment != null) {
				Variable factor = experiment.getFactors().findById(variableType.getId());
				if (factor != null) {
					variable.setValue(factor.getValue());
				} else {
					Variable variate = experiment.getVariates().findById(variableType.getId());
					if (variate != null) {
						variable.setValue(variate.getValue());
					}
				}
			}
		}
		return variable;
	}

	private String getPropertyValue(int id, List<ProjectProperty> properties) {
		for (ProjectProperty property : properties) {
			if (property.getVariableId() == id) {
				return property.getValue();
			}
		}
		return null;
	}
}
