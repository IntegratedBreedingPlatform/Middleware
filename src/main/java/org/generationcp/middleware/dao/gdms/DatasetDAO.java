/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 * <p/>
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.middleware.dao.gdms;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.gdms.Dataset;
import org.generationcp.middleware.pojos.gdms.DatasetElement;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DAO class for {@link Dataset}.
 *
 * @author Joyce Avestro
 *
 */
@SuppressWarnings("unchecked")
public class DatasetDAO extends GenericDAO<Dataset, Integer> {

	private static final String COUNT_BY_NAME = "SELECT COUNT(dataset_name) " + "FROM gdms_dataset " + "WHERE dataset_type != 'QTL' ";

	private static final String GET_DATASET_NAMES_NOT_QTL_AND_MTA =
			"SELECT CONCAT(dataset_name, '') " + "FROM gdms_dataset " + "WHERE dataset_type != 'QTL' AND dataset_type != 'MTA' ";

	private static final String GET_DATASET_ID_NOT_MAPPING_AND_NOT_QTL =
			"SELECT dataset_id " + "FROM gdms_dataset " + "WHERE dataset_type != 'mapping' " + "AND dataset_type != 'QTL' ";

	private static final String COUNT_DATASET_ID_NOT_MAPPING_AND_NOT_QTL =
			"SELECT COUNT(dataset_id) " + "FROM gdms_dataset " + "WHERE dataset_type != 'mapping' " + "AND dataset_type != 'QTL' ";

	private static final String GET_DATASET_ID_BY_MAPPING_AND_NOT_QTL =
			"SELECT dataset_id " + "FROM gdms_dataset " + "WHERE dataset_type = 'mapping' " + "AND dataset_type != 'QTL' ";

	private static final String COUNT_DATASET_ID_BY_MAPPING_AND_NOT_QTL =
			"SELECT COUNT(dataset_id) " + "FROM gdms_dataset " + "WHERE dataset_type = 'mapping' " + "AND dataset_type != 'QTL' ";

	private static final String GET_DETAILS_BY_NAME =
			"SELECT dataset_id, CONCAT(dataset_type, '') " + "FROM gdms_dataset " + "WHERE dataset_name = :datasetName";

	private static final String GET_DATASET_NAMES_BY_QTL_ID =
			"SELECT DISTINCT CONCAT(dataset_name,'') " + "FROM gdms_dataset gd " + "INNER JOIN "
					+ "gdms_qtl gq ON gd.dataset_id = gq.dataset_id " + "WHERE gq.qtl_id = :qtlId ";

	private static final String COUNT_DATASET_NAMES_BY_QTL_ID =
			"SELECT COUNT(DISTINCT CONCAT(dataset_name,'')) " + "FROM gdms_dataset gd " + "INNER JOIN "
					+ "gdms_qtl gq ON gd.dataset_id = gq.dataset_id " + "WHERE gq.qtl_id = :qtlId ";

	private static final Logger LOG = LoggerFactory.getLogger(DatasetDAO.class);

	public long countByName() throws MiddlewareQueryException {
		Query query = this.getSession().createSQLQuery(DatasetDAO.COUNT_BY_NAME);
		BigInteger result = (BigInteger) query.uniqueResult();
		if (result != null) {
			return result.longValue();
		}
		return 0;
	}

	public List<String> getDatasetNames(final int start, final int numOfRows) throws MiddlewareQueryException {
		try {
			SQLQuery query = this.getSession().createSQLQuery(DatasetDAO.GET_DATASET_NAMES_NOT_QTL_AND_MTA);
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);
			return query.list();
		} catch (HibernateException e) {
			final String errorMessage = "Error with getDatasetNames() query from Dataset: " + e.getMessage();
			DatasetDAO.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	@SuppressWarnings("rawtypes")
	public List<DatasetElement> getDetailsByName(final String name) throws MiddlewareQueryException {
		List<DatasetElement> dataValues = new ArrayList<>();
		try {
			if (name != null) {
				SQLQuery query = this.getSession().createSQLQuery(DatasetDAO.GET_DETAILS_BY_NAME);
				query.setParameter("datasetName", name);
				List results = query.list();

				for (final Object o : results) {
					Object[] result = (Object[]) o;
					if (result != null) {
						Integer datasetId = (Integer) result[0];
						String datasetType = (String) result[1];
						DatasetElement datasetElement = new DatasetElement(datasetId, datasetType);
						dataValues.add(datasetElement);
					}
				}
			}
			return dataValues;
		} catch (HibernateException e) {
			final String errorMessage = "Error with getDetailsByName(datasetName=" + name + ") query from Dataset: " + e.getMessage();
			DatasetDAO.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	public List<Integer> getDatasetIdsForFingerPrinting(final int start, final int numOfRows) throws MiddlewareQueryException {
		try {
			SQLQuery query = this.getSession().createSQLQuery(DatasetDAO.GET_DATASET_ID_NOT_MAPPING_AND_NOT_QTL);
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);
			return query.list();
		} catch (HibernateException e) {
			final String errorMessage = "Error with getDatasetIdsForFingerPrinting() query from Dataset: " + e.getMessage();
			DatasetDAO.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	public long countDatasetIdsForFingerPrinting() throws MiddlewareQueryException {
		Query query = this.getSession().createSQLQuery(DatasetDAO.COUNT_DATASET_ID_NOT_MAPPING_AND_NOT_QTL);
		BigInteger result = (BigInteger) query.uniqueResult();
		if (result != null) {
			return result.longValue();
		}
		return 0;
	}

	public List<Integer> getDatasetIdsForMapping(final int start, final int numOfRows) throws MiddlewareQueryException {
		try {
			SQLQuery query = this.getSession().createSQLQuery(DatasetDAO.GET_DATASET_ID_BY_MAPPING_AND_NOT_QTL);
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);
			return query.list();
		} catch (HibernateException e) {
			final String errorMessage = "Error with getDatasetIdsForMapping() query from Dataset: " + e.getMessage();
			DatasetDAO.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);

		}
	}

	public long countDatasetIdsForMapping() throws MiddlewareQueryException {
		Query query = this.getSession().createSQLQuery(DatasetDAO.COUNT_DATASET_ID_BY_MAPPING_AND_NOT_QTL);
		BigInteger result = (BigInteger) query.uniqueResult();
		if (result != null) {
			return result.longValue();
		}
		return 0;
	}

	public List<String> getDatasetNamesByQtlId(final Integer qtlId, final int start, final int numOfRows) throws MiddlewareQueryException {
		try {
			if (qtlId != null) {
				SQLQuery query = this.getSession().createSQLQuery(DatasetDAO.GET_DATASET_NAMES_BY_QTL_ID);
				query.setParameter("qtlId", qtlId);
				query.setFirstResult(start);
				query.setMaxResults(numOfRows);
				return query.list();
			}
			return new ArrayList<>();
		} catch (HibernateException e) {
			final String errorMessage = "Error with getDatasetNamesByQtlId() query from Dataset: " + e.getMessage();
			DatasetDAO.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);

		}
	}

	public long countDatasetNamesByQtlId(final Integer qtlId) throws MiddlewareQueryException {
		try {
			if (qtlId != null) {
				Query query = this.getSession().createSQLQuery(DatasetDAO.COUNT_DATASET_NAMES_BY_QTL_ID);
				query.setParameter("qtlId", qtlId);
				BigInteger result = (BigInteger) query.uniqueResult();
				if (result != null) {
					return result.longValue();
				}
			}
			return 0;
		} catch (HibernateException e) {
			final String errorMessage = "Error with countDatasetNamesByQtlId() query from Dataset: " + e.getMessage();
			DatasetDAO.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	public void deleteByDatasetId(final Integer datasetId) throws MiddlewareQueryException {
		try {
			// Please note we are manually flushing because non hibernate based deletes and updates causes the Hibernate session to get out of synch with
			// underlying database. Thus flushing to force Hibernate to synchronize with the underlying database before the delete
			// statement
			this.getSession().flush();

			SQLQuery statement = this.getSession().createSQLQuery("DELETE FROM gdms_dataset WHERE dataset_id = " + datasetId);
			statement.executeUpdate();
		} catch (HibernateException e) {
			final String errorMessage = "Error in deleteByDatasetId=" + datasetId + " in DatasetDAO: " + e.getMessage();
			DatasetDAO.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	@SuppressWarnings("rawtypes")
	public List<Dataset> getDatasetsByIds(final List<Integer> datasetIds) throws MiddlewareQueryException {
		try {
			if (datasetIds != null) {
				return this.getSession().createCriteria(Dataset.class, "dataset").add(Restrictions.in("datasetId", datasetIds)).list();
			}
			return new ArrayList<>();
		} catch (HibernateException e) {
			final String errorMessage = "Error with getDatasetsByIds() query from Dataset: " + e.getMessage();
			DatasetDAO.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	public List<Dataset> getDatasetsByType(final String type) throws MiddlewareQueryException {
		try {
			if (type != null) {
				Criteria crit = this.getSession().createCriteria(Dataset.class);
				crit.add(Restrictions.eq("datasetType", type));
				return crit.list();
			}
			return new ArrayList<>();
		} catch (HibernateException e) {
			final String errorMessage = "Error with getDatasetsByType(type=" + type + ") query from Dataset " + e.getMessage();
			DatasetDAO.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	public Dataset getByName(final String datasetName) throws MiddlewareQueryException {
		try {
			if (datasetName != null) {
				Criteria crit = this.getSession().createCriteria(Dataset.class);
				crit.add(Restrictions.eq("datasetName", datasetName));
				List<Object> result = crit.list();
				if (!result.isEmpty()) {
					return (Dataset) result.get(0);
				}
			}
			return null;
		} catch (HibernateException e) {
			final String errorMessage = "Error with getByName(datasetName=" + datasetName + ") query from Dataset " + e.getMessage();
			DatasetDAO.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

}
