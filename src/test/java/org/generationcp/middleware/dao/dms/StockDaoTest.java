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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.RandomStringUtils;
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
import org.generationcp.middleware.pojos.dms.ProjectRelationship;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class StockDaoTest extends IntegrationTestBase {
	
	private static final int TEST_COUNT = 3;
	private DmsProjectDao dmsProjectDao;
	private GermplasmDAO germplasmDao;
	private ExperimentDao experimentDao;
	private GeolocationDao geolocationDao;
	private StockDao stockDao;
	private StockPropertyDao stockPropertyDao;
	private ProjectRelationshipDao projectRelationshipDao;
	private CVTermDao cvtermDao;
	private PhenotypeDao phenotypeDao;
	private StudyTypeDAO studyTypeDAO;
	
	private DmsProject project;
	private List<StockModel> testStocks;
	private List<ExperimentModel> experiments;
	private Geolocation environment;
	

	@Before
	public void setUp() throws Exception {
		this.dmsProjectDao = new DmsProjectDao();
		this.dmsProjectDao.setSession(this.sessionProvder.getSession());
		
		this.germplasmDao = new GermplasmDAO();
		this.germplasmDao.setSession(this.sessionProvder.getSession());
		
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
		
		this.projectRelationshipDao = new ProjectRelationshipDao();
		this.projectRelationshipDao.setSession(this.sessionProvder.getSession());
		
		this.stockPropertyDao = new StockPropertyDao();
		this.stockPropertyDao.setSession(this.sessionProvder.getSession());

		this.studyTypeDAO = new StudyTypeDAO();
		this.studyTypeDAO.setSession(this.sessionProvder.getSession());
		
		this.project = this.createProject();
		this.testStocks = new ArrayList<>();
		this.experiments = new ArrayList<>();
		
		this.createSampleStocks(TEST_COUNT, project);
	}

	private DmsProject createProject() {
		final DmsProject project = new DmsProject();
		project.setName("Test Project Name " + RandomStringUtils.randomAlphanumeric(5));
		project.setDescription("Test Project " + RandomStringUtils.randomAlphanumeric(5));
		project.setStudyType(this.studyTypeDAO.getStudyTypeByName(StudyTypeDto.TRIAL_NAME));
		dmsProjectDao.save(project);
		return project;
	}
	
	private void saveProjectRelationship(final DmsProject object, final DmsProject subject) {
		final ProjectRelationship rel = new ProjectRelationship();
		rel.setObjectProject(object);
		rel.setSubjectProject(subject);
		rel.setTypeId(TermId.BELONGS_TO_STUDY.getId());
		projectRelationshipDao.save(rel);
		System.out.println(rel);
	}
	
	@Test
	public void testGetStockIdsByProperty_UsingDbxrefId() {
		final StockModel testStock = this.testStocks.get(0);
		final Integer gid = testStock.getGermplasm().getGid();
		final List<Integer> stockIds = this.stockDao.getStockIdsByProperty(StockDao.DBXREF_ID, gid.toString());
		Assert.assertNotNull(stockIds);
		Assert.assertEquals(testStock.getStockId(), stockIds.get(0));
	}
	
	@Test
	public void testGetStockIdsByProperty_UsingUniqueName() {
		final StockModel testStock = this.testStocks.get(0);
		final List<Integer> stockIds = this.stockDao.getStockIdsByProperty("uniqueName", testStock.getUniqueName());
		Assert.assertNotNull(stockIds);
		Assert.assertEquals(testStock.getStockId(), stockIds.get(0));
	}
	
	@Test
	public void testGetStockIdsByProperty_UsingName() {
		final StockModel testStock = this.testStocks.get(0);
		final List<Integer> stockIds = this.stockDao.getStockIdsByProperty("name", testStock.getName());
		Assert.assertNotNull(stockIds);
		Assert.assertEquals(testStock.getStockId(), stockIds.get(0));
	}
	
	@Test
	public void testGetStockIdsByProperty_UsingValue() {
		final StockModel testStock = this.testStocks.get(0);
		final List<Integer> stockIds = this.stockDao.getStockIdsByProperty("value", testStock.getValue());
		Assert.assertNotNull(stockIds);
		Assert.assertEquals(testStock.getStockId(), stockIds.get(0));
	}
	
	@Test
	public void testFindInDataset() {
		// Need to flush session to sync with underlying database before querying
		this.sessionProvder.getSession().flush();
		
		final Set<StockModel> stocks = this.stockDao.findInDataSet(this.project.getProjectId());
		Assert.assertNotNull(stocks);
		Assert.assertEquals(TEST_COUNT, stocks.size());
	}
	
	@Test
	public void testGetStocksByIds() {
		final List<Integer> ids = new ArrayList<>();
		for (final StockModel stock : this.testStocks){
			ids.add(stock.getStockId());
		}
		final Map<Integer, StockModel> stocksMap = this.stockDao.getStocksByIds(ids);
		Assert.assertEquals(TEST_COUNT, stocksMap.size());
		for (final StockModel stock : this.testStocks){
			Assert.assertEquals(stock, stocksMap.get(stock.getStockId()));
		}
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
	public void testCountObservations() {
		final CVTerm variateTerm = createVariate();
		for (final ExperimentModel experiment : experiments) {
			this.createTestObservations(experiment, variateTerm);
		}
		// Need to flush session to sync with underlying database before querying
		this.sessionProvder.getSession().flush();
		final long count = this.stockDao.countObservations(project.getProjectId(), environment.getLocationId(), variateTerm.getCvTermId());
		Assert.assertEquals(TEST_COUNT, count);
	}
	
	@Test
	public void testCountStudiesByGid() {
		final Germplasm germplasm = this.testStocks.get(0).getGermplasm();
		final StockModel stock = this.createTestStock(germplasm);
		final DmsProject parent1 = this.createProject();
		final DmsProject parent2 = this.createProject();
		this.setupTestProjectsWithRelationship(parent1, parent2, stock);
		
		// Need to flush session to sync with underlying database before querying
		this.sessionProvder.getSession().flush();
		final long count = this.stockDao.countStudiesByGid(germplasm.getGid());
		Assert.assertEquals(2, count);
	}
	
	@Test
	public void testGetStudiesByGid() {
		final Germplasm germplasm = this.testStocks.get(0).getGermplasm();
		final StockModel stock = this.createTestStock(germplasm);
		final DmsProject parent1 = this.createProject();
		final DmsProject parent2 = this.createProject();
		this.setupTestProjectsWithRelationship(parent1, parent2, stock);
		
		// Need to flush session to sync with underlying database before querying
		this.sessionProvder.getSession().flush();
		final List<StudyReference> studies = this.stockDao.getStudiesByGid(germplasm.getGid(), 0, 10);
		final ImmutableMap<Integer, StudyReference> resultsMap = Maps.uniqueIndex(studies, new Function<StudyReference, Integer>() {
			@Override
			public Integer apply(final StudyReference input) {
				return input.getId();
			}
		});
		Assert.assertEquals(2, resultsMap.size());
		final List<DmsProject> expectedStudies = Arrays.asList(parent1, parent2);
		for (final DmsProject study : expectedStudies) {
			final Integer id = study.getProjectId();
			final StudyReference studyReference = resultsMap.get(id);
			Assert.assertNotNull(studyReference);
			Assert.assertEquals(id, studyReference.getId());
			Assert.assertEquals(study.getName(), studyReference.getName());
			Assert.assertEquals(study.getDescription(), studyReference.getDescription());
		}
	}
	
	private void setupTestProjectsWithRelationship(final DmsProject parent1, final DmsProject parent2, final StockModel stockToReuse) {
		this.saveProjectRelationship(parent1, project);
		final DmsProject project2 = this.createProject();
		this.saveProjectRelationship(parent2, project2);
		this.createTestExperiment(project2, stockToReuse);
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



//	@Test
//	public void testGetStudiesByGid() {
//		final int gid = 123;
//		final int start = 10;
//		final int numOfRows = 500;
//		this.dao.getStudiesByGid(gid, start, numOfRows);
//		final String expectedSql =  "select distinct p.project_id, p.name, p.description, "
//				+ "st.study_type_id, st.label, st.name, st.visible, st.cvterm_id, p.program_uuid "
//				+ "FROM stock s "
//				+ "LEFT JOIN nd_experiment e on e.stock_id = s.stock_id "
//				+ "LEFT JOIN project_relationship pr ON pr.subject_project_id = e.project_id "
//				+ "LEFT JOIN project p ON pr.object_project_id = p.project_id "
//				+ "INNER JOIN study_type st ON p.study_type_id = st.study_type_id "
//				+ " WHERE s.dbxref_id = " + gid + " AND p.deleted = 0";
//		final ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
//		Mockito.verify(session).createSQLQuery(sqlCaptor.capture());
//		Assert.assertEquals(expectedSql, sqlCaptor.getValue());
//		Mockito.verify(query).setFirstResult(start);
//		Mockito.verify(query).setMaxResults(numOfRows);
//	}
//	
//	@Test
//	public void testCountStudiesByGid() {
//		Mockito.doReturn(new BigInteger("100")).when(this.query).uniqueResult();
//		final int gid = 123;
//		final long count = this.dao.countStudiesByGid(gid);
//		
//		final String expectedSql = "select count(distinct p.project_id) " + "FROM stock s "
//				+ "LEFT JOIN nd_experiment e on e.stock_id = s.stock_id "
//				+ "LEFT JOIN project_relationship pr ON pr.subject_project_id = e.project_id "
//				+ "LEFT JOIN project p ON pr.object_project_id = p.project_id " + "WHERE s.dbxref_id = " + gid
//				+ " AND p.deleted = 0";
//		final ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
//		Mockito.verify(session).createSQLQuery(sqlCaptor.capture());
//		Assert.assertEquals(expectedSql, sqlCaptor.getValue());
//		Assert.assertEquals(100L, count);
//	}
//	
	private void createSampleStocks(final Integer count, final DmsProject study) {
		// Save the experiments in the same instance
		environment = new Geolocation();
		geolocationDao.saveOrUpdate(environment);
		
		for (int i = 0; i < count; i++) {
			final Germplasm germplasm = GermplasmTestDataInitializer.createGermplasm(1);
			germplasm.setGid(null);
			this.germplasmDao.save(germplasm);
			
			final StockModel stockModel = createTestStock(germplasm);
			
			this.createTestExperiment(study, stockModel);
		}
		
	}

	private StockModel createTestStock(final Germplasm germplasm) {
		final StockModel stockModel = new StockModel();
		stockModel.setUniqueName(RandomStringUtils.randomAlphanumeric(10));
		stockModel.setTypeId(TermId.ENTRY_CODE.getId());
		stockModel.setName(RandomStringUtils.randomAlphanumeric(10));
		stockModel.setIsObsolete(false);
		stockModel.setGermplasm(germplasm);
		stockModel.setValue(RandomStringUtils.randomAlphanumeric(5));
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
		System.out.println(experimentModel);
	}
}
