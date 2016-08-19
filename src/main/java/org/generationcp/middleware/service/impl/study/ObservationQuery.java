
package org.generationcp.middleware.service.impl.study;

import java.util.List;

import org.generationcp.middleware.service.api.study.TraitDto;

class ObservationQuery {

	/**
	 * Constructs a query that will enable us to retrieve information about all plots, associated metadata and measurements in one go, for a
	 * trial/nursery.
	 * 
	 * @param traits list of traits that we need to construct a query for.
	 */
	String getAllObservationsQuery(final List<TraitDto> traits) {
		return this.getObservationsMainQuery(traits) + getInstanceNumberClause() + getGroupOrderClause();
	}

	String getObservationsMainQuery(final List<TraitDto> traits) {
		StringBuilder sqlBuilder = new StringBuilder();
		
		sqlBuilder.append( 
				"SELECT \n" + 
				"    nde.nd_experiment_id,\n" + 
				"    gl.description AS TRIAL_INSTANCE,\n" +
				"    (SELECT iispcvt.definition FROM stockprop isp INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = isp.type_id INNER JOIN cvterm iispcvt ON iispcvt.cvterm_id = isp.value WHERE isp.stock_id = s.stock_id AND ispcvt.name = 'ENTRY_TYPE') ENTRY_TYPE, \n" +
				"    s.dbxref_id AS GID,\n" + 
				"    s.name DESIGNATION,\n" + 
				"    s.uniquename ENTRY_NO,\n" + 
				"    (SELECT isp.value FROM stockprop isp INNER JOIN cvterm ispcvt1 ON ispcvt1.cvterm_id = isp.type_id WHERE isp.stock_id = s.stock_id AND ispcvt1.name = 'SEED_SOURCE') SEED_SOURCE, \n" +
				"    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = ep.nd_experiment_id AND ispcvt.name = 'REP_NO') REP_NO, \n" + 
				"    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = ep.nd_experiment_id AND ispcvt.name = 'PLOT_NO') PLOT_NO, \n" + 
				"    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = ep.nd_experiment_id AND ispcvt.name = 'BLOCK_NO') BLOCK_NO, \n" + 
				"    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = ep.nd_experiment_id AND ispcvt.name = 'ROW') ROW_NO, \n" + 
				"    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = ep.nd_experiment_id AND ispcvt.name = 'COL') COL_NO, \n");
		
		String traitClauseFormat = 
				" MAX(IF(cvterm_variable.name = '%s', ph.value, NULL)) AS %s, \n" + 
				" MAX(IF(cvterm_variable.name = '%s', ph.phenotype_id, NULL)) AS %s, \n";

		for (TraitDto trait : traits) {
			sqlBuilder.append(String.format(traitClauseFormat, trait.getTraitName(), trait.getTraitName(),
					trait.getTraitName(), trait.getTraitName() + "_PhenotypeId"));
		}
		
		sqlBuilder.append(
				" 1=1 FROM \n" + 
				"    project p \n" + 
				"        INNER JOIN project_relationship pr ON p.project_id = pr.subject_project_id \n" + 
				"        INNER JOIN nd_experiment_project ep ON pr.subject_project_id = ep.project_id \n" + 
				"        INNER JOIN nd_experiment nde ON nde.nd_experiment_id = ep.nd_experiment_id \n" + 
				"        INNER JOIN nd_geolocation gl ON nde.nd_geolocation_id = gl.nd_geolocation_id \n" + 
				"        INNER JOIN nd_experiment_stock es ON ep.nd_experiment_id = es.nd_experiment_id \n" + 
				"        INNER JOIN stock s ON s.stock_id = es.stock_id \n" + 
				"        LEFT JOIN nd_experiment_phenotype neph ON neph.nd_experiment_id = nde.nd_experiment_id \n" + 
				"        LEFT JOIN phenotype ph ON neph.phenotype_id = ph.phenotype_id \n" + 
				"        LEFT JOIN cvterm cvterm_variable ON cvterm_variable.cvterm_id = ph.observable_id \n" + 
				" WHERE p.project_id = (SELECT  p.project_id FROM project_relationship pr INNER JOIN project p ON p.project_id = pr.subject_project_id WHERE (pr.object_project_id = :studyId AND name LIKE '%PLOTDATA')) \n");				
				
		return sqlBuilder.toString();
	}

	String getInstanceNumberClause() {
		return " AND gl.description = :instance_number \n";
	}

	String getGroupOrderClause() {
		return " GROUP BY nde.nd_experiment_id ORDER BY (1 * REP_NO), (1 * PLOT_NO) ";
	}

	String getSingleObservationQuery(final List<TraitDto> traits) {
		return this.getObservationsMainQuery(traits) + " AND nde.nd_experiment_id = :experiment_id \n" + getGroupOrderClause();
	}
}
