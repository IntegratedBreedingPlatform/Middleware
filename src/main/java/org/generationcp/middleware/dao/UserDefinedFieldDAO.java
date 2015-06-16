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

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link UserDefinedField}.
 *
 */
public class UserDefinedFieldDAO extends GenericDAO<UserDefinedField, Integer> {

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
}
