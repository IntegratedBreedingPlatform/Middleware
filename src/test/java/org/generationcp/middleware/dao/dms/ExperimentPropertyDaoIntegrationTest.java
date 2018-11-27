package org.generationcp.middleware.dao.dms;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.data.initializer.DMSVariableTestDataInitializer;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.dms.ExperimentValues;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.operation.saver.ExperimentModelSaver;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.GeolocationProperty;
import org.generationcp.middleware.pojos.dms.ProjectRelationship;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ExperimentPropertyDaoIntegrationTest extends IntegrationTestBase {
	
	private static final int NO_OF_GERMPLASM = 5;

	private ExperimentModelSaver experimentModelSaver;

	private ExperimentPropertyDao experimentPropertyDao;
	
	private GeolocationDao geolocationDao;
	
	private GeolocationPropertyDao geolocPropDao;

	private ExperimentDao experimentDao;

	private StockDao stockDao;

	private GermplasmDAO germplasmDao;

	private DmsProjectDao dmsProjectDao;
	
	private ProjectRelationshipDao projRelDao;
	
	private DmsProject study;

	@Before
	public void setUp() {
		this.experimentPropertyDao = new ExperimentPropertyDao();
		this.experimentPropertyDao.setSession(this.sessionProvder.getSession());
		
		if (this.geolocationDao == null) {
			this.geolocationDao = new GeolocationDao();
			this.geolocationDao.setSession(this.sessionProvder.getSession());
		}
		
		if (this.geolocPropDao == null) {
			this.geolocPropDao = new GeolocationPropertyDao();
			this.geolocPropDao.setSession(this.sessionProvder.getSession());
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
		
		if (this.projRelDao == null) {
			this.projRelDao = new ProjectRelationshipDao();
			this.projRelDao.setSession(this.sessionProvder.getSession());
		}
		
		this.experimentModelSaver = new ExperimentModelSaver(this.sessionProvder);
		
		if (this.study == null) {
			this.study = new DmsProject();
			this.study.setName("Test Project");
			this.study.setDescription("Test Project");
			this.dmsProjectDao.save(this.study);
		}
	}

	@Test
	public void testGetTreatmentFactorValues() {
		final VariableList factors = new VariableList();
		factors.add(DMSVariableTestDataInitializer.createVariable(1001, "999", DataType.NUMERIC_VARIABLE.getId(), VariableType.TREATMENT_FACTOR));
		factors.add(DMSVariableTestDataInitializer.createVariable(1002, "Value", DataType.NUMERIC_VARIABLE.getId(), VariableType.TREATMENT_FACTOR));
		final ExperimentValues values = new ExperimentValues();
		values.setVariableList(factors);
		values.setLocationId(this.experimentModelSaver.createNewGeoLocation().getLocationId());
		values.setGermplasmId(1);
		//Save the experiment
		this.experimentModelSaver.addOrUpdateExperiment(1, ExperimentType.STUDY_INFORMATION, values);
		final List<String> treatmentFactorValues = this.experimentPropertyDao.getTreatmentFactorValues(1001, 1002, 1);
		Assert.assertEquals(1, treatmentFactorValues.size());
		Assert.assertEquals("Value", treatmentFactorValues.get(0));
	}
	
	@Test
	public void testHasFieldmap(){
		final Integer env1 = this.createEnvironmentData(true);
		final Integer env2 = this.createEnvironmentData(false);
		final Integer env3 = this.createEnvironmentData(true);
		final Map<Integer, Boolean> map = this.experimentPropertyDao.getInstanceHasFieldMapAsMap(this.study.getProjectId());
		Assert.assertEquals(3, map.size());
		Assert.assertNotNull(map.get(env1));
		Assert.assertTrue(map.get(env1));
		Assert.assertNotNull(map.get(env2));
		Assert.assertFalse(map.get(env2));
		Assert.assertNotNull(map.get(env3));
		Assert.assertTrue(map.get(env3));
	}
	
	private Integer createEnvironmentData(final boolean hasFieldmap) {
		final DmsProject dataset = new DmsProject();
		dataset.setName(RandomStringUtils.randomAlphabetic(10));
		dataset.setDescription(RandomStringUtils.randomAlphabetic(10));
		this.dmsProjectDao.save(dataset);
		
		final ProjectRelationship rel = new ProjectRelationship();
		rel.setSubjectProject(dataset);
		rel.setObjectProject(this.study);
		rel.setTypeId(TermId.BELONGS_TO_STUDY.getId());
		this.projRelDao.save(rel);

		final Geolocation geolocation = new Geolocation();
		this.geolocationDao.saveOrUpdate(geolocation);
		
		if (hasFieldmap){
			final GeolocationProperty prop = new GeolocationProperty();
			prop.setGeolocation(geolocation);
			prop.setType(TermId.BLOCK_ID.getId());
			prop.setRank(1);
			prop.setValue(geolocation.getLocationId().toString());
			this.geolocPropDao.save(prop);
		}
		
		for (int i = 1; i < NO_OF_GERMPLASM + 1; i++) {
			final Germplasm germplasm = GermplasmTestDataInitializer.createGermplasm(1);
			germplasm.setGid(null);
			this.germplasmDao.save(germplasm);

			final StockModel stockModel = new StockModel();
			stockModel.setName("Germplasm " + i);
			stockModel.setIsObsolete(false);
			stockModel.setTypeId(TermId.ENTRY_CODE.getId());
			stockModel.setUniqueName(String.valueOf(i));
			stockModel.setGermplasm(germplasm);
			this.stockDao.saveOrUpdate(stockModel);

			final ExperimentModel experimentModel = new ExperimentModel();
			experimentModel.setGeoLocation(geolocation);
			experimentModel.setTypeId(TermId.PLOT_EXPERIMENT.getId());
			experimentModel.setObsUnitId(RandomStringUtils.randomAlphabetic(13));
			experimentModel.setProject(dataset);
			experimentModel.setStock(stockModel);
			this.experimentDao.saveOrUpdate(experimentModel);
		}

		return geolocation.getLocationId();
	}
}
