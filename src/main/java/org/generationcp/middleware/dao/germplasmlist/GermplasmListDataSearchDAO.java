package org.generationcp.middleware.dao.germplasmlist;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.api.germplasmlist.data.GermplasmListStaticColumns;
import org.generationcp.middleware.api.germplasmlist.data.GermplasmListDataSearchRequest;
import org.generationcp.middleware.api.germplasmlist.data.GermplasmListDataSearchResponse;
import org.generationcp.middleware.api.germplasmlist.data.GermplasmListDataViewModel;
import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.sqlfilter.SqlTextFilter;
import org.generationcp.middleware.pojos.GermplasmListColumnCategory;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.ims.LotStatus;
import org.generationcp.middleware.pojos.ims.TransactionStatus;
import org.generationcp.middleware.pojos.ims.TransactionType;
import org.generationcp.middleware.util.Util;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GermplasmListDataSearchDAO extends GenericDAO<GermplasmListData, Integer> {

	// TODO: move to utils
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(Util.DATE_AS_NUMBER_FORMAT);

	// TODO: define in common constants
	private static final String MIXED_UNITS_LABEL = "Mixed";
	// TODO: use name status
	private static final Integer NAME_DELETED_STATUS = 9;

	public GermplasmListDataSearchDAO(final Session session) {
		super(session);
	}

	// Base query
	private final static String BASE_QUERY = "SELECT %s " // usage of SELECT_EXPRESION / COUNT_EXPRESSION
		+ " FROM listdata listData "
		+ " %s " // usage of join clause
		+ " WHERE %s" // usage of where clause
		+ "			%s" // usage of group clause
		+ "			%s"; // usage of order clause
	private static final String COUNT_EXPRESSION = " COUNT(1) ";

	// Alias for columns which are not columns of the table
	static final String LIST_DATA_ID_ALIAS = "listDataId";
	static final String LOCATION_ID_ALIAS = "LOCATION_ID";
	static final String BREEDING_METHOD_ID_ALIAS = "BREEDING_METHOD_ID";

	// Join clause
	private static final String GERMPLASM_JOIN = "INNER JOIN germplsm g ON g.gid = listData.gid";
	private static final String DESIGNATION_JOIN =
		"LEFT JOIN names designation ON listData.gid = designation.gid AND designation.nstat = 1";
	private static final String LOT_JOIN =
		String.format("LEFT JOIN ims_lot lot ON listData.gid = lot.eid AND etype = 'GERMPLSM' AND lot.status = %s",
			LotStatus.ACTIVE.getIntValue());
	private static final String LOT_UNIT_SCALE_JOIN = "LEFT JOIN cvterm scale ON scale.cvterm_id = lot.scaleid";
	private static final String TRANSACTION_JOIN =
		String
			.format("LEFT JOIN ims_transaction gt ON gt.lotid = lot.lotid AND gt.trnstat <> %s", TransactionStatus.CANCELLED.getIntValue());
	private static final String GROUP_SOURCE_NAME_JOIN =
		"LEFT JOIN names groupSource ON g.gpid1 = groupSource.gid AND groupSource.nstat = 1";
	private static final String IMMEDIATE_SOURCE_NAME_JOIN =
		"LEFT JOIN names immediateSource ON g.gpid2 = immediateSource.gid AND immediateSource.nstat = 1";
	private static final String BREEDING_METHOD_JOIN = "LEFT JOIN methods method ON method.mid = g.methn";
	private static final String LOCATION_JOIN = "LEFT JOIN location loc ON loc.locid = g.glocn";
	private static final String REFERENCE_JOIN = "LEFT JOIN bibrefs ref ON ref.refid = g.gref";

	private static final String GROUP_SOURCE_NAME_FILTER_JOIN =
		"LEFT JOIN names groupSourceFilterName ON groupSourceFilterName.gid = g.gpid1 AND g.gnpgs < 0";
	private static final String IMMEDIATE_SOURCE_NAME_FILTER_JOIN =
		"LEFT JOIN names immediateSourceFilterName ON immediateSourceFilterName.gid = g.gpid2 AND g.gnpgs < 0";
	private static final String FEMALE_PARENT_NAME_FILTER_JOIN =
		"LEFT JOIN names femaleParentName ON femaleParentName.gid = g.gpid1 AND femaleParentName.nstat = 1";
	private static final String MALE_PARENT_NAME_FILTER_JOIN =
		"LEFT JOIN names maleParentName ON maleParentName.gid = g.gpid2 AND maleParentName.nstat = 1";

	public List<GermplasmListDataSearchResponse> searchGermplasmListData(final Integer listId,
		final List<GermplasmListDataViewModel> view,
		final GermplasmListDataSearchRequest request, final Pageable pageable) {

		final Map<String, Object> queryParams = new HashMap<>();
		queryParams.put("listId", listId);

		final List<String> scalars = new ArrayList<>();
		final List<String> selects = new ArrayList<>();
		final Set<String> joins = new LinkedHashSet<>();
		joins.add(GERMPLASM_JOIN);

		final List<Integer> staticColumnIds = new ArrayList<>();
		view.forEach(column -> {
			if (column.isStaticColumn()) {
				staticColumnIds.add(column.getColumnId());
				return;
			}

			if (column.isNameColumn()) {
				this.addNameScalar(scalars, selects, joins, column.getColumnId());
				return;
			}

			if (column.isDescriptorColumn()) {
				this.addDescriptorScalar(scalars, selects, joins, column.getColumnId());
				return;
			}

			if (column.isEntryDetailColumn()) {
				this.addEntryDetailScalar(scalars, selects, joins, column.getColumnId());
			}
		});

		this.addFixedScalars(scalars, selects);
		this.addDesignationScalar(scalars, selects, joins, staticColumnIds);
		this.addGroupSourceNameScalar(scalars, selects, joins, staticColumnIds);
		this.addImmediateSourceNameScalar(scalars, selects, joins, staticColumnIds);
		this.addLotsNumberScalar(scalars, selects, joins, staticColumnIds);
		this.addLotsAvailableScalar(scalars, selects, joins, staticColumnIds);
		this.addLotsUnitScalar(scalars, selects, joins, staticColumnIds);
		this.addBreedingMethodScalar(scalars, selects, joins, staticColumnIds);
		this.addLocationScalar(scalars, selects, joins, staticColumnIds);
		this.addReferenceScalar(scalars, selects, joins, staticColumnIds);

		final List<String> where = this.addFilters(joins, queryParams, request);
		final String whereClause = this.getWhereClause(where);
		final String selectClause = selects.stream().collect(Collectors.joining(","));
		final String joinClause = this.getJoinClause(joins);
		final String orderClause = DAOQueryUtils.getOrderClause(input -> input, pageable);
		final String sql =
			this.formatQuery(selectClause, joinClause, whereClause, " GROUP BY listData.gid, listData.entryid ", orderClause);
		final SQLQuery query = this.getSession().createSQLQuery(sql);
		DAOQueryUtils.addParamsToQuery(query, queryParams);

		scalars.forEach(query::addScalar);

		GenericDAO.addPaginationToSQLQuery(query, pageable);

		final List<Object[]> results = query.list();
		return this.mapToGermplasmListSearchResponse(results, scalars);
	}

	public long countSearchGermplasmListData(final Integer listId, final GermplasmListDataSearchRequest request) {

		final Map<String, Object> queryParams = new HashMap<>();
		queryParams.put("listId", listId);

		final Set<String> joins = new LinkedHashSet<>();
		joins.add(GERMPLASM_JOIN);

		this.addCountQueryJoins(joins, request);

		final List<String> where = this.addFilters(joins, queryParams, request);
		final String joinClause = this.getJoinClause(joins);
		final String whereClause = this.getWhereClause(where);
		final String sql = this.formatQuery(COUNT_EXPRESSION, joinClause, whereClause, "", "");
		final SQLQuery query = this.getSession().createSQLQuery(sql);
		DAOQueryUtils.addParamsToQuery(query, queryParams);

		return ((BigInteger) query.uniqueResult()).longValue();
	}

	private void addCountQueryJoins(final Set<String> joins, final GermplasmListDataSearchRequest request) {
		final SqlTextFilter designationFilter = request.getDesignationFilter();
		if (designationFilter != null && !designationFilter.isEmpty()) {
			joins.add(DESIGNATION_JOIN);
		}

		final SqlTextFilter immediateSourceNameFilter = request.getImmediateSourceName();
		if (immediateSourceNameFilter != null && !immediateSourceNameFilter.isEmpty()) {
			joins.add(IMMEDIATE_SOURCE_NAME_JOIN);
		}

		final SqlTextFilter groupSourceNameFilter = request.getGroupSourceName();
		if (groupSourceNameFilter != null && !groupSourceNameFilter.isEmpty()) {
			joins.add(GROUP_SOURCE_NAME_JOIN);
		}

		if (!StringUtils.isEmpty(request.getBreedingMethodName()) || !StringUtils.isEmpty(request.getBreedingMethodAbbreviation()) ||
			!StringUtils.isEmpty(request.getBreedingMethodGroup())) {
			joins.add(BREEDING_METHOD_JOIN);
		}

		if (!StringUtils.isEmpty(request.getLocationName()) || !StringUtils.isEmpty(request.getLocationAbbreviation())) {
			joins.add(LOCATION_JOIN);
		}

		if (!StringUtils.isEmpty(request.getReference())) {
			joins.add(REFERENCE_JOIN);
		}

		if (!CollectionUtils.isEmpty(request.getNamesFilters())) {
			request.getNamesFilters().forEach((nameTypeId, o) -> {
				final String alias = this.formatNamesAlias(nameTypeId);
				final String join = this.formatNameJoin(alias, nameTypeId);
				joins.add(join);
			});
		}

		if (!CollectionUtils.isEmpty(request.getDescriptorsFilters())) {
			request.getDescriptorsFilters().forEach((variableId, o) -> {
				final String alias = this.formatVariableAlias(variableId);
				final String join = this.formatDescriptorJoin(alias, variableId);
				joins.add(join);
			});
		}

		if (!CollectionUtils.isEmpty(request.getVariablesFilters())) {
			request.getVariablesFilters().forEach((variableId, o) -> {
				final String alias = this.formatVariableAlias(variableId);
				final String join = this.formatEntryDetailJoin(alias, variableId);
				joins.add(join);
			});
		}
	}

	private List<String> addFilters(final Set<String> joins, final Map<String, Object> queryParams,
		final GermplasmListDataSearchRequest request) {
		final List<String> whereClause = new ArrayList<>();
		whereClause.add("listData.listid = :listId");
		whereClause.add("listData.lrstatus <> " + GermplasmListDataDAO.STATUS_DELETED);
		whereClause.add("g.deleted = 0");

		if (!CollectionUtils.isEmpty(request.getEntryNumbers())) {
			queryParams.put("entryNumbers", request.getEntryNumbers());
			whereClause.add("listData.entryId IN (:entryNumbers) ");
		}

		if (!CollectionUtils.isEmpty(request.getGids())) {
			queryParams.put("gids", request.getGids());
			whereClause.add("listData.gid IN (:gids) ");
		}

		if (!StringUtils.isEmpty(request.getGermplasmUUID())) {
			queryParams.put("germplasmUUID", request.getGermplasmUUID());
			whereClause.add("g.germplsm_uuid = :germplasmUUID ");
		}

		final SqlTextFilter designationFilter = request.getDesignationFilter();
		if (designationFilter != null && !designationFilter.isEmpty()) {
			final String value = designationFilter.getValue();
			final SqlTextFilter.Type type = designationFilter.getType();
			final String operator = GenericDAO.getOperator(type);
			queryParams.put("designation", GenericDAO.getParameter(type, value));
			whereClause.add(String.format("%s %s :%s", "designation.nval", operator, "designation"));
		}

		final SqlTextFilter immediateSourceNameFilter = request.getImmediateSourceName();
		if (immediateSourceNameFilter != null && !immediateSourceNameFilter.isEmpty()) {
			final String value = immediateSourceNameFilter.getValue();
			final SqlTextFilter.Type type = immediateSourceNameFilter.getType();
			final String operator = GenericDAO.getOperator(type);
			queryParams.put("immediateSourceName", GenericDAO.getParameter(type, value));
			whereClause.add(String.format("%s %s :%s", "immediateSourceFilterName.nval", operator, "immediateSourceName"));

			joins.add(IMMEDIATE_SOURCE_NAME_FILTER_JOIN);
		}

		final SqlTextFilter groupSourceNameFilter = request.getGroupSourceName();
		if (groupSourceNameFilter != null && !groupSourceNameFilter.isEmpty()) {
			final String value = groupSourceNameFilter.getValue();
			final SqlTextFilter.Type type = groupSourceNameFilter.getType();
			final String operator = GenericDAO.getOperator(type);
			queryParams.put("groupSourceName", GenericDAO.getParameter(type, value));
			whereClause.add(String.format("%s %s :%s", "groupSourceFilterName.nval", operator, "groupSourceName"));

			joins.add(GROUP_SOURCE_NAME_FILTER_JOIN);
		}

		final SqlTextFilter femaleParentNameFilter = request.getFemaleParentName();
		if (femaleParentNameFilter != null && !femaleParentNameFilter.isEmpty()) {
			final String value = femaleParentNameFilter.getValue();
			final SqlTextFilter.Type type = femaleParentNameFilter.getType();
			final String operator = GenericDAO.getOperator(type);
			queryParams.put("femaleParentName", GenericDAO.getParameter(type, value));
			whereClause.add(String.format("%s %s :%s", "femaleParentName.nval", operator, "femaleParentName"));

			joins.add(FEMALE_PARENT_NAME_FILTER_JOIN);
		}

		final SqlTextFilter maleParentNameFilter = request.getMaleParentName();
		if (maleParentNameFilter != null && !maleParentNameFilter.isEmpty()) {
			final String value = maleParentNameFilter.getValue();
			final SqlTextFilter.Type type = maleParentNameFilter.getType();
			final String operator = GenericDAO.getOperator(type);
			queryParams.put("maleParentName", GenericDAO.getParameter(type, value));
			whereClause.add(String.format("%s %s :%s", "maleParentName.nval", operator, "maleParentName"));

			joins.add(MALE_PARENT_NAME_FILTER_JOIN);
		}

		if (!StringUtils.isEmpty(request.getBreedingMethodName())) {
			queryParams.put("breedingMethodName", "%" + request.getBreedingMethodName() + "%");
			whereClause.add("method.mname LIKE :breedingMethodName ");
		}

		if (!StringUtils.isEmpty(request.getBreedingMethodAbbreviation())) {
			queryParams.put("breedingMethodAbbr", "%" + request.getBreedingMethodAbbreviation() + "%");
			whereClause.add("method.mcode LIKE :breedingMethodAbbr ");
		}

		if (!StringUtils.isEmpty(request.getBreedingMethodGroup())) {
			queryParams.put("breedingMethodGroup", "%" + request.getBreedingMethodGroup() + "%");
			whereClause.add("method.mgrp LIKE :breedingMethodGroup ");
		}

		if (!StringUtils.isEmpty(request.getLocationName())) {
			queryParams.put("locationName", "%" + request.getLocationName() + "%");
			whereClause.add("loc.lname LIKE :locationName ");
		}

		if (!StringUtils.isEmpty(request.getLocationAbbreviation())) {
			queryParams.put("locationAbbr", "%" + request.getLocationAbbreviation() + "%");
			whereClause.add("loc.labbr LIKE :locationAbbr ");
		}

		if (request.getGermplasmDateFrom() != null) {
			whereClause.add("g.gdate >= :germplasmDateFrom ");
			queryParams.put("germplasmDateFrom", DATE_FORMAT.format(request.getGermplasmDateFrom()));
		}

		if (request.getGermplasmDateTo() != null) {
			queryParams.put("germplasmDateTo", DATE_FORMAT.format(request.getGermplasmDateTo()));
			whereClause.add("g.gdate <= :germplasmDateTo ");
		}

		if (request.getReference() != null) {
			queryParams.put("reference", "%" + request.getReference() + "%");
			whereClause.add("ref.analyt LIKE :reference ");
		}

		final Map<Integer, Object> namesFilters = request.getNamesFilters();
		if (!CollectionUtils.isEmpty(namesFilters)) {
			namesFilters.forEach((nameTypeId, value) -> {
				final String alias = this.formatNamesAlias(nameTypeId);
				final String paramenterName = String.format("%s_NAME_FILTER", alias);
				queryParams.put(paramenterName, "%" + value + "%");
				whereClause.add(String.format("%s.nval LIKE :%s", alias, paramenterName));
			});
		}

		final Map<Integer, Object> descriptorsFilters = request.getDescriptorsFilters();
		if (!CollectionUtils.isEmpty(descriptorsFilters)) {
			descriptorsFilters.forEach((variableId, value) -> {
				final String alias = this.formatVariableAlias(variableId);
				final String paramenterName = String.format("%s_DESCRIPTOR_FILTER", alias);
				queryParams.put(paramenterName, "%" + value + "%");
				whereClause.add(String.format("%s.aval LIKE :%s", alias, paramenterName));
			});
		}

		final Map<Integer, Object> entryDetailFilters = request.getVariablesFilters();
		if (!CollectionUtils.isEmpty(entryDetailFilters)) {
			entryDetailFilters.forEach((variableId, value) -> {
				final String alias = this.formatVariableAlias(variableId);
				final String paramenterName = String.format("%s_ENTRY_DETAILS_FILTER", alias);
				queryParams.put(paramenterName, "%" + value + "%");
				whereClause.add(String.format("%s.value LIKE :%s", alias, paramenterName));
			});
		}

		return whereClause;
	}

	private void addFixedScalars(final List<String> scalars, final List<String> selectClause) {
		selectClause.add(this.addSelectExpression(scalars, "listData.lrecid", LIST_DATA_ID_ALIAS));
		selectClause.add(this.addSelectExpression(scalars, "listData.entryid", GermplasmListStaticColumns.ENTRY_NO.name()));
		selectClause.add(this.addSelectExpression(scalars, "g.gid", GermplasmListStaticColumns.GID.name()));
		selectClause.add(this.addSelectExpression(scalars, "g.germplsm_uuid", GermplasmListStaticColumns.GUID.name()));
		selectClause
			.add(this.addSelectExpression(scalars, "CAST(g.gdate as CHAR)", GermplasmListStaticColumns.GERMPLASM_DATE.name()));

		final String groupSourceGIDExpression = "CASE \n WHEN g.gnpgs = -1 AND g.gpid1 IS NOT NULL \n"
			+ " AND g.gpid1 <> 0 THEN g.gpid1 \n ELSE '-' \n" + " END \n";
		selectClause.add(this.addSelectExpression(scalars, groupSourceGIDExpression, GermplasmListStaticColumns.GROUP_SOURCE_GID.name()));

		final String immediateSourceGIDExpression = "CASE \n WHEN g.gnpgs = -1 AND g.gpid2 IS NOT NULL \n"
			+ " AND g.gpid2 <> 0 THEN g.gpid2 \n ELSE '-' \n" + " END \n";
		selectClause
			.add(this.addSelectExpression(scalars, immediateSourceGIDExpression, GermplasmListStaticColumns.IMMEDIATE_SOURCE_GID.name()));
	}

	private void addDesignationScalar(final List<String> scalars, final List<String> selectClause,
		final Set<String> joins, final List<Integer> columnVariableIds) {
		if (columnVariableIds.contains(GermplasmListStaticColumns.DESIGNATION.getTermId())) {
			selectClause.add(this.addSelectExpression(scalars, "designation.nval", GermplasmListStaticColumns.DESIGNATION.name()));

			joins.add(DESIGNATION_JOIN);
		}
	}

	private void addGroupSourceNameScalar(final List<String> scalars, final List<String> selectClause,
		final Set<String> joins, final List<Integer> columnVariableIds) {
		if (columnVariableIds.contains(GermplasmListStaticColumns.GROUP_SOURCE_NAME.getTermId())) {
			final String groupSourceNameExpression = "CASE \n  WHEN g.gnpgs = -1 \n AND g.gpid1 IS NOT NULL \n"
				+ " AND g.gpid1 <> 0 THEN groupSource.nval \n ELSE '-' \n" + " END \n";

			selectClause
				.add(this.addSelectExpression(scalars, groupSourceNameExpression, GermplasmListStaticColumns.GROUP_SOURCE_NAME.name()));

			joins.add(GROUP_SOURCE_NAME_JOIN);
		}
	}

	private void addImmediateSourceNameScalar(final List<String> scalars, final List<String> selectClause,
		final Set<String> joins, final List<Integer> columnVariableIds) {
		if (columnVariableIds.contains(GermplasmListStaticColumns.IMMEDIATE_SOURCE_NAME.getTermId())) {
			final String immediateSourceNameExpression = "CASE \n WHEN g.gnpgs = -1 AND g.gpid2 IS NOT NULL \n"
				+ "	AND g.gpid2 <> 0 THEN immediateSource.nval \n" + "	ELSE '-' \n END \n";

			selectClause
				.add(this.addSelectExpression(scalars, immediateSourceNameExpression,
					GermplasmListStaticColumns.IMMEDIATE_SOURCE_NAME.name()));

			joins.add(IMMEDIATE_SOURCE_NAME_JOIN);
		}
	}

	private void addLotsNumberScalar(final List<String> scalars, final List<String> selectClause,
		final Set<String> joins, final List<Integer> columnVariableIds) {
		if (columnVariableIds.contains(GermplasmListStaticColumns.LOTS.getTermId())) {
			selectClause.add(this.addSelectExpression(scalars, "COUNT(DISTINCT lot.lotid)", GermplasmListStaticColumns.LOTS.name()));

			joins.add(LOT_JOIN);
		}
	}

	private void addLotsAvailableScalar(final List<String> scalars, final List<String> selectClause,
		final Set<String> joins, final List<Integer> columnVariableIds) {
		if (columnVariableIds.contains(GermplasmListStaticColumns.AVAILABLE.getTermId())) {
			final String lotAvailableExpression = " IF(COUNT(DISTINCT IFNULL(lot.scaleid, 'null')) = 1, "
				+ "  IFNULL((SELECT SUM(CASE WHEN gt.trnstat = " + TransactionStatus.CONFIRMED.getIntValue()
				+ "    OR (gt.trnstat = " + TransactionStatus.PENDING.getIntValue() //
				+ "    AND gt.trntype = " + TransactionType.WITHDRAWAL.getId() + ") THEN gt.trnqty ELSE 0 END)) " //
				+ "  /(COUNT(gt.trnid)/count(DISTINCT gt.trnid)), 0)" //
				+ " , '" + MIXED_UNITS_LABEL + "')"; // AS  `" + GermplasmSearchDAO.AVAIL_BALANCE + "`, \n"  //
			selectClause.add(this.addSelectExpression(scalars, lotAvailableExpression, GermplasmListStaticColumns.AVAILABLE.name()));

			joins.add(LOT_JOIN);
			joins.add(TRANSACTION_JOIN);
		}
	}

	private void addLotsUnitScalar(final List<String> scalars, final List<String> selectClause,
		final Set<String> joins, final List<Integer> columnVariableIds) {
		if (columnVariableIds.contains(GermplasmListStaticColumns.UNIT.getTermId())) {
			selectClause.add(this.addSelectExpression(scalars,
				" IF(COUNT(DISTINCT IFNULL(lot.scaleid, 'null')) = 1, scale.name, '" + MIXED_UNITS_LABEL + "')",
				GermplasmListStaticColumns.UNIT.name()));

			joins.add(LOT_JOIN);
			joins.add(LOT_UNIT_SCALE_JOIN);
		}
	}

	private void addBreedingMethodScalar(final List<String> scalars, final List<String> selectClause,
		final Set<String> joins, final List<Integer> columnVariableIds) {
		if (columnVariableIds.contains(GermplasmListStaticColumns.BREEDING_METHOD_PREFERRED_NAME.getTermId()) ||
			columnVariableIds.contains(GermplasmListStaticColumns.BREEDING_METHOD_ABBREVIATION.getTermId()) ||
			columnVariableIds.contains(GermplasmListStaticColumns.BREEDING_METHOD_GROUP.getTermId())) {
			selectClause
				.add(this.addSelectExpression(scalars, "method.mid", BREEDING_METHOD_ID_ALIAS));
			selectClause
				.add(this.addSelectExpression(scalars, "method.mname", GermplasmListStaticColumns.BREEDING_METHOD_PREFERRED_NAME.name()));
			selectClause
				.add(this.addSelectExpression(scalars, "method.mcode", GermplasmListStaticColumns.BREEDING_METHOD_ABBREVIATION.name()));
			selectClause.add(this.addSelectExpression(scalars, "method.mgrp", GermplasmListStaticColumns.BREEDING_METHOD_GROUP.name()));

			joins.add(BREEDING_METHOD_JOIN);
		}
	}

	private void addLocationScalar(final List<String> scalars, final List<String> selectClause,
		final Set<String> joins, final List<Integer> columnVariableIds) {
		if (columnVariableIds.contains(GermplasmListStaticColumns.LOCATION_NAME.getTermId()) ||
			columnVariableIds.contains(GermplasmListStaticColumns.LOCATION_ABBREVIATION.getTermId())) {
			selectClause.add(this.addSelectExpression(scalars, "loc.locid", LOCATION_ID_ALIAS));
			selectClause.add(this.addSelectExpression(scalars, "loc.lname", GermplasmListStaticColumns.LOCATION_NAME.name()));
			selectClause.add(this.addSelectExpression(scalars, "loc.labbr", GermplasmListStaticColumns.LOCATION_ABBREVIATION.name()));

			joins.add(LOCATION_JOIN);
		}
	}

	private void addReferenceScalar(final List<String> scalars, final List<String> selectClause,
		final Set<String> joins, final List<Integer> columnVariableIds) {
		if (columnVariableIds.contains(GermplasmListStaticColumns.GERMPLASM_REFERENCE.getTermId())) {
			selectClause.add(this.addSelectExpression(scalars, "ref.analyt", GermplasmListStaticColumns.GERMPLASM_REFERENCE.name()));

			joins.add(REFERENCE_JOIN);
		}
	}

	private void addNameScalar(final List<String> scalars, final List<String> selectClause,
		final Set<String> joins, final Integer nameTypeId) {

		final String alias = this.formatNamesAlias(nameTypeId);
		selectClause
			.add(this.addSelectExpression(scalars, String.format("%s.nval", alias), alias));

		final String join = this.formatNameJoin(alias, nameTypeId);
		joins.add(join);
	}

	private void addDescriptorScalar(final List<String> scalars, final List<String> selectClause,
		final Set<String> joins, final Integer variableId) {

		final String alias = this.formatVariableAlias(variableId);
		selectClause.add(this.addSelectExpression(scalars, String.format("%s.aval", alias), alias));

		final String join = this.formatDescriptorJoin(alias, variableId);
		joins.add(join);
	}

	private void addEntryDetailScalar(final List<String> scalars, final List<String> selectClause,
		final Set<String> joins, final Integer variableId) {

		final String alias = this.formatVariableAlias(variableId);
		selectClause.add(this.addSelectExpression(scalars, String.format("%s.id", alias), alias + "_DETAIL_ID"));
		selectClause.add(this.addSelectExpression(scalars, String.format("%s.value", alias), alias));

		final String join = this.formatEntryDetailJoin(alias, variableId);
		joins.add(join);
	}

	private String addSelectExpression(final List<String> scalars, final String expression, final String columnAlias) {
		scalars.add(columnAlias);
		return String.format("%s AS %s", expression, columnAlias);
	}

	private String formatQuery(final String selectExpression, final String joinClause, final String whereClause, final String groupClause,
		final String orderClause) {
		return String.format(BASE_QUERY, selectExpression, joinClause, whereClause, groupClause, orderClause);
	}

	private String formatNamesAlias(final Integer nameTypeId) {
		return formatDynamicAlias(GermplasmListColumnCategory.NAMES, nameTypeId);
	}

	private String formatVariableAlias(final Integer variableId) {
		return formatDynamicAlias(GermplasmListColumnCategory.VARIABLE, variableId);
	}

	private String formatDynamicAlias(final GermplasmListColumnCategory category, final Integer variableId) {
		return String.format("%s_%s", category, variableId);
	}

	private String formatNameJoin(final String alias, final Integer nameTypeId) {
		return String.format("LEFT JOIN names %1$s ON g.gid = %1$s.gid AND %1$s.ntype = %2$s AND %1$s.nstat <> %3$s", alias, nameTypeId,
			NAME_DELETED_STATUS);
	}

	private String formatDescriptorJoin(final String alias, final Integer variableId) {
		return String.format("LEFT JOIN atributs %1$s ON g.gid = %1$s.gid AND %1$s.atype = %2$s", alias, variableId);
	}

	private String formatEntryDetailJoin(final String alias, final Integer variableId) {
		return String
			.format("LEFT JOIN list_data_details %1$s ON listData.lrecid = %1$s.lrecid AND %1$s.variable_id = %2$s", alias, variableId);
	}

	private String getWhereClause(final List<String> whereClause) {
		return whereClause
			.stream()
			.collect(Collectors.joining(" AND "));
	}

	private String getJoinClause(final Set<String> joins) {
		return joins
			.stream()
			.collect(Collectors.joining("\n"));
	}

	private List<GermplasmListDataSearchResponse> mapToGermplasmListSearchResponse(final List<Object[]> results,
		final List<String> scalars) {
		return results.stream().map(result -> {
			final GermplasmListDataSearchResponse row = new GermplasmListDataSearchResponse();
			final Map<String, Object> data = new HashMap<>();
			IntStream.range(0, scalars.size()).forEach(i -> {
				final String scalar = scalars.get(i);
				if (scalar.equals(LIST_DATA_ID_ALIAS)) {
					row.setListDataId((Integer) result[i]);
					return;
				}
				data.put(scalar, result[i]);
			});
			row.setData(data);
			return row;
		}).collect(Collectors.toList());
	}

}
