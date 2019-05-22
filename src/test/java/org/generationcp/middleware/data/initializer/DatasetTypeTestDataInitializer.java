package org.generationcp.middleware.data.initializer;

import org.generationcp.middleware.domain.dms.DatasetTypeDTO;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;

import java.util.HashMap;
import java.util.Map;

public class DatasetTypeTestDataInitializer {

	public static Map<Integer, DatasetTypeDTO> createDatasetTypes() {

		final Map<Integer, DatasetTypeDTO> datasetTypes = new HashMap<>();

		datasetTypes.put(DatasetTypeEnum.STUDY_CONDITIONS.getId(), new DatasetTypeDTO(DatasetTypeEnum.STUDY_CONDITIONS.getId(), "STUDY"));
		datasetTypes.put(DatasetTypeEnum.MEANS_DATA.getId(), new DatasetTypeDTO(DatasetTypeEnum.MEANS_DATA.getId(), "MEANS"));
		datasetTypes.put(DatasetTypeEnum.SUMMARY_DATA.getId(), new DatasetTypeDTO(DatasetTypeEnum.SUMMARY_DATA.getId(), "SUMMARY"));
		datasetTypes.put(DatasetTypeEnum.PLOT_DATA.getId(), new DatasetTypeDTO(DatasetTypeEnum.PLOT_DATA.getId(), "PLOT"));
		datasetTypes.put(DatasetTypeEnum.PLANT_SUBOBSERVATIONS.getId(), new DatasetTypeDTO(DatasetTypeEnum.PLANT_SUBOBSERVATIONS.getId(), "PLANT"));
		datasetTypes.put(DatasetTypeEnum.QUADRAT_SUBOBSERVATIONS.getId(), new DatasetTypeDTO(DatasetTypeEnum.QUADRAT_SUBOBSERVATIONS.getId(), "QUADRAT"));
		datasetTypes.put(DatasetTypeEnum.TIME_SERIES_SUBOBSERVATIONS.getId(), new DatasetTypeDTO(DatasetTypeEnum.TIME_SERIES_SUBOBSERVATIONS.getId(), "TIMESERIES"));
		datasetTypes.put(DatasetTypeEnum.CUSTOM_SUBOBSERVATIONS.getId(), new DatasetTypeDTO(DatasetTypeEnum.CUSTOM_SUBOBSERVATIONS.getId(), "CUSTOM"));

		return datasetTypes;
	}

}
