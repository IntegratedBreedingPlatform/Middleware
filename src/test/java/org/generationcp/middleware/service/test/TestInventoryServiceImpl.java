package org.generationcp.middleware.service.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.pojos.LotsResult;
import org.generationcp.middleware.service.ServiceFactory;
import org.generationcp.middleware.service.api.InventoryService;
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
    	int maxGids = 100;
    	for (int i = 1; i <= maxGids; i++) {
    		gids.add(i);
    	}
    	int locationId = 1;
    	int scaleId = 6090;
    	String comment = "No Comment";
    	int userId = 1;
    	LotsResult result = inventoryService.addLots(gids, locationId, scaleId, comment, userId);
    	if (result != null) {
    		if (result.getGidsProcessed() != null && !result.getGidsProcessed().isEmpty()) {
	    		System.out.println("GIDs ADDED = ");
	    		for (Integer gid : result.getGidsProcessed()) {
	    			System.out.print(gid + " ");
	    		}
	    		System.out.println();
	    	}
	    	else {
	    		System.out.println("NO LOT ADDED");
	    	}
    		
			if (result.getGidsSkipped() != null && !result.getGidsSkipped().isEmpty()) {
				System.out.println("GIDs SKIPPED ");
				for (Integer gid : result.getGidsSkipped()) {
					System.out.print(gid + " ");
				}
				System.out.println();
			}
    	}
    }
}
