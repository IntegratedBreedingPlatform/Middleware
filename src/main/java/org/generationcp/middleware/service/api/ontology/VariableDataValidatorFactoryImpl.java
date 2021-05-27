package org.generationcp.middleware.service.api.ontology;

import org.generationcp.middleware.domain.ontology.DataType;

import java.util.Optional;

public class VariableDataValidatorFactoryImpl implements VariableDataValidatorFactory {

	@Override
	public Optional<VariableValueValidator> getValidator(final DataType dataType) {
		if (DataType.NUMERIC_VARIABLE.getId().equals(dataType.getId())) {
			return Optional.of(new NumericValueValidator());
		} else if (DataType.CHARACTER_VARIABLE.getId().equals(dataType.getId())) {
			return Optional.of(new CharacterValueValidator());
		} else if (DataType.DATE_TIME_VARIABLE.getId().equals(dataType.getId())) {
			return Optional.of(new DateValueValidator());
		} else if (DataType.CATEGORICAL_VARIABLE.getId().equals(dataType.getId())) {
			return Optional.of(new CategoricalValueValidator());
		}
		return Optional.empty();
	}
}
