
package org.generationcp.middleware.service.impl.study;

import com.beust.jcommander.internal.Lists;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.service.api.study.MeasurementVariableDto;
import org.hibernate.engine.jdbc.internal.BasicFormatterImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * The class <code>QueryTest</code> contains tests for the class <code>{@link ObservationQuery}</code>.
 *
 * @author Akhil
 * @version $Revision: 1.0 $
 * @generatedBy CodePro at 17/04/15 3:08 PM
 */
public class ObservationQueryTest {

	private static final String PH_CM = "PH_cm";
	private static final String STOCK_ID = "STOCK_ID";
	private static final String FACT1 = "FACT1";
	final BasicFormatterImpl formattedSQL = new BasicFormatterImpl();
	final List<String> germplasmDescriptors = Lists.newArrayList(STOCK_ID);
	final List<String> designFactors = Lists.newArrayList(FACT1);
	List<MeasurementVariableDto> traitNames = new LinkedList<>();

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
	 * @generatedBy CodePro at 17/04/15 3:08 PM
	 */
	@Test
	public void testGetAllMeasurementsQueryGeneration() {
		final String result =
			this.observationQuery.getAllObservationsQuery(this.traitNames, this.germplasmDescriptors, this.designFactors, null, null);
		assertEquals("The generated query must match the expected query.", this.formatString(this.expectedQueryForAllMeasurements()),
			this.formatString(result));
	}

	@Test
	public void testGetSingleMeasurementQueryGeneration() {
		final String result =
			this.observationQuery.getSingleObservationQuery(this.traitNames, this.germplasmDescriptors, this.designFactors);
		assertEquals("The generated query must match the expected query.", this.formatString(this.expectedQueryForSingleMeasurement()),
			this.formatString(result));
	}

	@Test
	public void testGetSampleObservationQuery() {
		final String sql = "SELECT \n" + "    nde.nd_experiment_id as nd_experiment_id,\n"
			+ "    (select na.nval from names na where na.gid = s.dbxref_id and na.nstat = 1 limit 1) as preferred_name,\n" + "    ph.value"
			+ " as value, s.dbxref_id as gid"
			+ " FROM \n" + "    project p \n"
			+ "        INNER JOIN nd_experiment nde ON nde.project_id = p.project_id \n"
			+ "        INNER JOIN nd_geolocation gl ON nde.nd_geolocation_id = gl.nd_geolocation_id \n"
			+ "        INNER JOIN stock s ON s.stock_id = nde.stock_id \n"
			+ "        LEFT JOIN phenotype ph ON nde.nd_experiment_id = ph.nd_experiment_id \n"
			+ "        LEFT JOIN cvterm cvterm_variable ON cvterm_variable.cvterm_id = ph.observable_id \n"
			+ " WHERE \n"
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
			+ "    (SELECT iispcvt.definition FROM stockprop isp INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = isp.type_id INNER JOIN cvterm iispcvt ON iispcvt.cvterm_id = isp.cvalue_id WHERE isp.stock_id = s.stock_id AND ispcvt.name = 'ENTRY_TYPE') ENTRY_TYPE, \n"
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
			+ " MAX(IF(cvterm_variable.name = '" + ObservationQueryTest.PH_CM + "', ph.value, NULL)) AS '" + ObservationQueryTest.PH_CM
			+ "', \n"
			+ " MAX(IF(cvterm_variable.name = '" + ObservationQueryTest.PH_CM + "', ph.phenotype_id, NULL)) AS '"
			+ ObservationQueryTest.PH_CM + "_PhenotypeId', \n"
			+ " MAX(IF(cvterm_variable.name = '" + ObservationQueryTest.PH_CM + "', ph.status, NULL)) AS '" + ObservationQueryTest.PH_CM
			+ "_Status', \n"
			+ "   (SELECT sprop.value FROM stockprop sprop INNER JOIN cvterm spropcvt ON spropcvt.cvterm_id = sprop.type_id WHERE sprop.stock_id = s.stock_id AND spropcvt.name = '"
			+ ObservationQueryTest.STOCK_ID + "') '" + ObservationQueryTest.STOCK_ID + "', \n"
			+ "   (SELECT xprop.value FROM nd_experimentprop xprop INNER JOIN cvterm xpropcvt ON xpropcvt.cvterm_id = xprop.type_id WHERE xprop.nd_experiment_id = nde.nd_experiment_id AND xpropcvt.name = '"
			+ ObservationQueryTest.FACT1 + "') '" + ObservationQueryTest.FACT1 + "', \n"
			+ " 1=1 FROM \n"
			+ "    project p \n"
			+ "        INNER JOIN nd_experiment nde ON nde.project_id = p.project_id \n"
			+ "        INNER JOIN nd_geolocation gl ON nde.nd_geolocation_id = gl.nd_geolocation_id \n"
			+ "        INNER JOIN stock s ON s.stock_id = nde.stock_id \n"
			+ "        LEFT JOIN phenotype ph ON nde.nd_experiment_id = ph.nd_experiment_id \n"
			+ "       LEFT JOIN cvterm cvterm_variable ON cvterm_variable.cvterm_id = ph.observable_id "
			+ " WHERE p.study_id = :studyId AND p.dataset_type_id = " + DatasetTypeEnum.PLOT_DATA.getId() + " \n"
			+ "		AND gl.nd_geolocation_id = :instanceId \n"
			+ " GROUP BY nde.nd_experiment_id "
			+ " ORDER BY (1*PLOT_NO) asc ";
	}

	private String expectedQueryForSingleMeasurement() {

		return "SELECT \n" + "    nde.nd_experiment_id,\n" + "    gl.description AS TRIAL_INSTANCE,\n"
			+ "    (SELECT iispcvt.definition FROM stockprop isp INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = isp.type_id INNER JOIN cvterm iispcvt ON iispcvt.cvterm_id = isp.cvalue_id WHERE isp.stock_id = s.stock_id AND ispcvt.name = 'ENTRY_TYPE') ENTRY_TYPE, \n"
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
			+ " MAX(IF(cvterm_variable.name = '" + ObservationQueryTest.PH_CM + "', ph.value, NULL)) AS '" + ObservationQueryTest.PH_CM
			+ "', \n"
			+ " MAX(IF(cvterm_variable.name = '" + ObservationQueryTest.PH_CM + "', ph.phenotype_id, NULL)) AS '"
			+ ObservationQueryTest.PH_CM + "_PhenotypeId', \n"
			+ " MAX(IF(cvterm_variable.name = '" + ObservationQueryTest.PH_CM + "', ph.status, NULL)) AS '" + ObservationQueryTest.PH_CM
			+ "_Status', \n"
			+ "   (SELECT sprop.value FROM stockprop sprop INNER JOIN cvterm spropcvt ON spropcvt.cvterm_id = sprop.type_id WHERE sprop.stock_id = s.stock_id AND spropcvt.name = '"
			+ ObservationQueryTest.STOCK_ID + "') '" + ObservationQueryTest.STOCK_ID + "', \n"
			+ "   (SELECT xprop.value FROM nd_experimentprop xprop INNER JOIN cvterm xpropcvt ON xpropcvt.cvterm_id = xprop.type_id WHERE xprop.nd_experiment_id = nde.nd_experiment_id AND xpropcvt.name = '"
			+ ObservationQueryTest.FACT1 + "') '" + ObservationQueryTest.FACT1 + "', \n"
			+ " 1=1 FROM \n"
			+ "    project p \n"
			+ "        INNER JOIN nd_experiment nde ON nde.project_id = p.project_id \n"
			+ "        INNER JOIN nd_geolocation gl ON nde.nd_geolocation_id = gl.nd_geolocation_id \n"
			+ "        INNER JOIN stock s ON s.stock_id = nde.stock_id \n"
			+ "        LEFT JOIN phenotype ph ON nde.nd_experiment_id = ph.nd_experiment_id \n"
			+ "       LEFT JOIN cvterm cvterm_variable ON cvterm_variable.cvterm_id = ph.observable_id"
			+ " WHERE p.study_id = :studyId AND p.dataset_type_id = " + DatasetTypeEnum.PLOT_DATA.getId() + " \n"
			+ "		AND nde.nd_experiment_id=:experiment_id \n"
			+ " GROUP BY nde.nd_experiment_id ";

	}

	@Test
	public void testGgetObservationQueryWithBlockRowCol() {
		assertEquals(this.formatString(this.getRowColumnQuery()),
			this.formatString(this.observationQuery.getObservationQueryWithBlockRowCol(new ArrayList<>(), null)));
	}

	private String getRowColumnQuery() {
		return
			" SELECT    nde.nd_experiment_id,    gl.description AS TRIAL_INSTANCE,    proj.name AS PROJECT_NAME,    gl.nd_geolocation_id,    (SELECT iispcvt.definition  FROM       stockprop isp  INNER JOIN       cvterm ispcvt ON ispcvt.cvterm_id = isp.type_id  INNER JOIN       cvterm iispcvt ON iispcvt.cvterm_id = isp.cvalue_id  WHERE       isp.stock_id = s.stock_id       AND ispcvt.name = 'ENTRY_TYPE') AS ENTRY_TYPE,    g.germplsm_uuid AS GERMPLSM_UUID,    "
				+ "s.name AS DESIGNATION,    "
				+ "s.uniquename AS ENTRY_NO,    "
				+ "   (SELECT isp.value FROM stockprop isp "
				+ "              INNER JOIN cvterm ispcvt1 ON ispcvt1.cvterm_id = isp.type_id "
				+ "     WHERE isp.stock_id = s.stock_id AND ispcvt1.name = 'ENTRY_CODE' ) AS ENTRY_CODE, "
				+ "(SELECT isp.value  FROM       stockprop isp  INNER JOIN       cvterm ispcvt1 ON ispcvt1.cvterm_id = isp.type_id  WHERE       isp.stock_id = s.stock_id       AND ispcvt1.name = 'SEED_SOURCE') AS SEED_SOURCE,    "
				+ "(SELECT ndep.value  FROM       nd_experimentprop ndep  INNER JOIN       cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id  WHERE       ndep.nd_experiment_id = nde.nd_experiment_id       AND ispcvt.name = 'REP_NO') AS REP_NO,    "
				+ "(SELECT ndep.value  \tFROM       nd_experimentprop ndep  \tINNER JOIN       cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id  \tWHERE       ndep.nd_experiment_id = nde.nd_experiment_id       AND ispcvt.name = 'PLOT_NO') AS PLOT_NO,    "
				+ "nde.obs_unit_id AS OBS_UNIT_ID,    "
				+ "(SELECT ndep.value \t\tFROM nd_experimentprop ndep      INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id \t\tWHERE ndep.nd_experiment_id = nde.nd_experiment_id \t\tAND ispcvt.name = 'BLOCK_NO') AS BLOCK_NO, "
				+ "(SELECT (CASE WHEN(SELECT COUNT(*) FROM nd_experimentpropprop INNER JOIN cvterm_relationshipcrelprop ON crelprop.subject_id=prop.type_id AND crelprop.type_id=1200 AND crelprop.object_id=2170  WHERE prop.nd_experiment_id = nde.nd_experiment_id ) > 1THEN 'TBD' ELSE (SELECT (\t\t\t\tCASE WHEN scaletype.object_id = 1130\t\t\t\tTHEN (SELECT val.name from cvterm val WHERE val.cvterm_id = ndep.value) \t\t\t\tELSE ndep.value END\t\t\t\t)\t\tFROM  nd_experimentprop ndep      INNER JOIN  cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id \t\tINNER JOIN  cvterm_relationship crelprop ON crelprop.subject_id = ispcvt.cvterm_id AND crelprop.type_id = 1200 AND crelprop.object_id=2170      LEFT JOIN (SELECT scale.object_id as object_id, relation.subject_id as subject_id FROM cvterm_relationship relation INNER JOIN cvterm_relationship scale ON scale.subject_id = relation.object_id AND scale.type_id = 1105 WHERE relation.type_id = 1220 ) scaletype ON scaletype.subject_id = ispcvt.cvterm_id      WHERE ndep.nd_experiment_id = nde.nd_experiment_id) END) ) ROW, \t  "
				+ "(SELECT (CASE WHEN(SELECT COUNT(*) FROM nd_experimentpropprop INNER JOIN cvterm_relationshipcrelprop ON crelprop.subject_id=prop.type_id AND crelprop.type_id=1200 AND crelprop.object_id=2180  WHERE prop.nd_experiment_id = nde.nd_experiment_id ) > 1THEN 'TBD' ELSE (SELECT  (\t\t\t\tCASE WHEN scaletype.object_id = 1130\t\t\t\tTHEN (SELECT val.name from cvterm val WHERE val.cvterm_id = ndep.value) \t\t\t\tELSE ndep.value END\t\t\t\t)\t\tFROM    nd_experimentprop ndep   INNER JOIN  cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id \t  INNER JOIN  cvterm_relationship crelprop ON crelprop.subject_id = ispcvt.cvterm_id AND crelprop.type_id = 1200 AND crelprop.object_id = 2180   LEFT JOIN (SELECT scale.object_id as object_id, relation.subject_id as subject_id FROM cvterm_relationship relation INNER JOIN cvterm_relationship scale ON scale.subject_id = relation.object_id AND scale.type_id = 1105 WHERE relation.type_id = 1220 ) scaletype ON scaletype.subject_id = ispcvt.cvterm_id    WHERE ndep.nd_experiment_id = nde.nd_experiment_id) END) ) COL, \t "
				+ "(SELECT l.locid   \tFROM nd_geolocationprop gp      INNER JOIN location l ON l.locid = gp.value  \t\tWHERE  gp.type_id = 8190     AND gp.nd_geolocation_id = gl.nd_geolocation_id) AS locationDbId, "
				+ "(SELECT l.lname \tFROM nd_geolocationprop gp \tINNER JOIN location l ON l.locid = gp.value \tWHERE gp.type_id = 8190 AND gp.nd_geolocation_id = gl.nd_geolocation_id) AS LocationName, "
				+ "(SELECT  gp.value   FROM nd_geolocationprop gp  WHERE gp.type_id = 8189 AND gp.nd_geolocation_id = gl.nd_geolocation_id) AS LocationAbbreviation, "
				+ "FieldMapCol.value AS FieldMapColumn, "
				+ "FieldMapRow.value AS FieldMapRow,  1=1  FROM Project p     INNER JOIN project proj ON proj.project_id =  p.study_id     INNER JOIN nd_experiment nde ON nde.project_id = p.project_id     INNER JOIN nd_geolocation gl ON nde.nd_geolocation_id = gl.nd_geolocation_id     INNER JOIN stock s ON s.stock_id = nde.stock_id     INNER JOIN germplsm g ON g.gid = s.dbxref_id \t   LEFT JOIN phenotype ph ON nde.nd_experiment_id = ph.nd_experiment_id \t   LEFT JOIN cvterm cvterm_variable ON cvterm_variable.cvterm_id = ph.observable_id     LEFT JOIN nd_experimentprop FieldMapRow ON FieldMapRow.nd_experiment_id = nde.nd_experiment_id AND FieldMapRow.type_id = 8410    LEFT JOIN nd_experimentprop FieldMapCol ON FieldMapCol.nd_experiment_id = nde.nd_experiment_id AND FieldMapCol.type_id = 8400 WHERE p.study_id = :projectId AND p.dataset_type_id = 4 \n"
				+ " GROUP BY nde.nd_experiment_id ";
	}

}
