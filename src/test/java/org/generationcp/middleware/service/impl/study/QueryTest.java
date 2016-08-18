package org.generationcp.middleware.service.impl.study;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;

import org.generationcp.middleware.service.api.study.TraitDto;
import org.hibernate.jdbc.util.BasicFormatterImpl;
import org.junit.Test;

/**
 * The class <code>QueryTest</code> contains tests for the class
 * <code>{@link ObservationQuery}</code>.
 *
 * @generatedBy CodePro at 17/04/15 3:08 PM
 * @author Akhil
 * @version $Revision: 1.0 $
 */
public class QueryTest {
	
	final BasicFormatterImpl formattedSQL = new BasicFormatterImpl();
	
	/**
	 * Run the String generateQuery(String,List<String>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 17/04/15 3:08 PM
	 */
	@Test
	public void testMeasurementQueryGeneration() throws Exception {
		ObservationQuery fixture = new ObservationQuery();
		final List<TraitDto> traitNames = new LinkedList<TraitDto>();
		traitNames.add(new TraitDto(1,"PH_cm"));
		String result = fixture.getObservationQuery(traitNames);
		assertEquals("The expected query must match our expected queyr",
				formatString(expectedQueryForTheQueryGenerationTest()),
				formatString(result));
	}
	
	private String formatString(final String format) {
		return formattedSQL.format(format).replace(" ", "");
	}

	private String expectedQueryForTheQueryGenerationTest() {
		return "SELECT \n" + "    nde.nd_experiment_id,\n" + "    gl.description AS TRIAL_INSTANCE,\n"
				+ "    (SELECT iispcvt.definition FROM stockprop isp INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = isp.type_id INNER JOIN cvterm iispcvt ON iispcvt.cvterm_id = isp.value WHERE isp.stock_id = s.stock_id AND ispcvt.name = 'ENTRY_TYPE') ENTRY_TYPE, \n"
				+ "    s.dbxref_id AS GID,\n" + "    s.name DESIGNATION,\n" + "    s.uniquename ENTRY_NO,\n"
				+ "    (SELECT isp.value FROM stockprop isp INNER JOIN cvterm ispcvt1 ON ispcvt1.cvterm_id = isp.type_id WHERE isp.stock_id = s.stock_id AND ispcvt1.name = 'SEED_SOURCE') SEED_SOURCE, \n"
				+ "    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = ep.nd_experiment_id AND ispcvt.name = 'REP_NO') REP_NO, \n"
				+ "    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = ep.nd_experiment_id AND ispcvt.name = 'PLOT_NO') PLOT_NO, \n"
				+ "    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = ep.nd_experiment_id AND ispcvt.name = 'BLOCK_NO') BLOCK_NO, \n"
				+ "    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = ep.nd_experiment_id AND ispcvt.name = 'ROW') ROW_NO, \n"
				+ "    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = ep.nd_experiment_id AND ispcvt.name = 'COL') COL_NO, \n"
				+ " MAX(IF(cvterm_variable.name = 'PH_cm', ph.value, NULL)) AS PH_cm, \n"
				+ " MAX(IF(cvterm_variable.name = 'PH_cm', ph.phenotype_id, NULL)) AS PH_cm_PhenotypeId, \n" + " 1=1 FROM \n"
				+ "    project p \n" + "        INNER JOIN project_relationship pr ON p.project_id = pr.subject_project_id \n"
				+ "        INNER JOIN nd_experiment_project ep ON pr.subject_project_id = ep.project_id \n"
				+ "        INNER JOIN nd_experiment nde ON nde.nd_experiment_id = ep.nd_experiment_id \n"
				+ "        INNER JOIN nd_geolocation gl ON nde.nd_geolocation_id = gl.nd_geolocation_id \n"
				+ "        INNER JOIN nd_experiment_stock es ON ep.nd_experiment_id = es.nd_experiment_id \n"
				+ "        INNER JOIN stock s ON s.stock_id = es.stock_id \n"
				+ "        LEFT JOIN nd_experiment_phenotype neph ON neph.nd_experiment_id = nde.nd_experiment_id \n"
				+ "        LEFT JOIN phenotype ph ON neph.phenotype_id = ph.phenotype_id \n"
				+ "        LEFT JOIN cvterm cvterm_variable ON cvterm_variable.cvterm_id = ph.observable_id \n" + " WHERE \n"
				+ "		gl.description = :instance_number \n"
				+ "       AND p.project_id = (SELECT  p.project_id FROM project_relationship pr INNER JOIN project p ON p.project_id = pr.subject_project_id WHERE (pr.object_project_id = :studyId AND name LIKE '%PLOTDATA')) \n"
				+ " GROUP BY nde.nd_experiment_id ORDER BY (1 * REP_NO), (1 * PLOT_NO) ";
	}

}