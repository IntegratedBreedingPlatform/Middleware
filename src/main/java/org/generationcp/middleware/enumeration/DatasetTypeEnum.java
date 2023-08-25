package org.generationcp.middleware.enumeration;

import org.generationcp.middleware.api.brapi.v2.observationlevel.ObservationLevelEnum;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum DatasetTypeEnum {

	STUDY_CONDITIONS(1, "STUDY", ObservationLevelEnum.STUDY),
	MEANS_DATA(2, "MEANS", ObservationLevelEnum.MEANS),
	SUMMARY_DATA(3, "SUMMARY", null),
	PLOT_DATA(4, "PLOT", ObservationLevelEnum.PLOT),
	PLANT_SUBOBSERVATIONS(5, "PLANT", ObservationLevelEnum.PLANT),
	QUADRAT_SUBOBSERVATIONS(6, "QUADRAT", ObservationLevelEnum.SUB_PLOT),
	TIME_SERIES_SUBOBSERVATIONS(7, "TIMESERIES", ObservationLevelEnum.TIMESERIES),
	CUSTOM_SUBOBSERVATIONS(8, "CUSTOM", ObservationLevelEnum.CUSTOM),
	SUB_SAMPLE_DATA(9, "SS", null),
	WEATHER_DATA(10, "WD", null),
	MEANS_OVER_TRIAL_INSTANCES(11, "OM", null),
	SUMMARY_STATISTICS_DATA(12, "SUMMARY_STATISTICS", ObservationLevelEnum.SUMMARY_STATISTICS);

	public static final List<Integer> ANALYSIS_RESULTS_DATASET_IDS = Arrays.asList(MEANS_DATA.getId(), SUMMARY_STATISTICS_DATA.getId());

	private static final Map<Integer, DatasetTypeEnum> lookup = new HashMap<>();
	private static final Map<String, DatasetTypeEnum> lookupByName = new HashMap<>();

	static {
		for (final DatasetTypeEnum datasetTypeEnum : DatasetTypeEnum.values()) {
			lookup.put(datasetTypeEnum.getId(), datasetTypeEnum);
			lookupByName.put(datasetTypeEnum.getName(), datasetTypeEnum);
		}
	}

	public static DatasetTypeEnum get(final Integer id) {
		return lookup.get(id);
	}

	public static DatasetTypeEnum getByName(final String name) {
		return lookupByName.get(name);
	}

	private final int id;
	private final String name;

	final ObservationLevelEnum observationLevelEnum;

	DatasetTypeEnum(final int id, final String name, final ObservationLevelEnum observationLevelEnum) {
		this.id = id;
		this.name = name;
		this.observationLevelEnum = observationLevelEnum;
	}

	public int getId() {
		return this.id;
	}

	public String getName() {
		return name;
	}

	public ObservationLevelEnum getObservationLevel() {
		return this.observationLevelEnum;
	}
}
