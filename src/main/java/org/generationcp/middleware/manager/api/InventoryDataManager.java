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
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.report.TransactionReportRow;

import java.util.List;

/**
 * This is the API for retrieving information about Lots and Transactions.
 *
 * @author Kevin Manansala
 *
 */
public interface InventoryDataManager {

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

	/**
	 * Returns a report on all Transaction records with lot status(Active or closed). Included information are:
	 * userid, lotid, date of the transaction, transaction quantity,list name,person responsible for the transaction,
	 * status of lot.
	 * @param lotId - lotid
	 * @return List of TransactionReportRow objects
	 */
    List<TransactionReportRow> getTransactionDetailsForLot(Integer lotId);

}
