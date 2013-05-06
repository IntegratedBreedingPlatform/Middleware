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
import java.util.Set;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.v2.domain.DataSet;
import org.generationcp.middleware.v2.domain.DatasetReference;
import org.generationcp.middleware.v2.domain.DatasetValues;
import org.generationcp.middleware.v2.domain.Experiment;
import org.generationcp.middleware.v2.domain.ExperimentValues;
import org.generationcp.middleware.v2.domain.FactorDetails;
import org.generationcp.middleware.v2.domain.FolderReference;
import org.generationcp.middleware.v2.domain.Reference;
import org.generationcp.middleware.v2.domain.Study;
import org.generationcp.middleware.v2.domain.StudyQueryFilter;
import org.generationcp.middleware.v2.domain.StudyReference;
import org.generationcp.middleware.v2.domain.VariableList;
import org.generationcp.middleware.v2.domain.VariableTypeList;
import org.generationcp.middleware.v2.domain.VariateDetails;

/**
 * This is the API for retrieving phenotypic data stored as Studies and
 * datasets from the CHADO schema.
 * 
 * 
 */
public interface StudyDataManager {

	/**
	 * Get the Study for a specific study.
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
	public List<FolderReference> getRootFolders(Database instance) throws MiddlewareQueryException;
	
	
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
	public List<Reference> getChildrenOfFolder(int folderId) throws MiddlewareQueryException;
	
	
	/**
	 * Returns the list of dataset nodes for a specific study.
	 * Retrieves from central if studyId is positive, otherwise retrieves from local.
	 * 
	 * @param studyId 
	 * 			- the study id to match
	 * @return List of DatasetNodes belonging to the study
	 * @throws MiddlewareQueryException
	 */
	public List<DatasetReference> getDatasetReferences(int studyId) throws MiddlewareQueryException;
	
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
	List<Experiment> getExperiments(int dataSetId, int startIndex, int numRows) throws MiddlewareQueryException;
	
	/**
	 * Get the count of the number of experiments in a dataset.
	 * @param dataSetId
	 * @return
	 */
	int countExperiments(int dataSetId) throws MiddlewareQueryException;
	
	/**
	 * Returns the list of study nodes for a particular search filter.
	 * 
	 * @param filter
	 * @return
	 * @throws MiddlewareQueryException
	 */
	List<StudyReference> searchStudies(StudyQueryFilter filter) throws MiddlewareQueryException;
	
	/**
	 * Returns the list of factor details for a specific study.
	 * 
	 * @param studyId
	 * @return
	 * @throws MiddlewareQueryException
	 */
	List<FactorDetails> getFactors(Integer projectId) throws MiddlewareQueryException;
	
	/**
	 * Returns the list of observation details for a specific study.
	 * 
	 * @param studyId
	 * @return
	 * @throws MiddlewareQueryException
	 */
	List<VariateDetails> getVariates(Integer projectId) throws MiddlewareQueryException;
	

	/**
	 * Retrieves the studies belonging to a given folder id. 
	 * Assumption: that every study has a parent folder.
	 * 
	 * @param folderId
	 * @param start - the first Study index 
	 * @param numOfRows - maximum number of Studies to retrieve
	 * @return the list of studies
	 * @throws MiddlewareQueryException
	 */
	List<Study> getStudiesByFolder(Integer folderId, int start, int numOfRows) throws MiddlewareQueryException;
	
	/**
	 * Returns the number of studies in the given folder id.
	 * 
	 * @param folderId
	 * @return
	 * @throws MiddlewareQueryException
	 */
	long countStudiesByFolder(Integer folderId) throws MiddlewareQueryException;


    /**
     * Returns the list of study details by its GID value.
     * 
     * @param gid
     * @return
     * @throws MiddlewareQueryException
     */
    Set<Study> searchStudiesByGid(Integer gid) throws MiddlewareQueryException;

    /**
	 * Adds a study. Inserts into the tables project, projectprop and project_relationships. 
	 * Sets the parent to the given id in the hierarchy field of the study. 
	 * If no value is supplied, the new study is stored as a top-level study.
	 * 
	 * @param study The Study object to insert
	 * @return the added object with the generated id
	 * @throws MiddlewareQueryException
	 */
    StudyReference addStudy(Study study) throws MiddlewareQueryException;

    /**
     * Adds a dataset, dataset labels (factors and variate labels), and parent study association.
     * 
     * @param dataset
     * @return
     * @throws MiddlewareQueryException
     */
    //DatasetReference addDataSet(DataSet dataset, List<Experiment> experiments) throws MiddlewareQueryException;
    DatasetReference addDataSet(int studyId, VariableTypeList variableTypeList, DatasetValues datasetValues) throws MiddlewareQueryException;
    
    /**
     * Adds an experiment row to the dataset.
     * 
     * @param dataSetId
     * @param experimentValues
     * @throws MiddlewareQueryException
     */
    void addExperiment(int dataSetId, ExperimentValues experimentValues) throws MiddlewareQueryException;
    
    /**
     * Adds Geolocation and GeolocationProp records.
     * 
     * @param variableList
     * @return
     * @throws MiddlewareQueryException
     */
    int addLocation(VariableList variableList) throws MiddlewareQueryException;
    
    /**
     * Adds Stock and StockProp records.
     * @param variableList
     * @return
     * @throws MiddlewareQueryException
     */
    int addGermplasm(VariableList variableList) throws MiddlewareQueryException;
    
}
