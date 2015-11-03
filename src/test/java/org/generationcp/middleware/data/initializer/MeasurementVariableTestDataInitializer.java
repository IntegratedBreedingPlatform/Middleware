
package org.generationcp.middleware.data.initializer;

import org.generationcp.middleware.domain.etl.MeasurementVariable;

public class MeasurementVariableTestDataInitializer {

	private final int termId;
	private final int variableDataTypeId;

	public MeasurementVariableTestDataInitializer(final int termId, final int variableDataTypeId) {
		this.termId = termId;
		this.variableDataTypeId = variableDataTypeId;
	}

	public MeasurementVariable createMeasurementVariable() {
		final MeasurementVariable measurementVariable =
				new MeasurementVariable(this.termId, "Variable Name", "Variable Description", "Variable Scale", "Variable Method",
						"Variable Property", "1", "Variable Value", "Variable Lable");
		measurementVariable.setDataTypeId(this.variableDataTypeId);
		return measurementVariable;

	}
}
