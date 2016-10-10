
package org.generationcp.middleware.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.data.initializer.GermplasmListTestDataInitializer;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmFolderMetadata;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.util.Util;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

public class GermplasmListDAOTest extends IntegrationTestBase {


	private static final String KEYWORD = "QWERTY";
	private static final String TEST_GERMPLASM_LIST_NAME = KEYWORD + " List";
	private static final String TEST_GERMPLASM_LIST_DESC = "TestGermplasmListDesc";
	private static final String PROGRAM_UUID = "1001";
	private static final String TEST_GERMPLASM_NAME = KEYWORD + " Germplasm";
	
	private static final String TEST_LIST2_WITH_KEYWORD = "Some Other " + KEYWORD;
	private static final String TEST_LIST3_WITHOUT_KEYWORD = "Another List";
	
	@Autowired
	private GermplasmListManager germplasmListManager;
	
	@Autowired
	private GermplasmDataManager germplasmDataManager;

	private GermplasmListDAO germplasmListDAO;

	private static List<String> EXCLUDED_GERMPLASM_LIST_TYPES = new ArrayList<String>();
	private static GermplasmListTestDataInitializer germplasmListTestDataInitializer;

	private GermplasmList list;
	private Germplasm germplasm;

	static {
		GermplasmListDAOTest.EXCLUDED_GERMPLASM_LIST_TYPES.add("NURSERY");
		GermplasmListDAOTest.EXCLUDED_GERMPLASM_LIST_TYPES.add("TRIAL");
		GermplasmListDAOTest.EXCLUDED_GERMPLASM_LIST_TYPES.add("CHECK");
		GermplasmListDAOTest.EXCLUDED_GERMPLASM_LIST_TYPES.add("ADVANCED");
		GermplasmListDAOTest.EXCLUDED_GERMPLASM_LIST_TYPES.add("CROSSES");
		GermplasmListDAOTest.EXCLUDED_GERMPLASM_LIST_TYPES.add("FOLDER");
	}

	@BeforeClass
	public static void beforeClass() {
		germplasmListTestDataInitializer = new GermplasmListTestDataInitializer();
	}

	@Before
	public void setUp() throws Exception {
		this.germplasmListDAO = new GermplasmListDAO();
		this.germplasmListDAO.setSession(this.sessionProvder.getSession());

		// Create test list
		this.list = createAndSaveGermplasmList(TEST_GERMPLASM_LIST_NAME, TEST_GERMPLASM_LIST_DESC,
				GermplasmListTestDataInitializer.DEFAULT_GERMPLASM_LIST_TYPE, PROGRAM_UUID);
		
		// Create one germplasm and add as entry under that list
		final Name name = new Name(null, null, 1, 1, 1, TEST_GERMPLASM_NAME, 0, 0, 0);
		this.germplasm = new Germplasm(null, 0, 0, 0, 0, 1, 0, 0, Util.getCurrentDateAsIntegerValue(), name);
		this.germplasmDataManager.addGermplasm(this.germplasm, name);
		final GermplasmListData germplasmListData = new GermplasmListData(null, this.list, this.germplasm.getGid(), 1,
				"EntryCode", "SeedSource", TEST_GERMPLASM_NAME, "GroupName", 0, 99995);
		this.germplasmListManager.addGermplasmListData(germplasmListData);

	}

	private GermplasmList createAndSaveGermplasmList(final String name, final String description, final String listType,
			final String programUUID) {
		GermplasmList testList = germplasmListTestDataInitializer.createGermplasmList(name, description, listType,
				programUUID);
		germplasmListDAO.saveOrUpdate(testList);
		return testList;
	}

	@Test
	public void testHideSnapshotListTypes() {
		final GermplasmListDAO dao = new GermplasmListDAO();
		final Criteria criteria = Mockito.mock(Criteria.class);
		dao.hideSnapshotListTypes(criteria);
		final Criterion restrictedList = dao.getRestrictedSnapshopTypes();
		// this should ensure that the snapshot list types are added int he
		// criteria object
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
		Assert.assertEquals(
				"There should be one germplasm list with name " + GermplasmListDAOTest.TEST_GERMPLASM_LIST_NAME, 1,
				this.germplasmListDAO.countByName(GermplasmListDAOTest.TEST_GERMPLASM_LIST_NAME, Operation.EQUAL));

		this.list.setStatus(GermplasmListTestDataInitializer.STATUS_DELETED);
		this.germplasmListDAO.saveOrUpdate(this.list);
		
		Assert.assertEquals(
				"There should be no germplasm list with name " + GermplasmListDAOTest.TEST_GERMPLASM_LIST_NAME, 0,
				this.germplasmListDAO.countByName(GermplasmListDAOTest.TEST_GERMPLASM_LIST_NAME, Operation.EQUAL));
		// revert status
		this.list.setStatus(GermplasmListTestDataInitializer.STATUS_ACTIVE);
		this.germplasmListDAO.saveOrUpdate(this.list);

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetGermplasmListTypes() {
		final List<String> germplasmListTypes = this.germplasmListDAO.getGermplasmListTypes();
		for (final String listType : GermplasmListDAOTest.EXCLUDED_GERMPLASM_LIST_TYPES) {
			Assert.assertFalse(listType + " should not be in the Results Array", germplasmListTypes.contains(listType));
		}
	}

	@Test
	public void testGetAllListMetadata() {
		final List<GermplasmList> germplasmLists = this.germplasmListDAO
				.getListsByProgramUUID(GermplasmListDAOTest.PROGRAM_UUID);

		final List<Integer> germplasmListIds = new ArrayList<>();
		for (final GermplasmList germplasmList : germplasmLists) {
			germplasmListIds.add(germplasmList.getId());
		}

		final List<Object[]> listMetadata = this.germplasmListDAO.getAllListMetadata(germplasmListIds);
		Assert.assertEquals("Meta data size must be the same as the list size", listMetadata.size(),
				germplasmLists.size());
	}

	@Test
	public void testGetListsByProgramUUID() {
		final List<GermplasmList> germplasmLists = this.germplasmListDAO
				.getListsByProgramUUID(GermplasmListDAOTest.PROGRAM_UUID);
		final GermplasmList resultList = germplasmLists.get(0);
		Assert.assertEquals("The list name should be " + GermplasmListDAOTest.TEST_GERMPLASM_LIST_NAME,
				GermplasmListDAOTest.TEST_GERMPLASM_LIST_NAME, resultList.getName());
		Assert.assertEquals("The list description should be " + GermplasmListDAOTest.TEST_GERMPLASM_LIST_DESC,
				GermplasmListDAOTest.TEST_GERMPLASM_LIST_DESC, resultList.getDescription());
	}

	@Test
	public void testGetByGIDandProgramUUID() {
		final List<GermplasmList> germplasmLists = this.germplasmListDAO.getByGIDandProgramUUID(this.germplasm.getGid(),
				0, 1, GermplasmListDAOTest.PROGRAM_UUID);
		final GermplasmList resultList = germplasmLists.get(0);
		Assert.assertEquals("The list name should be " + GermplasmListDAOTest.TEST_GERMPLASM_LIST_NAME,
				GermplasmListDAOTest.TEST_GERMPLASM_LIST_NAME, resultList.getName());
		Assert.assertEquals("The list description should be " + GermplasmListDAOTest.TEST_GERMPLASM_LIST_DESC,
				GermplasmListDAOTest.TEST_GERMPLASM_LIST_DESC, resultList.getDescription());
	}

	@Test
	public void testCountByGIDandProgramUUID() {
		final int result = (int) this.germplasmListDAO.countByGIDandProgramUUID(this.germplasm.getGid(),
				GermplasmListDAOTest.PROGRAM_UUID);
		Assert.assertEquals("The count should be 1", 1, result);
	}

	@Test
	public void testGetGermplasmFolderMetadata() throws Exception {
		// Create germplasm test folder
		final GermplasmList testFolder = createAndSaveGermplasmList("TestFolder", "Test Folder Description",
				GermplasmListTestDataInitializer.FOLDER_GERMPLASM_LIST_TYPE, PROGRAM_UUID);

		final Map<Integer, GermplasmFolderMetadata> result = this.germplasmListDAO
				.getGermplasmFolderMetadata(Collections.singletonList(testFolder.getId()));

		final GermplasmFolderMetadata germplasmFolderMetadata = result.get(testFolder.getId());
		Assert.assertNotNull("Newly created folder should not be null", germplasmFolderMetadata);
		Assert.assertEquals("Newly created folder should have zero children", new Integer(0),
				germplasmFolderMetadata.getNumberOfChildren());
	}

	@Test
	public void testGetAllGermplasmListsById() throws Exception {
		final GermplasmList testList = createAndSaveGermplasmList("Test List", "Test List Description",
				GermplasmListTestDataInitializer.DEFAULT_GERMPLASM_LIST_TYPE, PROGRAM_UUID);

		this.germplasmListDAO.saveOrUpdate(testList);
		final List<GermplasmList> allGermplasmListsById = this.germplasmListDAO
				.getAllGermplasmListsById(Collections.singletonList(testList.getId()));
		Assert.assertTrue("Returned results should not be empty", !allGermplasmListsById.isEmpty());
		Assert.assertEquals("Returned results should contain one item", 1, allGermplasmListsById.size());

	}
	
	@Test
	public void testSearchForGermplasmListsUsingEqualOperation(){
		/*
		 *  Create other test lists. Total of 3 lists in DB:
		 *  1. "QWERTY List" with default germplasm - default list in setup method
		 *  2. Empty list "Some Other QWERTY"
		 *  3. "Another List" containing same GID as default germplasm, but different Designation saved in listdata
		 */
		this.createOtherTestLists();
		
		// Search for default list name "QWERTY List"
		List<GermplasmList> returnedLists = this.germplasmListDAO.searchForGermplasmLists(TEST_GERMPLASM_LIST_NAME, PROGRAM_UUID, Operation.EQUAL);
		Assert.assertNotNull(returnedLists);
		Assert.assertTrue("Expecting only 1 list to be returned with list name=" + this.list.getName(), returnedLists.size() == 1);
		Assert.assertEquals(this.list.getId(), returnedLists.get(0).getId());
		
		// Search for lists by created germplasm's GID
		String gidString = this.germplasm.getGid().toString();
		returnedLists = this.germplasmListDAO.searchForGermplasmLists(gidString, PROGRAM_UUID, Operation.EQUAL);
		Assert.assertNotNull(returnedLists);
		Assert.assertTrue("Expecting 2 lists to be returned with GID=" + gidString, returnedLists.size() == 2);
		
		// Search for lists by created germplasm's designation
		returnedLists = this.germplasmListDAO.searchForGermplasmLists(TEST_GERMPLASM_NAME, PROGRAM_UUID, Operation.EQUAL);
		Assert.assertNotNull(returnedLists);
		Assert.assertTrue("Expecting 1 list to be returned with Designation=" + TEST_GERMPLASM_NAME, returnedLists.size() == 1);
		Assert.assertEquals(this.list.getId(), returnedLists.get(0).getId());
	}
	
	
	@Test
	public void testSearchForGermplasmListsUsingLikeOperation(){
		/*
		 *  Create other test lists. Total of 3 lists in DB:
		 *  1. "QWERTY List" with default germplasm - default list in setup method
		 *  2. Empty list "Some Other QWERTY"
		 *  3. "Another List" containing same GID as default germplasm, but different Designation saved in listdata
		 */
		this.createOtherTestLists();
		
		// Search for lists with keyword starting with keyword "QWERTY"
		List<GermplasmList> returnedLists = this.germplasmListDAO.searchForGermplasmLists(KEYWORD + "%", PROGRAM_UUID, Operation.LIKE);
		Assert.assertNotNull(returnedLists);
		Assert.assertTrue("Expecting only 1 list to be returned starting with keyword=" + KEYWORD, returnedLists.size() == 1);
		Assert.assertEquals(this.list.getId(), returnedLists.get(0).getId());
		
		// Search for lists with keyword containing keyword "QWERTY". 
		// Names of first two lists contain "QWERTY", 3rd list has listdata.designation containing "QWERTY"
		returnedLists = this.germplasmListDAO.searchForGermplasmLists("%" + KEYWORD + "%", PROGRAM_UUID, Operation.LIKE);
		Assert.assertNotNull(returnedLists);
		Assert.assertTrue("Expecting 3 lists with to be returned containing keyword=" + KEYWORD, returnedLists.size() == 3);
		
	}
	
	
	private void createOtherTestLists(){
		createAndSaveGermplasmList(TEST_LIST2_WITH_KEYWORD, "Some Description", GermplasmListTestDataInitializer.DEFAULT_GERMPLASM_LIST_TYPE, PROGRAM_UUID);
		final GermplasmList testList = createAndSaveGermplasmList(TEST_LIST3_WITHOUT_KEYWORD, "Some Description", GermplasmListTestDataInitializer.DEFAULT_GERMPLASM_LIST_TYPE, PROGRAM_UUID);
		final GermplasmListData germplasmListData = new GermplasmListData(null, testList, this.germplasm.getGid(), 1,
				"EntryCode", "SeedSource", "A-" + KEYWORD, "GroupName", 0, 99995);
		this.germplasmListManager.addGermplasmListData(germplasmListData);
	}
	
	

}
