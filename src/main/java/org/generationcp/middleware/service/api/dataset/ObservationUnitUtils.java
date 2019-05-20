package org.generationcp.middleware.service.api.dataset;

import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;

import java.util.HashMap;

/**
 * Utility class to help migrate from Fieldbook/Workbook to BMSAPI
 */
public abstract class ObservationUnitUtils {

	public static ObservationUnitRow fromMeasurementRow(final MeasurementRow measurementRow) {

		if (measurementRow == null) {
            return null;
		}

		final ObservationUnitRow observationUnitRow = new ObservationUnitRow();

		// observationUnitRow.setObsUnitId(); // TODO
		observationUnitRow.setObservationUnitId(measurementRow.getExperimentId());

		final HashMap<String, ObservationUnitData> variables = new HashMap<>();
		for (final MeasurementData measurementData : measurementRow.getDataList()) {
			variables.put(measurementData.getMeasurementVariable().getName(), fromMeasurementData(measurementData));
		}
		observationUnitRow.setVariables(variables);

		return observationUnitRow;
	}

	public static ObservationUnitData fromMeasurementData(final MeasurementData measurementData) {
		if (measurementData == null) {
			return null;
		}

		final ObservationUnitData observationUnitData = new ObservationUnitData();
		observationUnitData.setValue(measurementData.getValue());
		observationUnitData.setObservationId(measurementData.getPhenotypeId());

		observationUnitData.setVariableId(measurementData.getMeasurementVariable().getTermId());

		if (measurementData.getcValueId() != null) {
			observationUnitData.setCategoricalValueId(Integer.valueOf(measurementData.getcValueId()));
		}
		observationUnitData.setStatus(measurementData.getValueStatus());
		return observationUnitData;
	}
}
