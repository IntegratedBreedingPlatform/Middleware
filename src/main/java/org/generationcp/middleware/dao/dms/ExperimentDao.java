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

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.domain.sample.SampleDTO;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.service.api.dataset.ObservationUnitData;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.generationcp.middleware.service.api.study.MeasurementVariableDto;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * DAO class for {@link ExperimentModel}.
 */
public class ExperimentDao extends GenericDAO<ExperimentModel, Integer> {

	private static final String ND_EXPERIMENT_ID = "ndExperimentId";
	private static final String OBS_UNIT_ID = "OBS_UNIT_ID";
	public static final String SQL_GET_SAMPLED_OBSERVATION_BY_STUDY = " SELECT " +
		" experiment.nd_experiment_id, " +
		" sample.sample_id," +
		" sample.sample_no " +
		" FROM nd_experiment experiment " +
		" INNER JOIN project p ON (p.project_id = experiment.project_id) " +
		" INNER JOIN sample sample ON (sample.nd_experiment_id = experiment.nd_experiment_id) " +
		" WHERE p.study_id = :studyId AND p.dataset_type_id = " + DatasetTypeEnum.PLOT_DATA.getId();

	private static final Logger LOG = LoggerFactory.getLogger(ExperimentDao.class);

	private static final String COUNT_EXPERIMENT_BY_VARIABLE_IN_PROJECTPROP = "SELECT count(e.nd_experiment_id) "
		+ "FROM nd_experiment e INNER JOIN projectprop pp ON pp.project_id = e.project_id "
		+ "AND pp.value = :variableId";

	private static final String COUNT_EXPERIMENT_BY_VARIABLE_IN_GEOLOCATION = "SELECT count(e.nd_experiment_id) "
		+ "FROM nd_experiment e INNER JOIN nd_geolocation g ON g.nd_geolocation_id = e.nd_geolocation_id "
		+ "WHERE (" + TermId.TRIAL_INSTANCE_FACTOR.getId() + " = :variableId AND g.description IS NOT NULL)  OR (" + TermId.LATITUDE.getId()
		+ " = :variableId AND g.latitude IS NOT NULL) "
		+ "OR (" + TermId.LONGITUDE.getId() + "= :variableId AND g.longitude IS NOT NULL) OR (" + TermId.GEODETIC_DATUM.getId()
		+ " = :variableId AND g.geodetic_datum IS NOT NULL) "
		+ "OR (" + TermId.ALTITUDE.getId() + " = :variableId AND g.altitude IS NOT NULL) ";

	private static final String COUNT_EXPERIMENT_BY_VARIABLE_IN_GEOLOCATIONPROP = "SELECT count(e.nd_experiment_id) "
		+ "FROM nd_experiment e INNER JOIN nd_geolocationprop gp ON gp.nd_geolocation_id = e.nd_geolocation_id "
		+ "WHERE gp.type_id = :variableId AND gp.value IS NOT NULL";

	private static final String COUNT_EXPERIMENT_BY_VARIABLE_IN_EXPERIMENTPROP = "SELECT count(e.nd_experiment_id) "
		+ "FROM nd_experiment e INNER JOIN nd_experimentprop ep ON ep.nd_experiment_id = e.nd_experiment_id "
		+ "WHERE ep.type_id = :variableId AND ep.value IS NOT NULL";

	private static final String COUNT_EXPERIMENT_BY_VARIABLE_IN_STOCK = "SELECT count(e.nd_experiment_id) "
		+ "FROM nd_experiment e INNER JOIN stock s ON s.stock_id = e.stock_id "
		+ "WHERE (" + TermId.ENTRY_NO.getId() + "= :variableId AND s.uniquename IS NOT NULL)  OR (" + TermId.GID.getId()
		+ " = :variableId AND s.dbxref_id IS NOT NULL) "
		+ "OR (" + TermId.DESIG.getId() + " = :variableId AND s.name IS NOT NULL) OR (" + TermId.ENTRY_CODE.getId()
		+ " = :variableId AND s.value IS NOT NULL)";

	private static final String COUNT_EXPERIMENT_BY_VARIABLE_IN_STOCKPROP = "SELECT count(e.nd_experiment_id) "
		+ "FROM nd_experiment e INNER JOIN stockprop sp ON sp.stock_id = e.stock_id "
		+ "WHERE sp.type_id = :variableId AND sp.value IS NOT NULL";

	private static final String COUNT_EXPERIMENT_BY_VARIABLE_IN_PHENOTYPE = "SELECT count(e.nd_experiment_id) "
		+ "FROM nd_experiment e  INNER JOIN phenotype p ON p.nd_experiment_id = e.nd_experiment_id "
		+ "AND p.observable_id = :variableId AND (p.value IS NOT NULL  OR p.cvalue_id IS NOT NULL)";

	@SuppressWarnings("unchecked")
	public List<Integer> getExperimentIdsByGeolocationIds(final Collection<Integer> geolocationIds) {
		try {
			if (geolocationIds != null && !geolocationIds.isEmpty()) {
				final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
				criteria.add(Restrictions.in("geoLocation.locationId", geolocationIds));
				criteria.setProjection(Projections.property(ND_EXPERIMENT_ID));

				return criteria.list();
			}
		} catch (final HibernateException e) {
			final String message =
				"Error at getExperimentIdsByGeolocationIds=" + geolocationIds + " query at ExperimentDao: " + e.getMessage();
			ExperimentDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
		return new ArrayList<>();
	}

	public ExperimentModel getExperimentByProjectIdAndLocation(final Integer projectId, final Integer locationId) {
		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("project.projectId", projectId));
			criteria.add(Restrictions.eq("geoLocation.locationId", locationId));
			final List<ExperimentModel> list = criteria.list();
			if (list != null && !list.isEmpty()) {
				return list.get(0);
			}
		} catch (final HibernateException e) {
			final String message = "Error at getExperimentByProjectIdAndLocation=" + projectId + "," + locationId
				+ " query at ExperimentDao: " + e.getMessage();
			ExperimentDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<ExperimentModel> getExperimentsByProjectIds(final List<Integer> projectIds) {
		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.in("project.projectId", projectIds));
			return criteria.list();

		} catch (final HibernateException e) {
			final String message = "Error at getExperimentsByProjectIds query at ExperimentDao: " + e.getMessage();
			ExperimentDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public boolean hasFieldmap(final int datasetId) {
		try {
			final String sql =
				"SELECT COUNT(eprop.value) " + " FROM nd_experiment ep "
					+ " INNER JOIN nd_experimentprop eprop ON eprop.nd_experiment_id = ep.nd_experiment_id "
					+ "    AND eprop.type_id = " + TermId.RANGE_NO.getId() + " AND eprop.value <> '' " + " WHERE ep.project_id = "
					+ datasetId + "  LIMIT 1 ";
			final SQLQuery query = this.getSession().createSQLQuery(sql);
			final BigInteger count = (BigInteger) query.uniqueResult();
			return count != null && count.longValue() > 0;

		} catch (final HibernateException e) {
			final String message = "Error at hasFieldmap=" + datasetId + " query at ExperimentDao: " + e.getMessage();
			ExperimentDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	@SuppressWarnings("unchecked")
	// Should be renamed to getInstanceIds
	public List<Integer> getLocationIdsOfStudy(final int studyId) {
		try {
			final String sql =
				"SELECT DISTINCT e.nd_geolocation_id " + " FROM nd_experiment e "
					+ " INNER JOIN project p ON p.project_id = e.project_id "
					+ " WHERE p.study_id = :studyId ";

			final SQLQuery query = this.getSession().createSQLQuery(sql);
			query.setParameter("studyId", studyId);
			return query.list();

		} catch (final HibernateException e) {
			final String message = "Error at getLocationIdsOfStudy=" + studyId + " query at ExperimentDao: " + e.getMessage();
			ExperimentDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getLocationIdsOfStudyWithFieldmap(final int studyId) {
		try {
			final String sql =
				"SELECT DISTINCT e.nd_geolocation_id " + " FROM nd_experiment e "
					+ " INNER JOIN project p ON p.project_id = e.project_id "
					+ " WHERE p.study_id = :studyId "
					+ " AND EXISTS (SELECT 1 FROM nd_experimentprop eprop " + "   WHERE eprop.type_id = "
					+ TermId.COLUMN_NO.getId() + "     AND eprop.nd_experiment_id = e.nd_experiment_id  AND eprop.value <> '') ";

			final SQLQuery query = this.getSession().createSQLQuery(sql);
			query.setParameter("studyId", studyId);
			return query.list();

		} catch (final HibernateException e) {
			final String message = "Error at getLocationIdsOfStudyWithFieldmap=" + studyId + " query at ExperimentDao: " + e.getMessage();
			ExperimentDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public void deleteExperimentsByIds(final List<Integer> experimentIdList) {
		final String experimentIds = StringUtils.join(experimentIdList, ",");

		try {
			// Please note we are manually flushing because non hibernate based deletes and updates causes the Hibernate session to get out of synch with
			// underlying database. Thus flushing to force Hibernate to synchronize with the underlying database before the delete
			// statement
			this.getSession().flush();

			// Delete phenotypes first because the foreign key with nd_experiment
			Query statement =
				this.getSession()
					.createSQLQuery("DELETE pheno FROM nd_experiment e"
						+ "  LEFT JOIN phenotype pheno ON pheno.nd_experiment_id = e.nd_experiment_id"
						+ "  where e.nd_experiment_id in (" + experimentIds + ") ");
			statement.executeUpdate();

			// Delete experiments
			statement =
				this.getSession().createSQLQuery("delete e, eprop " + "from nd_experiment e "
					+ "left join nd_experimentprop eprop on eprop.nd_experiment_id = e.nd_experiment_id "
					+ "where e.nd_experiment_id in (" + experimentIds + ") ");
			statement.executeUpdate();
		} catch (final HibernateException e) {
			final String message = "Error at deleteExperimentsByIds=" + experimentIds + " query at ExperimentDao: " + e.getMessage();
			ExperimentDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public void deleteExperimentsByStudy(final int datasetId) {

		try {
			// Please note we are manually flushing because non hibernate based deletes and updates causes the Hibernate session to get out of synch with
			// underlying database. Thus flushing to force Hibernate to synchronize with the underlying database before the delete
			// statement
			this.getSession().flush();

			// Delete phenotypes first because the foreign key with nd_experiment
			Query statement =
				this.getSession()
					.createSQLQuery("DELETE pheno FROM nd_experiment e"
						+ "  LEFT JOIN phenotype pheno ON pheno.nd_experiment_id = e.nd_experiment_id"
						+ "  WHERE e.project_id = :datasetId ");
			statement.setParameter("datasetId", datasetId);
			statement.executeUpdate();

			// Delete experiments
			statement =
				this.getSession()
					.createSQLQuery("DELETE e, eprop " + "FROM nd_experiment e "
						+ "LEFT JOIN nd_experimentprop eprop ON eprop.nd_experiment_id = e.nd_experiment_id "
						+ "WHERE e.project_id = :datasetId ");
			statement.setParameter("datasetId", datasetId);
			statement.executeUpdate();
		} catch (final HibernateException e) {
			final String message = "Error at deleteExperimentsByStudy=" + datasetId + " query at ExperimentDao: " + e.getMessage();
			ExperimentDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public void deleteTrialExperimentsOfStudy(final int datasetId) {

		try {

			// Please note we are manually flushing because non hibernate based deletes and updates causes the Hibernate session to get out of synch with
			// underlying database. Thus flushing to force Hibernate to synchronize with the underlying database before the delete
			// statement
			this.getSession().flush();

			// Delete phenotypes first because the foreign key with nd_experiment
			Query statement =
				this.getSession()
					.createSQLQuery("DELETE pheno FROM nd_experiment e"
						+ "  LEFT JOIN phenotype pheno ON pheno.nd_experiment_id = e.nd_experiment_id"
						+ "  WHERE e.project_id = :datasetId ");
			statement.setParameter("datasetId", datasetId);
			statement.executeUpdate();

			// Delete experiments
			statement =
				this.getSession()
					.createSQLQuery(
						"DELETE g, gp, e, eprop " + "FROM nd_geolocation g "
							+ "LEFT JOIN nd_geolocationprop gp on g.nd_geolocation_id = gp.nd_geolocation_id "
							+ "LEFT join nd_experiment e on g.nd_geolocation_id = e.nd_geolocation_id "
							+ "LEFT JOIN nd_experimentprop eprop ON eprop.nd_experiment_id = e.nd_experiment_id "
							+ "WHERE e.project_id = :datasetId ");
			statement.setParameter("datasetId", datasetId);

			statement.executeUpdate();
		} catch (final HibernateException e) {
			final String message = "Error at deleteTrialExperimentsOfStudy=" + datasetId + " query at ExperimentDao: " + e.getMessage();
			ExperimentDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public boolean checkIfAnyLocationIDsExistInExperiments(final int dataSetId, final List<Integer> locationIds) {

		try {
			final String sql =
				"SELECT count(*) FROM nd_experiment exp "
					+ "WHERE exp.nd_geolocation_id in (:locationIds) " + "AND exp.project_id = :dataSetId ";

			final SQLQuery query = this.getSession().createSQLQuery(sql);
			query.setParameterList("locationIds", locationIds);
			query.setParameter("dataSetId", dataSetId);

			long count = 0L;
			final Object obj = query.uniqueResult();
			if (obj != null) {
				count = ((Number) obj).longValue();
			}

			return count != 0;

		} catch (final HibernateException e) {
			final String message = "Error at checkIfLocationIDsExistInExperiments=" + locationIds + "," + dataSetId + ","
				+ " query at ExperimentDao: " + e.getMessage();
			ExperimentDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}

	}

	public Map<Integer, List<SampleDTO>> getExperimentSamplesDTOMap(final Integer studyId) {
		final Map<Integer, List<SampleDTO>> map = new HashMap<>();
		try {
			final SQLQuery query = this.getSession().createSQLQuery(SQL_GET_SAMPLED_OBSERVATION_BY_STUDY);
			query.setParameter("studyId", studyId);
			final List results = query.list();

			for (final Object o : results) {
				final Object[] result = (Object[]) o;
				if (result != null) {
					final SampleDTO sampleDTO = new SampleDTO();
					sampleDTO.setSampleId((Integer) result[1]);
					sampleDTO.setSampleNumber((Integer) result[2]);
					final Integer experimentId = (Integer) result[0];
					if (map.containsKey(experimentId)) {
						map.get(experimentId).add(sampleDTO);
					} else {
						final List<SampleDTO> sampleObservationUnitDTOS = new ArrayList<>();
						sampleObservationUnitDTOS.add(sampleDTO);
						map.put(experimentId, sampleObservationUnitDTOS);
					}
				}
			}
		} catch (final HibernateException e) {
			ExperimentDao.LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException(e.getMessage(), e);
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	public List<ExperimentModel> getExperiments(final int projectId, final int typeId, final int start, final int numOfRows) {
		try {
			final DmsProject project = new DmsProject();
			project.setProjectId(projectId);
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("typeId", typeId));
			criteria.add(Restrictions.eq("project", project));
			criteria.setMaxResults(numOfRows);
			criteria.setFirstResult(start);
			return criteria.list();
		} catch (final HibernateException e) {
			final String message = "Error at getExperiments=" + projectId + ", " + typeId;
			ExperimentDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<ExperimentModel> getExperiments(final int projectId, final List<TermId> types, final int start, final int numOfRows,
		final boolean firstInstance) {
		try {

			final List<Integer> lists = new ArrayList<>();
			for (final TermId termId : types) {
				lists.add(termId.getId());
			}

			final StringBuilder queryString = new StringBuilder();
			queryString.append("select distinct exp from ExperimentModel as exp ");
			queryString.append("left outer join exp.properties as plot with plot.typeId IN (8200,8380) ");
			queryString.append("left outer join exp.properties as rep with rep.typeId = 8210 ");
			queryString.append("left outer join exp.stock as st ");
			queryString.append("where exp.project.projectId =:p_id and exp.typeId in (:type_ids) ");
			if (firstInstance) {
				queryString.append("and exp.geoLocation.description = 1 ");
			}
			queryString.append("order by (exp.geoLocation.description * 1) ASC, ");
			queryString.append("(plot.value * 1) ASC, ");
			queryString.append("(rep.value * 1) ASC, ");
			queryString.append("(st.uniqueName * 1) ASC, ");
			queryString.append("exp.ndExperimentId ASC");

			final Query q = this.getSession().createQuery(queryString.toString());
			q.setParameter("p_id", projectId);
			q.setParameterList("type_ids", lists);
			q.setMaxResults(numOfRows);
			q.setFirstResult(start);

			return q.list();
		} catch (final HibernateException e) {
			final String message = "Error at getExperiments=" + projectId + ", " + types;
			ExperimentDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public long count(final int dataSetId) {
		try {
			return (Long) this.getSession().createQuery("select count(*) from ExperimentModel where project_id = " + dataSetId)
				.uniqueResult();
		} catch (final HibernateException e) {
			final String message = "Error at countExperiments=" + dataSetId;
			ExperimentDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public int getExperimentIdByLocationIdStockId(final int projectId, final Integer locationId, final Integer stockId) {
		try {
			final String sql =
				"SELECT exp.nd_experiment_id " + "FROM nd_experiment exp "
					+ " WHERE exp.project_id = " + projectId
					+ " AND exp.nd_geolocation_id = " + locationId + " AND exp.type_id = 1170 " + " AND exp.stock_id = "
					+ stockId;

			final SQLQuery statement = this.getSession().createSQLQuery(sql);
			final Integer returnVal = (Integer) statement.uniqueResult();

			if (returnVal == null) {
				return 0;
			} else {
				return returnVal.intValue();
			}

		} catch (final HibernateException e) {
			final String message = "Error in getExperimentIdByLocationIdStockId=" + projectId;
			ExperimentDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	@SuppressWarnings("unchecked")
	public Integer getExperimentIdByProjectId(final int projectId) {
		try {
			final DmsProject project = new DmsProject();
			project.setProjectId(projectId);
			final Criteria criteria = this.getSession().createCriteria(ExperimentModel.class);
			criteria.add(Restrictions.eq("project", project));
			criteria.setProjection(Projections.property(ND_EXPERIMENT_ID));
			final List<Integer> list = criteria.list();
			if (list != null && !list.isEmpty()) {
				return list.get(0);
			} else {
				return null;
			}
		} catch (final HibernateException e) {
			final String message = "Error at getExperimentIdByProjectId=" + projectId;
			ExperimentDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getExperimentIdsByStockIds(final Collection<Integer> stockIds) {
		try {
			if (stockIds != null && !stockIds.isEmpty()) {
				final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
				criteria.add(Restrictions.in("stock.stockId", stockIds));
				criteria.setProjection(Projections.property(ND_EXPERIMENT_ID));

				return criteria.list();
			}
		} catch (final HibernateException e) {
			final String error = "Error in getExperimentIdsByStockIds=" + stockIds + " query in ExperimentDao: " + e.getMessage();
			ExperimentDao.LOG.error(error);
			throw new MiddlewareQueryException(error, e);
		}
		return new ArrayList<>();
	}

	@SuppressWarnings("unchecked")
	public Map<Integer, Set<Integer>> getEnvironmentsOfGermplasms(final Set<Integer> gids, final String programUUID) {
		final Map<Integer, Set<Integer>> germplasmEnvironments = new HashMap<>();

		if (gids.isEmpty()) {
			return germplasmEnvironments;
		}

		for (final Integer gid : gids) {
			germplasmEnvironments.put(gid, new HashSet<Integer>());
		}

		final String sql = "SELECT DISTINCT s.dbxref_id, e.nd_geolocation_id " + "FROM nd_experiment e "
			+ "     INNER JOIN stock s ON e.stock_id = s.stock_id AND s.dbxref_id IN (:gids) ";
		final StringBuilder sb = new StringBuilder();
		sb.append(sql);
		if (programUUID != null) {
			sb.append("INNER JOIN project p ON p.project_id = e.project_id and p.program_uuid = :programUUID ");
		}
		sb.append(" ORDER BY s.dbxref_id ");
		try {
			final Query query = this.getSession().createSQLQuery(sb.toString());
			query.setParameterList("gids", gids);
			if (programUUID != null) {
				query.setParameter("programUUID", programUUID);
			}
			final List<Object[]> result = query.list();

			for (final Object[] row : result) {
				final Integer gId = (Integer) row[0];
				final Integer environmentId = (Integer) row[1];

				final Set<Integer> gidEnvironments = germplasmEnvironments.get(gId);
				gidEnvironments.add(environmentId);
				germplasmEnvironments.remove(gId);
				germplasmEnvironments.put(gId, gidEnvironments);
			}

		} catch (final HibernateException e) {
			final String error = "Error at getEnvironmentsOfGermplasms(programUUID=" + programUUID + " ,gids=" + gids
				+ ") query on ExperimentDao: " + e.getMessage();
			ExperimentDao.LOG.error(error);
			throw new MiddlewareQueryException(error, e);
		}

		return germplasmEnvironments;

	}

	public long countStocksByDatasetId(final int datasetId) {

		final StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(DISTINCT e.stock_id) FROM nd_experiment e ")
			.append(" WHERE e.project_id = :datasetId");

		try {
			final SQLQuery query = this.getSession().createSQLQuery(sql.toString());
			query.setParameter("datasetId", datasetId);
			final BigInteger count = (BigInteger) query.uniqueResult();
			return count.longValue();

		} catch (final HibernateException e) {
			final String error = "Error at countStocksByDatasetId=" + datasetId + " query at ExperimentDao: " + e.getMessage();
			ExperimentDao.LOG.error(error);
			throw new MiddlewareQueryException(error, e);
		}
	}

	public boolean isValidExperiment(final Integer projectId, final Integer experimentId) {
		final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
		criteria.add(Restrictions.eq("project.projectId", projectId));
		criteria.add(Restrictions.eq("ndExperimentId", experimentId));
		criteria.setProjection(Projections.property(ND_EXPERIMENT_ID));
		final Integer id = (Integer) criteria.uniqueResult();
		return id != null;
	}

	public boolean areAllInstancesExistInDataset(final int datasetId, final Set<Integer> instanceIds) {

		final StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(DISTINCT e.nd_geolocation_id) FROM nd_experiment e ")
			.append(" WHERE e.project_id = :datasetId and e.nd_geolocation_id in (:instanceIds)");

		try {

			final SQLQuery query = this.getSession().createSQLQuery(sql.toString());
			query.setParameter("datasetId", datasetId);
			query.setParameterList("instanceIds", instanceIds);

			final BigInteger count = (BigInteger) query.uniqueResult();
			return count.intValue() == instanceIds.size();

		} catch (final HibernateException e) {
			final String error =
				"Error at areAllInstancesExistInDataset=" + datasetId + "," + instanceIds + " query at ExperimentDao: " + e.getMessage();
			ExperimentDao.LOG.error(error);
			throw new MiddlewareQueryException(error, e);
		}
	}

	private void addScalarForTraits(
		final List<MeasurementVariableDto> selectionMethodsAndTraits, final SQLQuery createSQLQuery, final Boolean addStatus) {
		for (final MeasurementVariableDto measurementVariable : selectionMethodsAndTraits) {
			createSQLQuery.addScalar(measurementVariable.getName()); // Value
			createSQLQuery.addScalar(measurementVariable.getName() + "_PhenotypeId", new IntegerType());
			if (addStatus) {
				createSQLQuery.addScalar(measurementVariable.getName() + "_Status");
			}
			createSQLQuery.addScalar(measurementVariable.getName() + "_CvalueId", new IntegerType());
			createSQLQuery.addScalar(measurementVariable.getName() + "_DraftValue");
			createSQLQuery.addScalar(measurementVariable.getName() + "_DraftCvalueId", new IntegerType());
		}
	}

	public List<ExperimentModel> getObservationUnits(final Integer projectId, final List<Integer> instanceIds) {
		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("project.projectId", projectId));
			criteria.add(Restrictions.in("geoLocation.locationId", instanceIds));
			return criteria.list();
		} catch (final HibernateException e) {
			final String message =
				"Error at getObservationUnits=" + projectId + "," + instanceIds + " query at ExperimentDao: " + e.getMessage();
			ExperimentDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public Map<String, ObservationUnitRow> getObservationUnitsAsMap(
		final int datasetId,
		final List<MeasurementVariable> measurementVariables, final List<String> observationUnitIds) {

		final Function<MeasurementVariable, MeasurementVariableDto> measurementVariableFullToDto =
			new Function<MeasurementVariable, MeasurementVariableDto>() {

				public MeasurementVariableDto apply(final MeasurementVariable i) {
					return new MeasurementVariableDto(i.getTermId(), i.getName());
				}
			};

		final List<MeasurementVariableDto> measurementVariableDtos = Lists.transform(measurementVariables, measurementVariableFullToDto);

		try {
			final List<Map<String, Object>> results = this.getObservationUnitsQueryResult(
				datasetId,
				measurementVariableDtos, observationUnitIds);
			return this.mapResultsToMap(results, measurementVariableDtos);
		} catch (final Exception e) {
			final String error = "An internal error has ocurred when trying to execute the operation";
			ExperimentDao.LOG.error(error);
			throw new MiddlewareException(error);
		}
	}

	private List<Map<String, Object>> getObservationUnitsQueryResult(
		final int datasetId, final List<MeasurementVariableDto> selectionMethodsAndTraits, final List<String> observationUnitIds) {

		try {
			final String observationUnitTableQuery = this.getObservationUnitsQuery(selectionMethodsAndTraits);
			final SQLQuery query = this.createQueryAndAddScalar(selectionMethodsAndTraits, observationUnitTableQuery);
			query.setParameter("datasetId", datasetId);
			query.setParameterList("observationUnitIds", observationUnitIds);

			query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
			final List<Map<String, Object>> results = query.list();
			return results;

		} catch (final Exception e) {
			final String error = "An internal error has ocurred when trying to execute the operation";
			ExperimentDao.LOG.error(error);
			throw new MiddlewareException(error);
		}
	}

	private SQLQuery createQueryAndAddScalar(
		final List<MeasurementVariableDto> selectionMethodsAndTraits, final String observationUnitTableQuery) {
		final SQLQuery query = this.getSession().createSQLQuery(observationUnitTableQuery);
		query.addScalar(ExperimentDao.OBS_UNIT_ID, new StringType());
		this.addScalarForTraits(selectionMethodsAndTraits, query, true);
		return query;
	}

	private String getObservationUnitsQuery(
		final List<MeasurementVariableDto> selectionMethodsAndTraits) {
		{

			final StringBuilder sql = new StringBuilder("SELECT nde.obs_unit_id as OBS_UNIT_ID,  ");

			final String traitClauseFormat = " MAX(IF(cvterm_variable.name = '%s', ph.value, NULL)) AS '%s'," //
				+ " MAX(IF(cvterm_variable.name = '%s', ph.phenotype_id, NULL)) AS '%s'," //
				+ " MAX(IF(cvterm_variable.name = '%s', ph.status, NULL)) AS '%s'," //
				+ " MAX(IF(cvterm_variable.name = '%s', ph.cvalue_id, NULL)) AS '%s', " //
				+ " MAX(IF(cvterm_variable.name = '%s', ph.draft_value, NULL)) AS '%s'," //
				+ " MAX(IF(cvterm_variable.name = '%s', ph.draft_cvalue_id, NULL)) AS '%s', " //
				;

			for (final MeasurementVariableDto measurementVariable : selectionMethodsAndTraits) {
				sql.append(String.format( //
					traitClauseFormat, //
					measurementVariable.getName(), //
					measurementVariable.getName(), // Value
					measurementVariable.getName(), //
					measurementVariable.getName() + "_PhenotypeId", //
					measurementVariable.getName(), //
					measurementVariable.getName() + "_Status", //
					measurementVariable.getName(), //
					measurementVariable.getName() + "_CvalueId", //
					measurementVariable.getName(), //
					measurementVariable.getName() + "_DraftValue", //
					measurementVariable.getName(), //
					measurementVariable.getName() + "_DraftCvalueId" //
				));
			}

			sql.append(" 1=1 FROM " //
				+ " nd_experiment nde " //
				+ "	LEFT JOIN phenotype ph ON nde.nd_experiment_id = ph.nd_experiment_id " //
				+ "	LEFT JOIN cvterm cvterm_variable ON cvterm_variable.cvterm_id = ph.observable_id " //
				+ " WHERE nde.project_id = :datasetId "); //
			sql.append(" AND nde.obs_unit_id IN (:observationUnitIds)"); //

			sql.append(" GROUP BY nde.obs_unit_id ");

			return sql.toString();
		}
	}

	private Map<String, ObservationUnitRow> mapResultsToMap(
		final List<Map<String, Object>> results,
		final List<MeasurementVariableDto> selectionMethodsAndTraits) {
		final Map<String, ObservationUnitRow> observationUnitRows = new HashMap<>();

		if (results != null && !results.isEmpty()) {
			for (final Map<String, Object> row : results) {

				final Map<String, ObservationUnitData> variables = new HashMap<>();

				for (final MeasurementVariableDto variable : selectionMethodsAndTraits) {
					final String status = (String) row.get(variable.getName() + "_Status");
					final ObservationUnitData observationUnitData = new ObservationUnitData( //
						(Integer) row.get(variable.getName() + "_PhenotypeId"), //
						(Integer) row.get(variable.getName() + "_CvalueId"), //
						(String) row.get(variable.getName()), // Value
						(status != null ? Phenotype.ValueStatus.valueOf(status) : null), //
						variable.getId());
					observationUnitData.setDraftValue((String) row.get(variable.getName() + "_DraftValue"));
					observationUnitData.setDraftCategoricalValueId((Integer) row.get(variable.getName() + "_DraftCvalueId"));

					variables.put(variable.getName(), observationUnitData);
				}

				final ObservationUnitRow observationUnitRow = new ObservationUnitRow();
				final String obsUnitId = (String) row.get(OBS_UNIT_ID);
				observationUnitRow.setObsUnitId(obsUnitId);
				observationUnitRow.setVariables(variables);
				observationUnitRows.put(obsUnitId, observationUnitRow);
			}
		}

		return observationUnitRows;
	}

	public ExperimentModel getByObsUnitId(final String obsUnitId) {
		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("obsUnitId", obsUnitId));
			return (ExperimentModel) criteria.uniqueResult();

		} catch (final HibernateException e) {
			final String message = "Error at getExperimentsByProjectIds query at ExperimentDao: " + e.getMessage();
			ExperimentDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public long countByObservedVariable(final int variableId, final int variableTypeId) throws MiddlewareQueryException {
		try {
			String sql = null;
			if (VariableType.STUDY_DETAIL.getId() == variableTypeId) {
				sql = ExperimentDao.COUNT_EXPERIMENT_BY_VARIABLE_IN_PROJECTPROP;
			} else if (TermId.TRIAL_INSTANCE_FACTOR.getId() == variableId || TermId.LATITUDE.getId() == variableId
				|| TermId.LONGITUDE.getId() == variableId || TermId.GEODETIC_DATUM.getId() == variableId
				|| TermId.ALTITUDE.getId() == variableId) {
				sql = ExperimentDao.COUNT_EXPERIMENT_BY_VARIABLE_IN_GEOLOCATION;
			} else if (VariableType.ENVIRONMENT_DETAIL.getId() == variableTypeId) {
				sql = ExperimentDao.COUNT_EXPERIMENT_BY_VARIABLE_IN_GEOLOCATIONPROP;
			} else if (VariableType.EXPERIMENTAL_DESIGN.getId() == variableTypeId
				|| VariableType.TREATMENT_FACTOR.getId() == variableTypeId) {
				sql = ExperimentDao.COUNT_EXPERIMENT_BY_VARIABLE_IN_EXPERIMENTPROP;
			} else if (TermId.ENTRY_NO.getId() == variableId || TermId.GID.getId() == variableId || TermId.DESIG.getId() == variableId
				|| TermId.ENTRY_CODE.getId() == variableId) {
				sql = ExperimentDao.COUNT_EXPERIMENT_BY_VARIABLE_IN_STOCK;
			} else if (VariableType.GERMPLASM_DESCRIPTOR.getId() == variableTypeId) {
				sql = ExperimentDao.COUNT_EXPERIMENT_BY_VARIABLE_IN_STOCKPROP;
			} else if (VariableType.TRAIT.getId() == variableTypeId || VariableType.ANALYSIS.getId() == variableTypeId
				|| VariableType.STUDY_CONDITION.getId() == variableTypeId || VariableType.SELECTION_METHOD.getId() == variableTypeId) {
				sql = ExperimentDao.COUNT_EXPERIMENT_BY_VARIABLE_IN_PHENOTYPE;
			}

			if (sql != null) {
				final SQLQuery query = this.getSession().createSQLQuery(sql);
				if (sql.indexOf(":variableId") > -1) {
					query.setParameter("variableId", variableId);
				}
				return ((BigInteger) query.uniqueResult()).longValue();
			}

		} catch (final HibernateException e) {
			final String message = "Error at countByObservationVariable query at ExperimentDao: " + e.getMessage();
			ExperimentDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
		return 0;
	}

	@Override
	public ExperimentModel saveOrUpdate(final ExperimentModel experiment) {
		try {
			this.generateObsUnitId(experiment);
			super.saveOrUpdate(experiment);
			return experiment;
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error in saveOrUpdate(ExperimentModel): " + experiment + ",  " + e.getMessage(), e);
		}
	}

	/*
		Generate UUIDs (default format) for new experiments when observation unit ID has not been set before
	 */
	private void generateObsUnitId(final ExperimentModel experiment) {
		if (experiment.getNdExperimentId() == null && StringUtils.isBlank(experiment.getObsUnitId())) {
			experiment.setObsUnitId(UUID.randomUUID().toString());
		}
	}

	@Override
	public ExperimentModel save(final ExperimentModel entity) {
		this.generateObsUnitId(entity);
		return super.save(entity);
	}

	public Map<String, Long> countObservationsPerInstance(final Integer datasetId) {

		try {
			final ProjectionList projectionList = Projections.projectionList();
			projectionList.add(Projections.groupProperty("g.description"))
				.add(Projections.rowCount());

			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.createAlias("geoLocation", "g");
			criteria.setProjection(projectionList);
			criteria.add(Restrictions.eq("project.projectId", datasetId));
			final List<Object[]> rows = criteria.list();

			final Map<String, Long> results = new LinkedHashMap<>();
			for (final Object[] row : rows) {
				results.put((String) row[0], (Long) row[1]);
			}
			return results;

		} catch (
			final HibernateException e) {
			final String message =
				"Error at countObservationsPerInstance=" + datasetId + " query at ExperimentDao: " + e.getMessage();
			ExperimentDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public Map<Integer, Map<String, List<Object>>> getExperimentValuesAsMap(final int studyId, final List<Integer> datasetTypeIds,
		final Map<Integer, Integer> inputVariableDatasetMap) {

		final StringBuilder queryString = new StringBuilder("SELECT \n"
			+ "CASE WHEN e.parent_id IS NULL THEN e.nd_experiment_id ELSE e.parent_id END as `experimentId`,\n"
			+ "p.observable_id as `variableId`, \n"
			+ "p.value \n"
			+ "FROM ibdbv2_maize_merged.nd_experiment e \n"
			+ "INNER JOIN project proj ON proj.project_id = e.project_id AND proj.study_id = :studyId \n"
			+ "INNER JOIN phenotype p ON p.nd_experiment_id = e.nd_experiment_id \n"
			+ "WHERE proj.dataset_type_id IN (:datasetTypeIds) AND (");

		final Iterator<Map.Entry<Integer, Integer>> iterator = inputVariableDatasetMap.entrySet().iterator();
		while (iterator.hasNext()) {
			final Map.Entry<Integer, Integer> entry = iterator.next();
			queryString.append(String.format("(p.observable_id = %s AND e.project_id = %s %n)", entry.getKey(), entry.getValue()));
			if (iterator.hasNext()) {
				queryString.append(" OR ");
			} else {
				queryString.append(") \n");
			}
		}

		queryString.append("ORDER BY `experimentId`, `variableId` ;");

		final SQLQuery q = this.getSession().createSQLQuery(queryString.toString());
		q.addScalar("experimentId", new IntegerType());
		q.addScalar("variableId", new StringType());
		q.addScalar("value", new StringType());
		q.setParameter("studyId", studyId);
		q.setParameterList("datasetTypeIds", datasetTypeIds);
		q.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
		final List<Map<String, Object>> results = q.list();

		return this.convertResultsListAsMap(results);
	}

	private Map<Integer, Map<String, List<Object>>> convertResultsListAsMap(final List<Map<String, Object>> results) {

		final Map<Integer, Map<String, List<Object>>> map = new HashMap<>();


		for (final Map<String, Object> row : results) {
			final Integer experimentId = (Integer) row.get("experimentId");
			final String variableId = (String) row.get("variableId");
			final Object value = row.get("value");
			if (!map.containsKey(experimentId)) {
				map.put(experimentId, new HashMap<String, List<Object>>());
			}
			if (!map.get(experimentId).containsKey(variableId)) {
				map.get(experimentId).put(variableId, new ArrayList<Object>());
			}
			// Group values per variable and experimentId.
			map.get(experimentId).get(variableId).add(value);
		}

		return map;
	}
}
