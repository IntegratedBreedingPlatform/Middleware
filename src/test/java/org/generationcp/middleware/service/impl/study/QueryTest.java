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
		System.out.println(formatString(result));
		assertEquals("The expected query must match our expected queyr",
				formatString(expectedQueryForTheQueryGenerationTest()),
				formatString(result));
	}
	
	private String formatString(final String format) {
		return formattedSQL.format(format).replace(" ", "");
	}

	private String expectedQueryForTheQueryGenerationTest() {
		return "    SELECT\n" + 
				"        nde.nd_experiment_id,\n" + 
				"        gl.description AS TRIAL_INSTANCE,\n" + 
				"        (SELECT\n" + 
				"            iispcvt.definition   \n" + 
				"        FROM\n" + 
				"            stockprop isp     \n" + 
				"        INNER JOIN\n" + 
				"            cvterm ispcvt \n" + 
				"                ON ispcvt.cvterm_id = isp.type_id     \n" + 
				"        INNER JOIN\n" + 
				"            cvterm iispcvt \n" + 
				"                ON iispcvt.cvterm_id = isp.value   \n" + 
				"        WHERE\n" + 
				"            isp.stock_id = s.stock_id     \n" + 
				"            AND ispcvt.name = 'ENTRY_TYPE') ENTRY_TYPE,\n" + 
				"        s.dbxref_id AS GID,\n" + 
				"        s.name DESIGNATION,\n" + 
				"        s.uniquename ENTRY_NO,\n" + 
				"        (SELECT\n" + 
				"            isp.value   \n" + 
				"        FROM\n" + 
				"            stockprop isp     \n" + 
				"        INNER JOIN\n" + 
				"            cvterm ispcvt1 \n" + 
				"                ON ispcvt1.cvterm_id = isp.type_id   \n" + 
				"        WHERE\n" + 
				"            isp.stock_id = s.stock_id     \n" + 
				"            AND ispcvt1.name = 'SEED_SOURCE') SEED_SOURCE,\n" + 
				"        (SELECT\n" + 
				"            ndep.value   \n" + 
				"        FROM\n" + 
				"            nd_experimentprop ndep     \n" + 
				"        INNER JOIN\n" + 
				"            cvterm ispcvt \n" + 
				"                ON ispcvt.cvterm_id = ndep.type_id   \n" + 
				"        WHERE\n" + 
				"            ndep.nd_experiment_id = ep.nd_experiment_id     \n" + 
				"            AND ispcvt.name = 'REP_NO') REP_NO,\n" + 
				"        (SELECT\n" + 
				"            ndep.value   \n" + 
				"        FROM\n" + 
				"            nd_experimentprop ndep     \n" + 
				"        INNER JOIN\n" + 
				"            cvterm ispcvt \n" + 
				"                ON ispcvt.cvterm_id = ndep.type_id   \n" + 
				"        WHERE\n" + 
				"            ndep.nd_experiment_id = ep.nd_experiment_id     \n" + 
				"            AND ispcvt.name = 'PLOT_NO') PLOT_NO,\n" + 
				"        PH_cm.PhenotypeValue  AS PH_cm,\n" + 
				"        PH_cm.phenotype_id AS PH_cm_PhenotypeId \n" + 
				"    FROM\n" + 
				"        Project p   \n" + 
				"    INNER JOIN\n" + 
				"        project_relationship pr \n" + 
				"            ON p.project_id = pr.subject_project_id   \n" + 
				"    INNER JOIN\n" + 
				"        nd_experiment_project ep \n" + 
				"            ON pr.subject_project_id = ep.project_id   \n" + 
				"    INNER JOIN\n" + 
				"        nd_experiment nde \n" + 
				"            ON nde.nd_experiment_id = ep.nd_experiment_id   \n" + 
				"    INNER JOIN\n" + 
				"        nd_geolocation gl \n" + 
				"            ON nde.nd_geolocation_id = gl.nd_geolocation_id   \n" + 
				"    INNER JOIN\n" + 
				"        nd_experiment_stock es \n" + 
				"            ON ep.nd_experiment_id = es.nd_experiment_id   \n" + 
				"    INNER JOIN\n" + 
				"        Stock s \n" + 
				"            ON s.stock_id = es.stock_id   \n" + 
				"    LEFT OUTER JOIN\n" + 
				"        (\n" + 
				"            SELECT\n" + 
				"                nep.nd_experiment_id,\n" + 
				"                pt.phenotype_id,\n" + 
				"                IF(cvterm_id = cvterm_id,\n" + 
				"                pt.value,\n" + 
				"                NULL) AS PhenotypeValue  \n" + 
				"            FROM\n" + 
				"                phenotype pt  \n" + 
				"            INNER JOIN\n" + 
				"                cvterm svdo \n" + 
				"                    ON svdo.cvterm_id = pt.observable_id  \n" + 
				"            INNER JOIN\n" + 
				"                nd_experiment_phenotype nep \n" + 
				"                    ON nep.phenotype_id = pt.phenotype_id  \n" + 
				"            WHERE\n" + 
				"                svdo.name = ?\n" + 
				"        ) PH_cm \n" + 
				"            ON PH_cm.nd_experiment_id = nde.nd_experiment_id \n" + 
				"    WHERE\n" + 
				"p.project_id= (\n" + 
				"	Select\n" + 
				"		p.project_id\n" + 
				"	from\n" + 
				"			project_relationshippr\n" + 
				"	INNER JOIN\n" + 
				"projectp\n" + 
				"on p.project_id=pr.subject_project_id\n" + 
				"where\n" + 
				"(\n" +
				"pr.object_project_id = ?\n" + 
				"and name LIKE '%PLOTDATA'\n" + 
				")\n)"
				+ "ORDER BY PH_cm_PhenotypeId";
	}

}