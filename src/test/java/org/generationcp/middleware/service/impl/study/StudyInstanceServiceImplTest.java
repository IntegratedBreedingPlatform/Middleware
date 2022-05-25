package org.generationcp.middleware.service.impl.study;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.data.initializer.StudyTestDataInitializer;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.DatasetValues;
import org.generationcp.middleware.domain.dms.ExperimentDesignType;
import org.generationcp.middleware.domain.dms.InstanceDescriptorData;
import org.generationcp.middleware.domain.dms.InstanceObservationData;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.StudyDataManagerImpl;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.study.StudyInstanceService;
import org.generationcp.middleware.service.api.study.generation.ExperimentDesignService;
import org.generationcp.middleware.utils.test.IntegrationTestDataInitializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
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
	private OntologyDataManager ontologyManager;

	@Autowired
	private LocationDataManager locationManager;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private OntologyDataManager ontologyDataManager;

	@Autowired
	private WorkbenchTestDataUtil workbenchTestDataUtil;

	@Autowired
	private StudyInstanceService studyInstanceService;

	@Resource
	private ExperimentDesignService experimentDesignService;

	private DaoFactory daoFactory;
	private StudyDataManagerImpl studyDataManager;
	private StudyTestDataInitializer studyTestDataInitializer;

	private Project commonTestProject;
	private CropType cropType;
	private StudyReference studyReference;
	private DatasetReference environmentDataset;
	private Geolocation instance1;
	private Geolocation instance2;
	private Geolocation instance3;
	private DmsProject study;
	private WorkbenchUser testUser;

	@Before
	public void setup() throws Exception {

		this.studyDataManager = new StudyDataManagerImpl(this.sessionProvder);
		this.daoFactory = new DaoFactory(this.sessionProvder);

		if (this.commonTestProject == null) {
			this.commonTestProject = this.workbenchTestDataUtil.getCommonTestProject();
		}

		this.testDataInitializer = new IntegrationTestDataInitializer(this.sessionProvder, this.workbenchSessionProvider);

		this.studyTestDataInitializer =
			new StudyTestDataInitializer(this.studyDataManager, this.ontologyManager, this.commonTestProject,
				this.locationManager, this.sessionProvder);

		this.cropType = this.workbenchDataManager.getCropTypeByName(CropType.CropEnum.MAIZE.name());

		this.studyReference = this.studyTestDataInitializer.addTestStudy();

		// Create an environment dataset with TRIAL_INSTANCE and LOCATION_NAME properties.
		final VariableTypeList environmentVariables = new VariableTypeList();
		environmentVariables
			.add(new DMSVariableType(TRIAL_INSTANCE, "Trial instance - enumerated (number)", this.ontologyDataManager.getStandardVariable(
				TermId.TRIAL_INSTANCE_FACTOR.getId(), null), 1));
		environmentVariables.add(new DMSVariableType(LOCATION_NAME, "Location Name", this.ontologyDataManager.getStandardVariable(
			TermId.LOCATION_ID.getId(), null), 2));
		final DatasetValues datasetValues = new DatasetValues();
		datasetValues.setName(RandomStringUtils.randomAlphanumeric(10));
		datasetValues.setDescription(RandomStringUtils.randomAlphanumeric(10));
		this.environmentDataset =
			this.studyDataManager
				.addDataSet(this.studyReference.getId(), environmentVariables, datasetValues, null, DatasetTypeEnum.SUMMARY_DATA.getId());

		if (this.instance1 == null) {
			this.instance1 = this.testDataInitializer.createTestGeolocation("1", 1);
			this.instance2 = this.testDataInitializer.createTestGeolocation("2", 2);
			this.instance3 = this.testDataInitializer.createTestGeolocation("3", 3);
		}

		// Null study end date means it's still active
		this.testUser = this.testDataInitializer.createUserForTesting();
		this.study = this.testDataInitializer
			.createStudy("Study1", "Study-Description", 6, this.commonTestProject.getUniqueID(), this.testUser.getUserid().toString(),
				"20180205", null);
		final DmsProject environmentDataset =
			this.testDataInitializer
				.createDmsProject("Environment Dataset", "Environment Dataset-Description", this.study, this.study,
					DatasetTypeEnum.SUMMARY_DATA);
		final Random random = new Random();
		final int location1 = random.nextInt();
		final Geolocation geolocation = this.testDataInitializer.createInstance(environmentDataset, "1", location1);
		this.testDataInitializer.createTestExperiment(this.study, geolocation, TermId.STUDY_EXPERIMENT.getId(), null, null);
	}

	@Test
	public void testCreateStudyInstances() {

		// Create instance 1
		final Integer studyId = this.studyReference.getId();
		final List<StudyInstance> studyInstances =
			this.studyInstanceService.createStudyInstances(this.cropType, studyId, this.environmentDataset.getId(), 0, 2);
		final StudyInstance studyInstance1 = studyInstances.get(0);

		// Need to flush session to sync with underlying database before querying
		this.sessionProvder.getSession().flush();
		assertEquals(1, studyInstance1.getInstanceNumber());
		assertNotNull(studyInstance1.getInstanceId());
		assertNotNull(studyInstance1.getLocationId());
		assertFalse(studyInstance1.isHasFieldmap());
		assertEquals("Unknown", studyInstance1.getLocationName());
		assertEquals("UKN", studyInstance1.getLocationAbbreviation());
		assertNull(studyInstance1.getCustomLocationAbbreviation());
		assertTrue(studyInstance1.getCanBeDeleted());
		assertFalse(studyInstance1.isHasMeasurements());
		assertFalse(studyInstance1.isHasExperimentalDesign());

		// Create instance 2
		final StudyInstance studyInstance2 = studyInstances.get(1);
		// Need to flush session to sync with underlying database before querying
		this.sessionProvder.getSession().flush();
		assertEquals(2, studyInstance2.getInstanceNumber());
		assertNotNull(studyInstance2.getInstanceId());
		assertNotNull(studyInstance2.getLocationId());
		assertFalse(studyInstance2.isHasFieldmap());
		assertEquals("Unknown", studyInstance2.getLocationName());
		assertEquals("UKN", studyInstance2.getLocationAbbreviation());
		assertNull(studyInstance2.getCustomLocationAbbreviation());
		assertTrue(studyInstance2.getCanBeDeleted());
		assertFalse(studyInstance2.isHasMeasurements());
		assertFalse(studyInstance2.isHasExperimentalDesign());

		final List<Geolocation> geolocations =
			this.daoFactory.getGeolocationDao().getEnvironmentGeolocations(studyId);
		Assert.assertEquals(2, geolocations.size());
	}

	@Test
	public void testGetStudyInstances() {

		final DmsProject study = this.createTestStudy(false);

		this.sessionProvder.getSession().flush();

		final List<StudyInstance> studyInstances = this.studyInstanceService.getStudyInstances(study.getProjectId());

		Assert.assertEquals(3, studyInstances.size());

		final StudyInstance studyInstance1 = studyInstances.get(0);
		Assert.assertEquals(this.instance1.getLocationId().intValue(), studyInstance1.getInstanceId());
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
		Assert.assertEquals(this.instance2.getLocationId().intValue(), studyInstance2.getInstanceId());
		Assert.assertEquals(2, studyInstance2.getInstanceNumber());
		Assert.assertNull(studyInstance2.getCustomLocationAbbreviation());
		Assert.assertEquals("ALB", studyInstance2.getLocationAbbreviation());
		Assert.assertEquals("Albania", studyInstance2.getLocationName());
		Assert.assertTrue(studyInstance2.isHasFieldmap());
		Assert.assertTrue(studyInstance2.isHasExperimentalDesign());
		Assert.assertTrue(studyInstance2.getCanBeDeleted());
		Assert.assertFalse(studyInstance2.isHasMeasurements());

		final StudyInstance studyInstance3 = studyInstances.get(2);
		Assert.assertEquals(this.instance3.getLocationId().intValue(), studyInstance3.getInstanceId());
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

		final DmsProject study = this.createTestStudy(false);

		this.sessionProvder.getSession().flush();

		final StudyInstance studyInstance1 =
			this.studyInstanceService.getStudyInstance(study.getProjectId(), this.instance1.getLocationId()).get();
		Assert.assertEquals(this.instance1.getLocationId().intValue(), studyInstance1.getInstanceId());
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
			this.studyInstanceService.getStudyInstance(study.getProjectId(), this.instance2.getLocationId()).get();
		Assert.assertEquals(this.instance2.getLocationId().intValue(), studyInstance2.getInstanceId());
		Assert.assertEquals(2, studyInstance2.getInstanceNumber());
		Assert.assertNull(studyInstance2.getCustomLocationAbbreviation());
		Assert.assertEquals("ALB", studyInstance2.getLocationAbbreviation());
		Assert.assertEquals("Albania", studyInstance2.getLocationName());
		Assert.assertTrue(studyInstance2.isHasFieldmap());
		Assert.assertTrue(studyInstance2.isHasExperimentalDesign());
		Assert.assertTrue(studyInstance2.getCanBeDeleted());
		Assert.assertFalse(studyInstance2.isHasMeasurements());

		final StudyInstance studyInstance3 =
			this.studyInstanceService.getStudyInstance(study.getProjectId(), this.instance3.getLocationId()).get();
		Assert.assertEquals(this.instance3.getLocationId().intValue(), studyInstance3.getInstanceId());
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
	public void testDeleteStudyInstances() {
		final DmsProject study =
			this.testDataInitializer
				.createDmsProject("Study1", "Study-Description", null, this.daoFactory.getDmsProjectDAO().getById(1), null);
		final DmsProject environmentDataset =
			this.testDataInitializer
				.createDmsProject("Summary Dataset", "Summary Dataset-Description", study, study, DatasetTypeEnum.SUMMARY_DATA);
		final DmsProject plotDataset =
			this.testDataInitializer
				.createDmsProject("Plot Dataset", "Plot Dataset-Description", study, study, DatasetTypeEnum.PLOT_DATA);

		final Geolocation instance1 = this.testDataInitializer.createTestGeolocation("1", 1);
		final Geolocation instance2 = this.testDataInitializer.createTestGeolocation("2", 2);
		final Geolocation instance3 = this.testDataInitializer.createTestGeolocation("3", 3);
		this.testDataInitializer.addGeolocationProp(instance1, TermId.EXPERIMENT_DESIGN_FACTOR.getId(),
			ExperimentDesignType.RANDOMIZED_COMPLETE_BLOCK.getTermId().toString(), 1);
		this.testDataInitializer.addGeolocationProp(instance2, TermId.EXPERIMENT_DESIGN_FACTOR.getId(),
			ExperimentDesignType.RANDOMIZED_COMPLETE_BLOCK.getTermId().toString(), 1);
		this.testDataInitializer.addGeolocationProp(instance3, TermId.EXPERIMENT_DESIGN_FACTOR.getId(),
			ExperimentDesignType.RANDOMIZED_COMPLETE_BLOCK.getTermId().toString(), 1);

		final Integer studyExperimentId =
			this.createTestExperiments(study, environmentDataset, plotDataset, instance1, instance2, instance3);
		final Integer studyId = study.getProjectId();

		this.sessionProvder.getSession().flush();

		// Delete Instance 2
		final Integer instance2InstanceId = instance2.getLocationId();
		this.studyInstanceService.deleteStudyInstances(studyId, Arrays.asList(instance2InstanceId));
		this.sessionProvder.getSession().flush();

		List<StudyInstance> studyInstances =
			this.studyInstanceService.getStudyInstances(studyId);
		Assert.assertTrue(studyInstances.get(0).isHasExperimentalDesign());
		Assert.assertEquals(2, studyInstances.size());
		final Integer instance1LocationId = instance1.getLocationId();
		Assert.assertEquals(instance1LocationId,
			this.daoFactory.getExperimentDao().getById(studyExperimentId).getGeoLocation().getLocationId());

		// Confirm geolocation and its properties have been deleted
		Assert.assertNull(this.daoFactory.getGeolocationDao().getById(instance2InstanceId));
		Assert.assertTrue(CollectionUtils.isEmpty(this.daoFactory.getGeolocationPropertyDao().getByGeolocation(instance2InstanceId)));

		// Delete Instance 1 - study experiment Geolocation ID will be updated to next available geolocation
		this.studyInstanceService.deleteStudyInstances(studyId, Arrays.asList(instance1LocationId));
		this.sessionProvder.getSession().flush();

		studyInstances =
			this.studyInstanceService.getStudyInstances(studyId);
		Assert.assertEquals(1, studyInstances.size());
		Assert.assertNotEquals(2, studyInstances.get(0).getInstanceNumber());
		Assert.assertNotEquals(instance2InstanceId.intValue(), studyInstances.get(0).getInstanceId());
		// Confirm geolocation and its properties have been deleted
		Assert.assertNull(this.daoFactory.getGeolocationDao().getById(instance1LocationId));
		Assert.assertTrue(CollectionUtils.isEmpty(this.daoFactory.getGeolocationPropertyDao().getByGeolocation(instance1LocationId)));

		// Delete Instance 3 - should throw exception
		final Integer instance3LocationId = instance3.getLocationId();
		try {
			this.studyInstanceService.deleteStudyInstances(studyId, Arrays.asList(instance3LocationId));
			Assert.fail("Should have thrown exception when attempting to delete last environment.");
		} catch (final MiddlewareQueryException e) {
			// Perform assertions outside
		}
	}

	@Test
	public void testDeleteStudyInstances_NoInstancesWithExpDesignRemaining() {
		final DmsProject study =
			this.testDataInitializer
				.createDmsProject("Study1", "Study-Description", null, this.daoFactory.getDmsProjectDAO().getById(1), null);
		final DmsProject environmentDataset =
			this.testDataInitializer
				.createDmsProject("Summary Dataset", "Summary Dataset-Description", study, study, DatasetTypeEnum.SUMMARY_DATA);
		final DmsProject plotDataset =
			this.testDataInitializer
				.createDmsProject("Plot Dataset", "Plot Dataset-Description", study, study, DatasetTypeEnum.PLOT_DATA);

		final Geolocation instance1 = this.testDataInitializer.createTestGeolocation("1", 1);
		final Geolocation instance2 = this.testDataInitializer.createTestGeolocation("2", 2);
		final Geolocation instance3 = this.testDataInitializer.createTestGeolocation("3", 3);

		this.testDataInitializer.addGeolocationProp(instance1, TermId.EXPERIMENT_DESIGN_FACTOR.getId(),
			ExperimentDesignType.RANDOMIZED_COMPLETE_BLOCK.getTermId().toString(), 1);
		this.testDataInitializer.addGeolocationProp(instance2, TermId.EXPERIMENT_DESIGN_FACTOR.getId(),
			ExperimentDesignType.RANDOMIZED_COMPLETE_BLOCK.getTermId().toString(), 1);
		this.testDataInitializer.addGeolocationProp(instance3, TermId.EXPERIMENT_DESIGN_FACTOR.getId(),
			ExperimentDesignType.RANDOMIZED_COMPLETE_BLOCK.getTermId().toString(), 1);

		final Integer studyExperimentId =
			this.createTestExperiments(study, environmentDataset, plotDataset, instance1, instance2, instance3);
		final Integer studyId = study.getProjectId();

		this.sessionProvder.getSession().flush();

		// Delete Instance 1,2,3
		final Integer instance1InstanceId = instance1.getLocationId();
		final Integer instance2InstanceId = instance2.getLocationId();
		this.studyInstanceService.deleteStudyInstances(studyId, Arrays.asList(instance1InstanceId, instance2InstanceId));
		this.sessionProvder.getSession().flush();

		final List<StudyInstance> studyInstances =
			this.studyInstanceService.getStudyInstances(studyId);

		final boolean hasExperimentalDesign = this.experimentDesignService.getStudyExperimentDesignTypeTermId(studyId).isPresent();
		Assert.assertFalse(hasExperimentalDesign);
		Assert.assertFalse(studyInstances.get(0).isHasExperimentalDesign());
		Assert.assertEquals(1, studyInstances.size());

	}

	@Test
	public void testAddInstanceObservation() {

		final InstanceObservationData observationData = this.createTestObservationData(TermId.BLOCK_NAME.getId());
		final InstanceObservationData addedObservationData =
			this.studyInstanceService.addInstanceObservation(observationData);

		final Optional<InstanceObservationData>
			result = this.studyInstanceService
			.getInstanceObservation(addedObservationData.getInstanceId(), addedObservationData.getInstanceObservationId(),
				TermId.BLOCK_NAME.getId());

		Assert.assertTrue(result.isPresent());
		Assert.assertEquals(observationData.getValue(), result.get().getValue());
	}

	@Test
	public void testAddInstanceDescriptor() {

		final InstanceDescriptorData instanceDescriptorData = this.createTestDescriptorData(TermId.BLOCK_NAME.getId());
		final InstanceDescriptorData addedDescriptorData =
			this.studyInstanceService.addInstanceDescriptorData(instanceDescriptorData);

		final Optional<InstanceDescriptorData>
			result = this.studyInstanceService
			.getInstanceDescriptorData(addedDescriptorData.getInstanceId(), addedDescriptorData.getInstanceDescriptorDataId(),
				TermId.BLOCK_NAME.getId());

		Assert.assertTrue(result.isPresent());
		Assert.assertEquals(instanceDescriptorData.getValue(), result.get().getValue());
	}

	@Test
	public void testAddInstanceDescriptor_GeolocationMetadata() {

		final InstanceDescriptorData instanceDescriptorData = this.createTestDescriptorData(TermId.ALTITUDE.getId());
		final InstanceDescriptorData addedDescriptorData =
			this.studyInstanceService.addInstanceDescriptorData(instanceDescriptorData);

		final Optional<InstanceDescriptorData>
			result = this.studyInstanceService
			.getInstanceDescriptorData(addedDescriptorData.getInstanceId(), addedDescriptorData.getInstanceDescriptorDataId(),
				TermId.ALTITUDE.getId());

		Assert.assertTrue(result.isPresent());
		Assert.assertEquals(Double.valueOf(instanceDescriptorData.getValue()).toString(), result.get().getValue());
	}

	@Test
	public void testUpdateInstanceObservation() {

		final InstanceObservationData instanceObservationData = this.createTestObservationData(TermId.BLOCK_NAME.getId());
		final InstanceObservationData addedObservationData =
			this.studyInstanceService.addInstanceObservation(instanceObservationData);

		final String oldValue = addedObservationData.getValue();
		final String newValue = RandomStringUtils.randomNumeric(10);

		addedObservationData.setValue(newValue);
		this.studyInstanceService.updateInstanceObservation(addedObservationData);

		final Optional<InstanceObservationData>
			result = this.studyInstanceService
			.getInstanceObservation(addedObservationData.getInstanceId(), addedObservationData.getInstanceObservationId(),
				TermId.BLOCK_NAME.getId());

		Assert.assertTrue(result.isPresent());
		Assert.assertEquals(newValue, result.get().getValue());
	}

	@Test
	public void testUpdateInstanceDescriptor() {

		final InstanceDescriptorData instanceDescriptorData = this.createTestDescriptorData(TermId.BLOCK_NAME.getId());
		final InstanceDescriptorData addedDescriptorData =
			this.studyInstanceService.addInstanceDescriptorData(instanceDescriptorData);

		final String oldValue = addedDescriptorData.getValue();
		final String newValue = RandomStringUtils.randomNumeric(10);

		addedDescriptorData.setValue(newValue);
		this.studyInstanceService.updateInstanceDescriptorData(addedDescriptorData);

		final Optional<InstanceDescriptorData>
			result = this.studyInstanceService
			.getInstanceDescriptorData(addedDescriptorData.getInstanceId(), addedDescriptorData.getInstanceDescriptorDataId(),
				TermId.BLOCK_NAME.getId());

		Assert.assertTrue(result.isPresent());
		Assert.assertEquals(newValue, result.get().getValue());

	}

	private InstanceObservationData createTestObservationData(final int variableId) {
		final StudyInstance studyInstance = this.createStudyInstance();
		final String value = RandomStringUtils.randomNumeric(5);

		final InstanceObservationData instanceObservationData = new InstanceObservationData();
		instanceObservationData.setValue(value);
		instanceObservationData.setInstanceId(studyInstance.getInstanceId());
		instanceObservationData.setVariableId(variableId);
		return instanceObservationData;
	}

	private InstanceDescriptorData createTestDescriptorData(final int variableId) {
		final StudyInstance studyInstance = this.createStudyInstance();
		final String value = RandomStringUtils.randomNumeric(5);

		final InstanceDescriptorData instanceDescriptorData = new InstanceDescriptorData();
		instanceDescriptorData.setValue(value);
		instanceDescriptorData.setInstanceId(studyInstance.getInstanceId());
		instanceDescriptorData.setVariableId(variableId);
		return instanceDescriptorData;
	}

	private StudyInstance createStudyInstance() {
		// Create an instance
		final Integer studyId = this.studyReference.getId();
		final List<StudyInstance> studyInstances =
			this.studyInstanceService.createStudyInstances(this.cropType, studyId, this.environmentDataset.getId(), 0, 1);
		return studyInstances.get(0);
	}

	private DmsProject createTestStudy(final boolean hasMeansDataset) {
		final DmsProject study =
			this.testDataInitializer
				.createDmsProject("Study1", "Study-Description", null, this.daoFactory.getDmsProjectDAO().getById(1), null);
		final DmsProject environmentDataset =
			this.testDataInitializer
				.createDmsProject("Summary Dataset", "Summary Dataset-Description", study, study, DatasetTypeEnum.SUMMARY_DATA);
		final DmsProject plotDataset =
			this.testDataInitializer
				.createDmsProject("Plot Dataset", "Plot Dataset-Description", study, study, DatasetTypeEnum.PLOT_DATA);
		final DmsProject subObsDataset =
			this.testDataInitializer
				.createDmsProject("Subobs Dataset", "Subobs Dataset-Description", study, plotDataset,
					DatasetTypeEnum.QUADRAT_SUBOBSERVATIONS);

		this.testDataInitializer.addGeolocationProp(this.instance1, TermId.EXPERIMENT_DESIGN_FACTOR.getId(),
			ExperimentDesignType.RANDOMIZED_COMPLETE_BLOCK.getTermId().toString(), 1);
		this.testDataInitializer.addGeolocationProp(this.instance2, TermId.BLOCK_ID.getId(), RandomStringUtils.randomAlphabetic(5), 1);

		// Instance 1
		this.testDataInitializer.createTestExperiment(environmentDataset, this.instance1, TermId.SUMMARY_EXPERIMENT.getId(), "0", null);
		final ExperimentModel instance1PlotExperiment =
			this.testDataInitializer.createTestExperiment(plotDataset, this.instance1, TermId.PLOT_EXPERIMENT.getId(), "1", null);
		// Create 2 Sub-obs records
		final ExperimentModel instance1SubObsExperiment1 =
			this.testDataInitializer
				.createTestExperiment(subObsDataset, this.instance1, TermId.PLOT_EXPERIMENT.getId(), "1", instance1PlotExperiment);
		this.savePhenotype(instance1SubObsExperiment1);
		final ExperimentModel instance1SubObsExperiment2 = this.testDataInitializer
			.createTestExperiment(subObsDataset, this.instance1, TermId.PLOT_EXPERIMENT.getId(), "1", instance1PlotExperiment);
		this.savePhenotype(instance1SubObsExperiment2);

		if (hasMeansDataset) {
			final DmsProject meansDataset =
				this.testDataInitializer
					.createDmsProject("Means Dataset", "Means Dataset-Description", study, study, DatasetTypeEnum.MEANS_DATA);
			this.testDataInitializer.createTestExperiment(meansDataset, this.instance1, TermId.PLOT_EXPERIMENT.getId(), "1", null);
		}

		// Instance 2
		this.testDataInitializer.createTestExperiment(environmentDataset, this.instance2, TermId.SUMMARY_EXPERIMENT.getId(), "0", null);
		this.testDataInitializer.createTestExperiment(plotDataset, this.instance2, TermId.PLOT_EXPERIMENT.getId(), "1", null);

		// Instance 3 has no plot experiments
		this.testDataInitializer.createTestExperiment(environmentDataset, this.instance3, TermId.SUMMARY_EXPERIMENT.getId(), "0", null);
		this.sessionProvder.getSession().flush();
		return study;
	}

	private Integer createTestExperiments(final DmsProject study, final DmsProject environmentDataset, final DmsProject plotDataset,
		final Geolocation instance1, final Geolocation instance2, final Geolocation instance3) {
		// Study experiment
		final ExperimentModel studyExperiment =
			this.testDataInitializer.createTestExperiment(study, instance1, TermId.STUDY_EXPERIMENT.getId(), "0", null);

		// Instance 1
		this.testDataInitializer.createTestExperiment(environmentDataset, instance1, TermId.SUMMARY_EXPERIMENT.getId(), "0", null);
		final ExperimentModel instance1PlotExperiment =
			this.testDataInitializer.createTestExperiment(plotDataset, instance1, TermId.PLOT_EXPERIMENT.getId(), "1", null);
		this.savePhenotype(instance1PlotExperiment);

		// Instance 2
		this.testDataInitializer.createTestExperiment(environmentDataset, instance2, TermId.SUMMARY_EXPERIMENT.getId(), "0", null);
		final ExperimentModel instance2PlotExperiment =
			this.testDataInitializer.createTestExperiment(plotDataset, instance2, TermId.PLOT_EXPERIMENT.getId(), "1", null);

		// Instance 3 has no plot experiments
		this.testDataInitializer.createTestExperiment(environmentDataset, instance3, TermId.SUMMARY_EXPERIMENT.getId(), "0", null);

		return studyExperiment.getNdExperimentId();
	}

	private void savePhenotype(final ExperimentModel experiment) {
		final CVTerm trait1 = this.testDataInitializer.createTrait(RandomStringUtils.randomAlphabetic(10));
		this.testDataInitializer.addPhenotypes(Collections.singletonList(experiment), trait1.getCvTermId(), "100");
	}

	@Test
	public void testGetStudyInstancesWithMeansDataset() {

		final DmsProject study = this.createTestStudy(true);

		final StudyInstance studyInstance1 =
			this.studyInstanceService.getStudyInstance(study.getProjectId(), this.instance1.getLocationId()).get();
		Assert.assertEquals(this.instance1.getLocationId().intValue(), studyInstance1.getInstanceId());
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
			this.studyInstanceService.getStudyInstance(study.getProjectId(), this.instance2.getLocationId()).get();
		Assert.assertEquals(this.instance2.getLocationId().intValue(), studyInstance2.getInstanceId());
		Assert.assertEquals(2, studyInstance2.getInstanceNumber());
		Assert.assertNull(studyInstance2.getCustomLocationAbbreviation());
		Assert.assertEquals("ALB", studyInstance2.getLocationAbbreviation());
		Assert.assertEquals("Albania", studyInstance2.getLocationName());
		Assert.assertTrue(studyInstance2.isHasFieldmap());
		Assert.assertTrue(studyInstance2.isHasExperimentalDesign());
		// Instance deletion not allowed because instance has means dataset
		Assert.assertTrue(studyInstance2.getCanBeDeleted());
		Assert.assertFalse(studyInstance2.isHasMeasurements());

		final StudyInstance studyInstance3 =
			this.studyInstanceService.getStudyInstance(study.getProjectId(), this.instance3.getLocationId()).get();
		Assert.assertEquals(this.instance3.getLocationId().intValue(), studyInstance3.getInstanceId());
		Assert.assertEquals(3, studyInstance3.getInstanceNumber());
		Assert.assertNull(studyInstance3.getCustomLocationAbbreviation());
		Assert.assertEquals("DZA", studyInstance3.getLocationAbbreviation());
		Assert.assertEquals("Algeria", studyInstance3.getLocationName());
		Assert.assertFalse(studyInstance3.isHasFieldmap());
		Assert.assertFalse(studyInstance3.isHasExperimentalDesign());
		// Instance deletion not allowed because instance has means dataset
		Assert.assertTrue(studyInstance3.getCanBeDeleted());
		Assert.assertFalse(studyInstance3.isHasMeasurements());
	}
}
