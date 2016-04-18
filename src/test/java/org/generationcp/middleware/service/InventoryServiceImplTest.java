
package org.generationcp.middleware.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.dao.GermplasmListDAO;
import org.generationcp.middleware.dao.GermplasmListDataDAO;
import org.generationcp.middleware.dao.LocationDAO;
import org.generationcp.middleware.dao.PersonDAO;
import org.generationcp.middleware.dao.ims.LotDAO;
import org.generationcp.middleware.dao.ims.StockTransactionDAO;
import org.generationcp.middleware.dao.ims.TransactionDAO;
import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.domain.gms.GermplasmListType;
import org.generationcp.middleware.domain.inventory.InventoryDetails;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.operation.builder.LotBuilder;
import org.generationcp.middleware.operation.builder.TransactionBuilder;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.ListDataProject;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.ims.EntityType;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.LotStatus;
import org.generationcp.middleware.pojos.ims.StockTransaction;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.generationcp.middleware.pojos.ims.TransactionStatus;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.util.Util;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.exceptions.verification.TooLittleActualInvocations;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class InventoryServiceImplTest {

	public static final String TEST_INVENTORY_ID = "TR1-123";

	private static final String TEST_LIST_NAME = "LIST-TEST";

	private static final int NUM_OF_LISTDATA_RECORDS = 10;

	private static final int TEST_GID = 201;

	private static final int TEST_LOCATION_ID = 10;

	private static final int TEST_SCALE_ID = 1;

	private static final String TEST_LOCATION_NAME = "LOC";

	private static final String TEST_SCALE_NAME = "SCALE";

	private static final String TEST_FULLNAME = "Test User";

	@Mock
	private HibernateSessionProvider sessionProvider;

	@Mock
	private InventoryDaoFactory inventoryDaoFactory;

	@Mock
	private LotDAO lotDAO;

	@Mock
	private TransactionDAO transactionDAO;

	@Mock
	private StockTransactionDAO stockTransactionDAO;

	@Mock
	private GermplasmListDAO germplasmListDAO;

	@Mock
	private GermplasmListDataDAO germplasmListDataDAO;

	@Mock
	private LocationDAO locationDAO;

	@Mock
	private CVTermDao cvTermDAO;

	@Mock
	private PersonDAO personDAO;

	@Mock
	private LotBuilder lotBuilder;

	@Mock
	private TransactionBuilder transactionBuilder;

	@InjectMocks
	private InventoryServiceImpl inventoryServiceImpl = new InventoryServiceImpl();

	@Before
	public void setup() {

		Mockito.when(this.inventoryDaoFactory.getTransactionDAO()).thenReturn(this.transactionDAO);
		Mockito.when(this.inventoryDaoFactory.getStockTransactionDAO()).thenReturn(this.stockTransactionDAO);
		Mockito.when(this.inventoryDaoFactory.getPersonDAO()).thenReturn(this.personDAO);
		Mockito.when(this.inventoryDaoFactory.getLotDao()).thenReturn(this.lotDAO);
		Mockito.when(this.inventoryDaoFactory.getLocationDAO()).thenReturn(this.locationDAO);
		Mockito.when(this.inventoryDaoFactory.getGermplasmListDataDAO()).thenReturn(this.germplasmListDataDAO);
		Mockito.when(this.inventoryDaoFactory.getGermplasmListDAO()).thenReturn(this.germplasmListDAO);
		Mockito.when(this.inventoryDaoFactory.getCvTermDao()).thenReturn(this.cvTermDAO);
	}

	@Test
	public void testGetCurrentNotificationNumber_NullInventoryIds() throws MiddlewareException {
		final String breederIdentifier = "TR";

		Mockito.doReturn(null).when(this.transactionDAO).getInventoryIDsWithBreederIdentifier(breederIdentifier);
		final Integer currentNotificationNumber = this.inventoryServiceImpl.getCurrentNotationNumberForBreederIdentifier(breederIdentifier);
		Assert.assertEquals(0, currentNotificationNumber.intValue());
	}

	@Test
	public void testGetCurrentNotificationNumber_EmptyInventoryIds() throws MiddlewareException {
		final String breederIdentifier = "TR";
		Mockito.doReturn(new ArrayList<String>()).when(this.transactionDAO).getInventoryIDsWithBreederIdentifier(breederIdentifier);
		final Integer currentNotificationNumber = this.inventoryServiceImpl.getCurrentNotationNumberForBreederIdentifier(breederIdentifier);
		Assert.assertEquals(0, currentNotificationNumber.intValue());
	}

	@Test
	public void testGetCurrentNotationNumberForBreederIdentifier_WithExisting() throws MiddlewareException {
		final List<String> inventoryIDs = new ArrayList<>();
		inventoryIDs.add("PRE1-12");
		inventoryIDs.add("PRE1-13");
		inventoryIDs.add("PRE1-14");
		inventoryIDs.add("PRE2-1");
		inventoryIDs.add("PRE3-1");
		inventoryIDs.add("PRE35-1");

		final String breederIdentifier = "PRE";
		Mockito.doReturn(inventoryIDs).when(this.transactionDAO).getInventoryIDsWithBreederIdentifier(breederIdentifier);
		final Integer currentNotationNumber = this.inventoryServiceImpl.getCurrentNotationNumberForBreederIdentifier(breederIdentifier);
		Assert.assertEquals(35, currentNotationNumber.intValue());

	}

	@Test
	public void testGetCurrentNotationNumberForBreederIdentifier_WithNoMatch() throws MiddlewareException {
		final List<String> inventoryIDs = new ArrayList<>();
		inventoryIDs.add("DUMMY1-1");

		final String breederIdentifier = "PRE";
		Mockito.doReturn(inventoryIDs).when(this.transactionDAO).getInventoryIDsWithBreederIdentifier(breederIdentifier);
		final Integer currentNotationNumber = this.inventoryServiceImpl.getCurrentNotationNumberForBreederIdentifier(breederIdentifier);
		Assert.assertEquals("0 must be returned because PRE is not found in DUMMY1-1", 0, currentNotationNumber.intValue());

	}

	@Test(expected = MiddlewareQueryException.class)
	public void testAddLotAndTransaction_LotAlreadyExists() {
		final InventoryDetails details = this.createInventoryDetailsTestData(1, 1, TEST_GID, TEST_LOCATION_ID, TEST_SCALE_ID);
		final GermplasmListData listData = this.createGermplasmListDataTestData();
		final ListDataProject listDataProject = this.createListDataProjectTestData();

		List<Lot> lots = new ArrayList<Lot>();
		lots.add(new Lot());
		Mockito.doReturn(lots)
				.when(this.lotDAO)
				.getByEntityTypeEntityIdsLocationIdAndScaleId(Mockito.anyString(), Matchers.anyListOf(Integer.class), Mockito.anyInt(),
						Mockito.anyInt());

		this.inventoryServiceImpl.addLotAndTransaction(details, listData, listDataProject);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testAddLotAndTransaction() {
		final InventoryDetails details = this.createInventoryDetailsTestData(1, 1, TEST_GID, TEST_LOCATION_ID, TEST_SCALE_ID);
		final GermplasmListData listData = this.createGermplasmListDataTestData();
		final ListDataProject listDataProject = this.createListDataProjectTestData();

		Mockito.doReturn(null).when(this.lotDAO)
				.getByEntityTypeEntityIdsLocationIdAndScaleId(Mockito.anyString(), Mockito.anyList(), Mockito.anyInt(), Mockito.anyInt());

		final Lot lot = this.createLotTestData(details);
		Mockito.doReturn(lot)
				.when(this.lotBuilder)
				.createLotForAdd(details.getGid(), details.getLocationId(), details.getScaleId(), details.getComment(), details.getUserId());
		Mockito.doReturn(1).when(this.lotDAO).saveOrUpdate(lot);

		final Transaction transaction = this.createTransactionTestData(lot, listData, details);
		Mockito.doReturn(transaction)
				.when(this.transactionBuilder)
				.buildForAdd(lot, listData.getId(), details.getAmount(), details.getUserId(), details.getComment(), details.getSourceId(),
						details.getInventoryID(), details.getBulkWith(), details.getBulkCompl());
		Mockito.doReturn(1).when(this.transactionDAO).saveOrUpdate(transaction);

		final StockTransaction stockTransaction = this.createStockTransactionTestData(listDataProject, transaction);
		Mockito.doReturn(1).when(this.stockTransactionDAO).saveOrUpdate(stockTransaction);

		this.inventoryServiceImpl.addLotAndTransaction(details, listData, listDataProject);

		try {
			Mockito.verify(this.lotDAO).saveOrUpdate(lot);
			Mockito.verify(this.transactionDAO).saveOrUpdate(transaction);
			Mockito.verify(this.stockTransactionDAO).saveOrUpdate(Mockito.any(StockTransaction.class));
		} catch (TooLittleActualInvocations e) {
			Assert.fail("Inventory lot, inventory transaction and stock transaction must be saved to the database");
		}
	}

	@Test
	public void testAddLotAndTransaction_NullListData() {
		final InventoryDetails details = this.createInventoryDetailsTestData(1, 1, TEST_GID, TEST_LOCATION_ID, TEST_SCALE_ID);
		final GermplasmListData listData = null;
		final ListDataProject listDataProject = this.createListDataProjectTestData();

		Mockito.doReturn(null)
				.when(this.lotDAO)
				.getByEntityTypeEntityIdsLocationIdAndScaleId(Mockito.anyString(), Matchers.anyListOf(Integer.class), Mockito.anyInt(),
						Mockito.anyInt());

		final Lot lot = this.createLotTestData(details);
		Mockito.doReturn(lot)
				.when(this.lotBuilder)
				.createLotForAdd(details.getGid(), details.getLocationId(), details.getScaleId(), details.getComment(), details.getUserId());
		Mockito.doReturn(1).when(this.lotDAO).saveOrUpdate(lot);

		final Transaction transaction = this.createTransactionTestData(lot, listData, details);
		Mockito.doReturn(transaction)
				.when(this.transactionBuilder)
				.buildForAdd(lot, 0, details.getAmount(), details.getUserId(), details.getComment(), details.getSourceId(),
						details.getInventoryID(), details.getBulkWith(), details.getBulkCompl());
		Mockito.doReturn(1).when(this.transactionDAO).saveOrUpdate(transaction);

		final StockTransaction stockTransaction = this.createStockTransactionTestData(listDataProject, transaction);
		Mockito.doReturn(1).when(this.stockTransactionDAO).saveOrUpdate(stockTransaction);

		this.inventoryServiceImpl.addLotAndTransaction(details, listData, listDataProject);

		try {
			Mockito.verify(this.lotDAO).saveOrUpdate(lot);
			Mockito.verify(this.transactionDAO).saveOrUpdate(transaction);
			Mockito.verify(this.stockTransactionDAO).saveOrUpdate(Mockito.any(StockTransaction.class));
		} catch (TooLittleActualInvocations e) {
			Assert.fail("Inventory lot, inventory transaction and stock transaction must be saved to the database");
		}
	}

	private StockTransaction createStockTransactionTestData(final ListDataProject listDataProject, final Transaction transaction) {
		final StockTransaction stockTransaction = new StockTransaction(null, listDataProject, transaction);
		stockTransaction.setSourceRecordId(transaction.getSourceRecordId());
		return stockTransaction;
	}

	private Transaction createTransactionTestData(final Lot lot, final GermplasmListData listData, final InventoryDetails details) {
		final Transaction transaction =
				new Transaction(null, details.getUserId(), lot, Util.getCurrentDateAsIntegerValue(),
						TransactionStatus.ANTICIPATED.getIntValue(),
						Double.valueOf(new DecimalFormat("#.000").format(details.getAmount())), details.getComment(), 0,
						EntityType.LIST.name(), details.getSourceId(), listData == null ? 0 : listData.getId(), 0d, 1,
						details.getInventoryID());

		transaction.setBulkCompl(details.getBulkCompl());
		transaction.setBulkWith(details.getBulkWith());

		return transaction;
	}

	private Lot createLotTestData(final InventoryDetails details) {
		return new Lot(null, details.getUserId(), EntityType.GERMPLSM.name(), details.getGid(), details.getLocationId(),
				details.getScaleId(), LotStatus.ACTIVE.getIntValue(), 0, details.getComment());
	}

	private ListDataProject createListDataProjectTestData() {
		final ListDataProject listDataProject = new ListDataProject();
		return listDataProject;
	}

	private GermplasmListData createGermplasmListDataTestData() {
		final GermplasmListData germplasmListData = new GermplasmListData();
		germplasmListData.setId(1);
		return germplasmListData;
	}

	private InventoryDetails createInventoryDetailsTestData(Integer listId, int listDataId, Integer gid, Integer locationId, Integer scaleId) {
		final InventoryDetails inventoryDetails = new InventoryDetails();
		inventoryDetails.setGid(gid);
		inventoryDetails.setLocationId(locationId);
		inventoryDetails.setScaleId(scaleId);
		inventoryDetails.setComment("TEST");
		inventoryDetails.setUserId(1);
		inventoryDetails.setAmount(20d);
		inventoryDetails.setSourceId(listId);
		inventoryDetails.setSourceRecordId(listDataId);
		inventoryDetails.setInventoryID("SID1-1");
		inventoryDetails.setBulkCompl("Y");
		inventoryDetails.setBulkCompl("SID1-2");
		return inventoryDetails;
	}

	@Test
	public void testRetrieveInventoryDetailsForListDataProjectListId() throws MiddlewareQueryException {
		final Integer listId = 1;
		final List<InventoryDetails> expectedInventoryDetailsList = this.createInventoryDetailsListTestData(listId);
		Mockito.doReturn(expectedInventoryDetailsList).when(this.stockTransactionDAO)
				.retrieveInventoryDetailsForListDataProjectListId(listId);
		final List<InventoryDetails> inventoryDetailsList = this.inventoryServiceImpl.getInventoryListByListDataProjectListId(listId);
		for (final InventoryDetails inventoryDetails : inventoryDetailsList) {
			Assert.assertEquals("All inventory details must be belong to the list with id " + listId, listId,
					inventoryDetails.getSourceId());
		}
	}

	private List<InventoryDetails> createInventoryDetailsListTestData(Integer listId) {
		List<InventoryDetails> inventoryDetailsList = new ArrayList<>();
		inventoryDetailsList.add(this.createInventoryDetailsTestData(listId, 1, TEST_GID, TEST_LOCATION_ID, TEST_SCALE_ID));
		return inventoryDetailsList;
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRetrieveInventoryDetailsForListDataProjectListId_WrongListType() throws MiddlewareQueryException {
		final Integer listId = 1;
		Mockito.doThrow(IllegalArgumentException.class).when(this.stockTransactionDAO)
				.retrieveInventoryDetailsForListDataProjectListId(listId);
		this.inventoryServiceImpl.getInventoryListByListDataProjectListId(listId);
	}

	@Test
	public void testStockHasCompletedBulking() throws MiddlewareQueryException {
		final Integer listId = 1;
		Mockito.doReturn(true).when(this.stockTransactionDAO).stockHasCompletedBulking(listId);
		Assert.assertEquals("Bulking of stocks should be completed", true, this.inventoryServiceImpl.stockHasCompletedBulking(listId));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetInventoryDetailsByGermplasmList_Advanced() {
		final Integer advanceListId = 2;
		final Integer lstListId = 1;
		GermplasmList germplasmList = this.createGermplasmListTestData(advanceListId, GermplasmListType.ADVANCED.name());
		List<GermplasmListData> germplasmListDataList = this.createGermplasmListDataListTestData(germplasmList);
		List<Integer> germplasmListDataIDList = this.getAllGermplasmListDataIDs(germplasmListDataList);
		List<InventoryDetails> inventoryDetailsList = this.createInventoryDetailsListTestData(advanceListId, germplasmListDataIDList);
		List<Location> locationList = this.createLocationListTestData();
		List<CVTerm> scaleList = this.createScaleListTestData();
		Map<Integer, String> usernameList = this.createUsernameMapTestData();

		Mockito.doReturn(germplasmList).when(this.germplasmListDAO).getById(advanceListId);
		Mockito.doReturn(lstListId).when(this.germplasmListDAO).getListDataListIDFromListDataProjectListID(advanceListId);
		Mockito.doReturn(germplasmListDataList).when(this.germplasmListDataDAO).getByListId(lstListId);
		Mockito.doReturn(inventoryDetailsList).when(this.transactionDAO).getInventoryDetailsByTransactionRecordId(germplasmListDataIDList);
		Mockito.doReturn(locationList).when(this.locationDAO).getByIds(Mockito.anyList());
		Mockito.doReturn(scaleList).when(this.cvTermDAO).getByIds(Mockito.anyList());
		Mockito.doReturn(usernameList).when(this.personDAO).getPersonNamesByUserIds(Mockito.anyList());

		List<InventoryDetails> result = this.inventoryServiceImpl.getInventoryDetailsByGermplasmList(advanceListId);

		Assert.assertNotNull(result);
		Assert.assertEquals(NUM_OF_LISTDATA_RECORDS, result.size());
		for (InventoryDetails inventoryDetails : result) {
			Assert.assertEquals("Inventory source name should be " + germplasmList.getName(), germplasmList.getName(),
					inventoryDetails.getSourceName());
			Assert.assertEquals("Inventory source id should be " + germplasmList.getId(), germplasmList.getId(),
					inventoryDetails.getSourceId());
			Assert.assertEquals("Location name should be " + TEST_LOCATION_NAME + inventoryDetails.getLocationId(), TEST_LOCATION_NAME
					+ inventoryDetails.getLocationId(), inventoryDetails.getLocationName());
			Assert.assertEquals("Scale name should be " + TEST_SCALE_NAME + inventoryDetails.getScaleId(), TEST_SCALE_NAME
					+ inventoryDetails.getScaleId(), inventoryDetails.getScaleName());
			Assert.assertEquals("User name must be " + TEST_FULLNAME, TEST_FULLNAME, inventoryDetails.getUserName());
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetInventoryDetailsByGermplasmList_Crosses() {
		final Integer crossesId = 2;
		final Integer lstListId = 1;
		GermplasmList germplasmList = this.createGermplasmListTestData(crossesId, GermplasmListType.CROSSES.name());
		List<GermplasmListData> germplasmListDataList = this.createGermplasmListDataListTestData(germplasmList);
		List<Integer> germplasmListDataIDList = this.getAllGermplasmListDataIDs(germplasmListDataList);
		List<InventoryDetails> inventoryDetailsList = this.createInventoryDetailsListTestData(crossesId, germplasmListDataIDList);
		List<Location> locationList = this.createLocationListTestData();
		List<CVTerm> scaleList = this.createScaleListTestData();
		Map<Integer, String> usernameList = this.createUsernameMapTestData();

		Mockito.doReturn(germplasmList).when(this.germplasmListDAO).getById(crossesId);
		Mockito.doReturn(lstListId).when(this.germplasmListDAO).getListDataListIDFromListDataProjectListID(crossesId);
		Mockito.doReturn(germplasmListDataList).when(this.germplasmListDataDAO).getByListId(lstListId);
		Mockito.doReturn(inventoryDetailsList).when(this.transactionDAO).getInventoryDetailsByTransactionRecordId(germplasmListDataIDList);
		Mockito.doReturn(locationList).when(this.locationDAO).getByIds(Mockito.anyList());
		Mockito.doReturn(scaleList).when(this.cvTermDAO).getByIds(Mockito.anyList());
		Mockito.doReturn(usernameList).when(this.personDAO).getPersonNamesByUserIds(Mockito.anyList());

		List<InventoryDetails> result =
				this.inventoryServiceImpl.getInventoryDetailsByGermplasmList(crossesId, GermplasmListType.CROSSES.name());

		Assert.assertNotNull(result);
		Assert.assertEquals(NUM_OF_LISTDATA_RECORDS, result.size());
		for (InventoryDetails inventoryDetails : result) {
			Assert.assertEquals("Inventory source name should be " + germplasmList.getName(), germplasmList.getName(),
					inventoryDetails.getSourceName());
			Assert.assertEquals("Inventory source id should be " + germplasmList.getId(), germplasmList.getId(),
					inventoryDetails.getSourceId());
			Assert.assertEquals("Location name should be " + TEST_LOCATION_NAME + inventoryDetails.getLocationId(), TEST_LOCATION_NAME
					+ inventoryDetails.getLocationId(), inventoryDetails.getLocationName());
			Assert.assertEquals("Scale name should be " + TEST_SCALE_NAME + inventoryDetails.getScaleId(), TEST_SCALE_NAME
					+ inventoryDetails.getScaleId(), inventoryDetails.getScaleName());
			Assert.assertEquals("User name must be " + TEST_FULLNAME, TEST_FULLNAME, inventoryDetails.getUserName());
		}
	}

	private Map<Integer, String> createUsernameMapTestData() {
		Map<Integer, String> usernamesMap = new HashMap<>();
		usernamesMap.put(1, TEST_FULLNAME);
		return usernamesMap;
	}

	private List<CVTerm> createScaleListTestData() {
		List<CVTerm> scaleList = new ArrayList<>();
		int lastScaleId = TEST_SCALE_ID + NUM_OF_LISTDATA_RECORDS;
		for (int scaleId = TEST_SCALE_ID; scaleId <= lastScaleId; scaleId++) {
			String scaleName = TEST_SCALE_NAME + scaleId;
			scaleList.add(this.createScaleTestData(scaleId, scaleName));
		}
		return scaleList;
	}

	private CVTerm createScaleTestData(int scaleId, String scaleName) {
		CVTerm scale = new CVTerm();
		scale.setCvTermId(scaleId);
		scale.setName(scaleName);
		return scale;
	}

	private List<Location> createLocationListTestData() {
		List<Location> locationList = new ArrayList<>();
		int lastLocationId = TEST_LOCATION_ID + NUM_OF_LISTDATA_RECORDS;
		for (int locationId = TEST_LOCATION_ID; locationId <= lastLocationId; locationId++) {
			String locationName = TEST_LOCATION_NAME + locationId;
			locationList.add(this.createLocationTestData(locationId, locationName));
		}
		return locationList;
	}

	private Location createLocationTestData(int locationId, String locationName) {
		Location location = new Location();
		location.setLocid(locationId);
		location.setLname(locationName);
		return location;
	}

	private List<InventoryDetails> createInventoryDetailsListTestData(Integer listId, List<Integer> germplasmListDataIDList) {
		List<InventoryDetails> inventoryDetailsList = new ArrayList<>();
		int gid = TEST_GID;
		int locationId = TEST_LOCATION_ID;
		int scaleId = TEST_SCALE_ID;
		for (Integer listDataId : germplasmListDataIDList) {
			inventoryDetailsList.add(this.createInventoryDetailsTestData(listId, listDataId, gid, locationId, scaleId));
			gid++;
			locationId++;
			scaleId++;
		}
		return inventoryDetailsList;
	}

	private List<Integer> getAllGermplasmListDataIDs(List<GermplasmListData> germplasmListDataList) {
		List<Integer> germplasmListDataIDList = new ArrayList<>();
		for (GermplasmListData datum : germplasmListDataList) {
			if (datum != null) {
				germplasmListDataIDList.add(datum.getId());
			}
		}
		return germplasmListDataIDList;
	}

	private List<GermplasmListData> createGermplasmListDataListTestData(GermplasmList germplasmList) {
		List<GermplasmListData> germplasmListDataList = new ArrayList<>();
		for (int listDataId = 1; listDataId <= NUM_OF_LISTDATA_RECORDS; listDataId++) {
			germplasmListDataList.add(this.createGermplasmListDataTestData(listDataId, germplasmList));
		}
		return germplasmListDataList;
	}

	private GermplasmListData createGermplasmListDataTestData(Integer id, GermplasmList germplasmList) {
		GermplasmListData germplasmListData = new GermplasmListData(id);
		germplasmListData.setList(germplasmList);
		return germplasmListData;
	}

	private GermplasmList createGermplasmListTestData(Integer listId, String germplasmListType) {
		GermplasmList germplasmList = new GermplasmList(listId);
		germplasmList.setType(germplasmListType);
		germplasmList.setName(TEST_LIST_NAME);
		return germplasmList;
	}

	@Test
	public void testGetGermplasmListData_TypeLST() {
		final Integer listId = 1;
		String germplasmListType = GermplasmListType.LST.name();
		GermplasmList germplasmList = this.createGermplasmListTestData(listId, germplasmListType);

		this.inventoryServiceImpl.getGermplasmListData(germplasmList, germplasmListType);

		Mockito.verify(this.germplasmListDataDAO).getByListId(listId);
	}

	@Test
	public void testGetGermplasmListData_TypeNotLST() {
		final Integer listId = 1;
		String germplasmListType = GermplasmListType.CROSSES.name();
		GermplasmList germplasmList = this.createGermplasmListTestData(listId, germplasmListType);
		final Integer listDataListId = 2;

		Mockito.doReturn(listDataListId).when(this.germplasmListDAO).getListDataListIDFromListDataProjectListID(listId);

		this.inventoryServiceImpl.getGermplasmListData(germplasmList, germplasmListType);

		Mockito.verify(this.germplasmListDataDAO).getByListId(listDataListId);
	}

}
