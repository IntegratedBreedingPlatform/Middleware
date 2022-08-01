package org.generationcp.middleware.dao;

import com.google.common.collect.Ordering;
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
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.sample.SampleDetailsDTO;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.api.role.RoleServiceImpl;
import org.generationcp.middleware.api.role.RoleService;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.Sample;
import org.generationcp.middleware.pojos.SampleList;
import org.generationcp.middleware.pojos.dms.DatasetType;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.ExperimentProperty;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.user.UserService;
import org.generationcp.middleware.service.impl.user.UserServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

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
	private PersonDAO personDAO;
	private SampleDao sampleDao;
	private ExperimentDao experimentDao;
	private GeolocationDao geolocationDao;
	private DmsProjectDao dmsProjectDao;
	private StockDao stockDao;
	private ExperimentPropertyDao experimentPropertyDao;
	private GermplasmDAO germplasmDao;
	private ProjectPropertyDao projectPropertyDao;
	private RoleService roleService;
	private UserService userService;

	private DaoFactory daoFactory;

	public static final String ROOT_FOLDER = "Samples";

	@Before
	public void setUp() throws Exception {
		this.daoFactory = new DaoFactory(this.sessionProvder);

		this.sampleListDao = this.daoFactory.getSampleListDao();
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

		this.germplasmDao = new GermplasmDAO(this.sessionProvder.getSession());

		this.projectPropertyDao = new ProjectPropertyDao();
		this.projectPropertyDao.setSession(this.sessionProvder.getSession());

		this.roleService = new RoleServiceImpl(this.workbenchSessionProvider);
		this.userService = new UserServiceImpl(this.workbenchSessionProvider);
		// Create three sample lists test data for search
		this.createSampleListForSearch("TEST-LIST-1");
		this.createSampleListForSearch("TEST-LIST-2");
		this.createSampleListForSearch("TEST-LIST-3");
	}

	@Test
	public void testCreateSampleList() {
		final WorkbenchUser workbenchUser = this.userService.getUserByUsername(SampleListDaoTest.ADMIN);
		final SampleList sampleList = SampleListTestDataInitializer.createSampleList(workbenchUser.getUserid());

		final Sample sample = SampleTestDataInitializer.createSample(sampleList, workbenchUser.getUserid());

		this.sampleListDao.saveOrUpdate(sampleList);
		Assert.assertNotNull(sampleList.getId());
		// FIXME fresh db doesn't have admin workbenchUser in crop. Use workbench.users. BMS-886
		// Assert.assertEquals(workbenchUser.getName(), sampleList.getCreatedByUserId().getName());
		Assert.assertEquals(sampleList.getDescription(), SampleListDaoTest.DESCRIPTION);
		Assert.assertEquals(SampleListDaoTest.SAMPLE_LIST_NAME, sampleList.getListName());
		Assert.assertEquals(sampleList.getNotes(), SampleListDaoTest.NOTES);
		// Assert.assertEquals(sample.getTakenBy().getName(), workbenchUser.getName());
		Assert.assertEquals(sample.getSampleName(), SampleListDaoTest.GID);
		Assert.assertEquals(sample.getSampleBusinessKey(), SampleListDaoTest.S + SampleListDaoTest.CROP_PREFIX);
		Assert.assertEquals(sample.getSampleList(), sampleList);

	}

	@Test
	public void testGetSampleListByParentAndNameOk() throws Exception {
		final WorkbenchUser workbenchUser = this.userService.getUserByUsername(SampleListDaoTest.ADMIN);
		final SampleList sampleList =
			SampleListTestDataInitializer.createSampleList(workbenchUser.getUserid());
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
		final WorkbenchUser workbenchUser = this.userService.getUserByUsername(SampleListDaoTest.ADMIN);
		final SampleList sampleList = SampleListTestDataInitializer.createSampleList(workbenchUser.getUserid());
		final SampleList parent = this.sampleListDao.getRootSampleList();
		sampleList.setHierarchy(parent);
		this.sampleListDao.save(sampleList);

		final List<SampleList> topLevelLists = this.sampleListDao.getAllTopLevelLists(sampleList.getProgramUUID());

		Assert.assertTrue(topLevelLists.contains(sampleList));
		for (final SampleList list : topLevelLists) {
			Assert.assertNotNull(list.getProgramUUID());
		}
	}

	@Test
	public void testGetAllTopLevelListsProgramUUIDIsNull() {
		final WorkbenchUser workbenchUser = this.userService.getUserByUsername(SampleListDaoTest.ADMIN);
		final SampleList sampleList = SampleListTestDataInitializer.createSampleList(workbenchUser.getUserid());
		sampleList.setProgramUUID(null);
		final SampleList parent = this.sampleListDao.getRootSampleList();
		sampleList.setHierarchy(parent);
		this.sampleListDao.save(sampleList);

		final List<SampleList> topLevelLists = this.sampleListDao.getAllTopLevelLists(null);

		Assert.assertTrue(topLevelLists.contains(sampleList));
		for (final SampleList list : topLevelLists) {
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
		final Sort.Order order = new Sort.Order(Sort.Direction.ASC, "listName");
		Mockito.when(pageable.getSort()).thenReturn(new Sort(order));

		final List<SampleList> matchByListName = this.sampleListDao.searchSampleLists("TEST-LIST-", false, PROGRAM_UUID, pageable);

		final List<String> result = new LinkedList<>();
		for (final SampleList sampleList : matchByListName) {
			result.add(sampleList.getListName());
		}

		// Check if SampleLists' listName is in ascending order
		Assert.assertTrue(Ordering.natural().isOrdered(result));
	}

	@Test
	public void testSearchSampleListsSortDescending() {

		final Pageable pageable = Mockito.mock(Pageable.class);
		final Sort.Order order = new Sort.Order(Sort.Direction.DESC, "listName");
		Mockito.when(pageable.getSort()).thenReturn(new Sort(order));

		final List<SampleList> matchByListName = this.sampleListDao.searchSampleLists("TEST-LIST-", false, PROGRAM_UUID, pageable);

		final List<String> result = new LinkedList<>();
		for (final SampleList sampleList : matchByListName) {
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

		final WorkbenchUser workbenchUser = this.userService.getUserByUsername(SampleListDaoTest.ADMIN);
		Assert.assertEquals("BUSINESS-KEY-TEST-LIST-1", sampleDetailsDTO.getSampleBusinessKey());
		Assert.assertEquals(workbenchUser.getUserid(), sampleDetailsDTO.getTakenByUserId());
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

		final DmsProject study = new DmsProject();
		study.setName("TEST STUDY " + new Random().nextInt());
		study.setDescription("Test Study");
		this.dmsProjectDao.save(study);

		final DmsProject plotDmsProject = new DmsProject();
		plotDmsProject.setName("Plot Dataset");
		plotDmsProject.setDescription("Plot Dataset");
		plotDmsProject.setDatasetType(new DatasetType(DatasetTypeEnum.PLOT_DATA.getId()));
		plotDmsProject.setStudy(study);
		plotDmsProject.setParent(study);
		this.dmsProjectDao.save(plotDmsProject);

		final WorkbenchUser user = this.createTestUser();

		final ExperimentModel experimentModel = this.createTestExperiment(plotDmsProject);
		this.createTestStock(study, experimentModel);

		this.createTestSampleList(listName, user, experimentModel);

	}

	private void createTestSampleList(final String listName, final WorkbenchUser workbenchUser, final ExperimentModel experimentModel) {

		final SampleList sampleList = SampleListTestDataInitializer.createSampleList(workbenchUser.getUserid());
		sampleList.setListName(listName);
		sampleList.setDescription("DESCRIPTION-" + listName);

		final Sample sample = SampleTestDataInitializer.createSample(sampleList, workbenchUser.getUserid());
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

	private WorkbenchUser createTestUser() {
		WorkbenchUser workbenchUser = this.userService.getUserByUsername(SampleListDaoTest.ADMIN);
		if (workbenchUser == null) {
			// FIXME fresh db doesn't have admin user in crop. BMS-886
			final Person person = PersonTestDataInitializer.createPerson();
			person.setFirstName("John");
			person.setLastName("Doe");
			this.personDAO.saveOrUpdate(person);

			workbenchUser = UserTestDataInitializer.createWorkbenchUser();
			workbenchUser.setName(ADMIN);
			workbenchUser.setUserid(null);
			workbenchUser.setPerson(person);
			this.userService.addUser(workbenchUser);
		}

		return workbenchUser;
	}

	private ExperimentModel createTestExperiment(final DmsProject project) {

		final ExperimentModel experimentModel = new ExperimentModel();
		final Geolocation geolocation = new Geolocation();
		this.geolocationDao.saveOrUpdate(geolocation);

		experimentModel.setGeoLocation(geolocation);
		experimentModel.setTypeId(TermId.PLOT_EXPERIMENT.getId());
		experimentModel.setProject(project);
		experimentModel.setObservationUnitNo(1);
		this.experimentDao.saveOrUpdate(experimentModel);

		final ExperimentProperty experimentProperty = new ExperimentProperty();
		experimentProperty.setExperiment(experimentModel);
		experimentProperty.setTypeId(TermId.PLOT_NO.getId());
		experimentProperty.setValue("1");
		experimentProperty.setRank(1);
		this.experimentPropertyDao.saveOrUpdate(experimentProperty);

		return experimentModel;

	}

	private StockModel createTestStock(final DmsProject study, final ExperimentModel experimentModel) {
		final Germplasm germplasm = GermplasmTestDataInitializer.createGermplasm(1);
		germplasm.setGid(null);
		this.germplasmDao.save(germplasm);

		final StockModel stockModel = new StockModel();
		stockModel.setUniqueName("1");
		stockModel.setName("Germplasm 1");
		stockModel.setIsObsolete(false);
		stockModel.setGermplasm(germplasm);
		stockModel.setCross("-");
		stockModel.setProject(study);

		this.stockDao.saveOrUpdate(stockModel);
		experimentModel.setStock(stockModel);

		return stockModel;

	}

}
