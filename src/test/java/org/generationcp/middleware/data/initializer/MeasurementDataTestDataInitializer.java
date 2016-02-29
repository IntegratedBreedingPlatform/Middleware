
package org.generationcp.middleware.data.initializer;

import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;

import java.util.ArrayList;
import java.util.List;

public class MeasurementDataTestDataInitializer {

	public static final String DATA_TYPE_CHAR = "C";

	public static MeasurementData createMeasurementData(final Integer termId, final String label, final String value) {
		final MeasurementData measurementData = createMeasurementData(termId, label, value,TermId.CHARACTER_VARIABLE);
		measurementData.setDataType(MeasurementDataTestDataInitializer.DATA_TYPE_CHAR);

		return measurementData;
	}

	private static MeasurementData createMeasurementData(Integer termId, String label, String value,TermId dataType) {
		final MeasurementData measurementData = new MeasurementData(label, value);

		final MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementVariable.setTermId(termId);
		measurementVariable.setDataTypeId(dataType.getId());

		measurementData.setMeasurementVariable(measurementVariable);
		return measurementData;
	}

	public MeasurementData createCategoricalMeasurementData(final Integer termId, final String label, final String value) {
		final MeasurementData data = createMeasurementData(termId, label, value,TermId.CATEGORICAL_VARIABLE);

		List<ValueReference> possibleValues = new ArrayList<ValueReference>();
		possibleValues.add(new ValueReference(1, "Name", "Desc"));

		data.getMeasurementVariable().setPossibleValues(possibleValues);

		return data;
	}


}
