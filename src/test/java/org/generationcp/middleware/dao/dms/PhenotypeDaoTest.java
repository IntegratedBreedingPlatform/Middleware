package org.generationcp.middleware.dao.dms;

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
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;

public class PhenotypeDaoTest {

	public static final int CURRENT_IBDB_USER_ID = 1;
	private PhenotypeDao dao;

	@Mock
	private DataImportService dataImportService;

	@Mock
	private StudyDataManager studyDataManager;

	private Session session;

	@Before
	public void setUp() throws Exception {
		this.dao = new PhenotypeDao();
		session = Mockito.mock(Session.class);
		this.dao.setSession(session);
	}

	@Test
	public void testSearchPhenotypes() {
		final PhenotypeSearchRequestDTO request = new PhenotypeSearchRequestDTO();
		request.setPage(0);
		request.setPageSize(10);
		String studyDbId = "1";
		request.setStudyDbIds(Arrays.asList(new String[] {studyDbId}));

		final StringBuilder phenotypeSearchQueryStr = new StringBuilder(PhenotypeQuery.PHENOTYPE_SEARCH);
		phenotypeSearchQueryStr.append(PhenotypeQuery.PHENOTYPE_SEARCH_STUDY_DB_ID_FILTER);

		// Headers (Observation units)
		final SQLQuery query = Mockito.mock(SQLQuery.class);
		List<Object[]> searchPhenotypeMockResults = getSearchPhenotypeMockResults();
		Mockito.when(query.list()).thenReturn(searchPhenotypeMockResults);
		Mockito.when(query.addScalar(Matchers.anyString())).thenReturn(query);
		Mockito.when(query.addScalar(Matchers.anyString(), Matchers.any(PrimitiveType.class))).thenReturn(query);
		Mockito.when(this.session.createSQLQuery(phenotypeSearchQueryStr.toString())).thenReturn(query);

		// Observations
		final SQLQuery objsQuery = Mockito.mock(SQLQuery.class);
		Mockito.when(this.session.createSQLQuery(PhenotypeQuery.PHENOTYPE_SEARCH_OBSERVATIONS)).thenReturn(objsQuery);
		Mockito.when(objsQuery.addScalar(Matchers.anyString())).thenReturn(objsQuery);
		Mockito.when(objsQuery.addScalar(Matchers.anyString(), Matchers.any(PrimitiveType.class))).thenReturn(objsQuery);
		List<Object[]> searchPhenotypeObservationMockResults = getSearchPhenotypeObservationMockResults();
		Mockito.when(objsQuery.list()).thenReturn(searchPhenotypeObservationMockResults);

		List<PhenotypeSearchDTO> phenotypeSearchDTOS = this.dao.searchPhenotypes(0, Integer.MAX_VALUE, request);

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
