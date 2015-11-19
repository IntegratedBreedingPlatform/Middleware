
package org.generationcp.middleware.data.initializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.oms.TermId;

public class MeasurementRowTestDataInitializer {

	public static Map<String, MeasurementRow> createEnvironmentDataMap() {
		final Map<String, MeasurementRow> environmentData = new HashMap<String, MeasurementRow>();
		final MeasurementRow measurementRow = MeasurementRowTestDataInitializer.createSecondMeasurementRow();
		environmentData.put("test1", measurementRow);
		return environmentData;
	}

	public static Map<String, List<MeasurementRow>> createMeasurementDataMap() {
		final Map<String, List<MeasurementRow>> measurementData = new HashMap<String, List<MeasurementRow>>();
		final List<MeasurementRow> measurementRowList = new ArrayList<MeasurementRow>();
		measurementData.put("test1", measurementRowList);
		return measurementData;
	}

	public static MeasurementRow createMeasurementRow() {

		final MeasurementRow measurementRow = new MeasurementRow();
		final List<MeasurementData> dataList = new ArrayList<>();
		dataList.add(MeasurementDataTestDataInitializer.createMeasurementData(TermId.LOCATION_ID.getId(), "LOCATION_ID", "123"));
		dataList.add(MeasurementDataTestDataInitializer.createMeasurementData(TermId.TRIAL_LOCATION.getId(), "LOCATION_NAME", "Manila"));
		dataList.add(MeasurementDataTestDataInitializer.createMeasurementData(111, "Some Variable", "Test Data"));
		measurementRow.setDataList(dataList);

		return measurementRow;
	}

	public static MeasurementRow createSecondMeasurementRow() {

		final MeasurementRow measurementRow = new MeasurementRow();
		final List<MeasurementData> dataList = new ArrayList<>();
		dataList.add(MeasurementDataTestDataInitializer.createMeasurementData(TermId.LOCATION_ID.getId(), "LOCATION_ID", "123"));
		dataList.add(MeasurementDataTestDataInitializer.createMeasurementData(TermId.TRIAL_LOCATION.getId(), "LOCATION_NAME", "Manila"));
		dataList.add(MeasurementDataTestDataInitializer.createMeasurementData(8250, "Some Variable", "Test Data"));
		measurementRow.setDataList(dataList);

		return measurementRow;
	}
}
