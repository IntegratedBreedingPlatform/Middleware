/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.manager;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.dao.ims.LotDAO;
import org.generationcp.middleware.dao.ims.StockTransactionDAO;
import org.generationcp.middleware.dao.ims.TransactionDAO;
import org.generationcp.middleware.domain.gms.GermplasmListType;
import org.generationcp.middleware.domain.inventory.GermplasmInventory;
import org.generationcp.middleware.domain.inventory.InventoryDetails;
import org.generationcp.middleware.domain.inventory.ListEntryLotDetails;
import org.generationcp.middleware.domain.inventory.LotDetails;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.ReservedInventoryKey;
import org.generationcp.middleware.pojos.ims.StockTransaction;
import org.generationcp.middleware.pojos.report.LotReportRow;
import org.generationcp.middleware.pojos.report.TransactionReportRow;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.api.user.UserService;
import org.generationcp.middleware.util.Util;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Implementation of the InventoryDataManager interface. Most of the functions in this class only use the connection to the local instance,
 * this is because the lot and transaction tables only exist in a local instance.
 *
 * @author Kevin Manansala
 *
 */
@Transactional
public class InventoryDataManagerImpl extends DataManager implements InventoryDataManager {

	public static final String MID_STRING = "L";
	public static final int SUFFIX_LENGTH = 8;

	@Resource
	private UserService userService;

	private DaoFactory daoFactory;

	public InventoryDataManagerImpl() {
	}

	public InventoryDataManagerImpl(final HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	public InventoryDataManagerImpl(final HibernateSessionProvider sessionProvider, final String databaseName) {
		super(sessionProvider, databaseName);
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public List<Lot> getLotsByEntityType(final String type, final int start, final int numOfRows) {
		return this.daoFactory.getLotDao().getByEntityType(type, start, numOfRows);
	}

	@Override
	public long countLotsByEntityType(final String type) {
		return this.daoFactory.getLotDao().countByEntityType(type);
	}

	@Override
	public List<Lot> getLotsByEntityTypeAndEntityId(final String type, final Integer entityId, final int start, final int numOfRows) {
		return this.daoFactory.getLotDao().getByEntityTypeAndEntityId(type, entityId, start, numOfRows);
	}

	@Override
	public long countLotsByEntityTypeAndEntityId(final String type, final Integer entityId) {
		return this.daoFactory.getLotDao().countByEntityTypeAndEntityId(type, entityId);
	}

	@Override
	public List<Lot> getLotsByEntityTypeAndLocationId(final String type, final Integer locationId, final int start, final int numOfRows) {
		return this.daoFactory.getLotDao().getByEntityTypeAndLocationId(type, locationId, start, numOfRows);
	}

	@Override
	public long countLotsByEntityTypeAndLocationId(final String type, final Integer locationId) {
		return this.daoFactory.getLotDao().countByEntityTypeAndLocationId(type, locationId);
	}

	@Override
	public List<Lot> getLotsByEntityTypeAndEntityIdAndLocationId(final String type, final Integer entityId, final Integer locationId, final int start, final int numOfRows) {
		return this.daoFactory.getLotDao().getByEntityTypeAndEntityIdAndLocationId(type, entityId, locationId, start, numOfRows);
	}

	@Override
	public long countLotsByEntityTypeAndEntityIdAndLocationId(final String type, final Integer entityId, final Integer locationId) {
		return this.daoFactory.getLotDao().countByEntityTypeAndEntityIdAndLocationId(type, entityId, locationId);
	}

	@Override
	public Double getActualLotBalance(final Integer lotId) {
		return this.daoFactory.getLotDao().getActualLotBalance(lotId);
	}

	@Override
	public Double getAvailableLotBalance(final Integer lotId) {
		return this.daoFactory.getLotDao().getAvailableLotBalance(lotId);
	}

	@Override
	public Integer addLot(final Lot lot) {
		final List<Lot> lots = new ArrayList<>();
		lots.add(lot);
		final List<Integer> ids = this.addOrUpdateLot(lots, Operation.ADD);
		return !ids.isEmpty() ? ids.get(0) : null;
	}

	@Override
	public List<Integer> addLots(final List<Lot> lots) {
		return this.addOrUpdateLot(lots, Operation.ADD);
	}

	@Override
	public Integer updateLot(final Lot lot) {
		final List<Lot> lots = new ArrayList<>();
		lots.add(lot);
		final List<Integer> ids = this.addOrUpdateLot(lots, Operation.UPDATE);
		return !ids.isEmpty() ? ids.get(0) : null;
	}

	@Override
	public List<Integer> updateLots(final List<Lot> lots) {
		return this.addOrUpdateLot(lots, Operation.UPDATE);
	}

	@Override
	public Integer addStockTransaction(StockTransaction stockTransaction) {
		try {
			final StockTransactionDAO stockTransactionDAO = this.daoFactory.getStockTransactionDAO();
			stockTransaction = stockTransactionDAO.saveOrUpdate(stockTransaction);
			return stockTransaction.getId();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(e.getMessage(), e);
		}
	}

	private List<Integer> addOrUpdateLot(final List<Lot> lots, final Operation operation) {
		final List<Integer> idLotsSaved = new ArrayList<>();
		try {
			final LotDAO dao = this.daoFactory.getLotDao();
			for (final Lot lot : lots) {
				final Lot recordSaved = dao.saveOrUpdate(lot);
				idLotsSaved.add(recordSaved.getId());
			}
		} catch (final ConstraintViolationException e) {

			throw new MiddlewareQueryException(e.getMessage(), e);
		} catch (final MiddlewareQueryException e) {

			throw e;
		} catch (final Exception e) {

			throw new MiddlewareQueryException("Error encountered while saving Lot: InventoryDataManager.addOrUpdateLot(lots=" + lots
					+ ", operation=" + operation + "): " + e.getMessage(), e);
		}

		return idLotsSaved;
	}

	@Override
	public Integer addTransaction(final org.generationcp.middleware.pojos.ims.Transaction transaction) {
		final List<org.generationcp.middleware.pojos.ims.Transaction> transactions =
				new ArrayList<>();
		transactions.add(transaction);
		final List<Integer> ids = this.addTransactions(transactions);
		return !ids.isEmpty() ? ids.get(0) : null;
	}

	@Override
	public List<Integer> addTransactions(final List<org.generationcp.middleware.pojos.ims.Transaction> transactions) {
		return this.addOrUpdateTransaction(transactions, Operation.ADD);
	}

	@Override
	public Integer updateTransaction(final org.generationcp.middleware.pojos.ims.Transaction transaction) {
		final List<org.generationcp.middleware.pojos.ims.Transaction> transactions =
				new ArrayList<>();
		transactions.add(transaction);
		final List<Integer> ids = this.addOrUpdateTransaction(transactions, Operation.UPDATE);
		return !ids.isEmpty() ? ids.get(0) : null;
	}

	@Override
	public List<Integer> updateTransactions(final List<org.generationcp.middleware.pojos.ims.Transaction> transactions) {
		return this.addOrUpdateTransaction(transactions, Operation.UPDATE);
	}

	private List<Integer> addOrUpdateTransaction(final List<org.generationcp.middleware.pojos.ims.Transaction> transactions, final Operation operation) {
		final List<Integer> idTransactionsSaved = new ArrayList<>();
		try {
			

			final TransactionDAO dao = this.daoFactory.getTransactionDAO();

			for (final org.generationcp.middleware.pojos.ims.Transaction transaction : transactions) {
				final org.generationcp.middleware.pojos.ims.Transaction recordSaved = dao.saveOrUpdate(transaction);
				idTransactionsSaved.add(recordSaved.getId());
			}
			

		} catch (final Exception e) {

			throw new MiddlewareQueryException(
					"Error encountered while saving Transaction: InventoryDataManager.addOrUpdateTransaction(transactions=" + transactions
							+ ", operation=" + operation + "): " + e.getMessage(), e);
		}

		return idTransactionsSaved;
	}

	@Override
	public org.generationcp.middleware.pojos.ims.Transaction getTransactionById(final Integer id) {
		return this.daoFactory.getTransactionDAO().getById(id, false);
	}

	@Override
	public List<org.generationcp.middleware.pojos.ims.Transaction> getTransactionsByIdList(final List<Integer> idList) {
		return this.daoFactory.getTransactionDAO().filterByColumnValues("id", idList);
	}

	@Override
	public List<org.generationcp.middleware.pojos.ims.Lot> getLotsByIdList(final List<Integer> idList) {
		return this.daoFactory.getLotDao().filterByColumnValues("id", idList);
	}

	@Override
	public Set<org.generationcp.middleware.pojos.ims.Transaction> getTransactionsByLotId(final Integer id) {
		final Lot lot = this.daoFactory.getLotDao().getById(id, false);
		return lot.getTransactions();
	}

	@Override
	public List<org.generationcp.middleware.pojos.ims.Transaction> getAllTransactions(final int start, final int numOfRows) {
		return this.daoFactory.getTransactionDAO().getAll(start, numOfRows);
	}

	@Override
	public List<org.generationcp.middleware.pojos.ims.Transaction> getAllReserveTransactions(final int start, final int numOfRows) {
		return this.daoFactory.getTransactionDAO().getAllReserve(start, numOfRows);
	}

	@Override
	public long countAllReserveTransactions() {
		return this.daoFactory.getTransactionDAO().countAllReserve();
	}

	@Override
	public List<org.generationcp.middleware.pojos.ims.Transaction> getAllDepositTransactions(final int start, final int numOfRows) {
		return this.daoFactory.getTransactionDAO().getAllDeposit(start, numOfRows);
	}

	@Override
	public long countAllDepositTransactions() {
		return this.daoFactory.getTransactionDAO().countAllDeposit();
	}

	@Override
	public List<TransactionReportRow> generateReportOnAllUncommittedTransactions(final int start, final int numOfRows) {
		final List<TransactionReportRow> report = new ArrayList<>();

		final LocationDataManagerImpl locationManager = new LocationDataManagerImpl(this.getSessionProvider());
		final OntologyDataManagerImpl ontologyManager = new OntologyDataManagerImpl(this.getSessionProvider());

		final List<org.generationcp.middleware.pojos.ims.Transaction> transactions =
			this.daoFactory.getTransactionDAO().getAllUncommitted(start, numOfRows);

		for (final org.generationcp.middleware.pojos.ims.Transaction t : transactions) {
			final TransactionReportRow row = new TransactionReportRow();
			row.setDate(t.getTransactionDate());
			row.setQuantity(t.getQuantity());
			row.setCommentOfLot(t.getLot().getComments());
			row.setLotDate(t.getLot().getCreatedDate());

			final Term termScale = ontologyManager.getTermById(t.getLot().getScaleId());
			row.setScaleOfLot(termScale);

			final Location location = locationManager.getLocationByID(t.getLot().getLocationId());
			row.setLocationOfLot(location);

			report.add(row);
		}
		return report;
	}

	@Override
	public long countAllUncommittedTransactions() {
		return this.daoFactory.getTransactionDAO().countAllUncommitted();
	}

	@Override
	public List<TransactionReportRow> generateReportOnAllReserveTransactions(final int start, final int numOfRows) {

		final List<TransactionReportRow> report = new ArrayList<>();
		final LocationDataManagerImpl locationManager = new LocationDataManagerImpl(this.getSessionProvider());
		final OntologyDataManagerImpl ontologyManager = new OntologyDataManagerImpl(this.getSessionProvider());

		final List<org.generationcp.middleware.pojos.ims.Transaction> transactions = this.getAllReserveTransactions(start, numOfRows);
		for (final org.generationcp.middleware.pojos.ims.Transaction t : transactions) {
			final TransactionReportRow row = new TransactionReportRow();
			row.setDate(t.getTransactionDate());
			row.setQuantity(t.getQuantity());
			row.setCommentOfLot(t.getLot().getComments());
			row.setEntityIdOfLot(t.getLot().getEntityId());
			row.setLotDate(t.getLot().getCreatedDate());

			final Term termScale = ontologyManager.getTermById(t.getLot().getScaleId());
			row.setScaleOfLot(termScale);

			final Location location = locationManager.getLocationByID(t.getLot().getLocationId());
			row.setLocationOfLot(location);

			report.add(row);
		}

		return report;
	}

	@Override
	public long countAllWithdrawalTransactions() {
		return this.daoFactory.getTransactionDAO().countAllWithdrawals();
	}

	@Override
	public List<TransactionReportRow> generateReportOnAllWithdrawalTransactions(final int start, final int numOfRows) {
		final List<TransactionReportRow> report = new ArrayList<>();

		final LocationDataManagerImpl locationManager = new LocationDataManagerImpl(this.getSessionProvider());
		final OntologyDataManagerImpl ontologyManager = new OntologyDataManagerImpl(this.getSessionProvider());

		final List<org.generationcp.middleware.pojos.ims.Transaction> transactions =
			this.daoFactory.getTransactionDAO().getAllWithdrawals(start, numOfRows);
		for (final org.generationcp.middleware.pojos.ims.Transaction t : transactions) {
			final TransactionReportRow row = new TransactionReportRow();
			row.setDate(t.getTransactionDate());
			row.setQuantity(t.getQuantity());
			row.setCommentOfLot(t.getLot().getComments());
			row.setEntityIdOfLot(t.getLot().getEntityId());
			row.setLotDate(t.getLot().getCreatedDate());

			final Term termScale = ontologyManager.getTermById(t.getLot().getScaleId());
			row.setScaleOfLot(termScale);

			final Location location = locationManager.getLocationByID(t.getLot().getLocationId());
			row.setLocationOfLot(location);

			final Person person = this.userService.getPersonById(t.getPersonId());
			row.setPerson(person);

			report.add(row);
		}
		return report;
	}

	@Override
	public List<Lot> getAllLots(final int start, final int numOfRows) {
		return this.daoFactory.getLotDao().getAll(start, numOfRows);
	}

	@Override
	public long countAllLots() {
		return this.daoFactory.getLotDao().countAll();
	}

	@Override
	public List<LotReportRow> generateReportOnAllLots(final int start, final int numOfRows) {
		final List<Lot> allLots = this.getAllLots(start, numOfRows);
		return this.generateLotReportRows(allLots);
	}

	@Override
	public List<LotReportRow> generateReportOnDormantLots(final int year, final int start, final int numOfRows) {
		

		final SQLQuery query = this.getActiveSession().createSQLQuery(Lot.GENERATE_REPORT_ON_DORMANT);
		query.setParameter("year", year);
		query.setFirstResult(start);
		query.setMaxResults(numOfRows);

		final LocationDataManagerImpl locationManager = new LocationDataManagerImpl(this.getSessionProvider());
		final OntologyDataManagerImpl ontologyManager = new OntologyDataManagerImpl(this.getSessionProvider());
		final List<LotReportRow> report = new ArrayList<>();

		final List<?> results = query.list();
		for (final Object o : results) {
			final Object[] result = (Object[]) o;
			if (result != null) {
				final LotReportRow row = new LotReportRow();

				row.setLotId((Integer) result[0]);

				row.setEntityIdOfLot((Integer) result[1]);

				row.setActualLotBalance(((Double) result[2]).doubleValue());

				final Location location = locationManager.getLocationByID((Integer) result[3]);
				row.setLocationOfLot(location);

				final Term termScale = ontologyManager.getTermById((Integer) result[4]);
				row.setScaleOfLot(termScale);

				report.add(row);
			}
		}
		return report;
	}

	@Override
	public List<LotReportRow> generateReportOnEmptyLots(final int start, final int numOfRows) {
		final List<Lot> emptyLots = new ArrayList<>();
		for (final org.generationcp.middleware.pojos.ims.Transaction t : this.daoFactory.getTransactionDAO().getEmptyLot(start, numOfRows)) {
			emptyLots.add(t.getLot());
		}
		return this.generateLotReportRows(emptyLots);
	}

	@Override
	public List<LotReportRow> generateReportOnLotsWithMinimumAmount(final long minAmount, final int start, final int numOfRows) {
		final List<Lot> lotsWithMinimunAmount = new ArrayList<>();
		for (final org.generationcp.middleware.pojos.ims.Transaction t : this.daoFactory
			.getTransactionDAO().getLotWithMinimumAmount(minAmount, start,
				numOfRows)) {
			lotsWithMinimunAmount.add(t.getLot());
		}
		return this.generateLotReportRows(lotsWithMinimunAmount);
	}

	@Override
	public List<LotReportRow> generateReportOnLotsByEntityType(final String type, final int start, final int numOfRows) {
		final List<Lot> lotsByEntity = this.getLotsByEntityType(type, start, numOfRows);
		return this.generateLotReportRows(lotsByEntity);
	}

	@Override
	public List<LotReportRow> generateReportOnLotsByEntityTypeAndEntityId(final String type, final Integer entityId, final int start, final int numOfRows) {
		final List<Integer> entityIds = new ArrayList<>();
		entityIds.add(entityId);
		return this.generateReportOnLotsByEntityTypeAndEntityId(type, entityIds, start, numOfRows);
	}

	@Override
	public List<LotReportRow> generateReportOnLotsByEntityTypeAndEntityId(final String type, final List<Integer> entityIds, final int start, final int numOfRows) {
		final List<Lot> lotsByEntityTypeAndEntityId = new ArrayList<>();
		for (final Integer entityId : entityIds) {
			final List<Lot> lotsForEntityId = this.getLotsByEntityTypeAndEntityId(type, entityId, start, numOfRows);
			lotsByEntityTypeAndEntityId.addAll(lotsForEntityId);
		}
		return this.generateLotReportRows(lotsByEntityTypeAndEntityId);
	}

	private List<LotReportRow> generateLotReportRows(final List<Lot> listOfLots) {

		final LocationDataManagerImpl locationManager = new LocationDataManagerImpl(this.getSessionProvider());
		final OntologyDataManagerImpl ontologyManager = new OntologyDataManagerImpl(this.getSessionProvider());
		final List<LotReportRow> report = new ArrayList<>();
		for (final Lot lot : listOfLots) {
			final LotReportRow row = new LotReportRow();

			row.setLotId(lot.getId());

			row.setEntityIdOfLot(lot.getEntityId());

			final Double lotBalance = this.getActualLotBalance(lot.getId());
			row.setActualLotBalance(lotBalance);

			final Location location = locationManager.getLocationByID(lot.getLocationId());
			row.setLocationOfLot(location);

			final Term termScale = ontologyManager.getTermById(lot.getScaleId());
			row.setScaleOfLot(termScale);

			row.setCommentOfLot(lot.getComments());

			report.add(row);
		}
		return report;
	}

	private List<GermplasmListData> getGermplasmListDataByListId(final Integer id) {
		return this.daoFactory.getGermplasmListDataDAO().getByListId(id);
	}

	@Override
	public boolean transactionsExistForListProjectDataListID(final Integer listDataProjectListID) {
		return this.daoFactory.getStockTransactionDAO().listDataProjectListHasStockTransactions(listDataProjectListID);
	}

	@Override
	public List<ListEntryLotDetails> getLotDetailsForListEntry(final Integer listId, final Integer recordId, final Integer gid) {
		return this.getListInventoryBuilder().retrieveInventoryLotsForListEntry(listId, recordId, gid);
	}

	@Override
	public List<GermplasmListData> getLotDetailsForList(final Integer listId, final int start, final int numOfRows) {
		final List<GermplasmListData> listEntries = this.getGermplasmListDataByListId(listId);
		return this.getListInventoryBuilder().retrieveInventoryLotsForList(listId, start, numOfRows, listEntries);
	}

	@Override
	public List<GermplasmListData> getReservedLotDetailsForExportList(final Integer listId) {
		final List<GermplasmListData> listEntries = this.getGermplasmListDataByListId(listId);
		return this.getListInventoryBuilder().retrieveReservedInventoryLotsForList(listId, listEntries);
	}

	@Override
	public List<GermplasmListData> getLotCountsForList(final Integer id, final int start, final int numOfRows) {
		final List<GermplasmListData> listEntries = this.getGermplasmListDataByListId(id);
		return this.getListInventoryBuilder().retrieveLotCountsForList(listEntries);
	}

	@Override
	public void populateLotCountsIntoExistingList(final GermplasmList germplasmList) {
		this.getListInventoryBuilder().retrieveLotCountsForList(germplasmList.getListData());
	}
	
	@Override
	public Integer countLotsWithAvailableBalanceForGermplasm(final Integer gid) {
		return this.getListInventoryBuilder().countLotsWithAvailableBalanceForGermplasm(gid);
	}

	@Override
	public List<LotDetails> getLotDetailsForGermplasm(final Integer gid) {
		return this.getListInventoryBuilder().retrieveInventoryLotsForGermplasm(gid);
	}

	@Override
	public List<GermplasmListData> getLotCountsForListEntries(final Integer listId, final List<Integer> entryIds) {
		return this.getListInventoryBuilder().retrieveLotCountsForListEntries(listId, entryIds);
	}

	@Override
	public void cancelReservedInventory(final List<ReservedInventoryKey> lotEntries) {
		for (final ReservedInventoryKey entry : lotEntries) {
			final Integer lotId = entry.getLotId();
			final Integer lrecId = entry.getLrecId();

			this.daoFactory.getTransactionDAO().cancelReservationsForLotEntryAndLrecId(lotId, lrecId);
		}
	}

	@Override
	public boolean isStockIdExists(final List<String> stockIDs) {
		return this.daoFactory.getTransactionDAO().isStockIdExists(stockIDs);
	}

	@Override
	public List<String> getSimilarStockIds(final List<String> stockIDs) {
		return this.daoFactory.getTransactionDAO().getSimilarStockIds(stockIDs);
	}

	@Override
	public List<String> getStockIdsByListDataProjectListId(final Integer listId) {
		return this.daoFactory.getTransactionDAO().getStockIdsByListDataProjectListId(listId);
	}

	@Override
	public void updateInventory(final Integer listId, final List<InventoryDetails> inventoryDetailList) {


		try {

			final GermplasmList germplasmList = this.daoFactory.getGermplasmListDAO().getById(listId);
			final GermplasmListType germplasmListType = GermplasmListType.valueOf(germplasmList.getType());
			for (final InventoryDetails inventoryDetails : inventoryDetailList) {
				final Lot lot = this.daoFactory.getLotDao().getById(inventoryDetails.getLotId());
				lot.setLocationId(inventoryDetails.getLocationId());
				lot.setScaleId(inventoryDetails.getScaleId());
				this.daoFactory.getLotDao().saveOrUpdate(lot);
				final org.generationcp.middleware.pojos.ims.Transaction transaction = this.getTransactionById(inventoryDetails.getTrnId());
				transaction.setQuantity(Util.zeroIfNull(inventoryDetails.getAmount()));
				transaction.setComments(Util.nullIfEmpty(inventoryDetails.getComment()));
				if (GermplasmListType.isCrosses(germplasmListType)) {
					transaction.setBulkWith(Util.nullIfEmpty(inventoryDetails.getBulkWith()));
					transaction.setBulkCompl(Util.nullIfEmpty(inventoryDetails.getBulkCompl()));
				}
				this.daoFactory.getTransactionDAO().saveOrUpdate(transaction);
			}

		} catch (final Exception e) {

			this.logAndThrowException("Error encountered while updating inventory " + "of list id " + listId + "." + e.getMessage(), e);
		}
	}

	@Override
	public Lot getLotById(final Integer id) {
		return this.daoFactory.getLotDao().getById(id, false);
	}

	@Override
	public List<TransactionReportRow> getTransactionDetailsForLot(final Integer lotId) {
		final List<TransactionReportRow> transactionDetailsForLot = this.daoFactory.getTransactionDAO().getTransactionDetailsForLot(lotId);
		final List<Integer> userIds = Lists.transform(transactionDetailsForLot, new Function<TransactionReportRow, Integer>() {

			@Nullable
			@Override
			public Integer apply(@Nullable final TransactionReportRow input) {
				return input.getUserId();
			}
		});
		if (!userIds.isEmpty()) {
			final Map<Integer, String> userIDFullNameMap = this.userService.getUserIDFullNameMap(userIds);
			for (final TransactionReportRow row : transactionDetailsForLot) {
				if (row.getUserId() != null) {
					row.setUser(userIDFullNameMap.get(row.getUserId()));
				}
			}
		}
		return transactionDetailsForLot;
	}

	@Override
	public List<Germplasm> getAvailableBalanceForGermplasms(final List<Germplasm> germplasms) {
		final List<Integer> gids = new ArrayList<>();

		for(final Germplasm germplasm : germplasms) {
			gids.add(germplasm.getGid());
			germplasm.setInventoryInfo(new GermplasmInventory(germplasm.getGid()));
		}

		final Map<Integer, Object[]> availableBalanceCountAndTotalLotsCount =
			this.daoFactory.getLotDao().getAvailableBalanceCountAndTotalLotsCount(gids);

		for(final Germplasm germplasm : germplasms) {
			final Object[] availableBalanceValues = availableBalanceCountAndTotalLotsCount.get(germplasm.getGid());
			this.getListInventoryBuilder().setAvailableBalanceAndScale(germplasm.getInventoryInfo(), availableBalanceValues);
		}

		this.getListInventoryBuilder().setAvailableBalanceScaleForGermplasm(germplasms);
		return germplasms;
	}

	@Override
	public Map<Integer, String> retrieveStockIds(final List<Integer> gids){
		return this.daoFactory.getTransactionDAO().retrieveStockIds(gids);
	}

	@Override
	public void generateLotIds(final CropType crop, final List<Lot> lots) {
		Preconditions.checkNotNull(crop);
		Preconditions.checkState(!CollectionUtils.isEmpty(lots));

		final boolean doUseUUID = crop.isUseUUID();
		for (final Lot lot : lots) {
			if (lot.getLotUuId() == null) {
				if (doUseUUID) {
					lot.setLotUuId(UUID.randomUUID().toString());
				} else {
					final String cropPrefix = crop.getPlotCodePrefix();
					lot.setLotUuId(cropPrefix + MID_STRING
						+ RandomStringUtils.randomAlphanumeric(SUFFIX_LENGTH));
				}
			}
		}
	}
}
