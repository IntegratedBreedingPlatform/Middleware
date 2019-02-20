
package org.generationcp.middleware.util;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.data.initializer.GermplasmListDataTestDataInitializer;
import org.generationcp.middleware.data.initializer.GermplasmListTestDataInitializer;
import org.generationcp.middleware.data.initializer.ListDataProjectTestDataInitializer;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.ListDataProject;
import org.generationcp.middleware.pojos.Method;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@RunWith(MockitoJUnitRunner.class)
public class FieldbookListUtilTest {

	@Test
	public void sortMethodNamesInAscendingOrderTest() throws Exception {
		List<Method> newMethodList = new ArrayList<Method>();
		Method methodOne = this.createMethod("UUID", 10, "AName");
		Method methodTwo = this.createMethod("UUID2", 12, "SecondName");
		Method methodThree = this.createMethod("UUID3", 14, "ThirdName");

		newMethodList.add(methodTwo);
		newMethodList.add(methodOne);
		newMethodList.add(methodThree);

		FieldbookListUtil.sortMethodNamesInAscendingOrder(newMethodList);

		assertEquals("AName", newMethodList.get(0).getMname());
		assertEquals("SecondName", newMethodList.get(1).getMname());
		assertEquals("ThirdName", newMethodList.get(2).getMname());
	}

	public Method createMethod(String uniqueId, Integer id, String name) {
		Method methodName = new Method();
		methodName.setUniqueID(uniqueId);
		methodName.setMid(id);
		methodName.setMname(name);
		return methodName;
	}

	@Test
	public void testPopulateStockIdInListDataProject() throws Exception {
		GermplasmList germplasmList = GermplasmListTestDataInitializer.createGermplasmList(1);
		InventoryDataManager inventoryDataManager = Mockito.mock(InventoryDataManager.class);

		Map<Integer, String> mockData = Maps.newHashMap();
		mockData.put(100, "StockID101, StockID102");

		Mockito.when(inventoryDataManager.retrieveStockIds(Mockito.anyListOf(Integer.class))).thenReturn(mockData);

		ListDataProject listDataProject = ListDataProjectTestDataInitializer
				.createListDataProject(germplasmList, 100, 0, 1, "entryCode", "seedSource", "designation", "groupName",
						"duplicate", "notes", 20170125);

		FieldbookListUtil.populateStockIdInListDataProject(Lists.newArrayList(listDataProject),inventoryDataManager);

		Assert.assertNotNull("StockID field should not be null after populating it");
		Assert.assertEquals(mockData.get(100), listDataProject.getStockIDs());

	}

	@Test
	public void testPopulateStockIdInGermplasmListData() throws Exception {
		GermplasmList germplasmList = GermplasmListTestDataInitializer
				.createGermplasmList(1);

		Map<Integer, String> mockData = Maps.newHashMap();
		mockData.put(101, "StockID101, StockID102");

		InventoryDataManager inventoryDataManager = Mockito.mock(InventoryDataManager.class);

		Mockito.when(inventoryDataManager.retrieveStockIds(Mockito.anyListOf(Integer.class))).thenReturn(mockData);
		GermplasmListData germplasmListData = GermplasmListDataTestDataInitializer.createGermplasmListData(germplasmList, 101, 1);

		FieldbookListUtil.populateStockIdInGermplasmListData(Lists.newArrayList(germplasmListData),inventoryDataManager);

		Assert.assertNotNull("StockID field should not be null after populating it");
		Assert.assertEquals(mockData.get(101), germplasmListData.getStockIDs());


	}

}
