package org.generationcp.middleware.service.impl.analysis;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.api.ontology.AnalysisVariablesImportRequest;
import org.generationcp.middleware.api.ontology.OntologyVariableService;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.domain.dms.DatasetDTO;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.ontology.daoElements.VariableFilter;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.dms.DatasetType;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.GeolocationProperty;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.generationcp.middleware.service.api.analysis.AnalysisService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AnalysisServiceImplIntegrationTest extends IntegrationTestBase {

	@Autowired
	private AnalysisService analysisService;

	@Autowired
	private OntologyVariableService ontologyVariableService;

	private DaoFactory daoFactory;

	@Before
	public void setUp() {
		this.daoFactory = new DaoFactory(this.sessionProvder);
	}

	@Test
	public void testCreateMeansDataset() {
		// Create study with 2 instances and 5 entries per instance
		final DmsProject study = this.createTestStudyWithObservations("TestStudy", Arrays.asList(1, 2), 5);
		final Variable testVariable = this.createTestVariable(RandomStringUtils.randomAlphabetic(10));

		// Create Analysis Variables to be used in creating means dataset
		final AnalysisVariablesImportRequest analysisVariablesImportRequest = new AnalysisVariablesImportRequest();
		analysisVariablesImportRequest.setVariableType(VariableType.ANALYSIS.getName());
		analysisVariablesImportRequest.setVariableIds(Arrays.asList(testVariable.getId()));
		analysisVariablesImportRequest.setAnalysisMethodNames(Arrays.asList("BLUEs", "BLUPs"));
		final List<Integer> analysisVariableIds = this.ontologyVariableService.createAnalysisVariables(analysisVariablesImportRequest);

		final VariableFilter variableFilter = new VariableFilter();
		analysisVariableIds.forEach(variableFilter::addVariableId);
		final Map<Integer, Variable> analysisVariablesMap = this.ontologyVariableService.getVariablesWithFilterById(variableFilter);

		// Create means dataset
		final MeansImportRequest meansImportRequest = new MeansImportRequest();
		final List<Geolocation> environmentGeolocations =
			this.daoFactory.getGeolocationDao().getEnvironmentGeolocations(study.getProjectId());
		final List<MeansImportRequest.MeansData> meansDataList =
			environmentGeolocations.stream().map(o -> this.createMeansData(o.getLocationId(), 1, analysisVariablesMap))
				.collect(Collectors.toList());
		meansImportRequest.setStudyId(study.getProjectId());
		meansImportRequest.setData(meansDataList);

		final int meansDatasetId = this.analysisService.createMeansDataset(meansImportRequest);

		// Verify the means dataset is saved successfully
		// including the project, projectprop, experiment, and phenotype records.
		final DatasetDTO meansDataset = this.daoFactory.getDmsProjectDAO().getDataset(meansDatasetId);
		final Map<Integer, ProjectProperty> meansDatasetProjectProperties =
			this.daoFactory.getProjectPropertyDAO().getByProjectId(meansDatasetId).stream().collect(
				Collectors.toMap(ProjectProperty::getVariableId, Function.identity()));
		final List<ExperimentModel> experimentModels = this.daoFactory.getExperimentDao()
			.getObservationUnits(meansDatasetId, environmentGeolocations.stream().map(Geolocation::getLocationId).collect(
				Collectors.toList()));
		// Verify DmsProject
		assertEquals(meansDataset.getDatasetTypeId().intValue(), DatasetTypeEnum.MEANS_DATA.getId());
		assertEquals("TestStudy-MEANS", meansDataset.getName());
		// Verify ProjectProp
		AnalysisServiceImpl.MEANS_DATASET_DMSPROJECT_PROPERTIES.entrySet().forEach(expectedProjectProperty -> {
			assertTrue(meansDatasetProjectProperties.containsKey(expectedProjectProperty.getKey()));
			assertEquals(expectedProjectProperty.getValue().getId(),
				meansDatasetProjectProperties.get(expectedProjectProperty.getKey()).getTypeId());
		});
		analysisVariablesMap.entrySet().forEach(expectedVariablesInProjectProp -> {
			assertTrue(meansDatasetProjectProperties.containsKey(expectedVariablesInProjectProp.getKey()));
			assertEquals(VariableType.ANALYSIS.getId(),
				meansDatasetProjectProperties.get(expectedVariablesInProjectProp.getKey()).getTypeId());
		});
		// Verify experiment and phenotype values
		assertEquals(meansImportRequest.getData().size(), experimentModels.size());
		final Map<Integer, MeansImportRequest.MeansData> meansDataByEnvironmentId =
			meansImportRequest.getData().stream()
				.collect(Collectors.toMap(MeansImportRequest.MeansData::getEnvironmentId, Function.identity()));
		for (final ExperimentModel experimentModel : experimentModels) {
			final MeansImportRequest.MeansData meansData = meansDataByEnvironmentId.get(experimentModel.getGeoLocation().getLocationId());
			experimentModel.getPhenotypes().forEach(p -> {
				final Variable analysisVariable = analysisVariablesMap.get(p.getObservableId());
				assertEquals(p.getValue(), meansData.getValues().get(analysisVariable.getName()).toString());
			});
		}
	}

	DmsProject createTestStudyWithObservations(final String studyName, final List<Integer> locationIds, final int noOfEntries) {

		final String programUUID = UUID.randomUUID().toString();

		final DmsProject study = new DmsProject();
		study.setName(studyName);
		study.setDescription(studyName);
		study.setProgramUUID(programUUID);
		this.daoFactory.getDmsProjectDAO().save(study);

		final DmsProject summaryDataset = new DmsProject();
		summaryDataset.setName(studyName + " - Plot Dataset");
		summaryDataset.setDescription(studyName + " - Plot Dataset");
		summaryDataset.setProgramUUID(programUUID);
		summaryDataset.setParent(study);
		summaryDataset.setStudy(study);
		summaryDataset.setDatasetType(new DatasetType(DatasetTypeEnum.SUMMARY_DATA.getId()));
		this.daoFactory.getDmsProjectDAO().save(summaryDataset);

		final DmsProject plot = new DmsProject();
		plot.setName(studyName + " - Plot Dataset");
		plot.setDescription(studyName + " - Plot Dataset");
		plot.setProgramUUID(programUUID);
		plot.setParent(study);
		plot.setStudy(study);
		plot.setDatasetType(new DatasetType(DatasetTypeEnum.PLOT_DATA.getId()));
		this.daoFactory.getDmsProjectDAO().save(plot);

		final Map<Integer, StockModel> stockModelMap = new HashMap<>();
		for (int i = 1; i <= noOfEntries; i++) {
			final Germplasm germplasm = GermplasmTestDataInitializer.createGermplasm(1);
			germplasm.setGid(null);
			this.daoFactory.getGermplasmDao().save(germplasm);
			final StockModel stockModel = new StockModel();
			stockModel.setName(germplasm.getGermplasmPreferredName());
			stockModel.setIsObsolete(false);
			stockModel.setTypeId(TermId.ENTRY_CODE.getId());
			stockModel.setUniqueName(String.valueOf(i));
			stockModel.setGermplasm(germplasm);
			stockModel.setValue(String.valueOf(i));
			stockModel.setProject(study);
			this.daoFactory.getStockDao().saveOrUpdate(stockModel);
		}

		for (final int locationId : locationIds) {

			final Geolocation geolocation = new Geolocation();
			geolocation.setDescription("1");
			this.daoFactory.getGeolocationDao().saveOrUpdate(geolocation);

			final GeolocationProperty geolocationProperty = new GeolocationProperty();
			geolocationProperty.setGeolocation(geolocation);
			geolocationProperty.setType(TermId.LOCATION_ID.getId());
			geolocationProperty.setRank(1);
			geolocationProperty.setValue(String.valueOf(locationId));
			this.daoFactory.getGeolocationPropertyDao().save(geolocationProperty);

			final ExperimentModel trialExperiment = new ExperimentModel();
			trialExperiment.setProject(summaryDataset);
			trialExperiment.setGeoLocation(geolocation);
			trialExperiment.setTypeId(TermId.TRIAL_ENVIRONMENT_EXPERIMENT.getId());
			this.daoFactory.getExperimentDao().save(trialExperiment);

			for (int i = 1; i <= noOfEntries; i++) {
				final ExperimentModel experimentModel = new ExperimentModel();
				experimentModel.setGeoLocation(geolocation);
				experimentModel.setTypeId(TermId.PLOT_EXPERIMENT.getId());
				experimentModel.setProject(plot);
				experimentModel.setStock(stockModelMap.get(i));
				final String customUnitID = RandomStringUtils.randomAlphabetic(10);
				experimentModel.setObsUnitId(customUnitID);
				this.daoFactory.getExperimentDao().saveOrUpdate(experimentModel);
			}

		}

		return study;

	}

	private Variable createTestVariable(final String variableName) {
		return this.createTestVariable(variableName, RandomStringUtils.randomAlphanumeric(10), RandomStringUtils.randomAlphanumeric(10),
			RandomStringUtils.randomAlphanumeric(10), Arrays.asList(VariableType.TRAIT, VariableType.SELECTION_METHOD));
	}

	private Variable createTestVariable(final String variableName, final String propertyName, final String scaleName,
		final String methodName, final List<VariableType> variableTypes) {
		// Create traitVariable
		final CVTerm cvTermVariable = this.daoFactory.getCvTermDao()
			.save(variableName, RandomStringUtils.randomAlphanumeric(10), CvId.VARIABLES);
		final CVTerm property = this.daoFactory.getCvTermDao().save(propertyName, "", CvId.PROPERTIES);
		final CVTerm scale = this.daoFactory.getCvTermDao().save(scaleName, "", CvId.SCALES);
		this.daoFactory.getCvTermRelationshipDao().save(scale.getCvTermId(), TermId.HAS_TYPE.getId(), DataType.NUMERIC_VARIABLE.getId());
		final CVTerm method = this.daoFactory.getCvTermDao().save(methodName, "", CvId.METHODS);
		final CVTerm numericDataType = this.daoFactory.getCvTermDao().getById(DataType.NUMERIC_VARIABLE.getId());

		// Assign Property, Scale, Method
		this.daoFactory.getCvTermRelationshipDao()
			.save(cvTermVariable.getCvTermId(), TermId.HAS_PROPERTY.getId(), property.getCvTermId());
		this.daoFactory.getCvTermRelationshipDao()
			.save(cvTermVariable.getCvTermId(), TermId.HAS_SCALE.getId(), scale.getCvTermId());
		this.daoFactory.getCvTermRelationshipDao().save(cvTermVariable.getCvTermId(), TermId.HAS_METHOD.getId(), method.getCvTermId());

		// Assign TRAIT and SELECTION_METHOD Variable types
		for (final VariableType variableType : variableTypes) {
			this.daoFactory.getCvTermPropertyDao()
				.save(new CVTermProperty(null, cvTermVariable.getCvTermId(), TermId.VARIABLE_TYPE.getId(), variableType.getName(), 0));
		}

		final VariableFilter variableFilter = new VariableFilter();
		variableFilter.addVariableId(cvTermVariable.getCvTermId());
		return this.daoFactory.getCvTermDao().getVariablesWithFilterById(variableFilter).values().stream().findFirst().get();

	}

	private MeansImportRequest.MeansData createMeansData(final int environmentId, final int entryNo,
		final Map<Integer, Variable> analysisVariablesMap) {
		final MeansImportRequest.MeansData meansData = new MeansImportRequest.MeansData();
		meansData.setEntryNo(entryNo);
		meansData.setEnvironmentId(environmentId);
		final Map<String, Double> valuesMap = new HashMap<>();
		for (final Variable variable : analysisVariablesMap.values()) {
			valuesMap.put(variable.getName(), new Random().nextDouble());
		}
		meansData.setValues(valuesMap);
		return meansData;
	}

}
