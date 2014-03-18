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
package org.generationcp.middleware.manager;

import java.io.IOException;
import java.util.Properties;

import org.generationcp.middleware.domain.oms.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The different types for germplasm names. Taken from the udflds table. The
 * primary key is included for querying purposes.
 * Values are stored in germplasmNameType.properties
 * 
 * @author Kevin Manansala
 * 
 */
public enum GermplasmNameType {
    GERMPLASM_BANK_ACCESSION_NUMBER
    , CROSS_NAME
    , UNNAMED_CROSS
    , RELEASE_NAME
    , DERIVATIVE_NAME
    , CULTIVAR_NAME
    , ABBREVIATED_CULTIVAR_NAME
    , SPECIES_NAME
    , COLLECTORS_NUMBER
    , FOREIGN_ACCESSION_NUMBER
    , INTERNATIONAL_TESTING_NUMBER
    , NATIONAL_TESTING_NUMBER
    , LINE_NAME
    , TEMPORARY_ACCESSION_NUMBER_QUARANTINE_NUMBER
    , ALTERNATIVE_DERIVATIVE_NAME
    , ALTERNATIVE_CULTIVAR_NAME
    , ALTERNATIVE_ABBREVIATION
    , OLD_MUTANT_NAME_1
    , OLD_MUTANT_NAME_2
    , ELITE_LINES
    , MANAGEMENT_NAME
    , DONORS_ACCESSION_NUMBER
    , LOCAL_COMMON_NAME_OF_WILD_RICE_SPECIE
    , IRRI_HRDC_CODE
    , GQNPC_Unique_ID
    , NATIONAL_RICE_COOPERATIVE_TESTING_PROJECT_ID
    , TRANSGENIC_EVENT_ID
    , ALTERNATE_CROSS_NAME
    , CIAT_GERMPLASM_BANK_ACCESSION_NUMBER
    , UNRESOLVED_NAME
    , CIMMYT_SELECTION_HISTORY
    , CIMMYT_WHEAT_PEDIGREE
    ;

    private static final Logger LOG = LoggerFactory.getLogger(GermplasmNameType.class);
    
    private static final String PROPERTY_FILE = "constants/germplasmNameType.properties";

    public int getId(){
        int id = 0;
        String value = null;
        Properties configFile = new Properties();
        try {
            configFile.load(TermId.class.getClassLoader().getResourceAsStream(PROPERTY_FILE));
            value = configFile.getProperty(this.toString().trim());
            id = Integer.valueOf(value);
        } catch (NumberFormatException e) {
            LOG.error("Invalid ID: " + value, e);
        } catch (IOException e) {
            LOG.error("Error accessing property file: " + PROPERTY_FILE, e); 
        }
        if (id == 0){
            LOG.error("ID value not found: " + value);
        }
        return id;
    }
    
    public int getUserDefinedFieldID() {
        return getId();
    }
    
}
