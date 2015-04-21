package org.generationcp.middleware.service;

import org.generationcp.middleware.DataManagerIntegrationTest;
import org.generationcp.middleware.dao.ims.TransactionDAO;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.ims.LotsResult;
import org.generationcp.middleware.service.api.InventoryService;
import org.generationcp.middleware.utils.test.Debug;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class InventoryServiceImplTest extends DataManagerIntegrationTest {

	private static InventoryService inventoryService;

	@BeforeClass
	public static void setUp() throws Exception {
		inventoryService = managerFactory.getInventoryMiddlewareService();
	}

	@Test
	public void testAddLot() throws Exception {
		List<Integer> gids = new ArrayList<Integer>();
		int maxGids = 10;
		for (int i = 0; i < maxGids; i++) {
			gids.add(i + ((int) (Math.random() * 1000)));
		}

		int locationId = 1 + ((int) (Math.random() * 1000));
		int scaleId = 6090;
		String comment = "No Comment";
		int userId = 1;
		double amount = 1.23456;
		int listId = 1426;
		LotsResult result = inventoryService.addLotsForList(gids, locationId, scaleId, comment,
				userId, amount, listId);
		if (result != null) {
			Debug.printFormattedObject(INDENT, result);
		}

	}

	@Test
	public void testGetCurrentNotificationNumber() throws MiddlewareException {
		Integer currentNotificationNumber = inventoryService.getCurrentNotationNumberForBreederIdentifier("TR");
		assertEquals(2, currentNotificationNumber.intValue());
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

		TransactionDAO dao = mock(TransactionDAO.class);
		InventoryServiceImpl dut = spy(
				new InventoryServiceImpl(mock(HibernateSessionProvider.class), ""));
		doReturn(dao).when(dut).getTransactionDao();
		when(dao.getInventoryIDsWithBreederIdentifier(anyString())).thenReturn(inventoryIDs);

		Integer currentNotationNumber = dut.getCurrentNotationNumberForBreederIdentifier("PRE");
		assertEquals(35, currentNotationNumber.intValue());

	}
}
