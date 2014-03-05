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
package org.generationcp.middleware.dao.gdms;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.gdms.Map;
import org.generationcp.middleware.pojos.gdms.MapDetailElement;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link Map}.
 *
 * <b>Author</b>: Michael Blancaflor
 * <br>
 * <b>File Created</b>: Jul 9, 2012.
 */
public class MapDAO extends GenericDAO<Map, Integer>{
    

    @SuppressWarnings("rawtypes")
    public List<MapDetailElement> getMapDetailsByName(String nameLike, int start, int numOfRows) throws MiddlewareQueryException {
    	
    	nameLike = nameLike.toLowerCase();
    	//nameLike = nameLike.replaceAll("'", "");
    	
        SQLQuery query = getSession().createSQLQuery(Map.GET_MAP_DETAILS_BY_NAME);
        query.setString("nameLike", nameLike);
        query.setFirstResult(start);
        query.setMaxResults(numOfRows);
        
        List<MapDetailElement> maps = new ArrayList<MapDetailElement>();
        
        try {

            List results = query.list();

            for (Object o : results) {
                Object[] result = (Object[]) o;
                if (result != null) {
                    int markerCount = ((BigInteger) result[0]).intValue();
                    Double maxStartPosition = (Double) result[1];
                    String linkageGroup = (String) result[2];
                    String mapName = (String) result[3];
                    String mapType = (String) result[4];
                    String mapDesc = (String) result[5];
                    String mapUnit = (String) result[6];
                    
                    MapDetailElement map = new MapDetailElement(markerCount, maxStartPosition, 
                    							linkageGroup, mapName, mapType, mapDesc, mapUnit);
                    maps.add(map);
                }
            }
            
            return maps;
                    
        } catch (HibernateException e) {
        	logAndThrowException("Error with getMapDetailsByName() query from Map: " + e.getMessage(), e);
        }	
        return maps;
    }

    
    public Map getByName(String mapName) throws MiddlewareQueryException {
        Map map = null;
        
        try{
            Criteria criteria = getSession().createCriteria(getPersistentClass());
            criteria.add(Restrictions.eq("mapName", mapName));
            map = (Map) criteria.uniqueResult(); 
            
        } catch (HibernateException e) {
            logAndThrowException("Error with getByName query from Map: " + e.getMessage(), e);
        }
    
        return map;        
    }

    
    public Long countMapDetailsByName(String nameLike) throws MiddlewareQueryException {

    	nameLike = nameLike.toLowerCase();
    	//nameLike = nameLike.replaceAll("'", "");
    	
        SQLQuery query = getSession().createSQLQuery(Map.COUNT_MAP_DETAILS_BY_NAME);
        query.setString("nameLike", nameLike);
        
        try {
            BigInteger result = (BigInteger) query.uniqueResult();
            return result.longValue();
        } catch (HibernateException e) {
        	logAndThrowException("Error with countMapDetailsByName() query from Map: " + e.getMessage(), e);
        }
        return 0L;
    }    
    
    @SuppressWarnings("rawtypes")
    public List<MapDetailElement> getAllMapDetails(int start, int numOfRows) throws MiddlewareQueryException {
        List<MapDetailElement> values = new ArrayList<MapDetailElement>();

        try {
            SQLQuery query = getSession().createSQLQuery(Map.GET_MAP_DETAILS);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);
            List results = query.list();

            for (Object o : results) {
                Object[] result = (Object[]) o;
                if (result != null) {
                    BigInteger markerCount = (BigInteger) result[0];
                    Double max  = (Double) result[1];
                    String linkageGroup = (String) result[2];
                    String mapName2 = (String) result[3];
                    String mapType = (String) result[4];
                    String mapDesc = (String) result[5];
                    String mapUnit = (String) result[6];
                    
                    MapDetailElement element = new MapDetailElement(markerCount.intValue(), max, 
                    		linkageGroup, mapName2, mapType, mapDesc, mapUnit);
                    values.add(element);
                }
            }

            return values;
        } catch (HibernateException e) {
        	logAndThrowException("Error with getAllMapDetails() query from Map & Mapping Data: " + e.getMessage(), e);
        }
        return values;
    }
    
    public long countAllMapDetails() throws MiddlewareQueryException {
        try {
            SQLQuery query = getSession().createSQLQuery(Map.COUNT_MAP_DETAILS);
            BigInteger result = (BigInteger) query.uniqueResult();
            if (result != null) {
                return result.longValue();
            }
            return 0;
        } catch (HibernateException e) {
        	logAndThrowException("Error with countAllMapDetails() query from Map & Mapping Data: " + e.getMessage(), e);
        }
        return 0;
    }

    public Integer getMapIdByName(String mapName) throws MiddlewareQueryException {
    	try {
    		SQLQuery query = getSession().createSQLQuery(Map.GET_MAP_ID_BY_NAME);
    		query.setParameter("mapName", mapName);
    		return (Integer) query.uniqueResult();
    		
    	} catch (HibernateException e) {
    		logAndThrowException("Error with getMapIdByName(" + mapName + ") in MapDAO: " + e.getMessage(), e);
    	}
    	return null;
    }
    
    @SuppressWarnings("rawtypes")
	public List<MapDetailElement> getMapAndMarkerCountByMarkers(List<Integer> markerIds) throws MiddlewareQueryException {
    	List<MapDetailElement> details = new ArrayList<MapDetailElement>();
    	try {
    		SQLQuery query = getSession().createSQLQuery(Map.GET_MAP_AND_MARKER_COUNT_BY_MARKERS);
    		query.setParameterList("markerIds", markerIds);
    		List results = query.list();
    		for (Object o : results) {
    			Object[] result = (Object[]) o;
    			details.add(new MapDetailElement(((BigInteger) result[1]).intValue(), null, null, result[0].toString(), null, null, null));
    		}
    		
    	} catch (HibernateException e) {
    		logAndThrowException("Error with getMapAndMarkerCountByMarkers(" + markerIds + ") in MapDAO: " + e.getMessage(), e);
    	}
    	return details;
    }
    
    public void deleteByMapId(int mapId) throws MiddlewareQueryException {
        try {
            this.flush();
            
            SQLQuery statement = getSession().createSQLQuery("DELETE FROM gdms_map WHERE map_id = " + mapId);
            statement.executeUpdate();

            this.flush();
            this.clear();

        } catch(HibernateException e) {
            logAndThrowException("Error in deleteByMapId=" + mapId + " in MapDAO: " + e.getMessage(), e);
        }
    }
    

    @SuppressWarnings("unchecked")
    public List<Map> getMapsByIds(List<Integer> mapIds) throws MiddlewareQueryException{
            try {
                Criteria criteria = getSession().createCriteria(getPersistentClass());
                criteria.add(Restrictions.in("mapId", mapIds));
                
                return criteria.list();

            } catch(HibernateException e) {
                logAndThrowException("Error in getMapsByIds=" + mapIds + " in MapDAO: " + e.getMessage(), e);
            }
            return new ArrayList<Map>();
 
    }
    
}