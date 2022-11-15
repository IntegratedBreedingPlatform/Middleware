package org.generationcp.middleware.service.impl.observationunit;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.api.brapi.StudyServiceBrapi;
import org.generationcp.middleware.api.brapi.TrialServiceBrapi;
import org.generationcp.middleware.api.brapi.v2.germplasm.ExternalReferenceDTO;
import org.generationcp.middleware.api.brapi.v2.observationunit.ObservationLevelRelationship;
import org.generationcp.middleware.api.brapi.v2.observationunit.ObservationUnitImportRequestDto;
import org.generationcp.middleware.api.brapi.v2.observationunit.ObservationUnitPosition;
import org.generationcp.middleware.api.brapi.v2.observationunit.ObservationUnitService;
import org.generationcp.middleware.api.brapi.v2.study.StudyImportRequestDTO;
import org.generationcp.middleware.api.brapi.v2.trial.TrialImportRequestDTO;
import org.generationcp.middleware.api.germplasm.GermplasmGuidGenerator;
import org.generationcp.middleware.api.program.ProgramService;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.domain.dms.StudySummary;
import org.generationcp.middleware.domain.gms.SystemDefinedEntryType;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.WorkbenchDaoFactory;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.dms.DatasetType;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.phenotype.ObservationUnitDto;
import org.generationcp.middleware.service.api.phenotype.ObservationUnitSearchRequestDTO;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObservationUnitServiceImplIntegrationTest extends IntegrationTestBase {

	public static final String ENTRY_NO = "ENTRY_NO";

	@Resource
	private TrialServiceBrapi trialServiceBrapi;

	@Resource
	private StudyServiceBrapi studyServiceBrapi;

	@Resource
	private ObservationUnitService observationUnitService;

	@Autowired
	private ProgramService programService;

	@Autowired
	private WorkbenchTestDataUtil workbenchTestDataUtil;

	private IntegrationTestDataInitializer testDataInitializer;
	private CropType crop;
	private Project commonTestProject;
	private WorkbenchUser testUser;
	private DaoFactory daoFactory;
	private WorkbenchDaoFactory workbenchDaoFactory;
	private StudySummary studySummary;
	private StudyInstanceDto studyInstanceDto;
	private Germplasm germplasm;

	@Before
	public void setUp() {
		this.daoFactory = new DaoFactory(this.sessionProvder);
		this.workbenchDaoFactory = new WorkbenchDaoFactory(this.workbenchSessionProvider);

		this.workbenchTestDataUtil.setUpWorkbench(workbenchDaoFactory);
		if (this.commonTestProject == null) {
			this.commonTestProject = this.workbenchTestDataUtil.getCommonTestProject();
			this.crop = this.programService.getProjectByUuid(this.commonTestProject.getUniqueID()).getCropType();
		}
		this.testDataInitializer = new IntegrationTestDataInitializer(this.sessionProvder, this.workbenchSessionProvider);
		this.testUser = this.testDataInitializer.createUserForTesting();

		final TrialImportRequestDTO importRequest1 = new TrialImportRequestDTO();
		importRequest1.setStartDate("2019-01-01");
		importRequest1.setEndDate("2020-12-31");
		importRequest1.setTrialDescription(RandomStringUtils.randomAlphabetic(20));
		importRequest1.setTrialName(RandomStringUtils.randomAlphabetic(20));
		importRequest1.setProgramDbId(this.commonTestProject.getUniqueID());

		this.studySummary = this.trialServiceBrapi
			.saveStudies(this.crop.getCropName(), Collections.singletonList(importRequest1), this.testUser.getUserid()).get(0);

		// Create means dataset
		this.createDataset(this.daoFactory.getDmsProjectDAO().getById(this.studySummary.getTrialDbId()), DatasetTypeEnum.MEANS_DATA,
			"-MEANS");

		final StudyImportRequestDTO dto = new StudyImportRequestDTO();
		dto.setTrialDbId(String.valueOf(this.studySummary.getTrialDbId()));
		this.studyInstanceDto = this.studyServiceBrapi
			.saveStudyInstances(this.crop.getCropName(), Collections.singletonList(dto), this.testUser.getUserid()).get(0);

		this.germplasm = GermplasmTestDataInitializer.createGermplasm(1);
		this.germplasm.setGid(null);
		GermplasmGuidGenerator.generateGermplasmGuids(this.crop, Collections.singletonList(this.germplasm));
		this.daoFactory.getGermplasmDao().save(this.germplasm);

		this.sessionProvder.getSession().flush();
	}

	@Test
	public void testImportObservationUnits_AllInfoSaved() {
		final ObservationUnitImportRequestDto dto = this.createObservationUnitImportRequestDto();

		final List<String> observationDbIds =
			this.observationUnitService.importObservationUnits(this.crop.getCropName(), Collections.singletonList(dto));

		final ObservationUnitSearchRequestDTO searchRequestDTO = new ObservationUnitSearchRequestDTO();
		searchRequestDTO.setObservationUnitDbIds(observationDbIds);
		final List<ObservationUnitDto> observationUnitDtoList =
			this.observationUnitService.searchObservationUnits(null, null, searchRequestDTO);

		Assert.assertEquals(1, observationUnitDtoList.size());
		final ObservationUnitDto observationUnitDto = observationUnitDtoList.get(0);
		Assert.assertEquals(dto.getProgramDbId(), observationUnitDto.getProgramDbId());
		Assert.assertEquals(dto.getTrialDbId(), observationUnitDto.getTrialDbId());
		Assert.assertEquals(dto.getStudyDbId(), observationUnitDto.getStudyDbId());
		Assert.assertEquals(dto.getGermplasmDbId(), observationUnitDto.getGermplasmDbId());
		Assert.assertTrue(
			dto.getObservationUnitPosition().getEntryType()
				.equalsIgnoreCase(observationUnitDto.getObservationUnitPosition().getEntryType()));
		Assert.assertEquals(dto.getObservationUnitPosition().getObservationLevel().getLevelName(),
			observationUnitDto.getObservationUnitPosition().getObservationLevel().getLevelName());
		Assert.assertEquals(dto.getObservationUnitPosition().getPositionCoordinateX(),
			observationUnitDto.getObservationUnitPosition().getPositionCoordinateX());
		Assert.assertEquals(dto.getObservationUnitPosition().getPositionCoordinateY(),
			observationUnitDto.getObservationUnitPosition().getPositionCoordinateY());
		Assert.assertEquals(3, observationUnitDto.getObservationUnitPosition().getObservationLevelRelationships().size());
		Assert.assertNotNull(observationUnitDto.getObservationUnitPosition().getGeoCoordinates());
		Assert.assertEquals(1, observationUnitDto.getExternalReferences().size());
		Assert.assertEquals(dto.getExternalReferences().get(0).getReferenceID(),
			observationUnitDto.getExternalReferences().get(0).getReferenceID());
		Assert.assertEquals(dto.getExternalReferences().get(0).getReferenceSource(),
			observationUnitDto.getExternalReferences().get(0).getReferenceSource());
		Assert.assertEquals("1", observationUnitDto.getAdditionalInfo().get(ENTRY_NO));
	}

	@Test
	public void testImportObservationUnits_SameGermplasm_WithoutEntryNo() {
		final ObservationUnitImportRequestDto dto1 = this.createObservationUnitImportRequestDto();
		final ObservationUnitImportRequestDto dto2 = this.createObservationUnitImportRequestDto();
		final ObservationUnitImportRequestDto dto3 = this.createObservationUnitImportRequestDto();

		final List<String> observationDbIds =
			this.observationUnitService.importObservationUnits(this.crop.getCropName(), Arrays.asList(dto1, dto2, dto3));

		final ObservationUnitSearchRequestDTO searchRequestDTO = new ObservationUnitSearchRequestDTO();
		searchRequestDTO.setObservationUnitDbIds(observationDbIds);
		final List<ObservationUnitDto> observationUnitDtoList =
			this.observationUnitService.searchObservationUnits(null, null, searchRequestDTO);

		Assert.assertEquals(3, observationUnitDtoList.size());
		final ObservationUnitDto observationUnitDto = observationUnitDtoList.get(0);
		Assert.assertEquals(dto1.getGermplasmDbId(), observationUnitDto.getGermplasmDbId());
		Assert.assertEquals("1", observationUnitDto.getAdditionalInfo().get(ENTRY_NO));
		final ObservationUnitDto observationUnitDto2 = observationUnitDtoList.get(1);
		Assert.assertEquals(dto2.getGermplasmDbId(), observationUnitDto2.getGermplasmDbId());
		Assert.assertEquals("1", observationUnitDto2.getAdditionalInfo().get(ENTRY_NO));
		final ObservationUnitDto observationUnitDto3 = observationUnitDtoList.get(2);
		Assert.assertEquals(dto3.getGermplasmDbId(), observationUnitDto3.getGermplasmDbId());
		Assert.assertEquals("1", observationUnitDto3.getAdditionalInfo().get(ENTRY_NO));
	}

	@Test
	public void testImportObservationUnits_SameGermplasm_EntryNoIsSpecifiedInAdditionalInfo() {
		final ObservationUnitImportRequestDto dto1 = this.createObservationUnitImportRequestDto();
		final Map<String, String> additionalInfo = new HashMap<>();
		additionalInfo.put(ENTRY_NO, "99");
		dto1.setAdditionalInfo(additionalInfo);
		final ObservationUnitImportRequestDto dto2 = this.createObservationUnitImportRequestDto();
		final Map<String, String> additionalInfo2 = new HashMap<>();
		additionalInfo2.put(ENTRY_NO, "100");
		dto2.setAdditionalInfo(additionalInfo2);
		final ObservationUnitImportRequestDto dto3 = this.createObservationUnitImportRequestDto();
		final Map<String, String> additionalInfo3 = new HashMap<>();
		additionalInfo3.put(ENTRY_NO, "101");
		dto3.setAdditionalInfo(additionalInfo3);

		final List<String> observationDbIds =
			this.observationUnitService.importObservationUnits(this.crop.getCropName(), Arrays.asList(dto1, dto2, dto3));

		final ObservationUnitSearchRequestDTO searchRequestDTO = new ObservationUnitSearchRequestDTO();
		searchRequestDTO.setObservationUnitDbIds(observationDbIds);
		final List<ObservationUnitDto> observationUnitDtoList =
			this.observationUnitService.searchObservationUnits(null, null, searchRequestDTO);

		Assert.assertEquals(3, observationUnitDtoList.size());
		final ObservationUnitDto observationUnitDto = observationUnitDtoList.get(0);
		Assert.assertEquals(dto1.getGermplasmDbId(), observationUnitDto.getGermplasmDbId());
		Assert.assertEquals(dto1.getAdditionalInfo().get(ENTRY_NO), observationUnitDto.getAdditionalInfo().get(ENTRY_NO));
		final ObservationUnitDto observationUnitDto2 = observationUnitDtoList.get(1);
		Assert.assertEquals(dto2.getGermplasmDbId(), observationUnitDto2.getGermplasmDbId());
		Assert.assertEquals(dto2.getAdditionalInfo().get(ENTRY_NO), observationUnitDto2.getAdditionalInfo().get(ENTRY_NO));
		final ObservationUnitDto observationUnitDto3 = observationUnitDtoList.get(2);
		Assert.assertEquals(dto3.getGermplasmDbId(), observationUnitDto3.getGermplasmDbId());
		Assert.assertEquals(dto3.getAdditionalInfo().get(ENTRY_NO), observationUnitDto3.getAdditionalInfo().get(ENTRY_NO));
	}

	@Test
	public void testImportObservationUnits_WithValidVariableHavingInvalidVariableType() {
		final ObservationUnitImportRequestDto dto = new ObservationUnitImportRequestDto();
		dto.setTrialDbId(this.studySummary.getTrialDbId().toString());
		dto.setStudyDbId(this.studyInstanceDto.getStudyDbId());
		dto.setProgramDbId(this.commonTestProject.getUniqueID());
		dto.setGermplasmDbId(this.germplasm.getGermplasmUUID());

		final ObservationUnitPosition observationUnitPosition = new ObservationUnitPosition();
		observationUnitPosition.setEntryType(SystemDefinedEntryType.TEST_ENTRY.getEntryTypeName());
		final ObservationLevelRelationship plotLevel = new ObservationLevelRelationship();
		plotLevel.setLevelName(DatasetTypeEnum.PLOT_DATA.getName());
		observationUnitPosition.setObservationLevel(plotLevel);

		final CVTerm numericVariable =
			this.testDataInitializer.createVariableWithScale(DataType.NUMERIC_VARIABLE, VariableType.STUDY_DETAIL);
		final ObservationLevelRelationship relationship = new ObservationLevelRelationship();
		relationship.setLevelCode("1");
		relationship.setLevelName(numericVariable.getName());
		observationUnitPosition.setObservationLevelRelationships(Collections.singletonList(relationship));

		dto.setObservationUnitPosition(observationUnitPosition);

		final List<String> observationDbIds =
			this.observationUnitService.importObservationUnits(this.crop.getCropName(), Collections.singletonList(dto));

		this.sessionProvder.getSession().flush();

		final ObservationUnitSearchRequestDTO searchRequestDTO = new ObservationUnitSearchRequestDTO();
		searchRequestDTO.setObservationUnitDbIds(observationDbIds);
		final List<ObservationUnitDto> observationUnitDtoList =
			this.observationUnitService.searchObservationUnits(null, null, searchRequestDTO);

		Assert.assertEquals(1, observationUnitDtoList.size());
		final ObservationUnitDto observationUnitDto = observationUnitDtoList.get(0);
		Assert.assertEquals(dto.getProgramDbId(), observationUnitDto.getProgramDbId());
		Assert.assertEquals(dto.getTrialDbId(), observationUnitDto.getTrialDbId());
		Assert.assertEquals(dto.getStudyDbId(), observationUnitDto.getStudyDbId());
		Assert.assertEquals(dto.getGermplasmDbId(), observationUnitDto.getGermplasmDbId());
		Assert.assertTrue(
			observationUnitPosition.getEntryType().equalsIgnoreCase(observationUnitDto.getObservationUnitPosition().getEntryType()));
		Assert.assertTrue(CollectionUtils.isEmpty(observationUnitDto.getObservationUnitPosition().getObservationLevelRelationships()));
	}

	@Test
	public void testImportObservationUnits_WithValidVariableHavingInvalidVariableValue() {
		final ObservationUnitImportRequestDto dto = new ObservationUnitImportRequestDto();
		dto.setTrialDbId(this.studySummary.getTrialDbId().toString());
		dto.setStudyDbId(this.studyInstanceDto.getStudyDbId());
		dto.setProgramDbId(this.commonTestProject.getUniqueID());
		dto.setGermplasmDbId(this.germplasm.getGermplasmUUID());

		final ObservationUnitPosition observationUnitPosition = new ObservationUnitPosition();
		observationUnitPosition.setEntryType(SystemDefinedEntryType.TEST_ENTRY.getEntryTypeName());
		final ObservationLevelRelationship plotLevel = new ObservationLevelRelationship();
		plotLevel.setLevelName(DatasetTypeEnum.PLOT_DATA.getName());
		observationUnitPosition.setObservationLevel(plotLevel);

		final CVTerm numericVariable =
			this.testDataInitializer.createVariableWithScale(DataType.NUMERIC_VARIABLE, VariableType.STUDY_DETAIL);
		final ObservationLevelRelationship relationship = new ObservationLevelRelationship();
		relationship.setLevelCode("NON NUMERIC VALUE");
		relationship.setLevelName(numericVariable.getName());
		observationUnitPosition.setObservationLevelRelationships(Collections.singletonList(relationship));

		dto.setObservationUnitPosition(observationUnitPosition);

		final List<String> observationDbIds =
			this.observationUnitService.importObservationUnits(this.crop.getCropName(), Collections.singletonList(dto));

		this.sessionProvder.getSession().flush();

		final ObservationUnitSearchRequestDTO searchRequestDTO = new ObservationUnitSearchRequestDTO();
		searchRequestDTO.setObservationUnitDbIds(observationDbIds);
		final List<ObservationUnitDto> observationUnitDtoList =
			this.observationUnitService.searchObservationUnits(null, null, searchRequestDTO);

		Assert.assertEquals(1, observationUnitDtoList.size());
		final ObservationUnitDto observationUnitDto = observationUnitDtoList.get(0);
		Assert.assertEquals(dto.getProgramDbId(), observationUnitDto.getProgramDbId());
		Assert.assertEquals(dto.getTrialDbId(), observationUnitDto.getTrialDbId());
		Assert.assertEquals(dto.getStudyDbId(), observationUnitDto.getStudyDbId());
		Assert.assertEquals(dto.getGermplasmDbId(), observationUnitDto.getGermplasmDbId());
		Assert.assertTrue(
			observationUnitPosition.getEntryType().equalsIgnoreCase(observationUnitDto.getObservationUnitPosition().getEntryType()));
		Assert.assertTrue(CollectionUtils.isEmpty(observationUnitDto.getObservationUnitPosition().getObservationLevelRelationships()));
	}

	@Test
	public void testImportObservationUnits_WithValidVariableHavingInvalidVariable() {
		final ObservationUnitImportRequestDto dto = new ObservationUnitImportRequestDto();
		dto.setTrialDbId(this.studySummary.getTrialDbId().toString());
		dto.setStudyDbId(this.studyInstanceDto.getStudyDbId());
		dto.setProgramDbId(this.commonTestProject.getUniqueID());
		dto.setGermplasmDbId(this.germplasm.getGermplasmUUID());

		final ObservationUnitPosition observationUnitPosition = new ObservationUnitPosition();
		observationUnitPosition.setEntryType(SystemDefinedEntryType.TEST_ENTRY.getEntryTypeName());
		final ObservationLevelRelationship plotLevel = new ObservationLevelRelationship();
		plotLevel.setLevelName(DatasetTypeEnum.PLOT_DATA.getName());
		observationUnitPosition.setObservationLevel(plotLevel);

		final ObservationLevelRelationship relationship = new ObservationLevelRelationship();
		relationship.setLevelCode("NON NUMERIC VALUE");
		relationship.setLevelName(RandomStringUtils.randomAlphabetic(20));
		observationUnitPosition.setObservationLevelRelationships(Collections.singletonList(relationship));

		dto.setObservationUnitPosition(observationUnitPosition);

		final List<String> observationDbIds =
			this.observationUnitService.importObservationUnits(this.crop.getCropName(), Collections.singletonList(dto));

		this.sessionProvder.getSession().flush();

		final ObservationUnitSearchRequestDTO searchRequestDTO = new ObservationUnitSearchRequestDTO();
		searchRequestDTO.setObservationUnitDbIds(observationDbIds);
		final List<ObservationUnitDto> observationUnitDtoList =
			this.observationUnitService.searchObservationUnits(null, null, searchRequestDTO);

		Assert.assertEquals(1, observationUnitDtoList.size());
		final ObservationUnitDto observationUnitDto = observationUnitDtoList.get(0);
		Assert.assertEquals(dto.getProgramDbId(), observationUnitDto.getProgramDbId());
		Assert.assertEquals(dto.getTrialDbId(), observationUnitDto.getTrialDbId());
		Assert.assertEquals(dto.getStudyDbId(), observationUnitDto.getStudyDbId());
		Assert.assertEquals(dto.getGermplasmDbId(), observationUnitDto.getGermplasmDbId());
		Assert.assertTrue(
			observationUnitPosition.getEntryType().equalsIgnoreCase(observationUnitDto.getObservationUnitPosition().getEntryType()));
		Assert.assertTrue(CollectionUtils.isEmpty(observationUnitDto.getObservationUnitPosition().getObservationLevelRelationships()));
	}

	@Test
	public void testImportObservationUnits_MeansLevel() {
		final ObservationUnitImportRequestDto dto = this.createObservationUnitImportRequestDtoForMeansLevel();

		final List<String> observationDbIds =
			this.observationUnitService.importObservationUnits(this.crop.getCropName(), Collections.singletonList(dto));

		final ObservationUnitSearchRequestDTO searchRequestDTO = new ObservationUnitSearchRequestDTO();
		searchRequestDTO.setObservationUnitDbIds(observationDbIds);
		final List<ObservationUnitDto> observationUnitDtoList =
			this.observationUnitService.searchObservationUnits(null, null, searchRequestDTO);

		Assert.assertEquals(1, observationUnitDtoList.size());
		final ObservationUnitDto observationUnitDto = observationUnitDtoList.get(0);
		Assert.assertEquals(dto.getProgramDbId(), observationUnitDto.getProgramDbId());
		Assert.assertEquals(dto.getTrialDbId(), observationUnitDto.getTrialDbId());
		Assert.assertEquals(dto.getStudyDbId(), observationUnitDto.getStudyDbId());
		Assert.assertEquals(dto.getGermplasmDbId(), observationUnitDto.getGermplasmDbId());
		Assert.assertTrue(
			dto.getObservationUnitPosition().getEntryType()
				.equalsIgnoreCase(observationUnitDto.getObservationUnitPosition().getEntryType()));
		Assert.assertEquals(dto.getObservationUnitPosition().getObservationLevel().getLevelName(),
			observationUnitDto.getObservationUnitPosition().getObservationLevel().getLevelName());
		Assert.assertEquals(1, observationUnitDto.getExternalReferences().size());
		Assert.assertEquals(dto.getExternalReferences().get(0).getReferenceID(),
			observationUnitDto.getExternalReferences().get(0).getReferenceID());
		Assert.assertEquals(dto.getExternalReferences().get(0).getReferenceSource(),
			observationUnitDto.getExternalReferences().get(0).getReferenceSource());
		Assert.assertEquals("1", observationUnitDto.getAdditionalInfo().get(ENTRY_NO));
	}

	private ObservationUnitImportRequestDto createObservationUnitImportRequestDto() {
		final ObservationUnitImportRequestDto dto = new ObservationUnitImportRequestDto();
		dto.setTrialDbId(this.studySummary.getTrialDbId().toString());
		dto.setStudyDbId(this.studyInstanceDto.getStudyDbId());
		dto.setProgramDbId(this.commonTestProject.getUniqueID());
		dto.setGermplasmDbId(this.germplasm.getGermplasmUUID());
		final ExternalReferenceDTO externalReference = new ExternalReferenceDTO();
		externalReference.setReferenceID(RandomStringUtils.randomAlphabetic(20));
		externalReference.setReferenceSource(RandomStringUtils.randomAlphabetic(20));
		dto.setExternalReferences(Collections.singletonList(externalReference));

		final ObservationUnitPosition observationUnitPosition = new ObservationUnitPosition();
		observationUnitPosition.setEntryType(SystemDefinedEntryType.TEST_ENTRY.getEntryTypeName());
		observationUnitPosition.setPositionCoordinateX("1");
		observationUnitPosition.setPositionCoordinateY("2");
		observationUnitPosition.setEntryType(SystemDefinedEntryType.TEST_ENTRY.getEntryTypeName());
		final ObservationLevelRelationship plotLevel = new ObservationLevelRelationship();
		plotLevel.setLevelName(DatasetTypeEnum.PLOT_DATA.getName());
		observationUnitPosition.setObservationLevel(plotLevel);

		final ObservationLevelRelationship plotRelationship = new ObservationLevelRelationship();
		plotRelationship.setLevelCode("1");
		plotRelationship.setLevelName("PLOT");
		final ObservationLevelRelationship repRelationship = new ObservationLevelRelationship();
		repRelationship.setLevelCode("1");
		repRelationship.setLevelName("REP");
		final ObservationLevelRelationship blockRelationship = new ObservationLevelRelationship();
		blockRelationship.setLevelCode("1");
		blockRelationship.setLevelName("BLOCK");
		observationUnitPosition.setObservationLevelRelationships(Arrays.asList(plotRelationship, repRelationship, blockRelationship));

		final Map<String, Object> geoCoodinates = new HashMap<>();
		geoCoodinates.put("type", "Feature");
		final Map<String, Object> geometry = new HashMap<>();
		geoCoodinates.put("type", "Point");
		final List<Double> coordinates = Arrays.asList(new Double(-76.506042), new Double(42.417373), new Double(123));
		geometry.put("coordinates", coordinates);
		geoCoodinates.put("geometry", geometry);
		observationUnitPosition.setGeoCoordinates(geoCoodinates);
		dto.setObservationUnitPosition(observationUnitPosition);

		return dto;
	}

	private ObservationUnitImportRequestDto createObservationUnitImportRequestDtoForMeansLevel() {
		final ObservationUnitImportRequestDto dto = new ObservationUnitImportRequestDto();
		dto.setTrialDbId(this.studySummary.getTrialDbId().toString());
		dto.setStudyDbId(this.studyInstanceDto.getStudyDbId());
		dto.setProgramDbId(this.commonTestProject.getUniqueID());
		dto.setGermplasmDbId(this.germplasm.getGermplasmUUID());
		final ExternalReferenceDTO externalReference = new ExternalReferenceDTO();
		externalReference.setReferenceID(RandomStringUtils.randomAlphabetic(20));
		externalReference.setReferenceSource(RandomStringUtils.randomAlphabetic(20));
		dto.setExternalReferences(Collections.singletonList(externalReference));

		final ObservationUnitPosition observationUnitPosition = new ObservationUnitPosition();
		observationUnitPosition.setEntryType(SystemDefinedEntryType.TEST_ENTRY.getEntryTypeName());
		final ObservationLevelRelationship meansLevel = new ObservationLevelRelationship();
		meansLevel.setLevelName(DatasetTypeEnum.MEANS_DATA.getName());
		observationUnitPosition.setObservationLevel(meansLevel);
		dto.setObservationUnitPosition(observationUnitPosition);

		return dto;
	}

	private DmsProject createDataset(final DmsProject study, final DatasetTypeEnum datasetType, final String nameSuffix) {
		final DmsProject dmsProject = new DmsProject();
		dmsProject.setDatasetType(new DatasetType(datasetType.getId()));
		dmsProject.setName(study.getName() + nameSuffix);
		dmsProject.setDescription(study.getDescription() + nameSuffix);
		dmsProject.setParent(study);
		dmsProject.setStudy(study);
		dmsProject.setDeleted(false);
		dmsProject.setProgramUUID(study.getProgramUUID());
		this.daoFactory.getDmsProjectDAO().save(dmsProject);
		return dmsProject;
	}

}
