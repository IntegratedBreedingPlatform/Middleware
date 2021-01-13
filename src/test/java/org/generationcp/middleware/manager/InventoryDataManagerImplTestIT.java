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

import com.google.common.collect.Lists;
import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.data.initializer.GermplasmListDataTestDataInitializer;
import org.generationcp.middleware.data.initializer.GermplasmListTestDataInitializer;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.data.initializer.InventoryDetailsTestDataInitializer;
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
import org.generationcp.middleware.pojos.ims.Transaction;
import org.generationcp.middleware.pojos.ims.TransactionStatus;
import org.generationcp.middleware.pojos.ims.TransactionType;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.utils.test.Debug;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InventoryDataManagerImplTestIT extends IntegrationTestBase {

	@Autowired
	private InventoryDataManager manager;

	@Autowired
	private GermplasmDataManager germplasmDataManager;

	@Autowired
	private GermplasmListManager germplasmListManager;

	GermplasmListTestDataInitializer germplasmListTestDataInitializer;

	private Integer lotId;
	private Lot lot;
	private CropType cropType;

	@Before
	public void setUpBefore() {
		final List<Lot> lots = new ArrayList<Lot>();
		this.lot = new Lot(null, 1, EntityType.GERMPLSM.name(), 50533, 9001, 1538, 0, 0, "sample added lot 1", RandomStringUtils.randomAlphabetic(35));
		lots.add(this.lot);
		lots.add(new Lot(null, 1, EntityType.GERMPLSM.name(), 50533, 9002, 1539, 0, 0, "sample added lot 2", RandomStringUtils.randomAlphabetic(35)));
		final List<Integer> idList = this.manager.addLots(lots);
		this.lotId = idList.get(0);
		this.lot.setId(this.lotId);

		final List<Transaction> transactions = new ArrayList<Transaction>();
		transactions.add(new Transaction(null, 1, this.manager.getLotsByEntityType(EntityType.GERMPLSM.name(), 0, 1).get(0), new Date((20140413)), 1, -1d, "sample added transaction 1", 0, null, null, null, 1, TransactionType.DEPOSIT.getId()));
		transactions.add(new Transaction(null, 1, this.manager.getLotsByEntityType(EntityType.GERMPLSM.name(), 0, 1).get(0), new Date((20140518)), 1, -2d, "sample added transaction 2", 0, null, null, null, 1, TransactionType.DEPOSIT.getId()));
		transactions.add(new Transaction(null, 1, this.manager.getLotsByEntityType(EntityType.GERMPLSM.name(), 0, 1).get(0), new Date((20140518)), 0, -2d, "sample added transaction 2", 0, null, null, null, 1, TransactionType.DEPOSIT.getId()));
		transactions.add(new Transaction(null, 1, this.manager.getLotsByEntityType(EntityType.GERMPLSM.name(), 0, 1).get(0), new Date((20140518)), 0, 2d, "sample added transaction 2", 0, null, null, null, 1, TransactionType.DEPOSIT.getId()));
		transactions.add(new Transaction(null, 1, this.lot, new Date((20140413)), 1, -1d, "sample added transaction 1", 0, null, null, null, 1, TransactionType.DEPOSIT.getId()));
		transactions.add(new Transaction(null, 1, this.manager.getLotsByEntityType(EntityType.GERMPLSM.name(), 0, 1).get(0), new Date((20120518)), 1, 2d, "sample added transaction 2", 0, null, null, null, 1, TransactionType.DEPOSIT.getId()));
		final Set<Transaction> transactionSet = new HashSet<>();
		transactionSet.add(transactions.get(4));
		this.lot.setTransactions(transactionSet);
		this.manager.addTransactions(transactions);

		this.germplasmListTestDataInitializer = new GermplasmListTestDataInitializer();

		this.cropType = new CropType();
		this.cropType.setUseUUID(false);
	}

	@Test
	public void testAddLot() {
		final Lot lot = new Lot(null, 1, EntityType.GERMPLSM.name(), 50533, 9001, 6088, 0, 0, "sample added lot", RandomStringUtils.randomAlphabetic(35));
		this.manager.addLot(lot);
		Assert.assertNotNull(lot.getId());
		Debug.println(IntegrationTestBase.INDENT, "Added: " + lot.toString());
	}

	@Test
	public void testAddLots() {
		final List<Lot> lots = new ArrayList<Lot>();
		lots.add(new Lot(null, 1, EntityType.GERMPLSM.name(), 50533, 9001, 1538, 0, 0, "sample added lot 1", RandomStringUtils.randomAlphabetic(35)));
		lots.add(new Lot(null, 1, EntityType.GERMPLSM.name(), 50533, 9002, 1539, 0, 0, "sample added lot 2", RandomStringUtils.randomAlphabetic(35)));
		final List<Integer> idList = this.manager.addLots(lots);

		Assert.assertFalse(idList.isEmpty());
		Debug.println(IntegrationTestBase.INDENT, "Added: ");
		Debug.printObjects(IntegrationTestBase.INDENT * 2, lots);
	}

	@Test
	public void testAddTransactions() {
		// this test assumes there are existing lot records with entity type = GERMPLSM
		final List<Transaction> transactions = new ArrayList<Transaction>();
		transactions.add(new Transaction(null, 1, this.manager.getLotsByEntityType(EntityType.GERMPLSM.name(), 0, 1).get(0), new Date((20140413)), 1, 200d, "sample added transaction 1", 0, null, null, null, 1, TransactionType.DEPOSIT.getId()));
		transactions.add(new Transaction(null, 1, this.manager.getLotsByEntityType(EntityType.GERMPLSM.name(), 0, 1).get(0), new Date((20140518)), 1, 300d, "sample added transaction 2", 0, null, null, null, 1, TransactionType.DEPOSIT.getId()));
		this.manager.addTransactions(transactions);
		Assert.assertNotNull(transactions.get(0).getId());
		Debug.printObjects(IntegrationTestBase.INDENT, transactions);
	}

	@Test
	public void testUpdateTransaction() {
		// this test assumes that there are existing records in the transaction table

		final Transaction transaction = this.manager.getTransactionById(Integer.valueOf(1));
		Debug.println(IntegrationTestBase.INDENT, "BEFORE: " + transaction.toString());

		// Update comment
		final String oldComment = transaction.getComments();
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
	public void testUpdateTransactions() {
		// Assumption: There are more than 2 transactions of lot_id = 1
		final List<Transaction> transactions = this.manager.getAllTransactions(0, 2);

		if (transactions.size() == 2) {
			Debug.println(IntegrationTestBase.INDENT, "BEFORE: ");
			Debug.printObjects(IntegrationTestBase.INDENT * 2, transactions);
			final String oldComment = transactions.get(0).getComments();

			for (final Transaction transaction : transactions) {
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
	public void testGetTransactionsByLotId() {
		final Set<Transaction> transactions = this.manager.getTransactionsByLotId(this.lotId);
		Assert.assertTrue(transactions != null);
		Assert.assertTrue(!transactions.isEmpty());
		Debug.println(IntegrationTestBase.INDENT, "testGetTransactionsByLotId(" + this.lotId + "): ");
		Debug.printObjects(IntegrationTestBase.INDENT, new ArrayList<Transaction>(transactions));
	}

	@Test
	public void testGetTransactionById() {
		final Integer id = 1;
		final Transaction transactionid = this.manager.getTransactionById(id);
		Assert.assertNotNull(transactionid);
		Debug.println(IntegrationTestBase.INDENT, "testGetTransactionById(" + id + "): ");
		Debug.println(transactionid.toString());
	}

	@Test
	public void testGetLotCountsForGermplasmList() throws MiddlewareQueryException {
		final int listid = 1;
		final List<GermplasmListData> listEntries = this.manager.getLotCountsForList(listid, 0, Integer.MAX_VALUE);
		for (final GermplasmListData entry : listEntries) {
			final ListDataInventory inventory = entry.getInventoryInfo();
			if (inventory != null) {
				System.out.println(inventory);
			}
		}
	}

	@Test
	public void testGetLotCountsForGermplasmListEntries() throws MiddlewareQueryException {
		final int listid = 1;
		final List<Integer> entryIds = new ArrayList<Integer>();
		entryIds.add(1);
		entryIds.add(2);
		entryIds.add(3);
		final List<GermplasmListData> listEntries = this.manager.getLotCountsForListEntries(entryIds);
		for (final GermplasmListData entry : listEntries) {
			final ListDataInventory inventory = entry.getInventoryInfo();
			if (inventory != null) {
				System.out.println(inventory);
			}
		}
	}

	@Test
	public void testGetLotsForGermplasmListEntry() throws MiddlewareQueryException {
		final List<ListEntryLotDetails> lots = this.manager.getLotDetailsForListEntry(-543041, -507029, -88175);
		for (final ListEntryLotDetails lot : lots) {
			Debug.print(lot);
		}
	}

	@Test
	public void testGetLotCountsForGermplasm() throws MiddlewareQueryException {
		final int gid = -644052;
		final Integer count = this.manager.countLotsWithAvailableBalanceForGermplasm(gid);
		Debug.print("GID=" + gid + ", lotCount=" + count);
	}

	@Test
	public void testGetLotsForGermplasm() throws MiddlewareQueryException {
		final int gid = 89;
		final List<LotDetails> lots = this.manager.getLotDetailsForGermplasm(gid);
		for (final LotDetails lot : lots) {
			System.out.println(lot);
		}
	}


	@Test
	public void testGetLotById() throws MiddlewareQueryException {
		Assert.assertNotNull(this.manager.getLotById(1));
	}

	@Test
	public void testGetAvailableBalanceForGermplasms() {
		final Germplasm germplasm = GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0 , 1 ,1 ,0, 1 ,1 , "MethodName",
				"LocationName");
		final Integer germplasmId = this.germplasmDataManager.addGermplasm(germplasm, germplasm.getPreferredName(), this.cropType);

		final Lot lot = InventoryDetailsTestDataInitializer.createLot(1, "GERMPLSM", germplasmId, 1, 8264, 0, 1, "Comments", "InventoryId");
		this.manager.addLots(Lists.<Lot>newArrayList(lot));

		final Transaction transaction = InventoryDetailsTestDataInitializer
			.createTransaction(
				2.0, 0, TransactionType.DEPOSIT.getValue(), lot, 1, 1, 1, "LIST", TransactionType.DEPOSIT.getId());
		this.manager.addTransactions(Lists.<Transaction>newArrayList(transaction));

		final List<Germplasm> availableBalanceForGermplasms =
				this.manager.getAvailableBalanceForGermplasms(Lists.<Germplasm>newArrayList(germplasm));

		Assert.assertEquals(1, availableBalanceForGermplasms.size());
		Assert.assertEquals(1, availableBalanceForGermplasms.get(0).getInventoryInfo().getActualInventoryLotCount().intValue());
		Assert.assertEquals("2.0", availableBalanceForGermplasms.get(0).getInventoryInfo().getTotalAvailableBalance().toString());
		Assert.assertEquals("g", availableBalanceForGermplasms.get(0).getInventoryInfo().getScaleForGermplsm());
	}

	@Test
	public void testRetrieveStockIds() {
		final Germplasm germplasm = GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0 , 1 ,1 ,0, 1 ,1 , "MethodName",
				"LocationName");
		final Integer germplasmId = this.germplasmDataManager.addGermplasm(germplasm, germplasm.getPreferredName(), this.cropType);


		final GermplasmList germplasmList = this.germplasmListTestDataInitializer.createGermplasmList(
				"GermplasmList", Integer.valueOf(1),
				"GermplasmList" + " Desc", null, 1, "programUUID");
		this.germplasmListManager.addGermplasmList(germplasmList);

		final GermplasmListData germplasmListData =
				GermplasmListDataTestDataInitializer.createGermplasmListData(germplasmList, germplasmId, 2);
		this.germplasmListManager.addGermplasmListData(germplasmListData);

		final Lot lotOne = InventoryDetailsTestDataInitializer.createLot(1, "GERMPLSM", germplasmId, 1, 8264, 0, 1, "First Lot for Gemrplasm",
			"InventoryId");
		final Lot lotTwo = InventoryDetailsTestDataInitializer.createLot(1, "GERMPLSM", germplasmId, 1, 8264, 0, 1, "Second Lot for Gemrplasm",
			"InventoryId");
		this.manager.addLots(Lists.newArrayList(lotOne, lotTwo));


		final Transaction depositTransactionForLotOne =  InventoryDetailsTestDataInitializer
				.createTransaction(5.0, TransactionStatus.PENDING.getIntValue(), TransactionType.DEPOSIT.getValue(), lotOne, 1, 1, germplasmListData.getId(), "LIST", TransactionType.DEPOSIT.getId());
		final Transaction depositTransactionForLotTwo =  InventoryDetailsTestDataInitializer
				.createTransaction(5.0, TransactionStatus.PENDING.getIntValue(), TransactionType.DEPOSIT.getValue(), lotTwo, 1, 1, germplasmListData.getId(), "LIST", TransactionType.DEPOSIT.getId());
		this.manager.addTransactions(Lists.<Transaction>newArrayList(depositTransactionForLotOne, depositTransactionForLotTwo));


		final Map<Integer, String> germplsmWiseStockID = this.manager.retrieveStockIds(Lists.newArrayList(germplasmId));

		Assert.assertNotNull(germplsmWiseStockID);
		Assert.assertEquals(depositTransactionForLotOne.getLot().getStockId() + ", " + depositTransactionForLotTwo.getLot().getStockId()
			, germplsmWiseStockID.get(germplasmId));
	}
}
