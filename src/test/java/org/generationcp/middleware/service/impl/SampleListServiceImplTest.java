package org.generationcp.middleware.service.impl;

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

import java.util.Date;

public class SampleListServiceImplTest extends IntegrationTestBase {

	private static final String SAMPLE_LIST_TYPE = "SAMPLE LIST";

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

		SampleList sampleList = new SampleList();
		sampleList.setCreatedDate(new Date());
		sampleList.setCreatedBy(userDao.getUserByUserName("admin"));
		sampleList.setDescription("description");
		sampleList.setListName("trialName" + "#" + Util.getCurrentDateAsStringValue("yyyyMMddHHmmssSSS"));
		sampleList.setNotes("Notes");
		sampleList.setType(SAMPLE_LIST_TYPE);

		Plant plant1 = getPlant();
		Sample sample = new Sample();
		getSample(sampleList, plant1, sample);

		sampleListDao.saveOrUpdate(sampleList);
		Assert.assertNotNull(sampleList.getListId());

	}

	private void getSample(SampleList sampleList, Plant plant1, Sample sample) {
		String cropPrefix = "ABCD";
		sample.setPlant(plant1);
		sample.setTakenBy(userDao.getUserByUserName("admin"));
		sample.setSampleName("GID");
		sample.setCreatedDate(new Date());
		sample.setSamplingDate(new Date());
		sample.setSampleBusinessKey("S" + cropPrefix);
		sample.setSampleList(sampleList);
	}

	private Plant getPlant() {
		Plant plant = new Plant();

		plant.setCreatedDate(new Date());
		plant.setExperiment(new ExperimentModel());
		plant.setPlantBusinessKey("P1234");
		plant.setPlantNumber(0);
		return plant;
	}
}
