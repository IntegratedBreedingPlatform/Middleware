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
import java.util.Set;

import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.helper.VariableInfo;
import org.generationcp.middleware.pojos.dms.ProjectProperty;

public class VariableInfoBuilder {

	public Set<VariableInfo> create(final List<ProjectProperty> properties) {
		final Set<VariableInfo> variableDefs = new HashSet<>();
		for (final ProjectProperty property : properties) {
			// TODO: workaround to avoid loading Names as Variables
			if (property.getTypeId() == null) {
				continue;
			}
			variableDefs.add(this.createVariableDef(property));
		}
		return variableDefs;
	}

	private VariableInfo createVariableDef(final ProjectProperty stdVariableProperty) {

		final String localNameProperty = stdVariableProperty.getAlias();
		final String localDescriptionProperty = stdVariableProperty.getDescription();

		final VariableInfo variableDef = new VariableInfo();

		variableDef.setLocalName(localNameProperty == null ? null : localNameProperty);
		variableDef.setLocalDescription(localDescriptionProperty == null ? null : localDescriptionProperty);
		variableDef.setStdVariableId(stdVariableProperty.getVariableId());
		variableDef.setRank(stdVariableProperty.getRank());
		if (TermId.MULTIFACTORIAL_INFO.getId() == stdVariableProperty.getTypeId()) {
			variableDef.setTreatmentLabel(stdVariableProperty.getValue());
		}

		final VariableType varType = VariableType.getById(stdVariableProperty.getTypeId());
		if (varType != null) {
			variableDef.setRole(varType.getRole());
			variableDef.setVariableType(varType);
		}

		return variableDef;
	}
	
}
