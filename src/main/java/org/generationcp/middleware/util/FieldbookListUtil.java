
package org.generationcp.middleware.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.pojos.GermplasmListData;
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

}
