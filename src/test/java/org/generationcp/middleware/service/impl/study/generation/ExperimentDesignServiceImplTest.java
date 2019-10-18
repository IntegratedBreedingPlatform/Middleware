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
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.LocationDataManager;
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
import org.generationcp.middleware.service.impl.dataset.DatasetServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nullable;
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
	private static final List<TermId> GERMPLASM_VARIABLES =
		Arrays.asList(TermId.ENTRY_TYPE, TermId.GID, TermId.DESIG, TermId.ENTRY_NO);
	private static final List<TermId> PLOT_VARIABLES =
		Arrays.asList(TermId.PLOT_NO, TermId.REP_NO);
	private static final String GERMPLASM_PREFIX = "GERMPLASM_PREFIX";
	public static final String LOCATION_ID = "9011";

	@Autowired
	private GermplasmDataManager germplasmDataManager;

	@Autowired
	private OntologyDataManager ontologyManager;

	@Autowired
	private LocationDataManager locationManager;

	@Autowired
	private WorkbenchTestDataUtil workbenchTestDataUtil;

	private DaoFactory daoFactory;
	private StudyDataManagerImpl studyDataManager;
	private StudyTestDataInitializer studyTDI;

	private DatasetService datasetService;
	private ExperimentDesignServiceImpl experimentDesignService;
	private GermplasmTestDataGenerator germplasmTestDataGenerator;

	private Project commonTestProject;
	private StudyReference studyReference;
	private Integer studyId;
	private Integer plotDatasetId;
	private Integer environmentDatasetId;
	private CVTerm treatmentFactor;
	private CVTerm treatmentFactorLabel;
	private Integer[] gids;

	@Before
	public void setup() throws Exception {
		this.experimentDesignService = new ExperimentDesignServiceImpl(this.sessionProvder);
		this.datasetService = new DatasetServiceImpl(this.sessionProvder);
		this.studyDataManager = new StudyDataManagerImpl(this.sessionProvder);
		this.daoFactory = new DaoFactory(this.sessionProvder);

		if (this.commonTestProject == null) {
			this.commonTestProject = this.workbenchTestDataUtil.getCommonTestProject();
		}

		if (this.germplasmTestDataGenerator == null) {
			this.germplasmTestDataGenerator = new GermplasmTestDataGenerator(this.germplasmDataManager);
		}

		this.studyTDI =
			new StudyTestDataInitializer(this.studyDataManager, this.ontologyManager, this.commonTestProject, this.germplasmDataManager,
				this.locationManager);

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
		}
	}

	@Test
	public void testSaveExperimentDesign() {
		this.experimentDesignService
			.saveExperimentDesign(new CropType(), this.studyId, this.createMeasurementVariables(), this.createObservationUnitRows());

		final List<ObservationUnitRow> rows = this.datasetService.getAllObservationUnitRows(this.studyId, this.plotDatasetId);
		Assert.assertNotNull(rows);

		// Verify saving of variables
		this.verifyEnvironmentVariablesWereSaved();
		this.verifyPlotVariablesWereSaved();
		this.verifyGeolocationPropRecords();


		// Check that plot experiments are created
		Assert.assertEquals(NO_INSTANCES * NO_ENTRIES * NO_REPS * NO_TREATMENTS, rows.size());
		final ObservationUnitRow row = rows.get(21);
		final Integer gid = this.gids[2];
		Assert.assertEquals(gid, row.getGid());
		Assert.assertEquals(GERMPLASM_PREFIX + gid, row.getDesignation());
		Assert.assertEquals(3, row.getEntryNumber().intValue());
		Assert.assertEquals(1, row.getTrialInstance().intValue());

		Assert.assertEquals("1", row.getVariables().get("TRIAL_INSTANCE").getValue());
		Assert.assertEquals(gid.toString(), row.getVariables().get("GID").getValue());
		Assert.assertEquals("3", row.getVariables().get("ENTRY_NO").getValue());
		Assert.assertEquals("2", row.getVariables().get("REP_NO").getValue());
		Assert.assertEquals("22", row.getVariables().get("PLOT_NO").getValue());
		Assert.assertEquals("1", row.getVariables().get(this.treatmentFactor.getName()).getValue());
		Assert.assertEquals("100", row.getVariables().get(this.treatmentFactorLabel.getName()).getValue());
		Assert.assertEquals("Test entry", row.getVariables().get("ENTRY_TYPE").getValue());
		Assert.assertEquals(GERMPLASM_PREFIX + gid, row.getVariables().get("DESIGNATION").getValue());
	}

	@Test
	public void testDeleteExperimentDesign() {
		// Save design first, then delete
		this.experimentDesignService
			.saveExperimentDesign(new CropType(), this.studyId, this.createMeasurementVariables(), this.createObservationUnitRows());
		this.experimentDesignService.deleteExperimentDesign(this.studyId);

		final List<ObservationUnitRow> rows = this.datasetService.getAllObservationUnitRows(this.studyId, this.plotDatasetId);
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
	}

	@Test
	public void testGetExperimentDesignTypeTermId() {
		Assert.assertFalse(this.experimentDesignService.getExperimentDesignTypeTermId(this.studyId).isPresent());

		final Integer exptDesignId = ExperimentDesignType.P_REP.getTermId();
		final ProjectProperty property = new ProjectProperty();
		property.setVariableId(TermId.EXPERIMENT_DESIGN_FACTOR.getId());
		property.setProject(new DmsProject(this.environmentDatasetId));
		property.setRank(this.daoFactory.getProjectPropertyDAO().getNextRank(this.environmentDatasetId));
		property.setAlias("EXPT_DESIGN");
		property.setValue(exptDesignId.toString());
		property.setTypeId(VariableType.ENVIRONMENT_DETAIL.getId());
		this.daoFactory.getProjectPropertyDAO().save(property);
		Assert.assertEquals(exptDesignId, this.experimentDesignService.getExperimentDesignTypeTermId(this.studyId).get());
	}

	private void verifyPlotVariablesWereSaved() {
		final List<Integer> plotVariableIds = this.daoFactory.getProjectPropertyDAO()
			.getDatasetVariableIdsForVariableTypeIds(this.plotDatasetId, Arrays.asList(VariableType.GERMPLASM_DESCRIPTOR.getId(),
				VariableType.EXPERIMENTAL_DESIGN.getId()), null);
		for (final TermId variable : Lists.newArrayList(Iterables.concat(PLOT_VARIABLES, GERMPLASM_VARIABLES))) {
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

	private void verifyGeolocationPropRecords() {
		final List<Geolocation> geolocations = this.daoFactory.getGeolocationDao().getEnvironmentGeolocations(studyId);
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

			Assert.assertNotNull(propertiesMap.get(TermId.EXPERIMENT_DESIGN_FACTOR.getId()));
			Assert.assertEquals(String.valueOf(ExperimentDesignType.RANDOMIZED_COMPLETE_BLOCK.getTermId()), propertiesMap.get(TermId.EXPERIMENT_DESIGN_FACTOR.getId()).getValue());
			Assert.assertNotNull(propertiesMap.get(TermId.NUMBER_OF_REPLICATES.getId()));
			Assert.assertEquals(NO_REPS.toString(), propertiesMap.get(TermId.NUMBER_OF_REPLICATES.getId()).getValue());
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

	private List<ObservationUnitRow> createObservationUnitRows() {

		final List<ObservationUnitRow> rows = new ArrayList<>();
		int plotNo = 1;
		for (int instance = 1; instance <= NO_INSTANCES; instance++)
			for (int rep = 1; rep <= NO_REPS; rep++) {
				for (int entry = 1; entry <= NO_ENTRIES; entry++) {
					for (int treatment = 1; treatment <= NO_TREATMENTS; treatment++) {
						final ObservationUnitRow row = new ObservationUnitRow();
						row.setEntryNumber(entry);
						row.setTrialInstance(instance);
						row.setVariables(new HashMap<String, ObservationUnitData>());
						row.getVariables().put(String.valueOf(TermId.TRIAL_INSTANCE_FACTOR.getId()),
							new ObservationUnitData(TermId.TRIAL_INSTANCE_FACTOR.getId(), "1"));
						row.getVariables().put(String.valueOf(TermId.ENTRY_TYPE.getId()), new ObservationUnitData(TermId.ENTRY_TYPE.getId(),
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
						rows.add(row);
					}
				}
			}
		return rows;
	}

}
