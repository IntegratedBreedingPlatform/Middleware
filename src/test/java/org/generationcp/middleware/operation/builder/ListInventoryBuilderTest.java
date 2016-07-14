
package org.generationcp.middleware.operation.builder;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.data.initializer.GermplasmListTestDataInitializer;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Ignore("Historic failing test. Disabled temporarily. Developers working in this area please spend some time to fix and remove @Ignore.")
public class ListInventoryBuilderTest extends IntegrationTestBase {

	private static final int LIST_ID = 1;
	private static final int GROUP_ID = 1;
	private static final int NO_OF_ENTRIES = 5;
	private static ListInventoryBuilder listInventoryBuilder;
	private static List<Integer> gids;

	@Autowired
	private GermplasmDataManager germplasmDataManager;
	

	@Before
	public void setUp() {
		listInventoryBuilder = new ListInventoryBuilder(this.sessionProvder);
		// initialize germplasm ids
		gids = this.createListOfGermplasmIds(NO_OF_ENTRIES);
		this.initializeGermplasms(gids);
	}

	@Test
	public void testRetrieveGroupId() {
		final GermplasmList germplasmList = GermplasmListTestDataInitializer.createGermplasmListWithListData(LIST_ID, NO_OF_ENTRIES);
		final List<GermplasmListData> listEntries = germplasmList.getListData();

		listInventoryBuilder.retrieveGroupId(listEntries, gids);

		for (final GermplasmListData listEntry : listEntries) {
			Assert.assertTrue("Expecting each list entry should have group id set to " + GROUP_ID + "but didn't.", listEntry.getGroupId()
					.equals(GROUP_ID));
		}
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
		for (int i = 1; i <= noOfEntries; i++) {
			gids.add(i);
		}
		return gids;
	}

}
