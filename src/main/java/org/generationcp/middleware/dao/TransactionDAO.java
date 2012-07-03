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

import java.util.List;

import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.pojos.Transaction;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public class TransactionDAO extends GenericDAO<Transaction, Integer>{

    public void validateId(Transaction transaction) throws QueryException {
        // Check if not a local record (has negative ID)
        Integer id = transaction.getId();
        if (id != null && id.intValue() > 0) {
            throw new QueryException("Cannot update a Central Database record. "
                    + "Attribute object to update must be a Local Record (ID must be negative)");
        }
    }

    @SuppressWarnings("unchecked")
    public List<Transaction> getAllReserve(int start, int numOfRows) {
        Criteria crit = getSession().createCriteria(Transaction.class);
        crit.add(Restrictions.eq("status", Integer.valueOf(0)));
        crit.add(Restrictions.lt("quantity", Integer.valueOf(0)));
        crit.setFirstResult(start);
        crit.setMaxResults(numOfRows);
        return crit.list();
    }

    public Long countAllReserve() {
        Criteria crit = getSession().createCriteria(Transaction.class);
        crit.setProjection(Projections.rowCount());
        crit.add(Restrictions.eq("status", Integer.valueOf(0)));
        crit.add(Restrictions.lt("quantity", Integer.valueOf(0)));
        return (Long) crit.uniqueResult(); //count
    }

    @SuppressWarnings("unchecked")
    public List<Transaction> getAllDeposit(int start, int numOfRows) {
        Criteria crit = getSession().createCriteria(Transaction.class);
        crit.add(Restrictions.eq("status", Integer.valueOf(0)));
        crit.add(Restrictions.gt("quantity", Integer.valueOf(0)));
        crit.setFirstResult(start);
        crit.setMaxResults(numOfRows);
        return crit.list();
    }

    public Long countAllDeposit() {
        Criteria crit = getSession().createCriteria(Transaction.class);
        crit.setProjection(Projections.rowCount());
        crit.add(Restrictions.eq("status", Integer.valueOf(0)));
        crit.add(Restrictions.gt("quantity", Integer.valueOf(0)));
        return (Long) crit.uniqueResult(); //count
    }

    @SuppressWarnings("unchecked")
    public List<Transaction> getAllReserveByRequestor(Integer personId, int start, int numOfRows) {
        Criteria crit = getSession().createCriteria(Transaction.class);
        crit.add(Restrictions.eq("status", Integer.valueOf(0)));
        crit.add(Restrictions.lt("quantity", Integer.valueOf(0)));
        crit.add(Restrictions.eq("personId", personId));
        crit.setFirstResult(start);
        crit.setMaxResults(numOfRows);
        return crit.list();
    }

    public Long countAllReserveByRequestor(Integer personId) {
        Criteria crit = getSession().createCriteria(Transaction.class);
        crit.setProjection(Projections.rowCount());
        crit.add(Restrictions.eq("status", Integer.valueOf(0)));
        crit.add(Restrictions.lt("quantity", Integer.valueOf(0)));
        crit.add(Restrictions.eq("personId", personId));
        return (Long) crit.uniqueResult(); //count
    }

    @SuppressWarnings("unchecked")
    public List<Transaction> getAllDepositByDonor(Integer personId, int start, int numOfRows) {
        Criteria crit = getSession().createCriteria(Transaction.class);
        crit.add(Restrictions.eq("status", Integer.valueOf(0)));
        crit.add(Restrictions.gt("quantity", Integer.valueOf(0)));
        crit.add(Restrictions.eq("personId", personId));
        crit.setFirstResult(start);
        crit.setMaxResults(numOfRows);
        return crit.list();
    }

    public Long countAllDepositByDonor(Integer personId) {
        Criteria crit = getSession().createCriteria(Transaction.class);
        crit.setProjection(Projections.rowCount());
        crit.add(Restrictions.eq("status", Integer.valueOf(0)));
        crit.add(Restrictions.gt("quantity", Integer.valueOf(0)));
        crit.add(Restrictions.eq("personId", personId));
        return (Long) crit.uniqueResult(); //count
    }

    @SuppressWarnings("unchecked")
    public List<Transaction> getAllUncommitted(int start, int numOfRows) {
        Criteria crit = getSession().createCriteria(Transaction.class);
        crit.add(Restrictions.eq("status", Integer.valueOf(0)));
        crit.setFirstResult(start);
        crit.setMaxResults(numOfRows);
        return crit.list();
    }

    public Long countAllUncommitted() {
        Criteria crit = getSession().createCriteria(Transaction.class);
        crit.setProjection(Projections.rowCount());
        crit.add(Restrictions.eq("status", Integer.valueOf(0)));
        return (Long) crit.uniqueResult(); //count
    }

    @SuppressWarnings("unchecked")
    public List<Transaction> getAllWithdrawals(int start, int numOfRows) {
        Criteria crit = getSession().createCriteria(Transaction.class);
        crit.add(Restrictions.lt("quantity", Integer.valueOf(0)));
        crit.setFirstResult(start);
        crit.setMaxResults(numOfRows);
        return crit.list();
    }

    public Long countAllWithdrawals() {
        Criteria crit = getSession().createCriteria(Transaction.class);
        crit.setProjection(Projections.rowCount());
        crit.add(Restrictions.lt("quantity", Integer.valueOf(0)));
        return (Long) crit.uniqueResult(); //count
    }

    @SuppressWarnings("unchecked")
    public List<Transaction> getEmptyLot(int start, int numOfRows) {
        Query query = getSession().getNamedQuery(Transaction.GET_EMPTY_LOT);
        query.setFirstResult(start);
        query.setMaxResults(numOfRows);
        return (List<Transaction>) query.list();

    }

    @SuppressWarnings("unchecked")
    public List<Transaction> getLotWithMinimumAmount(long minAmount, int start, int numOfRows) {
        Query query = getSession().getNamedQuery(Transaction.GET_LOT_WITH_MINIMUM_AMOUNT);
        query.setFirstResult(start);
        query.setMaxResults(numOfRows);
        query.setParameter("minAmount", minAmount);
        return (List<Transaction>) query.list();
    }
}
