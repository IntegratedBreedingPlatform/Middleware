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
import com.google.common.collect.Lists;
import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.dms.GeolocationProperty;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
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

	@SuppressWarnings("unchecked")
	public String getValueOfTrialInstance(final int datasetId, final int typeId, final Integer trialInstance) {
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

	public void deletePropertiesInDataset(final int datasetId, final List<Integer> variableIds) {
		this.deleteValues(datasetId, Collections.<Integer>emptyList(), variableIds);
	}

	public void deletePropertiesInDatasetInstances(final int datasetId, final List<Integer> instanceNumbers, final List<Integer> variableIds) {
		this.deleteValues(datasetId, instanceNumbers, variableIds);
	}

	private void deleteValues(final int projectId, final List<Integer> instanceNumbers, final List<Integer> variableIds) {
		// Please note we are manually flushing because non hibernate based deletes and updates causes the Hibernate session to get out of synch with
		// underlying database. Thus flushing to force Hibernate to synchronize with the underlying database before the delete
		// statement
		this.getSession().flush();

		final StringBuilder sql1 = new StringBuilder().append("Delete ngp.* FROM nd_geolocationprop ngp "
				+ "INNER JOIN nd_geolocation g on g.nd_geolocation_id = ngp.nd_geolocation_id "
				+ "INNER JOIN nd_experiment e ON e.nd_geolocation_id = g.nd_geolocation_id "
				+ "INNER JOIN project p ON p.project_id = e.project_id "
				+ "WHERE (p.study_id = :datasetId OR p.project_id = :datasetId) AND ngp.type_id IN (:variableIds) ");
		if (!CollectionUtils.isEmpty(instanceNumbers)) {
			sql1.append(" AND g.description IN (:instanceNumbers)");
		}

		final SQLQuery sqlQuery1 = this.getSession().createSQLQuery(sql1.toString());
		sqlQuery1.setParameter("datasetId", projectId);
		sqlQuery1.setParameterList("variableIds", variableIds);
		if (!CollectionUtils.isEmpty(instanceNumbers)) {
			sqlQuery1.setParameterList("instanceNumbers", instanceNumbers);
		}
		sqlQuery1.executeUpdate();
	}

	public Map<String, String> getGeolocationPropsAndValuesByGeolocation(final Integer geolocationId, final List<Integer> excludedVariableIds) {
		Preconditions.checkNotNull(geolocationId);
		final Map<String, String> geoProperties = new HashMap<>();
		final List<Integer> excludedIds = new ArrayList<>(excludedVariableIds);
		excludedIds.add(TermId.NO_OF_COLS_IN_REPS.getId());
		excludedIds.add(TermId.LOCATION_ID.getId());
		excludedIds.add(TermId.TRIAL_LOCATION.getId());

		final StringBuilder sql =
			new StringBuilder().append("SELECT  ").append("    cv.definition as name, geo.value as value ").append("FROM ")
				.append("    nd_geolocationprop geo ").append("        INNER JOIN ")
				.append("    cvterm cv ON (cv.cvterm_id = geo.type_id) ").append("WHERE ").append("    geo.nd_geolocation_id = :geolocationId ")
				.append("        AND geo.type_id NOT IN (:excludedIds) ");
		try {
			final Query query =
				this.getSession().createSQLQuery(sql.toString()).addScalar("name").addScalar("value").setParameter("geolocationId", geolocationId)
					.setParameterList("excludedIds", excludedIds);
			final List<Object> results = query.list();
			for (final Object obj : results) {
				final Object[] row = (Object[]) obj;
				geoProperties.put((String) row[0], row[1] == null ? "" : (String) row[1]);
			}
			return geoProperties;
		} catch (final MiddlewareQueryException e) {
			final String message = "Error with getGeolocationPropsAndValuesByGeolocation() query from geolocationId: " + geolocationId
				+ " and excludedIds: " + excludedIds;
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

	public String getGeolocationPropValue(final int stdVarId, final int datasetId) {
		try {
			final StringBuilder sql =
				new StringBuilder().append("SELECT distinct value ").append("FROM nd_experiment e ")
					.append("INNER JOIN nd_geolocationprop gp ON gp.nd_geolocation_id = e.nd_geolocation_id ")
					.append("WHERE e.project_id = :projectId AND gp.type_id = :stdVarId ORDER BY e.nd_geolocation_id ");
			final SQLQuery query = this.getSession().createSQLQuery(sql.toString());
			query.setParameter("projectId", datasetId);
			query.setParameter("stdVarId", stdVarId);
			return (String) query.uniqueResult();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				"Error at getGeolocationPropValue=" + stdVarId + " query on GeolocationPropertyDao: " + e.getMessage(), e);
		}
	}


	public List<GeolocationProperty> getByGeolocation(final Integer geolocationId) {
		final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
		criteria.add(Restrictions.eq("geolocation.locationId", geolocationId));
		return criteria.list();
	}

	public List<MeasurementVariable> getEnvironmentDetailVariablesByGeoLocationIdAndVariableIds(final Integer geolocationId, final List<Integer> variableIds) {
		final List<MeasurementVariable> studyVariables = new ArrayList<>();
		final List<Integer> standardEnvironmentFactors = Lists.newArrayList(
			TermId.LOCATION_ID.getId(),
			TermId.TRIAL_INSTANCE_FACTOR.getId(),
			TermId.EXPERIMENT_DESIGN_FACTOR.getId());
		try{
			final SQLQuery query =
				this.getSession().createSQLQuery("SELECT ispcvt.name as name, ispcvt.definition as definition, "
					+ "		cvt_scale.name AS scaleName, IFNULL(categoricalVar.name, gprop.value) AS value, cvt_scale.cvterm_id AS scaleId, "
					+ "		ispcvt.cvterm_id AS variableId FROM nd_geolocationprop gprop "
					+ "		INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = gprop.type_id AND ispcvt.cvterm_id in (:variableIds) "
					+ "		INNER JOIN cvterm_relationship cvt_rel ON cvt_rel.subject_id = ispcvt.cvterm_id AND cvt_rel.type_id = " + TermId.HAS_SCALE.getId()
					+ "		INNER JOIN cvterm cvt_scale ON cvt_scale.cvterm_id = cvt_rel.object_id "
					+ "		INNER JOIN nd_geolocation gl ON gprop.nd_geolocation_id = gl.nd_geolocation_id "
					+ "		LEFT JOIN cvterm categoricalVar ON categoricalVar.cvterm_id = gprop.value "
					+ "	    WHERE gl.nd_geolocation_id = :geolocationId AND ispcvt.cvterm_id NOT IN (:standardEnvironmentFactors) ;");
			query.addScalar("name", new StringType());
			query.addScalar("definition", new StringType());
			query.addScalar("scaleName", new StringType());
			query.addScalar("value", new StringType());
			query.addScalar("scaleId", new IntegerType());
			query.addScalar("variableId", new IntegerType());
			query.setParameterList("variableIds", variableIds);
			query.setParameter("geolocationId", geolocationId);
			query.setParameterList("standardEnvironmentFactors", standardEnvironmentFactors);

			final List<Object> results = query.list();
			for(final Object result: results) {

				final Object[] row = (Object[]) result;
				final MeasurementVariable measurementVariable = new MeasurementVariable();
				measurementVariable.setName((row[0] instanceof String) ? (String) row[0] : null);
				measurementVariable.setDescription((row[1] instanceof String) ? (String) row[1] : null);
				measurementVariable.setScale((row[2] instanceof String) ? (String) row[2] : null);
				measurementVariable.setValue((row[3] instanceof String) ? (String) row[3] : null);
				measurementVariable.setScaleId((row[4] instanceof Integer) ? (Integer) row[4] : null);
				measurementVariable.setTermId((row[5] instanceof Integer) ? (Integer) row[5] : 0);
				studyVariables.add(measurementVariable);
			}
		} catch (final MiddlewareQueryException e) {
			final String message = "Error with getEnvironmentConditionVariablesByGeoLocationIdAndVariableIds() query from geolocationId: " + geolocationId
				+ " and variableIds: " + variableIds;
			GeolocationPropertyDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
		return studyVariables;
	}

}
