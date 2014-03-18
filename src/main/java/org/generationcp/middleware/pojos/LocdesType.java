package org.generationcp.middleware.pojos;

public enum LocdesType {
	
	COLUMNS_IN_BLOCK("COL_IN_BLK")
	, RANGES_IN_BLOCK("RANGE_IN_BLK")
	, PLANTING_ORDER("PLOT_LYOUT")
	, ROWS_IN_PLOT("ROWS_IN_PLOT")
	, MACHINE_ROW_CAPACITY("MACHINE_CAP")
	, DELETED_PLOTS("DELETED_PLOT")
	;
	
	private String code;
	
	private LocdesType(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
