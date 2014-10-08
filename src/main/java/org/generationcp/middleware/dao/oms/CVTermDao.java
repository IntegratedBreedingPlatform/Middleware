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
package org.generationcp.middleware.dao.oms;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.h2h.CategoricalTraitInfo;
import org.generationcp.middleware.domain.h2h.CategoricalValue;
import org.generationcp.middleware.domain.h2h.TraitInfo;
import org.generationcp.middleware.domain.oms.*;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;

import java.math.BigInteger;
import java.util.*;

/**
 * DAO class for {@link CVTerm}.
 * 
 */
@SuppressWarnings("unchecked")
public class CVTermDao extends GenericDAO<CVTerm, Integer> {

	
	public CVTerm getByCvIdAndDefinition(Integer cvId, String definition) throws MiddlewareQueryException {
		CVTerm term = null;
		
		try {
			Criteria criteria = getSession().createCriteria(getPersistentClass());
			criteria.add(Restrictions.eq("cvId", cvId));
			criteria.add(Restrictions.eq("definition", definition));
			criteria.add(Restrictions.eq("isObsolete", 0));
			
			term = (CVTerm) criteria.uniqueResult();
		
		} catch (HibernateException e) {
			logAndThrowException("Error at getByCvIdAndDefinition=" + cvId + ", " + definition + " query on CVTermDao: " + e.getMessage(), e);
		}
		
		return term;
	}
	
	public List<Integer> getTermsByNameOrSynonym(String nameOrSynonym, int cvId) throws MiddlewareQueryException {
		List<Integer> termIds = new ArrayList<Integer>();
		try {
			
			StringBuffer sqlString = new StringBuffer()
					.append("SELECT DISTINCT cvt.cvterm_id ")
					.append("FROM cvterm cvt ")
					.append("WHERE cvt.cv_id = :cvId and cvt.name = :nameOrSynonym ")
					.append("UNION ")
					.append("SELECT DISTINCT cvt.cvterm_id ")
					.append("FROM cvterm cvt INNER JOIN cvtermsynonym syn ON  syn.cvterm_id = cvt.cvterm_id ")
					.append("AND cvt.cv_id = :cvId AND syn.synonym = :nameOrSynonym ");
			    
				SQLQuery query = getSession().createSQLQuery(sqlString.toString());
				query.setParameter("cvId", cvId);
				query.setParameter("nameOrSynonym", nameOrSynonym);
						             
		        List<Object> results = (List<Object>) query.list();
		        for (Object row : results) {
		            termIds.add((Integer) row);
		        }
		        
		} catch(HibernateException e) {
			logAndThrowException("Error in getTermsByNameOrSynonym=" + nameOrSynonym + " in CVTermDao: " + e.getMessage(), e);
		}
		return termIds;
	}

	public Map<String, Set<Integer>> getTermsByNameOrSynonyms(List<String> nameOrSynonyms, int cvId) throws MiddlewareQueryException {
		 Map<String, Set<Integer>> stdVarMap = new HashMap<String, Set<Integer>> ();

		// Store the names in the map in uppercase
		for (int i = 0, size = nameOrSynonyms.size(); i < size; i++) {
			nameOrSynonyms.set(i, nameOrSynonyms.get(i).toUpperCase());
		}

		try {
			if (nameOrSynonyms.size() > 0) {

				StringBuffer sqlString = new StringBuffer()
				.append("SELECT DISTINCT cvt.name, syn.synonym, cvt.cvterm_id ")
				.append("FROM cvterm cvt, cvtermsynonym syn ")
				.append("WHERE cvt.cv_id = :cvId and cvt.name IN (:nameOrSynonyms) ")
				.append("UNION ")
				.append("SELECT DISTINCT cvt.name, syn.synonym, cvt.cvterm_id ")
				.append("FROM cvterm cvt INNER JOIN cvtermsynonym syn ON  syn.cvterm_id = cvt.cvterm_id ")
				.append("AND cvt.cv_id = :cvId AND syn.synonym IN (:nameOrSynonyms) ");

				SQLQuery query = getSession().createSQLQuery(sqlString.toString());
				query.setParameter("cvId", cvId);
				query.setParameterList("nameOrSynonyms", nameOrSynonyms);
				
		        List<Object[]> results = query.list();

	            for (Object[] row : results){
	            	String cvtermName = ((String) row[0]).trim().toUpperCase();
	            	String cvtermSynonym = ((String) row[1]).trim().toUpperCase();
	            	Integer cvtermId = (Integer) row[2];
	            
        			Set<Integer> stdVarIds = new HashSet<Integer>();
	            	if (nameOrSynonyms.contains(cvtermName)){
	            		if (stdVarMap.containsKey(cvtermName)){
	            			stdVarIds = stdVarMap.get(cvtermName);
	            		}
            			stdVarIds.add(cvtermId);
            			stdVarMap.put(cvtermName, stdVarIds);

	            	} else if (nameOrSynonyms.contains(cvtermSynonym)){
	            		if (stdVarMap.containsKey(cvtermSynonym)){
	            			stdVarIds = stdVarMap.get(cvtermSynonym);
	            		}
            			stdVarIds.add(cvtermId);
            			stdVarMap.put(cvtermSynonym, stdVarIds);
	            	} 
	            	
	            }

			}
			
		} catch(HibernateException e) {
			logAndThrowException("Error in getTermsByNameOrSynonyms=" + nameOrSynonyms + " in CVTermDao: " + e.getMessage(), e);
		}
		return stdVarMap;
	}
	
	public CVTerm getByNameAndCvId(String name, int cvId) throws MiddlewareQueryException {
        CVTerm term = null;
		
		try {

			StringBuffer sqlString = new StringBuffer()
					.append("SELECT DISTINCT cvt.cvterm_id, cvt.cv_id, cvt.name, cvt.definition ")
					.append(", cvt.dbxref_id, cvt.is_obsolete, cvt.is_relationshiptype  ")
					.append("FROM cvterm cvt ")
					.append("WHERE cvt.cv_id = :cvId and cvt.name = :nameOrSynonym ")
					.append("UNION ")
					.append("	SELECT DISTINCT cvt.cvterm_id, cvt.cv_id, cvt.name, cvt.definition ")
					.append(", cvt.dbxref_id, cvt.is_obsolete, cvt.is_relationshiptype  ")
					.append("FROM cvterm cvt INNER JOIN cvtermsynonym syn ON  syn.cvterm_id = cvt.cvterm_id ")
					.append("AND cvt.cv_id = :cvId AND syn.synonym = :nameOrSynonym ");
			
			SQLQuery query = getSession().createSQLQuery(sqlString.toString());
			query.setParameter("cvId", cvId);
			query.setParameter("nameOrSynonym", name);

	        List<Object[]> results = query.list();

	        if (results.size() > 0){
	        	Object[] row = results.get(0);
	        	Integer cvtermId = (Integer) row[0];
	        	Integer cvtermCvId = (Integer) row[1];
	        	String cvtermName = (String) row[2];
	        	String cvtermDefinition = (String) row[3];
	        	Integer dbxrefId = (Integer) row[4];
	        	Integer isObsolete = (Integer) row[5];
	        	Integer isRelationshipType = (Integer) row[6];
	        	
	        	term = new CVTerm(cvtermId, cvtermCvId, cvtermName, cvtermDefinition, dbxrefId, isObsolete, isRelationshipType);
	        }
	        

		} catch (HibernateException e) {
			logAndThrowException("Error at getByNameAndCvId=" + name + ", " + cvId + " query on CVTermDao: " + e.getMessage(), e);
		}
		
		return term;
	}
	
	public List<CVTerm> getByIds(List<Integer> ids) throws MiddlewareQueryException {
		List<CVTerm> terms = new ArrayList<CVTerm>();
		
		if (ids != null && ids.size() > 0) {
			try {
				Criteria criteria = getSession().createCriteria(getPersistentClass());
				criteria.add(Restrictions.in("cvTermId", ids));
				
				terms = criteria.list();

			} catch(HibernateException e) {
				logAndThrowException("Error at GetByIds=" + ids + " query on CVTermDao: " + e.getMessage(), e);
			}
		}
		
		return terms;
	}
	
	public List<CVTerm> getValidCvTermsByIds(List<Integer> ids, int storedInId, int dataTypeId) throws MiddlewareQueryException {
        List<CVTerm> terms = new ArrayList<CVTerm>();
        
        if (ids != null && ids.size() > 0) {
            try {
                StringBuffer queryString = new StringBuffer()
                .append("SELECT cvt.cvterm_id, cvt.name, cvt.definition ")
                .append("FROM cvterm cvt ") 
                .append("INNER JOIN cvterm_relationship datatype ON datatype.subject_id = cvt.cvterm_id ")
                .append(" AND datatype.type_id = ").append(TermId.HAS_TYPE.getId())
                .append(" INNER JOIN cvterm_relationship stored_in ON datatype.subject_id = stored_in.subject_id ") 
                .append(" AND stored_in.type_id = ").append(TermId.STORED_IN.getId())
                .append(" WHERE cvt.cvterm_id in (:ids)")
                .append(" AND (stored_in.object_id <> :storedIn OR (stored_in.object_id = :storedIn ")
                .append(" AND datatype.object_id = :datatype))");
                
                SQLQuery query = getSession().createSQLQuery(queryString.toString());
                query.setParameterList("ids", ids);
                query.setParameter("storedIn", storedInId);
                query.setParameter("datatype", dataTypeId);
                
                List<Object[]> list =  query.list();
                
                for (Object[] row : list){
                    Integer id = (Integer) row[0]; 
                    String name = (String) row [1];
                    String definition = (String) row[2]; 
                    
                    CVTerm cvTerm = new CVTerm();
                    cvTerm.setCvTermId(id);
                    cvTerm.setName(name);
                    cvTerm.setDefinition(definition);
                    terms.add(cvTerm);
                }
            } catch (HibernateException e) {
                logAndThrowException("Error at getValidCvTermsByIds=" + ids + " query on CVTermDao: " + e.getMessage(), e);                
            }
        }
        return terms;
	}
	
	public List<CVTerm> getVariablesByType(List<Integer> types, Integer storedIn) throws MiddlewareQueryException {
        List<CVTerm> terms = new ArrayList<CVTerm>();
        
        try {
            StringBuffer queryString = new StringBuffer()
                .append("SELECT cvt.cvterm_id, cvt.name, cvt.definition ")
                .append("FROM cvterm cvt ") 
                .append("INNER JOIN cvterm_relationship cvr ON cvr.subject_id = cvt.cvterm_id ") 
                .append("             AND cvr.type_id = 1105 AND cvr.object_id IN (:types) ")
                    ;
            if (storedIn != null) {
                queryString.append("INNER JOIN cvterm_relationship stored_in ON cvr.subject_id = stored_in.subject_id ") 
                    .append("AND stored_in.type_id = 1044 AND stored_in.object_id = :storedIn ");
            }

            SQLQuery query = getSession().createSQLQuery(queryString.toString());
            query.setParameterList("types", types);
            if (storedIn != null) {
                query.setParameter("storedIn", storedIn);
            }
            
            List<Object[]> list =  query.list();
            
            for (Object[] row : list){
                Integer id = (Integer) row[0]; 
                String name = (String) row [1];
                String definition = (String) row[2]; 
                
                CVTerm cvTerm = new CVTerm();
                cvTerm.setCvTermId(id);
                cvTerm.setName(name);
                cvTerm.setDefinition(definition);
                terms.add(cvTerm);
            }
        } catch(HibernateException e) {
            logAndThrowException("Error at getVariablesByType=" + types + " query on CVTermDao: " + e.getMessage(), e);
        }

        return terms;	    
	}
	
    public List<CategoricalTraitInfo>  setCategoricalVariables(List<CategoricalTraitInfo> traitInfoList) throws MiddlewareQueryException {
      List<CategoricalTraitInfo> categoricalTraitInfoList = new ArrayList<CategoricalTraitInfo>();
      
      // Get trait IDs
      List<Integer> traitIds = new ArrayList<Integer>();
      for (CategoricalTraitInfo trait : traitInfoList) {
          traitIds.add(trait.getId());
      }
    
      try {
          SQLQuery query = getSession().createSQLQuery(
                  "SELECT cvt_categorical.cvterm_id, cvt_categorical.name, cvt_categorical.definition, cvr_value.object_id, cvt_value.name "
                  + "FROM cvterm_relationship cvr_categorical  "
                  + "INNER JOIN cvterm cvt_categorical ON cvr_categorical.subject_id = cvt_categorical.cvterm_id "
                  + "INNER JOIN cvterm_relationship cvr_stored_in ON cvr_categorical.subject_id = cvr_stored_in.subject_id "
                  + "INNER JOIN cvterm_relationship cvr_value ON cvr_stored_in.subject_id = cvr_value.subject_id and cvr_value.type_id = 1190 "
                  + "INNER JOIN cvterm cvt_value ON cvr_value.object_id = cvt_value.cvterm_id "
                  + "WHERE cvr_categorical.type_id = 1105 AND cvr_categorical.object_id = 1130 "
                  + "    AND cvr_stored_in.type_id = 1044 AND cvr_stored_in.object_id = 1048 "
                  + "    AND cvt_categorical.cvterm_id in (:traitIds) "
                  );
          query.setParameterList("traitIds", traitIds);

          List<Object[]> list = query.list();
    
          Map<Integer, String> valueIdName = new HashMap<Integer, String>();
          for (Object[] row : list) {
              Integer variableId = (Integer) row[0];
              String variableName = (String) row[1];
              String variableDescription = (String) row[2];
              Integer valueId = (Integer) row[3];
              String valueName = (String) row[4];
              
              valueIdName.put(valueId, valueName);
    
              for (CategoricalTraitInfo traitInfo : traitInfoList){
                  if (traitInfo.getId() == variableId){
                      traitInfo.setName(variableName);
                      traitInfo.setDescription(variableDescription);
                      traitInfo.addValue(new CategoricalValue(valueId, valueName));
                      break;                        
                  }
              }
          }
          
          // Remove non-categorical variable from the list
          for (CategoricalTraitInfo traitInfo : traitInfoList){
              if (traitInfo.getName() != null){
                  categoricalTraitInfoList.add(traitInfo);
              }
          }
          
          // This step was added since the valueName is not retrieved correctly with the above query in Java. 
          // Most probably because of the two cvterm id-name present in the query.
          // The steps that follow will just retrieve the name of the categorical values in each variable.
          
          List<Integer> valueIds = new ArrayList<Integer>();
          valueIds.addAll(valueIdName.keySet());
          
          if (valueIds != null && valueIds.size() != 0) {
	          query = getSession().createSQLQuery(
	                  "SELECT cvterm_id, cvterm.name " +
	                  "FROM cvterm " +
	                  "WHERE cvterm_id IN (:ids) " 
	                  );
	          query.setParameterList("ids", valueIds);
	          
	          list = query.list();
          }
    
          for (Object[] row : list) {
              Integer variableId = (Integer) row[0];
              String variableName = (String) row[1];
              
              valueIdName.put(variableId, variableName);
          }
          
          for (CategoricalTraitInfo traitInfo : categoricalTraitInfoList){
              List<CategoricalValue> values = traitInfo.getValues();
              for (CategoricalValue value : values){
                  String name = valueIdName.get(value.getId());
                  value.setName(name);
              }
              traitInfo.setValues(values);    
          }
          
          
      } catch (HibernateException e) {
          logAndThrowException(
                  "Error at setCategoricalVariables() query on CVTermDao: " + e.getMessage(), e);
      }
    
      return categoricalTraitInfoList;
    }

    public List<TraitInfo> getTraitInfo(List<Integer> traitIds) throws MiddlewareQueryException {
        List<TraitInfo> traits = new ArrayList<TraitInfo>();
        
        try{
        
            StringBuilder sql = new StringBuilder()
            .append("SELECT cvt.cvterm_id, cvt.name, cvt.definition,  c_scale.scaleName, cr_type.object_id ")
            .append("FROM cvterm cvt ") 
            .append("	INNER JOIN cvterm_relationship cr_scale ON cvt.cvterm_id = cr_scale.subject_id ")
            .append("   INNER JOIN (SELECT cvterm_id, name AS scaleName FROM cvterm) c_scale ON c_scale.cvterm_id = cr_scale.object_id ") 
            .append("        AND cr_scale.type_id = ").append(TermId.HAS_SCALE.getId()).append(" ")
            .append("	INNER JOIN cvterm_relationship cr_type ON cr_type.subject_id = cr_scale.subject_id ")
            .append("		AND cr_type.type_id = ").append(TermId.HAS_TYPE.getId()).append(" ")
            .append("WHERE cvt.cvterm_id in (:traitIds) ")
            ;
            
            SQLQuery query = getSession().createSQLQuery(sql.toString());
            query.setParameterList("traitIds", traitIds);
            
            List<Object[]> list = query.list();
      
            for (Object[] row : list) {
                Integer id = (Integer) row[0];
                String name = (String) row[1];
                String description = (String) row[2];
                String scaleName = (String) row[3];
                Integer typeId = (Integer) row [4];
                		
                traits.add(new TraitInfo(id, name, description, scaleName, typeId));
                
            }

        } catch (HibernateException e) {
            logAndThrowException(
                    "Error at getTraitInfo() query on CVTermDao: " + e.getMessage(), e);
        }
        return traits;
    }
	
	public Integer getStandadardVariableIdByPropertyScaleMethod(Integer propertyId, Integer scaleId, Integer methodId, String sortOrder)
			throws MiddlewareQueryException {
		try {
			StringBuilder queryString = new StringBuilder();
			queryString.append("SELECT DISTINCT cvr.subject_id ");
			queryString.append("FROM cvterm_relationship cvr ");
			queryString.append("INNER JOIN cvterm_relationship cvrp ON cvr.subject_id = cvrp.subject_id AND cvrp.type_id = 1200 ");
			queryString.append("INNER JOIN cvterm_relationship cvrs ON cvr.subject_id = cvrs.subject_id AND cvrs.type_id = 1220 ");
			queryString.append("INNER JOIN cvterm_relationship cvrm ON cvr.subject_id = cvrm.subject_id AND cvrm.type_id = 1210 ");
			queryString.append("WHERE cvrp.object_id = :propertyId AND cvrs.object_id = :scaleId AND cvrm.object_id = :methodId ");
			queryString.append("ORDER BY cvr.subject_id ").append(sortOrder).append(" LIMIT 0,1");
			
			SQLQuery query = getSession().createSQLQuery(queryString.toString());
			query.setParameter("propertyId", propertyId);
			query.setParameter("scaleId", scaleId);
			query.setParameter("methodId", methodId);
			
			Integer id = (Integer) query.uniqueResult();
						
			return id;
						
		} catch(HibernateException e) {
			logAndThrowException("Error at getStandadardVariableIdByPropertyScaleMethod :" + e.getMessage(), e);
		}
		return null;	
	
	}
	
	public List<Integer> getStandardVariableIdsByPhenotypicType(PhenotypicType type) 
			throws MiddlewareQueryException {
		try {
			//Standard variable has the combination of property-scale-method
			StringBuilder queryString = new StringBuilder();
			queryString.append("SELECT DISTINCT cvr.subject_id ");
			queryString.append("FROM cvterm_relationship cvr ");
			queryString.append("INNER JOIN cvterm_relationship cvrp ON cvr.subject_id = cvrp.subject_id AND cvrp.type_id = 1200 ");
			queryString.append("INNER JOIN cvterm_relationship cvrs ON cvr.subject_id = cvrs.subject_id AND cvrs.type_id = 1220 ");
			queryString.append("INNER JOIN cvterm_relationship cvrm ON cvr.subject_id = cvrm.subject_id AND cvrm.type_id = 1210 ");
			queryString.append("INNER JOIN cvterm_relationship storedIn ON cvr.subject_id = storedIn.subject_id AND storedIn.type_id = 1044 ");
			queryString.append("INNER JOIN cvterm term ON cvr.subject_id = term.cvterm_id ");
			queryString.append("WHERE storedIn.object_id IN (:type) ORDER BY term.name");
			
			SQLQuery query = getSession().createSQLQuery(queryString.toString());
			query.setParameterList("type", type.getTypeStorages());
			
			return (List<Integer>) query.list();
						
		} catch(HibernateException e) {
			logAndThrowException("Error at getStandardVariableIdsByPhenotypicType :" + e.getMessage(), e);
		}
		return null;
	}
	
	public List<CVTerm> getTermsByCvId(CvId cvId,int start,int numOfRows) throws MiddlewareQueryException{
		List<CVTerm> terms = new ArrayList<CVTerm>();
		
        try{
            
            SQLQuery query = getSession().createSQLQuery(
                    "SELECT cvterm_id, name, definition, dbxref_id, is_obsolete, is_relationshiptype "
                    + "FROM cvterm " 
                    + "WHERE cv_id = :cvId "
                    + "ORDER BY cvterm_id, name "
                    );
            query.setParameter("cvId", cvId.getId());
            setStartAndNumOfRows(query, start, numOfRows);
			List<Object[]> list = query.list();
            for (Object[] row : list) {
            	Integer termId = (Integer) row[0];
            	String name = (String) row[1];
            	String definition = (String) row[2];
            	Integer dbxrefId = (Integer) row[3];
            	Integer isObsolete = (Integer) row[4];
            	Integer isRelationshipType = (Integer) row[5];
            	
            	terms.add(new CVTerm(termId, cvId.getId(), name, definition, dbxrefId, isObsolete, isRelationshipType));
            	
            }
            
        } catch (HibernateException e) {
            logAndThrowException("Error at getTermsByCvId() query on CVTermDao: " + e.getMessage(), e);
        }
        
        return terms;
	}

	public long countTermsByCvId(CvId cvId) throws MiddlewareQueryException{
		
        try{
            
            SQLQuery query = getSession().createSQLQuery(
                    "SELECT COUNT(cvterm_id) "
                    + "FROM cvterm " 
                    + "WHERE cv_id = :cvId "
                    );
            query.setParameter("cvId", cvId.getId());
            
            return ((BigInteger) query.uniqueResult()).longValue();
      
        } catch (HibernateException e) {
            logAndThrowException("Error at countTermsByCvId() query on CVTermDao: " + e.getMessage(), e);
        }
        
        return 0;
	}
	
	public List<Integer> findMethodTermIdsByTrait(Integer traitId) 
			throws MiddlewareQueryException {
		try {
			//Standard variable has the combination of property-scale-method
			StringBuilder queryString = new StringBuilder();
			queryString.append("SELECT DISTINCT cvrm.object_id ");
			queryString.append("FROM cvterm_relationship cvr ");
			queryString.append("INNER JOIN cvterm_relationship cvrp ON cvr.subject_id = cvrp.subject_id AND cvrp.type_id = 1200 ");
			queryString.append("INNER JOIN cvterm_relationship cvrs ON cvr.subject_id = cvrs.subject_id AND cvrs.type_id = 1220 ");
			queryString.append("INNER JOIN cvterm_relationship cvrm ON cvr.subject_id = cvrm.subject_id AND cvrm.type_id = 1210 "); 
			queryString.append("WHERE cvrp.object_id = :traitId");
			
			SQLQuery query = getSession().createSQLQuery(queryString.toString());
			query.setInteger("traitId", traitId);
			
			return  (List<Integer>) query.list();
						
		} catch(HibernateException e) {
			logAndThrowException("Error at findMethodTermIdsByTrait :" + e.getMessage(), e);
		}
		return null;
	}
	
	public List<Integer> findScaleTermIdsByTrait(Integer traitId) 
			throws MiddlewareQueryException {
		try {
			//Standard variable has the combination of property-scale-method
			StringBuilder queryString = new StringBuilder();
			queryString.append("SELECT DISTINCT cvrs.object_id ");
			queryString.append("FROM cvterm_relationship cvr ");
			queryString.append("INNER JOIN cvterm_relationship cvrp ON cvr.subject_id = cvrp.subject_id AND cvrp.type_id = 1200 ");
			queryString.append("INNER JOIN cvterm_relationship cvrs ON cvr.subject_id = cvrs.subject_id AND cvrs.type_id = 1220 ");
			queryString.append("INNER JOIN cvterm_relationship cvrm ON cvr.subject_id = cvrm.subject_id AND cvrm.type_id = 1210 "); 
			queryString.append("WHERE cvrp.object_id = :traitId");
			
			SQLQuery query = getSession().createSQLQuery(queryString.toString());
			query.setInteger("traitId", traitId);
			
			return (List<Integer>) query.list();
						
		} catch(HibernateException e) {
			logAndThrowException("Error at findScaleTermIdsByTrait :" + e.getMessage(), e);
		}
		return null;
	}

	/**
	 * Returns standard variables associated to the given list of trait names or synonyms
	 * 
	 * @param propertyNameOrSynonyms
	 * @return Map of name-standard variable ids of the given trait name or synonyms
	 * @throws MiddlewareQueryException
	 */
	public Map<String, Set<Integer>> getStandardVariableIdsByProperties(List<String> propertyNameOrSynonyms) throws MiddlewareQueryException {
		Map<String, Set<Integer>> stdVarMap = new HashMap<String, Set<Integer>> ();
		
		// Store the names in the map in uppercase
		for (int i = 0, size = propertyNameOrSynonyms.size(); i < size; i++) {
			propertyNameOrSynonyms.set(i, propertyNameOrSynonyms.get(i).toUpperCase());
		}

		try {
			if (propertyNameOrSynonyms.size() > 0) {
				
				StringBuffer sqlString = new StringBuffer()
						.append("SELECT DISTINCT cvtr.name, syn.synonym, cvt.cvterm_id ")
						.append("FROM cvterm_relationship cvr ")
						.append("INNER JOIN cvterm cvtr ON cvr.object_id = cvtr.cvterm_id AND cvr.type_id = 1200 ")
						.append("INNER JOIN cvterm cvt ON cvr.subject_id = cvt.cvterm_id AND cvt.cv_id = 1040 ")
						.append(", cvtermsynonym syn ")
						.append("WHERE (cvtr.cvterm_id = syn.cvterm_id AND syn.synonym IN (:propertyNameOrSynonyms) ")
						.append("OR cvtr.name IN (:propertyNameOrSynonyms)) ");
					
				SQLQuery query = getSession().createSQLQuery(sqlString.toString());
				query.setParameterList("propertyNameOrSynonyms", propertyNameOrSynonyms);
				
		        List<Object[]> results = query.list();

	            for (Object[] row : results){
	            	String cvtermName = ((String) row[0]).trim().toUpperCase();
	            	String cvtermSynonym = ((String) row[1]).trim().toUpperCase();
	            	Integer cvtermId = (Integer) row[2];
	            
       			Set<Integer> stdVarIds = new HashSet<Integer>();
            	if (propertyNameOrSynonyms.contains(cvtermName)){
	            		if (stdVarMap.containsKey(cvtermName)){
	            			stdVarIds = stdVarMap.get(cvtermName);
	            		}
           			stdVarIds.add(cvtermId);
           			stdVarMap.put(cvtermName, stdVarIds);

	            	} 
	            	if (propertyNameOrSynonyms.contains(cvtermSynonym)){
	            		if (stdVarMap.containsKey(cvtermSynonym)){
	            			stdVarIds = stdVarMap.get(cvtermSynonym);
	            		}
           			stdVarIds.add(cvtermId);
           			stdVarMap.put(cvtermSynonym, stdVarIds);
	            	} 
	            	
	            }

			}
			
		} catch(HibernateException e) {
			logAndThrowException("Error in getStandardVariableIdsByProperties=" + propertyNameOrSynonyms + " in CVTermDao: " + e.getMessage(), e);
		}
		
		return stdVarMap;
		
	}
	
	public List<CVTerm> getIsAOfTermsByCvId(CvId cvId,int start,int numOfRows) throws MiddlewareQueryException{
		List<CVTerm> terms = new ArrayList<CVTerm>();
		
        try{
            
            SQLQuery query = getSession().createSQLQuery(
                    "SELECT DISTINCT isA.cvterm_id, isA.name, isA.definition, isA.dbxref_id, isA.is_obsolete, isA.is_relationshiptype "
                    + "FROM cvterm isA, cvterm_relationship rel, cvterm subj " 
                    + "WHERE subj.cv_id = :cvId "
                    + "AND subj.cvterm_id = rel.subject_id "
                    + "AND rel.object_id = isA.cvterm_id "
                    + "AND rel.type_id = "+TermId.IS_A.getId()+" "
                    + "ORDER BY isA.name, isA.cvterm_id "
                    );
            query.setParameter("cvId", cvId.getId());
            setStartAndNumOfRows(query, start, numOfRows);
			List<Object[]> list = query.list();
            for (Object[] row : list) {
            	Integer termId = (Integer) row[0];
            	String name = (String) row[1];
            	String definition = (String) row[2];
            	Integer dbxrefId = (Integer) row[3];
            	Integer isObsolete = (Integer) row[4];
            	Integer isRelationshipType = (Integer) row[5];
            	
            	terms.add(new CVTerm(termId, cvId.getId(), name, definition, dbxrefId, isObsolete, isRelationshipType));
            	
            }
            
        } catch (HibernateException e) {
            logAndThrowException("Error at getTermsByCvId() query on CVTermDao: " + e.getMessage(), e);
        }
        
        return terms;
	}

	public long countIsAOfTermsByCvId(CvId cvId) throws MiddlewareQueryException{
		
        try{
            
            SQLQuery query = getSession().createSQLQuery(
                    "SELECT COUNT(DISTINCT isA.cvterm_id) "
                     + "FROM cvterm isA, cvterm_relationship rel, cvterm subj " 
                     + "WHERE subj.cv_id = :cvId "
                     + "AND subj.cvterm_id = rel.subject_id "
                     + "AND rel.object_id = isA.cvterm_id "
                     + "AND rel.type_id = "+TermId.IS_A.getId()+" "
                    );
            query.setParameter("cvId", cvId.getId());
            
            return ((BigInteger) query.uniqueResult()).longValue();
      
        } catch (HibernateException e) {
            logAndThrowException("Error at countTermsByCvId() query on CVTermDao: " + e.getMessage(), e);
        }
        
        return 0;
	}
	
	public CVTerm getTermOfProperty(int termId, int cvId) throws MiddlewareQueryException {
		CVTerm term = null;
		
		try {
			StringBuffer sqlString = new StringBuffer()
			.append("SELECT * ")
			.append("FROM cvterm ")
			.append("WHERE cv_id = :cvId AND cvterm_id = :termId");
		
			SQLQuery query = getSession().createSQLQuery(sqlString.toString());
			query.setParameter("termId", termId);
			query.setParameter("cvId", cvId);

	        List<Object[]> results = query.list();

	        if (results.size() > 0){
	        	Object[] row = results.get(0);
	        	Integer cvtermId = (Integer) row[0];
	        	Integer cvtermCvId = (Integer) row[1];
	        	String cvtermName = (String) row[2];
	        	String cvtermDefinition = (String) row[3];
	        	Integer dbxrefId = (Integer) row[4];
	        	Integer isObsolete = (Integer) row[5];
	        	Integer isRelationshipType = (Integer) row[6];
	        	
	        	term = new CVTerm(cvtermId, cvtermCvId, cvtermName, cvtermDefinition, dbxrefId, isObsolete, isRelationshipType);
	        }

		} catch (HibernateException e) {
			logAndThrowException("Error at getTermOfProperty=" + termId + " query on CVTermDao: " + e.getMessage(), e);
		}
		
		return term;
	}
	
	public CVTerm getTermOfClassOfProperty(int termId, int cvId, int isATermId) throws MiddlewareQueryException {
		CVTerm term = null;
		
		try {
			StringBuffer sqlString = new StringBuffer()
			.append("SELECT cvt.* ")
			.append("FROM cvterm cvt ")
			.append("INNER JOIN cvterm_relationship cvr on cvr.object_id = cvt.cvterm_id ")
			.append("INNER JOIN cvterm v on cvr.subject_id = v.cvterm_id ")
			.append("WHERE cvr.type_id = :isAtermId AND v.cv_id = :cvId AND v.cvterm_id = :termId");

			SQLQuery query = getSession().createSQLQuery(sqlString.toString());
			query.setParameter("termId", termId);
			query.setParameter("isAtermId", isATermId);
			query.setParameter("cvId", cvId);


	        List<Object[]> results = query.list();

	        if (results.size() > 0){
	        	Object[] row = results.get(0);
	        	Integer cvtermId = (Integer) row[0];
	        	Integer cvtermCvId = (Integer) row[1];
	        	String cvtermName = (String) row[2];
	        	String cvtermDefinition = (String) row[3];
	        	Integer dbxrefId = (Integer) row[4];
	        	Integer isObsolete = (Integer) row[5];
	        	Integer isRelationshipType = (Integer) row[6];
	        	
	        	term = new CVTerm(cvtermId, cvtermCvId, cvtermName, cvtermDefinition, dbxrefId, isObsolete, isRelationshipType);
	        }

		} catch (HibernateException e) {
			logAndThrowException("Error at getTermOfClassOfProperty=" + termId + " query on CVTermDao: " + e.getMessage(), e);
		}
		
		return term;
	}

    /**
     * Returns the entries in cvterm of all trait classes (with subject_id entry in cvterm_relationship where object_id = classType and type_id = 1225)
     */
    public List<TraitClassReference> getTraitClasses(TermId classType) throws MiddlewareQueryException{
        
        List<TraitClassReference> traitClasses = new ArrayList<TraitClassReference>();
        
        try {
            /*
             SELECT cvterm_id, name, definition 
             FROM cvterm cvt JOIN cvterm_relationship cvr 
                 ON cvt.cvterm_id = cvr.subject_id AND cvr.type_id = 1225 AND cvr.object_id = 1330;  -- 1330 for Ontology Trait Class, 1045 for Ontology Research Class
            */ 

            StringBuffer sqlString = new StringBuffer()
                .append("SELECT cvterm_id, name, definition ")
                .append("FROM cvterm cvt JOIN cvterm_relationship cvr ")
                    .append("ON cvt.cvterm_id = cvr.subject_id AND cvr.type_id = ").append(TermId.IS_A.getId())
                    .append(" AND cvr.object_id = ").append(classType.getId()).append(" ")
                    .append(" AND cvt.cv_id = ").append(CvId.IBDB_TERMS.getId());
            
            SQLQuery query = getSession().createSQLQuery(sqlString.toString());
            
            List<Object[]> list = query.list();
            
            for (Object[] row : list) {
                Integer cvtermId = (Integer) row[0];
                String cvtermName = (String) row[1];
                String cvtermDefinition = (String) row[2];
                
                traitClasses.add(new TraitClassReference(cvtermId, cvtermName, cvtermDefinition, classType.getId()));
            }

        } catch (HibernateException e) {
            logAndThrowException("Error at getTraitClasses() query on CVTermDao: " + e.getMessage(), e);
        }

        return traitClasses;
        
    }
    
    /**
     * Retrieves all the trait classes (id, name, definition, parent trait class)
     *
     * @return List of trait class references
     */
    public List<TraitClassReference> getAllTraitClasses() throws MiddlewareQueryException{
        List<TraitClassReference> traitClasses = new ArrayList<TraitClassReference>();
        
        try {
            StringBuffer sqlString = new StringBuffer()
                .append("SELECT cvterm_id, name, definition, cvr.object_id ")
                .append("FROM cvterm cvt JOIN cvterm_relationship cvr ")
                .append("ON cvt.cvterm_id = cvr.subject_id AND cvr.type_id = ").append(TermId.IS_A.getId()).append(" ")
                .append("WHERE cv_id = 1000 AND object_id NOT IN (1000, 1002, 1003)  ")
                .append("ORDER BY cvr.object_id ")
                ;
            
            
            SQLQuery query = getSession().createSQLQuery(sqlString.toString());

            List<Object[]> list = query.list();

            for (Object[] row : list) {
                Integer id = (Integer) row[0];
                String name = (String) row[1];
                String definition = (String) row[2];
                Integer isAId = (Integer) row[3];

                traitClasses.add(new TraitClassReference(id, name, definition, isAId));
            }

        } catch (HibernateException e) {
            logAndThrowException("Error at getAllTraitClasses() query on CVTermDao: " + e.getMessage(), e);
        }
        
        return traitClasses;

    }
    
    /**
     * Retrieves the properties of a Trait Class
     */
    public List<PropertyReference> getPropertiesOfTraitClass(Integer traitClassId) throws MiddlewareQueryException{
        List<Integer> traitClasses = new ArrayList<Integer>();
        traitClasses.add(traitClassId);
        return getPropertiesOfTraitClasses(traitClasses).get(traitClassId);         
    }

    
    /**
     * Retrieves the properties of Trait Classes
     */
    public Map<Integer, List<PropertyReference>> getPropertiesOfTraitClasses(List<Integer> traitClassIds) throws MiddlewareQueryException{

        Map<Integer, List<PropertyReference>> propertiesOfTraitClasses = new HashMap<Integer, List<PropertyReference>>();
        
        if (traitClassIds.isEmpty()){
            return propertiesOfTraitClasses;
        }

        Collections.sort(traitClassIds);   
        
        try {

            StringBuffer sqlString = new StringBuffer()
                .append("SELECT cvterm_id, name, definition, cvr.object_id ")
                .append("FROM cvterm cvt JOIN cvterm_relationship cvr ")
                    .append("ON cvt.cvterm_id = cvr.subject_id AND cvr.type_id = ").append(TermId.IS_A.getId()).append(" ")
                    .append(" AND cvr.object_id  IN (:traitClassIds) ")
                .append("WHERE cv_id =  ").append(CvId.PROPERTIES.getId()).append(" ")
                .append("ORDER BY cvr.object_id ")
                ;
            
            SQLQuery query = getSession().createSQLQuery(sqlString.toString());
            query.setParameterList("traitClassIds", traitClassIds);
            
            List<Object[]> list = query.list();
            
            List<PropertyReference> properties = new ArrayList<PropertyReference>();
            Integer prevTraitClassId = traitClassIds.get(0);

            for (Object[] row : list) {
                Integer cvtermId = (Integer) row[0];
                String cvtermName = (String) row[1];
                String cvtermDefinition = (String) row[2];
                Integer traitClassId = (Integer) row[3];

                if (!prevTraitClassId.equals(traitClassId)){
                    propertiesOfTraitClasses.put(prevTraitClassId,  properties);
                    properties = new ArrayList<PropertyReference>();
                    prevTraitClassId = traitClassId;
                }
                properties.add(new PropertyReference(cvtermId, cvtermName, cvtermDefinition));
            }
            
            propertiesOfTraitClasses.put(prevTraitClassId,  properties);

        } catch (HibernateException e) {
            logAndThrowException("Error at getTraitClassProperties() query on CVTermDao: " + e.getMessage(), e);
        }
        
        return propertiesOfTraitClasses;
    }
    
    
    /**
     * Retrieves the standard variables of a property
     */
    public List<StandardVariableReference> getStandardVariablesOfProperty(Integer propertyId) throws MiddlewareQueryException{
        List<Integer> properties = new ArrayList<Integer>();
        properties.add(propertyId);
        return getStandardVariablesOfProperties(properties).get(propertyId);         
    }


    /**
     * Retrieves the standard variables of trait properties
     */
    public Map<Integer, List<StandardVariableReference>> getStandardVariablesOfProperties(List<Integer> propertyIds) throws MiddlewareQueryException{
        Map<Integer, List<StandardVariableReference>> variablesOfProperties = new HashMap<Integer, List<StandardVariableReference>>();
        
        if (propertyIds.isEmpty()){
            return variablesOfProperties;
        }
        
        Collections.sort(propertyIds);   
        
        try {
            StringBuffer sqlString = new StringBuffer()
                .append("SELECT cvterm_id, name, definition, cvr.object_id ")
                .append("FROM cvterm cvt JOIN cvterm_relationship cvr ")
                    .append("ON cvt.cvterm_id = cvr.subject_id AND cvr.type_id = ").append(TermId.HAS_PROPERTY.getId())
                    .append(" AND cvr.object_id  IN (:propertyIds) ")
                .append("ORDER BY cvr.object_id ")
                ;
            
            SQLQuery query = getSession().createSQLQuery(sqlString.toString());
            query.setParameterList("propertyIds", propertyIds);
            
            List<Object[]> list = query.list();
            
            List<StandardVariableReference> variables = new ArrayList<StandardVariableReference>();
            Integer prevPropertyId = propertyIds.get(0);

            for (Object[] row : list) {
                Integer cvtermId = (Integer) row[0];
                String cvtermName = (String) row[1];
                String cvtermDefinition = (String) row[2];
                Integer traitClassId = (Integer) row[3];

                if (!prevPropertyId.equals(traitClassId)){
                    variablesOfProperties.put(prevPropertyId,  variables);
                    variables = new ArrayList<StandardVariableReference>();
                    prevPropertyId = traitClassId;
                }
                variables.add(new StandardVariableReference(cvtermId, cvtermName, cvtermDefinition));
            }
            
            variablesOfProperties.put(prevPropertyId,  variables);

        } catch (HibernateException e) {
            logAndThrowException("Error at getStandardVariablesOfProperties() query on CVTermDao: " + e.getMessage(), e);
        }
        
        return variablesOfProperties;
    }

    /*
     * Retrieves the standard variable linked to an ontology
     * */
    public Integer getStandardVariableIdByTermId(int cvTermId, TermId termId) throws MiddlewareQueryException {
        try {
            StringBuilder queryString = new StringBuilder();
            queryString.append("SELECT DISTINCT cvr.subject_id ");
            queryString.append("FROM cvterm_relationship cvr ");
            queryString.append("INNER JOIN cvterm_relationship cvrt ON cvr.subject_id = cvrt.subject_id AND cvrt.type_id = :typeId ");
            queryString.append("WHERE cvr.object_id = :cvTermId ");
            queryString.append("ORDER BY cvr.subject_id ").append(" LIMIT 0,1");

            SQLQuery query = getSession().createSQLQuery(queryString.toString());
            query.setParameter("typeId", termId.getId());
            query.setParameter("cvTermId", cvTermId);
            
            Integer id = (Integer) query.uniqueResult();
                                    
            return id;
                                    
        } catch(HibernateException e) {
                logAndThrowException("Error at getStandadardVariableIdByTermId :" + e.getMessage(), e);
        }
        return null;
    }
    
    /**
     * Gets the all standard variables based on the parameters with values. 
     * At least one parameter needs to have a value.
     * If a standard variable has no trait class, it is not included in the result.
     * 
     * @param traitClassId
     * @param propertyId
     * @param methodId
     * @param scaleId
     * @return List of standard variable ids
     * @throws MiddlewareQueryException
     */
    public List<Integer> getStandardVariableIds(Integer traitClassId, Integer propertyId, Integer methodId, Integer scaleId) throws MiddlewareQueryException {
        List<Integer> standardVariableIds = new ArrayList<Integer>();
        try {
            StringBuilder queryString = new StringBuilder()
                .append("SELECT DISTINCT cvr.subject_id ")
                .append("FROM cvterm_relationship cvr ");
            
            if (traitClassId != null){
                // Trait class via 'IS A' of property
                queryString.append("INNER JOIN cvterm_relationship cvrpt ON cvr.subject_id = cvrpt.subject_id ");
                queryString.append("    AND cvrpt.type_id = ").append(TermId.HAS_PROPERTY.getId()).append(" ");
                queryString.append("INNER JOIN cvterm_relationship cvrt ON cvrpt.object_id = cvt.subject_id ");
                queryString.append("    AND cvrt.object_id = :traitClassId AND cvrt.type_id = ").append(TermId.IS_A.getId()).append(" ");
            }
            if (propertyId != null){
                queryString.append("INNER JOIN cvterm_relationship cvrp ON cvr.subject_id = cvrp.subject_id ");
                queryString.append("    AND cvr.object_id = :propertyId AND cvr.type_id = ").append(TermId.HAS_PROPERTY.getId()).append(" ");
            }
            if (methodId != null){
                queryString.append("INNER JOIN cvterm_relationship cvrm ON cvr.subject_id = cvrm.subject_id ");
                queryString.append("    AND cvr.object_id = :methodId AND cvr.type_id = ").append(TermId.HAS_METHOD.getId()).append(" ");
            }
            if (scaleId != null){
                queryString.append("INNER JOIN cvterm_relationship cvrs ON cvr.subject_id = cvrs.subject_id ");
                queryString.append("    AND  cvr.object_id = :scaleId AND cvr.type_id = ").append(TermId.HAS_SCALE.getId()).append(" ");
            }

            SQLQuery query = getSession().createSQLQuery(queryString.toString());
            if (traitClassId != null){
                query.setParameter("traitClassId", traitClassId);
            }
            if (propertyId != null){
                query.setParameter("propertyId", propertyId);
            }
            if (methodId != null){
                query.setParameter("methodId", methodId);
            }
            if (scaleId != null){
                query.setParameter("scaleId", scaleId);
            }
            
            List<Integer> result = query.list();
            
            if (result != null && !result.isEmpty()) {
                for (Integer row : result) {
                    standardVariableIds.add((Integer) row);
                }
            }
            

        } catch(HibernateException e) {
                logAndThrowException("Error at getStandardVariableIds :" + e.getMessage(), e);
        }
        return standardVariableIds;
    }
    
    
    @SuppressWarnings("unused")
    private List<Integer> getStandardVariablesBelongingToTraitClass(List<Integer> standardVariableIds) throws MiddlewareQueryException {
        List<Integer> standardVariablesOfTraitClass = new ArrayList<Integer>();
        
        if (standardVariableIds == null || standardVariableIds.size() == 0){
            return standardVariablesOfTraitClass;
        }
        
        try {
            // Trait class via 'IS A' of property
            StringBuilder queryString = new StringBuilder()
                .append("SELECT DISTINCT cvr.subject_id ")
                .append("FROM  cvterm_relationship cvr ")
                .append("   INNER JOIN cvterm_relationship cvrt ON cvr.object_id = cvrt.subject_id ")
                .append("                       AND cvr.type_id = ").append(TermId.HAS_PROPERTY.getId()).append(" ")
                .append("    AND cvrt.type_id = ").append(TermId.IS_A.getId()).append(" ")
                .append("   AND cvr.subject_id IN (:standardVariableIds) ")
                ;

            SQLQuery query = getSession().createSQLQuery(queryString.toString());
            query.setParameterList("standardVariableIds", standardVariableIds);
            
            List<Integer> result = query.list();
            
            if (result != null && !result.isEmpty()) {
                for (Integer row : result) {
                    standardVariablesOfTraitClass.add((Integer) row);
                }
            }

        } catch(HibernateException e) {
            logAndThrowException("Error at getStandardVariablesBelongingToTraitClass :" + e.getMessage(), e);
        }

        
        return standardVariablesOfTraitClass;
    }

    
    public List<Property> getAllPropertiesWithTraitClass() throws MiddlewareQueryException {
        List<Property> properties = new ArrayList<Property>();
        try {
            StringBuilder sql = new StringBuilder()
                .append("SELECT p.cvterm_id, p.name, p.definition, pr.object_id, coId.value ")
                .append(" FROM cvterm p ")
                .append(" INNER JOIN cvterm_relationship pr ON pr.subject_id = p.cvterm_id AND pr.type_id = ").append(TermId.IS_A.getId())
                .append(" LEFT JOIN cvtermprop coId ON coId.cvterm_id = p.cvterm_id AND coId.type_id = ").append(TermId.CROP_ONTOLOGY_ID.getId())
                .append(" WHERE p.cv_id = ").append(CvId.PROPERTIES.getId())
                .append(" AND p.is_obsolete = 0 ");
            
            SQLQuery query = getSession().createSQLQuery(sql.toString());
            List<Object[]> result = query.list();
            
            if (result != null && !result.isEmpty()) {
                for (Object[] row : result) {
                    properties.add(
                            new Property(
                                    new Term((Integer) row[0], (String) row[1], (String) row[2])
                                    , new Term((Integer) row[3], null, null)
                                    , (String) row[4]
                            )
                    );
                }
            }
            
        } catch(HibernateException e) {
            logAndThrowException("Error at getStandadardVariableIdByTermId :" + e.getMessage(), e);
        }
        return properties;
    }

    public Integer getStandadardVariableIdByPropertyScaleMethodRole(Integer propertyId, Integer scaleId, Integer methodId, PhenotypicType role)
            throws MiddlewareQueryException {
        try {
            StringBuilder queryString = new StringBuilder();
            queryString.append("SELECT DISTINCT cvr.subject_id ");
            queryString.append("FROM cvterm_relationship cvr ");
            queryString.append("INNER JOIN cvterm_relationship cvrp ON cvr.subject_id = cvrp.subject_id AND cvrp.type_id = 1200 ");
            queryString.append("INNER JOIN cvterm_relationship cvrs ON cvr.subject_id = cvrs.subject_id AND cvrs.type_id = 1220 ");
            queryString.append("INNER JOIN cvterm_relationship cvrm ON cvr.subject_id = cvrm.subject_id AND cvrm.type_id = 1210 ");
            queryString.append("INNER JOIN cvterm_relationship stinrel ON stinrel.subject_id = cvrm.subject_id AND stinrel.type_id = 1044 ");
            queryString.append("WHERE cvrp.object_id = :propertyId AND cvrs.object_id = :scaleId AND cvrm.object_id = :methodId ");
            queryString.append("AND stinrel.object_id IN ( ").append(getStoredInAsString(role)).append(" ) ");
            queryString.append(" LIMIT 0,1");
            
            SQLQuery query = getSession().createSQLQuery(queryString.toString());
            query.setParameter("propertyId", propertyId);
            query.setParameter("scaleId", scaleId);
            query.setParameter("methodId", methodId);
            
            Integer id = (Integer) query.uniqueResult();
                        
            return id;
                        
        } catch(HibernateException e) {
            logAndThrowException("Error at getStandadardVariableIdByPropertyScaleMethod :" + e.getMessage(), e);
        }
        return null;    
    
    }
    
    private String getStoredInAsString(PhenotypicType role) {
        StringBuilder sb = new StringBuilder();
        
        for (Integer storedin : role.getTypeStorages()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(storedin);
        }
        
        return sb.toString();
    }
    
    public List<StandardVariableReference> getAllTreatmentFactors(List<Integer> hiddenFields, boolean showOnlyPaired) throws MiddlewareQueryException {
    	List<StandardVariableReference> list = new ArrayList<StandardVariableReference>();
    	
		try {
			
			// TODO : further optimize to remove the need for union of operation
			StringBuffer sqlString = new StringBuffer()
				.append("SELECT c.cvterm_id, c.name, c.definition ")
				.append(" FROM cvterm c ")
				.append(" INNER JOIN cvterm_relationship stinrel ON stinrel.subject_id = c.cvterm_id ")
				.append("   AND stinrel.type_id = ").append(TermId.STORED_IN.getId())
				.append("   AND stinrel.object_id = ").append(TermId.TRIAL_DESIGN_INFO_STORAGE.getId())
				.append(" INNER JOIN cvterm_relationship dtyperel ON dtyperel.subject_id = c.cvterm_id ")
				.append("   AND dtyperel.type_id = ").append(TermId.HAS_TYPE.getId())
				.append("   AND dtyperel.object_id = ").append(TermId.NUMERIC_VARIABLE.getId())
				.append(" INNER JOIN cvterm_relationship proprel ON proprel.subject_id = c.cvterm_id ")
				.append("   AND proprel.type_id = ").append(TermId.HAS_PROPERTY.getId())
				.append(" WHERE c.cvterm_id NOT IN (:hiddenFields) AND ")
			;
			if (!showOnlyPaired) {
				sqlString.append(" NOT " );
			}
			sqlString.append(" EXISTS (SELECT 1 FROM cvterm_relationship pairrel ")
				.append(" INNER JOIN cvterm_relationship stin ON stin.subject_id = pairrel.subject_id ")
				.append(" AND stin.type_id = ").append(TermId.STORED_IN.getId())
				.append(" AND stin.object_id = ").append(TermId.TRIAL_DESIGN_INFO_STORAGE.getId())
				.append(" WHERE pairrel.object_id = proprel.object_id ")
				.append(" AND pairrel.type_id = ").append(TermId.HAS_PROPERTY.getId())
				.append(" AND pairrel.subject_id <> c.cvterm_id ")
				.append(" AND pairrel.subject_id NOT IN (:hiddenFields)) ")
				;
			    
			SQLQuery query = getSession().createSQLQuery(sqlString.toString());
			query.setParameterList("hiddenFields", hiddenFields);
			
	        List<Object[]> results = (List<Object[]>) query.list();
	        for (Object[] row : results) {
	        	StandardVariableReference svar = new StandardVariableReference((Integer) row[0], (String) row[1], (String) row[2]);
	        	svar.setHasPair(showOnlyPaired);
	            list.add(svar);
	        }
		        
		} catch(HibernateException e) {
			logAndThrowException("Error in getAllTreatmentFactors in CVTermDao: " + e.getMessage(), e);
		}
		
		return list;
    }
    
    public boolean hasPossibleTreatmentPairs(int cvTermId, int propertyId, List<Integer> hiddenFields) throws MiddlewareQueryException {
    	List<StandardVariable> list = new ArrayList<StandardVariable>();
    	
		try {
			StringBuffer sqlString = new StringBuffer()
				.append("SELECT count(c.cvterm_id) ")
				.append(" FROM cvterm c ")
				.append(" INNER JOIN cvterm_relationship pr ON pr.type_id = ").append(TermId.HAS_PROPERTY.getId())
				.append("   AND pr.subject_id = c.cvterm_id ")
				.append("   AND pr.object_id = ").append(propertyId)
				.append(" INNER JOIN cvterm_relationship sr ON sr.type_id = ").append(TermId.HAS_SCALE.getId())
				.append("   AND sr.subject_id = c.cvterm_id ")
				.append(" INNER JOIN cvterm_relationship mr ON mr.type_id = ").append(TermId.HAS_METHOD.getId())
				.append("   AND mr.subject_id = c.cvterm_id ")
				.append(" INNER JOIN cvterm_relationship stin ON stin.type_id = ").append(TermId.STORED_IN.getId())
				.append("   AND stin.subject_id = c.cvterm_id AND stin.object_id = ").append(TermId.TRIAL_DESIGN_INFO_STORAGE.getId())
				.append(" INNER JOIN cvterm_relationship dtyperel ON dtyperel.type_id = ").append(TermId.HAS_TYPE.getId())
				.append("   AND dtyperel.subject_id = c.cvterm_id AND dtyperel.object_id = ").append(TermId.NUMERIC_VARIABLE.getId())
				.append(" WHERE c.cvterm_id <> ").append(cvTermId)
				.append("   AND c.cvterm_id NOT IN (:hiddenFields) ");
			;
			
			SQLQuery query = getSession().createSQLQuery(sqlString.toString());
			query.setParameterList("hiddenFields", hiddenFields);
			long count = ((BigInteger) query.uniqueResult()).longValue();
			return count > 0;
			
		} catch(HibernateException e) {
			logAndThrowException("Error in getAllPossibleTreatmentPairs in CVTermDao: " + e.getMessage(), e);
		}
		return false;
    }
    
    public List<StandardVariable> getAllPossibleTreatmentPairs(int cvTermId, int propertyId, List<Integer> hiddenFields) throws MiddlewareQueryException {
    	List<StandardVariable> list = new ArrayList<StandardVariable>();
    	
		try {
			StringBuffer sqlString = new StringBuffer()
				.append("SELECT c.cvterm_id, c.name, c.definition, pr.object_id AS propertyId, sr.object_id AS scaleId, mr.object_id AS methodId ")
				.append(" FROM cvterm c ")
				.append(" INNER JOIN cvterm_relationship pr ON pr.type_id = ").append(TermId.HAS_PROPERTY.getId())
				.append("   AND pr.subject_id = c.cvterm_id and pr.object_id = ").append(propertyId)
				.append(" INNER JOIN cvterm_relationship sr ON sr.type_id = ").append(TermId.HAS_SCALE.getId())
				.append("   AND sr.subject_id = c.cvterm_id ")
				.append(" INNER JOIN cvterm_relationship mr ON mr.type_id = ").append(TermId.HAS_METHOD.getId())
				.append("   AND mr.subject_id = c.cvterm_id ")
				.append(" INNER JOIN cvterm_relationship stin ON stin.type_id = ").append(TermId.STORED_IN.getId())
				.append("   AND stin.subject_id = c.cvterm_id AND stin.object_id = ").append(TermId.TRIAL_DESIGN_INFO_STORAGE.getId())
				.append(" WHERE c.cvterm_id <> ").append(cvTermId)
				.append("   AND c.cvterm_id NOT IN (:hiddenFields) ");
			;
			    
			SQLQuery query = getSession().createSQLQuery(sqlString.toString())
					.addScalar("cvterm_id")
					.addScalar("name")
					.addScalar("definition")
					.addScalar("propertyId")
					.addScalar("scaleId")
					.addScalar("methodId");
			
			query.setParameterList("hiddenFields", hiddenFields);
					             
	        List<Object[]> results = (List<Object[]>) query.list();
	        for (Object[] row : results) {
	        	StandardVariable variable = new StandardVariable();
	        	variable.setId((Integer) row[0]);
	        	variable.setName((String) row[1]);
	        	variable.setDescription((String) row[2]);
	        	variable.setProperty(new Term((Integer) row[3], null, null));
	        	variable.setScale(new Term((Integer) row[4], null, null));
	        	variable.setMethod(new Term((Integer) row[5], null, null));
	            list.add(variable);
	        }
		        
		} catch(HibernateException e) {
			logAndThrowException("Error in getAllPossibleTreatmentPairs in CVTermDao: " + e.getMessage(), e);
		}
		
		return list;
    }
    
    public List<Scale> getAllInventoryScales() throws MiddlewareQueryException {
    	List<Scale> list = new ArrayList<Scale>();
    	try {
    		StringBuffer sql = new StringBuffer()
    							.append("SELECT pr.subject_id AS id, s.name AS scalename, m.name AS methodname ")
    							.append(" FROM cvterm_relationship pr ")
    							.append(" INNER JOIN cvterm_relationship mr ON mr.subject_id = pr.subject_id ")
    							.append("    AND mr.type_id = ").append(TermId.HAS_METHOD.getId())
    							.append(" INNER JOIN cvterm m ON m.cvterm_id = mr.object_id ")
    							.append(" INNER JOIN cvterm_relationship sr ON sr.subject_id = pr.subject_id ")
    							.append("    AND sr.type_id = ").append(TermId.HAS_SCALE.getId())
    							.append(" INNER JOIN cvterm s ON s.cvterm_id = sr.object_id ")
    							.append(" WHERE pr.type_id = ").append(TermId.HAS_PROPERTY.getId())
    							.append("    AND pr.object_id = ").append(TermId.INVENTORY_AMOUNT_PROPERTY.getId())
    							;
    		
    		SQLQuery query = getSession().createSQLQuery(sql.toString())
    							.addScalar("id")
    							.addScalar("scalename")
    							.addScalar("methodname")
    							;
    		List<Object[]> result = query.list();
    		if (result != null && !result.isEmpty()) {
    			for (Object[] row : result) {
    				String displayName = row[1] + " - " + row[2];

                    // TODO : consider adding a separate field 'displayName' to Term or Scale, to avoid de-sync of POJO values and that of DB.
                    // With this implementation, the name and description in the DB is ignored in favor of the display name format provided in the specs
    				list.add(new Scale(new Term((Integer) row[0], displayName, displayName)));
    			}
    		}
    		
    	} catch(HibernateException e) {
			logAndThrowException("Error in getAllInventoryScales in CVTermDao: " + e.getMessage(), e);
    	}
    	return list;
    }
}
