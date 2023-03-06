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
import org.generationcp.middleware.pojos.gdms.AlleleValues;
import org.generationcp.middleware.pojos.gdms.AllelicValueElement;
import org.generationcp.middleware.pojos.gdms.AllelicValueWithMarkerIdElement;
import org.generationcp.middleware.pojos.gdms.MarkerSampleId;
import org.generationcp.middleware.util.StringUtil;
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
 * DAO class for {@link AlleleValues}.
 *
 * @author Joyce Avestro
 */
@SuppressWarnings("rawtypes")
public class AlleleValuesDAO extends GenericDAO<AlleleValues, Integer> {

	// For getMarkerNamesByGIds()
	/**
	 * The Constant GET_ALLELE_COUNT_BY_GID.
	 */
	public static final String GET_ALLELE_COUNT_BY_GID = "SELECT COUNT(*) " + "FROM gdms_allele_values " + "WHERE gid IN (:gIdList)";

	// For getGermplasmNamesByMarkerNames()
	/**
	 * The Constant GET_ALLELE_COUNT_BY_MARKER_ID.
	 */
	public static final String GET_ALLELE_COUNT_BY_MARKER_ID = "SELECT COUNT(*) " + "FROM gdms_allele_values "
			+ "WHERE marker_id IN (:markerIdList)";

	// For getGermplasmNamesByMarkerNames()
	public static final String GET_ALLELE_GERMPLASM_NAME_AND_MARKER_NAME_BY_MARKER_NAMES = "SELECT n.nval, CONCAT(m.marker_name, '') "
			+ "FROM names n JOIN gdms_allele_values a ON n.gid = a.gid " + "           JOIN gdms_marker m ON a.marker_id = m.marker_id "
			+ "WHERE marker_name IN (:markerNameList) AND n.nstat = 1 " + "ORDER BY n.nval, m.marker_name";

	public static final String GET_ALLELIC_VALUES_BY_GIDS_AND_MARKER_IDS = "SELECT DISTINCT " + "gav.gid, " + "gav.marker_id, "
			+ "CONCAT(gav.allele_bin_value, ''), " + "gav.peak_height, " + "gav.marker_sample_id, " + "gav.acc_sample_id "
			+ "FROM gdms_allele_values gav " + "WHERE  gav.gid IN (:gidList) " + "AND gav.marker_id IN (:markerIdList) "
			+ "ORDER BY gav.gid DESC";

	public static final String GET_ALLELIC_VALUES_BY_MARKER_IDS =
			"SELECT an_id, dataset_id, marker_id, gid, CONCAT(allele_bin_value, ''), peak_height, "
					+ "gav.marker_sample_id, gav.acc_sample_id " + "FROM gdms_allele_values gav "
					+ "WHERE  gav.marker_id IN (:markerIdList) " + "ORDER BY gav.gid DESC";

	public static final String GET_ALLELIC_VALUES_BY_GID_LOCAL = "SELECT DISTINCT " + "gdms_allele_values.gid, "
			+ "gdms_allele_values.marker_id, " + "CONCAT(gdms_allele_values.allele_bin_value, ''), " + "gdms_allele_values.peak_height "
			+ "FROM gdms_allele_values " + "WHERE  gdms_allele_values.gid IN (:gidList) " + "ORDER BY gdms_allele_values.gid ASC";

	// For getAllelicValues by datasetId
	public static final String GET_ALLELIC_VALUES_BY_DATASET_ID = "SELECT gid, marker_id, CONCAT(allele_bin_value, ''), peak_height "
			+ "  , marker_sample_id, acc_sample_id " + "FROM gdms_allele_values " + "WHERE dataset_id = :datasetId "
			+ "ORDER BY gid ASC, marker_id ASC";

	public static final String COUNT_BY_DATASET_ID = "SELECT COUNT(*) " + "FROM gdms_allele_values " + "WHERE dataset_id = :datasetId";

	public static final String GET_GIDS_BY_MARKER_ID = "SELECT DISTINCT gid " + "FROM gdms_allele_values " + "WHERE marker_id = :markerId";

	public static final String COUNT_ALLELE_VALUES_BY_GIDS = "SELECT COUNT(*) " + "FROM gdms_allele_values " + "WHERE gid in (:gids)";

	public static final String GET_INT_ALLELE_VALUES_FOR_POLYMORPHIC_MARKERS_RETRIEVAL_BY_GIDS =
			"SELECT gdms_allele_values.dataset_id, gdms_allele_values.gid, gdms_allele_values.marker_id  "
					+ ", CONCAT(gdms_marker.marker_name, ''), CONCAT(gdms_allele_values.allele_bin_value, '') "
					+ ", gdms_allele_values.peak_height, gdms_allele_values.marker_sample_id, gdms_allele_values.acc_sample_id "
					+ "FROM gdms_allele_values LEFT JOIN gdms_marker ON gdms_marker.marker_id = gdms_allele_values.marker_id "
					+ "WHERE gdms_allele_values.gid IN (:gids) " + "ORDER BY gid, marker_name ";

	public static final String COUNT_INT_ALLELE_VALUES_FOR_POLYMORPHIC_MARKERS_RETRIEVAL_BY_GIDS = "SELECT COUNT(*) "
			+ "FROM gdms_allele_values LEFT JOIN gdms_marker ON gdms_marker.marker_id = gdms_allele_values.marker_id "
			+ "WHERE gdms_allele_values.gid IN (:gids) ";

	public static final String GET_CHAR_ALLELE_VALUES_FOR_POLYMORPHIC_MARKERS_RETRIEVAL_BY_GIDS =
			"SELECT gdms_char_values.dataset_id, gdms_char_values.gid, gdms_char_values.marker_id "
					+ ", CONCAT(gdms_marker.marker_name,''), CONCAT(gdms_char_values.char_value,'') "
					+ ", gdms_char_values.marker_sample_id, gdms_char_values.acc_sample_id "
					+ "FROM gdms_char_values LEFT JOIN gdms_marker ON gdms_marker.marker_id = gdms_char_values.marker_id "
					+ "WHERE gdms_char_values.gid IN (:gids) " + "ORDER BY gid, marker_name ";

	public static final String COUNT_CHAR_ALLELE_VALUES_FOR_POLYMORPHIC_MARKERS_RETRIEVAL_BY_GIDS = "SELECT COUNT(*) "
			+ "FROM gdms_char_values LEFT JOIN gdms_marker ON gdms_marker.marker_id = gdms_char_values.marker_id "
			+ "WHERE gdms_char_values.gid IN (:gids) ";

	public static final String GET_MAPPING_ALLELE_VALUES_FOR_POLYMORPHIC_MARKERS_RETRIEVAL_BY_GIDS =
			"SELECT gdms_mapping_pop_values.dataset_id, gdms_mapping_pop_values.gid, gdms_mapping_pop_values.marker_id "
					+ ", CONCAT(gdms_marker.marker_name,''), CONCAT(gdms_mapping_pop_values.map_char_value,'') "
					+ ", gdms_mapping_pop_values.marker_sample_id, gdms_mapping_pop_values.acc_sample_id "
					+ "FROM gdms_mapping_pop_values LEFT JOIN gdms_marker ON gdms_marker.marker_id = gdms_mapping_pop_values.marker_id "
					+ "WHERE gdms_mapping_pop_values.gid IN (:gids) " + "ORDER BY gid, marker_name ";

	public static final String COUNT_MAPPING_ALLELE_VALUES_FOR_POLYMORPHIC_MARKERS_RETRIEVAL_BY_GIDS = "SELECT COUNT(*) "
			+ "FROM gdms_mapping_pop_values LEFT JOIN gdms_marker ON gdms_marker.marker_id = gdms_mapping_pop_values.marker_id "
			+ "WHERE gdms_mapping_pop_values.gid IN (:gids) ";

	public static final String GET_MARKER_SAMPLE_IDS_BY_GIDS = "SELECT DISTINCT marker_id, marker_sample_id " + "FROM gdms_allele_values "
			+ "WHERE gid IN (:gids)";

	public static final String COUNT_BY_GIDS = "SELECT COUNT(*) " + "FROM gdms_allele_values " + "WHERE gid in (:gIdList)";
	
	// another transferred query from the Vaadin layer
	public static final String GET_UNIQUE_ALLELIC_VALUES_BY_GIDS_AND_MIDS = "select distinct gid,marker_id, allele_bin_value,acc_sample_id,marker_sample_id from gdms_allele_values where"
			+ " gid in(:gids) and marker_id in (:mids) ORDER BY gid, marker_id,acc_sample_id asc";

	private static final Logger LOG = LoggerFactory.getLogger(AlleleValuesDAO.class);

	public AlleleValuesDAO(final Session session) {
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
	public List<AllelicValueWithMarkerIdElement> getAllelicValuesByDatasetId(Integer datasetId, int start, int numOfRows)
			throws MiddlewareQueryException {
		List<AllelicValueWithMarkerIdElement> toReturn = new ArrayList<AllelicValueWithMarkerIdElement>();

		try {
			if (datasetId != null) {
				SQLQuery query = this.getSession().createSQLQuery(AlleleValuesDAO.GET_ALLELIC_VALUES_BY_DATASET_ID);
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
						Integer peakHeight = (Integer) result[3];
						Integer markerSampleId = (Integer) result[4];
						Integer accSampleId = (Integer) result[5];
						AllelicValueWithMarkerIdElement allelicValueElement =
								new AllelicValueWithMarkerIdElement(gid, data, markerId, peakHeight, markerSampleId, accSampleId);
						toReturn.add(allelicValueElement);
					}
				}
			}

		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error with getAllelicValuesByDatasetId(datasetId=" + datasetId + ") query from AlleleValues: " + e.getMessage(), e);
		}
		return toReturn;
	}

	public List<AllelicValueElement> getAlleleValuesByMarkerId(List<Integer> markerIdList) throws MiddlewareQueryException {
		List<AllelicValueElement> returnVal = new ArrayList<AllelicValueElement>();

		if (markerIdList == null || markerIdList.isEmpty()) {
			return returnVal;
		}

		try {
			SQLQuery query = this.getSession().createSQLQuery(AlleleValuesDAO.GET_ALLELIC_VALUES_BY_MARKER_IDS);
			query.setParameterList("markerIdList", markerIdList);

			List results = query.list();

			for (Object o : results) {
				Object[] result = (Object[]) o;
				if (result != null) {
					Integer anId = (Integer) result[0];
					Integer datasetId = (Integer) result[1];
					Integer markerId = (Integer) result[2];
					Integer gId = (Integer) result[3];
					String alleleBinValue = (String) result[4];
					Integer peakHeight = (Integer) result[5];
					Integer markerSampleId = (Integer) result[6];
					Integer accSampleId = (Integer) result[7];
					AllelicValueElement value =
							new AllelicValueElement(anId, datasetId, gId, markerId, alleleBinValue, peakHeight, markerSampleId, accSampleId);
					returnVal.add(value);
				}
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getAlleleValuesByMarkerId() query from AlleleValues: " + e.getMessage(), e);
		}

		return returnVal;
	}

	/**
	 * Count by dataset id.
	 *
	 * @param datasetId the dataset id
	 * @return the number of entries in allele_values table corresponding to the given datasetId
	 * @throws MiddlewareQueryException the MiddlewareQueryException
	 */
	public long countByDatasetId(Integer datasetId) throws MiddlewareQueryException {
		try {
			if (datasetId != null) {
				Query query = this.getSession().createSQLQuery(AlleleValuesDAO.COUNT_BY_DATASET_ID);
				query.setParameter("datasetId", datasetId);
				BigInteger result = (BigInteger) query.uniqueResult();
				if (result != null) {
					return result.longValue();
				}
			}
		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error with countByDatasetId(datasetId=" + datasetId + ") query from AlleleValues: " + e.getMessage(), e);
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
				SQLQuery query = this.getSession().createSQLQuery(AlleleValuesDAO.GET_GIDS_BY_MARKER_ID);
				query.setParameter("markerId", markerId);
				query.setFirstResult(start);
				query.setMaxResults(numOfRows);
				return query.list();
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getGIDsByMarkerId(markerId=" + markerId + ") query from AlleleValues: " + e.getMessage(),
					e);
		}
		return new ArrayList<Integer>();
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getGidsByMarkersAndAlleleValues(List<Integer> markerIdList, List<String> alleleValueList)
			throws MiddlewareQueryException {
		List<Integer> values = new ArrayList<Integer>();

		if (markerIdList.isEmpty() || alleleValueList.isEmpty()) {
			throw new MiddlewareQueryException("markerIdList and alleleValueList must not be empty");
		}
		if (markerIdList.size() != alleleValueList.size()) {
			throw new MiddlewareQueryException("markerIdList and alleleValueList must have the same size");
		}

		List<String> placeholderList = new ArrayList<String>();
		for (int i = 0; i < markerIdList.size(); i++) {
			placeholderList.add("(?,?)");
		}
		String placeholders = StringUtil.joinIgnoreNull(",", placeholderList);

		String sql =
				new StringBuffer().append("SELECT gid ").append("FROM gdms_allele_values ")
						.append("WHERE (marker_id, allele_bin_value) IN (" + placeholders + ") ").toString();

		try {
			SQLQuery query = this.getSession().createSQLQuery(sql);
			for (int i = 0; i < markerIdList.size(); i++) {
				int baseIndex = i * 2;

				query.setInteger(baseIndex, markerIdList.get(i));
				query.setString(baseIndex + 1, alleleValueList.get(i));
			}

			values = query.list();

		} catch (HibernateException e) {
			this.logAndThrowException("Error with getGidsByMarkersAndAlleleValues(markerIdList=" + markerIdList + ", alleleValueList="
					+ alleleValueList + "): " + e.getMessage(), e);
		}

		return values;
	}

	public long countAlleleValuesByGids(List<Integer> gids) throws MiddlewareQueryException {
		try {
			if (gids != null && !gids.isEmpty()) {
				SQLQuery query = this.getSession().createSQLQuery(AlleleValuesDAO.COUNT_ALLELE_VALUES_BY_GIDS);
				query.setParameterList("gids", gids);
				BigInteger result = (BigInteger) query.uniqueResult();
				if (result != null) {
					return result.longValue();
				}
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with countAlleleValuesByGids(gids=" + gids + ") query from AlleleValues: " + e.getMessage(), e);
		}
		return 0;
	}

	public List<AllelicValueElement> getIntAlleleValuesForPolymorphicMarkersRetrieval(List<Integer> gids, int start, int numOfRows)
			throws MiddlewareQueryException {
		List<AllelicValueElement> values = new ArrayList<AllelicValueElement>();
		try {
			if (gids != null && !gids.isEmpty()) {
				SQLQuery query =
						this.getSession().createSQLQuery(AlleleValuesDAO.GET_INT_ALLELE_VALUES_FOR_POLYMORPHIC_MARKERS_RETRIEVAL_BY_GIDS);
				query.setParameterList("gids", gids);
				query.setFirstResult(start);
				query.setMaxResults(numOfRows);
				List results = query.list();

				for (Object o : results) {
					Object[] result = (Object[]) o;
					if (result != null) {
						Integer datasetId = (Integer) result[0];
						Integer gid = (Integer) result[1];
						Integer markerId = (Integer) result[2];
						String markerName = (String) result[3];
						String alleleBinValue = (String) result[4];
						Integer peakHeight = (Integer) result[5];
						Integer markerSampleId = (Integer) result[6];
						Integer accSampleId = (Integer) result[7];
						AllelicValueElement allelicValueElement =
								new AllelicValueElement(datasetId, gid, markerId, markerName, alleleBinValue, peakHeight, markerSampleId,
										accSampleId);
						values.add(allelicValueElement);
					}
				}

			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getIntAlleleValuesForPolymorphicMarkersRetrieval(gids=" + gids
					+ ") query from AlleleValues: " + e.getMessage(), e);
		}
		return values;
	}

	public long countIntAlleleValuesForPolymorphicMarkersRetrieval(List<Integer> gids) throws MiddlewareQueryException {
		try {
			if (gids != null && !gids.isEmpty()) {
				SQLQuery query =
						this.getSession().createSQLQuery(AlleleValuesDAO.COUNT_INT_ALLELE_VALUES_FOR_POLYMORPHIC_MARKERS_RETRIEVAL_BY_GIDS);
				query.setParameterList("gids", gids);
				BigInteger result = (BigInteger) query.uniqueResult();
				if (result != null) {
					return result.longValue();
				}
				return 0;
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with countCharAlleleValuesForPolymorphicMarkersRetrieval(gids=" + gids
					+ ") query from AlleleValues: " + e.getMessage(), e);
		}
		return 0;
	}

	public List<AllelicValueElement> getCharAlleleValuesForPolymorphicMarkersRetrieval(List<Integer> gids, int start, int numOfRows)
			throws MiddlewareQueryException {
		List<AllelicValueElement> values = new ArrayList<AllelicValueElement>();
		try {
			if (gids != null && !gids.isEmpty()) {
				SQLQuery query =
						this.getSession().createSQLQuery(AlleleValuesDAO.GET_CHAR_ALLELE_VALUES_FOR_POLYMORPHIC_MARKERS_RETRIEVAL_BY_GIDS);
				query.setParameterList("gids", gids);
				query.setFirstResult(start);
				query.setMaxResults(numOfRows);
				List results = query.list();

				for (Object o : results) {
					Object[] result = (Object[]) o;
					if (result != null) {
						Integer datasetId = (Integer) result[0];
						Integer gid = (Integer) result[1];
						Integer markerId = (Integer) result[2];
						String markerName = (String) result[3];
						String charValue = (String) result[4];
						Integer markerSampleId = (Integer) result[5];
						Integer accSampleId = (Integer) result[6];
						AllelicValueElement allelicValueElement =
								new AllelicValueElement(datasetId, gid, markerId, markerName, charValue, markerSampleId, accSampleId);
						values.add(allelicValueElement);
					}
				}
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getCharAlleleValuesForPolymorphicMarkersRetrieval(gids=" + gids
					+ ") query from AlleleValues: " + e.getMessage(), e);
		}
		return values;
	}

	public long countCharAlleleValuesForPolymorphicMarkersRetrieval(List<Integer> gids) throws MiddlewareQueryException {
		try {
			if (gids != null && !gids.isEmpty()) {
				SQLQuery query =
						this.getSession()
								.createSQLQuery(AlleleValuesDAO.COUNT_CHAR_ALLELE_VALUES_FOR_POLYMORPHIC_MARKERS_RETRIEVAL_BY_GIDS);
				query.setParameterList("gids", gids);
				BigInteger result = (BigInteger) query.uniqueResult();
				if (result != null) {
					return result.longValue();
				}
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with countCharAlleleValuesForPolymorphicMarkersRetrieval(gids=" + gids
					+ ") query from AlleleValues: " + e.getMessage(), e);
		}
		return 0;
	}

	public List<AllelicValueElement> getMappingAlleleValuesForPolymorphicMarkersRetrieval(List<Integer> gids, int start, int numOfRows)
			throws MiddlewareQueryException {
		try {
			if (gids != null && !gids.isEmpty()) {
				SQLQuery query =
						this.getSession().createSQLQuery(
								AlleleValuesDAO.GET_MAPPING_ALLELE_VALUES_FOR_POLYMORPHIC_MARKERS_RETRIEVAL_BY_GIDS);
				query.setParameterList("gids", gids);
				query.setFirstResult(start);
				query.setMaxResults(numOfRows);
				List results = query.list();

				List<AllelicValueElement> values = new ArrayList<AllelicValueElement>();

				for (Object o : results) {
					Object[] result = (Object[]) o;
					if (result != null) {
						Integer datasetId = (Integer) result[0];
						Integer gid = (Integer) result[1];
						Integer markerId = (Integer) result[2];
						String markerName = (String) result[3];
						String data = (String) result[4];
						Integer markerSampleId = (Integer) result[5];
						Integer accSampleId = (Integer) result[6];
						AllelicValueElement allelicValueElement =
								new AllelicValueElement(datasetId, gid, markerId, markerName, data, markerSampleId, accSampleId);
						values.add(allelicValueElement);
					}
				}

				return values;
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getMappingAlleleValuesForPolymorphicMarkersRetrieval(gids=" + gids
					+ ") query from AlleleValues: " + e.getMessage(), e);
		}
		return new ArrayList<AllelicValueElement>();
	}

	public long countMappingAlleleValuesForPolymorphicMarkersRetrieval(List<Integer> gids) throws MiddlewareQueryException {
		try {
			if (gids != null && !gids.isEmpty()) {
				SQLQuery query =
						this.getSession().createSQLQuery(
								AlleleValuesDAO.COUNT_MAPPING_ALLELE_VALUES_FOR_POLYMORPHIC_MARKERS_RETRIEVAL_BY_GIDS);
				query.setParameterList("gids", gids);
				BigInteger result = (BigInteger) query.uniqueResult();
				if (result != null) {
					return result.longValue();
				}
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with countMappingAlleleValuesForPolymorphicMarkersRetrieval(gids=" + gids
					+ ") query from AlleleValues: " + e.getMessage(), e);
		}
		return 0;
	}

	public void deleteByDatasetId(int datasetId) throws MiddlewareQueryException {
		try {
			// Please note we are manually flushing because non hibernate based deletes and updates causes the Hibernate session to get out of synch with
			// underlying database. Thus flushing to force Hibernate to synchronize with the underlying database before the delete
			// statement
			this.getSession().flush();
			
			SQLQuery statement = this.getSession().createSQLQuery("DELETE FROM gdms_allele_values WHERE dataset_id = " + datasetId);
			statement.executeUpdate();
		} catch (HibernateException e) {
			this.logAndThrowException("Error in deleteByDatasetId=" + datasetId + " in AlleleValuesDAO: " + e.getMessage(), e);
		}
	}

	public List<MarkerSampleId> getMarkerSampleIdsByGids(List<Integer> gIds) throws MiddlewareQueryException {
		List<MarkerSampleId> toReturn = new ArrayList<MarkerSampleId>();
		try {
			if (gIds != null && !gIds.isEmpty()) {
				SQLQuery query = this.getSession().createSQLQuery(AlleleValuesDAO.GET_MARKER_SAMPLE_IDS_BY_GIDS);
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
					"Error with getMarkerSampleIdsByGids(gIds=" + gIds + ") query from AlleleValuesDAO: " + e.getMessage(), e);
		}
		return toReturn;
	}

	public long countByGids(List<Integer> gIds) throws MiddlewareQueryException {
		try {
			if (gIds != null && gIds.get(0) != null) {
				SQLQuery query = this.getSession().createSQLQuery(AlleleValuesDAO.COUNT_BY_GIDS);
				query.setParameterList("gIdList", gIds);
				BigInteger result = (BigInteger) query.uniqueResult();
				if (result != null) {
					return result.longValue();
				}
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with countByGids(gIds=" + gIds + ") query from AlleleValuesDAO: " + e.getMessage(), e);
		}
		return 0;
	}
	
	@SuppressWarnings({"deprecation", "unchecked"})
	public List<Object> getUniqueAllelesByGidsAndMids(List<Integer> gids, List<Integer> mids) {
		
		List<Object> results = new ArrayList<>();
		try {
			if (gids != null) {
				SQLQuery query =
						this.getSession().createSQLQuery(AlleleValuesDAO.GET_UNIQUE_ALLELIC_VALUES_BY_GIDS_AND_MIDS);
				query.setParameterList("gids", gids);				
				query.setParameterList("mids", mids);				
				query.addScalar("gid", IntegerType.INSTANCE);
				query.addScalar("marker_id", IntegerType.INSTANCE);
				query.addScalar("allele_bin_value", StringType.INSTANCE);
				query.addScalar("acc_sample_id", IntegerType.INSTANCE);
				query.addScalar("marker_sample_id", IntegerType.INSTANCE);
				results = query.list();

					}
		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error with getUniqueAllelesByGidsAndMids(gids=" + gids + ") query from AlleleValuesDAO " + e.getMessage(), e);
		}
		return results;
				
	}

}
