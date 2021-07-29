package org.generationcp.middleware.service.impl.ontology;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.service.api.ontology.BreedingMethodValidator;
import org.generationcp.middleware.service.api.ontology.CategoricalValueDescriptionValidator;
import org.generationcp.middleware.service.api.ontology.CharacterValueValidator;
import org.generationcp.middleware.service.api.ontology.DateValueValidator;
import org.generationcp.middleware.service.api.ontology.LocationValidator;
import org.generationcp.middleware.service.api.ontology.NumericValueValidator;
import org.generationcp.middleware.service.api.ontology.PersonValidator;
import org.generationcp.middleware.service.api.ontology.VariableDataValidatorFactory;
import org.generationcp.middleware.service.api.ontology.VariableValueValidator;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class VariableDataValidatorFactoryImplTest extends IntegrationTestBase {

	@Autowired
	private VariableDataValidatorFactory variableDataValidatorFactory;

	@Test
	public void testGetValidator_WhenCharacterDataType(){
		final Optional<VariableValueValidator> validator = this.variableDataValidatorFactory.getValidator(DataType.CHARACTER_VARIABLE);
		Assert.assertTrue(validator.isPresent());
		Assert.assertEquals(CharacterValueValidator.class, validator.get().getClass());
	}

	@Test
	public void testGetValidator_WhenNumericDataType(){
		final Optional<VariableValueValidator> validator = this.variableDataValidatorFactory.getValidator(DataType.NUMERIC_VARIABLE);
		Assert.assertTrue(validator.isPresent());
		Assert.assertEquals(NumericValueValidator.class, validator.get().getClass());
	}

	@Test
	public void testGetValidator_WhenNumericDbIdDataType(){
		final Optional<VariableValueValidator> validator = this.variableDataValidatorFactory.getValidator(DataType.NUMERIC_DBID_VARIABLE);
		Assert.assertTrue(validator.isPresent());
		Assert.assertEquals(NumericValueValidator.class, validator.get().getClass());
	}

	@Test
	public void testGetValidator_WhenCategoricalDataType_Default(){
		final Optional<VariableValueValidator> validator = this.variableDataValidatorFactory.getValidator(DataType.CATEGORICAL_VARIABLE);
		Assert.assertTrue(validator.isPresent());
		Assert.assertEquals(CategoricalValueDescriptionValidator.class, validator.get().getClass());
	}

	@Test
	public void testGetValidator_WhenDateDataType(){
		final Optional<VariableValueValidator> validator = this.variableDataValidatorFactory.getValidator(DataType.DATE_TIME_VARIABLE);
		Assert.assertTrue(validator.isPresent());
		Assert.assertEquals(DateValueValidator.class, validator.get().getClass());
	}

	@Test
	public void testGetValidator_WhenPersonDataType(){
		final Optional<VariableValueValidator> validator = this.variableDataValidatorFactory.getValidator(DataType.PERSON);
		Assert.assertTrue(validator.isPresent());
		Assert.assertEquals(PersonValidator.class, validator.get().getClass());
	}

	@Test
	public void testGetValidator_WhenBreedingMethodDataType(){
		final Optional<VariableValueValidator> validator = this.variableDataValidatorFactory.getValidator(DataType.BREEDING_METHOD);
		Assert.assertTrue(validator.isPresent());
		Assert.assertEquals(BreedingMethodValidator.class, validator.get().getClass());
	}

	@Test
	public void testGetValidator_WhenLocationDataType(){
		final Optional<VariableValueValidator> validator = this.variableDataValidatorFactory.getValidator(DataType.LOCATION);
		Assert.assertTrue(validator.isPresent());
		Assert.assertEquals(LocationValidator.class, validator.get().getClass());
	}

	@Test
	public void testGetValidator_NotImplementedDataTypes(){
		Assert.assertFalse(this.variableDataValidatorFactory.getValidator(DataType.STUDY).isPresent());
		Assert.assertFalse(this.variableDataValidatorFactory.getValidator(DataType.DATASET).isPresent());
		Assert.assertFalse(this.variableDataValidatorFactory.getValidator(DataType.GERMPLASM_LIST).isPresent());
		Assert.assertFalse(this.variableDataValidatorFactory.getValidator(DataType.FILE_VARIABLE).isPresent());
	}


}
