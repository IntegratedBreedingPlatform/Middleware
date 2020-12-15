package org.generationcp.middleware.service.impl.study;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.domain.dms.StudySummary;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.study.StudyDetailsDto;
import org.generationcp.middleware.service.api.study.StudyInstanceDto;
import org.generationcp.middleware.service.api.study.StudySearchFilter;
import org.generationcp.middleware.service.api.study.StudyService;
import org.generationcp.middleware.utils.test.IntegrationTestDataInitializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class StudyServiceImplIntegrationTest extends IntegrationTestBase {

	@Autowired
	private StudyService studyService;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private WorkbenchTestDataUtil workbenchTestDataUtil;

	private IntegrationTestDataInitializer testDataInitializer;
	private DmsProject study;
	private DmsProject plot;
	private ExperimentModel studyExperiment;
	private CVTerm testTrait;
	private CropType crop;
	private Project commonTestProject;
	private WorkbenchUser testUser;
	private DaoFactory daoFactory;

	@Before
	public void setUp() {
		this.daoFactory = new DaoFactory(this.sessionProvder);

		this.workbenchTestDataUtil.setUpWorkbench();
		if (this.commonTestProject == null) {
			this.commonTestProject = this.workbenchTestDataUtil.getCommonTestProject();
			this.crop = this.workbenchDataManager.getProjectByUuid(this.commonTestProject.getUniqueID()).getCropType();
		}

		this.testDataInitializer = new IntegrationTestDataInitializer(this.sessionProvder, this.workbenchSessionProvider);
		this.testUser = this.testDataInitializer.createUserForTesting();
		this.study = this.testDataInitializer
			.createStudy("Study1", "Study-Description", 6, this.commonTestProject.getUniqueID(), this.testUser.getUserid().toString());

		this.plot = this.testDataInitializer
			.createDmsProject("Plot Dataset", "Plot Dataset-Description", this.study, this.study, DatasetTypeEnum.PLOT_DATA);
		final DmsProject environmentDataset =
			this.testDataInitializer
				.createDmsProject("Environment Dataset", "Environment Dataset-Description", this.study, this.study,
					DatasetTypeEnum.SUMMARY_DATA);
		final Random random = new Random();
		final int location1 = random.nextInt();
		final Geolocation geolocation = this.testDataInitializer.createInstance(environmentDataset, "1", location1);
		this.studyExperiment =
			this.testDataInitializer.createTestExperiment(this.study, geolocation, TermId.STUDY_EXPERIMENT.getId(), null, null);
		this.testTrait = this.testDataInitializer.createTrait("SomeTrait");
	}

	@Test
	public void testCountTotalObservationUnits() {

		final Geolocation geolocation = this.testDataInitializer.createTestGeolocation("1", 101);
		this.testDataInitializer.createTestExperimentsWithStock(this.study, this.plot, null, geolocation, 5);

		Assert.assertEquals(5, this.studyService.countTotalObservationUnits(this.study.getProjectId(), geolocation.getLocationId()));
	}

	@Test
	public void testHasMeasurementDataEntered() {
		final Geolocation geolocation = this.testDataInitializer.createTestGeolocation("1", 101);
		final List<ExperimentModel> experimentModels =
			this.testDataInitializer.createTestExperimentsWithStock(this.study, this.plot, null, geolocation, 5);

		assertFalse(
			this.studyService
				.hasMeasurementDataEntered(Collections.singletonList(this.testTrait.getCvTermId()), this.study.getProjectId()));

		this.testDataInitializer.addPhenotypes(experimentModels, this.testTrait.getCvTermId(), RandomStringUtils.randomNumeric(5));
		// Need to flush session to sync with underlying database before querying
		this.sessionProvder.getSession().flush();
		Assert.assertTrue(
			this.studyService
				.hasMeasurementDataEntered(Collections.singletonList(this.testTrait.getCvTermId()), this.study.getProjectId()));
	}

	@Test
	public void testHasMeasurementDataOnEnvironment() {
		final Geolocation geolocation = this.testDataInitializer.createTestGeolocation("1", 101);
		final List<ExperimentModel> experimentModels =
			this.testDataInitializer.createTestExperimentsWithStock(this.study, this.plot, null, geolocation, 5);
		assertFalse(this.studyService.hasMeasurementDataOnEnvironment(this.study.getProjectId(), geolocation.getLocationId()));

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
		final StudyDetailsDto studyDetailsDto = this.studyService.getStudyDetailsByInstance(geolocation.getLocationId());
		Assert.assertTrue(CollectionUtils.isEmpty(studyDetailsDto.getContacts()));
		Assert.assertEquals(locationId, studyDetailsDto.getMetadata().getLocationId().intValue());
		Assert.assertEquals(geolocation.getLocationId(), studyDetailsDto.getMetadata().getStudyDbId());
		Assert.assertEquals(this.study.getProjectId(), studyDetailsDto.getMetadata().getTrialDbId());
		Assert.assertEquals(this.study.getName() + " Environment Number 1", studyDetailsDto.getMetadata().getStudyName());


	}

	@Test
	public void testGetStudyInstances() {
		final DmsProject environmentDataset =
			this.testDataInitializer
				.createDmsProject("Summary Dataset", "Summary Dataset-Description", this.study, this.study, DatasetTypeEnum.SUMMARY_DATA);

		final int locationId = 101;
		final Geolocation geolocation = this.testDataInitializer.createTestGeolocation("1", locationId);
		this.testDataInitializer
			.createTestExperiment(environmentDataset, geolocation, TermId.TRIAL_ENVIRONMENT_EXPERIMENT.getId(), "0", null);
		final StudySearchFilter studySearchFilter = new StudySearchFilter().withStudyDbId(geolocation.getLocationId().toString())
			.withProgramDbId(this.study.getProgramUUID());
		final Pageable pageable = new PageRequest(0, 20, new Sort(Sort.Direction.ASC, "trialName"));
		final List<StudyInstanceDto> studyDetailsDtoList = this.studyService.getStudyInstances(studySearchFilter, pageable);
		Assert.assertNotNull(studyDetailsDtoList);
		Assert.assertEquals(1, studyDetailsDtoList.size());
		final StudyInstanceDto studyInstanceDto = studyDetailsDtoList.get(0);
		assertFalse(CollectionUtils.isEmpty(studyInstanceDto.getContacts()));
		Assert.assertEquals(String.valueOf(locationId), studyInstanceDto.getLocationDbId());
		Assert.assertEquals(String.valueOf(geolocation.getLocationId()), studyInstanceDto.getStudyDbId());
		Assert.assertEquals(String.valueOf(this.study.getProjectId()), studyInstanceDto.getTrialDbId());
		Assert.assertEquals(this.study.getName() + " Environment Number 1", studyInstanceDto.getStudyName());
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


		final StudyDetailsDto studyDetailsDto = this.studyService.getStudyDetailsByInstance(geolocation.getLocationId());

		assertFalse(CollectionUtils.isEmpty(studyDetailsDto.getContacts()));
		Assert.assertEquals(user.getUserid(), studyDetailsDto.getContacts().get(0).getUserId());
		Assert.assertEquals(locationId, studyDetailsDto.getMetadata().getLocationId().intValue());
		Assert.assertEquals(geolocation.getLocationId(), studyDetailsDto.getMetadata().getStudyDbId());
		Assert.assertEquals(this.study.getProjectId(), studyDetailsDto.getMetadata().getTrialDbId());
		Assert.assertEquals(this.study.getName() + " Environment Number 1", studyDetailsDto.getMetadata().getStudyName());
	}

	@Test
	public void testGetStudyDetailsForGeolocationWithEnvConditionAndDetails_OK() {
		final int locationId = 101;

		final Geolocation geolocation = this.testDataInitializer.createTestGeolocation("1", locationId);

		final DmsProject dmsProject = this.daoFactory.getDmsProjectDAO()
			.getDatasetsByTypeForStudy(this.study.getProjectId(), DatasetTypeEnum.SUMMARY_DATA.getId()).get(0);

		final ExperimentModel testExperiment = this.testDataInitializer
			.createTestExperiment(dmsProject, geolocation, TermId.TRIAL_ENVIRONMENT_EXPERIMENT.getId(), "0", null);

		//Add 'Crop_season_Code' as environment details with 'Wet season' as value
		this.testDataInitializer.addProjectProp(dmsProject, TermId.SEASON_VAR.getId(), TermId.SEASON_VAR.name(), VariableType.ENVIRONMENT_DETAIL, null, 6);
		this.testDataInitializer.addGeolocationProp(geolocation, TermId.SEASON_VAR.getId(), String.valueOf(TermId.SEASON_WET.getId()), 1);

		//Add 'IrrigMethod_text' as environment details
		final CVTerm irrMethodText = this.daoFactory.getCvTermDao().getById(8700);
		assertNotNull(irrMethodText);

		this.testDataInitializer.addProjectProp(dmsProject, irrMethodText.getCvTermId(), irrMethodText.getName(), VariableType.ENVIRONMENT_DETAIL, null, 6);

		final String irrMethodTextValue = UUID.randomUUID().toString();
		this.testDataInitializer.addGeolocationProp(geolocation, irrMethodText.getCvTermId(), irrMethodTextValue, 1);

		//Add 'Selection_Trait' as environment condition with 'Drought tolerance' as value
		final CVTerm selectionTrait = this.daoFactory.getCvTermDao().getById(17290);
		assertNotNull(selectionTrait);

		final CVTerm droughtTolerance = this.daoFactory.getCvTermDao().getById(17285);
		assertNotNull(droughtTolerance);

		this.testDataInitializer.addProjectProp(dmsProject, selectionTrait.getCvTermId(), selectionTrait.getName(), VariableType.ENVIRONMENT_CONDITION, null, 6);
		this.testDataInitializer.addPhenotypes(Arrays.asList(testExperiment), selectionTrait.getCvTermId(), droughtTolerance.getCvTermId().toString());

		//Add 'SITE_SOIL_PH' as environment details
		final CVTerm siteSoilPH = this.daoFactory.getCvTermDao().getById(8270);
		assertNotNull(siteSoilPH);

		this.testDataInitializer.addProjectProp(dmsProject, siteSoilPH.getCvTermId(), siteSoilPH.getName(), VariableType.ENVIRONMENT_CONDITION, null, 6);

		final String siteSoilPHValue = "5.5";
		this.testDataInitializer.addPhenotypes(Arrays.asList(testExperiment), siteSoilPH.getCvTermId(), siteSoilPHValue);

		this.sessionProvder.getSession().flush();
		this.sessionProvder.getSession().clear();

		final StudyDetailsDto studyDetailsDto = this.studyService.getStudyDetailsByInstance(geolocation.getLocationId());

		Assert.assertEquals(locationId, studyDetailsDto.getMetadata().getLocationId().intValue());
		Assert.assertEquals(geolocation.getLocationId(), studyDetailsDto.getMetadata().getStudyDbId());
		Assert.assertEquals(this.study.getProjectId(), studyDetailsDto.getMetadata().getTrialDbId());
		Assert.assertEquals(this.study.getName() + " Environment Number 1", studyDetailsDto.getMetadata().getStudyName());

		final List<MeasurementVariable> environmentParameters = studyDetailsDto.getEnvironmentParameters();
		assertFalse(CollectionUtils.isEmpty(environmentParameters));
		assertThat(environmentParameters, hasSize(4));
		this.assertEnvironmentParameter(environmentParameters, TermId.SEASON_VAR.getId(), "Crop_season_Code", "1");
		this.assertEnvironmentParameter(environmentParameters, irrMethodText.getCvTermId(), irrMethodText.getName(), irrMethodTextValue);
		this.assertEnvironmentParameter(environmentParameters, selectionTrait.getCvTermId(), selectionTrait.getName(), droughtTolerance.getName());
		this.assertEnvironmentParameter(environmentParameters, siteSoilPH.getCvTermId(), siteSoilPH.getName(), siteSoilPHValue);
	}

	@Test
	public void testCountStudies() throws Exception {
		// Empty filter will retrieve all studies in crop
		final long initialCount = this.studyService.countStudies(new StudySearchFilter());

		// Add new study with new location ID
		final DmsProject newStudy = this.testDataInitializer
			.createStudy("Study2", "Study2-Description", 6, this.commonTestProject.getUniqueID(), this.testUser.getUserid().toString());
		final DmsProject environmentDataset =
			this.testDataInitializer
				.createDmsProject("Environment Dataset", "Environment Dataset-Description", newStudy, newStudy,
					DatasetTypeEnum.SUMMARY_DATA);
		final Random random = new Random();
		final int location1 = random.nextInt();
		final Geolocation geolocation = this.testDataInitializer.createInstance(environmentDataset, "1", location1);
		this.testDataInitializer.createTestExperiment(newStudy, geolocation, TermId.STUDY_EXPERIMENT.getId(), null, null);

		// Flushing to force Hibernate to synchronize with the underlying database
		this.sessionProvder.getSession().flush();

		// New study should be retrieved for empty filter
		Assert.assertEquals((int) initialCount + 1, this.studyService.countStudies(new StudySearchFilter()));
		// Expecting only seeded studies for this test class/method to be retrieved when filtered by programUUID
		Assert
			.assertEquals(2, this.studyService.countStudies(new StudySearchFilter().withProgramDbId(this.commonTestProject.getUniqueID())));
		// Expecting only one to be retrieved when filtered by location
		Assert.assertEquals(1, this.studyService.countStudies(new StudySearchFilter().withLocationDbId(String.valueOf(location1))));
	}

	@Test
	public void testGetStudies() throws Exception {
		// Add new study assigned new location ID
		final DmsProject newStudy = this.testDataInitializer
			.createStudy("Study2", "Study2-Description", 6, this.commonTestProject.getUniqueID(), this.testUser.getUserid().toString());
		final DmsProject environmentDataset =
			this.testDataInitializer
				.createDmsProject("Environment Dataset", "Environment Dataset-Description", newStudy, newStudy,
					DatasetTypeEnum.SUMMARY_DATA);
		final Random random = new Random();
		final Integer location1 = random.nextInt();
		final Geolocation geolocation = this.testDataInitializer.createInstance(environmentDataset, "1", location1);
		final ExperimentModel newStudyExperiment =
			this.testDataInitializer.createTestExperiment(newStudy, geolocation, TermId.STUDY_EXPERIMENT.getId(), null, null);

		// Flushing to force Hibernate to synchronize with the underlying database
		this.sessionProvder.getSession().flush();

		// Expecting only seeded studies for this test class/method to be retrieved when filtered by programUUID, sorted by descending study name
		List<StudySummary> studies =
			this.studyService.getStudies(new StudySearchFilter().withProgramDbId(this.commonTestProject.getUniqueID()), new PageRequest(0, 10, new Sort(Sort.Direction.fromString("desc"), "trialName")));
		Assert.assertEquals(2, studies.size());
		StudySummary study1 = studies.get(1);
		Assert.assertEquals(this.study.getProjectId(), study1.getStudyDbid());
		Assert.assertEquals(this.study.getName(), study1.getName());
		Assert.assertEquals(this.study.getDescription(), study1.getDescription());
		Assert.assertEquals(this.study.getProgramUUID(), study1.getProgramDbId());
		Assert.assertEquals(this.studyExperiment.getObsUnitId(), study1.getObservationUnitId());
		Assert.assertEquals(1, study1.getContacts().size());
		// Workbench person details cannot be retrieved properly from this service
		Assert.assertEquals("Creator", study1.getContacts().get(0).getType());
		final StudySummary study2 = studies.get(0);
		Assert.assertEquals(newStudy.getProjectId(), study2.getStudyDbid());
		Assert.assertEquals(newStudy.getName(), study2.getName());
		Assert.assertEquals(newStudy.getDescription(), study2.getDescription());
		Assert.assertEquals(newStudy.getProgramUUID(), study2.getProgramDbId());
		Assert.assertEquals(newStudyExperiment.getObsUnitId(), study2.getObservationUnitId());
		Assert.assertEquals(1, study2.getInstanceMetaData().size());
		Assert.assertEquals(1, study2.getContacts().size());
		Assert.assertEquals("Creator", study2.getContacts().get(0).getType());

		// Expecting only one study to be retrieved when filtered by location
		studies = this.studyService.getStudies(new StudySearchFilter().withLocationDbId(String.valueOf(location1)), null);
		Assert.assertEquals(1, studies.size());
		study1 = studies.get(0);
		Assert.assertEquals(newStudy.getProjectId(), study1.getStudyDbid());
		// Expecting environments of retrieved study to also be filtered by location
		Assert.assertEquals(1, study1.getInstanceMetaData().size());
		Assert.assertEquals(String.valueOf(location1), study1.getInstanceMetaData().get(0).getLocationDbId().toString());
	}

	@Test
	public void testGetStudiesWithDeletedStudy() throws Exception {
		// Add new study assigned new location ID
		final DmsProject newStudy = this.testDataInitializer
			.createStudy("Study2", "Study2-Description", 6, this.commonTestProject.getUniqueID(), this.testUser.getUserid().toString());
		final DmsProject environmentDataset =
			this.testDataInitializer
				.createDmsProject("Environment Dataset", "Environment Dataset-Description", newStudy, newStudy,
					DatasetTypeEnum.SUMMARY_DATA);
		final Random random = new Random();
		final Integer location1 = random.nextInt();
		final Geolocation geolocation = this.testDataInitializer.createInstance(environmentDataset, "1", location1);
		final ExperimentModel newStudyExperiment =
			this.testDataInitializer.createTestExperiment(newStudy, geolocation, TermId.STUDY_EXPERIMENT.getId(), null, null);

		// Create deleted study
		this.createDeletedStudy();
		// Flushing to force Hibernate to synchronize with the underlying database
		this.sessionProvder.getSession().flush();

		// Expecting only seeded studies for this test class/method to be retrieved when filtered by programUUID, sorted by descending study name
		List<StudySummary> studies =
			this.studyService.getStudies(new StudySearchFilter().withProgramDbId(this.commonTestProject.getUniqueID()), new PageRequest(0, 10, new Sort(Sort.Direction.fromString("desc"), "trialName")));
		Assert.assertEquals("Deleted study is not included",2, studies.size());
		StudySummary study1 = studies.get(1);
		Assert.assertEquals(this.study.getProjectId(), study1.getStudyDbid());
		Assert.assertEquals(this.study.getName(), study1.getName());
		Assert.assertEquals(this.study.getDescription(), study1.getDescription());
		Assert.assertEquals(this.study.getProgramUUID(), study1.getProgramDbId());
		Assert.assertEquals(this.studyExperiment.getObsUnitId(), study1.getObservationUnitId());
		Assert.assertEquals(1, study1.getContacts().size());
		// Workbench person details cannot be retrieved properly from this service
		Assert.assertEquals("Creator", study1.getContacts().get(0).getType());
		final StudySummary study2 = studies.get(0);
		Assert.assertEquals(newStudy.getProjectId(), study2.getStudyDbid());
		Assert.assertEquals(newStudy.getName(), study2.getName());
		Assert.assertEquals(newStudy.getDescription(), study2.getDescription());
		Assert.assertEquals(newStudy.getProgramUUID(), study2.getProgramDbId());
		Assert.assertEquals(newStudyExperiment.getObsUnitId(), study2.getObservationUnitId());
		Assert.assertEquals(1, study2.getInstanceMetaData().size());
		Assert.assertEquals(1, study2.getContacts().size());
		Assert.assertEquals("Creator", study2.getContacts().get(0).getType());

		// Expecting only one study to be retrieved when filtered by location
		studies = this.studyService.getStudies(new StudySearchFilter().withLocationDbId(String.valueOf(location1)), null);
		Assert.assertEquals(1, studies.size());
		study1 = studies.get(0);
		Assert.assertEquals(newStudy.getProjectId(), study1.getStudyDbid());
		// Expecting environments of retrieved study to also be filtered by location
		Assert.assertEquals(1, study1.getInstanceMetaData().size());
		Assert.assertEquals(String.valueOf(location1), study1.getInstanceMetaData().get(0).getLocationDbId().toString());
	}

	@Test
	public void testCountStudiesWithDeletedStudy() throws Exception {
		// Empty filter will retrieve all studies in crop
		final long initialCount = this.studyService.countStudies(new StudySearchFilter());

		// Add new study with new location ID
		final DmsProject newStudy = this.testDataInitializer
			.createStudy("Study2", "Study2-Description", 6, this.commonTestProject.getUniqueID(), this.testUser.getUserid().toString());
		final DmsProject environmentDataset =
			this.testDataInitializer
				.createDmsProject("Environment Dataset", "Environment Dataset-Description", newStudy, newStudy,
					DatasetTypeEnum.SUMMARY_DATA);
		final Random random = new Random();
		final int location1 = random.nextInt();
		final Geolocation geolocation = this.testDataInitializer.createInstance(environmentDataset, "1", location1);
		this.testDataInitializer.createTestExperiment(newStudy, geolocation, TermId.STUDY_EXPERIMENT.getId(), null, null);

		// Create deleted study
		this.createDeletedStudy();

		// Flushing to force Hibernate to synchronize with the underlying database
		this.sessionProvder.getSession().flush();

		// New study should be retrieved for empty filter
		Assert.assertEquals((int) initialCount + 1, this.studyService.countStudies(new StudySearchFilter()));
		// Expecting only seeded studies for this test class/method to be retrieved when filtered by programUUID
		Assert
			.assertEquals(2, this.studyService.countStudies(new StudySearchFilter().withProgramDbId(this.commonTestProject.getUniqueID())));
		// Expecting only one to be retrieved when filtered by location
		Assert.assertEquals(1, this.studyService.countStudies(new StudySearchFilter().withLocationDbId(String.valueOf(location1))));
	}

	private void createDeletedStudy() {
		final DmsProject deletedStudy = this.testDataInitializer
			.createStudy("StudyDeleted", "StudyDeleted-Description", 6, this.commonTestProject.getUniqueID(), this.testUser.getUserid().toString());
		deletedStudy.setDeleted(true);
		final DmsProject environmentDatasetDeleted =
			this.testDataInitializer
				.createDmsProject("Environment Dataset Deleted", "Environment Dataset-Description Deleted", deletedStudy, deletedStudy,
					DatasetTypeEnum.SUMMARY_DATA);
		final Random random = new Random();
		final int location1 = random.nextInt();
		final Geolocation geolocation = this.testDataInitializer.createInstance(environmentDatasetDeleted, "2", location1);
		this.testDataInitializer.createTestExperiment(deletedStudy, geolocation, TermId.STUDY_EXPERIMENT.getId(), null, null);
	}

	private void assertEnvironmentParameter(final List<MeasurementVariable> environmentParameters, final int expectedTermId, final String expectedName,
		final String expectedValue) {
		final Optional<MeasurementVariable> optional = environmentParameters
			.stream()
			.filter(measurementVariable -> measurementVariable.getTermId() == expectedTermId)
			.findFirst();
		assertTrue(optional.isPresent());

		final MeasurementVariable envParam = optional.get();
		assertThat(envParam.getName(), is(expectedName));
		assertThat(envParam.getValue(), is(expectedValue));
	}

}
