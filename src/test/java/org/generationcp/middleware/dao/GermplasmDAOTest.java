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

package org.generationcp.middleware.dao;

import java.util.List;

import org.generationcp.middleware.DataManagerIntegrationTest;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.generationcp.middleware.util.Debug;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class GermplasmDAOTest extends DataManagerIntegrationTest {

    private static final String DUMMY_STOCK_ID = "USER-1-1";
    private static final Integer testGid1 = 1;
    private static final Integer testGid1_Gpid1 = 2;
    private static final Integer testGid1_Gpid2 = 3;
    private static GermplasmDAO dao;
    private static InventoryDataManager inventoryDM;
    private static GermplasmDataManager germplasmDataDM;
    private static Integer testTransactionID;
    private static String oldInventoryID;
    private static Integer oldGid1_Gpid1;
    private static Integer oldGid1_Gpid2;
    

    @BeforeClass
    public static void setUp() throws Exception {
    	inventoryDM = managerFactory.getInventoryDataManager();
    	germplasmDataDM = managerFactory.getGermplasmDataManager();
        dao = new GermplasmDAO();
        dao.setSession(managerFactory.getSessionProvider().getSession());
        updateInventory();
        updateProgenitors();
    }

    private static void updateProgenitors() throws MiddlewareQueryException {
    	Germplasm germplasm1 = germplasmDataDM.getGermplasmByGID(testGid1);
    	oldGid1_Gpid1 = germplasm1.getGpid1();
    	oldGid1_Gpid2 = germplasm1.getGpid2();
		germplasmDataDM.updateProgenitor(testGid1, testGid1_Gpid1, 1);
		germplasmDataDM.updateProgenitor(testGid1, testGid1_Gpid2, 2);
	}

	private static void updateInventory() throws MiddlewareQueryException {
    	List<Transaction> transactions = inventoryDM.getAllTransactions(0, 1);
    	if(transactions!=null && !transactions.isEmpty()) {
    		Transaction transaction = transactions.get(0);
    		testTransactionID = transaction.getId();
    		oldInventoryID = transaction.getInventoryID();
    		transaction.setInventoryID(DUMMY_STOCK_ID);
    		inventoryDM.updateTransaction(transaction);
    	}
	}

	/**
     * @Test public void testGetAll() throws Exception { List<Germplasm>
     *       germplsmList = dao.getAll(0, 5); Assert.assertTrue(germplsmList !=
     *       null); Assert.assertTrue(germplsmList.size() >= 5);
     * 
     *       Debug.println(0, "SEARCH RESULTS:"); for(Germplasm g :
     *       germplsmList) { Debug.println(0, g); } }
     * @Test public void testCountAll() throws Exception { long count =
     *       dao.countAll(); Assert.assertTrue(count != null);
     *       Debug.println(0, "COUNT = " + count); }
     * @Test public void testGetByPrefName() throws Exception { List<Germplasm>
     *       germplsmList = dao.getByPrefName("IR 64", 0, 5);
     *       Assert.assertTrue(germplsmList != null);
     *       Assert.assertTrue(germplsmList.size() >= 5);
     * 
     *       Debug.println(0, "SEARCH RESULTS:"); for(Germplasm g :
     *       germplsmList) { Debug.println(0, g); } }
     * @Test public void testCountByPrefName() throws Exception { BigInteger
     *       count = dao.countByPrefName("IR 64"); Assert.assertTrue(count !=
     *       null); Debug.println(0, "COUNT = " + count); }
     * @Test public void testGetProgenitorsByGIDWithPrefName() throws Exception
     *       { List<Germplasm> results = dao.getProgenitorsByGIDWithPrefName(new
     *       Integer(306436)); Assert.assertTrue(results.size() > 0);
     *       Debug.println(0, "RESULTS:"); for(Germplasm g : results) {
     *       Debug.println(0, g); Debug.println(0, g.getPreferredName()); }
     *       }
     **/
    @Test
    public void testGetDerivativeChildren() throws Exception {
        Integer gid = Integer.valueOf(1);
        //List<Germplasm> results = dao.getDerivativeChildren(gid);
        List<Germplasm> results = dao.getChildren(gid, 'D');
        Assert.assertNotNull(results);
        Debug.println(0, "testGetDerivativeChildren(GId=" + gid + ") RESULTS:");
        for (Germplasm g : results) {
            Debug.println(0, "  " + g.getGid() + " : " + g.getPreferredName().getNval());
        }
    }
    
    @Test
    public void testGetMaintenanceChildren() throws Exception {
    	Integer gid = Integer.valueOf(1);
        List<Germplasm> results = dao.getChildren(gid, 'M');
        Assert.assertNotNull(results);
        Debug.println(0, "testGetMaintenanceChildren(GId=" + gid + ") RESULTS:");
        for (Germplasm g : results) {
            Debug.println(0, "  " + g.getGid() + " : " + g.getPreferredName().getNval());
        }
    }
    
    @Test
    public void testSearchForGermplasmsExactMatchGID() throws Exception {

        List<Germplasm> results = dao.searchForGermplasms("1", Operation.EQUAL, false, false);
        Assert.assertTrue(results.size() == 1);
        
    }
    
    @Test
    public void testSearchForGermplasmsExactMatchGermplasmName() throws Exception {

        List<Germplasm> results = dao.searchForGermplasms("(CML454 X CML451)-B-3-1-1", Operation.EQUAL, false, false);
        Assert.assertTrue(results.size() == 1);
        
        results = dao.searchForGermplasms("(CML454 X CML451)", Operation.EQUAL, false, false);
        Assert.assertTrue(results.isEmpty());
        
    }
    
    @Test
    public void testSearchForGermplasmsStartsWithGID() throws Exception {

        List<Germplasm> results = dao.searchForGermplasms("1%", Operation.LIKE, false, false);
        Assert.assertFalse(results.isEmpty());
    }
    
    @Test
    public void testSearchForGermplasmsStartsWithGermplasmName() throws Exception {

        List<Germplasm> results = dao.searchForGermplasms("(CML454%", Operation.LIKE, false, false);
        Assert.assertFalse(results.isEmpty());
        
    }
    
    @Test
    public void testSearchForGermplasmsContainsGID() throws Exception {

        List<Germplasm> results = dao.searchForGermplasms("%1%", Operation.LIKE, false, false);
        Assert.assertFalse(results.isEmpty());
        
        List<Germplasm> startsWithResults = dao.searchForGermplasms("1%", Operation.LIKE, false, false);
        Assert.assertTrue(results.containsAll(startsWithResults));
    }
    
    @Test
    public void testSearchForGermplasmsContainsGermplasmName() throws Exception {

        List<Germplasm> results = dao.searchForGermplasms("%CML454%", Operation.LIKE, false, false);
        Assert.assertFalse(results.isEmpty());
        
        List<Germplasm> startsWithResults = dao.searchForGermplasms("CML454%", Operation.LIKE, false, false);
        Assert.assertTrue(results.containsAll(startsWithResults));
        
    }
    
    @Test
    public void testSearchForGermplasmsByInventoryId_ExactMatch() throws Exception {
    	List<Germplasm> results = dao.searchForGermplasmsByInventoryId(DUMMY_STOCK_ID, Operation.EQUAL, "");
        Assert.assertNotNull(results);
        Assert.assertTrue(results.size() == 1);
    }

	@Test
    public void testSearchForGermplasmsByInventoryId_StartsWith() throws Exception {
		String inventoryID = DUMMY_STOCK_ID.substring(0, 3) + "%";
    	List<Germplasm> results = dao.searchForGermplasmsByInventoryId(inventoryID, Operation.LIKE, "");
        Assert.assertNotNull(results);
        Assert.assertFalse(results.isEmpty());
    }
    
    @Test
    public void testSearchForGermplasmsByInventoryId_Contains() throws Exception {
    	String inventoryID = "%" + DUMMY_STOCK_ID.substring(0, 3) + "%";
    	List<Germplasm> results = dao.searchForGermplasmsByInventoryId(inventoryID, Operation.LIKE, "");
        Assert.assertNotNull(results);
        Assert.assertFalse(results.isEmpty());
        
        List<Germplasm> startsWithResults = dao.searchForGermplasms(
        		DUMMY_STOCK_ID.substring(0, 3) + "%", Operation.LIKE, false, false);
        Assert.assertTrue(results.containsAll(startsWithResults));
    }
    
    @Test
    public void testSearchForGermplasmsWithInventory() throws Exception {
    	List<Germplasm> results = dao.searchForGermplasms("1%", Operation.LIKE, false, false);
    	List<Germplasm> resultsWithInventoryOnly = dao.searchForGermplasms("1%", Operation.LIKE, false, true);
        Assert.assertNotEquals(results.size(),resultsWithInventoryOnly.size());  
    }
    
    @Test
    public void testSearchForGermplasmsIncludeParents() throws Exception {
    	List<Germplasm> results = dao.searchForGermplasms(testGid1.toString(), Operation.EQUAL, false, false);
    	List<Germplasm> resultsWithParents = dao.searchForGermplasms(testGid1.toString(), Operation.EQUAL, true, false);
        Assert.assertNotEquals(results.size(),resultsWithParents.size());  
        Assert.assertEquals(1,results.size());
        Assert.assertEquals(3,resultsWithParents.size());
        
        results = dao.searchForGermplasms("2", Operation.EQUAL, false, false);
    	resultsWithParents = dao.searchForGermplasms("2", Operation.EQUAL, true, false);
    	Assert.assertEquals(results.size(),resultsWithParents.size());
    }
    
    @Test
    public void testSearchForGermplasmsEmptyKeyword() throws Exception {
    	List<Germplasm> results = dao.searchForGermplasms("", Operation.EQUAL, false, false);
    	Assert.assertTrue(results.isEmpty());
    }

    @AfterClass
    public static void tearDown() throws Exception {
    	revertChangesToInventory();
    	revertChangesToProgenitors();
        dao.setSession(null);
        dao = null;
    }
    
    private static void revertChangesToProgenitors() throws MiddlewareQueryException {
    	germplasmDataDM.updateProgenitor(testGid1, oldGid1_Gpid1, 1);
    	germplasmDataDM.updateProgenitor(testGid1, oldGid1_Gpid2, 2);
	}

	private static void revertChangesToInventory() throws MiddlewareQueryException {
    	if(testTransactionID!=null) {
    		Transaction transaction = inventoryDM.getTransactionById(testTransactionID);
    		if(transaction!=null) {
    			transaction.setInventoryID(oldInventoryID);
    			inventoryDM.updateTransaction(transaction);
    		}
    	}
	}
    

}
