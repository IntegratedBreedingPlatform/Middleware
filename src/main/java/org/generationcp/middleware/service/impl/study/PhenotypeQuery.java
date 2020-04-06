package org.generationcp.middleware.service.impl.study;

import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;

public class PhenotypeQuery {

	public static final String PHENOTYPE_SEARCH = " SELECT " //
		+ "  nde.nd_experiment_id AS nd_experiment_id, " //
		+ "  nde.obs_unit_id AS observationUnitDbId, " //
		+ "  nde.json_props AS jsonProps, " //
		+ "  '' AS observationUnitName, " //
		+ "  dataset_type.name AS observationLevel, " //
		+ "  NULL AS plantNumber, " // Until we have plant level observation
		+ "  s.dbxref_id AS germplasmDbId, " //
		+ "  s.name AS germplasmName, " //
		+ "  env_experiment.observation_unit_no AS instanceNumber, " //
		+ "  nde.parent_id AS studyDbId, " //
		+ "  concat(p.name, '_', env_experiment.observation_unit_no) AS studyName, " //
		+ "  wp.project_name AS programName, " //
		+ "  FieldMapRow.value AS FieldMapRow, " //
		+ "  FieldMapCol.value AS FieldMapCol, " //
		+ "  (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = nde.nd_experiment_id AND ispcvt.name = 'PLOT_NO') AS plotNumber, " //
		+ "  (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = nde.nd_experiment_id AND ispcvt.name = 'BLOCK_NO') AS blockNumber, " //
		+ "  (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = nde.nd_experiment_id AND ispcvt.name = 'REP_NO') AS replicate, " //
		+ "  (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = nde.nd_experiment_id AND ispcvt.name = 'COL') AS COL, " //
		+ "  (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = nde.nd_experiment_id AND ispcvt.name = 'ROW') AS ROW, " //
		+ "  l.locid AS studyLocationDbId, " //
		+ "  l.lname AS studyLocation, " //
		+ "  (SELECT iispcvt.definition FROM stockprop isp INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = isp.type_id INNER JOIN cvterm iispcvt ON iispcvt.cvterm_id = isp.value WHERE isp.stock_id = s.stock_id AND ispcvt.name = 'ENTRY_TYPE') AS entryType, " //
		+ "  s.uniquename AS entryNumber,"
		+ "  dataset.program_uuid as programDbId,"
		+ "  p.project_id as trialDbId, " //
		+ "  p.name as trialDbName "
		+ " FROM " //
		+ "  project dataset " //
		+ "  INNER JOIN nd_experiment nde ON nde.project_id = dataset.project_id " //
		+ "  INNER JOIN nd_experiment env_experiment ON nde.parent_id = env_experiment.nd_experiment_id " //
		+ "  INNER JOIN stock s ON s.stock_id = nde.stock_id " //
		+ "  INNER JOIN project p ON p.project_id = dataset.study_id " //
		+ "  LEFT JOIN workbench.workbench_project wp ON p.program_uuid = wp.project_uuid " //
		+ "  LEFT JOIN nd_experimentprop FieldMapRow ON FieldMapRow.nd_experiment_id = nde.nd_experiment_id AND FieldMapRow.type_id = " + TermId.FIELDMAP_RANGE.getId() //
		+ "  LEFT JOIN nd_experimentprop FieldMapCol ON FieldMapCol.nd_experiment_id = nde.nd_experiment_id AND FieldMapCol.type_id = " + TermId.FIELDMAP_COLUMN.getId() //
		+ "  LEFT JOIN dataset_type ON dataset_type.dataset_type_id = dataset.dataset_type_id " //
		+ "  LEFT JOIN nd_experimentprop loc_prop ON loc_prop.nd_experiment_id = env_experiment.nd_experiment_id AND loc_prop.type_id = " + TermId.LOCATION_ID.getId()//
		+ "  LEFT JOIN location l ON l.locid = loc_prop.value " //
		+ " WHERE 1 = 1" //
		; //

	public static final String PHENOTYPE_SEARCH_STUDY_DB_ID_FILTER = " AND nde.parent_id in (:studyDbIds) ";

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

	public static final String PHENOTYPE_SEARCH_OBSERVATIONS = "SELECT " //
		+ "  ph.nd_experiment_id as expid, " //
		+ "  ph.phenotype_id as phen_id, " //
		+ "  cvt.cvterm_id as cvterm_id, " //
		+ "  cvt.name as cvterm_name, " //
		+ "  ph.value as value , " //
		+ "  cvp.value as crop_ontology_id, "
		+ "  ph.updated_date as updated_date "
		+ " FROM " //
		+ "  phenotype ph  " //
		+ "  INNER JOIN cvterm cvt ON ph.observable_id = cvt.cvterm_id " //
		+ "  INNER JOIN nd_experiment ndep ON ph.nd_experiment_id = ndep.nd_experiment_id " //
		+ "  INNER JOIN projectprop pp ON pp.project_id = ndep.project_id " //
		+ "                            AND pp.variable_id = ph.observable_id " //
		+ "                            AND pp.type_id = " + VariableType.TRAIT.getId() //
		+ "  LEFT JOIN cvtermprop cvp on (cvp.cvterm_id = cvt.cvterm_id and cvp.type_id = " + TermId.CROP_ONTOLOGY_ID.getId() + ")"
		+ " WHERE ph.nd_experiment_id in (:ndExperimentIds) " //
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
