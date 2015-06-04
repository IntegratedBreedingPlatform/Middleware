
package org.generationcp.middleware.dao.ims;

import java.util.List;

import org.generationcp.middleware.MiddlewareIntegrationTest;
import org.generationcp.middleware.domain.gms.GermplasmListType;
import org.generationcp.middleware.domain.inventory.InventoryDetails;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class StockTransactionDAOTest extends MiddlewareIntegrationTest {

	private static StockTransactionDAO dao;

	@BeforeClass
	public static void setUp() throws Exception {
		StockTransactionDAOTest.dao = new StockTransactionDAO();
		StockTransactionDAOTest.dao.setSession(MiddlewareIntegrationTest.sessionUtil.getCurrentSession());
	}

	@Test
	public void testRetrieveInventoryDetailsForListDataProjectListId() throws MiddlewareQueryException {
		Integer stockListId = 17;
		List<InventoryDetails> inventoryDetailsList =
				StockTransactionDAOTest.dao.retrieveInventoryDetailsForListDataProjectListId(stockListId, GermplasmListType.CROSSES);
		Assert.assertNotNull(inventoryDetailsList);
		for (InventoryDetails inventoryDetails : inventoryDetailsList) {
			Assert.assertNotNull(inventoryDetails.getLotId());
			Assert.assertNotNull(inventoryDetails.getUserId());
			Assert.assertNotNull(inventoryDetails.getAmount());
			Assert.assertNotNull(inventoryDetails.getSourceId());
			Assert.assertNotNull(inventoryDetails.getInventoryID());
			Assert.assertNotNull(inventoryDetails.getEntryId());
			Assert.assertNotNull(inventoryDetails.getParentage());
			Assert.assertNotNull(inventoryDetails.getListDataProjectId());
			Assert.assertNotNull(inventoryDetails.getTrnId());
			Assert.assertNotNull(inventoryDetails.getSourceRecordId());
			Assert.assertNotNull(inventoryDetails.getLotGid());
			Assert.assertNotNull(inventoryDetails.getStockSourceRecordId());
			if (inventoryDetails.isBulkingCompleted()) {
				Assert.assertEquals(InventoryDetails.BULK_COMPL_COMPLETED, inventoryDetails.getBulkCompl());
			}
			if (inventoryDetails.isBulkingDonor()) {
				Assert.assertEquals(InventoryDetails.BULK_COMPL_COMPLETED, inventoryDetails.getBulkCompl());
				Assert.assertNull(inventoryDetails.getGid());
				Assert.assertNull(inventoryDetails.getGermplasmName());
				Assert.assertNotNull(inventoryDetails.getBulkWith());
			} else if (inventoryDetails.isBulkingRecipient()) {
				Assert.assertEquals(InventoryDetails.BULK_COMPL_COMPLETED, inventoryDetails.getBulkCompl());
				Assert.assertNotNull(inventoryDetails.getGid());
				Assert.assertNotNull(inventoryDetails.getGermplasmName());
				Assert.assertNotNull(inventoryDetails.getBulkWith());
			} else if (InventoryDetails.BULK_COMPL_Y.equals(inventoryDetails.getBulkCompl())) {
				Assert.assertNotNull(inventoryDetails.getBulkWith());
			} else if (inventoryDetails.getBulkWith() == null) {
				Assert.assertNull(inventoryDetails.getBulkCompl());
			}

		}
	}

	@AfterClass
	public static void tearDown() throws Exception {
		StockTransactionDAOTest.dao.setSession(null);
		StockTransactionDAOTest.dao = null;
	}
}
