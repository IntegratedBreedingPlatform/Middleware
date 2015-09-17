
package org.generationcp.middleware.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

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
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.ListDataProject;
import org.generationcp.middleware.pojos.ims.EntityType;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.LotStatus;
import org.generationcp.middleware.pojos.ims.StockTransaction;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.generationcp.middleware.pojos.ims.TransactionStatus;
import org.generationcp.middleware.util.Util;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class InventoryServiceImplTest {

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
		final String breederIdentifier = "TR";
		Mockito.doReturn(null).when(this.transactionDAO).getInventoryIDsWithBreederIdentifier(breederIdentifier);
		final Integer currentNotificationNumber = this.inventoryServiceImpl.getCurrentNotationNumberForBreederIdentifier(breederIdentifier);
		Assert.assertEquals(0, currentNotificationNumber.intValue());
	}

	@Test
	public void testGetCurrentNotificationNumber_EmptyInventoryIds() throws MiddlewareException {
		final String breederIdentifier = "TR";
		Mockito.doReturn(new ArrayList<String>()).when(this.transactionDAO).getInventoryIDsWithBreederIdentifier(breederIdentifier);
		final Integer currentNotificationNumber = this.inventoryServiceImpl.getCurrentNotationNumberForBreederIdentifier(breederIdentifier);
		Assert.assertEquals(0, currentNotificationNumber.intValue());
	}

	@Test
	public void testGetCurrentNotationNumberForBreederIdentifier_WithExisting() throws MiddlewareException {
		final List<String> inventoryIDs = new ArrayList<>();
		inventoryIDs.add("PRE1-12");
		inventoryIDs.add("PRE1-13");
		inventoryIDs.add("PRE1-14");
		inventoryIDs.add("PRE2-1");
		inventoryIDs.add("PRE3-1");
		inventoryIDs.add("PRE35-1");

		final String breederIdentifier = "PRE";
		Mockito.doReturn(inventoryIDs).when(this.transactionDAO).getInventoryIDsWithBreederIdentifier(breederIdentifier);
		final Integer currentNotationNumber = this.inventoryServiceImpl.getCurrentNotationNumberForBreederIdentifier(breederIdentifier);
		Assert.assertEquals(35, currentNotationNumber.intValue());

	}

	@Test
	public void testGetCurrentNotationNumberForBreederIdentifier_WithNoMatch() throws MiddlewareException {
		final List<String> inventoryIDs = new ArrayList<>();
		inventoryIDs.add("DUMMY1-1");

		final String breederIdentifier = "PRE";
		Mockito.doReturn(inventoryIDs).when(this.transactionDAO).getInventoryIDsWithBreederIdentifier(breederIdentifier);
		final Integer currentNotationNumber = this.inventoryServiceImpl.getCurrentNotationNumberForBreederIdentifier(breederIdentifier);
		Assert.assertEquals("0 must be returned because PRE is not found in DUMMY1-1", 0, currentNotationNumber.intValue());

	}

	@Test(expected = MiddlewareQueryException.class)
	public void testAddLotAndTransaction_LotAlreadyExists() {
		final InventoryDetails details = this.createInventoryDetailsTestData(1);
		final GermplasmListData listData = this.createGermplasmListDataTestData();
		final ListDataProject listDataProject = this.createListDataProjectTestData();

		Mockito.doReturn(new Lot()).when(this.inventoryDataManager).getLotByEntityTypeAndEntityIdAndLocationIdAndScaleId(
				EntityType.GERMPLSM.name(), details.getGid(), details.getLocationId(), details.getScaleId());

		this.inventoryServiceImpl.addLotAndTransaction(details, listData, listDataProject);
	}

	@Test
	public void testAddLotAndTransaction() {
		final InventoryDetails details = this.createInventoryDetailsTestData(1);
		final GermplasmListData listData = this.createGermplasmListDataTestData();
		final ListDataProject listDataProject = this.createListDataProjectTestData();

		Mockito.doReturn(null).when(this.inventoryDataManager).getLotByEntityTypeAndEntityIdAndLocationIdAndScaleId(
				EntityType.GERMPLSM.name(), details.getGid(), details.getLocationId(), details.getScaleId());

		final Lot lot = this.createLotTestData(details);
		Mockito.doReturn(lot).when(this.lotBuilder).createLotForAdd(details.getGid(), details.getLocationId(), details.getScaleId(),
				details.getComment(), details.getUserId());
		Mockito.doReturn(1).when(this.inventoryDataManager).addLot(lot);

		final Transaction transaction = this.createTransactionTestData(lot, listData, details);
		Mockito.doReturn(transaction).when(this.transactionBuilder).buildForAdd(lot, listData.getId(), details.getAmount(),
				details.getUserId(), details.getComment(), details.getSourceId(), details.getInventoryID(), details.getBulkWith(),
				details.getBulkCompl());
		Mockito.doReturn(1).when(this.inventoryDataManager).addTransaction(transaction);

		final StockTransaction stockTransaction = this.createStockTransactionTestData(listDataProject, transaction);
		Mockito.doReturn(1).when(this.inventoryDataManager).addStockTransaction(stockTransaction);

		this.inventoryServiceImpl.addLotAndTransaction(details, listData, listDataProject);

		Mockito.verify(this.inventoryDataManager).addLot(lot);
		Mockito.verify(this.inventoryDataManager).addTransaction(transaction);
		Mockito.verify(this.inventoryDataManager).addStockTransaction(Mockito.any(StockTransaction.class));
	}

	@Test
	public void testAddLotAndTransaction_NullListData() {
		final InventoryDetails details = this.createInventoryDetailsTestData(1);
		final GermplasmListData listData = null;
		final ListDataProject listDataProject = this.createListDataProjectTestData();

		Mockito.doReturn(null).when(this.inventoryDataManager).getLotByEntityTypeAndEntityIdAndLocationIdAndScaleId(
				EntityType.GERMPLSM.name(), details.getGid(), details.getLocationId(), details.getScaleId());

		final Lot lot = this.createLotTestData(details);
		Mockito.doReturn(lot).when(this.lotBuilder).createLotForAdd(details.getGid(), details.getLocationId(), details.getScaleId(),
				details.getComment(), details.getUserId());
		Mockito.doReturn(1).when(this.inventoryDataManager).addLot(lot);

		final Transaction transaction = this.createTransactionTestData(lot, listData, details);
		Mockito.doReturn(transaction).when(this.transactionBuilder).buildForAdd(lot, 0, details.getAmount(), details.getUserId(),
				details.getComment(), details.getSourceId(), details.getInventoryID(), details.getBulkWith(), details.getBulkCompl());
		Mockito.doReturn(1).when(this.inventoryDataManager).addTransaction(transaction);

		final StockTransaction stockTransaction = this.createStockTransactionTestData(listDataProject, transaction);
		Mockito.doReturn(1).when(this.inventoryDataManager).addStockTransaction(stockTransaction);

		this.inventoryServiceImpl.addLotAndTransaction(details, listData, listDataProject);

		Mockito.verify(this.inventoryDataManager).addLot(lot);
		Mockito.verify(this.inventoryDataManager).addTransaction(transaction);
		Mockito.verify(this.inventoryDataManager).addStockTransaction(Mockito.any(StockTransaction.class));
	}

	private StockTransaction createStockTransactionTestData(final ListDataProject listDataProject, final Transaction transaction) {
		final StockTransaction stockTransaction = new StockTransaction(null, listDataProject, transaction);
		stockTransaction.setSourceRecordId(transaction.getSourceRecordId());
		return stockTransaction;
	}

	private Transaction createTransactionTestData(final Lot lot, final GermplasmListData listData, final InventoryDetails details) {
		final Transaction transaction = new Transaction(null, details.getUserId(), lot, Util.getCurrentDateAsIntegerValue(),
				TransactionStatus.ANTICIPATED.getIntValue(), Double.valueOf(new DecimalFormat("#.000").format(details.getAmount())),
				details.getComment(), 0, EntityType.LIST.name(), details.getSourceId(), listData == null ? 0 : listData.getId(), 0d, 1,
				details.getInventoryID());

		transaction.setBulkCompl(details.getBulkCompl());
		transaction.setBulkWith(details.getBulkWith());

		return transaction;
	}

	private Lot createLotTestData(final InventoryDetails details) {
		return new Lot(null, details.getUserId(), EntityType.GERMPLSM.name(), details.getGid(), details.getLocationId(),
				details.getScaleId(), LotStatus.ACTIVE.getIntValue(), 0, details.getComment());
	}

	private ListDataProject createListDataProjectTestData() {
		final ListDataProject listDataProject = new ListDataProject();
		return listDataProject;
	}

	private GermplasmListData createGermplasmListDataTestData() {
		final GermplasmListData germplasmListData = new GermplasmListData();
		germplasmListData.setId(1);
		return germplasmListData;
	}

	private InventoryDetails createInventoryDetailsTestData(Integer listId) {
		final InventoryDetails inventoryDetails = new InventoryDetails();
		inventoryDetails.setGid(201);
		inventoryDetails.setLocationId(1);
		inventoryDetails.setScaleId(10);
		inventoryDetails.setComment("TEST");
		inventoryDetails.setUserId(1);
		inventoryDetails.setAmount(20d);
		inventoryDetails.setSourceId(listId);
		inventoryDetails.setInventoryID("SID1-1");
		inventoryDetails.setBulkCompl("Y");
		inventoryDetails.setBulkCompl("SID1-2");
		return inventoryDetails;
	}

	@Test
	public void testRetrieveInventoryDetailsForListDataProjectListId_Crosses() throws MiddlewareQueryException {
		final Integer listId = 1;
		GermplasmListType listType = GermplasmListType.CROSSES;
		final List<InventoryDetails> expectedInventoryDetailsList = this.createInventoryDetailsListTestData(listId);
		Mockito.doReturn(expectedInventoryDetailsList).when(this.stockTransactionDAO)
		.retrieveInventoryDetailsForListDataProjectListId(listId, listType);
		final List<InventoryDetails> inventoryDetailsList =
				this.inventoryServiceImpl.getInventoryListByListDataProjectListId(listId, listType);
		for (final InventoryDetails inventoryDetails : inventoryDetailsList) {
			Assert.assertEquals(listId, inventoryDetails.getSourceId());
		}
	}

	@Test
	public void testRetrieveInventoryDetailsForListDataProjectListId_Advanced() throws MiddlewareQueryException {
		final Integer listId = 1;
		GermplasmListType listType = GermplasmListType.ADVANCED;
		final List<InventoryDetails> expectedInventoryDetailsList = this.createInventoryDetailsListTestData(listId);
		Mockito.doReturn(expectedInventoryDetailsList).when(this.stockTransactionDAO)
		.retrieveInventoryDetailsForListDataProjectListId(listId, listType);
		final List<InventoryDetails> inventoryDetailsList =
				this.inventoryServiceImpl.getInventoryListByListDataProjectListId(listId, listType);
		for (final InventoryDetails inventoryDetails : inventoryDetailsList) {
			Assert.assertEquals(listId, inventoryDetails.getSourceId());
		}
	}

	private List<InventoryDetails> createInventoryDetailsListTestData(Integer listId) {
		List<InventoryDetails> inventoryDetailsList = new ArrayList<>();
		inventoryDetailsList.add(this.createInventoryDetailsTestData(listId));
		return inventoryDetailsList;
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRetrieveInventoryDetailsForListDataProjectListId_WrongListType() throws MiddlewareQueryException {
		final Integer listId = 1;
		GermplasmListType listType = GermplasmListType.LST;
		Mockito.doThrow(IllegalArgumentException.class).when(this.stockTransactionDAO)
		.retrieveInventoryDetailsForListDataProjectListId(listId, listType);
		this.inventoryServiceImpl.getInventoryListByListDataProjectListId(listId, listType);
	}

	@Test
	public void testStockHasCompletedBulking() throws MiddlewareQueryException {
		final Integer listId = 1;
		Mockito.doReturn(true).when(this.stockTransactionDAO).stockHasCompletedBulking(listId);
		Assert.assertEquals(true, this.inventoryServiceImpl.stockHasCompletedBulking(listId));
	}


}
