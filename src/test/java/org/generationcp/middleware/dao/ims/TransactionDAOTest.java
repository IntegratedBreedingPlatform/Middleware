package org.generationcp.middleware.dao.ims;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.MiddlewareIntegrationTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TransactionDAOTest extends MiddlewareIntegrationTest {

	private static TransactionDAO dao;

    @BeforeClass
    public static void setUp() throws Exception {
        dao = new TransactionDAO();
        dao.setSession(sessionUtil.getCurrentSession());
    }
    
    @Test
    public void testRetrieveStockIds(){
    	
    	List<Integer> lRecIDs = new ArrayList<>();
    	lRecIDs.add(1);
    	Map<Integer, String> lRecIDStockIDMap = dao.retrieveStockIds(lRecIDs);
    	
    }
    
    @AfterClass
    public static void tearDown() throws Exception {
        dao.setSession(null);
        dao = null;
    }
}
