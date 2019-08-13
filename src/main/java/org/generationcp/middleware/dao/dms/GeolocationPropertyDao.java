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

import com.google.common.base.Preconditions;
import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.dms.GeolocationProperty;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO class for {@link GeolocationProperty}.
 */
public class GeolocationPropertyDao extends GenericDAO<GeolocationProperty, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(GeolocationPropertyDao.class);

	@SuppressWarnings("unchecked")
	public List<Integer> getGeolocationIdsByPropertyTypeAndValue(final Integer typeId, final String value) {
		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("typeId", typeId));
			criteria.add(Restrictions.eq("value", value));
			criteria.setProjection(Projections.property("geolocation.locationId"));

			return criteria.list();

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				"Error at getIdsByPropertyTypeAndValue=" + typeId + ", " + value + " query on GeolocationDao: " + e.getMessage(), e);
		}
	}

	public String getGeolocationPropValue(final int stdVarId, final int studyId) {
		try {
			final StringBuilder sql =
				new StringBuilder().append("SELECT value ").append("FROM nd_experiment e ")
					.append("INNER JOIN nd_geolocationprop gp ON gp.nd_geolocation_id = e.nd_geolocation_id ")
					.append("WHERE e.project_id = :projectId AND gp.type_id = :stdVarId ORDER BY e.nd_geolocation_id ");
			final SQLQuery query = this.getSession().createSQLQuery(sql.toString());
			query.setParameter("projectId", studyId);
			query.setParameter("stdVarId", stdVarId);
			return (String) query.uniqueResult();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				"Error at getGeolocationPropValue=" + stdVarId + " query on GeolocationPropertyDao: " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public String getValueOfTrialInstance(final int datasetId, final int typeId, final String trialInstance) {
		try {
			final StringBuilder sql =
				new StringBuilder().append("SELECT gp.value FROM nd_geolocationprop gp ")
					.append(" INNER JOIN nd_geolocation g ON g.nd_geolocation_id = gp.nd_geolocation_id ")
					.append("   AND g.description = ").append(trialInstance)
					.append(" INNER JOIN nd_experiment e ON e.nd_geolocation_id = g.nd_geolocation_id ")
					.append(" WHERE gp.type_id = ").append(typeId).append("   AND e.project_id = ").append(datasetId);

			final SQLQuery query = this.getSession().createSQLQuery(sql.toString());
			return (String) query.uniqueResult();

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				"Error at getValueOfTrialInstance=" + datasetId + " query on GeolocationPropertyDao: " + e.getMessage(), e);
		}
	}

	public void deleteGeolocationPropertyValueInProject(final int studyId, final int termId) {
		try {
			// Please note we are manually flushing because non hibernate based deletes and updates causes the Hibernate session to get out of synch with
			// underlying database. Thus flushing to force Hibernate to synchronize with the underlying database before the delete
			// statement
			this.getSession().flush();

			this.deleteValues(studyId, termId);

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error at deleteGeolocationPropertyValueInProject=" + studyId + ", " + termId
				+ " query on GeolocationPropertyDao: " + e.getMessage(), e);
		}
	}

	private void deleteValues(final int studyId,final int termId) {
		final StringBuilder sql1 = new StringBuilder().append("Delete ngp.* FROM nd_geolocationprop ngp "
				+ "INNER JOIN nd_experiment e ON ngp.nd_geolocation_id = e.nd_geolocation_id AND ngp.type_id = :termId "
				+ "INNER JOIN project p ON p.project_id = e.project_id "
				+ "WHERE (p.study_id = :studyId OR p.project_id = :studyId)");

		final SQLQuery sqlQuery1 = this.getSession().createSQLQuery(sql1.toString());
		sqlQuery1.setParameter("studyId", studyId);
		sqlQuery1.setParameter("termId", termId);

		sqlQuery1.executeUpdate();
	}

	public Map<String, String> getGeolocationPropsAndValuesByGeolocation(final Integer geolocationId) {
		Preconditions.checkNotNull(geolocationId);
		final Map<String, String> geoProperties = new HashMap<>();
		final StringBuilder sql =
			new StringBuilder().append("SELECT  ").append("    cv.definition as name, geo.value as value ").append("FROM ")
				.append("    nd_geolocationprop geo ").append("        INNER JOIN ")
				.append("    cvterm cv ON (cv.cvterm_id = geo.type_id) ").append("WHERE ").append("    geo.nd_geolocation_id = :geolocationId ")
				.append("        AND geo.type_id NOT IN (8371, 8190, 8070, 8180) ");
		try {
			final Query query =
				this.getSession().createSQLQuery(sql.toString()).addScalar("name").addScalar("value").setParameter("geolocationId", geolocationId);
			final List<Object> results = query.list();
			for (final Object obj : results) {
				final Object[] row = (Object[]) obj;
				geoProperties.put((String) row[0], (String) row[1]);
			}
			return geoProperties;
		} catch (final MiddlewareQueryException e) {
			final String message = "Error with getGeolocationPropsAndValuesByGeolocation() query from geolocationId: " + geolocationId;
			GeolocationPropertyDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public Map<Integer, String> getInstanceIdLocationIdMap(final List<Integer> instanceIds) {
		Map<Integer, String> instanceIdLocationIdMap = new HashMap<>();
		final StringBuilder sql =
			new StringBuilder().append("SELECT  ").append("    geo.nd_geolocation_id as instanceId, geo.value as value ").append("FROM ")
				.append(" nd_geolocationprop geo ").append("WHERE ").append("    geo.nd_geolocation_id in (:geolocationIds) ")
				.append("        AND geo.type_id = 8190");

		try {
			final Query query =
				this.getSession().createSQLQuery(sql.toString()).addScalar("instanceId").addScalar("value").setParameterList("geolocationIds", instanceIds);
			final List<Object> results = query.list();
			for (final Object obj : results) {
				final Object[] row = (Object[]) obj;
				instanceIdLocationIdMap.put((Integer) row[0], (String) row[1]);
			}
			return instanceIdLocationIdMap;
		} catch (final MiddlewareQueryException e) {
			final String message = "Error with getInstanceIdLocationIdMap() query from instanceIds: " + instanceIds;
			GeolocationPropertyDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public Map<Integer, String> getGeoLocationPropertyByVariableId(final Integer datasetId, final Integer instanceDbId) {
		Preconditions.checkNotNull(datasetId);
		final String sql = "SELECT "
			+ "    gp.type_id as variableId, "
			+ "	   gp.value as value "
			+ "FROM "
			+ "    nd_experiment e "
			+ "        INNER JOIN "
			+ "    nd_geolocationprop gp ON gp.nd_geolocation_id = e.nd_geolocation_id "
			+ "WHERE "
			+ "		e.project_id = :datasetId "
			+ "		and e.nd_geolocation_id = :instanceDbId";

		final SQLQuery query = this.getSession().createSQLQuery(sql);
		query.addScalar("variableId").addScalar("value").setParameter("datasetId", datasetId).setParameter("instanceDbId", instanceDbId);
		query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);

		final List<Map<String, Object>> results = query.list();
		final Map<Integer, String> geoProperties = new HashMap<>();
		for (final Map<String, Object> result : results) {
			final Integer variableId = (Integer) result.get("variableId");
			final String value = (String) result.get("value");
			geoProperties.put(variableId, value);
		}
		return geoProperties;
	}
}
