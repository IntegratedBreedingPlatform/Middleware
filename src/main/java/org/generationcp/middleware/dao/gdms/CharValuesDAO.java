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

import com.google.common.base.Preconditions;
import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.gdms.AllelicValueElement;
import org.generationcp.middleware.pojos.gdms.AllelicValueWithMarkerIdElement;
import org.generationcp.middleware.pojos.gdms.CharValues;
import org.generationcp.middleware.pojos.gdms.Dataset;
import org.generationcp.middleware.pojos.gdms.MarkerSampleId;
import org.generationcp.middleware.util.StringUtil;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DAO class for {@link CharValues}.
 *
 * @author Joyce Avestro
 *
 */
public class CharValuesDAO extends GenericDAO<CharValues, Integer> {

	// For getMarkerNamesByGIds()
	public static final String GET_CHAR_COUNT_BY_GID = "SELECT COUNT(*) " + "FROM gdms_char_values " + "WHERE gid IN (:gIdList)";

	// For getGermplasmNamesByMarkerNames()
	public static final String GET_CHAR_COUNT_BY_MARKER_ID = "SELECT COUNT(*) " + "FROM gdms_char_values "
			+ "WHERE marker_id IN (:markerIdList)";

	// For getGermplasmNamesByMarkerNames()
	public static final String GET_CHAR_GERMPLASM_NAME_AND_MARKER_NAME_BY_MARKER_NAMES = "SELECT n.nval, CONCAT(m.marker_name, '') "
			+ "FROM names n JOIN gdms_char_values c ON n.gid = c.gid " + "           JOIN gdms_marker m ON c.marker_id = m.marker_id "
			+ "WHERE marker_name IN (:markerNameList) AND n.nstat = 1 " + "ORDER BY n.nval, m.marker_name";

	public static final String GET_ALLELIC_VALUES_BY_GIDS_AND_MARKER_IDS = "SELECT DISTINCT " + "gcv.gid, " + "gcv.marker_id, "
			+ "CONCAT(gcv.char_value, ''), " + "CAST(NULL AS UNSIGNED INTEGER), "
			+ // peak height
			"gcv.marker_sample_id, " + "gcv.acc_sample_id " + "FROM gdms_char_values gcv " + "WHERE gcv.gid IN (:gidList) "
			+ "AND gcv.marker_id IN (:markerIdList) " + "ORDER BY gcv.gid DESC ";

	public static final String GET_ALLELIC_VALUES_BY_GID_LOCAL = "SELECT DISTINCT " + "gdms_char_values.gid, "
			+ "gdms_char_values.marker_id, " + "CONCAT(gdms_char_values.char_value, ''), " + "CAST(NULL AS UNSIGNED INTEGER) " + // peak
																																	// height
			"FROM gdms_char_values " + "WHERE gdms_char_values.gid IN (:gidList) " + "ORDER BY gdms_char_values.gid ASC ";

	// For getAllelicValues by datasetId
	public static final String GET_ALLELIC_VALUES_BY_DATASET_ID = "SELECT gid, marker_id,  CONCAT(char_value, '') "
			+ "           , marker_sample_id, acc_sample_id " + "FROM gdms_char_values " + "WHERE dataset_id = :datasetId "
			+ "ORDER BY gid ASC, marker_id ASC";
	
	//getAllelicValues by datasetId - distinct (not sure of difference wrt query above)
	public static final String GET_UNIQUE_ALLELIC_VALUES_BY_DATASET_ID = "SELECT DISTINCT\n"
		+ "  sample_id,\n"
		+ "  marker_id,\n"
		+ "  char_value,\n"
		+ "  acc_sample_id,\n"
		+ "  marker_sample_id\n"
		+ "FROM gdms_char_values\n"
		+ "WHERE dataset_id = :datasetId\n"
		+ "ORDER BY sample_id, marker_id, acc_sample_id ASC";

	// another transferred query from the Vaadin layer
	public static final String GET_UNIQUE_ALLELIC_VALUES_BY_GIDS_AND_MIDS = "select distinct gid,marker_id, char_value,acc_sample_id,marker_sample_id from gdms_char_values where"
			+ " gid in(:gids) and marker_id in (:mids) ORDER BY gid, marker_id,acc_sample_id asc";

	public static final String COUNT_BY_DATASET_ID = "SELECT COUNT(*) " + "FROM gdms_char_values " + "WHERE dataset_id = :datasetId";

	public static final String GET_GIDS_BY_MARKER_ID = "SELECT DISTINCT gid " + "FROM gdms_char_values " + "WHERE marker_id = :markerId";

	public static final String COUNT_GIDS_BY_MARKER_ID = "SELECT COUNT(distinct gid) " + "FROM gdms_char_values "
			+ "WHERE marker_id = :markerId";

	public static final String GET_MARKER_SAMPLE_IDS_BY_GIDS = "SELECT DISTINCT marker_id, marker_sample_id " + "FROM gdms_char_values "
			+ "WHERE gid IN (:gids)";

	public static final String GET_ALLELIC_VALUES_BY_MARKER_IDS =
			"SELECT ac_id, dataset_id, marker_id, gid, CONCAT(char_value, ''), marker_sample_id, acc_sample_id "
					+ "FROM gdms_char_values cv " + "WHERE  cv.marker_id IN (:markerIdList) " + "ORDER BY cv.gid DESC ";

	private static final Logger LOG = LoggerFactory.getLogger(CharValuesDAO.class);

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
	// FIXME Could be removed after fixing retrieves
	public List<AllelicValueWithMarkerIdElement> getAllelicValuesByDatasetId(final Integer datasetId, final int start, final int numOfRows)
			throws MiddlewareQueryException {
		List<AllelicValueWithMarkerIdElement> toReturn = new ArrayList<AllelicValueWithMarkerIdElement>();

		try {
			if (datasetId != null) {
				SQLQuery query = this.getSession().createSQLQuery(CharValuesDAO.GET_ALLELIC_VALUES_BY_DATASET_ID);
				query.setParameter("datasetId", datasetId);
				query.setFirstResult(start);
				query.setMaxResults(numOfRows);
				List results = query.list();

				for (final Object o : results) {
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
			}
		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error with getAllelicValuesByDatasetId(datasetId=" + datasetId + ") queryfrom char_values : " + e.getMessage(), e);
		}
		return toReturn;
	}

	/**
	 * Count by dataset id.
	 *
	 * @param datasetId the dataset id
	 * @return the number of entries in char_values table corresponding to the given datasetId
	 * @throws MiddlewareQueryException the MiddlewareQueryException
	 */
	// FIXME Could be removed after fixing retrieves
	public long countByDatasetId(final Integer datasetId) throws MiddlewareQueryException {
		try {
			if (datasetId != null) {
				Query query = this.getSession().createSQLQuery(CharValuesDAO.COUNT_BY_DATASET_ID);
				query.setParameter("datasetId", datasetId);
				BigInteger result = (BigInteger) query.uniqueResult();
				if (result != null) {
					return result.longValue();
				}
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with countByDatasetId(datasetId=" + datasetId + ") query from char_values: " + e.getMessage(),
					e);
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getGIDsByMarkerId(final Integer markerId, final int start, final int numOfRows) throws MiddlewareQueryException {

		try {
			if (markerId != null) {
				SQLQuery query = this.getSession().createSQLQuery(CharValuesDAO.GET_GIDS_BY_MARKER_ID);
				query.setParameter("markerId", markerId);
				query.setFirstResult(start);
				query.setMaxResults(numOfRows);
				return query.list();
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getGIDsByMarkerId(markerId=" + markerId + ") query from CharValues: " + e.getMessage(), e);
		}
		return new ArrayList<Integer>();
	}

	public long countGIDsByMarkerId(final Integer markerId) throws MiddlewareQueryException {
		try {
			if (markerId != null) {
				SQLQuery query = this.getSession().createSQLQuery(CharValuesDAO.COUNT_GIDS_BY_MARKER_ID);
				query.setParameter("markerId", markerId);
				BigInteger result = (BigInteger) query.uniqueResult();
				if (result != null) {
					return result.longValue();
				}
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with countGIDsByMarkerId(markerId=" + markerId + ") query from CharValues: " + e.getMessage(),
					e);
		}
		return 0;
	}

	public void deleteByDatasetId(final int datasetId) throws MiddlewareQueryException {
		try {
			// Please note we are manually flushing because non hibernate based deletes and updates causes the Hibernate session to get out of synch with
			// underlying database. Thus flushing to force Hibernate to synchronize with the underlying database before the delete
			// statement
			this.getSession().flush();
			
			SQLQuery statement = this.getSession().createSQLQuery("DELETE FROM gdms_char_values WHERE dataset_id = " + datasetId);
			statement.executeUpdate();
		} catch (HibernateException e) {
			this.logAndThrowException("Error in deleteByDatasetId=" + datasetId + " in CharValuesDAO: " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("rawtypes")
	// FIXME Could be removed after fixing retrieves
	public List<MarkerSampleId> getMarkerSampleIdsByGids(final List<Integer> gIds) throws MiddlewareQueryException {
		List<MarkerSampleId> toReturn = new ArrayList<MarkerSampleId>();

		try {
			if (gIds != null && !gIds.isEmpty()) {
				SQLQuery query = this.getSession().createSQLQuery(CharValuesDAO.GET_MARKER_SAMPLE_IDS_BY_GIDS);
				query.setParameterList("gids", gIds);

				List results = query.list();
				for (final Object o : results) {
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
			this.logAndThrowException("Error with getMarkerIdsByGids(gIds=" + gIds + ") query from CharValues: " + e.getMessage(), e);
		}
		return toReturn;
	}
	
	@SuppressWarnings({"deprecation", "unchecked"})
	// FIXME Could be removed after fixing retrieves
	public List<Object> getUniqueAllelesByDatasetId(final String datasetId) {
		
		List<Object> results = new ArrayList<>();
		try {
			if (datasetId != null) {
				SQLQuery query = this.getSession().createSQLQuery(CharValuesDAO.GET_UNIQUE_ALLELIC_VALUES_BY_DATASET_ID);
				query.setParameter("datasetId", datasetId);
				query.addScalar("sample_id", Hibernate.INTEGER);
				query.addScalar("marker_id", Hibernate.INTEGER);
				query.addScalar("char_value", Hibernate.STRING);
				query.addScalar("acc_sample_id", Hibernate.INTEGER);
				query.addScalar("marker_sample_id", Hibernate.INTEGER);
				results = query.list();

			}
		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error with getUniqueAllelesByDatasetId(datasetId=" + datasetId + ") query from CharValuesDAO " + e.getMessage(), e);
		}
		return results;
				
	}
	
	@SuppressWarnings({"deprecation", "unchecked"})
	// FIXME Could be removed after fixing retrieves
	public List<Object> getUniqueCharAllelesByGidsAndMids(final List<Integer> gids, final List<Integer> mids) {
		
		List<Object> results = new ArrayList<>();
		try {
			if (gids != null) {
				SQLQuery query =
						this.getSession().createSQLQuery(CharValuesDAO.GET_UNIQUE_ALLELIC_VALUES_BY_GIDS_AND_MIDS);
				query.setParameterList("gids", gids);				
				query.setParameterList("mids", mids);				
				query.addScalar("gid", Hibernate.INTEGER);
				query.addScalar("marker_id", Hibernate.INTEGER);
				query.addScalar("char_value", Hibernate.STRING);
				query.addScalar("acc_sample_id", Hibernate.INTEGER);
				query.addScalar("marker_sample_id", Hibernate.INTEGER);
				results = query.list();

					}
		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error with getUniqueAllelesByGidsAndMids(gids=" + gids + ") query from CharValuesDAO " + e.getMessage(), e);
		}
		return results;
				
	}

	@SuppressWarnings("rawtypes")
	public List<AllelicValueElement> getAlleleValuesByMarkerId(final List<Integer> markerIdList) throws MiddlewareQueryException {
		List<AllelicValueElement> returnVal = new ArrayList<AllelicValueElement>();

		if (markerIdList == null || markerIdList.isEmpty()) {
			return returnVal;
		}

		try {
			SQLQuery query = this.getSession().createSQLQuery(CharValuesDAO.GET_ALLELIC_VALUES_BY_MARKER_IDS);
			query.setParameterList("markerIdList", markerIdList);

			List results = query.list();

			for (final Object o : results) {
				Object[] result = (Object[]) o;
				if (result != null) {
					Integer acId = (Integer) result[0];
					Integer datasetId = (Integer) result[1];
					Integer markerId = (Integer) result[2];
					Integer gId = (Integer) result[3];
					String data = (String) result[4];
					Integer markerSampleId = (Integer) result[5];
					Integer accSampleId = (Integer) result[6];
					AllelicValueElement value = new AllelicValueElement(acId, datasetId, gId, markerId, data, markerSampleId, accSampleId);
					returnVal.add(value);
				}
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getAlleleValuesByMarkerId() query from AlleleValues: " + e.getMessage(), e);
		}

		return returnVal;
	}


	@SuppressWarnings("unchecked")
	public List<CharValues> getCharValuesByMarkerIds(final List<Integer> markerIds) throws MiddlewareQueryException {
		List<CharValues> toReturn = new ArrayList<CharValues>();
		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.in("markerId", markerIds));
			toReturn = criteria.list();

		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error in getCharValuesByMarkerIds=" + markerIds.toString() + " query on CharValuesDAO: " + e.getMessage(), e);
		}

		return toReturn;
	}

}
