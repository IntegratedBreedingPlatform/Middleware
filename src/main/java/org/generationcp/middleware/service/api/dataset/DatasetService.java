package org.generationcp.middleware.service.api.dataset;

import com.google.common.collect.Table;
import org.generationcp.middleware.domain.dataset.ObservationDto;
import org.generationcp.middleware.domain.dms.DatasetDTO;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.service.api.study.MeasurementVariableDto;
import org.generationcp.middleware.service.impl.study.StudyInstance;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DatasetService {

	/**
	 * Given a dataset and a list of variables, it will count how many observations it has associated.
	 * Count all the dataset observations if variableIds is EMPTY
 	 * @param datasetId
	 * @param variableIds
	 * @return
	 */
	long countObservationsByVariables(Integer datasetId, List<Integer> variableIds);

	/**
	 * Given a dataset and an instance, it will count how many observations it has associated.
	 * @param datasetId
	 * @param instanceId
	 * @return
	 */
	long countObservationsByInstance(Integer datasetId, Integer instanceId);

	/**
	 * Adds a variable to the dataset. Variable type MUST be Trait or Selection
	 * @param studyId Id of the study
	 * @param datasetId Id of the dataset
	 * @param datasetVariable Variable to be added
	 * @return A measurement variable.
	 */
	void addDatasetVariable(Integer datasetId, Integer variableId, VariableType type, String alias);

	/**
	 * Given a dataset and a list of variables, it will de-associated them from the dataset
	 * @param studyId Id of the study
	 * @param datasetId Id of the dataset
	 * @param variableIds List of variables
	 */
	void removeDatasetVariables(Integer datasetId, List<Integer> variableIds);

	/**
	 * Return if an observation is valid
	 * @param datasetId
	 * @param observationUnitId
	 * @return
	 */
	boolean isValidObservationUnit(Integer datasetId, Integer observationUnitId);

	/**
	 * Given an observationUnitId, observationId, returns a Phenotype
	 * @param observationUnitId
	 * @param observationId
	 * @return
	 */
	Phenotype getPhenotype(Integer observationUnitId, Integer observationId);

	/**
	 * Create a new observation for the specified dataset and observation unit id
	 * Notice that status, updated date and created date are internally set so values in observation object will be discarded
	 * @param studyId Id of the study
	 * @param datasetId Id of the dataset
	 * @param observationUnitId Id of the observation unit
	 * @param observation Observation to be added
	 * @return The new created ObservationDto
	 */
	ObservationDto createObservation(ObservationDto observation);

	/**
	 * Update a phenotype
	 * @param observationId
	 * @param observationDto
	 * @return
	 */
	ObservationDto updatePhenotype(Integer observationId, ObservationDto observationDto);

	/**
	 * Given a dataset, it will retrieve the list of variables to be displayed as columns of the observations table.
	 * It will always return the union between the factors of the parent dataset, the variables of the specified dataset
	 * and some virtual columns that needs to be shown in the observations table (i.e. SAMPLES)
	 * draftMode == TRUE indicates that the dataset variables will only retrieve only the variables that contains draft data
	 * @param studyId Id of the study
	 * @param datasetId Id of the dataset
	 * @param draftMode Will only retrieve variables with draft data if TRUE
	 * @return List of Measurement Variables
	 */
	List<MeasurementVariable> getSubObservationSetColumns(Integer subObservationSetId, Boolean draftMode);

	/**
	 * Given a dataset, it will retrieve the union between the parent dataset variables and the dataset variables
	 * @param studyId Id of the Study
	 * @param datasetId Id of the Dataset
	 * @return List of Measurement Variables.
	 */
	List<MeasurementVariable> getSubObservationSetVariables(Integer subObservationSetId);

	/**
	 * Generates a sub-observation dataset for the indicated parent id
	 * @param cropName Crop name
	 * @param studyId Id of the study
	 * @param parentId Id of the parent dataset
	 * @param datasetGeneratorInput Dataset Generator Input.
	 * @return The new created dataset
	 */
	DatasetDTO generateSubObservationDataset(
		Integer studyId, String datasetName, Integer datasetTypeId,
		List<Integer> instanceIds, Integer observationUnitVariableId, Integer numberOfSubObservationUnits,
		Integer parentId);

	/**
	 * Given a list of dataset types and a study, it will retrieve the study datasets with the specified types
	 * @param studyId Id of the study
	 * @param datasetTypeIds List of dataset types
	 * @return List of datasets
	 */
	List<DatasetDTO> getDatasets(Integer studyId, Set<Integer> datasetTypeIds);

	/*
	 * If variable is input variable to formula, update the phenotypes status as "OUT OF SYNC" for given observation unit
	 */
	void updateDependentPhenotypesStatus(Integer variableId, Integer observationUnitId);

	/**
	 * Return a dataset given the id
	 * @param crop Crop name
	 * @param studyId Id of the study
	 * @param datasetId Id of the dataset
	 * @return
	 */
	DatasetDTO getDataset(Integer datasetId);

	/**
	 * Count all observation units for a dataset (draftMode = FALSE to count all of them, draftMode = TRUE to count only observation
	 * units with at least one draft observation)
	 * @param datasetId Id of the dataset
	 * @param instanceId Id of the instance
	 * @param draftMode Indicates to count all observation units  or draft observations
	 * @return Number of observations units that matches the dataset id and draftMode
	 */
	Integer countAllObservationUnitsForDataset(final Integer datasetId, final Integer instanceId, final Boolean draftMode);

	/**
	 * Count how many observation units are affected by a filter
	 *  (draftMode = FALSE to count all of them, draftMode = TRUE to count only observation
	 * 	 units with at least one draft observation)
	 * @param datasetId Id of the dataset
	 * @param instanceId Id of the instance
	 * @param draftMode draftMode
	 * @param filter Filyer
	 * @return Number of observation units that matches the datasetId, draftMode and filter
	 */
	long countFilteredObservationUnitsForDataset(Integer datasetId, Integer instanceId, final Boolean draftMode,
		ObservationUnitsSearchDTO.Filter filter);

	/**
	 * Returns the list of observation unit rows that matches the search param
	 * @param studyId Id of the study
	 * @param datasetId Id of the dataset
	 * @param searchDTO Search DTO
	 * @return List of ObservationUnitRow
	 */
	List<ObservationUnitRow> getObservationUnitRows(int studyId, int datasetId, ObservationUnitsSearchDTO searchDTO);

	/**
	 * Returns the list of observation unit rows for datasetId
	 * @param studyId Id of the study
	 * @param datasetId Id of the dataset
	 * @param searchDTO Search DTO
	 * @return List of ObservationUnitRow
	 */
	List<ObservationUnitRow> getAllObservationUnitRows(int studyId, int datasetId);

	/**
	 * Validates if dataset name is available
	 * @param name
	 * @param studyId
	 * @return
	 */
	Boolean isDatasetNameAvailable(final String name, final int studyId);

	/**
	 * Return number of children of a dataset
	 * @param parentId
	 * @return
	 */
	Integer getNumberOfChildren(Integer parentId);

	/**
	 * Return the list of instances for an specific dataset
	 * @param studyId Id of the study
	 * @param datasetId Id of the dataset
	 * @return List of StudyInstances
	 */
	List<StudyInstance> getDatasetInstances(Integer datasetId);

	/**
	 * Deletes a phenotype
	 * @param phenotypeId
	 */
	void deletePhenotype(Integer phenotypeId);

	/**
	 * Get the list of dataset variables of an specific variable type
	 * @param studyId Id of the study
	 * @param datasetId If of the dataset
	 * @param variableType Variable Type
	 * @return List of variables
	 */
	List<MeasurementVariableDto> getDatasetVariablesByType(Integer datasetId, VariableType variableType);

	/**
	 * It will accept all the draft data even when there are out of bounds values for numerical types.
	 * @param studyId Id of the study
	 * @param datasetId Id of the dataset
	 */
	void acceptAllDatasetDraftData(Integer datasetId);

	/**
	 * Return a map with all needed information to show in dataset observation table
	 * @param datasetId
	 * @param selectionMethodsAndTraits
	 * @param observationUnitIds
	 * @return
	 */
	Map<String, ObservationUnitRow> getObservationUnitsAsMap(
		int datasetId,
		List<MeasurementVariable> selectionMethodsAndTraits, List<String> observationUnitIds);

	/**
	 * Import table into dataset
	 * @param datasetId
	 * @param table
	 * @param draftMode
	 */
	void importDataset(Integer datasetId, Table<String, String, String> table, Boolean draftMode);

	/**
	 * Return all measurements variables from dataset
	 * @param datasetId
	 * @return
	 */
	List<MeasurementVariable> getDatasetMeasurementVariables(Integer datasetId);

	/**
	 * Delete dataset
	 * @param datasetId
	 */
	void deleteDataset(int datasetId);

	/**
	 * Return map with information by InstanceId
	 * @param studyId
	 * @param datasetId
	 * @param instanceIds
	 * @return
	 */
	Map<Integer, List<ObservationUnitRow>> getInstanceIdToObservationUnitRowsMap(
		int studyId, int datasetId, List<Integer> instanceIds);

	/**
	 * Count obseravtion grouped by instance
	 * @param datasetId
	 * @return
	 */
	Map<String, Long> countObservationsGroupedByInstance(Integer datasetId);

	/**
	 * Get the list of dataset variables with specific types indicated in variableTypes list
	 * @param projectId Id of the project
	 * @param variableTypes
	 * @return List of measurement variables
	 */
	List<MeasurementVariable> getMeasurementVariables(Integer projectId, List<Integer> variableTypes);

	/**
	 * It will reject all the draft data for a dataset
	 * @param studyId Id of the study
	 * @param datasetId Id of the dataset
	 */
	void rejectDatasetDraftData(Integer datasetId);

	/**
	 *
	 * @param studyId Id of the study
	 * @param datasetId Id of the dataset
	 * @return a boolean indicating if the dataset draft data has out of bound values or not
	 */
	Boolean hasDatasetDraftDataOutOfBounds(Integer datasetId);

	/**
	 * Accepts the in bounds values for the draft data and set as missing the out of bounds values
	 * @param studyId Id of the study
	 * @param datasetId Id of the dataset
	 */
	void acceptDraftDataAndSetOutOfBoundsToMissing(Integer datasetId);

	/**
	 * Count how many instances and observations are filtered given a filter with a not null variable
	 * @param studyId Id of the study
	 * @param datasetId Id of the dataset
	 * @param filter Filter
	 * @return FilteredPhenotypesInstancesCountDTO
	 */
	FilteredPhenotypesInstancesCountDTO countFilteredInstancesAndPhenotypes(
		Integer datasetId, ObservationUnitsSearchDTO filter);

	/**
	 * Accept the draft values that are retrieved after filtering by searchDTO.
	 * variableId in searchDTO can not be null
	 * @param studyId Id of the study
	 * @param datasetId Id of the dataset
	 * @param searchDTO searchDTO
	 */
	void acceptDraftDataFilteredByVariable(Integer datasetId, ObservationUnitsSearchDTO searchDTO, int studyId);
}
