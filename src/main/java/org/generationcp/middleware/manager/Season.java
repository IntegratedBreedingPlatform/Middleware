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

/**
 * Used to specify the different seasons
 * 
 * @author Joyce Avestro, Tiffany Go
 * 
 */
public enum Season {
    DRY ("Dry season"), WET ("Wet season"), GENERAL;
    
    private String definition;
    
    private Season() {
    }
    
    private Season(String definition) {
    	this.definition = definition;
    }
    
    public String getDefinition() {
    	return this.definition;
    }
}
