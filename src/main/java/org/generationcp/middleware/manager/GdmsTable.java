package org.generationcp.middleware.manager;

public enum GdmsTable {
	
	GDMS_DATASET("gdms_dataset", "dataset_id")
	, GDMS_MARKER("gdms_marker", "marker_id")
	, GDMS_CHAR_VALUES("gdms_char_values", "ac_id")
	, GDMS_ALLELE_VALUES("gdms_allele_values", "an_id")
	, GDMS_DART_VALUES("gdms_dart_values", "ad_id")
	, GDMS_MAPPING_POP_VAUES("gdms_mapping_pop_values", "mp_id")
	, GDMS_MTA("gdms_mta", "mta_id")
	;

	private final String tableName;
	private final String idName;
	
	private GdmsTable(String tableName, String idName) {
		this.tableName = tableName;
		this.idName = idName;
	}

	public String getTableName() {
		return tableName;
	}

	public String getIdName() {
		return idName;
	}

}
