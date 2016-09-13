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

import java.util.List;

import org.generationcp.middleware.dao.dms.InstanceMetadata;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.DataSetType;
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
import org.generationcp.middleware.domain.dms.StudySummary;
import org.generationcp.middleware.domain.dms.StudyValues;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.fieldbook.FieldMapInfo;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.search.StudyResultSet;
import org.generationcp.middleware.domain.search.filter.StudyQueryFilter;
import org.generationcp.middleware.domain.workbench.StudyNode;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.PhenotypeOutlier;
import org.generationcp.middleware.util.CrossExpansionProperties;

/**
 * This is the API for retrieving phenotypic data stored as Studies and datasets from the CHADO schema.
 *
 */
public interface StudyDataManager {

	/**
	 * Get the Study for a specific study id. Retrieves from central if the given ID is positive, otherwise retrieves from local.
	 * 
	 * @param studyId the study's unique id
	 * @return the study or null if not found
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Study getStudy(int studyId) throws MiddlewareException;

	/**
	 * Gets the study.
	 * 
	 * @param studyId the study id
	 * @param hasVariableType the has variable type
	 * @return the study
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Study getStudy(int studyId, boolean hasVariableType) throws MiddlewareException;

	/**
	 * Gets the study id by name.
	 * 
	 * @param studyName the study name
	 * @return the study id by name
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Integer getStudyIdByNameAndProgramUUID(String studyName, String programUUID) throws MiddlewareQueryException;

	/**
	 * Returns list of root or top-level folders and studies.
	 *
	 * @param programUUID program's unique id
	 * @param studyTypes specify types of studies to filter. Must not be null or empty.
	 * @return List of Folder POJOs or empty list if none found
	 */
	List<Reference> getRootFolders(String programUUID, List<StudyType> studyTypes);

	/**
	 * Returns list of children of a folder given its ID.
	 *
	 * @param folderId The id of the folder to match
	 * @param programUUID unique id of the program
	 * @return List of containing study (StudyReference) and folder (FolderReference) references or empty list if none found
	 */
	List<Reference> getChildrenOfFolder(int folderId, String programUUID, List<StudyType> studyTypes);

	/**
	 * Returns the list of DataSet references for a specific study.
	 * 
	 * @param studyId The study id to match
	 * @return List of DatasetReferences belonging to the study or empty list if none found. Never returns null.
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<DatasetReference> getDatasetReferences(int studyId) throws MiddlewareQueryException;

	/**
	 * Returns the DataSet corresponding to the given dataset ID. Retrieves from central if the given ID is positive, otherwise retrieves
	 * from local.
	 * 
	 * @param dataSetId the data set id
	 * @return The DataSet matching the given ID or null if none found
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	DataSet getDataSet(int dataSetId) throws MiddlewareException;

	/**
	 * Gets the experiments given a dataset ID.
	 * 
	 * @param dataSetId The dataset ID to match
	 * @param start The start index of the rows to retrieve
	 * @param numOfRows The number of items to retrieve
	 * @return List of Experiments associated to the dataset ID or empty list if none found
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Experiment> getExperiments(int dataSetId, int start, int numOfRows) throws MiddlewareException;

	/**
	 * Gets the experiments.
	 * 
	 * @param dataSetId the data set id
	 * @param start the start
	 * @param numOfRows the num of rows
	 * @param varTypeList the var type list
	 * @return the experiments
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Experiment> getExperiments(int dataSetId, int start, int numOfRows, VariableTypeList varTypeList) throws MiddlewareException;

	/**
	 * Get the number of experiments in a dataset. Retrieves from central if the given ID is positive, otherwise retrieves from local.
	 * 
	 * @param dataSetId the data set id
	 * @return the count
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	long countExperiments(int dataSetId) throws MiddlewareQueryException;

	/**
	 * Returns the list of study references for a particular search filter.
	 * 
	 * @param filter The filter for the search - could be an instance of BrowseStudyQueryFilter, GidStudyQueryFilter,
	 *        ParentFolderStudyQueryFilter.
	 * @param numOfRows The number of rows to retrieve
	 * @return The result set containing the matching studies
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	StudyResultSet searchStudies(StudyQueryFilter filter, int numOfRows) throws MiddlewareQueryException;

	/**
	 * Returns the list of factors for a specific study. Retrieves from central if the given ID is positive, otherwise retrieves from local.
	 * 
	 * @param studyId the study id
	 * @return The factors of the study stored in a VariableTypeList
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	VariableTypeList getAllStudyFactors(int studyId) throws MiddlewareException;

	/**
	 * Returns the list of variates for a specific study. Retrieves from central if the given ID is positive, otherwise retrieves from
	 * local.
	 * 
	 * @param studyId the study id
	 * @return The variates of the study stored in a VariableTypeList
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	VariableTypeList getAllStudyVariates(int studyId) throws MiddlewareException;

	/**
	 * Adds a study to the local database. Adds an entry into Project, ProjectProperty, ProjectRelationships and Experiment. Inserts
	 * constants and conditions listed in variableTypeList. Sets the parent to the given parentFolderId input parameter.
	 * 
	 * @param parentFolderId The ID of the parent folder
	 * @param variableTypeList The conditions and constants of the Study
	 * @param studyValues The values for the variables to insert
	 * @param programUUID the program UUID
	 * @return StudyReference corresponding to the newly-created Study
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	StudyReference addStudy(int parentFolderId, VariableTypeList variableTypeList, StudyValues studyValues, String programUUID)
			throws MiddlewareQueryException;

	/**
	 * Adds a dataset, dataset labels (factors and variate labels), and parent study association in the local database.
	 * 
	 * @param studyId the study id
	 * @param variableTypeList the variable type list
	 * @param datasetValues the dataset values
	 * @param programUUID the program UUID
	 * @return DatasetReference corresponding to the newly-created DataSet
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	DatasetReference addDataSet(int studyId, VariableTypeList variableTypeList, DatasetValues datasetValues, String programUUID)
			throws MiddlewareQueryException;

	/**
	 * Add a new variable/column to the dataset.
	 * 
	 * @param datasetId the dataset id
	 * @param variableType the variable type
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	void addDataSetVariableType(int datasetId, DMSVariableType variableType) throws MiddlewareQueryException;

	/**
	 * Adds an experiment row to the dataset.
	 * 
	 * @param dataSetId The ID of the dataset to add the experiment into
	 * @param experimentType The type of Experiment - could be ExperimentType.PLOT, ExperimentType.SAMPLE, ExperimentType.AVERAGE,
	 *        ExperimentType.SUMMARY
	 * @param experimentValues The values to set
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	void addExperiment(int dataSetId, ExperimentType experimentType, ExperimentValues experimentValues) throws MiddlewareQueryException;

	/**
	 * Adds or updates an experiment row to the dataset.
	 * 
	 * @param dataSetId The ID of the dataset to add the experiment into
	 * @param experimentType The type of Experiment - could be ExperimentType.PLOT, ExperimentType.SAMPLE, ExperimentType.AVERAGE,
	 *        ExperimentType.SUMMARY
	 * @param experimentValues The values to set
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	void addOrUpdateExperiment(int dataSetId, ExperimentType experimentType, ExperimentValues experimentValues)
			throws MiddlewareQueryException;

	/**
	 * Adds or updates experiment rows to the dataset.
	 * 
	 * @param dataSetId The ID of the dataset to add the experiment into
	 * @param experimentType The type of Experiment - could be ExperimentType.PLOT, ExperimentType.SAMPLE, ExperimentType.AVERAGE,
	 *        ExperimentType.SUMMARY
	 * @param experimentValues The values to set
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	void addOrUpdateExperiment(int dataSetId, ExperimentType experimentType, List<ExperimentValues> experimentValues)
			throws MiddlewareQueryException;

	/**
	 * Adds a Trial Environment. Accepts a variable list and sets up the trial environment data in the local database. It will throw an
	 * exception if the variable in the variable list passed is not recognized for trial environment.
	 * 
	 * @param variableList the variable list
	 * @return ID of the trial environment data created.
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	int addTrialEnvironment(VariableList variableList) throws MiddlewareQueryException;

	/**
	 * Adds a Stock entry. Accepts a variable list and sets up the stock data in the local database. It will throw an exception if the
	 * variable in the variable list is not a stock variable.
	 * 
	 * @param variableList the variable list
	 * @return ID of the stock data created
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	int addStock(VariableList variableList) throws MiddlewareQueryException;

	/**
	 * Returns a list of datasets based on the given type. Retrieves from central if the given ID is positive, otherwise retrieves from
	 * local.
	 * 
	 * @param studyId the study id
	 * @param dataSetType the data set type
	 * @return The list of datasets matching the dataSetType or empty list if non found.
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<DataSet> getDataSetsByType(int studyId, DataSetType dataSetType) throws MiddlewareException;

	/**
	 * Returns the number of experiments matching the given trial environment and variate. Counts from central if the given ID is positive,
	 * otherwise counts from local.
	 * 
	 * @param trialEnvironmentId the trial environment id
	 * @param variateVariableId the variate variable id
	 * @return The count
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	long countExperimentsByTrialEnvironmentAndVariate(int trialEnvironmentId, int variateVariableId) throws MiddlewareQueryException;

	/**
	 * Retrieves the trial environments belonging to the given dataset. Retrieves from central if the given ID is positive, otherwise
	 * retrieves from local.
	 * 
	 * @param datasetId the dataset id
	 * @return The trial environments
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	TrialEnvironments getTrialEnvironmentsInDataset(int datasetId) throws MiddlewareException;

	/**
	 * Retrieves the stocks belonging to the given dataset. Retrieves from central if the given ID is positive, otherwise retrieves from
	 * local.
	 * 
	 * @param datasetId the dataset id
	 * @return The stocks
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Stocks getStocksInDataset(int datasetId) throws MiddlewareException;

	/**
	 * Returns the number of stocks matching the given dataset ID, trial environment ID and variate ID. Counts from central if the given ID
	 * is positive, otherwise counts from local.
	 * 
	 * @param datasetId the dataset id
	 * @param trialEnvironmentId the trial environment id
	 * @param variateStdVarId the variate std var id
	 * @return The count
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	long countStocks(int datasetId, int trialEnvironmentId, int variateStdVarId) throws MiddlewareQueryException;

	/**
	 * Returns the number of observations with value, matching the given dataset ID, trial environment ID and variate ID. Counts from
	 * central if the given ID is positive, otherwise counts from local.
	 * 
	 * @param datasetId the dataset id
	 * @param trialEnvironmentId the trial environment id
	 * @param variateStdVarId the variate std var id
	 * @return The count
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	long countObservations(int datasetId, int trialEnvironmentId, int variateStdVarId) throws MiddlewareQueryException;

	/**
	 * Returns a single dataset belonging to the study with the given type. If there is more than one matching dataset, only one is
	 * returned. If there are none, null is returned.
	 * 
	 * @param studyId the study id
	 * @param type the type
	 * @return the data set
	 * @throws MiddlewareQueryException the middleware query exception
	 */

	DataSet findOneDataSetByType(int studyId, DataSetType type) throws MiddlewareException;

	/**
	 * Light weight variant of {@link #findOneDataSetByType(int, DataSetType)} which does not load entire DataSet, just a DatasetReference.
	 * 
	 * Returns a single dataset reference belonging to the study with the given type. If there is more than one matching dataset, only the
	 * first one is returned. If there are none, null is returned.
	 * 
	 * @param studyId the study id
	 * @param type the dataset type
	 * @return the data set reference
	 */
	DatasetReference findOneDataSetReferenceByType(int studyId, DataSetType type);

	/**
	 * Deletes the dataset matching the given ID.
	 * 
	 * @param datasetId the dataset id
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	void deleteDataSet(int datasetId) throws MiddlewareQueryException;

	/**
	 * Deletes location matching the given dataset ID and location ID.
	 * 
	 * @param datasetId the dataset id
	 * @param locationId the location id
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	void deleteExperimentsByLocation(int datasetId, int locationId) throws MiddlewareQueryException;

	/**
	 * Retrieves the local name associated to the given project ID and standard variable ID.
	 * 
	 * @param projectId the project id
	 * @param standardVariableId the standard variable id
	 * @return The local name
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	String getLocalNameByStandardVariableId(Integer projectId, Integer standardVariableId) throws MiddlewareQueryException;

	/**
	 * Retrieves the details of nursery and trial studies from the currently selected program. Returns the id, name, description, start
	 * date, start year, season and study type of a Nursery or Trial Study. Returns in sorted order of the following: Year (Descending),
	 * Season (Dry/Wet/General), Study Type(Nursery/Trial), Name(Ascending)
	 * 
	 * @param programUUID of the currently selected program
	 * @return The list of study details having the given study type from local and central
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<StudyNode> getAllNurseryAndTrialStudyNodes(String programUUID) throws MiddlewareQueryException;

	/**
	 * Checks if the name specified is an already existing project name.
	 * 
	 * @param name the name
	 * @param programUUID the program UUID
	 * @return true or false
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	boolean checkIfProjectNameIsExistingInProgram(String name, String programUUID) throws MiddlewareQueryException;

	/**
	 * Gets the field map information (entries, reps, plots and count) of the given study id and study type.
	 * 
	 * @param studyIdList the study id list
	 * @param studyType Can be either StudyType.T (Trial) or StudyType.N (Nursery)
	 * @return the FieldMapCount object containing the counts
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<FieldMapInfo> getFieldMapInfoOfStudy(List<Integer> studyIdList, StudyType studyType,
			CrossExpansionProperties crossExpansionProperties) throws MiddlewareQueryException;

	/**
	 * Save or Update Field Map Properties like row, column, block, total rows, total columns, planting order.
	 * 
	 * @param info the info
	 * @param userId the user id
	 * @param isNew the is new
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	void saveOrUpdateFieldmapProperties(List<FieldMapInfo> info, int userId, boolean isNew) throws MiddlewareQueryException;

	/**
	 * Save Project Properties of the Project.
	 * 
	 * @param project the project
	 * @param variableTypeList the variable type list
	 * @param experimentValues the experiment values
	 * @param locationIds the location ids
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	void saveTrialDatasetSummary(DmsProject project, VariableTypeList variableTypeList, List<ExperimentValues> experimentValues,
			List<Integer> locationIds) throws MiddlewareQueryException;

	/**
	 * Retrieve all field map labels in the block of the specified trial instance id.
	 * 
	 * @param datasetId the dataset id
	 * @param geolocationId the geolocation id
	 * @return the all field maps in block by trial instance id
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<FieldMapInfo> getAllFieldMapsInBlockByTrialInstanceId(int datasetId, int geolocationId,
			CrossExpansionProperties crossExpansionProperties) throws MiddlewareQueryException;

	/**
	 * Check if the given id is an existing study.
	 * 
	 * @param id the id
	 * @return true, if is study
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	boolean isStudy(int id) throws MiddlewareQueryException;

	/**
	 * Adds a subFolder. Accepts a parentFolderId, the name and description of the folder. It will throw an exception if the parentFolderId
	 * is not existing in the local database and the name of the folder is not unique
	 * 
	 * @param parentFolderId the parent folder id
	 * @param name the name
	 * @param description the description
	 * @param programUUID the program UUID
	 * @return ID of the folder created
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	int addSubFolder(int parentFolderId, String name, String description, String programUUID) throws MiddlewareQueryException;

	/**
	 * Rename sub folder.
	 * 
	 * @param newFolderName the new folder name
	 * @param folderId the folder id
	 * @param programUUID the program UUID
	 * @return true, if successful
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	boolean renameSubFolder(String newFolderName, int folderId, String programUUID) throws MiddlewareQueryException;

	/**
	 * Logically delete a folder by updating the folder's name and deleting its project relationships.
	 * 
	 * @param id the id
	 * @param programUUID the programUUID
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	void deleteEmptyFolder(int id, String programUUID) throws MiddlewareQueryException;

	/**
	 * checks if the folder is empty given the folder id.
	 * 
	 * @param id the id
	 * @param programUUID the programUUID
	 * @param studyTypes list of StudyType
	 */
	boolean isFolderEmpty(int id, String programUUID, List<StudyType> studyTypes);

	/**
	 * Returns the parent folder of the project. Accepts a project id.
	 * 
	 * @param id the id
	 * @return ID of the folder created
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	DmsProject getParentFolder(int id) throws MiddlewareQueryException;

	/**
	 * Returns the dms project. Accepts a project id.
	 * 
	 * @param id the id
	 * @return DmsProject referenced by id
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	DmsProject getProject(int id) throws MiddlewareQueryException;

	/**
	 * Move dms project.
	 * 
	 * @param sourceId the source id
	 * @param targetId the target id
	 * @param isAStudy the is a study
	 * @return true, if successful
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	boolean moveDmsProject(int sourceId, int targetId, boolean isAStudy) throws MiddlewareQueryException;

	/**
	 * Retrieves the study details of the given study type from from both selected DB instance ordered by db instance then study name.
	 * 
	 * @param studyType Can be any of the types defined in {@link StudyType}
	 * @param start The start index of the rows to retrieve
	 * @param numOfRows The number of items to retrieve
	 * @return The list of study details having the given study type
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<StudyDetails> getStudyDetails(StudyType studyType, String programUUID, int start, int numOfRows) throws MiddlewareQueryException;

	/**
	 * Gets the study details.
	 * 
	 * @param studyType the study type
	 * @param id the id
	 * @return the study details
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	StudyDetails getStudyDetails(StudyType studyType, int id) throws MiddlewareQueryException;

	/**
	 * Retrieves the study details of the all nurseries and trials from both selected DB instance ordered by study name.
	 * 
	 * @param programUUID unique ID of the currently selected program
	 * @param start The start index of the rows to retrieve
	 * @param numOfRows The number of items to retrieve
	 * @return The list of study details of Nurseries and Trials
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<StudyDetails> getNurseryAndTrialStudyDetails(String programUUID, int start, int numOfRows) throws MiddlewareQueryException;

	/**
	 * Retrieves all the study details of the given study type from both central and local ordered by db instance then study name.
	 * 
	 * @param studyType Can be any of the types defined in {@link StudyType}
	 * @param programUUID unique ID of the currenly selected program
	 * @return The list of study details having the given study type
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<StudyDetails> getAllStudyDetails(StudyType studyType, String programUUID) throws MiddlewareQueryException;

	/**
	 * Count all studies of the given study type from selected DB instance.
	 * 
	 * @param studyType Can be any of the types defined in {@link StudyType}
	 * @param programUUID unique ID of the currently selected program
	 * @return The list of study details having the given study type
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	long countStudyDetails(StudyType studyType, String programUUID) throws MiddlewareQueryException;

	/**
	 * Count all studies of the given study type from both central and local.
	 * 
	 * @param studyType Can be any of the types defined in {@link StudyType}
	 * @param programUUID unique ID of the currently selected program
	 * @return The list of study details having the given study type
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	long countAllStudyDetails(StudyType studyType, String programUUID) throws MiddlewareQueryException;

	/**
	 * Retrieves the study details of the all nurseries and trials from both central and local ordered by db instance then study name.
	 * 
	 * @param programUUID unique ID of the currently selected program
	 * @return The list of study details of Nurseries and Trials
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<StudyDetails> getAllNurseryAndTrialStudyDetails(String programUUID) throws MiddlewareQueryException;

	/**
	 * Count all nurseries and trials
	 * 
	 * @param programUUID unique ID of the currently selected program
	 * @return The list of study details from the currently selected program
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	long countAllNurseryAndTrialStudyDetails(String programUUID) throws MiddlewareQueryException;

	/**
	 * Retrieves the folder tree.
	 * 
	 * @return the folder tree
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<FolderReference> getFolderTree() throws MiddlewareQueryException;

	/**
	 * Retrieves a flat list (no tree structuring) of all folders.
	 */
	List<FolderReference> getAllFolders();

	/**
	 * Count plots with plants selectedof dataset.
	 * 
	 * @param dataSetId the data set id
	 * @param variateIds the variate ids
	 * @return the int
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	int countPlotsWithRecordedVariatesInDataset(int dataSetId, List<Integer> variateIds) throws MiddlewareQueryException;

	/**
	 * Gets the geolocation prop value.
	 * 
	 * @param stdVarId the std var id
	 * @param studyId the study id
	 * @return the geolocation prop value
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	String getGeolocationPropValue(int stdVarId, int studyId) throws MiddlewareQueryException;

	/**
	 * Gets the all field maps in block by block id.
	 * 
	 * @param blockId the block id
	 * @return List of all field maps in the block
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<FieldMapInfo> getAllFieldMapsInBlockByBlockId(int blockId) throws MiddlewareQueryException;

	/**
	 * Gets the folder name by id.
	 * 
	 * @param folderId the folder id
	 * @return the folder name by id
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	String getFolderNameById(Integer folderId) throws MiddlewareQueryException;

	/**
	 * Check if study has measurement data.
	 * 
	 * @param datasetId
	 * @param variateIds
	 * @return
	 * @throws MiddlewareQueryException
	 */
	boolean checkIfStudyHasMeasurementData(int datasetId, List<Integer> variateIds) throws MiddlewareQueryException;

	/**
	 * Count the number of variates with recorded data.
	 * 
	 * @param datasetId
	 * @param variateIds
	 * @return
	 * @throws MiddlewareQueryException
	 */
	int countVariatesWithData(int datasetId, List<Integer> variateIds) throws MiddlewareQueryException;

	/**
	 * Check if study has measurement data.
	 * 
	 * @param projectId the project id
	 * @param locationId the location id
	 * @param plotNos list of plotNos
	 * @param cvTermIds list of std var Ids
	 * @return list of plotNo, stdVarId and phenoTypeId
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Object[]> getPhenotypeIdsByLocationAndPlotNo(int projectId, int locationId, List<Integer> plotNos, List<Integer> cvTermIds)
			throws MiddlewareQueryException;

	/**
	 * Check if study has measurement data.
	 * 
	 * @param projectId the project id
	 * @param locationId the location id
	 * @param plotNo
	 * @param cvTermIds list of std var Ids
	 * @return list of plotNo, stdVarId and phenoTypeId
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Object[]> getPhenotypeIdsByLocationAndPlotNo(int projectId, int locationId, Integer plotNo, List<Integer> cvTermIds)
			throws MiddlewareQueryException;

	/**
	 * Save the Phenotype Outlier data
	 * 
	 * @param phenotyleOutliers list of PhenotypeOutliers
	 * @return none
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	void saveOrUpdatePhenotypeOutliers(List<PhenotypeOutlier> phenotyleOutliers) throws MiddlewareQueryException;

	/**
	 * Determines if the data for the specified Trial contains at least 2 replicates with values
	 * 
	 * @param project_id the project id
	 * @param location_id the location id
	 * @param germplamTermId the germplasm CVTerm id
	 * @return true or false
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Boolean containsAtLeast2CommonEntriesWithValues(int projectId, int locationId, int germplasmTermId) throws MiddlewareQueryException;

	/**
	 * Determines the {@link StudyType} for study identified by the provided studyId.
	 * 
	 * @param studyId Identifier of the study to determine study type for.
	 * @return {@link StudyType} of the study. Returns {@code null} if study type can not be determined for the given study.
	 * @throws MiddlewareQueryException if any error occurs during data access.
	 */
	StudyType getStudyType(int studyId) throws MiddlewareQueryException;

	/**
	 * Soft-delete all program studies
	 * 
	 * @param programUUID Program UUID of the studies to be deleted
	 * @throws MiddlewareQueryException if any error occurs during data access.
	 */
	void deleteProgramStudies(String programUUID) throws MiddlewareQueryException;

	List<Experiment> getExperimentsWithTrialEnvironment(int trialDataSetId, int dataSetId, int start, int numRows)
			throws MiddlewareException;

	/**
	 * Updates the rank or order of given variables as they ordered in the given list
	 * 
	 * @param datasetId - project Id of
	 * @param variableIds - list of variable IDs in the order that they will be saved
	 */
	void updateVariableOrdering(int datasetId, List<Integer> variableIds) throws MiddlewareQueryException;

	/**
	 * Gets the geolocation id by project id and trial instance number.
	 * 
	 * @param projectId - study id or dataset id
	 * @param trial instance number
	 * @return the geolocation id
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	public Integer getGeolocationIdByProjectIdAndTrialInstanceNumber(int projectId, String trialInstanceNumber)
			throws MiddlewareQueryException;

	/**
	 * Retrieves the trial instance number by geolocation id
	 * 
	 * @param geolocationId
	 * @return trial instance number
	 * @throws MiddlewareQueryException
	 */
	public String getTrialInstanceNumberByGeolocationId(int geolocationId) throws MiddlewareQueryException;

	/**
	 * Save the geolocation property given the geolocation id, type id and value
	 * 
	 * @param geolocationId
	 * @param typeId
	 * @param value
	 * @throws MiddlewareQueryException
	 */
	public void saveGeolocationProperty(int geolocationId, int typeId, String value) throws MiddlewareQueryException;

	/**
	 * Retrieves all DMS project names with no program uuid.
	 * 
	 * @throws MiddlewareQueryException
	 * @return list of DMS project names with no programUUID
	 */
	public List<String> getAllSharedProjectNames() throws MiddlewareQueryException;

	/**
	 * Checks whether the specified locationIds exist in a given dataset
	 * 
	 * @param locationIds list of location ids
	 * @param dataSetId the id of the dataset
	 * @param experimentTypeId the experiment type
	 * @return
	 * @throws MiddlewareQueryException
	 */
	boolean checkIfAnyLocationIDsExistInExperiments(int studyId, DataSetType dataSetType, List<Integer> locationIds);

	/**
	 *
	 * Retrieves all the StudySummaries of the DMS Project that matches the conditions: SeasonDbId, LocationDbId and ProgramDbId
	 *
	 * @param programDbId Program Identifier
	 * @param locationDbId Location Abbreviation
	 * @param seasonDbId Season or Year
	 * @param pageSize Page Size
	 * @param page Page
	 * @return List of StudySummary
	 * @throws MiddlewareQueryException
	 */
	List<StudySummary> findPagedProjects(String programDbId, String locationDbId, String seasonDbId, Integer pageSize, Integer page)
			throws MiddlewareQueryException;

	/**
	 *
	 * Count how many DMS Project matches the conditions: programDBid, locationDbId and SeasonDbId
	 *
	 * @param programDbId
	 * @param locationDbId
	 * @param seasonDbId
	 * @return Number of programs
	 * @throws MiddlewareQueryException
	 */
	Long countAllStudies(String programDbId, String locationDbId, String seasonDbId) throws MiddlewareQueryException;

	List<InstanceMetadata> getInstanceMetadata(int studyId);
}
