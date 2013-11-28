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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.gdms.Qtl;
import org.generationcp.middleware.pojos.gdms.QtlDetailElement;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;

/**
 * DAO class for {@link Qtl}.
 *
 */ 
public class QtlDAO  extends GenericDAO<Qtl, Integer>{    
    
    public long countQtlIdByName(String name) throws MiddlewareQueryException {
        try {
            Query query = getSession().createSQLQuery(Qtl.COUNT_QTL_ID_BY_NAME);
            query.setParameter("qtlName", name);
            BigInteger result = (BigInteger) query.uniqueResult();
            if (result != null) {
                return result.longValue();
            } else {
                return 0;
            }
        } catch (HibernateException e) {
        	logAndThrowException("Error with countQtlIdByName(name=" + name + ") query from gdms_qtl: "
                    + e.getMessage(), e);
        }
        return 0;
    }
    
    @SuppressWarnings("unchecked")
    public List<Integer> getQtlIdByName(String name, int start, int numOfRows) throws MiddlewareQueryException {
        try {
            SQLQuery query = getSession().createSQLQuery(Qtl.GET_QTL_ID_BY_NAME);
            query.setParameter("qtlName", name);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);
            return (List<Integer>) query.list();
        } catch (HibernateException e) {
        	logAndThrowException("Error with getQtlIdByName(name=" + name + ") query from gdms_qtl: "
                    + e.getMessage(), e);
        }
        return new ArrayList<Integer>();
    }

    @SuppressWarnings("rawtypes")
    public List<QtlDetailElement> getQtlDetailsByQTLIDs(List<Integer> qtlIDs, int start, int numOfRows) 
    		throws MiddlewareQueryException{
        List<QtlDetailElement> toReturn = new ArrayList<QtlDetailElement>();

        try {
        	if (qtlIDs != null && !qtlIDs.isEmpty()){
	            SQLQuery query = getSession().createSQLQuery(Qtl.GET_QTL_BY_QTL_IDS);
	            query.setParameterList("qtl_id_list", qtlIDs);
	            query.setFirstResult(start);
	            query.setMaxResults(numOfRows);
	            List results = query.list();
	
	            for (Object o : results) {
	                Object[] result = (Object[]) o;
	                if (result != null) {
	                    String qtlName = (String) result[0];
	                    String mapName = (String) result[1];
	                    String chromosome = (String) result[2];
	                    Float minPosition = (Float) result[3];
	                    Float maxPosition = (Float) result[4];
	                    Integer traitId = (Integer) result[5];
	                    String experiment = (String) result[6];
	                    String leftFlankingMarker = (String) result[7];
	                    String rightFlankingMarker = (String) result[8];
	                    Integer effect = (Integer) result[9];
	                    Float scoreValue = (Float) result[10];
	                    Float rSquare = (Float) result[11];
	                    String interactions = (String) result[12];
	                    String tRName = (String) result[13];
	                    String ontology = (String) result[14];
	                                           
	                    QtlDetailElement element = new QtlDetailElement(
	                    		qtlName, mapName, chromosome, minPosition, maxPosition, traitId,
	                            experiment, leftFlankingMarker, rightFlankingMarker, effect, 
	                            scoreValue, rSquare, interactions, tRName, ontology);
	                    toReturn.add(element);
	                }
	            }
	
	            return toReturn;
        	}
        } catch (HibernateException e) {
        	logAndThrowException("Error with getQtlDetailsByQTLIDs(qtl ids=" + qtlIDs 
        			+ ") query from gdms_qtl_details: " + e.getMessage(), e);
        }
        return new ArrayList<QtlDetailElement>();
    }

    public long countQtlDetailsByQTLIDs(List<Integer> qtlIDs) throws MiddlewareQueryException {
        try {
            Query query = getSession().createSQLQuery(Qtl.COUNT_QTL_BY_QTL_IDS);
            query.setParameterList("qtl_id_list", qtlIDs);
            BigInteger result = (BigInteger) query.uniqueResult();
            if (result != null) {
                return result.longValue();
            } else {
                return 0;
            }
        } catch (HibernateException e) {
        	logAndThrowException("Error with countQtlDetailsByQTLIDs(qtl ids=" + qtlIDs + ") query from gdms_qtl_details: "
                    + e.getMessage(), e);
        }
        return 0;
    }
    
    @SuppressWarnings("rawtypes")
    public List<QtlDetailElement> getQtlDetailsByName(String name, int start, int numOfRows) throws MiddlewareQueryException{
        List<QtlDetailElement> toReturn = new ArrayList<QtlDetailElement>();

        try {
            SQLQuery query = getSession().createSQLQuery(Qtl.GET_QTL_BY_NAME);
            query.setParameter("qtlName", name);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);
            List results = query.list();

            for (Object o : results) {
                Object[] result = (Object[]) o;
                if (result != null) {
                    String qtlName = (String) result[0];
                    String mapName = (String) result[1];
                    String chromosome = (String) result[2];
                    Float minPosition = (Float) result[3];
                    Float maxPosition = (Float) result[4];
                    Integer traitId = (Integer) result[5];
                    String experiment = (String) result[6];
                    String leftFlankingMarker = (String) result[7];
                    String rightFlankingMarker = (String) result[8];
                    Integer effect = (Integer) result[9];
                    Float scoreValue = (Float) result[10];
                    Float rSquare = (Float) result[11];
                    String interactions = (String) result[12];
                    String tRName = (String) result[13];
                    String ontology = (String) result[14];
                    
                    QtlDetailElement element = new QtlDetailElement(
                    		qtlName, mapName, chromosome, minPosition, maxPosition, traitId,
                            experiment, leftFlankingMarker, rightFlankingMarker, effect, 
                            scoreValue, rSquare, interactions, tRName, ontology);
                    toReturn.add(element);
                }
            }

            return toReturn;
        } catch (HibernateException e) {
        	logAndThrowException("Error with getQtlDetailsByName(name=" + name 
        			+ ") query from gdms_qtl_details: " + e.getMessage(), e);
        }
        return toReturn;
    }

    public long countQtlDetailsByName(String name) throws MiddlewareQueryException {
        try {
            Query query = getSession().createSQLQuery(Qtl.COUNT_QTL_BY_NAME);
            query.setParameter("qtlName", name);
            BigInteger result = (BigInteger) query.uniqueResult();
            if (result != null) {
                return result.longValue();
            } else {
                return 0;
            }
        } catch (HibernateException e) {
        	logAndThrowException("Error with countQtlDetailsByName(name=" + name 
        			+ ") query from gdms_qtl_details: " + e.getMessage(), e);
        }
        return 0L;
    }
    
    @SuppressWarnings("unchecked")
	public Set<Integer> getMapIDsByQTLName(String qtlName, int start, int numOfRows) 
			throws MiddlewareQueryException{
        try {
            
            SQLQuery query;

            query = getSession().createSQLQuery(Qtl.GET_MAP_IDS_BY_QTL_NAME);
            query.setParameter("qtl_name", qtlName);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);
            Set<Integer> mapIDSet = new TreeSet<Integer>(query.list());

            return mapIDSet;
            
        } catch (HibernateException e) {
        	logAndThrowException("Error with getMapIDsByQTLName(qtlName=" + qtlName 
        			+ ", start=" + start + ", numOfRows=" + numOfRows + ") query from QTL: "
                    + e.getMessage(), e);
        }
        return new TreeSet<Integer>();
    }
    
    public long countMapIDsByQTLName(String qtlName) throws MiddlewareQueryException{
        try {
            
            SQLQuery query;

            query = getSession().createSQLQuery(Qtl.COUNT_MAP_IDS_BY_QTL_NAME);
            query.setParameter("qtl_name", qtlName);
            BigInteger result = (BigInteger) query.uniqueResult();
            if (result != null) {
                return result.longValue();
            } else {
                return 0;
            }
            
        } catch (HibernateException e) {
        	logAndThrowException("Error with countMapIDsByQTLName(qtlName=" + qtlName + ") query from QTL: "
                    + e.getMessage(), e);
        }
        return 0L;
    }

    @SuppressWarnings("unchecked")
    public List<Integer> getQtlByTrait(Integer traitId, int start, int numOfRows) throws MiddlewareQueryException {
        try {
        	if (traitId != null){
	            SQLQuery query = getSession().createSQLQuery(Qtl.GET_QTL_BY_TRAIT);
	            query.setParameter("qtlTrait", traitId);
	            query.setFirstResult(start);
	            query.setMaxResults(numOfRows);
	            return (List<Integer>) query.list();
        	}
        } catch (HibernateException e) {
        	logAndThrowException("Error with getQtlByTrait(traitId=" + traitId + ") query from gdms_qtl_details: "
                    + e.getMessage(), e);
        }
        return new ArrayList<Integer>();
    }

    public long countQtlByTrait(Integer traitId) throws MiddlewareQueryException {
        try {
        	if (traitId != null){
	            Query query = getSession().createSQLQuery(Qtl.COUNT_QTL_BY_TRAIT);
	            query.setParameter("qtlTrait", traitId);
	            BigInteger result = (BigInteger) query.uniqueResult();
	            if (result != null) {
	                return result.longValue();
	            }
        	}
        } catch (HibernateException e) {
        	logAndThrowException("Error with countQtlByTrait(traitId=" + traitId + ") query from gdms_qtl_details: "
                    + e.getMessage(), e);
        }
        return 0;
    }

    
    @SuppressWarnings("unchecked")
    public List<Integer> getQTLIdsByDatasetIds(List<Integer> datasetIds) throws MiddlewareQueryException{
        try {
            if (datasetIds != null && datasetIds.get(0) != null){
                Query query = getSession().createSQLQuery(Qtl.GET_QTL_IDS_BY_DATASET_IDS);
                query.setParameterList("datasetIds", datasetIds);
                return (List<Integer>) query.list();
            }
        } catch (HibernateException e) {
            logAndThrowException("Error with getQTLIdsByDatasetIds(datasetIds=" + datasetIds + ") query from gdms_qtl: "
                    + e.getMessage(), e);
        }
        return new ArrayList<Integer>();
    }
    
 	public void deleteByQtlIds(List<Integer> qtlIds) throws MiddlewareQueryException {
		try {
			this.flush();
			
			SQLQuery statement = getSession().createSQLQuery("DELETE FROM gdms_qtl WHERE qtl_id IN (" + StringUtils.join(qtlIds, ",") + ")");
			statement.executeUpdate();

			this.flush();
            this.clear();

		} catch(HibernateException e) {
			logAndThrowException("Error in deleteByQtlIds=" + qtlIds + " from Qtl: " + e.getMessage(), e);
		}
    }
 	
 	@SuppressWarnings("unchecked")
    public Map<Integer, String> getQtlNameByQtlIds(List<Integer> qtlIds) throws MiddlewareQueryException{
 	   Map<Integer, String> qtlNames = new HashMap<Integer, String>();
 	   
       try {
           /*
               SELECT DISTINCT qtl_id, qtl_name 
               FROM gdms_qtl 
               WHERE qtl_id IN (:qtlIds);
            */

           StringBuilder sqlString = new StringBuilder()
           .append("SELECT DISTINCT qtl_id, CONCAT(qtl_name, '')  ")
           .append("FROM gdms_qtl  ")
           .append("WHERE qtl_id IN (:qtlIds) ")
           ;
       
           Query query = getSession().createSQLQuery(sqlString.toString());
           query.setParameterList("qtlIds", qtlIds);

           List<Object[]> list =  query.list();
           
           if (list != null && list.size() > 0) {
               for (Object[] row : list){
                   Integer qtlId = (Integer) row[0];
                   String qtlName = (String) row [1]; 

                   qtlNames.put(qtlId, qtlName);
               }
           }

       } catch(HibernateException e) {
           logAndThrowException("Error in getMapNameByMarkerIds() query from QTL: " + e.getMessage(), e);
       }
 	   
 	   return qtlNames;
 	}
 	
 	
 	
}
