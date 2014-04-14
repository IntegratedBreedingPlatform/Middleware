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
import org.generationcp.middleware.pojos.gdms.MappingPopValues;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;

/**
 * DAO class for {@link MappingPopValues}.
 *
 * @author Joyce Avestro
 * 
 */
public class MappingPopValuesDAO extends GenericDAO<MappingPopValues, Integer>{

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
        	if (datasetId != null){
	            SQLQuery query = getSession().createSQLQuery(MappingPopValues.GET_ALLELIC_VALUES_BY_DATASET_ID);
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
	            return toReturn;
        	}
        } catch (HibernateException e) {
        	logAndThrowException("Error with getAllelicValuesByDatasetId(datasetId=" + datasetId
                    + ") query from MappingPopValues: " + e.getMessage(), e);
        }
        return toReturn;

    }

    /**
     * Count by dataset id.
     *
     * @param datasetId the dataset id
     * @return the number of entries in mapping_pop_values table corresponding to the given datasetId
     * @throws MiddlewareQueryException the MiddlewareQueryException
     */
    public long countByDatasetId(Integer datasetId) throws MiddlewareQueryException {
        try {
        	if (datasetId != null){
	            Query query = getSession().createSQLQuery(MappingPopValues.COUNT_BY_DATASET_ID);
	            query.setParameter("datasetId", datasetId);
	            BigInteger result = (BigInteger) query.uniqueResult();
	            if (result != null) {
	                return result.longValue();
	            }
        	}
    	} catch (HibernateException e) {
        	logAndThrowException("Error with countByDatasetId(datasetId=" + datasetId + ") query from MappingPopValues: "
                    + e.getMessage(), e);
        }
        return 0;
    }

    /**
     * Gets the gids by marker id.
     *
     * @param markerId the marker id
     * @param start the start
     * @param numOfRows the num of rows
     * @return the gI ds by marker id
     * @throws MiddlewareQueryException the MiddlewareQueryException
     */
    @SuppressWarnings("unchecked")
    public List<Integer> getGIDsByMarkerId(Integer markerId, int start, int numOfRows) throws MiddlewareQueryException {

        try {
        	if (markerId != null){
            SQLQuery query = getSession().createSQLQuery(MappingPopValues.GET_GIDS_BY_MARKER_ID);
            query.setParameter("markerId", markerId);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);
            return query.list();
        	}
        } catch (HibernateException e) {
        	logAndThrowException("Error with getGIDsByMarkerId(markerId=" + markerId + ") query from MappingPopValues: " + e.getMessage(), e);
        }
        return new ArrayList<Integer>();
    }

    /**
     * Count gids by marker id.
     *
     * @param markerId the marker id
     * @return the long
     * @throws MiddlewareQueryException the MiddlewareQueryException
     */
    public long countGIDsByMarkerId(Integer markerId) throws MiddlewareQueryException {
        try {
        	if (markerId != null){
	            SQLQuery query = getSession().createSQLQuery(MappingPopValues.COUNT_GIDS_BY_MARKER_ID);
	            query.setParameter("markerId", markerId);
	            BigInteger result = (BigInteger) query.uniqueResult();
	            if (result != null) {
	                return result.longValue();
	            }
        	}
    	} catch (HibernateException e) {
        	logAndThrowException("Error with countGIDsByMarkerId(markerId=" + markerId + ") query from MappingPopValues: " + e.getMessage(), e);
        }
        return 0;
    }
    
    public long countByGids(List<Integer> gIds) throws MiddlewareQueryException {
        try {
            if (gIds != null && gIds.get(0) != null){
                SQLQuery query = getSession().createSQLQuery(MappingPopValues.COUNT_BY_GIDS);
                query.setParameterList("gIdList", gIds);
                BigInteger result = (BigInteger) query.uniqueResult();
                if (result != null) {
                    return result.longValue();
                }
            }
        } catch (HibernateException e) {
            logAndThrowException("Error with countByGids(gIds=" + gIds + ") query from MappingPopValues: " + e.getMessage(), e);
        }
        return 0;
    }

    public void deleteByDatasetId(int datasetId) throws MiddlewareQueryException {
        try {
            this.flush();
            
            SQLQuery statement = getSession().createSQLQuery("DELETE FROM gdms_mapping_pop_values WHERE dataset_id = " + datasetId);
            statement.executeUpdate();

            this.flush();
            this.clear();

        } catch(HibernateException e) {
            logAndThrowException("Error in deleteByDatasetId=" + datasetId + " in MappingPopValuesDAO: " + e.getMessage(), e);
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<Integer> getMarkerIdsByGids(List<Integer> gIds) throws MiddlewareQueryException {

        try {
            if (gIds != null && gIds.size() > 0) {
                SQLQuery query = getSession().createSQLQuery(MappingPopValues.GET_MARKER_IDS_BY_GIDS);
                query.setParameterList("gids", gIds);
                
                return query.list();
            }
        } catch (HibernateException e) {
            logAndThrowException("Error with getMarkerIdsByGids(gIds=" + gIds + ") query from MappingPopValuesDAO: " + e.getMessage(), e);
        }
        return new ArrayList<Integer>();
    }
    

	@SuppressWarnings("rawtypes")
	public List<MappingPopValues> getMappingPopValuesByDatasetId(Integer datasetId) throws MiddlewareQueryException{
		
	    List<MappingPopValues> toReturn = new ArrayList<MappingPopValues>();
        try {
            if (datasetId != null){
                SQLQuery query = getSession().createSQLQuery(
                		"SELECT mp_id, CONCAT(map_char_value, ''), dataset_id, gid, marker_id " +
                		" FROM gdms_mapping_pop_values where dataset_id = :datasetId "); 
                query.setParameter("datasetId", datasetId);

                List results = query.list();
                for (Object o : results) {
                    Object[] result = (Object[]) o;
                    if (result != null) {
                    	Integer mpId = (Integer) result[0];
                    	String mapCharValue = (String) result[1];
                        Integer datasetId2 =  (Integer) result[2];
                        Integer gId = (Integer) result[3];
                        Integer markerId = (Integer) result[4];
                        
                        MappingPopValues dataElement = new MappingPopValues(mpId, mapCharValue, datasetId2, gId, markerId);
                        toReturn.add(dataElement);
                    }
                }
            }
        } catch (HibernateException e) {
            logAndThrowException("Error with getMappingPopValuesByDatasetId(datasetId=" + datasetId + ") query from MappingPopValues " + e.getMessage(), e);
        }
        return toReturn;
	}
	
}
