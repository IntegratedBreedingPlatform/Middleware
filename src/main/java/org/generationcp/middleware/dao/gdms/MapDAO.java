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
import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.gdms.Map;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;

/**
 * <b>Description</b>: DAO for Map table
 * 
 * <br>
 * <br>
 * 
 * <b>Author</b>: Michael Blancaflor
 * <br>
 * <b>File Created</b>: Jul 9, 2012.
 */
public class MapDAO extends GenericDAO<Map, Integer>{
    

    public List<Map> getMapDetailsByName(String nameLike, int start, int numOfRows) throws MiddlewareQueryException {
    	
    	nameLike = nameLike.toLowerCase();
    	nameLike = nameLike.replaceAll("'", "");
    	
        SQLQuery query = getSession().createSQLQuery(Map.GET_MAP_DETAILS_BY_NAME_LEFT + nameLike + Map.GET_MAP_DETAILS_BY_NAME_RIGHT);
        query.setFirstResult(start);
        query.setMaxResults(numOfRows);
        
        List<Map> maps = new ArrayList<Map>();
        try {
            List results = query.list();

            for (Object o : results) {
                Object[] result = (Object[]) o;
                if (result != null) {
                    Long markerCount = (Long) result[0];
                    Long maxStartPosition = (Long) result[1];
                    String linkageGroup = (String) result[2];
                    String mapName = (String) result[3];
                    String mapType = (String) result[4];
                    
                    Map map = new Map(markerCount, maxStartPosition, linkageGroup, mapName, mapType);
                    maps.add(map);
                }
            }
            return maps;        
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getMapDetailsByName() query from Map: " + e.getMessage(), e);
        }	
    }

    
    public Long countMapDetailsByName(String nameLike) throws MiddlewareQueryException {

    	nameLike = nameLike.toLowerCase();
    	nameLike = nameLike.replaceAll("'", "");
    	
        SQLQuery query = getSession().createSQLQuery(Map.COUNT_MAP_DETAILS_BY_NAME_LEFT + nameLike + Map.COUNT_MAP_DETAILS_BY_NAME_RIGHT);

        try {
            Long result = (Long) query.uniqueResult();
            if (result != null) {
                return result.longValue();
            }
        	return (long) 0;            
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with countMapDetailsByName() query from Map: " + e.getMessage(), e);
        }
    }    
    
    
}