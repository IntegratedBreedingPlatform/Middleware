
package org.generationcp.middleware.data.initializer;

import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.oms.TermId;

public class MeasurementDataTestDataInitializer {

	private final MeasurementVariableTestDataInitializer measurementVariableInit;
	private final int termId;
	private final int variableDataTypeId;
	private final String value;

	public MeasurementDataTestDataInitializer(final int termId, final int variableDataTypeId, final String value) {
		this.termId = termId;
		this.variableDataTypeId = variableDataTypeId;
		this.value = value;
		this.measurementVariableInit = new MeasurementVariableTestDataInitializer(this.termId, this.variableDataTypeId);
	}

	public MeasurementData createMeasurementData() {
		final MeasurementData measurementData = new MeasurementData();
		measurementData.setAccepted(true);

		measurementData.setDataType("1");
		measurementData.setEditable(true);
		measurementData.setLabel("Measurement Data Label");

		measurementData.setMeasurementVariable(this.measurementVariableInit.createMeasurementVariable());

		measurementData.setPhenotypeId(123);
		measurementData.setVariable(new Variable());

		if (TermId.CATEGORICAL_VARIABLE.getId() == this.variableDataTypeId) {
			measurementData.setcValueId(this.value);
		} else {
			measurementData.setValue(this.value);
		}
		return measurementData;
	}
}
