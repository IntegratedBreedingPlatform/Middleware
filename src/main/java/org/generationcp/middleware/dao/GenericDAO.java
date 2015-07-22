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

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Database;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class GenericDAO<T, ID extends Serializable> {

	private static final Logger LOG = LoggerFactory.getLogger(GenericDAO.class);
	private final Class<T> persistentClass;
	private Session session;

	@SuppressWarnings("unchecked")
	public GenericDAO() {
		this.persistentClass = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
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

	protected void logAndThrowException(String message, Throwable e) throws MiddlewareQueryException {
		GenericDAO.LOG.error(message, e);
		throw new MiddlewareQueryException(message, e);
	}

	protected String getLogExceptionMessage(String methodName, String paramVar, String paramValue, String exceptionMessage, String className) {
		String message = "Error with " + methodName + "(";

		if (paramVar.length() != 0) {
			message += paramVar + "=" + paramValue;
		}

		message += ") query from " + className + ": " + exceptionMessage;

		return message;
	}

	public T getById(ID id) throws MiddlewareQueryException {
		return this.getById(id, false);
	}

	@SuppressWarnings("unchecked")
	public T getById(ID id, boolean lock) throws MiddlewareQueryException {
		if (id == null) {
			return null;
		}
		try {
			T entity;
			if (lock) {
				entity = (T) this.getSession().get(this.getPersistentClass(), id, LockOptions.UPGRADE);
			} else {
				entity = (T) this.getSession().get(this.getPersistentClass(), id);
			}
			return entity;
		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error in getById(id=" + id + "): " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	protected List<T> getByCriteria(List<Criterion> criterion) throws MiddlewareQueryException {
		try {
			Criteria crit = this.getSession().createCriteria(this.getPersistentClass());
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
			Criteria crit = this.getSession().createCriteria(this.getPersistentClass());

			for (String field : aliases.keySet()) {
				String alias = aliases.get(field);
				crit.createAlias(field, alias);
			}

			for (Criterion c : criterion) {
				crit.add(c);
			}

			return crit;
		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error in getByCriteriaWithAliases(criterion=" + criterion + ", aliases=" + aliases + "): "
					+ e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<T> getAll() throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			return criteria.list();
		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error in getAll(): " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<T> getAll(int start, int numOfRows) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.setFirstResult(start);
			criteria.setMaxResults(numOfRows);
			return criteria.list();
		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error in getAll(start=" + start + ", numOfRows=" + numOfRows + "): " + e.getMessage(), e);
		}
	}

	public long countAll() throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.setProjection(Projections.rowCount());
			return ((Long) criteria.uniqueResult()).longValue();
		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error in countAll(): " + e.getMessage(), e);
		}
	}

	public T save(T entity) throws MiddlewareQueryException {
		try {
			this.getSession().save(entity);
			return entity;
		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error in save(" + entity + "): " + e.getMessage(), e);
		}
	}

	public T update(T entity) throws MiddlewareQueryException {
		try {
			this.getSession().update(entity);
			return entity;
		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error in update(entity): " + e.getMessage(), e);
		}
	}

	public T saveOrUpdate(T entity) throws MiddlewareQueryException {
		try {
			this.getSession().saveOrUpdate(entity);
			return entity;
		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error in saveOrUpdate(entity): " + e.getMessage(), e);
		}
	}

	public T merge(T entity) throws MiddlewareQueryException {
		try {
			this.getSession().merge(entity);
			return entity;
		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error in merge(entity): " + e.getMessage(), e);
		}
	}

	public void evict(T entity) throws MiddlewareQueryException {
		try {
			this.getSession().evict(entity);
		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error in evict(" + entity + "): " + e.getMessage(), e);
		}
	}

	public void makeTransient(T entity) throws MiddlewareQueryException {
		try {
			this.getSession().delete(entity);
		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error in makeTransient(" + entity + "): " + e.getMessage(), e);
		}
	}

	public void refresh(T entity) throws MiddlewareQueryException {
		try {
			this.getSession().refresh(entity);
		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error in refresh(" + entity + "): " + e.getMessage(), e);
		}
	}

	public static Integer getLastId(Session session, Database instance, String tableName, String idName) throws MiddlewareQueryException {
		try {
			SQLQuery query = session.createSQLQuery("SELECT MAX(" + idName + ") FROM " + tableName);
			Integer result = (Integer) query.uniqueResult();

			return result != null ? result : 0;

		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error in getMaxId(instance=" + instance + ", tableName=" + tableName + ", idName=" + idName
					+ "): " + e.getMessage(), e);
		}
	}

	public void flush() {
		this.getSession().flush();
	}

	public void clear() {
		this.getSession().clear();
	}

	public void setStartAndNumOfRows(Query query, int start, int numOfRows) {
		if (numOfRows > 0) {
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);
		}
	}

	private static Set<Class<?>> getWrapperTypes() {
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
