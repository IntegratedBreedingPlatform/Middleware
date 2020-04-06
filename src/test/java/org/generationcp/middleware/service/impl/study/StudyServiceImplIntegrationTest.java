package org.generationcp.middleware.service.impl.study;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.study.StudyDetailsDto;
import org.generationcp.middleware.service.api.study.StudyService;
import org.generationcp.middleware.utils.test.IntegrationTestDataInitializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

public class StudyServiceImplIntegrationTest extends IntegrationTestBase {

	@Autowired
	private StudyService studyService;
	private IntegrationTestDataInitializer testDataInitializer;
	private DmsProject study;
	private DmsProject plot;
	private DmsProject summary;

	@Before
	public void setUp() {

		final DmsProjectDao dmsProjectDao = new DmsProjectDao();
		dmsProjectDao.setSession(this.sessionProvder.getSession());
		this.testDataInitializer = new IntegrationTestDataInitializer(this.sessionProvder, this.workbenchSessionProvider);
		this.study = this.testDataInitializer.createDmsProject("Study1", "Study-Description", null, dmsProjectDao.getById(1), null);

		this.plot = this.testDataInitializer
			.createDmsProject("Plot Dataset", "Plot Dataset-Description", this.study, this.study, DatasetTypeEnum.PLOT_DATA);
		this.summary = this.testDataInitializer
			.createDmsProject("Summary Dataset", "Summary Dataset-Description", this.study, this.study, DatasetTypeEnum.SUMMARY_DATA);
	}

	@Test
	public void testGetStudyDetailsByEnvironment() {
		final ExperimentModel instanceModel = this.testDataInitializer.createInstanceExperimentModel(this.summary, 1, "1");

		this.testDataInitializer
			.createTestExperiment(this.plot, null, TermId.TRIAL_ENVIRONMENT_EXPERIMENT.getId(), "0", instanceModel);
		final StudyDetailsDto studyDetailsDto = this.studyService.getStudyDetailsByEnvironment(instanceModel.getNdExperimentId());
		Assert.assertTrue(CollectionUtils.isEmpty(studyDetailsDto.getContacts()));
		Assert.assertEquals(1, studyDetailsDto.getMetadata().getLocationId().intValue());
		Assert.assertEquals(instanceModel.getNdExperimentId(), studyDetailsDto.getMetadata().getStudyDbId());
		Assert.assertEquals(this.study.getProjectId(), studyDetailsDto.getMetadata().getTrialDbId());
		Assert.assertEquals(this.study.getName() + " Environment Number 1", studyDetailsDto.getMetadata().getStudyName());
	}

	@Test
	public void testGetStudyDetailsByEnvironmentWithPI_ID() {
		final WorkbenchUser user = this.testDataInitializer.createUserForTesting();
		final ExperimentModel instanceModel = this.testDataInitializer.createInstanceExperimentModel(this.summary, 1, "1");

		this.testDataInitializer
			.createTestExperiment(this.plot, null, TermId.TRIAL_ENVIRONMENT_EXPERIMENT.getId(), "0", null);
		this.testDataInitializer.addProjectProp(this.study, TermId.PI_ID.getId(), "", VariableType.STUDY_DETAIL, String.valueOf(user.getPerson().getId()), 6);


		final StudyDetailsDto studyDetailsDto = this.studyService.getStudyDetailsByEnvironment(instanceModel.getNdExperimentId());

		Assert.assertFalse(CollectionUtils.isEmpty(studyDetailsDto.getContacts()));
		Assert.assertEquals(user.getUserid(), studyDetailsDto.getContacts().get(0).getUserId());
		Assert.assertEquals(1, studyDetailsDto.getMetadata().getLocationId().intValue());
		Assert.assertEquals(instanceModel.getNdExperimentId(), studyDetailsDto.getMetadata().getStudyDbId());
		Assert.assertEquals(this.study.getProjectId(), studyDetailsDto.getMetadata().getTrialDbId());
		Assert.assertEquals(this.study.getName() + " Environment Number 1", studyDetailsDto.getMetadata().getStudyName());
	}

}
