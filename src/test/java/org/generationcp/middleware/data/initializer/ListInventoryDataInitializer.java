package org.generationcp.middleware.data.initializer;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.inventory.ListDataInventory;
import org.generationcp.middleware.domain.inventory.ListEntryLotDetails;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.ims.LotStatus;

public class ListInventoryDataInitializer {

	public static final int NO_OF_LISTDATA = 5;

	public static final int NO_OF_LOTS_PER_LISTDATA = 5;

	public static List<GermplasmListData> createGermplasmListDataWithInventoryDetails() {
		List<GermplasmListData> inventoryDetails = new ArrayList<GermplasmListData>();

		for (int i = 0; i < NO_OF_LISTDATA; i++) {
			int id = i + 1;
			inventoryDetails.add(createGermplasmListData(id));
		}

		return inventoryDetails;
	}

	public static GermplasmListData createGermplasmListData(int id) {

		GermplasmListData listData = new GermplasmListData();

		listData.setId(id);
		listData.setEntryId(id);
		listData.setGid(id);
		listData.setInventoryInfo(createInventoryInfo(id));
		listData.setStatus(0);
		listData.setSeedSource("Seed Source for gid " + id);

		return listData;

	}

	public static ListDataInventory createInventoryInfo(int listDataId) {
		ListDataInventory inventoryInfo = new ListDataInventory(listDataId, listDataId);

		inventoryInfo.setLotRows(createLotDetails(listDataId));
		inventoryInfo.setActualInventoryLotCount(1);
		inventoryInfo.setTotalAvailableBalance(5.0);
		inventoryInfo.setDistinctScaleCountForGermplsm(0);
		return inventoryInfo;
	}

	public static List<ListEntryLotDetails> createLotDetails(int listDataId) {
		List<ListEntryLotDetails> lotDetails = new ArrayList<ListEntryLotDetails>();
		for (int i = 0; i < NO_OF_LOTS_PER_LISTDATA; i++) {
			lotDetails.add(createLotDetail(i, listDataId));
		}
		return lotDetails;
	}

	public static ListEntryLotDetails createLotDetail(int i, int listDataId) {
		ListEntryLotDetails lotDetail = new ListEntryLotDetails();
		int id = (i + 1) * listDataId;
		lotDetail.setId(id);
		lotDetail.setLotId(id);
		lotDetail.setLocationOfLot(createLocation(id));
		lotDetail.setLocId(i);
		lotDetail.setScaleOfLot(createScale(id));
		lotDetail.setScaleId(i);
		lotDetail.setAvailableLotBalance(100D);
		lotDetail.setActualLotBalance(100D);
		lotDetail.setReservedTotalForEntry(100D);
		lotDetail.setCommentOfLot("Lot Comment" + id);
		lotDetail.setStockIds("STK1-1,STK2-2,STK-3");
		lotDetail.setLotScaleNameAbbr("g");
		lotDetail.setWithdrawalBalance(12.0);
		lotDetail.setWithdrawalStatus("1");
		lotDetail.setTransactionStatus(false);
		lotDetail.setLotStatus(LotStatus.ACTIVE.name());
		lotDetail.setReservedTotal(200D);
		lotDetail.setCommittedTotalForEntry(50D);
		lotDetail.setEntityIdOfLot(id);
		return lotDetail;
	}

	public static Term createScale(int id) {
		Term scale = new Term();
		scale.setId(id);
		scale.setName("Scale" + id);
		return scale;
	}

	public static Location createLocation(int id) {
		Location location = new Location();
		location.setLocid(id);
		location.setLname("Location" + id);
		return location;
	}

	public static Term createTerm(String name) {
		Term term = new Term();
		term.setName(name);
		term.setId(0);
		return term;
	}

}
