package org.generationcp.middleware.operation.builder;

import com.beust.jcommander.internal.Lists;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.data.initializer.GermplasmListTestDataInitializer;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.data.initializer.InventoryDetailsTestDataInitializer;
import org.generationcp.middleware.domain.inventory.GermplasmInventory;
import org.generationcp.middleware.domain.inventory.ListDataInventory;
import org.generationcp.middleware.domain.inventory.LotDetails;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.GermplasmListManagerImpl;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.generationcp.middleware.pojos.ims.TransactionType;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class ListInventoryBuilderTest extends IntegrationTestBase {

	private static final int LIST_ID = 1;
	private static final int GROUP_ID = 0;
	private static final int NO_OF_ENTRIES = 5;
	private static ListInventoryBuilder listInventoryBuilder;
	private static GermplasmListManager germplasmListManager;
	private static List<Integer> gids;
	private CropType cropType;

	@Autowired
	private GermplasmDataManager germplasmDataManager;

	@Autowired
	private InventoryDataManager inventoryDataManager;

	private GermplasmList germplasmList;

	InventoryDetailsTestDataInitializer inventoryDetailsTestDataInitializer;

	private DaoFactory daoFactory;

	@Before
	public void setUp() {
		if (this.daoFactory == null) {
			this.daoFactory = new DaoFactory(this.sessionProvder);
		}
		this.inventoryDetailsTestDataInitializer = new InventoryDetailsTestDataInitializer();
		listInventoryBuilder = new ListInventoryBuilder(this.sessionProvder);
		germplasmListManager = new GermplasmListManagerImpl(this.sessionProvder);
		this.cropType = new CropType();
		this.cropType.setUseUUID(false);
		gids = this.createListOfGermplasmIds(NO_OF_ENTRIES);
		this.initializeGermplasms(gids);
		this.germplasmList = GermplasmListTestDataInitializer.createGermplasmListWithListData(LIST_ID, NO_OF_ENTRIES, gids);
		germplasmListManager.addGermplasmList(this.germplasmList);
	}

	@Test
	public void testRetrieveGroupId() {
		final List<GermplasmListData> listEntries = this.germplasmList.getListData();

		listInventoryBuilder.retrieveGroupId(listEntries, gids);

		for (final GermplasmListData listEntry : listEntries) {
			Assert.assertTrue("Expecting each list entry should have group id set to " + GROUP_ID + "but didn't.",
					listEntry.getGroupId().equals(GROUP_ID));
		}
	}

	@Test
	public void testRetrieveInventoryLotsForGermplasm() {
		final List<GermplasmListData> listEntries = this.germplasmList.getListData();

		final List<Integer> listGid = this.retrieveGidFromListEntries(listEntries);

		final List<Lot> lots = this.inventoryDetailsTestDataInitializer.createLots(listGid, this.germplasmList.getId(), 8234, 9007);

		lots.forEach(l -> this.daoFactory.getLotDao().save(l));

		final Transaction initialTransaction = this.inventoryDetailsTestDataInitializer
			.createTransaction(5.0, 1, "Initial inventory", lots.get(0), 1, this.germplasmList.getId(),
				listEntries.get(0).getId(), "LIST", TransactionType.DEPOSIT.getId());

		final Transaction reservationTransaction = this.inventoryDetailsTestDataInitializer
			.createTransaction(-2.0, 0, "2 reserved", lots.get(0), 1, this.germplasmList.getId(), listEntries.get(0).getId(),
				"LIST", TransactionType.WITHDRAWAL.getId());

		this.daoFactory.getTransactionDAO().save(initialTransaction);
		this.daoFactory.getTransactionDAO().save(reservationTransaction);
		final List<LotDetails> lotDetails = listInventoryBuilder.retrieveInventoryLotsForGermplasm(listGid.get(0));

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
		this.daoFactory.getGermplasmDao().save(germplasm);
		this.daoFactory.getGermplasmDao().refresh(germplasm);
		final Name name = germplasm.getPreferredName();
		name.setGermplasm(germplasm);
		this.daoFactory.getNameDao().save(name);
		final Integer germplasmId = this.germplasmDataManager.addGermplasm(germplasm, germplasm.getPreferredName(), this.cropType);
		germplasm.setInventoryInfo(new GermplasmInventory(germplasmId));

		final Lot lot = InventoryDetailsTestDataInitializer.createLot(1, "GERMPLSM", germplasmId, 1, 8264, 0, 1, "Comments", "InventoryId");
		this.daoFactory.getLotDao().save(lot);

		final Transaction transaction = InventoryDetailsTestDataInitializer
				.createTransaction(2.0, 0, TransactionType.DEPOSIT.getValue(), lot, 1, 1, 1, "LIST", TransactionType.DEPOSIT.getId());
		this.daoFactory.getTransactionDAO().save(transaction);

		final List<Germplasm> germplasmList = Lists.newArrayList(germplasm);
		listInventoryBuilder.setAvailableBalanceScaleForGermplasm(germplasmList);

		Assert.assertEquals(1, germplasmList.size());

		Assert.assertEquals("g", germplasmList.get(0).getInventoryInfo().getScaleForGermplsm());
	}

	private void initializeGermplasms(final List<Integer> gids) {
		for (final Integer gid : gids) {
			final Germplasm germplasm = GermplasmTestDataInitializer.createGermplasm(gid);
			germplasm.setMgid(GROUP_ID);
			this.germplasmDataManager.addGermplasm(germplasm, germplasm.getPreferredName(), this.cropType);
		}
	}

	private List<Integer> createListOfGermplasmIds(final int noOfEntries) {
		final List<Integer> gids = new ArrayList<Integer>();
		final Integer randomNumber = Integer.MIN_VALUE;
		for (int i = 1; i <= noOfEntries; i++) {
			gids.add(randomNumber + i);
		}
		return gids;
	}

	private List<Integer> retrieveGidFromListEntries(final List<GermplasmListData> listEntries) {
		final List<Integer> listGid = Lists.newArrayList();

		for (final GermplasmListData germplasmListData : listEntries) {
			listGid.add(germplasmListData.getGid());
			germplasmListData.setInventoryInfo(new ListDataInventory(germplasmListData.getId(), germplasmListData.getGid()));
		}
		return listGid;
	}

	private List<Integer> retrieveEntryIdFromListEntries(final List<GermplasmListData> listEntries) {
		final List<Integer> listEntryId = Lists.newArrayList();
		for (final GermplasmListData germplasmListData : listEntries) {
			listEntryId.add(germplasmListData.getId());
			germplasmListData.setInventoryInfo(new ListDataInventory(germplasmListData.getId(), germplasmListData.getGid()));
		}
		return listEntryId;
	}

}
