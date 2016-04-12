
package org.generationcp.middleware.data.initializer;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.inventory.ListDataInventory;
import org.generationcp.middleware.domain.inventory.LotDetails;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.util.Util;

public class GermplasmListTestDataInitializer {

	public GermplasmList createGermplasmList(final int id) {
		final GermplasmList germplasmList = new GermplasmList();
		germplasmList.setId(id);
		germplasmList.setName("List " + id);
		germplasmList.setDescription("List " + id + " Description");
		germplasmList.setDate(20150101L);
		germplasmList.setType("LST");
		return germplasmList;
	}

	public GermplasmList createGermplasmList(final String name, final Integer userId, final String description, final GermplasmList parent,
			final Integer status, final String programUUID) {
		final GermplasmList germplasmList =
				new GermplasmList(null, name, Util.getCurrentDateAsLongValue(), "LST", userId, description, parent, status);
		germplasmList.setProgramUUID(programUUID);
		return germplasmList;
	}

	public GermplasmList createGermplasmListWithListData(final int id, final int noOfEntries) {
		final GermplasmList germplasmList = this.createGermplasmList(id);
		germplasmList.setListData(this.createGermplasmListData(noOfEntries));

		return germplasmList;
	}

	public GermplasmList createGermplasmListWithType(final int id, final String type) {
		final GermplasmList germplasmList = this.createGermplasmList(id);
		germplasmList.setType(type);

		return germplasmList;
	}

	public GermplasmList createGermplasmListWithListDataAndInventoryInfo(final int id, final int noOfEntries) {
		final GermplasmList germplasmList = this.createGermplasmList(id);
		germplasmList.setListData(this.createGermplasmListDataWithInventoryInfo(noOfEntries));
		return germplasmList;
	}

	public List<GermplasmListData> createGermplasmListData(final Integer itemNo) {
		final List<GermplasmListData> listEntries = new ArrayList<GermplasmListData>();
		for (int i = 1; i <= itemNo; i++) {
			final GermplasmListData listEntry = this.createGermplasmListDataItem(i);
			listEntries.add(listEntry);
		}

		return listEntries;
	}

	public List<GermplasmListData> createGermplasmListDataWithInventoryInfo(final Integer itemNo) {
		final List<GermplasmListData> listEntries = new ArrayList<GermplasmListData>();
		for (int i = 1; i <= itemNo; i++) {
			final GermplasmListData listEntry = this.createGermplasmListDataItemWithInventoryInfo(i);
			listEntries.add(listEntry);
		}

		return listEntries;
	}

	protected GermplasmListData createGermplasmListDataItem(final int i) {
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

	protected GermplasmListData createGermplasmListDataItemWithInventoryInfo(final int i) {
		final GermplasmListData listEntry = new GermplasmListData();
		listEntry.setId(i);
		listEntry.setDesignation("Designation " + i);
		listEntry.setEntryCode("EntryCode " + i);
		listEntry.setEntryId(i);
		listEntry.setGroupName("GroupName " + i);
		listEntry.setStatus(1);
		listEntry.setSeedSource("SeedSource " + i);
		listEntry.setGid(i);
		listEntry.setInventoryInfo(this.createInventoryInfo(i, i, i));
		return listEntry;
	}

	protected ListDataInventory createInventoryInfo(final int itemNo, final int listDataId, final int gid) {

		final ListDataInventory listDataInventory = new ListDataInventory(listDataId, gid);
		listDataInventory.setLotCount(0);
		listDataInventory.setActualInventoryLotCount(0);
		listDataInventory.setLotRows(new ArrayList<LotDetails>());
		listDataInventory.setReservedLotCount(0);
		listDataInventory.setStockIDs("SID:" + itemNo);

		return listDataInventory;
	}
}
