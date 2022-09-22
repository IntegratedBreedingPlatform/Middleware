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

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.apache.commons.lang.RandomStringUtils;
import org.generationcp.middleware.DataSetupTest;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.dao.StudyTypeDAO;
import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.data.initializer.CVTermTestDataInitializer;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.service.api.study.StudyEntryDto;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StockDaoIntegrationTest extends IntegrationTestBase {

	private static final int TEST_COUNT = 3;

	private DmsProjectDao dmsProjectDao;
	private GermplasmDAO germplasmDao;
	private ExperimentDao experimentDao;
	private GeolocationDao geolocationDao;
	private StockDao stockDao;
	private CVTermDao cvtermDao;
	private PhenotypeDao phenotypeDao;
	private StudyTypeDAO studyTypeDAO;
	private DmsProject project;
	private List<StockModel> testStocks;
	private List<ExperimentModel> experiments;
	private Geolocation environment;
	private List<Integer> gids;

	@Before
	public void setUp() throws Exception {
		this.dmsProjectDao = new DmsProjectDao();
		this.dmsProjectDao.setSession(this.sessionProvder.getSession());

		this.germplasmDao = new GermplasmDAO(this.sessionProvder.getSession());

		this.experimentDao = new ExperimentDao();
		this.experimentDao.setSession(this.sessionProvder.getSession());

		this.geolocationDao = new GeolocationDao();
		this.geolocationDao.setSession(this.sessionProvder.getSession());

		this.stockDao = new StockDao();
		this.stockDao.setSession(this.sessionProvder.getSession());

		this.cvtermDao = new CVTermDao();
		this.cvtermDao.setSession(this.sessionProvder.getSession());

		this.phenotypeDao = new PhenotypeDao();
		this.phenotypeDao.setSession(this.sessionProvder.getSession());

		this.studyTypeDAO = new StudyTypeDAO();
		this.studyTypeDAO.setSession(this.sessionProvder.getSession());

		this.project = this.createProject(null);

		this.testStocks= new ArrayList<>();
		this.experiments = new ArrayList<>();

		this.createSampleStocks(TEST_COUNT, project);
	}

	@Test
	public void testCountStocks() {
		final CVTerm variateTerm = createVariate();
		for (final ExperimentModel experiment : experiments) {
			this.createTestObservations(experiment, variateTerm);
		}
		// Need to flush session to sync with underlying database before querying
		this.sessionProvder.getSession().flush();
		final long count = this.stockDao.countStocks(project.getProjectId(), environment.getLocationId(), variateTerm.getCvTermId());
		Assert.assertEquals(TEST_COUNT, count);
	}

	@Test
	public void testCountStudiesByGid() {
		final Germplasm germplasm = this.testStocks.get(0).getGermplasm();
		this.createTestStock(this.project, germplasm);
		final DmsProject study2 = this.createProject(null);
		this.createTestStock(study2, germplasm);

		// Need to flush session to sync with underlying database before querying
		this.sessionProvder.getSession().flush();
		final long count = this.stockDao.countStudiesByGids(Collections.singletonList(germplasm.getGid()));
		Assert.assertEquals(2, count);
	}

	@Test
	public void testGetStudiesByGid() {
		final Germplasm germplasm = this.testStocks.get(0).getGermplasm();
		this.createTestStock(this.project, germplasm);
		final DmsProject study2 = this.createProject(null);
		this.createTestStock(study2, germplasm);

		// Need to flush session to sync with underlying database before querying
		this.sessionProvder.getSession().flush();
		final List<StudyReference> studies = this.stockDao.getStudiesByGid(germplasm.getGid());
		final ImmutableMap<Integer, StudyReference> resultsMap = Maps.uniqueIndex(studies, new Function<StudyReference, Integer>() {
			@Override
			public Integer apply(final StudyReference input) {
				return input.getId();
			}
		});
		Assert.assertEquals(2, resultsMap.size());
		final List<DmsProject> expectedStudies = Arrays.asList(project, study2);
		for (final DmsProject study : expectedStudies) {
			final Integer id = study.getProjectId();
			final StudyReference studyReference = resultsMap.get(id);
			Assert.assertNotNull(studyReference);
			Assert.assertEquals(id, studyReference.getId());
			Assert.assertEquals(study.getName(), studyReference.getName());
			Assert.assertEquals(study.getDescription(), studyReference.getDescription());
			Assert.assertEquals(study.getProgramUUID(), studyReference.getProgramUUID());
			Assert.assertEquals(study.getStudyType().getName(), studyReference.getStudyType().getName());
			Assert.assertEquals(this.findAdminUser(), studyReference.getOwnerId());
			Assert.assertNull(studyReference.getOwnerName());
			Assert.assertTrue(studyReference.getIsLocked());
		}
	}

	@Test
	public void testGetPlotEntriesMap() {
		Map<Integer, StudyEntryDto> plotEntriesMap = this.stockDao.getPlotEntriesMap(this.project.getProjectId(), new HashSet<>(Arrays.asList(1, 2, 3, 4, 5)));

		final List<Integer> gids = this.testStocks.stream().map(s -> s.getGermplasm().getGid()).collect(Collectors.toList());
		for (final Map.Entry<Integer, StudyEntryDto> entry : plotEntriesMap.entrySet()) {
			final Integer plot = entry.getKey();
			Assert.assertEquals(DataSetupTest.GERMPLSM_PREFIX + plot, entry.getValue().getDesignation());
			Assert.assertTrue(gids.contains(entry.getValue().getGid()));
		}

		//Retrieve non existent plots in study
		plotEntriesMap = this.stockDao.getPlotEntriesMap(this.project.getProjectId(), new HashSet<>(Arrays.asList(51, 49)));
		Assert.assertTrue(plotEntriesMap.isEmpty());

	}

	private DmsProject createProject(final DmsProject parent) {
		final DmsProject project = new DmsProject();
		project.setName("Test Project Name " + RandomStringUtils.randomAlphanumeric(5));
		project.setDescription("Test Project " + RandomStringUtils.randomAlphanumeric(5));
		project.setStudyType(this.studyTypeDAO.getStudyTypeByName(StudyTypeDto.TRIAL_NAME));
		project.setProgramUUID(RandomStringUtils.randomAlphanumeric(20));
		project.setCreatedBy(this.findAdminUser().toString());
		project.setLocked(true);
		if (parent != null) {
			project.setParent(parent);
			project.setStudy(parent);
		}
		dmsProjectDao.save(project);
		return project;
	}

	private void createTestObservations(final ExperimentModel experiment, final CVTerm variateTerm) {
		final Phenotype phenotype = new Phenotype();
		phenotype.setObservableId(variateTerm.getCvTermId());
		phenotype.setValue(RandomStringUtils.randomNumeric(5));
		phenotype.setExperiment(experiment);
		phenotypeDao.save(phenotype);
	}

	private CVTerm createVariate() {
		final CVTerm variateTerm = CVTermTestDataInitializer.createTerm(RandomStringUtils.randomAlphanumeric(50), CvId.VARIABLES.getId());
		cvtermDao.save(variateTerm);
		return variateTerm;
	}

	private void createSampleStocks(final Integer count, final DmsProject study) {
		// Save the experiments in the same instance
		environment = new Geolocation();
		geolocationDao.saveOrUpdate(environment);
		this.gids = new ArrayList<>();

		for (int i = 0; i < count; i++) {
			final Germplasm germplasm = GermplasmTestDataInitializer.createGermplasm(1);
			germplasm.setGid(null);
			this.germplasmDao.save(germplasm);
			this.germplasmDao.refresh(germplasm);
			this.gids.add(germplasm.getGid());

			final StockModel stockModel = createTestStock(study, germplasm);

			this.createTestExperiment(study, stockModel);
		}

	}

	private StockModel createTestStock(final DmsProject study, final Germplasm germplasm) {
		final StockModel stockModel = new StockModel();
		stockModel.setUniqueName(RandomStringUtils.randomAlphanumeric(10));
		stockModel.setIsObsolete(false);
		stockModel.setGermplasm(germplasm);
		stockModel.setCross("-");
		stockModel.setProject(study);
		stockModel.setCross(RandomStringUtils.randomAlphanumeric(10));
		this.stockDao.saveOrUpdate(stockModel);
		this.testStocks.add(stockModel);
		return stockModel;
	}

	private void createTestExperiment(final DmsProject study, final StockModel stockModel) {
		final ExperimentModel experimentModel = new ExperimentModel();
		experimentModel.setGeoLocation(environment);
		experimentModel.setTypeId(TermId.PLOT_EXPERIMENT.getId());
		experimentModel.setProject(study);
		experimentModel.setStock(stockModel);
		experimentDao.saveOrUpdate(experimentModel);
		this.experiments.add(experimentModel);
	}

}
