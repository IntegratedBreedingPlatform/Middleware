package org.generationcp.middleware.dao;

import org.generationcp.middleware.pojos.StudyExternalReference;

public class StudyExternalReferenceDao extends GenericExternalReferenceDAO<StudyExternalReference> {

	@Override
	String getIdField() {
		return "study_id";
	}

	@Override
	String getReferenceTable() {
		return "external_reference_study";
	}

}
