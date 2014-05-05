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

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.EntityType;
import org.generationcp.middleware.pojos.Lot;
import org.generationcp.middleware.pojos.LotStatus;

public class LotBuilder extends Builder {

	public LotBuilder(HibernateSessionProvider sessionProviderForLocal,
			HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	public List<Lot> build(List<Integer> gids, Integer locationId, Integer scaleId, String comment, Integer userId) throws MiddlewareQueryException {
		
		List<Lot> lots = createLotsForAdd(gids, locationId, scaleId, comment, userId);
		return lots;
	}

	public List<Lot> buildForSave(List<Integer> gids, Integer locationId, Integer scaleId, String comment, Integer userId) throws MiddlewareQueryException {
		
		requireLocalDatabaseInstance();
		List<Integer> newGids = removeGidsWithExistingCombination(gids, locationId, scaleId);
		List<Lot> lots = createLotsForAdd(newGids, locationId, scaleId, comment, userId);
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
	
	private List<Lot> createLotsForAdd(List<Integer> gids, Integer locationId, Integer scaleId, String comment, Integer userId) throws MiddlewareQueryException {
		List<Lot> lots = new ArrayList<Lot>();
		
		if (gids != null && !gids.isEmpty()) {
			for (Integer gid : gids) {
				lots.add(new Lot(null/*lotId*/, userId, EntityType.GERMPLSM.name(), gid, locationId, scaleId, LotStatus.ACTIVE.getIntValue(), comment));
			}
		}
		
		return lots;
	}
}
