
package org.generationcp.middleware.dao.germplasmlist;

import org.generationcp.middleware.GermplasmTestDataGenerator;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.api.germplasmlist.search.GermplasmListSearchRequest;
import org.generationcp.middleware.api.germplasmlist.search.GermplasmListSearchResponse;
import org.generationcp.middleware.data.initializer.GermplasmListTestDataInitializer;
import org.generationcp.middleware.data.initializer.StudyTestDataInitializer;
import org.generationcp.middleware.domain.sqlfilter.SqlTextFilter;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.StudyDataManagerImpl;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.util.Util;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.CollectionUtils;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

public class GermplasmListDAOTest extends IntegrationTestBase {

	private static final String TEST_GERMPLASM_LIST_NAME = "TestGermplasmListName";
	private static final String TEST_GERMPLASM_LIST_DESC = "TestGermplasmListDesc";
	private static final long TEST_GERMPLASM_LIST_DATE = 20141103;
	private static final String TEST_GERMPLASM_LIST_TYPE_LST = "LST";
	private static final String TEST_LIST_DESCRIPTION = "Test List Description";
	private static final String TEST_LIST_NOTES = "Test List Notes";
	private static final String PARENT_FOLDER_NAME = "Integration Test";

	private static final String PROGRAM_UUID = UUID.randomUUID().toString();
	private static final List<String> EXCLUDED_GERMPLASM_LIST_TYPES = new ArrayList<>();
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

	@Autowired
	private GermplasmListManager manager;

	@Autowired
	private OntologyDataManager ontologyManager;

	@Autowired
	private WorkbenchTestDataUtil workbenchTestDataUtil;

	private GermplasmListDAO germplasmListDAO;
	private GermplasmListDataDAO germplasmListDataDAO;
	private GermplasmTestDataGenerator germplasmTestDataGenerator;

	private GermplasmList list;
	private Germplasm germplasm;
	private Project commonTestProject;

	private DaoFactory daoFactory;
	private StudyTestDataInitializer studyTDI;

	private CropType cropType;

	static {
		EXCLUDED_GERMPLASM_LIST_TYPES.add("STUDY");
		EXCLUDED_GERMPLASM_LIST_TYPES.add("CHECK");
		EXCLUDED_GERMPLASM_LIST_TYPES.add("ADVANCED");
		EXCLUDED_GERMPLASM_LIST_TYPES.add("CROSSES");
		EXCLUDED_GERMPLASM_LIST_TYPES.add("FOLDER");
	}

	private static final int UNKNOWN_GENERATIVE_METHOD_ID = 1;

	@Before
	public void setUp() throws Exception {
		this.daoFactory = new DaoFactory(this.sessionProvder);

		this.germplasmListDAO = new GermplasmListDAO();
		this.germplasmListDAO.setSession(this.sessionProvder.getSession());
		this.germplasmTestDataGenerator = new GermplasmTestDataGenerator(this.daoFactory);

		this.germplasmListDataDAO = new GermplasmListDataDAO();
		this.germplasmListDataDAO.setSession(this.sessionProvder.getSession());

		this.cropType = new CropType();
		this.cropType.setUseUUID(false);

		this.list = this.saveGermplasmList(GermplasmListTestDataInitializer.createGermplasmListTestData(
			TEST_GERMPLASM_LIST_NAME, TEST_GERMPLASM_LIST_DESC,
			TEST_GERMPLASM_LIST_DATE, TEST_GERMPLASM_LIST_TYPE_LST,
			this.findAdminUser(), GermplasmList.Status.FOLDER.getCode(), PROGRAM_UUID, null));

		this.germplasm = this.createGermplasm();

		this.createGermplasmListData(this.list, this.germplasm);

		this.workbenchTestDataUtil.setUpWorkbench();

		if (this.commonTestProject == null) {
			this.commonTestProject = this.workbenchTestDataUtil.getCommonTestProject();
		}

		final StudyDataManagerImpl studyDataManager = new StudyDataManagerImpl(this.sessionProvder);
		this.studyTDI = new StudyTestDataInitializer(studyDataManager, this.ontologyManager, this.commonTestProject,
			this.sessionProvder);


	}

	@Test
	public void testCountByName() {
		Assert.assertEquals("There should be one germplasm list with name " + TEST_GERMPLASM_LIST_NAME, 1,
			this.germplasmListDAO.countByName(TEST_GERMPLASM_LIST_NAME, Operation.EQUAL));

		this.list.setStatus(GermplasmList.Status.DELETED.getCode());
		this.saveGermplasmList(this.list);
		Assert.assertEquals("There should be no germplasm list with name " + TEST_GERMPLASM_LIST_NAME, 0,
			this.germplasmListDAO.countByName(TEST_GERMPLASM_LIST_NAME, Operation.EQUAL));
		// revert status
		this.list.setStatus(GermplasmList.Status.FOLDER.getCode());
		this.saveGermplasmList(this.list);

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetGermplasmListTypes() {
		final List<String> germplasmListTypes = this.germplasmListDAO.getGermplasmListTypes();
		for (final String listType : EXCLUDED_GERMPLASM_LIST_TYPES) {
			Assert.assertFalse(listType + " should not be in the Results Array", germplasmListTypes.contains(listType));
		}
	}

	@Test
	public void testGetGermplasmUsedInLockedList_GermplasmIsNotInLockedList() {
		final List<Integer> germplasmUsedInLockedList = this.germplasmListDAO.getGermplasmUsedInLockedList(
			Collections.singletonList(this.germplasm.getGid()));
		Assert.assertTrue(CollectionUtils.isEmpty(germplasmUsedInLockedList));
	}

	@Test
	public void testGetListIdsByGids() {
		final List<Integer> listIds = this.germplasmListDAO.getListIdsByGIDs(Collections.singletonList(this.germplasm.getGid()));
		Assert.assertEquals(1, listIds.size());
		Assert.assertEquals(this.list.getId(), listIds.get(0));
	}

	@Test
	public void testGetGermplasmUsedInLockedList_GermplasmIsInLockedList() {
		this.list.setStatus(GermplasmListDAO.LOCKED_LIST_STATUS);
		this.germplasmListDAO.saveOrUpdate(this.list);
		this.sessionProvder.getSession().flush();
		final List<Integer> germplasmUsedInLockedList = this.germplasmListDAO.getGermplasmUsedInLockedList(
			Collections.singletonList(this.germplasm.getGid()));
		Assert.assertEquals(1, germplasmUsedInLockedList.size());
		Assert.assertEquals(this.germplasm.getGid(), germplasmUsedInLockedList.get(0));
	}

	private GermplasmList saveGermplasmList(final GermplasmList list) throws MiddlewareQueryException {
		final GermplasmList newList = this.germplasmListDAO.saveOrUpdate(list);
		return newList;
	}

	@Test
	public void testGetAllTopLevelLists() {

		final List<GermplasmList> germplasmLists = this.germplasmListDAO.getAllTopLevelLists(PROGRAM_UUID);

		Assert.assertFalse(germplasmLists.isEmpty());
		Assert.assertEquals(TEST_GERMPLASM_LIST_NAME, germplasmLists.get(0).getName());
		Assert.assertEquals(PROGRAM_UUID, germplasmLists.get(0).getProgramUUID());

	}

	@Test
	public void testGetAllTopLevelListsCropList() {

		// Create a test germplasm list accessible to all programs (a list with null programUUID).
		final String testGermplasmName = "Germplasm List acessible from all programs";

		final GermplasmList germplasmList = this
			.saveGermplasmList(
				GermplasmListTestDataInitializer.createGermplasmListTestData(testGermplasmName, "", TEST_GERMPLASM_LIST_DATE, "",
					this.findAdminUser(), GermplasmList.Status.FOLDER.getCode(), null, null));

		final List<GermplasmList> germplasmLists = this.germplasmListDAO.getAllTopLevelLists(null);

		Assert.assertFalse(germplasmLists.isEmpty());
		Assert.assertTrue(germplasmLists.contains(germplasmList));

	}

	@Test
	public void testGetListsByProgramUUID() {
		final List<GermplasmList> germplasmLists = this.germplasmListDAO.getListsByProgramUUID(PROGRAM_UUID);
		final GermplasmList resultList = germplasmLists.get(0);
		Assert.assertEquals("The list name should be " + TEST_GERMPLASM_LIST_NAME,
			TEST_GERMPLASM_LIST_NAME, resultList.getName());
		Assert.assertEquals("The list description should be " + TEST_GERMPLASM_LIST_DESC,
			TEST_GERMPLASM_LIST_DESC, resultList.getDescription());
	}

	@Test
	public void testGetByGIDandProgramUUID() {
		final List<GermplasmList> germplasmLists =
			this.germplasmListDAO.getByGIDandProgramUUID(this.germplasm.getGid(), 0, 1, PROGRAM_UUID);
		final GermplasmList resultList = germplasmLists.get(0);
		Assert.assertEquals("The list name should be " + TEST_GERMPLASM_LIST_NAME,
			TEST_GERMPLASM_LIST_NAME, resultList.getName());
		Assert.assertEquals("The list description should be " + TEST_GERMPLASM_LIST_DESC,
			TEST_GERMPLASM_LIST_DESC, resultList.getDescription());
	}

	@Test
	public void testCountByGIDandProgramUUID() {
		final int result =
			(int) this.germplasmListDAO.countByGIDandProgramUUID(this.germplasm.getGid(), PROGRAM_UUID);
		Assert.assertEquals("The count should be 1", 1, result);
	}

	@Test
	public void testGetAllGermplasmListsById() {
		final GermplasmList testList =
			GermplasmListTestDataInitializer.createGermplasmListTestData("TestList", TEST_LIST_DESCRIPTION,
				TEST_GERMPLASM_LIST_DATE, TEST_GERMPLASM_LIST_TYPE_LST,
				this.findAdminUser(), GermplasmList.Status.FOLDER.getCode(), PROGRAM_UUID,
				null);
		this.saveGermplasmList(testList);
		final List<GermplasmList> allGermplasmListsById =
			this.germplasmListDAO.getAllGermplasmListsById(Collections.singletonList(testList.getId()));
		Assert.assertTrue("Returned results should not be empty", !allGermplasmListsById.isEmpty());
		Assert.assertEquals("Returned results should contain one item",
			1, allGermplasmListsById.size());

	}

	@Test
	public void getAndCountSearchGermplasmList_OK() {
		final Germplasm germplasm1 = this.createGermplasm();
		final Germplasm germplasm2 = this.createGermplasm();

		// Create a parent folder
		final GermplasmList parentFolder = this.createFolder();

		// Create a list with two entries and a parent folder
		final GermplasmList list = this.saveGermplasmList(
			GermplasmListTestDataInitializer.createGermplasmListTestData(TEST_GERMPLASM_LIST_NAME, TEST_LIST_DESCRIPTION,
				TEST_GERMPLASM_LIST_DATE, TEST_GERMPLASM_LIST_TYPE_LST,
				this.findAdminUser(), GermplasmList.Status.LIST.getCode(), PROGRAM_UUID,
				null, parentFolder, TEST_LIST_NOTES));
		this.createGermplasmListData(list, germplasm1);
		this.createGermplasmListData(list, germplasm2);

		// Create a deleted list
		this.saveGermplasmList(
			GermplasmListTestDataInitializer.createGermplasmListTestData("TestList2", TEST_LIST_DESCRIPTION,
				TEST_GERMPLASM_LIST_DATE, TEST_GERMPLASM_LIST_TYPE_LST,
				this.findAdminUser(), GermplasmList.Status.DELETED.getCode(), PROGRAM_UUID, null, parentFolder, null));

		final GermplasmListSearchRequest germplasmListSearchRequest = new GermplasmListSearchRequest();
		germplasmListSearchRequest.setParentFolderName(PARENT_FOLDER_NAME);
		assertThat(this.germplasmListDAO.countSearchGermplasmList(germplasmListSearchRequest, PROGRAM_UUID), is(1L));

		final List<GermplasmListSearchResponse> response =
			this.germplasmListDAO.searchGermplasmList(germplasmListSearchRequest, new PageRequest(0, 50), PROGRAM_UUID);
		assertThat(response, hasSize(1));
		final GermplasmListSearchResponse germplasmListSearchResponse = response.get(0);
		assertThat(germplasmListSearchResponse.getListId(), is(list.getId()));
		assertThat(germplasmListSearchResponse.getListName(), is(list.getName()));
		assertThat(germplasmListSearchResponse.getParentFolderName(), is(list.getParent().getName()));
		assertThat(germplasmListSearchResponse.getDescription(), is(list.getDescription()));
		assertThat(germplasmListSearchResponse.getListOwner(), is(ADMIN_NAME));
		assertThat(germplasmListSearchResponse.getListType(), is(list.getType()));
		assertThat(germplasmListSearchResponse.getNumberOfEntries(), is(2));
		assertFalse(germplasmListSearchResponse.isLocked());
		assertThat(germplasmListSearchResponse.getNotes(), is(list.getNotes()));
		assertThat(germplasmListSearchResponse.getCreationDate(), is(this.convertLongToDate(list.getDate())));
	}

	@Test
	public void getAndCountSearchGermplasmList_filterAndSortByListName() {
		// Create a parent folder
		final GermplasmList parentFolder = this.createFolder();

		final GermplasmList list1 = this.saveGermplasmList(
			GermplasmListTestDataInitializer.createGermplasmListTestData("New List 1", TEST_LIST_DESCRIPTION,
				TEST_GERMPLASM_LIST_DATE, TEST_GERMPLASM_LIST_TYPE_LST,
				this.findAdminUser(), GermplasmList.Status.LIST.getCode(), PROGRAM_UUID,
				null, parentFolder, TEST_LIST_NOTES));

		final GermplasmList list2 = this.saveGermplasmList(
			GermplasmListTestDataInitializer.createGermplasmListTestData("This is a new list", TEST_LIST_DESCRIPTION,
				TEST_GERMPLASM_LIST_DATE, TEST_GERMPLASM_LIST_TYPE_LST,
				this.findAdminUser(), GermplasmList.Status.LIST.getCode(), PROGRAM_UUID,
				null, parentFolder, TEST_LIST_NOTES));

		// Filter by name with exact match
		final GermplasmListSearchRequest germplasmListSearchRequest1 = new GermplasmListSearchRequest();
		germplasmListSearchRequest1.setParentFolderName(PARENT_FOLDER_NAME);
		germplasmListSearchRequest1.setListNameFilter(this.createSQLTextFilter("New List 1", SqlTextFilter.Type.EXACTMATCH));

		assertThat(this.germplasmListDAO.countSearchGermplasmList(germplasmListSearchRequest1, PROGRAM_UUID), is(1L));

		final List<GermplasmListSearchResponse> response1 =
			this.germplasmListDAO.searchGermplasmList(germplasmListSearchRequest1, new PageRequest(0, 50), PROGRAM_UUID);
		assertThat(response1, hasSize(1));
		assertThat(response1.get(0).getListId(), is(list1.getId()));

		// Filter by name containing
		final GermplasmListSearchRequest germplasmListSearchRequest2 = new GermplasmListSearchRequest();
		germplasmListSearchRequest2.setParentFolderName(PARENT_FOLDER_NAME);
		germplasmListSearchRequest2.setListNameFilter(this.createSQLTextFilter("new list", SqlTextFilter.Type.CONTAINS));

		assertThat(this.germplasmListDAO.countSearchGermplasmList(germplasmListSearchRequest2, PROGRAM_UUID), is(2L));

		//Sort by list name ascending
		final Pageable pageRequest1 = this.createPageRequest(Sort.Direction.ASC, "LIST_NAME");
		final List<GermplasmListSearchResponse> response2 =
			this.germplasmListDAO.searchGermplasmList(germplasmListSearchRequest2, pageRequest1, PROGRAM_UUID);
		assertThat(response2, hasSize(2));
		assertThat(response2.get(0).getListId(), is(list1.getId()));
		assertThat(response2.get(1).getListId(), is(list2.getId()));

		//Sort by list name descending
		final Pageable pageRequest2 = this.createPageRequest(Sort.Direction.DESC, "LIST_NAME");
		final List<GermplasmListSearchResponse> response3 =
			this.germplasmListDAO.searchGermplasmList(germplasmListSearchRequest2, pageRequest2, PROGRAM_UUID);
		assertThat(response3, hasSize(2));
		assertThat(response3.get(0).getListId(), is(list2.getId()));
		assertThat(response3.get(1).getListId(), is(list1.getId()));

		// Filter by name starts with
		final GermplasmListSearchRequest germplasmListSearchRequest4 = new GermplasmListSearchRequest();
		germplasmListSearchRequest4.setParentFolderName(PARENT_FOLDER_NAME);
		germplasmListSearchRequest4.setListNameFilter(this.createSQLTextFilter("new", SqlTextFilter.Type.STARTSWITH));

		assertThat(this.germplasmListDAO.countSearchGermplasmList(germplasmListSearchRequest4, PROGRAM_UUID), is(1L));

		final List<GermplasmListSearchResponse> response4 =
			this.germplasmListDAO.searchGermplasmList(germplasmListSearchRequest4, new PageRequest(0, 50), PROGRAM_UUID);
		assertThat(response4, hasSize(1));
		assertThat(response4.get(0).getListId(), is(list1.getId()));

		// Filter by name ends with
		final GermplasmListSearchRequest germplasmListSearchRequest5 = new GermplasmListSearchRequest();
		germplasmListSearchRequest5.setParentFolderName(PARENT_FOLDER_NAME);
		germplasmListSearchRequest5.setListNameFilter(this.createSQLTextFilter("list", SqlTextFilter.Type.ENDSWITH));

		assertThat(this.germplasmListDAO.countSearchGermplasmList(germplasmListSearchRequest5, PROGRAM_UUID), is(1L));

		final List<GermplasmListSearchResponse> response5 =
			this.germplasmListDAO.searchGermplasmList(germplasmListSearchRequest5, new PageRequest(0, 50), PROGRAM_UUID);
		assertThat(response5, hasSize(1));
		assertThat(response5.get(0).getListId(), is(list2.getId()));

		// Filter by other name ends with
		final GermplasmListSearchRequest germplasmListSearchRequest6 = new GermplasmListSearchRequest();
		germplasmListSearchRequest6.setListNameFilter(this.createSQLTextFilter("other", SqlTextFilter.Type.ENDSWITH));

		assertThat(this.germplasmListDAO.countSearchGermplasmList(germplasmListSearchRequest6, PROGRAM_UUID), is(0L));
		assertThat(this.germplasmListDAO.searchGermplasmList(germplasmListSearchRequest6, new PageRequest(0, 50), PROGRAM_UUID),
			hasSize(0));
	}

	@Test
	public void getAndCountSearchGermplasmList_filterAndSortByParentFolderName() {
		// Create a parent folder
		final GermplasmList parentFolder = this.createFolder();

		// Create a folder
		final GermplasmList parentFolder1 = this.saveGermplasmList(
			GermplasmListTestDataInitializer.createGermplasmListTestData("This is a parent Folder", TEST_LIST_DESCRIPTION,
				TEST_GERMPLASM_LIST_DATE, TEST_GERMPLASM_LIST_TYPE_LST,
				this.findAdminUser(), GermplasmList.Status.FOLDER.getCode(), PROGRAM_UUID,
				null, parentFolder, null));

		// Create a folder
		final GermplasmList parentFolder2 = this.saveGermplasmList(
			GermplasmListTestDataInitializer.createGermplasmListTestData("Parent Folder", TEST_LIST_DESCRIPTION,
				TEST_GERMPLASM_LIST_DATE, TEST_GERMPLASM_LIST_TYPE_LST,
				this.findAdminUser(), GermplasmList.Status.FOLDER.getCode(), PROGRAM_UUID,
				null, parentFolder, null));

		//Create list using parentFolder1
		final GermplasmList list1 = this.saveGermplasmList(
			GermplasmListTestDataInitializer.createGermplasmListTestData("New List 1", TEST_LIST_DESCRIPTION,
				TEST_GERMPLASM_LIST_DATE, TEST_GERMPLASM_LIST_TYPE_LST,
				this.findAdminUser(), GermplasmList.Status.LIST.getCode(), PROGRAM_UUID,
				null, parentFolder1, TEST_LIST_NOTES));

		//Create list using parentFolder1
		final GermplasmList list2 = this.saveGermplasmList(
			GermplasmListTestDataInitializer.createGermplasmListTestData("New List 2", TEST_LIST_DESCRIPTION,
				TEST_GERMPLASM_LIST_DATE, TEST_GERMPLASM_LIST_TYPE_LST,
				this.findAdminUser(), GermplasmList.Status.LIST.getCode(), PROGRAM_UUID,
				null, parentFolder2, TEST_LIST_NOTES));

		// Filter by parent folder name
		final GermplasmListSearchRequest germplasmListSearchRequest1 = new GermplasmListSearchRequest();
		germplasmListSearchRequest1.setParentFolderName(PARENT_FOLDER_NAME);
		germplasmListSearchRequest1.setParentFolderName("is a parent");

		assertThat(this.germplasmListDAO.countSearchGermplasmList(germplasmListSearchRequest1, PROGRAM_UUID), is(1L));

		final List<GermplasmListSearchResponse> response1 =
			this.germplasmListDAO.searchGermplasmList(germplasmListSearchRequest1, new PageRequest(0, 50), PROGRAM_UUID);
		assertThat(response1, hasSize(1));
		assertThat(response1.get(0).getListId(), is(list1.getId()));

		// Filter by parent folder name
		final GermplasmListSearchRequest germplasmListSearchRequest2 = new GermplasmListSearchRequest();
		germplasmListSearchRequest2.setParentFolderName(PARENT_FOLDER_NAME);
		germplasmListSearchRequest2.setParentFolderName("parent folder");

		assertThat(this.germplasmListDAO.countSearchGermplasmList(germplasmListSearchRequest2, PROGRAM_UUID), is(2L));

		final Pageable pageRequest1 = this.createPageRequest(Sort.Direction.ASC, "PARENT_FOLDER_NAME");
		final List<GermplasmListSearchResponse> response2 =
			this.germplasmListDAO.searchGermplasmList(germplasmListSearchRequest2, pageRequest1, PROGRAM_UUID);
		assertThat(response2, hasSize(2));
		assertThat(response2.get(0).getListId(), is(list2.getId()));
		assertThat(response2.get(1).getListId(), is(list1.getId()));

		final Pageable pageRequest2 = this.createPageRequest(Sort.Direction.DESC, "PARENT_FOLDER_NAME");
		final List<GermplasmListSearchResponse> response3 =
			this.germplasmListDAO.searchGermplasmList(germplasmListSearchRequest2, pageRequest2, PROGRAM_UUID);
		assertThat(response3, hasSize(2));
		assertThat(response3.get(0).getListId(), is(list1.getId()));
		assertThat(response3.get(1).getListId(), is(list2.getId()));

		// Filter by parent folder name
		final GermplasmListSearchRequest germplasmListSearchRequest4 = new GermplasmListSearchRequest();
		germplasmListSearchRequest4.setParentFolderName("other");

		assertThat(this.germplasmListDAO.countSearchGermplasmList(germplasmListSearchRequest4, PROGRAM_UUID), is(0L));
		assertThat(this.germplasmListDAO.searchGermplasmList(germplasmListSearchRequest4, new PageRequest(0, 50), PROGRAM_UUID),
			hasSize(0));
	}

	@Test
	public void getAndCountSearchGermplasmList_filterAndSortByDescription() {
		// Create a parent folder
		final GermplasmList parentFolder = this.createFolder();

		final GermplasmList list1 = this.saveGermplasmList(
			GermplasmListTestDataInitializer.createGermplasmListTestData("New List 1", "Description 1",
				TEST_GERMPLASM_LIST_DATE, TEST_GERMPLASM_LIST_TYPE_LST,
				this.findAdminUser(), GermplasmList.Status.LIST.getCode(), PROGRAM_UUID,
				null, parentFolder, TEST_LIST_NOTES));

		final GermplasmList list2 = this.saveGermplasmList(
			GermplasmListTestDataInitializer.createGermplasmListTestData("New List 2", "Some description",
				TEST_GERMPLASM_LIST_DATE, TEST_GERMPLASM_LIST_TYPE_LST,
				this.findAdminUser(), GermplasmList.Status.LIST.getCode(), PROGRAM_UUID,
				null, parentFolder, TEST_LIST_NOTES));

		// Filter by description
		final GermplasmListSearchRequest germplasmListSearchRequest1 = new GermplasmListSearchRequest();
		germplasmListSearchRequest1.setParentFolderName(PARENT_FOLDER_NAME);
		germplasmListSearchRequest1.setDescription("description");

		assertThat(this.germplasmListDAO.countSearchGermplasmList(germplasmListSearchRequest1, PROGRAM_UUID), is(2L));

		final Pageable pageRequest1 = this.createPageRequest(Sort.Direction.ASC, "DESCRIPTION");
		final List<GermplasmListSearchResponse> response1 =
			this.germplasmListDAO.searchGermplasmList(germplasmListSearchRequest1, pageRequest1, PROGRAM_UUID);
		assertThat(response1, hasSize(2));
		assertThat(response1.get(0).getListId(), is(list1.getId()));
		assertThat(response1.get(1).getListId(), is(list2.getId()));

		final Pageable pageRequest2 = this.createPageRequest(Sort.Direction.DESC, "DESCRIPTION");
		final List<GermplasmListSearchResponse> response2 =
			this.germplasmListDAO.searchGermplasmList(germplasmListSearchRequest1, pageRequest2, PROGRAM_UUID);
		assertThat(response2, hasSize(2));
		assertThat(response2.get(0).getListId(), is(list2.getId()));
		assertThat(response2.get(1).getListId(), is(list1.getId()));

		// Filter by description
		final GermplasmListSearchRequest germplasmListSearchRequest2 = new GermplasmListSearchRequest();
		germplasmListSearchRequest2.setParentFolderName(PARENT_FOLDER_NAME);
		germplasmListSearchRequest2.setOwnerName("other");

		assertThat(this.germplasmListDAO.countSearchGermplasmList(germplasmListSearchRequest2, PROGRAM_UUID), is(0L));
		assertThat(this.germplasmListDAO.searchGermplasmList(germplasmListSearchRequest2, new PageRequest(0, 50), PROGRAM_UUID),
			hasSize(0));
	}

	@Test
	public void getAndCountSearchGermplasmList_filterByOwnerFolderName() {
		// Create a parent folder
		final GermplasmList parentFolder = this.createFolder();

		final GermplasmList list1 = this.saveGermplasmList(
			GermplasmListTestDataInitializer.createGermplasmListTestData("New List 1", TEST_LIST_DESCRIPTION,
				TEST_GERMPLASM_LIST_DATE, TEST_GERMPLASM_LIST_TYPE_LST,
				this.findAdminUser(), GermplasmList.Status.LIST.getCode(), PROGRAM_UUID,
				null, parentFolder, TEST_LIST_NOTES));

		// Filter by owner name
		final GermplasmListSearchRequest germplasmListSearchRequest1 = new GermplasmListSearchRequest();
		germplasmListSearchRequest1.setParentFolderName(PARENT_FOLDER_NAME);
		germplasmListSearchRequest1.setOwnerName("min");

		assertThat(this.germplasmListDAO.countSearchGermplasmList(germplasmListSearchRequest1, PROGRAM_UUID), is(1L));

		final List<GermplasmListSearchResponse> response1 =
			this.germplasmListDAO.searchGermplasmList(germplasmListSearchRequest1, new PageRequest(0, 50), PROGRAM_UUID);
		assertThat(response1, hasSize(1));
		assertThat(response1.get(0).getListId(), is(list1.getId()));

		// Filter by owner name
		final GermplasmListSearchRequest germplasmListSearchRequest2 = new GermplasmListSearchRequest();
		germplasmListSearchRequest2.setParentFolderName(PARENT_FOLDER_NAME);
		germplasmListSearchRequest2.setOwnerName("other");

		assertThat(this.germplasmListDAO.countSearchGermplasmList(germplasmListSearchRequest2, PROGRAM_UUID), is(0L));
		assertThat(this.germplasmListDAO.searchGermplasmList(germplasmListSearchRequest2, new PageRequest(0, 50), PROGRAM_UUID),
			hasSize(0));
	}

	@Test
	public void getAndCountSearchGermplasmList_filterAndSortByListTypeIds() {
		// Create a parent folder
		final GermplasmList parentFolder = this.createFolder();

		//Create a list
		final GermplasmList list1 = this.saveGermplasmList(
			GermplasmListTestDataInitializer.createGermplasmListTestData("New List 1", TEST_LIST_DESCRIPTION,
				TEST_GERMPLASM_LIST_DATE, TEST_GERMPLASM_LIST_TYPE_LST,
				this.findAdminUser(), GermplasmList.Status.LOCKED_LIST.getCode(), PROGRAM_UUID,
				null, parentFolder, TEST_LIST_NOTES));

		final GermplasmList list2 = this.saveGermplasmList(
			GermplasmListTestDataInitializer.createGermplasmListTestData("New List 1", TEST_LIST_DESCRIPTION,
				TEST_GERMPLASM_LIST_DATE, "HARVEST",
				this.findAdminUser(), GermplasmList.Status.LOCKED_LIST.getCode(), PROGRAM_UUID,
				null, parentFolder, TEST_LIST_NOTES));

		// Filter by list type
		final GermplasmListSearchRequest germplasmListSearchRequest1 = new GermplasmListSearchRequest();
		germplasmListSearchRequest1.setParentFolderName(PARENT_FOLDER_NAME);
		germplasmListSearchRequest1.setListTypes(Arrays.asList(TEST_GERMPLASM_LIST_TYPE_LST, "HARVEST"));

		assertThat(this.germplasmListDAO.countSearchGermplasmList(germplasmListSearchRequest1, PROGRAM_UUID), is(2L));

		final Pageable pageRequest1 = this.createPageRequest(Sort.Direction.ASC, "LIST_TYPE");
		final List<GermplasmListSearchResponse> response1 =
			this.germplasmListDAO.searchGermplasmList(germplasmListSearchRequest1, pageRequest1, PROGRAM_UUID);
		assertThat(response1, hasSize(2));
		assertThat(response1.get(0).getListId(), is(list2.getId()));
		assertThat(response1.get(1).getListId(), is(list1.getId()));

		final Pageable pageRequest2 = this.createPageRequest(Sort.Direction.DESC, "LIST_TYPE");
		final List<GermplasmListSearchResponse> response2 =
			this.germplasmListDAO.searchGermplasmList(germplasmListSearchRequest1, pageRequest2, PROGRAM_UUID);
		assertThat(response2, hasSize(2));
		assertThat(response2.get(0).getListId(), is(list1.getId()));
		assertThat(response2.get(1).getListId(), is(list2.getId()));

		// Filter by list type
		final GermplasmListSearchRequest germplasmListSearchRequest2 = new GermplasmListSearchRequest();
		germplasmListSearchRequest2.setParentFolderName(PARENT_FOLDER_NAME);
		germplasmListSearchRequest2.setListTypes(Arrays.asList("other"));

		assertThat(this.germplasmListDAO.countSearchGermplasmList(germplasmListSearchRequest2, PROGRAM_UUID), is(0L));
		assertThat(this.germplasmListDAO.searchGermplasmList(germplasmListSearchRequest2, new PageRequest(0, 50), PROGRAM_UUID),
			hasSize(0));
	}

	@Test
	public void getAndCountSearchGermplasmList_filterAndSortByLocked() {
		// Create a parent folder
		final GermplasmList parentFolder = this.createFolder();

		//Create a locked list
		final GermplasmList list1 = this.saveGermplasmList(
			GermplasmListTestDataInitializer.createGermplasmListTestData("New List 1", TEST_LIST_DESCRIPTION,
				TEST_GERMPLASM_LIST_DATE, TEST_GERMPLASM_LIST_TYPE_LST,
				this.findAdminUser(), GermplasmList.Status.LOCKED_LIST.getCode(), PROGRAM_UUID,
				null, parentFolder, TEST_LIST_NOTES));

		//Create an unlocked list
		final GermplasmList list2 = this.saveGermplasmList(
			GermplasmListTestDataInitializer.createGermplasmListTestData("New List 3", TEST_LIST_DESCRIPTION,
				TEST_GERMPLASM_LIST_DATE, TEST_GERMPLASM_LIST_TYPE_LST,
				this.findAdminUser(), GermplasmList.Status.LIST.getCode(), PROGRAM_UUID,
				null, parentFolder, TEST_LIST_NOTES));

		// Filter by locked list
		final GermplasmListSearchRequest germplasmListSearchRequest1 = new GermplasmListSearchRequest();
		germplasmListSearchRequest1.setParentFolderName(PARENT_FOLDER_NAME);
		germplasmListSearchRequest1.setLocked(true);

		assertThat(this.germplasmListDAO.countSearchGermplasmList(germplasmListSearchRequest1, PROGRAM_UUID), is(1L));

		final List<GermplasmListSearchResponse> response1 =
			this.germplasmListDAO.searchGermplasmList(germplasmListSearchRequest1, new PageRequest(0, 50), PROGRAM_UUID);
		assertThat(response1, hasSize(1));
		assertThat(response1.get(0).getListId(), is(list1.getId()));

		// Filter by unlocked list
		final GermplasmListSearchRequest germplasmListSearchRequest2 = new GermplasmListSearchRequest();
		germplasmListSearchRequest2.setParentFolderName(PARENT_FOLDER_NAME);
		germplasmListSearchRequest2.setLocked(false);

		assertThat(this.germplasmListDAO.countSearchGermplasmList(germplasmListSearchRequest2, PROGRAM_UUID), is(1L));

		final List<GermplasmListSearchResponse> response2 =
			this.germplasmListDAO.searchGermplasmList(germplasmListSearchRequest2, new PageRequest(0, 50), PROGRAM_UUID);
		assertThat(response2, hasSize(1));
		assertThat(response2.get(0).getListId(), is(list2.getId()));

		// Sort by status
		final GermplasmListSearchRequest germplasmListSearchRequest3 = new GermplasmListSearchRequest();
		germplasmListSearchRequest3.setParentFolderName(PARENT_FOLDER_NAME);
		assertThat(this.germplasmListDAO.countSearchGermplasmList(germplasmListSearchRequest3, PROGRAM_UUID), is(2L));

		final Pageable pageRequest1 = this.createPageRequest(Sort.Direction.ASC, "LOCKED");
		final List<GermplasmListSearchResponse> response3 =
			this.germplasmListDAO.searchGermplasmList(germplasmListSearchRequest3, pageRequest1, PROGRAM_UUID);
		assertThat(response3, hasSize(2));
		assertThat(response3.get(0).getListId(), is(list2.getId()));
		assertThat(response3.get(1).getListId(), is(list1.getId()));

		final Pageable pageRequest2 = this.createPageRequest(Sort.Direction.DESC, "LOCKED");
		final List<GermplasmListSearchResponse> response4 =
			this.germplasmListDAO.searchGermplasmList(germplasmListSearchRequest3, pageRequest2, PROGRAM_UUID);
		assertThat(response4, hasSize(2));
		assertThat(response4.get(0).getListId(), is(list1.getId()));
		assertThat(response4.get(1).getListId(), is(list2.getId()));
	}

	@Test
	public void getAndCountSearchGermplasmList_filterAndSortByEntryNumbers() {
		final Germplasm germplasm1 = this.createGermplasm();
		final Germplasm germplasm2 = this.createGermplasm();
		final Germplasm germplasm3 = this.createGermplasm();
		final Germplasm germplasm4 = this.createGermplasm();

		// Create a parent folder
		final GermplasmList parentFolder = this.createFolder();

		// Create a list with two entries
		final GermplasmList list1 = this.saveGermplasmList(
			GermplasmListTestDataInitializer.createGermplasmListTestData(TEST_GERMPLASM_LIST_NAME, TEST_LIST_DESCRIPTION,
				TEST_GERMPLASM_LIST_DATE, TEST_GERMPLASM_LIST_TYPE_LST,
				this.findAdminUser(), GermplasmList.Status.LIST.getCode(), PROGRAM_UUID,
				null, parentFolder, TEST_LIST_NOTES));
		this.createGermplasmListData(list1, germplasm1);
		this.createGermplasmListData(list1, germplasm2);

		// Create a list with four entries
		final GermplasmList list2 = this.saveGermplasmList(
			GermplasmListTestDataInitializer.createGermplasmListTestData(TEST_GERMPLASM_LIST_NAME, TEST_LIST_DESCRIPTION,
				TEST_GERMPLASM_LIST_DATE, TEST_GERMPLASM_LIST_TYPE_LST,
				this.findAdminUser(), GermplasmList.Status.LIST.getCode(), PROGRAM_UUID,
				null, parentFolder, TEST_LIST_NOTES));
		this.createGermplasmListData(list2, germplasm1);
		this.createGermplasmListData(list2, germplasm2);
		this.createGermplasmListData(list2, germplasm3);
		this.createGermplasmListData(list2, germplasm4);

		//Filter by only entry numbers from
		final GermplasmListSearchRequest germplasmListSearchRequest1 = new GermplasmListSearchRequest();
		germplasmListSearchRequest1.setParentFolderName(PARENT_FOLDER_NAME);
		germplasmListSearchRequest1.setNumberOfEntriesFrom(3);
		assertThat(this.germplasmListDAO.countSearchGermplasmList(germplasmListSearchRequest1, PROGRAM_UUID), is(1L));

		final Pageable pageRequest1 = this.createPageRequest(Sort.Direction.ASC, "NUMBER_OF_ENTRIES");
		final List<GermplasmListSearchResponse> response1 =
			this.germplasmListDAO.searchGermplasmList(germplasmListSearchRequest1, pageRequest1, PROGRAM_UUID);
		assertThat(response1, hasSize(1));
		assertThat(response1.get(0).getListId(), is(list2.getId()));

		germplasmListSearchRequest1.setNumberOfEntriesFrom(5);
		assertThat(this.germplasmListDAO.countSearchGermplasmList(germplasmListSearchRequest1, PROGRAM_UUID), is(0L));
		assertThat(this.germplasmListDAO.searchGermplasmList(germplasmListSearchRequest1, new PageRequest(0, 50), PROGRAM_UUID),
			hasSize(0));

		//Filter by only entry numbers to
		final GermplasmListSearchRequest germplasmListSearchRequest2 = new GermplasmListSearchRequest();
		germplasmListSearchRequest2.setParentFolderName(PARENT_FOLDER_NAME);
		germplasmListSearchRequest2.setNumberOfEntriesTo(3);
		assertThat(this.germplasmListDAO.countSearchGermplasmList(germplasmListSearchRequest2, PROGRAM_UUID), is(1L));

		final Pageable pageRequest2 = this.createPageRequest(Sort.Direction.ASC, "NUMBER_OF_ENTRIES");
		final List<GermplasmListSearchResponse> response3 =
			this.germplasmListDAO.searchGermplasmList(germplasmListSearchRequest2, pageRequest2, PROGRAM_UUID);
		assertThat(response3, hasSize(1));
		assertThat(response3.get(0).getListId(), is(list1.getId()));

		germplasmListSearchRequest2.setNumberOfEntriesTo(1);
		assertThat(this.germplasmListDAO.countSearchGermplasmList(germplasmListSearchRequest2, PROGRAM_UUID), is(0L));
		assertThat(this.germplasmListDAO.searchGermplasmList(germplasmListSearchRequest2, new PageRequest(0, 50), PROGRAM_UUID),
			hasSize(0));

		//Filter by entry numbers from and to
		final GermplasmListSearchRequest germplasmListSearchRequest3 = new GermplasmListSearchRequest();
		germplasmListSearchRequest3.setParentFolderName(PARENT_FOLDER_NAME);
		germplasmListSearchRequest3.setNumberOfEntriesFrom(2);
		germplasmListSearchRequest3.setNumberOfEntriesTo(4);
		assertThat(this.germplasmListDAO.countSearchGermplasmList(germplasmListSearchRequest3, PROGRAM_UUID), is(2L));

		final Pageable pageRequest3 = this.createPageRequest(Sort.Direction.ASC, "NUMBER_OF_ENTRIES");
		final List<GermplasmListSearchResponse> response4 =
			this.germplasmListDAO.searchGermplasmList(germplasmListSearchRequest3, pageRequest3, PROGRAM_UUID);
		assertThat(response4, hasSize(2));
		assertThat(response4.get(0).getListId(), is(list1.getId()));
		assertThat(response4.get(1).getListId(), is(list2.getId()));

		final Pageable pageRequest4 = this.createPageRequest(Sort.Direction.DESC, "NUMBER_OF_ENTRIES");
		final List<GermplasmListSearchResponse> response5 =
			this.germplasmListDAO.searchGermplasmList(germplasmListSearchRequest3, pageRequest4, PROGRAM_UUID);
		assertThat(response5, hasSize(2));
		assertThat(response5.get(0).getListId(), is(list2.getId()));
		assertThat(response5.get(1).getListId(), is(list1.getId()));

		germplasmListSearchRequest3.setNumberOfEntriesFrom(5);
		germplasmListSearchRequest3.setNumberOfEntriesTo(10);
		assertThat(this.germplasmListDAO.countSearchGermplasmList(germplasmListSearchRequest3, PROGRAM_UUID), is(0L));
		assertThat(this.germplasmListDAO.searchGermplasmList(germplasmListSearchRequest3, new PageRequest(0, 50), PROGRAM_UUID),
			hasSize(0));
	}

	@Test
	public void getAndCountSearchGermplasmList_filterAndSortByNotes() {
		// Create a parent folder
		final GermplasmList parentFolder = this.createFolder();

		final GermplasmList list1 = this.saveGermplasmList(
			GermplasmListTestDataInitializer.createGermplasmListTestData("New List 1", TEST_LIST_DESCRIPTION,
				TEST_GERMPLASM_LIST_DATE, TEST_GERMPLASM_LIST_TYPE_LST,
				this.findAdminUser(), GermplasmList.Status.LIST.getCode(), PROGRAM_UUID,
				null, parentFolder, "this is a new note"));

		final GermplasmList list2 = this.saveGermplasmList(
			GermplasmListTestDataInitializer.createGermplasmListTestData("New List 1", TEST_LIST_DESCRIPTION,
				TEST_GERMPLASM_LIST_DATE, TEST_GERMPLASM_LIST_TYPE_LST,
				this.findAdminUser(), GermplasmList.Status.LIST.getCode(), PROGRAM_UUID,
				null, parentFolder, "New note 2"));

		// Filter by notes
		final GermplasmListSearchRequest germplasmListSearchRequest1 = new GermplasmListSearchRequest();
		germplasmListSearchRequest1.setParentFolderName(PARENT_FOLDER_NAME);
		germplasmListSearchRequest1.setNotes("new");

		assertThat(this.germplasmListDAO.countSearchGermplasmList(germplasmListSearchRequest1, PROGRAM_UUID), is(2L));

		final Pageable pageRequest1 = this.createPageRequest(Sort.Direction.ASC, "NOTES");
		final List<GermplasmListSearchResponse> response2 =
			this.germplasmListDAO.searchGermplasmList(germplasmListSearchRequest1, pageRequest1, PROGRAM_UUID);
		assertThat(response2, hasSize(2));
		assertThat(response2.get(0).getListId(), is(list2.getId()));
		assertThat(response2.get(1).getListId(), is(list1.getId()));

		final Pageable pageRequest2 = this.createPageRequest(Sort.Direction.DESC, "NOTES");
		final List<GermplasmListSearchResponse> response3 =
			this.germplasmListDAO.searchGermplasmList(germplasmListSearchRequest1, pageRequest2, PROGRAM_UUID);
		assertThat(response3, hasSize(2));
		assertThat(response3.get(0).getListId(), is(list1.getId()));
		assertThat(response3.get(1).getListId(), is(list2.getId()));

		// Filter by notes
		final GermplasmListSearchRequest germplasmListSearchRequest2 = new GermplasmListSearchRequest();
		germplasmListSearchRequest2.setParentFolderName(PARENT_FOLDER_NAME);
		germplasmListSearchRequest2.setOwnerName("other");

		assertThat(this.germplasmListDAO.countSearchGermplasmList(germplasmListSearchRequest2, PROGRAM_UUID), is(0L));
		assertThat(this.germplasmListDAO.searchGermplasmList(germplasmListSearchRequest2, new PageRequest(0, 50), PROGRAM_UUID),
			hasSize(0));
	}

	@Test
	public void getAndCountSearchGermplasmList_filterAndSortByDate() {
		// Create a parent folder
		final GermplasmList parentFolder = this.createFolder();

		final GermplasmList list1 = this.saveGermplasmList(
			GermplasmListTestDataInitializer.createGermplasmListTestData("New List 1", TEST_LIST_DESCRIPTION,
				TEST_GERMPLASM_LIST_DATE, TEST_GERMPLASM_LIST_TYPE_LST,
				this.findAdminUser(), GermplasmList.Status.LIST.getCode(), PROGRAM_UUID,
				null, parentFolder, "this is a new note"));

		// Filter by only date from
		final GermplasmListSearchRequest germplasmListSearchRequest1 = new GermplasmListSearchRequest();
		germplasmListSearchRequest1.setParentFolderName(PARENT_FOLDER_NAME);
		germplasmListSearchRequest1.setListDateFrom(Date.from(LocalDate.of(2014, 11, 3).atStartOfDay(ZoneId.systemDefault()).toInstant()));

		assertThat(this.germplasmListDAO.countSearchGermplasmList(germplasmListSearchRequest1, PROGRAM_UUID), is(1L));

		final List<GermplasmListSearchResponse> response1 =
			this.germplasmListDAO.searchGermplasmList(germplasmListSearchRequest1, new PageRequest(0, 50), PROGRAM_UUID);
		assertThat(response1, hasSize(1));
		assertThat(response1.get(0).getListId(), is(list1.getId()));

		// Filter by only date from
		final GermplasmListSearchRequest germplasmListSearchRequest2 = new GermplasmListSearchRequest();
		germplasmListSearchRequest2.setParentFolderName(PARENT_FOLDER_NAME);
		germplasmListSearchRequest2.setListDateFrom(Date.from(LocalDate.of(2014, 11, 4).atStartOfDay(ZoneId.systemDefault()).toInstant()));

		assertThat(this.germplasmListDAO.countSearchGermplasmList(germplasmListSearchRequest2, PROGRAM_UUID), is(0L));
		assertThat(this.germplasmListDAO.searchGermplasmList(germplasmListSearchRequest2, new PageRequest(0, 50), PROGRAM_UUID),
			hasSize(0));

		// Filter by only date to
		final GermplasmListSearchRequest germplasmListSearchRequest3 = new GermplasmListSearchRequest();
		germplasmListSearchRequest3.setParentFolderName(PARENT_FOLDER_NAME);
		germplasmListSearchRequest3.setListDateTo(Date.from(LocalDate.of(2014, 11, 3).atStartOfDay(ZoneId.systemDefault()).toInstant()));

		assertThat(this.germplasmListDAO.countSearchGermplasmList(germplasmListSearchRequest3, PROGRAM_UUID), is(1L));

		final List<GermplasmListSearchResponse> response3 =
			this.germplasmListDAO.searchGermplasmList(germplasmListSearchRequest3, new PageRequest(0, 50), PROGRAM_UUID);
		assertThat(response3, hasSize(1));
		assertThat(response3.get(0).getListId(), is(list1.getId()));

		// Filter by only date to
		final GermplasmListSearchRequest germplasmListSearchRequest4 = new GermplasmListSearchRequest();
		germplasmListSearchRequest4.setParentFolderName(PARENT_FOLDER_NAME);
		germplasmListSearchRequest4.setListDateTo(Date.from(LocalDate.of(2014, 11, 2).atStartOfDay(ZoneId.systemDefault()).toInstant()));

		assertThat(this.germplasmListDAO.countSearchGermplasmList(germplasmListSearchRequest4, PROGRAM_UUID), is(0L));
		assertThat(this.germplasmListDAO.searchGermplasmList(germplasmListSearchRequest4, new PageRequest(0, 50), PROGRAM_UUID),
			hasSize(0));

		final GermplasmList list2 = this.saveGermplasmList(
			GermplasmListTestDataInitializer.createGermplasmListTestData("New List 1", TEST_LIST_DESCRIPTION,
				20151103, TEST_GERMPLASM_LIST_TYPE_LST,
				this.findAdminUser(), GermplasmList.Status.LIST.getCode(), PROGRAM_UUID,
				null, parentFolder, "this is a new note"));

		// Filter by date from and to
		final GermplasmListSearchRequest germplasmListSearchRequest5 = new GermplasmListSearchRequest();
		germplasmListSearchRequest5.setParentFolderName(PARENT_FOLDER_NAME);
		germplasmListSearchRequest5.setListDateFrom(Date.from(LocalDate.of(2014, 11, 2).atStartOfDay(ZoneId.systemDefault()).toInstant()));
		germplasmListSearchRequest5.setListDateTo(Date.from(LocalDate.of(2016, 11, 4).atStartOfDay(ZoneId.systemDefault()).toInstant()));

		assertThat(this.germplasmListDAO.countSearchGermplasmList(germplasmListSearchRequest5, PROGRAM_UUID), is(2L));

		final Pageable pageRequest1 = this.createPageRequest(Sort.Direction.ASC, "CREATION_DATE");
		final List<GermplasmListSearchResponse> response5 =
			this.germplasmListDAO.searchGermplasmList(germplasmListSearchRequest5, pageRequest1, PROGRAM_UUID);
		assertThat(response5, hasSize(2));
		assertThat(response5.get(0).getListId(), is(list1.getId()));
		assertThat(response5.get(1).getListId(), is(list2.getId()));

		final Pageable pageRequest2 = this.createPageRequest(Sort.Direction.DESC, "CREATION_DATE");
		final List<GermplasmListSearchResponse> response6 =
			this.germplasmListDAO.searchGermplasmList(germplasmListSearchRequest5, pageRequest2, PROGRAM_UUID);
		assertThat(response6, hasSize(2));
		assertThat(response6.get(0).getListId(), is(list2.getId()));
		assertThat(response6.get(1).getListId(), is(list1.getId()));

		//  Filter by date from and to
		final GermplasmListSearchRequest germplasmListSearchRequest6 = new GermplasmListSearchRequest();
		germplasmListSearchRequest6.setParentFolderName(PARENT_FOLDER_NAME);
		germplasmListSearchRequest6.setListDateFrom(Date.from(LocalDate.of(2014, 11, 4).atStartOfDay(ZoneId.systemDefault()).toInstant()));
		germplasmListSearchRequest6.setListDateTo(Date.from(LocalDate.of(2014, 11, 5).atStartOfDay(ZoneId.systemDefault()).toInstant()));

		assertThat(this.germplasmListDAO.countSearchGermplasmList(germplasmListSearchRequest6, PROGRAM_UUID), is(0L));
		assertThat(this.germplasmListDAO.searchGermplasmList(germplasmListSearchRequest6, new PageRequest(0, 50), PROGRAM_UUID),
			hasSize(0));
	}

	private Germplasm createGermplasm() {
		final Name name = new Name(null, null, 1, 1, "Name", 0, 0, 0);
		final Germplasm germplasm = new Germplasm(null, 0, 0, 0, 0, Util.getCurrentDateAsIntegerValue(), 0, 0, 0, name, null,
			new Method(UNKNOWN_GENERATIVE_METHOD_ID));

		this.germplasmTestDataGenerator.addGermplasm(germplasm, name, this.cropType);
		return germplasm;
	}

	private GermplasmList createFolder() {
		return this.saveGermplasmList(
			GermplasmListTestDataInitializer.createGermplasmListTestData(PARENT_FOLDER_NAME, TEST_LIST_DESCRIPTION,
				TEST_GERMPLASM_LIST_DATE, TEST_GERMPLASM_LIST_TYPE_LST,
				this.findAdminUser(), GermplasmList.Status.FOLDER.getCode(), PROGRAM_UUID,
				null));
	}

	private GermplasmListData createGermplasmListData(final GermplasmList germplasmList, final Germplasm germplasm) {
		final GermplasmListData germplasmListData = new GermplasmListData(null, germplasmList, germplasm.getGid(),
			1, "SeedSource", "GroupName", 0, 99995);
		this.manager.addGermplasmListData(germplasmListData);
		return germplasmListData;
	}

	private SqlTextFilter createSQLTextFilter(final String value, final SqlTextFilter.Type type) {
		final SqlTextFilter sqlTextFilter = new SqlTextFilter();
		sqlTextFilter.setValue(value);
		sqlTextFilter.setType(type);
		return sqlTextFilter;
	}

	private Pageable createPageRequest(final Sort.Direction direction, final String property) {
		return new PageRequest(0, 50, new Sort(new Sort.Order(direction, property)));
	}

	private Date convertLongToDate(final long date) {
		return Date.valueOf(LocalDate.parse(String.valueOf(date), DATE_FORMATTER));
	}

}
