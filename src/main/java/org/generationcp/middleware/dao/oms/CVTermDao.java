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

import java.math.BigInteger;
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
import org.generationcp.middleware.domain.h2h.TraitInfo;
import org.generationcp.middleware.domain.h2h.TraitType;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.TermId;
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

    public List<TraitInfo> getTraitInfo(List<Integer> traitIds) throws MiddlewareQueryException {
        List<TraitInfo> traits = new ArrayList<TraitInfo>();
        
        try{
        
            StringBuilder sql = new StringBuilder()
            .append("SELECT cvt.cvterm_id, cvt.name, cvt.definition,  c_scale.scaleName, cr_type.object_id ")
            .append("FROM cvterm cvt ") 
            .append("	INNER JOIN cvterm_relationship cr_scale ON cvt.cvterm_id = cr_scale.subject_id ")
            .append("   INNER JOIN (SELECT cvterm_id, scaleName FROM cvterm) c_scale ON c_scale.cvterm_id = cr_scale.object_id ") 
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
			
			List<Integer> methodIds = (List<Integer>) query.list();						
			return methodIds;
						
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
			
			List<Integer> scaleIds = (List<Integer>) query.list();						
			return scaleIds;
						
		} catch(HibernateException e) {
			logAndThrowException("Error at findScaleTermIdsByTrait :" + e.getMessage(), e);
		}
		return null;
	}
	
}
