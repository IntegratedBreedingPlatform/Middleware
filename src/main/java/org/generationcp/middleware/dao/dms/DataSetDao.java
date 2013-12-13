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

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;

/**
 * DAO class for Dataset (Stored in {@link DmsProject}).
 * 
 * @author Donald Barre
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
			statement = getSession().createSQLQuery("delete e, ep, es, epheno, pheno, eprop " +
                       "from nd_experiment e, nd_experiment_project ep, " +
					   "nd_experiment_stock es, nd_experiment_phenotype epheno, phenotype pheno, nd_experimentprop eprop " + 
                       "where ep.project_id = " + datasetId +
					   "  and e.nd_experiment_id = ep.nd_experiment_id " +
					   "  and e.nd_experiment_id = es.nd_experiment_id " + 
                       "  and e.nd_experiment_id = epheno.nd_experiment_id " + 
					   "  and epheno.phenotype_id = pheno.phenotype_id " +
					   "  and e.nd_experiment_id = eprop.nd_experiment_id");
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
			logAndThrowException("Error in delete=" + datasetId + " in DataSetDao: " + e.getMessage(), e);
		}
	}

	public void deleteExperimentsByLocation(int datasetId, int locationId) throws MiddlewareQueryException {
		try {
			this.flush();
			
			// Delete experiments
			SQLQuery statement = getSession().createSQLQuery("delete e, ep, es, epheno, pheno, eprop " +
                       "from nd_experiment e, nd_experiment_project ep, " +
					   "nd_experiment_stock es, nd_experiment_phenotype epheno, phenotype pheno, nd_experimentprop eprop " + 
                       "where ep.project_id = " + datasetId +
                       "  and e.nd_geolocation_id = " + locationId +
					   "  and e.nd_experiment_id = ep.nd_experiment_id " +
					   "  and e.nd_experiment_id = es.nd_experiment_id " + 
                       "  and e.nd_experiment_id = epheno.nd_experiment_id " +
					   "  and epheno.phenotype_id = pheno.phenotype_id " +
					   "  and e.nd_experiment_id = eprop.nd_experiment_id");
			if (statement.executeUpdate() == 0){
				 statement = getSession().createSQLQuery("delete e, ep, es, epheno, pheno " +
	                       "from nd_experiment e, nd_experiment_project ep, " +
						   "nd_experiment_stock es, nd_experiment_phenotype epheno, phenotype pheno  " + 
	                       "where ep.project_id = " + datasetId +
	                       "  and e.nd_geolocation_id = " + locationId +
						   "  and e.nd_experiment_id = ep.nd_experiment_id " +
						   "  and e.nd_experiment_id = es.nd_experiment_id " + 
	                       "  and e.nd_experiment_id = epheno.nd_experiment_id " +
						   "  and epheno.phenotype_id = pheno.phenotype_id ");
				statement.executeUpdate();	   
			}
			
            this.flush();
            this.clear();

		} catch(HibernateException e) {
			logAndThrowException("Error in deleteExperimentsByLocation=" + datasetId + ", " + locationId + " in DataSetDao: " + e.getMessage(), e);
		}
	}
	
	public void deleteExperimentsByLocationAndType(int datasetId, int locationId, int typeId) throws MiddlewareQueryException {
		try {
			this.flush();
			
			// Delete experiments
			SQLQuery statement = getSession().createSQLQuery("delete e, ep, es, epheno, pheno, eprop " +
                       "from nd_experiment e, nd_experiment_project ep, " +
					   "nd_experiment_stock es, nd_experiment_phenotype epheno, phenotype pheno, nd_experimentprop eprop " + 
                       "where ep.project_id = " + datasetId +
                       "  and e.nd_geolocation_id = " + locationId +
                       "  and e.type_id = " + typeId +
					   "  and e.nd_experiment_id = ep.nd_experiment_id " +
					   "  and e.nd_experiment_id = es.nd_experiment_id " + 
                       "  and e.nd_experiment_id = epheno.nd_experiment_id " +
					   "  and epheno.phenotype_id = pheno.phenotype_id " +
					   "  and e.nd_experiment_id = eprop.nd_experiment_id");
			statement.executeUpdate();
			
            this.flush();
            this.clear();

		} catch(HibernateException e) {
			logAndThrowException("Error in deleteExperimentsByLocationAndType=" + datasetId + ", " + locationId + ", " + typeId + " in DataSetDao: " + e.getMessage(), e);
		}
	}
}
