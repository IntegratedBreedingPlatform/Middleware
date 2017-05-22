package org.generationcp.middleware.service.impl.study;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;

import org.generationcp.middleware.service.api.study.MeasurementVariableDto;
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
		final List<MeasurementVariableDto> traitNames = new LinkedList<MeasurementVariableDto>();
		traitNames.add(new MeasurementVariableDto(1,"PH_cm"));
		String result = fixture.getObservationQuery(traitNames);
		System.out.println(formatString(result));
		assertEquals("The expected query must match our expected queyr",
				formatString(expectedQueryForTheQueryGenerationTest()),
				formatString(result));
	}
	
	private String formatString(final String format) {
		return formattedSQL.format(format).replace(" ", "");
	}

	private String expectedQueryForTheQueryGenerationTest() {
		return " SELECT  \n"
			+ "     nde.nd_experiment_id, \n"
			+ "     gl.description AS TRIAL_INSTANCE, \n"
			+ "     (SELECT  \n"
			+ "             iispcvt.definition \n"
			+ "         FROM \n"
			+ "             stockprop isp \n"
			+ "                 INNER JOIN \n"
			+ "             cvterm ispcvt ON ispcvt.cvterm_id = isp.type_id \n"
			+ "                 INNER JOIN \n"
			+ "             cvterm iispcvt ON iispcvt.cvterm_id = isp.value \n"
			+ "         WHERE \n"
			+ "             isp.stock_id = s.stock_id \n"
			+ "                 AND ispcvt.name = 'ENTRY_TYPE') ENTRY_TYPE, \n"
			+ "     s.dbxref_id AS GID, \n"
			+ "     s.name DESIGNATION, \n"
			+ "     s.uniquename ENTRY_NO, \n"
			+ "     s.value as ENTRY_CODE, \n"
			+ "     (SELECT  \n"
			+ "             isp.value \n"
			+ "         FROM \n"
			+ "             stockprop isp \n"
			+ "                 INNER JOIN \n"
			+ "             cvterm ispcvt1 ON ispcvt1.cvterm_id = isp.type_id \n"
			+ "         WHERE \n"
			+ "             isp.stock_id = s.stock_id \n"
			+ "                 AND ispcvt1.name = 'SEED_SOURCE') SEED_SOURCE, \n"
			+ "     (SELECT  \n"
			+ "             ndep.value \n"
			+ "         FROM \n"
			+ "             nd_experimentprop ndep \n"
			+ "                 INNER JOIN \n"
			+ "             cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id \n"
			+ "         WHERE \n"
			+ "             ndep.nd_experiment_id = ep.nd_experiment_id \n"
			+ "                 AND ispcvt.name = 'REP_NO') REP_NO, \n"
			+ "     (SELECT  \n"
			+ "             ndep.value \n"
			+ "         FROM \n"
			+ "             nd_experimentprop ndep \n"
			+ "                 INNER JOIN \n"
			+ "             cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id \n"
			+ "         WHERE \n"
			+ "             ndep.nd_experiment_id = ep.nd_experiment_id \n"
			+ "                 AND ispcvt.name = 'PLOT_NO') PLOT_NO, \n"
			+ "     nde.plot_id as PLOT_ID  \n"
			+ " ,  \n"
			+ " PH_cm.PhenotypeValue AS PH_cm, \n"
			+ " PH_cm.phenotype_id AS PH_cm_PhenotypeId \n"
			+ "  FROM \n"
			+ "     Project p \n"
			+ "         INNER JOIN \n"
			+ "     project_relationship pr ON p.project_id = pr.subject_project_id \n"
			+ "         INNER JOIN \n"
			+ "     nd_experiment_project ep ON pr.subject_project_id = ep.project_id \n"
			+ "         INNER JOIN \n"
			+ "     nd_experiment nde ON nde.nd_experiment_id = ep.nd_experiment_id \n"
			+ "         INNER JOIN \n"
			+ "     nd_geolocation gl ON nde.nd_geolocation_id = gl.nd_geolocation_id \n"
			+ "         INNER JOIN \n"
			+ "     nd_experiment_stock es ON ep.nd_experiment_id = es.nd_experiment_id \n"
			+ "         INNER JOIN \n"
			+ "     Stock s ON s.stock_id = es.stock_id \n"
			+ "         LEFT OUTER JOIN \n"
			+ "     (SELECT  \n"
			+ "         nep.nd_experiment_id, \n"
			+ "             pt.phenotype_id, \n"
			+ "             IF(cvterm_id = cvterm_id, pt.value, NULL) AS PhenotypeValue \n"
			+ "     FROM \n"
			+ "         phenotype pt \n"
			+ "     INNER JOIN cvterm svdo ON svdo.cvterm_id = pt.observable_id \n"
			+ "     INNER JOIN nd_experiment_phenotype nep ON nep.phenotype_id = pt.phenotype_id \n"
			+ "     WHERE \n"
			+ "         svdo.name = ? ) PH_cm ON PH_cm.nd_experiment_id = nde.nd_experiment_id \n"
			+ "     LEFT JOIN nd_experimentprop FieldMapRow ON FieldMapRow.nd_experiment_id = ep.nd_experiment_id AND FieldMapRow.type_id = 8410 \n"
			+ "     LEFT JOIN nd_experimentprop FieldMapCol ON FieldMapCol.nd_experiment_id = ep.nd_experiment_id AND FieldMapCol.type_id = 8400 \n"
			+ " WHERE \n"
			+ "     p.project_id = (Select p.project_id from project_relationship pr \n"
			+ " INNER JOIN project p on p.project_id = pr.subject_project_id \n"
			+ " where (pr.object_project_id = ? and name LIKE '%PLOTDATA')) ORDER BY PH_cm_PhenotypeId \n";
	}

}
