package org.generationcp.middleware.service.api.ontology;

import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.reports.Reporter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class VariableDataValidatorFactoryImpl implements VariableDataValidatorFactory {

	private Map<DataType, VariableValueValidator> dataTypeValidatorMap = new HashMap<>();

	@Override
	public Optional<VariableValueValidator> getValidator(final DataType dataType) {
		// Look for validator in custom or prioritized registered validators
		if (this.dataTypeValidatorMap.containsKey(dataType)) {
			return Optional.of(this.dataTypeValidatorMap.get(dataType));
		} else if (DataType.NUMERIC_VARIABLE.getId().equals(dataType.getId())) {
			return Optional.of(new NumericValueValidator());
		} else if (DataType.CHARACTER_VARIABLE.getId().equals(dataType.getId())) {
			return Optional.of(new CharacterValueValidator());
		} else if (DataType.DATE_TIME_VARIABLE.getId().equals(dataType.getId())) {
			return Optional.of(new DateValueValidator());
		} else if (DataType.CATEGORICAL_VARIABLE.getId().equals(dataType.getId())) {
			return Optional.of(new CategoricalValueDescriptionValidator());
		}
		return Optional.empty();
	}

	@Override
	public void registerDataTypeValidator(final DataType dataType, final VariableValueValidator validator) {
		this.dataTypeValidatorMap.put(dataType, validator);
	}
}
