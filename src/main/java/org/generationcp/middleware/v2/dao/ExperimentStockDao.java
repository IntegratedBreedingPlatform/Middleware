/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.middleware.v2.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.v2.pojos.ExperimentStock;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
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
			if (stockIds != null && stockIds.size() > 0) {
				Criteria criteria = getSession().createCriteria(getPersistentClass());
				criteria.add(Restrictions.in("stockId", stockIds));
				criteria.setProjection(Projections.property("experimentId"));
				
				return criteria.list();
			}
		} catch (HibernateException e) {
			logAndThrowException("Error in getExperimentIdsByStockIds=" + stockIds + " query in ExperimentStockDao: " + e.getMessage(), e);
		}
		return new ArrayList<Integer>();
	}
}
