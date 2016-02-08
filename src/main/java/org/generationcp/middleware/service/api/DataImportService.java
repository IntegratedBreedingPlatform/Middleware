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

package org.generationcp.middleware.service.api;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.exceptions.WorkbookParserException;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.operation.parser.WorkbookParser;
import org.generationcp.middleware.util.Message;

/**
 * This is the API for importing data to new schema. The methods here involve transformers (from old to new schema) and loaders (persisting
 * data).
 * 
 * 
 */
public interface DataImportService {

	/**
	 * Saves a workbook as a local trial or nursery on the new CHADO schema
	 * 
	 * @param workbook
	 * @param programUUID
	 * @return id of created trial or nursery
	 */
	int saveDataset(Workbook workbook, String programUUID) throws MiddlewareQueryException;

	/**
	 * Saves a workbook as a local trial or nursery on the new CHADO schema
	 * 
	 * @param workbook
	 * @param retainValues if true, values of the workbook items are retained, else they are cleared to conserve memory
	 * @param isDeleteObservations if true, values of the workbook observations will be removed
	 * @param programUUID the program UUID
	 * @return id of created trial or nursery
	 */
	int saveDataset(Workbook workbook, boolean retainValues, boolean isDeleteObservations, String programUUID)
			throws MiddlewareQueryException;

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
	 * @param programUUID
	 * @param discardOutOfBoundsData
	 * @param ontologyDataManger
	 * @return
	 * @throws WorkbookParserException
	 */
	Workbook parseWorkbook(File file, String programUUID, boolean discardInvalidValues, OntologyDataManager ontologyDataManger,
			WorkbookParser workbookParser) throws WorkbookParserException;

	/**
	 * 
	 * @param file
	 * @param programUUID
	 * @return the workbook
	 * @throws WorkbookParserException
	 * @throws MiddlewareQueryException
	 */
	Workbook strictParseWorkbook(File file, String programUUID) throws WorkbookParserException, MiddlewareException;

	/**
	 * Checks if the name specified is an already existing project name
	 * 
	 * @param name
	 * @param programUUID
	 * @return true or false
	 * @throws MiddlewareQueryException
	 */
	boolean checkIfProjectNameIsExistingInProgram(String name, String programUUID) throws MiddlewareQueryException;

	/**
	 * Checks if the experiment is already existing given the project name and location description
	 * 
	 * @param projectName
	 * @param locationDescription
	 * @param programUUID
	 * @return nd_geolocation_id
	 * @throws MiddlewareQueryException
	 */
	Integer getLocationIdByProjectNameAndDescriptionAndProgramUUID(String projectName, String locationDescription, String programUUID)
			throws MiddlewareQueryException;

	/**
	 * Validate the project ontology from the Workbook and return the list of errors
	 * 
	 * @param workbook
	 * @return Map<String,List<Message>> - map of errors for each header and global errors
	 * @throws MiddlewareQueryException
	 */
	Map<String, List<Message>> validateProjectOntology(Workbook workbook, String programUUID) throws MiddlewareException;

	/**
	 * Saves the project ontology from the Workbook Tables: project, project_relationship, project_properties
	 * 
	 * @param workbook
	 * @param programUUID
	 * @return id of created the study (Table.column = Project.project_id)
	 */
	int saveProjectOntology(Workbook workbook, String programUUID) throws MiddlewareQueryException;

	/**
	 * Saves the project ontology from the Workbook Tables: project, project_relationship, project_properties
	 * 
	 * @param workbook
	 * @param programUUID
	 * @return 1 = successful, 0 = failure
	 */
	int saveProjectData(Workbook workbook, String programUUID) throws MiddlewareQueryException;

	Map<String, List<Message>> validateProjectData(Workbook importData, String programUUID) throws MiddlewareException;

	/**
	 * Checks the Workbook's observation data for out of bounds values. Returns true if there are Out of Bounds data.
	 * 
	 * @param ontologyDataManager
	 * @param workbook
	 * @param programUUID
	 * @return
	 */
	boolean checkForOutOfBoundsData(OntologyDataManager ontologyDataManager, Workbook workbook, String programUUID);

	/**
	 * 
	 * @param workbook
	 * @param programUUID
	 * @param ontologyDataManager
	 */
	void populatePossibleValuesForCategoricalVariates(List<MeasurementVariable> variates, String programUUID,
			OntologyDataManager ontologyDataManager);

}
