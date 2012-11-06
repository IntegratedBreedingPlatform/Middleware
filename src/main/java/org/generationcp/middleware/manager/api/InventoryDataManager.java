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

package org.generationcp.middleware.manager.api;

import java.util.List;
import java.util.Set;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.Lot;
import org.generationcp.middleware.pojos.Transaction;
import org.generationcp.middleware.pojos.report.LotReportRow;
import org.generationcp.middleware.pojos.report.TransactionReportRow;

/**
 * This is the API for retrieving information about Lots and Transactions.
 * 
 * @author Kevin Manansala
 * 
 */
public interface InventoryDataManager{

    /**
     * Returns the Lot records with entity type matching the given parameter.
     * 
     * @param type
     * @param start
     *            - the starting index of the sublist of results to be returned
     * @param numOfRows
     *            - the number of rows to be included in the sublist of results
     *            to be returned
     * 
     * @return List of Lot POJOs
     */
    public List<Lot> getLotsByEntityType(String type, int start, int numOfRows) throws MiddlewareQueryException;

    /**
     * Returns the number of Lot records with entity type matching the given
     * parameter.
     * 
     * @param type
     * @return
     */
    public long countLotsByEntityType(String type) throws MiddlewareQueryException;

    /**
     * Returns the Lot records with entity type and entity id matching the given
     * parameters.
     * 
     * @param type
     * @param entityId
     * @param start
     *            - the starting index of the sublist of results to be returned
     * @param numOfRows
     *            - the number of rows to be included in the sublist of results
     *            to be returned
     * 
     * @return List of Lot POJOs
     */
    public List<Lot> getLotsByEntityTypeAndEntityId(String type, Integer entityId, int start, int numOfRows) throws MiddlewareQueryException;

    /**
     * Returns the number of Lot records with entity type and entity id matching
     * the given parameters.
     * 
     * @param type
     * @param entityId
     * @return
     */
    public long countLotsByEntityTypeAndEntityId(String type, Integer entityId) throws MiddlewareQueryException;

    /**
     * Returns the Lot records with entity type and location id matching the
     * given parameters.
     * 
     * @param type
     * @param locationId
     * @param start
     *            - the starting index of the sublist of results to be returned
     * @param numOfRows
     *            - the number of rows to be included in the sublist of results
     *            to be returned
     * 
     * @return List of Lot POJOs
     */
    public List<Lot> getLotsByEntityTypeAndLocationId(String type, Integer locationId, int start, int numOfRows) throws MiddlewareQueryException;

    /**
     * Returns the number of Lot records with entity type and location id
     * matching the given parameters.
     * 
     * @param type
     * @param locationId
     * @return
     */
    public long countLotsByEntityTypeAndLocationId(String type, Integer locationId) throws MiddlewareQueryException;

    /**
     * Returns the Lot records with entity type, entity id, and location id
     * matching the given parameters.
     * 
     * @param type
     * @param entityId
     * @param start
     *            - the starting index of the sublist of results to be returned
     * @param numOfRows
     *            - the number of rows to be included in the sublist of results
     *            to be returned
     * 
     * @return List of Lot POJOs
     */
    public List<Lot> getLotsByEntityTypeAndEntityIdAndLocationId(String type, 
            Integer entityId, Integer locationId, int start, int numOfRows) throws MiddlewareQueryException;

    /**
     * Returns the number of Lot records with entity type, entity id, and
     * location id matching the given parameters.
     * 
     * @param type
     * @param entityId
     * @param locationId
     * @return
     */
    public long countLotsByEntityTypeAndEntityIdAndLocationId(String type, Integer entityId, Integer locationId) throws MiddlewareQueryException;

    /**
     * Returns the actual transaction balance of the Lot with the specified
     * lotId. Lot balance is computed from the sum of all its related
     * transaction records' quantity. Only committed transactions (nstat=1) are
     * included in the computation.
     * 
     * @param lotId
     *            - id of the Lot record
     * @return The actual transaction balance of all the specified Lot
     */
    public Long getActualLotBalance(Integer lotId) throws MiddlewareQueryException;

    /**
     * Returns the available transaction balance of the Lot with the specified
     * lotId. Lot balance is computed from the sum of all its related
     * transaction records' quantity. All non-cancelled transactions (nstat!=9)
     * are included in the computation.
     * 
     * @param lotId
     *            - id of the Lot record
     * @return The available transaction balance of all the specified Lot
     */
    public Long getAvailableLotBalance(Integer lotId) throws MiddlewareQueryException;

    /**
     * Given a valid Lot object, add it as a new record to the database. It is
     * assumed that the entity referenced by the Lot is already present in the
     * database.
     * 
     * @param lot
     * @return Returns the id of the {@code Lot} record added
     * @throws MiddlewareQueryException
     */
    public Integer addLot(Lot lot) throws MiddlewareQueryException;

    /**
     * Given a List of valid Lot objects, add them as new records to the
     * database. It is assumed that the entities referenced by the lots are
     * already present in the database.
     * 
     * @param lots
     * @return Returns the ids of the {@code Lot} records added
     * @throws MiddlewareQueryException
     */
    public List<Integer> addLot(List<Lot> lots) throws MiddlewareQueryException;

    /**
     * Given a valid Lot object which represents an existing record in the
     * database, update the record to the changes contained in the given object.
     * 
     * @param lot
     * @return Returns the id of the updated {@code Lot} record
     * @throws MiddlewareQueryException
     */
    public Integer updateLot(Lot lot) throws MiddlewareQueryException;

    /**
     * Given a List of valid Lot objects, each of them representing an existing
     * record in the database, update the records to the changes contained in
     * the given objects.
     * 
     * @param lots
     * @return Returns the ids of the updated {@code Lot} records
     * @throws MiddlewareQueryException
     */
    public List<Integer> updateLot(List<Lot> lots) throws MiddlewareQueryException;

    /**
     * Given a valid Transaction record, add it as a new record to the database.
     * 
     * @param transaction
     * @return Returns the id of the {@code Transaction} record added
     * @throws MiddlewareQueryException
     */
    public Integer addTransaction(Transaction transaction) throws MiddlewareQueryException;

    /**
     * Given a List of valid Transaction records, add them as new records to the
     * database.
     * 
     * @param transactions
     * @return Returns the ids of the {@code Transaction} records added
     * @throws MiddlewareQueryException
     */
    public List<Integer> addTransaction(List<Transaction> transactions) throws MiddlewareQueryException;

    /**
     * Given a valid Transaction record, update the database to the changes from
     * the object. Note that the Lot can not be changed.
     * 
     * @param transaction
     * @return Returns the id of the updated {@code Transaction} record
     * @throws MiddlewareQueryException
     */
    public Integer updateTransaction(Transaction transaction) throws MiddlewareQueryException;

    /**
     * Givan a List of valid Transaction objects, update their corresponding
     * records in the database. Note that the Lot of the Transactions can not be
     * changed.
     * 
     * @param transactions
     * @return Returns the ids of the updated {@code Transaction} records
     * @throws MiddlewareQueryException
     */
    public List<Integer> updateTransaction(List<Transaction> transactions) throws MiddlewareQueryException;

    /**
     * Returns the Transaction object which represents the record identified by
     * the given id.
     * 
     * @param id
     * @return
     */
    public Transaction getTransactionById(Integer id) throws MiddlewareQueryException;

    /**
     * Return all Transaction records associated with the Lot identified by the
     * given parameter.
     * 
     * @param id
     * @return Set of Transaction POJOs representing the records
     */
    public Set<Transaction> getTransactionsByLotId(Integer id) throws MiddlewareQueryException;

    /**
     * Returns the Transaction records which are classified as reserve
     * transactions. The records have status = 0 and negative quantities.
     * 
     * @param start
     *            - the starting index of the sublist of results to be returned
     * @param numOfRows
     *            - the number of rows to be included in the sublist of results
     *            to be returned
     * @return
     */
    public List<Transaction> getAllReserveTransactions(int start, int numOfRows) throws MiddlewareQueryException;

    /**
     * Returns the number of Transaction records which are classified as reserve
     * transactions. The records have status = 0 and negative quantities.
     * 
     * @return
     */
    public long countAllReserveTransactions() throws MiddlewareQueryException;

    /**
     * Returns the Transaction records which are classified as deposit
     * transactions. The records have status = 0 and positive quantities.
     * 
     * @param start
     *            - the starting index of the sublist of results to be returned
     * @param numOfRows
     *            - the number of rows to be included in the sublist of results
     *            to be returned
     * @return List of Transaction POJOs
     */
    public List<Transaction> getAllDepositTransactions(int start, int numOfRows) throws MiddlewareQueryException;

    /**
     * Returns the number of Transaction records which are classified as deposit
     * transactions. The records have status = 0 and positive quantities.
     * 
     * @return
     */
    public long countAllDepositTransactions() throws MiddlewareQueryException;

    /**
     * Returns the Transaction records which are classified as reserve
     * transactions (the records have status = 0 and negative quantities) and
     * made by the person identified by the given id.
     * 
     * @param personId
     * @param start
     *            - the starting index of the sublist of results to be returned
     * @param numOfRows
     *            - the number of rows to be included in the sublist of results
     *            to be returned
     * @return List of Transaction POJOs
     */
    public List<Transaction> getAllReserveTransactionsByRequestor(Integer personId, int start, int numOfRows) throws MiddlewareQueryException;

    /**
     * Returns the number of Transaction records which are classified as reserve
     * transactions (the records have status = 0 and negative quantities) and
     * made by the person identified by the given id.
     * 
     * @param personId
     * @return
     */
    public long countAllReserveTransactionsByRequestor(Integer personId) throws MiddlewareQueryException;

    /**
     * Returns the Transaction records which are classified as deposit
     * transactions (the records have status = 0 and positive quantities) and
     * made by the person identified by the given id.
     * 
     * @param personId
     * @param start
     *            - the starting index of the sublist of results to be returned
     * @param numOfRows
     *            - the number of rows to be included in the sublist of results
     *            to be returned
     * @return List of Transaction POJOs
     */
    public List<Transaction> getAllDepositTransactionsByDonor(Integer personId, int start, int numOfRows) throws MiddlewareQueryException;

    /**
     * Returns the number of Transaction records which are classified as deposit
     * transactions (the records have status = 0 and positive quantities) and
     * made by the person identified by the given id.
     * 
     * @param personId
     * @return
     */
    public long countAllDepositTransactionsByDonor(Integer personId) throws MiddlewareQueryException;

    /**
     * Returns a report on all uncommitted Transaction records. Included
     * information are: commitment date, quantity of transaction, scale of the
     * lot of the transaction, location of the lot, comment on the lot.
     * 
     * @param start
     *            - the starting index of the sublist of results to be returned
     * @param numOfRows
     *            - the number of rows to be included in the sublist of results
     *            to be returned
     * @return List of TransactionReportRow objects
     */
    public List<TransactionReportRow> generateReportOnAllUncommittedTransactions(int start, int numOfRows) throws MiddlewareQueryException;

    /**
     * Return the number of all Transaction records which are uncommitted
     * (status is equal to zero).
     * 
     * @return
     */
    public long countAllUncommittedTransactions() throws MiddlewareQueryException;

    /**
     * Returns a report on all Transaction records classified as reserve
     * transactions. Included information are: commitment date, quantity of
     * transaction, scale of the lot of the transaction, location of the lot,
     * comment on the lot, entity id of the lot.
     * 
     * @param start
     *            - the starting index of the sublist of results to be returned
     * @param numOfRows
     *            - the number of rows to be included in the sublist of results
     *            to be returned
     * @return List of TransactionReportRow objects
     */
    public List<TransactionReportRow> generateReportOnAllReserveTransactions(int start, int numOfRows) throws MiddlewareQueryException;

    /**
     * Returns a report on all Transaction records classified as withdrawal
     * transactions (quantity is negative). Included information are: commitment
     * date, quantity of transaction, scale of the lot of the transaction,
     * location of the lot, comment on the lot, entity id of the lot, person
     * responsible for the transaction.
     * 
     * @param start
     *            - the starting index of the sublist of results to be returned
     * @param numOfRows
     *            - the number of rows to be included in the sublist of results
     *            to be returned
     * @return List of TransactionReportRow objects
     */
    public List<TransactionReportRow> generateReportOnAllWithdrawalTransactions(int start, int numOfRows) throws MiddlewareQueryException;

    /**
     * Returns the number of Transaction records classified as withdrawal
     * transactions (quantity is negative).
     * 
     * @return
     */
    public long countAllWithdrawalTransactions() throws MiddlewareQueryException;

    /**
     * Returns all Lot records in the database.
     * 
     * @param start
     *            - the starting index of the sublist of results to be returned
     * @param numOfRows
     *            - the number of rows to be included in the sublist of results
     *            to be returned
     * @return List of Lot POJOs
     */
    public List<Lot> getAllLots(int start, int numOfRows) throws MiddlewareQueryException;

    /**
     * Counts the lots in the database.
     * 
     * @return The number of lots in the database.
     */
    public long countAllLots() throws MiddlewareQueryException;

    /**
     * Returns a report on all Lot records. Included information are: lot
     * balance, location of the lot, and scale of the lot.
     * 
     * @param start
     *            - the starting index of the sublist of results to be returned
     * @param numOfRows
     *            - the number of rows to be included in the sublist of results
     *            to be returned
     * @return List of LotReportRow objects
     */
    public List<LotReportRow> generateReportOnAllLots(int start, int numOfRows) throws MiddlewareQueryException;

    /**
     * Returns a report on all dormant Lot records given a specific year. All
     * lots with non-zero balance on or before the given year are retrieved as
     * dormant lots. Included information are: lot balance, location of the lot,
     * and scale of the lot.
     * 
     * @param year
     *            - filter dormant lots depending on the year specified
     * @param start
     *            - the starting index of the sublist of results to be returned
     * @param numOfRows
     *            - the number of rows to be included in the sublist of results
     *            to be returned
     * @return List of LotReportRow objects
     */
    public List<LotReportRow> generateReportOnDormantLots(int year, int start, int numOfRows) throws MiddlewareQueryException;

    /**
     * Returns a report about Lots with zero balance Included information are:
     * lot id of the lot, entity id of the lot, lot balance, location of the
     * lot, and scale of the lot.
     * 
     * @return List of LotReportRow
     * @throws MiddlewareQueryException
     */
    public List<LotReportRow> generateReportOnEmptyLots(int start, int numOfRows) throws MiddlewareQueryException;

    /**
     * Returns a report about Lots with balance less than the amount specified
     * Included information are: lot id of the lot, entity id of the lot, lot
     * balance, location of the lot, and scale of the lot.
     * 
     * @param minAmount
     *            - value specified
     * @return List of LotReportRow objects
     * @throws MiddlewareQueryException
     */
    public List<LotReportRow> generateReportOnLotsWithMinimumAmount(long minimumAmount, int start, int numOfRows) throws MiddlewareQueryException;

    /**
     * Returns a report on all Lot records associated with the given entity
     * type. Included information are: lot id of the lot, entity id of the lot,
     * lot balance, location of the lot, and scale of the lot.
     * 
     * @param type
     *            - entity type of the Lots to generate the report from
     * @param start
     *            - the starting index of the sublist of results to be returned
     * @param numOfRows
     *            - the number of rows to be included in the sublist of results
     *            to be returned
     * @return List of LotReportRow objects
     */
    public List<LotReportRow> generateReportOnLotsByEntityType(String type, int start, int numOfRows) throws MiddlewareQueryException;

    /**
     * Returns a report on all Lot records associated with the given entity type
     * and entityId. Included information are: lot id of the lot, entity id of
     * the lot, lot balance, location of the lot, and scale of the lot.
     * 
     * @param type
     *            - entity type of the Lots to generate the report from
     * @param entityId
     *            - entity Id of the Lot to generate the report from
     * @param start
     *            - the starting index of the sublist of results to be returned
     * @param numOfRows
     *            - the number of rows to be included in the sublist of results
     *            to be returned
     * @return List of LotReportRow objects
     */
    public List<LotReportRow> generateReportOnLotsByEntityTypeAndEntityId(String type, Integer entityId, int start, int numOfRows) throws MiddlewareQueryException;

    /**
     * Returns a report on all Lot records associated with the given entity type
     * and a list of entityIds. Included information are: lot id of the lot,
     * entity id of the lot, lot balance, location of the lot, and scale of the
     * lot.
     * 
     * @param type
     *            - entity type of the Lots to generate the report from
     * @param entityIds
     *            - a List of entity Ids of the Lots to generate the report from
     * @param start
     *            - the starting index of the sublist of results to be returned
     * @param numOfRows
     *            - the number of rows to be included in the sublist of results
     *            to be returned
     * @return List of LotReportRow objects
     */
    public List<LotReportRow> generateReportOnLotsByEntityTypeAndEntityId(String type, List<Integer> entityIds, int start, int numOfRows) throws MiddlewareQueryException;

}
