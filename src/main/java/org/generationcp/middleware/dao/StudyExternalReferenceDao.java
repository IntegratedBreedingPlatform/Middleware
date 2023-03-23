package org.generationcp.middleware.dao;

import org.generationcp.middleware.pojos.StudyExternalReference;
import org.hibernate.Session;

public class StudyExternalReferenceDao extends GenericExternalReferenceDAO<StudyExternalReference> {

	public StudyExternalReferenceDao(final Session session) {
		super(session);
	}

	@Override
	String getIdField() {
		return "study_id";
	}

	@Override
	String getReferenceTable() {
		return "external_reference_study";
	}

}
