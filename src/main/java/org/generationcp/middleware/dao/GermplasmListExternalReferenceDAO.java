package org.generationcp.middleware.dao;

import org.generationcp.middleware.pojos.GermplasmListExternalReference;
import org.hibernate.Session;

public class GermplasmListExternalReferenceDAO extends GenericExternalReferenceDAO<GermplasmListExternalReference> {

	public GermplasmListExternalReferenceDAO(final Session session) {
		super(session);
	}

	@Override
	String getIdField() {
		return "listid";
	}

	@Override
	String getReferenceTable() {
		return "external_reference_listnms";
	}

}
