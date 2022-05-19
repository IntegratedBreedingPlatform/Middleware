/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.manager.api;

import org.generationcp.middleware.dao.dms.InstanceMetadata;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.DatasetValues;
import org.generationcp.middleware.domain.dms.Experiment;
import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.dms.ExperimentValues;
import org.generationcp.middleware.domain.dms.FolderReference;
import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.domain.dms.Stocks;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.fieldbook.FieldMapInfo;
import org.generationcp.middleware.domain.fieldbook.FieldmapBlockInfo;
import org.generationcp.middleware.domain.sample.SampleDTO;
import org.generationcp.middleware.domain.search.filter.StudyQueryFilter;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.PhenotypeOutlier;
import org.generationcp.middleware.pojos.dms.StudyType;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.api.study.StudyMetadata;
import org.generationcp.middleware.util.CrossExpansionProperties;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This is the API for retrieving phenotypic data stored as Studies and datasets from the CHADO schema.
 */
public interface StudyDataManager {

	/**
	 * Get the Study for a specific study id. Retrieves from central if the given ID is positive, otherwise retrieves from local.
	 *
	 * @param studyId the study's unique id
	 * @return the study or null if not found
	 */
	Study getStudy(int studyId);

	/**
	 * Gets the study.
	 *
	 * @param studyId         the study id
	 * @param hasVariableType the has variable type
	 * @return the study
	 */
	Study getStudy(int studyId, boolean hasVariableType);

	/**
	 * Gets the study id by name.
	 *
	 * @param studyName the study name
	 * @return the study id by name
	 */
	Integer getStudyIdByNameAndProgramUUID(String studyName, String programUUID);

	/**
	 * Returns list of root or top-level folders and studies.
	 *
	 * @param programUUID program's unique id
	 * @return List of Folder POJOs or empty list if none found
	 */
	List<Reference> getRootFolders(String programUUID);

	/**
	 * Returns list of children of a folder given its ID.
	 *
	 * @param folderId    The id of the folder to match
	 * @param programUUID unique id of the program
	 * @return List of containing study (StudyReference) and folder (FolderReference) references or empty list if none found
	 */
	List<Reference> getChildrenOfFolder(int folderId, String programUUID);

	/**
	 * Returns the list of DataSet references for a specific study.
	 *
	 * @param studyId The study id to match
	 * @return List of DatasetReferences belonging to the study or empty list if none found. Never returns null.
	 */
	List<DatasetReference> getDatasetReferences(int studyId);

	/**
	 * Returns the DataSet corresponding to the given dataset ID. Retrieves from central if the given ID is positive, otherwise retrieves
	 * from local.
	 *
	 * @param dataSetId the data set id
	 * @return The DataSet matching the given ID or null if none found
	 */
	DataSet getDataSet(int dataSetId);

	/**
	 * Gets the experiments given a dataset ID.
	 *
	 * @param dataSetId The dataset ID to match
	 * @param start     The start index of the rows to retrieve
	 * @param numOfRows The number of items to retrieve
	 * @return List of Experiments associated to the dataset ID or empty list if none found
	 */
	List<Experiment> getExperiments(int dataSetId, int start, int numOfRows);

	/**
	 * Gets the experiments.
	 *
	 * @param dataSetId   the data set id
	 * @param start       the start
	 * @param numOfRows   the num of rows
	 * @param varTypeList the var type list
	 * @return the experiments
	 */
	List<Experiment> getExperiments(int dataSetId, int start, int numOfRows, VariableTypeList varTypeList);

	/**
	 * Gets the experiments of the given instance/s.
	 *
	 * @param dataSetId       the data set id
	 * @param instanceNumbers - instances to retrieve
	 * @param repNumbers      - repetition numbers to retrieve
	 * @return the experiments
	 */
	List<Experiment> getExperiments(int dataSetId, List<Integer> instanceNumbers, List<Integer> repNumbers);

	/**
	 * Gets the treatment factor variables of the study
	 *
	 * @param dataSetId
	 * @return
	 */
	VariableTypeList getTreatmentFactorVariableTypes(int dataSetId);

	/**
	 * Get the number of experiments in a dataset. Retrieves from central if the given ID is positive, otherwise retrieves from local.
	 *
	 * @param dataSetId the data set id
	 * @return the count
	 */
	long countExperiments(int dataSetId);

	/**
	 * Returns the list of study references for a particular search filter.
	 *
	 * @param filter The filter for the search - could be an instance of BrowseStudyQueryFilter, GidStudyQueryFilter,
	 *               ParentFolderStudyQueryFilter.
	 * @return list of matching studies
	 */
	List<StudyReference> searchStudies(StudyQueryFilter filter);

	/**
	 * Returns the list of factors for a specific study. Retrieves from central if the given ID is positive, otherwise retrieves from local.
	 *
	 * @param studyId the study id
	 * @return The factors of the study stored in a VariableTypeList
	 */
	VariableTypeList getAllStudyFactors(int studyId);

	/**
	 * Returns the list of variates for a specific study. Retrieves from central if the given ID is positive, otherwise retrieves from
	 * local.
	 *
	 * @param studyId the study id
	 * @return The variates of the study stored in a VariableTypeList
	 */
	VariableTypeList getAllStudyVariates(int studyId);

	/**
	 * Adds a dataset, dataset labels (factors and variate labels), and parent study association in the local database.
	 *
	 * @param studyId          the study id
	 * @param variableTypeList the variable type list
	 * @param datasetValues    the dataset values
	 * @param programUUID      the program UUID
	 * @return DatasetReference corresponding to the newly-created DataSet
	 */
	DatasetReference addDataSet(
		int studyId, VariableTypeList variableTypeList, DatasetValues datasetValues, String programUUID, int datasetTypeId);

	/**
	 * Add a new variable/column to the dataset.
	 *
	 * @param datasetId    the dataset id
	 * @param variableType the variable type
	 */
	void addDataSetVariableType(int datasetId, DMSVariableType variableType);

	/**
	 * Adds or updates experiment rows to the dataset.
	 *
	 * @param crop             Crop to which dataset is stored in
	 * @param dataSetId        The ID of the dataset to add the experiment into
	 * @param experimentType   The type of Experiment - could be ExperimentType.PLOT, ExperimentType.SAMPLE, ExperimentType.AVERAGE,
	 *                         ExperimentType.SUMMARY
	 * @param experimentValues The values to set
	 */
	void addOrUpdateExperiment(CropType crop, int dataSetId, ExperimentType experimentType, List<ExperimentValues> experimentValues);

	/**
	 * Returns a list of datasets based on the given type. Retrieves from central if the given ID is positive, otherwise retrieves from
	 * local.
	 *
	 * @param studyId       the study id
	 * @param datasetTypeId the dataset type id
	 * @return The list of datasets matching the datasetTypeId or empty list if non found.
	 */
	List<DataSet> getDataSetsByType(int studyId, int datasetTypeId);

	/**
	 * Retrieves the trial environments belonging to the given dataset. Retrieves from central if the given ID is positive, otherwise
	 * retrieves from local.
	 *
	 * @param datasetId the dataset id
	 * @return The trial environments
	 */
	TrialEnvironments getTrialEnvironmentsInDataset(int datasetId);

	/**
	 * Retrieves the stocks belonging to the given dataset. Retrieves from central if the given ID is positive, otherwise retrieves from
	 * local.
	 *
	 * @param datasetId the dataset id
	 * @return The stocks
	 */
	Stocks getStocksInDataset(int datasetId);

	/**
	 * Returns the number of stocks matching the given dataset ID, trial environment ID and variate ID. Counts from central if the given ID
	 * is positive, otherwise counts from local.
	 *
	 * @param datasetId          the dataset id
	 * @param trialEnvironmentId the trial environment id
	 * @param variateStdVarId    the variate std var id
	 * @return The count
	 */
	long countStocks(int datasetId, int trialEnvironmentId, int variateStdVarId);

	/**
	 * Returns a single dataset belonging to the study with the given type. If there is more than one matching dataset, only one is
	 * returned. If there are none, null is returned.
	 *
	 * @param studyId       the study id
	 * @param datasetTypeId the dataset type id
	 * @return the data set
	 */
	DataSet findOneDataSetByType(int studyId, int datasetTypeId);

	/**
	 * Light weight variant of {@link #findOneDataSetByType(int, int)} which does not load entire DataSet, just a DatasetReference.
	 * <p>
	 * Returns a single dataset reference belonging to the study with the given type. If there is more than one matching dataset, only the
	 * first one is returned. If there are none, null is returned.
	 *
	 * @param studyId       the study id
	 * @param datasetTypeId the dataset type id
	 * @return the data set reference
	 */
	DatasetReference findOneDataSetReferenceByType(int studyId, int datasetTypeId);

	/**
	 * Retrieves the local name associated to the given project ID and standard variable ID.
	 *
	 * @param projectId          the project id
	 * @param standardVariableId the standard variable id
	 * @return The local name
	 */
	String getLocalNameByStandardVariableId(Integer projectId, Integer standardVariableId);

	/**
	 * Checks if the name specified is an already existing project name.
	 *
	 * @param name        the name
	 * @param programUUID the program UUID
	 * @return true or false
	 */
	boolean checkIfProjectNameIsExistingInProgram(String name, String programUUID);

	/**
	 * Gets the field map information (entries, reps, plots and count) of the given study id and study type.
	 *
	 * @param studyIdList the study id list
	 * @return the FieldMapCount object containing the counts
	 */

	List<FieldMapInfo> getFieldMapInfoOfStudy(List<Integer> studyIdList, CrossExpansionProperties crossExpansionProperties);

	/**
	 * Save or Update Field Map Properties like row, column, block, total rows, total columns, planting order.
	 *
	 * @param info   the info
	 * @param userId the user id
	 * @param isNew  the is new
	 */
	void saveOrUpdateFieldmapProperties(List<FieldMapInfo> info, int userId, boolean isNew);

	/**
	 * Retrieve all field map labels in the block of the specified trial instance id.
	 *
	 * @param datasetId     the dataset id
	 * @param geolocationId the geolocation id
	 * @return the all field maps in block by trial instance id
	 */
	List<FieldMapInfo> getAllFieldMapsInBlockByTrialInstanceId(
		int datasetId, int geolocationId,
		CrossExpansionProperties crossExpansionProperties);

	/**
	 * Check if the given id is an existing study.
	 *
	 * @param id the id
	 * @return true, if is study
	 */
	boolean isStudy(int id);

	/**
	 * Adds a subFolder. Accepts a parentFolderId, the name and description of the folder. It will throw an exception if the parentFolderId
	 * is not existing in the local database and the name of the folder is not unique
	 *
	 * @param parentFolderId the parent folder id
	 * @param name           the name
	 * @param description    the description
	 * @param programUUID    the program UUID
	 * @param objective
	 * @return ID of the folder created
	 */
	int addSubFolder(int parentFolderId, String name, String description, String programUUID, String objective);

	/**
	 * Rename sub folder.
	 *
	 * @param newFolderName the new folder name
	 * @param folderId      the folder id
	 * @param programUUID   the program UUID
	 * @return true, if successful
	 */
	boolean renameSubFolder(String newFolderName, int folderId, String programUUID);

	/**
	 * Logically delete a folder by updating the folder's name and deleting its project relationships.
	 *
	 * @param id          the id
	 * @param programUUID the programUUID
	 */
	void deleteEmptyFolder(int id, String programUUID);

	/**
	 * checks if the folder is empty given the folder id.
	 *
	 * @param id          the id
	 * @param programUUID the programUUID
	 */
	boolean isFolderEmpty(int id, String programUUID);

	/**
	 * Returns the parent folder of the project. Accepts a project id.
	 *
	 * @param id the id
	 * @return ID of the folder created
	 */
	DmsProject getParentFolder(int id);

	/**
	 * Returns the dms project. Accepts a project id.
	 *
	 * @param id the id
	 * @return DmsProject referenced by id
	 */
	DmsProject getProject(int id);

	/**
	 * Move dms project.
	 *
	 * @param sourceId the source id
	 * @param targetId the target id
	 * @return true, if successful
	 */
	boolean moveDmsProject(int sourceId, int targetId);

	/**
	 * Retrieves the study details of the given study type from from both selected DB instance ordered by db instance then study name.
	 *
	 * @param studyType Can be any of the types defined in {@link StudyType}
	 * @param start     The start index of the rows to retrieve
	 * @param numOfRows The number of items to retrieve
	 * @return The list of study details having the given study type
	 */
	List<StudyDetails> getStudyDetails(StudyTypeDto studyType, String programUUID, int start, int numOfRows);

	/**
	 * Gets the study details.
	 *
	 * @param id the id
	 * @return the study details
	 */
	StudyDetails getStudyDetails(int id);

	/**
	 * Retrieves the study details of the all nurseries and trials from both selected DB instance ordered by study name.
	 *
	 * @param programUUID unique ID of the currently selected program
	 * @param start       The start index of the rows to retrieve
	 * @param numOfRows   The number of items to retrieve
	 * @return The list of study details of Nurseries and Trials
	 */
	List<StudyDetails> getNurseryAndTrialStudyDetails(String programUUID, int start, int numOfRows);

	/**
	 * Retrieves all the study details of the given study type from both central and local ordered by db instance then study name.
	 *
	 * @param studyType   Can be any of the types defined in {@link StudyType}
	 * @param programUUID unique ID of the currently selected program
	 * @return The list of study details having the given study type
	 */
	List<StudyDetails> getAllStudyDetails(StudyTypeDto studyType, String programUUID);

	/**
	 * Count all studies of the given study type from both central and local.
	 *
	 * @param studyType   Can be any of the types defined in {@link StudyType}
	 * @param programUUID unique ID of the currently selected program
	 * @return The list of study details having the given study type
	 */
	long countAllStudyDetails(StudyTypeDto studyType, String programUUID);

	/**
	 * Count all nurseries and trials
	 *
	 * @param programUUID unique ID of the currently selected program
	 * @return The list of study details from the currently selected program
	 */
	long countAllNurseryAndTrialStudyDetails(String programUUID);

	/**
	 * Count plots with plants selected of dataset.
	 *
	 * @param dataSetId  the data set id
	 * @param variateIds the variate ids
	 * @return the int
	 */
	int countPlotsWithRecordedVariatesInDataset(int dataSetId, List<Integer> variateIds);

	/**
	 * returns methods of experiments
	 *
	 * @param dataSetId
	 * @param variableId
	 * @param trialInstances
	 * @return
	 */
	List<Method> getMethodsFromExperiments(int dataSetId, Integer variableId, List<String> trialInstances);

	/**
	 * Gets the all field maps in block by block id.
	 *
	 * @param blockId the block id
	 * @return List of all field maps in the block
	 */
	List<FieldMapInfo> getAllFieldMapsInBlockByBlockId(int blockId);

	/**
	 * Gets the folder name by id.
	 *
	 * @param folderId the folder id
	 * @return the folder name by id
	 */
	String getFolderNameById(Integer folderId);

	/**
	 * Check if study has measurement data.
	 *
	 * @param datasetId
	 * @param variateIds
	 * @return
	 */
	boolean checkIfStudyHasMeasurementData(int datasetId, List<Integer> variateIds);

	/**
	 * Count the number of variates with recorded data.
	 *
	 * @param datasetId
	 * @param variateIds
	 * @return
	 */
	int countVariatesWithData(int datasetId, List<Integer> variateIds);

	/**
	 * Check if study has measurement data.
	 *
	 * @param projectId  the project id
	 * @param locationId the location id
	 * @param plotNo
	 * @param cvTermIds  list of std var Ids
	 * @return list of plotNo, stdVarId and phenoTypeId
	 */
	List<Object[]> getPhenotypeIdsByLocationAndPlotNo(int projectId, int locationId, Integer plotNo, List<Integer> cvTermIds);

	/**
	 * Save the Phenotype Outlier data
	 *
	 * @param phenotypeOutliers list of PhenotypeOutliers
	 * @return none
	 */
	void saveOrUpdatePhenotypeOutliers(List<PhenotypeOutlier> phenotypeOutliers);

	/**
	 * Determines if the data for the specified Trial contains at least 2 replicates with values
	 *
	 * @param projectId       the project id
	 * @param locationId      the location id
	 * @param germplasmTermId the germplasm CVTerm id
	 * @return true or false
	 */
	Boolean containsAtLeast2CommonEntriesWithValues(int projectId, int locationId, int germplasmTermId);

	List<Experiment> getExperimentsWithTrialEnvironment(int trialDataSetId, int dataSetId, int start, int numRows);

	/**
	 * Updates the rank or order of given variables as they ordered in the given list
	 *
	 * @param datasetId   - project Id of
	 * @param variableIds - list of variable IDs in the order that they will be saved
	 */
	void updateVariableOrdering(int datasetId, List<Integer> variableIds);

	/**
	 * Checks whether the specified locationIds exist in a given dataset
	 *
	 * @param studyId
	 * @param datasetTypeId
	 * @param locationIds
	 * @return
	 */
	boolean checkIfAnyLocationIDsExistInExperiments(int studyId, int datasetTypeId, List<Integer> locationIds);

	List<InstanceMetadata> getInstanceMetadata(int studyId);

	StudyMetadata getStudyMetadataForInstance(Integer instanceId);

	Integer getProjectIdByStudyDbId(Integer studyDbId);

	/**
	 * Retrieves a map with the values of SAMPLES by ExperimentId Key.
	 *
	 * @param studyDbId
	 * @return
	 */
	Map<Integer, String> getExperimentSampleMap(Integer studyDbId);

	/**
	 * @param studyId
	 * @return a map of experiments ids with a list of it sampled plants
	 */
	Map<Integer, List<SampleDTO>> getExperimentSamplesDTOMap(Integer studyId);

	Map<String, Integer> getInstanceGeolocationIdsMap(Integer studyId);

	List<StudyTypeDto> getAllStudyTypes();

	StudyTypeDto getStudyTypeByName(String name);

	StudyTypeDto getStudyTypeByLabel(String label);

	List<StudyTypeDto> getAllVisibleStudyTypes();

	String getProjectStartDateByProjectId(int projectId);

	boolean isLocationIdVariable(int studyId, String variableName);

	Map<String, String> createInstanceLocationIdToNameMapFromStudy(int studyId);

	StudyTypeDto getStudyTypeByStudyId(Integer studyIdentifier);

	/**
	 * Returns list of root or top-level folders and studies.
	 *
	 * @param programUUID program's unique id
	 * @return List of Folder POJOs or empty list if none found
	 */
	List<Reference> getRootFoldersByStudyType(String programUUID, Integer studyTypeId);

	/**
	 * Returns list of children of a folder given its ID.
	 *
	 * @param folderId    The id of the folder to match
	 * @param programUUID unique id of the program
	 * @return List of containing study (StudyReference) and folder (FolderReference) references or empty list if none found
	 */
	List<Reference> getChildrenOfFolderByStudyType(int folderId, String programUUID, Integer studyTypeId);

	StudyReference getStudyReference(Integer studyId);

	void updateStudyLockedStatus(Integer studyId, Boolean isLocked);

	boolean areAllInstancesExistInDataset(Integer datasetId, Set<Integer> instanceIds);

	String getBlockId(int datasetId, Integer trialInstance);

	FieldmapBlockInfo getBlockInformation(int blockId);

	Map<Integer, String> getGeolocationByInstanceId(Integer datasetId, Integer instanceDbId);

	Boolean instancesExist(Set<Integer> instanceIds);

	Map<Integer, String> getPhenotypeByVariableId(Integer datasetId, Integer instanceDbId);

	boolean renameStudy(String newStudyName, int studyId, String programUUID);
}
