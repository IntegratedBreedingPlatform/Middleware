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
import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.pojos.gdms.Marker;
import org.generationcp.middleware.pojos.gdms.MarkerNameElement;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;

/**
 * <b>Description</b>: DAO for Marker object.
 * 
 * <br>
 * <br>
 * 
 * <b>Authors</b>: Mark Agarrado <br>
 * <b>File Created</b>: Jul 10, 2012
 */
@SuppressWarnings("unchecked")
public class MarkerDAO extends GenericDAO<Marker, Integer>{
    
    /**
     * Gets the ids by names.
     *
     * @param names the names
     * @param start the start
     * @param numOfRows the num of rows
     * @return the ids by names
     * @throws QueryException the query exception
     */
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

    /**
     * Gets the marker type by marker ids.
     *
     * @param markerIds the marker ids
     * @return the marker type by marker ids
     * @throws QueryException the query exception
     */
    public List<String> getMarkerTypeByMarkerIds(List<Integer> markerIds) throws QueryException{
        SQLQuery query = getSession().createSQLQuery(Marker.GET_MARKER_TYPE_BY_MARKER_IDS); 
        query.setParameterList("markerIdList", markerIds);
        return (List<String>) query.list();
    }
    
    /**
     * Gets the marker names by gids.
     * 
     * - Searches the allele_values, char_values, mapping_pop_values tables for the existence of gids.
     * - Gets marker ids from allele_values, char_values, mapping_pop_values by gids
     * - Gets marker name from marker table by marker ids
     * - Returns marker names matching the gids 
     *
     * @param gIds the g ids
     * @return the marker names by g ids
     * @throws QueryException the query exception
     */
    @SuppressWarnings("rawtypes")
    public List<MarkerNameElement> getMarkerNamesByGIds (List<Integer> gIds) throws QueryException {
        try {
            // Search the allele_values, char_values, mapping_pop_values tables for the existence of gids.
            // by getting alleleCount, charCount and mappingCount
            
            SQLQuery query = getSession().createSQLQuery(Marker.GET_ALLELE_COUNT_BY_GID);
            query.setParameterList("gIdList", gIds);
            BigInteger alleleCount = (BigInteger) query.uniqueResult();
            
            query = getSession().createSQLQuery(Marker.GET_CHAR_COUNT_BY_GID);
            query.setParameterList("gIdList", gIds);
            BigInteger charCount = (BigInteger) query.uniqueResult();
            
            query = getSession().createSQLQuery(Marker.GET_MAPPING_COUNT_BY_GID);
            query.setParameterList("gIdList", gIds);
            BigInteger mappingCount = (BigInteger) query.uniqueResult();

            // Used to store the result
            List<MarkerNameElement> dataValues = new ArrayList<MarkerNameElement>();
            
            // Retrieves markers that are being genotyped
            if (alleleCount.intValue() > 0){
                query = getSession().createSQLQuery(Marker.GET_ALLELE_MARKER_NAMES_BY_GID);
                query.setParameterList("gIdList", gIds);
                List results = query.list();                
                for (Object o : results) {
                    Object[] result = (Object[]) o;
                    if (result != null) {
                        Integer gId = (Integer) result[0];
                        String markerName = (String) result[1];
                        MarkerNameElement element = new MarkerNameElement(gId, markerName);
                        dataValues.add(element);
                    }
                }
            }
            
            if (charCount.intValue() > 0){
                query = getSession().createSQLQuery(Marker.GET_CHAR_MARKER_NAMES_BY_GID);
                query.setParameterList("gIdList", gIds);
                List results = query.list();                
                for (Object o : results) {
                    Object[] result = (Object[]) o;
                    if (result != null) {
                        Integer gId = (Integer) result[0];
                        String markerName = (String) result[1];
                        MarkerNameElement element = new MarkerNameElement(gId, markerName);
                        dataValues.add(element);
                    }
                }                
            }
            
            if (mappingCount.intValue() > 0){
                query = getSession().createSQLQuery(Marker.GET_MAPPING_MARKER_NAMES_BY_GID);
                query.setParameterList("gIdList", gIds);
                List results = query.list();                
                for (Object o : results) {
                    Object[] result = (Object[]) o;
                    if (result != null) {
                        Integer gId = (Integer) result[0];
                        String markerName = (String) result[1];
                        MarkerNameElement element = new MarkerNameElement(gId, markerName);
                        dataValues.add(element);
                    }
                }                
            }
            
            return dataValues;
            
        } catch (HibernateException ex) {
            throw new QueryException("Error with get Marker Names by list of GIds query: " + ex.getMessage());
        }
    }
    

}
