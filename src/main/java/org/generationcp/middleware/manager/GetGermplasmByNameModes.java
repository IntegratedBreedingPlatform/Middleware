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
 * Used to specify the different modes for searching germplasms by name.
 * 
 * @author Kevin Manansala
 * 
 */
public enum GetGermplasmByNameModes {
    NORMAL
    , SPACES_REMOVED
    , STANDARDIZED
    , SPACES_REMOVED_BOTH_SIDES // Remove spaces from both input and output
}
