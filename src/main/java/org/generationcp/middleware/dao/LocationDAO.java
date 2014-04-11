/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
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
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.LocationDetails;
import org.hibernate.*;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link Location}.
 */
@SuppressWarnings("unchecked")
public class LocationDAO extends GenericDAO<Location, Integer> {

    public List<Location> getByName(String name, Operation operation) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(Location.class);

            if (operation == null || operation == Operation.EQUAL) {
                criteria.add(Restrictions.eq("lname", name));
            } else if (operation == Operation.LIKE) {
                criteria.add(Restrictions.like("lname", name));
            }
            return criteria.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getByName(name=" + name + ", operation=" + operation + ") query from Location: "
                    + e.getMessage(), e);
        }
        return new ArrayList<Location>();
    }

    public List<Location> getByName(String name, Operation operation, int start, int numOfRows) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(Location.class);

            if (operation == null || operation == Operation.EQUAL) {
                criteria.add(Restrictions.eq("lname", name));
            } else if (operation == Operation.LIKE) {
                criteria.add(Restrictions.like("lname", name));
            }

            criteria.setFirstResult(start);
            criteria.setMaxResults(numOfRows);
            return criteria.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getByName(name=" + name + ", operation=" + operation + ") query from Location: "
                    + e.getMessage(), e);
        }
        return new ArrayList<Location>();
    }

    public long countByName(String name, Operation operation) throws MiddlewareQueryException {
        try {
            if (name != null) {
                Criteria criteria = getSession().createCriteria(Location.class);
                criteria.setProjection(Projections.rowCount());

                if (operation == null || operation == Operation.EQUAL) {
                    criteria.add(Restrictions.eq("lname", name));
                } else if (operation == Operation.LIKE) {
                    criteria.add(Restrictions.like("lname", name));
                }

                return ((Long) criteria.uniqueResult()).longValue();
            }
        } catch (HibernateException e) {
            logAndThrowException("Error with countByName(name=" + name + ", operation=" + operation
                    + ") query from Location: " + e.getMessage(), e);
        }
        return 0;
    }

    public List<Location> getByCountry(Country country) throws MiddlewareQueryException {
        try {
            if (country != null) {
                Integer countryId = country.getCntryid();
                Criteria criteria = getSession().createCriteria(Location.class);
                criteria.add(Restrictions.eq("cntryid", countryId));
                criteria.addOrder(Order.asc("lname"));
                return criteria.list();
            }
        } catch (HibernateException e) {
            logAndThrowException("Error with getByCountry(country=" + country + ") query from Location: "
                    + e.getMessage(), e);
        }
        return new ArrayList<Location>();
    }

    public List<Location> getByCountryAndType(Country country, Integer type) throws MiddlewareQueryException {
        try {
            if (country != null && type != null) {
                Integer countryId = country.getCntryid();
                Criteria criteria = getSession().createCriteria(Location.class);
                criteria.add(Restrictions.eq("cntryid", countryId));
                criteria.add(Restrictions.eq("ltype", type));
                criteria.addOrder(Order.asc("lname"));
                return criteria.list();
            }
        } catch (HibernateException e) {
            logAndThrowException("Error with getByCountry(country=" + country + ") query from Location: "
                    + e.getMessage(), e);
        }
        return new ArrayList<Location>();
    }

    public List<Location> getByNameCountryAndType(String name, Country country, Integer type) throws MiddlewareQueryException {
        try {

            Integer countryId = null;
            if (country != null)
                countryId = country.getCntryid();

            Criteria criteria = getSession().createCriteria(Location.class);

            if (countryId != null)
                criteria.add(Restrictions.eq("cntryid", countryId));

            if (type != null)
                criteria.add(Restrictions.eq("ltype", type));

            if (name != null && !name.isEmpty())
                criteria.add(Restrictions.like("lname", "%" + name.trim() + "%"));

            criteria.addOrder(Order.asc("lname"));

            return criteria.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getByCountry(country=" + country + ") query from Location: "
                    + e.getMessage(), e);
        }
        return new ArrayList<Location>();
    }

    public List<Location> getByCountry(Country country, int start, int numOfRows) throws MiddlewareQueryException {
        try {
            if (country != null) {
                Integer countryId = country.getCntryid();
                Criteria criteria = getSession().createCriteria(Location.class);
                criteria.add(Restrictions.eq("cntryid", countryId));
                criteria.setFirstResult(start);
                criteria.setMaxResults(numOfRows);
                return criteria.list();
            }
        } catch (HibernateException e) {
            logAndThrowException("Error with getByCountry(country=" + country + ") query from Location: "
                    + e.getMessage(), e);
        }
        return new ArrayList<Location>();
    }

    public long countByCountry(Country country) throws MiddlewareQueryException {
        try {
            if (country != null) {
                Integer countryId = country.getCntryid();
                Criteria criteria = getSession().createCriteria(Location.class);
                criteria.add(Restrictions.eq("cntryid", countryId));
                criteria.setProjection(Projections.rowCount());
                return ((Long) criteria.uniqueResult()).longValue();
            }
        } catch (HibernateException e) {
            logAndThrowException("Error with countByCountry(country=" + country
                    + ") query from Location: " + e.getMessage(), e);
        }
        return 0;
    }

    public List<Location> getByType(Integer type) throws MiddlewareQueryException {
        try {
            if (type != null) {
                Criteria criteria = getSession().createCriteria(Location.class);
                criteria.add(Restrictions.eq("ltype", type));
                criteria.addOrder(Order.asc("lname"));
                return criteria.list();
            }
        } catch (HibernateException e) {
            logAndThrowException("Error with getByType(type=" + type + ") query from Location: "
                    + e.getMessage(), e);
        }
        return new ArrayList<Location>();
    }

    public List<Location> getByType(Integer type, int start, int numOfRows) throws MiddlewareQueryException {
        try {
            if (type != null) {
                Criteria criteria = getSession().createCriteria(Location.class);
                criteria.add(Restrictions.eq("ltype", type));
                criteria.setFirstResult(start);
                criteria.setMaxResults(numOfRows);
                return criteria.list();
            }
        } catch (HibernateException e) {
            logAndThrowException("Error with getByType(type=" + type + ") query from Location: "
                    + e.getMessage(), e);
        }
        return new ArrayList<Location>();
    }

    public long countByType(Integer type) throws MiddlewareQueryException {
        try {
            if (type != null) {
                Criteria criteria = getSession().createCriteria(Location.class);
                criteria.add(Restrictions.eq("ltype", type));
                criteria.setProjection(Projections.rowCount());
                return ((Long) criteria.uniqueResult()).longValue();
            }
        } catch (HibernateException e) {
            logAndThrowException("Error with countBytype(type=" + type
                    + ") query from Location: " + e.getMessage(), e);
        }
        return 0;
    }

    @SuppressWarnings("rawtypes")
    public List<Location> getAllBreedingLocations() throws MiddlewareQueryException {
        List<Location> locationList = new ArrayList<Location>();
        try {
            Session session = getSession();
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

                    Location location = new Location(locid, ltype, nllp, lname, labbr, snl3id, snl2id, snl1id, cntryid, lrplce);
                    locationList.add(location);
                }
            }
            return locationList;
        } catch (HibernateException e) {
            logAndThrowException("Error with getAllBreedingLocations() query from GermplasmDataManager: " + e.getMessage(), e);
            return null;
        }
    }

    @SuppressWarnings("deprecation")
    public Long countAllBreedingLocations() throws MiddlewareQueryException, HibernateException {
        try {
            Session session = getSession();
            SQLQuery query = session.createSQLQuery(Location.COUNT_ALL_BREEDING_LOCATIONS);
            return (Long) query.addScalar("count", Hibernate.LONG).uniqueResult();
        } catch (HibernateException e) {
            logAndThrowException("Error with countAllBredingLocations() query from Location: " + e.getMessage(), e);
        }
        return Long.valueOf(0);
    }

    public List<LocationDetails> getLocationDetails(Integer locationId, Integer start, Integer numOfRows) throws MiddlewareQueryException {
        try {
            StringBuilder queryString = new StringBuilder();
            queryString.append("select lname as location_name,locid,l.ltype as ltype, c.cntryid as cntryid,");
            queryString.append(" c.isofull as country_full_name, labbr as location_abbreviation,");
            queryString.append(" ud.fname as location_type,");
            queryString.append(" ud.fdesc as location_description");
            queryString.append(" from location l");
            queryString.append(" left join cntry c on l.cntryid = c.cntryid");
            queryString.append(" left join udflds ud on ud.fldno = l.ltype");

            if (locationId != null) {
                queryString.append(" where locid = :id");

                SQLQuery query = getSession().createSQLQuery(queryString.toString());
                query.setParameter("id", locationId);
                query.setFirstResult(start);
                query.setMaxResults(numOfRows);
                query.addEntity(LocationDetails.class);

                List<LocationDetails> list = query.list();

                return list;
            }

        } catch (HibernateException e) {
            logAndThrowException("Error with getLocationDetails(id=" + locationId + ") : " + e.getMessage(), e);
        }
        return null;
    }

    public List<LocationDetails> getLocationDetails(List<Integer> locationId, Integer start, Integer numOfRows) throws MiddlewareQueryException {
        try {
            StringBuilder queryString = new StringBuilder();
            queryString.append("select lname as location_name,locid,l.ltype as ltype, c.cntryid as cntryid,");
            queryString.append(" c.isofull as country_full_name, labbr as location_abbreviation,");
            queryString.append(" ud.fname as location_type,");
            queryString.append(" ud.fdesc as location_description");
            queryString.append(" from location l");
            queryString.append(" left join cntry c on l.cntryid = c.cntryid");
            queryString.append(" left join udflds ud on ud.fldno = l.ltype");

            if (locationId != null) {

                queryString.append(" where locid = :id");

                SQLQuery query = getSession().createSQLQuery(queryString.toString());
                query.setParameterList("id", locationId);
                query.setFirstResult(start);
                query.setMaxResults(numOfRows);
                query.addEntity(LocationDetails.class);

                List<LocationDetails> list = query.list();

                return list;

            }
        } catch (HibernateException e) {
            logAndThrowException("Error with getLocationDetails(id=" + locationId + ") : " + e.getMessage(), e);
        }
        return null;
    }

	public List<Location> getAllProvincesByCountry(Integer countryId) throws MiddlewareQueryException {
        if (countryId == null || countryId == 0) {
            return new ArrayList<Location>();
        }

        try {
            SQLQuery query = getSession().createSQLQuery(Location.GET_PROVINCE_BY_COUNTRY);
            query.addEntity(Location.class);
            query.setParameter("countryId", countryId);
            return query.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getAllProvinces() query from Location: " + e.getMessage(), e);
            return new ArrayList<Location>();
        }
    }

	public List<Location> getAllProvinces() throws MiddlewareQueryException {

            try {
                SQLQuery query = getSession().createSQLQuery(Location.GET_ALL_PROVINCES);
                query.addEntity(Location.class);
                return query.list();
            } catch (HibernateException e) {
                logAndThrowException("Error with getAllProvinces() query from Location: " + e.getMessage(), e);
                return new ArrayList<Location>();
            }
        }

    public List<LocationDto> getLocationDtoByIds(Collection<Integer> ids) throws MiddlewareQueryException {
        List<LocationDto> returnList = new ArrayList<LocationDto>();
        if (ids != null && !ids.isEmpty()) {
            try {
                String sql = "SELECT l.lname, prov.lname, c.isoabbr, l.locid"
                        + " FROM location l"
                        + " LEFT JOIN location prov ON prov.locid = l.snl1id"
                        + " LEFT JOIN cntry c ON c.cntryid = l.cntryid"
                        + " WHERE l.locid in (:ids)";
                SQLQuery query = getSession().createSQLQuery(sql);
                query.setParameterList("ids", ids);
                List<Object[]> results = query.list();

                if (results != null) {
                    for (Object[] result : results) {
                        returnList.add(new LocationDto((Integer) result[3], (String) result[0], (String) result[1], (String) result[2]));
                    }
                }

            } catch (HibernateException e) {
                logAndThrowException("Error with getLocationDtoById(id=" + ids + "): " + e.getMessage(), e);
            }
        }
        return returnList;
    }

    public List<Location> getLocationByIds(Collection<Integer> ids) throws MiddlewareQueryException {
        try {
            return getSession().createCriteria(Location.class).add(Restrictions.in("locid", ids)).list();
        } catch (HibernateException e) {
            logAndThrowException(String.format("Error with getLocationByIds(id=[%s])", StringUtils.join(ids, ",")), e);
        }

        return new ArrayList<Location>();
    }

    public Map<Integer, String> getLocationNamesByLocationIDs(List<Integer> locIds) throws MiddlewareQueryException {
        Map<Integer, String> toreturn = new HashMap<Integer, String>();

        List<Location> locations = getLocationByIds(locIds);
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
            SQLQuery query = getSession().createSQLQuery(Location.GET_LOCATION_NAMES_BY_GIDS);
            query.setParameterList("gids", gids);

            List<Object> results = query.list();
            for (Object result : results) {
                Object resultArray[] = (Object[]) result;
                Integer gid = (Integer) resultArray[0];
                String location = (String) resultArray[2];
                toreturn.put(gid, location);
            }
        } catch (HibernateException e) {
            logAndThrowException("Error with getLocationNamesMapByGIDs(gids=" + gids + ") query from Location " + e.getMessage(), e);
        }

        return toreturn;
    }

    public Map<Integer, LocationDto> getLocationNamesByGIDs(List<Integer> gids) throws MiddlewareQueryException {
        Map<Integer, LocationDto> toreturn = new HashMap<Integer, LocationDto>();
        for (Integer gid : gids) {
            toreturn.put(gid, null);
        }

        try {
            SQLQuery query = getSession().createSQLQuery(Location.GET_LOCATION_NAMES_BY_GIDS);
            query.setParameterList("gids", gids);

            List<Object> results = query.list();
            for (Object result : results) {
                Object resultArray[] = (Object[]) result;
                Integer gid = (Integer) resultArray[0];
                Integer locid = (Integer) resultArray[1];
                String locationName = (String) resultArray[2];
                toreturn.put(gid, new LocationDto(locid, locationName));
            }
        } catch (HibernateException e) {
            logAndThrowException("Error with getLocationNamesByGIDs(gids=" + gids + ") query from Location " + e.getMessage(), e);
        }

        return toreturn;
    }

    public List<Location> getLocationsByDTypeAndLType(String dval
            , Integer dType, Integer lType) throws MiddlewareQueryException {
        List<Location> locations = new ArrayList<Location>();
        try {
            StringBuilder sqlString = new StringBuilder()
                    .append("SELECT  l.locid, l.ltype, l.nllp, l.lname, l.labbr")
                    .append(", l.snl3id, l.snl2id, l.snl1id, l.cntryid, l.lrplce ")
                    .append("FROM locdes ld INNER JOIN location l ")
                    .append(" ON l.locid = ld.locid ")
                    .append("WHERE dtype = :dtype  AND ltype = :ltype AND dval = :dval ");

            SQLQuery query = getSession().createSQLQuery(sqlString.toString());
            query.setParameter("dtype", dType);
            query.setParameter("ltype", lType);
            query.setParameter("dval", dval);

            List<Object[]> results = query.list();

            if (results.size() > 0) {
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

                    locations.add(new Location(
                            locid, ltype, nllp, lname, labbr, snl3id, snl2id, snl1id, cntryid, lrplce));

                }
            }


        } catch (HibernateException e) {
            logAndThrowException("Error with getLoctionsByDTypeAndLType(dtype=" + dType
                    + ", ltype=" + lType + ") query from Locdes: " + e.getMessage(), e);
        }
        return locations;
    }

    public List<Location> getByTypeWithParent(Integer type, Integer relationshipType) throws MiddlewareQueryException {
        List<Location> locationList = new ArrayList<Location>();
        try {
            Session session = getSession();
            String sql = "SELECT f.locid, f.lname, fd.dval "
                    + " FROM location f "
                    + " INNER JOIN locdes fd ON fd.locid = f.locid AND fd.dtype = " + relationshipType
                    + " WHERE f.ltype = " + type;
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
            logAndThrowException("Error with getByTypeWithParent(type=" + type + ") query from Location: "
                    + e.getMessage(), e);
        }
        return locationList;
    }

    public Map<Integer, String> getNamesByIdsIntoMap(Collection<Integer> ids) throws MiddlewareQueryException {
        Map<Integer, String> map = new HashMap<Integer, String>();
        try {
            Criteria criteria = getSession().createCriteria(Location.class);
            criteria.add(Restrictions.in("locid", ids));
            List<Location> locations = criteria.list();

            if (locations != null && !locations.isEmpty()) {
                for (Location location : locations) {
                    map.put(location.getLocid(), location.getLname());
                }
            }

        } catch (HibernateException e) {
            logAndThrowException("Error with getNamesByIdsIntoMap(" + ") query from Location: "
                    + e.getMessage(), e);
        }
        return map;
    }

}
