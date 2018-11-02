/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.domain.dms;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

/**
 * The different dataset types used - e.g. study conditions, means, summary, plot.
 *
 */
public enum DataSetType {

	STUDY_CONDITIONS(10060), MEANS_DATA(10070), SUMMARY_DATA(10080), PLOT_DATA(10090), PLANT_SUBOBSERVATIONS(
			10094), QUADRAT_SUBOBSERVATIONS(10095), TIME_SERIES_SUBOBSERVATIONS(10096), CUSTOM_SUBOBSERVATIONS(10097);

	public static final List<DataSetType> SUBOBSERVATIONS =
		Arrays.asList(PLANT_SUBOBSERVATIONS, QUADRAT_SUBOBSERVATIONS, TIME_SERIES_SUBOBSERVATIONS, CUSTOM_SUBOBSERVATIONS);

	public static final Integer[] SUBOBSERVATION_IDS;

	static {
		SUBOBSERVATION_IDS = Lists.transform(SUBOBSERVATIONS, new Function<DataSetType, Integer>() {

			@Nullable
			@Override
			public Integer apply(@Nullable final DataSetType dataSetType) {
				return dataSetType.getId();
			}
		}).toArray(new Integer[0]);
	}

	private int id;

	private DataSetType(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public static DataSetType findById(int id) {
		for (DataSetType type : DataSetType.values()) {
			if (type.getId() == id) {
				return type;
			}
		}
		return null;
	}

	public static boolean isSubObservationDatasetType(final DataSetType type)  {
		return SUBOBSERVATIONS.contains(type);
	}
}
