package org.generationcp.middleware.service.api.ontology;

import liquibase.util.StringUtils;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.ontology.DataType;

public class CharacterValueValidator implements VariableValueValidator {

	@Override
	public boolean isValid(final MeasurementVariable variable) {
		this.ensureCharacterDataType(variable.getDataTypeId());
		return StringUtils.isEmpty(variable.getValue()) || variable.getValue().length() <= 255;
	}

	private void ensureCharacterDataType(final Integer dataTypeId) {
		if (!DataType.CHARACTER_VARIABLE.getId().equals(dataTypeId)) {
			throw new IllegalStateException("The ensureCharacterDataType method must never be called for non character variables. "
				+ "Please report this error to your administrator.");
		}
	}

}
