package org.generationcp.middleware.brapi;

import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.api.brapi.TrialServiceBrapiImpl;
import org.generationcp.middleware.api.brapi.v2.germplasm.ExternalReferenceDTO;
import org.generationcp.middleware.api.brapi.v2.trial.TrialImportRequestDTO;
import org.generationcp.middleware.api.program.ProgramService;
import org.generationcp.middleware.api.role.RoleService;
import org.generationcp.middleware.dao.CropPersonDAO;
import org.generationcp.middleware.dao.WorkbenchUserDAO;
import org.generationcp.middleware.domain.dms.TrialSummary;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.domain.search_request.brapi.v2.TrialSearchRequestDTO;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.WorkbenchDaoFactory;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.StudyExternalReference;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.ontology.VariableDataValidatorFactory;
import org.generationcp.middleware.service.api.user.ContactDto;
import org.generationcp.middleware.service.api.user.ContactVariable;
import org.generationcp.middleware.service.impl.study.generation.ExperimentModelGenerator;
import org.generationcp.middleware.util.Util;
import org.generationcp.middleware.utils.test.IntegrationTestDataInitializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
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

public class TrialServiceBrapiImplTest extends IntegrationTestBase {

	private TrialServiceBrapiImpl trialServiceBrapi;

	@Autowired
	private RoleService roleService;

	@Autowired
	private WorkbenchTestDataUtil workbenchTestDataUtil;

	@Autowired
	private ProgramService programService;

	@Autowired
	private VariableDataValidatorFactory variableDataValidatorFactory;

	@Autowired
	private ExperimentModelGenerator experimentModelGenerator;

	@Autowired
	private StudyDataManager studyDataManager;

	private DaoFactory daoFactory;
	private WorkbenchDaoFactory workbenchDaoFactory;
	private IntegrationTestDataInitializer testDataInitializer;
	private Project commonTestProject;
	private CropType crop;
	private WorkbenchUser testUser;
	private DmsProject study;
	private ExperimentModel studyExperiment;

	@Before
	public void setUp() {

		this.daoFactory = new DaoFactory(this.sessionProvder);
		this.workbenchDaoFactory = Mockito.spy(new WorkbenchDaoFactory(this.workbenchSessionProvider));

		this.trialServiceBrapi = new TrialServiceBrapiImpl(this.sessionProvder, this.workbenchSessionProvider);
		this.trialServiceBrapi.setExperimentModelGenerator(this.experimentModelGenerator);
		this.trialServiceBrapi.setVariableDataValidatorFactory(this.variableDataValidatorFactory);
		this.trialServiceBrapi.setStudyDataManager(this.studyDataManager);
		this.trialServiceBrapi.setWorkbenchDaoFactory(this.workbenchDaoFactory);
		this.trialServiceBrapi.setDaoFactory(this.daoFactory);
		this.trialServiceBrapi.setWorkbenchDaoFactory(this.workbenchDaoFactory);

		this.workbenchTestDataUtil.setUpWorkbench(this.workbenchDaoFactory);
		if (this.commonTestProject == null) {
			this.commonTestProject = this.workbenchTestDataUtil.getCommonTestProject();
			this.crop = this.programService.getProjectByUuid(this.commonTestProject.getUniqueID()).getCropType();
		}

		this.testDataInitializer = new IntegrationTestDataInitializer(this.sessionProvder, this.workbenchSessionProvider);
		this.testUser = this.testDataInitializer.createUserForTesting();
		this.study = this.testDataInitializer
			.createStudy("Study1", "Study-Description", 6, this.commonTestProject.getUniqueID(), this.testUser.getUserid().toString(),
				"20180205", null);

		final DmsProject plot = this.testDataInitializer
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
		this.sessionProvder.getSession().flush();
	}

	@Test
	public void testCountSearchTrials() {
		// Empty filter will retrieve all studies in crop
		final TrialSearchRequestDTO trialSearchRequestDTO = new TrialSearchRequestDTO();
		final long initialCount = this.trialServiceBrapi.countSearchTrials(trialSearchRequestDTO);

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
		Assert.assertEquals((int) initialCount + 1, this.trialServiceBrapi.countSearchTrials(trialSearchRequestDTO));
		// Expecting only seeded studies for this test class/method to be retrieved when filtered by programUUID
		trialSearchRequestDTO.setProgramDbIds(Arrays.asList(this.commonTestProject.getUniqueID()));
		Assert
			.assertEquals(2, this.trialServiceBrapi.countSearchTrials(trialSearchRequestDTO));
		trialSearchRequestDTO.setLocationDbIds(Arrays.asList(String.valueOf(location1)));
		// Expecting only one to be retrieved when filtered by location
		Assert.assertEquals(1, this.trialServiceBrapi.countSearchTrials(trialSearchRequestDTO));
	}

	@Test
	public void testSearchTrials() {
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
		// Add contact name variable
		final String contactName = RandomStringUtils.randomAlphabetic(20);
		this.testDataInitializer.addProjectProp(newStudy, ContactVariable.CONTACT_NAME.getId(), "CONTACT_NAME", VariableType.STUDY_DETAIL,
			contactName, 1);
		// Flushing to force Hibernate to synchronize with the underlying database
		this.sessionProvder.getSession().flush();

		// Expecting only seeded studies for this test class/method to be retrieved when filtered by programUUID, sorted by descending study name
		final TrialSearchRequestDTO trialSearchRequestDTO = new TrialSearchRequestDTO();
		trialSearchRequestDTO.setProgramDbIds(Arrays.asList(this.commonTestProject.getUniqueID()));
		List<TrialSummary> trialSummaries =
			this.trialServiceBrapi.searchTrials(
				trialSearchRequestDTO, new PageRequest(0, 10, new Sort(Sort.Direction.fromString("desc"), "trialName")));
		Assert.assertEquals(2, trialSummaries.size());
		TrialSummary trial1 = trialSummaries.get(1);
		Assert.assertEquals(this.study.getProjectId(), trial1.getTrialDbId());
		Assert.assertEquals(this.study.getName(), trial1.getName());
		Assert.assertEquals(this.study.getDescription(), trial1.getDescription());
		Assert.assertEquals(this.study.getProgramUUID(), trial1.getProgramDbId());
		Assert.assertEquals(this.studyExperiment.getObsUnitId(), trial1.getObservationUnitId());
		Assert.assertEquals(0, trial1.getContacts().size());
		Assert.assertTrue(trial1.isActive());

		final TrialSummary trial2 = trialSummaries.get(0);
		Assert.assertEquals(newStudy.getProjectId(), trial2.getTrialDbId());
		Assert.assertEquals(newStudy.getName(), trial2.getName());
		Assert.assertEquals(newStudy.getDescription(), trial2.getDescription());
		Assert.assertEquals(newStudy.getProgramUUID(), trial2.getProgramDbId());
		Assert.assertEquals(newStudyExperiment.getObsUnitId(), trial2.getObservationUnitId());
		Assert.assertEquals(1, trial2.getInstanceMetaData().size());
		Assert.assertEquals(1, trial2.getContacts().size());
		Assert.assertEquals(contactName, trial2.getContacts().get(0).getName());
		Assert.assertFalse(trial2.isActive());

		trialSearchRequestDTO.setLocationDbIds(Arrays.asList(String.valueOf(location1)));
		// Expecting only one study to be retrieved when filtered by location
		trialSummaries = this.trialServiceBrapi.searchTrials(trialSearchRequestDTO, null);
		Assert.assertEquals(1, trialSummaries.size());
		trial1 = trialSummaries.get(0);
		Assert.assertEquals(newStudy.getProjectId(), trial1.getTrialDbId());
		// Expecting environments of retrieved study to also be filtered by location
		Assert.assertEquals(1, trial1.getInstanceMetaData().size());
		Assert.assertEquals(String.valueOf(location1), trial1.getInstanceMetaData().get(0).getLocationDbId().toString());
	}

	@Test
	public void testSearchTrials_FilterByStudyExternalReference() {
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

		final TrialSearchRequestDTO trialSearchRequestDTO = new TrialSearchRequestDTO();
		trialSearchRequestDTO.setExternalReferenceSources(Arrays.asList(studyExternalReference.getSource()));
		trialSearchRequestDTO.setExternalReferenceIds(Arrays.asList(studyExternalReference.getReferenceId()));

		final List<TrialSummary> trialSummaries =
			this.trialServiceBrapi.searchTrials(
				trialSearchRequestDTO, new PageRequest(0, 10, new Sort(Sort.Direction.fromString("desc"), "trialName")));
		Assert.assertEquals(1, trialSummaries.size());
		final TrialSummary study2 = trialSummaries.get(0);
		Assert.assertEquals(newStudy.getProjectId(), study2.getTrialDbId());
		Assert.assertEquals(newStudy.getName(), study2.getName());
	}

	@Test
	public void testCountSearchTrials_FilterByStudyExternalReference() {
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

		final TrialSearchRequestDTO trialSearchRequestDTO = new TrialSearchRequestDTO();
		trialSearchRequestDTO.setExternalReferenceSources(Arrays.asList(studyExternalReference.getSource()));
		trialSearchRequestDTO.setExternalReferenceIds(Arrays.asList(studyExternalReference.getReferenceId()));

		Assert.assertEquals(1, this.trialServiceBrapi.countSearchTrials(trialSearchRequestDTO));
	}

	@Test
	public void testSearchTrialsWithDeletedStudy() {
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
		final TrialSearchRequestDTO trialSearchRequestDTO = new TrialSearchRequestDTO();
		trialSearchRequestDTO.setProgramDbIds(Arrays.asList(this.commonTestProject.getUniqueID()));
		List<TrialSummary> trialSummaries =
			this.trialServiceBrapi.searchTrials(
				trialSearchRequestDTO, new PageRequest(0, 10, new Sort(Sort.Direction.fromString("desc"), "trialName")));
		Assert.assertEquals("Deleted study is not included", 2, trialSummaries.size());
		TrialSummary study1 = trialSummaries.get(1);
		Assert.assertEquals(this.study.getProjectId(), study1.getTrialDbId());
		Assert.assertEquals(this.study.getName(), study1.getName());
		Assert.assertEquals(this.study.getDescription(), study1.getDescription());
		Assert.assertEquals(this.study.getProgramUUID(), study1.getProgramDbId());
		Assert.assertEquals(this.studyExperiment.getObsUnitId(), study1.getObservationUnitId());
		Assert.assertEquals(0, study1.getContacts().size());
		final TrialSummary study2 = trialSummaries.get(0);
		Assert.assertEquals(newStudy.getProjectId(), study2.getTrialDbId());
		Assert.assertEquals(newStudy.getName(), study2.getName());
		Assert.assertEquals(newStudy.getDescription(), study2.getDescription());
		Assert.assertEquals(newStudy.getProgramUUID(), study2.getProgramDbId());
		Assert.assertEquals(newStudyExperiment.getObsUnitId(), study2.getObservationUnitId());
		Assert.assertEquals(1, study2.getInstanceMetaData().size());
		Assert.assertEquals(0, study2.getContacts().size());

		// Expecting only one study to be retrieved when filtered by location
		trialSearchRequestDTO.setLocationDbIds(Arrays.asList(String.valueOf(location1)));
		trialSummaries = this.trialServiceBrapi.searchTrials(trialSearchRequestDTO, null);
		Assert.assertEquals(1, trialSummaries.size());
		study1 = trialSummaries.get(0);
		Assert.assertEquals(newStudy.getProjectId(), study1.getTrialDbId());
		// Expecting environments of retrieved study to also be filtered by location
		Assert.assertEquals(1, study1.getInstanceMetaData().size());
		Assert.assertEquals(String.valueOf(location1), study1.getInstanceMetaData().get(0).getLocationDbId().toString());
	}

	@Test
	public void testCountSearchTrialsWithDeletedStudy() {
		// Empty filter will retrieve all studies in crop
		final TrialSearchRequestDTO trialSearchRequestDTO = new TrialSearchRequestDTO();
		final long initialCount = this.trialServiceBrapi.countSearchTrials(trialSearchRequestDTO);

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
		Assert.assertEquals((int) initialCount + 1, this.trialServiceBrapi.countSearchTrials(trialSearchRequestDTO));
		// Expecting only seeded studies for this test class/method to be retrieved when filtered by programUUID
		trialSearchRequestDTO.setProgramDbIds(Arrays.asList(this.commonTestProject.getUniqueID()));
		Assert
			.assertEquals(2, this.trialServiceBrapi.countSearchTrials(trialSearchRequestDTO));
		trialSearchRequestDTO.setLocationDbIds(Arrays.asList(String.valueOf(location1)));
		// Expecting only one to be retrieved when filtered by location
		Assert.assertEquals(1, this.trialServiceBrapi.countSearchTrials(trialSearchRequestDTO));
	}

	@Test
	public void testSaveTrials_AllInfoSaved() {
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

		final ContactDto contact1 = new ContactDto(StringUtils.EMPTY, RandomStringUtils.randomAlphabetic(20),
			RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20), StringUtils.EMPTY,
			RandomStringUtils.randomAlphabetic(20));
		importRequest1.setContacts(Collections.singletonList(contact1));
		final TrialImportRequestDTO importRequest2 = new TrialImportRequestDTO();
		importRequest2.setStartDate("2019-01-01");
		importRequest2.setTrialDescription(RandomStringUtils.randomAlphabetic(20));
		importRequest2.setTrialName(RandomStringUtils.randomAlphabetic(20));
		importRequest2.setProgramDbId(this.commonTestProject.getUniqueID());
		final ExternalReferenceDTO externalReference = new ExternalReferenceDTO();
		externalReference.setReferenceID(RandomStringUtils.randomAlphabetic(20));
		externalReference.setReferenceSource(RandomStringUtils.randomAlphabetic(20));
		importRequest2.setExternalReferences(Collections.singletonList(externalReference));

		final List<TrialSummary> savedStudies = this.trialServiceBrapi
			.saveTrials(this.crop.getCropName(), Arrays.asList(importRequest1, importRequest2), this.testUser.getUserid());
		Assert.assertEquals(2, savedStudies.size());
		final TrialSummary study1 = savedStudies.get(0);
		this.verifyStudySummary(importRequest1, study1);
		Assert.assertNotNull(study1.getContacts());
		Assert.assertEquals(1, study1.getContacts().size());
		Assert.assertEquals(contact1, study1.getContacts().get(0));
		Assert.assertTrue(StringUtils.isEmpty(study1.getContacts().get(0).getContactDbId()));
		Assert.assertTrue(StringUtils.isEmpty(study1.getContacts().get(0).getOrcid()));
		// Verify study settings, only first study imported study details
		Assert.assertFalse(CollectionUtils.isEmpty(study1.getAdditionalInfo()));
		Assert.assertEquals(4, study1.getAdditionalInfo().size());
		for (final String key : study1.getAdditionalInfo().keySet()) {
			Assert.assertEquals(importRequest1.getAdditionalInfo().get(key), study1.getAdditionalInfo().get(key));
		}

		final TrialSummary study2 = savedStudies.get(1);
		this.verifyStudySummary(importRequest2, study2);
		Assert.assertTrue(CollectionUtils.isEmpty(study2.getContacts()));
	}

	@Test
	public void testSaveTrials_WithInvalidStudyVariableNames() {
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

		final List<TrialSummary> savedStudies =
			this.trialServiceBrapi.saveTrials(this.crop.getCropName(), Collections.singletonList(importRequest1),
				this.testUser.getUserid());
		Assert.assertEquals(1, savedStudies.size());
		final TrialSummary study1 = savedStudies.get(0);
		this.verifyStudySummary(importRequest1, study1);
		Assert.assertNotNull(study1.getAdditionalInfo());
		// Verify invalid study variable name was not saved, but the valid ones were
		Assert.assertEquals(importRequest1.getAdditionalInfo().size() - 1, study1.getAdditionalInfo().size());
		Assert.assertFalse(study1.getAdditionalInfo().keySet().contains(invalidVariableName));
	}

	@Test
	public void testSaveTrials_WithInvalidStudyVariableValues() {
		final TrialImportRequestDTO importRequest1 = new TrialImportRequestDTO();
		importRequest1.setStartDate("2019-01-01");
		importRequest1.setEndDate("2020-12-31");
		importRequest1.setTrialDescription(RandomStringUtils.randomAlphabetic(20));
		importRequest1.setTrialName(RandomStringUtils.randomAlphabetic(20));
		importRequest1.setProgramDbId(this.commonTestProject.getUniqueID());

		final CVTerm numericVariable =
			this.testDataInitializer.createVariableWithScale(DataType.NUMERIC_VARIABLE, VariableType.STUDY_DETAIL);
		final CVTerm dateTimeVariable =
			this.testDataInitializer.createVariableWithScale(DataType.DATE_TIME_VARIABLE, VariableType.STUDY_DETAIL);
		final List<String> possibleValues = Arrays
			.asList(RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20));
		final CVTerm categoricalVariable = this.testDataInitializer.createCategoricalVariable(VariableType.STUDY_DETAIL, possibleValues);

		final Map<String, String> settingsMap = Maps.newHashMap();
		settingsMap.put(numericVariable.getName(), RandomStringUtils.randomAlphabetic(30));
		settingsMap.put(dateTimeVariable.getName(), "2021-05-01");
		settingsMap.put(categoricalVariable.getName(), RandomStringUtils.randomAlphabetic(30));
		importRequest1.setAdditionalInfo(settingsMap);

		final List<TrialSummary> savedStudies =
			this.trialServiceBrapi.saveTrials(this.crop.getCropName(), Collections.singletonList(importRequest1),
				this.testUser.getUserid());
		Assert.assertEquals(1, savedStudies.size());
		final TrialSummary study1 = savedStudies.get(0);
		this.verifyStudySummary(importRequest1, study1);
		final List<ProjectProperty> projectProperties = this.daoFactory.getProjectPropertyDAO().getByProjectId(study1.getTrialDbId());

		// Verify that study settings are saved even if their values are invalid.
		Assert.assertEquals(3, projectProperties.size());
		Assert.assertTrue(projectProperties.stream().filter(pp -> pp.getAlias().equals(numericVariable.getName())).findAny().isPresent());
		Assert.assertTrue(projectProperties.stream().filter(pp -> pp.getAlias().equals(dateTimeVariable.getName())).findAny().isPresent());
		Assert.assertTrue(
			projectProperties.stream().filter(pp -> pp.getAlias().equals(categoricalVariable.getName())).findAny().isPresent());
	}

	@Test
	public void testSaveTrials_WithValidVariablesHavingNonStudyDetailVariableType() {
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

		final List<TrialSummary> savedStudies =
			this.trialServiceBrapi.saveTrials(this.crop.getCropName(), Collections.singletonList(importRequest1),
				this.testUser.getUserid());
		Assert.assertEquals(1, savedStudies.size());
		final TrialSummary study1 = savedStudies.get(0);
		this.verifyStudySummary(importRequest1, study1);
		Assert.assertTrue(CollectionUtils.isEmpty(study1.getAdditionalInfo()));
	}

	@Test
	public void testSaveTrials_WithCooperatorVariable() {
		final TrialImportRequestDTO importRequest1 = new TrialImportRequestDTO();
		importRequest1.setStartDate("2019-01-01");
		importRequest1.setEndDate("2020-12-31");
		importRequest1.setTrialDescription(RandomStringUtils.randomAlphabetic(20));
		importRequest1.setTrialName(RandomStringUtils.randomAlphabetic(20));
		importRequest1.setProgramDbId(this.commonTestProject.getUniqueID());

		final Map<String, String> settingsMap = Maps.newHashMap();

		final CVTerm cooperatorVariable = this.daoFactory.getCvTermDao().getById(TermId.COOPERATOR.getId());
		final CVTerm cooperatorIdVariable = this.daoFactory.getCvTermDao().getById(TermId.COOPERATOOR_ID.getId());

		final WorkbenchUser user = this.workbenchDaoFactory.getWorkbenchUserDAO().getById(1);
		settingsMap
			.put(cooperatorVariable.getName(), user.getPerson().getDisplayName());
		settingsMap
			.put(cooperatorIdVariable.getName(), "");

		importRequest1.setAdditionalInfo(settingsMap);

		final List<TrialSummary> savedStudies =
			this.trialServiceBrapi.saveTrials(this.crop.getCropName(), Collections.singletonList(importRequest1),
				this.testUser.getUserid());

		Assert.assertEquals(1, savedStudies.size());
		final TrialSummary study1 = savedStudies.get(0);

		Assert.assertEquals(user.getPerson().getDisplayName(), study1.getAdditionalInfo().get(cooperatorVariable.getName()));
		Assert.assertEquals("1", study1.getAdditionalInfo().get(cooperatorIdVariable.getName()));
	}

	@Test
	public void testSaveTrials_WithCooperatorVariable_PersonDoesNotExist() {
		final TrialImportRequestDTO importRequest1 = new TrialImportRequestDTO();
		importRequest1.setStartDate("2019-01-01");
		importRequest1.setEndDate("2020-12-31");
		importRequest1.setTrialDescription(RandomStringUtils.randomAlphabetic(20));
		importRequest1.setTrialName(RandomStringUtils.randomAlphabetic(20));
		importRequest1.setProgramDbId(this.commonTestProject.getUniqueID());

		final Map<String, String> settingsMap = Maps.newHashMap();

		final CVTerm cooperatorVariable = this.daoFactory.getCvTermDao().getById(TermId.COOPERATOR.getId());
		final CVTerm cooperatorIdVariable = this.daoFactory.getCvTermDao().getById(TermId.COOPERATOOR_ID.getId());

		final String personFullName = RandomStringUtils.randomAlphabetic(10);
		settingsMap
			.put(cooperatorVariable.getName(), personFullName);
		settingsMap
			.put(cooperatorIdVariable.getName(), "1");

		importRequest1.setAdditionalInfo(settingsMap);

		final List<TrialSummary> savedStudies =
			this.trialServiceBrapi.saveTrials(this.crop.getCropName(), Collections.singletonList(importRequest1),
				this.testUser.getUserid());
		Assert.assertEquals(1, savedStudies.size());
		final TrialSummary study1 = savedStudies.get(0);

		Assert.assertTrue(CollectionUtils.isEmpty(study1.getAdditionalInfo()));
	}

	@Test
	public void testSaveTrials_WithCooperatorVariable_PersonMultipleMatches() {
		final TrialImportRequestDTO importRequest1 = new TrialImportRequestDTO();
		importRequest1.setStartDate("2019-01-01");
		importRequest1.setEndDate("2020-12-31");
		importRequest1.setTrialDescription(RandomStringUtils.randomAlphabetic(20));
		importRequest1.setTrialName(RandomStringUtils.randomAlphabetic(20));
		importRequest1.setProgramDbId(this.commonTestProject.getUniqueID());

		final Map<String, String> settingsMap = Maps.newHashMap();

		final WorkbenchUserDAO workbenchUserDAO = Mockito.mock(WorkbenchUserDAO.class);
		Mockito.doReturn(workbenchUserDAO).when(this.workbenchDaoFactory).getWorkbenchUserDAO();
		Mockito.when(workbenchUserDAO.countUsersByFullName(this.testUser.getPerson().getDisplayName())).thenReturn(2l);

		final CVTerm cooperatorVariable = this.daoFactory.getCvTermDao().getById(TermId.COOPERATOR.getId());
		final CVTerm cooperatorIdVariable = this.daoFactory.getCvTermDao().getById(TermId.COOPERATOOR_ID.getId());

		settingsMap
			.put(cooperatorVariable.getName(), this.testUser.getPerson().getDisplayName());
		settingsMap
			.put(cooperatorIdVariable.getName(), "");

		importRequest1.setAdditionalInfo(settingsMap);

		final List<TrialSummary> savedStudies =
			this.trialServiceBrapi.saveTrials(this.crop.getCropName(), Collections.singletonList(importRequest1),
				this.testUser.getUserid());
		Assert.assertEquals(1, savedStudies.size());
		final TrialSummary study1 = savedStudies.get(0);

		Assert.assertTrue(CollectionUtils.isEmpty(study1.getAdditionalInfo()));
	}

	@Test
	public void testSaveTrials_WithCooperatorVariable_PersonExistButNoAccessToCrop() {
		final TrialImportRequestDTO importRequest1 = new TrialImportRequestDTO();
		importRequest1.setStartDate("2019-01-01");
		importRequest1.setEndDate("2020-12-31");
		importRequest1.setTrialDescription(RandomStringUtils.randomAlphabetic(20));
		importRequest1.setTrialName(RandomStringUtils.randomAlphabetic(20));
		importRequest1.setProgramDbId(this.commonTestProject.getUniqueID());

		final Map<String, String> settingsMap = Maps.newHashMap();

		final WorkbenchUserDAO workbenchUserDAO = Mockito.mock(WorkbenchUserDAO.class);
		final CropPersonDAO cropPersonDAO = Mockito.mock(CropPersonDAO.class);
		Mockito.doReturn(workbenchUserDAO).when(this.workbenchDaoFactory).getWorkbenchUserDAO();
		Mockito.doReturn(cropPersonDAO).when(this.workbenchDaoFactory).getCropPersonDAO();
		Mockito.when(workbenchUserDAO.countUsersByFullName(this.testUser.getPerson().getDisplayName())).thenReturn(1l);
		Mockito.when(workbenchUserDAO.getUserByFullName(this.testUser.getPerson().getDisplayName())).thenReturn(Optional.of(this.testUser));
		Mockito.when(cropPersonDAO.getByCropNameAndPersonId(this.testUser.getPerson().getDisplayName(), this.testUser.getPerson().getId()))
			.thenReturn(null);

		final CVTerm cooperatorVariable = this.daoFactory.getCvTermDao().getById(TermId.COOPERATOR.getId());
		final CVTerm cooperatorIdVariable = this.daoFactory.getCvTermDao().getById(TermId.COOPERATOOR_ID.getId());

		settingsMap
			.put(cooperatorVariable.getName(), this.testUser.getPerson().getDisplayName());
		settingsMap
			.put(cooperatorIdVariable.getName(), "");

		importRequest1.setAdditionalInfo(settingsMap);

		final List<TrialSummary> savedStudies =
			this.trialServiceBrapi.saveTrials(this.crop.getCropName(), Collections.singletonList(importRequest1),
				this.testUser.getUserid());
		Assert.assertEquals(1, savedStudies.size());
		final TrialSummary study1 = savedStudies.get(0);

		Assert.assertTrue(CollectionUtils.isEmpty(study1.getAdditionalInfo()));
	}

	@Test
	public void testSaveTrials_WithPrincipalInvestigatorVariable() {
		final TrialImportRequestDTO importRequest1 = new TrialImportRequestDTO();
		importRequest1.setStartDate("2019-01-01");
		importRequest1.setEndDate("2020-12-31");
		importRequest1.setTrialDescription(RandomStringUtils.randomAlphabetic(20));
		importRequest1.setTrialName(RandomStringUtils.randomAlphabetic(20));
		importRequest1.setProgramDbId(this.commonTestProject.getUniqueID());

		final Map<String, String> settingsMap = Maps.newHashMap();

		final CVTerm piNameVariable = this.daoFactory.getCvTermDao().getById(TermId.PI_NAME.getId());
		final CVTerm piNameIDVariable = this.daoFactory.getCvTermDao().getById(TermId.PI_ID.getId());

		final WorkbenchUser user = this.workbenchDaoFactory.getWorkbenchUserDAO().getById(1);
		settingsMap
			.put(piNameVariable.getName(), user.getPerson().getDisplayName());
		settingsMap
			.put(piNameIDVariable.getName(), "1");

		importRequest1.setAdditionalInfo(settingsMap);

		final List<TrialSummary> savedStudies =
			this.trialServiceBrapi.saveTrials(this.crop.getCropName(), Collections.singletonList(importRequest1),
				this.testUser.getUserid());
		Assert.assertEquals(1, savedStudies.size());
		final TrialSummary study1 = savedStudies.get(0);

		Assert.assertEquals(user.getPerson().getDisplayName(), study1.getAdditionalInfo().get(piNameVariable.getName()));
		Assert.assertEquals("1", study1.getAdditionalInfo().get(piNameIDVariable.getName()));
	}

	@Test
	public void testSaveTrials_WithPrincipalInvestigatorVariable_PersonDoesNotExist() {
		final TrialImportRequestDTO importRequest1 = new TrialImportRequestDTO();
		importRequest1.setStartDate("2019-01-01");
		importRequest1.setEndDate("2020-12-31");
		importRequest1.setTrialDescription(RandomStringUtils.randomAlphabetic(20));
		importRequest1.setTrialName(RandomStringUtils.randomAlphabetic(20));
		importRequest1.setProgramDbId(this.commonTestProject.getUniqueID());

		final Map<String, String> settingsMap = Maps.newHashMap();

		final CVTerm piNameVariable = this.daoFactory.getCvTermDao().getById(TermId.PI_NAME.getId());
		final CVTerm piNameIDVariable = this.daoFactory.getCvTermDao().getById(TermId.PI_ID.getId());

		final String personFullName = RandomStringUtils.randomAlphabetic(10);
		settingsMap
			.put(piNameVariable.getName(), personFullName);
		settingsMap
			.put(piNameIDVariable.getName(), "1");

		importRequest1.setAdditionalInfo(settingsMap);

		final List<TrialSummary> savedStudies =
			this.trialServiceBrapi.saveTrials(this.crop.getCropName(), Collections.singletonList(importRequest1),
				this.testUser.getUserid());
		Assert.assertEquals(1, savedStudies.size());
		final TrialSummary study1 = savedStudies.get(0);

		Assert.assertTrue(CollectionUtils.isEmpty(study1.getAdditionalInfo()));
	}

	@Test
	public void testSaveTrials_WithPrincipalInvestigatorVariable_PersonMultipleMatches() {
		final TrialImportRequestDTO importRequest1 = new TrialImportRequestDTO();
		importRequest1.setStartDate("2019-01-01");
		importRequest1.setEndDate("2020-12-31");
		importRequest1.setTrialDescription(RandomStringUtils.randomAlphabetic(20));
		importRequest1.setTrialName(RandomStringUtils.randomAlphabetic(20));
		importRequest1.setProgramDbId(this.commonTestProject.getUniqueID());

		final Map<String, String> settingsMap = Maps.newHashMap();

		final WorkbenchUserDAO workbenchUserDAO = Mockito.mock(WorkbenchUserDAO.class);
		Mockito.doReturn(workbenchUserDAO).when(this.workbenchDaoFactory).getWorkbenchUserDAO();
		Mockito.when(workbenchUserDAO.countUsersByFullName(this.testUser.getPerson().getDisplayName())).thenReturn(2l);

		final CVTerm piNameVariable = this.daoFactory.getCvTermDao().getById(TermId.PI_NAME.getId());
		final CVTerm piNameIDVariable = this.daoFactory.getCvTermDao().getById(TermId.PI_ID.getId());

		settingsMap
			.put(piNameVariable.getName(), this.testUser.getPerson().getDisplayName());
		settingsMap
			.put(piNameIDVariable.getName(), "1");

		importRequest1.setAdditionalInfo(settingsMap);

		final List<TrialSummary> savedStudies =
			this.trialServiceBrapi.saveTrials(this.crop.getCropName(), Collections.singletonList(importRequest1),
				this.testUser.getUserid());
		Assert.assertEquals(1, savedStudies.size());
		final TrialSummary study1 = savedStudies.get(0);

		Assert.assertTrue(CollectionUtils.isEmpty(study1.getAdditionalInfo()));
	}

	@Test
	public void testSaveTrials_WithPrincipalInvestigatorVariable_PersonExistButNoAccessToCrop() {
		final TrialImportRequestDTO importRequest1 = new TrialImportRequestDTO();
		importRequest1.setStartDate("2019-01-01");
		importRequest1.setEndDate("2020-12-31");
		importRequest1.setTrialDescription(RandomStringUtils.randomAlphabetic(20));
		importRequest1.setTrialName(RandomStringUtils.randomAlphabetic(20));
		importRequest1.setProgramDbId(this.commonTestProject.getUniqueID());

		final Map<String, String> settingsMap = Maps.newHashMap();

		final WorkbenchUserDAO workbenchUserDAO = Mockito.mock(WorkbenchUserDAO.class);
		final CropPersonDAO cropPersonDAO = Mockito.mock(CropPersonDAO.class);
		Mockito.doReturn(workbenchUserDAO).when(this.workbenchDaoFactory).getWorkbenchUserDAO();
		Mockito.doReturn(cropPersonDAO).when(this.workbenchDaoFactory).getCropPersonDAO();
		Mockito.when(workbenchUserDAO.countUsersByFullName(this.testUser.getPerson().getDisplayName())).thenReturn(1l);
		Mockito.when(workbenchUserDAO.getUserByFullName(this.testUser.getPerson().getDisplayName())).thenReturn(Optional.of(this.testUser));
		Mockito.when(cropPersonDAO.getByCropNameAndPersonId(this.testUser.getPerson().getDisplayName(), this.testUser.getPerson().getId()))
			.thenReturn(null);

		final CVTerm piNameVariable = this.daoFactory.getCvTermDao().getById(TermId.PI_NAME.getId());
		final CVTerm piNameIDVariable = this.daoFactory.getCvTermDao().getById(TermId.PI_ID.getId());

		settingsMap
			.put(piNameVariable.getName(), this.testUser.getPerson().getDisplayName());
		settingsMap
			.put(piNameIDVariable.getName(), "1");

		importRequest1.setAdditionalInfo(settingsMap);

		final List<TrialSummary> savedStudies =
			this.trialServiceBrapi.saveTrials(this.crop.getCropName(), Collections.singletonList(importRequest1),
				this.testUser.getUserid());
		Assert.assertEquals(1, savedStudies.size());
		final TrialSummary study1 = savedStudies.get(0);

		Assert.assertTrue(CollectionUtils.isEmpty(study1.getAdditionalInfo()));
	}

	private void verifyStudySummary(final TrialImportRequestDTO importRequestDTO, final TrialSummary study) {
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
