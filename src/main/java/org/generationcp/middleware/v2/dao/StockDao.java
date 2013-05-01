package org.generationcp.middleware.v2.dao;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.v2.pojos.Stock;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public class StockDao extends GenericDAO<Stock, Integer> {
	
	
	@SuppressWarnings("unchecked")
	public List<Integer> getStockIdsByProperty(String columnName, String value) throws MiddlewareQueryException {
		List<Integer> stockIds = new ArrayList<Integer>();
		try {
			Criteria criteria = getSession().createCriteria(getPersistentClass());
			if ("dbxrefId".equals(columnName)) {
				criteria.add(Restrictions.eq(columnName, Integer.valueOf(value)));
			} else {
				criteria.add(Restrictions.eq(columnName, value));
			}
			criteria.setProjection(Projections.property("stockId"));
		
			stockIds = criteria.list();
			
		} catch(HibernateException e) {
			logAndThrowException("Error in getStockIdsByProperty=" + value + " in StockDao: " + e.getMessage(), e);
		}
		return stockIds;
	}
}
