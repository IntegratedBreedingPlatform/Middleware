package org.generationcp.middleware.dao.dms;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.Method;
import org.generationcp.middleware.domain.ontology.Property;
import org.generationcp.middleware.domain.ontology.Scale;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.manager.ontology.api.OntologyMethodDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyPropertyDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyScaleDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.manager.ontology.daoElements.OntologyVariableInfo;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.pojos.dms.StockModel;
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
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ObservationUnitsSearchDaoTest extends IntegrationTestBase {

	public static final String COL = "COL";
	public static final String GID = "GID";
	public static final String FIELDMAP_RANGE = "FIELDMAP RANGE";
	public static final String FIELDMAP_COLUMN = "FIELDMAP COLUMN";
	public static final String OBS_UNIT_ID = "OBS_UNIT_ID";
	public static final String ENTRY_TYPE = "ENTRY_TYPE";
	public static final String EXPT_DESIGN = "EXPT_DESIGN";
	public static final String ENTRY_NO = "ENTRY_NO";
	public static final String DESIGNATION = "DESIGNATION";
	public static final String TRIAL_INSTANCE = "TRIAL_INSTANCE";
	public static final String ENTRY_CODE = "ENTRY_CODE";
	public static final String BLOCK_NO = "BLOCK_NO";
	public static final String LOCATION_ID = "LOCATION_ID";
	public static final String ROW = "ROW";
	public static final String REP_NO = "REP_NO";
	public static final String PLOT_NO = "PLOT_NO";
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
		final DmsProjectDao dmsProjectDao = new DmsProjectDao();
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
				.createTestExperimentsWithStock(this.study, plantSubObsDataset, plotExperimentModel, geolocation, noOfSubObservationExperiment);

		this.testDataInitializer.addPhenotypes(plantExperimentModels, trait1.getCvTermId(), RandomStringUtils.randomNumeric(5));

		final MeasurementVariableDto environmentDetailDto =
			new MeasurementVariableDto(environmentDetailVariable.getCvTermId(), environmentDetailVariable.getName());
		final MeasurementVariableDto environmentConditionDto =
			new MeasurementVariableDto(environmentConditionVariable.getCvTermId(), environmentConditionVariable.getName());
		final MeasurementVariableDto measurementVariableDto = new MeasurementVariableDto(trait1.getCvTermId(), trait1.getName());

		final ObservationUnitsSearchDTO observationUnitsSearchDTO = this.testDataInitializer.createTestObservationUnitsDTO();
		observationUnitsSearchDTO.setDatasetId(plantSubObsDataset.getProjectId());
		observationUnitsSearchDTO.setInstanceId(geolocation.getLocationId());
		observationUnitsSearchDTO.setDatasetVariables(Collections.singletonList(measurementVariableDto));
		observationUnitsSearchDTO.setEnvironmentDetails(Collections.singletonList(environmentDetailDto));
		observationUnitsSearchDTO.setEnvironmentConditions(Collections.singletonList(environmentConditionDto));
		observationUnitsSearchDTO.setFilter(observationUnitsSearchDTO.new Filter());
		observationUnitsSearchDTO.setEntryDetails(Arrays.asList(
			new MeasurementVariableDto(TermId.ENTRY_NO.getId(),"ENTRY_NO"), //
			new MeasurementVariableDto(TermId.ENTRY_TYPE.getId(),"ENTRY_TYPE"), //
			new MeasurementVariableDto(TermId.ENTRY_CODE.getId(),"ENTRY_CODE")));

		// Need to flush session to sync with underlying database before querying
		this.sessionProvder.getSession().flush();

		final List<ObservationUnitRow> measurementRows = this.obsUnitSearchDao.getObservationUnitTable(observationUnitsSearchDTO, new PageRequest(0, Integer.MAX_VALUE));

		assertEquals(noOfSubObservationExperiment, measurementRows.size());

		final ObservationUnitRow observationUnitRow = measurementRows.get(0);

		final StockModel stock = plantExperimentModels.get(0).getStock();
		assertEquals(stock.getGermplasm().getGid(), observationUnitRow.getGid());
		assertEquals("-", observationUnitRow.getSamplesCount());
		assertEquals(1, observationUnitRow.getEntryNumber().intValue());
		assertEquals(1, observationUnitRow.getTrialInstance().intValue());

		assertNotNull(observationUnitRow.getObsUnitId());

		final Map<String, ObservationUnitData> dataMap = observationUnitRow.getVariables();

		assertEquals("1", dataMap.get(observationUnitVariableName).getValue());
		assertNotNull(dataMap.get(traitName).getValue());
		assertNull(dataMap.get(COL).getValue());
		assertEquals(observationUnitRow.getGid().toString(), dataMap.get(GID).getValue());
		assertNull(dataMap.get(FIELDMAP_RANGE).getValue());
		assertNull(dataMap.get(FIELDMAP_COLUMN).getValue());
		assertEquals(observationUnitRow.getObsUnitId(), dataMap.get(OBS_UNIT_ID).getValue());
		assertEquals(plotExperimentModel.getObsUnitId(), dataMap.get(ObservationUnitsSearchDao.PARENT_OBS_UNIT_ID).getValue());
		assertNull(dataMap.get(ENTRY_TYPE).getValue());
		assertNull(dataMap.get(EXPT_DESIGN).getValue());
		assertEquals("1", dataMap.get(ENTRY_NO).getValue());
		assertEquals("1", dataMap.get(TRIAL_INSTANCE).getValue());
		assertNull(dataMap.get(ENTRY_CODE).getValue());
		assertNull(dataMap.get(BLOCK_NO).getValue());
		assertEquals("India", dataMap.get(LOCATION_ID).getValue());
		assertNull(dataMap.get(ROW).getValue());
		assertNull(dataMap.get(REP_NO).getValue());
		assertNull(dataMap.get(PLOT_NO).getValue());

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
				.createTestExperimentsWithStock(this.study, plantSubObsDataset, plotExperimentModel, geolocation, noOfSubObservationExperiment);

		this.testDataInitializer.addPhenotypes(plantExperimentModels, trait1.getCvTermId(), RandomStringUtils.randomNumeric(5));

		final MeasurementVariableDto measurementVariableDto = new MeasurementVariableDto(trait1.getCvTermId(), trait1.getName());

		final ObservationUnitsSearchDTO observationUnitsSearchDTO = this.testDataInitializer.createTestObservationUnitsDTO();
		observationUnitsSearchDTO.setDatasetId(plantSubObsDataset.getProjectId());
		observationUnitsSearchDTO.setInstanceId(geolocation.getLocationId());
		observationUnitsSearchDTO.setDatasetVariables(Collections.singletonList(measurementVariableDto));

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
				.createTestExperimentsWithStock(this.study, plantSubObsDataset, plotExperimentModel1, geolocation, noOfSubObservationExperiment);

		final Geolocation geolocation2 = this.testDataInitializer.createTestGeolocation("2", 101);
		final ExperimentModel plotExperimentModel2 =
			this.testDataInitializer.createTestExperiment(this.plot, geolocation2, TermId.PLOT_EXPERIMENT.getId(), null, null);
		final List<ExperimentModel> subObsExperimentsInstance2 = this.testDataInitializer
			.createTestExperimentsWithStock(this.study, plantSubObsDataset, plotExperimentModel2, geolocation2, noOfSubObservationExperiment);

		// Only first instance has observations
		final CVTerm trait1 = this.testDataInitializer.createTrait(traitName);
		this.testDataInitializer.addPhenotypes(subObsExperimentsInstance1, trait1.getCvTermId(), RandomStringUtils.randomNumeric(5));

		final MeasurementVariableDto measurementVariableDto = new MeasurementVariableDto(trait1.getCvTermId(), trait1.getName());

		final ObservationUnitsSearchDTO observationUnitsSearchDTO = this.testDataInitializer.createTestObservationUnitsDTO();
		final Integer datasetId = plantSubObsDataset.getProjectId();
		observationUnitsSearchDTO.setDatasetId(datasetId);
		observationUnitsSearchDTO.setDatasetVariables(Collections.singletonList(measurementVariableDto));

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
		filteredTextValues.put(String.valueOf(TermId.DESIG.getId()), "Name ");
		assertEquals(noOfSubObservationExperiment,
			this.obsUnitSearchDao.countObservationUnitsForDataset(datasetId, null, false, filter).intValue());
	}

	@Test
	public void testCountObservationUnitsForPlotDataset() {

		final String traitName = "MyTrait";

		final int numberOfPlotExperiments = 3;
		final Geolocation geolocation = this.testDataInitializer.createTestGeolocation("1", 101);
		final List<ExperimentModel> instance1Units =
			this.testDataInitializer.createTestExperimentsWithStock(this.study, this.plot, null, geolocation, numberOfPlotExperiments);
		final Geolocation geolocation2 = this.testDataInitializer.createTestGeolocation("2", 101);
		final List<ExperimentModel> instance2Units =
			this.testDataInitializer.createTestExperimentsWithStock(this.study, this.plot, null, geolocation2, numberOfPlotExperiments);

		// Only 2 experiments in first instance have observations
		final CVTerm trait1 = this.testDataInitializer.createTrait(traitName);
		final List<ExperimentModel> unitsWithObservations = Arrays.asList(instance1Units.get(0), instance1Units.get(1));
		this.testDataInitializer.addPhenotypes(unitsWithObservations, trait1.getCvTermId(), RandomStringUtils.randomNumeric(5));

		final MeasurementVariableDto measurementVariableDto = new MeasurementVariableDto(trait1.getCvTermId(), trait1.getName());
		final ObservationUnitsSearchDTO observationUnitsSearchDTO = this.testDataInitializer.createTestObservationUnitsDTO();
		final Integer datasetId = this.plot.getProjectId();
		observationUnitsSearchDTO.setDatasetId(datasetId);
		observationUnitsSearchDTO.setDatasetVariables(Collections.singletonList(measurementVariableDto));

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
		filteredTextValues.put(String.valueOf(TermId.DESIG.getId()), "Name ");
		assertEquals(3,
			this.obsUnitSearchDao.countObservationUnitsForDataset(datasetId, null, false, filter).intValue());
	}

	@Test
	public void testFilterByOutOfBoundsWithoutOutOfBounds() {
		final String traitName = "MyTrait";

		final Geolocation geolocation = this.testDataInitializer.createTestGeolocation("1", 101);
		final List<ExperimentModel> instance1Units = this.testDataInitializer.createTestExperimentsWithStock(this.study, this.plot, null, geolocation, 2);
		final Integer traitId = this.createTrait(traitName);
		final List<ExperimentModel> unitsWithObservations = Collections.singletonList(instance1Units.get(0));
		final List<ExperimentModel> unitsWithObservations2 = Collections.singletonList(instance1Units.get(1));
		this.testDataInitializer.addPhenotypes(unitsWithObservations, traitId, "40");
		this.testDataInitializer.addPhenotypes(unitsWithObservations2, traitId, "100");

		final MeasurementVariableDto measurementVariableDto = new MeasurementVariableDto(traitId, traitName);
		final ObservationUnitsSearchDTO observationUnitsSearchDTO = this.testDataInitializer.createTestObservationUnitsDTO();
		final Integer datasetId = this.plot.getProjectId();
		observationUnitsSearchDTO.setDatasetId(datasetId);
		observationUnitsSearchDTO.setDatasetVariables(Collections.singletonList(measurementVariableDto));
		observationUnitsSearchDTO.setInstanceId(geolocation.getLocationId());

		final ObservationUnitsSearchDTO.Filter filter = observationUnitsSearchDTO.new Filter();
		filter.setByOutOfBound(true);
		filter.setVariableId(traitId);
		observationUnitsSearchDTO.setFilter(filter);

		// Need to flush session to sync with underlying database before querying
		this.sessionProvder.getSession().flush();

		final List<ObservationUnitRow> measurementRows = this.obsUnitSearchDao.getObservationUnitsByVariable(observationUnitsSearchDTO);
		assertEquals(0, measurementRows.size());
	}

	@Test
	public void testFilterByOutOfBoundsSomeOutOfBounds() {
		final String traitName = "MyTrait";

		final Geolocation geolocation = this.testDataInitializer.createTestGeolocation("1", 101);
		final List<ExperimentModel> instance1Units = this.testDataInitializer.createTestExperimentsWithStock(this.study, this.plot, null, geolocation, 3);
		final Integer traitId = this.createTrait(traitName);
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
		observationUnitsSearchDTO.setDatasetVariables(Collections.singletonList(measurementVariableDto));
		observationUnitsSearchDTO.setInstanceId(geolocation.getLocationId());

		final ObservationUnitsSearchDTO.Filter filter = observationUnitsSearchDTO.new Filter();
		filter.setByOutOfBound(true);
		filter.setVariableId(traitId);
		observationUnitsSearchDTO.setFilter(filter);

		// Need to flush session to sync with underlying database before querying
		this.sessionProvder.getSession().flush();

		final List<ObservationUnitRow> measurementRows = this.obsUnitSearchDao.getObservationUnitsByVariable(observationUnitsSearchDTO);
		assertEquals(1, measurementRows.size());
	}

	@Test
	public void testFilterByOutOfBoundsAllIsOutOfBounds() {
		final String traitName = "MyTrait";

		final Geolocation geolocation = this.testDataInitializer.createTestGeolocation("1", 101);
		final List<ExperimentModel> instance1Units = this.testDataInitializer.createTestExperimentsWithStock(this.study, this.plot, null, geolocation, 4);
		final Integer traitId = this.createTrait(traitName);
		final List<ExperimentModel> unitsWithObservations = Collections.singletonList(instance1Units.get(0));
		final List<ExperimentModel> unitsWithObservations2 = Collections.singletonList(instance1Units.get(1));
		final List<ExperimentModel> unitsWithObservations3 = Collections.singletonList(instance1Units.get(2));
		final List<ExperimentModel> unitsWithObservations4 = Collections.singletonList(instance1Units.get(3));
		this.testDataInitializer.addPhenotypes(unitsWithObservations, traitId, "1000");
		this.testDataInitializer.addPhenotypes(unitsWithObservations2, traitId, "3000");
		this.testDataInitializer.addPhenotypes(unitsWithObservations3, traitId, "5");
		this.testDataInitializer.addPhenotypes(unitsWithObservations4, traitId, "100.1");
		final MeasurementVariableDto measurementVariableDto = new MeasurementVariableDto(traitId, traitName);
		final ObservationUnitsSearchDTO observationUnitsSearchDTO = this.testDataInitializer.createTestObservationUnitsDTO();
		final Integer datasetId = this.plot.getProjectId();
		observationUnitsSearchDTO.setDatasetId(datasetId);
		observationUnitsSearchDTO.setDatasetVariables(Collections.singletonList(measurementVariableDto));
		observationUnitsSearchDTO.setInstanceId(geolocation.getLocationId());

		final ObservationUnitsSearchDTO.Filter filter = observationUnitsSearchDTO.new Filter();
		filter.setByOutOfBound(true);
		filter.setVariableId(traitId);
		observationUnitsSearchDTO.setFilter(filter);

		// Need to flush session to sync with underlying database before querying
		this.sessionProvder.getSession().flush();

		final List<ObservationUnitRow> measurementRows = this.obsUnitSearchDao.getObservationUnitsByVariable(observationUnitsSearchDTO);
		assertEquals(4, measurementRows.size());
	}

	@Test
	public void testGetObservationUnitRowsAsMapList() {

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

		final ExperimentModel environmentExperimentModel =
			this.testDataInitializer.createTestExperiment(this.summary, geolocation, TermId.SUMMARY_EXPERIMENT.getId(), null, null);
		final ExperimentModel plotExperimentModel =
			this.testDataInitializer.createTestExperiment(this.plot, geolocation, TermId.PLOT_EXPERIMENT.getId(), null, null);
		final List<ExperimentModel> plantExperimentModels =
			this.testDataInitializer
				.createTestExperimentsWithStock(this.study, plantSubObsDataset, plotExperimentModel, geolocation, noOfSubObservationExperiment);

		this.testDataInitializer.addPhenotypes(plantExperimentModels, trait1.getCvTermId(), RandomStringUtils.randomNumeric(5));

		final MeasurementVariableDto measurementVariableDto = new MeasurementVariableDto(trait1.getCvTermId(), trait1.getName());

		final ObservationUnitsSearchDTO observationUnitsSearchDTO = this.testDataInitializer.createTestObservationUnitsDTO();
		observationUnitsSearchDTO.setDatasetId(plantSubObsDataset.getProjectId());
		observationUnitsSearchDTO.setInstanceId(geolocation.getLocationId());
		observationUnitsSearchDTO.setDatasetVariables(Collections.singletonList(measurementVariableDto));
		observationUnitsSearchDTO.setFilter(observationUnitsSearchDTO.new Filter());
		observationUnitsSearchDTO.setEntryDetails(Arrays.asList(
			new MeasurementVariableDto(TermId.ENTRY_NO.getId(),"ENTRY_NO"), //
			new MeasurementVariableDto(TermId.ENTRY_TYPE.getId(),"ENTRY_TYPE"), //
			new MeasurementVariableDto(TermId.ENTRY_CODE.getId(),"ENTRY_CODE")));
		final List<String> filterColumns = observationUnitsSearchDTO.getFilterColumns();

		// Add the columns we want the query to return.
		filterColumns.add(TRIAL_INSTANCE);
		filterColumns.add(GID);
		filterColumns.add(DESIGNATION);
		filterColumns.add(ENTRY_TYPE);
		filterColumns.add(ENTRY_CODE);
		filterColumns.add(ENTRY_NO);
		filterColumns.add(REP_NO);
		filterColumns.add(PLOT_NO);
		filterColumns.add(BLOCK_NO);
		filterColumns.add(ROW);
		filterColumns.add(COL);
		filterColumns.add(ObservationUnitsSearchDao.SUM_OF_SAMPLES);
		filterColumns.add(observationUnitVariableName);
		filterColumns.add(FIELDMAP_RANGE);
		filterColumns.add(FIELDMAP_COLUMN);
		filterColumns.add(traitName);

		// Need to flush session to sync with underlying database before querying
		this.sessionProvder.getSession().flush();

		final List<Map<String, Object>> measurementRows =
			this.obsUnitSearchDao.getObservationUnitTableMapList(observationUnitsSearchDTO, new PageRequest(0, Integer.MAX_VALUE));

		assertEquals(noOfSubObservationExperiment, measurementRows.size());

		final Map<String, Object> dataMap = measurementRows.get(0);

		final StockModel stock = plantExperimentModels.get(0).getStock();

		assertEquals("1", dataMap.get(TRIAL_INSTANCE));
		assertEquals(stock.getGermplasm().getGid(), dataMap.get(GID));
		assertNull(dataMap.get(ENTRY_TYPE));
		assertNull(dataMap.get(ENTRY_CODE));
		assertEquals("1", dataMap.get(ENTRY_NO));
		assertNull(dataMap.get(REP_NO));
		assertNull(dataMap.get(PLOT_NO));
		assertNull(dataMap.get(BLOCK_NO));
		assertNull(dataMap.get(ROW));
		assertNull(dataMap.get(COL));
		assertEquals("-", dataMap.get(ObservationUnitsSearchDao.SUM_OF_SAMPLES));
		assertEquals("1", dataMap.get(observationUnitVariableName));
		assertNull(dataMap.get(FIELDMAP_RANGE));
		assertNull(dataMap.get(FIELDMAP_COLUMN));
		assertNotNull(dataMap.get(traitName));

	}

	@Test
	public void testConvertSelectionAndTraitColumnsValueType() {

		final MeasurementVariableDto trait1 = new MeasurementVariableDto(1, "Trait1");
		final MeasurementVariableDto trait2 = new MeasurementVariableDto(2, "Trait2");
		final MeasurementVariableDto trait3 = new MeasurementVariableDto(3, "Trait3");
		final List<MeasurementVariableDto> selectionAndTraits = Arrays.asList(trait1, trait2, trait3);

		final Map<String, Object> dataMap = new HashMap<>();
		dataMap.put(TRIAL_INSTANCE, "1");
		dataMap.put(GID, 1);
		dataMap.put(trait1.getName(), "1");
		dataMap.put(trait2.getName(), "ABC");
		dataMap.put(trait3.getName(), Phenotype.MISSING_VALUE);

		final List<Map<String, Object>> listMap = Arrays.asList(dataMap);

		final List<Map<String, Object>> result =
			this.obsUnitSearchDao.convertSelectionAndTraitColumnsValueType(listMap, selectionAndTraits);

		final Map<String, Object> resultDataMap = result.get(0);
		assertEquals("1", resultDataMap.get(TRIAL_INSTANCE));
		assertEquals(1, resultDataMap.get(GID));
		assertEquals(BigDecimal.valueOf(1), resultDataMap.get(trait1.getName()));
		assertEquals("ABC", resultDataMap.get(trait2.getName()));
		assertEquals(null, resultDataMap.get(trait3.getName()));

	}

	/**
	 * Properly Create Trait
	 *
	 * @param traitName
	 * @return cvTermId
	 */
	private Integer createTrait(final String traitName) {
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
		scale.setMinValue("10");
		scale.setMaxValue("100");
		this.scaleManager.addScale(scale);

		final OntologyVariableInfo variableInfo = new OntologyVariableInfo();
		variableInfo.setProgramUuid(this.plot.getProgramUUID());
		variableInfo.setName(traitName);
		variableInfo.setDescription("Test Variable");
		variableInfo.setMethodId(method.getId());
		variableInfo.setPropertyId(property.getId());
		variableInfo.setScaleId(scale.getId());
		variableInfo.setAlias(traitName);
		variableInfo.setExpectedMin("");
		variableInfo.setExpectedMax("");
		variableInfo.addVariableType(VariableType.GERMPLASM_DESCRIPTOR);
		variableInfo.setIsFavorite(true);
		this.variableManager.addVariable(variableInfo);

		return variableInfo.getId();
	}
}
