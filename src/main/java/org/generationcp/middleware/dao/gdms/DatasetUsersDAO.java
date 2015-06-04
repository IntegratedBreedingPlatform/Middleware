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

package org.generationcp.middleware.dao.gdms;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.gdms.DatasetUsers;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;

/**
 * DAO class for {@link DatasetUsers}.
 *
 * <b>Authors</b>: Dennis Billano <br>
 * <b>File Created</b>: Mar 7, 2013
 */

public class DatasetUsersDAO extends GenericDAO<DatasetUsers, Integer> {

	public void deleteByDatasetId(int datasetId) throws MiddlewareQueryException {
		try {
			this.flush();

			SQLQuery statement = this.getSession().createSQLQuery("DELETE FROM gdms_dataset_users WHERE dataset_id = " + datasetId);
			statement.executeUpdate();

			this.flush();
			this.clear();

		} catch (HibernateException e) {
			this.logAndThrowException("Error in deleteByDatasetId=" + datasetId + " in DatasetUsersDAO: " + e.getMessage(), e);
		}
	}
}
