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

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.dms.GeolocationProperty;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link GeolocationProperty}.
 * 
 */
public class GeolocationPropertyDao extends GenericDAO<GeolocationProperty, Integer> {
	
	@SuppressWarnings("unchecked")
	public List<Integer> getGeolocationIdsByPropertyTypeAndValue(Integer typeId, String value) throws MiddlewareQueryException {
		try {
			Criteria criteria = getSession().createCriteria(getPersistentClass());
			criteria.add(Restrictions.eq("typeId", typeId));
			criteria.add(Restrictions.eq("value", value));
			criteria.setProjection(Projections.property("geolocation.locationId"));
			
			return criteria.list();
			
		} catch (HibernateException e) {
			logAndThrowException("Error at getIdsByPropertyTypeAndValue=" + typeId + ", " + value + " query on GeolocationDao: " + e.getMessage(), e);
		}
		return new ArrayList<Integer>();
	}
	
	@SuppressWarnings("unchecked")
	public List<ValueReference> getDistinctPropertyValues(int stdVarId) throws MiddlewareQueryException {
		List<ValueReference> results = new ArrayList<ValueReference>();
		try {
			String sql = "SELECT DISTINCT value FROM nd_geolocationprop WHERE type_id = :stdVarId ";
			SQLQuery query = getSession().createSQLQuery(sql);
			query.setParameter("stdVarId", stdVarId);
			
			List<String> list = query.list();
			if (list != null && !list.isEmpty()) {
				for (String row : list) {
					results.add(new ValueReference(row, row));
				}
			}
			
		} catch (HibernateException e) {
			logAndThrowException("Error at getDistinctPropertyValues=" + stdVarId + " query on GeolocationPropertyDao: " + e.getMessage(), e);
		}
		return results;
	}
	
        public String getGeolocationPropValue(int stdVarId, int studyId) throws MiddlewareQueryException {
                try {
                    StringBuilder sql = new StringBuilder()
                    .append("SELECT value ")
                    .append("FROM nd_experiment e ")
                    .append("INNER JOIN nd_experiment_project ep ON ep.nd_experiment_id = e.nd_experiment_id ")
                    .append("INNER JOIN nd_geolocationprop gp ON gp.nd_geolocation_id = e.nd_geolocation_id ")
                    .append("WHERE ep.project_id = :projectId AND gp.type_id = :stdVarId ORDER BY e.nd_geolocation_id ");
                    SQLQuery query = getSession().createSQLQuery(sql.toString());
                    query.setParameter("projectId", studyId);
                    query.setParameter("stdVarId", stdVarId);
                    return (String) query.uniqueResult();
                } catch (HibernateException e) {
                    logAndThrowException("Error at getGeolocationPropValue=" + stdVarId + " query on GeolocationPropertyDao: " + e.getMessage(), e);
                }
                return "";
        }
	
   	@SuppressWarnings("unchecked")
   	public String getValueOfTrialInstance(int datasetId, int typeId, String trialInstance) throws MiddlewareQueryException {
   		try {
   			StringBuilder sql = new StringBuilder()
   				.append("SELECT gp.value FROM nd_geolocationprop gp ")
   				.append(" INNER JOIN nd_geolocation g ON g.nd_geolocation_id = gp.nd_geolocation_id ")
   				.append("   AND g.description = ").append(trialInstance)
   				.append(" INNER JOIN nd_experiment e ON e.nd_geolocation_id = g.nd_geolocation_id ")
   				.append(" INNER JOIN nd_experiment_project ep ON ep.nd_experiment_id = e.nd_experiment_id ")
   				.append("   AND ep.project_id = ").append(datasetId)
   				.append(" WHERE gp.type_id = ").append(typeId);
   			
            SQLQuery query = getSession().createSQLQuery(sql.toString());
            return (String) query.uniqueResult();

   		} catch (HibernateException e) {
			logAndThrowException("Error at getValueOfTrialInstance=" + datasetId + " query on GeolocationPropertyDao: " + e.getMessage(), e);
   		}
   		return null;
   	}
   	
   	public void deleteGeolocationPropertyValueInProject(int studyId, int termId) throws MiddlewareQueryException {
  		try {
  			StringBuilder sql = new StringBuilder()
  				.append("DELETE FROM nd_geolocationprop  ")
  				.append(" WHERE nd_geolocation_id IN ( ")
  				.append("   SELECT e.nd_geolocation_id ")
  				.append("   FROM nd_experiment e ")
  				.append("   INNER JOIN nd_experiment_project ep ON ep.nd_experiment_id = e.nd_experiment_id ")
  				.append("   INNER JOIN project_relationship pr ON pr.type_id = ").append(TermId.BELONGS_TO_STUDY.getId())
  				.append("     AND (pr.object_project_id = ep.project_id OR pr.subject_project_id = ep.project_id) ")
  				.append("   WHERE pr.object_project_id = ").append(studyId).append(") ")
  				.append("   AND type_id = ").append(termId);
  			
  			SQLQuery query = getSession().createSQLQuery(sql.toString());
  			query.executeUpdate();
  			
   		} catch (HibernateException e) {
			logAndThrowException("Error at deleteGeolocationPropertyValueInProject=" + studyId + ", " + termId + " query on GeolocationPropertyDao: " + e.getMessage(), e);
   		}
   	}
}
