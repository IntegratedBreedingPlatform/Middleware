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

import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.Experiment;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ProjectProperty;

import java.util.List;

public class StudyVariableBuilder extends Builder {

	protected StudyVariableBuilder(final HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	public VariableList create(final DmsProject project, final Experiment experiment, final VariableTypeList variableTypes) throws MiddlewareQueryException {
		final VariableList variables = new VariableList();

		for (final DMSVariableType variableType : variableTypes.getVariableTypes()) {
			variables.add(this.createVariable(variableType, project, experiment));
		}

		return variables.sort();
	}

	public VariableList create(final DmsProject project, final Experiment experiment, final VariableTypeList variableTypes, final boolean hasVariabletype)
			throws MiddlewareQueryException {
		final VariableList variables = new VariableList();

		for (final DMSVariableType variableType : variableTypes.getVariableTypes()) {
			final Variable var = this.createVariable(variableType, project, experiment, hasVariabletype);
			if (var.getVariableType() != null) {
				variables.add(var);
			}
		}

		return variables.sort();
	}

	private Variable createVariable(final DMSVariableType variableType, final DmsProject project, final Experiment experiment)
			throws MiddlewareQueryException {
		return createVariable(variableType,project,experiment,false);
	}

	private Variable createVariable(final DMSVariableType variableType, final DmsProject project, final Experiment experiment, final boolean hasVariableType)
			throws MiddlewareQueryException {
		final Variable variable = new Variable();
		variable.setVariableType(variableType);

		if (variableType == null && hasVariableType == false) {
			return variable;
		}

		final String projectPropValue = getPropertyValue(variableType.getId(), project.getProperties());
		if (projectPropValue != null) {
			variable.setValue(projectPropValue);
		} else if (experiment != null) {
			final Variable factor = experiment.getFactors().findById(variableType.getId());
			if (factor != null) {
				variable.setValue(factor.getValue());
			} else {
				final Variable variate = experiment.getVariates().findById(variableType.getId());
				if (variate != null) {
					variable.setValue(variate.getValue());
				}
			}
		}

		return variable;
	}

	private String getPropertyValue(final int variableId, final List<ProjectProperty> properties) {
		for (final ProjectProperty property : properties) {
			if (property.getVariableId() .equals(variableId)) {
				return property.getValue();
			}
		}
		return null;
	}
}
