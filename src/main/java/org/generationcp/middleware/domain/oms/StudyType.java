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
 * The possible study types used in Middleware.
 *
 */
public enum StudyType {

	//CV_ID = 2010
	N("N",10000)
	,HB("HB",10001)
	,PN("PN",10002)
	,CN("CN",10003)
	,OYT("OYT",10005)
	,BON("BON",10007)
	,T("T",10010)
	,RYT("RYT",10015)
	,OFT("OFT",10017)
	,S("S",10020)
	,E("E",10030);
	
	
	private final int id;
	private final String name; 
	
	private StudyType(String name, int id) {
		this.name = name;
		this.id = id;
	}
	
	public int getId() {
		return this.id;
	}

	public String getName() {
		return name;
	}
	
	
	
	
}
