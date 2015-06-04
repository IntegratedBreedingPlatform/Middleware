
package org.generationcp.middleware.domain.etl;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.oms.TermId;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

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
		MeasurementData data = Mockito.spy(new MeasurementData());
		MeasurementVariable var = new MeasurementVariable();
		var.setDataTypeId(TermId.CATEGORICAL_VARIABLE.getId());
		var.setPossibleValues(new ArrayList<ValueReference>());
		data.setMeasurementVariable(var);
		Mockito.doReturn("").when(data).getDisplayValue();
		Assert.assertFalse("Should return false since possible values is empty list", data.isCategoricalDisplayAcceptedValidValues());
	}

	@Test
	public void testIsCategoricalDisplayAcceptedValidValuesIfVariableIsCategoricalAndValueMarkedMissing() {
		MeasurementData data = Mockito.spy(new MeasurementData());
		MeasurementVariable var = new MeasurementVariable();
		var.setDataTypeId(TermId.CATEGORICAL_VARIABLE.getId());
		var.setPossibleValues(new ArrayList<ValueReference>());
		data.setMeasurementVariable(var);
		Mockito.doReturn(MeasurementData.MISSING_VALUE).when(data).getDisplayValue();
		Assert.assertFalse("Should return false since value is marked as missing", data.isCategoricalDisplayAcceptedValidValues());
	}

	@Test
	public void testIsCategoricalDisplayAcceptedValidValuesIfVariableIsCategoricalAndDisplayValueIsMatching() {
		MeasurementData data = Mockito.spy(new MeasurementData());
		MeasurementVariable var = new MeasurementVariable();
		var.setDataTypeId(TermId.CATEGORICAL_VARIABLE.getId());
		List<ValueReference> possibleValues = new ArrayList<ValueReference>();
		possibleValues.add(new ValueReference(1, "Name", "Desc"));
		var.setPossibleValues(possibleValues);
		data.setMeasurementVariable(var);
		Mockito.doReturn("Desc").when(data).getDisplayValue();
		Assert.assertFalse("Should return false since dispay value part of the possible values",
				data.isCategoricalDisplayAcceptedValidValues());
	}

	@Test
	public void testIsCategoricalDisplayAcceptedValidValuesIfVariableIsCategoricalAndDisplayValueIsNotMatching() {
		MeasurementData data = Mockito.spy(new MeasurementData());
		MeasurementVariable var = new MeasurementVariable();
		var.setDataTypeId(TermId.CATEGORICAL_VARIABLE.getId());
		List<ValueReference> possibleValues = new ArrayList<ValueReference>();
		possibleValues.add(new ValueReference(1, "Name", "Desc"));
		var.setPossibleValues(possibleValues);
		data.setMeasurementVariable(var);
		Mockito.doReturn("5").when(data).getDisplayValue();
		Assert.assertTrue("Should return true since dispay value is a custom out of bounds value (accepted value)",
				data.isCategoricalDisplayAcceptedValidValues());
	}
}
