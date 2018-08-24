package org.generationcp.middleware.operation.builder;

import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.internal.Lists;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.data.initializer.GermplasmListTestDataInitializer;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.data.initializer.InventoryDetailsTestDataInitializer;
import org.generationcp.middleware.domain.inventory.GermplasmInventory;
import org.generationcp.middleware.domain.inventory.ListDataInventory;
import org.generationcp.middleware.domain.inventory.LotDetails;
import org.generationcp.middleware.manager.GermplasmListManagerImpl;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ListInventoryBuilderTest extends IntegrationTestBase {

	private static final int LIST_ID = 1;
	private static final int GROUP_ID = 0;
	private static final int NO_OF_ENTRIES = 5;
	private static ListInventoryBuilder listInventoryBuilder;
	private static GermplasmListManager germplasmListManager;
	private static List<Integer> gids;

	@Autowired
	private GermplasmDataManager germplasmDataManager;

	@Autowired
	private InventoryDataManager inventoryDataManager;

	private GermplasmList germplasmList;

	InventoryDetailsTestDataInitializer inventoryDetailsTestDataInitializer;

	@Before
	public void setUp() {
		inventoryDetailsTestDataInitializer = new InventoryDetailsTestDataInitializer();
		listInventoryBuilder = new ListInventoryBuilder(this.sessionProvder);
		germplasmListManager = new GermplasmListManagerImpl(this.sessionProvder);
		gids = this.createListOfGermplasmIds(NO_OF_ENTRIES);
		this.initializeGermplasms(gids);
		germplasmList = GermplasmListTestDataInitializer.createGermplasmListWithListData(LIST_ID, NO_OF_ENTRIES, gids);
		germplasmListManager.addGermplasmList(germplasmList);
	}

	@Test
	public void testRetrieveGroupId() {
		final List<GermplasmListData> listEntries = germplasmList.getListData();

		listInventoryBuilder.retrieveGroupId(listEntries, gids);

		for (final GermplasmListData listEntry : listEntries) {
			Assert.assertTrue("Expecting each list entry should have group id set to " + GROUP_ID + "but didn't.",
					listEntry.getGroupId().equals(GROUP_ID));
		}
	}

	@Test
	public void testRetrieveWithdrawalBalance() {
		final List<GermplasmListData> listEntries = germplasmList.getListData();

		final List<Integer> listEntryId = this.retrieveEntryIdFromListEntries(listEntries);
		final List<Integer> listGid = this.retrieveGidFromListEntries(listEntries);

		final List<Lot> lots = inventoryDetailsTestDataInitializer.createLots(listGid, germplasmList.getId(), 8234, 9007);

		this.inventoryDataManager.addLots(lots);

		Transaction reservationTransaction = inventoryDetailsTestDataInitializer
				.createReservationTransaction(-2.0, 0, "2 reserved", lots.get(0), 1, germplasmList.getId(), listEntries.get(0).getId(),
						"LIST");

		this.inventoryDataManager.addTransaction(reservationTransaction);

		listInventoryBuilder.retrieveWithdrawalBalance(listEntries, listEntryId);

		GermplasmListData germplasmListData = null;
		ListDataInventory inventoryInfo = null;

		germplasmListData = listEntries.get(0);
		inventoryInfo = germplasmListData.getInventoryInfo();

		Assert.assertEquals("2.0", inventoryInfo.getWithdrawalBalance().toString());
		Assert.assertEquals(1, inventoryInfo.getDistinctCountWithdrawalScale().intValue());
		Assert.assertEquals(8234, inventoryInfo.getWithdrawalScaleId().intValue());

		germplasmListData = listEntries.get(1);
		inventoryInfo = germplasmListData.getInventoryInfo();

		Assert.assertEquals("0.0", inventoryInfo.getWithdrawalBalance().toString());
		Assert.assertEquals(0, inventoryInfo.getDistinctCountWithdrawalScale().intValue());
		Assert.assertNull(inventoryInfo.getWithdrawalScaleId());
		Assert.assertNull(inventoryInfo.getWithdrawalScale());

	}

	@Test
	public void testRetrieveWithdrawalStatusWithReservedTransaction() {
		final List<GermplasmListData> listEntries = germplasmList.getListData();

		final List<Integer> listGid = this.retrieveGidFromListEntries(listEntries);

		final List<Lot> lots = inventoryDetailsTestDataInitializer.createLots(listGid, germplasmList.getId(), 8234, 9007);

		this.inventoryDataManager.addLots(lots);

		Transaction reservationTransaction = inventoryDetailsTestDataInitializer
				.createReservationTransaction(-2.0, 0, "2 reserved", lots.get(0), 1, germplasmList.getId(), listEntries.get(0).getId(),
						"LIST");

		this.inventoryDataManager.addTransaction(reservationTransaction);

		listInventoryBuilder.retrieveWithdrawalStatus(listEntries, listGid);

		GermplasmListData germplasmListData = null;
		ListDataInventory inventoryInfo = null;

		germplasmListData = listEntries.get(0);
		inventoryInfo = germplasmListData.getInventoryInfo();
		Assert.assertEquals(ListDataInventory.RESERVED, inventoryInfo.getTransactionStatus());

		germplasmListData = listEntries.get(1);
		inventoryInfo = germplasmListData.getInventoryInfo();
		Assert.assertEquals("", inventoryInfo.getTransactionStatus());

	}

	@Test
	public void testRetrieveLotCountsForList() {
		final List<GermplasmListData> listEntries = germplasmList.getListData();

		final List<Integer> listGid = this.retrieveGidFromListEntries(listEntries);

		final List<Lot> lots = inventoryDetailsTestDataInitializer.createLots(listGid, germplasmList.getId(), 8234, 9007);

		this.inventoryDataManager.addLots(lots);

		Transaction reservationTransaction = inventoryDetailsTestDataInitializer
				.createReservationTransaction(-2.0, 0, "2 reserved", lots.get(0), 1, germplasmList.getId(), listEntries.get(0).getId(),
						"LIST");

		this.inventoryDataManager.addTransaction(reservationTransaction);

		listInventoryBuilder.retrieveLotCountsForList(listEntries);

		GermplasmListData germplasmListData = null;
		ListDataInventory inventoryInfo = null;
		germplasmListData = listEntries.get(0);
		inventoryInfo = germplasmListData.getInventoryInfo();
		Assert.assertEquals(1, inventoryInfo.getReservedLotCount().intValue());
		Assert.assertEquals("2.0", inventoryInfo.getWithdrawalBalance().toString());
		Assert.assertEquals(8234, inventoryInfo.getWithdrawalScaleId().intValue());
		Assert.assertEquals(ListDataInventory.RESERVED, inventoryInfo.getTransactionStatus());

	}

	@Test
	public void testRetrieveInventoryLotsForGermplasm() {
		final List<GermplasmListData> listEntries = germplasmList.getListData();

		final List<Integer> listGid = this.retrieveGidFromListEntries(listEntries);

		final List<Lot> lots = inventoryDetailsTestDataInitializer.createLots(listGid, germplasmList.getId(), 8234, 9007);

		this.inventoryDataManager.addLots(lots);

		Transaction initialTransaction = inventoryDetailsTestDataInitializer
				.createReservationTransaction(5.0, 0, "Initial inventory", lots.get(0), 1, germplasmList.getId(),
						listEntries.get(0).getId(), "LIST");

		Transaction reservationTransaction = inventoryDetailsTestDataInitializer
				.createReservationTransaction(-2.0, 0, "2 reserved", lots.get(0), 1, germplasmList.getId(), listEntries.get(0).getId(),
						"LIST");

		this.inventoryDataManager.addTransaction(initialTransaction);
		this.inventoryDataManager.addTransaction(reservationTransaction);

		List<LotDetails> lotDetails = listInventoryBuilder.retrieveInventoryLotsForGermplasm(listGid.get(0));

		Assert.assertEquals(1, lotDetails.size());
		Assert.assertEquals("5.0", lotDetails.get(0).getActualLotBalance().toString());
		Assert.assertEquals("3.0", lotDetails.get(0).getAvailableLotBalance().toString());
		Assert.assertEquals("2.0", lotDetails.get(0).getReservedTotal().toString());
		Assert.assertEquals("2.0", lotDetails.get(0).getWithdrawalBalance().toString());
		Assert.assertEquals(ListDataInventory.RESERVED, lotDetails.get(0).getWithdrawalStatus());
		Assert.assertEquals(8234, lotDetails.get(0).getScaleId().intValue());
		Assert.assertEquals(9007, lotDetails.get(0).getLocId().intValue());
	}

	@Test
	public void testSetAvailableBalanceScaleForGermplasm() throws Exception {
		final Germplasm germplasm = GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0 , 1 ,1 ,0, 1 ,1 , "MethodName",
				"LocationName");
		final Integer germplasmId = this.germplasmDataManager.addGermplasm(germplasm, germplasm.getPreferredName());
		germplasm.setInventoryInfo(new GermplasmInventory(germplasmId));

		Lot lot = InventoryDetailsTestDataInitializer.createLot(1, "GERMPLSM", germplasmId, 1, 8264, 0, 1, "Comments");
		this.inventoryDataManager.addLots(com.google.common.collect.Lists.<Lot>newArrayList(lot));

		Transaction transaction = InventoryDetailsTestDataInitializer
				.createReservationTransaction(2.0, 0, "Deposit", lot, 1, 1, 1, "LIST");
		this.inventoryDataManager.addTransactions(com.google.common.collect.Lists.<Transaction>newArrayList(transaction));

		List<Germplasm> germplasmList = Lists.newArrayList(germplasm);
		listInventoryBuilder.setAvailableBalanceScaleForGermplasm(germplasmList);

		Assert.assertEquals(1, germplasmList.size());

		Assert.assertEquals("g", germplasmList.get(0).getInventoryInfo().getScaleForGermplsm());
	}

	private void initializeGermplasms(final List<Integer> gids) {
		for (final Integer gid : gids) {
			final Germplasm germplasm = GermplasmTestDataInitializer.createGermplasm(gid);
			germplasm.setMgid(GROUP_ID);
			this.germplasmDataManager.addGermplasm(germplasm, germplasm.getPreferredName());
		}
	}

	private List<Integer> createListOfGermplasmIds(final int noOfEntries) {
		final List<Integer> gids = new ArrayList<Integer>();
		Integer randomNumber = Integer.MIN_VALUE;
		for (int i = 1; i <= noOfEntries; i++) {
			gids.add(randomNumber + i);
		}
		return gids;
	}

	private List<Integer> retrieveGidFromListEntries(List<GermplasmListData> listEntries) {
		List<Integer> listGid = Lists.newArrayList();

		for (final GermplasmListData germplasmListData : listEntries) {
			listGid.add(germplasmListData.getGid());
			germplasmListData.setInventoryInfo(new ListDataInventory(germplasmListData.getId(), germplasmListData.getGid()));
		}
		return listGid;
	}

	private List<Integer> retrieveEntryIdFromListEntries(List<GermplasmListData> listEntries) {
		List<Integer> listEntryId = Lists.newArrayList();
		for (final GermplasmListData germplasmListData : listEntries) {
			listEntryId.add(germplasmListData.getId());
			germplasmListData.setInventoryInfo(new ListDataInventory(germplasmListData.getId(), germplasmListData.getGid()));
		}
		return listEntryId;
	}

	private List<Integer> retrieveRecordIdFromListEntries(List<GermplasmListData> listEntries) {
		List<Integer> listRecordId = Lists.newArrayList();
		for (final GermplasmListData germplasmListData : listEntries) {
			germplasmListData.setInventoryInfo(new ListDataInventory(germplasmListData.getId(), germplasmListData.getGid()));
			listRecordId.add(germplasmListData.getId());
		}
		return listRecordId;
	}

}
