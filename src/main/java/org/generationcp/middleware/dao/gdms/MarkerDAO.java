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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link Marker}.
 * <p/>
 * <b>Authors</b>: Mark Agarrado <br>
 * <b>File Created</b>: Jul 10, 2012
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class MarkerDAO extends GenericDAO<Marker, Integer> {

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

	/**
	 * Gets the ids by names.
	 *
	 * @param names the names
	 * @param start the start
	 * @param numOfRows the num of rows
	 * @return the ids by names
	 * @throws MiddlewareQueryException the MiddlewareQueryException
	 */
	public List<Integer> getIdsByNames(final List<String> names, final int start, final int numOfRows) throws MiddlewareQueryException {

		if (names == null || names.isEmpty()) {
			return new ArrayList<Integer>();
		}

		try {
			SQLQuery query = this.getSession().createSQLQuery(MarkerDAO.GET_IDS_BY_NAMES);
			query.setParameterList("markerNameList", names);
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);
			return query.list();
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getIdsByNames(names=" + names + ") query from Marker: " + e.getMessage(), e);
		}
		return new ArrayList<Integer>();
	}

	public List<Marker> getByNames(final List<String> names, final int start, final int numOfRows) throws MiddlewareQueryException {
		List<Marker> toReturn = new ArrayList<Marker>();
		if (names == null || names.isEmpty()) {
			return toReturn;
		}

		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.in("markerName", names));
			toReturn = criteria.list();
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getIdsByNames(names=" + names + ") query from Marker: " + e.getMessage(), e);
		}
		return toReturn;
	}

	public List<Marker> getByType(final String markerType) throws MiddlewareQueryException {
		List<Marker> returnVal = new ArrayList<Marker>();

		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("markerType", markerType));
			returnVal = criteria.list();
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getByType(type=" + markerType + ") query from Marker: " + e.getMessage(), e);
		}

		return returnVal;
	}

	/**
	 * Gets the marker_id of the first occurence of the marker_name
	 *
	 * @param names
	 * @return Map of markerId-markerName pairs
	 * @throws MiddlewareQueryException
	 */
	public Map<Integer, String> getFirstMarkerIdByMarkerName(final List<String> names) throws MiddlewareQueryException {
		Map<Integer, String> toReturn = new HashMap<Integer, String>();
		if (names == null || names.isEmpty()) {
			return toReturn;
		}

		try {
			SQLQuery query = this.getSession().createSQLQuery(MarkerDAO.GET_ID_AND_NAME_BY_NAMES);
			query.setParameterList("markerNameList", names);
			List<Object> results = query.list();

			for (final Object o : results) {
				Object[] result = (Object[]) o;
				if (result != null) {
					Integer id = (Integer) result[0];
					String name = (String) result[1];

					// Add to map if it doesn't contain the name yet. Ignore the case.
					if (!toReturn.containsValue(name.toUpperCase())) {
						toReturn.put(id, name.toUpperCase());
					}
				}
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error with getIdAndNameByNames(names=" + names + ") query from Marker: " + e.getMessage(), e);
		}
		return toReturn;

	}

	/**
	 * Gets the marker type by marker ids.
	 *
	 * @param markerIds the marker ids
	 * @return the marker type by marker ids
	 * @throws MiddlewareQueryException the MiddlewareQueryException
	 */
	public List<String> getMarkerTypeByMarkerIds(final List<Integer> markerIds) throws MiddlewareQueryException {

		if (markerIds == null || markerIds.isEmpty()) {
			return new ArrayList<String>();
		}

		try {

			SQLQuery query = this.getSession().createSQLQuery(MarkerDAO.GET_MARKER_TYPE_BY_MARKER_IDS);
			query.setParameterList("markerIdList", markerIds);
			return query.list();

		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error with getMarkerTypeByMarkerIds(markerIds=" + markerIds + ") query from Marker: " + e.getMessage(), e);
		}
		return new ArrayList<String>();
	}

	public Map<Integer, String> getMarkerTypeMapByIds(final List<Integer> markerIds) throws MiddlewareQueryException {
		Map<Integer, String> markerTypes = new HashMap<Integer, String>();

		if (markerIds == null || markerIds.isEmpty()) {
			return markerTypes;
		}

		try {

			StringBuffer sql =
					new StringBuffer().append("SELECT DISTINCT marker_id, CONCAT(marker_type, '') ").append("FROM gdms_marker ")
							.append("WHERE marker_id IN (:markerIdList)");
			SQLQuery query = this.getSession().createSQLQuery(sql.toString());
			query.setParameterList("markerIdList", markerIds);

			List<Object> results = query.list();

			for (final Object o : results) {
				Object[] result = (Object[]) o;
				if (result != null) {
					Integer id = (Integer) result[0];
					String type = (String) result[1];
					markerTypes.put(id, type);
				}
			}

		} catch (HibernateException e) {
			this.logAndThrowException(
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
	 * @throws MiddlewareQueryException the MiddlewareQueryException
	 */
	public List<MarkerNameElement> getMarkerNamesByGIds(final List<Integer> gIds) throws MiddlewareQueryException {
		List<MarkerNameElement> dataValues = new ArrayList<MarkerNameElement>();

		if (gIds == null || gIds.isEmpty()) {
			return dataValues;
		}

		try {
			// Search the allele_values, char_values, mapping_pop_values tables for the existence of gids.
			// by getting alleleCount, charCount and mappingCount

			SQLQuery query = this.getSession().createSQLQuery(AlleleValuesDAO.GET_ALLELE_COUNT_BY_GID);
			query.setParameterList("gIdList", gIds);
			BigInteger alleleCount = (BigInteger) query.uniqueResult();

			query = this.getSession().createSQLQuery(CharValuesDAO.GET_CHAR_COUNT_BY_GID);
			query.setParameterList("gIdList", gIds);
			BigInteger charCount = (BigInteger) query.uniqueResult();

			query = this.getSession().createSQLQuery(MappingPopValuesDAO.GET_MAPPING_COUNT_BY_GID);
			query.setParameterList("gIdList", gIds);
			BigInteger mappingCount = (BigInteger) query.uniqueResult();

			// Retrieves markers that are being genotyped
			if (alleleCount.intValue() > 0) {
				query = this.getSession().createSQLQuery(MarkerDAO.GET_ALLELE_MARKER_NAMES_BY_GID);
				query.setParameterList("gIdList", gIds);
				List results = query.list();
				dataValues.addAll(this.createMarkerNameElementList(results));
			}

			if (charCount.intValue() > 0) {
				query = this.getSession().createSQLQuery(MarkerDAO.GET_CHAR_MARKER_NAMES_BY_GID);
				query.setParameterList("gIdList", gIds);
				List results = query.list();
				dataValues.addAll(this.createMarkerNameElementList(results));
			}

			if (mappingCount.intValue() > 0) {
				query = this.getSession().createSQLQuery(MarkerDAO.GET_MAPPING_MARKER_NAMES_BY_GID);
				query.setParameterList("gIdList", gIds);
				List results = query.list();
				dataValues.addAll(this.createMarkerNameElementList(results));
			}

			return dataValues;

		} catch (HibernateException e) {
			this.logAndThrowException("Error with getMarkerNamesByGIds(gIds=" + gIds + ") query from Marker: " + e.getMessage(), e);
		}
		return dataValues;
	}

	private List<MarkerNameElement> createMarkerNameElementList(final List<Object> list) {
		List<MarkerNameElement> dataValues = new ArrayList<MarkerNameElement>();
		for (final Object o : list) {
			Object[] result = (Object[]) o;
			if (result != null) {
				Integer gId = (Integer) result[0];
				Integer markerId = (Integer) result[1];
				String markerName = (String) result[2];
				MarkerNameElement element = new MarkerNameElement(gId, markerId, markerName);
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
		ArrayList<GermplasmMarkerElement> dataValues = new ArrayList<GermplasmMarkerElement>();
		String prevGermplasmName = null;
		ArrayList<String> markerNameList = new ArrayList<String>();

		for (final Object o : results) {
			Object[] result = (Object[]) o;
			if (result != null) {
				String germplasmName = (String) result[0];
				String markerName = (String) result[1];

				if (prevGermplasmName == null) {
					prevGermplasmName = germplasmName;
				}

				if (germplasmName.equals(prevGermplasmName)) {
					markerNameList.add(markerName);
				} else {
					dataValues.add(new GermplasmMarkerElement(prevGermplasmName, markerNameList));
					prevGermplasmName = germplasmName;
					markerNameList = new ArrayList<String>();
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
	 * @throws MiddlewareQueryException the MiddlewareQueryException
	 */
	public List<GermplasmMarkerElement> getGermplasmNamesByMarkerNames(final List<String> markerNames) throws MiddlewareQueryException {

		ArrayList<GermplasmMarkerElement> dataValues = new ArrayList<GermplasmMarkerElement>();

		if (markerNames == null || markerNames.isEmpty()) {
			return dataValues;
		}

		// Get marker_ids by marker_names
		List<Integer> markerIds = this.getIdsByNames(markerNames, 0, Long.valueOf(this.countAll()).intValue());

		if (markerIds.isEmpty()) {
			return dataValues;
		}

		try {
			// Search the allele_values, char_values, mapping_pop_values tables for the existence of marker_ids.
			// by getting alleleCount, charCount and mappingCount

			SQLQuery query = this.getSession().createSQLQuery(AlleleValuesDAO.GET_ALLELE_COUNT_BY_MARKER_ID);
			query.setParameterList("markerIdList", markerIds);
			BigInteger alleleCount = (BigInteger) query.uniqueResult();

			query = this.getSession().createSQLQuery(CharValuesDAO.GET_CHAR_COUNT_BY_MARKER_ID);
			query.setParameterList("markerIdList", markerIds);
			BigInteger charCount = (BigInteger) query.uniqueResult();

			query = this.getSession().createSQLQuery(MappingPopValuesDAO.GET_MAPPING_COUNT_BY_MARKER_ID);
			query.setParameterList("markerIdList", markerIds);
			BigInteger mappingCount = (BigInteger) query.uniqueResult();

			// Get marker name, germplasm name from allele_values given the marker names
			if (alleleCount.intValue() > 0) {
				query = this.getSession().createSQLQuery(AlleleValuesDAO.GET_ALLELE_GERMPLASM_NAME_AND_MARKER_NAME_BY_MARKER_NAMES);
				query.setParameterList("markerNameList", markerNames);
				List results = query.list();
				dataValues.addAll(this.getGermplasmMarkerElementsFromList(results));
			}

			// Get marker name, germplasm name from char_values given the marker names
			if (charCount.intValue() > 0) {
				query = this.getSession().createSQLQuery(CharValuesDAO.GET_CHAR_GERMPLASM_NAME_AND_MARKER_NAME_BY_MARKER_NAMES);
				query.setParameterList("markerNameList", markerNames);
				List results = query.list();
				dataValues.addAll(this.getGermplasmMarkerElementsFromList(results));
			}

			// Get marker name, germplasm name from mapping_pop_values given the marker names
			if (mappingCount.intValue() > 0) {
				query = this.getSession().createSQLQuery(MappingPopValuesDAO.GET_MAPPING_GERMPLASM_NAME_AND_MARKER_NAME_BY_MARKER_NAMES);
				query.setParameterList("markerNameList", markerNames);
				List results = query.list();
				dataValues.addAll(this.getGermplasmMarkerElementsFromList(results));
			}

			return dataValues;

		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error with getGermplasmNamesByMarkerNames(markerNames=" + markerNames + ") query from Marker: " + e.getMessage(), e);
		}
		return dataValues;
	}

	/**
	 * Gets the allelic value elements from list.
	 *
	 * @param results the results
	 * @return the allelic value elements from list
	 */
	private List<AllelicValueElement> getAllelicValueElementsFromList(final List results) {
		List<AllelicValueElement> values = new ArrayList<AllelicValueElement>();

		for (final Object o : results) {
			Object[] result = (Object[]) o;
			if (result != null) {
				Integer gid = (Integer) result[0];
				Integer markerId = (Integer) result[1];
				String data = (String) result[2];
				Integer peakHeight = (Integer) result[3];
				Integer markerSampleId = (Integer) result[4];
				Integer accSampleId = (Integer) result[5];
				AllelicValueElement allelicValueElement =
						new AllelicValueElement(gid, data, markerId, null, peakHeight, markerSampleId, accSampleId);
				values.add(allelicValueElement);
			}
		}

		return values;
	}

	private List<AllelicValueElement> getAllelicValueElementsFromListLocal(final List results) {
		List<AllelicValueElement> values = new ArrayList<AllelicValueElement>();

		for (final Object o : results) {
			Object[] result = (Object[]) o;
			if (result != null) {
				Integer gid = (Integer) result[0];
				Integer markerId = (Integer) result[1];
				String data = (String) result[2];
				Integer peakHeight = (Integer) result[3];
				AllelicValueElement allelicValueElement = new AllelicValueElement(gid, data, null, peakHeight);
				allelicValueElement.setMarkerId(markerId);
				values.add(allelicValueElement);
			}
		}

		return values;
	}

	/**
	 * Gets the allelic values by gids and marker names.
	 *
	 * @param gids the gids
	 * @param markerNames the marker names
	 * @return the allelic values by gids and marker names
	 * @throws MiddlewareQueryException the MiddlewareQueryException
	 */
	public List<AllelicValueElement> getAllelicValuesByGidsAndMarkerNames(final List<Integer> gids, final List<String> markerNames)
			throws MiddlewareQueryException {

		List<AllelicValueElement> allelicValues = new ArrayList<AllelicValueElement>();

		if (gids == null || gids.isEmpty() || markerNames == null || markerNames.isEmpty()) {
			return allelicValues;
		}

		// Get marker_ids by marker_names
		List<Integer> markerIds = this.getIdsByNames(markerNames, 0, Integer.MAX_VALUE);

		if (markerIds == null || markerIds.isEmpty()) {
			return allelicValues;
		}

		return this.getAllelicValuesByGidsAndMarkerIds(gids, markerIds);
	}

	/**
	 * Gets the allelic values by gids and marker ids.
	 *
	 * @param gids the gids
	 * @param markerIds the marker ids
	 * @return the allelic values by gids and marker ids
	 * @throws MiddlewareQueryException the MiddlewareQueryException
	 */
	public List<AllelicValueElement> getAllelicValuesByGidsAndMarkerIds(final List<Integer> gids, final List<Integer> markerIds)
			throws MiddlewareQueryException {

		List<AllelicValueElement> allelicValues = new ArrayList<AllelicValueElement>();

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
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getAllelicValuesByGidsAndMarkerIds(gIds=" + gids + ", markerIds=" + markerIds
					+ ")  query from Marker: " + e.getMessage(), e);
		}
		return allelicValues;
	}

	public List<AllelicValueElement> getAllelicValuesFromLocal(final List<Integer> gids) throws MiddlewareQueryException {

		List<AllelicValueElement> allelicValues = new ArrayList<AllelicValueElement>();

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
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getAllelicValuesFromLocal() query from Marker: " + e.getMessage(), e);
		}
		return allelicValues;
	}

	/**
	 * Get list of marker names by marker ids.
	 *
	 * @param ids the ids
	 * @return the names by ids
	 * @throws MiddlewareQueryException the MiddlewareQueryException
	 */
	public List<MarkerIdMarkerNameElement> getNamesByIds(final List<Integer> ids) throws MiddlewareQueryException {

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
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getNamesByIds(markerIds" + ids + ") query from Marker: " + e.getMessage(), e);
		}
		return new ArrayList<>();
	}

	public Map<Integer, String> getNamesByIdsMap(final List<Integer> ids) throws MiddlewareQueryException {
		Map<Integer, String> dataValues = new HashMap<Integer, String>();
		if (ids == null || ids.isEmpty()) {
			return dataValues;
		}

		try {
			SQLQuery query = this.getSession().createSQLQuery(MarkerDAO.GET_NAMES_BY_IDS);
			query.setParameterList("markerIdList", ids);

			List<Object> results = query.list();

			for (final Object o : results) {
				Object[] result = (Object[]) o;
				if (result != null) {
					Integer id = (Integer) result[0];
					String name = (String) result[1];
					dataValues.put(id, name);
				}
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error with getNamesByIdsMap(markerIds" + ids + ") query from Marker: " + e.getMessage(), e);
		}
		return dataValues;
	}

	public String getNameById(final Integer markerId) throws MiddlewareQueryException {
		String name = null;

		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("markerId", markerId));
			Marker marker = (Marker) criteria.uniqueResult();

			if (marker != null) {
				name = marker.getMarkerName();
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getNameById query from Marker: " + e.getMessage(), e);
		}

		return name;

	}

	/**
	 * Get all marker types.
	 *
	 * @param start the start
	 * @param numOfRows the num of rows
	 * @return the all marker types
	 * @throws MiddlewareQueryException the MiddlewareQueryException
	 */
	public List<String> getAllMarkerTypes(final int start, final int numOfRows) throws MiddlewareQueryException {
		try {
			SQLQuery query = this.getSession().createSQLQuery(MarkerDAO.GET_ALL_MARKER_TYPES);
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);

			List<String> markerTypes = query.list();

			return markerTypes;

		} catch (HibernateException e) {
			this.logAndThrowException("Error with getAllMarkerTypes() query from Marker: " + e.getMessage(), e);
		}
		return new ArrayList<String>();
	}

	/**
	 * Count all marker types.
	 *
	 * @return the long
	 * @throws MiddlewareQueryException the MiddlewareQueryException
	 */
	public long countAllMarkerTypes() throws MiddlewareQueryException {
		try {
			SQLQuery query = this.getSession().createSQLQuery(MarkerDAO.COUNT_ALL_MARKER_TYPES);
			BigInteger result = (BigInteger) query.uniqueResult();
			if (result != null) {
				return result.longValue();
			} else {
				return 0L;
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with countAllMarkerTypes() query from Marker: " + e.getMessage(), e);
		}
		return 0L;
	}

	/**
	 * Gets the all marker names by marker type.
	 *
	 * @param markerType the marker type
	 * @param start the start
	 * @param numOfRows the num of rows
	 * @return the all marker names by marker type
	 * @throws MiddlewareQueryException the MiddlewareQueryException
	 */
	public List<String> getMarkerNamesByMarkerType(final String markerType, final int start, final int numOfRows) throws MiddlewareQueryException {

		try {
			SQLQuery query = this.getSession().createSQLQuery(MarkerDAO.GET_NAMES_BY_TYPE);
			query.setParameter("markerType", markerType);
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);

			List<String> markerNames = query.list();

			return markerNames;
		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error with getMarkerNamesByMarkerType(markerType=" + markerType + ") query from Marker: " + e.getMessage(), e);
		}
		return new ArrayList<String>();
	}

	/**
	 * Count marker names by marker type.
	 *
	 * @param markerType the marker type
	 * @return the long
	 * @throws MiddlewareQueryException the MiddlewareQueryException
	 */
	public long countMarkerNamesByMarkerType(final String markerType) throws MiddlewareQueryException {
		try {
			SQLQuery query = this.getSession().createSQLQuery(MarkerDAO.COUNT_MARKER_NAMES_BY_MARKER_TYPE);
			query.setParameter("markerType", markerType);
			BigInteger result = (BigInteger) query.uniqueResult();

			if (result != null) {
				return result.longValue();
			}
			return 0;
		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error with countMarkerNamesByMarkerType(markerType=" + markerType + ") query from Marker: " + e.getMessage(), e);
		}
		return 0;
	}

	/**
	 * Gets the all db accession ids.
	 *
	 * @param start the start
	 * @param numOfRows the num of rows
	 * @return all non-empty db accession ids
	 * @throws MiddlewareQueryException
	 */
	public List<String> getAllDbAccessionIds(final int start, final int numOfRows) throws MiddlewareQueryException {
		try {
			SQLQuery query = this.getSession().createSQLQuery(MarkerDAO.GET_ALL_DB_ACCESSION_IDS);
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);
			return query.list();
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getAllDbAccessionIds() query from Marker: " + e.getMessage(), e);
		}
		return new ArrayList<String>();
	}

	/**
	 * Count all db accession ids.
	 *
	 * @return the number of distinct db accession ids
	 * @throws MiddlewareQueryException the MiddlewareQueryException
	 */
	public long countAllDbAccessionIds() throws MiddlewareQueryException {
		try {
			SQLQuery query = this.getSession().createSQLQuery(MarkerDAO.COUNT_ALL_DB_ACCESSION_IDS);
			BigInteger result = (BigInteger) query.uniqueResult();
			if (result != null) {
				return result.longValue();
			} else {
				return 0L;
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with countAllDbAccessionIds() query from Marker: " + e.getMessage(), e);
		}
		return 0L;
	}

	public List<Marker> getMarkersByIds(final List<Integer> markerIds) throws MiddlewareQueryException {
		return this.getMarkersByIds(markerIds, 0, Integer.MAX_VALUE);
	}

	public List<Marker> getMarkersByIds(final List<Integer> markerIds, final int start, final int numOfRows) throws MiddlewareQueryException {
		if (markerIds == null || markerIds.isEmpty()) {
			return new ArrayList<Marker>();
		}

		try {
			SQLQuery query = this.getSession().createSQLQuery(MarkerDAO.GET_MARKERS_BY_IDS);
			query.setParameterList("markerIdList", markerIds);
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);
			List results = query.list();

			List<Marker> dataValues = new ArrayList<Marker>();
			for (final Object o : results) {
				Object[] result = (Object[]) o;
				if (result != null) {
					dataValues.add(this.convertToMarker(result));
				}
			}
			return dataValues;
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getMarkersByIds() query from Marker: " + e.getMessage(), e);
		}
		return new ArrayList<Marker>();
	}

	// GCP-7874
	public List<Marker> getSNPMarkersByHaplotype(final String haplotype) throws MiddlewareQueryException {
		if (StringUtils.isEmpty(haplotype)) {
			return new ArrayList<Marker>();
		}

		try {
			SQLQuery query = this.getSession().createSQLQuery(MarkerDAO.GET_SNP_MARKERS_BY_HAPLOTYPE);
			query.setParameter("trackName", haplotype);
			List results = query.list();

			List<Marker> dataValues = new ArrayList<Marker>();
			for (final Object o : results) {
				Object[] result = (Object[]) o;
				if (result != null) {
					dataValues.add(this.convertToMarker(result));
				}
			}
			return dataValues;
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getSNPMarkersByHaplotype() query from Marker: " + e.getMessage(), e);
		}

		return new ArrayList<Marker>();
	}

	public List<Integer> getMarkerIDsByHaplotype(final String haplotype) throws MiddlewareQueryException {
		if (StringUtils.isEmpty(haplotype)) {
			return new ArrayList<Integer>();
		}

		try {
			SQLQuery query = this.getSession().createSQLQuery(MarkerDAO.GET_MARKER_IDS_BY_HAPLOTYPE);
			query.setParameter("trackName", haplotype);
			return query.list();
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getSNPMarkerIDsByHaplotype() query from Marker: " + e.getMessage(), e);
		}

		return new ArrayList<Integer>();
	}

	public List<Marker> getMarkersByIdsAndType(final List<Integer> markerIds, final String markerType) throws MiddlewareQueryException {
		List<Marker> dataValues = new ArrayList<Marker>();
		if (StringUtils.isEmpty(markerType)) {
			return dataValues;
		}

		try {
			List<Marker> markersByIds = this.getMarkersByIds(markerIds, 0, Integer.MAX_VALUE);

			for (final Marker marker : markersByIds) {
				if (marker.getMarkerType().equalsIgnoreCase(markerType)) {
					dataValues.add(marker);
				}
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getMarkersByIdsAndType() query from Marker: " + e.getMessage(), e);
		}

		return dataValues;
	}

	public List<Marker> getMarkersByType(final String markerType) throws MiddlewareQueryException {
		List<Marker> dataValues = new ArrayList<Marker>();
		if (StringUtils.isEmpty(markerType)) {
			return dataValues;
		}

		try {
			SQLQuery query = this.getSession().createSQLQuery(MarkerDAO.GET_MARKERS_BY_TYPE);
			query.setParameter("markerType", markerType);
			List results = query.list();
			for (final Object o : results) {
				Object[] result = (Object[]) o;
				if (result != null) {
					dataValues.add(this.convertToMarker(result));
				}
			}
			return dataValues;
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getMarkersByIds() query from Marker: " + e.getMessage(), e);
		}

		return dataValues;
	}

	protected Marker convertToMarker(final Object[] result) {

		Integer markerId = (Integer) result[0];
		String markerType = (String) result[1];
		String markerName = (String) result[2];
		String species = (String) result[3];
		String dbAccessionId = (String) result[4];
		String reference = (String) result[5];
		String genotype = (String) result[6];
		String ploidy = (String) result[7];
		String primerId = (String) result[8];
		String remarks = (String) result[9];
		String assayType = (String) result[10];
		String motif = (String) result[11];
		String forwardPrimer = (String) result[12];
		String reversePrimer = (String) result[13];
		String productSize = (String) result[14];
		Float annealingTemp = (Float) result[15];
		String amplification = (String) result[16];

		Marker element =
				new Marker(markerId, markerType, markerName, species, dbAccessionId, reference, genotype, ploidy, primerId, remarks,
						assayType, motif, forwardPrimer, reversePrimer, productSize, annealingTemp, amplification);

		return element;
	}

	public long countMarkersByIds(final List<Integer> markerIds) throws MiddlewareQueryException {
		if (markerIds == null || markerIds.isEmpty()) {
			return 0;
		}
		try {
			SQLQuery query = this.getSession().createSQLQuery(MarkerDAO.COUNT_MARKERS_BY_IDS);
			query.setParameterList("markerIdList", markerIds);
			BigInteger result = (BigInteger) query.uniqueResult();
			if (result != null) {
				return result.longValue();
			} else {
				return 0L;
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with countMarkersByIds() query from Marker: " + e.getMessage(), e);
		}
		return 0L;
	}

	public Set<Integer> getMarkerIDsByMapIDAndLinkageBetweenStartPosition(final int mapID, final String linkageGroup, final double startPos, final double endPos,
			final int start, final int numOfRows) throws MiddlewareQueryException {
		try {

			SQLQuery query;

			query = this.getSession().createSQLQuery(MarkerDAO.GET_MARKER_IDS_BY_MAP_ID_AND_LINKAGE_BETWEEN_START_POSITION);
			query.setParameter("mapId", mapID);
			query.setParameter("linkageGroup", linkageGroup);
			query.setParameter("startPosition", startPos);
			query.setParameter("endPosition", endPos);
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);
			Set<Integer> markerIDSet = new TreeSet<Integer>(query.list());

			return markerIDSet;

		} catch (HibernateException e) {
			this.logAndThrowException("Error with getMarkerIdsByMapIDAndLinkageBetweenStartPosition(mapID=" + mapID + ", linkageGroup="
					+ linkageGroup + ", start=" + start + ", numOfRows=" + numOfRows + ") query from MarkerOnMap: " + e.getMessage(), e);
		}
		return new TreeSet<Integer>();
	}

	public long countMarkerIDsByMapIDAndLinkageBetweenStartPosition(
			final int mapID, final String linkageGroup, final double startPos, final double endPos)
			throws MiddlewareQueryException {
		try {

			SQLQuery query;

			query = this.getSession().createSQLQuery(MarkerDAO.COUNT_MARKER_IDS_BY_MAP_ID_AND_LINKAGE_BETWEEN_START_POSITION);
			query.setParameter("map_id", mapID);
			query.setParameter("linkage_group", linkageGroup);
			query.setParameter("start_position", startPos);
			query.setParameter("end_position", endPos);
			BigInteger result = (BigInteger) query.uniqueResult();
			if (result != null) {
				return result.longValue();
			} else {
				return 0;
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error with countMarkerIdsByMapIDAndLinkageBetweenStartPosition(mapID=" + mapID + ", linkageGroup="
					+ linkageGroup + ") query from Marker: " + e.getMessage(), e);
		}
		return 0L;
	}

	public Integer getIdByName(final String name) throws MiddlewareQueryException {
		try {
			SQLQuery query = this.getSession().createSQLQuery(MarkerDAO.GET_ID_BY_NAME);
			query.setParameter("markerName", name);
			return (Integer) query.uniqueResult();

		} catch (HibernateException e) {
			this.logAndThrowException("Error with getIdByName(" + name + "): " + e.getMessage(), e);
		}

		return null;
	}

	public List<String> getMarkerNamesByIds(final List<Integer> markerIds) throws MiddlewareQueryException {
		if (markerIds == null || markerIds.isEmpty()) {
			return new ArrayList<String>();
		}

		try {
			StringBuffer sql =
					new StringBuffer().append("SELECT CONCAT(marker_name, '') FROM gdms_marker WHERE marker_id IN (:markerIds) ");

			SQLQuery query = this.getSession().createSQLQuery(sql.toString());
			query.setParameterList("markerIds", markerIds);
			return query.list();
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getMarkerNamesByIds() query from Marker: " + e.getMessage(), e);
		}
		return new ArrayList<String>();
	}

	public List<Marker> getMarkersByMapId(final Integer mapId) throws MiddlewareQueryException {
		List<Marker> dataValues = new ArrayList<>();
		if (null == mapId) {
			return dataValues;
		}

		try {

			SQLQuery query = this.getSession().createSQLQuery(MarkerDAO.GET_MARKERS_BY_MAP_ID);
			query.setParameter("map_id", mapId);
			List results = query.list();
			for (final Object o : results) {
				Object[] result = (Object[]) o;
				if (result != null) {
					dataValues.add(this.convertToMarker(result));
				}
			}
			return dataValues;
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getMarkersByMapId() query from Marker: " + e.getMessage(), e);
		}

		return dataValues;
	}
}
