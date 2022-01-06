package org.generationcp.middleware.dao.breedingmethod;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.api.breedingmethod.BreedingMethodSearchRequest;
import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.dao.util.DAOQueryUtils;
import org.generationcp.middleware.domain.sqlfilter.SqlTextFilter;
import org.generationcp.middleware.pojos.dms.ProgramFavorite;
import org.generationcp.middleware.util.SQLQueryBuilder;
import org.generationcp.middleware.util.Util;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.Arrays;

public class BreedingMethodSearchDAOQuery {

  //TODO: move to utils
  public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(Util.DATE_AS_NUMBER_FORMAT);

  enum SortColumn {

    NAME(NAME_ALIAS),
    DESCRIPTION(DESCRIPTION_ALIAS),
    GROUP(GROUP_ALIAS),
    CODE(ABBREVIATION_ALIAS),
    TYPE(TYPE_ALIAS),
    DATE(DATE_ALIAS),
    CLASS_NAME(CLASS_NAME_ALIAS),
    FAVORITE_PROGRAM_UUID(FAVORITE_PROGRAM_UUID_ALIAS);

    private String value;

    SortColumn(final String value) {
      this.value = value;
    }

    static SortColumn getByValue(final String value) {
      return Arrays.stream(SortColumn.values()).filter(e -> e.name().equals(value)).findFirst()
          .orElseThrow(() -> new IllegalStateException(String.format("Unsupported sort value %s.", value)));
    }
  }

  public static final String ID_ALIAS = "id";
  public static final String NAME_ALIAS = "name";
  public static final String TYPE_ALIAS = "type";
  public static final String DESCRIPTION_ALIAS = "description";
  public static final String GROUP_ALIAS = "methodGgroup";
  public static final String ABBREVIATION_ALIAS = "abbreviation";
  public static final String DATE_ALIAS = "date";
  public static final String CLASS_ID_ALIAS = "methodClassId";
  public static final String CLASS_NAME_ALIAS = "methodClassName";
  public static final String NUMBER_OF_PROGENITORS_ALIAS = "numberOfProgenitors";
  public static final String SEPARATOR_ALIAS = "methodSeparator";
  public static final String PREFIX_ALIAS = "prefix";
  public static final String COUNT_ALIAS = "count";
  public static final String SUFFIX_ALIAS = "suffix";
  public static final String FAVORITE_PROGRAM_UUID_ALIAS = "favoriteProgramUUID";
  public static final String FAVORITE_PROGRAM_ID_ALIAS = "favoriteProgramId";

  private final static String BASE_QUERY = "SELECT %s " // usage of SELECT_EXPRESION / COUNT_EXPRESSION
      + " FROM methods m " + " %s " // usage of SELECT_JOINS
      + " WHERE 1 = 1 ";

  private final static String SELECT_EXPRESSION =" m.mid AS " + ID_ALIAS + ", "
      + " m.mname AS " + NAME_ALIAS + ", "
      + " m.mtype AS " + TYPE_ALIAS + ", "
      + " m.mdesc AS " + DESCRIPTION_ALIAS + ", "
      + " m.mgrp AS " + GROUP_ALIAS + ", "
      + " m.mcode AS " + ABBREVIATION_ALIAS + ", "
      + " STR_TO_DATE (convert(m.mdate, char), '%Y%m%d') AS " + DATE_ALIAS + ", "
      + " m.geneq AS " + CLASS_ID_ALIAS + ", "
      + " term.name AS " + CLASS_NAME_ALIAS + ", "
      + " m.mprgn AS " + NUMBER_OF_PROGENITORS_ALIAS + ", "
      + " m.separator AS " + SEPARATOR_ALIAS + ", "
      + " m.prefix AS " + PREFIX_ALIAS + ", "
      + " m.count AS " + COUNT_ALIAS + ", "
      + " m.suffix AS " + SUFFIX_ALIAS;

  private final static String METHOD_CLASS_JOIN_QUERY = " LEFT JOIN cvterm term on term.cvterm_id = m.geneq ";
  private final static String PROGRAM_FAVORITE_JOIN_QUERY =
      " LEFT JOIN program_favorites pf on pf.entity_id = m.mid AND entity_type = '"
          + ProgramFavorite.FavoriteType.METHODS.name() + "' AND program_uuid = '%s' ";

  private static final String COUNT_EXPRESSION = " COUNT(m.mid) ";

  public static SQLQueryBuilder getSelectQuery(final BreedingMethodSearchRequest request, final Pageable pageable,
      final String programUUID) {
    final String selectExpression = getSelectExpression(programUUID);
    final String joins = getSelectQueryJoins(programUUID);
    final String baseQuery = String.format(BASE_QUERY, selectExpression, joins);
    final SQLQueryBuilder sqlQueryBuilder = new SQLQueryBuilder(baseQuery);
    addCommonScalars(sqlQueryBuilder, programUUID);
    addFilters(sqlQueryBuilder, request);

    if (pageable == null || pageable.getSort() == null) {
      // Add default order
      sqlQueryBuilder.append(" ORDER BY ").append(NAME_ALIAS);
    } else {
      sqlQueryBuilder.append(DAOQueryUtils.getOrderClause(input -> SortColumn.getByValue(input).value, pageable));
    }
    return sqlQueryBuilder;
  }

  public static SQLQueryBuilder getCountQuery(final BreedingMethodSearchRequest request, final String programUUID) {
    final String countQueryJoins = getCountQueryJoins(request, programUUID);
    final String baseQuery = String.format(BASE_QUERY, COUNT_EXPRESSION, countQueryJoins);
    final SQLQueryBuilder sqlQueryBuilder = new SQLQueryBuilder(baseQuery);
    addFilters(sqlQueryBuilder, request);
    return sqlQueryBuilder;
  }

  private static void addCommonScalars(final SQLQueryBuilder sqlQueryBuilder, final String programUUID) {
    sqlQueryBuilder.addScalar(new SQLQueryBuilder.Scalar(ID_ALIAS));
    sqlQueryBuilder.addScalar(new SQLQueryBuilder.Scalar(NAME_ALIAS));
    sqlQueryBuilder.addScalar(new SQLQueryBuilder.Scalar(TYPE_ALIAS));
    sqlQueryBuilder.addScalar(new SQLQueryBuilder.Scalar(DESCRIPTION_ALIAS));
    sqlQueryBuilder.addScalar(new SQLQueryBuilder.Scalar(GROUP_ALIAS));
    sqlQueryBuilder.addScalar(new SQLQueryBuilder.Scalar(ABBREVIATION_ALIAS));
    sqlQueryBuilder.addScalar(new SQLQueryBuilder.Scalar(DATE_ALIAS));
    sqlQueryBuilder.addScalar(new SQLQueryBuilder.Scalar(CLASS_ID_ALIAS));
    sqlQueryBuilder.addScalar(new SQLQueryBuilder.Scalar(CLASS_NAME_ALIAS));
    sqlQueryBuilder.addScalar(new SQLQueryBuilder.Scalar(NUMBER_OF_PROGENITORS_ALIAS));
    sqlQueryBuilder.addScalar(new SQLQueryBuilder.Scalar(SEPARATOR_ALIAS));
    sqlQueryBuilder.addScalar(new SQLQueryBuilder.Scalar(PREFIX_ALIAS));
    sqlQueryBuilder.addScalar(new SQLQueryBuilder.Scalar(COUNT_ALIAS));
    sqlQueryBuilder.addScalar(new SQLQueryBuilder.Scalar(SUFFIX_ALIAS));
    if (!StringUtils.isEmpty(programUUID)) {
      sqlQueryBuilder.addScalar(new SQLQueryBuilder.Scalar(FAVORITE_PROGRAM_UUID_ALIAS));
      sqlQueryBuilder.addScalar(new SQLQueryBuilder.Scalar(FAVORITE_PROGRAM_ID_ALIAS));
    }
  }

  private static String getSelectExpression(final String programUUID) {
    final StringBuilder selectExpression = new StringBuilder(SELECT_EXPRESSION);
    if (!StringUtils.isEmpty(programUUID)) {
      selectExpression.append(", pf.program_uuid AS ").append(FAVORITE_PROGRAM_UUID_ALIAS);
      selectExpression.append(", pf.id AS ").append(FAVORITE_PROGRAM_ID_ALIAS);
    }
    return selectExpression.toString();
  }

  private static String getSelectQueryJoins(final String programUUID) {
    final StringBuilder joins = new StringBuilder();
    joins.append(METHOD_CLASS_JOIN_QUERY);
    if (!StringUtils.isEmpty(programUUID)) {
      joins.append(getProgramFavoriteJoinQuery(programUUID));
    }
    return joins.toString();
  }

  private static String getCountQueryJoins(final BreedingMethodSearchRequest request, final String programUUID) {
    final StringBuilder joinBuilder = new StringBuilder();
    if (request.getFilterFavoriteProgramUUID() != null) {
      joinBuilder.append(getProgramFavoriteJoinQuery(programUUID));
    }
    return joinBuilder.toString();
  }

  private static String getProgramFavoriteJoinQuery(String programUUID) {
    return String.format(PROGRAM_FAVORITE_JOIN_QUERY, programUUID);
  }

  private static void addFilters(final SQLQueryBuilder sqlQueryBuilder, final BreedingMethodSearchRequest request) {
    final SqlTextFilter nameFilter = request.getNameFilter();
    if (nameFilter != null && !nameFilter.isEmpty()) {
      final String value = nameFilter.getValue();
      final SqlTextFilter.Type type = nameFilter.getType();
      final String operator = GenericDAO.getOperator(type);

      sqlQueryBuilder.append(" AND m.mname ").append(operator).append(" :name");
      sqlQueryBuilder.setParameter("name", GenericDAO.getParameter(type, value));
    }

    if (!CollectionUtils.isEmpty(request.getMethodIds())) {
      sqlQueryBuilder.append(" AND m.mid IN (:methodIds) ");
      sqlQueryBuilder.setParameter("methodIds", request.getMethodIds());
    }

    if (!CollectionUtils.isEmpty(request.getMethodAbbreviations())) {
      sqlQueryBuilder.append(" AND m.mcode IN (:methodAbbreviations) ");
      sqlQueryBuilder.setParameter("methodAbbreviations", request.getMethodAbbreviations());
    }

    if (!CollectionUtils.isEmpty(request.getMethodTypes())) {
      sqlQueryBuilder.append(" AND m.mtype IN (:methodTypes) ");
      sqlQueryBuilder.setParameter("methodTypes", request.getMethodTypes());
    }

    if (!StringUtils.isEmpty(request.getDescription())) {
      sqlQueryBuilder.append(" AND m.mdesc LIKE :description ");
      sqlQueryBuilder.setParameter("description", "%" + request.getDescription() + "%");
    }

    if (!CollectionUtils.isEmpty(request.getGroups())) {
      sqlQueryBuilder.append(" AND m.mgrp IN (:groups) ");
      sqlQueryBuilder.setParameter("groups", request.getGroups());
    }

    if (request.getMethodDateFrom() != null) {
      sqlQueryBuilder.append(" AND m.mdate >= :methodDateFrom ");
      sqlQueryBuilder.setParameter("methodDateFrom", DATE_FORMAT.format(request.getMethodDateFrom()));
    }

    if (request.getMethodDateTo() != null) {
      sqlQueryBuilder.append(" AND m.mdate <= :methodDateTo ");
      sqlQueryBuilder.setParameter("methodDateTo", DATE_FORMAT.format(request.getMethodDateTo()));
    }

    if (!CollectionUtils.isEmpty(request.getMethodClassIds())) {
      sqlQueryBuilder.append(" AND m.geneq IN (:methodClassIds) ");
      sqlQueryBuilder.setParameter("methodClassIds", request.getMethodClassIds());
    }

    if (request.getFilterFavoriteProgramUUID() != null) {
      if (request.getFilterFavoriteProgramUUID() && !StringUtils.isEmpty(request.getFavoriteProgramUUID())) {
        sqlQueryBuilder.append(" AND pf.program_uuid = :favoriteProgramUUID ");
        sqlQueryBuilder.setParameter("favoriteProgramUUID", request.getFavoriteProgramUUID());
      } else {
				sqlQueryBuilder.append(" AND pf.program_uuid is null ");
			}
    }
  }

}
