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

import org.generationcp.middleware.domain.gms.GermplasmListType;
import org.generationcp.middleware.domain.inventory.InventoryDetails;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.ListDataProject;
import org.generationcp.middleware.pojos.ims.*;
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

	@Override
	public void addLotAndTransaction(InventoryDetails details, GermplasmListData listData, ListDataProject listDataProject) throws MiddlewareQueryException {
		Lot existingLot = getInventoryDataManager().getLotByEntityTypeAndEntityIdAndLocationIdAndScaleId(EntityType.GERMPLSM.name(), details.getGid(), details.getLocationId(), details.getScaleId());

		if (existingLot != null) {
			throw new MiddlewareQueryException("A lot with the same entity id, location id, and scale id already exists");
		}

		Lot lot = getLotBuilder().createLotForAdd(details.getGid(), details.getLocationId(), details.getScaleId(), details.getComment(), details.getUserId());
		getInventoryDataManager().addLot(lot);

		Transaction transaction = getTransactionBuilder().buildForAdd(lot, listData == null ? 0 : listData.getId(),
				details.getAmount(), details.getUserId(), details.getComment(), details.getSourceId(), details.getInventoryID());
		getInventoryDataManager().addTransaction(transaction);

		StockTransaction stockTransaction = new StockTransaction(null, listDataProject, transaction);
		getInventoryDataManager().addStockTransaction(stockTransaction);
	}

	@Override
	public List<InventoryDetails> getInventoryListByListDataProjectListId(Integer listDataProjectListId, GermplasmListType type) throws MiddlewareQueryException {
		return getStockTransactionDAO().retrieveInventoryDetailsForListDataProjectListId(listDataProjectListId, type);
	}
}