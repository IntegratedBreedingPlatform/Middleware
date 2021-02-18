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
import org.generationcp.middleware.domain.dms.LocationDto;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Georef;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.LocationDetails;
import org.generationcp.middleware.pojos.Locdes;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO class for {@link Location}.
 */
@SuppressWarnings("unchecked")
public class LocationDAO extends GenericDAO<Location, Integer> {

	private static final String PROGRAM_UUID = "programUUID";
	private static final String CLASS_NAME_LOCATION = "Location";
	private static final String COUNTRY_ID = "cntryid";
	private static final String COUNTRY = "country";
	private static final String GET_BY_TYPE = "getByType";
	private static final String GET_BY_COUNTRY = "getByCountry";
	private static final String LNAME = "lname";
	private static final String LOCID = "locid";
	private static final String LTYPE = "ltype";
	private static final String LABBREVIATION = "labbr";
	private static final String NAME_OR_OPERATION = "name|operation";

	private static final Logger LOG = LoggerFactory.getLogger(LocationDAO.class);

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

	public List<Location> getByNameAndProgramUUID(final String name, final Operation operation, final String programUUID) {
		try {
			final Criteria criteria = this.getSession().createCriteria(Location.class);

			this.addNameSearchCriteria(name, operation, criteria);
			criteria.add(
				Restrictions.or(Restrictions.eq(LocationDAO.PROGRAM_UUID, programUUID), Restrictions.isNull(LocationDAO.PROGRAM_UUID)));
			return criteria.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				this.getLogExceptionMessage("getByName", "name|operation", name + "|" + operation, e.getMessage(), "Location"), e);
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

	public List<Location> getByNameAndProgramUUID(final String name, final Operation operation, final String programUUID, final int start,
		final int numOfRows) {
		try {
			final Criteria criteria = this.getSession().createCriteria(Location.class);

			this.addNameSearchCriteria(name, operation, criteria);
			criteria.add(
				Restrictions.or(Restrictions.eq(LocationDAO.PROGRAM_UUID, programUUID), Restrictions.isNull(LocationDAO.PROGRAM_UUID)));
			criteria.setFirstResult(start);
			criteria.setMaxResults(numOfRows);
			return criteria.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				this.getLogExceptionMessage("getByName", "name|operation", name + "|" + operation, e.getMessage(), "Location"), e);
		}
	}

	public long countByName(final String name, final Operation operation) {
		try {
			if (name != null) {
				final Criteria criteria = this.getSession().createCriteria(Location.class);
				criteria.setProjection(Projections.rowCount());

				this.addNameSearchCriteria(name, operation, criteria);

				return ((Long) criteria.uniqueResult()).longValue();
			}
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				this.getLogExceptionMessage("countByName", LocationDAO.NAME_OR_OPERATION, name + "|" + operation, e.getMessage(),
					LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return 0;
	}

	public long countByNameAndProgramUUID(final String name, final Operation operation, final String programUUID) {
		try {
			if (name != null) {
				final Criteria criteria = this.getSession().createCriteria(Location.class);
				criteria.setProjection(Projections.rowCount());

				this.addNameSearchCriteria(name, operation, criteria);
				criteria.add(
					Restrictions.or(Restrictions.eq(LocationDAO.PROGRAM_UUID, programUUID), Restrictions.isNull(LocationDAO.PROGRAM_UUID)));
				return ((Long) criteria.uniqueResult()).longValue();
			}
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				this.getLogExceptionMessage("countByName", "name|operation|programUUID", name + "|" + operation + "|" + programUUID,
					e.getMessage(), "Location"), e);
		}
		return 0;
	}

	public List<Location> getByCountry(final Country country) {
		try {
			if (country != null) {
				final Integer countryId = country.getCntryid();
				final Criteria criteria = this.getSession().createCriteria(Location.class);
				criteria.add(Restrictions.eq(LocationDAO.COUNTRY_ID, countryId));
				criteria.addOrder(Order.asc(LocationDAO.LNAME));
				return criteria.list();
			}
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				this.getLogExceptionMessage(LocationDAO.GET_BY_COUNTRY, LocationDAO.COUNTRY, country.toString(), e.getMessage(),
					LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return new ArrayList<>();
	}

	public List<Location> getByCountryAndType(final Country country, final Integer type) {
		try {
			if (country != null && type != null) {
				final Integer countryId = country.getCntryid();
				final Criteria criteria = this.getSession().createCriteria(Location.class);
				criteria.add(Restrictions.eq(LocationDAO.COUNTRY_ID, countryId));
				criteria.add(Restrictions.eq(LocationDAO.LTYPE, type));
				criteria.addOrder(Order.asc(LocationDAO.LNAME));
				return criteria.list();
			}
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				this.getLogExceptionMessage(LocationDAO.GET_BY_COUNTRY, LocationDAO.COUNTRY, country.toString(), e.getMessage(),
					LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return new ArrayList<>();
	}

	public List<Location> getByNameCountryAndType(final String name, final Country country, final Integer type) {
		try {

			Integer countryId = null;
			if (country != null) {
				countryId = country.getCntryid();
			}

			final Criteria criteria = this.getSession().createCriteria(Location.class);

			if (countryId != null) {
				criteria.add(Restrictions.eq(LocationDAO.COUNTRY_ID, countryId));
			}

			if (type != null && 0 != type.intValue()) {
				criteria.add(Restrictions.eq(LocationDAO.LTYPE, type));
			}

			if (name != null && !name.isEmpty()) {
				criteria.add(Restrictions.like(LocationDAO.LNAME, "%" + name.trim() + "%"));
			}

			criteria.addOrder(Order.asc(LocationDAO.LNAME));

			return criteria.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				this.getLogExceptionMessage(LocationDAO.GET_BY_COUNTRY, LocationDAO.COUNTRY, country.toString(), e.getMessage(),
					LocationDAO.CLASS_NAME_LOCATION), e);
		}
	}

	public List<Location> getByCountry(final Country country, final int start, final int numOfRows) {
		try {
			if (country != null) {
				final Integer countryId = country.getCntryid();
				final Criteria criteria = this.getSession().createCriteria(Location.class);
				criteria.add(Restrictions.eq(LocationDAO.COUNTRY_ID, countryId));
				criteria.setFirstResult(start);
				criteria.setMaxResults(numOfRows);
				return criteria.list();
			}
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				this.getLogExceptionMessage(LocationDAO.GET_BY_COUNTRY, LocationDAO.COUNTRY, country.toString(), e.getMessage(),
					LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return new ArrayList<>();
	}

	public long countByCountry(final Country country) {
		try {
			if (country != null) {
				final Integer countryId = country.getCntryid();
				final Criteria criteria = this.getSession().createCriteria(Location.class);
				criteria.add(Restrictions.eq(LocationDAO.COUNTRY_ID, countryId));
				criteria.setProjection(Projections.rowCount());
				return ((Long) criteria.uniqueResult()).longValue();
			}
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				this.getLogExceptionMessage(LocationDAO.GET_BY_COUNTRY, LocationDAO.COUNTRY, country.toString(), e.getMessage(),
					LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return 0;
	}

	public List<Location> getByType(final Integer type) {
		try {
			if (type != null) {
				final Criteria criteria = this.getSession().createCriteria(Location.class);
				criteria.add(Restrictions.eq(LocationDAO.LTYPE, type));
				criteria.addOrder(Order.asc(LocationDAO.LNAME));
				return criteria.list();
			}
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				this.getLogExceptionMessage(LocationDAO.GET_BY_TYPE, "type", String.valueOf(type), e.getMessage(),
					LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return new ArrayList<>();
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

	public long countFilterLocations(final LocationSearchRequest locationSearchRequest) {
		try {

			final Criteria criteria =
				this.createFilterLocationCriteria(locationSearchRequest);
			return (Long) criteria.setProjection(Projections.rowCount()).uniqueResult();

		} catch (final HibernateException e) {
			LocationDAO.LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException(
				this.getLogExceptionMessage("countFilterLocations", "types,locationIds,locationAbbreviations,locationName", "",
					e.getMessage(),
					LocationDAO.CLASS_NAME_LOCATION), e);
		}
	}

	public List<Location> filterLocations(final LocationSearchRequest locationSearchRequest, final Pageable pageable) {
		try {

			final Criteria criteria =
				this.createFilterLocationCriteria(locationSearchRequest);

			criteria.addOrder(Order.asc(LocationDAO.LNAME));

			addPagination(criteria, pageable);

			return criteria.list();

		} catch (final HibernateException e) {
			LocationDAO.LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException(
				this.getLogExceptionMessage("filterLocations", "types,locationIds,locationAbbreviations,locationName", "", e.getMessage(),
					LocationDAO.CLASS_NAME_LOCATION), e);
		}
	}

	private Criteria createFilterLocationCriteria(final LocationSearchRequest locationSearchRequest) {
		final Criteria criteria = this.getSession().createCriteria(Location.class);

		if (locationSearchRequest.getProgramUUID() != null) {
			criteria.add(Restrictions.disjunction()
				.add(Restrictions.eq(PROGRAM_UUID, locationSearchRequest.getProgramUUID()))
				.add(Restrictions.isNull(PROGRAM_UUID)));
		} else {
			criteria.add(Restrictions.isNull(PROGRAM_UUID));
		}

		if (!CollectionUtils.isEmpty(locationSearchRequest.getLocationTypeIds())) {
			criteria.add(Restrictions.in(LocationDAO.LTYPE, locationSearchRequest.getLocationTypeIds()));
		}

		if (!CollectionUtils.isEmpty(locationSearchRequest.getLocationIds())) {
			criteria.add(Restrictions.in(LocationDAO.LOCID, locationSearchRequest.getLocationIds()));
		}

		if (!CollectionUtils
			.isEmpty(locationSearchRequest.getLocationAbbreviations())) {
			criteria.add(Restrictions.in(LocationDAO.LABBREVIATION, locationSearchRequest.getLocationAbbreviations()));
		}

		if (StringUtils.isNotEmpty(locationSearchRequest.getLocationName())) {
			criteria.add(Restrictions.like("lname", locationSearchRequest.getLocationName(), MatchMode.START));
		}
		return criteria;
	}

	public List<Location> getByType(final Integer type, final String programUUID) {
		try {
			if (type != null) {
				final Criteria criteria = this.getSession().createCriteria(Location.class);
				criteria.add(Restrictions.eq(LocationDAO.LTYPE, type));
				criteria.add(
					Restrictions.or(Restrictions.eq(LocationDAO.PROGRAM_UUID, programUUID), Restrictions.isNull(LocationDAO.PROGRAM_UUID)));
				criteria.addOrder(Order.asc(LocationDAO.LNAME));
				return criteria.list();
			}
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				this.getLogExceptionMessage(LocationDAO.GET_BY_TYPE, "type", String.valueOf(type), e.getMessage(),
					LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return new ArrayList<>();
	}

	public List<Location> getByType(final Integer type, final int start, final int numOfRows) {
		try {
			if (type != null) {
				final Criteria criteria = this.getSession().createCriteria(Location.class);
				criteria.add(Restrictions.eq(LocationDAO.LTYPE, type));
				criteria.setFirstResult(start);
				criteria.setMaxResults(numOfRows);
				return criteria.list();
			}
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				this.getLogExceptionMessage(LocationDAO.GET_BY_TYPE, "type", String.valueOf(type), e.getMessage(),
					LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return new ArrayList<>();
	}

	public long countByType(final Integer type) {
		try {
			if (type != null) {
				final Criteria criteria = this.getSession().createCriteria(Location.class);
				criteria.add(Restrictions.eq(LocationDAO.LTYPE, type));
				criteria.setProjection(Projections.rowCount());
				return ((Long) criteria.uniqueResult()).longValue();
			}
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(this.getLogExceptionMessage("countByType", "type", String.valueOf(type), e.getMessage(),
				LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return 0;
	}

	public long countByType(final Integer type, final String programUUID) {
		try {
			if (type != null) {
				final Criteria criteria = this.getSession().createCriteria(Location.class);
				criteria.add(Restrictions.eq(LocationDAO.LTYPE, type));
				criteria.add(
					Restrictions.or(Restrictions.eq(LocationDAO.PROGRAM_UUID, programUUID), Restrictions.isNull(LocationDAO.PROGRAM_UUID)));
				criteria.setProjection(Projections.rowCount());
				return ((Long) criteria.uniqueResult()).longValue();
			}
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(this.getLogExceptionMessage("countByType", "type", String.valueOf(type), e.getMessage(),
				LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return 0;
	}

	@SuppressWarnings("rawtypes")
	public List<Location> getAllBreedingLocations() {
		final List<Location> locationList = new ArrayList<>();
		try {
			final Session session = this.getSession();
			final SQLQuery query = session.createSQLQuery(Location.GET_ALL_BREEDING_LOCATIONS);
			final List results = query.list();

			for (final Object o : results) {
				final Object[] result = (Object[]) o;
				if (result != null) {
					final Integer locid = (Integer) result[0];
					final Integer ltype = (Integer) result[1];
					final Integer nllp = (Integer) result[2];
					final String lname = (String) result[3];
					final String labbr = (String) result[4];
					final Integer snl3id = (Integer) result[5];
					final Integer snl2id = (Integer) result[6];
					final Integer snl1id = (Integer) result[7];
					final Integer cntryid = (Integer) result[8];
					final Integer lrplce = (Integer) result[9];
					final Double latitude = (Double) result[11];
					final Double longitude = (Double) result[12];
					final Double altitude = (Double) result[13];
					final String programUUID = (String) result[14];
					final Boolean ldefault = (Boolean) result[15];

					final Location location = new Location(locid, ltype, nllp, lname, labbr, snl3id, snl2id, snl1id, cntryid, lrplce);
					location.setLdefault(ldefault);
					location.setProgramUUID(programUUID);

					final Georef georef = new Georef();
					georef.setLocid(locid);
					georef.setLat(latitude);
					georef.setLon(longitude);
					georef.setAlt(altitude);
					location.setGeoref(georef);
					locationList.add(location);
				}
			}
			return locationList;
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				this.getLogExceptionMessage("getAllBreedingLocations", "", null, e.getMessage(), "GermplasmDataManager"), e);
		}
	}

	@SuppressWarnings("deprecation")
	public Long countAllBreedingLocations() {
		try {
			final Session session = this.getSession();
			final SQLQuery query = session.createSQLQuery(Location.COUNT_ALL_BREEDING_LOCATIONS);
			return (Long) query.addScalar("count", Hibernate.LONG).uniqueResult();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				this.getLogExceptionMessage("countAllBreedingLocations", "", null, e.getMessage(), LocationDAO.CLASS_NAME_LOCATION), e);
		}
	}

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
				+ "  l.snl1id as provinceId," //
				+ "  l.program_uuid as programUUID" //
				+ " from location l" //
				+ "  left join georef g on l.locid = g.locid" //
				+ " where l.locid = :locationId");

			final SQLQuery sqlQuery = this.getSession().createSQLQuery(query.toString());
			sqlQuery.setParameter("locationId", locationId);
			sqlQuery.addScalar("id").addScalar("name").addScalar("type").addScalar("abbreviation").addScalar("latitude")
				.addScalar("longitude").addScalar("altitude").addScalar("countryId").addScalar("provinceId").addScalar("programUUID");
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
				.append(" ud.fname as location_type,").append(" ud.fdesc as location_description, l.program_uuid,")
				.append(" c.isoabbr as cntry_name, province.lname AS province_name, province.locid as province_id, l.ldefault")
				.append(" from location l").append(" left join georef g on l.locid = g.locid")
				.append(" left join cntry c on l.cntryid = c.cntryid").append(" left join udflds ud on ud.fldno = l.ltype")
				.append(" ,location province");

			if (locationId != null) {
				query.append(" where l.locid = :id");
				query.append(" AND province.locid = l.snl1id");

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

	public List<Location> getAllProvincesByCountry(final Integer countryId) {
		if (countryId == null || countryId == 0) {
			return new ArrayList<>();
		}

		try {
			final SQLQuery query = this.getSession().createSQLQuery(Location.GET_PROVINCE_BY_COUNTRY);
			query.addEntity(Location.class);
			query.setParameter("countryId", countryId);
			return query.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				this.getLogExceptionMessage("getAllProvinces", "", null, e.getMessage(), LocationDAO.CLASS_NAME_LOCATION), e);
		}
	}

	public List<Location> getAllProvinces() {

		try {
			final SQLQuery query = this.getSession().createSQLQuery(Location.GET_ALL_PROVINCES);
			query.addEntity(Location.class);
			return query.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				this.getLogExceptionMessage("getAllProvinces", "", null, e.getMessage(), LocationDAO.CLASS_NAME_LOCATION), e);
		}
	}

	public List<LocationDto> getLocationDtoByIds(final Collection<Integer> ids) {
		final List<LocationDto> returnList = new ArrayList<>();
		if (ids == null || ids.isEmpty()) {
			return returnList;
		}
		try {
			final String sql = "SELECT l.lname, prov.lname, c.isoabbr, l.locid" + " FROM location l"
				+ " LEFT JOIN location prov ON prov.locid = l.snl1id" + " LEFT JOIN cntry c ON c.cntryid = l.cntryid"
				+ " WHERE l.locid in (:ids)";
			final SQLQuery query = this.getSession().createSQLQuery(sql);
			query.setParameterList("ids", ids);
			final List<Object[]> results = query.list();

			if (results != null) {
				for (final Object[] result : results) {
					returnList.add(new LocationDto((Integer) result[3], (String) result[0], (String) result[1], (String) result[2]));
				}
			}

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(this.getLogExceptionMessage("getLocationDtoById", "id", ids.toString(), e.getMessage(),
				LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return returnList;
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

	public Map<Integer, String> getLocationNamesMapByGIDs(final List<Integer> gids) {
		final Map<Integer, String> toreturn = new HashMap<>();
		for (final Integer gid : gids) {
			toreturn.put(gid, null);
		}

		try {
			final SQLQuery query = this.getSession().createSQLQuery(Location.GET_LOCATION_NAMES_BY_GIDS);
			query.setParameterList("gids", gids);

			final List<Object> results = query.list();
			for (final Object result : results) {
				final Object[] resultArray = (Object[]) result;
				final Integer gid = (Integer) resultArray[0];
				final String location = (String) resultArray[2];
				toreturn.put(gid, location);
			}
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				this.getLogExceptionMessage("getLocationNamesMapByGIDs", "gids", gids.toString(), e.getMessage(),
					LocationDAO.CLASS_NAME_LOCATION), e);
		}

		return toreturn;
	}

	public List<Location> getLocationsByDTypeAndLType(final String dval, final Integer dType, final Integer lType) {
		final List<Location> locations = new ArrayList<>();
		try {
			final StringBuilder sqlString = new StringBuilder().append("SELECT  l.locid, l.ltype, l.nllp, l.lname, l.labbr")
				.append(", l.snl3id, l.snl2id, l.snl1id, l.cntryid, l.lrplce ").append("FROM locdes ld INNER JOIN location l ")
				.append(" ON l.locid = ld.locid ").append("WHERE dtype = :dtype  AND ltype = :ltype AND dval = :dval ");

			final SQLQuery query = this.getSession().createSQLQuery(sqlString.toString());
			query.setParameter("dtype", dType);
			query.setParameter(LocationDAO.LTYPE, lType);
			query.setParameter("dval", dval);

			final List<Object[]> results = query.list();

			if (!results.isEmpty()) {
				for (final Object[] row : results) {
					final Integer locid = (Integer) row[0];
					final Integer ltype = (Integer) row[1];
					final Integer nllp = (Integer) row[2];
					final String lname = (String) row[3];
					final String labbr = (String) row[4];
					final Integer snl3id = (Integer) row[5];
					final Integer snl2id = (Integer) row[6];
					final Integer snl1id = (Integer) row[7];
					final Integer cntryid = (Integer) row[8];
					final Integer lrplce = (Integer) row[9];

					locations.add(new Location(locid, ltype, nllp, lname, labbr, snl3id, snl2id, snl1id, cntryid, lrplce));

				}
			}

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				this.getLogExceptionMessage("getLocationsByDTypeAndLType", "dType|lType", dType + "|" + lType, e.getMessage(),
					"Locdes"), e);
		}
		return locations;
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

	public List<Location> getByProgramUUID(final String programUUID) {
		List<Location> locations = new ArrayList<>();

		if (programUUID == null || programUUID.isEmpty()) {
			return locations;
		}

		try {
			final Criteria criteria = this.getSession().createCriteria(Location.class);
			criteria.add(
				Restrictions.or(Restrictions.eq(LocationDAO.PROGRAM_UUID, programUUID), Restrictions.isNull(LocationDAO.PROGRAM_UUID)));
			locations = criteria.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				this.getLogExceptionMessage("getByProgramUUID", "programUUID", null, e.getMessage(), LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return locations;
	}

	public List<Location> getByProgramUUIDAndExcludeLocationTypes(final String programUUID, final List<Integer> locationTypesToExclude) {

		List<Location> locations = new ArrayList<>();

		if (programUUID == null || programUUID.isEmpty()) {
			return locations;
		}

		try {
			final Criteria criteria = this.getSession().createCriteria(Location.class);
			criteria.add(
				Restrictions.or(Restrictions.eq(LocationDAO.PROGRAM_UUID, programUUID), Restrictions.isNull(LocationDAO.PROGRAM_UUID)));
			if (locationTypesToExclude != null && !locationTypesToExclude.isEmpty()) {
				criteria.add(Restrictions.not(Restrictions.in(LocationDAO.LTYPE, locationTypesToExclude)));
			}
			locations = criteria.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				this.getLogExceptionMessage("getByProgramUUIDAndExcludeLocationTypes", "", null, e.getMessage(),
					LocationDAO.CLASS_NAME_LOCATION), e);
		}

		return locations;
	}

	public long countByLocationAbbreviation(final String locationAbbreviation) {
		try {
			if (locationAbbreviation != null) {
				final Criteria criteria = this.getSession().createCriteria(Location.class);
				criteria.add(Restrictions.eq("labbr", locationAbbreviation));
				criteria.setProjection(Projections.rowCount());
				return ((Long) criteria.uniqueResult()).longValue();
			}
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				this.getLogExceptionMessage("countByLocationAbbreviation", "locationAbbreviation", locationAbbreviation, e.getMessage(),
					LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return 0;
	}

	public long countByProgramUUID(final String programUUID) {
		try {
			if (programUUID != null) {
				final Criteria criteria = this.getSession().createCriteria(Location.class);
				criteria.add(
					Restrictions.or(Restrictions.eq(LocationDAO.PROGRAM_UUID, programUUID), Restrictions.isNull(LocationDAO.PROGRAM_UUID)));
				criteria.setProjection(Projections.rowCount());
				return ((Long) criteria.uniqueResult()).longValue();
			}
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(this.getLogExceptionMessage("countByProgramUUID", "programUUID", programUUID, e.getMessage(),
				LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return 0;
	}

	public List<Location> getProgramLocations(final String programUUID) {
		try {
			final Criteria criteria = this.getSession().createCriteria(Location.class);
			criteria.add(Restrictions.eq(LocationDAO.PROGRAM_UUID, programUUID));
			return criteria.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error in getProgramLocations(" + programUUID + ") in LocationDao: " + e.getMessage(), e);
		}
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
			throw new MiddlewareQueryException(this.getLogExceptionMessage("getBreedingLocations", "", null, e.getMessage(), "Location"),
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

	public List<Location> getBreedingLocationsByProgramUUID(final String programUUID) {
		List<Location> locations = new ArrayList<>();

		if (programUUID == null || programUUID.isEmpty()) {
			return locations;
		}

		try {
			final Criteria criteria = this.getSession().createCriteria(Location.class);
			// filter by programUUID plus return also records with null
			// programUUID (common historical data)
			criteria.add(
				Restrictions.or(Restrictions.eq(LocationDAO.PROGRAM_UUID, programUUID), Restrictions.isNull(LocationDAO.PROGRAM_UUID)));
			// set location types for Breeding Location
			criteria.add(Restrictions.in(LocationDAO.LTYPE, Arrays.asList(Location.BREEDING_LOCATION_TYPE_IDS)));
			criteria.addOrder(Order.asc("lname"));

			locations = criteria.list();
		} catch (final HibernateException e) {
			LocationDAO.LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException(
				this.getLogExceptionMessage("getBreedingLocationsByProgramUUID", "", null, e.getMessage(), "Location"), e);

		}
		return locations;

	}

	public List<LocationDetails> getFilteredLocations(final Integer countryId, final Integer locationType, final String locationName,
		final String programUUID) {

		try {

			final StringBuilder queryString = new StringBuilder().append("SELECT l.lname as location_name,l.locid,l.ltype as ltype,")
				.append(" g.lat as latitude, g.lon as longitude, g.alt as altitude,")
				.append(" c.cntryid as cntryid, c.isofull as country_full_name, l.labbr as location_abbreviation,")
				.append(" ud.fname as location_type,").append(" ud.fdesc as location_description, l.program_uuid")
				.append(" ,c.isoabbr as cntry_name, province.lname AS province_name, province.locid as province_id, l.ldefault as ldefault")
				.append(" FROM location l").append(" LEFT JOIN georef g on l.locid = g.locid")
				.append(" LEFT JOIN cntry c on l.cntryid = c.cntryid").append(" LEFT JOIN udflds ud on ud.fldno = l.ltype")
				.append(" ,location province ").append(" WHERE (l.program_uuid = '").append(programUUID).append("'")
				.append(" or l.program_uuid is null) ").append(" and province.locid = l.snl1id ");

			if (countryId != null) {
				queryString.append(" AND c.cntryid = ");
				queryString.append(countryId);
			}

			if (locationType != null) {
				queryString.append(" AND l.ltype = ");
				queryString.append(locationType);
			}

			if (locationName != null && !locationName.isEmpty()) {
				queryString.append(" AND l.lname REGEXP '");
				queryString.append(locationName);
				queryString.append("' ");
			}

			queryString.append(" ORDER BY UPPER(l.lname) ");

			final SQLQuery query = this.getSession().createSQLQuery(queryString.toString());
			query.addEntity(LocationDetails.class);

			return query.list();

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				this.getLogExceptionMessage("getFilteredLocationsDetails", "", null, e.getMessage(), LocationDAO.CLASS_NAME_LOCATION), e);
		}
	}

	public long countLocations(final LocationSearchRequest locationSearchRequest) {
		final StringBuilder sql = new StringBuilder(" SELECT COUNT(l.locid) ");
		this.appendGetLocationFromQuery(sql);
		this.appendLocationSearchFilter(sql, locationSearchRequest);
		final SQLQuery sqlQuery = this.getSession().createSQLQuery(sql.toString());
		this.addLocationSearchFilterParameters(sqlQuery, locationSearchRequest);
		return ((BigInteger) sqlQuery.uniqueResult()).longValue();
	}

	public List<org.generationcp.middleware.api.location.Location> getLocations(final LocationSearchRequest locationSearchRequest, final Pageable pageable) {

		final SQLQuery sqlQuery =
			this.getSession().createSQLQuery(this.createGetLocationsQuery(locationSearchRequest));
		sqlQuery.addScalar("locationDbId");
		sqlQuery.addScalar("locationType");
		sqlQuery.addScalar("locationName");
		sqlQuery.addScalar("abbreviation");
		sqlQuery.addScalar("countryCode");
		sqlQuery.addScalar("countryName");
		sqlQuery.addScalar("latitude");
		sqlQuery.addScalar("longitude");
		sqlQuery.addScalar("altitude");
		sqlQuery.addScalar("province");

		this.addLocationSearchFilterParameters(sqlQuery, locationSearchRequest);
		addPaginationToSQLQuery(sqlQuery, pageable);
		sqlQuery.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
		final List<Map<String, Object>> results = sqlQuery.list();
		final List<org.generationcp.middleware.api.location.Location> locations = new ArrayList<>();
		for (final Map<String, Object> result : results) {
			final org.generationcp.middleware.api.location.Location location = new org.generationcp.middleware.api.location.Location();
			location.setLocationDbId(String.valueOf(result.get("locationDbId")));
			location.setLocationType(String.valueOf(result.get("locationType")));
			location.setLocationName(String.valueOf(result.get("locationName")));
			location.setAbbreviation(String.valueOf(result.get("abbreviation")));
			location.setCountryCode(String.valueOf(result.get("countryCode")));
			location.setCountryName(String.valueOf(result.get("countryName")));
			location.setName(String.valueOf(result.get("locationName")));
			final Geometry geometry = new Geometry(
				Arrays.asList((Double) result.get("latitude"), (Double)result.get("longitude"), (Double)result.get("altitude")),
				"Point");
			final Coordinate coordinate = new Coordinate(geometry, "Feature");
			location.setCoordinates(coordinate);
			location.setAltitude((Double) result.get("latitude"));
			location.setLongitude((Double) result.get("longitude"));
			location.setLongitude((Double) result.get("altitude"));
			if (!location.getLocationType().equalsIgnoreCase(LocationDAO.COUNTRY)) {
				final Map<String, String> additionalInfo = new HashMap<>();
				additionalInfo.put("province", String.valueOf(result.get("province")));
				location.setAdditionalInfo(additionalInfo);
			}

			locations.add(location);
		}
		return locations;
	}

	private void addLocationSearchFilterParameters(final SQLQuery sqlQuery, final LocationSearchRequest locationSearchRequest) {
		if(!StringUtils.isEmpty(locationSearchRequest.getLocationType())) {
			sqlQuery.setParameter("locationType", locationSearchRequest.getLocationType());
		}
		if(!StringUtils.isEmpty(locationSearchRequest.getLocationId())) {
			sqlQuery.setParameter("locationId", locationSearchRequest.getLocationId());
		}
	}

	private String createGetLocationsQuery(final LocationSearchRequest locationSearchRequest) {
		final StringBuilder queryString = new StringBuilder();
		queryString.append("SELECT l.locid AS locationDbId, ");
		queryString.append("ud.fname AS locationType, ");
		queryString.append("l.lname AS locationName, ");
		queryString.append("l.labbr AS abbreviation, ");
		queryString.append("c.isothree AS countryCode, ");
		queryString.append("c.isoabbr AS countryName, ");
		queryString.append("g.lat AS latitude, ");
		queryString.append("g.lon AS longitude, ");
		queryString.append("g.alt AS altitude, ");
		queryString.append("province.lname AS province ");
		this.appendGetLocationFromQuery(queryString);
		this.appendLocationSearchFilter(queryString, locationSearchRequest);
		queryString.append(" ORDER BY l.locid ");
		return queryString.toString();
	}

	private void appendGetLocationFromQuery(final StringBuilder queryString) {
		queryString.append("FROM location l ");
		queryString.append(" LEFT JOIN georef g on l.locid = g.locid ");
		queryString.append(" LEFT JOIN cntry c on l.cntryid = c.cntryid ");
		queryString.append(" LEFT JOIN udflds ud on ud.fldno = l.ltype ");
		queryString.append(" LEFT JOIN location province ON  province.locid = l.snl1id ");
	}

	private void appendLocationSearchFilter(final StringBuilder queryString, final LocationSearchRequest locationSearchRequest) {
		queryString.append("WHERE 1=1 ");
		if(!StringUtils.isEmpty(locationSearchRequest.getLocationType())) {
			queryString.append("AND ud.fname = :locationType ");
		}
		if(!StringUtils.isEmpty(locationSearchRequest.getLocationId())) {
			queryString.append("AND l.locid = :locationId ");
		}
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
}
