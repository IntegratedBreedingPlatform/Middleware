
package org.generationcp.middleware.dao;

import java.util.List;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.data.initializer.PlantTestDataInitializer;
import org.generationcp.middleware.data.initializer.SampleListTestDataInitializer;
import org.generationcp.middleware.data.initializer.SampleTestDataInitializer;
import org.generationcp.middleware.pojos.Plant;
import org.generationcp.middleware.pojos.Sample;
import org.generationcp.middleware.pojos.SampleList;
import org.generationcp.middleware.pojos.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SampleListDaoTest extends IntegrationTestBase {

	public static final String P = "P";
	public static final String ADMIN = "admin";
	public static final String DESCRIPTION = "description";
	private static final String SAMPLE_LIST_NAME = "Sample list";
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
		final User user = this.userDao.getUserByUserName(SampleListDaoTest.ADMIN);
		final SampleList sampleList = SampleListTestDataInitializer.createSampleList(user);

		final Plant plant = PlantTestDataInitializer.createPlant();
		final Sample sample = SampleTestDataInitializer.createSample(sampleList, plant, user);

		this.sampleListDao.saveOrUpdate(sampleList);
		Assert.assertNotNull(sampleList.getId());
		Assert.assertEquals(user.getName(), sampleList.getCreatedBy().getName());
		Assert.assertEquals(sampleList.getDescription(), SampleListDaoTest.DESCRIPTION);
		Assert.assertEquals(SampleListDaoTest.SAMPLE_LIST_NAME, sampleList.getListName());
		Assert.assertEquals(sampleList.getNotes(), SampleListDaoTest.NOTES);
		Assert.assertEquals(plant.getPlantBusinessKey(), SampleListDaoTest.P + SampleListDaoTest.CROP_PREFIX);
		Assert.assertEquals(plant.getPlantNumber(), new Integer(0));
		Assert.assertEquals(sample.getPlant(), plant);
		Assert.assertEquals(sample.getTakenBy().getName(), user.getName());
		Assert.assertEquals(sample.getSampleName(), SampleListDaoTest.GID);
		Assert.assertEquals(sample.getSampleBusinessKey(), SampleListDaoTest.S + SampleListDaoTest.CROP_PREFIX);
		Assert.assertEquals(sample.getSampleList(), sampleList);

	}

	@Test
	public void testGetSampleListByParentAndNameOk() throws Exception {
		final SampleList sampleList = SampleListTestDataInitializer
				.createSampleList(this.userDao.getUserByUserName(SampleListDaoTest.ADMIN));
		final SampleList parent = this.sampleListDao.getRootSampleList();
		sampleList.setHierarchy(parent);
		this.sampleListDao.save(sampleList);
		final SampleList uSampleList = this.sampleListDao.getSampleListByParentAndName(sampleList.getListName(),
				parent.getId());
		Assert.assertEquals(sampleList.getId(), uSampleList.getId());
		Assert.assertEquals(sampleList.getListName(), uSampleList.getListName());
		Assert.assertEquals(sampleList.getDescription(), uSampleList.getDescription());
		Assert.assertEquals(sampleList.getHierarchy(), uSampleList.getHierarchy());
		Assert.assertEquals(sampleList.getCreatedDate(), uSampleList.getCreatedDate());
		Assert.assertEquals(sampleList.getNotes(), uSampleList.getNotes());
		Assert.assertEquals(sampleList.getType(), uSampleList.getType());
	}

	@Test(expected = NullPointerException.class)
	public void testGetSampleListByParentAndNameNullSampleName() throws Exception {
		this.sampleListDao.getSampleListByParentAndName(null, 1);
	}

	@Test(expected = NullPointerException.class)
	public void testGetSampleListByParentAndNameNullParent() throws Exception {
		this.sampleListDao.getSampleListByParentAndName("name", null);
	}

	@Test
	public void testGetAllTopLevelLists() {
		final User user = this.userDao.getUserByUserName(SampleListDaoTest.ADMIN);
		final SampleList sampleList = SampleListTestDataInitializer.createSampleList(user);
		final SampleList parent = this.sampleListDao.getRootSampleList();
		sampleList.setHierarchy(parent);
		this.sampleListDao.save(sampleList);

		final List<SampleList> topLevelLists = this.sampleListDao.getAllTopLevelLists(sampleList.getProgramUUID());

		final SampleList sampleListFolder = SampleListTestDataInitializer.createSampleListFolder(user);
		sampleListFolder.setHierarchy(parent);
		this.sampleListDao.save(sampleListFolder);
		final List<SampleList> updatedTopLevelLists = this.sampleListDao
				.getAllTopLevelLists(sampleList.getProgramUUID());

		Assert.assertEquals(topLevelLists.size(), updatedTopLevelLists.size() - 1);
	}

	@Test
	public void testGetRootSampleList() {
		final SampleList rootList = this.sampleListDao.getRootSampleList();
		Assert.assertEquals(new Integer(1), rootList.getId());
	}
}
