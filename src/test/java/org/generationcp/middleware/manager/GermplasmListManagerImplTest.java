/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
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

/*
 * The add/update/delete tests are highly dependent on the tests before it Therefore the order of execution is important. In the future, we
 * will change this to make tests independent of each other. As a temporary solution, we will force the ordering in those methods using
 * FixMethodOrder"
 *
 * Some Methods are ignored since the method being called inside is deprecated or the tests are already failing. To be fixed in BMS-2436
 */
@FixMethodOrder(MethodSorters.JVM)
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

	@Autowired private GermplasmListManager manager;

	@Autowired private GermplasmDataManager dataManager;

	@Autowired
	private DataImportService dataImportService;
	@Autowired
	private FieldbookService middlewareFieldbookService;

	private GermplasmTestDataGenerator germplasmTestDataGenerator;

	private static List<Integer> testDataIds = new ArrayList<Integer>();
	private static final Integer STATUS_DELETED = 9;
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
	private GermplasmTestDataInitializer germplasmTDI;
	private ListDataProject testListDataProject;
	private Integer studyId;
	private ListDataProjectDAO listDataProjectDAO;
	private Germplasm parentGermplasm;
	private DataSetupTest dataSetupTest;

	@Before
	public void setUpBefore() throws Exception {
		this.listDataProjectSaver = new ListDataProjectSaver(this.sessionProvder);
		this.listDataProjectDAO = new ListDataProjectDAO();
		this.germplasmListTDI = new GermplasmListTestDataInitializer();
		this.germplasmTDI = new GermplasmTestDataInitializer();
		this.dataSetupTest = new DataSetupTest();
		this.dataSetupTest.setDataImportService(this.dataImportService);
		this.dataSetupTest.setGermplasmListManager(this.manager);
		this.dataSetupTest.setMiddlewareFieldbookService(this.middlewareFieldbookService);
		this.testGermplasm = this.germplasmTDI.createGermplasmWithPreferredName();
		this.dataManager.addGermplasm(this.testGermplasm, this.testGermplasm.getPreferredName());

		final GermplasmList germplasmListOther = this.germplasmListTDI
			.createGermplasmList(GermplasmListManagerImplTest.OTHER_PROGRAM_LIST_NAME, Integer.valueOf(1),
				GermplasmListManagerImplTest.OTHER_PROGRAM_LIST_NAME + " Desc", null, 1, GermplasmListManagerImplTest.OTHER_PROGRAM_UUID);
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
	public void testGetGermplasmListById() throws Exception {
		final Integer id = Integer.valueOf(1);
		final GermplasmList list = this.manager.getGermplasmListById(id);
		Debug.println(IntegrationTestBase.INDENT, "testGetGermplasmListById(" + id + "): " + list);
	}

	@Test
	public void testGetAllGermplasmLists() throws Exception {
		final int count = (int) this.manager.countAllGermplasmLists();
		final List<GermplasmList> lists = this.manager.getAllGermplasmLists(0, count, Database.CENTRAL);
		Debug.println(IntegrationTestBase.INDENT, "testGetAllGermplasmLists: " + count);
		Debug.printObjects(IntegrationTestBase.INDENT, lists);
		// Verify using: select * from listnms where liststatus <> 9;
	}

	@Test
	public void testCountAllGermplasmLists() throws Exception {
		Debug.println(IntegrationTestBase.INDENT, "testCountAllGermplasmLists(): " + this.manager.countAllGermplasmLists());
		// Verify using: select count(*) from listnms where liststatus <> 9;
	}

	@Test
	public void testGetGermplasmListByName() throws Exception {
		final String name = "nursery advance list";
		final List<GermplasmList> lists =
			this.manager.getGermplasmListByName(name, GermplasmListManagerImplTest.PROGRAM_UUID, 0, 5, Operation.LIKE);
		Debug.println(IntegrationTestBase.INDENT, "testGetGermplasmListByName(" + name + "): ");
		Debug.printObjects(IntegrationTestBase.INDENT, lists);
		// Verify using: select * from listnms where liststatus <> 9 and listname like '2002%';
	}

	@Test
	public void testCountGermplasmListByName() throws Exception {
		final String name = "2002%";
		Debug.println(IntegrationTestBase.INDENT,
			"testCountGermplasmListByName(" + name + "): " + this.manager.countGermplasmListByName(name, Operation.LIKE, Database.CENTRAL));
		// Verify using: select count(*) from listnms where liststatus <> 9 and listname like '2002%';
	}

	@Test
	public void testCountGermplasmListByStatus() throws Exception {
		final Integer status = Integer.valueOf(1);
		Debug.println(IntegrationTestBase.INDENT,
			"testCountGermplasmListByStatus(status=" + status + "): " + this.manager.countGermplasmListByStatus(status, Database.CENTRAL));
		// Verify using: select count(*) from listnms where liststatus <> 9 and liststatus = 1;
	}

	@Test
	public void testGetGermplasmListByGID() throws Exception {
		final Integer gid = Integer.valueOf(2827287);
		final List<GermplasmList> results = this.manager.getGermplasmListByGID(gid, 0, 200);

		Debug.println(IntegrationTestBase.INDENT, "testGetGermplasmListByGID(" + gid + "): ");
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testCountGermplasmListByGID() throws Exception {
		final Integer gid = Integer.valueOf(2827287);
		Debug.println(IntegrationTestBase.INDENT,
			"testCountGermplasmListByGID(gid=" + gid + "): " + this.manager.countGermplasmListByGID(gid));
	}

	@Test
	public void testGetGermplasmListDataByListId() throws Exception {
		final Integer listId = Integer.valueOf(28781);
		final List<GermplasmListData> results = this.manager.getGermplasmListDataByListId(listId);

		Debug.println(IntegrationTestBase.INDENT, "testGetGermplasmListDataByListId(" + listId + "): ");
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testCountGermplasmListDataByListId() throws Exception {
		final Integer listId = Integer.valueOf(28781);
		Debug.println(IntegrationTestBase.INDENT,
			"testCountGermplasmListDataByListId(" + listId + "): " + this.manager.countGermplasmListDataByListId(listId));
	}

	@Ignore
	@Test
	public void testGetGermplasmListDataByListIdAndGID() throws Exception {
		final Integer listId = Integer.valueOf(1);
		final Integer gid = Integer.valueOf(91959);
		final List<GermplasmListData> results = this.manager.getGermplasmListDataByListIdAndGID(listId, gid);

		Debug.println(IntegrationTestBase.INDENT, "testGetGermplasmListDataByListIdAndGID(" + listId + ", " + gid + "): ");
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testGetGermplasmListDataByListIdAndEntryId() throws Exception {
		final Integer listId = Integer.valueOf(1);
		final Integer entryId = Integer.valueOf(1);
		final GermplasmListData data = this.manager.getGermplasmListDataByListIdAndEntryId(listId, entryId);
		Debug.println(IntegrationTestBase.INDENT, "testGetGermplasmListDataByListIdAndEntryId(" + listId + ", " + entryId + "): " + data);
	}

	@Ignore
	@Test
	public void testGetGermplasmListDataByGID() throws Exception {
		final Integer gid = Integer.valueOf(91959);
		final List<GermplasmListData> results = this.manager.getGermplasmListDataByGID(gid, 0, 5);

		Debug.println(IntegrationTestBase.INDENT, "testGetGermplasmListDataByGID(" + gid + "): ");
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Ignore
	@Test
	public void testCountGermplasmListDataByGID() throws Exception {
		final Integer gid = Integer.valueOf(91959);
		Debug.println(IntegrationTestBase.INDENT,
			"testCountGermplasmListDataByGID(" + gid + "): " + this.manager.countGermplasmListDataByGID(gid));
	}

	@Test
	public void testAddGermplasmList() throws Exception {
		final GermplasmList germplasmList =
			new GermplasmList(null, TEST_LIST_1, Long.valueOf(20120305), "LST", Integer.valueOf(1), "Test List #1 for GCP-92", null, 1);
		germplasmList.setProgramUUID(GermplasmListManagerImplTest.PROGRAM_UUID);
		final Integer id = this.manager.addGermplasmList(germplasmList);
		Debug.println(IntegrationTestBase.INDENT,
			"testAddGermplasmList(germplasmList=" + germplasmList + "): \n  " + this.manager.getGermplasmListById(id));
		GermplasmListManagerImplTest.testDataIds.add(id);
		// No need to clean up, will need the record in subsequent tests
	}

	@Test
	public void testAddGermplasmLists() throws Exception {
		final List<GermplasmList> germplasmLists = new ArrayList<GermplasmList>();
		GermplasmList germplasmList =
			new GermplasmList(null, TEST_LIST_1, Long.valueOf(20120305), "LST", Integer.valueOf(1), "Test List #1 for GCP-92", null, 1);
		germplasmList.setProgramUUID(GermplasmListManagerImplTest.PROGRAM_UUID);
		germplasmLists.add(germplasmList);

		germplasmList =
			new GermplasmList(null, "Test List #2", Long.valueOf(20120305), "LST", Integer.valueOf(1), "Test List #2 for GCP-92", null, 1);
		germplasmList.setProgramUUID(GermplasmListManagerImplTest.PROGRAM_UUID);
		germplasmLists.add(germplasmList);

		germplasmList =
			new GermplasmList(null, TEST_LIST_3, Long.valueOf(20120305), "LST", Integer.valueOf(1), "Test List #3 for GCP-92", null, 1);
		germplasmList.setProgramUUID(GermplasmListManagerImplTest.PROGRAM_UUID);
		germplasmLists.add(germplasmList);

		germplasmList =
			new GermplasmList(null, "Test List #4", Long.valueOf(20120305), "LST", Integer.valueOf(1), "Test List #4 for GCP-92", null, 1);
		germplasmList.setProgramUUID(GermplasmListManagerImplTest.PROGRAM_UUID);
		germplasmLists.add(germplasmList);

		final List<Integer> ids = this.manager.addGermplasmList(germplasmLists);
		Debug.println(IntegrationTestBase.INDENT, "testAddGermplasmLists() GermplasmLists added: " + ids.size());
		Debug.println(IntegrationTestBase.INDENT, "testAddGermplasmLists(): ");
		for (final Integer id : ids) {
			final GermplasmList listAdded = this.manager.getGermplasmListById(id);
			Debug.println(IntegrationTestBase.INDENT, listAdded);
			// since we are not using logical delete, cleanup of test data will now be done at the AfterClass method instead
			// delete record
			// if (!listAdded.getName().equals("Test List #4")) { // delete except for Test List #4 which is used in testUpdateGermplasmList
			// manager.deleteGermplasmList(listAdded);
			// }
		}

		GermplasmListManagerImplTest.testDataIds.addAll(ids);
	}

	@Ignore
	@Test
	public void testUpdateGermplasmList() throws Exception {
		final List<GermplasmList> germplasmLists = new ArrayList<GermplasmList>();
		final List<String> germplasmListStrings = new ArrayList<String>();

		final GermplasmList germplasmList1 = this.manager.getGermplasmListByName(TEST_LIST_1, 0, 1, Operation.EQUAL, Database.LOCAL).get(0);
		germplasmListStrings.add(germplasmList1.toString());
		germplasmList1.setDescription("Test List #1 for GCP-92, UPDATE");
		germplasmLists.add(germplasmList1);

		final GermplasmList parent = this.manager.getGermplasmListById(-1);
		final GermplasmList germplasmList2 =
			this.manager.getGermplasmListByName("Test List #4", 0, 1, Operation.EQUAL, Database.LOCAL).get(0);
		germplasmListStrings.add(germplasmList2.toString());
		germplasmList2.setDescription("Test List #4 for GCP-92 UPDATE");
		germplasmList2.setParent(parent);
		germplasmLists.add(germplasmList2);

		final List<Integer> updatedIds = this.manager.updateGermplasmList(germplasmLists);

		Debug.println(IntegrationTestBase.INDENT, "testUpdateGermplasmList() IDs updated: " + updatedIds);
		Debug.println(IntegrationTestBase.INDENT, "testUpdateGermplasmList(): ");
		for (int i = 0; i < updatedIds.size(); i++) {
			final GermplasmList updatedGermplasmList = this.manager.getGermplasmListById(updatedIds.get(i));
			Debug.println(IntegrationTestBase.INDENT, "FROM " + germplasmListStrings.get(i));
			Debug.println(IntegrationTestBase.INDENT, "TO   " + updatedGermplasmList);

			// No need to clean up, will use the records in subsequent tests
		}

	}

	@Ignore
	@Test
	public void testAddGermplasmListData() throws Exception {
		final GermplasmList germList = this.manager.getGermplasmListByName(TEST_LIST_1, 0, 1, Operation.EQUAL, Database.LOCAL).get(0);
		final GermplasmListData germplasmListData =
			new GermplasmListData(null, germList, Integer.valueOf(2), 1, "EntryCode", "SeedSource", "Germplasm Name 3", "GroupName", 0,
				99992);

		Debug.println(IntegrationTestBase.INDENT,
			"testAddGermplasmListData() records added: " + this.manager.addGermplasmListData(germplasmListData));
		Debug.println(IntegrationTestBase.INDENT, "testAddGermplasmListData(): ");
		if (germplasmListData.getId() != null) {
			Debug.println(IntegrationTestBase.INDENT, germplasmListData);
		}

	}

	@Ignore
	@Test
	public void testAddGermplasmListDatas() throws Exception {
		final List<GermplasmListData> germplasmListDatas = new ArrayList<GermplasmListData>();

		GermplasmList germList = this.manager.getGermplasmListByName("Test List #4", 0, 1, Operation.EQUAL, Database.LOCAL).get(0);
		GermplasmListData germplasmListData =
			new GermplasmListData(null, germList, Integer.valueOf(2), /* entryId= */2, "EntryCode", "SeedSource", "Germplasm Name 4",
				"GroupName", 0, 99993);
		germplasmListDatas.add(germplasmListData);
		germplasmListData =
			new GermplasmListData(null, germList, Integer.valueOf(2), /* entryId= */1, "EntryCode", "SeedSource", "Germplasm Name 6",
				"GroupName", 0, 99996);
		germplasmListDatas.add(germplasmListData);

		germList = this.manager.getGermplasmListByName(TEST_LIST_1, 0, 1, Operation.EQUAL, Database.LOCAL).get(0);
		germplasmListData =
			new GermplasmListData(null, germList, Integer.valueOf(1), 2, "EntryCode", "SeedSource", "Germplasm Name 1", "GroupName", 0,
				99990);
		germplasmListDatas.add(germplasmListData);

		germplasmListData =
			new GermplasmListData(null, germList, Integer.valueOf(1), 3, "EntryCode", "SeedSource", "Germplasm Name 2", "GroupName", 0,
				99991);
		germplasmListDatas.add(germplasmListData);

		germList = this.manager.getGermplasmListByName("Test List #2", 0, 1, Operation.EQUAL, Database.LOCAL).get(0);
		germplasmListData =
			new GermplasmListData(null, germList, Integer.valueOf(3), 2, "EntryCode", "SeedSource", "Germplasm Name 5", "GroupName", 0,
				99995);
		germplasmListDatas.add(germplasmListData);

		germList = this.manager.getGermplasmListByName(TEST_LIST_3, 0, 1, Operation.EQUAL, Database.LOCAL).get(0);
		germplasmListData =
			new GermplasmListData(null, germList, Integer.valueOf(4), 1, "EntryCode", "SeedSource", "Germplasm Name 7", "GroupName", 0,
				99997);
		germplasmListDatas.add(germplasmListData);
		germplasmListData =
			new GermplasmListData(null, germList, Integer.valueOf(4), 2, "EntryCode", "SeedSource", "Germplasm Name 8", "GroupName", 0,
				99998);
		germplasmListDatas.add(germplasmListData);
		germplasmListData =
			new GermplasmListData(null, germList, Integer.valueOf(4), 3, "EntryCode", "SeedSource", "Germplasm Name 9", "GroupName", 0,
				99999);
		germplasmListDatas.add(germplasmListData);

		Debug.println(IntegrationTestBase.INDENT,
			"testAddGermplasmListDatas() records added: " + this.manager.addGermplasmListData(germplasmListDatas));
		Debug.println(IntegrationTestBase.INDENT, "testAddGermplasmListDatas(): ");
		Debug.printObjects(IntegrationTestBase.INDENT, germplasmListDatas);
	}

	@Ignore
	@Test
	public void testUpdateGermplasmListData() throws Exception {
		final List<GermplasmListData> germplasmListDatas = new ArrayList<GermplasmListData>();

		// GermplasmListData prior to update
		final List<String> germplasmListDataStrings = new ArrayList<String>();

		// Get germListId of GermplasmList with name Test List #1
		final Integer germListId = this.manager.getGermplasmListByName(TEST_LIST_1, 0, 1, Operation.EQUAL, Database.LOCAL).get(0).getId();

		GermplasmListData germplasmListData = this.manager.getGermplasmListDataByListId(germListId).get(0);
		germplasmListDataStrings.add(germplasmListData.toString());
		germplasmListData.setDesignation("Germplasm Name 3, UPDATE");
		germplasmListDatas.add(germplasmListData);

		germplasmListData = this.manager.getGermplasmListDataByListId(germListId).get(1);
		germplasmListDataStrings.add(germplasmListData.toString());
		germplasmListData.setDesignation("Germplasm Name 4, UPDATE");
		germplasmListDatas.add(germplasmListData);

		Debug.println(IntegrationTestBase.INDENT,
			"testUpdateGermplasmListData() updated records: " + this.manager.updateGermplasmListData(germplasmListDatas));
		Debug.println(IntegrationTestBase.INDENT, "testUpdateGermplasmListData(): ");
		for (int i = 0; i < germplasmListDatas.size(); i++) {
			Debug.println(IntegrationTestBase.INDENT, "FROM " + germplasmListDataStrings.get(i));
			Debug.println(IntegrationTestBase.INDENT, "TO   " + germplasmListDatas.get(i));
		}
	}

	@Ignore
	@Test
	public void testDeleteGermplasmListData() throws Exception {
		final List<GermplasmListData> listData = new ArrayList<GermplasmListData>();
		Debug.println(IntegrationTestBase.INDENT, "Test Case #1: test deleteGermplasmListDataByListId");
		GermplasmList germplasmList = this.manager.getGermplasmListByName(TEST_LIST_1, 0, 1, Operation.EQUAL, Database.LOCAL).get(0);
		listData.addAll(this.manager.getGermplasmListDataByListId(germplasmList.getId()));
		this.manager.deleteGermplasmListDataByListId(germplasmList.getId());

		Debug.println(IntegrationTestBase.INDENT, "Test Case #2: test deleteGermplasmListDataByListIdEntryId");
		germplasmList = this.manager.getGermplasmListByName(TEST_LIST_444, 0, 1, Operation.EQUAL, Database.LOCAL).get(0);
		listData.add(this.manager.getGermplasmListDataByListIdAndEntryId(germplasmList.getId(), /* entryId= */2));
		this.manager.deleteGermplasmListDataByListIdEntryId(germplasmList.getId(), 2);

		Debug.println(IntegrationTestBase.INDENT, "Test Case #3: test deleteGermplasmListData(data)");
		germplasmList = this.manager.getGermplasmListByName(TEST_LIST_5, 0, 1, Operation.EQUAL, Database.LOCAL).get(0);
		final GermplasmListData data = this.manager.getGermplasmListDataByListIdAndEntryId(germplasmList.getId(), /*
																													 * entryId=
																													 */1);
		listData.add(data);
		this.manager.deleteGermplasmListData(data);

		Debug.println(IntegrationTestBase.INDENT, "Test Case #4: test deleteGermplasmListData(list of data)");
		germplasmList = this.manager.getGermplasmListByName(TEST_LIST_3, 0, 1, Operation.EQUAL, Database.LOCAL).get(0);
		final List<GermplasmListData> toBeDeleted = new ArrayList<GermplasmListData>();
		toBeDeleted.addAll(this.manager.getGermplasmListDataByListId(germplasmList.getId()));
		listData.addAll(toBeDeleted);
		this.manager.deleteGermplasmListData(toBeDeleted);

		Debug.println(IntegrationTestBase.INDENT, "testDeleteGermplasmListData() records to delete: " + listData.size());
		Debug.println(IntegrationTestBase.INDENT, "testDeleteGermplasmListData() deleted records: " + listData);
		for (final GermplasmListData listItem : listData) {
			Debug.println(IntegrationTestBase.INDENT, listItem);
			// check if status in database was set to deleted
			Assert.assertEquals(GermplasmListManagerImplTest.STATUS_DELETED, this.getGermplasmListDataStatus(listItem.getId()));
		}
	}

	@Ignore
	@Test
	public void testDeleteGermplasmList() throws Exception {
		final List<GermplasmList> germplasmLists = new ArrayList<GermplasmList>();
		final List<GermplasmListData> listDataList = new ArrayList<GermplasmListData>();

		Debug.println(IntegrationTestBase.INDENT, "Test Case #1: test deleteGermplasmListByListId");
		GermplasmList germplasmList =
			this.manager.getGermplasmListByName(TEST_LIST_1, GermplasmListManagerImplTest.PROGRAM_UUID, 0, 1, Operation.EQUAL).get(0);
		germplasmLists.add(germplasmList);
		listDataList.addAll(germplasmList.getListData());
		Debug.println(IntegrationTestBase.INDENT,
			"\tremoved " + this.manager.deleteGermplasmListByListId(germplasmList.getId()) + " record(s)");

		Debug.println(IntegrationTestBase.INDENT, "Test Case #2: test deleteGermplasmList(data)");
		germplasmList =
			this.manager.getGermplasmListByName("Test List #4", GermplasmListManagerImplTest.PROGRAM_UUID, 0, 1, Operation.EQUAL).get(0);
		germplasmLists.add(germplasmList);
		listDataList.addAll(germplasmList.getListData());
		Debug.println(IntegrationTestBase.INDENT, "\tremoved " + this.manager.deleteGermplasmList(germplasmList) + " record(s)");

		Debug.println(IntegrationTestBase.INDENT, "Test Case #3: test deleteGermplasmList(list of data) - with cascade delete");
		final List<GermplasmList> toBeDeleted = new ArrayList<GermplasmList>();
		/*
		 * germplasmList = manager.getGermplasmListByName("Test List #2", 0, 1, Operation.EQUAL, Database.LOCAL).get(0);
		 * toBeDeleted.add(germplasmList); listDataList.addAll(germplasmList.getListData());
		 */
		germplasmList =
			this.manager.getGermplasmListByName(TEST_LIST_3, GermplasmListManagerImplTest.PROGRAM_UUID, 0, 1, Operation.EQUAL).get(0);
		toBeDeleted.add(germplasmList);
		listDataList.addAll(germplasmList.getListData());
		germplasmLists.addAll(toBeDeleted);
		Debug.println(IntegrationTestBase.INDENT, "\tremoved " + this.manager.deleteGermplasmList(toBeDeleted));

		Debug.println(IntegrationTestBase.INDENT, "testDeleteGermplasmList() records to delete: " + germplasmLists.size());
		Debug.println(IntegrationTestBase.INDENT, "testDeleteGermplasmList() deleted list records: ");
		for (final GermplasmList listItem : germplasmLists) {
			Debug.println(IntegrationTestBase.INDENT, listItem);
			Assert.assertEquals(GermplasmListManagerImplTest.STATUS_DELETED, this.getGermplasmListStatus(listItem.getId()));
		}
		// checking cascade delete
		Debug.println(IntegrationTestBase.INDENT, "testDeleteGermplasmList() deleted data records: ");
		for (final GermplasmListData listData : listDataList) {
			Debug.println(IntegrationTestBase.INDENT, " " + listData);
			Assert.assertEquals(GermplasmListManagerImplTest.STATUS_DELETED, this.getGermplasmListDataStatus(listData.getId()));
		}
	}

	@Test
	public void testDeleteGermplasmListByListId() {
		GermplasmList germplasmList =
			this.manager.getGermplasmListByName(TEST_LIST_1, GermplasmListManagerImplTest.PROGRAM_UUID, 0, 1, Operation.EQUAL).get(0);
		Integer germplasmListId = germplasmList.getId();

		this.manager.deleteGermplasmListByListIdPhysically(germplasmListId);

		Assert.assertTrue(
			this.manager.getGermplasmListByName(TEST_LIST_1, GermplasmListManagerImplTest.PROGRAM_UUID, 0, 1, Operation.EQUAL).size() == 0);
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

	@Ignore
	@Test
	public void testSaveListDataColumns() throws MiddlewareQueryException {
		final List<ListDataInfo> listDataCollection = new ArrayList<ListDataInfo>();

		final List<GermplasmList> firstListData = this.manager.getAllGermplasmLists(0, 2, Database.LOCAL);

		if (firstListData != null && firstListData.size() == 2) {
			Integer listDataId = firstListData.get(0).getId();

			// list Data ID 1
			List<ListDataColumn> columns = new ArrayList<ListDataColumn>();
			columns.add(new ListDataColumn("Preferred Name", "IRGC65"));
			columns.add(new ListDataColumn("Germplasm Date", ""));
			columns.add(new ListDataColumn("Location1", null));
			columns.add(new ListDataColumn("Location2", "IRRI222"));
			columns.add(new ListDataColumn("Location3", "IRRI333"));
			listDataCollection.add(new ListDataInfo(listDataId, columns)); // Change the List Data ID applicable for local db

			listDataId = firstListData.get(1).getId();
			// list Data ID 2
			columns = new ArrayList<ListDataColumn>();
			columns.add(new ListDataColumn("Location1", "IRRI1"));
			columns.add(new ListDataColumn("Location2", "IRRI2"));
			columns.add(new ListDataColumn("Location3", "IRRI3"));
			listDataCollection.add(new ListDataInfo(listDataId, columns)); // Change the List Data ID applicable for local db

			final List<ListDataInfo> results = this.manager.saveListDataColumns(listDataCollection);
			Debug.printObjects(IntegrationTestBase.INDENT, results);
		}
	}

	@Test
	public void testGetAdditionalColumnsForList() throws MiddlewareQueryException {
		final GermplasmListNewColumnsInfo listInfo = this.manager.getAdditionalColumnsForList(-14);
		listInfo.print(0);
	}

	private Integer getGermplasmListStatus(final Integer id) throws Exception {
		final Session session = ((DataManager) this.manager).getCurrentSession();
		final Query query = session.createSQLQuery("SELECT liststatus FROM listnms WHERE listid = " + id);
		return (Integer) query.uniqueResult();
	}

	private Integer getGermplasmListDataStatus(final Integer id) throws Exception {
		final Session session = ((DataManager) this.manager).getCurrentSession();
		final Query query = session.createSQLQuery("SELECT lrstatus FROM listdata WHERE lrecid = " + id);
		return (Integer) query.uniqueResult();
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
	@Ignore()
	public void testDeleteOneGermplasm() {
		final Germplasm germplasm = this.germplasmTestDataGenerator.createGermplasm("Germ");
		assertThat(germplasm, is(equalTo(this.dataManager.getGermplasmByGID(germplasm.getGid()))));
		this.manager.deleteGermplasms(Arrays.asList(germplasm.getGid()), this.listId);
		final Germplasm germplasmDeleted = this.dataManager.getGermplasmByGID(germplasm.getGid());
		assertThat(germplasmDeleted, is(nullValue()));
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

		listDataProjectSaver.saveOrUpdateListDataProject(studyId, GermplasmListType.NURSERY, listId, listDataProjects, userId);

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

	private List<ListDataProject> createListDataProject(final GermplasmList germplasmList, final long noOfTestEntries,
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

	private GermplasmList createGermplasmListTestData(final String name, final String description, final long date, final String type,
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

	private FeatureMatcher<Germplasm, Boolean> isDeleted(Matcher<Boolean> matcher) {
		return new FeatureMatcher<Germplasm, Boolean>(matcher, "isDeleted", "isDeleted") {

			@Override
			protected Boolean featureValueOf(Germplasm germplasm) {
				return germplasm.getDeleted();
			}
		};
	}

	@Test
	public void getCodeFixedStatusByGidList() {
		final GermplasmListManagerImpl germplasmListManager = Mockito.mock(GermplasmListManagerImpl.class);
		final GermplasmDAO germplasmDAO = Mockito.mock(GermplasmDAO.class);

		final List<Integer> gids = Arrays.asList(1, 2);
		Germplasm gid1 = new Germplasm();
		gid1.setGid(1);
		gid1.setMgid(1);
		Germplasm gid2 = new Germplasm();
		gid2.setGid(2);
		gid2.setMgid(0);
		List<Germplasm> germplasms = Arrays.asList(gid1, gid2);
		Mockito.when(germplasmListManager.getGermplasmDao()).thenReturn(germplasmDAO);
		Mockito.when(germplasmDAO.getByGIDList(gids)).thenReturn(germplasms);
		Mockito.doCallRealMethod().when(germplasmListManager).getCodeFixedGidsByGidList(Mockito.anyList());
		Set<Integer> result = germplasmListManager.getCodeFixedGidsByGidList(gids);
		Assert.assertEquals(result.size(), 1);
		Assert.assertEquals(result.contains(gid1.getGid()), Boolean.TRUE);
	}
}
