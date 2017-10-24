/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.dao.dms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;
import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.dms.GeolocationProperty;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DAO class for {@link GeolocationProperty}.
 *
 */
public class GeolocationPropertyDao extends GenericDAO<GeolocationProperty, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(GeolocationPropertyDao.class);

	@SuppressWarnings("unchecked")
	public List<Integer> getGeolocationIdsByPropertyTypeAndValue(Integer typeId, String value) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("typeId", typeId));
			criteria.add(Restrictions.eq("value", value));
			criteria.setProjection(Projections.property("geolocation.locationId"));

			return criteria.list();

		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error at getIdsByPropertyTypeAndValue=" + typeId + ", " + value + " query on GeolocationDao: " + e.getMessage(), e);
		}
		return new ArrayList<Integer>();
	}

	@SuppressWarnings("unchecked")
	public List<ValueReference> getDistinctPropertyValues(int stdVarId) throws MiddlewareQueryException {
		List<ValueReference> results = new ArrayList<ValueReference>();
		try {
			String sql = "SELECT DISTINCT value FROM nd_geolocationprop WHERE type_id = :stdVarId ";
			SQLQuery query = this.getSession().createSQLQuery(sql);
			query.setParameter("stdVarId", stdVarId);

			List<String> list = query.list();
			if (list != null && !list.isEmpty()) {
				for (String row : list) {
					results.add(new ValueReference(row, row));
				}
			}

		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error at getDistinctPropertyValues=" + stdVarId + " query on GeolocationPropertyDao: " + e.getMessage(), e);
		}
		return results;
	}

	public String getGeolocationPropValue(int stdVarId, int studyId) throws MiddlewareQueryException {
		try {
			StringBuilder sql =
					new StringBuilder().append("SELECT value ").append("FROM nd_experiment e ")
							.append("INNER JOIN nd_experiment_project ep ON ep.nd_experiment_id = e.nd_experiment_id ")
							.append("INNER JOIN nd_geolocationprop gp ON gp.nd_geolocation_id = e.nd_geolocation_id ")
							.append("WHERE ep.project_id = :projectId AND gp.type_id = :stdVarId ORDER BY e.nd_geolocation_id ");
			SQLQuery query = this.getSession().createSQLQuery(sql.toString());
			query.setParameter("projectId", studyId);
			query.setParameter("stdVarId", stdVarId);
			return (String) query.uniqueResult();
		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error at getGeolocationPropValue=" + stdVarId + " query on GeolocationPropertyDao: " + e.getMessage(), e);
		}
		return "";
	}

	@SuppressWarnings("unchecked")
	public String getValueOfTrialInstance(int datasetId, int typeId, String trialInstance) throws MiddlewareQueryException {
		try {
			StringBuilder sql =
					new StringBuilder().append("SELECT gp.value FROM nd_geolocationprop gp ")
							.append(" INNER JOIN nd_geolocation g ON g.nd_geolocation_id = gp.nd_geolocation_id ")
							.append("   AND g.description = ").append(trialInstance)
							.append(" INNER JOIN nd_experiment e ON e.nd_geolocation_id = g.nd_geolocation_id ")
							.append(" INNER JOIN nd_experiment_project ep ON ep.nd_experiment_id = e.nd_experiment_id ")
							.append("   AND ep.project_id = ").append(datasetId).append(" WHERE gp.type_id = ").append(typeId);

			SQLQuery query = this.getSession().createSQLQuery(sql.toString());
			return (String) query.uniqueResult();

		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error at getValueOfTrialInstance=" + datasetId + " query on GeolocationPropertyDao: " + e.getMessage(), e);
		}
		return null;
	}

	public void deleteGeolocationPropertyValueInProject(int studyId, int termId) throws MiddlewareQueryException {
		try {
			// Please note we are manually flushing because non hibernate based deletes and updates causes the Hibernate session to get out of synch with
			// underlying database. Thus flushing to force Hibernate to synchronize with the underlying database before the delete
			// statement
			this.getSession().flush();
			
			executeDeleteStatement(studyId, termId, "pr.object_project_id");
			executeDeleteStatement(studyId, termId, "pr.subject_project_id");

		} catch (HibernateException e) {
			this.logAndThrowException("Error at deleteGeolocationPropertyValueInProject=" + studyId + ", " + termId
					+ " query on GeolocationPropertyDao: " + e.getMessage(), e);
		}
	}

	private void executeDeleteStatement(final int studyId,final int termId,final String joinCriteria) {
		final StringBuilder sql1 = new StringBuilder().append("Delete ngp.* FROM nd_geolocationprop ngp "
				+ "INNER JOIN nd_experiment e ON ngp.nd_geolocation_id = e.nd_geolocation_id AND ngp.type_id = :termId "
				+ "INNER JOIN nd_experiment_project ep ON ep.nd_experiment_id = e.nd_experiment_id "
				+ "INNER JOIN project_relationship pr ON pr.type_id = :belongsToTypeId "
				+ "			  AND ").append(joinCriteria).append(" = ep.project_id "
				+ "WHERE pr.object_project_id = :studyId");

		final SQLQuery sqlQuery1 = this.getSession().createSQLQuery(sql1.toString());
		sqlQuery1.setParameter("studyId", studyId);
		sqlQuery1.setParameter("termId", termId);
		sqlQuery1.setParameter("belongsToTypeId", TermId.BELONGS_TO_STUDY.getId());

		sqlQuery1.executeUpdate();
	}

	public Map<String, String> getGeolocationPropsAndValuesByStudy(final Integer studyId) throws MiddlewareQueryException {
		Preconditions.checkNotNull(studyId);
		Map<String, String> geoProperties = new HashMap<>();
		StringBuilder sql = new StringBuilder().append("SELECT  ").append("    cv.definition as name, geo.value as value ").append("FROM ")
				.append("    nd_geolocationprop geo ").append("        INNER JOIN ")
				.append("    cvterm cv ON (cv.cvterm_id = geo.type_id) ").append("WHERE ").append("    geo.nd_geolocation_id = :studyId ")
				.append("        AND geo.type_id NOT IN (8050 , 8060, 8371, 8190, 8070, 8180) ");
		try {
			Query query =
					this.getSession().createSQLQuery(sql.toString()).addScalar("name").addScalar("value").setParameter("studyId", studyId);
			List<Object> results = query.list();
			for (Object obj : results) {
				Object[] row = (Object[]) obj;
				geoProperties.put((String) row[0], (String) row[1]);
			}
			return geoProperties;
		} catch (MiddlewareQueryException e) {
			final String message = "Error with getGeolocationPropsAndValuesByStudy() query from studyId: " + studyId;
			GeolocationPropertyDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}
}
