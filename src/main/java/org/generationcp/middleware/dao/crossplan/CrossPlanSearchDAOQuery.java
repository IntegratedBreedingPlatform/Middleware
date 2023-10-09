package org.generationcp.middleware.dao.crossplan;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.api.crossplan.CrossPlanSearchRequest;
import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.dao.util.DAOQueryUtils;
import org.generationcp.middleware.domain.sqlfilter.SqlTextFilter;
import org.generationcp.middleware.util.SQLQueryBuilder;
import org.generationcp.middleware.util.StringUtil;
import org.generationcp.middleware.util.Util;
import org.springframework.data.domain.Pageable;

import java.text.SimpleDateFormat;
import java.util.Arrays;

public class CrossPlanSearchDAOQuery {

    private static final String PROGRAM_CROSS_PLANS = "Program Cross Plan";
    private static final String CROP_CROSS_PLANS = "Crop Cross Plan";
    enum SortColumn {

        LIST_NAME(CROSS_PLAN_NAME_ALIAS),
        PARENT_FOLDER_NAME(PARENT_FOLDER_NAME_ALIAS),
        DESCRIPTION(DESCRIPTION_ALIAS),
        LIST_OWNER(CROSS_PLAN_OWNER_ALIAS),
        LIST_TYPE(TYPE_ALIAS),
        NOTES(NOTES_ALIAS),
        CREATION_DATE(CREATION_DATE_ALIAS);

        private String value;

        SortColumn(final String value) {
            this.value = value;
        }

        static CrossPlanSearchDAOQuery.SortColumn getByValue(final String value) {
            return Arrays.stream(CrossPlanSearchDAOQuery.SortColumn.values())
                    .filter(e -> e.name().equals(value))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException(String.format("Unsupported sort value %s.", value)));
        }
    }

    //TODO: move to utils
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(Util.DATE_AS_NUMBER_FORMAT);

    static final String CROSS_PLAN_ID_ALIAS = "id";
    static final String CROSS_PLAN_NAME_ALIAS = "name";
    static final String PARENT_FOLDER_NAME_ALIAS = "parentFolderName";
    static final String DESCRIPTION_ALIAS = "description";
    static final String CROSS_PLAN_OWNER_ALIAS = "createdBy";
    static final String TYPE_ALIAS = "type";
    static final String NOTES_ALIAS = "notes";
    static final String CREATION_DATE_ALIAS = "createdDate";
    static final String PROGRAM_UUID_ALIAS = "programUUID";

    private final static String BASE_QUERY = "SELECT %s "
            + " FROM cross_plan cp "
            + " %s "
            + " WHERE "
            + " (cp.program_uuid = :programUUID OR cp.program_uuid IS NULL) and cp.type = 'CROSS_PLAN' ";

    private final static String SELECT_EXPRESSION = "cp.id AS " + CROSS_PLAN_ID_ALIAS + ", "
            + " cp.name AS " + CROSS_PLAN_NAME_ALIAS + ", "
            + " IF (cp.cross_plan_parent_id IS NULL, "
            + "			IF(cp.program_uuid IS NULL, '" + CROP_CROSS_PLANS + "', '" + PROGRAM_CROSS_PLANS + "'), "
            + "			inn.name) AS " + PARENT_FOLDER_NAME_ALIAS + ", "
            + " cp.description AS " + DESCRIPTION_ALIAS + ", "
            + " user.uname AS " + CROSS_PLAN_OWNER_ALIAS + ", "
            + " cp.type AS " + TYPE_ALIAS + ", "
            + " cp.notes AS " + NOTES_ALIAS + ", "
            + " STR_TO_DATE (convert(cp.created_date,char), '%Y%m%d') AS " + CREATION_DATE_ALIAS + ", "
            + " cp.program_uuid AS " + PROGRAM_UUID_ALIAS;

    private final static String SELF_JOIN_QUERY = " LEFT JOIN cross_plan inn ON cp.cross_plan_parent_id = inn.id ";
    private final static String WORKBENCH_USER_JOIN_QUERY = " INNER JOIN workbench.users user ON user.userid = cp.created_by ";

    private static final String COUNT_EXPRESSION = " COUNT(1) ";

    private static final String CROSS_PLAN_TYPE_JOIN_QUERY = "";
    static SQLQueryBuilder getSelectQuery(final CrossPlanSearchRequest request, final Pageable pageable) {
        final String baseQuery = String.format(BASE_QUERY, SELECT_EXPRESSION, SELF_JOIN_QUERY + WORKBENCH_USER_JOIN_QUERY);
        final SQLQueryBuilder sqlQueryBuilder = new SQLQueryBuilder(baseQuery);
        addFilters(sqlQueryBuilder, request);
        sqlQueryBuilder.append(DAOQueryUtils.getOrderClause(input -> CrossPlanSearchDAOQuery.SortColumn.getByValue(input).value, pageable));
        return sqlQueryBuilder;
    }

    static SQLQueryBuilder getCountQuery(final CrossPlanSearchRequest request) {
        final String countQueryJoins = getCountQueryJoins(request);
        final String baseQuery = String.format(BASE_QUERY, COUNT_EXPRESSION, countQueryJoins);
        final SQLQueryBuilder sqlQueryBuilder = new SQLQueryBuilder(baseQuery);
        addFilters(sqlQueryBuilder, request);
        return sqlQueryBuilder;
    }

    private static String getCountQueryJoins(final CrossPlanSearchRequest request) {
        final StringBuilder joinBuilder = new StringBuilder();
        if (!StringUtils.isEmpty(request.getParentFolderName())) {
            joinBuilder.append(SELF_JOIN_QUERY);
        }
        if (!StringUtil.isEmpty(request.getOwnerName())) {
            joinBuilder.append(WORKBENCH_USER_JOIN_QUERY);
        }
        return joinBuilder.toString();
    }

    private static void addFilters(final SQLQueryBuilder sqlQueryBuilder, final CrossPlanSearchRequest request) {
        final SqlTextFilter crossPlanNameFilter = request.getCrossPlanNameFilter();
        if (crossPlanNameFilter != null && !crossPlanNameFilter.isEmpty()) {
            final String value = crossPlanNameFilter.getValue();
            final SqlTextFilter.Type type = crossPlanNameFilter.getType();
            final String operator = GenericDAO.getOperator(type);
            sqlQueryBuilder.setParameter("cross_plan_name", GenericDAO.getParameter(type, value));
            sqlQueryBuilder.append(" AND cp.name ").append(operator).append(":cross_plan_name");
        }

        if (!StringUtils.isEmpty(request.getParentFolderName())) {
            sqlQueryBuilder.setParameter("parentFolderName", "%" + request.getParentFolderName() + "%");
            sqlQueryBuilder.append(" AND (inn.name LIKE :parentFolderName "
                    + " 	OR (cp.cross_plan_parent_id IS NULL AND cp.program_uuid IS NOT NULL AND '" + PROGRAM_CROSS_PLANS + "' LIKE :parentFolderName)"
                    + " 	OR (cp.cross_plan_parent_id IS NULL AND cp.program_uuid IS NULL AND '" + CROP_CROSS_PLANS + "' LIKE :parentFolderName)"
                    + " )");
        }

        if (!StringUtils.isEmpty(request.getDescription())) {
            sqlQueryBuilder.setParameter("description", "%" + request.getDescription() + "%");
            sqlQueryBuilder.append(" AND cp.description LIKE :description ");
        }

        if (!StringUtils.isEmpty(request.getOwnerName())) {
            sqlQueryBuilder.setParameter("ownerName", "%" + request.getOwnerName() + "%");
            sqlQueryBuilder.append(" AND user.uname LIKE :ownerName ");
        }


        if (!StringUtils.isEmpty(request.getNotes())) {
            sqlQueryBuilder.setParameter("notes", "%" + request.getNotes() + "%");
            sqlQueryBuilder.append(" AND cp.notes LIKE :notes ");
        }

        if (request.getCrossPlanStartDateFrom() != null) {
            sqlQueryBuilder.append(" AND cp.created_date >= :listDateFrom ");
            sqlQueryBuilder.setParameter("listDateFrom", DATE_FORMAT.format(request.getCrossPlanStartDateFrom()));
        }

        if (request.getCrossPlanStartDateTo() != null) {
            sqlQueryBuilder.append(" AND cp.created_date <= :listDateTo ");
            sqlQueryBuilder.setParameter("listDateTo", DATE_FORMAT.format(request.getCrossPlanStartDateTo()));
        }

    }

}
