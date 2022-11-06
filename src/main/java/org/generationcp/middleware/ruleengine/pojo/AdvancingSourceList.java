/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.ruleengine.pojo;

import java.util.List;

/**
 *
 * The POJO for the Germplasm List when Advancing a Nursery.
 *
 */
public class AdvancingSourceList {

	private List<AdvancingSource> rows;

	public AdvancingSourceList() {
	}

	/**
	 * @return the rows
	 */
	public List<AdvancingSource> getRows() {
		return this.rows;
	}

	/**
	 * @param rows the rows to set
	 */
	public void setRows(List<AdvancingSource> rows) {
		this.rows = rows;
	}

}
