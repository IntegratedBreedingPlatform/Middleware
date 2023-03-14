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

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.gdms.DartValues;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

/**
 * DAO class for {@link DartValues}.
 *
 * <b>Authors</b>: Dennis Billano <br>
 * <b>File Created</b>: Mar 8, 2013
 */

public class DartValuesDAO extends GenericDAO<DartValues, Integer> {

	public DartValuesDAO(final Session session) {
		super(session);
	}

	public void deleteByDatasetId(int datasetId) throws MiddlewareQueryException {
		try {
			// Please note we are manually flushing because non hibernate based deletes and updates causes the Hibernate session to get out of synch with
			// underlying database. Thus flushing to force Hibernate to synchronize with the underlying database before the delete
			// statement
			this.getSession().flush();
			
			SQLQuery statement = this.getSession().createSQLQuery("DELETE FROM gdms_dart_values WHERE dataset_id = " + datasetId);
			statement.executeUpdate();
		} catch (HibernateException e) {
			this.logAndThrowException("Error in deleteByDatasetId=" + datasetId + " in DartValuesDAO: " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("rawtypes")
	public List<DartValues> getDartValuesByDatasetId(Integer datasetId) throws MiddlewareQueryException {

		List<DartValues> toReturn = new ArrayList<DartValues>();
		try {
			if (datasetId != null) {
				SQLQuery query = this.getSession().createSQLQuery("SELECT * " + " FROM gdms_dart_values where dataset_id = :datasetId ");
				query.setParameter("datasetId", datasetId);

				List results = query.list();
				for (Object o : results) {
					Object[] result = (Object[]) o;
					if (result != null) {
						Integer adId = (Integer) result[0];
						Integer datasetId2 = (Integer) result[1];
						Integer markerId = (Integer) result[2];
						Integer cloneId = (Integer) result[3];
						Float qValue = (Float) result[4];
						Float reproducibility = (Float) result[5];
						Float callRate = (Float) result[6];
						Float picValue = (Float) result[7];
						Float discordance = (Float) result[8];

						DartValues dataElement =
								new DartValues(adId, datasetId2, markerId, cloneId, qValue, reproducibility, callRate, picValue,
										discordance);
						toReturn.add(dataElement);
					}
				}
			}
		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error with getDartValuesByDatasetId(datasetId=" + datasetId + ") query from DartValues " + e.getMessage(), e);
		}
		return toReturn;
	}

	@SuppressWarnings("rawtypes")
	public List<DartValues> getDartValuesByMarkerIds(List<Integer> markerIds) throws MiddlewareQueryException {

		List<DartValues> toReturn = new ArrayList<DartValues>();
		try {
			if (markerIds != null && !markerIds.isEmpty()) {
				SQLQuery query = this.getSession().createSQLQuery("SELECT * " + " FROM gdms_dart_values where marker_id IN (:markerIds) ");
				query.setParameterList("markerIds", markerIds);

				List results = query.list();
				for (Object o : results) {
					Object[] result = (Object[]) o;
					if (result != null) {
						Integer adId = (Integer) result[0];
						Integer datasetId2 = (Integer) result[1];
						Integer markerId = (Integer) result[2];
						Integer cloneId = (Integer) result[3];
						Float qValue = (Float) result[4];
						Float reproducibility = (Float) result[5];
						Float callRate = (Float) result[6];
						Float picValue = (Float) result[7];
						Float discordance = (Float) result[8];

						DartValues dataElement =
								new DartValues(adId, datasetId2, markerId, cloneId, qValue, reproducibility, callRate, picValue,
										discordance);
						toReturn.add(dataElement);
					}
				}
			}
		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error with getDartValuesByMarkerIds(markerIds=" + markerIds + ") query from DartValues " + e.getMessage(), e);
		}
		return toReturn;
	}

}
