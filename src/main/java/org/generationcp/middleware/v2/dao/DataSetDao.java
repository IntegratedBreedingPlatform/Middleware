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

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.v2.pojos.DmsProject;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;

/**
 * 
 * @author Don
 *
 */

public class DataSetDao extends GenericDAO<DmsProject, Integer> {

	public void delete(int datasetId) throws MiddlewareQueryException {
		try {
			this.flush();
			
			// Delete from project relationship
			SQLQuery statement = getSession().createSQLQuery("delete pr " +
		                                                     "from project_relationship pr " + 
					                                         "where pr.subject_project_id = " + datasetId);
			statement.executeUpdate();
			
			// Delete experiments
			statement = getSession().createSQLQuery("delete e, eprop, ep, es, epheno, pheno " +
                       "from nd_experiment e, nd_experimentprop eprop, nd_experiment_project ep, " +
					   "nd_experiment_stock es, nd_experiment_phenotype epheno, phenotype pheno " + 
                       "where ep.project_id = " + datasetId +
					   "  and e.nd_experiment_id = ep.nd_experiment_id " +
                       "  and e.nd_experiment_id = eprop.nd_experiment_id " + 
					   "  and e.nd_experiment_id = es.nd_experiment_id " + 
                       "  and e.nd_experiment_id = epheno.nd_experiment_id " + 
					   "  and epheno.phenotype_id = pheno.phenotype_id");
			statement.executeUpdate();
			
			// Delete project stuff
			statement = getSession().createSQLQuery("delete p, pp " +
                                           "from project p, projectprop pp " + 
                                           "where p.project_id = " + datasetId +
                                           "  and p.project_id = pp.project_id");
            statement.executeUpdate();
            this.flush();
            this.clear();

		} catch(HibernateException e) {
			e.printStackTrace();
			logAndThrowException("Error in delete=" + datasetId + " in DataSetDao: " + e.getMessage(), e);
		}
	}
}
