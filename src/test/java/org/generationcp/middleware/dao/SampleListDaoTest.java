package org.generationcp.middleware.dao;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.dao.dms.ExperimentDao;
import org.generationcp.middleware.dao.dms.ExperimentPropertyDao;
import org.generationcp.middleware.dao.dms.GeolocationDao;
import org.generationcp.middleware.dao.dms.ProjectPropertyDao;
import org.generationcp.middleware.dao.dms.StockDao;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.data.initializer.PersonTestDataInitializer;
import org.generationcp.middleware.data.initializer.SampleListTestDataInitializer;
import org.generationcp.middleware.data.initializer.SampleTestDataInitializer;
import org.generationcp.middleware.data.initializer.UserTestDataInitializer;
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.sample.SampleDetailsDTO;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.Sample;
import org.generationcp.middleware.pojos.SampleList;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.ExperimentProperty;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.google.common.collect.Ordering;

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
	private PersonDAO personDAO;
	private SampleDao sampleDao;
	private ExperimentDao experimentDao;
	private GeolocationDao geolocationDao;
	private DmsProjectDao dmsProjectDao;
	private StockDao stockDao;
	private ExperimentPropertyDao experimentPropertyDao;
	private GermplasmDAO germplasmDao;
	private ProjectPropertyDao projectPropertyDao;

	private DaoFactory daoFactory;

	public static final String ROOT_FOLDER = "Samples";

	@Before
	public void setUp() throws Exception {
		this.daoFactory = new DaoFactory(this.sessionProvder);

		this.sampleListDao = this.daoFactory.getSampleListDao();
		this.userDao = this.daoFactory.getUserDao();
		this.sampleDao = this.daoFactory.getSampleDao();

		this.personDAO = new PersonDAO();
		this.personDAO.setSession(this.sessionProvder.getSession());

		this.stockDao = new StockDao();
		this.stockDao.setSession(this.sessionProvder.getSession());

		this.experimentDao = new ExperimentDao();
		this.experimentDao.setSession(this.sessionProvder.getSession());

		this.experimentPropertyDao = new ExperimentPropertyDao();
		this.experimentPropertyDao.setSession(this.sessionProvder.getSession());

		this.geolocationDao = new GeolocationDao();
		this.geolocationDao.setSession(this.sessionProvder.getSession());

		this.dmsProjectDao = new DmsProjectDao();
		this.dmsProjectDao.setSession(this.sessionProvder.getSession());
		
		this.germplasmDao = new GermplasmDAO();
		this.germplasmDao.setSession(this.sessionProvder.getSession());

		this.projectPropertyDao = new ProjectPropertyDao();
		this.projectPropertyDao.setSession(this.sessionProvder.getSession());

		// Create three sample lists test data for search
		this.createSampleListForSearch("TEST-LIST-1");
		this.createSampleListForSearch("TEST-LIST-2");
		this.createSampleListForSearch("TEST-LIST-3");
	}

	@Test
	public void testCreateSampleList() {
		final User user = this.userDao.getUserByUserName(SampleListDaoTest.ADMIN);
		final SampleList sampleList = SampleListTestDataInitializer.createSampleList(user);

		final Sample sample = SampleTestDataInitializer.createSample(sampleList, user);

		this.sampleListDao.saveOrUpdate(sampleList);
		Assert.assertNotNull(sampleList.getId());
		// FIXME fresh db doesn't have admin user in crop. Use workbench.users. BMS-886
		// Assert.assertEquals(user.getName(), sampleList.getCreatedBy().getName());
		Assert.assertEquals(sampleList.getDescription(), SampleListDaoTest.DESCRIPTION);
		Assert.assertEquals(SampleListDaoTest.SAMPLE_LIST_NAME, sampleList.getListName());
		Assert.assertEquals(sampleList.getNotes(), SampleListDaoTest.NOTES);
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

	@Test
	public void testGetSampleDetailsDTO() {

		final List<SampleList> sampleLists = this.sampleListDao.searchSampleLists("TEST-LIST-1", true, PROGRAM_UUID, null);

		final List<SampleDetailsDTO> result = this.sampleListDao.getSampleDetailsDTO(sampleLists.get(0).getId());

		Assert.assertFalse(result.isEmpty());
		final SampleDetailsDTO sampleDetailsDTO = result.get(0);

		final User user = this.userDao.getUserByUserName(ADMIN);
		final Person person = user.getPerson();
		Assert.assertEquals("BUSINESS-KEY-TEST-LIST-1", sampleDetailsDTO.getSampleBusinessKey());
		Assert.assertEquals(person.getFirstName() + " " + person.getLastName(), sampleDetailsDTO.getTakenBy());
		Assert.assertEquals("SAMPLE-TEST-LIST-1", sampleDetailsDTO.getSampleName());
		Assert.assertEquals("Germplasm 1", sampleDetailsDTO.getDesignation());
		Assert.assertEquals(sampleDetailsDTO.getDateFormat().format(new Date()), sampleDetailsDTO.getDisplayDate());
		Assert.assertEquals(1, sampleDetailsDTO.getEntryNumber().intValue());
		Assert.assertEquals(1, sampleDetailsDTO.getObservationUnitNumber().intValue());
		Assert.assertNotNull(sampleDetailsDTO.getGid());
		Assert.assertEquals(1, sampleDetailsDTO.getSampleNumber().intValue());
		Assert.assertNotNull(sampleDetailsDTO.getObsUnitId());
		Assert.assertEquals(1, sampleDetailsDTO.getObservationUnitNumber().intValue());
		Assert.assertEquals(1, sampleDetailsDTO.getSampleNumber().intValue());
		Assert.assertEquals("PLATEID", sampleDetailsDTO.getPlateId());
		Assert.assertEquals("WELLID", sampleDetailsDTO.getWell());
	}

	private void createSampleListForSearch(final String listName) {

		final DmsProject plotDmsProject = new DmsProject();
		plotDmsProject.setName("Plot Dataset");
		plotDmsProject.setDescription("Plot Dataset");
		dmsProjectDao.save(plotDmsProject);

		final ProjectProperty datasetTypeProperty =
			new ProjectProperty(plotDmsProject, 1805, String.valueOf(DataSetType.PLOT_DATA.getId()), 1, TermId.DATASET_TYPE.getId(), "");
		this.projectPropertyDao.save(datasetTypeProperty);

		final User user = this.createTestUser();

		final ExperimentModel experimentModel = this.createTestExperiment(plotDmsProject);
		this.createTestStock(experimentModel);

		this.createTestSampleList(listName, user, experimentModel);


	}

	private void createTestSampleList(final String listName, final User user, final ExperimentModel experimentModel) {

		final SampleList sampleList = SampleListTestDataInitializer.createSampleList(user);
		sampleList.setListName(listName);
		sampleList.setDescription("DESCRIPTION-" + listName);

		final Sample sample = SampleTestDataInitializer.createSample(sampleList, user);
		sample.setSampleName("SAMPLE-" + listName);
		sample.setSampleBusinessKey("BUSINESS-KEY-" + listName);
		sample.setEntryNumber(1);
		sample.setExperiment(experimentModel);
		sample.setSampleNumber(1);
		sample.setPlateId("PLATEID");
		sample.setWell("WELLID");

		this.sampleListDao.saveOrUpdate(sampleList);
		this.sampleDao.saveOrUpdate(sample);

	}

	private User createTestUser() {
		User user = this.userDao.getUserByUserName(SampleListDaoTest.ADMIN);
		if (user == null) {
			// FIXME fresh db doesn't have admin user in crop. BMS-886
			final Person person = PersonTestDataInitializer.createPerson();
			person.setFirstName("John");
			person.setLastName("Doe");
			this.personDAO.saveOrUpdate(person);

			user = UserTestDataInitializer.createUser();
			user.setName(ADMIN);
			user.setUserid(null);
			user.setPersonid(person.getId());
			user.setPerson(person);
			this.userDao.saveOrUpdate(user);
		}

		return user;
	}

	private ExperimentModel createTestExperiment(final DmsProject project) {

		final ExperimentModel experimentModel = new ExperimentModel();
		final Geolocation geolocation = new Geolocation();
		geolocationDao.saveOrUpdate(geolocation);

		experimentModel.setGeoLocation(geolocation);
		experimentModel.setTypeId(TermId.PLOT_EXPERIMENT.getId());
		experimentModel.setProject(project);
		experimentModel.setObservationUnitNo(1);
		experimentDao.saveOrUpdate(experimentModel);

		final ExperimentProperty experimentProperty = new ExperimentProperty();
		experimentProperty.setExperiment(experimentModel);
		experimentProperty.setTypeId(TermId.PLOT_NO.getId());
		experimentProperty.setValue("1");
		experimentProperty.setRank(1);
		experimentPropertyDao.saveOrUpdate(experimentProperty);

		return experimentModel;

	}

	private StockModel createTestStock(final ExperimentModel experimentModel) {
		final Germplasm germplasm = GermplasmTestDataInitializer.createGermplasm(1);
		germplasm.setGid(null);
		this.germplasmDao.save(germplasm);
		
		final StockModel stockModel = new StockModel();
		stockModel.setUniqueName("1");
		stockModel.setTypeId(TermId.ENTRY_CODE.getId());
		stockModel.setName("Germplasm 1");
		stockModel.setIsObsolete(false);
		stockModel.setGermplasm(germplasm);

		this.stockDao.saveOrUpdate(stockModel);
		experimentModel.setStock(stockModel);

		return stockModel;

	}




}
