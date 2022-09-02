package org.generationcp.middleware.dao.study;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.dao.AbstractGenericSearchDAO;
import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.dao.util.DAOQueryUtils;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.domain.study.StudyEntrySearchDto;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.ims.LotStatus;
import org.generationcp.middleware.pojos.ims.TransactionStatus;
import org.generationcp.middleware.pojos.ims.TransactionType;
import org.generationcp.middleware.service.api.study.StudyEntryDto;
import org.generationcp.middleware.service.api.study.StudyEntryPropertyData;
import org.generationcp.middleware.util.Scalar;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class StudyEntrySearchDAO extends AbstractGenericSearchDAO<StockModel, Integer> {

	private static final Map<String, String> factorsFilterMap;

	private static final List<Integer> REMOVE_FILTERS = Lists.newArrayList(TermId.FEMALE_PARENT_GID.getId(),
		TermId.FEMALE_PARENT_NAME.getId(), TermId.MALE_PARENT_GID.getId(), TermId.MALE_PARENT_NAME.getId());
	private static final String LIMIT_CLAUSE = " LIMIT 5000 ";

	static {
		factorsFilterMap = new HashMap<>();
		factorsFilterMap.put(String.valueOf(TermId.GID.getId()), "s.dbxref_id");
		factorsFilterMap.put(String.valueOf(TermId.DESIG.getId()), "name.nval");
		factorsFilterMap.put(String.valueOf(TermId.GUID.getId()), "g.germplsm_uuid");
		factorsFilterMap.put(String.valueOf(TermId.CROSS.getId()), "s.cross_value");
		factorsFilterMap.put(String.valueOf(TermId.GROUPGID.getId()), "g.mgid");
		factorsFilterMap.put(String.valueOf(TermId.IMMEDIATE_SOURCE_NAME.getId()), "immediateSource.nval");
		factorsFilterMap.put(String.valueOf(TermId.GROUP_SOURCE_NAME.getId()), "groupSourceName.nval");
		factorsFilterMap.put(String.valueOf(TermId.ENTRY_NO.getId()), "uniquename");
		factorsFilterMap.put(String.valueOf(TermId.GID_ACTIVE_LOTS_COUNT.getId()),
			"EXISTS (SELECT 1 FROM ims_lot l1 WHERE l1.eid = s.dbxref_id and l1.status = " +
				LotStatus.ACTIVE.getIntValue() + " HAVING COUNT(l1.lotid)");
		factorsFilterMap.put(String.valueOf(TermId.GID_UNIT.getId()), "EXISTS("
			+ "select l1.eid, IF(COUNT(DISTINCT IFNULL(l1.scaleid, 'null')) = 1, IFNULL(c1.name, '-'), 'Mixed') as unit1 "
			+ "             from  stock s1"
			+ "                       left join ims_lot l1 on s1.dbxref_id = l1.eid and l1.status = " + LotStatus.ACTIVE.getIntValue()
			+ "                       left join cvterm c1 ON c1.cvterm_id = l1.scaleid where s1.dbxref_id = s.dbxref_id group by l1.eid"
			+ "             having unit1");
	}

	// Base query
	private static final String BASE_QUERY = "SELECT %s " // usage of SELECT_EXPRESION / COUNT_EXPRESSION
		+ " FROM stock s "
		+ " %s " // usage of join clause
		+ " WHERE s.project_id = :studyId %s" // usage of where clause
		+ " %s" // usage of group clause
		+ " %s" // usage of having clause
		+ " %s"; // usage of order clause

	public static final String LOT_AVAILABLE_EXPRESSION =
		"IF(COUNT(DISTINCT IFNULL(l.scaleid, 'null')) = 1, IFNULL((SELECT SUM(CASE WHEN imt.trnstat = "
			+ TransactionStatus.CONFIRMED.getIntValue()
			+ "  OR (imt.trnstat = " + TransactionStatus.PENDING.getIntValue()
			+ " AND imt.trntype = " + TransactionType.WITHDRAWAL.getId() + ") THEN imt.trnqty ELSE 0 END) "
			+ "  FROM ims_transaction imt INNER JOIN ims_lot lo ON lo.lotid = imt.lotid WHERE lo.eid = l.eid),0), 'Mixed')";

	// Aliases
	private static final String ENTRY_ID_ALIAS = "entryId";
	private static final String ENTRY_NO_ALIAS = "entry_no";
	private static final String GID_ALIAS = "gid";
	private static final String DESIGNATION_ALIAS = "designation";
	private static final String LOT_COUNT_ALIAS = "lotCount";
	private static final String LOT_AVAILABLE_BALANCE_ALIAS = "availableBalance";
	private static final String LOT_UNIT_ALIAS = "unit";
	private static final String CROSS_ALIAS = "cross";
	private static final String GROUP_GID_ALIAS = "groupGID";
	private static final String GUID_ALIAS = "guid";

	// Joins
	private static final String LOT_JOIN = "LEFT JOIN ims_lot l ON l.eid = s.dbxref_id and l.status = " + LotStatus.ACTIVE.getIntValue();
	private static final String CVTERM_JOIN = "LEFT JOIN cvterm c ON c.cvterm_id = l.scaleid";
	private static final String STOCK_PROP_JOIN = "LEFT JOIN stockprop sp ON sp.stock_id = s.stock_id";
	private static final String CVTERM_VARIABLE_JOIN = "LEFT JOIN cvterm cvterm_variable ON cvterm_variable.cvterm_id = sp.type_id";
	private static final String GERMPLASM_JOIN = "INNER JOIN germplsm g ON g.gid = s.dbxref_id";
	private static final String NAME_JOIN = "INNER JOIN names name ON name.gid = s.dbxref_id and name.nstat = 1";
	private static final String IMMEDIATE_SOURCE_NAME_JOIN =
		" LEFT JOIN names immediateSource ON g.gpid2 = immediateSource.gid AND immediateSource.nstat = 1 ";
	private static final String GROUP_SOURCE_NAME_JOIN =
		"LEFT JOIN names groupSourceName ON groupSourceName.gid = g.gpid1 AND g.gnpgs < 0";
	private static final String GERMPLASM_PASSPORT_AND_ATTRIBUTE_JOIN = "LEFT JOIN atributs %1$s ON s.dbxref_id = %1$s.gid AND %1$s.atype = %2$s";

	public StudyEntrySearchDAO(final Session session) {
		super(session);
	}

	public List<StudyEntryDto> getStudyEntries(final StudyEntrySearchDto studyEntrySearchDto, final List<MeasurementVariable> entryVariables,
		final Pageable pageable) {

		final Map<String, Object> queryParams = new HashMap<>();
		queryParams.put("studyId", studyEntrySearchDto.getStudyId());

		final List<Scalar> scalars = new ArrayList<>();
		final List<String> selects = new ArrayList<>();
		final Set<String> joins = this.getFixedJoins();

		this.addFixedScalars(scalars, selects);
		this.addDesignationScalar(scalars, selects, entryVariables);
		this.addGroupGidScalar(scalars, selects, entryVariables);
		this.addGuidScalar(scalars, selects, entryVariables);
		this.addImmediateSourceScalar(scalars, selects, entryVariables);
		this.addGroupSourceNameScalar(scalars, selects, entryVariables);
		this.addGermplasmAttributeScalars(scalars, selects, entryVariables);
		this.addEntryDetailScalars(scalars, selects, entryVariables);

		this.addJoins(joins, entryVariables);

		final String whereClause = this.addFilters(studyEntrySearchDto.getFilter(), queryParams);
		final String joinClause = this.getJoinClause(joins);
		final String selectClause = selects.stream().collect(Collectors.joining(","));
		final String orderClause = this.getOrderClause(pageable);

		// Use HAVING clause to filter the records by AVAILABLE BALANCE
		final String havingClause = this.getHavingClause(studyEntrySearchDto.getFilter());
		final String sql =
			this.formatQuery(selectClause, joinClause, whereClause, " GROUP BY s.stock_id ", havingClause, orderClause);

		final SQLQuery query = this.getSession().createSQLQuery(sql);
		query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);

		DAOQueryUtils.addParamsToQuery(query, queryParams);
		scalars.forEach(scalar -> query.addScalar(scalar.getColumnAlias(), scalar.getType()));

		GenericDAO.addPaginationToSQLQuery(query, pageable);

		final List<Map<String, Object>> results = query.list();
		return this.mapResults(results, entryVariables);
	}

	private void addDesignationScalar(final List<Scalar> scalars, final List<String> selects, final List<MeasurementVariable> variables) {
		if (!CollectionUtils.isEmpty(variables)) {
			variables.stream()
				.filter(measurementVariable -> measurementVariable.getTermId() == TermId.DESIG.getId())
				.findFirst()
				.ifPresent(measurementVariable ->
					selects.add(this.addSelectExpression(scalars, "name.nval", DESIGNATION_ALIAS, StringType.INSTANCE))
				);
		}
	}

	public long countFilteredStudyEntries(final StudyEntrySearchDto studyEntrySearchDto, final List<MeasurementVariable> entryVariables) {
		final Map<String, Object> queryParams = new HashMap<>();
		queryParams.put("studyId", studyEntrySearchDto.getStudyId());

		final Set<String> joins = this.getFixedJoins();
		this.addJoins(joins, entryVariables);

		final String joinClause = this.getJoinClause(joins);
		final String whereClause = this.addFilters(studyEntrySearchDto.getFilter(), queryParams);
		final String havingClause = this.getHavingClause(studyEntrySearchDto.getFilter());

		// Compute the AVAILABLE BALANCE first for each entry so that we can filter the rows by AVAILABLE BALANCE using HAVING clause.
		final Set<String> selects = new LinkedHashSet<>();
		selects.add("s.stock_id");
		selects.add(LOT_AVAILABLE_EXPRESSION + " AS " + LOT_AVAILABLE_BALANCE_ALIAS);
		final String derivedTableQuery =
			this.formatQuery(selects.stream().collect(Collectors.joining(",")), joinClause, whereClause, " GROUP BY s.stock_id ",
				havingClause, "");

		// Then count the rows from the derived table result.
		final String sql = String.format("SELECT COUNT(stock_id) FROM (%s) a", derivedTableQuery);

		final SQLQuery query = this.getSession().createSQLQuery(sql);
		DAOQueryUtils.addParamsToQuery(query, queryParams);

		return ((BigInteger) query.uniqueResult()).longValue();
	}

	private void addJoins(final Set<String> joins, final List<MeasurementVariable> entryVariables) {
		entryVariables.forEach(variable -> {
			final int termId = variable.getTermId();

			if (TermId.DESIG.getId() == termId) {
				joins.add(GERMPLASM_JOIN);
				joins.add(NAME_JOIN);
				return;
			}
			if (TermId.GROUPGID.getId() == termId || TermId.GUID.getId() == termId) {
				joins.add(GERMPLASM_JOIN);
				return;
			}
			if (TermId.GROUP_SOURCE_NAME.getId() == termId) {
				joins.add(GERMPLASM_JOIN);
				joins.add(GROUP_SOURCE_NAME_JOIN);
				return;
			}
			if (TermId.IMMEDIATE_SOURCE_NAME.getId() == termId) {
				joins.add(GERMPLASM_JOIN);
				joins.add(IMMEDIATE_SOURCE_NAME_JOIN);
				return;
			}
			if (variable.getVariableType() == VariableType.GERMPLASM_ATTRIBUTE ||
				variable.getVariableType() == VariableType.GERMPLASM_PASSPORT) {
				final String alias = this.formatVariableAlias(termId);
				final String join = String.format(GERMPLASM_PASSPORT_AND_ATTRIBUTE_JOIN, alias, termId);
				joins.add(join);
				return;
			}
		});
	}

	private void addFixedScalars(final List<Scalar> scalars, final List<String> selectClause) {
		selectClause.add(this.addSelectExpression(scalars, "s.stock_id", ENTRY_ID_ALIAS, IntegerType.INSTANCE));
		selectClause.add(this.addSelectExpression(scalars, "CONVERT(S.uniquename, UNSIGNED INT)", ENTRY_NO_ALIAS, IntegerType.INSTANCE));
		selectClause.add(this.addSelectExpression(scalars, "s.dbxref_id", GID_ALIAS, IntegerType.INSTANCE));
		selectClause.add(this.addSelectExpression(scalars, "COUNT(DISTINCT (l.lotid))", LOT_COUNT_ALIAS, IntegerType.INSTANCE));
		selectClause.add(this.addSelectExpression(scalars, LOT_AVAILABLE_EXPRESSION, LOT_AVAILABLE_BALANCE_ALIAS, StringType.INSTANCE));

		selectClause.add(this.addSelectExpression(scalars, "IF(COUNT(DISTINCT ifnull(l.scaleid, 'null')) = 1, IFNULL(c.name,'-'), 'Mixed')",
			LOT_UNIT_ALIAS, StringType.INSTANCE));
		selectClause.add(this.addSelectExpression(scalars, "s.cross_value", CROSS_ALIAS, StringType.INSTANCE));
	}

	private void addGermplasmAttributeScalars(final List<Scalar> scalars, final List<String> selectClause,
		final List<MeasurementVariable> entryVariables) {
		entryVariables.stream()
			.filter(variable -> variable.getVariableType() == VariableType.GERMPLASM_ATTRIBUTE ||
				variable.getVariableType() == VariableType.GERMPLASM_PASSPORT)
			.forEach(variable -> {
				final String alias = this.formatVariableAlias(variable.getTermId());
				selectClause.add(this.addSelectExpression(scalars, String.format("%s.aval", alias), alias, StringType.INSTANCE));
			});
	}

	private void addEntryDetailScalars(final List<Scalar> scalars, final List<String> selectClause, final List<MeasurementVariable> entryVariables) {
		entryVariables
			.stream()
			.filter(variable -> variable.getVariableType() == VariableType.ENTRY_DETAIL && variable.getTermId() != TermId.ENTRY_NO.getId())
			.forEach(variable -> {
				final String entryName = variable.getName();
				selectClause.add(
					this.addSelectExpression(scalars, String.format("MAX(IF(cvterm_variable.name = '%s', sp.value, NULL))", entryName), entryName,
						StringType.INSTANCE));
				selectClause.add(
					this.addSelectExpression(scalars, String.format("MAX(IF(cvterm_variable.name = '%s', sp.stockprop_id, NULL))", entryName),
						entryName + "_propertyId", IntegerType.INSTANCE));
				selectClause.add(
					this.addSelectExpression(scalars, String.format("MAX(IF(cvterm_variable.name = '%s', sp.type_id, NULL))", entryName),
						entryName + "_variableId", IntegerType.INSTANCE));

				final String valueColumnReference =
					(DataType.CATEGORICAL_VARIABLE.getName().equals(variable.getDataType())) ? "cvalue_id" : "value";
				selectClause.add(this.addSelectExpression(scalars,
					String.format("MAX(IF(cvterm_variable.name = '%s', sp.%s, NULL))", entryName, valueColumnReference), entryName + "_value",
					StringType.INSTANCE));
			});
	}

	private void addGroupGidScalar(final List<Scalar> scalars, final List<String> selectClause,
		final List<MeasurementVariable> entryDescriptors) {
		if (!CollectionUtils.isEmpty(entryDescriptors)) {
			entryDescriptors.stream()
				.filter(measurementVariable -> measurementVariable.getTermId() == TermId.GROUPGID.getId())
				.findFirst()
				.ifPresent(measurementVariable ->
					selectClause.add(this.addSelectExpression(scalars, "g.mgid", GROUP_GID_ALIAS, IntegerType.INSTANCE))
				);
		}
	}

	private void addGuidScalar(final List<Scalar> scalars, final List<String> selectClause,
		final List<MeasurementVariable> entryDescriptors) {
		if (!CollectionUtils.isEmpty(entryDescriptors)) {
			entryDescriptors.stream()
				.filter(measurementVariable -> measurementVariable.getTermId() == TermId.GUID.getId())
				.findFirst()
				.ifPresent(measurementVariable ->
					selectClause.add(this.addSelectExpression(scalars, "g.germplsm_uuid", GUID_ALIAS, StringType.INSTANCE))
				);
		}
	}

	private void addImmediateSourceScalar(final List<Scalar> scalars, final List<String> selectClause,
		final List<MeasurementVariable> entryDescriptors) {
		if (!CollectionUtils.isEmpty(entryDescriptors)) {
			entryDescriptors.stream()
				.filter(measurementVariable -> measurementVariable.getTermId() == TermId.IMMEDIATE_SOURCE_NAME.getId())
				.findFirst()
				.ifPresent(measurementVariable ->
					selectClause.add(this.addSelectExpression(scalars,
						"( CASE \n"
							+ " WHEN g.gnpgs = -1 \n"
							+ " AND g.gpid2 IS NOT NULL\n"
							+ " AND g.gpid2 <> 0 THEN immediateSource.nval\n"
							+ " ELSE '-' END ) ", TermId.IMMEDIATE_SOURCE_NAME.name(), StringType.INSTANCE))
				);
		}
	}

	private void addGroupSourceNameScalar(final List<Scalar> scalars, final List<String> selectClause,
		final List<MeasurementVariable> entryDescriptors) {
		if (!CollectionUtils.isEmpty(entryDescriptors)) {
			entryDescriptors.stream()
				.filter(measurementVariable -> measurementVariable.getTermId() == TermId.GROUP_SOURCE_NAME.getId())
				.findFirst()
				.ifPresent(measurementVariable ->
					selectClause.add(this.addSelectExpression(scalars,
						"( CASE \n"
							+ " WHEN g.gnpgs = -1 \n"
							+ " AND g.gpid1 IS NOT NULL \n"
							+ " AND g.gpid1 <> 0 THEN groupSourceName.nval \n"
							+ "ELSE '-' END ) ", TermId.GROUP_SOURCE_NAME.name(), StringType.INSTANCE))
				);
		}
	}

	private LinkedHashSet<String> getFixedJoins() {
		final LinkedHashSet<String> joins = new LinkedHashSet<>();
		joins.add(LOT_JOIN);
		joins.add(CVTERM_JOIN);
		joins.add(STOCK_PROP_JOIN);
		joins.add(CVTERM_VARIABLE_JOIN);
		return joins;
	}

	private String addFilters(final StudyEntrySearchDto.Filter filter, final Map<String, Object> queryParams) {

		final StringBuilder whereClause = new StringBuilder();
		if (filter!= null && !CollectionUtils.isEmpty(filter.getPreFilteredGids())) {
			whereClause.append(" and s.dbxref_id in (:preFilteredGids) ");
			queryParams.put("preFilteredGids", filter.getPreFilteredGids());
		}

		if (filter == null) {
			return "";
		}

		if (!CollectionUtils.isEmpty(filter.getEntryNumbers())) {
			whereClause.append(" AND s.uniquename in (:entryNumbers)");
			queryParams.put("entryNumbers", filter.getEntryNumbers());
		}
		if (!CollectionUtils.isEmpty(filter.getEntryIds())) {
			whereClause.append(" AND s.stock_id in (:entryIds)");
			queryParams.put("entryIds", filter.getEntryIds());
		}

		final Map<String, List<String>> filteredValues = filter.getFilteredValues();
		if (!CollectionUtils.isEmpty(filteredValues)) {
			// Perform IN operation on variable values
			this.appendVariableIdAndOperationToFilterQuery(whereClause, filter,
				filteredValues.keySet(), false);

			for (final Map.Entry<String, List<String>> entry : filteredValues.entrySet()) {
				final String variableId = entry.getKey();
				if (factorsFilterMap.get(variableId) == null) {
					queryParams.put(variableId + "_Id", variableId);
				}
				final String finalId = variableId.replace("-", "");
				queryParams.put(finalId + "_values", filteredValues.get(variableId));
			}
		}

		final Map<String, String> filteredTextValues = filter.getFilteredTextValues();
		if (!CollectionUtils.isEmpty(filteredTextValues)) {
			// Perform LIKE operation on variable value
			this.appendVariableIdAndOperationToFilterQuery(whereClause, filter,
				filteredTextValues.keySet(), true);

			for (final Map.Entry<String, String> entry : filteredTextValues.entrySet()) {
				final String variableId = entry.getKey();

				if (REMOVE_FILTERS.contains(Integer.valueOf(variableId))) {
					continue;
				}
				// Skip WHERE filter for LOT AVAILABLE BALANCE. AVAILABLE BALANCE will be filtered using the HAVING clause.
				if (String.valueOf(TermId.GID_AVAILABLE_BALANCE.getId()).equals(variableId)) {
					continue;
				}

				final String variableType = filter.getVariableTypeMap().get(variableId);
				if (!VariableType.GERMPLASM_ATTRIBUTE.name().equals(variableType) &&
					!VariableType.GERMPLASM_PASSPORT.name().equals(variableType) &&
					factorsFilterMap.get(variableId) == null) {
					queryParams.put(variableId + "_Id", variableId);
				}
				final String finalId = variableId.replace("-", "");
				queryParams.put(finalId + "_text", "%" + filteredTextValues.get(variableId) + "%");
			}
		}
		return whereClause.toString();
	}

	private void addFixedVariableIfPresent(final TermId termId, final String value, final List<MeasurementVariable> entryDescriptors,
		final Map<Integer, StudyEntryPropertyData> variables) {
		final Optional<MeasurementVariable>
			measurementVariable =
			entryDescriptors.stream().filter(v -> v.getTermId() == termId.getId())
				.findFirst();
		if (measurementVariable.isPresent()) {
			variables.put(
				measurementVariable.get().getTermId(), new StudyEntryPropertyData(value));
		}
	}

	private void appendVariableIdAndOperationToFilterQuery(final StringBuilder sql,
		final StudyEntrySearchDto.Filter filter,
		final Set<String> variableIds, final boolean performLikeOperation) {
		for (final String variableId : variableIds) {
			if (REMOVE_FILTERS.contains(Integer.valueOf(variableId))) {
				continue;
			}
			final String variableTypeString = filter.getVariableTypeMap().get(variableId);
			this.applyFactorsFilter(sql, variableId, variableTypeString, performLikeOperation);
		}
	}

	private void applyFactorsFilter(final StringBuilder sql, final String variableId, final String variableType,
		final boolean performLikeOperation) {
		final String filterClause = factorsFilterMap.get(variableId);
		if (filterClause != null) {
			final String finalId = variableId.replace("-", "");
			final String matchClause = performLikeOperation ? " LIKE :" + finalId + "_text " : " IN (:" + finalId + "_values) ";
			sql.append(" AND ").append(filterClause).append(matchClause);
			if (variableId.equalsIgnoreCase(String.valueOf(TermId.GID_ACTIVE_LOTS_COUNT.getId())) ||
				variableId.equalsIgnoreCase(String.valueOf(TermId.GID_AVAILABLE_BALANCE.getId())) ||
				variableId.equalsIgnoreCase(String.valueOf(TermId.GID_UNIT.getId())))
				sql.append(") ");
			return;
		}

		if (VariableType.GERMPLASM_PASSPORT.name().equals(variableType) || VariableType.GERMPLASM_ATTRIBUTE.name().equals(variableType)) {
			final String alias = this.formatVariableAlias(variableId);
			sql.append(String.format(" AND %s.aval LIKE :%s_text", alias, variableId));
			return;
		}

		// Otherwise, look in "props" tables
		// If doing text searching, perform LIKE operation. Otherwise perform value "IN" operation
		if (VariableType.GERMPLASM_DESCRIPTOR.name().equals(variableType) || VariableType.ENTRY_DETAIL.name().equals(variableType)) {
			// IF searching by list of values, search for the values in:
			// 1)cvterm.name (for categorical variables) or
			// 2)perform IN operation on stockprop.value
			// Otherwise, search the value like a text by LIKE operation
			final String stockMatchClause = performLikeOperation ? "sp.value LIKE :" + variableId + "_text " :
				" (cvt.name IN (:" + variableId + "_values) OR sp.value IN (:" + variableId + "_values ))";
			sql.append(" AND EXISTS ( SELECT 1 FROM stockprop sp "
				+ "LEFT JOIN cvterm cvt ON cvt.cvterm_id = sp.value "
				+ "WHERE sp.stock_id = s.stock_id AND sp.type_id = :" + variableId
				+ "_Id AND ").append(stockMatchClause).append(" )");
		}
	}

	private String getHavingClause(final StudyEntrySearchDto.Filter filter) {

		if (Objects.isNull(filter) || CollectionUtils.isEmpty(filter.getFilteredTextValues())) {
			return StringUtils.EMPTY;
		}

		if (filter.getFilteredTextValues().containsKey(String.valueOf(TermId.GID_AVAILABLE_BALANCE.getId()))) {
			return String.format("HAVING %s LIKE '%%%s%%'", LOT_AVAILABLE_BALANCE_ALIAS,
				filter.getFilteredTextValues().get(String.valueOf(TermId.GID_AVAILABLE_BALANCE.getId())));
		}

		return StringUtils.EMPTY;
	}

	private String getOrderClause(final Pageable pageable) {
		if (Objects.isNull(pageable) || Objects.isNull(pageable.getSort())) {
			return "";
		}

		final String sortBy = pageable.getSort().iterator().hasNext() ? pageable.getSort().iterator().next().getProperty() : "";
		final String sortOrder = pageable.getSort().iterator().hasNext() ? pageable.getSort().iterator().next().getDirection().name() : "";
		final String direction = StringUtils.isNotBlank(sortOrder) ? sortOrder : "asc";

		Optional<String> orderColumn = Optional.empty();
		if (NumberUtils.isNumber(sortBy)) {
			if (String.valueOf(TermId.GID_ACTIVE_LOTS_COUNT.getId()).equalsIgnoreCase(sortBy)) {
				orderColumn = Optional.of(LOT_COUNT_ALIAS);
			} else if (String.valueOf(TermId.GID_UNIT.getId()).equalsIgnoreCase(sortBy)) {
				orderColumn = Optional.of(LOT_UNIT_ALIAS);
			} else if (String.valueOf(TermId.GID_AVAILABLE_BALANCE.getId()).equalsIgnoreCase(sortBy)) {
				orderColumn = Optional.of(LOT_AVAILABLE_BALANCE_ALIAS);
			}
		} else if (StringUtils.isNotBlank(sortBy)) {
			orderColumn = Optional.of(sortBy);
		}

		if (orderColumn.isPresent()) {
			return " ORDER BY `" + orderColumn.get() + "` " + direction;
		}
		return "";
	}

	private String formatQuery(final String selectExpression, final String joinClause, final String whereClause, final String groupClause,
		final String havingClause,
		final String orderClause) {
		return String.format(BASE_QUERY, selectExpression, joinClause, whereClause, groupClause, havingClause, orderClause);
	}

	private List<StudyEntryDto> mapResults(final List<Map<String, Object>> results, final List<MeasurementVariable> entryVariables) {
		return results.stream().map(row -> {
			final Integer entryId = (Integer) row.get(ENTRY_ID_ALIAS);
			final Integer entryNumber = (Integer) row.get(ENTRY_NO_ALIAS);
			final Integer gid = (Integer) row.get(GID_ALIAS);
			final String designation = (String) row.get(DESIGNATION_ALIAS);
			final Integer lotCount = (Integer) row.get(LOT_COUNT_ALIAS);
			final String availableBalance = (String) row.get(LOT_AVAILABLE_BALANCE_ALIAS);
			final String unit = (String) row.get(LOT_UNIT_ALIAS);
			final String cross = (String) row.get(CROSS_ALIAS);
			final Integer groupGid = (Integer) row.get(GROUP_GID_ALIAS);
			final String guid = (String) row.get(GUID_ALIAS);

			final StudyEntryDto studyEntryDto =
				new StudyEntryDto(entryId, entryNumber, gid, designation, lotCount, availableBalance, unit, cross, groupGid, guid);
			final Map<Integer, StudyEntryPropertyData> properties = new HashMap<>();
			entryVariables.stream()
				.filter(variable -> VariableType.ENTRY_DETAIL == variable.getVariableType())
				.forEach(variable -> {
					final String value;
					final Integer categoricalValueId;
					if (variable.getDataType().equals(DataType.CATEGORICAL_VARIABLE.getName())) {
						value = (String) row.get(variable.getName());
						categoricalValueId = row.get(variable.getName() + "_value") != null ?
							Integer.valueOf((String) row.get(variable.getName() + "_value")) : null;
					} else {
						value = (String) row.get(variable.getName() + "_value");
						categoricalValueId = null;
					}

					final StudyEntryPropertyData studyEntryPropertyData =
						new StudyEntryPropertyData((Integer) row.get(variable.getName() + "_propertyId"),
							(Integer) row.get(variable.getName() + "_variableId"),
							value,
							categoricalValueId);
					properties.put(variable.getTermId(), studyEntryPropertyData);
				});

			entryVariables.stream()
				.filter(variable -> VariableType.GERMPLASM_ATTRIBUTE == variable.getVariableType() ||
					VariableType.GERMPLASM_PASSPORT == variable.getVariableType())
				.forEach(variable -> {
					final String variableAlias = this.formatVariableAlias(variable.getTermId());
					final String value = (String) row.get(variableAlias);
					properties.put(variable.getTermId(), new StudyEntryPropertyData(value));
				});

			//These elements should not be listed as germplasm descriptors, this is a way to match values between column
			//and table cells. In the near future this block should be removed
			this.addFixedVariableIfPresent(TermId.GID, String.valueOf(studyEntryDto.getGid()), entryVariables, properties);
			this.addFixedVariableIfPresent(TermId.GUID, String.valueOf(studyEntryDto.getGuid()), entryVariables, properties);
			this.addFixedVariableIfPresent(TermId.DESIG, studyEntryDto.getDesignation(), entryVariables, properties);
			this.addFixedVariableIfPresent(TermId.ENTRY_NO, String.valueOf(studyEntryDto.getEntryNumber()),	entryVariables, properties);
			this.addFixedVariableIfPresent(TermId.IMMEDIATE_SOURCE_NAME, String.valueOf(row.get(TermId.IMMEDIATE_SOURCE_NAME.name())),
				entryVariables, properties);
			this.addFixedVariableIfPresent(TermId.GROUP_SOURCE_NAME, String.valueOf(row.get(TermId.GROUP_SOURCE_NAME.name())),
				entryVariables, properties);

			studyEntryDto.setProperties(properties);
			return studyEntryDto;
		}).collect(Collectors.toList());
	}

	private String formatVariableAlias(final Object variableId) {
		return String.format("VARIABLE_%s", variableId);
	}

	public Set<Integer>  addPreFilteredGids(final StudyEntrySearchDto.Filter filter) {
		String femaleName = null;
		String maleName = null;
		String femaleGid = null;
		String maleGid = null;
		final Set<Integer> preFilteredGids = new HashSet<Integer>();
		if (filter.getFilteredTextValues().containsKey(String.valueOf(TermId.FEMALE_PARENT_NAME.getId()))) {
			femaleName = filter.getFilteredTextValues().get(String.valueOf(TermId.FEMALE_PARENT_NAME.getId()));
		}

		if (filter.getFilteredTextValues().containsKey(String.valueOf(TermId.FEMALE_PARENT_GID.getId()))) {
			final String filterValue = filter.getFilteredTextValues().get(String.valueOf(TermId.FEMALE_PARENT_GID.getId()));
			femaleGid = filterValue.equalsIgnoreCase("UNKNOWN") ? "0" : filterValue;

		}

		if (filter.getFilteredTextValues().containsKey(String.valueOf(TermId.MALE_PARENT_NAME.getId()))) {
			maleName = filter.getFilteredTextValues().get(String.valueOf(TermId.MALE_PARENT_NAME.getId()));
		}

		if (filter.getFilteredTextValues().containsKey(String.valueOf(TermId.MALE_PARENT_GID.getId()))) {
			final String filterValue = filter.getFilteredTextValues().get(String.valueOf(TermId.MALE_PARENT_GID.getId()));
			maleGid = filterValue.equalsIgnoreCase("UNKNOWN") ? "0" : filterValue;
		}

		if (StringUtils.isNotBlank(femaleName) || StringUtils.isNotBlank(femaleGid)) {
			StringBuilder sql = new StringBuilder("select g.gid from names n \n");//
			sql.append("   straight_join germplsm female_parent on n.gid = female_parent.gid \n");//
			sql.append("   straight_join germplsm group_source on female_parent.gid = group_source.gpid1 and group_source.gnpgs > 0 \n");//
			sql.append("   straight_join germplsm g on g.gnpgs < 0 and group_source.gid = g.gpid1 \n");  //
			sql.append("                            or g.gnpgs > 0 and group_source.gid = g.gid \n"); //
			sql.append(" where n.nstat = 1 \n");
			if (StringUtils.isNotBlank(femaleName)) {
				sql.append(" and n.nval = :femaleName "); //
			}
			if (StringUtils.isNotBlank(femaleGid)) {
				sql.append(" and female_parent.gid = :femaleGid "); //
			}
			sql.append(LIMIT_CLAUSE); //
			final SQLQuery query = this.getSession().createSQLQuery(sql.toString());

			if (StringUtils.isNotBlank(femaleName)) {
				query.setParameter("femaleName", femaleName); //
			}
			if (StringUtils.isNotBlank(femaleGid)) {
				query.setParameter("femaleGid", femaleGid); //
			}

			final List<Integer> gids = query.list();
			if (!gids.isEmpty()) {
				preFilteredGids.addAll(gids);
			}
		}

		if (StringUtils.isNotBlank(maleName) || StringUtils.isNotBlank(maleGid)) {
			StringBuilder sql = new StringBuilder("select g.gid from names n \n");//
			sql.append("   straight_join germplsm male_parent on n.gid = male_parent.gid \n");//
			sql.append("   straight_join germplsm group_source on male_parent.gid = group_source.gpid2 and group_source.gnpgs > 0 \n");//
			sql.append("   straight_join germplsm g on g.gnpgs < 0 and group_source.gid = g.gpid1 \n");  //
			sql.append("                            or g.gnpgs > 0 and group_source.gid = g.gid \n"); //
			sql.append(" where n.nstat = 1 \n");

			if (StringUtils.isNotBlank(maleName)) {
				sql.append(" and n.nval = :maleName "); //
			}
			if (StringUtils.isNotBlank(maleGid)) {
				sql.append(" and male_parent.gid = :maleGid "); //
			}
			sql.append(LIMIT_CLAUSE); //

			final SQLQuery query = this.getSession().createSQLQuery(sql.toString());

			if (StringUtils.isNotBlank(maleName)) {
				query.setParameter("maleName", maleName); //
			}
			if (StringUtils.isNotBlank(maleGid)) {
				query.setParameter("maleGid", maleGid); //
			}

			final List<Integer> gids = query.list();
			if (!gids.isEmpty()) {
				preFilteredGids.addAll(gids);
			}
		}

		return preFilteredGids;
	}
}
