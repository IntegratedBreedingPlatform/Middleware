/*******************************************************************************
 * Copyright (c) 2014, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/
package org.generationcp.middleware.service;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.inventory.InventoryDetails;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.Lot;
import org.generationcp.middleware.pojos.LotsResult;
import org.generationcp.middleware.service.api.InventoryService;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the API for inventory management system.
 *  
 */
public class InventoryServiceImpl extends Service implements InventoryService {
	
	private Logger LOG = LoggerFactory.getLogger(InventoryServiceImpl.class);

    public InventoryServiceImpl(
            HibernateSessionProvider sessionProviderForLocal,
            HibernateSessionProvider sessionProviderForCentral) {
        super(sessionProviderForLocal, sessionProviderForCentral);
    }

	@Override
	public List<InventoryDetails> getInventoryDetailsByGermplasmList(
			Integer listId) throws MiddlewareQueryException {
		return getInventoryDataManager().getInventoryDetailsByGermplasmList(listId);
	}

	@Override
	public List<InventoryDetails> getInventoryDetailsByGids(List<Integer> gids)
			throws MiddlewareQueryException {
		return getInventoryDataManager().getInventoryDetailsByGids(gids);
	}

	@Override
	public List<InventoryDetails> getInventoryDetailsByStudy(Integer studyId)
			throws MiddlewareQueryException {
		return getInventoryDataManager().getInventoryDetailsByGermplasmList(studyId);
	}


	@Override
	public LotsResult addLots(List<Integer> gids, Integer locationId, Integer scaleId, String comment, Integer userId) throws MiddlewareQueryException {
		List<Lot> lots = getLotBuilder().buildForSave(gids, locationId, scaleId, comment, userId);
		List<Integer> lotIdsAdded = new ArrayList<Integer>();
		try {
			lotIdsAdded = getInventoryDataManager().addLot(lots);
		} catch(MiddlewareQueryException e) {
	    	if (e.getCause() != null && e.getCause() instanceof ConstraintViolationException) {
	        	lotIdsAdded = getInventoryDataManager().addIndividualLots(lots);	
	    	}
	    	else {
	    		logAndThrowException(e.getMessage(), e, LOG);
	    	}
		}
		
		List<Integer> gidsAdded = new ArrayList<Integer>();
		if (lotIdsAdded != null) {
			for (Lot lot : lots) {
				if (lot.getId() != null && lotIdsAdded.contains(lot.getId())) {
					gidsAdded.add(lot.getEntityId());
				}
			}
		}
		List<Integer> gidsNotAdded = new ArrayList<Integer>();
		if (gids != null && !gids.isEmpty()) {
			for (Integer gid : gids) {
				if (!gidsAdded.contains(gid)) {
					gidsNotAdded.add(gid);
				}
			}
		}
		LotsResult result = new LotsResult();
		result.setGidsSkipped(gidsNotAdded);
		result.setGidsProcessed(gidsAdded);
		result.setLotIdsAdded(lotIdsAdded);
		return result;
	}
	
}
