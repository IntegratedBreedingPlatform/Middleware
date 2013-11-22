/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.middleware.domain.oms;

import org.generationcp.middleware.manager.Season;

/**
 * The cvterm ID constants used in Middleware.
 *
 */
public enum TermId {

	//Standard Variable
	STANDARD_VARIABLE(1070)
	, STUDY_INFORMATION(1010)
	, VARIABLE_DESCRIPTION(1060)
	, IBDB_STRUCTURE(1000)
	
	//CV Term Relationship
	, HAS_METHOD(1210)
	, HAS_PROPERTY(1200)
	, HAS_SCALE(1220)
	, HAS_TYPE(1105)
	, HAS_VALUE(1190)
	, IS_A(1225)
	, STORED_IN(1044)
	
	//Ontology
	, IBDB_CLASS(1001)
	, ONTOLOGY_TRAIT_CLASS(1330)
	, ONTOLOGY_RESEARCH_CLASS(1045)
	
	//Study Fields
	, STUDY_NAME(8005)
	, PM_KEY(8040)
	, STUDY_TITLE(8007)
	, STUDY_OBJECTIVE(8030)
    , PI_ID(8110)
    , PI_NAME(8100)
	, STUDY_TYPE(8070)
	, START_DATE(8050)
	, END_DATE(8060)
	, STUDY_UID(8020)
	/*, STUDY_IP(8120)*/
	, CREATION_DATE(8048)
	, STUDY_STATUS(8006)
	
	// Dataset Fields
	, DATASET_NAME(8150)
	, DATASET_TITLE(8155)
	, DATASET_TYPE(8160)
	
	// Variable Types
	, CLASS(1090)
	, NUMERIC_VARIABLE(1110)
	, DATE_VARIABLE(1117)
	, NUMERIC_DBID_VARIABLE(1118)
	, CHARACTER_DBID_VARIABLE(1128)
    , CHARACTER_VARIABLE(1120)
    , TIMESTAMP_VARIABLE(1125)
    , CATEGORICAL_VARIABLE(1130)
    
	
	//Variate Types
	, OBSERVATION_VARIATE(1043)
	, CATEGORICAL_VARIATE(1048)
	
	//Folder, Study, Dataset Nodes
	, HAS_PARENT_FOLDER(1140)
	, STUDY_HAS_FOLDER(1145)
	, BELONGS_TO_STUDY(1150) 
	, IS_STUDY(1145)
	
	//Properties
	, SEASON(2452)
	, SEASON_WET(10300)
	, SEASON_DRY(10290)
	
	, GID(8240)
	
	// Experiment Types
	, STUDY_EXPERIMENT(1010)
	, DATASET_EXPERIMENT(1050)
	, TRIAL_ENVIRONMENT_EXPERIMENT(1020)
	, PLOT_EXPERIMENT(1155)
	, SAMPLE_EXPERIMENT(1160)
	, AVERAGE_EXPERIMENT(1170)
	, SUMMARY_EXPERIMENT(1180)
	
	// Location storage
	, TRIAL_ENVIRONMENT_INFO_STORAGE(1020)
    , TRIAL_INSTANCE_STORAGE(1021)
    , LATITUDE_STORAGE(1022)
    , LONGITUDE_STORAGE(1023)
    , DATUM_STORAGE(1024)
    , ALTITUDE_STORAGE(1025)
    
    // Germplasm storage
    , GERMPLASM_ENTRY_STORAGE(1040)
    , ENTRY_NUMBER_STORAGE(1041)
    , ENTRY_GID_STORAGE(1042)
    , ENTRY_DESIGNATION_STORAGE(1046)
    , ENTRY_CODE_STORAGE(1047)
    
    // Stock Plot / Fieldmap 
    , PLOT_NO (8200)
    , PLOT_NNO (8380)
    , REP_NO (8210)
    , ROW_NO (22769)
    , COLUMN_NO (22770)
    , BLOCK (8221)
    , TOTAL_ROWS (22772)
    , TOTAL_COLUMNS (22773)
    , PLANTING_ORDER (22774)
	
    // Experiment storage
    , TRIAL_DESIGN_INFO_STORAGE(1030)
    
    // Study/DataSet storage
    , STUDY_NAME_STORAGE(1011)
    , STUDY_TITLE_STORAGE(1012)
    , DATASET_NAME_STORAGE(1016)
    , DATASET_TITLE_STORAGE(1017)
    , STUDY_INFO_STORAGE(1010)
    , DATASET_INFO_STORAGE(1015)
    	
	// Other
    , ORDER(1420)
	, MIN_VALUE(1113)
	, MAX_VALUE(1115)
	, CROP_ONTOLOGY_ID(1226)

	// Stock Type
	, ENTRY_CODE(8300)
	
	//Location 
    , TRIAL_LOCATION(8180)
    , LOCATION_ID(8190)
    , SITE_NAME(8196)
	
	//Study Type
	, NURSERY(10000)
	, TRIAL(10010)
	
	//Main Factor (Variable)
	, TRIAL_INSTANCE_FACTOR(8170)
	
	, DELETED_STUDY(12990)
	
	//Planting Order
	, ROW_COLUMN (22778)
	, SERPENTINE (22779)
	;
	
	private final int id;
	
	private TermId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return this.id;
	}
}
