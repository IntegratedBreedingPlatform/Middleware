package org.generationcp.middleware.domain.inventory.util;

import org.generationcp.middleware.domain.inventory.ListDataInventory;
import org.generationcp.middleware.domain.inventory.ListEntryLotDetails;
import org.generationcp.middleware.domain.inventory.LotAggregateData;
import org.generationcp.middleware.domain.inventory.LotDetails;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.LotStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
				lotDetails.setLotStatus(lot.getStatus() == LotStatus.ACTIVE.getIntValue() ? LotStatus.ACTIVE.name() : LotStatus.CLOSED.name());


				LotAggregateData aggregateData = lot.getAggregateData();
				if (aggregateData != null) {
					lotDetails.setAvailableLotBalance(aggregateData.getAvailableBalance());
					lotDetails.setReservedTotal(aggregateData.getReservedTotal());
					lotDetails.setCommittedTotal(aggregateData.getCommittedTotal());
					lotDetails.setActualLotBalance(aggregateData.getAvailableBalance() + aggregateData.getReservedTotal());
					lotDetails.setStockIds(aggregateData.getStockIds());
					lotDetails.setTransactionId(aggregateData.getTransactionId());
					// get reserved and committed amount for list entry and # reserved for other entries in list for specific lot
					Map<Integer, Double> reservationMap = aggregateData.getReservationMap();
					Map<Integer, Double> committedMap = aggregateData.getCommittedMap();

					if (reservationMap != null) {
						setAggregateTransactionBalance(reservationMap, lotDetails, id, ListDataInventory.RESERVED);
					}

					if(committedMap != null) {
						setAggregateTransactionBalance(committedMap, lotDetails, id, ListDataInventory.COMMITTED);
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
				lotDetails.setLotStatus(lot.getStatus() == LotStatus.ACTIVE.getIntValue() ? LotStatus.ACTIVE.name() : LotStatus.CLOSED.name());
				lotDetails.setCreatedDate(lot.getCreatedDate());

				LotAggregateData aggregateData = lot.getAggregateData();
				if (aggregateData != null) {
					lotDetails.setActualLotBalance(aggregateData.getActualBalance());
					lotDetails.setAvailableLotBalance(aggregateData.getAvailableBalance());
					lotDetails.setReservedTotal(aggregateData.getReservedTotal());
					lotDetails.setStockIds(aggregateData.getStockIds());
					lotDetails.setWithdrawalBalance(aggregateData.getReservedTotal() + aggregateData.getCommittedTotal());
					lotDetails.setWithdrawalStatus(aggregateData.getLotStatus());
				}

				returnLotRows.add(lotDetails);
			}
		}

		return returnLotRows;
	}

	private static void setAggregateTransactionBalance(Map<Integer, Double> transactionMap, ListEntryLotDetails lotDetails, Integer entryId,
			String
			transactionType) {
		Double sumForEntry = 0d;
		Double sumForOtherEntries = 0d;

		for (Integer recordId : transactionMap.keySet()) {
			Double transactionAmount = transactionMap.get(recordId);
			if (entryId.equals(recordId)) {
				sumForEntry = transactionAmount.doubleValue();
			} else {
				sumForOtherEntries += transactionAmount.doubleValue();
			}
		}

		if(ListDataInventory.RESERVED.equals(transactionType)) {
			lotDetails.setReservedTotalForEntry(sumForEntry);
			lotDetails.setReservedTotalForOtherEntries(sumForOtherEntries);
			lotDetails.setWithdrawalBalance(sumForEntry);
		}

		if(ListDataInventory.COMMITTED.equals(transactionType)) {
			lotDetails.setCommittedTotalForEntry(sumForEntry);
			lotDetails.setCommittedTotalForOtherEntries(sumForOtherEntries);
		}

	}
}
