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

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.ims.EntityType;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.generationcp.middleware.pojos.ims.TransactionStatus;
import org.generationcp.middleware.util.Util;

import java.text.DecimalFormat;
import java.util.Date;

public class TransactionBuilder extends Builder {

	private static final int COMMITMENT_DATE_INDEFINITE = 0;

	public TransactionBuilder(final HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	public Transaction buildForAdd(final Lot lot, final Integer lRecordID, final Double amount, final Integer userId, final Integer personId, final String comment, final Integer sourceId,
			final String inventoryID, final String bulkWith, final String bulkComp) {
		final Transaction transaction =
				new Transaction(null, userId, lot, this.getCurrentDate(), TransactionStatus.COMMITTED.getIntValue(),
						this.formatAmount(amount), comment, TransactionBuilder.COMMITMENT_DATE_INDEFINITE, EntityType.LIST.name(),
						sourceId, lRecordID, 0d, personId, inventoryID);

		transaction.setBulkCompl(bulkComp);
		transaction.setBulkWith(bulkWith);

		return transaction;
	}

	private Date getCurrentDate() {
		// Get current date in ICIS date format YYYMMDD
		return Util.getCurrentDate();
	}

	private Double formatAmount(final Double amount) {
		// Truncate amount to 3 decimal places
		return Double.valueOf(new DecimalFormat("#.000").format(amount));
	}
}
