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
import org.generationcp.middleware.utils.test.TestOutputFormatter;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestInventoryDataManagerImpl extends TestOutputFormatter {

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
        Debug.println(INDENT, "testGetLotsByEntityType(" + type + "): ");
        Debug.printObjects(INDENT, results);
    }

    @Test
    public void testCountLotsByEntityType() throws Exception {
        Debug.println(INDENT, "testCountLotsByEntityType(\"GERMPLSM\"): " + manager.countLotsByEntityType("GERMPLSM"));
    }

    @Test
    public void testGetLotsByEntityTypeAndEntityId() throws Exception {
        String type = "GERMPLSM";
        Integer entityId = Integer.valueOf(50533);
        List<Lot> results = manager.getLotsByEntityTypeAndEntityId(type, entityId, 0, 5);
        Assert.assertTrue(results != null);
        Assert.assertTrue(!results.isEmpty());
        Debug.println(INDENT, "testGetLotsByEntityTypeAndEntityId(type=" + type + ", entityId=" + entityId + "): ");
        Debug.printObjects(INDENT, results);
    }

    @Test
    public void testCountLotsByEntityTypeAndEntityId() throws Exception {
        String type = "GERMPLSM";
        Integer entityId = Integer.valueOf(50533);
        Debug.println(INDENT, "testCountLotsByEntityTypeAndEntityId(type=" + type + ", entityId=" + entityId + "): "
                + manager.countLotsByEntityTypeAndEntityId(type, entityId));
    }

    @Test
    public void testGetLotsByEntityTypeAndLocationId() throws Exception {
        String type = "GERMPLSM";
        Integer locationId = Integer.valueOf(9000);
        List<Lot> results = manager.getLotsByEntityTypeAndLocationId(type, locationId, 0, 5);
        Assert.assertTrue(results != null);
        Assert.assertTrue(!results.isEmpty());
        Debug.println(INDENT, "testGetLotsByEntityTypeAndLocationId(type=" + type + ", locationId=" + locationId + "): ");
        Debug.printObjects(INDENT, results);
    }

    @Test
    public void testCountLotsByEntityTypeAndLocationId() throws Exception {
        String type = "GERMPLSM";
        Integer locationId = Integer.valueOf(9000);
        Debug.println(INDENT, "testCountLotsByEntityTypeAndLocationId(type=" + type + ", locationId=" + locationId + "): "
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
        Debug.println(INDENT, "testGetLotsByEntityTypeAndEntityIdAndLocationId(type=" + type + ", entityId=" + entityId + ", locationId="
                + locationId + "): ");
        Debug.printObjects(INDENT, results);
    }

    @Test
    public void testCountLotsByEntityTypeAndEntityIdAndLocationId() throws Exception {
        String type = "GERMPLSM";
        Integer entityId = Integer.valueOf(50533);
        Integer locationId = Integer.valueOf(9000);
        Debug.println(INDENT, "testCountLotsByEntityTypeAndEntityIdAndLocationId(type=" + type + ", entityId=" + entityId + ", locationId="
                + locationId + "): " + manager.countLotsByEntityTypeAndEntityIdAndLocationId(type, entityId, locationId));
    }

    @Test
    public void testGetActualLotBalance() throws Exception {
        Integer lotId = Integer.valueOf(-1);
        Debug.println(INDENT, "testGetActualLotBalance(lotId=" + lotId + "): " + manager.getActualLotBalance(lotId));
    }

    @Test
    public void testGetAvailableLotBalance() throws Exception {
        Integer lotId = Integer.valueOf(-1);
        Debug.println(INDENT, "testGetAvailableLotBalance(lotId=" + lotId + "): " + manager.getAvailableLotBalance(lotId));
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
            Debug.println(INDENT, "testAddLot() Added: " + id + "\n\t" + lot);
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
            Debug.println(INDENT, "testAddLot() Added: " + idList);
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
        Debug.println(INDENT, "testUpdateLot(): " + id + "\n  Old comment: " + oldComment + "\n  New comment: " + lot.getComments());
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
            Debug.println(INDENT, "testAddTransaction() Added: " + transaction);
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
            Debug.println(INDENT, "testAddTransaction() Added: " + transaction);
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
        Debug.println(INDENT, "testUpdateTransaction(): " + "\n" + oldValues + "\n  New comment: " + t.getComments()
                + ", new status: " + t.getStatus());
    }
    

    @Test
    public void testGetTransactionsByLotId() throws Exception {
        Integer lotId = Integer.valueOf(-1);
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
        Integer personId = Integer.valueOf(253);
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
        Integer personId = Integer.valueOf(253);
        List<Transaction> transactions = manager.getAllDepositTransactionsByDonor(personId, 0, 5);
        Assert.assertTrue(transactions != null);
        Assert.assertTrue(!transactions.isEmpty());
        Debug.println(INDENT, "testGetAllDepositTransactionsByDonor(" + personId + "): ");
        Debug.printObjects(INDENT, transactions);
    }

    @Test
    public void testCountAllDepositTransactionsByDonor() throws Exception {
        Integer personId = Integer.valueOf(253);
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
        String type = "GERMPLSM";
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
        String type = "GERMPLSM";
        List<Integer> entityIdList = new ArrayList<Integer>();
        entityIdList.add(532153);
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
        List<Lot> results = manager.getAllLots(0,50);
        Assert.assertNotNull(results);
        Assert.assertTrue(!results.isEmpty());
        Debug.println(INDENT, "testGetAllLots(): ");
        Debug.printObjects(INDENT, results);
    }
    
    @Test
    public void testGetTransactionById() throws Exception {
        Integer id = -1;
        Transaction transactionid = manager.getTransactionById(id);
		Assert.assertNotNull(transactionid);
		Debug.println(INDENT, "testGetTransactionById(" + id + "): ");
		Debug.println(transactionid.toString());
    }
    
    @AfterClass
    public static void tearDown() throws Exception {
        factory.close();
    }
}
