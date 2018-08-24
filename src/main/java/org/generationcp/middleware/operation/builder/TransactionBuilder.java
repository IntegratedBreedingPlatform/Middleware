/*******************************************************************************
 * Copyright (c) 2014, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.operation.builder;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.ims.EntityType;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.generationcp.middleware.pojos.ims.TransactionStatus;
import org.generationcp.middleware.util.Util;

public class TransactionBuilder extends Builder {

	private static final int COMMITMENT_DATE_INDEFINITE = 0;

	private DaoFactory daoFactory;

	public TransactionBuilder(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
		this.daoFactory = new DaoFactory(sessionProviderForLocal);
	}

	public List<Transaction> buildForSave(List<Lot> lots, Double amount, Integer userId, String comment, Integer sourceId,
			String inventoryID) throws MiddlewareQueryException {

		List<Transaction> transactions = new ArrayList<Transaction>();
		for (Lot lot : lots) {
			transactions.add(new Transaction(/* id */null, userId, lot, this.getCurrentDate(), TransactionStatus.ANTICIPATED.getIntValue(),
			/* quantity */this.formatAmount(amount), comment, TransactionBuilder.COMMITMENT_DATE_INDEFINITE,
			/* sourceType: LIST for now */EntityType.LIST.name(), sourceId, /* sourceRecordId */lot.getEntityId(), /* prevAmount */0d,
					this.getPersonId(userId), inventoryID));
		}
		return transactions;
	}

	public Transaction buildForAdd(Lot lot, Integer lRecordID, Double amount, Integer userId, String comment, Integer sourceId,
			String inventoryID, String bulkWith, String bulkComp) throws MiddlewareQueryException {
		Transaction transaction =
				new Transaction(null, userId, lot, this.getCurrentDate(), TransactionStatus.ANTICIPATED.getIntValue(),
						this.formatAmount(amount), comment, TransactionBuilder.COMMITMENT_DATE_INDEFINITE, EntityType.LIST.name(),
						sourceId, lRecordID, 0d, this.getPersonId(userId), inventoryID);

		transaction.setBulkCompl(bulkComp);
		transaction.setBulkWith(bulkWith);

		return transaction;
	}

	public List<Transaction> buildForUpdate(List<Lot> lots, Double amount, String comment) throws MiddlewareQueryException {

		List<Integer> lotIds = new ArrayList<Integer>();
		List<Integer> gids = new ArrayList<Integer>();
		for (Lot lot : lots) {
			lotIds.add(lot.getId());
			gids.add(lot.getEntityId());
		}

		List<Transaction> existingTransactions = daoFactory.getTransactionDAO().getByLotIds(lotIds);

		if (gids != null && !gids.isEmpty()) {
			for (Transaction transaction : existingTransactions) {
				if (gids.contains(transaction.getSourceRecordId())) {
					transaction.setQuantity(this.formatAmount(amount));
					transaction.setComments(comment);
				}
			}
		}

		return existingTransactions;
	}

	private Integer getCurrentDate() {
		// Get current date in ICIS date format YYYMMDD
		return Util.getCurrentDateAsIntegerValue();
	}

	private Double formatAmount(Double amount) {
		// Truncate amount to 3 decimal places
		return Double.valueOf(new DecimalFormat("#.000").format(amount));
	}

	private Integer getPersonId(Integer userId) throws MiddlewareQueryException {
		User user = this.daoFactory.getUserDao().getById(userId);
		return user.getPersonid();
	}
}
