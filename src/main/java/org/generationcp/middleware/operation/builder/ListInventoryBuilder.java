package org.generationcp.middleware.operation.builder;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.domain.inventory.ListDataInventory;
import org.generationcp.middleware.domain.inventory.ListEntryLotDetails;
import org.generationcp.middleware.domain.inventory.LotDetails;
import org.generationcp.middleware.domain.inventory.util.LotTransformer;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.util.Debug;

public class ListInventoryBuilder extends Builder {
	
	public ListInventoryBuilder(
			HibernateSessionProvider sessionProviderForLocal,
			HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}
	
	/**
	 * Return list of GermplasmListData objects for given list, with counts for
	 * number of lots with available balance and number of lots with reserved seed per entry
	 * 
	 * @param listId
	 * @param start
	 * @param numOfRows
	 * @return
	 * @throws MiddlewareQueryException
	 */
	public List<GermplasmListData> retrieveLotCountsForList(Integer listId, Integer start, Integer numOfRows) throws MiddlewareQueryException{
		List<GermplasmListData> listEntries = null;
		
		if (setWorkingDatabase(listId)){
			listEntries = getGermplasmListDataDAO().getByListId(listId, start, numOfRows);
			
			List<Integer> listEntryIds = new ArrayList<Integer>();
			List<Integer> gids = new ArrayList<Integer>();
			for (GermplasmListData entry : listEntries){
				listEntryIds.add(entry.getId());
				gids.add(entry.getGid());
				entry.setInventoryInfo(new ListDataInventory(entry.getId(), entry.getGid()));
			}

			retrieveLotCounts(listEntryIds, listEntries, gids);
		}
    	
		return listEntries;
	}
	
	
	public List<GermplasmListData> retrieveLotCountsForListEntries(Integer listId, List<Integer> entryIds) throws MiddlewareQueryException{
		List<GermplasmListData> listEntries = null;
		if (setWorkingDatabase(listId)){
			listEntries = getGermplasmListDataDAO().getByIds(entryIds);
			List<Integer> gids = new ArrayList<Integer>();
			for (GermplasmListData entry : listEntries){
				gids.add(entry.getGid());
				entry.setInventoryInfo(new ListDataInventory(entry.getId(), entry.getGid()));
			}

			retrieveLotCounts(entryIds, listEntries, gids);
		}
		
		return listEntries;

	}

	private void retrieveLotCounts(List<Integer> entryIds,
			List<GermplasmListData> listEntries, List<Integer> gids)
			throws MiddlewareQueryException {
		// retrieve inventory information from local db
		setWorkingDatabase(Database.LOCAL);
		
		// NEED to pass specific GIDs instead of listdata.gid because of handling for CHANGES table
		// where listdata.gid may not be the final germplasm displayed
		retrieveAvailableBalLotCounts(listEntries, gids);
		
		retrieveReservedLotCounts(listEntries, entryIds);
	}
	
	public Integer countLotsWithAvailableBalanceForGermplasm(Integer gid) throws MiddlewareQueryException{
		Integer lotCount = null;
		if (setWorkingDatabase(Database.LOCAL)){
			Map<Integer, BigInteger> lotCounts = getLotDao().countLotsWithAvailableBalance(Collections.singletonList(gid));
			BigInteger lotCountBigInt = lotCounts.get(gid);
			if (lotCounts != null && lotCountBigInt != null){
				lotCount = lotCountBigInt.intValue();
			}
		}
		
		return lotCount; 
	}

	public List<LotDetails> retrieveInventoryLotsForGermplasm(Integer gid) throws MiddlewareQueryException{
		List<LotDetails> lotDetails = null;
		if (setWorkingDatabase(Database.LOCAL)){
			List<Lot> lots = getLotDao().getLotAggregateDataForGermplasm(gid);
			lotDetails = LotTransformer.extraLotDetails(lots);
			setLocationsAndScales(lotDetails);
		}
		
		return lotDetails;
	}
	
	
	/**
	 * Return list of GermplasmListData objects for given list with
	 * list of lots associated per germplasm entry
	 * 
	 * @param listId
	 * @param start
	 * @param numOfRows
	 * @return
	 * @throws MiddlewareQueryException
	 */
	public List<GermplasmListData> retrieveInventoryLotsForList(Integer listId, int start, int numOfRows) throws MiddlewareQueryException{
		List<GermplasmListData> listEntries = null;
		
		if (setWorkingDatabase(listId)){
			listEntries = getGermplasmListDataDAO().getByListId(listId, start, numOfRows);
			
			List<Integer> listEntryIds = new ArrayList<Integer>();
			List<Integer> gids = new ArrayList<Integer>();
			for (GermplasmListData entry : listEntries){
				listEntryIds.add(entry.getId());
				gids.add(entry.getGid());
				entry.setInventoryInfo(new ListDataInventory(entry.getId(), entry.getGid()));
			}

			// retrieve inventory information from local db
			setWorkingDatabase(Database.LOCAL);
			
			// NEED to pass specific GIDs instead of listdata.gid because of handling for CHANGES table
			// where listdata.gid may not be the final germplasm displayed
			List<Lot> lots = getLotDao().getLotAggregateDataForList(listId, gids);
			
			// add to each list entry related lot information
			List<ListEntryLotDetails> lotRows = LotTransformer.extractLotRowsForList(listEntries, lots);
			setLocationsAndScales(lotRows);
			
		}
    	
		return listEntries;
	}
	
	
	public List<ListEntryLotDetails> retrieveInventoryLotsForListEntry(Integer listId, Integer recordId, Integer gid) throws MiddlewareQueryException{
		List<ListEntryLotDetails> lotRows = new ArrayList<ListEntryLotDetails>();
		
		if (setWorkingDatabase(Database.LOCAL)){
			List<Lot> lots = getLotDao().getLotAggregateDataForListEntry(listId, gid);
			lotRows = LotTransformer.extractLotDetailsForListEntry(lots, recordId);
			setLocationsAndScales(lotRows);
			
		}
		return lotRows;
	}
	

	
	/*
	 * Retrieve the number of lots with available balance per germplasm
	 */
	private void retrieveAvailableBalLotCounts(List<GermplasmListData> listEntries, List<Integer> gids) throws MiddlewareQueryException{
		Map<Integer, BigInteger> lotCounts = getLotDao().countLotsWithAvailableBalance(gids);
		for (GermplasmListData entry : listEntries){
			ListDataInventory inventory = entry.getInventoryInfo();
			if (inventory != null ){
				BigInteger count = lotCounts.get(entry.getGid());
				if (count != null){
					inventory.setActualInventoryLotCount(count.intValue());
				}
			}
		}
	}
	
	
	/*
	 * Retrieve the number of lots with reserved seeds per list entry
	 */
	private void retrieveReservedLotCounts(List<GermplasmListData> listEntries, List<Integer> listEntryIds) throws MiddlewareQueryException{
		Map<Integer, BigInteger> reservedLotCounts = getTransactionDao().countLotsWithReservationForListEntries(listEntryIds);
		Debug.print(0, listEntryIds);
		for (GermplasmListData entry : listEntries){
			ListDataInventory inventory = entry.getInventoryInfo();
			if (inventory != null ){
				BigInteger count = reservedLotCounts.get(entry.getId());
				if (count != null){
					inventory.setReservedLotCount(count.intValue());
				}
			}
		}
	}
	
	/*
	 * Perform one-retrieval for central/local scales and central/local locations
	 * for list of lots
	 */
	private void setLocationsAndScales(List<? extends LotDetails> lots) throws MiddlewareQueryException{
		
		List<Integer> negativeLocationIds = new ArrayList<Integer>();
		List<Integer> positiveLocationIds = new ArrayList<Integer>();
		List<Integer> negativeScaleIds = new ArrayList<Integer>();
		List<Integer> positiveScaleIds = new ArrayList<Integer>();
		Map<Integer, List<LotDetails>> scaleLotMap = new HashMap<Integer, List<LotDetails>>();
		Map<Integer, List<LotDetails>> locationLotMap = new HashMap<Integer, List<LotDetails>>();
		
		createScaleAndLocationMaps(lots, negativeLocationIds,	positiveLocationIds, negativeScaleIds, positiveScaleIds,
				scaleLotMap, locationLotMap);
		
		List<Location> allLocations = new ArrayList<Location>();
		List<Term> allScales = new ArrayList<Term>();
		queryLocationsAndScalesFromDatabase(negativeLocationIds, positiveLocationIds, negativeScaleIds, positiveScaleIds, 
				allLocations, allScales);
		
		for (Location location : allLocations){
			List<LotDetails> lotList = locationLotMap.get(location.getLocid());
			for (LotDetails lot : lotList){
				lot.setLocationOfLot(location);
			}
		}
		
		for (Term scale : allScales){
			List<LotDetails> lotList = scaleLotMap.get(scale.getId());
			for (LotDetails lot : lotList){
				lot.setScaleOfLot(scale);
			}
		}
	}

	// create maps of scale/location IDs to lots for easier setting of Terms and Locations
	private void createScaleAndLocationMaps(List<? extends LotDetails> lots,
			List<Integer> negativeLocationIds,
			List<Integer> positiveLocationIds, List<Integer> negativeScaleIds,
			List<Integer> positiveScaleIds,
			Map<Integer, List<LotDetails>> scaleLotMap,
			Map<Integer, List<LotDetails>> locationLotMap) {
		
		for (LotDetails lot : lots){
			Integer locationId = lot.getLocId();
			List<LotDetails> lotList = locationLotMap.get(locationId);
			if (lotList != null){
				lotList.add(lot);
			} else {
				List<LotDetails> listLot = new ArrayList<LotDetails>();
				listLot.add(lot);
				locationLotMap.put(locationId, listLot); 
			}
			if (locationId < 0){
				negativeLocationIds.add(locationId);
			} else {
				positiveLocationIds.add(locationId);
			}
			
			Integer scaleId = lot.getScaleId();
			if (scaleLotMap.get(scaleId) != null){
				scaleLotMap.get(scaleId).add(lot);
			} else {
				List<LotDetails> listLot = new ArrayList<LotDetails>();
				listLot.add(lot);
				scaleLotMap.put(scaleId, listLot); 
			}
			if (scaleId < 0){
				negativeScaleIds.add(scaleId);
			} else {
				positiveScaleIds.add(scaleId);
			}
		}
		
	}

	private void queryLocationsAndScalesFromDatabase(
			List<Integer> negativeLocationIds,
			List<Integer> positiveLocationIds, List<Integer> negativeScaleIds,
			List<Integer> positiveScaleIds, List<Location> allLocations,
			List<Term> allScales) throws MiddlewareQueryException {
		
		List<Location> localLocations = new ArrayList<Location>();
		List<Location> centralLocations = new ArrayList<Location>();
		List<Term> localScales = new ArrayList<Term>();
		List<Term> centralScales = new ArrayList<Term>();
		
		if (!negativeLocationIds.isEmpty() || !negativeScaleIds.isEmpty()){
			setWorkingDatabase(Database.LOCAL);
			
			if (!negativeLocationIds.isEmpty()){
				localLocations = getLocationDao().getByIds(negativeLocationIds);
			}
			
			if (!negativeScaleIds.isEmpty()){
				List<CVTerm> cvTerms = getCvTermDao().getByIds(negativeScaleIds);
				for (CVTerm cvTerm : cvTerms){
					localScales.add(TermBuilder.mapCVTermToTerm(cvTerm));
				}
			}
		}
		
		if (!positiveLocationIds.isEmpty() || !positiveScaleIds.isEmpty()){
			setWorkingDatabase(Database.CENTRAL);
			
			if (!positiveLocationIds.isEmpty()){
				centralLocations = getLocationDao().getByIds(positiveLocationIds);
			}
			
			if (!positiveScaleIds.isEmpty()){
				List<CVTerm> cvTerms = getCvTermDao().getByIds(positiveScaleIds);
				for (CVTerm cvTerm : cvTerms){
					centralScales.add(TermBuilder.mapCVTermToTerm(cvTerm));
				}
			}
		}
		allLocations.addAll(localLocations);
		allLocations.addAll(centralLocations);
		allScales.addAll(localScales);
		allScales.addAll(centralScales);
	}

}
