package org.generationcp.middleware.service.impl.derived_variables;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.dao.dms.ExperimentDao;
import org.generationcp.middleware.dao.dms.PhenotypeDao;
import org.generationcp.middleware.dao.dms.ProjectPropertyDao;
import org.generationcp.middleware.domain.dms.DatasetDTO;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.VariableDatasetsDTO;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.ontology.FormulaDto;
import org.generationcp.middleware.domain.ontology.FormulaVariable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.service.api.dataset.DatasetService;
import org.generationcp.middleware.service.api.derived_variables.FormulaService;
import org.generationcp.middleware.service.api.user.UserService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DerivedVariableServiceImplTest {

	private static final int VARIABLE1_TERMID = 123;
	private static final int VARIABLE2_TERMID = 456;
	private static final int VARIABLE3_TERMID = 789;
	private static final int VARIABLE4_TERMID = 999;
	public static final int STUDY_ID = 1000;
	private static final int TERM_ID = 19001;
	private static final int USER_ID = 1;

	@Mock
	private FormulaService formulaService;

	@Mock
	private DatasetService datasetService;

	@Mock
	private PhenotypeDao phenotypeDao;

	@Mock
	private DmsProjectDao dmsProjectDao;

	@Mock
	private ExperimentDao experimentDao;

	@Mock
	private ProjectPropertyDao projectPropertyDao;

	@Mock
	private UserService userService;

	@Mock
	private DaoFactory factory;

	@InjectMocks
	private final DerivedVariableServiceImpl derivedVariableService = new DerivedVariableServiceImpl();

	private final Random random = new Random();
	private static final Integer PLOT_DATASET_ID = 1001;
	private static final Integer SUBOBS_DATASET_ID = 1002;

	@Before
	public void setup() {
		this.derivedVariableService.setDaoFactory(this.factory);
		when(this.factory.getDmsProjectDAO()).thenReturn(this.dmsProjectDao);
		when(this.factory.getPhenotypeDAO()).thenReturn(this.phenotypeDao);
		when(this.factory.getExperimentDao()).thenReturn(this.experimentDao);
		when(this.factory.getProjectPropertyDAO()).thenReturn(this.projectPropertyDao);
	}

	@Test
	public void testGetMissingFormulaVariablesInStudy_FormulaVariablesAreNotPresent() {

		final List<MeasurementVariable> traits = new ArrayList<>();
		final MeasurementVariable trait1 = new MeasurementVariable();
		final MeasurementVariable trait2 = new MeasurementVariable();
		trait1.setTermId(VARIABLE1_TERMID);
		trait2.setTermId(VARIABLE2_TERMID);

		// Only add variables that are not formula/input variables.
		traits.add(trait1);
		traits.add(trait2);

		final Set<FormulaVariable> formulaVariables = this.createFormulaVariables();

		final int studyId = this.random.nextInt(10);
		final int datasetId = this.random.nextInt(10);
		final DatasetDTO dataset = new DatasetDTO();
		dataset.setDatasetId(datasetId);
		dataset.setDatasetTypeId(DatasetTypeEnum.PLOT_DATA.getId());

		when(this.dmsProjectDao.getDatasets(studyId)).thenReturn(Arrays.asList(dataset));
		when(this.dmsProjectDao.getObservationSetVariables(Arrays.asList(datasetId),
			Arrays.asList(VariableType.TRAIT.getId(), VariableType.ENVIRONMENT_DETAIL.getId(), VariableType.ENVIRONMENT_CONDITION.getId())))
			.thenReturn(traits);
		when(this.formulaService.getAllFormulaVariables(new HashSet<>(Arrays.asList(VARIABLE1_TERMID))))
			.thenReturn(formulaVariables);

		final Set<FormulaVariable> missingFormulaVariablesInStudy =
			this.derivedVariableService.getMissingFormulaVariablesInStudy(studyId, datasetId, VARIABLE1_TERMID);

		assertEquals(formulaVariables.size(), missingFormulaVariablesInStudy.size());
		for (final FormulaVariable formulaVariable : formulaVariables) {
			missingFormulaVariablesInStudy.contains(formulaVariable.getName());
		}

	}

	@Test
	public void testGetMissingFormulaVariablesInStudy_FormulaVariablesArePresent() {

		final MeasurementVariable trait1 = new MeasurementVariable();
		final MeasurementVariable trait2 = new MeasurementVariable();
		final MeasurementVariable trait3 = new MeasurementVariable();
		final MeasurementVariable trait4 = new MeasurementVariable();
		trait1.setTermId(VARIABLE1_TERMID);
		trait2.setTermId(VARIABLE2_TERMID);

		// Add the formula/input variables to the list of available variables in a dataset
		trait3.setTermId(VARIABLE3_TERMID);
		trait4.setTermId(VARIABLE4_TERMID);

		final int studyId = this.random.nextInt(10);
		final int datasetId = this.random.nextInt(10);
		final DatasetDTO dataset = new DatasetDTO();
		dataset.setDatasetId(datasetId);
		dataset.setDatasetTypeId(DatasetTypeEnum.PLOT_DATA.getId());

		when(this.dmsProjectDao.getDatasets(studyId)).thenReturn(Arrays.asList(dataset));
		when(this.dmsProjectDao.getObservationSetVariables(Arrays.asList(datasetId),
			Arrays.asList(VariableType.TRAIT.getId(), VariableType.ENVIRONMENT_DETAIL.getId(), VariableType.ENVIRONMENT_CONDITION.getId())))
			.thenReturn(Arrays.asList(trait1, trait2, trait3, trait4));
		when(this.formulaService.getAllFormulaVariables(
			new HashSet<Integer>(Arrays.asList(VARIABLE1_TERMID))))
			.thenReturn(this.createFormulaVariables());

		final Set<FormulaVariable> missingFormulaVariablesInStudy =
			this.derivedVariableService.getMissingFormulaVariablesInStudy(studyId, datasetId, VARIABLE1_TERMID);

		assertTrue(missingFormulaVariablesInStudy.isEmpty());

	}

	@Test
	public void testGetValuesFromObservations() {
		this.derivedVariableService
			.getValuesFromObservations(STUDY_ID, Arrays.asList(DatasetTypeEnum.PLOT_DATA.getId()), new HashMap<>());
		verify(this.experimentDao)
			.getValuesFromObservations(STUDY_ID, Arrays.asList(DatasetTypeEnum.PLOT_DATA.getId()), new HashMap<>());
	}

	@Test
	public void testCreateInputVariableDatasetReferenceMapForPlotDataset() {
		final FormulaVariable formulaVariable = new FormulaVariable();
		formulaVariable.setId(VARIABLE1_TERMID);
		final FormulaDto formulaDto = new FormulaDto();
		formulaDto.setInputs(Arrays.asList(formulaVariable));
		when(this.formulaService.getByTargetId(TERM_ID)).thenReturn(Optional.of(formulaDto));

		final DatasetDTO plotDataset = new DatasetDTO();
		plotDataset.setDatasetId(PLOT_DATASET_ID);
		when(this.datasetService.getDatasets(STUDY_ID, new HashSet<>(Arrays.asList(DatasetTypeEnum.PLOT_DATA.getId()))))
			.thenReturn(Arrays.asList(plotDataset));

		final ProjectProperty projectProperty = new ProjectProperty();
		final DmsProject dmsProject = new DmsProject();
		dmsProject.setProjectId(PLOT_DATASET_ID);
		dmsProject.setName("PLOT DATA");
		projectProperty.setProject(dmsProject);
		projectProperty.setVariableId(VARIABLE1_TERMID);
		projectProperty.setAlias("VARIABLE INPUT");
		when(this.projectPropertyDao.getByStudyAndStandardVariableIds(STUDY_ID, Arrays.asList(VARIABLE1_TERMID)))
			.thenReturn(Arrays.asList(projectProperty));

		final Map<Integer, VariableDatasetsDTO> map =
			this.derivedVariableService.createVariableDatasetsMap(STUDY_ID, PLOT_DATASET_ID, TERM_ID);
		assertNotNull(map.get(projectProperty.getVariableId()));
		final VariableDatasetsDTO variableDatasetsDTO = map.get(projectProperty.getVariableId());
		assertEquals(projectProperty.getAlias(), variableDatasetsDTO.getVariableName());
		final List<DatasetReference> datasetReferences = variableDatasetsDTO.getDatasets();
		assertEquals(PLOT_DATASET_ID, datasetReferences.get(0).getId());
		assertEquals(dmsProject.getName(), datasetReferences.get(0).getName());
	}

	@Test
	public void testCreateInputVariableDatasetReferenceMapForSubobservation() {
		final FormulaVariable formulaVariable = new FormulaVariable();
		formulaVariable.setId(VARIABLE1_TERMID);
		final FormulaDto formulaDto = new FormulaDto();
		formulaDto.setInputs(Arrays.asList(formulaVariable));
		when(this.formulaService.getByTargetId(TERM_ID)).thenReturn(Optional.of(formulaDto));

		final DatasetDTO plotDataset = new DatasetDTO();
		plotDataset.setDatasetId(PLOT_DATASET_ID);
		when(this.datasetService.getDatasets(STUDY_ID, new HashSet<>(Arrays.asList(DatasetTypeEnum.PLOT_DATA.getId()))))
			.thenReturn(Arrays.asList(plotDataset));

		final ProjectProperty projectProperty = new ProjectProperty();
		final DmsProject dmsProject = new DmsProject();
		dmsProject.setProjectId(SUBOBS_DATASET_ID);
		dmsProject.setName("PLANT SUBOBS");
		projectProperty.setProject(dmsProject);
		projectProperty.setVariableId(VARIABLE1_TERMID);
		projectProperty.setAlias("VARIABLE INPUT");
		when(this.projectPropertyDao.getByProjectIdAndVariableIds(SUBOBS_DATASET_ID, Arrays.asList(VARIABLE1_TERMID)))
			.thenReturn(Arrays.asList(projectProperty));

		final Map<Integer, VariableDatasetsDTO> map =
			this.derivedVariableService.createVariableDatasetsMap(STUDY_ID, SUBOBS_DATASET_ID, TERM_ID);
		assertNotNull(map.get(projectProperty.getVariableId()));
		final VariableDatasetsDTO variableDatasetsDTO = map.get(projectProperty.getVariableId());
		assertEquals(projectProperty.getAlias(), variableDatasetsDTO.getVariableName());
		final List<DatasetReference> datasetReferences = variableDatasetsDTO.getDatasets();
		assertEquals(SUBOBS_DATASET_ID, datasetReferences.get(0).getId());
		assertEquals(dmsProject.getName(), datasetReferences.get(0).getName());
	}

	@Test
	public void testCreateVariableIdMeasurementVariableMap() {
		final List<DatasetDTO> datasets = this.createDatasetDTOS();
		final List<Integer> projectIds = new ArrayList<>();
		for (final DatasetDTO datasetDTO : datasets) {
			projectIds.add(datasetDTO.getDatasetId());
		}
		when(this.dmsProjectDao.getDatasets(STUDY_ID)).thenReturn(datasets);
		final MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementVariable.setTermId(TERM_ID);

		when(this.dmsProjectDao.getObservationSetVariables(projectIds,
			Arrays.asList(VariableType.TRAIT.getId(), VariableType.ENVIRONMENT_DETAIL.getId(), VariableType.ENVIRONMENT_CONDITION.getId())))
			.thenReturn(Arrays.asList(measurementVariable));
		final Map<Integer, MeasurementVariable> variableIdMeasurementVariableMap =
			this.derivedVariableService.createVariableIdMeasurementVariableMapInStudy(STUDY_ID);
		assertEquals(measurementVariable, variableIdMeasurementVariableMap.get(measurementVariable.getTermId()));
	}

	@Test
	public void testCountCalculatedVariablesInDatasets() {
		final int expectedCount = this.random.nextInt();
		final Set<Integer> datasetIds = new HashSet<Integer>(Arrays.asList(1));
		when(this.dmsProjectDao.countCalculatedVariablesInDatasets(datasetIds)).thenReturn(expectedCount);
		assertEquals(expectedCount, this.derivedVariableService.countCalculatedVariablesInDatasets(datasetIds));
	}

	@Test
	@Ignore // FIXME IBP-2634
	public void testSaveCalculatedResultUpdatePhenotype() {

		final int variableTermId = this.random.nextInt(10);
		final MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementVariable.setTermId(variableTermId);

		final String value = RandomStringUtils.random(10);
		final Integer categoricalId = this.random.nextInt(10);
		final Integer observationUnitId = this.random.nextInt(10);
		final Integer observationId = this.random.nextInt(10);

		final Phenotype existingPhenotype = new Phenotype();
		existingPhenotype.setObservableId(observationId);
		when(this.phenotypeDao.getById(observationId)).thenReturn(existingPhenotype);

		this.derivedVariableService.saveCalculatedResult(value, categoricalId, observationUnitId, observationId, measurementVariable);

		verify(this.phenotypeDao).update(existingPhenotype);
		verify(this.datasetService).updateDependentPhenotypesAsOutOfSync(variableTermId, Sets.newHashSet(observationUnitId));
		assertEquals(value, existingPhenotype.getValue());
		assertEquals(categoricalId, existingPhenotype.getcValueId());
		assertTrue(existingPhenotype.isChanged());
		assertNull(existingPhenotype.getValueStatus());

	}

	@Test
	public void testGetFormulaVariablesInStudy() {
		final DmsProject plotDataset = new DmsProject();
		plotDataset.setProjectId(PLOT_DATASET_ID);
		when(this.dmsProjectDao.getDatasetsByTypeForStudy(STUDY_ID, DatasetTypeEnum.PLOT_DATA.getId()))
			.thenReturn(Arrays.asList(plotDataset));

		final MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementVariable.setTermId(TERM_ID);
		when(this.dmsProjectDao.getObservationSetVariables(Arrays.asList(PLOT_DATASET_ID, SUBOBS_DATASET_ID),
			Arrays
				.asList(VariableType.TRAIT.getId(), VariableType.ENVIRONMENT_DETAIL.getId(), VariableType.ENVIRONMENT_CONDITION.getId())))
			.thenReturn(Arrays.asList(measurementVariable));

		this.derivedVariableService.getFormulaVariablesInStudy(STUDY_ID, SUBOBS_DATASET_ID);
		verify(this.dmsProjectDao).getDatasetsByTypeForStudy(STUDY_ID, DatasetTypeEnum.PLOT_DATA.getId());
		verify(this.dmsProjectDao).getObservationSetVariables(Arrays.asList(PLOT_DATASET_ID, SUBOBS_DATASET_ID),
			Arrays
				.asList(VariableType.TRAIT.getId(), VariableType.ENVIRONMENT_DETAIL.getId(), VariableType.ENVIRONMENT_CONDITION.getId()));
		verify(this.formulaService).getAllFormulaVariables(new HashSet<>(Arrays.asList(measurementVariable.getTermId())));
	}

	@Test
	public void testExtractVariableIdsFromDatasetForSubObs() {
		final List<DatasetDTO> datasets = this.createDatasetDTOS();
		final List<Integer> projectIds = new ArrayList<>();
		for (final DatasetDTO datasetDTO : datasets) {
			projectIds.add(datasetDTO.getDatasetId());
		}
		when(this.dmsProjectDao.getDatasets(STUDY_ID)).thenReturn(datasets);
		final MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementVariable.setTermId(TERM_ID);
		when(this.dmsProjectDao.getObservationSetVariables(Arrays.asList(SUBOBS_DATASET_ID),
			Arrays.asList(VariableType.TRAIT.getId()))).thenReturn(Arrays.asList(measurementVariable));

		final Set<Integer> variableIds = this.derivedVariableService.extractVariableIdsFromDataset(STUDY_ID, SUBOBS_DATASET_ID);
		verify(this.dmsProjectDao).getDatasets(STUDY_ID);
		verify(this.dmsProjectDao).getObservationSetVariables(Arrays.asList(SUBOBS_DATASET_ID),
			Arrays.asList(VariableType.TRAIT.getId()));
		verify(this.dmsProjectDao, never()).getObservationSetVariables(projectIds,
			Arrays
				.asList(VariableType.TRAIT.getId(), VariableType.ENVIRONMENT_DETAIL.getId(), VariableType.ENVIRONMENT_CONDITION.getId()));
		assertEquals(1, variableIds.size());
		assertTrue(variableIds.contains(measurementVariable.getTermId()));
	}

	@Test
	public void testExtractVariableIdsFromDatasetForPlotDataset() {
		final List<DatasetDTO> datasets = this.createDatasetDTOS();
		final List<Integer> projectIds = new ArrayList<>();
		for (final DatasetDTO datasetDTO : datasets) {
			projectIds.add(datasetDTO.getDatasetId());
		}
		datasets.get(1).setDatasetTypeId(DatasetTypeEnum.PLOT_DATA.getId());
		when(this.dmsProjectDao.getDatasets(STUDY_ID)).thenReturn(datasets);
		final MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementVariable.setTermId(TERM_ID);
		when(this.dmsProjectDao.getObservationSetVariables(projectIds,
			Arrays.asList(VariableType.TRAIT.getId(), VariableType.ENVIRONMENT_DETAIL.getId(), VariableType.ENVIRONMENT_CONDITION.getId())))
			.thenReturn(Arrays.asList(measurementVariable));

		final Set<Integer> variableIds = this.derivedVariableService.extractVariableIdsFromDataset(STUDY_ID, PLOT_DATASET_ID);
		verify(this.dmsProjectDao).getDatasets(STUDY_ID);
		verify(this.dmsProjectDao, never()).getObservationSetVariables(Arrays.asList(PLOT_DATASET_ID),
			Arrays.asList(VariableType.TRAIT.getId()));
		verify(this.dmsProjectDao).getObservationSetVariables(projectIds,
			Arrays
				.asList(VariableType.TRAIT.getId(), VariableType.ENVIRONMENT_DETAIL.getId(), VariableType.ENVIRONMENT_CONDITION.getId()));
		assertEquals(1, variableIds.size());
		assertTrue(variableIds.contains(measurementVariable.getTermId()));
	}

	@Test
	public void testSaveCalculatedResultCreatePhenotype() {

		final int variableTermId = this.random.nextInt(10);
		final MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementVariable.setTermId(variableTermId);

		final String value = RandomStringUtils.random(10);
		final Integer categoricalId = this.random.nextInt(10);
		final Integer observationUnitId = this.random.nextInt(10);

		// When observationId is null, it means there's no phenotype existing yet.
		final Integer observationId = null;

		when(this.userService.getCurrentlyLoggedInUserId()).thenReturn(USER_ID);
		this.derivedVariableService.saveCalculatedResult(value, categoricalId, observationUnitId, observationId, measurementVariable);

		final ArgumentCaptor<Phenotype> captor = ArgumentCaptor.forClass(Phenotype.class);
		verify(this.phenotypeDao).save(captor.capture());

		final Phenotype phenotypeToBeSaved = captor.getValue();
		assertNotNull(phenotypeToBeSaved.getCreatedDate());
		assertNotNull(phenotypeToBeSaved.getUpdatedDate());
		assertEquals(categoricalId, phenotypeToBeSaved.getcValueId());
		assertEquals(variableTermId, phenotypeToBeSaved.getObservableId().intValue());
		assertEquals(value, phenotypeToBeSaved.getValue());
		assertEquals(observationUnitId, phenotypeToBeSaved.getExperiment().getNdExperimentId());
		assertEquals(String.valueOf(variableTermId), phenotypeToBeSaved.getName());

	}

	private Set<FormulaVariable> createFormulaVariables() {

		final Set<FormulaVariable> formulaVariables = new HashSet<>();

		final FormulaVariable formulaVariable1 = new FormulaVariable();
		formulaVariable1.setId(VARIABLE3_TERMID);
		formulaVariable1.setName("VARIABLE3");
		formulaVariable1.setTargetTermId(VARIABLE1_TERMID);

		final FormulaVariable formulaVariable2 = new FormulaVariable();
		formulaVariable2.setId(VARIABLE4_TERMID);
		formulaVariable2.setName("VARIABLE4");
		formulaVariable2.setTargetTermId(VARIABLE2_TERMID);

		formulaVariables.add(formulaVariable1);
		formulaVariables.add(formulaVariable2);

		return formulaVariables;

	}

	private List<DatasetDTO> createDatasetDTOS() {
		final List<DatasetDTO> datasets = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			final DatasetDTO datasetDTO = new DatasetDTO();
			datasetDTO.setDatasetId(STUDY_ID + i);
			datasetDTO.setDatasetTypeId(DatasetTypeEnum.PLANT_SUBOBSERVATIONS.getId());
			datasets.add(datasetDTO);
		}
		return datasets;
	}

}
