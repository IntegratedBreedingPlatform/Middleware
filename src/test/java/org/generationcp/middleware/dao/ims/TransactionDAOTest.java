package org.generationcp.middleware.dao.ims;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.MiddlewareIntegrationTest;
import org.generationcp.middleware.dao.GermplasmListDataDAO;
import org.generationcp.middleware.domain.inventory.InventoryDetails;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.hibernate.Session;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TransactionDAOTest extends MiddlewareIntegrationTest {

	private static TransactionDAO dao;
	private static GermplasmListDataDAO germplasmListDataDAO;

    @BeforeClass
    public static void setUp() throws Exception {
        dao = new TransactionDAO();
        germplasmListDataDAO = new GermplasmListDataDAO();
        Session session = sessionUtil.getCurrentSession();
        dao.setSession(session);
        germplasmListDataDAO.setSession(session);
        
    }
    
    @Test
    public void testRetrieveStockIds(){
    	
    	List<Integer> lRecIDs = new ArrayList<>();
    	lRecIDs.add(1);
    	Map<Integer, String> lRecIDStockIDMap = dao.retrieveStockIds(lRecIDs);
    	assertNotNull(lRecIDStockIDMap);
    }
    
    @Test
    public void testGetStockIdsByListDataProjectListId() throws MiddlewareQueryException{
    	List<String> stockIds = dao.getStockIdsByListDataProjectListId(17);
    	assertNotNull(stockIds);
    }
    
    @Test
    public void testGetInventoryDetailsByTransactionRecordId() throws MiddlewareQueryException {
    	List<Integer> recordIds = new ArrayList<Integer>();
    	List<GermplasmListData> listDataList = germplasmListDataDAO.getByListId(1, 0, Integer.MAX_VALUE);
    	for (GermplasmListData germplasmListData : listDataList) {
    		recordIds.add(germplasmListData.getId());
		}
    	List<InventoryDetails> inventoryDetailsList = dao.getInventoryDetailsByTransactionRecordId(recordIds);
    	for (InventoryDetails inventoryDetails : inventoryDetailsList) {
			assertTrue(recordIds.contains(inventoryDetails.getSourceRecordId()));
		}
    }
    
    @AfterClass
    public static void tearDown() throws Exception {
        dao.setSession(null);
        dao = null;
    }
}
