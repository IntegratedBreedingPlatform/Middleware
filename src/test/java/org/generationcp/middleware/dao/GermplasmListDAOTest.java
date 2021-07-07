
package org.generationcp.middleware.dao;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.dao.germplasmlist.GermplasmListDAO;
import org.generationcp.middleware.data.initializer.GermplasmListTestDataInitializer;
import org.generationcp.middleware.data.initializer.StudyTestDataInitializer;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.StudyDataManagerImpl;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.ListMetadata;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.util.Util;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GermplasmListDAOTest extends IntegrationTestBase {

	@Autowired
	private GermplasmListManager manager;

	@Autowired
	private GermplasmDataManager dataManager;

	@Autowired
	private OntologyDataManager ontologyManager;

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
	private static final List<String> EXCLUDED_GERMPLASM_LIST_TYPES = new ArrayList<>();

	private GermplasmList list;
	private Germplasm germplasm;
	private Project commonTestProject;

	private StudyReference studyReference;
	private StudyTestDataInitializer studyTDI;

	private CropType cropType;

	static {
		GermplasmListDAOTest.EXCLUDED_GERMPLASM_LIST_TYPES.add("STUDY");
		GermplasmListDAOTest.EXCLUDED_GERMPLASM_LIST_TYPES.add("CHECK");
		GermplasmListDAOTest.EXCLUDED_GERMPLASM_LIST_TYPES.add("ADVANCED");
		GermplasmListDAOTest.EXCLUDED_GERMPLASM_LIST_TYPES.add("CROSSES");
		GermplasmListDAOTest.EXCLUDED_GERMPLASM_LIST_TYPES.add("FOLDER");
	}
	private static final int UNKNOWN_GENERATIVE_METHOD_ID = 1;

	@Before
	public void setUp() throws Exception {
		this.dao = new GermplasmListDAO();
		this.dao.setSession(this.sessionProvder.getSession());

		this.cropType = new CropType();
		this.cropType.setUseUUID(false);

		this.list = this.saveGermplasm(GermplasmListTestDataInitializer.createGermplasmListTestData(
				TEST_GERMPLASM_LIST_NAME, GermplasmListDAOTest.TEST_GERMPLASM_LIST_DESC,
				TEST_GERMPLASM_LIST_DATE, GermplasmListDAOTest.TEST_GERMPLASM_LIST_TYPE_LST,
				GermplasmListDAOTest.TEST_GERMPLASM_LIST_USER_ID, GermplasmListDAOTest.STATUS_ACTIVE, GermplasmListDAOTest.PROGRAM_UUID, null));
		final Name name = new Name(null, null, 1, 1, "Name", 0, 0, 0);
		this.germplasm = new Germplasm(null, GermplasmListDAOTest.UNKNOWN_GENERATIVE_METHOD_ID, 0, 0, 0, 0, 0, Util.getCurrentDateAsIntegerValue(), name);
		this.dataManager.addGermplasm(this.germplasm, name, this.cropType);
		final GermplasmListData germplasmListData = new GermplasmListData(null, this.list, this.germplasm.getGid(), 1, "EntryCode",
				"SeedSource", "Germplasm Name 5", "GroupName", 0, 99995);
		this.manager.addGermplasmListData(germplasmListData);

		this.workbenchTestDataUtil.setUpWorkbench();

		if (this.commonTestProject == null) {
			this.commonTestProject = this.workbenchTestDataUtil.getCommonTestProject();
		}

		final StudyDataManagerImpl studyDataManager = new StudyDataManagerImpl(this.sessionProvder);
		this.studyTDI = new StudyTestDataInitializer(studyDataManager, this.ontologyManager, this.commonTestProject,
			this.locationManager, this.sessionProvder);

		this.studyReference = this.studyTDI.addTestStudy("ABCD");

	}

	@Test
	public void testCountByName() {
		Assert.assertEquals("There should be one germplasm list with name " + GermplasmListDAOTest.TEST_GERMPLASM_LIST_NAME, 1,
				this.dao.countByName(GermplasmListDAOTest.TEST_GERMPLASM_LIST_NAME, Operation.EQUAL));

		this.list.setStatus(GermplasmListDAOTest.STATUS_DELETED);
		this.saveGermplasm(this.list);
		Assert.assertEquals("There should be no germplasm list with name " + GermplasmListDAOTest.TEST_GERMPLASM_LIST_NAME, 0,
				this.dao.countByName(GermplasmListDAOTest.TEST_GERMPLASM_LIST_NAME, Operation.EQUAL));
		// revert status
		this.list.setStatus(GermplasmListDAOTest.STATUS_ACTIVE);
		this.saveGermplasm(this.list);

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetGermplasmListTypes() {
		final List<String> germplasmListTypes = this.dao.getGermplasmListTypes();
		for (final String listType : GermplasmListDAOTest.EXCLUDED_GERMPLASM_LIST_TYPES) {
			Assert.assertFalse(listType + " should not be in the Results Array", germplasmListTypes.contains(listType));
		}
	}

	@Test
	public void testGetGermplasmUsedInLockedList_GermplasmIsNotInLockedList() {
		final List<Integer> germplasmUsedInLockedList = this.dao.getGermplasmUsedInLockedList(
			Collections.singletonList(this.germplasm.getGid()));
		Assert.assertTrue(CollectionUtils.isEmpty(germplasmUsedInLockedList));
	}

	@Test
	public void testGetListIdsByGids() {
		final List<Integer> listIds = this.dao.getListIdsByGIDs(Collections.singletonList(this.germplasm.getGid()));
		Assert.assertEquals(1, listIds.size());
		Assert.assertEquals(this.list.getId(), listIds.get(0));
	}

	@Test
	public void testGetGermplasmUsedInLockedList_GermplasmIsInLockedList() {
		this.list.setStatus(GermplasmListDAO.LOCKED_LIST_STATUS);
		this.dao.saveOrUpdate(this.list);
		this.sessionProvder.getSession().flush();
		final List<Integer> germplasmUsedInLockedList = this.dao.getGermplasmUsedInLockedList(
			Collections.singletonList(this.germplasm.getGid()));
		Assert.assertEquals(1, germplasmUsedInLockedList.size());
		Assert.assertEquals(this.germplasm.getGid(), germplasmUsedInLockedList.get(0));
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

		final GermplasmList germplasmList = this
			.saveGermplasm(GermplasmListTestDataInitializer.createGermplasmListTestData(testGermplasmName, "" ,TEST_GERMPLASM_LIST_DATE ,"" ,
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
	public void testGetGermplasmFolderMetadata() {
		// Create germplasm test folder
		final GermplasmList testFolder = GermplasmListTestDataInitializer.createGermplasmListTestData("TestFolder", "Test Folder Description",
				GermplasmListDAOTest.TEST_GERMPLASM_LIST_DATE, GermplasmListDAOTest.TEST_GERMPLASM_LIST_TYPE_FOLDER,
				GermplasmListDAOTest.TEST_GERMPLASM_LIST_USER_ID, GermplasmListDAOTest.STATUS_ACTIVE, GermplasmListDAOTest.PROGRAM_UUID, null);
		this.saveGermplasm(testFolder);
		final Map<Integer, ListMetadata> result =
				this.dao.getGermplasmFolderMetadata(Collections.singletonList(testFolder.getId()));
		final ListMetadata germplasmFolderMetadata = result.get(testFolder.getId());
		Assert.assertNotNull("Newly created folder should not be null", germplasmFolderMetadata);
		Assert.assertEquals("Newly created folder should have zero children",
				new Integer(0), germplasmFolderMetadata.getNumberOfChildren());
	}

	@Test
	public void testGetAllGermplasmListsById() {
		final GermplasmList testList =
			GermplasmListTestDataInitializer.createGermplasmListTestData("TestList", GermplasmListDAOTest.TEST_LIST_DESCRIPTION,
				GermplasmListDAOTest.TEST_GERMPLASM_LIST_DATE, GermplasmListDAOTest.TEST_GERMPLASM_LIST_TYPE_LST,
				GermplasmListDAOTest.TEST_GERMPLASM_LIST_USER_ID, GermplasmListDAOTest.STATUS_ACTIVE, GermplasmListDAOTest.PROGRAM_UUID,
				null);
		this.saveGermplasm(testList);
		final List<GermplasmList> allGermplasmListsById = this.dao.getAllGermplasmListsById(Collections.singletonList(testList.getId()));
		Assert.assertTrue("Returned results should not be empty", !allGermplasmListsById.isEmpty());
		Assert.assertEquals("Returned results should contain one item",
			1, allGermplasmListsById.size());

	}
}
