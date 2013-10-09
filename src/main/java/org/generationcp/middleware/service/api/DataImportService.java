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

import java.io.File;

import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.exceptions.WorkbookParserException;

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
	int saveDataset(Workbook workbook) throws MiddlewareQueryException;
	
	/**
	 * Given a file, parse the file to create a workbook object
	 * 
	 * @param file
	 * @return workbook 
	 */
	Workbook parseWorkbook(File file) throws WorkbookParserException;

    Workbook strictParseWorkbook(File file) throws WorkbookParserException, MiddlewareQueryException;
}
