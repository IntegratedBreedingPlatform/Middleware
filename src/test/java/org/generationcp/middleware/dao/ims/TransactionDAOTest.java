
package org.generationcp.middleware.dao.ims;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.GermplasmListDataDAO;
import org.generationcp.middleware.domain.inventory.InventoryDetails;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TransactionDAOTest extends IntegrationTestBase {

	private TransactionDAO dao;
	private GermplasmListDataDAO germplasmListDataDAO;

	@Before
	public void setUp() throws Exception {
		this.dao = new TransactionDAO();
		this.dao.setSession(this.sessionProvder.getSession());

		this.germplasmListDataDAO = new GermplasmListDataDAO();
		this.germplasmListDataDAO.setSession(this.sessionProvder.getSession());
	}

	@Test
	public void testRetrieveStockIds() {

		List<Integer> lRecIDs = new ArrayList<>();
		lRecIDs.add(1);
		Map<Integer, String> lRecIDStockIDMap = this.dao.retrieveStockIds(lRecIDs);
		Assert.assertNotNull(lRecIDStockIDMap);
	}

	@Test
	public void testGetStockIdsByListDataProjectListId() throws MiddlewareQueryException {
		List<String> stockIds = this.dao.getStockIdsByListDataProjectListId(17);
		Assert.assertNotNull(stockIds);
	}

	@Test
	public void testGetInventoryDetailsByTransactionRecordId() throws MiddlewareQueryException {
		List<Integer> recordIds = new ArrayList<Integer>();
		List<GermplasmListData> listDataList = this.germplasmListDataDAO.getByListId(1);
		for (GermplasmListData germplasmListData : listDataList) {
			recordIds.add(germplasmListData.getId());
		}
		List<InventoryDetails> inventoryDetailsList = this.dao.getInventoryDetailsByTransactionRecordId(recordIds);
		for (InventoryDetails inventoryDetails : inventoryDetailsList) {
			Assert.assertTrue(recordIds.contains(inventoryDetails.getSourceRecordId()));
		}
	}

	@Test
	public void testGetSimilarStockIdsEmptyListParam() {
		final boolean emptyListParamCondition = this.dao.getSimilarStockIds(new ArrayList<String>()).isEmpty();
		final boolean nullParamCondition = this.dao.getSimilarStockIds(null).isEmpty();

		Assert.assertTrue("List of returned similar stock ids should be empty given empty list", emptyListParamCondition);
		Assert.assertTrue("List of returned similar stock ids should be empty given a null parameter", nullParamCondition);
	}
}
