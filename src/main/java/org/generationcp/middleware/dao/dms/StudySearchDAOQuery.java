package org.generationcp.middleware.dao.dms;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.api.study.StudySearchRequest;
import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.dao.util.DAOQueryUtils;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.domain.sqlfilter.SqlTextFilter;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.util.Constants;
import org.generationcp.middleware.util.SQLQueryBuilder;
import org.generationcp.middleware.util.StringUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StudySearchDAOQuery {

	enum SortColumn {

		STUDY_NAME(STUDY_NAME_ALIAS),
		STUDY_TYPE_NAME(STUDY_TYPE_NAME_ALIAS),
		LOCKED(LOCKED_ALIAS),
		STUDY_OWNER_NAME(STUDY_OWNER_NAME_ALIAS),
		START_DATE(START_DATE_ALIAS),
		PARENT_FOLDER_NAME(PARENT_FOLDER_NAME_ALIAS),
		OBJECTIVE(OBJECTIVE_ALIAS);

		private final String value;

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

	private static final Map<Integer, String> geoCoordinatesColumnsByTermId = new HashMap<>();

	static {
		geoCoordinatesColumnsByTermId.put(TermId.LATITUDE.getId(), "latitude");
		geoCoordinatesColumnsByTermId.put(TermId.LONGITUDE.getId(), "longitude");
		geoCoordinatesColumnsByTermId.put(TermId.ALTITUDE.getId(), "altitude");
	}

	static final String STUDY_ID_ALIAS = "studyId";
	static final String STUDY_NAME_ALIAS = "studyName";
	static final String STUDY_DESCRIPTION_ALIAS = "description";
	static final String STUDY_TYPE_NAME_ALIAS = "studyTypeName";
	static final String LOCKED_ALIAS = "locked";
	static final String STUDY_OWNER_NAME_ALIAS = "ownerName";
	static final String STUDY_OWNER_ID_ALIAS = "ownerId";
	static final String START_DATE_ALIAS = "startDate";
	static final String END_DATE_ALIAS = "endDate";
	static final String UPDATE_DATE_ALIAS = "updateDate";
	static final String PARENT_FOLDER_NAME_ALIAS = "parentFolderName";
	static final String OBJECTIVE_ALIAS = "objective";

	private static final String BASE_QUERY = "SELECT %s " // usage of SELECT_EXPRESION / COUNT_EXPRESSION
		+ " FROM project study "
		+ " %s " // usage of SELECT_JOINS
		+ " WHERE study.study_type_id IS NOT NULL AND study.deleted = 0 AND study.program_uuid = :programUUID";

	private static final String COUNT_EXPRESSION = " COUNT(DISTINCT study.project_id) ";

	private static final String SELECT_EXPRESSION = " DISTINCT(study.project_id) AS " + STUDY_ID_ALIAS + ", "
		+ " study.name AS " + STUDY_NAME_ALIAS + ", "
		+ " study.description AS " + STUDY_DESCRIPTION_ALIAS + ", "
		+ " studyType.label AS " + STUDY_TYPE_NAME_ALIAS + ", "
		+ " study.locked AS " + LOCKED_ALIAS + ", "
		+ " user.uname AS " + STUDY_OWNER_NAME_ALIAS + ", "
		+ " user.userid AS " + STUDY_OWNER_ID_ALIAS + ", "
		+ " STR_TO_DATE (convert(study.start_date,char), '%Y%m%d') AS " + START_DATE_ALIAS + ", "
		+ " CASE WHEN study.end_date = '' THEN null ELSE ((STR_TO_DATE(convert(study.end_date, char), '%Y%m%d'))) END as " + END_DATE_ALIAS
		+ ", "
		+ " STR_TO_DATE (convert(study.study_update,char), '%Y%m%d') AS " + UPDATE_DATE_ALIAS + ", "
		+ " inn.name AS " + PARENT_FOLDER_NAME_ALIAS + ", "
		+ " study.objective AS " + OBJECTIVE_ALIAS;

	// JOINS
	private static final String SELF_JOIN_QUERY = "LEFT JOIN project inn ON study.parent_project_id = inn.project_id";
	private static final String STUDY_TYPE_JOIN_QUERY =
		"INNER JOIN study_type studyType ON study.study_type_id = studyType.study_type_id";
	private static final String WORKBENCH_USER_JOIN_QUERY = "INNER JOIN workbench.users user ON user.userid = study.created_by";
	private static final String STUDY_SETTINGS_JOIN_QUERY =
		"INNER JOIN projectprop studySetting_%1$s ON study.project_id = studySetting_%1$s.project_id AND studySetting_%1$s.type_id = "
			+ VariableType.STUDY_DETAIL.getId();

	// ENVIRONMENT VARIABLES
	private static final String ENVIRONMENT_BASE_SUBQUERY = "SELECT env.parent_project_id FROM project env "
		+ " %s " // usage of SELECT_JOINS
		+ " WHERE env.dataset_type_id = " + DatasetTypeEnum.SUMMARY_DATA.getId()
		+ " AND %s"; // usage of conditions;

	private static final String EXPERIMENT_JOIN_QUERY = "INNER JOIN nd_experiment nde ON nde.project_id = env.project_id";
	private static final String GEOLOCATION_JOIN_QUERY = "INNER JOIN nd_geolocation gl ON nde.nd_geolocation_id = gl.nd_geolocation_id";
	private static final String ENVIRONMENT_PROPS_JOIN_QUERY =
		"INNER JOIN projectprop envDetails on env.project_id = envDetails.project_id AND envDetails.type_id = "
			+ VariableType.ENVIRONMENT_DETAIL.getId() + " AND envDetails.variable_id = %s";
	private static final String GEOLOCATION_PROP_JOIN_QUERY =
		"INNER JOIN nd_geolocationprop glp ON gl.nd_geolocation_id = glp.nd_geolocation_id";
	private static final String PHENOTYPE_JOIN_QUERY = "INNER JOIN phenotype ph ON nde.nd_experiment_id = ph.nd_experiment_id";

	static SQLQueryBuilder getSelectQuery(final StudySearchRequest request,
		final Map<Integer, List<Integer>> categoricalValueReferenceIdsByVariablesIds, final List<Integer> locationIds,
		final List<Integer> userIds, final Pageable pageable) {
		final String joins = getSelectQueryJoins(request);
		final String baseQuery =
			String.format(BASE_QUERY, SELECT_EXPRESSION, joins);
		final SQLQueryBuilder sqlQueryBuilder = new SQLQueryBuilder(baseQuery);
		addFilters(sqlQueryBuilder, request, categoricalValueReferenceIdsByVariablesIds, locationIds, userIds);
		sqlQueryBuilder.append(
			DAOQueryUtils.getOrderClause(input -> SortColumn.getByValue(input).value, pageable));
		return sqlQueryBuilder;
	}

	static SQLQueryBuilder getCountQuery(final StudySearchRequest request,
		final Map<Integer, List<Integer>> categoricalValueReferenceIdsByVariablesIds, final List<Integer> locationIds,
		final List<Integer> userIds) {
		final String countQueryJoins = getCountQueryJoins(request);
		final String baseQuery = String.format(BASE_QUERY, COUNT_EXPRESSION, countQueryJoins);
		final SQLQueryBuilder sqlQueryBuilder = new SQLQueryBuilder(baseQuery);
		addFilters(sqlQueryBuilder, request, categoricalValueReferenceIdsByVariablesIds, locationIds, userIds);
		return sqlQueryBuilder;
	}

	private static String getSelectQueryJoins(final StudySearchRequest request) {
		final Set<String> joins = new LinkedHashSet<>();
		joins.add(SELF_JOIN_QUERY);
		joins.add(STUDY_TYPE_JOIN_QUERY);
		joins.add(WORKBENCH_USER_JOIN_QUERY);
		if (!CollectionUtils.isEmpty(request.getStudySettings())) {
			request.getStudySettings().keySet().forEach(key -> joins.add(String.format(STUDY_SETTINGS_JOIN_QUERY, key)));
		}
		return String.join(" ", joins);
	}

	private static String getCountQueryJoins(final StudySearchRequest request) {
		final Set<String> joins = new LinkedHashSet<>();
		if (!StringUtils.isEmpty(request.getParentFolderName())) {
			joins.add(SELF_JOIN_QUERY);
		}
		if (!StringUtil.isEmpty(request.getOwnerName())) {
			joins.add(WORKBENCH_USER_JOIN_QUERY);
		}
		if (!CollectionUtils.isEmpty(request.getStudySettings())) {
			request.getStudySettings().keySet().forEach(key -> joins.add(String.format(STUDY_SETTINGS_JOIN_QUERY, key)));
		}
		return String.join(" ", joins);
	}

	private static void addFilters(final SQLQueryBuilder sqlQueryBuilder, final StudySearchRequest request,
		final Map<Integer, List<Integer>> categoricalValueReferenceIdsByVariablesIds, final List<Integer> locationIds,
		final List<Integer> userIds) {
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
			sqlQueryBuilder.setParameter("studyStartDateFrom", Constants.DATE_FORMAT.format(request.getStudyStartDateFrom()));
		}

		if (request.getStudyStartDateTo() != null) {
			sqlQueryBuilder.append(" AND study.start_date <= :studyStartDateTo ");
			sqlQueryBuilder.setParameter("studyStartDateTo", Constants.DATE_FORMAT.format(request.getStudyStartDateTo()));
		}

		if (!StringUtils.isEmpty(request.getParentFolderName())) {
			sqlQueryBuilder.setParameter("parentFolderName", "%" + request.getParentFolderName() + "%");
			sqlQueryBuilder.append(" AND inn.name LIKE :parentFolderName ");
		}

		if (!StringUtils.isEmpty(request.getObjective())) {
			sqlQueryBuilder.setParameter("objective", "%" + request.getObjective() + "%");
			sqlQueryBuilder.append(" AND study.objective LIKE :objective ");
		}

		addStudySettingsFilters(sqlQueryBuilder, request, categoricalValueReferenceIdsByVariablesIds);
		addEnvironmentDetailsFilters(sqlQueryBuilder, request, categoricalValueReferenceIdsByVariablesIds, locationIds, userIds);
		addEnvironmentConditionsFilters(sqlQueryBuilder, request, categoricalValueReferenceIdsByVariablesIds);
	}

	private static void addStudySettingsFilters(final SQLQueryBuilder sqlQueryBuilder, final StudySearchRequest request,
		final Map<Integer, List<Integer>> categoricalValueReferenceIdsByVariablesIds) {
		if (!CollectionUtils.isEmpty(request.getStudySettings())) {
			request.getStudySettings().forEach((key, value) -> {
				final String studySettingIdParameter = "studySetting_" + key;
				final String tableAlias = "studySetting_" + key;
				sqlQueryBuilder.setParameter(studySettingIdParameter, key);
				sqlQueryBuilder
					.append(" AND " + tableAlias + ".variable_id = :" + studySettingIdParameter + " AND " + tableAlias + ".value ");

				if (categoricalValueReferenceIdsByVariablesIds.containsKey(key)) {
					final String studySettingCatIdsParameter = "studySettingsCatIds_" + key;
					sqlQueryBuilder.setParameter(studySettingCatIdsParameter, categoricalValueReferenceIdsByVariablesIds.get(key));
					sqlQueryBuilder.append(" IN (:" + studySettingCatIdsParameter + ")");
				} else {
					final String studySettingValueParameter = "studySettingValue_" + key;
					sqlQueryBuilder.setParameter(studySettingValueParameter, "%" + value + "%");
					sqlQueryBuilder.append(" LIKE :" + studySettingValueParameter);
				}
			});
		}
	}

	private static void addEnvironmentDetailsFilters(final SQLQueryBuilder sqlQueryBuilder, final StudySearchRequest request,
		final Map<Integer, List<Integer>> categoricalValueReferenceIdsByVariablesIds, final List<Integer> locationIds,
		final List<Integer> userIds) {
		if (!CollectionUtils.isEmpty(request.getEnvironmentDetails())) {
			request.getEnvironmentDetails().forEach((key, value) -> {

				final boolean isGeoCoordinateVariable = TermId.LATITUDE.getId() == key ||
					TermId.LONGITUDE.getId() == key ||
					TermId.ALTITUDE.getId() == key;

				final Set<String> envDetailsJoins = new LinkedHashSet<>();
				envDetailsJoins.add(EXPERIMENT_JOIN_QUERY);
				envDetailsJoins.add(GEOLOCATION_JOIN_QUERY);

				final String envPropJoin = String.format(ENVIRONMENT_PROPS_JOIN_QUERY, key);
				envDetailsJoins.add(envPropJoin);

				final StringBuilder envDetailCondition = new StringBuilder();
				if (!isGeoCoordinateVariable) {
					envDetailsJoins.add(GEOLOCATION_PROP_JOIN_QUERY);

					final String environmentDetailIdParameter = "environmentDetail_" + key;
					sqlQueryBuilder.setParameter(environmentDetailIdParameter, key);
					envDetailCondition.append("glp.type_id = :").append(environmentDetailIdParameter).append(" AND glp.value");
				}

				if (isGeoCoordinateVariable) {
					final String geoCoordinateValueParameter = "geoCoordinateValue_" + key;
					sqlQueryBuilder.setParameter(geoCoordinateValueParameter, value);
					envDetailCondition.append("gl.").append(geoCoordinatesColumnsByTermId.get(key)).append(" = :")
						.append(geoCoordinateValueParameter);

				} else if (TermId.LOCATION_ID.getId() == key) {
					final String locationIdsParameter = "locationIds_" + key;
					sqlQueryBuilder.setParameter(locationIdsParameter, locationIds);
					envDetailCondition.append(" IN (:").append(locationIdsParameter).append(")");

				} else if (TermId.COOPERATOOR_ID.getId() == key) {
					final String cooperatorIdsParameter = "cooperatorIds_" + key;
					sqlQueryBuilder.setParameter(cooperatorIdsParameter, userIds);
					envDetailCondition.append(" IN (:").append(cooperatorIdsParameter).append(")");

				} else if (categoricalValueReferenceIdsByVariablesIds.containsKey(key)) {
					final String environmentDetailCatIdsParameter = "environmentDetailCatIds_" + key;
					sqlQueryBuilder.setParameter(environmentDetailCatIdsParameter, categoricalValueReferenceIdsByVariablesIds.get(key));
					envDetailCondition.append(" IN (:").append(environmentDetailCatIdsParameter).append(")");

				} else {
					final String environmentDetailValueParameter = "environmentDetailValue_" + key;
					sqlQueryBuilder.setParameter(environmentDetailValueParameter, "%" + value + "%");
					envDetailCondition.append(" LIKE :").append(environmentDetailValueParameter);

				}

				final String envDetailsSubQuery = String.format(ENVIRONMENT_BASE_SUBQUERY,
					String.join(" ", envDetailsJoins),
					String.join(" AND ", envDetailCondition.toString()));
				sqlQueryBuilder.append(" AND study.project_id IN (").append(envDetailsSubQuery).append(") ");
			});
		}
	}

	private static void addEnvironmentConditionsFilters(final SQLQueryBuilder sqlQueryBuilder, final StudySearchRequest request,
		final Map<Integer, List<Integer>> categoricalValueReferenceIdsByVariablesIds) {
		if (!CollectionUtils.isEmpty(request.getEnvironmentConditions())) {
			request.getEnvironmentConditions().forEach((key, value) -> {

				final Set<String> envDetailsJoins = new LinkedHashSet<>();
				envDetailsJoins.add(EXPERIMENT_JOIN_QUERY);
				envDetailsJoins.add(PHENOTYPE_JOIN_QUERY);

				final StringBuilder envConditionCondition = new StringBuilder();
				final String environmentConditionIdParameter = "environmentCondition_" + key;
				sqlQueryBuilder.setParameter(environmentConditionIdParameter, key);
				envConditionCondition.append("ph.observable_id = :").append(environmentConditionIdParameter).append(" AND ph.value");

				if (categoricalValueReferenceIdsByVariablesIds.containsKey(key)) {
					final String environmentConditionCatIdsParameter = "environmentConditionCatIds_" + key;
					sqlQueryBuilder.setParameter(environmentConditionCatIdsParameter, categoricalValueReferenceIdsByVariablesIds.get(key));
					envConditionCondition.append(" IN (:").append(environmentConditionCatIdsParameter).append(")");

				} else {
					final String environmentConditionValueParameter = "environmentConditionValue_" + key;
					sqlQueryBuilder.setParameter(environmentConditionValueParameter, "%" + value + "%");
					envConditionCondition.append(" LIKE :").append(environmentConditionValueParameter);

				}

				final String envDetailsSubQuery = String.format(ENVIRONMENT_BASE_SUBQUERY,
					String.join(" ", envDetailsJoins),
					String.join(" AND ", envConditionCondition.toString()));
				sqlQueryBuilder.append(" AND study.project_id IN (").append(envDetailsSubQuery).append(") ");
			});
		}
	}

}
