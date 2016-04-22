
package org.generationcp.middleware.data.initializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.domain.inventory.InventoryDetails;

public class InventoryDetailsTestDataInitializer {

	private static final int NO_OF_STOCK_LIST_ENTRIES = 20;

	public static Map<String, InventoryDetails> createInventoryDetailsMap() {
		final Map<String, InventoryDetails> inventoryDetails = new HashMap<String, InventoryDetails>();

		for (int i = 1; i <= InventoryDetailsTestDataInitializer.NO_OF_STOCK_LIST_ENTRIES; i++) {
			inventoryDetails.put(String.valueOf(i), new InventoryDetails());
		}

		return inventoryDetails;
	}

	public static List<InventoryDetails> createInventoryDetailList(final Integer numOfEntries) {
		final List<InventoryDetails> inventoryDetails = new ArrayList<InventoryDetails>();

		for (int i = 0; i < numOfEntries; i++) {
			final int id = i + 1;
			final InventoryDetails invDetails = new InventoryDetails();
			invDetails.setLotId(id);
			invDetails.setGid(id);
            invDetails.setInstanceNumber(1);
            invDetails.setEntryId(1);
			inventoryDetails.add(invDetails);
		}

		return inventoryDetails;
	}
}
