package org.generationcp.middleware.dao.dms;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.data.initializer.StudyTestDataInitializer;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.dms.StudySearchMatchingOption;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.Season;
import org.generationcp.middleware.manager.StudyDataManagerImpl;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class StudySearchDaoTest extends IntegrationTestBase {

	public static final int NO_OF_DRY_SEASON_STUDIES = 2;
	public static final int NO_OF_WET_SEASON_STUDIES = 1;
	public static final String TEST_TRIAL_NAME_1 = "1 Test Trial Sample";
	public static final String TEST_TRIAL_NAME_2 = "2 Test Trial Sample";
	public static final String TEST_TRIAL_NAME_3 = "3 Test Trial Sample";
	private final int NO_OF_TEST_STUDIES = 3;
	private final int LUXEMBOURG_COUNTRY_LOCATION_ID = 127;

	private StudySearchDao studySearchDao;

	@Autowired
	private OntologyDataManager ontologyManager;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private GermplasmDataManager germplasmDataDM;

	@Autowired
	private LocationDataManager locationManager;

	private long numberOfDrySeasonBeforeCreatingTestData = 0;
	private long numberOfWetSeasoBeforeCreatingTestData = 0;

	@Before
	public void init() throws Exception {

		this.studySearchDao = new StudySearchDao();
		this.studySearchDao.setSession(this.sessionProvder.getSession());

		numberOfDrySeasonBeforeCreatingTestData = studySearchDao.countStudiesBySeason(Season.DRY);
		numberOfWetSeasoBeforeCreatingTestData = studySearchDao.countStudiesBySeason(Season.WET);

		this.createTestStudies();

	}

	@Test
	public void testGetStudiesByNameNoMatch() {

		final String studyNameSearchKeyword = "TestTrialSample";

		final List<StudyReference> studies =
				studySearchDao.getStudiesByName(studyNameSearchKeyword, 0, Integer.MAX_VALUE, StudySearchMatchingOption.EXACT_MATCHES);

		Assert.assertEquals("No studies should be found, study count should be zero.", 0, studies.size());

	}

	@Test
	public void testGetStudiesByNameExactMatches() {

		final String studyNameSearchKeyword = "1 Test Trial Sample";

		final List<StudyReference> studies =
				studySearchDao.getStudiesByName(studyNameSearchKeyword, 0, Integer.MAX_VALUE, StudySearchMatchingOption.EXACT_MATCHES);

		Assert.assertEquals("Study count should be one.", 1, studies.size());
		Assert.assertEquals("Searched keyword should exactly match the returned Study name", studyNameSearchKeyword,
				studies.get(0).getName());

	}

	@Test
	public void testGetStudiesByNameMatchesStartingWith() {

		final String studyNameSearchKeyword = "1 Test";

		final List<StudyReference> studies = studySearchDao
				.getStudiesByName(studyNameSearchKeyword, 0, Integer.MAX_VALUE, StudySearchMatchingOption.MATCHES_STARTING_WITH);

		Assert.assertEquals("Study count should be one.", 1, studies.size());
		Assert.assertTrue("The returned Study name should start with " + studyNameSearchKeyword,
				studies.get(0).getName().startsWith(studyNameSearchKeyword));

	}

	@Test
	public void testGetStudiesByNameMatchesContaining() {

		final String studyNameSearchKeyword = "Test Trial Sample";

		final List<StudyReference> studies =
				studySearchDao.getStudiesByName(studyNameSearchKeyword, 0, Integer.MAX_VALUE, StudySearchMatchingOption.MATCHES_CONTAINING);

		Assert.assertEquals("Study count should be " + NO_OF_TEST_STUDIES, NO_OF_TEST_STUDIES, studies.size());

		for (final StudyReference studyReference : studies) {
			Assert.assertTrue("The returned Study name should contain " + studyNameSearchKeyword,
					studyReference.getName().contains(studyNameSearchKeyword));
		}

	}

	@Test
	public void testCountStudiesByNameNoMatch() {

		final String studyNameSearchKeyword = "TestTrialSample";

		Assert.assertEquals("No studies should be found, study count should be zero.", 0,
				studySearchDao.countStudiesByName(studyNameSearchKeyword, StudySearchMatchingOption.EXACT_MATCHES));

	}

	@Test
	public void testCountStudiesByNameExactMatches() {

		final String studyNameSearchKeyword = "1 Test Trial Sample";

		Assert.assertEquals("Study count should be one.", 1,
				studySearchDao.countStudiesByName(studyNameSearchKeyword, StudySearchMatchingOption.EXACT_MATCHES));

	}

	@Test
	public void testCountStudiesByNameMatchesStartingWith() {

		final String studyNameSearchKeyword = "1 Test";

		Assert.assertEquals("Study count should be one.", 1,
				studySearchDao.countStudiesByName(studyNameSearchKeyword, StudySearchMatchingOption.MATCHES_STARTING_WITH));

	}

	@Test
	public void testCountStudiesByNameMatchesContaining() {

		final String studyNameSearchKeyword = "Test Trial Sample";

		Assert.assertEquals("Study count should be " + NO_OF_TEST_STUDIES, NO_OF_TEST_STUDIES,
				studySearchDao.countStudiesByName(studyNameSearchKeyword, StudySearchMatchingOption.MATCHES_CONTAINING));

	}

	@Test
	public void testCountStudiesByLocationIds() {

		final List<Integer> locationIds = new ArrayList<>();
		locationIds.add(LUXEMBOURG_COUNTRY_LOCATION_ID);

		Assert.assertEquals("There should be " + NO_OF_TEST_STUDIES + " studies that are in Luxembourg", NO_OF_TEST_STUDIES,
				studySearchDao.countStudiesByLocationIds(locationIds));

	}

	@Test
	public void testGetStudiesByLocationIds() {

		final List<Integer> locationIds = new ArrayList<>();
		locationIds.add(LUXEMBOURG_COUNTRY_LOCATION_ID);

		final List<StudyReference> studyReferences = studySearchDao.getStudiesByLocationIds(locationIds, 0, Integer.MAX_VALUE);

		Assert.assertEquals("There should be " + NO_OF_TEST_STUDIES + " studies that are in Luxembourg", NO_OF_TEST_STUDIES,
				studyReferences.size());

	}

	@Test
	public void testCountStudiesBySeason() {

		final long expectedActualDrySeasonCount = numberOfDrySeasonBeforeCreatingTestData + NO_OF_DRY_SEASON_STUDIES;
		final long expectedActualWetSeasonCount = numberOfWetSeasoBeforeCreatingTestData + NO_OF_WET_SEASON_STUDIES;

		Assert.assertEquals(expectedActualDrySeasonCount, studySearchDao.countStudiesBySeason(Season.DRY));
		Assert.assertEquals(expectedActualWetSeasonCount, studySearchDao.countStudiesBySeason(Season.WET));

	}

	@Test
	public void testGetStudiesBySeason() {

		final long expectedActualDrySeasonCount = numberOfDrySeasonBeforeCreatingTestData + NO_OF_DRY_SEASON_STUDIES;
		final long expectedActualWetSeasonCount = numberOfWetSeasoBeforeCreatingTestData + NO_OF_WET_SEASON_STUDIES;

		final List<StudyReference> drySeasonStudyReferences = studySearchDao.getStudiesBySeason(Season.DRY, 0, Integer.MAX_VALUE);
		final List<StudyReference> wetSeasonStudyReferences = studySearchDao.getStudiesBySeason(Season.WET, 0, Integer.MAX_VALUE);

		Assert.assertEquals(expectedActualDrySeasonCount, drySeasonStudyReferences.size());
		Assert.assertEquals(expectedActualWetSeasonCount, wetSeasonStudyReferences.size());

		final List<String> drySeasonStudyNames = new ArrayList<>();
		for (final StudyReference studyReference : drySeasonStudyReferences) {
			drySeasonStudyNames.add(studyReference.getName());
		}
		final List<String> wetSeasonStudyNames = new ArrayList<>();
		for (final StudyReference studyReference : wetSeasonStudyReferences) {
			wetSeasonStudyNames.add(studyReference.getName());
		}

		Assert.assertTrue(TEST_TRIAL_NAME_1 + " should be in Dry Season study list", drySeasonStudyNames.contains(TEST_TRIAL_NAME_1));
		Assert.assertTrue(TEST_TRIAL_NAME_3 + " should be in Dry Season study list", drySeasonStudyNames.contains(TEST_TRIAL_NAME_3));
		Assert.assertTrue(TEST_TRIAL_NAME_2 + " should be in Wet Season study list", wetSeasonStudyNames.contains(TEST_TRIAL_NAME_2));

	}

	private void createTestStudies() throws Exception {

		final WorkbenchTestDataUtil workbenchTestDataUtil = new WorkbenchTestDataUtil(this.workbenchDataManager);
		final Project project = workbenchTestDataUtil.createTestProjectData();

		final StudyDataManagerImpl studyDataManager = new StudyDataManagerImpl();

		studyDataManager.setSessionProvider(this.sessionProvder);
		final StudyTestDataInitializer studyTestDataInitializer =
				new StudyTestDataInitializer(studyDataManager, this.ontologyManager, project, this.germplasmDataDM, this.locationManager);

		final StudyReference studyReference1 = studyTestDataInitializer
				.addTestStudy(TEST_TRIAL_NAME_1, StudyType.T, String.valueOf(TermId.SEASON_DRY.getId()),
						String.valueOf(LUXEMBOURG_COUNTRY_LOCATION_ID));
		studyTestDataInitializer.addTestDataset(studyReference1.getId());

		final StudyReference studyReference2 = studyTestDataInitializer
				.addTestStudy(TEST_TRIAL_NAME_2, StudyType.T, String.valueOf(TermId.SEASON_WET.getId()),
						String.valueOf(LUXEMBOURG_COUNTRY_LOCATION_ID));
		studyTestDataInitializer.addTestDataset(studyReference2.getId());

		final StudyReference studyReference3 = studyTestDataInitializer
				.addTestStudy(TEST_TRIAL_NAME_3, StudyType.T, String.valueOf(TermId.SEASON_DRY.getId()),
						String.valueOf(LUXEMBOURG_COUNTRY_LOCATION_ID));
		studyTestDataInitializer.addTestDataset(studyReference3.getId());

	}

}
