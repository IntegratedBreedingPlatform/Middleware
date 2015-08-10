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

import org.generationcp.middleware.DataManagerIntegrationTest;
import org.generationcp.middleware.MiddlewareIntegrationTest;
import org.generationcp.middleware.domain.gms.GermplasmListType;
import org.generationcp.middleware.domain.inventory.InventoryDetails;
import org.generationcp.middleware.domain.inventory.ListDataInventory;
import org.generationcp.middleware.domain.inventory.ListEntryLotDetails;
import org.generationcp.middleware.domain.inventory.LotDetails;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.InventoryDataManager;
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
import org.junit.BeforeClass;
import org.junit.Test;

public class InventoryDataManagerImplTestIT extends DataManagerIntegrationTest {

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

	private static InventoryDataManager manager;
	private static InventoryService inventoryService;

	private Integer lotId;
	private Lot lot;

	@BeforeClass
	public static void setUp() throws Exception {
		InventoryDataManagerImplTestIT.manager = DataManagerIntegrationTest.managerFactory.getInventoryDataManager();
		InventoryDataManagerImplTestIT.inventoryService = DataManagerIntegrationTest.managerFactory.getInventoryMiddlewareService();
	}

	@Before
	public void setUpBefore() throws Exception {
		List<Lot> lots = new ArrayList<Lot>();
		lot = new Lot(null, 1, EntityType.GERMPLSM.name(), 50533, 9001, 1538, 0, 0, "sample added lot 1");
		lots.add(lot);
		lots.add(new Lot(null, 1, EntityType.GERMPLSM.name(), 50533, 9002, 1539, 0, 0, "sample added lot 2"));
		List<Integer> idList = InventoryDataManagerImplTestIT.manager.addLots(lots);
		lotId = idList.get(0);
		lot.setId(lotId);

		List<Transaction> transactions = new ArrayList<Transaction>();
		transactions.add(new Transaction(null, 1, InventoryDataManagerImplTestIT.manager.getLotsByEntityType(EntityType.GERMPLSM.name(), 0,
				1).get(0), Integer.valueOf(20140413), 1, -1d, "sample added transaction 1", 0, null, null, null, 100d, 1, null));
		transactions.add(new Transaction(null, 1, InventoryDataManagerImplTestIT.manager.getLotsByEntityType(EntityType.GERMPLSM.name(), 0,
				1).get(0), Integer.valueOf(20140518), 1, -2d, "sample added transaction 2", 0, null, null, null, 150d, 1, null));
		transactions.add(new Transaction(null, 1, InventoryDataManagerImplTestIT.manager.getLotsByEntityType(EntityType.GERMPLSM.name(), 0,
				1).get(0), Integer.valueOf(20140518), 0, -2d, "sample added transaction 2", 0, null, null, null, 150d, 1, null));
		transactions.add(new Transaction(null, 1, InventoryDataManagerImplTestIT.manager.getLotsByEntityType(EntityType.GERMPLSM.name(), 0,
				1).get(0), Integer.valueOf(20140518), 0, 2d, "sample added transaction 2", 0, null, null, null, 150d, 1, null));
		transactions.add(new Transaction(null, 1, lot, Integer.valueOf(20140413), 1, -1d, "sample added transaction 1", 0, null, null, null,
				100d, 1, null));
		transactions.add(new Transaction(null, 1, InventoryDataManagerImplTestIT.manager.getLotsByEntityType(EntityType.GERMPLSM.name(), 0,
				1).get(0), Integer.valueOf(20120518), 1, 2d, "sample added transaction 2", 0, null, null, null, 150d, 1, null));
		Set<Transaction> transactionSet = new HashSet<>();
		transactionSet.add(transactions.get(4));
		lot.setTransactions(transactionSet);
		InventoryDataManagerImplTestIT.manager.addTransactions(transactions);
	}

	@Test
	public void testGetLotsByEntityType() throws Exception {
		String type = EntityType.GERMPLSM.name();
		List<Lot> results = InventoryDataManagerImplTestIT.manager.getLotsByEntityType(type, 0, 5);
		Assert.assertTrue(results != null);
		Assert.assertTrue(!results.isEmpty());
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetLotsByEntityType(" + type + "): ");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, results);
	}

	@Test
	public void testCountLotsByEntityType() throws Exception {
		Debug.println(MiddlewareIntegrationTest.INDENT, "testCountLotsByEntityType(\"GERMPLSM\"): "
				+ InventoryDataManagerImplTestIT.manager.countLotsByEntityType(EntityType.GERMPLSM.name()));
	}

	@Test
	public void testGetLotsByEntityTypeAndEntityId() throws Exception {
		String type = EntityType.GERMPLSM.name();
		Integer entityId = Integer.valueOf(50533);
		List<Lot> results = InventoryDataManagerImplTestIT.manager.getLotsByEntityTypeAndEntityId(type, entityId, 0, 5);
		Assert.assertTrue(results != null);
		Assert.assertTrue(!results.isEmpty());
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetLotsByEntityTypeAndEntityId(type=" + type + ", entityId=" + entityId
				+ "): ");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, results);
	}

	@Test
	public void testCountLotsByEntityTypeAndEntityId() throws Exception {
		String type = EntityType.GERMPLSM.name();
		Integer entityId = Integer.valueOf(50533);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testCountLotsByEntityTypeAndEntityId(type=" + type + ", entityId=" + entityId
				+ "): " + InventoryDataManagerImplTestIT.manager.countLotsByEntityTypeAndEntityId(type, entityId));
	}

	@Test
	public void testGetLotsByEntityTypeAndLocationId() throws Exception {
		String type = EntityType.GERMPLSM.name();
		Integer locationId = Integer.valueOf(9001);
		List<Lot> results = InventoryDataManagerImplTestIT.manager.getLotsByEntityTypeAndLocationId(type, locationId, 0, 5);
		Assert.assertTrue(results != null);
		Assert.assertTrue(!results.isEmpty());
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetLotsByEntityTypeAndLocationId(type=" + type + ", locationId=" + locationId
				+ "): ");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, results);
	}

	@Test
	public void testCountLotsByEntityTypeAndLocationId() throws Exception {
		String type = EntityType.GERMPLSM.name();
		Integer locationId = Integer.valueOf(9000);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testCountLotsByEntityTypeAndLocationId(type=" + type + ", locationId="
				+ locationId + "): " + InventoryDataManagerImplTestIT.manager.countLotsByEntityTypeAndLocationId(type, locationId));
	}

	@Test
	public void testGetLotsByEntityTypeAndEntityIdAndLocationId() throws Exception {
		String type = EntityType.GERMPLSM.name();
		Integer entityId = Integer.valueOf(50533);
		Integer locationId = Integer.valueOf(9001);
		List<Lot> results =
				InventoryDataManagerImplTestIT.manager.getLotsByEntityTypeAndEntityIdAndLocationId(type, entityId, locationId, 0, 5);
		Assert.assertTrue(results != null);
		Assert.assertTrue(!results.isEmpty());
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetLotsByEntityTypeAndEntityIdAndLocationId(type=" + type + ", entityId="
				+ entityId + ", locationId=" + locationId + "): ");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, results);
	}

	@Test
	public void testCountLotsByEntityTypeAndEntityIdAndLocationId() throws Exception {
		String type = EntityType.GERMPLSM.name();
		Integer entityId = Integer.valueOf(50533);
		Integer locationId = Integer.valueOf(9000);
		Debug.println(
				MiddlewareIntegrationTest.INDENT,
				"testCountLotsByEntityTypeAndEntityIdAndLocationId(type=" + type + ", entityId=" + entityId + ", locationId=" + locationId
						+ "): "
						+ InventoryDataManagerImplTestIT.manager.countLotsByEntityTypeAndEntityIdAndLocationId(type, entityId, locationId));
	}

	@Test
	public void testGetActualLotBalance() throws Exception {
		Integer lotId = Integer.valueOf(1);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetActualLotBalance(lotId=" + lotId + "): "
				+ InventoryDataManagerImplTestIT.manager.getActualLotBalance(lotId));
	}

	@Test
	public void testGetAvailableLotBalance() throws Exception {
		Integer lotId = Integer.valueOf(1);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetAvailableLotBalance(lotId=" + lotId + "): "
				+ InventoryDataManagerImplTestIT.manager.getAvailableLotBalance(lotId));
	}

	@Test
	public void testAddLot() throws Exception {
		Lot lot = new Lot(null, 1, EntityType.GERMPLSM.name(), 50533, 9001, 6088, 0, 0, "sample added lot");
		InventoryDataManagerImplTestIT.manager.addLot(lot);
		Assert.assertNotNull(lot.getId());
		Debug.println(MiddlewareIntegrationTest.INDENT, "Added: " + lot.toString());
	}

	@Test
	public void testAddLots() throws Exception {
		List<Lot> lots = new ArrayList<Lot>();
		lots.add(new Lot(null, 1, EntityType.GERMPLSM.name(), 50533, 9001, 1538, 0, 0, "sample added lot 1"));
		lots.add(new Lot(null, 1, EntityType.GERMPLSM.name(), 50533, 9002, 1539, 0, 0, "sample added lot 2"));
		List<Integer> idList = InventoryDataManagerImplTestIT.manager.addLots(lots);

		Assert.assertFalse(idList.isEmpty());
		Debug.println(MiddlewareIntegrationTest.INDENT, "Added: ");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT * 2, lots);
	}

	@Test
	public void testUpdateLot() throws Exception {
		// this test assumes there are existing lot records with entity type = GERMPLSM
		Lot lot = InventoryDataManagerImplTestIT.manager.getLotsByEntityType(EntityType.GERMPLSM.name(), 0, 1).get(0);

		Debug.println(MiddlewareIntegrationTest.INDENT, "BEFORE: " + lot.toString());

		String oldComment = lot.getComments();
		String newComment = oldComment + " UPDATED " + (int) (Math.random() * 100);
		if (newComment.length() > 255) {
			newComment = newComment.substring(newComment.length() - 255);
		}
		lot.setComments(newComment);

		InventoryDataManagerImplTestIT.manager.updateLot(lot);

		Assert.assertFalse(oldComment.equals(lot.getComments()));
		Debug.println(MiddlewareIntegrationTest.INDENT, "AFTER: " + lot.toString());
	}

	@Test
	public void testUpdateLots() throws Exception {
		// this test assumes there are at least 2 existing lot records with entity type = GERMPLSM
		List<Lot> lots = InventoryDataManagerImplTestIT.manager.getLotsByEntityType(EntityType.GERMPLSM.name(), 0, 2);

		Debug.println(MiddlewareIntegrationTest.INDENT, "BEFORE: ");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT * 2, lots);

		if (lots.size() == 2) {
			String oldComment = lots.get(0).getComments();
			for (Lot lot : lots) {
				String newComment = lot.getComments() + " UPDATED " + (int) (Math.random() * 100);
				if (newComment.length() > 255) {
					newComment = newComment.substring(newComment.length() - 255);
				}
				lot.setComments(newComment);
			}
			InventoryDataManagerImplTestIT.manager.updateLots(lots);
			Debug.println(MiddlewareIntegrationTest.INDENT, "AFTER: ");
			Debug.printObjects(MiddlewareIntegrationTest.INDENT * 2, lots);
			Assert.assertFalse(oldComment.equals(lots.get(0).getComments()));
		} else {
			Debug.println(MiddlewareIntegrationTest.INDENT, "At least two LOT entries of type=\"GERMPLSM\" are required in this test");
		}
	}

	@Test
	public void testAddTransaction() throws Exception {
		Transaction transaction =
				new Transaction(null, 1, InventoryDataManagerImplTestIT.manager.getLotsByEntityType(EntityType.GERMPLSM.name(), 0, 1)
						.get(0), Integer.valueOf(20140413), 1, 200d, "sample added transaction", 0, null, null, null, 100d, 1, null);
		InventoryDataManagerImplTestIT.manager.addTransaction(transaction);
		Assert.assertNotNull(transaction.getId());
		Debug.println(MiddlewareIntegrationTest.INDENT, "testAddTransaction() Added: " + transaction);
	}

	@Test
	public void testAddTransactions() throws Exception {
		// this test assumes there are existing lot records with entity type = GERMPLSM
		List<Transaction> transactions = new ArrayList<Transaction>();
		transactions.add(new Transaction(null, 1, InventoryDataManagerImplTestIT.manager.getLotsByEntityType(EntityType.GERMPLSM.name(), 0,
				1).get(0), Integer.valueOf(20140413), 1, 200d, "sample added transaction 1", 0, null, null, null, 100d, 1, null));
		transactions.add(new Transaction(null, 1, InventoryDataManagerImplTestIT.manager.getLotsByEntityType(EntityType.GERMPLSM.name(), 0,
				1).get(0), Integer.valueOf(20140518), 1, 300d, "sample added transaction 2", 0, null, null, null, 150d, 1, null));
		InventoryDataManagerImplTestIT.manager.addTransactions(transactions);
		Assert.assertNotNull(transactions.get(0).getId());
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, transactions);
	}

	@Test
	public void testUpdateTransaction() throws Exception {
		// this test assumes that there are existing records in the transaction table

		Transaction transaction = InventoryDataManagerImplTestIT.manager.getTransactionById(Integer.valueOf(1));
		Debug.println(MiddlewareIntegrationTest.INDENT, "BEFORE: " + transaction.toString());

		// Update comment
		String oldComment = transaction.getComments();
		String newComment = oldComment + " UPDATED " + (int) (Math.random() * 100);
		if (newComment.length() > 255) {
			newComment = newComment.substring(newComment.length() - 255);
		}
		transaction.setComments(newComment);

		// Invert status
		transaction.setStatus(transaction.getStatus() ^ 1);

		InventoryDataManagerImplTestIT.manager.updateTransaction(transaction);

		Assert.assertFalse(oldComment.equals(transaction.getComments()));
		Debug.println(MiddlewareIntegrationTest.INDENT, "AFTER: " + transaction.toString());
	}

	@Test
	public void testUpdateTransactions() throws Exception {
		// Assumption: There are more than 2 transactions of lot_id = 1
		List<Transaction> transactions = InventoryDataManagerImplTestIT.manager.getAllTransactions(0, 2);

		if (transactions.size() == 2) {
			Debug.println(MiddlewareIntegrationTest.INDENT, "BEFORE: ");
			Debug.printObjects(MiddlewareIntegrationTest.INDENT * 2, transactions);
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
			InventoryDataManagerImplTestIT.manager.updateTransactions(transactions);

			Assert.assertFalse(oldComment.equals(transactions.get(0).getComments()));
			Debug.println(MiddlewareIntegrationTest.INDENT, "AFTER: ");
			Debug.printObjects(MiddlewareIntegrationTest.INDENT * 2, transactions);
		} else {
			Debug.println(MiddlewareIntegrationTest.INDENT, "At least two TRANSACTION entries are required in this test");
		}
	}

	@Test
	public void testGetTransactionsByLotId() throws Exception {
		Set<Transaction> transactions = InventoryDataManagerImplTestIT.manager.getTransactionsByLotId(lotId);
		Assert.assertTrue(transactions != null);
		Assert.assertTrue(!transactions.isEmpty());
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetTransactionsByLotId(" + lotId + "): ");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, new ArrayList<Transaction>(transactions));
	}

	@Test
	public void testGetAllReserveTransactions() throws Exception {
		List<Transaction> transactions = InventoryDataManagerImplTestIT.manager.getAllReserveTransactions(0, 5);
		Assert.assertTrue(transactions != null);
		Assert.assertTrue(!transactions.isEmpty());
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, transactions);
	}

	@Test
	public void testCountAllReserveTransactions() throws Exception {
		Debug.println(MiddlewareIntegrationTest.INDENT,
				"countAllReserveTransactions(): " + InventoryDataManagerImplTestIT.manager.countAllReserveTransactions());
	}

	@Test
	public void testGetAllDepositTransactions() throws Exception {
		List<Transaction> transactions = InventoryDataManagerImplTestIT.manager.getAllDepositTransactions(0, 5);
		Assert.assertTrue(transactions != null);
		Assert.assertTrue(!transactions.isEmpty());
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetAllDepositTransactions(): ");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, transactions);
	}

	@Test
	public void testCountAllDepositTransactions() throws Exception {
		Debug.println(MiddlewareIntegrationTest.INDENT,
				"countAllDepositTransactions(): " + InventoryDataManagerImplTestIT.manager.countAllDepositTransactions());
	}

	@Test
	public void testGetAllReserveTransactionsByRequestor() throws Exception {
		Integer personId = Integer.valueOf(1);
		List<Transaction> transactions = InventoryDataManagerImplTestIT.manager.getAllReserveTransactionsByRequestor(personId, 0, 5);
		Assert.assertTrue(transactions != null);
		Assert.assertTrue(!transactions.isEmpty());
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetAllReserveTransactionsByRequestor(" + personId + "): ");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, transactions);
	}

	@Test
	public void testCountAllReserveTransactionsByRequestor() throws Exception {
		Integer personId = Integer.valueOf(253);
		Debug.println(MiddlewareIntegrationTest.INDENT, "countAllReserveTransactionsByRequestor(" + personId + "): "
				+ InventoryDataManagerImplTestIT.manager.countAllReserveTransactionsByRequestor(personId));
	}

	@Test
	public void testGetAllDepositTransactionsByDonor() throws Exception {
		Integer personId = Integer.valueOf(1);
		List<Transaction> transactions = InventoryDataManagerImplTestIT.manager.getAllDepositTransactionsByDonor(personId, 0, 5);
		Assert.assertTrue(transactions != null);
		Assert.assertTrue(!transactions.isEmpty());
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetAllDepositTransactionsByDonor(" + personId + "): ");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, transactions);
	}

	@Test
	public void testCountAllDepositTransactionsByDonor() throws Exception {
		Integer personId = Integer.valueOf(1);
		Debug.println(MiddlewareIntegrationTest.INDENT, "CountAllDepositTransactionsByDonor(" + personId + "): "
				+ InventoryDataManagerImplTestIT.manager.countAllDepositTransactionsByDonor(personId));
	}

	@Test
	public void testGenerateReportOnAllUncommittedTransactions() throws Exception {
		Debug.println(MiddlewareIntegrationTest.INDENT, "Number of uncommitted transactions [countAllUncommittedTransactions()]: "
				+ InventoryDataManagerImplTestIT.manager.countAllUncommittedTransactions());
		List<TransactionReportRow> report = InventoryDataManagerImplTestIT.manager.generateReportOnAllUncommittedTransactions(0, 5);
		Assert.assertTrue(report != null);
		Assert.assertTrue(!report.isEmpty());
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, report);
	}

	@Test
	public void testGenerateReportOnAllReserveTransactions() throws Exception {
		Debug.println(MiddlewareIntegrationTest.INDENT, "Number of reserved transactions [countAllReserveTransactions()]: "
				+ InventoryDataManagerImplTestIT.manager.countAllReserveTransactions());
		List<TransactionReportRow> report = InventoryDataManagerImplTestIT.manager.generateReportOnAllReserveTransactions(0, 5);
		Assert.assertTrue(report != null);
		Assert.assertTrue(!report.isEmpty());
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, report);
	}

	@Test
	public void testGenerateReportOnAllWithdrawalTransactions() throws Exception {
		Debug.println(MiddlewareIntegrationTest.INDENT, "Number of withdrawal transactions [countAllWithdrawalTransactions()]: "
				+ InventoryDataManagerImplTestIT.manager.countAllWithdrawalTransactions());
		List<TransactionReportRow> report = InventoryDataManagerImplTestIT.manager.generateReportOnAllWithdrawalTransactions(0, 5);
		Assert.assertTrue(report != null);
		Assert.assertTrue(!report.isEmpty());
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, report);
	}

	@Test
	public void testGenerateReportOnAllLots() throws Exception {
		Debug.println(MiddlewareIntegrationTest.INDENT, "Balance Report on All Lots");
		Debug.println(MiddlewareIntegrationTest.INDENT,
				"Number of lots [countAllLots()]: " + InventoryDataManagerImplTestIT.manager.countAllLots());
		List<LotReportRow> report = InventoryDataManagerImplTestIT.manager.generateReportOnAllLots(0, 10);
		Assert.assertTrue(report != null);
		Assert.assertTrue(!report.isEmpty());
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, report);
	}

	@Test
	public void testGenerateReportsOnDormantLots() throws Exception {
		int year = 2012;
		Debug.println(MiddlewareIntegrationTest.INDENT, "Balance Report on DORMANT Lots");
		List<LotReportRow> report = InventoryDataManagerImplTestIT.manager.generateReportOnDormantLots(year, 0, 10);
		Assert.assertTrue(report != null);
		Assert.assertTrue(!report.isEmpty());
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGenerateReportsOnDormantLots(year=" + year + ") REPORT: ");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, report);
	}

	@Test
	public void testGenerateReportOnLotsByEntityType() throws Exception {
		String type = EntityType.GERMPLSM.name();
		Debug.println(MiddlewareIntegrationTest.INDENT, "Balance Report on Lots by Entity Type: " + type);
		List<LotReportRow> report = InventoryDataManagerImplTestIT.manager.generateReportOnLotsByEntityType(type, 0, 10);
		Assert.assertTrue(report != null);
		Assert.assertTrue(!report.isEmpty());
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGenerateReportOnLotsByEntityType(" + type + ") REPORT: ");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, report);
	}

	@Test
	public void testGenerateReportOnLotsByEntityTypeAndEntityId() throws Exception {
		Debug.println(MiddlewareIntegrationTest.INDENT, "Balance Report on Lots by Entity Type and Entity ID:");
		String type = EntityType.GERMPLSM.name();
		List<Integer> entityIdList = new ArrayList<Integer>();
		entityIdList.add(50533);
		entityIdList.add(537652);

		List<LotReportRow> report =
				InventoryDataManagerImplTestIT.manager.generateReportOnLotsByEntityTypeAndEntityId(type, entityIdList, 0, 10);

		Assert.assertTrue(report != null);
		Assert.assertTrue(!report.isEmpty());
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGenerateReportOnLotsByEntityTypeAndEntityId(type=" + type + ", entityId="
				+ entityIdList + ") REPORT: ");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, report);
	}

	@Test
	public void testGenerateReportOnEmptyLot() throws Exception {
		Debug.println(MiddlewareIntegrationTest.INDENT, "Report on empty lot");
		List<LotReportRow> report = InventoryDataManagerImplTestIT.manager.generateReportOnEmptyLots(0, 2);
		Assert.assertTrue(report != null);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGenerateReportOnEmptyLot() REPORT: ");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, report);
	}

	@Test
	public void testGenerateReportOnLotWithMinimumAmount() throws Exception {
		long minimumAmount = 700;
		Debug.println(MiddlewareIntegrationTest.INDENT, "Report on lot with minimum balance");
		List<LotReportRow> report = InventoryDataManagerImplTestIT.manager.generateReportOnLotsWithMinimumAmount(minimumAmount, 0, 5);
		Assert.assertTrue(report != null);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGenerateReportOnLotWithMinimumAmount(minimumAmount=" + minimumAmount
				+ ") REPORT: ");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, report);
	}

	@Test
	public void testCountAllUncommittedTransactions() throws Exception {
		Debug.println(MiddlewareIntegrationTest.INDENT,
				"testCountAllUncommittedTransactions(): " + InventoryDataManagerImplTestIT.manager.countAllUncommittedTransactions());
	}

	@Test
	public void testCountAllWithdrawalTransactions() throws Exception {
		Debug.println(MiddlewareIntegrationTest.INDENT,
				"testCountAllWithdrawalTransactions(): " + InventoryDataManagerImplTestIT.manager.countAllWithdrawalTransactions());
	}

	@Test
	public void testCountAllLots() throws Exception {
		Debug.println(MiddlewareIntegrationTest.INDENT, "testCountAllLots(): " + InventoryDataManagerImplTestIT.manager.countAllLots());
	}

	@Test
	public void testGetAllLots() throws Exception {
		List<Lot> results = InventoryDataManagerImplTestIT.manager.getAllLots(0, Integer.MAX_VALUE);
		Assert.assertNotNull(results);
		Assert.assertTrue(!results.isEmpty());
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetAllLots(): ");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, results);
	}

	@Test
	public void testGetTransactionById() throws Exception {
		Integer id = 1;
		Transaction transactionid = InventoryDataManagerImplTestIT.manager.getTransactionById(id);
		Assert.assertNotNull(transactionid);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetTransactionById(" + id + "): ");
		Debug.println(transactionid.toString());
	}

	@Test
	public void testGetInventoryDetailsByGermplasmList() throws Exception {
		Integer listId = 1;
		List<InventoryDetails> result = InventoryDataManagerImplTestIT.manager.getInventoryDetailsByGermplasmList(listId);
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, result);
	}

	@Test
	public void testGetLotCountsForGermplasmList() throws MiddlewareQueryException {
		int listid = 1;
		List<GermplasmListData> listEntries = InventoryDataManagerImplTestIT.manager.getLotCountsForList(listid, 0, Integer.MAX_VALUE);
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
		List<GermplasmListData> listEntries = InventoryDataManagerImplTestIT.manager.getLotCountsForListEntries(listid, entryIds);
		for (GermplasmListData entry : listEntries) {
			ListDataInventory inventory = entry.getInventoryInfo();
			if (inventory != null) {
				System.out.println(inventory);
			}
		}
	}

	@Test
	public void testGetLotsForGermplasmListEntry() throws MiddlewareQueryException {
		List<ListEntryLotDetails> lots = InventoryDataManagerImplTestIT.manager.getLotDetailsForListEntry(-543041, -507029, -88175);
		for (ListEntryLotDetails lot : lots) {
			Debug.print(lot);
		}
	}

	@Test
	public void testGetLotsForGermplasmList() throws MiddlewareQueryException {
		List<GermplasmListData> listEntries = InventoryDataManagerImplTestIT.manager.getLotDetailsForList(-543041, 0, 500);
		for (GermplasmListData entry : listEntries) {
			Debug.print("Id=" + entry.getId() + ", GID = " + entry.getGid());
			Debug.print(3, entry.getInventoryInfo());
		}
	}

	@Test
	public void testGetLotCountsForGermplasm() throws MiddlewareQueryException {
		int gid = -644052;
		Integer count = InventoryDataManagerImplTestIT.manager.countLotsWithAvailableBalanceForGermplasm(gid);
		Debug.print("GID=" + gid + ", lotCount=" + count);
	}

	@Test
	public void testGetLotsForGermplasm() throws MiddlewareQueryException {
		int gid = 89;
		List<LotDetails> lots = InventoryDataManagerImplTestIT.manager.getLotDetailsForGermplasm(gid);
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
		InventoryDataManagerImplTestIT.manager.cancelReservedInventory(lotEntries);
	}

	@Test
	public void testGetStockIdsByListDataProjectListId() throws MiddlewareQueryException {
		List<String> stockIds = InventoryDataManagerImplTestIT.manager.getStockIdsByListDataProjectListId(17);
		Assert.assertNotNull(stockIds);
	}

	@Test
	public void testUpdateInventory() throws MiddlewareQueryException {
		Integer listId = 17;
		List<InventoryDetails> inventoryDetailList =
				InventoryDataManagerImplTestIT.inventoryService.getInventoryListByListDataProjectListId(listId, GermplasmListType.CROSSES);
		if (inventoryDetailList != null && !inventoryDetailList.isEmpty()) {
			InventoryDetails inventoryDetails = inventoryDetailList.get(0);
			Map<String, Object> originalData = this.getInventorySpecificDetails(inventoryDetails);
			this.modifyInventoryDetails(inventoryDetails);
			InventoryDataManagerImplTestIT.manager.updateInventory(listId, inventoryDetailList);
			InventoryDetails modifiedInventoryDetails =
					this.getModifiedInventoryDetails(originalData, InventoryDataManagerImplTestIT.inventoryService
							.getInventoryListByListDataProjectListId(listId, GermplasmListType.CROSSES));
			Assert.assertEquals(InventoryDataManagerImplTestIT.TEST_DUPLICATE, modifiedInventoryDetails.getDuplicate());
			Assert.assertEquals(InventoryDataManagerImplTestIT.TEST_BULK_WITH, modifiedInventoryDetails.getBulkWith());
			Assert.assertEquals(InventoryDataManagerImplTestIT.TEST_BULK_COMPL, modifiedInventoryDetails.getBulkCompl());
			Assert.assertEquals(InventoryDataManagerImplTestIT.TEST_LOCATION_ID, modifiedInventoryDetails.getLocationId().intValue());
			Assert.assertEquals(InventoryDataManagerImplTestIT.TEST_SCALE_ID, modifiedInventoryDetails.getScaleId().intValue());
			Assert.assertEquals(0, modifiedInventoryDetails.getAmount().compareTo(InventoryDataManagerImplTestIT.TEST_AMOUNT));
			Assert.assertEquals(InventoryDataManagerImplTestIT.TEST_COMMENT, modifiedInventoryDetails.getComment());
			this.revertChangesToInventoryDetails(inventoryDetails, originalData);
			InventoryDataManagerImplTestIT.manager.updateInventory(listId, inventoryDetailList);
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
		Assert.assertNotNull(InventoryDataManagerImplTestIT.manager.getLotById(1));
	}
}
