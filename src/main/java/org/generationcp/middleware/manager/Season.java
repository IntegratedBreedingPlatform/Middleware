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

/**
 * Used to specify the different seasons
 * 
 * @author Joyce Avestro, Tiffany Go
 * 
 */
public enum Season {
    DRY ("Dry", "Dry season", 0), WET ("Wet", "Wet season", 1), GENERAL("General", "General", 2);
    
    private String label;

    private String definition;
    
    private Integer sortOrder;
    
    private Season() {
    }
    
    private Season(String label, String definition, Integer sortOrder) {
        this.label = label;
    	this.definition = definition;
    	this.sortOrder = sortOrder;
    }
    
    public String getLabel() {
        return this.label;
    }
    
    public String getDefinition() {
        return this.definition;
    }
    
    public Integer getSortOrder(){
        return this.sortOrder;
    }
    
    /**
     * Returns the corresponding Season object based on the given string input
     * @param seasonStr
     * @return the Season based on the given String
     */
    public static Season getSeason(String seasonStr){
        Season season = Season.GENERAL;

        if (seasonStr == null || seasonStr.isEmpty()) {
            return season;
        }

        if (seasonStr != null && Integer.parseInt(seasonStr.trim()) == TermId.SEASON_DRY.getId()){
            season = Season.DRY;
        } else if (seasonStr != null && Integer.parseInt(seasonStr.trim()) == TermId.SEASON_WET.getId()){
            season = Season.WET;
        }

        return season;
    }
}
