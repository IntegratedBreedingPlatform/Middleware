package org.generationcp.middleware.dao.ims;

import org.generationcp.middleware.dao.GenericAttributeDAO;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.domain.shared.AttributeDto;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.LotAttribute;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.transform.AliasToBeanResultTransformer;

import java.util.ArrayList;
import java.util.List;

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
		+ "null AS hasFiles "
		+ "FROM ims_lot_attribute a "
		+ "INNER JOIN cvterm cv ON a.atype = cv.cvterm_id "
		+ "INNER JOIN cvtermprop cp ON cp.type_id = " + TermId.VARIABLE_TYPE.getId() + " and cv.cvterm_id = cp.cvterm_id "
		+ "		AND cp.value = '" + VariableType.INVENTORY_ATTRIBUTE.getName() + "' "
		+ "LEFT JOIN location l on a.alocn = l.locid "
		+ "LEFT JOIN variable_overrides vpo ON vpo.cvterm_id = cv.cvterm_id AND vpo.program_uuid = :programUUID "
		+ "WHERE a.lotId = :lotId ";

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

	public List<LotAttribute> getLotAttributeValuesIdList(final List<Integer> lotIdList) {
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
}
