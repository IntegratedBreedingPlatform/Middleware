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
import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.dao.ProjectDAO;
import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.data.initializer.CVTermTestDataInitializer;
import org.generationcp.middleware.data.initializer.DMSVariableTestDataInitializer;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.dms.ExperimentValues;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.operation.saver.ExperimentModelSaver;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.dms.DatasetType;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.service.api.phenotype.PhenotypeSearchDTO;
import org.generationcp.middleware.service.api.phenotype.PhenotypeSearchObservationDTO;
import org.generationcp.middleware.service.api.phenotype.PhenotypeSearchRequestDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class PhenotypeDaoIntegrationTest extends IntegrationTestBase {

	private static final int NO_OF_GERMPLASM = 5;

	private PhenotypeDao phenotypeDao;

	private GeolocationDao geolocationDao;

	private ExperimentDao experimentDao;

	private StockDao stockDao;

	private GermplasmDAO germplasmDao;

	private DmsProjectDao dmsProjectDao;

	private CVTermDao cvTermDao;
	private ProjectPropertyDao projectPropertyDao;
	private ProjectDAO workbenchProjectDao;

	private DmsProject study;
	private CVTerm trait;
	private List<Phenotype> phenotypes;
	private Map<String, ExperimentModel> experiments;
	private ExperimentModelSaver experimentModelSaver;
	private CropType crop;
	private Project commonTestProject;
	private List<Germplasm> germplasm;

	@Autowired
	private StudyDataManager studyDataManager;

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

		if (this.germplasmDao == null) {
			this.germplasmDao = new GermplasmDAO();
			this.germplasmDao.setSession(this.sessionProvder.getSession());
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

		if (this.germplasm == null) {
			this.createGermplasm();
		}

		this.crop = new CropType();
		this.crop.setUseUUID(true);
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
			this.germplasmDao.save(germplasm);
			this.germplasm.add(germplasm);
		}
	}

	@Test
	public void testContainsAtLeast2CommonEntriesWithValues() {
		final Integer studyId = this.study.getProjectId();
		// Create environment with 2 reps but no phenotype data
		Integer locationId = this.createEnvironmentData(2, false);
		Assert.assertFalse(this.phenotypeDao.containsAtLeast2CommonEntriesWithValues(studyId, locationId, TermId.GID.getId()));

		// Create environment with 1 rep and phenotype data
		locationId = this.createEnvironmentData(1, true);
		Assert.assertFalse(this.phenotypeDao.containsAtLeast2CommonEntriesWithValues(studyId, locationId, TermId.GID.getId()));

		// Create environment with 2 reps and phenotype data
		locationId = this.createEnvironmentData(2, true);
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
		this.studyDataManager.addExperiment(this.crop, 1, ExperimentType.TRIAL_ENVIRONMENT, values);
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
		this.studyDataManager.addExperiment(this.crop, 1, ExperimentType.TRIAL_ENVIRONMENT, values);
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
			this.phenotypeDao.countPhenotypesForDataset(this.study.getProjectId(),Collections.singletonList(this.trait.getCvTermId())));
	}

	@Test
	public void testCountPhenotypesForDataset() {
		final int numberOfReps = 2;
		this.createEnvironmentData(numberOfReps, true);
		Assert.assertEquals(NO_OF_GERMPLASM * numberOfReps,
			this.phenotypeDao.countPhenotypesForDataset(this.study.getProjectId(),Collections.singletonList(this.trait.getCvTermId())));
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
		final List<Integer> traitIds =Collections.singletonList(this.trait.getCvTermId());
		final Integer projectId = this.study.getProjectId();
		Assert.assertEquals(NO_OF_GERMPLASM * numberOfReps, this.phenotypeDao.countPhenotypesForDataset(projectId, traitIds));

		this.phenotypeDao.deletePhenotypesByProjectIdAndVariableIds(projectId, traitIds);
		Assert.assertEquals(0, this.phenotypeDao.countPhenotypesForDataset(projectId, traitIds));
	}

	@Test
	public void testDeletePhenotypesByProjectIdAndLocationId() {
		final Integer locationId = this.createEnvironmentData(1, true);
		final List<Integer> traitIds =Collections.singletonList(this.trait.getCvTermId());
		final Integer projectId = this.study.getProjectId();
		Assert.assertEquals(NO_OF_GERMPLASM, this.phenotypeDao.countPhenotypesForDataset(projectId, traitIds));

		this.phenotypeDao.deletePhenotypesByProjectIdAndLocationId(projectId, locationId);
		Assert.assertEquals(0, this.phenotypeDao.countPhenotypesForDataset(projectId, traitIds));
	}
	
	@Test
	public void testUpdateOutOfSyncPhenotypes(){
		this.createEnvironmentData(1, true);	
		final Integer experimentId = this.phenotypes.get(0).getExperiment().getNdExperimentId();
		final Integer variableId = this.trait.getCvTermId();
		final Integer datasetId = this.study.getProjectId();
		Assert.assertFalse(this.phenotypeDao.hasOutOfSync(datasetId));
		
		this.phenotypeDao.updateOutOfSyncPhenotypes(experimentId, Arrays.asList(variableId));
		Assert.assertTrue(this.phenotypeDao.hasOutOfSync(datasetId));
		final Phenotype phenotype = this.phenotypeDao.getPhenotypeByExperimentIdAndObservableId(experimentId, variableId);
		Assert.assertEquals(Phenotype.ValueStatus.OUT_OF_SYNC, phenotype.getValueStatus());
	}
	
	@Test
	public void testIsValidPhenotype() {
		this.createEnvironmentData(1, true);
		final Integer experimentId = this.phenotypes.get(0).getExperiment().getNdExperimentId();
		final Integer phenotypeId = this.phenotypes.get(0).getPhenotypeId();
		Assert.assertNotNull(this.phenotypeDao.getPhenotype(experimentId,  phenotypeId));
		Assert.assertNull(this.phenotypeDao.getPhenotype(experimentId + 1,  phenotypeId));
	}

	@Test
	public void testSearchPhenotypes() {
		// Create 2 studies
		final String uniqueID = this.commonTestProject.getUniqueID();
		final DmsProject plot =
			this.createDataset(this.study.getName() + " - Plot Dataset", uniqueID, DatasetTypeEnum.PLOT_DATA.getId(),
				study, study);
		final CVTerm trait2 = CVTermTestDataInitializer.createTerm(RandomStringUtils.randomAlphanumeric(50), CvId.VARIABLES.getId());
		this.cvTermDao.save(trait2);
		final List<Integer> traitIds = Arrays.asList(trait.getCvTermId(), trait2.getCvTermId());
		this.createProjectProperties(plot, traitIds);
		final Integer environment1 = this.createEnvironmentData(plot, 1, traitIds);
		final DmsProject study2 = this.createStudy();
		final DmsProject plot2 =
			this.createDataset(study2.getName() + " - Plot Dataset", uniqueID, DatasetTypeEnum.MEANS_DATA.getId(),
				study2, study2);
		this.createProjectProperties(plot2, traitIds);
		this.createEnvironmentData(plot2, 1, traitIds);
		this.sessionProvder.getSession().flush();

		final PhenotypeSearchRequestDTO dto = new PhenotypeSearchRequestDTO();
		dto.setProgramDbIds(Collections.singletonList(uniqueID));
		final List<PhenotypeSearchDTO> results = this.phenotypeDao.searchPhenotypes(1000, 1, dto);
		Assert.assertNotNull(results);
		Assert.assertEquals(NO_OF_GERMPLASM * 2, results.size());
		for (final PhenotypeSearchDTO result : results) {
			Assert.assertEquals(2, result.getObservations().size());
			for (final PhenotypeSearchObservationDTO observation : result.getObservations()) {
				final boolean isFirstTrait = observation.getObservationVariableDbId().equals(trait.getCvTermId().toString());
				Assert.assertEquals(isFirstTrait? trait.getCvTermId() : trait2.getCvTermId(), Integer.valueOf(observation.getObservationVariableDbId()));
				Assert.assertEquals(isFirstTrait? trait.getName() : trait2.getName(), observation.getObservationVariableName());
				Assert.assertNotNull(observation.getObservationDbId());
				Assert.assertNotNull(observation.getObservationTimeStamp());
				Assert.assertNotNull(observation.getValue());
			}
			final boolean isFirstStudy = result.getStudyName().equals(this.study.getName());
			Assert.assertEquals(isFirstStudy? this.study.getName() : study2.getName(), result.getStudyName());
			Assert.assertNull(result.getPlantNumber());
			final String obsUnitId = result.getObservationUnitDbId();
			Assert.assertNotNull(obsUnitId);
			final ExperimentModel experimentModel = this.experiments.get(obsUnitId);
			Assert.assertNotNull(experimentModel);
			Assert.assertEquals(experimentModel.getStock().getGermplasm().getGid().toString(), result.getGermplasmDbId());
			Assert.assertEquals(experimentModel.getStock().getName(), result.getGermplasmName());
			Assert.assertEquals(experimentModel.getStock().getUniqueName(), result.getEntryNumber());
			Assert.assertEquals(experimentModel.getGeoLocation().getLocationId().toString(), result.getStudyDbId());
		}

		// Search by Study ID
		final PhenotypeSearchRequestDTO dto2 = new PhenotypeSearchRequestDTO();
		dto2.setTrialDbIds(Collections.singletonList(study2.getProjectId().toString()));
		Assert.assertEquals(NO_OF_GERMPLASM, this.phenotypeDao.countPhenotypes(dto2));

		// Search by Geolocation ID
		final PhenotypeSearchRequestDTO dto3 = new PhenotypeSearchRequestDTO();
		dto3.setStudyDbIds(Collections.singletonList(environment1.toString()));
		Assert.assertEquals(NO_OF_GERMPLASM, this.phenotypeDao.countPhenotypes(dto3));

		// Search by Trait
		final PhenotypeSearchRequestDTO dto4 = new PhenotypeSearchRequestDTO();
		dto4.setObservationVariableDbIds(Collections.singletonList(this.trait.getCvTermId().toString()));
		Assert.assertEquals(NO_OF_GERMPLASM * 2, this.phenotypeDao.countPhenotypes(dto4));

		// Search by GIDs
		final PhenotypeSearchRequestDTO dto5 = new PhenotypeSearchRequestDTO();
		dto5.setGermplasmDbIds(Arrays.asList(this.germplasm.get(0).getGid().toString(), this.germplasm.get(1).getGid().toString()));
		// # of GIDs in filter x 2# of studies
		Assert.assertEquals(2 * 2, this.phenotypeDao.countPhenotypes(dto5));

		// Search by Dataset Type, just for current program
		final PhenotypeSearchRequestDTO dto6 = new PhenotypeSearchRequestDTO();
		dto6.setProgramDbIds(Collections.singletonList(uniqueID));
		dto6.setObservationLevel(String.valueOf(DatasetTypeEnum.PLOT_DATA.getId()));
		Assert.assertEquals(NO_OF_GERMPLASM, this.phenotypeDao.countPhenotypes(dto6));
	}


	private DmsProject createDataset(final String name, final String programUUID, final int datasetType, final DmsProject parent, final DmsProject study) {
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
		return createEnvironmentData(this.study, numberOfReps,
			withPhenotype ? Collections.singletonList(this.trait.getCvTermId()) : Collections.<Integer>emptyList());
	}

	private void createProjectProperties(final DmsProject project, final List<Integer> traitIds) {
		Integer rank = 1;
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
	private Integer createEnvironmentData(final DmsProject project, final Integer numberOfReps, final List<Integer> traitIds) {
		this.phenotypes = new ArrayList<>();
		final Geolocation geolocation = new Geolocation();
		this.geolocationDao.saveOrUpdate(geolocation);

		for (final Germplasm germplasm : this.germplasm) {
			final StockModel stockModel = new StockModel();
			stockModel.setName("Germplasm " + RandomStringUtils.randomAlphanumeric(5));
			stockModel.setIsObsolete(false);
			stockModel.setTypeId(TermId.ENTRY_CODE.getId());
			stockModel.setUniqueName(RandomStringUtils.randomAlphanumeric(10));
			stockModel.setGermplasm(germplasm);
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
					phenotype.setValue(String.valueOf(new Random().nextDouble()));
					this.phenotypes.add(this.phenotypeDao.save(phenotype));
				}
			}

		}

		return geolocation.getLocationId();
	}
}
