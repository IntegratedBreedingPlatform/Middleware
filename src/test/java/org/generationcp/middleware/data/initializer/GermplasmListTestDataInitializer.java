
package org.generationcp.middleware.data.initializer;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;

public class GermplasmListTestDataInitializer {

	public static List<GermplasmList> createGermplasmLists(final int numOfEntries) {
		final List<GermplasmList> germplasmLists = new ArrayList<GermplasmList>();

		for (int i = 0; i < numOfEntries; i++) {
			final Integer id = i + 1;
			final GermplasmList germplasmList = new GermplasmList();
			germplasmList.setId(id);
			germplasmList.setName("List " + id);
			germplasmList.setDescription("Description " + id);
			germplasmList.setListData(GermplasmListTestDataInitializer.getGermplasmListData(numOfEntries));

			germplasmLists.add(germplasmList);
		}

		return germplasmLists;
	}

	public static List<GermplasmList> createGermplasmListsWithType(final int numOfEntries) {
		final List<GermplasmList> germplasmLists = new ArrayList<GermplasmList>();

		for (int i = 0; i < numOfEntries; i++) {
			final Integer id = i + 1;
			final GermplasmList germplasmList = new GermplasmList();
			germplasmList.setId(id);
			germplasmList.setName("List " + id);
			germplasmList.setDescription("Description " + id);
			germplasmList.setListData(GermplasmListTestDataInitializer.getGermplasmListData(numOfEntries));
			germplasmList.setType("test" + id);
			germplasmLists.add(germplasmList);
		}

		return germplasmLists;
	}

	public static List<GermplasmListData> getGermplasmListData(final int numOfEntries) {
		final List<GermplasmListData> germplasmListData = new ArrayList<GermplasmListData>();

		for (int i = 0; i < numOfEntries; i++) {
			final Integer id = i + 1;
			final GermplasmListData listData = new GermplasmListData();
			listData.setId(id);
			listData.setDesignation("Designation" + id);
			listData.setEntryCode("EntryCode" + id);
			listData.setGroupName("GroupName" + id);

			germplasmListData.add(listData);
		}

		return germplasmListData;
	}
}
