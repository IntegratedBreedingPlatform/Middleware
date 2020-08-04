
package org.generationcp.middleware.data.initializer;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.generationcp.middleware.pojos.ims.TransactionType;
import org.generationcp.middleware.pojos.report.TransactionReportRow;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class InventoryDetailsTestDataInitializer {

	private static final int PERSON_ID = 1;
	private static final String LIST_SOURCE_TYPE = "LIST";
	private static final String GERMPLASM_ENTITY_TYPE = "GERMPLSM";
	private static final int USER_ID = 1;
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

	public List<Lot> createLots(final List<Integer> gids, final Integer listId, final Integer scaleId, final Integer locId) {
		final List<Lot> lots = new ArrayList<>();

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
			lot.setStockId(RandomStringUtils.randomAlphabetic(35));
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
		final List<Transaction> transactions = new ArrayList<>();

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
		final List<Transaction> transactions = new ArrayList<>();

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
