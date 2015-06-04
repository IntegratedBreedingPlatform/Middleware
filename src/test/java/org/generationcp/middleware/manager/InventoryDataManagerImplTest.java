
package org.generationcp.middleware.manager;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.inventory.InventoryDetails;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Created by Daniel Villafuerte on 5/8/2015.
 */

@RunWith(MockitoJUnitRunner.class)
public class InventoryDataManagerImplTest {

	public static final int TEST_DATA_COUNT = 5;

	InventoryDataManagerImpl dut = new InventoryDataManagerImpl();

	@Test
	public void testFillInventoryDetailList() {
		List<InventoryDetails> detailsList = new ArrayList<>();
		List<GermplasmListData> dataList = new ArrayList<>();

		for (int i = 0; i < InventoryDataManagerImplTest.TEST_DATA_COUNT; i++) {
			// the fill process matches InventoryDetails object with GermplasmListData
			// based on sourceRecordId and id respectively
			InventoryDetails details = new InventoryDetails();
			details.setGid(i);
			details.setSourceRecordId(i);

			GermplasmListData data = new GermplasmListData();
			data.setGid(i);
			data.setId(i);
			data.setEntryId(i);
			data.setDesignation("TEST " + i);
			data.setSeedSource("SOURCE " + i);
			data.setGroupName("PARENTAGE " + i);
			detailsList.add(details);
			dataList.add(data);

		}

		// add one more data entry to test case where germplasmlistdata items are more than retrieved inventory details
		GermplasmListData data = new GermplasmListData();
		data.setId(InventoryDataManagerImplTest.TEST_DATA_COUNT + 1);
		data.setGid(InventoryDataManagerImplTest.TEST_DATA_COUNT + 1);
		data.setEntryId(InventoryDataManagerImplTest.TEST_DATA_COUNT + 1);
		data.setSeedSource("SOURCE " + InventoryDataManagerImplTest.TEST_DATA_COUNT + 1);
		data.setDesignation("TEST " + InventoryDataManagerImplTest.TEST_DATA_COUNT + 1);
		data.setGroupName("PARENTAGE " + InventoryDataManagerImplTest.TEST_DATA_COUNT + 1);
		dataList.add(data);

		this.dut.fillInventoryDetailList(detailsList, dataList);

		Assert.assertEquals(InventoryDataManagerImplTest.TEST_DATA_COUNT + 1, detailsList.size());
		for (int i = 0; i < InventoryDataManagerImplTest.TEST_DATA_COUNT + 1; i++) {
			Assert.assertNotNull(detailsList.get(i).getGermplasmName());
			Assert.assertNotNull(detailsList.get(i).getSource());
			Assert.assertNotNull(detailsList.get(i).getEntryId());
			Assert.assertNotNull(detailsList.get(i).getParentage());
		}

		// assertNotNull(detailsList.get(TEST_DATA_COUNT).getGermplasmName());
	}
}
