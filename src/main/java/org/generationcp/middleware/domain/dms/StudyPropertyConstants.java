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

import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.util.PropertyReader;

/**
 * The constants of StudyProperty - e.g. USER_ID, STUDY_TYPE, START_DATE.
 *
 */
public enum StudyPropertyConstants {

	USER_ID (TermId.STUDY_UID.getId(), "USER_ID_NAME", "USER_ID_DESCRIPTION", "USER_ID_RANK")
	, STUDY_TYPE (TermId.STUDY_TYPE.getId(), "STUDY_TYPE_NAME", "STUDY_TYPE_DESCRIPTION", "STUDY_TYPE_RANK")
	, START_DATE (TermId.START_DATE.getId(), "START_DATE_NAME", "START_DATE_DESCRIPTION", "START_DATE_RANK")
	;
	
	private Integer cvTermId;
	private String name;
	private String description;
	private String rank;
	
    private static final String PROPERTY_FILE = "constants/studyPropertyConstants.properties";
    private static final PropertyReader propertyReader = new PropertyReader(PROPERTY_FILE);

	
	StudyPropertyConstants(Integer cvTermId, String name, String description, String rank) {
		this.cvTermId = cvTermId;
		this.name = name;
		this.description = description;
		this.rank = rank;
	}
	
	public Integer getCvTermId() {
		return this.cvTermId;
	}
	
	public String getName() {
		return propertyReader.getValue(this.name);
	}
	
	public String getDescription() {
		return propertyReader.getValue(this.description);
	}
	
	public Integer getRank() {
		return propertyReader.getIntegerValue(this.rank);
	}
	
	
}
