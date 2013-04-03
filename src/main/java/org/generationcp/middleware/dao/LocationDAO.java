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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Location;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

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
            Criteria criteria = getSession().createCriteria(Location.class);
            criteria.setProjection(Projections.rowCount());

            if (operation == null || operation == Operation.EQUAL) {
                criteria.add(Restrictions.eq("lname", name));
            } else if (operation == Operation.LIKE) {
                criteria.add(Restrictions.like("lname", name));
            }

            return ((Long) criteria.uniqueResult()).longValue(); //count
        } catch (HibernateException e) {
            logAndThrowException("Error with countByName(name=" + name + ", operation=" + operation
                    + ") query from Location: " + e.getMessage(), e);
        }
        return 0;
    }

    @SuppressWarnings("unchecked")
    public List<Location> getByCountry(Country country) throws MiddlewareQueryException {
        Integer countryId = country.getCntryid();
        try {
            Criteria criteria = getSession().createCriteria(Location.class);
            criteria.add(Restrictions.eq("cntryid", countryId));
            criteria.addOrder(Order.asc("lname"));
            return criteria.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getByCountry(country=" + country + ") query from Location: "
                    + e.getMessage(), e);
        }
        return new ArrayList<Location>();
    }
    
    @SuppressWarnings("unchecked")
    public List<Location> getByCountryAndType(Country country,Integer type) throws MiddlewareQueryException {
        Integer countryId = country.getCntryid();
        try {
            Criteria criteria = getSession().createCriteria(Location.class);
            criteria.add(Restrictions.eq("cntryid", countryId));
            criteria.add(Restrictions.eq("ltype", type));
            criteria.addOrder(Order.asc("lname"));
            return criteria.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getByCountry(country=" + country + ") query from Location: "
                    + e.getMessage(), e);
        }
        return new ArrayList<Location>();
    }

    @SuppressWarnings("unchecked")
    public List<Location> getByCountry(Country country, int start, int numOfRows) throws MiddlewareQueryException {
        Integer countryId = country.getCntryid();
        try {
            Criteria criteria = getSession().createCriteria(Location.class);
            criteria.add(Restrictions.eq("cntryid", countryId));
            criteria.setFirstResult(start);
            criteria.setMaxResults(numOfRows);
            return criteria.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getByCountry(country=" + country + ") query from Location: "
                    + e.getMessage(), e);
        }
        return new ArrayList<Location>();
    }

    public long countByCountry(Country country) throws MiddlewareQueryException {
        Integer countryId = country.getCntryid();
        try {
            Criteria criteria = getSession().createCriteria(Location.class);
            criteria.add(Restrictions.eq("cntryid", countryId));
            criteria.setProjection(Projections.rowCount());
            return ((Long) criteria.uniqueResult()).longValue(); //count
        } catch (HibernateException e) {
            logAndThrowException("Error with countByCountry(country=" + country 
                    + ") query from Location: " + e.getMessage(), e);
        }
        return 0;
    }


    @SuppressWarnings("unchecked")
    public List<Location> getByType(Integer type) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(Location.class);
            criteria.add(Restrictions.eq("ltype", type));
            criteria.addOrder(Order.asc("lname"));
            return criteria.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getByType(type=" + type + ") query from Location: "
                    + e.getMessage(), e);
        }
        return new ArrayList<Location>();
    }
    


    @SuppressWarnings("unchecked")
    public List<Location> getByType(Integer type, int start, int numOfRows) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(Location.class);
            criteria.add(Restrictions.eq("ltype", type));
            criteria.setFirstResult(start);
            criteria.setMaxResults(numOfRows);
            return criteria.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getByType(type=" + type + ") query from Location: "
                    + e.getMessage(), e);
        }
        return new ArrayList<Location>();
    }

    public long countByType(Integer type) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(Location.class);
            criteria.add(Restrictions.eq("ltype", type));
            criteria.setProjection(Projections.rowCount());
            return ((Long) criteria.uniqueResult()).longValue(); //count
        } catch (HibernateException e) {
            logAndThrowException("Error with countBytype(type=" + type 
                    + ") query from Location: " + e.getMessage(), e);
        }
        return 0;
    }
    
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
}
