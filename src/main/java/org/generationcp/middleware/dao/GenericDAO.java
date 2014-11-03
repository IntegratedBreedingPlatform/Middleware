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
import org.generationcp.middleware.manager.Database;
import org.hibernate.*;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class GenericDAO<T, ID extends Serializable> {

    private static final Logger LOG = LoggerFactory.getLogger(GenericDAO.class);
    private static final Set<Class<?>> WRAPPER_TYPES = getWrapperTypes();
    
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
    
    protected void logAndThrowException(String message, Throwable e) throws MiddlewareQueryException{
        LOG.error(message, e);
        throw new MiddlewareQueryException(message, e);
    }

    public T getById(ID id) throws MiddlewareQueryException {
    	return getById(id, false);
    }
    
    @SuppressWarnings("unchecked")
    public T getById(ID id, boolean lock) throws MiddlewareQueryException {
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
            Criteria crit = getSession().createCriteria(getPersistentClass());
            for (Criterion c : criterion) {
                crit.add(c);
            }

            return crit.list();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error in getByCriteria(" + criterion + "): " + e.getMessage(), e);
        }
    }

    protected Criteria getByCriteriaWithAliases(List<Criterion> criterion, Map<String, String> aliases) throws MiddlewareQueryException {
        try {
            Criteria crit = getSession().createCriteria(getPersistentClass());

            for (String field : aliases.keySet()) {
                String alias = aliases.get(field);
                crit.createAlias(field, alias);
            }

            for (Criterion c : criterion) {
                crit.add(c);
            }

            return crit;
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error in getByCriteriaWithAliases(criterion=" + criterion + ", aliases=" + aliases + "): " + e.getMessage(), e);
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
            return ((Long) criteria.uniqueResult()).longValue();
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
    
    public void evict(T entity) throws MiddlewareQueryException {
        try {
            getSession().evict(entity);
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error in evict(" + entity + "): " + e.getMessage(), e);
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
    
    public Integer getNegativeId(String idName) throws MiddlewareQueryException {
        try {
            Criteria crit = getSession().createCriteria(getPersistentClass());
            crit.setProjection(Projections.min(idName));
            Integer minId = (Integer) crit.uniqueResult();
            if (minId != null) {
                if (minId.intValue() >= 0) {
                    minId = Integer.valueOf(-1);
                } else {
                    minId = Integer.valueOf(minId.intValue() - 1);
                }
            } else {
                minId = Integer.valueOf(-1);
            }

            return minId;
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error in getNegativeId(idName=" + idName + "): " + e.getMessage(), e);
        }
    }
    
    public Integer getPositiveId(String idName) throws MiddlewareQueryException {
        try {
            Criteria crit = getSession().createCriteria(getPersistentClass());
            crit.setProjection(Projections.max(idName));
            Integer maxId = (Integer) crit.uniqueResult();
            if (maxId != null) {
                if (maxId.intValue() <= 0) {
                    maxId = Integer.valueOf(1);
                } else {
                    maxId = Integer.valueOf(maxId.intValue() + 1);
                }
            } else {
                maxId = Integer.valueOf(1);
            }

            return maxId;
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error in getPositiveId(idName=" + idName + "): " + e.getMessage(), e);
        }
    }
    
    public static Integer getLastId(Session session, Database instance, String tableName, String idName) throws MiddlewareQueryException {
    	try {
    		String selectCol = (instance == Database.LOCAL ? "MIN" : "MAX") + "(" + idName + ")";
    		SQLQuery query = session.createSQLQuery("SELECT " + selectCol + " FROM " + tableName);
    		Integer result = (Integer) query.uniqueResult();
    		
    		return result != null ? result : 0;    		
    		
    	} catch(HibernateException e) {
    		throw new MiddlewareQueryException("Error in getMaxId(instance=" + instance + ", tableName=" + tableName + ", idName=" + idName + "): " + e.getMessage(), e);
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
    
    public void setStartAndNumOfRows(Criteria criteria,int start, int numOfRows) {
    	if(numOfRows>0) {
        	criteria.setFirstResult(start);
            criteria.setMaxResults(numOfRows);	
        }
    }
    
    @SuppressWarnings("unchecked")
	public <Type> Type callStoredProcedureForObject(
            final String procedureName,
            final Map<String,Object> params,
            final Class<Type> returnType) {

        final String sql = buildSQLQuery(procedureName, params);
        LOG.debug("sql = " + sql);
        SQLQuery query = session.createSQLQuery(sql);
        if (params != null && params.size() > 0) {
        	for (Map.Entry<String,Object> entry : params.entrySet()) {
                LOG.debug(entry.getKey() + " = " + entry.getValue());
                query.setParameter(entry.getKey().toString(), entry.getValue());
            }
        }
        if(returnType!=null && !isWrapperType(returnType)) {
        	query.addEntity(returnType);
        }
        
        Object object = query.uniqueResult();
        if(object!=null && object instanceof BigInteger) {
        	BigInteger b = (BigInteger) object;
        	if(returnType.getName().equals(Integer.class.getName())) {
        		return (Type)(Integer)b.intValue();
        	} else if(returnType.getName().equals(Long.class.getName())) {
        		return (Type)(Long)b.longValue();
        	}
        	return (Type) b;
        } else if(object!=null && object instanceof BigDecimal) {
        	BigDecimal b = (BigDecimal) object;
        	if(returnType.getName().equals(Float.class.getName())) {
        		return (Type)(Float)b.floatValue();
        	} else if(returnType.getName().equals(Double.class.getName())) {
        		return (Type)(Double)b.doubleValue();
        	}
        	return (Type) b;
        } else {
        	return (Type) object;
        }
    }
    
	public void callStoredProcedure(
            final String procedureName,
            final Map<String,Object> params) {

        final String sql = buildSQLQuery(procedureName, params);
        LOG.debug("sql = " + sql);
        SQLQuery query = session.createSQLQuery(sql);
        if (params != null && params.size() > 0) {
        	for (Map.Entry<String,Object> entry : params.entrySet()) {
                LOG.debug(entry.getKey() + " = " + entry.getValue());
                query.setParameter(entry.getKey().toString(), entry.getValue());
            }
        }
        
        query.executeUpdate();
        

        
    }
	
	@SuppressWarnings("unchecked")
	public <Type> List<Type> callStoredProcedureForList(
            final String procedureName,
            final Map<String,Object> params,
            final Class<Type> returnType) {

        final String sql = buildSQLQuery(procedureName, params);
        LOG.debug("sql = " + sql);
        SQLQuery query = session.createSQLQuery(sql);
        if (params != null && params.size() > 0) {
        	for (Map.Entry<String,Object> entry : params.entrySet()) {
                LOG.debug(entry.getKey() + " = " + entry.getValue());
                query.setParameter(entry.getKey().toString(), entry.getValue());
            }
        }
        if(returnType!=null && !isWrapperType(returnType)) {
        	query.addEntity(returnType);
        }

        return query.list();
    }

    private String buildSQLQuery(String procedureName, Map<String,Object> params) {
        StringBuilder sql = new StringBuilder();
        sql.append("CALL ");
        sql.append(procedureName);
        sql.append("(");

        if (params != null && !params.isEmpty()) {
            boolean start = true;
            for (String paramName : params.keySet()) {
                if (!start) {
                    sql.append(", ");
                } else {
                    start = false;
                }
                sql.append(":");
                sql.append(paramName);
            }
        }

        sql.append(")");
        return sql.toString();
    }
    
    private static boolean isWrapperType(Class<?> clazz)
    {
        return WRAPPER_TYPES.contains(clazz);
    }
    
    private static Set<Class<?>> getWrapperTypes()
    {
        Set<Class<?>> ret = new HashSet<Class<?>>();
        ret.add(Boolean.class);
        ret.add(Character.class);
        ret.add(Byte.class);
        ret.add(Short.class);
        ret.add(Integer.class);
        ret.add(Long.class);
        ret.add(Float.class);
        ret.add(Double.class);
        ret.add(Void.class);
        ret.add(String.class);
        return ret;
    }
}
