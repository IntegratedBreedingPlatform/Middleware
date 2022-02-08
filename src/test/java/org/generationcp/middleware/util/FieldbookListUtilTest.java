
package org.generationcp.middleware.util;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.data.initializer.GermplasmListDataTestDataInitializer;
import org.generationcp.middleware.data.initializer.GermplasmListTestDataInitializer;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.Method;
import org.junit.Assert;
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
		Method methodOne = this.createMethod(10, "AName");
		Method methodTwo = this.createMethod(12, "SecondName");
		Method methodThree = this.createMethod(14, "ThirdName");

		newMethodList.add(methodTwo);
		newMethodList.add(methodOne);
		newMethodList.add(methodThree);

		FieldbookListUtil.sortMethodNamesInAscendingOrder(newMethodList);

		assertEquals("AName", newMethodList.get(0).getMname());
		assertEquals("SecondName", newMethodList.get(1).getMname());
		assertEquals("ThirdName", newMethodList.get(2).getMname());
	}

	public Method createMethod(Integer id, String name) {
		Method methodName = new Method();
		methodName.setMid(id);
		methodName.setMname(name);
		return methodName;
	}

}
