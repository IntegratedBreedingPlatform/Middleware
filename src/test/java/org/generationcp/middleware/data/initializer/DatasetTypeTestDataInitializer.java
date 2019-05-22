package org.generationcp.middleware.data.initializer;

import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.pojos.dms.DatasetType;

import java.util.HashMap;
import java.util.Map;

public class DatasetTypeTestDataInitializer {

	public static Map<Integer, DatasetType> createDatasetTypes() {

		final Map<Integer, DatasetType> datasetTypes = new HashMap<>();

		datasetTypes.put(DatasetTypeEnum.STUDY_CONDITIONS.getId(), new DatasetType(DatasetTypeEnum.STUDY_CONDITIONS.getId(), "STUDY"));
		datasetTypes.put(DatasetTypeEnum.MEANS_DATA.getId(), new DatasetType(DatasetTypeEnum.MEANS_DATA.getId(), "MEANS"));
		datasetTypes.put(DatasetTypeEnum.SUMMARY_DATA.getId(), new DatasetType(DatasetTypeEnum.SUMMARY_DATA.getId(), "SUMMARY"));
		datasetTypes.put(DatasetTypeEnum.PLOT_DATA.getId(), new DatasetType(DatasetTypeEnum.PLOT_DATA.getId(), "PLOT"));
		datasetTypes.put(DatasetTypeEnum.PLANT_SUBOBSERVATIONS.getId(), new DatasetType(DatasetTypeEnum.PLANT_SUBOBSERVATIONS.getId(), "PLANT"));
		datasetTypes.put(DatasetTypeEnum.QUADRAT_SUBOBSERVATIONS.getId(), new DatasetType(DatasetTypeEnum.QUADRAT_SUBOBSERVATIONS.getId(), "QUADRAT"));
		datasetTypes.put(DatasetTypeEnum.TIME_SERIES_SUBOBSERVATIONS.getId(), new DatasetType(DatasetTypeEnum.TIME_SERIES_SUBOBSERVATIONS.getId(), "TIMESERIES"));
		datasetTypes.put(DatasetTypeEnum.CUSTOM_SUBOBSERVATIONS.getId(), new DatasetType(DatasetTypeEnum.CUSTOM_SUBOBSERVATIONS.getId(), "CUSTOM"));

		return datasetTypes;
	}

}
