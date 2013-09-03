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
package org.generationcp.middleware.dao.dms;

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

    @SuppressWarnings("unchecked")
    public Map<Integer, Set<Integer>> getEnvironmentsOfGermplasms(Set<Integer> gids) throws MiddlewareQueryException{
        Map<Integer, Set<Integer>> germplasmEnvironments = new HashMap<Integer, Set<Integer>>();
        
        if(gids.size() == 0){
            return germplasmEnvironments;
        }
        
        for(Integer gid : gids){
            germplasmEnvironments.put(gid, new HashSet<Integer>());
        }
        
        String sql = "SELECT DISTINCT s.dbxref_id, e.nd_geolocation_id "
                    + "FROM nd_experiment e "
                    + "     INNER JOIN nd_experiment_stock es ON e.nd_experiment_id = es.nd_experiment_id " 
                    + "     INNER JOIN stock s ON es.stock_id = s.stock_id AND s.dbxref_id IN (:gids) "
                    + "ORDER BY s.dbxref_id "
                    ;
        try{
            Query query = getSession().createSQLQuery(sql).setParameterList(
                    "gids", gids);

            List<Object[]> result = query.list();

            for (Object[] row : result) {
                Integer stockId = (Integer) row[0];
                Integer environmentId = (Integer) row[1];

                Set<Integer> stockEnvironments = germplasmEnvironments
                        .get(stockId);
                stockEnvironments.add(environmentId);
                germplasmEnvironments.remove(stockId);
                germplasmEnvironments.put(stockId, stockEnvironments);
            }

        } catch (HibernateException e) {
            logAndThrowException(
                    "Error at getEnvironmentsOfGermplasms(" + gids + ") query on ExperimentStockDao: " + e.getMessage(), e);
        }


        return germplasmEnvironments;

    }
   
}
