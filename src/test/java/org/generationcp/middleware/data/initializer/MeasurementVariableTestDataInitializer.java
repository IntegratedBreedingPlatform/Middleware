
package org.generationcp.middleware.data.initializer;

import org.generationcp.middleware.domain.etl.MeasurementVariable;

public class MeasurementVariableTestDataInitializer {

	public static MeasurementVariable createMeasurementVariable(final int termId, final int variableDataTypeId) {
		final MeasurementVariable measurementVariable =
				new MeasurementVariable(termId, "Variable Name", "Variable Description", "Variable Scale", "Variable Method",
						"Variable Property", "1", "Variable Value", "Variable Lable");
		measurementVariable.setDataTypeId(variableDataTypeId);
		return measurementVariable;

	}
}
