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
package org.generationcp.middleware.domain.fieldbook;

import org.generationcp.middleware.domain.oms.TermId;

/**
 * List of factors which are non editable
 * @author chezka
 *
 */
public enum NonEditableFactors {

	ENTRY_NO(TermId.ENTRY_NO.getId()), 
	ENTRY_CODE(TermId.ENTRY_CODE.getId()), 
	DESIG(TermId.DESIG.getId()), 
	CROSS(TermId.CROSS.getId()), 
	GID(TermId.GID.getId()),
	PLOT_NO(TermId.PLOT_NO.getId()), 
	SEED_SOURCE(TermId.SEED_SOURCE.getId()), 
	COLUMN_NO(TermId.COLUMN_NO.getId()), 
	RANGE_NO(TermId.RANGE_NO.getId());
	
	private int id;
	
	private NonEditableFactors(int id) {
		this.setId(id);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public static NonEditableFactors find(Integer id) {
		for (NonEditableFactors factor : NonEditableFactors.values()) {
			if (factor.getId() == id) {
				return factor;
			}
		}
		return null;
	}
}
