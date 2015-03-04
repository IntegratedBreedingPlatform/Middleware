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

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.hibernate.*;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class GenericDAO<T, ID extends Serializable> {

    private static final Logger LOG = LoggerFactory.getLogger(GenericDAO.class);

    protected final String SHOULD_NOT_OBSOLETE = "is_obsolete = 0";
    
    private Class<T> persistentClass;
    private Session session;

    @SuppressWarnings("unchecked")
    public GenericDAO() {
        this.persistentClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public void setSession(Session session) {
        this.session = session;
    }

    protected Session getSession() {
        return this.session;
    }

    public Class<T> getPersistentClass() {
        return this.persistentClass;
    }
    
    public T getById(ID id) throws MiddlewareQueryException {
    	return getById(id, false);
    }
    
    @SuppressWarnings("unchecked")
    public T getById(ID id, boolean lock) throws MiddlewareQueryException {
    	if (id == null) {
    		return null;
    	}
        try {
            T entity;
            if (lock) {
                entity = (T) getSession().get(getPersistentClass(), id, LockOptions.UPGRADE);
            } else {
                entity = (T) getSession().get(getPersistentClass(), id);
            }
            return entity;
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error in getById(id=" + id + "): " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    protected List<T> getByCriteria(List<Criterion> criterion) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(getPersistentClass());
            for (Criterion c : criterion) {
                criteria.add(c);
            }

            return criteria.list();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error in getByCriteria(" + criterion + "): " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<T> getAll() throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(getPersistentClass());
            return criteria.list();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error in getAll(): " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<T> getAll(int start, int numOfRows) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(getPersistentClass());
            criteria.setFirstResult(start);
            criteria.setMaxResults(numOfRows);
            return criteria.list();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error in getAll(start=" + start + ", numOfRows=" + numOfRows + "): " + e.getMessage(), e);
        }
    }

    public long countAll() throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(getPersistentClass());
            criteria.setProjection(Projections.rowCount());
            return (Long) criteria.uniqueResult();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error in countAll(): " + e.getMessage(), e);
        }
    }

    public T save(T entity) throws MiddlewareQueryException {
        try {
            getSession().save(entity);
            return entity;
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error in save(" + entity + "): " + e.getMessage(), e);
        }
    }

    public T update(T entity) throws MiddlewareQueryException {
        try {
            getSession().update(entity);
            return entity;
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error in update(entity): " + e.getMessage(), e);
        }
    }

    public T saveOrUpdate(T entity) throws MiddlewareQueryException {
        try {
            getSession().saveOrUpdate(entity);
            return entity;
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error in saveOrUpdate(entity): " + e.getMessage(), e);
        }
    }

    public T merge(T entity) throws MiddlewareQueryException {
        try {
            getSession().merge(entity);
            return entity;
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error in merge(entity): " + e.getMessage(), e);
        }
    }

    public void makeTransient(T entity) throws MiddlewareQueryException {
        try {
            getSession().delete(entity);
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error in makeTransient(" + entity + "): " + e.getMessage(), e);
        }
    }

    public void refresh(T entity) throws MiddlewareQueryException {
    	try {
            getSession().refresh(entity);
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error in refresh(" + entity + "): " + e.getMessage(), e);
        }
    }
    
    public Integer getNextId(String idName) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(getPersistentClass());
            criteria.setProjection(Projections.max(idName));
            Integer maxId = (Integer) criteria.uniqueResult();
            Integer nextId = maxId != null ? Integer.valueOf(maxId + 1) : Integer.valueOf(1);
            LOG.debug("Returning nextId " + nextId + " for entity " + getPersistentClass().getName());
            return nextId;
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error in getNextId(idName=" + idName + "): " + e.getMessage(), e);
        }
    }

    public static Integer getLastId(Session session, String tableName, String idName) throws MiddlewareQueryException {
        try {
            SQLQuery query = session.createSQLQuery("SELECT MAX(" + idName + ") FROM " + tableName);
            Integer result = (Integer) query.uniqueResult();

            return result != null ? result : 0;

        } catch(HibernateException e) {
            throw new MiddlewareQueryException("Error in getMaxId(tableName=" + tableName + ", idName=" + idName + "): " + e.getMessage(), e);
        }
    }
    
    public void flush() {
        getSession().flush();
    }

    public void clear() {
        getSession().clear();
    }
    
    public void setStartAndNumOfRows(Query query,int start, int numOfRows) {
    	if(numOfRows>0) {
        	query.setFirstResult(start);
            query.setMaxResults(numOfRows);	
        }
    }

    protected void logAndThrowException(String message, Throwable e) throws MiddlewareQueryException{
        LOG.error(message, e);
        throw new MiddlewareQueryException(message, e);
    }

    protected String getLogExceptionMessage(String methodName, String paramVar, String paramValue, String exceptionMessage, String className){
        String message = "Error with " + methodName + "(";

        if(paramVar.length()!=0){
            message += paramVar + "=" + paramValue;
        }

        message += ") query from " +className + ": " + exceptionMessage;

        return message;
    }
}
