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
import org.generationcp.middleware.pojos.gdms.AllelicValueWithMarkerIdElement;
import org.generationcp.middleware.pojos.gdms.CharValues;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;

/**
 * DAO class for {@link CharValues}.
 *
 * @author Joyce Avestro
 * 
 */
public class CharValuesDAO extends GenericDAO<CharValues, Integer>{

    /**
     * Gets the allelic values based on the given dataset id. The result is limited by the start and numOfRows parameters.
     * 
     * @param datasetId the dataset id
     * @param start the start of the rows to retrieve
     * @param numOfRows the number of rows to retrieve
     * @return the Allelic Values (germplasm id, data value, and marker id) for the given dataset id
     * @throws MiddlewareQueryException the MiddlewareQueryException
     */
    @SuppressWarnings("rawtypes")
    public List<AllelicValueWithMarkerIdElement> getAllelicValuesByDatasetId(Integer datasetId, int start, int numOfRows)
            throws MiddlewareQueryException {
        List<AllelicValueWithMarkerIdElement> toReturn = new ArrayList<AllelicValueWithMarkerIdElement>();

        try {
            if (datasetId != null) {
	            SQLQuery query = getSession().createSQLQuery(CharValues.GET_ALLELIC_VALUES_BY_DATASET_ID);
	            query.setParameter("datasetId", datasetId);
	            query.setFirstResult(start);
	            query.setMaxResults(numOfRows);
	            List results = query.list();
	
	            for (Object o : results) {
	                Object[] result = (Object[]) o;
	                if (result != null) {
	                    Integer gid = (Integer) result[0];
	                    Integer markerId = (Integer) result[1];
	                    String data = (String) result[2];
	                    AllelicValueWithMarkerIdElement allelicValueElement = new AllelicValueWithMarkerIdElement(gid, data, markerId);
	                    toReturn.add(allelicValueElement);
	                }
	            }
            }
        } catch (HibernateException e) {
            logAndThrowException("Error with getAllelicValuesByDatasetId(datasetId=" + datasetId
                    + ") queryfrom char_values : " + e.getMessage(), e);
        }
        return toReturn;
    }

    /**
     * Count by dataset id.
     *
     * @param datasetId the dataset id
     * @return the number of entries in char_values table corresponding to the given datasetId
     * @throws MiddlewareQueryException the MiddlewareQueryException
     */
    public long countByDatasetId(Integer datasetId) throws MiddlewareQueryException {
        try {
            if (datasetId != null) {
	            Query query = getSession().createSQLQuery(CharValues.COUNT_BY_DATASET_ID);
	            query.setParameter("datasetId", datasetId);
	            BigInteger result = (BigInteger) query.uniqueResult();
	            if (result != null) {
	                return result.longValue();
	            }
            }
        } catch (HibernateException e) {
            logAndThrowException("Error with countByDatasetId(datasetId=" + datasetId + ") query from char_values: "
                    + e.getMessage(), e);
        }
        return 0;
    }

    @SuppressWarnings("unchecked")
    public List<Integer> getGIDsByMarkerId(Integer markerId, int start, int numOfRows) throws MiddlewareQueryException {

        try {
            if (markerId != null) {
	            SQLQuery query = getSession().createSQLQuery(CharValues.GET_GIDS_BY_MARKER_ID);
	            query.setParameter("markerId", markerId);
	            query.setFirstResult(start);
	            query.setMaxResults(numOfRows);
	            return query.list();
	        }
        } catch (HibernateException e) {
            logAndThrowException("Error with getGIDsByMarkerId(markerId=" + markerId + ") query from CharValues: " + e.getMessage(), e);
        }
        return new ArrayList<Integer>();
    }

    public long countGIDsByMarkerId(Integer markerId) throws MiddlewareQueryException {
        try {
        	if (markerId != null){
	            SQLQuery query = getSession().createSQLQuery(CharValues.COUNT_GIDS_BY_MARKER_ID);
	            query.setParameter("markerId", markerId);
	            BigInteger result = (BigInteger) query.uniqueResult();
	            if (result != null) {
	                return result.longValue();
	            }
        	}
        } catch (HibernateException e) {
            logAndThrowException("Error with countGIDsByMarkerId(markerId=" + markerId + ") query from CharValues: " + e.getMessage(), e);
        }
        return 0;
    }

    public long countCharValuesByGids(List<Integer> gids) throws MiddlewareQueryException{
        try {
        	if (gids != null && !gids.isEmpty()){
	            SQLQuery query = getSession().createSQLQuery(CharValues.COUNT_CHAR_VALUES_BY_GIDS);
	            query.setParameterList("gids", gids);
	            BigInteger result = (BigInteger) query.uniqueResult();
	            if (result != null) {
	                return result.longValue();
	            }
        	}
        } catch (HibernateException e) {
            logAndThrowException("Error with countCharValuesByGids(gids=" + gids + ") query from CharValues: " + e.getMessage(), e);
        }
        return 0;
    }
    
    public void deleteByDatasetId(int datasetId) throws MiddlewareQueryException {
        try {
            this.flush();
            
            SQLQuery statement = getSession().createSQLQuery("DELETE FROM gdms_char_values WHERE dataset_id = " + datasetId);
            statement.executeUpdate();

            this.flush();
            this.clear();

        } catch(HibernateException e) {
            logAndThrowException("Error in deleteByDatasetId=" + datasetId + " in CharValuesDAO: " + e.getMessage(), e);
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<Integer> getMarkerIdsByGids(List<Integer> gIds) throws MiddlewareQueryException {

        try {
            if (gIds != null && gIds.size() > 0) {
                SQLQuery query = getSession().createSQLQuery(CharValues.GET_MARKER_IDS_BY_GIDS);
                query.setParameterList("gids", gIds);
                
                return query.list();
            }
        } catch (HibernateException e) {
            logAndThrowException("Error with getMarkerIdsByGids(gIds=" + gIds + ") query from CharValues: " + e.getMessage(), e);
        }
        return new ArrayList<Integer>();
    }


	@SuppressWarnings("rawtypes")
	public List<CharValues> getCharValuesByDatasetId(Integer datasetId) throws MiddlewareQueryException{
		
	    List<CharValues> toReturn = new ArrayList<CharValues>();
        try {
            if (datasetId != null){
                SQLQuery query = getSession().createSQLQuery(
                		"SELECT ac_id, dataset_id, marker_id, gid, CONCAT(char_value, '') " +
                		" FROM gdms_char_values where dataset_id = :datasetId "); 
                query.setParameter("datasetId", datasetId);

                List results = query.list();
                for (Object o : results) {
                    Object[] result = (Object[]) o;
                    if (result != null) {
                    	Integer acId = (Integer) result[0];
                        Integer datasetId2 =  (Integer) result[1];
                        Integer markerId = (Integer) result[2];
                        Integer gId = (Integer) result[3];
                        String charValue  = (String) result[4];
                        
                        CharValues dataElement = new CharValues(acId, datasetId2, markerId, gId, charValue);
                        toReturn.add(dataElement);
                    }
                }
            }
        } catch (HibernateException e) {
            logAndThrowException("Error with getCharValuesByDatasetId(datasetId=" + datasetId + ") query from CharValues " + e.getMessage(), e);
        }
        return toReturn;
	}

}
