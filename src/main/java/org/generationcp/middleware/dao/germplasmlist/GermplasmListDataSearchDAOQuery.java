package org.generationcp.middleware.dao.germplasmlist;

import org.generationcp.middleware.api.germplasmlist.GermplasmListStaticColumns;
import org.generationcp.middleware.api.germplasmlist.search.GermplasmListDataSearchRequest;
import org.generationcp.middleware.pojos.GermplasmListDataView;
import org.generationcp.middleware.pojos.ims.LotStatus;
import org.generationcp.middleware.pojos.ims.TransactionStatus;
import org.generationcp.middleware.pojos.ims.TransactionType;
import org.generationcp.middleware.util.SQLQueryBuilder;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GermplasmListDataSearchDAOQuery {

	// TODO: define in common constants
	private static final String MIXED_UNITS_LABEL = "Mixed";
	private static final Integer NAME_DELETED_STATUS = 9;


	//TODO: check if it's necessary to do a dynamic sort columns
	enum SortColumn {

		ENTRY_NUMBER(GermplasmListStaticColumns.ENTRY_NO.name());

		private String value;

		SortColumn(final String value) {
			this.value = value;
		}

		static SortColumn getByValue(final String value) {
			return Arrays.stream(SortColumn.values())
				.filter(e -> e.name().equals(value))
				.findFirst()
				.orElseThrow(() -> new IllegalStateException(String.format("Unsupported sort value %s.", value)));
		}
	}


	// Base query
	private final static String BASE_QUERY = "SELECT %s " // usage of SELECT_EXPRESION / COUNT_EXPRESSION
		+ " FROM listdata listData "
		+ " 	INNER JOIN germplsm g ON g.gid = listData.gid"
		+ " %s " // usage of SELECT_JOINS
		+ " WHERE listData.listid = :listId "
		+ "		AND listData.lrstatus <> " + GermplasmListDataDAO.STATUS_DELETED
		+ "		AND g.deleted = 0";
	private static final String COUNT_EXPRESSION = " COUNT(1) ";

	// Alias for columns which are not columns of the table
	static final String LIST_DATA_ID_ALIAS = "listDataId";
	static final String LOCATION_ID_ALIAS = "LOCATION_ID";
	static final String BREEDING_METHOD_ID_ALIAS = "BREEDING_METHOD_ID";

	// Join clause
	private static final String LOT_JOIN =
		String.format("LEFT JOIN ims_lot lot ON listData.gid = lot.eid AND etype = 'GERMPLSM' AND lot.status = %s",
			LotStatus.ACTIVE.getIntValue());
	private static final String TRANSACTION_JOIN =
		String.format("LEFT JOIN ims_transaction gt ON gt.lotid = gl.lotid AND gt.trnstat <> %s", TransactionStatus.CANCELLED.getValue());
	private static final String GROUP_SOURCE_NAME_JOIN =
		"LEFT JOIN names groupSource ON g.gpid1 = groupSource.gid AND groupSource.nstat = 1";
	private static final String IMMEDIATE_SOURCE_NAME_JOIN =
		"LEFT JOIN names immediateSource ON g.gpid2 = immediateSource.gid AND immediateSource.nstat = 1";
	private static final String BREEDING_METHOD_JOIN = "LEFT JOIN methods method ON method.mid = g.methn";
	private static final String LOCATION_JOIN = "LEFT JOIN location loc ON loc.locid = g.glocn";
	private static final String REFERENCE_JOIN = "LEFT JOIN bibrefs ref ON ref.refid = g.gref";

	static SQLQueryBuilder getSelectQuery(final GermplasmListDataSearchRequest request, final List<GermplasmListDataView> view,
		final Pageable pageable) {

		final List<SQLQueryBuilder.Scalar> scalars = new ArrayList<>();
		final List<String> selects = new ArrayList<>();
		final Set<String> joins = new LinkedHashSet<>();
		final List<Integer> staticColumnIds = new ArrayList<>();
		view.forEach(column -> {
			if (column.isStaticColumn()) {
				staticColumnIds.add(column.getVariableId());
				return;
			}

			if (column.isNameColumn()) {
				addNameData(scalars, selects, joins, column.getVariableId());
				return;
			}

			if (column.isDescriptorColumn()) {
				addDescriptorData(scalars, selects, joins, column.getVariableId());
			}
		});

		addFixedScalars(scalars, selects);
		addGroupSourceNameData(scalars, selects, joins, staticColumnIds);
		addImmediateSourceNameData(scalars, selects, joins, staticColumnIds);
		addLotsNumberData(scalars, selects, joins, staticColumnIds);
		addLotsAvailableAndUnitData(scalars, selects, joins, staticColumnIds);
		addBreedingMethodData(scalars, selects, joins, staticColumnIds);
		addLocationData(scalars, selects, joins, staticColumnIds);
		addReferenceData(scalars, selects, joins, staticColumnIds);

		final String selectClause = selects.stream().collect(Collectors.joining(","));
		final String joinClause = joins.stream().collect(Collectors.joining("\n"));

		final String sql = formatQuery(selectClause, joinClause);
		final SQLQueryBuilder sqlQueryBuilder = new SQLQueryBuilder(sql, scalars);

		addFilters(sqlQueryBuilder, request);
		sqlQueryBuilder.append(" GROUP BY g.gid");
		//TODO: get sort from scalars???
		DAOQueryUtils.addOrder(input -> SortColumn.getByValue(input).value, sqlQueryBuilder, pageable);

		return sqlQueryBuilder;
	}

	static SQLQueryBuilder getCountQuery(final GermplasmListDataSearchRequest request) {
		final String baseQuery = formatQuery(COUNT_EXPRESSION, "");
		final SQLQueryBuilder sqlQueryBuilder = new SQLQueryBuilder(baseQuery);
		addFilters(sqlQueryBuilder, request);
		return sqlQueryBuilder;
	}

	private static void addFilters(final SQLQueryBuilder sqlQueryBuilder, final GermplasmListDataSearchRequest request) {
		// TODO: implement filters
	}

	private static void addFixedScalars(final List<SQLQueryBuilder.Scalar> scalars, final List<String> selectClause) {
		selectClause.add(addSelectExpression(scalars, "listData.lrecid", LIST_DATA_ID_ALIAS));
		selectClause.add(addSelectExpression(scalars, "listData.entryid", GermplasmListStaticColumns.ENTRY_NO.name()));
		// TODO: review if we need to get designation from listdata table or from names
		selectClause.add(addSelectExpression(scalars, "listData.desig", GermplasmListStaticColumns.DESIGNATION.name()));
		selectClause.add(addSelectExpression(scalars, "g.gid", GermplasmListStaticColumns.GID.name()));
		selectClause.add(addSelectExpression(scalars, "g.germplsm_uuid", GermplasmListStaticColumns.GUID.name()));
		selectClause
			.add(addSelectExpression(scalars, "CAST(g.gdate as CHAR)", GermplasmListStaticColumns.GERMPLASM_DATE.name()));

		final String groupSourceGIDExpression = "CASE \n WHEN g.gnpgs = -1 AND g.gpid1 IS NOT NULL \n"
			+ " AND g.gpid1 <> 0 THEN g.gpid1 \n ELSE '-' \n" + " END \n";
		selectClause.add(addSelectExpression(scalars, groupSourceGIDExpression, GermplasmListStaticColumns.GROUP_SOURCE_GID.name()));

		final String immediateSourceGIDExpression = "CASE \n WHEN g.gnpgs = -1 AND g.gpid2 IS NOT NULL \n"
			+ " AND g.gpid2 <> 0 THEN g.gpid2 \n ELSE '-' \n" + " END \n";
		selectClause
			.add(addSelectExpression(scalars, immediateSourceGIDExpression, GermplasmListStaticColumns.IMMEDIATE_SOURCE_GID.name()));
	}

	private static void addGroupSourceNameData(final List<SQLQueryBuilder.Scalar> scalars, final List<String> selectClause,
		final Set<String> joins, final List<Integer> columnVariableIds) {
		if (columnVariableIds.contains(GermplasmListStaticColumns.GROUP_SOURCE_NAME.getTermId())) {
			final String groupSourceNameExpression = "CASE \n  WHEN g.gnpgs = -1 \n AND g.gpid1 IS NOT NULL \n"
				+ " AND g.gpid1 <> 0 THEN groupSource.nval \n ELSE '-' \n" + " END \n";

			selectClause
				.add(addSelectExpression(scalars, groupSourceNameExpression, GermplasmListStaticColumns.GROUP_SOURCE_NAME.name()));

			joins.add(GROUP_SOURCE_NAME_JOIN);
		}
	}

	private static void addImmediateSourceNameData(final List<SQLQueryBuilder.Scalar> scalars, final List<String> selectClause,
		final Set<String> joins, final List<Integer> columnVariableIds) {
		if (columnVariableIds.contains(GermplasmListStaticColumns.IMMEDIATE_SOURCE_NAME.getTermId())) {
			final String immediateSourceNameExpression = "CASE \n WHEN g.gnpgs = -1 AND g.gpid2 IS NOT NULL \n"
				+ "	AND g.gpid2 <> 0 THEN immediateSource.nval \n" + "	ELSE '-' \n END \n";

			selectClause
				.add(addSelectExpression(scalars, immediateSourceNameExpression, GermplasmListStaticColumns.IMMEDIATE_SOURCE_NAME.name()));

			joins.add(IMMEDIATE_SOURCE_NAME_JOIN);
		}
	}

	private static void addLotsNumberData(final List<SQLQueryBuilder.Scalar> scalars, final List<String> selectClause,
		final Set<String> joins, final List<Integer> columnVariableIds) {
		if (columnVariableIds.contains(GermplasmListStaticColumns.LOTS.getTermId())) {
			selectClause.add(addSelectExpression(scalars, "COUNT(DISTINCT lot.lotid)", GermplasmListStaticColumns.LOTS.name()));

			joins.add(LOT_JOIN);
		}
	}

	private static void addLotsAvailableAndUnitData(final List<SQLQueryBuilder.Scalar> scalars, final List<String> selectClause,
		final Set<String> joins, final List<Integer> columnVariableIds) {
		if (columnVariableIds.contains(GermplasmListStaticColumns.AVAILABLE.getTermId()) ||
			columnVariableIds.contains(GermplasmListStaticColumns.UNIT.getTermId())) {
			final String lotAvailableExpression = " IF(COUNT(DISTINCT IFNULL(gl.scaleid, 'null')) = 1, "
				+ "  IFNULL((SELECT SUM(CASE WHEN gt.trnstat = " + TransactionStatus.CONFIRMED.getIntValue()
				+ "    OR (gt.trnstat = " + TransactionStatus.PENDING.getIntValue() //
				+ "    AND gt.trntype = " + TransactionType.WITHDRAWAL.getId() + ") THEN gt.trnqty ELSE 0 END)) " //
				+ "  /(COUNT(gt.trnid)/count(DISTINCT gt.trnid)), 0)" //
				+ " , '" + MIXED_UNITS_LABEL + "')"; // AS  `" + GermplasmSearchDAO.AVAIL_BALANCE + "`, \n"  //
			selectClause.add(addSelectExpression(scalars, lotAvailableExpression, GermplasmListStaticColumns.AVAILABLE.name()));
			selectClause.add(addSelectExpression(scalars,
				" IF(COUNT(DISTINCT IFNULL(gl.scaleid, 'null')) = 1, scale.name, " + MIXED_UNITS_LABEL + "')",
				GermplasmListStaticColumns.UNIT.name()));

			joins.add(LOT_JOIN);
			joins.add(TRANSACTION_JOIN);
		}
	}

	private static void addBreedingMethodData(final List<SQLQueryBuilder.Scalar> scalars, final List<String> selectClause,
		final Set<String> joins, final List<Integer> columnVariableIds) {
		if (columnVariableIds.contains(GermplasmListStaticColumns.BREEDING_METHOD_PREFERRED_NAME.getTermId()) ||
			columnVariableIds.contains(GermplasmListStaticColumns.BREEDING_METHOD_ABBREVIATION.getTermId()) ||
			columnVariableIds.contains(GermplasmListStaticColumns.BREEDING_METHOD_GROUP.getTermId())) {
			selectClause
				.add(addSelectExpression(scalars, "method.mid", BREEDING_METHOD_ID_ALIAS));
			selectClause
				.add(addSelectExpression(scalars, "method.mname", GermplasmListStaticColumns.BREEDING_METHOD_PREFERRED_NAME.name()));
			selectClause
				.add(addSelectExpression(scalars, "method.mcode", GermplasmListStaticColumns.BREEDING_METHOD_ABBREVIATION.name()));
			selectClause.add(addSelectExpression(scalars, "method.mgrp", GermplasmListStaticColumns.BREEDING_METHOD_GROUP.name()));

			joins.add(BREEDING_METHOD_JOIN);
		}
	}

	private static void addLocationData(final List<SQLQueryBuilder.Scalar> scalars, final List<String> selectClause,
		final Set<String> joins, final List<Integer> columnVariableIds) {
		if (columnVariableIds.contains(GermplasmListStaticColumns.LOCATION_NAME.getTermId()) ||
			columnVariableIds.contains(GermplasmListStaticColumns.LOCATION_ABBREVIATION.getTermId())) {
			selectClause.add(addSelectExpression(scalars, "loc.locid", LOCATION_ID_ALIAS));
			selectClause.add(addSelectExpression(scalars, "loc.lname", GermplasmListStaticColumns.LOCATION_NAME.name()));
			selectClause.add(addSelectExpression(scalars, "loc.labbr", GermplasmListStaticColumns.LOCATION_ABBREVIATION.name()));

			joins.add(LOCATION_JOIN);
		}
	}

	private static void addReferenceData(final List<SQLQueryBuilder.Scalar> scalars, final List<String> selectClause,
		final Set<String> joins, final List<Integer> columnVariableIds) {
		if (columnVariableIds.contains(GermplasmListStaticColumns.GERMPLASM_REFERENCE.getTermId())) {
			selectClause.add(addSelectExpression(scalars, "ref.analyt", GermplasmListStaticColumns.GERMPLASM_REFERENCE.name()));

			joins.add(REFERENCE_JOIN);
		}
	}

	private static void addDescriptorData(final List<SQLQueryBuilder.Scalar> scalars, final List<String> selectClause,
		final Set<String> joins, final Integer variableId) {

		String alias = "attr_" + variableId;
		selectClause.add(addSelectExpression(scalars, String.format("%s.aval", alias), variableId.toString(), "attr_val"));

		String join = String.format("LEFT JOIN atributs %1$s ON g.gid = %1$s.gid AND %1$s.atype = %2$s",
			alias, variableId);
		joins.add(String.format(join, alias, alias, variableId));
	}

	private static void addNameData(final List<SQLQueryBuilder.Scalar> scalars, final List<String> selectClause,
		final Set<String> joins, final Integer nameTypeId) {

		String alias = "name_" + nameTypeId;
		selectClause.add(addSelectExpression(scalars, String.format("%s.nval", alias), nameTypeId.toString(), "name_val"));

		String join = String.format("LEFT JOIN names %1$s ON g.gid = %1$s.gid AND %1$s.ntype = %2$s AND %1$s.nstat <> %3$s",
			alias, nameTypeId, NAME_DELETED_STATUS);
		joins.add(String.format(join, alias, alias, nameTypeId));
	}

	static String addSelectExpression(final List<SQLQueryBuilder.Scalar> scalars, final String expression, final String columnAlias) {
		return addSelectExpression(scalars, expression, columnAlias, null);
	}

	static String addSelectExpression(final List<SQLQueryBuilder.Scalar> scalars, final String expression, final String columnAlias,
		final String scalarPrefix) {
		final SQLQueryBuilder.Scalar scalar = new SQLQueryBuilder.Scalar(columnAlias, scalarPrefix);
		scalars.add(scalar);
		return String.format("%s AS %s", expression, scalar.getAlias());
	}

	private static String formatQuery(final String selectExpression, final String joinClause) {
		return String.format(BASE_QUERY, selectExpression, joinClause);
	}

}
