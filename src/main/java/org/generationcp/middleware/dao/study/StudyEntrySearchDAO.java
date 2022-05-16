package org.generationcp.middleware.dao.study;

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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class StudyEntrySearchDAO extends AbstractGenericSearchDAO<StockModel, Integer> {

	private static final Map<String, String> factorsFilterMap = new HashMap() {{
		this.put(String.valueOf(TermId.GID.getId()), "s.dbxref_id");
		this.put(String.valueOf(TermId.DESIG.getId()), "s.name");
		this.put(String.valueOf(TermId.ENTRY_NO.getId()), "uniquename");
		this.put(String.valueOf(TermId.GID_ACTIVE_LOTS_COUNT.getId()),
			"EXISTS (SELECT 1 FROM ims_lot l1 WHERE l1.eid = s.dbxref_id and l1.status = " +
				LotStatus.ACTIVE.getIntValue() + " HAVING COUNT(l1.lotid)");
		this.put(String.valueOf(TermId.GID_UNIT.getId()), "EXISTS("
			+ "select l1.eid, IF(COUNT(DISTINCT IFNULL(l1.scaleid, 'null')) = 1, IFNULL(c1.name, '-'), 'Mixed') as unit1 "
			+ "             from  stock s1"
			+ "                       left join ims_lot l1 on s1.dbxref_id = l1.eid and l1.status = " + LotStatus.ACTIVE.getIntValue()
			+ "                       left join cvterm c1 ON c1.cvterm_id = l1.scaleid where s1.dbxref_id = s.dbxref_id group by l1.eid"
			+ "             having unit1");
	}};

	// Base query
	private final static String BASE_QUERY = "SELECT %s " // usage of SELECT_EXPRESION / COUNT_EXPRESSION
		+ " FROM stock s "
		+ " %s " // usage of join clause
		+ " WHERE s.project_id = :studyId %s" // usage of where clause
		+ "			%s" // usage of group clause
		+ "			%s"; // usage of order clause+
	private static final String COUNT_EXPRESSION = " COUNT(DISTINCT S.stock_id) ";

	// Aliases
	private static final String ENTRY_ID_ALIAS = "entryId";
	private static final String ENTRY_NO_ALIAS = "entry_no";
	private static final String GID_ALIAS = "gid";
	private static final String DESIGNATION_ALIAS = "designation";
	private static final String LOT_COUNT_ALIAS = "lotCount";
	private static final String LOT_AVAILABLE_BALANCE_ALIAS = "availableBalance";
	private static final String LOT_UNIT_ALIAS = "unit";
	private static final String CROSS_ALIAS = "crossValue";
	private static final String GROUP_GID_ALIAS = "groupGID";

	// Joins
	private static final String LOT_JOIN = "LEFT JOIN ims_lot l ON l.eid = s.dbxref_id and l.status = " + LotStatus.ACTIVE.getIntValue();
	private static final String CVTERM_JOIN = "LEFT JOIN cvterm c ON c.cvterm_id = l.scaleid";
	private static final String STOCK_PROP_JOIN = "LEFT JOIN stockprop sp ON sp.stock_id = s.stock_id";
	private static final String CVTERM_VARIABLE_JOIN = "LEFT JOIN cvterm cvterm_variable ON cvterm_variable.cvterm_id = sp.type_id";
	private static final String GERMPLASM_JOIN = "INNER JOIN germplsm g ON g.gid = s.dbxref_id";
	private static final String SOURCE_JOIN = " LEFT JOIN names immediateSource ON g.gpid2 = immediateSource.gid AND immediateSource.nstat = 1 ";

	public StudyEntrySearchDAO(final Session session) {
		super(session);
	}

	public List<StudyEntryDto> getStudyEntries(final StudyEntrySearchDto studyEntrySearchDto, final Pageable pageable) {

		final Map<String, Object> queryParams = new HashMap<>();
		queryParams.put("studyId", studyEntrySearchDto.getStudyId());

		final List<Scalar> scalars = new ArrayList<>();
		final List<String> selects = new ArrayList<>();
		final Set<String> joins = this.getFixedJoins();

		this.addFixedScalars(scalars, selects);
		this.addGroupGidScalar(scalars, selects, joins, studyEntrySearchDto.getVariableEntryDescriptors());
		this.addSourceScalar(scalars, selects, joins, studyEntrySearchDto.getFixedEntryDescriptors());
		if (!CollectionUtils.isEmpty(studyEntrySearchDto.getVariableEntryDescriptors())) {
			studyEntrySearchDto.getVariableEntryDescriptors().stream()
				.filter(measurementVariable -> measurementVariable.getTermId() != TermId.GROUPGID.getId())
				.forEach(measurementVariable -> this
				.addVariableEntryDescriptorsScalars(scalars, selects, measurementVariable.getName(), measurementVariable.getDataType()));
		}

		final String whereClause = this.addFilters(studyEntrySearchDto.getFilter(), queryParams);
		final String joinClause = this.getJoinClause(joins);
		final String selectClause = selects.stream().collect(Collectors.joining(","));
		final String orderClause = this.getOrderClause(pageable);
		final String sql =
			this.formatQuery(selectClause, joinClause, whereClause, " GROUP BY s.stock_id ", orderClause);

		final SQLQuery query = this.getSession().createSQLQuery(sql);
		query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);

		DAOQueryUtils.addParamsToQuery(query, queryParams);
		scalars.forEach(scalar -> query.addScalar(scalar.getColumnAlias(), scalar.getType()));

		GenericDAO.addPaginationToSQLQuery(query, pageable);

		final List<Map<String, Object>> results = query.list();
		return this.mapResults(results, studyEntrySearchDto);
	}

	public long countFilteredStudyEntries(final int studyId, final StudyEntrySearchDto.Filter filter) {
		final Map<String, Object> queryParams = new HashMap<>();
		queryParams.put("studyId", studyId);

		final Set<String> joins = this.getFixedJoins();
		final String joinClause = this.getJoinClause(joins);
		final String whereClause = this.addFilters(filter, queryParams);
		final String sql =
			this.formatQuery(COUNT_EXPRESSION, joinClause, whereClause, "", "");

		final SQLQuery query = this.getSession().createSQLQuery(sql);
		DAOQueryUtils.addParamsToQuery(query, queryParams);

		return ((BigInteger) query.uniqueResult()).longValue();
	}

	private void addFixedScalars(final List<Scalar> scalars, final List<String> selectClause) {
		selectClause.add(this.addSelectExpression(scalars, "s.stock_id", ENTRY_ID_ALIAS, IntegerType.INSTANCE));
		selectClause.add(this.addSelectExpression(scalars, "CONVERT(S.uniquename, UNSIGNED INT)", ENTRY_NO_ALIAS, IntegerType.INSTANCE));
		selectClause.add(this.addSelectExpression(scalars, "s.dbxref_id", GID_ALIAS, IntegerType.INSTANCE));
		selectClause.add(this.addSelectExpression(scalars, "s.name", DESIGNATION_ALIAS, StringType.INSTANCE));
		selectClause.add(this.addSelectExpression(scalars, "COUNT(DISTINCT (l.lotid))", LOT_COUNT_ALIAS, IntegerType.INSTANCE));

		final String lotAvailableExpression =
			"IF(COUNT(DISTINCT IFNULL(l.scaleid, 'null')) = 1, IFNULL((SELECT SUM(CASE WHEN imt.trnstat = "
				+ TransactionStatus.CONFIRMED.getIntValue()
				+ "  OR (imt.trnstat = " + TransactionStatus.PENDING.getIntValue()
				+ " AND imt.trntype = " + TransactionType.WITHDRAWAL.getId() + ") THEN imt.trnqty ELSE 0 END) "
				+ "  FROM ims_transaction imt INNER JOIN ims_lot lo ON lo.lotid = imt.lotid WHERE lo.eid = l.eid),0), 'Mixed')";
		selectClause.add(this.addSelectExpression(scalars, lotAvailableExpression, LOT_AVAILABLE_BALANCE_ALIAS, StringType.INSTANCE));

		selectClause.add(this.addSelectExpression(scalars, "IF(COUNT(DISTINCT ifnull(l.scaleid, 'null')) = 1, IFNULL(c.name,'-'), 'Mixed')",
			LOT_UNIT_ALIAS, StringType.INSTANCE));
		selectClause.add(this.addSelectExpression(scalars, "s.cross_value", CROSS_ALIAS, StringType.INSTANCE));
	}

	private void addVariableEntryDescriptorsScalars(final List<Scalar> scalars, final List<String> selectClause, final String entryName,
		final String dataType) {
		selectClause.add(
			this.addSelectExpression(scalars, String.format("MAX(IF(cvterm_variable.name = '%s', sp.value, NULL))", entryName), entryName, StringType.INSTANCE));
		selectClause.add(
			this.addSelectExpression(scalars, String.format("MAX(IF(cvterm_variable.name = '%s', sp.stockprop_id, NULL))", entryName),
				entryName + "_PropertyId", IntegerType.INSTANCE));
		selectClause.add(
			this.addSelectExpression(scalars, String.format("MAX(IF(cvterm_variable.name = '%s', sp.type_id, NULL))", entryName),
				entryName + "_variableId", IntegerType.INSTANCE));

		final String valueColumnReference =
			(DataType.CATEGORICAL_VARIABLE.getName().equals(dataType)) ? "cvalue_id" : "value";
		selectClause.add(this.addSelectExpression(scalars,
			String.format("MAX(IF(cvterm_variable.name = '%s', sp.%s, NULL))", entryName, valueColumnReference), entryName + "_value", StringType.INSTANCE));
	}

	private void addGroupGidScalar(final List<Scalar> scalars, final List<String> selectClause, final Set<String> joins, final List<MeasurementVariable> entryDescriptors) {
		if (!CollectionUtils.isEmpty(entryDescriptors)) {
			entryDescriptors.stream()
				.filter(measurementVariable -> measurementVariable.getTermId() == TermId.GROUPGID.getId())
				.findFirst()
				.ifPresent(measurementVariable -> {
					selectClause.add(this.addSelectExpression(scalars, "g.mgid", GROUP_GID_ALIAS, IntegerType.INSTANCE));
					joins.add(GERMPLASM_JOIN);
				});
		}
	}

	private void addSourceScalar(final List<Scalar> scalars, final List<String> selectClause, final Set<String> joins, final List<MeasurementVariable> entryDescriptors) {
		if (!CollectionUtils.isEmpty(entryDescriptors)) {
			entryDescriptors.stream()
				.filter(measurementVariable -> measurementVariable.getTermId() == TermId.SOURCE.getId())
				.findFirst()
				.ifPresent(measurementVariable -> {
					selectClause.add(this.addSelectExpression(scalars,
						  "	( CASE \n"
						+ "		WHEN g.gnpgs = -1 \n"
						+ "		    AND g.gpid2 IS NOT NULL\n"
						+ "			AND g.gpid2 <> 0 THEN immediateSource.nval\n"
						+ "	ELSE '-' END ) "
						+ "", TermId.SOURCE.name(), StringType.INSTANCE));

					if (!joins.contains(GERMPLASM_JOIN)) {
						joins.add(GERMPLASM_JOIN);
					}
					joins.add(SOURCE_JOIN);
				});
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
		if (filter == null) {
			return "";
		}

		final StringBuilder whereClause = new StringBuilder();
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
				if (factorsFilterMap.get(variableId) == null) {
					queryParams.put(variableId + "_Id", variableId);
				}
				final String finalId = variableId.replace("-", "");
				queryParams.put(finalId + "_text", "%" + filteredTextValues.get(variableId) + "%");
			}
		}
		return whereClause.toString();
	}

	private void addFixedVariableIfPresent(final TermId termId, final String value, final StudyEntrySearchDto studyEntrySearchDto,
		final Map<Integer, StudyEntryPropertyData> variables) {
		final Optional<MeasurementVariable>
			measurementVariable =
			studyEntrySearchDto.getFixedEntryDescriptors().stream().filter(v -> v.getTermId() == termId.getId())
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
				variableId.equalsIgnoreCase(String.valueOf(TermId.GID_UNIT.getId())))
				sql.append(") ");
			return;
		}

		// Otherwise, look in "props" tables
		// If doing text searching, perform LIKE operation. Otherwise perform value "IN" operation
		if (VariableType.GERMPLASM_DESCRIPTOR.name().equals(variableType)) {
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
		final String orderClause) {
		return String.format(BASE_QUERY, selectExpression, joinClause, whereClause, groupClause, orderClause);
	}

	private List<StudyEntryDto> mapResults(final List<Map<String, Object>> results, final StudyEntrySearchDto studyEntrySearchDto) {
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

			final StudyEntryDto studyEntryDto =
				new StudyEntryDto(entryId, entryNumber, gid, designation, lotCount, availableBalance, unit, cross, groupGid);
			final Map<Integer, StudyEntryPropertyData> properties = new HashMap<>();
			for (final MeasurementVariable entryDescriptor : studyEntrySearchDto.getVariableEntryDescriptors()) {
				if (entryDescriptor.getTermId() == TermId.GROUPGID.getId()) {
					continue;
				}

				final String value;
				final Integer categoricalValueId;
				if (entryDescriptor.getDataType().equals(DataType.CATEGORICAL_VARIABLE.getName())) {
					value = (String) row.get(entryDescriptor.getName());
					categoricalValueId = row.get(entryDescriptor.getName() + "_value") != null ?
						Integer.valueOf((String) row.get(entryDescriptor.getName() + "_value")) : null;
				} else {
					value = (String) row.get(entryDescriptor.getName() + "_value");
					categoricalValueId = null;
				}

				final StudyEntryPropertyData studyEntryPropertyData =
					new StudyEntryPropertyData((Integer) row.get(entryDescriptor.getName() + "_propertyId"),
						(Integer) row.get(entryDescriptor.getName() + "_variableId"),
						value,
						categoricalValueId);
				properties.put(entryDescriptor.getTermId(), studyEntryPropertyData);
			}
			//These elements should not be listed as germplasm descriptors, this is a way to match values between column
			//and table cells. In the near future this block should be removed
			this.addFixedVariableIfPresent(TermId.GID, String.valueOf(studyEntryDto.getGid()), studyEntrySearchDto, properties);
			this.addFixedVariableIfPresent(TermId.DESIG, studyEntryDto.getDesignation(), studyEntrySearchDto, properties);
			this.addFixedVariableIfPresent(TermId.ENTRY_NO, String.valueOf(studyEntryDto.getEntryNumber()), studyEntrySearchDto, properties);
			this.addFixedVariableIfPresent(TermId.SOURCE, String.valueOf(row.get(TermId.SOURCE.name())), studyEntrySearchDto, properties);
			studyEntryDto.setProperties(properties);
			return studyEntryDto;
		}).collect(Collectors.toList());
	}

}
