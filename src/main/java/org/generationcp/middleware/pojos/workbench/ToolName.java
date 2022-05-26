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
 * The name of tools (e.g. germplasm_browser, study_browser) as stored in workbench_tool table in Workbench Database
 *
 */
public enum ToolName {
	BREEDING_VIEW("breeding_view"),
	BV_SSA("breeding_view_wb"),
	BV_GXE("breeding_gxe"),
	BV_META_ANALYSIS("bv_meta_analysis"),
	DATASET_IMPORTER("dataset_importer"),
	FIELDBOOK_WEB("fieldbook_web"),
	GDMS("gdms"),
	GERMPLASM_BROWSER("germplasm_browser"),
	GERMPLASM_LIST_BROWSER("germplasm_list_browser"),
	MAIN_HEAD_TO_HEAD_BROWSER("germplasm_mainheadtohead"),
	MIGRATOR("migrator"),
	NURSERY_MANAGER_FIELDBOOK_WEB("nursery_manager_fieldbook_web"),
	ONTOLOGY_MANAGER("ontology_manager"),
	QUERY_FOR_ADAPTED_GERMPLASM("query_for_adapted_germplasm"),
	SAMPLE_MANAGER("sample_manager"),
	STUDY_BROWSER("study_browser"),
	STUDY_BROWSER_WITH_ID("study_browser_with_id"),
	GRAPHICAL_QUERIES("graphical_queries"),
	TRAIT_DONOR_QUERY("trait_donor_query"),
	TRIAL_MANAGER_FIELDBOOK_WEB("trial_manager_fieldbook_web"),
	STUDY_MANAGER_FIELDBOOK_WEB("study_manager_fieldbook_web"),
	CREATE_PROGRAMS("create_program"),
	GERMPLASM_LISTS("germplasm_lists"),
	INVENTORY_MANAGEMENT("inventory_manager"),
	HIGH_DENSITY("high_density"),
	GERMPLASM_SEARCH("search_germplasm"),
	METHOD_MANAGER("ProgramMethods"),
	LOCATION_MANAGER("ProgramLocations");

	private final String name;

	ToolName(final String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
}
