package org.generationcp.middleware.data.initializer;

import org.generationcp.middleware.domain.gms.GermplasmListType;
import org.generationcp.middleware.domain.inventory.ListDataInventory;
import org.generationcp.middleware.domain.inventory.LotDetails;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.util.Util;

import java.util.ArrayList;
import java.util.List;

public class GermplasmListTestDataInitializer {

	private static final int USER_ID = 1;

	/**
	 * create GermplasmList object with basic list details initialized
	 *
	 * @param id
	 * @return
	 */
	public static GermplasmList createGermplasmList(final Integer id) {
		return GermplasmListTestDataInitializer.createGermplasmList(id, true);
	}

	/**
	 * create GermplasmList object with basic list details initialized given the
	 * user has an option to set the default id or not
	 *
	 * @param id
	 * @param setDefaultId
	 *            - allow to set default id to germplasm list object
	 * @return
	 */
	public static GermplasmList createGermplasmList(final Integer id, final boolean setDefaultId) {
		final GermplasmList germplasmList = new GermplasmList();
		germplasmList.setId(setDefaultId ? id : null);
		germplasmList.setName("List " + id);
		germplasmList.setDescription("List " + id + " Description");
		germplasmList.setDate(20150101L);
		germplasmList.setUserId(GermplasmListTestDataInitializer.USER_ID);
		germplasmList.setType(GermplasmListType.LST.name());
		germplasmList.setStatus(1);
		germplasmList.setNotes("Some notes here");
		return germplasmList;
	}

	public GermplasmList createGermplasmList(final String name, final Integer userId, final String description,
			final GermplasmList parent, final Integer status, final String programUUID) {
		final GermplasmList germplasmList = new GermplasmList(null, name, Util.getCurrentDateAsLongValue(), "LST",
				userId, description, parent, status);
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
		final GermplasmList germplasmList = GermplasmListTestDataInitializer.createGermplasmList(listId);
		germplasmList.setListData(GermplasmListTestDataInitializer.createGermplasmListData(germplasmList, noOfEntries));
		return germplasmList;
	}

	public static GermplasmList createGermplasmListWithListData(final int listId, final int noOfEntries,
			final List<Integer> gIds) {
		final GermplasmList germplasmList = GermplasmListTestDataInitializer.createGermplasmList(listId);
		germplasmList.setListData(
				GermplasmListTestDataInitializer.createGermplasmListData(germplasmList, noOfEntries, gIds));
		return germplasmList;
	}

	/**
	 * Create list of GermplasmListData with dummy assigned ids
	 *
	 * @param germplasmList
	 * @param itemNo
	 * @return
	 */
	public static List<GermplasmListData> createGermplasmListData(final GermplasmList germplasmList,
			final Integer itemNo) {
		final List<GermplasmListData> listEntries = new ArrayList<GermplasmListData>();
		for (int i = 1; i <= itemNo; i++) {
			final GermplasmListData listEntry = GermplasmListTestDataInitializer
					.createGermplasmListDataItem(germplasmList, i);
			listEntries.add(listEntry);
		}

		return listEntries;
	}

	/**
	 * Create list of GermplasmListData with dummy assigned ids and gid
	 *
	 * @param germplasmList
	 * @param itemNo
	 * @return
	 */
	public static List<GermplasmListData> createGermplasmListData(final GermplasmList germplasmList,
			final Integer itemNo, final List<Integer> gids) {
		final List<GermplasmListData> listEntries = new ArrayList<GermplasmListData>();
		for (int i = 1; i <= itemNo; i++) {
			final GermplasmListData listEntry = GermplasmListTestDataInitializer
					.createGermplasmListDataItemWithGid(germplasmList, gids.get(i - 1), gids.get(i - 1));
			listEntries.add(listEntry);
		}

		return listEntries;
	}

	/**
	 * Create list of GermplasmListData with user-defined gids where the user
	 * has an optioned to set default id or not
	 *
	 * @param germplasmList
	 * @param gids
	 * @param setDefaultId
	 * @return
	 */
	public static List<GermplasmListData> createGermplasmListData(final GermplasmList germplasmList,
			final List<Integer> gids, final boolean setDefaultId) {
		final List<GermplasmListData> listEntries = new ArrayList<GermplasmListData>();
		int ctr = 1;
		for (final Integer gid : gids) {
			final GermplasmListData listEntry = GermplasmListTestDataInitializer
					.createGermplasmListDataItem(germplasmList, ctr, gid, setDefaultId);
			listEntries.add(listEntry);
			ctr++;
		}

		return listEntries;
	}

	protected static GermplasmListData createGermplasmListDataItem(final GermplasmList germplasmList,
			final int listDataId) {
		return GermplasmListTestDataInitializer.createGermplasmListDataItem(germplasmList, listDataId, listDataId,
				true);
	}

	protected static GermplasmListData createGermplasmListDataItemWithGid(final GermplasmList germplasmList,
			final int listDataId, final int gid) {
		return GermplasmListTestDataInitializer.createGermplasmListDataItem(germplasmList, listDataId, gid, true);
	}

	protected static GermplasmListData createGermplasmListDataItem(final GermplasmList germplasmList,
			final int listDataId, final int gid, final boolean setDefaultId) {
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
		listEntry.setGroupId(0);;
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

	protected static ListDataInventory createInventoryInfo(final int itemNo, final int listDataId, final int gid) {
		final ListDataInventory listDataInventory = new ListDataInventory(listDataId, gid);
		listDataInventory.setLotCount(0);
		listDataInventory.setActualInventoryLotCount(0);
		listDataInventory.setLotRows(new ArrayList<LotDetails>());
		listDataInventory.setStockIDs("SID:" + itemNo);
		listDataInventory.setDistinctScaleCountForGermplsm(0);

		return listDataInventory;
	}



	public static GermplasmList createGermplasmListTestData(final String name, final String description, final long date,
			final String type, final int userId, final int status, final String programUUID, final Integer projectId) throws MiddlewareQueryException {
		return createGermplasmListTestData(name, description, date, type, userId, status, programUUID, projectId, null, null);
	}

	public static GermplasmList createGermplasmListTestData(final String name, final String description, final long date,
		final String type, final int userId, final int status, final String programUUID, final Integer projectId, final GermplasmList parentFolder,
		final String notes) {
		final GermplasmList list = new GermplasmList();
		list.setName(name);
		list.setDescription(description);
		list.setDate(date);
		list.setType(type);
		list.setUserId(userId);
		list.setStatus(status);
		list.setProgramUUID(programUUID);
		list.setProjectId(projectId);
		list.setParent(parentFolder);
		list.setNotes(notes);
		return list;
	}

}
