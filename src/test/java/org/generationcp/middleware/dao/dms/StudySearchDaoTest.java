package org.generationcp.middleware.dao.dms;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.data.initializer.StudyTestDataInitializer;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.dms.StudySearchMatchingOption;
import org.generationcp.middleware.domain.oms.StudyType;
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

import java.util.List;

public class StudySearchDaoTest extends IntegrationTestBase {

	private final int NO_OF_TEST_STUDIES = 3;

	private StudySearchDao studySearchDao;

	@Autowired
	private OntologyDataManager ontologyManager;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private GermplasmDataManager germplasmDataDM;

	@Autowired
	private LocationDataManager locationManager;
	private final String cropPrefix = "ABCD";

	@Before
	public void init() throws Exception {
		
		this.createTestStudies(NO_OF_TEST_STUDIES);

		this.studySearchDao = new StudySearchDao();
		this.studySearchDao.setSession(this.sessionProvder.getSession());

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

	private void createTestStudies(final int numberOfStudies) throws Exception {

		final WorkbenchTestDataUtil workbenchTestDataUtil = new WorkbenchTestDataUtil(this.workbenchDataManager);
		final Project project = workbenchTestDataUtil.createTestProjectData();

		final StudyDataManagerImpl studyDataManager = new StudyDataManagerImpl();
		studyDataManager.setSessionProvider(this.sessionProvder);
		final StudyTestDataInitializer studyTestDataInitializer =
				new StudyTestDataInitializer(studyDataManager, this.ontologyManager, project, this.germplasmDataDM, this.locationManager);

		for (int i = 1; i <= numberOfStudies; i++) {
			final StudyReference studyReference = studyTestDataInitializer.addTestStudy(StudyType.T, i + " Test Trial Sample", cropPrefix);
			studyTestDataInitializer.addTestDataset(studyReference.getId());
		}

	}

}
