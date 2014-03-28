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
import org.generationcp.middleware.pojos.gdms.QtlDetails;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;

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

    public List<QtlDetailElement> getQtlAndQtlDetailsByQtlIds(List<Integer> qtlIDs, int start, int numOfRows) 
    		throws MiddlewareQueryException{
        List<QtlDetailElement> toReturn = new ArrayList<QtlDetailElement>();

        try {
        	if (qtlIDs != null && !qtlIDs.isEmpty()){
	            SQLQuery query = getSession().createSQLQuery(Qtl.GET_QTL_AND_QTL_DETAILS_BY_QTL_IDS);
	            query.setParameterList("qtl_id_list", qtlIDs);
	            query.setFirstResult(start);
	            query.setMaxResults(numOfRows);
	            
	            toReturn = getQtlAndQtlDetails(query);
        	}
        } catch (HibernateException e) {
        	logAndThrowException("Error with getQtlAndQtlDetailsByQtlIds(qtl ids=" + qtlIDs 
        			+ ") query from gdms_qtl_details: " + e.getMessage(), e);
        }
        return toReturn;
    }

    public long countQtlAndQtlDetailsByQtlIds(List<Integer> qtlIDs) throws MiddlewareQueryException {
        long count = 0;
        try {
            Query query = getSession().createSQLQuery(Qtl.COUNT_QTL_AND_QTL_DETAILS_BY_QTL_IDS);
            query.setParameterList("qtl_id_list", qtlIDs);
            BigInteger result = (BigInteger) query.uniqueResult();
            if (result != null) {
                count += result.longValue();
            } 
        } catch (HibernateException e) {
        	logAndThrowException("Error with countQtlAndQtlDetailsByQtlIds(qtl ids=" + qtlIDs + ") query from gdms_qtl_details: "
                    + e.getMessage(), e);
        }
        return count;
    }
    
    public List<QtlDetailElement> getQtlAndQtlDetailsByName(String name, int start, int numOfRows) throws MiddlewareQueryException{
        List<QtlDetailElement> toReturn = new ArrayList<QtlDetailElement>();

        try {
            SQLQuery query = getSession().createSQLQuery(Qtl.GET_QTL_AND_QTL_DETAILS_BY_NAME);
            query.setParameter("qtlName", name);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);

            toReturn = getQtlAndQtlDetails(query);
        } catch (HibernateException e) {
        	logAndThrowException("Error with getQtlDetailsByName(name=" + name 
        			+ ") query from gdms_qtl_details: " + e.getMessage(), e);
        }
        return toReturn;
    }
    

    public long countQtlAndQtlDetailsByName(String name) throws MiddlewareQueryException {
        try {
            Query query = getSession().createSQLQuery(Qtl.COUNT_QTL_AND_QTL_DETAILS_BY_NAME);
            query.setParameter("qtlName", name);
            BigInteger result = (BigInteger) query.uniqueResult();
            if (result != null) {
                return result.longValue();
            } else {
                return 0;
            }
        } catch (HibernateException e) {
            logAndThrowException("Error with countQtlAndQtlDetailsByName(name=" + name 
                    + ") query from gdms_qtl_details: " + e.getMessage(), e);
        }
        return 0L;
    }
    
    public List<QtlDetailElement> getQtlDetailsByQtlTraits(List<Integer> qtlTraits, int start, int numOfRows) throws MiddlewareQueryException{
        List<QtlDetailElement> toReturn = new ArrayList<QtlDetailElement>();

        try {
            SQLQuery query = getSession().createSQLQuery(Qtl.GET_QTL_DETAILS_BY_TRAITS);
            query.setParameterList("qtlTraitIds", qtlTraits);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);

            toReturn = getQtlAndQtlDetails(query);
        } catch (HibernateException e) {
            logAndThrowException("Error with getQtlDetailsByQtlTraits(qtlTraits=" + qtlTraits 
                    + ") query from gdms_qtl_details: " + e.getMessage(), e);
        }
        return toReturn;
    }
    

    public long countQtlDetailsByQtlTraits(List<Integer> traitIds) throws MiddlewareQueryException {
        try {
            Query query = getSession().createSQLQuery(Qtl.COUNT_QTL_DETAILS_BY_TRAITS);
            query.setParameterList("qtlTraitIds", traitIds);
            BigInteger result = (BigInteger) query.uniqueResult();
            if (result != null) {
                return result.longValue();
            } else {
                return 0;
            }
        } catch (HibernateException e) {
            logAndThrowException("Error with countQtlAndQtlDetailsByName(traitIds=" + traitIds 
                    + ") query from gdms_qtl_details: " + e.getMessage(), e);
        }
        return 0L;
    }
    
    
    @SuppressWarnings("rawtypes")
    public List<QtlDetailElement> getQtlAndQtlDetails(SQLQuery query) throws HibernateException{
        List<QtlDetailElement> toReturn = new ArrayList<QtlDetailElement>();

        List results = query.list();

        for (Object o : results) {
            Object[] result = (Object[]) o;
            if (result != null) {
                Integer qtlId = (Integer) result[0];
                String qtlName = (String) result[1];
                Integer mapId = (Integer) result[2];
                String mapName = (String) result[3];
                String chromosome = (String) result[4];
                Float minPosition = (Float) result[5];
                Float maxPosition = (Float) result[6];
                Integer traitId = (Integer) result[7];
                String experiment = (String) result[8];
                String leftFlankingMarker = (String) result[9];
                String rightFlankingMarker = (String) result[10];
                Integer effect = (Integer) result[11];
                Float scoreValue = (Float) result[12];
                Float rSquare = (Float) result[13];
                String interactions = (String) result[14];
                Float position = (Float) result[15];
                Float clen = (Float) result[16];
                String seAdditive = (String) result[17];
                String hvParent = (String) result[18];
                String hvAllele = (String) result[19];
                String lvParent = (String) result[20];
                String lvAllele = (String) result[21];
                String tRName = (String) result[22];
                String ontology = (String) result[23];
                
                QtlDetails qtlDetails = new QtlDetails(qtlId, mapId, minPosition, maxPosition, traitId, experiment, effect,
                        scoreValue, rSquare, chromosome,  interactions, leftFlankingMarker,
                        rightFlankingMarker, position, clen, seAdditive, hvParent, hvAllele, lvParent,
                        lvAllele);
                
                QtlDetailElement element = new QtlDetailElement(qtlName, mapName, qtlDetails, tRName, ontology);
                toReturn.add(element);
            }
        }
        return toReturn;
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
 	
 	@SuppressWarnings("unchecked")
    public List<Qtl> getQtlsByIds(List<Integer> qtlIds) throws MiddlewareQueryException{
 	        try {
 	            Criteria criteria = getSession().createCriteria(getPersistentClass());
 	            criteria.add(Restrictions.in("qtlId", qtlIds));
 	            
 	            return criteria.list();

 	        } catch(HibernateException e) {
 	            logAndThrowException("Error in getQtlsByIds=" + qtlIds + " in QtlDAO: " + e.getMessage(), e);
 	        }
 	        return new ArrayList<Qtl>();
 
 	}
 	
    @SuppressWarnings("unchecked")
    public List<Qtl> getQtlByName(String qtlName) throws MiddlewareQueryException{
            try {
                Criteria criteria = getSession().createCriteria(getPersistentClass());
                criteria.add(Restrictions.like("qtlName", qtlName));
                
                return criteria.list();

            } catch(HibernateException e) {
                logAndThrowException("Error in getQtlByName=" + qtlName + " in QtlDAO: " + e.getMessage(), e);
            }
            return new ArrayList<Qtl>();
 
    }
 	
}
