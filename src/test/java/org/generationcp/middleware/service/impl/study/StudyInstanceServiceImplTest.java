package org.generationcp.middleware.service.impl.study;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.data.initializer.StudyTestDataInitializer;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.DatasetValues;
import org.generationcp.middleware.domain.dms.ExperimentDesignType;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.dms.VariableTypeList;
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
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.service.api.study.StudyInstanceService;
import org.generationcp.middleware.utils.test.IntegrationTestDataInitializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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
	private StudyReference studyReference;
	private DatasetReference environmentDataset;

	@Before
	public void setup() throws Exception {

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

	}

	@Test
	public void testCreateStudyInstance() {

		final int instanceNumber = 1;
		this.studyInstanceService.createStudyInstance(this.cropType, this.environmentDataset.getId(), instanceNumber);

		// Need to flush session to sync with underlying database before querying
		this.sessionProvder.getSession().flush();

		final List<StudyInstance> studyInstances =
			this.studyInstanceService.getStudyInstances(this.studyReference.getId());

		final StudyInstance studyInstance = studyInstances.get(0);
		assertEquals(instanceNumber, studyInstance.getInstanceNumber());
		assertNotNull(studyInstance.getInstanceDbId());
		assertNotNull(studyInstance.getExperimentId());
		assertNotNull(studyInstance.getLocationId());
		assertFalse(studyInstance.isHasFieldmap());
		assertEquals("Unspecified Location", studyInstance.getLocationName());
		assertEquals("NOLOC", studyInstance.getLocationAbbreviation());
		assertNull(studyInstance.getCustomLocationAbbreviation());
	}

	@Test
	public void testGetStudyInstances() {

		final DmsProject study =
			this.testDataInitializer.createDmsProject("Study1", "Study-Description", null, this.daoFactory.getDmsProjectDAO().getById(1), null);
		final DmsProject environmentDataset =
			this.testDataInitializer
				.createDmsProject("Summary Dataset", "Summary Dataset-Description", study, study, DatasetTypeEnum.SUMMARY_DATA);
		final DmsProject plotDataset =
			this.testDataInitializer
				.createDmsProject("Plot Dataset", "Plot Dataset-Description", study, study, DatasetTypeEnum.PLOT_DATA);
		final DmsProject subObsDataset =
			this.testDataInitializer
				.createDmsProject("Plot Dataset", "Plot Dataset-Description", study, plotDataset, DatasetTypeEnum.QUADRAT_SUBOBSERVATIONS);


		final Geolocation instance1 = this.testDataInitializer.createTestGeolocation("1", 1);
		final Geolocation instance2 = this.testDataInitializer.createTestGeolocation("2", 2);
		final Geolocation instance3 = this.testDataInitializer.createTestGeolocation("3", 3);
		this.testDataInitializer.addGeolocationProp(instance1,  TermId.EXPERIMENT_DESIGN_FACTOR.getId(),  ExperimentDesignType.RANDOMIZED_COMPLETE_BLOCK.getTermId().toString(), 1);
		this.testDataInitializer.addGeolocationProp(instance2,  TermId.BLOCK_ID.getId(), RandomStringUtils.randomAlphabetic(5), 1);

		// Instance 1
		this.testDataInitializer.createTestExperiment(environmentDataset, instance1, TermId.SUMMARY_EXPERIMENT.getId(), "0", null);
		final ExperimentModel instance1PlotExperiment =
			this.testDataInitializer.createTestExperiment(plotDataset, instance1, TermId.PLOT_EXPERIMENT.getId(), "1", null);
		// Create 2 Sub-obs records
		final ExperimentModel instance1SubObsExperiment1 =
			this.testDataInitializer.createTestExperiment(subObsDataset, instance1, TermId.PLOT_EXPERIMENT.getId(), "1", instance1PlotExperiment);
		this.savePhenotype(instance1SubObsExperiment1);
		final ExperimentModel instance1SubObsExperiment2 = this.testDataInitializer
			.createTestExperiment(subObsDataset, instance1, TermId.PLOT_EXPERIMENT.getId(), "1", instance1PlotExperiment);
		this.savePhenotype(instance1SubObsExperiment2);

		// Instance 2
		this.testDataInitializer.createTestExperiment(environmentDataset, instance2, TermId.SUMMARY_EXPERIMENT.getId(), "0", null);
		final ExperimentModel instance2PlotExperiment =
			this.testDataInitializer.createTestExperiment(plotDataset, instance2, TermId.PLOT_EXPERIMENT.getId(), "1", null);

		// Instance 3 has no plot experiments
		this.testDataInitializer.createTestExperiment(environmentDataset, instance3, TermId.SUMMARY_EXPERIMENT.getId(), "0", null);

		final List<StudyInstance> studyInstances = this.studyInstanceService.getStudyInstances(study.getProjectId());

		Assert.assertEquals(3, studyInstances.size());

		final StudyInstance studyInstance1 = studyInstances.get(0);
		Assert.assertEquals(instance1.getLocationId().intValue(), studyInstance1.getInstanceDbId());
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
		Assert.assertEquals(instance2.getLocationId().intValue(), studyInstance2.getInstanceDbId());
		Assert.assertEquals(2, studyInstance2.getInstanceNumber());
		Assert.assertNull(studyInstance2.getCustomLocationAbbreviation());
		Assert.assertEquals("ALB", studyInstance2.getLocationAbbreviation());
		Assert.assertEquals("Albania", studyInstance2.getLocationName());
		Assert.assertTrue(studyInstance2.isHasFieldmap());
		Assert.assertTrue(studyInstance2.isHasExperimentalDesign());
		Assert.assertTrue(studyInstance2.getCanBeDeleted());
		Assert.assertFalse(studyInstance2.isHasMeasurements());

		final StudyInstance studyInstance3 = studyInstances.get(2);
		Assert.assertEquals(instance3.getLocationId().intValue(), studyInstance3.getInstanceDbId());
		Assert.assertEquals(3, studyInstance3.getInstanceNumber());
		Assert.assertNull(studyInstance3.getCustomLocationAbbreviation());
		Assert.assertEquals("DZA", studyInstance3.getLocationAbbreviation());
		Assert.assertEquals("Algeria", studyInstance3.getLocationName());
		Assert.assertFalse(studyInstance3.isHasFieldmap());
		Assert.assertFalse(studyInstance3.isHasExperimentalDesign());
		Assert.assertTrue(studyInstance3.getCanBeDeleted());
		Assert.assertFalse(studyInstance3.isHasMeasurements());
	}

	private void savePhenotype(final ExperimentModel experiment){
		final CVTerm trait1 = this.testDataInitializer.createTrait(RandomStringUtils.randomAlphabetic(10));
		this.testDataInitializer.addPhenotypes(Collections.singletonList(experiment), trait1.getCvTermId(), "100");
	}

}
