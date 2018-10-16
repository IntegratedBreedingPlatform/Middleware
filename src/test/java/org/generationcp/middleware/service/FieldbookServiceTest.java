
package org.generationcp.middleware.service;

import javax.annotation.Resource;

import org.apache.commons.lang.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.data.initializer.StudyTestDataInitializer;
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.StudyDataManagerImpl;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.manager.api.UserDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.service.api.FieldbookService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Optional;

public class FieldbookServiceTest extends IntegrationTestBase {

	@Autowired
	private OntologyDataManager ontologyManager;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private GermplasmDataManager germplasmDataDM;

	@Autowired
	private LocationDataManager locationManager;

	@Resource
	private FieldbookService fieldbookMiddlewareService;
	
	@Autowired
	private UserDataManager userDataManager;

	private StudyReference studyReference;
	private WorkbenchTestDataUtil workbenchTestDataUtil;
	private StudyTestDataInitializer studyTDI;
	private final String cropPrefix = "ABCD";
	private StudyDataManagerImpl manager;
	private Project commonTestProject;

	@Before
	public void setUp() throws Exception {
		this.manager = new StudyDataManagerImpl(this.sessionProvder);

		if (this.workbenchTestDataUtil == null) {
			this.workbenchTestDataUtil = new WorkbenchTestDataUtil(this.workbenchDataManager);
			this.workbenchTestDataUtil.setUpWorkbench();
		}

		if (this.commonTestProject == null) {
			this.commonTestProject = this.workbenchTestDataUtil.getCommonTestProject();
		}

		this.studyTDI = new StudyTestDataInitializer(this.manager, this.ontologyManager, this.commonTestProject, this.germplasmDataDM,
				this.locationManager, this.userDataManager);

		this.studyReference = this.studyTDI.addTestStudy(this.cropPrefix);
		this.studyTDI.addEnvironmentDataset(this.studyReference.getId(), "1", String.valueOf(TermId.SEASON_DRY.getId()));
		this.studyTDI.addTestDataset(this.studyReference.getId(), DataSetType.PLOT_DATA);
	}

	@Test
	public void testSetOrderVariableByRankIfWorkbookIsNull() {
		Assert.assertFalse("Should return false since the workbook is null", this.fieldbookMiddlewareService.setOrderVariableByRank(null));
	}

	@Test
	public void testGetStudyByNameAndProgramUUID() {
		final Workbook workbook = this.fieldbookMiddlewareService.getStudyByNameAndProgramUUID(this.studyReference.getName(),
				this.studyReference.getProgramUUID());
		Assert.assertEquals(this.studyReference.getName(), workbook.getStudyName());
		Assert.assertEquals(this.studyReference.getDescription(), workbook.getStudyDetails().getDescription());
		Assert.assertEquals(this.studyReference.getProgramUUID(), workbook.getStudyDetails().getProgramUUID());
	}

	@Test
	public void testSetOrderVariableByRankIfWorkbookIsNotNull() {
		final Workbook workbook = this.fieldbookMiddlewareService.getStudyByNameAndProgramUUID(this.studyReference.getName(),
				this.studyReference.getProgramUUID());
		Assert.assertTrue("Should return true since the workbook is not null",
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
		Optional<StudyReference> studyOptional = this.fieldbookMiddlewareService.getStudyReferenceByNameAndProgramUUID(RandomStringUtils.random(5), this.commonTestProject.getUniqueID());
		Assert.assertFalse(studyOptional.isPresent());
		
		studyOptional = this.fieldbookMiddlewareService.getStudyReferenceByNameAndProgramUUID(this.studyReference.getName(), RandomStringUtils.random(5));
		Assert.assertFalse(studyOptional.isPresent());
		
		studyOptional = this.fieldbookMiddlewareService.getStudyReferenceByNameAndProgramUUID(this.studyReference.getName(), this.commonTestProject.getUniqueID());
		Assert.assertTrue(studyOptional.isPresent());
		StudyReference study = studyOptional.get();
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

}
