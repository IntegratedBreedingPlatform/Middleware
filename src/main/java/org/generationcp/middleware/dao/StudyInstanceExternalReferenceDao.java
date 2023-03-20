package org.generationcp.middleware.dao;

import org.generationcp.middleware.pojos.InstanceExternalReference;
import org.hibernate.Session;

public class StudyInstanceExternalReferenceDao extends GenericExternalReferenceDAO<InstanceExternalReference> {

	public StudyInstanceExternalReferenceDao(final Session session) {
		super(session);
	}

	@Override
	String getIdField() {
		return "nd_geolocation_id";
	}

	@Override
	String getReferenceTable() {
		return "external_reference_instance";
	}

}
