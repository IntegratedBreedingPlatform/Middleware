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
import org.generationcp.middleware.pojos.Lot;
import org.generationcp.middleware.pojos.Transaction;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link Lot}.
 * 
 */
public class LotDAO extends GenericDAO<Lot, Integer>{

    @SuppressWarnings("unchecked")
    public List<Lot> getByEntityType(String type, int start, int numOfRows) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(Lot.class);
            criteria.add(Restrictions.eq("entityType", type));
            criteria.setFirstResult(start);
            criteria.setMaxResults(numOfRows);
            return criteria.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getByEntityType(type=" + type + ") query from Lot: " + e.getMessage(), e);
        }
        return new ArrayList<Lot>();
    }

    public long countByEntityType(String type) throws MiddlewareQueryException {
        try {
    		Criteria criteria = getSession().createCriteria(Lot.class);
            criteria.setProjection(Projections.rowCount());
            criteria.add(Restrictions.eq("entityType", type));
            return ((Long) criteria.uniqueResult()).longValue(); 
        } catch (HibernateException e) {
            logAndThrowException("Error with countByEntityType(type=" + type + ") query from Lot: " + e.getMessage(), e);
        }
        return 0;
    }

    @SuppressWarnings("unchecked")
    public List<Lot> getByEntityTypeAndEntityId(String type, Integer entityId, int start, int numOfRows) throws MiddlewareQueryException {
        try {
        	if (entityId != null){
	            Criteria criteria = getSession().createCriteria(Lot.class);
	            criteria.add(Restrictions.eq("entityType", type));
	            criteria.add(Restrictions.eq("entityId", entityId));
	            criteria.setFirstResult(start);
	            criteria.setMaxResults(numOfRows);
	            return criteria.list();
        	}
        } catch (HibernateException e) {
            logAndThrowException("Error with getByEntityTypeAndEntityId(type=" + type + ", entityId=" + entityId
                    + ") query from Lot: " + e.getMessage(), e);
        }
        return new ArrayList<Lot>();
    }

    public long countByEntityTypeAndEntityId(String type, Integer entityId) throws MiddlewareQueryException {
        try {
        	if (entityId != null){
	            Criteria criteria = getSession().createCriteria(Lot.class);
	            criteria.setProjection(Projections.rowCount());
	            criteria.add(Restrictions.eq("entityType", type));
	            criteria.add(Restrictions.eq("entityId", entityId));
	            return ((Long) criteria.uniqueResult()).longValue(); 
        	}
        } catch (HibernateException e) {
            logAndThrowException("Error with countByEntityTypeAndEntityId(type=" + type + ", entityId=" + entityId
                    + ") query from Lot: " + e.getMessage(), e);
        }
        return 0;
    }

    @SuppressWarnings("unchecked")
    public List<Lot> getByEntityTypeAndLocationId(String type, Integer locationId, int start, int numOfRows)
            throws MiddlewareQueryException {
        try {
        	if (locationId != null){
	            Criteria criteria = getSession().createCriteria(Lot.class);
	            criteria.add(Restrictions.eq("entityType", type));
	            criteria.add(Restrictions.eq("locationId", locationId));
	            criteria.setFirstResult(start);
	            criteria.setMaxResults(numOfRows);
	            return criteria.list();
        	}
        } catch (HibernateException e) {
            logAndThrowException("Error with getByEntityTypeAndLocationId(type=" + type + ", locationId=" + locationId
                    + ") query from Lot: " + e.getMessage(), e);
        }
        return new ArrayList<Lot>();
    }

    public long countByEntityTypeAndLocationId(String type, Integer locationId) throws MiddlewareQueryException {
        try {
        	if (locationId != null){
	            Criteria criteria = getSession().createCriteria(Lot.class);
	            criteria.setProjection(Projections.rowCount());
	            criteria.add(Restrictions.eq("entityType", type));
	            criteria.add(Restrictions.eq("locationId", locationId));
	            return ((Long) criteria.uniqueResult()).longValue(); 
        	}
        } catch (HibernateException e) {
            logAndThrowException("Error with countByEntityTypeAndLocationId(type=" + type + ", locationId=" + locationId
                    + ") query from Lot: " + e.getMessage(), e);
        }
        return 0;
    }

    @SuppressWarnings("unchecked")
    public List<Lot> getByEntityTypeAndEntityIdAndLocationId(String type, Integer entityId, Integer locationId, int start, int numOfRows)
            throws MiddlewareQueryException {
        try {
        	if (entityId != null && locationId != null){
	            Criteria criteria = getSession().createCriteria(Lot.class);
	            criteria.add(Restrictions.eq("entityType", type));
	            criteria.add(Restrictions.eq("entityId", entityId));
	            criteria.add(Restrictions.eq("locationId", locationId));
	            criteria.setFirstResult(start);
	            criteria.setMaxResults(numOfRows);
	            return criteria.list();
        	}
        } catch (HibernateException e) {
            logAndThrowException("Error with getByEntityTypeAndEntityIdAndLocationId(type=" + type + ", entityId=" + entityId
                    + ", locationId=" + locationId + ") query from Lot: " + e.getMessage(), e);
        }
        return new ArrayList<Lot>();
    }

    public long countByEntityTypeAndEntityIdAndLocationId(String type, Integer entityId, Integer locationId)
            throws MiddlewareQueryException {
        try {
        	if (entityId != null && locationId != null){
	            Criteria criteria = getSession().createCriteria(Lot.class);
	            criteria.setProjection(Projections.rowCount());
	            criteria.add(Restrictions.eq("entityType", type));
	            criteria.add(Restrictions.eq("entityId", entityId));
	            criteria.add(Restrictions.eq("locationId", locationId));
	            return ((Long) criteria.uniqueResult()).longValue(); 
        	}
        } catch (HibernateException e) {
            logAndThrowException("Error with countByEntityTypeAndEntityIdAndLocationId(type=" + type + ", entityId="
                    + entityId + ", locationId=" + locationId + ") query from Lot: " + e.getMessage(), e);
        }
        return 0;
    }

    public Long getActualLotBalance(Integer lotId) throws MiddlewareQueryException {
        try {
        	if (lotId != null){
	            Lot lot = getById(lotId, false);
	            Criteria criteria = getSession().createCriteria(Transaction.class);
	            criteria.setProjection(Projections.sum("quantity"));
	            criteria.add(Restrictions.eq("lot", lot));
	            // get only committed transactions
	            criteria.add(Restrictions.eq("status", 1));
	            return (Long) criteria.uniqueResult();
        	}
        } catch (HibernateException e) {
            logAndThrowException("Error with getActualLotBalance(lotId=" + lotId + ") query from Lot: " + e.getMessage(), e);
        }
        return 0L;
    }

    public Long getAvailableLotBalance(Integer lotId) throws MiddlewareQueryException {
        try {
        	if (lotId != null){
	            Lot lot = getById(lotId, false);
	            Criteria criteria = getSession().createCriteria(Transaction.class);
	            criteria.setProjection(Projections.sum("quantity"));
	            criteria.add(Restrictions.eq("lot", lot));
	            // get all non-cancelled transactions
	            criteria.add(Restrictions.ne("status", 9));
	            return (Long) criteria.uniqueResult();
        	}
        } catch (HibernateException e) {
            logAndThrowException("Error with getAvailableLotBalance(lotId=" + lotId + ") query from Lot: " + e.getMessage(),
                    e);
        }
        return 0L;
    }

    @SuppressWarnings("unchecked")
    public List<Lot> getByEntityTypeEntityIdsLocationIdAndScaleId(String type, List<Integer> entityIds, Integer locationId, Integer scaleId)
            throws MiddlewareQueryException {
        try {
        	if (entityIds != null && !entityIds.isEmpty() && locationId != null){
	            Criteria criteria = getSession().createCriteria(Lot.class);
	            criteria.add(Restrictions.eq("entityType", type));
	            criteria.add(Restrictions.in("entityId", entityIds));
	            criteria.add(Restrictions.eq("locationId", locationId));
	            criteria.add(Restrictions.eq("scaleId", scaleId));
	            return criteria.list();
        	}
        } catch (HibernateException e) {
            logAndThrowException("Error with getByEntityTypeEntityIdLocationIdAndScaleId(type=" + type + ", entityIds=" + entityIds
                    + ", locationId=" + locationId + ", scaleId=" + scaleId + ") query from Lot: " + e.getMessage(), e);
        }
        return new ArrayList<Lot>();
    }
}
