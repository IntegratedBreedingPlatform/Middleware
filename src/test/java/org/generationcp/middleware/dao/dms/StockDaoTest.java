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

package org.generationcp.middleware.dao.dms;

import java.util.List;

import org.generationcp.middleware.MiddlewareIntegrationTest;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.util.Debug;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class StockDaoTest extends MiddlewareIntegrationTest {

    private static StockDao dao;

    @BeforeClass
    public static void setUp() throws Exception {
        dao = new StockDao();
        dao.setSession(sessionUtil.getCurrentSession());
    }


    @Test
    public void testGetStocks() throws Exception {
        int projectId = -178; //-147;
        List<StockModel> stocks = dao.getStocks(projectId);
        Debug.println(0, "testGetStocks(projectId=" + projectId + ") RESULTS:");
        for (StockModel stock: stocks) {
        	Debug.println(3, stock.toString());
        }
//        assertFalse(stocks.isEmpty());
    }
    
    @AfterClass
    public static void tearDown() throws Exception {
        dao.setSession(null);
        dao = null;
    }

}
