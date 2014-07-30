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
import org.generationcp.middleware.pojos.gdms.MapInfo;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.FloatType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;

/**
 * DAO class for {@link Map}.
 *
 * <b>Author</b>: Michael Blancaflor
 * <br>
 * <b>File Created</b>: Jul 9, 2012.
 */
public class MapDAO extends GenericDAO<Map, Integer>{
    

    private static final String GET_MAP_DETAILS_SELECT = 
            "SELECT COUNT(DISTINCT gdms_mapping_data.marker_id) AS marker_count " +
            "       , MAX(gdms_mapping_data.start_position) AS max " +
            "       , gdms_mapping_data.linkage_group AS Linkage_group " +
            "       , concat(gdms_mapping_data.map_name,'') AS map " +
            "       , concat(gdms_map.map_type,'') AS map_type " +
            "       , gdms_map.map_desc AS map_desc " +
            "       , gdms_map.map_unit AS map_unit " +
            "FROM gdms_mapping_data, gdms_map " +
            "WHERE gdms_mapping_data.map_id=gdms_map.map_id " 
            ;

    private static final String GET_MAP_DETAILS_WHERE = 
            "       AND lower(gdms_mapping_data.map_name) LIKE (:nameLike) " ;

    private static final String GET_MAP_DETAILS_GROUP_ORDER = 
            "GROUP BY gdms_mapping_data.linkage_group, gdms_mapping_data.map_name " +
                    "ORDER BY gdms_mapping_data.map_name, gdms_mapping_data.linkage_group "
                    ;

    public static final String GET_MAP_DETAILS = 
            GET_MAP_DETAILS_SELECT + GET_MAP_DETAILS_GROUP_ORDER;
    
    public static final String GET_MAP_DETAILS_BY_NAME = 
            GET_MAP_DETAILS_SELECT + GET_MAP_DETAILS_WHERE + GET_MAP_DETAILS_GROUP_ORDER;

    public static final String COUNT_MAP_DETAILS = 
            "SELECT COUNT(DISTINCT gdms_mapping_data.linkage_group, gdms_mapping_data.map_name) " +
            "FROM `gdms_mapping_data` JOIN `gdms_map` ON gdms_mapping_data.map_id=gdms_map.map_id "
            ;

    public static final String COUNT_MAP_DETAILS_BY_NAME = 
            COUNT_MAP_DETAILS + "WHERE lower(gdms_mapping_data.map_name) LIKE (:nameLike) ";
    
    public static final String GET_MAP_ID_BY_NAME = 
    		"SELECT map_id FROM gdms_map WHERE map_name = :mapName LIMIT 0,1";

    public static final String GET_MAP_AND_MARKER_COUNT_BY_MARKERS = 
    		"SELECT CONCAT(m.map_name, ''), COUNT(k.marker_id) " +
    		"FROM gdms_map m " +
    		"INNER JOIN gdms_markers_onmap k ON k.map_id = m.map_id " +
    		"WHERE k.marker_id IN (:markerIds) " +
    		"GROUP BY m.map_name";
    
    public static final String GET_MAP_INFO_BY_MAP_AND_CHROMOSOME =
            "SELECT DISTINCT "
            + "  gdms_markers_onmap.marker_id"
            + " ,gdms_marker.marker_name"
            + " ,gdms_map.map_name"
            + " ,gdms_map.map_type"
            + " ,gdms_markers_onmap.start_position"
            + " ,gdms_markers_onmap.linkage_group"
            + " ,gdms_map.map_unit"
            + " FROM gdms_map"
            + "     INNER JOIN gdms_markers_onmap ON"
            + "         gdms_map.map_id = gdms_markers_onmap.map_id"
            + "     LEFT JOIN gdms_marker ON"
            + "         gdms_marker.marker_id = gdms_markers_onmap.marker_id"
            + " WHERE"
            + "     gdms_markers_onmap.map_id = :mapId"
            + "     AND gdms_markers_onmap.linkage_group = :chromosome"
            ;
    
    public static final String GET_MAP_INFO_BY_MAP_CHROMOSOME_AND_POSITION =
            "SELECT DISTINCT "
            + "  gdms_markers_onmap.marker_id"
            + " ,gdms_marker.marker_name"
            + " ,gdms_map.map_name"
            + " ,gdms_map.map_type"
            + " ,gdms_markers_onmap.linkage_group"
            + " ,gdms_map.map_unit"
            + " FROM gdms_map"
            + "     INNER JOIN gdms_markers_onmap ON"
            + "         gdms_map.map_id = gdms_markers_onmap.map_id"
            + "     LEFT JOIN gdms_marker ON"
            + "         gdms_marker.marker_id = gdms_markers_onmap.marker_id"
            + " WHERE"
            + "     gdms_markers_onmap.map_id = :mapId"
            + "     AND gdms_markers_onmap.linkage_group = :chromosome"
            + "     AND gdms_markers_onmap.start_position = :startPosition"
            + " ORDER BY"
            + "      gdms_map.map_name"
            + "     ,gdms_markers_onmap.linkage_group"
            + "     ,gdms_markers_onmap.start_position ASC"
            ;
    
    public static final String GET_MAP_INFO_BY_MARKERS_AND_MAP =
            "SELECT DISTINCT "
            + "  gdms_markers_onmap.marker_id"
            + " ,gdms_marker.marker_name"
            + " ,gdms_map.map_name"
            + " ,gdms_map.map_type"
            + " ,gdms_markers_onmap.start_position"
            + " ,gdms_markers_onmap.linkage_group"
            + " ,gdms_map.map_unit"
            + " FROM gdms_map"
            + "     INNER JOIN gdms_markers_onmap ON"
            + "         gdms_map.map_id = gdms_markers_onmap.map_id"
            + "     LEFT JOIN gdms_marker ON"
            + "         gdms_marker.marker_id = gdms_markers_onmap.marker_id"
            + " WHERE"
            + "     gdms_markers_onmap.marker_id IN (:markerIdList)"
            + "     AND gdms_markers_onmap.map_id = :mapId"
            + " ORDER BY"
            + "     gdms_map.map_name"
            + "     ,gdms_markers_onmap.linkage_group"
            + "     ,gdms_markers_onmap.start_position ASC"
            ;
 
	
    @SuppressWarnings("rawtypes")
    public List<MapDetailElement> getMapDetailsByName(String nameLike, int start, int numOfRows) throws MiddlewareQueryException {
    	
    	nameLike = nameLike.toLowerCase();
    	//nameLike = nameLike.replaceAll("'", "");
    	
        SQLQuery query = getSession().createSQLQuery(GET_MAP_DETAILS_BY_NAME);
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

    public List<MapInfo> getMapInfoByMapAndChromosome(Integer mapId, String chromosome) throws MiddlewareQueryException {
        SQLQuery query = getSession().createSQLQuery(GET_MAP_INFO_BY_MAP_AND_CHROMOSOME);
        query.setInteger("mapId", mapId);
        query.setString("chromosome", chromosome);
        
        query.addScalar("marker_id", new IntegerType());
        query.addScalar("marker_name", new StringType());
        query.addScalar("map_name", new StringType());
        query.addScalar("map_type", new StringType());
        query.addScalar("start_position", new FloatType());
        query.addScalar("linkage_group", new StringType());
        query.addScalar("map_unit", new StringType());
        
        List<MapInfo> mapInfoList = new ArrayList<MapInfo>();
        
        try {
            @SuppressWarnings("rawtypes")
            List results = query.list();

            for (Object o : results) {
                Object[] result = (Object[]) o;
                
                if (result != null) {
                    Integer markerId        = (Integer) result[0];
                    String markerName       = (String) result[1];
                    String mapName          = (String) result[2];
                    String mapType          = (String) result[3];
                    Float startPosition     = (Float) result[4];
                    String linkageGroup     = (String) result[5];
                    String mapUnit          = (String) result[6];
                    
                    MapInfo mapInfo = new MapInfo(markerId, markerName, mapId, mapName, linkageGroup, startPosition, mapType, mapUnit);
                    mapInfoList.add(mapInfo);
                }
            }
        } catch (HibernateException e) {
            logAndThrowException("Error with getMapInfoByMapAndChromosome() query: " + e.getMessage(), e);
        }
        
        return mapInfoList;
    }
    
    public List<MapInfo> getMapInfoByMapChromosomeAndPosition(Integer mapId, String chromosome, Float startPosition) throws MiddlewareQueryException {
        SQLQuery query = getSession().createSQLQuery(GET_MAP_INFO_BY_MAP_CHROMOSOME_AND_POSITION);
        query.setInteger("mapId", mapId);
        query.setString("chromosome", chromosome);
        query.setFloat("startPosition", startPosition);
        
        query.addScalar("marker_id", new IntegerType());
        query.addScalar("marker_name", new StringType());
        query.addScalar("map_name", new StringType());
        query.addScalar("map_type", new StringType());
        query.addScalar("linkage_group", new StringType());
        query.addScalar("map_unit", new StringType());
        
        List<MapInfo> mapInfoList = new ArrayList<MapInfo>();
        
        try {
            @SuppressWarnings("rawtypes")
            List results = query.list();

            for (Object o : results) {
                Object[] result = (Object[]) o;
                
                if (result != null) {
                    Integer markerId        = (Integer) result[0];
                    String markerName       = (String) result[1];
                    String mapName          = (String) result[2];
                    String mapType          = (String) result[3];
                    String linkageGroup     = (String) result[4];
                    String mapUnit          = (String) result[5];
                    
                    MapInfo mapInfo = new MapInfo(markerId, markerName, mapId, mapName, linkageGroup, startPosition, mapType, mapUnit);
                    mapInfoList.add(mapInfo);
                }
            }
        } catch (HibernateException e) {
            logAndThrowException("Error with getMapInfoByMapChromosomeAndPosition() query: " + e.getMessage(), e);
        }
        
        return mapInfoList;
    }
    
    public List<MapInfo> getMapInfoByMarkersAndMap(List<Integer> markers, Integer mapId) throws MiddlewareQueryException {
        SQLQuery query = getSession().createSQLQuery(GET_MAP_INFO_BY_MARKERS_AND_MAP);
        query.setParameterList("markerIdList", markers);
        query.setInteger("mapId", mapId);
        
        query.addScalar("marker_id", new IntegerType());
        query.addScalar("marker_name", new StringType());
        query.addScalar("map_name", new StringType());
        query.addScalar("map_type", new StringType());
        query.addScalar("start_position", new FloatType());
        query.addScalar("linkage_group", new StringType());
        query.addScalar("map_unit", new StringType());
        
        List<MapInfo> mapInfoList = new ArrayList<MapInfo>();
        
        try {
            @SuppressWarnings("rawtypes")
            List results = query.list();

            for (Object o : results) {
                Object[] result = (Object[]) o;
                
                if (result != null) {
                    Integer markerId        = (Integer) result[0];
                    String markerName       = (String) result[1];
                    String mapName          = (String) result[2];
                    String mapType          = (String) result[3];
                    Float startPosition     = (Float) result[4];
                    String linkageGroup     = (String) result[5];
                    String mapUnit          = (String) result[6];
                    
                    MapInfo mapInfo = new MapInfo(markerId, markerName, mapId, mapName, linkageGroup, startPosition, mapType, mapUnit);
                    mapInfoList.add(mapInfo);
                }
            }
        } catch (HibernateException e) {
            logAndThrowException("Error with getMapInfoByMapAndChromosome() query: " + e.getMessage(), e);
        }
        
        return mapInfoList;
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
    	
        SQLQuery query = getSession().createSQLQuery(COUNT_MAP_DETAILS_BY_NAME);
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
            SQLQuery query = getSession().createSQLQuery(GET_MAP_DETAILS);
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
            SQLQuery query = getSession().createSQLQuery(COUNT_MAP_DETAILS);
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
    		SQLQuery query = getSession().createSQLQuery(GET_MAP_ID_BY_NAME);
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
    		SQLQuery query = getSession().createSQLQuery(GET_MAP_AND_MARKER_COUNT_BY_MARKERS);
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