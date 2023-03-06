package org.generationcp.middleware.dao.oms;

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
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.ExperimentProperty;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.GeolocationProperty;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.dms.StockProperty;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermRelationship;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
	private Geolocation geolocation;
	private Germplasm germplasm;
	private StockModel stock;
	private CVTerm variable;
	private CVTerm scale;
	private List<CVTerm> categories;

	@Before
	public void setUp() throws Exception {
		if (this.cvtermRelationshipDao == null) {			
			this.cvtermRelationshipDao = new CVTermRelationshipDao(this.sessionProvder.getSession());
		}
		
		if (this.cvtermDao == null) {			
			this.cvtermDao = new CVTermDao(this.sessionProvder.getSession());
		}
		
		if (this.projectDao == null) {			
			this.projectDao = new DmsProjectDao(this.sessionProvder.getSession());
		}
		
		if (this.projectPropDao == null) {			
			this.projectPropDao = new ProjectPropertyDao(this.sessionProvder.getSession());
		}
		
		if (this.experimentDao == null) {			
			this.experimentDao = new ExperimentDao(this.sessionProvder.getSession());
		}
		
		if (this.experimentPropDao == null) {			
			this.experimentPropDao = new ExperimentPropertyDao(this.sessionProvder.getSession());
		}
		
		if (this.phenotypeDao == null) {			
			this.phenotypeDao = new PhenotypeDao(this.sessionProvder.getSession());
		}
		
		if (this.germplasmDao == null) {
			this.germplasmDao = new GermplasmDAO(this.sessionProvder.getSession());
		}
		
		if (this.stockDao == null) {			
			this.stockDao = new StockDao(this.sessionProvder.getSession());
		}
		
		if (this.stockPropDao == null) {			
			this.stockPropDao = new StockPropertyDao(this.sessionProvder.getSession());
		}
		
		if (this.geolocationDao == null) {			
			this.geolocationDao = new GeolocationDao(this.sessionProvder.getSession());
		}
		
		if (this.geolocPropDao == null) {			
			this.geolocPropDao = new GeolocationPropertyDao(this.sessionProvder.getSession());
		}
		
		this.createTestStudy();
		this.createTestOntologyData();
		
	}

	private void createTestStudy() {
		this.study = new DmsProject();
		this.study.setName("Test Project");
		this.study.setDescription("Test Project");
		this.projectDao.save(this.study);
	}
	
	private void createTestOntologyData() {
		if (this.scale == null) {
			this.scale = CVTermTestDataInitializer.createTerm(RandomStringUtils.randomAlphanumeric(50), CvId.SCALES.getId());
			this.cvtermDao.save(this.scale);
		}
		
		if (this.categories == null) {
			this.categories = new ArrayList<>();
			for (int i = 0; i < NO_OF_CATEGORIES; i++) {
				final CVTerm category = this.cvtermDao
						.save(CVTermTestDataInitializer.createTerm(RandomStringUtils.randomAlphanumeric(20), CvId.IBDB_TERMS.getId()));
				this.categories.add(category);
				
				final CVTermRelationship hasValueRelationship = new CVTermRelationship();
				hasValueRelationship.setTypeId(TermId.HAS_VALUE.getId());
				hasValueRelationship.setSubjectId(this.scale.getCvTermId());
				hasValueRelationship.setObjectId(category.getCvTermId());
				this.cvtermRelationshipDao.save(hasValueRelationship);
			}
		}
		
		if (this.variable == null) {
			this.variable = CVTermTestDataInitializer.createTerm(RandomStringUtils.randomAlphanumeric(50), CvId.VARIABLES.getId());
			this.cvtermDao.save(this.variable);
			
			final CVTermRelationship hasScaleRelationship = new CVTermRelationship();
			hasScaleRelationship.setTypeId(TermId.HAS_SCALE.getId());
			hasScaleRelationship.setSubjectId(this.variable.getCvTermId());
			hasScaleRelationship.setObjectId(this.scale.getCvTermId());
			this.cvtermRelationshipDao.save(hasScaleRelationship);
		}
	}
	
	@Test
	public void testGetScaleCategoriesUsedInStudies() {
		this.createStudyData();
		final Set<String> usedCategories = this.cvtermRelationshipDao.getCategoriesInUse(this.scale.getCvTermId());
		final Iterator<String> itr = usedCategories.iterator();

		assertEquals(6, usedCategories.size());
		while (itr.hasNext()) {
			String categoricalName =itr.next();
			assertTrue(this.categories.stream().anyMatch(cvTerm ->  cvTerm.getName().equals(categoricalName)));
		}
	}

	private void createStudyData() {
		this.createObservations();
		this.createGermplasmDescriptor();
		this.createEnvironmentFactor();
		this.createTrialDesignFactor();
		this.createStudyConstant();
	}

	@Test
	public void testGetScaleCategoriesUsedInObservations() {
		this.createObservations();
		final List<String> usedCategories = this.cvtermRelationshipDao.getScaleCategoriesUsedInObservations(this.scale.getCvTermId());
		assertEquals(2, usedCategories.size());
		assertEquals(this.categories.get(0).getName(), usedCategories.get(0));
		assertEquals(this.categories.get(1).getName(), usedCategories.get(1));
	}
	
	@Test
	public void testGetScaleCategoriesUsedInObservationsWithStudyDeleted() {
		this.markTestStudyAsDeleted();
		this.createObservations();
		// For some reason, need to flush to be able to get the latest state of test study as deleted
		this.sessionProvder.getSession().flush();
		final List<String> usedCategories = this.cvtermRelationshipDao.getScaleCategoriesUsedInObservations(this.scale.getCvTermId());
		assertEquals(0, usedCategories.size());
	}
	
	
	@Test
	public void testGetScaleCategoriesUsedAsConditions() {
		this.createStudyConstant();
		final List<String> usedCategories = this.cvtermRelationshipDao.getScaleCategoriesUsedAsConditions(this.scale.getCvTermId());
		assertEquals(1, usedCategories.size());
		assertEquals(this.categories.get(2).getName(), usedCategories.get(0));
	}
	
	@Test
	public void testGetScaleCategoriesUsedAsConditionsWithStudyDeleted() {
		this.markTestStudyAsDeleted();
		this.createStudyConstant();
		// For some reason, need to flush to be able to get the latest state of test study as deleted
		this.sessionProvder.getSession().flush();
		final List<String> usedCategories = this.cvtermRelationshipDao.getScaleCategoriesUsedAsConditions(this.scale.getCvTermId());
		assertEquals(0, usedCategories.size());
	}
	
	@Test
	public void testGetScaleCategoriesUsedInStudyEntries() {
		this.createGermplasmDescriptor();
		this.sessionProvder.getSession().flush();
		final List<String> usedCategories =
			this.cvtermRelationshipDao.getScaleCategoriesUsedInStudyEntries(this.scale.getCvTermId());
		assertEquals(1, usedCategories.size());
		assertEquals(this.categories.get(3).getName(), usedCategories.get(0));
	}
	
	@Test
	public void testGetScaleCategoriesUsedInStudyEntriesWithStudyDeleted() {
		this.markTestStudyAsDeleted();
		this.createGermplasmDescriptor();
		// For some reason, need to flush to be able to get the latest state of test study as deleted
		this.sessionProvder.getSession().flush();
		final List<String> usedCategories = this.cvtermRelationshipDao.getScaleCategoriesUsedInStudyEntries(this.scale.getCvTermId());
		assertEquals(0, usedCategories.size());
	}
	
	@Test
	public void testGetScaleCategoriesUsedAsTrialDesignFactors() {
		this.createTrialDesignFactor();
		this.sessionProvder.getSession().flush();
		final List<String> usedCategories = this.cvtermRelationshipDao.getScaleCategoriesUsedAsTrialDesignFactors(this.scale.getCvTermId());
		assertEquals(1, usedCategories.size());
		assertEquals(this.categories.get(4).getName(), usedCategories.get(0));
	}
	
	@Test
	public void testGetScaleCategoriesUsedAsTrialDesignFactorsWithStudyDeleted() {
		this.markTestStudyAsDeleted();
		this.createTrialDesignFactor();
		// For some reason, need to flush to be able to get the latest state of test study as deleted
		this.sessionProvder.getSession().flush();
		final List<String> usedCategories = this.cvtermRelationshipDao.getScaleCategoriesUsedAsTrialDesignFactors(this.scale.getCvTermId());
		assertEquals(0, usedCategories.size());
	}
	
	@Test
	public void testgetScaleCategoriesUsedAsEnvironmentFactors() {
		this.createEnvironmentFactor();
		final List<String> usedCategories = this.cvtermRelationshipDao.getScaleCategoriesUsedAsEnvironmentFactors(this.scale.getCvTermId());
		assertEquals(1, usedCategories.size());
		assertEquals(this.categories.get(5).getName(), usedCategories.get(0));
	}
	
	@Test
	public void testgetScaleCategoriesUsedAsEnvironmentFactorsWithStudyDeleted() {
		this.markTestStudyAsDeleted();
		this.createEnvironmentFactor();
		// For some reason, need to flush to be able to get the latest state of test study as deleted
		this.sessionProvder.getSession().flush();
		final List<String> usedCategories = this.cvtermRelationshipDao.getScaleCategoriesUsedAsEnvironmentFactors(this.scale.getCvTermId());
		assertEquals(0, usedCategories.size());
	}
	
	private void createObservations() {
		final ExperimentModel experiment1 = new ExperimentModel();
		experiment1.setGeoLocation(getGeolocation());
		experiment1.setTypeId(TermId.PLOT_EXPERIMENT.getId());
		experiment1.setProject(this.study);
		experiment1.setStock(getStock());
		this.experimentDao.save(experiment1);

		final Phenotype phenotype1 = new Phenotype();
		phenotype1.setObservableId(this.variable.getCvTermId());
		phenotype1.setExperiment(experiment1);
		final CVTerm category = this.categories.get(0);
		phenotype1.setValue(category.getName());
		phenotype1.setcValue(category.getCvTermId());
		this.phenotypeDao.save(phenotype1);
		
		final ExperimentModel experiment2 = new ExperimentModel();
		experiment2.setGeoLocation(getGeolocation());
		experiment2.setTypeId(TermId.PLOT_EXPERIMENT.getId());
		experiment2.setProject(this.study);
		experiment2.setStock(getStock());
		this.experimentDao.save(experiment2);
		
		final Phenotype phenotype2 = new Phenotype();
		phenotype2.setObservableId(this.variable.getCvTermId());
		phenotype2.setExperiment(experiment2);
		final CVTerm category2 = this.categories.get(1);
		phenotype2.setValue(category2.getName());
		phenotype2.setcValue(category2.getCvTermId());
		this.phenotypeDao.save(phenotype2);
		
		// For some reason, need to flush to be able to get the phenotypes created
		this.sessionProvder.getSession().flush();
	}
	
	private void createGermplasmDescriptor() {
		StockModel  stock = getStock();
		final StockProperty prop = new StockProperty(stock, this.variable.getCvTermId(), null, this.categories.get(3).getCvTermId());
		this.stockPropDao.save(prop);

	}
	
	private void createTrialDesignFactor() {
		final ExperimentModel experiment = new ExperimentModel();
		experiment.setGeoLocation(getGeolocation());
		experiment.setTypeId(TermId.PLOT_EXPERIMENT.getId());
		experiment.setProject(this.study);
		experiment.setStock(getStock());
		this.experimentDao.save(experiment);
		
		final ExperimentProperty  prop = new ExperimentProperty();
		prop.setRank(1);
		prop.setExperiment(experiment);
		prop.setTypeId(this.variable.getCvTermId());
		prop.setValue(String.valueOf(this.categories.get(4).getCvTermId()));
		this.experimentPropDao.save(prop);
	}
	
	private void createEnvironmentFactor() {
		final ExperimentModel experiment = new ExperimentModel();
		experiment.setGeoLocation(getGeolocation());
		experiment.setTypeId(TermId.PLOT_EXPERIMENT.getId());
		experiment.setProject(this.study);
		experiment.setStock(getStock());
		this.experimentDao.save(experiment);
		
		final GeolocationProperty prop = new GeolocationProperty();
		prop.setRank(1);
		prop.setGeolocation(getGeolocation());
		prop.setType(this.variable.getCvTermId());
		prop.setValue(String.valueOf(this.categories.get(5).getCvTermId()));
		this.geolocPropDao.save(prop);
	}

	private StockModel getStock() {
		if (this.stock == null) {			
			this.stock = new StockModel();
			this.stock.setIsObsolete(false);
			this.stock.setUniqueName("1");
			this.stock.setGermplasm(getGermplasm());
			this.stock.setCross("-");
			this.stock.setProject(this.study);
			this.stockDao.save(this.stock);
		}
		return this.stock;
	}

	private Germplasm getGermplasm() {
		if (this.germplasm == null) {			
			this.germplasm = GermplasmTestDataInitializer.createGermplasm(1);
			germplasm.setGid(null);
			this.germplasmDao.save(germplasm);
		}
		return germplasm;
	}

	private Geolocation getGeolocation() {
		if (this.geolocation == null) {			
			this.geolocation = new Geolocation();
			this.geolocationDao.save(this.geolocation);
		}
		return this.geolocation;
	}
	
	private void createStudyConstant() {
		final ProjectProperty prop = new ProjectProperty();
		prop.setProject(this.study);
		prop.setAlias(RandomStringUtils.randomAlphabetic(10));
		prop.setVariableId(this.variable.getCvTermId());
		prop.setRank(1);
		prop.setTypeId(VariableType.ENVIRONMENT_CONDITION.getId());
		prop.setValue(String.valueOf(this.categories.get(2).getCvTermId()));
		this.projectPropDao.save(prop);
	}
	
	private void markTestStudyAsDeleted() {
		this.study.setDeleted(true);
		this.projectDao.save(this.study);
	}
}
