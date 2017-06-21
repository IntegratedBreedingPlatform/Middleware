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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.dms.ExperimentStock;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link ExperimentStock}.
 *
 */
public class ExperimentStockDao extends GenericDAO<ExperimentStock, Integer> {

	@SuppressWarnings("unchecked")
	public List<Integer> getExperimentIdsByStockIds(Collection<Integer> stockIds) throws MiddlewareQueryException {
		try {
			if (stockIds != null && !stockIds.isEmpty()) {
				Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
				criteria.add(Restrictions.in("stockId", stockIds));
				criteria.setProjection(Projections.property("experimentId"));

				return criteria.list();
			}
		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error in getExperimentIdsByStockIds=" + stockIds + " query in ExperimentStockDao: " + e.getMessage(), e);
		}
		return new ArrayList<>();
	}

	@SuppressWarnings("unchecked")
	public Map<Integer, Set<Integer>> getEnvironmentsOfGermplasms(Set<Integer> gids) throws MiddlewareQueryException {
		Map<Integer, Set<Integer>> germplasmEnvironments = new HashMap<>();

		if (gids.isEmpty()) {
			return germplasmEnvironments;
		}

		for (Integer gid : gids) {
			germplasmEnvironments.put(gid, new HashSet<Integer>());
		}

		String sql =
				"SELECT DISTINCT s.dbxref_id, e.nd_geolocation_id " + "FROM nd_experiment e "
						+ "     INNER JOIN nd_experiment_stock es ON e.nd_experiment_id = es.nd_experiment_id "
						+ "     INNER JOIN stock s ON es.stock_id = s.stock_id AND s.dbxref_id IN (:gids) " + "ORDER BY s.dbxref_id ";
		try {
			Query query = this.getSession().createSQLQuery(sql).setParameterList("gids", gids);

			List<Object[]> result = query.list();

			for (Object[] row : result) {
				Integer gId = (Integer) row[0];
				Integer environmentId = (Integer) row[1];

				Set<Integer> gidEnvironments = germplasmEnvironments.get(gId);
				gidEnvironments.add(environmentId);
				germplasmEnvironments.remove(gId);
				germplasmEnvironments.put(gId, gidEnvironments);
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error at getEnvironmentsOfGermplasms(" + gids + ") query on ExperimentStockDao: " + e.getMessage(),
					e);
		}

		return germplasmEnvironments;

	}

	public long countStocksByDatasetId(int datasetId) throws MiddlewareQueryException {

		final StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(DISTINCT es.stock_id) FROM nd_experiment_project ep ")
			.append(" INNER JOIN nd_experiment_stock es ON es.nd_experiment_id = ep.nd_experiment_id ")
			.append(" WHERE ep.project_id = :datasetId");

		try {
			SQLQuery query = this.getSession().createSQLQuery(sql.toString());
			query.setParameter("datasetId", datasetId);
			BigInteger count = (BigInteger) query.uniqueResult();
			return count.longValue();

		} catch (HibernateException e) {
			this.logAndThrowException("Error at countStocksByDatasetId=" + datasetId + " query at ExperimentDao: " + e.getMessage(), e);
		}
		return 0;
	}

}
