package org.generationcp.middleware.service.api.ontology;

import liquibase.util.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.ontology.DataType;

public class NumericValueValidator implements VariableValueValidator {

	@Override
	public boolean isValid(final MeasurementVariable variable) {
		this.ensureNumericDataType(variable.getDataTypeId());
		return StringUtils.isEmpty(variable.getValue()) || NumberUtils.isNumber(variable.getValue().trim());
	}

	private void ensureNumericDataType(final Integer dataTypeId) {
		if (!DataType.NUMERIC_VARIABLE.getId().equals(dataTypeId)) {
			throw new IllegalStateException("The ensureNumericDataType method must never be called for non numeric variables. "
				+ "Please report this error to your administrator.");
		}
	}
}
