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

package org.generationcp.middleware.v2.manager.api;

import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.v2.domain.DataSet;
import org.generationcp.middleware.v2.domain.DataSetType;
import org.generationcp.middleware.v2.domain.DatasetReference;
import org.generationcp.middleware.v2.domain.DatasetValues;
import org.generationcp.middleware.v2.domain.Experiment;
import org.generationcp.middleware.v2.domain.ExperimentValues;
import org.generationcp.middleware.v2.domain.FolderReference;
import org.generationcp.middleware.v2.domain.Reference;
import org.generationcp.middleware.v2.domain.Study;
import org.generationcp.middleware.v2.domain.StudyReference;
import org.generationcp.middleware.v2.domain.StudyValues;
import org.generationcp.middleware.v2.domain.VariableList;
import org.generationcp.middleware.v2.domain.VariableType;
import org.generationcp.middleware.v2.domain.VariableTypeList;
import org.generationcp.middleware.v2.search.StudyResultSet;
import org.generationcp.middleware.v2.search.filter.StudyQueryFilter;

/**
 * This is the API for retrieving phenotypic data stored as Studies and
 * datasets from the CHADO schema.
 * 
 * 
 */
public interface StudyDataManager {

	/**
	 * Get the Study for a specific study id.
	 * 
	 * @param studyId the study's unique id
	 * @return the study or null if not found
	 * @throws MiddlewareQueryException 
	 */
	Study getStudy(int studyId) throws MiddlewareQueryException;

	/**
	 * Returns list of root or top-level folders from specified database
	 * 
	 * @param instance
	 *            - can be CENTRAL or LOCAL
	 * @return List of Folder POJOs or null if none found
	 * @throws MiddlewareQueryException 
	 */
	List<FolderReference> getRootFolders(Database instance) throws MiddlewareQueryException;
	
	
	/**
	 * Returns list of children of a folder given its id from the specified database
	 * 
	 * @param folderId
	 *            - the id of the folder to match
	 * @param instance
	 *            - can be CENTRAL or LOCAL
	 * @return List of AbstractNode (FolderNode, StudyNode) POJOs or null if none found
	 * @throws MiddlewareQueryException 
	 */
	List<Reference> getChildrenOfFolder(int folderId) throws MiddlewareQueryException;
	
	
	/**
	 * Returns the list of dataset nodes for a specific study.
	 * Retrieves from central if studyId is positive, otherwise retrieves from local.
	 * 
	 * @param studyId 
	 * 			- the study id to match
	 * @return List of DatasetNodes belonging to the study
	 * @throws MiddlewareQueryException
	 */
	List<DatasetReference> getDatasetReferences(int studyId) throws MiddlewareQueryException;
	
	/**
	 * @param dataSetId
	 * @return
	 * @throws MiddlewareQueryException
	 */
	DataSet getDataSet(int dataSetId) throws MiddlewareQueryException;
	
	/**
	 * Get experiments from a dataset.  Each experiment contains 
	 * @param datasetId
	 * @param startIndex
	 * @param numRows
	 * @return
	 */
	List<Experiment> getExperiments(int dataSetId, int start, int numOfRows) throws MiddlewareQueryException;
	
	/**
	 * Get the count of the number of experiments in a dataset.
	 * @param dataSetId
	 * @return
	 */
	long countExperiments(int dataSetId) throws MiddlewareQueryException;
	
	/**
	 * Returns the list of study references for a particular search filter.
	 *
	 * @param filter
	 * @param numOfRows
	 * @return
	 * @throws MiddlewareQueryException
	 */
	StudyResultSet searchStudies(StudyQueryFilter filter, int numOfRows) throws MiddlewareQueryException;
	
	/**
	 * Returns the list of factors for a specific study.
	 * 
	 * @param studyId
	 * @return
	 * @throws MiddlewareQueryException
	 */
	VariableTypeList getAllStudyFactors(int studyId) throws MiddlewareQueryException;
	
	/**
	 * Returns the list of variates for a specific study.
	 * 
	 * @param studyId
	 * @return
	 * @throws MiddlewareQueryException
	 */
	VariableTypeList getAllStudyVariates(int studyId) throws MiddlewareQueryException;
	
    /**
	 * Adds a study. Adds an entry into Project, ProjectProperty, ProjectRelationships and Experiment 
	 * Inserts constants and conditions listed in variableTypeList. 
	 * Sets the parent to the given parentFolderId input parameter. 
	 * 
	 * @param parentFolderId The ID of the parent folder
	 * @param variableTypeList The conditions and constants of the Study
	 * @param studyValues The values for the variables to insert
	 * @return StudyReference corresponding to the newly-created Study
	 * @throws MiddlewareQueryException
	 */
    StudyReference addStudy(int parentFolderId, VariableTypeList variableTypeList, StudyValues studyValues) throws MiddlewareQueryException;
    
    /**
     * Adds a dataset, dataset labels (factors and variate labels), and parent study association.
     * 
     * @param dataset
     * @return
     * @throws MiddlewareQueryException
     */
    DatasetReference addDataSet(int studyId, VariableTypeList variableTypeList, DatasetValues datasetValues) throws MiddlewareQueryException;
    
    /**
     * Add a new variable/column to the dataset.
     * @param datasetId
     * @param variableType
     * @throws MiddlewareQueryException 
     */
    void addDataSetVariableType(int datasetId, VariableType variableType) throws MiddlewareQueryException;
    
    /**
     * @param datasetId
     * @param experimentId
     * @param variableId
     * @param value
     */
    void setExperimentValue(int experimentId, int variableId, String value) throws MiddlewareQueryException;
    
    /**
     * Adds an experiment row to the dataset.
     * 
     * @param dataSetId
     * @param experimentValues
     * @throws MiddlewareQueryException
     */
    void addExperiment(int dataSetId, ExperimentValues experimentValues) throws MiddlewareQueryException;
    
    /**
     * Accepts a variable list and sets up the trial environment data in the local database.
     * It will throw an exception if the variable in the variable list passed is not recognized for trial environment.
     * It will return the ID of the trial environment data created.
     * 
     * @param variableList
     * @return
     * @throws MiddlewareQueryException
     */
    int addTrialEnvironment(VariableList variableList) throws MiddlewareQueryException;
    
    /**
     * Accepts a variable list and sets up the stock data in the local database.
     * It will throw an exception if the variable in the variable list is not a stock variable.
     * It will return the ID of the stock data created.
     * 
     * @param variableList
     * @return
     * @throws MiddlewareQueryException
     */
    int addStock(VariableList variableList) throws MiddlewareQueryException;
    
    /**
     * 
     * @param studyId
     * @param dataSetType
     * @return
     */
    List<DataSet> getDataSetsByType(int studyId, DataSetType dataSetType) throws MiddlewareQueryException;
    
    /**
     * @param trialEnvironmentId
     * @param variateVariableId
     * @return
     */
    long countExperimentsByTrialEnvironmentAndVariate(int trialEnvironmentId, int variateVariableId) throws MiddlewareQueryException;
}
