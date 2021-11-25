
package org.generationcp.middleware.dao.ims;

import com.google.common.collect.Lists;
import org.generationcp.middleware.DataSetupTest;
import org.generationcp.middleware.GermplasmTestDataGenerator;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.api.inventory.study.StudyTransactionsDto;
import org.generationcp.middleware.dao.NameDAO;
import org.generationcp.middleware.dao.germplasmlist.GermplasmListDataDAO;
import org.generationcp.middleware.data.initializer.GermplasmListTestDataInitializer;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.data.initializer.InventoryDetailsTestDataInitializer;
import org.generationcp.middleware.domain.dms.Experiment;
import org.generationcp.middleware.domain.inventory.manager.ExtendedLotDto;
import org.generationcp.middleware.domain.inventory.manager.TransactionDto;
import org.generationcp.middleware.domain.inventory.manager.TransactionsSearchDto;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.ims.ExperimentTransaction;
import org.generationcp.middleware.pojos.ims.ExperimentTransactionType;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.generationcp.middleware.pojos.ims.TransactionStatus;
import org.generationcp.middleware.pojos.ims.TransactionType;
import org.generationcp.middleware.pojos.report.TransactionReportRow;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.DataImportService;
import org.generationcp.middleware.service.api.FieldbookService;
import org.generationcp.middleware.service.api.user.UserService;
import org.generationcp.middleware.service.impl.user.UserServiceImpl;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class TransactionDAOTest extends IntegrationTestBase {

	@Autowired
	private GermplasmDataManager germplasmDataManager;

	@Autowired
	private GermplasmListManager germplasmListManager;

	@Autowired
	private InventoryDataManager inventoryDataManager;

	@Autowired
	private StudyDataManager studyDataManager;

	@Autowired
	private DataImportService dataImportService;

	@Autowired
	private FieldbookService middlewareFieldbookService;

	@Autowired
	private WorkbenchTestDataUtil workbenchTestDataUtil;

	private TransactionDAO transactionDAO;
	private GermplasmListDataDAO germplasmListDataDAO;
	private ExperimentTransactionDAO experimentTransactionDAO;

	private static final int LIST_ID = 1;
	private static final String STOCK_ID_PREFIX = "STOCK";
	private static final int LOCATION_ID = 1;
	private static final int UNIT_ID = 1;

	private static final String LOT_DISCARD = "Discard" ;
	private static final String LOT_DEPOSIT = TransactionType.DEPOSIT.getValue();

	private final Map<Integer, Germplasm> germplasmMap = new HashMap<Integer, Germplasm>();

	private List<GermplasmListData> germplasmListData;

	private InventoryDetailsTestDataInitializer inventoryDetailsTestDataInitializer;
	private DataSetupTest dataSetupTest;
	private GermplasmTestDataGenerator germplasmTestDataGenerator;
	private Project commonTestProject;

	private Integer studyId;
	private Integer observationDatasetId;
	private Integer germplasmListId;
	private final Map<Integer, Transaction> listDataIdTransactionMap = new HashMap<>();
	private UserService userService;

	@Before
	public void setUp() throws Exception {
		this.transactionDAO = new TransactionDAO();
		this.transactionDAO.setSession(this.sessionProvder.getSession());

		this.germplasmListDataDAO = new GermplasmListDataDAO();
		this.germplasmListDataDAO.setSession(this.sessionProvder.getSession());

		this.experimentTransactionDAO = new ExperimentTransactionDAO();
		this.experimentTransactionDAO.setSession(this.sessionProvder.getSession());

		this.germplasmListData = Lists.newArrayList();

		this.inventoryDetailsTestDataInitializer = new InventoryDetailsTestDataInitializer();

		this.dataSetupTest = new DataSetupTest();
		this.dataSetupTest.setDataImportService(this.dataImportService);
		this.dataSetupTest.setGermplasmListManager(this.germplasmListManager);
		this.dataSetupTest.setMiddlewareFieldbookService(this.middlewareFieldbookService);

		if (this.commonTestProject == null) {
			this.commonTestProject = this.workbenchTestDataUtil.getCommonTestProject();
		}

		//		this.testDataInitializer = new IntegrationTestDataInitializer(this.sessionProvder, this.workbenchSessionProvider);
		this.initializeGermplasms(1);
		this.initializeGermplasmsListAndListData(this.germplasmMap);
		this.initLotsAndTransactions(this.germplasmListId);

		this.userService = new UserServiceImpl(this.workbenchSessionProvider);

		if (this.germplasmTestDataGenerator == null) {
			this.germplasmTestDataGenerator = new GermplasmTestDataGenerator(this.germplasmDataManager, new NameDAO(this.sessionProvder
					.getSession()));
		}

		if (this.studyId == null) {
			this.createTestStudyWithObservationDataset();
		}
	}

	public UserService getUserService() {
		return this.userService;
	}

	public void setUserService(final UserService userService) {
		this.userService = userService;
	}

	@Test
	public void testRetrieveStockIds() {

		final List<Integer> lRecIDs = new ArrayList<>();
		lRecIDs.add(1);
		final Map<Integer, String> lRecIDStockIDMap = this.transactionDAO.retrieveStockIds(lRecIDs);
		Assert.assertNotNull(lRecIDStockIDMap);
	}

	private void initializeGermplasms(final int noOfEntries) {
		final CropType cropType = new CropType();
		cropType.setUseUUID(false);
		for (int i = 1; i <= noOfEntries; i++) {
			final Germplasm germplasm = GermplasmTestDataInitializer.createGermplasm(i);
			final Integer gidAfterAdd = this.germplasmDataManager.addGermplasm(germplasm, germplasm.getPreferredName(), cropType);
			this.germplasmMap.put(gidAfterAdd, germplasm);
		}
	}

	private void initializeGermplasmsListAndListData(final Map<Integer, Germplasm> germplasmMap) {
		final GermplasmList germplasmList = GermplasmListTestDataInitializer.createGermplasmList(LIST_ID, false);
		this.germplasmListId = this.germplasmListManager.addGermplasmList(germplasmList);

		final List<GermplasmListData> germplasmListData =
				GermplasmListTestDataInitializer.createGermplasmListData(germplasmList, new ArrayList<>(germplasmMap.keySet()), false);
		this.germplasmListManager.addGermplasmListData(germplasmListData);
	}


	private void initLotsAndTransactions(final Integer germplasmListId) {

		final List<Lot> lots =
				this.inventoryDetailsTestDataInitializer.createLots(new ArrayList<>(this.germplasmMap.keySet()), germplasmListId, UNIT_ID,
						LOCATION_ID);

		final List<Integer> lotIds = this.inventoryDataManager.addLots(lots);

		lots.clear();

		final Map<Integer, Integer> lotIdLrecIdMap = new HashMap<>();
		final Map<Integer, Integer> gidLotIdMap = new HashMap<>();

		this.germplasmListData = this.germplasmListManager.getGermplasmListDataByListId(germplasmListId);

		for (final Integer lotId : lotIds) {
			final Lot lot = this.inventoryDataManager.getLotById(lotId);
			lots.add(lot);
			gidLotIdMap.put(lot.getEntityId(), lotId);
		}


		for (final GermplasmListData listEntry : this.germplasmListData) {
			final Integer gid = listEntry.getGermplasmId();
			lotIdLrecIdMap.put(gidLotIdMap.get(gid), listEntry.getId());
		}


		final List<Transaction> listTransaction =
				this.inventoryDetailsTestDataInitializer.createReservedTransactions(lots, this.germplasmListId, lotIdLrecIdMap, STOCK_ID_PREFIX);

		final List<Integer> transactionIds = this.inventoryDataManager.addTransactions(listTransaction);

		for (final Integer transactionId : transactionIds) {
			final Transaction transaction = this.inventoryDataManager.getTransactionById(transactionId);
			this.listDataIdTransactionMap.put(transaction.getSourceRecordId(), transaction);
		}

	}

	@Test
	public void testGetTransactionDetailsForLot() throws ParseException {
		final CropType cropType = new CropType();
		cropType.setUseUUID(false);
		final Germplasm germplasm =
			GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		final Integer germplasmId = this.germplasmDataManager.addGermplasm(germplasm, germplasm.getPreferredName(), cropType);

		final WorkbenchUser user = this.getUserService().getUserById(1);

		final Lot lot = InventoryDetailsTestDataInitializer.createLot(user.getUserid(), "GERMPLSM", germplasmId, 1, 8264, 0, 1, "Comments",
			"InventoryId");
		this.inventoryDataManager.addLots(com.google.common.collect.Lists.newArrayList(lot));

		final String sDate1 = "01/01/2015";
		final Date date1 = new SimpleDateFormat("dd/MM/yyyy").parse(sDate1);
		final Transaction depositTransaction =
			InventoryDetailsTestDataInitializer
				.createTransaction(5.0, 0, TransactionType.DEPOSIT.getValue(), lot, 1, 1, 1, "LIST",
					TransactionType.DEPOSIT.getId());
		depositTransaction.setTransactionDate(date1);
		depositTransaction.setUserId(user.getUserid());

		final String sDate2 = "10/10/2015";
		final Date date2 = new SimpleDateFormat("dd/MM/yyyy").parse(sDate2);
		final Transaction closedTransaction =
			InventoryDetailsTestDataInitializer
				.createTransaction(-5.0, 1, "Discard", lot, 1, 1, 1, "LIST", TransactionType.DISCARD.getId());
		closedTransaction.setTransactionDate(date2);
		closedTransaction.setUserId(user.getUserid());

		final List<Transaction> transactionList = new ArrayList<>();
		transactionList.add(depositTransaction);
		transactionList.add(closedTransaction);
		this.inventoryDataManager.addTransactions(transactionList);

		final List<TransactionReportRow> transactionReportRows = this.transactionDAO.getTransactionDetailsForLot(lot.getId());

		for (final TransactionReportRow reportRow : transactionReportRows) {
			if (LOT_DEPOSIT.equals(reportRow.getLotStatus())) {

				Assert.assertEquals(depositTransaction.getQuantity(), reportRow.getQuantity());
				Assert.assertEquals(LOT_DEPOSIT, reportRow.getLotStatus());
				Assert.assertEquals(depositTransaction.getTransactionDate(), reportRow.getDate());
				Assert.assertEquals(depositTransaction.getComments(), reportRow.getCommentOfLot());

			}
			if (LOT_DISCARD.equals(reportRow.getLotStatus())) {
				Assert.assertEquals(closedTransaction.getTransactionDate(), reportRow.getDate());
				Assert.assertEquals(closedTransaction.getComments(), reportRow.getCommentOfLot());
				Assert.assertEquals(closedTransaction.getQuantity(), reportRow.getQuantity());
				Assert.assertEquals(LOT_DISCARD, reportRow.getLotStatus());
			}
			Assert.assertEquals(user.getUserid(), reportRow.getUserId());
		}

	}

	@Test
	public void testSearchTransactions() throws ParseException {
		final CropType cropType = new CropType();
		cropType.setUseUUID(false);

		final Germplasm germplasm =
			GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0, 1,
				1, 0, 1, 1, "MethodName", "LocationName");
		final Integer germplasmId = this.germplasmDataManager.addGermplasm(germplasm, germplasm.getPreferredName(), cropType);

		final WorkbenchUser user = this.getUserService().getUserById(1);

		final Lot lot = InventoryDetailsTestDataInitializer.createLot(user.getUserid(), "GERMPLSM", germplasmId, 1,
			8264, 0, 1, "Comments", "ABC-1");

		this.inventoryDataManager.addLots(com.google.common.collect.Lists.newArrayList(lot));

		final String sDate1 = "01/01/2015";
		final Date date1 = new SimpleDateFormat("dd/MM/yyyy").parse(sDate1);
		final Transaction depositTransaction =
			InventoryDetailsTestDataInitializer.createTransaction(5.0, 0, "Deposit", lot, 1,
				1, 1, "LIST", TransactionType.DEPOSIT.getId());
		depositTransaction.setType(TransactionType.DEPOSIT.getId());
		depositTransaction.setTransactionDate(date1);
		depositTransaction.setUserId(user.getUserid());

		final String sDate2 = "10/10/2015";
		final Date date2 = new SimpleDateFormat("dd/MM/yyyy").parse(sDate2);
		final Transaction closedTransaction =
			InventoryDetailsTestDataInitializer.createTransaction(-5.0, 1, "Discard", lot, 1,
				1, 1, "LIST", TransactionType.DEPOSIT.getId());
		closedTransaction.setType(TransactionType.DISCARD.getId());
		closedTransaction.setTransactionDate(date2);
		closedTransaction.setUserId(user.getUserid());

		final List<Transaction> transactionList = new ArrayList<>();
		transactionList.add(depositTransaction);
		transactionList.add(closedTransaction);
		this.inventoryDataManager.addTransactions(transactionList);
		final TransactionsSearchDto transactionsSearchDto = new TransactionsSearchDto();
		transactionsSearchDto.setDesignation(germplasm.getPreferredName().getNval());
		transactionsSearchDto.setGids(Lists.newArrayList(germplasmId));
		transactionsSearchDto.setLotIds(Lists.newArrayList(lot.getId()));
		transactionsSearchDto.setMaxAmount(10.0);
		transactionsSearchDto.setMinAmount(-10.0);
		transactionsSearchDto.setNotes("Deposit");
		transactionsSearchDto.setUnitIds(Lists.newArrayList(8264));
		transactionsSearchDto.setStockId("ABC-1");
		transactionsSearchDto.setCreatedDateFrom(date1);
		transactionsSearchDto.setCreatedDateTo(date1);
		transactionsSearchDto.setTransactionIds(Lists.newArrayList(depositTransaction.getId(), closedTransaction.getId()));
		transactionsSearchDto.setTransactionTypes(Lists.newArrayList(TransactionType.DEPOSIT.getId()));
		transactionsSearchDto.setCreatedByUsername(user.getName());

		final List<TransactionDto> transactionDtos = this.transactionDAO.searchTransactions(transactionsSearchDto, null);

		for (final TransactionDto transactionDto : transactionDtos) {
			Assert.assertTrue(transactionDto.getLot().getDesignation().equalsIgnoreCase(germplasm.getPreferredName().getNval()));
			Assert.assertTrue(transactionDto.getLot().getGid().equals(germplasmId));
			Assert.assertTrue(transactionDto.getLot().getLotId().equals(lot.getId()));
			Assert.assertTrue(transactionDto.getAmount() <= 10.0);
			Assert.assertTrue(transactionDto.getAmount() >= -10.0);
			Assert.assertTrue(transactionDto.getNotes().equalsIgnoreCase("Deposit"));
			Assert.assertTrue(transactionDto.getLot().getUnitId().equals(8264));
			Assert.assertTrue(transactionDto.getLot().getStockId().equalsIgnoreCase("ABC-1"));
			Assert.assertTrue(transactionDto.getCreatedDate().equals(date1));
			Assert.assertTrue(transactionDto.getTransactionId().equals(depositTransaction.getId()));
			Assert.assertTrue(transactionDto.getTransactionType().equalsIgnoreCase("Deposit"));
			Assert.assertTrue(transactionDto.getCreatedByUsername().equalsIgnoreCase(user.getName()));
			Assert.assertTrue(transactionDto.getLot().getLocationName().equalsIgnoreCase("Afghanistan"));
			Assert.assertTrue(transactionDto.getLot().getLocationAbbr().equalsIgnoreCase("AFG"));
		}
	}

	@Test
  public void testGetStudyTransactionByTransactionId() throws ParseException {
		final CropType cropType = new CropType();
		cropType.setUseUUID(false);

		final Germplasm germplasm =
				GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0, 1,
						1, 0, 1, 1, "MethodName", "LocationName");
		final Integer germplasmId = this.germplasmDataManager.addGermplasm(germplasm, germplasm.getPreferredName(), cropType);

		final WorkbenchUser user = this.getUserService().getUserById(1);

		final Lot lot = InventoryDetailsTestDataInitializer.createLot(user.getUserid(), "GERMPLSM", germplasmId, 1,
				TermId.SEED_AMOUNT_G.getId(), 0, 1, "Comments", "ABC-1");

		this.inventoryDataManager.addLots(com.google.common.collect.Lists.newArrayList(lot));

		final String sDate1 = "01/01/2015";
		final Date date1 = new SimpleDateFormat("dd/MM/yyyy").parse(sDate1);
		final double transactionAmount = 5.0d;
		final String transactionNote = "Transaction deposit";
		final Transaction depositTransaction =
				InventoryDetailsTestDataInitializer.createTransaction(transactionAmount, 0, transactionNote, lot, 1,
						1, 1, "LIST", TransactionType.DEPOSIT.getId());
		depositTransaction.setType(TransactionType.DEPOSIT.getId());
		depositTransaction.setTransactionDate(date1);
		depositTransaction.setUserId(user.getUserid());

		final List<Transaction> transactionList = new ArrayList<>();
		transactionList.add(depositTransaction);
		this.inventoryDataManager.addTransactions(transactionList);

		final List<Experiment> experiments = this.studyDataManager.getExperiments(this.observationDatasetId, 0, 40);

		final ExperimentTransaction experimentTransaction =
				new ExperimentTransaction(new ExperimentModel(experiments.get(0).getId()), depositTransaction,
						ExperimentTransactionType.PLANTING.getId());
		this.experimentTransactionDAO.save(experimentTransaction);

		final StudyTransactionsDto studyTransactionsDto =
				this.transactionDAO.getStudyTransactionByTransactionId(depositTransaction.getId());
		assertNotNull(studyTransactionsDto);
		assertThat(studyTransactionsDto.getTransactionId(), is(depositTransaction.getId()));
		assertThat(studyTransactionsDto.getCreatedByUsername(), is(ADMIN_NAME));
		assertThat(studyTransactionsDto.getTransactionType(), is(TransactionType.DEPOSIT.getValue()));
		assertThat(studyTransactionsDto.getTransactionStatus(), is(TransactionStatus.PENDING.getValue()));
		assertThat(studyTransactionsDto.getAmount(), is(transactionAmount));
		assertThat(studyTransactionsDto.getNotes(), is(transactionNote));
		assertNotNull(studyTransactionsDto.getCreatedDate());
		assertThat(studyTransactionsDto.getCreatedDate().toString(), is("2015-01-01"));
		assertThat(studyTransactionsDto.getExperimentTransactionType(), is(ExperimentTransactionType.PLANTING));

		final List<StudyTransactionsDto.ObservationUnitDto> observationUnits = studyTransactionsDto.getObservationUnits();
		assertThat(observationUnits, Matchers.hasSize(1));

		final StudyTransactionsDto.ObservationUnitDto observationUnitDto = observationUnits.get(0);
		assertThat(observationUnitDto.getNdExperimentId(), is(experiments.get(0).getId()));
		assertThat(observationUnitDto.getTransactionId(), is(depositTransaction.getId()));

		final ExtendedLotDto actualLot = studyTransactionsDto.getLot();
		assertNotNull(actualLot);
		assertNotNull(actualLot.getDesignation());
		assertThat(actualLot.getUnitName().toLowerCase(), is(TermId.SEED_AMOUNT_G.name().toLowerCase()));
		assertThat(actualLot.getLotId(), is(lot.getId()));
		assertThat(actualLot.getLotUUID(), is(lot.getLotUuId()));
		assertThat(actualLot.getStockId(), is(lot.getStockId()));
		assertThat(actualLot.getLocationId(), is(lot.getLocationId()));
		assertThat(actualLot.getNotes(), is(lot.getComments()));
		assertThat(actualLot.getGid(), is(germplasm.getGid()));
		assertThat(actualLot.getGermplasmUUID(), is(germplasm.getGermplasmUUID()));
	}

	private void createTestStudyWithObservationDataset() {
		final Germplasm parentGermplasm = this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames();

		final Integer[] gids = this.germplasmTestDataGenerator
				.createChildrenGermplasm(DataSetupTest.NUMBER_OF_GERMPLASM, "PREFF", parentGermplasm);

		this.studyId = this.dataSetupTest.createNurseryForGermplasm(this.commonTestProject.getUniqueID(), gids, "ABCD");
		this.observationDatasetId = studyDataManager.getDataSetsByType(this.studyId, DatasetTypeEnum.PLOT_DATA.getId()).get(0).getId();
	}


}
