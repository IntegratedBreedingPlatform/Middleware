package org.generationcp.middleware.domain.inventory.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.generationcp.middleware.domain.inventory.ListDataInventory;
import org.generationcp.middleware.domain.inventory.ListEntryLotDetails;
import org.generationcp.middleware.domain.inventory.LotAggregateData;
import org.generationcp.middleware.domain.inventory.LotDetails;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.ims.Lot;

public class LotTransformer {

	/**
	 * Transform list of ims_lot information and aggregate inventory data to list of ListEntryLotDetails objects while getting reserved
	 * total per lot for specific list entry
	 *
	 * @param lots - lot records assumed to have aggregate inventory data
	 * @param id   - listdata id
	 * @return
	 */
	public static List<ListEntryLotDetails> extractLotDetailsForListEntry(List<Lot> lots, Integer id) {
		List<ListEntryLotDetails> listEntryLots = null;

		if (lots != null && id != null) {
			listEntryLots = new ArrayList<ListEntryLotDetails>();

			for (Lot lot : lots) {
				ListEntryLotDetails lotDetails = new ListEntryLotDetails(id);
				lotDetails.setLotId(lot.getId());
				lotDetails.setLocId(lot.getLocationId());
				lotDetails.setScaleId(lot.getScaleId());
				lotDetails.setEntityIdOfLot(lot.getEntityId());
				lotDetails.setCommentOfLot(lot.getComments());

				LotAggregateData aggregateData = lot.getAggregateData();
				if (aggregateData != null) {
					lotDetails.setAvailableLotBalance(aggregateData.getAvailableBalance());
					lotDetails.setReservedTotal(aggregateData.getReservedTotal());
					lotDetails.setActualLotBalance(aggregateData.getAvailableBalance() + aggregateData.getReservedTotal());
					lotDetails.setStockIds(aggregateData.getStockIds());
					lotDetails.setTransactionId(aggregateData.getTransactionId());
					// get reserved amount for list entry and # reserved for other entries in list for specific lot
					Map<Integer, Double> reservationMap = aggregateData.getReservationMap();
					Double sumEntry = 0d;
					Double sumOthers = 0d;
					if (reservationMap != null) {
						for (Integer recordId : reservationMap.keySet()) {
							Double reservedAmount = reservationMap.get(recordId);
							if (id.equals(recordId)) {
								sumEntry = reservedAmount.doubleValue();
							} else {
								sumOthers += reservedAmount.doubleValue();
							}
						}
						lotDetails.setReservedTotalForEntry(sumEntry);
						lotDetails.setReservedTotalForOtherEntries(sumOthers);
						lotDetails.setWithdrawalBalance(sumEntry);
					}

					Map<Integer, Set<String>> statusMap = aggregateData.getReservationStatusMap();

					if (statusMap != null) {
						Set<String> statusSet = statusMap.get(id);
						String status = "";
						if (statusSet != null) {
							if (statusSet.size() == 1) {
								status = statusSet.iterator().next();
								if ("0".equals(status)) {
									status = ListDataInventory.RESERVED;
								} else if ("1".equals(status)) {
									status = ListDataInventory.WITHDRAWN;
								}
							} else if (statusSet.size() > 1) {
								status = ListDataInventory.RESERVED;
							}
						}

						lotDetails.setWithdrawalStatus(status);

					}
				}

				listEntryLots.add(lotDetails);
			}

		}
		return listEntryLots;
	}

	/**
	 * For each entry in germplasm list, add related list of ListEntryLotDetails objects transformed from Lot objects. Return list of
	 * ListEntryLotDetails for all entries.
	 *
	 * @param lots        - lot records assumed to have aggregate inventory data
	 * @param listEntries - entries of list
	 * @return
	 */
	public static List<ListEntryLotDetails> extractLotRowsForList(List<GermplasmListData> listEntries, List<Lot> lots) {
		List<ListEntryLotDetails> returnLotRows = null;

		if (lots != null && listEntries != null) {
			Map<Integer, List<Lot>> gidLotsMap = new HashMap<Integer, List<Lot>>();
			returnLotRows = new ArrayList<ListEntryLotDetails>();
			LotTransformer.createGidLotListMap(lots, gidLotsMap);

			for (GermplasmListData listEntry : listEntries) {
				Integer gid = listEntry.getGid();
				Integer id = listEntry.getId();
				List<ListEntryLotDetails> lotRows = LotTransformer.extractLotDetailsForListEntry(gidLotsMap.get(gid), id);

				if (lotRows != null) {
					returnLotRows.addAll(lotRows);
					if (listEntry.getInventoryInfo() == null) {
						listEntry.setInventoryInfo(new ListDataInventory(id, gid));
					}
					listEntry.getInventoryInfo().setLotRows(lotRows);
				}
			}

		}
		return returnLotRows;
	}

	/**
	 * Transform Lot objects to LotDetails objects
	 *
	 * @param lots
	 * @return
	 */
	public static List<LotDetails> extraLotDetails(List<Lot> lots) {
		List<LotDetails> returnLotRows = null;

		if (lots != null) {
			returnLotRows = new ArrayList<LotDetails>();

			for (Lot lot : lots) {
				LotDetails lotDetails = new LotDetails();
				lotDetails.setLotId(lot.getId());
				lotDetails.setLocId(lot.getLocationId());
				lotDetails.setScaleId(lot.getScaleId());
				lotDetails.setEntityIdOfLot(lot.getEntityId());
				lotDetails.setCommentOfLot(lot.getComments());

				LotAggregateData aggregateData = lot.getAggregateData();
				if (aggregateData != null) {
					lotDetails.setActualLotBalance(aggregateData.getActualBalance());
					lotDetails.setAvailableLotBalance(aggregateData.getAvailableBalance());
					lotDetails.setReservedTotal(aggregateData.getReservedTotal());
					lotDetails.setStockIds(aggregateData.getStockIds());
				}

				returnLotRows.add(lotDetails);
			}
		}

		return returnLotRows;
	}

	// Germplasm IDs mapped to list of lots for that germplasm
	private static void createGidLotListMap(List<Lot> lots, Map<Integer, List<Lot>> gidLotsMap) {
		List<Lot> lotList = null;
		Integer lastGid = null;

		for (Lot lot : lots) {
			Integer gid = lot.getEntityId();
			if (lastGid == null || !lastGid.equals(gid)) {
				if (lotList != null && !lotList.isEmpty()) {
					gidLotsMap.put(lastGid, lotList);
				}
				lastGid = gid;
				lotList = new ArrayList<Lot>();
			}
			lotList.add(lot);
		}

		if (lastGid != null && lotList != null) {
			gidLotsMap.put(lastGid, lotList);
		}
	}

}
