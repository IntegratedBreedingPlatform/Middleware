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

package org.generationcp.middleware.domain.oms;

/**
 * Contains the CV ID values used in Middleware.
 *
 */
public enum CvId {

	// Ontology
	IBDB_TERMS(1000), PROPERTIES(1010), TRAIT_CLASS(1011), METHODS(1020), SCALES(1030), DATA_TYPE(1031), VARIABLES(1040), VARIABLE_TYPE(1041);

	private final int id;

	CvId(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public static CvId valueOf(int id) {
		for (CvId cvId : CvId.values()) {
			if (cvId.getId() == id) {
				return cvId;
			}
		}

		return null;
	}
}
