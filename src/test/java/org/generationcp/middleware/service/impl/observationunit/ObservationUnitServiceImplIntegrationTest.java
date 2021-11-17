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
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.domain.dms.StudySummary;
import org.generationcp.middleware.domain.gms.SystemDefinedEntryType;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.phenotype.ObservationUnitDto;
import org.generationcp.middleware.service.api.phenotype.ObservationUnitSearchRequestDTO;
import org.generationcp.middleware.service.api.study.StudyInstanceDto;
import org.generationcp.middleware.service.api.study.StudyInstanceService;
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

	@Resource
	private TrialServiceBrapi trialServiceBrapi;

	@Resource
	private StudyServiceBrapi studyServiceBrapi;

	@Resource
	private ObservationUnitService observationUnitService;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private WorkbenchTestDataUtil workbenchTestDataUtil;

	private IntegrationTestDataInitializer testDataInitializer;
	private CropType crop;
	private Project commonTestProject;
	private WorkbenchUser testUser;
	private DaoFactory daoFactory;
	private StudySummary studySummary;
	private StudyInstanceDto studyInstanceDto;
	private Germplasm germplasm;

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

		final TrialImportRequestDTO importRequest1 = new TrialImportRequestDTO();
		importRequest1.setStartDate("2019-01-01");
		importRequest1.setEndDate("2020-12-31");
		importRequest1.setTrialDescription(RandomStringUtils.randomAlphabetic(20));
		importRequest1.setTrialName(RandomStringUtils.randomAlphabetic(20));
		importRequest1.setProgramDbId(this.commonTestProject.getUniqueID());

		this.studySummary = this.trialServiceBrapi
			.saveStudies(this.crop.getCropName(), Collections.singletonList(importRequest1), this.testUser.getUserid()).get(0);

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

		final Map<String, String> additionalInfo = new HashMap<>();
		additionalInfo.put("ENTRY_NO", "99");
		dto.setAdditionalInfo(additionalInfo);

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
			observationUnitPosition.getEntryType().equalsIgnoreCase(observationUnitDto.getObservationUnitPosition().getEntryType()));
		Assert.assertEquals(observationUnitPosition.getPositionCoordinateX(),
			observationUnitDto.getObservationUnitPosition().getPositionCoordinateX());
		Assert.assertEquals(observationUnitPosition.getPositionCoordinateY(),
			observationUnitDto.getObservationUnitPosition().getPositionCoordinateY());
		Assert.assertEquals(3, observationUnitDto.getObservationUnitPosition().getObservationLevelRelationships().size());
		Assert.assertNotNull(observationUnitDto.getObservationUnitPosition().getGeoCoordinates());
		Assert.assertEquals(1, observationUnitDto.getExternalReferences().size());
		Assert.assertEquals(externalReference.getReferenceID(), observationUnitDto.getExternalReferences().get(0).getReferenceID());
		Assert.assertEquals(externalReference.getReferenceSource(), observationUnitDto.getExternalReferences().get(0).getReferenceSource());
		Assert.assertEquals(dto.getAdditionalInfo().get("ENTRY_NO"), observationUnitDto.getAdditionalInfo().get("ENTRY_NO"));
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

}
