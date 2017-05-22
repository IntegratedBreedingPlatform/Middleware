package org.generationcp.middleware.dao.ims;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.data.initializer.InventoryDetailsTestDataInitializer;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;

public class LotDAOTest extends IntegrationTestBase {

	private LotDAO lotDAO;

	@Autowired
	private GermplasmDataManager germplasmDataManager;

	@Autowired
	private InventoryDataManager inventoryDataManager;


	@Before
	public void setUp() throws Exception {
		this.lotDAO = new LotDAO();
		this.lotDAO.setSession(this.sessionProvder.getSession());
	}

	@Test
	public void testRetrieveLotScalesForGermplasms() throws Exception {
		final Germplasm germplasm =
				GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		final Integer germplasmId = this.germplasmDataManager.addGermplasm(germplasm, germplasm.getPreferredName());

		Lot lot = InventoryDetailsTestDataInitializer.createLot(1, "GERMPLSM", germplasmId, 1, 8264, 0, 1, "Comments");
		this.inventoryDataManager.addLots(com.google.common.collect.Lists.<Lot>newArrayList(lot));

		Transaction transaction = InventoryDetailsTestDataInitializer
				.createReservationTransaction(2.0, 0, "Deposit", lot, 1, 1, 1, "LIST");
		this.inventoryDataManager.addTransactions(Lists.<Transaction>newArrayList(transaction));

		List<Object[]> scalesForGermplsms = this.lotDAO.retrieveLotScalesForGermplasms(Lists.<Integer>newArrayList(germplasmId));

		Assert.assertEquals(1, scalesForGermplsms.size());
		Assert.assertEquals(germplasmId, (Integer) scalesForGermplsms.get(0)[0]);
		Assert.assertEquals(8264, ((Integer) scalesForGermplsms.get(0)[1]).intValue());
		Assert.assertEquals("g", (String) scalesForGermplsms.get(0)[2]);

	}

	@Test
	public void testGetAvailableBalanceCountAndTotalLotsCount() throws Exception {
		final Germplasm germplasm =
				GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		final Integer germplasmId = this.germplasmDataManager.addGermplasm(germplasm, germplasm.getPreferredName());

		Lot lot = InventoryDetailsTestDataInitializer.createLot(1, "GERMPLSM", germplasmId, 1, 8264, 0, 1, "Comments");
		this.inventoryDataManager.addLots(com.google.common.collect.Lists.<Lot>newArrayList(lot));

		Transaction transaction = InventoryDetailsTestDataInitializer
				.createReservationTransaction(5.0, 0, "Deposit", lot, 1, 1, 1, "LIST");
		this.inventoryDataManager.addTransactions(Lists.<Transaction>newArrayList(transaction));

		Map<Integer, Object[]> availableBalanceCountAndTotalLotsCount =
				this.lotDAO.getAvailableBalanceCountAndTotalLotsCount(Lists.<Integer>newArrayList(germplasmId));

		Assert.assertEquals(1, availableBalanceCountAndTotalLotsCount.size());
		Object[] balanceValues = availableBalanceCountAndTotalLotsCount.get(germplasmId);

		Assert.assertEquals(1, ((BigInteger)balanceValues[0]).intValue());
		Assert.assertEquals(1, ((BigInteger)balanceValues[1]).intValue());
		Assert.assertEquals("5.0", ((Double)balanceValues[2]).toString());
		Assert.assertEquals(1, ((BigInteger)balanceValues[3]).intValue());
		Assert.assertEquals(8264, ((Integer)balanceValues[4]).intValue());

	}

	@Test
	public void testGetLotAggregateDataForGermplasm() throws Exception {

		final Germplasm germplasm =
				GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		final Integer germplasmId = this.germplasmDataManager.addGermplasm(germplasm, germplasm.getPreferredName());

		Lot lot = InventoryDetailsTestDataInitializer.createLot(1, "GERMPLSM", germplasmId, 1, 8264, 0, 1, "Comments");
		this.inventoryDataManager.addLots(com.google.common.collect.Lists.<Lot>newArrayList(lot));

		Transaction transaction = InventoryDetailsTestDataInitializer
				.createDepositTransaction(5.0, 0, "Deposit", lot, 1, 1, 1, "LIST", "InventoryId");
		this.inventoryDataManager.addTransactions(Lists.<Transaction>newArrayList(transaction));

		List<Lot> lotAggregateDataForGermplasm = this.lotDAO.getLotAggregateDataForGermplasm(germplasmId);

		Assert.assertEquals(1, lotAggregateDataForGermplasm.size());

		Lot returnedLot = lotAggregateDataForGermplasm.get(0);

		Assert.assertEquals(lot.getId(), returnedLot.getId());
		Assert.assertEquals(lot.getEntityId(), returnedLot.getEntityId());
		Assert.assertEquals(lot.getLocationId(), returnedLot.getLocationId());
		Assert.assertEquals(lot.getComments(), returnedLot.getComments());
		Assert.assertEquals(lot.getStatus(), returnedLot.getStatus());
		Assert.assertEquals("5.0", returnedLot.getAggregateData().getActualBalance().toString());
		Assert.assertEquals("5.0", returnedLot.getAggregateData().getAvailableBalance().toString());
		Assert.assertEquals("0.0", returnedLot.getAggregateData().getReservedTotal().toString());
		Assert.assertEquals("0.0", returnedLot.getAggregateData().getCommittedTotal().toString());
		Assert.assertEquals("InventoryId", returnedLot.getAggregateData().getStockIds());

		Assert.assertEquals(0, returnedLot.getAggregateData().getReservationMap().size());
		Assert.assertEquals(0, returnedLot.getAggregateData().getCommittedMap().size());

	}

	@Test
	public void testGetGermplasmsWithOpenLots() throws Exception {
		final Germplasm germplasm =
				GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		final Integer germplasmId = this.germplasmDataManager.addGermplasm(germplasm, germplasm.getPreferredName());

		final Lot lot = InventoryDetailsTestDataInitializer.createLot(1, "GERMPLSM", germplasmId, 1, 8264, 0, 1, "Comments");
		this.inventoryDataManager.addLots(com.google.common.collect.Lists.newArrayList(lot));

		final Transaction transaction =
				InventoryDetailsTestDataInitializer.createReservationTransaction(2.0, 0, "Deposit", lot, 1, 1, 1, "LIST");
		this.inventoryDataManager.addTransactions(Lists.newArrayList(transaction));

		final Set<Integer> gids = this.lotDAO.getGermplasmsWithOpenLots(Lists.newArrayList(germplasm.getGid()));

		Assert.assertEquals(1, gids.size());
	}

	@Test
	public void testGetGermplasmsWithNoOpenLots() throws Exception {
		final Germplasm germplasm =
				GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		final Integer germplasmId = this.germplasmDataManager.addGermplasm(germplasm, germplasm.getPreferredName());

		final Lot lot = InventoryDetailsTestDataInitializer.createLot(1, "GERMPLSM", germplasmId, 1, 8264, 1, 1, "Comments");
		this.inventoryDataManager.addLots(com.google.common.collect.Lists.newArrayList(lot));

		final Transaction transaction =
				InventoryDetailsTestDataInitializer.createReservationTransaction(2.0, 0, "Deposit", lot, 1, 1, 1, "LIST");
		this.inventoryDataManager.addTransactions(Lists.newArrayList(transaction));

		final Set<Integer> gids = this.lotDAO.getGermplasmsWithOpenLots(Lists.newArrayList(germplasm.getGid()));

		Assert.assertEquals(0, gids.size());
	}
}
