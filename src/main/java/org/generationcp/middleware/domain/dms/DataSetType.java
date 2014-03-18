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
package org.generationcp.middleware.domain.dms;

import org.generationcp.middleware.util.PropertyReader;

/**
 * The different dataset types used - e.g. study conditions, means, summary, plot.
 * Values are stored in dataSetType.properties.
 *
 */
public enum DataSetType {

	STUDY_CONDITIONS
	, MEANS_DATA
	, SUMMARY_DATA
	, PLOT_DATA
	;
	
    private static final String PROPERTY_FILE = "constants/dataSetType.properties";
    private static final PropertyReader propertyReader = new PropertyReader(PROPERTY_FILE);

    public int getId(){
        return propertyReader.getIntegerValue(this.toString().trim());
    }
	

    public static DataSetType findById(int id) {
        for (DataSetType type : DataSetType.values()) {
            if (type.getId() == id) {
                return type;
            }
        }
        return null;
    }
    
	
	
}
