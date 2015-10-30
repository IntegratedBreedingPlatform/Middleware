
package org.generationcp.middleware.data.initializer;

import java.util.Collections;

import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;

public class MeasurementRowTestDataInitializer {

	public static MeasurementRow createMeasurementRowWithAtLeast1MeasurementVar(final MeasurementData measurementData) {
		final MeasurementRow measurementRow = new MeasurementRow();
		measurementRow.setExperimentId(1);
		measurementRow.setDataList(Collections.<MeasurementData>singletonList(measurementData));
		return measurementRow;
	}
}
