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

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.MethodType;
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
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * DAO class for {@link Method}.
 */
public class MethodDAO extends GenericDAO<Method, Integer> {

	private static final String UNIQUE_ID = "uniqueID";
	private static final String METHOD_NAME = "mname";

	private static final Logger LOG = LoggerFactory.getLogger(MethodDAO.class);

	@SuppressWarnings("unchecked")
	public List<Method> getMethodsByIds(final List<Integer> ids) {
		try {
			return this.getSession().createCriteria(Method.class).add(Restrictions.in("mid", ids)).addOrder(Order.asc(METHOD_NAME)).list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(this.getLogExceptionMessage("getMethodsByIds", "", null, e.getMessage(), "Method"), e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Method> getAllMethod() {
		try {
			final Query query = this.getSession().getNamedQuery(Method.GET_ALL);
			return query.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(this.getLogExceptionMessage("getAllMethod", "", null, e.getMessage(), "Method"), e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Method> getAllMethodOrderByMname() {
		try {
			final Criteria criteria = this.getSession().createCriteria(Method.class);
			criteria.addOrder(Order.asc(METHOD_NAME));
			return criteria.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(this.getLogExceptionMessage("getAllMethodOrderByMname", "", null, e.getMessage(), "Method"),
				e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Method> getByUniqueID(final String programUUID) {
		try {
			final Criteria criteria = this.getSession().createCriteria(Method.class);
			criteria.add(Restrictions.or(Restrictions.eq(MethodDAO.UNIQUE_ID, programUUID), Restrictions.isNull(MethodDAO.UNIQUE_ID)));
			criteria.addOrder(Order.asc(METHOD_NAME));
			return criteria.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				this.getLogExceptionMessage("getMethodsByType", "programUUID", programUUID, e.getMessage(), "Method"), e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Method> getByType(final String type) {
		try {
			final Criteria criteria = this.getSession().createCriteria(Method.class);
			criteria.add(Restrictions.eq("mtype", type));
			criteria.addOrder(Order.asc(METHOD_NAME));
			return criteria.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(this.getLogExceptionMessage("getMethodsByType", "type", type, e.getMessage(), "Method"), e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Method> getByType(final String type, final String programUUID) {
		try {
			final Criteria criteria = this.getSession().createCriteria(Method.class);
			criteria.add(Restrictions.eq("mtype", type));
			criteria.add(Restrictions.or(Restrictions.eq(MethodDAO.UNIQUE_ID, programUUID), Restrictions.isNull(MethodDAO.UNIQUE_ID)));
			criteria.addOrder(Order.asc(METHOD_NAME));
			return criteria.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(this.getLogExceptionMessage("getMethodsByType", "type", type, e.getMessage(), "Method"), e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Method> getByType(final String type, final int start, final int numOfRows) {
		try {
			final Criteria criteria = this.getSession().createCriteria(Method.class);
			criteria.add(Restrictions.eq("mtype", type));
			criteria.addOrder(Order.asc(METHOD_NAME));
			criteria.setFirstResult(start);
			criteria.setMaxResults(numOfRows);
			return criteria.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(this.getLogExceptionMessage("getMethodsByType", "type", type, e.getMessage(), "Method"), e);
		}
	}

	public List<String> getNonGenerativeMethodCodes(final Set<String> methodCodes) {
		final List<String> methodsCodes = new ArrayList<String>();
		final StringBuilder queryString = new StringBuilder();
		queryString.append("SELECT mcode FROM methods WHERE mcode IN (:methodCodes) AND mtype <> :generativeType");
		final SQLQuery query = this.getSession().createSQLQuery(queryString.toString());
		query.setParameterList("methodCodes", methodCodes);
		query.setParameter("generativeType", MethodType.GENERATIVE.getCode());

		methodsCodes.addAll(query.list());
		return methodsCodes;
	}

	public long countByType(final String type) throws MiddlewareQueryException {
		try {
			final Criteria criteria = this.getSession().createCriteria(Method.class);
			criteria.add(Restrictions.eq("mtype", type));
			criteria.setProjection(Projections.rowCount());
			return ((Long) criteria.uniqueResult()).longValue(); // count
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(this.getLogExceptionMessage("countMethodsByType", "type", type, e.getMessage(), "Method"),
				e);
		}
	}

	public long countByType(final String type, final String programUUID) {
		try {
			final Criteria criteria = this.getSession().createCriteria(Method.class);
			criteria.add(Restrictions.eq("mtype", type));
			criteria.add(Restrictions.or(Restrictions.eq(MethodDAO.UNIQUE_ID, programUUID), Restrictions.isNull(MethodDAO.UNIQUE_ID)));
			criteria.setProjection(Projections.rowCount());
			return ((Long) criteria.uniqueResult()).longValue(); // count
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(this.getLogExceptionMessage("countMethodsByType", "type", type, e.getMessage(), "Method"),
				e);
		}
	}

	public long countByUniqueID(final String programUUID) {
		try {
			final Criteria criteria = this.getSession().createCriteria(Method.class);
			criteria.add(Restrictions.or(Restrictions.eq(MethodDAO.UNIQUE_ID, programUUID), Restrictions.isNull(MethodDAO.UNIQUE_ID)));
			criteria.setProjection(Projections.rowCount());
			return ((Long) criteria.uniqueResult()).longValue(); // count
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				this.getLogExceptionMessage("countMethodsByType", "programUUID", programUUID, e.getMessage(), "Method"), e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Method> getByGroup(final String group) {
		try {
			final Criteria criteria = this.getSession().createCriteria(Method.class);
			criteria.add(Restrictions.eq("mgrp", group));
			criteria.addOrder(Order.asc(METHOD_NAME));
			return criteria.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(this.getLogExceptionMessage("getMethodsByGroup", "group", group, e.getMessage(), "Method"),
				e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Method> getByGroupIncludesGgroup(final String group) {
		try {
			final Criteria criteria = this.getSession().createCriteria(Method.class);
			final Criterion group1 = Restrictions.eq("mgrp", group);
			final Criterion group2 = Restrictions.eq("mgrp", "G");
			final LogicalExpression orExp = Restrictions.or(group1, group2);
			criteria.add(orExp);
			criteria.addOrder(Order.asc(METHOD_NAME));
			return criteria.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(this.getLogExceptionMessage("getMethodsByGroup", "group", group, e.getMessage(), "Method"),
				e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Method> getByGroup(final String group, final int start, final int numOfRows) {
		try {
			final Criteria criteria = this.getSession().createCriteria(Method.class);
			criteria.add(Restrictions.eq("mgrp", group));
			criteria.addOrder(Order.asc(METHOD_NAME));
			criteria.setFirstResult(start);
			criteria.setMaxResults(numOfRows);
			return criteria.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(this.getLogExceptionMessage("getMethodsByGroup", "group", group, e.getMessage(), "Method"),
				e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Method> getByGroupAndType(final String group, final String type) {
		try {

			final Criteria criteria = this.getSession().createCriteria(Method.class);
			final Criterion group1 = Restrictions.eq("mgrp", group);
			final Criterion group2 = Restrictions.eq("mgrp", "G");
			final LogicalExpression orExp = Restrictions.or(group1, group2);
			final Criterion filterType = Restrictions.eq("mtype", type);
			final LogicalExpression andExp = Restrictions.and(orExp, filterType);

			criteria.add(andExp);
			criteria.addOrder(Order.asc(METHOD_NAME));
			return criteria.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				this.getLogExceptionMessage("getMethodsByGroupAndType", "group|type", group + "|" + type, e.getMessage(), "Method"), e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Method> getByGroupAndTypeAndName(final String group, final String type, final String name) {
		try {

			final Criteria criteria = this.getSession().createCriteria(Method.class);

			if (type != null && !type.isEmpty()) {
				criteria.add(Restrictions.eq("mtype", type));
			}

			if (name != null && !name.isEmpty()) {
				criteria.add(Restrictions.like(METHOD_NAME, "%" + name.trim() + "%"));
			}

			if (group != null && !group.isEmpty()) {
				final Criterion group1 = Restrictions.eq("mgrp", group);
				final Criterion group2 = Restrictions.eq("mgrp", "G");
				final LogicalExpression orExp = Restrictions.or(group1, group2);

				criteria.add(orExp);
			}

			criteria.addOrder(Order.asc(METHOD_NAME));
			return criteria.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				this.getLogExceptionMessage("getMethodsByGroupAndType", "group|type", group + "|" + type, e.getMessage(), "Method"), e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Method> getAllMethodsNotGenerative() {
		try {
			final List<Integer> validMethodClasses = new ArrayList<Integer>();
			validMethodClasses.addAll(Method.BULKED_CLASSES);
			validMethodClasses.addAll(Method.NON_BULKED_CLASSES);

			final Criteria criteria = this.getSession().createCriteria(Method.class);
			criteria.add(Restrictions.ne("mtype", "GEN"));
			criteria.add(Restrictions.in("geneq", validMethodClasses));
			criteria.addOrder(Order.asc(METHOD_NAME));

			return criteria.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				this.getLogExceptionMessage("getAllMethodsNotGenerative", "", null, e.getMessage(), "Method"), e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Method> getMethodsNotGenerativeById(final List<Integer> ids) {
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
			criteria.addOrder(Order.asc(METHOD_NAME));

			return criteria.list();
		} catch (final HibernateException e) {
			MethodDAO.LOG.error(this.getLogExceptionMessage("getMethodsNotGenerativeById", "", null, e.getMessage(), "Method"), e);
			throw new MiddlewareQueryException(this.getLogExceptionMessage("getMethodsNotGenerativeById", "", null, e.getMessage(),
				"Method"), e);
		}
	}

	public long countByGroup(final String group) {
		try {
			final Criteria criteria = this.getSession().createCriteria(Method.class);
			criteria.add(Restrictions.eq("mgrp", group));
			criteria.setProjection(Projections.rowCount());
			return ((Long) criteria.uniqueResult()).longValue(); // count
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(this.getLogExceptionMessage("countMethodsByGroup", "group", group, e.getMessage(), "Method"),
				e);
		}
	}

	public Method getByCode(final String code, final String programUUID) {
		try {
			final Criteria criteria = this.getSession().createCriteria(Method.class);
			criteria.add(Restrictions.eq("mcode", code));
			criteria.add(Restrictions.or(Restrictions.eq(MethodDAO.UNIQUE_ID, programUUID), Restrictions.isNull(MethodDAO.UNIQUE_ID)));
			return (Method) criteria.uniqueResult();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(this.getLogExceptionMessage("getMethodsByCode", "code", code, e.getMessage(), "Method"), e);
		}
	}

	public List<Method> getByCode(final List<String> codes) {
		try {
			final Criteria criteria = this.getSession().createCriteria(Method.class);
			criteria.add(Restrictions.in("mcode", codes));
			return criteria.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				this.getLogExceptionMessage("getMethodsByCode", "codes", codes.toString(), e.getMessage(), "Method"),
				e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Method> getByName(final String name) {
		final List<Method> methods = new ArrayList<Method>();
		try {
			final StringBuilder queryString = new StringBuilder();
			queryString.append(
				"SELECT mid, mtype, mgrp, mcode, mname, mdesc, mref, mprgn, mfprg, mattr, geneq, muid, lmid, mdate, program_uuid ")
				.append("FROM methods m WHERE m.mname = :mname");
			final SQLQuery query = this.getSession().createSQLQuery(queryString.toString());
			query.setParameter(METHOD_NAME, name);

			final List<Object[]> list = query.list();

			for (final Object[] row : list) {
				final Integer mid = (Integer) row[0];
				final String mtype = (String) row[1];
				final String mgrp = (String) row[2];
				final String mcode = (String) row[3];
				final String mname = (String) row[4];
				final String mdesc = (String) row[5];
				final Integer mref = (Integer) row[6];
				final Integer mprgn = (Integer) row[7];
				final Integer mfprg = (Integer) row[8];
				final Integer mattr = (Integer) row[9];
				final Integer geneq = (Integer) row[10];
				final Integer muid = (Integer) row[11];
				final Integer lmid = (Integer) row[12];
				final Integer mdate = (Integer) row[13];
				final String programUUID = (String) row[14];

				final Method method =
					new Method(mid, mtype, mgrp, mcode, mname, mdesc, mref, mprgn, mfprg, mattr, geneq, muid, lmid, mdate, programUUID);
				methods.add(method);
			}

			return methods;
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(this.getLogExceptionMessage("getByName", "name", name, e.getMessage(), "Method"), e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Method> getByName(final String name, final String uniqueId) {
		final List<Method> methods = new ArrayList<Method>();
		try {
			final StringBuilder queryString = new StringBuilder();
			queryString
				.append("SELECT mid, mtype, mgrp, mcode, mname, mdesc, mref, mprgn, mfprg, mattr, geneq, muid, lmid, mdate, program_uuid ")
				.append("FROM methods m WHERE m.mname = :mname ").append("AND m.program_uuid = :uniqueId");
			final SQLQuery query = this.getSession().createSQLQuery(queryString.toString());
			query.setParameter(METHOD_NAME, name);
			query.setParameter("uniqueId", uniqueId);

			final List<Object[]> list = query.list();

			for (final Object[] row : list) {
				final Integer mid = (Integer) row[0];
				final String mtype = (String) row[1];
				final String mgrp = (String) row[2];
				final String mcode = (String) row[3];
				final String mname = (String) row[4];
				final String mdesc = (String) row[5];
				final Integer mref = (Integer) row[6];
				final Integer mprgn = (Integer) row[7];
				final Integer mfprg = (Integer) row[8];
				final Integer mattr = (Integer) row[9];
				final Integer geneq = (Integer) row[10];
				final Integer muid = (Integer) row[11];
				final Integer lmid = (Integer) row[12];
				final Integer mdate = (Integer) row[13];
				final String programUUID = (String) row[14];

				final Method method =
					new Method(mid, mtype, mgrp, mcode, mname, mdesc, mref, mprgn, mfprg, mattr, geneq, muid, lmid, mdate, programUUID);
				methods.add(method);
			}

			return methods;
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(this.getLogExceptionMessage("getByName", "name", name, e.getMessage(), "Method"), e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Method> getProgramMethods(final String programUUID) {
		List<Method> method = new ArrayList<Method>();
		try {
			final Criteria criteria = this.getSession().createCriteria(Method.class);
			criteria.add(Restrictions.eq(MethodDAO.UNIQUE_ID, programUUID));
			method = criteria.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				this.getLogExceptionMessage("getProgramMethods", "programUUID", programUUID, e.getMessage(), "Method"), e);
		}
		return method;
	}

	@SuppressWarnings("unchecked")
	public List<Method> getDerivativeAndMaintenanceMethods(final List<Integer> ids) {
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
			criteria.addOrder(Order.asc(METHOD_NAME));

			return criteria.list();
		} catch (final HibernateException e) {
			MethodDAO.LOG.error(this.getLogExceptionMessage("getMethodsNotGenerativeById", "", null, e.getMessage(), "Method"), e);
			throw new MiddlewareQueryException(
				this.getLogExceptionMessage("getAllMethodsDerivativeAndManintenance", "", null, e.getMessage(), "Method"), e);
		}
	}

	public List<Method> getFavoriteMethodsByMethodType(final String methodType, final String programUUID) {
		try {
			final Query query = this.getSession().getNamedQuery(Method.GET_FAVORITE_METHODS_BY_TYPE);
			query.setParameter("mType", methodType);
			query.setParameter("programUUID", programUUID);
			return query.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				this.getLogExceptionMessage("getFavoriteMethodsByMethodType", "", null, e.getMessage(), "Method"), e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<String> getMethodCodeByMethodIds(final Set<Integer> methodIds) {
		final List<String> methodsCodes = new ArrayList<String>();
		final StringBuilder queryString = new StringBuilder();
		queryString.append("SELECT mcode FROM methods WHERE mid  IN (:mids)");
		final SQLQuery query = this.getSession().createSQLQuery(queryString.toString());
		query.setParameterList("mids", methodIds);

		methodsCodes.addAll(query.list());
		return methodsCodes;
	}

	public List<Method> getAllNoBulkingMethods() {
		try {

			final Criteria criteria = this.getSession().createCriteria(Method.class);
			criteria.add(Restrictions.in("geneq", Method.NON_BULKED_CLASSES));
			criteria.addOrder(Order.asc(METHOD_NAME));
			return criteria.list();

		} catch (final HibernateException e) {
			MethodDAO.LOG.error(this.getLogExceptionMessage("getAllNoBulkingMethods", "", null, e.getMessage(), "Method"), e);
			throw new MiddlewareQueryException(this.getLogExceptionMessage("getAllNoBulkingMethods", "", null, e.getMessage(), "Method"),
				e);
		}
	}

	public List<Method> getNoBulkingMethodsByIdList(final List<Integer> ids) {
		if (!ids.isEmpty()) {
			try {

				final Criteria criteria = this.getSession().createCriteria(Method.class);
				criteria.add(Restrictions.in("geneq", Method.NON_BULKED_CLASSES));
				criteria.add(Restrictions.in("mid", ids));
				return criteria.list();

			} catch (final HibernateException e) {
				MethodDAO.LOG.error(this.getLogExceptionMessage("getNoBulkingMethodsByIdList", "", null, e.getMessage(), "Method"), e);
				throw new MiddlewareQueryException(
					this.getLogExceptionMessage("getNoBulkingMethodsByIdList", "", null, e.getMessage(), "Method"), e);
			}
		} else {
			return new ArrayList<>();
		}
	}

	public List<Method> getAllMethodsNotBulkingNotGenerative() {
		try {

			final Criteria criteria = this.getSession().createCriteria(Method.class);
			criteria.add(Restrictions.in("geneq", Method.NON_BULKED_CLASSES));
			criteria.add(Restrictions.ne("mtype", "GEN"));
			criteria.addOrder(Order.asc(METHOD_NAME));
			return criteria.list();

		} catch (final HibernateException e) {
			MethodDAO.LOG.error(this.getLogExceptionMessage("getAllMethodsNotBulkingNotGenerative", "", null, e.getMessage(), "Method"), e);
			throw new MiddlewareQueryException(
				this.getLogExceptionMessage("getAllMethodsNotBulkingNotGenerative", "", null, e.getMessage(), "Method"), e);
		}
	}

	public List<Method> getNoBulkingMethodsByType(final String type, final String programUUID) {
		try {

			final Criteria criteria = this.getSession().createCriteria(Method.class);
			criteria.add(Restrictions.in("geneq", Method.NON_BULKED_CLASSES));
			criteria.add(Restrictions.eq("mtype", type));
			criteria.add(Restrictions.or(Restrictions.eq(MethodDAO.UNIQUE_ID, programUUID), Restrictions.isNull(MethodDAO.UNIQUE_ID)));
			criteria.addOrder(Order.asc(METHOD_NAME));
			return criteria.list();

		} catch (final HibernateException e) {
			MethodDAO.LOG.error(this.getLogExceptionMessage("getNoBulkingMethodsByType", "", null, e.getMessage(), "Method"), e);
			throw new MiddlewareQueryException(this.getLogExceptionMessage("getNoBulkingMethodsByType", "", null, e.getMessage(), "Method"),
				e);
		}
	}

	public List<Method> filterMethods(final String programUUID, final Set<String> abbreviations, final List<Integer> methodIds) {

		try {
			final Criteria criteria = this.getSession().createCriteria(Method.class);
			if (StringUtils.isEmpty(programUUID)) {
				criteria.add(Restrictions.isNull(MethodDAO.UNIQUE_ID));
			} else {
				criteria.add(Restrictions.or(Restrictions.eq(MethodDAO.UNIQUE_ID, programUUID), Restrictions.isNull(MethodDAO.UNIQUE_ID)));
			}

			if (!CollectionUtils.isEmpty(methodIds)) {
				criteria.add(Restrictions.in("mid", methodIds));
			}

			if (!CollectionUtils.isEmpty(abbreviations)) {
				criteria.add(Restrictions.in("mcode", abbreviations));
			}

			criteria.addOrder(Order.asc(METHOD_NAME));
			return criteria.list();
		} catch (final Exception e) {
			MethodDAO.LOG.error(this.getLogExceptionMessage("filterMethods", "", null, e.getMessage(), "Method"), e);
			throw new MiddlewareQueryException(this.getLogExceptionMessage("filterMethods", "", null, e.getMessage(), "Method"),
				e);
		}
	}

}
