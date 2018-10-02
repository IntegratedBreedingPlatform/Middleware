package org.generationcp.middleware.dao.dms;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.service.api.DataImportService;
import org.generationcp.middleware.service.api.phenotype.PhenotypeSearchDTO;
import org.generationcp.middleware.service.api.phenotype.PhenotypeSearchRequestDTO;
import org.generationcp.middleware.service.impl.study.PhenotypeQuery;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.type.PrimitiveType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class PhenotypeDaoTest {

	public static final int CURRENT_IBDB_USER_ID = 1;
	private PhenotypeDao dao;

	@Mock
	private DataImportService dataImportService;

	@Mock
	private StudyDataManager studyDataManager;

	@Mock
	private Session session;
	
	@Mock
	private SQLQuery query;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		this.dao = new PhenotypeDao();
		this.dao.setSession(this.session);
		
		Mockito.when(this.query.addScalar(Matchers.anyString())).thenReturn(this.query);
		Mockito.when(this.query.addScalar(Matchers.anyString(), Matchers.any(PrimitiveType.class))).thenReturn(this.query);
		Mockito.when(this.session.createSQLQuery(Matchers.anyString())).thenReturn(this.query);
	}

	@Test
	public void testSearchPhenotypes() {
		final PhenotypeSearchRequestDTO request = new PhenotypeSearchRequestDTO();
		request.setPage(0);
		request.setPageSize(10);
		final String studyDbId = "1";
		final List<String> studyIds = Arrays.asList(studyDbId);
		request.setStudyDbIds(studyIds);
		final List<String> termIds = Arrays.asList("111", "222");
		request.setCvTermIds(termIds);

		// Headers (Observation units)
		final List<Object[]> searchPhenotypeMockResults = this.getSearchPhenotypeMockResults();
		Mockito.when(this.query.list()).thenReturn(searchPhenotypeMockResults);
	
		// Observations
		final SQLQuery objsQuery = Mockito.mock(SQLQuery.class);
		Mockito.when(this.session.createSQLQuery(PhenotypeQuery.PHENOTYPE_SEARCH_OBSERVATIONS)).thenReturn(objsQuery);
		Mockito.when(objsQuery.addScalar(Matchers.anyString())).thenReturn(objsQuery);
		Mockito.when(objsQuery.addScalar(Matchers.anyString(), Matchers.any(PrimitiveType.class))).thenReturn(objsQuery);
		final List<Object[]> searchPhenotypeObservationMockResults = this.getSearchPhenotypeObservationMockResults();
		Mockito.when(objsQuery.list()).thenReturn(searchPhenotypeObservationMockResults);

		final List<PhenotypeSearchDTO> phenotypeSearchDTOS = this.dao.searchPhenotypes(0, Integer.MAX_VALUE, request);

		final String sql = this.getPhenotypeSearchMainQuery() + " AND gl.nd_geolocation_id in (:studyDbIds) "
				+ " AND exists(SELECT 1 " //
				+ " FROM phenotype ph " //
				+ "   INNER JOIN cvterm cvt ON ph.observable_id = cvt.cvterm_id " //
				+ "   INNER JOIN nd_experiment ndep ON ph.nd_experiment_id = ndep.nd_experiment_id " //
				+ "   INNER JOIN project p ON ndep.project_id = p.project_id AND p.name LIKE '%PLOTDATA'" //
				+ "   INNER JOIN projectprop pp ON pp.project_id = p.project_id " //
				+ "                             AND pp.variable_id = ph.observable_id " //
				+ "                             AND pp.type_id = " + VariableType.TRAIT.getId() //
				+ " WHERE ph.nd_experiment_id = nde.nd_experiment_id AND cvt.cvterm_id in (:cvTermIds))" //
				;

		Mockito.verify(this.session).createSQLQuery(Matchers.eq(sql));
		Mockito.verify(this.query).setParameterList("cvTermIds", termIds);
		Mockito.verify(this.query).setParameterList("studyDbIds", studyIds);
		Assert.assertThat(phenotypeSearchDTOS, is(not(empty())));
		
		Assert.assertThat(phenotypeSearchDTOS.get(0).getObservationUnitDbId(), is(searchPhenotypeMockResults.get(0)[1]));
		Assert.assertThat(phenotypeSearchDTOS.get(0).getObservationUnitName(), is(searchPhenotypeMockResults.get(0)[2]));
		Assert.assertThat(phenotypeSearchDTOS.get(0).getObservationLevel(), is(searchPhenotypeMockResults.get(0)[3]));
		Assert.assertThat(phenotypeSearchDTOS.get(0).getPlantNumber(), is(searchPhenotypeMockResults.get(0)[4]));
		Assert.assertThat(phenotypeSearchDTOS.get(0).getGermplasmDbId(), is(searchPhenotypeMockResults.get(0)[5]));
		Assert.assertThat(phenotypeSearchDTOS.get(0).getGermplasmName(), is(searchPhenotypeMockResults.get(0)[6]));
		Assert.assertThat(phenotypeSearchDTOS.get(0).getStudyDbId(), is(searchPhenotypeMockResults.get(0)[7]));
		Assert.assertThat(phenotypeSearchDTOS.get(0).getStudyName(), is(searchPhenotypeMockResults.get(0)[8]));
		Assert.assertThat(phenotypeSearchDTOS.get(0).getProgramName(), is(searchPhenotypeMockResults.get(0)[9]));
		Assert.assertThat(phenotypeSearchDTOS.get(0).getX(), is(searchPhenotypeMockResults.get(0)[10]));
		Assert.assertThat(phenotypeSearchDTOS.get(0).getY(), is(searchPhenotypeMockResults.get(0)[11]));
		Assert.assertThat(phenotypeSearchDTOS.get(0).getPlotNumber(), is(searchPhenotypeMockResults.get(0)[12]));
		Assert.assertThat(phenotypeSearchDTOS.get(0).getBlockNumber(), is(searchPhenotypeMockResults.get(0)[13]));
		Assert.assertThat(phenotypeSearchDTOS.get(0).getReplicate(), is(searchPhenotypeMockResults.get(0)[14]));
		Assert.assertThat(phenotypeSearchDTOS.get(0).getStudyLocationDbId(), is(searchPhenotypeMockResults.get(0)[17]));
		Assert.assertThat(phenotypeSearchDTOS.get(0).getStudyLocation(), is(searchPhenotypeMockResults.get(0)[18]));
		Assert.assertThat(phenotypeSearchDTOS.get(0).getEntryType(), is(searchPhenotypeMockResults.get(0)[19]));
		Assert.assertThat(phenotypeSearchDTOS.get(0).getEntryNumber(), is(searchPhenotypeMockResults.get(0)[20]));

		Assert.assertThat(phenotypeSearchDTOS.get(1).getObservationUnitDbId(), is(searchPhenotypeMockResults.get(1)[1]));
		Assert.assertThat(phenotypeSearchDTOS.get(1).getObservationUnitName(), is(searchPhenotypeMockResults.get(1)[2]));
		Assert.assertThat(phenotypeSearchDTOS.get(1).getObservationLevel(), is(searchPhenotypeMockResults.get(1)[3]));
		Assert.assertThat(phenotypeSearchDTOS.get(1).getPlantNumber(), is(searchPhenotypeMockResults.get(1)[4]));
		Assert.assertThat(phenotypeSearchDTOS.get(1).getGermplasmDbId(), is(searchPhenotypeMockResults.get(1)[5]));
		Assert.assertThat(phenotypeSearchDTOS.get(1).getGermplasmName(), is(searchPhenotypeMockResults.get(1)[6]));
		Assert.assertThat(phenotypeSearchDTOS.get(1).getStudyDbId(), is(searchPhenotypeMockResults.get(1)[7]));
		Assert.assertThat(phenotypeSearchDTOS.get(1).getStudyName(), is(searchPhenotypeMockResults.get(1)[8]));
		Assert.assertThat(phenotypeSearchDTOS.get(1).getProgramName(), is(searchPhenotypeMockResults.get(1)[9]));
		Assert.assertThat(phenotypeSearchDTOS.get(1).getX(), is(searchPhenotypeMockResults.get(1)[15])); // fieldMapRow
		Assert.assertThat(phenotypeSearchDTOS.get(1).getY(), is(searchPhenotypeMockResults.get(1)[16])); // fieldMapCol
		Assert.assertThat(phenotypeSearchDTOS.get(1).getPlotNumber(), is(searchPhenotypeMockResults.get(1)[12]));
		Assert.assertThat(phenotypeSearchDTOS.get(1).getBlockNumber(), is(searchPhenotypeMockResults.get(1)[13]));
		Assert.assertThat(phenotypeSearchDTOS.get(1).getReplicate(), is(searchPhenotypeMockResults.get(1)[14]));
		Assert.assertThat(phenotypeSearchDTOS.get(1).getStudyLocationDbId(), is(searchPhenotypeMockResults.get(1)[17]));
		Assert.assertThat(phenotypeSearchDTOS.get(1).getStudyLocation(), is(searchPhenotypeMockResults.get(1)[18]));
		Assert.assertThat(phenotypeSearchDTOS.get(1).getEntryType(), is(searchPhenotypeMockResults.get(1)[19]));
		Assert.assertThat(phenotypeSearchDTOS.get(1).getEntryNumber(), is(searchPhenotypeMockResults.get(1)[20]));
	}
	
	@Test
	public void testCountPhenotypes() {
		Mockito.doReturn(new BigInteger("100")).when(this.query).uniqueResult();
		final PhenotypeSearchRequestDTO request = new PhenotypeSearchRequestDTO();
		request.setPage(0);
		request.setPageSize(10);
		final String studyDbId = "1";
		final List<String> studyIds = Arrays.asList(studyDbId);
		request.setStudyDbIds(studyIds);
		final List<String> termIds = Arrays.asList("111", "222");
		request.setCvTermIds(termIds);
		final long count = this.dao.countPhenotypes(request);
		
		final String sql = "SELECT COUNT(1) FROM (" + this.getPhenotypeSearchMainQuery() + " AND exists(SELECT 1 " //
				+ " FROM phenotype ph " //
				+ "   INNER JOIN cvterm cvt ON ph.observable_id = cvt.cvterm_id " //
				+ "   INNER JOIN nd_experiment ndep ON ph.nd_experiment_id = ndep.nd_experiment_id " //
				+ "   INNER JOIN project p ON ndep.project_id = p.project_id AND p.name LIKE '%PLOTDATA'" //
				+ "   INNER JOIN projectprop pp ON pp.project_id = p.project_id " //
				+ "                             AND pp.variable_id = ph.observable_id " //
				+ "                             AND pp.type_id = " + VariableType.TRAIT.getId() //
				+ " WHERE ph.nd_experiment_id = nde.nd_experiment_id AND cvt.cvterm_id in (:cvTermIds))" //
				+ " AND gl.nd_geolocation_id in (:studyDbIds) " 
				+ ") T";
		final ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(this.session).createSQLQuery(sqlCaptor.capture());
		Assert.assertEquals(sql.replace(" ", ""), sqlCaptor.getValue().replace(" ", ""));
		Mockito.verify(this.query).setParameterList("cvTermIds", termIds);
		Mockito.verify(this.query).setParameterList("studyDbIds", studyIds);
		Assert.assertEquals(100L, count);
	}
	
	private String getPhenotypeSearchMainQuery() {
		return " SELECT " //
				+ "  nde.nd_experiment_id AS nd_experiment_id, " //
				+ "  nde.OBS_UNIT_ID AS observationUnitDbId, " //
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
				+ "  (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = nde.nd_experiment_id AND ispcvt.name = 'PLOT_NO') AS plotNumber, " //
				+ "  (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = nde.nd_experiment_id AND ispcvt.name = 'BLOCK_NO') AS blockNumber, " //
				+ "  (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = nde.nd_experiment_id AND ispcvt.name = 'REP_NO') AS replicate, " //
				+ "  (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = nde.nd_experiment_id AND ispcvt.name = 'COL') AS COL, " //
				+ "  (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = nde.nd_experiment_id AND ispcvt.name = 'ROW') AS ROW, " //
				+ "  (SELECT l.locid FROM nd_geolocationprop gp INNER JOIN location l ON l.locid = gp.value WHERE gp.type_id = " + TermId.LOCATION_ID.getId() + " AND gp.nd_geolocation_id = gl.nd_geolocation_id) AS studyLocationDbId, " //
				+ "  (SELECT l.lname FROM nd_geolocationprop gp INNER JOIN location l ON l.locid = gp.value WHERE gp.type_id = " + TermId.LOCATION_ID.getId() + " AND gp.nd_geolocation_id = gl.nd_geolocation_id) AS studyLocation, " //
				+ "  (SELECT iispcvt.definition FROM stockprop isp INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = isp.type_id INNER JOIN cvterm iispcvt ON iispcvt.cvterm_id = isp.value WHERE isp.stock_id = s.stock_id AND ispcvt.name = 'ENTRY_TYPE') AS entryType, " //
				+ "  s.uniquename AS entryNumber " //
				+ " FROM " //
				+ "  project plotdata_project " //
				+ "  INNER JOIN nd_experiment nde ON nde.project_id = plotdata_project.project_id " //
				+ "  INNER JOIN nd_geolocation gl ON nde.nd_geolocation_id = gl.nd_geolocation_id " //
				+ "  INNER JOIN stock s ON s.stock_id = nde.stock_id " //
				+ "  INNER JOIN project_relationship pr ON plotdata_project.project_id = pr.subject_project_id " //
				+ "  INNER JOIN project p ON pr.object_project_id = p.project_id " //
				+ "  INNER JOIN workbench.workbench_project wp ON p.program_uuid = wp.project_uuid " //
				+ "  LEFT JOIN nd_experimentprop FieldMapRow ON FieldMapRow.nd_experiment_id = nde.nd_experiment_id AND FieldMapRow.type_id = " + TermId.FIELDMAP_RANGE.getId() //
				+ "  LEFT JOIN nd_experimentprop FieldMapCol ON FieldMapCol.nd_experiment_id = nde.nd_experiment_id AND FieldMapCol.type_id = " + TermId.FIELDMAP_COLUMN.getId() //
				+ " WHERE plotdata_project.name LIKE '%PLOTDATA' " //
				; //

	}
	
	
	@Test
	public void testGetObservationForTraitOnGermplasms() {
		final List<Integer> traitIds = Arrays.asList(5134, 7645);
		final List<Integer> germplasmIds = Arrays.asList(51, 52, 53, 54, 55);
		final List<Integer> environmentIds = Arrays.asList(1, 2, 3);
		this.dao.getObservationForTraitOnGermplasms(traitIds, germplasmIds, environmentIds);
		
		final String expectedSql = this.getObservationsForTraitMainQuery() + " AND s.dbxref_id IN (:germplasmIds) "
				+ "ORDER BY p.observable_id, s.dbxref_id, e.nd_geolocation_id, p.value ";
		final ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(this.session).createSQLQuery(sqlCaptor.capture());
		Assert.assertEquals(expectedSql, sqlCaptor.getValue());
		Mockito.verify(this.query).setParameterList("traitIds", traitIds);
		Mockito.verify(this.query).setParameterList("germplasmIds", germplasmIds);
		Mockito.verify(this.query).setParameterList("environmentIds", environmentIds);
	}
	
	@Test
	public void testGetObservationForTraits() {
		final List<Integer> traitIds = Arrays.asList(5134, 7645);
		final List<Integer> environmentIds = Arrays.asList(1, 2, 3);
		final int start = 100;
		final int numOfRows = 500;
		this.dao.getObservationForTraits(traitIds, environmentIds, start, numOfRows);
		
		final String expectedSql = this.getObservationsForTraitMainQuery()
				+ "ORDER BY p.observable_id, s.dbxref_id, e.nd_geolocation_id, p.value ";
		final ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(this.session).createSQLQuery(sqlCaptor.capture());
		Assert.assertEquals(expectedSql, sqlCaptor.getValue());
		Mockito.verify(this.query).setParameterList("traitIds", traitIds);
		Mockito.verify(this.query).setParameterList("environmentIds", environmentIds);
		Mockito.verify(this.query).setFirstResult(start);
		Mockito.verify(this.query).setMaxResults(numOfRows);
	}
	
	@Test
	public void testCountObservationForTraits() {
		Mockito.doReturn(new BigInteger("100")).when(this.query).uniqueResult();
		final List<Integer> traitIds = Arrays.asList(5134, 7645);
		final List<Integer> environmentIds = Arrays.asList(1, 2, 3);
		final long count = this.dao.countObservationForTraits(traitIds, environmentIds);
		
		final String expectedSql = "SELECT COUNT(*) " + "FROM nd_experiment e "
				+ "INNER JOIN stock s ON e.stock_id = s.stock_id "
				+ "INNER JOIN phenotype p ON e.nd_experiment_id = p.nd_experiment_id " 
				+ "WHERE e.nd_geolocation_id IN (:environmentIds) "
				+ "AND p.observable_id IN (:traitIds) ";
		final ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(this.session).createSQLQuery(sqlCaptor.capture());
		Assert.assertEquals(expectedSql, sqlCaptor.getValue());
		Mockito.verify(this.query).setParameterList("traitIds", traitIds);
		Mockito.verify(this.query).setParameterList("environmentIds", environmentIds);
		Assert.assertEquals(100L, count);
	}
	
	@Test
	public void testGetNumericTraitInfoList() {
		final List<Integer> traitIds = Arrays.asList(5134, 7645);
		final List<Integer> environmentIds = Arrays.asList(1, 2, 3);
		this.dao.getNumericTraitInfoList(environmentIds, traitIds);
		
		final String expectedSql = "SELECT p.observable_id, " + "COUNT(DISTINCT e.nd_geolocation_id) AS location_count, "
				+ "COUNT(DISTINCT s.dbxref_id) AS germplasm_count, "
				+ "COUNT(DISTINCT e.nd_experiment_id) AS observation_count , "
				+ "IF (MIN(p.value * 1) IS NULL, 0, MIN(p.value * 1))  AS min_value, "
				+ "IF (MAX(p.value * 1) IS NULL, 0, MAX(p.value * 1)) AS max_value " + "FROM phenotype p "
				+ "    INNER JOIN nd_experiment e ON e.nd_experiment_id = p.nd_experiment_id "
				+ "    INNER JOIN stock s ON e.stock_id = s.stock_id " + "WHERE e.nd_geolocation_id IN (:environmentIds) "
				+ "    AND p.observable_id IN (:numericVariableIds) " + "GROUP by p.observable_id ";
		final ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(this.session).createSQLQuery(sqlCaptor.capture());
		Assert.assertEquals(expectedSql, sqlCaptor.getValue());
		Mockito.verify(this.query).setParameterList("numericVariableIds", traitIds);
		Mockito.verify(this.query).setParameterList("environmentIds", environmentIds);
	}
	
	@Test
	public void testGetTraitInfoCountsForTraits() {
		final List<Integer> traitIds = Arrays.asList(5134, 7645);
		final List<Integer> environmentIds = Arrays.asList(1, 2, 3);
		this.dao.getTraitInfoCounts(environmentIds, traitIds);
		
		final String expectedSql = "SELECT p.observable_id, " + "COUNT(DISTINCT e.nd_geolocation_id) AS location_count, "
				+ "COUNT(DISTINCT s.dbxref_id) AS germplasm_count, "
				+ "COUNT(DISTINCT e.nd_experiment_id) AS observation_count " + "FROM phenotype p "
				+ "    INNER JOIN nd_experiment e ON e.nd_experiment_id = p.nd_experiment_id "
				+ "    INNER JOIN stock s ON e.stock_id = s.stock_id " + "WHERE e.nd_geolocation_id IN (:environmentIds) "
				+ "    AND p.observable_id IN (:variableIds) " + "GROUP by p.observable_id ";
		final ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(this.session).createSQLQuery(sqlCaptor.capture());
		Assert.assertEquals(expectedSql, sqlCaptor.getValue());
		Mockito.verify(this.query).setParameterList("variableIds", traitIds);
		Mockito.verify(this.query).setParameterList("environmentIds", environmentIds);
	}
	
	@Test
	public void testGetTraitInfoCounts() {
		final List<Integer> environmentIds = Arrays.asList(1, 2, 3);
		this.dao.getTraitInfoCounts(environmentIds);
		
		final String expectedSql = "SELECT p.observable_id, " + "COUNT(DISTINCT e.nd_geolocation_id) AS location_count, "
				+ "COUNT(DISTINCT s.dbxref_id) AS germplasm_count, "
				+ "COUNT(DISTINCT e.nd_experiment_id) AS observation_count " + "FROM phenotype p "
				+ "    INNER JOIN nd_experiment e ON e.nd_experiment_id = p.nd_experiment_id "
				+ "    INNER JOIN stock s ON e.stock_id = s.stock_id " + "WHERE e.nd_geolocation_id IN (:environmentIds) "
				+ "GROUP by p.observable_id ";
		final ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(this.session).createSQLQuery(sqlCaptor.capture());
		Assert.assertEquals(expectedSql, sqlCaptor.getValue());
		Mockito.verify(this.query).setParameterList("environmentIds", environmentIds);
	}
	
	@Test
	public void testUpdatePhenotypesByProjectIdAndLocationId() {
		final int projectId = 1;
		final int locationId = 2;
		final int stockId = 3;
		final int cvTermId = 5157;
		final String value = "1.5678";
		this.dao.updatePhenotypesByProjectIdAndLocationId(projectId, locationId, stockId, cvTermId, value);
		
		Mockito.verify(this.session).flush();
		final String updateSql = "UPDATE nd_experiment exp "
				+ "INNER JOIN phenotype pheno ON exp.nd_experiment_id = pheno.nd_experiment_id " + "SET pheno.value = '" + value + "'"
				+ " WHERE exp.project_id = " + projectId + " AND exp.nd_geolocation_id = " + locationId + " AND exp.type_id = 1170 "
				+ " AND exp.stock_id = " + stockId + " AND pheno.observable_id = " + cvTermId;
		final ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(this.session).createSQLQuery(sqlCaptor.capture());
		Assert.assertEquals(updateSql, sqlCaptor.getValue());
		Mockito.verify(this.query).executeUpdate();
	}
	
	@Test
	public void testContainsAtLeast2CommonEntriesWithValues_WithDesignationGermplasmFactor() {
		final int projectId = 1;
		final int locationId = 2;
		this.dao.containsAtLeast2CommonEntriesWithValues(projectId, locationId, TermId.DESIG.getId());
		
		final String expectedSql = this.getContainsAtLeast2CommonEntriesQuery(projectId, locationId, "stock.name");
		final ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(this.session).createSQLQuery(sqlCaptor.capture());
		Assert.assertEquals(expectedSql, sqlCaptor.getValue());
	}
	
	@Test
	public void testContainsAtLeast2CommonEntriesWithValues_WithGIDGermplasmFactor() {
		final int projectId = 1;
		final int locationId = 2;
		this.dao.containsAtLeast2CommonEntriesWithValues(projectId, locationId, TermId.GID.getId());
		
		final String expectedSql = this.getContainsAtLeast2CommonEntriesQuery(projectId, locationId, "stock.dbxref_id");
		final ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(this.session).createSQLQuery(sqlCaptor.capture());
		Assert.assertEquals(expectedSql, sqlCaptor.getValue());
	}
	
	@Test
	public void testContainsAtLeast2CommonEntriesWithValues_WithEntryNoGermplasmFactor() {
		final int projectId = 1;
		final int locationId = 2;
		this.dao.containsAtLeast2CommonEntriesWithValues(projectId, locationId, TermId.ENTRY_NO.getId());
		
		final String expectedSql = this.getContainsAtLeast2CommonEntriesQuery(projectId, locationId, "stock.uniquename");
		final ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(this.session).createSQLQuery(sqlCaptor.capture());
		Assert.assertEquals(expectedSql, sqlCaptor.getValue());
	}
	
	@Test
	public void testContainsAtLeast2CommonEntriesWithValues_WithOtherGermplasmFactor() {
		final int projectId = 1;
		final int locationId = 2;
		this.dao.containsAtLeast2CommonEntriesWithValues(projectId, locationId, TermId.CROSS.getId());
		
		final String expectedSql = this.getContainsAtLeast2CommonEntriesQuery(projectId, locationId, "stock.stock_id");
		final ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(this.session).createSQLQuery(sqlCaptor.capture());
		Assert.assertEquals(expectedSql, sqlCaptor.getValue());
	}

	private String getContainsAtLeast2CommonEntriesQuery(final Integer projectId, final Integer locationId, final String germplasmGroupBy) {
		return " SELECT phenotype.observable_id,count(phenotype.observable_id) "
		+ " FROM nd_experiment nd_exp "
		+ " INNER JOIN stock ON nd_exp.stock_id = stock.stock_id "
		+ " LEFT JOIN phenotype  ON nd_exp.nd_experiment_id = phenotype.nd_experiment_id  where nd_exp.project_id = "
		+ projectId + " and nd_exp.nd_geolocation_id = " + locationId
		+ " and ((phenotype.value <> '' and phenotype.value is not null) or "
		+ " (phenotype.cvalue_id <> '' and phenotype.cvalue_id is not null))  group by nd_exp.nd_geolocation_id, "
		+ germplasmGroupBy + " , phenotype.observable_id "
		+ " having count(phenotype.observable_id) >= 2 LIMIT 1 ";
	}
	private String getObservationsForTraitMainQuery() {
		return "SELECT p.observable_id, s.dbxref_id, e.nd_geolocation_id, p.value "
				+ "FROM nd_experiment e "
				+ "INNER JOIN stock s ON e.stock_id = s.stock_id "
				+ "INNER JOIN phenotype p ON e.nd_experiment_id = p.nd_experiment_id " + "WHERE e.nd_geolocation_id IN (:environmentIds) "
				+ "AND p.observable_id IN (:traitIds) ";
	}

	private List<Object[]> getSearchPhenotypeObservationMockResults() {
		return Arrays.asList(new Object[][] {  //
			new Object[] { //
				1, // row[0]
				1, // row[1]
				"", // row[2]
				"observationVariableName1", // row[3]
				"value1", // row[4]
				"" // row[5]
			}, //
			new Object[] { //
				2, // row[0]
				2, // row[1]
				"", // row[2]
				"observationVariableName2", // row[3]
				"value2", // row[4]
				"", // row[5]
			} //
		});
	}

	private List<Object[]> getSearchPhenotypeMockResults() {
		return Arrays.asList(new Object[][] {  //
			new Object[] { //
				1, // row[0]
				"setObservationUnitDbId1", // row[1]
				"observationUnitNameRow1", // row[2]
				"observationLevelRow1", // row[3]
				"plantNumberRow1", // row[4]
				"germplasmDbIdRow1", // row[5]
				"germplasmNameRow1", // row[6]
				"studyDbIdRow1", // row[7]
				"studyNameRow1", // row[8]
				"programNameRow1", // row[9]
				"xRow1", // row[10]
				"yRow1", // row[11]
				"plotNumberRow1", // row[12]
				"blockNumberRow1", // row[13]
				"replicateRow1", // row[14]
				"", // row[15]
				"", // row[16]
				"studyLocationDbIdRow1", // row[17]
				"studyLocationRow1", // row[18]
				"entryTypeRow1", // row[19]
				"entryNumberRow1", // row[20]
			}, //
			new Object[] { //
				2, // row[0]
				"setObservationUnitDbId2", // row[1]
				"observationUnitNameRow2", // row[2]
				"observationLevelRow2", // row[3]
				"plantNumberRow2", // row[4]
				"germplasmDbIdRow2", // row[5]
				"germplasmNameRow2", // row[6]
				"studyDbIdRow2", // row[7]
				"studyNameRow2", // row[8]
				"programNameRow2", // row[9]
				"", // row[10]
				"", // row[11]
				"plotNumberRow2", // row[12]
				"blockNumberRow2", // row[13]
				"replicateRow2", // row[14]
				"fieldMapRow2", // row[15]
				"fieldMapCol2", // row[16]
				"studyLocationDbIdRow2", // row[17]
				"studyLocationRow2", // row[18]
				"entryTypeRow2", // row[19]
				"entryNumberRow2", // row[20]
			} //
		});
	}

}
