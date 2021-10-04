package org.generationcp.middleware.dao.germplasmlist;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.pojos.GermplasmListDataDefaultView;
import org.hibernate.Session;

/**
 * DAO class for {@link GermplasmListDataDefaultView}.
 */
public class GermplasmListDataDefaultViewDAO extends GenericDAO<GermplasmListDataDefaultView, Integer> {

	public GermplasmListDataDefaultViewDAO(final Session session) {
		super(session);
	}

}
