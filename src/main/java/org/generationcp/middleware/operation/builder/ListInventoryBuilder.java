package org.generationcp.middleware.operation.builder;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.domain.inventory.ListDataInventory;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.util.Debug;

public class ListInventoryBuilder extends Builder {
	
	public ListInventoryBuilder(
			HibernateSessionProvider sessionProviderForLocal,
			HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}
	
	public List<GermplasmListData> retrieveInventoryForList(Integer listId, Integer start, Integer numOfRows) throws MiddlewareQueryException{
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
			retrieveAvailableBalLotCounts(listEntries, gids);
			
			retrieveReservedLotCounts(listEntries, listEntryIds);
		}
    	
		return listEntries;
	}

	
	/*
	 * Retrieve the number of lots with available balance per germplasm
	 */
	private void retrieveAvailableBalLotCounts(List<GermplasmListData> listEntries, List<Integer> gids) throws MiddlewareQueryException{
		Map<Integer, BigInteger> lotCounts = getLotDao().countLotsWithAvailableBalance(gids);
		Debug.print(0, gids);
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

}
