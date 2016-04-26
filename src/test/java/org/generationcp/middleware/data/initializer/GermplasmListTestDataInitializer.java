
package org.generationcp.middleware.data.initializer;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.gms.GermplasmListType;
import org.generationcp.middleware.domain.inventory.ListDataInventory;
import org.generationcp.middleware.domain.inventory.LotDetails;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.ListDataProject;

public class GermplasmListTestDataInitializer {

	private static final int USER_ID = 1;

	public static GermplasmList createGermplasmList(final Integer id) {
		final GermplasmList germplasmList = new GermplasmList();
		germplasmList.setId(id);
		germplasmList.setName("List " + id);
		germplasmList.setDescription("List " + id + " Description");
		germplasmList.setDate(20150101L);
		germplasmList.setUserId(USER_ID);
		germplasmList.setType(GermplasmListType.LST.name());

		return germplasmList;
	}

	public static GermplasmList createGermplasmListWithListData(final int id, final int noOfEntries) {
		final GermplasmList germplasmList = createGermplasmList(id);
		germplasmList.setListData(createGermplasmListData(noOfEntries));

		return germplasmList;
	}

	/**
	 * Create germplasm list with list data and inventory info given the number of entries
	 * 
	 * @param id
	 * @param noOfEntries
	 * @return
	 */
	public static GermplasmList createGermplasmListWithListDataAndInventoryInfo(final Integer id, final int noOfEntries) {
		final GermplasmList germplasmList = createGermplasmList(id);
		germplasmList.setListData(createGermplasmListDataWithInventoryInfo(noOfEntries));
		return germplasmList;
	}

	/**
	 * Create germplasm list with list data and inventory info given the number of entries
	 * 
	 * @param id
	 * @param gids
	 * @return
	 */
	public static GermplasmList createGermplasmListWithListDataAndInventoryInfo(final Integer id, final List<Integer> gids) {
		final GermplasmList germplasmList = createGermplasmList(id);
		germplasmList.setListData(createGermplasmListDataWithInventoryInfo(gids));
		return germplasmList;
	}

	public static List<GermplasmListData> createGermplasmListData(final Integer itemNo) {
		final List<GermplasmListData> listEntries = new ArrayList<GermplasmListData>();
		for (int i = 1; i <= itemNo; i++) {
			final GermplasmListData listEntry = createGermplasmListDataItem(i);
			listEntries.add(listEntry);
		}

		return listEntries;
	}

	public static List<GermplasmListData> createGermplasmListDataWithInventoryInfo(final Integer itemNo) {
		final List<GermplasmListData> listEntries = new ArrayList<GermplasmListData>();
		for (int i = 1; i <= itemNo; i++) {
			final GermplasmListData listEntry = createGermplasmListDataItemWithInventoryInfo(i);
			listEntries.add(listEntry);
		}

		return listEntries;
	}

	private static List<GermplasmListData> createGermplasmListDataWithInventoryInfo(final List<Integer> gids) {
		final List<GermplasmListData> listEntries = new ArrayList<GermplasmListData>();
		for (final Integer gid : gids) {
			final GermplasmListData listEntry = createGermplasmListDataItemWithInventoryInfo(gid);
			listEntries.add(listEntry);
		}

		return listEntries;
	}

	public static List<ListDataProject> createListDataSnapShotFromListEntries(final GermplasmList germplasmList) {

		final List<GermplasmListData> listEntries = germplasmList.getListData();
		final List<ListDataProject> listDataProjectEntries = new ArrayList<ListDataProject>();

		for (final GermplasmListData listEntry : listEntries) {
			final ListDataProject listDataProjectEntry = new ListDataProject();

			listDataProjectEntry.setList(germplasmList);
			listDataProjectEntry.setGermplasmId(listEntry.getGermplasmId());
			listDataProjectEntry.setCheckType(0);
			listDataProjectEntry.setEntryId(listEntry.getEntryId());
			listDataProjectEntry.setEntryCode(listEntry.getEntryCode());
			listDataProjectEntry.setSeedSource(listEntry.getSeedSource());
			listDataProjectEntry.setDesignation(listEntry.getDesignation());
			listDataProjectEntry.setGroupName(listEntry.getGroupName());

			listDataProjectEntries.add(listDataProjectEntry);
		}

		return listDataProjectEntries;
	}

	protected static GermplasmListData createGermplasmListDataItem(final int i) {
		final GermplasmListData listEntry = new GermplasmListData();
		listEntry.setId(i);
		listEntry.setDesignation("Designation " + i);
		listEntry.setEntryCode("EntryCode " + i);
		listEntry.setEntryId(i);
		listEntry.setGroupName("GroupName " + i);
		listEntry.setStatus(1);
		listEntry.setSeedSource("SeedSource " + i);
		listEntry.setGid(i);
		// Default MGID(GROUP ID) is 0
		listEntry.setMgid(0);
		return listEntry;
	}

	public static List<GermplasmList> createGermplasmLists(final int numOfEntries) {
		final List<GermplasmList> germplasmLists = new ArrayList<GermplasmList>();

		for (int i = 0; i < numOfEntries; i++) {
			final Integer id = i + 1;
			germplasmLists.add(GermplasmListTestDataInitializer.createGermplasmListWithListData(id, numOfEntries));
		}
		return germplasmLists;
	}

	public static List<GermplasmList> createGermplasmListsWithType(final int numOfEntries) {
		final List<GermplasmList> germplasmLists = new ArrayList<GermplasmList>();

		for (int i = 0; i < numOfEntries; i++) {
			final Integer id = i + 1;
			final GermplasmList germplasmList = createGermplasmListWithListData(id, numOfEntries);
			germplasmList.setType("test" + id);
			germplasmLists.add(germplasmList);
		}

		return germplasmLists;
	}

	protected static GermplasmListData createGermplasmListDataItemWithInventoryInfo(final int i) {
		final GermplasmListData listEntry = new GermplasmListData();
		listEntry.setId(i);
		listEntry.setDesignation("Designation " + i);
		listEntry.setEntryCode("EntryCode " + i);
		listEntry.setEntryId(i);
		listEntry.setGroupName("GroupName " + i);
		listEntry.setStatus(1);
		listEntry.setSeedSource("SeedSource " + i);
		listEntry.setGid(i);
		listEntry.setInventoryInfo(createInventoryInfo(i, i, i));
		return listEntry;
	}

	protected static ListDataInventory createInventoryInfo(final int itemNo, final int listDataId, final int gid) {

		final ListDataInventory listDataInventory = new ListDataInventory(listDataId, gid);
		listDataInventory.setLotCount(0);
		listDataInventory.setActualInventoryLotCount(0);
		listDataInventory.setLotRows(new ArrayList<LotDetails>());
		listDataInventory.setReservedLotCount(0);
		listDataInventory.setStockIDs("SID:" + itemNo);

		return listDataInventory;
	}

}
