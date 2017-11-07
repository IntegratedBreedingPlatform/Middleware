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

	public static final String GET_DATASETS_BY_IDS = DatasetDAO.GET_DATASETS_SELECT + "FROM gdms_dataset d "
			+ "WHERE dataset_id in (:datasetIds) ";

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
		List<Dataset> dataValues = new ArrayList<Dataset>();
		try {
			if (datasetIds != null && datasetIds.get(0) != null) {
				SQLQuery query = this.getSession().createSQLQuery(DatasetDAO.GET_DATASETS_BY_IDS);
				query.setParameterList("datasetIds", datasetIds);
				List results = query.list();
				dataValues = this.buildDatasetList(results);
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getDatasetsByIds() query from Dataset: " + e.getMessage(), e);
		}
		return dataValues;
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

	public List<Dataset> getDatasetsByMappingType(GdmsType type) throws MiddlewareQueryException {
		List<Dataset> dataValues = new ArrayList<Dataset>();
		try {
			// MappingABH
			StringBuffer sqlString =
					new StringBuffer().append(DatasetDAO.GET_DATASETS_SELECT)
							.append("FROM gdms_mapping_pop_values m JOIN gdms_dataset d ON m.dataset_id = d.dataset_id ")
							.append("WHERE d.dataset_type = '").append(GdmsType.TYPE_MAPPING.getValue()).append("' ")
							.append("AND m.dataset_id NOT IN (SELECT a.dataset_id FROM gdms_allele_values a ")
							.append("						  JOIN gdms_dataset d ON a.dataset_id = d.dataset_id WHERE d.dataset_type = '")
							.append(GdmsType.TYPE_MAPPING.getValue()).append("') ")
							.append("AND m.dataset_id NOT IN (SELECT c.dataset_id FROM gdms_char_values c ")
							.append("							JOIN gdms_dataset d ON c.dataset_id = d.dataset_id WHERE d.dataset_type = '")
							.append(GdmsType.TYPE_MAPPING.getValue()).append("') ");

			if (type == GdmsType.TYPE_SNP) { // MappingSSR (with char values)
				sqlString =
						new StringBuffer().append(DatasetDAO.GET_DATASETS_SELECT)
								.append("FROM gdms_char_values c JOIN gdms_dataset d ON c.dataset_id = d.dataset_id ")
								.append("WHERE d.dataset_type =  '").append(GdmsType.TYPE_MAPPING.getValue()).append("' ");

			} else if (type == GdmsType.TYPE_SSR) { // MappingAllelicSSRDArT (with allele values)
				sqlString =
						new StringBuffer().append(DatasetDAO.GET_DATASETS_SELECT)
								.append("FROM gdms_allele_values a JOIN gdms_dataset d ON a.dataset_id = d.dataset_id ")
								.append("WHERE d.dataset_type =  '").append(GdmsType.TYPE_MAPPING.getValue()).append("' ");
			}
			SQLQuery query = this.getSession().createSQLQuery(sqlString.toString());

			@SuppressWarnings("rawtypes")
			List results = query.list();
			dataValues = this.buildDatasetList(results);

		} catch (HibernateException e) {
			this.logAndThrowException("Error with getDatasetsByMappingType(type=" + type + ") query from Dataset " + e.getMessage(), e);
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

	@SuppressWarnings("rawtypes")
	private List<Dataset> buildDatasetList(List results) {
		List<Dataset> dataValues = new ArrayList<Dataset>();

		for (Object o : results) {
			Object[] result = (Object[]) o;
			if (result != null) {
				Integer datasetId = (Integer) result[0];
				String datasetName = (String) result[1];
				String datasetDesc = (String) result[2];
				String datasetType = (String) result[3];
				String genus = (String) result[4];
				String species = (String) result[5];
				Date uploadTemplateDate = (Date) result[6];
				String remarks = (String) result[7];
				String dataType = (String) result[8];
				String missingData = (String) result[9];
				String method = (String) result[10];
				String score = (String) result[11];
				String institute = (String) result[12];
				String principalInvestigator = (String) result[13];
				String email = (String) result[14];
				String purposeOfStudy = (String) result[15];

				Dataset dataset =
						new Dataset(datasetId, datasetName, datasetDesc, datasetType, genus, species, uploadTemplateDate, remarks,
								dataType, missingData, method, score, institute, principalInvestigator, email, purposeOfStudy, null, null, null, null);
				dataValues.add(dataset);
			}
		}
		return dataValues;
	}

}
