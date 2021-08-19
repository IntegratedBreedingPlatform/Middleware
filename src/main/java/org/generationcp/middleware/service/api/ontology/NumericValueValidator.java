package org.generationcp.middleware.service.api.ontology;

import liquibase.util.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.ontology.DataType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NumericValueValidator implements VariableValueValidator {

	public static final List<Integer> NUMERIC_DATATYPES =
		Collections.unmodifiableList(Arrays.asList(DataType.NUMERIC_VARIABLE.getId(), DataType.NUMERIC_DBID_VARIABLE.getId()));

	@Override
	public boolean isValid(final MeasurementVariable variable) {
		this.ensureNumericDataType(variable.getDataTypeId());
		final String value = variable.getValue();
		return StringUtils.isEmpty(value) || (DataType.NUMERIC_DBID_VARIABLE.getId().equals(variable.getDataTypeId()) && NumberUtils
			.isDigits(value)) || (DataType.NUMERIC_VARIABLE.getId().equals(variable.getDataTypeId()) && NumberUtils.isNumber(value.trim()));
	}

	private void ensureNumericDataType(final Integer dataTypeId) {
		if (!NUMERIC_DATATYPES.contains(dataTypeId)) {
			throw new IllegalStateException("The ensureNumericDataType method must never be called for non numeric variables. "
				+ "Please report this error to your administrator.");
		}
	}
}
