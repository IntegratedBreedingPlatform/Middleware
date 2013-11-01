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
import java.util.Set;
import java.util.TreeSet;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.SetOperation;
import org.generationcp.middleware.pojos.gdms.AccMetadataSet;
import org.generationcp.middleware.pojos.gdms.AccMetadataSetPK;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;

/**
 * DAO class for {@link AccMetadataSet}.
 *
 * @author Joyce Avestro
 * 
 */
public class AccMetadataSetDAO extends GenericDAO<AccMetadataSet, Integer>{

    @SuppressWarnings("unchecked")
    public List<Integer> getNameIdsByGermplasmIds(List<Integer> gIds) throws MiddlewareQueryException {
        try {
            if (gIds != null && !gIds.isEmpty()){
	            SQLQuery query = getSession().createSQLQuery(AccMetadataSet.GET_NAME_IDS_BY_GERMPLASM_IDS);
	            query.setParameterList("gIdList", gIds);
	            return (List<Integer>) query.list();
            }
        } catch (HibernateException e) {
        	logAndThrowException("Error with getNameIdsByGermplasmIds(" + gIds + ") query from AccMetadataSet: " + e.getMessage(), e);
        }
        return new ArrayList<Integer>();
    }

    @SuppressWarnings("unchecked")
    public List<Integer> getNIDsByDatasetIds(List<Integer> datasetIds, List<Integer> gids, int start, int numOfRows)
            throws MiddlewareQueryException {
        List<Integer> nids = new ArrayList<Integer>();
        try { 
        	if (datasetIds != null && !datasetIds.isEmpty()){
            SQLQuery query;

            if (gids == null || gids.isEmpty()) {
                query = getSession().createSQLQuery(AccMetadataSet.GET_NIDS_BY_DATASET_IDS);
            } else {
                query = getSession().createSQLQuery(
                        AccMetadataSet.GET_NIDS_BY_DATASET_IDS + AccMetadataSet.GET_NIDS_BY_DATASET_IDS_FILTER_BY_GIDS);
                query.setParameterList("gids", gids);
            }

            query.setParameterList("datasetId", datasetIds);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);
            nids = query.list();
        	}
        } catch (HibernateException e) {
            logAndThrowException("Error with getNIDsByDatasetIds(datasetIds=" + datasetIds + ", gids=" + gids + ") query from AccMetadataSet: "
                    + e.getMessage(), e);
        }
        return nids;
    }
    
    @SuppressWarnings("unchecked")
    public Set<Integer> getNIdsByMarkerIdsAndDatasetIdsAndNotGIds(List<Integer> datasetIds, List<Integer> markerIds, List<Integer> gIds, int start, int numOfRows)
            throws MiddlewareQueryException {
        try {
            
        	if (datasetIds != null && !datasetIds.isEmpty()){
        		StringBuilder queryString = new StringBuilder(AccMetadataSet.GET_NIDS_BY_DATASET_IDS_AND_MARKER_IDS_AND_NOT_GIDS_SELECT);
        		queryString.append(AccMetadataSet.GET_NIDS_BY_DATASET_IDS_AND_MARKER_IDS_AND_NOT_GIDS_FROM);
        		
        		if (markerIds != null && !markerIds.isEmpty()){
        			queryString.append(AccMetadataSet.GET_NIDS_BY_DATASET_IDS_FILTER_BY_MARKER_IDS);
	            }
	            
        		if (gIds != null && !gIds.isEmpty()){
        			queryString.append(AccMetadataSet.GET_NIDS_BY_DATASET_IDS_FILTER_NOT_BY_GIDS);
        		}
        		
        		queryString.append(AccMetadataSet.GET_NIDS_BY_DATASET_IDS_ORDER);
        		
	            SQLQuery query;
	            query = getSession().createSQLQuery(queryString.toString());
	            query.setParameterList("represnos", datasetIds);
	            if (markerIds != null && !markerIds.isEmpty()){
	        		query.setParameterList("markerids", markerIds);
	            }
        		if (gIds != null && !gIds.isEmpty()){
        			query.setParameterList("gids", gIds);
        		}
        		query.setFirstResult(start);
        		query.setMaxResults(numOfRows);
        		return (Set<Integer>) new TreeSet<Integer>(query.list());
        	}
            
        } catch (HibernateException e) {
        	logAndThrowException("Error with getNIDsByDatasetIdsAndMarkerIdsAndNotGIDs(datasetIds=" + datasetIds + ", markerIds=" + markerIds + ", gIds=" + gIds + ") query from AccMetadataSet: "
                    + e.getMessage(), e);
        }
        return new TreeSet<Integer>();
    }
    
    public int countNIdsByMarkerIdsAndDatasetIdsAndNotGIds(List<Integer> datasetIds, List<Integer> markerIds, List<Integer> gIds)
            throws MiddlewareQueryException {
        try {
            
        	if (datasetIds != null && !datasetIds.isEmpty()){
        		StringBuilder queryString = new StringBuilder(AccMetadataSet.COUNT_NIDS_BY_DATASET_IDS_AND_MARKER_IDS_AND_NOT_GIDS_SELECT);
        		queryString.append(AccMetadataSet.GET_NIDS_BY_DATASET_IDS_AND_MARKER_IDS_AND_NOT_GIDS_FROM);
        		
        		if (markerIds != null && !markerIds.isEmpty()){
        			queryString.append(AccMetadataSet.GET_NIDS_BY_DATASET_IDS_FILTER_BY_MARKER_IDS);
	            }
	            
        		if (gIds != null && !gIds.isEmpty()){
        			queryString.append(AccMetadataSet.GET_NIDS_BY_DATASET_IDS_FILTER_NOT_BY_GIDS);
        		}
        		
        		queryString.append(AccMetadataSet.GET_NIDS_BY_DATASET_IDS_ORDER);
        		
	            SQLQuery query;
	            query = getSession().createSQLQuery(queryString.toString());
	            query.setParameterList("represnos", datasetIds);
	            if (markerIds != null && !markerIds.isEmpty()){
	        		query.setParameterList("markerids", markerIds);
	            }
        		if (gIds != null && !gIds.isEmpty()){
        			query.setParameterList("gids", gIds);
        		}
        		
        		return ((BigInteger) query.uniqueResult()).intValue();
        	}
            
        } catch (HibernateException e) {
        	logAndThrowException("Error with countNIDsByDatasetIdsAndMarkerIdsAndNotGIDs(datasetIds=" + datasetIds 
        			+ ", markerIds=" + markerIds + ", gIds=" + gIds + ") query from AccMetadataSet: "
                    + e.getMessage(), e);
        }
        return 0;
    }

    @SuppressWarnings("unchecked")
    public Set<Integer> getNIdsByMarkerIdsAndDatasetIds(List<Integer> datasetIds, List<Integer> markerIds)
            throws MiddlewareQueryException {
        try {
            
        	if (datasetIds != null && !datasetIds.isEmpty()){
            
        		StringBuilder queryString = new StringBuilder(AccMetadataSet.GET_NIDS_BY_DATASET_IDS_AND_MARKER_IDS);
        		
        		if (markerIds != null && !markerIds.isEmpty()){
        			queryString.append(AccMetadataSet.GET_NIDS_BY_DATASET_IDS_FILTER_BY_MARKER_IDS);
        		}
        		queryString.append(AccMetadataSet.GET_NIDS_BY_DATASET_IDS_ORDER);
        		
        		SQLQuery query;
        		query = getSession().createSQLQuery(queryString.toString());
        		query.setParameterList("represnos", datasetIds);
        		if (markerIds != null && !markerIds.isEmpty()){
            		query.setParameterList("markerids", markerIds);
        		}
            	return (Set<Integer>) new TreeSet<Integer>(query.list());
        	}
            
        } catch (HibernateException e) {
        	logAndThrowException("Error with getNIdsByMarkerIdsAndDatasetIds(datasetIds=" + datasetIds + ", markerIds=" + markerIds + ") query from AccMetadataSet: "
                    + e.getMessage(), e);
        }
        return new TreeSet<Integer>();
    }

    @SuppressWarnings("rawtypes")
    public List<AccMetadataSetPK> getAccMetadataSetByGids(List<Integer> gids, int start, int numOfRows) throws MiddlewareQueryException {
        List<AccMetadataSetPK> dataValues = new ArrayList<AccMetadataSetPK>();
        try {
            if (gids != null && !gids.isEmpty()) {
                
                SQLQuery query = getSession().createSQLQuery(AccMetadataSet.GET_ACC_METADATASETS_BY_GIDS);
                query.setParameterList("gids", gids);
                query.setFirstResult(start);
                query.setMaxResults(numOfRows);

                List results = query.list();
                for (Object o : results) {
                    Object[] result = (Object[]) o;
                    if (result != null) {
                        Integer gid = (Integer) result[0];
                        Integer nid = (Integer) result[1];
                        Integer datasetId = (Integer) result[2];

                        AccMetadataSetPK dataElement = new AccMetadataSetPK(datasetId, gid, nid);
                        dataValues.add(dataElement);
                    }
                }
            }
        } catch (HibernateException e) {
        	logAndThrowException("Error with getAccMetadataSetByGids(gids=" + gids + ") query from AccMetadataSet: "
                    + e.getMessage(), e);
        }
        return dataValues;
    }

    public long countAccMetadataSetByGids(List<Integer> gids) throws MiddlewareQueryException {
        long count = 0;
        try {
            if ((gids != null) && (!gids.isEmpty())) {
                SQLQuery query = getSession().createSQLQuery(AccMetadataSet.COUNT_ACC_METADATASETS_BY_GIDS);
                query.setParameterList("gids", gids);
                BigInteger result = (BigInteger) query.uniqueResult();
                if (result != null) {
                    count = result.longValue();
                }
            }
        } catch (HibernateException e) {
        	logAndThrowException("Error with countAccMetadataSetByGids(gids=" + gids + ") query from AccMetadataSet: "
                    + e.getMessage(), e);
        }
        return count;
    }
    
    public long countNidsByDatasetIds(List<Integer> datasetIds) throws MiddlewareQueryException {
    	long count = 0;
    	try {
    		if (datasetIds != null && !datasetIds.isEmpty()) {
    			SQLQuery query = getSession().createSQLQuery(AccMetadataSet.COUNT_NIDS_BY_DATASET_IDS);
    			query.setParameterList("datasetIds", datasetIds);
                BigInteger result = (BigInteger) query.uniqueResult();
                if (result != null) {
                    count = result.longValue();
                }
    		}
    		
    	} catch(HibernateException e) {
    		logAndThrowException("Error with countNidsByDatasetIds=" + datasetIds + ") query from AccMetadataSet: "
    				+ e.getMessage(), e);
    	}
    	return count;
    }
    
    @SuppressWarnings("rawtypes")
    public List<AccMetadataSetPK> getAccMetadataSetByGidsAndDatasetId(List<Integer> gids, 
            Integer datasetId, SetOperation operation)throws MiddlewareQueryException{
        
        List<AccMetadataSetPK> dataValues = new ArrayList<AccMetadataSetPK>();
        try {
            if (gids != null && !gids.isEmpty()) {
                
                String queryString = SetOperation.IN.equals(operation)? AccMetadataSet.GET_ACC_METADATASETS_BY_DATASET_ID_AND_IN_GIDS : 
                    AccMetadataSet.GET_ACC_METADATASETS_BY_DATASET_ID_AND_NOT_IN_GIDS;
                SQLQuery query = getSession().createSQLQuery(queryString);
                query.setParameterList("gids", gids);
                query.setParameter("datasetId", datasetId);
                

                List results = query.list();
                for (Object o : results) {
                    Object[] result = (Object[]) o;
                    if (result != null) {
                        Integer gid = (Integer) result[0];
                        Integer nid = (Integer) result[1];

                        AccMetadataSetPK dataElement = new AccMetadataSetPK(datasetId, gid, nid);
                        dataValues.add(dataElement);
                    }
                }
            }
        } catch (HibernateException e) {
                logAndThrowException("Error with getAccMetadataSetByGidsAndDatasetId(gids=" + gids + ", datasetId=" + datasetId + 
                        ") query from AccMetadataSet: " + e.getMessage(), e);
        }
        return dataValues;
     }

     public void deleteByDatasetId(Integer datasetId) throws MiddlewareQueryException {
		try {
			this.flush();
			
			SQLQuery statement = getSession().createSQLQuery("DELETE FROM gdms_acc_metadataset WHERE dataset_id = " + datasetId);
			statement.executeUpdate();

			this.flush();
            this.clear();

		} catch(HibernateException e) {
			logAndThrowException("Error in deleteByDatasetId=" + datasetId + " in AccMetadataSetDAO: " + e.getMessage(), e);
		}
    }
}
