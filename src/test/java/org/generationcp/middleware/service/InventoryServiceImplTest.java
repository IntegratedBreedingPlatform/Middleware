
package org.generationcp.middleware.service;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.ims.StockTransactionDAO;
import org.generationcp.middleware.dao.ims.TransactionDAO;
import org.generationcp.middleware.domain.gms.GermplasmListType;
import org.generationcp.middleware.domain.inventory.InventoryDetails;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.operation.builder.LotBuilder;
import org.generationcp.middleware.operation.builder.TransactionBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class InventoryServiceImplTest extends IntegrationTestBase {

	public static final String TEST_INVENTORY_ID = "TR1-123";

	@Mock
	private HibernateSessionProvider sessionProvider;

	@Mock
	private TransactionDAO transactionDAO;

	@Mock
	private InventoryDataManager inventoryDataManager;

	@Mock
	private StockTransactionDAO stockTransactionDAO;

	@Mock
	private LotBuilder lotBuilder;

	@Mock
	private TransactionBuilder transactionBuilder;

	@InjectMocks
	private InventoryServiceImpl inventoryServiceImpl;

	@Test
	public void testGetCurrentNotificationNumber_NullInventoryIds() throws MiddlewareException {
		String breederIdentifier = "TR";
		Mockito.doReturn(null).when(this.transactionDAO).getInventoryIDsWithBreederIdentifier(breederIdentifier);
		Integer currentNotificationNumber = this.inventoryServiceImpl.getCurrentNotationNumberForBreederIdentifier(breederIdentifier);
		Assert.assertEquals(0, currentNotificationNumber.intValue());
	}

	@Test
	public void testGetCurrentNotificationNumber_EmptyInventoryIds() throws MiddlewareException {
		String breederIdentifier = "TR";
		Mockito.doReturn(new ArrayList<String>()).when(this.transactionDAO).getInventoryIDsWithBreederIdentifier(breederIdentifier);
		Integer currentNotificationNumber = this.inventoryServiceImpl.getCurrentNotationNumberForBreederIdentifier(breederIdentifier);
		Assert.assertEquals(0, currentNotificationNumber.intValue());
	}

	@Test
	public void testGetCurrentNotationNumberForBreederIdentifier_WithExisting() throws MiddlewareException {
		List<String> inventoryIDs = new ArrayList<>();
		inventoryIDs.add("PRE1-12");
		inventoryIDs.add("PRE1-13");
		inventoryIDs.add("PRE1-14");
		inventoryIDs.add("PRE2-1");
		inventoryIDs.add("PRE3-1");
		inventoryIDs.add("PRE35-1");

		String breederIdentifier = "PRE";
		Mockito.doReturn(inventoryIDs).when(this.transactionDAO).getInventoryIDsWithBreederIdentifier(breederIdentifier);
		Integer currentNotationNumber = this.inventoryServiceImpl.getCurrentNotationNumberForBreederIdentifier(breederIdentifier);
		Assert.assertEquals(35, currentNotationNumber.intValue());

	}

	@Test
	public void testStockHasCompletedBulking() throws MiddlewareQueryException {
		Integer listId = 17;
		List<InventoryDetails> inventoryDetailsList =
				this.inventoryServiceImpl.getInventoryListByListDataProjectListId(listId, GermplasmListType.CROSSES);
		boolean hasCompletedBulking = false;
		for (InventoryDetails inventoryDetails : inventoryDetailsList) {
			if (InventoryDetails.BULK_COMPL_COMPLETED.equals(inventoryDetails.getBulkCompl())) {
				hasCompletedBulking = true;
			}
		}
		Assert.assertEquals(hasCompletedBulking, this.inventoryServiceImpl.stockHasCompletedBulking(listId));
	}
}
