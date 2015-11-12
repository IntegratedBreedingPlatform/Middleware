
package org.generationcp.middleware.data.initializer;

import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;

public class MeasurementDataTestDataInitializer {

	public static final String DATA_TYPE_CHAR = "C";

	public static MeasurementData createMeasurementData(final Integer termId, final String label, final String value) {

		final MeasurementData measurementData = new MeasurementData(label, value);
		measurementData.setDataType(MeasurementDataTestDataInitializer.DATA_TYPE_CHAR);

		final MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementVariable.setTermId(termId);
		measurementVariable.setDataTypeId(TermId.CHARACTER_VARIABLE.getId());

		measurementData.setMeasurementVariable(measurementVariable);
		return measurementData;
	}
}
