/**
 * ****************************************************************************
 * Copyright (c) 2014, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 * <p/>
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * <p/>
 * *****************************************************************************
 */
package org.generationcp.middleware.service;

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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is the API for inventory management system.
 *
 */
public class InventoryServiceImpl extends Service implements InventoryService {

	private static final Logger LOG = LoggerFactory.getLogger(InventoryServiceImpl.class);

	public InventoryServiceImpl(HibernateSessionProvider sessionProvider,
			String localDatabaseName) {
		super(sessionProvider, localDatabaseName);
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
	public LotsResult addLotsForList(List<Integer> gids, Integer locationId, Integer scaleId,
			String comment,
			Integer userId, Double amount, Integer sourceId) throws MiddlewareQueryException {

		LotsResult result = getLotBuilder().getGidsForUpdateAndAdd(gids);
		List<Integer> newGids = result.getGidsAdded();
		List<Integer> existingGids = result.getGidsUpdated();

		// Save lots
		List<Lot> lots = getLotBuilder()
				.build(newGids, locationId, scaleId, comment, userId, amount, sourceId);
		List<Integer> lotIdsAdded = new ArrayList<Integer>();
		try {
			lotIdsAdded = getInventoryDataManager().addLots(lots);
		} catch (MiddlewareQueryException e) {
			if (e.getCause() != null && e.getCause() instanceof ConstraintViolationException) {
				lotIdsAdded = getInventoryDataManager().addLots(lots);
			} else {
				logAndThrowException(e.getMessage(), e, LOG);
			}
		}
		result.setLotIdsAdded(lotIdsAdded);

		// Update existing lots
		List<Lot> existingLots = getLotBuilder()
				.buildForUpdate(existingGids, locationId, scaleId, comment);
		List<Integer> lotIdsUpdated = new ArrayList<Integer>();
		try {
			lotIdsUpdated = getInventoryDataManager().updateLots(existingLots);
		} catch (MiddlewareQueryException e) {
			if (e.getCause() != null && e.getCause() instanceof ConstraintViolationException) {
				lotIdsUpdated = getInventoryDataManager().updateLots(lots);
			} else {
				logAndThrowException(e.getMessage(), e, LOG);
			}
		}
		result.setLotIdsUpdated(lotIdsUpdated);

		// Update existing transactions - for existing gids
		List<Transaction> transactionsForUpdate = getTransactionBuilder()
				.buildForUpdate(existingLots, amount, comment);
		getInventoryDataManager().updateTransactions(transactionsForUpdate);

		// Add new transactions - for non-existing gid/location/scale combination
		List<Transaction> transactionsForAdd = getTransactionBuilder()
				.buildForSave(lots, amount, userId, comment, sourceId);
		getInventoryDataManager().addTransactions(transactionsForAdd);

		return result;
	}

	@Override
	public List<InventoryDetails> getInventoryDetailsByGermplasmList(Integer listId,
			String germplasmListType) throws MiddlewareQueryException {
		return getInventoryDataManager()
				.getInventoryDetailsByGermplasmList(listId, germplasmListType);
	}

	@Override
	public Integer getCurrentNotationNumberForBreederIdentifier(
			String breederIdentifier) throws MiddlewareQueryException {
		List<String> inventoryIDs = getTransactionDao()
				.getInventoryIDsWithBreederIdentifier(breederIdentifier);

		if (inventoryIDs == null || inventoryIDs.isEmpty()) {
			return 0;
		}

		String expression = breederIdentifier + "([0-9]+)";
		Pattern pattern = Pattern.compile(expression);

		return findCurrentMaxNotationNumberInInventoryIDs(inventoryIDs, pattern);

	}

	protected Integer findCurrentMaxNotationNumberInInventoryIDs(List<String> inventoryIDs,
			Pattern pattern) {
		Integer currentMax = 0;

		for (String inventoryID : inventoryIDs) {
			Matcher matcher = pattern.matcher(inventoryID);
			if (matcher.find()) {
				// Matcher.group(1) is needed because group(0) includes the identifier in the match
				// Matcher.group(1) only captures the value inside the parenthesis
				currentMax = Math.max(currentMax, Integer.valueOf(matcher.group(1)));
			}

		}

		return currentMax;
	}

}
