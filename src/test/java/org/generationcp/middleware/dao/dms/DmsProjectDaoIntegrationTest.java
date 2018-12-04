package org.generationcp.middleware.dao.dms;

import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.GeolocationProperty;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.service.impl.study.StudyInstance;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Optional;


public class DmsProjectDaoIntegrationTest extends IntegrationTestBase {
	
	private static final int NO_OF_GERMPLASM = 5;

	private ExperimentPropertyDao experimentPropertyDao;
	
	private GeolocationDao geolocationDao;
	
	private GeolocationPropertyDao geolocPropDao;

	private ExperimentDao experimentDao;

	private StockDao stockDao;

	private GermplasmDAO germplasmDao;

	private DmsProjectDao dmsProjectDao;
	
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
		
		if (this.study == null) {
			this.study = new DmsProject();
			this.study.setName("Test Project");
			this.study.setDescription("Test Project");
			this.dmsProjectDao.save(this.study);
		}
	}
	
	@Test
	public void testGetDatasetInstances() {
		final Integer env1 = this.createEnvironmentData("1", 1, Optional.<String>absent(), Optional.of(1));
		final Integer env2 = this.createEnvironmentData("2", 2, Optional.<String>absent(), Optional.of(2));
		final String customLocation = RandomStringUtils.randomAlphabetic(10);
		final Integer env3 = this.createEnvironmentData("3", 3, Optional.of(customLocation), Optional.<Integer>absent());
		final List<StudyInstance> instances = this.dmsProjectDao.getDatasetInstances(this.study.getProjectId());
		Assert.assertEquals(3, instances.size());
		
		final StudyInstance instance1 = instances.get(0);
		Assert.assertEquals(env1.intValue(), instance1.getInstanceDbId());
		Assert.assertEquals(1, instance1.getInstanceNumber());
		Assert.assertEquals("Afghanistan", instance1.getLocationName());
		Assert.assertEquals("AFG", instance1.getLocationAbbreviation());
		Assert.assertNull(instance1.getCustomLocationAbbreviation());
		Assert.assertTrue(instance1.isHasFieldmap());
		
		final StudyInstance instance2 = instances.get(1);
		Assert.assertEquals(env2.intValue(), instance2.getInstanceDbId());
		Assert.assertEquals(2, instance2.getInstanceNumber());
		Assert.assertEquals("Albania", instance2.getLocationName());
		Assert.assertEquals("ALB", instance2.getLocationAbbreviation());
		Assert.assertNull(instance2.getCustomLocationAbbreviation());
		Assert.assertTrue(instance2.isHasFieldmap());
		
		final StudyInstance instance3 = instances.get(2);
		Assert.assertEquals(env3.intValue(), instance3.getInstanceDbId());
		Assert.assertEquals(3, instance3.getInstanceNumber());
		Assert.assertEquals("Algeria", instance3.getLocationName());
		Assert.assertEquals("DZA", instance3.getLocationAbbreviation());
		Assert.assertEquals(customLocation, instance3.getCustomLocationAbbreviation());
		Assert.assertFalse(instance3.isHasFieldmap());
	}
	
	private Integer createEnvironmentData(final String instanceNumber, final Integer locationId, Optional<String> customAbbev, Optional<Integer> blockId) {
		final Geolocation geolocation = new Geolocation();
		geolocation.setDescription(instanceNumber);
		this.geolocationDao.saveOrUpdate(geolocation);
		
		final GeolocationProperty prop = new GeolocationProperty();
		prop.setGeolocation(geolocation);
		prop.setType(TermId.LOCATION_ID.getId());
		prop.setRank(1);
		prop.setValue(locationId.toString());
		this.geolocPropDao.save(prop);
		
		if (customAbbev.isPresent()){
			final GeolocationProperty prop2 = new GeolocationProperty();
			prop2.setGeolocation(geolocation);
			prop2.setType(TermId.LOCATION_ABBR.getId());
			prop2.setRank(2);
			prop2.setValue(customAbbev.get());
			this.geolocPropDao.save(prop2);
		}
		
		if (blockId.isPresent()){
			final GeolocationProperty prop3 = new GeolocationProperty();
			prop3.setGeolocation(geolocation);
			prop3.setType(TermId.BLOCK_ID.getId());
			prop3.setRank(3);
			prop3.setValue(blockId.get().toString());
			this.geolocPropDao.save(prop3);
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
			experimentModel.setProject(this.study);
			experimentModel.setStock(stockModel);
			this.experimentDao.saveOrUpdate(experimentModel);
		}

		return geolocation.getLocationId();
	}

}
