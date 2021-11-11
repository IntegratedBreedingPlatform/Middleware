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

import org.apache.commons.collections.CollectionUtils;
import org.generationcp.middleware.api.brapi.v1.attribute.AttributeDTO;
import org.generationcp.middleware.domain.germplasm.GermplasmAttributeDto;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.Attribute;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BooleanType;
import org.springframework.data.domain.Pageable;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO class for {@link Attribute}.
 */
public class AttributeDAO extends GenericDAO<Attribute, Integer> {

	private static final String COUNT_ATTRIBUTE_WITH_VARIABLES =
		"SELECT COUNT(A.ATYPE) FROM ATRIBUTS A INNER JOIN GERMPLSM G ON G.GID = A.GID AND G.DELETED = 0 AND g.grplce = 0 WHERE A.ATYPE IN (:variableIds)";

	private static final String COUNT_ATTRIBUTE_WITH_GERMPLASM_DELETED =
		"SELECT COUNT(A.ATYPE) FROM ATRIBUTS A INNER JOIN GERMPLSM G ON G.GID = A.GID AND G.DELETED = 1 WHERE A.ATYPE = :variableId";

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

	public List<Attribute> getByGIDsAndVariableType(final List<Integer> gids, final Integer variableTypeId) {
		try {
			if (CollectionUtils.isNotEmpty(gids)) {
				final StringBuilder queryString = new StringBuilder("SELECT {a.*} FROM atributs a ");
				queryString.append("INNER JOIN cvterm cv ON a.atype = cv.cvterm_id ");
				queryString.append(
					"INNER JOIN cvtermprop cp ON cp.type_id = " + TermId.VARIABLE_TYPE.getId() + " and cv.cvterm_id = cp.cvterm_id ");
				queryString.append("AND cp.value = (select name from cvterm where cvterm_id = :variableTypeId) ");
				queryString.append("WHERE a.gid in (:gids) ");
				final SQLQuery query = this.getSession().createSQLQuery(queryString.toString());
				query.addEntity("a", Attribute.class);
				query.setParameter("variableTypeId", variableTypeId);
				query.setParameterList("gids", gids);
				return query.list();
			}
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				"Error with getByGIDsAndVariableType(gid=" + gids + ") query from Attributes: " + e.getMessage(), e);
		}
		return new ArrayList<>();
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
	public List<Attribute> getAttributeValuesByTypeAndGIDList(final Integer variableId, final List<Integer> gidList) {
		List<Attribute> returnList = new ArrayList<>();
		if (gidList != null && !gidList.isEmpty()) {
			try {
				final String sql = "SELECT {a.*}" + " FROM atributs a" + " WHERE a.atype=:variableId" + " AND a.gid in (:gidList)";
				final SQLQuery query = this.getSession().createSQLQuery(sql);
				query.addEntity("a", Attribute.class);
				query.setParameter("variableId", variableId);
				query.setParameterList("gidList", gidList);
				returnList = query.list();
			} catch (final HibernateException e) {
				throw new MiddlewareQueryException("Error with getAttributeValuesByTypeAndGIDList(variableId=" + variableId
					+ ", gidList=" + gidList + "): " + e.getMessage(), e);
			}
		}
		return returnList;
	}

	public List<GermplasmAttributeDto> getGermplasmAttributeDtos(final Integer gid, final Integer variableTypeId,
		final String programUUID) {
		try {
			final StringBuilder queryString = new StringBuilder();
			queryString.append("Select a.aid AS id, ");
			queryString.append("cv.cvterm_id as variableId, ");
			queryString.append("a.aval AS value, ");
			queryString.append("IFNULL(vpo.alias, cv.name) AS variableName, ");
			queryString.append("cp.value AS variableTypeName, ");
			queryString.append("cv.definition AS variableDescription, ");
			queryString.append("CAST(a.adate AS CHAR(255)) AS date, ");
			queryString.append("a.alocn AS locationId, ");
			queryString.append("l.lname AS locationName, ");
			queryString.append("(select exists(select 1 from file_metadata f "
				+ " inner join file_metadata_cvterm fmc on f.file_id = fmc.file_metadata_id "
				+ " where f.gid = a.gid and fmc.cvterm_id = a.atype)) AS hasFiles ");
			queryString.append("FROM atributs a ");
			queryString.append("INNER JOIN cvterm cv ON a.atype = cv.cvterm_id ");
			queryString.append(
				"INNER JOIN cvtermprop cp ON cp.type_id = " + TermId.VARIABLE_TYPE.getId() + " and cv.cvterm_id = cp.cvterm_id ");
			queryString.append("LEFT JOIN location l on a.alocn = l.locid ");
			queryString.append("LEFT JOIN variable_overrides vpo ON vpo.cvterm_id = cv.cvterm_id AND vpo.program_uuid = :programUUID ");
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
			sqlQuery.addScalar("hasFiles", new BooleanType());
			sqlQuery.setParameter("gid", gid);
			if (variableTypeId != null) {
				sqlQuery.setParameter("variableTypeId", variableTypeId);
			}

			sqlQuery.setParameter("programUUID", programUUID);
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
			throw new MiddlewareQueryException(
				"Error with getAttributesByGUIDAndAttributeIds(germplasmUUID=" + germplasmUUID + "): " + e.getMessage(), e);
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

	private String buildQueryForAttributes(final List<String> attributeIds) {
		String sql = "SELECT "
			+ "    u.name AS attributeCode,"
			+ "    u.cvterm_id AS attributeDbId,"
			+ "    u.definition AS attributeName,"
			+ "    a.adate AS determinedDate,"
			+ "    a.aval AS value "
			+ " FROM"
			+ "    atributs a"
			+ "        INNER JOIN"
			+ "    cvterm u ON a.atype = u.cvterm_id "
			+ "        INNER JOIN"
			+ "    germplsm g ON a.gid = g.gid "
			+ " WHERE"
			+ "    g.germplsm_uuid = :germplasmUUID ";

		if (attributeIds != null && !attributeIds.isEmpty()) {
			sql = sql + " AND u.cvterm_id IN ( :attributs )";
		}
		return sql;
	}

	public Map<Integer, List<AttributeDTO>> getAttributesByGidsMap(
		final List<Integer> gids) {

		final Map<Integer, List<AttributeDTO>> attributesMap = new HashMap<>();

		if (CollectionUtils.isEmpty(gids)) {
			return attributesMap;
		}

		try {
			final String sql = "SELECT "
				+ "    u.name AS attributeCode,"
				+ "    u.cvterm_id AS attributeDbId,"
				+ "    u.definition AS attributeName,"
				+ "    a.adate AS determinedDate,"
				+ "    a.aval AS value, "
				+ "    a.gid AS gid "
				+ " FROM"
				+ "    atributs a"
				+ "        INNER JOIN"
				+ "    cvterm u ON a.atype = u.cvterm_id "
				+ "        INNER JOIN"
				+ "    germplsm g ON a.gid = g.gid "
				+ " WHERE"
				+ "    g.gid IN (:gids) ";

			final SQLQuery query = this.getSession().createSQLQuery(sql);
			query.setParameterList("gids", gids);

			final List<Object[]> rows = query.list();
			for (final Object[] row : rows) {
				final AttributeDTO attributeDTO =
					new AttributeDTO((String) row[0], (Integer) row[1], (String) row[2], (Integer) row[3], (String) row[4]);
				final Integer gid = (Integer) row[5];
				attributesMap.putIfAbsent(gid, new ArrayList<>());
				attributesMap.get(gid).add(attributeDTO);
			}

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error with getAttributesByGidsMap(gids=" + gids + "): " + e.getMessage(), e);
		}
		return attributesMap;
	}

	public long countByVariables(final List<Integer> variablesIds) {
		try {
			final SQLQuery query =
				this.getSession().createSQLQuery(COUNT_ATTRIBUTE_WITH_VARIABLES);
			query.setParameterList("variableIds", variablesIds);

			return ((BigInteger) query.uniqueResult()).longValue();

		} catch (final HibernateException e) {
			final String errorMessage = "Error at countByVariables=" + variablesIds + " in AttributeDAO: " + e.getMessage();
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	public long countByVariablesUsedInHistoricalGermplasm(final Integer variablesId) {
		try {
			final SQLQuery query =
				this.getSession().createSQLQuery(COUNT_ATTRIBUTE_WITH_GERMPLASM_DELETED);
			query.setParameter("variableId", variablesId);

			return ((BigInteger) query.uniqueResult()).longValue();

		} catch (final HibernateException e) {
			final String errorMessage =
				"Error at countByVariablesUsedInHistoricalGermplasm=" + variablesId + " in AttributeDAO: " + e.getMessage();
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	public boolean isLocationIdUsedInAttribute(final Integer locationId) {
		try {
			final String sql = "SELECT count(1) FROM ATRIBUTS WHERE alocn = :locationId";
			final SQLQuery query = this.getSession().createSQLQuery(sql);
			query.setParameter("locationId", locationId);
			return ((BigInteger) query.uniqueResult()).longValue() > 0;
		} catch (final HibernateException e) {
			final String message = "Error with isLocationIdUsedInAttributes(locationId=" + locationId + "): " + e.getMessage();
			throw new MiddlewareQueryException(message, e);
		}
	}
}
