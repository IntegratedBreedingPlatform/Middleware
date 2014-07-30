package org.generationcp.middleware.service.test;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.pojos.ims.LotsResult;
import org.generationcp.middleware.service.ServiceFactory;
import org.generationcp.middleware.service.api.InventoryService;
import org.generationcp.middleware.utils.test.Debug;
import org.generationcp.middleware.utils.test.TestOutputFormatter;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class TestInventoryServiceImpl extends TestOutputFormatter {

    private static ServiceFactory serviceFactory;
    private static InventoryService inventoryService;
    
    static DatabaseConnectionParameters local;
    static DatabaseConnectionParameters central;


    @BeforeClass
    public static void setUp() throws Exception {

        local = new DatabaseConnectionParameters(
                "testDatabaseConfig.properties", "local");
        central = new DatabaseConnectionParameters(
                "testDatabaseConfig.properties", "central");

        serviceFactory = new ServiceFactory(local, central);
        inventoryService = serviceFactory.getInventoryService();
        
    }

    @AfterClass
    public static void tearDown() throws Exception {
        if (serviceFactory != null) {
            serviceFactory.close();
        }
    }

    @Test
    public void testAddLot() throws Exception {
    	List<Integer> gids = new ArrayList<Integer>();
    	int maxGids = 10;
    	for (int i = 0; i < maxGids; i++) {
    		gids.add(i + ((int)(Math.random() * 1000)));
    	}
    	
    	int locationId = 1 + ((int)(Math.random() * 1000));
    	int scaleId = 6090;
    	String comment = "No Comment";
    	int userId = 1;
    	double amount = 1.23456;
    	int listId = 1426;
    	LotsResult result = inventoryService.addAdvanceLots(gids, locationId, scaleId, comment, userId, amount, listId);
    	if (result != null) {
    		Debug.printFormattedObject(INDENT, result);
    	}
    	
    }
}
