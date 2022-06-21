package org.generationcp.middleware.service.impl.study;

import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;

public class PhenotypeQuery {

	public static final String PHENOTYPE_SEARCH = " SELECT " //
		+ "  nde.nd_experiment_id AS nd_experiment_id, " //
		+ "  nde.obs_unit_id AS observationUnitDbId, " //
		+ "  nde.json_props AS jsonProps, " //
		+ "  CONCAT(dataset_type.name, plotNumber.value) AS observationUnitName, " //
		+ "  dataset_type.name AS datasetName, " //
		+ "  NULL AS plantNumber, " // Until we have plant level observation
		+ "  g.germplsm_uuid AS germplasmDbId, " //
		+ "  s.name AS germplasmName, " //
		+ "  gl.description AS instanceNumber, " //
		+ "  gl.nd_geolocation_id AS studyDbId, " //
		+ "  concat(p.name, '_', gl.description) AS studyName, " //
		+ "  wp.project_name AS programName, " //
		+ "  FieldMapRow.value AS FieldMapRow, " //
		+ "  FieldMapCol.value AS FieldMapCol, " //
		+ "  plotNumber.value AS plotNumber, " //
		+ "  (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = nde.nd_experiment_id AND ispcvt.name = 'BLOCK_NO') AS blockNumber, "
		//
		+ "  (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = nde.nd_experiment_id AND ispcvt.name = 'REP_NO') AS replicate, "
		//
		+ "  (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = nde.nd_experiment_id AND ispcvt.name = 'COL') AS COL, "
		//
		+ "  (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = nde.nd_experiment_id AND ispcvt.name = 'ROW') AS ROW, "
		//
		+ "  l.locid AS studyLocationDbId, " //
		+ "  l.lname AS studyLocation, " //
		+ "  (SELECT iispcvt.definition FROM stockprop isp INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = isp.type_id INNER JOIN cvterm iispcvt ON iispcvt.cvterm_id = isp.cvalue_id WHERE isp.stock_id = s.stock_id AND ispcvt.name = 'ENTRY_TYPE') AS entryType, "
		//
		+ "  s.uniquename AS entryNumber,"
		+ "  dataset.program_uuid as programDbId,"
		+ "  p.project_id as trialDbId, " //
		+ "  p.name as trialDbName, "//
		+ "  dataset.project_id as datasetDbId "
		+ " FROM " //
		+ "  project dataset " //
		+ "  INNER JOIN nd_experiment nde ON nde.project_id = dataset.project_id " //
		+ "  INNER JOIN nd_geolocation gl ON nde.nd_geolocation_id = gl.nd_geolocation_id " //
		+ "  INNER JOIN stock s ON s.stock_id = nde.stock_id " //
		+ "  INNER JOIN project p ON p.project_id = dataset.study_id " //
		+ "  INNER JOIN germplsm g ON g.gid = s.dbxref_id "
		+ "  LEFT JOIN workbench.workbench_project wp ON p.program_uuid = wp.project_uuid " //
		+ "  LEFT JOIN nd_experimentprop plotNumber ON plotNumber.nd_experiment_id = nde.nd_experiment_id AND plotNumber.type_id = "
		+ TermId.PLOT_NO.getId() //
		+ "  LEFT JOIN nd_experimentprop FieldMapRow ON FieldMapRow.nd_experiment_id = nde.nd_experiment_id AND FieldMapRow.type_id = "
		+ TermId.FIELDMAP_RANGE.getId() //
		+ "  LEFT JOIN nd_experimentprop FieldMapCol ON FieldMapCol.nd_experiment_id = nde.nd_experiment_id AND FieldMapCol.type_id = "
		+ TermId.FIELDMAP_COLUMN.getId() //
		+ "  LEFT JOIN dataset_type ON dataset_type.dataset_type_id = dataset.dataset_type_id " //
		+ "  LEFT JOIN nd_geolocationprop gp ON gl.nd_geolocation_id = gp.nd_geolocation_id AND gp.type_id = " + TermId.LOCATION_ID.getId()
		+ " AND gp.nd_geolocation_id = gl.nd_geolocation_id " //
		+ "  LEFT JOIN location l ON l.locid = gp.value " //
		+ " WHERE p.deleted = 0 " //
		; //

	public static final String PHENOTYPE_SEARCH_STUDY_DB_ID_FILTER = " AND gl.nd_geolocation_id in (:studyDbIds) ";

	public static final String PHENOTYPE_SEARCH_OBSERVATION_FILTER = " AND exists(SELECT 1 " //
		+ " FROM phenotype ph " //
		+ "   INNER JOIN cvterm cvt ON ph.observable_id = cvt.cvterm_id " //
		+ "   INNER JOIN nd_experiment ndep ON ph.nd_experiment_id = ndep.nd_experiment_id " //
		+ "   INNER JOIN project p ON ndep.project_id = p.project_id " //
		+ "   INNER JOIN projectprop pp ON pp.project_id = p.project_id " //
		+ "                             AND pp.variable_id = ph.observable_id " //
		+ "                             AND pp.type_id = " + VariableType.TRAIT.getId() //
		+ " WHERE ph.nd_experiment_id = nde.nd_experiment_id AND cvt.cvterm_id in (:cvTermIds))" //
		;

	public static final String TREATMENT_FACTORS_SEARCH_OBSERVATIONS = "SELECT DISTINCT "
		+ "    CVT.NAME AS factor, pp.value AS modality, nde.nd_experiment_id as nd_experiment_id "
		+ "FROM "
		+ "    projectprop pp "
		+ "        INNER JOIN "
		+ "    CVTERM CVT ON PP.VARIABLE_ID = CVT.cvterm_id "
		+ " INNER JOIN nd_experiment nde ON nde.project_id = pp.project_id"
		+ " WHERE "
		+ "    PP.type_id = " + TermId.MULTIFACTORIAL_INFO.getId()
		+ " AND nde.nd_experiment_id in (:ndExperimentIds) ";
}
