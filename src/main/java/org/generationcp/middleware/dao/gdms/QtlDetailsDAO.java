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

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.gdms.QtlDataElement;
import org.generationcp.middleware.pojos.gdms.QtlDetails;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;


/**
 * DAO class for {@link QtlDetails}.
 *
 * @author Joyce Avestro
 * 
 */
@SuppressWarnings("unchecked")
public class QtlDetailsDAO  extends GenericDAO<QtlDetails, Integer>{

    public List<Integer> getMarkerIdsByQtl(String qtlName, String chromosome, int min, int max
            , int start, int numOfRows) throws MiddlewareQueryException{
        try {
            SQLQuery query = getSession().createSQLQuery(QtlDetails.GET_MARKER_IDS_BY_QTL);
            query.setParameter("qtlName", qtlName);
            query.setParameter("chromosome", chromosome);
            query.setParameter("min", min);
            query.setParameter("max", max);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);
            return query.list();
            
        } catch (HibernateException e) {
        	logAndThrowException("Error with getMarkerIDsByQtl() query from QtlDetails: " + e.getMessage(), e);    
        }
        return new ArrayList<Integer>();
    }
    
    public long countMarkerIdsByQtl(String qtlName, String chromosome, int min, int max) throws MiddlewareQueryException{
        try {
            SQLQuery query = getSession().createSQLQuery(QtlDetails.COUNT_MARKER_IDS_BY_QTL);
            query.setParameter("qtlName", qtlName);
            query.setParameter("chromosome", chromosome);
            query.setParameter("min", min);
            query.setParameter("max", max);
            BigInteger result = (BigInteger) query.uniqueResult();
            if (result != null) {
                return result.longValue();
            }
            
        } catch (HibernateException e) {
        	logAndThrowException("Error with countMarkerIdsByQtl() query from QtlDetails: " + e.getMessage(), e);    
        }
        return 0;
        
    }

	public Integer getMapIdByQtlName(String qtlName)
			throws MiddlewareQueryException {
		try {
			SQLQuery query = getSession().createSQLQuery(
					QtlDetails.GET_MAP_ID_BY_QTL);
			query.setParameter("qtlName", qtlName);
			return (Integer) query.uniqueResult();

		} catch (HibernateException e) {
			logAndThrowException(
					"Error with getMapIdByQtlName() query from QtlDetails: "
							+ e.getMessage(), e);
		}
		return 0;
	}


    public List<Integer> getMapIdsByQtlName(String qtlName, int start, int numOfRows) throws MiddlewareQueryException{
        try {
            SQLQuery query = getSession().createSQLQuery(QtlDetails.GET_MAP_IDS_BY_QTL);
            query.setParameter("qtlName", qtlName);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);
            return query.list();
            
        } catch (HibernateException e) {
        	logAndThrowException("Error with getMapIdsByQtlName() query from QtlDetails: " + e.getMessage(), e);    
        }
        return new ArrayList<Integer>();
    }

    public long countMapIdsByQtlName(String qtlName) throws MiddlewareQueryException{
        try {
            SQLQuery query = getSession().createSQLQuery(QtlDetails.COUNT_MAP_IDS_BY_QTL);
            query.setParameter("qtlName", qtlName);
            BigInteger result = (BigInteger) query.uniqueResult();
            if (result != null) {
                return result.longValue();
            }
        } catch (HibernateException e) {
        	logAndThrowException("Error with countMapIdsByQtlName() query from QtlDetails: " + e.getMessage(), e);    
        }
        return 0;
    }

    public List<Integer> getMarkerIdsByQtl(Integer mapId, String qtlName, String chromosome, int min,
            int max, int start, int numOfRows) throws MiddlewareQueryException{
        try {
            SQLQuery query = getSession().createSQLQuery(QtlDetails.GET_MARKER_IDS_BY_QTL_AND_MAP_ID);
            query.setParameter("qtlName", qtlName);
            query.setParameter("chromosome", chromosome);
            query.setParameter("min", min);
            query.setParameter("max", max);
            query.setParameter("mapId", mapId);
            return query.list();
        } catch (HibernateException e) {
        	logAndThrowException("Error with getMarkerIDsByQtl() query from QtlDetails: " + e.getMessage(), e);    
        }
        return new ArrayList<Integer>();
    }

    public long countMarkerIdsByQtl(Integer mapId, String qtlName, String chromosome, int min, int max)  
    		throws MiddlewareQueryException{
        try {
            SQLQuery query = getSession().createSQLQuery(QtlDetails.COUNT_MARKER_IDS_BY_QTL_AND_MAP_ID);
            query.setParameter("qtlName", qtlName);
            query.setParameter("chromosome", chromosome);
            query.setParameter("min", min);
            query.setParameter("max", max);
            query.setParameter("mapId", mapId);
            BigInteger result = (BigInteger) query.uniqueResult();
            if (result != null) {
                return result.longValue();
            }
            
        } catch (HibernateException e) {
        	logAndThrowException("Error with countMarkerIdsByQtl() query from QtlDetails: " + e.getMessage(), e);    
        }
        return 0;
    }
    
    
    public List<Integer> getQtlTraitsByDatasetId(Integer datasetId, int start, int numOfRows) throws MiddlewareQueryException{
        try {
        	if (datasetId != null){
        		SQLQuery query = getSession().createSQLQuery(QtlDetails.GET_QTL_TRAITS_BY_DATASET_ID);
	            query.setParameter("datasetId", datasetId);
	            query.setFirstResult(start);
	            query.setMaxResults(numOfRows);
	            return (List<Integer>) query.list();
        	}
        } catch (HibernateException e) {
        	logAndThrowException("Error with getQtlTraitsByDatasetId() query from QtlDetails: " + e.getMessage(), e);    
        }
        return new ArrayList<Integer>();
    }
    
    public long countQtlTraitsByDatasetId(Integer datasetId)  throws MiddlewareQueryException{
        try {
        	if (datasetId != null){
	            SQLQuery query = getSession().createSQLQuery(QtlDetails.COUNT_QTL_TRAITS_BY_DATASET_ID);
	            query.setParameter("datasetId", datasetId);
	            BigInteger result = (BigInteger) query.uniqueResult();
	            if (result != null) {
	                return result.longValue();
	            }
        	}            
        } catch (HibernateException e) {
        	logAndThrowException("Error with countQtlTraitsByDatasetId() query from QtlDetails: " + e.getMessage(), e);    
        }
        return 0;
    }
    
    public List<QtlDataElement> getQtlDataByQtlTraits(List<Integer> qtlTraits, int start, int numOfRows) throws MiddlewareQueryException{
        List<QtlDataElement> toReturn = new ArrayList<QtlDataElement>();

        try {
        	if (qtlTraits != null && !qtlTraits.isEmpty()){
	            SQLQuery query = getSession().createSQLQuery(QtlDetails.GET_QTL_DATA_BY_QTL_TRAITS);
	            query.setParameterList("qtlTraits", qtlTraits);
	            query.setFirstResult(start);
	            query.setMaxResults(numOfRows);
	            
	            @SuppressWarnings("rawtypes")
				List results = query.list();
	
	            for (Object o : results) {
	                Object[] result = (Object[]) o;
	                if (result != null) {
	                	//Get the fields for QtlDataElement         
	                	String qtlName = (String) result[0];
	                	String linkageGroup = (String) result[1];
	                	Float position = (Float) result[2]; 
	                	Float minPosition = (Float) result[3];
	                	Float maxPosition = (Float) result[4]; 
	                	Integer traitId = (Integer) result[5]; 
	                	String experiment = (String) result[6]; 
	                	String leftFlankingMarker = (String) result[7]; 
	                	String rightFlankingMarker = (String) result[8]; 
	                	Integer effect = (Integer) result[9]; 
	                	Float scoreValue = (Float) result[10]; 
	                	Float rSquare = (Float) result[11];
	
	                    QtlDataElement qtlData = new QtlDataElement(
	                    		qtlName, linkageGroup, position, minPosition, maxPosition, 
	                    		traitId, experiment, leftFlankingMarker, rightFlankingMarker, 
	                    		effect, scoreValue, rSquare);
	                    toReturn.add(qtlData);
	                }
	            }
        	}
        } catch (HibernateException e) {
        	logAndThrowException("Error with getQtlDataByQtlTraits() query from QtlDetails: " + e.getMessage(), e);    
        }
        return toReturn;
    }
    
    public long countQtlDataByQtlTraits(List<String> qtlTraits)  throws MiddlewareQueryException{
        try {
        	if (qtlTraits != null && !qtlTraits.isEmpty()){        	
	            SQLQuery query = getSession().createSQLQuery(QtlDetails.COUNT_QTL_DATA_BY_QTL_TRAITS);
	            query.setParameterList("qtlTraits", qtlTraits);
	            BigInteger result = (BigInteger) query.uniqueResult();
	            if (result != null) {
	                return result.longValue();
	            }
        	}            
        } catch (HibernateException e) {
        	logAndThrowException("Error with countQtlDataByQtlTraits() query from QtlDetails: " + e.getMessage(), e);    
        }
        return 0;
    }
    
	public void deleteByQtlIds(List<Integer> qtlIds) throws MiddlewareQueryException {
		try {
			this.flush();
			
			SQLQuery statement = getSession().createSQLQuery("DELETE FROM gdms_qtl_details WHERE qtl_id IN (" + StringUtils.join(qtlIds, ",") + ")");
			statement.executeUpdate();

			this.flush();
            this.clear();

		} catch(HibernateException e) {
			logAndThrowException("Error in deleteByQtlIds=" + qtlIds + " in QtlDetailsDAO: " + e.getMessage(), e);
		}
    }
	
    public List<QtlDetails> getQtlDetailsByMapId(Integer mapId) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(getPersistentClass());
            criteria.add(Restrictions.eq("id.mapId", mapId));
            
            return criteria.list();

        } catch(HibernateException e) {
            logAndThrowException("Error in getQtlDetailsByMapId=" + mapId + " in QtlDetailsDAO: " + e.getMessage(), e);
        }
        return new ArrayList<QtlDetails>();
    }

    public List<QtlDetails> getQtlDetailsByQtlIds(List<Integer> qtlIds) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(getPersistentClass());
            criteria.add(Restrictions.in("id.qtlId", qtlIds));
            
            return criteria.list();

        } catch(HibernateException e) {
            logAndThrowException("Error in getQtlDetailsByQtlId=" + qtlIds + " in QtlDetailsDAO: " + e.getMessage(), e);
        }
        return new ArrayList<QtlDetails>();
    }

	public long countQtlDetailsByMapId(Integer mapId) throws MiddlewareQueryException {
		try {
			Criteria criteria = getSession().createCriteria(getPersistentClass());
			criteria.add(Restrictions.eq("id.mapId", mapId));
			criteria.setProjection(Projections.rowCount());
			
			return (Long) criteria.uniqueResult();

		} catch(HibernateException e) {
			logAndThrowException("Error in countQtlDetailsByMapId=" + mapId + " in QtlDetailsDAO: " + e.getMessage(), e);
		}
		return 0;
	}
}
