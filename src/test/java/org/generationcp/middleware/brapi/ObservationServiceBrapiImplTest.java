package org.generationcp.middleware.brapi;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.ContextHolder;
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
import org.generationcp.middleware.api.ontology.OntologyVariableService;
import org.generationcp.middleware.api.program.ProgramService;
import org.generationcp.middleware.api.role.RoleService;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.domain.dms.StudySummary;
import org.generationcp.middleware.domain.gms.SystemDefinedEntryType;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.domain.search_request.brapi.v2.VariableSearchRequestDTO;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.WorkbenchDaoFactory;
import org.generationcp.middleware.manager.ontology.daoElements.VariableFilter;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.PhenotypeExternalReference;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.analysis.SiteAnalysisService;
import org.generationcp.middleware.service.api.study.StudyInstanceDto;
import org.generationcp.middleware.service.api.study.VariableDTO;
import org.generationcp.middleware.service.impl.analysis.MeansImportRequest;
import org.generationcp.middleware.service.impl.analysis.SummaryStatisticsImportRequest;
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
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

public class ObservationServiceBrapiImplTest extends IntegrationTestBase {

	public static final String REF_ID = "refId";
	public static final String REF_SOURCE = "refSource";
	public static final String VALUE = "1";
	public static final String PROP1 = "PROP1";
	@Resource
	private TrialServiceBrapi trialServiceBrapi;

	@Resource
	private StudyServiceBrapi studyServiceBrapi;

	@Resource
	private ObservationUnitService observationUnitService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private WorkbenchTestDataUtil workbenchTestDataUtil;

	@Autowired
	private ObservationServiceBrapi observationServiceBrapi;

	@Autowired
	private VariableServiceBrapi variableServiceBrapi;

	@Autowired
	private ProgramService programService;

	@Autowired
	private SiteAnalysisService analysisService;

	@Autowired
	private OntologyVariableService ontologyVariableService;

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

	private WorkbenchDaoFactory workbenchDaoFactory;

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

		final StudyImportRequestDTO dto = new StudyImportRequestDTO();
		dto.setTrialDbId(String.valueOf(this.studySummary.getTrialDbId()));
		this.studyInstanceDto = this.studyServiceBrapi
			.saveStudyInstances(this.crop.getCropName(), Collections.singletonList(dto), this.testUser.getUserid()).get(0);

		this.germplasm = GermplasmTestDataInitializer.createGermplasm(1);
		this.germplasm.setGid(null);
		GermplasmGuidGenerator.generateGermplasmGuids(this.crop, Collections.singletonList(this.germplasm));
		this.daoFactory.getGermplasmDao().save(this.germplasm);

		this.sessionProvder.getSession().flush();

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
		Assert.assertEquals(observationDtos.get(0).getObservationDbId().toString(), observationDto.getObservationDbId());
		Assert.assertEquals(this.observationUnitDbId, observationDto.getObservationUnitDbId());
		Assert.assertEquals(this.variableDTO.getObservationVariableDbId(), observationDto.getObservationVariableDbId());
		Assert.assertEquals(this.germplasm.getGermplasmUUID(), observationDto.getGermplasmDbId());
		Assert.assertEquals(1, observationDto.getExternalReferences().size());
		Assert.assertEquals(REF_ID, observationDto.getExternalReferences().get(0).getReferenceID());
		Assert.assertEquals(REF_SOURCE, observationDto.getExternalReferences().get(0).getReferenceSource());
		Assert.assertTrue(observationDto.getAdditionalInfo().containsKey(PROP1));
		Assert.assertEquals(VALUE, observationDto.getAdditionalInfo().get(PROP1));
		Assert.assertNotNull(observationDto.getJsonProps());

		final Integer experimentId = this.daoFactory.getExperimentDao()
			.getByObsUnitIds(Arrays.asList(observationDto.getObservationUnitDbId())).get(0).getNdExperimentId();
		final Phenotype phenotype = this.daoFactory.getPhenotypeDAO().getPhenotypeByExperimentIdAndObservableId(
			experimentId, Integer.parseInt(observationDto.getObservationVariableDbId()));
		Assert.assertEquals(VALUE, phenotype.getDraftValue());
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
		Assert.assertEquals(observationDtos.get(0).getObservationDbId(), resultObservationDto.getObservationDbId());
		Assert.assertEquals(this.observationUnitDbId, resultObservationDto.getObservationUnitDbId());
		Assert.assertEquals(categoricalVariableDto.getObservationVariableDbId(), resultObservationDto.getObservationVariableDbId());
		Assert.assertEquals(this.germplasm.getGermplasmUUID(), resultObservationDto.getGermplasmDbId());
		Assert.assertEquals(1, resultObservationDto.getExternalReferences().size());
		Assert.assertEquals(REF_ID, resultObservationDto.getExternalReferences().get(0).getReferenceID());
		Assert.assertEquals(REF_SOURCE, resultObservationDto.getExternalReferences().get(0).getReferenceSource());

		final Integer experimentId = this.daoFactory.getExperimentDao()
			.getByObsUnitIds(Arrays.asList(resultObservationDto.getObservationUnitDbId())).get(0).getNdExperimentId();
		final Phenotype phenotype = this.daoFactory.getPhenotypeDAO().getPhenotypeByExperimentIdAndObservableId(
			experimentId, Integer.parseInt(resultObservationDto.getObservationVariableDbId()));
		Assert.assertEquals(value, phenotype.getDraftValue());
	}

	@Test
	public void testCreateObservations_AutomaticallyAssociateTraitAndSelectionMethodVariableToPlotDataset() {

		// Create a new TRAIT variable that is not yet associated to the PLOT dataset
		final CVTerm traitVariable =
			this.testDataInitializer.createVariableWithScale(DataType.NUMERIC_VARIABLE, VariableType.TRAIT);
		final CVTerm selectionMethodVariable =
			this.testDataInitializer.createVariableWithScale(DataType.NUMERIC_VARIABLE, VariableType.SELECTION_METHOD);
		// Create a new ANALYSIS variable, this variable should not be added to the PLOT dataset
		final CVTerm analysisVariable =
			this.testDataInitializer.createVariableWithScale(DataType.NUMERIC_VARIABLE, VariableType.ANALYSIS);

		final ObservationDto observationDtoForTrait = this.createObservationDto(RandomStringUtils.randomNumeric(5), traitVariable);
		final ObservationDto observationDtoForSelectionMethod =
			this.createObservationDto(RandomStringUtils.randomNumeric(5), selectionMethodVariable);
		final ObservationDto observationDtoForAnalysis = this.createObservationDto(RandomStringUtils.randomNumeric(5), analysisVariable);
		final List<ObservationDto> observationDtos = this.observationServiceBrapi
			.createObservations(Arrays.asList(observationDtoForTrait, observationDtoForSelectionMethod, observationDtoForAnalysis));

		final int plotDatasetId = this.daoFactory.getDmsProjectDAO()
			.getDatasetsByTypeForStudy(this.studySummary.getTrialDbId(), DatasetTypeEnum.PLOT_DATA.getId()).get(0).getProjectId();

		final Map<Integer, Map<Integer, ProjectProperty>> datasetVariablesMaps =
			this.daoFactory.getProjectPropertyDAO().getPropsForProjectIds(Arrays.asList(plotDatasetId))
				.entrySet().stream()
				.collect(toMap(Map.Entry::getKey,
					entry -> entry.getValue().stream().collect(toMap(ProjectProperty::getVariableId, Function.identity()))));

		Assert.assertTrue(datasetVariablesMaps.containsKey(plotDatasetId));
		final Map<Integer, ProjectProperty> projectPropertyMap = datasetVariablesMaps.get(plotDatasetId);
		Assert.assertEquals(VariableType.TRAIT.getId(), projectPropertyMap.get(traitVariable.getCvTermId()).getTypeId());
		Assert.assertEquals(VariableType.SELECTION_METHOD.getId(),
			projectPropertyMap.get(selectionMethodVariable.getCvTermId()).getTypeId());
		// Only TRAIT and SELECTION METHOD observation variables can be associated automatically to the plot dataset.
		Assert.assertFalse(datasetVariablesMaps.get(plotDatasetId).containsKey(analysisVariable.getCvTermId()));

	}

	@Test
	public void testCreateObservations_AutomaticallyAssociateAnalysisVariableToMeansDataset() {

		// Create a new ANALYSIS variable that is not yet associated to the MEANS dataset
		final CVTerm analysisVariable =
			this.testDataInitializer.createVariableWithScale(DataType.NUMERIC_VARIABLE, VariableType.ANALYSIS);
		// Create a new TRAIT variable, this variable should not be added to the MEANS dataset
		final CVTerm traitVariable =
			this.testDataInitializer.createVariableWithScale(DataType.NUMERIC_VARIABLE, VariableType.TRAIT);

		final int meansDatasetId = this.createMeansDataset();

		final List<Geolocation> environmentGeolocations =
			this.daoFactory.getGeolocationDao().getEnvironmentGeolocations(this.studySummary.getTrialDbId());

		final List<ExperimentModel> experimentModels = this.daoFactory.getExperimentDao()
			.getObservationUnits(meansDatasetId, environmentGeolocations.stream().map(Geolocation::getLocationId).collect(
				Collectors.toList()));

		final ObservationDto observationDtoForAnalysisVariable = new ObservationDto();
		observationDtoForAnalysisVariable.setGermplasmDbId(this.germplasm.getGermplasmUUID());
		observationDtoForAnalysisVariable.setStudyDbId(this.studyInstanceDto.getStudyDbId());
		observationDtoForAnalysisVariable.setObservationVariableDbId(analysisVariable.getCvTermId().toString());
		observationDtoForAnalysisVariable.setObservationUnitDbId(experimentModels.get(0).getObsUnitId());

		final ObservationDto observationDtoForTraitVariable = new ObservationDto();
		observationDtoForTraitVariable.setGermplasmDbId(this.germplasm.getGermplasmUUID());
		observationDtoForTraitVariable.setStudyDbId(this.studyInstanceDto.getStudyDbId());
		observationDtoForTraitVariable.setObservationVariableDbId(traitVariable.getCvTermId().toString());
		observationDtoForTraitVariable.setObservationUnitDbId(experimentModels.get(0).getObsUnitId());

		final List<ObservationDto> observationDtos = this.observationServiceBrapi
			.createObservations(Arrays.asList(observationDtoForAnalysisVariable, observationDtoForTraitVariable));

		final Map<Integer, Map<Integer, ProjectProperty>> datasetVariablesMaps =
			this.daoFactory.getProjectPropertyDAO().getPropsForProjectIds(Arrays.asList(meansDatasetId))
				.entrySet().stream()
				.collect(toMap(Map.Entry::getKey,
					entry -> entry.getValue().stream().collect(toMap(ProjectProperty::getVariableId, Function.identity()))));

		Assert.assertTrue(datasetVariablesMaps.containsKey(meansDatasetId));
		final Map<Integer, ProjectProperty> projectPropertyMap = datasetVariablesMaps.get(meansDatasetId);
		Assert.assertEquals(VariableType.ANALYSIS.getId(), projectPropertyMap.get(analysisVariable.getCvTermId()).getTypeId());
		// Only ANALYSIS observation variable can be associated automatically to the means dataset.
		Assert.assertFalse(datasetVariablesMaps.get(meansDatasetId).containsKey(traitVariable.getCvTermId()));

	}

	@Test
	public void testCreateObservations_AutomaticallyAssociateAnalysisSummaryVariableToSummaryStatisticsDataset() {

		// Create a new ANALYSIS_SUMMARY variable that is not yet associated to the SUMMARY-STATISTICS dataset
		final CVTerm analysisSummaryVariable =
			this.testDataInitializer.createVariableWithScale(DataType.NUMERIC_VARIABLE, VariableType.ANALYSIS_SUMMARY);
		// Create a new ANALYSIS variable, this variable should not be added to the SUMMARY-STATISTICS dataset
		final CVTerm analysisVariable =
			this.testDataInitializer.createVariableWithScale(DataType.NUMERIC_VARIABLE, VariableType.TRAIT);

		final int summaryStatisticsDatasetId = this.createSummaryStatisticsDataset();

		final List<Geolocation> environmentGeolocations =
			this.daoFactory.getGeolocationDao().getEnvironmentGeolocations(this.studySummary.getTrialDbId());

		final List<ExperimentModel> experimentModels = this.daoFactory.getExperimentDao()
			.getObservationUnits(summaryStatisticsDatasetId, environmentGeolocations.stream().map(Geolocation::getLocationId).collect(
				Collectors.toList()));

		final ObservationDto observationDtoForAnalysisVariable = new ObservationDto();
		observationDtoForAnalysisVariable.setGermplasmDbId(this.germplasm.getGermplasmUUID());
		observationDtoForAnalysisVariable.setStudyDbId(this.studyInstanceDto.getStudyDbId());
		observationDtoForAnalysisVariable.setObservationVariableDbId(analysisSummaryVariable.getCvTermId().toString());
		observationDtoForAnalysisVariable.setObservationUnitDbId(experimentModels.get(0).getObsUnitId());

		final ObservationDto observationDtoForTraitVariable = new ObservationDto();
		observationDtoForTraitVariable.setGermplasmDbId(this.germplasm.getGermplasmUUID());
		observationDtoForTraitVariable.setStudyDbId(this.studyInstanceDto.getStudyDbId());
		observationDtoForTraitVariable.setObservationVariableDbId(analysisVariable.getCvTermId().toString());
		observationDtoForTraitVariable.setObservationUnitDbId(experimentModels.get(0).getObsUnitId());

		final List<ObservationDto> observationDtos = this.observationServiceBrapi
			.createObservations(Arrays.asList(observationDtoForAnalysisVariable, observationDtoForTraitVariable));

		final Map<Integer, Map<Integer, ProjectProperty>> datasetVariablesMaps =
			this.daoFactory.getProjectPropertyDAO().getPropsForProjectIds(Arrays.asList(summaryStatisticsDatasetId))
				.entrySet().stream()
				.collect(toMap(Map.Entry::getKey,
					entry -> entry.getValue().stream().collect(toMap(ProjectProperty::getVariableId, Function.identity()))));

		Assert.assertTrue(datasetVariablesMaps.containsKey(summaryStatisticsDatasetId));
		final Map<Integer, ProjectProperty> projectPropertyMap = datasetVariablesMaps.get(summaryStatisticsDatasetId);
		// Only ANALYSIS_SUMMARY observation variable can be associated automatically to the SUMMARY-STATISTICS dataset.
		Assert.assertEquals(VariableType.ANALYSIS_SUMMARY.getId(),
			projectPropertyMap.get(analysisSummaryVariable.getCvTermId()).getTypeId());
		Assert.assertFalse(datasetVariablesMaps.get(summaryStatisticsDatasetId).containsKey(analysisVariable.getCvTermId()));

	}

	@Test
	public void testUpdateObservations() {
		final List<ObservationDto> observationDtos = this.createObservationDtos();
		Phenotype phenotype = this.daoFactory.getPhenotypeDAO().getById(Integer.valueOf(observationDtos.get(0).getObservationDbId()));
		Assert.assertEquals(VALUE, phenotype.getDraftValue());
		observationDtos.get(0).setValue(RandomStringUtils.randomNumeric(5));
		this.observationServiceBrapi.updateObservations(observationDtos);
		phenotype = this.daoFactory.getPhenotypeDAO().getById(Integer.valueOf(observationDtos.get(0).getObservationDbId()));
		Assert.assertNotEquals(VALUE, phenotype.getDraftValue());
		Assert.assertEquals(observationDtos.get(0).getValue(), phenotype.getDraftValue());
	}

	@Test
	public void testUpdateObservations_UpdateExternalReferences() {
		final List<ObservationDto> observationDtos = this.createObservationDtos();
		Phenotype phenotype = this.daoFactory.getPhenotypeDAO().getById(Integer.valueOf(observationDtos.get(0).getObservationDbId()));
		Assert.assertEquals(1, phenotype.getExternalReferences().size());
		Assert.assertEquals(observationDtos.get(0).getExternalReferences().get(0).getReferenceID(),
			phenotype.getExternalReferences().get(0).getReferenceId());

		final String newRefId = RandomStringUtils.randomAlphanumeric(10);
		observationDtos.get(0).getExternalReferences().get(0).setReferenceID(newRefId);
		final ExternalReferenceDTO externalReferenceDTO = new ExternalReferenceDTO();
		externalReferenceDTO.setReferenceID(RandomStringUtils.randomAlphanumeric(10));
		externalReferenceDTO.setReferenceSource(RandomStringUtils.randomAlphanumeric(10));
		observationDtos.get(0).getExternalReferences().add(externalReferenceDTO);

		this.observationServiceBrapi.updateObservations(observationDtos);
		phenotype = this.daoFactory.getPhenotypeDAO().getById(Integer.valueOf(observationDtos.get(0).getObservationDbId()));
		Assert.assertEquals(2, phenotype.getExternalReferences().size());
		final Optional<PhenotypeExternalReference> existingPhenotypeExternalReference = phenotype.getExternalReferences().stream()
			.filter(exref -> exref.getSource().equals(REF_SOURCE)).findFirst();
		Assert.assertTrue(existingPhenotypeExternalReference.isPresent());
		Assert.assertEquals(newRefId, existingPhenotypeExternalReference.get().getReferenceId());

		final Optional<PhenotypeExternalReference> addedPhenotypeExternalReference = phenotype.getExternalReferences().stream()
			.filter(exref -> exref.getSource().equals(externalReferenceDTO.getReferenceSource())).findFirst();
		Assert.assertTrue(addedPhenotypeExternalReference.isPresent());
		Assert.assertEquals(externalReferenceDTO.getReferenceID(), addedPhenotypeExternalReference.get().getReferenceId());
	}

	private ObservationDto createObservationDto(final String value, final CVTerm traitVariable) {
		final ObservationDto observationDtoForTrait = new ObservationDto();
		observationDtoForTrait.setGermplasmDbId(this.germplasm.getGermplasmUUID());
		observationDtoForTrait.setStudyDbId(this.studyInstanceDto.getStudyDbId());
		observationDtoForTrait.setObservationVariableDbId(traitVariable.getCvTermId().toString());
		observationDtoForTrait.setObservationUnitDbId(this.observationUnitDbId);
		final ExternalReferenceDTO externalReferenceDTO = new ExternalReferenceDTO();
		externalReferenceDTO.setReferenceID(REF_ID);
		externalReferenceDTO.setReferenceSource(REF_SOURCE);
		observationDtoForTrait.setExternalReferences(Collections.singletonList(externalReferenceDTO));
		observationDtoForTrait.setValue(value);
		return observationDtoForTrait;
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
		observationDto.setAdditionalInfo(Collections.singletonMap(PROP1, VALUE));

		final List<ObservationDto> observations = this.observationServiceBrapi
			.createObservations(Collections.singletonList(observationDto));

		this.sessionProvder.getSession().flush();
		return observations;
	}

	private MeansImportRequest.MeansData createMeansData(final int environmentNumber, final int entryNo,
		final Map<Integer, Variable> analysisVariablesMap) {
		final MeansImportRequest.MeansData meansData = new MeansImportRequest.MeansData();
		meansData.setEntryNo(entryNo);
		meansData.setEnvironmentNumber(environmentNumber);
		final Map<String, Double> valuesMap = new HashMap<>();
		for (final Variable variable : analysisVariablesMap.values()) {
			valuesMap.put(variable.getName(), new Random().nextDouble());
		}
		meansData.setValues(valuesMap);
		return meansData;
	}

	private SummaryStatisticsImportRequest.SummaryData createSummaryData(final int environmentNumber,
		final Map<Integer, Variable> analysisSummaryVariablesMap) {
		final SummaryStatisticsImportRequest.SummaryData summaryData = new SummaryStatisticsImportRequest.SummaryData();
		summaryData.setEnvironmentNumber(environmentNumber);
		final Map<String, Double> valuesMap = new HashMap<>();
		for (final Variable variable : analysisSummaryVariablesMap.values()) {
			valuesMap.put(variable.getName(), new Random().nextDouble());
		}
		summaryData.setValues(valuesMap);
		return summaryData;
	}

	private int createMeansDataset() {
		final int testStudyId = this.studySummary.getTrialDbId();

		final CVTerm existingAnalysisVariable =
			this.testDataInitializer.createVariableWithScale(DataType.NUMERIC_VARIABLE, VariableType.ANALYSIS);

		final VariableFilter variableFilter = new VariableFilter();
		variableFilter.addVariableId(existingAnalysisVariable.getCvTermId());
		final Map<Integer, Variable> analysisVariablesMap = this.ontologyVariableService.getVariablesWithFilterById(variableFilter);

		// Add means dataset to the test study
		final MeansImportRequest meansImportRequest = new MeansImportRequest();
		final List<Geolocation> environmentGeolocations =
			this.daoFactory.getGeolocationDao().getEnvironmentGeolocations(testStudyId);
		final List<MeansImportRequest.MeansData> meansDataList =
			environmentGeolocations.stream().map(o -> this.createMeansData(Integer.valueOf(o.getDescription()), 1, analysisVariablesMap))
				.collect(Collectors.toList());
		meansImportRequest.setData(meansDataList);

		return this.analysisService.createMeansDataset(ContextHolder.getCurrentCrop(), testStudyId, meansImportRequest);
	}

	private int createSummaryStatisticsDataset() {
		final int testStudyId = this.studySummary.getTrialDbId();

		final CVTerm existingSummaryVariable =
			this.testDataInitializer.createVariableWithScale(DataType.NUMERIC_VARIABLE, VariableType.ANALYSIS_SUMMARY);

		final VariableFilter variableFilter = new VariableFilter();
		variableFilter.addVariableId(existingSummaryVariable.getCvTermId());
		final Map<Integer, Variable> analysisSummaryVariablesMap = this.ontologyVariableService.getVariablesWithFilterById(variableFilter);

		// Add means dataset to the test study
		final SummaryStatisticsImportRequest summaryStatisticsImportRequest = new SummaryStatisticsImportRequest();
		final List<Geolocation> environmentGeolocations =
			this.daoFactory.getGeolocationDao().getEnvironmentGeolocations(testStudyId);
		final List<SummaryStatisticsImportRequest.SummaryData> summaryDataList =
			environmentGeolocations.stream()
				.map(o -> this.createSummaryData(Integer.valueOf(o.getDescription()), analysisSummaryVariablesMap))
				.collect(Collectors.toList());
		summaryStatisticsImportRequest.setData(summaryDataList);

		return this.analysisService.createSummaryStatisticsDataset(ContextHolder.getCurrentCrop(), testStudyId,
			summaryStatisticsImportRequest);
	}
}
