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

import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.gdms.MarkerOnMap;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;

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

}
