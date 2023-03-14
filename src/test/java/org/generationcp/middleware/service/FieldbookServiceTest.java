
package org.generationcp.middleware.service;

import com.google.common.base.Optional;
import org.apache.commons.lang.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.api.program.ProgramService;
import org.generationcp.middleware.dao.germplasmlist.GermplasmListDAO;
import org.generationcp.middleware.data.initializer.StudyTestDataInitializer;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.manager.StudyDataManagerImpl;
import org.generationcp.middleware.manager.WorkbenchDaoFactory;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.operation.builder.DataSetBuilder;
import org.generationcp.middleware.operation.builder.WorkbookBuilder;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.user.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class FieldbookServiceTest extends IntegrationTestBase {

	@Autowired
	private OntologyDataManager ontologyManager;

	@Autowired
	private UserService userService;

	@Autowired
	private ProgramService programService;

	@Autowired
	private WorkbenchTestDataUtil workbenchTestDataUtil;

	@Autowired
	private DataSetBuilder dataSetBuilder;

	@Autowired
	private WorkbookBuilder workbookBuilder;

	@Autowired
	private StudyDataManager studyDataManager;

	private FieldbookServiceImpl fieldbookService;

	private StudyReference studyReference;

	private StudyTestDataInitializer studyTDI;
	private StudyDataManagerImpl manager;
	private Project commonTestProject;
	private GermplasmListDAO germplasmListDAO;
	private CropType crop;

	private WorkbenchDaoFactory workbenchDaoFactory;

	@Before
	public void setUp() throws Exception {
		this.fieldbookService = new FieldbookServiceImpl(this.sessionProvder);
		this.manager = new StudyDataManagerImpl(this.sessionProvder);
		this.workbenchDaoFactory = new WorkbenchDaoFactory(this.workbenchSessionProvider);

		this.workbenchTestDataUtil.setUpWorkbench(workbenchDaoFactory);

		if (this.commonTestProject == null) {
			this.commonTestProject = this.workbenchTestDataUtil.getCommonTestProject();
			this.crop = this.programService.getProjectByUuid(this.commonTestProject.getUniqueID()).getCropType();
		}

		this.germplasmListDAO = new GermplasmListDAO(this.sessionProvder.getSession());

		this.studyTDI = new StudyTestDataInitializer(this.manager, this.ontologyManager, this.commonTestProject,
			this.sessionProvder);

		this.studyReference = this.studyTDI.addTestStudy();
		this.studyTDI.createEnvironmentDataset(this.crop, this.studyReference.getId(), "1", String.valueOf(TermId.SEASON_DRY.getId()));
		this.studyTDI.addTestDataset(this.studyReference.getId(), DatasetTypeEnum.PLOT_DATA.getId());
		this.fieldbookService.setStudyDataManager(this.studyDataManager);
		this.fieldbookService.setDataSetBuilder(this.dataSetBuilder);
		this.fieldbookService.setWorkbookBuilder(this.workbookBuilder);
	}

	@Test
	public void testSetOrderVariableByRankIfWorkbookIsNull() {
		Assert.assertFalse("Should return false since the workbook is null", this.fieldbookService.setOrderVariableByRank(null));
	}

	@Test
	public void testGetStudyByNameAndProgramUUID() {
		final Workbook workbook = this.fieldbookService.getStudyByNameAndProgramUUID(
			this.studyReference.getName(),
			this.studyReference.getProgramUUID());
		Assert.assertEquals(this.studyReference.getName(), workbook.getStudyName());
		Assert.assertEquals(this.studyReference.getDescription(), workbook.getStudyDetails().getDescription());
		Assert.assertEquals(this.studyReference.getProgramUUID(), workbook.getStudyDetails().getProgramUUID());
	}

	@Test
	public void testSetOrderVariableByRankIfWorkbookIsNotNull() {
		final Workbook workbook = this.fieldbookService.getStudyByNameAndProgramUUID(
			this.studyReference.getName(),
			this.studyReference.getProgramUUID());
		Assert.assertTrue(
			"Should return true since the workbook is not null",
			this.fieldbookService.setOrderVariableByRank(workbook));
	}

	@Test
	public void testGetStudyReferenceByNameAndProgramUUID() {
		Optional<StudyReference> studyOptional = this.fieldbookService.getStudyReferenceByNameAndProgramUUID(
			RandomStringUtils.random(5), this.commonTestProject.getUniqueID());
		Assert.assertFalse(studyOptional.isPresent());

		studyOptional = this.fieldbookService
			.getStudyReferenceByNameAndProgramUUID(this.studyReference.getName(), RandomStringUtils.random(5));
		Assert.assertFalse(studyOptional.isPresent());

		studyOptional = this.fieldbookService
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
		final WorkbenchUser workbenchUser = this.userService.getUserById(this.studyReference.getOwnerId());
		Assert.assertEquals(workbenchUser.getPerson().getFirstName() + " " + workbenchUser.getPerson().getLastName(), study.getOwnerName());
	}


}
