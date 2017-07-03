/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.domain.dms.LocationDto;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Georef;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.LocationDetails;
import org.generationcp.middleware.pojos.Locdes;
import org.generationcp.middleware.service.api.location.AdditionalInfoDto;
import org.generationcp.middleware.service.api.location.LocationDetailsDto;
import org.generationcp.middleware.service.api.location.LocationFilters;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DAO class for {@link Location}.
 */
@SuppressWarnings("unchecked")
public class LocationDAO extends GenericDAO<Location, Integer> {

	private static final String UNIQUE_ID = "uniqueID";
	private static final String CLASS_NAME_LOCATION = "Location";
	private static final String LOCATION = "location";
	private static final String COUNTRY_ID = "cntryid";
	private static final String COUNTRY = "country";
	private static final String GET_BY_TYPE = "getByType";
	private static final String GET_BY_COUNTRY = "getByCountry";
	private static final String LNAME = "lname";
	private static final String LOCID = "locid";
	private static final String LTYPE = "ltype";
	private static final String NAME_OR_OPERATION = "name|operation";

	private static final Logger LOG = LoggerFactory.getLogger(LocationDAO.class);

	public List<Location> getByName(final String name, final Operation operation) throws MiddlewareQueryException {
		try {
			final Criteria criteria = this.getSession().createCriteria(Location.class);
			this.addNameSearchCriteria(name, operation, criteria);
			return criteria.list();
		} catch (final HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage("getByName", LocationDAO.NAME_OR_OPERATION,
					name + "|" + operation, e.getMessage(), LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return new ArrayList<Location>();
	}

	private void addNameSearchCriteria(final String name, final Operation operation, final Criteria criteria) {
		if (operation == null || operation == Operation.EQUAL) {
			criteria.add(Restrictions.eq(LocationDAO.LNAME, name));
		} else if (operation == Operation.LIKE) {
			criteria.add(Restrictions.like(LocationDAO.LNAME, name, MatchMode.START));
		}
	}

	public List<Location> getByNameAndUniqueID(final String name, final Operation operation, final String programUUID)
			throws MiddlewareQueryException {
		try {
			final Criteria criteria = this.getSession().createCriteria(Location.class);

			this.addNameSearchCriteria(name, operation, criteria);
			criteria.add(Restrictions.or(Restrictions.eq(LocationDAO.UNIQUE_ID, programUUID),
					Restrictions.isNull(LocationDAO.UNIQUE_ID)));
			return criteria.list();
		} catch (final HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage("getByName", "name|operation", name + "|" + operation,
					e.getMessage(), "Location"), e);
		}
		return new ArrayList<Location>();
	}

	public List<Location> getByName(final String name, final Operation operation, final int start, final int numOfRows)
			throws MiddlewareQueryException {
		try {
			final Criteria criteria = this.getSession().createCriteria(Location.class);

			this.addNameSearchCriteria(name, operation, criteria);

			criteria.setFirstResult(start);
			criteria.setMaxResults(numOfRows);
			return criteria.list();
		} catch (final HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage("getByName", LocationDAO.NAME_OR_OPERATION,
					name + "|" + operation, e.getMessage(), LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return new ArrayList<Location>();
	}

	public List<Location> getByNameAndUniqueID(final String name, final Operation operation, final String programUUID,
			final int start, final int numOfRows) throws MiddlewareQueryException {
		try {
			final Criteria criteria = this.getSession().createCriteria(Location.class);

			this.addNameSearchCriteria(name, operation, criteria);
			criteria.add(Restrictions.or(Restrictions.eq(LocationDAO.UNIQUE_ID, programUUID),
					Restrictions.isNull(LocationDAO.UNIQUE_ID)));
			criteria.setFirstResult(start);
			criteria.setMaxResults(numOfRows);
			return criteria.list();
		} catch (final HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage("getByName", "name|operation", name + "|" + operation,
					e.getMessage(), "Location"), e);
		}
		return new ArrayList<Location>();
	}

	public long countByName(final String name, final Operation operation) throws MiddlewareQueryException {
		try {
			if (name != null) {
				final Criteria criteria = this.getSession().createCriteria(Location.class);
				criteria.setProjection(Projections.rowCount());

				this.addNameSearchCriteria(name, operation, criteria);

				return ((Long) criteria.uniqueResult()).longValue();
			}
		} catch (final HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage("countByName", LocationDAO.NAME_OR_OPERATION,
					name + "|" + operation, e.getMessage(), LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return 0;
	}

	public long countByNameAndUniqueID(final String name, final Operation operation, final String programUUID)
			throws MiddlewareQueryException {
		try {
			if (name != null) {
				final Criteria criteria = this.getSession().createCriteria(Location.class);
				criteria.setProjection(Projections.rowCount());

				this.addNameSearchCriteria(name, operation, criteria);
				criteria.add(Restrictions.or(Restrictions.eq(LocationDAO.UNIQUE_ID, programUUID),
						Restrictions.isNull(LocationDAO.UNIQUE_ID)));
				return ((Long) criteria.uniqueResult()).longValue();
			}
		} catch (final HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage("countByName", "name|operation|programUUID",
					name + "|" + operation + "|" + programUUID, e.getMessage(), "Location"), e);
		}
		return 0;
	}

	public List<Location> getByCountry(final Country country) throws MiddlewareQueryException {
		try {
			if (country != null) {
				final Integer countryId = country.getCntryid();
				final Criteria criteria = this.getSession().createCriteria(Location.class);
				criteria.add(Restrictions.eq(LocationDAO.COUNTRY_ID, countryId));
				criteria.addOrder(Order.asc(LocationDAO.LNAME));
				return criteria.list();
			}
		} catch (final HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage(LocationDAO.GET_BY_COUNTRY, LocationDAO.COUNTRY,
					country.toString(), e.getMessage(), LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return new ArrayList<Location>();
	}

	public List<Location> getByCountryAndType(final Country country, final Integer type)
			throws MiddlewareQueryException {
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
			this.logAndThrowException(this.getLogExceptionMessage(LocationDAO.GET_BY_COUNTRY, LocationDAO.COUNTRY,
					country.toString(), e.getMessage(), LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return new ArrayList<Location>();
	}

	public List<Location> getByNameCountryAndType(final String name, final Country country, final Integer type)
			throws MiddlewareQueryException {
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
			this.logAndThrowException(this.getLogExceptionMessage(LocationDAO.GET_BY_COUNTRY, LocationDAO.COUNTRY,
					country.toString(), e.getMessage(), LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return new ArrayList<Location>();
	}

	public List<Location> getByCountry(final Country country, final int start, final int numOfRows)
			throws MiddlewareQueryException {
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
			this.logAndThrowException(this.getLogExceptionMessage(LocationDAO.GET_BY_COUNTRY, LocationDAO.COUNTRY,
					country.toString(), e.getMessage(), LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return new ArrayList<Location>();
	}

	public long countByCountry(final Country country) throws MiddlewareQueryException {
		try {
			if (country != null) {
				final Integer countryId = country.getCntryid();
				final Criteria criteria = this.getSession().createCriteria(Location.class);
				criteria.add(Restrictions.eq(LocationDAO.COUNTRY_ID, countryId));
				criteria.setProjection(Projections.rowCount());
				return ((Long) criteria.uniqueResult()).longValue();
			}
		} catch (final HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage(LocationDAO.GET_BY_COUNTRY, LocationDAO.COUNTRY,
					country.toString(), e.getMessage(), LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return 0;
	}

	public List<Location> getByType(final Integer type) throws MiddlewareQueryException {
		try {
			if (type != null) {
				final Criteria criteria = this.getSession().createCriteria(Location.class);
				criteria.add(Restrictions.eq(LocationDAO.LTYPE, type));
				criteria.addOrder(Order.asc(LocationDAO.LNAME));
				return criteria.list();
			}
		} catch (final HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage(LocationDAO.GET_BY_TYPE, "type", String.valueOf(type),
					e.getMessage(), LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return new ArrayList<Location>();
	}

	public List<Location> getByType(final Integer type, final String programUUID) throws MiddlewareQueryException {
		try {
			if (type != null) {
				final Criteria criteria = this.getSession().createCriteria(Location.class);
				criteria.add(Restrictions.eq(LocationDAO.LTYPE, type));
				criteria.add(Restrictions.or(Restrictions.eq(LocationDAO.UNIQUE_ID, programUUID),
						Restrictions.isNull(LocationDAO.UNIQUE_ID)));
				criteria.addOrder(Order.asc(LocationDAO.LNAME));
				return criteria.list();
			}
		} catch (final HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage(LocationDAO.GET_BY_TYPE, "type", String.valueOf(type),
					e.getMessage(), LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return new ArrayList<Location>();
	}

	public List<Location> getByType(final Integer type, final int start, final int numOfRows)
			throws MiddlewareQueryException {
		try {
			if (type != null) {
				final Criteria criteria = this.getSession().createCriteria(Location.class);
				criteria.add(Restrictions.eq(LocationDAO.LTYPE, type));
				criteria.setFirstResult(start);
				criteria.setMaxResults(numOfRows);
				return criteria.list();
			}
		} catch (final HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage(LocationDAO.GET_BY_TYPE, "type", String.valueOf(type),
					e.getMessage(), LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return new ArrayList<Location>();
	}

	public long countByType(final Integer type) throws MiddlewareQueryException {
		try {
			if (type != null) {
				final Criteria criteria = this.getSession().createCriteria(Location.class);
				criteria.add(Restrictions.eq(LocationDAO.LTYPE, type));
				criteria.setProjection(Projections.rowCount());
				return ((Long) criteria.uniqueResult()).longValue();
			}
		} catch (final HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage("countByType", "type", String.valueOf(type),
					e.getMessage(), LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return 0;
	}

	public long countByType(final Integer type, final String programUUID) throws MiddlewareQueryException {
		try {
			if (type != null) {
				final Criteria criteria = this.getSession().createCriteria(Location.class);
				criteria.add(Restrictions.eq(LocationDAO.LTYPE, type));
				criteria.add(Restrictions.or(Restrictions.eq(LocationDAO.UNIQUE_ID, programUUID),
						Restrictions.isNull(LocationDAO.UNIQUE_ID)));
				criteria.setProjection(Projections.rowCount());
				return ((Long) criteria.uniqueResult()).longValue();
			}
		} catch (final HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage("countByType", "type", String.valueOf(type),
					e.getMessage(), LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return 0;
	}

	@SuppressWarnings("rawtypes")
	public List<Location> getAllBreedingLocations() throws MiddlewareQueryException {
		final List<Location> locationList = new ArrayList<Location>();
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

					final Location location = new Location(locid, ltype, nllp, lname, labbr, snl3id, snl2id, snl1id,
							cntryid, lrplce);
					location.setUniqueID(programUUID);

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
			this.logAndThrowException(this.getLogExceptionMessage("getAllBreedingLocations", "", null, e.getMessage(),
					"GermplasmDataManager"), e);
			return new ArrayList<Location>();
		}
	}

	@SuppressWarnings("deprecation")
	public Long countAllBreedingLocations() throws MiddlewareQueryException {
		try {
			final Session session = this.getSession();
			final SQLQuery query = session.createSQLQuery(Location.COUNT_ALL_BREEDING_LOCATIONS);
			return (Long) query.addScalar("count", Hibernate.LONG).uniqueResult();
		} catch (final HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage("countAllBreedingLocations", "", null, e.getMessage(),
					LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return Long.valueOf(0);
	}

	public List<LocationDetails> getLocationDetails(final Integer locationId, final Integer start,
			final Integer numOfRows) throws MiddlewareQueryException {
		try {
			final StringBuilder queryString = new StringBuilder();
			queryString.append("select l.lname as location_name,l.locid,l.ltype as ltype,");
			queryString.append(" g.lat as latitude, g.lon as longitude, g.alt as altitude,");
			queryString
					.append(" c.cntryid as cntryid, c.isofull as country_full_name, labbr as location_abbreviation,");
			queryString.append(" ud.fname as location_type,");
			queryString.append(" ud.fdesc as location_description");
			queryString.append(" from location l");
			queryString.append(" left join georef g on l.locid = g.locid");
			queryString.append(" left join cntry c on l.cntryid = c.cntryid");
			queryString.append(" left join udflds ud on ud.fldno = l.ltype");

			if (locationId != null) {
				queryString.append(" where l.locid = :id");

				final SQLQuery query = this.getSession().createSQLQuery(queryString.toString());
				query.setParameter("id", locationId);
				query.setFirstResult(start);
				query.setMaxResults(numOfRows);
				query.addEntity(LocationDetails.class);

				return query.list();
			}

		} catch (final HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage("getLocationDetails", "id",
					String.valueOf(locationId), e.getMessage(), LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return new ArrayList<LocationDetails>();
	}

	@Override
	public Location saveOrUpdate(final Location location) throws MiddlewareQueryException {
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

	public List<LocationDetails> getLocationDetails(final List<Integer> locationId, final Integer start,
			final Integer numOfRows) throws MiddlewareQueryException {
		try {
			final StringBuilder queryString = new StringBuilder();
			queryString.append("select l.lname as location_name,l.locid,l.ltype as ltype, c.cntryid as cntryid,");
			queryString.append(" c.isofull as country_full_name, labbr as location_abbreviation,");
			queryString.append(" g.lat as latitude, g.lon as longitude, g.alt as altitude,");
			queryString.append(" ud.fname as location_type,");
			queryString.append(" ud.fdesc as location_description");
			queryString.append(" from location l");
			queryString.append(" left join georef g on l.locid = g.locid");
			queryString.append(" left join cntry c on l.cntryid = c.cntryid");
			queryString.append(" left join udflds ud on ud.fldno = l.ltype");

			if (locationId != null) {

				queryString.append(" where l.locid = :id");

				final SQLQuery query = this.getSession().createSQLQuery(queryString.toString());
				query.setParameterList("id", locationId);
				query.setFirstResult(start);
				query.setMaxResults(numOfRows);
				query.addEntity(LocationDetails.class);

				return query.list();

			}
		} catch (final HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage("getLocationDetails", "id",
					String.valueOf(locationId), e.getMessage(), LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return new ArrayList<LocationDetails>();
	}

	public List<Location> getAllProvincesByCountry(final Integer countryId) throws MiddlewareQueryException {
		if (countryId == null || countryId == 0) {
			return new ArrayList<Location>();
		}

		try {
			final SQLQuery query = this.getSession().createSQLQuery(Location.GET_PROVINCE_BY_COUNTRY);
			query.addEntity(Location.class);
			query.setParameter("countryId", countryId);
			return query.list();
		} catch (final HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage("getAllProvinces", "", null, e.getMessage(),
					LocationDAO.CLASS_NAME_LOCATION), e);
			return new ArrayList<Location>();
		}
	}

	public List<Location> getAllProvinces() throws MiddlewareQueryException {

		try {
			final SQLQuery query = this.getSession().createSQLQuery(Location.GET_ALL_PROVINCES);
			query.addEntity(Location.class);
			return query.list();
		} catch (final HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage("getAllProvinces", "", null, e.getMessage(),
					LocationDAO.CLASS_NAME_LOCATION), e);
			return new ArrayList<Location>();
		}
	}

	public List<LocationDto> getLocationDtoByIds(final Collection<Integer> ids) throws MiddlewareQueryException {
		final List<LocationDto> returnList = new ArrayList<LocationDto>();
		if (ids == null || ids.isEmpty()) {
			return returnList;
		}
		try {
			final String sql = "SELECT l.lname, prov.lname, c.isoabbr, l.locid" + " FROM location l"
					+ " LEFT JOIN location prov ON prov.locid = l.snl1id"
					+ " LEFT JOIN cntry c ON c.cntryid = l.cntryid" + " WHERE l.locid in (:ids)";
			final SQLQuery query = this.getSession().createSQLQuery(sql);
			query.setParameterList("ids", ids);
			final List<Object[]> results = query.list();

			if (results != null) {
				for (final Object[] result : results) {
					returnList.add(new LocationDto((Integer) result[3], (String) result[0], (String) result[1],
							(String) result[2]));
				}
			}

		} catch (final HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage("getLocationDtoById", "id", ids.toString(),
					e.getMessage(), LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return returnList;
	}

	public List<Location> getLocationByIds(final Collection<Integer> ids) throws MiddlewareQueryException {

		if (ids == null || ids.isEmpty()) {
			return new ArrayList<>();
		}

		try {
			return this.getSession().createCriteria(Location.class).add(Restrictions.in(LocationDAO.LOCID, ids))
					.addOrder(Order.asc(LocationDAO.LNAME)).list();
		} catch (final HibernateException e) {
			this.logAndThrowException(String.format("Error with getLocationByIds(id=[%s])", StringUtils.join(ids, ",")),
					e);
		}

		return new ArrayList<>();
	}

	public Map<Integer, String> getLocationNamesByLocationIDs(final List<Integer> locIds)
			throws MiddlewareQueryException {
		final Map<Integer, String> toreturn = new HashMap<Integer, String>();

		final List<Location> locations = this.getLocationByIds(locIds);
		for (final Location location : locations) {
			toreturn.put(location.getLocid(), location.getLname());
		}

		return toreturn;
	}

	public Map<Integer, String> getLocationNamesMapByGIDs(final List<Integer> gids) throws MiddlewareQueryException {
		final Map<Integer, String> toreturn = new HashMap<Integer, String>();
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
			this.logAndThrowException(this.getLogExceptionMessage("getLocationNamesMapByGIDs", "gids", gids.toString(),
					e.getMessage(), LocationDAO.CLASS_NAME_LOCATION), e);
		}

		return toreturn;
	}

	public Map<Integer, LocationDto> getLocationNamesByGIDs(final List<Integer> gids) throws MiddlewareQueryException {
		final Map<Integer, LocationDto> toreturn = new HashMap<Integer, LocationDto>();
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
				final Integer locid = (Integer) resultArray[1];
				final String locationName = (String) resultArray[2];
				toreturn.put(gid, new LocationDto(locid, locationName));
			}
		} catch (final HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage("getLocationNamesByGIDs", "gids", gids.toString(),
					e.getMessage(), LocationDAO.CLASS_NAME_LOCATION), e);
		}

		return toreturn;
	}

	public List<Location> getLocationsByDTypeAndLType(final String dval, final Integer dType, final Integer lType)
			throws MiddlewareQueryException {
		final List<Location> locations = new ArrayList<Location>();
		try {
			final StringBuilder sqlString = new StringBuilder()
					.append("SELECT  l.locid, l.ltype, l.nllp, l.lname, l.labbr")
					.append(", l.snl3id, l.snl2id, l.snl1id, l.cntryid, l.lrplce ")
					.append("FROM locdes ld INNER JOIN location l ").append(" ON l.locid = ld.locid ")
					.append("WHERE dtype = :dtype  AND ltype = :ltype AND dval = :dval ");

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

					locations.add(
							new Location(locid, ltype, nllp, lname, labbr, snl3id, snl2id, snl1id, cntryid, lrplce));

				}
			}

		} catch (final HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage("getLocationsByDTypeAndLType", "dType|lType",
					dType + "|" + lType, e.getMessage(), "Locdes"), e);
		}
		return locations;
	}

	public List<Location> getByTypeWithParent(final Integer type, final Integer relationshipType)
			throws MiddlewareQueryException {
		final List<Location> locationList = new ArrayList<Location>();
		try {
			final Session session = this.getSession();
			final String sql = "SELECT f.locid, f.lname, fd.dval " + " FROM location f "
					+ " INNER JOIN locdes fd ON fd.locid = f.locid AND fd.dtype = " + relationshipType
					+ " WHERE f.ltype = " + type;
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
					location.setParentLocationId(
							parentId != null && NumberUtils.isNumber(parentId) ? Integer.valueOf(parentId) : null);
					locationList.add(location);
				}
			}
		} catch (final HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage("getByTypeWithParent", "type", type.toString(),
					e.getMessage(), LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return locationList;
	}

	public Map<Integer, Location> getNamesByIdsIntoMap(final Collection<Integer> ids) throws MiddlewareQueryException {
		final Map<Integer, Location> map = new HashMap<Integer, Location>();
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
			this.logAndThrowException(this.getLogExceptionMessage("getNamesByIdsIntoMap", "", null, e.getMessage(),
					LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return map;
	}

	public List<Location> getByIds(final List<Integer> ids) throws MiddlewareQueryException {
		List<Location> locations = new ArrayList<Location>();

		if (ids == null || ids.isEmpty()) {
			return locations;
		}

		try {
			final Criteria criteria = this.getSession().createCriteria(Location.class);
			criteria.add(Restrictions.in(LocationDAO.LOCID, ids));
			locations = criteria.list();
		} catch (final HibernateException e) {
			this.logAndThrowException(
					this.getLogExceptionMessage("getByIds", "", null, e.getMessage(), LocationDAO.CLASS_NAME_LOCATION),
					e);
		}
		return locations;
	}

	public List<Location> getByUniqueID(final String programUUID) throws MiddlewareQueryException {
		List<Location> locations = new ArrayList<Location>();

		if (programUUID == null || programUUID.isEmpty()) {
			return locations;
		}

		try {
			final Criteria criteria = this.getSession().createCriteria(Location.class);
			criteria.add(Restrictions.or(Restrictions.eq(LocationDAO.UNIQUE_ID, programUUID),
					Restrictions.isNull(LocationDAO.UNIQUE_ID)));
			locations = criteria.list();
		} catch (final HibernateException e) {
			this.logAndThrowException(
					this.getLogExceptionMessage("getByIds", "", null, e.getMessage(), LocationDAO.CLASS_NAME_LOCATION),
					e);
		}
		return locations;
	}

	public long countByUniqueID(final String programUUID) throws MiddlewareQueryException {
		try {
			if (programUUID != null) {
				final Criteria criteria = this.getSession().createCriteria(Location.class);
				criteria.add(Restrictions.or(Restrictions.eq(LocationDAO.UNIQUE_ID, programUUID),
						Restrictions.isNull(LocationDAO.UNIQUE_ID)));
				criteria.setProjection(Projections.rowCount());
				return ((Long) criteria.uniqueResult()).longValue();
			}
		} catch (final HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage("countByUniqueID", "uniqueID", programUUID,
					e.getMessage(), LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return 0;
	}

	public List<Location> getProgramLocations(final String programUUID) throws MiddlewareQueryException {
		List<Location> locations = new ArrayList<Location>();
		try {
			final Criteria criteria = this.getSession().createCriteria(Location.class);
			criteria.add(Restrictions.eq(LocationDAO.UNIQUE_ID, programUUID));
			locations = criteria.list();
		} catch (final HibernateException e) {
			this.logAndThrowException(
					"Error in getProgramLocations(" + programUUID + ") in LocationDao: " + e.getMessage(), e);
		}
		return locations;
	}

	public List<Locdes> getLocdesByLocId(final Integer locationId) throws MiddlewareQueryException {
		try {
			final Criteria criteria = this.getSession().createCriteria(Locdes.class);
			criteria.add(Restrictions.eq("locationId", locationId));
			return criteria.list();
		} catch (final HibernateException e) {
			this.logAndThrowException("Error with getByIds() query from Location: " + e.getMessage(), e);
		}
		return new ArrayList<Locdes>();
	}

	public List<Location> getBreedingLocations(final List<Integer> ids) throws MiddlewareQueryException {
		try {
			final List<Integer> validCodes = new ArrayList<Integer>();
			// 410, 411, 412
			validCodes.add(410);
			validCodes.add(411);
			validCodes.add(412);

			final Criteria criteria = this.getSession().createCriteria(Location.class);
			if (ids.size() > 0) {
				criteria.add(Restrictions.in("locid", ids));
			}
			criteria.add(Restrictions.in("ltype", validCodes));
			criteria.addOrder(Order.asc("lname"));

			return criteria.list();
		} catch (final HibernateException e) {
			LocationDAO.LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException(
					this.getLogExceptionMessage("getBreedingLocations", "", null, e.getMessage(), "Location"), e);
		}
	}

	public List<Location> getSeedingLocations(final List<Integer> ids, final Integer seedLType)
			throws MiddlewareQueryException {
		try {

			final Criteria criteria = this.getSession().createCriteria(Location.class);
			if (ids.size() > 0) {
				criteria.add(Restrictions.in("locid", ids));
			}
			criteria.add(Restrictions.eq("ltype", seedLType));
			criteria.addOrder(Order.asc("lname"));

			return criteria.list();
		} catch (final HibernateException e) {
			LocationDAO.LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException(
					this.getLogExceptionMessage("getSeedingLocations", "", null, e.getMessage(), "Location"), e);
		}
	}

	public List<Location> getBreedingLocationsByUniqueID(final String programUUID) {
		List<Location> locations = new ArrayList<Location>();

		if (programUUID == null || programUUID.isEmpty()) {
			return locations;
		}

		try {
			final Criteria criteria = this.getSession().createCriteria(Location.class);
			// filter by programUUID plus return also records with null
			// programUUID (common historical data)
			criteria.add(Restrictions.or(Restrictions.eq(LocationDAO.UNIQUE_ID, programUUID),
					Restrictions.isNull(LocationDAO.UNIQUE_ID)));
			// set location types for Breeding Location
			criteria.add(Restrictions.in(LocationDAO.LTYPE, Arrays.asList(Location.BREEDING_LOCATION_TYPE_IDS)));
			criteria.addOrder(Order.asc("lname"));

			locations = criteria.list();
		} catch (final HibernateException e) {
			LocationDAO.LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException(
					this.getLogExceptionMessage("getBreedingLocationsByUniqueID", "", null, e.getMessage(), "Location"),
					e);

		}
		return locations;

	}

	public List<LocationDetails> getFilteredLocations(final Integer countryId, final Integer locationType, final String locationName,
			final String programUUID) throws MiddlewareQueryException {
		try {

			final StringBuilder queryString = new StringBuilder();

			queryString.append("SELECT l.lname as location_name,l.locid,l.ltype as ltype,")
					.append(" g.lat as latitude, g.lon as longitude, g.alt as altitude,")
					.append(" c.cntryid as cntryid, c.isofull as country_full_name, labbr as location_abbreviation,")
					.append(" ud.fname as location_type,").append(" ud.fdesc as location_description").append(" FROM location l")
					.append(" LEFT JOIN georef g on l.locid = g.locid").append(" LEFT JOIN cntry c on l.cntryid = c.cntryid")
					.append(" LEFT JOIN udflds ud on ud.fldno = l.ltype").append(" WHERE (l.program_uuid = '").append(programUUID)
					.append("'").append(" or l.program_uuid is null) ");

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
			this.logAndThrowException(
					this.getLogExceptionMessage("getFilteredLocations", "", null, e.getMessage(), LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return new ArrayList<LocationDetails>();
	}


	public long countLocationsByFilter(final Map<LocationFilters, Object> filters) {

		try {
			final StringBuilder sqlString = new StringBuilder();

			sqlString.append("SELECT l.locid ") //
					.append(" FROM location l ") //
					.append(" LEFT JOIN georef g on g.locid = l.locid ") //
					.append(" LEFT JOIN cntry c on c.cntryid = l.cntryid ") //
					.append(" LEFT JOIN udflds ud on ud.fldno = l.ltype ");

			if (!filters.isEmpty()) {
				sqlString.append(createConditionWhereByFilter(filters));

			}
			final SQLQuery query = this.getSession().createSQLQuery(sqlString.toString());
			
			for (Map.Entry<LocationFilters, Object> entry : filters.entrySet()) {
				LocationFilters filter = entry.getKey();
				Object value = entry.getValue();
				if (value.getClass().isArray()) {
					query.setParameterList(filter.getParameter(), (Object[])value);
				}else{
					query.setParameter(filter.getParameter(), value);
				}
			}
			
			return query.list().size();
		} catch (final HibernateException e) {
			LocationDAO.LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException(
					this.getLogExceptionMessage("countLocationsByFilter", "", null, e.getMessage(), LocationDAO.CLASS_NAME_LOCATION), e);
		}
	}

	public List<LocationDetailsDto> getLocationsByFilter(final int pageNumber,final int pageSize,
			final Map<LocationFilters, Object> filters) {
		final List<LocationDetailsDto> locationList = new ArrayList<LocationDetailsDto>();

		try {

			final StringBuilder sqlString = new StringBuilder();

			sqlString.append("SELECT l.locid ,ud.fname ,l.lname ,l.labbr ,c.isothree ,c.isoabbr ,g.lat ,g.lon ,g.alt ,province.lname as province") //
					.append(" FROM location l ") //
					.append(" LEFT JOIN georef g on l.locid = g.locid ") //
					.append(" LEFT JOIN cntry c on l.cntryid = c.cntryid ") //
					.append(" LEFT JOIN udflds ud on ud.fldno = l.ltype, ")
					.append(" location province");

			if (!filters.isEmpty()) {
				sqlString.append(createConditionWhereByFilter(filters));

			}
			sqlString.append(" and province.locid = l.snl3id ");
			sqlString.append(" ORDER BY l.locid ");

			final SQLQuery query = this.getSession().createSQLQuery(sqlString.toString()).addScalar("l.locid").addScalar("ud.fname").addScalar("l.lname").addScalar("l.labbr").addScalar("c.isothree").addScalar("c.isoabbr").addScalar("g.lat").addScalar("g.lon").addScalar("g.alt").addScalar("province");
			int start = pageSize * (pageNumber - 1);
			int numOfRows = pageSize;
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);

			for (Map.Entry<LocationFilters, Object> entry : filters.entrySet()) {
				LocationFilters filter = entry.getKey();
				Object value = entry.getValue();
				if (value.getClass().isArray()) {
					query.setParameterList(filter.getParameter(), (Object[])value);
				}else{
					query.setParameter(filter.getParameter(), value);
				}
			}
			
			final List<Object[]> results = query.list();

			if (!results.isEmpty()) {
				for (final Object[] row : results) {
					final Integer locationDbId = (Integer) row[0];
					final String locationType = (String) row[1];
					final String name = (String) row[2];
					final String abbreviation = (String) row[3];
					final String countryCode = (String) row[4];
					final String countryName = (String) row[5];
					final Double latitude = (Double) row[6];
					final Double longitude = (Double) row[7];
					final Double altitude = (Double) row[8];

					final LocationDetailsDto locationDetailsDto =
						new LocationDetailsDto(locationDbId, locationType, name, abbreviation, countryCode, countryName, latitude,
						longitude, altitude);
					if (!locationType.equalsIgnoreCase("COUNTRY")) {
						AdditionalInfoDto additionalInfoDto = new AdditionalInfoDto(locationDetailsDto.getLocationDbId());
						additionalInfoDto.addInfo("province", (String) row[9]);
						locationDetailsDto.setMapAdditionalInfo(additionalInfoDto);
					}
					locationList.add(locationDetailsDto);
				}
			}

			return locationList;

		} catch (final HibernateException e) {
			LocationDAO.LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException(
					this.getLogExceptionMessage("getLocalLocationsByFilter", "", null, e.getMessage(), LocationDAO.CLASS_NAME_LOCATION), e);
		}
	}

	private String createConditionWhereByFilter(final Map<LocationFilters, Object> filters) {
		final StringBuilder sqlString = new StringBuilder();
		sqlString.append(" WHERE 1 = 1");

		for (Map.Entry<LocationFilters, Object> entry : filters.entrySet()) {
			LocationFilters filter = entry.getKey();
			Object value = entry.getValue();

			sqlString.append(" AND ");

			if (value.getClass().isArray()) {
				sqlString.append(filter.getStatement()).append("in (:").append(filter.getParameter()).append(") ");

			} else {
				sqlString.append(filter.getStatement()).append("= :").append(filter.getParameter()).append(" ");
			}
		}
		return sqlString.toString();

	}
}
