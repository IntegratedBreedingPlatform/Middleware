package org.generationcp.middleware.dao;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import org.generationcp.middleware.domain.gms.search.GermplasmSearchParameter;
import org.generationcp.middleware.domain.gms.search.GermplasmSortableColumn;
import org.generationcp.middleware.domain.inventory.GermplasmInventory;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.GermplasmDataManagerUtil;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.util.Debug;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	private static final String Q_NO_SPACES = "qNoSpaces";
	private static final String Q_STANDARDIZED = "qStandardized";

	public static final String NAMES = "NAMES";
	public static final String GID = "GID";
	public static final String GROUP_ID = "GROUP ID";
	public static final String STOCK_IDS = "STOCKID";
	public static final String AVAIL_LOTS = "LOTS";
	public static final String AVAIL_BALANCE = "AVAILABLE";
	public static final String METHOD_NAME = "METHOD NAME";
	public static final String LOCATION_NAME = "LOCATIONS";
	public static final String METHOD_ABBREVIATION = "METHOD ABBREV";
	public static final String METHOD_NUMBER = "METHOD NUMBER";
	public static final String METHOD_GROUP = "METHOD GROUP";
	public static final String PREFERRED_NAME = "PREFERRED NAME";
	public static final String PREFERRED_ID = "PREFERRED ID";
	public static final String FEMALE_PARENT_ID = "CROSS-FEMALE GID";
	public static final String FEMALE_PARENT_PREFERRED_NAME = "CROSS-FEMALE PREFERRED NAME";
	public static final String MALE_PARENT_ID = "CROSS-MALE GID";
	public static final String MALE_PARENT_PREFERRED_NAME = "CROSS-MALE PREFERRED NAME";
	public static final String GERMPLASM_DATE = "GERMPLASM DATE";

	@SuppressWarnings("unchecked")
	public List<Germplasm> searchForGermplasms(final GermplasmSearchParameter germplasmSearchParameter) {

		// actual searching here

		final Integer startingRow = germplasmSearchParameter.getStartingRow();
		final Integer noOfEntries = germplasmSearchParameter.getNumberOfEntries();

		try {
			final Set<Germplasm> germplasmSearchResult = new LinkedHashSet<>();
			final Set<Integer> gidSearchResult = this.retrieveGIDSearchResults(germplasmSearchParameter);
			// return an empty germplasm list when there is no GID search results returned
			if (gidSearchResult.isEmpty()) {
				return new ArrayList<>(germplasmSearchResult);
			}

			final SQLQuery query = this.getSession().createSQLQuery(createGermplasmSearchQueryString(germplasmSearchParameter));

			Debug.println(query.getQueryString());

			query.setParameterList("gids", gidSearchResult);
			query.addEntity(GermplasmSearchDAO.GERMPLSM, Germplasm.class);
			query.addScalar(GermplasmSearchDAO.NAMES);
			query.addScalar(GermplasmSearchDAO.STOCK_IDS);
			query.addScalar(GermplasmSearchDAO.GID);
			query.addScalar(GermplasmSearchDAO.GROUP_ID);
			query.addScalar(GermplasmSearchDAO.AVAIL_LOTS);
			query.addScalar(GermplasmSearchDAO.AVAIL_BALANCE);
			query.addScalar(GermplasmSearchDAO.METHOD_NAME);
			query.addScalar(GermplasmSearchDAO.LOCATION_NAME);

			for (String propertyId : germplasmSearchParameter.getAddedColumnsPropertyIds()) {
				query.addScalar(propertyId);
			}

			query.setFirstResult(startingRow);
			query.setMaxResults(noOfEntries);

			germplasmSearchResult
					.addAll(this.getSearchForGermplasmsResult(query.list(), germplasmSearchParameter.getAddedColumnsPropertyIds(), germplasmSearchParameter.getAttributeTypesMap()));
			return new ArrayList<>(germplasmSearchResult);

		} catch (final HibernateException e) {
			String message = "Error with searchForGermplasms(" + germplasmSearchParameter.getSearchKeyword() + ") " + e.getMessage();
			GermplasmSearchDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	@SuppressWarnings("unchecked")
	public Set<Integer> retrieveGIDSearchResults(final GermplasmSearchParameter germplasmSearchParameter) {

		final String q = germplasmSearchParameter.getSearchKeyword().trim();
		final Operation o = germplasmSearchParameter.getOperation();
		final boolean includeParents = germplasmSearchParameter.isIncludeParents();
		final boolean withInventoryOnly = germplasmSearchParameter.isWithInventoryOnly();
		final boolean includeMGMembers = germplasmSearchParameter.isIncludeMGMembers();

		// return empty search results when keyword is blank or an empty string
		if ("".equals(q)) {
			return new HashSet<>();
		}

		try {
			final Set<Integer> gidSearchResults = new HashSet<>();
			final StringBuilder queryString = new StringBuilder();
			final Map<String, String> params = new HashMap<>();

			queryString.append("SELECT GermplasmSearchResults.GID FROM (");

			// 1. find germplasms with GID = or like q
			searchInGidCriteria(queryString, params, q, o);

			// 2. find germplasms with inventory_id = or like q
			searchInStockIdCriteria(queryString, params, q, o);

			// 3. find germplasms with nVal = or like q
			searchInNamesCriteria(queryString, params, q, o);

			queryString.append(") GermplasmSearchResults ");

			if (withInventoryOnly) {
				queryString.append(" INNER JOIN ");
				queryString
						.append("(SELECT l.eid as GID from ims_lot l INNER JOIN ims_transaction t on l.lotid = t.lotid AND l.etype = 'GERMPLSM' GROUP BY l.eid HAVING SUM(t.trnqty) > 0 ) ");
				queryString.append(" GermplasmWithInventory ON GermplasmSearchResults.GID = GermplasmWithInventory.GID ");
			}

			final SQLQuery query = this.getSession().createSQLQuery(queryString.toString());
			for (final Map.Entry<String, String> param : params.entrySet()) {
				query.setParameter(param.getKey(), param.getValue());
			}

			gidSearchResults.addAll(query.list());

			if (includeParents && !gidSearchResults.isEmpty()) {
				gidSearchResults.addAll(this.retrieveGIDParentsResults(gidSearchResults));
			}

			if (includeMGMembers && !gidSearchResults.isEmpty()) {
				gidSearchResults.addAll(this.retrieveGIDGroupMemberResults(gidSearchResults));
			}

			return gidSearchResults;
		} catch (final HibernateException e) {
			String message = "Error with retrieveGIDSearchResults(" + q + ") " + e.getMessage();
			GermplasmSearchDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public Integer countSearchForGermplasms(final GermplasmSearchParameter germplasmSearchParameter) {

		final Monitor countSearchForGermplasms = MonitorFactory.start("Method Started : countSearchForGermplasms ");

		Integer searchResultsCount = 0;

		final Set<Integer> gidSearchResults = this.retrieveGIDSearchResults(germplasmSearchParameter);

		searchResultsCount = gidSearchResults.size();

		GermplasmSearchDAO.LOG.debug("Method End : countSearchForGermplasms " + countSearchForGermplasms.stop());

		return searchResultsCount;
	}

	private void searchInGidCriteria(final StringBuilder queryString, final Map<String, String> params, final String q, final Operation o) {

		if (q.matches("(-)?(%)?[(\\d+)(%|_)?]*(%)?")) {
			queryString.append("SELECT g.gid as GID FROM germplsm g ");
			if (o.equals(Operation.LIKE)) {
				queryString.append("WHERE g.gid like :gid ");
				params.put("gid", q);
			} else {
				queryString.append("WHERE g.gid=:gid AND length(g.gid) = :gidLength ");
				params.put("gidLength", String.valueOf(q.length()));
				params.put("gid", q);
			}
			// make sure to not include deleted germplasm from the search results
			queryString.append(GERMPLASM_NOT_DELETED_CLAUSE + LIMIT_CLAUSE);
			queryString.append(UNION);
		}
	}

	private void searchInStockIdCriteria(final StringBuilder queryString, final Map<String, String> params, final String q,
			final Operation o) {

		queryString.append("SELECT eid as GID FROM ims_lot l " + "INNER JOIN germplsm g on l.eid = g.gid "
				+ "INNER JOIN ims_transaction t on l.lotid = t.lotid AND l.etype = 'GERMPLSM' ");
		if (o.equals(Operation.LIKE)) {
			queryString.append("WHERE t.inventory_id LIKE :inventory_id");
		} else {
			queryString.append("WHERE t.inventory_id = :inventory_id");
		}
		params.put("inventory_id", q);

		// make sure to not include deleted germplasm from the search results
		queryString.append(GERMPLASM_NOT_DELETED_CLAUSE + LIMIT_CLAUSE);
		queryString.append(UNION);
	}

	private void searchInNamesCriteria(final StringBuilder queryString, final Map<String, String> params, final String q,
			final Operation o) {

		queryString.append("SELECT n.gid as GID FROM names n ");
		queryString.append("INNER JOIN germplsm g on n.gid = g.gid ");
		if (o.equals(Operation.LIKE)) {
			queryString
					.append("WHERE n.nstat != :deletedStatus AND (n.nval LIKE :q OR n.nval LIKE :qStandardized OR n.nval LIKE :qNoSpaces)");
		} else {
			queryString.append("WHERE n.nstat != :deletedStatus AND (n.nval = :q OR n.nval = :qStandardized OR n.nval = :qNoSpaces)");
		}
		params.put("q", q);
		params.put(GermplasmSearchDAO.Q_NO_SPACES, q.replaceAll(" ", ""));
		params.put(GermplasmSearchDAO.Q_STANDARDIZED, GermplasmDataManagerUtil.standardizeName(q));
		params.put("deletedStatus", GermplasmSearchDAO.STATUS_DELETED);

		// make sure to not include deleted germplasm from the search results
		queryString.append(GERMPLASM_NOT_DELETED_CLAUSE + LIMIT_CLAUSE);
	}

	@SuppressWarnings("unchecked")
	private Set<Integer> retrieveGIDParentsResults(final Set<Integer> gidSearchResults) {
		try {
			final Set<Integer> gidParentsSearchResults = new HashSet<>();
			final StringBuilder queryString = new StringBuilder();
			queryString.append("SELECT GermplasmParents.GID FROM (");

			// female parent
			queryString.append("SELECT g.gpid1 as GID from germplsm g where g.gpid1 != 0 AND g.gid IN (:gids) ");
			queryString.append(UNION);

			// male parent
			queryString.append("SELECT g.gpid2 as GID from germplsm g where g.gpid1 != 0 AND g.gid IN (:gids) ");
			queryString.append(UNION);

			// other progenitors
			queryString.append("SELECT p.gid as GID from progntrs p where p.gid IN (:gids)");
			queryString.append(") GermplasmParents "
					+ "INNER JOIN germplsm g on GermplasmParents.GID = g.gid AND  g.deleted = 0  AND g.grplce = 0");

			final SQLQuery query = this.getSession().createSQLQuery(queryString.toString());
			query.setParameterList("gids", gidSearchResults);

			gidParentsSearchResults.addAll(query.list());

			return gidParentsSearchResults;
		} catch (final HibernateException e) {
			String message = "Error with retrieveGIDParentsResults(GIDS=" + gidSearchResults + ") : " + e.getMessage();
			GermplasmSearchDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	@SuppressWarnings("unchecked")
	private Set<Integer> retrieveGIDGroupMemberResults(final Set<Integer> gidSearchResults) {
		try {
			final Set<Integer> gidGroupMembersSearchResults = new HashSet<Integer>();
			final StringBuilder queryString = new StringBuilder();
			queryString.append("SELECT members.gid FROM germplsm members WHERE members.deleted = 0 AND members.grplce = 0 "
					+ "AND members.mgid IN (select g.mgid from germplsm g where g.gid IN (:gids) and g.mgid != 0)");

			final SQLQuery query = this.getSession().createSQLQuery(queryString.toString());
			query.setParameterList("gids", gidSearchResults);

			gidGroupMembersSearchResults.addAll(query.list());
			return gidGroupMembersSearchResults;

		} catch (final HibernateException e) {
			String message = "Error with retrieveGIDGroupMemberResults(GIDS=" + gidSearchResults + ") : " + e.getMessage();
			GermplasmSearchDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	protected String createGermplasmSearchQueryString(final GermplasmSearchParameter germplasmSearchParameter) {

		final StringBuilder mainQuery = new StringBuilder();

		mainQuery.append(createSelectClauseForGermplasmSearch(germplasmSearchParameter.getAddedColumnsPropertyIds(), germplasmSearchParameter.getAttributeTypesMap()));
		mainQuery.append(createFromClauseForGermplasmSearch(germplasmSearchParameter.getAddedColumnsPropertyIds(), germplasmSearchParameter.getAttributeTypesMap()));
		mainQuery.append("WHERE g.gid IN (:gids) GROUP BY g.gid");
		mainQuery.append(this.addSortingColumns(germplasmSearchParameter.getSortState(), germplasmSearchParameter.getAttributeTypesMap()));

		return mainQuery.toString();

	}

	protected String createFromClauseForGermplasmSearch(final List<String> addedColumnsPropertyIds,
			final Map<String, Integer> attributeTypesMap) {

		Map<String, String> fromClauseColumnsMap = new HashMap<>();

		fromClauseColumnsMap.put(PREFERRED_NAME, "LEFT JOIN `names` nameOfGermplasm \n" + "              ON g.gid = nameOfGermplasm.gid \n"
				+ "                 AND nameOfGermplasm.nstat = 1 \n");
		fromClauseColumnsMap.put(PREFERRED_ID,
				"LEFT JOIN `names` preferredIdOfGermplasm \n" + "              ON g.gid = preferredIdOfGermplasm.gid \n"
						+ "                 AND preferredIdOfGermplasm.nstat = 8 \n");
		fromClauseColumnsMap.put(FEMALE_PARENT_PREFERRED_NAME,
				"LEFT JOIN `names` nameOfFemaleParent \n" + "              ON g.gpid1 = nameOfFemaleParent.gid \n"
						+ "                 AND nameOfFemaleParent.nstat = 1 \n");
		fromClauseColumnsMap.put(MALE_PARENT_PREFERRED_NAME,
				"LEFT JOIN `names` nameOfMaleParent \n" + "              ON g.gpid2 = nameOfMaleParent.gid \n"
						+ "                 AND nameOfMaleParent.nstat = 1 \n");

		StringBuilder fromClause = new StringBuilder();
		fromClause.append("FROM   germplsm g \n" + "       LEFT JOIN ims_lot gl \n"
				+ "              ON gl.eid = g.gid AND gl.etype = 'GERMPLSM' AND gl.status = 0 \n"
				+ "       LEFT JOIN ims_transaction gt \n" + "              ON gt.lotid = gl.lotid AND gt.trnstat <> 9 \n"
				+ "       LEFT JOIN methods m \n" + "              ON m.mid = g.methn \n" + "       LEFT JOIN location l \n"
				+ "              ON l.locid = g.glocn \n" + "       LEFT JOIN `names` allNames  \n"
				+ "              ON g.gid = allNames.gid \n");

		for (String propertyId : addedColumnsPropertyIds) {
			if (fromClauseColumnsMap.containsKey(propertyId)) {
				fromClause.append(fromClauseColumnsMap.get(propertyId));
			} else if (attributeTypesMap.containsKey(propertyId)) {
				fromClause.append(String.format("LEFT JOIN `atributs` `%s` on `%<s`.gid = g.gid and `%<s`.atype = %s \n", propertyId , attributeTypesMap.get(propertyId)));
			}
		}

		return fromClause.toString();

	}

	protected String createSelectClauseForGermplasmSearch(final List<String> addedColumnsPropertyIds,
			final Map<String, Integer> attributeTypesMap) {

		Map<String, String> selectClauseColumnsMap = new HashMap<>();

		selectClauseColumnsMap.put(METHOD_ABBREVIATION, String.format("m.mcode AS `%s` \n", METHOD_ABBREVIATION));
		selectClauseColumnsMap.put(METHOD_NUMBER, String.format("m.mid AS `%s` \n", METHOD_NUMBER));
		selectClauseColumnsMap.put(METHOD_GROUP, String.format("m.mgrp AS `%s` \n", METHOD_GROUP));
		selectClauseColumnsMap.put(PREFERRED_ID, String.format("preferredIdOfGermplasm.nval AS `%s` \n", PREFERRED_ID));
		selectClauseColumnsMap.put(PREFERRED_NAME, String.format("nameOfGermplasm.nval AS `%s` \n", PREFERRED_NAME));
		selectClauseColumnsMap.put(GERMPLASM_DATE, String.format("g.gdate AS `%s` \n", GERMPLASM_DATE));
		selectClauseColumnsMap.put(FEMALE_PARENT_ID, String.format(
				"       CASE \n" + "         WHEN g.gnpgs >= 2 \n" + "              AND g.gpid1 IS NOT NULL \n"
						+ "              AND g.gpid1 <> 0 THEN g.gpid1 \n" + "         ELSE '-' \n"
						+ "       END                         AS `%s` \n", FEMALE_PARENT_ID));
		selectClauseColumnsMap.put(FEMALE_PARENT_PREFERRED_NAME, String.format(
				"       CASE \n" + "         WHEN g.gnpgs >= 2 \n" + "              AND g.gpid1 IS NOT NULL \n"
						+ "              AND g.gpid1 <> 0 THEN nameOfFemaleParent.nval \n" + "         ELSE '-' \n"
						+ "       END                         AS `%s` \n", FEMALE_PARENT_PREFERRED_NAME));
		selectClauseColumnsMap.put(MALE_PARENT_ID, String.format(
				"        CASE \n" + "         WHEN g.gnpgs >= 2 \n" + "              AND g.gpid2 IS NOT NULL \n"
						+ "              AND g.gpid2 <> 0 THEN g.gpid2 \n" + "         ELSE '-' \n"
						+ "       END                         AS `%s` \n", MALE_PARENT_ID));
		selectClauseColumnsMap.put(MALE_PARENT_PREFERRED_NAME, String.format(
				"       CASE \n" + "         WHEN g.gnpgs >= 2 \n" + "              AND g.gpid2 IS NOT NULL \n"
						+ "              AND g.gpid2 <> 0 THEN nameOfMaleParent.nval \n" + "         ELSE '-' \n"
						+ "       END                         AS `%s` \n", MALE_PARENT_PREFERRED_NAME));

		StringBuilder selectClause = new StringBuilder();
		selectClause.append("SELECT g.*, \n" + " Group_concat(DISTINCT allNames.nval ORDER BY allNames.nval SEPARATOR" + "       ', ')\n"
				+ "                                   AS `" + NAMES + "`, \n"
				+ "       Group_concat(DISTINCT gt.inventory_id ORDER BY gt.inventory_id SEPARATOR \n" + "       ', ') \n"
				+ "                                   AS `" + STOCK_IDS + "`, \n"
				+ "       g.gid                     AS `" + GID + "`, \n"
				+ "       g.mgid                     AS `" + GROUP_ID + "`, \n"
				+ "       Count(DISTINCT gt.lotid)    AS `" + AVAIL_LOTS + "`, \n"
				+ "       Sum(gt.trnqty)              AS `" + AVAIL_BALANCE + "`, \n"
				+ "       m.mname                     AS `" + METHOD_NAME + "`, \n"
				+ "       l.lname                     AS `" + LOCATION_NAME + "` \n");

		for (String propertyId : addedColumnsPropertyIds) {
			if (selectClauseColumnsMap.containsKey(propertyId)) {
				selectClause.append(",");
				selectClause.append(selectClauseColumnsMap.get(propertyId));
			} else if (attributeTypesMap.containsKey(propertyId)) {
				selectClause.append(",");
				selectClause.append(String.format("`%s`.aval as `%<s` \n", propertyId));
			}
		}

		return selectClause.toString();
	}

	protected String addSortingColumns(final Map<String, Boolean> sortState, final Map<String, Integer> attributeTypesMap) {
		if (sortState.isEmpty()) {
			return "";
		}

		final StringBuilder sortingQuery = new StringBuilder();
		sortingQuery.append(" ORDER BY ");
		for (final Map.Entry<String, Boolean> sortCondition : sortState.entrySet()) {
			final String order = sortCondition.getValue().equals(true) ? "ASC" : "DESC";

			if (attributeTypesMap.containsKey(sortCondition.getKey())) {
				sortingQuery.append(String.format(" `%s`", sortCondition.getKey()));
			} else {
				sortingQuery.append(String.format(" `%s`", GermplasmSortableColumn.get(sortCondition.getKey()).getDbColumnName()));
			}

			sortingQuery.append(" " + order);
			sortingQuery.append(",");
		}


		String sortingQueryStr = sortingQuery.toString();
		// remove unnecessary ',' at end of sorting query
		sortingQueryStr = sortingQueryStr.substring(0, sortingQuery.lastIndexOf(","));
		return sortingQueryStr;
	}

	protected List<Germplasm> getSearchForGermplasmsResult(final List<Object[]> result, final List<String> addedColumnsPropertyIds,
			final Map<String, Integer> attributeTypesMap) {
		final List<Germplasm> germplasms = new ArrayList<>();
		if (result != null) {
			for (final Object[] row : result) {
				germplasms.add(this.mapToGermplasm(row, addedColumnsPropertyIds, attributeTypesMap));
			}
		}
		return germplasms;
	}

	protected Germplasm mapToGermplasm(final Object[] row, final List<String> addedColumnsPropertyIds,
			final Map<String, Integer> attributeTypesMap) {
		final Germplasm germplasm = (Germplasm) row[0];
		final GermplasmInventory inventoryInfo = new GermplasmInventory(germplasm.getGid());
		germplasm.setGermplasmNamesString(row[1] != null ? String.valueOf(row[1]) : "");
		inventoryInfo.setStockIDs((String) row[2]);
		inventoryInfo.setActualInventoryLotCount(row[5] != null ? ((BigInteger) row[5]).intValue() : 0);
		inventoryInfo.setTotalAvailableBalance(row[6] != null ? (Double) row[6] : 0d);
		germplasm.setInventoryInfo(inventoryInfo);
		germplasm.setMethodName(row[7] != null ? String.valueOf(row[7]) : "");
		germplasm.setLocationName(row[8] != null ? String.valueOf(row[8]) : "");

		germplasm.setGermplasmDate(getValueOfAddedColumns(GERMPLASM_DATE, row, addedColumnsPropertyIds));
		germplasm.setMethodCode(getValueOfAddedColumns(METHOD_ABBREVIATION, row, addedColumnsPropertyIds));
		germplasm.setMethodNumber(getValueOfAddedColumns(METHOD_NUMBER, row, addedColumnsPropertyIds));
		germplasm.setMethodGroup(getValueOfAddedColumns(METHOD_GROUP, row, addedColumnsPropertyIds));
		germplasm.setGermplasmPeferredName(getValueOfAddedColumns(PREFERRED_NAME, row, addedColumnsPropertyIds));
		germplasm.setGermplasmPeferredId(getValueOfAddedColumns(PREFERRED_ID, row, addedColumnsPropertyIds));
		germplasm.setFemaleParentPreferredID(getValueOfAddedColumns(FEMALE_PARENT_ID, row, addedColumnsPropertyIds));
		germplasm.setFemaleParentPreferredName(getValueOfAddedColumns(FEMALE_PARENT_PREFERRED_NAME, row, addedColumnsPropertyIds));
		germplasm.setMaleParentPreferredID(getValueOfAddedColumns(MALE_PARENT_ID, row, addedColumnsPropertyIds));
		germplasm.setMaleParentPreferredName(getValueOfAddedColumns(MALE_PARENT_PREFERRED_NAME, row, addedColumnsPropertyIds));
		germplasm.setAttributeTypesValueMap(createAttributeTypesValueMap(row, addedColumnsPropertyIds, attributeTypesMap));

		return germplasm;
	}

	protected Map<String,String> createAttributeTypesValueMap(final Object[] row, final List<String> addedColumnsPropertyIds, final Map<String, Integer> attributeTypesMap) {

		Map<String, String> attributeTypesValueMap = new HashMap<>();
		for (String propertyId : addedColumnsPropertyIds) {
			if (attributeTypesMap.containsKey(propertyId)) {
				attributeTypesValueMap.put(propertyId, getValueOfAddedColumns(propertyId, row, addedColumnsPropertyIds));
			}

		}
		return attributeTypesValueMap;

	}

	protected String getValueOfAddedColumns(String propertyId, final Object[] row, final List<String> addedColumnsPropertyIds) {

		if (addedColumnsPropertyIds.contains(propertyId)) {
			int addedColumnIndex = (addedColumnsPropertyIds.indexOf(propertyId) + 9);

			return row[addedColumnIndex] != null ? String.valueOf(row[addedColumnIndex]) : "";
		} else {
			return null;
		}


	}

}
