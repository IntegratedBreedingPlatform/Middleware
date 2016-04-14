package org.generationcp.middleware.data.initializer;

import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;

import java.util.ArrayList;
import java.util.List;

public class MeasurementDataTestDataInitializer {

	public static final String DATA_TYPE_CHAR = "C";

	// This method remains as static for compatibility to older tests that uses this method.
	public static MeasurementData createMeasurementData(final Integer termId, final String label, final String value) {
		final MeasurementData measurementData =
				new MeasurementDataTestDataInitializer().createMeasurementData(termId, label, value, TermId.CHARACTER_VARIABLE);
		measurementData.setDataType(MeasurementDataTestDataInitializer.DATA_TYPE_CHAR);

		return measurementData;
	}

	/**
	 * Create a new measurement data
	 * @param termId
	 * @param label
	 * @param value
	 * @param dataType
	 * @return
	 */
	public MeasurementData createMeasurementData(Integer termId, String label, String value, TermId dataType) {
		final MeasurementData measurementData = new MeasurementData(label, value);
		measurementData.setEditable(false);
		measurementData.setLabel("");
		measurementData.setPhenotypeId(0);
		measurementData.setValue(value);

		final MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementVariable.setTermId(termId);
		measurementVariable.setDataTypeId(dataType.getId());
		measurementVariable.setName(label);
		measurementVariable.setLabel(label);
		measurementVariable.setPossibleValues(new ArrayList<ValueReference>());

		measurementData.setMeasurementVariable(measurementVariable);
		return measurementData;
	}

	/**
	 * Create a categorical measurement data
	 * @param termId
	 * @param label
	 * @param value
	 * @param possibleValues
	 * @return
	 */
	public MeasurementData createCategoricalMeasurementData(final Integer termId, final String label, final String value,
			List<ValueReference> possibleValues) {
		final MeasurementData data = createMeasurementData(termId, label, value, TermId.CATEGORICAL_VARIABLE);

		final MeasurementVariable measurementVariable = data.getMeasurementVariable();
		measurementVariable.setPossibleValues(possibleValues);

		data.getMeasurementVariable().setPossibleValues(possibleValues);

		return data;
	}

	/**
	 * Create a categorical measurement data with initial ppssibleValue
	 * @param termId
	 * @param label
	 * @param value
	 * @return
	 */
	public MeasurementData createCategoricalMeasurementData(final Integer termId, final String label, final String value) {
		final List<ValueReference> possibleValues = new ArrayList<>();
		possibleValues.add(new ValueReference(1, "Name", "Desc"));

		return createCategoricalMeasurementData(termId, label, value, possibleValues);
	}

}
