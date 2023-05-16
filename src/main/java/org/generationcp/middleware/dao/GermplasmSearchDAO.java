package org.generationcp.middleware.dao;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.api.germplasm.search.GermplasmSearchRequest;
import org.generationcp.middleware.api.germplasm.search.GermplasmSearchRequest.IncludePedigree;
import org.generationcp.middleware.api.germplasm.search.GermplasmSearchResponse;
import org.generationcp.middleware.constant.ColumnLabels;
import org.generationcp.middleware.domain.gms.search.GermplasmSortableColumn;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.domain.sqlfilter.SqlTextFilter;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.exceptions.MiddlewareRequestException;
import org.generationcp.middleware.manager.GermplasmDataManagerUtil;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.ims.ExperimentTransactionType;
import org.generationcp.middleware.pojos.ims.TransactionStatus;
import org.generationcp.middleware.pojos.ims.TransactionType;
import org.generationcp.middleware.util.SqlQueryParamBuilder;
import org.generationcp.middleware.util.Util;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.CollectionUtils;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * DAO class for Germplasm Search functionality.
 */
public class GermplasmSearchDAO extends GenericDAO<Germplasm, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(GermplasmSearchDAO.class);

	// Prevent silly searches from resulting in GIANT IN clauses in search query (which reuses this function).
	// Old search query had the same hardcoded limit of 5000 anyway so this is not changing existing logic as such. Applies to both
	// count and search queries. In future we can detect that the search is resulting in more than 5000 matches and go back to the
	// user asking them to refine search criteria.
	private static final String LIMIT_CLAUSE = " LIMIT 5000 ";

	private static final String UNION = " UNION ";
	private static final String GERMPLASM_NOT_DELETED_CLAUSE = " AND  g.deleted = 0  AND g.grplce = 0 ";
	private static final String STATUS_DELETED = "9";
	private static final String GERMPLSM = "germplsm";

	public static final String NAMES = "NAMES";
	private static final String MIXED_UNITS_LABEL = "Mixed";
	public static final String LOCATION_ID = "LOCATION_ID";
	public static final String LOCATION_ABBR = "LOCATION_ABBR";
	public static final String REFERENCE = "REFERENCE";
	public static final String METHOD_ID = "METHOD_ID";

	public static final String GID = ColumnLabels.GID.getName();
	public static final String GERMLASM_UUID = ColumnLabels.GERMPLASM_UUID.getName();
	public static final String GROUP_ID = ColumnLabels.GROUP_ID.getName();
	public static final String STOCK_IDS = ColumnLabels.STOCKID.getName();
	public static final String AVAIL_LOTS = ColumnLabels.AVAILABLE_INVENTORY.getName();
	public static final String AVAIL_BALANCE = ColumnLabels.TOTAL.getName();
	public static final String LOT_UNITS = ColumnLabels.UNITS.getName();
	public static final String METHOD_NAME = ColumnLabels.BREEDING_METHOD_NAME.getName();
	public static final String LOCATION_NAME = ColumnLabels.GERMPLASM_LOCATION.getName();
	public static final String METHOD_ABBREVIATION = ColumnLabels.BREEDING_METHOD_ABBREVIATION.getName();
	public static final String METHOD_NUMBER = ColumnLabels.BREEDING_METHOD_NUMBER.getName();
	public static final String METHOD_GROUP = ColumnLabels.BREEDING_METHOD_GROUP.getName();
	public static final String PREFERRED_NAME = ColumnLabels.PREFERRED_NAME.getName();
	public static final String PREFERRED_ID = ColumnLabels.PREFERRED_ID.getName();
	public static final String FEMALE_PARENT_ID = ColumnLabels.FGID.getName();
	public static final String FEMALE_PARENT_PREFERRED_NAME = ColumnLabels.CROSS_FEMALE_PREFERRED_NAME.getName();
	public static final String MALE_PARENT_ID = ColumnLabels.MGID.getName();
	public static final String MALE_PARENT_PREFERRED_NAME = ColumnLabels.CROSS_MALE_PREFERRED_NAME.getName();
	public static final String GERMPLASM_DATE = ColumnLabels.GERMPLASM_DATE.getName();

	public static final String GROUP_SOURCE_GID = ColumnLabels.GROUP_SOURCE_GID.getName();
	public static final String GROUP_SOURCE_PREFERRED_NAME = ColumnLabels.GROUP_SOURCE_PREFERRED_NAME.getName();
	public static final String IMMEDIATE_SOURCE_GID = ColumnLabels.IMMEDIATE_SOURCE_GID.getName();
	public static final String IMMEDIATE_SOURCE_NAME = ColumnLabels.IMMEDIATE_SOURCE_NAME.getName();

	public static final String HAS_PROGENY = ColumnLabels.HAS_PROGENY.getName();
	public static final String USED_IN_LOCKED_STUDY = ColumnLabels.USED_IN_LOCKED_STUDY.getName();
	public static final String USED_IN_LOCKED_LIST = ColumnLabels.USED_IN_LOCKED_LIST.getName();

	private static final Map<String, String> selectClauseColumnsMap = new HashMap<>();

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(Util.DATE_AS_NUMBER_FORMAT);

	//Excluding Parents Property from SQL
	private static final List<String> GERMPLASM_TREE_NODE_PROPERTY_IDS =
		Collections.unmodifiableList(Arrays.asList(GermplasmSearchDAO.MALE_PARENT_ID,
			GermplasmSearchDAO.FEMALE_PARENT_ID,
			GermplasmSearchDAO.MALE_PARENT_PREFERRED_NAME,
			GermplasmSearchDAO.FEMALE_PARENT_PREFERRED_NAME));

	private static final List<String> GERMPLASM_SEARCH_FIXED_SCALARS = Collections.unmodifiableList(Arrays.asList(NAMES,
		GROUP_ID, AVAIL_LOTS,
		AVAIL_BALANCE,
		LOT_UNITS,
		METHOD_NAME,
		LOCATION_NAME,
		LOCATION_ID,
		METHOD_ID,
		HAS_PROGENY,
		USED_IN_LOCKED_STUDY,
		USED_IN_LOCKED_LIST));

	static {

		GermplasmSearchDAO.selectClauseColumnsMap.put(GermplasmSearchDAO.METHOD_ABBREVIATION,
			String.format("m.mcode AS `%s` \n", GermplasmSearchDAO.METHOD_ABBREVIATION));
		GermplasmSearchDAO.selectClauseColumnsMap.put(GermplasmSearchDAO.METHOD_NUMBER,
			String.format("m.mid AS `%s` \n", GermplasmSearchDAO.METHOD_NUMBER));
		GermplasmSearchDAO.selectClauseColumnsMap.put(GermplasmSearchDAO.METHOD_GROUP,
			String.format("m.mgrp AS `%s` \n", GermplasmSearchDAO.METHOD_GROUP));
		GermplasmSearchDAO.selectClauseColumnsMap.put(GermplasmSearchDAO.PREFERRED_ID,
			String.format("preferredIdOfGermplasm.nval AS `%s` \n", GermplasmSearchDAO.PREFERRED_ID));
		GermplasmSearchDAO.selectClauseColumnsMap.put(GermplasmSearchDAO.PREFERRED_NAME,
			String.format("nameOfGermplasm.nval AS `%s` \n", GermplasmSearchDAO.PREFERRED_NAME));
		GermplasmSearchDAO.selectClauseColumnsMap.put(GermplasmSearchDAO.GERMPLASM_DATE,
			String.format("g.gdate AS `%s` \n", GermplasmSearchDAO.GERMPLASM_DATE));
		GermplasmSearchDAO.selectClauseColumnsMap
			.put(GermplasmSearchDAO.GROUP_SOURCE_GID,
				String.format(" CASE \n WHEN g.gnpgs = -1 AND g.gpid1 IS NOT NULL \n"
						+ " AND g.gpid1 <> 0 THEN g.gpid1 \n ELSE '-' \n" + " END AS `%s` \n",
					GermplasmSearchDAO.GROUP_SOURCE_GID));
		GermplasmSearchDAO.selectClauseColumnsMap.put(GermplasmSearchDAO.GROUP_SOURCE_PREFERRED_NAME,
			String.format(
				" CASE \n  WHEN g.gnpgs = -1 \n AND g.gpid1 IS NOT NULL \n"
					+ " AND g.gpid1 <> 0 THEN groupSource.nval \n ELSE '-' \n" + " END AS `%s` \n",
				GermplasmSearchDAO.GROUP_SOURCE_PREFERRED_NAME));

		GermplasmSearchDAO.selectClauseColumnsMap
			.put(GermplasmSearchDAO.IMMEDIATE_SOURCE_GID,
				String.format(" CASE \n WHEN g.gnpgs = -1 AND g.gpid2 IS NOT NULL \n"
						+ " AND g.gpid2 <> 0 THEN g.gpid2 \n ELSE '-' \n" + " END AS `%s` \n",
					GermplasmSearchDAO.IMMEDIATE_SOURCE_GID));
		GermplasmSearchDAO.selectClauseColumnsMap
			.put(GermplasmSearchDAO.IMMEDIATE_SOURCE_NAME,
				String.format(" CASE \n WHEN g.gnpgs = -1 AND g.gpid2 IS NOT NULL \n"
						+ "	AND g.gpid2 <> 0 THEN immediateSource.nval \n" + "	ELSE '-' \n END AS `%s` \n",
					GermplasmSearchDAO.IMMEDIATE_SOURCE_NAME));

	}

	private static final Map<String, String> fromClauseColumnsMap = new HashMap<>();

	static {

		GermplasmSearchDAO.fromClauseColumnsMap.put(GermplasmSearchDAO.PREFERRED_NAME,
			"LEFT JOIN names nameOfGermplasm ON g.gid = nameOfGermplasm.gid AND nameOfGermplasm.nstat = 1 \n");
		GermplasmSearchDAO.fromClauseColumnsMap.put(GermplasmSearchDAO.PREFERRED_ID,
			"LEFT JOIN names preferredIdOfGermplasm ON g.gid = preferredIdOfGermplasm.gid AND preferredIdOfGermplasm.nstat = 8 \n");
		GermplasmSearchDAO.fromClauseColumnsMap.put(GermplasmSearchDAO.GROUP_SOURCE_GID, " ");
		GermplasmSearchDAO.fromClauseColumnsMap.put(GermplasmSearchDAO.GROUP_SOURCE_PREFERRED_NAME,
			"LEFT JOIN names groupSource ON g.gpid1 = groupSource.gid AND groupSource.nstat = 1 \n");
		GermplasmSearchDAO.fromClauseColumnsMap.put(GermplasmSearchDAO.IMMEDIATE_SOURCE_GID, " ");
		GermplasmSearchDAO.fromClauseColumnsMap.put(GermplasmSearchDAO.IMMEDIATE_SOURCE_NAME,
			"LEFT JOIN names immediateSource ON g.gpid2 = immediateSource.gid AND immediateSource.nstat = 1 \n");

	}

	public GermplasmSearchDAO(final Session session) {
		super(session);
	}

	private Set<Integer> retrieveGIDGroupMemberResults(final Set<Integer> gidSearchResults) {
		try {
			final Set<Integer> gidGroupMembersSearchResults = new HashSet<>();
			final StringBuilder queryString = new StringBuilder();
			queryString.append("SELECT members.gid FROM germplsm members WHERE members.deleted = 0 AND members.grplce = 0 "
				+ "AND members.mgid IN (select g.mgid from germplsm g where g.gid IN (:gids) and g.mgid != 0)");

			final SQLQuery query = this.getSession().createSQLQuery(queryString.toString());
			query.setParameterList("gids", gidSearchResults);

			gidGroupMembersSearchResults.addAll(query.list());
			return gidGroupMembersSearchResults;

		} catch (final HibernateException e) {
			final String message = "Error with retrieveGIDGroupMemberResults(GIDS=" + gidSearchResults + ") : " + e.getMessage();
			GermplasmSearchDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	protected String addSortingColumns(final Map<String, Boolean> sortState, final Map<String, Integer> attributeTypesMap,
		final Map<String, Integer> nameTypesMap) {

		final Map<String, Boolean> filteredSortState =
			sortState.entrySet().stream().filter(map -> !this.GERMPLASM_TREE_NODE_PROPERTY_IDS.contains(map.getKey()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		if (filteredSortState.isEmpty()) {
			return "";
		}

		final StringBuilder sortingQuery = new StringBuilder();
		sortingQuery.append(" ORDER BY ");
		for (final Map.Entry<String, Boolean> sortCondition : filteredSortState.entrySet()) {

			final String order = sortCondition.getValue().equals(true) ? "ASC" : "DESC";

			if (attributeTypesMap.containsKey(sortCondition.getKey()) || nameTypesMap.containsKey(sortCondition.getKey())) {
				sortingQuery.append(String.format(" `%s`", sortCondition.getKey()));

			} else if (GermplasmSortableColumn.get(sortCondition.getKey()) != null) {
				sortingQuery.append(String.format(" `%s`", GermplasmSortableColumn.get(sortCondition.getKey()).getDbColumnName()));
			} else {
				throw new MiddlewareRequestException(null, "error.sort.invalid.key", new String[] {sortCondition.getKey()});
			}

			sortingQuery.append(" " + order);
			sortingQuery.append(",");
		}

		String sortingQueryStr = sortingQuery.toString();
		// remove unnecessary ',' at end of sorting query
		sortingQueryStr = sortingQueryStr.substring(0, sortingQuery.lastIndexOf(","));
		return sortingQueryStr;
	}

	/**
	 * Creates a map that contains the values of 'Attribute' columns.
	 *
	 * @param indexOffset
	 * @param row
	 * @param addedColumnsPropertyIds
	 * @param attributeTypesMap
	 * @return
	 */
	protected void setValuesMapForAttributeAndNameTypes(final Germplasm germplasm, final Object[] row,
		final List<String> addedColumnsPropertyIds, final Map<String, Integer> attributeTypesMap,
		final Map<String, Integer> nameTypesMap, final int indexOffset) {

		final Map<String, String> attributeTypesValueMap = new HashMap<>();
		final Map<String, String> nameTypesValueMap = new HashMap<>();
		for (final String propertyId : addedColumnsPropertyIds) {
			if (attributeTypesMap.containsKey(propertyId)) {
				attributeTypesValueMap.put(propertyId, this.getValueOfAddedColumns(propertyId, row, addedColumnsPropertyIds, indexOffset));
			} else if (nameTypesMap.containsKey(propertyId)) {
				nameTypesValueMap.put(propertyId, this.getValueOfAddedColumns(propertyId, row, addedColumnsPropertyIds, indexOffset));
			}

		}
		germplasm.setAttributeTypesValueMap(attributeTypesValueMap);
		germplasm.setNameTypesValueMap(nameTypesValueMap);

	}

	/**
	 * Gets the value of the addable columns (e.g. Preferred ID, Germplmasm Date, etc.) from the Row object array returned by germplasm
	 * search
	 *
	 * @param propertyId
	 * @param row
	 * @param addedColumnsPropertyIds
	 * @param indexOffset
	 * @return
	 */
	protected String getValueOfAddedColumns(final String propertyId, final Object[] row, final List<String> addedColumnsPropertyIds,
		final int indexOffset) {

		if (addedColumnsPropertyIds.contains(propertyId)) {
			final int addedColumnIndex = addedColumnsPropertyIds.indexOf(propertyId) + indexOffset;

			return row[addedColumnIndex] != null ? String.valueOf(row[addedColumnIndex]) : "";
		} else {
			return "";
		}

	}

	protected Map<String, Integer> getAttributeTypesMap(final List<String> addedColumnsPropertyIds, final String programUUID) {

		final List<String> nonStandardColumns = new ArrayList<>();
		for (final String propertyId : addedColumnsPropertyIds) {
			if (!ColumnLabels.getAddableGermplasmColumns().contains(propertyId)) {
				nonStandardColumns.add(propertyId);
			}
		}

		return this.getAttributeFromOntology(addedColumnsPropertyIds, nonStandardColumns, programUUID);
	}

	protected Map<String, Integer> getNameTypesMap(final List<String> addedColumnsPropertyIds) {

		final List<String> nonStandardColumns = new ArrayList<>();
		for (final String propertyId : addedColumnsPropertyIds) {
			if (!ColumnLabels.getAddableGermplasmColumns().contains(propertyId)) {
				nonStandardColumns.add(propertyId);
			}
		}

		return this.getNameTypesFromUserDefinedFieldTable(addedColumnsPropertyIds, nonStandardColumns);
	}

	private Map<String, Integer> getNameTypesFromUserDefinedFieldTable(final List<String> addedColumnsPropertyIds,
		final List<String> nonStandardColumns) {
		final Map<String, Integer> typesMap = new HashMap<>();
		if (!nonStandardColumns.isEmpty()) {
			final SQLQuery query = this.getSession()
				.createSQLQuery("SELECT fname , fldno from udflds where ftable = 'NAMES' and fname IN (:fieldList)");
			query.setParameterList("fieldList", addedColumnsPropertyIds);
			final List<Object[]> results = query.list();

			for (final Object[] row : results) {
				typesMap.put(String.valueOf(row[0]).toUpperCase(), (Integer) row[1]);
			}

		}

		return typesMap;
	}

	private Map<String, Integer> getAttributeFromOntology(final List<String> addedColumnsPropertyIds,
		final List<String> nonStandardColumns, final String programUUID) {
		final Map<String, Integer> typesMap = new HashMap<>();
		if (!nonStandardColumns.isEmpty()) {
			final SQLQuery query = this.getSession()
				.createSQLQuery(" SELECT IFNULL(vo.alias, cv.name) as name, cv.cvterm_id "
					+ " FROM cvterm cv INNER JOIN cvtermprop cp ON cp.type_id = " + TermId.VARIABLE_TYPE.getId() + " and cv.cvterm_id = cp.cvterm_id " //
					+ " LEFT JOIN variable_overrides vo ON vo.cvterm_id = cv.cvterm_id AND vo.program_uuid = :programUUID " //
					+ " INNER JOIN cvterm vartype on vartype.name = cp.value and vartype.cvterm_id in ( "
					+ VariableType.GERMPLASM_PASSPORT.getId() + ","
					+ VariableType.GERMPLASM_ATTRIBUTE.getId() + ") " //
					+ "WHERE cv.name IN (:fieldList) or vo.alias IN (:fieldList)");
			query.setParameter("programUUID", programUUID);
			query.setParameterList("fieldList", addedColumnsPropertyIds);

			final List<Object[]> results = query.list();

			for (final Object[] row : results) {
				typesMap.put(String.valueOf(row[0]).toUpperCase(), (Integer) row[1]);
			}

		}

		return typesMap;
	}

	// +++++++++++++++++++++++++++++++++++++++New Germplasm search ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * New germplasm query
	 *
	 * @param germplasmSearchRequest
	 * @param pageable               if null -> returns 5000 + included gids
	 * @param programUUID
	 */
	public List<GermplasmSearchResponse> searchGermplasm(final GermplasmSearchRequest germplasmSearchRequest, final Pageable pageable,
		final String programUUID) {

		try {
			// list is used again in a query wrapper to paginate (filtered+included) gids
			final List<Integer> gids = this.retrieveSearchGids(germplasmSearchRequest, pageable, programUUID);

			if (gids.isEmpty()) {
				return Collections.emptyList();
			}

			//Transform all added columns to upper case as attribute and name types will be stored as uppercase in map key
			germplasmSearchRequest.getAddedColumnsPropertyIds().replaceAll(String::toUpperCase);

			final List<String> addedColumnsPropertyIds = germplasmSearchRequest.getAddedColumnsPropertyIds();
			final Map<String, Integer> attributeTypesMap = this.getAttributeTypesMap(addedColumnsPropertyIds, programUUID);
			final Map<String, Integer> nameTypesMap = this.getNameTypesMap(addedColumnsPropertyIds);

			// main query
			final StringBuilder queryBuilder =
				new StringBuilder(this.createSelectClause(addedColumnsPropertyIds, attributeTypesMap, nameTypesMap));
			queryBuilder.append(this.createFromClause(addedColumnsPropertyIds, attributeTypesMap, nameTypesMap));

			queryBuilder.append(" where g.gid in (:gids) ");
			queryBuilder.append(GERMPLASM_NOT_DELETED_CLAUSE);
			queryBuilder.append(" group by g.gid ");

			final Map<String, Boolean> sortState = convertSort(pageable);
			if (!sortState.isEmpty()) {
				queryBuilder.append(this.addSortingColumns(sortState, attributeTypesMap, nameTypesMap));
			}

			final SQLQuery query = this.getSession().createSQLQuery(queryBuilder.toString());

			query.addEntity(GERMPLSM, Germplasm.class);
			query.addScalar(NAMES);
			query.addScalar(GROUP_ID);
			query.addScalar(AVAIL_LOTS);
			query.addScalar(AVAIL_BALANCE);
			query.addScalar(LOT_UNITS);
			query.addScalar(METHOD_NAME);
			query.addScalar(LOCATION_NAME);
			query.addScalar(LOCATION_ID);
			query.addScalar(METHOD_ID);
			query.addScalar(LOCATION_ABBR);
			query.addScalar(REFERENCE);

			final List<String> filteredProperties = addedColumnsPropertyIds.stream()
				.filter(s -> !this.GERMPLASM_TREE_NODE_PROPERTY_IDS.contains(s)).collect(Collectors.toList());
			for (final String propertyId : filteredProperties) {
				if (!GERMPLASM_SEARCH_FIXED_SCALARS.contains(propertyId)) {
					query.addScalar(propertyId);
				}
			}

			/*
			 * For big databases (e.g brachiaria, ~6M germplsm), sorting is slow.
			 * If sort is needed, we limit the inner query to 5000 records and sort only that.
			 * Otherwise, Pagination is done in the inner query.
			 *
			 * The outer query returns a PAGE of results + associated gids (e.g pedigree, group members),
			 * which don't count for the Total results
			 */
			if (!sortState.isEmpty() && pageable != null) {
				addPaginationToSQLQuery(query, pageable);
			}
			query.setParameterList("gids", gids);

			final List<Object[]> results = query.list();

			if (results == null) {
				return Collections.emptyList();
			}

			final List<GermplasmSearchResponse> response = new ArrayList<>();
			for (final Object[] result : results) {
				response.add(this.mapToGermplasmSearchResponse(result, filteredProperties, attributeTypesMap, nameTypesMap));
			}
			return response;
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error at GermplasmSearchDAO.searchGermplasm(): " + e.getMessage(), e);
		}
	}

	private String createFromClause(final List<String> addedColumnsPropertyIds,
		final Map<String, Integer> attributeTypesMap, final Map<String, Integer> nameTypesMap) {

		final StringBuilder fromClause = new StringBuilder();
		fromClause.append("FROM germplsm g \n" //
			+ " LEFT JOIN ims_lot gl"  //
			// FIXME IBP-4320
			+ "  FORCE INDEX (ims_lot_idx01)"  //
			+ "  ON gl.eid = g.gid AND gl.etype = 'GERMPLSM' AND gl.status = 0 \n" //
			+ " LEFT JOIN ims_transaction gt"  //
			+ "  FORCE INDEX (ims_transaction_idx01) " //
			+ "  ON gt.lotid = gl.lotid AND gt.trnstat <> 9 \n" //
			+ " LEFT JOIN cvterm scale ON scale.cvterm_id = gl.scaleid \n" //
			+ " LEFT JOIN methods m ON m.mid = g.methn \n" //
			+ " LEFT JOIN location l ON l.locid = g.glocn \n" //
			+ " LEFT JOIN bibrefs ref ON ref.refid = g.gref \n" //
			+ " LEFT JOIN `names` allNames ON g.gid = allNames.gid and allNames.nstat != " + STATUS_DELETED + " \n");

		for (final String propertyId : addedColumnsPropertyIds) {
			if (GermplasmSearchDAO.fromClauseColumnsMap.containsKey(propertyId)) {
				fromClause.append(GermplasmSearchDAO.fromClauseColumnsMap.get(propertyId));

			} else if (attributeTypesMap.containsKey(propertyId)) {
				fromClause.append(String.format("LEFT JOIN `atributs` `%s` on `%<s`.gid = g.gid and `%<s`.atype = %s \n", propertyId,
					attributeTypesMap.get(propertyId)));

			} else if (nameTypesMap.containsKey(propertyId)) {
				fromClause.append(String.format("LEFT JOIN `names` `%s` on `%<s`.gid = g.gid and `%<s`.ntype = %s \n", propertyId,
					nameTypesMap.get(propertyId)));
			}
		}

		return fromClause.toString();

	}

	private String createSelectClause(final List<String> addedColumnsPropertyIds,
		final Map<String, Integer> attributeTypesMap, final Map<String, Integer> nameTypesMap) {

		final StringBuilder selectClause = new StringBuilder();
		selectClause.append("SELECT g.*, \n" //
			// name ordering: preferred name > latest > oldest
			+ " g.germplsm_uuid AS `" + GermplasmSearchDAO.GERMLASM_UUID + "`, \n" //
			+ " Group_concat(DISTINCT allNames.nval ORDER BY allNames.nstat = 1 desc, allNames.ndate desc SEPARATOR ', ') AS `"
			+ GermplasmSearchDAO.NAMES + "`, \n"  //
			+ " g.mgid AS `" + GermplasmSearchDAO.GROUP_ID + "`, \n" //
			+ " Count(DISTINCT gl.lotid) AS `" + GermplasmSearchDAO.AVAIL_LOTS + "`, \n" //
			/*
			 * Sum of transaction (duplicated because of joins)
			 * -----divided by----
			 * ( count of tr / count of distinct tr) = how many times the real sum is repeated
			 * ===================
			 * Real sum = Available balance
			 */
			+ " IF(COUNT(DISTINCT IFNULL(gl.scaleid, 'null')) = 1, " //
			+ "  IFNULL((SELECT SUM(CASE WHEN gt.trnstat = " + TransactionStatus.CONFIRMED.getIntValue() //
			+ "    OR (gt.trnstat = " + TransactionStatus.PENDING.getIntValue() //
			+ "    AND gt.trntype = " + TransactionType.WITHDRAWAL.getId() + ") THEN gt.trnqty ELSE 0 END)) " //
			+ "  /(Count(gt.trnid)/Count(DISTINCT gt.trnid)), 0)" //
			+ " , '" + MIXED_UNITS_LABEL + "') AS  `" + GermplasmSearchDAO.AVAIL_BALANCE + "`, \n"  //
			+ " IF(COUNT(DISTINCT IFNULL(gl.scaleid, 'null')) = 1, scale.name, '" + MIXED_UNITS_LABEL + "') AS `" + LOT_UNITS + "`, \n"  //
			+ " m.mname AS `" + GermplasmSearchDAO.METHOD_NAME + "`, \n"  //
			+ " l.lname AS `" + GermplasmSearchDAO.LOCATION_NAME + "`, \n"
			+ " m.mid AS `" + GermplasmSearchDAO.METHOD_ID + "`, \n"  //
			+ " l.locid AS `" + GermplasmSearchDAO.LOCATION_ID + "`, \n"
			+ " l.labbr AS `" + GermplasmSearchDAO.LOCATION_ABBR + "`, \n"
			+ " ref.analyt AS `" + GermplasmSearchDAO.REFERENCE + "` \n")
		;

		for (final String propertyId : addedColumnsPropertyIds) {
			if (GermplasmSearchDAO.selectClauseColumnsMap.containsKey(propertyId)) {
				selectClause.append(",");
				selectClause.append(GermplasmSearchDAO.selectClauseColumnsMap.get(propertyId));
			} else if (attributeTypesMap.containsKey(propertyId)) {
				selectClause.append(",");
				selectClause.append(String.format("`%s`.aval as `%<s` \n", propertyId));

			} else if (nameTypesMap.containsKey(propertyId)) {
				selectClause.append(",");
				selectClause.append(String.format("`%s`.nval as `%<s` \n", propertyId));
			}
		}

		return selectClause.toString();
	}

	protected GermplasmSearchResponse mapToGermplasmSearchResponse(final Object[] row, final List<String> addedColumnsPropertyIds,
		final Map<String, Integer> attributeTypesMap, final Map<String, Integer> nameTypesMap) {

		final GermplasmSearchResponse response = new GermplasmSearchResponse();

		final Germplasm germplasm = (Germplasm) row[0];

		response.setGermplasmUUID(germplasm.getGermplasmUUID());
		response.setGid(germplasm.getGid());
		response.setNames((String) row[1]);
		response.setGroupId(germplasm.getMgid());
		response.setLotCount(row[3] != null ? ((BigInteger) row[3]).intValue() : 0);
		response.setAvailableBalance((String) row[4]);
		response.setUnit((String) row[5]);
		response.setMethodName((String) row[6]);
		response.setLocationName((String) row[7]);
		response.setLocationId((Integer) row[8]);
		response.setBreedingMethodId((Integer) row[9]);
		response.setLocationAbbr((String) row[10]);
		response.setReference((String) row[11]);

		final int indexOffset = 12;
		response.setGermplasmDate(this.getValueOfAddedColumns(GERMPLASM_DATE, row, addedColumnsPropertyIds, indexOffset));
		response.setMethodCode(this.getValueOfAddedColumns(METHOD_ABBREVIATION, row, addedColumnsPropertyIds, indexOffset));
		response.setMethodNumber(this.getValueOfAddedColumns(METHOD_NUMBER, row, addedColumnsPropertyIds, indexOffset));
		response.setMethodGroup(this.getValueOfAddedColumns(METHOD_GROUP, row, addedColumnsPropertyIds, indexOffset));
		response.setGermplasmPreferredName(this.getValueOfAddedColumns(PREFERRED_NAME, row, addedColumnsPropertyIds, indexOffset));
		response.setGermplasmPreferredId(this.getValueOfAddedColumns(PREFERRED_ID, row, addedColumnsPropertyIds, indexOffset));
		response.setGroupSourceGID(this.getValueOfAddedColumns(GROUP_SOURCE_GID, row, addedColumnsPropertyIds, indexOffset));
		response.setGroupSourcePreferredName(
			this.getValueOfAddedColumns(GROUP_SOURCE_PREFERRED_NAME, row, addedColumnsPropertyIds, indexOffset));
		response.setImmediateSourceGID(this.getValueOfAddedColumns(IMMEDIATE_SOURCE_GID, row, addedColumnsPropertyIds, indexOffset));
		response.setImmediateSourceName(this.getValueOfAddedColumns(IMMEDIATE_SOURCE_NAME, row, addedColumnsPropertyIds, indexOffset));

		this.setValuesMapForAttributeAndNameTypes(germplasm, row, addedColumnsPropertyIds, attributeTypesMap, nameTypesMap, indexOffset);

		response.setAttributeTypesValueMap(germplasm.getAttributeTypesValueMap());
		response.setNameTypesValueMap(germplasm.getNameTypesValueMap());

		return response;
	}

	/**
	 * Filtered gids + associated gids (e.g pedigree, group).
	 */
	private List<Integer> retrieveSearchGids(final GermplasmSearchRequest germplasmSearchRequest,
		final Pageable pageable, final String programUUID) {

		final List<String> addedColumnsPropertyIds = germplasmSearchRequest.getAddedColumnsPropertyIds();
		final Map<String, Integer> attributeTypesMap = this.getAttributeTypesMap(addedColumnsPropertyIds, programUUID);
		final Map<String, Integer> nameTypesMap = this.getNameTypesMap(addedColumnsPropertyIds);

		// main query
		final StringBuilder queryBuilder =
			new StringBuilder(this.createSelectClause(addedColumnsPropertyIds, attributeTypesMap, nameTypesMap));
		queryBuilder.append(this.createFromClause(addedColumnsPropertyIds, attributeTypesMap, nameTypesMap));

		final List<Integer> preFilteredGids = new ArrayList<>();
		final boolean isPrefilterEmpty = this.addPreFilteredGids(germplasmSearchRequest, preFilteredGids, programUUID);

		if (isPrefilterEmpty) {
			return Collections.emptyList();
		}

		// group by inside filters
		addFilters(new SqlQueryParamBuilder(queryBuilder), germplasmSearchRequest, preFilteredGids, programUUID);

		final Map<String, Boolean> sortState = convertSort(pageable);
		if (!sortState.isEmpty() || pageable == null) {
			queryBuilder.append(LIMIT_CLAUSE);
		}

		final SQLQuery sqlQuery = this.getSession().createSQLQuery(queryBuilder.toString());
		sqlQuery.addScalar(GID);

		if (sortState.isEmpty() && pageable != null) {
			addPaginationToSQLQuery(sqlQuery, pageable);
		}
		addFilters(new SqlQueryParamBuilder(sqlQuery), germplasmSearchRequest, preFilteredGids, programUUID);

		final List<Integer> filteredGids = sqlQuery.list();

		if (filteredGids.isEmpty()) {
			return Collections.emptyList();
		}

		/*
		 * Included gids are only for the current page, for performance reasons.
		 * They are in separate queries instead of a subquery, because mysql cannot perform semijoin transformation with UNION
		 * https://dev.mysql.com/doc/refman/5.6/en/semijoins.html
		 */
		final List<Integer> pageGids = new ArrayList<>(filteredGids);
		pageGids.addAll(this.retrievePedigreeGids(filteredGids, germplasmSearchRequest));

		if (germplasmSearchRequest.isIncludeGroupMembers()) {
			pageGids.addAll(this.retrieveGIDGroupMemberResults(new HashSet<>(filteredGids)));
		}

		return pageGids;
	}

	public long countSearchGermplasm(final GermplasmSearchRequest germplasmSearchRequest, final String programUUID) {

		try {
			// Reusing the same query without filters is expensive. We create a simpler one for count all.
			// Even then, count takes ~ 15s in large db, so it's not executed by default
			if (germplasmSearchRequest == null) {
				final SQLQuery sqlQuery =
					this.getSession().createSQLQuery(" select count(gid) from germplsm g where 1 = 1 " + GERMPLASM_NOT_DELETED_CLAUSE);
				return ((BigInteger) sqlQuery.uniqueResult()).longValue();
			}

			final List<String> addedColumnsPropertyIds = germplasmSearchRequest.getAddedColumnsPropertyIds();
			final Map<String, Integer> attributeTypesMap = this.getAttributeTypesMap(addedColumnsPropertyIds, programUUID);
			final Map<String, Integer> nameTypesMap = this.getNameTypesMap(addedColumnsPropertyIds);

			// main query
			final StringBuilder queryBuilder =
				new StringBuilder(this.createSelectClause(addedColumnsPropertyIds, attributeTypesMap, nameTypesMap));
			queryBuilder.append(this.createFromClause(addedColumnsPropertyIds, attributeTypesMap, nameTypesMap));

			final List<Integer> preFilteredGids = new ArrayList<>();

			final boolean isPrefilterEmpty = this.addPreFilteredGids(germplasmSearchRequest, preFilteredGids, programUUID);
			if (isPrefilterEmpty) {
				return 0;
			}

			// group by inside filters
			addFilters(new SqlQueryParamBuilder(queryBuilder), germplasmSearchRequest, preFilteredGids, programUUID);

			// Filtered count is limited for performance
			// It's not possible to know the exact count if count > LIMIT
			final SQLQuery sqlQuery = this.getSession().createSQLQuery("select count(1) from ("
				+ queryBuilder.toString() + LIMIT_CLAUSE
				+ ") T");
			addFilters(new SqlQueryParamBuilder(sqlQuery), germplasmSearchRequest, preFilteredGids, programUUID);

			return ((BigInteger) sqlQuery.uniqueResult()).longValue();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error at GermplasmSearchDAO.countSearchGermplasm(): " + e.getMessage(), e);
		}
	}

	// TODO remove sortState dependency
	private static Map<String, Boolean> convertSort(final Pageable pageable) {
		final Map<String, Boolean> sortState = new HashMap<>();
		if (pageable == null || pageable.getSort() == null) {
			return Collections.emptyMap();
		}
		final Sort sort = pageable.getSort();
		final Iterator<Sort.Order> iterator = sort.iterator();
		while (iterator.hasNext()) {
			final Sort.Order next = iterator.next();
			sortState.put(next.getProperty(), next.getDirection().equals(Sort.Direction.ASC));
		}
		return sortState;
	}

	private static void addFilters(final SqlQueryParamBuilder paramBuilder, final GermplasmSearchRequest germplasmSearchRequest,
		final List<Integer> preFilteredGids, final String programUUID) {

		// Pre-WHERE filtering

		final Boolean withInventoryOnly = germplasmSearchRequest.getWithInventoryOnly();
		if (Boolean.TRUE.equals(withInventoryOnly)) {
			paramBuilder.append(" inner join (SELECT l.eid as GID from ims_lot l "
				+ " INNER JOIN ims_transaction t on l.lotid = t.lotid and t.trnstat <> " + STATUS_DELETED
				+ " AND l.etype = 'GERMPLSM' GROUP BY l.eid HAVING SUM(t.trnqty) > 0 )"
				+ " GermplasmWithInventory ON GermplasmWithInventory.GID = g.gid");
		}

		// WHERE filtering section
		// TODO verify which filters could perform better in the main join

		paramBuilder.append(" where 1 = 1 " + GERMPLASM_NOT_DELETED_CLAUSE);

		final SqlTextFilter nameFilter = germplasmSearchRequest.getNameFilter();
		if (nameFilter != null && nameFilter.getValue() != null && nameFilter.getType() != null) {
			final String q = nameFilter.getValue();
			final SqlTextFilter.Type type = nameFilter.getType();
			final String operator = getOperator(type);
			paramBuilder.setParameter("q", getParameter(type, q));
			paramBuilder.setParameter("qStandardized", getParameter(type, GermplasmDataManagerUtil.standardizeName(q)));
			paramBuilder.setParameter("qNoSpaces", getParameter(type, q.replaceAll(" ", "")));
			paramBuilder.append(" AND (allNames.nval " + operator + " :q "
				+ "OR allNames.nval " + operator + " :qStandardized "
				+ "OR allNames.nval " + operator + " :qNoSpaces) \n ");
		}

		final List<Integer> gids = germplasmSearchRequest.getGids();
		if (gids != null && !gids.isEmpty()) {
			paramBuilder.append(" and g.gid in (:gids)");
			paramBuilder.setParameterList("gids", gids);
		}

		final Integer gidFrom = germplasmSearchRequest.getGidFrom();
		if (gidFrom != null) {
			paramBuilder.append(" and g.gid >= :gidFrom");
			paramBuilder.setParameter("gidFrom", gidFrom);
		}

		final Integer gidTo = germplasmSearchRequest.getGidTo();
		if (gidTo != null) {
			paramBuilder.append(" and g.gid <= :gidTo");
			paramBuilder.setParameter("gidTo", gidTo);
		}

		final String germplasmUUID = germplasmSearchRequest.getGermplasmUUID();
		if (germplasmUUID != null) {
			paramBuilder.append(" and g.germplsm_uuid = :germplasmUUID");
			paramBuilder.setParameter("germplasmUUID", germplasmUUID);
		}

		final Integer groupId = germplasmSearchRequest.getGroupId();
		if (groupId != null) {
			paramBuilder.append(" and g.mgid = :groupId ");
			paramBuilder.setParameter("groupId", groupId);
		}

		final String sampleUID = germplasmSearchRequest.getSampleUID();
		if (sampleUID != null) {
			paramBuilder.append(" and exists(select 1" //
				+ " from nd_experiment filter_nde" //
				+ "  inner join stock filter_stock on filter_nde.stock_id = filter_stock.stock_id" //
				+ "  inner join sample filter_sample on filter_nde.nd_experiment_id = filter_sample.nd_experiment_id"
				+ " where filter_stock.dbxref_id = g.gid and filter_sample.sample_bk = :sampleUID) \n ");
			paramBuilder.setParameter("sampleUID", sampleUID);
		}

		final List<Integer> germplasmListIds = germplasmSearchRequest.getGermplasmListIds();
		if (germplasmListIds != null) {
			paramBuilder.append(" and g.gid IN (select filter_l.gid from listdata filter_l "  //
				+ "where filter_l.listid in (:germplasmListIds)) ");
			paramBuilder.setParameterList("germplasmListIds", germplasmListIds);
		}

		final String stockId = germplasmSearchRequest.getStockId();
		if (stockId != null) {
			paramBuilder.append(" and gl.stock_id like :stockId ");
			paramBuilder.setParameter("stockId", stockId + '%');
		}

		final String locationOfOrigin = germplasmSearchRequest.getLocationOfOrigin();
		if (locationOfOrigin != null) {
			paramBuilder.append(" and l.lname like :locationOfOrigin ");
			paramBuilder.setParameter("locationOfOrigin", '%' + locationOfOrigin + '%');
		}

		final String locationOfUse = germplasmSearchRequest.getLocationOfUse();
		if (locationOfUse != null) {
			paramBuilder.append(" and exists(select 1 from nd_experiment filter_nde" //
				+ " inner join nd_geolocation filter_ndgeo on filter_nde.nd_geolocation_id = filter_ndgeo.nd_geolocation_id"
				+ " inner join nd_geolocationprop filter_gp on filter_gp.nd_geolocation_id = filter_ndgeo.nd_geolocation_id "
				+ "            AND filter_gp.type_id = " + +TermId.LOCATION_ID.getId() //
				+ " inner join location filter_location on filter_location.locid =  filter_gp.value" //
				+ " inner join stock filter_stock on filter_nde.stock_id = filter_stock.stock_id" //
				+ " where filter_stock.dbxref_id = g.gid and filter_location.lname like :locationOfUse) \n ");
			paramBuilder.setParameter("locationOfUse", '%' + locationOfUse + '%');
		}

		final String reference = germplasmSearchRequest.getReference();
		if (!StringUtils.isEmpty(reference)) {
			paramBuilder.append(" and ref.analyt like :reference ");
			paramBuilder.setParameter("reference", '%' + reference + '%');
		}

		final List<Integer> studyOfOriginIds = germplasmSearchRequest.getStudyOfOriginIds();
		if (studyOfOriginIds != null) {
			paramBuilder.append(" and g.gid IN (select gss.gid " //
				+ " from project p" //
				+ "  inner join germplasm_study_source gss on gss.project_id = p.project_id " //
				+ " where p.project_id in (:studyOfOriginIds)) \n"); //
			paramBuilder.setParameterList("studyOfOriginIds", studyOfOriginIds);
		}

		final List<Integer> studyOfUseIds = germplasmSearchRequest.getStudyOfUseIds();
		if (studyOfUseIds != null) {
			paramBuilder.append(" and g.gid IN (select s.dbxref_id " //
				+ " from project p" //
				+ "	 inner join project plotdata on p.project_id = plotdata.study_id" //
				+ "  inner join nd_experiment nde on plotdata.project_id = nde.project_id" //
				+ "  inner join stock s on nde.stock_id = s.stock_id " //
				+ " where p.project_id in (:studyOfUseIds)) \n"); //
			paramBuilder.setParameterList("studyOfUseIds", studyOfUseIds);
		}

		final List<Integer> harvestingStudyIds = germplasmSearchRequest.getHarvestingStudyIds();
		if (harvestingStudyIds != null) {
			paramBuilder.append(" and gl.lotid IN (select lot.lotid" //
				+ " from project p" //
				+ "	 inner join project plotdata on p.project_id = plotdata.study_id" //
				+ "  inner join nd_experiment nde on plotdata.project_id = nde.project_id" //
				+ "  inner join ims_experiment_transaction iet on nde.nd_experiment_id = iet.nd_experiment_id" //
				+ "   and iet.type = " + ExperimentTransactionType.HARVESTING.getId() //
				+ "  inner join ims_transaction tr on iet.trnid = tr.trnid and tr.trnstat <> " + STATUS_DELETED //
				+ "  inner join ims_lot lot on tr.lotid = lot.lotid" //
				+ " where p.project_id in (:harvestingStudyIds)) \n"); //
			paramBuilder.setParameterList("harvestingStudyIds", harvestingStudyIds);
		}

		final List<Integer> plantingStudyIds = germplasmSearchRequest.getPlantingStudyIds();
		if (plantingStudyIds != null) {
			paramBuilder.append(" and gl.lotid IN (select lot.lotid" //
				+ " from project p" //
				+ "	 inner join project plotdata on p.project_id = plotdata.study_id" //
				+ "  inner join nd_experiment nde on plotdata.project_id = nde.project_id" //
				+ "  inner join ims_experiment_transaction iet on nde.nd_experiment_id = iet.nd_experiment_id" //
				+ "   and iet.type = " + ExperimentTransactionType.PLANTING.getId() //
				+ "  inner join ims_transaction tr on iet.trnid = tr.trnid and tr.trnstat <> " + STATUS_DELETED //
				+ "  inner join ims_lot lot on tr.lotid = lot.lotid" //
				+ " where p.project_id in (:plantingStudyIds)) \n"); //
			paramBuilder.setParameterList("plantingStudyIds", plantingStudyIds);
		}

		final String breedingMethodName = germplasmSearchRequest.getBreedingMethodName();
		if (breedingMethodName != null) {
			paramBuilder.append(" and m.mname like :breedingMethodName");
			paramBuilder.setParameter("breedingMethodName", '%' + breedingMethodName + '%');
		}

		final Date germplasmDateFrom = germplasmSearchRequest.getGermplasmDateFrom();
		if (germplasmDateFrom != null) {
			paramBuilder.append(" and g.gdate >= :germplasmDateFrom ");
			paramBuilder.setParameter("germplasmDateFrom", DATE_FORMAT.format(germplasmDateFrom));
		}

		final Date germplasmDateTo = germplasmSearchRequest.getGermplasmDateTo();
		if (germplasmDateTo != null) {
			paramBuilder.append(" and g.gdate <= :germplasmDateTo ");
			paramBuilder.setParameter("germplasmDateTo", DATE_FORMAT.format(germplasmDateTo));
		}

		final Boolean withRawObservationsOnly = germplasmSearchRequest.getWithRawObservationsOnly();
		if (Boolean.TRUE.equals(withRawObservationsOnly)) {
			paramBuilder.append(" and exists(select 1 from nd_experiment filter_nde" //
				+ "  inner join phenotype filter_phenotype on filter_phenotype.nd_experiment_id = filter_nde.nd_experiment_id" //
				+ "  inner join stock filter_stock on filter_nde.stock_id = filter_stock.stock_id" //
				+ " where filter_stock.dbxref_id = g.gid) \n ");
		}

		final Boolean withAnalyzedDataOnly = germplasmSearchRequest.getWithAnalyzedDataOnly();
		if (Boolean.TRUE.equals(withAnalyzedDataOnly)) {
			paramBuilder.append(" and exists(select 1 from project filter_project" //
				+ " inner join nd_experiment filter_nde on filter_project.project_id = filter_nde.project_id" //
				+ " inner join stock filter_stock on filter_nde.stock_id = filter_stock.stock_id" //
				+ " where filter_stock.dbxref_id = g.gid "  //
				+ "   and filter_project.dataset_type_id =  " + DatasetTypeEnum.MEANS_DATA.getId() + ") \n ");
		}

		final Boolean withSampleOnly = germplasmSearchRequest.getWithSampleOnly();
		if (Boolean.TRUE.equals(withSampleOnly)) {
			paramBuilder.append(" and exists(select 1" //
				+ " from nd_experiment filter_nde" //
				+ "  inner join stock filter_stock on filter_nde.stock_id = filter_stock.stock_id" //
				+ "  inner join sample filter_sample on filter_nde.nd_experiment_id = filter_sample.nd_experiment_id"
				+ " where filter_stock.dbxref_id = g.gid) \n ");
		}

		final Boolean inProgramListOnly = germplasmSearchRequest.getInProgramListOnly();
		if (Boolean.TRUE.equals(inProgramListOnly)) {
			paramBuilder.append(" and exists(select 1 from listdata filter_l "  //
				+ "  inner join listnms filter_listnms on filter_l.listid = filter_listnms.listid"
				+ " where filter_listnms.liststatus != 9 and filter_l.gid = g.gid and filter_listnms.program_uuid = :programUUID) ");
			paramBuilder.setParameter("programUUID", programUUID);
		}

		if (preFilteredGids != null && !preFilteredGids.isEmpty()) {
			paramBuilder.append(" and g.gid in (:preFilteredGids) ");
			paramBuilder.setParameterList("preFilteredGids", preFilteredGids);
		}

		final SqlTextFilter externalReferenceSource = germplasmSearchRequest.getExternalReferenceSource();
		if (externalReferenceSource != null) {
			final SqlTextFilter.Type type = externalReferenceSource.getType();
			paramBuilder.append(" and g.gid in (select exref.gid from external_reference_germplasm exref ");
			paramBuilder.append(" where exref.reference_source " + getOperator(type) + " :exrefSource) " );
			paramBuilder.setParameter("exrefSource", getParameter(type, externalReferenceSource.getValue()));
		}

		final SqlTextFilter externalReferenceId = germplasmSearchRequest.getExternalReferenceId();
		if (externalReferenceId != null) {
			final SqlTextFilter.Type type = externalReferenceId.getType();
			paramBuilder.append(" and g.gid in (select exref.gid from external_reference_germplasm exref ");
			paramBuilder.append(" where exref.reference_id " + getOperator(type) + " :exrefId) " );
			paramBuilder.setParameter("exrefId", getParameter(type, externalReferenceId.getValue()));
		}

		paramBuilder.append(" group by g.gid having 1 = 1 ");

		// Post-group-by filtering section

		// withInventoryOnly moved to pre-where

	}

	/**
	 * This query contains those filters that don't perform well in the main query.
	 * The queries are bounded by the LIMIT clause, and therefore they are less appropriate for filters
	 * that match a lot of records.
	 *
	 * @return true if request contains any prefiltering and it has no matches.
	 */
	boolean addPreFilteredGids(final GermplasmSearchRequest germplasmSearchRequest, final List<Integer> preFilteredGids, final String programUUID) {

		if (this.filterByFemaleParentName(germplasmSearchRequest, preFilteredGids)){
			return true;
		}

		if (this.filterByMaleParentName(germplasmSearchRequest, preFilteredGids)) {
			return true;
		}

		if (this.filterByGroupSourceName(germplasmSearchRequest, preFilteredGids)){
			return true;
		}

		if (this.filterByImmediateSourceName(germplasmSearchRequest, preFilteredGids)) {
			return true;
		}

		if (this.preFilterByAttributesTypes(germplasmSearchRequest, preFilteredGids, programUUID)) {
			return true;
		}

		if (this.preFilterByAttributesTypesRange(germplasmSearchRequest, preFilteredGids, programUUID)) {
			return true;
		}

		return this.filterByNameTypes(germplasmSearchRequest, preFilteredGids);
	}

	private boolean filterByNameTypes(final GermplasmSearchRequest germplasmSearchRequest, final List<Integer> preFilteredGids) {
		final Map<String, String> nameTypes = germplasmSearchRequest.getNameTypes();
		boolean filterApplied = false;
		if (!CollectionUtils.isEmpty(nameTypes)) {
			final StringBuilder queryBuilder = new StringBuilder();
			queryBuilder.append(" select distinct g.gid from germplsm g where  ");
			final Iterator<Map.Entry<String, String>> iterator = nameTypes.entrySet().iterator();
			while (iterator.hasNext()) {
				final Map.Entry<String, String> entry = iterator.next();
				queryBuilder.append(String.format("EXISTS (select 1 from names n \n"
					+ " inner join udflds u on n.ntype = u.fldno \n"
					+ " where n.gid = g.gid and u.fcode = :nameTypeKey%s and n.nval like :nameValue%<s )", entry.getKey()));
				if (iterator.hasNext()) {
					queryBuilder.append(" and ");
				}
			}
			queryBuilder.append(LIMIT_CLAUSE);

			final SQLQuery sqlQuery = this.getSession().createSQLQuery(queryBuilder.toString());
			for (final Map.Entry<String, String> entry : nameTypes.entrySet()) {
				sqlQuery.setParameter("nameTypeKey" + entry.getKey(), entry.getKey());
				sqlQuery.setParameter("nameValue" + entry.getKey(), '%' + entry.getValue() + '%');
			}

			final List<Integer> gids = sqlQuery.list();
			filterApplied = true;
			this.findIntersectionOfMatchedGids(gids, preFilteredGids);
		}
		return filterApplied && preFilteredGids.isEmpty();
	}

	/**
	 * Filter by germplasm attributes and passport descriptors
	 *
	 * @return true if request was prefiltered and it has no matches.
	 */
	private boolean preFilterByAttributesTypes(final GermplasmSearchRequest germplasmSearchRequest, final List<Integer> preFilteredGids,
		final String programUUID) {
		final Map<String, String> attributes = germplasmSearchRequest.getAttributes();
		boolean filterApplied = false;
		if (attributes != null && !attributes.isEmpty()) {
			final StringBuilder queryBuilder = new StringBuilder();
			queryBuilder.append(" select distinct a.gid from atributs a ");
			final Iterator<Map.Entry<String, String>> iterator = attributes.entrySet().iterator();
			int i = 0;
			while (iterator.hasNext()) {
				final Map.Entry<String, String> entry = iterator.next();
				// String.format relative indexing (%<s): argument for the previous format specifier is re-used
				queryBuilder.append(String.format(
					" inner join ( "
						+ "    select a.gid "
						+ "    from atributs a "
						+ "      INNER JOIN cvterm cv on a.atype = cv.cvterm_id "
						+ "      INNER JOIN cvtermprop cp ON cp.type_id = %s and cv.cvterm_id = cp.cvterm_id "
						+ "      LEFT JOIN variable_overrides vo ON vo.cvterm_id = cv.cvterm_id AND vo.program_uuid = :programUUID "
						+ "      INNER JOIN cvterm vartype on vartype.name = cp.value and vartype.cvterm_id in (%s, %s) "
						+ "    WHERE (cv.name = :attributeKey%s or vo.alias = :attributeKey%<s) and a.aval like :attributeValue%<s "
						+ "    %s "
						+ ") T%s on T%<s.gid = a.gid ",
					TermId.VARIABLE_TYPE.getId(),
					VariableType.GERMPLASM_PASSPORT.getId(),
					VariableType.GERMPLASM_ATTRIBUTE.getId(),
					entry.getKey(),
					LIMIT_CLAUSE,
					i++
				));
			}

			final SQLQuery sqlQuery = this.getSession().createSQLQuery(queryBuilder.toString());
			sqlQuery.setParameter("programUUID", programUUID);
			for (final Map.Entry<String, String> entry : attributes.entrySet()) {
				sqlQuery.setParameter("attributeKey" + entry.getKey(), entry.getKey());
				sqlQuery.setParameter("attributeValue" + entry.getKey(), '%' + entry.getValue() + '%');
			}
			final List<Integer> gids = sqlQuery.list();
			filterApplied = true;
			this.findIntersectionOfMatchedGids(gids, preFilteredGids);
		}
		return filterApplied && preFilteredGids.isEmpty();
	}

	private boolean preFilterByAttributesTypesRange(final GermplasmSearchRequest germplasmSearchRequest, final List<Integer> preFilteredGids,
											   final String programUUID) {
		final Map<String, GermplasmSearchRequest.AttributeRange> attributes = germplasmSearchRequest.getAttributeRangeMap();
		boolean filterApplied = false;
		if (attributes != null && !attributes.isEmpty()) {
			final StringBuilder queryBuilder = new StringBuilder();
			queryBuilder.append(" select distinct a.gid from atributs a ");
			final Iterator<Map.Entry<String, GermplasmSearchRequest.AttributeRange>> iterator = attributes.entrySet().iterator();
			int i = 0;
			while (iterator.hasNext()) {
				final Map.Entry<String, GermplasmSearchRequest.AttributeRange> entry = iterator.next();
				String attributeFromCondition = "";
				if (StringUtils.isNotEmpty(entry.getValue().getFromValue())) {
					attributeFromCondition = " and a.aval >= :attributeFromValue%<s ";
				}
				String attributeToCondition = "";
				if (StringUtils.isNotEmpty(entry.getValue().getToValue())) {
					attributeToCondition = " and :attributeToValue%<s >= a.aval ";
				}
				// String.format relative indexing (%<s): argument for the previous format specifier is re-used
				queryBuilder.append(String.format(
						" inner join ( "
								+ "    select a.gid "
								+ "    from atributs a "
								+ "      INNER JOIN cvterm cv on a.atype = cv.cvterm_id "
								+ "      INNER JOIN cvtermprop cp ON cp.type_id = %s and cv.cvterm_id = cp.cvterm_id "
								+ "      LEFT JOIN variable_overrides vo ON vo.cvterm_id = cv.cvterm_id AND vo.program_uuid = :programUUID "
								+ "      INNER JOIN cvterm vartype on vartype.name = cp.value and vartype.cvterm_id in (%s, %s) "
								+ "    WHERE (cv.name = :attributeKey%s or vo.alias = :attributeKey%<s) "
								+ attributeFromCondition
								+ attributeToCondition
								+ "    %s "
								+ ") T%s on T%<s.gid = a.gid ",
						TermId.VARIABLE_TYPE.getId(),
						VariableType.GERMPLASM_PASSPORT.getId(),
						VariableType.GERMPLASM_ATTRIBUTE.getId(),
						entry.getKey(),
						LIMIT_CLAUSE,
						i++
				));
			}

			final SQLQuery sqlQuery = this.getSession().createSQLQuery(queryBuilder.toString());
			sqlQuery.setParameter("programUUID", programUUID);
			for (final Map.Entry<String, GermplasmSearchRequest.AttributeRange> entry : attributes.entrySet()) {
				sqlQuery.setParameter("attributeKey" + entry.getKey(), entry.getKey());
				if (StringUtils.isNotEmpty(entry.getValue().getFromValue())) {
					sqlQuery.setParameter("attributeFromValue" + entry.getKey(), entry.getValue().getFromValue());
				}
				if (StringUtils.isNotEmpty(entry.getValue().getToValue())) {
					sqlQuery.setParameter("attributeToValue" + entry.getKey(), entry.getValue().getToValue());
				}

			}
			final List<Integer> gids = sqlQuery.list();
			filterApplied = true;
			this.findIntersectionOfMatchedGids(gids, preFilteredGids);
		}
		return filterApplied && preFilteredGids.isEmpty();
	}

	/**
	 * Filter by immediate source's preferred name
	 *
	 * @return true if request was prefiltered and it has no matches.
	 */
	private boolean filterByImmediateSourceName(final GermplasmSearchRequest germplasmSearchRequest, final List<Integer> preFilteredGids) {
		final SqlTextFilter immediateSourceName = germplasmSearchRequest.getImmediateSourceName();
		boolean filterApplied = false;
		if (immediateSourceName != null) {
			final SqlTextFilter.Type type = immediateSourceName.getType();
			final String value = immediateSourceName.getValue();
			final List<Integer> gids = this.getSession().createSQLQuery("select g.gid from names n \n"
				+ " straight_join germplsm immediate_source on n.gid = immediate_source.gid \n"
				+ " straight_join germplsm g on immediate_source.gid = g.gpid2 and g.gnpgs < 0 \n"
				+ " where n.nstat != " + STATUS_DELETED + " and n.nval " + getOperator(type) + " :immediateSourceName " + LIMIT_CLAUSE) //
				.setParameter("immediateSourceName", getParameter(type, value))
				.list();
			filterApplied = true;
			this.findIntersectionOfMatchedGids(gids, preFilteredGids);
		}
		return filterApplied && preFilteredGids.isEmpty();
	}

	/**
	 * Filter by group source's preferred name
	 *
	 * @return true if request was prefiltered and it has no matches.
	 */
	private boolean filterByGroupSourceName(final GermplasmSearchRequest germplasmSearchRequest, final List<Integer> preFilteredGids) {
		final SqlTextFilter groupSourceName = germplasmSearchRequest.getGroupSourceName();
		boolean filterApplied = false;
		if (groupSourceName != null) {
			final SqlTextFilter.Type type = groupSourceName.getType();
			final String value = groupSourceName.getValue();
			final List<Integer> gids = this.getSession().createSQLQuery("select g.gid from names n \n" //
				+ " straight_join germplsm group_source on n.gid = group_source.gid \n" //
				+ " straight_join germplsm g on group_source.gid = g.gpid1 and g.gnpgs < 0 \n" //
				+ " where n.nstat != " + STATUS_DELETED + " and n.nval " + getOperator(type) + " :groupSourceName " + LIMIT_CLAUSE) //
				.setParameter("groupSourceName", getParameter(type, value))
				.list();
			filterApplied = true;
			this.findIntersectionOfMatchedGids(gids, preFilteredGids);
		}
		return filterApplied && preFilteredGids.isEmpty();
	}

	/**
	 * Filter by male parent's preferred name
	 *
	 * @return true if request was prefiltered and it has no matches.
	 */
	private boolean filterByMaleParentName(final GermplasmSearchRequest germplasmSearchRequest, final List<Integer> preFilteredGids) {
		final SqlTextFilter maleParentName = germplasmSearchRequest.getMaleParentName();
		boolean filterApplied = false;
		if (maleParentName != null) {
			final SqlTextFilter.Type type = maleParentName.getType();
			final String value = maleParentName.getValue();
			final List<Integer> gids = this.getSession().createSQLQuery("select g.gid from names n \n" //
				+ "   straight_join germplsm male_parent on n.gid = male_parent.gid \n" //
				+ "   straight_join germplsm group_source on male_parent.gid = group_source.gpid2 and group_source.gnpgs > 0 \n" //
				+ "   straight_join germplsm g on g.gnpgs < 0 and group_source.gid = g.gpid1 \n" //
				+ "                            or g.gnpgs > 0 and group_source.gid = g.gid \n" //
				+ " where n.nstat != " + STATUS_DELETED + " and n.nval " + getOperator(type) + " :maleParentName " + LIMIT_CLAUSE) //
				.setParameter("maleParentName", getParameter(type, value)) //
				.list();
			filterApplied = true;
			this.findIntersectionOfMatchedGids(gids, preFilteredGids);
		}
		return filterApplied && preFilteredGids.isEmpty();
	}

	/**
	 * Filter by female parent's preferred name
	 *
	 * @return true if request was prefiltered and it has no matches.
	 */
	private boolean filterByFemaleParentName(final GermplasmSearchRequest germplasmSearchRequest, final List<Integer> preFilteredGids) {
		final SqlTextFilter femaleParentName = germplasmSearchRequest.getFemaleParentName();
		boolean filterApplied = false;
		if (femaleParentName != null) {
			final SqlTextFilter.Type type = femaleParentName.getType();
			final String value = femaleParentName.getValue();
			final List<Integer> gids = this.getSession().createSQLQuery("select g.gid from names n \n" //
				+ "   straight_join germplsm female_parent on n.gid = female_parent.gid \n" //
				+ "   straight_join germplsm group_source on female_parent.gid = group_source.gpid1 and group_source.gnpgs > 0 \n" //
				+ "   straight_join germplsm g on g.gnpgs < 0 and group_source.gid = g.gpid1 \n"  //
				+ "                            or g.gnpgs > 0 and group_source.gid = g.gid \n" //
				+ " where n.nstat != " + STATUS_DELETED + " and n.nval " + getOperator(type) + " :femaleParentName " + LIMIT_CLAUSE) //
				.setParameter("femaleParentName", getParameter(type, value)) //
				.list();
			filterApplied = true;
			this.findIntersectionOfMatchedGids(gids, preFilteredGids);
		}
		return filterApplied && preFilteredGids.isEmpty();
	}

	private void findIntersectionOfMatchedGids(final List<Integer> gidsMatchedForFilter, final List<Integer> preFilteredGids) {
		if (!CollectionUtils.isEmpty(gidsMatchedForFilter)) {
			if (preFilteredGids.isEmpty()) {
				preFilteredGids.addAll(gidsMatchedForFilter);
			} else {
				preFilteredGids.retainAll(gidsMatchedForFilter);
			}
		}
	}

	/**
	 * NOTE: This part of the query has performance implications. Please take this into consideration when making changes.
	 * The current setup has been tested on crop=brachiaria, #germplsm: ~ 6M, page size: 1000, include pedigree generative
	 * -> 5 levels: 7 sec, 20 levels: 30 sec
	 * <p>
	 * STRAIGHT_JOIN joins the table in left to right order.
	 * For some reason, with "inner join" the query optimizer chooses a suboptimal order and instead of starting with P0 (limited by where)
	 * it starts with P1, and the query never finishes. With straight_join, it finishes in seconds.
	 * <p>
	 * The gnpgs column also plays a key role in the running time. Some tested alternatives that don't make use of this column have shown
	 * poor performance.
	 * <p>
	 * The query doesn't exclude deleted germplasm (g.deleted and g.grplce), because it slows it down considerably.
	 * Deleted germplasm should be excluded by the caller later.
	 */
	private List<Integer> retrievePedigreeGids(final List<Integer> gids, final GermplasmSearchRequest germplasmSearchRequest) {

		final StringBuilder queryBuilder = new StringBuilder();

		final IncludePedigree includePedigree = germplasmSearchRequest.getIncludePedigree();

		if (includePedigree != null && includePedigree.getGenerationLevel() > 0) {

			// levels of pedigree to retrieve
			final int generationLevel = includePedigree.getGenerationLevel();
			// TODO extract max level as config (IBP-4013)
			if (generationLevel > 10) {
				throw new MiddlewareRequestException("", "error.pedigree.max.generation.level", new Integer[] {10});
			}

			int level = 1;
			switch (includePedigree.getType()) {
				// Consider germplasm generated using generative methods
				case GENERATIVE:
					while (level <= generationLevel) {
						if (!StringUtils.isBlank(queryBuilder.toString())) {
							queryBuilder.append(UNION);
						}
						queryBuilder.append(" select P" + level + ".gid from germplsm P0 \n ");

						// using some abbreviations for short-lived variables
						int i = 1;
						while (i <= level) {
							final int prev = i - 1;
							queryBuilder.append(String.format(" straight_join germplsm PGROUP%s", prev, prev) //
								// find the group source (last gid produced by a generative process)
								// join with itself if gid is already the group source
								+ String.format(" on ((P%s.gnpgs < 0 and PGROUP%s.gid = P%s.gpid1)", prev, prev, prev) //
								+ String.format("  or (P%s.gnpgs > 0 and PGROUP%s.gid = P%s.gid)) \n", prev, prev, prev) //
								// find the group source parents
								+ String.format(" straight_join germplsm P%s on PGROUP%s.gnpgs > 0", i, prev)
								+ String.format("  and (P%s.gid = PGROUP%s.gpid1 or P%s.gid = PGROUP%s.gpid2) \n ", i, prev, i, prev));
							/*
							 * trying to include other progenitors associated with PGROUP won't perform well here:
							 *    or PGROUP%s.gnpgs > 2 and exists(select 1 from progntrs ...
							 */
							i++;
						}
						queryBuilder.append(" where P0.gid in (:gids) \n ");

						level++;
					}
					break;

				case DERIVATIVE:
				case BOTH:

					/*
					 * DERIVATIVE:
					 * Consider germplasm generated using derivative and maintenance methods (gnpgs < 0)
					 * BOTH:
					 * traverse the pedigree tree considering both derivative and generative,
					 * without jumping to group source, considering every intermediate step.
					 */

					while (level <= generationLevel) {
						if (!StringUtils.isBlank(queryBuilder.toString())) {
							queryBuilder.append(UNION);
						}
						queryBuilder.append(" select P" + level + ".gid from germplsm P0 \n ");

						// using some abbreviations for short-lived variables
						int i = 1;
						while (i <= level) {
							final int prev = i - 1;
							queryBuilder.append(String.format(" straight_join germplsm P%s", i));
							queryBuilder.append(String.format(" on P%s.gnpgs < 0 and (", prev));
							queryBuilder.append(String.format("  P%s.gid = P%s.gpid2", i, prev));
							// some derivative/maintenance methods may produce gpid2=0
							// for that scenario, try to get the source using gpid1
							queryBuilder.append(String.format("  or P%s.gpid2 = 0 and P%s.gid = P%s.gpid1) \n ", prev, i, prev));
							if (IncludePedigree.Type.BOTH.equals(includePedigree.getType())) {
								queryBuilder.append(String.format("or P%s.gnpgs > 0 and (", prev));
								queryBuilder.append(String.format(" P%s.gid = P%s.gpid1 or P%s.gid = P%s.gpid2) \n ", i, prev, i, prev));
							}
							i++;
						}
						queryBuilder.append(" where P0.gid in (:gids) \n ");

						level++;
					}
					break;
			}

		}

		if (queryBuilder.toString().isEmpty()) {
			return Collections.emptyList();
		}

		final SQLQuery sqlQuery = this.getSession().createSQLQuery(queryBuilder.toString());
		sqlQuery.setParameterList("gids", gids);
		final List<Integer> pedigreeGids = sqlQuery.list();

		final List<Integer> gidsWithPedigree = new ArrayList<>(gids);
		gidsWithPedigree.addAll(pedigreeGids);
		pedigreeGids.addAll(this.retrieveOtherProgenitors(gidsWithPedigree));

		return pedigreeGids;
	}

	private List<Integer> retrieveOtherProgenitors(final List<Integer> gids) {
		if (gids == null || gids.isEmpty()) {
			return Collections.emptyList();
		}
		return this.getSession().createSQLQuery("select p.pid from progntrs p where p.gid in (:gids)") //
			.setParameterList("gids", gids) //
			.list();
	}

}
