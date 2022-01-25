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
	MS_MANAGE_FILES,
	BROWSE_STUDIES,
	HEAD_TO_HEAD_QUERY,
	MULTI_TRAIT_QUERY,
	IMPORT_DATASETS,
	SINGLE_SITE_ANALYSIS,
	MULTI_SITE_ANALYSIS,
	MANAGE_INVENTORY,
	MANAGE_LOTS,
	LOT_LABEL_PRINTING,
	QUERIES,
	GERMPLASM_LABEL_PRINTING,
	GERMPLASM_LIST_LABEL_PRINTING,
	DELETE_GERMPLASM_LIST,
	LOCK_UNLOCK_GERMPLASM_LIST;

	/**
	 * Indicates a group of permissions that enables access to the manage studies module
	 * Enabling some services that are immediately called on load, in order to avoid
	 * ajax errors and allow navigation. In a future, a proper "manage studies view" permission
	 * might be combined with individual permission to make this constant unnecessary.
	 * <br/>
	 * E.g get datasets tabs
	 */
	public static final String HAS_MANAGE_STUDIES_VIEW = " or hasAnyAuthority('ADMIN'"
		+ ", 'STUDIES'"
		+ ", 'MANAGE_STUDIES'"
		+ ", 'MS_MANAGE_OBSERVATION_UNITS'"
		+ ", 'MS_WITHDRAW_INVENTORY'"
		+ ", 'MS_CREATE_PENDING_WITHDRAWALS'"
		+ ", 'MS_CREATE_CONFIRMED_WITHDRAWALS'"
		+ ", 'MS_MANAGE_FILES'"
		+ ", 'MS_CREATE_LOTS')";

	public static final String HAS_CREATE_LOTS_BATCH = " or hasAnyAuthority('ADMIN'"
		+ ", 'STUDIES'"
		+ ", 'MG_MANAGE_INVENTORY'"
		+ ", 'MG_CREATE_LOTS'"
		+ ", 'MANAGE_STUDIES'"
		+ ", 'MS_CREATE_LOTS'"
		+ ", 'GERMPLASM'"
		+ ", 'MANAGE_GERMPLASM'"
		+ ", 'CREATE_LOTS')";

	public static final String HAS_IMPORT_GERMPLASM = " or hasAnyAuthority('ADMIN'"
		+ ", 'GERMPLASM'"
		+ ", 'MANAGE_GERMPLASM'"
		+ ", 'IMPORT_GERMPLASM')";

	public static final List<String> SITE_ADMIN_PERMISSIONS = Arrays.asList(SITE_ADMIN.name(), ADMINISTRATION.name(), ADMIN.name());
}
