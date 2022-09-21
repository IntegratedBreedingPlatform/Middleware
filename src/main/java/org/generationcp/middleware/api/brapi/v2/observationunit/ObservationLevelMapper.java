package org.generationcp.middleware.api.brapi.v2.observationunit;

import org.generationcp.middleware.api.brapi.v2.observationlevel.ObservationLevelEnum;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;

public class ObservationLevelMapper {

	/**
	 * Map some accepted levels in brapi, otherwise return the dataset name (custom observationlevel as in /observationlevels)
	 */
	public static String getObservationLevelNameEnumByDataset(final DatasetTypeEnum datasetTypeEnum) {
		switch (datasetTypeEnum) {
			case PLOT_DATA:
				return ObservationLevelEnum.PLOT.getLevelName();
			case PLANT_SUBOBSERVATIONS:
				return ObservationLevelEnum.PLANT.getLevelName();
			case TIME_SERIES_SUBOBSERVATIONS:
				return ObservationLevelEnum.TIMESERIES.getLevelName();
			case QUADRAT_SUBOBSERVATIONS:
				return ObservationLevelEnum.SUB_PLOT.getLevelName();
			case CUSTOM_SUBOBSERVATIONS:
				return ObservationLevelEnum.CUSTOM.getLevelName();
			case MEANS_DATA:
				return ObservationLevelEnum.MEANS.getLevelName();
			case SUMMARY_STATISTICS_DATA:
				return ObservationLevelEnum.SUMMARY_STATISTICS.getLevelName();
			default:
				return datasetTypeEnum.getName();
		}
	}

	public static String getDatasetTypeNameByObservationLevelName(final String observationLevelName) {
		if (ObservationLevelEnum.PLOT.getLevelName().equalsIgnoreCase(observationLevelName)) {
			return DatasetTypeEnum.PLOT_DATA.getName();
		} else if (ObservationLevelEnum.PLANT.getLevelName().equalsIgnoreCase(observationLevelName)) {
			return DatasetTypeEnum.PLANT_SUBOBSERVATIONS.getName();
		} else if (ObservationLevelEnum.TIMESERIES.getLevelName().equalsIgnoreCase(observationLevelName)) {
			return DatasetTypeEnum.TIME_SERIES_SUBOBSERVATIONS.getName();
		} else if (ObservationLevelEnum.SUB_PLOT.getLevelName().equalsIgnoreCase(observationLevelName)) {
			return DatasetTypeEnum.QUADRAT_SUBOBSERVATIONS.getName();
		} else if (ObservationLevelEnum.CUSTOM.getLevelName().equalsIgnoreCase(observationLevelName)) {
			return DatasetTypeEnum.CUSTOM_SUBOBSERVATIONS.getName();
		} else if (ObservationLevelEnum.MEANS.getLevelName().equalsIgnoreCase(observationLevelName)) {
			return DatasetTypeEnum.MEANS_DATA.getName();
		} else if (ObservationLevelEnum.SUMMARY_STATISTICS.getLevelName().equalsIgnoreCase(observationLevelName)) {
			return DatasetTypeEnum.SUMMARY_STATISTICS_DATA.getName();
		}
		return observationLevelName;
	}

}
