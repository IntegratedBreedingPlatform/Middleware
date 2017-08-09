package org.generationcp.middleware.service.impl;

import org.codehaus.groovy.runtime.dgmimpl.arrays.IntegerArrayGetAtMetaMethod;
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

	public static final String P = "P";
	private static final String SAMPLE_LIST_TYPE = "SAMPLE LIST";
	public static final String ADMIN = "admin";
	public static final String DESCRIPTION = "description";
	public static final String YYYY_M_MDD_HH = "yyyyMMddHH";
	public static final String TRIAL_NAME = "trialName#";
	public static final String NOTES = "Notes";
	public static final String CROP_PREFIX = "ABCD";
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

		SampleList sampleList = new SampleList();
		sampleList.setCreatedDate(new Date());
		sampleList.setCreatedBy(userDao.getUserByUserName(ADMIN));
		sampleList.setDescription(DESCRIPTION);
		sampleList.setListName(TRIAL_NAME + Util.getCurrentDateAsStringValue(YYYY_M_MDD_HH));
		sampleList.setNotes(NOTES);
		sampleList.setType(SAMPLE_LIST_TYPE);

		Plant plant = getPlant();
		Sample sample = new Sample();
		getSample(sampleList, plant, sample);

		sampleListDao.saveOrUpdate(sampleList);
		Assert.assertNotNull(sampleList.getListId());
		Assert.assertEquals(sampleList.getCreatedBy().getName(), ADMIN);
		Assert.assertEquals(sampleList.getDescription(), DESCRIPTION);
		Assert.assertEquals(sampleList.getListName(), TRIAL_NAME + Util.getCurrentDateAsStringValue(YYYY_M_MDD_HH));
		Assert.assertEquals(sampleList.getNotes(), NOTES);
		Assert.assertEquals(sampleList.getType(), SAMPLE_LIST_TYPE);
		Assert.assertEquals(plant.getPlantBusinessKey(), P + CROP_PREFIX);
		Assert.assertEquals(plant.getPlantNumber(), new Integer(0));
		Assert.assertEquals(sample.getPlant(), plant);
		Assert.assertEquals(sample.getTakenBy().getName(), ADMIN);
		Assert.assertEquals(sample.getSampleName(), GID);
		Assert.assertEquals(sample.getSampleBusinessKey(), S + CROP_PREFIX);
		Assert.assertEquals(sample.getSampleList(), sampleList);

	}

	private void getSample(SampleList sampleList, Plant plant1, Sample sample) {

		sample.setPlant(plant1);
		sample.setTakenBy(userDao.getUserByUserName(ADMIN));
		sample.setSampleName(GID);
		sample.setCreatedDate(new Date());
		sample.setSamplingDate(new Date());
		sample.setSampleBusinessKey(S + CROP_PREFIX);
		sample.setSampleList(sampleList);
	}

	private Plant getPlant() {
		Plant plant = new Plant();

		plant.setCreatedDate(new Date());
		plant.setExperiment(new ExperimentModel());
		plant.setPlantBusinessKey(P + CROP_PREFIX);
		plant.setPlantNumber(0);
		return plant;
	}
}
