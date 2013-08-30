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
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.generationcp.middleware.domain.dms.LocationDto;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.LocationDetails;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link Location}.
 * 
 */
public class LocationDAO extends GenericDAO<Location, Integer>{

    @SuppressWarnings("unchecked")
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

    @SuppressWarnings("unchecked")
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
        	if (name != null){
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

    @SuppressWarnings("unchecked")
    public List<Location> getByCountry(Country country) throws MiddlewareQueryException {
        try {
        	if (country != null){
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
    
    @SuppressWarnings("unchecked")
    public List<Location> getByCountryAndType(Country country, Integer type) throws MiddlewareQueryException {
        try {
        	if (country != null && type != null){
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

    @SuppressWarnings("unchecked")
    public List<Location> getByCountry(Country country, int start, int numOfRows) throws MiddlewareQueryException {
        try {
        	if (country != null){
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
        	if (country != null){
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


    @SuppressWarnings("unchecked")
    public List<Location> getByType(Integer type) throws MiddlewareQueryException {
        try {
        	if (type != null){
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
    


    @SuppressWarnings("unchecked")
    public List<Location> getByType(Integer type, int start, int numOfRows) throws MiddlewareQueryException {
        try {
        	if (type != null){
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
        	if (type != null){
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
            		String lname =  (String) result[3];
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
        	Long total = (Long) query.addScalar("count",Hibernate.LONG).uniqueResult();
        	return total;
        } catch (HibernateException e) {
            logAndThrowException("Error with countAllBredingLocations() query from Location: "+ e.getMessage(), e);
        }
        return Long.valueOf(0);
    }
    
    @SuppressWarnings("unchecked")
	public List<LocationDetails> getLocationDetails(Integer locationId, Integer start, Integer numOfRows) throws MiddlewareQueryException {
    	        try {
    	        	if (locationId != null){
	    	            StringBuilder queryString = new StringBuilder();
	    	            queryString.append("select lname as location_name,locid,");
	    	            queryString.append(" c.isofull as country_full_name, labbr as location_abbreviation,"); 
	    	            queryString.append(" ud.fname as location_type,");
	    	            queryString.append(" ud.fdesc as location_description");
	    	            queryString.append(" from location l");
	    	            queryString.append(" inner join cntry c on l.cntryid = c.cntryid");
	    	            queryString.append(" inner join udflds ud on ud.fldno = l.ltype");
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
    
    @SuppressWarnings("unchecked")
	public List<LocationDto> getLocationDtoByIds(Collection<Integer> ids) throws MiddlewareQueryException {
		List<LocationDto> returnList = new ArrayList<LocationDto>();
		if (ids != null && ids.size() > 0) {
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
    
    @SuppressWarnings("unchecked")
    public Map<Integer, String> getLocationNamesByGIDs(List<Integer> gids) throws MiddlewareQueryException {
        Map<Integer, String> toreturn = new HashMap<Integer, String>();
        for(Integer gid : gids){
            toreturn.put(gid, null);
        }
        
        try{
            SQLQuery query = getSession().createSQLQuery(Location.GET_LOCATION_NAMES_BY_GIDS);
            query.setParameterList("gids", gids);
            
            List<Object> results = query.list();
            for(Object result : results){
                Object resultArray[] = (Object[]) result;
                Integer gid = (Integer) resultArray[0];
                String location = (String) resultArray[1];
                toreturn.put(gid, location);
            }
        } catch (HibernateException e) {
            logAndThrowException("Error with getLocationNamesByGIDs(gids=" + gids + ") query from Location " + e.getMessage(), e);
        }
        
        return toreturn;
    }
}
