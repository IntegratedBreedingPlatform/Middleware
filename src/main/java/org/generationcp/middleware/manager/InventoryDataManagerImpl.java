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
package org.generationcp.middleware.manager;

import org.generationcp.middleware.dao.ims.LotDAO;
import org.generationcp.middleware.dao.ims.StockTransactionDAO;
import org.generationcp.middleware.dao.ims.TransactionDAO;
import org.generationcp.middleware.domain.gms.GermplasmListType;
import org.generationcp.middleware.domain.inventory.InventoryDetails;
import org.generationcp.middleware.domain.inventory.ListEntryLotDetails;
import org.generationcp.middleware.domain.inventory.LotDetails;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.pojos.*;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.ReservedInventoryKey;
import org.generationcp.middleware.pojos.ims.StockTransaction;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.report.LotReportRow;
import org.generationcp.middleware.pojos.report.TransactionReportRow;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Implementation of the InventoryDataManager interface.
 * Most of the functions in this class only use the connection to the local
 * instance, this is because the lot and transaction tables only exist in a
 * local instance.
 * 
 * @author Kevin Manansala
 * 
 */
public class InventoryDataManagerImpl extends DataManager implements InventoryDataManager{

    private static final Logger LOG = LoggerFactory.getLogger(InventoryDataManagerImpl.class);

    public InventoryDataManagerImpl() {
    }
    
    public InventoryDataManagerImpl(HibernateSessionProvider sessionProvider) {
        super(sessionProvider);
    }

    public InventoryDataManagerImpl(HibernateSessionProvider sessionProvider, String databaseName) {
        super(sessionProvider, databaseName);
    }

    @Override
    public List<Lot> getLotsByEntityType(String type, int start, int numOfRows) throws MiddlewareQueryException {
        List<Lot> toReturn = new ArrayList<Lot>();
        toReturn = getLotDao().getByEntityType(type, start, numOfRows);
        return toReturn;
    }

    @Override
    public long countLotsByEntityType(String type) throws MiddlewareQueryException {
        return getLotDao().countByEntityType(type);
    }

    @Override
    public List<Lot> getLotsByEntityTypeAndEntityId(String type, Integer entityId, int start, int numOfRows)
            throws MiddlewareQueryException {
        return getLotDao().getByEntityTypeAndEntityId(type, entityId, start, numOfRows);
    }

    @Override
    public long countLotsByEntityTypeAndEntityId(String type, Integer entityId) throws MiddlewareQueryException {
        return getLotDao().countByEntityTypeAndEntityId(type, entityId);
    }

    @Override
    public List<Lot> getLotsByEntityTypeAndLocationId(String type, Integer locationId, int start, int numOfRows)
            throws MiddlewareQueryException {
        return getLotDao().getByEntityTypeAndLocationId(type, locationId, start, numOfRows);
    }

    @Override
    public long countLotsByEntityTypeAndLocationId(String type, Integer locationId) throws MiddlewareQueryException {
        return getLotDao().countByEntityTypeAndLocationId(type, locationId);
    }

    @Override
    public List<Lot> getLotsByEntityTypeAndEntityIdAndLocationId(String type, Integer entityId, Integer locationId, int start, int numOfRows)
            throws MiddlewareQueryException {
        return getLotDao().getByEntityTypeAndEntityIdAndLocationId(type, entityId, locationId,
                start, numOfRows);
    }
    
    @Override
    public Lot getLotByEntityTypeAndEntityIdAndLocationIdAndScaleId(String type, Integer entityId, Integer locationId, Integer scaleId)
            throws MiddlewareQueryException {
        List<Lot> lots = getLotDao().getByEntityTypeEntityIdsLocationIdAndScaleId(type, Arrays.asList(new Integer[]{entityId}), locationId, scaleId);
        if(lots!=null && !lots.isEmpty()) {
        	return lots.get(0);
        }
        return null;
    }

    @Override
    public long countLotsByEntityTypeAndEntityIdAndLocationId(String type, Integer entityId, Integer locationId)
            throws MiddlewareQueryException {
        return getLotDao().countByEntityTypeAndEntityIdAndLocationId(type, entityId, locationId);
    }

    @Override
    public Double getActualLotBalance(Integer lotId) throws MiddlewareQueryException {
        return getLotDao().getActualLotBalance(lotId);
    }

    @Override
    public Double getAvailableLotBalance(Integer lotId) throws MiddlewareQueryException {
        return getLotDao().getAvailableLotBalance(lotId);
    }

    @Override
    public Integer addLot(Lot lot) throws MiddlewareQueryException {
        List<Lot> lots = new ArrayList<Lot>();
        lots.add(lot);
        List<Integer> ids = addOrUpdateLot(lots, Operation.ADD);
        return !ids.isEmpty() ? ids.get(0) : null;
    }

    @Override
    public List<Integer> addLots(List<Lot> lots) throws MiddlewareQueryException {
        return addOrUpdateLot(lots, Operation.ADD);
    }

    @Override
    public Integer updateLot(Lot lot) throws MiddlewareQueryException {
        List<Lot> lots = new ArrayList<Lot>();
        lots.add(lot);
        List<Integer> ids = addOrUpdateLot(lots, Operation.UPDATE);
        return !ids.isEmpty() ? ids.get(0) : null;
    }

    @Override
    public List<Integer> updateLots(List<Lot> lots) throws MiddlewareQueryException {
        return addOrUpdateLot(lots, Operation.UPDATE);
    }

    @Override
    public Integer addStockTransaction(StockTransaction stockTransaction) throws MiddlewareQueryException {
        Session session = getCurrentSession();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            StockTransactionDAO stockTransactionDAO = getStockTransactionDAO();
            Integer id = stockTransactionDAO.getNextId("id");
            stockTransaction.setId(id);
            stockTransactionDAO.saveOrUpdate(stockTransaction);
            stockTransactionDAO.flush();
            stockTransactionDAO.clear();
            trans.commit();

            return id;
        } catch (HibernateException e) {
            rollbackTransaction(trans);
            throw new MiddlewareQueryException(e.getMessage());
        } catch (MiddlewareQueryException e) {
            rollbackTransaction(trans);
            throw e;
        }
    }

    private List<Integer> addOrUpdateLot(List<Lot> lots, Operation operation) throws MiddlewareQueryException {
        Session session = getCurrentSession();
        Transaction trans = null;

        int lotsSaved = 0;
        List<Integer> idLotsSaved = new ArrayList<Integer>();
        try {
            // begin save transaction
            trans = session.beginTransaction();
            LotDAO dao = getLotDao();

            for (Lot lot : lots) {
                if (operation == Operation.ADD && lot.getId() == null){
                    // Auto-assign IDs for new local DB records
            		Integer id = dao.getNextId("id");
            		lot.setId(id);
                }
                Lot recordSaved = dao.saveOrUpdate(lot);
                idLotsSaved.add(recordSaved.getId());
                lotsSaved++;
                if (lotsSaved % JDBC_BATCH_SIZE == 0) {
                    // flush a batch of inserts and release memory
                    dao.flush();
                    dao.clear();
                }
            }
            // end transaction, commit to database
            trans.commit();
        } catch (ConstraintViolationException e) {
        	rollbackTransaction(trans);
        	throw new MiddlewareQueryException(e.getMessage());
    	} catch (MiddlewareQueryException e) {
        	rollbackTransaction(trans);
        	throw e;
    	} catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while saving Lot: InventoryDataManager.addOrUpdateLot(lots=" + lots + ", operation="
	                    + operation + "): " + e.getMessage(), e, LOG);
        } finally {
            session.flush();
        }

        return idLotsSaved;
    }

    @Override
    public Integer addTransaction(org.generationcp.middleware.pojos.ims.Transaction transaction) throws MiddlewareQueryException {
        List<org.generationcp.middleware.pojos.ims.Transaction> transactions = new ArrayList<org.generationcp.middleware.pojos.ims.Transaction>();
        transactions.add(transaction);
        List<Integer> ids = addTransactions(transactions);
        return !ids.isEmpty() ? ids.get(0) : null;
    }

    @Override
    public List<Integer> addTransactions(List<org.generationcp.middleware.pojos.ims.Transaction> transactions) throws MiddlewareQueryException {
        return addOrUpdateTransaction(transactions, Operation.ADD);
    }

    @Override
    public Integer updateTransaction(org.generationcp.middleware.pojos.ims.Transaction transaction) throws MiddlewareQueryException {
        List<org.generationcp.middleware.pojos.ims.Transaction> transactions = new ArrayList<org.generationcp.middleware.pojos.ims.Transaction>();
        transactions.add(transaction);
        List<Integer> ids = addOrUpdateTransaction(transactions, Operation.UPDATE);
        return !ids.isEmpty() ? ids.get(0) : null;
    }

    @Override
    public List<Integer> updateTransactions(List<org.generationcp.middleware.pojos.ims.Transaction> transactions)
            throws MiddlewareQueryException {
        return addOrUpdateTransaction(transactions, Operation.UPDATE);
    }

    private List<Integer> addOrUpdateTransaction(List<org.generationcp.middleware.pojos.ims.Transaction> transactions, Operation operation)
            throws MiddlewareQueryException {
        Session session = getCurrentSession();
        Transaction trans = null;

        int transactionsSaved = 0;
        List<Integer> idTransactionsSaved = new ArrayList<Integer>();
        try {
            // begin save transaction
            trans = session.beginTransaction();
            TransactionDAO dao = getTransactionDao();

            for (org.generationcp.middleware.pojos.ims.Transaction transaction : transactions) {
                if (operation == Operation.ADD) {
                    // Auto-assign IDs for new local DB records
                    Integer id = dao.getNextId("id");
                    transaction.setId(id);
                }
                org.generationcp.middleware.pojos.ims.Transaction recordSaved = dao.saveOrUpdate(transaction);
                idTransactionsSaved.add(recordSaved.getId());
                transactionsSaved++;
                if (transactionsSaved % JDBC_BATCH_SIZE == 0) {
                    // flush a batch of inserts and release memory
                    dao.flush();
                    dao.clear();
                }
            }
            // end transaction, commit to database
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while saving Transaction: InventoryDataManager.addOrUpdateTransaction(transactions="
                    + transactions + ", operation=" + operation + "): " + e.getMessage(), e, LOG);
        } finally {
            session.flush();
        }

        return idTransactionsSaved;
    }

    @Override
    public org.generationcp.middleware.pojos.ims.Transaction getTransactionById(Integer id) throws MiddlewareQueryException {
        return getTransactionDao().getById(id, false);
    }

    @Override
    public Set<org.generationcp.middleware.pojos.ims.Transaction> getTransactionsByLotId(Integer id) throws MiddlewareQueryException {
        Lot lot = getLotDao().getById(id, false);
        return lot.getTransactions();
    }

    @Override
    public List<org.generationcp.middleware.pojos.ims.Transaction> getAllTransactions(int start, int numOfRows) throws MiddlewareQueryException{
        List<org.generationcp.middleware.pojos.ims.Transaction> transactions = new ArrayList<org.generationcp.middleware.pojos.ims.Transaction>();
        transactions = getTransactionDao().getAll(start, numOfRows);
        return transactions;
    }

    @Override
    public List<org.generationcp.middleware.pojos.ims.Transaction> getAllReserveTransactions(int start, int numOfRows)
            throws MiddlewareQueryException {
        return getTransactionDao().getAllReserve(start, numOfRows);
    }

    @Override
    public long countAllReserveTransactions() throws MiddlewareQueryException {
        return getTransactionDao().countAllReserve();
    }

    @Override
    public List<org.generationcp.middleware.pojos.ims.Transaction> getAllDepositTransactions(int start, int numOfRows)
            throws MiddlewareQueryException {
        return getTransactionDao().getAllDeposit(start, numOfRows);
    }

    @Override
    public long countAllDepositTransactions() throws MiddlewareQueryException {
        return getTransactionDao().countAllDeposit();
    }

    @Override
    public List<org.generationcp.middleware.pojos.ims.Transaction> getAllReserveTransactionsByRequestor(Integer personId, int start,
            int numOfRows) throws MiddlewareQueryException {
        return getTransactionDao().getAllReserveByRequestor(personId, start, numOfRows);
    }

    @Override
    public long countAllReserveTransactionsByRequestor(Integer personId) throws MiddlewareQueryException {
        return getTransactionDao().countAllReserveByRequestor(personId);
    }

    @Override
    public List<org.generationcp.middleware.pojos.ims.Transaction> getAllDepositTransactionsByDonor(Integer personId, int start, int numOfRows)
            throws MiddlewareQueryException {
        return getTransactionDao().getAllDepositByDonor(personId, start, numOfRows);
    }

    @Override
    public long countAllDepositTransactionsByDonor(Integer personId) throws MiddlewareQueryException {
        return getTransactionDao().countAllDepositByDonor(personId);
    }

    @Override
    public List<TransactionReportRow> generateReportOnAllUncommittedTransactions(int start, int numOfRows) throws MiddlewareQueryException {
        List<TransactionReportRow> report = new ArrayList<TransactionReportRow>();

        LocationDataManagerImpl locationManager = new LocationDataManagerImpl(getSessionProvider());
        OntologyDataManagerImpl ontologyManager = new OntologyDataManagerImpl(getSessionProvider());

        List<org.generationcp.middleware.pojos.ims.Transaction> transactions = getTransactionDao().getAllUncommitted(start, numOfRows);

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
        return getTransactionDao().countAllUncommitted();
    }

    @Override
    public List<TransactionReportRow> generateReportOnAllReserveTransactions(int start, int numOfRows) throws MiddlewareQueryException {

        List<TransactionReportRow> report = new ArrayList<TransactionReportRow>();
        LocationDataManagerImpl locationManager = new LocationDataManagerImpl(getSessionProvider());
        OntologyDataManagerImpl ontologyManager = new OntologyDataManagerImpl(getSessionProvider());

        List<org.generationcp.middleware.pojos.ims.Transaction> transactions = getAllReserveTransactions(start, numOfRows);
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
        return getTransactionDao().countAllWithdrawals();
    }

    @Override
    public List<TransactionReportRow> generateReportOnAllWithdrawalTransactions(int start, int numOfRows) throws MiddlewareQueryException {
        List<TransactionReportRow> report = new ArrayList<TransactionReportRow>();

        LocationDataManagerImpl locationManager = new LocationDataManagerImpl(getSessionProvider());
        OntologyDataManagerImpl ontologyManager = new OntologyDataManagerImpl(getSessionProvider());

        List<org.generationcp.middleware.pojos.ims.Transaction> transactions = getTransactionDao().getAllWithdrawals(start, numOfRows);
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

            Person person = getPersonById(t.getPersonId());
            row.setPerson(person);

            report.add(row);
        }
        return report;
    }

    private Person getPersonById(Integer id) throws MiddlewareQueryException {
        return getPersonDao().getById(id, false);
    }

    @Override
    public List<Lot> getAllLots(int start, int numOfRows) throws MiddlewareQueryException {
        return getLotDao().getAll(start, numOfRows);
    }

    @Override
    public long countAllLots() throws MiddlewareQueryException {
        return getLotDao().countAll();
    }

    @Override
    public List<LotReportRow> generateReportOnAllLots(int start, int numOfRows) throws MiddlewareQueryException {
        List<Lot> allLots = getAllLots(start, numOfRows);
        return generateLotReportRows(allLots);
    }

    @Override
    public List<LotReportRow> generateReportOnDormantLots(int year, int start, int numOfRows) throws MiddlewareQueryException {
        Session sessionForLocal = getCurrentSession();

        SQLQuery query = sessionForLocal.createSQLQuery(Lot.GENERATE_REPORT_ON_DORMANT);
        query.setParameter("year", year);
        query.setFirstResult(start);
        query.setMaxResults(numOfRows);

        LocationDataManagerImpl locationManager = new LocationDataManagerImpl(getSessionProvider());
        OntologyDataManagerImpl ontologyManager = new OntologyDataManagerImpl(getSessionProvider());
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
        for (org.generationcp.middleware.pojos.ims.Transaction t : getTransactionDao().getEmptyLot(start, numOfRows)) {
            emptyLots.add(t.getLot());
        }
        return generateLotReportRows(emptyLots);
    }

    @Override
    public List<LotReportRow> generateReportOnLotsWithMinimumAmount(long minAmount, int start, int numOfRows)
            throws MiddlewareQueryException {
        List<Lot> lotsWithMinimunAmount = new ArrayList<Lot>();
        for (org.generationcp.middleware.pojos.ims.Transaction t : getTransactionDao().getLotWithMinimumAmount(
                minAmount, start, numOfRows)) {
            lotsWithMinimunAmount.add(t.getLot());
        }
        return generateLotReportRows(lotsWithMinimunAmount);
    }

    @Override
    public List<LotReportRow> generateReportOnLotsByEntityType(String type, int start, int numOfRows) throws MiddlewareQueryException {
        List<Lot> lotsByEntity = getLotsByEntityType(type, start, numOfRows);
        return generateLotReportRows(lotsByEntity);
    }

    @Override
    public List<LotReportRow> generateReportOnLotsByEntityTypeAndEntityId(String type, Integer entityId, int start, int numOfRows)
            throws MiddlewareQueryException {
        List<Integer> entityIds = new ArrayList<Integer>();
        entityIds.add(entityId);
        return generateReportOnLotsByEntityTypeAndEntityId(type, entityIds, start, numOfRows);
    }

    @Override
    public List<LotReportRow> generateReportOnLotsByEntityTypeAndEntityId(String type, List<Integer> entityIds, int start, int numOfRows)
            throws MiddlewareQueryException {
        List<Lot> lotsByEntityTypeAndEntityId = new ArrayList<Lot>();
        for (Integer entityId : entityIds) {
            List<Lot> lotsForEntityId = getLotsByEntityTypeAndEntityId(type, entityId, start, numOfRows);
            lotsByEntityTypeAndEntityId.addAll(lotsForEntityId);
        }
        return generateLotReportRows(lotsByEntityTypeAndEntityId);
    }

    private List<LotReportRow> generateLotReportRows(List<Lot> listOfLots) throws MiddlewareQueryException {

        LocationDataManagerImpl locationManager = new LocationDataManagerImpl(getSessionProvider());
        OntologyDataManagerImpl ontologyManager = new OntologyDataManagerImpl(getSessionProvider());
        List<LotReportRow> report = new ArrayList<LotReportRow>();
        for (Lot lot : listOfLots) {
            LotReportRow row = new LotReportRow();

            row.setLotId(lot.getId());

            row.setEntityIdOfLot(lot.getEntityId());

            Double lotBalance = getActualLotBalance(lot.getId());
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
    
    private List<GermplasmListData> getGermplasmListDataByListId(Integer id, int start, int numOfRows) throws MiddlewareQueryException {
		return getGermplasmListDataDAO().getByListId(id, start, numOfRows);
    }
    
	@Override
	public List<InventoryDetails> getInventoryDetailsByGermplasmList(Integer listId,
			String germplasmType) throws MiddlewareQueryException {
		
		GermplasmList germplasmList = getGermplasmListDAO().getById(listId);
		List<GermplasmListData> listData = null;
		
		if (germplasmList.getType() != null && germplasmList.getType().equalsIgnoreCase(germplasmType)) {
			
			Integer listDataListId = getGermplasmListDAO().getListDataListIDFromListDataProjectListID(listId);
            listData = getGermplasmListDataByListId(listDataListId,0,Integer.MAX_VALUE);
		} else { 
			listData = getGermplasmListDataByListId(listId,0,Integer.MAX_VALUE);
		}

		// Get recordIds in list
		List<Integer> recordIds = new ArrayList<Integer>();
		for (GermplasmListData datum : listData){
			if (datum != null){
				recordIds.add(datum.getId());
			}
		}

		List<InventoryDetails> inventoryDetails = getTransactionDao().getInventoryDetailsByTransactionRecordId(recordIds);

        fillInventoryDetailList(inventoryDetails, listData);

		
		Set<Integer> locationIds = new HashSet<Integer>();
		Set<Integer> userIds = new HashSet<Integer>();
		Set<Integer> scaleIds = new HashSet<Integer>();
		
		for (InventoryDetails detail : inventoryDetails){

            Integer locationId = detail.getLocationId();
            if (locationId != null) {
                locationIds.add(locationId);
            }

            Integer userId = detail.getUserId();
            if (userId != null) {
                userIds.add(userId);
            }

            Integer scaleId = detail.getScaleId();
            if (scaleId != null) {
                scaleIds.add(scaleId);
            }

        }
		
		List<Location> locations = new ArrayList<Location>();
		List<CVTerm> scales = new ArrayList<CVTerm>();
		Map<Integer, String> userNames = new HashMap<Integer, String>();
		
		if (!locationIds.isEmpty()) {
            locations.addAll(getLocationDao().getByIds(new ArrayList<>(locationIds)));
        }
		if (!userIds.isEmpty()) {
            userNames.putAll(getPersonDao().getPersonNamesByUserIds(new ArrayList<>(userIds)));
        }
		if (!scaleIds.isEmpty()) {
            scales.addAll(getCvTermDao().getByIds(new ArrayList<>(scaleIds)));
        }

		// Build List<InventoryDetails>
        for (InventoryDetails detail : inventoryDetails) {
            for (Location location : locations) {
                if (detail.getLocationId() != null && detail.getLocationId().equals(location.getLocid())) {
                    // in preparation for BMS-143 (use abbreviation when exporting inventory details of advance list
                    detail.setLocationName(location.getLname());
                    detail.setLocationAbbr(location.getLabbr());
                    break;
                }
            }

            if (detail.getUserId() != null && userNames.containsKey(detail.getUserId())) {
                detail.setUserName(userNames.get(detail.getUserId()));
            }

            for (CVTerm scale : scales) {
                if (detail.getScaleId() != null && detail.getScaleId().equals(scale.getCvTermId())) {
                    detail.setScaleName(scale.getName());
                    break;
                }
            }

            // set source name
            if (germplasmList != null) {
                detail.setSourceId(listId);
                detail.setSourceName(germplasmList.getName());
            }
        }

        Collections.sort(inventoryDetails);
		return inventoryDetails;
	}

    public boolean transactionsExistForListProjectDataListID(Integer listDataProjectListID) throws MiddlewareQueryException {
        Integer dataListID = getGermplasmListDAO().getListDataListIDFromListDataProjectListID(listDataProjectListID);
        return getTransactionDao().transactionsExistForListData(dataListID);
    }

    protected void fillInventoryDetailList(List<InventoryDetails> detailList, List<GermplasmListData> dataList) {
        List<GermplasmListData> forFill = new ArrayList<>();

        for (GermplasmListData germplasmListData : dataList) {
            boolean found = false;
            for (InventoryDetails inventoryDetails : detailList) {
                if (inventoryDetails.getGid().equals(germplasmListData.getGid())) {
                    inventoryDetails.copyFromGermplasmListData(germplasmListData);
                    found = true;
                    break;
                }
            }

            if (!found) {
                forFill.add(germplasmListData);
            }
        }

        for (GermplasmListData data : forFill) {
            InventoryDetails detail = new InventoryDetails();
            detail.copyFromGermplasmListData(data);
        }
    }

	@Override
	public List<InventoryDetails> getInventoryDetailsByGermplasmList(
			Integer listId) throws MiddlewareQueryException {
		
		return this.getInventoryDetailsByGermplasmList(listId, GermplasmListType.ADVANCED.name());
		
    }
    
    @SuppressWarnings("unchecked")
	@Override
	public List<InventoryDetails> getInventoryDetailsByGids(List<Integer> gids)
			throws MiddlewareQueryException {

		List<Name> gidNames = super.getAllByMethod(getNameDao(),
									"getNamesByGids", new Object[]{gids}, new Class[]{List.class});

		List<InventoryDetails> inventoryDetails = getTransactionDao().getInventoryDetailsByGids(gids);
		Set<Integer> locationIds = new HashSet<Integer>();
		Set<Integer> userIds = new HashSet<Integer>();
		Set<Integer> scaleIds = new HashSet<Integer>();
		
		for (InventoryDetails detail : inventoryDetails){
			if (detail != null) {
				Integer locationId = detail.getLocationId();
				if (locationId != null){
					locationIds.add(locationId);
				}

				Integer userId = detail.getUserId();
				if (userId != null){
					userIds.add(userId);
				}

				Integer scaleId = detail.getScaleId();
				if (scaleId != null){
					scaleIds.add(scaleId);
				}
			}
		}
		
		List<Location> locations = new ArrayList<Location>();
		List<CVTerm> scales = new ArrayList<CVTerm>();
		Map<Integer, String> userNames = new HashMap<Integer, String>();
		
		if (!locationIds.isEmpty()) {
            locations.addAll(getLocationDao().getByIds(new ArrayList<Integer>(locationIds)));
        }
		if (!userIds.isEmpty()) {
            userNames.putAll(getPersonDao().getPersonNamesByUserIds(new ArrayList<Integer>(userIds)));
        }
		if (!scaleIds.isEmpty()) {
            scales.addAll(getCvTermDao().getByIds(new ArrayList<Integer>(scaleIds)));
        }

		// Build List<InventoryDetails>
		for (InventoryDetails detail : inventoryDetails){
			if (detail != null) {
				for (Name name: gidNames){
					if (detail.getGid() != null && detail.getGid().equals(name.getGermplasmId())){
						detail.setGermplasmName(name.getNval());
						break;
					}
				}
				
				for (Location location: locations){
					if (detail.getLocationId() != null && detail.getLocationId().equals(location.getLocid())){
						detail.setLocationName(location.getLname());
						break;
					}
				}

				if (detail.getUserId() != null && userNames.containsKey(detail.getUserId())){
					detail.setUserName(userNames.get(detail.getUserId()));
				}

				for (CVTerm scale: scales){
					if (detail.getScaleId() != null && detail.getScaleId().equals(scale.getCvTermId())){
						detail.setScaleName(scale.getName());
						break;
					}
				}
			}
		}		
		Collections.sort(inventoryDetails);
		return inventoryDetails;
	}
    
    @Override
	public List<InventoryDetails> getInventoryDetailsByStudy(Integer studyId)
			throws MiddlewareQueryException {
    	// FIXME - get gids from study/nd_experiment, call getInventoryDetailsByGids, set sourceName
		return new ArrayList<InventoryDetails>();
	}

	@Override
	public List<ListEntryLotDetails> getLotDetailsForListEntry(Integer listId, Integer recordId, Integer gid)
			throws MiddlewareQueryException {
		return getListInventoryBuilder().retrieveInventoryLotsForListEntry(listId, recordId, gid);
	}

	@Override
	public List<GermplasmListData> getLotDetailsForList(Integer listId, int start, int numOfRows) throws MiddlewareQueryException {
		List<GermplasmListData> listEntries = getGermplasmListDataByListId(listId, start, numOfRows);
		return getListInventoryBuilder().retrieveInventoryLotsForList(listId, start, numOfRows,
                listEntries);
	}
	
    @Override
    public List<GermplasmListData> getLotCountsForList(Integer id, int start, int numOfRows) throws MiddlewareQueryException {
    	List<GermplasmListData> listEntries = getGermplasmListDataByListId(id, start, numOfRows);
    	return getListInventoryBuilder().retrieveLotCountsForList(id, start, numOfRows, listEntries);
    }

	@Override
	public Integer countLotsWithAvailableBalanceForGermplasm(Integer gid)
			throws MiddlewareQueryException {
		return getListInventoryBuilder().countLotsWithAvailableBalanceForGermplasm(gid);
	}

	@Override
	public List<LotDetails> getLotDetailsForGermplasm(Integer gid)
			throws MiddlewareQueryException {
		return getListInventoryBuilder().retrieveInventoryLotsForGermplasm(gid);
	}

	@Override
	public List<GermplasmListData> getLotCountsForListEntries(Integer listId, List<Integer> entryIds) throws MiddlewareQueryException {
		return getListInventoryBuilder().retrieveLotCountsForListEntries(listId, entryIds);
	}
	
	@Override
	public void cancelReservedInventory(List<ReservedInventoryKey> lotEntries) throws MiddlewareQueryException {
		for(ReservedInventoryKey entry : lotEntries){
			Integer lotId = entry.getLotId();
			Integer lrecId = entry.getLrecId();
			
			getTransactionDao().cancelReservationsForLotEntryAndLrecId(lotId,lrecId);
		}
	}

    @Override
    public boolean isStockIdExists(List<String> stockIDs) throws MiddlewareQueryException {
        return getTransactionDao().isStockIdExists(stockIDs);
    }

    @Override
    public List<String> getSimilarStockIds(List<String> stockIDs) throws MiddlewareQueryException {
        return getTransactionDao().getSimilarStockIds(stockIDs);
    }
}
