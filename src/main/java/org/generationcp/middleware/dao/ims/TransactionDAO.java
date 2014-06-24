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
package org.generationcp.middleware.dao.ims;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.inventory.InventoryDetails;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.ims.LotStatus;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.generationcp.middleware.util.Util;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link Transaction}.
 * 
 */
public class TransactionDAO extends GenericDAO<Transaction, Integer>{

    @SuppressWarnings("unchecked")
    public List<Transaction> getAllReserve(int start, int numOfRows) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(Transaction.class);
            criteria.add(Restrictions.eq("status", Integer.valueOf(0)));
            criteria.add(Restrictions.lt("quantity", Double.valueOf(0)));
            criteria.setFirstResult(start);
            criteria.setMaxResults(numOfRows);
            return criteria.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getAllReserve() query from Transaction: " + e.getMessage(), e);
        }
        return new ArrayList<Transaction>();
    }

    public long countAllReserve() throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(Transaction.class);
            criteria.setProjection(Projections.rowCount());
            criteria.add(Restrictions.eq("status", Integer.valueOf(0)));
            criteria.add(Restrictions.lt("quantity", Double.valueOf(0)));
            return ((Long) criteria.uniqueResult()).longValue(); //count
        } catch (HibernateException e) {
            logAndThrowException("Error with countAllReserve() query from Transaction: " + e.getMessage(), e);
        }
        return 0;
    }

    @SuppressWarnings("unchecked")
    public List<Transaction> getAllDeposit(int start, int numOfRows) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(Transaction.class);
            criteria.add(Restrictions.eq("status", Integer.valueOf(0)));
            criteria.add(Restrictions.gt("quantity", Double.valueOf(0)));
            criteria.setFirstResult(start);
            criteria.setMaxResults(numOfRows);
            return criteria.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getAllDeposit() query from Transaction: " + e.getMessage(), e);
        }
        return new ArrayList<Transaction>();
    }

    public long countAllDeposit() throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(Transaction.class);
            criteria.setProjection(Projections.rowCount());
            criteria.add(Restrictions.eq("status", Integer.valueOf(0)));
            criteria.add(Restrictions.gt("quantity", Double.valueOf(0)));
            return ((Long) criteria.uniqueResult()).longValue(); //count
        } catch (HibernateException e) {
            logAndThrowException("Error with countAllDeposit() query from Transaction: " + e.getMessage(), e);
        }
        return 0;
    }

    @SuppressWarnings("unchecked")
    public List<Transaction> getAllReserveByRequestor(Integer personId, int start, int numOfRows) throws MiddlewareQueryException {
        try {
        	if (personId != null){
	            Criteria criteria = getSession().createCriteria(Transaction.class);
	            criteria.add(Restrictions.eq("status", Integer.valueOf(0)));
	            criteria.add(Restrictions.lt("quantity", Double.valueOf(0)));
	            criteria.add(Restrictions.eq("personId", personId));
	            criteria.setFirstResult(start);
	            criteria.setMaxResults(numOfRows);
	            return criteria.list();
        	}
        } catch (HibernateException e) {
            logAndThrowException("Error with getAllReserveByRequestor(personId=" + personId + ") query from Transaction: "
                    + e.getMessage(), e);
        }
        return new ArrayList<Transaction>();
    }

    public long countAllReserveByRequestor(Integer personId) throws MiddlewareQueryException {
        try {
        	if (personId != null){
	            Criteria criteria = getSession().createCriteria(Transaction.class);
	            criteria.setProjection(Projections.rowCount());
	            criteria.add(Restrictions.eq("status", Integer.valueOf(0)));
	            criteria.add(Restrictions.lt("quantity", Double.valueOf(0)));
	            criteria.add(Restrictions.eq("personId", personId));
	            return ((Long) criteria.uniqueResult()).longValue(); 
        	}
        } catch (HibernateException e) {
            logAndThrowException("Error with countAllReserveByRequestor(personId=" + personId + ") query from Transaction: "
                    + e.getMessage(), e);
        }
        return 0;
    }

    @SuppressWarnings("unchecked")
    public List<Transaction> getAllDepositByDonor(Integer personId, int start, int numOfRows) throws MiddlewareQueryException {
        try {
        	if (personId != null){
	            Criteria criteria = getSession().createCriteria(Transaction.class);
	            criteria.add(Restrictions.eq("status", Integer.valueOf(0)));
	            criteria.add(Restrictions.gt("quantity", Double.valueOf(0)));
	            criteria.add(Restrictions.eq("personId", personId));
	            criteria.setFirstResult(start);
	            criteria.setMaxResults(numOfRows);
	            return criteria.list();
        	}
        } catch (HibernateException e) {
            logAndThrowException("Error with getAllDepositByDonor(personId=" + personId + ") query from Transaction: "
                    + e.getMessage(), e);
        }
        return new ArrayList<Transaction>();
    }

    public long countAllDepositByDonor(Integer personId) throws MiddlewareQueryException {
        try {
        	if (personId != null){
	            Criteria criteria = getSession().createCriteria(Transaction.class);
	            criteria.setProjection(Projections.rowCount());
	            criteria.add(Restrictions.eq("status", Integer.valueOf(0)));
	            criteria.add(Restrictions.gt("quantity", Double.valueOf(0)));
	            criteria.add(Restrictions.eq("personId", personId));
	            return ((Long) criteria.uniqueResult()).longValue(); 
        	}
        } catch (HibernateException e) {
            logAndThrowException("Error with countAllDepositByDonor(personId=" + personId + ") query from Transaction: "
                    + e.getMessage(), e);
        }
        return 0;
    }

    @SuppressWarnings("unchecked")
    public List<Transaction> getAllUncommitted(int start, int numOfRows) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(Transaction.class);
            criteria.add(Restrictions.eq("status", Integer.valueOf(0)));
            criteria.setFirstResult(start);
            criteria.setMaxResults(numOfRows);
            return criteria.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getAllUncommitted() query from Transaction: " + e.getMessage(), e);
        }
        return new ArrayList<Transaction>();
    }

    public long countAllUncommitted() throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(Transaction.class);
            criteria.setProjection(Projections.rowCount());
            criteria.add(Restrictions.eq("status", Integer.valueOf(0)));
            return ((Long) criteria.uniqueResult()).longValue(); //count
        } catch (HibernateException e) {
            logAndThrowException("Error with countAllUncommitted() query from Transaction: " + e.getMessage(), e);
        }
        return 0;
    }

    @SuppressWarnings("unchecked")
    public List<Transaction> getAllWithdrawals(int start, int numOfRows) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(Transaction.class);
            criteria.add(Restrictions.lt("quantity", Double.valueOf(0)));
            criteria.setFirstResult(start);
            criteria.setMaxResults(numOfRows);
            return criteria.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getAllWithdrawals() query from Transaction: " + e.getMessage(), e);
        }
        return new ArrayList<Transaction>();
    }

    public long countAllWithdrawals() throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(Transaction.class);
            criteria.setProjection(Projections.rowCount());
            criteria.add(Restrictions.lt("quantity", Double.valueOf(0)));
            return ((Long) criteria.uniqueResult()).longValue(); //count
        } catch (HibernateException e) {
            logAndThrowException("Error with countAllWithdrawals() query from Transaction: " + e.getMessage(), e);
        }
        return 0;
    }

    @SuppressWarnings("unchecked")
    public List<Transaction> getEmptyLot(int start, int numOfRows) throws MiddlewareQueryException {
        try {
            Query query = getSession().getNamedQuery(Transaction.GET_EMPTY_LOT);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);
            return (List<Transaction>) query.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getEmptyLot() query from Transaction: " + e.getMessage(), e);
        }
        return new ArrayList<Transaction>();
    }

    @SuppressWarnings("unchecked")
    public List<Transaction> getLotWithMinimumAmount(double minAmount, int start, int numOfRows) throws MiddlewareQueryException {
        try {
            Query query = getSession().getNamedQuery(Transaction.GET_LOT_WITH_MINIMUM_AMOUNT);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);
            query.setParameter("minAmount", minAmount);
            return (List<Transaction>) query.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getLotWithMinimumAmount(minAmount=" + minAmount + ") query from Transaction: "
                    + e.getMessage(), e);
        }
        return new ArrayList<Transaction>();
    }
    
    @SuppressWarnings("unchecked")
    public List<InventoryDetails> getInventoryDetailsByGids(List<Integer> gids) throws MiddlewareQueryException {
    	List<InventoryDetails> inventoryDetails = new ArrayList<InventoryDetails>();
    	
    	if (gids == null || gids.isEmpty()){
    		return inventoryDetails;
    	}
    	
        try {
        	Session session = getSession();
        	
        	StringBuffer sql = new StringBuffer()
        		.append("SELECT lot.lotid, lot.userid, lot.eid, lot.locid, lot.scaleid, ")
        		.append("tran.sourceid, tran.trnqty, lot.comments ")
        		.append("FROM ims_lot lot ")
        		.append("LEFT JOIN ims_transaction tran ON lot.lotid = tran.lotid ")
        		.append("WHERE lot.status = ").append(LotStatus.ACTIVE.getIntValue())
        		.append("		 AND lot.eid IN (:gids) ");
        	SQLQuery query = session.createSQLQuery(sql.toString()); 
        	query.setParameterList("gids", gids);
        	
			List<Object[]> results = query.list();

	        if (results.size() > 0){
	        	for (Object[] row: results){
		        	Integer lotId = (Integer) row[0];
		        	Integer userId = (Integer) row[1];
		        	Integer gid = (Integer) row[2];
		        	Integer locationId = (Integer) row[3];
		        	Integer scaleId = (Integer) row[4];
		        	Integer sourceId = (Integer) row[5];
		        	Double amount = (Double) row[6];
		        	String comment = (String) row[7];
		        	
					inventoryDetails.add(new InventoryDetails(gid, null, lotId, locationId, null, 
													userId, amount, sourceId, null, scaleId, null, comment));
	        	}
	        }
	        
			for (Integer gid: gids){
				if (!isGidInInventoryList(inventoryDetails, gid)){
					inventoryDetails.add(new InventoryDetails(gid, null, null, null, null, null, null, null, null, null, null, null));
				}
			}


        } catch (HibernateException e) {
            logAndThrowException("Error with getGidsByListId() query from GermplasmList: " + e.getMessage(), e);
        }    	

    	return inventoryDetails;
    }
    
    
    @SuppressWarnings("unchecked")
	public Map<Integer, BigInteger> countLotsWithReservationForListEntries(List<Integer> listEntryIds) throws MiddlewareQueryException{
    	Map<Integer, BigInteger> lotCounts = new HashMap<Integer, BigInteger>();

    	try {
    		String sql = "SELECT recordid, count(t.lotid) " +
    		"FROM ims_transaction t " +
    		"INNER JOIN ims_lot l ON l.lotid = t.lotid " +
    		"WHERE trnstat = 0 AND trnqty <= 0 AND recordid IN (:entryIds) " +
    		"  AND l.status = 0 AND l.etype = 'GERMPLSM' " +
    		"GROUP BY recordid " +
    		"ORDER BY recordid ";
    		Query query = getSession().createSQLQuery(sql)
    		.setParameterList("entryIds", listEntryIds);
    		List<Object[]> result = query.list();
    		for (Object[] row : result) {
    			Integer entryId = (Integer) row[0];
    			BigInteger count = (BigInteger) row[1];
    			
    			lotCounts.put(entryId, count);
    		}
    		
		} catch (Exception e) {
			logAndThrowException("Error at countLotsWithReservationForListEntries=" + listEntryIds + " at TransactionDAO: " + e.getMessage(), e);
		}
			
		return lotCounts;
    }
    
    private boolean isGidInInventoryList(List<InventoryDetails> inventoryDetails, Integer gid){
    	for (InventoryDetails detail: inventoryDetails){
    		if (detail.getGid().equals(gid)){
    			return true;
    		}
    	}
    	return false;
    }
    
    
//    @SuppressWarnings("unchecked")
//    public List<Transaction> getByGids(List<Integer> gids) throws MiddlewareQueryException {
//    	List<Transaction> transactions = new ArrayList<Transaction>();
//    	
//    	if (gids == null || gids.isEmpty()){
//    		return transactions;
//    	}
//    	
//        try {
//                Criteria criteria = getSession().createCriteria(Transaction.class);
//                criteria.add(Restrictions.in("sourceRecordId", gids));
//                return criteria.list();
//        } catch (HibernateException e) {
//            logAndThrowException("Error with getByGids() query from Transaction: " + e.getMessage(), e);
//        }
//
//    	return transactions;
//    }
    
    @SuppressWarnings("unchecked")
    public List<Transaction> getByLotIds(List<Integer> lotIds) throws MiddlewareQueryException {
    	List<Transaction> transactions = new ArrayList<Transaction>();
    	
    	if (lotIds == null || lotIds.isEmpty()){
    		return transactions;
    	}
    	
        try {
                Criteria criteria = getSession().createCriteria(Transaction.class);
                criteria.add(Restrictions.in("lot.id", lotIds));
                return criteria.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getByLotIds() query from Transaction: " + e.getMessage(), e);
        }

    	return transactions;
    }
    
    
    public void cancelUnconfirmedTransactionsForListEntries(List<Integer> listEntryIds) throws MiddlewareQueryException{
    	try {
    		String sql = "UPDATE ims_transaction " +
    		"SET trnstat = 9, " +
    		"trndate = :currentDate " +
    		"WHERE trnstat = 0 AND recordid IN (:entryIds) " +
    		"AND sourceType = 'LIST'";
    		Query query = getSession().createSQLQuery(sql)
    			.setParameter("currentDate", Util.getCurrentDate())
    			.setParameterList("entryIds", listEntryIds);
    		query.executeUpdate();
		} catch (Exception e) {
			logAndThrowException("Error at cancelReservationForListEntries=" + listEntryIds + " at TransactionDAO: " + e.getMessage(), e);
		}
    }
    
    public void cancelUnconfirmedTransactionsForGermplasms(List<Integer> gids) throws MiddlewareQueryException{
    	try {
    		String sql = "UPDATE ims_transaction " +
    		"SET trnstat = 9, " +
    		"trndate = :currentDate " +
    		"WHERE trnstat = 0 AND sourceType = 'LIST' "+
    		"AND lotid in ( select lotid from ims_lot " +
    		"WHERE status = 0 AND etype = 'GERMPLSM' " +
    		"AND eid = (:gids))";
    		Query query = getSession().createSQLQuery(sql)
    			.setParameter("currentDate", Util.getCurrentDate())
    			.setParameterList("gids", gids);
    		query.executeUpdate();
		} catch (Exception e) {
			logAndThrowException("Error at cancelUnconfirmedTransactionsForGermplasms=" + gids + " at TransactionDAO: " + e.getMessage(), e);
		}
    }

    
}
