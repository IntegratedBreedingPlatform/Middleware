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
	private static final String COUNTRY_ID = "cntryid";
	private static final String COUNTRY = "country";
	private static final String GET_BY_TYPE = "getByType";
	private static final String GET_BY_COUNTRY = "getByCountry";
	private static final String LNAME = "lname";
	private static final String LOCID = "locid";
	private static final String LTYPE = "ltype";
	private static final String NAME_OR_OPERATION = "name|operation";

	private static final Logger LOG = LoggerFactory.getLogger(LocationDAO.class);

	public List<Location> getByName(String name, Operation operation) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(Location.class);
			this.addNameSearchCriteria(name, operation, criteria);
			return criteria.list();
		} catch (HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage("getByName", LocationDAO.NAME_OR_OPERATION, name + "|" + operation,
					e.getMessage(), LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return new ArrayList<Location>();
	}

	private void addNameSearchCriteria(String name, Operation operation, Criteria criteria) {
		if (operation == null || operation == Operation.EQUAL) {
			criteria.add(Restrictions.eq(LocationDAO.LNAME, name));
		} else if (operation == Operation.LIKE) {
			criteria.add(Restrictions.like(LocationDAO.LNAME, name, MatchMode.START));
		}
	}

	public List<Location> getByNameAndUniqueID(String name, Operation operation, String programUUID) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(Location.class);

			this.addNameSearchCriteria(name, operation, criteria);
			criteria.add(Restrictions.or(Restrictions.eq(LocationDAO.UNIQUE_ID, programUUID), Restrictions.isNull(LocationDAO.UNIQUE_ID)));
			return criteria.list();
		} catch (HibernateException e) {
			this.logAndThrowException(
					this.getLogExceptionMessage("getByName", "name|operation", name + "|" + operation, e.getMessage(), "Location"), e);
		}
		return new ArrayList<Location>();
	}

	public List<Location> getByName(String name, Operation operation, int start, int numOfRows) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(Location.class);

			this.addNameSearchCriteria(name, operation, criteria);

			criteria.setFirstResult(start);
			criteria.setMaxResults(numOfRows);
			return criteria.list();
		} catch (HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage("getByName", LocationDAO.NAME_OR_OPERATION, name + "|" + operation,
					e.getMessage(), LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return new ArrayList<Location>();
	}

	public List<Location> getByNameAndUniqueID(String name, Operation operation, String programUUID, int start, int numOfRows)
			throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(Location.class);

			this.addNameSearchCriteria(name, operation, criteria);
			criteria.add(Restrictions.or(Restrictions.eq(LocationDAO.UNIQUE_ID, programUUID), Restrictions.isNull(LocationDAO.UNIQUE_ID)));
			criteria.setFirstResult(start);
			criteria.setMaxResults(numOfRows);
			return criteria.list();
		} catch (HibernateException e) {
			this.logAndThrowException(
					this.getLogExceptionMessage("getByName", "name|operation", name + "|" + operation, e.getMessage(), "Location"), e);
		}
		return new ArrayList<Location>();
	}

	public long countByName(String name, Operation operation) throws MiddlewareQueryException {
		try {
			if (name != null) {
				Criteria criteria = this.getSession().createCriteria(Location.class);
				criteria.setProjection(Projections.rowCount());

				this.addNameSearchCriteria(name, operation, criteria);

				return ((Long) criteria.uniqueResult()).longValue();
			}
		} catch (HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage("countByName", LocationDAO.NAME_OR_OPERATION, name + "|" + operation,
					e.getMessage(), LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return 0;
	}

	public long countByNameAndUniqueID(String name, Operation operation, String programUUID) throws MiddlewareQueryException {
		try {
			if (name != null) {
				Criteria criteria = this.getSession().createCriteria(Location.class);
				criteria.setProjection(Projections.rowCount());

				this.addNameSearchCriteria(name, operation, criteria);
				criteria.add(
						Restrictions.or(Restrictions.eq(LocationDAO.UNIQUE_ID, programUUID), Restrictions.isNull(LocationDAO.UNIQUE_ID)));
				return ((Long) criteria.uniqueResult()).longValue();
			}
		} catch (HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage("countByName", "name|operation|programUUID",
					name + "|" + operation + "|" + programUUID, e.getMessage(), "Location"), e);
		}
		return 0;
	}

	public List<Location> getByCountry(Country country) throws MiddlewareQueryException {
		try {
			if (country != null) {
				Integer countryId = country.getCntryid();
				Criteria criteria = this.getSession().createCriteria(Location.class);
				criteria.add(Restrictions.eq(LocationDAO.COUNTRY_ID, countryId));
				criteria.addOrder(Order.asc(LocationDAO.LNAME));
				return criteria.list();
			}
		} catch (HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage(LocationDAO.GET_BY_COUNTRY, LocationDAO.COUNTRY, country.toString(),
					e.getMessage(), LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return new ArrayList<Location>();
	}

	public List<Location> getByCountryAndType(Country country, Integer type) throws MiddlewareQueryException {
		try {
			if (country != null && type != null) {
				Integer countryId = country.getCntryid();
				Criteria criteria = this.getSession().createCriteria(Location.class);
				criteria.add(Restrictions.eq(LocationDAO.COUNTRY_ID, countryId));
				criteria.add(Restrictions.eq(LocationDAO.LTYPE, type));
				criteria.addOrder(Order.asc(LocationDAO.LNAME));
				return criteria.list();
			}
		} catch (HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage(LocationDAO.GET_BY_COUNTRY, LocationDAO.COUNTRY, country.toString(),
					e.getMessage(), LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return new ArrayList<Location>();
	}

	public List<Location> getByNameCountryAndType(String name, Country country, Integer type) throws MiddlewareQueryException {
		try {

			Integer countryId = null;
			if (country != null) {
				countryId = country.getCntryid();
			}

			Criteria criteria = this.getSession().createCriteria(Location.class);

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
		} catch (HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage(LocationDAO.GET_BY_COUNTRY, LocationDAO.COUNTRY, country.toString(),
					e.getMessage(), LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return new ArrayList<Location>();
	}

	public List<Location> getByCountry(Country country, int start, int numOfRows) throws MiddlewareQueryException {
		try {
			if (country != null) {
				Integer countryId = country.getCntryid();
				Criteria criteria = this.getSession().createCriteria(Location.class);
				criteria.add(Restrictions.eq(LocationDAO.COUNTRY_ID, countryId));
				criteria.setFirstResult(start);
				criteria.setMaxResults(numOfRows);
				return criteria.list();
			}
		} catch (HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage(LocationDAO.GET_BY_COUNTRY, LocationDAO.COUNTRY, country.toString(),
					e.getMessage(), LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return new ArrayList<Location>();
	}

	public long countByCountry(Country country) throws MiddlewareQueryException {
		try {
			if (country != null) {
				Integer countryId = country.getCntryid();
				Criteria criteria = this.getSession().createCriteria(Location.class);
				criteria.add(Restrictions.eq(LocationDAO.COUNTRY_ID, countryId));
				criteria.setProjection(Projections.rowCount());
				return ((Long) criteria.uniqueResult()).longValue();
			}
		} catch (HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage(LocationDAO.GET_BY_COUNTRY, LocationDAO.COUNTRY, country.toString(),
					e.getMessage(), LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return 0;
	}

	public List<Location> getByType(Integer type) throws MiddlewareQueryException {
		try {
			if (type != null) {
				Criteria criteria = this.getSession().createCriteria(Location.class);
				criteria.add(Restrictions.eq(LocationDAO.LTYPE, type));
				criteria.addOrder(Order.asc(LocationDAO.LNAME));
				return criteria.list();
			}
		} catch (HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage(LocationDAO.GET_BY_TYPE, "type", String.valueOf(type), e.getMessage(),
					LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return new ArrayList<Location>();
	}

	public List<Location> getByType(Integer type, String programUUID) throws MiddlewareQueryException {
		try {
			if (type != null) {
				Criteria criteria = this.getSession().createCriteria(Location.class);
				criteria.add(Restrictions.eq(LocationDAO.LTYPE, type));
				criteria.add(
						Restrictions.or(Restrictions.eq(LocationDAO.UNIQUE_ID, programUUID), Restrictions.isNull(LocationDAO.UNIQUE_ID)));
				criteria.addOrder(Order.asc(LocationDAO.LNAME));
				return criteria.list();
			}
		} catch (HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage(LocationDAO.GET_BY_TYPE, "type", String.valueOf(type), e.getMessage(),
					LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return new ArrayList<Location>();
	}

	public List<Location> getByType(Integer type, int start, int numOfRows) throws MiddlewareQueryException {
		try {
			if (type != null) {
				Criteria criteria = this.getSession().createCriteria(Location.class);
				criteria.add(Restrictions.eq(LocationDAO.LTYPE, type));
				criteria.setFirstResult(start);
				criteria.setMaxResults(numOfRows);
				return criteria.list();
			}
		} catch (HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage(LocationDAO.GET_BY_TYPE, "type", String.valueOf(type), e.getMessage(),
					LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return new ArrayList<Location>();
	}

	public long countByType(Integer type) throws MiddlewareQueryException {
		try {
			if (type != null) {
				Criteria criteria = this.getSession().createCriteria(Location.class);
				criteria.add(Restrictions.eq(LocationDAO.LTYPE, type));
				criteria.setProjection(Projections.rowCount());
				return ((Long) criteria.uniqueResult()).longValue();
			}
		} catch (HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage("countByType", "type", String.valueOf(type), e.getMessage(),
					LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return 0;
	}

	public long countByType(Integer type, String programUUID) throws MiddlewareQueryException {
		try {
			if (type != null) {
				Criteria criteria = this.getSession().createCriteria(Location.class);
				criteria.add(Restrictions.eq(LocationDAO.LTYPE, type));
				criteria.add(
						Restrictions.or(Restrictions.eq(LocationDAO.UNIQUE_ID, programUUID), Restrictions.isNull(LocationDAO.UNIQUE_ID)));
				criteria.setProjection(Projections.rowCount());
				return ((Long) criteria.uniqueResult()).longValue();
			}
		} catch (HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage("countByType", "type", String.valueOf(type), e.getMessage(),
					LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return 0;
	}

	@SuppressWarnings("rawtypes")
	public List<Location> getAllBreedingLocations() throws MiddlewareQueryException {
		List<Location> locationList = new ArrayList<Location>();
		try {
			Session session = this.getSession();
			SQLQuery query = session.createSQLQuery(Location.GET_ALL_BREEDING_LOCATIONS);
			List results = query.list();

			for (Object o : results) {
				Object[] result = (Object[]) o;
				if (result != null) {
					Integer locid = (Integer) result[0];
					Integer ltype = (Integer) result[1];
					Integer nllp = (Integer) result[2];
					String lname = (String) result[3];
					String labbr = (String) result[4];
					Integer snl3id = (Integer) result[5];
					Integer snl2id = (Integer) result[6];
					Integer snl1id = (Integer) result[7];
					Integer cntryid = (Integer) result[8];
					Integer lrplce = (Integer) result[9];
					Double latitude = (Double) result[11];
					Double longitude = (Double) result[12];
					Double altitude = (Double) result[13];
					String programUUID = (String) result[14];

					Location location = new Location(locid, ltype, nllp, lname, labbr, snl3id, snl2id, snl1id, cntryid, lrplce);
					location.setUniqueID(programUUID);

					Georef georef = new Georef();
					georef.setLocid(locid);
					georef.setLat(latitude);
					georef.setLon(longitude);
					georef.setAlt(altitude);
					location.setGeoref(georef);
					locationList.add(location);
				}
			}
			return locationList;
		} catch (HibernateException e) {
			this.logAndThrowException(
					this.getLogExceptionMessage("getAllBreedingLocations", "", null, e.getMessage(), "GermplasmDataManager"), e);
			return new ArrayList<Location>();
		}
	}

	@SuppressWarnings("deprecation")
	public Long countAllBreedingLocations() throws MiddlewareQueryException {
		try {
			Session session = this.getSession();
			SQLQuery query = session.createSQLQuery(Location.COUNT_ALL_BREEDING_LOCATIONS);
			return (Long) query.addScalar("count", Hibernate.LONG).uniqueResult();
		} catch (HibernateException e) {
			this.logAndThrowException(
					this.getLogExceptionMessage("countAllBreedingLocations", "", null, e.getMessage(), LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return Long.valueOf(0);
	}

	public List<LocationDetails> getLocationDetails(Integer locationId, Integer start, Integer numOfRows) throws MiddlewareQueryException {
		try {
			StringBuilder queryString = new StringBuilder();
			queryString.append("select l.lname as location_name,l.locid,l.ltype as ltype,");
			queryString.append(" g.lat as latitude, g.lon as longitude, g.alt as altitude,");
			queryString.append(" c.cntryid as cntryid, c.isofull as country_full_name, labbr as location_abbreviation,");
			queryString.append(" ud.fname as location_type,");
			queryString.append(" ud.fdesc as location_description");
			queryString.append(" from location l");
			queryString.append(" left join georef g on l.locid = g.locid");
			queryString.append(" left join cntry c on l.cntryid = c.cntryid");
			queryString.append(" left join udflds ud on ud.fldno = l.ltype");

			if (locationId != null) {
				queryString.append(" where l.locid = :id");

				SQLQuery query = this.getSession().createSQLQuery(queryString.toString());
				query.setParameter("id", locationId);
				query.setFirstResult(start);
				query.setMaxResults(numOfRows);
				query.addEntity(LocationDetails.class);

				return query.list();
			}

		} catch (HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage("getLocationDetails", "id", String.valueOf(locationId), e.getMessage(),
					LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return new ArrayList<LocationDetails>();
	}

	@Override
	public Location saveOrUpdate(Location location) throws MiddlewareQueryException {
		try {
			Location savedLocation = super.saveOrUpdate(location);
			if (location.getGeoref() != null) {
				location.getGeoref().setLocid(location.getLocid());
				this.getSession().saveOrUpdate(location.getGeoref());
			}
			return savedLocation;
		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error in saveOrUpdate(location): " + e.getMessage(), e);
		}
	}

	public List<LocationDetails> getLocationDetails(List<Integer> locationId, Integer start, Integer numOfRows)
			throws MiddlewareQueryException {
		try {
			StringBuilder queryString = new StringBuilder();
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

				SQLQuery query = this.getSession().createSQLQuery(queryString.toString());
				query.setParameterList("id", locationId);
				query.setFirstResult(start);
				query.setMaxResults(numOfRows);
				query.addEntity(LocationDetails.class);

				return query.list();

			}
		} catch (HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage("getLocationDetails", "id", String.valueOf(locationId), e.getMessage(),
					LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return new ArrayList<LocationDetails>();
	}

	public List<Location> getAllProvincesByCountry(Integer countryId) throws MiddlewareQueryException {
		if (countryId == null || countryId == 0) {
			return new ArrayList<Location>();
		}

		try {
			SQLQuery query = this.getSession().createSQLQuery(Location.GET_PROVINCE_BY_COUNTRY);
			query.addEntity(Location.class);
			query.setParameter("countryId", countryId);
			return query.list();
		} catch (HibernateException e) {
			this.logAndThrowException(
					this.getLogExceptionMessage("getAllProvinces", "", null, e.getMessage(), LocationDAO.CLASS_NAME_LOCATION), e);
			return new ArrayList<Location>();
		}
	}

	public List<Location> getAllProvinces() throws MiddlewareQueryException {

		try {
			SQLQuery query = this.getSession().createSQLQuery(Location.GET_ALL_PROVINCES);
			query.addEntity(Location.class);
			return query.list();
		} catch (HibernateException e) {
			this.logAndThrowException(
					this.getLogExceptionMessage("getAllProvinces", "", null, e.getMessage(), LocationDAO.CLASS_NAME_LOCATION), e);
			return new ArrayList<Location>();
		}
	}

	public List<LocationDto> getLocationDtoByIds(Collection<Integer> ids) throws MiddlewareQueryException {
		List<LocationDto> returnList = new ArrayList<LocationDto>();
		if (ids == null || ids.isEmpty()) {
			return returnList;
		}
		try {
			String sql = "SELECT l.lname, prov.lname, c.isoabbr, l.locid" + " FROM location l"
					+ " LEFT JOIN location prov ON prov.locid = l.snl1id" + " LEFT JOIN cntry c ON c.cntryid = l.cntryid"
					+ " WHERE l.locid in (:ids)";
			SQLQuery query = this.getSession().createSQLQuery(sql);
			query.setParameterList("ids", ids);
			List<Object[]> results = query.list();

			if (results != null) {
				for (Object[] result : results) {
					returnList.add(new LocationDto((Integer) result[3], (String) result[0], (String) result[1], (String) result[2]));
				}
			}

		} catch (HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage("getLocationDtoById", "id", ids.toString(), e.getMessage(),
					LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return returnList;
	}

	public List<Location> getLocationByIds(Collection<Integer> ids) throws MiddlewareQueryException {

		if(ids == null || ids.isEmpty()) {
			return new ArrayList<>();
		}

		try {
			return this.getSession().createCriteria(Location.class).add(Restrictions.in(LocationDAO.LOCID, ids))
					.addOrder(Order.asc(LocationDAO.LNAME)).list();
		} catch (HibernateException e) {
			this.logAndThrowException(String.format("Error with getLocationByIds(id=[%s])", StringUtils.join(ids, ",")), e);
		}

		return new ArrayList<>();
	}

	public Map<Integer, String> getLocationNamesByLocationIDs(List<Integer> locIds) throws MiddlewareQueryException {
		Map<Integer, String> toreturn = new HashMap<Integer, String>();

		List<Location> locations = this.getLocationByIds(locIds);
		for (Location location : locations) {
			toreturn.put(location.getLocid(), location.getLname());
		}

		return toreturn;
	}

	public Map<Integer, String> getLocationNamesMapByGIDs(List<Integer> gids) throws MiddlewareQueryException {
		Map<Integer, String> toreturn = new HashMap<Integer, String>();
		for (Integer gid : gids) {
			toreturn.put(gid, null);
		}

		try {
			SQLQuery query = this.getSession().createSQLQuery(Location.GET_LOCATION_NAMES_BY_GIDS);
			query.setParameterList("gids", gids);

			List<Object> results = query.list();
			for (Object result : results) {
				Object[] resultArray = (Object[]) result;
				Integer gid = (Integer) resultArray[0];
				String location = (String) resultArray[2];
				toreturn.put(gid, location);
			}
		} catch (HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage("getLocationNamesMapByGIDs", "gids", gids.toString(), e.getMessage(),
					LocationDAO.CLASS_NAME_LOCATION), e);
		}

		return toreturn;
	}

	public Map<Integer, LocationDto> getLocationNamesByGIDs(List<Integer> gids) throws MiddlewareQueryException {
		Map<Integer, LocationDto> toreturn = new HashMap<Integer, LocationDto>();
		for (Integer gid : gids) {
			toreturn.put(gid, null);
		}

		try {
			SQLQuery query = this.getSession().createSQLQuery(Location.GET_LOCATION_NAMES_BY_GIDS);
			query.setParameterList("gids", gids);

			List<Object> results = query.list();
			for (Object result : results) {
				Object[] resultArray = (Object[]) result;
				Integer gid = (Integer) resultArray[0];
				Integer locid = (Integer) resultArray[1];
				String locationName = (String) resultArray[2];
				toreturn.put(gid, new LocationDto(locid, locationName));
			}
		} catch (HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage("getLocationNamesByGIDs", "gids", gids.toString(), e.getMessage(),
					LocationDAO.CLASS_NAME_LOCATION), e);
		}

		return toreturn;
	}

	public List<Location> getLocationsByDTypeAndLType(String dval, Integer dType, Integer lType) throws MiddlewareQueryException {
		List<Location> locations = new ArrayList<Location>();
		try {
			StringBuilder sqlString = new StringBuilder().append("SELECT  l.locid, l.ltype, l.nllp, l.lname, l.labbr")
					.append(", l.snl3id, l.snl2id, l.snl1id, l.cntryid, l.lrplce ").append("FROM locdes ld INNER JOIN location l ")
					.append(" ON l.locid = ld.locid ").append("WHERE dtype = :dtype  AND ltype = :ltype AND dval = :dval ");

			SQLQuery query = this.getSession().createSQLQuery(sqlString.toString());
			query.setParameter("dtype", dType);
			query.setParameter(LocationDAO.LTYPE, lType);
			query.setParameter("dval", dval);

			List<Object[]> results = query.list();

			if (!results.isEmpty()) {
				for (Object[] row : results) {
					Integer locid = (Integer) row[0];
					Integer ltype = (Integer) row[1];
					Integer nllp = (Integer) row[2];
					String lname = (String) row[3];
					String labbr = (String) row[4];
					Integer snl3id = (Integer) row[5];
					Integer snl2id = (Integer) row[6];
					Integer snl1id = (Integer) row[7];
					Integer cntryid = (Integer) row[8];
					Integer lrplce = (Integer) row[9];

					locations.add(new Location(locid, ltype, nllp, lname, labbr, snl3id, snl2id, snl1id, cntryid, lrplce));

				}
			}

		} catch (HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage("getLocationsByDTypeAndLType", "dType|lType", dType + "|" + lType,
					e.getMessage(), "Locdes"), e);
		}
		return locations;
	}

	public List<Location> getByTypeWithParent(Integer type, Integer relationshipType) throws MiddlewareQueryException {
		List<Location> locationList = new ArrayList<Location>();
		try {
			Session session = this.getSession();
			String sql = "SELECT f.locid, f.lname, fd.dval " + " FROM location f "
					+ " INNER JOIN locdes fd ON fd.locid = f.locid AND fd.dtype = " + relationshipType + " WHERE f.ltype = " + type;
			SQLQuery query = session.createSQLQuery(sql);
			List<Object[]> results = query.list();

			for (Object o : results) {
				Object[] result = (Object[]) o;
				if (result != null) {
					Integer fieldId = (Integer) result[0];
					String fieldName = (String) result[1];
					String parentId = (String) result[2];

					Location location = new Location();
					location.setLocid(fieldId);
					location.setLname(fieldName);
					location.setParentLocationId(parentId != null && NumberUtils.isNumber(parentId) ? Integer.valueOf(parentId) : null);
					locationList.add(location);
				}
			}
		} catch (HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage("getByTypeWithParent", "type", type.toString(), e.getMessage(),
					LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return locationList;
	}

	public Map<Integer, Location> getNamesByIdsIntoMap(Collection<Integer> ids) throws MiddlewareQueryException {
		Map<Integer, Location> map = new HashMap<Integer, Location>();
		try {
			Criteria criteria = this.getSession().createCriteria(Location.class);
			criteria.add(Restrictions.in(LocationDAO.LOCID, ids));
			List<Location> locations = criteria.list();

			if (locations != null && !locations.isEmpty()) {
				for (Location location : locations) {
					map.put(location.getLocid(), location);
				}
			}

		} catch (HibernateException e) {
			this.logAndThrowException(
					this.getLogExceptionMessage("getNamesByIdsIntoMap", "", null, e.getMessage(), LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return map;
	}

	public List<Location> getByIds(List<Integer> ids) throws MiddlewareQueryException {
		List<Location> locations = new ArrayList<Location>();

		if (ids == null || ids.isEmpty()) {
			return locations;
		}

		try {
			Criteria criteria = this.getSession().createCriteria(Location.class);
			criteria.add(Restrictions.in(LocationDAO.LOCID, ids));
			locations = criteria.list();
		} catch (HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage("getByIds", "", null, e.getMessage(), LocationDAO.CLASS_NAME_LOCATION),
					e);
		}
		return locations;
	}

	public List<Location> getByUniqueID(String programUUID) throws MiddlewareQueryException {
		List<Location> locations = new ArrayList<Location>();

		if (programUUID == null || programUUID.isEmpty()) {
			return locations;
		}

		try {
			Criteria criteria = this.getSession().createCriteria(Location.class);
			criteria.add(Restrictions.or(Restrictions.eq(LocationDAO.UNIQUE_ID, programUUID), Restrictions.isNull(LocationDAO.UNIQUE_ID)));
			locations = criteria.list();
		} catch (HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage("getByIds", "", null, e.getMessage(), LocationDAO.CLASS_NAME_LOCATION),
					e);
		}
		return locations;
	}

	public long countByUniqueID(String programUUID) throws MiddlewareQueryException {
		try {
			if (programUUID != null) {
				Criteria criteria = this.getSession().createCriteria(Location.class);
				criteria.add(
						Restrictions.or(Restrictions.eq(LocationDAO.UNIQUE_ID, programUUID), Restrictions.isNull(LocationDAO.UNIQUE_ID)));
				criteria.setProjection(Projections.rowCount());
				return ((Long) criteria.uniqueResult()).longValue();
			}
		} catch (HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage("countByUniqueID", "uniqueID", programUUID, e.getMessage(),
					LocationDAO.CLASS_NAME_LOCATION), e);
		}
		return 0;
	}

	public List<Location> getProgramLocations(String programUUID) throws MiddlewareQueryException {
		List<Location> locations = new ArrayList<Location>();
		try {
			Criteria criteria = this.getSession().createCriteria(Location.class);
			criteria.add(Restrictions.eq(LocationDAO.UNIQUE_ID, programUUID));
			locations = criteria.list();
		} catch (HibernateException e) {
			this.logAndThrowException("Error in getProgramLocations(" + programUUID + ") in LocationDao: " + e.getMessage(), e);
		}
		return locations;
	}

	public List<Locdes> getLocdesByLocId(Integer locationId) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(Locdes.class);
			criteria.add(Restrictions.eq("locationId", locationId));
			return criteria.list();
		} catch (HibernateException e) {
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
			throw new MiddlewareQueryException(this.getLogExceptionMessage("getBreedingLocations", "", null, e.getMessage(), "Location"),
					e);
		}
	}

	public List<Location> getSeedingLocations(final List<Integer> ids, final Integer seedLType) throws MiddlewareQueryException {
		try {

			final Criteria criteria = this.getSession().createCriteria(Location.class);

			criteria.add(Restrictions.in("locid", ids));
			criteria.add(Restrictions.eq("ltype", seedLType));
			criteria.addOrder(Order.asc("lname"));

			return criteria.list();
		} catch (final HibernateException e) {
			LocationDAO.LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException(this.getLogExceptionMessage("getSeedingLocations", "", null, e.getMessage(), "Location"), e);
		}
	}

}
