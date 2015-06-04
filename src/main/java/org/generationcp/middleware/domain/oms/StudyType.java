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
 * The possible study types used in Middleware.
 *
 */
public enum StudyType {

	// CV_ID = 2010
	N("N", 10000, "Nursery"), HB("HB", 10001, "Hybridization Nursery"), PN("PN", 10002, "Pedigree Nursery"), CN("CN", 10003,
			"Characterization Nursery"), OYT("OYT", 10005, "Observational Yield Trial"), BON("BON", 10007, "BULU Observational Nursery"), T(
			"T", 10010, "Trial"), RYT("RYT", 10015, "Replication Yield Trial"), OFT("OFT", 10017, "On Form Trial"), S("S", 10020, "Survey"), E(
			"E", 10030, "Experiment");

	private final int id;
	private final String name;
	private final String label;

	private StudyType(String name, int id, String label) {
		this.name = name;
		this.id = id;
		this.label = label;
	}

	public int getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String getLabel() {
		return this.label;
	}

	public static StudyType getStudyType(String name) {
		for (StudyType studyType : StudyType.values()) {
			if (studyType.getName().equals(name)) {
				return studyType;
			}
		}
		return null;
	}

	public static StudyType getStudyTypeById(int id) {
		for (StudyType studyType : StudyType.values()) {
			if (studyType.getId() == id) {
				return studyType;
			}
		}
		return null;
	}
}
