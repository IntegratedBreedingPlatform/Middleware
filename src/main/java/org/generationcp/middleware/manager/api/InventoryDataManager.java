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

package org.generationcp.middleware.manager.api;

import org.generationcp.middleware.domain.inventory.InventoryDetails;
import org.generationcp.middleware.domain.inventory.ListEntryLotDetails;
import org.generationcp.middleware.domain.inventory.LotDetails;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.StockTransaction;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.generationcp.middleware.pojos.report.TransactionReportRow;
import org.generationcp.middleware.pojos.workbench.CropType;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This is the API for retrieving information about Lots and Transactions.
 *
 * @author Kevin Manansala
 *
 */
public interface InventoryDataManager {

	/**
	 * Returns the Lot records with entity type matching the given parameter.
	 *
	 * @param type the type
	 * @param start - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @return List of Lot POJOs
	 */
	List<Lot> getLotsByEntityType(String type, int start, int numOfRows);

	/**
	 * Given a valid Lot object, add it as a new record to the database. It is assumed that the entity referenced by the Lot is already
	 * present in the database.
	 *
	 * @param lot the lot
	 * @return Returns the id of the {@code Lot} record added
	 */
	Integer addLot(Lot lot);

	/**
	 * Given a List of valid Lot objects, add them as new records to the database. It is assumed that the entities referenced by the lots
	 * are already present in the database.
	 *
	 * @param lots the lots
	 * @return Returns the ids of the {@code Lot} records added
	 */
	List<Integer> addLots(List<Lot> lots);

	/**
	 * Given a List of valid Lot objects, each of them representing an existing record in the database, update the records to the changes
	 * contained in the given objects.
	 *
	 * @param lots the lots
	 * @return Returns the ids of the updated {@code Lot} records
	 */
	List<Integer> updateLots(List<Lot> lots);

	/**
	 * Given a valid Transaction record, add it as a new record to the database.
	 *
	 * @param transaction the transaction
	 * @return Returns the id of the {@code Transaction} record added
	 */
	Integer addTransaction(Transaction transaction);

	/**
	 * Given a List of valid Transaction records, add them as new records to the database.
	 *
	 * @param transactions the transactions
	 * @return Returns the ids of the {@code Transaction} records added
	 */
	@Deprecated
	List<Integer> addTransactions(List<Transaction> transactions);

	/**
	 * Given a valid Transaction record, update the database to the changes from the object. Note that the Lot can not be changed.
	 *
	 * @param transaction the transaction
	 * @return Returns the id of the updated {@code Transaction} record
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Integer updateTransaction(Transaction transaction);

	/**
	 * Givan a List of valid Transaction objects, update their corresponding records in the database. Note that the Lot of the Transactions
	 * can not be changed.
	 *
	 * @param transactions the transactions
	 * @return Returns the ids of the updated {@code Transaction} records
	 */
	List<Integer> updateTransactions(List<Transaction> transactions);

	/**
	 * Returns the Transaction object which represents the record identified by the given id.
	 *
	 * @param id the id
	 * @return the Transaction of the given id
	 */
	Transaction getTransactionById(Integer id);

	/**
	 * Return all Transaction records associated with the Lot identified by the given parameter.
	 *
	 * @param id the id
	 * @return Set of Transaction POJOs representing the records
	 */
	Set<Transaction> getTransactionsByLotId(Integer id);

	/**
	 * Gets the all transactions.
	 *
	 * @param start the start
	 * @param numOfRows the num of rows
	 * @return the all transactions
	 */
	List<Transaction> getAllTransactions(int start, int numOfRows);

	/**
	 *
	 * @param listDataProjectListID
	 * @return
	 */
	boolean transactionsExistForListProjectDataListID(Integer listDataProjectListID);

	/**
	 * Returns lot rows and aggregate inventory data for given list entry
	 *
	 * @param listId
	 * @param recordId
	 * @param gid
	 * @return
	 */
	List<ListEntryLotDetails> getLotDetailsForListEntry(Integer listId, Integer recordId, Integer gid);

	/**
	 * Gets number of lots with available balance for germplasm
	 *
	 * @param gid
	 * @return
	 */
	Integer countLotsWithAvailableBalanceForGermplasm(Integer gid);

	/**
	 * Return list of lots with aggregate inventory information for given germplasm
	 *
	 * @param gid
	 * @return
	 */
	List<LotDetails> getLotDetailsForGermplasm(Integer gid);

	/**
	 * Returns the germplasm entries of given list id with lot counts such as # of lots with available balance and # of lots with reserved
	 * seed per entry
	 *
	 * @param listId - id of list
	 * @param start - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 *
	 * @return List of GermplasmListData POJOs
	 */
	List<GermplasmListData> getLotCountsForList(Integer listId, int start, int numOfRows);

	/**
	 * Return the germplasm entries of given entry IDs of specific list with lot counts such as # of lots with available balance and # of
	 * lots with reserved seed per entry
	 *
	 * @param listId
	 * @param entryIds
	 * @return
	 */
	List<GermplasmListData> getLotCountsForListEntries(Integer listId, List<Integer> entryIds);

	/**
	 * Adds in inventory related information into an existing {@link GermplasmList}
	 * @param germplasmList Existing germplasm list that we want to add inventory data too.
	 */
	void populateLotCountsIntoExistingList(GermplasmList germplasmList);

	Integer addStockTransaction(StockTransaction stockTransaction);

	List<String> getSimilarStockIds(List<String> stockIDs);

	List<String> getStockIdsByListDataProjectListId(Integer listId);

	Map<String, Double>  getStockIdsWithMultipleTransactions(Integer listId);

	void updateInventory(Integer listId, List<InventoryDetails> inventoryDetailListFromDB);

	Lot getLotById(Integer id);

	/**
	 * Returns a report on all Transaction records with lot status(Active or closed). Included information are:
	 * userid, lotid, date of the transaction, transaction quantity,list name,person responsible for the transaction,
	 * status of lot.
	 * @param lotId - lotid
	 * @return List of TransactionReportRow objects
	 */
    List<TransactionReportRow> getTransactionDetailsForLot(Integer lotId);

	/**
	 * This method will retrieve available balance for germplasm along with its scale
	 *
	 * @param germplasms
	 * @return List of Germplasm with inventoryInfo
	 */
	List<Germplasm> getAvailableBalanceForGermplasms(List<Germplasm> germplasms);

	/**
	 * Returns the Map of gid and related stockIds.
	 *
	 * @param gids
	 * @return Map of stockIDs per gid
	 */
	Map<Integer, String> retrieveStockIds(List<Integer> gids);

	void generateLotIds(final CropType crop, final List<Lot> lots);
}
