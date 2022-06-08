package org.generationcp.middleware.dao.ims;

import org.generationcp.middleware.dao.GenericAttributeDAO;
import org.generationcp.middleware.domain.germplasm.GermplasmAttributeDto;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.LotAttribute;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.transform.AliasToBeanResultTransformer;

import java.util.List;

/**
 * DAO class for {@link Lot Attribute}.
 */
public class LotAttributeDAO extends GenericAttributeDAO<LotAttribute> {

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
		+ "LEFT JOIN location l on a.alocn = l.locid "
		+ "LEFT JOIN variable_overrides vpo ON vpo.cvterm_id = cv.cvterm_id AND vpo.program_uuid = :programUUID "
		+ "WHERE a.lotId = :lotId ";

	public List<GermplasmAttributeDto> getLotAttributeDtos(final Integer lotId, final String programUUID) {
		try {
			final SQLQuery sqlQuery = this.getSession().createSQLQuery(GET_LOT_ATTRIBUTES);
			this.addQueryScalars(sqlQuery);
			sqlQuery.setParameter("lotId", lotId);
			sqlQuery.setParameter("programUUID", programUUID);

			sqlQuery.setResultTransformer(new AliasToBeanResultTransformer(GermplasmAttributeDto.class));
			return sqlQuery.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				"Error with getLotAttributeDtos(lotId=" + lotId + "): " + e.getMessage(), e);
		}
	}

	@Override
	protected LotAttribute getNewAttributeInstance(final Integer id) {
		final LotAttribute newAttribute = new LotAttribute();
		newAttribute.setLotId(id);
		return newAttribute;
	}
}
