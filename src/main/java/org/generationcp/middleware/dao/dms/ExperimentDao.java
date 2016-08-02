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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.ExperimentPhenotype;
import org.generationcp.middleware.pojos.dms.ExperimentProject;
import org.generationcp.middleware.pojos.dms.ExperimentProperty;
import org.generationcp.middleware.pojos.dms.ExperimentStock;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.GeolocationProperty;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.dms.StockProperty;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link ExperimentModel}.
 *
 */
public class ExperimentDao extends GenericDAO<ExperimentModel, Integer> {

	private static final String COUNT_EXPERIMENT_BY_VARIABLE_IN_PROJECTPROP = "SELECT count(ep.nd_experiment_id) "
			+ " FROM nd_experiment_project ep " + " INNER JOIN projectprop pp ON pp.project_id = ep.project_id"
			+ " AND pp.type_id = 1070 and pp.value = :variableId";

	private static final String COUNT_EXPERIMENT_BY_VARIABLE_IN_GEOLOCATION = "SELECT count(e.nd_experiment_id) "
			+ " FROM nd_experiment e " + " INNER JOIN nd_geolocation g ON g.nd_geolocation_id = e.nd_geolocation_id "
			+ " WHERE (8170 = :variableId AND g.description IS NOT NULL) " + " OR (8191 = :variableId AND g.latitude IS NOT NULL) "
			+ " OR (8192 = :variableId AND g.longitude IS NOT NULL) " + " OR (8193 = :variableId AND g.geodetic_datum IS NOT NULL) "
			+ " OR (8194 = :variableId AND g.altitude IS NOT NULL)";

	private static final String COUNT_EXPERIMENT_BY_VARIABLE_IN_GEOLOCATIONPROP = "SELECT count(e.nd_experiment_id) "
			+ " FROM nd_experiment e " + " INNER JOIN nd_geolocationprop gp ON gp.nd_geolocation_id = e.nd_geolocation_id "
			+ " WHERE gp.type_id = :variableId AND gp.value IS NOT NULL";

	private static final String COUNT_EXPERIMENT_BY_VARIABLE_IN_EXPERIMENTPROP = "SELECT count(e.nd_experiment_id) "
			+ " FROM nd_experiment e " + " INNER JOIN nd_experimentprop ep ON ep.nd_experiment_id = e.nd_experiment_id "
			+ " WHERE ep.type_id = :variableId AND ep.value IS NOT NULL";

	private static final String COUNT_EXPERIMENT_BY_VARIABLE_IN_STOCK = "SELECT count(es.nd_experiment_id) "
			+ " FROM nd_experiment_stock es " + " INNER JOIN stock s ON s.stock_id = es.stock_id "
			+ " WHERE (8230 = :variableId AND s.uniquename IS NOT NULL) " + " OR (8240 = :variableId AND s.dbxref_id IS NOT NULL) "
			+ " OR (8250 = :variableId AND s.name IS NOT NULL) " + " OR (8300 = :variableId AND s.value IS NOT NULL)";

	private static final String COUNT_EXPERIMENT_BY_VARIABLE_IN_STOCKPROP = "SELECT count(es.nd_experiment_id) "
			+ " FROM nd_experiment_stock es " + " INNER JOIN stockprop sp ON sp.stock_id = es.stock_id "
			+ " WHERE sp.type_id = :variableId AND sp.value IS NOT NULL";

	private static final String COUNT_EXPERIMENT_BY_VARIABLE_IN_PHENOTYPE = "SELECT count(ep.nd_experiment_id) "
			+ " FROM nd_experiment_phenotype ep " + " INNER JOIN phenotype p ON p.phenotype_id = ep.phenotype_id "
			+ " AND p.observable_id = :variableId " + " AND (p.value IS NOT NULL " + " OR p.cvalue_id IS NOT NULL)";

	@SuppressWarnings("unchecked")
	public List<Integer> getExperimentIdsByGeolocationIds(Collection<Integer> geolocationIds) throws MiddlewareQueryException {
		try {
			if (geolocationIds != null && !geolocationIds.isEmpty()) {
				Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
				criteria.add(Restrictions.in("geoLocation.locationId", geolocationIds));
				criteria.setProjection(Projections.property("ndExperimentId"));

				return criteria.list();
			}
		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error at getExperimentIdsByGeolocationIds=" + geolocationIds + " query at ExperimentDao: " + e.getMessage(), e);
		}
		return new ArrayList<Integer>();
	}

	public long countByTrialEnvironmentAndVariate(int trialEnvironmentId, int variateVariableId) throws MiddlewareQueryException {
		try {
			SQLQuery query =
					this.getSession().createSQLQuery(
							"select count(distinct e.nd_experiment_id) " + "from nd_experiment e, nd_experiment_phenotype ep, phenotype p "
									+ "where e.nd_experiment_id = ep.nd_experiment_id " + "   and ep.phenotype_id = p.phenotype_id "
									+ "   and e.nd_geolocation_id = " + trialEnvironmentId + "   and p.observable_id = "
									+ variateVariableId);

			return ((BigInteger) query.uniqueResult()).longValue();

		} catch (HibernateException e) {
			this.logAndThrowException("Error at countByTrialEnvironmentAndVariate=" + trialEnvironmentId + ", " + variateVariableId
					+ " query at ExperimentDao: " + e.getMessage(), e);
		}
		return 0;
	}

	public long countByObservedVariable(int variableId, int variableTypeId) throws MiddlewareQueryException {
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
					|| VariableType.NURSERY_CONDITION.getId() == variableTypeId || VariableType.SELECTION_METHOD.getId() == variableTypeId
					|| VariableType.TRIAL_CONDITION.getId() == variableTypeId) {
				sql = ExperimentDao.COUNT_EXPERIMENT_BY_VARIABLE_IN_PHENOTYPE;
			}

			if (sql != null) {
				SQLQuery query = this.getSession().createSQLQuery(sql);
				if (sql.indexOf(":variableId") > -1) {
					query.setParameter("variableId", variableId);
				}
				return ((BigInteger) query.uniqueResult()).longValue();
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error at countByObservationVariable=" + variableId + "," + variableTypeId
					+ " query at ExperimentDAO: " + e.getMessage(), e);
		}
		return 0;
	}

	public ExperimentModel getExperimentByProjectIdAndLocation(Integer projectId, Integer locationId) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("project.projectId", projectId));
			criteria.add(Restrictions.eq("geoLocation.locationId", locationId));
			@SuppressWarnings("rawtypes")
			List list = criteria.list();
			if (list != null && !list.isEmpty()) {
				return (ExperimentModel) list.get(0);
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error at getExperimentByProjectIdAndLocation=" + projectId + "," + locationId
					+ " query at ExperimentDao: " + e.getMessage(), e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<ExperimentModel> getExperimentsByProjectIds(List<Integer> projectIds) throws MiddlewareQueryException {
		List<ExperimentModel> list = new ArrayList<ExperimentModel>();
		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.in("project.projectId", projectIds));
			return criteria.list();

		} catch (HibernateException e) {
			this.logAndThrowException("Error at getExperimentsByProjectIds query at ExperimentDao: " + e.getMessage(), e);
		}
		return list;
	}

	public boolean hasFieldmap(int datasetId) throws MiddlewareQueryException {
		try {
			String sql =
					"SELECT COUNT(eprop.value) " + " FROM nd_experiment_project ep "
							+ " INNER JOIN nd_experimentprop eprop ON eprop.nd_experiment_id = ep.nd_experiment_id "
							+ "    AND eprop.type_id = " + TermId.RANGE_NO.getId() + " AND eprop.value <> '' " + " WHERE ep.project_id = "
							+ datasetId + "  LIMIT 1 ";
			SQLQuery query = this.getSession().createSQLQuery(sql);
			BigInteger count = (BigInteger) query.uniqueResult();
			return count != null && count.longValue() > 0;

		} catch (HibernateException e) {
			this.logAndThrowException("Error at countExperimentsByDatasetId=" + datasetId + " query at ExperimentDao: " + e.getMessage(), e);
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getLocationIdsOfStudy(int studyId) throws MiddlewareQueryException {
		try {
			String sql =
					"SELECT DISTINCT e.nd_geolocation_id " + " FROM nd_experiment e "
							+ " INNER JOIN nd_experiment_project ep ON ep.nd_experiment_id = e.nd_experiment_id "
							+ " INNER JOIN project_relationship pr ON pr.type_id = " + TermId.BELONGS_TO_STUDY.getId()
							+ "   AND pr.object_project_id = " + studyId + "   AND pr.subject_project_id = ep.project_id ";

			SQLQuery query = this.getSession().createSQLQuery(sql);
			return query.list();

		} catch (HibernateException e) {
			this.logAndThrowException("Error at getLocationIdsOfStudy=" + studyId + " query at ExperimentDao: " + e.getMessage(), e);
		}
		return new ArrayList<Integer>();
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getLocationIdsOfStudyWithFieldmap(int studyId) throws MiddlewareQueryException {
		try {
			String sql =
					"SELECT DISTINCT e.nd_geolocation_id " + " FROM nd_experiment e "
							+ " INNER JOIN nd_experiment_project ep ON ep.nd_experiment_id = e.nd_experiment_id "
							+ " INNER JOIN project_relationship pr ON pr.type_id = " + TermId.BELONGS_TO_STUDY.getId()
							+ "   AND pr.object_project_id = " + studyId + "   AND pr.subject_project_id = ep.project_id "
							+ " WHERE EXISTS (SELECT 1 FROM nd_experimentprop eprop " + "   WHERE eprop.type_id = "
							+ TermId.COLUMN_NO.getId() + "     AND eprop.nd_experiment_id = e.nd_experiment_id  AND eprop.value <> '') ";

			SQLQuery query = this.getSession().createSQLQuery(sql);
			return query.list();

		} catch (HibernateException e) {
			this.logAndThrowException("Error at getLocationIdsOfStudy=" + studyId + " query at ExperimentDao: " + e.getMessage(), e);
		}
		return new ArrayList<Integer>();
	}

	public void deleteExperimentsByIds(List<Integer> experimentIdList) throws MiddlewareQueryException {
		String experimentIds = StringUtils.join(experimentIdList, ",");

		try {
			// Please note we are manually flushing because non hibernate based deletes and updates causes the Hibernate session to get out of synch with
			// underlying database. Thus flushing to force Hibernate to synchronize with the underlying database before the delete
			// statement
			this.getSession().flush();
			
			// Delete experiments
			SQLQuery statement =
					this.getSession().createSQLQuery(
							"delete e, ep, es, epheno, pheno, eprop " + "from nd_experiment e "
									+ "left join nd_experiment_project ep on e.nd_experiment_id = ep.nd_experiment_id "
									+ "left join nd_experiment_stock es on e.nd_experiment_id = es.nd_experiment_id "
									+ "left join nd_experiment_phenotype epheno on e.nd_experiment_id = epheno.nd_experiment_id "
									+ "left join phenotype pheno on epheno.phenotype_id = pheno.phenotype_id "
									+ "left join nd_experimentprop eprop on eprop.nd_experiment_id = e.nd_experiment_id "
									+ "where ep.nd_experiment_id in (" + experimentIds + ") ");
			statement.executeUpdate();
		} catch (HibernateException e) {
			this.logAndThrowException("Error in deleteExperimentsByLocation=" + experimentIds + " in DataSetDao: " + e.getMessage(), e);
		}
	}

	public void deleteExperimentsByStudy(int datasetId) throws MiddlewareQueryException {

		try {
			// Please note we are manually flushing because non hibernate based deletes and updates causes the Hibernate session to get out of synch with
			// underlying database. Thus flushing to force Hibernate to synchronize with the underlying database before the delete
			// statement
			this.getSession().flush();

			// Delete experiments
			Query statement =
					this.getSession()
					.createSQLQuery(
							"DELETE e, ep, es, epheno, pheno, eprop " + "FROM nd_experiment e "
									+ "LEFT JOIN nd_experiment_project ep ON e.nd_experiment_id = ep.nd_experiment_id "
									+ "LEFT JOIN nd_experiment_stock es ON e.nd_experiment_id = es.nd_experiment_id "
									+ "LEFT JOIN nd_experiment_phenotype epheno ON e.nd_experiment_id = epheno.nd_experiment_id "
									+ "LEFT JOIN phenotype pheno ON epheno.phenotype_id = pheno.phenotype_id "
									+ "LEFT JOIN nd_experimentprop eprop ON eprop.nd_experiment_id = e.nd_experiment_id "
									+ "WHERE ep.project_id = :datasetId ").setParameter("datasetId", datasetId);
			statement.executeUpdate();
		} catch (HibernateException e) {
			this.logAndThrowException("Error in deleteExperimentsByStudy=" + datasetId + " in DataSetDao: " + e.getMessage(), e);
		}
	}

	public void deleteTrialExperimentsOfStudy(int datasetId) throws MiddlewareQueryException {

		try {

			// Please note we are manually flushing because non hibernate based deletes and updates causes the Hibernate session to get out of synch with
			// underlying database. Thus flushing to force Hibernate to synchronize with the underlying database before the delete
			// statement
			this.getSession().flush();

			// Delete experiments
			Query statement =
					this.getSession()
					.createSQLQuery(
							"DELETE g, gp, e, ep, es, epheno, pheno, eprop " + "FROM nd_geolocation g "
									+ "LEFT JOIN nd_geolocationprop gp on g.nd_geolocation_id = gp.nd_geolocation_id "
									+ "LEFT join nd_experiment e on g.nd_geolocation_id = e.nd_geolocation_id "
									+ "LEFT JOIN nd_experiment_project ep ON e.nd_experiment_id = ep.nd_experiment_id "
									+ "LEFT JOIN nd_experiment_stock es ON e.nd_experiment_id = es.nd_experiment_id "
									+ "LEFT JOIN nd_experiment_phenotype epheno ON e.nd_experiment_id = epheno.nd_experiment_id "
									+ "LEFT JOIN phenotype pheno ON epheno.phenotype_id = pheno.phenotype_id "
									+ "LEFT JOIN nd_experimentprop eprop ON eprop.nd_experiment_id = e.nd_experiment_id "
									+ "WHERE ep.project_id = :datasetId ").setParameter("datasetId", datasetId);

			statement.executeUpdate();

		} catch (HibernateException e) {
			this.logAndThrowException("Error in deleteTrialExperimentsOfStudy=" + datasetId + " in DataSetDao: " + e.getMessage(), e);
		}
	}

	public boolean checkIfAnyLocationIDsExistInExperiments(int dataSetId, List<Integer> locationIds) {

		try {
			String sql =
					"SELECT count(*) FROM nd_experiment exp "
							+ "INNER JOIN nd_experiment_project exp_proj ON exp.nd_experiment_id = exp_proj.nd_experiment_id "
							+ "WHERE exp.nd_geolocation_id in (:locationIds) " + "AND exp_proj.project_id = :dataSetId ";

			SQLQuery query = this.getSession().createSQLQuery(sql);
			query.setParameterList("locationIds", locationIds);
			query.setParameter("dataSetId", dataSetId);

			Long count = 0L;
			final Object obj = query.uniqueResult();
			if (obj != null) {
				count = ((Number) obj).longValue();
			}

			if (count == 0) {
				return false;
			} else {
				return true;
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error at checkIfLocationIDsExistInExperiments=" + locationIds + "," + dataSetId + ","
					+ " query at ExperimentDao: " + e.getMessage(), e);
		}

		return false;

	}
	
	public void deleteEnvironmentsAndRelationshipsByLocationIds(List<Integer> locationIds) throws MiddlewareQueryException {
		try {
			// Please note we are manually flushing because non hibernate based deletes and updates causes the Hibernate session to get out of synch with
			// underlying database. Thus flushing to force Hibernate to synchronize with the underlying database before the delete
			// statement
			this.getSession().flush();
			
			SQLQuery statement =
					this.getSession().createSQLQuery(
							"delete g, e, gprop, ep, es, epheno, pheno, eprop from nd_geolocation g "
									+ "inner join nd_experiment e on g.nd_geolocation_id = e.nd_geolocation_id "
									+ "left join nd_geolocationprop gprop on g.nd_geolocation_id = gprop.nd_geolocation_id "
									+ "left join nd_experiment_project ep on e.nd_experiment_id = ep.nd_experiment_id "
									+ "left join nd_experiment_stock es on e.nd_experiment_id = es.nd_experiment_id "
									+ "left join nd_experiment_phenotype epheno on e.nd_experiment_id = epheno.nd_experiment_id "
									+ "left join phenotype pheno on epheno.phenotype_id = pheno.phenotype_id "
									+ "left join nd_experimentprop eprop on eprop.nd_experiment_id = e.nd_experiment_id "
									+ "where g.nd_geolocation_id in (:locationIds) "
									+ "and e.type_id != " + TermId.STUDY_INFORMATION.getId());
			statement.setParameterList("locationIds", locationIds);
			statement.executeUpdate();
			
		} catch (HibernateException e) {
			this.logAndThrowException("Error in deleteEnvironmentsAndRelationshipsByLocationIds=" + locationIds + " in DataSetDao: " + e.getMessage(), e);
		}
	}
}
