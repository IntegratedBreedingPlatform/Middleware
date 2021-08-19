package org.generationcp.middleware.api.brapi.v2.observationunit;

import org.generationcp.middleware.enumeration.DatasetTypeEnum;

public class ObservationLevelMapper {

	/**
	 * https://wiki.brapi.org/index.php/Observation_Levels#Accepted_Levels
	 * Sticking to uppercase for backwards compatibility, although they are lowercase in the standard
	 * TODO review if there is any client expecting uppercase only
	 */
	public enum ObservationLevelEnum {
		STUDY("STUDY"),
		FIELD("FIELD"),
		ENTRY("ENTRY"),
		REP("REP"),
		BLOCK("BLOCK"),
		SUB_BLOCK("SUB-BLOCK"),
		PLOT("PLOT"),
		SUB_PLOT("SUB-PLOT"),
		POT("POT"),
		SAMPLE("SAMPLE"),
		PLANT("PLANT");

		private final String name;

		ObservationLevelEnum(final String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	/**
	 * TODO Continue IBP-4289, it can be used to map from observationlevel to nd_experimentprop
	 */
	public enum ObservationLevelProp {
		REP_NO,
		BLOCK_NO,
		PLOT_NO
	}

	/**
	 * Map some accepted levels in brapi, otherwise return the dataset name (custom observationlevel as in /observationlevels)
	 */
	public static String getObservationLevelNameEnumByDataset(final DatasetTypeEnum datasetTypeEnum) {
		switch (datasetTypeEnum) {
			case PLOT_DATA:
				return ObservationLevelEnum.PLOT.getName();
			case PLANT_SUBOBSERVATIONS:
				return ObservationLevelEnum.PLANT.getName();
			case QUADRAT_SUBOBSERVATIONS:
			case CUSTOM_SUBOBSERVATIONS:
			case TIME_SERIES_SUBOBSERVATIONS:
				return ObservationLevelEnum.SUB_PLOT.getName();
			default:
				return datasetTypeEnum.getName();
		}
	}

}
