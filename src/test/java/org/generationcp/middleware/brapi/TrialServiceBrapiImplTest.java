package org.generationcp.middleware.brapi;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.api.brapi.TrialServiceBrapi;
import org.generationcp.middleware.api.brapi.v2.germplasm.ExternalReferenceDTO;
import org.generationcp.middleware.api.brapi.v2.trial.TrialImportRequestDTO;
import org.generationcp.middleware.domain.dms.StudySummary;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.StudyExternalReference;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.study.StudySearchFilter;
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
import java.util.Random;

public class TrialServiceBrapiImplTest extends IntegrationTestBase {

	@Autowired
	private TrialServiceBrapi trialServiceBrapi;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private WorkbenchTestDataUtil workbenchTestDataUtil;

	private IntegrationTestDataInitializer testDataInitializer;
	private Project commonTestProject;
	private CropType crop;
	private WorkbenchUser testUser;
	private DmsProject study;
	private ExperimentModel studyExperiment;


	@Before
	public void setUp() {
//		this.daoFactory = new DaoFactory(this.sessionProvder);

		this.workbenchTestDataUtil.setUpWorkbench();
		if (this.commonTestProject == null) {
			this.commonTestProject = this.workbenchTestDataUtil.getCommonTestProject();
			this.crop = this.workbenchDataManager.getProjectByUuid(this.commonTestProject.getUniqueID()).getCropType();
		}

		this.testDataInitializer = new IntegrationTestDataInitializer(this.sessionProvder, this.workbenchSessionProvider);
		this.testUser = this.testDataInitializer.createUserForTesting();
	}

	@Test
	public void testCountStudies() {
		// Empty filter will retrieve all studies in crop
		final StudySearchFilter studySearchFilter = new StudySearchFilter();
		final long initialCount = this.trialServiceBrapi.countStudies(studySearchFilter);

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
		Assert.assertEquals((int) initialCount + 1, this.trialServiceBrapi.countStudies(studySearchFilter));
		// Expecting only seeded studies for this test class/method to be retrieved when filtered by programUUID
		studySearchFilter.setProgramDbId(this.commonTestProject.getUniqueID());
		Assert
			.assertEquals(2, this.trialServiceBrapi.countStudies(studySearchFilter));
		studySearchFilter.setLocationDbId(String.valueOf(location1));
		// Expecting only one to be retrieved when filtered by location
		Assert.assertEquals(1, this.trialServiceBrapi.countStudies(studySearchFilter));
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
			this.trialServiceBrapi.getStudies(
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
		studies = this.trialServiceBrapi.getStudies(studySearchFilter, null);
		Assert.assertEquals(1, studies.size());
		study1 = studies.get(0);
		Assert.assertEquals(newStudy.getProjectId(), study1.getTrialDbId());
		// Expecting environments of retrieved study to also be filtered by location
		Assert.assertEquals(1, study1.getInstanceMetaData().size());
		Assert.assertEquals(String.valueOf(location1), study1.getInstanceMetaData().get(0).getLocationDbId().toString());
	}

	@Test
	public void testGetStudies_FilterByStudyExternalReference() {
		// Add new completed study assigned new location ID
		final DmsProject newStudy = this.testDataInitializer
			.createStudy("Study2", "Study2-Description", 6, this.commonTestProject.getUniqueID(), this.testUser.getUserid().toString(),
				"20200101", "20201231");
		final StudyExternalReference studyExternalReference = this.testDataInitializer
			.createStudyExternalReference(newStudy, RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10));
		final DmsProject environmentDataset =
			this.testDataInitializer
				.createDmsProject("Environment Dataset", "Environment Dataset-Description", newStudy, newStudy,
					DatasetTypeEnum.SUMMARY_DATA);
		final Geolocation geolocation = this.testDataInitializer.createInstance(environmentDataset, "1", new Random().nextInt());
		final ExperimentModel newStudyExperiment =
			this.testDataInitializer.createTestExperiment(newStudy, geolocation, TermId.STUDY_EXPERIMENT.getId(), null, null);

		// Flushing to force Hibernate to synchronize with the underlying database
		this.sessionProvder.getSession().flush();

		final StudySearchFilter studySearchFilter = new StudySearchFilter();
		studySearchFilter.setExternalReferenceSource(studyExternalReference.getSource());
		studySearchFilter.setExternalReferenceID(studyExternalReference.getReferenceId());

		final List<StudySummary> studies =
			this.trialServiceBrapi.getStudies(
				studySearchFilter, new PageRequest(0, 10, new Sort(Sort.Direction.fromString("desc"), "trialName")));
		Assert.assertEquals(1, studies.size());
		final StudySummary study2 = studies.get(0);
		Assert.assertEquals(newStudy.getProjectId(), study2.getTrialDbId());
		Assert.assertEquals(newStudy.getName(), study2.getName());
	}

	@Test
	public void testCountStudies_FilterByStudyExternalReference() {
		// Add new completed study assigned new location ID
		final DmsProject newStudy = this.testDataInitializer
			.createStudy("Study2", "Study2-Description", 6, this.commonTestProject.getUniqueID(), this.testUser.getUserid().toString(),
				"20200101", "20201231");
		final StudyExternalReference studyExternalReference = this.testDataInitializer
			.createStudyExternalReference(newStudy, RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10));
		final DmsProject environmentDataset =
			this.testDataInitializer
				.createDmsProject("Environment Dataset", "Environment Dataset-Description", newStudy, newStudy,
					DatasetTypeEnum.SUMMARY_DATA);
		final Geolocation geolocation = this.testDataInitializer.createInstance(environmentDataset, "1", new Random().nextInt());
		final ExperimentModel newStudyExperiment =
			this.testDataInitializer.createTestExperiment(newStudy, geolocation, TermId.STUDY_EXPERIMENT.getId(), null, null);

		// Flushing to force Hibernate to synchronize with the underlying database
		this.sessionProvder.getSession().flush();

		final StudySearchFilter studySearchFilter = new StudySearchFilter();
		studySearchFilter.setExternalReferenceSource(studyExternalReference.getSource());
		studySearchFilter.setExternalReferenceID(studyExternalReference.getReferenceId());

		Assert.assertEquals(1, this.trialServiceBrapi.countStudies(studySearchFilter));
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
			this.trialServiceBrapi.getStudies(
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
		studies = this.trialServiceBrapi.getStudies(studySearchFilter, null);
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
		final long initialCount = this.trialServiceBrapi.countStudies(studySearchFilter);

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
		Assert.assertEquals((int) initialCount + 1, this.trialServiceBrapi.countStudies(studySearchFilter));
		// Expecting only seeded studies for this test class/method to be retrieved when filtered by programUUID
		studySearchFilter.setProgramDbId(this.commonTestProject.getUniqueID());
		Assert
			.assertEquals(2, this.trialServiceBrapi.countStudies(studySearchFilter));
		studySearchFilter.setLocationDbId(String.valueOf(location1));
		// Expecting only one to be retrieved when filtered by location
		Assert.assertEquals(1, this.trialServiceBrapi.countStudies(studySearchFilter));
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
		settingsMap.put(this.testDataInitializer.createVariableWithScale(DataType.CHARACTER_VARIABLE, VariableType.STUDY_DETAIL).getName(),
			RandomStringUtils.randomAlphabetic(30));
		settingsMap.put(this.testDataInitializer.createVariableWithScale(DataType.DATE_TIME_VARIABLE, VariableType.STUDY_DETAIL).getName(),
			"20210501");
		settingsMap.put(this.testDataInitializer.createVariableWithScale(DataType.NUMERIC_VARIABLE, VariableType.STUDY_DETAIL).getName(),
			RandomStringUtils.randomNumeric(10));
		final List<String> possibleValues = Arrays
			.asList(RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20));
		settingsMap.put(this.testDataInitializer.createCategoricalVariable(VariableType.STUDY_DETAIL, possibleValues).getName(),
			possibleValues.get(0));
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

		final List<StudySummary> savedStudies = this.trialServiceBrapi
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
		settingsMap.put(this.testDataInitializer.createVariableWithScale(DataType.CHARACTER_VARIABLE, VariableType.STUDY_DETAIL).getName(),
			RandomStringUtils.randomAlphabetic(30));
		settingsMap.put(this.testDataInitializer.createVariableWithScale(DataType.DATE_TIME_VARIABLE, VariableType.STUDY_DETAIL).getName(),
			"20210501");
		importRequest1.setAdditionalInfo(settingsMap);

		final List<StudySummary> savedStudies =
			this.trialServiceBrapi.saveStudies(this.crop.getCropName(), Collections.singletonList(importRequest1), this.testUser.getUserid());
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
		settingsMap.put(this.testDataInitializer.createVariableWithScale(DataType.NUMERIC_VARIABLE, VariableType.STUDY_DETAIL).getName(),
			RandomStringUtils.randomAlphabetic(30));
		settingsMap.put(this.testDataInitializer.createVariableWithScale(DataType.DATE_TIME_VARIABLE, VariableType.STUDY_DETAIL).getName(),
			"2021-05-01");
		final List<String> possibleValues = Arrays
			.asList(RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20));
		settingsMap.put(this.testDataInitializer.createCategoricalVariable(VariableType.STUDY_DETAIL, possibleValues).getName(),
			RandomStringUtils.randomAlphabetic(30));
		importRequest1.setAdditionalInfo(settingsMap);

		final List<StudySummary> savedStudies =
			this.trialServiceBrapi.saveStudies(this.crop.getCropName(), Collections.singletonList(importRequest1), this.testUser.getUserid());
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
		settingsMap
			.put(this.testDataInitializer.createVariableWithScale(DataType.CHARACTER_VARIABLE, VariableType.SELECTION_METHOD).getName(),
				RandomStringUtils.randomAlphabetic(30));
		settingsMap
			.put(this.testDataInitializer.createVariableWithScale(DataType.CHARACTER_VARIABLE, VariableType.ENVIRONMENT_DETAIL).getName(),
				RandomStringUtils.randomAlphabetic(30));
		settingsMap
			.put(this.testDataInitializer.createVariableWithScale(DataType.CHARACTER_VARIABLE, VariableType.TREATMENT_FACTOR).getName(),
				RandomStringUtils.randomAlphabetic(30));
		settingsMap
			.put(this.testDataInitializer.createVariableWithScale(DataType.NUMERIC_VARIABLE, VariableType.EXPERIMENTAL_DESIGN).getName(),
				RandomStringUtils.randomNumeric(5));
		final List<String> possibleValues = Arrays
			.asList(RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20));
		settingsMap.put(this.testDataInitializer.createCategoricalVariable(VariableType.GERMPLASM_DESCRIPTOR, possibleValues).getName(),
			possibleValues.get(1));
		settingsMap
			.put(this.testDataInitializer.createVariableWithScale(DataType.DATE_TIME_VARIABLE, VariableType.TRAIT).getName(), "20210501");

		importRequest1.setAdditionalInfo(settingsMap);

		final List<StudySummary> savedStudies =
			this.trialServiceBrapi.saveStudies(this.crop.getCropName(), Collections.singletonList(importRequest1), this.testUser.getUserid());
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
		Assert.assertTrue(CollectionUtils.isEmpty(study.getInstanceMetaData()));
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
