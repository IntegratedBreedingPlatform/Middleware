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

	private static final String IN_STOCK_DAO = " in StockDao: ";
	protected static final String DBXREF_ID = "dbxrefId";

	@SuppressWarnings("unchecked")
	public List<Integer> getStockIdsByProperty(final String columnName, final String value)  {
		final List<Integer> stockIds;
		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			if (DBXREF_ID.equals(columnName)) {
				criteria.add(Restrictions.eq("germplasm.gid", Integer.valueOf(value)));
			} else {
				criteria.add(Restrictions.eq(columnName, value));
			}
			criteria.setProjection(Projections.property("stockId"));

			stockIds = criteria.list();

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error in getStockIdsByProperty=" + value + StockDao.IN_STOCK_DAO + e.getMessage(), e);
		}
		return stockIds;
	}

	public long countStudiesByGid(final int gid)  {

		try {
			final SQLQuery query = this.getSession()
					.createSQLQuery("select count(distinct p.project_id) " + "FROM stock s "
							+ "LEFT JOIN nd_experiment e on e.stock_id = s.stock_id "
							+ "LEFT JOIN project_relationship pr ON pr.subject_project_id = e.project_id "
							+ "LEFT JOIN project p ON pr.object_project_id = p.project_id " + "WHERE s.dbxref_id = " + gid
							+ " AND p.deleted = 0");
			return ((BigInteger) query.uniqueResult()).longValue();

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error in countStudiesByGid=" + gid + StockDao.IN_STOCK_DAO + e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<StudyReference> getStudiesByGid(final int gid, final int start, final int numOfRows) {
		final List<StudyReference> studyReferences = new ArrayList<>();
		try {
			final SQLQuery query = this.getSession()
				.createSQLQuery("select distinct p.project_id, p.name, p.description, "
				+ "st.study_type_id, st.label, st.name as studyTypeName, st.visible, st.cvterm_id, p.program_uuid, p.locked, "
				+ "u.userId, CONCAT(fname, ' ', lname) as ownerName "
				+ "FROM stock s "
				+ "LEFT JOIN nd_experiment e on e.stock_id = s.stock_id "
				+ "LEFT JOIN project_relationship pr ON pr.subject_project_id = e.project_id "
				+ "LEFT JOIN project p ON pr.object_project_id = p.project_id "
				+ "INNER JOIN study_type st ON p.study_type_id = st.study_type_id "
				+ "LEFT JOIN users u ON u.userid = p.created_by "
				+ "LEFT JOIN persons per ON per.personid = u.personid "
				+ " WHERE s.dbxref_id = " + gid + " AND p.deleted = 0");
			query.addScalar("project_id").addScalar("name").addScalar("description").addScalar("study_type_id").addScalar("label")
					.addScalar("studyTypeName").addScalar("visible").addScalar("cvterm_id").addScalar("program_uuid").addScalar("locked")
					.addScalar("userId").addScalar("ownerName");
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
				final String programUUID = (String) row[8];
				final Boolean isLocked = (Boolean) row[9];
				final Integer ownerId = (Integer) row[10];
				final String ownerName = (String) row[11];
				
				final StudyTypeDto studyTypeDto = new StudyTypeDto(studyTypeId, label, studyTypeName, cvtermId, visible);
				studyReferences.add(new StudyReference((Integer) row[0], (String) row[1], (String) row[2], programUUID, studyTypeDto,
						isLocked, ownerId, ownerName));
			}

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error in getStudiesByGid=" + gid + StockDao.IN_STOCK_DAO + e.getMessage(), e);
		}
		return studyReferences;
	}

	@SuppressWarnings("unchecked")
	public Set<StockModel> findInDataSet(final int datasetId)  {
		final Set<StockModel> stockModels = new LinkedHashSet<>();
		try {

			final String sql = "SELECT DISTINCT e.stock_id" + " FROM nd_experiment e "
					+ " WHERE e.project_id = :projectId ORDER BY e.stock_id";
			final Query query = this.getSession().createSQLQuery(sql);
			query.setParameter("projectId", datasetId);
			final List<Integer> ids = query.list();
			for (final Integer id : ids) {
				stockModels.add(this.getById(id));
			}

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error in findInDataSet=" + datasetId + StockDao.IN_STOCK_DAO + e.getMessage(), e);
		}
		return stockModels;
	}

	public long countStocks(final int datasetId, final int trialEnvironmentId, final int variateStdVarId)  {
		try {

			final String sql = "select count(distinct e.stock_id) "
					+ "from nd_experiment e, phenotype p "
					+ "where e.nd_experiment_id = p.nd_experiment_id  "
					+ "  and e.nd_geolocation_id = " + trialEnvironmentId + "  and p.observable_id = " + variateStdVarId
					+ "  and e.project_id = " + datasetId;
			final Query query = this.getSession().createSQLQuery(sql);

			return ((BigInteger) query.uniqueResult()).longValue();

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error at countStocks=" + datasetId + StockDao.IN_STOCK_DAO + e.getMessage(), e);
		}
	}

	public long countObservations(final int datasetId, final int trialEnvironmentId, final int variateStdVarId)
			 {
		try {

			final String sql = "select count(e.nd_experiment_id) "
					+ "from nd_experiment e, phenotype p "
					+ "where e.nd_experiment_id = p.nd_experiment_id  "
					+ "  and e.nd_geolocation_id = " + trialEnvironmentId
					+ "  and p.observable_id = " + variateStdVarId + "  and e.project_id = " + datasetId
					+ "  and (trim(p.value) <> '' and p.value is not null)";
			final Query query = this.getSession().createSQLQuery(sql);

			return ((BigInteger) query.uniqueResult()).longValue();

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error at countObservations=" + datasetId + " at StockDao: " + e.getMessage(), e);
		}
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
			throw new MiddlewareQueryException("Error in getStocksByIds=" + ids + StockDao.IN_STOCK_DAO + e.getMessage(), e);
		}

		return stockModels;
	}

	public int countStockObservations(final int datasetId, final String nonEditableFactors)  {
		try {

			final StringBuilder sql = new StringBuilder().append("SELECT COUNT(sp.stockprop_id) ")
					.append("FROM nd_experiment e ")
					.append("INNER JOIN stockProp sp ON sp.stock_id = e.stock_id ").append("WHERE e.project_id = ").append(datasetId)
					.append(" AND sp.type_id NOT IN (").append(nonEditableFactors).append(")");
			final Query query = this.getSession().createSQLQuery(sql.toString());

			return ((BigInteger) query.uniqueResult()).intValue();

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error at countStockObservations=" + datasetId + " at StockDao: " + e.getMessage(), e);
		}
	}
}
