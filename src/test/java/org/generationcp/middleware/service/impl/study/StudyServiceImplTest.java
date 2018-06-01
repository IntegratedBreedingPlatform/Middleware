
package org.generationcp.middleware.service.impl.study;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.generationcp.middleware.constant.ColumnLabels;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.domain.oms.StudyType;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.UserDataManager;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.service.api.study.MeasurementDto;
import org.generationcp.middleware.service.api.study.MeasurementVariableDto;
import org.generationcp.middleware.service.api.study.MeasurementVariableService;
import org.generationcp.middleware.service.api.study.ObservationDto;
import org.generationcp.middleware.service.api.study.StudyDetailsDto;
import org.generationcp.middleware.service.api.study.StudyGermplasmListService;
import org.generationcp.middleware.service.api.study.StudyMetadata;
import org.generationcp.middleware.service.api.study.StudySearchParameters;
import org.generationcp.middleware.service.api.study.StudySummary;
import org.generationcp.middleware.service.api.user.UserDto;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.beust.jcommander.internal.Lists;

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
	private GermplasmDescriptors germplasmDescriptors;
	
	@Mock
	private DesignFactors designFactors;
	
	@Mock
	private StudyDataManager studyDataManager;
	
	@Mock
	private UserDataManager userDataManager;
	
	private StudyServiceImpl studyServiceImpl;
	
	final List<String> additionalGermplasmDescriptors = Lists.newArrayList(STOCK_ID);
	
	final List<String> additionalDesignFactors = Lists.newArrayList(FACT1);
	

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		this.studyServiceImpl = new StudyServiceImpl(this.mockSessionProvider);
		this.studyServiceImpl.setGermplasmDescriptors(this.germplasmDescriptors);
		this.studyServiceImpl.setDesignFactors(this.designFactors);
		
		this.studyServiceImpl.setStudyDataManager(this.studyDataManager);
		this.studyServiceImpl.setUserDataManager(this.userDataManager);
		
		Mockito.when(this.mockSessionProvider.getSession()).thenReturn(this.mockSession);
		Mockito.when(this.mockSession.createSQLQuery(Matchers.anyString())).thenReturn(this.mockSqlQuery);
		Mockito.when(this.mockSqlQuery.addScalar(Matchers.anyString())).thenReturn(this.mockSqlQuery);
		Mockito.when(this.germplasmDescriptors.find(StudyServiceImplTest.STUDY_ID))
				.thenReturn(Lists.newArrayList(TermId.GID.name(), ColumnLabels.DESIGNATION.name(), TermId.ENTRY_NO.name(),
						TermId.ENTRY_TYPE.name(), TermId.ENTRY_CODE.name(), TermId.PLOT_ID.name(), StudyServiceImplTest.STOCK_ID));
		Mockito.when(this.designFactors.find(StudyServiceImplTest.STUDY_ID))
				.thenReturn(Lists.newArrayList(TermId.REP_NO.name(), TermId.PLOT_NO.name(), StudyServiceImplTest.FACT1));
	}

	@Test
	public void testHasMeasurementDataOnEnvironmentAssertTrue() throws Exception {
		Mockito.when(this.mockSqlQuery.uniqueResult()).thenReturn(1);
		Mockito.when(this.mockSessionProvider.getSession().createSQLQuery(StudyServiceImpl.SQL_FOR_COUNT_TOTAL_OBSERVATION_UNITS_NO_NULL_VALUES))
				.thenReturn(this.mockSqlQuery);

		Assert.assertTrue(this.studyServiceImpl.hasMeasurementDataOnEnvironment(123, 4));
	}

	@Test
	public void testHasMeasurementDataOnEnvironmentAssertFalse() throws Exception {
		Mockito.when(this.mockSqlQuery.uniqueResult()).thenReturn(0);
		Mockito.when(this.mockSessionProvider.getSession().createSQLQuery(StudyServiceImpl.SQL_FOR_COUNT_TOTAL_OBSERVATION_UNITS_NO_NULL_VALUES))
				.thenReturn(this.mockSqlQuery);

		Assert.assertFalse(this.studyServiceImpl.hasMeasurementDataOnEnvironment(123, 4));
	}

	@Test
	public void testHasMeasurementDataEnteredAssertTrue() throws Exception {
		final Object[] testDBRow = {2503,51547, "AleuCol_E_1to5", 43};
		final List<Object[]> testResult = Arrays.<Object[]>asList(testDBRow);
		Mockito.when(this.mockSqlQuery.list()).thenReturn(testResult);

		Mockito.when(this.mockSessionProvider.getSession().createSQLQuery(StudyServiceImpl.SQL_FOR_HAS_MEASUREMENT_DATA_ENTERED))
			.thenReturn(this.mockSqlQuery);

		List<Integer> ids = Arrays.asList(1000,1002);
		assertThat(true, is(equalTo(this.studyServiceImpl.hasMeasurementDataEntered(ids, 4))));
	}

	@Test
	public void testHasMeasurementDataEnteredAssertFalse() throws Exception {
		final List<Object[]> testResult = Arrays.<Object[]>asList();

		Mockito.when(this.mockSqlQuery.list()).thenReturn(testResult);
		Mockito.when(this.mockSessionProvider.getSession().createSQLQuery(StudyServiceImpl.SQL_FOR_HAS_MEASUREMENT_DATA_ENTERED))
			.thenReturn(this.mockSqlQuery);

		List<Integer> ids = Arrays.asList(1000,1002);
		assertThat(false,is(equalTo(this.studyServiceImpl.hasMeasurementDataEntered(ids, 4))));
	}

	/**
	 * Run the StudyServiceImpl(HibernateSessionProvider) constructor test.
	 *
	 */
	@Test
	public void testGetObservations() throws Exception {
		final MeasurementVariableService mockTrialTraits = Mockito.mock(MeasurementVariableService.class);
		final StudyMeasurements mockTrialMeasurements = Mockito.mock(StudyMeasurements.class);
		final StudyGermplasmListService mockStudyGermplasmListService = Mockito.mock(StudyGermplasmListService.class);

		final StudyServiceImpl studyServiceImpl = new StudyServiceImpl(mockTrialTraits, mockTrialMeasurements, mockStudyGermplasmListService,
				this.germplasmDescriptors);
		studyServiceImpl.setDesignFactors(this.designFactors);

		final List<MeasurementVariableDto> projectTraits =
				Arrays.<MeasurementVariableDto>asList(new MeasurementVariableDto(1, "Trait1"), new MeasurementVariableDto(1, "Trait2"));
		Mockito.when(mockTrialTraits.getVariables(StudyServiceImplTest.STUDY_ID, VariableType.TRAIT.getId(),
				VariableType.SELECTION_METHOD.getId())).thenReturn(projectTraits);
		final List<MeasurementDto> traits = new ArrayList<MeasurementDto>();
		traits.add(new MeasurementDto(new MeasurementVariableDto(1, "traitName"), 9999, "traitValue"));
		final ObservationDto measurement = new ObservationDto(1, "trialInstance", "entryType", StudyServiceImplTest.STUDY_ID, "designation",
				"entryNo", "seedSource", "repitionNumber", "plotNumber", "blockNumber", traits);
		final List<ObservationDto> testMeasurements = Collections.<ObservationDto>singletonList(measurement);
		final int instanceId = 1;
		final int pageNumber = 1;
		final int pageSize = 100;
		Mockito.when(mockTrialMeasurements.getAllMeasurements(StudyServiceImplTest.STUDY_ID, projectTraits,
				this.additionalGermplasmDescriptors, this.additionalDesignFactors, instanceId, pageNumber, pageSize, null, null))
				.thenReturn(testMeasurements);

		// Method to test
		final List<ObservationDto> actualMeasurements = studyServiceImpl.getObservations(StudyServiceImplTest.STUDY_ID, 1, 1, 100, null, null);

		Assert.assertEquals(testMeasurements, actualMeasurements);
		Mockito.verify(mockTrialMeasurements).getAllMeasurements(StudyServiceImplTest.STUDY_ID, projectTraits,
				this.additionalGermplasmDescriptors, this.additionalDesignFactors, instanceId, pageNumber, pageSize, null, null);
	}

	@Test
	public void testListAllStudies() throws MiddlewareQueryException {

		final Object[] testDBRow = {2007, "Wheat Trial 1", "Wheat Trial 1 Title", "c996de54-3ebb-41ca-8fed-160a33ffffd4", StudyType.T.getName(),
				"Wheat Trial 1 Objective", "20150417", "20150422", "Mr. Breeder", "Auckland", "Summer"};
		final List<Object[]> testResult = Arrays.<Object[]>asList(testDBRow);

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
		Assert.assertEquals(testDBRow[4], studySummary.getType().getName());
		Assert.assertEquals(testDBRow[5], studySummary.getObjective());
		Assert.assertEquals(testDBRow[6], studySummary.getStartDate());
		Assert.assertEquals(testDBRow[7], studySummary.getEndDate());
		Assert.assertEquals(testDBRow[8], studySummary.getPrincipalInvestigator());
		Assert.assertEquals(testDBRow[9], studySummary.getLocation());
		Assert.assertEquals(testDBRow[10], studySummary.getSeason());

	}

	@Test
	public void testGetStudyInstances() throws Exception {

		final Object[] testDBRow = {12345, "Gujarat, India", "GUJ", 1};
		final List<Object[]> testResult = Arrays.<Object[]>asList(testDBRow);
		Mockito.when(this.mockSqlQuery.list()).thenReturn(testResult);

		final List<StudyInstance> studyInstances = this.studyServiceImpl.getStudyInstances(123);

		Assert.assertTrue(studyInstances.size() == 1);
		Assert.assertEquals(testDBRow[0], studyInstances.get(0).getInstanceDbId());
		Assert.assertEquals(testDBRow[1], studyInstances.get(0).getLocationName());
		Assert.assertEquals(testDBRow[2], studyInstances.get(0).getLocationAbbreviation());
		Assert.assertEquals(testDBRow[3], studyInstances.get(0).getInstanceNumber());
	}

	@Test
	public void testGetStudyDetailsForANursery() {
		List<String> seasons = new ArrayList<>();
		seasons.add("WET");
		final StudyMetadata metadata =
				new StudyMetadata(2, 2, 4, Boolean.TRUE, "20160101", "20170101", 8, seasons, "trialName", StudyType.N.getName(),
					"studyName");

		UserDto user = new UserDto();
		user.setEmail("a@a.com");
		user.setFirstName("name");
		user.setLastName("last");
		user.setRole(new Role(1, "ADMIN"));
		user.setUserId(1);
		List<UserDto> users = new ArrayList<>();
		users.add(user);

		Map<String, String> properties = new HashMap<>();
		properties.put("p1", "v1");

		Mockito.when(this.studyDataManager.getStudyMetadata(metadata.getStudyDbId())).thenReturn(metadata);
		Mockito.when(this.userDataManager.getUsersAssociatedToStudy(metadata.getNurseryOrTrialId())).thenReturn(users);
		Mockito.when(this.studyDataManager.getProjectPropsAndValuesByStudy(metadata.getNurseryOrTrialId())).thenReturn(properties);

		final StudyDetailsDto studyDetailsDto = this.studyServiceImpl.getStudyDetails(metadata.getStudyDbId());

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

	}

	@Test
	public void testGetStudyDetailsForATrial() {
		List<String> seasons = new ArrayList<>();
		seasons.add("WET");
		final StudyMetadata metadata =
				new StudyMetadata(2, 2, 4, Boolean.TRUE, "20160101", "20170101", 8, seasons, "trialName", StudyType.T.getName(),
					"studyName");

		UserDto user = new UserDto();
		user.setEmail("a@a.com");
		user.setFirstName("name");
		user.setLastName("last");
		user.setRole(new Role(1, "ADMIN"));
		user.setUserId(1);
		List<UserDto> users1 = new ArrayList<>();
		users1.add(user);

		List<UserDto> users2 = new ArrayList<>();


		Map<String, String> properties1 = new HashMap<>();
		properties1.put("p1", "v1");

		Map<String, String> properties2 = new HashMap<>();
		properties2.put("p2", "v2");

		Mockito.when(this.studyDataManager.getStudyMetadata(metadata.getStudyDbId())).thenReturn(metadata);
		Mockito.when(this.userDataManager.getUsersAssociatedToStudy(metadata.getNurseryOrTrialId())).thenReturn(users1);
		Mockito.when(this.studyDataManager.getProjectPropsAndValuesByStudy(metadata.getNurseryOrTrialId())).thenReturn(properties1);
		Mockito.when(this.userDataManager.getUsersForEnvironment(metadata.getNurseryOrTrialId())).thenReturn(users2);
		Mockito.when(this.studyDataManager.getGeolocationPropsAndValuesByStudy(metadata.getNurseryOrTrialId())).thenReturn(properties2);


		final StudyDetailsDto studyDetailsDto = this.studyServiceImpl.getStudyDetails(metadata.getStudyDbId());

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

	}
	
	@Test
	public void testFindGenericGermplasmDescriptors() {
		final List<String> genericGermplasmFactors = this.studyServiceImpl.findGenericGermplasmDescriptors(StudyServiceImplTest.STUDY_ID);
		Assert.assertEquals(this.additionalGermplasmDescriptors, genericGermplasmFactors);
	}
	
	@Test
	public void testFindAdditionalDesignFactors() {
		final List<String> genericDesignFactors = this.studyServiceImpl.findAdditionalDesignFactors(StudyServiceImplTest.STUDY_ID);
		Assert.assertEquals(this.additionalDesignFactors, genericDesignFactors);
	}
	
	@Test
	public void testGetYearFromStudy() {
		Mockito.when(this.studyDataManager.getProjectStartDateByProjectId(Matchers.anyInt())).thenReturn("20180404");
		final String year = this.studyServiceImpl.getYearFromStudy(1);
		Assert.assertEquals("2018", year);
	}
	
	@Test
	public void testGetYearFromStudyNull() {
		Mockito.when(this.studyDataManager.getProjectStartDateByProjectId(Matchers.anyInt())).thenReturn(null);
		final String year = this.studyServiceImpl.getYearFromStudy(1);
		Assert.assertNull(year);
	}
}
