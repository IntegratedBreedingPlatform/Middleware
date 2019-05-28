package org.generationcp.middleware.service.impl.dataset;

import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import org.apache.commons.lang.RandomStringUtils;
import org.generationcp.middleware.constant.ColumnLabels;
import org.generationcp.middleware.dao.FormulaDAO;
import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.dao.dms.ExperimentDao;
import org.generationcp.middleware.dao.dms.PhenotypeDao;
import org.generationcp.middleware.dao.dms.ProjectPropertyDao;
import org.generationcp.middleware.dao.dms.ProjectRelationshipDao;
import org.generationcp.middleware.data.initializer.MeasurementVariableTestDataInitializer;
import org.generationcp.middleware.domain.dataset.ObservationDto;
import org.generationcp.middleware.domain.dms.DatasetDTO;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.SortedPageRequest;
import org.generationcp.middleware.pojos.derived_variables.Formula;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.service.api.dataset.ObservationUnitData;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.generationcp.middleware.service.api.dataset.ObservationUnitsSearchDTO;
import org.generationcp.middleware.service.api.study.MeasurementDto;
import org.generationcp.middleware.service.api.study.MeasurementVariableDto;
import org.generationcp.middleware.service.api.study.MeasurementVariableService;
import org.generationcp.middleware.service.api.study.StudyService;
import org.generationcp.middleware.service.impl.study.ObservationUnitIDGeneratorImplTest;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

public class DatasetServiceImplTest {

	private static final int STUDY_ID = 1234;
	private static final String FACT1 = "FACT1";
	public static final ArrayList<String> DESING_FACTORS =
		Lists.newArrayList(TermId.REP_NO.name(), TermId.PLOT_NO.name(), DatasetServiceImplTest.FACT1);
	private static final String STOCK_ID = "STOCK_ID";
	public static final ArrayList<String> GERMPLASM_DESCRIPTORS = Lists.newArrayList(
		TermId.GID.name(), ColumnLabels.DESIGNATION.name(), TermId.ENTRY_NO.name(),
		TermId.ENTRY_TYPE.name(), TermId.ENTRY_CODE.name(), TermId.OBS_UNIT_ID.name(), DatasetServiceImplTest.STOCK_ID);
	private static final int DATASET_ID = 567;
	private static final int INSTANCE_ID = 30;
	private static final String PROGRAM_UUID = RandomStringUtils.randomAlphabetic(20);
	private static final String ND_EXPERIMENT_ID = "ndExperimentId";
	public static final String OBS_UNIT_ID = "OBS_UNIT_ID";
	public static final String ENTRY_CODE = "ENTRY_CODE";
	public static final String ENTRY_NO = "ENTRY_NO";
	public static final String DESIGNATION = "DESIGNATION";
	public static final String GID = "GID";
	public static final String ENTRY_TYPE = "ENTRY_TYPE";
	public static final String TRIAL_INSTANCE = "TRIAL_INSTANCE";
	public static final String FIELD_MAP_COLUMN = "FieldMapColumn";
	public static final String FIELD_MAP_RANGE = "FIELD_MAP_RANGE";
	public static final String COL = "COL";
	public static final String ROW = "ROW";
	public static final String BLOCK_NO = "BLOCK_NO";
	public static final String PLOT_NO = "PLOT_NO";
	public static final String REP_NO = "REP_NO";
	public static final String EXPECTED = "5";

	@Mock
	private DaoFactory daoFactory;

	@Mock
	private HibernateSessionProvider mockSessionProvider;

	@Mock
	private Session mockSession;

	@Mock
	private PhenotypeDao phenotypeDao;

	@Mock
	private DmsProjectDao dmsProjectDao;

	@Mock
	private ExperimentDao experimentDao;

	@Mock
	private MeasurementVariableService measurementVariableService;

	@Mock
	private ProjectRelationshipDao projectRelationshipDao;

	@Mock
	private StudyService studyService;

	@Mock
	private ProjectPropertyDao projectPropertyDao;

	@Mock
	private FormulaDAO formulaDao;

	@Mock
	private WorkbenchDataManager workbenchDataManager;

	@InjectMocks
	private DatasetServiceImpl datasetService;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		this.datasetService.setDaoFactory(this.daoFactory);
		this.datasetService.setStudyService(this.studyService);
		this.datasetService.setWorkbenchDataManager(this.workbenchDataManager);
		when(this.daoFactory.getPhenotypeDAO()).thenReturn(this.phenotypeDao);
		when(this.daoFactory.getDmsProjectDAO()).thenReturn(this.dmsProjectDao);
		when(this.daoFactory.getProjectPropertyDAO()).thenReturn(this.projectPropertyDao);
		when(this.daoFactory.getExperimentDao()).thenReturn(this.experimentDao);
		when(this.daoFactory.getFormulaDAO()).thenReturn(this.formulaDao);
		when(this.daoFactory.getProjectRelationshipDao()).thenReturn(this.projectRelationshipDao);
	}

	@Test
	public void testCountPhenotypes() {
		final long count = 5;
		when(this.phenotypeDao.countPhenotypesForDataset(Matchers.anyInt(), Matchers.anyListOf(Integer.class))).thenReturn(count);
		Assert.assertEquals(count, this.datasetService.countObservationsByVariables(123, Arrays.asList(11, 22)));
	}

	@Test
	public void testAddVariable() {
		final Random ran = new Random();
		final Integer datasetId = ran.nextInt();
		final Integer nextRank = ran.nextInt();
		Mockito.doReturn(nextRank).when(this.projectPropertyDao).getNextRank(datasetId);
		final Integer traitId = ran.nextInt();
		final String alias = RandomStringUtils.randomAlphabetic(20);

		this.datasetService.addDatasetVariable(datasetId, traitId, VariableType.TRAIT, alias);
		final ArgumentCaptor<ProjectProperty> projectPropertyCaptor = ArgumentCaptor.forClass(ProjectProperty.class);
		Mockito.verify(this.projectPropertyDao).save(projectPropertyCaptor.capture());
		final ProjectProperty datasetVariable = projectPropertyCaptor.getValue();
		Assert.assertEquals(datasetId, datasetVariable.getProject().getProjectId());
		Assert.assertEquals(VariableType.TRAIT.getId(), datasetVariable.getTypeId());
		Assert.assertEquals(nextRank, datasetVariable.getRank());
		Assert.assertEquals(traitId, datasetVariable.getVariableId());
		Assert.assertEquals(alias, datasetVariable.getAlias());
	}

	@Test
	public void testRemoveVariables() {
		final Random ran = new Random();
		final int datasetId = ran.nextInt();
		final List<Integer> variableIds = Arrays.asList(ran.nextInt(), ran.nextInt());
		this.datasetService.removeDatasetVariables(datasetId, variableIds);
		Mockito.verify(this.phenotypeDao).deletePhenotypesByProjectIdAndVariableIds(datasetId, variableIds);
		Mockito.verify(this.projectPropertyDao).deleteProjectVariables(datasetId, variableIds);
	}

	@Test
	public void testIsValidObservationUnit() {
		final Random ran = new Random();
		final int datasetId = ran.nextInt();
		final int observationUnitId = ran.nextInt();
		this.datasetService.isValidObservationUnit(datasetId, observationUnitId);
		Mockito.verify(this.experimentDao).isValidExperiment(datasetId, observationUnitId);
	}

	@Test
	public void testAddPhenotype() {
		final Random ran = new Random();

		final Phenotype savedPhenotype = new Phenotype();
		savedPhenotype.setPhenotypeId(ran.nextInt());
		savedPhenotype.setCreatedDate(new Date("01/01/2018 12:59:59"));
		savedPhenotype.setUpdatedDate(new Date("02/02/2018 11:59:59"));

		final ObservationDto observationDto = new ObservationDto();

		observationDto.setCategoricalValueId(ran.nextInt());
		observationDto.setVariableId(ran.nextInt());
		observationDto.setValue(ran.toString());
		observationDto.setObservationUnitId(ran.nextInt());

		when(this.formulaDao.getByTargetVariableId(observationDto.getVariableId())).thenReturn(new Formula());
		when(this.phenotypeDao.save(Mockito.any(Phenotype.class))).thenReturn(savedPhenotype);

		final ObservationDto savedObservation = this.datasetService.createObservation(observationDto);

		final ArgumentCaptor<Phenotype> captor = ArgumentCaptor.forClass(Phenotype.class);
		Mockito.verify(this.phenotypeDao).save(captor.capture());

		final Phenotype phenotypeToBeSaved = captor.getValue();

		Assert.assertEquals(phenotypeToBeSaved.getcValueId(), observationDto.getCategoricalValueId());
		Assert.assertEquals(phenotypeToBeSaved.getObservableId(), observationDto.getVariableId());
		Assert.assertEquals(phenotypeToBeSaved.getValue(), observationDto.getValue());
		Assert.assertEquals(phenotypeToBeSaved.getExperiment().getNdExperimentId(), observationDto.getObservationUnitId());
		Assert.assertEquals(Phenotype.ValueStatus.MANUALLY_EDITED, phenotypeToBeSaved.getValueStatus());
		Assert.assertEquals(savedObservation.getObservationId(), savedPhenotype.getPhenotypeId());
		Assert.assertEquals(phenotypeToBeSaved.getName(), observationDto.getVariableId().toString());

		final SimpleDateFormat dateFormat = new SimpleDateFormat(DatasetServiceImpl.DATE_FORMAT);
		Assert.assertEquals(savedObservation.getCreatedDate(), dateFormat.format(savedPhenotype.getCreatedDate()));
		Assert.assertEquals(savedObservation.getUpdatedDate(), dateFormat.format(savedPhenotype.getUpdatedDate()));

	}

	@Test
	public void testUpdatePhenotype() {
		final Random ran = new Random();

		final Integer observationUnitId = ran.nextInt();
		final Integer observationId = ran.nextInt();
		final Integer categoricalValueId = ran.nextInt();
		final Integer observableId = ran.nextInt();
		final String value = ran.toString();
		final ObservationDto observationDto = new ObservationDto();
		observationDto.setObservationId(observationId);
		observationDto.setObservationUnitId(observationUnitId);
		observationDto.setValue(value);
		observationDto.setCategoricalValueId(categoricalValueId);

		final Phenotype existingPhenotype = new Phenotype();
		existingPhenotype.setPhenotypeId(observationId);
		existingPhenotype.setCreatedDate(new Date("01/01/2018 12:59:59"));
		existingPhenotype.setUpdatedDate(new Date("02/02/2018 11:59:59"));
		existingPhenotype.setExperiment(new ExperimentModel(observationUnitId));
		existingPhenotype.setObservableId(observableId);

		when(this.formulaDao.getByTargetVariableId(observableId)).thenReturn(new Formula());
		when(this.phenotypeDao.getById(observationId)).thenReturn(existingPhenotype);

		final ObservationDto savedObservation =
			this.datasetService.updatePhenotype(observationId, observationDto);

		Mockito.verify(this.phenotypeDao).update(existingPhenotype);

		final SimpleDateFormat dateFormat = new SimpleDateFormat(DatasetServiceImpl.DATE_FORMAT);

		Assert.assertEquals(value, existingPhenotype.getValue());
		Assert.assertEquals(categoricalValueId, existingPhenotype.getcValueId());
		Assert.assertEquals(savedObservation.getObservationId(), existingPhenotype.getPhenotypeId());
		Assert.assertEquals(savedObservation.getCategoricalValueId(), existingPhenotype.getcValueId());
		Assert.assertEquals(savedObservation.getStatus(), existingPhenotype.getValueStatus().getName());
		Assert.assertEquals(savedObservation.getValue(), existingPhenotype.getValue());
		Assert.assertEquals(savedObservation.getUpdatedDate(), dateFormat.format(existingPhenotype.getUpdatedDate()));
		Assert.assertEquals(savedObservation.getCreatedDate(), dateFormat.format(existingPhenotype.getCreatedDate()));
		Assert.assertEquals(savedObservation.getObservationUnitId(), existingPhenotype.getExperiment().getNdExperimentId());

	}

	@Test
	public void testResolveObservationStatusVaribleHasFormula() {
		final Random ran = new Random();
		final int variableId = ran.nextInt();
		when(this.formulaDao.getByTargetVariableId(variableId)).thenReturn(new Formula());

		final Phenotype phenotype = new Phenotype();
		this.datasetService.resolveObservationStatus(variableId, phenotype);

		Assert.assertEquals(Phenotype.ValueStatus.MANUALLY_EDITED, phenotype.getValueStatus());
	}

	@Test
	public void testResolveObservationStatusVaribleHasNoFormula() {
		final Random ran = new Random();
		final int variableId = ran.nextInt();

		final Phenotype phenotype = new Phenotype();
		when(this.formulaDao.getByTargetVariableId(variableId)).thenReturn(new Formula());

		Assert.assertNull(phenotype.getValueStatus());
	}

	@Test
	public void testUpdateDependentPhenotypesWhenNotInputVariable() {
		final Random ran = new Random();
		final int variableId = ran.nextInt();
		final int observationUnitId = ran.nextInt();
		Mockito.doReturn(new ArrayList<Formula>()).when(this.formulaDao).getByInputId(variableId);
		this.datasetService.updateDependentPhenotypesStatus(variableId, observationUnitId);
		Mockito.verify(this.phenotypeDao, Mockito.never()).updateOutOfSyncPhenotypes(Matchers.anyInt(), Matchers.anyListOf(Integer.class));
	}

	@Test
	public void testUpdateDependentPhenotypes() {
		final Random ran = new Random();
		final int variableId = ran.nextInt();
		final int observationUnitId = ran.nextInt();
		final Formula formula1 = new Formula();
		final CVTerm term1 = new CVTerm();
		term1.setCvTermId(ran.nextInt());
		formula1.setTargetCVTerm(term1);
		final Formula formula2 = new Formula();
		final CVTerm term2 = new CVTerm();
		term2.setCvTermId(ran.nextInt());
		formula2.setTargetCVTerm(term2);
		Mockito.doReturn(Arrays.asList(formula1, formula2)).when(this.formulaDao).getByInputId(variableId);
		this.datasetService.updateDependentPhenotypesStatus(variableId, observationUnitId);
		Mockito.verify(this.phenotypeDao).updateOutOfSyncPhenotypes(
			observationUnitId,
			Arrays.asList(term1.getCvTermId(), term2.getCvTermId()));
	}

	@Test
	public void testIsValidObservation() {
		final Random ran = new Random();
		final int observationUnitId = ran.nextInt();
		final int observationId = ran.nextInt();
		this.datasetService.getPhenotype(observationUnitId, observationId);
		Mockito.verify(this.phenotypeDao).getPhenotype(observationUnitId, observationId);
	}

	@Test
	public void testCountPhenotypesByInstance() {
		final long count = 6;
		Mockito.when(this.phenotypeDao.countPhenotypesForDatasetAndInstance(Matchers.anyInt(), Matchers.anyInt())).thenReturn(count);
		Assert.assertEquals(count, this.datasetService.countObservationsByInstance(1, 2));
	}

	@Test
	public void testGetDatasets() {
		final List<DatasetDTO> datasetDTOList = this.setUpDatasets(null);
		final List<DatasetDTO> result = this.datasetService.getDatasets(25019, new TreeSet<Integer>());
		assertThat(datasetDTOList, equalTo(result));
	}

	@Test
	public void testGetDataset() {
		final List<DatasetDTO> datasetDTOList = this.setUpDatasets(null);
		Mockito.when(this.datasetService.getDataset(datasetDTOList.get(4).getDatasetId())).thenReturn(datasetDTOList.get(4));
		final DatasetDTO result = this.datasetService.getDataset(datasetDTOList.get(4).getDatasetId());
		assertThat(datasetDTOList.get(4), equalTo(result));
	}

	@Test
	public void testGetDatasetsFilteringByDatasetTypeId() {
		final List<DatasetDTO> datasetDTOList = this.setUpDatasets(DatasetTypeEnum.PLANT_SUBOBSERVATIONS.getId());
		final Set<Integer> datasetTypeIds = new TreeSet<>();
		datasetTypeIds.add(DatasetTypeEnum.PLANT_SUBOBSERVATIONS.getId());
		final List<DatasetDTO> result = this.datasetService.getDatasets(25019, datasetTypeIds);
		assertThat(datasetDTOList, equalTo(result));
	}

	@Test
	public void testaddStudyVariablesToUnitRows() {
		final ObservationUnitRow observationUnitRow = new ObservationUnitRow();
		final Map<String, ObservationUnitData> variables = new HashMap<>();
		variables.put(OBS_UNIT_ID, new ObservationUnitData("obunit123"));
		observationUnitRow.setVariables(variables);
		final MeasurementVariable trialInstanceVariable = MeasurementVariableTestDataInitializer.createMeasurementVariable(TermId.TRIAL_INSTANCE_FACTOR.getId(), TermId.TRIAL_INSTANCE_FACTOR.name(),"1");
		this.datasetService.addStudyVariablesToUnitRows(Arrays.asList(observationUnitRow), Arrays.asList(trialInstanceVariable));
		Assert.assertNotNull(observationUnitRow.getVariables().get(trialInstanceVariable.getName()));
	}

	private  List<DatasetDTO> setUpDatasets(final Integer datasetTypeId){
		final List<DatasetDTO> datasetDTOs1 = new ArrayList<>();
		final List<DatasetDTO> datasetDTOs2 = new ArrayList<>();
		final List<DatasetDTO> datasetDTOs3 = new ArrayList<>();
		final List<DatasetDTO> datasetDTOs4 = new ArrayList<>();
		final List<DatasetDTO> datasetDTOList = new ArrayList<>();
		DatasetDTO datasetDTO;

		final boolean filterDataset = datasetTypeId == null || datasetTypeId == 0 ? false : true;

		datasetDTO = createDataset(25020, 25019, "IBP-2015-ENVIRONMENT", DatasetTypeEnum.SUMMARY_DATA.getId());
		datasetDTOs1.add(datasetDTO);
		if ((filterDataset && datasetTypeId.equals(datasetDTO.getDatasetTypeId()) || !filterDataset)) {
			datasetDTOList.add(datasetDTO);

		}
		datasetDTO = createDataset(25021, 25019, "IBP-2015-PLOTDATA", DatasetTypeEnum.PLOT_DATA.getId());
		datasetDTOs1.add(datasetDTO);
		if ((filterDataset && datasetTypeId.equals(datasetDTO.getDatasetTypeId()) || !filterDataset)) {
			datasetDTOList.add(datasetDTO);

		}

		Mockito.when(this.dmsProjectDao.getDatasets(25019)).thenReturn(datasetDTOs1);
		Mockito.when(this.dmsProjectDao.getDatasets(25020)).thenReturn(new ArrayList<DatasetDTO>());

		datasetDTO = createDataset(25022, 25021, "IBP-2015-PLOTDATA-SUBOBS", DatasetTypeEnum.PLANT_SUBOBSERVATIONS.getId());
		datasetDTOs2.add(datasetDTO);
		if ((filterDataset && datasetTypeId.equals(datasetDTO.getDatasetTypeId()) || !filterDataset)) {
			datasetDTOList.add(datasetDTO);

		}
		Mockito.when(this.dmsProjectDao.getDatasets(25021)).thenReturn(datasetDTOs2);

		datasetDTO = createDataset(25023, 25022, "IBP-2015-PLOTDATA-SUBOBS-SUBOBS", DatasetTypeEnum.PLANT_SUBOBSERVATIONS.getId());
		datasetDTOs3.add(datasetDTO);
		if ((filterDataset && datasetTypeId.equals(datasetDTO.getDatasetTypeId()) || !filterDataset)) {
			datasetDTOList.add(datasetDTO);

		}
		Mockito.when(this.dmsProjectDao.getDatasets(25022)).thenReturn(datasetDTOs3);

		datasetDTO = createDataset(25024, 25023, "IBP-2015-PLOTDATA-SUBOBS-SUBOBS-SUBOBS", DatasetTypeEnum.PLANT_SUBOBSERVATIONS.getId());
		datasetDTOs4.add(datasetDTO);
		if ((filterDataset && datasetTypeId.equals(datasetDTO.getDatasetTypeId()) || !filterDataset)) {
			datasetDTOList.add(datasetDTO);

		}
		Mockito.when(this.dmsProjectDao.getDatasets(25023)).thenReturn(datasetDTOs4);

		return datasetDTOList;
	}

	private static DatasetDTO createDataset(final Integer datasetId, final Integer parentDatasetId, final String name,
		final Integer datasetTypeId) {
		final DatasetDTO datasetDTO = new DatasetDTO();
		datasetDTO.setDatasetId(datasetId);
		datasetDTO.setDatasetTypeId(datasetTypeId);
		datasetDTO.setName(name);
		datasetDTO.setParentDatasetId(parentDatasetId);
		return datasetDTO;

	}

	@Ignore // TODO move to integration tests
	@Test
	public void testGetObservations() throws Exception {
		this.datasetService = new DatasetServiceImpl(this.mockSessionProvider);
		this.datasetService.setMeasurementVariableService(this.measurementVariableService);
		this.datasetService.setStudyService(this.studyService);

		Mockito.when(this.mockSessionProvider.getSession()).thenReturn(this.mockSession);
		Mockito.when(this.studyService.getGenericGermplasmDescriptors(DatasetServiceImplTest.STUDY_ID))
			.thenReturn(GERMPLASM_DESCRIPTORS);
		Mockito.when(this.studyService.getAdditionalDesignFactors(DatasetServiceImplTest.STUDY_ID))
			.thenReturn(DESING_FACTORS);

		final MeasurementVariableService mockTraits = Mockito.mock(MeasurementVariableService.class);
		this.datasetService.setMeasurementVariableService(mockTraits);
		final SQLQuery mockQuery = Mockito.mock(SQLQuery.class);
		final List<MeasurementVariableDto> projectTraits =
			Arrays.<MeasurementVariableDto>asList(new MeasurementVariableDto(1, "Trait1"), new MeasurementVariableDto(1, "Trait2"));
		Mockito.when(mockTraits.getVariables(
			DatasetServiceImplTest.STUDY_ID, VariableType.TRAIT.getId(),
			VariableType.SELECTION_METHOD.getId())).thenReturn(projectTraits);
		final List<MeasurementDto> traits = new ArrayList<MeasurementDto>();
		traits.add(new MeasurementDto(new MeasurementVariableDto(1, "traitName"), 9999, "traitValue", Phenotype.ValueStatus.OUT_OF_SYNC));
		final ObservationUnitRow observationUnitRow = new ObservationUnitRow();
		observationUnitRow.setObservationUnitId(1);
		observationUnitRow.setAction("1");
		observationUnitRow.setGid(2);
		observationUnitRow.setDesignation("ABCD");
		final Map<String, ObservationUnitData> variables = new HashMap<>();
		variables.put(TRIAL_INSTANCE, new ObservationUnitData("10"));
		variables.put(ENTRY_TYPE, new ObservationUnitData("T"));
		variables.put(ENTRY_NO, new ObservationUnitData("10000"));
		variables.put(ENTRY_CODE, new ObservationUnitData("12"));
		variables.put(REP_NO, new ObservationUnitData());
		variables.put(PLOT_NO, new ObservationUnitData());
		variables.put(BLOCK_NO, new ObservationUnitData());
		variables.put(ROW, new ObservationUnitData());
		variables.put(COL, new ObservationUnitData());
		variables.put(OBS_UNIT_ID, new ObservationUnitData("obunit123"));
		variables.put(FIELD_MAP_COLUMN, new ObservationUnitData());
		variables.put(FIELD_MAP_RANGE, new ObservationUnitData());
		variables.put(STOCK_ID, new ObservationUnitData());
		variables.put(FACT1, new ObservationUnitData());
		observationUnitRow.setVariables(variables);

		final List<ObservationUnitRow> testMeasurements = Collections.<ObservationUnitRow>singletonList(observationUnitRow);
		Mockito.when(this.experimentDao.getObservationVariableName(DATASET_ID)).thenReturn("PLANT_NO");
		final ObservationUnitsSearchDTO
			searchDTO = new ObservationUnitsSearchDTO(DATASET_ID, INSTANCE_ID, GERMPLASM_DESCRIPTORS, DESING_FACTORS, projectTraits);
		searchDTO.setSortedRequest(new SortedPageRequest(1, 100, null, null));
		Mockito.when(this.experimentDao.getObservationUnitTable(searchDTO)).thenReturn(testMeasurements);

		Mockito.when(this.mockSession.createSQLQuery(Mockito.anyString())).thenReturn(mockQuery);
		final List<Map<String, Object>> results = new ArrayList<>();
		final Map<String, Object> map = new HashMap<>();
		map.put(ND_EXPERIMENT_ID, 1);
		map.put(GID, 2);
		map.put(DESIGNATION, "ABCD");
		map.put(TRIAL_INSTANCE, "10");
		map.put(ENTRY_TYPE, "T");
		map.put(ENTRY_NO, "10000");
		map.put(ENTRY_CODE, "12");
		map.put(OBS_UNIT_ID, "obunit123");
		results.add(map);
		Mockito.when(mockQuery.list()).thenReturn(results);

		// Method to test
		final List<ObservationUnitRow> actualMeasurements = this.datasetService.getObservationUnitRows(DatasetServiceImplTest.STUDY_ID,
			DatasetServiceImplTest.DATASET_ID,
			new ObservationUnitsSearchDTO());

		Assert.assertEquals(testMeasurements, actualMeasurements);
	}

	@Test
	public void testDeletePhenotype() {
		final Random random = new Random();
		final Integer observableId = random.nextInt();
		final Integer observationUnitId = random.nextInt();
		final Integer phenotypeId = random.nextInt();
		final Phenotype phenotype = new Phenotype();
		phenotype.setPhenotypeId(phenotypeId);
		phenotype.setObservableId(observableId);
		final ExperimentModel experiment = new ExperimentModel(observationUnitId);
		phenotype.setExperiment(experiment);
		experiment.setPhenotypes(Lists.newArrayList(phenotype));
		when(this.phenotypeDao.getById(phenotypeId)).thenReturn(phenotype);

		final Formula formula1 = new Formula();
		final CVTerm term1 = new CVTerm();
		term1.setCvTermId(random.nextInt());
		formula1.setTargetCVTerm(term1);
		final Formula formula2 = new Formula();
		final CVTerm term2 = new CVTerm();
		term2.setCvTermId(random.nextInt());
		formula2.setTargetCVTerm(term2);
		Mockito.doReturn(Arrays.asList(formula1, formula2)).when(this.formulaDao).getByInputId(observableId);


		this.datasetService.deletePhenotype(phenotypeId);
		Mockito.verify(this.phenotypeDao).makeTransient(phenotype);
		Mockito.verify(this.phenotypeDao).updateOutOfSyncPhenotypes(observationUnitId, Arrays.asList(term1.getCvTermId(), term2.getCvTermId()));
	}

    @Test
    public void testGetDatasetInstances() {
        final Random random = new Random();
        final int datasetId = random.nextInt();
        this.datasetService.getDatasetInstances(datasetId);
        Mockito.verify(this.dmsProjectDao).getDatasetInstances(datasetId);
    }

    @Test
	public void countObservationsGroupedByInstance_Verified_ExperimentCountObservationsPerInstance(){
		final Random random = new Random();
		final int datasetId = random.nextInt();
		this.datasetService.countObservationsGroupedByInstance(datasetId);
		Mockito.verify(this.experimentDao).countObservationsPerInstance(datasetId);
	}

	@Test
	public void getAllObservationUnitRows_Verified_DAOInteractions(){
		final Random random = new Random();
		final int datasetId = random.nextInt();
		final int studyId = random.nextInt();
		final DmsProject dmsProject = new DmsProject();
		dmsProject.setProjectId(datasetId);

		Mockito.doReturn(new ArrayList<>()).when(this.studyService).getGenericGermplasmDescriptors(studyId);
		Mockito.doReturn(new ArrayList<>()).when(this.studyService).getAdditionalDesignFactors(studyId);
		Mockito.doReturn(Arrays.asList(dmsProject)).when(this.dmsProjectDao).getDatasetsByTypeForStudy(studyId, DatasetTypeEnum.SUMMARY_DATA.getId());

		this.datasetService.getAllObservationUnitRows(studyId, datasetId);
		Mockito.verify(this.dmsProjectDao).getDatasetsByTypeForStudy(studyId, DatasetTypeEnum.SUMMARY_DATA.getId());
		Mockito.verify(this.dmsProjectDao).getObservationSetVariables(studyId,Lists.newArrayList(VariableType.STUDY_DETAIL.getId()));
		Mockito.verify(this.experimentDao).getObservationUnitTable(Mockito.any(ObservationUnitsSearchDTO.class));

	}

	@Test
	public void testAcceptDraftData() throws Exception {
		final Integer datasetId = 3;

		final DmsProject project = new DmsProject();
		project.setProjectId(datasetId);

		final Integer observationUnitId = 333;
		final ExperimentModel experimentModel = new ExperimentModel(observationUnitId);
		experimentModel.setProject(project);

		final Phenotype phenotype = new Phenotype();
		phenotype.setCreatedDate(new Date());
		phenotype.setUpdatedDate(new Date());
		phenotype.setcValue(123);
		final Integer variableId = 12;
		phenotype.setObservableId(variableId);
		phenotype.setValue("55");
		phenotype.setDraftValue("8");
		phenotype.setExperiment(experimentModel);
		phenotype.setName(String.valueOf(variableId));

		experimentModel.setPhenotypes(Lists.newArrayList(phenotype));

		final List<Phenotype> phenotypes = Lists.newArrayList(phenotype);

		Mockito.when(this.phenotypeDao.getDatasetDraftData(datasetId)).thenReturn(phenotypes);
		this.datasetService.acceptAllDatasetDraftData(datasetId);

		final ArgumentCaptor<Phenotype> phenotypeArgumentCaptor = ArgumentCaptor.forClass(Phenotype.class);
		Mockito.verify(this.phenotypeDao).update(phenotypeArgumentCaptor.capture());
		final Phenotype phenotypeArgumentCaptorValue = phenotypeArgumentCaptor.getValue();
		Assert.assertEquals(phenotype.getValue(), phenotypeArgumentCaptorValue.getValue());
		Assert.assertNull(phenotypeArgumentCaptorValue.getDraftValue());
	}

	@Test
	public void testDiscardDraftData() throws Exception {
		final Integer datasetId = 3;

		final DmsProject project = new DmsProject();
		project.setProjectId(datasetId);

		final Integer observationUnitId = 333;
		final ExperimentModel experimentModel = new ExperimentModel(observationUnitId);
		experimentModel.setProject(project);

		final Phenotype phenotype = new Phenotype();
		phenotype.setCreatedDate(new Date());
		phenotype.setUpdatedDate(new Date());
		phenotype.setcValue(123);
		final Integer variableId = 12;
		phenotype.setObservableId(variableId);
		phenotype.setValue("55");
		phenotype.setDraftValue("8");
		phenotype.setExperiment(experimentModel);
		phenotype.setName(String.valueOf(variableId));
		phenotype.setPhenotypeId(12345);

		experimentModel.setPhenotypes(Lists.newArrayList(phenotype));

		final List<Phenotype> phenotypes = Lists.newArrayList(phenotype);

		Mockito.when(this.phenotypeDao.getDatasetDraftData(datasetId)).thenReturn(phenotypes);
		Mockito.when(this.phenotypeDao.getById(phenotype.getPhenotypeId())).thenReturn(phenotype);
		this.datasetService.rejectDatasetDraftData(datasetId);

		final ArgumentCaptor<Phenotype> phenotypeArgumentCaptor = ArgumentCaptor.forClass(Phenotype.class);
		Mockito.verify(this.phenotypeDao).update(phenotypeArgumentCaptor.capture());
		final Phenotype phenotypeArgumentCaptorValue = phenotypeArgumentCaptor.getValue();
		Assert.assertEquals(phenotype.getValue(), phenotypeArgumentCaptorValue.getValue());
		Assert.assertNull(phenotypeArgumentCaptorValue.getDraftValue());
	}

	@Test
	public void testAcceptDraftDataDeletingRow() throws Exception {
		final Integer datasetId = 3;

		final DmsProject project = new DmsProject();
		project.setProjectId(datasetId);

		final Integer observationUnitId = 333;
		final ExperimentModel experimentModel = new ExperimentModel(observationUnitId);
		experimentModel.setProject(project);

		final Phenotype phenotype = new Phenotype();
		final Integer phenotypeId = 999;
		phenotype.setPhenotypeId(phenotypeId);
		phenotype.setCreatedDate(new Date());
		phenotype.setUpdatedDate(new Date());
		phenotype.setcValue(123);
		final Integer variableId = 12;
		phenotype.setObservableId(variableId);
		phenotype.setValue("55");
		phenotype.setDraftValue(null);
		phenotype.setExperiment(experimentModel);
		phenotype.setName(String.valueOf(variableId));

		experimentModel.setPhenotypes(Lists.newArrayList(phenotype));

		final List<Phenotype> phenotypes = Lists.newArrayList(phenotype);

		Mockito.when(this.phenotypeDao.getDatasetDraftData(datasetId)).thenReturn(phenotypes);
		Mockito.when(this.daoFactory.getPhenotypeDAO().getById(phenotypeId)).thenReturn(phenotype);
		this.datasetService.acceptAllDatasetDraftData(datasetId);

		final ArgumentCaptor<Phenotype> phenotypeArgumentCaptor = ArgumentCaptor.forClass(Phenotype.class);
		Mockito.verify(this.phenotypeDao).makeTransient(phenotypeArgumentCaptor.capture());
	}

	@Test
	public void testDiscardDraftDataDeletingRow() throws Exception {
		final Integer datasetId = 3;

		final DmsProject project = new DmsProject();
		project.setProjectId(datasetId);

		final Integer observationUnitId = 333;
		final ExperimentModel experimentModel = new ExperimentModel(observationUnitId);
		experimentModel.setProject(project);

		final Phenotype phenotype = new Phenotype();
		phenotype.setCreatedDate(new Date());
		phenotype.setUpdatedDate(new Date());
		phenotype.setcValue(null);
		final Integer variableId = 12;
		phenotype.setObservableId(variableId);
		phenotype.setValue(null);
		phenotype.setDraftValue("8");
		phenotype.setExperiment(experimentModel);
		phenotype.setName(String.valueOf(variableId));
		phenotype.setPhenotypeId(12345);

		experimentModel.setPhenotypes(Lists.newArrayList(phenotype));

		final List<Phenotype> phenotypes = Lists.newArrayList(phenotype);

		Mockito.when(this.phenotypeDao.getDatasetDraftData(datasetId)).thenReturn(phenotypes);
		Mockito.when(this.phenotypeDao.getById(phenotype.getPhenotypeId())).thenReturn(phenotype);
		this.datasetService.rejectDatasetDraftData(datasetId);

		final ArgumentCaptor<Phenotype> phenotypeArgumentCaptor = ArgumentCaptor.forClass(Phenotype.class);
		Mockito.verify(this.phenotypeDao).makeTransient(phenotypeArgumentCaptor.capture());
	}

	@Test
	public void testDiscardDraftDataDeletingRowWithEmpty() throws Exception {
		final Integer datasetId = 3;

		final DmsProject project = new DmsProject();
		project.setProjectId(datasetId);

		final Integer observationUnitId = 333;
		final ExperimentModel experimentModel = new ExperimentModel(observationUnitId);
		experimentModel.setProject(project);

		final Phenotype phenotype = new Phenotype();
		phenotype.setCreatedDate(new Date());
		phenotype.setUpdatedDate(new Date());
		phenotype.setcValue(null);
		final Integer variableId = 12;
		phenotype.setObservableId(variableId);
		phenotype.setValue("");
		phenotype.setDraftValue("8");
		phenotype.setExperiment(experimentModel);
		phenotype.setName(String.valueOf(variableId));
		phenotype.setPhenotypeId(12345);

		experimentModel.setPhenotypes(Lists.newArrayList(phenotype));

		final List<Phenotype> phenotypes = Lists.newArrayList(phenotype);

		Mockito.when(this.phenotypeDao.getDatasetDraftData(datasetId)).thenReturn(phenotypes);
		Mockito.when(this.phenotypeDao.getById(phenotype.getPhenotypeId())).thenReturn(phenotype);
		this.datasetService.rejectDatasetDraftData(datasetId);

		final ArgumentCaptor<Phenotype> phenotypeArgumentCaptor = ArgumentCaptor.forClass(Phenotype.class);
		Mockito.verify(this.phenotypeDao).makeTransient(phenotypeArgumentCaptor.capture());
	}

	@Test
	public void testAcceptDraftDataDeletingRowWithEmpty() throws Exception {
		final Integer datasetId = 3;

		final DmsProject project = new DmsProject();
		project.setProjectId(datasetId);

		final Integer observationUnitId = 333;
		final ExperimentModel experimentModel = new ExperimentModel(observationUnitId);
		experimentModel.setProject(project);

		final Phenotype phenotype = new Phenotype();
		final Integer phenotypeId = 999;
		phenotype.setPhenotypeId(phenotypeId);
		phenotype.setCreatedDate(new Date());
		phenotype.setUpdatedDate(new Date());
		phenotype.setcValue(123);
		final Integer variableId = 12;
		phenotype.setObservableId(variableId);
		phenotype.setValue("55");
		phenotype.setDraftValue("");
		phenotype.setExperiment(experimentModel);
		phenotype.setName(String.valueOf(variableId));

		experimentModel.setPhenotypes(Lists.newArrayList(phenotype));

		final List<Phenotype> phenotypes = Lists.newArrayList(phenotype);

		Mockito.when(this.phenotypeDao.getDatasetDraftData(datasetId)).thenReturn(phenotypes);
		Mockito.when(this.daoFactory.getPhenotypeDAO().getById(phenotypeId)).thenReturn(phenotype);
		this.datasetService.acceptAllDatasetDraftData(datasetId);

		final ArgumentCaptor<Phenotype> phenotypeArgumentCaptor = ArgumentCaptor.forClass(Phenotype.class);
		Mockito.verify(this.phenotypeDao).makeTransient(phenotypeArgumentCaptor.capture());
	}

	@Test
	public void testSetAsMissingDraftDataValidValue() throws Exception {
		final Integer datasetId = 3;

		final DmsProject project = new DmsProject();
		project.setProjectId(datasetId);

		final Integer observationUnitId = 333;
		final ExperimentModel experimentModel = new ExperimentModel(observationUnitId);
		experimentModel.setProject(project);

		final Phenotype phenotype = new Phenotype();
		phenotype.setCreatedDate(new Date());
		phenotype.setUpdatedDate(new Date());
		phenotype.setcValue(1);
		final Integer variableId = 12;
		phenotype.setObservableId(variableId);
		phenotype.setValue("1");
		phenotype.setDraftValue(EXPECTED);
		phenotype.setExperiment(experimentModel);
		phenotype.setName(String.valueOf(variableId));

		experimentModel.setPhenotypes(Lists.newArrayList(phenotype));

		final List<Phenotype> phenotypes = Lists.newArrayList(phenotype);
		final MeasurementVariable variable = new MeasurementVariable();
		variable.setTermId(12);
		variable.setDataTypeId(DataType.CATEGORICAL_VARIABLE.getId());
		final ValueReference valueReference = new ValueReference();
		valueReference.setKey(EXPECTED);
		valueReference.setName(EXPECTED);
		valueReference.setId(5);
		valueReference.setId(5);
		variable.setPossibleValues(Lists.<ValueReference>newArrayList(valueReference));
		final List<MeasurementVariable> variables = Lists.newArrayList(variable);


		Mockito.when(this.phenotypeDao.getDatasetDraftData(datasetId)).thenReturn(phenotypes);
		Mockito.when(this.phenotypeDao.getPhenotypes(datasetId)).thenReturn(phenotypes);
		Mockito.when(this.phenotypeDao.getById(phenotype.getPhenotypeId())).thenReturn(phenotype);
		Mockito.when(this.dmsProjectDao.getObservationSetVariables(datasetId, DatasetServiceImpl.MEASUREMENT_VARIABLE_TYPES)).thenReturn(variables);
		this.datasetService.acceptDraftDataAndSetOutOfBoundsToMissing(datasetId);

		final ArgumentCaptor<Phenotype> phenotypeArgumentCaptor = ArgumentCaptor.forClass(Phenotype.class);
		Mockito.verify(this.phenotypeDao).update(phenotypeArgumentCaptor.capture());
		final Phenotype phenotypeArgumentCaptorValue = phenotypeArgumentCaptor.getValue();
		Assert.assertEquals(EXPECTED, phenotypeArgumentCaptorValue.getValue());
		Assert.assertNull(phenotypeArgumentCaptorValue.getDraftValue());
	}

	@Test
	public void testSetAsMissingDraftDataInvalidValue() throws Exception {
		final Integer datasetId = 3;

		final DmsProject project = new DmsProject();
		project.setProjectId(datasetId);

		final Integer observationUnitId = 333;
		final ExperimentModel experimentModel = new ExperimentModel(observationUnitId);
		experimentModel.setProject(project);

		final Phenotype phenotype = new Phenotype();
		phenotype.setCreatedDate(new Date());
		phenotype.setUpdatedDate(new Date());
		phenotype.setcValue(1);
		final Integer variableId = 12;
		phenotype.setObservableId(variableId);
		phenotype.setValue("10");
		phenotype.setDraftValue("8");
		phenotype.setExperiment(experimentModel);
		phenotype.setName(String.valueOf(variableId));

		experimentModel.setPhenotypes(Lists.newArrayList(phenotype));

		final List<Phenotype> phenotypes = Lists.newArrayList(phenotype);
		final MeasurementVariable variable = new MeasurementVariable();
		variable.setTermId(12);
		variable.setDataTypeId(DataType.CATEGORICAL_VARIABLE.getId());
		final ValueReference valueReference = new ValueReference();
		valueReference.setKey("50");
		valueReference.setName("50");
		valueReference.setId(50);
		valueReference.setId(50);
		variable.setPossibleValues(Lists.<ValueReference>newArrayList(valueReference));
		final List<MeasurementVariable> variables = Lists.newArrayList(variable);


		Mockito.when(this.phenotypeDao.getDatasetDraftData(datasetId)).thenReturn(phenotypes);
		Mockito.when(this.phenotypeDao.getPhenotypes(datasetId)).thenReturn(phenotypes);
		Mockito.when(this.phenotypeDao.getById(phenotype.getPhenotypeId())).thenReturn(phenotype);
		Mockito.when(this.dmsProjectDao.getObservationSetVariables(datasetId, DatasetServiceImpl.MEASUREMENT_VARIABLE_TYPES)).thenReturn(variables);
		this.datasetService.acceptDraftDataAndSetOutOfBoundsToMissing(datasetId);

		final ArgumentCaptor<Phenotype> phenotypeArgumentCaptor = ArgumentCaptor.forClass(Phenotype.class);
		Mockito.verify(this.phenotypeDao).update(phenotypeArgumentCaptor.capture());
		final Phenotype phenotypeArgumentCaptorValue = phenotypeArgumentCaptor.getValue();
		Assert.assertEquals(Phenotype.MISSING, phenotypeArgumentCaptorValue.getValue());
		Assert.assertNull(phenotypeArgumentCaptorValue.getDraftValue());
	}

	@Test
	public void testSaveSubObservationUnits() {
		final DmsProject study = new DmsProject();
		study.setProjectId(DatasetServiceImplTest.STUDY_ID);
		study.setProgramUUID(DatasetServiceImplTest.PROGRAM_UUID);
		Mockito.doReturn(study).when(this.dmsProjectDao).getById(DatasetServiceImplTest.STUDY_ID);
		final CropType crop = new CropType();
		crop.setCropName("maize");
		crop.setUseUUID(false);
		final Project project = new Project();
		project.setCropType(crop);
		Mockito.doReturn(project).when(this.workbenchDataManager).getProjectByUuid(DatasetServiceImplTest.PROGRAM_UUID);

		final int plotCount = 3;
		final List<ExperimentModel> plotExperiments = this.getPlotExperiments(plotCount);
		final int plotDatasetId = new Random().nextInt();
		final List<Integer> instanceIds = Arrays.asList(11, 12, 13);
		Mockito.doReturn(plotExperiments).when(this.experimentDao).getObservationUnits(plotDatasetId, instanceIds);

		final DmsProject plotDataset = new DmsProject();
		plotDataset.setProjectId(plotDatasetId);
		final DmsProject subobsDataset = new DmsProject();
		final int subObsDatasetId = new Random().nextInt();
		subobsDataset.setProjectId(subObsDatasetId);
		final Integer numberOfSubObsUnits = 5;
		this.datasetService
			.saveSubObservationUnits(DatasetServiceImplTest.STUDY_ID, instanceIds, numberOfSubObsUnits, plotDataset, subobsDataset);
		final ArgumentCaptor<ExperimentModel> experimentCaptor = ArgumentCaptor.forClass(ExperimentModel.class);
		Mockito.verify(this.experimentDao, Mockito.times(plotCount * numberOfSubObsUnits)).save(experimentCaptor.capture());
		final List<ExperimentModel> subObsExperiments = experimentCaptor.getAllValues();
		for (int i=0; i<plotCount; i++){
			final ExperimentModel plotExperiment = plotExperiments.get(i);
			final List<ExperimentModel> plotSubObsUnits = subObsExperiments.subList(i * numberOfSubObsUnits, (i + 1) * numberOfSubObsUnits);
			for (final ExperimentModel subObsUnit : plotSubObsUnits) {
				Assert.assertEquals(plotExperiment, subObsUnit.getParent());
				Assert.assertEquals(plotExperiment.getGeoLocation(), subObsUnit.getGeoLocation());
				Assert.assertEquals(plotExperiment.getStock(), subObsUnit.getStock());
				Assert.assertEquals(plotExperiment.getTypeId(), subObsUnit.getTypeId());
				Assert.assertNotNull(subObsUnit.getObsUnitId());
				Assert.assertFalse(subObsUnit.getObsUnitId().matches(ObservationUnitIDGeneratorImplTest.UUID_REGEX));
			}
		}
	}
	@Test
	public void testFindAdditionalEnvironmentFactors() {
		final Random random = new Random();
		final Integer datasetId = random.nextInt(10);

		final MeasurementVariable measurementVariable = new MeasurementVariable();
		final int termId = 100;
		final String variableName = "VariableName";
		measurementVariable.setTermId(termId);
		measurementVariable.setVariableType(VariableType.ENVIRONMENT_DETAIL);
		measurementVariable.setName(variableName);

		final MeasurementVariable measurementVariable1 = new MeasurementVariable();
		measurementVariable1.setTermId(TermId.LOCATION_ID.getId());
		measurementVariable1.setVariableType(VariableType.ENVIRONMENT_DETAIL);
		measurementVariable1.setName("LOCATION_ID");

		final MeasurementVariable measurementVariable2 = new MeasurementVariable();
		measurementVariable2.setTermId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		measurementVariable2.setVariableType(VariableType.ENVIRONMENT_DETAIL);
		measurementVariable2.setName("TRIAL_INSTANCE");

		final MeasurementVariable measurementVariable3 = new MeasurementVariable();
		measurementVariable3.setTermId(TermId.EXPERIMENT_DESIGN_FACTOR.getId());
		measurementVariable3.setVariableType(VariableType.ENVIRONMENT_DETAIL);
		measurementVariable3.setName("EXPERIMENT_DESIGN_FACTOR");

		final List<MeasurementVariable> observationSetVariables = Arrays.asList(measurementVariable, measurementVariable1,
			measurementVariable2, measurementVariable3);

		when(this.dmsProjectDao.getObservationSetVariables(datasetId, Lists.newArrayList(
			VariableType.ENVIRONMENT_DETAIL.getId()))).thenReturn(observationSetVariables);

		final List<String> result = this.datasetService.findAdditionalEnvironmentFactors(datasetId);

		// Only 1 variable is expected to be returned. Standard Environment Variables
		// (TRIAL_INSTANCE, LOCATION_ID and EXPERIMENT_DESIGN should be ignored.
		Assert.assertEquals(1, result.size());
		Assert.assertTrue(result.contains(measurementVariable.getName()));
	}


	@Test
	public void testGetEnvironmentConditionVariableNames() {
		final Random random = new Random();
		final Integer datasetId = random.nextInt(10);

		final MeasurementVariable measurementVariable = new MeasurementVariable();
		final int termId = 100;
		final String variableName = "VariableName";
		measurementVariable.setTermId(termId);
		measurementVariable.setVariableType(VariableType.STUDY_CONDITION);
		measurementVariable.setName(variableName);

		final List<MeasurementVariable> observationSetVariables = Arrays.asList(measurementVariable);

		when(this.dmsProjectDao.getObservationSetVariables(datasetId, Lists.newArrayList(
			VariableType.STUDY_CONDITION.getId()))).thenReturn(observationSetVariables);

		final List<String> result = this.datasetService.getEnvironmentConditionVariableNames(datasetId);

		Assert.assertEquals(1, result.size());
		Assert.assertTrue(result.contains(measurementVariable.getName()));
	}

	@Test
	@Ignore // TODO IBP-2695
	public void testImportDataset() {
		final Boolean draftMode = null;
		final Integer datasetId = null;
		final Table<String, String, String> table = null;

		this.datasetService.importDataset(datasetId, table, draftMode);
	}

	private  List<ExperimentModel> getPlotExperiments(final Integer count) {
		final List<ExperimentModel> plotUnits = new ArrayList<>();
		for (int i=0; i<count; i++) {
			final Random random = new Random();
			final ExperimentModel plot= new ExperimentModel();
			final Geolocation geoLocation = new Geolocation();
			geoLocation.setLocationId(random.nextInt());
			plot.setGeoLocation(geoLocation);
			final StockModel stock = new StockModel();
			stock.setStockId(random.nextInt());
			plot.setStock(stock);
			plot.setTypeId(random.nextInt());
			plotUnits.add(plot);
		}
		return plotUnits;
	}
}
