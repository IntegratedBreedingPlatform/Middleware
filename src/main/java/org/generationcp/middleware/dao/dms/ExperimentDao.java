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
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.service.api.dataset.ObservationUnitData;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.generationcp.middleware.service.api.dataset.ObservationUnitsSearchDTO;
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
import org.springframework.util.CollectionUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * DAO class for {@link ExperimentModel}.
 *
 */
public class ExperimentDao extends GenericDAO<ExperimentModel, Integer> {

	private static final String ND_EXPERIMENT_ID = "ndExperimentId";
	private static final String OBSERVATION_UNIT_ID = "observationUnitId";
	public static final String FIELD_MAP_COLUMN = "FIELDMAP COLUMN";
	public static final String LOCATION_ID = "LOCATION_ID";
	public static final String EXPT_DESIGN = "EXPT_DESIGN";
	public static final String OBS_UNIT_ID = "OBS_UNIT_ID";
	public static final String PARENT_OBS_UNIT_ID = "PARENT_OBS_UNIT_ID";
	public static final String COL = "COL";
	public static final String ROW = "ROW";
	public static final String BLOCK_NO = "BLOCK_NO";
	public static final String PLOT_NO = "PLOT_NO";
	public static final String REP_NO = "REP_NO";
	public static final String ENTRY_CODE = "ENTRY_CODE";
	public static final String ENTRY_NO = "ENTRY_NO";
	public static final String DESIGNATION = "DESIGNATION";
	public static final String GID = "GID";
	public static final String ENTRY_TYPE = "ENTRY_TYPE";
	public static final String TRIAL_INSTANCE = "TRIAL_INSTANCE";
	public static final String FIELD_MAP_RANGE = "FIELDMAP RANGE";
	public static final String SUM_OF_SAMPLES = "SUM_OF_SAMPLES";
	public static final String SQL_GET_SAMPLED_OBSERVATION_BY_STUDY = " SELECT " +
			" experiment.nd_experiment_id, " +
			" sample.sample_id," +
			" sample.sample_no " +
			" FROM nd_experiment experiment " +
			" INNER JOIN project_relationship pr ON (pr.subject_project_id = experiment.project_id) " +
			" INNER JOIN project p ON (p.project_id = pr.subject_project_id) " +
			" INNER JOIN sample sample ON (sample.nd_experiment_id = experiment.nd_experiment_id) " +
			" WHERE p.project_id = (SELECT p.project_id " +
			" FROM project_relationship pr " +
			" INNER JOIN project p ON p.project_id = pr.subject_project_id " +
			" WHERE (pr.object_project_id = :studyId AND name LIKE '%PLOTDATA'))";

	private static final Logger LOG = LoggerFactory.getLogger(ExperimentDao.class);
	public static final String OBSERVATION_UNIT_NO = "OBSERVATION_UNIT_NO";

	private static final String COUNT_EXPERIMENT_BY_VARIABLE_IN_PROJECTPROP = "SELECT count(e.nd_experiment_id) "
			+ "FROM nd_experiment e INNER JOIN projectprop pp ON pp.project_id = e.project_id "
			+ "AND pp.value = :variableId";

	private static final String COUNT_EXPERIMENT_BY_VARIABLE_IN_GEOLOCATION = "SELECT count(e.nd_experiment_id) "
			+ "FROM nd_experiment e INNER JOIN nd_geolocation g ON g.nd_geolocation_id = e.nd_geolocation_id "
			+ "WHERE (" + TermId.TRIAL_INSTANCE_FACTOR.getId() + " = :variableId AND g.description IS NOT NULL)  OR (" + TermId.LATITUDE.getId() + " = :variableId AND g.latitude IS NOT NULL) "
			+ "OR (" + TermId.LONGITUDE.getId() + "= :variableId AND g.longitude IS NOT NULL) OR (" + TermId.GEODETIC_DATUM.getId() + " = :variableId AND g.geodetic_datum IS NOT NULL) "
			+ "OR ("+ TermId.ALTITUDE.getId()+" = :variableId AND g.altitude IS NOT NULL) ";

	private static final String COUNT_EXPERIMENT_BY_VARIABLE_IN_GEOLOCATIONPROP = "SELECT count(e.nd_experiment_id) "
			+ "FROM nd_experiment e INNER JOIN nd_geolocationprop gp ON gp.nd_geolocation_id = e.nd_geolocation_id "
			+ "WHERE gp.type_id = :variableId AND gp.value IS NOT NULL";

	private static final String COUNT_EXPERIMENT_BY_VARIABLE_IN_EXPERIMENTPROP = "SELECT count(e.nd_experiment_id) "
			+ "FROM nd_experiment e INNER JOIN nd_experimentprop ep ON ep.nd_experiment_id = e.nd_experiment_id "
			+ "WHERE ep.type_id = :variableId AND ep.value IS NOT NULL";

	private static final String COUNT_EXPERIMENT_BY_VARIABLE_IN_STOCK = "SELECT count(e.nd_experiment_id) "
			+ "FROM nd_experiment e INNER JOIN stock s ON s.stock_id = e.stock_id "
			+ "WHERE ("+ TermId.ENTRY_NO.getId() + "= :variableId AND s.uniquename IS NOT NULL)  OR ("+ TermId.GID.getId() +" = :variableId AND s.dbxref_id IS NOT NULL) "
			+ "OR ("+TermId.DESIG.getId()+" = :variableId AND s.name IS NOT NULL) OR ("+TermId.ENTRY_CODE.getId()+" = :variableId AND s.value IS NOT NULL)";

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
			final String message = "Error at getExperimentIdsByGeolocationIds=" + geolocationIds + " query at ExperimentDao: " + e.getMessage();
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
							+ " INNER JOIN project_relationship pr ON pr.type_id = " + TermId.BELONGS_TO_STUDY.getId()
							+ "   AND pr.object_project_id = " + studyId + "   AND pr.subject_project_id = e.project_id ";

			final SQLQuery query = this.getSession().createSQLQuery(sql);
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
							+ " INNER JOIN project_relationship pr ON pr.type_id = " + TermId.BELONGS_TO_STUDY.getId()
							+ "   AND pr.object_project_id = " + studyId + "   AND pr.subject_project_id = e.project_id "
							+ " WHERE EXISTS (SELECT 1 FROM nd_experimentprop eprop " + "   WHERE eprop.type_id = "
							+ TermId.COLUMN_NO.getId() + "     AND eprop.nd_experiment_id = e.nd_experiment_id  AND eprop.value <> '') ";

			final SQLQuery query = this.getSession().createSQLQuery(sql);
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
	public List<ExperimentModel> getExperiments(final int projectId, final List<TermId> types, final int start, final int numOfRows, final boolean firstInstance) {
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
			if(firstInstance) {
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

	private String getObservationUnitTableQuery(
			final ObservationUnitsSearchDTO searchDto, final String observationUnitNoName) {

		// FIXME some props should be fetched from plot, not immediate parent. It won't work for sub-sub obs
		// same for columns -> DatasetServiceImpl.getSubObservationSetColumns
		final StringBuilder sql = new StringBuilder("SELECT  " //
			+ "    nde.nd_experiment_id as observationUnitId, " //
			+ "    gl.description AS TRIAL_INSTANCE, " //
			+ "    (SELECT loc.lname FROM nd_geolocationprop gprop INNER JOIN location loc on loc.locid = gprop.value WHERE gprop.nd_geolocation_id = gl.nd_geolocation_id and gprop.type_id = 8190) 'LOCATION_ID', "
			+ "    (SELECT edesign.name FROM nd_geolocationprop gprop INNER JOIN cvterm edesign on edesign.cvterm_id = gprop.value WHERE gprop.nd_geolocation_id = gl.nd_geolocation_id and gprop.type_id = 8135) 'EXPT_DESIGN',  "
			+ "    (SELECT iispcvt.definition FROM stockprop isp INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = isp.type_id INNER JOIN cvterm iispcvt ON iispcvt.cvterm_id = isp.value WHERE isp.stock_id = s.stock_id AND ispcvt.name = 'ENTRY_TYPE') ENTRY_TYPE,  "
			+ "    s.dbxref_id AS GID, " //
			+ "    s.name DESIGNATION, " //
			+ "    s.uniquename ENTRY_NO, " //
			+ "    s.value as ENTRY_CODE, " //
			+ "    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = parent.nd_experiment_id AND ispcvt.name = 'REP_NO') REP_NO,  "
			+ "    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = parent.nd_experiment_id AND ispcvt.name = 'PLOT_NO') PLOT_NO,  "
			+ "    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = parent.nd_experiment_id AND ispcvt.name = 'BLOCK_NO') BLOCK_NO,  "
			+ "    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = parent.nd_experiment_id AND ispcvt.name = 'ROW') ROW,  "
			+ "    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = parent.nd_experiment_id AND ispcvt.name = 'COL') COL,  "
			+ "    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = parent.nd_experiment_id AND ispcvt.name = 'FIELDMAP COLUMN') 'FIELDMAP COLUMN',  "
			+ "    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = parent.nd_experiment_id AND ispcvt.name = 'FIELDMAP RANGE') 'FIELDMAP RANGE',  "
			+ "    nde.obs_unit_id as OBS_UNIT_ID,  " //
			+ "    parent.obs_unit_id as PARENT_OBS_UNIT_ID,  " //
			+ "    (SELECT coalesce(nullif(count(sp.sample_id), 0), '-') FROM sample AS sp WHERE nde.nd_experiment_id = sp.nd_experiment_id ) 'SUM_OF_SAMPLES',");

		final String traitClauseFormat = " MAX(IF(cvterm_variable.name = '%s', ph.value, NULL)) AS '%s'," //
			+ " MAX(IF(cvterm_variable.name = '%s', ph.phenotype_id, NULL)) AS '%s'," //
			+ " MAX(IF(cvterm_variable.name = '%s', ph.status, NULL)) AS '%s'," //
			+ " MAX(IF(cvterm_variable.name = '%s', ph.cvalue_id, NULL)) AS '%s', " //
			+ " MAX(IF(cvterm_variable.name = '%s', ph.draft_value, NULL)) AS '%s'," //
			+ " MAX(IF(cvterm_variable.name = '%s', ph.draft_cvalue_id, NULL)) AS '%s', " //
			;

		for (final MeasurementVariableDto measurementVariable : searchDto.getSelectionMethodsAndTraits()) {
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

		if (!CollectionUtils.isEmpty(searchDto.getGenericGermplasmDescriptors())) {
			final String germplasmDescriptorClauseFormat =
				"    (SELECT sprop.value FROM stockprop sprop INNER JOIN cvterm spropcvt ON spropcvt.cvterm_id = sprop.type_id WHERE sprop.stock_id = s.stock_id AND spropcvt.name = '%s') '%s',  ";
			for (final String gpFactor : searchDto.getGenericGermplasmDescriptors()) {
				sql.append(String.format(germplasmDescriptorClauseFormat, gpFactor, gpFactor));
			}
		}

		if (!CollectionUtils.isEmpty(searchDto.getAdditionalDesignFactors())) {
			final String designFactorClauseFormat =
				"    (SELECT xprop.value FROM nd_experimentprop xprop INNER JOIN cvterm xpropcvt ON xpropcvt.cvterm_id = xprop.type_id WHERE xprop.nd_experiment_id = parent.nd_experiment_id AND xpropcvt.name = '%s') '%s',  ";
			for (final String designFactor : searchDto.getAdditionalDesignFactors()) {
				sql.append(String.format(designFactorClauseFormat, designFactor, designFactor));
			}
		}

		if (!CollectionUtils.isEmpty(searchDto.getEnvironmentDetails())) {
			final String envFactorFormat =
				"    (SELECT gprop.value FROM nd_geolocationprop gprop INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = gprop.type_id AND ispcvt.name = '%s' WHERE gprop.nd_geolocation_id = gl.nd_geolocation_id ) '%s',  ";
			for (final String envFactor : searchDto.getEnvironmentDetails()) {
				sql.append(String.format(envFactorFormat, envFactor, envFactor));
			}
		}

		if (!CollectionUtils.isEmpty(searchDto.getEnvironmentConditions())) {
			final String envConditionFormat =
				"    (SELECT pheno.value from phenotype pheno "
					+ "		INNER JOIN cvterm envcvt ON envcvt.cvterm_id = pheno.observable_id AND envcvt.name = '%s' "
					+ "		INNER JOIN nd_experiment envnde ON  pheno.nd_experiment_id = envnde.nd_experiment_id AND envnde.project_id = :datasetEnvironmentId "
					+ "		WHERE envnde.nd_geolocation_id = gl.nd_geolocation_id) '%s',  ";
			for (final String envCondition : searchDto.getEnvironmentConditions()) {
				sql.append(String.format(envConditionFormat, envCondition, envCondition));
			}
		}

		sql.append("nde.observation_unit_no AS OBSERVATION_UNIT_NO, ");
		sql.append(" 1=1 FROM " //
			+ "	project p " //
			+ "	INNER JOIN project_relationship pr ON p.project_id = pr.subject_project_id " //
			+ "	INNER JOIN nd_experiment nde ON nde.project_id = pr.subject_project_id " //
			+ "	INNER JOIN nd_geolocation gl ON nde.nd_geolocation_id = gl.nd_geolocation_id " //
			+ "	INNER JOIN stock s ON s.stock_id = nde.stock_id " //
			+ "	LEFT JOIN phenotype ph ON nde.nd_experiment_id = ph.nd_experiment_id " //
			+ "	LEFT JOIN cvterm cvterm_variable ON cvterm_variable.cvterm_id = ph.observable_id " //
			+ "   INNER JOIN nd_experiment parent ON parent.nd_experiment_id = nde.parent_id " //
			+ " WHERE p.project_id = :datasetId "); //

		if (searchDto.getInstanceId() != null) {
			sql.append(" AND gl.nd_geolocation_id = :instanceId"); //
		}

		if (Boolean.TRUE.equals(searchDto.getDraftMode())) {
			sql.append(" AND (ph.draft_value is not null or ph.draft_cvalue_id is not null) "); //
		}

		final ObservationUnitsSearchDTO.Filter filter = searchDto.getFilter();
		this.addFilters(sql, filter, searchDto.getDraftMode());

		sql.append(" GROUP BY observationUnitId "); //

		String orderColumn;
		final String sortBy = searchDto.getSortedRequest() != null? searchDto.getSortedRequest().getSortBy() : "";
		if (observationUnitNoName != null && StringUtils.isNotBlank(sortBy) && observationUnitNoName.equalsIgnoreCase(sortBy)) {
			orderColumn = OBSERVATION_UNIT_NO;
		} else {
			orderColumn = StringUtils.isNotBlank(sortBy) ? sortBy : "PLOT_NO";
		}

		final String sortOrder = searchDto.getSortedRequest() != null? searchDto.getSortedRequest().getSortOrder() : "";
		final String direction = StringUtils.isNotBlank(sortOrder) ? sortOrder : "asc";
		/**
		 * Values of these columns are numbers but the database stores it in string format (facepalm). Sorting on them requires multiplying
		 * with 1 so that they turn into number and are sorted as numbers rather than strings.
		 */
		final List<String> columnsWithNumbersAsStrings = Lists.newArrayList("ENTRY_NO", "REP_NO", "PLOT_NO", "ROW", "COL", "BLOCK_NO");
		if (columnsWithNumbersAsStrings.contains(orderColumn)) {
			orderColumn = "(1 * " + orderColumn + ")";
		}
		else {
			orderColumn = "`" + orderColumn + "`";
		}

		sql.append(" ORDER BY " + orderColumn + " " + direction);
		return sql.toString();
	}

	private void addFilters(final StringBuilder sql, final ObservationUnitsSearchDTO.Filter filter, final Boolean draftMode) {
		
		if (filter == null) {
			return;
		}

		final String filterByDraftOrValue = Boolean.TRUE.equals(draftMode) ? "draft_value" : "value";
		
		if (Boolean.TRUE.equals(filter.getByOutOfBound())) {
			sql.append(" and nde.nd_experiment_id in (select ph2.nd_experiment_id " //
					+ "      from cvterm_relationship cvtrscale " //
					+ "           inner join cvterm scale on cvtrscale.object_id = scale.cvterm_id " //
					+ "           inner join cvterm_relationship cvtrdataType on scale.cvterm_id = cvtrdataType.subject_id and cvtrdataType.type_id = " + TermId.HAS_TYPE.getId()
					+ "           inner join cvterm dataType on cvtrdataType.object_id = dataType.cvterm_id " //
					+ "           left join cvtermprop scaleMaxRange on scale.cvterm_id = scaleMaxRange.cvterm_id and scaleMaxRange.type_id = " + TermId.MAX_VALUE.getId()
					+ "           left join cvtermprop scaleMinRange on scale.cvterm_id = scaleMinRange.cvterm_id and scaleMinRange.type_id = " + TermId.MIN_VALUE.getId()
					+ " inner join phenotype ph2 on cvtrscale.subject_id = ph2.observable_id " //
					+ "    inner join nd_experiment nde2 on ph2.nd_experiment_id = nde2.nd_experiment_id " //
					+ "      where ph2." + filterByDraftOrValue + " is not null " //
					+ "        and cvtrscale.type_id = " + TermId.HAS_SCALE.getId() //
					+ "        and case " //
					+ "        when dataType.cvterm_id = " + TermId.CATEGORICAL_VARIABLE.getId() //
					/* get the categoricals whose value != category value (out-of-bound)
					in other words, the set where ph.value = category value NOT exists*/
					+ "          then not exists( " //
					+ "          select 1 " //
					+ "            from cvterm_relationship cvtrcategory " //
					+ "                 inner join cvterm category on cvtrcategory.object_id = category.cvterm_id " //
					+ "            where scale.cvterm_id = cvtrcategory.subject_id " //
					+ "              and cvtrcategory.type_id = " + TermId.HAS_VALUE.getId() //
					+ "              and ph2." + filterByDraftOrValue + " = category.name " //
					+ "          ) " //
					+ "        when dataType.cvterm_id = " + TermId.NUMERIC_VARIABLE.getId() //
					// get the numericals whose value is not within bounds
					+ "          then ph2." + filterByDraftOrValue + " < scaleMinRange.value or ph2." + filterByDraftOrValue + " > scaleMaxRange.value " //
					+ "        else false " //
					+ "        end " //
					+ "    )"); //
		}

		if (!filter.getFilteredValues().isEmpty()) {
			final Map<String, List<String>> filteredValues = filter.getFilteredValues();

			for (final String observationId : filteredValues.keySet()) {
				sql.append(
					" and nde.nd_experiment_id in ( " //
						+ "    select ph2.nd_experiment_id " //
						+ "    from phenotype ph2 " //
						+ "    inner join nd_experiment nde2 on ph2.nd_experiment_id = nde2.nd_experiment_id " //
						+ "    where ph2.observable_id = :" + observationId + "_Id" //
						+ "    and nde2.project_id = p.project_id " //
						+ "    and ph2." + filterByDraftOrValue + " in (:" + observationId + "_values ))"); //
			}
		}

		if (!filter.getFilteredTextValues().isEmpty()) {
			// filter by column value (text)
			final Map<String, String> filteredTextValues = filter.getFilteredTextValues();

			for (final String observationId : filteredTextValues.keySet()) {
				sql.append(
					" and nde.nd_experiment_id in ( " //
						+ "    select ph2.nd_experiment_id " //
						+ "    from phenotype ph2 " //
						+ "    inner join nd_experiment nde2 on ph2.nd_experiment_id = nde2.nd_experiment_id " //
						+ "    where ph2.observable_id = " + observationId //
						+ "    and nde2.project_id = p.project_id " //
					); //
				sql.append(" and ph2." + filterByDraftOrValue + " like '%").append(filteredTextValues.get(observationId)).append("%')");
			}
		}

		if (Boolean.TRUE.equals(filter.getByOverwritten())) {
			sql.append(
					" and nde.nd_experiment_id in ( " //
						+ "    select ph2.nd_experiment_id " //
						+ "    from phenotype ph2 " //
						+ "    inner join nd_experiment nde2 on ph2.nd_experiment_id = nde2.nd_experiment_id " //
						+ "    where nde2.project_id = p.project_id " //
						+ "    and ph2.value is not null and ph2.draft_value is not null )"); //
		}

		if (Boolean.TRUE.equals(filter.getByOutOfSync())) {
			sql.append(
				" and nde.nd_experiment_id in ( " //
					+ "    select ph2.nd_experiment_id " //
					+ "    from phenotype ph2 " //
					+ "    inner join nd_experiment nde2 on ph2.nd_experiment_id = nde2.nd_experiment_id " //
					+ "    where nde2.project_id = p.project_id " //
					+ "    and ph2.status = '" + Phenotype.ValueStatus.OUT_OF_SYNC.getName() + "' )" ); //
		}

		if (Boolean.TRUE.equals(filter.getByMissing())) {
			// filter by missing
			sql.append(
				" and nde.nd_experiment_id in ( " //
					+ "    select ph2.nd_experiment_id " //
					+ "    from phenotype ph2 " //
					+ "    inner join nd_experiment nde2 on ph2.nd_experiment_id = nde2.nd_experiment_id " //
					+ "    where nde2.project_id = p.project_id " //
					+ "    and ph2.value =  '" + Phenotype.MISSING_VALUE + "' )" ); //
		}
	}

	public List<ObservationUnitRow> getObservationUnitTable(final ObservationUnitsSearchDTO searchDto) {
		try {
			final String observationVariableName = this.getObservationVariableName(searchDto.getDatasetId());
			final List<Map<String, Object>> results = this.getObservationUnitsQueryResult(
				searchDto,
				observationVariableName);
			return this.mapResults(results, searchDto, observationVariableName);
		} catch (final Exception e) {
			ExperimentDao.LOG.error(e.getMessage());
			final String error = "An internal error has ocurred when trying to retrieve observation unit rows " + e.getMessage();
			throw new MiddlewareException(error, e);
		}
	}

	private SQLQuery createQueryAndAddScalar(
		final ObservationUnitsSearchDTO searchDto, final String generateQuery) {
		final SQLQuery query = this.getSession().createSQLQuery(generateQuery);

		this.addScalar(query);
		query.addScalar("FIELDMAP COLUMN");
		query.addScalar("FIELDMAP RANGE");
		query.addScalar(LOCATION_ID);
		query.addScalar(EXPT_DESIGN);

		this.addScalarForTraits(searchDto.getSelectionMethodsAndTraits(), query, true);

		for (final String gpDescriptor : searchDto.getGenericGermplasmDescriptors()) {
			query.addScalar(gpDescriptor, new StringType());
		}

		for (final String designFactor : searchDto.getAdditionalDesignFactors()) {
			query.addScalar(designFactor, new StringType());
		}

		for (final String envFactor : searchDto.getEnvironmentDetails()) {
			query.addScalar(envFactor, new StringType());
		}
		for (final String envCondition : searchDto.getEnvironmentConditions()) {
			query.addScalar(envCondition, new StringType());
		}

		query.addScalar(OBSERVATION_UNIT_NO);
		return query;
	}


	private void addScalar(final SQLQuery createSQLQuery) {
		createSQLQuery.addScalar(ExperimentDao.OBSERVATION_UNIT_ID);
		createSQLQuery.addScalar(ExperimentDao.TRIAL_INSTANCE);
		createSQLQuery.addScalar(ExperimentDao.ENTRY_TYPE);
		createSQLQuery.addScalar(ExperimentDao.GID);
		createSQLQuery.addScalar(ExperimentDao.DESIGNATION);
		createSQLQuery.addScalar(ExperimentDao.ENTRY_NO);
		createSQLQuery.addScalar(ExperimentDao.ENTRY_CODE);
		createSQLQuery.addScalar(ExperimentDao.REP_NO);
		createSQLQuery.addScalar(ExperimentDao.PLOT_NO);
		createSQLQuery.addScalar(ExperimentDao.BLOCK_NO);
		createSQLQuery.addScalar(ExperimentDao.ROW);
		createSQLQuery.addScalar(ExperimentDao.COL);
		createSQLQuery.addScalar(ExperimentDao.PARENT_OBS_UNIT_ID, new StringType());
		createSQLQuery.addScalar(ExperimentDao.OBS_UNIT_ID, new StringType());
		createSQLQuery.addScalar(ExperimentDao.SUM_OF_SAMPLES);
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

	public Integer countObservationUnitsForDataset(final Integer datasetId, final Integer instanceId, final Boolean draftMode,
		final ObservationUnitsSearchDTO.Filter filter) {

		try {
			StringBuilder sql = new StringBuilder("select count(*) as totalObservationUnits from " //
				+ "nd_experiment nde " //
				+ "    inner join project p on p.project_id = nde.project_id " //
				+ "    inner join nd_geolocation gl ON nde.nd_geolocation_id = gl.nd_geolocation_id " //
				+ " where " //
				+ "	p.project_id = :datasetId ");

			if (instanceId != null) {
				sql.append(" and gl.nd_geolocation_id = :instanceId ");
			}

			if (Boolean.TRUE.equals(draftMode)) {
				sql.append(" and exists(select 1" //
					+ "   from phenotype ph" //
					+ "   where ph.nd_experiment_id = nde.nd_experiment_id " //
					+ "         and (ph.draft_value is not null " //
					+ "                or ph.draft_cvalue_id is not null)) ");
			}

			if (filter != null) {
				addFilters(sql, filter, draftMode);
			}

			final SQLQuery query = this.getSession().createSQLQuery(sql.toString());

			if (filter != null && !filter.getFilteredValues().isEmpty()) {
				final Map<String, List<String>> filteredValues = filter.getFilteredValues();

				for (final String observationId : filteredValues.keySet()) {
					query.setParameter(observationId + "_Id", observationId);
					query.setParameterList(observationId + "_values", filter.getFilteredValues().get(observationId));
				}
			}

			query.addScalar("totalObservationUnits", new IntegerType());
			query.setParameter("datasetId", datasetId);

			if (instanceId != null) {
				query.setParameter("instanceId", instanceId);
			}

			return (Integer) query.uniqueResult();
		} catch (final HibernateException he) {
			throw new MiddlewareQueryException(
				String.format("Unexpected error in executing countTotalObservations(studyId = %s, instanceNumber = %s) : ",
					datasetId, instanceId) + he.getMessage(),
				he);
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

	public String getObservationVariableName(final int datasetId) {
		final SQLQuery query = this.getSession()
			.createSQLQuery(
				"SELECT pp.alias AS OBSERVATION_UNIT_NO_NAME FROM projectprop pp INNER JOIN cvterm cvt ON cvt.cvterm_id = pp.type_id WHERE pp.project_id = :datasetId AND cvt.cvterm_id = 1812 ");
		query.addScalar("OBSERVATION_UNIT_NO_NAME", new StringType());
		query.setParameter("datasetId", datasetId);
		return (query.list() != null && !query.list().isEmpty() ? (String) query.list().get(0) : null);
	}

	public Map<String, ObservationUnitRow> getObservationUnitsAsMap(
		final int datasetId,
		final List<MeasurementVariable> measurementVariables, final List<String> observationUnitIds) {

		final Function<MeasurementVariable, MeasurementVariableDto> measurementVariableFullToDto =
				new Function<MeasurementVariable,MeasurementVariableDto>() {
					public MeasurementVariableDto apply(final MeasurementVariable i) { return new MeasurementVariableDto(i.getTermId(), i.getName()); }
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

	private List<Map<String, Object>> getObservationUnitsQueryResult(final ObservationUnitsSearchDTO params, final String observationVariableName) {
		try {

			final String observationUnitTableQuery = this.getObservationUnitTableQuery(params, observationVariableName);
			final SQLQuery query = this.createQueryAndAddScalar(params, observationUnitTableQuery);
			query.setParameter("datasetId", params.getDatasetId());

			if (params.getInstanceId() != null) {
				query.setParameter("instanceId", String.valueOf(params.getInstanceId()));
			}

			if(!CollectionUtils.isEmpty(params.getEnvironmentConditions())){
				query.setParameter("datasetEnvironmentId", String.valueOf(params.getEnvironmentDatasetId()));
			}

			if (!params.getFilter().getFilteredValues().isEmpty()) {
				final Map<String, List<String>> filteredValues = params.getFilter().getFilteredValues();

				for (final String observationId : filteredValues.keySet()) {
					query.setParameter(observationId + "_Id", observationId);
					query.setParameterList(observationId + "_values", params.getFilter().getFilteredValues().get(observationId));
				}
			}

			final Integer pageNumber = params.getSortedRequest() != null ? params.getSortedRequest().getPageNumber() : null;
			final Integer pageSize = params.getSortedRequest() != null ? params.getSortedRequest().getPageSize() : null;
			if (pageNumber != null && pageSize != null) {
				query.setFirstResult(pageSize * (pageNumber - 1));
				query.setMaxResults(pageSize);
			}

			query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
			final List<Map<String, Object>> results = query.list();
			return results;

		} catch (final Exception e) {
			final String error = "An internal error has ocurred when trying to execute the operation " + e.getMessage();
			ExperimentDao.LOG.error(error);
			throw new MiddlewareException(error, e);
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

	private List<ObservationUnitRow> mapResults(final List<Map<String, Object>> results, final ObservationUnitsSearchDTO searchDto,
			final String observationVariableName) {
		final List<ObservationUnitRow> observationUnitRows = new ArrayList<>();

		if (results != null && !results.isEmpty()) {
			for (final Map<String, Object> row : results) {
				final ObservationUnitRow observationUnitRow = this.getObservationUnitRow(searchDto, observationVariableName, row);
				observationUnitRows.add(observationUnitRow);
			}
		}

		return observationUnitRows;
	}

	private ObservationUnitRow getObservationUnitRow(final ObservationUnitsSearchDTO searchDto, final String observationVariableName,
		final Map<String, Object> row) {
		final Map<String, ObservationUnitData> variables = new HashMap<>();

		for (final MeasurementVariableDto variable : searchDto.getSelectionMethodsAndTraits()) {
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

		observationUnitRow.setObservationUnitId((Integer) row.get(OBSERVATION_UNIT_ID));
		observationUnitRow.setAction(((Integer) row.get(OBSERVATION_UNIT_ID)).toString());
		observationUnitRow.setObsUnitId((String) row.get(OBS_UNIT_ID));
		observationUnitRow.setSamplesCount((String) row.get(SUM_OF_SAMPLES));
		final Integer gid = (Integer) row.get(GID);
		observationUnitRow.setGid(gid);
		variables.put(GID, new ObservationUnitData(gid.toString()));

		final String designation = (String) row.get(DESIGNATION);
		observationUnitRow.setDesignation(designation);
		variables.put(DESIGNATION, new ObservationUnitData(designation));

		variables.put(TRIAL_INSTANCE, new ObservationUnitData((String) row.get(TRIAL_INSTANCE)));
		variables.put(ENTRY_TYPE, new ObservationUnitData((String) row.get(ENTRY_TYPE)));
		variables.put(ENTRY_NO, new ObservationUnitData((String) row.get(ENTRY_NO)));
		variables.put(ENTRY_CODE, new ObservationUnitData((String) row.get(ENTRY_CODE)));
		variables.put(REP_NO, new ObservationUnitData((String) row.get(REP_NO)));
		variables.put(PLOT_NO, new ObservationUnitData((String) row.get(PLOT_NO)));
		variables.put(BLOCK_NO, new ObservationUnitData((String) row.get(BLOCK_NO)));
		variables.put(ROW, new ObservationUnitData((String) row.get(ROW)));
		variables.put(COL, new ObservationUnitData((String) row.get(COL)));
		variables.put(OBS_UNIT_ID, new ObservationUnitData((String) row.get(OBS_UNIT_ID)));
		variables.put(PARENT_OBS_UNIT_ID, new ObservationUnitData((String) row.get(PARENT_OBS_UNIT_ID)));
		variables.put(FIELD_MAP_COLUMN, new ObservationUnitData((String) row.get(FIELD_MAP_COLUMN)));
		variables.put(FIELD_MAP_RANGE, new ObservationUnitData((String) row.get(FIELD_MAP_RANGE)));
		variables.put(LOCATION_ID,  new ObservationUnitData((String) row.get(LOCATION_ID)));
		variables.put(EXPT_DESIGN,  new ObservationUnitData((String) row.get(EXPT_DESIGN)));
		variables.put(
			observationVariableName,
			new ObservationUnitData(((Integer) row.get(OBSERVATION_UNIT_NO)).toString()));

		for (final String gpDesc : searchDto.getGenericGermplasmDescriptors()) {
			variables.put(gpDesc, new ObservationUnitData((String) row.get(gpDesc)));
		}
		for (final String designFactor : searchDto.getAdditionalDesignFactors()) {
			variables.put(designFactor, new ObservationUnitData((String) row.get(designFactor)));
		}
		for (final String envFactor : searchDto.getEnvironmentDetails()) {
			variables.put(envFactor, new ObservationUnitData((String) row.get(envFactor)));
		}
		for (final String envCondition : searchDto.getEnvironmentConditions()) {
			variables.put(envCondition, new ObservationUnitData((String) row.get(envCondition)));
		}

		observationUnitRow.setVariables(variables);
		return observationUnitRow;
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
			throw new MiddlewareQueryException("Error in saveOrUpdate(ExperimentModel): " + experiment + ",  "+ e.getMessage(), e);
		}
	}

	private void generateObsUnitId(final ExperimentModel experiment) {
		if (experiment.getNdExperimentId() == null) {
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

}
