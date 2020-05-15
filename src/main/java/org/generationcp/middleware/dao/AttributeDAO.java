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

import org.generationcp.middleware.domain.germplasm.AttributeDTO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

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

	public List<Attribute> getAttributeValuesGIDList(final List<Integer> gidList) {
		List<Attribute> attributes = new ArrayList<>();
		if (gidList != null && !gidList.isEmpty()) {
			try {
				final String sql = "SELECT {a.*}" + " FROM atributs a" + " WHERE a.gid in (:gidList)";
				final SQLQuery query = this.getSession().createSQLQuery(sql);
				query.addEntity("a", Attribute.class);
				query.setParameterList("gidList", gidList);
				attributes = query.list();
			} catch (final HibernateException e) {
				throw new MiddlewareQueryException(
					"Error with getAttributeValuesGIDList(gidList=" + gidList + "): " + e.getMessage(), e);
			}
		}
		return attributes;
	}

	@SuppressWarnings("unchecked")
	public List<Attribute> getAttributeValuesByTypeAndGIDList(final Integer attributeType, final List<Integer> gidList) {
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

	public Attribute getAttribute(final Integer gid, final String attributeName) {
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

	public List<AttributeDTO> getAttributesByGidAndAttributeIds(
		final String gid, final List<String> attributeIds, final Integer pageSize, final Integer pageNumber) {

		List<AttributeDTO> attributes;
		try {
			String sql = this.buildQueryForAttributes(attributeIds);

			final SQLQuery query = this.getSession().createSQLQuery(sql);
			query.addScalar("attributeCode").addScalar("attributeDbId").addScalar("attributeName").addScalar("determinedDate")
				.addScalar("value");
			query.setParameter("gid", gid);

			if (attributeIds != null && !attributeIds.isEmpty()) {
				query.setParameterList("attributs", attributeIds);
			}

			if (pageNumber != null && pageSize != null) {
				query.setFirstResult(pageSize * (pageNumber - 1));
				query.setMaxResults(pageSize);
			}

			query.setResultTransformer(Transformers.aliasToBean(AttributeDTO.class));

			attributes = query.list();

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error with getAttributesByGid(gidList=" + gid + "): " + e.getMessage(), e);
		}
		return attributes;
	}

	public long countAttributesByGid(final String gid, final List<String> attributeDbIds) {
		String sql = "SELECT COUNT(1) "
			+ " FROM ("
			+ this.buildQueryForAttributes(attributeDbIds);

		sql = sql + " ) as result ";

		final SQLQuery query = this.getSession().createSQLQuery(sql);
		query.setParameter("gid", gid);

		if (attributeDbIds != null && !attributeDbIds.isEmpty()) {
			query.setParameterList("attributs", attributeDbIds);
		}

		return ((BigInteger) query.uniqueResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	public List<Attribute> getByIDs(final List<Integer> ids) {
		List<Attribute> toReturn = new ArrayList<>();
		try {
			if (ids != null && !ids.isEmpty()) {
				final String sql = "SELECT {a.*} FROM atributs a "
					+ " WHERE a.aid IN ( :ids ) ";
				final SQLQuery query = this.getSession().createSQLQuery(sql);
				query.addEntity("a", Attribute.class);
				query.setParameterList("ids", ids);
				toReturn = query.list();
			}
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error with getByIDs(ids=" + ids + ") query from Attributes: " + e.getMessage(), e);
		}
		return toReturn;
	}

	private String buildQueryForAttributes(final List<String> attributeIds) {
		String sql = "SELECT "
			+ "    u.fcode AS attributeCode,"
			+ "    u.fldno AS attributeDbId,"
			+ "    u.fname AS attributeName,"
			+ "    a.adate AS determinedDate,"
			+ "    a.aval AS value "
			+ " FROM"
			+ "    atributs a"
			+ "        INNER JOIN"
			+ "    udflds u ON a.atype = u.fldno "
			+ " WHERE"
			+ "    a.gid = :gid AND u.ftable = 'ATRIBUTS'";

		if (attributeIds != null && !attributeIds.isEmpty()) {
			sql = sql + " AND u.fldno IN ( :attributs )";
		}
		return sql;
	}
}
