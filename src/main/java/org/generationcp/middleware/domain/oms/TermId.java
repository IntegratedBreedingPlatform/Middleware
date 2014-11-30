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


/**
 * The cvterm ID constants used in Middleware.
 *
 */
public enum TermId {

	//Standard Variable
	STANDARD_VARIABLE(1070)
	, STUDY_INFORMATION(1010)
	, VARIABLE_DESCRIPTION(1060)
	, MULTIFACTORIAL_INFO (1100)
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
	, GENERAL_TRAIT_CLASS(8580)
	//, UNCLASSIFIED_TRAIT_CLASS(32789)
	
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
	, STUDY_UPDATE(8009)
	
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
	
	//Season
	, SEASON(2452)
	, SEASON_VAR(8371) 
	, SEASON_WET(10300)
	, SEASON_DRY(10290)
	, SEASON_VAR_TEXT(8370)
	, SEASON_MONTH (8369)
	
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
    , PLOT_CODE (8350)
    , REP_NO (8210)
    , BLOCK_NO (8220)
    , COLUMN_NO (8400) //
    //(32769)
    , RANGE_NO (8410)
    //(32770)
    ,ROW (8581)
    ,COL (8582)
    , BLOCK_NAME (8221)
    , COLUMNS_IN_BLOCK (32772)
    , RANGES_IN_BLOCK (32773)
    , PLANTING_ORDER (32774)
    , ROWS_PER_PLOT (32780)
    , FIELD_NAME (32783)
    , FIELDMAP_UUID (32785)
    , MACHINE_ROW_CAPACITY (32787)
    , BLOCK_ID (8583)// (77783)
	
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
	, ENTRY_NO (8230)
	, SOURCE (8360)
	, CROSS (8377)
	, DESIG (8250)
	, CHECK (8255)
	
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
	, ACTIVE_STUDY(12960)
	
	//Planting Order
	, ROW_COLUMN (32778)
	, SERPENTINE (32779)
	
	, BREEDING_METHOD_ID (8257)
	, BREEDING_METHOD (8256)
	, BREEDING_METHOD_CODE (8251)
	
	//Advance Nursery
	, PLANTS_SELECTED (8263)
	
	//Manage Settings
	, NURSERY_TYPE (8065)
		
    //Experimental Design
    , EXPERIMENT_DESIGN_FACTOR (8135)
    , NUMBER_OF_REPLICATES (8131)
    , BLOCK_SIZE (8132)
    , BLOCKS_PER_REPLICATE (77797) //TODO: NOT IN DB, REMOVE
    , REPLICATIONS_MAP (8133)
    , NO_OF_REPS_IN_COLS (8134)
    , NO_OF_ROWS_IN_REPS (8136)
    , NO_OF_COLS_IN_REPS (8137)
    , NO_OF_CROWS_LATINIZE (8138)
    , NO_OF_CCOLS_LATINIZE (8139)
    , NO_OF_CBLKS_LATINIZE (8142)
    
    //Experimental Design Factor Possible Values
    , RANDOMIZED_COMPLETE_BLOCK (10110)
    , RESOLVABLE_INCOMPLETE_BLOCK (10130)
    , RESOLVABLE_INCOMPLETE_ROW_COL (10145)
    , RESOLVABLE_INCOMPLETE_BLOCK_LATIN (10166)
    , RESOLVABLE_INCOMPLETE_ROW_COL_LATIN (10167)
    
    //Replications Map values
    , REPS_IN_SINGLE_ROW (8143)
    , REPS_IN_SINGLE_COL (8144)
    , REPS_IN_ADJACENT_COLS( 8145)

    //Selection Variates Properties
    , BREEDING_METHOD_PROP (2670)
    , PLANTS_SELECTED_PROP (2660)
    
    , BREEDING_METHOD_VARIATE (8262)
    , BREEDING_METHOD_VARIATE_CODE (8252)
    , SEED_SOURCE (8360)
    , GERMPLASM_SOURCE (8378)
    , BREEDING_METHOD_VARIATE_TEXT (8261)
    
    , INVENTORY_AMOUNT_PROPERTY (2620)

    //Method Classes
    , BULKING_BREEDING_METHOD_CLASS (1490)
    , NON_BULKING_BREEDING_METHOD_CLASS (1510)
    , SEED_INCREASE_METHOD_CLASS (1530)
    , SEED_ACQUISITION_METHOD_CLASS (1540)
    , CULTIVAR_FORMATION_METHOD_CLASS (1550)
    , CROSSING_METHODS_CLASS (1560)
    , MUTATION_METHODS_CLASS (1570)
    , GENETIC_MODIFICATION_CLASS (1590)
    , CYTOGENETIC_MANIPULATION (1580)
    
    //Check Variables
    , CHECK_START (8411)
    , CHECK_INTERVAL (8412)
    , CHECK_PLAN (8413)
;
	
	private final int id;
	
	private TermId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return this.id;
	}
	
	public static TermId getById(int id) {
		for (TermId term : values()) {
			if (term.getId() == id) {
				return term;
			}
		}
		return null;
	}
}
