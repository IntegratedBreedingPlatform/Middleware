package org.generationcp.middleware.dao;

import org.generationcp.middleware.pojos.InstanceExternalReference;

public class StudyInstanceExternalReferenceDao extends GenericExternalReferenceDAO<InstanceExternalReference> {

	@Override
	String getIdField() {
		return "nd_geolocation_id";
	}

	@Override
	String getReferenceTable() {
		return "external_reference_instance";
	}

}
