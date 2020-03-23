package org.generationcp.middleware.service.impl.study;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.data.initializer.StudyTestDataInitializer;
import org.generationcp.middleware.domain.dms.ExperimentDesignType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.StudyDataManagerImpl;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.service.api.study.StudyInstanceService;
import org.generationcp.middleware.utils.test.IntegrationTestDataInitializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class StudyInstanceServiceImplTest extends IntegrationTestBase {

	public static final String PROGRAM_UUID = UUID.randomUUID().toString();
	public static final String TRIAL_INSTANCE = "TRIAL_INSTANCE";
	public static final String LOCATION_NAME = "LOCATION_NAME";

	private IntegrationTestDataInitializer testDataInitializer;

	@Autowired
	private GermplasmDataManager germplasmDataManager;

	@Autowired
	private OntologyDataManager ontologyManager;

	@Autowired
	private LocationDataManager locationManager;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private OntologyDataManager ontologyDataManager;

	@Autowired
	private WorkbenchTestDataUtil workbenchTestDataUtil;

	private DaoFactory daoFactory;
	private StudyDataManagerImpl studyDataManager;
	private StudyTestDataInitializer studyTestDataInitializer;
	private StudyInstanceService studyInstanceService;
	private Project commonTestProject;
	private CropType cropType;
	private DmsProject study;
	private DmsProject summary;
	private DmsProject plot;
	private ExperimentModel instance1;
	private ExperimentModel instance2;
	private ExperimentModel instance3;

	@Before
	public void setup() {

		this.studyInstanceService = new StudyInstanceServiceImpl(this.sessionProvder);
		this.studyDataManager = new StudyDataManagerImpl(this.sessionProvder);
		this.daoFactory = new DaoFactory(this.sessionProvder);

		if (this.commonTestProject == null) {
			this.commonTestProject = this.workbenchTestDataUtil.getCommonTestProject();
		}

		this.testDataInitializer = new IntegrationTestDataInitializer(this.sessionProvder, this.workbenchSessionProvider);

		this.studyTestDataInitializer =
			new StudyTestDataInitializer(this.studyDataManager, this.ontologyManager, this.commonTestProject, this.germplasmDataManager,
				this.locationManager);

		this.cropType = this.workbenchDataManager.getCropTypeByName(CropType.CropEnum.MAIZE.name());

		this.study =
			this.testDataInitializer
				.createDmsProject("Study1", "Study-Description", null, this.daoFactory.getDmsProjectDAO().getById(1), null);
		this.summary =
			this.testDataInitializer
				.createDmsProject("Summary Dataset", "Summary Dataset-Description", study, study, DatasetTypeEnum.SUMMARY_DATA);
		this.plot =
			this.testDataInitializer
				.createDmsProject("Plot Dataset", "Plot Dataset-Description", study, study, DatasetTypeEnum.PLOT_DATA);

	}

	@Test
	public void testCreateStudyInstances() {

		// Create instance 1
		final Integer studyId = this.study.getProjectId();
		final List<StudyInstance> studyInstances =
			this.studyInstanceService.createStudyInstances(this.cropType, studyId, this.summary.getProjectId(), 2);

		// Need to flush session to sync with underlying database before querying
		this.sessionProvder.getSession().flush();
		final StudyInstance studyInstance1 = studyInstances.get(0);
		assertEquals(1, studyInstance1.getInstanceNumber());
		assertNotNull(studyInstance1.getInstanceId());
		assertNotNull(studyInstance1.getLocationId());
		assertFalse(studyInstance1.isHasFieldmap());
		assertEquals("Unspecified Location", studyInstance1.getLocationName());
		assertEquals("NOLOC", studyInstance1.getLocationAbbreviation());
		assertNull(studyInstance1.getCustomLocationAbbreviation());
		assertTrue(studyInstance1.getCanBeDeleted());
		assertFalse(studyInstance1.isHasMeasurements());
		assertFalse(studyInstance1.isHasExperimentalDesign());

		final StudyInstance studyInstance2 = studyInstances.get(1);
		assertEquals(2, studyInstance2.getInstanceNumber());
		assertNotNull(studyInstance2.getInstanceId());
		assertNotNull(studyInstance2.getLocationId());
		assertFalse(studyInstance2.isHasFieldmap());
		assertEquals("Unspecified Location", studyInstance2.getLocationName());
		assertEquals("NOLOC", studyInstance2.getLocationAbbreviation());
		assertNull(studyInstance2.getCustomLocationAbbreviation());
		assertTrue(studyInstance2.getCanBeDeleted());
		assertFalse(studyInstance2.isHasMeasurements());
		assertFalse(studyInstance2.isHasExperimentalDesign());

		final List<ExperimentModel> retrievedStudyInstances =
			this.daoFactory.getInstanceDao().getEnvironments(studyId);
		Assert.assertEquals(2, retrievedStudyInstances.size());

	}

	@Test
	public void testGetStudyInstances() {

		this.createStudyInstances();

		final List<StudyInstance> studyInstances = this.studyInstanceService.getStudyInstances(study.getProjectId());

		Assert.assertEquals(3, studyInstances.size());

		final StudyInstance studyInstance1 = studyInstances.get(0);

		Assert.assertEquals(instance1.getNdExperimentId().intValue(), studyInstance1.getInstanceId());
		Assert.assertEquals(1, studyInstance1.getInstanceNumber());
		Assert.assertNull(studyInstance1.getCustomLocationAbbreviation());
		Assert.assertEquals("AFG", studyInstance1.getLocationAbbreviation());
		Assert.assertEquals("Afghanistan", studyInstance1.getLocationName());
		Assert.assertFalse(studyInstance1.isHasFieldmap());
		Assert.assertTrue(studyInstance1.isHasExperimentalDesign());
		// Instance deletion not allowed because instance has subobservation
		Assert.assertFalse(studyInstance1.getCanBeDeleted());
		Assert.assertTrue(studyInstance1.isHasMeasurements());

		final StudyInstance studyInstance2 = studyInstances.get(1);
		Assert.assertEquals(instance2.getNdExperimentId().intValue(), studyInstance2.getInstanceId());
		Assert.assertEquals(2, studyInstance2.getInstanceNumber());
		Assert.assertNull(studyInstance2.getCustomLocationAbbreviation());
		Assert.assertEquals("ALB", studyInstance2.getLocationAbbreviation());
		Assert.assertEquals("Albania", studyInstance2.getLocationName());
		Assert.assertTrue(studyInstance2.isHasFieldmap());
		Assert.assertTrue(studyInstance2.isHasExperimentalDesign());
		Assert.assertTrue(studyInstance2.getCanBeDeleted());
		Assert.assertFalse(studyInstance2.isHasMeasurements());

		final StudyInstance studyInstance3 = studyInstances.get(2);
		Assert.assertEquals(instance3.getNdExperimentId().intValue(), studyInstance3.getInstanceId());
		Assert.assertEquals(3, studyInstance3.getInstanceNumber());
		Assert.assertNull(studyInstance3.getCustomLocationAbbreviation());
		Assert.assertEquals("DZA", studyInstance3.getLocationAbbreviation());
		Assert.assertEquals("Algeria", studyInstance3.getLocationName());
		Assert.assertFalse(studyInstance3.isHasFieldmap());
		Assert.assertFalse(studyInstance3.isHasExperimentalDesign());
		Assert.assertTrue(studyInstance3.getCanBeDeleted());
		Assert.assertFalse(studyInstance3.isHasMeasurements());
	}

	@Test
	public void testGetStudyInstance() {

		final StudyInstance studyInstance1 =
			this.studyInstanceService.getStudyInstance(study.getProjectId(), instance1.getNdExperimentId()).get();
		Assert.assertEquals(instance1.getNdExperimentId().intValue(), studyInstance1.getInstanceId());
		Assert.assertEquals(1, studyInstance1.getInstanceNumber());
		Assert.assertNull(studyInstance1.getCustomLocationAbbreviation());
		Assert.assertEquals("AFG", studyInstance1.getLocationAbbreviation());
		Assert.assertEquals("Afghanistan", studyInstance1.getLocationName());
		Assert.assertFalse(studyInstance1.isHasFieldmap());
		Assert.assertTrue(studyInstance1.isHasExperimentalDesign());
		// Instance deletion not allowed because instance has subobservation
		Assert.assertFalse(studyInstance1.getCanBeDeleted());
		Assert.assertTrue(studyInstance1.isHasMeasurements());

		final StudyInstance studyInstance2 =
			this.studyInstanceService.getStudyInstance(study.getProjectId(), instance2.getNdExperimentId()).get();
		Assert.assertEquals(instance2.getNdExperimentId().intValue(), studyInstance2.getInstanceId());
		Assert.assertEquals(2, studyInstance2.getInstanceNumber());
		Assert.assertNull(studyInstance2.getCustomLocationAbbreviation());
		Assert.assertEquals("ALB", studyInstance2.getLocationAbbreviation());
		Assert.assertEquals("Albania", studyInstance2.getLocationName());
		Assert.assertTrue(studyInstance2.isHasFieldmap());
		Assert.assertTrue(studyInstance2.isHasExperimentalDesign());
		Assert.assertTrue(studyInstance2.getCanBeDeleted());
		Assert.assertFalse(studyInstance2.isHasMeasurements());

		final StudyInstance studyInstance3 =
			this.studyInstanceService.getStudyInstance(study.getProjectId(), instance3.getNdExperimentId()).get();
		Assert.assertEquals(instance3.getNdExperimentId().intValue(), studyInstance3.getInstanceId());
		Assert.assertEquals(3, studyInstance3.getInstanceNumber());
		Assert.assertNull(studyInstance3.getCustomLocationAbbreviation());
		Assert.assertEquals("DZA", studyInstance3.getLocationAbbreviation());
		Assert.assertEquals("Algeria", studyInstance3.getLocationName());
		Assert.assertFalse(studyInstance3.isHasFieldmap());
		Assert.assertFalse(studyInstance3.isHasExperimentalDesign());
		Assert.assertTrue(studyInstance3.getCanBeDeleted());
		Assert.assertFalse(studyInstance3.isHasMeasurements());
	}

	@Test
	public void testDeleteEnvironment() {
		this.createStudyInstances();
		this.testDataInitializer.addExperimentProp(instance1, TermId.EXPERIMENT_DESIGN_FACTOR.getId(),
			ExperimentDesignType.RANDOMIZED_COMPLETE_BLOCK.getTermId().toString(), 1);
		this.testDataInitializer.addExperimentProp(instance2, TermId.EXPERIMENT_DESIGN_FACTOR.getId(),
			ExperimentDesignType.RANDOMIZED_COMPLETE_BLOCK.getTermId().toString(), 1);
		this.testDataInitializer.addExperimentProp(instance3, TermId.EXPERIMENT_DESIGN_FACTOR.getId(),
			ExperimentDesignType.RANDOMIZED_COMPLETE_BLOCK.getTermId().toString(), 1);

		final Integer studyId = study.getProjectId();

		// Delete Instance 2
		final Integer instance2LocationId = instance2.getNdExperimentId();
		this.studyInstanceService.deleteStudyInstances(studyId, Arrays.asList(instance2LocationId));

		this.sessionProvder.getSession().flush();

		List<StudyInstance> studyInstances =
			this.studyInstanceService.getStudyInstances(studyId);
		Assert.assertEquals(2, studyInstances.size());

		final Integer instance1LocationId = instance1.getNdExperimentId();
		Assert.assertEquals(instance1LocationId,
			this.daoFactory.getExperimentDao().getById(instance1.getNdExperimentId()).getNdExperimentId());
		for (final StudyInstance instance : studyInstances) {
			Assert.assertNotEquals(2, instance.getInstanceNumber());
			Assert.assertNotEquals(instance2LocationId.intValue(), instance.getInstanceId());
		}
		// Confirm geolocation and its properties have been deleted
		Assert.assertFalse(this.studyInstanceService.getStudyInstance(study.getProjectId(), instance2LocationId).isPresent());
		Assert.assertTrue(
			CollectionUtils.isEmpty(this.daoFactory.getEnvironmentPropertyDao().getEnvironmentVariableNameValuesMap(instance2LocationId)));
	}

	private void createStudyInstances() {
		//Study Experiment
		this.testDataInitializer.createTestExperiment(study, null, TermId.STUDY_EXPERIMENT.getId(), "0", null);

		this.instance1 = this.testDataInitializer.createInstanceExperimentModel(this.summary, 1, "1");
		final ExperimentModel instance1PlotExperiment =
			this.testDataInitializer.createTestExperiment(plot, null, TermId.PLOT_EXPERIMENT.getId(), "1", instance1);
		final DmsProject subobs =
			this.testDataInitializer
				.createDmsProject("Subobs Dataset", "Subobs Dataset-Description", study, plot, DatasetTypeEnum.PLANT_SUBOBSERVATIONS);
		// Create 2 Sub-obs records
		final ExperimentModel instance1SubObsExperiment1 =
			this.testDataInitializer
				.createTestExperiment(subobs, null, TermId.PLOT_EXPERIMENT.getId(), "1", instance1PlotExperiment);
		this.savePhenotype(instance1SubObsExperiment1);
		final ExperimentModel instance1SubObsExperiment2 = this.testDataInitializer
			.createTestExperiment(subobs, null, TermId.PLOT_EXPERIMENT.getId(), "1", instance1PlotExperiment);
		this.savePhenotype(instance1SubObsExperiment2);

		this.instance2 = this.testDataInitializer.createInstanceExperimentModel(this.summary, 2, "2");
		this.testDataInitializer.addExperimentProp(instance2, TermId.BLOCK_ID.getId(), "", 1);
		this.testDataInitializer.createTestExperiment(plot, null, TermId.PLOT_EXPERIMENT.getId(), "1", instance2);

		this.instance3 = this.testDataInitializer.createInstanceExperimentModel(this.summary, 3, "3");
	}

	private void savePhenotype(final ExperimentModel experiment) {
		final CVTerm trait1 = this.testDataInitializer.createTrait(RandomStringUtils.randomAlphabetic(10));
		this.testDataInitializer.addPhenotypes(Collections.singletonList(experiment), trait1.getCvTermId(), "100");
	}

}
