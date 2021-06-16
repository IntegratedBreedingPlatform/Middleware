package org.generationcp.middleware.service.api.ontology;

import liquibase.util.StringUtils;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.ontology.DataType;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

public class CategoricalValueValidator implements VariableValueValidator {

	@Override
	public boolean isValid(final MeasurementVariable variable, final boolean useCategoricalValueId) {
		this.verifyCategoricalDataType(variable);
		List<String> possibleValues;
		if(useCategoricalValueId) {
			possibleValues = variable.getPossibleValues().stream().map(value -> value.getId().toString()).collect(
				Collectors.toList());
		} else {
			possibleValues = variable.getPossibleValues().stream().map(ValueReference::getName).collect(
				Collectors.toList());
		}
		return StringUtils.isEmpty(variable.getValue()) || possibleValues.contains(variable.getValue().trim());
	}


	private void verifyCategoricalDataType(final MeasurementVariable variable) {
		if (!DataType.CATEGORICAL_VARIABLE.getId().equals(variable.getDataTypeId())) {
			throw new IllegalStateException("The verifyCategoricalDataType method must never be called for non categorical variables. "
				+ "Please report this error to your administrator.");
		} else if (CollectionUtils.isEmpty(variable.getPossibleValues())){
			throw new IllegalStateException("The categorical variable " + variable.getTermId() + " do not have possible values. "
				+ "Please report this error to your administrator.");
		}
	}

}
