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

package org.generationcp.middleware.service.api;

import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.exceptions.WorkbookParserException;
import org.generationcp.middleware.util.Message;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * This is the API for importing data to new schema. The methods here involve
 * transformers (from old to new schema) and loaders (persisting data).
 * 
 * 
 */
public interface DataImportService {

	/**
	 * Saves a workbook as a local trial or nursery on the new CHADO schema
	 * 
	 * @param workbook
	 * @return id of created trial or nursery
	 */
	int saveDataset(Workbook workbook, String programUUID) throws MiddlewareQueryException;
	
    /**
     * Saves a workbook as a local trial or nursery on the new CHADO schema
     * 
     * @param workbook
     * @param retainValues if true, values of the workbook items are retained, else they are cleared to conserve memory
     * @return id of created trial or nursery
     */
    int saveDataset(Workbook workbook, boolean retainValues, boolean isDeleteObservations, String programUUID) throws MiddlewareQueryException;

	/**
	 * Given a file, parse the file to create a workbook object
	 * 
	 * @param file
	 * @return workbook 
	 */
	Workbook parseWorkbook(File file) throws WorkbookParserException;

	/**
	 * 
	 * @param file
	 * @return the workbook
	 * @throws WorkbookParserException
	 * @throws MiddlewareQueryException
	 */
    Workbook strictParseWorkbook(File file) throws WorkbookParserException, MiddlewareQueryException;

    /**
     * 
     * @param workbook
     * @return the workbook
     * @throws WorkbookParserException
     * @throws MiddlewareQueryException
     */
    @Deprecated
	Workbook validateWorkbook(Workbook workbook)
			throws WorkbookParserException, MiddlewareQueryException;
	
	/**
     * Checks if the name specified is an already existing project name
     * 
     * @param name
     * @return true or false
     * @throws MiddlewareQueryException
     */
    boolean checkIfProjectNameIsExisting(String name) throws MiddlewareQueryException;
    
    /**
     * Checks if the experiment is already existing given the project name and location description
     * 
     * @param projectName
     * @param locationDescription
     * @return nd_geolocation_id
     * @throws MiddlewareQueryException
     */
    Integer getLocationIdByProjectNameAndDescription(String projectName, String locationDescription) throws MiddlewareQueryException;
    
    /**
	 * Validate the project ontology from the Workbook and return the list of errors
	 * @param workbook
	 * @return Map<String,List<Message>> - map of errors for each header and global errors
	 * @throws MiddlewareQueryException
	 */
    Map<String,List<Message>> validateProjectOntology(Workbook workbook) throws MiddlewareQueryException;
    
    /**
	 * Saves the project ontology from the Workbook
	 * Tables: project, project_relationship, project_properties
	 * @param workbook
	 * @return id of created the study (Table.column = Project.project_id)
	 */
    int saveProjectOntology(Workbook workbook, String programUUID) throws MiddlewareQueryException;
    
    /**
	 * Saves the project ontology from the Workbook
	 * Tables: project, project_relationship, project_properties
	 * @param workbook
	 * @return 1 = successful, 0 = failure
	 */
    int saveProjectData(Workbook workbook) throws MiddlewareQueryException;

	Map<String, List<Message>> validateProjectData(Workbook importData) throws MiddlewareQueryException;
	
	
}
