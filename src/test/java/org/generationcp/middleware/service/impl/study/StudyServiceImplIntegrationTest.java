package org.generationcp.middleware.service.impl.study;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.study.StudyDetailsDto;
import org.generationcp.middleware.service.api.study.StudyService;
import org.generationcp.middleware.utils.test.IntegrationTestDataInitializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

public class StudyServiceImplIntegrationTest extends IntegrationTestBase {

	@Autowired
	private StudyService studyService;
	private IntegrationTestDataInitializer testDataInitializer;
	private DmsProject study;
	private DmsProject plot;
	private CVTerm testTrait;

	@Before
	public void setUp() {

		final DmsProjectDao dmsProjectDao = new DmsProjectDao();
		dmsProjectDao.setSession(this.sessionProvder.getSession());
		this.testDataInitializer = new IntegrationTestDataInitializer(this.sessionProvder, this.workbenchSessionProvider);
		this.study = this.testDataInitializer.createStudy("Study1", "Study-Description", 6);

		this.plot = this.testDataInitializer
			.createDmsProject("Plot Dataset", "Plot Dataset-Description", this.study, this.study, DatasetTypeEnum.PLOT_DATA);
		this.testTrait = this.testDataInitializer.createTrait("SomeTrait");
	}

	@Test
	public void testCountTotalObservationUnits() {

		final Geolocation geolocation = this.testDataInitializer.createTestGeolocation("1", 101);
		this.testDataInitializer.createTestExperiments(this.plot, null, geolocation, 5);

		Assert.assertEquals(5, this.studyService.countTotalObservationUnits(this.study.getProjectId(), geolocation.getLocationId()));
	}

	@Test
	public void testHasMeasurementDataEntered() {
		final Geolocation geolocation = this.testDataInitializer.createTestGeolocation("1", 101);
		final List<ExperimentModel> experimentModels = this.testDataInitializer.createTestExperiments(this.plot, null, geolocation, 5);

		Assert.assertFalse(
			this.studyService.hasMeasurementDataEntered(Collections.singletonList(this.testTrait.getCvTermId()), this.study.getProjectId()));

		this.testDataInitializer.addPhenotypes(experimentModels, this.testTrait.getCvTermId(), RandomStringUtils.randomNumeric(5));
		// Need to flush session to sync with underlying database before querying
		this.sessionProvder.getSession().flush();
		Assert.assertTrue(
			this.studyService.hasMeasurementDataEntered(Collections.singletonList(this.testTrait.getCvTermId()), this.study.getProjectId()));
	}

	@Test
	public void testHasMeasurementDataOnEnvironment() {
		final Geolocation geolocation = this.testDataInitializer.createTestGeolocation("1", 101);
		final List<ExperimentModel> experimentModels = this.testDataInitializer.createTestExperiments(this.plot, null, geolocation, 5);
		Assert.assertFalse(this.studyService.hasMeasurementDataOnEnvironment(this.study.getProjectId(), geolocation.getLocationId()));

		this.testDataInitializer.addPhenotypes(experimentModels, this.testTrait.getCvTermId(), RandomStringUtils.randomNumeric(5));
		// Need to flush session to sync with underlying database before querying
		this.sessionProvder.getSession().flush();
		Assert.assertTrue(this.studyService.hasMeasurementDataOnEnvironment(this.study.getProjectId(), geolocation.getLocationId()));
	}

	@Test
	public void testGetStudyDetailsForGeolocation() {
		final DmsProject environmentDataset =
			this.testDataInitializer
				.createDmsProject("Summary Dataset", "Summary Dataset-Description", this.study, this.study, DatasetTypeEnum.SUMMARY_DATA);

		final int locationId = 101;
		final Geolocation geolocation = this.testDataInitializer.createTestGeolocation("1", locationId);
		this.testDataInitializer
			.createTestExperiment(environmentDataset, geolocation, TermId.TRIAL_ENVIRONMENT_EXPERIMENT.getId(), "0", null);
		final StudyDetailsDto studyDetailsDto = this.studyService.getStudyDetailsByGeolocation(geolocation.getLocationId());
		Assert.assertTrue(CollectionUtils.isEmpty(studyDetailsDto.getContacts()));
		Assert.assertEquals(locationId, studyDetailsDto.getMetadata().getLocationId().intValue());
		Assert.assertEquals(geolocation.getLocationId(), studyDetailsDto.getMetadata().getStudyDbId());
		Assert.assertEquals(this.study.getProjectId(), studyDetailsDto.getMetadata().getTrialDbId());
		Assert.assertEquals(this.study.getName() + " Environment Number 1", studyDetailsDto.getMetadata().getStudyName());
	}

	@Test
	public void testGetStudyDetailsForGeolocationWithPI_ID() {
		final DmsProject environmentDataset =
			this.testDataInitializer
				.createDmsProject("Summary Dataset", "Summary Dataset-Description", this.study, this.study, DatasetTypeEnum.SUMMARY_DATA);
		final WorkbenchUser user = this.testDataInitializer.createUserForTesting();
		final int locationId = 101;

		final Geolocation geolocation = this.testDataInitializer.createTestGeolocation("1", locationId);
		this.testDataInitializer
			.createTestExperiment(environmentDataset, geolocation, TermId.TRIAL_ENVIRONMENT_EXPERIMENT.getId(), "0", null);
		this.testDataInitializer.addProjectProp(this.study, TermId.PI_ID.getId(), "", VariableType.STUDY_DETAIL, String.valueOf(user.getPerson().getId()), 6);


		final StudyDetailsDto studyDetailsDto = this.studyService.getStudyDetailsByGeolocation(geolocation.getLocationId());

		Assert.assertFalse(CollectionUtils.isEmpty(studyDetailsDto.getContacts()));
		Assert.assertEquals(user.getUserid(), studyDetailsDto.getContacts().get(0).getUserId());
		Assert.assertEquals(locationId, studyDetailsDto.getMetadata().getLocationId().intValue());
		Assert.assertEquals(geolocation.getLocationId(), studyDetailsDto.getMetadata().getStudyDbId());
		Assert.assertEquals(this.study.getProjectId(), studyDetailsDto.getMetadata().getTrialDbId());
		Assert.assertEquals(this.study.getName() + " Environment Number 1", studyDetailsDto.getMetadata().getStudyName());
	}

}
