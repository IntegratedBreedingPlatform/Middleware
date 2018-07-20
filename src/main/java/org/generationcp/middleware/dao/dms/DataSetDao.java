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

	public void delete(final int datasetId) throws MiddlewareQueryException {
		try {
			// Please note we are manually flushing because non hibernate based deletes and updates causes the Hibernate session to get out of synch with
			// underlying database. Thus flushing to force Hibernate to synchronize with the underlying database before the delete
			// statement
			this.getSession().flush();
			
			// Delete from project relationship
			SQLQuery statement =
					this.getSession().createSQLQuery(
							"delete pr " + "from project_relationship pr " + "where pr.subject_project_id = " + datasetId);
			statement.executeUpdate();

			// Delete experiments
			statement =
					this.getSession().createSQLQuery(
							"delete e, pheno, eprop " + "from nd_experiment e, "
									+ "phenotype pheno, nd_experimentprop eprop "
									+ "where e.project_id = " + datasetId
									+ "  and e.nd_experiment_id = pheno.nd_experiment_id "
									+ "  and e.nd_experiment_id = eprop.nd_experiment_id");
			statement.executeUpdate();

			// Delete project stuff
			statement =
					this.getSession().createSQLQuery(
							"delete p, pp " + "from project p, projectprop pp " + "where p.project_id = " + datasetId
									+ "  and p.project_id = pp.project_id");
			statement.executeUpdate();

		} catch (final HibernateException e) {
			this.logAndThrowException("Error in delete=" + datasetId + " in DataSetDao: " + e.getMessage(), e);
		}
	}

	public void deleteExperimentsByLocation(final int datasetId, final int locationId) throws MiddlewareQueryException {
		try {
			// Please note we are manually flushing because non hibernate based deletes and updates causes the Hibernate session to get out of synch with
			// underlying database. Thus flushing to force Hibernate to synchronize with the underlying database before the delete
			// statement
			this.getSession().flush();
			
			// Delete experiments
			SQLQuery statement =
					this.getSession().createSQLQuery(
							"delete e, pheno, eprop " + "from nd_experiment e, "
									+ "phenotype pheno, nd_experimentprop eprop "
									+ "where e.project_id = " + datasetId + "  and e.nd_geolocation_id = " + locationId
									+ "  and e.nd_experiment_id = pheno.nd_experiment_id "
									+ "  and e.nd_experiment_id = eprop.nd_experiment_id");
			if (statement.executeUpdate() == 0) {
				statement =
						this.getSession().createSQLQuery(
								"delete e, pheno " + "from nd_experiment e, "
										+ "phenotype pheno  "
										+ "where e.project_id = " + datasetId + "  and e.nd_geolocation_id = " + locationId
										+ "  and e.nd_experiment_id = pheno.nd_experiment_id ");
				statement.executeUpdate();
			}

		} catch (final HibernateException e) {
			this.logAndThrowException(
					"Error in deleteExperimentsByLocation=" + datasetId + ", " + locationId + " in DataSetDao: " + e.getMessage(), e);
		}
	}

	public void deleteExperimentsByLocationAndType(final int datasetId, final int locationId, final int typeId) throws MiddlewareQueryException {
		try {
			// Please note we are manually flushing because non hibernate based deletes and updates causes the Hibernate session to get out of synch with
			// underlying database. Thus flushing to force Hibernate to synchronize with the underlying database before the delete
			// statement
			this.getSession().flush();
			
			// Delete experiments
			final SQLQuery statement =
					this.getSession().createSQLQuery(
							"delete e, pheno, eprop " + "from nd_experiment e, "
									+ "phenotype pheno, nd_experimentprop eprop "
									+ "where e.project_id = " + datasetId + "  and e.nd_geolocation_id = " + locationId
									+ "  and e.type_id = " + typeId
									+ "  and e.nd_experiment_id = pheno.nd_experiment_id "
									+ "  and e.nd_experiment_id = eprop.nd_experiment_id");
			statement.executeUpdate();
		} catch (final HibernateException e) {
			this.logAndThrowException("Error in deleteExperimentsByLocationAndType=" + datasetId + ", " + locationId + ", " + typeId
					+ " in DataSetDao: " + e.getMessage(), e);
		}
	}
}
