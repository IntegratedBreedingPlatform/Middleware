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

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.helper.VariableInfo;
import org.generationcp.middleware.pojos.dms.ProjectProperty;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

public class VariableInfoBuilder {

	public Set<VariableInfo> create(List<ProjectProperty> properties) {
		final Monitor monitor = MonitorFactory.start("OpenTrial.bms.middleware.VariableInfoBuilder.create");
		try {

			Set<VariableInfo> variableDefs = new HashSet<>();
			for (ProjectProperty property : properties) {
				if (this.isStandardVariableType(property)) {
					variableDefs.add(this.createVariableDef(property, this.filterByRank(properties, property.getRank())));
				}
			}
			return variableDefs;
		} finally {
			monitor.stop();
		}
	}

	private VariableInfo createVariableDef(ProjectProperty stdVariableProperty, Set<ProjectProperty> properties) {

		ProjectProperty localNameProperty = this.findLocalNameProperty(stdVariableProperty.getValue(), properties);
		ProjectProperty localDescriptionProperty = this.findLocalDescriptionProperty(properties);
		ProjectProperty treatmentLabelProperty = this.findTreatmentLabelProperty(properties);

		VariableInfo variableDef = new VariableInfo();
		variableDef.setLocalName(localNameProperty == null ? null : localNameProperty.getValue());
		variableDef.setLocalDescription(localDescriptionProperty == null ? null : localDescriptionProperty.getValue());
		variableDef.setStdVariableId(Integer.parseInt(stdVariableProperty.getValue()));
		if (properties.iterator().hasNext()) {
			variableDef.setRank(properties.iterator().next().getRank());
		}
		if (treatmentLabelProperty != null) {
			variableDef.setTreatmentLabel(treatmentLabelProperty.getValue());
		}

		for (ProjectProperty property : properties) {
			VariableType varType = VariableType.getById(property.getTypeId());
			if (varType != null) {
				variableDef.setRole(varType.getRole());
				variableDef.setVariableType(varType);
			}
		}

		return variableDef;
	}

	private ProjectProperty findLocalDescriptionProperty(Set<ProjectProperty> properties) {
		for (ProjectProperty property : properties) {
			if (this.isLocalDescriptionType(property)) {
				return property;
			}
		}
		return null;
	}

	private ProjectProperty findTreatmentLabelProperty(Set<ProjectProperty> properties) {
		for (ProjectProperty property : properties) {
			if (this.isMultiFactorialType(property)) {
				return property;
			}
		}
		return null;
	}

	private ProjectProperty findLocalNameProperty(String stdVariableIdStr, Set<ProjectProperty> properties) {
		Integer stdVariableId = Integer.parseInt(stdVariableIdStr);
		for (ProjectProperty property : properties) {
			if (this.isStudyInformationType(property)) {
				return property;
			}

			if (!this.isLocalDescriptionType(property) && !this.isStandardVariableType(property) && !this.isMultiFactorialType(property)) {
				if (!stdVariableId.equals(property.getTypeId())) {
					return property;
				}
			}
		}
		return null;
	}

	private boolean isStudyInformationType(ProjectProperty property) {
		return TermId.STUDY_INFORMATION.getId() == property.getTypeId();
	}

	private boolean isLocalDescriptionType(ProjectProperty property) {
		return TermId.VARIABLE_DESCRIPTION.getId() == property.getTypeId();
	}

	private boolean isStandardVariableType(ProjectProperty property) {
		return TermId.STANDARD_VARIABLE.getId() == property.getTypeId();
	}

	private boolean isMultiFactorialType(ProjectProperty property) {
		return TermId.MULTIFACTORIAL_INFO.getId() == property.getTypeId();
	}

	private Set<ProjectProperty> filterByRank(List<ProjectProperty> properties, int rank) {
		Set<ProjectProperty> filteredProperties = new HashSet<>();
		for (ProjectProperty property : properties) {
			if (Objects.equals(property.getRank(), rank)) {
				filteredProperties.add(property);
			}
		}
		return filteredProperties;
	}
}
