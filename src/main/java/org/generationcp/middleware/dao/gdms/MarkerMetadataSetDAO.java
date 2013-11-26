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
import org.generationcp.middleware.pojos.gdms.MarkerMetadataSet;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;

/**
 * DAO class for {@link MarkerMetadataSet}.
 *
 * @author Joyce Avestro
 * 
 */
public class MarkerMetadataSetDAO extends GenericDAO<MarkerMetadataSet, Integer>{
    
    /**
     * Gets the marker id by dataset id.
     *
     * @param datasetId the dataset id
     * @return the marker id by dataset id
     * @throws MiddlewareQueryException the MiddlewareQueryException
     */
    @SuppressWarnings("unchecked")
    public List<Integer> getMarkerIdByDatasetId(Integer datasetId) throws MiddlewareQueryException{
    	try{
    		if (datasetId != null){
    			SQLQuery query = getSession().createSQLQuery(MarkerMetadataSet.GET_MARKER_ID_BY_DATASET_ID); 
    			query.setParameter("datasetId", datasetId);
    			return (List<Integer>) query.list();
    		}
    	} catch (HibernateException e) {
    		logAndThrowException("Error with getMarkerIdByDatasetId(datasetId=" + datasetId 
    				+ ") query from MarkerMetadataSet: " + e.getMessage(), e);
    	}
    	return new ArrayList<Integer>();

    }

    @SuppressWarnings("unchecked")
    public List<Integer> getMarkersByGidAndDatasetIds(Integer gid, List<Integer> datasetIds, int start, int numOfRows) 
    		throws MiddlewareQueryException {
        List<Integer> markerIds = new ArrayList<Integer>();

        try {
            if ((gid != null) && (datasetIds != null)) {
                SQLQuery query = getSession().createSQLQuery(MarkerMetadataSet.GET_MARKERS_BY_GID_AND_DATASETS);
                query.setParameterList("datasetids", datasetIds);
                query.setParameter("gid", gid);
                query.setFirstResult(start);
                query.setMaxResults(numOfRows);
                
                markerIds = query.list();     
            } else {
                return new ArrayList<Integer>();
            }
        } catch (HibernateException e) {
        	logAndThrowException("Error with getMarkersByGidAndDatasetIds(gid=" + gid + ", datasetIds=" 
        			+ datasetIds + ") query from MarkerMetadataSet: " + e.getMessage(), e);
        }
        return markerIds;
    }

    public long countMarkersByGidAndDatasetIds(Integer gid, List<Integer> datasetIds) throws MiddlewareQueryException{
        long count = 0;
        try {
            if (gid != null) {
                SQLQuery query = getSession().createSQLQuery(MarkerMetadataSet.COUNT_MARKERS_BY_GID_AND_DATASETS);
                query.setParameterList("datasetids", datasetIds);
                query.setParameter("gid", gid);
                BigInteger result = (BigInteger) query.uniqueResult();
                if (result != null) {
                    count = result.longValue();
                }
            }
        } catch (HibernateException e) {
        	logAndThrowException("Error with countMarkersByGidAndDatasetIds(gid=" + gid + ", datasetIds=" + datasetIds 
        			+ ") query from MarkerMetadataSet: " + e.getMessage(), e);
        }
        return count;
    }
    
    public long countByDatasetIds(List<Integer> datasetIds) throws MiddlewareQueryException {
        long count = 0;
        try {
            if (datasetIds != null && !datasetIds.isEmpty()) {
                SQLQuery query = getSession().createSQLQuery(MarkerMetadataSet.COUNT_MARKER_BY_DATASET_IDS);
                query.setParameterList("datasetIds", datasetIds);
                BigInteger result = (BigInteger) query.uniqueResult();
                if (result != null) {
                    count = result.longValue();
                }
            }
            
        } catch(HibernateException e) {
            logAndThrowException("Error with countByDatasetIds=" + datasetIds + ") query from MarkerMetadataSet: "
                    + e.getMessage(), e);
        }
        return count;
    }

    
    
    @SuppressWarnings("rawtypes")
    public List<MarkerMetadataSet> getByMarkerId(Integer markerId) throws MiddlewareQueryException{
        List<MarkerMetadataSet> toReturn = new ArrayList<MarkerMetadataSet>();
        try{
            if (markerId != null){
                SQLQuery query = getSession().createSQLQuery(MarkerMetadataSet.GET_BY_MARKER_ID); 
                query.setParameter("markerId", markerId);

                List results = query.list();
                for (Object o : results) {
                    Object[] result = (Object[]) o;
                    if (result != null) {
                        Integer datasetId =  (Integer) result[0];
                        Integer markerId2 = (Integer) result[1];

                        MarkerMetadataSet dataElement = new MarkerMetadataSet(datasetId, markerId2);
                        toReturn.add(dataElement);
                    }
                }
            }
        } catch (HibernateException e) {
            logAndThrowException("Error with getByMarkerId(markerId=" + markerId 
                    + ") query from MarkerMetadataSet: " + e.getMessage(), e);
        }
        return toReturn;

    }

    public void deleteByDatasetId(Integer datasetId) throws MiddlewareQueryException {
		try {
			this.flush();
			
			SQLQuery statement = getSession().createSQLQuery("DELETE FROM gdms_marker_metadataset WHERE dataset_id = " + datasetId);
			statement.executeUpdate();

			this.flush();
            this.clear();

		} catch(HibernateException e) {
			logAndThrowException("Error in deleteByDatasetId=" + datasetId + " in MarkerMetadataSetDAO: " + e.getMessage(), e);
		}
    }


}
