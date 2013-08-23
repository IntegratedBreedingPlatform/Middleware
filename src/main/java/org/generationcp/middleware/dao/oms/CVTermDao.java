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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.h2h.CategoricalTraitInfo;
import org.generationcp.middleware.domain.h2h.CategoricalValue;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;

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
	
	public Set<Integer> findStdVariablesByNameOrSynonym(String nameOrSynonym) throws MiddlewareQueryException {
		Set<Integer> stdVarIds = new HashSet<Integer>();
		try {
			SQLQuery query = getSession().createSQLQuery("select distinct cvterm.cvterm_id " +
	                                                     "from cvterm cvterm, cvtermsynonym syn " +
	                                                     "where cvterm.cv_id = 1040 " +
	                                                     "   and (cvterm.name = '" + nameOrSynonym + "'" +
	                                                     "        or (syn.synonym = '" + nameOrSynonym + "'" +
	                                                     "            and syn.cvterm_id = cvterm.cvterm_id))");
	             
	        List<Object> results = (List<Object>) query.list();
	        for (Object row : results) {
	            stdVarIds.add((Integer) row);
	        }
			
		} catch(HibernateException e) {
			logAndThrowException("Error in findStdVariablesByNameOrSynonym=" + nameOrSynonym + " in CVTermDao: " + e.getMessage(), e);
		}
		return stdVarIds;
	}

	public CVTerm getByNameAndCvId(String name, int cvId) throws MiddlewareQueryException {
        CVTerm term = null;
		
		try {
			Criteria criteria = getSession().createCriteria(getPersistentClass());
			criteria.add(Restrictions.eq("cvId", cvId));
			criteria.add(Restrictions.eq("name", name));
			
			term = (CVTerm) criteria.uniqueResult();
		
		} catch (HibernateException e) {
			logAndThrowException("Error at getByNameAndCvId=" + name + ", " + cvId + " query on CVTermDao: " + e.getMessage(), e);
		}
		
		return term;
	}
	
	public List<CVTerm> getByIds(Collection<Integer> ids) throws MiddlewareQueryException {
		List<CVTerm> terms = new ArrayList<CVTerm>();
		
		try {
			Criteria criteria = getSession().createCriteria(getPersistentClass());
			criteria.add(Restrictions.in("cvTermId", ids));
			
			terms = criteria.list();
			
		} catch(HibernateException e) {
			logAndThrowException("Error at GetByIds=" + ids + " query on CVTermDao: " + e.getMessage(), e);
		}
		
		return terms;
	}
	
	public List<CVTerm> getVariablesByType(List<Integer> types, Integer storedIn) throws MiddlewareQueryException {
        List<CVTerm> terms = new ArrayList<CVTerm>();
        
        try {
            String queryString = 
                    "SELECT cvt.cvterm_id, cvt.name, cvt.definition "
                    + "FROM cvterm cvt " 
                    + "INNER JOIN cvterm_relationship cvr ON cvr.subject_id = cvt.cvterm_id " 
                    + "             AND cvr.type_id = 1105 AND cvr.object_id IN (:types) "
                    ;
            if (storedIn != null) {
                queryString += 
                    "INNER JOIN cvterm_relationship stored_in ON cvr.subject_id = stored_in.subject_id " 
                    + "AND stored_in.type_id = 1044 AND stored_in.object_id = :storedIn " ;
            }
            
            SQLQuery query = getSession().createSQLQuery(queryString);
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
          query = getSession().createSQLQuery(
                  "SELECT cvterm_id, cvterm.name " +
                  "FROM cvterm " +
                  "WHERE cvterm_id IN (:ids) " 
                  );
          query.setParameterList("ids", valueIds);
          
          list = query.list();
    
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
	
	

}
