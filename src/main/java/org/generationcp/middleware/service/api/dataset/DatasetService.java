package org.generationcp.middleware.service.api.dataset;

import com.google.common.collect.Table;
import org.generationcp.middleware.api.nametype.GermplasmNameTypeDTO;
import org.generationcp.middleware.domain.dataset.ObservationDto;
import org.generationcp.middleware.domain.dataset.PlotDatasetPropertiesDTO;
import org.generationcp.middleware.domain.dms.DatasetDTO;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.service.api.study.MeasurementVariableDto;
import org.generationcp.middleware.service.impl.study.StudyInstance;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DatasetService {

	/**
	 * Given a dataset and a list of variables, it will count how many observations it has associated.
	 * Count all the dataset observations if variableIds is EMPTY
	 *
	 * @param datasetId
	 * @param variableIds
	 * @return
	 */
	long countObservationsByVariables(Integer datasetId, List<Integer> variableIds);

	/**
	 * Given a dataset and an instance, it will count how many observations it has associated.
	 *
	 * @param datasetId
	 * @param instanceId
	 * @return
	 */
	long countObservationsByInstance(Integer datasetId, Integer instanceId);

	/**
	 * Adds a variable to the dataset. Variable type MUST be Trait or Selection
	 *
	 * @param datasetId  Id of the dataset
	 * @param variableId If of the variable
	 * @param type       Variable type
	 * @param alias      Assigned to the variable in the dataset
	 */
	void addDatasetVariable(Integer datasetId, Integer variableId, VariableType type, String alias);

	/**
	 * Given a dataset and a list of variables, it will de-associated them from the dataset
	 *
	 * @param studyId
	 * @param datasetId   Id of the dataset
	 * @param variableIds List of variables
	 */
	void removeDatasetVariables(Integer studyId, Integer datasetId, List<Integer> variableIds);

	/**
	 * Return if an observation is valid
	 *
	 * @param datasetId
	 * @param observationUnitId
	 * @return
	 */
	boolean isValidObservationUnit(Integer datasetId, Integer observationUnitId);

	boolean isValidDatasetId(Integer datasetId);

	/**
	 * Given an observationUnitId, observationId, returns a Phenotype
	 *
	 * @param observationUnitId
	 * @param observationId
	 * @return
	 */
	Phenotype getPhenotype(Integer observationUnitId, Integer observationId);

	/**
	 * Create a new observation for the specified dataset and observation unit id
	 * Notice that status, updated date and created date are internally set so values in observation object will be discarded
	 *
	 * @param observation Observation to be added
	 * @return The new created ObservationDto
	 */
	ObservationDto createObservation(ObservationDto observation);

	/**
	 * Update a phenotype
	 *
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
	 *
	 * @param draftMode Will only retrieve variables with draft data if TRUE
	 * @return List of Measurement Variables
	 */
	List<MeasurementVariable> getObservationSetColumns(Integer studyId, Integer subObservationSetId, Boolean draftMode);

	/**
	 * Given a dataset, it will retrieve the union between the parent dataset variables and the dataset variables
	 *
	 * @param observationSetId Id of the Dataset
	 * @return List of Measurement Variables.
	 */
	List<MeasurementVariable> getObservationSetVariables(Integer observationSetId);

	/**
	 * Generates a sub-observation dataset for the indicated parent id
	 *
	 * @param studyId                     Id of the study
	 * @param datasetName                 Name of the new dataset
	 * @param datasetTypeId               Type of the new dataset
	 * @param instanceIds                 List of instances to which new experiments will be created
	 * @param observationUnitVariableId   Variable to define the sequence number
	 * @param numberOfSubObservationUnits Number of sub-obs to be created per experiment
	 * @param parentId                    Id of the parent dataset
	 * @return The new created dataset
	 */
	DatasetDTO generateSubObservationDataset(
		Integer studyId, String datasetName, Integer datasetTypeId,
		List<Integer> instanceIds, Integer observationUnitVariableId, Integer numberOfSubObservationUnits,
		Integer parentId);

	/**
	 * Given a list of dataset types and a study, it will retrieve the study datasets with the specified types
	 *
	 * @param studyId        Id of the study
	 * @param datasetTypeIds List of dataset types
	 * @return List of datasets
	 */
	List<DatasetDTO> getDatasets(Integer studyId, Set<Integer> datasetTypeIds);

	/**
	 * Given a list of dataset types and a study, it will retrieve the study datasets with variables and with the specified types
	 *
	 * @param studyId        Id of the study
	 * @param datasetTypeIds List of dataset types
	 * @return List of datasets
	 */
	List<DatasetDTO> getDatasetsWithVariables(Integer studyId, Set<Integer> datasetTypeIds);

	/**
	 * If the variable is input variable for a formula, update the phenotypes status as "OUT OF SYNC" for given observation unit.
	 * This will also update the phenotype status of calculated variables in plot observation if the observation unit is a sub-observation.
	 *
	 * @param variableId
	 * @param observationUnitIds
	 */
	void updateDependentPhenotypesAsOutOfSync(Integer variableId, Set<Integer> observationUnitIds);

	/**
	 * Update the phenotype status as "OUT OF SYNC" for given calculated variables and observation unit.
	 * This will also update the phenotype status of calculated variables in plot observation if the observation unit is a sub-observation.
	 *
	 * @param targetVariableIds
	 * @param observationUnitIds
	 */
	void updateOutOfSyncPhenotypes(Set<Integer> targetVariableIds, Set<Integer> observationUnitIds);

	/**
	 * Update the phenotype status as "OUT OF SYNC" for calculated variables using given variables as inputs.
	 * This will update the phenotype status of calculated variables in all observation for the specified geolocation (trial instance)
	 *
	 * @param geolocation
	 * @param variableIds
	 */
	void updateDependentPhenotypesStatusByGeolocation(Integer geolocation, List<Integer> variableIds);

	/**
	 * Return a dataset given the id
	 *
	 * @param datasetId Id of the dataset
	 * @return
	 */
	DatasetDTO getDataset(Integer datasetId);

	/**
	 * Get dataset that observationUnitDbId belongs to
	 */
	DatasetDTO getDatasetByObsUnitDbId(String observationUnitDbId);

	/**
	 * Count all observation units for a dataset (draftMode = FALSE to count all of them, draftMode = TRUE to count only observation
	 * units with at least one draft observation)
	 *
	 * @param datasetId  Id of the dataset
	 * @param instanceIds Id of the instance
	 * @param draftMode  Indicates to count all observation units  or draft observations
	 * @return Number of observations units that matches the dataset id and draftMode
	 */
	Integer countAllObservationUnitsForDataset(Integer datasetId, List<Integer> instanceIds, Boolean draftMode);

	/**
	 * Count how many observation units are affected by a filter
	 * (draftMode = FALSE to count all of them, draftMode = TRUE to count only observation
	 * units with at least one draft observation)
	 *
	 * @param datasetId  Id of the dataset
	 * @param instanceIds Id of the instance
	 * @param draftMode  draftMode
	 * @param filter     Filyer
	 * @return Number of observation units that matches the datasetId, draftMode and filter
	 */
	long countFilteredObservationUnitsForDataset(Integer datasetId, List<Integer> instanceIds, Boolean draftMode,
		ObservationUnitsSearchDTO.Filter filter);

	/**
	 * Returns the list of observation unit rows that matches the search param
	 *
	 * @param studyId   Id of the study
	 * @param datasetId Id of the dataset
	 * @param searchDTO Search DTO
	 * @param pageable Pagination parameters
	 * @return List of ObservationUnitRow
	 */
	List<ObservationUnitRow> getObservationUnitRows(int studyId, int datasetId, ObservationUnitsSearchDTO searchDTO, Pageable pageable);

	/**
	 * Returns the list of observation unit rows (represented as List of HashMap) that matches the search param.
	 *
	 * @param studyId
	 * @param datasetId
	 * @param searchDTO
	 * @return List of Variable (Column) Name and Value Map
	 */
	List<Map<String, Object>> getObservationUnitRowsAsMapList(
		int studyId, int datasetId, ObservationUnitsSearchDTO searchDTO, Pageable pageable);

	/**
	 * Returns the list of observation unit rows for datasetId
	 *
	 * @param studyId   Id of the study
	 * @param datasetId Id of the dataset
	 * @param visibleColumns List of columns that will be included in the search query.
	 * @return List of ObservationUnitRow
	 */
	List<ObservationUnitRow> getAllObservationUnitRows(int studyId, int datasetId, Set<String> visibleColumns);

	/**
	 * Validates if dataset name is available
	 *
	 * @param name
	 * @param studyId
	 * @return
	 */
	Boolean isDatasetNameAvailable(String name, int studyId);

	/**
	 * Return number of children of a dataset
	 *
	 * @param parentId
	 * @return
	 */
	Integer getNumberOfChildren(Integer parentId);

	/**
	 * Return the list of instances for an specific dataset
	 *
	 * @param datasetId Id of the dataset
	 * @return List of StudyInstances
	 */
	List<StudyInstance> getDatasetInstances(Integer datasetId);

	/**
	 * Deletes a phenotype
	 *
	 * @param phenotypeId
	 */
	void deletePhenotype(Integer phenotypeId);

	/**
	 * Get the list of dataset variables of an specific variable type
	 *
	 * @param datasetId    If of the dataset
	 * @param variableType Variable Type
	 * @return List of variables
	 */
	List<MeasurementVariableDto> getDatasetVariablesByType(Integer datasetId, VariableType variableType);

	/**
	 * Return a map with all needed information to show in dataset observation table
	 *
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
	 *
	 * @param datasetId
	 * @param table
	 * @param draftMode
	 * @param allowDateAndCharacterBlankValue
	 */
	Table<String, Integer, Integer> importDataset(Integer datasetId, Table<String, String, String> table, Boolean draftMode, Boolean allowDateAndCharacterBlankValue);

	/**
	 * Return all measurements variables from dataset
	 *
	 * @param datasetId
	 * @return
	 */
	List<MeasurementVariable> getDatasetMeasurementVariables(Integer datasetId);

	List<MeasurementVariable> getDatasetMeasurementVariablesByVariableType(Integer datasetId, List<Integer> variableTypes);

	/**
	 * Return map with information by InstanceId
	 *
	 * @param studyId
	 * @param datasetId
	 * @param instanceIds
	 * @return
	 */
	Map<Integer, List<ObservationUnitRow>> getInstanceIdToObservationUnitRowsMap(int studyId, int datasetId, List<Integer> instanceIds);

	/**
	 * Count obseravtion grouped by instance
	 *
	 * @param datasetId
	 * @return
	 */
	Map<String, Long> countObservationsGroupedByInstance(Integer datasetId);

	/**
	 * Get the list of dataset variables with specific types indicated in variableTypes list
	 *
	 * @param projectId     Id of the project
	 * @param variableTypes
	 * @return List of measurement variables
	 */
	List<MeasurementVariable> getObservationSetVariables(Integer projectId, List<Integer> variableTypes);

	/**
	 * It will reject all the draft data for a dataset
	 *
	 * @param datasetId   Id of the dataset
	 * @param instanceIds Id of the instance
	 */
	void rejectDatasetDraftData(Integer datasetId, Set<Integer> instanceIds);

	/**
	 * @param datasetId Id of the dataset
	 * @return a boolean indicating if the dataset draft data has out of bound values or not
	 */
	Boolean hasDatasetDraftDataOutOfBounds(Integer datasetId);

	/**
	 * It will accept all the draft data even when there are out of bounds values for numerical types.
	 *
	 * @param studyId
	 * @param datasetId
	 * @param instanceIds
	 */
	void acceptDatasetDraftData(Integer studyId, Integer datasetId, Set<Integer> instanceIds);

	/**
	 * Accepts the in bounds values for the draft data and set as missing the out of bounds values
	 *
	 * @param studyId
	 * @param datasetId
	 * @param instanceIds
	 */
	void acceptDraftDataAndSetOutOfBoundsToMissing(Integer studyId, Integer datasetId, Set<Integer> instanceIds);

	/**
	 * Accept the draft values that are retrieved after filtering by searchDTO.
	 * variableId in searchDTO can not be null
	 *
	 * @param studyId   Id of the study
	 * @param datasetId Id of the dataset
	 * @param searchDTO searchDTO
	 */
	void acceptDraftDataFilteredByVariable(Integer datasetId, ObservationUnitsSearchDTO searchDTO, int studyId);

	/**
	 * Count how many instances and observations are filtered given a filter with a not null variable
	 *
	 * @param datasetId Id of the dataset
	 * @param filter    Filter
	 * @return FilteredPhenotypesInstancesCountDTO
	 */
	FilteredPhenotypesInstancesCountDTO countFilteredInstancesAndObservationUnits(
		Integer datasetId, ObservationUnitsSearchDTO filter);

	/**
	 * Set value to a specific variable
	 *
	 * @param datasetId
	 * @param searchDTO
	 * @param studyId
	 */
	void setValueToVariable(Integer datasetId, ObservationUnitsParamDTO searchDTO, Integer studyId);

	/**
	 * Delete specific variable value
	 *
	 * @param datasetId
	 * @param searchDTO
	 * @param studyId
	 */
	void deleteVariableValues(Integer studyId, Integer datasetId, ObservationUnitsSearchDTO searchDTO);

	boolean allDatasetIdsBelongToStudy(Integer studyId, List<Integer> datasetIds);

	Table<Integer, Integer, Integer> getTrialNumberPlotNumberObservationUnitIdTable(Integer datasetId, Set<Integer> instanceNumbers, Set<Integer> plotNumbers);

	/**
	 * Given a dataset Id and Study Id, it will retrieve how many observations, number of repetitions, and entries it has associated.
	 *
	 * @param datasetId
	 * @param studyId
	 * @return List<InstanceDetailsDTO>
	 */
	List<InstanceDetailsDTO> getInstanceDetails(Integer datasetId, Integer studyId);

	void replaceObservationUnitEntry(List<Integer> observationUnitIds, Integer newEntryId);

	Long countObservationUnits(Integer datasetId);

	long countByVariableIdAndValue(final Integer variableId, final String value);

	long countObservationsByVariableIdAndValue(final Integer variableId, final String value);

	void updatePlotDatasetProperties(Integer studyId, PlotDatasetPropertiesDTO plotDatasetPropertiesDTO, final String programUUID);

	List<GermplasmNameTypeDTO> getDatasetNameTypes(Integer datasetId);

}
