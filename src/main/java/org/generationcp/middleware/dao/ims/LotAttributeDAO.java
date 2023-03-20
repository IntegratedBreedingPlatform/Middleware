package org.generationcp.middleware.dao.ims;

import org.apache.commons.collections.CollectionUtils;
import org.generationcp.middleware.dao.GenericAttributeDAO;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.domain.shared.AttributeDto;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.LotAttribute;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.AliasToBeanResultTransformer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO class for {@link Lot Attribute}.
 */
public class LotAttributeDAO extends GenericAttributeDAO<LotAttribute> {

	private static final String COUNT_ATTRIBUTE_WITH_VARIABLES =
		"SELECT COUNT(A.ATYPE) FROM IMS_LOT_ATTRIBUTE A INNER JOIN IMS_LOT L ON L.LOTID = A.LOTID WHERE A.ATYPE IN (:variableIds)";

	private static final String GET_LOT_ATTRIBUTES = "Select a.aid AS id, "
		+ "cv.cvterm_id as variableId, "
		+ "a.aval AS value, "
		+ "IFNULL(vpo.alias, cv.name) AS variableName, "
		+ "cp.value AS variableTypeName, "
		+ "cv.definition AS variableDescription, "
		+ "CAST(a.adate AS CHAR(255)) AS date, "
		+ "a.alocn AS locationId, "
		+ "l.lname AS locationName, "
		+ "(select exists(select 1 from file_metadata f "
		+ "		inner join file_metadata_cvterm fmc on f.file_id = fmc.file_metadata_id "
		+ "		where f.lotId = a.lotId and fmc.cvterm_id = a.atype))  AS hasFiles "
		+ "FROM ims_lot_attribute a "
		+ "INNER JOIN cvterm cv ON a.atype = cv.cvterm_id "
		+ "INNER JOIN cvtermprop cp ON cp.type_id = " + TermId.VARIABLE_TYPE.getId() + " and cv.cvterm_id = cp.cvterm_id "
		+ "		AND cp.value = '" + VariableType.INVENTORY_ATTRIBUTE.getName() + "' "
		+ "LEFT JOIN location l on a.alocn = l.locid "
		+ "LEFT JOIN variable_overrides vpo ON vpo.cvterm_id = cv.cvterm_id AND vpo.program_uuid = :programUUID "
		+ "WHERE a.lotId = :lotId ";

	final String GET_LOT_ATTRIBUTE_KEYVALUE_BY_LOTID = "SELECT "
		+ "    u.cvterm_id AS attributeDbId,"
		+ "    a.aval AS value, "
		+ "    a.lotId AS lotId "
		+ " FROM"
		+ "    ims_lot_attribute a"
		+ "        INNER JOIN"
		+ "    cvterm u ON a.atype = u.cvterm_id "
		+ "        INNER JOIN"
		+ "    ims_lot lot ON a.lotId = lot.lotId "
		+ " WHERE"
		+ "    lot.lotId IN (:lotIds) ";

	public LotAttributeDAO(final Session session) {
		super(session);
	}

	public List<AttributeDto> getLotAttributeDtos(final Integer lotId, final String programUUID) {
		try {
			final SQLQuery sqlQuery = this.getSession().createSQLQuery(GET_LOT_ATTRIBUTES);
			this.addQueryScalars(sqlQuery);
			sqlQuery.setParameter("lotId", lotId);
			sqlQuery.setParameter("programUUID", programUUID);

			sqlQuery.setResultTransformer(new AliasToBeanResultTransformer(AttributeDto.class));
			return sqlQuery.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				"Error with getLotAttributeDtos(lotId=" + lotId + "): " + e.getMessage(), e);
		}
	}


	/**
	 * Given a list of Lots return the Variables related with type INVENTORY_ATTRIBUTE.
	 * The programUUID is used to return the expected range and alias of the program if it exists.
	 *
	 * @param List        of lotIds
	 * @param programUUID program's unique id
	 * @return List of Variable or empty list if none found
	 */
	public List<Variable> getLotAttributeVariables(final List<Integer> lotIds, final String programUUID) {
		return this.getAttributeVariables(lotIds, programUUID,
			Arrays.asList(VariableType.INVENTORY_ATTRIBUTE.getId()));
	}

	public Map<Integer, Map<Integer, String>> getAttributesByLotIdsMap(
		final List<Integer> lotIds) {

		final Map<Integer, Map<Integer, String>> attributesMap = new HashMap<>();

		if (CollectionUtils.isEmpty(lotIds)) {
			return attributesMap;
		}

		try {
			final SQLQuery query = this.getSession().createSQLQuery(this.GET_LOT_ATTRIBUTE_KEYVALUE_BY_LOTID);
			query.setParameterList("lotIds", lotIds);

			final List<Object[]> rows = query.list();
			for (final Object[] row : rows) {
				final Integer lotId = (Integer) row[2];
				attributesMap.putIfAbsent(lotId, new HashMap<>());
				attributesMap.get(lotId).put((Integer) row[0], (String) row[1]);
			}

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error with getLotAttributeVariables(lotIds=" + lotIds + "): " + e.getMessage(), e);
		}
		return attributesMap;
	}

	public List<LotAttribute> getLotAttributeByIds(final List<Integer> lotIdList) {
		List<LotAttribute> attributes = new ArrayList<>();
		if (lotIdList != null && !lotIdList.isEmpty()) {
			try {
				final String sql = "SELECT {a.*}" + " FROM ims_lot_attribute a" + " WHERE a.lotId in (:lotIdList)";
				final SQLQuery query = this.getSession().createSQLQuery(sql);
				query.addEntity("a", LotAttribute.class);
				query.setParameterList("lotIdList", lotIdList);
				attributes = query.list();
			} catch (final HibernateException e) {
				throw new MiddlewareQueryException(
					"Error with getLotAttributeValuesIdList(lotIdList=" + lotIdList + "): " + e.getMessage(), e);
			}
		}
		return attributes;
	}

	@Override
	protected LotAttribute getNewAttributeInstance(final Integer id) {
		final LotAttribute newAttribute = new LotAttribute();
		newAttribute.setLotId(id);
		return newAttribute;
	}

	@Override
	protected String getCountAttributeWithVariablesQuery() {
		return COUNT_ATTRIBUTE_WITH_VARIABLES;
	}

	@Override
	protected String getAttributeTable() {
		return "ims_lot_attribute";
	}

	@Override
	protected String getForeignKeyToMainRecord() {
		return "lotId";
	}
}
