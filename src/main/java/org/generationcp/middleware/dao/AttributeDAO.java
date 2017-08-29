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
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.workbench.Project;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link Attribute}.
 *
 */
public class AttributeDAO extends GenericDAO<Attribute, Integer> {

	@SuppressWarnings("unchecked")
	public List<Attribute> getByGID(Integer gid) throws MiddlewareQueryException {
		List<Attribute> toReturn = new ArrayList<Attribute>();
		try {
			if (gid != null) {
				Query query = this.getSession().getNamedQuery(Attribute.GET_BY_GID);
				query.setParameter("gid", gid);
				toReturn = query.list();
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getByGID(gid=" + gid + ") query from Attributes: " + e.getMessage(), e);
		}
		return toReturn;
	}

	@SuppressWarnings("unchecked")
	public List<UserDefinedField> getAttributeTypesByGIDList(List<Integer> gidList) throws MiddlewareQueryException {
		List<UserDefinedField> returnList = new ArrayList<UserDefinedField>();
		if (gidList != null && !gidList.isEmpty()) {
			try {
				String sql =
						"SELECT {u.*}" + " FROM atributs a" + " INNER JOIN udflds u" + " WHERE a.atype=u.fldno"
								+ " AND a.gid in (:gidList)" + " ORDER BY u.fname";
				SQLQuery query = this.getSession().createSQLQuery(sql);
				query.addEntity("u", UserDefinedField.class);
				query.setParameterList("gidList", gidList);
				returnList = query.list();

			} catch (HibernateException e) {
				this.logAndThrowException("Error with getAttributesByGIDList(gidList=" + gidList + "): " + e.getMessage(), e);
			}
		}
		return returnList;
	}

	@SuppressWarnings("unchecked")
	public List<Attribute> getAttributeValuesByTypeAndGIDList(Integer attributeType, List<Integer> gidList) throws MiddlewareQueryException {
		List<Attribute> returnList = new ArrayList<Attribute>();
		if (gidList != null && !gidList.isEmpty()) {
			try {
				String sql = "SELECT {a.*}" + " FROM atributs a" + " WHERE a.atype=:attributeType" + " AND a.gid in (:gidList)";
				SQLQuery query = this.getSession().createSQLQuery(sql);
				query.addEntity("a", Attribute.class);
				query.setParameter("attributeType", attributeType);
				query.setParameterList("gidList", gidList);
				returnList = query.list();
			} catch (HibernateException e) {
				this.logAndThrowException("Error with getAttributeValuesByTypeAndGIDList(attributeType=" + attributeType + ", gidList="
						+ gidList + "): " + e.getMessage(), e);
			}
		}
		return returnList;
	}
	
	@SuppressWarnings("unchecked")
	public List<UserDefinedField> getAttributeTypes() {
		final Criteria criteria = this.getSession().createCriteria(UserDefinedField.class).add(Restrictions.eq("ftable", "ATRIBUTS"));
		return criteria.list();
	}
}
