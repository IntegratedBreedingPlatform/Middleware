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
import org.generationcp.middleware.pojos.gdms.AllelicValueWithMarkerIdElement;
import org.generationcp.middleware.pojos.gdms.MappingPopValues;
import org.generationcp.middleware.pojos.gdms.MarkerSampleId;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO class for {@link MappingPopValues}.
 *
 * @author Joyce Avestro
 *
 */
public class MappingPopValuesDAO extends GenericDAO<MappingPopValues, Integer> {

	// For getMarkerNamesByGIds()
	public static final String GET_MAPPING_COUNT_BY_GID = "SELECT COUNT(*) " + "FROM gdms_mapping_pop_values " + "WHERE gid IN (:gIdList)";

	// For getGermplasmNamesByMarkerNames()
	public static final String GET_MAPPING_COUNT_BY_MARKER_ID = "SELECT count(*) " + "FROM gdms_mapping_pop_values "
			+ "WHERE marker_id IN (:markerIdList)";

	// For getGermplasmNamesByMarkerNames()
	public static final String GET_MAPPING_GERMPLASM_NAME_AND_MARKER_NAME_BY_MARKER_NAMES = "SELECT n.nval, CONCAT(m.marker_name, '') "
			+ "FROM names n JOIN gdms_mapping_pop_values mp ON n.gid = mp.gid "
			+ "           JOIN gdms_marker m ON mp.marker_id = m.marker_id " + "WHERE marker_name IN (:markerNameList) AND n.nstat = 1 "
			+ "ORDER BY n.nval, m.marker_name";

	// For getAllelicValues by gid and marker names
	public static final String GET_ALLELIC_VALUES_BY_GIDS_AND_MARKER_NAMES = "SELECT DISTINCT " + "gdms_mapping_pop_values.gid, "
			+ "CONCAT(gdms_mapping_pop_values.map_char_value, ''), "
			+ "CONCAT(gdms_marker.marker_name, ''), "
			+ "CAST(NULL AS UNSIGNED INTEGER) "
			+ // peak height
			"FROM gdms_mapping_pop_values, " + "gdms_marker " + "WHERE gdms_mapping_pop_values.marker_id = gdms_marker.marker_id "
			+ "AND gdms_mapping_pop_values.gid IN (:gidList) " + "AND gdms_mapping_pop_values.marker_id IN (:markerIdList) "
			+ "ORDER BY gdms_mapping_pop_values.gid DESC, gdms_marker.marker_name";

	public static final String GET_ALLELIC_VALUES_BY_GIDS_AND_MARKER_IDS = "SELECT DISTINCT " + "gmpv.gid, " + "gmpv.marker_id, "
			+ "CONCAT(gmpv.map_char_value, ''), " + "CAST(NULL AS UNSIGNED INTEGER), "
			+ // peak height
			"gmpv.marker_sample_id, " + "gmpv.acc_sample_id " + "FROM gdms_mapping_pop_values gmpv " + "WHERE gmpv.gid IN (:gidList) "
			+ "AND gmpv.marker_id IN (:markerIdList) " + "ORDER BY gmpv.gid DESC ";

	public static final String GET_ALLELIC_VALUES_BY_GID_LOCAL = "SELECT DISTINCT " + "gdms_mapping_pop_values.gid, "
			+ "gdms_mapping_pop_values.marker_id, " + "CONCAT(gdms_mapping_pop_values.map_char_value, ''), "
			+ "CAST(NULL AS UNSIGNED INTEGER) "
			+ // peak height
			"FROM gdms_mapping_pop_values " + "WHERE gdms_mapping_pop_values.gid IN (:gidList) "
			+ "ORDER BY gdms_mapping_pop_values.gid ASC ";

	// For getAllelicValues by datasetId
	public static final String GET_ALLELIC_VALUES_BY_DATASET_ID = "SELECT gid, marker_id, CONCAT(map_char_value, '') "
			+ "           , marker_sample_id, acc_sample_id " + "FROM gdms_mapping_pop_values " + "WHERE dataset_id = :datasetId "
			+ "ORDER BY gid ASC, marker_id ASC";

	public static final String COUNT_BY_DATASET_ID = "SELECT COUNT(*) " + "FROM gdms_mapping_pop_values " + "WHERE dataset_id = :datasetId";

	public static final String GET_GIDS_BY_MARKER_ID = "SELECT DISTINCT gid " + "FROM gdms_mapping_pop_values "
			+ "WHERE marker_id = :markerId";

	public static final String COUNT_GIDS_BY_MARKER_ID = "SELECT COUNT(distinct gid) " + "FROM gdms_mapping_pop_values "
			+ "WHERE marker_id = :markerId";

	public static final String COUNT_BY_GIDS = "SELECT COUNT(distinct mp_id) " + "FROM gdms_mapping_pop_values "
			+ "WHERE gid in (:gIdList)";

	public static final String GET_MARKER_SAMPLE_IDS_BY_GIDS = "SELECT DISTINCT marker_id, marker_sample_id "
			+ "FROM gdms_mapping_pop_values " + "WHERE gid IN (:gids)";
	
	// another transferred query from the Vaadin layer
	public static final String GET_UNIQUE_MAPPOP_ALLELIC_VALUES_BY_GIDS_AND_MIDS = "select distinct gid,marker_id,map_char_value,acc_sample_id,marker_sample_id from gdms_mapping_pop_values where"
			+ " gid in(:gids) and marker_id in (:mids) ORDER BY gid, marker_id,acc_sample_id asc";

	private static final Logger LOG = LoggerFactory.getLogger(MappingPopValuesDAO.class);

	public MappingPopValuesDAO(final Session session) {
		super(session);
	}

	/**
	 * Gets the allelic values based on the given dataset id. The result is limited by the start and numOfRows parameters.
	 * 
	 * @param datasetId the dataset id
	 * @param start the start of the rows to retrieve
	 * @param numOfRows the number of rows to retrieve
	 * @return the Allelic Values (germplasm id, data value, and marker id) for the given dataset id
	 * @throws MiddlewareQueryException the MiddlewareQueryException
	 */
	@SuppressWarnings("rawtypes")
	public List<AllelicValueWithMarkerIdElement> getAllelicValuesByDatasetId(Integer datasetId, int start, int numOfRows)
			throws MiddlewareQueryException {
		List<AllelicValueWithMarkerIdElement> toReturn = new ArrayList<AllelicValueWithMarkerIdElement>();

		try {
			if (datasetId != null) {
				SQLQuery query = this.getSession().createSQLQuery(MappingPopValuesDAO.GET_ALLELIC_VALUES_BY_DATASET_ID);
				query.setParameter("datasetId", datasetId);
				query.setFirstResult(start);
				query.setMaxResults(numOfRows);
				List results = query.list();

				for (Object o : results) {
					Object[] result = (Object[]) o;
					if (result != null) {
						Integer gid = (Integer) result[0];
						Integer markerId = (Integer) result[1];
						String data = (String) result[2];
						Integer markerSampleId = (Integer) result[3];
						Integer accSampleId = (Integer) result[4];
						AllelicValueWithMarkerIdElement allelicValueElement =
								new AllelicValueWithMarkerIdElement(gid, data, markerId, markerSampleId, accSampleId);
						toReturn.add(allelicValueElement);
					}
				}
				return toReturn;
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getAllelicValuesByDatasetId(datasetId=" + datasetId + ") query from MappingPopValues: "
					+ e.getMessage(), e);
		}
		return toReturn;

	}

	/**
	 * Count by dataset id.
	 *
	 * @param datasetId the dataset id
	 * @return the number of entries in mapping_pop_values table corresponding to the given datasetId
	 * @throws MiddlewareQueryException the MiddlewareQueryException
	 */
	public long countByDatasetId(Integer datasetId) throws MiddlewareQueryException {
		try {
			if (datasetId != null) {
				Query query = this.getSession().createSQLQuery(MappingPopValuesDAO.COUNT_BY_DATASET_ID);
				query.setParameter("datasetId", datasetId);
				BigInteger result = (BigInteger) query.uniqueResult();
				if (result != null) {
					return result.longValue();
				}
			}
		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error with countByDatasetId(datasetId=" + datasetId + ") query from MappingPopValues: " + e.getMessage(), e);
		}
		return 0;
	}

	/**
	 * Gets the gids by marker id.
	 *
	 * @param markerId the marker id
	 * @param start the start
	 * @param numOfRows the num of rows
	 * @return the gI ds by marker id
	 * @throws MiddlewareQueryException the MiddlewareQueryException
	 */
	@SuppressWarnings("unchecked")
	public List<Integer> getGIDsByMarkerId(Integer markerId, int start, int numOfRows) throws MiddlewareQueryException {

		try {
			if (markerId != null) {
				SQLQuery query = this.getSession().createSQLQuery(MappingPopValuesDAO.GET_GIDS_BY_MARKER_ID);
				query.setParameter("markerId", markerId);
				query.setFirstResult(start);
				query.setMaxResults(numOfRows);
				return query.list();
			}
		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error with getGIDsByMarkerId(markerId=" + markerId + ") query from MappingPopValues: " + e.getMessage(), e);
		}
		return new ArrayList<Integer>();
	}

	/**
	 * Count gids by marker id.
	 *
	 * @param markerId the marker id
	 * @return the long
	 * @throws MiddlewareQueryException the MiddlewareQueryException
	 */
	public long countGIDsByMarkerId(Integer markerId) throws MiddlewareQueryException {
		try {
			if (markerId != null) {
				SQLQuery query = this.getSession().createSQLQuery(MappingPopValuesDAO.COUNT_GIDS_BY_MARKER_ID);
				query.setParameter("markerId", markerId);
				BigInteger result = (BigInteger) query.uniqueResult();
				if (result != null) {
					return result.longValue();
				}
			}
		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error with countGIDsByMarkerId(markerId=" + markerId + ") query from MappingPopValues: " + e.getMessage(), e);
		}
		return 0;
	}

	public long countByGids(List<Integer> gIds) throws MiddlewareQueryException {
		try {
			if (gIds != null && gIds.get(0) != null) {
				SQLQuery query = this.getSession().createSQLQuery(MappingPopValuesDAO.COUNT_BY_GIDS);
				query.setParameterList("gIdList", gIds);
				BigInteger result = (BigInteger) query.uniqueResult();
				if (result != null) {
					return result.longValue();
				}
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with countByGids(gIds=" + gIds + ") query from MappingPopValues: " + e.getMessage(), e);
		}
		return 0;
	}

	public void deleteByDatasetId(int datasetId) throws MiddlewareQueryException {
		try {
			// Please note we are manually flushing because non hibernate based deletes and updates causes the Hibernate session to get out of synch with
			// underlying database. Thus flushing to force Hibernate to synchronize with the underlying database before the delete
			// statement
			this.getSession().flush();
			
			SQLQuery statement = this.getSession().createSQLQuery("DELETE FROM gdms_mapping_pop_values WHERE dataset_id = " + datasetId);
			statement.executeUpdate();
		} catch (HibernateException e) {
			this.logAndThrowException("Error in deleteByDatasetId=" + datasetId + " in MappingPopValuesDAO: " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("rawtypes")
	public List<MarkerSampleId> getMarkerSampleIdsByGids(List<Integer> gIds) throws MiddlewareQueryException {
		List<MarkerSampleId> toReturn = new ArrayList<MarkerSampleId>();
		try {
			if (gIds != null && !gIds.isEmpty()) {
				SQLQuery query = this.getSession().createSQLQuery(MappingPopValuesDAO.GET_MARKER_SAMPLE_IDS_BY_GIDS);
				query.setParameterList("gids", gIds);

				List results = query.list();
				for (Object o : results) {
					Object[] result = (Object[]) o;
					if (result != null) {
						Integer markerId = (Integer) result[0];
						Integer markerSampleId = (Integer) result[1];
						MarkerSampleId dataElement = new MarkerSampleId(markerId, markerSampleId);
						toReturn.add(dataElement);
					}
				}
			}
		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error with getMarkerSampleIdsByGids(gIds=" + gIds + ") query from MappingPopValuesDAO: " + e.getMessage(), e);
		}
		return toReturn;
	}

	@SuppressWarnings({"deprecation", "unchecked"})
	public List<Object> getUniqueMapPopAllelesByGidsAndMids(List<Integer> gids, List<Integer> mids) {
		
		List<Object> results = new ArrayList<>();
		try {
			if (gids != null) {
				SQLQuery query =
						this.getSession().createSQLQuery(MappingPopValuesDAO.GET_UNIQUE_MAPPOP_ALLELIC_VALUES_BY_GIDS_AND_MIDS);
				query.setParameterList("gids", gids);				
				query.setParameterList("mids", mids);				
				query.addScalar("gid", IntegerType.INSTANCE);
				query.addScalar("marker_id", IntegerType.INSTANCE);
				query.addScalar("map_char_value", StringType.INSTANCE);
				query.addScalar("acc_sample_id", IntegerType.INSTANCE);
				query.addScalar("marker_sample_id", IntegerType.INSTANCE);
				results = query.list();

					}
		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error with getUniqueMapPopAllelesByGidsAndMids(gids=" + gids + ") query from MappingPopValuesDAO " + e.getMessage(), e);
		}
		return results;
				
	}

}
