package org.generationcp.middleware.service.api.ontology;

import liquibase.util.StringUtils;
import org.apache.commons.validator.routines.DateValidator;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.ontology.DataType;

import java.text.DateFormat;

public class DateValueValidator implements VariableValueValidator {

	private final DateValidator dateValidator;

	public DateValueValidator() {
		this.dateValidator = new DateValidator(false, DateFormat.SHORT);
	}

	@Override
	public boolean isValid(final MeasurementVariable variable, final boolean useCategoricalValueName) {
		this.ensureDateDataType(variable.getDataTypeId());
		return StringUtils.isEmpty(variable.getValue()) || this.dateValidator.isValid(variable.getValue().trim(), "yyyyMMdd");
	}

	private void ensureDateDataType(final Integer dataTypeId) {
		if (!DataType.DATE_TIME_VARIABLE.getId().equals(dataTypeId)) {
			throw new IllegalStateException("The ensureDateDataType method must never be called for non numeric variables. "
				+ "Please report this error to your administrator.");
		}
	}
}
