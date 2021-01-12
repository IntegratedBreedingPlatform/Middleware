
package org.generationcp.middleware.service.impl.study;

import com.beust.jcommander.internal.Lists;
import org.generationcp.middleware.constant.ColumnLabels;
import org.generationcp.middleware.dao.GermplasmStudySourceDAO;
import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.dao.dms.ExperimentDao;
import org.generationcp.middleware.dao.dms.PhenotypeDao;
import org.generationcp.middleware.dao.dms.ProjectPropertyDao;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.service.api.study.MeasurementVariableDto;
import org.generationcp.middleware.service.api.study.TrialObservationTable;
import org.generationcp.middleware.service.api.study.germplasm.source.GermplasmStudySourceSearchRequest;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;

/**
 * The class <code>StudyServiceImplTest</code> contains tests for the class <code>{@link StudyServiceImpl}</code>.
 *
 * @author Akhil
 */
public class StudyServiceImplTest {

	private static final String FACT1 = "FACT1";

	private static final String STOCK_ID = "STOCK_ID";

	private static final int STUDY_ID = 1234;

	@Mock
	private Session mockSession;

	@Mock
	private SQLQuery mockSqlQuery;

	@Mock
	private HibernateSessionProvider mockSessionProvider;

	@Mock
	private StudyDataManager studyDataManager;

	@Mock
	private StudyMeasurements studyMeasurements;

	@Mock
	private DaoFactory daoFactory;

	@Mock
	private ProjectPropertyDao projectPropertyDao;

	@Mock
	private DmsProjectDao dmsProjectDao;

	@Mock
	private ExperimentDao experimentDao;

	@Mock
	private PhenotypeDao phenotypeDao;

	private StudyServiceImpl studyServiceImpl;

	final List<String> additionalGermplasmDescriptors = Lists.newArrayList(STOCK_ID);

	final List<String> additionalDesignFactors = Lists.newArrayList(FACT1);

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		this.studyServiceImpl = new StudyServiceImpl(this.mockSessionProvider);
		this.studyServiceImpl.setStudyDataManager(this.studyDataManager);
		this.studyServiceImpl.setStudyMeasurements(this.studyMeasurements);
		this.studyServiceImpl.setDaoFactory(this.daoFactory);
		Mockito.when(this.daoFactory.getProjectPropertyDAO()).thenReturn(this.projectPropertyDao);
		Mockito.when(this.daoFactory.getExperimentDao()).thenReturn(this.experimentDao);
		Mockito.when(this.daoFactory.getPhenotypeDAO()).thenReturn(this.phenotypeDao);

		Mockito.when(this.daoFactory.getDmsProjectDAO()).thenReturn(this.dmsProjectDao);
		Mockito.when(this.mockSessionProvider.getSession()).thenReturn(this.mockSession);
		Mockito.when(this.mockSession.createSQLQuery(ArgumentMatchers.anyString())).thenReturn(this.mockSqlQuery);
		Mockito.when(this.mockSqlQuery.addScalar(ArgumentMatchers.anyString())).thenReturn(this.mockSqlQuery);
		Mockito.when(this.studyServiceImpl.getGenericGermplasmDescriptors(StudyServiceImplTest.STUDY_ID))
			.thenReturn(Lists.newArrayList(TermId.GID.name(), ColumnLabels.DESIGNATION.name(), TermId.ENTRY_NO.name(),
				TermId.ENTRY_TYPE.name(), TermId.ENTRY_CODE.name(), TermId.OBS_UNIT_ID.name(), StudyServiceImplTest.STOCK_ID));
		Mockito.when(this.studyServiceImpl.getAdditionalDesignFactors(StudyServiceImplTest.STUDY_ID))
			.thenReturn(Lists.newArrayList(TermId.REP_NO.name(), TermId.PLOT_NO.name(), StudyServiceImplTest.FACT1));

	}

	@Test
	public void testHasMeasurementDataEnteredAssertTrue() {
		Mockito.when(phenotypeDao.hasMeasurementDataEntered(Mockito.anyList(), Mockito.anyInt())).thenReturn(true);

		final List<Integer> ids = Arrays.asList(1000, 1002);
		assertThat(true, is(equalTo(this.studyServiceImpl.hasMeasurementDataEntered(ids, 4))));
	}

	@Test
	public void testHasMeasurementDataEnteredAssertFalse() {
		Mockito.when(phenotypeDao.hasMeasurementDataEntered(Mockito.anyList(), Mockito.anyInt())).thenReturn(false);

		final List<Integer> ids = Arrays.asList(1000, 1002);
		assertThat(false, is(equalTo(this.studyServiceImpl.hasMeasurementDataEntered(ids, 4))));
	}

	@Test
	public void testHasCrossesOrSelections() {
		final GermplasmStudySourceDAO sourceDao = Mockito.mock(GermplasmStudySourceDAO.class);
		Mockito.doReturn(sourceDao).when(this.daoFactory).getGermplasmStudySourceDAO();
		final int studyId = new Random().nextInt();
		this.studyServiceImpl.hasCrossesOrSelections(studyId);
		final ArgumentCaptor<GermplasmStudySourceSearchRequest> captor = ArgumentCaptor.forClass(GermplasmStudySourceSearchRequest.class);
		Mockito.verify(sourceDao).countGermplasmStudySourceList(captor.capture());
		Assert.assertEquals(studyId, captor.getValue().getStudyId());
	}

	@Test
	public void testFindGenericGermplasmDescriptors() {
		final List<String> genericGermplasmFactors = this.studyServiceImpl.getGenericGermplasmDescriptors(StudyServiceImplTest.STUDY_ID);
		Assert.assertEquals(this.additionalGermplasmDescriptors, genericGermplasmFactors);
	}

	@Test
	public void testFindAdditionalDesignFactors() {
		final List<String> genericDesignFactors = this.studyServiceImpl.getAdditionalDesignFactors(StudyServiceImplTest.STUDY_ID);
		Assert.assertEquals(this.additionalDesignFactors, genericDesignFactors);
	}

	@Test
	public void testGetYearFromStudy() {
		Mockito.when(this.studyDataManager.getProjectStartDateByProjectId(ArgumentMatchers.anyInt())).thenReturn("20180404");
		final String year = this.studyServiceImpl.getYearFromStudy(1);
		Assert.assertEquals("2018", year);
	}

	@Test
	public void testGetYearFromStudyNull() {
		Mockito.when(this.studyDataManager.getProjectStartDateByProjectId(ArgumentMatchers.anyInt())).thenReturn(null);
		final String year = this.studyServiceImpl.getYearFromStudy(1);
		Assert.assertNull(year);
	}

	@Test
	public void testGetTrialObservationTable() {
		final List<Object[]> results = new ArrayList<>();
		final Object[] result = {
			1, 1, "Test", 1, "desig", 1, "entry code", "1", "PLOT_NO", "1", 1, 1, "OBS_UNIT_ID", "LOC_NAME", "LOC_ABBR", 1, 1, 1, 1,
			"Study Name", 1};
		results.add(result);
		Mockito.when(this.studyMeasurements
			.getAllStudyDetailsAsTable(ArgumentMatchers.anyInt(), ArgumentMatchers.anyListOf(MeasurementVariableDto.class),
				ArgumentMatchers.anyInt())).thenReturn(results);
		Mockito.when(this.projectPropertyDao.getVariables(1, VariableType.TRAIT.getId()))
			.thenReturn(Arrays.asList(new MeasurementVariableDto(TermId.ALTITUDE.getId(), TermId.ALTITUDE.name())));
		Mockito.when(this.studyDataManager.getProjectStartDateByProjectId(1)).thenReturn("20180821");

		final TrialObservationTable dto = this.studyServiceImpl.getTrialObservationTable(1, 1);
		Mockito.verify(this.studyMeasurements)
			.getAllStudyDetailsAsTable(ArgumentMatchers.anyInt(), ArgumentMatchers.anyListOf(MeasurementVariableDto.class),
				ArgumentMatchers.anyInt());
		Mockito.verify(this.projectPropertyDao).getVariables(1, VariableType.TRAIT.getId());
		Assert.assertNotNull(dto.getHeaderRow());
		Assert.assertEquals("1", dto.getStudyDbId().toString());
		Assert.assertEquals(String.valueOf(TermId.ALTITUDE.getId()), dto.getObservationVariableDbIds().get(0).toString());
		Assert.assertEquals(TermId.ALTITUDE.name(), dto.getObservationVariableNames().get(0));
		final List<String> tableResults = dto.getData().get(0);
		Assert.assertEquals("2018", tableResults.get(0));
		Assert.assertEquals("1", tableResults.get(1));
		Assert.assertEquals("Study Name Environment Number 1", tableResults.get(2));
		Assert.assertEquals("1", tableResults.get(3));
		Assert.assertEquals("LOC_ABBR", tableResults.get(4));
		Assert.assertEquals("1", tableResults.get(5));
		Assert.assertEquals("desig", tableResults.get(6));
		Assert.assertEquals("1", tableResults.get(7));
		Assert.assertEquals("PLOT_NO", tableResults.get(8));
		Assert.assertEquals("1", tableResults.get(9));
		Assert.assertEquals("1", tableResults.get(10));
		Assert.assertEquals("UnknownTimestamp", tableResults.get(11));
		Assert.assertEquals("Test", tableResults.get(12));
		Assert.assertEquals("1", tableResults.get(13));
		Assert.assertEquals("1", tableResults.get(14));
		Assert.assertEquals("OBS_UNIT_ID", tableResults.get(15));
		Assert.assertEquals("1", tableResults.get(16));
	}

	@Test
	public void testGetPlotDatasetId() {
		final Integer plotDatasetId = new Random().nextInt();
		final Integer studyId = new Random().nextInt();
		Mockito.doReturn(Collections.singletonList(new DmsProject(plotDatasetId))).when(this.dmsProjectDao).getDatasetsByTypeForStudy(
			studyId, DatasetTypeEnum.PLOT_DATA.getId());
		Assert.assertEquals(plotDatasetId, this.studyServiceImpl.getPlotDatasetId(studyId));
	}

	@Test
	public void testEnvironmentDatasetId() {
		final Integer envDatasetId = new Random().nextInt();
		final Integer studyId = new Random().nextInt();
		Mockito.doReturn(Collections.singletonList(new DmsProject(envDatasetId))).when(this.dmsProjectDao).getDatasetsByTypeForStudy(
			studyId, DatasetTypeEnum.SUMMARY_DATA.getId());
		Assert.assertEquals(envDatasetId, this.studyServiceImpl.getEnvironmentDatasetId(studyId));
	}
}
