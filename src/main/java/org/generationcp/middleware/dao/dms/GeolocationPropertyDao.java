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
			logAndThrowException("Error at getDistinctPropertyValues=" + stdVarId + " query on GeolocationDao: " + e.getMessage(), e);
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
	
}
