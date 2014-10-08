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
import org.generationcp.middleware.pojos.gdms.MarkerMetadataSet;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO class for {@link MarkerMetadataSet}.
 *
 * @author Joyce Avestro
 * 
 */
public class MarkerMetadataSetDAO extends GenericDAO<MarkerMetadataSet, Integer>{
    
    public static final String GET_MARKER_ID_BY_DATASET_ID = 
            "SELECT marker_id " +
            "FROM gdms_marker_metadataset " +
            "WHERE dataset_id = :datasetId " +
            "ORDER BY marker_id;";

    public static final String GET_MARKERS_BY_GID_AND_DATASETS = 
            "SELECT DISTINCT marker_id " + 
            "FROM gdms_marker_metadataset JOIN gdms_acc_metadataset " +
            "        ON gdms_marker_metadataset.dataset_id = gdms_acc_metadataset.dataset_id " + 
            "WHERE gdms_marker_metadataset.dataset_id in (:datasetids)  " +
            "    AND gdms_acc_metadataset.gid = :gid " + 
            "ORDER BY gdms_marker_metadataset.marker_id ";
    
    public static final String COUNT_MARKERS_BY_GID_AND_DATASETS = 
            "SELECT COUNT(DISTINCT marker_id) " + 
            "FROM gdms_marker_metadataset JOIN gdms_acc_metadataset " +
            "        ON gdms_marker_metadataset.dataset_id = gdms_acc_metadataset.dataset_id " + 
            "WHERE gdms_marker_metadataset.dataset_id in (:datasetids)  " +
            "    AND gdms_acc_metadataset.gid = :gid " + 
            "ORDER BY gdms_marker_metadataset.marker_id ";
    
    public static final String GET_BY_MARKER_IDS = 
            "SELECT marker_metadataset_id, dataset_id, marker_id, marker_sample_id " +
            "FROM gdms_marker_metadataset " +
            "WHERE  marker_id IN (:markerIdList) ";
   
    public static final String COUNT_MARKER_BY_DATASET_IDS = 
            "SELECT COUNT(DISTINCT marker_id) " 
            + "FROM gdms_marker_metadataset "
            + "WHERE dataset_id IN (:datasetIds)";

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
    			SQLQuery query = getSession().createSQLQuery(GET_MARKER_ID_BY_DATASET_ID); 
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
                SQLQuery query = getSession().createSQLQuery(GET_MARKERS_BY_GID_AND_DATASETS);
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
                SQLQuery query = getSession().createSQLQuery(COUNT_MARKERS_BY_GID_AND_DATASETS);
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
                SQLQuery query = getSession().createSQLQuery(COUNT_MARKER_BY_DATASET_IDS);
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
    public List<MarkerMetadataSet> getByMarkerIds(List<Integer> markerIds) throws MiddlewareQueryException{
        List<MarkerMetadataSet> toReturn = new ArrayList<MarkerMetadataSet>();
        try{
            if (markerIds != null && markerIds.size() > 0){
                SQLQuery query = getSession().createSQLQuery(GET_BY_MARKER_IDS); 
                query.setParameterList("markerIdList", markerIds);

                List results = query.list();
                for (Object o : results) {
                    Object[] result = (Object[]) o;
                    if (result != null) {
                        Integer markerMetadatasetId =  (Integer) result[0];
                        Integer datasetId = (Integer) result[1];
                        Integer markerId2 =  (Integer) result[2];
                        Integer markerSampleId = (Integer) result[3];

                        MarkerMetadataSet dataElement = new MarkerMetadataSet(markerMetadatasetId, datasetId, markerId2, markerSampleId);
                        toReturn.add(dataElement);
                    }
                }
            }
        } catch (HibernateException e) {
            logAndThrowException("Error with getByMarkerIds(markerIds=" + markerIds 
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

	@SuppressWarnings("rawtypes")
	public List<MarkerMetadataSet> getMarkerMetadataSetsByDatasetId(Integer datasetId) throws MiddlewareQueryException{
		
	    List<MarkerMetadataSet> toReturn = new ArrayList<MarkerMetadataSet>();
        try {
            if (datasetId != null){
                SQLQuery query = getSession().createSQLQuery("SELECT  marker_metadataset_id, dataset_id, marker_id, marker_sample_id" +
                		" FROM gdms_marker_metadataset where dataset_id = :datasetId "); 
                query.setParameter("datasetId", datasetId);

                List results = query.list();
                for (Object o : results) {
                    Object[] result = (Object[]) o;
                    if (result != null) {
                        Integer markerMetadatasetId =  (Integer) result[0];
                        Integer datasetId2 = (Integer) result[1];
                        Integer markerId =  (Integer) result[0];
                        Integer markerSampleId = (Integer) result[1];

                        MarkerMetadataSet dataElement = new MarkerMetadataSet(markerMetadatasetId, datasetId2, markerId, markerSampleId);
                        toReturn.add(dataElement);
                    }
                }
            }
        } catch (HibernateException e) {
            logAndThrowException("Error with getMarkerMetadataSetsByDatasetId(datasetId=" + datasetId + ") query from MarkerMetadataSet " + e.getMessage(), e);
        }
        return toReturn;
	}

	@SuppressWarnings("rawtypes")
	public boolean isExisting(MarkerMetadataSet markerMetadataSet) throws MiddlewareQueryException {
	        try {
	                SQLQuery query = getSession().createSQLQuery(
	                		"SELECT * FROM gdms_marker_metadataset where dataset_id = :datasetId " +
	                		"AND marker_id = :markerId "); 
	                query.setParameter("datasetId", markerMetadataSet.getDatasetId());
	                query.setParameter("markerId", markerMetadataSet.getMarkerId());

	                List results = query.list();
	                
	                if (results.size() > 0) {
	                	return true;
	                }
	        } catch (HibernateException e) {
	            logAndThrowException("Error with isExisting(markerMetadataSet=" + markerMetadataSet + ") query from MarkerMetadataSet " + e.getMessage(), e);
	        }
			return false;	
	}

    @SuppressWarnings("unchecked")
    public List<MarkerMetadataSet> getMarkerMetadataSetByDatasetId(Integer datasetId) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(getPersistentClass());
            criteria.add(Restrictions.eq("datasetId", datasetId));
            return criteria.list();

        } catch (HibernateException e) {
            logAndThrowException("Error in getMarkerMetadataSetByDatasetId=" + datasetId
                    + " query on MarkerMetadataSetDao: " + e.getMessage(), e);
        }

        return new ArrayList<MarkerMetadataSet>();

    }

}
