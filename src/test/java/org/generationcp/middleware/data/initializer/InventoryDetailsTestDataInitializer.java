
package org.generationcp.middleware.data.initializer;

import com.beust.jcommander.internal.Lists;
import org.generationcp.middleware.domain.inventory.InventoryDetails;
import org.generationcp.middleware.domain.inventory.ListDataInventory;
import org.generationcp.middleware.domain.inventory.ListEntryLotDetails;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.ListDataProject;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.StockTransaction;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.generationcp.middleware.pojos.ims.TransactionType;
import org.generationcp.middleware.pojos.report.TransactionReportRow;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryDetailsTestDataInitializer {

	private static final int PERSON_ID = 1;
	private static final String LIST_SOURCE_TYPE = "LIST";
	private static final String GERMPLASM_ENTITY_TYPE = "GERMPLSM";
	private static final int USER_ID = 1;
	private static final int NO_OF_STOCK_LIST_ENTRIES = 20;
	public static final String LIST_NAME = "List1";
	public static final String USER = "User";
	public static final String STATUS = "Active";
	public static final Double AMOUNT = -50.0;
	public Date date;

	public InventoryDetailsTestDataInitializer() {
		// do nothing
		final String sDate1 = "13/04/2014";
		try {
			this.date = new SimpleDateFormat("dd/MM/yyyy").parse(sDate1);
		} catch (final ParseException e) {
			e.printStackTrace();
			this.date = null;
		}
	}

	public Map<String, InventoryDetails> createInventoryDetailsMap() {
		final Map<String, InventoryDetails> inventoryDetails = new HashMap<String, InventoryDetails>();

		for (int i = 1; i <= InventoryDetailsTestDataInitializer.NO_OF_STOCK_LIST_ENTRIES; i++) {
			inventoryDetails.put(String.valueOf(i), new InventoryDetails());
		}

		return inventoryDetails;
	}

	public List<InventoryDetails> createInventoryDetailList(final Integer numOfEntries) {
		final List<InventoryDetails> inventoryDetails = new ArrayList<InventoryDetails>();

		for (int i = 0; i < numOfEntries; i++) {
			final int id = i + 1;
			final InventoryDetails invDetails = new InventoryDetails();
			invDetails.setLotId(id);
			invDetails.setGid(id);
			invDetails.setGermplasmName("GermplsmName"+i);
			invDetails.setCross("Cross"+i);
			invDetails.setSource("SeedSource"+i);
			invDetails.setGroupId(i);
			invDetails.setReplicationNumber(1);
			invDetails.setPlotNumber(1);
			invDetails.setInstanceNumber(1);
			invDetails.setEntryId(1);
			inventoryDetails.add(invDetails);
		}

		return inventoryDetails;
	}

	public List<Lot> createLots(final List<Integer> gids, final Integer listId, final Integer scaleId, final Integer locId) {
		final List<Lot> lots = new ArrayList<Lot>();

		for (final Integer gid : gids) {
			final Lot lot = new Lot();
			lot.setEntityType(GERMPLASM_ENTITY_TYPE);
			lot.setUserId(USER_ID);
			lot.setEntityId(gid);
			lot.setLocationId(locId);
			lot.setScaleId(scaleId);
			lot.setStatus(0);
			lot.setSource(listId);
			lot.setComments("Lot for gid: " + gid);

			lots.add(lot);
		}

		return lots;
	}

	/**
	 * This method creates set of transactions with the following parameters:
	 * 
	 * @param lots
	 * @param listId
	 * @param lotIdLrecIdMap - Map of Lot Id and Germplasm List Data Id
	 * @param inventoryIdPrefix
	 * @return
	 */
	public List<Transaction> createTransactions(final List<Lot> lots, final Integer listId, final Map<Integer, Integer> lotIdLrecIdMap,
			final String inventoryIdPrefix, final Integer type) {
		final List<Transaction> transactions = new ArrayList<Transaction>();

		for (final Lot lot : lots) {
			lot.setStockId(inventoryIdPrefix + lot.getId());

			final Transaction transaction = new Transaction();
			transaction.setUserId(USER_ID);
			transaction.setPersonId(PERSON_ID);
			transaction.setLot(lot);
			transaction.setTransactionDate(new Date(20160101));
			transaction.setStatus(0);
			transaction.setQuantity(Math.random() * lots.size());
			transaction.setSourceType(LIST_SOURCE_TYPE);
			transaction.setSourceRecordId(lotIdLrecIdMap.get(lot.getId()));
			transaction.setSourceId(listId);
			transaction.setType(type);

			transactions.add(transaction);
		}

		return transactions;
	}

	/**
	 * This method creates set of reserved transactions with the following parameters:
	 *
	 * @param lots
	 * @param listId
	 * @param lotIdLrecIdMap - Map of Lot Id and Germplasm List Data Id
	 * @param inventoryIdPrefix
	 * @return
	 */
	public List<Transaction> createReservedTransactions(final List<Lot> lots, final Integer listId, final Map<Integer, Integer> lotIdLrecIdMap,
			final String inventoryIdPrefix) {
		final List<Transaction> transactions = new ArrayList<Transaction>();

		for (final Lot lot : lots) {
			lot.setStockId(inventoryIdPrefix + lot.getId());

			final Transaction transaction = new Transaction();
			transaction.setUserId(USER_ID);
			transaction.setPersonId(PERSON_ID);
			transaction.setLot(lot);
			transaction.setTransactionDate(new Date(20160101));
			transaction.setStatus(0);
			transaction.setQuantity(new Double("-100"));
			transaction.setSourceType(LIST_SOURCE_TYPE);
			transaction.setSourceRecordId(lotIdLrecIdMap.get(lot.getId()));
			transaction.setSourceId(listId);
			transaction.setType(TransactionType.DEPOSIT.getId());

			transactions.add(transaction);
		}

		return transactions;
	}

	/**
	 * Create List of StockTransaction objects based on the following input:
	 * 
	 * @param listDataIdTransact - map of listdata_id to transaction
	 * @param listDataIdListDataProject - map of listdata_id to listdataproject
	 * @return
	 */
	public List<StockTransaction> createStockTransactions(final Map<Integer, Transaction> listDataIdTransact,
			final Map<Integer, ListDataProject> listDataIdListDataProject) {

		final List<StockTransaction> stockTransactions = new ArrayList<StockTransaction>();

		for (final Map.Entry<Integer, Transaction> entry : listDataIdTransact.entrySet()) {
			final Integer listDataId = Integer.valueOf(entry.getKey());
			final Transaction transaction = entry.getValue();
			final ListDataProject listDataProject = listDataIdListDataProject.get(listDataId);

			final StockTransaction stockTransaction = new StockTransaction();
			stockTransaction.setSourceRecordId(listDataId);
			stockTransaction.setTransaction(transaction);
			stockTransaction.setListDataProject(listDataProject);

			stockTransactions.add(stockTransaction);
		}

		return stockTransactions;
	}

	public static List<GermplasmListData> createGermplasmListDataForReservedEntries(){
		final List germplasmListData = Lists.newArrayList();

		final GermplasmListData listEntry = new GermplasmListData();
		listEntry.setId(1);
		listEntry.setDesignation("Des");
		listEntry.setEntryId(1);
		listEntry.setGroupName("GroupName");
		listEntry.setStatus(0);
		listEntry.setSeedSource("SeedSource");
		listEntry.setGid(28);

		final List<ListEntryLotDetails> lots = new ArrayList<ListEntryLotDetails>();

		final ListEntryLotDetails lotDetails = new ListEntryLotDetails();
		lotDetails.setLotId(1);
		lotDetails.setReservedTotalForEntry(2.0);
		lotDetails.setLotScaleMethodName("weight");
		lotDetails.setLotScaleNameAbbr("g");
		final Location location = new Location(1);
		location.setLname("locName");
		lotDetails.setLocationOfLot(location);
		lotDetails.setStockIds("stockIds");
		lotDetails.setTransactionId(120);
		lotDetails.setCommentOfLot("comments");
		lotDetails.setAvailableLotBalance(1.0);
		lotDetails.setActualLotBalance(5.0);
		lotDetails.setReservedTotalForEntry(2.0);
		lotDetails.setReservedTotal(2.0);

		lots.add(lotDetails);
		final ListDataInventory listDataInfo = new ListDataInventory(1,28);
		listDataInfo.setLotRows(lots);
		listEntry.setInventoryInfo(listDataInfo);
		germplasmListData.add(listEntry);

		return germplasmListData;
	}


	public static List<Transaction> createValidReservedTransactions(){
		final Transaction transaction = new Transaction();
		transaction.setId(110);
		transaction.setQuantity(-2.0);
		transaction.setStatus(0);
		transaction.setComments("comments");

		return Lists.newArrayList(transaction);
	}

	public static Transaction createReservationTransaction(
		final Double quantity, final Integer status, final String comments, final Lot lot, final Integer personId,
		final Integer sourceId, final Integer sourceRecordId, final String sourceType, final Integer type) {
		final Transaction transaction = new Transaction();
		transaction.setQuantity(quantity);
		transaction.setStatus(status);
		transaction.setComments(comments);
		transaction.setLot(lot);
		transaction.setPersonId(personId);
		transaction.setSourceId(sourceId);
		transaction.setSourceRecordId(sourceRecordId);
		transaction.setSourceType(sourceType);
		transaction.setType(type);

		return transaction;
	}

	public static Transaction createDepositTransaction(
		final Double quantity, final Integer status, final String comments, final Lot lot, final Integer personId,
			final Integer sourceId, final Integer sourceRecordId, final String sourceType, final String inventoryID){
		final Transaction transaction = new Transaction();
		transaction.setQuantity(quantity);
		transaction.setStatus(status);
		transaction.setComments(comments);
		transaction.setLot(lot);
		transaction.setPersonId(personId);
		transaction.setSourceId(sourceId);
		transaction.setSourceRecordId(sourceRecordId);
		transaction.setSourceType(sourceType);
		lot.setStockId(inventoryID);

		return  transaction;
	}



	public static Lot createLot(
		final Integer userId, final String entityType, final Integer entityId, final Integer locationId, final Integer scaleId,
		final Integer status,
		final Integer sourceId, final String comments, final String stockId) {
		final Lot lot = new Lot();
		lot.setUserId(userId);
		lot.setEntityType(entityType);
		lot.setEntityId(entityId);
		lot.setLocationId(locationId);
		lot.setScaleId(scaleId);
		lot.setStatus(status);
		lot.setSource(sourceId);
		lot.setComments(comments);
		lot.setStockId(stockId);

		return lot;
	}

	public List<TransactionReportRow> createTransactionReportRowTestData() {

		final List<TransactionReportRow> transactionReportRowList = new ArrayList<>();
		final TransactionReportRow transactionReportRows = new TransactionReportRow();
		transactionReportRows.setListName(LIST_NAME);
		transactionReportRows.setLotStatus(STATUS);
		transactionReportRows.setDate(this.date);
		transactionReportRows.setQuantity(AMOUNT);
		transactionReportRows.setUser(USER);

		transactionReportRowList.add(transactionReportRows);
		return transactionReportRowList;
	}
}
