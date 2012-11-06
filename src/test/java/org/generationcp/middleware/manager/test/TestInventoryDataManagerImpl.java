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

import junit.framework.Assert;

import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.pojos.Lot;
import org.generationcp.middleware.pojos.Transaction;
import org.generationcp.middleware.pojos.report.LotReportRow;
import org.generationcp.middleware.pojos.report.TransactionReportRow;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestInventoryDataManagerImpl{

    private static ManagerFactory factory;
    private static InventoryDataManager manager;

    @BeforeClass
    public static void setUp() throws Exception {
        DatabaseConnectionParameters local = new DatabaseConnectionParameters("testDatabaseConfig.properties", "local");
        DatabaseConnectionParameters central = new DatabaseConnectionParameters("testDatabaseConfig.properties", "central");
        factory = new ManagerFactory(local, central);
        manager = factory.getInventoryDataManager();
    }

    @Test
    public void testGetLotsByEntityType() throws Exception {
        String type = "GERMPLSM";
        List<Lot> results = manager.getLotsByEntityType(type, 0, 5);
        Assert.assertTrue(results != null);
        Assert.assertTrue(!results.isEmpty());
        System.out.println("testGetLotsByEntityType(" + type + ") RESULTS: ");
        for (Lot result : results)
            System.out.println("  " + result);
    }

    @Test
    public void testCountLotsByEntityType() throws Exception {
        System.out.println("testCountLotsByEntityType(\"GERMPLSM\") RESULTS: " + manager.countLotsByEntityType("GERMPLSM"));
    }

    @Test
    public void testGetLotsByEntityTypeAndEntityId() throws Exception {
        String type = "GERMPLSM";
        Integer entityId = new Integer(50533);
        List<Lot> results = manager.getLotsByEntityTypeAndEntityId(type, entityId, 0, 5);
        Assert.assertTrue(results != null);
        Assert.assertTrue(!results.isEmpty());
        System.out.println("testGetLotsByEntityTypeAndEntityId(type=" + type + ", entityId=" + entityId + ") RESULTS: ");
        for (Lot result : results)
            System.out.println("  " + result);
    }

    @Test
    public void testCountLotsByEntityTypeAndEntityId() throws Exception {
        String type = "GERMPLSM";
        Integer entityId = new Integer(50533);
        System.out.println("testCountLotsByEntityTypeAndEntityId(type=" + type + ", entityId=" + entityId + ") RESULTS: "
                + manager.countLotsByEntityTypeAndEntityId(type, entityId));
    }

    @Test
    public void testGetLotsByEntityTypeAndLocationId() throws Exception {
        String type = "GERMPLSM";
        Integer locationId = new Integer(9000);
        List<Lot> results = manager.getLotsByEntityTypeAndLocationId(type, locationId, 0, 5);
        Assert.assertTrue(results != null);
        Assert.assertTrue(!results.isEmpty());
        System.out.println("testGetLotsByEntityTypeAndLocationId(type=" + type + ", locationId=" + locationId + ") RESULTS: ");
        for (Lot result : results)
            System.out.println("  " + result);
    }

    @Test
    public void testCountLotsByEntityTypeAndLocationId() throws Exception {
        String type = "GERMPLSM";
        Integer locationId = new Integer(9000);
        System.out.println("testCountLotsByEntityTypeAndLocationId(type=" + type + ", locationId=" + locationId + ") RESULTS: "
                + manager.countLotsByEntityTypeAndLocationId(type, locationId));
    }

    @Test
    public void testGetLotsByEntityTypeAndEntityIdAndLocationId() throws Exception {
        String type = "GERMPLSM";
        Integer entityId = new Integer(50533);
        Integer locationId = new Integer(9000);
        List<Lot> results = manager.getLotsByEntityTypeAndEntityIdAndLocationId(type, entityId, locationId, 0, 5);
        Assert.assertTrue(results != null);
        Assert.assertTrue(!results.isEmpty());
        System.out.println("testGetLotsByEntityTypeAndEntityIdAndLocationId(type=" + type + ", entityId=" + entityId + ", locationId="
                + locationId + ") RESULTS: ");
        for (Lot result : results)
            System.out.println("  " + result);
    }

    @Test
    public void testCountLotsByEntityTypeAndEntityIdAndLocationId() throws Exception {
        String type = "GERMPLSM";
        Integer entityId = new Integer(50533);
        Integer locationId = new Integer(9000);
        System.out.println("testCountLotsByEntityTypeAndEntityIdAndLocationId(type=" + type + ", entityId=" + entityId + ", locationId="
                + locationId + ") RESULTS: " + manager.countLotsByEntityTypeAndEntityIdAndLocationId(type, entityId, locationId));
    }

    @Test
    public void testGetActualLotBalance() throws Exception {
        Integer lotId = Integer.valueOf(-1);
        System.out.println("testGetActualLotBalance(lotId=" + lotId + "): " + manager.getActualLotBalance(lotId));
    }

    @Test
    public void testGetAvailableLotBalance() throws Exception {
        Integer lotId = Integer.valueOf(-1);
        System.out.println("testGetAvailableLotBalance(lotId=" + lotId + "): " + manager.getAvailableLotBalance(lotId));
    }

    @Test
    public void testAddLot() throws Exception {
        Lot lot = new Lot();
        lot.setComments("sample added lot");
        lot.setEntityId(new Integer(50533));
        lot.setEntityType("GERMPLSM");
        lot.setLocationId(new Integer(9001));
        lot.setScaleId(new Integer(1538));
        lot.setSource(null);
        lot.setStatus(new Integer(0));
        lot.setUserId(new Integer(1));

        Integer id = manager.addLot(lot);

        if (lot.getId() != null) {
            System.out.println("testAddLot() Added: " + id + "\n\t" + lot);
        }
        
        //TODO: delete lot
    }

    @Test
    public void testAddLots() throws Exception {
        
        List<Lot> lots = new ArrayList<Lot>();
        
        Lot lot = new Lot();
        lot.setComments("sample added lot 1");
        lot.setEntityId(new Integer(50533));
        lot.setEntityType("GERMPLSM");
        lot.setLocationId(new Integer(9001));
        lot.setScaleId(new Integer(1538));
        lot.setSource(null);
        lot.setStatus(new Integer(0));
        lot.setUserId(new Integer(1));        
        lots.add(lot);

        lot = new Lot();
        lot.setComments("sample added lot 2");
        lot.setEntityId(new Integer(50533));
        lot.setEntityType("GERMPLSM");
        lot.setLocationId(new Integer(9001));
        lot.setScaleId(new Integer(1538));
        lot.setSource(null);
        lot.setStatus(new Integer(0));
        lot.setUserId(new Integer(1));
        lots.add(lot);

        List<Integer> idList = manager.addLot(lots);

        if (lot.getId() != null) {
            System.out.println("testAddLot() Added: " + lot);
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
        System.out.println("testUpdateLot() RESULTS: " + id + "\n  Old comment: " + oldComment + "\n  New comment: " + lot.getComments());
    }

    @Test
    public void testAddTransaction() throws Exception {
        // this test assumes there are existing lot records with entity type = GERMPLSM
        Transaction transaction = new Transaction();
        transaction.setComments("sample added transaction");
        transaction.setDate(new Integer(20120413));
        Lot lot = manager.getLotsByEntityType("GERMPLSM", 0, 5).get(0);
        transaction.setLot(lot);
        transaction.setPersonId(new Integer(1));
        transaction.setPreviousAmount(null);
        transaction.setQuantity(new Integer(100));
        transaction.setSourceId(null);
        transaction.setSourceRecordId(null);
        transaction.setSourceType(null);
        transaction.setStatus(new Integer(1));
        transaction.setUserId(new Integer(1));

        manager.addTransaction(transaction);

        if (transaction.getId() != null) {
            System.out.println("testAddTransaction() Added: " + transaction);
        }
        //TODO Delete transactions
        
    }
    
    @Test
    public void testAddTransactions() throws Exception {
        // this test assumes there are existing lot records with entity type = GERMPLSM
        
        List<Transaction> transactions = new ArrayList<Transaction>();
        Transaction transaction = new Transaction();
        transaction.setComments("sample added transaction 1");
        transaction.setDate(new Integer(20120413));
        Lot lot = manager.getLotsByEntityType("GERMPLSM", 0, 5).get(0);
        transaction.setLot(lot);
        transaction.setPersonId(new Integer(1));
        transaction.setPreviousAmount(null);
        transaction.setQuantity(new Integer(100));
        transaction.setSourceId(null);
        transaction.setSourceRecordId(null);
        transaction.setSourceType(null);
        transaction.setStatus(new Integer(1));
        transaction.setUserId(new Integer(1));
        transactions.add(transaction);

        transaction = new Transaction();
        transaction.setComments("sample added transaction 2");
        transaction.setDate(new Integer(20120413));
        lot = manager.getLotsByEntityType("GERMPLSM", 0, 5).get(0);
        transaction.setLot(lot);
        transaction.setPersonId(new Integer(1));
        transaction.setPreviousAmount(null);
        transaction.setQuantity(new Integer(100));
        transaction.setSourceId(null);
        transaction.setSourceRecordId(null);
        transaction.setSourceType(null);
        transaction.setStatus(new Integer(1));
        transaction.setUserId(new Integer(1));
        transactions.add(transaction);

        manager.addTransaction(transactions);

        if (transactions.size() > 0 && transactions.get(0).getId() != null) {
            System.out.println("testAddTransaction() Added: " + transaction);
        }
        
        //TODO Delete transactions
        
    }


    @Test
    public void testUpdateTransaction() throws Exception {
        // this test assumes that there are existing records in the transaction
        // table
        Transaction t = manager.getTransactionById(new Integer(-1));
        String oldValues = "  Old comment: " + t.getComments() + ", old status: " + t.getStatus();
        t.setComments("updated comment again");
        t.setStatus(new Integer(0));

        manager.updateTransaction(t);
        System.out.println("testUpdateTransaction() RESULTS: " + "\n" + oldValues + "\n  New comment: " + t.getComments()
                + ", new status: " + t.getStatus());
    }
    

    @Test
    public void testGetTransactionsByLotId() throws Exception {
        Integer lotId = Integer.valueOf(-1);
        Set<Transaction> transactions = manager.getTransactionsByLotId(lotId);
        Assert.assertTrue(transactions != null);
        Assert.assertTrue(!transactions.isEmpty());
        System.out.println("testGetTransactionsByLotId(" + lotId + ") RESULTS: ");
        for (Transaction t : transactions)
            System.out.println("  " + t);
    }

    @Test
    public void testGetAllReserveTransactions() throws Exception {
        List<Transaction> transactions = manager.getAllReserveTransactions(0, 5);
        Assert.assertTrue(transactions != null);
        Assert.assertTrue(!transactions.isEmpty());
        System.out.println("testGetAllReserveTransactions() RESULTS: ");
        for (Transaction t : transactions)
            System.out.println("  " + t);
    }

    @Test
    public void countAllReserveTransactions() throws Exception {
        System.out.println("countAllReserveTransactions(): " + manager.countAllReserveTransactions());
    }

    @Test
    public void testGetAllDepositTransactions() throws Exception {
        List<Transaction> transactions = manager.getAllDepositTransactions(0, 5);
        Assert.assertTrue(transactions != null);
        Assert.assertTrue(!transactions.isEmpty());
        System.out.println("testGetAllDepositTransactions() RESULTS: ");
        for (Transaction t : transactions)
            System.out.println("  " + t);
    }

    @Test
    public void countAllDepositTransactions() throws Exception {
        System.out.println("countAllDepositTransactions(): " + manager.countAllDepositTransactions());
    }

    @Test
    public void testGetAllReserveTransactionsByRequestor() throws Exception {
        Integer personId = Integer.valueOf(253);
        List<Transaction> transactions = manager.getAllReserveTransactionsByRequestor(personId, 0, 5);
        Assert.assertTrue(transactions != null);
        Assert.assertTrue(!transactions.isEmpty());
        System.out.println("testGetAllReserveTransactionsByRequestor(" + personId + ") RESULTS: ");
        for (Transaction t : transactions)
            System.out.println("  " + t);
    }

    @Test
    public void countAllReserveTransactionsByRequestor() throws Exception {
        Integer personId = Integer.valueOf(253);
        System.out.println("countAllReserveTransactionsByRequestor(" + personId + "): "
                + manager.countAllReserveTransactionsByRequestor(personId));
    }

    @Test
    public void testGetAllDepositTransactionsByDonor() throws Exception {
        Integer personId = Integer.valueOf(253);
        List<Transaction> transactions = manager.getAllDepositTransactionsByDonor(personId, 0, 5);
        Assert.assertTrue(transactions != null);
        Assert.assertTrue(!transactions.isEmpty());
        System.out.println("testGetAllDepositTransactionsByDonor(" + personId + ") RESULTS: ");
        for (Transaction t : transactions)
            System.out.println("  " + t);
    }

    @Test
    public void countAllDepositTransactionsByDonor() throws Exception {
        Integer personId = Integer.valueOf(253);
        System.out
                .println("countAllDepositTransactionsByDonor(" + personId + ") : " + manager.countAllDepositTransactionsByDonor(personId));
    }

    @Test
    public void testGenerateReportOnAllUncommittedTransactions() throws Exception {
        System.out.println("Number of uncommitted transactions [countAllUncommittedTransactions()]: "
                + manager.countAllUncommittedTransactions());
        List<TransactionReportRow> report = manager.generateReportOnAllUncommittedTransactions(0, 5);
        Assert.assertTrue(report != null);
        Assert.assertTrue(!report.isEmpty());
        System.out.println("testGenerateReportOnAllUncommittedTransactions() REPORT: ");
        for (TransactionReportRow row : report)
            System.out.println("  " + row);
    }

    @Test
    public void testGenerateReportOnAllReserveTransactions() throws Exception {
        System.out.println("Number of reserved transactions [countAllReserveTransactions()]: " + manager.countAllReserveTransactions());
        List<TransactionReportRow> report = manager.generateReportOnAllReserveTransactions(0, 5);
        Assert.assertTrue(report != null);
        Assert.assertTrue(!report.isEmpty());
        System.out.println("testGenerateReportOnAllReserveTransactions() REPORT: ");
        for (TransactionReportRow row : report)
            System.out.println("  " + row);
    }

    @Test
    public void testGenerateReportOnAllWithdrawalTransactions() throws Exception {
        System.out.println("Number of withdrawal transactions [countAllWithdrawalTransactions()]: "
                + manager.countAllWithdrawalTransactions());
        List<TransactionReportRow> report = manager.generateReportOnAllWithdrawalTransactions(0, 5);
        Assert.assertTrue(report != null);
        Assert.assertTrue(!report.isEmpty());
        System.out.println("testGenerateReportOnAllWithdrawalTransactions() REPORT: ");
        for (TransactionReportRow row : report)
            System.out.println("  " + row);
    }

    @Test
    public void testGenerateReportOnAllLots() throws Exception {
        System.out.println("Balance Report on All Lots");
        System.out.println("Number of lots [countAllLots()]: " + manager.countAllLots());
        List<LotReportRow> report = manager.generateReportOnAllLots(0, 10);
        Assert.assertTrue(report != null);
        Assert.assertTrue(!report.isEmpty());
        System.out.println("testGenerateReportOnAllLots() REPORT: ");
        for (LotReportRow row : report)
            System.out.println("  " + row);
    }

    @Test
    public void testGenerateReportsOnDormantLots() throws Exception {
        int year = 2012;
        System.out.println("Balance Report on DORMANT Lots");
        List<LotReportRow> report = manager.generateReportOnDormantLots(year, 0, 10);
        Assert.assertTrue(report != null);
        Assert.assertTrue(!report.isEmpty());
        System.out.println("testGenerateReportsOnDormantLots(year=" + year + ") REPORT: ");
        for (LotReportRow row : report)
            System.out.println("  " + row);
    }

    @Test
    public void testGenerateReportOnLotsByEntityType() throws Exception {
        String type = "GERMPLSM";
        System.out.println("Balance Report on Lots by Entity Type: " + type);
        List<LotReportRow> report = manager.generateReportOnLotsByEntityType(type, 0, 10);
        Assert.assertTrue(report != null);
        Assert.assertTrue(!report.isEmpty());
        System.out.println("testGenerateReportOnLotsByEntityType(" + type + ") REPORT: ");
        for (LotReportRow row : report)
            System.out.println("  " + row);
    }

    @Test
    public void testGenerateReportOnLotsByEntityTypeAndEntityId() throws Exception {
        System.out.println("Balance Report on Lots by Entity Type and Entity ID:");
        String type = "GERMPLSM";
        List<Integer> entityIdList = new ArrayList<Integer>();
        entityIdList.add(50533);
        entityIdList.add(3);

        List<LotReportRow> report = manager.generateReportOnLotsByEntityTypeAndEntityId(type, entityIdList, 0, 10);

        Assert.assertTrue(report != null);
        Assert.assertTrue(!report.isEmpty());
        System.out.println("testGenerateReportOnLotsByEntityTypeAndEntityId(type=" + type + ", entityId=" + entityIdList + ") REPORT: ");
        for (LotReportRow row : report)
            System.out.println("  " + row);
    }

    @Test
    public void testGenerateReportOnEmptyLot() throws Exception {
        System.out.println("Report on empty lot");
        List<LotReportRow> report = manager.generateReportOnEmptyLots(0, 2);
        Assert.assertTrue(report != null);
        System.out.println("testGenerateReportOnEmptyLot() REPORT: ");
        for (LotReportRow row : report)
            System.out.println("  " + row);
    }

    @Test
    public void testGenerateReportOnLotWithMinimumAmount() throws Exception {
        long minimumAmount = 700;
        System.out.println("Report on lot with minimum balance");
        List<LotReportRow> report = manager.generateReportOnLotsWithMinimumAmount(minimumAmount, 0, 5);
        Assert.assertTrue(report != null);
        System.out.println("testGenerateReportOnLotWithMinimumAmount(minimumAmount="+minimumAmount+") REPORT: ");
        for (LotReportRow row : report)
            System.out.println("  " + row);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        factory.close();
    }
}
