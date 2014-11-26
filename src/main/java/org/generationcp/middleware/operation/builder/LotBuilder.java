/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.middleware.operation.builder;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.ims.EntityType;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.LotStatus;
import org.generationcp.middleware.pojos.ims.LotsResult;

import java.util.ArrayList;
import java.util.List;

public class LotBuilder extends Builder {
	
	private static final int LOT_NOT_DERIVED_FROM_ANOTHER = 0;
	

	public LotBuilder(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	public List<Lot> build(List<Integer> gids, Integer locationId, Integer scaleId, String comment, Integer userId, 
			Double amount, Integer sourceId) throws MiddlewareQueryException {
		
		List<Lot> lots = createLotsForAdd(gids, locationId, scaleId, comment, userId, amount, sourceId);
		return lots;
	}

	public List<Lot> buildForUpdate(List<Integer> gids, Integer locationId, Integer scaleId, String comment) throws MiddlewareQueryException {
		
		List<Lot> lots = createLotsForUpdate(gids, locationId, scaleId, comment);
		return lots;
	}

	public List<Lot> buildForSave(List<Integer> gids, Integer locationId, Integer scaleId, String comment, 
			Integer userId, Double amount, Integer sourceId) throws MiddlewareQueryException {
		
		requireLocalDatabaseInstance();
		List<Integer> newGids = removeGidsWithExistingCombination(gids, locationId, scaleId); 
		List<Lot> lots = createLotsForAdd(newGids, locationId, scaleId, comment, userId, amount, sourceId);
		return lots;
	}
	
	private List<Integer> removeGidsWithExistingCombination(List<Integer> gids, Integer locationId, Integer scaleId) 
			throws MiddlewareQueryException {
		
		List<Integer> newGids = new ArrayList<Integer>();
		
		List<Lot> existingLots = getLotDao().getByEntityTypeEntityIdsLocationIdAndScaleId(EntityType.GERMPLSM.name(), gids, locationId, scaleId);
		List<Integer> gidsWithExistingCombi = new ArrayList<Integer>();
		if (existingLots != null && !existingLots.isEmpty()) {
			for (Lot lot : existingLots) {
				gidsWithExistingCombi.add(lot.getEntityId());
			}
		}
		
		for (Integer gid : gids) {
			if (!gidsWithExistingCombi.contains(gid)) {
				newGids.add(gid);
			}
		}
		
		return newGids;
	}
	
	private List<Lot> createLotsForAdd(List<Integer> gids, Integer locationId, Integer scaleId, String comment, 
			Integer userId, Double amount, Integer sourceId) throws MiddlewareQueryException {
		List<Lot> lots = new ArrayList<Lot>();
		
        if (gids != null && !gids.isEmpty()) {
			for (Integer gid : gids) {
				
				lots.add(new Lot(null/*lotId*/, userId, EntityType.GERMPLSM.name(), gid, locationId, scaleId
						, LotStatus.ACTIVE.getIntValue(), LOT_NOT_DERIVED_FROM_ANOTHER, comment));
			}
		}
		
		return lots;
	}
	
	private List<Lot> createLotsForUpdate(List<Integer> gids, Integer locationId, Integer scaleId, String comment) throws MiddlewareQueryException {
		requireLocalDatabaseInstance();
		List<Lot> existingLots = getLotDao().getByEntityTypeAndEntityIds(EntityType.GERMPLSM.name(), gids);

        if (gids != null && !gids.isEmpty()) {
        	
			for (Lot lot : existingLots) {
				lot.setLocationId(locationId);
				lot.setScaleId(scaleId);
				lot.setComments(comment);
			}
		}
		
		return existingLots;
	}
	
	public LotsResult getGidsForUpdateAndAdd(List<Integer> gids) 
			throws MiddlewareQueryException {
		requireLocalDatabaseInstance();

		List<Integer> newGids = new ArrayList<Integer>();
		
		List<Lot> existingLots = getLotDao().getByEntityTypeAndEntityIds(EntityType.GERMPLSM.name(), gids);

		List<Integer> gidsWithExistingCombi = new ArrayList<Integer>();
		if (existingLots != null && !existingLots.isEmpty()) {
			for (Lot lot : existingLots) {
				gidsWithExistingCombi.add(lot.getEntityId());
			}
		}
		
		for (Integer gid : gids) {
			if (!gidsWithExistingCombi.contains(gid)) {
				newGids.add(gid);
			}
		}
		
		LotsResult result = new LotsResult();
		result.setGidsUpdated(gidsWithExistingCombi);
		result.setGidsAdded(newGids);
		return result;
	}

}
