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
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
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
	static final String DBXREF_ID = "dbxrefId";

	@SuppressWarnings("unchecked")
	List<Integer> getStockIdsByProperty(final String columnName, final String value)  {
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

	long countStudiesByGid(final int gid)  {

		try {
			final SQLQuery query = this.getSession()
					.createSQLQuery("select count(distinct p.study_id) " + "FROM stock s "
							+ "LEFT JOIN nd_experiment e on e.stock_id = s.stock_id "
							+ "LEFT JOIN project p ON e.project_id= p.project_id " + "WHERE s.dbxref_id = " + gid
							+ " AND p.deleted = 0");
			return ((BigInteger) query.uniqueResult()).longValue();

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error in countStudiesByGid=" + gid + StockDao.IN_STOCK_DAO + e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<StudyReference> getStudiesByGid(final int gid) {
		final List<StudyReference> studyReferences = new ArrayList<>();
		try {
			final SQLQuery query = this.getSession()
				.createSQLQuery("select distinct p.project_id, p.name, p.description, "
				+ "st.study_type_id, st.label, st.name as studyTypeName, st.visible, st.cvterm_id, p.program_uuid, p.locked, "
				+ "p.created_by "
				+ "FROM stock s "
				+ "LEFT JOIN nd_experiment e on e.stock_id = s.stock_id "
				+ "LEFT JOIN project ds ON ds.project_id = e.project_id "
				+ "LEFT JOIN project p ON ds.study_id = p.project_id "
				+ "INNER JOIN study_type st ON p.study_type_id = st.study_type_id "
				+ " WHERE s.dbxref_id = " + gid + " AND p.deleted = 0");
			query.addScalar("project_id").addScalar("name").addScalar("description").addScalar("study_type_id").addScalar("label")
					.addScalar("studyTypeName").addScalar("visible").addScalar("cvterm_id").addScalar("program_uuid").addScalar("locked")
					.addScalar("created_by");

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
				final String ownerId = (String) row[10];

				final StudyTypeDto studyTypeDto = new StudyTypeDto(studyTypeId, label, studyTypeName, cvtermId, visible);
				studyReferences.add(new StudyReference((Integer) row[0], (String) row[1], (String) row[2], programUUID, studyTypeDto,
						isLocked, Integer.valueOf(ownerId)));
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
			// Dataset ID can be for means, plot or sub-obs dataset
			final String sql = "select count(distinct plot.stock_id) "
					+ "from nd_experiment e "
					+ " inner join phenotype p ON e.nd_experiment_id = p.nd_experiment_id "
					+ " inner join project p ON p.project_id = e.project_id  "
					+ " inner join project plot_ds on plot_ds.study_id = p.study_id and plot_ds.dataset_type_id = "
					+ 	DatasetTypeEnum.PLOT_DATA.getId() + " "
					+ " inner join nd_experiment plot ON plot_ds.project_id = plot.project_id "
					+ "  WHERE plot.parent_id = :environmentId  and p.observable_id = " + variateStdVarId
					+ "  and e.project_id = :datasetId ";
			final Query query = this.getSession().createSQLQuery(sql);
			query.setParameter("environmentId", trialEnvironmentId);
			query.setParameter("datasetId", datasetId);


			return ((BigInteger) query.uniqueResult()).longValue();

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error at countStocks=" + datasetId + StockDao.IN_STOCK_DAO + e.getMessage(), e);
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

}
