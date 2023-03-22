package org.generationcp.middleware.pojos.workbench;

import java.util.Arrays;
import java.util.List;

public enum PermissionsEnum {
	ADMIN,
	ADMINISTRATION,
	CROP_MANAGEMENT,
	GERMPLASM,
	MANAGE_GERMPLASM,
	IMPORT_GERMPLASM,
	EDIT_GERMPLASM,
	MG_MANAGE_FILES,
	STUDIES,
	LISTS,
	SAMPLES_LISTS,
	MANAGE_GERMPLASM_LISTS,
	SITE_ADMIN,
	MANAGE_ONTOLOGIES,
	LOW_DENSITY,
	MANAGE_PROGRAMS,
	ADD_PROGRAM,
	MANAGE_PROGRAM_SETTINGS,
	MANAGE_STUDIES,
	MS_MANAGE_OBSERVATION_UNITS,
	MS_EXPORT_STUDY_ENTRIES,
	MS_ENVIRONMENT,
	MS_MANAGE_FILES_ENVIRONMENT,
	MS_MANAGE_FILES_OBSERVATIONS,
	VIEW_STUDIES,
	HEAD_TO_HEAD_QUERY,
	MULTI_TRAIT_QUERY,
	IMPORT_DATASETS,
	SINGLE_SITE_ANALYSIS,
	MULTI_SITE_ANALYSIS,
	MANAGE_INVENTORY,
	MANAGE_LOTS,
	UPDATE_LOTS,
	MI_MANAGE_FILES,
	LOT_LABEL_PRINTING,
	QUERIES,
	GERMPLASM_LABEL_PRINTING,
	GERMPLASM_LIST_LABEL_PRINTING,
	DELETE_GERMPLASM_LIST,
	LOCK_UNLOCK_GERMPLASM_LIST,
	MS_SAMPLE_LISTS,
	MS_EXPORT_SAMPLE_LIST,
	MS_DELETE_SAMPLES,
	MS_STUDY_ACTIONS,
	MS_DESIGN_AND_PLANNING_OPTIONS,
	MS_CREATE_PLANTING_LABELS,
	MS_OBSERVATIONS,
	MS_MANAGE_PENDING_OBSERVATIONS,
	MS_MANAGE_CONFIRMED_OBSERVATIONS,
	MS_ACCEPT_PENDING_OBSERVATION;

	public static final String HAS_CREATE_LOTS_BATCH = " or hasAnyAuthority('ADMIN'"
		+ ", 'STUDIES'"
		+ ", 'MANAGE_STUDIES'"
		+ ", 'MS_CREATE_LOTS'"
		+ ", 'GERMPLASM'"
		+ ", 'MANAGE_GERMPLASM'"
		+ ", 'MG_MANAGE_INVENTORY'"
		+ ", 'MG_CREATE_LOTS'"
		+ ", 'CREATE_LOTS')";

	public static final String HAS_IMPORT_GERMPLASM = " or hasAnyAuthority('ADMIN'"
		+ ", 'GERMPLASM'"
		+ ", 'MANAGE_GERMPLASM'"
		+ ", 'IMPORT_GERMPLASM')";

	public static final List<String> SITE_ADMIN_PERMISSIONS = Arrays.asList(SITE_ADMIN.name(), ADMINISTRATION.name(), ADMIN.name());

	// Sample List Export List
	public static final List<String> EXPORT_FILE_SAMPLE_LIST_PERMISSIONS =
		Arrays.asList(ADMIN.name(), STUDIES.name(), MANAGE_STUDIES.name(), MS_SAMPLE_LISTS.name(), MS_EXPORT_SAMPLE_LIST.name());
	public static final List<String> DELETE_SAMPLES_PERMISSIONS =
		Arrays.asList(ADMIN.name(), STUDIES.name(), MANAGE_STUDIES.name(), MS_SAMPLE_LISTS.name(), MS_DELETE_SAMPLES.name());
}
