package org.generationcp.middleware.enumeration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum DatasetTypeEnum {

	STUDY_CONDITIONS(1),
	MEANS_DATA(2),
	SUMMARY_DATA(3),
	PLOT_DATA(4),
	PLANT_SUBOBSERVATIONS(5),
	QUADRAT_SUBOBSERVATIONS(6),
	TIME_SERIES_SUBOBSERVATIONS(7),
	CUSTOM_SUBOBSERVATIONS(8),
	SUB_SAMPLE_DATA(9),
	WEATHER_DATA(10),
	MEANS_OVER_TRIAL_INSTANCES(11);

	public static final List<Integer> SUBOBSERVATIONS =
		Arrays.asList(PLANT_SUBOBSERVATIONS.getId(), QUADRAT_SUBOBSERVATIONS.getId(), TIME_SERIES_SUBOBSERVATIONS.getId(),
			CUSTOM_SUBOBSERVATIONS.getId());

	private static final Map<Integer, DatasetTypeEnum> lookup = new HashMap<>();

	static {
		for (final DatasetTypeEnum datasetTypeEnum : DatasetTypeEnum.values()) {
			lookup.put(datasetTypeEnum.getId(), datasetTypeEnum);
		}
	}

	public static DatasetTypeEnum get(final Integer id) {
		return lookup.get(id);
	}

	private final int id;

	DatasetTypeEnum(final int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public boolean isObservationType() {
		return Arrays.asList(PLOT_DATA, PLANT_SUBOBSERVATIONS, QUADRAT_SUBOBSERVATIONS, TIME_SERIES_SUBOBSERVATIONS, CUSTOM_SUBOBSERVATIONS)
			.contains(this);
	}

}
