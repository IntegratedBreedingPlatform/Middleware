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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.NonUniqueResultException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DAO class for {@link UserDefinedField}.
 *
 */
public class UserDefinedFieldDAO extends GenericDAO<UserDefinedField, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(UserDefinedFieldDAO.class);

	@SuppressWarnings("unchecked")
	public List<UserDefinedField> getByFieldTableNameAndType(String tableName, String fieldType) throws MiddlewareQueryException {
		try {
			if (tableName != null && fieldType != null) {
				Criteria criteria = this.getSession().createCriteria(UserDefinedField.class);
				criteria.add(Restrictions.eq("ftable", tableName));
				criteria.add(Restrictions.eq("ftype", fieldType));
				criteria.addOrder(Order.asc("fname"));
				return criteria.list();
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getByFieldTableNameAndType(name=" + tableName + " type= " + fieldType
					+ " ) query from UserDefinedField: " + e.getMessage(), e);
		}
		return new ArrayList<UserDefinedField>();
	}

	@SuppressWarnings("unchecked")
	public List<UserDefinedField> getByTableNameAndNameLike(String tableName, String nameLike) throws MiddlewareQueryException {
		try {
			if (tableName != null && nameLike != null) {
				Criteria criteria = this.getSession().createCriteria(UserDefinedField.class);
				criteria.add(Restrictions.eq("ftable", tableName));
				criteria.add(Restrictions.like("fname", nameLike));
				criteria.addOrder(Order.asc("fname"));
				return criteria.list();
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getByTableNameAndNameLike(name=" + tableName + " nameLike= " + nameLike
					+ " ) query from UserDefinedField: " + e.getMessage(), e);
		}
		return new ArrayList<UserDefinedField>();
	}

	@SuppressWarnings("unchecked")
	public Map<String, Integer> getByCodesInMap(String table, String type, List<String> codes) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(UserDefinedField.class);
			criteria.add(Restrictions.eq("ftable", table));
			criteria.add(Restrictions.like("ftype", type));
			criteria.add(Restrictions.in("fcode", codes));
			List<UserDefinedField> list = criteria.list();
			if (list != null && !list.isEmpty()) {
				Map<String, Integer> map = new HashMap<String, Integer>();
				for (UserDefinedField field : list) {
					map.put(field.getFcode(), field.getFldno());
				}
				return map;
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error with getByCodesInMap(name=" + table + " type= " + type + " ) query from UserDefinedField: "
					+ e.getMessage(), e);
		}
		return new HashMap<String, Integer>();
	}

	public UserDefinedField getByLocalFieldNo(Integer lfldno) {
		Criteria criteria = this.getSession().createCriteria(UserDefinedField.class);
		criteria.add(Restrictions.eq("lfldno", lfldno));
		return (UserDefinedField) criteria.uniqueResult();
	}

	public UserDefinedField getByTableTypeAndCode(String table, String type, String code) throws MiddlewareQueryException {
		try {
			if (StringUtils.isNotBlank(table) && StringUtils.isNotBlank(type) && StringUtils.isNotBlank(code)) {
				Criteria criteria = this.getSession().createCriteria(UserDefinedField.class);
				criteria.add(Restrictions.eq("ftable", table));
				criteria.add(Restrictions.eq("ftype", type));
				criteria.add(Restrictions.eq("fcode", code));
				return (UserDefinedField) criteria.uniqueResult();
			}
		} catch (NonUniqueResultException nonUniqueResultException) {
			final String message =
					"Multiple UDFLD records were found with fTable={}, fType={}, fCode={}. Was expecting one uniqe result only : {}";
			LOG.error(message, table, type, code, nonUniqueResultException.getMessage());
			throw new MiddlewareQueryException(message, nonUniqueResultException);

		} catch (HibernateException e) {
			String message = "Error executing UserDefinedFieldDAO.getByTableTypeAndCode(fTable={}, fType={}, fCode={}) : {}";
			LOG.error(message, table, type, code, e.getMessage());
			throw new MiddlewareQueryException(message, e);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<UserDefinedField> getAttributeTypesByGIDList(final List<Integer> gidList) {
		List<UserDefinedField> returnList = new ArrayList<>();
		if (gidList != null && !gidList.isEmpty()) {
			try {
				final String sql = "SELECT DISTINCT {u.*}" + " FROM atributs a" + " INNER JOIN udflds u" + " WHERE a.atype=u.fldno"
						+ " AND a.gid in (:gidList)" + " ORDER BY u.fname";
				final SQLQuery query = this.getSession().createSQLQuery(sql);
				query.addEntity("u", UserDefinedField.class);
				query.setParameterList("gidList", gidList);
				returnList = query.list();

			} catch (final HibernateException e) {
				throw new MiddlewareQueryException("Error with getAttributesByGIDList(gidList=" + gidList + "): " + e.getMessage(), e);
			}
		}
		return returnList;
	}
	
	@SuppressWarnings("unchecked")
	public List<UserDefinedField> getNameTypesByGIDList(final List<Integer> gidList) {
		List<UserDefinedField> returnList = new ArrayList<>();
		if (gidList != null && !gidList.isEmpty()) {
			try {
				final String sql = "SELECT DISTINCT {u.*}" + " FROM names n" + " INNER JOIN udflds u" + " WHERE n.ntype=u.fldno"
						+ " AND n.gid in (:gidList)" + " ORDER BY u.fname";
				final SQLQuery query = this.getSession().createSQLQuery(sql);
				query.addEntity("u", UserDefinedField.class);
				query.setParameterList("gidList", gidList);
				returnList = query.list();

			} catch (final HibernateException e) {
				throw new MiddlewareQueryException("Error with getNameTypesByGIDList(gidList=" + gidList + "): " + e.getMessage(), e);
			}
		}
		return returnList;
	}
}
