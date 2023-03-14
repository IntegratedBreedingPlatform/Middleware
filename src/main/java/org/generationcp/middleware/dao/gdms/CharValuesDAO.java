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

import com.google.common.base.Preconditions;
import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.gdms.AllelicValueElement;
import org.generationcp.middleware.pojos.gdms.CharValueElement;
import org.generationcp.middleware.pojos.gdms.CharValues;
import org.generationcp.middleware.pojos.gdms.MarkerSampleId;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

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

	// another transferred query from the Vaadin layer
	public static final String GET_UNIQUE_ALLELIC_VALUES_BY_GIDS_AND_MIDS = "select distinct gid,marker_id, char_value,acc_sample_id,marker_sample_id from gdms_char_values where"
			+ " gid in(:gids) and marker_id in (:mids) ORDER BY gid, marker_id,acc_sample_id asc";

	public static final String GET_GIDS_BY_MARKER_ID = "SELECT DISTINCT gid " + "FROM gdms_char_values " + "WHERE marker_id = :markerId";

	public static final String COUNT_GIDS_BY_MARKER_ID = "SELECT COUNT(distinct gid) " + "FROM gdms_char_values "
			+ "WHERE marker_id = :markerId";

	public static final String GET_MARKER_SAMPLE_IDS_BY_GIDS = "SELECT DISTINCT marker_id, marker_sample_id " + "FROM gdms_char_values "
			+ "WHERE gid IN (:gids)";

	public static final String GET_ALLELIC_VALUES_BY_MARKER_IDS =
			"SELECT ac_id, dataset_id, marker_id, gid, CONCAT(char_value, ''), marker_sample_id, acc_sample_id "
					+ "FROM gdms_char_values cv " + "WHERE  cv.marker_id IN (:markerIdList) " + "ORDER BY cv.gid DESC ";

	private static final String GET_CHAR_VALUES_ELEMENTS_BY_DATASETID =
			"SELECT sample.sample_bk as sampleUID, " //
					+ "  charvalues.acc_sample_id as accessionId, " //
					+ "  sample.sample_name as sampleName, " //
					+ "  stock.dbxref_id as gid, " //
					+ "  (SELECT na.nval FROM names na " //
					+ "  WHERE na.gid = stock.dbxref_id AND na.nstat = 1 LIMIT 1) AS designation, " //
					+ "  sample.sample_no as sampleNo, " //
					+ "  marker.marker_id as markerId, " //
					+ "  marker.marker_name as markerName, " //
					+ "  charvalues.char_value as charValue, " //
					+ "  charvalues.dataset_id as datasetId " //
					+ "  FROM gdms_char_values charvalues " //
					+ "  INNER JOIN sample sample ON (sample.sample_id = charvalues.sample_id) " //
					+ "  INNER JOIN nd_experiment experiment ON (sample.nd_experiment_id = experiment.nd_experiment_id) " //
					+ "  INNER JOIN stock stock ON (stock.stock_id = experiment.stock_id) " //
					+ "  INNER JOIN gdms_marker marker ON (marker.marker_id = charvalues.marker_id) " //
					+ "  WHERE charvalues.dataset_id = :datasetId "; //

	private static final Logger LOG = LoggerFactory.getLogger(CharValuesDAO.class);

	public CharValuesDAO(final Session session) {
		super(session);
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getGIDsByMarkerId(final Integer markerId, final int start, final int numOfRows) {

		try {
			if (markerId != null) {
				SQLQuery query = this.getSession().createSQLQuery(CharValuesDAO.GET_GIDS_BY_MARKER_ID);
				query.setParameter("markerId", markerId);
				query.setFirstResult(start);
				query.setMaxResults(numOfRows);
				return query.list();
			}
		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error with getGIDsByMarkerId(markerId=" + markerId + ") query from CharValues: " + e.getMessage(), e);
		}
		return new ArrayList<>();
	}

	public long countGIDsByMarkerId(final Integer markerId) {
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
			throw new MiddlewareQueryException("Error with countGIDsByMarkerId(markerId=" + markerId + ") query from CharValues: " + e.getMessage(),
					e);
		}
		return 0;
	}

	public void deleteByDatasetId(final int datasetId) {
		try {
			// Please note we are manually flushing because non hibernate based deletes and updates causes the Hibernate session to get out of synch with
			// underlying database. Thus flushing to force Hibernate to synchronize with the underlying database before the delete
			// statement
			this.getSession().flush();
			
			SQLQuery statement = this.getSession().createSQLQuery("DELETE FROM gdms_char_values WHERE dataset_id = " + datasetId);
			statement.executeUpdate();
		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error in deleteByDatasetId=" + datasetId + " in CharValuesDAO: " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("rawtypes")
	public List<MarkerSampleId> getMarkerSampleIdsByGids(final List<Integer> gIds) {
		List<MarkerSampleId> toReturn = new ArrayList<>();

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
			throw new MiddlewareQueryException("Error with getMarkerIdsByGids(gIds=" + gIds + ") query from CharValues: " + e.getMessage(), e);
		}
		return toReturn;
	}
	
	@SuppressWarnings({"deprecation", "unchecked"})
	public List<Object> getUniqueCharAllelesByGidsAndMids(final List<Integer> gids, final List<Integer> mids) {
		
		List<Object> results = new ArrayList<>();
		try {
			if (gids != null) {
				SQLQuery query =
						this.getSession().createSQLQuery(CharValuesDAO.GET_UNIQUE_ALLELIC_VALUES_BY_GIDS_AND_MIDS);
				query.setParameterList("gids", gids);				
				query.setParameterList("mids", mids);				
				query.addScalar("gid", IntegerType.INSTANCE);
				query.addScalar("marker_id", IntegerType.INSTANCE);
				query.addScalar("char_value", StringType.INSTANCE);
				query.addScalar("acc_sample_id", IntegerType.INSTANCE);
				query.addScalar("marker_sample_id", IntegerType.INSTANCE);
				results = query.list();

					}
		} catch (HibernateException e) {
			throw new MiddlewareQueryException(
					"Error with getUniqueAllelesByGidsAndMids(gids=" + gids + ") query from CharValuesDAO " + e.getMessage(), e);
		}
		return results;
				
	}

	@SuppressWarnings("rawtypes")
	public List<AllelicValueElement> getAlleleValuesByMarkerId(final List<Integer> markerIdList) {
		List<AllelicValueElement> returnVal = new ArrayList<>();

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
			throw new MiddlewareQueryException("Error with getAlleleValuesByMarkerId() query from AlleleValues: " + e.getMessage(), e);
		}

		return returnVal;
	}


	@SuppressWarnings("unchecked")
	public List<CharValues> getCharValuesByMarkerIds(final List<Integer> markerIds) {
		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.in("markerId", markerIds));
			return criteria.list();

		} catch (HibernateException e) {
			final String errorMessage = "Error in getCharValuesByMarkerIds=" + markerIds.toString() + " query on CharValuesDAO: " + e.getMessage();
			CharValuesDAO.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	public List<CharValueElement> getCharValueElementsByDatasetId (final Integer datasetId) {
		Preconditions.checkNotNull(datasetId);
		try {
			return this.getSession().createSQLQuery(GET_CHAR_VALUES_ELEMENTS_BY_DATASETID)
					.addScalar("sampleUID", new StringType())
					.addScalar("accessionId", new IntegerType())
					.addScalar("sampleName", new StringType())
					.addScalar("gid", new IntegerType())
					.addScalar("designation", new StringType())
					.addScalar("sampleNo", new IntegerType())
					.addScalar("markerId", new IntegerType())
					.addScalar("markerName", new StringType())
					.addScalar("charValue", new StringType())
					.addScalar("datasetId", new IntegerType())
					.setParameter("datasetId", datasetId)
					.setResultTransformer(Transformers.aliasToBean(CharValueElement.class))
					.list();
			
		} catch (HibernateException e) {
			final String errorMessage = "Error with getCharValueElementsByDatasetId(datasetId=" + datasetId + ") query from CharValuesDAO " + e.getMessage();
			CharValuesDAO.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

}
