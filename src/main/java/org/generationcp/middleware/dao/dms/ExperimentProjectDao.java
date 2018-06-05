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
import java.util.Collection;
import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.dms.ExperimentProject;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link ExperimentProject}.
 *
 */
public class ExperimentProjectDao extends GenericDAO<ExperimentProject, Integer> {

	@SuppressWarnings("unchecked")
	public List<Integer> getProjectIdsByExperimentIds(Collection<Integer> experimentIds) throws MiddlewareQueryException {
		try {
			if (experimentIds != null && !experimentIds.isEmpty()) {
				boolean first = true;
				StringBuffer buf = new StringBuffer();
				for (int i = 0; i < experimentIds.size(); i++) {
					if (first) {
						first = false;
						buf.append("?");
					} else {
						buf.append(",?");
					}
				}
				SQLQuery query =
						this.getSession().createSQLQuery(
								"select distinct ep.project_id from nd_experiment_project ep where ep.nd_experiment_id in (" + buf + ")");
				int index = 0;
				for (Integer id : experimentIds) {
					query.setParameter(index, id);
					index++;
				}
				return query.list();
			}

		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error at getProjectIdsByExperimentIds=" + experimentIds + " query at ExperimentDao: " + e.getMessage(), e);
		}
		return new ArrayList<Integer>();

	}

	@SuppressWarnings("unchecked")
	public List<ExperimentProject> getExperimentProjects(int projectId, int typeId, int start, int numOfRows)
			throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("projectId", projectId));
			criteria.createAlias("experiment", "experiment").add(Restrictions.eq("experiment.typeId", typeId));
			criteria.setMaxResults(numOfRows);
			criteria.setFirstResult(start);
			return criteria.list();
		} catch (HibernateException e) {
			this.logAndThrowException("Error at getExperimentProjects=" + projectId + ", " + typeId + " query at ExperimentProjectDao: "
					+ e.getMessage(), e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<ExperimentProject> getExperimentProjects(int projectId, List<TermId> types, int start, int numOfRows)
			throws MiddlewareQueryException {
		try {

			List<Integer> lists = new ArrayList<Integer>();
			for (TermId termId : types) {
				lists.add(termId.getId());
			}

			StringBuilder queryString = new StringBuilder();
			queryString.append("select distinct ep from ExperimentProject as ep ");
			queryString.append("inner join ep.experiment as exp ");
			queryString.append("left outer join exp.properties as plot with plot.typeId IN (8200,8380) ");
			queryString.append("left outer join exp.properties as rep with rep.typeId = 8210 ");
			queryString.append("left outer join exp.experimentStocks as es ");
			queryString.append("left outer join es.stock as st ");
			queryString.append("where ep.projectId =:p_id and ep.experiment.typeId in (:type_ids) ");
			queryString.append("order by (ep.experiment.geoLocation.description * 1) ASC, ");
			queryString.append("(plot.value * 1) ASC, ");
			queryString.append("(rep.value * 1) ASC, ");
			queryString.append("(st.uniqueName * 1) ASC, ");
			queryString.append("ep.experiment.ndExperimentId ASC");

			Query q =
					this.getSession().createQuery(queryString.toString())//
							.setParameter("p_id", projectId) //
							.setParameterList("type_ids", lists) //
							.setMaxResults(numOfRows) //
							.setFirstResult(start);

			return q.list();
		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error at getExperimentProjects=" + projectId + ", " + types + " query at ExperimentProjectDao: " + e.getMessage(), e);
			return null;
		}
	}

	public long count(int dataSetId) throws MiddlewareQueryException {
		try {
			return (Long) this.getSession().createQuery("select count(*) from ExperimentProject where project_id = " + dataSetId)
					.uniqueResult();
		} catch (HibernateException e) {
			this.logAndThrowException("Error at getExperimentProjects=" + dataSetId + " query at ExperimentProjectDao: " + e.getMessage(),
					e);
			return 0;
		}
	}

	public int getExperimentIdByLocationIdStockId(int projectId, Integer locationId, Integer stockId) throws MiddlewareQueryException {
		try {
			// update the value of phenotypes
			String sql =
					"SELECT exp.nd_experiment_id " + "FROM nd_experiment_project ep "
							+ "INNER JOIN nd_experiment exp ON ep.nd_experiment_id = exp.nd_experiment_id "
							+ "INNER JOIN nd_experiment_stock expstock ON expstock.nd_experiment_id = exp.nd_experiment_id  "
							+ "INNER JOIN stock ON expstock.stock_id = stock.stock_id " + " WHERE ep.project_id = " + projectId
							+ " AND exp.nd_geolocation_id = " + locationId + " AND exp.type_id = 1170 " + " AND stock.stock_id = "
							+ stockId;

			SQLQuery statement = this.getSession().createSQLQuery(sql);
			Integer returnVal = (Integer) statement.uniqueResult();

			if (returnVal == null) {
				return 0;
			} else {
				return returnVal.intValue();
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error in getExperimentIdByLocationIdStockId=" + projectId + ", " + locationId
					+ " in ExperimentProjectDao: " + e.getMessage(), e);
			return 0;
		}
	}

	@SuppressWarnings("unchecked")
	public Integer getExperimentIdByProjectId(int projectId) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("projectId", projectId));
			criteria.setProjection(Projections.property("experiment.ndExperimentId"));
			List<Integer> list = criteria.list();
			if (list != null && !list.isEmpty()) {
				return list.get(0);
			}
		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error at getExperimentIdByProjectId=" + projectId + ", " + " query at ExperimentProjectDao: " + e.getMessage(), e);
		}
		return null;
	}

}
