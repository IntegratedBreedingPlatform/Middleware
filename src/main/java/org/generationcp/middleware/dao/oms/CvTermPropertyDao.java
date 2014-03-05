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
package org.generationcp.middleware.dao.oms;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link CVTermProperty}.
 * 
 */
public class CvTermPropertyDao extends GenericDAO<CVTermProperty, Integer> {

    @SuppressWarnings("unchecked")
    public List<CVTermProperty> getByCvTermId(int cvTermId) throws MiddlewareQueryException {
        List<CVTermProperty> properties = new ArrayList<CVTermProperty>();
        try {
            Criteria criteria = getSession().createCriteria(getPersistentClass());
            criteria.add(Restrictions.eq("cvTermId", cvTermId));
            properties = criteria.list();
            
        } catch(HibernateException e) {
            logAndThrowException("Error at getByCvTermId=" + cvTermId + " query on CVTermPropertyDao: " + e.getMessage(), e);
        }
        return properties;
    }
    

    @SuppressWarnings("unchecked")
    public List<CVTermProperty> getByCvTermIds(List<Integer> cvTermIds) throws MiddlewareQueryException {
        List<CVTermProperty> properties = new ArrayList<CVTermProperty>();
        try {
            Criteria criteria = getSession().createCriteria(getPersistentClass());
            criteria.add(Restrictions.in("cvTermId", cvTermIds));
            properties = criteria.list();
            
        } catch(HibernateException e) {
            logAndThrowException("Error at getByCvTermIds=" + cvTermIds + " query on CVTermPropertyDao: " + e.getMessage(), e);
        }
        return properties;
    }
    
    @SuppressWarnings("unchecked")
    public List<CVTermProperty> getByCvTermAndType(int cvTermId, int typeId) throws MiddlewareQueryException {
        List<CVTermProperty> properties = new ArrayList<CVTermProperty>();
        try {
            Criteria criteria = getSession().createCriteria(getPersistentClass());
            criteria.add(Restrictions.eq("cvTermId", cvTermId));
            criteria.add(Restrictions.eq("typeId", typeId));
            properties = criteria.list();
            
        } catch(HibernateException e) {
            logAndThrowException("Error at getByCvTermId=" + cvTermId + " query on CVTermPropertyDao: " + e.getMessage(), e);
        }
        return properties;
    }

    @SuppressWarnings("unchecked")
    public CVTermProperty getOneByCvTermAndType(int cvTermId, int typeId) throws MiddlewareQueryException {
        CVTermProperty property = null;
        try {
            Criteria criteria = getSession().createCriteria(getPersistentClass());
            criteria.add(Restrictions.eq("cvTermId", cvTermId));
            criteria.add(Restrictions.eq("typeId", typeId));
            List<CVTermProperty> properties = criteria.list();
            if (properties != null && !properties.isEmpty()) {
                property = properties.get(0);
            }
            
        } catch(HibernateException e) {
            logAndThrowException("Error at getByCvTermId=" + cvTermId + " query on CVTermPropertyDao: " + e.getMessage(), e);
        }
        return property;
    }
}
