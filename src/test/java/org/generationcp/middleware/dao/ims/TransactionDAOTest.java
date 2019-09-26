
package org.generationcp.middleware.dao.ims;

import com.beust.jcommander.internal.Lists;
import org.apache.commons.lang.time.DateUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.GermplasmListDataDAO;
import org.generationcp.middleware.data.initializer.GermplasmListTestDataInitializer;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.data.initializer.InventoryDetailsTestDataInitializer;
import org.generationcp.middleware.domain.inventory.InventoryDetails;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.generationcp.middleware.pojos.report.TransactionReportRow;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.utils.test.IntegrationTestDataInitializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionDAOTest extends IntegrationTestBase {

	private TransactionDAO dao;
	private GermplasmListDataDAO germplasmListDataDAO;

	private static final int LIST_ID = 1;
	private static final String STOCK_ID_PREFIX = "STOCK";
	private static final int LOCATION_ID = 1;
	private static final int SCALE_ID = 1;

	private static final String LOT_DISCARD = "Discard" ;
	private static final String LOT_DEPOSIT = "Deposit";

	private final Map<Integer, Germplasm> germplasmMap = new HashMap<Integer, Germplasm>();

	private List<GermplasmListData> germplasmListData;

	@Autowired
	private GermplasmDataManager germplasmDataManager;

	@Autowired
	private GermplasmListManager germplasmListManager;

	@Autowired
	private InventoryDataManager inventoryDataManager;

	private InventoryDetailsTestDataInitializer inventoryDetailsTestDataInitializer;
	private IntegrationTestDataInitializer testDataInitializer;

	private Integer germplasmListId;
	private final Map<Integer, Transaction> listDataIdTransactionMap = new HashMap<Integer, Transaction>();


	@Before
	public void setUp() throws Exception {
		this.dao = new TransactionDAO();
		this.dao.setSession(this.sessionProvder.getSession());

		this.germplasmListDataDAO = new GermplasmListDataDAO();
		this.germplasmListDataDAO.setSession(this.sessionProvder.getSession());

		this.germplasmListData = Lists.newArrayList();

		this.inventoryDetailsTestDataInitializer = new InventoryDetailsTestDataInitializer();
		this.testDataInitializer = new IntegrationTestDataInitializer(this.sessionProvder, this.workbenchSessionProvider);
		this.initializeGermplasms(1);
		this.initializeGermplasmsListAndListData(this.germplasmMap);
		this.initLotsAndTransactions(this.germplasmListId);
	}

	@Test
	public void testRetrieveStockIds() {

		final List<Integer> lRecIDs = new ArrayList<>();
		lRecIDs.add(1);
		final Map<Integer, String> lRecIDStockIDMap = this.dao.retrieveStockIds(lRecIDs);
		Assert.assertNotNull(lRecIDStockIDMap);
	}

	@Test
	public void testGetStockIdsByListDataProjectListId() throws MiddlewareQueryException {
		final List<String> stockIds = this.dao.getStockIdsByListDataProjectListId(17);
		Assert.assertNotNull(stockIds);
	}

	@Test
	public void testGetInventoryDetailsByTransactionRecordId() throws MiddlewareQueryException {
		final List<Integer> recordIds = new ArrayList<Integer>();
		final List<GermplasmListData> listDataList = this.germplasmListDataDAO.getByListId(1);
		for (final GermplasmListData germplasmListData : listDataList) {
			recordIds.add(germplasmListData.getId());
		}
		final List<InventoryDetails> inventoryDetailsList = this.dao.getInventoryDetailsByTransactionRecordId(recordIds);
		for (final InventoryDetails inventoryDetails : inventoryDetailsList) {
			Assert.assertTrue(recordIds.contains(inventoryDetails.getSourceRecordId()));
		}
	}

	@Test
	public void testGetSimilarStockIdsEmptyListParam() {
		final boolean emptyListParamCondition = this.dao.getSimilarStockIds(new ArrayList<String>()).isEmpty();
		Assert.assertTrue("List of returned similar stock ids should be empty given empty list", emptyListParamCondition);
	}

	@Test
	public void testGetSimilarStockIdsNullParam() {
		final boolean nullParamCondition = this.dao.getSimilarStockIds(null).isEmpty();
		Assert.assertTrue("List of returned similar stock ids should be empty given a null parameter", nullParamCondition);
	}

	@Test
	public void testRetrieveWithdrawalBalanceWithDistinctScale() throws MiddlewareQueryException{
		final List<Integer> recordsList = Lists.newArrayList();
		final Integer recordsId = this.germplasmListData.get(0).getId();
		recordsList.add(recordsId);

		final Map<Integer, Object[]> returnMap = this.dao.retrieveWithdrawalBalanceWithDistinctScale(recordsList);

		Assert.assertNotNull(returnMap);
		final Object[] returnObjectArray = returnMap.get(recordsId);
		Assert.assertNotNull(returnObjectArray);
		Assert.assertEquals(100.0, returnObjectArray[0]);
		Assert.assertEquals(new BigInteger("1"), ((BigInteger) returnObjectArray[1]));
		Assert.assertEquals(1, ((Integer) returnObjectArray[2]).intValue());

	}

	@Test
	public void testRetrieveWithdrawalStatus() throws MiddlewareQueryException{
		final List<Object[]> returnObjectArray = this.dao.retrieveWithdrawalStatus(this.germplasmListId, new ArrayList<>(this.germplasmMap.keySet()));
		Assert.assertNotNull(returnObjectArray);
		Assert.assertEquals(this.germplasmListData.get(0).getGid(), returnObjectArray.get(0)[1]);
		Assert.assertEquals(this.germplasmListData.get(0).getId(), returnObjectArray.get(0)[2]);
		Assert.assertEquals(0, returnObjectArray.get(0)[3]);

	}

	private void initializeGermplasms(final int noOfEntries) {
		for (int i = 1; i <= noOfEntries; i++) {
			final Germplasm germplasm = GermplasmTestDataInitializer.createGermplasm(i);
			final Integer gidAfterAdd = this.germplasmDataManager.addGermplasm(germplasm, germplasm.getPreferredName());
			this.germplasmMap.put(gidAfterAdd, germplasm);
		}
	}

	private void initializeGermplasmsListAndListData(final Map<Integer, Germplasm> germplasmMap) {
		final GermplasmList germplasmList = GermplasmListTestDataInitializer.createGermplasmList(LIST_ID, false);
		this.germplasmListId = this.germplasmListManager.addGermplasmList(germplasmList);

		final List<GermplasmListData> germplasmListData =
				GermplasmListTestDataInitializer.createGermplasmListData(germplasmList, new ArrayList<>(germplasmMap.keySet()), false);
		this.germplasmListManager.addGermplasmListData(germplasmListData);
	}


	private void initLotsAndTransactions(final Integer germplasmListId) {

		final List<Lot> lots =
				this.inventoryDetailsTestDataInitializer.createLots(new ArrayList<>(this.germplasmMap.keySet()), germplasmListId, SCALE_ID,
						LOCATION_ID);

		final List<Integer> lotIds = this.inventoryDataManager.addLots(lots);

		lots.clear();

		final Map<Integer, Integer> lotIdLrecIdMap = new HashMap<Integer, Integer>();
		final Map<Integer, Integer> gidLotIdMap = new HashMap<Integer, Integer>();

		this.germplasmListData = this.germplasmListManager.getGermplasmListDataByListId(germplasmListId);

		for (final Integer lotId : lotIds) {
			final Lot lot = this.inventoryDataManager.getLotById(lotId);
			lots.add(lot);
			gidLotIdMap.put(lot.getEntityId(), lotId);
		}


		for (final GermplasmListData listEntry : this.germplasmListData) {
			final Integer gid = listEntry.getGermplasmId();
			lotIdLrecIdMap.put(gidLotIdMap.get(gid), listEntry.getId());
		}


		final List<Transaction> listTransaction =
				this.inventoryDetailsTestDataInitializer.createReservedTransactions(lots, this.germplasmListId, lotIdLrecIdMap, STOCK_ID_PREFIX);

		final List<Integer> transactionIds = this.inventoryDataManager.addTransactions(listTransaction);

		for (final Integer transactionId : transactionIds) {
			final Transaction transaction = this.inventoryDataManager.getTransactionById(transactionId);
			this.listDataIdTransactionMap.put(transaction.getSourceRecordId(), transaction);
		}

	}

	@Test
	public void testGetTransactionDetailsForLot() {

		final Germplasm germplasm =
				GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		final Integer germplasmId = this.germplasmDataManager.addGermplasm(germplasm, germplasm.getPreferredName());

		final WorkbenchUser user = this.testDataInitializer.createUserForTesting();

		final Lot lot = InventoryDetailsTestDataInitializer.createLot(user.getUserid(), "GERMPLSM", germplasmId, 1, 8264, 0, 1, "Comments");
		final Date createdDate = new Date();
		lot.setCreatedDate(createdDate);
		this.inventoryDataManager.addLots(com.google.common.collect.Lists.<Lot>newArrayList(lot));

		final Transaction depositTransaction =
				InventoryDetailsTestDataInitializer.createReservationTransaction(5.0, 0, "Deposit", lot, 1, 1, 1, "LIST");
		depositTransaction.setTransactionDate(20150101);
		depositTransaction.setUserId(user.getUserid());

		final Transaction closedTransaction =
				InventoryDetailsTestDataInitializer.createReservationTransaction(-5.0, 1, "Discard", lot, 1, 1, 1, "LIST");
		closedTransaction.setTransactionDate(20151010);
		closedTransaction.setUserId(user.getUserid());

		final List<Transaction> transactionList = new ArrayList<>();
		transactionList.add(depositTransaction);
		transactionList.add(closedTransaction);
		this.inventoryDataManager.addTransactions(transactionList);

		final List<TransactionReportRow> transactionReportRows = this.dao.getTransactionDetailsForLot(lot.getId());

		for (final TransactionReportRow reportRow : transactionReportRows) {
			Assert.assertTrue(DateUtils.isSameDay(createdDate, reportRow.getDate()));
			if (LOT_DEPOSIT.equals(reportRow.getLotStatus())) {

				Assert.assertEquals(depositTransaction.getQuantity(), reportRow.getQuantity());
				Assert.assertEquals(LOT_DEPOSIT, reportRow.getLotStatus());
				Assert.assertEquals(depositTransaction.getComments(), reportRow.getCommentOfLot());

			}
			if (LOT_DISCARD.equals(reportRow.getLotStatus())) {
				Assert.assertEquals(closedTransaction.getComments(), reportRow.getCommentOfLot());
				Assert.assertEquals(closedTransaction.getQuantity(), reportRow.getQuantity());
				Assert.assertEquals(LOT_DISCARD, reportRow.getLotStatus());
			}
			Assert.assertEquals(user.getUserid(), reportRow.getUserId());
		}

	}
}
