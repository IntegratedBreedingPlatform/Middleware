
package org.generationcp.middleware.service;

import com.google.common.base.Optional;
import org.apache.commons.lang.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.dao.GermplasmListDAO;
import org.generationcp.middleware.data.initializer.GermplasmListTestDataInitializer;
import org.generationcp.middleware.data.initializer.StudyTestDataInitializer;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.gms.GermplasmListType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.manager.StudyDataManagerImpl;
import org.generationcp.middleware.manager.UserDataManagerImpl;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.manager.api.UserDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class FieldbookServiceTest extends IntegrationTestBase {

	@Autowired
	private OntologyDataManager ontologyManager;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private GermplasmDataManager germplasmDataDM;

	@Autowired
	private LocationDataManager locationManager;

	private FieldbookServiceImpl fieldbookMiddlewareService;

	private UserDataManager userDataManager;

	private StudyReference studyReference;
	private WorkbenchTestDataUtil workbenchTestDataUtil;
	private StudyTestDataInitializer studyTDI;
	private StudyDataManagerImpl manager;
	private Project commonTestProject;
	private GermplasmListDAO germplasmListDAO;
	private CropType crop;

	private final String cropPrefix = "ABCD";
	private static final String TEST_LIST_DESCRIPTION = "Test List Description";
	private static final long TEST_GERMPLASM_LIST_DATE = 20141103;
	private static final Integer STATUS_ACTIVE = 0;
	private static final String PROGRAM_UUID = "1001";
	private static final int TEST_GERMPLASM_LIST_USER_ID = 9999;

	@Before
	public void setUp() throws Exception {
		this.fieldbookMiddlewareService = new FieldbookServiceImpl(this.sessionProvder, "TESTCROP");
		this.manager = new StudyDataManagerImpl(this.sessionProvder);
		this.userDataManager = new UserDataManagerImpl(this.sessionProvder);

		if (this.workbenchTestDataUtil == null) {
			this.workbenchTestDataUtil = new WorkbenchTestDataUtil(this.workbenchDataManager);
			this.workbenchTestDataUtil.setUpWorkbench();
		}

		if (this.commonTestProject == null) {
			this.commonTestProject = this.workbenchTestDataUtil.getCommonTestProject();
			this.crop = this.workbenchDataManager.getProjectByUuid(this.commonTestProject.getUniqueID()).getCropType();
		}

		this.germplasmListDAO = new GermplasmListDAO();
		this.germplasmListDAO.setSession(this.sessionProvder.getSession());

		this.studyTDI = new StudyTestDataInitializer(this.manager, this.ontologyManager, this.commonTestProject, this.germplasmDataDM,
			this.locationManager, this.userDataManager);

		this.studyReference = this.studyTDI.addTestStudy();
		this.studyTDI.addEnvironmentDataset(this.crop, this.studyReference.getId(), "1", String.valueOf(TermId.SEASON_DRY.getId()));
		this.studyTDI.addTestDataset(this.studyReference.getId(), DatasetTypeEnum.PLOT_DATA.getId());
	}

	@Test
	public void testSetOrderVariableByRankIfWorkbookIsNull() {
		Assert.assertFalse("Should return false since the workbook is null", this.fieldbookMiddlewareService.setOrderVariableByRank(null));
	}

	@Test
	public void testGetStudyByNameAndProgramUUID() {
		final Workbook workbook = this.fieldbookMiddlewareService.getStudyByNameAndProgramUUID(
			this.studyReference.getName(),
			this.studyReference.getProgramUUID());
		Assert.assertEquals(this.studyReference.getName(), workbook.getStudyName());
		Assert.assertEquals(this.studyReference.getDescription(), workbook.getStudyDetails().getDescription());
		Assert.assertEquals(this.studyReference.getProgramUUID(), workbook.getStudyDetails().getProgramUUID());
	}

	@Test
	public void testSetOrderVariableByRankIfWorkbookIsNotNull() {
		final Workbook workbook = this.fieldbookMiddlewareService.getStudyByNameAndProgramUUID(
			this.studyReference.getName(),
			this.studyReference.getProgramUUID());
		Assert.assertTrue(
			"Should return true since the workbook is not null",
			this.fieldbookMiddlewareService.setOrderVariableByRank(workbook));
	}

	@Test
	public void testGetCompleteDataset() {
		final Workbook workbook = this.fieldbookMiddlewareService.getCompleteDataset(this.studyReference.getId());
		Assert.assertNotNull(workbook.getObservations());
		Assert.assertNotNull(workbook.getFactors());
		Assert.assertNotNull(workbook.getVariates());
		Assert.assertNotNull(workbook.getMeasurementDatasetVariables());
	}

	@Test
	public void testGetStudyReferenceByNameAndProgramUUID() {
		Optional<StudyReference> studyOptional = this.fieldbookMiddlewareService.getStudyReferenceByNameAndProgramUUID(
			RandomStringUtils.random(5), this.commonTestProject.getUniqueID());
		Assert.assertFalse(studyOptional.isPresent());

		studyOptional = this.fieldbookMiddlewareService
			.getStudyReferenceByNameAndProgramUUID(this.studyReference.getName(), RandomStringUtils.random(5));
		Assert.assertFalse(studyOptional.isPresent());

		studyOptional = this.fieldbookMiddlewareService
			.getStudyReferenceByNameAndProgramUUID(this.studyReference.getName(), this.commonTestProject.getUniqueID());
		Assert.assertTrue(studyOptional.isPresent());
		final StudyReference study = studyOptional.get();
		Assert.assertEquals(this.studyReference.getId(), study.getId());
		Assert.assertEquals(this.studyReference.getName(), study.getName());
		Assert.assertEquals(this.studyReference.getDescription(), study.getDescription());
		Assert.assertEquals(this.studyReference.getProgramUUID(), study.getProgramUUID());
		Assert.assertEquals(this.studyReference.getStudyType(), study.getStudyType());
		Assert.assertFalse(study.getIsLocked());
		Assert.assertEquals(this.studyReference.getOwnerId(), study.getOwnerId());
		final User user = this.userDataManager.getUserById(this.studyReference.getOwnerId());
		final Person person = this.userDataManager.getPersonById(user.getPersonid());
		Assert.assertEquals(person.getFirstName() + " " + person.getLastName(), study.getOwnerName());
	}

	@Test
	public void testHasAdvancedOrCrossesListForAdvanced() {
		Assert.assertFalse(this.fieldbookMiddlewareService.hasAdvancedOrCrossesList(this.studyReference.getId()));
		final GermplasmList testList =
			GermplasmListTestDataInitializer.createGermplasmListTestData("ADV LIST", FieldbookServiceTest.TEST_LIST_DESCRIPTION,
				FieldbookServiceTest.TEST_GERMPLASM_LIST_DATE, GermplasmListType.ADVANCED.name(),
				FieldbookServiceTest.TEST_GERMPLASM_LIST_USER_ID, FieldbookServiceTest.STATUS_ACTIVE, FieldbookServiceTest.PROGRAM_UUID,
				this.studyReference.getId());
		testList.setProjectId(this.studyReference.getId());
		this.germplasmListDAO.saveOrUpdate(testList);
		Assert.assertTrue(this.fieldbookMiddlewareService.hasAdvancedOrCrossesList(this.studyReference.getId()));
	}

	@Test
	public void testHasAdvancedOrCrossesListForCreatedCrosses() {
		Assert.assertFalse(this.fieldbookMiddlewareService.hasAdvancedOrCrossesList(this.studyReference.getId()));
		final GermplasmList testList =
			GermplasmListTestDataInitializer.createGermplasmListTestData("CREATED CROSSES", FieldbookServiceTest.TEST_LIST_DESCRIPTION,
				FieldbookServiceTest.TEST_GERMPLASM_LIST_DATE, GermplasmListType.CRT_CROSS.name(),
				FieldbookServiceTest.TEST_GERMPLASM_LIST_USER_ID, FieldbookServiceTest.STATUS_ACTIVE, FieldbookServiceTest.PROGRAM_UUID,
				this.studyReference.getId());
		testList.setProjectId(this.studyReference.getId());
		this.germplasmListDAO.saveOrUpdate(testList);
		Assert.assertTrue(this.fieldbookMiddlewareService.hasAdvancedOrCrossesList(this.studyReference.getId()));
	}

	@Test
	public void testHasAdvancedOrCrossesListForImportedCrosses() {
		Assert.assertFalse(this.fieldbookMiddlewareService.hasAdvancedOrCrossesList(this.studyReference.getId()));
		final GermplasmList testList =
			GermplasmListTestDataInitializer.createGermplasmListTestData("IMPORTED CROSSES", FieldbookServiceTest.TEST_LIST_DESCRIPTION,
				FieldbookServiceTest.TEST_GERMPLASM_LIST_DATE, GermplasmListType.IMP_CROSS.name(),
				FieldbookServiceTest.TEST_GERMPLASM_LIST_USER_ID, FieldbookServiceTest.STATUS_ACTIVE, FieldbookServiceTest.PROGRAM_UUID,
				this.studyReference.getId());
		testList.setProjectId(this.studyReference.getId());
		this.germplasmListDAO.saveOrUpdate(testList);
		Assert.assertTrue(this.fieldbookMiddlewareService.hasAdvancedOrCrossesList(this.studyReference.getId()));
	}
}
