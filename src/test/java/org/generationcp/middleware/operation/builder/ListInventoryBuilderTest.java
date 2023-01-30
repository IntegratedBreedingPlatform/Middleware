package org.generationcp.middleware.operation.builder;

import com.beust.jcommander.internal.Lists;
import org.generationcp.middleware.GermplasmTestDataGenerator;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.data.initializer.GermplasmListTestDataInitializer;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.data.initializer.InventoryDetailsTestDataInitializer;
import org.generationcp.middleware.domain.inventory.ListDataInventory;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.GermplasmListManagerImpl;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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

	private GermplasmList germplasmList;


	private DaoFactory daoFactory;

	private GermplasmTestDataGenerator germplasmTestDataGenerator;

	@Before
	public void setUp() {
		if (this.daoFactory == null) {
			this.daoFactory = new DaoFactory(this.sessionProvder);
		}
		this.germplasmTestDataGenerator = new GermplasmTestDataGenerator(this.sessionProvder, daoFactory);
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

	private void initializeGermplasms(final List<Integer> gids) {
		for (final Integer gid : gids) {
			final Germplasm germplasm = GermplasmTestDataInitializer.createGermplasm(gid);
			germplasm.setMgid(GROUP_ID);
			this.germplasmTestDataGenerator.addGermplasm(germplasm, germplasm.getPreferredName(), this.cropType);
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
