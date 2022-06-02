package org.generationcp.middleware.brapi;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.api.brapi.ObservationServiceBrapi;
import org.generationcp.middleware.api.brapi.StudyServiceBrapi;
import org.generationcp.middleware.api.brapi.TrialServiceBrapi;
import org.generationcp.middleware.api.brapi.VariableServiceBrapi;
import org.generationcp.middleware.api.brapi.VariableTypeGroup;
import org.generationcp.middleware.api.brapi.v2.germplasm.ExternalReferenceDTO;
import org.generationcp.middleware.api.brapi.v2.observation.ObservationDto;
import org.generationcp.middleware.api.brapi.v2.observation.ObservationSearchRequestDto;
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
import org.generationcp.middleware.domain.search_request.brapi.v2.VariableSearchRequestDTO;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.study.StudyInstanceDto;
import org.generationcp.middleware.service.api.study.VariableDTO;
import org.generationcp.middleware.utils.test.IntegrationTestDataInitializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ObservationServiceBrapiImplTest extends IntegrationTestBase {

	public static final String REF_ID = "refId";
	public static final String REF_SOURCE = "refSource";
	public static final String VALUE = "1";
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

	@Autowired
	private ObservationServiceBrapi observationServiceBrapi;

	@Autowired
	private VariableServiceBrapi variableServiceBrapi;

	@Autowired
	private ProgramService programService;

	private IntegrationTestDataInitializer testDataInitializer;
	private CropType crop;
	private Project commonTestProject;
	private WorkbenchUser testUser;
	private DaoFactory daoFactory;
	private StudySummary studySummary;
	private StudyInstanceDto studyInstanceDto;
	private Germplasm germplasm;
	private String observationUnitDbId;
	private VariableDTO variableDTO;

	@Before
	public void setUp() {
		this.daoFactory = new DaoFactory(this.sessionProvder);

		this.workbenchTestDataUtil.setUpWorkbench();
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

		final StudyImportRequestDTO dto = new StudyImportRequestDTO();
		dto.setTrialDbId(String.valueOf(this.studySummary.getTrialDbId()));
		this.studyInstanceDto = this.studyServiceBrapi
			.saveStudyInstances(this.crop.getCropName(), Collections.singletonList(dto), this.testUser.getUserid()).get(0);

		this.germplasm = GermplasmTestDataInitializer.createGermplasm(1);
		this.germplasm.setGid(null);
		GermplasmGuidGenerator.generateGermplasmGuids(this.crop, Collections.singletonList(this.germplasm));
		this.daoFactory.getGermplasmDao().save(this.germplasm);

		final Name germplasmName = GermplasmTestDataInitializer.createGermplasmName(this.germplasm.getGid());
		germplasmName.setTypeId(2);
		this.daoFactory.getNameDao().save(germplasmName);

		final ObservationUnitImportRequestDto observationUnitImportRequestDto = new ObservationUnitImportRequestDto();
		observationUnitImportRequestDto.setTrialDbId(this.studySummary.getTrialDbId().toString());
		observationUnitImportRequestDto.setStudyDbId(this.studyInstanceDto.getStudyDbId());
		observationUnitImportRequestDto.setProgramDbId(this.commonTestProject.getUniqueID());
		observationUnitImportRequestDto.setGermplasmDbId(this.germplasm.getGermplasmUUID());

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
		observationUnitImportRequestDto.setObservationUnitPosition(observationUnitPosition);

		this.observationUnitDbId = this.observationUnitService
			.importObservationUnits(this.crop.getCropName(), Collections.singletonList(observationUnitImportRequestDto)).get(0);

		final CVTerm numericVariable =
			this.testDataInitializer.createVariableWithScale(DataType.NUMERIC_VARIABLE, VariableType.TRAIT);
		final VariableSearchRequestDTO variableSearchRequestDTO = new VariableSearchRequestDTO();
		variableSearchRequestDTO.setObservationVariableDbIds(Collections.singletonList(numericVariable.getCvTermId().toString()));
		this.variableDTO = this.variableServiceBrapi
			.getVariables(variableSearchRequestDTO, null, VariableTypeGroup.TRAIT).get(0);
		this.variableDTO.setStudyDbIds(Collections.singletonList(this.studyInstanceDto.getStudyDbId()));
		this.variableServiceBrapi.updateObservationVariable(this.variableDTO);
		this.sessionProvder.getSession().flush();
	}

	@Test
	public void testCountObservations() {
		final List<ObservationDto> observationDtos = this.createObservationDtos();
		final ObservationSearchRequestDto observationSearchRequestDto = new ObservationSearchRequestDto();
		observationSearchRequestDto.setObservationDbIds(
			observationDtos.stream().map(o -> Integer.valueOf(o.getObservationDbId())).collect(Collectors.toList()));
		final long observationsCount = this.observationServiceBrapi.countObservations(observationSearchRequestDto);
		Assert.assertEquals((long) 1, observationsCount);
	}

	@Test
	public void testSearchObservations() {
		final List<ObservationDto> observationDtos = this.createObservationDtos();

		final ObservationSearchRequestDto observationSearchRequestDto = new ObservationSearchRequestDto();
		observationSearchRequestDto.setObservationDbIds(
			observationDtos.stream().map(o -> Integer.valueOf(o.getObservationDbId())).collect(Collectors.toList()));
		final ObservationDto observationDto = this.observationServiceBrapi
			.searchObservations(observationSearchRequestDto, null).get(0);
		Assert.assertEquals(VALUE, observationDto.getValue());
		Assert.assertEquals(observationDtos.get(0).getObservationDbId().toString(), observationDto.getObservationDbId());
		Assert.assertEquals(this.observationUnitDbId, observationDto.getObservationUnitDbId());
		Assert.assertEquals(this.variableDTO.getObservationVariableDbId(), observationDto.getObservationVariableDbId());
		Assert.assertEquals(this.germplasm.getGermplasmUUID(), observationDto.getGermplasmDbId());
		Assert.assertEquals(1, observationDto.getExternalReferences().size());
		Assert.assertEquals(REF_ID, observationDto.getExternalReferences().get(0).getReferenceID());
		Assert.assertEquals(REF_SOURCE, observationDto.getExternalReferences().get(0).getReferenceSource());
	}

	@Test
	public void testCreateObservations_AllInfoSavedForCategoricalVariable() {
		final String value = "value";
		final List<String> possibleValues = Arrays.asList(value, "b", "c");
		final CVTerm categoricalVariable =
			this.testDataInitializer.createCategoricalVariable(VariableType.TRAIT, possibleValues);
		final VariableSearchRequestDTO variableSearchRequestDTO = new VariableSearchRequestDTO();
		variableSearchRequestDTO.setObservationVariableDbIds(Collections.singletonList(categoricalVariable.getCvTermId().toString()));
		final VariableDTO categoricalVariableDto = this.variableServiceBrapi
			.getVariables(variableSearchRequestDTO, null, VariableTypeGroup.TRAIT).get(0);
		categoricalVariableDto.setStudyDbIds(Collections.singletonList(this.studyInstanceDto.getStudyDbId()));
		this.variableServiceBrapi.updateObservationVariable(this.variableDTO);

		final ObservationDto observationDto = new ObservationDto();
		observationDto.setGermplasmDbId(this.germplasm.getGermplasmUUID());
		observationDto.setStudyDbId(this.studyInstanceDto.getStudyDbId());
		observationDto.setObservationVariableDbId(categoricalVariableDto.getObservationVariableDbId());
		observationDto.setObservationUnitDbId(this.observationUnitDbId);
		final ExternalReferenceDTO externalReferenceDTO = new ExternalReferenceDTO();
		externalReferenceDTO.setReferenceID(REF_ID);
		externalReferenceDTO.setReferenceSource(REF_SOURCE);
		observationDto.setExternalReferences(Collections.singletonList(externalReferenceDTO));
		observationDto.setValue(value);
		final List<ObservationDto> observationDtos = this.observationServiceBrapi
				.createObservations(Collections.singletonList(observationDto));

		final ObservationSearchRequestDto observationSearchRequestDto = new ObservationSearchRequestDto();
		observationSearchRequestDto.setObservationDbIds(
			observationDtos.stream().map(o -> Integer.valueOf(o.getObservationDbId())).collect(Collectors.toList()));
		final ObservationDto resultObservationDto = this.observationServiceBrapi
			.searchObservations(observationSearchRequestDto, null).get(0);
		Assert.assertEquals(value, resultObservationDto.getValue());
		Assert.assertEquals(observationDtos.get(0).getObservationDbId(), resultObservationDto.getObservationDbId());
		Assert.assertEquals(this.observationUnitDbId, resultObservationDto.getObservationUnitDbId());
		Assert.assertEquals(categoricalVariableDto.getObservationVariableDbId(), resultObservationDto.getObservationVariableDbId());
		Assert.assertEquals(this.germplasm.getGermplasmUUID(), resultObservationDto.getGermplasmDbId());
		Assert.assertEquals(1, resultObservationDto.getExternalReferences().size());
		Assert.assertEquals(REF_ID, resultObservationDto.getExternalReferences().get(0).getReferenceID());
		Assert.assertEquals(REF_SOURCE, resultObservationDto.getExternalReferences().get(0).getReferenceSource());

	}

	private List<ObservationDto> createObservationDtos() {
		final ObservationDto observationDto = new ObservationDto();
		observationDto.setGermplasmDbId(this.germplasm.getGermplasmUUID());
		observationDto.setStudyDbId(this.studyInstanceDto.getStudyDbId());
		observationDto.setObservationVariableDbId(this.variableDTO.getObservationVariableDbId());
		observationDto.setObservationUnitDbId(this.observationUnitDbId);
		final ExternalReferenceDTO externalReferenceDTO = new ExternalReferenceDTO();
		externalReferenceDTO.setReferenceID(REF_ID);
		externalReferenceDTO.setReferenceSource(REF_SOURCE);
		observationDto.setExternalReferences(Collections.singletonList(externalReferenceDTO));
		observationDto.setValue(VALUE);

		final List<ObservationDto> observations = this.observationServiceBrapi
			.createObservations(Collections.singletonList(observationDto));

		this.sessionProvder.getSession().flush();
		return observations;
	}
}
