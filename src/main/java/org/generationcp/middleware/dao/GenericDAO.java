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

import org.generationcp.middleware.domain.sqlfilter.SqlTextFilter;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public abstract class GenericDAO<T, ID extends Serializable> {

	private static final Logger LOG = LoggerFactory.getLogger(GenericDAO.class);

	private final Class<T> persistentClass;
	private Session session;

	/**
	 * @deprecated please, instead use {@link #GenericDAO(Session)}}
	 */
	@Deprecated
	@SuppressWarnings("unchecked")
	public GenericDAO() {
		this.persistentClass = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	public GenericDAO(final Session session) {
		this.persistentClass = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		this.session = session;
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

	public T getById(final ID id) {
		return this.getById(id, false);
	}

	@SuppressWarnings("unchecked")
	public T getById(final ID id, final boolean lock) {
		if (id == null) {
			return null;
		}
		try {
			final T entity;
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

	public List<T> filterByColumnValue(final String columnName, final Object value) {
		Criterion criterion = value == null ? Restrictions.isNull(columnName) : Restrictions.eq(columnName, value);
		return this.getByCriteria(new ArrayList<>(Collections.singletonList(criterion)));
	}

	public List<T> filterByColumnValues(String columnName, List<?> values) throws MiddlewareQueryException {
		if (values == null || values.isEmpty()) {
			return new ArrayList<>();
		}
		return this.getByCriteria(new ArrayList<>(Collections.singletonList(Restrictions.in(columnName, values))));
	}

	@SuppressWarnings("unchecked")
	protected List<T> getByCriteria(List<Criterion> criterion) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
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
			return (Long) criteria.uniqueResult();
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

	public void setStartAndNumOfRows(Query query, int start, int numOfRows) {
		if (numOfRows > 0) {
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);
		}
	}

	@Deprecated
	protected void logAndThrowException(String message, Throwable e) throws MiddlewareQueryException {
		GenericDAO.LOG.error(message, e);
		throw new MiddlewareQueryException(message, e);
	}

	protected String getLogExceptionMessage(String methodName, String paramVar, String paramValue, String exceptionMessage,
		String className) {
		String message = "Error with " + methodName + "(";

		if (paramVar.length() != 0) {
			message += paramVar + "=" + paramValue;
		}

		message += ") query from " + className + ": " + exceptionMessage;

		return message;
	}

	private final static int PARAMETER_LIMIT = 999;

	/**
	 * An utility method to build the Criterion Query IN clause if the number of parameter values passed has a size more than 1000. Oracle
	 * does not allow more than 1000 parameter values in a IN clause. maximum number of expressions in a list is 1000'.
	 *
	 * @param propertyName The name of property
	 * @param values       List to be passed in clause
	 * @return Criterion
	 */
	public static Criterion buildInCriterion(String propertyName, List values) {

		Criterion criterion = null;

		int listSize = values.size();
		for (int i = 0; i < listSize; i += GenericDAO.PARAMETER_LIMIT) {
			List subList;
			if (listSize > i + GenericDAO.PARAMETER_LIMIT) {
				subList = values.subList(i, i + GenericDAO.PARAMETER_LIMIT);
			} else {
				subList = values.subList(i, listSize);
			}
			if (criterion != null) {
				criterion = Restrictions.or(criterion, Restrictions.in(propertyName, subList));
			} else {
				criterion = Restrictions.in(propertyName, subList);
			}
		}
		return criterion;
	}

	/**
	 * addOrder to criteria for each pageable.getSort
	 *
	 * @param criteria
	 * @param pageable
	 */
	public static void addOrder(final Criteria criteria, final Pageable pageable) {
		if (pageable == null || pageable.getSort() == null) {
			return;
		}
		for (Sort.Order order : pageable.getSort()) {
			switch (order.getDirection()) {
				case ASC:
					criteria.addOrder(Order.asc(order.getProperty()));
					break;
				case DESC:
					criteria.addOrder(Order.desc(order.getProperty()));
			}
		}
	}

	public static void addPagination(final Criteria criteria, final Pageable pageable) {
		if (pageable == null || criteria == null) {
			return;
		}
		criteria.setFirstResult(pageable.getPageSize() * pageable.getPageNumber());
		criteria.setMaxResults(pageable.getPageSize());
	}

	/**
	 * addPagination to criteria
	 *
	 * @param pageable
	 */
	public static void addPaginationToSQLQuery(final SQLQuery query, final Pageable pageable) {
		if (pageable == null || query == null) {
			return;
		}
		query.setFirstResult(pageable.getPageSize() * pageable.getPageNumber());
		query.setMaxResults(pageable.getPageSize());
	}

	public static void addPageRequestOrderBy(final StringBuilder query, final Pageable pageable) {
		if (pageable == null || pageable.getSort() == null || query == null) {
			return;
		}
		final Iterator<Sort.Order> iterator = pageable.getSort().iterator();
		query.append(" ORDER BY ");
		while (iterator.hasNext()) {
			final Sort.Order order = iterator.next();
			query.append(order.getProperty() + " " + order.getDirection().name() + (iterator.hasNext() ? "," : ""));
		}
	}

	public static void addPageRequestOrderBy(final StringBuilder query, final Pageable pageable, final List<String> sortableFields) {
		if (pageable == null || pageable.getSort() == null || query == null || sortableFields == null) {
			return;
		}
		final Iterator<Sort.Order> iterator = pageable.getSort().iterator();
		query.append(" ORDER BY ");
		while (iterator.hasNext()) {
			final Sort.Order order = iterator.next();
			if (sortableFields.contains(order.getProperty())) {
				query.append(order.getProperty() + " " + order.getDirection().name() + (iterator.hasNext() ? "," : ""));
			}
		}
	}

	public static String getOperator(final SqlTextFilter.Type filterType) {
		if (SqlTextFilter.Type.EXACTMATCH.equals(filterType)) {
			return "=";
		} else {
			return "LIKE";
		}
	}

	public static String getParameter(final SqlTextFilter.Type type, final String value) {
		if (type == null) {
			return value;
		}
		switch (type) {
			case STARTSWITH:
				return value + "%";
			case ENDSWITH:
				return "%" + value;
			case CONTAINS:
				return "%" + value + "%";
			case EXACTMATCH:
			default:
				return value;
		}
	}

}
