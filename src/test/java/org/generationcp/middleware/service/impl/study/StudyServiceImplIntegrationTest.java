package org.generationcp.middleware.service.impl.study;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.dao.dms.GeolocationPropertyDao;
import org.generationcp.middleware.domain.dms.ExperimentDesignType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.GeolocationProperty;
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
	private GeolocationPropertyDao geolocPropertyDao;
	private StudyService studyService;
	private IntegrationTestDataInitializer testDataInitializer;
	private DmsProject study;
	private DmsProject plot;
	private CVTerm testTrait;

	@Before
	public void setUp() {

		this.dmsProjectDao = new DmsProjectDao();
		this.dmsProjectDao.setSession(this.sessionProvder.getSession());
		this.geolocPropertyDao = new GeolocationPropertyDao();
		this.geolocPropertyDao.setSession(this.sessionProvder.getSession());
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

	@Test
	public void testGetStudyInstances() {

		final DmsProject someStudy =
			this.testDataInitializer.createDmsProject("Study1", "Study-Description", null, this.dmsProjectDao.getById(1), null);
		final DmsProject someSummary =
			this.testDataInitializer
				.createDmsProject("Summary Dataset", "Summary Dataset-Description", someStudy, someStudy, DatasetTypeEnum.SUMMARY_DATA);

		final Geolocation instance1 = this.testDataInitializer.createTestGeolocation("1", 1);
		final Geolocation instance2 = this.testDataInitializer.createTestGeolocation("2", 2);
		this.saveGeolocationProperty(instance1, ExperimentDesignType.RANDOMIZED_COMPLETE_BLOCK.getTermId().toString(), TermId.EXPERIMENT_DESIGN_FACTOR.getId());
		this.saveGeolocationProperty(instance2, RandomStringUtils.randomAlphabetic(5), TermId.BLOCK_ID.getId());

		this.testDataInitializer.createTestExperiment(someSummary, instance1, TermId.SUMMARY_EXPERIMENT.getId(), "0", null);
		this.testDataInitializer.createTestExperiment(someSummary, instance2, TermId.SUMMARY_EXPERIMENT.getId(), "0", null);

		final List<StudyInstance> studyInstances = this.studyService.getStudyInstances(someStudy.getProjectId());

		Assert.assertEquals(2, studyInstances.size());

		final StudyInstance studyInstance1 = studyInstances.get(0);

		Assert.assertEquals(instance1.getLocationId().intValue(), studyInstance1.getInstanceDbId());
		Assert.assertEquals(1, studyInstance1.getInstanceNumber());
		Assert.assertNull(studyInstance1.getCustomLocationAbbreviation());
		Assert.assertEquals("AFG", studyInstance1.getLocationAbbreviation());
		Assert.assertEquals("Afghanistan", studyInstance1.getLocationName());
		Assert.assertFalse(studyInstance1.isHasFieldmap());
		Assert.assertTrue(studyInstance1.isHasExperimentalDesign());
		// Design re-generation not allowed because instance has fieldmap
		Assert.assertTrue(studyInstance1.isDesignReGenerationAllowed());

		final StudyInstance studyInstance2 = studyInstances.get(1);

		Assert.assertEquals(instance2.getLocationId().intValue(), studyInstance2.getInstanceDbId());
		Assert.assertEquals(2, studyInstance2.getInstanceNumber());
		Assert.assertNull(studyInstance2.getCustomLocationAbbreviation());
		Assert.assertEquals("ALB", studyInstance2.getLocationAbbreviation());
		Assert.assertEquals("Albania", studyInstance2.getLocationName());
		Assert.assertTrue(studyInstance2.isHasFieldmap());
		Assert.assertFalse(studyInstance2.isHasExperimentalDesign());
		Assert.assertFalse(studyInstance2.isDesignReGenerationAllowed());
	}

	private void saveGeolocationProperty(final Geolocation geolocation, final String value, final Integer variableId) {
		final GeolocationProperty geolocationProperty = new GeolocationProperty(geolocation, value, 1, variableId);
		this.geolocPropertyDao.save(geolocationProperty);
	}

}
