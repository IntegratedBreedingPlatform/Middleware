/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 * <p/>
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.middleware.dao;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.api.location.Coordinate;
import org.generationcp.middleware.api.location.Geometry;
import org.generationcp.middleware.api.location.LocationDTO;
import org.generationcp.middleware.api.location.search.LocationSearchRequest;
import org.generationcp.middleware.api.program.ProgramFavoriteDTO;
import org.generationcp.middleware.dao.location.LocationSearchDAOQuery;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.LocationDetails;
import org.generationcp.middleware.pojos.Locdes;
import org.generationcp.middleware.pojos.dms.ProgramFavorite;
import org.generationcp.middleware.util.SQLQueryBuilder;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * DAO class for {@link Location}.
 */
@SuppressWarnings("unchecked")
public class LocationDAO extends GenericDAO<Location, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(LocationDAO.class);

	private static final String CLASS_NAME_LOCATION = "Location";
	private static final String COUNTRY = "country";
	private static final String LNAME = "lname";
	private static final String LOCID = "locid";
	private static final String LABBREVIATION = "labbr";
	private static final String NAME_OR_OPERATION = "name|operation";

	public LocationDAO(final Session session) {
		super(session);
	}

	public List<Location> getByName(final String name, final Operation operation) {
		try {
			final Criteria criteria = this.getSession().createCriteria(Location.class);
			this.addNameSearchCriteria(name, operation, criteria);
			return criteria.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				this.getLogExceptionMessage("getByName", LocationDAO.NAME_OR_OPERATION, name + "|" + operation, e.getMessage(),
					LocationDAO.CLASS_NAME_LOCATION), e);
		}
	}

	private void addNameSearchCriteria(final String name, final Operation operation, final Criteria criteria) {
		if (operation == null || operation == Operation.EQUAL) {
			criteria.add(Restrictions.eq(LocationDAO.LNAME, name));
		} else if (operation == Operation.LIKE) {
			criteria.add(Restrictions.like(LocationDAO.LNAME, name, MatchMode.START));
		}
	}

	public List<Location> getByName(final String name, final Operation operation, final int start, final int numOfRows) {
		try {
			final Criteria criteria = this.getSession().createCriteria(Location.class);

			this.addNameSearchCriteria(name, operation, criteria);

			criteria.setFirstResult(start);
			criteria.setMaxResults(numOfRows);
			return criteria.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				this.getLogExceptionMessage("getByName", LocationDAO.NAME_OR_OPERATION, name + "|" + operation, e.getMessage(),
					LocationDAO.CLASS_NAME_LOCATION), e);
		}
	}

	public List<Location> getByAbbreviations(final List<String> abbreviations) {
		try {
			if (abbreviations != null && !abbreviations.isEmpty()) {
				final Criteria criteria = this.getSession().createCriteria(Location.class);
				criteria.add(Restrictions.in(LocationDAO.LABBREVIATION, abbreviations));
				return criteria.list();
			}
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				this.getLogExceptionMessage("getByAbbreviations", "abbreviations", abbreviations.toString(), e.getMessage(),
					LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return new ArrayList<>();
	}

	public List<LocationDTO> searchLocations(
		final LocationSearchRequest locationSearchRequest, final Pageable pageable,
		final String programUUID) {
		final SQLQueryBuilder queryBuilder = LocationSearchDAOQuery.getSelectQuery(locationSearchRequest, pageable,
			programUUID);
		final SQLQuery query = this.getSession().createSQLQuery(queryBuilder.build());
		queryBuilder.addParamsToQuery(query);
		queryBuilder.addScalarsToQuery(query);

		query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);

		GenericDAO.addPaginationToSQLQuery(query, pageable);

		final List<Map<String, Object>> results = query.list();

		return results.stream().map(row -> {
			final LocationDTO locationDTO = new LocationDTO();
			locationDTO.setId((Integer) row.get(LocationSearchDAOQuery.LOCATION_ID_ALIAS));
			locationDTO.setName((String) row.get(LocationSearchDAOQuery.LOCATION_NAME_ALIAS));
			locationDTO.setType((Integer) row.get(LocationSearchDAOQuery.LOCATION_TYPE_ALIAS));
			locationDTO.setLocationTypeName((String) row.get(LocationSearchDAOQuery.LOCATION_TYPE_NAME_ALIAS));
			locationDTO.setAbbreviation((String) row.get(LocationSearchDAOQuery.ABBREVIATION_ALIAS));
			locationDTO.setLatitude((Double) row.get(LocationSearchDAOQuery.LATITUDE_ALIAS));
			locationDTO.setLongitude((Double) row.get(LocationSearchDAOQuery.LONGITUDE_ALIAS));
			locationDTO.setAltitude((Double) row.get(LocationSearchDAOQuery.ALTITUDE_ALIAS));
			locationDTO.setCountryId((Integer) row.get(LocationSearchDAOQuery.COUNTRY_ID_ALIAS));
			locationDTO.setCountryName((String) row.get(LocationSearchDAOQuery.COUNTRY_NAME_ALIAS));
			locationDTO.setCountryCode((String) row.get(LocationSearchDAOQuery.COUNTRY_CODE_ALIAS));
			locationDTO.setProvinceId((Integer) row.get(LocationSearchDAOQuery.PROVINCE_ID_ALIAS));
			locationDTO.setProvinceName((String) row.get(LocationSearchDAOQuery.PROVINCE_NAME_ALIAS));
			locationDTO.setDefaultLocation((Boolean) row.get(LocationSearchDAOQuery.LOCATION_DEFAULT_ALIAS));

			final Integer programFavoriteId = (Integer) row.get(LocationSearchDAOQuery.FAVORITE_PROGRAM_ID_ALIAS);
			if (programFavoriteId != null) {
				final ProgramFavoriteDTO programFavoriteDTO =
					new ProgramFavoriteDTO(programFavoriteId, ProgramFavorite.FavoriteType.LOCATION, locationDTO.getId(),
						(String) row.get(LocationSearchDAOQuery.FAVORITE_PROGRAM_UUID_ALIAS));
				locationDTO.setProgramFavorites(Arrays.asList(programFavoriteDTO));
			}

			return locationDTO;
		}).collect(Collectors.toList());
	}

	@SuppressWarnings("rawtypes")
	public List<Location> getAllBreedingLocations() {
		try {
			return this.getSession().createCriteria(this.getPersistentClass())
				.add(Restrictions.in("ltype", Arrays.asList(410, 411, 412)))
				.addOrder(Order.asc(LocationDAO.LNAME))
				.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				this.getLogExceptionMessage("getAllBreedingLocations", "", null, e.getMessage(), "GermplasmDataManager"), e);
		}
	}

	// TODO: add country and province as location entities in LocationDTO
	public LocationDTO getLocationDTO(final Integer locationId) {
		try {
			final StringBuilder query = new StringBuilder("select l.locid as id," //
				+ "  l.lname as name," //
				+ "  l.ltype as type," //
				+ "  l.labbr as abbreviation," //
				+ "  g.lat as latitude," //
				+ "  g.lon as longitude," //
				+ "  g.alt as altitude," //
				+ "  l.cntryid as countryId," //
				+ "  l.snl1id as provinceId, " //
				+ "  l.ldefault as defaultLocation " //
				+ " from location l" //
				+ "  left join georef g on l.locid = g.locid" //
				+ " where l.locid = :locationId");

			final SQLQuery sqlQuery = this.getSession().createSQLQuery(query.toString());
			sqlQuery.setParameter("locationId", locationId);
			sqlQuery.addScalar("id").addScalar("name").addScalar("type").addScalar("abbreviation").addScalar("latitude")
				.addScalar("longitude").addScalar("altitude").addScalar("countryId").addScalar("provinceId").addScalar("defaultLocation");
			sqlQuery.setResultTransformer(Transformers.aliasToBean(LocationDTO.class));

			return (LocationDTO) sqlQuery.uniqueResult();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error with getLocationDTO(locationId=" + locationId + "): " + e.getMessage(), e);
		}
	}

	public List<LocationDetails> getLocationDetails(final Integer locationId, final Integer start, final Integer numOfRows) {
		try {

			final StringBuilder query = new StringBuilder().append("select l.lname as location_name,l.locid,l.ltype as ltype,")
				.append(" g.lat as latitude, g.lon as longitude, g.alt as altitude,")
				.append(" c.cntryid as cntryid, c.isofull as country_full_name, l.labbr as location_abbreviation,")
				.append(" ud.fname as location_type,").append(" ud.fdesc as location_description,")
				.append(" c.isoabbr as cntry_name, province.lname AS province_name, province.locid as province_id, l.ldefault")
				.append(" from location l")
				.append(" left join georef g on l.locid = g.locid")
				.append(" left join cntry c on l.cntryid = c.cntryid")
				.append(" left join udflds ud on ud.fldno = l.ltype")
				.append(" left join location province on l.snl1id = province.locid");

			if (locationId != null) {
				query.append(" where l.locid = :id");

				final SQLQuery sqlQuery = this.getSession().createSQLQuery(query.toString());
				sqlQuery.setParameter("id", locationId);
				sqlQuery.setFirstResult(start);
				sqlQuery.setMaxResults(numOfRows);
				sqlQuery.addEntity(LocationDetails.class);

				return sqlQuery.list();
			}

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				this.getLogExceptionMessage("getLocationDetails", "id", String.valueOf(locationId), e.getMessage(),
					LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return new ArrayList<>();
	}

	@Override
	public Location saveOrUpdate(final Location location) {
		try {
			final Location savedLocation = super.saveOrUpdate(location);
			if (location.getGeoref() != null) {
				location.getGeoref().setLocid(location.getLocid());
				this.getSession().saveOrUpdate(location.getGeoref());
			}
			return savedLocation;
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error in saveOrUpdate(location): " + e.getMessage(), e);
		}
	}

	public List<LocationDTO> getAllCountries() {
		final List<LocationDTO> locationDTOs = new ArrayList<>();
		try {
			final SQLQuery query = this.getSession().createSQLQuery(Location.GET_ALL_COUNTRY);
			query.addEntity(Location.class);
			final List<Location> Locations = query.list();
			Locations.stream().forEach((location) -> {
				final LocationDTO locationDTO = new LocationDTO(location);
				locationDTOs.add(locationDTO);
			});

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				this.getLogExceptionMessage("getAllCountries", "", null, e.getMessage(), LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return locationDTOs;
	}

	public List<Location> getLocationByIds(final Collection<Integer> ids) {

		if (ids == null || ids.isEmpty()) {
			return new ArrayList<>();
		}

		try {
			return this.getSession().createCriteria(Location.class).add(Restrictions.in(LocationDAO.LOCID, ids))
				.addOrder(Order.asc(LocationDAO.LNAME)).list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(String.format("Error with getLocationByIds(id=[%s])", StringUtils.join(ids, ",")), e);
		}
	}

	public Map<Integer, String> getLocationNamesByLocationIDs(final List<Integer> locIds) {
		final Map<Integer, String> toreturn = new HashMap<>();

		final List<Location> locations = this.getLocationByIds(locIds);
		for (final Location location : locations) {
			toreturn.put(location.getLocid(), location.getLname());
		}

		return toreturn;
	}

	public List<Location> getLocationsByDTypeAndLType(final String dval, final Integer dType, final Integer lType) {
		try {
			final DetachedCriteria locdesCriteria = DetachedCriteria.forClass(Locdes.class);
			locdesCriteria.setProjection(Property.forName("locationId"));
			locdesCriteria.add(Restrictions.eq("typeId", dType));
			locdesCriteria.add(Restrictions.eq("dval", dval));

			return this.getSession().createCriteria(Location.class, "location")
				.add(Restrictions.eq("location.ltype", lType))
				.add(Property.forName("location.locid").in(locdesCriteria))
				.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				this.getLogExceptionMessage("getLocationsByDTypeAndLType", "dType|lType", dType + "|" + lType, e.getMessage(),
					"Locdes"), e);
		}
	}

	public List<Location> getByTypeWithParent(final Integer type, final Integer relationshipType) {
		final List<Location> locationList = new ArrayList<>();
		try {
			final Session session = this.getSession();
			final String sql = "SELECT f.locid, f.lname, fd.dval " + " FROM location f "
				+ " INNER JOIN locdes fd ON fd.locid = f.locid AND fd.dtype = " + relationshipType + " WHERE f.ltype = " + type;
			final SQLQuery query = session.createSQLQuery(sql);
			final List<Object[]> results = query.list();

			for (final Object o : results) {
				final Object[] result = (Object[]) o;
				if (result != null) {
					final Integer fieldId = (Integer) result[0];
					final String fieldName = (String) result[1];
					final String parentId = (String) result[2];

					final Location location = new Location();
					location.setLocid(fieldId);
					location.setLname(fieldName);
					location.setParentLocationId(parentId != null && NumberUtils.isNumber(parentId) ? Integer.valueOf(parentId) : null);
					locationList.add(location);
				}
			}
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(this.getLogExceptionMessage("getByTypeWithParent", "type", type.toString(), e.getMessage(),
				LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return locationList;
	}

	public Map<Integer, Location> getNamesByIdsIntoMap(final Collection<Integer> ids) {
		final Map<Integer, Location> map = new HashMap<>();
		try {
			final Criteria criteria = this.getSession().createCriteria(Location.class);
			criteria.add(Restrictions.in(LocationDAO.LOCID, ids));
			final List<Location> locations = criteria.list();

			if (locations != null && !locations.isEmpty()) {
				for (final Location location : locations) {
					map.put(location.getLocid(), location);
				}
			}

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				this.getLogExceptionMessage("getNamesByIdsIntoMap", "", null, e.getMessage(), LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return map;
	}

	public List<Location> getByIds(final List<Integer> ids) {
		List<Location> locations = new ArrayList<>();

		if (ids == null || ids.isEmpty()) {
			return locations;
		}

		try {
			final Criteria criteria = this.getSession().createCriteria(Location.class);
			criteria.add(Restrictions.in(LocationDAO.LOCID, ids));
			locations = criteria.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				this.getLogExceptionMessage("getByIds", "", null, e.getMessage(), LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return locations;
	}

	public List<Locdes> getLocdesByLocId(final Integer locationId) {
		try {
			final Criteria criteria = this.getSession().createCriteria(Locdes.class);
			criteria.add(Restrictions.eq("locationId", locationId));
			return criteria.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error with getByIds() query from Location: " + e.getMessage(), e);
		}
	}

	public List<Location> getBreedingLocations(final List<Integer> ids) {
		try {
			final List<Integer> validCodes = new ArrayList<>();
			// 410, 411, 412
			validCodes.add(410);
			validCodes.add(411);
			validCodes.add(412);

			final Criteria criteria = this.getSession().createCriteria(Location.class);
			if (!ids.isEmpty()) {
				criteria.add(Restrictions.in("locid", ids));
			}
			criteria.add(Restrictions.in("ltype", validCodes));
			criteria.addOrder(Order.asc("lname"));

			return criteria.list();
		} catch (final HibernateException e) {
			LocationDAO.LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException(
				this.getLogExceptionMessage("getBreedingLocations", "", null, e.getMessage(), "Location"),
				e);
		}
	}

	public List<Location> getSeedingLocations(final List<Integer> ids, final Integer seedLType) {
		try {

			final Criteria criteria = this.getSession().createCriteria(Location.class);
			if (!ids.isEmpty()) {
				criteria.add(Restrictions.in("locid", ids));
			}
			criteria.add(Restrictions.eq("ltype", seedLType));
			criteria.addOrder(Order.asc("lname"));

			return criteria.list();
		} catch (final HibernateException e) {
			LocationDAO.LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException(this.getLogExceptionMessage("getSeedingLocations", "", null, e.getMessage(), "Location"), e);
		}
	}

	public long countSearchLocation(final LocationSearchRequest locationSearchRequest, final String programUUID) {
		final SQLQueryBuilder queryBuilder = LocationSearchDAOQuery.getCountQuery(locationSearchRequest, programUUID);
		final SQLQuery query = this.getSession().createSQLQuery(queryBuilder.build());
		queryBuilder.addParamsToQuery(query);

		return ((BigInteger) query.uniqueResult()).longValue();
	}

	public List<org.generationcp.middleware.api.location.Location> getLocations(
		final LocationSearchRequest locationSearchRequest,
		final Pageable pageable) {

		final SQLQueryBuilder queryBuilder = LocationSearchDAOQuery.getSelectQuery(locationSearchRequest, pageable, null);
		final SQLQuery sqlQuery = this.getSession().createSQLQuery(queryBuilder.build());
		queryBuilder.addParamsToQuery(sqlQuery);

		addPaginationToSQLQuery(sqlQuery, pageable);
		sqlQuery.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
		final List<Map<String, Object>> results = sqlQuery.list();
		final List<org.generationcp.middleware.api.location.Location> locations = new ArrayList<>();
		for (final Map<String, Object> result : results) {

			Coordinate coordinate = null;
			final Double longitude = (Double) result.get(LocationSearchDAOQuery.LONGITUDE_ALIAS);
			final Double latitude = (Double) result.get(LocationSearchDAOQuery.LATITUDE_ALIAS);
			final Double altitude = (Double) result.get(LocationSearchDAOQuery.ALTITUDE_ALIAS);
			if (longitude != null && latitude != null) {
				final List<Double> coordinatesList = new ArrayList<>();
				coordinatesList.add(longitude);
				coordinatesList.add(latitude);
				if (altitude != null) {
					// Only add altitude if available.
					coordinatesList.add(altitude);
				}
				final Geometry geometry = new Geometry(coordinatesList, "Point");
				coordinate = new Coordinate(geometry, "Feature");
			}

			final org.generationcp.middleware.api.location.Location location = new org.generationcp.middleware.api.location.Location()
				.withLocationDbId(String.valueOf(result.get(LocationSearchDAOQuery.LOCATION_ID_ALIAS)))
				.withLocationType(String.valueOf(result.get(LocationSearchDAOQuery.LOCATION_TYPE_NAME_ALIAS)))
				.withLocationName(String.valueOf(result.get(LocationSearchDAOQuery.LOCATION_NAME_ALIAS)))
				.withAbbreviation(String.valueOf(result.get(LocationSearchDAOQuery.ABBREVIATION_ALIAS)))
				.withCountryCode(String.valueOf(result.get(LocationSearchDAOQuery.COUNTRY_CODE_ALIAS)))
				.withCountryName(String.valueOf(result.get(LocationSearchDAOQuery.COUNTRY_NAME_ALIAS)))
				.withName(String.valueOf(result.get(LocationSearchDAOQuery.LOCATION_NAME_ALIAS)))
				.withCoordinates(coordinate)
				.withLatitude(latitude)
				.withLongitude(longitude)
				.withAltitude(altitude);
			if (!location.getLocationType().equalsIgnoreCase(LocationDAO.COUNTRY)) {
				final Map<String, String> additionalInfo = new HashMap<>();
				additionalInfo.put("province", String.valueOf(result.get(LocationSearchDAOQuery.PROVINCE_NAME_ALIAS)));
				location.withAdditionalInfo(additionalInfo);
			}

			locations.add(location);
		}
		return locations;
	}

	public Location getDefaultLocationByType(final Integer type) {
		try {
			final Criteria criteria = this.getSession().createCriteria(Location.class);
			criteria.add(Restrictions.eq("ltype", type));
			criteria.add(Restrictions.eq("ldefault", Boolean.TRUE));

			return (Location) criteria.uniqueResult();
		} catch (final HibernateException e) {
			LocationDAO.LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException(
				this.getLogExceptionMessage("getDefaultLocationByType", "type", String.valueOf(type), e.getMessage(), "Location"),
				e);
		}
	}

	public Optional<Location> getUnspecifiedLocation() {
		final List<Location> locations = this.getByName(Location.UNSPECIFIED_LOCATION, Operation.EQUAL);
		if (!locations.isEmpty()) {
			return Optional.of(locations.get(0));
		}
		return Optional.empty();
	}

	public boolean blockIdIsUsedInFieldMap(final List<Integer> blockIds) {
		try {

			final StringBuilder queryString = new StringBuilder().append("SELECT ");
			queryString.append("count(blk.value)");
			queryString.append(" FROM ");
			queryString.append(" 	nd_experiment nde ");
			queryString.append("        INNER JOIN ");
			queryString.append("    project proj ON proj.project_id = nde.project_id ");
			queryString.append("        INNER JOIN ");
			queryString.append("    project st ON st.project_id = proj.study_id ");
			queryString.append("        INNER JOIN ");
			queryString.append("    nd_experiment geo ON nde.nd_experiment_id = geo.nd_experiment_id ");
			queryString.append("        AND geo.type_id = " + TermId.PLOT_EXPERIMENT.getId() + " ");
			queryString.append("        LEFT JOIN ");
			queryString.append("    nd_geolocationprop blk ON blk.nd_geolocation_id = geo.nd_geolocation_id ");
			queryString.append("        AND blk.type_id = " + TermId.BLOCK_ID.getId() + " ");
			queryString.append(" WHERE  blk.value in(:blockIds); ");

			final SQLQuery query = this.getSession().createSQLQuery(queryString.toString());
			query.setParameterList("blockIds", blockIds);
			return ((BigInteger) query.uniqueResult()).longValue() > 0;
		} catch (final HibernateException e) {
			LocationDAO.LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException(
				this.getLogExceptionMessage("blockIdIsUsedInFieldMap", "blockIds", String.valueOf(blockIds), e.getMessage(), "Location"),
				e);
		}
	}

	public void deleteByLocationIds(final List<Integer> locids) {
		try {
			final Query query = this.getSession().createQuery("delete from Location where locid in (:locids) ");
			query.setParameterList("locids", locids);
			query.executeUpdate();
		} catch (final HibernateException e) {
			final String message = "Error with deleteByLocationIds(locids=" + locids + ") in LocationDAO: " + e.getMessage();
			LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}
}
