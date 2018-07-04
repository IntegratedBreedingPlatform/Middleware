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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DAO class for {@link ExperimentStock}.
 *
 */
public class ExperimentStockDao extends GenericDAO<ExperimentStock, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(ExperimentStockDao.class);

	@SuppressWarnings("unchecked")
	public List<Integer> getExperimentIdsByStockIds(final Collection<Integer> stockIds) {
		try {
			if (stockIds != null && !stockIds.isEmpty()) {
				final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
				criteria.add(Restrictions.in("stockId", stockIds));
				criteria.setProjection(Projections.property("experimentId"));

				return criteria.list();
			}
		} catch (final HibernateException e) {
			final String error = "Error in getExperimentIdsByStockIds=" + stockIds + " query in ExperimentStockDao: " + e.getMessage();
			ExperimentStockDao.LOG.error(error);
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
				+ "     INNER JOIN nd_experiment_stock es ON e.nd_experiment_id = es.nd_experiment_id "
				+ "     INNER JOIN stock s ON es.stock_id = s.stock_id AND s.dbxref_id IN (:gids) ";
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
					+ ") query on ExperimentStockDao: " + e.getMessage();
			ExperimentStockDao.LOG.error(error);
			throw new MiddlewareQueryException(error, e);
		}

		return germplasmEnvironments;

	}

	public long countStocksByDatasetId(final int datasetId) {

		final StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(DISTINCT es.stock_id) FROM nd_experiment e ")
				.append(" INNER JOIN nd_experiment_stock es ON es.nd_experiment_id = e.nd_experiment_id ")
				.append(" WHERE e.project_id = :datasetId");

		try {
			final SQLQuery query = this.getSession().createSQLQuery(sql.toString());
			query.setParameter("datasetId", datasetId);
			final BigInteger count = (BigInteger) query.uniqueResult();
			return count.longValue();

		} catch (final HibernateException e) {
			final String error = "Error at countStocksByDatasetId=" + datasetId + " query at ExperimentStockDao: " + e.getMessage();
			ExperimentStockDao.LOG.error(error);
			throw new MiddlewareQueryException(error, e);
		}
	}

}
