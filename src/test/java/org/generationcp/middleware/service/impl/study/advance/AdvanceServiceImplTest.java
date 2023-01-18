package org.generationcp.middleware.service.impl.study.advance;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.DataSetupTest;
import org.generationcp.middleware.GermplasmTestDataGenerator;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.api.germplasm.GermplasmService;
import org.generationcp.middleware.api.study.AdvanceSamplesRequest;
import org.generationcp.middleware.api.study.AdvanceStudyRequest;
import org.generationcp.middleware.dao.MethodDAO;
import org.generationcp.middleware.dao.dms.InstanceMetadata;
import org.generationcp.middleware.domain.dataset.ObservationDto;
import org.generationcp.middleware.domain.dms.DatasetDTO;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.enumeration.SampleListType;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.GermplasmNameType;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmStudySource;
import org.generationcp.middleware.pojos.GermplasmStudySourceType;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.MethodType;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.Sample;
import org.generationcp.middleware.pojos.SampleList;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.service.api.DataImportService;
import org.generationcp.middleware.service.api.FieldbookService;
import org.generationcp.middleware.service.api.SampleService;
import org.generationcp.middleware.service.api.dataset.DatasetService;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.generationcp.middleware.service.api.dataset.ObservationUnitsSearchDTO;
import org.generationcp.middleware.service.api.study.advance.AdvanceService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class AdvanceServiceImplTest extends IntegrationTestBase {

	private static final String BREEDING_METHOD_SEPARATOR = "-";

	private static final String PREFERRED_NAME_PREFIX = "PREFF";

	private Project commonTestProject;

	private GermplasmTestDataGenerator germplasmTestDataGenerator;
	private DataSetupTest dataSetupTest;

	@Autowired
	private AdvanceService advanceService;

	@Autowired
	private WorkbenchTestDataUtil workbenchTestDataUtil;

	@Autowired
	private StudyDataManager studyDataManager;

	@Autowired
	private DataImportService dataImportService;

	@Autowired
	private GermplasmListManager germplasmListManager;

	@Autowired
	private FieldbookService middlewareFieldbookService;

	@Autowired
	private GermplasmService germplasmService;

	@Autowired
	private OntologyDataManager ontologyDataManager;

	@Autowired
	private DatasetService datasetService;

	@Autowired
	private SampleService sampleService;

	private Integer studyId;
	private Integer plotDatasetId;
	private List<Integer> instanceIds;
	private Integer locationId;
	private Germplasm parentGermplasm;
	private Integer[] originGermplasmGids;

	private Integer plotCodeVariableId;
	private Integer plotNumberVariableId;
	private Integer repNumberVariableId;
	private Integer trialInstanceVariableId;
	private Integer plantNumberVariableId;

	private Integer methodSelectionVariableId;
	private Integer lineSelectionVariableId;

	private DaoFactory daoFactory;
	private MethodDAO methodDAO;

	@Before
	public void setUp() throws Exception {
		if (this.daoFactory == null) {
			this.daoFactory = new DaoFactory(this.sessionProvder);
		}

		if (this.methodDAO == null) {
			this.methodDAO = new MethodDAO(this.sessionProvder.getSession());
		}

		if (this.commonTestProject == null) {
			this.commonTestProject = this.workbenchTestDataUtil.getCommonTestProject();
		}

		if (this.germplasmTestDataGenerator == null) {
			this.germplasmTestDataGenerator = new GermplasmTestDataGenerator(this.sessionProvder, this.daoFactory);
		}

		this.dataSetupTest = new DataSetupTest();
		this.dataSetupTest.setDataImportService(this.dataImportService);
		this.dataSetupTest.setGermplasmListManager(this.germplasmListManager);
		this.dataSetupTest.setMiddlewareFieldbookService(this.middlewareFieldbookService);

		if (this.plotCodeVariableId == null) {
			this.plotCodeVariableId = this.germplasmService.getPlotCodeField().getId();
		}
		if (this.plotNumberVariableId == null) {
			this.plotNumberVariableId = this.getVariableId(AdvanceServiceImpl.PLOT_NUMBER_VARIABLE_NAME);
		}
		if (this.repNumberVariableId == null) {
			this.repNumberVariableId = this.getVariableId(AdvanceServiceImpl.REP_NUMBER_VARIABLE_NAME);
		}
		if (this.trialInstanceVariableId == null) {
			this.trialInstanceVariableId = this.getVariableId(AdvanceServiceImpl.TRIAL_INSTANCE_VARIABLE_NAME);
		}
		if (this.plantNumberVariableId == null) {
			this.plantNumberVariableId = this.getVariableId(AdvanceServiceImpl.PLANT_NUMBER_VARIABLE_NAME);
		}
	}

	@Test
	public void advanceStudy_OK_usingSameNotBulkingMethodAndLinesSelectedAndFilterByReplicationNumber() {
		this.createTestStudy(1, 2);

		final Method method = this.createMethod(false);

		// Getting all replications
		final AdvanceStudyRequest.BreedingMethodSelectionRequest breedingMethodSelectionRequest =
			this.createBreedingMethodSelectionRequest(method.getMid(), null);
		final AdvanceStudyRequest.LineSelectionRequest lineSelectionRequest = this.createLineSelectionRequest(1, null);
		final AdvanceStudyRequest advanceStudyRequest =
			this.createAdvanceStudyRequest(null, breedingMethodSelectionRequest, lineSelectionRequest, null, null);

		final List<Integer> advancedGermplasmGidsForAllReps = this.advanceService.advanceStudy(this.studyId, advanceStudyRequest);
		assertThat(advancedGermplasmGidsForAllReps, hasSize(2));

		final List<Germplasm> originGermplasms = this.daoFactory.getGermplasmDao().getByGIDList(Arrays.asList(this.originGermplasmGids));
		final List<Germplasm> advancedGermplasmsForAllReps =
			this.daoFactory.getGermplasmDao().getByGIDList(advancedGermplasmGidsForAllReps);

		// Assert advanced germplasm 1 for replication 1
		this.assertAdvancedGermplasm(advancedGermplasmsForAllReps.get(0), method, originGermplasms.get(0).getGid(), "1", "1", 1, 1);
		// Assert advanced germplasm 2 for replication 2
		this.assertAdvancedGermplasm(advancedGermplasmsForAllReps.get(1), method, originGermplasms.get(0).getGid(), "2", "2", 1, 1);

		// Getting only the first replication
		advanceStudyRequest.setSelectedReplications(Arrays.asList(1));
		final List<Integer> advancedGermplasmGidsForRep1 = this.advanceService.advanceStudy(this.studyId, advanceStudyRequest);
		assertThat(advancedGermplasmGidsForRep1, hasSize(1));

		final List<Germplasm> advancedGermplasmsForRep1 = this.daoFactory.getGermplasmDao().getByGIDList(advancedGermplasmGidsForRep1);
		assertThat(advancedGermplasmsForRep1, hasSize(1));
		this.assertAdvancedGermplasm(advancedGermplasmsForRep1.get(0), method, originGermplasms.get(0).getGid(), "1", "1", 1, 1);

		// Getting only the second replication
		advanceStudyRequest.setSelectedReplications(Arrays.asList(2));
		final List<Integer> advancedGermplasmGidsForRep2 = this.advanceService.advanceStudy(this.studyId, advanceStudyRequest);
		assertThat(advancedGermplasmGidsForRep2, hasSize(1));

		final List<Germplasm> advancedGermplasmsForRep2 = this.daoFactory.getGermplasmDao().getByGIDList(advancedGermplasmGidsForRep2);
		assertThat(advancedGermplasmsForRep2, hasSize(1));
		this.assertAdvancedGermplasm(advancedGermplasmsForRep2.get(0), method, originGermplasms.get(0).getGid(), "2", "2", 1, 1);
	}

	@Test
	public void advanceStudy_OK_usingVariateForBreedingSelectionAndForSelectedLinesAndForBulking() {
		this.createTestStudy(3, 1);

		final Method notBulkingMethod = this.createMethod(false);
		final Method bulkingMethod = this.createMethod(true);

		final List<ObservationUnitRow> observationUnitRows = this.datasetService
			.getObservationUnitRows(this.studyId, this.plotDatasetId, new ObservationUnitsSearchDTO(), new PageRequest(0, 10));
		assertFalse(CollectionUtils.isEmpty(observationUnitRows));
		assertThat(observationUnitRows, hasSize(3));

		// Create observation for breeding method selection:
		// For the first observation it should advance the number of plants defined in the variable 'cause the method is not bulking
		this.createObservation(this.methodSelectionVariableId, observationUnitRows.get(0).getObservationUnitId(),
			notBulkingMethod.getMcode());

		// We are not going to set any plant por plots for the second observation, so it should not advance any germplasm
		this.createObservation(this.methodSelectionVariableId, observationUnitRows.get(1).getObservationUnitId(), bulkingMethod.getMcode());

		// For the third observation it should advance only one plot no matter how many plants where set per plot 'cause the method is bulking
		this.createObservation(this.methodSelectionVariableId, observationUnitRows.get(2).getObservationUnitId(), bulkingMethod.getMcode());

		// Create observation for line selection
		this.createObservation(this.lineSelectionVariableId, observationUnitRows.get(0).getObservationUnitId(), "2");
		this.createObservation(this.lineSelectionVariableId, observationUnitRows.get(2).getObservationUnitId(), "800");

		final AdvanceStudyRequest.BreedingMethodSelectionRequest breedingMethodSelectionRequest =
			this.createBreedingMethodSelectionRequest(null, this.methodSelectionVariableId);
		final AdvanceStudyRequest.LineSelectionRequest lineSelectionRequest =
			this.createLineSelectionRequest(null, this.lineSelectionVariableId);
		final AdvanceStudyRequest.BulkingRequest bulkingRequest = this.createBulkingRequest(null, this.lineSelectionVariableId);
		final AdvanceStudyRequest advanceStudyRequest =
			this.createAdvanceStudyRequest(null, breedingMethodSelectionRequest, lineSelectionRequest, bulkingRequest, null);

		final List<Integer> advancedGermplasmGids = this.advanceService.advanceStudy(this.studyId, advanceStudyRequest);
		assertThat(advancedGermplasmGids, hasSize(3));

		final List<Germplasm> originGermplasms = this.daoFactory.getGermplasmDao().getByGIDList(Arrays.asList(this.originGermplasmGids));
		final List<Germplasm> advancedGermplasms = this.daoFactory.getGermplasmDao().getByGIDList(advancedGermplasmGids);

		// Assert advanced germplasm 1
		this.assertAdvancedGermplasm(advancedGermplasms.get(0), notBulkingMethod, originGermplasms.get(0).getGid(), "1", "1", 1, 1);
		// Assert advanced germplasm 2
		this.assertAdvancedGermplasm(advancedGermplasms.get(1), notBulkingMethod, originGermplasms.get(0).getGid(), "1", "1", 1, 2);
		// Assert advanced germplasm 3
		this.assertAdvancedGermplasm(advancedGermplasms.get(2), bulkingMethod, originGermplasms.get(2).getGid(), "3", "1", 3, null);
	}

	@Test
	public void advanceSample_OK() {
		this.createTestStudy(1, 1);

		final List<ObservationUnitRow> observationUnitRows = this.datasetService
			.getObservationUnitRows(this.studyId, this.plotDatasetId, new ObservationUnitsSearchDTO(), new PageRequest(0, 10));
		assertFalse(CollectionUtils.isEmpty(observationUnitRows));
		assertThat(observationUnitRows, hasSize(1));

		final SampleList sampleList = this.createSampleList();
		this.createSample(sampleList, observationUnitRows.get(0).getObsUnitId());
		assertTrue(this.sampleService.studyHasSamples(this.studyId));

		final Method notBulkingMethod = this.createMethod(false);

		final AdvanceSamplesRequest advanceSampledPlantsRequest = new AdvanceSamplesRequest();
		advanceSampledPlantsRequest.setBreedingMethodId(notBulkingMethod.getMid());
		final List<Integer> advancedGermplasmGids = this.advanceService.advanceSamples(this.studyId, advanceSampledPlantsRequest);
		assertThat(advancedGermplasmGids, hasSize(1));

		final List<Germplasm> originGermplasms = this.daoFactory.getGermplasmDao().getByGIDList(Arrays.asList(this.originGermplasmGids));
		final List<Germplasm> advancedGermplasms = this.daoFactory.getGermplasmDao().getByGIDList(advancedGermplasmGids);

		this.assertAdvancedGermplasm(advancedGermplasms.get(0), notBulkingMethod, originGermplasms.get(0).getGid(), "1", "1", 1, 1);
	}

	private Integer getVariableId(final String name) {
		final Term term = this.ontologyDataManager.findTermByName(name, CvId.VARIABLES.getId());
		if (term == null) {
			return null;
		}
		return term.getId();
	}

	private void createTestStudy(final int numberOfGermplasm, final int replicationNumber) {
		this.parentGermplasm = this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames();

		this.originGermplasmGids = this.germplasmTestDataGenerator
			.createChildrenGermplasm(numberOfGermplasm, PREFERRED_NAME_PREFIX, this.parentGermplasm);

		this.commonTestProject = this.workbenchTestDataUtil.getCommonTestProject();

		this.studyId =
			this.dataSetupTest
				.createNurseryForGermplasm(this.commonTestProject.getUniqueID(), this.originGermplasmGids, "ABCD", numberOfGermplasm,
					replicationNumber);
		this.instanceIds = new ArrayList<>(this.studyDataManager.getInstanceGeolocationIdsMap(this.studyId).values());

		final List<InstanceMetadata> instanceMetadata = this.studyDataManager.getInstanceMetadata(this.studyId);
		assertNotNull(instanceMetadata);
		assertThat(instanceMetadata, hasSize(1));

		this.locationId = instanceMetadata.get(0).getLocationDbId();
		assertNotNull(this.locationId);

		final List<DatasetDTO> datasets =
			this.datasetService.getDatasets(studyId, Collections.singleton(DatasetTypeEnum.PLOT_DATA.getId()));
		assertFalse(CollectionUtils.isEmpty(datasets));
		assertThat(datasets, hasSize(1));
		this.plotDatasetId = datasets.get(0).getDatasetId();

		if (this.methodSelectionVariableId == null) {
			this.methodSelectionVariableId = this.getVariableId("BM_CODE_VTE");
			assertNotNull(this.methodSelectionVariableId);

			this.datasetService.addDatasetVariable(this.plotDatasetId, methodSelectionVariableId, VariableType.SELECTION_METHOD, null);
		}

		if (this.lineSelectionVariableId == null) {
			this.lineSelectionVariableId = this.getVariableId("NPSEL");
			assertNotNull(this.methodSelectionVariableId);

			this.datasetService.addDatasetVariable(this.plotDatasetId, lineSelectionVariableId, VariableType.SELECTION_METHOD, null);
		}
	}

	private Method createMethod(final boolean isBulking) {
		final String code = RandomStringUtils.randomAlphabetic(8);

		final Method method = new Method();
		method.setMtype(MethodType.DERIVATIVE.getCode());
		method.setMcode(code);
		method.setMname("Method Name " + code);
		method.setMdesc("Method Description " + code);
		method.setLmid(0);
		method.setMattr(0);
		method.setMfprg(0);
		method.setReference(0);
		method.setMdate(20220101);
		method.setMgrp("");
		method.setMprgn(-1);
		method.setUser(this.findAdminUser());
		method.setSeparator(BREEDING_METHOD_SEPARATOR);
		method.setPrefix(code);
		method.setSuffix(RandomStringUtils.randomAlphabetic(8));

		if (isBulking) {
			method.setGeneq(Method.BULKED_CLASSES.get(0));
		} else {
			method.setGeneq(Method.NON_BULKED_CLASSES.get(0));
			method.setCount("[NUMBER]");
		}

		return this.methodDAO.save(method);
	}

	private SampleList createSampleList() {
		final SampleList sampleList = new SampleList();
		sampleList.setListName(RandomStringUtils.randomAlphabetic(10));
		sampleList.setCreatedDate(new Date());
		sampleList.setCreatedByUserId(this.findAdminUser());
		sampleList.setType(SampleListType.SAMPLE_LIST);
		sampleList.setProgramUUID(ContextHolder.getCurrentProgramOptional().get());
		return this.daoFactory.getSampleListDao().save(sampleList);
	}

	private void createSample(final SampleList sampleList, final String observationUnitId) {
		final List<Germplasm> originGermplasms = this.daoFactory.getGermplasmDao().getByGIDList(Arrays.asList(this.originGermplasmGids));

		final Sample sample = new Sample();
		sample.setEntryNumber(1);
		sample.setSampleName(originGermplasms.get(0).getNames().get(0).getNval());
		sample.setTakenBy(this.findAdminUser());
		sample.setSamplingDate(new Date());
		sample.setSampleBusinessKey(RandomStringUtils.randomAlphanumeric(8));
		sample.setSampleList(sampleList);
		sample.setCreatedBy(this.findAdminUser());

		final Optional<ExperimentModel> experimentModel =
			this.daoFactory.getExperimentDao().getByObsUnitId(observationUnitId);
		assertTrue(experimentModel.isPresent());
		sample.setExperiment(experimentModel.get());
		sample.setSampleNumber(1);

		this.daoFactory.getSampleDao().save(sample);
	}

	private AdvanceStudyRequest.BreedingMethodSelectionRequest createBreedingMethodSelectionRequest(final Integer breedingMethodId,
		final Integer methodVariateId) {
		final AdvanceStudyRequest.BreedingMethodSelectionRequest request =
			new AdvanceStudyRequest.BreedingMethodSelectionRequest();
		request.setBreedingMethodId(breedingMethodId);
		request.setMethodVariateId(methodVariateId);
		return request;
	}

	private AdvanceStudyRequest.LineSelectionRequest createLineSelectionRequest(final Integer linesSelected,
		final Integer lineVariateId) {
		final AdvanceStudyRequest.LineSelectionRequest request =
			new AdvanceStudyRequest.LineSelectionRequest();
		request.setLinesSelected(linesSelected);
		request.setLineVariateId(lineVariateId);
		return request;
	}

	private AdvanceStudyRequest.BulkingRequest createBulkingRequest(final Boolean allPlotsSelected,
		final Integer plotVariateId) {
		final AdvanceStudyRequest.BulkingRequest request =
			new AdvanceStudyRequest.BulkingRequest();
		request.setAllPlotsSelected(allPlotsSelected);
		request.setPlotVariateId(plotVariateId);
		return request;
	}

	private AdvanceStudyRequest createAdvanceStudyRequest(final List<Integer> selectedReplications,
		final AdvanceStudyRequest.BreedingMethodSelectionRequest breedingMethodSelectionRequest,
		final AdvanceStudyRequest.LineSelectionRequest lineSelectionRequest, final AdvanceStudyRequest.BulkingRequest bulkingRequest,
		final AdvanceStudyRequest.SelectionTraitRequest selectionTraitRequest) {
		final AdvanceStudyRequest request = new AdvanceStudyRequest();
		request.setInstanceIds(this.instanceIds);
		request.setSelectedReplications(selectedReplications);
		request.setBreedingMethodSelectionRequest(breedingMethodSelectionRequest);
		request.setLineSelectionRequest(lineSelectionRequest);
		request.setBulkingRequest(bulkingRequest);
		request.setSelectionTraitRequest(selectionTraitRequest);
		return request;
	}

	private void assertAdvancedGermplasm(final Germplasm advancedGermplasm, final Method method, final Integer expectedGpid2,
		final String expectedPlotNumber, final String expectedReplicationNumber, final Integer germplasmNumber,
		final Integer plantNumber) {
		this.assertAdvancedGermplasm(advancedGermplasm, method, germplasmNumber, expectedGpid2, plantNumber);

		// Assert attributes
		final Map<Integer, Attribute> advancedGermplasm1AttributesByTypeId =
			this.getAdvancedGermplasmAttributesByTypeId(advancedGermplasm.getGid());
		this.assertPlotCodeAttribute(advancedGermplasm1AttributesByTypeId.get(this.plotCodeVariableId));

		if (this.plotNumberVariableId != null) {
			this.assertGeneratedAttribute(advancedGermplasm1AttributesByTypeId.get(this.plotNumberVariableId), expectedPlotNumber);
		}

		if (this.repNumberVariableId != null) {
			this.assertGeneratedAttribute(advancedGermplasm1AttributesByTypeId.get(this.repNumberVariableId), expectedReplicationNumber);
		}

		if (this.trialInstanceVariableId != null) {
			this.assertGeneratedAttribute(advancedGermplasm1AttributesByTypeId.get(this.trialInstanceVariableId), "1");
		}
	}

	private void assertAdvancedGermplasm(final Germplasm germplasm, final Method selectedMethod, final int germplasmNumber,
		final Integer expectedGipd2, final Integer expectedPlantNumber) {
		assertNotNull(germplasm);

		final Method advancedGermplasmMethod = germplasm.getMethod();
		assertNotNull(advancedGermplasmMethod);
		assertThat(advancedGermplasmMethod.getMid(), is(selectedMethod.getMid()));

		assertThat(germplasm.getGnpgs(), is(-1));
		assertThat(germplasm.getGpid2(), is(expectedGipd2));

		assertThat(germplasm.getCreatedBy(), is(this.findAdminUser()));
		assertThat(germplasm.getLocationId(), is(this.locationId));
		assertNotNull(germplasm.getGdate());
		assertNotNull(germplasm.getGermplasmUUID());

		assertThat(germplasm.getReferenceId(), is(0));
		assertThat(germplasm.getGrplce(), is(0));
		assertThat(germplasm.getMgid(), is(0));

		final List<Name> names = germplasm.getNames();
		assertNotNull(names);
		assertThat(names, hasSize(1));

		// Assert name
		this.assertGeneratedName(germplasm.getNames().get(0), germplasmNumber, selectedMethod, expectedPlantNumber);

		// Assert study source
		this.assertGermplasmStudySource(germplasm.getGid());
	}

	private void assertGeneratedName(final Name generatedName, final int germplasmNumber, final Method selectedMethod,
		final Integer expectedPlantNumber) {
		assertNotNull(generatedName);
		assertNotNull(generatedName.getNid());
		assertNotNull(generatedName.getNdate());
		assertThat(generatedName.getNstat(), is(1));
		assertThat(generatedName.getTypeId(), is(GermplasmNameType.DERIVATIVE_NAME.getUserDefinedFieldID()));
		assertThat(generatedName.getLocationId(), is(this.locationId));

		final StringBuilder expectedName =
			new StringBuilder(PREFERRED_NAME_PREFIX + germplasmNumber + BREEDING_METHOD_SEPARATOR + selectedMethod.getMcode());
		if (expectedPlantNumber != null) {
			expectedName.append(expectedPlantNumber);
		}
		expectedName.append(selectedMethod.getSuffix());

		assertThat(generatedName.getNval(), is(expectedName.toString()));
		assertThat(generatedName.getCreatedBy(), is(this.findAdminUser()));
	}

	private void assertPlotCodeAttribute(final Attribute attribute) {
		assertNotNull(attribute.getAval());

		this.assertAttribute(attribute);
	}

	private void assertGeneratedAttribute(final Attribute attribute, final String value) {
		assertThat(attribute.getAval(), is(value));

		this.assertAttribute(attribute);
	}

	private void assertAttribute(final Attribute attribute) {
		assertNotNull(attribute);
		assertThat(attribute.getCreatedBy(), is(this.findAdminUser()));
		assertThat(attribute.getLocationId(), is(this.locationId));
		assertNotNull(attribute.getAdate());
	}

	private Map<Integer, Attribute> getAdvancedGermplasmAttributesByTypeId(final Integer advanceGermplasmGid) {
		final List<Attribute> advancedGermrplasm1Attributes = this.daoFactory.getAttributeDAO().getByGID(advanceGermplasmGid);
		assertFalse(CollectionUtils.isEmpty(advancedGermrplasm1Attributes));

		return advancedGermrplasm1Attributes.stream().collect(Collectors.toMap(Attribute::getTypeId, attribute -> attribute));
	}

	private void assertGermplasmStudySource(final Integer advancedGermplasmGid) {
		final List<GermplasmStudySource> germplasmStudySources =
			this.daoFactory.getGermplasmStudySourceDAO().getByGids(Collections.singleton(advancedGermplasmGid));
		assertFalse(CollectionUtils.isEmpty(germplasmStudySources));
		assertThat(germplasmStudySources, hasSize(1));

		final GermplasmStudySource germplasmStudySource = germplasmStudySources.get(0);
		final DmsProject study = germplasmStudySource.getStudy();
		assertNotNull(study);
		assertThat(study.getProjectId(), is(this.studyId));
		assertThat(germplasmStudySource.getGermplasmStudySourceType(), is(GermplasmStudySourceType.ADVANCE));
	}

	private void createObservation(final Integer variableId, final Integer observationUnitId, final String value) {
		final ObservationDto observationDto = new ObservationDto();
		observationDto.setVariableId(variableId);
		observationDto.setObservationUnitId(observationUnitId);
		observationDto.setValue(value);
		this.datasetService.createObservation(observationDto);
	}

}
