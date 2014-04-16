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

import org.generationcp.middleware.util.PropertyReader;

/**
 * The cvterm ID constants used in Middleware.
 * Values are stored in termId.properties.
 *
 */
  public enum TermId {
    
    //Standard Variable
    STANDARD_VARIABLE
    , STUDY_INFORMATION
    , VARIABLE_DESCRIPTION
    , MULTIFACTORIAL_INFO
    , IBDB_STRUCTURE
    
    //CV Term Relationship
    , HAS_METHOD
    , HAS_PROPERTY
    , HAS_SCALE
    , HAS_TYPE
    , HAS_VALUE
    , IS_A
    , STORED_IN
    
    //Ontology
    , IBDB_CLASS
    , ONTOLOGY_TRAIT_CLASS
    , ONTOLOGY_RESEARCH_CLASS
    , UNCLASSIFIED_TRAIT_CLASS
    
    //Study Fields
    , STUDY_NAME
    , PM_KEY
    , STUDY_TITLE
    , STUDY_OBJECTIVE
    , PI_ID
    , PI_NAME
    , STUDY_TYPE
    , START_DATE
    , END_DATE
    , STUDY_UID
    /*, STUDY_IP("8120")*/
    , CREATION_DATE
    , STUDY_STATUS
    
    // Dataset Fields
    , DATASET_NAME
    , DATASET_TITLE
    , DATASET_TYPE
    
    // Variable Types
    , CLASS
    , NUMERIC_VARIABLE
    , DATE_VARIABLE
    , NUMERIC_DBID_VARIABLE
    , CHARACTER_DBID_VARIABLE
    , CHARACTER_VARIABLE
    , TIMESTAMP_VARIABLE
    , CATEGORICAL_VARIABLE
    
    // Variate Types
    , OBSERVATION_VARIATE
    , CATEGORICAL_VARIATE
    
    // Folder, Study, Dataset Nodes
    , HAS_PARENT_FOLDER
    , STUDY_HAS_FOLDER
    , BELONGS_TO_STUDY
    , IS_STUDY
    
    // Season
    , SEASON
    , SEASON_VAR 
    , SEASON_WET
    , SEASON_DRY
    
    , GID
    
    // Experiment Types
    , STUDY_EXPERIMENT
    , DATASET_EXPERIMENT
    , TRIAL_ENVIRONMENT_EXPERIMENT
    , PLOT_EXPERIMENT
    , SAMPLE_EXPERIMENT
    , AVERAGE_EXPERIMENT
    , SUMMARY_EXPERIMENT
    
    // Location storage
    , TRIAL_ENVIRONMENT_INFO_STORAGE
    , TRIAL_INSTANCE_STORAGE
    , LATITUDE_STORAGE
    , LONGITUDE_STORAGE
    , DATUM_STORAGE
    , ALTITUDE_STORAGE
    
    // Germplasm storage
    , GERMPLASM_ENTRY_STORAGE
    , ENTRY_NUMBER_STORAGE
    , ENTRY_GID_STORAGE
    , ENTRY_DESIGNATION_STORAGE
    , ENTRY_CODE_STORAGE

    // Stock Plot / Fieldmap 
    , PLOT_NO
    , PLOT_NNO
    , REP_NO
    , BLOCK_NO
    , COLUMN_NO //(8400) //(32769)
    , RANGE_NO //(8410) //(32770)
    , BLOCK_NAME
    , COLUMNS_IN_BLOCK
    , RANGES_IN_BLOCK
    , PLANTING_ORDER
    , ROWS_PER_PLOT
    , FIELD_NAME
    , FIELDMAP_UUID
    , MACHINE_ROW_CAPACITY
    , BLOCK_ID //(8583)// (77783)
    
    // Experiment storage
    , TRIAL_DESIGN_INFO_STORAGE
    
    // Study/DataSet storage
    , STUDY_NAME_STORAGE
    , STUDY_TITLE_STORAGE
    , DATASET_NAME_STORAGE
    , DATASET_TITLE_STORAGE
    , STUDY_INFO_STORAGE
    , DATASET_INFO_STORAGE
            
    // Other
    , ORDER
    , MIN_VALUE
    , MAX_VALUE
    , CROP_ONTOLOGY_ID

    // Stock Type
    , ENTRY_CODE
    , ENTRY_NO
    , SOURCE
    , CROSS
    , DESIG
    , CHECK
    
    // Location 
    , TRIAL_LOCATION
    , LOCATION_ID
    , SITE_NAME
    
    // Study Type
    , NURSERY
    , TRIAL
    
    // Main Factor ("Variable")
    , TRIAL_INSTANCE_FACTOR

    , DELETED_STUDY
    
    // Planting Order
    , ROW_COLUMN
    , SERPENTINE
    
    ,BREEDING_METHOD_ID
    ,BREEDING_METHOD
    
    // Breeding Methods
    , SINGLE_PLANT_SELECTION_SF
    , SELECTED_BULK_SF
    , RANDOM_BULK_SF
    
    // Advance Nursery
    , PLANTS_SELECTED
    
    //Manage Settings
    , NURSERY_TYPE
    
    //Experimental Design
    , EXPERIMENT_DESIGN_FACTOR
    , NUMBER_OF_REPLICATES
    , BLOCK_SIZE
    , BLOCKS_PER_REPLICATE
    ;

    private static final String PROPERTY_FILE = "constants/termId.properties";
    private static final PropertyReader propertyReader = new PropertyReader(PROPERTY_FILE);
    
    public int getId(){
        return propertyReader.getIntegerValue(this.toString().trim());
    }

}
	
