package org.generationcp.middleware.service.impl.study.generation;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.GermplasmTestDataGenerator;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.data.initializer.CVTermTestDataInitializer;
import org.generationcp.middleware.data.initializer.StudyTestDataInitializer;
import org.generationcp.middleware.domain.dms.ExperimentDesignType;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.gms.SystemDefinedEntryType;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.StudyDataManagerImpl;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.GeolocationProperty;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.service.api.dataset.DatasetService;
import org.generationcp.middleware.service.api.dataset.ObservationUnitData;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.generationcp.middleware.service.api.study.generation.ExperimentDesignService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExperimentDesignServiceImplTest extends IntegrationTestBase {

	private static final Integer NO_INSTANCES = 3;
	private static final Integer NO_REPS = 2;
	private static final Integer NO_ENTRIES = 5;
	private static final Integer NO_TREATMENTS = 3;
	private static final List<TermId> GERMPLASM_VARIABLES =	Arrays.asList(TermId.GID, TermId.DESIG);
	private static final List<TermId> PLOT_VARIABLES = Arrays.asList(TermId.PLOT_NO, TermId.REP_NO);
	private static final List<TermId> ENTRY_DETAIL_VARIABLES = Arrays.asList(TermId.ENTRY_TYPE, TermId.ENTRY_NO);
	public static final String LOCATION_ID = "9011";

	@Autowired
	private OntologyDataManager ontologyManager;

	@Autowired
	private WorkbenchTestDataUtil workbenchTestDataUtil;

	private DaoFactory daoFactory;
	private StudyDataManagerImpl studyDataManager;
	private StudyTestDataInitializer studyTDI;

	@Resource
	private DatasetService datasetService;

	private GermplasmTestDataGenerator germplasmTestDataGenerator;

	private Project commonTestProject;
	private StudyReference studyReference;
	private Integer studyId;
	private Integer plotDatasetId;
	private Integer environmentDatasetId;
	private CVTerm treatmentFactor;
	private CVTerm treatmentFactorLabel;
	private Integer[] gids;

	@Autowired
	private ExperimentDesignService experimentDesignService;

	@Before
	public void setup() throws Exception {
		this.studyDataManager = new StudyDataManagerImpl(this.sessionProvder);
		this.daoFactory = new DaoFactory(this.sessionProvder);

		if (this.commonTestProject == null) {
			this.commonTestProject = this.workbenchTestDataUtil.getCommonTestProject();
		}

		if (this.germplasmTestDataGenerator == null) {
			this.germplasmTestDataGenerator = new GermplasmTestDataGenerator(this.sessionProvder, daoFactory);
		}

		this.studyTDI =
			new StudyTestDataInitializer(this.studyDataManager, this.ontologyManager, this.commonTestProject,
				this.sessionProvder);

		// Create a study with environments
		if (this.studyReference == null) {
			this.studyReference = this.studyTDI.addTestStudy();
			this.studyId = this.studyReference.getId();
			this.environmentDatasetId = this.studyTDI.createEnvironmentDataset(new CropType(), this.studyId, LOCATION_ID, null);
			for (int i = 1; i < NO_INSTANCES; i++) {
				this.studyTDI.addEnvironmentToDataset(new CropType(), this.environmentDatasetId, i + 1, LOCATION_ID, null);
			}
			this.plotDatasetId = this.studyTDI.addTestDataset(this.studyId, DatasetTypeEnum.PLOT_DATA.getId()).getId();
			this.treatmentFactor = this.daoFactory.getCvTermDao()
				.save(CVTermTestDataInitializer.createTerm(RandomStringUtils.randomAlphanumeric(50), CvId.VARIABLES.getId()));
			this.treatmentFactorLabel = this.daoFactory.getCvTermDao()
				.save(CVTermTestDataInitializer.createTerm(RandomStringUtils.randomAlphanumeric(50), CvId.VARIABLES.getId()));
		}

		if (this.gids == null) {
			final Germplasm parentGermplasm = this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames();
			this.gids = this.germplasmTestDataGenerator
				.createChildrenGermplasm(NO_ENTRIES, "PREF-ABC", parentGermplasm);
			this.studyTDI.addStudyGermplasm(this.studyId, 1, Arrays.asList(gids));
		}
	}

	@Test
	public void testSaveExperimentDesign_ForAllStudyInstancesAtOnce() {
		final List<Integer> instanceNumbers = Arrays.asList(1, 2, 3);
		this.experimentDesignService
			.saveExperimentDesign(new CropType(), this.studyId, this.createMeasurementVariables(), this.createObservationUnitRows(
				instanceNumbers));
		this.sessionProvder.getSession().flush();
		final List<ObservationUnitRow> rows = this.datasetService.getAllObservationUnitRows(this.studyId, this.plotDatasetId, null);
		Assert.assertNotNull(rows);

		// Verify saving of variables
		this.verifyEnvironmentVariablesWereSaved();
		this.verifyPlotVariablesWereSaved();
		this.verifyGeolocationPropRecords(true, instanceNumbers);

		// Check that plot experiments are created per instance
		Assert.assertEquals(NO_INSTANCES * NO_ENTRIES * NO_REPS * NO_TREATMENTS, rows.size());
		final Map<Integer, List<ObservationUnitRow>> instancesRowMap = new HashMap<>();
		for (final ObservationUnitRow row : rows) {
			final Integer trialInstance = row.getTrialInstance();
			if (instancesRowMap.get(trialInstance) == null) {
				instancesRowMap.put(trialInstance, new ArrayList<ObservationUnitRow>());
			}
			instancesRowMap.get(trialInstance).add(row);
		}
		Assert.assertEquals(NO_ENTRIES * NO_REPS * NO_TREATMENTS, instancesRowMap.get(1).size());
		Assert.assertEquals(NO_ENTRIES * NO_REPS * NO_TREATMENTS, instancesRowMap.get(2).size());
		Assert.assertEquals(NO_ENTRIES * NO_REPS * NO_TREATMENTS, instancesRowMap.get(3).size());

		this.verifyObservationUnitRowValues(instancesRowMap);
	}

	@Test
	public void testSaveExperimentDesign_IterativeForAllStudyInstances() {
		// Save design first instance
		this.experimentDesignService
			.saveExperimentDesign(new CropType(), this.studyId, this.createMeasurementVariables(), this.createObservationUnitRows(Collections.singletonList(1)));

		// Save design for other instances
		this.experimentDesignService
			.saveExperimentDesign(new CropType(), this.studyId, this.createMeasurementVariables(), this.createObservationUnitRows(Arrays.asList(2, 3)));

		this.sessionProvder.getSession().flush();

		final List<ObservationUnitRow> rows = this.datasetService.getAllObservationUnitRows(this.studyId, this.plotDatasetId, null);
		Assert.assertNotNull(rows);

		// Verify saving of variables
		this.verifyEnvironmentVariablesWereSaved();
		this.verifyPlotVariablesWereSaved();
		this.verifyGeolocationPropRecords(true, Arrays.asList(1, 2, 3));

		// Check that plot experiments are created per instance
		Assert.assertEquals(NO_INSTANCES * NO_ENTRIES * NO_REPS * NO_TREATMENTS, rows.size());
		final Map<Integer, List<ObservationUnitRow>> instancesRowMap = new HashMap<>();
		for (final ObservationUnitRow row : rows) {
			final Integer trialInstance = row.getTrialInstance();
			if (instancesRowMap.get(trialInstance) == null) {
				instancesRowMap.put(trialInstance, new ArrayList<ObservationUnitRow>());
			}
			instancesRowMap.get(trialInstance).add(row);
		}
		Assert.assertEquals(NO_ENTRIES * NO_REPS * NO_TREATMENTS, instancesRowMap.get(1).size());
		Assert.assertEquals(NO_ENTRIES * NO_REPS * NO_TREATMENTS, instancesRowMap.get(2).size());
		Assert.assertEquals(NO_ENTRIES * NO_REPS * NO_TREATMENTS, instancesRowMap.get(3).size());

		this.verifyObservationUnitRowValues(instancesRowMap);
	}

	@Test
	public void testSaveExperimentDesign_IterativeAndRegeneratePreviousInstance() {
		// Save design - first 2 instances
		this.experimentDesignService
			.saveExperimentDesign(new CropType(), this.studyId, this.createMeasurementVariables(), this.createObservationUnitRows((Arrays.asList(1, 2))));
		this.sessionProvder.getSession().flush();

		final List<ObservationUnitRow> previousRows = this.datasetService.getAllObservationUnitRows(this.studyId, this.plotDatasetId, null);
		Assert.assertEquals(2 * NO_ENTRIES * NO_REPS * NO_TREATMENTS, previousRows.size());
		// Save fieldmap info for instance1
		final Integer geolocationId1 = this.daoFactory.getGeolocationDao()
			.getEnvironmentGeolocationsForInstances(this.studyId, Collections.singletonList(1)).get(0).getLocationId();
		Assert.assertFalse(this.daoFactory.getGeolocationPropertyDao()
			.getGeoLocationPropertyByVariableId(this.environmentDatasetId, geolocationId1)
			.containsKey(TermId.BLOCK_ID.getId()));
		this.daoFactory.getGeolocationPropertyDao().save(this.createGeolocationProperty(geolocationId1, TermId.BLOCK_ID.getId(), RandomStringUtils.randomAlphabetic(5)));
		Assert.assertTrue(this.daoFactory.getGeolocationPropertyDao()
			.getGeoLocationPropertyByVariableId(this.environmentDatasetId, geolocationId1)
			.containsKey(TermId.BLOCK_ID.getId()));

		// Save design - overwrite first instance, generate experiments for 3rd
		this.experimentDesignService
			.saveExperimentDesign(new CropType(), this.studyId, this.createMeasurementVariables(), this.createObservationUnitRows(Arrays.asList(1, 3)));

		// Need to flush session to sync with underlying database before querying
		this.sessionProvder.getSession().flush();
		final List<ObservationUnitRow> rows = this.datasetService.getAllObservationUnitRows(this.studyId, this.plotDatasetId, null);
		Assert.assertNotNull(rows);

		// Verify saving of variables
		this.verifyEnvironmentVariablesWereSaved();
		this.verifyPlotVariablesWereSaved();
		this.verifyGeolocationPropRecords(true, Arrays.asList(1, 2, 3));
		final Map<Integer, String> map = this.daoFactory.getGeolocationPropertyDao()
			.getGeoLocationPropertyByVariableId(this.environmentDatasetId, geolocationId1);
		for (final Integer id : map.keySet()) {
			System.out.println("TEST ASSERTION LOC = " + geolocationId1 + ":: Found geolocprop variable= " + id);
		}
		Assert.assertFalse(map
			.containsKey(TermId.BLOCK_ID.getId()));

		// Check that plot experiments are created per instance
		Assert.assertEquals(NO_INSTANCES * NO_ENTRIES * NO_REPS * NO_TREATMENTS, rows.size());
		final Map<Integer, List<ObservationUnitRow>> instancesRowMap = new HashMap<>();
		for (final ObservationUnitRow row : rows) {
			final Integer trialInstance = row.getTrialInstance();
			if (instancesRowMap.get(trialInstance) == null) {
				instancesRowMap.put(trialInstance, new ArrayList<ObservationUnitRow>());
			}
			instancesRowMap.get(trialInstance).add(row);
		}
		Assert.assertEquals(NO_ENTRIES * NO_REPS * NO_TREATMENTS, instancesRowMap.get(1).size());
		Assert.assertEquals(NO_ENTRIES * NO_REPS * NO_TREATMENTS, instancesRowMap.get(2).size());
		Assert.assertEquals(NO_ENTRIES * NO_REPS * NO_TREATMENTS, instancesRowMap.get(3).size());

		this.verifyObservationUnitRowValues(instancesRowMap);
	}

	@Test
	public void testSaveExperimentDesign_SubsetOfInstances() {
		final List<Integer> instanceNumbers = Arrays.asList(1, 3);
		final Map<Integer, List<ObservationUnitRow>> instanceRowsMap = this.createObservationUnitRows(instanceNumbers);
		this.experimentDesignService
			.saveExperimentDesign(new CropType(), this.studyId, this.createMeasurementVariables(), instanceRowsMap);
		this.sessionProvder.getSession().flush();

		final List<ObservationUnitRow> rows = this.datasetService.getAllObservationUnitRows(this.studyId, this.plotDatasetId, null);
		Assert.assertNotNull(rows);

		// Verify saving of variables
		this.verifyEnvironmentVariablesWereSaved();
		this.verifyPlotVariablesWereSaved();
		this.verifyGeolocationPropRecords(true, instanceNumbers);

		// Check that plot experiments are created for selected instances
		Assert.assertEquals(instanceRowsMap.keySet().size() * NO_ENTRIES * NO_REPS * NO_TREATMENTS, rows.size());
		final Map<Integer, List<ObservationUnitRow>> instancesRowMap = new HashMap<>();
		for (final ObservationUnitRow row : rows) {
			final Integer trialInstance = row.getTrialInstance();
			if (instancesRowMap.get(trialInstance) == null) {
				instancesRowMap.put(trialInstance, new ArrayList<ObservationUnitRow>());
			}
			instancesRowMap.get(trialInstance).add(row);
		}
		Assert.assertEquals(NO_ENTRIES * NO_REPS * NO_TREATMENTS, instancesRowMap.get(1).size());
		Assert.assertNull(instancesRowMap.get(2));
		Assert.assertEquals(NO_ENTRIES * NO_REPS * NO_TREATMENTS, instancesRowMap.get(3).size());

		this.verifyObservationUnitRowValues(instancesRowMap);
	}

	@Test
	public void testDeleteExperimentDesign() {
		// Save design first, then delete
		final List<Integer> instanceNumbers = Arrays.asList(1, 2, 3);
		this.experimentDesignService
			.saveExperimentDesign(new CropType(), this.studyId, this.createMeasurementVariables(), this.createObservationUnitRows(
				instanceNumbers));
		// Add fieldmap-related environment variables
		this.daoFactory.getProjectPropertyDAO().save(
			new ProjectProperty(new DmsProject(this.environmentDatasetId), VariableType.ENVIRONMENT_DETAIL.getId(), null, 1,
				TermId.BLOCK_ID.getId(), "BLOCK_ID"));
		final Geolocation geolocation1 = this.daoFactory.getGeolocationDao()
			.getEnvironmentGeolocationsForInstances(this.studyId, Collections.singletonList(1)).get(0);
		this.daoFactory.getGeolocationPropertyDao().save(this.createGeolocationProperty(geolocation1.getLocationId(), TermId.BLOCK_ID.getId(), RandomStringUtils.randomAlphabetic(5)));

		List<Integer> environmentVariableIds =
			this.daoFactory.getProjectPropertyDAO().getVariableIdsForDataset(this.environmentDatasetId);
		Assert.assertTrue(environmentVariableIds.contains(TermId.EXPERIMENT_DESIGN_FACTOR.getId()));
		Assert.assertTrue(environmentVariableIds.contains(TermId.NUMBER_OF_REPLICATES.getId()));
		Assert.assertTrue(environmentVariableIds.contains(TermId.BLOCK_ID.getId()));
		Assert.assertTrue(this.daoFactory.getGeolocationPropertyDao()
			.getGeoLocationPropertyByVariableId(this.environmentDatasetId,geolocation1.getLocationId())
			.containsKey(TermId.BLOCK_ID.getId()));

		// Delete experiment design
		this.experimentDesignService.deleteStudyExperimentDesign(this.studyId);

		final List<ObservationUnitRow> rows = this.datasetService.getAllObservationUnitRows(this.studyId, this.plotDatasetId, null);
		Assert.assertTrue(rows.isEmpty());

		environmentVariableIds =
			this.daoFactory.getProjectPropertyDAO().getVariableIdsForDataset(this.environmentDatasetId);
		Assert.assertFalse(environmentVariableIds.contains(TermId.EXPERIMENT_DESIGN_FACTOR.getId()));
		Assert.assertFalse(environmentVariableIds.contains(TermId.NUMBER_OF_REPLICATES.getId()));
		Assert.assertFalse(environmentVariableIds.contains(TermId.BLOCK_ID.getId()));

		final List<Integer> plotVariableIds =
			this.daoFactory.getProjectPropertyDAO().getVariableIdsForDataset(this.plotDatasetId);
		for (final TermId variable : PLOT_VARIABLES) {
			Assert.assertFalse(plotVariableIds.contains(variable.getId()));
		}
		Assert.assertFalse(plotVariableIds.contains(this.treatmentFactor.getCvTermId()));
		Assert.assertFalse(plotVariableIds.contains(this.treatmentFactorLabel.getCvTermId()));

		this.verifyGeolocationPropRecords(false, instanceNumbers);
	}

	@Test
	public void testDeleteThenSaveExperimentDesignAgain() {
		// Save design first
		List<Integer> instanceNumbers = Arrays.asList(1, 2, 3);
		this.experimentDesignService
			.saveExperimentDesign(new CropType(), this.studyId, this.createMeasurementVariables(), this.createObservationUnitRows(
				instanceNumbers));

		// Delete Design
		this.experimentDesignService.deleteStudyExperimentDesign(this.studyId);
		List<ObservationUnitRow> rows = this.datasetService.getAllObservationUnitRows(this.studyId, this.plotDatasetId, null);
		Assert.assertTrue(rows.isEmpty());

		final List<Integer> environmentVariableIds =
			this.daoFactory.getProjectPropertyDAO().getVariableIdsForDataset(this.environmentDatasetId);
		Assert.assertFalse(environmentVariableIds.contains(TermId.EXPERIMENT_DESIGN_FACTOR.getId()));
		Assert.assertFalse(environmentVariableIds.contains(TermId.NUMBER_OF_REPLICATES.getId()));

		final List<Integer> plotVariableIds =
			this.daoFactory.getProjectPropertyDAO().getVariableIdsForDataset(this.plotDatasetId);
		for (final TermId variable : PLOT_VARIABLES) {
			Assert.assertFalse(plotVariableIds.contains(variable.getId()));
		}
		Assert.assertFalse(plotVariableIds.contains(this.treatmentFactor.getCvTermId()));
		Assert.assertFalse(plotVariableIds.contains(this.treatmentFactorLabel.getCvTermId()));
		this.verifyGeolocationPropRecords(false, instanceNumbers);


		// Save instances again
		instanceNumbers = Arrays.asList(1, 3);
		final Map<Integer, List<ObservationUnitRow>> instanceRowsMap = this.createObservationUnitRows(instanceNumbers);
		this.experimentDesignService
			.saveExperimentDesign(new CropType(), this.studyId, this.createMeasurementVariables(),instanceRowsMap);
		this.sessionProvder.getSession().flush();

		rows = this.datasetService.getAllObservationUnitRows(this.studyId, this.plotDatasetId, null);
		Assert.assertNotNull(rows);

		// Verify saving of variables
		this.verifyEnvironmentVariablesWereSaved();
		this.verifyPlotVariablesWereSaved();
		this.verifyGeolocationPropRecords(true, instanceNumbers);

		// Check that plot experiments are created for selected instances
		Assert.assertEquals(instanceRowsMap.keySet().size() * NO_ENTRIES * NO_REPS * NO_TREATMENTS, rows.size());
		final Map<Integer, List<ObservationUnitRow>> instancesRowMap = new HashMap<>();
		for (final ObservationUnitRow row : rows) {
			final Integer trialInstance = row.getTrialInstance();
			if (instancesRowMap.get(trialInstance) == null) {
				instancesRowMap.put(trialInstance, new ArrayList<ObservationUnitRow>());
			}
			instancesRowMap.get(trialInstance).add(row);
		}
		Assert.assertEquals(NO_ENTRIES * NO_REPS * NO_TREATMENTS, instancesRowMap.get(1).size());
		Assert.assertNull(instancesRowMap.get(2));
		Assert.assertEquals(NO_ENTRIES * NO_REPS * NO_TREATMENTS, instancesRowMap.get(3).size());
		this.verifyObservationUnitRowValues(instancesRowMap);
	}

	@Test
	public void testGetExperimentDesignTypeTermId() {
		Assert.assertFalse(this.experimentDesignService.getStudyExperimentDesignTypeTermId(this.studyId).isPresent());

		final Integer exptDesignId = ExperimentDesignType.P_REP.getTermId();
		this.daoFactory.getProjectPropertyDAO().save(
			new ProjectProperty(new DmsProject(this.environmentDatasetId), VariableType.ENVIRONMENT_DETAIL.getId(), exptDesignId.toString(),
				this.daoFactory.getProjectPropertyDAO().getNextRank(this.environmentDatasetId), TermId.EXPERIMENT_DESIGN_FACTOR.getId(),
				"EXPT_DESIGN"));
		Assert.assertEquals(exptDesignId, this.experimentDesignService.getStudyExperimentDesignTypeTermId(this.studyId).get());
	}

	private void verifyObservationUnitRowValues(final Map<Integer, List<ObservationUnitRow>> instancesRowMap) {
		final ObservationUnitRow row = instancesRowMap.get(1).get(21);
		final Integer gid = this.gids[2];
		Assert.assertEquals(gid, row.getGid());
		Assert.assertEquals("PREF-ABC" + row.getEntryNumber().intValue(), row.getDesignation());
		Assert.assertEquals(3, row.getEntryNumber().intValue());
		Assert.assertEquals(1, row.getTrialInstance().intValue());
		Assert.assertNotNull(row.getObsUnitId());

		Assert.assertEquals("1", row.getVariables().get("TRIAL_INSTANCE").getValue());
		Assert.assertEquals(gid.toString(), row.getVariables().get("GID").getValue());
		Assert.assertEquals("3", row.getVariables().get("ENTRY_NO").getValue());
		Assert.assertEquals("2", row.getVariables().get("REP_NO").getValue());
		Assert.assertEquals("22", row.getVariables().get("PLOT_NO").getValue());
		Assert.assertEquals("1", row.getVariables().get(this.treatmentFactor.getName()).getValue());
		Assert.assertEquals("100", row.getVariables().get(this.treatmentFactorLabel.getName()).getValue());
		Assert.assertEquals("T", row.getVariables().get("ENTRY_TYPE").getValue());
		Assert.assertEquals("PREF-ABC" + row.getEntryNumber().intValue(), row.getVariables().get("DESIGNATION").getValue());
		Assert.assertNotNull(row.getVariables().get("OBS_UNIT_ID").getValue());
	}

	private void verifyPlotVariablesWereSaved() {
		final List<Integer> plotVariableIds = this.daoFactory.getProjectPropertyDAO()
			.getDatasetVariableIdsForVariableTypeIds(this.plotDatasetId, Arrays.asList(VariableType.GERMPLASM_DESCRIPTOR.getId(),
				VariableType.EXPERIMENTAL_DESIGN.getId(), VariableType.ENTRY_DETAIL.getId()), null);
		for (final TermId variable : Lists.newArrayList(Iterables.concat(PLOT_VARIABLES, GERMPLASM_VARIABLES, ENTRY_DETAIL_VARIABLES))) {
			Assert.assertTrue(plotVariableIds.contains(variable.getId()));
		}
		Assert.assertTrue(plotVariableIds.contains(this.treatmentFactor.getCvTermId()));
		Assert.assertTrue(plotVariableIds.contains(this.treatmentFactorLabel.getCvTermId()));

		final List<Integer> treatmentFactorVariableIds = this.daoFactory.getProjectPropertyDAO()
			.getDatasetVariableIdsForVariableTypeIds(this.plotDatasetId, Collections.singletonList(TermId.MULTIFACTORIAL_INFO.getId()),
				null);
		Assert.assertTrue(treatmentFactorVariableIds.contains(this.treatmentFactor.getCvTermId()));
		Assert.assertTrue(treatmentFactorVariableIds.contains(this.treatmentFactorLabel.getCvTermId()));
	}

	private void verifyEnvironmentVariablesWereSaved() {
		final List<MeasurementVariable> environmentVariables =
			this.datasetService.getDatasetMeasurementVariablesByVariableType(this.environmentDatasetId,
				Collections.singletonList(VariableType.ENVIRONMENT_DETAIL.getId()));
		final ImmutableMap<Integer, MeasurementVariable> environmentVariablesMap =
			Maps.uniqueIndex(environmentVariables, new Function<MeasurementVariable, Integer>() {

				@Override
				public Integer apply(final MeasurementVariable variable) {
					return variable.getTermId();
				}
			});
		final MeasurementVariable expDesignVariable = environmentVariablesMap.get(TermId.EXPERIMENT_DESIGN_FACTOR.getId());
		Assert.assertNotNull(expDesignVariable);
		Assert.assertEquals(String.valueOf(ExperimentDesignType.RANDOMIZED_COMPLETE_BLOCK.getTermId()), expDesignVariable.getValue());

		final MeasurementVariable nrepVariable = environmentVariablesMap.get(TermId.NUMBER_OF_REPLICATES.getId());
		Assert.assertNotNull(nrepVariable);
		Assert.assertEquals(NO_REPS.toString(), nrepVariable.getValue());
	}

	private void verifyGeolocationPropRecords(final boolean shouldExist, final List<Integer> instanceNumbers) {
		final List<Geolocation> geolocations = this.daoFactory.getGeolocationDao().getEnvironmentGeolocations(this.studyId);
		Assert.assertEquals(NO_INSTANCES.intValue(), geolocations.size());

		for (final Geolocation geolocation : geolocations) {

			final List<GeolocationProperty> properties =
				this.daoFactory.getGeolocationPropertyDao().getByGeolocation(geolocation.getLocationId());
			final ImmutableMap<Object, GeolocationProperty> propertiesMap =
				Maps.uniqueIndex(properties, new Function<GeolocationProperty, Object>() {

					@Override
					public Object apply(@Nullable final GeolocationProperty input) {
						return input.getTypeId();
					}
				});
			final Integer instanceNumber = Integer.valueOf(geolocation.getDescription());

			if (shouldExist && instanceNumbers.contains(instanceNumber)) {
				Assert.assertNotNull("Expecting EXP_DESIGN factor for instance " + instanceNumber, propertiesMap.get(TermId.EXPERIMENT_DESIGN_FACTOR.getId()));
				Assert.assertEquals(String.valueOf(ExperimentDesignType.RANDOMIZED_COMPLETE_BLOCK.getTermId()),
					propertiesMap.get(TermId.EXPERIMENT_DESIGN_FACTOR.getId()).getValue());
				Assert.assertNotNull("Expecting NO_REPS factor for instance " + instanceNumber, propertiesMap.get(TermId.NUMBER_OF_REPLICATES.getId()));
				Assert.assertEquals(NO_REPS.toString(), propertiesMap.get(TermId.NUMBER_OF_REPLICATES.getId()).getValue());
			} else {
				Assert.assertNull(propertiesMap.get(TermId.EXPERIMENT_DESIGN_FACTOR.getId()));
				Assert.assertNull(propertiesMap.get(TermId.NUMBER_OF_REPLICATES.getId()));
				Assert.assertNull(propertiesMap.get(TermId.BLOCK_ID.getId()));
			}
			// Location ID should always exist with or without design
			Assert.assertNotNull(propertiesMap.get(TermId.LOCATION_ID.getId()));
			Assert.assertEquals(LOCATION_ID, propertiesMap.get(TermId.LOCATION_ID.getId()).getValue());
		}
	}

	private List<MeasurementVariable> createMeasurementVariables() {
		final List<MeasurementVariable> variables = new ArrayList<>();

		final MeasurementVariable expDesignVariable = new MeasurementVariable();
		expDesignVariable.setVariableType(VariableType.ENVIRONMENT_DETAIL);
		expDesignVariable.setAlias("EXP_DESIGN");
		expDesignVariable.setTermId(TermId.EXPERIMENT_DESIGN_FACTOR.getId());
		expDesignVariable.setValue(String.valueOf(ExperimentDesignType.RANDOMIZED_COMPLETE_BLOCK.getTermId()));
		variables.add(expDesignVariable);

		final MeasurementVariable nrepVariable = new MeasurementVariable();
		nrepVariable.setVariableType(VariableType.ENVIRONMENT_DETAIL);
		nrepVariable.setAlias("NREP");
		nrepVariable.setTermId(TermId.NUMBER_OF_REPLICATES.getId());
		nrepVariable.setValue(NO_REPS.toString());
		variables.add(nrepVariable);

		final MeasurementVariable trialInstanceVariable = new MeasurementVariable();
		trialInstanceVariable.setVariableType(VariableType.ENVIRONMENT_DETAIL);
		trialInstanceVariable.setAlias("TRIAL_INSTANCE");
		trialInstanceVariable.setTermId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		variables.add(trialInstanceVariable);

		for (final TermId variable : GERMPLASM_VARIABLES) {
			final MeasurementVariable germplasmVariable = new MeasurementVariable();
			germplasmVariable.setVariableType(VariableType.GERMPLASM_DESCRIPTOR);
			germplasmVariable.setAlias(variable.name());
			germplasmVariable.setTermId(variable.getId());
			variables.add(germplasmVariable);
		}

		for (final TermId variable : PLOT_VARIABLES) {
			final MeasurementVariable plotVariable = new MeasurementVariable();
			plotVariable.setVariableType(VariableType.EXPERIMENTAL_DESIGN);
			plotVariable.setAlias(variable.name());
			plotVariable.setTermId(variable.getId());
			variables.add(plotVariable);
		}

		for (final TermId variable : ENTRY_DETAIL_VARIABLES) {
			final MeasurementVariable entryDetailVariable = new MeasurementVariable();
			entryDetailVariable.setVariableType(VariableType.ENTRY_DETAIL);
			entryDetailVariable.setAlias(variable.name());
			entryDetailVariable.setTermId(variable.getId());
			variables.add(entryDetailVariable);
		}

		// Treatment Factor variables
		final Integer tfVariableId1 = this.treatmentFactor.getCvTermId();
		final MeasurementVariable tfVariable = new MeasurementVariable();
		tfVariable.setVariableType(VariableType.TREATMENT_FACTOR);
		tfVariable.setAlias(this.treatmentFactor.getName());
		tfVariable.setTermId(tfVariableId1);
		tfVariable.setValue(this.treatmentFactor.getName());
		variables.add(tfVariable);

		final Integer tfVariableId2 = this.treatmentFactorLabel.getCvTermId();
		final MeasurementVariable tfLabelVariable = new MeasurementVariable();
		tfLabelVariable.setVariableType(VariableType.TREATMENT_FACTOR);
		tfLabelVariable.setAlias(this.treatmentFactorLabel.getName());
		tfLabelVariable.setTermId(tfVariableId2);
		tfLabelVariable.setValue(this.treatmentFactor.getName());
		variables.add(tfLabelVariable);

		return variables;
	}

	private Map<Integer, List<ObservationUnitRow>> createObservationUnitRows(final List<Integer> instanceNumbers) {

		final Map<Integer, List<ObservationUnitRow>> instanceRowsMap = new HashMap<>();
		for (int instance = 1; instance <= NO_INSTANCES; instance++) {

			if (instanceNumbers.contains(instance)) {
				instanceRowsMap.put(instance, new ArrayList<ObservationUnitRow>());
				int plotNo = 1;
				for (int rep = 1; rep <= NO_REPS; rep++) {
					for (int entry = 1; entry <= NO_ENTRIES; entry++) {
						for (int treatment = 1; treatment <= NO_TREATMENTS; treatment++) {
							final ObservationUnitRow row = new ObservationUnitRow();
							row.setEntryNumber(entry);
							row.setTrialInstance(instance);
							row.setVariables(new HashMap<String, ObservationUnitData>());
							row.getVariables().put(String.valueOf(TermId.TRIAL_INSTANCE_FACTOR.getId()),
								new ObservationUnitData(TermId.TRIAL_INSTANCE_FACTOR.getId(), "1"));
							row.getVariables()
								.put(String.valueOf(TermId.ENTRY_TYPE.getId()), new ObservationUnitData(TermId.ENTRY_TYPE.getId(),
									String.valueOf(SystemDefinedEntryType.TEST_ENTRY.getEntryTypeCategoricalId())));
							final Integer gid = this.gids[entry - 1];
							row.getVariables()
								.put(String.valueOf(TermId.GID.getId()), new ObservationUnitData(TermId.GID.getId(), String.valueOf(gid)));
							row.getVariables()
								.put(String.valueOf(TermId.DESIG.getId()),
									new ObservationUnitData(TermId.DESIG.getId(), "GERMPLASM_PREFIX" + gid));
							row.getVariables().put(String.valueOf(TermId.ENTRY_NO.getId()),
								new ObservationUnitData(TermId.ENTRY_NO.getId(), String.valueOf(entry)));
							row.getVariables()
								.put(String.valueOf(TermId.PLOT_NO.getId()),
									new ObservationUnitData(TermId.PLOT_NO.getId(), String.valueOf(plotNo++)));
							row.getVariables()
								.put(String.valueOf(TermId.REP_NO.getId()),
									new ObservationUnitData(TermId.REP_NO.getId(), String.valueOf(rep)));
							row.getVariables().put(this.treatmentFactor.getCvTermId().toString(),
								new ObservationUnitData(this.treatmentFactor.getCvTermId(), String.valueOf(treatment)));
							row.getVariables().put(this.treatmentFactorLabel.getCvTermId().toString(),
								new ObservationUnitData(this.treatmentFactorLabel.getCvTermId(), String.valueOf(treatment * 100)));
							instanceRowsMap.get(instance).add(row);
						}
					}
				}
			}
		}

		return instanceRowsMap;
	}

	private GeolocationProperty createGeolocationProperty(final Integer geolocationId, final Integer variableId, final String value) {
		final GeolocationProperty property = new GeolocationProperty();
		property.setGeolocation(this.daoFactory.getGeolocationDao().getById(geolocationId));
		property.setType(variableId);
		property.setRank(1);
		property.setValue(value);
		return property;
	}

}
