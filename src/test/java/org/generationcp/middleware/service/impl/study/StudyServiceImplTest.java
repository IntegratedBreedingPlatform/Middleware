
package org.generationcp.middleware.service.impl.study;

import com.beust.jcommander.internal.Lists;
import org.generationcp.middleware.constant.ColumnLabels;
import org.generationcp.middleware.dao.GermplasmListDAO;
import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.dao.dms.ProjectPropertyDao;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.service.api.study.MeasurementDto;
import org.generationcp.middleware.service.api.study.MeasurementVariableDto;
import org.generationcp.middleware.service.api.study.MeasurementVariableService;
import org.generationcp.middleware.service.api.study.ObservationDto;
import org.generationcp.middleware.service.api.study.StudyGermplasmListService;
import org.generationcp.middleware.service.api.study.StudySearchParameters;
import org.generationcp.middleware.service.api.study.StudySummary;
import org.generationcp.middleware.service.api.study.TrialObservationTable;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
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
	private MeasurementVariableService measurementVariableService;

	@Mock
	private DaoFactory daoFactory;

	@Mock
	private ProjectPropertyDao projectPropertyDao;

	@Mock
	private DmsProjectDao dmsProjectDao;

	private StudyServiceImpl studyServiceImpl;

	final List<String> additionalGermplasmDescriptors = Lists.newArrayList(STOCK_ID);

	final List<String> additionalDesignFactors = Lists.newArrayList(FACT1);

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		this.studyServiceImpl = new StudyServiceImpl(this.mockSessionProvider);
		this.studyServiceImpl.setStudyDataManager(this.studyDataManager);
		this.studyServiceImpl.setMeasurementVariableService(this.measurementVariableService);
		this.studyServiceImpl.setStudyMeasurements(this.studyMeasurements);
		this.studyServiceImpl.setDaoFactory(this.daoFactory);
		Mockito.when(this.daoFactory.getProjectPropertyDAO()).thenReturn(this.projectPropertyDao);
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
	public void testHasMeasurementDataOnEnvironmentAssertTrue() {
		Mockito.when(this.mockSqlQuery.uniqueResult()).thenReturn(1);
		Mockito.when(
			this.mockSessionProvider.getSession().createSQLQuery(StudyServiceImpl.SQL_FOR_COUNT_TOTAL_OBSERVATION_UNITS_NO_NULL_VALUES))
			.thenReturn(this.mockSqlQuery);

		Assert.assertTrue(this.studyServiceImpl.hasMeasurementDataOnInstance(123, 4));
	}

	@Test
	public void testHasMeasurementDataOnEnvironmentAssertFalse() {
		Mockito.when(this.mockSqlQuery.uniqueResult()).thenReturn(0);
		Mockito.when(
			this.mockSessionProvider.getSession().createSQLQuery(StudyServiceImpl.SQL_FOR_COUNT_TOTAL_OBSERVATION_UNITS_NO_NULL_VALUES))
			.thenReturn(this.mockSqlQuery);

		Assert.assertFalse(this.studyServiceImpl.hasMeasurementDataOnInstance(123, 4));
	}

	@Test
	public void testHasMeasurementDataEnteredAssertTrue() {
		final Object[] testDBRow = {2503, 51547, "AleuCol_E_1to5", 43};
		final List<Object[]> testResult = Collections.singletonList(testDBRow);
		Mockito.when(this.mockSqlQuery.list()).thenReturn(testResult);

		Mockito.when(this.mockSessionProvider.getSession().createSQLQuery(StudyServiceImpl.SQL_FOR_HAS_MEASUREMENT_DATA_ENTERED))
			.thenReturn(this.mockSqlQuery);

		final List<Integer> ids = Arrays.asList(1000, 1002);
		assertThat(true, is(equalTo(this.studyServiceImpl.hasMeasurementDataEntered(ids, 4))));
	}

	@Test
	public void testHasMeasurementDataEnteredAssertFalse() {
		final List<Object[]> testResult = Collections.emptyList();

		Mockito.when(this.mockSqlQuery.list()).thenReturn(testResult);
		Mockito.when(this.mockSessionProvider.getSession().createSQLQuery(StudyServiceImpl.SQL_FOR_HAS_MEASUREMENT_DATA_ENTERED))
			.thenReturn(this.mockSqlQuery);

		final List<Integer> ids = Arrays.asList(1000, 1002);
		assertThat(false, is(equalTo(this.studyServiceImpl.hasMeasurementDataEntered(ids, 4))));
	}

	@Test
	public void testHasAdvancedOrCrossesList() {
		final GermplasmListDAO listDao = Mockito.mock(GermplasmListDAO.class);
		Mockito.doReturn(listDao).when(this.daoFactory).getGermplasmListDAO();
		final int studyId = new Random().nextInt();
		this.studyServiceImpl.hasAdvancedOrCrossesList(studyId);
		Mockito.verify(listDao).hasAdvancedOrCrossesList(studyId);
	}

	/**
	 * Run the StudyServiceImpl(HibernateSessionProvider) constructor test.
	 */
	@Test
	public void testGetObservations() {
		final MeasurementVariableService mockTraits = Mockito.mock(MeasurementVariableService.class);
		final StudyMeasurements mockMeasurements = Mockito.mock(StudyMeasurements.class);
		final StudyGermplasmListService mockStudyGermplasmListService = Mockito.mock(StudyGermplasmListService.class);

		final StudyServiceImpl studyServiceImpl = new StudyServiceImpl(mockTraits, mockMeasurements, mockStudyGermplasmListService);
		studyServiceImpl.setDaoFactory(this.daoFactory);

		final List<MeasurementVariableDto> projectTraits =
			Arrays.<MeasurementVariableDto>asList(new MeasurementVariableDto(1, "Trait1"), new MeasurementVariableDto(1, "Trait2"));
		Mockito.when(mockTraits.getVariables(StudyServiceImplTest.STUDY_ID, VariableType.TRAIT.getId(),
			VariableType.SELECTION_METHOD.getId())).thenReturn(projectTraits);
		final List<MeasurementDto> traits = new ArrayList<>();
		traits.add(new MeasurementDto(new MeasurementVariableDto(1, "traitName"), 9999, "traitValue", Phenotype.ValueStatus.OUT_OF_SYNC));
		final ObservationDto measurement = new ObservationDto(1, "trialInstance", "entryType", StudyServiceImplTest.STUDY_ID, "designation",
			"entryNo", "seedSource", "repitionNumber", "plotNumber", "blockNumber", traits);
		final List<ObservationDto> testMeasurements = Collections.<ObservationDto>singletonList(measurement);
		final int instanceId = 1;
		final int pageNumber = 1;
		final int pageSize = 100;
		Mockito.when(mockMeasurements.getAllMeasurements(StudyServiceImplTest.STUDY_ID, projectTraits,
			this.additionalGermplasmDescriptors, this.additionalDesignFactors, instanceId, pageNumber, pageSize, null, null))
			.thenReturn(testMeasurements);

		// Method to test
		final List<ObservationDto> actualMeasurements =
			studyServiceImpl.getObservations(StudyServiceImplTest.STUDY_ID, 1, 1, 100, null, null);

		Assert.assertEquals(testMeasurements, actualMeasurements);
		Mockito.verify(mockMeasurements).getAllMeasurements(StudyServiceImplTest.STUDY_ID, projectTraits,
			this.additionalGermplasmDescriptors, this.additionalDesignFactors, instanceId, pageNumber, pageSize, null, null);
	}

	@Test
	public void testListAllStudies() {
		final StudyTypeDto studyTypeDto = StudyTypeDto.getTrialDto();
		final Object[] testDBRow = {
			2007, "Wheat Study 1", "Wheat Study 1 Title", "c996de54-3ebb-41ca-8fed-160a33ffffd4", studyTypeDto.getId(),
			studyTypeDto.getLabel(), studyTypeDto.getName(), Byte.valueOf("1"), studyTypeDto.getCvTermId(),
			"Wheat Study 1 Objective", "20150417", "20150422", "Mr. Breeder", "Auckland", "Summer"};
		final List<Object[]> testResult = Collections.singletonList(testDBRow);

		Mockito.when(this.mockSqlQuery.list()).thenReturn(testResult);

		final StudySearchParameters searchParameters = new StudySearchParameters();
		searchParameters.setProgramUniqueId("c996de54-3ebb-41ca-8fed-160a33ffffd4");
		final List<StudySummary> studySummaries = this.studyServiceImpl.search(searchParameters);
		Assert.assertNotNull(studySummaries);
		Assert.assertEquals(1, studySummaries.size());

		final StudySummary studySummary = studySummaries.get(0);

		Assert.assertEquals(testDBRow[0], studySummary.getId());
		Assert.assertEquals(testDBRow[1], studySummary.getName());
		Assert.assertEquals(testDBRow[2], studySummary.getTitle());
		Assert.assertEquals(testDBRow[3], studySummary.getProgramUUID());
		Assert.assertEquals(testDBRow[4], studySummary.getType().getId());
		Assert.assertEquals(testDBRow[9], studySummary.getObjective());
		Assert.assertEquals(testDBRow[10], studySummary.getStartDate());
		Assert.assertEquals(testDBRow[11], studySummary.getEndDate());
		Assert.assertEquals(testDBRow[12], studySummary.getPrincipalInvestigator());
		Assert.assertEquals(testDBRow[13], studySummary.getLocation());
		Assert.assertEquals(testDBRow[14], studySummary.getSeason());

	}

	/*@Test
	public void testGetStudyDetailsForANursery() {
		final List<String> seasons = new ArrayList<>();
		seasons.add("WET");
		final StudyMetadata metadata =
			new StudyMetadata(2, 2, 4, Boolean.TRUE, "20160101", "20170101", 8, seasons, "trialName", StudyTypeDto.NURSERY_NAME,
				"studyName", "studyDescription", "Entry list order", "20170101");

		final UserDto user = new UserDto();
		user.setEmail(RandomStringUtils.randomAlphabetic(10) + "@gmail.com");
		user.setFirstName("name");
		user.setLastName("last");
		final UserRoleDto userRoleDto = new UserRoleDto(1,
			new RoleDto(1, "Admin", "",
				"instance", true, true,
				true), null,
			null, null);
		final List<UserRoleDto> userRoleDtos = new ArrayList<>();
		userRoleDtos.add(userRoleDto);
		user.setUserId(1);
		final List<UserDto> users = new ArrayList<>();
		users.add(user);

		final Map<String, String> properties = new HashMap<>();
		properties.put("p1", "v1");

		Mockito.when(this.studyDataManager.getStudyMetadataForGeolocationId(metadata.getStudyDbId())).thenReturn(metadata);
		Mockito.when(this.studyDataManager.getUsersAssociatedToStudy(metadata.getNurseryOrTrialId())).thenReturn(users);
		Mockito.when(this.studyDataManager.getProjectPropsAndValuesByStudy(metadata.getNurseryOrTrialId())).thenReturn(properties);

		final StudyDetailsDto studyDetailsDto = this.studyServiceImpl.getStudyDetailsForGeolocation(metadata.getStudyDbId());

		assertThat(studyDetailsDto.getMetadata().getActive(), equalTo(metadata.getActive()));
		assertThat(studyDetailsDto.getMetadata().getEndDate(), equalTo(metadata.getEndDate()));
		assertThat(studyDetailsDto.getMetadata().getLocationId(), equalTo(metadata.getLocationId()));
		assertThat(studyDetailsDto.getMetadata().getNurseryOrTrialId(), equalTo(metadata.getNurseryOrTrialId()));
		assertThat(studyDetailsDto.getMetadata().getSeasons().size(), equalTo(metadata.getSeasons().size()));
		assertThat(studyDetailsDto.getMetadata().getStudyDbId(), equalTo(metadata.getStudyDbId()));
		assertThat(studyDetailsDto.getMetadata().getStudyName(), equalTo(metadata.getStudyName()));
		assertThat(studyDetailsDto.getMetadata().getTrialDbId(), equalTo(metadata.getTrialDbId()));
		assertThat(studyDetailsDto.getMetadata().getStartDate(), equalTo(metadata.getStartDate()));
		assertThat(studyDetailsDto.getMetadata().getTrialName(), equalTo(metadata.getTrialName()));
		assertThat(studyDetailsDto.getMetadata().getStudyType(), equalTo(metadata.getStudyType()));
		assertThat(studyDetailsDto.getAdditionalInfo().size(), equalTo(properties.size()));
		assertThat(studyDetailsDto.getContacts().size(), equalTo(users.size()));

	}*/

	/*@Test
	public void testGetStudyDetailsForAStudy() {
		final List<String> seasons = new ArrayList<>();
		seasons.add("WET");
		final StudyMetadata metadata =
			new StudyMetadata(2, 2, 4, Boolean.TRUE, "20160101", "20170101", 8, seasons, "studyName", StudyTypeDto.TRIAL_NAME,
				"studyName", "studyDescription", "Entry list order", "20170101");

		final UserDto user = new UserDto();
		user.setEmail("a@a.com");
		user.setFirstName("name");
		user.setLastName("last");
		final UserRoleDto userRoleDto = new UserRoleDto(1,
			new RoleDto(1, "Admin", "",
				"instance", true, true,
				true), null,
			null, null);
		final List<UserRoleDto> userRoleDtos = new ArrayList<>();
		userRoleDtos.add(userRoleDto);
		user.setUserId(1);
		final List<UserDto> users1 = new ArrayList<>();
		users1.add(user);

		final List<UserDto> users2 = new ArrayList<>();

		final Map<String, String> properties1 = new HashMap<>();
		properties1.put("p1", "v1");

		final Map<String, String> properties2 = new HashMap<>();
		properties2.put("p2", "v2");

		Mockito.when(this.studyDataManager.getUsersAssociatedToStudy(metadata.getStudyDbId())).thenReturn(users1);
		Mockito.when(this.studyDataManager.getUsersForEnvironment(metadata.getStudyDbId())).thenReturn(users2);
		Mockito.when(this.studyDataManager.getStudyMetadataForGeolocationId(metadata.getStudyDbId())).thenReturn(metadata);
		Mockito.when(this.studyDataManager.getProjectPropsAndValuesByStudy(metadata.getNurseryOrTrialId())).thenReturn(properties1);

		final StudyDetailsDto studyDetailsDto = this.studyServiceImpl.getStudyDetailsForGeolocation(metadata.getStudyDbId());

		assertThat(studyDetailsDto.getMetadata().getActive(), equalTo(metadata.getActive()));
		assertThat(studyDetailsDto.getMetadata().getEndDate(), equalTo(metadata.getEndDate()));
		assertThat(studyDetailsDto.getMetadata().getLocationId(), equalTo(metadata.getLocationId()));
		assertThat(studyDetailsDto.getMetadata().getNurseryOrTrialId(), equalTo(metadata.getNurseryOrTrialId()));
		assertThat(studyDetailsDto.getMetadata().getSeasons().size(), equalTo(metadata.getSeasons().size()));
		assertThat(studyDetailsDto.getMetadata().getStudyDbId(), equalTo(metadata.getStudyDbId()));
		assertThat(studyDetailsDto.getMetadata().getStudyName(), equalTo(metadata.getStudyName()));
		assertThat(studyDetailsDto.getMetadata().getTrialDbId(), equalTo(metadata.getTrialDbId()));
		assertThat(studyDetailsDto.getMetadata().getStartDate(), equalTo(metadata.getStartDate()));
		assertThat(studyDetailsDto.getMetadata().getTrialName(), equalTo(metadata.getTrialName()));
		assertThat(studyDetailsDto.getMetadata().getStudyType(), equalTo(metadata.getStudyType()));
		assertThat(studyDetailsDto.getAdditionalInfo().size(), equalTo(properties1.size() + properties2.size()));
		assertThat(studyDetailsDto.getContacts().size(), equalTo(users1.size() + users2.size()));

	}*/

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
		Mockito.when(this.measurementVariableService.getVariables(1, VariableType.TRAIT.getId()))
			.thenReturn(Arrays.asList(new MeasurementVariableDto(TermId.ALTITUDE.getId(), TermId.ALTITUDE.name())));
		Mockito.when(this.studyDataManager.getProjectStartDateByProjectId(1)).thenReturn("20180821");

		final TrialObservationTable dto = this.studyServiceImpl.getTrialObservationTable(1, 1);
		Mockito.verify(this.studyMeasurements)
			.getAllStudyDetailsAsTable(ArgumentMatchers.anyInt(), ArgumentMatchers.anyListOf(MeasurementVariableDto.class),
				ArgumentMatchers.anyInt());
		Mockito.verify(this.measurementVariableService).getVariables(1, VariableType.TRAIT.getId());
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
