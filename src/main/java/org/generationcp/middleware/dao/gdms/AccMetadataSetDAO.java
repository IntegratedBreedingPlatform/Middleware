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
import org.generationcp.middleware.manager.SetOperation;
import org.generationcp.middleware.pojos.Sample;
import org.generationcp.middleware.pojos.gdms.AccMetadataSet;
import org.generationcp.middleware.pojos.gdms.Dataset;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.IntegerType;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * DAO class for {@link AccMetadataSet}.
 *
 * @author Joyce Avestro
 *
 */
public class AccMetadataSetDAO extends GenericDAO<AccMetadataSet, Integer> {

	public static final String GET_DATASET_IDS_BY_GERMPLASM_IDS = "SELECT dataset_id " + "FROM gdms_acc_metadataset " + "WHERE gid IN (:gIdList)";

	public static final String GET_ACC_METADATASETS_BY_DATASET_ID_AND_IN_GIDS = "SELECT acc_metadataset_id, sample_id "
			+ "FROM gdms_acc_metadataset " + "WHERE sample_id IN (:sampleIds) " + "AND dataset_id = :datasetId";

	public static final String GET_ACC_METADATASETS_BY_DATASET_ID_AND_NOT_IN_GIDS = "SELECT acc_metadataset_id, gid, nid "
			+ "FROM gdms_acc_metadataset " + "WHERE gid NOT IN (:gids) " + "AND dataset_id = :datasetId";

	public static final String COUNT_ACC_METADATASETS_BY_GIDS = "SELECT COUNT(*) " + "FROM gdms_acc_metadataset " + "WHERE gid IN (:gids) ";

	public static final String GET_NIDS_BY_DATASET_IDS_AND_MARKER_IDS_AND_NOT_GIDS_SELECT = "SELECT DISTINCT nid ";

	public static final String COUNT_NIDS_BY_DATASET_IDS_AND_MARKER_IDS_AND_NOT_GIDS_SELECT = "SELECT COUNT(DISTINCT nid) ";

	public static final String GET_NIDS_BY_DATASET_IDS_AND_MARKER_IDS_AND_NOT_GIDS_FROM = "FROM gdms_acc_metadataset gam "
			+ "INNER JOIN gdms_marker_metadataset gmm on gmm.dataset_id = gam.dataset_id " + "WHERE gam.dataset_id IN (:represnos) ";

	public static final String GET_NIDS_BY_DATASET_IDS_FILTER_BY_MARKER_IDS = "AND gmm.marker_id IN (:markerids) ";

	public static final String GET_NIDS_BY_DATASET_IDS_FILTER_NOT_BY_GIDS = "AND gam.gid NOT IN (:gids) ";

	public static final String GET_NIDS_BY_DATASET_IDS_ORDER = "ORDER BY nid DESC";

	public static final String GET_NIDS_BY_DATASET_IDS_AND_MARKER_IDS = "SELECT DISTINCT nid from gdms_acc_metadataset gam "
			+ "INNER JOIN gdms_marker_metadataset gmm on gmm.dataset_id = gam.dataset_id " + "WHERE gam.dataset_id IN (:represnos) ";

	public static final String COUNT_SAMPLE_IDS_BY_DATASET_IDS =
			"SELECT COUNT(DISTINCT sample_id) FROM gdms_acc_metadataset WHERE dataset_id IN (:datasetIds)";

	public static final String GET_SAMPLE_ID_BY_DATASET =
		"SELECT distinct sample_id, acc_sample_id from gdms_acc_metadataset where dataset_id = (:datasetId) order by sample_id ,acc_sample_id asc";

	public static final String GET_UNIQUE_ACC_METADATASET_BY_GIDS = "select distinct gid,nid, acc_sample_id from gdms_acc_metadataset where gid in (:gids)" 
			+ " order by gid, nid,acc_sample_id asc";

	private static final String GET_NIDS_BY_DATASET_IDS = "SELECT nid FROM gdms_acc_metadataset WHERE dataset_id IN (:datasetIds)";

	public AccMetadataSetDAO(final Session session) {
		super(session);
	}

	//FIXME
	@SuppressWarnings("unchecked")
	public List<Integer> getDatasetIdsByGermplasmIds(List<Integer> gIds) throws MiddlewareQueryException {
		try {
			if (gIds != null && !gIds.isEmpty()) {
				SQLQuery query = this.getSession().createSQLQuery(AccMetadataSetDAO.GET_DATASET_IDS_BY_GERMPLASM_IDS);
				query.setParameterList("gIdList", gIds);
				return query.list();
			}
		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error with getDatasetIdsByGermplasmIds(" + gIds + ") query from AccMetadataSet: " + e.getMessage(), e);
		}
		return new ArrayList<Integer>();
	}
	
	@SuppressWarnings("unchecked")
	// FIXME implement start and numOfRows
	public List<AccMetadataSet> getByDatasetIds(List<Integer> datasetIds, int start, int numOfRows)
			throws MiddlewareQueryException {
		try {
			if (datasetIds != null && !datasetIds.isEmpty()) {
				return this.getSession()
					.createCriteria(this.getPersistentClass())
					.add(Restrictions.in("dataset.datasetId", datasetIds))
					.list();
			}
		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error with getByDatasetIds(datasetIds=" + datasetIds
					+ ") query from AccMetadataSet: " + e.getMessage(), e);
		}
		return new ArrayList<>();
	}

	@SuppressWarnings("unchecked")
	//FIXME
	public Set<Integer> getNIdsByMarkerIdsAndDatasetIdsAndNotGIds(List<Integer> datasetIds, List<Integer> markerIds, List<Integer> gIds,
			int start, int numOfRows) throws MiddlewareQueryException {
		try {

			if (datasetIds != null && !datasetIds.isEmpty()) {
				StringBuilder queryString = new StringBuilder(AccMetadataSetDAO.GET_NIDS_BY_DATASET_IDS_AND_MARKER_IDS_AND_NOT_GIDS_SELECT);
				queryString.append(AccMetadataSetDAO.GET_NIDS_BY_DATASET_IDS_AND_MARKER_IDS_AND_NOT_GIDS_FROM);

				if (markerIds != null && !markerIds.isEmpty()) {
					queryString.append(AccMetadataSetDAO.GET_NIDS_BY_DATASET_IDS_FILTER_BY_MARKER_IDS);
				}

				if (gIds != null && !gIds.isEmpty()) {
					queryString.append(AccMetadataSetDAO.GET_NIDS_BY_DATASET_IDS_FILTER_NOT_BY_GIDS);
				}

				queryString.append(AccMetadataSetDAO.GET_NIDS_BY_DATASET_IDS_ORDER);

				SQLQuery query;
				query = this.getSession().createSQLQuery(queryString.toString());
				query.setParameterList("represnos", datasetIds);
				if (markerIds != null && !markerIds.isEmpty()) {
					query.setParameterList("markerids", markerIds);
				}
				if (gIds != null && !gIds.isEmpty()) {
					query.setParameterList("gids", gIds);
				}
				query.setFirstResult(start);
				query.setMaxResults(numOfRows);
				return new TreeSet<Integer>(query.list());
			}

		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error with getNIDsByDatasetIdsAndMarkerIdsAndNotGIDs(datasetIds=" + datasetIds + ", markerIds="
					+ markerIds + ", gIds=" + gIds + ") query from AccMetadataSet: " + e.getMessage(), e);
		}
		return new TreeSet<Integer>();
	}

	//FIXME
	public long countNIdsByMarkerIdsAndDatasetIdsAndNotGIds(List<Integer> datasetIds, List<Integer> markerIds, List<Integer> gIds)
			throws MiddlewareQueryException {
		try {

			if (datasetIds != null && !datasetIds.isEmpty()) {
				StringBuilder queryString =
						new StringBuilder(AccMetadataSetDAO.COUNT_NIDS_BY_DATASET_IDS_AND_MARKER_IDS_AND_NOT_GIDS_SELECT);
				queryString.append(AccMetadataSetDAO.GET_NIDS_BY_DATASET_IDS_AND_MARKER_IDS_AND_NOT_GIDS_FROM);

				if (markerIds != null && !markerIds.isEmpty()) {
					queryString.append(AccMetadataSetDAO.GET_NIDS_BY_DATASET_IDS_FILTER_BY_MARKER_IDS);
				}

				if (gIds != null && !gIds.isEmpty()) {
					queryString.append(AccMetadataSetDAO.GET_NIDS_BY_DATASET_IDS_FILTER_NOT_BY_GIDS);
				}

				queryString.append(AccMetadataSetDAO.GET_NIDS_BY_DATASET_IDS_ORDER);

				SQLQuery query;
				query = this.getSession().createSQLQuery(queryString.toString());
				query.setParameterList("represnos", datasetIds);
				if (markerIds != null && !markerIds.isEmpty()) {
					query.setParameterList("markerids", markerIds);
				}
				if (gIds != null && !gIds.isEmpty()) {
					query.setParameterList("gids", gIds);
				}

				return ((BigInteger) query.uniqueResult()).intValue();
			}

		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error with countNIDsByDatasetIdsAndMarkerIdsAndNotGIDs(datasetIds=" + datasetIds + ", markerIds="
					+ markerIds + ", gIds=" + gIds + ") query from AccMetadataSet: " + e.getMessage(), e);
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	//FIXME
	public Set<Integer> getNIdsByMarkerIdsAndDatasetIds(List<Integer> datasetIds, List<Integer> markerIds) throws MiddlewareQueryException {
		try {

			if (datasetIds != null && !datasetIds.isEmpty()) {

				StringBuilder queryString = new StringBuilder(AccMetadataSetDAO.GET_NIDS_BY_DATASET_IDS_AND_MARKER_IDS);

				if (markerIds != null && !markerIds.isEmpty()) {
					queryString.append(AccMetadataSetDAO.GET_NIDS_BY_DATASET_IDS_FILTER_BY_MARKER_IDS);
				}
				queryString.append(AccMetadataSetDAO.GET_NIDS_BY_DATASET_IDS_ORDER);

				SQLQuery query;
				query = this.getSession().createSQLQuery(queryString.toString());
				query.setParameterList("represnos", datasetIds);
				if (markerIds != null && !markerIds.isEmpty()) {
					query.setParameterList("markerids", markerIds);
				}
				return new TreeSet<Integer>(query.list());
			}

		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error with getNIdsByMarkerIdsAndDatasetIds(datasetIds=" + datasetIds + ", markerIds=" + markerIds
					+ ") query from AccMetadataSet: " + e.getMessage(), e);
		}
		return new TreeSet<Integer>();
	}

	@SuppressWarnings("unchecked")
	//FIXME
	public List<AccMetadataSet> getAccMetadataSetsByGids(List<Integer> gids, int start, int numOfRows) throws MiddlewareQueryException {
		List<AccMetadataSet> dataValues = new ArrayList<AccMetadataSet>();
		try {

			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.in("germplasmId", gids));
			dataValues = criteria.list();
		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error with getAccMetadataSetByGids(gids=" + gids + ") query from AccMetadataSet: " + e.getMessage(),
					e);
		}
		return dataValues;
	}

	//FIXME
	public long countAccMetadataSetsByGids(List<Integer> gids) throws MiddlewareQueryException {
		long count = 0;
		try {
			if (gids != null && !gids.isEmpty()) {
				SQLQuery query = this.getSession().createSQLQuery(AccMetadataSetDAO.COUNT_ACC_METADATASETS_BY_GIDS);
				query.setParameterList("gids", gids);
				BigInteger result = (BigInteger) query.uniqueResult();
				if (result != null) {
					count = result.longValue();
				}
			}
		} catch (HibernateException e) {
			throw new MiddlewareQueryException(
					"Error with countAccMetadataSetByGids(gids=" + gids + ") query from AccMetadataSet: " + e.getMessage(), e);
		}
		return count;
	}

	//FIXME
	public List<Integer> getNidsByDatasetIds(List<Integer> datasetIds) throws MiddlewareQueryException {
		List<Integer> results = new ArrayList<>();
		try{
				SQLQuery query = this.getSession().createSQLQuery(AccMetadataSetDAO.GET_NIDS_BY_DATASET_IDS);
				query.setParameterList("datasetIds", datasetIds);
				results = query.list();

		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error with getNidsByDatasetIds=" + datasetIds + ") query from AccMetadataSet: " + e.getMessage(),
					e);
		}
		return results;
	}

	public long countSampleIdsByDatasetIds(List<Integer> datasetIds) throws MiddlewareQueryException {
		long count = 0;
		try {
			if (datasetIds != null && !datasetIds.isEmpty()) {
				SQLQuery query = this.getSession().createSQLQuery(AccMetadataSetDAO.COUNT_SAMPLE_IDS_BY_DATASET_IDS);
				query.setParameterList("datasetIds", datasetIds);
				BigInteger result = (BigInteger) query.uniqueResult();
				if (result != null) {
					count = result.longValue();
				}
			}

		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error with countSampleIdsByDatasetIds=" + datasetIds + ") query from AccMetadataSet", e);
		}
		return count;
	}

	@SuppressWarnings("rawtypes")
	//FIXME
	public List<AccMetadataSet> getAccMetadataSetByGidsAndDatasetId(List<Integer> sampleIds, Integer datasetId, SetOperation operation)
			throws MiddlewareQueryException {

		Dataset dataset1 = new Dataset();
		dataset1.setDatasetId(datasetId);

		List<AccMetadataSet> dataValues = new ArrayList<AccMetadataSet>();
		try {
			if (sampleIds != null && !sampleIds.isEmpty()) {

				String queryString =
						SetOperation.IN.equals(operation) ? AccMetadataSetDAO.GET_ACC_METADATASETS_BY_DATASET_ID_AND_IN_GIDS
								: AccMetadataSetDAO.GET_ACC_METADATASETS_BY_DATASET_ID_AND_NOT_IN_GIDS;
				SQLQuery query = this.getSession().createSQLQuery(queryString);
				query.setParameterList("sampleIds", sampleIds);
				query.setParameter("datasetId", datasetId);

				List results = query.list();
				for (Object o : results) {
					Object[] result = (Object[]) o;
					if (result != null) {
						Integer accMetadataSetId = (Integer) result[0];
						Integer sampleId = (Integer) result[1];
						Sample sample = new Sample();
						sample.setSampleId(sampleId);
						AccMetadataSet dataElement = new AccMetadataSet(accMetadataSetId, dataset1, sample, null);
						dataValues.add(dataElement);
					}
				}
			}
		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error with getAccMetadataSetByGidsAndDatasetId(sampleIds=" + sampleIds + ", datasetId=" + datasetId
					+ ") query from AccMetadataSet: " + e.getMessage(), e);
		}
		return dataValues;
	}

	// FIXME, use hibernate to delete
	public void deleteByDatasetId(Integer datasetId) throws MiddlewareQueryException {
		try {
			// Please note we are manually flushing because non hibernate based deletes and updates causes the Hibernate session to get out of synch with
			// underlying database. Thus flushing to force Hibernate to synchronize with the underlying database before the delete
			// statement
			this.getSession().flush();
			
			SQLQuery statement = this.getSession().createSQLQuery("DELETE FROM gdms_acc_metadataset WHERE dataset_id = " + datasetId);
			statement.executeUpdate();

		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error in deleteByDatasetId=" + datasetId + " in AccMetadataSetDAO: " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	//Reviewed
	public List<AccMetadataSet> getAccMetadataSetsByDatasetId(Integer datasetId) throws MiddlewareQueryException {
		List<AccMetadataSet> dataValues = new ArrayList<AccMetadataSet>();
		try {

			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("datasetId", datasetId));
			dataValues = criteria.list();
		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error with getAccMetadataSetsByDatasetId(datasetId=" + datasetId + ") query from AccMetadataSet "
					+ e.getMessage(), e);
		}
		return dataValues;
	}
	
	@SuppressWarnings("unchecked")
	public List<Object> getUniqueAccMetaDatsetByDatasetId(String datasetId) throws MiddlewareQueryException {
		try {
				SQLQuery query = this.getSession().createSQLQuery(AccMetadataSetDAO.GET_SAMPLE_ID_BY_DATASET);
				query.setParameter("datasetId", datasetId);
				query.addScalar("sample_id");
				query.addScalar("acc_sample_id");
				return query.list();
		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error with getUniqueAccMetaDatasetByDatasetId(" + datasetId + ") query from AccMetadataSet: " + e.getMessage(), e);
		}
	}
	
	@SuppressWarnings("unchecked")
	//FIXME
	public List<Object> getUniqueAccMetaDatsetByGids(List<Integer> gids) throws MiddlewareQueryException {
		try {
				SQLQuery query = this.getSession().createSQLQuery(AccMetadataSetDAO.GET_UNIQUE_ACC_METADATASET_BY_GIDS);
				query.setParameterList("gids", gids);
				query.addScalar("gid", IntegerType.INSTANCE);
				query.addScalar("nid", IntegerType.INSTANCE);
				query.addScalar("acc_sample_id", IntegerType.INSTANCE);
				return query.list();
		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error with getUniqueAccMetaDatasetByGids(" + gids + ") query from AccMetadataSet: " + e.getMessage(), e);
		}
	}

}
