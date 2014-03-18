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
 * Contains the CV ID values used in Middleware.
 * Values are stored in cvId.properties.
 *
 */
public enum CvId {

	//Ontology
	IBDB_TERMS
	,PROPERTIES
	,METHODS
	,SCALES
	,VARIABLES
	,STUDY_STATUS
	;
	
    private static final String PROPERTY_FILE = "constants/cvId.properties";
    private static final PropertyReader propertyReader = new PropertyReader(PROPERTY_FILE);

    public int getId(){
        return propertyReader.getIntegerValue(this.toString().trim());
    }

    public static CvId valueOf(int id) {
        for (CvId cvId : CvId.values()) {
            if (cvId.getId() == id) {
                return cvId;
            }
        }
        return null;
    }
    
    
}
