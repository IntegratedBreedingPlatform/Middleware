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
import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.pojos.gdms.Marker;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;

/**
 * <b>Description</b>: DAO for Marker object.
 * 
 * <br>
 * <br>
 * 
 * <b>Author</b>: Mark Agarrado <br>
 * <b>File Created</b>: Jul 10, 2012
 */
@SuppressWarnings("unchecked")
public class MarkerDAO extends GenericDAO<Marker, Integer>{
    public List<Integer> getIdsByNames (List<String> names, int start, int numOfRows) throws QueryException {
        try {
            SQLQuery query = getSession().createSQLQuery(Marker.GET_IDS_BY_NAMES);
            query.setParameterList("markerNameList", names);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);
            
            List<Integer> markerIds = query.list();
            
            return markerIds;
        } catch (HibernateException ex) {
            throw new QueryException("Error with get Marker IDs by list of Marker Names query: " + ex.getMessage());
        }
    }
}
