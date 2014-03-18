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
 * The possible study types used in Middleware.
 * Values are stored in studyType.properties.
 *
 */
public enum StudyType {

	//CV_ID = 2010
    N("N_NAME", "N_ID", "N_LABEL")
    ,HB("HB_NAME", "H_ID", "H_LABEL")
    ,PN("PN_NAME", "PN_ID", "PN_LABEL")
    ,CN("CN_NAME", "CN_ID", "CN_LABEL")
    ,OYT("OYT_NAME", "OYT_ID", "OYT_LABEL")
    ,BON("BON_NAME", "BON_ID", "BON_LABEL")
    ,T("T_NAME", "T_ID", "T_LABEL")
    ,RYT("RYT_NAME", "RYT_ID", "RYT_LABEL")
    ,OFT("OFT_NAME", "OFT_ID", "OFT_LABEL")
    ,S("S_NAME", "S_ID", "S_LABEL")
    ,E("E_NAME", "E_ID", "E_LABEL");
	
	private final String name; 
    private final String id;
	private final String label;
	
    private static final String PROPERTY_FILE = "constants/studyType.properties";
    private static final PropertyReader propertyReader = new PropertyReader(PROPERTY_FILE);        

    private StudyType(String name, String id, String label) {
		this.name = name;
		this.id = id;
		this.label = label;
	}
	
	public int getId() {
	    return propertyReader.getIntegerValue(this.id);
    }

    public String getName() {
        return propertyReader.getValue(this.name);
    }

    public String getLabel() {
        return propertyReader.getValue(this.label);
    }

    public static StudyType getStudyType(String name) {
        for (StudyType studyType : StudyType.values()) {
            if (studyType.getName().equals(name)) {
                return studyType;
            }
        }

        return null;
    }
    
            
}
