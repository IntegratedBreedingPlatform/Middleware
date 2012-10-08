/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/

package org.generationcp.middleware.manager.test;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestGermplasmListManagerImpl{

    private static ManagerFactory factory;
    private static GermplasmListManager manager;

    @BeforeClass
    public static void setUp() throws Exception {
        DatabaseConnectionParameters local = new DatabaseConnectionParameters("testDatabaseConfig.properties", "local");
        DatabaseConnectionParameters central = new DatabaseConnectionParameters("testDatabaseConfig.properties", "central");
        factory = new ManagerFactory(local, central);
        manager = factory.getGermplasmListManager();
    }

    @Test
    public void testGetGermplasmListById() throws Exception {
        Integer id = Integer.valueOf(1);
        GermplasmList list = manager.getGermplasmListById(id);
        System.out.println("testGetGermplasmListById(" + id + ") RESULTS:" + list);
    }

    @Test
    public void testGetAllGermplasmLists() throws Exception {
        List<GermplasmList> lists = manager.getAllGermplasmLists(0, 5, Database.CENTRAL);
        System.out.println("testGetAllGermplasmLists RESULTS: ");
        for (GermplasmList list : lists) {
            System.out.println("  " + list);
        }
    }

    @Test
    public void testCountAllGermplasmLists() throws Exception {
        System.out.println("testCountAllGermplasmLists() RESULTS: " + manager.countAllGermplasmLists());
    }

    @Test
    public void testGetGermplasmListByName() throws Exception {
        String name = "2002%";
        List<GermplasmList> lists = manager.getGermplasmListByName(name, 0, 5, Operation.LIKE, Database.CENTRAL);
        System.out.println("testGetGermplasmListByName(" + name + ") RESULTS:");
        for (GermplasmList list : lists) {
            System.out.println("  " + list);
        }
    }

    @Test
    public void testCountGermplasmListByName() throws Exception {
        String name = "2002%";
        System.out.println("testCountGermplasmListByName(" + name + ") RESULTS: " + manager.countGermplasmListByName(name, Operation.LIKE));
    }

    @Test
    public void testGetGermplasmListByStatus() throws Exception {
        Integer status = Integer.valueOf(1);
        List<GermplasmList> lists = manager.getGermplasmListByStatus(status, 0, 5, Database.CENTRAL);

        System.out.println("testGetGermplasmListByStatus(status=" + status + ") RESULTS: ");
        for (GermplasmList list : lists) {
            System.out.println("  " + list);
        }
    }

    @Test
    public void testCountGermplasmListByStatus() throws Exception {
        Integer status = Integer.valueOf(1);
        System.out.println("testCountGermplasmListByStatus(status=" + status + ") RESULTS: " + manager.countGermplasmListByStatus(status));
    }

    @Test
    public void testGetGermplasmListDataByListId() throws Exception {
        Integer listId = Integer.valueOf(1);
        List<GermplasmListData> results = manager.getGermplasmListDataByListId(listId, 0, 5);

        System.out.println("testGetGermplasmListDataByListId(" + listId + ") RESULTS: ");
        for (GermplasmListData data : results) {
            System.out.println("  " + data);
        }
    }

    @Test
    public void testCountGermplasmListDataByListId() throws Exception {
        Integer listId = Integer.valueOf(1);
        System.out.println("testCountGermplasmListDataByListId(" + listId + ") RESULTS: " + manager.countGermplasmListDataByListId(listId));
    }

    @Test
    public void testGetGermplasmListDataByListIdAndGID() throws Exception {
        Integer listId = Integer.valueOf(1);
        Integer gid = Integer.valueOf(91959);
        List<GermplasmListData> results = manager.getGermplasmListDataByListIdAndGID(listId, gid);

        System.out.println("testGetGermplasmListDataByListIdAndGID(" + listId + ", " + gid + ") RESULTS: ");
        for (GermplasmListData data : results) {
            System.out.println("  " + data);
        }
    }

    @Test
    public void testGetGermplasmListDataByListIdAndEntryId() throws Exception {
        Integer listId = Integer.valueOf(1);
        Integer entryId = Integer.valueOf(1);
        GermplasmListData data = manager.getGermplasmListDataByListIdAndEntryId(listId, entryId);
        System.out.println("testGetGermplasmListDataByListIdAndEntryId(" + listId + ", " + entryId + ") RESULTS: " + data);
    }

    @Test
    public void testGetGermplasmListDataByGID() throws Exception {
        Integer gid = Integer.valueOf(91959);
        List<GermplasmListData> results = manager.getGermplasmListDataByGID(gid, 0, 5);

        System.out.println("testGetGermplasmListDataByGID(" + gid + ") RESULTS:");
        for (GermplasmListData data : results) {
            System.out.println(data);
        }
    }

    @Test
    public void testCountGermplasmListDataByGID() throws Exception {
        Integer gid = Integer.valueOf(91959);
        System.out.println("testCountGermplasmListDataByGID(" + gid + ") RESULTS: " + manager.countGermplasmListDataByGID(gid));
    }

    @Test
    public void testAddGermplasmList() throws Exception {
        GermplasmList germplasmList = new GermplasmList(null, "Test List #1", new Long(20120305), "LST", new Integer(1),
                "Test List #1 for GCP-92", null, 1);
        Integer id = manager.addGermplasmList(germplasmList);
        System.out.println("testAddGermplasmList(germplasmList=" + germplasmList + ") RESULTS: \n  " + manager.getGermplasmListById(id));
        
        // No need to clean up, will need the record in subsequent tests
    }

    @Test
    public void testAddGermplasmLists() throws Exception {
        List<GermplasmList> germplasmLists = new ArrayList<GermplasmList>();
        GermplasmList germplasmList = new GermplasmList(null, "Test List #2", new Long(20120305), "LST", new Integer(1),
                "Test List #2 for GCP-92", null, 1);
        germplasmLists.add(germplasmList);

        germplasmList = new GermplasmList(null, "Test List #3", new Long(20120305), "LST", new Integer(1), "Test List #3 for GCP-92", null,
                1);
        germplasmLists.add(germplasmList);

        germplasmList = new GermplasmList(null, "Test List #4", new Long(20120305), "LST", new Integer(1), "Test List #4 for GCP-92", null,
                1);
        germplasmLists.add(germplasmList);

        List<Integer> ids = manager.addGermplasmList(germplasmLists);
        System.out.println("testAddGermplasmLists() GermplasmLists added: " + ids.size());
        System.out.println("testAddGermplasmLists() RESULTS: ");
        for (Integer id : ids) {
            GermplasmList listAdded = manager.getGermplasmListById(id);
            System.out.println("  " + listAdded);
            // delete record
            if (!listAdded.getName().equals("Test List #4")) { // delete except for Test List #4 which is used in testUpdateGermplasmList
                manager.deleteGermplasmList(listAdded);
            }
        }
    }

    @Test
    public void testUpdateGermplasmList() throws Exception {
        List<GermplasmList> germplasmLists = new ArrayList<GermplasmList>();
        List<String> germplasmListStrings = new ArrayList<String>();

        GermplasmList germplasmList1 = manager.getGermplasmListByName("Test List #1", 0, 1, Operation.EQUAL, Database.LOCAL).get(0);
        germplasmListStrings.add(germplasmList1.toString());
        germplasmList1.setDescription("Test List #1 for GCP-92, UPDATE");
        germplasmLists.add(germplasmList1);

        GermplasmList parent = manager.getGermplasmListById(-1);
        GermplasmList germplasmList2 = manager.getGermplasmListByName("Test List #4", 0, 1, Operation.EQUAL, Database.LOCAL).get(0);
        germplasmListStrings.add(germplasmList2.toString());
        germplasmList2.setDescription("Test List #4 for GCP-92 UPDATE");
        germplasmList2.setParent(parent);
        germplasmLists.add(germplasmList2);

        List<Integer> updatedIds = manager.updateGermplasmList(germplasmLists);

        System.out.println("testUpdateGermplasmList() IDs updated: " + updatedIds);
        System.out.println("testUpdateGermplasmList() RESULTS: ");
        for (int i = 0; i < updatedIds.size(); i++) {
            GermplasmList updatedGermplasmList = manager.getGermplasmListById(updatedIds.get(i));
            System.out.println("  FROM " + germplasmListStrings.get(i));
            System.out.println("  TO   " + updatedGermplasmList);
            
            // No need to clean up, will use the records in subsequent tests
        }

    }

    @Test
    public void testAddGermplasmListData() throws Exception {
        GermplasmList germList = manager.getGermplasmListByName("Test List #1", 0, 1, Operation.EQUAL, Database.LOCAL).get(0);
        GermplasmListData germplasmListData = new GermplasmListData(null, germList, new Integer(2), 1, "EntryCode", "SeedSource",
                "Germplasm Name 3", "GroupName", 0, 99992);

        System.out.println("testAddGermplasmListData() records added: " + manager.addGermplasmListData(germplasmListData));
        System.out.println("testAddGermplasmListData() RESULTS: ");
        if (germplasmListData.getId() != null) {
            System.out.println("  " + germplasmListData);
        }

    }

    @Test
    public void testAddGermplasmListDatas() throws Exception {
        List<GermplasmListData> germplasmListDatas = new ArrayList<GermplasmListData>();

        GermplasmList germList = manager.getGermplasmListByName("Test List #4", 0, 1, Operation.EQUAL, Database.LOCAL).get(0);
        GermplasmListData germplasmListData = new GermplasmListData(null, germList, new Integer(2), 2, "EntryCode", "SeedSource",
                "Germplasm Name 4", "GroupName", 0, 99993);
        germplasmListDatas.add(germplasmListData);

        germList = manager.getGermplasmListByName("Test List #1", 0, 1, Operation.EQUAL, Database.LOCAL).get(0);
        germplasmListData = new GermplasmListData(null, germList, new Integer(1), 2, "EntryCode", "SeedSource", "Germplasm Name 1",
                "GroupName", 0, 99990);
        germplasmListDatas.add(germplasmListData);

        germplasmListData = new GermplasmListData(null, germList, new Integer(1), 3, "EntryCode", "SeedSource", "Germplasm Name 2",
                "GroupName", 0, 99991);
        germplasmListDatas.add(germplasmListData);

        System.out.println("testAddGermplasmListDatas() records added: " + manager.addGermplasmListData(germplasmListDatas));
        System.out.println("testAddGermplasmListDatas() RESULTS: ");
        for (GermplasmListData listData : germplasmListDatas) {
            if (listData.getId() != null) {
                System.out.println("  " + listData);
            }
        }
    }

    @Test
    public void testUpdateGermplasmListData() throws Exception {
        List<GermplasmListData> germplasmListDatas = new ArrayList<GermplasmListData>();
        
        //GermplasmListData prior to update
        List<String> germplasmListDataStrings = new ArrayList<String>();
        
        // Get germListId of GermplasmList with name Test List #1
        Integer germListId = manager.getGermplasmListByName("Test List #1", 0, 1, Operation.EQUAL, Database.LOCAL).get(0).getId();
        
        GermplasmListData germplasmListData = manager.getGermplasmListDataByListId(germListId, 0, 5).get(0); 
        germplasmListDataStrings.add(germplasmListData.toString());
        germplasmListData.setDesignation("Germplasm Name 3, UPDATE");
        germplasmListDatas.add(germplasmListData);

        germplasmListData  = manager.getGermplasmListDataByListId(germListId, 0, 5).get(1); 
        germplasmListDataStrings.add(germplasmListData.toString());
        germplasmListData.setDesignation("Germplasm Name 4, UPDATE");
        germplasmListDatas.add(germplasmListData);

        System.out.println("testUpdateGermplasmListData() updated records: " + manager.updateGermplasmListData(germplasmListDatas));
        System.out.println("testUpdateGermplasmListData() RESULTS: ");
        for (int i=0; i< germplasmListDatas.size(); i++){
            System.out.println("  FROM " + germplasmListDataStrings.get(i));
            System.out.println("  TO   " + germplasmListDatas.get(i));   
        }
    }

    @Test
    public void testDeleteGermplasmListData() throws Exception {        
        List<GermplasmListData> listData = new ArrayList<GermplasmListData>();
        
        // Get GermplasmList with name Test List #4
        GermplasmList germplasmList = manager.getGermplasmListByName("Test List #1", 0, 1, Operation.EQUAL, Database.LOCAL).get(0);
        listData.addAll(manager.getGermplasmListDataByListId(germplasmList.getId(), 0, 10));
        manager.deleteGermplasmListDataByListId(germplasmList.getId());
        
        // Get GermplasmList with name Test List #4
        germplasmList = manager.getGermplasmListByName("Test List #4", 0, 1, Operation.EQUAL, Database.LOCAL).get(0);
        listData.addAll(manager.getGermplasmListDataByListId(germplasmList.getId(), 0, 10));
        manager.deleteGermplasmListDataByListId(germplasmList.getId());
        
        System.out.println("testDeleteGermplasmListData() records to delete: " + listData.size());        
        System.out.println("testDeleteGermplasmListData() deleted records: " + listData);
        for (GermplasmListData listItem : listData) {
            System.out.println("  " + listItem);
        }

    }

    @Test
    public void testDeleteGermplasmList() throws Exception {
        List<GermplasmList> germplasmLists = new ArrayList<GermplasmList>();

        GermplasmList germplasmList = manager.getGermplasmListByName("Test List #1", 0, 1, Operation.EQUAL, Database.LOCAL).get(0);
        germplasmLists.add(germplasmList);

        germplasmList = manager.getGermplasmListByName("Test List #4", 0, 1, Operation.EQUAL, Database.LOCAL).get(0);
        germplasmLists.add(germplasmList);

        System.out.println("testDeleteGermplasmList() records to delete: " + manager.deleteGermplasmList(germplasmLists));
        System.out.println("testDeleteGermplasmList() deleted records: ");
        for (GermplasmList listItem : germplasmLists) {
            System.out.println("  " + listItem);
        }
    }

    @Test
    public void testGetTopLevelLists() throws Exception {
        List<GermplasmList> topLevelFolders = manager.getAllTopLevelLists(0, 100, Database.LOCAL);
        System.out.println("testGetTopLevelLists(0, 100, Database.Local) RESULTS: " + topLevelFolders);
    }

    @Test
    public void testCountTopLevelLists() throws Exception {
        long count = manager.countAllTopLevelLists(Database.LOCAL);
        System.out.println("testCountTopLevelLists(Database.LOCAL) RESULTS: " + count);
    }

    @Test
    public void testGetGermplasmListChildren() throws Exception {
        Integer parentFolderId = Integer.valueOf(-1);
        List<GermplasmList> children = manager.getGermplasmListByParentFolderId(parentFolderId, 0, 10);
        System.out.println("testGetGermplasmListChildren(" + parentFolderId + ") RESULTS: ");
        for (GermplasmList child : children) {
            System.out.println("  " + child.getId() + ": " + child.getName());
        }
    }

    @Test
    public void testCountGermplasmListChildren() throws Exception {
        Integer parentFolderId = Integer.valueOf(-1);
        Long result = manager.countGermplasmListByParentFolderId(parentFolderId);
        System.out.println("testCountGermplasmListChildren(" + parentFolderId + ") RESULTS: " + result);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        factory.close();
    }
}
