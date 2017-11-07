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
import java.util.Date;
import java.util.List;

import com.google.common.base.Preconditions;
import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.GdmsType;
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

	public static final String COUNT_BY_NAME = "SELECT COUNT(dataset_name) " + "FROM gdms_dataset " + "WHERE dataset_type != 'QTL' ";

	public static final String GET_DATASET_NAMES_NOT_QTL_AND_MTA = "SELECT CONCAT(dataset_name, '') " + "FROM gdms_dataset "
			+ "WHERE dataset_type != 'QTL' AND dataset_type != 'MTA' ";

	public static final String GET_DATASET_ID_NOT_MAPPING_AND_NOT_QTL = "SELECT dataset_id " + "FROM gdms_dataset "
			+ "WHERE dataset_type != 'mapping' " + "AND dataset_type != 'QTL' ";

	public static final String COUNT_DATASET_ID_NOT_MAPPING_AND_NOT_QTL = "SELECT COUNT(dataset_id) " + "FROM gdms_dataset "
			+ "WHERE dataset_type != 'mapping' " + "AND dataset_type != 'QTL' ";

	public static final String GET_DATASET_ID_BY_MAPPING_AND_NOT_QTL = "SELECT dataset_id " + "FROM gdms_dataset "
			+ "WHERE dataset_type = 'mapping' " + "AND dataset_type != 'QTL' ";

	public static final String COUNT_DATASET_ID_BY_MAPPING_AND_NOT_QTL = "SELECT COUNT(dataset_id) " + "FROM gdms_dataset "
			+ "WHERE dataset_type = 'mapping' " + "AND dataset_type != 'QTL' ";

	public static final String GET_DETAILS_BY_NAME = "SELECT dataset_id, CONCAT(dataset_type, '') " + "FROM gdms_dataset "
			+ "WHERE dataset_name = :datasetName";

	public static final String GET_DATASET_NAMES_BY_QTL_ID = "SELECT DISTINCT CONCAT(dataset_name,'') " + "FROM gdms_dataset gd "
			+ "INNER JOIN " + "gdms_qtl gq ON gd.dataset_id = gq.dataset_id " + "WHERE gq.qtl_id = :qtlId ";

	public static final String COUNT_DATASET_NAMES_BY_QTL_ID = "SELECT COUNT(DISTINCT CONCAT(dataset_name,'')) " + "FROM gdms_dataset gd "
			+ "INNER JOIN " + "gdms_qtl gq ON gd.dataset_id = gq.dataset_id " + "WHERE gq.qtl_id = :qtlId ";

	public static final String GET_DATASETS_SELECT = "SELECT d.dataset_id " + ", CONCAT(dataset_name, '')  " + ", dataset_desc  "
			+ ", CONCAT(dataset_type, '')  " + ", CONCAT(genus, '')  " + ", CONCAT(species, '')  " + ", upload_template_date  "
			+ ", remarks  " + ", CONCAT(datatype, '')  " + ", missing_data  " + ", method  " + ", score  " + ", institute  "
			+ ", principal_investigator  " + ", email  " + ", purpose_of_study  ";

	private static final Logger LOG = LoggerFactory.getLogger(DatasetDAO.class);


	public long countByName() throws MiddlewareQueryException {
		Query query = this.getSession().createSQLQuery(DatasetDAO.COUNT_BY_NAME);
		BigInteger result = (BigInteger) query.uniqueResult();
		if (result != null) {
			return result.longValue();
		}
		return 0;
	}

	public List<String> getDatasetNames(int start, int numOfRows) throws MiddlewareQueryException {
		try {
			SQLQuery query = this.getSession().createSQLQuery(DatasetDAO.GET_DATASET_NAMES_NOT_QTL_AND_MTA);
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);
			return query.list();
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getDatasetNames() query from Dataset: " + e.getMessage(), e);
		}
		return new ArrayList<>();
	}

	@SuppressWarnings("rawtypes")
	public List<DatasetElement> getDetailsByName(String name) throws MiddlewareQueryException {
		List<DatasetElement> dataValues = new ArrayList<DatasetElement>();

		try {
			if (name != null) {
				SQLQuery query = this.getSession().createSQLQuery(DatasetDAO.GET_DETAILS_BY_NAME);
				query.setParameter("datasetName", name);
				List results = query.list();

				for (Object o : results) {
					Object[] result = (Object[]) o;
					if (result != null) {
						Integer datasetId = (Integer) result[0];
						String datasetType = (String) result[1];
						DatasetElement datasetElement = new DatasetElement(datasetId, datasetType);
						dataValues.add(datasetElement);
					}
				}
				return dataValues;
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getDetailsByName(datasetName=" + name + ") query from Dataset: " + e.getMessage(), e);
		}
		return dataValues;
	}

	public List<Integer> getDatasetIdsForFingerPrinting(int start, int numOfRows) throws MiddlewareQueryException {
		try {
			SQLQuery query = this.getSession().createSQLQuery(DatasetDAO.GET_DATASET_ID_NOT_MAPPING_AND_NOT_QTL);
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);
			return query.list();
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getDatasetIdsForFingerPrinting() query from Dataset: " + e.getMessage(), e);
		}
		return new ArrayList<Integer>();
	}

	public long countDatasetIdsForFingerPrinting() throws MiddlewareQueryException {
		Query query = this.getSession().createSQLQuery(DatasetDAO.COUNT_DATASET_ID_NOT_MAPPING_AND_NOT_QTL);
		BigInteger result = (BigInteger) query.uniqueResult();
		if (result != null) {
			return result.longValue();
		}
		return 0;
	}

	public List<Integer> getDatasetIdsForMapping(int start, int numOfRows) throws MiddlewareQueryException {
		try {
			SQLQuery query = this.getSession().createSQLQuery(DatasetDAO.GET_DATASET_ID_BY_MAPPING_AND_NOT_QTL);
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);
			return query.list();
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getDatasetIdsForMapping() query from Dataset: " + e.getMessage(), e);
		}
		return new ArrayList<Integer>();
	}

	public long countDatasetIdsForMapping() throws MiddlewareQueryException {
		Query query = this.getSession().createSQLQuery(DatasetDAO.COUNT_DATASET_ID_BY_MAPPING_AND_NOT_QTL);
		BigInteger result = (BigInteger) query.uniqueResult();
		if (result != null) {
			return result.longValue();
		}
		return 0;
	}

	public List<String> getDatasetNamesByQtlId(Integer qtlId, int start, int numOfRows) throws MiddlewareQueryException {
		try {
			if (qtlId != null) {
				SQLQuery query = this.getSession().createSQLQuery(DatasetDAO.GET_DATASET_NAMES_BY_QTL_ID);
				query.setParameter("qtlId", qtlId);
				query.setFirstResult(start);
				query.setMaxResults(numOfRows);
				return query.list();
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getDatasetNamesByQtlId() query from Dataset: " + e.getMessage(), e);
		}
		return new ArrayList<String>();
	}

	public long countDatasetNamesByQtlId(Integer qtlId) throws MiddlewareQueryException {
		try {
			if (qtlId != null) {
				Query query = this.getSession().createSQLQuery(DatasetDAO.COUNT_DATASET_NAMES_BY_QTL_ID);
				query.setParameter("qtlId", qtlId);
				BigInteger result = (BigInteger) query.uniqueResult();
				if (result != null) {
					return result.longValue();
				}
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with countDatasetNamesByQtlId() query from Dataset: " + e.getMessage(), e);
		}
		return 0;
	}

	public void deleteByDatasetId(Integer datasetId) throws MiddlewareQueryException {
		try {
			// Please note we are manually flushing because non hibernate based deletes and updates causes the Hibernate session to get out of synch with
			// underlying database. Thus flushing to force Hibernate to synchronize with the underlying database before the delete
			// statement
			this.getSession().flush();
			
			SQLQuery statement = this.getSession().createSQLQuery("DELETE FROM gdms_dataset WHERE dataset_id = " + datasetId);
			statement.executeUpdate();
		} catch (HibernateException e) {
			this.logAndThrowException("Error in deleteByDatasetId=" + datasetId + " in DatasetDAO: " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("rawtypes")
	public List<Dataset> getDatasetsByIds(List<Integer> datasetIds) throws MiddlewareQueryException {
		Preconditions.checkNotNull(datasetIds);
		try {
			return this.getSession()
					.createCriteria(Dataset.class, "dataset")
					.add(Restrictions.in("datasetId", datasetIds))
					.list();
		} catch (HibernateException e) {
			final String errorMessage = "Error with getDatasetsByIds() query from Dataset: " + e.getMessage();
			DatasetDAO.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	public List<Dataset> getDatasetsByType(String type) throws MiddlewareQueryException {
		List<Dataset> dataValues = new ArrayList<Dataset>();
		try {
			if (type != null) {
				Criteria crit = this.getSession().createCriteria(Dataset.class);
				crit.add(Restrictions.eq("datasetType", type));
				dataValues = crit.list();

			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getDatasetsByType(type=" + type + ") query from Dataset " + e.getMessage(), e);
		}
		return dataValues;
	}

	public Dataset getByName(String datasetName) throws MiddlewareQueryException {
		try {
			if (datasetName != null) {
				Criteria crit = this.getSession().createCriteria(Dataset.class);
				crit.add(Restrictions.eq("datasetName", datasetName));
				List<Object> result = crit.list();
				if (!result.isEmpty()) {
					return (Dataset) result.get(0);
				}
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getByName(datasetName=" + datasetName + ") query from Dataset " + e.getMessage(), e);
		}
		return null;
	}

}
