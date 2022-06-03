package org.generationcp.middleware.dao.ims;

import org.generationcp.middleware.dao.GenericAttributeDAO;
import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.pojos.GenericAttribute;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.LotAttribute;

/**
 * DAO class for {@link Lot Attribute}.
 */
public class LotAttributeDAO extends GenericAttributeDAO<LotAttribute> {

	@Override
	protected LotAttribute getNewAttributeInstance(Integer id) {
		LotAttribute newAttribute = new LotAttribute();
		newAttribute.setLotId(id);
		return newAttribute;
	}
}
