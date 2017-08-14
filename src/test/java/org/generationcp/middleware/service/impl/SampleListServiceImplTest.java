
package org.generationcp.middleware.service.impl;

import java.util.Date;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.PlantDao;
import org.generationcp.middleware.dao.SampleListDao;
import org.generationcp.middleware.dao.UserDAO;
import org.generationcp.middleware.pojos.Plant;
import org.generationcp.middleware.pojos.Sample;
import org.generationcp.middleware.pojos.SampleList;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.util.Util;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SampleListServiceImplTest extends IntegrationTestBase {

	public static final String P = "P";
	public static final String ADMIN = "admin";
	public static final String DESCRIPTION = "description";
	private static final String YYYY_M_MDD_HH = "yyyyMMddHH";
	private static final String TRIAL_NAME = "trialName#";
	public static final String NOTES = "Notes";
	private static final String CROP_PREFIX = "ABCD";
	public static final String GID = "GID";
	public static final String S = "S";

	private SampleListDao sampleListDao;
	private UserDAO userDao;
	private PlantDao plantDao;

	@Before
	public void setUp() throws Exception {
		this.sampleListDao = new SampleListDao();
		this.sampleListDao.setSession(this.sessionProvder.getSession());

		this.userDao = new UserDAO();
		this.userDao.setSession(this.sessionProvder.getSession());

		this.plantDao = new PlantDao();
		this.plantDao.setSession(this.sessionProvder.getSession());
	}

	@Test
	public void testCreateSampleList() {

		final SampleList sampleList = new SampleList();
		sampleList.setCreatedDate(new Date());
		sampleList.setCreatedBy(this.userDao.getUserByUserName(SampleListServiceImplTest.ADMIN));
		sampleList.setDescription(SampleListServiceImplTest.DESCRIPTION);
		sampleList.setListName(
				SampleListServiceImplTest.TRIAL_NAME + Util.getCurrentDateAsStringValue(SampleListServiceImplTest.YYYY_M_MDD_HH));
		sampleList.setNotes(SampleListServiceImplTest.NOTES);

		final Plant plant = this.getPlant();
		final Sample sample = new Sample();
		this.getSample(sampleList, plant, sample);

		this.sampleListDao.saveOrUpdate(sampleList);
		Assert.assertNotNull(sampleList.getListId());
		Assert.assertEquals(sampleList.getCreatedBy().getName(), SampleListServiceImplTest.ADMIN);
		Assert.assertEquals(sampleList.getDescription(), SampleListServiceImplTest.DESCRIPTION);
		Assert.assertEquals(sampleList.getListName(),
				SampleListServiceImplTest.TRIAL_NAME + Util.getCurrentDateAsStringValue(SampleListServiceImplTest.YYYY_M_MDD_HH));
		Assert.assertEquals(sampleList.getNotes(), SampleListServiceImplTest.NOTES);
		Assert.assertEquals(plant.getPlantBusinessKey(), SampleListServiceImplTest.P + SampleListServiceImplTest.CROP_PREFIX);
		Assert.assertEquals(plant.getPlantNumber(), new Integer(0));
		Assert.assertEquals(sample.getPlant(), plant);
		Assert.assertEquals(sample.getTakenBy().getName(), SampleListServiceImplTest.ADMIN);
		Assert.assertEquals(sample.getSampleName(), SampleListServiceImplTest.GID);
		Assert.assertEquals(sample.getSampleBusinessKey(), SampleListServiceImplTest.S + SampleListServiceImplTest.CROP_PREFIX);
		Assert.assertEquals(sample.getSampleList(), sampleList);

	}

	private void getSample(final SampleList sampleList, final Plant plant1, final Sample sample) {

		sample.setPlant(plant1);
		sample.setTakenBy(this.userDao.getUserByUserName(SampleListServiceImplTest.ADMIN));
		sample.setSampleName(SampleListServiceImplTest.GID);
		sample.setCreatedDate(new Date());
		sample.setSamplingDate(new Date());
		sample.setSampleBusinessKey(SampleListServiceImplTest.S + SampleListServiceImplTest.CROP_PREFIX);
		sample.setSampleList(sampleList);
	}

	private Plant getPlant() {
		final Plant plant = new Plant();

		plant.setCreatedDate(new Date());
		plant.setExperiment(new ExperimentModel());
		plant.setPlantBusinessKey(SampleListServiceImplTest.P + SampleListServiceImplTest.CROP_PREFIX);
		plant.setPlantNumber(0);
		return plant;
	}
}
