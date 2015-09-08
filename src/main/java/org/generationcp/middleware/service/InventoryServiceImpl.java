/**
 * **************************************************************************** Copyright (c) 2014, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 * <p/>
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * <p/>
 * *****************************************************************************
 */

package org.generationcp.middleware.service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.generationcp.middleware.dao.ims.StockTransactionDAO;
import org.generationcp.middleware.dao.ims.TransactionDAO;
import org.generationcp.middleware.domain.gms.GermplasmListType;
import org.generationcp.middleware.domain.inventory.InventoryDetails;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.operation.builder.LotBuilder;
import org.generationcp.middleware.operation.builder.TransactionBuilder;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.ListDataProject;
import org.generationcp.middleware.pojos.ims.EntityType;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.StockTransaction;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.generationcp.middleware.service.api.InventoryService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This is the API for inventory management system.
 *
 */

@Transactional
public class InventoryServiceImpl extends Service implements InventoryService {

	private TransactionDAO transactionDAO;
	private InventoryDataManager inventoryDataManager;
	private StockTransactionDAO stockTransactionDAO;
	private LotBuilder lotBuilder;
	private TransactionBuilder transactionBuilder;

	public InventoryServiceImpl() {
		super();
		this.transactionDAO = this.getTransactionDao();
		this.inventoryDataManager = this.getInventoryDataManager();
		this.stockTransactionDAO = this.getStockTransactionDAO();
		this.lotBuilder = this.getLotBuilder();
		this.transactionBuilder = this.getTransactionBuilder();
	}

	public InventoryServiceImpl(HibernateSessionProvider sessionProvider, String localDatabaseName) {
		super(sessionProvider, localDatabaseName);
		this.transactionDAO = this.getTransactionDao();
		this.inventoryDataManager = this.getInventoryDataManager();
		this.stockTransactionDAO = this.getStockTransactionDAO();
		this.lotBuilder = this.getLotBuilder();
		this.transactionBuilder = this.getTransactionBuilder();
	}

	public InventoryServiceImpl(HibernateSessionProvider sessionProvider, TransactionDAO transactionDAO,
			InventoryDataManager inventoryDataManager, StockTransactionDAO stockTransactionDAO, LotBuilder lotBuilder,
			TransactionBuilder transactionBuilder) {
		super(sessionProvider);
		this.transactionDAO = transactionDAO;
		this.inventoryDataManager = inventoryDataManager;
		this.stockTransactionDAO = stockTransactionDAO;
		this.lotBuilder = lotBuilder;
		this.transactionBuilder = transactionBuilder;
	}

	@Override
	public List<InventoryDetails> getInventoryDetailsByGermplasmList(Integer listId) throws MiddlewareQueryException {
		return this.inventoryDataManager.getInventoryDetailsByGermplasmList(listId);
	}

	@Override
	public List<InventoryDetails> getInventoryDetailsByGids(List<Integer> gids) throws MiddlewareQueryException {
		return this.inventoryDataManager.getInventoryDetailsByGids(gids);
	}

	@Override
	public List<InventoryDetails> getInventoryDetailsByStudy(Integer studyId) throws MiddlewareQueryException {
		return this.inventoryDataManager.getInventoryDetailsByGermplasmList(studyId);
	}

	@Override
	public List<InventoryDetails> getInventoryDetailsByGermplasmList(Integer listId, String germplasmListType)
			throws MiddlewareQueryException {
		return this.inventoryDataManager.getInventoryDetailsByGermplasmList(listId, germplasmListType);
	}

	@Override
	public Integer getCurrentNotationNumberForBreederIdentifier(String breederIdentifier) throws MiddlewareQueryException {
		List<String> inventoryIDs = this.transactionDAO.getInventoryIDsWithBreederIdentifier(breederIdentifier);

		if (inventoryIDs == null || inventoryIDs.isEmpty()) {
			return 0;
		}

		String expression = breederIdentifier + "([0-9]+)";
		Pattern pattern = Pattern.compile(expression);

		return this.findCurrentMaxNotationNumberInInventoryIDs(inventoryIDs, pattern);

	}

	protected Integer findCurrentMaxNotationNumberInInventoryIDs(List<String> inventoryIDs, Pattern pattern) {
		Integer currentMax = 0;

		for (String inventoryID : inventoryIDs) {
			Matcher matcher = pattern.matcher(inventoryID);
			matcher.find();
			// Matcher.group(1) is needed because group(0) includes the identifier in the match
			// Matcher.group(1) only captures the value inside the parenthesis
			currentMax = Math.max(currentMax, Integer.valueOf(matcher.group(1)));
		}

		return currentMax;
	}

	@Override
	public void addLotAndTransaction(InventoryDetails details, GermplasmListData listData, ListDataProject listDataProject)
			throws MiddlewareQueryException {
		Lot existingLot =
				this.inventoryDataManager.getLotByEntityTypeAndEntityIdAndLocationIdAndScaleId(EntityType.GERMPLSM.name(),
						details.getGid(), details.getLocationId(), details.getScaleId());

		if (existingLot != null) {
			throw new MiddlewareQueryException("A lot with the same entity id, location id, and scale id already exists");
		}

		Lot lot = this.lotBuilder.createLotForAdd(details.getGid(), details.getLocationId(), details.getScaleId(), details.getComment(),
				details.getUserId());
		this.inventoryDataManager.addLot(lot);

		Transaction transaction = this.transactionBuilder.buildForAdd(lot, listData == null ? 0 : listData.getId(), details.getAmount(),
				details.getUserId(), details.getComment(), details.getSourceId(), details.getInventoryID(), details.getBulkWith(),
				details.getBulkCompl());
		this.inventoryDataManager.addTransaction(transaction);

		StockTransaction stockTransaction = new StockTransaction(null, listDataProject, transaction);
		stockTransaction.setSourceRecordId(transaction.getSourceRecordId());
		this.inventoryDataManager.addStockTransaction(stockTransaction);
	}

	@Override
	public List<InventoryDetails> getInventoryListByListDataProjectListId(Integer listDataProjectListId, GermplasmListType type)
			throws MiddlewareQueryException {
		return this.stockTransactionDAO.retrieveInventoryDetailsForListDataProjectListId(listDataProjectListId, type);
	}

	@Override
	public List<InventoryDetails> getSummedInventoryListByListDataProjectListId(Integer listDataProjectListId, GermplasmListType type)
			throws MiddlewareQueryException {
		return this.stockTransactionDAO.retrieveSummedInventoryDetailsForListDataProjectListId(listDataProjectListId, type);
	}

	@Override
	public boolean stockHasCompletedBulking(Integer listId) throws MiddlewareQueryException {
		return this.stockTransactionDAO.stockHasCompletedBulking(listId);
	}
}
