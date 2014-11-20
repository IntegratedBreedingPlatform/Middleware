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
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.report.LotReportRow;
import org.generationcp.middleware.pojos.report.TransactionReportRow;
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
    
    public InventoryDataManagerImpl(HibernateSessionProvider sessionProviderForLocal, HibernateSessionProvider sessionProviderForCentral) {
        super(sessionProviderForLocal, sessionProviderForCentral);
    }

    public InventoryDataManagerImpl(HibernateSessionProvider sessionProviderForLocal, HibernateSessionProvider sessionProviderForCentral,
    		String localDatabaseName, String centralDatabaseName) {
        super(sessionProviderForLocal, sessionProviderForCentral, localDatabaseName, centralDatabaseName);
    }

    public InventoryDataManagerImpl(Session sessionForLocal, Session sessionForCentral) {
        super(sessionForLocal, sessionForCentral);
    }

    @Override
    public List<Lot> getLotsByEntityType(String type, int start, int numOfRows) throws MiddlewareQueryException {
        List<Lot> toReturn = new ArrayList<Lot>();
        if (setWorkingDatabase(Database.LOCAL)) {
            toReturn = getLotDao().getByEntityType(type, start, numOfRows);
        }
        return toReturn;
    }

    @Override
    public long countLotsByEntityType(String type) throws MiddlewareQueryException {
        if (setWorkingDatabase(Database.LOCAL)) {
            return getLotDao().countByEntityType(type);
        }
        return 0;
    }

    @Override
    public List<Lot> getLotsByEntityTypeAndEntityId(String type, Integer entityId, int start, int numOfRows)
            throws MiddlewareQueryException {
        if (setWorkingDatabase(Database.LOCAL)) {
            return getLotDao().getByEntityTypeAndEntityId(type, entityId, start, numOfRows);
        }
        return new ArrayList<Lot>();
    }

    @Override
    public long countLotsByEntityTypeAndEntityId(String type, Integer entityId) throws MiddlewareQueryException {
        if (setWorkingDatabase(Database.LOCAL)) {
            return getLotDao().countByEntityTypeAndEntityId(type, entityId);
        }
        return 0;
    }

    @Override
    public List<Lot> getLotsByEntityTypeAndLocationId(String type, Integer locationId, int start, int numOfRows)
            throws MiddlewareQueryException {
        if (setWorkingDatabase(Database.LOCAL)) {
            return getLotDao().getByEntityTypeAndLocationId(type, locationId, start, numOfRows);
        }
        return new ArrayList<Lot>();
    }

    @Override
    public long countLotsByEntityTypeAndLocationId(String type, Integer locationId) throws MiddlewareQueryException {
        if (setWorkingDatabase(Database.LOCAL)) {
            return getLotDao().countByEntityTypeAndLocationId(type, locationId);
        }
        return 0;
    }

    @Override
    public List<Lot> getLotsByEntityTypeAndEntityIdAndLocationId(String type, Integer entityId, Integer locationId, int start, int numOfRows)
            throws MiddlewareQueryException {
        if (setWorkingDatabase(Database.LOCAL)) {
            return getLotDao().getByEntityTypeAndEntityIdAndLocationId(type, entityId, locationId, start, numOfRows);
        }
        return new ArrayList<Lot>();
    }
    
    @Override
    public Lot getLotByEntityTypeAndEntityIdAndLocationIdAndScaleId(String type, Integer entityId, Integer locationId, Integer scaleId)
            throws MiddlewareQueryException {
        if (setWorkingDatabase(Database.LOCAL)) {
            List<Lot> lots = getLotDao().getByEntityTypeEntityIdsLocationIdAndScaleId(type, Arrays.asList(new Integer[]{entityId}), locationId, scaleId);
            if(lots!=null && !lots.isEmpty()) {
            	return lots.get(0);
            }
        }
        return null;
    }

    @Override
    public long countLotsByEntityTypeAndEntityIdAndLocationId(String type, Integer entityId, Integer locationId)
            throws MiddlewareQueryException {
        if (setWorkingDatabase(Database.LOCAL)) {
            return getLotDao().countByEntityTypeAndEntityIdAndLocationId(type, entityId, locationId);
        }
        return 0;
    }

    @Override
    public Double getActualLotBalance(Integer lotId) throws MiddlewareQueryException {
        if (setWorkingDatabase(Database.LOCAL)) {
            return getLotDao().getActualLotBalance(lotId);
        }
        return Double.valueOf(0);
    }

    @Override
    public Double getAvailableLotBalance(Integer lotId) throws MiddlewareQueryException {
        if (setWorkingDatabase(Database.LOCAL)) {
            return getLotDao().getAvailableLotBalance(lotId);
        }
        return Double.valueOf(0);
    }

    @Override
    public Integer addLot(Lot lot) throws MiddlewareQueryException {
        List<Lot> lots = new ArrayList<Lot>();
        lots.add(lot);
        List<Integer> ids = addOrUpdateLot(lots, Operation.ADD);
        return ids.size() > 0 ? ids.get(0) : null;
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
        return ids.size() > 0 ? ids.get(0) : null;
    }

    @Override
    public List<Integer> updateLots(List<Lot> lots) throws MiddlewareQueryException {
        return addOrUpdateLot(lots, Operation.UPDATE);
    }

    private List<Integer> addOrUpdateLot(List<Lot> lots, Operation operation) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        int lotsSaved = 0;
        List<Integer> idLotsSaved = new ArrayList<Integer>();
        try {
            // begin save transaction
            trans = session.beginTransaction();
            LotDAO dao = getLotDao();

            for (Lot lot : lots) {
                if (operation == Operation.ADD) {
                	if (lot.getId() == null){
                        // Auto-assign IDs for new local DB records
                		Integer id = dao.getPositiveId("id");
                		lot.setId(id);
                	}
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
        } catch (Exception e) {
            rollbackTransaction(trans);
            if (e.getCause() instanceof ConstraintViolationException && e instanceof MiddlewareQueryException) {
            	throw (MiddlewareQueryException) e;
            }
            else {
	            logAndThrowException("Error encountered while saving Lot: InventoryDataManager.addOrUpdateLot(lots=" + lots + ", operation="
	                    + operation + "): " + e.getMessage(), e, LOG);
            }
        	
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
        return ids.size() > 0 ? ids.get(0) : null;
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
        return ids.size() > 0 ? ids.get(0) : null;
    }

    @Override
    public List<Integer> updateTransactions(List<org.generationcp.middleware.pojos.ims.Transaction> transactions)
            throws MiddlewareQueryException {
        return addOrUpdateTransaction(transactions, Operation.UPDATE);
    }

    private List<Integer> addOrUpdateTransaction(List<org.generationcp.middleware.pojos.ims.Transaction> transactions, Operation operation)
            throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
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
                    Integer id = dao.getPositiveId("id");
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
        if (setWorkingDatabase(Database.LOCAL)) {
            return getTransactionDao().getById(id, false);
        }
        return null;
    }

    @Override
    public Set<org.generationcp.middleware.pojos.ims.Transaction> getTransactionsByLotId(Integer id) throws MiddlewareQueryException {
        if (setWorkingDatabase(Database.LOCAL)) {
            Lot lot = getLotDao().getById(id, false);
            return lot.getTransactions();
        }
        return null;
    }

    @Override
    public List<org.generationcp.middleware.pojos.ims.Transaction> getAllTransactions(int start, int numOfRows) throws MiddlewareQueryException{
        List<org.generationcp.middleware.pojos.ims.Transaction> transactions = new ArrayList<org.generationcp.middleware.pojos.ims.Transaction>();
        if (setWorkingDatabase(Database.LOCAL)) {
            transactions = getTransactionDao().getAll(start, numOfRows);
        }
        return transactions;
    }

    @Override
    public List<org.generationcp.middleware.pojos.ims.Transaction> getAllReserveTransactions(int start, int numOfRows)
            throws MiddlewareQueryException {
        if (setWorkingDatabase(Database.LOCAL)) {
            return getTransactionDao().getAllReserve(start, numOfRows);
        }
        return new ArrayList<org.generationcp.middleware.pojos.ims.Transaction>();
    }

    @Override
    public long countAllReserveTransactions() throws MiddlewareQueryException {
        if (setWorkingDatabase(Database.LOCAL)) {
            return getTransactionDao().countAllReserve();
        }
        return 0;
    }

    @Override
    public List<org.generationcp.middleware.pojos.ims.Transaction> getAllDepositTransactions(int start, int numOfRows)
            throws MiddlewareQueryException {
        if (setWorkingDatabase(Database.LOCAL)) {
            return getTransactionDao().getAllDeposit(start, numOfRows);
        }
        return null;
    }

    @Override
    public long countAllDepositTransactions() throws MiddlewareQueryException {
        if (setWorkingDatabase(Database.LOCAL)) {
            return getTransactionDao().countAllDeposit();
        }
        return 0;
    }

    @Override
    public List<org.generationcp.middleware.pojos.ims.Transaction> getAllReserveTransactionsByRequestor(Integer personId, int start,
            int numOfRows) throws MiddlewareQueryException {
        if (setWorkingDatabase(Database.LOCAL)) {
            return getTransactionDao().getAllReserveByRequestor(personId, start, numOfRows);
        }
        return null;
    }

    @Override
    public long countAllReserveTransactionsByRequestor(Integer personId) throws MiddlewareQueryException {
        if (setWorkingDatabase(Database.LOCAL)) {
            return getTransactionDao().countAllReserveByRequestor(personId);
        }
        return 0;
    }

    @Override
    public List<org.generationcp.middleware.pojos.ims.Transaction> getAllDepositTransactionsByDonor(Integer personId, int start, int numOfRows)
            throws MiddlewareQueryException {
        if (setWorkingDatabase(Database.LOCAL)) {
            return getTransactionDao().getAllDepositByDonor(personId, start, numOfRows);
        }
        return null;
    }

    @Override
    public long countAllDepositTransactionsByDonor(Integer personId) throws MiddlewareQueryException {
        if (setWorkingDatabase(Database.LOCAL)) {
            return getTransactionDao().countAllDepositByDonor(personId);
        }
        return 0;
    }

    @Override
    public List<TransactionReportRow> generateReportOnAllUncommittedTransactions(int start, int numOfRows) throws MiddlewareQueryException {
        List<TransactionReportRow> report = new ArrayList<TransactionReportRow>();

        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        LocationDataManagerImpl locationManager = new LocationDataManagerImpl(sessionForLocal, sessionForCentral);
        OntologyDataManagerImpl ontologyManager = new OntologyDataManagerImpl(sessionForLocal, sessionForCentral);

        if (setWorkingDatabase(Database.LOCAL)) {
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

        }
        return report;
    }

    @Override
    public long countAllUncommittedTransactions() throws MiddlewareQueryException {
        if (setWorkingDatabase(Database.LOCAL)) {
            return getTransactionDao().countAllUncommitted();
        }
        return 0;
    }

    @Override
    public List<TransactionReportRow> generateReportOnAllReserveTransactions(int start, int numOfRows) throws MiddlewareQueryException {
        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();

        List<TransactionReportRow> report = new ArrayList<TransactionReportRow>();
        LocationDataManagerImpl locationManager = new LocationDataManagerImpl(sessionForLocal, sessionForCentral);
        OntologyDataManagerImpl ontologyManager = new OntologyDataManagerImpl(sessionForLocal, sessionForCentral);

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
        if (setWorkingDatabase(Database.LOCAL)) {
            return getTransactionDao().countAllWithdrawals();
        }
        return 0;
    }

    @Override
    public List<TransactionReportRow> generateReportOnAllWithdrawalTransactions(int start, int numOfRows) throws MiddlewareQueryException {
        List<TransactionReportRow> report = new ArrayList<TransactionReportRow>();

        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        LocationDataManagerImpl locationManager = new LocationDataManagerImpl(sessionForLocal, sessionForCentral);
        OntologyDataManagerImpl ontologyManager = new OntologyDataManagerImpl(sessionForLocal, sessionForCentral);

        if (setWorkingDatabase(Database.LOCAL)) {
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
        }
        return report;
    }

    private Person getPersonById(Integer id) throws MiddlewareQueryException {
        if (setWorkingDatabase(id)) {
            return getPersonDao().getById(id, false);
        }
        return null;
    }

    @Override
    public List<Lot> getAllLots(int start, int numOfRows) throws MiddlewareQueryException {
        if (setWorkingDatabase(Database.LOCAL)) {
            return getLotDao().getAll(start, numOfRows);
        }
        return new ArrayList<Lot>();
    }

    @Override
    public long countAllLots() throws MiddlewareQueryException {
        if (setWorkingDatabase(Database.LOCAL)) {
            return getLotDao().countAll();
        }
        return 0;
    }

    @Override
    public List<LotReportRow> generateReportOnAllLots(int start, int numOfRows) throws MiddlewareQueryException {
        List<Lot> allLots = getAllLots(start, numOfRows);
        return generateLotReportRows(allLots);
    }

    @Override
    public List<LotReportRow> generateReportOnDormantLots(int year, int start, int numOfRows) throws MiddlewareQueryException {
        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();

        SQLQuery query = sessionForLocal.createSQLQuery(Lot.GENERATE_REPORT_ON_DORMANT);
        query.setParameter("year", year);
        query.setFirstResult(start);
        query.setMaxResults(numOfRows);

        LocationDataManagerImpl locationManager = new LocationDataManagerImpl(sessionForLocal, sessionForCentral);
        OntologyDataManagerImpl ontologyManager = new OntologyDataManagerImpl(sessionForLocal, sessionForCentral);
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
        if (setWorkingDatabase(Database.LOCAL)) {
            for (org.generationcp.middleware.pojos.ims.Transaction t : getTransactionDao().getEmptyLot(start, numOfRows)) {
                emptyLots.add(t.getLot());
            }
        }
        return generateLotReportRows(emptyLots);
    }

    @Override
    public List<LotReportRow> generateReportOnLotsWithMinimumAmount(long minAmount, int start, int numOfRows)
            throws MiddlewareQueryException {
        List<Lot> lotsWithMinimunAmount = new ArrayList<Lot>();
        if (setWorkingDatabase(Database.LOCAL)) {
            for (org.generationcp.middleware.pojos.ims.Transaction t : getTransactionDao().getLotWithMinimumAmount(minAmount, start, numOfRows)) {
                lotsWithMinimunAmount.add(t.getLot());
            }
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
        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();

        LocationDataManagerImpl locationManager = new LocationDataManagerImpl(sessionForLocal, sessionForCentral);
        OntologyDataManagerImpl ontologyManager = new OntologyDataManagerImpl(
                getSessionProviderForLocal(), getSessionProviderForCentral());
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
    	setWorkingDatabase(Database.LOCAL);
		Map<String,Object> params = new LinkedHashMap<String,Object>();
		params.put("central_db_name", centralDatabaseName);
		params.put("listid",id);
		params.put("start",start);
		params.put("numOfRows",numOfRows);
		return getGermplasmListDataDAO().
				callStoredProcedureForList("getGermplasmListDataByListId",
						params,GermplasmListData.class);
    }
    
	@Override
	public List<InventoryDetails> getInventoryDetailsByGermplasmList(
			Integer listId) throws MiddlewareQueryException {
		

		setWorkingDatabase(listId);
		
		GermplasmList germplasmList = getGermplasmListDAO().getById(listId);
		List<GermplasmListData> listData = null;
		
		if (germplasmList.getType() != null && germplasmList.getType().equalsIgnoreCase(GermplasmListType.ADVANCED.name())) {
			
			germplasmList = getGermplasmListDAO().getById(listId);
			List<ListDataProject> listDataProjects = getListDataProjectDAO().getByListId(listId);
			listData = new ArrayList<GermplasmListData>();
			if (listDataProjects != null) {
				for (ListDataProject listDataProject : listDataProjects) {
					listData.add(new GermplasmListData(listDataProject.getListDataProjectId(), listDataProject.getList(), listDataProject.getGermplasmId(), 
							listDataProject.getEntryId(), listDataProject.getEntryCode(), listDataProject.getSeedSource(), listDataProject.getDesignation(), 
							listDataProject.getGroupName(), 1, null));
				}
			}
		}
		else { 
			listData = getGermplasmListDataByListId(listId,0,Integer.MAX_VALUE);
		}

		// Get gids
		List<Integer> gids = new ArrayList<Integer>();
		for (GermplasmListData datum : listData){
			if (datum != null){
				gids.add(datum.getGid());
			}
		}



		setWorkingDatabase(Database.LOCAL);
		List<InventoryDetails> inventoryDetails = getTransactionDao().getInventoryDetailsByGids(gids);
		
		Set<Integer> centralLocationIds = new HashSet<Integer>();
		Set<Integer> centralUserIds = new HashSet<Integer>();
		Set<Integer> centralScaleIds = new HashSet<Integer>();
		Set<Integer> localLocationIds = new HashSet<Integer>();
		Set<Integer> localUserIds = new HashSet<Integer>();
		Set<Integer> localScaleIds = new HashSet<Integer>();
		
		for (InventoryDetails detail : inventoryDetails){
			
			if (detail != null) {
				
				Integer locationId = detail.getLocationId();
				if (locationId != null){
					if (locationId < 0){
						localLocationIds.add(locationId);
					} else {
						centralLocationIds.add(locationId);
					}
				}

				Integer userId = detail.getUserId();
				if (userId != null){
					if (userId < 0){
						localUserIds.add(userId);
					} else {
						centralUserIds.add(userId);
					}
				}

				Integer scaleId = detail.getScaleId();
				if (scaleId != null){
					if (scaleId < 0){
						localScaleIds.add(scaleId);
					} else {
						centralScaleIds.add(scaleId);
					}
				}
			}
		}
		
		List<Location> locations = new ArrayList<Location>();
		List<CVTerm> scales = new ArrayList<CVTerm>();
		Map<Integer, String> userNames = new HashMap<Integer, String>();
		
		setWorkingDatabase(Database.LOCAL);
		if (localLocationIds.size() > 0) {
            locations.addAll(getLocationDao().getByIds(new ArrayList<Integer>(localLocationIds)));
        }
		if (localUserIds.size() > 0) {
            userNames.putAll(getPersonDao().getPersonNamesByUserIds(new ArrayList<Integer>(localUserIds)));
        }
		if (localScaleIds.size() > 0) {
            scales.addAll(getCvTermDao().getByIds(new ArrayList<Integer>(localScaleIds)));
        }

		setWorkingDatabase(Database.CENTRAL);
		if (centralLocationIds.size() > 0) {
            locations.addAll(getLocationDao().getByIds(new ArrayList<Integer>(centralLocationIds)));
        }
		if (centralUserIds.size() > 0) {
            userNames.putAll(getPersonDao().getPersonNamesByUserIds(new ArrayList<Integer>(centralUserIds)));
        }
		if (centralScaleIds.size() > 0) {
            scales.addAll(getCvTermDao().getByIds(new ArrayList<Integer>(centralScaleIds)));
        }
		
		// Build List<InventoryDetails>
		
		for (InventoryDetails detail : inventoryDetails){
			
			if (detail != null) {

				// Set germplasm name, entry id, parentage, source
				for (GermplasmListData datum : listData){
					if (datum != null && datum.getGid().equals(detail.getGid())){
						detail.setGermplasmName(datum.getDesignation());
						detail.setEntryId(datum.getEntryId());
						detail.setParentage(datum.getGroupName());
						detail.setSource(datum.getSeedSource());
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
		
		// Set source name
		for (InventoryDetails detail : inventoryDetails){
			if (detail != null && germplasmList != null) {
				detail.setSourceId(listId);
				detail.setSourceName(germplasmList.getName());
			}
		}


		
		Collections.sort(inventoryDetails);

		return inventoryDetails;
    }
    
    @SuppressWarnings("unchecked")
	@Override
	public List<InventoryDetails> getInventoryDetailsByGids(List<Integer> gids)
			throws MiddlewareQueryException {

		List<Name> gidNames = super.getAllFromCentralAndLocalByMethod(getNameDao(),
									"getNamesByGids", new Object[]{gids}, new Class[]{List.class});

		setWorkingDatabase(Database.LOCAL);
		List<InventoryDetails> inventoryDetails = getTransactionDao().getInventoryDetailsByGids(gids);
		
		Set<Integer> centralLocationIds = new HashSet<Integer>();
		Set<Integer> centralUserIds = new HashSet<Integer>();
		Set<Integer> centralScaleIds = new HashSet<Integer>();
		Set<Integer> localLocationIds = new HashSet<Integer>();
		Set<Integer> localUserIds = new HashSet<Integer>();
		Set<Integer> localScaleIds = new HashSet<Integer>();
		
		for (InventoryDetails detail : inventoryDetails){
			
			if (detail != null) {
				
				Integer locationId = detail.getLocationId();
				if (locationId != null){
					if (locationId < 0){
						localLocationIds.add(locationId);
					} else {
						centralLocationIds.add(locationId);
					}
				}

				Integer userId = detail.getUserId();
				if (userId != null){
					if (userId < 0){
						localUserIds.add(userId);
					} else {
						centralUserIds.add(userId);
					}
				}

				Integer scaleId = detail.getScaleId();
				if (scaleId != null){
					if (scaleId < 0){
						localScaleIds.add(scaleId);
					} else {
						centralScaleIds.add(scaleId);
					}
				}
			}
		}
		
		List<Location> locations = new ArrayList<Location>();
		List<CVTerm> scales = new ArrayList<CVTerm>();
		Map<Integer, String> userNames = new HashMap<Integer, String>();
		
		setWorkingDatabase(Database.LOCAL);
		if (localLocationIds.size() > 0) {
            locations.addAll(getLocationDao().getByIds(new ArrayList<Integer>(localLocationIds)));
        }
		if (localUserIds.size() > 0) {
            userNames.putAll(getPersonDao().getPersonNamesByUserIds(new ArrayList<Integer>(localUserIds)));
        }
		if (localScaleIds.size() > 0) {
            scales.addAll(getCvTermDao().getByIds(new ArrayList<Integer>(localScaleIds)));
        }

		setWorkingDatabase(Database.CENTRAL);
		if (centralLocationIds.size() > 0) {
            locations.addAll(getLocationDao().getByIds(new ArrayList<Integer>(centralLocationIds)));
        }
		if (centralUserIds.size() > 0) {
            userNames.putAll(getPersonDao().getPersonNamesByUserIds(new ArrayList<Integer>(centralUserIds)));
        }
		if (centralScaleIds.size() > 0) {
            scales.addAll(getCvTermDao().getByIds(new ArrayList<Integer>(centralScaleIds)));
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
		List<InventoryDetails> inventoryDetails = new ArrayList<InventoryDetails>();

		// TODO - get gids from study/nd_experiment, call getInventoryDetailsByGids, set sourceName

		return inventoryDetails;
		
	}

	@Override
	public List<ListEntryLotDetails> getLotDetailsForListEntry(Integer listId, Integer recordId, Integer gid)
			throws MiddlewareQueryException {
		return getListInventoryBuilder().retrieveInventoryLotsForListEntry(listId, recordId, gid);
	}

	@Override
	public List<GermplasmListData> getLotDetailsForList(Integer listId, int start, int numOfRows) throws MiddlewareQueryException {
		List<GermplasmListData> listEntries = getGermplasmListDataByListId(listId, start, numOfRows);
		return getListInventoryBuilder().retrieveInventoryLotsForList(listId, start, numOfRows, listEntries);
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
		setWorkingDatabase(Database.LOCAL);
		
		for(ReservedInventoryKey entry : lotEntries){
			Integer lotId = entry.getLotId();
			Integer lrecId = entry.getLrecId();
			
			getTransactionDao().cancelReservationsForLotEntryAndLrecId(lotId,lrecId);
		}
	}
}
