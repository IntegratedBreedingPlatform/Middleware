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

import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.collections.map.MultiKeyMap;
import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.dms.StockProperty;
import org.generationcp.middleware.util.Debug;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import java.util.List;
import java.util.Optional;

/**
 * DAO class for {@link StockProperty}.
 *
 */
public class StockPropertyDao extends GenericDAO<StockProperty, Integer> {

	public static final String MODEL_STOCK_ID = "stockModel.stockId";
	public static final String TYPE_ID = "typeId";

	public StockPropertyDao(final Session session) {
		super(session);
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getStockIdsByPropertyTypeAndValue(final Integer typeId, final String value) {
		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq(TYPE_ID, typeId));
			criteria.add(Restrictions.eq("value", value));
			criteria.setProjection(Projections.property(MODEL_STOCK_ID));

			return criteria.list();

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error at getStockIdsByPropertyTypeAndValue=" + typeId + ", " + value
					+ " query on StockPropertyDao: " + e.getMessage(), e);
		}
	}

	public void deleteStockPropInProjectByTermId(final Integer projectId, final int termId) {
		try {
			// Please note we are manually flushing because non hibernate based deletes and updates causes the Hibernate session to get out of synch with
			// underlying database. Thus flushing to force Hibernate to synchronize with the underlying database before the delete
			// statement
			this.getSession().flush();

			final StringBuilder sql =
					new StringBuilder().append("DELETE FROM stockprop ").append(" WHERE stock_id IN ( ").append(" SELECT s.stock_id ")
							.append(" FROM stock s ")
							.append(" INNER JOIN nd_experiment nde ON s.stock_id = nde.stock_id ")
							.append(" AND nde.project_id = ").append(projectId);
			sql.append(") ").append(" AND type_id =").append(termId);

			final SQLQuery query = this.getSession().createSQLQuery(sql.toString());
			Debug.println("DELETE STOCKPROP ROWS FOR " + termId + " : " + query.executeUpdate());

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error in deleteStockPropInProjectByTermId(" + projectId + ", " + termId + ") in StockPropertyDao: "
					+ e.getMessage(), e);
		}
	}

	public boolean updateByStockIdsAndTypeId(final List<Integer> stockIds, final Integer typeId, final String entryTypeId,
		final String value) {
		try {
			this.getSession().flush();

			final String queryString = "UPDATE stockprop SET value = :value, cvalue_id = :entryTypeId WHERE type_id = :typeId AND stock_id IN (:stockIds)";
			final SQLQuery query = this.getSession().createSQLQuery(queryString);
			query.setParameter("value", value);
			query.setParameter("entryTypeId", entryTypeId);
			query.setParameter(TYPE_ID, typeId);
			query.setParameterList("stockIds", stockIds);
			return query.executeUpdate() > 0;

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error in updateByStockIdsAndTypeId(" + stockIds + ", " + typeId  + ", " + entryTypeId
				+ ") in StockPropertyDao: "	+ e.getMessage(), e);
		}
	}

	/**
	 * @param stockIds list of stock IDs to filter
	 * @return MultiKeyMap with stock ID and type ID as keys and StockProperty object as value
	 */
	public MultiKeyMap getStockPropertiesMap(final List<Integer> stockIds) {
		final Criteria criteria = this.getSession().createCriteria(StockProperty.class);
		criteria.add(Restrictions.in(MODEL_STOCK_ID, stockIds));
		final List<StockProperty> stockPropertyList = criteria.list();

		final MultiKeyMap stockPropertyMap = MultiKeyMap.decorate(new LinkedMap());
		stockPropertyList.forEach(stockProperty ->
			stockPropertyMap.put(stockProperty.getStock().getStockId(), stockProperty.getTypeId(), stockProperty)
		);

		return stockPropertyMap;
	}

	public Optional<StockProperty> getByStockIdAndTypeId(final Integer stockId, final Integer typeId) {
		final Criteria criteria = this.getSession().createCriteria(StockProperty.class);
		criteria.add(Restrictions.eq(MODEL_STOCK_ID, stockId));
		criteria.add(Restrictions.eq(TYPE_ID, typeId));
		return Optional.ofNullable((StockProperty) criteria.uniqueResult());
	}

	public long countObservationsByStudyIdAndVariableIds(final Integer studyId, final List<Integer> variableIds) {
		if (variableIds.isEmpty()) {
			return 0l;
		}
		final Criteria criteria = this.getSession().createCriteria(StockProperty.class);
		criteria.createAlias("stockModel", "stockModel");
		criteria.createAlias("stockModel.project", "study");
		criteria.add(Restrictions.eq("study.projectId", studyId));
		criteria.add(Restrictions.in(TYPE_ID, variableIds));
		criteria.setProjection(Projections.rowCount());
		return (Long) criteria.uniqueResult();
	}

}
