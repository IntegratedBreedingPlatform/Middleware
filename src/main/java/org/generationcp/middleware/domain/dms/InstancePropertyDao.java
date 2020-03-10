package org.generationcp.middleware.domain.dms;

import com.google.common.base.Preconditions;
import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.dms.ExperimentProperty;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.hibernate.type.StringType;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InstancePropertyDao extends GenericDAO<ExperimentProperty, Integer> {

	public Map<Integer, String> getInstanceVariablesMap(final Integer datasetId, final Integer instanceDbId) {
		Preconditions.checkNotNull(datasetId);
		final String sql = "SELECT "
			+ "    xp.type_id as variableId, "
			+ "	   xp.value as value "
			+ "FROM "
			+ "    nd_experiment e "
			+ "        INNER JOIN "
			+ "    nd_experimentprop xp ON xp.nd_experiment_id = e.nd_experiment_id "
			+ "WHERE "
			+ "		e.project_id = :datasetId and e.type_id = 1020 "
			+ "		and e.nd_experiment_id = :instanceDbId";

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

	@SuppressWarnings("unchecked")
	public String getVariableValueForTrialInstance(final int datasetId, final int variableId, final Integer trialInstance) {
		try {
			final StringBuilder sql =
				new StringBuilder().append("SELECT xp.value FROM nd_experimentprop xp ")
					.append(" INNER JOIN nd_experiment e ON e.nd_experiment_id = xp.nd_experiment_id AND e.type_id = 1020 ")
					.append(" WHERE e.observation_unit_no = :instanceNumber AND xp.type_id = :variableId  AND e.project_id = :datasetId");

			final SQLQuery query = this.getSession().createSQLQuery(sql.toString());
			query.setParameter("instanceNumber", trialInstance);
			query.setParameter("variableId", variableId);
			query.setParameter("datasetId", datasetId);
			return (String) query.uniqueResult();

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				"Error at getVariableValueForTrialInstance=" + datasetId + " query on InstancePropertyDao: " + e.getMessage(), e);
		}
	}

	public String getVariableValue(final int stdVarId, final int datasetId) {
		try {
			final StringBuilder sql =
				new StringBuilder().append("SELECT distinct value ").append("FROM nd_experiment e ")
					.append("INNER JOIN nd_experimentprop xp ON xp.nd_experiment_id = e.nd_experiment_id ")
					.append("WHERE e.project_id = :projectId AND xp.type_id = :stdVarId ORDER BY e.observation_unit_no ");
			final SQLQuery query = this.getSession().createSQLQuery(sql.toString());
			query.setParameter("projectId", datasetId);
			query.setParameter("stdVarId", stdVarId);
			return (String) query.uniqueResult();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				"Error at getGeolocationPropValue=" + stdVarId + " query on InstancePropertyDao: " + e.getMessage(), e);
		}
	}

	public void deletePropertiesInDataset(final int datasetId, final List<Integer> variableIds) {
		this.deleteValues(datasetId, Collections.emptyList(), variableIds);
	}

	public void deletePropertiesInDatasetInstances(final int datasetId, final List<Integer> instanceNumbers, final List<Integer> variableIds) {
		this.deleteValues(datasetId, instanceNumbers, variableIds);
	}

	private void deleteValues(final int projectId, final List<Integer> instanceNumbers, final List<Integer> variableIds) {
		// Please note we are manually flushing because non hibernate based deletes and updates causes the Hibernate session to get out of synch with
		// underlying database. Thus flushing to force Hibernate to synchronize with the underlying database before the delete
		// statement
		this.getSession().flush();

		final StringBuilder sql1 = new StringBuilder().append("Delete xp.* FROM nd_experimentprop xp "
			+ "INNER JOIN nd_experiment e ON e.nd_experiment_id = xp.nd_experiment_id "
			+ "INNER JOIN project p ON p.project_id = e.project_id "
			+ "WHERE (p.study_id = :datasetId OR p.project_id = :datasetId) AND xp.type_id IN (:variableIds) ");
		if (!CollectionUtils.isEmpty(instanceNumbers)) {
			sql1.append(" AND e.observation_unit_no IN (:instanceNumbers)");
		}

		final SQLQuery sqlQuery1 = this.getSession().createSQLQuery(sql1.toString());
		sqlQuery1.setParameter("datasetId", projectId);
		sqlQuery1.setParameterList("variableIds", variableIds);
		if (!CollectionUtils.isEmpty(instanceNumbers)) {
			sqlQuery1.setParameterList("instanceNumbers", instanceNumbers);
		}
		sqlQuery1.executeUpdate();
	}

	public Map<String, String> getInstanceVariableNameValuesMap(final Integer instanceId) {
		Preconditions.checkNotNull(instanceId);
		final Map<String, String> geoProperties = new HashMap<>();
		final StringBuilder sql =
			new StringBuilder().append("SELECT  ").append("    cv.definition as name, xp.value as value ").append("FROM ")
				.append("    nd_experimentprop xp ").append("        INNER JOIN ")
				.append("    cvterm cv ON (cv.cvterm_id = xp.type_id) ").append("WHERE ").append("    xp.nd_experiment_id = :instanceId ")
				.append("        AND xp.type_id NOT IN (8371, 8190, 8070, 8180) ");
		try {
			final Query query =
				this.getSession().createSQLQuery(sql.toString()).addScalar("name").addScalar("value").setParameter("instanceId",
					instanceId);
			final List<Object> results = query.list();
			for (final Object obj : results) {
				final Object[] row = (Object[]) obj;
				geoProperties.put((String) row[0], (String) row[1]);
			}
			return geoProperties;
		} catch (final MiddlewareQueryException e) {
			final String message = "Error with getInstanceVariableNameValuesMap() query from instanceId: " + instanceId;
			throw new MiddlewareQueryException(message, e);
		}
	}

	public List<MeasurementVariable> getInstanceDetailVariablesExcludeVariableIds(final Integer instanceId, final List<Integer> excludedVariableIds) {
		List<MeasurementVariable> studyVariables = new ArrayList<>();

		try{
			final SQLQuery query =
				this.getSession().createSQLQuery("SELECT ispcvt.name as name, ispcvt.definition as definition, "
					+ "		cvt_scale.name AS scaleName, xprop.value AS value "
					+ "		FROM nd_experimentprop xprop "
					+ "		INNER JOIN nd_experiment exp ON exp.nd_experiment_id = xprop.nd_experiment_id AND exp.type_id = 1020 "
					+ "		INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = xprop.type_id "
					+ "		INNER JOIN cvterm_relationship cvt_rel ON cvt_rel.subject_id = ispcvt.cvterm_id AND cvt_rel.type_id = " + TermId.HAS_SCALE.getId()
					+ "		INNER JOIN cvterm cvt_scale ON cvt_scale.cvterm_id = cvt_rel.object_id "
					+ "	    WHERE exp.nd_experiment_id = :instanceId AND ispcvt.cvterm_id NOT IN (:excludedVariableIds) ;");
			query.addScalar("name", new StringType());
			query.addScalar("definition", new StringType());
			query.addScalar("scaleName", new StringType());
			query.addScalar("value", new StringType());
			query.setParameter("instanceId", instanceId);
			query.setParameterList("excludedVariableIds", excludedVariableIds);

			final List<Object> results = query.list();
			for(Object result: results) {

				final Object[] row = (Object[]) result;
				final MeasurementVariable measurementVariable = new MeasurementVariable();
				measurementVariable.setName((row[0] instanceof String) ? (String) row[0] : null);
				measurementVariable.setDescription((row[1] instanceof String) ? (String) row[1] : null);
				measurementVariable.setScale((row[2] instanceof String) ? (String) row[2] : null);
				measurementVariable.setValue((row[3] instanceof String) ? (String) row[3] : null);
				studyVariables.add(measurementVariable);
			}
		} catch (final MiddlewareQueryException e) {
			final String message = "Error with getInstanceDetailVariablesExcludeVariableIds() query from instanceId: " + instanceId
				+ " and excluded variableIds: " + excludedVariableIds;
			throw new MiddlewareQueryException(message, e);
		}
		return studyVariables;
	}



}
