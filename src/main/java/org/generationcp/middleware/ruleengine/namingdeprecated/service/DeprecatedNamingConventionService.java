/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.ruleengine.namingdeprecated.service;

import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.ruleengine.RuleException;
import org.generationcp.middleware.ruleengine.pojo.DeprecatedAdvancingSource;
import org.generationcp.middleware.ruleengine.pojo.DeprecatedAdvancingSourceList;
import org.generationcp.middleware.ruleengine.pojo.ImportedCross;
import org.generationcp.middleware.ruleengine.pojo.ImportedGermplasm;

import java.util.List;

/**
 *
 * Service for Rules Based Naming.
 *
 */
@Deprecated
public interface DeprecatedNamingConventionService {

	void generateAdvanceListNames(List<DeprecatedAdvancingSource> advancingSourceItems, boolean checkForDuplicateName, List<ImportedGermplasm> germplasmList) throws
		MiddlewareQueryException, RuleException;

	/*
	* Generated the names for the list of crosses based on on rules setup for the breeding methods
	*/
	List<ImportedCross> generateCrossesList(List<ImportedCross> importedCrosses, DeprecatedAdvancingSourceList rows, boolean checkForDuplicateName,
		Workbook workbook, List<Integer> gids) throws RuleException;

}
