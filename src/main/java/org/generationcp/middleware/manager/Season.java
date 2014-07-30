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

import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.util.PropertyReader;

/**
 * Used to specify the different seasons
 * Values are stored in season.properties
 * 
 * @author Joyce Avestro, Tiffany Go
 * 
 */
public enum Season {
    DRY ("DRY_LABEL", "DRY_DEFINITION", "DRY_SORT_ORDER")
    , WET ("WET_LABEL", "WET_DEFINITION", "WET_SORT_ORDER")
    , GENERAL("GENERAL_LABEL", "GENERAL_DEFINITION", "GENERAL_SORT_ORDER");
    
    private String label;

    private String definition;
    
    private String sortOrder;
    
    private static final String PROPERTY_FILE = "constants/season.properties";
    private static final PropertyReader propertyReader = new PropertyReader(PROPERTY_FILE);

    
    private Season(String label, String definition, String sortOrder) {
        this.label = label;
    	this.definition = definition;
    	this.sortOrder = sortOrder;
    }

    public String getLabel() {
        return propertyReader.getValue(this.label);
    }
    
    public String getDefinition() {
        return propertyReader.getValue(this.definition);
    }
    
    public Integer getSortOrder(){
        return propertyReader.getIntegerValue(this.sortOrder);
    }
    
    /**
     * Returns the corresponding Season object based on the given string input
     * @param seasonStr
     * @return the Season based on the given String
     */
    public static Season getSeason(String seasonStr){
        Season season = Season.GENERAL;
        if (seasonStr != null && Integer.parseInt(seasonStr.trim()) == TermId.SEASON_DRY.getId()){
            season = Season.DRY;
        } else if (seasonStr != null && Integer.parseInt(seasonStr.trim()) == TermId.SEASON_WET.getId()){
            season = Season.WET;
        }
        return season;
    }
}
