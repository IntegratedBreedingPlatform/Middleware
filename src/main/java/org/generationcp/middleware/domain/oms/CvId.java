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
 * Contains the CV ID values used in Middleware.
 *
 */
public enum CvId {

	//Ontology
	IBDB_TERMS(1000)
	,PROPERTIES(1010)
	,METHODS(1020)
	,SCALES(1030)
	,VARIABLES(1040)
	;
	
	private final int id;
	
	private CvId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return this.id;
	}
}
