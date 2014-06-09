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
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.LotsResult;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.generationcp.middleware.service.api.InventoryService;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the API for inventory management system.
 *  
 */
public class InventoryServiceImpl extends Service implements InventoryService {
    
    private static final Logger LOG = LoggerFactory.getLogger(InventoryServiceImpl.class);

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
	public LotsResult addAdvanceLots(List<Integer> gids, Integer locationId, Integer scaleId, String comment, 
			Integer userId, Double amount, Integer sourceId) throws MiddlewareQueryException {
		
		requireLocalDatabaseInstance();
		
		LotsResult result  = getLotBuilder().getGidsForUpdateAndAdd(gids);
		List<Integer> newGids = result.getGidsAdded();
		List<Integer> existingGids = result.getGidsUpdated();
		
		// Save lots
		List<Lot> lots = getLotBuilder().build(newGids, locationId, scaleId, comment, userId, amount, sourceId);
		List<Integer> lotIdsAdded = new ArrayList<Integer>();
		try {
			lotIdsAdded = getInventoryDataManager().addLots(lots);
		} catch(MiddlewareQueryException e) {
	    	if (e.getCause() != null && e.getCause() instanceof ConstraintViolationException) {
	        	lotIdsAdded = getInventoryDataManager().addLots(lots); 	
	    	}
	    	else {
	    		logAndThrowException(e.getMessage(), e, LOG);
	    	}
		}
		result.setLotIdsAdded(lotIdsAdded);
		
		// Update existing lots
		List<Lot> existingLots = getLotBuilder().buildForUpdate(existingGids, locationId, scaleId, comment);
		List<Integer> lotIdsUpdated = new ArrayList<Integer>();
		try {
			lotIdsUpdated = getInventoryDataManager().updateLots(existingLots);
		} catch(MiddlewareQueryException e) {
	    	if (e.getCause() != null && e.getCause() instanceof ConstraintViolationException) {
	    		lotIdsUpdated = getInventoryDataManager().updateLots(lots); 	
	    	}
	    	else {
	    		logAndThrowException(e.getMessage(), e, LOG);
	    	}
		}
		result.setLotIdsUpdated(lotIdsUpdated);
		

		// Update existing transactions - for existing gids
		List<Transaction> transactionsForUpdate = getTransactionBuilder(). buildForUpdate(existingLots, amount);
		getInventoryDataManager().updateTransactions(transactionsForUpdate);

		// Add new transactions - for non-existing gid/location/scale combination
		List<Transaction> transactionsForAdd = getTransactionBuilder(). buildForSave(lots, amount, userId, comment, sourceId);
		getInventoryDataManager().addTransactions(transactionsForAdd);
		
		return result;
	}
	
}
