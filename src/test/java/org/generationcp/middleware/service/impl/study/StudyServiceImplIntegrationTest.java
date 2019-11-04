package org.generationcp.middleware.service.impl.study;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.service.api.study.StudyService;
import org.generationcp.middleware.utils.test.IntegrationTestDataInitializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class StudyServiceImplIntegrationTest extends IntegrationTestBase {

	private DmsProjectDao dmsProjectDao;
	private StudyService studyService;
	private IntegrationTestDataInitializer testDataInitializer;
	private DmsProject study;
	private DmsProject plot;
	private CVTerm testTrait;

	@Before
	public void setUp() {

		this.dmsProjectDao = new DmsProjectDao();
		this.dmsProjectDao.setSession(this.sessionProvder.getSession());
		this.studyService = new StudyServiceImpl(this.sessionProvder);
		this.testDataInitializer = new IntegrationTestDataInitializer(this.sessionProvder, this.workbenchSessionProvider);
		this.study = this.testDataInitializer.createDmsProject("Study1", "Study-Description", null, this.dmsProjectDao.getById(1), null);
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
			this.studyService.hasMeasurementDataEntered(Arrays.asList(this.testTrait.getCvTermId()), this.study.getProjectId()));

		this.testDataInitializer.addPhenotypes(experimentModels, this.testTrait.getCvTermId(), RandomStringUtils.randomNumeric(5));
		// Need to flush session to sync with underlying database before querying
		this.sessionProvder.getSession().flush();
		Assert.assertTrue(
			this.studyService.hasMeasurementDataEntered(Arrays.asList(this.testTrait.getCvTermId()), this.study.getProjectId()));
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

}
