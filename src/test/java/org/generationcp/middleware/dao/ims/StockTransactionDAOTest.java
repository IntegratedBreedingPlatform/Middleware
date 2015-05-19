package org.generationcp.middleware.dao.ims;

import static org.junit.Assert.*;

import java.util.List;

import org.generationcp.middleware.MiddlewareIntegrationTest;
import org.generationcp.middleware.domain.gms.GermplasmListType;
import org.generationcp.middleware.domain.inventory.InventoryDetails;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class StockTransactionDAOTest extends MiddlewareIntegrationTest {

	private static StockTransactionDAO dao;

    @BeforeClass
    public static void setUp() throws Exception {
        dao = new StockTransactionDAO();
        dao.setSession(sessionUtil.getCurrentSession());
    }
    
    @Test
    public void testRetrieveInventoryDetailsForListDataProjectListId() 
    		throws MiddlewareQueryException{
    	Integer stockListId = 17;
    	List<InventoryDetails> inventoryDetailsList = 
    			dao.retrieveInventoryDetailsForListDataProjectListId(
    					stockListId,GermplasmListType.CROSSES);
    	assertNotNull(inventoryDetailsList);
    	for (InventoryDetails inventoryDetails : inventoryDetailsList) {
			assertNotNull(inventoryDetails.getLotId());
			assertNotNull(inventoryDetails.getUserId());
			assertNotNull(inventoryDetails.getAmount());
			assertNotNull(inventoryDetails.getSourceId());
			assertNotNull(inventoryDetails.getInventoryID());
			assertNotNull(inventoryDetails.getEntryId());
			assertNotNull(inventoryDetails.getParentage());
			assertNotNull(inventoryDetails.getListDataProjectId());
			assertNotNull(inventoryDetails.getTrnId());
			assertNotNull(inventoryDetails.getSourceRecordId());
			assertNotNull(inventoryDetails.getLotGid());
			if(inventoryDetails.isBulkingCompleted()) {
				assertEquals(InventoryDetails.BULK_COMPL_COMPLETED,
						inventoryDetails.getBulkCompl());
			}
			if(inventoryDetails.isBulkingDonor()) {
				assertEquals(InventoryDetails.BULK_COMPL_COMPLETED,
						inventoryDetails.getBulkCompl());
				assertNull(inventoryDetails.getGid());
				assertNull(inventoryDetails.getGermplasmName());
				assertNotNull(inventoryDetails.getBulkWith());
			} else if(inventoryDetails.isBulkingRecipient()) {
				assertEquals(InventoryDetails.BULK_COMPL_COMPLETED,
						inventoryDetails.getBulkCompl());
				assertNotNull(inventoryDetails.getGid());
				assertNotNull(inventoryDetails.getGermplasmName());
				assertNotNull(inventoryDetails.getBulkWith());
			} else if(InventoryDetails.BULK_COMPL_Y.equals(
					inventoryDetails.getBulkCompl())) {
				assertNotNull(inventoryDetails.getBulkWith());
			} else if(inventoryDetails.getBulkWith()==null) {
				assertNull(inventoryDetails.getBulkCompl());
			}
			
		}
    }
    
    @AfterClass
    public static void tearDown() throws Exception {
        dao.setSession(null);
        dao = null;
    }
}
