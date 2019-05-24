package org.generationcp.middleware.enumeration;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import javax.annotation.Nullable;
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

	public static final Integer[] SUB_OBSERVATION_IDS;

	public static final Integer[] OBSERVATION_IDS;

	public static final List<DatasetTypeEnum> SUB_OBSERVATION =
		Arrays.asList(PLANT_SUBOBSERVATIONS, QUADRAT_SUBOBSERVATIONS, TIME_SERIES_SUBOBSERVATIONS, CUSTOM_SUBOBSERVATIONS);


	public static final List<DatasetTypeEnum> OBSERVATIONS =
		Arrays.asList(PLOT_DATA, PLANT_SUBOBSERVATIONS, QUADRAT_SUBOBSERVATIONS, TIME_SERIES_SUBOBSERVATIONS, CUSTOM_SUBOBSERVATIONS);
	private static final Map<Integer, DatasetTypeEnum> lookup = new HashMap<>();

	static {
		for (final DatasetTypeEnum datasetTypeEnum : DatasetTypeEnum.values()) {
			lookup.put(datasetTypeEnum.getId(), datasetTypeEnum);
		}

		SUB_OBSERVATION_IDS = Lists.transform(SUB_OBSERVATION, new Function<DatasetTypeEnum, Integer>() {

			@Nullable
			@Override
			public Integer apply(@Nullable final DatasetTypeEnum dataSetType) {
				return dataSetType.getId();
			}
		}).toArray(new Integer[0]);

		OBSERVATION_IDS = Lists.transform(OBSERVATIONS, new Function<DatasetTypeEnum, Integer>() {

			@Nullable
			@Override
			public Integer apply(@Nullable final DatasetTypeEnum dataSetType) {
				return dataSetType.getId();
			}
		}).toArray(new Integer[0]);
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

}
