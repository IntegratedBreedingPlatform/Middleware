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
	public List<Attribute> getByGID(final Integer gid) {
		List<Attribute> toReturn = new ArrayList<>();
		try {
			if (gid != null) {
				final Query query = this.getSession().getNamedQuery(Attribute.GET_BY_GID);
				query.setParameter("gid", gid);
				toReturn = query.list();
			}
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error with getByGID(gid=" + gid + ") query from Attributes: " + e.getMessage(), e);
		}
		return toReturn;
	}

	@SuppressWarnings("unchecked")
	public List<Attribute> getAttributeValuesByTypeAndGIDList(final Integer attributeType, final List<Integer> gidList)
			throws MiddlewareQueryException {
		List<Attribute> returnList = new ArrayList<>();
		if (gidList != null && !gidList.isEmpty()) {
			try {
				final String sql = "SELECT {a.*}" + " FROM atributs a" + " WHERE a.atype=:attributeType" + " AND a.gid in (:gidList)";
				final SQLQuery query = this.getSession().createSQLQuery(sql);
				query.addEntity("a", Attribute.class);
				query.setParameter("attributeType", attributeType);
				query.setParameterList("gidList", gidList);
				returnList = query.list();
			} catch (final HibernateException e) {
				throw new MiddlewareQueryException("Error with getAttributeValuesByTypeAndGIDList(attributeType=" + attributeType
						+ ", gidList=" + gidList + "): " + e.getMessage(), e);
			}
		}
		return returnList;
	}

	@SuppressWarnings("unchecked")
	public List<UserDefinedField> getAttributeTypes() {
		final Criteria criteria = this.getSession().createCriteria(UserDefinedField.class).add(Restrictions.eq("ftable", "ATRIBUTS"));
		return criteria.list();
	}

	public Attribute getAttribute (final Integer gid, final String attributeName) {
		Attribute attribute = null;
			try {
				final String sql = "SELECT {a.*} FROM atributs a INNER JOIN udflds u ON (a.atype=u.fldno)"
						+ " WHERE a.gid = :gid AND u.ftable='ATRIBUTS' and u.fcode=:name";
				final SQLQuery query = this.getSession().createSQLQuery(sql);
				query.addEntity("a", Attribute.class);
				query.setParameter("gid", gid);
				query.setParameter("name", attributeName);
				final List<Attribute> attributes = query.list();
				if (!attributes.isEmpty()) {
					attribute = attributes.get(0);
				}

			} catch (final HibernateException e) {
				throw new MiddlewareQueryException("Error with getAttribute(gidList=" + gid + ", " + attributeName + "): " + e.getMessage(), e);
			}
		return attribute;
	}
}
