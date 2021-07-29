package org.generationcp.middleware.service.impl.ontology;

import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.service.api.ontology.NumericValueValidator;
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

public class NumericValueValidatorTest {

	private final NumericValueValidator numericValueValidator = new NumericValueValidator();

	@Test(expected = IllegalStateException.class)
	public void test_isValid_NonNumericDataType_ShouldThrowException(){
		final MeasurementVariable variable = new MeasurementVariable();
		variable.setDataTypeId(DataType.CHARACTER_VARIABLE.getId());
		this.numericValueValidator.isValid(variable);
	}

	@Test
	public void test_isValid_NumericDataType_EmptyValue(){
		final MeasurementVariable variable = new MeasurementVariable();
		variable.setDataTypeId(DataType.NUMERIC_VARIABLE.getId());
		Assert.assertTrue(this.numericValueValidator.isValid(variable));
	}

	@Test
	public void test_isValid_NumericDbIdDataType_EmptyValue(){
		final MeasurementVariable variable = new MeasurementVariable();
		variable.setDataTypeId(DataType.NUMERIC_DBID_VARIABLE.getId());
		Assert.assertTrue(this.numericValueValidator.isValid(variable));
	}

	@Test
	public void test_isValid_NumericDataType_ValidValue(){
		final MeasurementVariable variable = new MeasurementVariable();
		variable.setDataTypeId(DataType.NUMERIC_VARIABLE.getId());
		variable.setValue("1.5");
		Assert.assertTrue(this.numericValueValidator.isValid(variable));
	}

	@Test
	public void test_isValid_NumericDataType_NonNumericValue(){
		final MeasurementVariable variable = new MeasurementVariable();
		variable.setDataTypeId(DataType.NUMERIC_VARIABLE.getId());
		variable.setValue("abc");
		Assert.assertFalse(this.numericValueValidator.isValid(variable));
	}


	@Test
	public void test_isValid_NumericDbIdDataType_ValidValue(){
		final MeasurementVariable variable = new MeasurementVariable();
		variable.setDataTypeId(DataType.NUMERIC_DBID_VARIABLE.getId());
		variable.setValue(String.valueOf(new Random().nextInt(100)));
		Assert.assertTrue(this.numericValueValidator.isValid(variable));
	}

	@Test
	public void test_isValid_NumericDbIdDataType_NonDigitsValue(){
		final MeasurementVariable variable = new MeasurementVariable();
		variable.setDataTypeId(DataType.NUMERIC_DBID_VARIABLE.getId());
		variable.setValue("2.2");
		Assert.assertFalse(this.numericValueValidator.isValid(variable));
	}

	@Test
	public void test_isValid_NumericDbIdDataType_NonNumericValue(){
		final MeasurementVariable variable = new MeasurementVariable();
		variable.setDataTypeId(DataType.NUMERIC_DBID_VARIABLE.getId());
		variable.setValue("x");
		Assert.assertFalse(this.numericValueValidator.isValid(variable));
	}


}
