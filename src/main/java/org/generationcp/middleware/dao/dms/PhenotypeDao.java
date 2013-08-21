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
package org.generationcp.middleware.dao.dms;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.h2h.NumericTraitInfo;
import org.generationcp.middleware.domain.h2h.TraitInfo;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;

/**
 * DAO class for {@link Phenotype}.
 * 
 */
@SuppressWarnings("unchecked")
public class PhenotypeDao extends GenericDAO<Phenotype, Integer> {
    
    public List<NumericTraitInfo>  getNumericTraitInfoList(List<Integer> environmentIds, List<Integer> numericVariableIds) throws MiddlewareQueryException{
        List<NumericTraitInfo> numericTraitInfoList = new ArrayList<NumericTraitInfo>();
        try {
            SQLQuery query = getSession().createSQLQuery(
                    "SELECT p.observable_id, "
                    + "COUNT(DISTINCT e.nd_geolocation_id) AS location_count, "
                    + "COUNT(DISTINCT es.stock_id) AS germplasm_count, "
                    + "COUNT(DISTINCT e.nd_experiment_id) AS observation_count, "
                    + "MIN(p.value * 1) AS min_value, "
                    + "MAX(p.value * 1) AS max_value "
                    + "FROM phenotype p "
                    + "    INNER JOIN nd_experiment_phenotype eph ON eph.phenotype_id = p.phenotype_id "
                    + "    INNER JOIN nd_experiment e ON e.nd_experiment_id = eph.nd_experiment_id "
                    + "    INNER JOIN nd_experiment_stock es ON es.nd_experiment_id = e.nd_experiment_id "
                    + "WHERE e.nd_geolocation_id IN (:environmentIds) "
                    + "    AND p.observable_id IN (:numericVariableIds) "
                    + "GROUP by p.observable_id ");
            query.setParameterList("environmentIds", environmentIds);
            query.setParameterList("numericVariableIds", numericVariableIds);
            
            List<Object[]> list =  query.list();
              
            for (Object[] row : list){
                Integer id = (Integer) row[0]; 
                Long locationCount = ((BigInteger) row [1]).longValue();
                Long germplasmCount =((BigInteger) row [2]).longValue();
                Long observationCount = ((BigInteger)  row [3]).longValue();
                Double minValue = (Double) row[4];
                Double maxValue = (Double) row[5];
                  
                numericTraitInfoList.add(new NumericTraitInfo(null, id, null, locationCount, germplasmCount, observationCount, minValue, maxValue, 0));
              }
            
        } catch(HibernateException e) {
            logAndThrowException("Error at getNumericTraitInfoList() query on PhenotypeDao: " + e.getMessage(), e);
        }

        return numericTraitInfoList;

    }
    
    
    public List<TraitInfo>  getTraitInfoCounts(List<Integer> environmentIds, List<Integer> numericVariableIds) throws MiddlewareQueryException{
        List<TraitInfo> traitInfoList = new ArrayList<TraitInfo>();
        try {
            SQLQuery query = getSession().createSQLQuery(
                    "SELECT p.observable_id, "
                    + "COUNT(DISTINCT e.nd_geolocation_id) AS location_count, "
                    + "COUNT(DISTINCT es.stock_id) AS germplasm_count, "
                    + "COUNT(DISTINCT e.nd_experiment_id) AS observation_count "
                    + "FROM phenotype p "
                    + "    INNER JOIN nd_experiment_phenotype eph ON eph.phenotype_id = p.phenotype_id "
                    + "    INNER JOIN nd_experiment e ON e.nd_experiment_id = eph.nd_experiment_id "
                    + "    INNER JOIN nd_experiment_stock es ON es.nd_experiment_id = e.nd_experiment_id "
                    + "WHERE e.nd_geolocation_id IN (:environmentIds) "
                    + "    AND p.observable_id IN (:numericVariableIds) "
                    + "GROUP by p.observable_id ");
            query.setParameterList("environmentIds", environmentIds);
            query.setParameterList("numericVariableIds", numericVariableIds);
            
            List<Object[]> list =  query.list();
              
            for (Object[] row : list){
                Integer id = (Integer) row[0]; 
                Long locationCount = ((BigInteger) row [1]).longValue();
                Long germplasmCount =((BigInteger) row [2]).longValue();
                Long observationCount = ((BigInteger)  row [3]).longValue();
                  
                traitInfoList.add(new TraitInfo(null, id, null, locationCount, germplasmCount, observationCount));
              }
            
        } catch(HibernateException e) {
            logAndThrowException("Error at getTraitInfoCounts() query on PhenotypeDao: " + e.getMessage(), e);
        }

        return traitInfoList;

    }
    
    
    public List<Double>  getNumericTraitInfoValues(List<Integer> environmentIds, Integer traitId) throws MiddlewareQueryException{
        List<Double> toReturn = new ArrayList<Double>();
        try {
            SQLQuery query = getSession().createSQLQuery(
                    "SELECT p.value * 1 "
                    + "FROM phenotype p "
                    + "    INNER JOIN nd_experiment_phenotype eph ON eph.phenotype_id = p.phenotype_id "
                    + "    INNER JOIN nd_experiment e ON e.nd_experiment_id = eph.nd_experiment_id "
                    + "WHERE e.nd_geolocation_id IN (:environmentIds) "
                    + "    AND p.observable_id = :traitId "
                    );
            query.setParameterList("environmentIds", environmentIds);
            query.setParameter("traitId", traitId);

            toReturn = (List<Double>) query.list();
            
        } catch(HibernateException e) {
            logAndThrowException("Error at getNumericTraitInfoValues() query on PhenotypeDao: " + e.getMessage(), e);
        }

        return toReturn;

    }


    public List<String>  getCharacterTraitInfoValues(List<Integer> environmentIds, Integer traitId) throws MiddlewareQueryException{
        List<String> toReturn = new ArrayList<String>();
        try {
            SQLQuery query = getSession().createSQLQuery(
                    "SELECT DISTINCT p.value "
                    + "FROM phenotype p "
                    + "    INNER JOIN nd_experiment_phenotype eph ON eph.phenotype_id = p.phenotype_id "
                    + "    INNER JOIN nd_experiment e ON e.nd_experiment_id = eph.nd_experiment_id "
                    + "WHERE e.nd_geolocation_id IN (:environmentIds) "
                    + "    AND p.observable_id = :traitId "
                    );
            query.setParameterList("environmentIds", environmentIds);
            query.setParameter("traitId", traitId);

            toReturn = (List<String>) query.list();
            
        } catch(HibernateException e) {
            logAndThrowException("Error at getCharacterraitInfoValues() query on PhenotypeDao: " + e.getMessage(), e);
        }

        return toReturn;

    }
    
    public Map<String, Integer>  getCategoricalTraitInfoValues(List<Integer> environmentIds, Integer traitId) throws MiddlewareQueryException{
        Map<String, Integer> toReturn = new HashMap<String, Integer>();
        try {
            //TODO
            SQLQuery query = getSession().createSQLQuery(
                    "SELECT p.cvalue_id, count(p.phenotype_id) "
                    + "FROM phenotype p "
                    + "    INNER JOIN nd_experiment_phenotype eph ON eph.phenotype_id = p.phenotype_id "
                    + "    INNER JOIN nd_experiment e ON e.nd_experiment_id = eph.nd_experiment_id "
                    + "WHERE e.nd_geolocation_id IN (:environmentIds) "
                    + "    AND p.observable_id = :traitId "
                    );
            query.setParameterList("environmentIds", environmentIds);
            query.setParameter("traitId", traitId);

            List<Object> results = query.list();
            for(Object result : results){
                Object resultArray[] = (Object[]) result;
                String value = (String) resultArray[0];
                Integer count = (Integer) resultArray[1];
                toReturn.put(value, count);
            }

            
        } catch(HibernateException e) {
            logAndThrowException("Error at getCategoricalTraitInfoValues() query on PhenotypeDao: " + e.getMessage(), e);
        }

        return toReturn;

    }

}
