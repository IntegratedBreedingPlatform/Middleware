package org.generationcp.middleware.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.Ordering;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.dms.ExperimentDao;
import org.generationcp.middleware.dao.dms.GeolocationDao;
import org.generationcp.middleware.data.initializer.PlantTestDataInitializer;
import org.generationcp.middleware.data.initializer.SampleListTestDataInitializer;
import org.generationcp.middleware.data.initializer.SampleTestDataInitializer;
import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.pojos.Plant;
import org.generationcp.middleware.pojos.Sample;
import org.generationcp.middleware.pojos.SampleList;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class SampleListDaoTest extends IntegrationTestBase {

	public static final String P = "P";
	public static final String ADMIN = "admin";
	public static final String DESCRIPTION = "description";
	private static final String SAMPLE_LIST_NAME = "Sample list";
	public static final String NOTES = "Notes";
	private static final String CROP_PREFIX = "ABCD";
	public static final String GID = "GID";
	public static final String S = "S";
	public static final String PROGRAM_UUID = "c35c7769-bdad-4c70-a6c4-78c0dbf784e5";

	private SampleListDao sampleListDao;
	private UserDAO userDao;
	private PlantDao plantDao;
	private SampleDao sampleDao;
	private ExperimentDao experimentDao;
	private GeolocationDao geolocationDao;

	public static final String ROOT_FOLDER = "Samples";

	@Before
	public void setUp() throws Exception {
		this.sampleListDao = new SampleListDao();
		this.sampleListDao.setSession(this.sessionProvder.getSession());

		this.userDao = new UserDAO();
		this.userDao.setSession(this.sessionProvder.getSession());

		this.plantDao = new PlantDao();
		this.plantDao.setSession(this.sessionProvder.getSession());

		this.sampleDao = new SampleDao();
		this.sampleDao.setSession(this.sessionProvder.getSession());

		this.experimentDao = new ExperimentDao();
		this.experimentDao.setSession(this.sessionProvder.getSession());

		this.geolocationDao = new GeolocationDao();
		this.geolocationDao.setSession(this.sessionProvder.getSession());

		// Create three sample lists test data for search
		this.createSampleListForSearch("TEST-LIST-1");
		this.createSampleListForSearch("TEST-LIST-2");
		this.createSampleListForSearch("TEST-LIST-3");
	}

	@Test
	public void testCreateSampleList() {
		final User user = this.userDao.getUserByUserName(SampleListDaoTest.ADMIN);
		final SampleList sampleList = SampleListTestDataInitializer.createSampleList(user);

		final Plant plant = PlantTestDataInitializer.createPlant();
		final Sample sample = SampleTestDataInitializer.createSample(sampleList, plant, user);

		this.sampleListDao.saveOrUpdate(sampleList);
		Assert.assertNotNull(sampleList.getId());
		// FIXME fresh db doesn't have admin user in crop. Use workbench.users. BMS-886
		// Assert.assertEquals(user.getName(), sampleList.getCreatedBy().getName());
		Assert.assertEquals(sampleList.getDescription(), SampleListDaoTest.DESCRIPTION);
		Assert.assertEquals(SampleListDaoTest.SAMPLE_LIST_NAME, sampleList.getListName());
		Assert.assertEquals(sampleList.getNotes(), SampleListDaoTest.NOTES);
		Assert.assertEquals(plant.getPlantBusinessKey(), SampleListDaoTest.P + SampleListDaoTest.CROP_PREFIX);
		Assert.assertEquals(plant.getPlantNumber(), new Integer(0));
		Assert.assertEquals(sample.getPlant(), plant);
		// Assert.assertEquals(sample.getTakenBy().getName(), user.getName());
		Assert.assertEquals(sample.getSampleName(), SampleListDaoTest.GID);
		Assert.assertEquals(sample.getSampleBusinessKey(), SampleListDaoTest.S + SampleListDaoTest.CROP_PREFIX);
		Assert.assertEquals(sample.getSampleList(), sampleList);

	}

	@Test
	public void testGetSampleListByParentAndNameOk() throws Exception {
		final SampleList sampleList =
				SampleListTestDataInitializer.createSampleList(this.userDao.getUserByUserName(SampleListDaoTest.ADMIN));
		final SampleList parent = this.sampleListDao.getRootSampleList();
		sampleList.setHierarchy(parent);
		this.sampleListDao.save(sampleList);
		final SampleList uSampleList =
				this.sampleListDao.getSampleListByParentAndName(sampleList.getListName(), parent.getId(), PROGRAM_UUID);
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
		this.sampleListDao.getSampleListByParentAndName(null, 1, PROGRAM_UUID);
	}

	@Test(expected = NullPointerException.class)
	public void testGetSampleListByParentAndNameNullParent() throws Exception {
		this.sampleListDao.getSampleListByParentAndName("name", null, PROGRAM_UUID);
	}

	@Test
	public void testGetAllTopLevelLists() {
		final User user = this.userDao.getUserByUserName(SampleListDaoTest.ADMIN);
		final SampleList sampleList = SampleListTestDataInitializer.createSampleList(user);
		final SampleList parent = this.sampleListDao.getRootSampleList();
		sampleList.setHierarchy(parent);
		this.sampleListDao.save(sampleList);

		final List<SampleList> topLevelLists = this.sampleListDao.getAllTopLevelLists(sampleList.getProgramUUID());

		Assert.assertTrue(topLevelLists.contains(sampleList));
		for (SampleList list : topLevelLists) {
			Assert.assertNotNull(list.getProgramUUID());
		}
	}

	@Test
	public void testGetAllTopLevelListsProgramUUIDIsNull() {
		final User user = this.userDao.getUserByUserName(SampleListDaoTest.ADMIN);
		final SampleList sampleList = SampleListTestDataInitializer.createSampleList(user);
		sampleList.setProgramUUID(null);
		final SampleList parent = this.sampleListDao.getRootSampleList();
		sampleList.setHierarchy(parent);
		this.sampleListDao.save(sampleList);

		final List<SampleList> topLevelLists = this.sampleListDao.getAllTopLevelLists(null);

		Assert.assertTrue(topLevelLists.contains(sampleList));
		for (SampleList list : topLevelLists) {
			Assert.assertNull(list.getProgramUUID());
		}

	}

	@Test
	public void testGetRootSampleList() {
		final SampleList rootList = this.sampleListDao.getRootSampleList();
		Assert.assertEquals(new Integer(1), rootList.getId());
	}

	@Test
	public void testSearchSampleListsStartsWith() {

		final List<SampleList> matchByListName = this.sampleListDao.searchSampleLists("TEST-LIST-", false, PROGRAM_UUID, null);
		final List<SampleList> matchBySampleName = this.sampleListDao.searchSampleLists("SAMPLE-TEST-LIST-", false, PROGRAM_UUID, null);
		final List<SampleList> matchByUID = this.sampleListDao.searchSampleLists("BUSINESS-KEY-TEST-LIST-", false, PROGRAM_UUID, null);
		final List<SampleList> noMatch = this.sampleListDao.searchSampleLists("TEST-LIST-12", false, PROGRAM_UUID, null);

		Assert.assertEquals(3, matchByListName.size());
		Assert.assertEquals(3, matchBySampleName.size());
		Assert.assertEquals(3, matchByUID.size());
		Assert.assertTrue(noMatch.isEmpty());
	}

	@Test
	public void testSearchSampleListsExactMatch() {

		final List<SampleList> matchByListName = this.sampleListDao.searchSampleLists("TEST-LIST-1", true, PROGRAM_UUID, null);
		final List<SampleList> matchBySampleName = this.sampleListDao.searchSampleLists("SAMPLE-TEST-LIST-2", true, PROGRAM_UUID, null);
		final List<SampleList> matchByUID = this.sampleListDao.searchSampleLists("BUSINESS-KEY-TEST-LIST-3", true, PROGRAM_UUID, null);
		final List<SampleList> noMatch = this.sampleListDao.searchSampleLists("TEST-LIST-", true, PROGRAM_UUID, null);

		Assert.assertEquals(1, matchByListName.size());
		Assert.assertEquals(1, matchBySampleName.size());
		Assert.assertEquals(1, matchByUID.size());
		Assert.assertTrue(noMatch.isEmpty());
	}

	@Test
	public void testSearchSampleListsSortAscending() {

		final Pageable pageable = Mockito.mock(Pageable.class);
		Sort.Order order = new Sort.Order(Sort.Direction.ASC, "listName");
		Mockito.when(pageable.getSort()).thenReturn(new Sort(order));

		final List<SampleList> matchByListName = this.sampleListDao.searchSampleLists("TEST-LIST-", false, PROGRAM_UUID, pageable);

		final List<String> result = new LinkedList<>();
		for (SampleList sampleList : matchByListName) {
			result.add(sampleList.getListName());
		}

		// Check if SampleLists' listName is in ascending order
		Assert.assertTrue(Ordering.natural().isOrdered(result));
	}

	@Test
	public void testSearchSampleListsSortDescending() {

		final Pageable pageable = Mockito.mock(Pageable.class);
		Sort.Order order = new Sort.Order(Sort.Direction.DESC, "listName");
		Mockito.when(pageable.getSort()).thenReturn(new Sort(order));

		final List<SampleList> matchByListName = this.sampleListDao.searchSampleLists("TEST-LIST-", false, PROGRAM_UUID, pageable);

		final List<String> result = new LinkedList<>();
		for (SampleList sampleList : matchByListName) {
			result.add(sampleList.getListName());
		}

		// Check if SampleLists' listName is in descending order
		Assert.assertTrue(Ordering.natural().reverse().isOrdered(result));
	}

	private void createSampleListForSearch(final String listName) {

		final User user = this.userDao.getUserByUserName(SampleListDaoTest.ADMIN);
		final SampleList sampleList = SampleListTestDataInitializer.createSampleList(user);
		sampleList.setListName(listName);
		sampleList.setDescription("DESCRIPTION-" + listName);

		final ExperimentModel experimentModel = new ExperimentModel();
		final Geolocation geolocation = new Geolocation();
		geolocationDao.saveOrUpdate(geolocation);
		experimentModel.setGeoLocation(geolocation);
		experimentModel.setTypeId(TermId.PLOT_EXPERIMENT.getId());
		experimentDao.saveOrUpdate(experimentModel);

		final Plant plant = PlantTestDataInitializer.createPlant();
		plant.getExperiment().setNdExperimentId(experimentModel.getNdExperimentId());

		final Sample sample = SampleTestDataInitializer.createSample(sampleList, plant, user);
		sample.setSampleName("SAMPLE-" + listName);
		sample.setSampleBusinessKey("BUSINESS-KEY-" + listName);
		sample.setEntryNumber(1);

		this.sampleListDao.saveOrUpdate(sampleList);
		this.sampleDao.saveOrUpdate(sample);

		System.out.print(1);
	}

}
