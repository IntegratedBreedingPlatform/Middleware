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
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
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

			return count != 0;

		} catch (HibernateException e) {
			this.logAndThrowException("Error at checkIfLocationIDsExistInExperiments=" + locationIds + "," + dataSetId + ","
					+ " query at ExperimentDao: " + e.getMessage(), e);
		}

		return false;

	}

	@SuppressWarnings("rawtypes")
	public boolean checkIfPlotIdExists(final String plotId) {
		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("plotId", plotId));
			final List list = criteria.list();
			return list != null && !list.isEmpty();
		} catch (final HibernateException e) {
			this.logAndThrowException("Error at checkIfPlotIdExists=" + plotId + " query at ExperimentDao: " + e.getMessage(), e);
		}
		return true;
	}

	@SuppressWarnings("rawtypes")
	public boolean checkIfPlotIdsExist(final List<String> plotIds) {
		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.in("plotId", plotIds));
			final List list = criteria.list();
			return list != null && !list.isEmpty();
		} catch (final HibernateException e) {
			this.logAndThrowException("Error at checkIfPlotIdExists=" + plotIds + " query at ExperimentDao: " + e.getMessage(), e);
		}
		return true;
	}
}
