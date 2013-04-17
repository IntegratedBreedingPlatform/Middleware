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
import org.generationcp.middleware.v2.pojos.FactorDetails;
import org.generationcp.middleware.v2.pojos.Folder;
import org.generationcp.middleware.v2.pojos.ObservationDetails;
import org.generationcp.middleware.v2.pojos.StudyDetails;

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
	StudyDetails getStudyDetails(int studyId) throws MiddlewareQueryException;
	
	/**
	 * Returns list of root or top-level folders from specified database
	 * 
	 * @param instance
	 *            - can be CENTRAL or LOCAL
	 * @return List of Folder POJOs or null if none found
	 * @throws MiddlewareQueryException 
	 */
	public List<Folder> getRootFolders(Database instance) throws MiddlewareQueryException;
	
	/**
	 * Returns the list of factor details for a specific study.
	 * 
	 * @param studyId
	 * @return
	 * @throws MiddlewareQueryException
	 */
	List<FactorDetails> getFactorDetails(int studyId) throws MiddlewareQueryException;

	/**
	 * Returns the list of observation details for a specific study.
	 * 
	 * @param studyId
	 * @return
	 * @throws MiddlewareQueryException
	 */
	List<ObservationDetails> getObservationDetails(int studyId) throws MiddlewareQueryException;
}
