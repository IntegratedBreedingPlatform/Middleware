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

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * DAO class for {@link StockModel}.
 *
 */
public class StockDao extends GenericDAO<StockModel, Integer> {

	@SuppressWarnings("unchecked")
	public List<Integer> getStockIdsByProperty(final String columnName, final String value)  {
		final List<Integer> stockIds;
		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			if ("dbxrefId".equals(columnName)) {
				criteria.add(Restrictions.eq(columnName, Integer.valueOf(value)));
			} else {
				criteria.add(Restrictions.eq(columnName, value));
			}
			criteria.setProjection(Projections.property("stockId"));

			stockIds = criteria.list();

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error in getStockIdsByProperty=" + value + " in StockDao: " + e.getMessage(), e);
		}
		return stockIds;
	}

	public long countStudiesByGid(final int gid)  {

		try {
			final SQLQuery query = this.getSession()
					.createSQLQuery("select count(distinct p.project_id) " + "FROM stock s "
							+ "LEFT JOIN nd_experiment_stock es ON s.stock_id = es.stock_id "
							+ "LEFT JOIN nd_experiment e on es.nd_experiment_id = e.nd_experiment_id "
							+ "LEFT JOIN nd_experiment_project ep ON ep.nd_experiment_id = e.nd_experiment_id "
							+ "LEFT JOIN project_relationship pr ON pr.subject_project_id = ep.project_id "
							+ "LEFT JOIN project p ON pr.object_project_id = p.project_id " + "WHERE s.dbxref_id = " + gid
							+ " AND p.deleted = 0");
			return ((BigInteger) query.uniqueResult()).longValue();

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error in countStudiesByGid=" + gid + " in StockDao: " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<StudyReference> getStudiesByGid(final int gid, final int start, final int numOfRows) {
		final List<StudyReference> studyReferences = new ArrayList<>();
		try {
			final SQLQuery query = this.getSession()//
				.createSQLQuery("select distinct p.project_id, p.name, p.description, "
				+ "st.study_type_id, st.label, st.name, st.visible, st.cvterm_id, p.program_uuid "
				+ "FROM stock s "
				+ "LEFT JOIN nd_experiment_stock es ON s.stock_id = es.stock_id "
				+ "LEFT JOIN nd_experiment e on es.nd_experiment_id = e.nd_experiment_id "
				+ "LEFT JOIN nd_experiment_project ep ON ep.nd_experiment_id = e.nd_experiment_id "
				+ "LEFT JOIN project_relationship pr ON pr.subject_project_id = ep.project_id "
				+ "LEFT JOIN project p ON pr.object_project_id = p.project_id "
				+ "INNER JOIN study_type st ON p.study_type_id = st.study_type_id "
				+ " WHERE s.dbxref_id = " + gid + " AND p.deleted = 0");
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);

			final List<Object[]> results = query.list();
			for (final Object[] row : results) {
				if (row[0] == null) {
					continue;
				}
				final Integer studyTypeId = (Integer) row[3];
				final String label = (String) row[4];
				final String studyTypeName = (String) row[5];
				final boolean visible = ((Byte) row[6]) == 1;
				final Integer cvtermId = (Integer) row[7];
				final StudyTypeDto studyTypeDto = new StudyTypeDto(studyTypeId, label, studyTypeName, cvtermId, visible);
				studyReferences.add(new StudyReference((Integer) row[0], (String) row[1], (String) row[2], (String) row[8], studyTypeDto));
			}

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error in getStudiesByGid=" + gid + " in StockDao: " + e.getMessage(), e);
		}
		return studyReferences;
	}

	@SuppressWarnings("unchecked")
	public Set<StockModel> findInDataSet(final int datasetId)  {
		final Set<StockModel> stockModels = new LinkedHashSet<>();
		try {

			final String sql = "SELECT DISTINCT es.stock_id" + " FROM nd_experiment_stock es"
					+ " INNER JOIN nd_experiment e ON e.nd_experiment_id = es.nd_experiment_id"
					+ " INNER JOIN nd_experiment_project ep ON ep.nd_experiment_id = e.nd_experiment_id"
					+ " WHERE ep.project_id = :projectId ORDER BY es.stock_id";
			final Query query = this.getSession().createSQLQuery(sql).setParameter("projectId", datasetId);
			final List<Integer> ids = query.list();
			for (final Integer id : ids) {
				stockModels.add(this.getById(id));
			}

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error in findInDataSet=" + datasetId + " at StockDao: " + e.getMessage(), e);
		}
		return stockModels;
	}

	public long countStocks(final int datasetId, final int trialEnvironmentId, final int variateStdVarId)  {
		try {

			final String sql = "select count(distinct nes.stock_id) "
					+ "from nd_experiment e, nd_experiment_phenotype ep, phenotype p, nd_experiment_stock nes, nd_experiment_project nep "
					+ "where e.nd_experiment_id = ep.nd_experiment_id  " + "  and ep.phenotype_id = p.phenotype_id  "
					+ "  and nes.nd_experiment_id = e.nd_experiment_id " + "  and nep.nd_experiment_id = e.nd_experiment_id "
					+ "  and e.nd_geolocation_id = " + trialEnvironmentId + "  and p.observable_id = " + variateStdVarId
					+ "  and nep.project_id = " + datasetId;
			final Query query = this.getSession().createSQLQuery(sql);

			return ((BigInteger) query.uniqueResult()).longValue();

		} catch (final HibernateException e) {
			this.logAndThrowException("Error at countStocks=" + datasetId + " at StockDao: " + e.getMessage(), e);
		}
		return 0;
	}

	public long countObservations(final int datasetId, final int trialEnvironmentId, final int variateStdVarId)
			 {
		try {

			final String sql = "select count(e.nd_experiment_id) "
					+ "from nd_experiment e, nd_experiment_phenotype ep, phenotype p, nd_experiment_project nep "
					+ "where e.nd_experiment_id = ep.nd_experiment_id  " + "  and ep.phenotype_id = p.phenotype_id  "
					+ "  and nep.nd_experiment_id = e.nd_experiment_id " + "  and e.nd_geolocation_id = " + trialEnvironmentId
					+ "  and p.observable_id = " + variateStdVarId + "  and nep.project_id = " + datasetId
					+ "  and (trim(p.value) <> '' and p.value is not null)";
			final Query query = this.getSession().createSQLQuery(sql);

			return ((BigInteger) query.uniqueResult()).longValue();

		} catch (final HibernateException e) {
			this.logAndThrowException("Error at countObservations=" + datasetId + " at StockDao: " + e.getMessage(), e);
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	public List<StockModel> getStocks(final int projectId)  {
		final List<StockModel> stocks = new ArrayList<>();

		try {

			final StringBuilder sql = new StringBuilder().append("SELECT DISTINCT s.* ").append("FROM nd_experiment_project eproj  ")
					.append("   INNER JOIN nd_experiment_stock es ON eproj.nd_experiment_id = es.nd_experiment_id ")
					.append("       AND eproj.project_id = :projectId ").append("   INNER JOIN stock s ON es.stock_id = s.stock_id ");

			final Query query = this.getSession().createSQLQuery(sql.toString());
			query.setParameter("projectId", projectId);

			final List<Object[]> list = query.list();

			if (list != null && !list.isEmpty()) {
				for (final Object[] row : list) {
					final Integer id = (Integer) row[0];
					final Integer dbxrefId = (Integer) row[1];
					final Integer organismId = (Integer) row[2];
					final String name = (String) row[3];
					final String uniqueName = (String) row[4];
					final String value = (String) row[5];
					final String description = (String) row[6];
					final Integer typeId = (Integer) row[7];
					final Boolean isObsolete = (Boolean) row[8];

					stocks.add(new StockModel(id, dbxrefId, organismId, name, uniqueName, value, description, typeId, isObsolete));
				}
			}

			return stocks;

		} catch (final HibernateException e) {
			this.logAndThrowException("Error at getStocks(projectId=" + projectId + ") at StockDao: " + e.getMessage(), e);
		}

		return stocks;
	}

	@SuppressWarnings("unchecked")
	public Map<Integer, StockModel> getStocksByIds(final List<Integer> ids)  {
		final Map<Integer, StockModel> stockModels = new HashMap<>();
		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.in("stockId", ids));
			final List<StockModel> stocks = criteria.list();

			for (final StockModel stock : stocks) {
				stockModels.put(stock.getStockId(), stock);
			}

		} catch (final HibernateException e) {
			this.logAndThrowException("Error in getStocksByIds=" + ids + " in StockDao: " + e.getMessage(), e);
		}

		return stockModels;
	}

	public int countStockObservations(final int datasetId, final String nonEditableFactors)  {
		try {

			final StringBuilder sql = new StringBuilder().append("SELECT COUNT(sp.stockprop_id) ").append("FROM nd_experiment_stock es ")
					.append("INNER JOIN nd_experiment e ON e.nd_experiment_id = es.nd_experiment_id ")
					.append("INNER JOIN nd_experiment_project ep ON ep.nd_experiment_id = e.nd_experiment_id ")
					.append("INNER JOIN stockProp sp ON sp.stock_id = es.stock_id ").append("WHERE ep.project_id = ").append(datasetId)
					.append(" AND sp.type_id NOT IN (").append(nonEditableFactors).append(")");
			final Query query = this.getSession().createSQLQuery(sql.toString());

			return ((BigInteger) query.uniqueResult()).intValue();

		} catch (final HibernateException e) {
			this.logAndThrowException("Error at countStockObservations=" + datasetId + " at StockDao: " + e.getMessage(), e);
		}
		return 0;
	}
}
