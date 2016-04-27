
package org.generationcp.middleware.dao.ims;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.data.initializer.GermplasmListTestDataInitializer;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.data.initializer.InventoryDetailsTestDataInitializer;
import org.generationcp.middleware.domain.gms.GermplasmListType;
import org.generationcp.middleware.domain.inventory.InventoryDetails;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.ListDataProject;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.StockTransaction;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.generationcp.middleware.service.api.FieldbookService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class StockTransactionDAOTest extends IntegrationTestBase {

	private static final int LIST_ID = 1;
	private static final String STOCK_ID_PREFIX = "STOCK";
	private static final int LOCATION_ID = 1;
	private static final int SCALE_ID = 1;
	private static final int NO_OF_ENTRIES = 5;
	private static final int PROJECT_ID = 1;
	private static final int USER_ID = 1;

	private StockTransactionDAO dao;
	private final Map<Integer, Germplasm> germplasmMap = new HashMap<Integer, Germplasm>();

	@Autowired
	private GermplasmDataManager germplasmDataManager;

	@Autowired
	private GermplasmListManager germplasmListManager;

	@Autowired
	private FieldbookService fieldbookService;

	@Autowired
	private InventoryDataManager inventoryDataManager;

	private InventoryDetailsTestDataInitializer inventoryDetailsTestDataInitializer;

	private Integer germplasmListId;
	private final Map<Integer, Transaction> listDataIdTransactionMap = new HashMap<Integer, Transaction>();

	@Before
	public void setUp() throws Exception {

		// initialize data initializer
		this.inventoryDetailsTestDataInitializer = new InventoryDetailsTestDataInitializer();

		this.dao = new StockTransactionDAO();
		this.dao.setSession(this.sessionProvder.getSession());

		// initialize germplasm
		this.initializeGermplasms(NO_OF_ENTRIES);

		// initialize germplasm list and snapshot list
		this.initializeGermplasmsListAndListData(this.germplasmMap);
		this.initLotsAndTransactions(this.germplasmListId);
	}

	@Test
	public void testRetrieveInventoryDetailsForListDataProjectListId() {
		final Integer stockListId = 17;
		final List<InventoryDetails> inventoryDetailsList = this.dao.retrieveInventoryDetailsForListDataProjectListId(stockListId);
		Assert.assertNotNull(inventoryDetailsList);
		this.assertInventoryDetailsFields(inventoryDetailsList);
	}

	/**
	 * This method assert specifically every field of InventoryDetails object
	 * 
	 * @param inventoryDetailsList
	 */
	private void assertInventoryDetailsFields(final List<InventoryDetails> inventoryDetailsList) {
		for (final InventoryDetails inventoryDetails : inventoryDetailsList) {
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

	@Test(expected = IllegalArgumentException.class)
	public void testRetrieveSummedInventoryDetailsForListDataProjectListIdForInvalidListType() {

		final Integer listDataProjectListId = 1;
		final GermplasmListType germplasmListType = GermplasmListType.LST;

		this.dao.retrieveSummedInventoryDetailsForListDataProjectListId(listDataProjectListId, germplasmListType);

		Assert.fail("Expecting to throw an exception when the method receives a list type other than CROSSES or ADVANCED.");
	}

	@Test
	public void testRetrieveSummedInventoryDetailsForListDataProjectListIdForAdvancedListType() {

		final Integer advancedListId = this.initializeGermplasmsListSnapShot(this.germplasmListId, GermplasmListType.CROSSES);
		this.initStockTransactions(this.germplasmListId, advancedListId);

		final GermplasmListType germplasmListType = GermplasmListType.CROSSES;

		this.testRetrieveSummedInventoryDetailsForListDataProjectListId(advancedListId, germplasmListType);

	}

	@Test
	public void testRetrieveSummedInventoryDetailsForListDataProjectListIdForCrossListType() {

		final Integer crossListId = this.initializeGermplasmsListSnapShot(this.germplasmListId, GermplasmListType.CROSSES);
		this.initStockTransactions(this.germplasmListId, crossListId);

		final GermplasmListType germplasmListType = GermplasmListType.CROSSES;

		this.testRetrieveSummedInventoryDetailsForListDataProjectListId(crossListId, germplasmListType);

	}

	private void testRetrieveSummedInventoryDetailsForListDataProjectListId(final Integer snapShotListId,
			final GermplasmListType germplasmListType) {
		List<InventoryDetails> detailsList = null;
		try {
			detailsList = this.dao.retrieveSummedInventoryDetailsForListDataProjectListId(snapShotListId, germplasmListType);
		} catch (final IllegalArgumentException e) {
			Assert.fail("Expecting not to throw an exception when the method receives a list type either CROSSES or ADVANCED.");
		}

		Assert.assertNotNull(
				"Expecting that details list has an object assigned to it after the calling retrieveSummedInventoryDetailsForListDataProjectListId() method.",
				detailsList);

		Assert.assertTrue(
				"Expecting that details list has content to it after the calling retrieveSummedInventoryDetailsForListDataProjectListId() method.",
				detailsList.size() > 0);

		this.assertInventoryDetailsFields(detailsList);

	}

	private void initializeGermplasms(final int noOfEntries) {
		for (int i = 1; i <= noOfEntries; i++) {
			final Germplasm germplasm = GermplasmTestDataInitializer.createGermplasm(i);
			final Integer gidAfterAdd = this.germplasmDataManager.addGermplasm(germplasm, germplasm.getPreferredName());
			this.germplasmMap.put(gidAfterAdd, germplasm);
		}
	}

	private void initializeGermplasmsListAndListData(final Map<Integer, Germplasm> germplasmMap) {
		// initialize germplasm list
		final GermplasmList germplasmList = GermplasmListTestDataInitializer.createGermplasmList(LIST_ID, false);
		this.germplasmListId = this.germplasmListManager.addGermplasmList(germplasmList);

		// initialize germplasm list data
		final List<GermplasmListData> germplasmListData =
				GermplasmListTestDataInitializer.createGermplasmListData(germplasmList, new ArrayList<>(germplasmMap.keySet()), false);
		this.germplasmListManager.addGermplasmListData(germplasmListData);
	}

	private void initLotsAndTransactions(final Integer germplasmListId) {

		// initialize lots
		final List<Lot> lots =
				this.inventoryDetailsTestDataInitializer.createLots(new ArrayList<>(this.germplasmMap.keySet()), germplasmListId, SCALE_ID,
						LOCATION_ID);

		// add lots to database
		final List<Integer> lotIds = this.inventoryDataManager.addLots(lots);

		// retrieve existing lot saved from the database,
		// prepare parameters for data initialization
		lots.clear();
		final Map<Integer, Integer> gidLotIdMap = new HashMap<Integer, Integer>();
		for (final Integer lotId : lotIds) {
			final Lot lot = this.inventoryDataManager.getLotById(lotId);
			lots.add(lot);
			gidLotIdMap.put(lot.getEntityId(), lotId);
		}

		// retrieve map of lot id that corresponds to the germplasm list data id (lrecId)
		final Map<Integer, Integer> lotIdLrecIdMap = new HashMap<Integer, Integer>();

		final List<GermplasmListData> listEntries = this.germplasmListManager.getGermplasmListDataByListId(germplasmListId);
		for (final GermplasmListData listEntry : listEntries) {
			final Integer gid = listEntry.getGermplasmId();
			lotIdLrecIdMap.put(gidLotIdMap.get(gid), listEntry.getId());
		}

		// initialize transactions
		final List<Transaction> transactions =
				this.inventoryDetailsTestDataInitializer.createTransactions(lots, this.germplasmListId, lotIdLrecIdMap, STOCK_ID_PREFIX);

		// add transactions to database
		final List<Integer> transactionIds = this.inventoryDataManager.addTransactions(transactions);

		for (final Integer transactionId : transactionIds) {
			final Transaction transaction = this.inventoryDataManager.getTransactionById(transactionId);
			this.listDataIdTransactionMap.put(transaction.getSourceRecordId(), transaction);
		}

	}

	private int initializeGermplasmsListSnapShot(final Integer germplasmListId, final GermplasmListType type) {
		final GermplasmList germplasmList = this.germplasmListManager.getGermplasmListById(germplasmListId);
		final List<GermplasmListData> listEntries = this.germplasmListManager.getGermplasmListDataByListId(germplasmListId);
		final List<ListDataProject> listDataProjectEntries =
				GermplasmListTestDataInitializer.createListDataSnapShotFromListEntries(germplasmList, listEntries);
		return this.fieldbookService.saveOrUpdateListDataProject(PROJECT_ID, type, germplasmListId, listDataProjectEntries, USER_ID);
	}

	private void initStockTransactions(final Integer germplasmListId, final Integer advancedListId) {

		// retrieve snapshot list entries
		final List<ListDataProject> snapShotListData = this.germplasmListManager.retrieveSnapshotListData(advancedListId);

		// create a map for entryId and listDataProject
		final Map<Integer, ListDataProject> entryIdListDataProject = new HashMap<Integer, ListDataProject>();
		for (final ListDataProject listDataProject : snapShotListData) {
			entryIdListDataProject.put(listDataProject.getEntryId(), listDataProject);
		}

		// retrieve germplasm list data
		final List<GermplasmListData> germplasmListData = this.germplasmListManager.getGermplasmListDataByListId(germplasmListId);

		// create map for listdataId and ListDataProject
		final Map<Integer, ListDataProject> listDataIdListDataProject = new HashMap<Integer, ListDataProject>();
		for (final GermplasmListData listEntry : germplasmListData) {
			final Integer entryId = listEntry.getEntryId();
			final Integer listDataId = listEntry.getId();
			listDataIdListDataProject.put(listDataId, entryIdListDataProject.get(entryId));
		}

		final List<StockTransaction> stockTransactions =
				this.inventoryDetailsTestDataInitializer.createStockTransactions(this.listDataIdTransactionMap, listDataIdListDataProject);

		for (final StockTransaction stockTransaction : stockTransactions) {
			this.inventoryDataManager.addStockTransaction(stockTransaction);
		}
	}
}
