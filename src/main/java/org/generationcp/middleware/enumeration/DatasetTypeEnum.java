package org.generationcp.middleware.enumeration;

import java.util.HashMap;
import java.util.Map;

public enum DatasetTypeEnum {

	STUDY_CONDITIONS(1, "STUDY"),
	MEANS_DATA(2, "MEANS"),
	SUMMARY_DATA(3, "SUMMARY"),
	PLOT_DATA(4, "PLOT"),
	PLANT_SUBOBSERVATIONS(5, "PLANT"),
	QUADRAT_SUBOBSERVATIONS(6, "QUADRAT"),
	TIME_SERIES_SUBOBSERVATIONS(7, "TIMESERIES"),
	CUSTOM_SUBOBSERVATIONS(8, "CUSTOM"),
	SUB_SAMPLE_DATA(9, "SS"),
	WEATHER_DATA(10, "WD"),
	MEANS_OVER_TRIAL_INSTANCES(11, "OM"),
	SUMMARY_STATISTICS_DATA(12, "SUMMARY");

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

	DatasetTypeEnum(final int id, final String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return this.id;
	}

	public String getName() {
		return name;
	}
}
