package org.generationcp.middleware.dao;

import org.generationcp.middleware.pojos.ExperimentExternalReference;
import org.hibernate.Session;

public class ExperimentExternalReferenceDao extends GenericExternalReferenceDAO<ExperimentExternalReference> {

	public ExperimentExternalReferenceDao(final Session session) {
		super(session);
	}

	@Override
	String getIdField() {
		return "nd_experiment_id";
	}

	@Override
	String getReferenceTable() {
		return "external_reference_experiment";
	}

}
