package org.generationcp.middleware.dao.dms;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ExperimentDaoIntegrationTest extends IntegrationTestBase {
	
	private static final int NO_OF_GERMPLASM = 5;
	
	private DmsProjectDao dmsProjectDao;
	
	private ExperimentDao experimentDao;
	
	private GeolocationDao geolocationDao;
	
	private StockDao stockDao;
	
	private GermplasmDAO germplasmDao;
	
	private DmsProject study;
	
	private List<ExperimentModel> experiments;
	
	@Before
	public void setUp() throws Exception {
		
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
			
		if (this.study == null) {
			this.study = new DmsProject();
			this.study.setName("Test Project " + new Random().nextInt());
			this.study.setDescription("Test Project");
			this.dmsProjectDao.save(this.study);
		}	
	}
	
	@Test
	public void testIsValidExperiment() {
		this.createExperiments();
		final Integer datasetId = this.study.getProjectId();
		final Integer validExperimentId = this.experiments.get(0).getNdExperimentId();
		Assert.assertFalse(this.experimentDao.isValidExperiment(datasetId + 1, validExperimentId));
		Assert.assertFalse(this.experimentDao.isValidExperiment(datasetId, validExperimentId + 10));
		Assert.assertTrue(this.experimentDao.isValidExperiment(datasetId, validExperimentId));
	}
	
	private Integer createExperiments() {
		this.experiments = new ArrayList<>();
		
		final Geolocation geolocation = new Geolocation();
		this.geolocationDao.saveOrUpdate(geolocation);

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
			this.experiments.add(this.experimentDao.saveOrUpdate(experimentModel));
		}
		
		return geolocation.getLocationId();
	}

}
