package org.generationcp.middleware.service.api.derived_variables;

import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.ontology.FormulaVariable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DerivedVariableService {

	/**
	 * Gets the aggregate values of TRAIT variables, grouped by experimentId and variableId.
	 *
	 * @param studyId
	 * @param datasetTypeIds
	 * @param inputVariableDatasetMap - contains input variable id and dataset id from which input variable data will be read from.
	 *                                This is to ensure that even if the input variable has multiple occurrences in study, the data will only
	 *                                come from the dataset specified in this map.
	 * @return
	 */
	Map<Integer, Map<String, List<Object>>> getValuesFromObservations(final int datasetId, final List<Integer> datasetTypeIds,
		final Map<Integer, Integer> inputVariableDatasetMap);

	/**
	 * Gets the list of formula variables that are not yet included in a study.
	 *
	 * @param studyId
	 * @param variableId - the ID of calculated (derived) variable
	 * @return
	 */
	Set<FormulaVariable> getMissingFormulaVariablesInStudy(final int studyId, final int datasetId, final int variableId);

	/**
	 * Gets all formula variables in a study
	 *
	 * @param studyId
	 * @return
	 */
	Set<FormulaVariable> getFormulaVariablesInStudy(final Integer studyId, final Integer datasetId);

	int countCalculatedVariablesInDatasets(final Set<Integer> datasetIds);

	void saveCalculatedResult(
		final String value, final Integer categoricalId, final Integer observationUnitId, final Integer observationId,
		final MeasurementVariable measurementVariable);

	/**
	 * Gets the measurement variables of all environment details, environment conditions and traits in a study.
	 * @param studyId
	 * @return
	 */
	Map<Integer, MeasurementVariable> createVariableIdMeasurementVariableMapInStudy(final int studyId);


	Map<Integer, Map<String, Object>> createInputVariableDatasetReferenceMap(Integer studyId, Integer datasetId, Integer variableId);
}
