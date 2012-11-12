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

import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Location;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
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
            throw new MiddlewareQueryException("Error with getByName(name=" + name + ", operation=" + operation + ") query from Location: "
                    + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Location> getByName(String name, int start, int numOfRows, Operation operation) throws MiddlewareQueryException {
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
            throw new MiddlewareQueryException("Error with getByName(name=" + name + ", operation=" + operation + ") query from Location: "
                    + e.getMessage(), e);
        }
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
            throw new MiddlewareQueryException("Error with countByName(name=" + name + ", operation=" + operation
                    + ") query from Location: " + e.getMessage(), e);
        }
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
            throw new MiddlewareQueryException("Error with getByCountry(country=" + country + ") query from Location: "
                    + e.getMessage(), e);
        }
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
            throw new MiddlewareQueryException("Error with getByCountry(country=" + country + ") query from Location: "
                    + e.getMessage(), e);
        }
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
            throw new MiddlewareQueryException("Error with getByCountry(country=" + country + ") query from Location: "
                    + e.getMessage(), e);
        }
    }

    public long countByCountry(Country country) throws MiddlewareQueryException {
        Integer countryId = country.getCntryid();
        try {
            Criteria criteria = getSession().createCriteria(Location.class);
            criteria.add(Restrictions.eq("cntryid", countryId));
            criteria.setProjection(Projections.rowCount());
            return ((Long) criteria.uniqueResult()).longValue(); //count
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with countByCountry(country=" + country 
                    + ") query from Location: " + e.getMessage(), e);
        }
    }


    @SuppressWarnings("unchecked")
    public List<Location> getByType(Integer type) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(Location.class);
            criteria.add(Restrictions.eq("ltype", type));
            criteria.addOrder(Order.asc("lname"));
            return criteria.list();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getByType(type=" + type + ") query from Location: "
                    + e.getMessage(), e);
        }
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
            throw new MiddlewareQueryException("Error with getByType(type=" + type + ") query from Location: "
                    + e.getMessage(), e);
        }
    }

    public long countByType(Integer type) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(Location.class);
            criteria.add(Restrictions.eq("ltype", type));
            criteria.setProjection(Projections.rowCount());
            return ((Long) criteria.uniqueResult()).longValue(); //count
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with countBytype(type=" + type 
                    + ") query from Location: " + e.getMessage(), e);
        }
    }
}
