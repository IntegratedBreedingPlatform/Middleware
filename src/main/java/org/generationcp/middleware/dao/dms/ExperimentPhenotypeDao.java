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

import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.dms.ExperimentPhenotype;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link ExperimentPhenotype}.
 * 
 */
public class ExperimentPhenotypeDao extends GenericDAO<ExperimentPhenotype, Integer> {
    
    public ExperimentPhenotype getbyExperimentAndPhenotype(int experimentId, int phenotypeId) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(getPersistentClass());
            criteria.add(Restrictions.eq("experiment", experimentId));
            criteria.add(Restrictions.eq("phenotype", phenotypeId));
            @SuppressWarnings("rawtypes")
            List list = criteria.list();
            if (list != null && !list.isEmpty()) {
                return (ExperimentPhenotype) list.get(0);
            }
        } catch (HibernateException e) {
            logAndThrowException("Error at getbyExperimentAndPhenotype=" + experimentId + "," + phenotypeId 
                    + " query at ExperimentPhenotypeDao: " + e.getMessage(), e);
        }
        return null;
    }

    public void deleteByStudyIdAndTermId(List<Integer> ids, int termId) throws MiddlewareQueryException {
        try {
        	StringBuilder sql = new StringBuilder()
        		.append("DELETE FROM nd_experiment_phenotype ")
        		.append(" WHERE nd_experiment_id IN ( ")
        		.append(" SELECT ep.nd_experiment_id ")
        		.append(" FROM nd_experiment_project ep ")
        		.append(" WHERE ep.project_id IN (:ids)) ")
        		.append(" AND phenotype_id NOT IN (SELECT phenotype_id FROM phenotype) ");

        	SQLQuery query = getSession().createSQLQuery(sql.toString());
  			query.setParameter("ids", ids);
  			query.executeUpdate();
    	
        } catch (HibernateException e) {
            logAndThrowException("Error at deleteByStudyIdAndTermId=" + ids + "," + termId 
                    + " query at ExperimentPhenotypeDao: " + e.getMessage(), e);
        }
    }
}
