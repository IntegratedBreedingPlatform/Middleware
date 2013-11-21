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

/**
 * This is the API for retrieving phenotypic data stored as Studies and datasets
 * from the CHADO schema.
 * 
 * 
 */
public interface StudyDataManager{

    /**
     * Get the Study for a specific study id. Retrieves from central if the
     * given ID is positive, otherwise retrieves from local.
     * 
     * @param studyId
     *            the study's unique id
     * @return the study or null if not found
     * @throws MiddlewareQueryException
     */
    Study getStudy(int studyId) throws MiddlewareQueryException;

    int getStudyIdByName(String studyName) throws MiddlewareQueryException;

    /**
     * Returns list of root or top-level folders from specified database.
     * 
     * @param instance
     *            Can be CENTRAL or LOCAL
     * @return List of Folder POJOs or empty list if none found
     * @throws MiddlewareQueryException
     */
    List<FolderReference> getRootFolders(Database instance) throws MiddlewareQueryException;

    /**
     * Returns list of children of a folder given its ID. Retrieves from central
     * if the given ID is positive, otherwise retrieves from local.
     * 
     * @param folderId
     *            The id of the folder to match
     * @param instance
     *            Can be CENTRAL or LOCAL
     * @return List of AbstractNode (FolderNode, StudyNode) POJOs or empty list
     *         if none found
     * @throws MiddlewareQueryException
     */
    List<Reference> getChildrenOfFolder(int folderId) throws MiddlewareQueryException;

    /**
     * Returns the list of DataSet references for a specific study. Retrieves
     * from central if the given ID is positive, otherwise retrieves from local.
     * 
     * @param studyId
     *            The study id to match
     * @return List of DatasetReferences belonging to the study or empty list if
     *         none found
     * @throws MiddlewareQueryException
     */
    List<DatasetReference> getDatasetReferences(int studyId) throws MiddlewareQueryException;

    /**
     * Returns the DataSet corresponding to the given dataset ID. Retrieves from
     * central if the given ID is positive, otherwise retrieves from local.
     * 
     * @param dataSetId
     * @return The DataSet matching the given ID or null if none found
     * @throws MiddlewareQueryException
     */
    DataSet getDataSet(int dataSetId) throws MiddlewareQueryException;

    /**
     * Gets the experiments given a dataset ID.
     * 
     * @param dataSetId
     *            The dataset ID to match
     * @param start
     *            The start index of the rows to retrieve
     * @param numOfRows
     *            The number of items to retrieve
     * @return List of Experiments associated to the dataset ID or empty list if
     *         none found
     * @throws MiddlewareQueryException
     */
    List<Experiment> getExperiments(int dataSetId, int start, int numOfRows) throws MiddlewareQueryException;

    /**
     * Get the number of experiments in a dataset. Retrieves from central if the
     * given ID is positive, otherwise retrieves from local.
     * 
     * @param dataSetId
     * @return the count
     */
    long countExperiments(int dataSetId) throws MiddlewareQueryException;

    /**
     * Returns the list of study references for a particular search filter.
     * 
     * @param filter
     *            The filter for the search - could be an instance of
     *            BrowseStudyQueryFilter, GidStudyQueryFilter,
     *            ParentFolderStudyQueryFilter.
     * @param numOfRows
     *            The number of rows to retrieve
     * @return The result set containing the matching studies
     * @throws MiddlewareQueryException
     */
    StudyResultSet searchStudies(StudyQueryFilter filter, int numOfRows) throws MiddlewareQueryException;

    /**
     * Returns the list of factors for a specific study. Retrieves from central
     * if the given ID is positive, otherwise retrieves from local.
     * 
     * @param studyId
     * @return The factors of the study stored in a VariableTypeList
     * @throws MiddlewareQueryException
     */
    VariableTypeList getAllStudyFactors(int studyId) throws MiddlewareQueryException;

    /**
     * Returns the list of variates for a specific study. Retrieves from central
     * if the given ID is positive, otherwise retrieves from local.
     * 
     * @param studyId
     * @return The variates of the study stored in a VariableTypeList
     * @throws MiddlewareQueryException
     */
    VariableTypeList getAllStudyVariates(int studyId) throws MiddlewareQueryException;

    /**
     * Adds a study to the local database. Adds an entry into Project,
     * ProjectProperty, ProjectRelationships and Experiment. Inserts constants
     * and conditions listed in variableTypeList. Sets the parent to the given
     * parentFolderId input parameter.
     * 
     * @param parentFolderId
     *            The ID of the parent folder
     * @param variableTypeList
     *            The conditions and constants of the Study
     * @param studyValues
     *            The values for the variables to insert
     * @return StudyReference corresponding to the newly-created Study
     * @throws MiddlewareQueryException
     */
    StudyReference addStudy(int parentFolderId, VariableTypeList variableTypeList, StudyValues studyValues)
            throws MiddlewareQueryException;

    /**
     * Adds a dataset, dataset labels (factors and variate labels), and parent
     * study association in the local database.
     * 
     * @param studyId
     * @param variableTypeList
     * @param datasetValues
     * @return DatasetReference corresponding to the newly-created DataSet
     * @throws MiddlewareQueryException
     */
    DatasetReference addDataSet(int studyId, VariableTypeList variableTypeList, DatasetValues datasetValues)
            throws MiddlewareQueryException;

    /**
     * Add a new variable/column to the dataset.
     * 
     * @param datasetId
     * @param variableType
     * @throws MiddlewareQueryException
     */
    void addDataSetVariableType(int datasetId, VariableType variableType) throws MiddlewareQueryException;

    /**
     * Updates an Experiment to contain the given value.
     * 
     * @param experimentId
     *            The ID of the experiment to update
     * @param variableId
     *            The standard variable ID
     * @param value
     *            The value to set
     */
    void setExperimentValue(int experimentId, int variableId, String value) throws MiddlewareQueryException;

    /**
     * Adds an experiment row to the dataset.
     * 
     * @param dataSetId
     *            The ID of the dataset to add the experiment into
     * @param experimentType
     *            The type of Experiment - could be ExperimentType.PLOT,
     *            ExperimentType.SAMPLE, ExperimentType.AVERAGE,
     *            ExperimentType.SUMMARY
     * @param experimentValues
     *            The values to set
     * @throws MiddlewareQueryException
     */
    void addExperiment(int dataSetId, ExperimentType experimentType, ExperimentValues experimentValues)
            throws MiddlewareQueryException;

    /**
     * Adds a Trial Environment. Accepts a variable list and sets up the trial
     * environment data in the local database. It will throw an exception if the
     * variable in the variable list passed is not recognized for trial
     * environment.
     * 
     * @param variableList
     * @return ID of the trial environment data created.
     * @throws MiddlewareQueryException
     */
    int addTrialEnvironment(VariableList variableList) throws MiddlewareQueryException;

    /**
     * Adds a Stock entry. Accepts a variable list and sets up the stock data in
     * the local database. It will throw an exception if the variable in the
     * variable list is not a stock variable.
     * 
     * @param variableList
     * @return ID of the stock data created
     * @throws MiddlewareQueryException
     */
    int addStock(VariableList variableList) throws MiddlewareQueryException;

    /**
     * Returns a list of datasets based on the given type. Retrieves from
     * central if the given ID is positive, otherwise retrieves from local.
     * 
     * @param studyId
     * @param dataSetType
     * @return The list of datasets matching the dataSetType or empty list if
     *         non found.
     */
    List<DataSet> getDataSetsByType(int studyId, DataSetType dataSetType) throws MiddlewareQueryException;

    /**
     * Returns the number of experiments matching the given trial environment
     * and variate. Counts from central if the given ID is positive, otherwise
     * counts from local.
     * 
     * @param trialEnvironmentId
     * @param variateVariableId
     * @return The count
     */
    long countExperimentsByTrialEnvironmentAndVariate(int trialEnvironmentId, int variateVariableId)
            throws MiddlewareQueryException;

    /**
     * Retrieves the trial environments belonging to the given dataset.
     * Retrieves from central if the given ID is positive, otherwise retrieves
     * from local.
     * 
     * @param datasetId
     * @return The trial environments
     * @throws MiddlewareQueryException
     */
    TrialEnvironments getTrialEnvironmentsInDataset(int datasetId) throws MiddlewareQueryException;

    /**
     * Retrieves the stocks belonging to the given dataset. Retrieves from
     * central if the given ID is positive, otherwise retrieves from local.
     * 
     * @param datasetId
     * @return The stocks
     * @throws MiddlewareQueryException
     */
    Stocks getStocksInDataset(int datasetId) throws MiddlewareQueryException;

    /**
     * Returns the number of stocks matching the given dataset ID, trial
     * environment ID and variate ID. Counts from central if the given ID is
     * positive, otherwise counts from local.
     * 
     * @param datasetId
     * @param trialEnvironmentId
     * @param variateStdVarId
     * @return The count
     * @throws MiddlewareQueryException
     */
    long countStocks(int datasetId, int trialEnvironmentId, int variateStdVarId) throws MiddlewareQueryException;

    /**
     * Returns a single dataset belonging to the study with the given type. If
     * there is more than one matching dataset, only one is returned. If there
     * are none, null is returned.
     * 
     * @param studyId
     * @param type
     * @return
     * @throws MiddlewareQueryException
     */
    DataSet findOneDataSetByType(int studyId, DataSetType type) throws MiddlewareQueryException;

    /**
     * Deletes the dataset matching the given ID.
     * 
     * @param datasetId
     * @throws MiddlewareQueryException
     */
    void deleteDataSet(int datasetId) throws MiddlewareQueryException;

    /**
     * Deletes location matching the given dataset ID and location ID.
     * 
     * @param datasetId
     * @param locationId
     * @throws MiddlewareQueryException
     */
    void deleteExperimentsByLocation(int datasetId, int locationId) throws MiddlewareQueryException;

    /**
     * Retrieves the local name associated to the given project ID and standard
     * variable ID.
     * 
     * @param projectId
     * @param standardVariableId
     * @return The local name
     * @throws MiddlewareQueryException
     */
    String getLocalNameByStandardVariableId(Integer projectId, Integer standardVariableId)
            throws MiddlewareQueryException;

    /**
     * 
     * Retrieves the study details of the given study type from the specified
     * database.
     * 
     * @param instance
     *            Can be CENTRAL or LOCAL
     * @param studyType
     *            Can be any of the types defined in {@link StudyType}
     * @return The list of study details having the given study type from the
     *         given database
     * @throws MiddlewareQueryException
     */
    List<StudyDetails> getAllStudyDetails(Database instance, StudyType studyType) throws MiddlewareQueryException;

    /**
     * 
     * Retrieves the details of nursery and trial studies from the specified
     * database.
     * 
     * @param instance
     *            Can be CENTRAL or LOCAL
     * @return The list of study details having the given study type from the
     *         given database
     * @throws MiddlewareQueryException
     */
    List<StudyNode> getNurseryAndTrialStudyNodes(Database instance) throws MiddlewareQueryException;

    /**
     * 
     * Retrieves the details of nursery and trial studies from central and local. 
     * Returns the id, name, description, start date, start year, season and study type of a Nursery or Trial Study.
     * Returns in sorted order of the following: Database(Local/Central), Year (Descending), Season (Dry/Wet/General), 
     * Study Type(Nursery/Trial), Name(Ascending)
     * 
     * @return The list of study details having the given study type from local and central
     * @throws MiddlewareQueryException
     */
    List<StudyNode> getAllNurseryAndTrialStudyNodes() throws MiddlewareQueryException;

    /**
     * Checks if the name specified is an already existing project name
     * 
     * @param name
     * @return true or false
     * @throws MiddlewareQueryException
     */
    boolean checkIfProjectNameIsExisting(String name) throws MiddlewareQueryException;

    /**
     * Count the number of projects the variable was used in the project.
     * 
     * @param variableId
     * @return
     * @throws MiddlewareQueryException
     */
    long countProjectsByVariable(int variableId) throws MiddlewareQueryException;

    /**
     * Count the number of experiments the variable was used in the project.
     * 
     * @param variableId
     * @return
     * @throws MiddlewareQueryException
     */
    long countExperimentsByVariable(int variableId, int storedInId) throws MiddlewareQueryException;

    /**
     * Gets the field map information (entries, reps, plots and count) of the
     * given study id and study type.
     * 
     * @param studyId
     *            the id of the study to retrieve the count from
     * @param studyType
     *            Can be either StudyType.T (Trial) or StudyType.N (Nursery)
     * @return the FieldMapCount object containing the counts
     */
    FieldMapInfo getFieldMapInfoOfStudy(int studyId, StudyType studyType) throws MiddlewareQueryException;

}
