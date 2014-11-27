/*******************************************************************************
 * Copyright (c) 2014, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.middleware.operation.builder;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.ims.EntityType;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.generationcp.middleware.pojos.ims.TransactionStatus;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TransactionBuilder  extends Builder {

	private static final int COMMITMENT_DATE_INDEFINITE = 0;

	public TransactionBuilder(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	public List<Transaction> buildForSave(List<Lot> lots, Double amount, Integer userId, String comment, Integer sourceId)
			throws MiddlewareQueryException {
		
		List<Transaction> transactions = new ArrayList<Transaction>();
		for (Lot lot : lots){		
				transactions.add(new Transaction(/*id*/ null, userId, lot, getCurrentDate(), TransactionStatus.ANTICIPATED.getIntValue(), 
						/*quantity*/ formatAmount(amount), comment, COMMITMENT_DATE_INDEFINITE, 
						/*sourceType: LIST for now */ EntityType.LIST.name(),
						sourceId, /*sourceRecordId*/ lot.getEntityId(), /*prevAmount*/ 0d, getPersonId(userId)));
		}
		return transactions;
	}
	
	public List<Transaction> buildForUpdate(List<Lot> lots, Double amount, String comment) throws MiddlewareQueryException {
		
		List<Integer> lotIds = new ArrayList<Integer>();
		List<Integer> gids = new ArrayList<Integer>();
		for (Lot lot : lots){
			lotIds.add(lot.getId());
			gids.add(lot.getEntityId());
		}
		
		List<Transaction> existingTransactions = getTransactionDao().getByLotIds(lotIds);
		
		if (gids != null && !gids.isEmpty()){
			for (Transaction transaction : existingTransactions){
				if (gids.contains(transaction.getSourceRecordId())){
					transaction.setQuantity(formatAmount(amount));
					transaction.setComments(comment);
				}
			}
		}
		
		return existingTransactions;
	}

	private Integer getCurrentDate(){
		// Get current date in ICIS date format YYYMMDD
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());        
        return Integer.parseInt(date);
	}

	private Double formatAmount(Double amount){
        // Truncate amount to 3 decimal places
        return Double.valueOf((new DecimalFormat("#.000")).format(amount));
	}
	
	private Integer getPersonId(Integer userId) throws MiddlewareQueryException{
        User user = getUserDao().getById(userId);
        return user.getPersonid();
	}
}
