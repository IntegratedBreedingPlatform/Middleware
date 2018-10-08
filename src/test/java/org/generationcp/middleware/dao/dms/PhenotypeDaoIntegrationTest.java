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
import org.generationcp.middleware.dao.oms.CVTermDao;
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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PhenotypeDaoIntegrationTest extends IntegrationTestBase {

	private static final int NO_OF_GERMPLASM = 5;

	private PhenotypeDao phenotypeDao;
	
	private GeolocationDao geolocationDao;
	
	private ExperimentDao experimentDao;
	
	private StockDao stockDao;
	
	private GermplasmDAO germplasmDao;
	
	private DmsProjectDao dmsProjectDao;
	
	private CVTermDao cvTermDao;
	
	private DmsProject study;
	private CVTerm trait;

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
		
		if (this.study == null) {
			this.study = new DmsProject();
			this.study.setName("Test Project");
			this.study.setDescription("Test Project");
			this.dmsProjectDao.save(this.study);
		}
		
		if (this.trait == null) {
			this.trait = CVTermTestDataInitializer.createTerm(RandomStringUtils.randomAlphanumeric(50), CvId.VARIABLES.getId());
			this.cvTermDao.save(this.trait);
		}

		
	}


	@Test
	public void testContainsAtLeast2CommonEntriesWithValues() throws Exception {
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
	
	private Integer createEnvironmentData(final Integer numberOfReps, final boolean withPhenotype) {
		
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
			
			// Create two experiments for the same stock
			for (int j=0; j < numberOfReps; j++) {
				final ExperimentModel experimentModel = new ExperimentModel();
				experimentModel.setGeoLocation(geolocation);
				experimentModel.setTypeId(TermId.PLOT_EXPERIMENT.getId());
				experimentModel.setObsUnitId(RandomStringUtils.randomAlphabetic(13));
				experimentModel.setProject(this.study);
				experimentModel.setStock(stockModel);
				this.experimentDao.saveOrUpdate(experimentModel);
				
				if (withPhenotype) {
					final Phenotype phenotype = new Phenotype();
					phenotype.setObservableId(this.trait.getCvTermId());
					phenotype.setExperiment(experimentModel);
					phenotype.setValue(i + "." + j);
					this.phenotypeDao.save(phenotype);
				}
			}

		}
		
		return geolocation.getLocationId();
	}
}
