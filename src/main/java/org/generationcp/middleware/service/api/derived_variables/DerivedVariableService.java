package org.generationcp.middleware.service.api.derived_variables;

import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.ontology.FormulaVariable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DerivedVariableService {

	Map<Integer, Map<String, List<Object>>> getValuesFromObservations(final int datasetId, final List<Integer> datasetTypeIds, final Map<Integer, Integer> inputVariableDatasetMap);

	Set<FormulaVariable> getMissingFormulaVariablesInStudy(final int studyId, final int variableId);

	Set<FormulaVariable> getFormulaVariablesInStudy(final Integer studyId);

	int countCalculatedVariablesInDatasets(final Set<Integer> datasetIds);

	void saveCalculatedResult(
		final String value, final Integer categoricalId, final Integer observationUnitId, final Integer observationId,
		final MeasurementVariable measurementVariable);

	Map<Integer, MeasurementVariable> createVariableIdMeasurementVariableMap(final int studyId);

	Map<Integer, Map<String, Object>> createInputVariableDatasetReferenceMap(Integer studyId, Integer variableId);
}
