package org.generationcp.middleware.manager.api;

import java.util.List;
import java.util.Set;

import org.generationcp.middleware.exceptions.QueryException;
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
public interface InventoryDataManager
{
	/**
	 * Returns the Lot records with entity type matching the given parameter.
	 * 
	 * @param type
	 * @param start - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * 
	 * @return  List of Lot POJOs
	 */
	public List<Lot> findLotsByEntityType(String type, int start, int numOfRows);
	
	/**
	 * Returns the number of Lot records with entity type matching the given parameter.
	 * 
	 * @param type
	 * @return
	 */
	public int countLotsByEntityType(String type);
	
	/**
	 * Returns the Lot records with entity type and entity id matching the given parameters.
	 * 
	 * @param type
	 * @param entityId
	 * @param start - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * 
	 * @return List of Lot POJOs
	 */
	public List<Lot> findLotsByEntityTypeAndEntityId(String type, Integer entityId, int start, int numOfRows);
	
	/**
	 * Returns the number of Lot records with entity type and entity id matching the given parameters.
	 * 
	 * @param type
	 * @param entityId
	 * @return
	 */
	public int countLotsByEntityTypeAndEntityId(String type, Integer entityId);
	
	/**
	 * Returns the Lot records with entity type and location id matching the given parameters.
	 * 
	 * @param type
	 * @param locationId
	 * @param start - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * 
	 * @return List of Lot POJOs
	 */
	public List<Lot> findLotsByEntityTypeAndLocationId(String type, Integer locationId, int start, int numOfRows);
	
	/**
	 * Returns the number of Lot records with entity type and location id matching the given parameters.
	 * 
	 * @param type
	 * @param locationId
	 * @return
	 */
	public int countLotsByEntityTypeAndLocationId(String type, Integer locationId);
	
	/**
	 * Returns the Lot records with entity type, entity id, and location id matching the given parameters.
	 * 
	 * @param type
	 * @param entityId
	 * @param start - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * 
	 * @return List of Lot POJOs
	 */
	public List<Lot> findLotsByEntityTypeAndEntityIdAndLocationId(String type, Integer entityId, Integer locationId, int start, int numOfRows);
	
	/**
	 * Returns the number of Lot records with entity type, entity id, and location id matching the given parameters.
	 * 
	 * @param type
	 * @param entityId
	 * @param locationId
	 * @return
	 */
	public int countLotsByEntityTypeAndEntityIdAndLocationId(String type, Integer entityId, Integer locationId);

	/**
	 * Returns the actual transaction balance of the Lot with the specified lotId.
	 * Lot balance is computed from the sum of all its related transaction records' quantity.
	 * Only committed transactions (nstat=1) are included in the computation.
	 * 
	 * @param lotId - id of the Lot record
	 * @return The actual transaction balance of all the specified Lot
	 */
	public Long getActualLotBalance(Integer lotId);
	
	/**
	 * Returns the available transaction balance of the Lot with the specified lotId.
	 * Lot balance is computed from the sum of all its related transaction records' quantity.
	 * All non-cancelled transactions (nstat!=9) are included in the computation.
	 * 
	 * @param lotId - id of the Lot record
	 * @return The available transaction balance of all the specified Lot
	 */
	public Long getAvailableLotBalance(Integer lotId);
	
	/**
	 * Given a valid Lot object, add it as a new record to the database.  It is assumed
	 * that the entity referenced by the Lot is already present in the database.
	 * 
	 * @param lot
	 * @return number of Lot records added
	 * @throws QueryException
	 */
	public int addLot(Lot lot) throws QueryException;
	
	/**
	 * Given a List of valid Lot objects, add them as new records to the database.  It is
	 * assumed that the entities referenced by the lots are already present in the
	 * database.
	 * 
	 * @param lots
	 * @return number of Lot records added
	 * @throws QueryException
	 */
	public int addLot(List<Lot> lots) throws QueryException;
	
	/**
	 * Given a valid Lot object which represents an existing record in the database,
	 * update the record to the changes contained in the given object.
	 * 
	 * @param lot
	 * @return number of Lot records updated
	 * @throws QueryException
	 */
	public int updateLot(Lot lot) throws QueryException;
	
	/**
	 * Given a List of valid Lot objects, each of them representing an existing record
	 * in the database, update the records to the changes contained in the given objects.
	 * 
	 * @param lots
	 * @return number of Lot records updated
	 * @throws QueryException
	 */
	public int updateLot(List<Lot> lots) throws QueryException;
	
	/**
	 * Given a valid Transaction record, add it as a new record to the database.
	 * 
	 * @param transaction
	 * @return the number of Transaction records added
	 * @throws QueryException
	 */
	public int addTransaction(Transaction transaction) throws QueryException;
	
	/**
	 * Given a List of valid Transaction records, add them as new records to the
	 * database.
	 * 
	 * @param transactions
	 * @return the number of Transaction records added
	 * @throws QueryException
	 */
	public int addTransaction(List<Transaction> transactions) throws QueryException;
	
	/**
	 * Given a valid Transaction record, update the database to the changes from the
	 * object.  Note that the Lot can not be changed.
	 * 
	 * @param transaction
	 * @return the number of Transaction records updated
	 * @throws QueryException
	 */
	public int updateTransaction(Transaction transaction) throws QueryException;
	
	/**
	 * Givan a List of valid Transaction objects, update their corresponding
	 * records in the database.  Note that the Lot of the Transactions
	 * can not be changed.
	 * 
	 * @param transactions
	 * @return the number of Transaction records updated
	 * @throws QueryException
	 */
	public int updateTransaction(List<Transaction> transactions) throws QueryException;
	
	/**
	 * Returns the Transaction object which represents the record identified by the given id.
	 * 
	 * @param id
	 * @return
	 */
	public Transaction getTransactionById(Integer id);
	
	/**
	 * Return all Transaction records associated with the Lot identified by the given parameter.
	 * 
	 * @param id
	 * @return Set of Transaction POJOs representing the records
	 */
	public Set<Transaction> findTransactionsByLotId(Integer id);
	
	/**
	 * Returns the Transaction records which are classified as reserve transactions. The records have
	 * status = 0 and negative quantities.
	 * 
	 * @param start - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @return
	 */
	public List<Transaction> getAllReserveTransactions(int start, int numOfRows);
	
	/**
	 * Returns the number of Transaction records which are classified as reserve transactions. The records have
	 * status = 0 and negative quantities.
	 * 
	 * @return
	 */
	public int countAllReserveTransactions();
	
	/**
	 * Returns the Transaction records which are classified as deposit transactions. The records have status = 0
	 * and positive quantities.
	 * 
	 * @param start - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @return List of Transaction POJOs
	 */
	public List<Transaction> getAllDepositTransactions(int start, int numOfRows);
	
	/**
	 * Returns the number of Transaction records which are classified as deposit transactions. The records have status = 0
	 * and positive quantities.
	 * 
	 * @return
	 */
	public int countAllDepositTransactions();
	
	/**
	 * Returns the Transaction records which are classified as reserve transactions (the records have
	 * status = 0 and negative quantities) and made by the person identified by the given id.
	 * 
	 * @param personId
	 * @param start - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @return List of Transaction POJOs
	 */
	public List<Transaction> getAllReserveTransactionsByRequestor(Integer personId, int start, int numOfRows);
	
	/**
	 * Returns the number of Transaction records which are classified as reserve transactions (the records have
	 * status = 0 and negative quantities) and made by the person identified by the given id.
	 * 
	 * @param personId
	 * @return
	 */
	public int countAllReserveTransactionsByRequestor(Integer personId);
	
	/**
	 * Returns the Transaction records which are classified as deposit transactions (the records have
	 * status = 0 and positive quantities) and made by the person identified by the given id.
	 * 
	 * @param personId
	 * @param start - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @return List of Transaction POJOs
	 */
	public List<Transaction> getAllDepositTransactionsByDonor(Integer personId, int start, int numOfRows);
	
	/**
	 * Returns the number of Transaction records which are classified as deposit transactions (the records have
	 * status = 0 and positive quantities) and made by the person identified by the given id.
	 * 
	 * @param personId
	 * @return
	 */
	public int countAllDepositTransactionsByDonor(Integer personId);
	
	/**
	 * Returns a report on all uncommitted Transaction records.  Included information are: commitment date,
	 * quantity of transaction, scale of the lot of the transaction, location of the lot, comment on the lot.
	 * 
	 * @param start - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @return List of TransactionReportRow objects
	 */
	public List<TransactionReportRow> generateReportOnAllUncommittedTransactions(int start, int numOfRows);
	
	/**
	 * Return the number of all Transaction records which are uncommitted (status is equal to zero).
	 * 
	 * @return
	 */
	public int countAllUncommittedTransactions();
	
	/**
	 * Returns a report on all Transaction records classified as reserve transactions.  Included information are:
	 * commitment date, quantity of transaction, scale of the lot of the transaction, location of the lot, 
	 * comment on the lot, entity id of the lot.
	 * 
	 * @param start - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @return List of TransactionReportRow objects
	 */
	public List<TransactionReportRow> generateReportOnAllReserveTransactions(int start,int numOfRows);
	
	/**
	 * Returns a report on all Transaction records classified as withdrawal transactions (quantity is
	 * negative). Included information are: commitment date, quantity of transaction, scale of the lot of the transaction, location of the lot, 
	 * comment on the lot, entity id of the lot, person responsible for the transaction.
	 * 
	 * @param start - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @return List of TransactionReportRow objects
	 */
	public List<TransactionReportRow> generateReportOnAllWithdrawalTransactions(int start, int numOfRows);
	
	/**
	 * Returns the number of Transaction records classified as withdrawal transactions (quantity is negative).
	 * 
	 * @return
	 */
	public int countAllWithdrawalTransactions();
	
	/**
	 * Returns all Lot records in the database.
	 * 
	 * @param start - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @return List of Lot POJOs
	 */
	public List<Lot> getAllLots(int start, int numOfRows);
	
	/**
	 * Counts the lots in the database.
	 * @return The number of lots in the database. 
	 */
	public Long countAllLots();

	/**
	 * Returns a report on all Lot records. Included information are: lot balance, location of the lot,
	 * and scale of the lot.
	 * 
	 * @param start - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @return List of LotReportRow objects
	 */
	public List<LotReportRow> generateReportOnAllLots(int start, int numOfRows);
	
	/**
	 * Returns a report on all dormant Lot records given a specific year. All lots with non-zero balance
	 * on or before the given year are retrieved as dormant lots. Included information are: lot balance, 
	 * location of the lot, and scale of the lot.
	 * 
	 * @param year - filter dormant lots depending on the year specified
	 * @param start - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @return List of LotReportRow objects
	 */
	public List<LotReportRow> generateReportOnDormantLots(int year, int start, int numOfRows);
	
	/**
	 * Returns a report about Lots with zero balance
	 * Included information are: lot id of the lot, entity id of the lot, lot balance,
	 * location of the lot, and scale of the lot.
	 * 
	 * @return List of LotReportRow
	 * @throws QueryException 
	 */
	public List<LotReportRow> generateReportOnEmptyLots(int start, int numOfRows);
	
	/**
	 * Returns a report about Lots with balance less than the amount specified
	 * Included information are: lot id of the lot, entity id of the lot, lot balance,
	 * location of the lot, and scale of the lot.
	 * 
	 * @param minAmount- value specified
	 * @return List of LotReportRow objects
	 * @throws QueryException 
	 */
	public List<LotReportRow> generateReportOnLotsWithMinimumAmount(long minimumAmount,int start,int numOfRows);
	
	/**
	 * Returns a report on all Lot records associated with the given entity type. 
	 * Included information are: lot id of the lot, entity id of the lot, lot balance,
	 * location of the lot, and scale of the lot.
	 * 
	 * @param type - entity type of the Lots to generate the report from
	 * @param start - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @return List of LotReportRow objects
	 */
	public List<LotReportRow> generateReportOnLotsByEntityType(String type, int start, int numOfRows);

	/**
	 * Returns a report on all Lot records associated with the given entity type and entityId. 
	 * Included information are: lot id of the lot, entity id of the lot, lot balance,
	 * location of the lot, and scale of the lot.
	 * 
	 * @param type - entity type of the Lots to generate the report from
	 * @param entityId - entity Id of the Lot to generate the report from
	 * @param start - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @return List of LotReportRow objects
	 */
	public List<LotReportRow> generateReportOnLotsByEntityTypeAndEntityId(String type,
			Integer entityId, int start, int numOfRows);

	/**
	 * Returns a report on all Lot records associated with the given entity type and a list of entityIds. 
	 * Included information are: lot id of the lot, entity id of the lot, lot balance,
	 * location of the lot, and scale of the lot.
	 * 
	 * @param type - entity type of the Lots to generate the report from
	 * @param entityIds - a List of entity Ids of the Lots to generate the report from
	 * @param start - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @return List of LotReportRow objects
	 */
	public List<LotReportRow> generateReportOnLotsByEntityTypeAndEntityId(String type,
			List<Integer> entityIds, int start, int numOfRows);
	
}
