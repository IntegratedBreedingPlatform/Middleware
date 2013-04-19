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
import org.generationcp.middleware.pojos.Study;
import org.generationcp.middleware.v2.pojos.DatasetNode;
import org.generationcp.middleware.v2.pojos.DmsDataset;
import org.generationcp.middleware.v2.pojos.StudyDetails;
import org.generationcp.middleware.v2.pojos.AbstractNode;
import org.generationcp.middleware.v2.pojos.FolderNode;
import org.generationcp.middleware.v2.pojos.FactorDetails;
import org.generationcp.middleware.v2.pojos.ObservationDetails;
import org.generationcp.middleware.v2.pojos.StudyNode;
import org.generationcp.middleware.v2.pojos.StudyQueryFilter;

/**
 * This is the API for retrieving phenotypic data stored as Studies and
 * datasets from the CHADO schema.
 * 
 * 
 */
public interface StudyDataManager {

	/**
	 * Get the Study Details for a specific study.
	 * 
	 * @param studyId the study's unique id
	 * @return the study details or null if not found
	 * @throws MiddlewareQueryException 
	 */
	StudyDetails getStudyDetails(Integer studyId) throws MiddlewareQueryException;
	
	/**
	 * Returns list of root or top-level folders from specified database
	 * 
	 * @param instance
	 *            - can be CENTRAL or LOCAL
	 * @return List of Folder POJOs or null if none found
	 * @throws MiddlewareQueryException 
	 */
	public List<FolderNode> getRootFolders(Database instance) throws MiddlewareQueryException;
	
	
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
	public List<AbstractNode> getChildrenOfFolder(Integer folderId, Database instance) throws MiddlewareQueryException;
	
	
	/**
	 * Returns the list of dataset nodes for a specific study
	 * 
	 * @param studyId 
	 * 			- the study id to match
	 * @return List of DatasetNodes belonging to the study
	 * @throws MiddlewareQueryException
	 */
	public List<DatasetNode> getDatasetNodesByStudyId(Integer studyId, Database instance) throws MiddlewareQueryException;
	
	/**
	 * Returns the list of factor details for a specific study.
	 * 
	 * @param studyId
	 * @return
	 * @throws MiddlewareQueryException
	 */
	List<FactorDetails> getFactorDetails(Integer studyId) throws MiddlewareQueryException;

	/**
	 * Returns the list of observation details for a specific study.
	 * 
	 * @param studyId
	 * @return
	 * @throws MiddlewareQueryException
	 */
	List<ObservationDetails> getObservationDetails(Integer studyId) throws MiddlewareQueryException;
	
	/**
	 * Returns the list of study nodes for a particular search filter.
	 * 
	 * @param filter
	 * @return
	 * @throws MiddlewareQueryException
	 */
	List<StudyNode> searchStudies(StudyQueryFilter filter) throws MiddlewareQueryException;
	
    public Study addStudy(Study study) throws MiddlewareQueryException;
    
    
    public DmsDataset addDmsDataset(DmsDataset dataset) throws MiddlewareQueryException;

}
