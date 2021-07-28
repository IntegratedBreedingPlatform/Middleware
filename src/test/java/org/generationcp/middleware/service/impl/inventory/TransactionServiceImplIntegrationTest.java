package org.generationcp.middleware.service.impl.inventory;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.domain.inventory.manager.ExtendedLotDto;
import org.generationcp.middleware.domain.inventory.manager.LotDepositRequestDto;
import org.generationcp.middleware.domain.inventory.manager.LotsSearchDto;
import org.generationcp.middleware.domain.inventory.manager.TransactionDto;
import org.generationcp.middleware.domain.inventory.manager.TransactionUpdateRequestDto;
import org.generationcp.middleware.domain.inventory.manager.TransactionsSearchDto;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareRequestException;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.pojos.GermplasmStudySource;
import org.generationcp.middleware.pojos.LocationType;
import org.generationcp.middleware.pojos.UDTableType;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.ims.EntityType;
import org.generationcp.middleware.pojos.ims.ExperimentTransactionType;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.LotStatus;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.generationcp.middleware.pojos.ims.TransactionSourceType;
import org.generationcp.middleware.pojos.ims.TransactionStatus;
import org.generationcp.middleware.pojos.ims.TransactionType;
import org.generationcp.middleware.util.Util;
import org.generationcp.middleware.utils.test.IntegrationTestDataInitializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;

public class TransactionServiceImplIntegrationTest extends IntegrationTestBase {

	private TransactionServiceImpl transactionService;

	private LotServiceImpl lotService;

	private DaoFactory daoFactory;

	private Integer studyId, userId, pendingWithdrawalId, pendingDepositId, gid;

	private Lot lot;

	private String unitName;

	private Integer storageLocationId;

	public static final int UNIT_ID = TermId.SEED_AMOUNT_G.getId();

	@Autowired
	private LocationDataManager locationDataManager;

	@Before
	public void setUp() {
		this.lotService = new LotServiceImpl(this.sessionProvder);
		this.transactionService = new TransactionServiceImpl(this.sessionProvder);
		this.transactionService.setLotService(this.lotService);
		this.daoFactory = new DaoFactory(this.sessionProvder);
		this.createGermplasmStudySource();
		userId = findAdminUser();
		this.resolveStorageLocation();
		this.createLot();
		this.createTransactions();
		this.resolveUnitName();
	}

	@Test(expected = MiddlewareRequestException.class)
	public void testUpdatePendingTransactions_WithdrawalInvalidAvailableBalance() {
		final TransactionUpdateRequestDto transactionUpdateRequestDto =
			new TransactionUpdateRequestDto(pendingWithdrawalId, null, 30D, null);
		this.transactionService.updatePendingTransactions(Collections.singletonList(transactionUpdateRequestDto));
	}

	@Test(expected = MiddlewareRequestException.class)
	public void testUpdatePendingTransactions_DepositInvalidAvailableBalance() {
		final TransactionUpdateRequestDto transactionUpdateRequestDto = new TransactionUpdateRequestDto(pendingDepositId, null, 2D, null);
		this.transactionService.updatePendingTransactions(Collections.singletonList(transactionUpdateRequestDto));
	}

	@Test(expected = MiddlewareRequestException.class)
	public void testUpdatePendingTransactions_WithdrawalInvalidAmount() {
		final TransactionUpdateRequestDto transactionUpdateRequestDto =
			new TransactionUpdateRequestDto(pendingWithdrawalId, 22D, null, null);
		this.transactionService.updatePendingTransactions(Collections.singletonList(transactionUpdateRequestDto));
	}

	@Test
	public void testUpdatePendingTransactions_WithdrawalNewAmount_Ok() {
		final TransactionUpdateRequestDto transactionUpdateRequestDto =
			new TransactionUpdateRequestDto(pendingWithdrawalId, 20D, null, null);
		this.transactionService.updatePendingTransactions(Collections.singletonList(transactionUpdateRequestDto));
		final Transaction transaction = this.daoFactory.getTransactionDAO().getById(pendingWithdrawalId);
		Assert.assertTrue(transaction.getQuantity().equals(-20D));
	}

	@Test
	public void testUpdatePendingTransactions_WithdrawalNewBalance_Ok() {
		final TransactionUpdateRequestDto transactionUpdateRequestDto =
			new TransactionUpdateRequestDto(pendingWithdrawalId, null, 0D, null);
		this.transactionService.updatePendingTransactions(Collections.singletonList(transactionUpdateRequestDto));
		final Transaction transaction = this.daoFactory.getTransactionDAO().getById(pendingWithdrawalId);
		final LotsSearchDto lotsSearchDto = new LotsSearchDto();
		lotsSearchDto.setLotIds(Collections.singletonList(lot.getId()));
		final ExtendedLotDto lotDto = this.daoFactory.getLotDao().searchLots(lotsSearchDto, null, null).get(0);
		Assert.assertTrue(transaction.getQuantity().equals(-20D));
		Assert.assertTrue(lotDto.getAvailableBalance().equals(0D));
	}

	@Test
	public void testUpdatePendingTransactions_DepositNewBalance_Ok() {
		final TransactionUpdateRequestDto transactionUpdateRequestDto =
			new TransactionUpdateRequestDto(pendingDepositId, null, 20D, null);
		this.transactionService.updatePendingTransactions(Collections.singletonList(transactionUpdateRequestDto));
		final Transaction transaction = this.daoFactory.getTransactionDAO().getById(pendingDepositId);
		final LotsSearchDto lotsSearchDto = new LotsSearchDto();
		lotsSearchDto.setLotIds(Collections.singletonList(lot.getId()));
		final ExtendedLotDto lotDto = this.daoFactory.getLotDao().searchLots(lotsSearchDto, null, null).get(0);
		Assert.assertTrue(transaction.getQuantity().equals(2D));
		Assert.assertTrue(lotDto.getAvailableBalance().equals(18D));
	}

	@Test
	public void testUpdatePendingTransactions_DepositNewAmount_Ok() {
		final TransactionUpdateRequestDto transactionUpdateRequestDto =
			new TransactionUpdateRequestDto(pendingDepositId, 5D, null, null);
		this.transactionService.updatePendingTransactions(Collections.singletonList(transactionUpdateRequestDto));
		final Transaction transaction = this.daoFactory.getTransactionDAO().getById(pendingDepositId);
		final LotsSearchDto lotsSearchDto = new LotsSearchDto();
		lotsSearchDto.setLotIds(Collections.singletonList(lot.getId()));
		final ExtendedLotDto lotDto = this.daoFactory.getLotDao().searchLots(lotsSearchDto, null, null).get(0);
		Assert.assertTrue(transaction.getQuantity().equals(5D));
		Assert.assertTrue(lotDto.getAvailableBalance().equals(18D));
	}

	@Test
	public void testDepositLots_Ok() {
		final LotDepositRequestDto lotDepositRequestDto = new LotDepositRequestDto();
		final Map<String, Double> instructions = new HashMap<>();
		instructions.put(unitName, 20D);
		lotDepositRequestDto.setDepositsPerUnit(instructions);

		final List<Integer> lotIds = Collections.singletonList(lot.getId());
		this.transactionService.depositLots(userId, new HashSet<>(lotIds), lotDepositRequestDto, TransactionStatus.CONFIRMED, null, null);

		final LotsSearchDto lotsSearchDto = new LotsSearchDto();
		lotsSearchDto.setLotIds(lotIds);
		final List<ExtendedLotDto> extendedLotDtos = this.lotService.searchLots(lotsSearchDto, null);
		final ExtendedLotDto extendedLotDto = extendedLotDtos.get(0);
		Assert.assertTrue(extendedLotDto.getAvailableBalance().equals(38D));
	}


	@Test
	public void testDepositLots_WithSourceStudy_Ok() {
		final LotDepositRequestDto lotDepositRequestDto = new LotDepositRequestDto();
		final Map<String, Double> instructions = new HashMap<>();
		instructions.put(unitName, 20D);
		lotDepositRequestDto.setDepositsPerUnit(instructions);
		lotDepositRequestDto.setSourceStudyId(this.studyId);

		final List<Integer> lotIds = Collections.singletonList(lot.getId());
		this.transactionService.depositLots(userId, new HashSet<>(lotIds), lotDepositRequestDto, TransactionStatus.CONFIRMED, null, null);

		final LotsSearchDto lotsSearchDto = new LotsSearchDto();
		lotsSearchDto.setLotIds(lotIds);
		final List<ExtendedLotDto> extendedLotDtos = this.lotService.searchLots(lotsSearchDto, null);
		final ExtendedLotDto extendedLotDto = extendedLotDtos.get(0);
		Assert.assertTrue(extendedLotDto.getAvailableBalance().equals(38D));

		final List<Transaction> transactions = this.daoFactory.getExperimentTransactionDao()
			.getTransactionsByStudyId(this.studyId, TransactionStatus.CONFIRMED, ExperimentTransactionType.HARVESTING);

		Assert.assertEquals(1, transactions.size());
	}

	@Test
	public void testDepositLots_WithSourceTypeAndSourceId_Ok() {
		final LotDepositRequestDto lotDepositRequestDto = new LotDepositRequestDto();
		final Map<String, Double> instructions = new HashMap<>();
		instructions.put(unitName, 20D);
		lotDepositRequestDto.setDepositsPerUnit(instructions);

		final List<Integer> lotIds = Collections.singletonList(lot.getId());
		this.transactionService.depositLots(userId, new HashSet<>(lotIds), lotDepositRequestDto, TransactionStatus.CONFIRMED,
			TransactionSourceType.SPLIT_LOT, lot.getId());

		final LotsSearchDto lotsSearchDto = new LotsSearchDto();
		lotsSearchDto.setLotIds(lotIds);
		final List<ExtendedLotDto> extendedLotDtos = this.lotService.searchLots(lotsSearchDto, null);
		final ExtendedLotDto extendedLotDto = extendedLotDtos.get(0);
		Assert.assertTrue(extendedLotDto.getAvailableBalance().equals(38D));

		final List<Transaction> transactions = this.sessionProvder.getSession().createQuery(
			String.format("select T from %s T where lotId=%s",
				Transaction.class.getCanonicalName(),
				this.lot.getId()))
			.list();
		this.assertTransaction(transactions.get(transactions.size() - 1), TransactionType.DEPOSIT, TransactionStatus.CONFIRMED,
			20D, TransactionSourceType.SPLIT_LOT, this.lot.getId());
	}

	@Test(expected = MiddlewareRequestException.class)
	public void testSaveAdjustmentTransactions_InvalidNewBalance() {
		this.transactionService.saveAdjustmentTransactions(userId, Collections.singleton(lot.getId()), 1D, "");
	}

	@Test
	public void testSaveAdjustmentTransactions_OK() {
		this.transactionService.saveAdjustmentTransactions(userId, Collections.singleton(lot.getId()), 3D, "");
		final LotsSearchDto lotsSearchDto = new LotsSearchDto();
		lotsSearchDto.setLotIds(Collections.singletonList(lot.getId()));
		final List<ExtendedLotDto> extendedLotDtos = this.lotService.searchLots(lotsSearchDto, null);
		final ExtendedLotDto extendedLotDto = extendedLotDtos.get(0);
		Assert.assertTrue(extendedLotDto.getActualBalance().equals(3D));
	}

	@Test
	public void testSaveAdjustmentTransactions_NoTransactionSaved() {
		this.transactionService.saveAdjustmentTransactions(userId, Collections.singleton(lot.getId()), 20D, "");

		final TransactionsSearchDto transactionsSearchDto = new TransactionsSearchDto();
		transactionsSearchDto.setLotIds(Collections.singletonList(lot.getId()));
		final List<TransactionDto> transactionDtos = this.transactionService.searchTransactions(transactionsSearchDto, null);

		final LotsSearchDto lotsSearchDto = new LotsSearchDto();
		lotsSearchDto.setLotIds(Collections.singletonList(lot.getId()));
		final List<ExtendedLotDto> extendedLotDtos = this.lotService.searchLots(lotsSearchDto, null);
		final ExtendedLotDto extendedLotDto = extendedLotDtos.get(0);
		Assert.assertTrue(extendedLotDto.getActualBalance().equals(20D));

		Assert.assertEquals(3, transactionDtos.size());
	}

	private void createLot() {
		lot = new Lot(null, userId, EntityType.GERMPLSM.name(), gid, storageLocationId, UNIT_ID, LotStatus.ACTIVE.getIntValue(), 0,
			"Lot", RandomStringUtils.randomAlphabetic(35));
		this.daoFactory.getLotDao().save(lot);
	}

	private void createTransactions() {

		final Transaction confirmedDeposit =
			new Transaction(null, userId, lot, Util.getCurrentDate(), TransactionStatus.CONFIRMED.getIntValue(),
				20D, "Transaction 1", Util.getCurrentDateAsIntegerValue(), null, null, null, userId, TransactionType.DEPOSIT.getId());

		final Transaction pendingDeposit =
			new Transaction(null, userId, lot, Util.getCurrentDate(), TransactionStatus.PENDING.getIntValue(),
				20D, "Transaction 2", 0, null, null, null, userId, TransactionType.DEPOSIT.getId());

		final Transaction pendingWithdrawal =
			new Transaction(null, userId, lot, Util.getCurrentDate(), TransactionStatus.PENDING.getIntValue(),
				-2D, "Transaction 3", 0, null, null, null, userId, TransactionType.WITHDRAWAL.getId());

		this.daoFactory.getTransactionDAO().save(confirmedDeposit);
		this.daoFactory.getTransactionDAO().save(pendingDeposit);
		this.pendingDepositId = pendingDeposit.getId();
		this.daoFactory.getTransactionDAO().save(pendingWithdrawal);
		this.pendingWithdrawalId = pendingWithdrawal.getId();

	}

	private void createGermplasmStudySource() {
		final IntegrationTestDataInitializer integrationTestDataInitializer =
			new IntegrationTestDataInitializer(this.sessionProvder, this.workbenchSessionProvider);

		final DmsProject study = integrationTestDataInitializer
			.createStudy(RandomStringUtils.randomAlphanumeric(10), RandomStringUtils.randomAlphanumeric(10), 1);
		final DmsProject plot = integrationTestDataInitializer
			.createDmsProject(RandomStringUtils.randomAlphanumeric(10), RandomStringUtils.randomAlphanumeric(10), study,
				study, DatasetTypeEnum.PLOT_DATA);
		final Geolocation geolocation = integrationTestDataInitializer.createInstance(study, "1", 1);

		final GermplasmStudySource
			germplasmStudySource = integrationTestDataInitializer.addGermplasmStudySource(study, plot, geolocation, "111", "222");

		this.gid = germplasmStudySource.getGermplasm().getGid();
		this.studyId = study.getProjectId();
	}

	private void resolveUnitName() {
		unitName = this.daoFactory.getCvTermDao().getById(UNIT_ID).getName();
	}

	private void resolveStorageLocation() {
		final Integer id = locationDataManager.getUserDefinedFieldIdOfCode(UDTableType.LOCATION_LTYPE, LocationType.SSTORE.name());
		storageLocationId = this.daoFactory.getLocationDAO().getDefaultLocationByType(id).getLocid();
	}

	private void assertTransaction(final Transaction actualTransaction, final TransactionType type, final TransactionStatus status,
		final double amount, final TransactionSourceType sourceType, final Integer sourceId) {
		assertNotNull(actualTransaction);
		assertThat(actualTransaction.getType(), is(type.getId()));
		assertThat(actualTransaction.getStatus(), is(status.getIntValue()));
		assertThat(actualTransaction.getQuantity(), is(amount));
		assertThat(actualTransaction.getSourceType(), is(sourceType.name()));
		assertThat(actualTransaction.getSourceId(), is(sourceId));
	}

}
