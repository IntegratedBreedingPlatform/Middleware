
package org.generationcp.middleware.dao.dms;

import com.google.common.collect.Lists;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.data.initializer.StudyTestDataInitializer;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.dms.StudySearchMatchingOption;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.search.filter.BrowseStudyQueryFilter;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.exceptions.UnpermittedDeletionException;
import org.generationcp.middleware.manager.Season;
import org.generationcp.middleware.manager.StudyDataManagerImpl;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.service.api.FieldbookService;
import org.generationcp.middleware.service.api.study.StudyService;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StudySearchDaoTest extends IntegrationTestBase {

	public static final String TEST_STUDY_SUFFIX = "Test Study Sample";
	public static final String TEST_STUDY_NAME_1 = "1 " + TEST_STUDY_SUFFIX;
	public static final String TEST_STUDY_NAME_2 = "2 " + TEST_STUDY_SUFFIX;
	public static final String TEST_STUDY_NAME_3 = "3 " + TEST_STUDY_SUFFIX;
	public static final String TEST_STUDY_NAME_4 = "4 " + TEST_STUDY_SUFFIX;
	public static final String TEST_STUDY_NAME_5 = "5 " + TEST_STUDY_SUFFIX;
	private static final String PROGRAM_UUID = "700e62d7-09b2-46af-a79c-b19ba4850681";
	private static final int NO_OF_TEST_STUDIES = 5;
	private static final int LUXEMBOURG_COUNTRY_LOCATION_ID = 127;
	private static final int BANGLADESH_COUNTRY_LOCATION_ID = 16;

	private StudySearchDao studySearchDao;

	@Autowired
	private OntologyDataManager ontologyManager;

	@Autowired
	private FieldbookService fieldbookService;

	@Autowired
	private WorkbenchTestDataUtil workbenchTestDataUtil;

	@Autowired
	private StudyService studyService;
	
	@Mock
	private Session mockSession;
	
	private final List<StudyReference> dryStudies = new ArrayList<>();
	private final List<StudyReference> wetStudies = new ArrayList<>();
	

	@Before
	public void init() throws Exception {
		MockitoAnnotations.initMocks(this);

		this.studySearchDao = new StudySearchDao(this.sessionProvder.getSession());

		this.createTestStudies();
	}

	@Test
	public void testGetStudiesByNameNoMatch() {

		final String studyNameSearchKeyword = "TestStudySample";

		final List<StudyReference> studies = this.studySearchDao.getStudiesByName(studyNameSearchKeyword, StudySearchMatchingOption.EXACT_MATCHES, StudySearchDaoTest.PROGRAM_UUID);

		Assert.assertEquals("No studies should be found, study count should be zero.", 0, studies.size());

	}

	@Test
	public void testGetStudiesByNameExactMatches() {

		final String studyNameSearchKeyword = "1 Test Study Sample";

		final List<StudyReference> studies = this.studySearchDao.getStudiesByName(studyNameSearchKeyword, StudySearchMatchingOption.EXACT_MATCHES, StudySearchDaoTest.PROGRAM_UUID);

		Assert.assertEquals("Study count should be one.", 1, studies.size());
		Assert.assertEquals("Searched keyword should exactly match the returned Study name", studyNameSearchKeyword,
				studies.get(0).getName());

	}

	@Test
	public void testGetStudiesByNameMatchesStartingWith() {

		final String studyNameSearchKeyword = "1 Test";

		final List<StudyReference> studies = this.studySearchDao.getStudiesByName(studyNameSearchKeyword, StudySearchMatchingOption.MATCHES_STARTING_WITH, StudySearchDaoTest.PROGRAM_UUID);

		Assert.assertEquals("Study count should be one.", 1, studies.size());
		Assert.assertTrue("The returned Study name should start with " + studyNameSearchKeyword,
				studies.get(0).getName().startsWith(studyNameSearchKeyword));

	}
	
	@Test
	public void testGetStudiesByNameWhenNameIsEmpty() {
		this.studySearchDao.setSession(this.mockSession);
		
		this.studySearchDao.getStudiesByName("", StudySearchMatchingOption.MATCHES_STARTING_WITH, StudySearchDaoTest.PROGRAM_UUID);
		Mockito.verify(this.mockSession, Mockito.never()).createSQLQuery(Matchers.anyString());
	}
	
	@Test
	public void testGetStudiesByNameWhenNameIsNull() {
		this.studySearchDao.setSession(this.mockSession);
		
		this.studySearchDao.getStudiesByName(null, StudySearchMatchingOption.MATCHES_STARTING_WITH, StudySearchDaoTest.PROGRAM_UUID);
		Mockito.verify(this.mockSession, Mockito.never()).createSQLQuery(Matchers.anyString());
	}

	@Test
	public void testGetStudiesByNameMatchesContaining() {

		final List<StudyReference> studies = this.studySearchDao.getStudiesByName(TEST_STUDY_SUFFIX, StudySearchMatchingOption.MATCHES_CONTAINING, StudySearchDaoTest.PROGRAM_UUID);

		Assert.assertEquals("Study count should be " + StudySearchDaoTest.NO_OF_TEST_STUDIES, StudySearchDaoTest.NO_OF_TEST_STUDIES,
				studies.size());

		for (final StudyReference studyReference : studies) {
			Assert.assertTrue("The returned Study name should contain " + TEST_STUDY_SUFFIX,
					studyReference.getName().contains(TEST_STUDY_SUFFIX));
		}
	}
	
	@Test
	public void testGetStudiesByNameExcludingDeletedStudies() throws UnpermittedDeletionException {

		final String studyNameSearchKeyword = "1 Test Study Sample";

		List<StudyReference> studiesByName = this.studySearchDao.getStudiesByName(studyNameSearchKeyword, StudySearchMatchingOption.EXACT_MATCHES, StudySearchDaoTest.PROGRAM_UUID);
		Assert.assertEquals("Study count should be one.", 1, studiesByName.size());

		// Delete test study
		final StudyReference study = studiesByName.get(0);
		this.studyService.deleteStudy(study.getId());
		flush();

		// Check that deleted study is not retrieved
		studiesByName = this.studySearchDao.getStudiesByName(studyNameSearchKeyword, StudySearchMatchingOption.EXACT_MATCHES, StudySearchDaoTest.PROGRAM_UUID);
		Assert.assertEquals("Deleted study should not be returned. ", 0, studiesByName.size());

	}

	@Test
	public void testGetStudiesByLocationIds() {
		final List<StudyReference> studyReferences = this.studySearchDao.getStudiesByLocationIds(
				Lists.newArrayList(StudySearchDaoTest.LUXEMBOURG_COUNTRY_LOCATION_ID), StudySearchDaoTest.PROGRAM_UUID);

		final int locationIds = StudySearchDaoTest.NO_OF_TEST_STUDIES - 1;
		Assert.assertEquals("There should be " + locationIds + " studies that are in Luxembourg",
				locationIds, studyReferences.size());

	}
	
	@Test
	public void testGetStudiesByLocationIdsWhenNoLocationIdsSpecified() {
		this.studySearchDao.setSession(this.mockSession);
		
		this.studySearchDao.getStudiesByLocationIds(new ArrayList<Integer>(), StudySearchDaoTest.PROGRAM_UUID);
		Mockito.verify(this.mockSession, Mockito.never()).createSQLQuery(Matchers.anyString());
	}

	@Test
	public void testGetStudiesByLocationIdsExcludingDeletedStudies() throws UnpermittedDeletionException {
		final ArrayList<Integer> locationIds = Lists.newArrayList(StudySearchDaoTest.LUXEMBOURG_COUNTRY_LOCATION_ID);
		List<StudyReference> studyReferences = this.studySearchDao.getStudiesByLocationIds(locationIds, StudySearchDaoTest.PROGRAM_UUID);
		final Integer previousCount = studyReferences.size();
		Assert.assertEquals("There should be " + StudySearchDaoTest.NO_OF_TEST_STUDIES + " studies that are in Luxembourg",
			StudySearchDaoTest.NO_OF_TEST_STUDIES - 1, studyReferences.size());

		// Delete test study
		final StudyReference studyToDelete = studyReferences.get(0);
		this.studyService.deleteStudy(studyToDelete.getId());
		flush();

		// Check that deleted study is not retrieved
		studyReferences = this.studySearchDao.getStudiesByLocationIds(locationIds, StudySearchDaoTest.PROGRAM_UUID);
		Assert.assertEquals("Deleted study should not be returned. ", previousCount - 1, studyReferences.size());
		for (final StudyReference study : studyReferences) {
			if (studyToDelete.equals(study)) {
				Assert.fail("Expecting deleted study not to be retrieved but was included in returned list.");
			}
		}

	}

	@Test
	public void testGetStudiesBySeason() {
		final List<StudyReference> drySeasonStudyReferences =
				this.studySearchDao.getStudiesBySeason(Season.DRY, StudySearchDaoTest.PROGRAM_UUID);
		final List<StudyReference> wetSeasonStudyReferences =
				this.studySearchDao.getStudiesBySeason(Season.WET, StudySearchDaoTest.PROGRAM_UUID);

		for (final StudyReference study : this.dryStudies) {
			Assert.assertTrue(drySeasonStudyReferences.contains(study));
		}
		
		for (final StudyReference study : this.wetStudies) {
			Assert.assertTrue(wetSeasonStudyReferences.contains(study));
		}

	}

	@Test
	public void testGetStudiesBySeasonExcludingDeletedStudies() throws UnpermittedDeletionException {
		final long previousDrySeasonCount = this.dryStudies.size();
		
		List<StudyReference> drySeasonStudyReferences =
				this.studySearchDao.getStudiesBySeason(Season.DRY, StudySearchDaoTest.PROGRAM_UUID);
		Assert.assertEquals(previousDrySeasonCount, drySeasonStudyReferences.size());

		// Delete test study
		final StudyReference studyToDelete = drySeasonStudyReferences.get(0);
		this.studyService.deleteStudy(studyToDelete.getId());
		flush();

		// Check that deleted study is not retrieved
		drySeasonStudyReferences =
				this.studySearchDao.getStudiesBySeason(Season.DRY, StudySearchDaoTest.PROGRAM_UUID);
		Assert.assertEquals("Deleted study should not be returned. ", previousDrySeasonCount - 1, drySeasonStudyReferences.size());
		for (final StudyReference study : drySeasonStudyReferences) {
			if (studyToDelete.equals(study)) {
				Assert.fail("Expecting deleted study not to be retrieved but was included in returned list.");
			}
		}
	}
	
	@Test
	public void testGetStudiesBySeasonWhenNoSeasonSpecified() {
		this.studySearchDao.setSession(this.mockSession);
		
		this.studySearchDao.getStudiesBySeason(null, StudySearchDaoTest.PROGRAM_UUID);
		Mockito.verify(this.mockSession, Mockito.never()).createSQLQuery(Matchers.anyString());
	}

	@Test
	public void testGetStudiesByStartDate() {

		Assert.assertEquals("There should be 4 studies created in Year 2020", 4,
				this.studySearchDao.getStudiesByStartDate(2020, StudySearchDaoTest.PROGRAM_UUID).size());
		
		Assert.assertEquals("There should be 3 studies created in January 2020", 3,
				this.studySearchDao.getStudiesByStartDate(202001, StudySearchDaoTest.PROGRAM_UUID).size());

		final List<StudyReference> studies =
				this.studySearchDao.getStudiesByStartDate(20201201, StudySearchDaoTest.PROGRAM_UUID);
		Assert.assertEquals("There should be 1 study created in December 1 2020", 1, studies.size());

		Assert.assertEquals(StudySearchDaoTest.TEST_STUDY_NAME_3, studies.get(0).getName());

	}
	
	@Test
	public void testGetStudiesByStartDateWhenNoStartDateSpecified() {
		this.studySearchDao.setSession(this.mockSession);
		
		this.studySearchDao.getStudiesByStartDate(null, StudySearchDaoTest.PROGRAM_UUID);
		Mockito.verify(this.mockSession, Mockito.never()).createSQLQuery(Matchers.anyString());
	}
	
	@Test
	public void testGetStudiesByStartDateExcludingDeletedStudies() throws UnpermittedDeletionException {
		final List<StudyReference> studies = this.studySearchDao.getStudiesByStartDate(2020, StudySearchDaoTest.PROGRAM_UUID);
		
		// Delete test study
		final StudyReference studyToDelete = studies.get(0);
		this.studyService.deleteStudy(studyToDelete.getId());
		flush();
		
		// Check that deleted study is not retrieved
		final List<StudyReference> latestStudies = this.studySearchDao.getStudiesByStartDate(2020, StudySearchDaoTest.PROGRAM_UUID);
		for (final StudyReference study : latestStudies) {
			if (studyToDelete.equals(study)) {
				Assert.fail("Expecting deleted study not to be retrieved but was included in returned list.");
			}
		}
	}
	
	@Test
	public void testSearchStudiesWhenNoSearchFilterSpecified() {
		this.studySearchDao.setSession(this.mockSession);
		
		final BrowseStudyQueryFilter filter = new BrowseStudyQueryFilter();
		this.studySearchDao.searchStudies(filter, new ArrayList<Integer>());
		
		Mockito.verify(this.mockSession, Mockito.never()).createSQLQuery(Matchers.anyString());
	}
	
	@Test
	public void testSearchStudiesWithNameStartDateLocationAndSeasonFilters() {
		final List<Integer> locationIds = Arrays.asList(LUXEMBOURG_COUNTRY_LOCATION_ID);
		final BrowseStudyQueryFilter filter = new BrowseStudyQueryFilter();
		filter.setName(StudySearchDaoTest.TEST_STUDY_SUFFIX);
		filter.setStudySearchMatchingOption(StudySearchMatchingOption.MATCHES_CONTAINING);
		filter.setProgramUUID(StudySearchDaoTest.PROGRAM_UUID);
		filter.setSeason(Season.DRY);
		filter.setStartDate(202001);
		
		final List<StudyReference> matchedStudies = this.studySearchDao.searchStudies(filter, locationIds);
		Assert.assertEquals(1, matchedStudies.size());
	}
	
	@Test
	public void testSearchStudiesWithNameStartDateAndLocationFilters() {
		final List<Integer> locationIds = Arrays.asList(LUXEMBOURG_COUNTRY_LOCATION_ID);
		final BrowseStudyQueryFilter filter = new BrowseStudyQueryFilter();
		filter.setName(StudySearchDaoTest.TEST_STUDY_SUFFIX);
		filter.setStudySearchMatchingOption(StudySearchMatchingOption.MATCHES_CONTAINING);
		filter.setProgramUUID(StudySearchDaoTest.PROGRAM_UUID);
		filter.setStartDate(202001);
		
		final List<StudyReference> matchedStudies = this.studySearchDao.searchStudies(filter, locationIds);
		Assert.assertEquals(2, matchedStudies.size());
	}
	
	@Test
	public void testSearchStudiesWithNameStartDateAndSeasonFilters() {
		final BrowseStudyQueryFilter filter = new BrowseStudyQueryFilter();
		filter.setName(StudySearchDaoTest.TEST_STUDY_SUFFIX);
		filter.setStudySearchMatchingOption(StudySearchMatchingOption.MATCHES_CONTAINING);
		filter.setProgramUUID(StudySearchDaoTest.PROGRAM_UUID);
		filter.setSeason(Season.DRY);
		filter.setStartDate(2020);
		
		final List<StudyReference> matchedStudies = this.studySearchDao.searchStudies(filter, new ArrayList<Integer>());
		Assert.assertEquals(2, matchedStudies.size());
	}
	

	@Test
	public void testSearchStudiesWithNameStartDateFilters() {
		final BrowseStudyQueryFilter filter = new BrowseStudyQueryFilter();
		filter.setName(StudySearchDaoTest.TEST_STUDY_SUFFIX);
		filter.setStudySearchMatchingOption(StudySearchMatchingOption.MATCHES_CONTAINING);
		filter.setProgramUUID(StudySearchDaoTest.PROGRAM_UUID);
		filter.setStartDate(202001);
		
		final List<StudyReference> matchedStudies = this.studySearchDao.searchStudies(filter, new ArrayList<Integer>());
		Assert.assertEquals(3, matchedStudies.size());
	}
	
	@Test
	public void testSearchStudiesWithNameLocationAndSeasonFilters() {
		final List<Integer> locationIds = Arrays.asList(LUXEMBOURG_COUNTRY_LOCATION_ID);
		final BrowseStudyQueryFilter filter = new BrowseStudyQueryFilter();
		filter.setName(StudySearchDaoTest.TEST_STUDY_SUFFIX);
		filter.setStudySearchMatchingOption(StudySearchMatchingOption.MATCHES_CONTAINING);
		filter.setProgramUUID(StudySearchDaoTest.PROGRAM_UUID);
		filter.setSeason(Season.WET);
		
		final List<StudyReference> matchedStudies = this.studySearchDao.searchStudies(filter, locationIds);
		Assert.assertEquals(1, matchedStudies.size());
	}
	
	@Test
	public void testSearchStudiesWithNameAndLocationFilters() {
		final List<Integer> locationIds = Arrays.asList(LUXEMBOURG_COUNTRY_LOCATION_ID);
		final BrowseStudyQueryFilter filter = new BrowseStudyQueryFilter();
		filter.setName(StudySearchDaoTest.TEST_STUDY_SUFFIX);
		filter.setStudySearchMatchingOption(StudySearchMatchingOption.MATCHES_CONTAINING);
		filter.setProgramUUID(StudySearchDaoTest.PROGRAM_UUID);
		
		final List<StudyReference> matchedStudies = this.studySearchDao.searchStudies(filter, locationIds);
		Assert.assertEquals(4, matchedStudies.size());
	}
	
	@Test
	public void testSearchStudiesWithNameAndSeasonFilters() {
		final BrowseStudyQueryFilter filter = new BrowseStudyQueryFilter();
		filter.setName(StudySearchDaoTest.TEST_STUDY_SUFFIX);
		filter.setStudySearchMatchingOption(StudySearchMatchingOption.MATCHES_CONTAINING);
		filter.setProgramUUID(StudySearchDaoTest.PROGRAM_UUID);
		filter.setSeason(Season.DRY);
		
		final List<StudyReference> matchedStudies = this.studySearchDao.searchStudies(filter, new ArrayList<Integer>());
		Assert.assertEquals(this.dryStudies.size(), matchedStudies.size());
	}
	
	@Test
	public void testSearchStudiesWithNameFilter() {
		final BrowseStudyQueryFilter filter = new BrowseStudyQueryFilter();
		filter.setName(StudySearchDaoTest.TEST_STUDY_SUFFIX);
		filter.setStudySearchMatchingOption(StudySearchMatchingOption.MATCHES_CONTAINING);
		filter.setProgramUUID(StudySearchDaoTest.PROGRAM_UUID);
		
		final List<StudyReference> matchedStudies = this.studySearchDao.searchStudies(filter, new ArrayList<Integer>());
		Assert.assertEquals(StudySearchDaoTest.NO_OF_TEST_STUDIES, matchedStudies.size());
	}
	
	@Test
	public void testSearchStudiesWithStartDateLocationAndSeasonFilters() {
		final List<Integer> locationIds = Arrays.asList(LUXEMBOURG_COUNTRY_LOCATION_ID);
		final BrowseStudyQueryFilter filter = new BrowseStudyQueryFilter();
		filter.setProgramUUID(StudySearchDaoTest.PROGRAM_UUID);
		filter.setSeason(Season.DRY);
		filter.setStartDate(202001);
		
		final List<StudyReference> matchedStudies = this.studySearchDao.searchStudies(filter, locationIds);
		Assert.assertEquals(1, matchedStudies.size());
	}
	
	@Test
	public void testSearchStudiesWithStartDateAndLocationFilters() {
		final List<Integer> locationIds = Arrays.asList(LUXEMBOURG_COUNTRY_LOCATION_ID);
		final BrowseStudyQueryFilter filter = new BrowseStudyQueryFilter();
		filter.setProgramUUID(StudySearchDaoTest.PROGRAM_UUID);
		filter.setStartDate(202001);
		
		final List<StudyReference> matchedStudies = this.studySearchDao.searchStudies(filter, locationIds);
		Assert.assertEquals(2, matchedStudies.size());
	}
	
	@Test
	public void testSearchStudiesWithStartDateAndSeasonFilters() {
		final BrowseStudyQueryFilter filter = new BrowseStudyQueryFilter();
		filter.setProgramUUID(StudySearchDaoTest.PROGRAM_UUID);
		filter.setSeason(Season.DRY);
		filter.setStartDate(2020);
		
		final List<StudyReference> matchedStudies = this.studySearchDao.searchStudies(filter, new ArrayList<Integer>());
		Assert.assertEquals(2, matchedStudies.size());
	}
	

	@Test
	public void testSearchStudiesWithStartDateFilter() {
		final BrowseStudyQueryFilter filter = new BrowseStudyQueryFilter();
		filter.setProgramUUID(StudySearchDaoTest.PROGRAM_UUID);
		filter.setStartDate(202001);
		
		final List<StudyReference> matchedStudies = this.studySearchDao.searchStudies(filter, new ArrayList<Integer>());
		Assert.assertEquals(3, matchedStudies.size());
	}
	
	@Test
	public void testSearchStudiesWithLocationAndSeasonFilters() {
		final List<Integer> locationIds = Arrays.asList(LUXEMBOURG_COUNTRY_LOCATION_ID);
		final BrowseStudyQueryFilter filter = new BrowseStudyQueryFilter();
		filter.setProgramUUID(StudySearchDaoTest.PROGRAM_UUID);
		filter.setSeason(Season.WET);
		
		final List<StudyReference> matchedStudies = this.studySearchDao.searchStudies(filter, locationIds);
		Assert.assertEquals(1, matchedStudies.size());
	}
	
	@Test
	public void testSearchStudiesWithLocationFilter() {
		final List<Integer> locationIds = Arrays.asList(LUXEMBOURG_COUNTRY_LOCATION_ID);
		final BrowseStudyQueryFilter filter = new BrowseStudyQueryFilter();
		filter.setName(StudySearchDaoTest.TEST_STUDY_SUFFIX);
		filter.setStudySearchMatchingOption(StudySearchMatchingOption.MATCHES_CONTAINING);
		filter.setProgramUUID(StudySearchDaoTest.PROGRAM_UUID);
		
		final List<StudyReference> matchedStudies = this.studySearchDao.searchStudies(filter, locationIds);
		Assert.assertEquals(4, matchedStudies.size());
	}
	
	@Test
	public void testSearchStudiesWithSeasonFilter() {
		final BrowseStudyQueryFilter filter = new BrowseStudyQueryFilter();
		filter.setName(StudySearchDaoTest.TEST_STUDY_SUFFIX);
		filter.setStudySearchMatchingOption(StudySearchMatchingOption.MATCHES_CONTAINING);
		filter.setProgramUUID(StudySearchDaoTest.PROGRAM_UUID);
		filter.setSeason(Season.DRY);
		
		final List<StudyReference> matchedStudies = this.studySearchDao.searchStudies(filter, new ArrayList<Integer>());
		Assert.assertEquals(this.dryStudies.size(), matchedStudies.size());
	}
	
	private void flush() {
		this.sessionProvder.getSession().flush();
	}

	private void createTestStudies() throws Exception {


		final Project project = workbenchTestDataUtil.createTestProjectData();
		project.setUniqueID(StudySearchDaoTest.PROGRAM_UUID);

		final StudyDataManagerImpl studyDataManager = new StudyDataManagerImpl();
		studyDataManager.setSessionProvider(this.sessionProvder);

		final StudyTestDataInitializer studyTestDataInitializer =
			new StudyTestDataInitializer(studyDataManager, this.ontologyManager, project, this.sessionProvder);

		// First 3 studies have location and season variables at study level
		// We need to add datasets to studies
		final StudyReference studyReference1 = studyTestDataInitializer.addTestStudy(StudySearchDaoTest.TEST_STUDY_NAME_1, StudyTypeDto.getTrialDto(),
				String.valueOf(TermId.SEASON_DRY.getId()), String.valueOf(StudySearchDaoTest.LUXEMBOURG_COUNTRY_LOCATION_ID), "20200101");
		studyTestDataInitializer.addTestDataset(studyReference1.getId());
		this.dryStudies.add(studyReference1);

		final StudyReference studyReference2 = studyTestDataInitializer.addTestStudy(StudySearchDaoTest.TEST_STUDY_NAME_2, StudyTypeDto.getTrialDto(),
				String.valueOf(TermId.SEASON_WET.getId()), String.valueOf(StudySearchDaoTest.LUXEMBOURG_COUNTRY_LOCATION_ID), "20200102");
		studyTestDataInitializer.addTestDataset(studyReference2.getId());
		this.wetStudies.add(studyReference2);

		final StudyReference studyReference3 = studyTestDataInitializer.addTestStudy(StudySearchDaoTest.TEST_STUDY_NAME_3, StudyTypeDto.getTrialDto(),
				String.valueOf(TermId.SEASON_DRY.getId()), String.valueOf(StudySearchDaoTest.LUXEMBOURG_COUNTRY_LOCATION_ID), "20201201");
		studyTestDataInitializer.addTestDataset(studyReference3.getId());
		this.dryStudies.add(studyReference3);

		// This study has season and location variables at environment level
		final CropType crop = new CropType();
		crop.setUseUUID(true);
		final StudyReference studyReference4 =
				studyTestDataInitializer.addTestStudy(StudyTypeDto.getTrialDto(), StudySearchDaoTest.TEST_STUDY_NAME_4);
		studyTestDataInitializer.createEnvironmentDataset(crop, studyReference4.getId(),
				String.valueOf(StudySearchDaoTest.LUXEMBOURG_COUNTRY_LOCATION_ID), String.valueOf(TermId.SEASON_DRY.getId()));
		this.dryStudies.add(studyReference4);
		
		final StudyReference studyReference5 = studyTestDataInitializer.addTestStudy(StudySearchDaoTest.TEST_STUDY_NAME_5, StudyTypeDto.getTrialDto(),
				String.valueOf(TermId.SEASON_WET.getId()), String.valueOf(StudySearchDaoTest.BANGLADESH_COUNTRY_LOCATION_ID), "20200103");
		studyTestDataInitializer.addTestDataset(studyReference5.getId());
		this.wetStudies.add(studyReference5);
	}
	
	
}
