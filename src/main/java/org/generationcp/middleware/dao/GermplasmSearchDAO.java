
package org.generationcp.middleware.dao;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.api.germplasm.search.GermplasmSearchRequest;
import org.generationcp.middleware.api.germplasm.search.GermplasmSearchRequest.IncludePedigree;
import org.generationcp.middleware.api.germplasm.search.GermplasmSearchResponse;
import org.generationcp.middleware.constant.ColumnLabels;
import org.generationcp.middleware.domain.gms.search.GermplasmSearchParameter;
import org.generationcp.middleware.domain.gms.search.GermplasmSortableColumn;
import org.generationcp.middleware.domain.inventory.GermplasmInventory;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.sqlfilter.SqlTextFilter;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.exceptions.MiddlewareRequestException;
import org.generationcp.middleware.manager.GermplasmDataManagerUtil;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.ims.ExperimentTransactionType;
import org.generationcp.middleware.pojos.ims.TransactionStatus;
import org.generationcp.middleware.pojos.ims.TransactionType;
import org.generationcp.middleware.util.Debug;
import org.generationcp.middleware.util.SqlQueryParamBuilder;
import org.generationcp.middleware.util.Util;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
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
    private static final String MIXED_UNITS_LABEL = "Mixed";

    public static final String GID = ColumnLabels.GID.getName();
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
    public static final String IMMEDIATE_SOURCE_PREFERRED_NAME = ColumnLabels.IMMEDIATE_SOURCE_PREFERRED_NAME.getName();

    private static final int STOCKID_INDEX = 2;
    private static final int LOT_INDEX = 5;
    private static final int AVAIL_BALANCE_INDEX = 6;
    private static final Map<String, String> selectClauseColumnsMap = new HashMap<>();

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(Util.DATE_AS_NUMBER_FORMAT);

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
            + " g.mgid AS `" + GermplasmSearchDAO.GROUP_ID + "`, \n" //
            + " Count(DISTINCT gt.lotid) AS `" + GermplasmSearchDAO.AVAIL_LOTS + "`, \n" //
            /*
             * Sum of transaction (duplicated because of joins)
             * -----divided by----
             * ( count of tr / count of distinct tr) = how many times the real sum is repeated
             * ===================
             * Real sum = Available balance (not considering status)
             */
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
        germplasm.setGermplasmNamesString(row[GermplasmSearchDAO.GERMPLASM_NAMES_INDEX] != null ? String.valueOf(row[GermplasmSearchDAO.GERMPLASM_NAMES_INDEX]) : "");
        inventoryInfo.setStockIDs((String) row[GermplasmSearchDAO.STOCKID_INDEX]);
        inventoryInfo.setActualInventoryLotCount(row[GermplasmSearchDAO.LOT_INDEX] != null ? ((BigInteger) row[GermplasmSearchDAO.LOT_INDEX]).intValue() : 0);
        inventoryInfo.setTotalAvailableBalance(row[GermplasmSearchDAO.AVAIL_BALANCE_INDEX] != null ? (Double) row[GermplasmSearchDAO.AVAIL_BALANCE_INDEX] : 0d);
        germplasm.setInventoryInfo(inventoryInfo);
        germplasm.setMethodName(row[7] != null ? String.valueOf(row[7]) : "");
        germplasm.setLocationName(row[8] != null ? String.valueOf(row[8]) : "");

        final int indexOffset = 9;
        germplasm.setGermplasmDate(this.getValueOfAddedColumns(GERMPLASM_DATE, row, addedColumnsPropertyIds, indexOffset));
        germplasm.setMethodCode(this.getValueOfAddedColumns(METHOD_ABBREVIATION, row, addedColumnsPropertyIds, indexOffset));
        germplasm.setMethodNumber(this.getValueOfAddedColumns(METHOD_NUMBER, row, addedColumnsPropertyIds, indexOffset));
        germplasm.setMethodGroup(this.getValueOfAddedColumns(METHOD_GROUP, row, addedColumnsPropertyIds, indexOffset));
        germplasm.setGermplasmPeferredName(this.getValueOfAddedColumns(PREFERRED_NAME, row, addedColumnsPropertyIds, indexOffset));
        germplasm.setGermplasmPeferredId(this.getValueOfAddedColumns(PREFERRED_ID, row, addedColumnsPropertyIds, indexOffset));
        germplasm.setFemaleParentPreferredID(this.getValueOfAddedColumns(FEMALE_PARENT_ID, row, addedColumnsPropertyIds, indexOffset));
        germplasm.setFemaleParentPreferredName(this.getValueOfAddedColumns(FEMALE_PARENT_PREFERRED_NAME, row, addedColumnsPropertyIds, indexOffset));
        germplasm.setMaleParentPreferredID(this.getValueOfAddedColumns(MALE_PARENT_ID, row, addedColumnsPropertyIds, indexOffset));
        germplasm.setMaleParentPreferredName(this.getValueOfAddedColumns(MALE_PARENT_PREFERRED_NAME, row, addedColumnsPropertyIds, indexOffset));
        germplasm.setGroupSourceGID(this.getValueOfAddedColumns(GROUP_SOURCE_GID, row, addedColumnsPropertyIds, indexOffset));
        germplasm.setGroupSourcePreferredName(this.getValueOfAddedColumns(GROUP_SOURCE_PREFERRED_NAME, row, addedColumnsPropertyIds, indexOffset));
        germplasm.setImmediateSourceGID(this.getValueOfAddedColumns(IMMEDIATE_SOURCE_GID, row, addedColumnsPropertyIds, indexOffset));
        germplasm.setImmediateSourcePreferredName(this.getValueOfAddedColumns(IMMEDIATE_SOURCE_PREFERRED_NAME, row, addedColumnsPropertyIds, indexOffset));
        
        this.setValuesMapForAttributeAndNameTypes(germplasm, row, addedColumnsPropertyIds, attributeTypesMap, nameTypesMap, indexOffset);

        return germplasm;
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

    // +++++++++++++++++++++++++++++++++++++++New Germplasm search ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    /**
     * New germplasm query (replaces {@link GermplasmSearchDAO#searchForGermplasms(GermplasmSearchParameter)}
     */
    public List<GermplasmSearchResponse> searchGermplasm(final GermplasmSearchRequest germplasmSearchRequest, final Pageable pageable,
        final String programUUID) {

        try {
            // list is used again in a query wrapper to paginate (filtered+included) gids
            final List<Integer> gids = this.retrieveSearchGids(germplasmSearchRequest, pageable, programUUID);

            if (gids.isEmpty()) {
                return Collections.EMPTY_LIST;
            }

            final List<String> addedColumnsPropertyIds = germplasmSearchRequest.getAddedColumnsPropertyIds();
            final Map<String, Integer> attributeTypesMap = this.getAttributeTypesMap(addedColumnsPropertyIds);
            final Map<String, Integer> nameTypesMap = this.getNameTypesMap(addedColumnsPropertyIds);

            // main query
            final StringBuilder queryBuilder =
                new StringBuilder(this.createSelectClause(addedColumnsPropertyIds, attributeTypesMap, nameTypesMap));
            queryBuilder.append(this.createFromClause(addedColumnsPropertyIds, attributeTypesMap, nameTypesMap));

			queryBuilder.append(" where g.gid in (:gids) ");
			queryBuilder.append(" and g.deleted = 0 and g.grplce = 0 ");
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

            for (final String addedColumnPropertyId : addedColumnsPropertyIds) {
                if (!GERMPLASM_TREE_NODE_PROPERTY_IDS.contains(addedColumnPropertyId)) {
                    query.addScalar(addedColumnPropertyId);
                }
            }

            /*
             * For big databases (e.g brachiaria, ~6M germplsm), sorting is slow.
             * If sort is needed, we limit the inner query to 5000 records and sort only that.
             * In this mode, it's not possible to paginate past
             * Otherwise, Pagination is done in the inner query without a limit.
             *
             * The outer query returns a PAGE of results + associated gids (e.g pedigree, group members),
             * which don't count for the Total results
             */
            if (!sortState.isEmpty()) {
            	addPaginationToSQLQuery(query, pageable);
            }
            query.setParameterList("gids", gids);

            final List<Object[]> results = query.list();

            if (results == null) {
                return Collections.EMPTY_LIST;
            }

            final List<GermplasmSearchResponse> response = new ArrayList<>();
            for (final Object[] result : results) {
                response.add(this.mapToGermplasmSearchResponse(result, addedColumnsPropertyIds, attributeTypesMap, nameTypesMap));
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
            + " LEFT JOIN ims_lot gl ON gl.eid = g.gid AND gl.etype = 'GERMPLSM' AND gl.status = 0 \n" //
            + " LEFT JOIN cvterm scale ON scale.cvterm_id = gl.scaleid \n" //
            + " LEFT JOIN methods m ON m.mid = g.methn \n" //
            + " LEFT JOIN location l ON l.locid = g.glocn \n" //
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
            + " Group_concat(DISTINCT allNames.nval ORDER BY allNames.nstat = 1 desc, allNames.ndate desc SEPARATOR ', ') AS `" + GermplasmSearchDAO.NAMES + "`, \n"  //
            + " g.mgid AS `" + GermplasmSearchDAO.GROUP_ID + "`, \n" //
            + " Count(DISTINCT gl.lotid) AS `" + GermplasmSearchDAO.AVAIL_LOTS + "`, \n" //
            + " IF(COUNT(DISTINCT IFNULL(gl.scaleid, 'null')) = 1, " //
            + "  IFNULL((SELECT SUM(CASE WHEN imt.trnstat = " + TransactionStatus.CONFIRMED.getIntValue() //
            + "    OR (imt.trnstat = " + TransactionStatus.PENDING.getIntValue() //
            + "    AND imt.trntype = " + TransactionType.WITHDRAWAL.getId() + ") THEN imt.trnqty ELSE 0 END) " //
            + "   FROM ims_transaction imt WHERE imt.lotid = gl.lotid), 0)," //
            + " '" + MIXED_UNITS_LABEL + "') AS  `" + GermplasmSearchDAO.AVAIL_BALANCE + "`, \n"  //
            + " IF(COUNT(DISTINCT IFNULL(gl.scaleid, 'null')) = 1, scale.name, '" + MIXED_UNITS_LABEL + "') AS `" + LOT_UNITS + "`, \n"  //
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

    protected GermplasmSearchResponse mapToGermplasmSearchResponse(final Object[] row, final List<String> addedColumnsPropertyIds,
        final Map<String, Integer> attributeTypesMap, final Map<String, Integer> nameTypesMap) {

        final GermplasmSearchResponse response = new GermplasmSearchResponse();

        final Germplasm germplasm = (Germplasm) row[0];

        response.setGid(germplasm.getGid());
        response.setNames((String) row[1]);
        response.setGroupId(germplasm.getMgid());
        response.setLotCount(row[3] != null ? ((BigInteger) row[3]).intValue() : 0);
        response.setAvailableBalance((String) row[4]);
        response.setUnit((String) row[5]);
        response.setMethodName((String) row[6]);
        response.setLocationName((String) row[7]);

        final int indexOffset = 8;
        response.setGermplasmDate(this.getValueOfAddedColumns(GERMPLASM_DATE, row, addedColumnsPropertyIds, indexOffset));
        response.setMethodCode(this.getValueOfAddedColumns(METHOD_ABBREVIATION, row, addedColumnsPropertyIds, indexOffset));
        response.setMethodNumber(this.getValueOfAddedColumns(METHOD_NUMBER, row, addedColumnsPropertyIds, indexOffset));
        response.setMethodGroup(this.getValueOfAddedColumns(METHOD_GROUP, row, addedColumnsPropertyIds, indexOffset));
        response.setGermplasmPeferredName(this.getValueOfAddedColumns(PREFERRED_NAME, row, addedColumnsPropertyIds, indexOffset));
        response.setGermplasmPeferredId(this.getValueOfAddedColumns(PREFERRED_ID, row, addedColumnsPropertyIds, indexOffset));
        response.setFemaleParentGID(this.getValueOfAddedColumns(FEMALE_PARENT_ID, row, addedColumnsPropertyIds, indexOffset));
        response.setFemaleParentPreferredName(this.getValueOfAddedColumns(FEMALE_PARENT_PREFERRED_NAME, row, addedColumnsPropertyIds, indexOffset));
        response.setMaleParentGID(this.getValueOfAddedColumns(MALE_PARENT_ID, row, addedColumnsPropertyIds, indexOffset));
        response.setMaleParentPreferredName(this.getValueOfAddedColumns(MALE_PARENT_PREFERRED_NAME, row, addedColumnsPropertyIds, indexOffset));
        response.setGroupSourceGID(this.getValueOfAddedColumns(GROUP_SOURCE_GID, row, addedColumnsPropertyIds, indexOffset));
        response.setGroupSourcePreferredName(this.getValueOfAddedColumns(GROUP_SOURCE_PREFERRED_NAME, row, addedColumnsPropertyIds, indexOffset));
        response.setImmediateSourceGID(this.getValueOfAddedColumns(IMMEDIATE_SOURCE_GID, row, addedColumnsPropertyIds, indexOffset));
        response.setImmediateSourcePreferredName(this.getValueOfAddedColumns(IMMEDIATE_SOURCE_PREFERRED_NAME, row, addedColumnsPropertyIds, indexOffset));

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
        final Map<String, Integer> attributeTypesMap = this.getAttributeTypesMap(addedColumnsPropertyIds);
        final Map<String, Integer> nameTypesMap = this.getNameTypesMap(addedColumnsPropertyIds);

        // main query
        final StringBuilder queryBuilder =
            new StringBuilder(this.createSelectClause(addedColumnsPropertyIds, attributeTypesMap, nameTypesMap));
        queryBuilder.append(this.createFromClause(addedColumnsPropertyIds, attributeTypesMap, nameTypesMap));

        final List<Integer> preFilteredGids = this.retrievePreFilteredGids(germplasmSearchRequest);
        // group by inside filters
        addFilters(new SqlQueryParamBuilder(queryBuilder), germplasmSearchRequest, preFilteredGids, programUUID);

        final Map<String, Boolean> sortState = convertSort(pageable);
		if (!sortState.isEmpty()) {
            queryBuilder.append(LIMIT_CLAUSE);
        }

        final SQLQuery sqlQuery = this.getSession().createSQLQuery(queryBuilder.toString());
        sqlQuery.addScalar(GID);

        if (sortState.isEmpty()) {
            addPaginationToSQLQuery(sqlQuery, pageable);
        }
		addFilters(new SqlQueryParamBuilder(sqlQuery), germplasmSearchRequest, preFilteredGids, programUUID);

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
        if (germplasmSearchRequest.isIncludeGroupMembers()) {
            gids.addAll(this.retrieveGIDGroupMemberResults(new HashSet<>(gids)));
        }

        return gids;
    }

    public long countSearchGermplasm(final GermplasmSearchRequest germplasmSearchRequest, final String programUUID) {

        try {
            // Reusing the same query without filters is expensive. We create a simpler one for count all
            if (germplasmSearchRequest == null) {
                final SQLQuery sqlQuery = this.getSession().createSQLQuery(" select count(1) from germplsm ");
                return ((BigInteger) sqlQuery.uniqueResult()).longValue();
            }

            final List<String> addedColumnsPropertyIds = germplasmSearchRequest.getAddedColumnsPropertyIds();
            final Map<String, Integer> attributeTypesMap = this.getAttributeTypesMap(addedColumnsPropertyIds);
            final Map<String, Integer> nameTypesMap = this.getNameTypesMap(addedColumnsPropertyIds);

            // main query
            final StringBuilder queryBuilder =
                new StringBuilder(this.createSelectClause(addedColumnsPropertyIds, attributeTypesMap, nameTypesMap));
            queryBuilder.append(this.createFromClause(addedColumnsPropertyIds, attributeTypesMap, nameTypesMap));

            final List<Integer> preFilteredGids = this.retrievePreFilteredGids(germplasmSearchRequest);
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

    private static void addFilters(final SqlQueryParamBuilder paramBuilder, final GermplasmSearchRequest germplasmSearchRequest,
        final List<Integer> preFilteredGids, final String programUUID) {

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

        final Integer gid = germplasmSearchRequest.getGid();
        if (gid != null) {
            paramBuilder.append(" and g.gid = :gid");
            paramBuilder.setParameter("gid", gid);
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
            paramBuilder.append(" and exists(select 1 from listdata filter_l "  //
                + "where filter_l.listid in (:germplasmListIds) and filter_l.gid = g.gid) ");
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
                + "            AND filter_gp.type_id = " + + TermId.LOCATION_ID.getId() //
                + " inner join location filter_location on filter_location.locid =  filter_gp.value" //
                + " inner join stock filter_stock on filter_nde.stock_id = filter_stock.stock_id" //
                + " where filter_stock.dbxref_id = g.gid and filter_location.lname like :locationOfUse) \n ");
            paramBuilder.setParameter("locationOfUse", '%' + locationOfUse + '%');
        }

        final Integer reference = germplasmSearchRequest.getReference();
        if (reference != null) {
            paramBuilder.append(" and g.gref = :reference ");
            paramBuilder.setParameter("reference", reference);
        }

        final List<Integer> harvestingStudyIds = germplasmSearchRequest.getHarvestingStudyIds();
        if (harvestingStudyIds != null) {
            paramBuilder.append(" and exists(select 1" //
                + " from project filter_p" //
                + "	 inner join project filter_plotdata on filter_p.project_id = filter_plotdata.study_id" //
                + "  inner join nd_experiment filter_nde on filter_plotdata.project_id = filter_nde.project_id" //
                + "  inner join ims_experiment_transaction filter_iet on filter_nde.nd_experiment_id = filter_iet.nd_experiment_id" //
                + "   and filter_iet.type = " + ExperimentTransactionType.HARVESTING.getId() //
                + "  inner join ims_transaction filter_transaction on filter_iet.trnid = filter_transaction.trnid" //
                + "  inner join ims_lot filter_lot on filter_transaction.lotid = filter_lot.lotid" //
                + " where filter_p.project_id in (:harvestingStudyIds) and filter_lot.lotid = gl.lotid) \n"); //
            paramBuilder.setParameterList("harvestingStudyIds", harvestingStudyIds);
        }

        final List<Integer> plantingStudyIds = germplasmSearchRequest.getPlantingStudyIds();
        if (plantingStudyIds != null) {
            paramBuilder.append(" and exists(select 1" //
                + " from project filter_p" //
                + "	 inner join project filter_plotdata on filter_p.project_id = filter_plotdata.study_id" //
                + "  inner join nd_experiment filter_nde on filter_plotdata.project_id = filter_nde.project_id" //
                + "  inner join ims_experiment_transaction filter_iet on filter_nde.nd_experiment_id = filter_iet.nd_experiment_id" //
                + "   and filter_iet.type = " + ExperimentTransactionType.PLANTING.getId() //
                + "  inner join ims_transaction filter_transaction on filter_iet.trnid = filter_transaction.trnid" //
                + "  inner join ims_lot filter_lot on filter_transaction.lotid = filter_lot.lotid" //
                + " where filter_p.project_id in (:plantingStudyIds) and filter_lot.lotid = gl.lotid) \n"); //
            paramBuilder.setParameterList("plantingStudyIds", plantingStudyIds);
        }

        final String breedingMethodName = germplasmSearchRequest.getBreedingMethodName();
        if (breedingMethodName != null) {
            paramBuilder.append(" and m.mname like :breedingMethodName");
            paramBuilder.setParameter("breedingMethodName", '%' + breedingMethodName + '%');
        }

        final Date harvestDateFrom = germplasmSearchRequest.getHarvestDateFrom();
        if (harvestDateFrom != null) {
            paramBuilder.append(" and g.gdate >= :harvestDateFrom ");
            paramBuilder.setParameter("harvestDateFrom", DATE_FORMAT.format(harvestDateFrom));
        }

        final Date harvestDateTo = germplasmSearchRequest.getHarvestDateTo();
        if (harvestDateTo != null) {
            paramBuilder.append(" and g.gdate <= :harvestDateTo ");
            paramBuilder.setParameter("harvestDateTo", DATE_FORMAT.format(harvestDateTo));
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

        paramBuilder.append(" group by g.gid having 1 = 1 ");

        // Post-group-by filtering

        // TODO improve perf (brachiaria - 6M germplsm - 3.8 min -  Faster if combined with other filters)
        final Boolean withInventoryOnly = germplasmSearchRequest.getWithInventoryOnly();
        if (Boolean.TRUE.equals(withInventoryOnly)) {
            paramBuilder.append(" and (" + AVAIL_BALANCE + " > 0 or " + AVAIL_BALANCE + " = '" + MIXED_UNITS_LABEL + "')");
        }

    }

    /**
     * This query contains those filters that don't perform well in the main query.
	 * The queries are bounded by the LIMIT clause, and therefore they are less appropriate for filters
     * that match a lot of records.
     */
    private List<Integer> retrievePreFilteredGids(final GermplasmSearchRequest germplasmSearchRequest) {

        final List<Integer> prefilteredGids = new ArrayList<>();

        final String femaleParentName = germplasmSearchRequest.getFemaleParentName();
        if (femaleParentName != null) {
            final List<Integer> gids = this.getSession().createSQLQuery("select g.gid from names n \n" //
                + "   straight_join germplsm female_parent on n.gid = female_parent.gid \n" //
                + "   straight_join germplsm group_source on female_parent.gid = group_source.gpid1 and group_source.gnpgs > 0 \n" //
                + "   straight_join germplsm g on g.gnpgs < 0 and group_source.gid = g.gpid1 \n"  //
                + "                            or g.gnpgs > 0 and group_source.gid = g.gid \n" //
                + " where n.nstat != " + STATUS_DELETED + " and n.nval like :femaleParentName " + LIMIT_CLAUSE) //
                .setParameter("femaleParentName", '%' + femaleParentName + '%') //
                .list();
            prefilteredGids.addAll(gids);
        }

        final String maleParentName = germplasmSearchRequest.getMaleParentName();
        if (maleParentName != null) {
            final List<Integer> gids = this.getSession().createSQLQuery("select g.gid from names n \n" //
                + "   straight_join germplsm male_parent on n.gid = male_parent.gid \n" //
                + "   straight_join germplsm group_source on male_parent.gid = group_source.gpid2 and group_source.gnpgs > 0 \n" //
                + "   straight_join germplsm g on g.gnpgs < 0 and group_source.gid = g.gpid1 \n" //
                + "                            or g.gnpgs > 0 and group_source.gid = g.gid \n" //
                + " where n.nstat != " + STATUS_DELETED + " and n.nval like :maleParentName " + LIMIT_CLAUSE) //
                .setParameter("maleParentName", '%' + maleParentName + '%') //
                .list();
            prefilteredGids.addAll(gids);
        }

        final String groupSourceName = germplasmSearchRequest.getGroupSourceName();
        if (groupSourceName != null) {
            final List<Integer> gids = this.getSession().createSQLQuery("select g.gid from names n \n" //
                + " straight_join germplsm group_source on n.gid = group_source.gid \n" //
                + " straight_join germplsm g on group_source.gid = g.gpid1 and g.gnpgs < 0 \n" //
                + " where n.nstat != " + STATUS_DELETED + " and n.nval like :groupSourceName " + LIMIT_CLAUSE) //
                .setParameter("groupSourceName", '%' + groupSourceName + '%')
                .list();
            prefilteredGids.addAll(gids);
        }

        final String immediateSourceName = germplasmSearchRequest.getImmediateSourceName();
        if (immediateSourceName != null) {
            final List<Integer> gids = this.getSession().createSQLQuery("select g.gid from names n \n"
                + " straight_join germplsm immediate_source on n.gid = immediate_source.gid \n"
                + " straight_join germplsm g on immediate_source.gid = g.gpid2 and g.gnpgs < 0 \n"
                + " where n.nstat != " + STATUS_DELETED + " and n.nval like :immediateSourceName " + LIMIT_CLAUSE) //
                .setParameter("immediateSourceName", '%' + immediateSourceName + '%')
                .list();
            prefilteredGids.addAll(gids);
        }

        final Map<String, String> attributes = germplasmSearchRequest.getAttributes();
        if (attributes != null && !attributes.isEmpty()) {
            final StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append(" select distinct a.gid from atributs a \n"
                + "  inner join udflds u on a.atype = u.fldno \n"
                + " where ");
            final Iterator<Map.Entry<String, String>> iterator = attributes.entrySet().iterator();
            while (iterator.hasNext()) {
                final Map.Entry<String, String> entry = iterator.next();
                queryBuilder.append(String.format(" u.fcode = :attributeKey%s and aval like :attributeValue%<s ", entry.getKey()));
                if (iterator.hasNext()) {
                    queryBuilder.append(" or ");
                }
            }
            queryBuilder.append(LIMIT_CLAUSE);

            final SQLQuery sqlQuery = this.getSession().createSQLQuery(queryBuilder.toString());
            for (final Map.Entry<String, String> entry : attributes.entrySet()) {
                sqlQuery.setParameter("attributeKey" + entry.getKey(), entry.getKey());
                sqlQuery.setParameter("attributeValue" + entry.getKey(), '%' + entry.getValue() + '%');
            }

            final List<Integer> gids = sqlQuery.list();
            prefilteredGids.addAll(gids);
        }

        return prefilteredGids;
    }

    /**
     * NOTE: This part of the query has performance implications. Please take this into consideration when making changes.
     * The current setup has been tested on crop=brachiaria, #germplsm: ~ 6M, page size: 1000, include pedigree generative
     * -> 5 levels: 7 sec, 20 levels: 30 sec
     *
     * STRAIGHT_JOIN joins the table in left to right order.
     * For some reason, with "inner join" the query optimizer chooses a suboptimal order and instead of starting with P0 (limited by where)
     * it starts with P1, and the query never finishes. With straight_join, it finishes in seconds.
     *
     * The gnpgs column also plays a key role in the running time. Some tested alternatives that don't make use of this column have shown
     * poor performance.
     *
     * The query doesn't exclude deleted germplasm (g.deleted and g.grplce), because it slows it down considerably.
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
}
