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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.gdms.MarkerOnMap;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link MarkerOnMap}.
 *
 * <b>Authors</b>: Dennis Billano <br>
 * <b>File Created</b>: Mar 7, 2013
 */

public class MarkerOnMapDAO extends GenericDAO<MarkerOnMap, Integer>{
    
    public void deleteByMapId(int mapId) throws MiddlewareQueryException {
        try {
            this.flush();
            
            SQLQuery statement = getSession().createSQLQuery("DELETE FROM gdms_markers_onmap WHERE map_id = " + mapId);
            statement.executeUpdate();

            this.flush();
            this.clear();

        } catch(HibernateException e) {
            logAndThrowException("Error in deleteByMapId=" + mapId + " in MarkerOnMapDAO: " + e.getMessage(), e);
        }
    }
    
    @SuppressWarnings("unchecked")
	public MarkerOnMap findByMarkerIdAndMapId(int markerId, int mapId) throws MiddlewareQueryException {
    	try {
    		
    		Query query = getSession().createQuery("FROM MarkerOnMap WHERE markerId = :markerId AND mapId = :mapId");
    		query.setParameter("markerId", markerId);
    		query.setParameter("mapId", mapId);
    		List<MarkerOnMap> result = query.list();
    		if (result != null && result.size() > 0) {
    			return result.get(0);
    		}
    		
    	} catch(HibernateException e) {
            logAndThrowException("Error in getByMarkerIdAndMapId(markerId=" + markerId + ", mapId=" + mapId + " in MarkerOnMapDAO: " + e.getMessage(), e);
    	}
    	return null;
    }
    
    
    @SuppressWarnings("unchecked")
    public Map<Integer, List<String>> getMapNameByMarkerIds(List<Integer> markerIds) throws MiddlewareQueryException{
        Map<Integer, List<String>> markerMaps = new HashMap<Integer, List<String>>();
        
        try {
            /*
                SELECT DISTINCT map_name, marker_id 
                FROM gdms_map map INNER JOIN gdms_markers_onmap markermap ON map.map_id = markermap.map_id
                WHERE markermap.marker_id IN (@markerIds);
            */

            StringBuilder sqlString = new StringBuilder()
            .append("SELECT DISTINCT marker_id, CONCAT(map_name,'') ")
            .append("FROM gdms_map map INNER JOIN gdms_markers_onmap markermap ON map.map_id = markermap.map_id ")
            .append(" WHERE markermap.marker_id IN (:markerIds) ")
            ;
        
            Query query = getSession().createSQLQuery(sqlString.toString());
            query.setParameterList("markerIds", markerIds);

            List<Object[]> list =  query.list();
            
            if (list != null && list.size() > 0) {
                for (Object[] row : list){
                    Integer markerId = (Integer) row[0];
                    String mapName = (String) row [1]; 

                    List<String> mapNames = new ArrayList<String>();
                    if (markerMaps.containsKey(markerId)){
                        mapNames = markerMaps.get(markerId);
                    }
                    mapNames.add(mapName);
                    markerMaps.put(markerId, mapNames);
                }
            }

        } catch(HibernateException e) {
            logAndThrowException("Error in getMapNameByMarkerIds() query from MarkerOnMap: " + e.getMessage(), e);
        }
        return markerMaps;
        
    }
    
    
    @SuppressWarnings("unchecked")
    public List<MarkerOnMap> getMarkersOnMapByMapId(Integer mapId) throws MiddlewareQueryException {

        List<MarkerOnMap> markersOnMap = new ArrayList<MarkerOnMap>();
        
        try{
            Criteria criteria = getSession().createCriteria(getPersistentClass());
            criteria.add(Restrictions.eq("mapId", mapId));
            criteria.addOrder(Order.asc("linkageGroup"));
            criteria.addOrder(Order.asc("startPosition"));
    
            return (List<MarkerOnMap>) criteria.list(); 
            
        } catch (HibernateException e) {
            logAndThrowException("Error with getByMapId query from MarkerOnMap: " + e.getMessage(), e);
        }
    
        return markersOnMap;
        
    }

}
