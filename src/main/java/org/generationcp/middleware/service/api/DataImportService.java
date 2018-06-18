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

import com.google.common.base.Optional;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.exceptions.WorkbookParserException;
import org.generationcp.middleware.operation.parser.WorkbookParser;
import org.generationcp.middleware.util.Message;

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
	 * @param programUUID
	 * @param cropPrefix
	 * @return id of created trial or nursery
	 */
	int saveDataset(final Workbook workbook, final String programUUID, final String cropPrefix)
			throws MiddlewareQueryException;

	/**
	 * Saves a workbook as a local trial or nursery on the new CHADO schema
	 *
	 * @param workbook
	 * @param retainValues
	 *            if true, values of the workbook items are retained, else they
	 *            are cleared to conserve memory
	 * @param isDeleteObservations
	 *            if true, values of the workbook observations will be removed
	 * @param programUUID
	 *            the program UUID
	 * @param cropPrefix
	 * @return id of created trial or nursery
	 */
	int saveDataset(final Workbook workbook, final boolean retainValues, final boolean isDeleteObservations,
			final String programUUID, final String cropPrefix) throws MiddlewareQueryException;

	/**
	 * Given a file, parse the file to create a workbook object
	 *
	 * @param file
	 * @return workbook
	 */
	Workbook parseWorkbook(File file, final Integer currentIbdbUserId) throws WorkbookParserException;

	/**
	 * Parses the file to create a workbook object with options to discard the
	 * invalid values. If discardOutOfBoundsData is true, all invalid values of
	 * categorical traits in the file will be removed/discarded and set to
	 * empty.
	 *
	 * @param file
	 * @param programUUID
	 * @param discardOutOfBoundsData
	 * @param ontologyDataManger
	 * @return
	 * @throws WorkbookParserException
	 */
	Workbook parseWorkbook(File file, String programUUID, boolean discardInvalidValues, WorkbookParser workbookParser, final Integer currentIbdbUserId)
			throws WorkbookParserException;

	/**
	 *
	 * @param file
	 * @param programUUID
	 * @return the workbook
	 * @throws WorkbookParserException
	 * @throws MiddlewareQueryException
	 */
	Workbook strictParseWorkbook(File file, String programUUID, final Integer currentIbdbUserId) throws WorkbookParserException,
		MiddlewareException;

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
	 * Checks if the experiment is already existing given the project name and
	 * location description
	 *
	 * @param projectName
	 * @param locationDescription
	 * @param programUUID
	 * @return nd_geolocation_id
	 * @throws MiddlewareQueryException
	 */
	Integer getLocationIdByProjectNameAndDescriptionAndProgramUUID(String projectName, String locationDescription,
			String programUUID) throws MiddlewareQueryException;

	/**
	 * Validate the project ontology from the Workbook and return the list of
	 * errors
	 *
	 * @param workbook
	 * @return Map<String,List<Message>> - map of errors for each header and
	 *         global errors
	 * @throws MiddlewareQueryException
	 */
	Map<String, List<Message>> validateProjectOntology(Workbook workbook, String programUUID)
			throws MiddlewareException;

	/**
	 * Saves the project ontology from the Workbook Tables: project,
	 * project_relationship, project_properties
	 *
	 * @param workbook
	 * @param programUUID
	 * @param cropPrefix
	 * @return id of created the study (Table.column = Project.project_id)
	 */
	int saveProjectOntology(final Workbook workbook, final String programUUID, final String cropPrefix)
			throws MiddlewareQueryException;

	/**
	 * Saves the project ontology from the Workbook Tables: project,
	 * project_relationship, project_properties
	 *
	 * @param workbook
	 * @param programUUID
	 * @param cropPrefix
	 * @return 1 = successful, 0 = failure
	 */
	int saveProjectData(final Workbook workbook, final String programUUID, final String cropPrefix)
			throws MiddlewareQueryException;

	Map<String, List<Message>> validateProjectData(Workbook importData, String programUUID) throws MiddlewareException;

	Optional<MeasurementVariable> findMeasurementVariableByTermId(int termId, List<MeasurementVariable> list);

	void checkForInvalidGids(final Workbook workbook, final List<Message> messages);

	/**
	 * Checks the Workbook's observation data for out-of-bounds values. Returns
	 * true if there are out-of-bounds data.
	 *
	 * @param ontologyDataManager
	 * @param workbook
	 * @param programUUID
	 * @return
	 */
	boolean checkForOutOfBoundsData(Workbook workbook, String programUUID);

	void addLocationIDVariableInFactorsIfNotExists(Workbook workbook, String programUUID);

	void addLocationIDVariableInConditionsIfNotExists(Workbook workbook, String programUUID);

	void removeLocationNameVariableIfExists(Workbook workbook);

	void assignLocationVariableWithUnspecifiedLocationIfEmpty(List<MeasurementVariable> measurementVariables);

	void assignLocationIdVariableToEnvironmentDetailSection(Workbook workbook);

	/**
	 * Populates the measurement variables with their possible values. Only the
	 * categorical type variable will be populated.
	 *
	 * @param workbook
	 * @param programUUID
	 * @param ontologyDataManager
	 */
	void populatePossibleValuesForCategoricalVariates(List<MeasurementVariable> variates, String programUUID);

	Workbook parseWorkbookDescriptionSheet(org.apache.poi.ss.usermodel.Workbook excelWorkbook, final Integer currentIbdbUserId)
			throws WorkbookParserException;

}
