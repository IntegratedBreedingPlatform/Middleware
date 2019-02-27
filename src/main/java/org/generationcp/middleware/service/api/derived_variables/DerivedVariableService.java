package org.generationcp.middleware.service.api.derived_variables;

import org.generationcp.middleware.domain.etl.MeasurementVariable;

import java.util.Map;
import java.util.Set;

public interface DerivedVariableService {

	Set<String> getDependencyVariables(final int datasetId);

	Set<String> getDependencyVariables(final int datasetId, final int variableId);

	int countCalculatedVariablesInDatasets(final Set<Integer> datasetIds);

	void saveCalculatedResult(
		final String value, final Integer categoricalId, final Integer observationUnitId, final Integer observationId,
		final MeasurementVariable measurementVariable);

	Map<Integer, MeasurementVariable> createVariableIdMeasurementVariableMap(final int datasetId);
}
