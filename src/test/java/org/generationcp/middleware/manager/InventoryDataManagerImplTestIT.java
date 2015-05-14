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
package org.generationcp.middleware.manager;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.generationcp.middleware.DataManagerIntegrationTest;
import org.generationcp.middleware.domain.gms.GermplasmListType;
import org.generationcp.middleware.domain.inventory.InventoryDetails;
import org.generationcp.middleware.domain.inventory.ListDataInventory;
import org.generationcp.middleware.domain.inventory.ListEntryLotDetails;
import org.generationcp.middleware.domain.inventory.LotDetails;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.ims.EntityType;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.ReservedInventoryKey;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.generationcp.middleware.pojos.report.LotReportRow;
import org.generationcp.middleware.pojos.report.TransactionReportRow;
import org.generationcp.middleware.service.api.InventoryService;
import org.generationcp.middleware.utils.test.Debug;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class InventoryDataManagerImplTestIT extends DataManagerIntegrationTest {

    private static final String TEST_DUPLICATE = "TEST_DUPLICATE";
	private static final String TEST_BULK_WITH = "SID1-2";
	private static final String TEST_BULK_COMPL = "Y";
	private static final int TEST_LOCATION_ID = 1;
	private static final int TEST_SCALE_ID = 2;
	private static final double TEST_AMOUNT = 1.0;
	private static final String TEST_COMMENT = "TEST COMMENT";
	private static final String LOT_ID_KEY = "lotId";
	private static final String TRN_ID_KEY = "trnId";
	private static final String LIST_DATA_PROJECT_ID_KEY = "listDataProjectId";
	private static final String DUPLICATE_KEY = "duplicate";
	private static final String BULK_WITH_KEY = "bulkWith";
	private static final String BULK_COMPL_KEY = "bulkCompl";
	private static final String LOCATION_ID_KEY = "locationId";
	private static final String SCALE_ID_KEY = "scaleId";
	private static final String AMOUNT_KEY = "amount";
	private static final String COMMENT_KEY = "comment";
	
	private static InventoryDataManager manager;
    private static InventoryService inventoryService;
    
    @BeforeClass
    public static void setUp() throws Exception {
        manager = managerFactory.getInventoryDataManager();
        inventoryService = managerFactory.getInventoryMiddlewareService();
    }

    @Test
    public void testGetLotsByEntityType() throws Exception {
        String type = EntityType.GERMPLSM.name();
        List<Lot> results = manager.getLotsByEntityType(type, 0, 5);
        Assert.assertTrue(results != null);
        Assert.assertTrue(!results.isEmpty());
        Debug.println(INDENT, "testGetLotsByEntityType(" + type + "): ");
        Debug.printObjects(INDENT, results);
    }

    @Test
    public void testCountLotsByEntityType() throws Exception {
        Debug.println(INDENT, "testCountLotsByEntityType(\"GERMPLSM\"): " + manager.countLotsByEntityType(EntityType.GERMPLSM.name()));
    }

    @Test
    public void testGetLotsByEntityTypeAndEntityId() throws Exception {
        String type = EntityType.GERMPLSM.name();
        Integer entityId = Integer.valueOf(50533);
        List<Lot> results = manager.getLotsByEntityTypeAndEntityId(type, entityId, 0, 5);
        Assert.assertTrue(results != null);
        Assert.assertTrue(!results.isEmpty());
        Debug.println(INDENT, "testGetLotsByEntityTypeAndEntityId(type=" + type + ", entityId=" + entityId + "): ");
        Debug.printObjects(INDENT, results);
    }

    @Test
    public void testCountLotsByEntityTypeAndEntityId() throws Exception {
        String type = EntityType.GERMPLSM.name();
        Integer entityId = Integer.valueOf(50533);
        Debug.println(INDENT, "testCountLotsByEntityTypeAndEntityId(type=" + type + ", entityId=" + entityId + "): "
                + manager.countLotsByEntityTypeAndEntityId(type, entityId));
    }

    @Test
    public void testGetLotsByEntityTypeAndLocationId() throws Exception {
        String type = EntityType.GERMPLSM.name();
        Integer locationId = Integer.valueOf(9001);
        List<Lot> results = manager.getLotsByEntityTypeAndLocationId(type, locationId, 0, 5);
        Assert.assertTrue(results != null);
        Assert.assertTrue(!results.isEmpty());
        Debug.println(INDENT, "testGetLotsByEntityTypeAndLocationId(type=" + type + ", locationId=" + locationId + "): ");
        Debug.printObjects(INDENT, results);
    }

    @Test
    public void testCountLotsByEntityTypeAndLocationId() throws Exception {
        String type = EntityType.GERMPLSM.name();
        Integer locationId = Integer.valueOf(9000);
        Debug.println(INDENT, "testCountLotsByEntityTypeAndLocationId(type=" + type + ", locationId=" + locationId + "): "
                + manager.countLotsByEntityTypeAndLocationId(type, locationId));
    }

    @Test
    public void testGetLotsByEntityTypeAndEntityIdAndLocationId() throws Exception {
        String type = EntityType.GERMPLSM.name();
        Integer entityId = Integer.valueOf(50533);
        Integer locationId = Integer.valueOf(9001);
        List<Lot> results = manager.getLotsByEntityTypeAndEntityIdAndLocationId(type, entityId, locationId, 0, 5);
        Assert.assertTrue(results != null);
        Assert.assertTrue(!results.isEmpty());
        Debug.println(INDENT, "testGetLotsByEntityTypeAndEntityIdAndLocationId(type=" + type + ", entityId=" + entityId + ", locationId="
                + locationId + "): ");
        Debug.printObjects(INDENT, results);
    }

    @Test
    public void testCountLotsByEntityTypeAndEntityIdAndLocationId() throws Exception {
        String type = EntityType.GERMPLSM.name();
        Integer entityId = Integer.valueOf(50533);
        Integer locationId = Integer.valueOf(9000);
        Debug.println(INDENT, "testCountLotsByEntityTypeAndEntityIdAndLocationId(type=" + type + ", entityId=" + entityId + ", locationId="
                + locationId + "): " + manager.countLotsByEntityTypeAndEntityIdAndLocationId(type, entityId, locationId));
    }

    @Test
    public void testGetActualLotBalance() throws Exception {
        Integer lotId = Integer.valueOf(1);
        Debug.println(INDENT, "testGetActualLotBalance(lotId=" + lotId + "): " + manager.getActualLotBalance(lotId));
    }

    @Test
    public void testGetAvailableLotBalance() throws Exception {
        Integer lotId = Integer.valueOf(1);
        Debug.println(INDENT, "testGetAvailableLotBalance(lotId=" + lotId + "): " + manager.getAvailableLotBalance(lotId));
    }

    @Test
    public void testAddLot() throws Exception {
        Lot lot = new Lot(null, 1, EntityType.GERMPLSM.name(), 50533, 9001, 6088, 0, 0, "sample added lot");
        manager.addLot(lot);
        assertNotNull(lot.getId());
        Debug.println(INDENT, "Added: " + lot.toString());
    }
    
    @Test
    public void testAddLots() throws Exception {
        List<Lot> lots = new ArrayList<Lot>();
        lots.add(new Lot(null, 1, EntityType.GERMPLSM.name(), 50533, 9001, 1538, 0, 0, "sample added lot 1"));
        lots.add(new Lot(null, 1, EntityType.GERMPLSM.name(), 50533, 9002, 1539, 0, 0, "sample added lot 2"));
        List<Integer> idList = manager.addLots(lots);

        assertFalse(idList.isEmpty());
        Debug.println(INDENT, "Added: ");
        Debug.printObjects(INDENT * 2, lots);
    }

    @Test
    public void testUpdateLot() throws Exception {
        // this test assumes there are existing lot records with entity type = GERMPLSM
        Lot lot = manager.getLotsByEntityType(EntityType.GERMPLSM.name(), 0, 1).get(0);
        
        Debug.println(INDENT, "BEFORE: " + lot.toString());
        
        String oldComment = lot.getComments();
        String newComment = oldComment +  " UPDATED " + (int) (Math.random()*100);
        if (newComment.length() > 255){
            newComment = newComment.substring(newComment.length() - 255);
        }
        lot.setComments(newComment);
        
        manager.updateLot(lot);
        
        assertFalse(oldComment.equals(lot.getComments()));
        Debug.println(INDENT, "AFTER: " + lot.toString());
    }

    @Test
    public void testUpdateLots() throws Exception {
        // this test assumes there are at least 2 existing lot records with entity type = GERMPLSM
        List<Lot> lots = manager.getLotsByEntityType(EntityType.GERMPLSM.name(), 0, 2);

        Debug.println(INDENT, "BEFORE: ");
        Debug.printObjects(INDENT * 2, lots);
        
        if (lots.size() == 2){
            String oldComment = lots.get(0).getComments();
            for (Lot lot : lots){
                String newComment = lot.getComments() +  " UPDATED " + (int) (Math.random()*100);
                if (newComment.length() > 255){
                    newComment = newComment.substring(newComment.length() - 255);
                }
                lot.setComments(newComment);
            }
            manager.updateLots(lots);
            Debug.println(INDENT, "AFTER: ");
            Debug.printObjects(INDENT * 2, lots);
            assertFalse(oldComment.equals(lots.get(0).getComments()));
        } else {
            Debug.println(INDENT, 
                "At least two LOT entries of type=\"GERMPLSM\" are required in this test");
        }
    }
    
    @Test
    public void testAddTransaction() throws Exception {
        Transaction transaction =  new Transaction(null, 1, manager.getLotsByEntityType(EntityType.GERMPLSM.name(), 0, 1).get(0)
                , Integer.valueOf(20140413), 1, 200d, "sample added transaction", 0, null, null, null, 100d, 1, null);
        manager.addTransaction(transaction);
        assertNotNull(transaction.getId());
        Debug.println(INDENT, "testAddTransaction() Added: " + transaction);
    }
    
    @Test
    public void testAddTransactions() throws Exception {
        // this test assumes there are existing lot records with entity type = GERMPLSM
        List<Transaction> transactions = new ArrayList<Transaction>();
        transactions.add(new Transaction(null, 1, manager.getLotsByEntityType(EntityType.GERMPLSM.name(), 0, 1).get(0)
                , Integer.valueOf(20140413), 1, 200d, "sample added transaction 1", 0, null, null, null, 100d, 1, null));
        transactions.add(new Transaction(null, 1, manager.getLotsByEntityType(EntityType.GERMPLSM.name(), 0, 1).get(0)
                , Integer.valueOf(20140518), 1, 300d, "sample added transaction 2", 0, null, null, null, 150d, 1, null));
        manager.addTransactions(transactions);
        assertNotNull(transactions.get(0).getId());
        Debug.printObjects(INDENT, transactions);
    }

    @Test
    public void testUpdateTransaction() throws Exception {
        // this test assumes that there are existing records in the transaction table

        Transaction transaction = manager.getTransactionById(Integer.valueOf(1));
        Debug.println(INDENT, "BEFORE: " + transaction.toString());
        
        // Update comment
        String oldComment = transaction.getComments();
        String newComment = oldComment +  " UPDATED " + (int) (Math.random()*100);
        if (newComment.length() > 255){
            newComment = newComment.substring(newComment.length() - 255);
        }
        transaction.setComments(newComment);
        
        // Invert status
        transaction.setStatus(transaction.getStatus() ^ 1); 

        manager.updateTransaction(transaction);
        
        assertFalse(oldComment.equals(transaction.getComments()));
        Debug.println(INDENT, "AFTER: " + transaction.toString());
    }
    
    @Test
    public void testUpdateTransactions() throws Exception {
        // Assumption: There are more than 2 transactions of lot_id = 1
        List<Transaction> transactions = manager.getAllTransactions(0, 2);
        
        if (transactions.size() == 2){
            Debug.println(INDENT, "BEFORE: ");
            Debug.printObjects(INDENT * 2, transactions);
            String oldComment = transactions.get(0).getComments();

            for (Transaction transaction : transactions){
                // Update comment
                String newComment = transaction.getComments() +  " UPDATED " + (int) (Math.random()*100);
                if (newComment.length() > 255){
                    newComment = newComment.substring(newComment.length() - 255);
                }
                transaction.setComments(newComment);
                
                // Invert status
                transaction.setStatus(transaction.getStatus() ^ 1); 
            }
            manager.updateTransactions(transactions);
            
            assertFalse(oldComment.equals(transactions.get(0).getComments()));
            Debug.println(INDENT, "AFTER: ");
            Debug.printObjects(INDENT * 2, transactions);
        } else {
            Debug.println(INDENT, 
                "At least two TRANSACTION entries are required in this test");
        }
    }


    @Test
    public void testGetTransactionsByLotId() throws Exception {
        Integer lotId = Integer.valueOf(1);
        Set<Transaction> transactions = manager.getTransactionsByLotId(lotId);
        Assert.assertTrue(transactions != null);
        Assert.assertTrue(!transactions.isEmpty());
        Debug.println(INDENT, "testGetTransactionsByLotId(" + lotId + "): ");
        Debug.printObjects(INDENT, new ArrayList<Transaction>(transactions));
    }

    @Test
    public void testGetAllReserveTransactions() throws Exception {
        List<Transaction> transactions = manager.getAllReserveTransactions(0, 5);
        Assert.assertTrue(transactions != null);
        Assert.assertTrue(!transactions.isEmpty());
        Debug.printObjects(INDENT, transactions);
    }

    @Test
    public void testCountAllReserveTransactions() throws Exception {
        Debug.println(INDENT, "countAllReserveTransactions(): " + manager.countAllReserveTransactions());
    }

    @Test
    public void testGetAllDepositTransactions() throws Exception {
        List<Transaction> transactions = manager.getAllDepositTransactions(0, 5);
        Assert.assertTrue(transactions != null);
        Assert.assertTrue(!transactions.isEmpty());
        Debug.println(INDENT, "testGetAllDepositTransactions(): ");
        Debug.printObjects(INDENT, transactions);
    }

    @Test
    public void testCountAllDepositTransactions() throws Exception {
        Debug.println(INDENT, "countAllDepositTransactions(): " + manager.countAllDepositTransactions());
    }

    @Test
    public void testGetAllReserveTransactionsByRequestor() throws Exception {
        Integer personId = Integer.valueOf(1);
        List<Transaction> transactions = manager.getAllReserveTransactionsByRequestor(personId, 0, 5);
        Assert.assertTrue(transactions != null);
        Assert.assertTrue(!transactions.isEmpty());
        Debug.println(INDENT, "testGetAllReserveTransactionsByRequestor(" + personId + "): ");
        Debug.printObjects(INDENT, transactions);
    }

    @Test
    public void testCountAllReserveTransactionsByRequestor() throws Exception {
        Integer personId = Integer.valueOf(253);
        Debug.println(INDENT, "countAllReserveTransactionsByRequestor(" + personId + "): "
                + manager.countAllReserveTransactionsByRequestor(personId));
    }

    @Test
    public void testGetAllDepositTransactionsByDonor() throws Exception {
        Integer personId = Integer.valueOf(1);
        List<Transaction> transactions = manager.getAllDepositTransactionsByDonor(personId, 0, 5);
        Assert.assertTrue(transactions != null);
        Assert.assertTrue(!transactions.isEmpty());
        Debug.println(INDENT, "testGetAllDepositTransactionsByDonor(" + personId + "): ");
        Debug.printObjects(INDENT, transactions);
    }

    @Test
    public void testCountAllDepositTransactionsByDonor() throws Exception {
        Integer personId = Integer.valueOf(1);
        Debug.println(INDENT, "CountAllDepositTransactionsByDonor(" + personId + "): " 
                + manager.countAllDepositTransactionsByDonor(personId));
    }

    @Test
    public void testGenerateReportOnAllUncommittedTransactions() throws Exception {
        Debug.println(INDENT, "Number of uncommitted transactions [countAllUncommittedTransactions()]: "
                + manager.countAllUncommittedTransactions());
        List<TransactionReportRow> report = manager.generateReportOnAllUncommittedTransactions(0, 5);
        Assert.assertTrue(report != null);
        Assert.assertTrue(!report.isEmpty());
        Debug.printObjects(INDENT, report);
    }

    @Test
    public void testGenerateReportOnAllReserveTransactions() throws Exception {
        Debug.println(INDENT, "Number of reserved transactions [countAllReserveTransactions()]: " + manager.countAllReserveTransactions());
        List<TransactionReportRow> report = manager.generateReportOnAllReserveTransactions(0, 5);
        Assert.assertTrue(report != null);
        Assert.assertTrue(!report.isEmpty());
        Debug.printObjects(INDENT, report);
    }

    @Test
    public void testGenerateReportOnAllWithdrawalTransactions() throws Exception {
        Debug.println(INDENT, "Number of withdrawal transactions [countAllWithdrawalTransactions()]: "
                + manager.countAllWithdrawalTransactions());
        List<TransactionReportRow> report = manager.generateReportOnAllWithdrawalTransactions(0, 5);
        Assert.assertTrue(report != null);
        Assert.assertTrue(!report.isEmpty());
        Debug.printObjects(INDENT, report);
    }

    @Test
    public void testGenerateReportOnAllLots() throws Exception {
        Debug.println(INDENT, "Balance Report on All Lots");
        Debug.println(INDENT, "Number of lots [countAllLots()]: " + manager.countAllLots());
        List<LotReportRow> report = manager.generateReportOnAllLots(0, 10);
        Assert.assertTrue(report != null);
        Assert.assertTrue(!report.isEmpty());
        Debug.printObjects(INDENT, report);
    }

    @Test
    public void testGenerateReportsOnDormantLots() throws Exception {
        int year = 2012;
        Debug.println(INDENT, "Balance Report on DORMANT Lots");
        List<LotReportRow> report = manager.generateReportOnDormantLots(year, 0, 10);
        Assert.assertTrue(report != null);
        Assert.assertTrue(!report.isEmpty());
        Debug.println(INDENT, "testGenerateReportsOnDormantLots(year=" + year + ") REPORT: ");
        Debug.printObjects(INDENT, report);
    }

    @Test
    public void testGenerateReportOnLotsByEntityType() throws Exception {
        String type = EntityType.GERMPLSM.name();
        Debug.println(INDENT, "Balance Report on Lots by Entity Type: " + type);
        List<LotReportRow> report = manager.generateReportOnLotsByEntityType(type, 0, 10);
        Assert.assertTrue(report != null);
        Assert.assertTrue(!report.isEmpty());
        Debug.println(INDENT, "testGenerateReportOnLotsByEntityType(" + type + ") REPORT: ");
        Debug.printObjects(INDENT, report);
    }

    @Test
    public void testGenerateReportOnLotsByEntityTypeAndEntityId() throws Exception {
        Debug.println(INDENT, "Balance Report on Lots by Entity Type and Entity ID:");
        String type = EntityType.GERMPLSM.name();
        List<Integer> entityIdList = new ArrayList<Integer>();
        entityIdList.add(50533);
        entityIdList.add(537652);

        List<LotReportRow> report = manager.generateReportOnLotsByEntityTypeAndEntityId(type, entityIdList, 0, 10);

        Assert.assertTrue(report != null);
        Assert.assertTrue(!report.isEmpty());
        Debug.println(INDENT, "testGenerateReportOnLotsByEntityTypeAndEntityId(type=" + type + ", entityId=" + entityIdList + ") REPORT: ");
        Debug.printObjects(INDENT, report);
    }

    @Test
    public void testGenerateReportOnEmptyLot() throws Exception {
        Debug.println(INDENT, "Report on empty lot");
        List<LotReportRow> report = manager.generateReportOnEmptyLots(0, 2);
        Assert.assertTrue(report != null);
        Debug.println(INDENT, "testGenerateReportOnEmptyLot() REPORT: ");
        Debug.printObjects(INDENT, report);
    }

    @Test
    public void testGenerateReportOnLotWithMinimumAmount() throws Exception {
        long minimumAmount = 700;
        Debug.println(INDENT, "Report on lot with minimum balance");
        List<LotReportRow> report = manager.generateReportOnLotsWithMinimumAmount(minimumAmount, 0, 5);
        Assert.assertTrue(report != null);
        Debug.println(INDENT, "testGenerateReportOnLotWithMinimumAmount(minimumAmount=" + minimumAmount + ") REPORT: ");
        Debug.printObjects(INDENT, report);
    }
    
    @Test
    public void testCountAllUncommittedTransactions() throws Exception {
        Debug.println(INDENT, "testCountAllUncommittedTransactions(): " + manager.countAllUncommittedTransactions());
    }
    
    @Test
    public void testCountAllWithdrawalTransactions() throws Exception {
        Debug.println(INDENT, "testCountAllWithdrawalTransactions(): " + manager.countAllWithdrawalTransactions());
    }

    @Test
    public void testCountAllLots() throws Exception {
        Debug.println(INDENT, "testCountAllLots(): " + manager.countAllLots());
    }
    
    @Test
    public void testGetAllLots() throws Exception {
        List<Lot> results = manager.getAllLots(0, Integer.MAX_VALUE);
        Assert.assertNotNull(results);
        Assert.assertTrue(!results.isEmpty());
        Debug.println(INDENT, "testGetAllLots(): ");
        Debug.printObjects(INDENT, results);
    }
    
    @Test
    public void testGetTransactionById() throws Exception {
        Integer id = 1;
        Transaction transactionid = manager.getTransactionById(id);
		Assert.assertNotNull(transactionid);
		Debug.println(INDENT, "testGetTransactionById(" + id + "): ");
		Debug.println(transactionid.toString());
    }
    
    @Test
    public void testGetInventoryDetailsByGermplasmList() throws Exception{
    	Integer listId = 1;
    	List<InventoryDetails> result = manager.getInventoryDetailsByGermplasmList(listId);
    	Debug.printObjects(INDENT, result);
    }
    
    @Test
    public void testGetLotCountsForGermplasmList() throws MiddlewareQueryException{
    	int listid = 1;
		List<GermplasmListData> listEntries = manager.getLotCountsForList(listid, 0, Integer.MAX_VALUE);
		for (GermplasmListData entry : listEntries){
			ListDataInventory inventory = entry.getInventoryInfo();
			if (inventory != null){
				System.out.println(inventory);
			}
		}
    }
    
    
    @Test
    public void testGetLotCountsForGermplasmListEntries() throws MiddlewareQueryException{
    	int listid = 1;
    	List<Integer> entryIds = new ArrayList<Integer>();
    	entryIds.add(1);
    	entryIds.add(2);
    	entryIds.add(3);
		List<GermplasmListData> listEntries = manager.getLotCountsForListEntries(listid, entryIds);
		for (GermplasmListData entry : listEntries){
			ListDataInventory inventory = entry.getInventoryInfo();
			if (inventory != null){
				System.out.println(inventory);
			}
		}
    }
    
    @Test
    public void testGetLotsForGermplasmListEntry() throws MiddlewareQueryException{
    	List<ListEntryLotDetails> lots = manager.getLotDetailsForListEntry(-543041, -507029, -88175);
    	for (ListEntryLotDetails lot : lots){
    		Debug.print(lot);
    	}
    }
    
    @Test
    public void testGetLotsForGermplasmList() throws MiddlewareQueryException{
    	List<GermplasmListData> listEntries = manager.getLotDetailsForList(-543041, 0, 500);
    	for (GermplasmListData entry : listEntries){
    		Debug.print("Id=" + entry.getId() + ", GID = " + entry.getGid());
    		Debug.print(3, entry.getInventoryInfo());
    	}
    }
    
    @Test
    public void testGetLotCountsForGermplasm() throws MiddlewareQueryException{
    	int gid = -644052;
		Integer count = manager.countLotsWithAvailableBalanceForGermplasm(gid);
    	Debug.print("GID=" + gid + ", lotCount=" + count);
    }
    
    @Test
    public void testGetLotsForGermplasm() throws MiddlewareQueryException{
    	int gid = 89;
		List<LotDetails> lots = manager.getLotDetailsForGermplasm(gid);
		for (LotDetails lot : lots){
			System.out.println(lot);
		}
    }
    
    @Test
    public void testCancelReservedInventory() throws MiddlewareQueryException{
    	int lrecId = -520659;
    	int lotId = 340597;
    	
    	List<ReservedInventoryKey> lotEntries = new ArrayList<ReservedInventoryKey>();
    	lotEntries.add(new ReservedInventoryKey(1, lrecId, lotId));
    	manager.cancelReservedInventory(lotEntries);
    }
    
    @Test
    public void testGetStockIdsByListDataProjectListId() throws MiddlewareQueryException{
    	List<String> stockIds = manager.getStockIdsByListDataProjectListId(17);
    	assertNotNull(stockIds);
    }
    
    @Test
    public void testUpdateInventory() throws MiddlewareQueryException{
    	Integer listId = 17;
		List<InventoryDetails> inventoryDetailList = 
    			inventoryService.getInventoryListByListDataProjectListId(
    					listId, GermplasmListType.CROSSES);
    	if(inventoryDetailList!=null && !inventoryDetailList.isEmpty()) {
    		InventoryDetails inventoryDetails = inventoryDetailList.get(0);
    		Map<String,Object> originalData = getInventorySpecificDetails(inventoryDetails);
    		modifyInventoryDetails(inventoryDetails);
    		manager.updateInventory(listId, inventoryDetailList);
    		InventoryDetails modifiedInventoryDetails = getModifiedInventoryDetails(originalData,
    				inventoryService.getInventoryListByListDataProjectListId(
    						listId, GermplasmListType.CROSSES));
    		assertEquals(TEST_DUPLICATE,modifiedInventoryDetails.getDuplicate());
    		assertEquals(TEST_BULK_WITH,modifiedInventoryDetails.getBulkWith());
    		assertEquals(TEST_BULK_COMPL,modifiedInventoryDetails.getBulkCompl());
    		assertEquals(TEST_LOCATION_ID,modifiedInventoryDetails.getLocationId().intValue());
    		assertEquals(TEST_SCALE_ID,modifiedInventoryDetails.getScaleId().intValue());
    		assertEquals(0,modifiedInventoryDetails.getAmount().compareTo(TEST_AMOUNT));
    		assertEquals(TEST_COMMENT,modifiedInventoryDetails.getComment());
    		revertChangesToInventoryDetails(inventoryDetails,originalData);
    		manager.updateInventory(listId, inventoryDetailList);
    	}
    }

	private InventoryDetails getModifiedInventoryDetails(
			Map<String, Object> data,
			List<InventoryDetails> inventoryDetailList) {
		if(inventoryDetailList!=null && !inventoryDetailList.isEmpty()) {
			Integer lotId = (Integer)data.get(LOT_ID_KEY);
			Integer trnId = (Integer)data.get(TRN_ID_KEY);
			Integer listDataProjectId = (Integer)data.get(LIST_DATA_PROJECT_ID_KEY);
			for (InventoryDetails inventoryDetails : inventoryDetailList) {
				if(lotId.equals(inventoryDetails.getLotId()) && 
						trnId.equals(inventoryDetails.getTrnId()) &&
						listDataProjectId.equals(inventoryDetails.getListDataProjectId())) {
					return inventoryDetails;
				}
			}
		}
		return null;
	}
	
	private void revertChangesToInventoryDetails(
			InventoryDetails inventoryDetails, Map<String, Object> originalData) {
		inventoryDetails.setDuplicate((String)originalData.get(DUPLICATE_KEY));
		inventoryDetails.setBulkWith((String)originalData.get(BULK_WITH_KEY));
		inventoryDetails.setBulkCompl((String)originalData.get(BULK_COMPL_KEY));
		inventoryDetails.setLocationId((Integer)originalData.get(LOCATION_ID_KEY));
		inventoryDetails.setScaleId((Integer)originalData.get(SCALE_ID_KEY));
		inventoryDetails.setAmount((Double)originalData.get(AMOUNT_KEY));
		inventoryDetails.setComment((String)originalData.get(COMMENT_KEY));
	}

	private void modifyInventoryDetails(InventoryDetails inventoryDetails) {
		inventoryDetails.setDuplicate(TEST_DUPLICATE);
		inventoryDetails.setBulkWith(TEST_BULK_WITH);
		inventoryDetails.setBulkCompl(TEST_BULK_COMPL);
		inventoryDetails.setLocationId(TEST_LOCATION_ID);
		inventoryDetails.setScaleId(TEST_SCALE_ID);
		inventoryDetails.setAmount(TEST_AMOUNT);
		inventoryDetails.setComment(TEST_COMMENT);
	}

	private Map<String, Object> getInventorySpecificDetails(
			InventoryDetails inventoryDetails) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put(LOT_ID_KEY, inventoryDetails.getLotId());
		data.put(TRN_ID_KEY, inventoryDetails.getTrnId());
		data.put(LIST_DATA_PROJECT_ID_KEY, inventoryDetails.getListDataProjectId());
		data.put(DUPLICATE_KEY, inventoryDetails.getDuplicate());
		data.put(BULK_WITH_KEY, inventoryDetails.getBulkWith());
		data.put(BULK_COMPL_KEY, inventoryDetails.getBulkCompl());
		data.put(LOCATION_ID_KEY, inventoryDetails.getLocationId());
		data.put(SCALE_ID_KEY, inventoryDetails.getScaleId());
		data.put(AMOUNT_KEY, inventoryDetails.getAmount());
		data.put(COMMENT_KEY, inventoryDetails.getComment());
		return data;
	}
}
