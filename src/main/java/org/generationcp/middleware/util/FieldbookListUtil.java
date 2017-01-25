
package org.generationcp.middleware.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.ListDataProject;
import org.generationcp.middleware.pojos.Method;

public class FieldbookListUtil {

	private FieldbookListUtil() {

	}

	public static void sortMethodNamesInAscendingOrder(List<Method> methodList) {
		Collections.sort(methodList, new Comparator<Method>() {

			@Override
			public int compare(Method o1, Method o2) {
				String methodName1 = o1.getMname().toUpperCase();
				String methodName2 = o2.getMname().toUpperCase();

				// ascending order
				return methodName1.compareTo(methodName2);
			}
		});
	}

	public static void populateStockIdInListDataProject(final List<ListDataProject> data , InventoryDataManager inventoryDataManager) {
		final List<Integer> gids = new ArrayList<>();
		if (data != null && !data.isEmpty()) {
			for (final ListDataProject listDataProject : data) {
				gids.add(listDataProject.getGermplasmId());
			}
		}

		Map<Integer, String> stockIds = inventoryDataManager.retrieveStockIds(gids);


		if (data != null && !data.isEmpty()) {
			for (final ListDataProject ListData : data) {
				String stockIdValue = "";
				if (stockIds != null) {
					for (final Integer gid : stockIds.keySet()) {
						if (ListData.getGermplasmId().equals(gid)) {
							stockIdValue = stockIds.get(gid);
							break;
						}
					}
				}
				ListData.setStockIDs(stockIdValue);
			}
		}
	}

	public static void populateStockIdInGermplasmListData(final List<GermplasmListData> data , InventoryDataManager inventoryDataManager) {
		final List<Integer> gids = new ArrayList<>();
		if (data != null && !data.isEmpty()) {
			for (final GermplasmListData germplasmListData : data) {
				gids.add(germplasmListData.getGid());
			}
		}

		Map<Integer, String> stockIds = inventoryDataManager.retrieveStockIds(gids);

		if (data != null && !data.isEmpty()) {
			for (final GermplasmListData ListData : data) {
				String stockIdValue = "";
				if (stockIds != null) {
					for (final Integer gid : stockIds.keySet()) {
						if (ListData.getGid().equals(gid)) {
							stockIdValue = stockIds.get(gid);
							break;
						}
					}
				}
				ListData.setStockIDs(stockIdValue);
			}
		}
	}


}
