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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
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
import org.generationcp.middleware.service.api.user.UserService;
import org.generationcp.middleware.util.Util;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import javax.annotation.Resource;

/**
 * Implementation of the InventoryDataManager interface. Most of the functions in this class only use the connection to the local instance,
 * this is because the lot and transaction tables only exist in a local instance.
 *
 * @author Kevin Manansala
 *
 */
@Transactional
public class InventoryDataManagerImpl extends DataManager implements InventoryDataManager {

	@Resource
	private UserService userService;

	private DaoFactory daoFactory;

	public InventoryDataManagerImpl() {
	}

	public InventoryDataManagerImpl(HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	public InventoryDataManagerImpl(HibernateSessionProvider sessionProvider, String databaseName) {
		super(sessionProvider, databaseName);
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public List<Lot> getLotsByEntityType(String type, int start, int numOfRows) throws MiddlewareQueryException {
		List<Lot> toReturn = new ArrayList<Lot>();
		toReturn = daoFactory.getLotDao().getByEntityType(type, start, numOfRows);
		return toReturn;
	}

	@Override
	public long countLotsByEntityType(String type) throws MiddlewareQueryException {
		return daoFactory.getLotDao().countByEntityType(type);
	}

	@Override
	public List<Lot> getLotsByEntityTypeAndEntityId(String type, Integer entityId, int start, int numOfRows)
			throws MiddlewareQueryException {
		return daoFactory.getLotDao().getByEntityTypeAndEntityId(type, entityId, start, numOfRows);
	}

	@Override
	public long countLotsByEntityTypeAndEntityId(String type, Integer entityId) throws MiddlewareQueryException {
		return daoFactory.getLotDao().countByEntityTypeAndEntityId(type, entityId);
	}

	@Override
	public List<Lot> getLotsByEntityTypeAndLocationId(String type, Integer locationId, int start, int numOfRows)
			throws MiddlewareQueryException {
		return daoFactory.getLotDao().getByEntityTypeAndLocationId(type, locationId, start, numOfRows);
	}

	@Override
	public long countLotsByEntityTypeAndLocationId(String type, Integer locationId) throws MiddlewareQueryException {
		return daoFactory.getLotDao().countByEntityTypeAndLocationId(type, locationId);
	}

	@Override
	public List<Lot> getLotsByEntityTypeAndEntityIdAndLocationId(String type, Integer entityId, Integer locationId, int start, int numOfRows)
			throws MiddlewareQueryException {
		return daoFactory.getLotDao().getByEntityTypeAndEntityIdAndLocationId(type, entityId, locationId, start, numOfRows);
	}

	@Override
	public long countLotsByEntityTypeAndEntityIdAndLocationId(String type, Integer entityId, Integer locationId)
			throws MiddlewareQueryException {
		return daoFactory.getLotDao().countByEntityTypeAndEntityIdAndLocationId(type, entityId, locationId);
	}

	@Override
	public Double getActualLotBalance(Integer lotId) throws MiddlewareQueryException {
		return daoFactory.getLotDao().getActualLotBalance(lotId);
	}

	@Override
	public Double getAvailableLotBalance(Integer lotId) throws MiddlewareQueryException {
		return daoFactory.getLotDao().getAvailableLotBalance(lotId);
	}

	@Override
	public Integer addLot(Lot lot) throws MiddlewareQueryException {
		List<Lot> lots = new ArrayList<Lot>();
		lots.add(lot);
		List<Integer> ids = this.addOrUpdateLot(lots, Operation.ADD);
		return !ids.isEmpty() ? ids.get(0) : null;
	}

	@Override
	public List<Integer> addLots(List<Lot> lots) throws MiddlewareQueryException {
		return this.addOrUpdateLot(lots, Operation.ADD);
	}

	@Override
	public Integer updateLot(Lot lot) throws MiddlewareQueryException {
		List<Lot> lots = new ArrayList<Lot>();
		lots.add(lot);
		List<Integer> ids = this.addOrUpdateLot(lots, Operation.UPDATE);
		return !ids.isEmpty() ? ids.get(0) : null;
	}

	@Override
	public List<Integer> updateLots(List<Lot> lots) throws MiddlewareQueryException {
		return this.addOrUpdateLot(lots, Operation.UPDATE);
	}

	@Override
	public Integer addStockTransaction(StockTransaction stockTransaction) throws MiddlewareQueryException {
		try {
			StockTransactionDAO stockTransactionDAO = daoFactory.getStockTransactionDAO();
			stockTransaction = stockTransactionDAO.saveOrUpdate(stockTransaction);
			return stockTransaction.getId();
		} catch (HibernateException e) {

			throw new MiddlewareQueryException(e.getMessage(), e);
		} catch (MiddlewareQueryException e) {

			throw e;
		}
	}

	private List<Integer> addOrUpdateLot(List<Lot> lots, Operation operation) throws MiddlewareQueryException {
		List<Integer> idLotsSaved = new ArrayList<Integer>();
		try {
			LotDAO dao = daoFactory.getLotDao();
			for (Lot lot : lots) {
				Lot recordSaved = dao.saveOrUpdate(lot);
				idLotsSaved.add(recordSaved.getId());
			}
		} catch (ConstraintViolationException e) {

			throw new MiddlewareQueryException(e.getMessage(), e);
		} catch (MiddlewareQueryException e) {

			throw e;
		} catch (Exception e) {

			throw new MiddlewareQueryException("Error encountered while saving Lot: InventoryDataManager.addOrUpdateLot(lots=" + lots
					+ ", operation=" + operation + "): " + e.getMessage(), e);
		}

		return idLotsSaved;
	}

	@Override
	public Integer addTransaction(org.generationcp.middleware.pojos.ims.Transaction transaction) throws MiddlewareQueryException {
		List<org.generationcp.middleware.pojos.ims.Transaction> transactions =
				new ArrayList<org.generationcp.middleware.pojos.ims.Transaction>();
		transactions.add(transaction);
		List<Integer> ids = this.addTransactions(transactions);
		return !ids.isEmpty() ? ids.get(0) : null;
	}

	@Override
	public List<Integer> addTransactions(List<org.generationcp.middleware.pojos.ims.Transaction> transactions)
			throws MiddlewareQueryException {
		return this.addOrUpdateTransaction(transactions, Operation.ADD);
	}

	@Override
	public Integer updateTransaction(org.generationcp.middleware.pojos.ims.Transaction transaction) throws MiddlewareQueryException {
		List<org.generationcp.middleware.pojos.ims.Transaction> transactions =
				new ArrayList<org.generationcp.middleware.pojos.ims.Transaction>();
		transactions.add(transaction);
		List<Integer> ids = this.addOrUpdateTransaction(transactions, Operation.UPDATE);
		return !ids.isEmpty() ? ids.get(0) : null;
	}

	@Override
	public List<Integer> updateTransactions(List<org.generationcp.middleware.pojos.ims.Transaction> transactions)
			throws MiddlewareQueryException {
		return this.addOrUpdateTransaction(transactions, Operation.UPDATE);
	}

	private List<Integer> addOrUpdateTransaction(List<org.generationcp.middleware.pojos.ims.Transaction> transactions, Operation operation)
			throws MiddlewareQueryException {

		List<Integer> idTransactionsSaved = new ArrayList<Integer>();
		try {
			

			TransactionDAO dao = daoFactory.getTransactionDAO();

			for (org.generationcp.middleware.pojos.ims.Transaction transaction : transactions) {
				org.generationcp.middleware.pojos.ims.Transaction recordSaved = dao.saveOrUpdate(transaction);
				idTransactionsSaved.add(recordSaved.getId());
			}
			

		} catch (Exception e) {

			throw new MiddlewareQueryException(
					"Error encountered while saving Transaction: InventoryDataManager.addOrUpdateTransaction(transactions=" + transactions
							+ ", operation=" + operation + "): " + e.getMessage(), e);
		}

		return idTransactionsSaved;
	}

	@Override
	public org.generationcp.middleware.pojos.ims.Transaction getTransactionById(Integer id) throws MiddlewareQueryException {
		return daoFactory.getTransactionDAO().getById(id, false);
	}

	@Override
	public List<org.generationcp.middleware.pojos.ims.Transaction> getTransactionsByIdList(List<Integer> idList) throws MiddlewareQueryException {
		return daoFactory.getTransactionDAO().filterByColumnValues("id", idList);
	}

	@Override
	public List<org.generationcp.middleware.pojos.ims.Lot> getLotsByIdList(List<Integer> idList) throws MiddlewareQueryException {
		return daoFactory.getLotDao().filterByColumnValues("id", idList);
	}

	@Override
	public Set<org.generationcp.middleware.pojos.ims.Transaction> getTransactionsByLotId(Integer id) throws MiddlewareQueryException {
		Lot lot = daoFactory.getLotDao().getById(id, false);
		return lot.getTransactions();
	}

	@Override
	public List<org.generationcp.middleware.pojos.ims.Transaction> getAllTransactions(int start, int numOfRows)
			throws MiddlewareQueryException {
		List<org.generationcp.middleware.pojos.ims.Transaction> transactions =
				new ArrayList<org.generationcp.middleware.pojos.ims.Transaction>();
		transactions = daoFactory.getTransactionDAO().getAll(start, numOfRows);
		return transactions;
	}

	@Override
	public List<org.generationcp.middleware.pojos.ims.Transaction> getAllReserveTransactions(int start, int numOfRows)
			throws MiddlewareQueryException {
		return daoFactory.getTransactionDAO().getAllReserve(start, numOfRows);
	}

	@Override
	public long countAllReserveTransactions() throws MiddlewareQueryException {
		return daoFactory.getTransactionDAO().countAllReserve();
	}

	@Override
	public List<org.generationcp.middleware.pojos.ims.Transaction> getAllDepositTransactions(int start, int numOfRows)
			throws MiddlewareQueryException {
		return daoFactory.getTransactionDAO().getAllDeposit(start, numOfRows);
	}

	@Override
	public long countAllDepositTransactions() throws MiddlewareQueryException {
		return daoFactory.getTransactionDAO().countAllDeposit();
	}

	@Override
	public List<org.generationcp.middleware.pojos.ims.Transaction> getAllReserveTransactionsByRequestor(Integer personId, int start,
			int numOfRows) throws MiddlewareQueryException {
		return daoFactory.getTransactionDAO().getAllReserveByRequestor(personId, start, numOfRows);
	}

	@Override
	public long countAllReserveTransactionsByRequestor(Integer personId) throws MiddlewareQueryException {
		return daoFactory.getTransactionDAO().countAllReserveByRequestor(personId);
	}

	@Override
	public List<org.generationcp.middleware.pojos.ims.Transaction> getAllDepositTransactionsByDonor(Integer personId, int start,
			int numOfRows) throws MiddlewareQueryException {
		return daoFactory.getTransactionDAO().getAllDepositByDonor(personId, start, numOfRows);
	}

	@Override
	public long countAllDepositTransactionsByDonor(Integer personId) throws MiddlewareQueryException {
		return daoFactory.getTransactionDAO().countAllDepositByDonor(personId);
	}

	@Override
	public List<TransactionReportRow> generateReportOnAllUncommittedTransactions(int start, int numOfRows) throws MiddlewareQueryException {
		List<TransactionReportRow> report = new ArrayList<TransactionReportRow>();

		LocationDataManagerImpl locationManager = new LocationDataManagerImpl(this.getSessionProvider());
		OntologyDataManagerImpl ontologyManager = new OntologyDataManagerImpl(this.getSessionProvider());

		List<org.generationcp.middleware.pojos.ims.Transaction> transactions = daoFactory.getTransactionDAO().getAllUncommitted(start, numOfRows);

		for (org.generationcp.middleware.pojos.ims.Transaction t : transactions) {
			TransactionReportRow row = new TransactionReportRow();
			row.setDate(t.getTransactionDate());
			row.setQuantity(t.getQuantity());
			row.setCommentOfLot(t.getLot().getComments());

			Term termScale = ontologyManager.getTermById(t.getLot().getScaleId());
			row.setScaleOfLot(termScale);

			Location location = locationManager.getLocationByID(t.getLot().getLocationId());
			row.setLocationOfLot(location);

			report.add(row);
		}
		return report;
	}

	@Override
	public long countAllUncommittedTransactions() throws MiddlewareQueryException {
		return daoFactory.getTransactionDAO().countAllUncommitted();
	}

	@Override
	public List<TransactionReportRow> generateReportOnAllReserveTransactions(int start, int numOfRows) throws MiddlewareQueryException {

		List<TransactionReportRow> report = new ArrayList<TransactionReportRow>();
		LocationDataManagerImpl locationManager = new LocationDataManagerImpl(this.getSessionProvider());
		OntologyDataManagerImpl ontologyManager = new OntologyDataManagerImpl(this.getSessionProvider());

		List<org.generationcp.middleware.pojos.ims.Transaction> transactions = this.getAllReserveTransactions(start, numOfRows);
		for (org.generationcp.middleware.pojos.ims.Transaction t : transactions) {
			TransactionReportRow row = new TransactionReportRow();
			row.setDate(t.getTransactionDate());
			row.setQuantity(t.getQuantity());
			row.setCommentOfLot(t.getLot().getComments());
			row.setEntityIdOfLot(t.getLot().getEntityId());

			Term termScale = ontologyManager.getTermById(t.getLot().getScaleId());
			row.setScaleOfLot(termScale);

			Location location = locationManager.getLocationByID(t.getLot().getLocationId());
			row.setLocationOfLot(location);

			report.add(row);
		}

		return report;
	}

	@Override
	public long countAllWithdrawalTransactions() throws MiddlewareQueryException {
		return daoFactory.getTransactionDAO().countAllWithdrawals();
	}

	@Override
	public List<TransactionReportRow> generateReportOnAllWithdrawalTransactions(int start, int numOfRows) throws MiddlewareQueryException {
		List<TransactionReportRow> report = new ArrayList<TransactionReportRow>();

		LocationDataManagerImpl locationManager = new LocationDataManagerImpl(this.getSessionProvider());
		OntologyDataManagerImpl ontologyManager = new OntologyDataManagerImpl(this.getSessionProvider());

		List<org.generationcp.middleware.pojos.ims.Transaction> transactions = daoFactory.getTransactionDAO().getAllWithdrawals(start, numOfRows);
		for (org.generationcp.middleware.pojos.ims.Transaction t : transactions) {
			TransactionReportRow row = new TransactionReportRow();
			row.setDate(t.getTransactionDate());
			row.setQuantity(t.getQuantity());
			row.setCommentOfLot(t.getLot().getComments());
			row.setEntityIdOfLot(t.getLot().getEntityId());

			Term termScale = ontologyManager.getTermById(t.getLot().getScaleId());
			row.setScaleOfLot(termScale);

			Location location = locationManager.getLocationByID(t.getLot().getLocationId());
			row.setLocationOfLot(location);

			Person person = this.getPersonById(t.getPersonId());
			row.setPerson(person);

			report.add(row);
		}
		return report;
	}

	private Person getPersonById(Integer id) throws MiddlewareQueryException {
		return daoFactory.getPersonDAO().getById(id, false);
	}

	@Override
	public List<Lot> getAllLots(int start, int numOfRows) throws MiddlewareQueryException {
		return daoFactory.getLotDao().getAll(start, numOfRows);
	}

	@Override
	public long countAllLots() throws MiddlewareQueryException {
		return daoFactory.getLotDao().countAll();
	}

	@Override
	public List<LotReportRow> generateReportOnAllLots(int start, int numOfRows) throws MiddlewareQueryException {
		List<Lot> allLots = this.getAllLots(start, numOfRows);
		return this.generateLotReportRows(allLots);
	}

	@Override
	public List<LotReportRow> generateReportOnDormantLots(int year, int start, int numOfRows) throws MiddlewareQueryException {
		

		SQLQuery query = this.getActiveSession().createSQLQuery(Lot.GENERATE_REPORT_ON_DORMANT);
		query.setParameter("year", year);
		query.setFirstResult(start);
		query.setMaxResults(numOfRows);

		LocationDataManagerImpl locationManager = new LocationDataManagerImpl(this.getSessionProvider());
		OntologyDataManagerImpl ontologyManager = new OntologyDataManagerImpl(this.getSessionProvider());
		List<LotReportRow> report = new ArrayList<LotReportRow>();

		List<?> results = query.list();
		for (Object o : results) {
			Object[] result = (Object[]) o;
			if (result != null) {
				LotReportRow row = new LotReportRow();

				row.setLotId((Integer) result[0]);

				row.setEntityIdOfLot((Integer) result[1]);

				row.setActualLotBalance(((Double) result[2]).doubleValue());

				Location location = locationManager.getLocationByID((Integer) result[3]);
				row.setLocationOfLot(location);

				Term termScale = ontologyManager.getTermById((Integer) result[4]);
				row.setScaleOfLot(termScale);

				report.add(row);
			}
		}
		return report;
	}

	@Override
	public List<LotReportRow> generateReportOnEmptyLots(int start, int numOfRows) throws MiddlewareQueryException {
		List<Lot> emptyLots = new ArrayList<Lot>();
		for (org.generationcp.middleware.pojos.ims.Transaction t : daoFactory.getTransactionDAO().getEmptyLot(start, numOfRows)) {
			emptyLots.add(t.getLot());
		}
		return this.generateLotReportRows(emptyLots);
	}

	@Override
	public List<LotReportRow> generateReportOnLotsWithMinimumAmount(long minAmount, int start, int numOfRows)
			throws MiddlewareQueryException {
		List<Lot> lotsWithMinimunAmount = new ArrayList<Lot>();
		for (org.generationcp.middleware.pojos.ims.Transaction t : daoFactory.getTransactionDAO().getLotWithMinimumAmount(minAmount, start,
				numOfRows)) {
			lotsWithMinimunAmount.add(t.getLot());
		}
		return this.generateLotReportRows(lotsWithMinimunAmount);
	}

	@Override
	public List<LotReportRow> generateReportOnLotsByEntityType(String type, int start, int numOfRows) throws MiddlewareQueryException {
		List<Lot> lotsByEntity = this.getLotsByEntityType(type, start, numOfRows);
		return this.generateLotReportRows(lotsByEntity);
	}

	@Override
	public List<LotReportRow> generateReportOnLotsByEntityTypeAndEntityId(String type, Integer entityId, int start, int numOfRows)
			throws MiddlewareQueryException {
		List<Integer> entityIds = new ArrayList<Integer>();
		entityIds.add(entityId);
		return this.generateReportOnLotsByEntityTypeAndEntityId(type, entityIds, start, numOfRows);
	}

	@Override
	public List<LotReportRow> generateReportOnLotsByEntityTypeAndEntityId(String type, List<Integer> entityIds, int start, int numOfRows)
			throws MiddlewareQueryException {
		List<Lot> lotsByEntityTypeAndEntityId = new ArrayList<Lot>();
		for (Integer entityId : entityIds) {
			List<Lot> lotsForEntityId = this.getLotsByEntityTypeAndEntityId(type, entityId, start, numOfRows);
			lotsByEntityTypeAndEntityId.addAll(lotsForEntityId);
		}
		return this.generateLotReportRows(lotsByEntityTypeAndEntityId);
	}

	private List<LotReportRow> generateLotReportRows(List<Lot> listOfLots) throws MiddlewareQueryException {

		LocationDataManagerImpl locationManager = new LocationDataManagerImpl(this.getSessionProvider());
		OntologyDataManagerImpl ontologyManager = new OntologyDataManagerImpl(this.getSessionProvider());
		List<LotReportRow> report = new ArrayList<LotReportRow>();
		for (Lot lot : listOfLots) {
			LotReportRow row = new LotReportRow();

			row.setLotId(lot.getId());

			row.setEntityIdOfLot(lot.getEntityId());

			Double lotBalance = this.getActualLotBalance(lot.getId());
			row.setActualLotBalance(lotBalance);

			Location location = locationManager.getLocationByID(lot.getLocationId());
			row.setLocationOfLot(location);

			Term termScale = ontologyManager.getTermById(lot.getScaleId());
			row.setScaleOfLot(termScale);

			row.setCommentOfLot(lot.getComments());

			report.add(row);
		}
		return report;
	}

	private List<GermplasmListData> getGermplasmListDataByListId(Integer id) throws MiddlewareQueryException {
		return daoFactory.getGermplasmListDataDAO().getByListId(id);
	}

	@Override
	public boolean transactionsExistForListProjectDataListID(Integer listDataProjectListID) throws MiddlewareQueryException {

		return daoFactory.getStockTransactionDAO().listDataProjectListHasStockTransactions(listDataProjectListID);
	}

	@Override
	public List<ListEntryLotDetails> getLotDetailsForListEntry(Integer listId, Integer recordId, Integer gid)
			throws MiddlewareQueryException {
		return this.getListInventoryBuilder().retrieveInventoryLotsForListEntry(listId, recordId, gid);
	}

	@Override
	public List<GermplasmListData> getLotDetailsForList(Integer listId, int start, int numOfRows) throws MiddlewareQueryException {
		List<GermplasmListData> listEntries = this.getGermplasmListDataByListId(listId);
		return this.getListInventoryBuilder().retrieveInventoryLotsForList(listId, start, numOfRows, listEntries);
	}

	@Override
	public List<GermplasmListData> getReservedLotDetailsForExportList(Integer listId) throws MiddlewareQueryException {
		List<GermplasmListData> listEntries = this.getGermplasmListDataByListId(listId);
		return this.getListInventoryBuilder().retrieveReservedInventoryLotsForList(listId, listEntries);
	}

	@Override
	public List<GermplasmListData> getLotCountsForList(Integer id, int start, int numOfRows) throws MiddlewareQueryException {
		List<GermplasmListData> listEntries = this.getGermplasmListDataByListId(id);
		return this.getListInventoryBuilder().retrieveLotCountsForList(listEntries);
	}

	@Override
	public void populateLotCountsIntoExistingList(final GermplasmList germplasmList) throws MiddlewareQueryException {
		this.getListInventoryBuilder().retrieveLotCountsForList(germplasmList.getListData());
	}
	
	@Override
	public Integer countLotsWithAvailableBalanceForGermplasm(Integer gid) throws MiddlewareQueryException {
		return this.getListInventoryBuilder().countLotsWithAvailableBalanceForGermplasm(gid);
	}

	@Override
	public List<LotDetails> getLotDetailsForGermplasm(Integer gid) throws MiddlewareQueryException {
		return this.getListInventoryBuilder().retrieveInventoryLotsForGermplasm(gid);
	}

	@Override
	public List<GermplasmListData> getLotCountsForListEntries(Integer listId, List<Integer> entryIds) throws MiddlewareQueryException {
		return this.getListInventoryBuilder().retrieveLotCountsForListEntries(listId, entryIds);
	}

	@Override
	public void cancelReservedInventory(List<ReservedInventoryKey> lotEntries) throws MiddlewareQueryException {
		for (ReservedInventoryKey entry : lotEntries) {
			Integer lotId = entry.getLotId();
			Integer lrecId = entry.getLrecId();

			daoFactory.getTransactionDAO().cancelReservationsForLotEntryAndLrecId(lotId, lrecId);
		}
	}

	@Override
	public boolean isStockIdExists(List<String> stockIDs) throws MiddlewareQueryException {
		return daoFactory.getTransactionDAO().isStockIdExists(stockIDs);
	}

	@Override
	public List<String> getSimilarStockIds(List<String> stockIDs) throws MiddlewareQueryException {
		return daoFactory.getTransactionDAO().getSimilarStockIds(stockIDs);
	}

	@Override
	public List<String> getStockIdsByListDataProjectListId(Integer listId) throws MiddlewareQueryException {
		return daoFactory.getTransactionDAO().getStockIdsByListDataProjectListId(listId);
	}

	@Override
	public void updateInventory(Integer listId, List<InventoryDetails> inventoryDetailList) throws MiddlewareQueryException {


		try {

			GermplasmList germplasmList = daoFactory.getGermplasmListDAO().getById(listId);
			GermplasmListType germplasmListType = GermplasmListType.valueOf(germplasmList.getType());
			for (InventoryDetails inventoryDetails : inventoryDetailList) {
				Lot lot = daoFactory.getLotDao().getById(inventoryDetails.getLotId());
				lot.setLocationId(inventoryDetails.getLocationId());
				lot.setScaleId(inventoryDetails.getScaleId());
				daoFactory.getLotDao().saveOrUpdate(lot);
				org.generationcp.middleware.pojos.ims.Transaction transaction = this.getTransactionById(inventoryDetails.getTrnId());
				transaction.setQuantity(Util.zeroIfNull(inventoryDetails.getAmount()));
				transaction.setComments(Util.nullIfEmpty(inventoryDetails.getComment()));
				if (GermplasmListType.isCrosses(germplasmListType)) {
					transaction.setBulkWith(Util.nullIfEmpty(inventoryDetails.getBulkWith()));
					transaction.setBulkCompl(Util.nullIfEmpty(inventoryDetails.getBulkCompl()));
				}
				daoFactory.getTransactionDAO().saveOrUpdate(transaction);
			}

		} catch (Exception e) {

			this.logAndThrowException("Error encountered while updating inventory " + "of list id " + listId + "." + e.getMessage(), e);
		}
	}

	@Override
	public Lot getLotById(Integer id) throws MiddlewareQueryException {
		return daoFactory.getLotDao().getById(id, false);
	}

	@Override
	public List<TransactionReportRow> getTransactionDetailsForLot(Integer lotId) throws MiddlewareQueryException {
		final List<TransactionReportRow> transactionDetailsForLot = daoFactory.getTransactionDAO().getTransactionDetailsForLot(lotId);
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
	public List<Germplasm> getAvailableBalanceForGermplasms(List<Germplasm> germplasms) throws MiddlewareQueryException {
		List<Integer> gids = new ArrayList<>();

		for(Germplasm germplasm : germplasms) {
			gids.add(germplasm.getGid());
			germplasm.setInventoryInfo(new GermplasmInventory(germplasm.getGid()));
		}

		Map<Integer, Object[]> availableBalanceCountAndTotalLotsCount = daoFactory.getLotDao().getAvailableBalanceCountAndTotalLotsCount(gids);

		for(Germplasm germplasm : germplasms) {
			Object[] availableBalanceValues = availableBalanceCountAndTotalLotsCount.get(germplasm.getGid());
			this.getListInventoryBuilder().setAvailableBalanceAndScale(germplasm.getInventoryInfo(), availableBalanceValues);
		}

		this.getListInventoryBuilder().setAvailableBalanceScaleForGermplasm(germplasms);
		return germplasms;
	}

	@Override
	public Map<Integer, String> retrieveStockIds(List<Integer> gids){
		return daoFactory.getTransactionDAO().retrieveStockIds(gids);
	}

}
