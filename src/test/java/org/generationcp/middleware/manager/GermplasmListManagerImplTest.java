/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 * <p/>
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.middleware.manager;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.TransformerUtils;
import org.generationcp.middleware.DataSetupTest;
import org.generationcp.middleware.GermplasmTestDataGenerator;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.dao.ListDataProjectDAO;
import org.generationcp.middleware.data.initializer.GermplasmListDataTestDataInitializer;
import org.generationcp.middleware.data.initializer.GermplasmListTestDataInitializer;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.domain.gms.GermplasmListNewColumnsInfo;
import org.generationcp.middleware.domain.gms.GermplasmListType;
import org.generationcp.middleware.domain.gms.ListDataColumn;
import org.generationcp.middleware.domain.gms.ListDataInfo;
import org.generationcp.middleware.domain.gms.SystemDefinedEntryType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.operation.saver.ListDataProjectSaver;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.ListDataProject;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.service.api.DataImportService;
import org.generationcp.middleware.service.api.FieldbookService;
import org.generationcp.middleware.utils.test.Debug;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.hibernate.Query;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsNot.not;

public class GermplasmListManagerImplTest extends IntegrationTestBase {

	public static final String TEST_LIST_1_PARENT = "Test List #1 Parent";
	public static final String TEST_LIST_1 = "Test List #1";
	public static final String TEST_LIST_444 = "TestList444";
	public static final String TEST_LIST_5 = "Test List #5";
	public static final String TEST_LIST_3 = "Test List #3";
	public static final String TEST_LIST_6 = "Test List #6";
	private static final String TEST_GERMPLASM_LIST_NAME = "TestGermplasmListName";
	private static final String TEST_GERMPLASM_LIST_DESC = "TestGermplasmListDesc";
	private static final long TEST_GERMPLASM_LIST_DATE = 20141103;
	private static final String TEST_GERMPLASM_LIST_TYPE_LST = "LST";
	private static final String GERMPLASM_PREFERRED_NAME_PREFIX = DataSetupTest.GERMPLSM_PREFIX + "PR-";

	private static final int TEST_GERMPLASM_LIST_USER_ID = 1;
	private static final Integer STATUS_ACTIVE = 0;
	private ListDataProjectSaver listDataProjectSaver;

	@Autowired
	private GermplasmListManager manager;

	@Autowired
	private GermplasmDataManager dataManager;

	@Autowired
	private DataImportService dataImportService;

	@Autowired
	private FieldbookService middlewareFieldbookService;

	private GermplasmTestDataGenerator germplasmTestDataGenerator;

	private static final String PROGRAM_UUID = "a7433c01-4f46-4bc8-ae3a-678f0b62ac23";
	private static final String OTHER_PROGRAM_UUID = "b67d2e71-4f46-4bc8-ae3a-678f0b62ac23";
	private static final String OTHER_PROGRAM_LIST_NAME = "Other Program List";
	private static final String GERMPLASM_LIST_NAME = "Germplasm List Name";
	private static final String GERMPLASM_LIST_DESC = "Germplasm List Description";
	private static final String LIST_PROGRAM_UUID = "1001";

	private Integer parentId;
	private Integer listId;
	private Integer lrecId;

	private Germplasm testGermplasm;

	private GermplasmListTestDataInitializer germplasmListTDI;
	private Integer studyId;
	private Germplasm parentGermplasm;
	private DataSetupTest dataSetupTest;

	@Before
	public void setUpBefore() throws Exception {
		this.listDataProjectSaver = new ListDataProjectSaver(this.sessionProvder);
		this.germplasmListTDI = new GermplasmListTestDataInitializer();
		this.dataSetupTest = new DataSetupTest();
		this.dataSetupTest.setDataImportService(this.dataImportService);
		this.dataSetupTest.setGermplasmListManager(this.manager);
		this.dataSetupTest.setMiddlewareFieldbookService(this.middlewareFieldbookService);
		this.testGermplasm = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		this.dataManager.addGermplasm(this.testGermplasm, this.testGermplasm.getPreferredName());

		final GermplasmList germplasmListOther = this.germplasmListTDI
			.createGermplasmList(GermplasmListManagerImplTest.OTHER_PROGRAM_LIST_NAME, Integer.valueOf(1),
				GermplasmListManagerImplTest.OTHER_PROGRAM_LIST_NAME + " Desc", null, 1,
				GermplasmListManagerImplTest.OTHER_PROGRAM_UUID);
		this.manager.addGermplasmList(germplasmListOther);

		final GermplasmList germplasmListParent = this.germplasmListTDI
			.createGermplasmList(TEST_LIST_1_PARENT, Integer.valueOf(1), "Test Parent List #1", null, 1,
				GermplasmListManagerImplTest.PROGRAM_UUID);
		this.parentId = this.manager.addGermplasmList(germplasmListParent);

		final GermplasmList germplasmList = this.germplasmListTDI
			.createGermplasmList(TEST_LIST_1, Integer.valueOf(1), "Test List #1 for GCP-92", germplasmListParent, 1,
				GermplasmListManagerImplTest.PROGRAM_UUID);
		this.manager.addGermplasmList(germplasmList);

		final GermplasmList germplasmList1 = this.germplasmListTDI
			.createGermplasmList(TEST_LIST_444, Integer.valueOf(1), "Test List #4 for GCP-92", null, 1,
				GermplasmListManagerImplTest.PROGRAM_UUID);
		this.manager.addGermplasmList(germplasmList1);

		final GermplasmListData germplasmListData =
			GermplasmListDataTestDataInitializer.createGermplasmListData(germplasmList1, this.testGermplasm.getGid(), 2);
		this.manager.addGermplasmListData(germplasmListData);

		final GermplasmList germplasmList2 = this.germplasmListTDI
			.createGermplasmList(TEST_LIST_5, Integer.valueOf(1), "Test List #5 for GCP-92", null, 1,
				GermplasmListManagerImplTest.PROGRAM_UUID);
		this.manager.addGermplasmList(germplasmList2);

		final GermplasmListData germplasmListData1 =
			GermplasmListDataTestDataInitializer.createGermplasmListData(germplasmList2, this.testGermplasm.getGid(), 1);
		this.manager.addGermplasmListData(germplasmListData1);

		final GermplasmList germplasmList3 = this.germplasmListTDI
			.createGermplasmList(TEST_LIST_3, Integer.valueOf(1), "Test List #3 for GCP-92", null, 1,
				GermplasmListManagerImplTest.PROGRAM_UUID);
		this.manager.addGermplasmList(germplasmList3);

		final GermplasmList germplasmList6 = this.germplasmListTDI
			.createGermplasmList(TEST_LIST_6, Integer.valueOf(1), "Test List #6 for GCP-92", null, 1,
				GermplasmListManagerImplTest.PROGRAM_UUID);
		this.listId = this.manager.addGermplasmList(germplasmList6);

		final GermplasmListData germplasmListData2 =
			GermplasmListDataTestDataInitializer.createGermplasmListData(germplasmList6, this.testGermplasm.getGid(), 1);
		this.manager.addGermplasmListData(germplasmListData2);
		this.lrecId = germplasmListData2.getId();

		final GermplasmList testGermplasmList = this.germplasmListTDI
			.createGermplasmList(GermplasmListManagerImplTest.GERMPLASM_LIST_NAME, Integer.valueOf(1),
				GermplasmListManagerImplTest.GERMPLASM_LIST_DESC, null, 1, GermplasmListManagerImplTest.LIST_PROGRAM_UUID);
		this.manager.addGermplasmList(testGermplasmList);

		final GermplasmListData listData =
			GermplasmListDataTestDataInitializer.createGermplasmListData(testGermplasmList, this.testGermplasm.getGid(), 2);
		this.manager.addGermplasmListData(listData);

		if (this.germplasmTestDataGenerator == null) {
			this.germplasmTestDataGenerator = new GermplasmTestDataGenerator(this.dataManager);
		}

	}

	/*
	 * Create nursery to create proper listdataproject records. Would be needing
	 * nursery as well for refactoring on ListDataProject.getByStudy method
	 * later on
	 */
	private int createNurseryTestData() {
		final String programUUID = "884fefcc-1cbd-4e0f-9186-ceeef3aa3b78";
		this.parentGermplasm = this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames();

		final Integer[] gids = this.germplasmTestDataGenerator
			.createChildrenGermplasm(DataSetupTest.NUMBER_OF_GERMPLASM, GERMPLASM_PREFERRED_NAME_PREFIX, this.parentGermplasm);

		final int nurseryId = this.dataSetupTest.createNurseryForGermplasm(programUUID, gids, "ABCD");

		return nurseryId;
	}

	@Test
	public void testGetGermplasmListById() {
		final GermplasmList list = this.manager.getGermplasmListById(listId);
		Assert.assertEquals(listId, list.getId());
	}

	@Test
	public void testGetAllGermplasmLists() {
		final int count = (int) this.manager.countAllGermplasmLists();
		final List<GermplasmList> lists = this.manager.getAllGermplasmLists(0, count);
		Assert.assertEquals(count, lists.size());
	}

	@Test
	public void testCountAllGermplasmLists() {
		Assert.assertTrue(this.manager.countAllGermplasmLists() > 0);
	}

	@Test
	public void testGetGermplasmListByName() {
		final List<GermplasmList> lists =
			this.manager.getGermplasmListByName(TEST_LIST_1, GermplasmListManagerImplTest.PROGRAM_UUID, 0, 5, Operation.LIKE);
		Assert.assertEquals(TEST_LIST_1, lists.get(0).getName());
	}

	@Test
	public void testCountGermplasmListByName() {
		Assert.assertEquals(1, this.manager.countGermplasmListByName(TEST_LIST_1, Operation.LIKE));
	}

	@Test
	public void testCountGermplasmListByStatus() {
		final Integer status = Integer.valueOf(1);
		Assert.assertTrue(this.manager.countGermplasmListByStatus(status) > 0);
	}

	@Test
	public void testGetGermplasmListByGID() {
		final List<GermplasmList> results = this.manager.getGermplasmListByGID(testGermplasm.getGid(), 0, 200);
		Assert.assertEquals(TEST_LIST_444, results.get(0).getName());
	}

	@Test
	public void testCountGermplasmListByGID() {
		Assert.assertEquals(4, this.manager.countGermplasmListByGID(testGermplasm.getGid()));
	}

	@Test
	public void testGetGermplasmListDataByListId() {
		final List<GermplasmListData> results = this.manager.getGermplasmListDataByListId(listId);
		Assert.assertEquals(1, results.size());
	}

	@Test
	public void testCountGermplasmListDataByListId() {
		Assert.assertEquals(1, this.manager.countGermplasmListDataByListId(listId));
	}

	@Test
	public void testGetGermplasmListDataByListIdAndGID() {
		final List<GermplasmListData> results = this.manager.getGermplasmListDataByListIdAndGID(listId, testGermplasm.getGid());
		Assert.assertEquals(1, results.size());
	}

	@Test
	public void testGetGermplasmListDataByListIdAndEntryId() {
		final Integer entryId = Integer.valueOf(1);
		final GermplasmListData data = this.manager.getGermplasmListDataByListIdAndEntryId(listId, entryId);
		Assert.assertEquals(listId, data.getList().getId());
		Assert.assertEquals(entryId, data.getEntryId());
	}

	@Test
	public void testAddGermplasmList() {
		final GermplasmList germplasmList =
			new GermplasmList(null, TEST_LIST_1, Long.valueOf(20120305), "LST", Integer.valueOf(1), "Test List #1 for GCP-92", null, 1);
		germplasmList.setProgramUUID(GermplasmListManagerImplTest.PROGRAM_UUID);

		final Integer id = this.manager.addGermplasmList(germplasmList);
		Assert.assertNotNull(this.manager.getGermplasmListById(id));
	}

	@Test
	public void testDeleteGermplasmList() {

		final GermplasmList germplasmList =
			this.manager.getGermplasmListByName(TEST_LIST_3, GermplasmListManagerImplTest.PROGRAM_UUID, 0, 1, Operation.EQUAL).get(0);

		this.manager.deleteGermplasmList(germplasmList);

		// After delete, make sure that the system can't find the deleted germplasm list.
		final List<GermplasmList> result =
			this.manager.getGermplasmListByName(TEST_LIST_3, GermplasmListManagerImplTest.PROGRAM_UUID, 0, 1, Operation.EQUAL);
		Assert.assertTrue(result.isEmpty());

	}

	@Test
	public void testDeleteGermplasmListByListId() {
		final GermplasmList germplasmList =
			this.manager.getGermplasmListByName(TEST_LIST_1, GermplasmListManagerImplTest.PROGRAM_UUID, 0, 1, Operation.EQUAL).get(0);
		final Integer germplasmListId = germplasmList.getId();

		this.manager.deleteGermplasmListByListIdPhysically(germplasmListId);

		Assert.assertTrue(
			this.manager.getGermplasmListByName(TEST_LIST_1, GermplasmListManagerImplTest.PROGRAM_UUID, 0, 1, Operation.EQUAL).size()
				== 0);
	}

	@Test
	public void testGermplasmListByParentFolderId() throws Exception {
		final Integer parentFolderId = Integer.valueOf(56);
		final List<GermplasmList> children =
			this.manager.getGermplasmListByParentFolderId(parentFolderId, GermplasmListManagerImplTest.PROGRAM_UUID);
		Debug.println(IntegrationTestBase.INDENT, "testGermplasmListByParentFolderId(" + parentFolderId + "): ");
		Debug.printObjects(IntegrationTestBase.INDENT, children);
		// Verify using: select * from listnms where liststatus <> 9 and lhierarchy = 56;
	}

	@Test
	public void testCountGermplasmListByParentFolderId() throws Exception {
		final Integer parentFolderId = Integer.valueOf(56);
		Debug.println(IntegrationTestBase.INDENT, "testCountGermplasmListByParentFolderId(" + parentFolderId + "): ");
		// Verify using: select count(*) from listnms where liststatus <> 9 and lhierarchy = 56;
	}

	@Test
	public void testGetGermplasmListTypes() throws Exception {
		List<UserDefinedField> userDefinedFields = new ArrayList<UserDefinedField>();
		userDefinedFields = this.manager.getGermplasmListTypes();
		Debug.println(IntegrationTestBase.INDENT, "testGetGermplasmListTypes(): " + userDefinedFields);
	}

	@Test
	public void testGetGermplasmNameTypes() throws Exception {
		List<UserDefinedField> userDefinedFields = new ArrayList<UserDefinedField>();
		userDefinedFields = this.manager.getGermplasmNameTypes();
		Debug.println(IntegrationTestBase.INDENT, "testGetGermplasmNameTypes(): " + userDefinedFields);
	}

	@Test
	public void testGetAllTopLevelLists() throws Exception {
		final int batchSize = 1;
		final List<GermplasmList> results = this.manager.getAllTopLevelLists(GermplasmListManagerImplTest.PROGRAM_UUID);
		Assert.assertNotNull(results);
		Assert.assertTrue(!results.isEmpty());
		Debug.println(IntegrationTestBase.INDENT, "testGetAllTopLevelListsBatched(" + batchSize + "): ");
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testGetGermplasmListByParentFolderId() throws Exception {
		final List<GermplasmList> results =
			this.manager.getGermplasmListByParentFolderId(this.parentId, GermplasmListManagerImplTest.PROGRAM_UUID);
		Assert.assertNotNull(results);
		Assert.assertTrue(!results.isEmpty());
		Debug.println(IntegrationTestBase.INDENT, "testGetGermplasmListByParentFolderId(" + this.parentId + "): ");
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testGetGermplasmListByParentFolderIdBatched() throws Exception {
		final int batchSize = 1;
		final List<GermplasmList> results =
			this.manager.getGermplasmListByParentFolderIdBatched(this.parentId, GermplasmListManagerImplTest.PROGRAM_UUID, batchSize);
		Assert.assertNotNull(results);
		Assert.assertTrue(!results.isEmpty());
		Debug.println(IntegrationTestBase.INDENT, "testGetGermplasmListByParentFolderIdBatched(): ");
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testSearchGermplasmList() throws MiddlewareQueryException {
		final String q = "list";

		final List<GermplasmList> results =
			this.manager.searchForGermplasmList(q, GermplasmListManagerImplTest.PROGRAM_UUID, Operation.EQUAL);
		boolean hasMatch = false;
		for (final GermplasmList germplasmList : results) {
			if (germplasmList.getName().equals(GermplasmListManagerImplTest.OTHER_PROGRAM_LIST_NAME)) {
				hasMatch = true;
			}
		}
		Assert.assertFalse(GermplasmListManagerImplTest.OTHER_PROGRAM_LIST_NAME + " should not be found", hasMatch);
	}

	@Test
	public void testSearchGermplasmListOtherProgram() throws MiddlewareQueryException {
		final String q = GermplasmListManagerImplTest.OTHER_PROGRAM_LIST_NAME;

		final List<GermplasmList> results =
			this.manager.searchForGermplasmList(q, GermplasmListManagerImplTest.OTHER_PROGRAM_UUID, Operation.EQUAL);
		Assert.assertEquals("There should be one result found", 1, results.size());
		boolean hasMatch = false;
		for (final GermplasmList germplasmList : results) {
			if (germplasmList.getName().equals(GermplasmListManagerImplTest.OTHER_PROGRAM_LIST_NAME)) {
				hasMatch = true;
			}
		}
		Assert.assertTrue(GermplasmListManagerImplTest.OTHER_PROGRAM_LIST_NAME + " should be found", hasMatch);
	}

	@Test
	public void testSearchGermplasmListProgramAgnostic() throws MiddlewareQueryException {
		final String q = GermplasmListManagerImplTest.OTHER_PROGRAM_LIST_NAME;

		final List<GermplasmList> results = this.manager.searchForGermplasmList(q, Operation.EQUAL);
		Assert.assertEquals("There should be one result found", 1, results.size());
		boolean hasMatch = false;
		for (final GermplasmList germplasmList : results) {
			if (germplasmList.getName().equals(GermplasmListManagerImplTest.OTHER_PROGRAM_LIST_NAME)) {
				hasMatch = true;
			}
		}
		Assert.assertTrue(GermplasmListManagerImplTest.OTHER_PROGRAM_LIST_NAME + " should be found", hasMatch);
	}

	@Test
	public void testGetAdditionalColumnsForList() throws MiddlewareQueryException {
		final GermplasmListNewColumnsInfo listInfo = this.manager.getAdditionalColumnsForList(-14);
		listInfo.print(0);
	}

	@Test
	public void testGetGermplasmListDataByListIdAndLrecId() throws Exception {
		final GermplasmListData data = this.manager.getGermplasmListDataByListIdAndLrecId(this.listId, this.lrecId);
		Assert.assertNotNull("It should not be null", data);
		Assert.assertEquals("It should be equal", this.listId, data.getList().getId());
		Assert.assertEquals("It should be equal", this.lrecId, data.getId());
	}

	@Test
	public void testRetrieveSnapshotListData() throws Exception {
		final Integer listId = 1;

		final List<ListDataProject> listData = this.manager.retrieveSnapshotListData(listId);
		Assert.assertNotNull("It should not be null", listData);
	}

	@Test
	public void testRetrieveSnapshotListDataWithParents() throws Exception {
		final Integer listId = 1;

		final List<ListDataProject> listData = this.manager.retrieveSnapshotListDataWithParents(listId);
		Assert.assertNotNull("It should not be null", listData);
	}

	@Test
	public void testGetAllGermplasmListsByProgramUUID() {
		final List<GermplasmList> germplasmLists =
			this.manager.getAllGermplasmListsByProgramUUID(GermplasmListManagerImplTest.LIST_PROGRAM_UUID);

		final GermplasmList germplasmList = germplasmLists.get(0);
		Assert.assertEquals("The list name should be " + GermplasmListManagerImplTest.GERMPLASM_LIST_NAME,
			GermplasmListManagerImplTest.GERMPLASM_LIST_NAME, germplasmList.getName());
		Assert.assertEquals("The list description should be " + GermplasmListManagerImplTest.GERMPLASM_LIST_DESC,
			GermplasmListManagerImplTest.GERMPLASM_LIST_DESC, germplasmList.getDescription());
	}

	@Test
	public void testCountGermplasmListByGIDandProgramUUID() {
		final int germplasmListCount = (int) this.manager
			.countGermplasmListByGIDandProgramUUID(this.testGermplasm.getGid(), GermplasmListManagerImplTest.LIST_PROGRAM_UUID);
		Assert.assertEquals("The germplasm list count should be 1", 1, germplasmListCount);
	}

	@Test
	public void testGetGermplasmListByGIDandProgramUUID() {
		final List<GermplasmList> germplasmLists = this.manager
			.getGermplasmListByGIDandProgramUUID(this.testGermplasm.getGid(), 0, 1, GermplasmListManagerImplTest.LIST_PROGRAM_UUID);

		final GermplasmList germplasmList = germplasmLists.get(0);
		Assert.assertEquals("The list name should be " + GermplasmListManagerImplTest.GERMPLASM_LIST_NAME,
			GermplasmListManagerImplTest.GERMPLASM_LIST_NAME, germplasmList.getName());
		Assert.assertEquals("The list description should be " + GermplasmListManagerImplTest.GERMPLASM_LIST_DESC,
			GermplasmListManagerImplTest.GERMPLASM_LIST_DESC, germplasmList.getDescription());
	}

	@Test
	public void testDeleteSelecteGermplasms() {
		final int userId = 2;
		final long noOfTestEntries = 3;
		final long noOfCheckEntries = 4;
		final List<Germplasm> germplasms = this.germplasmTestDataGenerator.createGermplasmsList(10, "Germ");
		final List<Integer> gidsNews = (List<Integer>) CollectionUtils.collect(germplasms, TransformerUtils.invokerTransformer("getGid"));

		final GermplasmList list1 =
			(createGermplasmListTestData(TEST_GERMPLASM_LIST_NAME, TEST_GERMPLASM_LIST_DESC, TEST_GERMPLASM_LIST_DATE,
				TEST_GERMPLASM_LIST_TYPE_LST, TEST_GERMPLASM_LIST_USER_ID, STATUS_ACTIVE));
		this.saveGermplasmList(list1);

		for (final Germplasm result : germplasms) {
			final GermplasmListData listData1 =
				new GermplasmListData(null, list1, result.getGid(), 1, "EntryCode", "SeedSource", "Germplasm Name 5", "GroupName", 0,
					99995);
			this.manager.addGermplasmListData(listData1);
		}

		final List<ListDataProject> listDataProjects = this.createListDataProject(list1, noOfTestEntries, noOfCheckEntries);

		this.studyId = this.createNurseryTestData();

		listDataProjectSaver.saveOrUpdateListDataProject(studyId, GermplasmListType.STUDY, listId, listDataProjects, userId);

		assertThat(germplasms, is(equalTo(this.dataManager.getGermplasms(gidsNews))));

		this.manager.deleteGermplasms(gidsNews, list1.getId());
		this.sessionProvder.getSession().clear();

		final List<Germplasm> germplasmDeleted = this.dataManager.getGermplasms(gidsNews);
		assertThat(germplasmDeleted, both(is(not(empty()))).and(notNullValue()));
		assertThat(germplasmDeleted, hasItem(isDeleted(is(Boolean.TRUE))));

		final List<GermplasmListData> germplasmListDataByGID = this.manager.getGermplasmListDataByListId(list1.getId());
		for (final GermplasmListData result : germplasmListDataByGID) {

			assertThat(null, is(equalTo(result)));
		}

		final List<ListDataProject> deletedListDataProjects = this.manager.retrieveSnapshotListData(listId);
		for (final ListDataProject result : deletedListDataProjects) {

			assertThat(null, is(equalTo(result)));
		}

	}

	private List<ListDataProject> createListDataProject(
		final GermplasmList germplasmList, final long noOfTestEntries,
		final long noOfCheckEntries) {

		final List<ListDataProject> listDataProjects = new ArrayList<>();
		for (int i = 0; i < noOfCheckEntries; i++) {
			listDataProjects.add(createListDataProject(germplasmList, SystemDefinedEntryType.CHECK_ENTRY));
		}
		for (int i = 0; i < noOfTestEntries; i++) {
			listDataProjects.add(createListDataProject(germplasmList, SystemDefinedEntryType.TEST_ENTRY));
		}

		return listDataProjects;

	}

	private ListDataProject createListDataProject(final GermplasmList germplasmList, final SystemDefinedEntryType systemDefinedEntryType) {

		final ListDataProject listDataProject = new ListDataProject();
		listDataProject.setCheckType(systemDefinedEntryType.getEntryTypeCategoricalId());
		listDataProject.setSeedSource("");
		listDataProject.setList(germplasmList);
		listDataProject.setGermplasmId(1);

		return listDataProject;

	}

	private Integer saveGermplasmList(final GermplasmList list) throws MiddlewareQueryException {
		return this.manager.addGermplasmList(list);
	}

	private GermplasmList createGermplasmListTestData(
		final String name, final String description, final long date, final String type,
		final int userId, final int status) throws MiddlewareQueryException {
		final GermplasmList list = new GermplasmList();
		list.setName(name);
		list.setDescription(description);
		list.setDate(date);
		list.setType(type);
		list.setUserId(userId);
		list.setStatus(status);
		list.setProgramUUID(PROGRAM_UUID);
		return list;
	}

	private FeatureMatcher<Germplasm, Boolean> isDeleted(final Matcher<Boolean> matcher) {
		return new FeatureMatcher<Germplasm, Boolean>(matcher, "isDeleted", "isDeleted") {

			@Override
			protected Boolean featureValueOf(final Germplasm germplasm) {
				return germplasm.getDeleted();
			}
		};
	}

	@Test
	public void getCodeFixedStatusByGidList() {
		final GermplasmListManagerImpl germplasmListManager = Mockito.mock(GermplasmListManagerImpl.class);
		final GermplasmDAO germplasmDAO = Mockito.mock(GermplasmDAO.class);

		final List<Integer> gids = Arrays.asList(1, 2);
		final Germplasm gid1 = new Germplasm();
		gid1.setGid(1);
		gid1.setMgid(1);
		final Germplasm gid2 = new Germplasm();
		gid2.setGid(2);
		gid2.setMgid(0);
		final List<Germplasm> germplasms = Arrays.asList(gid1, gid2);
		Mockito.when(germplasmListManager.getGermplasmDao()).thenReturn(germplasmDAO);
		Mockito.when(germplasmDAO.getByGIDList(gids)).thenReturn(germplasms);
		Mockito.doCallRealMethod().when(germplasmListManager).getCodeFixedGidsByGidList(gids);
		final Set<Integer> result = germplasmListManager.getCodeFixedGidsByGidList(gids);
		Assert.assertEquals(1, result.size());
		Assert.assertTrue(result.contains(gid1.getGid()));
	}
}
