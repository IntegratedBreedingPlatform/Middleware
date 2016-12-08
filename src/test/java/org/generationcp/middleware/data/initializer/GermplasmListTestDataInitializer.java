package org.generationcp.middleware.data.initializer;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.gms.GermplasmListType;
import org.generationcp.middleware.domain.inventory.ListDataInventory;
import org.generationcp.middleware.domain.inventory.LotDetails;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.util.Util;

import org.generationcp.middleware.pojos.ListDataProject;

public class GermplasmListTestDataInitializer {

	private static final int USER_ID = 1;

	/**
	 * create GermplasmList object with basic list details initialized
	 *
	 * @param id
	 * @return
	 */
	public static GermplasmList createGermplasmList(final Integer id) {
		return createGermplasmList(id, true);
	}

	public GermplasmList createGermplasmListWithType(final Integer id, final String type) {
		GermplasmList germplasmList = createGermplasmList(id);
		germplasmList.setType(type);
		return germplasmList;
	}

	/**
	 * create GermplasmList object with basic list details initialized given the user has an option to set the default id or not
	 *
	 * @param id
	 * @param setDefaultId - allow to set default id to germplasm list object
	 * @return
	 */
	public static GermplasmList createGermplasmList(final Integer id, final boolean setDefaultId) {
		final GermplasmList germplasmList = new GermplasmList();
		germplasmList.setId(setDefaultId ? id : null);
		germplasmList.setName("List " + id);
		germplasmList.setDescription("List " + id + " Description");
		germplasmList.setDate(20150101L);
		germplasmList.setUserId(USER_ID);
		germplasmList.setType(GermplasmListType.LST.name());
		germplasmList.setStatus(1);
		germplasmList.setNotes("Some notes here");
		return germplasmList;
	}

	public GermplasmList createGermplasmList(final String name, final Integer userId, final String description, final GermplasmList parent,
			final Integer status, final String programUUID) {
		final GermplasmList germplasmList =
				new GermplasmList(null, name, Util.getCurrentDateAsLongValue(), "LST", userId, description, parent, status);
		germplasmList.setProgramUUID(programUUID);
		return germplasmList;
	}

	/**
	 * Create dummy germplasm list with list data having dummy assigned id
	 *
	 * @param listId
	 * @param noOfEntries
	 * @return
	 */
	public static GermplasmList createGermplasmListWithListData(final int listId, final int noOfEntries) {
		final GermplasmList germplasmList = createGermplasmList(listId);
		germplasmList.setListData(createGermplasmListData(germplasmList, noOfEntries));
		return germplasmList;
	}

	public static GermplasmList createGermplasmListWithListData(final int listId, final int noOfEntries, List<Integer> gIds) {
		final GermplasmList germplasmList = createGermplasmList(listId);
		germplasmList.setListData(createGermplasmListData(germplasmList, noOfEntries, gIds));
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
		germplasmList.setListData(createGermplasmListDataWithInventoryInfo(germplasmList, noOfEntries));
		return germplasmList;
	}

	/**
	 * Create list of GermplasmListData with dummy assigned ids
	 *
	 * @param germplasmList
	 * @param itemNo
	 * @return
	 */
	public static List<GermplasmListData> createGermplasmListData(final GermplasmList germplasmList, final Integer itemNo) {
		final List<GermplasmListData> listEntries = new ArrayList<GermplasmListData>();
		for (int i = 1; i <= itemNo; i++) {
			final GermplasmListData listEntry = createGermplasmListDataItem(germplasmList, i);
			listEntries.add(listEntry);
		}

		return listEntries;
	}

	/**
	 * Create list of GermplasmListData with dummy assigned ids and  gid
	 *
	 * @param germplasmList
	 * @param itemNo
	 * @return
	 */
	public static List<GermplasmListData> createGermplasmListData(final GermplasmList germplasmList, final Integer itemNo,
			final List<Integer> gids) {
		final List<GermplasmListData> listEntries = new ArrayList<GermplasmListData>();
		for (int i = 1; i <= itemNo; i++) {
			final GermplasmListData listEntry = createGermplasmListDataItemWithGid(germplasmList, gids.get(i - 1), gids.get(i - 1));
			listEntries.add(listEntry);
		}

		return listEntries;
	}

	/**
	 * Create list of GermplasmListData with user-defined gids where the user has an optioned to set default id or not
	 *
	 * @param germplasmList
	 * @param gids
	 * @param setDefaultId
	 * @return
	 */
	public static List<GermplasmListData> createGermplasmListData(final GermplasmList germplasmList, final List<Integer> gids,
			final boolean setDefaultId) {
		final List<GermplasmListData> listEntries = new ArrayList<GermplasmListData>();
		int ctr = 1;
		for (final Integer gid : gids) {
			final GermplasmListData listEntry = createGermplasmListDataItem(germplasmList, ctr, gid, setDefaultId);
			listEntries.add(listEntry);
			ctr++;
		}

		return listEntries;
	}

	public static List<GermplasmListData> createGermplasmListDataWithInventoryInfo(final GermplasmList germplasmList,
			final Integer itemNo) {
		final List<GermplasmListData> listEntries = new ArrayList<GermplasmListData>();
		for (int i = 1; i <= itemNo; i++) {
			final GermplasmListData listEntry = createGermplasmListDataItemWithInventoryInfo(germplasmList, i);
			listEntries.add(listEntry);
		}

		return listEntries;
	}

	public static List<ListDataProject> createListDataSnapShotFromListEntries(final GermplasmList germplasmList,
			final List<GermplasmListData> listEntries) {

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

	protected static GermplasmListData createGermplasmListDataItem(final GermplasmList germplasmList, final int listDataId) {
		return createGermplasmListDataItem(germplasmList, listDataId, listDataId, true);
	}

	protected static GermplasmListData createGermplasmListDataItemWithGid(final GermplasmList germplasmList, final int listDataId,
			final int gid) {
		return createGermplasmListDataItem(germplasmList, listDataId, gid, true);
	}

	protected static GermplasmListData createGermplasmListDataItem(final GermplasmList germplasmList, final int listDataId, final int gid,
			final boolean setDefaultId) {
		final GermplasmListData listEntry = new GermplasmListData();
		listEntry.setId(setDefaultId ? listDataId : null);
		listEntry.setList(germplasmList);
		listEntry.setDesignation("Designation " + listDataId);
		listEntry.setEntryCode("EntryCode " + listDataId);
		listEntry.setEntryId(listDataId);
		listEntry.setGroupName("GroupName " + listDataId);
		listEntry.setStatus(listDataId);
		listEntry.setSeedSource("SeedSource " + listDataId);
		listEntry.setGid(gid);
		// Default MGID(GROUP ID) is 0
		listEntry.setMgid(0);
		return listEntry;
	}

	public List<GermplasmList> createGermplasmLists(final int numOfEntries) {
		final List<GermplasmList> germplasmLists = new ArrayList<GermplasmList>();

		for (int i = 0; i < numOfEntries; i++) {
			final Integer id = i + 1;
			germplasmLists.add(this.createGermplasmListWithListData(id, numOfEntries));
		}
		return germplasmLists;
	}

	public List<GermplasmList> createGermplasmListsWithType(final int numOfEntries) {
		final List<GermplasmList> germplasmLists = new ArrayList<GermplasmList>();

		for (int i = 0; i < numOfEntries; i++) {
			final Integer id = i + 1;
			final GermplasmList germplasmList = this.createGermplasmListWithListData(id, numOfEntries);
			germplasmList.setType("test" + id);
			germplasmLists.add(germplasmList);
		}

		return germplasmLists;
	}

	protected static GermplasmListData createGermplasmListDataItemWithInventoryInfo(final GermplasmList germplasmList,
			final int listDataId) {
		final GermplasmListData listEntry = createGermplasmListDataItem(germplasmList, listDataId, listDataId, true);
		listEntry.setInventoryInfo(createInventoryInfo(listDataId, listDataId, listDataId));
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
