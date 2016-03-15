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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.domain.gms.GermplasmListNewColumnsInfo;
import org.generationcp.middleware.domain.gms.ListDataColumn;
import org.generationcp.middleware.domain.gms.ListDataInfo;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.GermplasmListMetadata;
import org.generationcp.middleware.pojos.ListDataProject;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.util.Util;
import org.generationcp.middleware.utils.test.Debug;
import org.hibernate.Query;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

/*
 * The add/update/delete tests are highly dependent on the tests before it Therefore the order of execution is important. In the future, we
 * will change this to make tests independent of each other. As a temporary solution, we will force the ordering in those methods using
 * FixMethodOrder"
 *
 * // Test using RICE database
 */
@FixMethodOrder(MethodSorters.JVM)
public class GermplasmListManagerImplTest extends IntegrationTestBase {

	@Autowired
	private GermplasmListManager manager;

	@Autowired
	private GermplasmDataManager dataManager;

	private static List<Integer> testDataIds = new ArrayList<Integer>();
	private static final Integer STATUS_DELETED = 9;
	private static final String PROGRAM_UUID = "a7433c01-4f46-4bc8-ae3a-678f0b62ac23";
	private static final String OTHER_PROGRAM_UUID = "b67d2e71-4f46-4bc8-ae3a-678f0b62ac23";
	private static final String OTHER_PROGRAM_LIST_NAME = "Other Program List";
	private static final String TEST_GERMPLASM_LIST_NAME = "Test List #1";
	private static final String TEST_GERMPLASM_LIST_DESC = "Test Parent List #1";
	private static final String GERMPLASM_LIST_NAME = "Germplasm List Name";
	private static final String GERMPLASM_LIST_DESC = "Germplasm List Description";
	private static final String LIST_PROGRAM_UUID = "1001";

	private Integer parentId;
	private Integer listId;
	private Integer lrecId;
	
	private Germplasm testGermplasm;
	@Before
	public void setUpBefore() throws Exception {

		GermplasmList germplasmListOther =
				new GermplasmList(null, OTHER_PROGRAM_LIST_NAME, Long.valueOf(20120305), "LST", Integer.valueOf(1), OTHER_PROGRAM_LIST_NAME
						+ " Desc", null, 1);
		germplasmListOther.setProgramUUID(GermplasmListManagerImplTest.OTHER_PROGRAM_UUID);
		this.manager.addGermplasmList(germplasmListOther);

		GermplasmList germplasmListParent =
				new GermplasmList(null, "Test List #1", Long.valueOf(20120305), "LST", Integer.valueOf(1), "Test Parent List #1", null, 1);
		germplasmListParent.setProgramUUID(GermplasmListManagerImplTest.PROGRAM_UUID);
		this.parentId = this.manager.addGermplasmList(germplasmListParent);
		GermplasmList germplasmList = new GermplasmList(null, "Test List #1", Long.valueOf(20120305), "LST", Integer.valueOf(1),
				"Test List #1 for GCP-92", germplasmListParent, 1);
		germplasmList.setProgramUUID(GermplasmListManagerImplTest.PROGRAM_UUID);
		this.manager.addGermplasmList(germplasmList);
		GermplasmList germplasmList1 = new GermplasmList(null, "TestList444", Long.valueOf(20120306), "LST", Integer.valueOf(1),
				"Test List #4 for GCP-92", null, 1);
		germplasmList1.setProgramUUID(GermplasmListManagerImplTest.PROGRAM_UUID);
		this.manager.addGermplasmList(germplasmList1);
		Name name = new Name(null, null, 1, 1, 1, "Name", 0, 0, 0);
		Germplasm germplasm1 = new Germplasm(null, 0, 0, 0, 0, 1, 0, 0, Util.getCurrentDateAsIntegerValue(), name);
		this.dataManager.addGermplasm(germplasm1, name);
		GermplasmListData germplasmListData = new GermplasmListData(null, germplasmList1, germplasm1.getGid(), 2, "EntryCode", "SeedSource",
				"Germplasm Name 5", "GroupName", 0, 99995);
		this.manager.addGermplasmListData(germplasmListData);

		GermplasmList germplasmList2 = new GermplasmList(null, "Test List #5", Long.valueOf(20120306), "LST", Integer.valueOf(1),
				"Test List #5 for GCP-92", null, 1);
		germplasmList2.setProgramUUID(GermplasmListManagerImplTest.PROGRAM_UUID);
		this.manager.addGermplasmList(germplasmList2);
		GermplasmListData germplasmListData1 = new GermplasmListData(null, germplasmList2, germplasm1.getGid(), 1, "EntryCode", "SeedSource",
				"Germplasm Name 5", "GroupName", 0, 99995);
		this.manager.addGermplasmListData(germplasmListData1);
		GermplasmList germplasmList3 = new GermplasmList(null, "Test List #3", Long.valueOf(20120306), "LST", Integer.valueOf(1),
				"Test List #3 for GCP-92", null, 1);
		germplasmList3.setProgramUUID(GermplasmListManagerImplTest.PROGRAM_UUID);
		this.manager.addGermplasmList(germplasmList3);
		GermplasmList germplasmList6 = new GermplasmList(null, "Test List #6", Long.valueOf(20120306), "LST", Integer.valueOf(1),
				"Test List #6 for GCP-92", null, 1);
		germplasmList6.setProgramUUID(GermplasmListManagerImplTest.PROGRAM_UUID);
		this.listId = this.manager.addGermplasmList(germplasmList6);
		GermplasmListData germplasmListData2 = new GermplasmListData(null, germplasmList6, germplasm1.getGid(), 1, "EntryCode", "SeedSource",
				"Germplasm Name 6", "GroupName", 0, 99995);
		this.manager.addGermplasmListData(germplasmListData2);
		this.lrecId = germplasmListData2.getId();
		
		GermplasmList testGermplasmList = new GermplasmList(null, GERMPLASM_LIST_NAME, Long.valueOf(20120306), "LST", Integer.valueOf(1),
				GERMPLASM_LIST_DESC, null, 1);
		testGermplasmList.setProgramUUID(LIST_PROGRAM_UUID);
		this.manager.addGermplasmList(testGermplasmList);
		Name preferredName = new Name(null, null, 1, 1, 1, "Name", 0, 0, 0);
		this.testGermplasm = new Germplasm(null, 0, 0, 0, 0, 1, 0, 0, Util.getCurrentDateAsIntegerValue(), preferredName);
		this.dataManager.addGermplasm(this.testGermplasm, preferredName);
		GermplasmListData list = new GermplasmListData(null, testGermplasmList, this.testGermplasm.getGid(), 2, "EntryCode", "SeedSource",
				"Germplasm Name 5", "GroupName", 0, 99995);
		this.manager.addGermplasmListData(list);
	}

	@Test
	public void testGetGermplasmListById() throws Exception {
		Integer id = Integer.valueOf(1);
		GermplasmList list = this.manager.getGermplasmListById(id);
		Debug.println(IntegrationTestBase.INDENT, "testGetGermplasmListById(" + id + "): " + list);
	}

	@Test
	public void testGetAllGermplasmLists() throws Exception {
		int count = (int) this.manager.countAllGermplasmLists();
		List<GermplasmList> lists = this.manager.getAllGermplasmLists(0, count, Database.CENTRAL);
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
		String name = "nursery advance list";
		List<GermplasmList> lists =
				this.manager.getGermplasmListByName(name, GermplasmListManagerImplTest.PROGRAM_UUID, 0, 5, Operation.LIKE);
		Debug.println(IntegrationTestBase.INDENT, "testGetGermplasmListByName(" + name + "): ");
		Debug.printObjects(IntegrationTestBase.INDENT, lists);
		// Verify using: select * from listnms where liststatus <> 9 and listname like '2002%';
	}

	@Test
	public void testCountGermplasmListByName() throws Exception {
		String name = "2002%";
		Debug.println(IntegrationTestBase.INDENT, "testCountGermplasmListByName(" + name + "): "
				+ this.manager.countGermplasmListByName(name, Operation.LIKE, Database.CENTRAL));
		// Verify using: select count(*) from listnms where liststatus <> 9 and listname like '2002%';
	}

	@Test
	public void testCountGermplasmListByStatus() throws Exception {
		Integer status = Integer.valueOf(1);
		Debug.println(IntegrationTestBase.INDENT, "testCountGermplasmListByStatus(status=" + status + "): "
				+ this.manager.countGermplasmListByStatus(status, Database.CENTRAL));
		// Verify using: select count(*) from listnms where liststatus <> 9 and liststatus = 1;
	}

	@Test
	public void testGetGermplasmListByGID() throws Exception {
		Integer gid = Integer.valueOf(2827287);
		List<GermplasmList> results = this.manager.getGermplasmListByGID(gid, 0, 200);

		Debug.println(IntegrationTestBase.INDENT, "testGetGermplasmListByGID(" + gid + "): ");
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testCountGermplasmListByGID() throws Exception {
		Integer gid = Integer.valueOf(2827287);
		Debug.println(IntegrationTestBase.INDENT,
				"testCountGermplasmListByGID(gid=" + gid + "): " + this.manager.countGermplasmListByGID(gid));
	}

	@Test
	public void testGetGermplasmListDataByListId() throws Exception {
		Integer listId = Integer.valueOf(28781);
		List<GermplasmListData> results = this.manager.getGermplasmListDataByListId(listId);

		Debug.println(IntegrationTestBase.INDENT, "testGetGermplasmListDataByListId(" + listId + "): ");
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testCountGermplasmListDataByListId() throws Exception {
		Integer listId = Integer.valueOf(28781);
		Debug.println(IntegrationTestBase.INDENT,
				"testCountGermplasmListDataByListId(" + listId + "): " + this.manager.countGermplasmListDataByListId(listId));
	}

	@Ignore
	@Test
	public void testGetGermplasmListDataByListIdAndGID() throws Exception {
		Integer listId = Integer.valueOf(1);
		Integer gid = Integer.valueOf(91959);
		List<GermplasmListData> results = this.manager.getGermplasmListDataByListIdAndGID(listId, gid);

		Debug.println(IntegrationTestBase.INDENT, "testGetGermplasmListDataByListIdAndGID(" + listId + ", " + gid + "): ");
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testGetGermplasmListDataByListIdAndEntryId() throws Exception {
		Integer listId = Integer.valueOf(1);
		Integer entryId = Integer.valueOf(1);
		GermplasmListData data = this.manager.getGermplasmListDataByListIdAndEntryId(listId, entryId);
		Debug.println(IntegrationTestBase.INDENT, "testGetGermplasmListDataByListIdAndEntryId(" + listId + ", " + entryId + "): " + data);
	}

	@Ignore
	@Test
	public void testGetGermplasmListDataByGID() throws Exception {
		Integer gid = Integer.valueOf(91959);
		List<GermplasmListData> results = this.manager.getGermplasmListDataByGID(gid, 0, 5);

		Debug.println(IntegrationTestBase.INDENT, "testGetGermplasmListDataByGID(" + gid + "): ");
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Ignore
	@Test
	public void testCountGermplasmListDataByGID() throws Exception {
		Integer gid = Integer.valueOf(91959);
		Debug.println(IntegrationTestBase.INDENT,
				"testCountGermplasmListDataByGID(" + gid + "): " + this.manager.countGermplasmListDataByGID(gid));
	}

	@Test
	public void testAddGermplasmList() throws Exception {
		GermplasmList germplasmList = new GermplasmList(null, "Test List #1", Long.valueOf(20120305), "LST", Integer.valueOf(1),
				"Test List #1 for GCP-92", null, 1);
		germplasmList.setProgramUUID(GermplasmListManagerImplTest.PROGRAM_UUID);
		Integer id = this.manager.addGermplasmList(germplasmList);
		Debug.println(IntegrationTestBase.INDENT,
				"testAddGermplasmList(germplasmList=" + germplasmList + "): \n  " + this.manager.getGermplasmListById(id));
		GermplasmListManagerImplTest.testDataIds.add(id);
		// No need to clean up, will need the record in subsequent tests
	}

	@Test
	public void testAddGermplasmLists() throws Exception {
		List<GermplasmList> germplasmLists = new ArrayList<GermplasmList>();
		GermplasmList germplasmList = new GermplasmList(null, "Test List #1", Long.valueOf(20120305), "LST", Integer.valueOf(1),
				"Test List #1 for GCP-92", null, 1);
		germplasmList.setProgramUUID(GermplasmListManagerImplTest.PROGRAM_UUID);
		germplasmLists.add(germplasmList);

		germplasmList = new GermplasmList(null, "Test List #2", Long.valueOf(20120305), "LST", Integer.valueOf(1),
				"Test List #2 for GCP-92", null, 1);
		germplasmList.setProgramUUID(GermplasmListManagerImplTest.PROGRAM_UUID);
		germplasmLists.add(germplasmList);

		germplasmList = new GermplasmList(null, "Test List #3", Long.valueOf(20120305), "LST", Integer.valueOf(1),
				"Test List #3 for GCP-92", null, 1);
		germplasmList.setProgramUUID(GermplasmListManagerImplTest.PROGRAM_UUID);
		germplasmLists.add(germplasmList);

		germplasmList = new GermplasmList(null, "Test List #4", Long.valueOf(20120305), "LST", Integer.valueOf(1),
				"Test List #4 for GCP-92", null, 1);
		germplasmList.setProgramUUID(GermplasmListManagerImplTest.PROGRAM_UUID);
		germplasmLists.add(germplasmList);

		List<Integer> ids = this.manager.addGermplasmList(germplasmLists);
		Debug.println(IntegrationTestBase.INDENT, "testAddGermplasmLists() GermplasmLists added: " + ids.size());
		Debug.println(IntegrationTestBase.INDENT, "testAddGermplasmLists(): ");
		for (Integer id : ids) {
			GermplasmList listAdded = this.manager.getGermplasmListById(id);
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
		List<GermplasmList> germplasmLists = new ArrayList<GermplasmList>();
		List<String> germplasmListStrings = new ArrayList<String>();

		GermplasmList germplasmList1 = this.manager.getGermplasmListByName("Test List #1", 0, 1, Operation.EQUAL, Database.LOCAL).get(0);
		germplasmListStrings.add(germplasmList1.toString());
		germplasmList1.setDescription("Test List #1 for GCP-92, UPDATE");
		germplasmLists.add(germplasmList1);

		GermplasmList parent = this.manager.getGermplasmListById(-1);
		GermplasmList germplasmList2 = this.manager.getGermplasmListByName("Test List #4", 0, 1, Operation.EQUAL, Database.LOCAL).get(0);
		germplasmListStrings.add(germplasmList2.toString());
		germplasmList2.setDescription("Test List #4 for GCP-92 UPDATE");
		germplasmList2.setParent(parent);
		germplasmLists.add(germplasmList2);

		List<Integer> updatedIds = this.manager.updateGermplasmList(germplasmLists);

		Debug.println(IntegrationTestBase.INDENT, "testUpdateGermplasmList() IDs updated: " + updatedIds);
		Debug.println(IntegrationTestBase.INDENT, "testUpdateGermplasmList(): ");
		for (int i = 0; i < updatedIds.size(); i++) {
			GermplasmList updatedGermplasmList = this.manager.getGermplasmListById(updatedIds.get(i));
			Debug.println(IntegrationTestBase.INDENT, "FROM " + germplasmListStrings.get(i));
			Debug.println(IntegrationTestBase.INDENT, "TO   " + updatedGermplasmList);

			// No need to clean up, will use the records in subsequent tests
		}

	}
	
	@Ignore
	@Test
	public void testAddGermplasmListData() throws Exception {
		GermplasmList germList = this.manager.getGermplasmListByName("Test List #1", 0, 1, Operation.EQUAL, Database.LOCAL).get(0);
		GermplasmListData germplasmListData = new GermplasmListData(null, germList, Integer.valueOf(2), 1, "EntryCode", "SeedSource",
				"Germplasm Name 3", "GroupName", 0, 99992);

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
		List<GermplasmListData> germplasmListDatas = new ArrayList<GermplasmListData>();

		GermplasmList germList = this.manager.getGermplasmListByName("Test List #4", 0, 1, Operation.EQUAL, Database.LOCAL).get(0);
		GermplasmListData germplasmListData = new GermplasmListData(null, germList, Integer.valueOf(2), /* entryId= */2, "EntryCode",
				"SeedSource", "Germplasm Name 4", "GroupName", 0, 99993);
		germplasmListDatas.add(germplasmListData);
		germplasmListData = new GermplasmListData(null, germList, Integer.valueOf(2), /* entryId= */1, "EntryCode", "SeedSource",
				"Germplasm Name 6", "GroupName", 0, 99996);
		germplasmListDatas.add(germplasmListData);

		germList = this.manager.getGermplasmListByName("Test List #1", 0, 1, Operation.EQUAL, Database.LOCAL).get(0);
		germplasmListData = new GermplasmListData(null, germList, Integer.valueOf(1), 2, "EntryCode", "SeedSource", "Germplasm Name 1",
				"GroupName", 0, 99990);
		germplasmListDatas.add(germplasmListData);

		germplasmListData = new GermplasmListData(null, germList, Integer.valueOf(1), 3, "EntryCode", "SeedSource", "Germplasm Name 2",
				"GroupName", 0, 99991);
		germplasmListDatas.add(germplasmListData);

		germList = this.manager.getGermplasmListByName("Test List #2", 0, 1, Operation.EQUAL, Database.LOCAL).get(0);
		germplasmListData = new GermplasmListData(null, germList, Integer.valueOf(3), 2, "EntryCode", "SeedSource", "Germplasm Name 5",
				"GroupName", 0, 99995);
		germplasmListDatas.add(germplasmListData);

		germList = this.manager.getGermplasmListByName("Test List #3", 0, 1, Operation.EQUAL, Database.LOCAL).get(0);
		germplasmListData = new GermplasmListData(null, germList, Integer.valueOf(4), 1, "EntryCode", "SeedSource", "Germplasm Name 7",
				"GroupName", 0, 99997);
		germplasmListDatas.add(germplasmListData);
		germplasmListData = new GermplasmListData(null, germList, Integer.valueOf(4), 2, "EntryCode", "SeedSource", "Germplasm Name 8",
				"GroupName", 0, 99998);
		germplasmListDatas.add(germplasmListData);
		germplasmListData = new GermplasmListData(null, germList, Integer.valueOf(4), 3, "EntryCode", "SeedSource", "Germplasm Name 9",
				"GroupName", 0, 99999);
		germplasmListDatas.add(germplasmListData);

		Debug.println(IntegrationTestBase.INDENT,
				"testAddGermplasmListDatas() records added: " + this.manager.addGermplasmListData(germplasmListDatas));
		Debug.println(IntegrationTestBase.INDENT, "testAddGermplasmListDatas(): ");
		Debug.printObjects(IntegrationTestBase.INDENT, germplasmListDatas);
	}
	
	@Ignore
	@Test
	public void testUpdateGermplasmListData() throws Exception {
		List<GermplasmListData> germplasmListDatas = new ArrayList<GermplasmListData>();

		// GermplasmListData prior to update
		List<String> germplasmListDataStrings = new ArrayList<String>();

		// Get germListId of GermplasmList with name Test List #1
		Integer germListId = this.manager.getGermplasmListByName("Test List #1", 0, 1, Operation.EQUAL, Database.LOCAL).get(0).getId();

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
		List<GermplasmListData> listData = new ArrayList<GermplasmListData>();
		Debug.println(IntegrationTestBase.INDENT, "Test Case #1: test deleteGermplasmListDataByListId");
		GermplasmList germplasmList = this.manager.getGermplasmListByName("Test List #1", 0, 1, Operation.EQUAL, Database.LOCAL).get(0);
		listData.addAll(this.manager.getGermplasmListDataByListId(germplasmList.getId()));
		this.manager.deleteGermplasmListDataByListId(germplasmList.getId());

		Debug.println(IntegrationTestBase.INDENT, "Test Case #2: test deleteGermplasmListDataByListIdEntryId");
		germplasmList = this.manager.getGermplasmListByName("TestList444", 0, 1, Operation.EQUAL, Database.LOCAL).get(0);
		listData.add(this.manager.getGermplasmListDataByListIdAndEntryId(germplasmList.getId(), /* entryId= */2));
		this.manager.deleteGermplasmListDataByListIdEntryId(germplasmList.getId(), 2);

		Debug.println(IntegrationTestBase.INDENT, "Test Case #3: test deleteGermplasmListData(data)");
		germplasmList = this.manager.getGermplasmListByName("Test List #5", 0, 1, Operation.EQUAL, Database.LOCAL).get(0);
		GermplasmListData data = this.manager.getGermplasmListDataByListIdAndEntryId(germplasmList.getId(), /*
																											 * entryId=
																											 */1);
		listData.add(data);
		this.manager.deleteGermplasmListData(data);

		Debug.println(IntegrationTestBase.INDENT, "Test Case #4: test deleteGermplasmListData(list of data)");
		germplasmList = this.manager.getGermplasmListByName("Test List #3", 0, 1, Operation.EQUAL, Database.LOCAL).get(0);
		List<GermplasmListData> toBeDeleted = new ArrayList<GermplasmListData>();
		toBeDeleted.addAll(this.manager.getGermplasmListDataByListId(germplasmList.getId()));
		listData.addAll(toBeDeleted);
		this.manager.deleteGermplasmListData(toBeDeleted);

		Debug.println(IntegrationTestBase.INDENT, "testDeleteGermplasmListData() records to delete: " + listData.size());
		Debug.println(IntegrationTestBase.INDENT, "testDeleteGermplasmListData() deleted records: " + listData);
		for (GermplasmListData listItem : listData) {
			Debug.println(IntegrationTestBase.INDENT, listItem);
			// check if status in database was set to deleted
			Assert.assertEquals(GermplasmListManagerImplTest.STATUS_DELETED, this.getGermplasmListDataStatus(listItem.getId()));
		}
	}
	
	@Ignore
	@Test
	public void testDeleteGermplasmList() throws Exception {
		List<GermplasmList> germplasmLists = new ArrayList<GermplasmList>();
		List<GermplasmListData> listDataList = new ArrayList<GermplasmListData>();

		Debug.println(IntegrationTestBase.INDENT, "Test Case #1: test deleteGermplasmListByListId");
		GermplasmList germplasmList = this.manager
				.getGermplasmListByName("Test List #1", GermplasmListManagerImplTest.PROGRAM_UUID, 0, 1, Operation.EQUAL).get(0);
		germplasmLists.add(germplasmList);
		listDataList.addAll(germplasmList.getListData());
		Debug.println(IntegrationTestBase.INDENT,
				"\tremoved " + this.manager.deleteGermplasmListByListId(germplasmList.getId()) + " record(s)");

		Debug.println(IntegrationTestBase.INDENT, "Test Case #2: test deleteGermplasmList(data)");
		germplasmList = this.manager
				.getGermplasmListByName("Test List #4", GermplasmListManagerImplTest.PROGRAM_UUID, 0, 1, Operation.EQUAL).get(0);
		germplasmLists.add(germplasmList);
		listDataList.addAll(germplasmList.getListData());
		Debug.println(IntegrationTestBase.INDENT, "\tremoved " + this.manager.deleteGermplasmList(germplasmList) + " record(s)");

		Debug.println(IntegrationTestBase.INDENT, "Test Case #3: test deleteGermplasmList(list of data) - with cascade delete");
		List<GermplasmList> toBeDeleted = new ArrayList<GermplasmList>();
		/*
		 * germplasmList = manager.getGermplasmListByName("Test List #2", 0, 1, Operation.EQUAL, Database.LOCAL).get(0);
		 * toBeDeleted.add(germplasmList); listDataList.addAll(germplasmList.getListData());
		 */
		germplasmList = this.manager
				.getGermplasmListByName("Test List #3", GermplasmListManagerImplTest.PROGRAM_UUID, 0, 1, Operation.EQUAL).get(0);
		toBeDeleted.add(germplasmList);
		listDataList.addAll(germplasmList.getListData());
		germplasmLists.addAll(toBeDeleted);
		Debug.println(IntegrationTestBase.INDENT, "\tremoved " + this.manager.deleteGermplasmList(toBeDeleted));

		Debug.println(IntegrationTestBase.INDENT, "testDeleteGermplasmList() records to delete: " + germplasmLists.size());
		Debug.println(IntegrationTestBase.INDENT, "testDeleteGermplasmList() deleted list records: ");
		for (GermplasmList listItem : germplasmLists) {
			Debug.println(IntegrationTestBase.INDENT, listItem);
			Assert.assertEquals(GermplasmListManagerImplTest.STATUS_DELETED, this.getGermplasmListStatus(listItem.getId()));
		}
		// checking cascade delete
		Debug.println(IntegrationTestBase.INDENT, "testDeleteGermplasmList() deleted data records: ");
		for (GermplasmListData listData : listDataList) {
			Debug.println(IntegrationTestBase.INDENT, " " + listData);
			Assert.assertEquals(GermplasmListManagerImplTest.STATUS_DELETED, this.getGermplasmListDataStatus(listData.getId()));
		}
	}

	@Test
	public void testGetTopLevelLists() throws Exception {
		int count = (int) this.manager.countAllTopLevelLists(GermplasmListManagerImplTest.PROGRAM_UUID);
		List<GermplasmList> topLevelFolders = this.manager.getAllTopLevelListsBatched(GermplasmListManagerImplTest.PROGRAM_UUID, count);
		Debug.println(IntegrationTestBase.INDENT, "testGetTopLevelLists(0, 100, Database.CENTRAL): " + count);
		Debug.printObjects(IntegrationTestBase.INDENT, topLevelFolders);
		// Verify using: select * from listnms where liststatus <> 9 and lhierarchy = null or lhierarchy = 0
	}

	@Test
	public void testCountTopLevelLists() throws Exception {
		long count = this.manager.countAllTopLevelLists(GermplasmListManagerImplTest.PROGRAM_UUID);
		Debug.println(IntegrationTestBase.INDENT, "testCountTopLevelLists(Database.CENTRAL): " + count);
		// Verify using: select count(*) from listnms where liststatus <> 9 and lhierarchy = null or lhierarchy = 0
	}

	@Test
	public void testGermplasmListByParentFolderId() throws Exception {
		Integer parentFolderId = Integer.valueOf(56);
		int count = (int) this.manager.countGermplasmListByParentFolderId(parentFolderId, GermplasmListManagerImplTest.PROGRAM_UUID);
		List<GermplasmList> children =
				this.manager.getGermplasmListByParentFolderId(parentFolderId, GermplasmListManagerImplTest.PROGRAM_UUID, 0, count);
		Debug.println(IntegrationTestBase.INDENT, "testGermplasmListByParentFolderId(" + parentFolderId + "): " + count);
		Debug.printObjects(IntegrationTestBase.INDENT, children);
		// Verify using: select * from listnms where liststatus <> 9 and lhierarchy = 56;
	}

	@Test
	public void testCountGermplasmListByParentFolderId() throws Exception {
		Integer parentFolderId = Integer.valueOf(56);
		Long result = this.manager.countGermplasmListByParentFolderId(parentFolderId, GermplasmListManagerImplTest.PROGRAM_UUID);
		Debug.println(IntegrationTestBase.INDENT, "testCountGermplasmListByParentFolderId(" + parentFolderId + "): " + result);
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
	public void testGetAllTopLevelListsBatched() throws Exception {
		int batchSize = 1;
		List<GermplasmList> results = this.manager.getAllTopLevelListsBatched(GermplasmListManagerImplTest.PROGRAM_UUID, batchSize);
		Assert.assertNotNull(results);
		Assert.assertTrue(!results.isEmpty());
		Debug.println(IntegrationTestBase.INDENT, "testGetAllTopLevelListsBatched(" + batchSize + "): ");
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testGetGermplasmListByParentFolderId() throws Exception {
		List<GermplasmList> results =
				this.manager.getGermplasmListByParentFolderId(this.parentId, GermplasmListManagerImplTest.PROGRAM_UUID, 0, 100);
		Assert.assertNotNull(results);
		Assert.assertTrue(!results.isEmpty());
		Debug.println(IntegrationTestBase.INDENT, "testGetGermplasmListByParentFolderId(" + this.parentId + "): ");
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testGetGermplasmListByParentFolderIdBatched() throws Exception {
		int batchSize = 1;
		List<GermplasmList> results =
				this.manager.getGermplasmListByParentFolderIdBatched(this.parentId, GermplasmListManagerImplTest.PROGRAM_UUID, batchSize);
		Assert.assertNotNull(results);
		Assert.assertTrue(!results.isEmpty());
		Debug.println(IntegrationTestBase.INDENT, "testGetGermplasmListByParentFolderIdBatched(): ");
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testSearchGermplasmList() throws MiddlewareQueryException {
		String q = "list";

		List<GermplasmList> results = this.manager.searchForGermplasmList(q, GermplasmListManagerImplTest.PROGRAM_UUID, Operation.EQUAL);
		boolean hasMatch = false;
		for (GermplasmList germplasmList : results) {
			if (germplasmList.getName().equals(OTHER_PROGRAM_LIST_NAME)) {
				hasMatch = true;
			}
		}
		Assert.assertFalse(OTHER_PROGRAM_LIST_NAME + " should not be found", hasMatch);
	}

	@Test
	public void testSearchGermplasmListOtherProgram() throws MiddlewareQueryException {
		String q = OTHER_PROGRAM_LIST_NAME;

		List<GermplasmList> results =
				this.manager.searchForGermplasmList(q, GermplasmListManagerImplTest.OTHER_PROGRAM_UUID, Operation.EQUAL);
		Assert.assertEquals("There should be one result found", 1, results.size());
		boolean hasMatch = false;
		for (GermplasmList germplasmList : results) {
			if (germplasmList.getName().equals(OTHER_PROGRAM_LIST_NAME)) {
				hasMatch = true;
			}
		}
		Assert.assertTrue(OTHER_PROGRAM_LIST_NAME + " should be found", hasMatch);
	}

	@Test
	public void testSearchGermplasmListProgramAgnostic() throws MiddlewareQueryException {
		String q = OTHER_PROGRAM_LIST_NAME;

		List<GermplasmList> results = this.manager.searchForGermplasmList(q, Operation.EQUAL);
		Assert.assertEquals("There should be one result found", 1, results.size());
		boolean hasMatch = false;
		for (GermplasmList germplasmList : results) {
			if (germplasmList.getName().equals(OTHER_PROGRAM_LIST_NAME)) {
				hasMatch = true;
			}
		}
		Assert.assertTrue(OTHER_PROGRAM_LIST_NAME + " should be found", hasMatch);
	}
	
	@Ignore
	@Test
	public void testSaveListDataColumns() throws MiddlewareQueryException {
		List<ListDataInfo> listDataCollection = new ArrayList<ListDataInfo>();

		List<GermplasmList> firstListData = this.manager.getAllGermplasmLists(0, 2, Database.LOCAL);

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

			List<ListDataInfo> results = this.manager.saveListDataColumns(listDataCollection);
			Debug.printObjects(IntegrationTestBase.INDENT, results);
		}
	}

	@Test
	public void testGetAdditionalColumnsForList() throws MiddlewareQueryException {
		GermplasmListNewColumnsInfo listInfo = this.manager.getAdditionalColumnsForList(-14);
		listInfo.print(0);
	}

	private Integer getGermplasmListStatus(Integer id) throws Exception {
		Session session = ((DataManager) this.manager).getCurrentSession();
		Query query = session.createSQLQuery("SELECT liststatus FROM listnms WHERE listid = " + id);
		return (Integer) query.uniqueResult();
	}

	private Integer getGermplasmListDataStatus(Integer id) throws Exception {
		Session session = ((DataManager) this.manager).getCurrentSession();
		Query query = session.createSQLQuery("SELECT lrstatus FROM listdata WHERE lrecid = " + id);
		return (Integer) query.uniqueResult();
	}

	@Test
	public void testGetGermplasmListDataByListIdAndLrecId() throws Exception {
		GermplasmListData data = this.manager.getGermplasmListDataByListIdAndLrecId(this.listId, this.lrecId);
		Assert.assertNotNull("It should not be null", data);
		Assert.assertEquals("It should be equal", this.listId, data.getList().getId());
		Assert.assertEquals("It should be equal", this.lrecId, data.getId());
	}

	@Test
	public void testRetrieveSnapshotListData() throws Exception {
		Integer listId = 1;

		List<ListDataProject> listData = this.manager.retrieveSnapshotListData(listId);
		Assert.assertNotNull("It should not be null", listData);
	}

	@Test
	public void testRetrieveSnapshotListDataWithParents() throws Exception {
		Integer listId = 1;

		List<ListDataProject> listData = this.manager.retrieveSnapshotListDataWithParents(listId);
		Assert.assertNotNull("It should not be null", listData);
	}
	
	@Test
	public void testGetAllListMetadata() {
		final Map<Integer, GermplasmListMetadata> allGermplasmListMetadata = manager.getAllGermplasmListMetadata();
		Assert.assertNotNull("getAllGermplasmListMetadata() should never return null.", allGermplasmListMetadata);
	}
	
	@Test
	public void testGetAllGermplasmListsByProgramUUID(){
		List<GermplasmList> germplasmLists = this.manager.getAllGermplasmListsByProgramUUID(LIST_PROGRAM_UUID);
		
		GermplasmList germplasmList = germplasmLists.get(0);
		Assert.assertEquals("The list name should be "+ GERMPLASM_LIST_NAME, GERMPLASM_LIST_NAME, germplasmList.getName());
		Assert.assertEquals("The list description should be "+ GERMPLASM_LIST_DESC, GERMPLASM_LIST_DESC, germplasmList.getDescription());
	}
	
	@Test
	public void testCountGermplasmListByGIDandProgramUUID(){
		Assert.assertEquals("The germplasm list count should be 1", 1, this.manager.countGermplasmListByGIDandProgramUUID(this.testGermplasm.getGid(), LIST_PROGRAM_UUID));
	}
	
	@Test
	public void testGetGermplasmListByGIDandProgramUUID(){
		List<GermplasmList> germplasmLists = this.manager.getGermplasmListByGIDandProgramUUID(this.testGermplasm.getGid(), 0, 1, LIST_PROGRAM_UUID);
		
		GermplasmList germplasmList = germplasmLists.get(0);
		Assert.assertEquals("The list name should be "+ GERMPLASM_LIST_NAME, GERMPLASM_LIST_NAME, germplasmList.getName());
		Assert.assertEquals("The list description should be "+ GERMPLASM_LIST_DESC, GERMPLASM_LIST_DESC, germplasmList.getDescription());
	}
}
