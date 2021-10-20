package org.generationcp.middleware.dao;

import org.generationcp.middleware.pojos.ExperimentExternalReference;

public class ExperimentExternalReferenceDao extends GenericExternalReferenceDAO<ExperimentExternalReference> {

	@Override
	String getIdField() {
		return "nd_experiment_id";
	}

	@Override
	String getReferenceTable() {
		return "external_reference_experiment";
	}

}
