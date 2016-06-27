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

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.Method;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DAO class for {@link Method}.
 *
 */
public class MethodDAO extends GenericDAO<Method, Integer> {

	private static final String UNIQUE_ID = "uniqueID";

	private static final Logger LOG = LoggerFactory.getLogger(MethodDAO.class);

	@SuppressWarnings("unchecked")
	public List<Method> getMethodsByIds(List<Integer> ids) throws MiddlewareQueryException {
		try {
			return this.getSession().createCriteria(Method.class).add(Restrictions.in("mid", ids)).addOrder(Order.asc("mname")).list();
		} catch (HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage("getMethodsByIds", "", null, e.getMessage(), "Method"), e);
		}

		return new ArrayList<Method>();
	}

	@SuppressWarnings("unchecked")
	public List<Method> getAllMethod() throws MiddlewareQueryException {
		try {
			Query query = this.getSession().getNamedQuery(Method.GET_ALL);
			return query.list();
		} catch (HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage("getAllMethod", "", null, e.getMessage(), "Method"), e);
		}
		return new ArrayList<Method>();
	}

	@SuppressWarnings("unchecked")
	public List<Method> getByUniqueID(String programUUID) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(Method.class);
			criteria.add(Restrictions.or(Restrictions.eq(MethodDAO.UNIQUE_ID, programUUID), Restrictions.isNull(MethodDAO.UNIQUE_ID)));
			criteria.addOrder(Order.asc("mname"));
			return criteria.list();
		} catch (HibernateException e) {
			this.logAndThrowException(
					this.getLogExceptionMessage("getMethodsByType", "programUUID", programUUID, e.getMessage(), "Method"), e);
		}
		return new ArrayList<Method>();
	}

	@SuppressWarnings("unchecked")
	public List<Method> getByType(String type) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(Method.class);
			criteria.add(Restrictions.eq("mtype", type));
			criteria.addOrder(Order.asc("mname"));
			return criteria.list();
		} catch (HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage("getMethodsByType", "type", type, e.getMessage(), "Method"), e);
		}
		return new ArrayList<Method>();
	}

	@SuppressWarnings("unchecked")
	public List<Method> getByType(String type, String programUUID) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(Method.class);
			criteria.add(Restrictions.eq("mtype", type));
			criteria.add(Restrictions.or(Restrictions.eq(MethodDAO.UNIQUE_ID, programUUID), Restrictions.isNull(MethodDAO.UNIQUE_ID)));
			criteria.addOrder(Order.asc("mname"));
			return criteria.list();
		} catch (HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage("getMethodsByType", "type", type, e.getMessage(), "Method"), e);
		}
		return new ArrayList<Method>();
	}

	@SuppressWarnings("unchecked")
	public List<Method> getByType(String type, int start, int numOfRows) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(Method.class);
			criteria.add(Restrictions.eq("mtype", type));
			criteria.addOrder(Order.asc("mname"));
			criteria.setFirstResult(start);
			criteria.setMaxResults(numOfRows);
			return criteria.list();
		} catch (HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage("getMethodsByType", "type", type, e.getMessage(), "Method"), e);
		}
		return new ArrayList<Method>();
	}

	public long countByType(String type) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(Method.class);
			criteria.add(Restrictions.eq("mtype", type));
			criteria.setProjection(Projections.rowCount());
			return ((Long) criteria.uniqueResult()).longValue(); // count
		} catch (HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage("countMethodsByType", "type", type, e.getMessage(), "Method"), e);
		}
		return 0;
	}

	public long countByType(String type, String programUUID) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(Method.class);
			criteria.add(Restrictions.eq("mtype", type));
			criteria.add(Restrictions.or(Restrictions.eq(MethodDAO.UNIQUE_ID, programUUID), Restrictions.isNull(MethodDAO.UNIQUE_ID)));
			criteria.setProjection(Projections.rowCount());
			return ((Long) criteria.uniqueResult()).longValue(); // count
		} catch (HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage("countMethodsByType", "type", type, e.getMessage(), "Method"), e);
		}
		return 0;
	}

	public long countByUniqueID(String programUUID) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(Method.class);
			criteria.add(Restrictions.or(Restrictions.eq(MethodDAO.UNIQUE_ID, programUUID), Restrictions.isNull(MethodDAO.UNIQUE_ID)));
			criteria.setProjection(Projections.rowCount());
			return ((Long) criteria.uniqueResult()).longValue(); // count
		} catch (HibernateException e) {
			this.logAndThrowException(
					this.getLogExceptionMessage("countMethodsByType", "programUUID", programUUID, e.getMessage(), "Method"), e);
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	public List<Method> getByGroup(String group) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(Method.class);
			criteria.add(Restrictions.eq("mgrp", group));
			criteria.addOrder(Order.asc("mname"));
			return criteria.list();
		} catch (HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage("getMethodsByGroup", "group", group, e.getMessage(), "Method"), e);
		}
		return new ArrayList<Method>();
	}

	@SuppressWarnings("unchecked")
	public List<Method> getByGroupIncludesGgroup(String group) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(Method.class);
			Criterion group1 = Restrictions.eq("mgrp", group);
			Criterion group2 = Restrictions.eq("mgrp", "G");
			LogicalExpression orExp = Restrictions.or(group1, group2);
			criteria.add(orExp);
			criteria.addOrder(Order.asc("mname"));
			return criteria.list();
		} catch (HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage("getMethodsByGroup", "group", group, e.getMessage(), "Method"), e);
		}
		return new ArrayList<Method>();
	}

	@SuppressWarnings("unchecked")
	public List<Method> getByGroup(String group, int start, int numOfRows) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(Method.class);
			criteria.add(Restrictions.eq("mgrp", group));
			criteria.addOrder(Order.asc("mname"));
			criteria.setFirstResult(start);
			criteria.setMaxResults(numOfRows);
			return criteria.list();
		} catch (HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage("getMethodsByGroup", "group", group, e.getMessage(), "Method"), e);
		}
		return new ArrayList<Method>();
	}

	@SuppressWarnings("unchecked")
	public List<Method> getByGroupAndType(String group, String type) throws MiddlewareQueryException {
		try {

			Criteria criteria = this.getSession().createCriteria(Method.class);
			Criterion group1 = Restrictions.eq("mgrp", group);
			Criterion group2 = Restrictions.eq("mgrp", "G");
			LogicalExpression orExp = Restrictions.or(group1, group2);
			Criterion filterType = Restrictions.eq("mtype", type);
			LogicalExpression andExp = Restrictions.and(orExp, filterType);

			criteria.add(andExp);
			criteria.addOrder(Order.asc("mname"));
			return criteria.list();
		} catch (HibernateException e) {
			this.logAndThrowException(
					this.getLogExceptionMessage("getMethodsByGroupAndType", "group|type", group + "|" + type, e.getMessage(), "Method"), e);
		}
		return new ArrayList<Method>();
	}

	@SuppressWarnings("unchecked")
	public List<Method> getByGroupAndTypeAndName(String group, String type, String name) throws MiddlewareQueryException {
		try {

			Criteria criteria = this.getSession().createCriteria(Method.class);

			if (type != null && !type.isEmpty()) {
				criteria.add(Restrictions.eq("mtype", type));
			}

			if (name != null && !name.isEmpty()) {
				criteria.add(Restrictions.like("mname", "%" + name.trim() + "%"));
			}

			if (group != null && !group.isEmpty()) {
				Criterion group1 = Restrictions.eq("mgrp", group);
				Criterion group2 = Restrictions.eq("mgrp", "G");
				LogicalExpression orExp = Restrictions.or(group1, group2);

				criteria.add(orExp);
			}

			criteria.addOrder(Order.asc("mname"));
			return criteria.list();
		} catch (HibernateException e) {
			this.logAndThrowException(
					this.getLogExceptionMessage("getMethodsByGroupAndType", "group|type", group + "|" + type, e.getMessage(), "Method"), e);
		}
		return new ArrayList<Method>();
	}

	@SuppressWarnings("unchecked")
	public List<Method> getAllMethodsNotGenerative() throws MiddlewareQueryException {
		try {
			List<Integer> validMethodClasses = new ArrayList<Integer>();
			validMethodClasses.addAll(Method.BULKED_CLASSES);
			validMethodClasses.addAll(Method.NON_BULKED_CLASSES);

			Criteria criteria = this.getSession().createCriteria(Method.class);
			criteria.add(Restrictions.ne("mtype", "GEN"));
			criteria.add(Restrictions.in("geneq", validMethodClasses));
			criteria.addOrder(Order.asc("mname"));

			return criteria.list();
		} catch (HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage("getAllMethodsNotGenerative", "", null, e.getMessage(), "Method"), e);
		}
		return new ArrayList<Method>();
	}

	@SuppressWarnings("unchecked")
	public List<Method> getMethodsNotGenerativeById(final List<Integer> ids) throws MiddlewareQueryException {
		try {
			final List<Integer> validMethodClasses = new ArrayList<>();
			validMethodClasses.addAll(Method.BULKED_CLASSES);
			validMethodClasses.addAll(Method.NON_BULKED_CLASSES);

			final Criteria criteria = this.getSession().createCriteria(Method.class);
			criteria.add(Restrictions.ne("mtype", "GEN"));
			criteria.add(Restrictions.in("geneq", validMethodClasses));
			if (ids.size() > 0) {
				criteria.add(Restrictions.in("mid", ids));
			}
			criteria.addOrder(Order.asc("mname"));

			return criteria.list();
		} catch (final HibernateException e) {
			MethodDAO.LOG.error(this.getLogExceptionMessage("getMethodsNotGenerativeById", "", null, e.getMessage(), "Method"), e);
			throw new MiddlewareQueryException(this.getLogExceptionMessage("getMethodsNotGenerativeById", "", null, e.getMessage(),
					"Method"), e);
		}
	}

	public long countByGroup(String group) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(Method.class);
			criteria.add(Restrictions.eq("mgrp", group));
			criteria.setProjection(Projections.rowCount());
			return ((Long) criteria.uniqueResult()).longValue(); // count
		} catch (HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage("countMethodsByGroup", "group", group, e.getMessage(), "Method"), e);
		}
		return 0;
	}

	public Method getByCode(String code, String programUUID) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(Method.class);
			criteria.add(Restrictions.eq("mcode", code));
			criteria.add(Restrictions.or(Restrictions.eq(MethodDAO.UNIQUE_ID, programUUID), Restrictions.isNull(MethodDAO.UNIQUE_ID)));
			return (Method) criteria.uniqueResult();
		} catch (HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage("getMethodsByCode", "code", code, e.getMessage(), "Method"), e);
		}
		return new Method();
	}

	public Method getByCode(String code) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(Method.class);
			criteria.add(Restrictions.eq("mcode", code));
			return (Method) criteria.uniqueResult();
		} catch (HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage("getMethodsByCode", "code", code, e.getMessage(), "Method"), e);
		}
		return new Method();
	}

	@SuppressWarnings("unchecked")
	public List<Method> getByName(String name) throws MiddlewareQueryException {
		List<Method> methods = new ArrayList<Method>();
		try {
			StringBuilder queryString = new StringBuilder();
			queryString.append(
					"SELECT mid, mtype, mgrp, mcode, mname, mdesc, mref, mprgn, mfprg, mattr, geneq, muid, lmid, mdate, program_uuid ")
					.append("FROM methods m WHERE m.mname = :mname");
			SQLQuery query = this.getSession().createSQLQuery(queryString.toString());
			query.setParameter("mname", name);

			List<Object[]> list = query.list();

			for (Object[] row : list) {
				Integer mid = (Integer) row[0];
				String mtype = (String) row[1];
				String mgrp = (String) row[2];
				String mcode = (String) row[3];
				String mname = (String) row[4];
				String mdesc = (String) row[5];
				Integer mref = (Integer) row[6];
				Integer mprgn = (Integer) row[7];
				Integer mfprg = (Integer) row[8];
				Integer mattr = (Integer) row[9];
				Integer geneq = (Integer) row[10];
				Integer muid = (Integer) row[11];
				Integer lmid = (Integer) row[12];
				Integer mdate = (Integer) row[13];
				String programUUID = (String) row[14];

				Method method =
						new Method(mid, mtype, mgrp, mcode, mname, mdesc, mref, mprgn, mfprg, mattr, geneq, muid, lmid, mdate, programUUID);
				methods.add(method);
			}

			return methods;
		} catch (HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage("getByName", "name", name, e.getMessage(), "Method"), e);
		}
		return new ArrayList<Method>();
	}

	@SuppressWarnings("unchecked")
	public List<Method> getByName(String name, String uniqueId) throws MiddlewareQueryException {
		List<Method> methods = new ArrayList<Method>();
		try {
			StringBuilder queryString = new StringBuilder();
			queryString
					.append("SELECT mid, mtype, mgrp, mcode, mname, mdesc, mref, mprgn, mfprg, mattr, geneq, muid, lmid, mdate, program_uuid ")
					.append("FROM methods m WHERE m.mname = :mname ").append("AND m.program_uuid = :uniqueId");
			SQLQuery query = this.getSession().createSQLQuery(queryString.toString());
			query.setParameter("mname", name);
			query.setParameter("uniqueId", uniqueId);

			List<Object[]> list = query.list();

			for (Object[] row : list) {
				Integer mid = (Integer) row[0];
				String mtype = (String) row[1];
				String mgrp = (String) row[2];
				String mcode = (String) row[3];
				String mname = (String) row[4];
				String mdesc = (String) row[5];
				Integer mref = (Integer) row[6];
				Integer mprgn = (Integer) row[7];
				Integer mfprg = (Integer) row[8];
				Integer mattr = (Integer) row[9];
				Integer geneq = (Integer) row[10];
				Integer muid = (Integer) row[11];
				Integer lmid = (Integer) row[12];
				Integer mdate = (Integer) row[13];
				String programUUID = (String) row[14];

				Method method =
						new Method(mid, mtype, mgrp, mcode, mname, mdesc, mref, mprgn, mfprg, mattr, geneq, muid, lmid, mdate, programUUID);
				methods.add(method);
			}

			return methods;
		} catch (HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage("getByName", "name", name, e.getMessage(), "Method"), e);
		}
		return new ArrayList<Method>();
	}

	@SuppressWarnings("unchecked")
	public List<Method> getProgramMethods(String programUUID) throws MiddlewareQueryException {
		List<Method> method = new ArrayList<Method>();
		try {
			Criteria criteria = this.getSession().createCriteria(Method.class);
			criteria.add(Restrictions.eq(MethodDAO.UNIQUE_ID, programUUID));
			method = criteria.list();
		} catch (HibernateException e) {
			this.logAndThrowException(
					this.getLogExceptionMessage("getProgramMethods", "programUUID", programUUID, e.getMessage(), "Method"), e);
		}
		return method;
	}
	
	@SuppressWarnings("unchecked")
	public List<Method> getDerivativeAndMaintenanceMethods(final List<Integer> ids) throws MiddlewareQueryException {
		try {
			final List<Integer> validMethodClasses = new ArrayList<Integer>();
			validMethodClasses.addAll(Method.BULKED_CLASSES);
			validMethodClasses.addAll(Method.NON_BULKED_CLASSES);

			final Criteria criteria = this.getSession().createCriteria(Method.class);
			final SimpleExpression der = Restrictions.eq("mtype", "DER");
			final SimpleExpression man = Restrictions.eq("mtype", "MAN");
			criteria.add(Restrictions.or(der, man));
			if (ids.size() > 0) {
				criteria.add(Restrictions.in("mid", ids));
			}
			criteria.add(Restrictions.in("geneq", validMethodClasses));
			criteria.addOrder(Order.asc("mname"));

			return criteria.list();
		} catch (final HibernateException e) {
			MethodDAO.LOG.error(this.getLogExceptionMessage("getMethodsNotGenerativeById", "", null, e.getMessage(), "Method"), e);
			throw new MiddlewareQueryException(
					this.getLogExceptionMessage("getAllMethodsDerivativeAndManintenance", "", null, e.getMessage(), "Method"), e);
		}
	}
}
