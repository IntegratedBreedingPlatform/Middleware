/*******************************************************************************
 *
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.dao.dms;

import java.util.List;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.util.Debug;
import org.junit.Before;
import org.junit.Test;

public class StockDaoTest extends IntegrationTestBase {

	private StockDao dao;

	@Before
	public void setUp() throws Exception {
		this.dao = new StockDao();
		this.dao.setSession(this.sessionProvder.getSession());
	}

	@Test
	public void testGetStocks() throws Exception {
		int projectId = -178; // -147;
		List<StockModel> stocks = this.dao.getStocks(projectId);
		Debug.println(0, "testGetStocks(projectId=" + projectId + ") RESULTS:");
		for (StockModel stock : stocks) {
			Debug.println(3, stock.toString());
		}
		// assertFalse(stocks.isEmpty());
	}
}
