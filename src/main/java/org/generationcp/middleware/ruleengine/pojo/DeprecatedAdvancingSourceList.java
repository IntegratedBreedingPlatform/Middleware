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
 * The POJO for the Germplasm List when Advancing a Nursery.
 */
@Deprecated
public class DeprecatedAdvancingSourceList {

	private List<DeprecatedAdvancingSource> rows;

	public DeprecatedAdvancingSourceList() {
	}

	/**
	 * @return the rows
	 */
	public List<DeprecatedAdvancingSource> getRows() {
		return this.rows;
	}

	/**
	 * @param rows the rows to set
	 */
	public void setRows(List<DeprecatedAdvancingSource> rows) {
		this.rows = rows;
	}

}
