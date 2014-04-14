/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/

package org.generationcp.middleware.manager.api;

import java.util.List;

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
import org.generationcp.middleware.domain.dms.StudyValues;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.fieldbook.FieldMapInfo;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.search.StudyResultSet;
import org.generationcp.middleware.domain.search.filter.StudyQueryFilter;
import org.generationcp.middleware.domain.workbench.StudyNode;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.pojos.dms.DmsProject;

/**
 * This is the API for retrieving phenotypic data stored as Studies and datasets
 * from the CHADO schema.
 * 
 */
public interface StudyDataManager{

    /**
     * Get the Study for a specific study id. Retrieves from central if the
     * given ID is positive, otherwise retrieves from local.
     *
     * @param studyId the study's unique id
     * @return the study or null if not found
     * @throws MiddlewareQueryException the middleware query exception
     */
    Study getStudy(int studyId) throws MiddlewareQueryException;
    
    /**
     * Gets the study.
     *
     * @param studyId the study id
     * @param hasVariableType the has variable type
     * @return the study
     * @throws MiddlewareQueryException the middleware query exception
     */
    Study getStudy(int studyId, boolean hasVariableType) throws MiddlewareQueryException;

    /**
     * Gets the study id by name.
     *
     * @param studyName the study name
     * @return the study id by name
     * @throws MiddlewareQueryException the middleware query exception
     */
    int getStudyIdByName(String studyName) throws MiddlewareQueryException;

    /**
     * Returns list of root or top-level folders from specified database.
     *
     * @param instance Can be CENTRAL or LOCAL
     * @return List of Folder POJOs or empty list if none found
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<FolderReference> getRootFolders(Database instance) throws MiddlewareQueryException;

    /**
     * Returns list of children of a folder given its ID. Retrieves from central
     * if the given ID is positive, otherwise retrieves from local.
     *
     * @param folderId The id of the folder to match
     * @return List of AbstractNode (FolderNode, StudyNode) POJOs or empty list
     * if none found
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Reference> getChildrenOfFolder(int folderId) throws MiddlewareQueryException;

    /**
     * Returns the list of DataSet references for a specific study. Retrieves
     * from central if the given ID is positive, otherwise retrieves from local.
     *
     * @param studyId The study id to match
     * @return List of DatasetReferences belonging to the study or empty list if
     * none found
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<DatasetReference> getDatasetReferences(int studyId) throws MiddlewareQueryException;

    /**
     * Returns the DataSet corresponding to the given dataset ID. Retrieves from
     * central if the given ID is positive, otherwise retrieves from local.
     *
     * @param dataSetId the data set id
     * @return The DataSet matching the given ID or null if none found
     * @throws MiddlewareQueryException the middleware query exception
     */
    DataSet getDataSet(int dataSetId) throws MiddlewareQueryException;

    /**
     * Gets the experiments given a dataset ID.
     *
     * @param dataSetId The dataset ID to match
     * @param start The start index of the rows to retrieve
     * @param numOfRows The number of items to retrieve
     * @return List of Experiments associated to the dataset ID or empty list if
     * none found
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Experiment> getExperiments(int dataSetId, int start, int numOfRows) throws MiddlewareQueryException;

    /**
     * Get the number of experiments in a dataset. Retrieves from central if the
     * given ID is positive, otherwise retrieves from local.
     *
     * @param dataSetId the data set id
     * @return the count
     * @throws MiddlewareQueryException the middleware query exception
     */
    long countExperiments(int dataSetId) throws MiddlewareQueryException;

    /**
     * Returns the list of study references for a particular search filter.
     *
     * @param filter The filter for the search - could be an instance of
     * BrowseStudyQueryFilter, GidStudyQueryFilter,
     * ParentFolderStudyQueryFilter.
     * @param numOfRows The number of rows to retrieve
     * @return The result set containing the matching studies
     * @throws MiddlewareQueryException the middleware query exception
     */
    StudyResultSet searchStudies(StudyQueryFilter filter, int numOfRows) throws MiddlewareQueryException;

    /**
     * Returns the list of factors for a specific study. Retrieves from central
     * if the given ID is positive, otherwise retrieves from local.
     *
     * @param studyId the study id
     * @return The factors of the study stored in a VariableTypeList
     * @throws MiddlewareQueryException the middleware query exception
     */
    VariableTypeList getAllStudyFactors(int studyId) throws MiddlewareQueryException;

    /**
     * Returns the list of variates for a specific study. Retrieves from central
     * if the given ID is positive, otherwise retrieves from local.
     *
     * @param studyId the study id
     * @return The variates of the study stored in a VariableTypeList
     * @throws MiddlewareQueryException the middleware query exception
     */
    VariableTypeList getAllStudyVariates(int studyId) throws MiddlewareQueryException;

    /**
     * Adds a study to the local database. Adds an entry into Project,
     * ProjectProperty, ProjectRelationships and Experiment. Inserts constants
     * and conditions listed in variableTypeList. Sets the parent to the given
     * parentFolderId input parameter.
     *
     * @param parentFolderId The ID of the parent folder
     * @param variableTypeList The conditions and constants of the Study
     * @param studyValues The values for the variables to insert
     * @return StudyReference corresponding to the newly-created Study
     * @throws MiddlewareQueryException the middleware query exception
     */
    StudyReference addStudy(int parentFolderId, VariableTypeList variableTypeList, StudyValues studyValues)
            throws MiddlewareQueryException;

    /**
     * Adds a dataset, dataset labels (factors and variate labels), and parent
     * study association in the local database.
     *
     * @param studyId the study id
     * @param variableTypeList the variable type list
     * @param datasetValues the dataset values
     * @return DatasetReference corresponding to the newly-created DataSet
     * @throws MiddlewareQueryException the middleware query exception
     */
    DatasetReference addDataSet(int studyId, VariableTypeList variableTypeList, DatasetValues datasetValues)
            throws MiddlewareQueryException;

    /**
     * Add a new variable/column to the dataset.
     *
     * @param datasetId the dataset id
     * @param variableType the variable type
     * @throws MiddlewareQueryException the middleware query exception
     */
    void addDataSetVariableType(int datasetId, VariableType variableType) throws MiddlewareQueryException;

    /**
     * Updates an Experiment to contain the given value.
     *
     * @param experimentId The ID of the experiment to update
     * @param variableId The standard variable ID
     * @param value The value to set
     * @throws MiddlewareQueryException the middleware query exception
     */
    void setExperimentValue(int experimentId, int variableId, String value) throws MiddlewareQueryException;

    /**
     * Adds an experiment row to the dataset.
     *
     * @param dataSetId The ID of the dataset to add the experiment into
     * @param experimentType The type of Experiment - could be ExperimentType.PLOT,
     * ExperimentType.SAMPLE, ExperimentType.AVERAGE,
     * ExperimentType.SUMMARY
     * @param experimentValues The values to set
     * @throws MiddlewareQueryException the middleware query exception
     */
    void addExperiment(int dataSetId, ExperimentType experimentType, ExperimentValues experimentValues)
            throws MiddlewareQueryException;
    
    /**
     * Adds or updates an experiment row to the dataset.
     *
     * @param dataSetId The ID of the dataset to add the experiment into
     * @param experimentType The type of Experiment - could be ExperimentType.PLOT,
     * ExperimentType.SAMPLE, ExperimentType.AVERAGE,
     * ExperimentType.SUMMARY
     * @param experimentValues The values to set
     * @throws MiddlewareQueryException the middleware query exception
     */
    void addOrUpdateExperiment(int dataSetId, ExperimentType experimentType, ExperimentValues experimentValues)
            throws MiddlewareQueryException;

    /**
     * Adds a Trial Environment. Accepts a variable list and sets up the trial
     * environment data in the local database. It will throw an exception if the
     * variable in the variable list passed is not recognized for trial
     * environment.
     *
     * @param variableList the variable list
     * @return ID of the trial environment data created.
     * @throws MiddlewareQueryException the middleware query exception
     */
    int addTrialEnvironment(VariableList variableList) throws MiddlewareQueryException;

    /**
     * Adds a Stock entry. Accepts a variable list and sets up the stock data in
     * the local database. It will throw an exception if the variable in the
     * variable list is not a stock variable.
     *
     * @param variableList the variable list
     * @return ID of the stock data created
     * @throws MiddlewareQueryException the middleware query exception
     */
    int addStock(VariableList variableList) throws MiddlewareQueryException;

    /**
     * Returns a list of datasets based on the given type. Retrieves from
     * central if the given ID is positive, otherwise retrieves from local.
     *
     * @param studyId the study id
     * @param dataSetType the data set type
     * @return The list of datasets matching the dataSetType or empty list if
     * non found.
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<DataSet> getDataSetsByType(int studyId, DataSetType dataSetType) throws MiddlewareQueryException;

    /**
     * Returns the number of experiments matching the given trial environment
     * and variate. Counts from central if the given ID is positive, otherwise
     * counts from local.
     *
     * @param trialEnvironmentId the trial environment id
     * @param variateVariableId the variate variable id
     * @return The count
     * @throws MiddlewareQueryException the middleware query exception
     */
    long countExperimentsByTrialEnvironmentAndVariate(int trialEnvironmentId, int variateVariableId)
            throws MiddlewareQueryException;

    /**
     * Retrieves the trial environments belonging to the given dataset.
     * Retrieves from central if the given ID is positive, otherwise retrieves
     * from local.
     *
     * @param datasetId the dataset id
     * @return The trial environments
     * @throws MiddlewareQueryException the middleware query exception
     */
    TrialEnvironments getTrialEnvironmentsInDataset(int datasetId) throws MiddlewareQueryException;

    /**
     * Retrieves the stocks belonging to the given dataset. Retrieves from
     * central if the given ID is positive, otherwise retrieves from local.
     *
     * @param datasetId the dataset id
     * @return The stocks
     * @throws MiddlewareQueryException the middleware query exception
     */
    Stocks getStocksInDataset(int datasetId) throws MiddlewareQueryException;

    /**
     * Returns the number of stocks matching the given dataset ID, trial
     * environment ID and variate ID. Counts from central if the given ID is
     * positive, otherwise counts from local.
     *
     * @param datasetId the dataset id
     * @param trialEnvironmentId the trial environment id
     * @param variateStdVarId the variate std var id
     * @return The count
     * @throws MiddlewareQueryException the middleware query exception
     */
    long countStocks(int datasetId, int trialEnvironmentId, int variateStdVarId) throws MiddlewareQueryException;

    /**
     * Returns the number of observations with value, matching the given dataset ID, trial
     * environment ID and variate ID. Counts from central if the given ID is
     * positive, otherwise counts from local.
     *
     * @param datasetId the dataset id
     * @param trialEnvironmentId the trial environment id
     * @param variateStdVarId the variate std var id
     * @return The count
     * @throws MiddlewareQueryException the middleware query exception
     */
    long countObservations(int datasetId, int trialEnvironmentId, int variateStdVarId) throws MiddlewareQueryException;
    
    /**
     * Returns a single dataset belonging to the study with the given type. If
     * there is more than one matching dataset, only one is returned. If there
     * are none, null is returned.
     *
     * @param studyId the study id
     * @param type the type
     * @return the data set
     * @throws MiddlewareQueryException the middleware query exception
     */
    
    DataSet findOneDataSetByType(int studyId, DataSetType type) throws MiddlewareQueryException;

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
     * Retrieves the local name associated to the given project ID and standard
     * variable ID.
     *
     * @param projectId the project id
     * @param standardVariableId the standard variable id
     * @return The local name
     * @throws MiddlewareQueryException the middleware query exception
     */
    String getLocalNameByStandardVariableId(Integer projectId, Integer standardVariableId)
            throws MiddlewareQueryException;

    /**
     * Retrieves the study details of the given study type from the specified
     * database.
     *
     * @param instance Can be CENTRAL or LOCAL
     * @param studyType Can be any of the types defined in {@link StudyType}
     * @return The list of study details having the given study type from the
     * given database
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<StudyDetails> getAllStudyDetails(Database instance, StudyType studyType) throws MiddlewareQueryException;

    /**
     * Retrieves the details of nursery and trial studies from the specified
     * database.
     *
     * @param instance Can be CENTRAL or LOCAL
     * @return The list of study details having the given study type from the
     * given database
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<StudyNode> getNurseryAndTrialStudyNodes(Database instance) throws MiddlewareQueryException;

    /**
     * Retrieves the details of nursery and trial studies from central and local.
     * Returns the id, name, description, start date, start year, season and study type of a Nursery or Trial Study.
     * Returns in sorted order of the following: Database(Local/Central), Year (Descending), Season (Dry/Wet/General),
     * Study Type(Nursery/Trial), Name(Ascending)
     *
     * @return The list of study details having the given study type from local and central
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<StudyNode> getAllNurseryAndTrialStudyNodes() throws MiddlewareQueryException;

    /**
     * Checks if the name specified is an already existing project name.
     *
     * @param name the name
     * @return true or false
     * @throws MiddlewareQueryException the middleware query exception
     */
    boolean checkIfProjectNameIsExisting(String name) throws MiddlewareQueryException;

    /**
     * Count the number of projects the variable was used in the project.
     *
     * @param variableId the variable id
     * @return the long
     * @throws MiddlewareQueryException the middleware query exception
     */
    long countProjectsByVariable(int variableId) throws MiddlewareQueryException;

    /**
     * Count the number of experiments the variable was used in the project.
     *
     * @param variableId the variable id
     * @param storedInId the stored in id
     * @return the long
     * @throws MiddlewareQueryException the middleware query exception
     */
    long countExperimentsByVariable(int variableId, int storedInId) throws MiddlewareQueryException;

    /**
     * Gets the field map information (entries, reps, plots and count) of the
     * given study id and study type.
     *
     * @param studyIdList the study id list
     * @param studyType Can be either StudyType.T (Trial) or StudyType.N (Nursery)
     * @return the FieldMapCount object containing the counts
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<FieldMapInfo> getFieldMapInfoOfStudy(List<Integer> studyIdList, StudyType studyType) throws MiddlewareQueryException;
    
    /**
     * Save or Update Field Map Properties like row, column, block, total rows, total columns, planting order.
     *
     * @param info the info
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
    void saveTrialDatasetSummary(DmsProject project, VariableTypeList variableTypeList, List<ExperimentValues> experimentValues, List<Integer> locationIds) throws MiddlewareQueryException;

    /**
     * Retrieve all field map labels in the block of the specified trial instance id.
     *
     * @param datasetId the dataset id
     * @param geolocationId the geolocation id
     * @return the all field maps in block by trial instance id
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<FieldMapInfo> getAllFieldMapsInBlockByTrialInstanceId(int datasetId, int geolocationId) throws MiddlewareQueryException;

    /**
     * Check if the given id is an existing study.
     *
     * @param id the id
     * @return true, if is study
     * @throws MiddlewareQueryException the middleware query exception
     */
	boolean isStudy(int id) throws MiddlewareQueryException;
	
	/**
	 * Adds a subFolder. Accepts a parentFolderId, the name and description of the folder.
	 * It will throw an exception if the parentFolderId is not existing in the local database
	 * and the name of the folder is not unique
	 *
	 * @param parentFolderId the parent folder id
	 * @param name the name
	 * @param description the description
	 * @return ID of the folder created
	 * @throws MiddlewareQueryException the middleware query exception
	 */
    int addSubFolder(int parentFolderId, String name, String description) throws MiddlewareQueryException;

    /**
     * Rename sub folder.
     *
     * @param newFolderName the new folder name
     * @param folderId the folder id
     * @return true, if successful
     * @throws MiddlewareQueryException the middleware query exception
     */
    boolean renameSubFolder(String newFolderName, int folderId) throws MiddlewareQueryException;
    
    
    /**
     * Logically delete a folder by updating the folder's name and deleting its project relationships.
     *
     * @param id the id
     * @return ID of the folder created
     * @throws MiddlewareQueryException the middleware query exception
     */
	void deleteEmptyFolder(int id) throws MiddlewareQueryException;
	
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
     * Retrieves the study details of the given study type from both central and local in batches
     * ordered by db instance then study name.
     *
     * @param studyType Can be any of the types defined in {@link StudyType}
     * @param start The start index of the rows to retrieve
     * @param numOfRows The number of items to retrieve
     * @return The list of study details having the given study type
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<StudyDetails> getStudyDetails(StudyType studyType, int start, int numOfRows) throws MiddlewareQueryException;
    
    /**
     * Retrieves the study details of the given study type from from both selected DB instance
     * ordered by db instance then study name.
     *
     * @param instance Database instance
     * @param studyType Can be any of the types defined in {@link StudyType}
     * @param start The start index of the rows to retrieve
     * @param numOfRows The number of items to retrieve
     * @return The list of study details having the given study type
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<StudyDetails> getStudyDetails(Database instance, StudyType studyType, int start, int numOfRows) throws MiddlewareQueryException;
    
    /**
     * Gets the study details.
     *
     * @param instance the instance
     * @param studyType the study type
     * @param id the id
     * @return the study details
     * @throws MiddlewareQueryException the middleware query exception
     */
    StudyDetails getStudyDetails(Database instance, StudyType studyType, int id) throws MiddlewareQueryException;
    
    /**
     * Retrieves the study details of the all nurseries and trials from both central and local in batches
     * ordered by db instance then study name.
     *
     * @param start The start index of the rows to retrieve
     * @param numOfRows The number of items to retrieve
     * @return The list of study details of Nurseries and Trials
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<StudyDetails> getNurseryAndTrialStudyDetails(int start, int numOfRows) throws MiddlewareQueryException;
    
    /**
     * Retrieves the study details of the all nurseries and trials from both selected DB instance
     * ordered by study name.
     *
     * @param instance DB instance
     * @param start The start index of the rows to retrieve
     * @param numOfRows The number of items to retrieve
     * @return The list of study details of Nurseries and Trials
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<StudyDetails> getNurseryAndTrialStudyDetails(Database instance, int start, int numOfRows) throws MiddlewareQueryException;
    
    /**
     * Retrieves all the study details of the given study type from both central and local ordered by db instance then study name.
     *
     * @param studyType Can be any of the types defined in {@link StudyType}
     * @return The list of study details having the given study type
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<StudyDetails> getAllStudyDetails(StudyType studyType) throws MiddlewareQueryException;
    
    /**
     * Count all studies of the given study type from selected DB instance.
     *
     * @param instance DB instance
     * @param studyType Can be any of the types defined in {@link StudyType}
     * @return The list of study details having the given study type
     * @throws MiddlewareQueryException the middleware query exception
     */
    long countStudyDetails(Database instance, StudyType studyType) throws MiddlewareQueryException;
    
    /**
     * Count all studies of the given study type from both central and local.
     *
     * @param studyType Can be any of the types defined in {@link StudyType}
     * @return The list of study details having the given study type
     * @throws MiddlewareQueryException the middleware query exception
     */
    long countAllStudyDetails(StudyType studyType) throws MiddlewareQueryException;
    
    /**
     * Retrieves the study details of the all nurseries and trials from both central and local
     * ordered by db instance then study name.
     *
     * @return The list of study details of Nurseries and Trials
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<StudyDetails> getAllNurseryAndTrialStudyDetails() throws MiddlewareQueryException;
    
    /**
     * Count all nurseries and trials from selected DB instance.
     *
     * @param instance DB instance
     * @return The list of study details having the given study type
     * @throws MiddlewareQueryException the middleware query exception
     */
    long countNurseryAndTrialStudyDetails(Database instance) throws MiddlewareQueryException;
    
    
    /**
     * Count all nurseries and trials from both central and local.
     *
     * @return The list of study details having the given study type
     * @throws MiddlewareQueryException the middleware query exception
     */
    long countAllNurseryAndTrialStudyDetails() throws MiddlewareQueryException;

    /**
     * Retrieves the folder tree.
     *
     * @return the folder tree
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<FolderReference> getFolderTree() throws MiddlewareQueryException;
    
    /**
     * Count plots with plants selectedof dataset.
     *
     * @param dataSetId the data set id
     * @return the int
     * @throws MiddlewareQueryException the middleware query exception
     */
    int countPlotsWithPlantsSelectedofDataset(int dataSetId) throws MiddlewareQueryException;
    
    /**
     * Gets the geolocation prop value.
     *
     * @param instance the instance
     * @param stdVarId the std var id
     * @param studyId the study id
     * @return the geolocation prop value
     * @throws MiddlewareQueryException the middleware query exception
     */
    String getGeolocationPropValue(Database instance, int stdVarId, int studyId) throws MiddlewareQueryException;
    
    /**
     * 
     * @param blockId
     * @return
     * @throws MiddlewareQueryException
     */
    List<FieldMapInfo> getAllFieldMapsInBlockByBlockId(int blockId)
            throws MiddlewareQueryException;
}
