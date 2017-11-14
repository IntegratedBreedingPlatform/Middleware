/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.domain.dms;

import org.generationcp.middleware.domain.oms.TermId;

/**
 * The constants of StudyProperty - e.g. USER_ID, STUDY_TYPE, START_DATE.
 *
 */
public enum StudyPropertyConstants {

	USER_ID(TermId.STUDY_UID.getId(), "USERID", "User ID", 1), START_DATE(
			TermId.START_DATE.getId(), "START", "Start date", 3);

	private Integer cvTermId;
	private String name;
	private String description;
	private Integer rank;

	StudyPropertyConstants(Integer cvTermId, String name, String description, Integer rank) {
		this.cvTermId = cvTermId;
		this.name = name;
		this.description = description;
		this.rank = rank;
	}

	public Integer getCvTermId() {
		return this.cvTermId;
	}

	public String getName() {
		return this.name;
	}

	public String getDescription() {
		return this.description;
	}

	public Integer getRank() {
		return this.rank;
	}

}
