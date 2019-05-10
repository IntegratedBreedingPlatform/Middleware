package org.generationcp.middleware.data.initializer;

import org.generationcp.middleware.pojos.dms.DatasetType;

import java.util.HashMap;
import java.util.Map;

public class DatasetTypeTestDataInitializer {

	public static Map<Integer, DatasetType> createDatasetTypes() {

		final Map<Integer, DatasetType> datasetTypes = new HashMap<>();

		datasetTypes.put(DatasetType.STUDY_CONDITIONS, new DatasetType(DatasetType.STUDY_CONDITIONS, "STUDY"));
		datasetTypes.put(DatasetType.MEANS_DATA, new DatasetType(DatasetType.MEANS_DATA, "MEANS"));
		datasetTypes.put(DatasetType.SUMMARY_DATA, new DatasetType(DatasetType.SUMMARY_DATA, "SUMMARY"));
		datasetTypes.put(DatasetType.PLOT_DATA, new DatasetType(DatasetType.PLOT_DATA, "PLOT"));
		datasetTypes.put(DatasetType.PLANT_SUBOBSERVATIONS, new DatasetType(DatasetType.PLANT_SUBOBSERVATIONS, "PLANT"));
		datasetTypes.put(DatasetType.QUADRAT_SUBOBSERVATIONS, new DatasetType(DatasetType.QUADRAT_SUBOBSERVATIONS, "QUADRAT"));
		datasetTypes.put(DatasetType.TIME_SERIES_SUBOBSERVATIONS, new DatasetType(DatasetType.TIME_SERIES_SUBOBSERVATIONS, "TIMESERIES"));
		datasetTypes.put(DatasetType.CUSTOM_SUBOBSERVATIONS, new DatasetType(DatasetType.CUSTOM_SUBOBSERVATIONS, "CUSTOM"));

		return datasetTypes;
	}

}
