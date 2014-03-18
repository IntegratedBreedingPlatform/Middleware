/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.middleware.manager;

import org.generationcp.middleware.util.PropertyReader;

/**
 * Values are stored in gdmsTable.properties.
 *
 */
public enum GdmsTable {
	
	GDMS_DATASET("GDMS_DATASET_TABLE", "GDMS_DATASET_ID")
	, GDMS_MARKER("GDMS_MARKER_TABLE", "GDMS_MARKER_ID")
	, GDMS_CHAR_VALUES("GDMS_CHAR_VALUES_TABLE", "GDMS_CHAR_VALUES_ID")
	, GDMS_ALLELE_VALUES("GDMS_ALLELE_VALUES_TABLE", "GDMS_ALLELE_VALUES_ID")
	, GDMS_DART_VALUES("GDMS_DART_VALUES_TABLE", "GDMS_DART_VALUES_ID")
	, GDMS_MAPPING_POP_VAUES("GDMS_MAPPING_POP_VAUES_TABLE", "GDMS_MAPPING_POP_VAUES_ID")
	, GDMS_MTA("GDMS_MTA_TABLE", "GDMS_MTA_ID")
	;

	private final String tableName;
	private final String idName;
	
    private static final String PROPERTY_FILE = "constants/gdmsTable.properties";
    private static final PropertyReader propertyReader = new PropertyReader(PROPERTY_FILE);

	
	private GdmsTable(String tableName, String idName) {
		this.tableName = tableName;
		this.idName = idName;
	}

	public String getTableName() {
		return propertyReader.getValue(tableName);
	}

	public String getIdName() {
		return propertyReader.getValue(idName);
	}
	

}
