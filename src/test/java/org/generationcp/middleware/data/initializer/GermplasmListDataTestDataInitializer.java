
package org.generationcp.middleware.data.initializer;

import java.util.Arrays;

import org.generationcp.middleware.domain.inventory.ListDataInventory;
import org.generationcp.middleware.domain.inventory.ListEntryLotDetails;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;

public class GermplasmListDataTestDataInitializer {

	public static GermplasmListData createGermplasmListData(final GermplasmList germplasmList, final Integer gid,
			final Integer entryId) {
		final GermplasmListData germplasmListData = new GermplasmListData(null, germplasmList, gid, entryId,
				"SeedSource", "GroupName", 0, 99995);
		return germplasmListData;
	}

	public static GermplasmListData createGermplasmListData(final GermplasmList germplasmList, final Integer gid,
			final Integer entryId, final Integer groupId) {
		final GermplasmListData germplasmListData = GermplasmListDataTestDataInitializer.createGermplasmListData(germplasmList, gid, entryId);
		germplasmListData.setGroupId(groupId);
		return germplasmListData;
	}
	
	public static GermplasmListData createGermplasmListDataWithInventoryInfo(final GermplasmList germplasmList, final Integer gid,
			final Integer entryId) {
		final GermplasmListData germplasmListData = GermplasmListDataTestDataInitializer.createGermplasmListData(germplasmList, gid, entryId);
		final ListDataInventory listDataInventory = new ListDataInventory(1, 1);
		final ListEntryLotDetails listEntryLotDetails = new ListEntryLotDetails();
		listDataInventory.setLotRows(Arrays.asList(listEntryLotDetails));
		germplasmListData.setInventoryInfo(listDataInventory);
		return germplasmListData;
	}
}
