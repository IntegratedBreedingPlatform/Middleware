package org.generationcp.middleware.service.impl.dataset;

import com.google.common.collect.Lists;
import org.apache.commons.lang.RandomStringUtils;
import org.generationcp.middleware.constant.ColumnLabels;
import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.dao.dms.ExperimentDao;
import org.generationcp.middleware.dao.dms.PhenotypeDao;
import org.generationcp.middleware.dao.dms.ProjectPropertyDao;
import org.generationcp.middleware.domain.dms.DatasetDTO;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.service.api.dataset.ObservationUnitData;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.generationcp.middleware.service.api.study.MeasurementDto;
import org.generationcp.middleware.service.api.study.MeasurementVariableDto;
import org.generationcp.middleware.service.api.study.MeasurementVariableService;
import org.generationcp.middleware.service.impl.study.DesignFactors;
import org.generationcp.middleware.service.impl.study.GermplasmDescriptors;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class DatasetServiceImplTest {

	@Mock
	private DaoFactory daoFactory;

	@Mock
	private HibernateSessionProvider mockSessionProvider;

	@Mock
	private HibernateSessionProvider session;

	@Mock
	private Session mockSession;

	@Mock
	private PhenotypeDao phenotypeDao;

	@Mock
	private DmsProjectDao dmsProjectDao;

	@Mock
	private ExperimentDao experimentDao;

	@Mock
	private GermplasmDescriptors germplasmDescriptors;

	@Mock
	private DesignFactors designFactors;

	@Mock
	private MeasurementVariableService measurementVariableService;

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
	private static final String ND_EXPERIMENT_ID = "ndExperimentId";
	public static final String OBS_UNIT_ID = "OBS_UNIT_ID";
	public static final String ENTRY_CODE = "ENTRY_CODE";
	public static final String ENTRY_NO = "ENTRY_NO";
	public static final String DESIGNATION = "DESIGNATION";
	public static final String GID = "GID";
	public static final String ENTRY_TYPE = "ENTRY_TYPE";
	public static final String TRIAL_INSTANCE = "TRIAL_INSTANCE";
	public static final String FIELD_MAP_ROW = "FieldMapRow";
	public static final String FIELD_MAP_COLUMN = "FieldMapColumn";
	public static final String FIELD_MAP_RANGE = "FIELD_MAP_RANGE";
	public static final String LOCATION_ABBREVIATION = "LocationAbbreviation";
	public static final String LOCATION_NAME = "LocationName";
		public static final String COL = "COL";
	public static final String ROW = "ROW";
	public static final String BLOCK_NO = "BLOCK_NO";
	public static final String PLOT_NO = "PLOT_NO";
	public static final String REP_NO = "REP_NO";

	private static final String QUERY = "SELECT \n"
		+ "    nde.nd_experiment_id as ndExperimentId,\n"
		+ "    gl.description AS TRIAL_INSTANCE,\n"
		+ "    (SELECT iispcvt.definition FROM stockprop isp INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = isp.type_id INNER JOIN cvterm iispcvt ON iispcvt.cvterm_id = isp.value WHERE isp.stock_id = s.stock_id AND ispcvt.name = 'ENTRY_TYPE') ENTRY_TYPE, \n"
		+ "    s.dbxref_id AS GID,\n"
		+ "    s.name DESIGNATION,\n"
		+ "    s.uniquename ENTRY_NO,\n"
		+ "    s.value as ENTRY_CODE,\n"
		+ "    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = parent.nd_experiment_id AND ispcvt.name = 'REP_NO') REP_NO, \n"
		+ "    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = parent.nd_experiment_id AND ispcvt.name = 'PLOT_NO') PLOT_NO, \n"
		+ "    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = parent.nd_experiment_id AND ispcvt.name = 'BLOCK_NO') BLOCK_NO, \n"
		+ "    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = parent.nd_experiment_id AND ispcvt.name = 'ROW') ROW, \n"
		+ "    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = parent.nd_experiment_id AND ispcvt.name = 'COL') COL, \n"
		+ "    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = parent.nd_experiment_id AND ispcvt.name = 'FIELDMAP COLUMN') 'FIELDMAP COLUMN', \n"
		+ "    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = parent.nd_experiment_id AND ispcvt.name = 'FIELDMAP RANGE') 'FIELDMAP RANGE', \n"
		+ "    nde.obs_unit_id as OBS_UNIT_ID, \n"
		+ "    (SELECT sprop.value FROM stockprop sprop INNER JOIN cvterm spropcvt ON spropcvt.cvterm_id = sprop.type_id WHERE sprop.stock_id = s.stock_id AND spropcvt.name = 'STOCK_ID') 'STOCK_ID', \n"
		+ "    (SELECT xprop.value FROM nd_experimentprop xprop INNER JOIN cvterm xpropcvt ON xpropcvt.cvterm_id = xprop.type_id WHERE xprop.nd_experiment_id = nde.nd_experiment_id AND xpropcvt.name = 'FACT1') 'FACT1', \n"
		+ " 1=1 FROM \n"
		+ "\tproject p \n"
		+ "\tINNER JOIN project_relationship pr ON p.project_id = pr.subject_project_id \n"
		+ "\tINNER JOIN nd_experiment nde ON nde.project_id = pr.subject_project_id \n"
		+ "\tINNER JOIN nd_geolocation gl ON nde.nd_geolocation_id = gl.nd_geolocation_id \n"
		+ "\tINNER JOIN stock s ON s.stock_id = nde.stock_id \n"
		+ "\tLEFT JOIN phenotype ph ON nde.nd_experiment_id = ph.nd_experiment_id \n"
		+ "\tLEFT JOIN cvterm cvterm_variable ON cvterm_variable.cvterm_id = ph.observable_id \n"
		+ "   INNER JOIN nd_experiment parent ON parent.nd_experiment_id = nde.parent_id \t\tWHERE p.project_id = :datasetId \n"
		+ " AND gl.nd_geolocation_id = :instanceId GROUP BY nde.nd_experiment_id  ORDER BY (1 * PLOT_NO) asc ";


	@Mock
	private ProjectPropertyDao projectPropertyDao;

	@InjectMocks
	private DatasetServiceImpl datasetService;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		this.datasetService.setDaoFactory(this.daoFactory);
		Mockito.when(this.daoFactory.getPhenotypeDAO()).thenReturn(this.phenotypeDao);
		Mockito.when(this.daoFactory.getDmsProjectDAO()).thenReturn(this.dmsProjectDao);
		Mockito.when(this.daoFactory.getExperimentDAO()).thenReturn(this.experimentDao);
		Mockito.when(this.daoFactory.getProjectPropertyDAO()).thenReturn(this.projectPropertyDao);
	}

	@Test
	public void testCountPhenotypes() {
		final long count = 5;
		Mockito.when(this.phenotypeDao.countPhenotypesForDataset(Matchers.anyInt(), Matchers.anyListOf(Integer.class))).thenReturn(count);
		Assert.assertEquals(count, this.datasetService.countPhenotypes(123, Arrays.asList(11, 22)));
	}

	@Test
	public void testAddVariable() {
		final Random ran = new Random();
		final Integer datasetId = ran.nextInt();
		final Integer nextRank = ran.nextInt();
		Mockito.doReturn(nextRank).when(this.projectPropertyDao).getNextRank(datasetId);
		final Integer traitId = ran.nextInt();
		final String alias = RandomStringUtils.randomAlphabetic(20);

		this.datasetService.addVariable(datasetId, traitId, VariableType.TRAIT, alias);
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
		this.datasetService.removeVariables(datasetId, variableIds);
		Mockito.verify(this.phenotypeDao).deletePhenotypesByProjectIdAndVariableIds(datasetId, variableIds);
		Mockito.verify(this.projectPropertyDao).deleteProjectVariables(datasetId, variableIds);
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
		final DatasetDTO result = this.datasetService.getDataset(25019,datasetDTOList.get(4).getDatasetId());
		assertThat(datasetDTOList.get(4), equalTo(result));
	}

	@Test
	public void testGetDatasetsFilteringByDatasetTypeId() {
		final List<DatasetDTO> datasetDTOList = this.setUpDatasets(10094);
		final Set<Integer> datasetTypeIds = new TreeSet<>();
		datasetTypeIds.add(10094);
		final List<DatasetDTO> result = this.datasetService.getDatasets(25019, datasetTypeIds);
		assertThat(datasetDTOList, equalTo(result));
	}

	private  List<DatasetDTO> setUpDatasets(final Integer datasetTypeId){
		final List<DatasetDTO> datasetDTOs1 = new ArrayList<>();
		final List<DatasetDTO> datasetDTOs2 = new ArrayList<>();
		final List<DatasetDTO> datasetDTOs3 = new ArrayList<>();
		final List<DatasetDTO> datasetDTOs4 = new ArrayList<>();
		final List<DatasetDTO> datasetDTOList = new ArrayList<>();
		DatasetDTO datasetDTO;

		final boolean filterDataset = datasetTypeId == null || datasetTypeId == 0 ? false : true;

		datasetDTO = createDataset(25020, 25019, "IBP-2015-ENVIRONMENT", 10080);
		datasetDTOs1.add(datasetDTO);
		if ((filterDataset && datasetTypeId.equals(datasetDTO.getDatasetTypeId()) || !filterDataset)) {
			datasetDTOList.add(datasetDTO);

		}
		datasetDTO = createDataset(25021, 25019, "IBP-2015-PLOTDATA", 10090);
		datasetDTOs1.add(datasetDTO);
		if ((filterDataset && datasetTypeId.equals(datasetDTO.getDatasetTypeId()) || !filterDataset)) {
			datasetDTOList.add(datasetDTO);

		}

		Mockito.when(this.dmsProjectDao.getDatasets(25019)).thenReturn(datasetDTOs1);
		Mockito.when(this.dmsProjectDao.getDatasets(25020)).thenReturn(new ArrayList<DatasetDTO>());

		datasetDTO = createDataset(25022, 25021, "IBP-2015-PLOTDATA-SUBOBS", 10094);
		datasetDTOs2.add(datasetDTO);
		if ((filterDataset && datasetTypeId.equals(datasetDTO.getDatasetTypeId()) || !filterDataset)) {
			datasetDTOList.add(datasetDTO);

		}
		Mockito.when(this.dmsProjectDao.getDatasets(25021)).thenReturn(datasetDTOs2);

		datasetDTO = createDataset(25023, 25022, "IBP-2015-PLOTDATA-SUBOBS-SUBOBS", 10094);
		datasetDTOs3.add(datasetDTO);
		if ((filterDataset && datasetTypeId.equals(datasetDTO.getDatasetTypeId()) || !filterDataset)) {
			datasetDTOList.add(datasetDTO);

		}
		Mockito.when(this.dmsProjectDao.getDatasets(25022)).thenReturn(datasetDTOs3);

		datasetDTO = createDataset(25024, 25023, "IBP-2015-PLOTDATA-SUBOBS-SUBOBS-SUBOBS", 10094);
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

	/*@Test
	public void testGetObservations() throws Exception {
		this.datasetService = new DatasetServiceImpl(this.mockSessionProvider);
		this.datasetService.setGermplasmDescriptors(this.germplasmDescriptors);
		this.datasetService.setDesignFactors(this.designFactors);
		this.datasetService.setMeasurementVariableService(this.measurementVariableService);

		Mockito.when(this.mockSessionProvider.getSession()).thenReturn(this.mockSession);
		Mockito.when(this.germplasmDescriptors.find(DatasetServiceImplTest.STUDY_ID))
			.thenReturn(GERMPLASM_DESCRIPTORS);
		Mockito.when(this.designFactors.find(DatasetServiceImplTest.STUDY_ID))
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
		final int instanceId = 1;
		final int pageNumber = 1;
		final int pageSize = 100;
		Mockito.when(this.experimentDao.getObservationVariableName(DATASET_ID)).thenReturn("PLANT_NO");
		Mockito.when(this.experimentDao.getObservationUnitTable(DATASET_ID, projectTraits, GERMPLASM_DESCRIPTORS, DESING_FACTORS, INSTANCE_ID, 1, 100,null, null)).thenReturn(testMeasurements);
		Mockito.when((this.experimentDao.getObservationUnitTableQuery(projectTraits, GERMPLASM_DESCRIPTORS, DESING_FACTORS, null, null,
			"PLANT_NO"))).thenReturn(
			QUERY);

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
//		Mockito.when(mockQuery.list()).thenReturn(results);
		Mockito.when(this.datasetService.getObservationUnitRows(
			DatasetServiceImplTest.STUDY_ID,
			DatasetServiceImplTest.DATASET_ID,
			DatasetServiceImplTest.INSTANCE_ID,
			1,
			10,
			null,
			null)).thenReturn(results);
		// Method to test
		final List<ObservationUnitRow> actualMeasurements = this.datasetService.getObservationUnitRows(DatasetServiceImplTest.STUDY_ID,
			DatasetServiceImplTest.DATASET_ID,
			DatasetServiceImplTest.INSTANCE_ID,
			1,
			10,
			null,
			null);

		Assert.assertEquals(testMeasurements, actualMeasurements);
	}*/

}
