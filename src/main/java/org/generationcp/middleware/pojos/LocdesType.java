package org.generationcp.middleware.pojos;

public enum LocdesType {
	
	COLUMNS_IN_BLOCK(10002)
	, RANGES_IN_BLOCK(10003)
	, PLANTING_ORDER(10005)
	, ROWS_IN_PLOT(10004)
	, MACHINE_ROW_CAPACITY(10006)
	, DELETED_PLOTS(10007)
	;
	
	private int id;
	
	private LocdesType(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
