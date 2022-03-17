package org.generationcp.middleware.dao;

import org.generationcp.middleware.pojos.GermplasmListExternalReference;

public class GermplasmListExternalReferenceDAO extends GenericExternalReferenceDAO<GermplasmListExternalReference> {

	@Override
	String getIdField() {
		return "listid";
	}

	@Override
	String getReferenceTable() {
		return "external_reference_listnms";
	}

}
