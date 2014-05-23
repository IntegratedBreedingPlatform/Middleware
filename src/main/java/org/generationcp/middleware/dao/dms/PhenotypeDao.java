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
import java.util.Set;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.dms.TrialEnvironment;
import org.generationcp.middleware.domain.h2h.CategoricalTraitInfo;
import org.generationcp.middleware.domain.h2h.CategoricalValue;
import org.generationcp.middleware.domain.h2h.CharacterTraitInfo;
import org.generationcp.middleware.domain.h2h.NumericTraitInfo;
import org.generationcp.middleware.domain.h2h.Observation;
import org.generationcp.middleware.domain.h2h.ObservationKey;
import org.generationcp.middleware.domain.h2h.TraitInfo;
import org.generationcp.middleware.domain.h2h.TraitObservation;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.util.Debug;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DAO class for {@link Phenotype}.
 * 
 */
@SuppressWarnings("unchecked")
public class PhenotypeDao extends GenericDAO<Phenotype, Integer> {
    
    private static final Logger LOG = LoggerFactory.getLogger(PhenotypeDao.class);
    
    private static final String GET_OBSERVATIONS = 
    	"SELECT p.observable_id, s.dbxref_id, e.nd_geolocation_id, p.value " +
		"FROM nd_experiment e " +
		"INNER JOIN nd_experiment_stock es ON e.nd_experiment_id = es.nd_experiment_id " +
		"INNER JOIN stock s ON es.stock_id = s.stock_id " +
		"INNER JOIN nd_experiment_phenotype ep ON e.nd_experiment_id = ep.nd_experiment_id " +
		"INNER JOIN phenotype p ON ep.phenotype_id = p.phenotype_id " +
		"WHERE e.nd_geolocation_id IN (:environmentIds) " +
		"AND p.observable_id IN (:traitIds) ";
    
    private static final String COUNT_OBSERVATIONS = 
    	"SELECT COUNT(*) " +
		"FROM nd_experiment e " +
		"INNER JOIN nd_experiment_stock es ON e.nd_experiment_id = es.nd_experiment_id " +
		"INNER JOIN stock s ON es.stock_id = s.stock_id " +
		"INNER JOIN nd_experiment_phenotype ep ON e.nd_experiment_id = ep.nd_experiment_id " +
		"INNER JOIN phenotype p ON ep.phenotype_id = p.phenotype_id " +
		"WHERE e.nd_geolocation_id IN (:environmentIds) " +
		"AND p.observable_id IN (:traitIds) ";
    
    private static final String ORDER_BY_OBS = "ORDER BY p.observable_id, s.dbxref_id, e.nd_geolocation_id, p.value ";
	
    
	public List<NumericTraitInfo>  getNumericTraitInfoList(List<Integer> environmentIds, List<Integer> numericVariableIds) throws MiddlewareQueryException{
        List<NumericTraitInfo> numericTraitInfoList = new ArrayList<NumericTraitInfo>();
        try {
            SQLQuery query = getSession().createSQLQuery(
                    "SELECT p.observable_id, "
                    + "COUNT(DISTINCT e.nd_geolocation_id) AS location_count, "
                    + "COUNT(DISTINCT s.dbxref_id) AS germplasm_count, "
                    + "COUNT(DISTINCT e.nd_experiment_id) AS observation_count , "
                    + "IF (MIN(p.value * 1) IS NULL, 0, MIN(p.value * 1))  AS min_value, "
                    + "IF (MAX(p.value * 1) IS NULL, 0, MAX(p.value * 1)) AS max_value "
                    + "FROM phenotype p "
                    + "    INNER JOIN nd_experiment_phenotype eph ON eph.phenotype_id = p.phenotype_id "
                    + "    INNER JOIN nd_experiment e ON e.nd_experiment_id = eph.nd_experiment_id "
                    + "    INNER JOIN nd_experiment_stock es ON es.nd_experiment_id = e.nd_experiment_id "
                    + "    INNER JOIN stock s ON es.stock_id = s.stock_id "
                    + "WHERE e.nd_geolocation_id IN (:environmentIds) "
                    + "    AND p.observable_id IN (:numericVariableIds) "
                    + "GROUP by p.observable_id ");
            query.setParameterList("environmentIds", environmentIds);
            query.setParameterList("numericVariableIds", numericVariableIds);
            
            List<Object[]> list = new ArrayList<Object[]>();
            
            if(environmentIds.size()>0 && numericVariableIds.size()>0){
            	list = query.list();
            	
            	for (Object[] row : list){
            		Integer id = (Integer) row[0]; 
            		Long locationCount = ((BigInteger) row [1]).longValue();
            		Long germplasmCount =((BigInteger) row [2]).longValue();
            		Long observationCount = ((BigInteger)  row [3]).longValue();
            		Double minValue = (Double) row[4];
            		Double maxValue = (Double) row[5];
            		
            		NumericTraitInfo numericTraitInfo = new NumericTraitInfo(null, id, null, locationCount, germplasmCount, observationCount, minValue, maxValue, 0);
					numericTraitInfoList.add(numericTraitInfo);
            	}
            }
              
            
        } catch(HibernateException e) {
            logAndThrowException("Error at getNumericTraitInfoList() query on PhenotypeDao: " + e.getMessage(), e);
        }

        return numericTraitInfoList;

    }
    
    
    public List<TraitInfo>  getTraitInfoCounts(List<Integer> environmentIds, List<Integer> variableIds) throws MiddlewareQueryException{
        List<TraitInfo> traitInfoList = new ArrayList<TraitInfo>();
        try {
            SQLQuery query = getSession().createSQLQuery(
                    "SELECT p.observable_id, "
                    + "COUNT(DISTINCT e.nd_geolocation_id) AS location_count, "
                    + "COUNT(DISTINCT s.dbxref_id) AS germplasm_count, "
                    + "COUNT(DISTINCT e.nd_experiment_id) AS observation_count "
                    + "FROM phenotype p "
                    + "    INNER JOIN nd_experiment_phenotype eph ON eph.phenotype_id = p.phenotype_id "
                    + "    INNER JOIN nd_experiment e ON e.nd_experiment_id = eph.nd_experiment_id "
                    + "    INNER JOIN nd_experiment_stock es ON es.nd_experiment_id = e.nd_experiment_id "
                    + "    INNER JOIN stock s ON es.stock_id = s.stock_id "
                    + "WHERE e.nd_geolocation_id IN (:environmentIds) "
                    + "    AND p.observable_id IN (:variableIds) "
                    + "GROUP by p.observable_id ");
            query.setParameterList("environmentIds", environmentIds);
            query.setParameterList("variableIds", variableIds);
            
            List<Object[]> list = new ArrayList<Object[]>();

            if(environmentIds.size()>0 && variableIds.size()>0)
                list = query.list();
              
            for (Object[] row : list){
                Integer id = (Integer) row[0]; 
                Long locationCount = ((BigInteger) row [1]).longValue();
                Long germplasmCount =((BigInteger) row [2]).longValue();
                Long observationCount = ((BigInteger)  row [3]).longValue();
                  
                traitInfoList.add(new TraitInfo(id, null, null, locationCount, germplasmCount, observationCount)); 
              }
            
        } catch(HibernateException e) {
            logAndThrowException("Error at getTraitInfoCounts() query on PhenotypeDao: " + e.getMessage(), e);
        }

        return traitInfoList;

    }
    

    public List<TraitInfo>  getTraitInfoCounts(List<Integer> environmentIds) throws MiddlewareQueryException{
        List<TraitInfo> traitInfoList = new ArrayList<TraitInfo>();
        try {
            SQLQuery query = getSession().createSQLQuery(
                    "SELECT p.observable_id, "
                    + "COUNT(DISTINCT e.nd_geolocation_id) AS location_count, "
                    + "COUNT(DISTINCT s.dbxref_id) AS germplasm_count, "
                    + "COUNT(DISTINCT e.nd_experiment_id) AS observation_count "
                    + "FROM phenotype p "
                    + "    INNER JOIN nd_experiment_phenotype eph ON eph.phenotype_id = p.phenotype_id "
                    + "    INNER JOIN nd_experiment e ON e.nd_experiment_id = eph.nd_experiment_id "
                    + "    INNER JOIN nd_experiment_stock es ON es.nd_experiment_id = e.nd_experiment_id "
                    + "    INNER JOIN stock s ON es.stock_id = s.stock_id "
                    + "WHERE e.nd_geolocation_id IN (:environmentIds) "
                    + "GROUP by p.observable_id ");
            query.setParameterList("environmentIds", environmentIds);
            
            List<Object[]> list =  query.list();
              
            for (Object[] row : list){
                Integer id = (Integer) row[0]; 
                Long locationCount = ((BigInteger) row [1]).longValue();
                Long germplasmCount =((BigInteger) row [2]).longValue();
                Long observationCount = ((BigInteger)  row [3]).longValue();
                  
                traitInfoList.add(new TraitInfo(id, null, null, locationCount, germplasmCount, observationCount)); 
              }
            
        } catch(HibernateException e) {
            logAndThrowException("Error at getTraitInfoCounts() query on PhenotypeDao: " + e.getMessage(), e);
        }

        return traitInfoList;

    }
    
    public  Map<Integer, List<Double>> getNumericTraitInfoValues(List<Integer> environmentIds, List<NumericTraitInfo> traitInfoList) throws MiddlewareQueryException{
        Map<Integer, List<Double>> traitValues = new HashMap<Integer, List<Double>>();
        
        // Get trait IDs
        List<Integer> traitIds = new ArrayList<Integer>();
        for (NumericTraitInfo trait : traitInfoList){
            traitIds.add(trait.getId());
        }

        try {
            SQLQuery query = getSession().createSQLQuery(
                    "SELECT p.observable_id, p.value * 1 "
                    + "FROM phenotype p "
                    + "    INNER JOIN nd_experiment_phenotype eph ON eph.phenotype_id = p.phenotype_id "
                    + "    INNER JOIN nd_experiment e ON e.nd_experiment_id = eph.nd_experiment_id "
                    + "WHERE e.nd_geolocation_id IN (:environmentIds) "
                    + "    AND p.observable_id IN (:traitIds) "
                    );
            query.setParameterList("environmentIds", environmentIds);
            query.setParameterList("traitIds", traitIds);

            List<Object[]> list = new ArrayList<Object[]>();

            if(environmentIds.size()>0)
                list = query.list();

            for (Object[] row : list){
                Integer traitId = (Integer) row[0];
                Double value = (Double) row[1];

                List<Double> values = new ArrayList<Double>();
                values.add(value); 
                // If the trait exists in the map, add the value found. Else, just add the <trait, values> pair.
                if (traitValues.containsKey(traitId)){
                    values = traitValues.get(traitId);
                    values.add(value);
                    traitValues.remove(traitId);
                }
                traitValues.put(traitId, values);
                
            }
            
            
        } catch(HibernateException e) {
            logAndThrowException("Error at getNumericTraitInfoValues() query on PhenotypeDao: " + e.getMessage(), e);
        }

        return traitValues;

    }
    
    public  Map<Integer, List<Double>> getNumericTraitInfoValues(List<Integer> environmentIds, Integer trait) throws MiddlewareQueryException{
        Map<Integer, List<Double>> traitValues = new HashMap<Integer, List<Double>>();
        
        try {
            SQLQuery query = getSession().createSQLQuery(
                    "SELECT p.observable_id, p.value * 1 "
                    + "FROM phenotype p "
                    + "    INNER JOIN nd_experiment_phenotype eph ON eph.phenotype_id = p.phenotype_id "
                    + "    INNER JOIN nd_experiment e ON e.nd_experiment_id = eph.nd_experiment_id "
                    + "WHERE e.nd_geolocation_id IN (:environmentIds) "
                    + "    AND p.observable_id = :traitId "
                    );
            query.setParameterList("environmentIds", environmentIds);
            query.setParameter("traitId", trait);

            List<Object[]> list = new ArrayList<Object[]>();

            if(environmentIds.size()>0)
                list = query.list();

            for (Object[] row : list){
                Integer traitId = (Integer) row[0];
                Double value = (Double) row[1];

                List<Double> values = new ArrayList<Double>();
                values.add(value); 
                // If the trait exists in the map, add the value found. Else, just add the <trait, values> pair.
                if (traitValues.containsKey(traitId)){
                    values = traitValues.get(traitId);
                    values.add(value);
                    traitValues.remove(traitId);
                }
                traitValues.put(traitId, values);
                
            }
            
            
        } catch(HibernateException e) {
            logAndThrowException("Error at getNumericTraitInfoValues() query on PhenotypeDao: " + e.getMessage(), e);
        }

        return traitValues;

    }
    
    public Map<Integer, List<String>> getCharacterTraitInfoValues(List<Integer> environmentIds, List<CharacterTraitInfo> traitInfoList) throws MiddlewareQueryException{
        
        Map<Integer, List<String>> traitValues = new HashMap<Integer, List<String>>();
        
        // Get trait IDs
        List<Integer> traitIds = new ArrayList<Integer>();
        for (CharacterTraitInfo trait : traitInfoList){
            traitIds.add(trait.getId());
        }
        
        try {
            SQLQuery query = getSession().createSQLQuery(
                    "SELECT DISTINCT p.observable_id, p.value "
                    + "FROM phenotype p "
                    + "    INNER JOIN nd_experiment_phenotype eph ON eph.phenotype_id = p.phenotype_id "
                    + "    INNER JOIN nd_experiment e ON e.nd_experiment_id = eph.nd_experiment_id "
                    + "WHERE e.nd_geolocation_id IN (:environmentIds) "
                    + "    AND p.observable_id IN (:traitIds) " 
                    + "ORDER BY p.observable_id "
                    );
            query.setParameterList("environmentIds", environmentIds);
            query.setParameterList("traitIds", traitIds);

            List<Object[]> list = new ArrayList<Object[]>();

            if(environmentIds.size()>0 && environmentIds.size()>0)
                list = query.list();
            
            for (Object[] row : list){
                Integer traitId = (Integer) row[0];
                String value = (String) row[1];
                
                List<String> values = new ArrayList<String>();
                values.add(value); 
                // If the trait exists in the map, add the value found. Else, just add the <trait, values> pair.
                if (traitValues.containsKey(traitId)){
                    values = traitValues.get(traitId);
                    values.add(value);
                    traitValues.remove(traitId);
                }
                traitValues.put(traitId, values);
            }
            
        } catch(HibernateException e) {
            logAndThrowException("Error at getCharacterraitInfoValues() query on PhenotypeDao: " + e.getMessage(), e);
        }
        return traitValues;

    }
    
    public void setCategoricalTraitInfoValues(List<CategoricalTraitInfo> traitInfoList, boolean isCentral) throws MiddlewareQueryException{
        Map<Integer, String> valueIdName = new HashMap<Integer, String>();

        // Get trait IDs
        List<Integer> traitIds = new ArrayList<Integer>();
        for (CategoricalTraitInfo trait : traitInfoList){
            traitIds.add(trait.getId());
        }
        
        String queryString = 
                "SELECT p.observable_id, p.cvalue_id, COUNT(p.phenotype_id) AS valuesCount " 
                + "FROM phenotype p "
                + "WHERE p.cvalue_id IS NOT NULL AND p.observable_id IN (:traitIds) "
                + "GROUP BY p.observable_id, p.cvalue_id ";
        
        if (isCentral) {
                queryString = 
                    "SELECT p.observable_id, p.cvalue_id, c.name, COUNT(p.phenotype_id) AS valuesCount " 
                    + "FROM phenotype p  "
                    + "INNER JOIN cvterm c on p.cvalue_id = c.cvterm_id "
                    + "WHERE p.cvalue_id IS NOT NULL AND p.observable_id IN (:traitIds) " 
                    + "GROUP BY p.observable_id, p.cvalue_id, c.name  ";
            }

        try {
            SQLQuery query = getSession().createSQLQuery(queryString);
            query.setParameterList("traitIds", traitIds);

            List<Object[]> list = new ArrayList<Object[]>();

            if(traitIds.size()>0)
                list = query.list();
            
            for (Object[] row : list){
                Integer traitId = (Integer) row[0]; 
                Integer cValueId = (Integer) row [1];
                Long count = 0L;
                if (isCentral){
                    String cValueName = (String) row[2];
                    valueIdName.put(cValueId, cValueName);
                    count = ((BigInteger) row[3]).longValue();
                } else {
                    count = ((BigInteger) row[2]).longValue();
                }
                
                // add value count to categorical traits
                for (CategoricalTraitInfo traitInfo: traitInfoList){
                    if(traitInfo.getId() == traitId){
                        traitInfo.addValueCount(new CategoricalValue(cValueId), count.longValue());
                        break;
                    }
                }
                
            }
            
            // This step was added since the valueName is not retrieved correctly with the previous query in Java (setCategoricalVariables). 
            // Most probably because of the two cvterm id-name present in the query.
            // The steps that follow will just retrieve the name of the categorical values in each variable.
            
            if (isCentral){
                for (CategoricalTraitInfo traitInfo : traitInfoList){
                    List<CategoricalValue> values = traitInfo.getValues();
                    for (CategoricalValue value : values){
                        String name = valueIdName.get(value.getId());
                        value.setName(name);
                    }
                    traitInfo.setValues(values);    
                }
            }

        } catch(HibernateException e) {
            logAndThrowException("Error at getCategoricalTraitInfoValues() query on PhenotypeDao: " + e.getMessage(), e);
        }
        
    }

    
    public void setCategoricalTraitInfoValues(List<CategoricalTraitInfo> traitInfoList, List<Integer> environmentIds) throws MiddlewareQueryException{
        
        // Get trait IDs
        List<Integer> traitIds = new ArrayList<Integer>();
        for (CategoricalTraitInfo trait : traitInfoList){
            traitIds.add(trait.getId());
        }

        try {
            SQLQuery query = getSession().createSQLQuery(
                    "SELECT p.observable_id, p.cvalue_id, COUNT(p.phenotype_id) AS valuesCount "
                    + "FROM phenotype p "
                    + "INNER JOIN nd_experiment_phenotype eph ON eph.phenotype_id = p.phenotype_id "
                    + "INNER JOIN nd_experiment e ON e.nd_experiment_id = eph.nd_experiment_id "
                    + "WHERE p.cvalue_id IS NOT NULL AND p.observable_id IN (:traitIds) "
                    + "  AND e.nd_geolocation_id IN (:environmentIds) "
                    + "GROUP BY p.observable_id, p.cvalue_id "
                    );
            query.setParameterList("traitIds", traitIds);
            query.setParameterList("environmentIds", environmentIds);

            List<Object[]> list = new ArrayList<Object[]>();

            if(environmentIds.size()>0 && traitIds.size()>0)
                list = query.list();
            
            for (Object[] row : list){
                Integer traitId = (Integer) row[0]; 
                Integer cValueId = (Integer) row [1];
                Long count = ((BigInteger) row[2]).longValue();
                
                for (CategoricalTraitInfo traitInfo: traitInfoList){
                    if(traitInfo.getId() == traitId){
                        traitInfo.addValueCount(new CategoricalValue(cValueId), count.longValue());
                        break;
                    }
                }
                
            }

        } catch(HibernateException e) {
            logAndThrowException("Error at getCategoricalTraitInfoValues() query on PhenotypeDao: " + e.getMessage(), e);
        }
        
    }

    public List<Observation> getObservationForTraitOnGermplasms(
    		List<Integer> traitIds, 
            List<Integer> germplasmIds, List<Integer> environmentIds) throws MiddlewareQueryException {
    	List<Observation> observationFinal = new ArrayList<Observation>();
        
        try {
        	StringBuilder sb = new StringBuilder(GET_OBSERVATIONS);
        	sb.append(" AND s.dbxref_id IN (:germplasmIds) ");
        	sb.append(ORDER_BY_OBS);
            SQLQuery query = getSession().createSQLQuery(sb.toString());
            query.setParameterList("traitIds", traitIds);
            query.setParameterList("germplasmIds", germplasmIds);
            query.setParameterList("environmentIds", environmentIds);

            List<Object[]> list = new ArrayList<Object[]>();

            if(environmentIds.size()>0 && traitIds.size()>0)
                list = query.list();
            
            for (Object[] row : list){
                Integer traitId = (Integer) row[0]; 
                Integer germplasmId = (Integer) row [1];
                Integer environmentId = (Integer) row[2];
                String value = (String) row[3];
                
                ObservationKey rowKey = new ObservationKey(traitId, germplasmId, environmentId);
                Observation observation = new Observation(rowKey,value);
                observationFinal.add(observation);
            }

        } catch(HibernateException e) {
            logAndThrowException("Error at getObservationForTraitOnGermplasms() query on PhenotypeDao: " + e.getMessage(), e);
        }
        
        return observationFinal;
    }
    
              
    
    public long countObservationForTraits(List<Integer> traitIds, List<Integer> environmentIds) throws MiddlewareQueryException {

    	try {
    		SQLQuery query = getSession().createSQLQuery(COUNT_OBSERVATIONS);
    	    query.setParameterList("traitIds", traitIds);
            query.setParameterList("environmentIds", environmentIds);
    		return ((BigInteger) query.uniqueResult()).longValue(); 
            

        } catch(HibernateException e) {
            logAndThrowException("Error at countObservationForTraits() query on PhenotypeDao: " + e.getMessage(), e);
        }
        return 0;
    }
    
    public List<Observation> getObservationForTraits(List<Integer> traitIds, List<Integer> environmentIds, 
    		int start, int numOfRows) throws MiddlewareQueryException {

    	List<Observation> toReturn = new ArrayList<Observation>();
        
        try {
        	StringBuilder sb = new StringBuilder(GET_OBSERVATIONS);
        	sb.append(ORDER_BY_OBS);
            SQLQuery query = getSession().createSQLQuery(sb.toString());
            
            query.setParameterList("traitIds", traitIds);
            query.setParameterList("environmentIds", environmentIds);
            setStartAndNumOfRows(query, start, numOfRows);
            List<Object[]> list =  query.list();
            
            for (Object[] row : list){
                Integer traitId = (Integer) row[0]; 
                Integer germplasmId = (Integer) row [1];
                Integer environmentId = (Integer) row[2];
                String value = (String) row[3];

                toReturn.add(new Observation(new ObservationKey(traitId, germplasmId, environmentId), value));

            }

        } catch(HibernateException e) {
            logAndThrowException("Error at getObservationForTraits() query on PhenotypeDao: " + e.getMessage(), e);
        }
        return toReturn;
    }
    


	public List<TraitObservation> getObservationsForTrait(int traitId, List<Integer> environmentIds) throws MiddlewareQueryException {
		List<TraitObservation> toreturn = new ArrayList<TraitObservation>();

		try {
			StringBuilder queryString = new StringBuilder();
			queryString.append("SELECT p.observable_id, p.value, s.dbxref_id, e.nd_experiment_id, l.lname ");
			queryString.append("FROM phenotype p ");
			queryString.append("INNER JOIN nd_experiment_phenotype eph ON eph.phenotype_id = p.phenotype_id ");
			queryString.append("INNER JOIN nd_experiment e ON e.nd_experiment_id = eph.nd_experiment_id ");
			queryString.append("INNER JOIN nd_geolocationprop gp ON gp.nd_geolocation_id = e.nd_geolocation_id AND gp.type_id = " + TermId.LOCATION_ID.getId() + " ");
			queryString.append("INNER JOIN location l ON l.locid = gp.value ");
			queryString.append("INNER JOIN nd_experiment_stock es ON es.nd_experiment_id = e.nd_experiment_id ");
			queryString.append("INNER JOIN stock s ON s.stock_id = es.stock_id ");
			queryString.append("WHERE p.observable_id = :traitId AND e.nd_geolocation_id IN ( :environmentIds ) ");
			queryString.append("ORDER BY s.dbxref_id ");
			
			LOG.debug(queryString.toString());
			 
			 SQLQuery query = getSession().createSQLQuery(queryString.toString());
			 query.setParameter("traitId", traitId)
			 .setParameterList("environmentIds", environmentIds);
			 
			 List<Object[]> list =  query.list();
			 
			  for (Object[] row : list){
	                Integer id = (Integer) row[0]; 
	                String value = (String) row[1];
	                Integer gid = (Integer) row[2];
	                Integer observationId = (Integer) row[3];
	                String locationName = row[4].toString();
	                
	                toreturn.add(new TraitObservation(id, value,gid,observationId,locationName));
	            }

        } catch(HibernateException e) {
            logAndThrowException("Error at getObservationsForTrait() query on PhenotypeDao: " + e.getMessage(), e);
            
        }
		
		return toreturn;
	}


    public List<TrialEnvironment> getEnvironmentTraits(Set<TrialEnvironment> trialEnvironments) throws MiddlewareQueryException {
        List<TrialEnvironment> environmentDetails = new ArrayList<TrialEnvironment>();
        
        if (trialEnvironments.size() == 0){
            return environmentDetails;
        }
        List<Integer> environmentIds = new ArrayList<Integer>();
        for (TrialEnvironment environment : trialEnvironments) {
            environmentIds.add(environment.getId());
            environmentDetails.add(environment);
        }


        try {
    		StringBuilder sql = new StringBuilder()
    		.append("SELECT DISTINCT e.nd_geolocation_id, p.observable_id, trait.name, trait.definition, c_scale.scaleName, cr_type.object_id ") 
    		.append("FROM phenotype p ")
    		.append("	INNER JOIN nd_experiment_phenotype ep ON p.phenotype_id = ep.phenotype_id ")
    		.append("	INNER JOIN nd_experiment e ON ep.nd_experiment_id = e.nd_experiment_id ")
    		.append("				AND e.nd_geolocation_id IN (:environmentIds) ")
    		.append("	LEFT JOIN cvterm_relationship cr_scale ON p.observable_id = cr_scale.subject_id ")
    		.append("	INNER JOIN  (SELECT cvterm_id, name AS scaleName FROM cvterm) c_scale ON c_scale.cvterm_id = cr_scale.object_id ")
    		.append("	    AND cr_scale.type_id = 1220 ")
    		.append("	INNER JOIN cvterm_relationship cr_type ON cr_type.subject_id = cr_scale.subject_id ")
    		.append("	    AND cr_type.type_id = 1105 ")
    		.append("INNER JOIN cvterm_relationship cr_property ON p.observable_id = cr_property.subject_id AND cr_property.type_id = 1200 ") 
    		.append("INNER JOIN cvterm trait ON cr_property.object_id = trait.cvterm_id ")
    		;

    		Query query = getSession().createSQLQuery(sql.toString())
                    .setParameterList("environmentIds", environmentIds);

            List<Object[]> result = query.list();

            for (Object[] row : result) {
                Integer environmentId = (Integer) row[0];
                Integer traitId = (Integer) row[1];
                String traitName = (String) row[2];
                String traitDescription = (String) row[3];
                String scaleName = (String) row[4];
                Integer typeId = (Integer) row [5];

                int index = environmentDetails.indexOf(new TrialEnvironment(environmentId));
                TrialEnvironment environment = environmentDetails.get(index);
                environment.addTrait(new TraitInfo(traitId, traitName, traitDescription, scaleName, typeId));
                environmentDetails.set(index, environment);
            }

        } catch (HibernateException e) {
            logAndThrowException(
                    "Error at getEnvironmentTraits() query on PhenotypeDao: "
                            + e.getMessage(), e);
        }

        return environmentDetails;
    }


	public void deletePhenotypesByProjectIdAndLocationId(Integer projectId, Integer locationId) throws MiddlewareQueryException {
		try {
			this.flush();
			
			// Delete phenotypes and experiment phenotypes
			String sql = "delete pheno, epheno" +
                    " from nd_experiment_project ep, nd_experiment e," +
                    " nd_experiment_phenotype epheno, phenotype pheno" + 
                    " where ep.project_id = " + projectId +
                    " and e.nd_geolocation_id = " + locationId +
                    " and e.nd_experiment_id = ep.nd_experiment_id" +
			 		" and e.nd_experiment_id = epheno.nd_experiment_id" +
					" and epheno.phenotype_id = pheno.phenotype_id";
			SQLQuery statement = getSession().createSQLQuery(sql);
			statement.executeUpdate();
			
            this.flush();
            this.clear();

		} catch(HibernateException e) {
			logAndThrowException("Error in deletePhenotypesByProjectIdAndLocationId=" 
					+ projectId + ", " + locationId + " in PhenotypeDao: " + e.getMessage(), e);
		}
	}
	
	public int updatePhenotypesByProjectIdAndLocationId(Integer projectId, 
			Integer locationId,
			Integer stockId,
			Integer cvTermId,
			String value) throws MiddlewareQueryException {
		try {
			this.flush();
			
			// update the value of phenotypes
			String sql = "UPDATE nd_experiment_project ep " +
					"INNER JOIN nd_experiment exp ON ep.nd_experiment_id = exp.nd_experiment_id " +
					"INNER JOIN nd_experiment_stock expstock ON expstock.nd_experiment_id = exp.nd_experiment_id  " +
					"INNER JOIN stock ON expstock.stock_id = stock.stock_id " +
					"INNER JOIN nd_experiment_phenotype expp ON ep.nd_experiment_id = expp.nd_experiment_id  " +
					"INNER JOIN phenotype pheno ON expp.phenotype_id = pheno.phenotype_id " +
					"SET pheno.value = '" + value + "'" +
					" WHERE ep.project_id = " + projectId +
					" AND exp.nd_geolocation_id = " + locationId + 
					" AND exp.type_id = 1170 " +
					" AND stock.stock_id = " + stockId +
					" AND pheno.observable_id = " + cvTermId;
					
			SQLQuery statement = getSession().createSQLQuery(sql);
			int returnVal = statement.executeUpdate();
			
            this.flush();
            this.clear();
            
            return returnVal;

		} catch(HibernateException e) {
			logAndThrowException("Error in updatePhenotypesByProjectIdAndLocationId=" 
					+ projectId + ", " + locationId + " in PhenotypeDao: " + e.getMessage(), e);
			return 0;
		}
	}
	
	public int countRecordedVariatesOfStudy(Integer projectId, List<Integer> variateIds) throws MiddlewareQueryException {
	    try {
	        
	    	if (variateIds != null && !variateIds.isEmpty()) {
                StringBuilder sql = new StringBuilder();

                sql.append("SELECT COUNT(p.phenotype_id) FROM phenotype p ")
                .append("INNER JOIN nd_experiment_phenotype ep ON p.phenotype_id = ep.phenotype_id ")
                .append("INNER JOIN nd_experiment_project e ON e.nd_experiment_id = ep.nd_experiment_id ")
                .append("WHERE e.project_id = ").append(projectId).append(" AND p.observable_id IN (");
                for (int i = 0; i < variateIds.size(); i++) {
                	if (i > 0) {
                		sql.append(",");
                	}
                	sql.append(variateIds.get(i));
                }
                sql.append(") AND p.value <> ''");
             Query query = getSession().createSQLQuery(sql.toString());
        
                return ((BigInteger) query.uniqueResult()).intValue();
	    	}
	    } catch (HibernateException e) {
	            logAndThrowException(
	                    "Error at countPlantsSelectedOfNursery() query on PhenotypeDao: "
	                            + e.getMessage(), e);
	    }
	    return 0;
	}

	public List<Phenotype> getByTypeAndValue(int typeId, String value, boolean isEnumeration) throws MiddlewareQueryException {
		try {
			Criteria criteria = getSession().createCriteria(getPersistentClass());
			criteria.add(Restrictions.eq("observableId", typeId));
			if (isEnumeration) {
                criteria.add(Restrictions.eq("cValueId", Integer.parseInt(value)));
			}
			else {
				criteria.add(Restrictions.eq("value", value));
			}
			return criteria.list();
			
		} catch (HibernateException e) {
            logAndThrowException(
                    "Error in getByTypeAndValue("    + typeId + ", " + value + ") in PhenotypeDao: " + e.getMessage(), e);
		}
		return new ArrayList<Phenotype>();
	}
	
	public void deletePhenotypesInProjectByTerm(List<Integer> ids, int termId) throws MiddlewareQueryException {
		try {
			StringBuilder sql = new StringBuilder()
				.append("DELETE FROM phenotype ")
				.append(" WHERE phenotype_id IN ( ")
				.append(" SELECT eph.phenotype_id ")
				.append(" FROM nd_experiment_phenotype eph ")
				.append(" INNER JOIN nd_experiment_project ep ON ep.nd_experiment_id = eph.nd_experiment_id ")
				.append(" AND ep.project_id IN (");
			for (int i = 0; i < ids.size(); i++) {
				if (i > 0) {
					sql.append(",");
				}
				sql.append(ids.get(i));
			}
			sql.append(")) ").append(" AND observable_id = ").append(termId);

  			SQLQuery query = getSession().createSQLQuery(sql.toString());
  			Debug.println("DELETE PHENOTYPE ROWS FOR " + termId + " : " + query.executeUpdate());
				
		} catch (HibernateException e) {
            logAndThrowException(
                    "Error in deletePhenotypesInProjectByTerm("    + ids + ", " + termId + ") in PhenotypeDao: " + e.getMessage(), e);
		}
	}
	
	public Integer getPhenotypeIdByProjectAndType(int projectId, int typeId) throws MiddlewareQueryException {
		try {
			StringBuilder sql = new StringBuilder()
				.append(" SELECT p.phenotype_id ")
				.append(" FROM phenotype p ")
				.append(" INNER JOIN nd_experiment_phenotype eph ON eph.phenotype_id = p.phenotype_id ")
				.append(" INNER JOIN nd_experiment_project ep ON ep.nd_experiment_id = eph.nd_experiment_id ")
				.append("   AND ep.project_id = ").append(projectId)
				.append(" WHERE p.observable_id = ").append(typeId);
			SQLQuery query = getSession().createSQLQuery(sql.toString());
			List<Integer> list = query.list();
			if (list != null && !list.isEmpty()) {
				return list.get(0);
			}

		} catch (HibernateException e) {
            logAndThrowException(
                    "Error in getPhenotypeIdByProjectAndType("    + projectId + ", " + typeId + ") in PhenotypeDao: " + e.getMessage(), e);
		}
		return null;
	}
	
}
