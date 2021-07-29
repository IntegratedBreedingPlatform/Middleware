package org.generationcp.middleware.service.api.ontology;

import org.generationcp.middleware.domain.ontology.DataType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class VariableDataValidatorFactoryImpl implements VariableDataValidatorFactory {

	@Autowired
	private PersonValidator personValidator;

	@Autowired
	private BreedingMethodValidator breedingMethodValidator;

	@Autowired
	private LocationValidator locationValidator;

	@Override
	public Optional<VariableValueValidator> getValidator(final DataType dataType) {
		if (DataType.PERSON.getId().equals(dataType.getId())) {
			return Optional.of(this.personValidator);

		} else if (DataType.BREEDING_METHOD.getId().equals(dataType.getId()) ) {
			return Optional.of(this.breedingMethodValidator);

		} else if (DataType.LOCATION.getId().equals(dataType.getId()) ) {
			return Optional.of(this.locationValidator);

		} else if (NumericValueValidator.NUMERIC_DATATYPES.contains(dataType.getId())) {
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

}
