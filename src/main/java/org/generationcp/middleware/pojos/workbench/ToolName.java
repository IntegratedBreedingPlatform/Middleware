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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The name of tools (e.g. germplasm_browser, study_browser) as stored in workbench_tool table in Workbench Database
 *
 */
public enum ToolName {
	LIST_MANAGER("list_manager"),
	BM_LIST_MANAGER_MAIN("bm_list_manager_main"),
	BREEDING_VIEW("breeding_view"),
	BV_SSA("breeding_view_wb"),
	BV_GXE("breeding_gxe"),
	BV_META_ANALYSIS("bv_meta_analysis"),
	DATASET_IMPORTER("dataset_importer"),
	FIELDBOOK_WEB("fieldbook_web"),
	GDMS("gdms"),
	GERMPLASM_BROWSER("germplasm_browser"),
	GERMPLASM_IMPORT("germplasm_import"),
	GERMPLASM_LIST_BROWSER("germplasm_list_browser"),
	MAIN_HEAD_TO_HEAD_BROWSER("germplasm_mainheadtohead"),
	MIGRATOR("migrator"),
	NURSERY_MANAGER_FIELDBOOK_WEB("nursery_manager_fieldbook_web"),
	ONTOLOGY_MANAGER("ontology_manager"),
	QUERY_FOR_ADAPTED_GERMPLASM("query_for_adapted_germplasm"),
	STUDY_BROWSER("study_browser"),
	STUDY_BROWSER_WITH_ID("study_browser_with_id"),
	TRAIT_DONOR_QUERY("trait_donor_query"),
	TRIAL_MANAGER_FIELDBOOK_WEB("trial_manager_fieldbook_web"),
	STUDY_MANAGER_FIELDBOOK_WEB("trial_manager_fieldbook_web");

	private final String name;

	ToolName(final String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public static boolean isCorrectTool(final String name) {
		for (ToolName tool : ToolName.values()) {
			if (tool.getName().equals(name)) {
				return true;
			}
		}

		return false;
	}

	public static boolean isURLAccessibleTool(final String name) {
		final List<ToolName> workbenchTools = Arrays.asList(BV_SSA, BV_META_ANALYSIS, BV_GXE);
		final List<ToolName> toolsWithURL = new ArrayList<>(Arrays.asList(ToolName.values()));
		toolsWithURL.removeAll(workbenchTools);
		for (ToolName tool : toolsWithURL) {
			if (tool.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	public static ToolName equivalentToolEnum(final String name) {
		for (ToolName tool : ToolName.values()) {
			if (tool.getName().equals(name)) {
				return tool;
			}
		}

		return null;
	}
}
