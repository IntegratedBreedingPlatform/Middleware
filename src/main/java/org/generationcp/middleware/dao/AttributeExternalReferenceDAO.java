package org.generationcp.middleware.dao;

import org.generationcp.middleware.pojos.AttributeExternalReference;

public class AttributeExternalReferenceDAO extends GenericExternalReferenceDAO<AttributeExternalReference> {

	@Override
	String getIdField() {
		return "aid";
	}

	@Override
	String getReferenceTable() {
		return "external_reference_atributs";
	}
}
