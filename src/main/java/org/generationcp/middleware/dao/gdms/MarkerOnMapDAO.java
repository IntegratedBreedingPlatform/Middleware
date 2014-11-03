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

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.gdms.MarkerOnMap;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        try {
            StringBuilder sqlString = new StringBuilder()
            .append("SELECT markeronmap_id, map_id, marker_id, start_position, end_position, linkage_group ")
            .append("FROM gdms_markers_onmap  ")
            .append("WHERE map_id = :mapId  ")
            .append("ORDER BY linkage_group, start_position ")
            ;
            Query query = getSession().createSQLQuery(sqlString.toString());
            query.setParameter("mapId", mapId);

            List<Object[]> list =  query.list();
            
            if (list != null && list.size() > 0) {
                for (Object[] row : list){
                    Integer markerOnMapId = (Integer) row[0];
                    Integer mapId2 = (Integer) row[1];
                    Integer markerId = (Integer) row[2];
                    Double startPosition = (Double) row[3];
                    Double endPosition = (Double) row[4];
                    String linkageGroup = (String) row [5]; 
                    
                    markersOnMap.add(new MarkerOnMap(markerOnMapId, mapId2, markerId, startPosition.floatValue(), 
                                            endPosition.floatValue(), linkageGroup));

                }
            }

        } catch(HibernateException e) {
            logAndThrowException("Error with getByMapId query from MarkerOnMap: " + e.getMessage(), e);
        }
    
        return markersOnMap;
        
    }
    
    @SuppressWarnings("unchecked")
	public List<Integer> getMarkerIdsByPositionAndLinkageGroup(double startPos, double endPos, String linkageGroup) 
    		throws MiddlewareQueryException {
    	List<Integer> toReturn = new ArrayList<Integer>();
        try {

            SQLQuery query;

            StringBuffer sql = new StringBuffer()
            .append("SELECT marker_id ")
            .append("FROM gdms_markers_onmap ")
            .append("WHERE linkage_group = :linkage_group ")
    		.append("AND start_position ")
			.append("BETWEEN :start_position ")
			.append("AND :end_position ") 
			.append("ORDER BY marker_id ")
			;
            
            query = getSession().createSQLQuery(sql.toString());
            query.setParameter("linkage_group", linkageGroup);
            query.setParameter("start_position", startPos);
            query.setParameter("end_position", endPos);
            
            toReturn = (List<Integer>)query.list();

        } catch (HibernateException e) {
            logAndThrowException("Error with getMarkersByPositionAndLinkageGroup(linkageGroup=" 
            		+ linkageGroup + ", startPos=" + startPos + ", endPos=" + endPos + ") query from gdms_markers_onmap: "
                    + e.getMessage(), e);
        }
        return toReturn;
    }

    
    @SuppressWarnings("unchecked")
    public List<MarkerOnMap> getMarkersOnMap(List<Integer> mapIds, String linkageGroup, double startPos, double endPos) throws MiddlewareQueryException {
        List<MarkerOnMap> markersOnMap = new ArrayList<MarkerOnMap>();

        try {
        	/*
		     	SELECT gdms_markers_onmap.* 
		     	FROM gdms_markers_onmap 
		   		WHERE map_id IN (:mapIds) AND linkage_group = :linkageGroup AND start_position BETWEEN :startPos AND :endPos 
		   		ORDER BY map_id, Linkage_group, start_position
        	 */

            StringBuilder sqlString = new StringBuilder()
            	.append("SELECT gdms_markers_onmap.* ") 
            	.append("FROM gdms_markers_onmap ") 
        		.append("WHERE map_id IN (:mapIds) AND linkage_group = :linkageGroup ")
        		.append("		AND start_position BETWEEN :startPos AND :endPos ") 
        		.append("ORDER BY map_id, Linkage_group, start_position ")
            ;
            Query query = getSession().createSQLQuery(sqlString.toString());
            query.setParameterList("mapIds", mapIds);
            query.setParameter("linkageGroup", linkageGroup);
            query.setParameter("startPos", startPos);
            query.setParameter("endPos", endPos);

            List<Object[]> list =  query.list();
            
            if (list != null && list.size() > 0) {
                for (Object[] row : list){
                    Integer markerOnMapId = (Integer) row[0];
                    Integer mapId2 = (Integer) row[1];
                    Integer markerId = (Integer) row[2];
                    Double startPosition = (Double) row[3];
                    Double endPosition = (Double) row[4];
                    String linkageGroup2 = (String) row [5]; 
                    
                    markersOnMap.add(new MarkerOnMap(markerOnMapId, mapId2, markerId, startPosition.floatValue(), 
                                            endPosition.floatValue(), linkageGroup2));

                }
            }

        } catch(HibernateException e) {
            logAndThrowException("Error with getMarkersOnMap query from MarkerOnMap: " + e.getMessage(), e);
        }
    
        return markersOnMap;
        
    }

	@SuppressWarnings("unchecked")
	public List<MarkerOnMap> getMarkersOnMapByMarkerIds(List<Integer> markerIds) throws MiddlewareQueryException {
        List<MarkerOnMap> markersOnMap = new ArrayList<MarkerOnMap>();

        try {
        	/*
		     	SELECT markeronmap_id, map_id, marker_id, start_position, end_position, linkage_group 
		     	FROM gdms_markers_onmap 
		   		WHERE marker_id IN (:markerIds) 
		   		ORDER BY map_id, Linkage_group, start_position
        	 */

            StringBuilder sqlString = new StringBuilder()
            	.append("SELECT markeronmap_id, map_id, marker_id, start_position, end_position, linkage_group ") 
            	.append("FROM gdms_markers_onmap ") 
        		.append("WHERE marker_id IN (:markerIds) ")
        		.append("ORDER BY map_id, Linkage_group, start_position ")
            ;
            Query query = getSession().createSQLQuery(sqlString.toString());
            query.setParameterList("markerIds", markerIds);

            List<Object[]> list =  query.list();
            
            if (list != null && list.size() > 0) {
                for (Object[] row : list){
                    Integer markerOnMapId = (Integer) row[0];
                    Integer mapId2 = (Integer) row[1];
                    Integer markerId = (Integer) row[2];
                    Double startPosition = (Double) row[3];
                    Double endPosition = (Double) row[4];
                    String linkageGroup2 = (String) row [5]; 
                    
                    markersOnMap.add(new MarkerOnMap(markerOnMapId, mapId2, markerId, startPosition.floatValue(), 
                                            endPosition.floatValue(), linkageGroup2));
                }
            }

        } catch(HibernateException e) {
            logAndThrowException("Error with getMarkersOnMapByMarkerIds query from MarkerOnMap: " + e.getMessage(), e);
        }
    
        return markersOnMap;	
    }
	
	@SuppressWarnings("unchecked")
	public List<Integer> getAllMarkerIds() throws MiddlewareQueryException{
        List<Integer> toReturn = new ArrayList<Integer>();

        try {
        	/*
		     	SELECT marker_id   
		     	FROM gdms_markers_onmap 
        	 */

            StringBuilder sqlString = new StringBuilder()
            	.append("SELECT marker_id ") 
            	.append("FROM gdms_markers_onmap ") 
            ;
            Query query = getSession().createSQLQuery(sqlString.toString());

            return query.list();

        } catch(HibernateException e) {
            logAndThrowException("Error with getAllMarkerIds query from MarkerOnMap: " + e.getMessage(), e);
        }
    
        return toReturn;	
	}
	
	
	
    
}
