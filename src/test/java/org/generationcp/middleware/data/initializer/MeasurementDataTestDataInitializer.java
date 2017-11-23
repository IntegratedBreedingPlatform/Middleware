
package org.generationcp.middleware.data.initializer;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;

public class MeasurementDataTestDataInitializer {

	public static final String DATA_TYPE_CHAR = "C";

	// This method remains as static for compatibility to older tests that uses
	// this method.
	public static MeasurementData createMeasurementData(final Integer termId, final String label, final String value) {
		final MeasurementData measurementData = new MeasurementDataTestDataInitializer().createMeasurementData(termId,
				label, value, TermId.CHARACTER_VARIABLE);
		measurementData.setDataType(MeasurementDataTestDataInitializer.DATA_TYPE_CHAR);

		return measurementData;
	}

	public MeasurementData createMeasurementData(final String value, final MeasurementVariable mv) {
		final MeasurementData measurementData = new MeasurementData("label", value);
		measurementData.setMeasurementVariable(mv);
		return measurementData;
	}

	/**
	 * Create a new measurement data
	 *
	 * @param termId
	 * @param label
	 * @param value
	 * @param dataType
	 * @return
	 */
	public MeasurementData createMeasurementData(final Integer termId, final String label, final String value,
			final TermId dataType) {
		final MeasurementData measurementData = new MeasurementData(label, value);
		measurementData.setEditable(false);
		measurementData.setLabel(label);
		measurementData.setPhenotypeId(0);
		measurementData.setValue(value);
		measurementData.setcValueId("1");

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
	 *
	 * @param termId
	 * @param label
	 * @param value
	 * @param possibleValues
	 * @return
	 */
	public MeasurementData createCategoricalMeasurementData(final Integer termId, final String label,
			final String value, final List<ValueReference> possibleValues) {
		final MeasurementData data = this.createMeasurementData(termId, label, value, TermId.CATEGORICAL_VARIABLE);

		final MeasurementVariable measurementVariable = data.getMeasurementVariable();
		measurementVariable.setPossibleValues(possibleValues);

		data.getMeasurementVariable().setPossibleValues(possibleValues);

		return data;
	}

	/**
	 * Create a categorical measurement data with initial ppssibleValue
	 *
	 * @param termId
	 * @param label
	 * @param value
	 * @return
	 */
	public MeasurementData createCategoricalMeasurementData(final Integer termId, final String label,
			final String value) {
		final List<ValueReference> possibleValues = new ArrayList<>();
		possibleValues.add(new ValueReference(1, "Name", "Desc"));

		return this.createCategoricalMeasurementData(termId, label, value, possibleValues);
	}

}
