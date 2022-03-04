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
import org.generationcp.middleware.api.brapi.v2.attribute.AttributeValueDto;
import org.generationcp.middleware.dao.util.VariableUtils;
import org.generationcp.middleware.domain.germplasm.GermplasmAttributeDto;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.TermRelationshipId;
import org.generationcp.middleware.domain.search_request.brapi.v2.AttributeValueSearchRequestDto;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.Attribute;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BooleanType;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.springframework.data.domain.Pageable;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * DAO class for {@link Attribute}.
 */
public class AttributeDAO extends GenericDAO<Attribute, Integer> {

	private static final String COUNT_ATTRIBUTE_WITH_VARIABLES =
		"SELECT COUNT(A.ATYPE) FROM ATRIBUTS A INNER JOIN GERMPLSM G ON G.GID = A.GID AND G.DELETED = 0 AND g.grplce = 0 WHERE A.ATYPE IN (:variableIds)";

	private static final String COUNT_ATTRIBUTE_WITH_GERMPLASM_DELETED =
		"SELECT COUNT(A.ATYPE) FROM ATRIBUTS A INNER JOIN GERMPLSM G ON G.GID = A.GID AND G.DELETED = 1 WHERE A.ATYPE = :variableId";

	private static final String ATTRIBUTE_VALUE_SELECT =
		"SELECT a.aid, cv.cvterm_id AS attributeDbId, "
			+ "IFNULL(vpo.alias, cv.name) AS attributeName, "
			+ "a.aid AS attributeValueDbId, "
			+ "a.adate AS determinedDate, "
			+ "a.alocn AS locationDbId, "
			+ "g.germplsm_uuid AS germplasmDbId, "
			+ "names.nval AS germplasmName, "
			+ "a.aval AS value ";

	public static final String ADDTL_INFO_LOCATION = "locationDbId";

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

	public long countAttributeValueDtos(final AttributeValueSearchRequestDto attributeValueSearchRequestDto, final String programUUID) {
		final StringBuilder sql = new StringBuilder(" SELECT COUNT(DISTINCT cv.cvterm_id) ");
		this.appendAttributeValuesFromQuery(sql);
		this.appendAttributeValueSeachFilters(sql, attributeValueSearchRequestDto);

		final SQLQuery sqlQuery = this.getSession().createSQLQuery(sql.toString());
		this.addAttributeValueSearchParameters(sqlQuery, attributeValueSearchRequestDto);
		sqlQuery.setParameter("programUUID", programUUID);

		return ((BigInteger) sqlQuery.uniqueResult()).longValue();
	}

	public List<AttributeValueDto> getAttributeValueDtos(final AttributeValueSearchRequestDto attributeValueSearchRequestDto,
		final Pageable pageable,
		final String programUUID) {
		final SQLQuery sqlQuery = this.getSession().createSQLQuery(this.createAttributeValuesQueryString(attributeValueSearchRequestDto));
		sqlQuery.setParameter("programUUID", programUUID);
		if (pageable != null) {
			sqlQuery.setFirstResult(pageable.getPageSize() * pageable.getPageNumber());
			sqlQuery.setMaxResults(pageable.getPageSize());
		}
		this.addAttributeValueSearchParameters(sqlQuery, attributeValueSearchRequestDto);

		sqlQuery.addScalar("aid", IntegerType.INSTANCE);
		sqlQuery.addScalar("locationDbId", StringType.INSTANCE);
		sqlQuery.addScalar("attributeDbId", StringType.INSTANCE);
		sqlQuery.addScalar("attributeName", StringType.INSTANCE);
		sqlQuery.addScalar("attributeValueDbId", StringType.INSTANCE);
		sqlQuery.addScalar("determinedDate", DateType.INSTANCE);
		sqlQuery.addScalar("germplasmDbId", StringType.INSTANCE);
		sqlQuery.addScalar("germplasmName", StringType.INSTANCE);
		sqlQuery.addScalar("value", StringType.INSTANCE);
		sqlQuery.setResultTransformer(new AliasToBeanResultTransformer(AttributeValueDto.class));

		final List<AttributeValueDto> results = sqlQuery.list();

		if (results != null && !results.isEmpty()) {
			results.stream().map(
					attributeValue -> this.addAdditionalInfoToAttributeValue(attributeValue))
				.collect(Collectors.toList());
		}

		return results;
	}

	private AttributeValueDto addAdditionalInfoToAttributeValue(final AttributeValueDto attributeValue) {
		final Map<String, String> additionalInfo = new HashMap<>();
		additionalInfo.put(ADDTL_INFO_LOCATION, attributeValue.getLocationDbId());
		attributeValue.setAdditionalInfo(additionalInfo);
		return attributeValue;
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

	public boolean isLocationUsedInAttribute(final Integer locationId) {
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

	private String createAttributeValuesQueryString(final AttributeValueSearchRequestDto attributeValueSearchRequestDto) {
		final StringBuilder sql = new StringBuilder();
		sql.append(ATTRIBUTE_VALUE_SELECT);
		this.appendAttributeValuesFromQuery(sql);
		this.appendAttributeValueSeachFilters(sql, attributeValueSearchRequestDto);
		return sql.toString();
	}

	private void appendAttributeValuesFromQuery(final StringBuilder sql) {
		sql.append("FROM atributs a ");
		sql.append(" INNER JOIN germplsm g ON g.gid = a.gid ");
		sql.append(" INNER JOIN cvterm cv ON a.atype = cv.cvterm_id ");
		sql.append(" LEFT JOIN variable_overrides vpo ON vpo.cvterm_id = cv.cvterm_id AND vpo.program_uuid = :programUUID  ");
		sql.append(" LEFT JOIN names ON names.gid = a.gid AND names.nstat = 1 ");
		sql.append(" WHERE 1=1 ");
	}

	private void appendAttributeValueSeachFilters(final StringBuilder sql, final AttributeValueSearchRequestDto requestDTO) {
		if (!CollectionUtils.isEmpty(requestDTO.getAttributeDbIds())) {
			sql.append(" AND cv.cvterm_id IN (:attributeDbIds)");
		}
		if (!CollectionUtils.isEmpty(requestDTO.getAttributeNames())) {
			sql.append(" AND IFNULL(vpo.alias, cv.name) IN (:attributeNames)");
		}
		if (!CollectionUtils.isEmpty(requestDTO.getAttributeValueDbIds())) {
			sql.append(" AND a.aid IN (:attributeValueDbIds)");
		}

		if (!CollectionUtils.isEmpty(requestDTO.getDataTypes())) {
			sql.append(" AND cv.cvterm_id IN (  SELECT vrsr.subject_id ");
			sql.append(
				"						FROM cvterm_relationship vrsr INNER JOIN cvterm s ON s.cvterm_id = vrsr.object_id AND vrsr.type_id = ");
			sql.append(TermRelationshipId.HAS_SCALE.getId() + " ");
			sql.append(
				" 						INNER JOIN cvterm_relationship drsr ON drsr.subject_id = vrsr.object_id AND drsr.type_id = ");
			sql.append(TermRelationshipId.HAS_TYPE.getId() + " AND drsr.object_id IN (:dataTypeIds) ) ");
		}

		if (!CollectionUtils.isEmpty(requestDTO.getGermplasmDbIds())) {
			sql.append(" AND g.germplsm_uuid IN (:germplasmDbIds)");
		}
		if (!CollectionUtils.isEmpty(requestDTO.getExternalReferenceIDs())) {
			sql.append(" AND EXISTS (SELECT * FROM external_reference_atributs ref ");
			sql.append(" WHERE a.aid = ref.aid AND ref.reference_id IN (:referenceIDs)) ");
		}

		if (!CollectionUtils.isEmpty(requestDTO.getExternalReferenceSources())) {
			sql.append(" AND EXISTS (SELECT * FROM external_reference_atributs ref ");
			sql.append(" WHERE a.aid = ref.aid AND ref.reference_source IN (:referenceSources)) ");
		}

		// Search preferred names
		if (!CollectionUtils.isEmpty(requestDTO.getGermplasmNames())) {
			sql.append(" AND g.gid IN ( SELECT n.gid ");
			sql.append(" FROM names n WHERE n.nstat = 1 AND n.nval in (:germplasmNames) ) ");
		}

		if (!CollectionUtils.isEmpty(requestDTO.getMethodDbIds())) {
			sql.append(" AND cv.cvterm_id IN (  SELECT mr.subject_id ");
			sql.append(
				"						FROM cvterm_relationship mr INNER JOIN cvterm m ON m.cvterm_id = mr.object_id AND mr.type_id = ");
			sql.append(TermRelationshipId.HAS_METHOD.getId() + " AND m.cvterm_id IN (:methodDbIds) ) ");
		}

		if (!CollectionUtils.isEmpty(requestDTO.getOntologyDbIds())) {
			sql.append(" AND cv.cvterm_id IN (:ontologyDbIds) ");
		}

		if (!CollectionUtils.isEmpty(requestDTO.getScaleDbIds())) {
			sql.append(" AND cv.cvterm_id IN (  SELECT sr.subject_id ");
			sql.append(
				"						FROM cvterm_relationship sr INNER JOIN cvterm s ON s.cvterm_id = sr.object_id AND sr.type_id = ");
			sql.append(TermRelationshipId.HAS_SCALE.getId() + " AND s.cvterm_id IN (:scaleDbIds) ) ");
		}

		if (!CollectionUtils.isEmpty(requestDTO.getTraitDbIds())) {
			sql.append(" AND cv.cvterm_id IN (  SELECT vrpr.subject_id ");
			sql.append(
				"						FROM cvterm_relationship vrpr INNER JOIN cvterm p ON p.cvterm_id = vrpr.object_id AND vrpr.type_id = ");
			sql.append(TermRelationshipId.HAS_PROPERTY.getId() + " ");
			sql.append(
				" 						INNER JOIN cvterm_relationship trpr ON trpr.subject_id = vrpr.object_id AND trpr.type_id = ");
			sql.append(TermRelationshipId.IS_A.getId() + " AND trpr.object_id IN (:traitDbIds) ) ");
		}

		if (!CollectionUtils.isEmpty(requestDTO.getTraitClasses())) {
			sql.append(" AND cv.cvterm_id IN (  SELECT vrpr2.subject_id ");
			sql.append(
				"						FROM cvterm_relationship vrpr2 INNER JOIN cvterm p2 ON p2.cvterm_id = vrpr2.object_id AND vrpr2.type_id = ");
			sql.append(TermRelationshipId.HAS_PROPERTY.getId() + " ");
			sql.append(
				" 						INNER JOIN cvterm_relationship trpr2 ON trpr2.subject_id = vrpr2.object_id AND trpr2.type_id = ");
			sql.append(TermRelationshipId.IS_A.getId() + " ");
			sql.append(
				"						INNER JOIN cvterm trait ON trait.cvterm_id = trpr2.object_id AND trait.name IN (:traitClasses) )");
		}
	}

	private void addAttributeValueSearchParameters(final SQLQuery sqlQuery, final AttributeValueSearchRequestDto requestDTO) {
		if (!CollectionUtils.isEmpty(requestDTO.getAttributeDbIds())) {
			sqlQuery.setParameterList("attributeDbIds", requestDTO.getAttributeDbIds());
		}
		if (!CollectionUtils.isEmpty(requestDTO.getAttributeNames())) {
			sqlQuery.setParameterList("attributeNames", requestDTO.getAttributeNames());
		}
		if (!CollectionUtils.isEmpty(requestDTO.getAttributeValueDbIds())) {
			sqlQuery.setParameterList("attributeValueDbIds", requestDTO.getAttributeValueDbIds());
		}
		if (!CollectionUtils.isEmpty(requestDTO.getDataTypes())) {
			sqlQuery.setParameterList("dataTypeIds", VariableUtils.convertBrapiDataTypeToDataTypeIds(requestDTO.getDataTypes()));
		}

		if (!CollectionUtils.isEmpty(requestDTO.getGermplasmDbIds())) {
			sqlQuery.setParameterList("germplasmDbIds", requestDTO.getGermplasmDbIds());
		}

		if (!CollectionUtils.isEmpty(requestDTO.getExternalReferenceIDs())) {
			sqlQuery.setParameterList("referenceIDs", requestDTO.getExternalReferenceIDs());
		}

		if (!CollectionUtils.isEmpty(requestDTO.getExternalReferenceSources())) {
			sqlQuery.setParameterList("referenceSources", requestDTO.getExternalReferenceSources());
		}

		// Search preferred names
		if (!CollectionUtils.isEmpty(requestDTO.getGermplasmNames())) {
			sqlQuery.setParameterList("germplasmNames", requestDTO.getGermplasmNames());
		}

		if (!CollectionUtils.isEmpty(requestDTO.getMethodDbIds())) {
			sqlQuery.setParameterList("methodDbIds", requestDTO.getMethodDbIds());
		}

		if (!CollectionUtils.isEmpty(requestDTO.getOntologyDbIds())) {
			sqlQuery.setParameterList("ontologyDbIds", requestDTO.getOntologyDbIds());
		}

		if (!CollectionUtils.isEmpty(requestDTO.getScaleDbIds())) {
			sqlQuery.setParameterList("scaleDbIds", requestDTO.getScaleDbIds());
		}

		if (!CollectionUtils.isEmpty(requestDTO.getTraitDbIds())) {
			sqlQuery.setParameterList("traitDbIds", requestDTO.getTraitDbIds());
		}

		if (!CollectionUtils.isEmpty(requestDTO.getTraitClasses())) {
			sqlQuery.setParameterList("traitClasses", requestDTO.getTraitClasses());
		}
	}
}
