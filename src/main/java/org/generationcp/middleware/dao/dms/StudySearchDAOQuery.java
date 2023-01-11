package org.generationcp.middleware.dao.dms;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.api.study.StudySearchRequest;
import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.dao.util.DAOQueryUtils;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.domain.sqlfilter.SqlTextFilter;
import org.generationcp.middleware.util.SQLQueryBuilder;
import org.generationcp.middleware.util.StringUtil;
import org.generationcp.middleware.util.Util;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class StudySearchDAOQuery {

	enum SortColumn {

		STUDY_NAME(STUDY_NAME_ALIAS),
		STUDY_TYPE_NAME(STUDY_TYPE_NAME_ALIAS),
		LOCKED(LOCKED_ALIAS),
		STUDY_OWNER(STUDY_OWNER_ALIAS),
		START_DATE(START_DATE_ALIAS),
		PARENT_FOLDER_NAME(PARENT_FOLDER_NAME_ALIAS),
		OBJECTIVE(OBJECTIVE_ALIAS);

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


	//TODO: move to utils
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(Util.DATE_AS_NUMBER_FORMAT);

	static final String STUDY_ID_ALIAS = "studyId";
	static final String STUDY_NAME_ALIAS = "studyName";
	static final String STUDY_DESCRIPTION_ALIAS = "description";
	static final String STUDY_TYPE_NAME_ALIAS = "studyTypeName";
	static final String LOCKED_ALIAS = "locked";
	static final String STUDY_OWNER_ALIAS = "ownerName";
	static final String START_DATE_ALIAS = "startDate";
	static final String END_DATE_ALIAS = "endDate";
	static final String UPDATE_DATE_ALIAS = "updateDate";
	static final String PARENT_FOLDER_NAME_ALIAS = "parentFolderName";
	static final String OBJECTIVE_ALIAS = "objective";

	private final static String BASE_QUERY = "SELECT %s " // usage of SELECT_EXPRESION / COUNT_EXPRESSION
		+ " FROM project study "
		+ " %s " // usage of SELECT_JOINS
		+ " WHERE study.study_type_id IS NOT NULL AND study.deleted = 0 AND study.program_uuid = :programUUID";

	private final static String SELECT_EXPRESSION = " DISTINCT(study.project_id) AS " + STUDY_ID_ALIAS + ", "
		+ " study.name AS " + STUDY_NAME_ALIAS + ", "
		+ " study.description AS " + STUDY_DESCRIPTION_ALIAS + ", "
		+ " studyType.label AS " + STUDY_TYPE_NAME_ALIAS + ", "
		+ " study.locked AS " + LOCKED_ALIAS + ", "
		+ " user.uname AS " + STUDY_OWNER_ALIAS + ", "
		+ " STR_TO_DATE (convert(study.start_date,char), '%Y%m%d') AS " + START_DATE_ALIAS + ", "
		+ " CASE WHEN study.end_date = '' THEN null ELSE ((STR_TO_DATE(convert(study.end_date, char), '%Y%m%d'))) END as " + END_DATE_ALIAS
		+ ", "
		+ " STR_TO_DATE (convert(study.study_update,char), '%Y%m%d') AS " + UPDATE_DATE_ALIAS + ", "
		+ " inn.name AS " + PARENT_FOLDER_NAME_ALIAS + ", "
		+ " study.objective AS " + OBJECTIVE_ALIAS;

	private final static String SELF_JOIN_QUERY = "LEFT JOIN project inn ON study.parent_project_id = inn.project_id";
	private final static String STUDY_TYPE_JOIN_QUERY =
		"INNER JOIN study_type studyType ON study.study_type_id = studyType.study_type_id";
	private final static String WORKBENCH_USER_JOIN_QUERY = "INNER JOIN workbench.users user ON user.userid = study.created_by";
	private final static String STUDY_SETTINGS_JOIN_QUERY =
		"INNER JOIN projectprop studySetting_%1$s ON study.project_id = studySetting_%1$s.project_id AND studySetting_%1$s.type_id = "
			+ VariableType.STUDY_DETAIL.getId();

	private static final String COUNT_EXPRESSION = " COUNT(DISTINCT study.project_id) ";

	static SQLQueryBuilder getSelectQuery(final StudySearchRequest request,
		final Map<Integer, List<Integer>> categoricalValueReferenceIdsByVariablesIds, final Pageable pageable) {
		final String joins = getSelectQueryJoins(request);
		final String baseQuery =
			String.format(BASE_QUERY, SELECT_EXPRESSION, joins);
		final SQLQueryBuilder sqlQueryBuilder = new SQLQueryBuilder(baseQuery);
		addFilters(sqlQueryBuilder, request, categoricalValueReferenceIdsByVariablesIds);
		sqlQueryBuilder.append(
			DAOQueryUtils.getOrderClause(input -> SortColumn.getByValue(input).value, pageable));
		return sqlQueryBuilder;
	}

	static SQLQueryBuilder getCountQuery(final StudySearchRequest request,
		final Map<Integer, List<Integer>> categoricalValueReferenceIdsByVariablesIds) {
		final String countQueryJoins = getCountQueryJoins(request);
		final String baseQuery = String.format(BASE_QUERY, COUNT_EXPRESSION, countQueryJoins);
		final SQLQueryBuilder sqlQueryBuilder = new SQLQueryBuilder(baseQuery);
		addFilters(sqlQueryBuilder, request, categoricalValueReferenceIdsByVariablesIds);
		return sqlQueryBuilder;
	}

	private static String getSelectQueryJoins(final StudySearchRequest request) {
		final Set<String> joins = new HashSet<>();
		joins.add(SELF_JOIN_QUERY);
		joins.add(STUDY_TYPE_JOIN_QUERY);
		joins.add(WORKBENCH_USER_JOIN_QUERY);
		if (!CollectionUtils.isEmpty(request.getStudySettings())) {
			request.getStudySettings().keySet().forEach(key -> joins.add(String.format(STUDY_SETTINGS_JOIN_QUERY, key)));
		}
		return joins.stream().collect(Collectors.joining(" "));
	}

	private static String getCountQueryJoins(final StudySearchRequest request) {
		final Set<String> joins = new HashSet<>();
		if (!StringUtils.isEmpty(request.getParentFolderName())) {
			joins.add(SELF_JOIN_QUERY);
		}
		if (!StringUtil.isEmpty(request.getOwnerName())) {
			joins.add(WORKBENCH_USER_JOIN_QUERY);
		}
		if (!CollectionUtils.isEmpty(request.getStudySettings())) {
			request.getStudySettings().keySet().forEach(key -> joins.add(String.format(STUDY_SETTINGS_JOIN_QUERY, key)));
		}
		return joins.stream().collect(Collectors.joining(" "));
	}

	private static void addFilters(final SQLQueryBuilder sqlQueryBuilder, final StudySearchRequest request,
		final Map<Integer, List<Integer>> categoricalValueReferenceIdsByVariablesIds) {
		if (!CollectionUtils.isEmpty(request.getStudyIds())) {
			sqlQueryBuilder.setParameter("studyIds", request.getStudyIds());
			sqlQueryBuilder.append(" AND study.project_id IN (:studyIds) ");
		}

		final SqlTextFilter studyNameFilter = request.getStudyNameFilter();
		if (studyNameFilter != null && !studyNameFilter.isEmpty()) {
			final String value = studyNameFilter.getValue();
			final SqlTextFilter.Type type = studyNameFilter.getType();
			final String operator = GenericDAO.getOperator(type);
			sqlQueryBuilder.setParameter("studyName", GenericDAO.getParameter(type, value));
			sqlQueryBuilder.append(" AND study.name ").append(operator).append(":studyName");
		}

		if (!CollectionUtils.isEmpty(request.getStudyTypeIds())) {
			sqlQueryBuilder.setParameter("studyTypeIds", request.getStudyTypeIds());
			sqlQueryBuilder.append(" AND study.study_type_id IN (:studyTypeIds) ");
		}

		if (request.getLocked() != null) {
			sqlQueryBuilder.setParameter("locked", request.getLocked());
			sqlQueryBuilder.append(" AND study.locked = :locked ");
		}

		if (!StringUtils.isEmpty(request.getOwnerName())) {
			sqlQueryBuilder.setParameter("ownerName", "%" + request.getOwnerName() + "%");
			sqlQueryBuilder.append(" AND user.uname LIKE :ownerName ");
		}

		if (request.getStudyStartDateFrom() != null) {
			sqlQueryBuilder.append(" AND study.start_date >= :studyStartDateFrom ");
			sqlQueryBuilder.setParameter("studyStartDateFrom", DATE_FORMAT.format(request.getStudyStartDateFrom()));
		}

		if (request.getStudyStartDateTo() != null) {
			sqlQueryBuilder.append(" AND study.start_date <= :studyStartDateTo ");
			sqlQueryBuilder.setParameter("studyStartDateTo", DATE_FORMAT.format(request.getStudyStartDateTo()));
		}

		if (!StringUtils.isEmpty(request.getParentFolderName())) {
			sqlQueryBuilder.setParameter("parentFolderName", "%" + request.getParentFolderName() + "%");
			sqlQueryBuilder.append(" AND inn.name LIKE :parentFolderName ");
		}

		if (!StringUtils.isEmpty(request.getObjective())) {
			sqlQueryBuilder.setParameter("objective", "%" + request.getObjective() + "%");
			sqlQueryBuilder.append(" AND study.objective LIKE :objective ");
		}

		final Map<Integer, String> studySettings = request.getStudySettings();
		if (!CollectionUtils.isEmpty(studySettings)) {
			studySettings.forEach((key, value) -> {
				final String tableName = "studySetting_" + key;
				final String studySettingIdParameter = "studySetting_" + key;
				sqlQueryBuilder.setParameter(studySettingIdParameter, key);
				sqlQueryBuilder.append(" AND " + tableName + ".variable_id = :" + studySettingIdParameter + " AND " + tableName + ".value ");

				if (categoricalValueReferenceIdsByVariablesIds.containsKey(key)) {
					final String studySettingIdsParameter = "studySettingsCatIds_" + key;
					sqlQueryBuilder.setParameter(studySettingIdsParameter, categoricalValueReferenceIdsByVariablesIds.get(key));
					sqlQueryBuilder.append(" IN (:" + studySettingIdsParameter + ")");
				} else {
					final String studySettingValueParameter = "studySettingValue_" + key;
					sqlQueryBuilder.setParameter(studySettingValueParameter, "%" + value + "%");
					sqlQueryBuilder.append(" LIKE :" + studySettingValueParameter);
				}
			});
		}
	}

}
