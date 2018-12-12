package org.generationcp.middleware.dao.oms;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.dao.dms.ExperimentDao;
import org.generationcp.middleware.dao.dms.ExperimentPropertyDao;
import org.generationcp.middleware.dao.dms.GeolocationDao;
import org.generationcp.middleware.dao.dms.GeolocationPropertyDao;
import org.generationcp.middleware.dao.dms.PhenotypeDao;
import org.generationcp.middleware.dao.dms.ProjectPropertyDao;
import org.generationcp.middleware.dao.dms.StockDao;
import org.generationcp.middleware.dao.dms.StockPropertyDao;
import org.generationcp.middleware.data.initializer.CVTermTestDataInitializer;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermRelationship;
import org.junit.Before;
import org.junit.Test;

public class CVTermRelationshipDaoTest extends IntegrationTestBase {
	
	private static final int NO_OF_CATEGORIES = 12;
	
	private CVTermRelationshipDao cvtermRelationshipDao;
	private CVTermDao cvtermDao;
	private DmsProjectDao projectDao;
	private ProjectPropertyDao projectPropDao;
	private ExperimentDao experimentDao;
	private ExperimentPropertyDao experimentPropDao;
	private PhenotypeDao phenotypeDao;
	private GermplasmDAO germplasmDao;
	private StockDao stockDao;
	private StockPropertyDao stockPropDao;
	private GeolocationDao geolocationDao;
	private GeolocationPropertyDao geolocPropDao;
	
	private DmsProject study;
	private CVTerm variable;
	private CVTerm scale;
	private List<CVTerm> categories;

	@Before
	public void setUp() throws Exception {
		if (this.cvtermRelationshipDao == null) {			
			this.cvtermRelationshipDao = new CVTermRelationshipDao();
			this.cvtermRelationshipDao.setSession(this.sessionProvder.getSession());
		}
		
		if (this.cvtermDao == null) {			
			this.cvtermDao = new CVTermDao();
			this.cvtermDao.setSession(this.sessionProvder.getSession());
		}
		
		if (this.projectDao == null) {			
			this.projectDao = new DmsProjectDao();
			this.projectDao.setSession(this.sessionProvder.getSession());
		}
		
		if (this.projectPropDao == null) {			
			this.projectPropDao = new ProjectPropertyDao();
			this.projectPropDao.setSession(this.sessionProvder.getSession());
		}
		
		if (this.experimentDao == null) {			
			this.experimentDao = new ExperimentDao();
			this.experimentDao.setSession(this.sessionProvder.getSession());
		}
		
		if (this.experimentPropDao == null) {			
			this.experimentPropDao = new ExperimentPropertyDao();
			this.experimentPropDao.setSession(this.sessionProvder.getSession());
		}
		
		if (this.phenotypeDao == null) {			
			this.phenotypeDao = new PhenotypeDao();
			this.phenotypeDao.setSession(this.sessionProvder.getSession());
		}
		
		if (this.germplasmDao == null) {
			this.germplasmDao = new GermplasmDAO();
			this.germplasmDao.setSession(this.sessionProvder.getSession());
		}
		
		if (this.stockDao == null) {			
			this.stockDao = new StockDao();
			this.stockDao.setSession(this.sessionProvder.getSession());
		}
		
		if (this.stockPropDao == null) {			
			this.stockPropDao = new StockPropertyDao();
			this.stockPropDao.setSession(this.sessionProvder.getSession());
		}
		
		if (this.geolocationDao == null) {			
			this.geolocationDao = new GeolocationDao();
			this.geolocationDao.setSession(this.sessionProvder.getSession());
		}
		
		if (this.geolocPropDao == null) {			
			this.geolocPropDao = new GeolocationPropertyDao();
			this.geolocPropDao.setSession(this.sessionProvder.getSession());
		}
		
		if (this.study == null) {
			this.study = new DmsProject();
			this.study.setName("Test Project");
			this.study.setDescription("Test Project");
			this.projectDao.save(this.study);
		}
		
		this.createTestOntologyData();
		this.createObservations();
	}
	
	private void createTestOntologyData() {
		if (this.scale == null) {
			this.scale = CVTermTestDataInitializer.createTerm(RandomStringUtils.randomAlphanumeric(50), CvId.SCALES.getId());
			this.cvtermDao.save(this.scale);
			System.out.println(this.scale);
		}
		
		if (this.categories == null) {
			this.categories = new ArrayList<>();
			for (int i = 0; i < NO_OF_CATEGORIES; i++) {
				final CVTerm category = this.cvtermDao
						.save(CVTermTestDataInitializer.createTerm(RandomStringUtils.randomAlphanumeric(20), CvId.IBDB_TERMS.getId()));
				this.categories.add(category);
				System.out.println(category);
				
				final CVTermRelationship hasValueRelationship = new CVTermRelationship();
				hasValueRelationship.setTypeId(TermId.HAS_VALUE.getId());
				hasValueRelationship.setSubjectId(this.scale.getCvTermId());
				hasValueRelationship.setObjectId(category.getCvTermId());
				this.cvtermRelationshipDao.save(hasValueRelationship);
				System.out.println(hasValueRelationship);
			}
		}
		
		if (this.variable == null) {
			this.variable = CVTermTestDataInitializer.createTerm(RandomStringUtils.randomAlphanumeric(50), CvId.VARIABLES.getId());
			this.cvtermDao.save(this.variable);
			System.out.println(this.variable);
			
			final CVTermRelationship hasScaleRelationship = new CVTermRelationship();
			hasScaleRelationship.setTypeId(TermId.HAS_SCALE.getId());
			hasScaleRelationship.setSubjectId(this.variable.getCvTermId());
			hasScaleRelationship.setObjectId(this.scale.getCvTermId());
			this.cvtermRelationshipDao.save(hasScaleRelationship);
			System.out.println(hasScaleRelationship);
		}
	}

	@Test
	public void testGetScaleCategoriesUsedInObservations() {
		final List<String> usedCategories = this.cvtermRelationshipDao.getScaleCategoriesUsedInObservations(62173);
		assertEquals(2, usedCategories.size());
		assertEquals("1", usedCategories.get(0));
		assertEquals("4", usedCategories.get(1));
//		assertEquals(this.categories.get(0).getName(), usedCategories.get(0));
//		assertEquals(this.categories.get(1).getName(), usedCategories.get(1));
	}
	
	@Test
	public void testGetScaleCategoriesUsedInStudies() {
		final List<String> usedCategories = this.cvtermRelationshipDao.getCategoriesUsedInStudies(62173);
		assertEquals(2, usedCategories.size());
		assertEquals("1", usedCategories.get(0));
		assertEquals("4", usedCategories.get(1));
//		assertEquals(this.categories.get(0).getName(), usedCategories.get(0));
//		assertEquals(this.categories.get(1).getName(), usedCategories.get(1));
	}
	
	private void createObservations() {
		final Geolocation geolocation = new Geolocation();
		this.geolocationDao.save(geolocation);

		final Germplasm germplasm = GermplasmTestDataInitializer.createGermplasm(1);
		germplasm.setGid(null);
		this.germplasmDao.save(germplasm);

		final StockModel stockModel = new StockModel();
		stockModel.setName("Germplasm ");
		stockModel.setIsObsolete(false);
		stockModel.setTypeId(TermId.ENTRY_CODE.getId());
		stockModel.setUniqueName("1");
		stockModel.setGermplasm(germplasm);
		this.stockDao.save(stockModel);

		final ExperimentModel experimentModel = new ExperimentModel();
		experimentModel.setGeoLocation(geolocation);
		experimentModel.setTypeId(TermId.PLOT_EXPERIMENT.getId());
		experimentModel.setProject(this.study);
		experimentModel.setStock(stockModel);
		this.experimentDao.save(experimentModel);

		final Phenotype phenotype1 = new Phenotype();
		phenotype1.setObservableId(this.variable.getCvTermId());
		phenotype1.setExperiment(experimentModel);
		final CVTerm category = this.categories.get(0);
		phenotype1.setValue(category.getName());
		phenotype1.setcValue(category.getCvTermId());
		this.phenotypeDao.save(phenotype1);
		System.out.println(phenotype1);
		
		final Phenotype phenotype2 = new Phenotype();
		phenotype2.setObservableId(this.variable.getCvTermId());
		phenotype2.setExperiment(experimentModel);
		final CVTerm category2 = this.categories.get(1);
		phenotype2.setValue(category2.getName());
		phenotype2.setcValue(category2.getCvTermId());
		this.phenotypeDao.save(phenotype2);
		System.out.println(phenotype2);
	}
	
	private void createStudyConstant() {
		final Geolocation geolocation = new Geolocation();
		this.geolocationDao.save(geolocation);

		final Germplasm germplasm = GermplasmTestDataInitializer.createGermplasm(1);
		germplasm.setGid(null);
		this.germplasmDao.save(germplasm);

		final StockModel stockModel = new StockModel();
		stockModel.setName("Germplasm ");
		stockModel.setIsObsolete(false);
		stockModel.setTypeId(TermId.ENTRY_CODE.getId());
		stockModel.setUniqueName("1");
		stockModel.setGermplasm(germplasm);
		this.stockDao.save(stockModel);

		final ExperimentModel experimentModel = new ExperimentModel();
		experimentModel.setGeoLocation(geolocation);
		experimentModel.setTypeId(TermId.PLOT_EXPERIMENT.getId());
		experimentModel.setProject(this.study);
		experimentModel.setStock(stockModel);
		this.experimentDao.save(experimentModel);

		final Phenotype phenotype1 = new Phenotype();
		phenotype1.setObservableId(this.variable.getCvTermId());
		phenotype1.setExperiment(experimentModel);
		final CVTerm category = this.categories.get(0);
		phenotype1.setValue(category.getName());
		phenotype1.setcValue(category.getCvTermId());
		this.phenotypeDao.save(phenotype1);
		System.out.println(phenotype1);
		
		final Phenotype phenotype2 = new Phenotype();
		phenotype2.setObservableId(this.variable.getCvTermId());
		phenotype2.setExperiment(experimentModel);
		final CVTerm category2 = this.categories.get(1);
		phenotype2.setValue(category2.getName());
		phenotype2.setcValue(category2.getCvTermId());
		this.phenotypeDao.save(phenotype2);
		System.out.println(phenotype2);
	}
}
