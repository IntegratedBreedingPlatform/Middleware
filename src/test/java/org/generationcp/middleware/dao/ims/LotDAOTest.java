package org.generationcp.middleware.dao.ims;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.GermplasmListDAO;
import org.generationcp.middleware.dao.LocationDAO;
import org.generationcp.middleware.data.initializer.GermplasmListTestDataInitializer;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.data.initializer.InventoryDetailsTestDataInitializer;
import org.generationcp.middleware.data.initializer.LocationTestDataInitializer;
import org.generationcp.middleware.domain.inventory.manager.ExtendedLotDto;
import org.generationcp.middleware.domain.inventory.manager.LotsSearchDto;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.generationcp.middleware.pojos.ims.TransactionType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LotDAOTest extends IntegrationTestBase {

	private LotDAO lotDAO;

	private LocationDAO locationDAO;

	private GermplasmListDAO germplasmListDAO;

	@Autowired
	private GermplasmListManager manager;

	@Autowired
	private GermplasmDataManager germplasmDataManager;

	@Autowired
	private InventoryDataManager inventoryDataManager;

	private Lot lot1, lot2, lot3;
	private Transaction transaction1, transaction2, transaction3;
	private Location location;
	private Germplasm germplasm1, germplasm2;
	private GermplasmList germplasmList;

	private static final String GERMPLASM = "GERMPLSM";
	
	private static final String LST = "LST";
	private static final String LIST = "LIST";

	@Before
	public void setUp() throws Exception {
		this.lotDAO = new LotDAO();
		this.lotDAO.setSession(this.sessionProvder.getSession());
		this.locationDAO = new LocationDAO();
		this.locationDAO.setSession(this.sessionProvder.getSession());
		this.germplasmListDAO = new GermplasmListDAO();
		this.germplasmListDAO.setSession(this.sessionProvder.getSession());
		this.createLocationForSearchLotTest();
		this.createDataForSearchLotsTest();
	}

	@Test
	public void testRetrieveLotScalesForGermplasms() throws Exception {
		final Germplasm germplasm =
				GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		final Integer germplasmId = this.germplasmDataManager.addGermplasm(germplasm, germplasm.getPreferredName());

		final Lot lot = InventoryDetailsTestDataInitializer.createLot(1, GERMPLASM, germplasmId, 1, 8264, 0, 1, "Comments", "InventoryId");
		this.inventoryDataManager.addLots(com.google.common.collect.Lists.newArrayList(lot));

		final Transaction transaction = InventoryDetailsTestDataInitializer
				.createReservationTransaction(2.0, 0, TransactionType.DEPOSIT.getValue(), lot, 1, 1, 1, "LIST");
		this.inventoryDataManager.addTransactions(Lists.newArrayList(transaction));

		final List<Object[]> scalesForGermplsms = this.lotDAO.retrieveLotScalesForGermplasms(Lists.newArrayList(germplasmId));

		Assert.assertEquals(1, scalesForGermplsms.size());
		Assert.assertEquals(germplasmId, scalesForGermplsms.get(0)[0]);
		Assert.assertEquals(8264, ((Integer) scalesForGermplsms.get(0)[1]).intValue());
		Assert.assertEquals("g", scalesForGermplsms.get(0)[2]);

	}

	@Test
	public void testGetAvailableBalanceCountAndTotalLotsCount() throws Exception {
		final Germplasm germplasm =
				GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		final Integer germplasmId = this.germplasmDataManager.addGermplasm(germplasm, germplasm.getPreferredName());

		final Lot lot = InventoryDetailsTestDataInitializer.createLot(1, GERMPLASM, germplasmId, 1, 8264, 0, 1, "Comments", "InventoryId");
		this.inventoryDataManager.addLots(Lists.newArrayList(lot));

		final Transaction transaction = InventoryDetailsTestDataInitializer.createReservationTransaction(5.0, 0,
				TransactionType.DEPOSIT.getValue(), lot, 1, 1, 1, LIST);
		this.inventoryDataManager.addTransactions(Lists.newArrayList(transaction));

		final Map<Integer, Object[]> availableBalanceCountAndTotalLotsCount =
				this.lotDAO.getAvailableBalanceCountAndTotalLotsCount(Lists.newArrayList(germplasmId));

		Assert.assertEquals(1, availableBalanceCountAndTotalLotsCount.size());
		Object[] balanceValues = availableBalanceCountAndTotalLotsCount.get(germplasmId);

		Assert.assertEquals(1, ((BigInteger) balanceValues[0]).intValue());
		Assert.assertEquals(1, ((BigInteger) balanceValues[1]).intValue());
		Assert.assertEquals("5.0", balanceValues[2].toString());
		Assert.assertEquals(1, ((BigInteger) balanceValues[3]).intValue());
		Assert.assertEquals(8264, ((Integer) balanceValues[4]).intValue());

	}

	@Test
	public void testGetLotAggregateDataForGermplasm() throws Exception {

		final Germplasm germplasm =
				GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		final Integer germplasmId = this.germplasmDataManager.addGermplasm(germplasm, germplasm.getPreferredName());

		final Lot lot = InventoryDetailsTestDataInitializer.createLot(1, GERMPLASM, germplasmId, 1, 8264, 0, 1, "Comments", "InventoryId");
		this.inventoryDataManager.addLots(com.google.common.collect.Lists.newArrayList(lot));

		Transaction transaction =
				InventoryDetailsTestDataInitializer.createDepositTransaction(5.0, 0, TransactionType.DEPOSIT.getValue(), lot, 1, 1, 1, LIST, "InventoryId");
		this.inventoryDataManager.addTransactions(Lists.newArrayList(transaction));

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

		final Lot lot =
			InventoryDetailsTestDataInitializer.createLot(1, GERMPLASM, germplasmId, 1, 8264, 0, 1, "Comments", "InventoryId");
		this.inventoryDataManager.addLots(com.google.common.collect.Lists.newArrayList(lot));

		final Transaction transaction =
				InventoryDetailsTestDataInitializer.createReservationTransaction(2.0, 0, TransactionType.DEPOSIT.getValue(), lot, 1, 1, 1, "LIST");
				InventoryDetailsTestDataInitializer.createReservationTransaction(2.0, 0, TransactionType.DEPOSIT.getValue(), lot, 1, 1, 1, LIST);
		this.inventoryDataManager.addTransactions(Lists.newArrayList(transaction));

		final Set<Integer> gids = this.lotDAO.getGermplasmsWithOpenLots(Lists.newArrayList(germplasm.getGid()));

		Assert.assertEquals(1, gids.size());
	}

	@Test
	public void testGetGermplasmsWithNoOpenLots() throws Exception {
		final Germplasm germplasm =
				GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		final Integer germplasmId = this.germplasmDataManager.addGermplasm(germplasm, germplasm.getPreferredName());

		final Lot lot =
			InventoryDetailsTestDataInitializer.createLot(1, GERMPLASM, germplasmId, 1, 8264, 1, 1, "Comments", "InventoryId");
		this.inventoryDataManager.addLots(com.google.common.collect.Lists.newArrayList(lot));

		final Transaction transaction =
				InventoryDetailsTestDataInitializer.createReservationTransaction(2.0, 0, TransactionType.DEPOSIT.getValue(), lot, 1, 1, 1, "LIST");
				InventoryDetailsTestDataInitializer.createReservationTransaction(2.0, 0, TransactionType.DEPOSIT.getValue(), lot, 1, 1, 1, LIST);
		this.inventoryDataManager.addTransactions(Lists.newArrayList(transaction));

		final Set<Integer> gids = this.lotDAO.getGermplasmsWithOpenLots(Lists.newArrayList(germplasm.getGid()));

		Assert.assertEquals(0, gids.size());
	}

	@Test
	public void testSearchAllLots() {

		final List<ExtendedLotDto> extendedLotDtos = lotDAO.searchLots(null, null);
		Assert.assertTrue(extendedLotDtos.size() >= 3);

	}

	@Test
	public void testSearchLotsByLotIds() {
		final LotsSearchDto lotsSearchDto = new LotsSearchDto();

		lotsSearchDto.setLotIds(Lists.newArrayList(lot1.getId(), lot2.getId()));

		final List<ExtendedLotDto> extendedLotDtos = lotDAO.searchLots(lotsSearchDto, null);
		Assert.assertEquals(extendedLotDtos.size(), 2);
	}

	@Test
	public void testSearchLotsByLocationIds() {
		final LotsSearchDto lotsSearchDto = new LotsSearchDto();
		lotsSearchDto.setLocationIds(Lists.newArrayList(location.getLocid()));
		final List<ExtendedLotDto> extendedLotDtos = lotDAO.searchLots(lotsSearchDto, null);

		Assert.assertEquals(extendedLotDtos.size(), 1);
	}

	@Test
	public void testSearchLotsByGids() {
		final LotsSearchDto lotsSearchDto = new LotsSearchDto();
		lotsSearchDto.setGids(Lists.newArrayList(germplasm1.getGid()));
		final List<ExtendedLotDto> extendedLotDtos = lotDAO.searchLots(lotsSearchDto, null);

		Assert.assertEquals(extendedLotDtos.size(), 2);
	}

	@Test
	public void testSearchLotsByGermplasmListIds() {
		final LotsSearchDto lotsSearchDto = new LotsSearchDto();
		lotsSearchDto.setGermplasmListIds(Lists.newArrayList(germplasmList.getId()));
		final List<ExtendedLotDto> extendedLotDtos = lotDAO.searchLots(lotsSearchDto, null);

		Assert.assertEquals(extendedLotDtos.size(), 2);
	}

	private void createLocationForSearchLotTest() {
		final String programUUID = RandomStringUtils.randomAlphabetic(16);
		final int ltype = 405;
		final String labbr = RandomStringUtils.randomAlphabetic(7);
		final String lname = RandomStringUtils.randomAlphabetic(9);

		final int cntryid = 1;
		location = LocationTestDataInitializer.createLocation(null, lname, ltype, labbr, programUUID);
		location.setCntryid(cntryid);

		final int provinceId = 1001;
		location.setSnl1id(provinceId);

		locationDAO.saveOrUpdate(location);

	}

	private void createDataForSearchLotsTest() {

		germplasm1 = GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		final Integer germplasmId1 = this.germplasmDataManager.addGermplasm(germplasm1, germplasm1.getPreferredName());

		germplasm2 = GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		final Integer germplasmId2 = this.germplasmDataManager.addGermplasm(germplasm2, germplasm2.getPreferredName());

		lot1 = InventoryDetailsTestDataInitializer
				.createLot(1, GERMPLASM, germplasmId1, location.getLocid(), 8264, 0, 1, "Comments", RandomStringUtils.randomAlphabetic(35));

		lot2 = InventoryDetailsTestDataInitializer.createLot(1, GERMPLASM, germplasmId1, 2, 8267, 0, 1, "Comments", RandomStringUtils.randomAlphabetic(35));

		lot3 = InventoryDetailsTestDataInitializer.createLot(1, GERMPLASM, germplasmId2, 1, 8267, 0, 1, "Comments", RandomStringUtils.randomAlphabetic(35));

		transaction1 = InventoryDetailsTestDataInitializer.createReservationTransaction(2.0, 0, TransactionType.DEPOSIT.getValue(), lot1, 1, 1, 1, LIST);

		transaction2 = InventoryDetailsTestDataInitializer.createReservationTransaction(2.0, 0, TransactionType.DEPOSIT.getValue(), lot2, 1, 1, 1, LIST);

		transaction3 = InventoryDetailsTestDataInitializer.createReservationTransaction(2.0, 0, TransactionType.DEPOSIT.getValue(), lot3, 1, 1, 1, LIST);

		this.inventoryDataManager.addTransactions(Lists.newArrayList(transaction1, transaction2, transaction3));

		this.inventoryDataManager.addLots(Lists.newArrayList(lot1, lot2, lot3));

		germplasmList = germplasmListDAO.save(GermplasmListTestDataInitializer
				.createGermplasmListTestData(RandomStringUtils.randomAlphabetic(6), RandomStringUtils.randomAlphabetic(6), 20141103, LST, 9999, 0,
						RandomStringUtils.randomAlphabetic(6), null));

		final GermplasmListData listData1 =
				new GermplasmListData(null, germplasmList, germplasm1.getGid(), 1, RandomStringUtils.randomAlphabetic(6), RandomStringUtils.randomAlphabetic(6),
						RandomStringUtils.randomAlphabetic(6), RandomStringUtils.randomAlphabetic(6), 0, 99995);

		this.manager.addGermplasmListData(listData1);

	}
}
