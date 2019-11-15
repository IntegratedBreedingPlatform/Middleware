
package org.generationcp.middleware.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.data.initializer.GermplasmListTestDataInitializer;
import org.generationcp.middleware.data.initializer.StudyTestDataInitializer;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.gms.GermplasmListType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.StudyDataManagerImpl;
import org.generationcp.middleware.manager.api.*;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.ListMetadata;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.util.Util;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

public class GermplasmListDAOTest extends IntegrationTestBase {

	@Autowired
	private GermplasmListManager manager;

	@Autowired
	private GermplasmDataManager dataManager;

	@Autowired
	private OntologyDataManager ontologyManager;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private LocationDataManager locationManager;

	@Autowired
	private WorkbenchTestDataUtil workbenchTestDataUtil;

	private GermplasmListDAO dao;
	private static final String TEST_GERMPLASM_LIST_NAME = "TestGermplasmListName";
	private static final String TEST_GERMPLASM_LIST_DESC = "TestGermplasmListDesc";
	private static final long TEST_GERMPLASM_LIST_DATE = 20141103;
	private static final String TEST_GERMPLASM_LIST_TYPE_LST = "LST";
	private static final String TEST_GERMPLASM_LIST_TYPE_FOLDER = "FOLDER";
	private static final String TEST_LIST_DESCRIPTION = "Test List Description";

	private static final int TEST_GERMPLASM_LIST_USER_ID = 9999;
	private static final Integer STATUS_ACTIVE = 0;
	private static final Integer STATUS_DELETED = 9;
	private static final String PROGRAM_UUID = "1001";
	public static List<String> EXCLUDED_GERMPLASM_LIST_TYPES = new ArrayList<String>();

	private GermplasmList list;
	private Germplasm germplasm;
	private Project commonTestProject;

	private StudyReference studyReference;
	private StudyTestDataInitializer studyTDI;

	static {
		GermplasmListDAOTest.EXCLUDED_GERMPLASM_LIST_TYPES.add("STUDY");
		GermplasmListDAOTest.EXCLUDED_GERMPLASM_LIST_TYPES.add("CHECK");
		GermplasmListDAOTest.EXCLUDED_GERMPLASM_LIST_TYPES.add("ADVANCED");
		GermplasmListDAOTest.EXCLUDED_GERMPLASM_LIST_TYPES.add("CROSSES");
		GermplasmListDAOTest.EXCLUDED_GERMPLASM_LIST_TYPES.add("FOLDER");
	}


	@Before
	public void setUp() throws Exception {
		this.dao = new GermplasmListDAO();
		this.dao.setSession(this.sessionProvder.getSession());
		this.list = saveGermplasm(GermplasmListTestDataInitializer.createGermplasmListTestData(
				TEST_GERMPLASM_LIST_NAME, GermplasmListDAOTest.TEST_GERMPLASM_LIST_DESC,
				TEST_GERMPLASM_LIST_DATE, GermplasmListDAOTest.TEST_GERMPLASM_LIST_TYPE_LST,
				GermplasmListDAOTest.TEST_GERMPLASM_LIST_USER_ID, GermplasmListDAOTest.STATUS_ACTIVE, GermplasmListDAOTest.PROGRAM_UUID, null));
		final Name name = new Name(null, null, 1, 1, 1, "Name", 0, 0, 0);
		this.germplasm = new Germplasm(null, 0, 0, 0, 0, 1, 0, 0, Util.getCurrentDateAsIntegerValue(), name);
		this.dataManager.addGermplasm(this.germplasm, name);
		final GermplasmListData germplasmListData = new GermplasmListData(null, this.list, this.germplasm.getGid(), 1, "EntryCode",
				"SeedSource", "Germplasm Name 5", "GroupName", 0, 99995);
		this.manager.addGermplasmListData(germplasmListData);

		this.workbenchTestDataUtil.setUpWorkbench();

		if (this.commonTestProject == null) {
			this.commonTestProject = this.workbenchTestDataUtil.getCommonTestProject();
		}

		final StudyDataManagerImpl studyDataManager = new StudyDataManagerImpl(this.sessionProvder);
		this.studyTDI = new StudyTestDataInitializer(studyDataManager, this.ontologyManager, this.commonTestProject, this.dataManager,
				this.locationManager);

		this.studyReference = this.studyTDI.addTestStudy("ABCD");

	}

	@Test
	public void testHideSnapshotListTypes() {
		final GermplasmListDAO dao = new GermplasmListDAO();
		final Criteria criteria = Mockito.mock(Criteria.class);
		dao.hideSnapshotListTypes(criteria);
		final Criterion restrictedList = dao.getRestrictedSnapshopTypes();
		// this should ensure that the snapshot list types are added int he criteria object
		Mockito.verify(criteria, Mockito.times(1)).add(restrictedList);
		
	}

	@Test
	public void testGetRestrictedSnapshopTypes() {
		final GermplasmListDAO dao = new GermplasmListDAO();
		final Criterion restrictedList = dao.getRestrictedSnapshopTypes();
		Assert.assertNotNull(restrictedList);
	}

	@Test
	public void testCountByName() throws Exception {
		Assert.assertEquals("There should be one germplasm list with name " + GermplasmListDAOTest.TEST_GERMPLASM_LIST_NAME, 1,
				this.dao.countByName(GermplasmListDAOTest.TEST_GERMPLASM_LIST_NAME, Operation.EQUAL));

		this.list.setStatus(GermplasmListDAOTest.STATUS_DELETED);
		saveGermplasm(this.list);
		Assert.assertEquals("There should be no germplasm list with name " + GermplasmListDAOTest.TEST_GERMPLASM_LIST_NAME, 0,
				this.dao.countByName(GermplasmListDAOTest.TEST_GERMPLASM_LIST_NAME, Operation.EQUAL));
		// revert status
		this.list.setStatus(GermplasmListDAOTest.STATUS_ACTIVE);
		saveGermplasm(this.list);

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetGermplasmListTypes() {
		final List<String> germplasmListTypes = this.dao.getGermplasmListTypes();
		for (final String listType : GermplasmListDAOTest.EXCLUDED_GERMPLASM_LIST_TYPES) {
			Assert.assertFalse(listType + " should not be in the Results Array", germplasmListTypes.contains(listType));
		}
	}

	private GermplasmList saveGermplasm(final GermplasmList list) throws MiddlewareQueryException {
		final GermplasmList newList = this.dao.saveOrUpdate(list);
		return newList;
	}

	@Test
	public void testGetAllTopLevelLists() {

		final List<GermplasmList> germplasmLists = this.dao.getAllTopLevelLists(GermplasmListDAOTest.PROGRAM_UUID);

		Assert.assertFalse(germplasmLists.isEmpty());
		Assert.assertEquals(GermplasmListDAOTest.TEST_GERMPLASM_LIST_NAME, germplasmLists.get(0).getName());
		Assert.assertEquals(GermplasmListDAOTest.PROGRAM_UUID, germplasmLists.get(0).getProgramUUID());

	}

	@Test
	public void testGetAllTopLevelListsCropList() {

		// Create a test germplasm list accessible to all programs (a list with null programUUID).
		final String testGermplasmName = "Germplasm List acessible from all programs";

		final GermplasmList germplasmList = saveGermplasm(GermplasmListTestDataInitializer.createGermplasmListTestData(testGermplasmName, "" ,TEST_GERMPLASM_LIST_DATE ,"" ,
				GermplasmListDAOTest.TEST_GERMPLASM_LIST_USER_ID, GermplasmListDAOTest.STATUS_ACTIVE, null, null));

		final List<GermplasmList> germplasmLists = this.dao.getAllTopLevelLists(null);

		Assert.assertFalse(germplasmLists.isEmpty());
		Assert.assertTrue(germplasmLists.contains(germplasmList));

	}

	@Test
	public void testGetAllListMetadata() {
		final List<GermplasmList> germplasmLists = this.dao.getListsByProgramUUID(GermplasmListDAOTest.PROGRAM_UUID);
		
		final List<Integer> germplasmListIds = new ArrayList<>();
		for (final GermplasmList germplasmList : germplasmLists) {
			germplasmListIds.add(germplasmList.getId());
		}
		
		final List<Object[]> listMetadata = this.dao.getAllListMetadata(germplasmListIds);
		Assert.assertEquals("Meta data size must be the same as the list size", listMetadata.size(), germplasmLists.size());
	}

	@Test
	public void testGetListsByProgramUUID() {
		final List<GermplasmList> germplasmLists = this.dao.getListsByProgramUUID(GermplasmListDAOTest.PROGRAM_UUID);
		final GermplasmList resultList = germplasmLists.get(0);
		Assert.assertEquals("The list name should be " + GermplasmListDAOTest.TEST_GERMPLASM_LIST_NAME,
				GermplasmListDAOTest.TEST_GERMPLASM_LIST_NAME, resultList.getName());
		Assert.assertEquals("The list description should be " + GermplasmListDAOTest.TEST_GERMPLASM_LIST_DESC,
				GermplasmListDAOTest.TEST_GERMPLASM_LIST_DESC, resultList.getDescription());
	}

	@Test
	public void testGetByGIDandProgramUUID() {
		final List<GermplasmList> germplasmLists =
				this.dao.getByGIDandProgramUUID(this.germplasm.getGid(), 0, 1, GermplasmListDAOTest.PROGRAM_UUID);
		final GermplasmList resultList = germplasmLists.get(0);
		Assert.assertEquals("The list name should be " + GermplasmListDAOTest.TEST_GERMPLASM_LIST_NAME,
				GermplasmListDAOTest.TEST_GERMPLASM_LIST_NAME, resultList.getName());
		Assert.assertEquals("The list description should be " + GermplasmListDAOTest.TEST_GERMPLASM_LIST_DESC,
				GermplasmListDAOTest.TEST_GERMPLASM_LIST_DESC, resultList.getDescription());
	}

	@Test
	public void testCountByGIDandProgramUUID() {
		final int result =
				(int) this.dao.countByGIDandProgramUUID(this.germplasm.getGid(), GermplasmListDAOTest.PROGRAM_UUID);
		Assert.assertEquals("The count should be 1", 1, result);
	}

	@Test
	public void testGetGermplasmFolderMetadata() throws Exception {
		// Create germplasm test folder
		final GermplasmList testFolder = GermplasmListTestDataInitializer.createGermplasmListTestData("TestFolder", "Test Folder Description",
				GermplasmListDAOTest.TEST_GERMPLASM_LIST_DATE, GermplasmListDAOTest.TEST_GERMPLASM_LIST_TYPE_FOLDER,
				GermplasmListDAOTest.TEST_GERMPLASM_LIST_USER_ID, GermplasmListDAOTest.STATUS_ACTIVE, GermplasmListDAOTest.PROGRAM_UUID, null);
		saveGermplasm(testFolder);
		final Map<Integer, ListMetadata> result =
				this.dao.getGermplasmFolderMetadata(Collections.singletonList(testFolder.getId()));
		final ListMetadata germplasmFolderMetadata = result.get(testFolder.getId());
		Assert.assertNotNull("Newly created folder should not be null", germplasmFolderMetadata);
		Assert.assertEquals("Newly created folder should have zero children", 
				new Integer(0), germplasmFolderMetadata.getNumberOfChildren());
	}

	@Test
	public void testGetAllGermplasmListsById() throws Exception {
		final GermplasmList testList = GermplasmListTestDataInitializer.createGermplasmListTestData("TestList", GermplasmListDAOTest.TEST_LIST_DESCRIPTION,
				GermplasmListDAOTest.TEST_GERMPLASM_LIST_DATE, GermplasmListDAOTest.TEST_GERMPLASM_LIST_TYPE_LST,
				GermplasmListDAOTest.TEST_GERMPLASM_LIST_USER_ID, GermplasmListDAOTest.STATUS_ACTIVE, GermplasmListDAOTest.PROGRAM_UUID, null);
		saveGermplasm(testList);
		final List<GermplasmList> allGermplasmListsById = this.dao.getAllGermplasmListsById(Collections.singletonList(testList.getId()));
		Assert.assertTrue("Returned results should not be empty", !allGermplasmListsById.isEmpty());
		Assert.assertEquals("Returned results should contain one item", 
				1, allGermplasmListsById.size());

	}

	@Test
	public void testHasAdvancedOrCrossesListForAdvanced() {
		Assert.assertFalse(this.dao.hasAdvancedOrCrossesList(this.studyReference.getId()));
		final GermplasmList testList = GermplasmListTestDataInitializer.createGermplasmListTestData("ADV LIST", GermplasmListDAOTest.TEST_LIST_DESCRIPTION,
				GermplasmListDAOTest.TEST_GERMPLASM_LIST_DATE, GermplasmListType.ADVANCED.name(),
				GermplasmListDAOTest.TEST_GERMPLASM_LIST_USER_ID, GermplasmListDAOTest.STATUS_ACTIVE, GermplasmListDAOTest.PROGRAM_UUID, this.studyReference.getId());
		testList.setProjectId(this.studyReference.getId());
		saveGermplasm(testList);
		Assert.assertTrue(this.dao.hasAdvancedOrCrossesList(this.studyReference.getId()));
	}

	@Test
	public void testHasAdvancedOrCrossesListForCreatedCrosses() {
		Assert.assertFalse(this.dao.hasAdvancedOrCrossesList(this.studyReference.getId()));
		final GermplasmList testList = GermplasmListTestDataInitializer.createGermplasmListTestData("CREATED CROSSES", GermplasmListDAOTest.TEST_LIST_DESCRIPTION,
				GermplasmListDAOTest.TEST_GERMPLASM_LIST_DATE, GermplasmListType.CRT_CROSS.name(),
				GermplasmListDAOTest.TEST_GERMPLASM_LIST_USER_ID, GermplasmListDAOTest.STATUS_ACTIVE, GermplasmListDAOTest.PROGRAM_UUID, this.studyReference.getId());
		saveGermplasm(testList);
		Assert.assertTrue(this.dao.hasAdvancedOrCrossesList(this.studyReference.getId()));
	}

	@Test
	public void testHasAdvancedOrCrossesListForImportedCrosses() {
		Assert.assertFalse(this.dao.hasAdvancedOrCrossesList(this.studyReference.getId()));
		final GermplasmList testList = GermplasmListTestDataInitializer.createGermplasmListTestData("IMPORTED CROSSES", GermplasmListDAOTest.TEST_LIST_DESCRIPTION,
				GermplasmListDAOTest.TEST_GERMPLASM_LIST_DATE, GermplasmListType.IMP_CROSS.name(),
				GermplasmListDAOTest.TEST_GERMPLASM_LIST_USER_ID, GermplasmListDAOTest.STATUS_ACTIVE, GermplasmListDAOTest.PROGRAM_UUID, this.studyReference.getId());
		saveGermplasm(testList);
		Assert.assertTrue(this.dao.hasAdvancedOrCrossesList(this.studyReference.getId()));
	}

	@Test
	public void testGetGermplasmUsedInMoreThanOneListFalse() {
		Assert.assertFalse(this.dao.getGermplasmUsedInMoreThanOneList(Arrays.asList(this.germplasm.getGid())).size() > 0);
	}

	@Test
	public void testGetGermplasmUsedInMoreThanOneListSuccess() {
		final Name name = new Name(null, null, 1, 1, 1, "Name", 0, 0, 0);
		final Germplasm germplasm = new Germplasm(null, 0, 0, 0, 0, 1, 0, 0, Util.getCurrentDateAsIntegerValue(), name);

		final GermplasmList list1 = saveGermplasm(GermplasmListTestDataInitializer.createGermplasmListTestData(
			TEST_GERMPLASM_LIST_NAME, GermplasmListDAOTest.TEST_GERMPLASM_LIST_DESC,
			TEST_GERMPLASM_LIST_DATE, GermplasmListDAOTest.TEST_GERMPLASM_LIST_TYPE_LST,
			GermplasmListDAOTest.TEST_GERMPLASM_LIST_USER_ID, GermplasmListDAOTest.STATUS_ACTIVE, GermplasmListDAOTest.PROGRAM_UUID, null));
		this.dataManager.addGermplasm(germplasm, name);
		final GermplasmListData listData1 = new GermplasmListData(null, list1, germplasm.getGid(), 1, "EntryCode",
			"SeedSource", "Germplasm Name 5", "GroupName", 0, 99995);
		this.manager.addGermplasmListData(listData1);

		final GermplasmList list2 = saveGermplasm(GermplasmListTestDataInitializer.createGermplasmListTestData(
			TEST_GERMPLASM_LIST_NAME, GermplasmListDAOTest.TEST_GERMPLASM_LIST_DESC,
			TEST_GERMPLASM_LIST_DATE, GermplasmListDAOTest.TEST_GERMPLASM_LIST_TYPE_LST,
			GermplasmListDAOTest.TEST_GERMPLASM_LIST_USER_ID, GermplasmListDAOTest.STATUS_ACTIVE, GermplasmListDAOTest.PROGRAM_UUID, null));
		this.dataManager.addGermplasm(germplasm, name);
		final GermplasmListData listData2 = new GermplasmListData(null, list2, germplasm.getGid(), 1, "EntryCode",
			"SeedSource", "Germplasm Name 5", "GroupName", 0, 99995);
		this.manager.addGermplasmListData(listData2);

		Assert.assertTrue(this.dao.getGermplasmUsedInMoreThanOneList(Arrays.asList(germplasm.getGid())).size() > 0);
	}

}
