package org.generationcp.middleware.service.impl.study;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.api.brapi.v2.germplasm.ExternalReferenceDTO;
import org.generationcp.middleware.api.brapi.v2.trial.TrialImportRequestDTO;
import org.generationcp.middleware.api.germplasm.GermplasmStudyDto;
import org.generationcp.middleware.dao.dms.InstanceMetadata;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.domain.dms.StudySummary;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.generationcp.middleware.pojos.oms.CVTermRelationship;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.study.StudySearchFilter;
import org.generationcp.middleware.service.api.study.StudyService;
import org.generationcp.middleware.util.Util;
import org.generationcp.middleware.utils.test.IntegrationTestDataInitializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import static org.junit.Assert.assertFalse;

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
		// Null study end date means it's still active
		this.study = this.testDataInitializer
			.createStudy("Study1", "Study-Description", 6, this.commonTestProject.getUniqueID(), this.testUser.getUserid().toString(),
				"20180205", null);

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
	public void testCountStudies() {
		// Empty filter will retrieve all studies in crop
		final StudySearchFilter studySearchFilter = new StudySearchFilter();
		final long initialCount = this.studyService.countStudies(studySearchFilter);

		// Add new study with new location ID
		final DmsProject newStudy = this.testDataInitializer
			.createStudy("Study2", "Study2-Description", 6, this.commonTestProject.getUniqueID(), this.testUser.getUserid().toString(),
				null, null);
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
		Assert.assertEquals((int) initialCount + 1, this.studyService.countStudies(studySearchFilter));
		// Expecting only seeded studies for this test class/method to be retrieved when filtered by programUUID
		studySearchFilter.setProgramDbId(this.commonTestProject.getUniqueID());
		Assert
			.assertEquals(2, this.studyService.countStudies(studySearchFilter));
		studySearchFilter.setLocationDbId(String.valueOf(location1));
		// Expecting only one to be retrieved when filtered by location
		Assert.assertEquals(1, this.studyService.countStudies(studySearchFilter));
	}

	@Test
	public void testGetStudies() {
		// Add new completed study assigned new location ID
		final DmsProject newStudy = this.testDataInitializer
			.createStudy("Study2", "Study2-Description", 6, this.commonTestProject.getUniqueID(), this.testUser.getUserid().toString(),
				"20200101", "20201231");
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
		final StudySearchFilter studySearchFilter = new StudySearchFilter();
		studySearchFilter.setProgramDbId(this.commonTestProject.getUniqueID());
		List<StudySummary> studies =
			this.studyService.getStudies(
				studySearchFilter, new PageRequest(0, 10, new Sort(Sort.Direction.fromString("desc"), "trialName")));
		Assert.assertEquals(2, studies.size());
		StudySummary study1 = studies.get(1);
		Assert.assertEquals(this.study.getProjectId(), study1.getTrialDbId());
		Assert.assertEquals(this.study.getName(), study1.getName());
		Assert.assertEquals(this.study.getDescription(), study1.getDescription());
		Assert.assertEquals(this.study.getProgramUUID(), study1.getProgramDbId());
		Assert.assertEquals(this.studyExperiment.getObsUnitId(), study1.getObservationUnitId());
		Assert.assertEquals(1, study1.getContacts().size());
		Assert.assertTrue(study1.isActive());
		// Workbench person details cannot be retrieved properly from this service
		Assert.assertEquals("Creator", study1.getContacts().get(0).getType());
		final StudySummary study2 = studies.get(0);
		Assert.assertEquals(newStudy.getProjectId(), study2.getTrialDbId());
		Assert.assertEquals(newStudy.getName(), study2.getName());
		Assert.assertEquals(newStudy.getDescription(), study2.getDescription());
		Assert.assertEquals(newStudy.getProgramUUID(), study2.getProgramDbId());
		Assert.assertEquals(newStudyExperiment.getObsUnitId(), study2.getObservationUnitId());
		Assert.assertEquals(1, study2.getInstanceMetaData().size());
		Assert.assertEquals(1, study2.getContacts().size());
		Assert.assertEquals("Creator", study2.getContacts().get(0).getType());
		Assert.assertFalse(study2.isActive());

		studySearchFilter.setLocationDbId(String.valueOf(location1));
		// Expecting only one study to be retrieved when filtered by location
		studies = this.studyService.getStudies(studySearchFilter, null);
		Assert.assertEquals(1, studies.size());
		study1 = studies.get(0);
		Assert.assertEquals(newStudy.getProjectId(), study1.getTrialDbId());
		// Expecting environments of retrieved study to also be filtered by location
		Assert.assertEquals(1, study1.getInstanceMetaData().size());
		Assert.assertEquals(String.valueOf(location1), study1.getInstanceMetaData().get(0).getLocationDbId().toString());
	}

	@Test
	public void testGetStudiesWithDeletedStudy() {
		// Add new study assigned new location ID
		final DmsProject newStudy = this.testDataInitializer
			.createStudy("Study2", "Study2-Description", 6, this.commonTestProject.getUniqueID(), this.testUser.getUserid().toString(),
				null, null);
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
		final StudySearchFilter studySearchFilter = new StudySearchFilter();
		studySearchFilter.setProgramDbId(this.commonTestProject.getUniqueID());
		List<StudySummary> studies =
			this.studyService.getStudies(
				studySearchFilter, new PageRequest(0, 10, new Sort(Sort.Direction.fromString("desc"), "trialName")));
		Assert.assertEquals("Deleted study is not included", 2, studies.size());
		StudySummary study1 = studies.get(1);
		Assert.assertEquals(this.study.getProjectId(), study1.getTrialDbId());
		Assert.assertEquals(this.study.getName(), study1.getName());
		Assert.assertEquals(this.study.getDescription(), study1.getDescription());
		Assert.assertEquals(this.study.getProgramUUID(), study1.getProgramDbId());
		Assert.assertEquals(this.studyExperiment.getObsUnitId(), study1.getObservationUnitId());
		Assert.assertEquals(1, study1.getContacts().size());
		// Workbench person details cannot be retrieved properly from this service
		Assert.assertEquals("Creator", study1.getContacts().get(0).getType());
		final StudySummary study2 = studies.get(0);
		Assert.assertEquals(newStudy.getProjectId(), study2.getTrialDbId());
		Assert.assertEquals(newStudy.getName(), study2.getName());
		Assert.assertEquals(newStudy.getDescription(), study2.getDescription());
		Assert.assertEquals(newStudy.getProgramUUID(), study2.getProgramDbId());
		Assert.assertEquals(newStudyExperiment.getObsUnitId(), study2.getObservationUnitId());
		Assert.assertEquals(1, study2.getInstanceMetaData().size());
		Assert.assertEquals(1, study2.getContacts().size());
		Assert.assertEquals("Creator", study2.getContacts().get(0).getType());

		// Expecting only one study to be retrieved when filtered by location
		studySearchFilter.setLocationDbId(String.valueOf(location1));
		studies = this.studyService.getStudies(studySearchFilter, null);
		Assert.assertEquals(1, studies.size());
		study1 = studies.get(0);
		Assert.assertEquals(newStudy.getProjectId(), study1.getTrialDbId());
		// Expecting environments of retrieved study to also be filtered by location
		Assert.assertEquals(1, study1.getInstanceMetaData().size());
		Assert.assertEquals(String.valueOf(location1), study1.getInstanceMetaData().get(0).getLocationDbId().toString());
	}

	@Test
	public void testCountStudiesWithDeletedStudy() {
		// Empty filter will retrieve all studies in crop
		final StudySearchFilter studySearchFilter = new StudySearchFilter();
		final long initialCount = this.studyService.countStudies(studySearchFilter);

		// Add new study with new location ID
		final DmsProject newStudy = this.testDataInitializer
			.createStudy("Study2", "Study2-Description", 6, this.commonTestProject.getUniqueID(), this.testUser.getUserid().toString(),
				null, null);
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
		Assert.assertEquals((int) initialCount + 1, this.studyService.countStudies(studySearchFilter));
		// Expecting only seeded studies for this test class/method to be retrieved when filtered by programUUID
		studySearchFilter.setProgramDbId(this.commonTestProject.getUniqueID());
		Assert
			.assertEquals(2, this.studyService.countStudies(studySearchFilter));
		studySearchFilter.setLocationDbId(String.valueOf(location1));
		// Expecting only one to be retrieved when filtered by location
		Assert.assertEquals(1, this.studyService.countStudies(studySearchFilter));
	}

	@Test
	public void shouldGetGermplasmStudies_OK() {
		final DmsProject newStudy = this.testDataInitializer
			.createStudy("Study " + RandomStringUtils.randomNumeric(5), "Study2-Description", 6,
				this.commonTestProject.getUniqueID(), this.testUser.getUserid().toString(), null, null);

		final StockModel stockModel = new StockModel();
		stockModel.setUniqueName(org.apache.commons.lang.RandomStringUtils.randomAlphanumeric(10));
		stockModel.setTypeId(TermId.ENTRY_CODE.getId());
		stockModel.setName(org.apache.commons.lang.RandomStringUtils.randomAlphanumeric(10));
		stockModel.setIsObsolete(false);

		final Germplasm germplasm = GermplasmTestDataInitializer.createGermplasm(1);
		germplasm.setGid(null);
		this.daoFactory.getGermplasmDao().save(germplasm);

		stockModel.setGermplasm(germplasm);
		stockModel.setValue(org.apache.commons.lang.RandomStringUtils.randomAlphanumeric(5));
		stockModel.setProject(newStudy);
		this.daoFactory.getStockDao().saveOrUpdate(stockModel);
		this.sessionProvder.getSession().flush();

		final List<GermplasmStudyDto> germplasmStudyDtos = this.studyService.getGermplasmStudies(germplasm.getGid());
		Assert.assertFalse(germplasmStudyDtos.isEmpty());
		final GermplasmStudyDto germplasmStudyDto = germplasmStudyDtos.get(0);
		Assert.assertEquals(newStudy.getProjectId(), germplasmStudyDto.getStudyId());
		Assert.assertEquals(newStudy.getName(), germplasmStudyDto.getName());
		Assert.assertEquals(newStudy.getDescription(), germplasmStudyDto.getDescription());
	}

	@Test
	public void testSaveStudy_AllInfoSaved() {
		final TrialImportRequestDTO importRequest1 = new TrialImportRequestDTO();
		importRequest1.setStartDate("2019-01-01");
		importRequest1.setEndDate("2020-12-31");
		importRequest1.setTrialDescription(RandomStringUtils.randomAlphabetic(20));
		importRequest1.setTrialName(RandomStringUtils.randomAlphabetic(20));
		importRequest1.setProgramDbId(this.commonTestProject.getUniqueID());

		final Map<String, String> settingsMap = Maps.newHashMap();
		settingsMap.put(this.createVariableWithScale(DataType.CHARACTER_VARIABLE, VariableType.STUDY_DETAIL).getName(),
			RandomStringUtils.randomAlphabetic(30));
		settingsMap.put(this.createVariableWithScale(DataType.DATE_TIME_VARIABLE, VariableType.STUDY_DETAIL).getName(), "20210501");
		settingsMap.put(this.createVariableWithScale(DataType.NUMERIC_VARIABLE, VariableType.STUDY_DETAIL).getName(),
			RandomStringUtils.randomNumeric(10));
		final List<String> possibleValues = Arrays
			.asList(RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20));
		settingsMap.put(this.createCategoricalVariable(VariableType.STUDY_DETAIL, possibleValues).getName(), possibleValues.get(0));
		importRequest1.setAdditionalInfo(settingsMap);
		final TrialImportRequestDTO importRequest2 = new TrialImportRequestDTO();
		importRequest2.setStartDate("2019-01-01");
		importRequest2.setTrialDescription(RandomStringUtils.randomAlphabetic(20));
		importRequest2.setTrialName(RandomStringUtils.randomAlphabetic(20));
		importRequest2.setProgramDbId(this.commonTestProject.getUniqueID());
		final ExternalReferenceDTO externalReference = new ExternalReferenceDTO();
		externalReference.setReferenceID(RandomStringUtils.randomAlphabetic(20));
		externalReference.setReferenceSource(RandomStringUtils.randomAlphabetic(20));
		importRequest2.setExternalReferences(Collections.singletonList(externalReference));

		final List<StudySummary> savedStudies = this.studyService
			.saveStudies(this.crop.getCropName(), Arrays.asList(importRequest1, importRequest2), this.testUser.getUserid());
		Assert.assertEquals(2, savedStudies.size());
		final StudySummary study1 = savedStudies.get(0);
		this.verifyStudySummary(importRequest1, study1);
		this.verifyStudySummary(importRequest2, savedStudies.get(1));
		// Verify study settings, only first study imported study details
		Assert.assertNotNull(study1.getAdditionalInfo());
		Assert.assertEquals(importRequest1.getAdditionalInfo().size(), study1.getAdditionalInfo().size());
		for (final String key : importRequest1.getAdditionalInfo().keySet()) {
			Assert.assertEquals(importRequest1.getAdditionalInfo().get(key), study1.getAdditionalInfo().get(key));
		}
	}

	@Test
	public void testSaveStudy_WithInvalidStudyVariableNames() {
		final TrialImportRequestDTO importRequest1 = new TrialImportRequestDTO();
		importRequest1.setStartDate("2019-01-01");
		importRequest1.setEndDate("2020-12-31");
		importRequest1.setTrialDescription(RandomStringUtils.randomAlphabetic(20));
		importRequest1.setTrialName(RandomStringUtils.randomAlphabetic(20));
		importRequest1.setProgramDbId(this.commonTestProject.getUniqueID());

		final Map<String, String> settingsMap = Maps.newHashMap();
		final String invalidVariableName = RandomStringUtils.randomAlphabetic(30);
		settingsMap.put(invalidVariableName, RandomStringUtils.randomAlphabetic(30));
		settingsMap.put(this.createVariableWithScale(DataType.CHARACTER_VARIABLE, VariableType.STUDY_DETAIL).getName(),
			RandomStringUtils.randomAlphabetic(30));
		settingsMap.put(this.createVariableWithScale(DataType.DATE_TIME_VARIABLE, VariableType.STUDY_DETAIL).getName(), "20210501");
		importRequest1.setAdditionalInfo(settingsMap);

		final List<StudySummary> savedStudies =
			this.studyService.saveStudies(this.crop.getCropName(), Collections.singletonList(importRequest1), this.testUser.getUserid());
		Assert.assertEquals(1, savedStudies.size());
		final StudySummary study1 = savedStudies.get(0);
		this.verifyStudySummary(importRequest1, study1);
		Assert.assertNotNull(study1.getAdditionalInfo());
		// Verify invalid study variable name was not saved, but the valid ones were
		Assert.assertEquals(importRequest1.getAdditionalInfo().size() - 1, study1.getAdditionalInfo().size());
		Assert.assertFalse(study1.getAdditionalInfo().keySet().contains(invalidVariableName));
	}

	@Test
	public void testSaveStudy_WithInvalidStudyVariableValues() {
		final TrialImportRequestDTO importRequest1 = new TrialImportRequestDTO();
		importRequest1.setStartDate("2019-01-01");
		importRequest1.setEndDate("2020-12-31");
		importRequest1.setTrialDescription(RandomStringUtils.randomAlphabetic(20));
		importRequest1.setTrialName(RandomStringUtils.randomAlphabetic(20));
		importRequest1.setProgramDbId(this.commonTestProject.getUniqueID());

		final Map<String, String> settingsMap = Maps.newHashMap();
		settingsMap.put(this.createVariableWithScale(DataType.NUMERIC_VARIABLE, VariableType.STUDY_DETAIL).getName(),
			RandomStringUtils.randomAlphabetic(30));
		settingsMap.put(this.createVariableWithScale(DataType.DATE_TIME_VARIABLE, VariableType.STUDY_DETAIL).getName(), "2021-05-01");
		final List<String> possibleValues = Arrays
			.asList(RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20));
		settingsMap.put(this.createCategoricalVariable(VariableType.STUDY_DETAIL, possibleValues).getName(),
			RandomStringUtils.randomAlphabetic(30));
		importRequest1.setAdditionalInfo(settingsMap);

		final List<StudySummary> savedStudies =
			this.studyService.saveStudies(this.crop.getCropName(), Collections.singletonList(importRequest1), this.testUser.getUserid());
		Assert.assertEquals(1, savedStudies.size());
		final StudySummary study1 = savedStudies.get(0);
		this.verifyStudySummary(importRequest1, study1);
		Assert.assertTrue(CollectionUtils.isEmpty(study1.getAdditionalInfo()));
	}

	@Test
	public void testSaveStudy_WithValidVariablesHavingNonStudyDetailVariableType() {
		final TrialImportRequestDTO importRequest1 = new TrialImportRequestDTO();
		importRequest1.setStartDate("2019-01-01");
		importRequest1.setEndDate("2020-12-31");
		importRequest1.setTrialDescription(RandomStringUtils.randomAlphabetic(20));
		importRequest1.setTrialName(RandomStringUtils.randomAlphabetic(20));
		importRequest1.setProgramDbId(this.commonTestProject.getUniqueID());

		final Map<String, String> settingsMap = Maps.newHashMap();
		settingsMap.put(this.createVariableWithScale(DataType.CHARACTER_VARIABLE, VariableType.SELECTION_METHOD).getName(),
			RandomStringUtils.randomAlphabetic(30));
		settingsMap.put(this.createVariableWithScale(DataType.CHARACTER_VARIABLE, VariableType.ENVIRONMENT_DETAIL).getName(),
			RandomStringUtils.randomAlphabetic(30));
		settingsMap.put(this.createVariableWithScale(DataType.CHARACTER_VARIABLE, VariableType.TREATMENT_FACTOR).getName(),
			RandomStringUtils.randomAlphabetic(30));
		settingsMap.put(this.createVariableWithScale(DataType.NUMERIC_VARIABLE, VariableType.EXPERIMENTAL_DESIGN).getName(),
			RandomStringUtils.randomNumeric(5));
		final List<String> possibleValues = Arrays
			.asList(RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20));
		settingsMap.put(this.createCategoricalVariable(VariableType.GERMPLASM_DESCRIPTOR, possibleValues).getName(), possibleValues.get(1));
		settingsMap.put(this.createVariableWithScale(DataType.DATE_TIME_VARIABLE, VariableType.TRAIT).getName(), "20210501");

		importRequest1.setAdditionalInfo(settingsMap);

		final List<StudySummary> savedStudies =
			this.studyService.saveStudies(this.crop.getCropName(), Collections.singletonList(importRequest1), this.testUser.getUserid());
		Assert.assertEquals(1, savedStudies.size());
		final StudySummary study1 = savedStudies.get(0);
		this.verifyStudySummary(importRequest1, study1);
		Assert.assertTrue(CollectionUtils.isEmpty(study1.getAdditionalInfo()));
	}

	private void verifyStudySummary(final TrialImportRequestDTO importRequestDTO, final StudySummary study) {
		Assert.assertEquals(importRequestDTO.getProgramDbId(), study.getProgramDbId());
		Assert.assertEquals(importRequestDTO.getTrialDescription(), study.getDescription());
		Assert.assertEquals(importRequestDTO.getTrialName(), study.getName());
		Assert.assertEquals(Util.tryConvertDate(importRequestDTO.getStartDate(), Util.FRONTEND_DATE_FORMAT, Util.DATE_AS_NUMBER_FORMAT),
			Util.convertDateToIntegerValue(study.getStartDate()).toString());
		if (importRequestDTO.getEndDate() != null) {
			Assert.assertEquals(Util.tryConvertDate(importRequestDTO.getEndDate(), Util.FRONTEND_DATE_FORMAT, Util.DATE_AS_NUMBER_FORMAT),
				Util.convertDateToIntegerValue(study.getEndDate()).toString());
			Assert.assertFalse(study.isActive());
		} else {
			// If no end date, study is assumed active (non-completed)
			Assert.assertTrue(study.isActive());
		}
		Assert.assertNotNull(study.getTrialDbId());
		Assert.assertNotNull(study.getObservationUnitId());
		Assert.assertEquals(1, study.getInstanceMetaData().size());
		final InstanceMetadata study1Instance = study.getInstanceMetaData().get(0);
		Assert.assertEquals("1", study1Instance.getInstanceNumber());
		Assert.assertEquals(study.getTrialDbId(), study1Instance.getTrialDbId());
		final Optional<Location> unspecifiedLocation = this.daoFactory.getLocationDAO().getUnspecifiedLocation();
		if (unspecifiedLocation.isPresent()) {
			final Integer locationId = unspecifiedLocation.get().getLocid();
			Assert.assertEquals(locationId.toString(), study.getLocationId());
			Assert.assertEquals(locationId, study1Instance.getLocationDbId());
			Assert.assertEquals(unspecifiedLocation.get().getLname(), study1Instance.getLocationName());
		}
		if (!CollectionUtils.isEmpty(importRequestDTO.getExternalReferences())) {
			Assert.assertNotNull(study.getExternalReferences());
			Assert.assertEquals(importRequestDTO.getExternalReferences().size(), study.getExternalReferences().size());
			final ListIterator<ExternalReferenceDTO> importReferencesIterator =
				importRequestDTO.getExternalReferences().listIterator();
			for (final ExternalReferenceDTO externalReference : study.getExternalReferences()) {
				final ExternalReferenceDTO externalReferenceImportRequest = importReferencesIterator.next();
				Assert.assertEquals(externalReferenceImportRequest.getReferenceID(), externalReference.getReferenceID());
				Assert.assertEquals(externalReferenceImportRequest.getReferenceSource(), externalReference.getReferenceSource());
				Assert.assertEquals(study.getTrialDbId().toString(), externalReference.getEntityId());
			}
		}
	}

	private CVTerm createVariableWithScale(final DataType dataType, final VariableType variableType) {
		final CVTerm variable = this.testDataInitializer.createTrait(RandomStringUtils.randomAlphabetic(20));
		final CVTerm scale = this.testDataInitializer.createCVTerm(RandomStringUtils.randomAlphabetic(20), CvId.SCALES.getId());
		this.daoFactory.getCvTermRelationshipDao()
			.save(new CVTermRelationship(TermId.HAS_SCALE.getId(), variable.getCvTermId(), scale.getCvTermId()));
		this.daoFactory.getCvTermRelationshipDao()
			.save(new CVTermRelationship(TermId.HAS_TYPE.getId(), scale.getCvTermId(), dataType.getId()));
		this.daoFactory.getCvTermPropertyDao()
			.save(new CVTermProperty(TermId.VARIABLE_TYPE.getId(), variableType.getName(), 1, variable.getCvTermId()));
		return variable;
	}

	private CVTerm createCategoricalVariable(final VariableType variableType, final List<String> possibleValues) {
		final CVTerm variable = this.testDataInitializer.createTrait(RandomStringUtils.randomAlphabetic(20));
		final CVTerm scale = this.testDataInitializer.createCVTerm(RandomStringUtils.randomAlphabetic(20), CvId.SCALES.getId());
		this.daoFactory.getCvTermRelationshipDao()
			.save(new CVTermRelationship(TermId.HAS_SCALE.getId(), variable.getCvTermId(), scale.getCvTermId()));
		this.daoFactory.getCvTermRelationshipDao()
			.save(new CVTermRelationship(TermId.HAS_TYPE.getId(), scale.getCvTermId(), DataType.CATEGORICAL_VARIABLE
				.getId()));
		this.daoFactory.getCvTermPropertyDao()
			.save(new CVTermProperty(TermId.VARIABLE_TYPE.getId(), variableType.getName(), 1, variable.getCvTermId()));
		for (final String value : possibleValues) {
			final CVTerm categoricalValue =
				this.testDataInitializer.createCVTerm(RandomStringUtils.randomAlphabetic(10), value, CvId.IBDB_TERMS.getId());
			this.daoFactory.getCvTermRelationshipDao()
				.save(new CVTermRelationship(TermId.HAS_VALUE.getId(), scale.getCvTermId(), categoricalValue.getCvTermId()));
		}
		return variable;
	}

	private void createDeletedStudy() {
		final DmsProject deletedStudy = this.testDataInitializer
			.createStudy("StudyDeleted", "StudyDeleted-Description", 6, this.commonTestProject.getUniqueID(),
				this.testUser.getUserid().toString(), null, null);
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

}
