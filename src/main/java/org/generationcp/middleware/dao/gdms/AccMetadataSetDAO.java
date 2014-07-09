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
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link AccMetadataSet}.
 *
 * @author Joyce Avestro
 * 
 */
public class AccMetadataSetDAO extends GenericDAO<AccMetadataSet, Integer>{
	
    public static final String GET_NAME_IDS_BY_GERMPLASM_IDS = 
            "SELECT nid " +
            "FROM gdms_acc_metadataset " +
            "WHERE gid IN (:gIdList)";
    
    public static final String GET_ACC_METADATASETS_BY_GIDS = 
            "SELECT gid, nid, dataset_id " +
            "FROM gdms_acc_metadataset " +
            "WHERE gid IN (:gids) ";
    
    public static final String GET_ACC_METADATASETS_BY_DATASET_ID_AND_IN_GIDS = 
        "SELECT acc_metadataset_id, gid, nid " +
        "FROM gdms_acc_metadataset " +
        "WHERE gid IN (:gids) " +
        "AND dataset_id = :datasetId";
    
    public static final String GET_ACC_METADATASETS_BY_DATASET_ID_AND_NOT_IN_GIDS = 
        "SELECT acc_metadataset_id, gid, nid " +
        "FROM gdms_acc_metadataset " +
        "WHERE gid NOT IN (:gids) " +
        "AND dataset_id = :datasetId";
    
    public static final String COUNT_ACC_METADATASETS_BY_GIDS = 
            "SELECT COUNT(*) " +
            "FROM gdms_acc_metadataset " +
            "WHERE gid IN (:gids) ";
    
    public static final String GET_NIDS_BY_DATASET_IDS_AND_MARKER_IDS_AND_NOT_GIDS_SELECT = 
            "SELECT DISTINCT nid " ;
    
    public static final String COUNT_NIDS_BY_DATASET_IDS_AND_MARKER_IDS_AND_NOT_GIDS_SELECT = 
            "SELECT COUNT(DISTINCT nid) " ;
    
    public static final String GET_NIDS_BY_DATASET_IDS_AND_MARKER_IDS_AND_NOT_GIDS_FROM = 
        "FROM gdms_acc_metadataset gam "+
        "INNER JOIN gdms_marker_metadataset gmm on gmm.dataset_id = gam.dataset_id " + 
        "WHERE gam.dataset_id IN (:represnos) " ;
    
    public static final String GET_NIDS_BY_DATASET_IDS_FILTER_BY_MARKER_IDS = 
            "AND gmm.marker_id IN (:markerids) " ;
    
    public static final String GET_NIDS_BY_DATASET_IDS_FILTER_NOT_BY_GIDS = 
            "AND gam.gid NOT IN (:gids) " ; 
  
    public static final String GET_NIDS_BY_DATASET_IDS_ORDER = 
    		"ORDER BY nid DESC";

    public static final String GET_NIDS_BY_DATASET_IDS_AND_MARKER_IDS = 
            "SELECT DISTINCT nid from gdms_acc_metadataset gam "+
            "INNER JOIN gdms_marker_metadataset gmm on gmm.dataset_id = gam.dataset_id " + 
            "WHERE gam.dataset_id IN (:represnos) " ;
    
    public static final String COUNT_NIDS_BY_DATASET_IDS = 
    		"SELECT COUNT(DISTINCT nid) FROM gdms_acc_metadataset WHERE dataset_id IN (:datasetIds)";


    @SuppressWarnings("unchecked")
    public List<Integer> getNameIdsByGermplasmIds(List<Integer> gIds) throws MiddlewareQueryException {
        try {
            if (gIds != null && !gIds.isEmpty()){
	            SQLQuery query = getSession().createSQLQuery(GET_NAME_IDS_BY_GERMPLASM_IDS);
	            query.setParameterList("gIdList", gIds);
	            return (List<Integer>) query.list();
            }
        } catch (HibernateException e) {
        	logAndThrowException("Error with getNameIdsByGermplasmIds(" + gIds + ") query from AccMetadataSet: " + e.getMessage(), e);
        }
        return new ArrayList<Integer>();
    }

    @SuppressWarnings("unchecked")
    public List<AccMetadataSet> getByDatasetIdsAndNotInGids(List<Integer> datasetIds, List<Integer> gids, int start, int numOfRows)
            throws MiddlewareQueryException {
        List<AccMetadataSet> returnValues = new ArrayList<AccMetadataSet>();
        try { 
        	if (datasetIds != null && !datasetIds.isEmpty()){
        		Criteria criteria = getSession().createCriteria(getPersistentClass());
        		criteria.add(Restrictions.in("datasetId", datasetIds));
        		
        		if (gids != null && !gids.isEmpty()){
        			criteria.add(Restrictions.not(Restrictions.in("germplasmId", gids)));        			
        		}

    			returnValues = criteria.list();
        	}
        } catch (HibernateException e) {
            logAndThrowException("Error with getByDatasetIdsAndNotInGids(datasetIds=" + datasetIds + ", gids=" + gids + ") query from AccMetadataSet: "
                    + e.getMessage(), e);
        }
        return returnValues;
    }
    
    @SuppressWarnings("unchecked")
    public Set<Integer> getNIdsByMarkerIdsAndDatasetIdsAndNotGIds(List<Integer> datasetIds, List<Integer> markerIds, List<Integer> gIds, int start, int numOfRows)
            throws MiddlewareQueryException {
        try {
            
        	if (datasetIds != null && !datasetIds.isEmpty()){
        		StringBuilder queryString = new StringBuilder(GET_NIDS_BY_DATASET_IDS_AND_MARKER_IDS_AND_NOT_GIDS_SELECT);
        		queryString.append(GET_NIDS_BY_DATASET_IDS_AND_MARKER_IDS_AND_NOT_GIDS_FROM);
        		
        		if (markerIds != null && !markerIds.isEmpty()){
        			queryString.append(GET_NIDS_BY_DATASET_IDS_FILTER_BY_MARKER_IDS);
	            }
	            
        		if (gIds != null && !gIds.isEmpty()){
        			queryString.append(GET_NIDS_BY_DATASET_IDS_FILTER_NOT_BY_GIDS);
        		}
        		
        		queryString.append(GET_NIDS_BY_DATASET_IDS_ORDER);
        		
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
    
    public long countNIdsByMarkerIdsAndDatasetIdsAndNotGIds(List<Integer> datasetIds, List<Integer> markerIds, List<Integer> gIds)
            throws MiddlewareQueryException {
        try {
            
        	if (datasetIds != null && !datasetIds.isEmpty()){
        		StringBuilder queryString = new StringBuilder(COUNT_NIDS_BY_DATASET_IDS_AND_MARKER_IDS_AND_NOT_GIDS_SELECT);
        		queryString.append(GET_NIDS_BY_DATASET_IDS_AND_MARKER_IDS_AND_NOT_GIDS_FROM);
        		
        		if (markerIds != null && !markerIds.isEmpty()){
        			queryString.append(GET_NIDS_BY_DATASET_IDS_FILTER_BY_MARKER_IDS);
	            }
	            
        		if (gIds != null && !gIds.isEmpty()){
        			queryString.append(GET_NIDS_BY_DATASET_IDS_FILTER_NOT_BY_GIDS);
        		}
        		
        		queryString.append(GET_NIDS_BY_DATASET_IDS_ORDER);
        		
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
            
        		StringBuilder queryString = new StringBuilder(GET_NIDS_BY_DATASET_IDS_AND_MARKER_IDS);
        		
        		if (markerIds != null && !markerIds.isEmpty()){
        			queryString.append(GET_NIDS_BY_DATASET_IDS_FILTER_BY_MARKER_IDS);
        		}
        		queryString.append(GET_NIDS_BY_DATASET_IDS_ORDER);
        		
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
    
    @SuppressWarnings("unchecked")
    public List<AccMetadataSet> getAccMetadataSetsByGids(List<Integer> gids, int start, int numOfRows) throws MiddlewareQueryException {
        List<AccMetadataSet> dataValues = new ArrayList<AccMetadataSet>();
        try {
        	
			Criteria criteria = getSession().createCriteria(getPersistentClass());
			criteria.add(Restrictions.in("germplasmId", gids));
			dataValues = criteria.list();
        } catch (HibernateException e) {
        	logAndThrowException("Error with getAccMetadataSetByGids(gids=" + gids + ") query from AccMetadataSet: "
                    + e.getMessage(), e);
        }
        return dataValues;
    }

    public long countAccMetadataSetsByGids(List<Integer> gids) throws MiddlewareQueryException {
        long count = 0;
        try {
            if ((gids != null) && (!gids.isEmpty())) {
                SQLQuery query = getSession().createSQLQuery(COUNT_ACC_METADATASETS_BY_GIDS);
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
    			SQLQuery query = getSession().createSQLQuery(COUNT_NIDS_BY_DATASET_IDS);
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
    public List<AccMetadataSet> getAccMetadataSetByGidsAndDatasetId(List<Integer> gids, 
            Integer datasetId, SetOperation operation)throws MiddlewareQueryException{
        
        List<AccMetadataSet> dataValues = new ArrayList<AccMetadataSet>();
        try {
            if (gids != null && !gids.isEmpty()) {
                
                String queryString = SetOperation.IN.equals(operation)? GET_ACC_METADATASETS_BY_DATASET_ID_AND_IN_GIDS : 
                    GET_ACC_METADATASETS_BY_DATASET_ID_AND_NOT_IN_GIDS;
                SQLQuery query = getSession().createSQLQuery(queryString);
                query.setParameterList("gids", gids);
                query.setParameter("datasetId", datasetId);
                

                List results = query.list();
                for (Object o : results) {
                    Object[] result = (Object[]) o;
                    if (result != null) {
                        Integer accMetadataSetId = (Integer) result[0];
                        Integer gid = (Integer) result[1];
                        Integer nid = (Integer) result[2];

                        AccMetadataSet dataElement = new AccMetadataSet(accMetadataSetId, datasetId, gid, nid, null);
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
     

 	@SuppressWarnings("unchecked")
	public List<AccMetadataSet> getAccMetadataSetsByDatasetId(Integer datasetId) throws MiddlewareQueryException{
        List<AccMetadataSet> dataValues = new ArrayList<AccMetadataSet>();
        try {
        	
			Criteria criteria = getSession().createCriteria(getPersistentClass());
			criteria.add(Restrictions.eq("datasetId", datasetId));
			dataValues = criteria.list();
        } catch (HibernateException e) {
        	logAndThrowException("Error with getAccMetadataSetsByDatasetId(datasetId=" + datasetId 
        			+ ") query from AccMetadataSet " + e.getMessage(), e);
        }
        return dataValues;
//
// 	    List<AccMetadataSet> toReturn = new ArrayList<AccMetadataSet>();
//         try {
//             if (datasetId != null){
//                 SQLQuery query = getSession().createSQLQuery("SELECT * FROM gdms_acc_metadataset where dataset_id = :datasetId "); 
//                 query.setParameter("datasetId", datasetId);
//
//                 List results = query.list();
//                 for (Object o : results) {
//                     Object[] result = (Object[]) o;
//                     if (result != null) {
//                         Integer datasetId2 =  (Integer) result[0];
//                         Integer gid = (Integer) result[1];
//                         Integer nid = (Integer) result[2];
//
//                         AccMetadataSet dataElement = new AccMetadataSet(datasetId2, gid, nid);
//                         toReturn.add(dataElement);
//                     }
//                 }
//             }
//         } catch (HibernateException e) {
//             logAndThrowException("Error with getAccMetadataSetsByDatasetId(datasetId=" + datasetId + ") query from AccMetadataSet " + e.getMessage(), e);
//         }
//         return toReturn;
 	}

	@SuppressWarnings("rawtypes")
	public boolean isExisting(AccMetadataSet accMetadataSet) throws MiddlewareQueryException {
        try {
                SQLQuery query = getSession().createSQLQuery(
                		"SELECT * FROM gdms_acc_metadataset where dataset_id = :datasetId " +
                		"AND gid = :gid AND nid = :nid "); 
                query.setParameter("datasetId", accMetadataSet.getDatasetId());
                query.setParameter("gid", accMetadataSet.getGermplasmId());
                query.setParameter("nid", accMetadataSet.getNameId());

                List results = query.list();
                
                if (results.size() > 0) {
                	return true;
                }
        } catch (HibernateException e) {
            logAndThrowException("Error with isExisting(accMetadataSet=" + accMetadataSet + ") query from AccMetadataSet " + e.getMessage(), e);
        }
		return false;
	}
	
}
