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

import java.util.Arrays;

/**
 * The different dataset types used - e.g. study conditions, means, summary, plot.
 *
 */
public enum DataSetType {

	STUDY_CONDITIONS(10060), MEANS_DATA(10070), SUMMARY_DATA(10080), PLOT_DATA(10090), PLANT_SUBOBSERVATIONS(
			10094), QUADRAT_SUBOBSERVATIONS(10095), TIME_SERIES_SUBOBSERVATIONS(10096), CUSTOM_SUBOBSERVATIONS(10097);

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
		return Arrays.asList(PLANT_SUBOBSERVATIONS, QUADRAT_SUBOBSERVATIONS, TIME_SERIES_SUBOBSERVATIONS, CUSTOM_SUBOBSERVATIONS)
				.contains(type);
	}
}
