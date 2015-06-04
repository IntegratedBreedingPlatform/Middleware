/*******************************************************************************
 * Copyright (c) 2014, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.manager;

public enum GdmsTable {

	GDMS_ACC_METADATASET("gdms_acc_metadataset", "acc_metadataset_id"), GDMS_ALLELE_VALUES("gdms_allele_values", "an_id"), GDMS_CHAR_VALUES(
			"gdms_char_values", "ac_id"), GDMS_DART_VALUES("gdms_dart_values", "ad_id"), GDMS_DATASET("gdms_dataset", "dataset_id"), GDMS_DATASET_DETAILS(
			"gdms_dataset_details", ""), GDMS_DATASET_USERS("gdms_dataset_users", "dataset_id"), GDMS_MAP("gdms_map", "map_id"), GDMS_MAPPING_POP_VAUES(
			"gdms_mapping_pop_values", "mp_id"), GDMS_MARKER("gdms_marker", "marker_id"), GDMS_MARKER_ALIAS("gdms_marker_alias",
			"markeralias_id"), GDMS_MARKER_METADATASET("gdms_marker_metadataset", "marker_metadataset_id"), GDMS_MARKER_USER_INFO(
			"gdms_marker_user_info", "userinfo_id"), GDMS_MARKER_USER_INFO_DETAILS("gdms_marker_user_info_details", "contact_id"), GDMS_MARKERS_ONMAP(
			"gdms_markers_onmap", "markeronmap_id"), GDMS_MTA("gdms_mta", "mta_id"), GDMS_QTL("gdms_qtl", "qtl_id"), GDMS_TRACK_ACC(
			"gdms_track_acc", "tacc_id"), GDMS_TRACK_DATA("gdms_track_data", "track_id"), GDMS_TRACK_MARKERS("gdms_track_markers",
			"tmarker_id");

	private final String tableName;
	private final String idName;

	private GdmsTable(String tableName, String idName) {
		this.tableName = tableName;
		this.idName = idName;
	}

	public String getTableName() {
		return this.tableName;
	}

	public String getIdName() {
		return this.idName;
	}

}
