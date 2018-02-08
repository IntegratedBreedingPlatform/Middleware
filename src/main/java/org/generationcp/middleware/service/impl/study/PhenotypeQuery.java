package org.generationcp.middleware.service.impl.study;

import org.generationcp.middleware.domain.oms.TermId;

public class PhenotypeQuery {

	public static final String PHENOTYPE_SEARCH = " SELECT " //
		+ "  nde.nd_experiment_id AS nd_experiment_id, " //
		+ "  nde.plot_id AS observationUnitDbId, " //
		+ "  '' AS observationUnitName, " //
		+ "  'plot' AS observationLevel, " //
		+ "  NULL AS plantNumber, " // Until we have plant level observation
		+ "  s.dbxref_id AS germplasmDbId, " //
		+ "  s.name AS germplasmName, " //
		+ "  gl.nd_geolocation_id AS studyDbId, " //
		+ "  p.name AS studyName, " //
		+ "  wp.project_name AS programName, " //
		+ "  FieldMapRow.value AS FieldMapRow, " //
		+ "  FieldMapCol.value AS FieldMapCol, " //
		+ "  (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = ep.nd_experiment_id AND ispcvt.name = 'PLOT_NO') AS plotNumber, " //
		+ "  (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = ep.nd_experiment_id AND ispcvt.name = 'BLOCK_NO') AS blockNumber, " //
		+ "  (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = ep.nd_experiment_id AND ispcvt.name = 'REP_NO') AS replicate, " //
		+ "  (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = ep.nd_experiment_id AND ispcvt.name = 'COL') AS COL, " //
		+ "  (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = ep.nd_experiment_id AND ispcvt.name = 'ROW') AS ROW, " //
		+ "  (SELECT l.locid FROM nd_geolocationprop gp INNER JOIN location l ON l.locid = gp.value WHERE gp.type_id = " + TermId.LOCATION_ID.getId() + " AND gp.nd_geolocation_id = gl.nd_geolocation_id) AS studyLocationDbId, " //
		+ "  (SELECT l.lname FROM nd_geolocationprop gp INNER JOIN location l ON l.locid = gp.value WHERE gp.type_id = " + TermId.LOCATION_ID.getId() + " AND gp.nd_geolocation_id = gl.nd_geolocation_id) AS studyLocation, " //
		+ "  (SELECT iispcvt.definition FROM stockprop isp INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = isp.type_id INNER JOIN cvterm iispcvt ON iispcvt.cvterm_id = isp.value WHERE isp.stock_id = s.stock_id AND ispcvt.name = 'ENTRY_TYPE') AS entryType, " //
		+ "  s.uniquename AS entryNumber " //
		+ "FROM " //
		+ "  project plotdata_project " //
		+ "  INNER JOIN nd_experiment_project ep ON plotdata_project.project_id = ep.project_id " //
		+ "  INNER JOIN nd_experiment nde ON nde.nd_experiment_id = ep.nd_experiment_id " //
		+ "  INNER JOIN nd_geolocation gl ON nde.nd_geolocation_id = gl.nd_geolocation_id " //
		+ "  INNER JOIN nd_experiment_stock es ON ep.nd_experiment_id = es.nd_experiment_id " //
		+ "  INNER JOIN stock s ON s.stock_id = es.stock_id " //
		+ "  INNER JOIN project_relationship pr ON plotdata_project.project_id = pr.subject_project_id " //
		+ "  INNER JOIN project p ON pr.object_project_id = p.project_id " //
		+ "  INNER JOIN workbench.workbench_project wp ON p.program_uuid = wp.project_uuid " //
		+ "  LEFT JOIN nd_experimentprop FieldMapRow ON FieldMapRow.nd_experiment_id = ep.nd_experiment_id AND FieldMapRow.type_id = 8410 " //
		+ "  LEFT JOIN nd_experimentprop FieldMapCol ON FieldMapCol.nd_experiment_id = ep.nd_experiment_id AND FieldMapCol.type_id = 8400 " //
		+ "WHERE 1 = 1 " //
		; //

	public static final String PHENOTYPE_SEARCH_STUDY_DB_ID_FILTER = " AND gl.nd_geolocation_id in (:studyDbIds) ";

	public static final String PHENOTYPE_SEARCH_OBSERVATION_FILTER = " AND exists(SELECT 1 " //
		+ " FROM nd_experiment_phenotype ndeph " //
		+ "   INNER JOIN phenotype ph ON ndeph.phenotype_id = ph.phenotype_id " //
		+ "   INNER JOIN cvterm cvt ON ph.observable_id = cvt.cvterm_id " //
		+ " WHERE ndeph.nd_experiment_id = nde.nd_experiment_id AND cvt.cvterm_id in (:cvTermIds))" //
		;

	public static final String PHENOTYPE_SEARCH_OBSERVATIONS = "SELECT " //
		+ "  ndeph.nd_experiment_id as expid, " //
		+ "  ndeph.nd_experiment_phenotype_id as nd_exp_phen_id, " //
		+ "  cvt.cvterm_id as cvterm_id, " //
		+ "  cvt.name as cvterm_name, " //
		+ "  ph.value as value , " //
		+ "  cvp.value as crop_ontology_id "
		+ "FROM " //
		+ "  nd_experiment_phenotype ndeph" //
		+ "  INNER JOIN phenotype ph ON ndeph.phenotype_id = ph.phenotype_id " //
		+ "  INNER JOIN cvterm cvt ON ph.observable_id = cvt.cvterm_id " //
		+ "  LEFT JOIN cvtermprop cvp on (cvp.cvterm_id = cvt.cvterm_id and cvp.type_id = " + TermId.CROP_ONTOLOGY_ID.getId() + ")"
		+ "WHERE ndeph.nd_experiment_id in (:ndExperimentIds)" //
		;


}
