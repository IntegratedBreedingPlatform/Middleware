
package org.generationcp.middleware.data.initializer;

import java.util.Collections;

import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.pojos.dms.Phenotype;

public class MeasurementTestDataInitializer {

	public MeasurementTestDataInitializer() {
		super();
	}

	public MeasurementVariable createMeasurementVariable(final int termId, final int variableDataTypeId) {
		final MeasurementVariable measurementVariable =
				new MeasurementVariable(termId, "Variable Name", "Variable Description", "Variable Scale", "Variable Method",
						"Variable Property", "1", "Variable Value", "Variable Lable");
		measurementVariable.setDataTypeId(variableDataTypeId);
		return measurementVariable;

	}

	public MeasurementData createMeasurementData(final int termId, final int variableDataTypeId, final String value) {
		final MeasurementData measurementData = new MeasurementData();
		measurementData.setAccepted(true);

		measurementData.setDataType("1");
		measurementData.setEditable(true);
		measurementData.setLabel("Measurement Data Label");

		measurementData.setMeasurementVariable(this.createMeasurementVariable(termId, variableDataTypeId));

		measurementData.setPhenotypeId(123);
		measurementData.setVariable(new Variable());

		if (TermId.CATEGORICAL_VARIABLE.getId() == variableDataTypeId) {
			measurementData.setcValueId(value);
		} else {
			measurementData.setValue(value);
		}
		measurementData.setValueStatus(Phenotype.ValueStatus.MANUALLY_EDITED);
		return measurementData;
	}

	public MeasurementRow createMeasurementRowWithAtLeast1MeasurementVar(final MeasurementData measurementData) {
		final MeasurementRow measurementRow = new MeasurementRow();
		measurementRow.setExperimentId(1);
		measurementRow.setDataList(Collections.<MeasurementData>singletonList(measurementData));
		return measurementRow;
	}
}
