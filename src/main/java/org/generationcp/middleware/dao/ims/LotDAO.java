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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.inventory.LotAggregateData;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link Lot}.
 * 
 */
public class LotDAO extends GenericDAO<Lot, Integer>{

    private static final String GET_LOTS_FOR_LIST_ENTRIES = "SELECT lot.*, recordid, trnqty * -1 " +
	    					"FROM " +
	    					"   (SELECT i.lotid, i.eid, i.locid, scaleid, i.comments, " +
	    					"       SUM(CASE WHEN trnstat = 1 THEN trnqty ELSE 0 END) AS actual_balance, " +
	    					"       SUM(trnqty) AS available_balance " +
	    					"      FROM ims_lot i " +
	    					"      LEFT JOIN ims_transaction act ON act.lotid = i.lotid AND act.trnstat <> 9 " +
	    					"     WHERE i.status = 0 and i.eid  IN (:gids) " +
	    					"     GROUP BY i.lotid" +
	    					"   ) lot " +
	    					" LEFT JOIN ims_transaction res ON res.lotid = lot.lotid " +
	    					"  AND trnstat = 0 and trnqty < 0 " +
	    					"  AND sourceid = :listId and sourcetype = 'LIST' ";

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

    public Double getActualLotBalance(Integer lotId) throws MiddlewareQueryException {
        try {
        	if (lotId != null){
	            Lot lot = getById(lotId, false);
	            Criteria criteria = getSession().createCriteria(Transaction.class);
	            criteria.setProjection(Projections.sum("quantity"));
	            criteria.add(Restrictions.eq("lot", lot));
	            // get only committed transactions
	            criteria.add(Restrictions.eq("status", 1));
	            return (Double) criteria.uniqueResult();
        	}
        } catch (HibernateException e) {
            logAndThrowException("Error with getActualLotBalance(lotId=" + lotId + ") query from Lot: " + e.getMessage(), e);
        }
        return 0d;
    }

    public Double getAvailableLotBalance(Integer lotId) throws MiddlewareQueryException {
        try {
        	if (lotId != null){
	            Lot lot = getById(lotId, false);
	            Criteria criteria = getSession().createCriteria(Transaction.class);
	            criteria.setProjection(Projections.sum("quantity"));
	            criteria.add(Restrictions.eq("lot", lot));
	            // get all non-cancelled transactions
	            criteria.add(Restrictions.ne("status", 9));
	            return (Double) criteria.uniqueResult();
        	}
        } catch (HibernateException e) {
            logAndThrowException("Error with getAvailableLotBalance(lotId=" + lotId + ") query from Lot: " + e.getMessage(),
                    e);
        }
        return 0d;
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
    
    @SuppressWarnings("unchecked")
	public Map<Integer, BigInteger> countLotsWithAvailableBalance(List<Integer> gids) throws MiddlewareQueryException{
    	Map<Integer, BigInteger> lotCounts = new HashMap<Integer, BigInteger>();

    	try {
    		String sql = "SELECT entity_id, count(lotid) FROM ( " +
    						"SELECT i.lotid, i.eid AS entity_id, " +
    						"   SUM(trnqty) AS avail_bal " +
    						"  FROM ims_lot i " +
    						"  LEFT JOIN ims_transaction act ON act.lotid = i.lotid AND act.trnstat <> 9 " +
    						" WHERE i.status = 0 and i.eid  in (:gids) " +
    						" GROUP BY i.lotid ) inv " +
    					"WHERE avail_bal > 0 " +
    					"GROUP BY entity_id;";
    		
    		Query query = getSession().createSQLQuery(sql)
    		.setParameterList("gids", gids);
    		List<Object[]> result = query.list();
    		for (Object[] row : result) {
    			Integer gid = (Integer) row[0];
    			BigInteger count = (BigInteger) row[1];
    			
    			lotCounts.put(gid, count);
    		}
    		
		} catch (Exception e) {
			logAndThrowException("Error at countLotsWithAvailableBalance=" + gids + " at LotDAO: " + e.getMessage(), e);
		}
			
		return lotCounts;
    }
    
    
    @SuppressWarnings("unchecked")
    public List<Lot> getByEntityTypeAndEntityIds(String type, List<Integer> entityIds) throws MiddlewareQueryException {
        try {
        	if (entityIds != null && !entityIds.isEmpty()){
	            Criteria criteria = getSession().createCriteria(Lot.class);
	            criteria.add(Restrictions.eq("entityType", type));
	            criteria.add(Restrictions.in("entityId", entityIds));
	            return criteria.list();
        	}
        } catch (HibernateException e) {
            logAndThrowException("Error with getByEntityTypeAndEntityIds(type=" + type + ", entityIds=" + entityIds
                    + ") query from Lot: " + e.getMessage(), e);
        }
        return new ArrayList<Lot>();
    }
    
    
	public List<Lot> getLotAggregateDataForListEntry(Integer listId, Integer gid) throws MiddlewareQueryException{
    	List<Lot> lots = new ArrayList<Lot>();
    	
    	try {
    		String sql = GET_LOTS_FOR_LIST_ENTRIES + "ORDER by lot.lotid ";
    		
    		Query query = getSession().createSQLQuery(sql);
    		query.setParameterList("gids", Collections.singletonList(gid));
    		query.setParameter("listId", listId);
    		
    		createLotRows(lots, query);
    		
		} catch (Exception e) {
			logAndThrowException("Error at getLotAggregateDataForListEntry for list ID = " + listId + 
					" and GID = " + gid + " at LotDAO: " + e.getMessage(), e);
		}
		
    	return lots;
    }
    
	
	public List<Lot> getLotAggregateDataForList(Integer listId, List<Integer> gids) throws MiddlewareQueryException{
    	List<Lot> lots = new ArrayList<Lot>();
    	
    	try {
    		String sql = GET_LOTS_FOR_LIST_ENTRIES + "ORDER by lot.eid ";
    		
    		Query query = getSession().createSQLQuery(sql);
    		query.setParameterList("gids", gids);
    		query.setParameter("listId", listId);
    		
    		createLotRows(lots, query);
    		
		} catch (Exception e) {
			logAndThrowException("Error at getLotAggregateDataForListEntry for list ID = " + listId + 
					" and GIDs = " + gids + " at LotDAO: " + e.getMessage(), e);
		}
		
    	return lots;
    }

	@SuppressWarnings("unchecked")
	private void createLotRows(List<Lot> lots, Query query) {
		List<Object[]> result = query.list();
		
		Map<Integer, Double> reservationMap = null;
		Lot lot = null;
		
		for (Object[] row : result) {
			Integer lotId = (Integer) row[0];
			if (lot == null || !lot.getId().equals(lotId)) {
				if (lot != null && reservationMap != null){
					lot.getAggregateData().setReservationMap(reservationMap);
				}
				Integer entityId = (Integer) row[1];
				Integer locationId = (Integer) row[2];
				Integer scaleId = (Integer) row[3];
				String comments = (String) row[4];
				Double actualBalance = (Double) row[5];
				Double availableBalance = (Double) row[6];

				lot = new Lot(lotId);
				lot.setEntityId(entityId);
				lot.setLocationId(locationId);
				lot.setScaleId(scaleId);
				lot.setComments(comments);

				LotAggregateData aggregateData = new LotAggregateData(lotId);
				aggregateData.setActualBalance(actualBalance);
				aggregateData.setAvailableBalance(availableBalance);
				
				reservationMap = new HashMap<Integer, Double>();
				aggregateData.setReservationMap(reservationMap);
				lot.setAggregateData(aggregateData);
				
				lots.add(lot);
			}
			
			Integer recordId = (Integer) row[7];
			Double qty = (Double) row[8];
			if (recordId != null && qty != null){ // compute total reserved for entry
				Double prevValue = reservationMap.get(recordId);
				Double prevTotal = prevValue == null ? 0d : prevValue;
				reservationMap.put(recordId, prevTotal + qty);
			}
			
		}
		
		//set last lot's reservation map
		if (lot != null && reservationMap != null){
			lot.getAggregateData().setReservationMap(reservationMap);
		}
	}
    
    


}
