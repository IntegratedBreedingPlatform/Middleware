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
import java.util.Set;

import org.junit.Assert;

import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.pojos.Lot;
import org.generationcp.middleware.pojos.Transaction;
import org.generationcp.middleware.pojos.report.LotReportRow;
import org.generationcp.middleware.pojos.report.TransactionReportRow;
import org.generationcp.middleware.util.Debug;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class TestInventoryDataManagerImpl{

    private static ManagerFactory factory;
    private static InventoryDataManager manager;
    

    private long startTime;

    @Rule
    public TestName name = new TestName();


    @BeforeClass
    public static void setUp() throws Exception {
        DatabaseConnectionParameters local = new DatabaseConnectionParameters("testDatabaseConfig.properties", "local");
        DatabaseConnectionParameters central = new DatabaseConnectionParameters("testDatabaseConfig.properties", "central");
        factory = new ManagerFactory(local, central);
        manager = factory.getInventoryDataManager();
    }


    @Before
    public void beforeEachTest() {
        Debug.println(0, "#####" + name.getMethodName() + " Start: ");
        startTime = System.nanoTime();
    }
    
    @After
    public void afterEachTest() {
        long elapsedTime = System.nanoTime() - startTime;
        Debug.println(0, "#####" + name.getMethodName() + ": Elapsed Time = " + elapsedTime + " ns = " + ((double) elapsedTime/1000000000) + " s");
    }

    @Test
    public void testGetLotsByEntityType() throws Exception {
        String type = "GERMPLSM";
        List<Lot> results = manager.getLotsByEntityType(type, 0, 5);
        Assert.assertTrue(results != null);
        Assert.assertTrue(!results.isEmpty());
        Debug.println(0, "testGetLotsByEntityType(" + type + ") RESULTS: ");
        for (Lot result : results)
            Debug.println(0, "  " + result);
    }

    @Test
    public void testCountLotsByEntityType() throws Exception {
        Debug.println(0, "testCountLotsByEntityType(\"GERMPLSM\") RESULTS: " + manager.countLotsByEntityType("GERMPLSM"));
    }

    @Test
    public void testGetLotsByEntityTypeAndEntityId() throws Exception {
        String type = "GERMPLSM";
        Integer entityId = Integer.valueOf(50533);
        List<Lot> results = manager.getLotsByEntityTypeAndEntityId(type, entityId, 0, 5);
        Assert.assertTrue(results != null);
        Assert.assertTrue(!results.isEmpty());
        Debug.println(0, "testGetLotsByEntityTypeAndEntityId(type=" + type + ", entityId=" + entityId + ") RESULTS: ");
        for (Lot result : results)
            Debug.println(0, "  " + result);
    }

    @Test
    public void testCountLotsByEntityTypeAndEntityId() throws Exception {
        String type = "GERMPLSM";
        Integer entityId = Integer.valueOf(50533);
        Debug.println(0, "testCountLotsByEntityTypeAndEntityId(type=" + type + ", entityId=" + entityId + ") RESULTS: "
                + manager.countLotsByEntityTypeAndEntityId(type, entityId));
    }

    @Test
    public void testGetLotsByEntityTypeAndLocationId() throws Exception {
        String type = "GERMPLSM";
        Integer locationId = Integer.valueOf(9000);
        List<Lot> results = manager.getLotsByEntityTypeAndLocationId(type, locationId, 0, 5);
        Assert.assertTrue(results != null);
        Assert.assertTrue(!results.isEmpty());
        Debug.println(0, "testGetLotsByEntityTypeAndLocationId(type=" + type + ", locationId=" + locationId + ") RESULTS: ");
        for (Lot result : results)
            Debug.println(0, "  " + result);
    }

    @Test
    public void testCountLotsByEntityTypeAndLocationId() throws Exception {
        String type = "GERMPLSM";
        Integer locationId = Integer.valueOf(9000);
        Debug.println(0, "testCountLotsByEntityTypeAndLocationId(type=" + type + ", locationId=" + locationId + ") RESULTS: "
                + manager.countLotsByEntityTypeAndLocationId(type, locationId));
    }

    @Test
    public void testGetLotsByEntityTypeAndEntityIdAndLocationId() throws Exception {
        String type = "GERMPLSM";
        Integer entityId = Integer.valueOf(50533);
        Integer locationId = Integer.valueOf(9000);
        List<Lot> results = manager.getLotsByEntityTypeAndEntityIdAndLocationId(type, entityId, locationId, 0, 5);
        Assert.assertTrue(results != null);
        Assert.assertTrue(!results.isEmpty());
        Debug.println(0, "testGetLotsByEntityTypeAndEntityIdAndLocationId(type=" + type + ", entityId=" + entityId + ", locationId="
                + locationId + ") RESULTS: ");
        for (Lot result : results)
            Debug.println(0, "  " + result);
    }

    @Test
    public void testCountLotsByEntityTypeAndEntityIdAndLocationId() throws Exception {
        String type = "GERMPLSM";
        Integer entityId = Integer.valueOf(50533);
        Integer locationId = Integer.valueOf(9000);
        Debug.println(0, "testCountLotsByEntityTypeAndEntityIdAndLocationId(type=" + type + ", entityId=" + entityId + ", locationId="
                + locationId + ") RESULTS: " + manager.countLotsByEntityTypeAndEntityIdAndLocationId(type, entityId, locationId));
    }

    @Test
    public void testGetActualLotBalance() throws Exception {
        Integer lotId = Integer.valueOf(-1);
        Debug.println(0, "testGetActualLotBalance(lotId=" + lotId + "): " + manager.getActualLotBalance(lotId));
    }

    @Test
    public void testGetAvailableLotBalance() throws Exception {
        Integer lotId = Integer.valueOf(-1);
        Debug.println(0, "testGetAvailableLotBalance(lotId=" + lotId + "): " + manager.getAvailableLotBalance(lotId));
    }

    @Test
    public void testAddLot() throws Exception {
        Lot lot = new Lot();
        lot.setComments("sample added lot");
        lot.setEntityId(Integer.valueOf(50533));
        lot.setEntityType("GERMPLSM");
        lot.setLocationId(Integer.valueOf(9001));
        lot.setScaleId(Integer.valueOf(1538));
        lot.setSource(null);
        lot.setStatus(Integer.valueOf(0));
        lot.setUserId(Integer.valueOf(1));

        Integer id = manager.addLot(lot);

        if (lot.getId() != null) {
            Debug.println(0, "testAddLot() Added: " + id + "\n\t" + lot);
        }
        
        //TODO: delete lot
    }

    @Test
    public void testAddLots() throws Exception {
        
        List<Lot> lots = new ArrayList<Lot>();
        
        Lot lot = new Lot();
        lot.setComments("sample added lot 1");
        lot.setEntityId(Integer.valueOf(50533));
        lot.setEntityType("GERMPLSM");
        lot.setLocationId(Integer.valueOf(9001));
        lot.setScaleId(Integer.valueOf(1538));
        lot.setSource(null);
        lot.setStatus(Integer.valueOf(0));
        lot.setUserId(Integer.valueOf(1));        
        lots.add(lot);

        lot = new Lot();
        lot.setComments("sample added lot 2");
        lot.setEntityId(Integer.valueOf(50533));
        lot.setEntityType("GERMPLSM");
        lot.setLocationId(Integer.valueOf(9001));
        lot.setScaleId(Integer.valueOf(1538));
        lot.setSource(null);
        lot.setStatus(Integer.valueOf(0));
        lot.setUserId(Integer.valueOf(1));
        lots.add(lot);

        List<Integer> idList = manager.addLot(lots);

        if (lot.getId() != null) {
            Debug.println(0, "testAddLot() Added: " + idList);
        }

        //TODO: delete lots
    }

    @Test
    public void testUpdateLot() throws Exception {
        // this test assumes there are existing lot records with entity type = GERMPLSM
        Lot lot = manager.getLotsByEntityType("GERMPLSM", 0, 5).get(0);
        String oldComment = lot.getComments();
        lot.setComments("update comment");
        Integer id = manager.updateLot(lot);
        Debug.println(0, "testUpdateLot() RESULTS: " + id + "\n  Old comment: " + oldComment + "\n  New comment: " + lot.getComments());
    }

    @Test
    public void testAddTransaction() throws Exception {
        // this test assumes there are existing lot records with entity type = GERMPLSM
        Transaction transaction = new Transaction();
        transaction.setComments("sample added transaction");
        transaction.setDate(Integer.valueOf(20120413));
        Lot lot = manager.getLotsByEntityType("GERMPLSM", 0, 5).get(0);
        transaction.setLot(lot);
        transaction.setPersonId(Integer.valueOf(1));
        transaction.setPreviousAmount(null);
        transaction.setQuantity(Integer.valueOf(100));
        transaction.setSourceId(null);
        transaction.setSourceRecordId(null);
        transaction.setSourceType(null);
        transaction.setStatus(Integer.valueOf(1));
        transaction.setUserId(Integer.valueOf(1));

        manager.addTransaction(transaction);

        if (transaction.getId() != null) {
            Debug.println(0, "testAddTransaction() Added: " + transaction);
        }
        //TODO Delete transactions
        
    }
    
    @Test
    public void testAddTransactions() throws Exception {
        // this test assumes there are existing lot records with entity type = GERMPLSM
        
        List<Transaction> transactions = new ArrayList<Transaction>();
        Transaction transaction = new Transaction();
        transaction.setComments("sample added transaction 1");
        transaction.setDate(Integer.valueOf(20120413));
        Lot lot = manager.getLotsByEntityType("GERMPLSM", 0, 5).get(0);
        transaction.setLot(lot);
        transaction.setPersonId(Integer.valueOf(1));
        transaction.setPreviousAmount(null);
        transaction.setQuantity(Integer.valueOf(100));
        transaction.setSourceId(null);
        transaction.setSourceRecordId(null);
        transaction.setSourceType(null);
        transaction.setStatus(Integer.valueOf(1));
        transaction.setUserId(Integer.valueOf(1));
        transactions.add(transaction);

        transaction = new Transaction();
        transaction.setComments("sample added transaction 2");
        transaction.setDate(Integer.valueOf(20120413));
        lot = manager.getLotsByEntityType("GERMPLSM", 0, 5).get(0);
        transaction.setLot(lot);
        transaction.setPersonId(Integer.valueOf(1));
        transaction.setPreviousAmount(null);
        transaction.setQuantity(Integer.valueOf(100));
        transaction.setSourceId(null);
        transaction.setSourceRecordId(null);
        transaction.setSourceType(null);
        transaction.setStatus(Integer.valueOf(1));
        transaction.setUserId(Integer.valueOf(1));
        transactions.add(transaction);

        manager.addTransaction(transactions);

        if (transactions.size() > 0 && transactions.get(0).getId() != null) {
            Debug.println(0, "testAddTransaction() Added: " + transaction);
        }
        
        //TODO Delete transactions
        
    }


    @Test
    public void testUpdateTransaction() throws Exception {
        // this test assumes that there are existing records in the transaction
        // table
        Transaction t = manager.getTransactionById(Integer.valueOf(-1));
        String oldValues = "  Old comment: " + t.getComments() + ", old status: " + t.getStatus();
        t.setComments("updated comment again");
        t.setStatus(Integer.valueOf(0));

        manager.updateTransaction(t);
        Debug.println(0, "testUpdateTransaction() RESULTS: " + "\n" + oldValues + "\n  New comment: " + t.getComments()
                + ", new status: " + t.getStatus());
    }
    

    @Test
    public void testGetTransactionsByLotId() throws Exception {
        Integer lotId = Integer.valueOf(-1);
        Set<Transaction> transactions = manager.getTransactionsByLotId(lotId);
        Assert.assertTrue(transactions != null);
        Assert.assertTrue(!transactions.isEmpty());
        Debug.println(0, "testGetTransactionsByLotId(" + lotId + ") RESULTS: ");
        for (Transaction t : transactions)
            Debug.println(0, "  " + t);
    }

    @Test
    public void testGetAllReserveTransactions() throws Exception {
        List<Transaction> transactions = manager.getAllReserveTransactions(0, 5);
        Assert.assertTrue(transactions != null);
        Assert.assertTrue(!transactions.isEmpty());
        for (Transaction t : transactions)
            Debug.println(0, "  " + t);
    }

    @Test
    public void testCountAllReserveTransactions() throws Exception {
        Debug.println(0, "CountAllReserveTransactions(): " + manager.countAllReserveTransactions());
    }

    @Test
    public void testGetAllDepositTransactions() throws Exception {
        List<Transaction> transactions = manager.getAllDepositTransactions(0, 5);
        Assert.assertTrue(transactions != null);
        Assert.assertTrue(!transactions.isEmpty());
        Debug.println(0, "testGetAllDepositTransactions() RESULTS: ");
        for (Transaction t : transactions)
            Debug.println(0, "  " + t);
    }

    @Test
    public void testCountAllDepositTransactions() throws Exception {
        Debug.println(0, "countAllDepositTransactions(): " + manager.countAllDepositTransactions());
    }

    @Test
    public void testGetAllReserveTransactionsByRequestor() throws Exception {
        Integer personId = Integer.valueOf(253);
        List<Transaction> transactions = manager.getAllReserveTransactionsByRequestor(personId, 0, 5);
        Assert.assertTrue(transactions != null);
        Assert.assertTrue(!transactions.isEmpty());
        Debug.println(0, "testGetAllReserveTransactionsByRequestor(" + personId + ") RESULTS: ");
        for (Transaction t : transactions)
            Debug.println(0, "  " + t);
    }

    @Test
    public void testCountAllReserveTransactionsByRequestor() throws Exception {
        Integer personId = Integer.valueOf(253);
        Debug.println(0, "CountAllReserveTransactionsByRequestor(" + personId + "): "
                + manager.countAllReserveTransactionsByRequestor(personId));
    }

    @Test
    public void testGetAllDepositTransactionsByDonor() throws Exception {
        Integer personId = Integer.valueOf(253);
        List<Transaction> transactions = manager.getAllDepositTransactionsByDonor(personId, 0, 5);
        Assert.assertTrue(transactions != null);
        Assert.assertTrue(!transactions.isEmpty());
        Debug.println(0, "testGetAllDepositTransactionsByDonor(" + personId + ") RESULTS: ");
        for (Transaction t : transactions)
            Debug.println(0, "  " + t);
    }

    @Test
    public void testCountAllDepositTransactionsByDonor() throws Exception {
        Integer personId = Integer.valueOf(253);
        Debug.println(0, "CountAllDepositTransactionsByDonor(" + personId + ") : " + manager.countAllDepositTransactionsByDonor(personId));
    }

    @Test
    public void testGenerateReportOnAllUncommittedTransactions() throws Exception {
        Debug.println(0, "Number of uncommitted transactions [countAllUncommittedTransactions()]: "
                + manager.countAllUncommittedTransactions());
        List<TransactionReportRow> report = manager.generateReportOnAllUncommittedTransactions(0, 5);
        Assert.assertTrue(report != null);
        Assert.assertTrue(!report.isEmpty());
        Debug.println(0, "testGenerateReportOnAllUncommittedTransactions() REPORT: ");
        for (TransactionReportRow row : report)
            Debug.println(0, "  " + row);
    }

    @Test
    public void testGenerateReportOnAllReserveTransactions() throws Exception {
        Debug.println(0, "Number of reserved transactions [countAllReserveTransactions()]: " + manager.countAllReserveTransactions());
        List<TransactionReportRow> report = manager.generateReportOnAllReserveTransactions(0, 5);
        Assert.assertTrue(report != null);
        Assert.assertTrue(!report.isEmpty());
        Debug.println(0, "testGenerateReportOnAllReserveTransactions() REPORT: ");
        for (TransactionReportRow row : report)
            Debug.println(0, "  " + row);
    }

    @Test
    public void testGenerateReportOnAllWithdrawalTransactions() throws Exception {
        Debug.println(0, "Number of withdrawal transactions [countAllWithdrawalTransactions()]: "
                + manager.countAllWithdrawalTransactions());
        List<TransactionReportRow> report = manager.generateReportOnAllWithdrawalTransactions(0, 5);
        Assert.assertTrue(report != null);
        Assert.assertTrue(!report.isEmpty());
        Debug.println(0, "testGenerateReportOnAllWithdrawalTransactions() REPORT: ");
        for (TransactionReportRow row : report)
            Debug.println(0, "  " + row);
    }

    @Test
    public void testGenerateReportOnAllLots() throws Exception {
        Debug.println(0, "Balance Report on All Lots");
        Debug.println(0, "Number of lots [countAllLots()]: " + manager.countAllLots());
        List<LotReportRow> report = manager.generateReportOnAllLots(0, 10);
        Assert.assertTrue(report != null);
        Assert.assertTrue(!report.isEmpty());
        Debug.println(0, "testGenerateReportOnAllLots() REPORT: ");
        for (LotReportRow row : report)
            Debug.println(0, "  " + row);
    }

    @Test
    public void testGenerateReportsOnDormantLots() throws Exception {
        int year = 2012;
        Debug.println(0, "Balance Report on DORMANT Lots");
        List<LotReportRow> report = manager.generateReportOnDormantLots(year, 0, 10);
        Assert.assertTrue(report != null);
        Assert.assertTrue(!report.isEmpty());
        Debug.println(0, "testGenerateReportsOnDormantLots(year=" + year + ") REPORT: ");
        for (LotReportRow row : report)
            Debug.println(0, "  " + row);
    }

    @Test
    public void testGenerateReportOnLotsByEntityType() throws Exception {
        String type = "GERMPLSM";
        Debug.println(0, "Balance Report on Lots by Entity Type: " + type);
        List<LotReportRow> report = manager.generateReportOnLotsByEntityType(type, 0, 10);
        Assert.assertTrue(report != null);
        Assert.assertTrue(!report.isEmpty());
        Debug.println(0, "testGenerateReportOnLotsByEntityType(" + type + ") REPORT: ");
        for (LotReportRow row : report)
            Debug.println(0, "  " + row);
    }

    @Test
    public void testGenerateReportOnLotsByEntityTypeAndEntityId() throws Exception {
        Debug.println(0, "Balance Report on Lots by Entity Type and Entity ID:");
        String type = "GERMPLSM";
        List<Integer> entityIdList = new ArrayList<Integer>();
        entityIdList.add(50533);
        entityIdList.add(3);

        List<LotReportRow> report = manager.generateReportOnLotsByEntityTypeAndEntityId(type, entityIdList, 0, 10);

        Assert.assertTrue(report != null);
        Assert.assertTrue(!report.isEmpty());
        Debug.println(0, "testGenerateReportOnLotsByEntityTypeAndEntityId(type=" + type + ", entityId=" + entityIdList + ") REPORT: ");
        for (LotReportRow row : report)
            Debug.println(0, "  " + row);
    }

    @Test
    public void testGenerateReportOnEmptyLot() throws Exception {
        Debug.println(0, "Report on empty lot");
        List<LotReportRow> report = manager.generateReportOnEmptyLots(0, 2);
        Assert.assertTrue(report != null);
        Debug.println(0, "testGenerateReportOnEmptyLot() REPORT: ");
        for (LotReportRow row : report)
            Debug.println(0, "  " + row);
    }

    @Test
    public void testGenerateReportOnLotWithMinimumAmount() throws Exception {
        long minimumAmount = 700;
        Debug.println(0, "Report on lot with minimum balance");
        List<LotReportRow> report = manager.generateReportOnLotsWithMinimumAmount(minimumAmount, 0, 5);
        Assert.assertTrue(report != null);
        Debug.println(0, "testGenerateReportOnLotWithMinimumAmount(minimumAmount="+minimumAmount+") REPORT: ");
        for (LotReportRow row : report)
            Debug.println(0, "  " + row);
    }
    
    @Test
    public void testCountAllUncommittedTransactions() throws Exception {
        Debug.println(0, "testCountAllUncommittedTransactions() Results: " + manager.countAllUncommittedTransactions());
    }
    
    @Test
    public void testCountAllWithdrawalTransactions() throws Exception {
        Debug.println(0, "testCountAllWithdrawalTransactions() Results: " + manager.countAllWithdrawalTransactions());
    }

    @Test
    public void testCountAllLots() throws Exception {
        Debug.println(0, "testCountAllLots() Results: " + manager.countAllLots());
    }
    
    @Test
    public void testGetAllLots() throws Exception {
        List<Lot> results = manager.getAllLots(0,50);
        Assert.assertNotNull(results);
        Assert.assertTrue(!results.isEmpty());
        Debug.println(0, "testGetAllLots() Results:");
        for (Lot result : results){
    	   Debug.println(0, result.toString());
       }
       Debug.println(0, "Number of records: " +results.size());
    }
    
    @Test
    public void testGetTransactionById() throws Exception {
        Integer id = -1;
        Transaction transactionid = manager.getTransactionById(id);
		Assert.assertNotNull(transactionid);
		Debug.println(0, "testGetTransactionById("+id+") Results:");
		Debug.println(0, transactionid.toString());
    }
    

    @AfterClass
    public static void tearDown() throws Exception {
        factory.close();
    }
}
