
package org.generationcp.middleware.dao;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.api.germplasm.search.GermplasmSearchRequest.IncludePedigree;
import org.generationcp.middleware.api.germplasm.search.GermplasmSearchResponse;
import org.generationcp.middleware.constant.ColumnLabels;
import org.generationcp.middleware.api.germplasm.search.GermplasmSearchRequest;
import org.generationcp.middleware.domain.gms.search.GermplasmSearchParameter;
import org.generationcp.middleware.domain.gms.search.GermplasmSortableColumn;
import org.generationcp.middleware.domain.inventory.GermplasmInventory;
import org.generationcp.middleware.domain.sqlfilter.SqlTextFilter;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.exceptions.MiddlewareRequestException;
import org.generationcp.middleware.manager.GermplasmDataManagerUtil;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.util.Debug;
import org.generationcp.middleware.util.SqlQueryParamBuilder;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * DAO class for Germplasm Search functionality.
 */
public class GermplasmSearchDAO extends GenericDAO<Germplasm, Integer> {

    private static final String ATRIBUTS = "ATRIBUTS";

    private static final Logger LOG = LoggerFactory.getLogger(GermplasmSearchDAO.class);

    // Prevent silly searches from resulting in GIANT IN clauses in search query (which reuses this function).
    // Old search query had the same hardcoded limit of 5000 anyway so this is not changing existing logic as such. Applies to both
    // count and search queries. In future we can detect that the search is resulting in more than 5000 matches and go back to the
    // user asking them to refine search criteria.
    private static final String LIMIT_CLAUSE = " LIMIT 5000 ";

    private static final int GERMPLASM_NAMES_INDEX = 1;

    private static final String UNION = " UNION ";
    private static final String GERMPLASM_NOT_DELETED_CLAUSE = " AND  g.deleted = 0  AND g.grplce = 0 ";
    private static final String STATUS_DELETED = "9";
    private static final String GERMPLSM = "germplsm";
    private static final String Q_NO_SPACES = "qNoSpaces";
    private static final String Q_STANDARDIZED = "qStandardized";

    public static final String NAMES = "NAMES";
    public static final String GID = ColumnLabels.GID.getName();
    public static final String GROUP_ID = ColumnLabels.GROUP_ID.getName();
    public static final String STOCK_IDS = ColumnLabels.STOCKID.getName();
    public static final String AVAIL_LOTS = ColumnLabels.AVAILABLE_INVENTORY.getName();
    public static final String AVAIL_BALANCE = ColumnLabels.TOTAL.getName();
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
    public static final String IMMEDIATE_SOURCE_PREFERRED_NAME = ColumnLabels.IMMEDIATE_SOURCE_PREFERRED_NAME.getName();

    private static final int STOCKID_INDEX = 2;
    private static final int LOT_INDEX = 5;
    private static final int AVAIL_BALANCE_INDEX = 6;
    private static final Map<String, String> selectClauseColumnsMap = new HashMap<>();

    //Excluding Parents Property from SQL
    private static final List<String> GERMPLASM_TREE_NODE_PROPERTY_IDS = Collections.unmodifiableList(Arrays.asList(GermplasmSearchDAO.MALE_PARENT_ID,
        GermplasmSearchDAO.FEMALE_PARENT_ID,
        GermplasmSearchDAO.MALE_PARENT_PREFERRED_NAME,
        GermplasmSearchDAO.FEMALE_PARENT_PREFERRED_NAME));

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
                .put(GermplasmSearchDAO.IMMEDIATE_SOURCE_PREFERRED_NAME,
                        String.format(" CASE \n WHEN g.gnpgs = -1 AND g.gpid2 IS NOT NULL \n"
                                        + "	AND g.gpid2 <> 0 THEN immediateSource.nval \n" + "	ELSE '-' \n END AS `%s` \n",
                                GermplasmSearchDAO.IMMEDIATE_SOURCE_PREFERRED_NAME));

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
        GermplasmSearchDAO.fromClauseColumnsMap.put(GermplasmSearchDAO.IMMEDIATE_SOURCE_PREFERRED_NAME,
                "LEFT JOIN names immediateSource ON g.gpid2 = immediateSource.gid AND immediateSource.nstat = 1 \n");

    }

    // TODO Remove (see searchGermplasm)
    @Deprecated
    public List<Germplasm> searchForGermplasms(final GermplasmSearchParameter germplasmSearchParameter) {

        // actual searching here

        final int startingRow = germplasmSearchParameter.getStartingRow();
        final int noOfEntries = germplasmSearchParameter.getNumberOfEntries();

        try {

            final Set<Germplasm> germplasmSearchResult = new LinkedHashSet<>();
            final Set<Integer> gidSearchResult = this.retrieveGIDSearchResults(germplasmSearchParameter);
            // return an empty germplasm list when there is no GID search results returned
            if (gidSearchResult.isEmpty()) {
                return new ArrayList<>(germplasmSearchResult);
            }

            final Map<String, Integer> attributeTypesMap = this.getAttributeTypesMap(germplasmSearchParameter.getAddedColumnsPropertyIds());
            final Map<String, Integer> nameTypesMap = this.getNameTypesMap(germplasmSearchParameter.getAddedColumnsPropertyIds());

            // Query for values for added columns
            final SQLQuery query = this.getSession()
                    .createSQLQuery(this.createGermplasmSearchQueryString(germplasmSearchParameter, attributeTypesMap, nameTypesMap));

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

            final List<String> filteredProperty = germplasmSearchParameter.getAddedColumnsPropertyIds().stream().filter(s -> !this.GERMPLASM_TREE_NODE_PROPERTY_IDS.contains(s)).collect(Collectors.toList());
            for (final String propertyId : filteredProperty) {
                    query.addScalar(propertyId);
            }

            query.setFirstResult(startingRow);
            query.setMaxResults(noOfEntries);

            germplasmSearchResult.addAll(this.convertObjectToGermplasmList(query.list(),
                    filteredProperty, attributeTypesMap, nameTypesMap));
            return new ArrayList<>(germplasmSearchResult);

        } catch (final HibernateException e) {
            final String message = "Error with searchForGermplasms(" + germplasmSearchParameter.getSearchKeyword() + ") " + e.getMessage();
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
            this.searchInGidCriteria(queryString, params, q, o);

            // 2. find germplasms with stock_id = or like q
            this.searchInStockIdCriteria(queryString, params, q, o);

            // 3. find germplasms with nVal = or like q
            this.searchInNamesCriteria(queryString, params, q, o);

            queryString.append(") GermplasmSearchResults ");

            if (withInventoryOnly) {
                queryString.append(" INNER JOIN ");
                queryString.append(
                        "(SELECT l.eid as GID from ims_lot l INNER JOIN ims_transaction t on l.lotid = t.lotid AND l.etype = 'GERMPLSM' GROUP BY l.eid HAVING SUM(t.trnqty) > 0 ) ");
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
            final String message = "Error with retrieveGIDSearchResults(" + q + ") " + e.getMessage();
            GermplasmSearchDAO.LOG.error(message, e);
            throw new MiddlewareQueryException(message, e);
        }
    }

    public Integer countSearchForGermplasms(final GermplasmSearchParameter germplasmSearchParameter) {

        final Monitor countSearchForGermplasms = MonitorFactory.start("Method Started : countSearchForGermplasms ");

        int searchResultsCount = 0;

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
            queryString.append(GermplasmSearchDAO.GERMPLASM_NOT_DELETED_CLAUSE + GermplasmSearchDAO.LIMIT_CLAUSE);
            queryString.append(GermplasmSearchDAO.UNION);
        }
    }

    private void searchInStockIdCriteria(final StringBuilder queryString, final Map<String, String> params, final String q,
                                         final Operation o) {

        queryString.append("SELECT eid as GID FROM ims_lot l " + "INNER JOIN germplsm g on l.eid = g.gid "
                + "INNER JOIN ims_transaction t on l.lotid = t.lotid AND l.etype = 'GERMPLSM' ");
        if (o.equals(Operation.LIKE)) {
            queryString.append("WHERE l.stock_id LIKE :stock_id");
        } else {
            queryString.append("WHERE l.stock_id = :stock_id");
        }
        params.put("stock_id", q);

        // make sure to not include deleted germplasm from the search results
        queryString.append(GermplasmSearchDAO.GERMPLASM_NOT_DELETED_CLAUSE + GermplasmSearchDAO.LIMIT_CLAUSE);
        queryString.append(GermplasmSearchDAO.UNION);
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
        queryString.append(GermplasmSearchDAO.GERMPLASM_NOT_DELETED_CLAUSE + GermplasmSearchDAO.LIMIT_CLAUSE);
    }

    private Set<Integer> retrieveGIDParentsResults(final Set<Integer> gidSearchResults) {
        try {
            final Set<Integer> gidParentsSearchResults = new HashSet<>();
            final StringBuilder queryString = new StringBuilder();
            queryString.append("SELECT GermplasmParents.GID FROM (");

            // female parent
            queryString.append("SELECT g.gpid1 as GID from germplsm g where g.gpid1 != 0 AND g.gid IN (:gids) ");
            queryString.append(GermplasmSearchDAO.UNION);

            // male parent
            queryString.append("SELECT g.gpid2 as GID from germplsm g where g.gpid1 != 0 AND g.gid IN (:gids) ");
            queryString.append(GermplasmSearchDAO.UNION);

            // other progenitors
            queryString.append("SELECT p.gid as GID from progntrs p where p.gid IN (:gids)");
            queryString.append(
                    ") GermplasmParents " + "INNER JOIN germplsm g on GermplasmParents.GID = g.gid AND  g.deleted = 0  AND g.grplce = 0");

            final SQLQuery query = this.getSession().createSQLQuery(queryString.toString());
            query.setParameterList("gids", gidSearchResults);

            gidParentsSearchResults.addAll(query.list());

            return gidParentsSearchResults;
        } catch (final HibernateException e) {
            final String message = "Error with retrieveGIDParentsResults(GIDS=" + gidSearchResults + ") : " + e.getMessage();
            GermplasmSearchDAO.LOG.error(message, e);
            throw new MiddlewareQueryException(message, e);
        }
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

    protected String createGermplasmSearchQueryString(final GermplasmSearchParameter germplasmSearchParameter,
                                                      final Map<String, Integer> attributeTypesMap, final Map<String, Integer> nameTypesMap) {

        final StringBuilder mainQuery = new StringBuilder();

        mainQuery.append(this.createSelectClauseForGermplasmSearch(germplasmSearchParameter.getAddedColumnsPropertyIds(), attributeTypesMap,
                nameTypesMap));
        mainQuery.append(this.createFromClauseForGermplasmSearch(germplasmSearchParameter.getAddedColumnsPropertyIds(), attributeTypesMap,
                nameTypesMap));
        mainQuery.append("WHERE g.gid IN (:gids) GROUP BY g.gid");
        mainQuery.append(this.addSortingColumns(germplasmSearchParameter.getSortState(), attributeTypesMap, nameTypesMap));

        return mainQuery.toString();

    }

    protected String createFromClauseForGermplasmSearch(final List<String> addedColumnsPropertyIds,
                                                        final Map<String, Integer> attributeTypesMap, final Map<String, Integer> nameTypesMap) {

        final StringBuilder fromClause = new StringBuilder();
        fromClause.append("FROM germplsm g \n" //
            + " LEFT JOIN ims_lot gl ON gl.eid = g.gid AND gl.etype = 'GERMPLSM' AND gl.status = 0 \n" //
            + " LEFT JOIN ims_transaction gt ON gt.lotid = gl.lotid AND gt.trnstat <> 9 \n" //
            + " LEFT JOIN methods m ON m.mid = g.methn \n" //
            + " LEFT JOIN location l  ON l.locid = g.glocn \n" //
            + " LEFT JOIN `names` allNames  ON g.gid = allNames.gid and allNames.nstat != 9 \n");

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

    protected String createSelectClauseForGermplasmSearch(final List<String> addedColumnsPropertyIds,
                                                          final Map<String, Integer> attributeTypesMap, final Map<String, Integer> nameTypesMap) {

        final StringBuilder selectClause = new StringBuilder();
        selectClause.append("SELECT g.*, \n" //
            // name ordering: preferred name > latest > oldest
            + " Group_concat(DISTINCT allNames.nval ORDER BY allNames.nstat = 1 desc, allNames.ndate desc SEPARATOR ', ') AS `" + GermplasmSearchDAO.NAMES + "`, \n"  //
            + " Group_concat(DISTINCT gl.stock_id ORDER BY gl.stock_id SEPARATOR  ', ')  AS `" + GermplasmSearchDAO.STOCK_IDS + "`, \n" //
            + " g.gid  AS `" + GermplasmSearchDAO.GID + "`, \n" //
            + " g.mgid AS `" + GermplasmSearchDAO.GROUP_ID + "`, \n" //
            + " Count(DISTINCT gt.lotid) AS `" + GermplasmSearchDAO.AVAIL_LOTS + "`, \n" //
            + " Sum(gt.trnqty)/(Count(gt.trnid)/Count(DISTINCT gt.trnid)) AS `" + GermplasmSearchDAO.AVAIL_BALANCE + "`, \n"  //
            + " m.mname AS `" + GermplasmSearchDAO.METHOD_NAME + "`, \n"  //
            + " l.lname AS `" + GermplasmSearchDAO.LOCATION_NAME + "` \n");

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

    protected String addSortingColumns(final Map<String, Boolean> sortState, final Map<String, Integer> attributeTypesMap,
                                       final Map<String, Integer> nameTypesMap) {

        final Map<String, Boolean> filteredSortState = sortState.entrySet().stream().filter(map->!this.GERMPLASM_TREE_NODE_PROPERTY_IDS.contains(map.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        if (filteredSortState.isEmpty()) {
            return "";
        }


        final StringBuilder sortingQuery = new StringBuilder();
        sortingQuery.append(" ORDER BY ");
        for (final Map.Entry<String, Boolean> sortCondition : filteredSortState.entrySet()) {

            final String order = sortCondition.getValue().equals(true) ? "ASC" : "DESC";

            if (attributeTypesMap.containsKey(sortCondition.getKey()) || nameTypesMap.containsKey(sortCondition.getKey())) {
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

    /**
     * Converts the list of object array (from germplasm search) to a list of Germplasm.
     *
     * @param result
     * @param addedColumnsPropertyIds
     * @param attributeTypesMap
     * @return
     */
    protected List<Germplasm> convertObjectToGermplasmList(final List<Object[]> result, final List<String> addedColumnsPropertyIds,
                                                           final Map<String, Integer> attributeTypesMap, final Map<String, Integer> nameTypesMap) {
        final List<Germplasm> germplasms = new ArrayList<>();
        if (result != null) {
            for (final Object[] row : result) {
                germplasms.add(this.mapToGermplasm(row, addedColumnsPropertyIds, attributeTypesMap, nameTypesMap));
            }
        }
        return germplasms;
    }

    /**
     * Map the values in Row object array to their designated field in Germplasm object.
     *
     * @param row
     * @param addedColumnsPropertyIds
     * @param attributeTypesMap
     * @return
     */
    protected Germplasm mapToGermplasm(final Object[] row, final List<String> addedColumnsPropertyIds,
                                       final Map<String, Integer> attributeTypesMap, final Map<String, Integer> nameTypesMap) {
        final Germplasm germplasm = (Germplasm) row[0];
        final GermplasmInventory inventoryInfo = new GermplasmInventory(germplasm.getGid());
        germplasm.setGermplasmNamesString(
                row[GermplasmSearchDAO.GERMPLASM_NAMES_INDEX] != null ? String.valueOf(row[GermplasmSearchDAO.GERMPLASM_NAMES_INDEX]) : "");
        inventoryInfo.setStockIDs((String) row[GermplasmSearchDAO.STOCKID_INDEX]);
        inventoryInfo.setActualInventoryLotCount(
                row[GermplasmSearchDAO.LOT_INDEX] != null ? ((BigInteger) row[GermplasmSearchDAO.LOT_INDEX]).intValue() : 0);
        inventoryInfo.setTotalAvailableBalance(
                row[GermplasmSearchDAO.AVAIL_BALANCE_INDEX] != null ? (Double) row[GermplasmSearchDAO.AVAIL_BALANCE_INDEX] : 0d);
        germplasm.setInventoryInfo(inventoryInfo);
        germplasm.setMethodName(row[7] != null ? String.valueOf(row[7]) : "");
        germplasm.setLocationName(row[8] != null ? String.valueOf(row[8]) : "");

        germplasm.setGermplasmDate(this.getValueOfAddedColumns(GermplasmSearchDAO.GERMPLASM_DATE, row, addedColumnsPropertyIds));
        germplasm.setMethodCode(this.getValueOfAddedColumns(GermplasmSearchDAO.METHOD_ABBREVIATION, row, addedColumnsPropertyIds));
        germplasm.setMethodNumber(this.getValueOfAddedColumns(GermplasmSearchDAO.METHOD_NUMBER, row, addedColumnsPropertyIds));
        germplasm.setMethodGroup(this.getValueOfAddedColumns(GermplasmSearchDAO.METHOD_GROUP, row, addedColumnsPropertyIds));
        germplasm.setGermplasmPeferredName(this.getValueOfAddedColumns(GermplasmSearchDAO.PREFERRED_NAME, row, addedColumnsPropertyIds));
        germplasm.setGermplasmPeferredId(this.getValueOfAddedColumns(GermplasmSearchDAO.PREFERRED_ID, row, addedColumnsPropertyIds));
        germplasm.setGroupSourceGID(this.getValueOfAddedColumns(GermplasmSearchDAO.GROUP_SOURCE_GID, row, addedColumnsPropertyIds));
        germplasm.setGroupSourcePreferredName(
                this.getValueOfAddedColumns(GermplasmSearchDAO.GROUP_SOURCE_PREFERRED_NAME, row, addedColumnsPropertyIds));
        germplasm.setImmediateSourceGID(this.getValueOfAddedColumns(GermplasmSearchDAO.IMMEDIATE_SOURCE_GID, row, addedColumnsPropertyIds));
        germplasm.setImmediateSourcePreferredName(
                this.getValueOfAddedColumns(GermplasmSearchDAO.IMMEDIATE_SOURCE_PREFERRED_NAME, row, addedColumnsPropertyIds));
        this.setValuesMapForAttributeAndNameTypes(germplasm, row, addedColumnsPropertyIds, attributeTypesMap, nameTypesMap);

        return germplasm;
    }

    /**
     * Creates a map that contains the values of 'Attribute' columns.
     *
     * @param row
     * @param addedColumnsPropertyIds
     * @param attributeTypesMap
     * @return
     */
    protected void setValuesMapForAttributeAndNameTypes(final Germplasm germplasm, final Object[] row,
                                                        final List<String> addedColumnsPropertyIds, final Map<String, Integer> attributeTypesMap,
                                                        final Map<String, Integer> nameTypesMap) {

        final Map<String, String> attributeTypesValueMap = new HashMap<>();
        final Map<String, String> nameTypesValueMap = new HashMap<>();
        for (final String propertyId : addedColumnsPropertyIds) {
            if (attributeTypesMap.containsKey(propertyId)) {
                attributeTypesValueMap.put(propertyId, this.getValueOfAddedColumns(propertyId, row, addedColumnsPropertyIds));

            } else if (nameTypesMap.containsKey(propertyId)) {
                nameTypesValueMap.put(propertyId, this.getValueOfAddedColumns(propertyId, row, addedColumnsPropertyIds));
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
     * @return
     */
    protected String getValueOfAddedColumns(final String propertyId, final Object[] row, final List<String> addedColumnsPropertyIds) {

        if (addedColumnsPropertyIds.contains(propertyId)) {
            final int addedColumnIndex = addedColumnsPropertyIds.indexOf(propertyId) + 9;

            return row[addedColumnIndex] != null ? String.valueOf(row[addedColumnIndex]) : "";
        } else {
            return "";
        }

    }

    protected Map<String, Integer> getAttributeTypesMap(final List<String> addedColumnsPropertyIds) {

        final List<String> nonStandardColumns = new ArrayList<>();
        for (final String propertyId : addedColumnsPropertyIds) {
            if (!ColumnLabels.getAddableGermplasmColumns().contains(propertyId)) {
                nonStandardColumns.add(propertyId);
            }
        }

        return this.getTypesFromUserDefinedFieldTable(addedColumnsPropertyIds, nonStandardColumns, GermplasmSearchDAO.ATRIBUTS);
    }

    protected Map<String, Integer> getNameTypesMap(final List<String> addedColumnsPropertyIds) {

        final List<String> nonStandardColumns = new ArrayList<>();
        for (final String propertyId : addedColumnsPropertyIds) {
            if (!ColumnLabels.getAddableGermplasmColumns().contains(propertyId)) {
                nonStandardColumns.add(propertyId);
            }
        }

        return this.getTypesFromUserDefinedFieldTable(addedColumnsPropertyIds, nonStandardColumns, "NAMES");
    }

    private Map<String, Integer> getTypesFromUserDefinedFieldTable(final List<String> addedColumnsPropertyIds,
                                                                   final List<String> nonStandardColumns, final String ftable) {
        final Map<String, Integer> typesMap = new HashMap<>();
        final String field = GermplasmSearchDAO.ATRIBUTS.equals(ftable) ? "fcode" : "fname";
        if (!nonStandardColumns.isEmpty()) {
            final SQLQuery query = this.getSession()
                    .createSQLQuery("SELECT " + field + ", fldno from udflds where ftable = :ftable and " + field + " IN (:fieldList)");
            query.setParameter("ftable", ftable);
            query.setParameterList("fieldList", addedColumnsPropertyIds);
            final List<Object[]> results = query.list();

            for (final Object[] row : results) {
                typesMap.put(String.valueOf(row[0]).toUpperCase(), (Integer) row[1]);
            }

        }

        return typesMap;
    }



    /**
     * New germplasm query (replaces {@link GermplasmSearchDAO#searchForGermplasms(GermplasmSearchParameter)}
     * @return
     */
    public List<GermplasmSearchResponse> searchGermplasm(final GermplasmSearchRequest germplasmSearchRequest, final Pageable pageable) {

        try {
            // list is used again in a query wrapper to paginate (filtered+included) gids
            final List<Integer> gids = this.retrieveSearchGids(germplasmSearchRequest, pageable);

            if (gids.isEmpty()) {
                return Collections.EMPTY_LIST;
            }

            final List<String> addedColumnsPropertyIds = germplasmSearchRequest.getAddedColumnsPropertyIds();
            final Map<String, Integer> attributeTypesMap = this.getAttributeTypesMap(addedColumnsPropertyIds);
            final Map<String, Integer> nameTypesMap = this.getNameTypesMap(addedColumnsPropertyIds);

            // main query
            final StringBuilder queryBuilder =
                new StringBuilder(this.createSelectClauseForGermplasmSearch(addedColumnsPropertyIds, attributeTypesMap, nameTypesMap));
            queryBuilder.append(this.createFromClauseForGermplasmSearch(addedColumnsPropertyIds, attributeTypesMap, nameTypesMap));

			queryBuilder.append(" where g.gid in (:gids) ");
			queryBuilder.append(" and g.deleted = 0 and g.grplce = 0 ");
            queryBuilder.append(" group by g.gid ");

            final Map<String, Boolean> sortState = convertSort(pageable);
            // TODO improve perf (e.g order by NAMES)
            queryBuilder.append(this.addSortingColumns(sortState, attributeTypesMap, nameTypesMap));

            final SQLQuery query = this.getSession().createSQLQuery(queryBuilder.toString());

            query.addEntity(GERMPLSM, Germplasm.class);
            query.addScalar(NAMES);
            query.addScalar(STOCK_IDS);
            query.addScalar(GID);
            query.addScalar(GROUP_ID);
            query.addScalar(AVAIL_LOTS);
            query.addScalar(AVAIL_BALANCE);
            query.addScalar(METHOD_NAME);
            query.addScalar(LOCATION_NAME);

            for (final String addedColumnPropertyId : addedColumnsPropertyIds) {
                if (!GERMPLASM_TREE_NODE_PROPERTY_IDS.contains(addedColumnPropertyId)) {
                    query.addScalar(addedColumnPropertyId);
                }
            }

            addPaginationToSQLQuery(query, pageable);
            query.setParameterList("gids", gids);

            final List<Object[]> result = query.list();

            /* TODO
             *  - rewrite mapToGermplasm after complete migration
             *  - delete searchForGermplasms
             */
            final List<Germplasm> germplasmList =
                this.convertObjectToGermplasmList(result, addedColumnsPropertyIds, attributeTypesMap, nameTypesMap);
            final List<GermplasmSearchResponse> response = new ArrayList<>();
            for (final Germplasm germplasm : germplasmList) {
                response.add(new GermplasmSearchResponse(germplasm));
            }
            return response;
        } catch (final HibernateException e) {
            throw new MiddlewareQueryException("Error at GermplasmSearchDAO.searchGermplasm(): " + e.getMessage(), e);
        }
    }

    /**
     * Filtered gids  + associated gids (e.g pedigree, group).
	 * Paginated, for performance reasons.
     */
    private List<Integer> retrieveSearchGids(final GermplasmSearchRequest germplasmSearchRequest,
        final Pageable pageable) {

        final List<String> addedColumnsPropertyIds = germplasmSearchRequest.getAddedColumnsPropertyIds();
        final Map<String, Integer> attributeTypesMap = this.getAttributeTypesMap(addedColumnsPropertyIds);
        final Map<String, Integer> nameTypesMap = this.getNameTypesMap(addedColumnsPropertyIds);

        // main query
        final StringBuilder queryBuilder =
            new StringBuilder(this.createSelectClauseForGermplasmSearch(addedColumnsPropertyIds, attributeTypesMap, nameTypesMap));
        queryBuilder.append(this.createFromClauseForGermplasmSearch(addedColumnsPropertyIds, attributeTypesMap, nameTypesMap));

        addFilters(new SqlQueryParamBuilder(queryBuilder), germplasmSearchRequest);

        queryBuilder.append(" group by g.gid ");

        final Map<String, Boolean> sortState = convertSort(pageable);
        // TODO improve perf (e.g order by NAMES)
        queryBuilder.append(this.addSortingColumns(sortState, attributeTypesMap, nameTypesMap));

        final SQLQuery sqlQuery = this.getSession().createSQLQuery(queryBuilder.toString());
        sqlQuery.addScalar(GID);
        addPaginationToSQLQuery(sqlQuery, pageable);
        addFilters(new SqlQueryParamBuilder(sqlQuery), germplasmSearchRequest);

        final List<Integer> gids = sqlQuery.list();

        if (gids.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        /*
         * Included gids are only for the current page, for performance reasons.
         * They are in separate queries instead of a subquery, because mysql cannot perform semijoin transformation with UNION
         * https://dev.mysql.com/doc/refman/5.6/en/semijoins.html
         */

        gids.addAll(this.retrievePedigreeGids(gids, germplasmSearchRequest));
        gids.addAll(this.retrieveGroupGids(gids, germplasmSearchRequest));

        return gids;
    }

    private static Map<String, Boolean> convertSort(final Pageable pageable) {
        final Map<String, Boolean> sortState = new HashMap<>();
        final Sort sort = pageable.getSort();
        if (sort == null) {
            return Collections.EMPTY_MAP;
        }
        final Iterator<Sort.Order> iterator = sort.iterator();
        while (iterator.hasNext()) {
            final Sort.Order next = iterator.next();
            sortState.put(next.getProperty(), next.getDirection().equals(Sort.Direction.ASC));
        }
        return sortState;
    }

    private static void addFilters(final SqlQueryParamBuilder paramBuilder, final GermplasmSearchRequest germplasmSearchRequest) {

        paramBuilder.append(" where g.deleted = 0 AND g.grplce = 0 ");

        final SqlTextFilter nameFilter = germplasmSearchRequest.getNameFilter();
        if (nameFilter != null && nameFilter.getValue() != null) {
            final String q = nameFilter.getValue();
            String operator = "LIKE";
            switch (nameFilter.getType()) {
                case EXACTMATCH:
                    operator = "=";
                    paramBuilder.setParameter("q", q);
                    paramBuilder.setParameter("qStandardized", GermplasmDataManagerUtil.standardizeName(q));
                    paramBuilder.setParameter("qNoSpaces", q.replaceAll(" ", ""));
                    break;
                case STARTSWITH:
                    paramBuilder.setParameter("q", q + "%");
                    paramBuilder.setParameter("qStandardized", GermplasmDataManagerUtil.standardizeName(q) + "%");
                    paramBuilder.setParameter("qNoSpaces", q.replaceAll(" ", "") + "%");
                    break;
                case CONTAINS:
                    paramBuilder.setParameter("q", "%" + q + "%");
                    paramBuilder.setParameter("qStandardized", "%" + GermplasmDataManagerUtil.standardizeName(q) + "%");
                    paramBuilder.setParameter("qNoSpaces", "%" + q.replaceAll(" ", "") + "%");
                    break;
            }
            paramBuilder.append(" AND (allNames.nval " + operator + " :q "
                + "OR allNames.nval " + operator + " :qStandardized " 
                + "OR allNames.nval " + operator + " :qNoSpaces) \n ");
        }

        // TODO complete filters
    }

    /**
     * NOTE: This part of the query has performance implications. Please take this into consideration when making changes.
     * The current setup has been tested on crop=brachiaria, #germplsm: ~ 6M, page size: 1000, include pedigree generative
     * -> 5 levels: 7 sec, 20 levels: 30 sec
     *
     * STRAIGHT_JOIN joins the table in left to right order.
     * For some reason, with "inner join" the query optimizer chooses a suboptimal order and instead of starting with P0 (limited by where)
     * it starts with P1, and the query never finishes.With straight_join, it finishes in seconds.
     *
     * The gnpgs column also plays a key role in the running time.Some tested alternatives that don't make use of this column have shown
     * poor performance.
	 *
     * The query doesn't exclude deleted germplasm (g.deleted and g.grplce), because it slows down the query considerably.
     * Deleted germplasm should be excluded by the caller later.
     *
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
                            queryBuilder.append(String.format(" straight_join germplsm PGROUP%s on P%s.gpid1 != 0", prev, prev) //
                                // find the group source (last gid produced by a generative process)
                                // join with itself if gid is already the group source
                                + String.format(" and ((P%s.gnpgs < 0 and PGROUP%s.gid = P%s.gpid1)", prev, prev, prev) //
                                + String.format("   or (P%s.gnpgs > 0 and PGROUP%s.gid = P%s.gid)) \n", prev, prev, prev) //
                                // find the group source parents
                                + String.format(" straight_join germplsm P%s on PGROUP%s.gnpgs > 0", i, prev)
                                + String.format("  and PGROUP%s.gpid1 != 0 and PGROUP%s.gpid2 != 0", prev, prev)
                                + String.format("  and (P%s.gid = PGROUP%s.gpid1 or P%s.gid = PGROUP%s.gpid2) \n ", i, prev, i, prev));
                            // TODO other progenitors
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
                            // some (non-standard?) derivative germplasm has gpid2=0
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
            return Collections.EMPTY_LIST;
        }

        final SQLQuery sqlQuery = this.getSession().createSQLQuery(queryBuilder.toString());
        sqlQuery.setParameterList("gids", gids);

        return sqlQuery.list();
    }

    private List<Integer> retrieveGroupGids(final List<Integer> gids, final GermplasmSearchRequest germplasmSearchRequest) {
        if (germplasmSearchRequest.isIncludeGroupMembers()) {
            // TODO
        }

        return Collections.EMPTY_LIST;
    }
}
