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

/**
 * The different name types available - e.g. alternative english, preferred english, alternative french.
 *
 */
public enum NameType {

	ALTERNATIVE_ENGLISH(1230),
	PREFERRED_FRENCH(1240),
	ALTERNATIVE_FRENCH(1250);
    
	private int id;

	private NameType(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}

	public static NameType find(Integer id) {
		for (NameType nameType : NameType.values()) {
			if (nameType.getId() == id) {
				return nameType;
			}
		}
		return null;
	}
}
