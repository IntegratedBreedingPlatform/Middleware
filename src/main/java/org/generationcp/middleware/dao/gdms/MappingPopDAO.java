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
import org.generationcp.middleware.pojos.gdms.MappingPop;
import org.generationcp.middleware.pojos.gdms.MappingValueElement;
import org.generationcp.middleware.pojos.gdms.ParentElement;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link MappingPop}.
 *
 * @author Joyce Avestro
 * 
 */
public class MappingPopDAO extends GenericDAO<MappingPop, Integer>{

    @SuppressWarnings("rawtypes")
    public List<ParentElement> getParentsByDatasetId(Integer datasetId) throws MiddlewareQueryException {
        SQLQuery query = getSession().createSQLQuery(MappingPop.GET_PARENTS_BY_DATASET_ID);
        query.setParameter("datasetId", datasetId);

        List<ParentElement> dataValues = new ArrayList<ParentElement>();
        try {
            List results = query.list();

            for (Object o : results) {
                Object[] result = (Object[]) o;
                if (result != null) {
                    Integer parentAGId = (Integer) result[0];
                    Integer parentBGId = (Integer) result[1];
                    String mappingPopType = (String) result[2];
                    ParentElement parentElement = new ParentElement(parentAGId, parentBGId, mappingPopType);
                    dataValues.add(parentElement);
                }
            }
            return dataValues;
        } catch (HibernateException e) {
        	logAndThrowException("Error with getParentsByDatasetId(datasetId=" + datasetId + ") query from MappingPop: " + e.getMessage(), e);
        }
        return dataValues;
    }

    @SuppressWarnings("rawtypes")
    public List<MappingValueElement> getMappingValuesByGidAndMarkerIds(List<Integer> gids, List<Integer> markerIds)
            throws MiddlewareQueryException {
        List<MappingValueElement> mappingValues = new ArrayList<MappingValueElement>();

        if (gids == null || gids.isEmpty() || markerIds == null || markerIds.isEmpty()) {
            return mappingValues;
        }

        SQLQuery query = getSession().createSQLQuery(MappingPop.GET_MAPPING_VALUES_BY_GIDS_AND_MARKER_IDS);
        query.setParameterList("markerIdList", markerIds);
        query.setParameterList("gidList", gids);

        try {
            List results = query.list();

            for (Object o : results) {
                Object[] result = (Object[]) o;
                if (result != null) {
                    Integer datasetId = (Integer) result[0];
                    String mappingPopType = (String) result[1];
                    Integer parentAGid = (Integer) result[2];
                    Integer parentBGid = (Integer) result[3];
                    String markerType = (String) result[4];
                    MappingValueElement mappingValueElement = new MappingValueElement(datasetId, mappingPopType, parentAGid, parentBGid,
                            markerType);
                    mappingValues.add(mappingValueElement);
                }
            }
            return mappingValues;
        } catch (HibernateException e) {
        	logAndThrowException("Error with getMappingValuesByGidAndMarkerIds(gids=" + gids + ", markerIds=" + markerIds
                    + ") query from MappingPop: " + e.getMessage(), e);
        }
        return mappingValues;
    }

    @SuppressWarnings("rawtypes")
    public List<ParentElement> getAllParentsFromMappingPopulation(int start, int numOfRows)
            throws MiddlewareQueryException {

        SQLQuery query = getSession().createSQLQuery(MappingPop.GET_ALL_PARENTS_FROM_MAPPING_POPULATION);
        query.setFirstResult(start);
        query.setMaxResults(numOfRows);
        
        List<ParentElement> dataValues = new ArrayList<ParentElement>();
        try {
            List results = query.list();

            for (Object o : results) {
                Object[] result = (Object[]) o;
                if (result != null) {
                    int parentAGId = (Integer) result[0];
                    int parentBGId = (Integer) result[1];
                    String mappingPopType = null;
                    ParentElement parentElement = new ParentElement(parentAGId, parentBGId, mappingPopType);
                    dataValues.add(parentElement);
                }
            }
            return dataValues;        
        } catch (HibernateException e) {
        	logAndThrowException("Error with getAllParentsFromMappingPopulation() query from MappingPop: " + e.getMessage(), e);
        }
        return dataValues;
    }

    
    public Long countAllParentsFromMappingPopulation()
            throws MiddlewareQueryException {

        SQLQuery query = getSession().createSQLQuery(MappingPop.COUNT_ALL_PARENTS_FROM_MAPPING_POPULATION);

        try {
            BigInteger result = (BigInteger) query.uniqueResult();
            return result.longValue();
        } catch (HibernateException e) {
        	logAndThrowException("Error with countAllParentsFromMappingPopulation() query from MappingPop: " + e.getMessage(), e);
        }
        return 0L;
    }

    public void deleteByDatasetId(int datasetId) throws MiddlewareQueryException {
        try {
            this.flush();
            
            SQLQuery statement = getSession().createSQLQuery("DELETE FROM gdms_mapping_pop WHERE dataset_id = " + datasetId);
            statement.executeUpdate();

            this.flush();
            this.clear();

        } catch(HibernateException e) {
            logAndThrowException("Error in deleteByDatasetId=" + datasetId + " in MappingPopDAO: " + e.getMessage(), e);
        }
    }
    
    
    public MappingPop getMappingPopByDatasetId(Integer datasetId) throws MiddlewareQueryException {
        try {
            if (datasetId != null){
    			Criteria criteria = getSession().createCriteria(getPersistentClass());
    			criteria.add(Restrictions.eq("datasetId", datasetId));
    			return (MappingPop) criteria.uniqueResult();
            }
        } catch (HibernateException e) {
            logAndThrowException("Error with getMappingPopByDatasetId(datasetId=" + datasetId + ") query from MappingPop " + e.getMessage(), e);
        }
        return null;

    }
    
        
}
