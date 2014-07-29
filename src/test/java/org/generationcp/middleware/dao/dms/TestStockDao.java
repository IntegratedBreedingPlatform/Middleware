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

import org.generationcp.middleware.dao.dms.StockDao;
import org.generationcp.middleware.hibernate.HibernateUtil;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.util.Debug;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestStockDao{

    private static HibernateUtil hibernateUtil;
    private static StockDao dao;

    @BeforeClass
    public static void setUp() throws Exception {
        hibernateUtil = new HibernateUtil(new DatabaseConnectionParameters("testDatabaseConfig.properties", "local"));
        dao = new StockDao();
        dao.setSession(hibernateUtil.getCurrentSession());
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
        hibernateUtil.shutdown();
    }

}
