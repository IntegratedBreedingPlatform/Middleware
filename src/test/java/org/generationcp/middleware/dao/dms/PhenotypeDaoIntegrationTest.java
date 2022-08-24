/*******************************************************************************
 *
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.dao.dms;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.api.brapi.v2.observationunit.ObservationLevelMapper;
import org.generationcp.middleware.api.germplasm.GermplasmGuidGenerator;
import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.dao.NameDAO;
import org.generationcp.middleware.dao.ProjectDAO;
import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.dao.oms.CvTermPropertyDao;
import org.generationcp.middleware.data.initializer.CVTermTestDataInitializer;
import org.generationcp.middleware.data.initializer.DMSVariableTestDataInitializer;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.dms.ExperimentValues;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.h2h.NumericTraitInfo;
import org.generationcp.middleware.domain.h2h.Observation;
import org.generationcp.middleware.domain.h2h.TraitInfo;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.operation.saver.ExperimentModelSaver;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.dms.DatasetType;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.GeolocationProperty;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.service.api.phenotype.ObservationUnitDto;
import org.generationcp.middleware.service.api.phenotype.ObservationUnitSearchRequestDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static org.hamcrest.Matchers.is;

public class PhenotypeDaoIntegrationTest extends IntegrationTestBase {

	private static final int NO_OF_GERMPLASM = 5;

	private PhenotypeDao phenotypeDao;

	private GeolocationDao geolocationDao;

	private GeolocationPropertyDao geolocPropDao;

	private ExperimentDao experimentDao;

	private StockDao stockDao;

	private GermplasmDAO germplasmDao;

	private NameDAO germplasmNameDao;

	private DmsProjectDao dmsProjectDao;

	private CVTermDao cvTermDao;
	private ProjectPropertyDao projectPropertyDao;
	private ProjectDAO workbenchProjectDao;
	private CvTermPropertyDao cvTermPropertyDao;

	private DmsProject study;
	private CVTerm trait;
	private List<Phenotype> phenotypes;
	private Map<String, ExperimentModel> experiments;
	private ExperimentModelSaver experimentModelSaver;
	private CropType crop;
	private Project commonTestProject;
	private List<Germplasm> germplasm;

	@Before
	public void setUp() throws Exception {

		if (this.phenotypeDao == null) {
			this.phenotypeDao = new PhenotypeDao();
			this.phenotypeDao.setSession(this.sessionProvder.getSession());
		}

		if (this.geolocationDao == null) {
			this.geolocationDao = new GeolocationDao();
			this.geolocationDao.setSession(this.sessionProvder.getSession());
		}

		if (this.geolocPropDao == null) {
			this.geolocPropDao = new GeolocationPropertyDao();
			this.geolocPropDao.setSession(this.sessionProvder.getSession());
		}

		if (this.germplasmDao == null) {
			this.germplasmDao = new GermplasmDAO(this.sessionProvder.getSession());
		}

		if (this.germplasmNameDao == null) {
			this.germplasmNameDao = new NameDAO(this.sessionProvder.getSession());
		}

		if (this.experimentDao == null) {
			this.experimentDao = new ExperimentDao();
			this.experimentDao.setSession(this.sessionProvder.getSession());
		}

		if (this.stockDao == null) {
			this.stockDao = new StockDao();
			this.stockDao.setSession(this.sessionProvder.getSession());
		}

		if (this.dmsProjectDao == null) {
			this.dmsProjectDao = new DmsProjectDao();
			this.dmsProjectDao.setSession(this.sessionProvder.getSession());
		}

		if (this.cvTermDao == null) {
			this.cvTermDao = new CVTermDao();
			this.cvTermDao.setSession(this.sessionProvder.getSession());
		}

		if (this.cvTermPropertyDao == null) {
			this.cvTermPropertyDao = new CvTermPropertyDao();
			this.cvTermPropertyDao.setSession(this.sessionProvder.getSession());
		}

		if (this.projectPropertyDao == null) {
			this.projectPropertyDao = new ProjectPropertyDao();
			this.projectPropertyDao.setSession(this.sessionProvder.getSession());
		}

		if (this.workbenchProjectDao == null) {
			this.workbenchProjectDao = new ProjectDAO();
			this.workbenchProjectDao.setSession(this.workbenchSessionProvider.getSession());
		}

		if (this.commonTestProject == null) {
			this.commonTestProject = new Project();
			this.commonTestProject.setProjectName("Project " + RandomStringUtils.randomAlphanumeric(10));
			this.commonTestProject.setStartDate(new Date());
			this.commonTestProject.setUniqueID(UUID.randomUUID().toString());
			this.commonTestProject.setLastOpenDate(new Date());
			this.commonTestProject.setCropType(new CropType("maize"));
			this.workbenchProjectDao.save(this.commonTestProject);
		}

		if (this.study == null) {
			this.study = this.createStudy();
		}

		if (this.trait == null) {
			this.trait = CVTermTestDataInitializer.createTerm(RandomStringUtils.randomAlphanumeric(50), CvId.VARIABLES.getId());
			this.cvTermDao.save(this.trait);
		}

		if (this.experimentModelSaver == null) {
			this.experimentModelSaver = new ExperimentModelSaver(this.sessionProvder);
		}

		if (this.crop == null) {
			this.crop = new CropType();
			this.crop.setUseUUID(true);
		}

		if (this.germplasm == null) {
			this.createGermplasm();
		}

		this.experiments = new HashMap<>();
	}

	private DmsProject createStudy() {
		final DmsProject study = new DmsProject();
		study.setName("Test Project " + RandomStringUtils.randomAlphanumeric(10));
		study.setDescription("Test Project");
		study.setProgramUUID(this.commonTestProject.getUniqueID());
		this.dmsProjectDao.save(study);
		return study;
	}

	private void createGermplasm() {
		this.germplasm = new ArrayList<>();
		for (int i = 0; i < NO_OF_GERMPLASM; i++) {
			final Germplasm germplasm = GermplasmTestDataInitializer.createGermplasm(1);
			germplasm.setGid(null);
			GermplasmGuidGenerator.generateGermplasmGuids(this.crop, Collections.singletonList(germplasm));
			this.germplasmDao.save(germplasm);
			final Name germplasmName = GermplasmTestDataInitializer.createGermplasmName(germplasm.getGid());
			this.germplasmNameDao.save(germplasmName);
			germplasm.setPreferredName(germplasmName);
			this.germplasm.add(germplasm);
		}
	}

	@Test
	public void testContainsAtLeast2CommonEntriesWithValues() {
		final Integer studyId = this.study.getProjectId();
		// Create environment with 2 reps but no phenotype data
		Integer locationId = this.createEnvironmentData(2, false);
		this.sessionProvder.getSession().flush();
		Assert.assertFalse(this.phenotypeDao.containsAtLeast2CommonEntriesWithValues(studyId, locationId, TermId.GID.getId()));

		// Create environment with 1 rep and phenotype data
		locationId = this.createEnvironmentData(1, true);
		this.sessionProvder.getSession().flush();
		Assert.assertFalse(this.phenotypeDao.containsAtLeast2CommonEntriesWithValues(studyId, locationId, TermId.GID.getId()));

		// Create environment with 2 reps and phenotype data
		locationId = this.createEnvironmentData(2, true);
		this.sessionProvder.getSession().flush();
		Assert.assertTrue(this.phenotypeDao.containsAtLeast2CommonEntriesWithValues(studyId, locationId, TermId.GID.getId()));
	}

	@Test
	public void testGetPhenotypeByExperimentIdAndObservableId() {
		final VariableList factors = new VariableList();
		factors.add(DMSVariableTestDataInitializer.createVariable(1001, "999", DataType.NUMERIC_VARIABLE.getId(), VariableType.TRAIT));
		final ExperimentValues values = new ExperimentValues();
		values.setVariableList(factors);
		values.setLocationId(this.experimentModelSaver.createNewGeoLocation().getLocationId());
		values.setGermplasmId(1);
		//Save the experiment
		this.experimentModelSaver.addExperiment(this.crop, 1, ExperimentType.TRIAL_ENVIRONMENT, values);

		final ExperimentModel experiment = this.experimentDao.getExperimentByProjectIdAndLocation(1, values.getLocationId());
		final Phenotype phenotype = this.phenotypeDao.getPhenotypeByExperimentIdAndObservableId(experiment.getNdExperimentId(), 1001);
		Assert.assertEquals("999", phenotype.getValue());
	}

	@Test
	public void testUpdatePhenotypesByExperimentIdAndObervableId() {
		final VariableList factors = new VariableList();
		factors.add(DMSVariableTestDataInitializer.createVariable(1001, "999", DataType.NUMERIC_VARIABLE.getId(), VariableType.TRAIT));
		final ExperimentValues values = new ExperimentValues();
		values.setVariableList(factors);
		values.setLocationId(this.experimentModelSaver.createNewGeoLocation().getLocationId());
		values.setGermplasmId(1);

		//Save the experiment
		this.experimentModelSaver.addExperiment(this.crop, 1, ExperimentType.TRIAL_ENVIRONMENT, values);

		final ExperimentModel experiment = this.experimentDao.getExperimentByProjectIdAndLocation(1, values.getLocationId());
		Phenotype phenotype = this.phenotypeDao.getPhenotypeByExperimentIdAndObservableId(experiment.getNdExperimentId(), 1001);
		Assert.assertEquals("999", phenotype.getValue());

		this.phenotypeDao.updatePhenotypesByExperimentIdAndObervableId(experiment.getNdExperimentId(), phenotype.getObservableId(), "1000");
		phenotype = this.phenotypeDao.getPhenotypeByExperimentIdAndObservableId(experiment.getNdExperimentId(), 1001);
		Assert.assertEquals("1000", phenotype.getValue());
	}

	@Test
	public void testCountPhenotypesForDatasetWhenNoPhenotypes() {
		this.createEnvironmentData(1, false);
		Assert.assertEquals(0,
			this.phenotypeDao.countPhenotypesForDataset(this.study.getProjectId(), Collections.singletonList(this.trait.getCvTermId())));
	}

	@Test
	public void testCountPhenotypesForDataset() {
		final int numberOfReps = 2;
		this.createEnvironmentData(numberOfReps, true);
		Assert.assertEquals(NO_OF_GERMPLASM * numberOfReps,
			this.phenotypeDao.countPhenotypesForDataset(this.study.getProjectId(), Collections.singletonList(this.trait.getCvTermId())));
	}

	@Test
	public void testCountPhenotypesForDatasetAndInstance() {
		final int numberOfReps = 2;
		final int instanceId = this.createEnvironmentData(numberOfReps, true);
		Assert.assertEquals(NO_OF_GERMPLASM * numberOfReps,
			this.phenotypeDao.countPhenotypesForDatasetAndInstance(this.study.getProjectId(), instanceId));
	}

	@Test
	public void testCountPhenotypesForDatasetAndInstanceNoPhenotypes() {
		final int numberOfReps = 2;
		final int instanceId = this.createEnvironmentData(numberOfReps, false);
		Assert.assertEquals(0, this.phenotypeDao.countPhenotypesForDatasetAndInstance(this.study.getProjectId(), instanceId));
	}

	@Test
	public void testDeletePhenotypesByProjectIdAndTraitIds() {
		final int numberOfReps = 2;
		this.createEnvironmentData(numberOfReps, true);
		final List<Integer> traitIds = Collections.singletonList(this.trait.getCvTermId());
		final Integer projectId = this.study.getProjectId();
		Assert.assertEquals(NO_OF_GERMPLASM * numberOfReps, this.phenotypeDao.countPhenotypesForDataset(projectId, traitIds));

		this.phenotypeDao.deletePhenotypesByProjectIdAndVariableIds(projectId, traitIds);
		Assert.assertEquals(0, this.phenotypeDao.countPhenotypesForDataset(projectId, traitIds));
	}

	@Test
	public void testDeletePhenotypesByProjectIdAndLocationId() {
		final Integer locationId = this.createEnvironmentData(1, true);
		final List<Integer> traitIds = Collections.singletonList(this.trait.getCvTermId());
		final Integer projectId = this.study.getProjectId();
		Assert.assertEquals(NO_OF_GERMPLASM, this.phenotypeDao.countPhenotypesForDataset(projectId, traitIds));

		this.phenotypeDao.deletePhenotypesByProjectIdAndLocationId(projectId, locationId);
		Assert.assertEquals(0, this.phenotypeDao.countPhenotypesForDataset(projectId, traitIds));
	}

	@Test
	public void testUpdateOutOfSyncPhenotypes() {
		this.createEnvironmentData(1, true);
		final Integer experimentId = this.phenotypes.get(0).getExperiment().getNdExperimentId();
		final Integer variableId = this.trait.getCvTermId();
		final Integer datasetId = this.study.getProjectId();
		Assert.assertFalse(this.phenotypeDao.hasOutOfSync(datasetId));
		this.sessionProvder.getSession().flush();

		this.phenotypeDao
			.updateOutOfSyncPhenotypes(new HashSet<>(Arrays.asList(experimentId)), new HashSet<>(Arrays.asList(variableId)));
		Assert.assertTrue(this.phenotypeDao.hasOutOfSync(datasetId));
		final Phenotype phenotype = this.phenotypeDao.getPhenotypeByExperimentIdAndObservableId(experimentId, variableId);
		Assert.assertEquals(Phenotype.ValueStatus.OUT_OF_SYNC, phenotype.getValueStatus());
	}

	@Test
	public void testUpdateOutOfSyncPhenotypesByGeolocation() {
		final Integer geolocationId = this.createEnvironmentData(1, true);
		final Integer experimentId = this.phenotypes.get(0).getExperiment().getNdExperimentId();
		final Integer variableId = this.trait.getCvTermId();
		final Integer datasetId = this.study.getProjectId();
		this.sessionProvder.getSession().flush();
		Assert.assertFalse(this.phenotypeDao.hasOutOfSync(datasetId));
		this.phenotypeDao.updateOutOfSyncPhenotypesByGeolocation(geolocationId, new HashSet<>(Arrays.asList(variableId)));
		Assert.assertTrue(this.phenotypeDao.hasOutOfSync(datasetId));
		final Phenotype phenotype = this.phenotypeDao.getPhenotypeByExperimentIdAndObservableId(experimentId, variableId);
		Assert.assertEquals(Phenotype.ValueStatus.OUT_OF_SYNC, phenotype.getValueStatus());
	}

	@Test
	public void testIsValidPhenotype() {
		this.createEnvironmentData(1, true);
		final Integer experimentId = this.phenotypes.get(0).getExperiment().getNdExperimentId();
		final Integer phenotypeId = this.phenotypes.get(0).getPhenotypeId();
		Assert.assertNotNull(this.phenotypeDao.getPhenotype(experimentId, phenotypeId));
		Assert.assertNull(this.phenotypeDao.getPhenotype(experimentId + 1, phenotypeId));
	}

	@Test
	public void testCountOutOfSyncDataOfDatasetsInStudy() {
		final String uniqueID = this.commonTestProject.getUniqueID();
		final DmsProject plot =
			this.createDataset(this.study.getName() + " - Plot Dataset", uniqueID, DatasetTypeEnum.PLOT_DATA.getId(),
				this.study, this.study);

		final List<Integer> traitIds = Arrays.asList(this.trait.getCvTermId());
		this.createProjectProperties(plot, traitIds);
		this.createEnvironmentData(plot, 1, traitIds, true);
		this.sessionProvder.getSession().flush();

		final Integer experimentId = this.phenotypes.get(0).getExperiment().getNdExperimentId();
		final Integer variableId = this.trait.getCvTermId();
		this.phenotypeDao
			.updateOutOfSyncPhenotypes(new HashSet<>(Arrays.asList(experimentId)), new HashSet<>(Arrays.asList(variableId)));
		this.sessionProvder.getSession().flush();

		final Map<Integer, Long> outOfSyncMap = this.phenotypeDao.countOutOfSyncDataOfDatasetsInStudy(this.study.getProjectId());
		Assert.assertNotNull(outOfSyncMap.get(plot.getProjectId()));
		Assert.assertEquals(new Long(1), outOfSyncMap.get(plot.getProjectId()));
	}

	// FIXME this test can be improved to be easier to work with / add assertions
	@Test
	public void testSearchObservationUnits() {
		// Create 2 studies
		final String uniqueID = this.commonTestProject.getUniqueID();
		final DmsProject plot =
			this.createDataset(this.study.getName() + " - Plot Dataset", uniqueID, DatasetTypeEnum.PLOT_DATA.getId(),
				this.study, this.study);
		final CVTerm trait2 = CVTermTestDataInitializer.createTerm(RandomStringUtils.randomAlphanumeric(50), CvId.VARIABLES.getId());
		this.cvTermDao.save(trait2);

		//Create cvTermproperty for trait 2
		final CVTermProperty cvTermProperty = new CVTermProperty();
		cvTermProperty.setCvTermPropertyId(1);
		cvTermProperty.setCvTermId(trait2.getCvTermId());
		cvTermProperty.setTypeId(TermId.CROP_ONTOLOGY_ID.getId());
		cvTermProperty.setRank(1);
		cvTermProperty.setValue("CO:1200");
		this.cvTermPropertyDao.save(cvTermProperty);

		final List<Integer> traitIds = Arrays.asList(this.trait.getCvTermId(), trait2.getCvTermId());
		this.createProjectProperties(plot, traitIds);
		final Integer environment1 = this.createEnvironmentData(plot, 1, traitIds, true);
		final DmsProject study2 = this.createStudy();
		final DmsProject plot2 =
			this.createDataset(study2.getName() + " - Plot Dataset", uniqueID, DatasetTypeEnum.MEANS_DATA.getId(),
				study2, study2);
		this.createProjectProperties(plot2, traitIds);
		this.createEnvironmentData(plot2, 1, traitIds, true);
		this.sessionProvder.getSession().flush();

		// Search by program
		final ObservationUnitSearchRequestDTO dto = new ObservationUnitSearchRequestDTO();
		dto.setProgramDbIds(Collections.singletonList(uniqueID));
		final List<ObservationUnitDto> results = this.phenotypeDao.searchObservationUnits(1000, 0, dto);
		Assert.assertNotNull(results);
		Assert.assertEquals(NO_OF_GERMPLASM * 2, results.size());
		for (final ObservationUnitDto result : results) {
			final boolean isFirstStudy = result.getStudyName().equals(this.study.getName() + "_1");
			Assert.assertEquals(isFirstStudy ? this.study.getName() + "_1" : study2.getName() + "_1", result.getStudyName());
			Assert.assertNull(result.getPlantNumber());
			final String obsUnitId = result.getObservationUnitDbId();
			Assert.assertNotNull(obsUnitId);
			final ExperimentModel experimentModel = this.experiments.get(obsUnitId);
			Assert.assertNotNull(experimentModel);
			Assert.assertEquals(experimentModel.getStock().getGermplasm().getGermplasmUUID(), result.getGermplasmDbId());
			Assert.assertEquals(experimentModel.getStock().getGermplasm().getPreferredName().getNval(), result.getGermplasmName());
			Assert.assertEquals(experimentModel.getStock().getUniqueName(), result.getEntryNumber());
			Assert.assertEquals(experimentModel.getGeoLocation().getLocationId().toString(), result.getStudyDbId());
		}

		// Search by Study ID
		final ObservationUnitSearchRequestDTO dto2 = new ObservationUnitSearchRequestDTO();
		dto2.setTrialDbIds(Collections.singletonList(study2.getProjectId().toString()));
		Assert.assertEquals(NO_OF_GERMPLASM, this.phenotypeDao.countObservationUnits(dto2));

		final List<ObservationUnitDto> study2ObservationUnits = this.phenotypeDao.searchObservationUnits(1000, 1, dto2);
		study2ObservationUnits.forEach(observationUnitDto -> {
			Assert.assertThat(observationUnitDto.getObservationUnitPosition().getObservationLevel().getLevelName(),
				is(DatasetTypeEnum.MEANS_DATA.getName()));
		});

		final ObservationUnitSearchRequestDTO dto2Study1 = new ObservationUnitSearchRequestDTO();
		dto2Study1.setTrialDbIds(Collections.singletonList(this.study.getProjectId().toString()));
		final List<ObservationUnitDto> study1ObservationUnits = this.phenotypeDao.searchObservationUnits(1000, 1, dto2Study1);
		study1ObservationUnits.forEach(observationUnitDto -> {
			Assert.assertThat(observationUnitDto.getObservationUnitPosition().getObservationLevel().getLevelName(),
				is(ObservationLevelMapper.ObservationLevelEnum.PLOT.getName()));
		});

		// Search by Geolocation ID
		final ObservationUnitSearchRequestDTO dto3 = new ObservationUnitSearchRequestDTO();
		dto3.setStudyDbIds(Collections.singletonList(environment1.toString()));
		Assert.assertEquals(NO_OF_GERMPLASM, this.phenotypeDao.countObservationUnits(dto3));

		// Search by Trait
		final ObservationUnitSearchRequestDTO dto4 = new ObservationUnitSearchRequestDTO();
		dto4.setObservationVariableDbIds(Collections.singletonList(this.trait.getCvTermId().toString()));
		Assert.assertEquals(NO_OF_GERMPLASM * 2, this.phenotypeDao.countObservationUnits(dto4));

		// Search by germplasm uuid
		final ObservationUnitSearchRequestDTO dto5 = new ObservationUnitSearchRequestDTO();
		dto5.setGermplasmDbIds(Arrays.asList(this.germplasm.get(0).getGermplasmUUID(), this.germplasm.get(1).getGermplasmUUID()));
		// # of GUUIDs in filter x 2# of studies
		Assert.assertEquals(2 * 2, this.phenotypeDao.countObservationUnits(dto5));

		// Search by Dataset Type, just for current program
		final ObservationUnitSearchRequestDTO dto6 = new ObservationUnitSearchRequestDTO();
		dto6.setProgramDbIds(Collections.singletonList(uniqueID));
		dto6.setObservationLevel(ObservationLevelMapper.ObservationLevelEnum.PLOT.getName());
		Assert.assertEquals(NO_OF_GERMPLASM, this.phenotypeDao.countObservationUnits(dto6));
	}

	private DmsProject createDataset(final String name, final String programUUID, final int datasetType, final DmsProject parent,
		final DmsProject study) {
		final DmsProject dataset = new DmsProject();
		dataset.setName(name);
		dataset.setDescription(name);
		dataset.setProgramUUID(programUUID);
		dataset.setDatasetType(new DatasetType(datasetType));
		dataset.setParent(parent);
		dataset.setStudy(study);
		this.dmsProjectDao.save(dataset);
		return dataset;
	}

	private Integer createEnvironmentData(final Integer numberOfReps, final boolean withPhenotype) {
		return this.createEnvironmentData(this.study, numberOfReps,
			withPhenotype ? Collections.singletonList(this.trait.getCvTermId()) : Collections.emptyList(), true);
	}

	private void createProjectProperties(final DmsProject project, final List<Integer> traitIds) {
		int rank = 1;
		for (final Integer traitId : traitIds) {
			final ProjectProperty projectProp = new ProjectProperty();
			projectProp.setAlias(traitId.toString());
			projectProp.setVariableId(traitId);
			projectProp.setDescription(traitId.toString());
			projectProp.setTypeId(VariableType.TRAIT.getId());
			projectProp.setRank(rank++);
			projectProp.setProject(project);
			this.projectPropertyDao.save(projectProp);
		}
	}

	private Integer createEnvironmentData(final DmsProject project, final Integer numberOfReps, final List<Integer> traitIds,
		final boolean isWithValue) {
		this.phenotypes = new ArrayList<>();
		final Geolocation geolocation = new Geolocation();
		geolocation.setDescription("1");
		this.geolocationDao.saveOrUpdate(geolocation);

		final GeolocationProperty prop = new GeolocationProperty();
		prop.setGeolocation(geolocation);
		prop.setType(TermId.LOCATION_ID.getId());
		prop.setRank(1);
		prop.setValue(geolocation.getLocationId().toString());
		this.geolocPropDao.save(prop);

		for (final Germplasm germplasm : this.germplasm) {
			final StockModel stockModel = new StockModel();
			stockModel.setName("Germplasm " + RandomStringUtils.randomAlphanumeric(5));
			stockModel.setIsObsolete(false);
			stockModel.setUniqueName(RandomStringUtils.randomAlphanumeric(10));
			stockModel.setGermplasm(germplasm);
			stockModel.setCross("-");
			stockModel.setProject(this.study);
			this.stockDao.saveOrUpdate(stockModel);

			// Create N experiments for the same stock
			for (int j = 0; j < numberOfReps; j++) {
				final ExperimentModel experimentModel = new ExperimentModel();
				experimentModel.setGeoLocation(geolocation);
				experimentModel.setTypeId(TermId.PLOT_EXPERIMENT.getId());
				experimentModel.setProject(project);
				experimentModel.setStock(stockModel);
				this.experimentDao.saveOrUpdate(experimentModel);
				this.experiments.put(experimentModel.getObsUnitId(), experimentModel);

				for (final Integer traitId : traitIds) {
					final Phenotype phenotype = new Phenotype();
					phenotype.setObservableId(traitId);
					phenotype.setExperiment(experimentModel);
					if (isWithValue) {
						phenotype.setValue(String.valueOf(new Random().nextDouble()));
					}
					this.phenotypes.add(this.phenotypeDao.save(phenotype));
				}
			}

		}

		return geolocation.getLocationId();
	}

	@Test
	public void testgetObservationForTraits() {
		//Study with valid observation values
		final Integer geolocation1 = this.createEnvironmentData(1, true);
		//Study invalid observation values
		final String uniqueID = this.commonTestProject.getUniqueID();
		final DmsProject plot =
			this.createDataset(this.study.getName() + " - Plot Dataset", uniqueID, DatasetTypeEnum.PLOT_DATA.getId(),
				this.study, this.study);
		final Integer geolocation2 = this.createEnvironmentData(plot, 1, Arrays.asList(this.trait.getCvTermId()), false);

		final List<Observation> observations = this.phenotypeDao
			.getObservationForTraits(Arrays.asList(this.trait.getCvTermId()), Arrays.asList(geolocation1, geolocation2), 0, 0);
		Assert.assertEquals("Null values should not be included", this.germplasm.size(), observations.size());
	}

	@Test
	public void testCountByVariableIdAndValue() {
		this.createEnvironmentData(1, true);

		final long phenotypeCount = this.phenotypeDao.countByVariableIdAndValue(this.trait.getCvTermId(), this.phenotypes.get(0).getValue());
		Assert.assertThat(phenotypeCount, is(1L));
	}

	/**
	 * Method getNumericTraitInfoList(Collection: environment, Collection: trait) is use when environment is > 1000
	 */
	@Test
	public void testGetNumericTraitInfoValues1() {
		//Study with valid observation values
		final Integer geolocation1 = this.createEnvironmentData(1, true);
		//Study invalid observation values
		final String uniqueID = this.commonTestProject.getUniqueID();
		final DmsProject plot =
			this.createDataset(this.study.getName() + " - Plot Dataset", uniqueID, DatasetTypeEnum.PLOT_DATA.getId(),
				this.study, this.study);
		final Integer geolocation2 = this.createEnvironmentData(plot, 1, Arrays.asList(this.trait.getCvTermId()), false);
		final List<NumericTraitInfo> numericTraitInfos =
			this.phenotypeDao.getNumericTraitInfoList(Arrays.asList(geolocation1, geolocation2), Arrays.asList(this.trait.getCvTermId()));
		final Map<Integer, List<Double>> traitInfoValues =
			this.phenotypeDao.getNumericTraitInfoValues(Arrays.asList(geolocation1, geolocation2), numericTraitInfos);
		final List<Double> values = traitInfoValues.get(this.trait.getCvTermId());
		try {
			Collections.sort(values);
		} catch (final Exception ex) {
			Assert.fail("Sorting encountered an issue " + ex.getMessage());
		}
		Assert.assertEquals("Null values should not be included", this.germplasm.size(),
			traitInfoValues.get(this.trait.getCvTermId()).size());
	}

	/**
	 * Method getNumericTraitInfoValues(Collection: environment, Integer trait) is use when environment is < 1000
	 */
	@Test
	public void testGetNumericTraitInfoValues2() {
		//Study with valid observation values
		final Integer geolocation1 = this.createEnvironmentData(1, true);
		//Study invalid observation values
		final String uniqueID = this.commonTestProject.getUniqueID();
		final DmsProject plot =
			this.createDataset(this.study.getName() + " - Plot Dataset", uniqueID, DatasetTypeEnum.PLOT_DATA.getId(),
				this.study, this.study);
		final Integer geolocation2 = this.createEnvironmentData(plot, 1, Arrays.asList(this.trait.getCvTermId()), false);

		final Map<Integer, List<Double>> traitInfoValues =
			this.phenotypeDao.getNumericTraitInfoValues(Arrays.asList(geolocation1, geolocation2), this.trait.getCvTermId());
		final List<Double> values = traitInfoValues.get(this.trait.getCvTermId());
		try {
			Collections.sort(values);
		} catch (final Exception ex) {
			Assert.fail("Sorting encountered an issue " + ex.getMessage());
		}
		Assert.assertEquals("Null values should not be included", this.germplasm.size(),
			traitInfoValues.get(this.trait.getCvTermId()).size());
	}

	/**
	 * Method is used in Character Traits
	 */
	@Test
	public void getTraitInfoCounts1() {
		//Study with valid observation values
		final Integer geolocation1 = this.createEnvironmentData(1, true);
		final Integer geolocation2 = this.createEnvironmentData(1, true);
		this.sessionProvder.getSession().flush();

		final List<TraitInfo> traitInfoValues =
			this.phenotypeDao.getTraitInfoCounts(Arrays.asList(geolocation1, geolocation2), Arrays.asList(this.trait.getCvTermId()));
		Assert.assertEquals("Trait Count", 1, traitInfoValues.size());
		Assert.assertEquals("All environment with valid value will be counted", 2, traitInfoValues.get(0).getLocationCount());

	}

	/**
	 * Method is used in Character Traits
	 */
	@Test
	public void getTraitInfoCounts2() {
		//Study with valid observation values
		final Integer geolocation1 = this.createEnvironmentData(1, true);
		//Study invalid observation values
		final String uniqueID = this.commonTestProject.getUniqueID();
		final DmsProject plot =
			this.createDataset(this.study.getName() + " - Plot Dataset", uniqueID, DatasetTypeEnum.PLOT_DATA.getId(),
				this.study, this.study);
		final Integer geolocation2 = this.createEnvironmentData(plot, 1, Arrays.asList(this.trait.getCvTermId()), false);

		final List<TraitInfo> traitInfoValues =
			this.phenotypeDao.getTraitInfoCounts(Arrays.asList(geolocation1, geolocation2), Arrays.asList(this.trait.getCvTermId()));
		Assert.assertEquals("Trait Count", 1, traitInfoValues.size());
		Assert.assertEquals("Only environment with valid value will be counted", 1, traitInfoValues.get(0).getLocationCount());
	}

	/**
	 * Method is used in Categorical Traits
	 */
	@Test
	public void getTraitInfoCountsCategorical() {
		//Study with valid observation values
		final Integer geolocation1 = this.createEnvironmentData(1, true);
		final Integer geolocation2 = this.createEnvironmentData(1, true);
		this.sessionProvder.getSession().flush();

		final List<TraitInfo> traitInfoValues = this.phenotypeDao.getTraitInfoCounts(Arrays.asList(geolocation1, geolocation2));
		Assert.assertEquals("Trait Count", 1, traitInfoValues.size());
		Assert.assertEquals("All environment with valid value will be counted", 2, traitInfoValues.get(0).getLocationCount());

	}

	/**
	 * Method is used in Categorical Traits
	 */
	@Test
	public void getTraitInfoCountsCategorical2() {
		//Study with valid observation values
		final Integer geolocation1 = this.createEnvironmentData(1, true);
		//Study invalid observation values
		final String uniqueID = this.commonTestProject.getUniqueID();
		final DmsProject plot =
			this.createDataset(this.study.getName() + " - Plot Dataset", uniqueID, DatasetTypeEnum.PLOT_DATA.getId(),
				this.study, this.study);
		final Integer geolocation2 = this.createEnvironmentData(plot, 1, Arrays.asList(this.trait.getCvTermId()), false);

		final List<TraitInfo> traitInfoValues = this.phenotypeDao.getTraitInfoCounts(Arrays.asList(geolocation1, geolocation2));
		Assert.assertEquals("Trait Count", 1, traitInfoValues.size());
		Assert.assertEquals("Only environment with valid value will be counted", 1, traitInfoValues.get(0).getLocationCount());
	}

}
