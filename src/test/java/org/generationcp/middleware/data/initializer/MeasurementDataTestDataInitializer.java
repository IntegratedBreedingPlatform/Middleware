
package org.generationcp.middleware.data.initializer;

import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.oms.TermId;

public class MeasurementDataTestDataInitializer {

	public static MeasurementData createMeasurementData(final int termId, final int variableDataTypeId, final String value) {
		final MeasurementData measurementData = new MeasurementData();
		measurementData.setAccepted(true);

		measurementData.setDataType("1");
		measurementData.setEditable(true);
		measurementData.setLabel("Measurement Data Label");
		measurementData
				.setMeasurementVariable(MeasurementVariableTestDataInitializer.createMeasurementVariable(termId, variableDataTypeId));
		measurementData.setPhenotypeId(123);
		measurementData.setVariable(new Variable());

		if (TermId.CATEGORICAL_VARIABLE.getId() == variableDataTypeId) {
			measurementData.setcValueId(value);
		} else {
			measurementData.setValue(value);
		}
		return measurementData;
	}
}
