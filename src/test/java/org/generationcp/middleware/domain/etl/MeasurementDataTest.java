
package org.generationcp.middleware.domain.etl;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.oms.TermId;
import org.junit.Assert;
import org.junit.Test;

public class MeasurementDataTest {

	@Test
	public void testIsCategoricalDisplayAcceptedValidValuesIfVariableIsNonCategorical() {
		MeasurementData data = new MeasurementData();
		MeasurementVariable var = new MeasurementVariable();
		var.setDataTypeId(TermId.DATE_VARIABLE.getId());
		data.setMeasurementVariable(var);
		Assert.assertFalse("Should return false since measurement variable is non categorical",
				data.isCategoricalDisplayAcceptedValidValues());
	}

	@Test
	public void testIsCategoricalDisplayAcceptedValidValuesIfVariableIsNull() {
		MeasurementData data = new MeasurementData();
		data.setMeasurementVariable(null);
		Assert.assertFalse("Should return false since measurement variable is null", data.isCategoricalDisplayAcceptedValidValues());
	}

	@Test
	public void testIsCategoricalDisplayAcceptedValidValuesIfVariableIsCategoricalAndPossibleValuesIsEmpty() {
		MeasurementData data = new MeasurementData();
		data.setValue("1");
		MeasurementVariable var = new MeasurementVariable();
		var.setDataTypeId(TermId.CATEGORICAL_VARIABLE.getId());
		var.setPossibleValues(new ArrayList<ValueReference>());
		data.setMeasurementVariable(var);
		Assert.assertTrue("Should return false since possible values is empty list", data.isCategoricalDisplayAcceptedValidValues());
	}

	@Test
	public void testIsCategoricalDisplayAcceptedValidValuesIfVariableIsCategoricalAndDisplayValueIsEmptyString() {
		MeasurementData data = new MeasurementData();
		data.setValue("");
		MeasurementVariable var = new MeasurementVariable();
		var.setDataTypeId(TermId.CATEGORICAL_VARIABLE.getId());
		var.setPossibleValues(new ArrayList<ValueReference>());
		data.setMeasurementVariable(var);
		Assert.assertFalse("Should return false since possible values is empty list", data.isCategoricalDisplayAcceptedValidValues());
	}

	@Test
	public void testIsCategoricalDisplayAcceptedValidValuesIfVariableIsCategoricalAndValueMarkedMissing() {
		MeasurementData data = new MeasurementData();
		MeasurementVariable var = new MeasurementVariable();
		var.setDataTypeId(TermId.CATEGORICAL_VARIABLE.getId());
		var.setPossibleValues(new ArrayList<ValueReference>());
		data.setMeasurementVariable(var);
		data.setValue(MeasurementData.MISSING_VALUE);
		Assert.assertFalse("Should return false since value is marked as missing", data.isCategoricalDisplayAcceptedValidValues());
	}

	@Test
	public void testIsCategoricalDisplayAcceptedValidValuesIfVariableIsCategoricalAndDisplayValueIsMatching() {
		MeasurementData data = new MeasurementData();
		MeasurementVariable var = new MeasurementVariable();
		var.setDataTypeId(TermId.CATEGORICAL_VARIABLE.getId());
		List<ValueReference> possibleValues = new ArrayList<ValueReference>();
		possibleValues.add(new ValueReference(1, "Name", "Desc"));
		var.setPossibleValues(possibleValues);
		data.setMeasurementVariable(var);
		data.setValue("1");
		Assert.assertFalse("Should return false since dispay value part of the possible values",
				data.isCategoricalDisplayAcceptedValidValues());
	}

	@Test
	public void testIsCategoricalDisplayAcceptedValidValuesIfVariableIsCategoricalAndDisplayValueIsNotMatching() {
		MeasurementData data = new MeasurementData();
		MeasurementVariable var = new MeasurementVariable();
		var.setDataTypeId(TermId.CATEGORICAL_VARIABLE.getId());
		List<ValueReference> possibleValues = new ArrayList<ValueReference>();
		possibleValues.add(new ValueReference(1, "Name", "Desc"));
		var.setPossibleValues(possibleValues);
		data.setMeasurementVariable(var);
		data.setValue("2");
		Assert.assertTrue("Should return true since dispay value is a custom out of bounds value (accepted value)",
				data.isCategoricalDisplayAcceptedValidValues());
	}

	@Test
	public void testIsValidDataForCategoricalDataTypeValidValue() {
		MeasurementData measurementData = new MeasurementData();
		MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementData.setMeasurementVariable(measurementVariable);
		measurementVariable.setDataTypeId(TermId.CATEGORICAL_VARIABLE.getId());
		measurementVariable.setPossibleValues(this.createTestPossibleValues());
		measurementData.setValue("1");
		Assert.assertTrue(measurementData.isCategoricalValueValid());
	}

	@Test
	public void testCategoricalValueValidInvalidValue() {
		MeasurementData measurementData = new MeasurementData();
		MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementData.setMeasurementVariable(measurementVariable);
		measurementVariable.setDataTypeId(TermId.CATEGORICAL_VARIABLE.getId());
		measurementVariable.setPossibleValues(this.createTestPossibleValues());
		measurementData.setValue("3");
		Assert.assertFalse(measurementData.isCategoricalValueValid());
	}

	@Test
	public void testCategoricalValueValidEmptyValue() {
		MeasurementData measurementData = new MeasurementData();
		MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementData.setMeasurementVariable(measurementVariable);
		measurementVariable.setDataTypeId(TermId.CATEGORICAL_VARIABLE.getId());
		measurementVariable.setPossibleValues(this.createTestPossibleValues());
		measurementData.setValue("");
		Assert.assertTrue(measurementData.isCategoricalValueValid());
	}

	@Test
	public void testIsCategoricalValueValidNoPossibleValues() {
		MeasurementData measurementData = new MeasurementData();
		MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementData.setMeasurementVariable(measurementVariable);
		measurementVariable.setDataTypeId(TermId.CATEGORICAL_VARIABLE.getId());
		measurementVariable.setPossibleValues(null);
		measurementData.setValue("");
		Assert.assertTrue(measurementData.isCategoricalValueValid());
	}

	private List<ValueReference> createTestPossibleValues() {
		List<ValueReference> possibleValues = new ArrayList<>();
		possibleValues.add(new ValueReference(1, "1", "VALUE 1"));
		possibleValues.add(new ValueReference(2, "2", "VALUE 2"));
		return possibleValues;
	}
}
