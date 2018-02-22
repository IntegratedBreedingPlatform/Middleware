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

package org.generationcp.middleware.pojos.workbench;

/**
 * The name of tools (e.g. germplasm_browser, study_browser).
 *
 */
public enum ToolName {
	// Execute:
	// SELECT name FROM workbench_tool ORDER BY path, name
	// in the database to get an organized list of names
	// some tools have more than one name and it will be
	// easier for us to group them here

	// Breeding Planner native app
	BREEDING_PLANNER

	// BreedingView native app
	, breeding_view

	// MBDT native app
	, mbdt

	// OptiMAS native app
	, optimas

	// BreedingManager webapp
	, BM_LIST_MANAGER, BM_LIST_MANAGER_MAIN, CROSSING_MANAGER, GERMPLASM_IMPORT, LIST_MANAGER, NURSERY_TEMPLATE_WIZARD

	// DatasetImporter webapp
	, DATASET_IMPORTER

	// fieldbook web apps
	, FIELDBOOK_WEB, NURSERY_MANAGER_FIELDBOOK_WEB, TRIAL_MANAGER_FIELDBOOK_WEB, ONTOLOGY_BROWSER_FIELDBOOK_WEB, STUDY_MANAGER_FIELDBOOK_WEB

	// GDMS webapp
	, GDMS

	// GermplasmStudyBrowser webapp
	, GERMPLASM_BROWSER, GERMPLASM_LIST_BROWSER, GERMPLASM_HEADTOHEAD, GERMPLASM_MAINHEADTOHEAD, QUERY_FOR_ADAPTED_GERMPLASM, STUDY_BROWSER, STUDY_BROWSER_WITH_ID

	// ibpwebservice webapp
	, IBPWEBSERVICE

	// not needed anymore?
	, GERMPLASM_PHENOTYPIC
}
