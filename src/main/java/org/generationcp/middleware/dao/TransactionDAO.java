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
package org.generationcp.middleware.dao;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.Transaction;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link Transaction}.
 * 
 */
public class TransactionDAO extends GenericDAO<Transaction, Integer>{

    public void validateId(Transaction transaction) throws MiddlewareQueryException {
        // Check if not a local record (has negative ID)
		if (transaction != null) {
			Integer id = transaction.getId();
			if (id != null && id.intValue() > 0) {
				logAndThrowException("Error with validateId(transaction="
						+ transaction
						+ "): Cannot update a Central Database record. "
						+ "Transaction object to update must be a Local Record (ID must be negative)");
			}
		} else {
			logAndThrowException("Error with validateId(transaction="
					+ transaction + "): transaction is null. ");
		}
    }

    @SuppressWarnings("unchecked")
    public List<Transaction> getAllReserve(int start, int numOfRows) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(Transaction.class);
            criteria.add(Restrictions.eq("status", Integer.valueOf(0)));
            criteria.add(Restrictions.lt("quantity", Integer.valueOf(0)));
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
            criteria.add(Restrictions.lt("quantity", Integer.valueOf(0)));
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
            criteria.add(Restrictions.gt("quantity", Integer.valueOf(0)));
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
            criteria.add(Restrictions.gt("quantity", Integer.valueOf(0)));
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
	            criteria.add(Restrictions.lt("quantity", Integer.valueOf(0)));
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
	            criteria.add(Restrictions.lt("quantity", Integer.valueOf(0)));
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
	            criteria.add(Restrictions.gt("quantity", Integer.valueOf(0)));
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
	            criteria.add(Restrictions.gt("quantity", Integer.valueOf(0)));
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
            criteria.add(Restrictions.lt("quantity", Integer.valueOf(0)));
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
            criteria.add(Restrictions.lt("quantity", Integer.valueOf(0)));
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
    public List<Transaction> getLotWithMinimumAmount(long minAmount, int start, int numOfRows) throws MiddlewareQueryException {
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
}
