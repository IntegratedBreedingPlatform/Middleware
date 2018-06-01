package org.generationcp.middleware.data.initializer;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.inventory.GermplasmInventory;
import org.generationcp.middleware.domain.inventory.ListEntryLotDetails;

public class ListEntryLotDetailsTestDataInitializer {
	
	public static ListEntryLotDetails createListEntryLotDetails(final Integer id) {
		final ListEntryLotDetails listEntryLotDetail = new ListEntryLotDetails(id);
		listEntryLotDetail.setLotId(id);
		listEntryLotDetail.setStockIds("Stock " + id);
		listEntryLotDetail.setWithdrawalStatus(GermplasmInventory.RESERVED);
		return listEntryLotDetail;
	}
	
	public static List<ListEntryLotDetails> createListEntryLotDetailsList(final int numberofEntries) {
		final List<ListEntryLotDetails> listEntryLotDetails = new ArrayList<>();
		for(int i = 0; i<numberofEntries; i++) {
			listEntryLotDetails.add(ListEntryLotDetailsTestDataInitializer.createListEntryLotDetails(i));
		}
		return listEntryLotDetails;
	}
	
	
}
