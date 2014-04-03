/*******************************************************************************
 * Copyright (c) 2014, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.middleware.pojos;

/**
 * Location type = fcode in udflds table.
 * 
 */
public enum LocationType {

    COUNTRY("COUNTRY")
    , PROV("PROV")
    , BREED("BREED")
    , BLOCK("BLOCK")
    , FIELD("FIELD")
    ;
	private String code;
	
	private LocationType(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
