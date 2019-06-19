package org.generationcp.middleware.dao.dms;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.domain.sample.SampleDTO;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.pojos.Sample;
import org.generationcp.middleware.pojos.SampleList;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.service.api.dataset.ObservationUnitData;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.generationcp.middleware.service.api.dataset.ObservationUnitsSearchDTO;
import org.generationcp.middleware.service.api.study.MeasurementVariableDto;
import org.generationcp.middleware.utils.test.IntegrationTestDataInitializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ExperimentDaoIntegrationTest extends IntegrationTestBase {

	private static final String UUID_REGEX = "[0-9a-f]{8}-[0-9a-f]{4}-[4][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}";

	private static final String GEOLOCATION_DESCRIPTION = "1";

	private ExperimentDao experimentDao;
	private DmsProjectDao dmsProjectDao;

	private IntegrationTestDataInitializer testDataInitializer;

	private DmsProject study;
	private DmsProject plot;

	@Before
	public void setUp() {

		this.experimentDao = new ExperimentDao();
		this.experimentDao.setSession(this.sessionProvder.getSession());
		this.dmsProjectDao = new DmsProjectDao();
		this.dmsProjectDao.setSession(this.sessionProvder.getSession());

		this.testDataInitializer = new IntegrationTestDataInitializer(this.sessionProvder);
		this.study = this.testDataInitializer.createDmsProject("Study1", "Study-Description", null, this.dmsProjectDao.getById(1), null);
		this.plot = this.testDataInitializer
			.createDmsProject("Plot Dataset", "Plot Dataset-Description", this.study, this.study, DatasetTypeEnum.PLOT_DATA);
	}

	@Test
	public void testSaveOrUpdate() {

		final Geolocation geolocation = this.testDataInitializer.createTestGeolocation("1", 101);
		final List<ExperimentModel> experimentModels = this.testDataInitializer.createTestExperiments(this.plot, null, geolocation, 5);

		// Verify that new experiments have auto-generated UUIDs as values for obs_unit_id
		for (final ExperimentModel experiment : experimentModels) {
			Assert.assertNotNull(experiment.getObsUnitId());
			Assert.assertTrue(experiment.getObsUnitId().matches(UUID_REGEX));
		}
	}

	@Test
	public void testSaveOrUpdateWithCustomObsUnitId() {

		final Geolocation geolocation = this.testDataInitializer.createTestGeolocation("1", 101);
		final ExperimentModel existingExperiment =
			this.testDataInitializer.createTestExperiment(this.plot, geolocation, TermId.PLOT_EXPERIMENT.getId(), null, null);

		// Save a new experiment
		final ExperimentModel experimentModel = new ExperimentModel();
		experimentModel.setGeoLocation(existingExperiment.getGeoLocation());
		experimentModel.setTypeId(TermId.PLOT_EXPERIMENT.getId());
		experimentModel.setProject(this.study);
		experimentModel.setStock(existingExperiment.getStock());
		final String customUnitID = RandomStringUtils.randomAlphabetic(10);
		experimentModel.setObsUnitId(customUnitID);
		this.experimentDao.saveOrUpdate(experimentModel);

		// Verify that custom observation unit IDs are preserved
		Assert.assertNotNull(experimentModel.getObsUnitId());
		assertEquals(customUnitID, experimentModel.getObsUnitId());
	}

	@Test
	public void testSave() {

		final Geolocation geolocation = this.testDataInitializer.createTestGeolocation("1", 101);
		final ExperimentModel existingExperiment =
			this.testDataInitializer.createTestExperiment(this.plot, geolocation, TermId.PLOT_EXPERIMENT.getId(), null, null);

		// Save a new experiment
		final ExperimentModel experimentModel = new ExperimentModel();
		experimentModel.setGeoLocation(existingExperiment.getGeoLocation());
		experimentModel.setTypeId(TermId.PLOT_EXPERIMENT.getId());
		experimentModel.setProject(this.study);
		experimentModel.setStock(existingExperiment.getStock());
		this.experimentDao.save(experimentModel);

		// Verify that new experiment has auto-generated UUIDs as value for obs_unit_id
		Assert.assertNotNull(experimentModel.getObsUnitId());
		Assert.assertTrue(experimentModel.getObsUnitId().matches(UUID_REGEX));
	}

	@Test
	public void testSaveWithCustomObsUnitId() {
		final Geolocation geolocation = this.testDataInitializer.createTestGeolocation("1", 101);
		final ExperimentModel existingExperiment =
			this.testDataInitializer.createTestExperiment(this.plot, geolocation, TermId.PLOT_EXPERIMENT.getId(), null, null);

		// Save a new experiment
		final ExperimentModel experimentModel = new ExperimentModel();
		experimentModel.setGeoLocation(existingExperiment.getGeoLocation());
		experimentModel.setTypeId(TermId.PLOT_EXPERIMENT.getId());
		experimentModel.setProject(this.study);
		experimentModel.setStock(existingExperiment.getStock());
		final String customUnitID = RandomStringUtils.randomAlphabetic(10);
		experimentModel.setObsUnitId(customUnitID);
		this.experimentDao.save(experimentModel);

		// Verify that custom observation unit IDs are preserved
		Assert.assertNotNull(experimentModel.getObsUnitId());
		assertEquals(customUnitID, experimentModel.getObsUnitId());
	}

	@Test
	public void testIsValidExperiment() {
		final Geolocation geolocation = this.testDataInitializer.createTestGeolocation(GEOLOCATION_DESCRIPTION, 101);
		final ExperimentModel experimentModel =
			this.testDataInitializer.createTestExperiment(this.plot, geolocation, TermId.PLOT_EXPERIMENT.getId(), null, null);

		final Integer validExperimentId = experimentModel.getNdExperimentId();
		Assert.assertFalse(this.experimentDao.isValidExperiment(this.study.getProjectId(), validExperimentId));
		Assert.assertFalse(this.experimentDao.isValidExperiment(this.plot.getProjectId(), validExperimentId + 10));
		Assert.assertTrue(this.experimentDao.isValidExperiment(this.plot.getProjectId(), validExperimentId));
	}

	@Test
	public void testCountObservationsPerInstance() {
		final Geolocation geolocation = this.testDataInitializer.createTestGeolocation(GEOLOCATION_DESCRIPTION, 101);
		final List<ExperimentModel> experimentModels = this.testDataInitializer.createTestExperiments(this.plot, null, geolocation, 10);
		final Map<String, Long> result = this.experimentDao.countObservationsPerInstance(this.plot.getProjectId());
		assertEquals(result.get(GEOLOCATION_DESCRIPTION), Long.valueOf(10));
	}

	@Test
	public void testGetExperimentSamplesDTOMap() {

		final Geolocation geolocation = this.testDataInitializer.createTestGeolocation("1", 101);
		final List<ExperimentModel> experimentModels = this.testDataInitializer.createTestExperiments(this.plot, null, geolocation, 1);

		final User user = this.testDataInitializer.createUserForTesting();
		final SampleList sampleList = this.testDataInitializer.createTestSampleList("MyList", user);
		final List<Sample> samples = this.testDataInitializer.addSamples(experimentModels, sampleList, user);

		final Map<Integer, List<SampleDTO>> resultMap = this.experimentDao.getExperimentSamplesDTOMap(this.study.getProjectId());

		assertEquals(1, resultMap.size());
		final SampleDTO sampleDTO = resultMap.values().iterator().next().get(0);
		final Sample sample = samples.get(0);
		assertEquals(sample.getSampleId(), sampleDTO.getSampleId());
		assertEquals(1, sampleDTO.getSampleNumber().intValue());

	}

	@Test
	public void testGetObservationUnitTable() {

		final String traitName = "MyTrait";
		final String observationUnitVariableName = "PLANT_NO";
		final int noOfSubObservationExperiment = 3;

		final CVTerm trait1 = this.testDataInitializer.createTrait(traitName);
		final DmsProject plantSubObsDataset =
			this.testDataInitializer.createDmsProject("Plant SubObs Dataset", "Plot Dataset-Description", this.study, this.plot,
				DatasetTypeEnum.PLANT_SUBOBSERVATIONS);
		this.testDataInitializer.addProjectProp(plantSubObsDataset, 8206, observationUnitVariableName, VariableType.OBSERVATION_UNIT, "", 1);

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
		observationUnitsSearchDTO.setSelectionMethodsAndTraits(Arrays.asList(measurementVariableDto));
		observationUnitsSearchDTO.setFilter(observationUnitsSearchDTO.new Filter());

		// Need to flush session to sync with underlying database before querying
		this.sessionProvder.getSession().flush();

		final List<ObservationUnitRow> measurementRows = this.experimentDao.getObservationUnitTable(observationUnitsSearchDTO);

		assertEquals(noOfSubObservationExperiment, measurementRows.size());

		final ObservationUnitRow observationUnitRow = measurementRows.get(0);

		assertEquals("Germplasm 1", observationUnitRow.getDesignation());
		assertNotNull(observationUnitRow.getGid());
		assertEquals("-", observationUnitRow.getSamplesCount());
		assertNotNull(observationUnitRow.getObsUnitId());

		final Map<String, ObservationUnitData> dataMap = observationUnitRow.getVariables();

		assertEquals("1", dataMap.get(observationUnitVariableName).getValue());
		assertNotNull(dataMap.get(traitName).getValue());
		assertNull(dataMap.get(ExperimentDao.COL).getValue());
		assertEquals(observationUnitRow.getGid().toString(), dataMap.get(ExperimentDao.GID).getValue());
		assertNull(dataMap.get(ExperimentDao.FIELD_MAP_RANGE).getValue());
		assertNull(dataMap.get(ExperimentDao.FIELD_MAP_COLUMN).getValue());
		assertEquals(observationUnitRow.getObsUnitId(), dataMap.get(ExperimentDao.OBS_UNIT_ID).getValue());
		assertEquals(plotExperimentModel.getObsUnitId(), dataMap.get(ExperimentDao.PARENT_OBS_UNIT_ID).getValue());
		assertNull(dataMap.get(ExperimentDao.ENTRY_TYPE).getValue());
		assertNull(dataMap.get(ExperimentDao.EXPT_DESIGN).getValue());
		assertEquals("1", dataMap.get(ExperimentDao.ENTRY_NO).getValue());
		assertEquals("Germplasm 1", dataMap.get(ExperimentDao.DESIGNATION).getValue());
		assertEquals("1", dataMap.get(ExperimentDao.TRIAL_INSTANCE).getValue());
		assertNull(dataMap.get(ExperimentDao.ENTRY_CODE).getValue());
		assertNull(dataMap.get(ExperimentDao.BLOCK_NO).getValue());
		assertEquals("India", dataMap.get(ExperimentDao.LOCATION_ID).getValue());
		assertNull(dataMap.get(ExperimentDao.ROW).getValue());
		assertNull(dataMap.get(ExperimentDao.REP_NO).getValue());
		assertNull(dataMap.get(ExperimentDao.PLOT_NO).getValue());

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
		this.testDataInitializer.addProjectProp(plantSubObsDataset, 8206, observationUnitVariableName, VariableType.OBSERVATION_UNIT, "", 1);

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
		observationUnitsSearchDTO.setSelectionMethodsAndTraits(Arrays.asList(measurementVariableDto));

		// Filter by Overwritten
		ObservationUnitsSearchDTO.Filter filter = observationUnitsSearchDTO.new Filter();
		filter.setVariableId(trait1.getCvTermId());
		filter.setByOverwritten(true);
		observationUnitsSearchDTO.setDraftMode(true);
		observationUnitsSearchDTO.setFilter(filter);

		// Need to flush session to sync with underlying database before querying
		this.sessionProvder.getSession().flush();

		final List<ObservationUnitRow> measurementRows = this.experimentDao.getObservationUnitsByVariable(observationUnitsSearchDTO);

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
	public void testGetLocationIdsOfStudy() {

		final DmsProject someStudy = this.testDataInitializer
			.createDmsProject("Study1", "Study-Description", null, this.dmsProjectDao.getById(1), null);
		final DmsProject someSummary =
			this.testDataInitializer
				.createDmsProject("Summary Dataset", "Summary Dataset-Description", someStudy, someStudy, DatasetTypeEnum.SUMMARY_DATA);

		final Geolocation instance1 = this.testDataInitializer.createTestGeolocation("1", 101);
		final Geolocation instance2 = this.testDataInitializer.createTestGeolocation("2", 102);
		this.testDataInitializer.createTestExperiment(someSummary, instance1, TermId.SUMMARY_EXPERIMENT.getId(), "1", null);
		this.testDataInitializer.createTestExperiment(someSummary, instance2, TermId.SUMMARY_EXPERIMENT.getId(), "2", null);

		final List<Integer> instanceIds = this.experimentDao.getLocationIdsOfStudy(someStudy.getProjectId());
		Assert.assertTrue(instanceIds.contains(instance1.getLocationId()));
		Assert.assertTrue(instanceIds.contains(instance2.getLocationId()));

	}

	@Test
	public void testGetLocationIdsOfStudyWithFieldmap() {

		final DmsProject someStudy = this.testDataInitializer
			.createDmsProject("Study1", "Study-Description", null, this.dmsProjectDao.getById(1), null);
		final DmsProject someSummary =
			this.testDataInitializer
				.createDmsProject("Summary Dataset", "Summary Dataset-Description", someStudy, someStudy, DatasetTypeEnum.SUMMARY_DATA);

		final Geolocation instance1 = this.testDataInitializer.createTestGeolocation("1", 101);
		final Geolocation instance2 = this.testDataInitializer.createTestGeolocation("2", 102);
		final ExperimentModel instance1Experiment =
			this.testDataInitializer.createTestExperiment(someSummary, instance1, TermId.SUMMARY_EXPERIMENT.getId(), "1", null);
		final ExperimentModel instance2Experiment =
			this.testDataInitializer.createTestExperiment(someSummary, instance2, TermId.SUMMARY_EXPERIMENT.getId(), "2", null);

		// Add fieldmap variable to istance 1
		this.testDataInitializer.addExperimentProp(instance1Experiment, TermId.COLUMN_NO.getId(), "1", 1);

		final List<Integer> instanceIds = this.experimentDao.getLocationIdsOfStudyWithFieldmap(someStudy.getProjectId());
		// Only instance 1 has fieldmap.
		Assert.assertTrue(instanceIds.contains(instance1.getLocationId()));
		Assert.assertFalse(instanceIds.contains(instance2.getLocationId()));

	}

}
