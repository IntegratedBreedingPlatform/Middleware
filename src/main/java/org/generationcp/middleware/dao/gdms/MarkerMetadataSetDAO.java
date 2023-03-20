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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.gdms.Dataset;
import org.generationcp.middleware.pojos.gdms.MarkerMetadataSet;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link MarkerMetadataSet}.
 *
 * @author Joyce Avestro
 *
 */
public class MarkerMetadataSetDAO extends GenericDAO<MarkerMetadataSet, Integer> {

	public static final String GET_MARKER_ID_BY_DATASET_ID = "SELECT marker_id " + "FROM gdms_marker_metadataset "
			+ "WHERE dataset_id = :datasetId " + "ORDER BY marker_id;";

	public static final String GET_MARKERS_BY_SAMPLEID_AND_DATASETS = "SELECT DISTINCT marker_id "
			+ "FROM gdms_marker_metadataset JOIN gdms_acc_metadataset "
			+ "        ON gdms_marker_metadataset.dataset_id = gdms_acc_metadataset.dataset_id "
			+ "WHERE gdms_marker_metadataset.dataset_id in (:datasetids)  " + "    AND gdms_acc_metadataset.sample_id = :sampleId "
			+ "ORDER BY gdms_marker_metadataset.marker_id ";

	public static final String COUNT_MARKERS_BY_SAMPLEID_AND_DATASETS = "SELECT COUNT(DISTINCT marker_id) "
			+ "FROM gdms_marker_metadataset JOIN gdms_acc_metadataset "
			+ "        ON gdms_marker_metadataset.dataset_id = gdms_acc_metadataset.dataset_id "
			+ "WHERE gdms_marker_metadataset.dataset_id in (:datasetids)  " + "    AND gdms_acc_metadataset.sample_id = :sampleId "
			+ "ORDER BY gdms_marker_metadataset.marker_id ";

	public static final String GET_BY_MARKER_IDS = "SELECT marker_metadataset_id, dataset_id, marker_id, marker_sample_id "
			+ "FROM gdms_marker_metadataset " + "WHERE  marker_id IN (:markerIdList) ";

	public static final String COUNT_MARKER_BY_DATASET_IDS = "SELECT COUNT(DISTINCT marker_id) " + "FROM gdms_marker_metadataset "
			+ "WHERE dataset_id IN (:datasetIds)";

	public MarkerMetadataSetDAO(final Session session) {
		super(session);
	}

	/**
	 * Gets the marker id by dataset id.
	 *
	 * @param datasetId the dataset id
	 * @return the marker id by dataset id
	 * @throws MiddlewareQueryException the MiddlewareQueryException
	 */
	@SuppressWarnings("unchecked")
	public List<Integer> getMarkerIdByDatasetId(final Integer datasetId) throws MiddlewareQueryException {
		try {
			if (datasetId != null) {
				SQLQuery query = this.getSession().createSQLQuery(MarkerMetadataSetDAO.GET_MARKER_ID_BY_DATASET_ID);
				query.setParameter("datasetId", datasetId);
				return query.list();
			}
		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error with getMarkerIdByDatasetId(datasetId=" + datasetId + ") query from MarkerMetadataSet: " + e.getMessage(), e);
		}
		return new ArrayList<Integer>();

	}

	@SuppressWarnings("unchecked")
	public List<Integer> getMarkersBySampleIdAndDatasetIds(
			final Integer sampleId, final List<Integer> datasetIds, final int start, final int numOfRows)
			throws MiddlewareQueryException {
		List<Integer> markerIds = new ArrayList<Integer>();

		try {
			if (sampleId != null && datasetIds != null) {
				SQLQuery query = this.getSession().createSQLQuery(MarkerMetadataSetDAO.GET_MARKERS_BY_SAMPLEID_AND_DATASETS);
				query.setParameterList("datasetids", datasetIds);
				query.setParameter("sampleId", sampleId);
				query.setFirstResult(start);
				query.setMaxResults(numOfRows);

				markerIds = query.list();
			} else {
				return new ArrayList<Integer>();
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getMarkersBySampleIdAndDatasetIds(sampleID=" + sampleId + ", datasetIds=" + datasetIds
					+ ") query from MarkerMetadataSet: " + e.getMessage(), e);
		}
		return markerIds;
	}

	public long countMarkersBySampleIdAndDatasetIds(final Integer sampleId, final List<Integer> datasetIds) throws MiddlewareQueryException {
		long count = 0;
		try {
			if (sampleId != null) {
				SQLQuery query = this.getSession().createSQLQuery(MarkerMetadataSetDAO.COUNT_MARKERS_BY_SAMPLEID_AND_DATASETS);
				query.setParameterList("datasetids", datasetIds);
				query.setParameter("sampleId", sampleId);
				BigInteger result = (BigInteger) query.uniqueResult();
				if (result != null) {
					count = result.longValue();
				}
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with countMarkersBySampleIdAndDatasetIds(sampleId=" + sampleId + ", datasetIds=" + datasetIds
					+ ") query from MarkerMetadataSet: " + e.getMessage(), e);
		}
		return count;
	}

	public long countByDatasetIds(final List<Integer> datasetIds) throws MiddlewareQueryException {
		long count = 0;
		try {
			if (datasetIds != null && !datasetIds.isEmpty()) {
				SQLQuery query = this.getSession().createSQLQuery(MarkerMetadataSetDAO.COUNT_MARKER_BY_DATASET_IDS);
				query.setParameterList("datasetIds", datasetIds);
				BigInteger result = (BigInteger) query.uniqueResult();
				if (result != null) {
					count = result.longValue();
				}
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error with countByDatasetIds=" + datasetIds + ") query from MarkerMetadataSet: " + e.getMessage(), e);
		}
		return count;
	}

	@SuppressWarnings("rawtypes")
	public List<MarkerMetadataSet> getByMarkerIds(final List<Integer> markerIds) throws MiddlewareQueryException {
		List<MarkerMetadataSet> toReturn = new ArrayList<MarkerMetadataSet>();
		try {
			if (markerIds != null && !markerIds.isEmpty()) {
				SQLQuery query = this.getSession().createSQLQuery(MarkerMetadataSetDAO.GET_BY_MARKER_IDS);
				query.setParameterList("markerIdList", markerIds);

				List results = query.list();
				for (final Object o : results) {
					Object[] result = (Object[]) o;
					if (result != null) {
						Integer markerMetadatasetId = (Integer) result[0];
						Integer datasetId = (Integer) result[1];
						Integer markerId2 = (Integer) result[2];
						Integer markerSampleId = (Integer) result[3];

						Dataset dataset = new Dataset();
						dataset.setDatasetId(datasetId);

						MarkerMetadataSet dataElement = new MarkerMetadataSet(markerMetadatasetId, dataset, markerId2, markerSampleId);
						toReturn.add(dataElement);
					}
				}
			}
		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error with getByMarkerIds(markerIds=" + markerIds + ") query from MarkerMetadataSet: " + e.getMessage(), e);
		}
		return toReturn;

	}

	public void deleteByDatasetId(final Integer datasetId) throws MiddlewareQueryException {
		try {
			// Please note we are manually flushing because non hibernate based deletes and updates causes the Hibernate session to get out of synch with
			// underlying database. Thus flushing to force Hibernate to synchronize with the underlying database before the delete
			// statement
			this.getSession().flush();
			
			SQLQuery statement = this.getSession().createSQLQuery("DELETE FROM gdms_marker_metadataset WHERE dataset_id = " + datasetId);
			statement.executeUpdate();

		} catch (HibernateException e) {
			this.logAndThrowException("Error in deleteByDatasetId=" + datasetId + " in MarkerMetadataSetDAO: " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("rawtypes")
	public List<MarkerMetadataSet> getMarkerMetadataSetsByDatasetId(final Integer datasetId) throws MiddlewareQueryException {

		List<MarkerMetadataSet> toReturn = new ArrayList<MarkerMetadataSet>();
		try {
			if (datasetId != null) {
				SQLQuery query =
						this.getSession().createSQLQuery(
								"SELECT  marker_metadataset_id, dataset_id, marker_id, marker_sample_id"
										+ " FROM gdms_marker_metadataset where dataset_id = :datasetId ");
				query.setParameter("datasetId", datasetId);

				List results = query.list();
				for (final Object o : results) {
					Object[] result = (Object[]) o;
					if (result != null) {
						Integer markerMetadatasetId = (Integer) result[0];
						Integer datasetId2 = (Integer) result[1];
						Integer markerId = (Integer) result[0];
						Integer markerSampleId = (Integer) result[1];

						Dataset dataset = new Dataset();
						dataset.setDatasetId(datasetId2);

						MarkerMetadataSet dataElement = new MarkerMetadataSet(markerMetadatasetId, dataset, markerId, markerSampleId);
						toReturn.add(dataElement);
					}
				}
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getMarkerMetadataSetsByDatasetId(datasetId=" + datasetId
					+ ") query from MarkerMetadataSet " + e.getMessage(), e);
		}
		return toReturn;
	}

	@SuppressWarnings("unchecked")
	public List<MarkerMetadataSet> getMarkerMetadataSetByDatasetId(final Integer datasetId) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("datasetId", datasetId));
			return criteria.list();

		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error in getMarkerMetadataSetByDatasetId=" + datasetId + " query on MarkerMetadataSetDao: " + e.getMessage(), e);
		}

		return new ArrayList<MarkerMetadataSet>();

	}

}
