
package org.generationcp.middleware.data.initializer;

import java.util.Collections;

import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;

public class MeasurementRowTestDataInitializer {

	private final MeasurementData measurementData;

	public MeasurementRowTestDataInitializer(final MeasurementData measurementData) {
		this.measurementData = measurementData;
	}

	public MeasurementRow createMeasurementRowWithAtLeast1MeasurementVar() {
		final MeasurementRow measurementRow = new MeasurementRow();
		measurementRow.setExperimentId(1);
		measurementRow.setDataList(Collections.<MeasurementData>singletonList(this.measurementData));
		return measurementRow;
	}
}
