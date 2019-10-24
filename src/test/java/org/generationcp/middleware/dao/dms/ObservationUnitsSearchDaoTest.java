package org.generationcp.middleware.dao.dms;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.*;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.manager.ontology.api.OntologyMethodDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyPropertyDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyScaleDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.manager.ontology.daoElements.OntologyVariableInfo;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.service.api.dataset.ObservationUnitData;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.generationcp.middleware.service.api.dataset.ObservationUnitsSearchDTO;
import org.generationcp.middleware.service.api.study.MeasurementVariableDto;
import org.generationcp.middleware.utils.test.IntegrationTestDataInitializer;
import org.generationcp.middleware.utils.test.OntologyDataCreationUtil;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static org.junit.Assert.*;

public class ObservationUnitsSearchDaoTest extends IntegrationTestBase {

	private ObservationUnitsSearchDao obsUnitSearchDao;

	private IntegrationTestDataInitializer testDataInitializer;

	private DmsProject study;
	private DmsProject plot;
	private DmsProject summary;


	@Autowired
	private OntologyMethodDataManager methodManager;

	@Autowired
	private OntologyPropertyDataManager propertyManager;

	@Autowired
	private OntologyScaleDataManager scaleManager;

	@Autowired
	private OntologyVariableDataManager variableManager;

	@Before
	public void setUp() {

		this.obsUnitSearchDao = new ObservationUnitsSearchDao();
		this.obsUnitSearchDao.setSession(this.sessionProvder.getSession());
		DmsProjectDao dmsProjectDao = new DmsProjectDao();
		dmsProjectDao.setSession(this.sessionProvder.getSession());

		this.testDataInitializer = new IntegrationTestDataInitializer(this.sessionProvder, this.workbenchSessionProvider);
		this.study = this.testDataInitializer.createDmsProject("Study1", "Study-Description", null, dmsProjectDao.getById(1), null);
		this.plot = this.testDataInitializer
			.createDmsProject("Plot Dataset", "Plot Dataset-Description", this.study, this.study, DatasetTypeEnum.PLOT_DATA);
		this.summary = this.testDataInitializer
			.createDmsProject("Environment Dataset", "Environment Dataset-Description", this.study, this.study,
				DatasetTypeEnum.SUMMARY_DATA);


	}

	@Test
	public void testGetObservationUnitTable() {

		final String traitName = "MyTrait";
		final String environmentDetailVariableName = "FACTOR1";
		final String environmentConditionVariableName = "FACTOR2";
		final String observationUnitVariableName = "PLANT_NO";
		final int noOfSubObservationExperiment = 3;

		final CVTerm trait1 = this.testDataInitializer.createTrait(traitName);
		final CVTerm environmentDetailVariable = this.testDataInitializer.createTrait(environmentDetailVariableName);
		final CVTerm environmentConditionVariable = this.testDataInitializer.createTrait(environmentConditionVariableName);

		final DmsProject plantSubObsDataset =
			this.testDataInitializer.createDmsProject("Plant SubObs Dataset", "Plot Dataset-Description", this.study, this.plot,
				DatasetTypeEnum.PLANT_SUBOBSERVATIONS);
		this.testDataInitializer
			.addProjectProp(plantSubObsDataset, 8206, observationUnitVariableName, VariableType.OBSERVATION_UNIT, "", 1);

		final Geolocation geolocation = this.testDataInitializer.createTestGeolocation("1", 101);
		this.testDataInitializer.addGeolocationProp(geolocation, environmentDetailVariable.getCvTermId(), "100", 1);

		final ExperimentModel environmentExperimentModel =
			this.testDataInitializer.createTestExperiment(this.summary, geolocation, TermId.SUMMARY_EXPERIMENT.getId(), null, null);
		final ExperimentModel plotExperimentModel =
			this.testDataInitializer.createTestExperiment(this.plot, geolocation, TermId.PLOT_EXPERIMENT.getId(), null, null);
		final List<ExperimentModel> plantExperimentModels =
			this.testDataInitializer
				.createTestExperiments(plantSubObsDataset, plotExperimentModel, geolocation, noOfSubObservationExperiment);

		this.testDataInitializer.addPhenotypes(plantExperimentModels, trait1.getCvTermId(), RandomStringUtils.randomNumeric(5));

		final MeasurementVariableDto environmentDetailDto =
			new MeasurementVariableDto(environmentDetailVariable.getCvTermId(), environmentDetailVariable.getName());
		final MeasurementVariableDto environmentConditionDto =
			new MeasurementVariableDto(environmentConditionVariable.getCvTermId(), environmentConditionVariable.getName());
		final MeasurementVariableDto measurementVariableDto = new MeasurementVariableDto(trait1.getCvTermId(), trait1.getName());

		final ObservationUnitsSearchDTO observationUnitsSearchDTO = this.testDataInitializer.createTestObservationUnitsDTO();
		observationUnitsSearchDTO.setDatasetId(plantSubObsDataset.getProjectId());
		observationUnitsSearchDTO.setInstanceId(geolocation.getLocationId());
		observationUnitsSearchDTO.setSelectionMethodsAndTraits(Collections.singletonList(measurementVariableDto));
		observationUnitsSearchDTO.setEnvironmentDetails(Collections.singletonList(environmentDetailDto));
		observationUnitsSearchDTO.setEnvironmentConditions(Collections.singletonList(environmentConditionDto));
		observationUnitsSearchDTO.setFilter(observationUnitsSearchDTO.new Filter());

		// Need to flush session to sync with underlying database before querying
		this.sessionProvder.getSession().flush();

		final List<ObservationUnitRow> measurementRows = this.obsUnitSearchDao.getObservationUnitTable(observationUnitsSearchDTO);

		assertEquals(noOfSubObservationExperiment, measurementRows.size());

		final ObservationUnitRow observationUnitRow = measurementRows.get(0);

		assertEquals(plantExperimentModels.get(0).getStock().getName(), observationUnitRow.getDesignation());
		assertNotNull(observationUnitRow.getGid());
		assertEquals("-", observationUnitRow.getSamplesCount());
		assertNotNull(observationUnitRow.getObsUnitId());

		final Map<String, ObservationUnitData> dataMap = observationUnitRow.getVariables();

		assertEquals("1", dataMap.get(observationUnitVariableName).getValue());
		assertNotNull(dataMap.get(traitName).getValue());
		assertNull(dataMap.get(ObservationUnitsSearchDao.COL).getValue());
		assertEquals(observationUnitRow.getGid().toString(), dataMap.get(ObservationUnitsSearchDao.GID).getValue());
		assertNull(dataMap.get(ObservationUnitsSearchDao.FIELD_MAP_RANGE).getValue());
		assertNull(dataMap.get(ObservationUnitsSearchDao.FIELD_MAP_COLUMN).getValue());
		assertEquals(observationUnitRow.getObsUnitId(), dataMap.get(ObservationUnitsSearchDao.OBS_UNIT_ID).getValue());
		assertEquals(plotExperimentModel.getObsUnitId(), dataMap.get(ObservationUnitsSearchDao.PARENT_OBS_UNIT_ID).getValue());
		assertNull(dataMap.get(ObservationUnitsSearchDao.ENTRY_TYPE).getValue());
		assertNull(dataMap.get(ObservationUnitsSearchDao.EXPT_DESIGN).getValue());
		assertEquals("1", dataMap.get(ObservationUnitsSearchDao.ENTRY_NO).getValue());
		assertEquals(plantExperimentModels.get(0).getStock().getName(), dataMap.get(ObservationUnitsSearchDao.DESIGNATION).getValue());
		assertEquals("1", dataMap.get(ObservationUnitsSearchDao.TRIAL_INSTANCE).getValue());
		assertNull(dataMap.get(ObservationUnitsSearchDao.ENTRY_CODE).getValue());
		assertNull(dataMap.get(ObservationUnitsSearchDao.BLOCK_NO).getValue());
		assertEquals("India", dataMap.get(ObservationUnitsSearchDao.LOCATION_ID).getValue());
		assertNull(dataMap.get(ObservationUnitsSearchDao.ROW).getValue());
		assertNull(dataMap.get(ObservationUnitsSearchDao.REP_NO).getValue());
		assertNull(dataMap.get(ObservationUnitsSearchDao.PLOT_NO).getValue());

		final Map<String, ObservationUnitData> environmentDataMap = observationUnitRow.getEnvironmentVariables();

		assertEquals("100", environmentDataMap.get(environmentDetailVariableName).getValue());
		assertNull(environmentDataMap.get(environmentConditionVariableName).getValue());

	}

	@Test
	public void testGetObservationUnitsByVariable() {

		final String traitName = "MyTrait";
		final String observationUnitVariableName = "PLANT_NO";
		final int noOfSubObservationExperiment = 3;

		final CVTerm trait1 = this.testDataInitializer.createTrait(traitName);
		final DmsProject plantSubObsDataset =
			this.testDataInitializer.createDmsProject("Plant SubObs Dataset", "Plot Dataset-Description", this.study, this.plot,
				DatasetTypeEnum.PLANT_SUBOBSERVATIONS);
		this.testDataInitializer
			.addProjectProp(plantSubObsDataset, 8206, observationUnitVariableName, VariableType.OBSERVATION_UNIT, "", 1);

		final Geolocation geolocation = this.testDataInitializer.createTestGeolocation("1", 101);

		final ExperimentModel plotExperimentModel =
			this.testDataInitializer.createTestExperiment(this.plot, geolocation, TermId.PLOT_EXPERIMENT.getId(), null, null);
		final List<ExperimentModel> plantExperimentModels =
			this.testDataInitializer
				.createTestExperiments(plantSubObsDataset, plotExperimentModel, geolocation, noOfSubObservationExperiment);

		this.testDataInitializer.addPhenotypes(plantExperimentModels, trait1.getCvTermId(), RandomStringUtils.randomNumeric(5));

		final MeasurementVariableDto measurementVariableDto = new MeasurementVariableDto(trait1.getCvTermId(), trait1.getName());

		final ObservationUnitsSearchDTO observationUnitsSearchDTO = this.testDataInitializer.createTestObservationUnitsDTO();
		observationUnitsSearchDTO.setDatasetId(plantSubObsDataset.getProjectId());
		observationUnitsSearchDTO.setInstanceId(geolocation.getLocationId());
		observationUnitsSearchDTO.setSelectionMethodsAndTraits(Collections.singletonList(measurementVariableDto));

		// Filter by Overwritten
		final ObservationUnitsSearchDTO.Filter filter = observationUnitsSearchDTO.new Filter();
		filter.setVariableId(trait1.getCvTermId());
		filter.setByOverwritten(true);
		observationUnitsSearchDTO.setDraftMode(true);
		observationUnitsSearchDTO.setFilter(filter);

		// Need to flush session to sync with underlying database before querying
		this.sessionProvder.getSession().flush();

		final List<ObservationUnitRow> measurementRows = this.obsUnitSearchDao.getObservationUnitsByVariable(observationUnitsSearchDTO);

		assertEquals(noOfSubObservationExperiment, measurementRows.size());

		final ObservationUnitRow observationUnitRow = measurementRows.get(0);

		assertNull(observationUnitRow.getDesignation());
		assertNull(observationUnitRow.getGid());
		assertNull(observationUnitRow.getSamplesCount());
		assertNull(observationUnitRow.getObsUnitId());

		final Map<String, ObservationUnitData> dataMap = observationUnitRow.getVariables();

		assertEquals(1, dataMap.size());
		assertNotNull(dataMap.get(trait1.getCvTermId().toString()).getDraftValue());

	}

	@Test
	public void testCountObservationUnitsForSubObsDataset() {

		final String traitName = "MyTrait";
		final String observationUnitVariableName = "PLANT_NO";
		final int noOfSubObservationExperiment = 3;

		final DmsProject plantSubObsDataset =
			this.testDataInitializer.createDmsProject("Plant SubObs Dataset", "Plot Dataset-Description", this.study, this.plot,
				DatasetTypeEnum.PLANT_SUBOBSERVATIONS);
		this.testDataInitializer
			.addProjectProp(plantSubObsDataset, 8206, observationUnitVariableName, VariableType.OBSERVATION_UNIT, "", 1);

		final Geolocation geolocation = this.testDataInitializer.createTestGeolocation("1", 101);
		final ExperimentModel plotExperimentModel1 =
			this.testDataInitializer.createTestExperiment(this.plot, geolocation, TermId.PLOT_EXPERIMENT.getId(), null, null);
		final List<ExperimentModel> subObsExperimentsInstance1 =
			this.testDataInitializer
				.createTestExperiments(plantSubObsDataset, plotExperimentModel1, geolocation, noOfSubObservationExperiment);

		final Geolocation geolocation2 = this.testDataInitializer.createTestGeolocation("2", 101);
		final ExperimentModel plotExperimentModel2 =
			this.testDataInitializer.createTestExperiment(this.plot, geolocation2, TermId.PLOT_EXPERIMENT.getId(), null, null);
		final List<ExperimentModel> subObsExperimentsInstance2 = this.testDataInitializer
			.createTestExperiments(plantSubObsDataset, plotExperimentModel2, geolocation2, noOfSubObservationExperiment);

		// Only first instance has observations
		final CVTerm trait1 = this.testDataInitializer.createTrait(traitName);
		this.testDataInitializer.addPhenotypes(subObsExperimentsInstance1, trait1.getCvTermId(), RandomStringUtils.randomNumeric(5));

		final MeasurementVariableDto measurementVariableDto = new MeasurementVariableDto(trait1.getCvTermId(), trait1.getName());

		final ObservationUnitsSearchDTO observationUnitsSearchDTO = this.testDataInitializer.createTestObservationUnitsDTO();
		final Integer datasetId = plantSubObsDataset.getProjectId();
		observationUnitsSearchDTO.setDatasetId(datasetId);
		observationUnitsSearchDTO.setSelectionMethodsAndTraits(Collections.singletonList(measurementVariableDto));

		final ObservationUnitsSearchDTO.Filter filter = observationUnitsSearchDTO.new Filter();
		final Map<String, String> variableTypeMap = new HashMap<>();
		variableTypeMap.put(String.valueOf(TermId.TRIAL_INSTANCE_FACTOR.getId()), VariableType.ENVIRONMENT_DETAIL.name());
		filter.setVariableTypeMap(variableTypeMap);
		final Map<String, List<String>> filteredValues = new HashMap<>();
		filter.setFilteredValues(filteredValues);
		final HashMap<String, String> filteredTextValues = new HashMap<>();
		filter.setFilteredTextValues(filteredTextValues);

		// Need to flush session to sync with underlying database before querying
		this.sessionProvder.getSession().flush();

		assertEquals((noOfSubObservationExperiment * 2),
			this.obsUnitSearchDao.countObservationUnitsForDataset(datasetId, null, false, filter).intValue());
		assertEquals(noOfSubObservationExperiment,
			this.obsUnitSearchDao.countObservationUnitsForDataset(datasetId, geolocation.getLocationId(), false, filter).intValue());
		assertEquals(noOfSubObservationExperiment,
			this.obsUnitSearchDao.countObservationUnitsForDataset(datasetId, geolocation2.getLocationId(), false, filter).intValue());

		// Filter by draft phenotype
		filter.setVariableId(trait1.getCvTermId());
		assertEquals(noOfSubObservationExperiment,
			this.obsUnitSearchDao.countObservationUnitsForDataset(datasetId, null, true, filter).intValue());

		filter.setVariableId(null);
		// Filter by TRIAL_INSTANCE
		filteredValues.put(String.valueOf(TermId.TRIAL_INSTANCE_FACTOR.getId()), Collections.singletonList("1"));
		assertEquals(noOfSubObservationExperiment,
			this.obsUnitSearchDao.countObservationUnitsForDataset(datasetId, null, false, filter).intValue());
		// Filter by GID
		filteredValues.put(String.valueOf(TermId.GID.getId()),
			Collections.singletonList(subObsExperimentsInstance1.get(0).getStock().getGermplasm().getGid().toString()));
		assertEquals(1, this.obsUnitSearchDao.countObservationUnitsForDataset(datasetId, null, false, filter).intValue());

		filteredValues.clear();
		filteredValues.put(String.valueOf(TermId.TRIAL_INSTANCE_FACTOR.getId()), Collections.singletonList("2"));
		// Filter by DESIGNATION using LIKE operation
		filteredTextValues.put(String.valueOf(TermId.DESIG.getId()), "Germplasm");
		assertEquals(noOfSubObservationExperiment,
			this.obsUnitSearchDao.countObservationUnitsForDataset(datasetId, null, false, filter).intValue());
	}

	@Test
	public void testCountObservationUnitsForPlotDataset() {

		final String traitName = "MyTrait";


		final int numberOfPlotExperiments = 3;
		final Geolocation geolocation = this.testDataInitializer.createTestGeolocation("1", 101);
		final List<ExperimentModel> instance1Units = this.testDataInitializer.createTestExperiments(this.plot, null, geolocation, numberOfPlotExperiments);
		final Geolocation geolocation2 = this.testDataInitializer.createTestGeolocation("2", 101);
		final List<ExperimentModel> instance2Units = this.testDataInitializer.createTestExperiments(this.plot, null, geolocation2, numberOfPlotExperiments);


		// Only 2 experiments in first instance have observations
		final CVTerm trait1 = this.testDataInitializer.createTrait(traitName);
		final List<ExperimentModel> unitsWithObservations = Arrays.asList(instance1Units.get(0), instance1Units.get(1));
		this.testDataInitializer.addPhenotypes(unitsWithObservations, trait1.getCvTermId(), RandomStringUtils.randomNumeric(5));

		final MeasurementVariableDto measurementVariableDto = new MeasurementVariableDto(trait1.getCvTermId(), trait1.getName());
		final ObservationUnitsSearchDTO observationUnitsSearchDTO = this.testDataInitializer.createTestObservationUnitsDTO();
		final Integer datasetId = this.plot.getProjectId();
		observationUnitsSearchDTO.setDatasetId(datasetId);
		observationUnitsSearchDTO.setSelectionMethodsAndTraits(Collections.singletonList(measurementVariableDto));

		final ObservationUnitsSearchDTO.Filter filter = observationUnitsSearchDTO.new Filter();
		final Map<String, String> variableTypeMap = new HashMap<>();
		variableTypeMap.put(String.valueOf(TermId.TRIAL_INSTANCE_FACTOR.getId()), VariableType.ENVIRONMENT_DETAIL.name());
		variableTypeMap.put(String.valueOf(TermId.PLOT_NO.getId()), VariableType.EXPERIMENTAL_DESIGN.name());
		filter.setVariableTypeMap(variableTypeMap);
		final Map<String, List<String>> filteredValues = new HashMap<>();
		filter.setFilteredValues(filteredValues);
		final HashMap<String, String> filteredTextValues = new HashMap<>();
		filter.setFilteredTextValues(filteredTextValues);

		// Need to flush session to sync with underlying database before querying
		this.sessionProvder.getSession().flush();

		assertEquals(instance1Units.size() + instance2Units.size(),
			this.obsUnitSearchDao.countObservationUnitsForDataset(datasetId, null, false, filter).intValue());
		assertEquals(instance1Units.size(),
			this.obsUnitSearchDao.countObservationUnitsForDataset(datasetId, geolocation.getLocationId(), false, filter).intValue());
		assertEquals(instance2Units.size(),
			this.obsUnitSearchDao.countObservationUnitsForDataset(datasetId, geolocation2.getLocationId(), false, filter).intValue());

		// Filter by draft phenotype
		filter.setVariableId(trait1.getCvTermId());
		assertEquals(unitsWithObservations.size(),
			this.obsUnitSearchDao.countObservationUnitsForDataset(datasetId, null, true, filter).intValue());

		filter.setVariableId(null);
		// Filter by PLOT_NO
		filteredValues.put(String.valueOf(TermId.PLOT_NO.getId()), Collections.singletonList("2"));
		assertEquals(2,
			this.obsUnitSearchDao.countObservationUnitsForDataset(datasetId, null, false, filter).intValue());

		// Filter by TRIAL_INSTANCE
		filteredValues.clear();
		filteredValues.put(String.valueOf(TermId.TRIAL_INSTANCE_FACTOR.getId()), Collections.singletonList("1"));
		assertEquals(3,
			this.obsUnitSearchDao.countObservationUnitsForDataset(datasetId, null, false, filter).intValue());
		// Filter by GID
		filteredValues.put(String.valueOf(TermId.GID.getId()),
			Collections.singletonList(unitsWithObservations.get(0).getStock().getGermplasm().getGid().toString()));
		assertEquals(1, this.obsUnitSearchDao.countObservationUnitsForDataset(datasetId, null, false, filter).intValue());

		filteredValues.clear();
		filteredValues.put(String.valueOf(TermId.TRIAL_INSTANCE_FACTOR.getId()), Collections.singletonList("2"));
		// Filter by DESIGNATION using LIKE operation
		filteredTextValues.put(String.valueOf(TermId.DESIG.getId()), "Germplasm");
		assertEquals(3,
			this.obsUnitSearchDao.countObservationUnitsForDataset(datasetId, null, false, filter).intValue());
	}


	@Test
	public void testFilterByOutOfBoundsWithoutOutOfBoundsMinValue10MaxValue100() {
		final String traitName = "MyTrait";
		final Geolocation geolocation = this.testDataInitializer.createTestGeolocation("1", 101);
		final List<ExperimentModel> instance1Units = this.testDataInitializer.createTestExperiments(this.plot, null, geolocation, 2);
		//trait minvalue 10 maxvalue 100
		final Integer traitId = this.createTrait(traitName, "10", "100", null, null);
		final List<ExperimentModel> unitsWithObservations = Collections.singletonList(instance1Units.get(0));
		final List<ExperimentModel> unitsWithObservations2 = Collections.singletonList(instance1Units.get(1));
		this.testDataInitializer.addPhenotypes(unitsWithObservations, traitId, "40");
		this.testDataInitializer.addPhenotypes(unitsWithObservations2, traitId, "100");

		final MeasurementVariableDto measurementVariableDto = new MeasurementVariableDto(traitId, traitName);
		final ObservationUnitsSearchDTO observationUnitsSearchDTO = this.testDataInitializer.createTestObservationUnitsDTO();
		final Integer datasetId = this.plot.getProjectId();
		observationUnitsSearchDTO.setDatasetId(datasetId);
		observationUnitsSearchDTO.setSelectionMethodsAndTraits(Collections.singletonList(measurementVariableDto));
		observationUnitsSearchDTO.setInstanceId(geolocation.getLocationId());

		final ObservationUnitsSearchDTO.Filter filter = observationUnitsSearchDTO.new Filter();
		filter.setByOutOfBound(true);
		filter.setVariableId(traitId);
		observationUnitsSearchDTO.setFilter(filter);

		// Need to flush session to sync with underlying database before querying
		this.sessionProvder.getSession().flush();

		final List<ObservationUnitRow> measurementRows = this.obsUnitSearchDao.getObservationUnitsByVariable(observationUnitsSearchDTO);

		assertTrue(measurementRows.isEmpty());
	}

	@Test
	public void testFilterByOutOfBoundsSomeOutOfBoundsMinValue10MaxValue100() {
		final String traitName = "MyTrait";

		final Geolocation geolocation = this.testDataInitializer.createTestGeolocation("1", 101);
		final List<ExperimentModel> instance1Units = this.testDataInitializer.createTestExperiments(this.plot, null, geolocation, 3);
		//trait minvalue 10 maxvalue 100
		final Integer traitId = this.createTrait(traitName, "10", "100", null, null);
		final List<ExperimentModel> unitsWithObservations = Collections.singletonList(instance1Units.get(0));
		final List<ExperimentModel> unitsWithObservations2 = Collections.singletonList(instance1Units.get(1));
		final List<ExperimentModel> unitsWithObservations3 = Collections.singletonList(instance1Units.get(2));
		this.testDataInitializer.addPhenotypes(unitsWithObservations, traitId, "1000");
		this.testDataInitializer.addPhenotypes(unitsWithObservations2, traitId, "100");
		this.testDataInitializer.addPhenotypes(unitsWithObservations3, traitId, "40");

		final MeasurementVariableDto measurementVariableDto = new MeasurementVariableDto(traitId, traitName);
		final ObservationUnitsSearchDTO observationUnitsSearchDTO = this.testDataInitializer.createTestObservationUnitsDTO();
		final Integer datasetId = this.plot.getProjectId();
		observationUnitsSearchDTO.setDatasetId(datasetId);
		observationUnitsSearchDTO.setSelectionMethodsAndTraits(Collections.singletonList(measurementVariableDto));
		observationUnitsSearchDTO.setInstanceId(geolocation.getLocationId());

		final ObservationUnitsSearchDTO.Filter filter = observationUnitsSearchDTO.new Filter();
		filter.setByOutOfBound(true);
		filter.setVariableId(traitId);
		observationUnitsSearchDTO.setFilter(filter);

		// Need to flush session to sync with underlying database before querying
		this.sessionProvder.getSession().flush();

		final List<ObservationUnitRow> measurementRows = this.obsUnitSearchDao.getObservationUnitsByVariable(observationUnitsSearchDTO);
		assertEquals(unitsWithObservations.get(0).getNdExperimentId(), measurementRows.get(0).getObservationUnitId());
		assertEquals(1, measurementRows.size());
	}

	@Test
	public void testFilterByOutOfBoundsAllIsOutOfBoundsMinValue10MaxValue() {
		final String traitName = "MyTrait";

		final Geolocation geolocation = this.testDataInitializer.createTestGeolocation("1", 101);
		final List<ExperimentModel> instance1Units = this.testDataInitializer.createTestExperiments(this.plot, null, geolocation, 3);
		//trait minvalue 10 maxvalue 100
		final Integer traitId = this.createTrait(traitName, "10", "100", "", "");
		final List<ExperimentModel> unitsWithObservations = Collections.singletonList(instance1Units.get(0));
		final List<ExperimentModel> unitsWithObservations2 = Collections.singletonList(instance1Units.get(1));
		final List<ExperimentModel> unitsWithObservations3 = Collections.singletonList(instance1Units.get(2));
		this.testDataInitializer.addPhenotypes(unitsWithObservations, traitId, "1000");
		this.testDataInitializer.addPhenotypes(unitsWithObservations2, traitId, "3000");
		this.testDataInitializer.addPhenotypes(unitsWithObservations3, traitId, "5");

		final MeasurementVariableDto measurementVariableDto = new MeasurementVariableDto(traitId, traitName);
		final ObservationUnitsSearchDTO observationUnitsSearchDTO = this.testDataInitializer.createTestObservationUnitsDTO();
		final Integer datasetId = this.plot.getProjectId();
		observationUnitsSearchDTO.setDatasetId(datasetId);
		observationUnitsSearchDTO.setSelectionMethodsAndTraits(Collections.singletonList(measurementVariableDto));
		observationUnitsSearchDTO.setInstanceId(geolocation.getLocationId());

		final ObservationUnitsSearchDTO.Filter filter = observationUnitsSearchDTO.new Filter();
		filter.setByOutOfBound(true);
		filter.setVariableId(traitId);
		observationUnitsSearchDTO.setFilter(filter);

		// Need to flush session to sync with underlying database before querying
		this.sessionProvder.getSession().flush();

		final List<ObservationUnitRow> measurementRows = this.obsUnitSearchDao.getObservationUnitsByVariable(observationUnitsSearchDTO);
		assertEquals(3, measurementRows.size());
	}



	@Test
	public void testFilterByOutOfBoundsAllIsOutOfBoundsExpectedMinValue10ExpectedMaxValue() {
		final String traitName = "MyTrait";
		final Geolocation geolocation = this.testDataInitializer.createTestGeolocation("1", 101);
		final List<ExperimentModel> instance1Units = this.testDataInitializer.createTestExperiments(this.plot, null, geolocation, 3);
		//trait minvalue 10 maxvalue 100 expected minvalue 0 expected maxvalue 1
		final Integer traitId = this.createTrait(traitName, "10", "100", "0", "1");
		final List<ExperimentModel> unitsWithObservations = Collections.singletonList(instance1Units.get(0));
		final List<ExperimentModel> unitsWithObservations2 = Collections.singletonList(instance1Units.get(1));
		final List<ExperimentModel> unitsWithObservations3 = Collections.singletonList(instance1Units.get(2));
		this.testDataInitializer.addPhenotypes(unitsWithObservations, traitId, "19");
		this.testDataInitializer.addPhenotypes(unitsWithObservations2, traitId, "10");
		this.testDataInitializer.addPhenotypes(unitsWithObservations3, traitId, "5");

		final MeasurementVariableDto measurementVariableDto = new MeasurementVariableDto(traitId, traitName);
		final ObservationUnitsSearchDTO observationUnitsSearchDTO = this.testDataInitializer.createTestObservationUnitsDTO();
		final Integer datasetId = this.plot.getProjectId();
		observationUnitsSearchDTO.setDatasetId(datasetId);
		observationUnitsSearchDTO.setSelectionMethodsAndTraits(Collections.singletonList(measurementVariableDto));
		observationUnitsSearchDTO.setInstanceId(geolocation.getLocationId());

		final ObservationUnitsSearchDTO.Filter filter = observationUnitsSearchDTO.new Filter();
		filter.setByOutOfBound(true);
		filter.setVariableId(traitId);
		observationUnitsSearchDTO.setFilter(filter);

		// Need to flush session to sync with underlying database before querying
		this.sessionProvder.getSession().flush();

		final List<ObservationUnitRow> measurementRows = this.obsUnitSearchDao.getObservationUnitsByVariable(observationUnitsSearchDTO);
		assertEquals(3,measurementRows.size());
	}


	/**
	 * Properly Create Trait
	 * @param traitName
	 * @return cvTermId
	 */
	private Integer createTrait(final String traitName, final String minValue, final String maxValue, final String expectedMinValue, final String expectedMaxValue) {
		final Method method = new Method();
		method.setName(OntologyDataCreationUtil.getNewRandomName());
		method.setDefinition("Test Method");
		this.methodManager.addMethod(method);

		final Property property = new Property();
		property.setName(OntologyDataCreationUtil.getNewRandomName());
		property.setDefinition("Test Property");
		property.setCropOntologyId("CO:0000001");
		property.addClass("My New Class");
		this.propertyManager.addProperty(property);

		final Scale scale = new Scale();
		scale.setName(OntologyDataCreationUtil.getNewRandomName());
		scale.setDefinition("Test Scale");
		scale.setDataType(DataType.NUMERIC_VARIABLE);
		scale.setMinValue(minValue);
		scale.setMaxValue(maxValue);
		this.scaleManager.addScale(scale);

		OntologyVariableInfo variableInfo = new OntologyVariableInfo();
		variableInfo.setProgramUuid(this.plot.getProgramUUID());
		variableInfo.setName(traitName);
		variableInfo.setDescription("Test Variable");
		variableInfo.setMethodId(method.getId());
		variableInfo.setPropertyId(property.getId());
		variableInfo.setScaleId(scale.getId());
		variableInfo.setAlias(traitName);
		variableInfo.setExpectedMin(expectedMinValue);
		variableInfo.setExpectedMax(expectedMaxValue);
		variableInfo.addVariableType(VariableType.GERMPLASM_DESCRIPTOR);
		variableInfo.setIsFavorite(true);
		this.variableManager.addVariable(variableInfo);

		return variableInfo.getId();
	}
}
