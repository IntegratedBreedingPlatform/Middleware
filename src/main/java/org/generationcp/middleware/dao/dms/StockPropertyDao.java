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
import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.dms.StockProperty;
import org.generationcp.middleware.util.Debug;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link StockProperty}.
 *
 */
public class StockPropertyDao extends GenericDAO<StockProperty, Integer> {

	@SuppressWarnings("unchecked")
	public List<Integer> getStockIdsByPropertyTypeAndValue(Integer typeId, String value) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("typeId", typeId));
			criteria.add(Restrictions.eq("value", value));
			criteria.setProjection(Projections.property("stockModel.stockId"));

			return criteria.list();

		} catch (HibernateException e) {
			this.logAndThrowException("Error at getStockIdsByPropertyTypeAndValue=" + typeId + ", " + value
					+ " query on StockPropertyDao: " + e.getMessage(), e);
		}
		return new ArrayList<Integer>();
	}

	public void deleteStockPropInProjectByTermId(Integer projectId, int termId) throws MiddlewareQueryException {
		try {
			// Please note we are manually flushing because non hibernate based deletes and updates causes the Hibernate session to get out of synch with
			// underlying database. Thus flushing to force Hibernate to synchronize with the underlying database before the delete
			// statement
			this.getSession().flush();
			
			StringBuilder sql =
					new StringBuilder().append("DELETE FROM stockprop ").append(" WHERE stock_id IN ( ").append(" SELECT s.stock_id ")
							.append(" FROM stock s ").append(" INNER JOIN nd_experiment_stock e ON s.stock_id = e.stock_id ")
							.append(" INNER JOIN nd_experiment nde ON e.nd_experiment_id = nde.nd_experiment_id ")
							.append(" AND nde.project_id = ").append(projectId);
			sql.append(") ").append(" AND type_id =").append(termId);

			SQLQuery query = this.getSession().createSQLQuery(sql.toString());
			Debug.println("DELETE STOCKPROP ROWS FOR " + termId + " : " + query.executeUpdate());

		} catch (HibernateException e) {
			this.logAndThrowException("Error in deleteStockPropInProjectByTermId(" + projectId + ", " + termId + ") in StockPropertyDao: "
					+ e.getMessage(), e);
		}
	}

}
