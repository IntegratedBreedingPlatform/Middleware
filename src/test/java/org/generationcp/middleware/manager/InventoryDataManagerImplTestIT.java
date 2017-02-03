/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.data.initializer.GermplasmListDataTestDataInitializer;
import org.generationcp.middleware.data.initializer.GermplasmListTestDataInitializer;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.data.initializer.InventoryDetailsTestDataInitializer;
import org.generationcp.middleware.domain.inventory.InventoryDetails;
import org.generationcp.middleware.domain.inventory.ListDataInventory;
import org.generationcp.middleware.domain.inventory.ListEntryLotDetails;
import org.generationcp.middleware.domain.inventory.LotDetails;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.ims.EntityType;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.ReservedInventoryKey;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.generationcp.middleware.pojos.report.LotReportRow;
import org.generationcp.middleware.pojos.report.TransactionReportRow;
import org.generationcp.middleware.service.api.InventoryService;
import org.generationcp.middleware.utils.test.Debug;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class InventoryDataManagerImplTestIT extends IntegrationTestBase {

	private static final String TEST_DUPLICATE = "TEST_DUPLICATE";
	private static final String TEST_BULK_WITH = "SID1-2";
	private static final String TEST_BULK_COMPL = "Y";
	private static final int TEST_LOCATION_ID = 1;
	private static final int TEST_SCALE_ID = 2;
	private static final double TEST_AMOUNT = 1.0;
	private static final String TEST_COMMENT = "TEST COMMENT";
	private static final String LOT_ID_KEY = "lotId";
	private static final String TRN_ID_KEY = "trnId";
	private static final String LIST_DATA_PROJECT_ID_KEY = "listDataProjectId";
	private static final String DUPLICATE_KEY = "duplicate";
	private static final String BULK_WITH_KEY = "bulkWith";
	private static final String BULK_COMPL_KEY = "bulkCompl";
	private static final String LOCATION_ID_KEY = "locationId";
	private static final String SCALE_ID_KEY = "scaleId";
	private static final String AMOUNT_KEY = "amount";
	private static final String COMMENT_KEY = "comment";

	@Autowired
	private InventoryDataManager manager;

	@Autowired
	private GermplasmDataManager germplasmDataManager;

	@Autowired
	private InventoryService inventoryService;

	@Autowired
	private GermplasmListManager germplasmListManager;

	GermplasmListTestDataInitializer germplasmListTestDataInitializer;

	GermplasmListDataTestDataInitializer germplasmListDataTestDataInitializer;

	private Integer lotId;
	private Lot lot;

	@Before
	public void setUpBefore() throws Exception {
		List<Lot> lots = new ArrayList<Lot>();
		this.lot = new Lot(null, 1, EntityType.GERMPLSM.name(), 50533, 9001, 1538, 0, 0, "sample added lot 1");
		lots.add(this.lot);
		lots.add(new Lot(null, 1, EntityType.GERMPLSM.name(), 50533, 9002, 1539, 0, 0, "sample added lot 2"));
		List<Integer> idList = this.manager.addLots(lots);
		this.lotId = idList.get(0);
		this.lot.setId(this.lotId);

		List<Transaction> transactions = new ArrayList<Transaction>();
		transactions.add(new Transaction(null, 1, this.manager.getLotsByEntityType(EntityType.GERMPLSM.name(), 0, 1).get(0), Integer
				.valueOf(20140413), 1, -1d, "sample added transaction 1", 0, null, null, null, 100d, 1, null));
		transactions.add(new Transaction(null, 1, this.manager.getLotsByEntityType(EntityType.GERMPLSM.name(), 0, 1).get(0), Integer
				.valueOf(20140518), 1, -2d, "sample added transaction 2", 0, null, null, null, 150d, 1, null));
		transactions.add(new Transaction(null, 1, this.manager.getLotsByEntityType(EntityType.GERMPLSM.name(), 0, 1).get(0), Integer
				.valueOf(20140518), 0, -2d, "sample added transaction 2", 0, null, null, null, 150d, 1, null));
		transactions.add(new Transaction(null, 1, this.manager.getLotsByEntityType(EntityType.GERMPLSM.name(), 0, 1).get(0), Integer
				.valueOf(20140518), 0, 2d, "sample added transaction 2", 0, null, null, null, 150d, 1, null));
		transactions.add(new Transaction(null, 1, this.lot, Integer.valueOf(20140413), 1, -1d, "sample added transaction 1", 0, null, null,
				null, 100d, 1, null));
		transactions.add(new Transaction(null, 1, this.manager.getLotsByEntityType(EntityType.GERMPLSM.name(), 0, 1).get(0), Integer
				.valueOf(20120518), 1, 2d, "sample added transaction 2", 0, null, null, null, 150d, 1, null));
		Set<Transaction> transactionSet = new HashSet<>();
		transactionSet.add(transactions.get(4));
		this.lot.setTransactions(transactionSet);
		this.manager.addTransactions(transactions);

		germplasmListTestDataInitializer = new GermplasmListTestDataInitializer();
		germplasmListDataTestDataInitializer = new GermplasmListDataTestDataInitializer();
	}

	@Test
	public void testGetLotsByEntityType() throws Exception {
		String type = EntityType.GERMPLSM.name();
		List<Lot> results = this.manager.getLotsByEntityType(type, 0, 5);
		Assert.assertTrue(results != null);
		Assert.assertTrue(!results.isEmpty());
		Debug.println(IntegrationTestBase.INDENT, "testGetLotsByEntityType(" + type + "): ");
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testCountLotsByEntityType() throws Exception {
		Debug.println(IntegrationTestBase.INDENT,
				"testCountLotsByEntityType(\"GERMPLSM\"): " + this.manager.countLotsByEntityType(EntityType.GERMPLSM.name()));
	}

	@Test
	public void testGetLotsByEntityTypeAndEntityId() throws Exception {
		String type = EntityType.GERMPLSM.name();
		Integer entityId = Integer.valueOf(50533);
		List<Lot> results = this.manager.getLotsByEntityTypeAndEntityId(type, entityId, 0, 5);
		Assert.assertTrue(results != null);
		Assert.assertTrue(!results.isEmpty());
		Debug.println(IntegrationTestBase.INDENT, "testGetLotsByEntityTypeAndEntityId(type=" + type + ", entityId=" + entityId + "): ");
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testCountLotsByEntityTypeAndEntityId() throws Exception {
		String type = EntityType.GERMPLSM.name();
		Integer entityId = Integer.valueOf(50533);
		Debug.println(IntegrationTestBase.INDENT, "testCountLotsByEntityTypeAndEntityId(type=" + type + ", entityId=" + entityId + "): "
				+ this.manager.countLotsByEntityTypeAndEntityId(type, entityId));
	}

	@Test
	public void testGetLotsByEntityTypeAndLocationId() throws Exception {
		String type = EntityType.GERMPLSM.name();
		Integer locationId = Integer.valueOf(9001);
		List<Lot> results = this.manager.getLotsByEntityTypeAndLocationId(type, locationId, 0, 5);
		Assert.assertTrue(results != null);
		Assert.assertTrue(!results.isEmpty());
		Debug.println(IntegrationTestBase.INDENT, "testGetLotsByEntityTypeAndLocationId(type=" + type + ", locationId=" + locationId
				+ "): ");
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testCountLotsByEntityTypeAndLocationId() throws Exception {
		String type = EntityType.GERMPLSM.name();
		Integer locationId = Integer.valueOf(9000);
		Debug.println(IntegrationTestBase.INDENT, "testCountLotsByEntityTypeAndLocationId(type=" + type + ", locationId=" + locationId
				+ "): " + this.manager.countLotsByEntityTypeAndLocationId(type, locationId));
	}

	@Test
	public void testGetLotsByEntityTypeAndEntityIdAndLocationId() throws Exception {
		String type = EntityType.GERMPLSM.name();
		Integer entityId = Integer.valueOf(50533);
		Integer locationId = Integer.valueOf(9001);
		List<Lot> results = this.manager.getLotsByEntityTypeAndEntityIdAndLocationId(type, entityId, locationId, 0, 5);
		Assert.assertTrue(results != null);
		Assert.assertTrue(!results.isEmpty());
		Debug.println(IntegrationTestBase.INDENT, "testGetLotsByEntityTypeAndEntityIdAndLocationId(type=" + type + ", entityId=" + entityId
				+ ", locationId=" + locationId + "): ");
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testCountLotsByEntityTypeAndEntityIdAndLocationId() throws Exception {
		String type = EntityType.GERMPLSM.name();
		Integer entityId = Integer.valueOf(50533);
		Integer locationId = Integer.valueOf(9000);
		Debug.println(IntegrationTestBase.INDENT,
				"testCountLotsByEntityTypeAndEntityIdAndLocationId(type=" + type + ", entityId=" + entityId + ", locationId=" + locationId
						+ "): " + this.manager.countLotsByEntityTypeAndEntityIdAndLocationId(type, entityId, locationId));
	}

	@Test
	public void testGetActualLotBalance() throws Exception {
		Integer lotId = Integer.valueOf(1);
		Debug.println(IntegrationTestBase.INDENT,
				"testGetActualLotBalance(lotId=" + lotId + "): " + this.manager.getActualLotBalance(lotId));
	}

	@Test
	public void testGetAvailableLotBalance() throws Exception {
		Integer lotId = Integer.valueOf(1);
		Debug.println(IntegrationTestBase.INDENT,
				"testGetAvailableLotBalance(lotId=" + lotId + "): " + this.manager.getAvailableLotBalance(lotId));
	}

	@Test
	public void testAddLot() throws Exception {
		Lot lot = new Lot(null, 1, EntityType.GERMPLSM.name(), 50533, 9001, 6088, 0, 0, "sample added lot");
		this.manager.addLot(lot);
		Assert.assertNotNull(lot.getId());
		Debug.println(IntegrationTestBase.INDENT, "Added: " + lot.toString());
	}

	@Test
	public void testAddLots() throws Exception {
		List<Lot> lots = new ArrayList<Lot>();
		lots.add(new Lot(null, 1, EntityType.GERMPLSM.name(), 50533, 9001, 1538, 0, 0, "sample added lot 1"));
		lots.add(new Lot(null, 1, EntityType.GERMPLSM.name(), 50533, 9002, 1539, 0, 0, "sample added lot 2"));
		List<Integer> idList = this.manager.addLots(lots);

		Assert.assertFalse(idList.isEmpty());
		Debug.println(IntegrationTestBase.INDENT, "Added: ");
		Debug.printObjects(IntegrationTestBase.INDENT * 2, lots);
	}

	@Test
	public void testUpdateLot() throws Exception {
		// this test assumes there are existing lot records with entity type = GERMPLSM
		Lot lot = this.manager.getLotsByEntityType(EntityType.GERMPLSM.name(), 0, 1).get(0);

		Debug.println(IntegrationTestBase.INDENT, "BEFORE: " + lot.toString());

		String oldComment = lot.getComments();
		String newComment = oldComment + " UPDATED " + (int) (Math.random() * 100);
		if (newComment.length() > 255) {
			newComment = newComment.substring(newComment.length() - 255);
		}
		lot.setComments(newComment);

		this.manager.updateLot(lot);

		Assert.assertFalse(oldComment.equals(lot.getComments()));
		Debug.println(IntegrationTestBase.INDENT, "AFTER: " + lot.toString());
	}

	@Test
	public void testUpdateLots() throws Exception {
		// this test assumes there are at least 2 existing lot records with entity type = GERMPLSM
		List<Lot> lots = this.manager.getLotsByEntityType(EntityType.GERMPLSM.name(), 0, 2);

		Debug.println(IntegrationTestBase.INDENT, "BEFORE: ");
		Debug.printObjects(IntegrationTestBase.INDENT * 2, lots);

		if (lots.size() == 2) {
			String oldComment = lots.get(0).getComments();
			for (Lot lot : lots) {
				String newComment = lot.getComments() + " UPDATED " + (int) (Math.random() * 100);
				if (newComment.length() > 255) {
					newComment = newComment.substring(newComment.length() - 255);
				}
				lot.setComments(newComment);
			}
			this.manager.updateLots(lots);
			Debug.println(IntegrationTestBase.INDENT, "AFTER: ");
			Debug.printObjects(IntegrationTestBase.INDENT * 2, lots);
			Assert.assertFalse(oldComment.equals(lots.get(0).getComments()));
		} else {
			Debug.println(IntegrationTestBase.INDENT, "At least two LOT entries of type=\"GERMPLSM\" are required in this test");
		}
	}

	@Test
	public void testAddTransaction() throws Exception {
		Transaction transaction =
				new Transaction(null, 1, this.manager.getLotsByEntityType(EntityType.GERMPLSM.name(), 0, 1).get(0),
						Integer.valueOf(20140413), 1, 200d, "sample added transaction", 0, null, null, null, 100d, 1, null);
		this.manager.addTransaction(transaction);
		Assert.assertNotNull(transaction.getId());
		Debug.println(IntegrationTestBase.INDENT, "testAddTransaction() Added: " + transaction);
	}

	@Test
	public void testAddTransactions() throws Exception {
		// this test assumes there are existing lot records with entity type = GERMPLSM
		List<Transaction> transactions = new ArrayList<Transaction>();
		transactions.add(new Transaction(null, 1, this.manager.getLotsByEntityType(EntityType.GERMPLSM.name(), 0, 1).get(0), Integer
				.valueOf(20140413), 1, 200d, "sample added transaction 1", 0, null, null, null, 100d, 1, null));
		transactions.add(new Transaction(null, 1, this.manager.getLotsByEntityType(EntityType.GERMPLSM.name(), 0, 1).get(0), Integer
				.valueOf(20140518), 1, 300d, "sample added transaction 2", 0, null, null, null, 150d, 1, null));
		this.manager.addTransactions(transactions);
		Assert.assertNotNull(transactions.get(0).getId());
		Debug.printObjects(IntegrationTestBase.INDENT, transactions);
	}

	@Test
	public void testUpdateTransaction() throws Exception {
		// this test assumes that there are existing records in the transaction table

		Transaction transaction = this.manager.getTransactionById(Integer.valueOf(1));
		Debug.println(IntegrationTestBase.INDENT, "BEFORE: " + transaction.toString());

		// Update comment
		String oldComment = transaction.getComments();
		String newComment = oldComment + " UPDATED " + (int) (Math.random() * 100);
		if (newComment.length() > 255) {
			newComment = newComment.substring(newComment.length() - 255);
		}
		transaction.setComments(newComment);

		// Invert status
		transaction.setStatus(transaction.getStatus() ^ 1);

		this.manager.updateTransaction(transaction);

		Assert.assertFalse(oldComment.equals(transaction.getComments()));
		Debug.println(IntegrationTestBase.INDENT, "AFTER: " + transaction.toString());
	}

	@Test
	public void testUpdateTransactions() throws Exception {
		// Assumption: There are more than 2 transactions of lot_id = 1
		List<Transaction> transactions = this.manager.getAllTransactions(0, 2);

		if (transactions.size() == 2) {
			Debug.println(IntegrationTestBase.INDENT, "BEFORE: ");
			Debug.printObjects(IntegrationTestBase.INDENT * 2, transactions);
			String oldComment = transactions.get(0).getComments();

			for (Transaction transaction : transactions) {
				// Update comment
				String newComment = transaction.getComments() + " UPDATED " + (int) (Math.random() * 100);
				if (newComment.length() > 255) {
					newComment = newComment.substring(newComment.length() - 255);
				}
				transaction.setComments(newComment);

				// Invert status
				transaction.setStatus(transaction.getStatus() ^ 1);
			}
			this.manager.updateTransactions(transactions);

			Assert.assertFalse(oldComment.equals(transactions.get(0).getComments()));
			Debug.println(IntegrationTestBase.INDENT, "AFTER: ");
			Debug.printObjects(IntegrationTestBase.INDENT * 2, transactions);
		} else {
			Debug.println(IntegrationTestBase.INDENT, "At least two TRANSACTION entries are required in this test");
		}
	}

	@Test
	public void testGetTransactionsByLotId() throws Exception {
		Set<Transaction> transactions = this.manager.getTransactionsByLotId(this.lotId);
		Assert.assertTrue(transactions != null);
		Assert.assertTrue(!transactions.isEmpty());
		Debug.println(IntegrationTestBase.INDENT, "testGetTransactionsByLotId(" + this.lotId + "): ");
		Debug.printObjects(IntegrationTestBase.INDENT, new ArrayList<Transaction>(transactions));
	}

	@Test
	public void testGetAllReserveTransactions() throws Exception {
		List<Transaction> transactions = this.manager.getAllReserveTransactions(0, 5);
		Assert.assertTrue(transactions != null);
		Assert.assertTrue(!transactions.isEmpty());
		Debug.printObjects(IntegrationTestBase.INDENT, transactions);
	}

	@Test
	public void testCountAllReserveTransactions() throws Exception {
		Debug.println(IntegrationTestBase.INDENT, "countAllReserveTransactions(): " + this.manager.countAllReserveTransactions());
	}

	@Test
	public void testGetAllDepositTransactions() throws Exception {
		List<Transaction> transactions = this.manager.getAllDepositTransactions(0, 5);
		Assert.assertTrue(transactions != null);
		Assert.assertTrue(!transactions.isEmpty());
		Debug.println(IntegrationTestBase.INDENT, "testGetAllDepositTransactions(): ");
		Debug.printObjects(IntegrationTestBase.INDENT, transactions);
	}

	@Test
	public void testCountAllDepositTransactions() throws Exception {
		Debug.println(IntegrationTestBase.INDENT, "countAllDepositTransactions(): " + this.manager.countAllDepositTransactions());
	}

	@Test
	public void testGetAllReserveTransactionsByRequestor() throws Exception {
		Integer personId = Integer.valueOf(1);
		List<Transaction> transactions = this.manager.getAllReserveTransactionsByRequestor(personId, 0, 5);
		Assert.assertTrue(transactions != null);
		Assert.assertTrue(!transactions.isEmpty());
		Debug.println(IntegrationTestBase.INDENT, "testGetAllReserveTransactionsByRequestor(" + personId + "): ");
		Debug.printObjects(IntegrationTestBase.INDENT, transactions);
	}

	@Test
	public void testCountAllReserveTransactionsByRequestor() throws Exception {
		Integer personId = Integer.valueOf(253);
		Debug.println(
				IntegrationTestBase.INDENT,
				"countAllReserveTransactionsByRequestor(" + personId + "): "
						+ this.manager.countAllReserveTransactionsByRequestor(personId));
	}

	@Test
	public void testGetAllDepositTransactionsByDonor() throws Exception {
		Integer personId = Integer.valueOf(1);
		List<Transaction> transactions = this.manager.getAllDepositTransactionsByDonor(personId, 0, 5);
		Assert.assertTrue(transactions != null);
		Assert.assertTrue(!transactions.isEmpty());
		Debug.println(IntegrationTestBase.INDENT, "testGetAllDepositTransactionsByDonor(" + personId + "): ");
		Debug.printObjects(IntegrationTestBase.INDENT, transactions);
	}

	@Test
	public void testCountAllDepositTransactionsByDonor() throws Exception {
		Integer personId = Integer.valueOf(1);
		Debug.println(IntegrationTestBase.INDENT,
				"CountAllDepositTransactionsByDonor(" + personId + "): " + this.manager.countAllDepositTransactionsByDonor(personId));
	}

	@Test
	public void testGenerateReportOnAllUncommittedTransactions() throws Exception {
		Debug.println(IntegrationTestBase.INDENT,
				"Number of uncommitted transactions [countAllUncommittedTransactions()]: " + this.manager.countAllUncommittedTransactions());
		List<TransactionReportRow> report = this.manager.generateReportOnAllUncommittedTransactions(0, 5);
		Assert.assertTrue(report != null);
		Assert.assertTrue(!report.isEmpty());
		Debug.printObjects(IntegrationTestBase.INDENT, report);
	}

	@Test
	public void testGenerateReportOnAllReserveTransactions() throws Exception {
		Debug.println(IntegrationTestBase.INDENT,
				"Number of reserved transactions [countAllReserveTransactions()]: " + this.manager.countAllReserveTransactions());
		List<TransactionReportRow> report = this.manager.generateReportOnAllReserveTransactions(0, 5);
		Assert.assertTrue(report != null);
		Assert.assertTrue(!report.isEmpty());
		Debug.printObjects(IntegrationTestBase.INDENT, report);
	}

	@Test
	public void testGenerateReportOnAllWithdrawalTransactions() throws Exception {
		Debug.println(IntegrationTestBase.INDENT,
				"Number of withdrawal transactions [countAllWithdrawalTransactions()]: " + this.manager.countAllWithdrawalTransactions());
		List<TransactionReportRow> report = this.manager.generateReportOnAllWithdrawalTransactions(0, 5);
		Assert.assertTrue(report != null);
		Assert.assertTrue(!report.isEmpty());
		Debug.printObjects(IntegrationTestBase.INDENT, report);
	}

	@Test
	public void testGenerateReportOnAllLots() throws Exception {
		Debug.println(IntegrationTestBase.INDENT, "Balance Report on All Lots");
		Debug.println(IntegrationTestBase.INDENT, "Number of lots [countAllLots()]: " + this.manager.countAllLots());
		List<LotReportRow> report = this.manager.generateReportOnAllLots(0, 10);
		Assert.assertTrue(report != null);
		Assert.assertTrue(!report.isEmpty());
		Debug.printObjects(IntegrationTestBase.INDENT, report);
	}

	@Test
	public void testGenerateReportsOnDormantLots() throws Exception {
		int year = 2012;
		Debug.println(IntegrationTestBase.INDENT, "Balance Report on DORMANT Lots");
		List<LotReportRow> report = this.manager.generateReportOnDormantLots(year, 0, 10);
		Assert.assertTrue(report != null);
		Assert.assertTrue(!report.isEmpty());
		Debug.println(IntegrationTestBase.INDENT, "testGenerateReportsOnDormantLots(year=" + year + ") REPORT: ");
		Debug.printObjects(IntegrationTestBase.INDENT, report);
	}

	@Test
	public void testGenerateReportOnLotsByEntityType() throws Exception {
		String type = EntityType.GERMPLSM.name();
		Debug.println(IntegrationTestBase.INDENT, "Balance Report on Lots by Entity Type: " + type);
		List<LotReportRow> report = this.manager.generateReportOnLotsByEntityType(type, 0, 10);
		Assert.assertTrue(report != null);
		Assert.assertTrue(!report.isEmpty());
		Debug.println(IntegrationTestBase.INDENT, "testGenerateReportOnLotsByEntityType(" + type + ") REPORT: ");
		Debug.printObjects(IntegrationTestBase.INDENT, report);
	}

	@Test
	public void testGenerateReportOnLotsByEntityTypeAndEntityId() throws Exception {
		Debug.println(IntegrationTestBase.INDENT, "Balance Report on Lots by Entity Type and Entity ID:");
		String type = EntityType.GERMPLSM.name();
		List<Integer> entityIdList = new ArrayList<Integer>();
		entityIdList.add(50533);
		entityIdList.add(537652);

		List<LotReportRow> report = this.manager.generateReportOnLotsByEntityTypeAndEntityId(type, entityIdList, 0, 10);

		Assert.assertTrue(report != null);
		Assert.assertTrue(!report.isEmpty());
		Debug.println(IntegrationTestBase.INDENT, "testGenerateReportOnLotsByEntityTypeAndEntityId(type=" + type + ", entityId="
				+ entityIdList + ") REPORT: ");
		Debug.printObjects(IntegrationTestBase.INDENT, report);
	}

	@Test
	public void testGenerateReportOnEmptyLot() throws Exception {
		Debug.println(IntegrationTestBase.INDENT, "Report on empty lot");
		List<LotReportRow> report = this.manager.generateReportOnEmptyLots(0, 2);
		Assert.assertTrue(report != null);
		Debug.println(IntegrationTestBase.INDENT, "testGenerateReportOnEmptyLot() REPORT: ");
		Debug.printObjects(IntegrationTestBase.INDENT, report);
	}

	@Test
	public void testGenerateReportOnLotWithMinimumAmount() throws Exception {
		long minimumAmount = 700;
		Debug.println(IntegrationTestBase.INDENT, "Report on lot with minimum balance");
		List<LotReportRow> report = this.manager.generateReportOnLotsWithMinimumAmount(minimumAmount, 0, 5);
		Assert.assertTrue(report != null);
		Debug.println(IntegrationTestBase.INDENT, "testGenerateReportOnLotWithMinimumAmount(minimumAmount=" + minimumAmount + ") REPORT: ");
		Debug.printObjects(IntegrationTestBase.INDENT, report);
	}

	@Test
	public void testCountAllUncommittedTransactions() throws Exception {
		Debug.println(IntegrationTestBase.INDENT,
				"testCountAllUncommittedTransactions(): " + this.manager.countAllUncommittedTransactions());
	}

	@Test
	public void testCountAllWithdrawalTransactions() throws Exception {
		Debug.println(IntegrationTestBase.INDENT, "testCountAllWithdrawalTransactions(): " + this.manager.countAllWithdrawalTransactions());
	}

	@Test
	public void testCountAllLots() throws Exception {
		Debug.println(IntegrationTestBase.INDENT, "testCountAllLots(): " + this.manager.countAllLots());
	}

	@Test
	public void testGetAllLots() throws Exception {
		List<Lot> results = this.manager.getAllLots(0, Integer.MAX_VALUE);
		Assert.assertNotNull(results);
		Assert.assertTrue(!results.isEmpty());
		Debug.println(IntegrationTestBase.INDENT, "testGetAllLots(): ");
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testGetTransactionById() throws Exception {
		Integer id = 1;
		Transaction transactionid = this.manager.getTransactionById(id);
		Assert.assertNotNull(transactionid);
		Debug.println(IntegrationTestBase.INDENT, "testGetTransactionById(" + id + "): ");
		Debug.println(transactionid.toString());
	}

	@Test
	public void testGetLotCountsForGermplasmList() throws MiddlewareQueryException {
		int listid = 1;
		List<GermplasmListData> listEntries = this.manager.getLotCountsForList(listid, 0, Integer.MAX_VALUE);
		for (GermplasmListData entry : listEntries) {
			ListDataInventory inventory = entry.getInventoryInfo();
			if (inventory != null) {
				System.out.println(inventory);
			}
		}
	}

	@Test
	public void testGetLotCountsForGermplasmListEntries() throws MiddlewareQueryException {
		int listid = 1;
		List<Integer> entryIds = new ArrayList<Integer>();
		entryIds.add(1);
		entryIds.add(2);
		entryIds.add(3);
		List<GermplasmListData> listEntries = this.manager.getLotCountsForListEntries(listid, entryIds);
		for (GermplasmListData entry : listEntries) {
			ListDataInventory inventory = entry.getInventoryInfo();
			if (inventory != null) {
				System.out.println(inventory);
			}
		}
	}

	@Test
	public void testGetLotsForGermplasmListEntry() throws MiddlewareQueryException {
		List<ListEntryLotDetails> lots = this.manager.getLotDetailsForListEntry(-543041, -507029, -88175);
		for (ListEntryLotDetails lot : lots) {
			Debug.print(lot);
		}
	}

	@Test
	public void testGetLotsForGermplasmList() throws MiddlewareQueryException {
		List<GermplasmListData> listEntries = this.manager.getLotDetailsForList(-543041, 0, 500);
		for (GermplasmListData entry : listEntries) {
			Debug.print("Id=" + entry.getId() + ", GID = " + entry.getGid());
			Debug.print(3, entry.getInventoryInfo());
		}
	}

	@Test
	public void testGetLotCountsForGermplasm() throws MiddlewareQueryException {
		int gid = -644052;
		Integer count = this.manager.countLotsWithAvailableBalanceForGermplasm(gid);
		Debug.print("GID=" + gid + ", lotCount=" + count);
	}

	@Test
	public void testGetLotsForGermplasm() throws MiddlewareQueryException {
		int gid = 89;
		List<LotDetails> lots = this.manager.getLotDetailsForGermplasm(gid);
		for (LotDetails lot : lots) {
			System.out.println(lot);
		}
	}

	@Test
	public void testCancelReservedInventory() throws MiddlewareQueryException {
		int lrecId = -520659;
		int lotId = 340597;

		List<ReservedInventoryKey> lotEntries = new ArrayList<ReservedInventoryKey>();
		lotEntries.add(new ReservedInventoryKey(1, lrecId, lotId));
		this.manager.cancelReservedInventory(lotEntries);
	}

	@Test
	public void testGetStockIdsByListDataProjectListId() throws MiddlewareQueryException {
		List<String> stockIds = this.manager.getStockIdsByListDataProjectListId(17);
		Assert.assertNotNull(stockIds);
	}

	@Test
	public void testUpdateInventory() throws MiddlewareQueryException {
		Integer listId = 17;
		List<InventoryDetails> inventoryDetailList = this.inventoryService.getInventoryListByListDataProjectListId(listId);
		if (inventoryDetailList != null && !inventoryDetailList.isEmpty()) {
			InventoryDetails inventoryDetails = inventoryDetailList.get(0);
			Map<String, Object> originalData = this.getInventorySpecificDetails(inventoryDetails);
			this.modifyInventoryDetails(inventoryDetails);
			this.manager.updateInventory(listId, inventoryDetailList);
			InventoryDetails modifiedInventoryDetails = this.getModifiedInventoryDetails(originalData,
							this.inventoryService.getInventoryListByListDataProjectListId(listId));
			Assert.assertEquals(InventoryDataManagerImplTestIT.TEST_DUPLICATE, modifiedInventoryDetails.getDuplicate());
			Assert.assertEquals(InventoryDataManagerImplTestIT.TEST_BULK_WITH, modifiedInventoryDetails.getBulkWith());
			Assert.assertEquals(InventoryDataManagerImplTestIT.TEST_BULK_COMPL, modifiedInventoryDetails.getBulkCompl());
			Assert.assertEquals(InventoryDataManagerImplTestIT.TEST_LOCATION_ID, modifiedInventoryDetails.getLocationId().intValue());
			Assert.assertEquals(InventoryDataManagerImplTestIT.TEST_SCALE_ID, modifiedInventoryDetails.getScaleId().intValue());
			Assert.assertEquals(0, modifiedInventoryDetails.getAmount().compareTo(InventoryDataManagerImplTestIT.TEST_AMOUNT));
			Assert.assertEquals(InventoryDataManagerImplTestIT.TEST_COMMENT, modifiedInventoryDetails.getComment());
			this.revertChangesToInventoryDetails(inventoryDetails, originalData);
			this.manager.updateInventory(listId, inventoryDetailList);
		}
	}

	private InventoryDetails getModifiedInventoryDetails(Map<String, Object> data, List<InventoryDetails> inventoryDetailList) {
		if (inventoryDetailList != null && !inventoryDetailList.isEmpty()) {
			Integer lotId = (Integer) data.get(InventoryDataManagerImplTestIT.LOT_ID_KEY);
			Integer trnId = (Integer) data.get(InventoryDataManagerImplTestIT.TRN_ID_KEY);
			Integer listDataProjectId = (Integer) data.get(InventoryDataManagerImplTestIT.LIST_DATA_PROJECT_ID_KEY);
			for (InventoryDetails inventoryDetails : inventoryDetailList) {
				if (lotId.equals(inventoryDetails.getLotId()) && trnId.equals(inventoryDetails.getTrnId())
						&& listDataProjectId.equals(inventoryDetails.getListDataProjectId())) {
					return inventoryDetails;
				}
			}
		}
		return null;
	}

	private void revertChangesToInventoryDetails(InventoryDetails inventoryDetails, Map<String, Object> originalData) {
		inventoryDetails.setDuplicate((String) originalData.get(InventoryDataManagerImplTestIT.DUPLICATE_KEY));
		inventoryDetails.setBulkWith((String) originalData.get(InventoryDataManagerImplTestIT.BULK_WITH_KEY));
		inventoryDetails.setBulkCompl((String) originalData.get(InventoryDataManagerImplTestIT.BULK_COMPL_KEY));
		inventoryDetails.setLocationId((Integer) originalData.get(InventoryDataManagerImplTestIT.LOCATION_ID_KEY));
		inventoryDetails.setScaleId((Integer) originalData.get(InventoryDataManagerImplTestIT.SCALE_ID_KEY));
		inventoryDetails.setAmount((Double) originalData.get(InventoryDataManagerImplTestIT.AMOUNT_KEY));
		inventoryDetails.setComment((String) originalData.get(InventoryDataManagerImplTestIT.COMMENT_KEY));
	}

	private void modifyInventoryDetails(InventoryDetails inventoryDetails) {
		inventoryDetails.setDuplicate(InventoryDataManagerImplTestIT.TEST_DUPLICATE);
		inventoryDetails.setBulkWith(InventoryDataManagerImplTestIT.TEST_BULK_WITH);
		inventoryDetails.setBulkCompl(InventoryDataManagerImplTestIT.TEST_BULK_COMPL);
		inventoryDetails.setLocationId(InventoryDataManagerImplTestIT.TEST_LOCATION_ID);
		inventoryDetails.setScaleId(InventoryDataManagerImplTestIT.TEST_SCALE_ID);
		inventoryDetails.setAmount(InventoryDataManagerImplTestIT.TEST_AMOUNT);
		inventoryDetails.setComment(InventoryDataManagerImplTestIT.TEST_COMMENT);
	}

	private Map<String, Object> getInventorySpecificDetails(InventoryDetails inventoryDetails) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put(InventoryDataManagerImplTestIT.LOT_ID_KEY, inventoryDetails.getLotId());
		data.put(InventoryDataManagerImplTestIT.TRN_ID_KEY, inventoryDetails.getTrnId());
		data.put(InventoryDataManagerImplTestIT.LIST_DATA_PROJECT_ID_KEY, inventoryDetails.getListDataProjectId());
		data.put(InventoryDataManagerImplTestIT.DUPLICATE_KEY, inventoryDetails.getDuplicate());
		data.put(InventoryDataManagerImplTestIT.BULK_WITH_KEY, inventoryDetails.getBulkWith());
		data.put(InventoryDataManagerImplTestIT.BULK_COMPL_KEY, inventoryDetails.getBulkCompl());
		data.put(InventoryDataManagerImplTestIT.LOCATION_ID_KEY, inventoryDetails.getLocationId());
		data.put(InventoryDataManagerImplTestIT.SCALE_ID_KEY, inventoryDetails.getScaleId());
		data.put(InventoryDataManagerImplTestIT.AMOUNT_KEY, inventoryDetails.getAmount());
		data.put(InventoryDataManagerImplTestIT.COMMENT_KEY, inventoryDetails.getComment());
		return data;
	}

	@Test
	public void testGetLotById() throws MiddlewareQueryException {
		Assert.assertNotNull(this.manager.getLotById(1));
	}

	@Test
	public void testGetTransactionsByIdList() throws Exception{
		List<Transaction> transactions = new ArrayList<Transaction>();
		Transaction transaction1 = new Transaction(null, 1, this.manager.getLotsByEntityType(EntityType.GERMPLSM.name(), 0, 1).get(0),
				Integer.valueOf(20140413), 1, 200d, "sample added transaction 1", 0, null, null, null, 100d, 1, null);
		transactions.add(transaction1);

		Transaction transaction2 = new Transaction(null, 1, this.manager.getLotsByEntityType(EntityType.GERMPLSM.name(), 0, 1).get(0),
				Integer.valueOf(20140518), 1, 300d, "sample added transaction 2", 0, null, null, null, 150d, 1, null);
		transactions.add(transaction2);

		this.manager.addTransactions(transactions);

		List<Integer> transactionIdList = Lists.newArrayList();
		transactionIdList.add(transaction1.getId());
		transactionIdList.add(transaction2.getId());

		List<Transaction> transactionList = this.manager.getTransactionsByIdList(transactionIdList);

		Assert.assertEquals(2, transactionList.size());
	}

	@Test
	public void testGetAvailableBalanceForGermplasms() throws Exception {
		final Germplasm germplasm = GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0 , 1 ,1 ,0, 1 ,1 , "MethodName",
				"LocationName");
		final Integer germplasmId = this.germplasmDataManager.addGermplasm(germplasm, germplasm.getPreferredName());

		Lot lot = InventoryDetailsTestDataInitializer.createLot(1, "GERMPLSM", germplasmId, 1, 8264, 0, 1, "Comments");
		this.manager.addLots(Lists.<Lot>newArrayList(lot));

		Transaction transaction = InventoryDetailsTestDataInitializer
				.createReservationTransaction(2.0, 0, "Deposit", lot, 1, 1, 1, "LIST");
		this.manager.addTransactions(Lists.<Transaction>newArrayList(transaction));

		List<Germplasm> availableBalanceForGermplasms =
				this.manager.getAvailableBalanceForGermplasms(Lists.<Germplasm>newArrayList(germplasm));

		Assert.assertEquals(1, availableBalanceForGermplasms.size());
		Assert.assertEquals(1, availableBalanceForGermplasms.get(0).getInventoryInfo().getActualInventoryLotCount().intValue());
		Assert.assertEquals("2.0", availableBalanceForGermplasms.get(0).getInventoryInfo().getTotalAvailableBalance().toString());
		Assert.assertEquals("g", availableBalanceForGermplasms.get(0).getInventoryInfo().getScaleForGermplsm());
	}

	@Test
	public void testRetrieveStockIds() throws Exception {
		final Germplasm germplasm = GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0 , 1 ,1 ,0, 1 ,1 , "MethodName",
				"LocationName");
		final Integer germplasmId = this.germplasmDataManager.addGermplasm(germplasm, germplasm.getPreferredName());


		final GermplasmList germplasmList = germplasmListTestDataInitializer.createGermplasmList(
				"GermplasmList", Integer.valueOf(1),
				"GermplasmList" + " Desc", null, 1, "programUUID");
		this.germplasmListManager.addGermplasmList(germplasmList);

		final GermplasmListData germplasmListData =
				germplasmListDataTestDataInitializer.createGermplasmListData(germplasmList, germplasmId, 2);
		this.germplasmListManager.addGermplasmListData(germplasmListData);

		Lot lotOne = InventoryDetailsTestDataInitializer.createLot(1, "GERMPLSM", germplasmId, 1, 8264, 0, 1, "First Lot for Gemrplasm");
		Lot lotTwo = InventoryDetailsTestDataInitializer.createLot(1, "GERMPLSM", germplasmId, 1, 8264, 0, 1, "Second Lot for Gemrplasm");
		this.manager.addLots(Lists.<Lot>newArrayList(lotOne, lotTwo));


		Transaction depositTransactionForLotOne =  InventoryDetailsTestDataInitializer
				.createDepositTransaction(5.0, 0, "Deposit", lotOne, 1, 1, germplasmListData.getId(), "LIST", "StockID1");
		Transaction depositTransactionForLotTwo =  InventoryDetailsTestDataInitializer
				.createDepositTransaction(5.0, 0, "Deposit", lotTwo, 1, 1, germplasmListData.getId(), "LIST", "StockID2");
		this.manager.addTransactions(Lists.<Transaction>newArrayList(depositTransactionForLotOne, depositTransactionForLotTwo));


		Map<Integer, String> germplsmWiseStockID = this.manager.retrieveStockIds(Lists.newArrayList(germplasmId));

		Assert.assertNotNull(germplsmWiseStockID);
		Assert.assertEquals(depositTransactionForLotOne.getInventoryID()+", "+depositTransactionForLotTwo.getInventoryID()
				, germplsmWiseStockID.get(germplasmId));
	}
}
