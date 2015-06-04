
package org.generationcp.middleware.service;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.DataManagerIntegrationTest;
import org.generationcp.middleware.dao.ims.TransactionDAO;
import org.generationcp.middleware.domain.gms.GermplasmListType;
import org.generationcp.middleware.domain.inventory.InventoryDetails;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.service.api.InventoryService;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class InventoryServiceImplTestIT extends DataManagerIntegrationTest {

	private static InventoryService inventoryService;

	public static final String TEST_INVENTORY_ID = "TR1-123";

	@BeforeClass
	public static void setUp() throws Exception {
		InventoryServiceImplTestIT.inventoryService = DataManagerIntegrationTest.managerFactory.getInventoryMiddlewareService();
	}

	@Test
	public void testGetCurrentNotificationNumber() throws MiddlewareException {
		Integer currentNotificationNumber = InventoryServiceImplTestIT.inventoryService.getCurrentNotationNumberForBreederIdentifier("TR");
		Assert.assertEquals(0, currentNotificationNumber.intValue());
	}

	@Test
	public void testMockedGetCurrentNotationNumberForBreederIdentifier() throws MiddlewareException {
		List<String> inventoryIDs = new ArrayList<>();
		inventoryIDs.add("PRE1-12");
		inventoryIDs.add("PRE1-13");
		inventoryIDs.add("PRE1-14");
		inventoryIDs.add("PRE2-1");
		inventoryIDs.add("PRE3-1");
		inventoryIDs.add("PRE35-1");

		TransactionDAO dao = Mockito.mock(TransactionDAO.class);
		InventoryServiceImpl dut = Mockito.spy(new InventoryServiceImpl(Mockito.mock(HibernateSessionProvider.class), ""));
		Mockito.doReturn(dao).when(dut).getTransactionDao();
		Mockito.when(dao.getInventoryIDsWithBreederIdentifier(Matchers.anyString())).thenReturn(inventoryIDs);

		Integer currentNotationNumber = dut.getCurrentNotationNumberForBreederIdentifier("PRE");
		Assert.assertEquals(35, currentNotationNumber.intValue());

	}

	@Test
	public void testStockHasCompletedBulking() throws MiddlewareQueryException {
		Integer listId = 17;
		List<InventoryDetails> inventoryDetailsList =
				InventoryServiceImplTestIT.inventoryService.getInventoryListByListDataProjectListId(listId, GermplasmListType.CROSSES);
		boolean hasCompletedBulking = false;
		for (InventoryDetails inventoryDetails : inventoryDetailsList) {
			if (InventoryDetails.BULK_COMPL_COMPLETED.equals(inventoryDetails.getBulkCompl())) {
				hasCompletedBulking = true;
			}
		}
		Assert.assertEquals(hasCompletedBulking, InventoryServiceImplTestIT.inventoryService.stockHasCompletedBulking(listId));
	}
}
