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

import org.generationcp.middleware.domain.inventory.ListEntryLotDetails;
import org.generationcp.middleware.domain.inventory.LotDetails;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.generationcp.middleware.pojos.report.TransactionReportRow;
import org.generationcp.middleware.pojos.workbench.CropType;

import java.util.List;

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
	// TODO: remove it. This is only used for test purpose.
	@Deprecated
	List<Lot> getLotsByEntityType(String type, int start, int numOfRows);

	/**
	 * Given a valid Lot object, add it as a new record to the database. It is assumed that the entity referenced by the Lot is already
	 * present in the database.
	 *
	 * @param lot the lot
	 * @return Returns the id of the {@code Lot} record added
	 */
	// TODO: remove it. This is only used for test purpose.
	@Deprecated
	Integer addLot(Lot lot);

	/**
	 * Given a List of valid Lot objects, add them as new records to the database. It is assumed that the entities referenced by the lots
	 * are already present in the database.
	 *
	 * @param lots the lots
	 * @return Returns the ids of the {@code Lot} records added
	 */
	// TODO: remove it. This is only used for test purpose.
	@Deprecated
	List<Integer> addLots(List<Lot> lots);

	/**
	 * Given a valid Transaction record, add it as a new record to the database.
	 *
	 * @param transaction the transaction
	 * @return Returns the id of the {@code Transaction} record added
	 */
	// TODO: remove it. This is only used for test purpose.
	@Deprecated
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
	// TODO: remove it. This is only used for test purpose.
	@Deprecated
	Integer updateTransaction(Transaction transaction);

	/**
	 * Returns the Transaction object which represents the record identified by the given id.
	 *
	 * @param id the id
	 * @return the Transaction of the given id
	 */
	// TODO: remove it. This is only used for test purpose.
	@Deprecated
	Transaction getTransactionById(Integer id);

	/**
	 * Gets the all transactions.
	 *
	 * @param start the start
	 * @param numOfRows the num of rows
	 * @return the all transactions
	 */
	// TODO: remove it. This is only used for test purpose.
	@Deprecated
	List<Transaction> getAllTransactions(int start, int numOfRows);

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

	// TODO: remove it. This is only used for test purpose.
	@Deprecated
	Lot getLotById(Integer id);

	/**
	 * Returns a report on all Transaction records with lot status(Active or closed). Included information are:
	 * userid, lotid, date of the transaction, transaction quantity,list name,person responsible for the transaction,
	 * status of lot.
	 * @param lotId - lotid
	 * @return List of TransactionReportRow objects
	 */
    List<TransactionReportRow> getTransactionDetailsForLot(Integer lotId);

	void generateLotIds(final CropType crop, final List<Lot> lots);
}
