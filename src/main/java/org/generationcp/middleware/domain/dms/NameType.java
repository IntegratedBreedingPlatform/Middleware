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
 * The different name types available - e.g. alternative english, preferred english, alternative french.
 * The values are stored in termId.properties
 *
 */
public enum NameType {

	ALTERNATIVE_ENGLISH
	, PREFERRED_FRENCH
	, ALTERNATIVE_FRENCH
	;

    private static final String PROPERTY_FILE = "constants/termId.properties";
    private static final PropertyReader propertyReader = new PropertyReader(PROPERTY_FILE);

    public int getId(){
        return propertyReader.getIntegerValue(this.toString().trim());
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
