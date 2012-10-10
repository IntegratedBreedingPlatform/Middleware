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
import org.generationcp.middleware.pojos.Method;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public class MethodDAO extends GenericDAO<Method, Integer>{

    @SuppressWarnings("unchecked")
    public List<Method> getAllMethod() throws MiddlewareQueryException {
        try {
            Query query = getSession().getNamedQuery(Method.GET_ALL);

            return (List<Method>) query.list();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getAllMethod() query from Method: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Method> getByType(String type) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(Method.class);
            criteria.add(Restrictions.eq("mtype", type));
            return criteria.list();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getMethodsByType(type=" + type + ") query from Method: " + e.getMessage(), e);
        }
    }


    @SuppressWarnings("unchecked")
    public List<Method> getByType(String type, int start, int numOfRows) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(Method.class);
            criteria.add(Restrictions.eq("mtype", type));
            criteria.setFirstResult(start);
            criteria.setMaxResults(numOfRows);            
            return criteria.list();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getMethodsByType(type=" + type + ") query from Method: " + e.getMessage(), e);
        }
    }

    public long countByType(String type) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(Method.class);
            criteria.add(Restrictions.eq("mtype", type));
            criteria.setProjection(Projections.rowCount());
            return ((Long) criteria.uniqueResult()).longValue(); // count
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with countMethodsByType(type=" + type + ") query from Method: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Method> getByGroup(String group) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(Method.class);
            criteria.add(Restrictions.eq("mgrp", group));
            return criteria.list();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getMethodsByGroup(group=" + group + ") query from Method: " + e.getMessage(), e);
        }
    }
    

    @SuppressWarnings("unchecked")
    public List<Method> getByGroup(String group, int start, int numOfRows) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(Method.class);
            criteria.add(Restrictions.eq("mgrp", group));
            criteria.setFirstResult(start);
            criteria.setMaxResults(numOfRows);            
            return criteria.list();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getMethodsByGroup(group=" + group + ") query from Method: " + e.getMessage(), e);
        }
    }
    
    public long countByGroup(String group) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(Method.class);
            criteria.add(Restrictions.eq("mgrp", group));
            criteria.setProjection(Projections.rowCount());
            return ((Long) criteria.uniqueResult()).longValue(); // count
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with countMethodsByGroup(group=" + group + ") query from Method: " + e.getMessage(), e);
        }
    }    
}
