/*******************************************************************************
 * Copyright (c) 2014, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.pojos;

/**
 * Locdes type = fcode in udflds table.
 *
 */
public enum LocdesType {

	ROWS_IN_BLOCK("COL_IN_BLK"), RANGES_IN_BLOCK("RANGE_IN_BLK"), PLANTING_ORDER("PLOT_LYOUT"), ROWS_IN_PLOT("ROWS_IN_PLOT"), MACHINE_ROW_CAPACITY(
			"MACHINE_CAP"), DELETED_PLOTS("DELETED_PLOT"), FIELD_PARENT("FIELD_PARENT"), BLOCK_PARENT("BLOCK_PARENT");

	private String code;

	private LocdesType(String code) {
		this.code = code;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
