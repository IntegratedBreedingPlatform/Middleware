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

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.gdms.AllelicValueElement;
import org.generationcp.middleware.pojos.gdms.GermplasmMarkerElement;
import org.generationcp.middleware.pojos.gdms.Marker;
import org.generationcp.middleware.pojos.gdms.MarkerIdMarkerNameElement;
import org.generationcp.middleware.pojos.gdms.MarkerNameElement;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * DAO class for {@link Marker}.
 * <p/>
 * <b>Authors</b>: Mark Agarrado <br>
 * <b>File Created</b>: Jul 10, 2012
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class MarkerDAO extends GenericDAO<Marker, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(MarkerDAO.class);

	public static final String GET_MARKER_IDS_BY_MAP_ID_AND_LINKAGE_BETWEEN_START_POSITION = "SELECT marker_id "
		+ "FROM gdms_markers_onmap " + "WHERE map_id = :mapId " + "AND linkage_group = :linkageGroup "
		+ "AND start_position >= :startPosition " + "AND end_position <= :endPosition " + "ORDER BY marker_id";

	public static final String COUNT_MARKER_IDS_BY_MAP_ID_AND_LINKAGE_BETWEEN_START_POSITION = "SELECT COUNT(marker_id) "
		+ "FROM gdms_markers_onmap " + "WHERE map_id = :map_id " + "AND linkage_group = :linkage_group " + "AND start_position "
		+ "BETWEEN :start_position " + "AND :end_position";

	public static final String GET_MARKER_TYPE_BY_MARKER_IDS = "SELECT DISTINCT CONCAT(marker_type, '') " + "FROM gdms_marker "
		+ "WHERE marker_id IN (:markerIdList)";

	public static final String GET_IDS_BY_NAMES = "SELECT marker_id " + "FROM gdms_marker " + "WHERE marker_name IN (:markerNameList)";

	public static final String GET_ID_AND_NAME_BY_NAMES = "SELECT marker_id, CONCAT(marker_name,'') " + "FROM gdms_marker "
		+ "WHERE marker_name IN (:markerNameList) " + "ORDER BY marker_id ";

	public static final String GET_NAMES_BY_IDS = "SELECT marker_id, CONCAT(marker_name, '') AS marker_name " + "FROM gdms_marker "
		+ "WHERE marker_id IN (:markerIdList) " + "ORDER BY marker_id asc";

	public static final String GET_ALL_MARKER_TYPES = "SELECT DISTINCT CONCAT(marker_type, '') " + "FROM gdms_marker "
		+ "WHERE UPPER(marker_type) != 'UA'";

	public static final String GET_NAMES_BY_TYPE = "SELECT DISTINCT CONCAT(marker_name, '') " + "FROM gdms_marker "
		+ "WHERE UPPER(marker_type) = UPPER(:markerType)";

	public static final String COUNT_ALL_MARKER_TYPES = "SELECT COUNT(DISTINCT marker_type) " + "FROM gdms_marker "
		+ "WHERE UPPER(marker_type) != 'UA'";

	public static final String COUNT_MARKER_NAMES_BY_MARKER_TYPE = "SELECT COUNT(DISTINCT marker_name) " + "FROM gdms_marker "
		+ "WHERE UPPER(marker_type) = UPPER(:markerType)";

	// For getMarkerNamesByGIds()
	public static final String GET_ALLELE_MARKER_NAMES_BY_GID =
		"SELECT DISTINCT gdms_allele_values.gid, gdms_allele_values.marker_id, CONCAT(gdms_marker.marker_name,'') "
			+ "FROM gdms_allele_values LEFT JOIN gdms_marker ON gdms_allele_values.marker_id = gdms_marker.marker_id "
			+ "WHERE gdms_allele_values.gid IN (:gIdList) " + "ORDER BY gid, marker_name";

	public static final String GET_CHAR_MARKER_NAMES_BY_GID =
		"SELECT DISTINCT gdms_char_values.gid, gdms_char_values.marker_id, CONCAT(gdms_marker.marker_name,'') "
			+ "FROM gdms_char_values LEFT JOIN gdms_marker ON gdms_char_values.marker_id = gdms_marker.marker_id "
			+ "WHERE gdms_char_values.gid IN (:gIdList) " + "ORDER BY gid, marker_name";

	public static final String GET_MAPPING_MARKER_NAMES_BY_GID =
		"SELECT DISTINCT gdms_mapping_pop_values.gid, gdms_mapping_pop_values.marker_id, CONCAT(gdms_marker.marker_name,'') "
			+ "FROM gdms_mapping_pop_values LEFT JOIN gdms_marker ON gdms_mapping_pop_values.marker_id = gdms_marker.marker_id "
			+ "WHERE gdms_mapping_pop_values.gid IN (:gIdList) " + "ORDER BY gid, marker_name";

	public static final String GET_MARKER_IDS_BY_HAPLOTYPE = "SELECT track.marker_id  " + "FROM gdms_track_markers track "
		+ "INNER JOIN gdms_track_data tdata ON (tdata.track_id = track.track_id) " + "WHERE track_name = (:trackName)";

	public static final String GET_ALL_DB_ACCESSION_IDS = "SELECT DISTINCT (db_accession_id) " + "FROM gdms_marker "
		+ "WHERE db_accession_id is not null " + "OR db_accession_id != ''";

	public static final String COUNT_ALL_DB_ACCESSION_IDS = "SELECT COUNT(DISTINCT db_accession_id) " + "FROM gdms_marker "
		+ "WHERE db_accession_id is not null " + "OR db_accession_id != ''";

	public static final String GET_MARKERS_SELECT_CLAUSE = "SELECT marker_id  " + ", CONCAT(marker_type, '') "
		+ ", CONCAT(marker_name, '') " + ", CONCAT(species, '') " + ", db_accession_id " + ", reference " + ", CONCAT(genotype, '') "
		+ ", ploidy  " + ", primer_id  " + ", remarks  " + ", assay_type " + ", motif  " + ", forward_primer  " + ", reverse_primer  "
		+ ", product_size  " + ", annealing_temp " + ", amplification ";
	public static final String GET_MARKERS_BY_IDS = MarkerDAO.GET_MARKERS_SELECT_CLAUSE + "FROM gdms_marker "
		+ "WHERE marker_id IN (:markerIdList) ";

	public static final String GET_SNP_MARKERS_BY_HAPLOTYPE = MarkerDAO.GET_MARKERS_SELECT_CLAUSE
		+ "FROM (gdms_marker gdms INNER JOIN gdms_track_markers track ON(track.marker_id = gdms.marker_id)) "
		+ "INNER JOIN gdms_track_data tdata ON (tdata.track_id = track.track_id) "
		+ "WHERE track_name = (:trackName) and gdms.marker_type = 'SNP'";

	public static final String GET_MARKERS_BY_TYPE = MarkerDAO.GET_MARKERS_SELECT_CLAUSE + "FROM gdms_marker "
		+ "WHERE marker_type = :markerType ";

	public static final String COUNT_MARKERS_BY_IDS = "SELECT COUNT(marker_id)  " + "FROM gdms_marker "
		+ "WHERE marker_id IN (:markerIdList) ";

	public static final String GET_ID_BY_NAME = "SELECT marker_id " + "FROM gdms_marker " + "WHERE marker_name = :markerName "
		+ "LIMIT 0,1";

	public static final String GET_MARKERS_BY_MAP_ID =
		"SELECT marker.marker_id  " + ", CONCAT(marker.marker_type, '') " + ", CONCAT(marker.marker_name, '') "
			+ ", CONCAT(marker.species, '') " + ", marker.db_accession_id " + ", marker.reference " + ", CONCAT(marker.genotype, '') "
			+ ", marker.ploidy  " + ", marker.primer_id  " + ", marker.remarks  " + ", marker.assay_type " + ", marker.motif  "
			+ ", marker.forward_primer  " + ", marker.reverse_primer  " + ", marker.product_size  " + ", marker.annealing_temp "
			+ ", marker.amplification "
			+ "FROM (gdms_marker marker INNER JOIN gdms_markers_onmap onmap on onmap.marker_id = marker.marker_id)"
			+ " WHERE onmap.map_id = :map_id";

	public MarkerDAO(final Session session) {
		super(session);
	}

	/**
	 * Gets the ids by names.
	 *
	 * @param names     the names
	 * @param start     the start
	 * @param numOfRows the num of rows
	 * @return the ids by names
	 * @ the MiddlewareQueryException
	 */
	public List<Integer> getIdsByNames(final List<String> names, final int start, final int numOfRows) {

		if (names == null || names.isEmpty()) {
			return new ArrayList<>();
		}

		try {
			final SQLQuery query = this.getSession().createSQLQuery(MarkerDAO.GET_IDS_BY_NAMES);
			query.setParameterList("markerNameList", names);
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);
			return query.list();
		} catch (final HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException("Error with getIdsByNames(names=" + names + ") query from Marker: " + e.getMessage(), e);
		}
	}

	public List<Marker> getByNames(final List<String> names, final int start, final int numOfRows) {
		List<Marker> toReturn = new ArrayList<>();
		if (names == null || names.isEmpty()) {
			return toReturn;
		}

		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.in("markerName", names));
			toReturn = criteria.list();
		} catch (final HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException("Error with getIdsByNames(names=" + names + ") query from Marker: " + e.getMessage(), e);
		}
		return toReturn;
	}

	public List<Marker> getByType(final String markerType) {
		List<Marker> returnVal = new ArrayList<>();

		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("markerType", markerType));
			returnVal = criteria.list();
		} catch (final HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException("Error with getByType(type=" + markerType + ") query from Marker: " + e.getMessage(), e);
		}

		return returnVal;
	}

	/**
	 * Gets the marker_id of the first occurence of the marker_name
	 *
	 * @param names
	 * @return Map of markerId-markerName pairs
	 * @
	 */
	public Map<Integer, String> getFirstMarkerIdByMarkerName(final List<String> names) {
		final Map<Integer, String> toReturn = new HashMap<>();
		if (names == null || names.isEmpty()) {
			return toReturn;
		}

		try {
			final SQLQuery query = this.getSession().createSQLQuery(MarkerDAO.GET_ID_AND_NAME_BY_NAMES);
			query.setParameterList("markerNameList", names);
			final List<Object> results = query.list();

			for (final Object o : results) {
				final Object[] result = (Object[]) o;
				if (result != null) {
					final Integer id = (Integer) result[0];
					final String name = (String) result[1];

					// Add to map if it doesn't contain the name yet. Ignore the case.
					if (!toReturn.containsValue(name.toUpperCase())) {
						toReturn.put(id, name.toUpperCase());
					}
				}
			}

		} catch (final HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException(
				"Error with getIdAndNameByNames(names=" + names + ") query from Marker: " + e.getMessage(), e);
		}
		return toReturn;

	}

	/**
	 * Gets the marker type by marker ids.
	 *
	 * @param markerIds the marker ids
	 * @return the marker type by marker ids
	 * @ the MiddlewareQueryException
	 */
	public List<String> getMarkerTypeByMarkerIds(final List<Integer> markerIds) {

		if (markerIds == null || markerIds.isEmpty()) {
			return new ArrayList<>();
		}

		try {

			final SQLQuery query = this.getSession().createSQLQuery(MarkerDAO.GET_MARKER_TYPE_BY_MARKER_IDS);
			query.setParameterList("markerIdList", markerIds);
			return query.list();

		} catch (final HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException(
				"Error with getMarkerTypeByMarkerIds(markerIds=" + markerIds + ") query from Marker: " + e.getMessage(), e);
		}
	}

	public Map<Integer, String> getMarkerTypeMapByIds(final List<Integer> markerIds) {
		final Map<Integer, String> markerTypes = new HashMap<>();

		if (markerIds == null || markerIds.isEmpty()) {
			return markerTypes;
		}

		try {

			final StringBuilder sql =
				new StringBuilder().append("SELECT DISTINCT marker_id, CONCAT(marker_type, '') ").append("FROM gdms_marker ")
					.append("WHERE marker_id IN (:markerIdList)");
			final SQLQuery query = this.getSession().createSQLQuery(sql.toString());
			query.setParameterList("markerIdList", markerIds);

			final List<Object> results = query.list();

			for (final Object o : results) {
				final Object[] result = (Object[]) o;
				if (result != null) {
					final Integer id = (Integer) result[0];
					final String type = (String) result[1];
					markerTypes.put(id, type);
				}
			}

		} catch (final HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException(
				"Error with getMarkerTypeByMarkerIds(markerIds=" + markerIds + ") query from Marker: " + e.getMessage(), e);
		}

		return markerTypes;
	}

	/**
	 * Gets the marker names by gids.
	 * <p/>
	 * - Searches the allele_values, char_values, mapping_pop_values tables for the existence of gids. - Gets marker ids from allele_values,
	 * char_values, mapping_pop_values by gids - Gets marker name from marker table by marker ids - Returns marker names matching the gids
	 *
	 * @param gIds the g ids
	 * @return the marker names by g ids
	 * @ the MiddlewareQueryException
	 */
	public List<MarkerNameElement> getMarkerNamesByGIds(final List<Integer> gIds) {
		final List<MarkerNameElement> dataValues = new ArrayList<>();

		if (gIds == null || gIds.isEmpty()) {
			return dataValues;
		}

		try {
			// Search the allele_values, char_values, mapping_pop_values tables for the existence of gids.
			// by getting alleleCount, charCount and mappingCount

			SQLQuery query = this.getSession().createSQLQuery(AlleleValuesDAO.GET_ALLELE_COUNT_BY_GID);
			query.setParameterList("gIdList", gIds);
			final BigInteger alleleCount = (BigInteger) query.uniqueResult();

			query = this.getSession().createSQLQuery(CharValuesDAO.GET_CHAR_COUNT_BY_GID);
			query.setParameterList("gIdList", gIds);
			final BigInteger charCount = (BigInteger) query.uniqueResult();

			query = this.getSession().createSQLQuery(MappingPopValuesDAO.GET_MAPPING_COUNT_BY_GID);
			query.setParameterList("gIdList", gIds);
			final BigInteger mappingCount = (BigInteger) query.uniqueResult();

			// Retrieves markers that are being genotyped
			if (alleleCount.intValue() > 0) {
				query = this.getSession().createSQLQuery(MarkerDAO.GET_ALLELE_MARKER_NAMES_BY_GID);
				query.setParameterList("gIdList", gIds);
				final List results = query.list();
				dataValues.addAll(this.createMarkerNameElementList(results));
			}

			if (charCount.intValue() > 0) {
				query = this.getSession().createSQLQuery(MarkerDAO.GET_CHAR_MARKER_NAMES_BY_GID);
				query.setParameterList("gIdList", gIds);
				final List results = query.list();
				dataValues.addAll(this.createMarkerNameElementList(results));
			}

			if (mappingCount.intValue() > 0) {
				query = this.getSession().createSQLQuery(MarkerDAO.GET_MAPPING_MARKER_NAMES_BY_GID);
				query.setParameterList("gIdList", gIds);
				final List results = query.list();
				dataValues.addAll(this.createMarkerNameElementList(results));
			}

			return dataValues;

		} catch (final HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException(
				"Error with getMarkerNamesByGIds(gIds=" + gIds + ") query from Marker: " + e.getMessage(), e);
		}
	}

	private List<MarkerNameElement> createMarkerNameElementList(final List<Object> list) {
		final List<MarkerNameElement> dataValues = new ArrayList<>();
		for (final Object o : list) {
			final Object[] result = (Object[]) o;
			if (result != null) {
				final Integer gId = (Integer) result[0];
				final Integer markerId = (Integer) result[1];
				final String markerName = (String) result[2];
				final MarkerNameElement element = new MarkerNameElement(gId, markerId, markerName);
				dataValues.add(element);
			}
		}
		return dataValues;
	}

	/**
	 * Gets the GermplasmMarkerElement items from the given list. Converts the result of SQL return values to GermplasmMarkerElement list.
	 * Used by getGermplasmNamesByMarkerNames().
	 *
	 * @param results the results
	 * @return the GermplasmMarkerElement items extracted from the list
	 */
	private List<GermplasmMarkerElement> getGermplasmMarkerElementsFromList(final List results) {
		final ArrayList<GermplasmMarkerElement> dataValues = new ArrayList<>();
		String prevGermplasmName = null;
		ArrayList<String> markerNameList = new ArrayList<>();

		for (final Object o : results) {
			final Object[] result = (Object[]) o;
			if (result != null) {
				final String germplasmName = (String) result[0];
				final String markerName = (String) result[1];

				if (prevGermplasmName == null) {
					prevGermplasmName = germplasmName;
				}

				if (germplasmName.equals(prevGermplasmName)) {
					markerNameList.add(markerName);
				} else {
					dataValues.add(new GermplasmMarkerElement(prevGermplasmName, markerNameList));
					prevGermplasmName = germplasmName;
					markerNameList = new ArrayList<>();
					markerNameList.add(markerName);
				}

				if (results.indexOf(result) == results.size() - 1) {
					dataValues.add(new GermplasmMarkerElement(germplasmName, markerNameList));
				}

			}
		}

		return dataValues;
	}

	/**
	 * Gets the germplasm names by marker names.
	 *
	 * @param markerNames the marker names
	 * @return the germplasm names by marker names
	 * @ the MiddlewareQueryException
	 */
	public List<GermplasmMarkerElement> getGermplasmNamesByMarkerNames(final List<String> markerNames) {

		final ArrayList<GermplasmMarkerElement> dataValues = new ArrayList<>();

		if (markerNames == null || markerNames.isEmpty()) {
			return dataValues;
		}

		// Get marker_ids by marker_names
		final List<Integer> markerIds = this.getIdsByNames(markerNames, 0, Long.valueOf(this.countAll()).intValue());

		if (markerIds.isEmpty()) {
			return dataValues;
		}

		try {
			// Search the allele_values, char_values, mapping_pop_values tables for the existence of marker_ids.
			// by getting alleleCount, charCount and mappingCount

			SQLQuery query = this.getSession().createSQLQuery(AlleleValuesDAO.GET_ALLELE_COUNT_BY_MARKER_ID);
			query.setParameterList("markerIdList", markerIds);
			final BigInteger alleleCount = (BigInteger) query.uniqueResult();

			query = this.getSession().createSQLQuery(CharValuesDAO.GET_CHAR_COUNT_BY_MARKER_ID);
			query.setParameterList("markerIdList", markerIds);
			final BigInteger charCount = (BigInteger) query.uniqueResult();

			query = this.getSession().createSQLQuery(MappingPopValuesDAO.GET_MAPPING_COUNT_BY_MARKER_ID);
			query.setParameterList("markerIdList", markerIds);
			final BigInteger mappingCount = (BigInteger) query.uniqueResult();

			// Get marker name, germplasm name from allele_values given the marker names
			if (alleleCount.intValue() > 0) {
				query = this.getSession().createSQLQuery(AlleleValuesDAO.GET_ALLELE_GERMPLASM_NAME_AND_MARKER_NAME_BY_MARKER_NAMES);
				query.setParameterList("markerNameList", markerNames);
				final List results = query.list();
				dataValues.addAll(this.getGermplasmMarkerElementsFromList(results));
			}

			// Get marker name, germplasm name from char_values given the marker names
			if (charCount.intValue() > 0) {
				query = this.getSession().createSQLQuery(CharValuesDAO.GET_CHAR_GERMPLASM_NAME_AND_MARKER_NAME_BY_MARKER_NAMES);
				query.setParameterList("markerNameList", markerNames);
				final List results = query.list();
				dataValues.addAll(this.getGermplasmMarkerElementsFromList(results));
			}

			// Get marker name, germplasm name from mapping_pop_values given the marker names
			if (mappingCount.intValue() > 0) {
				query = this.getSession().createSQLQuery(MappingPopValuesDAO.GET_MAPPING_GERMPLASM_NAME_AND_MARKER_NAME_BY_MARKER_NAMES);
				query.setParameterList("markerNameList", markerNames);
				final List results = query.list();
				dataValues.addAll(this.getGermplasmMarkerElementsFromList(results));
			}

			return dataValues;

		} catch (final HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException(
				"Error with getGermplasmNamesByMarkerNames(markerNames=" + markerNames + ") query from Marker: " + e.getMessage(), e);
		}
	}

	/**
	 * Gets the allelic value elements from list.
	 *
	 * @param results the results
	 * @return the allelic value elements from list
	 */
	private List<AllelicValueElement> getAllelicValueElementsFromList(final List results) {
		final List<AllelicValueElement> values = new ArrayList<>();

		for (final Object o : results) {
			final Object[] result = (Object[]) o;
			if (result != null) {
				final Integer gid = (Integer) result[0];
				final Integer markerId = (Integer) result[1];
				final String data = (String) result[2];
				final Integer peakHeight = (Integer) result[3];
				final Integer markerSampleId = (Integer) result[4];
				final Integer accSampleId = (Integer) result[5];
				final AllelicValueElement allelicValueElement =
					new AllelicValueElement(gid, data, markerId, null, peakHeight, markerSampleId, accSampleId);
				values.add(allelicValueElement);
			}
		}

		return values;
	}

	private List<AllelicValueElement> getAllelicValueElementsFromListLocal(final List results) {
		final List<AllelicValueElement> values = new ArrayList<>();

		for (final Object o : results) {
			final Object[] result = (Object[]) o;
			if (result != null) {
				final Integer gid = (Integer) result[0];
				final Integer markerId = (Integer) result[1];
				final String data = (String) result[2];
				final Integer peakHeight = (Integer) result[3];
				final AllelicValueElement allelicValueElement = new AllelicValueElement(gid, data, null, peakHeight);
				allelicValueElement.setMarkerId(markerId);
				values.add(allelicValueElement);
			}
		}

		return values;
	}

	/**
	 * Gets the allelic values by gids and marker names.
	 *
	 * @param gids        the gids
	 * @param markerNames the marker names
	 * @return the allelic values by gids and marker names
	 * @ the MiddlewareQueryException
	 */
	public List<AllelicValueElement> getAllelicValuesByGidsAndMarkerNames(final List<Integer> gids, final List<String> markerNames) {

		final List<AllelicValueElement> allelicValues = new ArrayList<>();

		if (gids == null || gids.isEmpty() || markerNames == null || markerNames.isEmpty()) {
			return allelicValues;
		}

		// Get marker_ids by marker_names
		final List<Integer> markerIds = this.getIdsByNames(markerNames, 0, Integer.MAX_VALUE);

		if (markerIds == null || markerIds.isEmpty()) {
			return allelicValues;
		}

		return this.getAllelicValuesByGidsAndMarkerIds(gids, markerIds);
	}

	/**
	 * Gets the allelic values by gids and marker ids.
	 *
	 * @param gids      the gids
	 * @param markerIds the marker ids
	 * @return the allelic values by gids and marker ids
	 * @ the MiddlewareQueryException
	 */
	public List<AllelicValueElement> getAllelicValuesByGidsAndMarkerIds(final List<Integer> gids, final List<Integer> markerIds) {

		final List<AllelicValueElement> allelicValues = new ArrayList<>();

		if (gids == null || gids.isEmpty() || markerIds == null || markerIds.isEmpty()) {
			return allelicValues;
		}

		try {

			// retrieve allelic values from allele_values
			SQLQuery query = this.getSession().createSQLQuery(AlleleValuesDAO.GET_ALLELIC_VALUES_BY_GIDS_AND_MARKER_IDS);
			query.setParameterList("gidList", gids);
			query.setParameterList("markerIdList", markerIds);
			List results = query.list();
			allelicValues.addAll(this.getAllelicValueElementsFromList(results));

			// retrieve allelic values from char_values
			query = this.getSession().createSQLQuery(CharValuesDAO.GET_ALLELIC_VALUES_BY_GIDS_AND_MARKER_IDS);
			query.setParameterList("gidList", gids);
			query.setParameterList("markerIdList", markerIds);
			results = query.list();
			allelicValues.addAll(this.getAllelicValueElementsFromList(results));

			// retrieve allelic values from mapping_pop_values
			query = this.getSession().createSQLQuery(MappingPopValuesDAO.GET_ALLELIC_VALUES_BY_GIDS_AND_MARKER_IDS);
			query.setParameterList("gidList", gids);
			query.setParameterList("markerIdList", markerIds);
			results = query.list();
			allelicValues.addAll(this.getAllelicValueElementsFromList(results));

			return allelicValues;
		} catch (final HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException("Error with getAllelicValuesByGidsAndMarkerIds(gIds=" + gids + ", markerIds=" + markerIds
				+ ")  query from Marker: " + e.getMessage(), e);
		}
	}

	public List<AllelicValueElement> getAllelicValuesFromLocal(final List<Integer> gids) {

		final List<AllelicValueElement> allelicValues = new ArrayList<>();

		if (gids == null || gids.isEmpty()) {
			return allelicValues;
		}

		try {

			// retrieve allelic values from allele_values
			SQLQuery query = this.getSession().createSQLQuery(AlleleValuesDAO.GET_ALLELIC_VALUES_BY_GID_LOCAL);
			query.setParameterList("gidList", gids);
			List results = query.list();
			allelicValues.addAll(this.getAllelicValueElementsFromListLocal(results));

			// retrieve allelic values from char_values
			query = this.getSession().createSQLQuery(CharValuesDAO.GET_ALLELIC_VALUES_BY_GID_LOCAL);
			query.setParameterList("gidList", gids);
			results = query.list();
			allelicValues.addAll(this.getAllelicValueElementsFromListLocal(results));

			// retrieve allelic values from mapping_pop_values
			query = this.getSession().createSQLQuery(MappingPopValuesDAO.GET_ALLELIC_VALUES_BY_GID_LOCAL);
			query.setParameterList("gidList", gids);
			results = query.list();
			allelicValues.addAll(this.getAllelicValueElementsFromListLocal(results));

			return allelicValues;
		} catch (final HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException("Error with getAllelicValuesFromLocal() query from Marker: " + e.getMessage(), e);
		}
	}

	/**
	 * Get list of marker names by marker ids.
	 *
	 * @param ids the ids
	 * @return the names by ids
	 * @ the MiddlewareQueryException
	 */
	public List<MarkerIdMarkerNameElement> getNamesByIds(final List<Integer> ids) {

		if (ids == null || ids.isEmpty()) {
			return new ArrayList<>();
		}

		try {
			final SQLQuery query = this.getSession().createSQLQuery(MarkerDAO.GET_NAMES_BY_IDS);
			query.setParameterList("markerIdList", ids);

			final List<MarkerIdMarkerNameElement> dataValues = new ArrayList<>();
			final List<Object> results = query.list();

			for (final Object o : results) {
				final Object[] result = (Object[]) o;
				if (result != null) {
					final Integer id = (Integer) result[0];
					final String name = (String) result[1];
					final MarkerIdMarkerNameElement elem = new MarkerIdMarkerNameElement(id, name);
					dataValues.add(elem);
				}
			}

			return dataValues;
		} catch (final HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException("Error with getNamesByIds(markerIds" + ids + ") query from Marker: " + e.getMessage(), e);
		}
	}

	public Map<Integer, String> getNamesByIdsMap(final List<Integer> ids) {
		final Map<Integer, String> dataValues = new HashMap<>();
		if (ids == null || ids.isEmpty()) {
			return dataValues;
		}

		try {
			final SQLQuery query = this.getSession().createSQLQuery(MarkerDAO.GET_NAMES_BY_IDS);
			query.setParameterList("markerIdList", ids);

			final List<Object> results = query.list();

			for (final Object o : results) {
				final Object[] result = (Object[]) o;
				if (result != null) {
					final Integer id = (Integer) result[0];
					final String name = (String) result[1];
					dataValues.put(id, name);
				}
			}

		} catch (final HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException("Error with getNamesByIdsMap(markerIds" + ids + ") query from Marker: " + e.getMessage(), e);
		}
		return dataValues;
	}

	public String getNameById(final Integer markerId) {
		String name = null;

		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("markerId", markerId));
			final Marker marker = (Marker) criteria.uniqueResult();

			if (marker != null) {
				name = marker.getMarkerName();
			}
		} catch (final HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException("Error with getNameById query from Marker: " + e.getMessage(), e);
		}

		return name;

	}

	/**
	 * Get all marker types.
	 *
	 * @param start     the start
	 * @param numOfRows the num of rows
	 * @return the all marker types
	 * @ the MiddlewareQueryException
	 */
	public List<String> getAllMarkerTypes(final int start, final int numOfRows) {
		try {
			final SQLQuery query = this.getSession().createSQLQuery(MarkerDAO.GET_ALL_MARKER_TYPES);
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);

			final List<String> markerTypes = query.list();

			return markerTypes;

		} catch (final HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException("Error with getAllMarkerTypes() query from Marker: " + e.getMessage(), e);
		}
	}

	/**
	 * Count all marker types.
	 *
	 * @return the long
	 * @ the MiddlewareQueryException
	 */
	public long countAllMarkerTypes() {
		try {
			final SQLQuery query = this.getSession().createSQLQuery(MarkerDAO.COUNT_ALL_MARKER_TYPES);
			final BigInteger result = (BigInteger) query.uniqueResult();
			if (result != null) {
				return result.longValue();
			} else {
				return 0L;
			}
		} catch (final HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException("Error with countAllMarkerTypes() query from Marker: " + e.getMessage(), e);
		}
	}

	/**
	 * Gets the all marker names by marker type.
	 *
	 * @param markerType the marker type
	 * @param start      the start
	 * @param numOfRows  the num of rows
	 * @return the all marker names by marker type
	 * @ the MiddlewareQueryException
	 */
	public List<String> getMarkerNamesByMarkerType(final String markerType, final int start, final int numOfRows) {

		try {
			final SQLQuery query = this.getSession().createSQLQuery(MarkerDAO.GET_NAMES_BY_TYPE);
			query.setParameter("markerType", markerType);
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);

			final List<String> markerNames = query.list();

			return markerNames;
		} catch (final HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException(
				"Error with getMarkerNamesByMarkerType(markerType=" + markerType + ") query from Marker: " + e.getMessage(), e);
		}
	}

	/**
	 * Count marker names by marker type.
	 *
	 * @param markerType the marker type
	 * @return the long
	 * @ the MiddlewareQueryException
	 */
	public long countMarkerNamesByMarkerType(final String markerType) {
		try {
			final SQLQuery query = this.getSession().createSQLQuery(MarkerDAO.COUNT_MARKER_NAMES_BY_MARKER_TYPE);
			query.setParameter("markerType", markerType);
			final BigInteger result = (BigInteger) query.uniqueResult();

			if (result != null) {
				return result.longValue();
			}
			return 0;
		} catch (final HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException(
				"Error with countMarkerNamesByMarkerType(markerType=" + markerType + ") query from Marker: " + e.getMessage(), e);
		}
	}

	/**
	 * Gets the all db accession ids.
	 *
	 * @param start     the start
	 * @param numOfRows the num of rows
	 * @return all non-empty db accession ids
	 * @
	 */
	public List<String> getAllDbAccessionIds(final int start, final int numOfRows) {
		try {
			final SQLQuery query = this.getSession().createSQLQuery(MarkerDAO.GET_ALL_DB_ACCESSION_IDS);
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);
			return query.list();
		} catch (final HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException("Error with getAllDbAccessionIds() query from Marker: " + e.getMessage(), e);
		}
	}

	/**
	 * Count all db accession ids.
	 *
	 * @return the number of distinct db accession ids
	 * @ the MiddlewareQueryException
	 */
	public long countAllDbAccessionIds() {
		try {
			final SQLQuery query = this.getSession().createSQLQuery(MarkerDAO.COUNT_ALL_DB_ACCESSION_IDS);
			final BigInteger result = (BigInteger) query.uniqueResult();
			if (result != null) {
				return result.longValue();
			} else {
				return 0L;
			}
		} catch (final HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException("Error with countAllDbAccessionIds() query from Marker: " + e.getMessage(), e);
		}
	}

	public List<Marker> getMarkersByIds(final List<Integer> markerIds) {
		return this.getMarkersByIds(markerIds, 0, Integer.MAX_VALUE);
	}

	public List<Marker> getMarkersByIds(final List<Integer> markerIds, final int start, final int numOfRows) {
		if (markerIds == null || markerIds.isEmpty()) {
			return new ArrayList<>();
		}

		try {
			final SQLQuery query = this.getSession().createSQLQuery(MarkerDAO.GET_MARKERS_BY_IDS);
			query.setParameterList("markerIdList", markerIds);
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);
			final List results = query.list();

			final List<Marker> dataValues = new ArrayList<>();
			for (final Object o : results) {
				final Object[] result = (Object[]) o;
				if (result != null) {
					dataValues.add(this.convertToMarker(result));
				}
			}
			return dataValues;
		} catch (final HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException("Error with getMarkersByIds() query from Marker: " + e.getMessage(), e);
		}
	}

	// GCP-7874
	public List<Marker> getSNPMarkersByHaplotype(final String haplotype) {
		if (StringUtils.isEmpty(haplotype)) {
			return new ArrayList<>();
		}

		try {
			final SQLQuery query = this.getSession().createSQLQuery(MarkerDAO.GET_SNP_MARKERS_BY_HAPLOTYPE);
			query.setParameter("trackName", haplotype);
			final List results = query.list();

			final List<Marker> dataValues = new ArrayList<>();
			for (final Object o : results) {
				final Object[] result = (Object[]) o;
				if (result != null) {
					dataValues.add(this.convertToMarker(result));
				}
			}
			return dataValues;
		} catch (final HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException("Error with getSNPMarkersByHaplotype() query from Marker: " + e.getMessage(), e);
		}
	}

	public List<Integer> getMarkerIDsByHaplotype(final String haplotype) {
		if (StringUtils.isEmpty(haplotype)) {
			return new ArrayList<>();
		}

		try {
			final SQLQuery query = this.getSession().createSQLQuery(MarkerDAO.GET_MARKER_IDS_BY_HAPLOTYPE);
			query.setParameter("trackName", haplotype);
			return query.list();
		} catch (final HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException("Error with getSNPMarkerIDsByHaplotype() query from Marker: " + e.getMessage(), e);
		}
	}

	public List<Marker> getMarkersByIdsAndType(final List<Integer> markerIds, final String markerType) {
		final List<Marker> dataValues = new ArrayList<>();
		if (StringUtils.isEmpty(markerType)) {
			return dataValues;
		}

		try {
			final List<Marker> markersByIds = this.getMarkersByIds(markerIds, 0, Integer.MAX_VALUE);

			for (final Marker marker : markersByIds) {
				if (marker.getMarkerType().equalsIgnoreCase(markerType)) {
					dataValues.add(marker);
				}
			}
		} catch (final HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException("Error with getMarkersByIdsAndType() query from Marker: " + e.getMessage(), e);
		}

		return dataValues;
	}

	public List<Marker> getMarkersByType(final String markerType) {
		final List<Marker> dataValues = new ArrayList<>();
		if (StringUtils.isEmpty(markerType)) {
			return dataValues;
		}

		try {
			final SQLQuery query = this.getSession().createSQLQuery(MarkerDAO.GET_MARKERS_BY_TYPE);
			query.setParameter("markerType", markerType);
			final List results = query.list();
			for (final Object o : results) {
				final Object[] result = (Object[]) o;
				if (result != null) {
					dataValues.add(this.convertToMarker(result));
				}
			}
			return dataValues;
		} catch (final HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException("Error with getMarkersByIds() query from Marker: " + e.getMessage(), e);
		}
	}

	protected Marker convertToMarker(final Object[] result) {

		final Integer markerId = (Integer) result[0];
		final String markerType = (String) result[1];
		final String markerName = (String) result[2];
		final String species = (String) result[3];
		final String dbAccessionId = (String) result[4];
		final String reference = (String) result[5];
		final String genotype = (String) result[6];
		final String ploidy = (String) result[7];
		final String primerId = (String) result[8];
		final String remarks = (String) result[9];
		final String assayType = (String) result[10];
		final String motif = (String) result[11];
		final String forwardPrimer = (String) result[12];
		final String reversePrimer = (String) result[13];
		final String productSize = (String) result[14];
		final Float annealingTemp = (Float) result[15];
		final String amplification = (String) result[16];

		final Marker element =
			new Marker(markerId, markerType, markerName, species, dbAccessionId, reference, genotype, ploidy, primerId, remarks,
				assayType, motif, forwardPrimer, reversePrimer, productSize, annealingTemp, amplification);

		return element;
	}

	public long countMarkersByIds(final List<Integer> markerIds) {
		if (markerIds == null || markerIds.isEmpty()) {
			return 0;
		}
		try {
			final SQLQuery query = this.getSession().createSQLQuery(MarkerDAO.COUNT_MARKERS_BY_IDS);
			query.setParameterList("markerIdList", markerIds);
			final BigInteger result = (BigInteger) query.uniqueResult();
			if (result != null) {
				return result.longValue();
			} else {
				return 0L;
			}
		} catch (final HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException("Error with countMarkersByIds() query from Marker: " + e.getMessage(), e);
		}
	}

	public Set<Integer> getMarkerIDsByMapIDAndLinkageBetweenStartPosition(
		final int mapID, final String linkageGroup, final double startPos, final double endPos,
		final int start, final int numOfRows) {
		try {

			final SQLQuery query;

			query = this.getSession().createSQLQuery(MarkerDAO.GET_MARKER_IDS_BY_MAP_ID_AND_LINKAGE_BETWEEN_START_POSITION);
			query.setParameter("mapId", mapID);
			query.setParameter("linkageGroup", linkageGroup);
			query.setParameter("startPosition", startPos);
			query.setParameter("endPosition", endPos);
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);
			final Set<Integer> markerIDSet = new TreeSet<>(query.list());

			return markerIDSet;

		} catch (final HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException(
				"Error with getMarkerIdsByMapIDAndLinkageBetweenStartPosition(mapID=" + mapID + ", linkageGroup="
					+ linkageGroup + ", start=" + start + ", numOfRows=" + numOfRows + ") query from MarkerOnMap: " + e.getMessage(), e);
		}
	}

	public long countMarkerIDsByMapIDAndLinkageBetweenStartPosition(
		final int mapID, final String linkageGroup, final double startPos, final double endPos) {
		try {

			final SQLQuery query;

			query = this.getSession().createSQLQuery(MarkerDAO.COUNT_MARKER_IDS_BY_MAP_ID_AND_LINKAGE_BETWEEN_START_POSITION);
			query.setParameter("map_id", mapID);
			query.setParameter("linkage_group", linkageGroup);
			query.setParameter("start_position", startPos);
			query.setParameter("end_position", endPos);
			final BigInteger result = (BigInteger) query.uniqueResult();
			if (result != null) {
				return result.longValue();
			} else {
				return 0;
			}

		} catch (final HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException(
				"Error with countMarkerIdsByMapIDAndLinkageBetweenStartPosition(mapID=" + mapID + ", linkageGroup="
					+ linkageGroup + ") query from Marker: " + e.getMessage(), e);
		}
	}

	public Integer getIdByName(final String name) {
		try {
			final SQLQuery query = this.getSession().createSQLQuery(MarkerDAO.GET_ID_BY_NAME);
			query.setParameter("markerName", name);
			return (Integer) query.uniqueResult();

		} catch (final HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException("Error with getIdByName(" + name + "): " + e.getMessage(), e);
		}
	}

	public List<String> getMarkerNamesByIds(final List<Integer> markerIds) {
		if (markerIds == null || markerIds.isEmpty()) {
			return new ArrayList<>();
		}

		try {
			final StringBuilder sql =
				new StringBuilder().append("SELECT CONCAT(marker_name, '') FROM gdms_marker WHERE marker_id IN (:markerIds) ");

			final SQLQuery query = this.getSession().createSQLQuery(sql.toString());
			query.setParameterList("markerIds", markerIds);
			return query.list();
		} catch (final HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException("Error with getMarkerNamesByIds() query from Marker: " + e.getMessage(), e);
		}
	}

	public List<Marker> getMarkersByMapId(final Integer mapId) {
		final List<Marker> dataValues = new ArrayList<>();
		if (null == mapId) {
			return dataValues;
		}

		try {

			final SQLQuery query = this.getSession().createSQLQuery(MarkerDAO.GET_MARKERS_BY_MAP_ID);
			query.setParameter("map_id", mapId);
			final List results = query.list();
			for (final Object o : results) {
				final Object[] result = (Object[]) o;
				if (result != null) {
					dataValues.add(this.convertToMarker(result));
				}
			}
			return dataValues;
		} catch (final HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException("Error with getMarkersByMapId() query from Marker: " + e.getMessage(), e);
		}
	}
}
