package org.generationcp.middleware.dao;

import org.generationcp.middleware.pojos.AttributeExternalReference;
import org.hibernate.Session;

public class AttributeExternalReferenceDAO extends GenericExternalReferenceDAO<AttributeExternalReference> {

	public AttributeExternalReferenceDAO(final Session session) {
		super(session);
	}

	@Override
	String getIdField() {
		return "aid";
	}

	@Override
	String getReferenceTable() {
		return "external_reference_atributs";
	}
}
