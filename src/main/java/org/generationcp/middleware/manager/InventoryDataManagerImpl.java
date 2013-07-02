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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.dao.LotDAO;
import org.generationcp.middleware.dao.TransactionDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Lot;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.Scale;
import org.generationcp.middleware.pojos.report.LotReportRow;
import org.generationcp.middleware.pojos.report.TransactionReportRow;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public long countLotsByEntityTypeAndEntityIdAndLocationId(String type, Integer entityId, Integer locationId)
            throws MiddlewareQueryException {
        if (setWorkingDatabase(Database.LOCAL)) {
            return getLotDao().countByEntityTypeAndEntityIdAndLocationId(type, entityId, locationId);
        }
        return 0;
    }

    @Override
    public Long getActualLotBalance(Integer lotId) throws MiddlewareQueryException {
        if (setWorkingDatabase(Database.LOCAL)) {
            return getLotDao().getActualLotBalance(lotId);
        }
        return Long.valueOf(0);
    }

    @Override
    public Long getAvailableLotBalance(Integer lotId) throws MiddlewareQueryException {
        if (setWorkingDatabase(Database.LOCAL)) {
            return getLotDao().getAvailableLotBalance(lotId);
        }
        return Long.valueOf(0);
    }

    @Override
    public Integer addLot(Lot lot) throws MiddlewareQueryException {
        List<Lot> lots = new ArrayList<Lot>();
        lots.add(lot);
        List<Integer> ids = addOrUpdateLot(lots, Operation.ADD);
        return ids.size() > 0 ? ids.get(0) : null;
    }

    @Override
    public List<Integer> addLot(List<Lot> lots) throws MiddlewareQueryException {
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
    public List<Integer> updateLot(List<Lot> lots) throws MiddlewareQueryException {
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
                    // Auto-assign negative IDs for new local DB records
                    Integer negativeId = dao.getNegativeId("id");
                    lot.setId(negativeId);
                } else if (operation == Operation.UPDATE) {
                    // Check if Lot is a local DB record. Throws exception if
                    // Lot is a central DB record.
                    dao.validateId(lot);
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
            logAndThrowException("Error encountered while saving Lot: InventoryDataManager.addOrUpdateLot(lots=" + lots + ", operation="
                    + operation + "): " + e.getMessage(), e, LOG);
        } finally {
            session.flush();
        }

        return idLotsSaved;
    }

    @Override
    public Integer addTransaction(org.generationcp.middleware.pojos.Transaction transaction) throws MiddlewareQueryException {
        List<org.generationcp.middleware.pojos.Transaction> transactions = new ArrayList<org.generationcp.middleware.pojos.Transaction>();
        transactions.add(transaction);
        List<Integer> ids = addTransaction(transactions);
        return ids.size() > 0 ? ids.get(0) : null;
    }

    @Override
    public List<Integer> addTransaction(List<org.generationcp.middleware.pojos.Transaction> transactions) throws MiddlewareQueryException {
        return addOrUpdateTransaction(transactions, Operation.ADD);
    }

    @Override
    public Integer updateTransaction(org.generationcp.middleware.pojos.Transaction transaction) throws MiddlewareQueryException {
        List<org.generationcp.middleware.pojos.Transaction> transactions = new ArrayList<org.generationcp.middleware.pojos.Transaction>();
        transactions.add(transaction);
        List<Integer> ids = addOrUpdateTransaction(transactions, Operation.UPDATE);
        return ids.size() > 0 ? ids.get(0) : null;
    }

    @Override
    public List<Integer> updateTransaction(List<org.generationcp.middleware.pojos.Transaction> transactions)
            throws MiddlewareQueryException {
        return addOrUpdateTransaction(transactions, Operation.UPDATE);
    }

    private List<Integer> addOrUpdateTransaction(List<org.generationcp.middleware.pojos.Transaction> transactions, Operation operation)
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

            for (org.generationcp.middleware.pojos.Transaction transaction : transactions) {
                if (operation == Operation.ADD) {
                    // Auto-assign negative IDs for new local DB records
                    Integer negativeId = dao.getNegativeId("id");
                    transaction.setId(negativeId);
                } else if (operation == Operation.UPDATE) {
                    // Check if Lot is a local DB record. Throws exception if
                    // Lot is a central DB record.
                    dao.validateId(transaction);
                }
                org.generationcp.middleware.pojos.Transaction recordSaved = dao.saveOrUpdate(transaction);
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
    public org.generationcp.middleware.pojos.Transaction getTransactionById(Integer id) throws MiddlewareQueryException {
        if (setWorkingDatabase(Database.LOCAL)) {
            return getTransactionDao().getById(id, false);
        }
        return null;
    }

    @Override
    public Set<org.generationcp.middleware.pojos.Transaction> getTransactionsByLotId(Integer id) throws MiddlewareQueryException {
        if (setWorkingDatabase(Database.LOCAL)) {
            Lot lot = getLotDao().getById(id, false);
            return lot.getTransactions();
        }
        return null;
    }

    @Override
    public List<org.generationcp.middleware.pojos.Transaction> getAllReserveTransactions(int start, int numOfRows)
            throws MiddlewareQueryException {
        if (setWorkingDatabase(Database.LOCAL)) {
            return getTransactionDao().getAllReserve(start, numOfRows);
        }
        return new ArrayList<org.generationcp.middleware.pojos.Transaction>();
    }

    @Override
    public long countAllReserveTransactions() throws MiddlewareQueryException {
        if (setWorkingDatabase(Database.LOCAL)) {
            return getTransactionDao().countAllReserve();
        }
        return 0;
    }

    @Override
    public List<org.generationcp.middleware.pojos.Transaction> getAllDepositTransactions(int start, int numOfRows)
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
    public List<org.generationcp.middleware.pojos.Transaction> getAllReserveTransactionsByRequestor(Integer personId, int start,
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
    public List<org.generationcp.middleware.pojos.Transaction> getAllDepositTransactionsByDonor(Integer personId, int start, int numOfRows)
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
        GermplasmDataManagerImpl germplasmManager = new GermplasmDataManagerImpl(sessionForLocal, sessionForCentral);
        TraitDataManagerImpl traitManager = new TraitDataManagerImpl(sessionForLocal, sessionForCentral);

        if (setWorkingDatabase(Database.LOCAL)) {
            List<org.generationcp.middleware.pojos.Transaction> transactions = getTransactionDao().getAllUncommitted(start, numOfRows);

            for (org.generationcp.middleware.pojos.Transaction t : transactions) {
                TransactionReportRow row = new TransactionReportRow();
                row.setDate(t.getDate());
                row.setQuantity(t.getQuantity());
                row.setCommentOfLot(t.getLot().getComments());

                Scale scale = traitManager.getScaleByID(t.getLot().getScaleId());
                row.setScaleOfLot(scale);

                Location location = germplasmManager.getLocationByID(t.getLot().getLocationId());
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
        GermplasmDataManagerImpl germplasmManager = new GermplasmDataManagerImpl(sessionForLocal, sessionForCentral);
        TraitDataManagerImpl traitManager = new TraitDataManagerImpl(sessionForLocal, sessionForCentral);

        List<org.generationcp.middleware.pojos.Transaction> transactions = getAllReserveTransactions(start, numOfRows);
        for (org.generationcp.middleware.pojos.Transaction t : transactions) {
            TransactionReportRow row = new TransactionReportRow();
            row.setDate(t.getDate());
            row.setQuantity(t.getQuantity());
            row.setCommentOfLot(t.getLot().getComments());
            row.setEntityIdOfLot(t.getLot().getEntityId());

            Scale scale = traitManager.getScaleByID(t.getLot().getScaleId());
            row.setScaleOfLot(scale);

            Location location = germplasmManager.getLocationByID(t.getLot().getLocationId());
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
        GermplasmDataManagerImpl germplasmManager = new GermplasmDataManagerImpl(sessionForLocal, sessionForCentral);
        TraitDataManagerImpl traitManager = new TraitDataManagerImpl(sessionForLocal, sessionForCentral);

        if (setWorkingDatabase(Database.LOCAL)) {
            List<org.generationcp.middleware.pojos.Transaction> transactions = getTransactionDao().getAllWithdrawals(start, numOfRows);
            for (org.generationcp.middleware.pojos.Transaction t : transactions) {
                TransactionReportRow row = new TransactionReportRow();
                row.setDate(t.getDate());
                row.setQuantity(t.getQuantity());
                row.setCommentOfLot(t.getLot().getComments());
                row.setEntityIdOfLot(t.getLot().getEntityId());

                Scale scale = traitManager.getScaleByID(t.getLot().getScaleId());
                row.setScaleOfLot(scale);

                Location location = germplasmManager.getLocationByID(t.getLot().getLocationId());
                row.setLocationOfLot(location);

                Person person = getPersonById(t.getPersonId());
                row.setPerson(person);

                report.add(row);
            }
        }
        return report;
    }

    public Person getPersonById(Integer id) throws MiddlewareQueryException {
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

        GermplasmDataManagerImpl germplasmManager = new GermplasmDataManagerImpl(sessionForLocal, sessionForCentral);
        TraitDataManagerImpl traitManager = new TraitDataManagerImpl(sessionForLocal, sessionForCentral);
        List<LotReportRow> report = new ArrayList<LotReportRow>();

        List<?> results = query.list();
        for (Object o : results) {
            Object[] result = (Object[]) o;
            if (result != null) {
                LotReportRow row = new LotReportRow();

                row.setLotId((Integer) result[0]);

                row.setEntityIdOfLot((Integer) result[1]);

                row.setActualLotBalance(((Double) result[2]).longValue());

                Location location = germplasmManager.getLocationByID((Integer) result[3]);
                row.setLocationOfLot(location);

                Scale scale = traitManager.getScaleByID((Integer) result[4]);
                row.setScaleOfLot(scale);

                report.add(row);
            }
        }
        return report;
    }

    @Override
    public List<LotReportRow> generateReportOnEmptyLots(int start, int numOfRows) throws MiddlewareQueryException {
        List<Lot> emptyLots = new ArrayList<Lot>();
        if (setWorkingDatabase(Database.LOCAL)) {
            for (org.generationcp.middleware.pojos.Transaction t : getTransactionDao().getEmptyLot(start, numOfRows)) {
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
            for (org.generationcp.middleware.pojos.Transaction t : getTransactionDao().getLotWithMinimumAmount(minAmount, start, numOfRows)) {
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

        GermplasmDataManagerImpl germplasmManager = new GermplasmDataManagerImpl(sessionForLocal, sessionForCentral);
        TraitDataManagerImpl traitManager = new TraitDataManagerImpl(sessionForLocal, sessionForCentral);
        List<LotReportRow> report = new ArrayList<LotReportRow>();
        for (Lot lot : listOfLots) {
            LotReportRow row = new LotReportRow();

            row.setLotId(lot.getId());

            row.setEntityIdOfLot(lot.getEntityId());

            Long lotBalance = getActualLotBalance(lot.getId());
            row.setActualLotBalance(lotBalance);

            Location location = germplasmManager.getLocationByID(lot.getLocationId());
            row.setLocationOfLot(location);

            Scale scale = traitManager.getScaleByID(lot.getScaleId());
            row.setScaleOfLot(scale);

            row.setCommentOfLot(lot.getComments());

            report.add(row);
        }
        return report;
    }

}
