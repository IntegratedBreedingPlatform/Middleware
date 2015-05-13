package org.generationcp.middleware.manager;

import org.generationcp.middleware.domain.inventory.InventoryDetails;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

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

        for (int i = 0; i < TEST_DATA_COUNT; i++) {
            //  the fill process matches InventoryDetails object with GermplasmListData based on GID
            InventoryDetails details = new InventoryDetails();
            details.setGid(i);

            GermplasmListData data = new GermplasmListData();
            data.setGid(i);
            data.setDesignation("TEST " + i);
            data.setSeedSource("SOURCE " + i);
            detailsList.add(details);
            dataList.add(data);

        }

        // add one more data entry to test case where germplasmlistdata items are more than retrieved inventory details
        GermplasmListData data = new GermplasmListData();
        data.setGid(TEST_DATA_COUNT + 1);
        data.setSeedSource("SOURCE " + TEST_DATA_COUNT + 1);
        data.setDesignation("TEST " + TEST_DATA_COUNT + 1);
        dataList.add(data);

        dut.fillInventoryDetailList(detailsList, dataList);

        assertEquals(TEST_DATA_COUNT  + 1, detailsList.size());
        for (int i = 0; i < TEST_DATA_COUNT; i++) {
            assertNotNull(detailsList.get(i).getGermplasmName());
            assertNotNull(detailsList.get(i).getSource());
        }

        assertNotNull(detailsList.get(TEST_DATA_COUNT).getGermplasmName());
    }
}
