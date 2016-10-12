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
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link StockModel}.
 *
 */
public class StockDao extends GenericDAO<StockModel, Integer> {

	@SuppressWarnings("unchecked")
	public List<Integer> getStockIdsByProperty(String columnName, String value) throws MiddlewareQueryException {
		List<Integer> stockIds = new ArrayList<Integer>();
		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			if ("dbxrefId".equals(columnName)) {
				criteria.add(Restrictions.eq(columnName, Integer.valueOf(value)));
			} else {
				criteria.add(Restrictions.eq(columnName, value));
			}
			criteria.setProjection(Projections.property("stockId"));

			stockIds = criteria.list();

		} catch (HibernateException e) {
			this.logAndThrowException("Error in getStockIdsByProperty=" + value + " in StockDao: " + e.getMessage(), e);
		}
		return stockIds;
	}

	public long countStudiesByGid(int gid) throws MiddlewareQueryException {

		try {
			SQLQuery query =
					this.getSession()
							.createSQLQuery(
									"select count(distinct p.project_id) "
					      					+ "FROM stock s "
					                + "LEFT JOIN nd_experiment_stock es ON s.stock_id = es.stock_id "
					                + "LEFT JOIN nd_experiment e on es.nd_experiment_id = e.nd_experiment_id "
					                + "LEFT JOIN nd_experiment_project ep ON ep.nd_experiment_id = e.nd_experiment_id "
					                + "LEFT JOIN project_relationship pr ON pr.subject_project_id = ep.project_id "
					                + "LEFT JOIN project p ON pr.object_project_id = p.project_id "
					                + "WHERE s.dbxref_id = " + gid
					                + " AND p.project_id NOT IN (SELECT pp.project_id FROM projectprop pp WHERE pp.type_id = " + TermId.STUDY_STATUS.getId()   
					                + " AND pp.value = " + TermId.DELETED_STUDY.getId() + ")");
			return ((BigInteger) query.uniqueResult()).longValue();

		} catch (HibernateException e) {
			this.logAndThrowException("Error in countStudiesByGid=" + gid + " in StockDao: " + e.getMessage(), e);
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	public List<StudyReference> getStudiesByGid(int gid, int start, int numOfRows) throws MiddlewareQueryException {
		List<StudyReference> studyReferences = new ArrayList<StudyReference>();
		try {
			SQLQuery query =
					this.getSession()
							.createSQLQuery(
									"select distinct p.project_id, p.name, p.description "
					      					+ "FROM stock s "
					                + "LEFT JOIN nd_experiment_stock es ON s.stock_id = es.stock_id "
					                + "LEFT JOIN nd_experiment e on es.nd_experiment_id = e.nd_experiment_id "
					                + "LEFT JOIN nd_experiment_project ep ON ep.nd_experiment_id = e.nd_experiment_id "
					                + "LEFT JOIN project_relationship pr ON pr.subject_project_id = ep.project_id "
					                + "LEFT JOIN project p ON pr.object_project_id = p.project_id "
					                + "WHERE s.dbxref_id = " + gid
					                + " AND p.project_id NOT IN (SELECT pp.project_id FROM projectprop pp WHERE pp.type_id = " + TermId.STUDY_STATUS.getId()   
					                + " AND pp.value = " + TermId.DELETED_STUDY.getId() + ")");
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);

			List<Object[]> results = query.list();
			for (Object[] row : results) {
				studyReferences.add(new StudyReference((Integer) row[0], (String) row[1], (String) row[2]));
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error in getStudiesByGid=" + gid + " in StockDao: " + e.getMessage(), e);
		}
		return studyReferences;
	}

	@SuppressWarnings("unchecked")
	public Set<StockModel> findInDataSet(int datasetId) throws MiddlewareQueryException {
		Set<StockModel> stockModels = new LinkedHashSet<StockModel>();
		try {

			String sql =
					"SELECT DISTINCT es.stock_id" + " FROM nd_experiment_stock es"
							+ " INNER JOIN nd_experiment e ON e.nd_experiment_id = es.nd_experiment_id"
							+ " INNER JOIN nd_experiment_project ep ON ep.nd_experiment_id = e.nd_experiment_id"
							+ " WHERE ep.project_id = :projectId ORDER BY es.stock_id";
			Query query = this.getSession().createSQLQuery(sql).setParameter("projectId", datasetId);
			List<Integer> ids = query.list();
			for (Integer id : ids) {
				stockModels.add(this.getById(id));
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error at findInDataSet=" + datasetId + " at StockDao: " + e.getMessage(), e);
		}
		return stockModels;
	}

	public long countStocks(int datasetId, int trialEnvironmentId, int variateStdVarId) throws MiddlewareQueryException {
		try {

			String sql =
					"select count(distinct nes.stock_id) "
							+ "from nd_experiment e, nd_experiment_phenotype ep, phenotype p, nd_experiment_stock nes, nd_experiment_project nep "
							+ "where e.nd_experiment_id = ep.nd_experiment_id  " + "  and ep.phenotype_id = p.phenotype_id  "
							+ "  and nes.nd_experiment_id = e.nd_experiment_id " + "  and nep.nd_experiment_id = e.nd_experiment_id "
							+ "  and e.nd_geolocation_id = " + trialEnvironmentId + "  and p.observable_id = " + variateStdVarId
							+ "  and nep.project_id = " + datasetId;
			Query query = this.getSession().createSQLQuery(sql);

			return ((BigInteger) query.uniqueResult()).longValue();

		} catch (HibernateException e) {
			this.logAndThrowException("Error at countStocks=" + datasetId + " at StockDao: " + e.getMessage(), e);
		}
		return 0;
	}

	public long countObservations(int datasetId, int trialEnvironmentId, int variateStdVarId) throws MiddlewareQueryException {
		try {

			String sql =
					"select count(e.nd_experiment_id) "
							+ "from nd_experiment e, nd_experiment_phenotype ep, phenotype p, nd_experiment_project nep "
							+ "where e.nd_experiment_id = ep.nd_experiment_id  " + "  and ep.phenotype_id = p.phenotype_id  "
							+ "  and nep.nd_experiment_id = e.nd_experiment_id " + "  and e.nd_geolocation_id = " + trialEnvironmentId
							+ "  and p.observable_id = " + variateStdVarId + "  and nep.project_id = " + datasetId
							+ "  and (trim(p.value) <> '' and p.value is not null)";
			Query query = this.getSession().createSQLQuery(sql);

			return ((BigInteger) query.uniqueResult()).longValue();

		} catch (HibernateException e) {
			this.logAndThrowException("Error at countObservations=" + datasetId + " at StockDao: " + e.getMessage(), e);
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	public List<StockModel> getStocks(int projectId) throws MiddlewareQueryException {
		List<StockModel> stocks = new ArrayList<StockModel>();

		try {

			StringBuilder sql =
					new StringBuilder().append("SELECT DISTINCT s.* ").append("FROM nd_experiment_project eproj  ")
							.append("   INNER JOIN nd_experiment_stock es ON eproj.nd_experiment_id = es.nd_experiment_id ")
							.append("       AND eproj.project_id = :projectId ")
							.append("   INNER JOIN stock s ON es.stock_id = s.stock_id ");

			Query query = this.getSession().createSQLQuery(sql.toString());
			query.setParameter("projectId", projectId);

			List<Object[]> list = query.list();

			if (list != null && !list.isEmpty()) {
				for (Object[] row : list) {
					Integer id = (Integer) row[0];
					Integer dbxrefId = (Integer) row[1];
					Integer organismId = (Integer) row[2];
					String name = (String) row[3];
					String uniqueName = (String) row[4];
					String value = (String) row[5];
					String description = (String) row[6];
					Integer typeId = (Integer) row[7];
					Boolean isObsolete = (Boolean) row[8];

					stocks.add(new StockModel(id, dbxrefId, organismId, name, uniqueName, value, description, typeId, isObsolete));
				}
			}

			return stocks;

		} catch (HibernateException e) {
			this.logAndThrowException("Error at getStocks(projectId=" + projectId + ") at StockDao: " + e.getMessage(), e);
		}

		return stocks;
	}

	@SuppressWarnings("unchecked")
	public Map<Integer, StockModel> getStocksByIds(List<Integer> ids) throws MiddlewareQueryException {
		Map<Integer, StockModel> stockModels = new HashMap<Integer, StockModel>();
		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.in("stockId", ids));
			List<StockModel> stocks = criteria.list();

			for (StockModel stock : stocks) {
				stockModels.put(stock.getStockId(), stock);
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error in getStocksByIds=" + ids + " in StockDao: " + e.getMessage(), e);
		}

		return stockModels;
	}

	public int countStockObservations(int datasetId, String nonEditableFactors) throws MiddlewareQueryException {
		try {

			StringBuilder sql =
					new StringBuilder().append("SELECT COUNT(sp.stockprop_id) ").append("FROM nd_experiment_stock es ")
							.append("INNER JOIN nd_experiment e ON e.nd_experiment_id = es.nd_experiment_id ")
							.append("INNER JOIN nd_experiment_project ep ON ep.nd_experiment_id = e.nd_experiment_id ")
							.append("INNER JOIN stockProp sp ON sp.stock_id = es.stock_id ").append("WHERE ep.project_id = ")
							.append(datasetId).append(" AND sp.type_id NOT IN (").append(nonEditableFactors).append(")");
			Query query = this.getSession().createSQLQuery(sql.toString());

			return ((BigInteger) query.uniqueResult()).intValue();

		} catch (HibernateException e) {
			this.logAndThrowException("Error at countStockObservations=" + datasetId + " at StockDao: " + e.getMessage(), e);
		}
		return 0;
	}
}
