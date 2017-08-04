package org.generationcp.middleware.domain.etl;

import junit.framework.Assert;
import org.generationcp.middleware.domain.ontology.DataType;
import org.junit.Test;

public class MeasurementVariableTest {

	@Test
	public void testGetDataTypeCode() {

		final MeasurementVariable measurementVariable = new MeasurementVariable();

		// Common Data Types
		measurementVariable.setDataTypeId(DataType.CHARACTER_VARIABLE.getId());
		Assert.assertEquals(DataType.CHARACTER_VARIABLE.getDataTypeCode(), measurementVariable.getDataTypeCode());

		measurementVariable.setDataTypeId(DataType.NUMERIC_VARIABLE.getId());
		Assert.assertEquals(DataType.NUMERIC_VARIABLE.getDataTypeCode(), measurementVariable.getDataTypeCode());

		measurementVariable.setDataTypeId(DataType.DATE_TIME_VARIABLE.getId());
		Assert.assertEquals(DataType.DATE_TIME_VARIABLE.getDataTypeCode(), measurementVariable.getDataTypeCode());

		measurementVariable.setDataTypeId(DataType.CATEGORICAL_VARIABLE.getId());
		Assert.assertEquals(DataType.CATEGORICAL_VARIABLE.getDataTypeCode(), measurementVariable.getDataTypeCode());

		// Special Data Types
		measurementVariable.setDataTypeId(DataType.PERSON.getId());
		Assert.assertEquals(DataType.PERSON.getDataTypeCode(), measurementVariable.getDataTypeCode());

		measurementVariable.setDataTypeId(DataType.LOCATION.getId());
		Assert.assertEquals(DataType.LOCATION.getDataTypeCode(), measurementVariable.getDataTypeCode());

		measurementVariable.setDataTypeId(DataType.STUDY.getId());
		Assert.assertEquals(DataType.STUDY.getDataTypeCode(), measurementVariable.getDataTypeCode());

		measurementVariable.setDataTypeId(DataType.DATASET.getId());
		Assert.assertEquals(DataType.DATASET.getDataTypeCode(), measurementVariable.getDataTypeCode());

		measurementVariable.setDataTypeId(DataType.GERMPLASM_LIST.getId());
		Assert.assertEquals(DataType.GERMPLASM_LIST.getDataTypeCode(), measurementVariable.getDataTypeCode());

		measurementVariable.setDataTypeId(DataType.BREEDING_METHOD.getId());
		Assert.assertEquals(DataType.BREEDING_METHOD.getDataTypeCode(), measurementVariable.getDataTypeCode());

		// Set an invalid Data Type Id
		measurementVariable.setDataTypeId(10101);
		// If the DataTypeId is invalid, the method will return an empty string.
		Assert.assertEquals("", measurementVariable.getDataTypeCode());
	}

}
