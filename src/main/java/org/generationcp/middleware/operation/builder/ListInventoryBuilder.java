
package org.generationcp.middleware.operation.builder;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import org.generationcp.middleware.domain.inventory.GermplasmInventory;
import org.generationcp.middleware.domain.inventory.ListDataInventory;
import org.generationcp.middleware.domain.inventory.ListEntryLotDetails;
import org.generationcp.middleware.domain.inventory.LotDetails;
import org.generationcp.middleware.domain.inventory.util.LotTransformer;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.oms.CVTerm;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import org.generationcp.middleware.pojos.oms.CVTermRelationship;

public class ListInventoryBuilder extends Builder {

	public ListInventoryBuilder(final HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	/**
	 * Return list of GermplasmListData objects for given list, with counts for number of lots with available balance and number of lots
	 * with reserved seed per entry
	 * 
	 * @param listEntries list of {@link GermplasmListData} to which we append lot counts.
	 * @return a germplasm list with the lot count
	 */
	public List<GermplasmListData> retrieveLotCountsForList(final List<GermplasmListData> listEntries) {
		final List<Integer> listEntryIds = new ArrayList<Integer>();
		final List<Integer> gids = new ArrayList<Integer>();
		final List<Integer> lrecIds = new ArrayList<Integer>();
		for (final GermplasmListData entry : listEntries) {
			listEntryIds.add(entry.getId());
			gids.add(entry.getGid());
			entry.setInventoryInfo(new ListDataInventory(entry.getId(), entry.getGid()));
			lrecIds.add(entry.getId());
		}

		if (listEntries != null && !listEntries.isEmpty()) {
			this.retrieveLotCounts(listEntryIds, listEntries, gids, lrecIds);
			this.retrieveWithdrawalAndStatus(listEntryIds, listEntries, gids, lrecIds);
			this.retrieveGroupId(listEntries, gids);
		}
		return listEntries;
	}

	private void retrieveWithdrawalAndStatus(final List<Integer> entryIds, final List<GermplasmListData> listEntries, final List<Integer> gids,
			final List<Integer> lrecIds) throws MiddlewareQueryException {
		this.retrieveWithdrawalBalance(listEntries, lrecIds);
		this.setAvailableBalanceScale(listEntries);
		this.setWithdrawalBalanceScale(listEntries);
		this.setTransactionStatus(listEntries);

	}


	/**
	 * Retrieves GROUP ID(germplsm.mgid) directly from the Germplasm Object
	 * 
	 * @param listEntries
	 * @param gids
	 */
	void retrieveGroupId(final List<GermplasmListData> listEntries, final List<Integer> gids) {

		// We retrieve the actual germplasm object here instead of using the Germplasm from GermplasmListData, since sometimes it is null
		// due to transaction isolation level.
		final List<Germplasm> germplasms = this.getGermplasmDataManager().getGermplasms(gids);

		final Map<Integer, Germplasm> germplasmMap = Maps.uniqueIndex(germplasms, new Function<Germplasm, Integer>() {

			@Override
			public Integer apply(final Germplasm from) {
				return from.getGid();
			}
		});

		for (final GermplasmListData listEntry : listEntries) {
			final Integer gid = listEntry.getGid();
			if (germplasmMap.containsKey(gid)) {
				listEntry.setGroupId(germplasmMap.get(gid).getMgid());
			}
		}
	}

	public List<GermplasmListData> retrieveLotCountsForListEntries(final Integer listId, final List<Integer> entryIds)
			throws MiddlewareQueryException {
		List<GermplasmListData> listEntries = null;
		listEntries = this.getGermplasmListDataDAO().getByIds(entryIds);
		final List<Integer> gids = new ArrayList<Integer>();
		final List<Integer> lrecIds = new ArrayList<Integer>();
		for (final GermplasmListData entry : listEntries) {
			gids.add(entry.getGid());
			entry.setInventoryInfo(new ListDataInventory(entry.getId(), entry.getGid()));
			lrecIds.add(entry.getId());
		}
		this.retrieveLotCounts(entryIds, listEntries, gids, lrecIds);
		this.retrieveGroupId(listEntries, gids);
		return listEntries;
	}

	private void retrieveLotCounts(final List<Integer> entryIds, final List<GermplasmListData> listEntries, final List<Integer> gids,
			final List<Integer> lrecIds) throws MiddlewareQueryException {

		// NEED to pass specific GIDs instead of listdata.gid because of handling for CHANGES table
		// where listdata.gid may not be the final germplasm displayed
		this.retrieveAvailableBalLotCounts(listEntries, gids);
		this.retrieveReservedLotCounts(listEntries, entryIds);
		this.retrieveStockIds(listEntries, lrecIds);

	}

	// This will add overall scale for germplsm and set in InventoryInfo. Useful to display scale along with available balance
	private void setAvailableBalanceScale(final List<GermplasmListData> listEntries){
		Set<Integer> setScaleIds = new HashSet<>();
		Map<Integer, Term> mapScaleTerm = new HashMap<>();
		for(GermplasmListData entry : listEntries){
			if(entry.getInventoryInfo() != null && entry.getInventoryInfo().getScaleIdForGermplsm() != null){
				setScaleIds.add(entry.getInventoryInfo().getScaleIdForGermplsm());
			}
		}

		List<Integer> listScaleIds = Lists.newArrayList(setScaleIds);

		if(!listScaleIds.isEmpty()){
			Map<Integer, Integer> scaleIdWiseTermId = new HashMap<>();
			final List<CVTermRelationship> relationshipsOfScales = this.getCvTermRelationshipDao().getBySubjectIdsAndTypeId(listScaleIds,
					TermId.HAS_SCALE.getId());
			List<Integer> listObjectIds = new ArrayList<>();
			if(relationshipsOfScales != null){
				for(CVTermRelationship relationship : relationshipsOfScales){
					scaleIdWiseTermId.put(relationship.getSubjectId(), relationship.getObjectId());
					listObjectIds.add(relationship.getObjectId());
				}

				List<CVTerm> listScaleCvTerm = this.getCvTermDao().getByIds(listObjectIds);

				for (final CVTerm cvTerm : listScaleCvTerm) {
					Term term = TermBuilder.mapCVTermToTerm(cvTerm);
					mapScaleTerm.put(term.getId(), term);

				}

				for(GermplasmListData entry : listEntries){
					if(entry.getInventoryInfo() != null && entry.getInventoryInfo().getScaleIdForGermplsm() != null){
						Integer scaleId = entry.getInventoryInfo().getScaleIdForGermplsm();

						if(scaleIdWiseTermId.containsKey(scaleId)){
							Integer objectId = scaleIdWiseTermId.get(scaleId);
							if(mapScaleTerm.containsKey(objectId)){
								Term scale = mapScaleTerm.get(objectId);
								entry.getInventoryInfo().setScaleForGermplsm(scale.getName());
							}
							else{
								entry.getInventoryInfo().setScaleForGermplsm("");
							}
						}
						else{
							entry.getInventoryInfo().setScaleForGermplsm("");
						}

					}

				}
			}
		}

	}

	// This will set withdrawal scale for germplsm for selected list entries
	private void setWithdrawalBalanceScale(final List<GermplasmListData> listEntries){
		Set<Integer> setScaleIds = new HashSet<>();
		Map<Integer, Term> mapScaleTerm = new HashMap<>();
		for(GermplasmListData entry : listEntries){
			if(entry.getInventoryInfo() != null && entry.getInventoryInfo().getWithdrawalScaleId() != null){
				setScaleIds.add(entry.getInventoryInfo().getScaleIdForGermplsm());
			}
		}

		List<Integer> listScaleIds = Lists.newArrayList(setScaleIds);

		if(!listScaleIds.isEmpty()){
			Map<Integer, Integer> scaleIdWiseTermId = new HashMap<>();
			final List<CVTermRelationship> relationshipsOfScales = this.getCvTermRelationshipDao().getBySubjectIdsAndTypeId(listScaleIds,
					TermId.HAS_SCALE.getId());
			List<Integer> listObjectIds = new ArrayList<>();
			if(relationshipsOfScales != null){
				for(CVTermRelationship relationship : relationshipsOfScales){
					scaleIdWiseTermId.put(relationship.getSubjectId(), relationship.getObjectId());
					listObjectIds.add(relationship.getObjectId());
				}

				List<CVTerm> listScaleCvTerm = this.getCvTermDao().getByIds(listObjectIds);

				for (final CVTerm cvTerm : listScaleCvTerm) {
					Term term = TermBuilder.mapCVTermToTerm(cvTerm);
					mapScaleTerm.put(term.getId(), term);

				}

				for(GermplasmListData entry : listEntries){
					if(entry.getInventoryInfo() != null && entry.getInventoryInfo().getScaleIdForGermplsm() != null){
						Integer scaleId = entry.getInventoryInfo().getScaleIdForGermplsm();

						if(scaleIdWiseTermId.containsKey(scaleId)){
							Integer objectId = scaleIdWiseTermId.get(scaleId);
							if(mapScaleTerm.containsKey(objectId)){
								Term scale = mapScaleTerm.get(objectId);
								entry.getInventoryInfo().setWithdrawalScale(scale.getName());

							}
							else{
								entry.getInventoryInfo().setWithdrawalScale("");
							}
						}
						else{
							entry.getInventoryInfo().setWithdrawalScale("");
						}

					}

				}
			}
		}

	}

	private void setTransactionStatus(final List<GermplasmListData> listEntries){
		for(GermplasmListData entry : listEntries){
			if(entry.getInventoryInfo() != null){
				Integer distinctCountWithdrawalStatus = entry.getInventoryInfo().getDistinctCountWithdrawalStatus();
				if(distinctCountWithdrawalStatus == 0){
					entry.getInventoryInfo().setTransactionStatus("");
				}else if(distinctCountWithdrawalStatus == 1){
					if(entry.getInventoryInfo().getWithdrawalStatus() == 0){
						entry.getInventoryInfo().setTransactionStatus(GermplasmInventory.RESERVED);
					}
					if(entry.getInventoryInfo().getWithdrawalStatus() == 1){
						entry.getInventoryInfo().setTransactionStatus(GermplasmInventory.WITHDRAWN);
					}
				}else{
					entry.getInventoryInfo().setTransactionStatus(GermplasmInventory.MIXED);
				}
			}
		}
	}


	private void retrieveStockIds(final List<GermplasmListData> listEntries, final List<Integer> lrecIds) {
		final Map<Integer, String> stockIDs = this.getTransactionDao().retrieveStockIds(lrecIds);
		for (final GermplasmListData entry : listEntries) {
			final ListDataInventory inventory = entry.getInventoryInfo();
			if (inventory != null) {
				inventory.setStockIDs(stockIDs.get(entry.getId()));
			}
		}
	}

	public Integer countLotsWithAvailableBalanceForGermplasm(final Integer gid) throws MiddlewareQueryException {
		Integer lotCount = null;
		final Map<Integer, BigInteger> lotCounts = this.getLotDao().countLotsWithAvailableBalance(Collections.singletonList(gid));
		final BigInteger lotCountBigInt = lotCounts.get(gid);
		if (lotCounts != null && lotCountBigInt != null) {
			lotCount = lotCountBigInt.intValue();
		}
		return lotCount;
	}

	public List<LotDetails> retrieveInventoryLotsForGermplasm(final Integer gid) throws MiddlewareQueryException {
		List<LotDetails> lotDetails = null;
		final List<Lot> lots = this.getLotDao().getLotAggregateDataForGermplasm(gid);
		lotDetails = LotTransformer.extraLotDetails(lots);
		this.setLocationsAndScales(lotDetails);
		return lotDetails;
	}

	/**
	 * Return list of GermplasmListData objects for given list with list of lots associated per germplasm entry
	 * 
	 * @param listId
	 * @param start
	 * @param numOfRows
	 * @return
	 * @throws MiddlewareQueryException
	 */
	public List<GermplasmListData> retrieveInventoryLotsForList(final Integer listId, final int start, final int numOfRows,
			final List<GermplasmListData> listEntries) throws MiddlewareQueryException {

		final List<Integer> listEntryIds = new ArrayList<Integer>();
		final List<Integer> gids = new ArrayList<Integer>();
		for (final GermplasmListData entry : listEntries) {
			listEntryIds.add(entry.getId());
			gids.add(entry.getGid());
			entry.setInventoryInfo(new ListDataInventory(entry.getId(), entry.getGid()));
		}

		if (listEntries != null && !listEntries.isEmpty()) {
			// retrieve inventory information from local db

			// NEED to pass specific GIDs instead of listdata.gid because of handling for CHANGES table
			// where listdata.gid may not be the final germplasm displayed
			final List<Lot> lots = this.getLotDao().getLotAggregateDataForList(listId, gids);

			// add to each list entry related lot information
			final List<ListEntryLotDetails> lotRows = LotTransformer.extractLotRowsForList(listEntries, lots);
			this.setLocationsAndScales(lotRows);
		}
		return listEntries;
	}

	public List<ListEntryLotDetails> retrieveInventoryLotsForListEntry(final Integer listId, final Integer recordId, final Integer gid)
			throws MiddlewareQueryException {
		List<ListEntryLotDetails> lotRows = new ArrayList<ListEntryLotDetails>();

		final List<Lot> lots = this.getLotDao().getLotAggregateDataForListEntry(listId, gid);
		lotRows = LotTransformer.extractLotDetailsForListEntry(lots, recordId);
		this.setLocationsAndScales(lotRows);

		return lotRows;
	}

	/*
	 * Retrieve the number of lots with available balance per germplasm and available seed balance per germplsm along with overall scaleId
	 */
	private void retrieveAvailableBalLotCounts(final List<GermplasmListData> listEntries, final List<Integer> gids)
			throws MiddlewareQueryException {
		final Map<Integer, Object[]> lotCounts = this.getLotDao().getLotsWithAvailableBalanceCountAndTotalLotsCount(gids);
		for (final GermplasmListData entry : listEntries) {
			final ListDataInventory inventory = entry.getInventoryInfo();
			if (inventory != null) {
				final Object[] count = lotCounts.get(entry.getGid());
				if (count != null) {
					BigInteger actualInventoryLotCount = (BigInteger)count[0];
					inventory.setActualInventoryLotCount(actualInventoryLotCount.intValue());

					BigInteger lotCount = (BigInteger)count[1];
					inventory.setLotCount(lotCount.intValue());

					Double totalAvailableBalance = (Double) count[2];
					inventory.setTotalAvailableBalance(totalAvailableBalance.doubleValue());

					BigInteger distinctScaleCount = (BigInteger)count[3];
					inventory.setDistinctScaleCountForGermplsm(distinctScaleCount.intValue());

					if(count[4] != null){
						Integer scaleId = (Integer)count[4];
						inventory.setScaleIdForGermplsm(scaleId.intValue());
					}
				} else {
					inventory.setActualInventoryLotCount(0);
					inventory.setLotCount(0);
					inventory.setTotalAvailableBalance(0.0);
					inventory.setDistinctScaleCountForGermplsm(0);
					inventory.setScaleIdForGermplsm(null);
				}
			}
		}
	}

	/*
	 * Retrieve the number of lots with reserved seeds per list entry
	 */
	private void retrieveReservedLotCounts(final List<GermplasmListData> listEntries, final List<Integer> listEntryIds)
			throws MiddlewareQueryException {
		final Map<Integer, BigInteger> reservedLotCounts = this.getTransactionDao().countLotsWithReservationForListEntries(listEntryIds);
		for (final GermplasmListData entry : listEntries) {
			final ListDataInventory inventory = entry.getInventoryInfo();
			if (inventory != null) {
				final BigInteger count = reservedLotCounts.get(entry.getId());
				if (count != null) {
					inventory.setReservedLotCount(count.intValue());
				} else {
					inventory.setReservedLotCount(0);
				}
			}
		}
	}

	/*
	 * Retrieve withdrawal balance per entry along with overall status
	 */
	private void retrieveWithdrawalBalance(final List<GermplasmListData> listEntries, final List<Integer> listEntryIds)
			throws MiddlewareQueryException {
		final Map<Integer, Object[]> withdrawalData = this.getTransactionDao().retrieveWithdrawalBalanceWithDistinctTransactionStatus(listEntryIds);
		for (final GermplasmListData entry : listEntries) {
			final ListDataInventory inventory = entry.getInventoryInfo();
			if (inventory != null) {
				final Object[] withdrawalPerRecord = withdrawalData.get(entry.getId());
 				if (withdrawalPerRecord != null) {
					inventory.setWithdrawalBalance((Double)withdrawalPerRecord[0]);

					BigInteger countDistinctStatus = (BigInteger) withdrawalPerRecord[1];
					inventory.setDistinctCountWithdrawalStatus(countDistinctStatus.intValue());
					inventory.setWithdrawalStatus((Integer) withdrawalPerRecord[2]);

					BigInteger countWithDrawalScale = (BigInteger) withdrawalPerRecord[3];
					inventory.setDistinctCountWithdrawalScale(countWithDrawalScale.intValue());
					inventory.setWithdrawalScaleId((Integer) withdrawalPerRecord[4]);



				} else {
					inventory.setWithdrawalBalance(0.0);
					inventory.setDistinctCountWithdrawalScale(0);
					inventory.setWithdrawalScaleId(null);
					inventory.setWithdrawalScale(null);

					inventory.setDistinctCountWithdrawalStatus(0);
					inventory.setWithdrawalStatus(null);
					inventory.setTransactionStatus(null);
				}
			}
		}
	}


	/*
	 * Perform one-retrieval for central/local scales and central/local locations for list of lots
	 */
	private void setLocationsAndScales(final List<? extends LotDetails> lots) throws MiddlewareQueryException {

		final List<Integer> locationIds = new ArrayList<Integer>();
		final List<Integer> scaleIds = new ArrayList<Integer>();
		final Map<Integer, List<LotDetails>> scaleLotMap = new HashMap<Integer, List<LotDetails>>();
		final Map<Integer, List<LotDetails>> locationLotMap = new HashMap<Integer, List<LotDetails>>();

		this.createScaleAndLocationMaps(lots, locationIds, scaleIds, scaleLotMap, locationLotMap);

		final List<Location> allLocations = this.getLocationDao().getByIds(locationIds);
		final List<Term> allScales = new ArrayList<Term>();
		final List<CVTerm> cvTerms = this.getCvTermDao().getByIds(scaleIds);
		for (final CVTerm cvTerm : cvTerms) {
			allScales.add(TermBuilder.mapCVTermToTerm(cvTerm));
		}

		Map<Integer, String> mapScaleAbbrMap = new HashMap<>();
		Map<Integer, Integer> scaleIdWiseTermId = new HashMap<>();
		if(!scaleIds.isEmpty()){

			final List<CVTermRelationship> relationshipsOfScales = this.getCvTermRelationshipDao().getBySubjectIdsAndTypeId(scaleIds,
					TermId.HAS_SCALE.getId());
			List<Integer> listObjectIds = new ArrayList<>();
			if(relationshipsOfScales != null){
				for(CVTermRelationship relationship : relationshipsOfScales){
					scaleIdWiseTermId.put(relationship.getSubjectId(), relationship.getObjectId());
					listObjectIds.add(relationship.getObjectId());
				}

				List<CVTerm> listScaleCvTerm = this.getCvTermDao().getByIds(listObjectIds);

				for (final CVTerm cvTerm : listScaleCvTerm) {
					Term term = TermBuilder.mapCVTermToTerm(cvTerm);
					mapScaleAbbrMap.put(term.getId(), term.getName());
				}

			}
		}


		for (final Location location : allLocations) {
			final List<LotDetails> lotList = locationLotMap.get(location.getLocid());
			for (final LotDetails lot : lotList) {
				lot.setLocationOfLot(location);
			}
		}

		for (final Term scale : allScales) {
			final List<LotDetails> lotList = scaleLotMap.get(scale.getId());
			for (final LotDetails lot : lotList) {
				lot.setScaleOfLot(scale);
				String scaleAbbr = "";
				if(scaleIdWiseTermId.containsKey(lot.getScaleId())){
					Integer hasScaleCvTermId = scaleIdWiseTermId.get(lot.getScaleId());

					if(mapScaleAbbrMap.containsKey(hasScaleCvTermId)){
						scaleAbbr = mapScaleAbbrMap.get(hasScaleCvTermId);
					}
				}
				lot.setLotScaleNameAbbr(scaleAbbr);

			}
		}
	}

	// create maps of scale/location IDs to lots for easier setting of Terms and Locations
	private void createScaleAndLocationMaps(final List<? extends LotDetails> lots, final List<Integer> locationIds,
			final List<Integer> scaleIds, final Map<Integer, List<LotDetails>> scaleLotMap,
			final Map<Integer, List<LotDetails>> locationLotMap) {

		for (final LotDetails lot : lots) {
			final Integer locationId = lot.getLocId();
			final List<LotDetails> lotList = locationLotMap.get(locationId);
			if (lotList != null) {
				lotList.add(lot);
			} else {
				final List<LotDetails> listLot = new ArrayList<LotDetails>();
				listLot.add(lot);
				locationLotMap.put(locationId, listLot);
			}
			locationIds.add(locationId);

			final Integer scaleId = lot.getScaleId();
			if (scaleLotMap.get(scaleId) != null) {
				scaleLotMap.get(scaleId).add(lot);
			} else {
				final List<LotDetails> listLot = new ArrayList<LotDetails>();
				listLot.add(lot);
				scaleLotMap.put(scaleId, listLot);
			}
			scaleIds.add(scaleId);
		}
	}
}
