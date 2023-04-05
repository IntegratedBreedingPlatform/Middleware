package org.generationcp.middleware.dao.location;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.api.location.Coordinate;
import org.generationcp.middleware.api.location.search.LocationSearchRequest;
import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.dao.util.DAOQueryUtils;
import org.generationcp.middleware.domain.sqlfilter.SqlTextFilter;
import org.generationcp.middleware.pojos.dms.ProgramFavorite;
import org.generationcp.middleware.util.SQLQueryBuilder;
import org.generationcp.middleware.util.Scalar;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;

public class LocationSearchDAOQuery {

  enum SortColumn {

    LOCATION_NAME(LOCATION_NAME_ALIAS),
    ABBREVIATION(ABBREVIATION_ALIAS),
    LOCATION_ID(LOCATION_ID_ALIAS),
    COUNTRY(COUNTRY_NAME_ALIAS),
    PROVINCE(PROVINCE_NAME_ALIAS),
    LATITUDE(LATITUDE_ALIAS),
    LONGITUDE(LONGITUDE_ALIAS),
    ALTITUDE(ALTITUDE_ALIAS),
    TYPE(LOCATION_TYPE_NAME_ALIAS),
    FAVORITE_PROGRAM_UUID(FAVORITE_PROGRAM_UUID_ALIAS);

    private final String value;

    SortColumn(final String value) {
      this.value = value;
    }

    static SortColumn getByValue(final String value) {
      return Arrays.stream(SortColumn.values()).filter(e -> e.name().equals(value)).findFirst()
          .orElseThrow(() -> new IllegalStateException(String.format("Unsupported sort value %s.", value)));
    }
  }


  public static final String LOCATION_ID_ALIAS = "id";
  public static final String LOCATION_NAME_ALIAS = "name";
  public static final String LOCATION_TYPE_ALIAS = "type";
  public static final String LOCATION_TYPE_NAME_ALIAS = "locationTypeName";
  public static final String ABBREVIATION_ALIAS = "abbreviation";
  public static final String LATITUDE_ALIAS = "latitude";
  public static final String LONGITUDE_ALIAS = "longitude";
  public static final String ALTITUDE_ALIAS = "altitude";
  public static final String COUNTRY_ID_ALIAS = "countryId";
  public static final String PROVINCE_ID_ALIAS = "provinceId";
  public static final String COUNTRY_NAME_ALIAS = "countryName";
  public static final String COUNTRY_CODE_ALIAS = "countryCode";
  public static final String PROVINCE_NAME_ALIAS = "provinceName";
  public static final String FAVORITE_PROGRAM_UUID_ALIAS = "favoriteProgramUUID";
  public static final String FAVORITE_PROGRAM_ID_ALIAS = "favoriteProgramId";

  private final static String BASE_QUERY = "SELECT %s " // usage of SELECT_EXPRESION / COUNT_EXPRESSION
      + " FROM location l " + " %s " // usage of SELECT_JOINS
      + " WHERE 1 = 1 ";

  private final static String SELECT_EXPRESSION =" l.locid AS " + LOCATION_ID_ALIAS + ", "
      + " l.lname AS " + LOCATION_NAME_ALIAS + ", "
      + " l.ltype AS " + LOCATION_TYPE_ALIAS + ", "
      + " ud.fname AS " + LOCATION_TYPE_NAME_ALIAS + ", "
      + " l.labbr AS " + ABBREVIATION_ALIAS + ", "
      + " g.lat AS " + LATITUDE_ALIAS + ", "
      + " g.lon AS " + LONGITUDE_ALIAS + ", "
      + " g.alt AS " + ALTITUDE_ALIAS + ", "
      + " l.cntryid AS " + COUNTRY_ID_ALIAS + ", "
      + " l.snl1id AS " + PROVINCE_ID_ALIAS + ", "
      + " c.isoabbr AS " + COUNTRY_NAME_ALIAS + ", "
      + " c.isothree AS " + COUNTRY_CODE_ALIAS + ", "
      + " province.lname AS " + PROVINCE_NAME_ALIAS;

  private final static String GEOREF_JOIN_QUERY = " LEFT JOIN georef g on l.locid = g.locid ";
  private final static String COUNTRY_JOIN_QUERY = " LEFT JOIN cntry c on l.cntryid = c.cntryid ";
  private final static String LOCATION_TYPE_JOIN_QUERY = " LEFT JOIN udflds ud on ud.fldno = l.ltype ";
  private final static String PROVINCE_JOIN_QUERY = " LEFT JOIN location province ON province.locid = l.snl1id ";
  private final static String PROGRAM_FAVORITE_JOIN_QUERY =
      " LEFT JOIN program_favorites pf on pf.entity_id = l.locid" + " AND entity_type = '"
          + ProgramFavorite.FavoriteType.LOCATION.name() + "' AND program_uuid = '%s' ";

  private static final String COUNT_EXPRESSION = " COUNT(l.locid) ";

  public static SQLQueryBuilder getSelectQuery(final LocationSearchRequest request, final Pageable pageable,
      final String programUUID) {
    final String selectExpression = getSelectExpression(programUUID);
    final String joins = getSelectQueryJoins(programUUID);
    final String baseQuery = String.format(BASE_QUERY, selectExpression, joins);
    final SQLQueryBuilder sqlQueryBuilder = new SQLQueryBuilder(baseQuery);
    addCommonScalars(sqlQueryBuilder, programUUID);
    addFilters(sqlQueryBuilder, request);

    if (pageable == null || pageable.getSort() == null) {
      // Add default order
      sqlQueryBuilder.append(" ORDER BY ").append(LOCATION_NAME_ALIAS);
    } else {
      sqlQueryBuilder.append(DAOQueryUtils.getOrderClause(input -> SortColumn.getByValue(input).value, pageable));
    }
    return sqlQueryBuilder;
  }

  public static SQLQueryBuilder getCountQuery(final LocationSearchRequest request, final String programUUID) {
    final String countQueryJoins = getCountQueryJoins(request, programUUID);
    final String baseQuery = String.format(BASE_QUERY, COUNT_EXPRESSION, countQueryJoins);
    final SQLQueryBuilder sqlQueryBuilder = new SQLQueryBuilder(baseQuery);
    addFilters(sqlQueryBuilder, request);
    return sqlQueryBuilder;
  }

  private static void addCommonScalars(final SQLQueryBuilder sqlQueryBuilder, final String programUUID) {
    sqlQueryBuilder.addScalar(new Scalar(LOCATION_ID_ALIAS));
    sqlQueryBuilder.addScalar(new Scalar(LOCATION_NAME_ALIAS));
    sqlQueryBuilder.addScalar(new Scalar(LOCATION_TYPE_ALIAS));
    sqlQueryBuilder.addScalar(new Scalar(LOCATION_TYPE_NAME_ALIAS));
    sqlQueryBuilder.addScalar(new Scalar(ABBREVIATION_ALIAS));
    sqlQueryBuilder.addScalar(new Scalar(LATITUDE_ALIAS));
    sqlQueryBuilder.addScalar(new Scalar(LONGITUDE_ALIAS));
    sqlQueryBuilder.addScalar(new Scalar(ALTITUDE_ALIAS));
    sqlQueryBuilder.addScalar(new Scalar(COUNTRY_ID_ALIAS));
    sqlQueryBuilder.addScalar(new Scalar(PROVINCE_ID_ALIAS));
    sqlQueryBuilder.addScalar(new Scalar(COUNTRY_NAME_ALIAS));
    sqlQueryBuilder.addScalar(new Scalar(COUNTRY_CODE_ALIAS));
    sqlQueryBuilder.addScalar(new Scalar(PROVINCE_NAME_ALIAS));
    if (!StringUtils.isEmpty(programUUID)) {
      sqlQueryBuilder.addScalar(new Scalar(FAVORITE_PROGRAM_UUID_ALIAS));
      sqlQueryBuilder.addScalar(new Scalar(FAVORITE_PROGRAM_ID_ALIAS));
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
    final StringBuilder joins =
        new StringBuilder(GEOREF_JOIN_QUERY).append(COUNTRY_JOIN_QUERY).append(LOCATION_TYPE_JOIN_QUERY)
            .append(PROVINCE_JOIN_QUERY);
    if (!StringUtils.isEmpty(programUUID)) {
      joins.append(getProgramFavoriteJoinQuery(programUUID));
    }
    return joins.toString();
  }

  private static String getCountQueryJoins(final LocationSearchRequest request, final String programUUID) {
    final StringBuilder joinBuilder = new StringBuilder();
    if (request.getLatitudeFrom() != null || request.getLatitudeTo() != null || request.getLongitudeFrom() != null
        || request.getLongitudeTo() != null || request.getAltitudeMin() != null || request.getAltitudeMax() != null
        || request.getCoordinates() != null) {
      joinBuilder.append(GEOREF_JOIN_QUERY);
    }
    if (!CollectionUtils.isEmpty(request.getLocationTypes())) {
      joinBuilder.append(LOCATION_TYPE_JOIN_QUERY);
    }

    if (!StringUtils.isEmpty(request.getCountryName()) || !CollectionUtils.isEmpty(request.getCountryNames())
        || !CollectionUtils.isEmpty(request.getCountryCodes()) || !CollectionUtils.isEmpty(request.getCountryIds())) {
      joinBuilder.append(COUNTRY_JOIN_QUERY);
    }
    if (!StringUtils.isEmpty(request.getProvinceName())) {
      joinBuilder.append(PROVINCE_JOIN_QUERY);
    }
    if (request.getFilterFavoriteProgramUUID() != null) {
      joinBuilder.append(getProgramFavoriteJoinQuery(programUUID));
    }
    return joinBuilder.toString();
  }

  private static String getProgramFavoriteJoinQuery(final String programUUID) {
    return String.format(PROGRAM_FAVORITE_JOIN_QUERY, programUUID);
  }

  private static void addFilters(final SQLQueryBuilder sqlQueryBuilder, final LocationSearchRequest request) {

    if (!CollectionUtils.isEmpty(request.getLocationDbIds())) {
      sqlQueryBuilder.append(" AND l.locid IN (:locationId) ");
      sqlQueryBuilder.setParameter("locationId", request.getLocationDbIds());
    }

    if (!CollectionUtils.isEmpty(request.getLocationNames())) {
      sqlQueryBuilder.append(" AND l.lname IN (:locationNames) ");
      sqlQueryBuilder.setParameter("locationNames", request.getLocationNames());
    }

    if (!CollectionUtils.isEmpty(request.getLocationNames())) {
      sqlQueryBuilder.append(" AND l.lname IN (:locationNames) ");
      sqlQueryBuilder.setParameter("locationNames", request.getLocationNames());
    }

    if (!StringUtils.isEmpty(request.getCountryName())) {
      sqlQueryBuilder.append(" AND c.isoabbr LIKE :countryName ");
      sqlQueryBuilder.setParameter("countryName", "%" + request.getCountryName() + "%");
    }

    if (!CollectionUtils.isEmpty(request.getCountryNames())) {
      sqlQueryBuilder.append(" AND c.isoabbr IN (:countryNames) ");
      sqlQueryBuilder.setParameter("countryNames", request.getCountryNames());
    }

    if (!CollectionUtils.isEmpty(request.getCountryCodes())) {
      sqlQueryBuilder.append(" AND c.isothree IN (:countryCodes) ");
      sqlQueryBuilder.setParameter("countryCodes", request.getCountryCodes());
    }

    if (!StringUtils.isEmpty(request.getProvinceName())) {
      sqlQueryBuilder.append(" AND province.lname LIKE :provinceName ");
      sqlQueryBuilder.setParameter("provinceName", "%" + request.getProvinceName() + "%");
    }

    if (!CollectionUtils.isEmpty(request.getLocationTypeIds())) {
      sqlQueryBuilder.append(" AND l.ltype IN (:locationTypeIds) ");
      sqlQueryBuilder.setParameter("locationTypeIds", request.getLocationTypeIds());
    }

    if (!CollectionUtils.isEmpty(request.getLocationTypes())) {
      sqlQueryBuilder.append(" AND ud.fname IN (:locationTypes) ");
      sqlQueryBuilder.setParameter("locationTypes", request.getLocationTypes());
    }

    if (!CollectionUtils.isEmpty(request.getAbbreviations())) {
      sqlQueryBuilder.append(" AND l.labbr IN (:locationAbbrs) ");
      sqlQueryBuilder.setParameter("locationAbbrs", request.getAbbreviations());
    }

    if (!CollectionUtils.isEmpty(request.getCountryIds())) {
      sqlQueryBuilder.append("AND l.cntryid IN (:contryIds) ");
      sqlQueryBuilder.setParameter("contryIds", request.getCountryIds());
    }

    if (!CollectionUtils.isEmpty(request.getProvinceIds())) {
      sqlQueryBuilder.append("AND l.snl1id IN (:provinceId) ");
      sqlQueryBuilder.setParameter("provinceId", request.getProvinceIds());
    }


    final SqlTextFilter locationNameFilter = request.getLocationNameFilter();
    if (locationNameFilter != null && !locationNameFilter.isEmpty()) {
      final String value = locationNameFilter.getValue();
      final SqlTextFilter.Type type = locationNameFilter.getType();
      final String operator = GenericDAO.getOperator(type);

      sqlQueryBuilder.append(" AND l.lname ").append(operator).append(" :name");
      sqlQueryBuilder.setParameter("name", GenericDAO.getParameter(type, value));
    }

    if (request.getLatitudeFrom() != null) {
      sqlQueryBuilder.append(" AND g.lat >= :latitudeFrom ");
      sqlQueryBuilder.setParameter("latitudeFrom", request.getLatitudeFrom());
    }

    if (request.getLatitudeTo() != null) {
      sqlQueryBuilder.append(" AND g.lat <= :latitudeTo ");
      sqlQueryBuilder.setParameter("latitudeTo", request.getLatitudeTo());
    }

    if (request.getLongitudeFrom() != null) {
      sqlQueryBuilder.append(" AND g.lon >= :longitudeFrom ");
      sqlQueryBuilder.setParameter("longitudeFrom", request.getLongitudeFrom());
    }

    if (request.getLongitudeTo() != null) {
      sqlQueryBuilder.append(" AND g.lon <= :longitudeTo ");
      sqlQueryBuilder.setParameter("longitudeTo", request.getLongitudeTo());
    }

    if (request.getAltitudeMin() != null) {
      sqlQueryBuilder.append(" AND g.alt >= :altitudeFrom ");
      sqlQueryBuilder.setParameter("altitudeFrom", request.getAltitudeMin());
    }

    if (request.getAltitudeMax() != null) {
      sqlQueryBuilder.append(" AND g.alt <= :altitudeTo ");
      sqlQueryBuilder.setParameter("altitudeTo", request.getAltitudeMax());
    }

    final Coordinate coordinateFilter = request.getCoordinates();
    if (coordinateFilter != null) {
      final List<Double> coordinatesList = coordinateFilter.getGeometry().getCoordinates();
      if (!CollectionUtils.isEmpty(coordinatesList) && coordinatesList.size() >= 2) {
        sqlQueryBuilder.append(" AND g.lon = :longitude ");
        sqlQueryBuilder.setParameter("longitude", coordinatesList.get(0));
        sqlQueryBuilder.append(" AND g.lat = :latitude ");
        sqlQueryBuilder.setParameter("latitude", coordinatesList.get(1));

        if (coordinatesList.size() == 3) {
          sqlQueryBuilder.append(" AND g.alt = :altitude ");
          sqlQueryBuilder.setParameter("altitude", coordinatesList.get(2));
        }
      }
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
