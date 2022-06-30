package org.generationcp.middleware.brapi;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.api.brapi.StudyServiceBrapi;
import org.generationcp.middleware.api.brapi.TrialServiceBrapi;
import org.generationcp.middleware.api.brapi.v2.germplasm.ExternalReferenceDTO;
import org.generationcp.middleware.api.brapi.v2.study.StudyImportRequestDTO;
import org.generationcp.middleware.api.brapi.v2.trial.TrialImportRequestDTO;
import org.generationcp.middleware.api.crop.CropService;
import org.generationcp.middleware.domain.dms.StudySummary;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.study.EnvironmentParameter;
import org.generationcp.middleware.service.api.study.StudyDetailsDto;
import org.generationcp.middleware.service.api.study.StudyInstanceDto;
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

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class StudyServiceBrapiImplTest extends IntegrationTestBase {

	@Resource
	private TrialServiceBrapi trialServiceBrapi;

	@Resource
	private StudyServiceBrapi studyServiceBrapi;

	@Autowired
	private WorkbenchTestDataUtil workbenchTestDataUtil;

	@Autowired
	private CropService cropService;

	private DaoFactory daoFactory;
	private IntegrationTestDataInitializer testDataInitializer;
	private CropType cropType;
	private WorkbenchUser testUser;
	private Project commonTestProject;
	private DmsProject study;

	@Before
	public void setup() throws Exception {

		this.daoFactory = new DaoFactory(this.sessionProvder);

		if (this.commonTestProject == null) {
			this.commonTestProject = this.workbenchTestDataUtil.getCommonTestProject();
		}

		this.testDataInitializer = new IntegrationTestDataInitializer(this.sessionProvder, this.workbenchSessionProvider);
		this.cropType = this.cropService.getCropTypeByName(CropType.CropEnum.MAIZE.name());
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
	public void testSaveStudyInstance_AllInfoSaved() {
		final StudySummary trial = this.createTrial();
		final StudyImportRequestDTO dto = new StudyImportRequestDTO();
		dto.setTrialDbId(String.valueOf(trial.getTrialDbId()));
		dto.setLocationDbId("0");
		final List<ValueReference> categoricalValues = this.daoFactory.getCvTermRelationshipDao()
			.getCategoriesForCategoricalVariables(Collections.singletonList(TermId.SEASON_VAR.getId())).get(TermId.SEASON_VAR.getId());
		dto.setSeasons(Collections.singletonList(categoricalValues.get(0).getDescription()));

		final CVTerm numericVariable =
			this.testDataInitializer.createVariableWithScale(DataType.NUMERIC_VARIABLE, VariableType.ENVIRONMENT_DETAIL);
		final EnvironmentParameter numericEnvironmentParameter = new EnvironmentParameter();
		numericEnvironmentParameter.setValue("1");
		numericEnvironmentParameter.setParameterPUI(numericVariable.getCvTermId().toString());

		final List<String> possibleValues = Arrays
			.asList(RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20));
		final CVTerm categoricalVariable = this.testDataInitializer
			.createCategoricalVariable(VariableType.ENVIRONMENT_CONDITION, possibleValues);
		final EnvironmentParameter categoricalEnvironmentParameter = new EnvironmentParameter();
		categoricalEnvironmentParameter.setParameterPUI(categoricalVariable.getCvTermId().toString());
		categoricalEnvironmentParameter.setValue(possibleValues.get(0));
		dto.setEnvironmentParameters(Arrays.asList(numericEnvironmentParameter, categoricalEnvironmentParameter));

		final ExternalReferenceDTO externalReference = new ExternalReferenceDTO();
		externalReference.setReferenceID(RandomStringUtils.randomAlphabetic(20));
		externalReference.setReferenceSource(RandomStringUtils.randomAlphabetic(20));
		dto.setExternalReferences(Collections.singletonList(externalReference));

		this.sessionProvder.getSession().flush();

		final StudyInstanceDto savedInstance = this.studyServiceBrapi
			.saveStudyInstances(this.cropType.getCropName(), Collections.singletonList(dto), this.testUser.getUserid()).get(0);

		Assert.assertEquals(dto.getTrialDbId(), savedInstance.getTrialDbId());
		Assert.assertEquals(dto.getLocationDbId(), savedInstance.getLocationDbId());
		Assert.assertEquals(3, savedInstance.getEnvironmentParameters().size());
		Assert.assertEquals(1, savedInstance.getExternalReferences().size());
		Assert.assertEquals(externalReference.getReferenceID(), savedInstance.getExternalReferences().get(0).getReferenceID());
		Assert.assertEquals(externalReference.getReferenceSource(), savedInstance.getExternalReferences().get(0).getReferenceSource());
		Assert.assertEquals(String.valueOf(TermId.EXTERNALLY_GENERATED.getId()), savedInstance.getExperimentalDesign().getPUI());
		Assert.assertEquals(dto.getSeasons().get(0), savedInstance.getSeasons().get(0).getSeason());
	}

	@Test
	public void testSaveStudyInstances_WithValidVariableHavingInvalidVariableType() {
		final StudySummary trial = this.createTrial();
		final StudyImportRequestDTO dto = new StudyImportRequestDTO();
		dto.setTrialDbId(String.valueOf(trial.getTrialDbId()));
		dto.setLocationDbId("0");

		final CVTerm numericVariable =
			this.testDataInitializer.createVariableWithScale(DataType.NUMERIC_VARIABLE, VariableType.STUDY_DETAIL);
		final EnvironmentParameter numericEnviromentParameter = new EnvironmentParameter();
		numericEnviromentParameter.setValue("1");
		numericEnviromentParameter.setParameterPUI(numericVariable.getCvTermId().toString());
		dto.setEnvironmentParameters(Collections.singletonList(numericEnviromentParameter));

		this.sessionProvder.getSession().flush();

		final StudyInstanceDto savedInstance = this.studyServiceBrapi
			.saveStudyInstances(this.cropType.getCropName(), Collections.singletonList(dto), this.testUser.getUserid()).get(0);
		Assert.assertEquals(dto.getTrialDbId(), savedInstance.getTrialDbId());
		Assert.assertEquals(dto.getLocationDbId(), savedInstance.getLocationDbId());
		Assert.assertTrue(CollectionUtils.isEmpty(savedInstance.getEnvironmentParameters()));
	}

	@Test
	public void testSaveStudyInstances_WithValidVariableHavingInvalidVariableValue() {
		final StudySummary trial = this.createTrial();
		final StudyImportRequestDTO dto = new StudyImportRequestDTO();
		dto.setTrialDbId(String.valueOf(trial.getTrialDbId()));
		dto.setLocationDbId("0");

		final CVTerm numericVariable =
			this.testDataInitializer.createVariableWithScale(DataType.NUMERIC_VARIABLE, VariableType.ENVIRONMENT_CONDITION);
		final EnvironmentParameter numericEnviromentParameter = new EnvironmentParameter();
		numericEnviromentParameter.setValue("NON NUMERIC");
		numericEnviromentParameter.setParameterPUI(numericVariable.getCvTermId().toString());
		dto.setEnvironmentParameters(Collections.singletonList(numericEnviromentParameter));

		this.sessionProvder.getSession().flush();

		final StudyInstanceDto savedInstance = this.studyServiceBrapi
			.saveStudyInstances(this.cropType.getCropName(), Collections.singletonList(dto), this.testUser.getUserid()).get(0);
		Assert.assertEquals(dto.getTrialDbId(), savedInstance.getTrialDbId());
		Assert.assertEquals(dto.getLocationDbId(), savedInstance.getLocationDbId());
		Assert.assertTrue(CollectionUtils.isEmpty(savedInstance.getEnvironmentParameters()));
	}

	@Test
	public void testSaveStudyInstances_WithInvalidVariable() {
		final StudySummary trial = this.createTrial();
		final StudyImportRequestDTO dto = new StudyImportRequestDTO();
		dto.setTrialDbId(String.valueOf(trial.getTrialDbId()));
		dto.setLocationDbId("0");

		final EnvironmentParameter numericEnviromentParameter = new EnvironmentParameter();
		numericEnviromentParameter.setValue("1");
		numericEnviromentParameter.setParameterPUI(RandomStringUtils.randomNumeric(3));
		dto.setEnvironmentParameters(Collections.singletonList(numericEnviromentParameter));

		this.sessionProvder.getSession().flush();

		final StudyInstanceDto savedInstance = this.studyServiceBrapi
			.saveStudyInstances(this.cropType.getCropName(), Collections.singletonList(dto), this.testUser.getUserid()).get(0);
		Assert.assertEquals(dto.getTrialDbId(), savedInstance.getTrialDbId());
		Assert.assertEquals(dto.getLocationDbId(), savedInstance.getLocationDbId());
		Assert.assertTrue(CollectionUtils.isEmpty(savedInstance.getEnvironmentParameters()));
	}

	@Test
	public void testGetStudyDetailsByInstanceWithPI_ID() {
		final DmsProject environmentDataset =
			this.testDataInitializer
				.createDmsProject("Summary Dataset", "Summary Dataset-Description", this.study, this.study, DatasetTypeEnum.SUMMARY_DATA);
		final WorkbenchUser user = this.testDataInitializer.createUserForTesting();
		final int locationId = 101;

		final Geolocation geolocation = this.testDataInitializer.createTestGeolocation("1", locationId);
		this.testDataInitializer
			.addGeolocationProp(geolocation, TermId.COOPERATOOR_ID.getId(), String.valueOf(user.getPerson().getId()), 1);
		this.testDataInitializer
			.createTestExperiment(environmentDataset, geolocation, TermId.TRIAL_ENVIRONMENT_EXPERIMENT.getId(), "0", null);
		this.testDataInitializer
			.addProjectProp(this.study, TermId.PI_ID.getId(), "", VariableType.STUDY_DETAIL, String.valueOf(user.getPerson().getId()), 6);

		final Optional<StudyDetailsDto> studyDetailsDtoOptional = this.studyServiceBrapi.getStudyDetailsByInstance(geolocation.getLocationId());
		Assert.assertTrue(studyDetailsDtoOptional.isPresent());
		final StudyDetailsDto studyDetailsDto = studyDetailsDtoOptional.get();
		Assert.assertEquals(1, studyDetailsDto.getContacts().size());
		Assert.assertEquals(user.getUserid(), studyDetailsDto.getContacts().get(0).getUserId());
		Assert.assertEquals(locationId, studyDetailsDto.getMetadata().getLocationId().intValue());
		Assert.assertEquals(geolocation.getLocationId(), studyDetailsDto.getMetadata().getStudyDbId());
		Assert.assertEquals(this.study.getProjectId(), studyDetailsDto.getMetadata().getTrialDbId());
		Assert.assertEquals(this.study.getName() + " Environment Number 1", studyDetailsDto.getMetadata().getStudyName());
	}

	@Test
	public void testGetStudyDetailsByInstanceWithEnvConditionAndDetails_OK() {
		final int locationId = 101;

		final Geolocation geolocation = this.testDataInitializer.createTestGeolocation("1", locationId);

		final DmsProject dmsProject = this.daoFactory.getDmsProjectDAO()
			.getDatasetsByTypeForStudy(this.study.getProjectId(), DatasetTypeEnum.SUMMARY_DATA.getId()).get(0);

		final ExperimentModel testExperiment = this.testDataInitializer
			.createTestExperiment(dmsProject, geolocation, TermId.TRIAL_ENVIRONMENT_EXPERIMENT.getId(), "0", null);

		//Add 'Crop_season_Code' as environment details with 'Wet season' as value
		this.testDataInitializer
			.addProjectProp(dmsProject, TermId.SEASON_VAR.getId(), TermId.SEASON_VAR.name(), VariableType.ENVIRONMENT_DETAIL, null, 6);
		this.testDataInitializer.addGeolocationProp(geolocation, TermId.SEASON_VAR.getId(), String.valueOf(TermId.SEASON_WET.getId()), 1);

		//Add 'IrrigMethod_text' as environment details
		final CVTerm irrMethodText = this.daoFactory.getCvTermDao().getById(8700);
		assertNotNull(irrMethodText);

		this.testDataInitializer
			.addProjectProp(dmsProject, irrMethodText.getCvTermId(), irrMethodText.getName(), VariableType.ENVIRONMENT_DETAIL, null, 6);

		//Use termId as value in order to check corner case. It must get the termId as value because the variable is not categorical
		final String irrMethodTextValue = String.valueOf(TermId.SEASON_VAR.getId());
		this.testDataInitializer.addGeolocationProp(geolocation, irrMethodText.getCvTermId(), irrMethodTextValue, 1);

		//Add 'Selection_Trait' as environment condition with 'Drought tolerance' as value
		final CVTerm selectionTrait = this.daoFactory.getCvTermDao().getById(17290);
		assertNotNull(selectionTrait);

		final CVTerm droughtTolerance = this.daoFactory.getCvTermDao().getById(17285);
		assertNotNull(droughtTolerance);

		this.testDataInitializer
			.addProjectProp(dmsProject, selectionTrait.getCvTermId(), selectionTrait.getName(), VariableType.ENVIRONMENT_CONDITION, null,
				6);
		this.testDataInitializer
			.addPhenotypes(Arrays.asList(testExperiment), selectionTrait.getCvTermId(), droughtTolerance.getCvTermId().toString());

		//Add 'SITE_SOIL_PH' as environment details
		final CVTerm siteSoilPH = this.daoFactory.getCvTermDao().getById(8270);
		assertNotNull(siteSoilPH);

		this.testDataInitializer
			.addProjectProp(dmsProject, siteSoilPH.getCvTermId(), siteSoilPH.getName(), VariableType.ENVIRONMENT_CONDITION, null, 6);

		//Use termId as value in order to check corner case. It must get the termId as value because the variable is not categorical
		final String siteSoilPHValue = selectionTrait.getCvTermId().toString();
		this.testDataInitializer.addPhenotypes(Arrays.asList(testExperiment), siteSoilPH.getCvTermId(), siteSoilPHValue);

		this.sessionProvder.getSession().flush();
		this.sessionProvder.getSession().clear();

		final Optional<StudyDetailsDto> studyDetailsDtoOptional = this.studyServiceBrapi.getStudyDetailsByInstance(geolocation.getLocationId());
		Assert.assertTrue(studyDetailsDtoOptional.isPresent());
		final StudyDetailsDto studyDetailsDto = studyDetailsDtoOptional.get();

		Assert.assertEquals(locationId, studyDetailsDto.getMetadata().getLocationId().intValue());
		Assert.assertEquals(geolocation.getLocationId(), studyDetailsDto.getMetadata().getStudyDbId());
		Assert.assertEquals(this.study.getProjectId(), studyDetailsDto.getMetadata().getTrialDbId());
		Assert.assertEquals(this.study.getName() + " Environment Number 1", studyDetailsDto.getMetadata().getStudyName());

		final List<MeasurementVariable> environmentParameters = studyDetailsDto.getEnvironmentParameters();
		assertFalse(CollectionUtils.isEmpty(environmentParameters));
		assertThat(environmentParameters, hasSize(4));
		this.assertEnvironmentParameter(environmentParameters, TermId.SEASON_VAR.getId(), "Crop_season_Code", "1");
		this.assertEnvironmentParameter(environmentParameters, irrMethodText.getCvTermId(), irrMethodText.getName(), irrMethodTextValue);
		this.assertEnvironmentParameter(environmentParameters, selectionTrait.getCvTermId(), selectionTrait.getName(),
			droughtTolerance.getName());
		this.assertEnvironmentParameter(environmentParameters, siteSoilPH.getCvTermId(), siteSoilPH.getName(), siteSoilPHValue);
	}

	@Test
	public void testGetStudyDetailsByInstance() {
		final DmsProject environmentDataset =
			this.testDataInitializer
				.createDmsProject("Summary Dataset", "Summary Dataset-Description", this.study, this.study, DatasetTypeEnum.SUMMARY_DATA);

		final int locationId = 101;
		final Geolocation geolocation = this.testDataInitializer.createTestGeolocation("1", locationId);
		this.testDataInitializer
			.createTestExperiment(environmentDataset, geolocation, TermId.TRIAL_ENVIRONMENT_EXPERIMENT.getId(), "0", null);
		this.sessionProvder.getSession().flush();
		final Optional<StudyDetailsDto> studyDetailsDtoOptional = this.studyServiceBrapi.getStudyDetailsByInstance(geolocation.getLocationId());
		Assert.assertTrue(studyDetailsDtoOptional.isPresent());
		final StudyDetailsDto studyDetailsDto = studyDetailsDtoOptional.get();
		Assert.assertTrue(CollectionUtils.isEmpty(studyDetailsDto.getContacts()));
		Assert.assertEquals(locationId, studyDetailsDto.getMetadata().getLocationId().intValue());
		Assert.assertEquals(geolocation.getLocationId(), studyDetailsDto.getMetadata().getStudyDbId());
		Assert.assertEquals(this.study.getProjectId(), studyDetailsDto.getMetadata().getTrialDbId());
		Assert.assertEquals(this.study.getName() + " Environment Number 1", studyDetailsDto.getMetadata().getStudyName());

		environmentDataset.setDeleted(true);

		this.daoFactory.getDmsProjectDAO().save(environmentDataset);

		this.sessionProvder.getSession().flush();
		this.sessionProvder.getSession().clear();

		assertFalse(this.studyServiceBrapi.getStudyDetailsByInstance(geolocation.getLocationId()).isPresent());
	}

	private void assertEnvironmentParameter(final List<MeasurementVariable> environmentParameters, final int expectedTermId,
		final String expectedName,
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

	private StudySummary createTrial() {
		final TrialImportRequestDTO dto = new TrialImportRequestDTO();
		dto.setStartDate("2019-01-01");
		dto.setTrialDescription(RandomStringUtils.randomAlphabetic(20));
		dto.setTrialName(RandomStringUtils.randomAlphabetic(20));
		dto.setProgramDbId(this.commonTestProject.getUniqueID());

		return this.trialServiceBrapi.saveStudies(this.cropType.getCropName(), Collections.singletonList(dto), this.testUser.getUserid()).get(0);
	}

}
