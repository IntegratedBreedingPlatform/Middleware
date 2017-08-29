
package org.generationcp.middleware.dao;

import java.util.Date;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.enumeration.SampleListType;
import org.generationcp.middleware.pojos.Plant;
import org.generationcp.middleware.pojos.Sample;
import org.generationcp.middleware.pojos.SampleList;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.util.Util;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SampleListDaoTest extends IntegrationTestBase {

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

	public static final String ROOT_FOLDER = "Samples";


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

		final SampleList sampleList = this.getSampleList();

		final Plant plant = this.getPlant();
		final Sample sample = new Sample();
		this.getSample(sampleList, plant, sample);

		this.sampleListDao.saveOrUpdate(sampleList);
		Assert.assertNotNull(sampleList.getId());
		Assert.assertEquals(sampleList.getCreatedBy().getName(), SampleListDaoTest.ADMIN);
		Assert.assertEquals(sampleList.getDescription(), SampleListDaoTest.DESCRIPTION);
		Assert.assertEquals(sampleList.getListName(),
				SampleListDaoTest.TRIAL_NAME + Util.getCurrentDateAsStringValue(SampleListDaoTest.YYYY_M_MDD_HH));
		Assert.assertEquals(sampleList.getNotes(), SampleListDaoTest.NOTES);
		Assert.assertEquals(plant.getPlantBusinessKey(), SampleListDaoTest.P + SampleListDaoTest.CROP_PREFIX);
		Assert.assertEquals(plant.getPlantNumber(), new Integer(0));
		Assert.assertEquals(sample.getPlant(), plant);
		Assert.assertEquals(sample.getTakenBy().getName(), SampleListDaoTest.ADMIN);
		Assert.assertEquals(sample.getSampleName(), SampleListDaoTest.GID);
		Assert.assertEquals(sample.getSampleBusinessKey(), SampleListDaoTest.S + SampleListDaoTest.CROP_PREFIX);
		Assert.assertEquals(sample.getSampleList(), sampleList);

	}

	@Test
	public void testGetSampleListByParentAndNameOk() throws Exception {
		final SampleList sampleList = this.getSampleList();
		final SampleList parent = this.sampleListDao.getBySampleListName(ROOT_FOLDER);
		sampleList.setHierarchy(parent);
		sampleList.setType(SampleListType.SAMPLE_LIST);
		this.sampleListDao.save(sampleList);
		final SampleList uSampleList = this.sampleListDao.getSampleListByParentAndName(sampleList.getListName(), parent.getId());
		Assert.assertEquals(uSampleList.getId(), sampleList.getId());
	}

	@Test(expected = NullPointerException.class)
	public void testGetSampleListByParentAndNameNullSampleName() throws Exception {
		this.sampleListDao.getSampleListByParentAndName(null, 1);
	}

	@Test(expected = NullPointerException.class)
	public void testGetSampleListByParentAndNameNullParent() throws Exception {
		this.sampleListDao.getSampleListByParentAndName("name", null);
	}

	private void getSample(final SampleList sampleList, final Plant plant1, final Sample sample) {

		sample.setPlant(plant1);
		sample.setTakenBy(this.userDao.getUserByUserName(SampleListDaoTest.ADMIN));
		sample.setSampleName(SampleListDaoTest.GID);
		sample.setCreatedDate(new Date());
		sample.setSamplingDate(new Date());
		sample.setSampleBusinessKey(SampleListDaoTest.S + SampleListDaoTest.CROP_PREFIX);
		sample.setSampleList(sampleList);
	}

	private Plant getPlant() {
		final Plant plant = new Plant();

		plant.setCreatedDate(new Date());
		plant.setExperiment(new ExperimentModel());
		plant.setPlantBusinessKey(SampleListDaoTest.P + SampleListDaoTest.CROP_PREFIX);
		plant.setPlantNumber(0);
		return plant;
	}

	private SampleList getSampleList() {
		final SampleList sampleList = new SampleList();

		sampleList.setCreatedDate(new Date());
		sampleList.setCreatedBy(this.userDao.getUserByUserName(SampleListDaoTest.ADMIN));
		sampleList.setDescription(SampleListDaoTest.DESCRIPTION);
		sampleList.setListName(
			SampleListDaoTest.TRIAL_NAME + Util.getCurrentDateAsStringValue(SampleListDaoTest.YYYY_M_MDD_HH));
		sampleList.setNotes(SampleListDaoTest.NOTES);
		sampleList.setType(SampleListType.SAMPLE_LIST);

		return sampleList;
	}
}
