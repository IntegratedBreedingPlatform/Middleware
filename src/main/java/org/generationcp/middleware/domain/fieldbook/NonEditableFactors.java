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

package org.generationcp.middleware.domain.fieldbook;

import org.generationcp.middleware.domain.oms.TermId;

// TODO: Auto-generated Javadoc
/**
 * List of factors which are non editable.
 *
 * @author chezka
 */
public enum NonEditableFactors {

	/** The entry no. */
	ENTRY_NO(TermId.ENTRY_NO.getId()),

	/** The entry code. */
	ENTRY_CODE(TermId.ENTRY_CODE.getId()),

	/** The desig. */
	DESIG(TermId.DESIG.getId()),

	/** The cross. */
	CROSS(TermId.CROSS.getId()),

	/** The gid. */
	GID(TermId.GID.getId()),

	/** The plot no. */
	PLOT_NO(TermId.PLOT_NO.getId()),

	/** The seed source. */
	SEED_SOURCE(TermId.SEED_SOURCE.getId()),

	/** The column no. */
	COLUMN_NO(TermId.COLUMN_NO.getId()),

	/** The range no. */
	RANGE_NO(TermId.RANGE_NO.getId()),

	ENTRY_TYPE(TermId.ENTRY_TYPE.getId()),

	TRIAL_INSTANCE(TermId.TRIAL_INSTANCE_FACTOR.getId()),

	GROUPGID(TermId.GROUPGID.getId()),

	STOCKID(TermId.STOCKID.getId()),

	/** The plot id. */
	PLOT_ID(TermId.PLOT_ID.getId());



	/** The id. */
	private int id;

	/**
	 * Instantiates a new non editable factors.
	 *
	 * @param id the id
	 */
	private NonEditableFactors(int id) {
		this.setId(id);
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Find.
	 *
	 * @param id the id
	 * @return the non editable factors
	 */
	public static NonEditableFactors find(Integer id) {
		for (NonEditableFactors factor : NonEditableFactors.values()) {
			if (factor.getId() == id) {
				return factor;
			}
		}
		return null;
	}
}
