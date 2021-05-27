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
import org.generationcp.middleware.api.brapi.v1.attribute.AttributeDTO;
import org.generationcp.middleware.domain.germplasm.GermplasmAttributeDto;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.transform.Transformers;
import org.springframework.data.domain.Pageable;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * DAO class for {@link Attribute}.
 *
 */
public class AttributeDAO extends GenericDAO<Attribute, Integer> {

	private static final String COUNT_ATTRIBUTE_WITH_VARIABLE =
		"SELECT COUNT(A.ATYPE) FROM ATRIBUTS A WHERE A.ATYPE= :variableId";

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

	public List<org.generationcp.middleware.api.attribute.AttributeDTO> searchAttributes(final String query) {
		if (StringUtils.isBlank(query)) {
			return Collections.EMPTY_LIST;
		}
		try {
			// Attributes will be migrated out of user defined fields later
			final SQLQuery sqlQuery = this.getSession().createSQLQuery("SELECT " //
				+ "   cv.name AS code," //
				+ "   cv.cvterm_id AS id," //
				+ "   cv.definition AS name" //
				+ " FROM cvterm cv INNER JOIN cvtermprop cp ON cv.cvterm_id = cp.cvterm_id " //
				+ " WHERE cp.value = (select name from cvterm where cvterm_id = 1814) " //
				+ "   AND (cv.definition like :fname or cv.name like :name)" //
				+ " LIMIT 100 ");
			sqlQuery.setParameter("fname", '%' + query + '%');
			sqlQuery.setParameter("name", '%' + query + '%');
			sqlQuery.addScalar("code");
			sqlQuery.addScalar("id");
			sqlQuery.addScalar("name");
			sqlQuery.setResultTransformer(Transformers.aliasToBean(org.generationcp.middleware.api.attribute.AttributeDTO.class));

			return sqlQuery.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error with searchAttributes(query=" + query + "): " + e.getMessage(), e);
		}
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

	public Attribute getAttributeByGidAndVariableId(final Integer gid, final Integer variableId) {
		Attribute attribute = null;
		try {
			final String sql = "SELECT {a.*} FROM atributs a "
				+ " WHERE a.gid = :gid AND a.atype = :variableId";
			final SQLQuery query = this.getSession().createSQLQuery(sql);
			query.addEntity("a", Attribute.class);
			query.setParameter("gid", gid);
			query.setParameter("variableId", variableId);
			final List<Attribute> attributes = query.list();
			if (!attributes.isEmpty()) {
				attribute = attributes.get(0);
			}

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				"Error with getAttributeByGidAndVariableId(gid=" + gid + ", " + variableId + "): " + e.getMessage(), e);
		}
		return attribute;
	}

	public List<GermplasmAttributeDto> getGermplasmAttributeDtos(final Integer gid, final Integer variableTypeId) {
		try {
			final StringBuilder queryString = new StringBuilder();
			queryString.append("Select a.aid AS id, ");
			queryString.append("cv.cvterm_id as variableId, ");
			queryString.append("a.aval AS value, ");
			queryString.append("cv.name AS variableName, ");
			queryString.append("cp.value AS variableTypeName, ");
			queryString.append("cv.definition AS variableDescription, ");
			queryString.append("CAST(a.adate AS CHAR(255)) AS date, ");
			queryString.append("a.alocn AS locationId, ");
			queryString.append("l.lname AS locationName ");
			queryString.append("FROM atributs a ");
			queryString.append("INNER JOIN cvterm cv ON a.atype = cv.cvterm_id ");
			queryString.append("INNER JOIN cvtermprop cp ON cp.type_id = 1800 and cv.cvterm_id = cp.cvterm_id ");
			queryString.append("LEFT JOIN location l on a.alocn = l.locid ");
			queryString.append("WHERE a.gid = :gid ");
			if (variableTypeId != null) {
				queryString.append("AND cp.value = (select name from cvterm where cvterm_id = :variableTypeId) ");
			}
			final SQLQuery sqlQuery = this.getSession().createSQLQuery(queryString.toString());
			sqlQuery.addScalar("id");
			sqlQuery.addScalar("variableId");
			sqlQuery.addScalar("value");
			sqlQuery.addScalar("variableName");
			sqlQuery.addScalar("variableTypeName");
			sqlQuery.addScalar("variableDescription");
			sqlQuery.addScalar("date");
			sqlQuery.addScalar("locationId");
			sqlQuery.addScalar("locationName");
			sqlQuery.setParameter("gid", gid);
			if (variableTypeId != null) {
				sqlQuery.setParameter("variableTypeId", variableTypeId);
			}

			sqlQuery.setResultTransformer(new AliasToBeanResultTransformer(GermplasmAttributeDto.class));
			return sqlQuery.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				"Error with getGermplasmAttributeDtos(gid=" + gid + ", variableTypeId=" + variableTypeId + "): " + e.getMessage(), e);
		}
	}

	public List<AttributeDTO> getAttributesByGUIDAndAttributeIds(
			final String germplasmUUID, final List<String> attributeIds, final Pageable pageable) {

		final List<AttributeDTO> attributes;
		try {
			final String sql = this.buildQueryForAttributes(attributeIds);

			final SQLQuery query = this.getSession().createSQLQuery(sql);
			query.addScalar("attributeCode").addScalar("attributeDbId").addScalar("attributeName").addScalar("determinedDate")
				.addScalar("value");
			query.setParameter("germplasmUUID", germplasmUUID);

			if (attributeIds != null && !attributeIds.isEmpty()) {
				query.setParameterList("attributs", attributeIds);
			}

			addPaginationToSQLQuery(query, pageable);

			query.setResultTransformer(Transformers.aliasToBean(AttributeDTO.class));

			attributes = query.list();

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error with getAttributesByGid(gidList=" + germplasmUUID + "): " + e.getMessage(), e);
		}
		return attributes;
	}

	public long countAttributesByGUID(final String germplasmUUID, final List<String> attributeDbIds) {
		String sql = "SELECT COUNT(1) "
			+ " FROM ("
			+ this.buildQueryForAttributes(attributeDbIds);

		sql = sql + " ) as result ";

		final SQLQuery query = this.getSession().createSQLQuery(sql);
		query.setParameter("germplasmUUID", germplasmUUID);

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
			+ "        INNER JOIN"
			+ "    germplsm g ON a.gid = g.gid "
			+ " WHERE"
			+ "    g.germplsm_uuid = :germplasmUUID AND u.ftable = 'ATRIBUTS'";

		if (attributeIds != null && !attributeIds.isEmpty()) {
			sql = sql + " AND u.fldno IN ( :attributs )";
		}
		return sql;
	}

	public long countByVariable(final int variableId){
		try {
			final SQLQuery query = this.getSession().createSQLQuery(AttributeDAO.COUNT_ATTRIBUTE_WITH_VARIABLE);
			query.setParameter("variableId", variableId);

			return ((BigInteger) query.uniqueResult()).longValue();

		} catch (final HibernateException e) {
			final String errorMessage = "Error at countByVariable=" + variableId + " in AttributeDAO: " + e.getMessage();
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}
}
