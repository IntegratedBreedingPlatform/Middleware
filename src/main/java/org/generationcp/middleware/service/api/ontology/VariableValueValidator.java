package org.generationcp.middleware.service.api.ontology;

import org.generationcp.middleware.domain.etl.MeasurementVariable;

public interface VariableValueValidator {

	boolean isValid(MeasurementVariable variable);

}
