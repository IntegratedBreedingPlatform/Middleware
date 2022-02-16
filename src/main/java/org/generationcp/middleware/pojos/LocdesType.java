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

	ROWS_IN_BLOCK("COL_IN_BLK", 306),
	RANGES_IN_BLOCK("RANGE_IN_BLK", 307),
	PLANTING_ORDER("PLOT_LYOUT", 309),
	ROWS_IN_PLOT("ROWS_IN_PLOT", 308),
	MACHINE_ROW_CAPACITY("MACHINE_CAP", 310),
	DELETED_PLOTS("DELETED_PLOT", 311),
	FIELD_PARENT("FIELD_PARENT", 312),
	BLOCK_PARENT("BLOCK_PARENT", 313),
	FIELD("FIELD", 415),
	BLOCK("BLOCK", 416);

	private String code;
	private Integer id;

	private LocdesType(final String code, final Integer id) {
		this.code = code;
		this.id = id;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}
}
