
package org.generationcp.middleware.service.impl.study;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;

import org.generationcp.middleware.service.api.study.MeasurementVariableDto;
import org.hibernate.jdbc.util.BasicFormatterImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.beust.jcommander.internal.Lists;

/**
 * The class <code>QueryTest</code> contains tests for the class <code>{@link ObservationQuery}</code>.
 *
 * @generatedBy CodePro at 17/04/15 3:08 PM
 * @author Akhil
 * @version $Revision: 1.0 $
 */
public class ObservationQueryTest {

	private static final String PH_CM = "PH_cm";
	private static final String STOCK_ID = "STOCK_ID";
	private static final String FACT1 = "FACT1";
	final BasicFormatterImpl formattedSQL = new BasicFormatterImpl();
	final List<String> germplasmDescriptors = Lists.newArrayList(STOCK_ID);
	final List<String> designFactors = Lists.newArrayList(FACT1);
	List<MeasurementVariableDto> traitNames  = new LinkedList<>();

	private ObservationQuery observationQuery;

	@Before
	public void setup() {
		this.observationQuery = new ObservationQuery();
		this.traitNames = new LinkedList<>();
		this.traitNames.add(new MeasurementVariableDto(1, PH_CM));
	}

	/**
	 * Run the String generateQuery(String,List<String>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 17/04/15 3:08 PM
	 */
	@Test
	public void testGetAllMeasurementsQueryGeneration() throws Exception {
		final String result = this.observationQuery.getAllObservationsQuery(this.traitNames, this.germplasmDescriptors, this.designFactors, null, null);
		assertEquals("The generated query must match the expected query.", this.formatString(this.expectedQueryForAllMeasurements()),
			this.formatString(result));
	}

	@Test
	public void testGetSingleMeasurementQueryGeneration() throws Exception {
		final String result = this.observationQuery.getSingleObservationQuery(this.traitNames, this.germplasmDescriptors, this.designFactors);
		assertEquals("The generated query must match the expected query.", this.formatString(this.expectedQueryForSingleMeasurement()),
			this.formatString(result));
	}

	@Test
	public void testGetSampleObservationQuery() {
		final String sql = "SELECT \n" + "    nde.nd_experiment_id as nd_experiment_id,\n"
				+ "    (select na.nval from names na where na.gid = s.dbxref_id and na.nstat = 1 limit 1) as preferred_name,\n" + "    ph.value"
				+ " as value, s.dbxref_id as gid"
				+ " FROM \n" + "    project p \n" + "        INNER JOIN project_relationship pr ON p.project_id = pr.subject_project_id \n"
				+ "        INNER JOIN nd_experiment nde ON nde.project_id = pr.subject_project_id \n"
				+ "        INNER JOIN nd_geolocation gl ON nde.nd_geolocation_id = gl.nd_geolocation_id \n"
				+ "        INNER JOIN stock s ON s.stock_id = nde.stock_id \n"
				+ "        LEFT JOIN phenotype ph ON nde.nd_experiment_id = ph.nd_experiment_id \n"
				+ "        LEFT JOIN cvterm cvterm_variable ON cvterm_variable.cvterm_id = ph.observable_id \n" + " WHERE \n"
				+ "\tp.project_id = :datasetId \n" + " AND gl.description IN (:instanceIds) \n"
				+ " and cvterm_variable.cvterm_id = :selectionVariableId\n" + " GROUP BY nde.nd_experiment_id";
		assertEquals("The generated query must match the expected query.", this.formatString(sql),
			this.formatString(this.observationQuery.getSampleObservationQuery()));
	}

	@Test
	public void testGetOrderingClause() {
		final String orderBy = " ORDER BY ";
		String orderingClause = this.observationQuery.getOrderingClause(null, null);
		String expectedResult = orderBy + "(1 * " + ObservationQuery.DEFAULT_SORT_COLUMN + ") " + ObservationQuery.DEFAULT_SORT_ORDER + " ";
		Assert.assertEquals(expectedResult, orderingClause);
		orderingClause = this.observationQuery.getOrderingClause("FIELDMAP RANGE", "desc");
		expectedResult = orderBy + "`FIELDMAP RANGE` desc ";
		Assert.assertEquals(expectedResult, orderingClause);
	}

	private String formatString(final String format) {
		return this.formattedSQL.format(format).replace(" ", "");
	}

	private String expectedQueryForAllMeasurements() {
		return "SELECT \n"
				+ "    nde.nd_experiment_id,\n"
				+ "    gl.description AS TRIAL_INSTANCE,\n"
				+ "    (SELECT iispcvt.definition FROM stockprop isp INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = isp.type_id INNER JOIN cvterm iispcvt ON iispcvt.cvterm_id = isp.value WHERE isp.stock_id = s.stock_id AND ispcvt.name = 'ENTRY_TYPE') ENTRY_TYPE, \n"
				+ "    s.dbxref_id AS GID,\n"
				+ "    s.name DESIGNATION,\n"
				+ "    s.uniquename ENTRY_NO,\n"
				+ "    s.value as ENTRY_CODE,\n"
				+ "    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = nde.nd_experiment_id AND ispcvt.name = 'REP_NO') REP_NO, \n"
				+ "    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = nde.nd_experiment_id AND ispcvt.name = 'PLOT_NO') PLOT_NO, \n"
				+ "    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = nde.nd_experiment_id AND ispcvt.name = 'BLOCK_NO') BLOCK_NO, \n"
				+ "    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = nde.nd_experiment_id AND ispcvt.name = 'ROW') ROW, \n"
				+ "    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = nde.nd_experiment_id AND ispcvt.name = 'COL') COL, \n"
				+ "    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = nde.nd_experiment_id AND ispcvt.name = 'FIELDMAP COLUMN') 'FIELDMAP COLUMN', \n"
				+ "    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = nde.nd_experiment_id AND ispcvt.name = 'FIELDMAP RANGE') 'FIELDMAP RANGE', \n"
				+ "    (SELECT coalesce(nullif(count(sp.sample_id), 0), '-') FROM sample AS sp INNER JOIN nd_experiment sp_nde ON sp.nd_experiment_id = sp_nde.nd_experiment_id WHERE sp_nde.nd_experiment_id = nde.nd_experiment_id OR sp_nde.parent_id = nde.nd_experiment_id) 'SUM_OF_SAMPLES', \n"
				+ "    nde.obs_unit_id as OBS_UNIT_ID,"
				+ " MAX(IF(cvterm_variable.name = '" + ObservationQueryTest.PH_CM + "', ph.value, NULL)) AS '" + ObservationQueryTest.PH_CM + "', \n"
				+ " MAX(IF(cvterm_variable.name = '" + ObservationQueryTest.PH_CM + "', ph.phenotype_id, NULL)) AS '" + ObservationQueryTest.PH_CM + "_PhenotypeId', \n"
				+ " MAX(IF(cvterm_variable.name = '" + ObservationQueryTest.PH_CM + "', ph.status, NULL)) AS '" + ObservationQueryTest.PH_CM + "_Status', \n"
				+ "   (SELECT sprop.value FROM stockprop sprop INNER JOIN cvterm spropcvt ON spropcvt.cvterm_id = sprop.type_id WHERE sprop.stock_id = s.stock_id AND spropcvt.name = '" + ObservationQueryTest.STOCK_ID + "') '" + ObservationQueryTest.STOCK_ID + "', \n"
				+ "   (SELECT xprop.value FROM nd_experimentprop xprop INNER JOIN cvterm xpropcvt ON xpropcvt.cvterm_id = xprop.type_id WHERE xprop.nd_experiment_id = nde.nd_experiment_id AND xpropcvt.name = '" + ObservationQueryTest.FACT1 + "') '" + ObservationQueryTest.FACT1 + "', \n"
				+ " 1=1 FROM \n"
				+ "    project p \n"
				+ "        INNER JOIN project_relationship pr ON p.project_id = pr.subject_project_id \n"
				+ "        INNER JOIN nd_experiment nde ON nde.project_id = pr.subject_project_id \n"
				+ "        INNER JOIN nd_geolocation gl ON nde.nd_geolocation_id = gl.nd_geolocation_id \n"
				+ "        INNER JOIN stock s ON s.stock_id = nde.stock_id \n"
				+ "        LEFT JOIN phenotype ph ON nde.nd_experiment_id = ph.nd_experiment_id \n"
				+ "       LEFT JOIN cvterm cvterm_variable ON cvterm_variable.cvterm_id = ph.observable_id "
					+ " WHERE p.project_id = (SELECT  p.project_id FROM project_relationship pr INNER JOIN project p ON p.project_id = pr.subject_project_id WHERE (pr.object_project_id = :studyId AND name LIKE '%PLOTDATA')) \n"
				+ "		AND gl.nd_geolocation_id = :instanceId \n"
				+ " GROUP BY nde.nd_experiment_id "
				+ " ORDER BY (1*PLOT_NO) asc ";
	}

	private String expectedQueryForSingleMeasurement() {

		return "SELECT \n" + "    nde.nd_experiment_id,\n" + "    gl.description AS TRIAL_INSTANCE,\n"
				+ "    (SELECT iispcvt.definition FROM stockprop isp INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = isp.type_id INNER JOIN cvterm iispcvt ON iispcvt.cvterm_id = isp.value WHERE isp.stock_id = s.stock_id AND ispcvt.name = 'ENTRY_TYPE') ENTRY_TYPE, \n"
				+ "    s.dbxref_id AS GID,\n"
				+ "    s.name DESIGNATION,\n"
				+ "    s.uniquename ENTRY_NO,\n"
				+ "    s.value as ENTRY_CODE,\n"
				+ "    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = nde.nd_experiment_id AND ispcvt.name = 'REP_NO') REP_NO, \n"
				+ "    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = nde.nd_experiment_id AND ispcvt.name = 'PLOT_NO') PLOT_NO, \n"
				+ "    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = nde.nd_experiment_id AND ispcvt.name = 'BLOCK_NO') BLOCK_NO, \n"
				+ "    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = nde.nd_experiment_id AND ispcvt.name = 'ROW') ROW, \n"
				+ "    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = nde.nd_experiment_id AND ispcvt.name = 'COL') COL, \n"
				+ "    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = nde.nd_experiment_id AND ispcvt.name = 'FIELDMAP COLUMN') 'FIELDMAP COLUMN', \n"
				+ "    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = nde.nd_experiment_id AND ispcvt.name = 'FIELDMAP RANGE') 'FIELDMAP RANGE', \n"
				+ "    (SELECT coalesce(nullif(count(sp.sample_id), 0), '-') FROM sample AS sp "
			 	+ " INNER JOIN nd_experiment sp_nde ON sp.nd_experiment_id = sp_nde.nd_experiment_id WHERE sp_nde.nd_experiment_id = nde.nd_experiment_id OR sp_nde.parent_id = nde.nd_experiment_id) 'SUM_OF_SAMPLES',"
				+ "    nde.obs_unit_id as OBS_UNIT_ID,"
				+ " MAX(IF(cvterm_variable.name = '" + ObservationQueryTest.PH_CM + "', ph.value, NULL)) AS '" + ObservationQueryTest.PH_CM + "', \n"
				+ " MAX(IF(cvterm_variable.name = '" + ObservationQueryTest.PH_CM + "', ph.phenotype_id, NULL)) AS '" + ObservationQueryTest.PH_CM + "_PhenotypeId', \n"
				+ " MAX(IF(cvterm_variable.name = '" + ObservationQueryTest.PH_CM + "', ph.status, NULL)) AS '" + ObservationQueryTest.PH_CM + "_Status', \n"
				+ "   (SELECT sprop.value FROM stockprop sprop INNER JOIN cvterm spropcvt ON spropcvt.cvterm_id = sprop.type_id WHERE sprop.stock_id = s.stock_id AND spropcvt.name = '" + ObservationQueryTest.STOCK_ID + "') '" + ObservationQueryTest.STOCK_ID + "', \n"
				+ "   (SELECT xprop.value FROM nd_experimentprop xprop INNER JOIN cvterm xpropcvt ON xpropcvt.cvterm_id = xprop.type_id WHERE xprop.nd_experiment_id = nde.nd_experiment_id AND xpropcvt.name = '" + ObservationQueryTest.FACT1 + "') '" + ObservationQueryTest.FACT1 + "', \n"
				+ " 1=1 FROM \n"
				+ "    project p \n"
				+ "        INNER JOIN project_relationship pr ON p.project_id = pr.subject_project_id \n"
				+ "        INNER JOIN nd_experiment nde ON nde.project_id = pr.subject_project_id \n"
				+ "        INNER JOIN nd_geolocation gl ON nde.nd_geolocation_id = gl.nd_geolocation_id \n"
				+ "        INNER JOIN stock s ON s.stock_id = nde.stock_id \n"
				+ "        LEFT JOIN phenotype ph ON nde.nd_experiment_id = ph.nd_experiment_id \n"
				+ "       LEFT JOIN cvterm cvterm_variable ON cvterm_variable.cvterm_id = ph.observable_id"
				+ " WHERE p.project_id = (SELECT  p.project_id FROM project_relationship pr INNER JOIN project p ON p.project_id = pr.subject_project_id WHERE (pr.object_project_id = :studyId AND name LIKE '%PLOTDATA')) \n"
				+ "		AND nde.nd_experiment_id=:experiment_id \n"
				+ " GROUP BY nde.nd_experiment_id ";

	}

}
