package org.generationcp.middleware.service;

import org.generationcp.middleware.DataManagerIntegrationTest;
import org.generationcp.middleware.pojos.ims.LotsResult;
import org.generationcp.middleware.service.api.InventoryService;
import org.generationcp.middleware.utils.test.Debug;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;


@RunWith(JUnit4.class)
public class InventoryServiceImplTest extends DataManagerIntegrationTest {

    private static InventoryService inventoryService;

    @BeforeClass
    public static void setUp() throws Exception {
        inventoryService = managerFactory.getInventoryMiddlewareService();
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
    	LotsResult result = inventoryService.addLotsForList(gids, locationId, scaleId, comment,
				userId, amount, listId);
    	if (result != null) {
    		Debug.printFormattedObject(INDENT, result);
    	}
    	
    }
}
