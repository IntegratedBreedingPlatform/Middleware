package org.generationcp.middleware.service.impl.inventory;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.domain.inventory.manager.ExtendedLotDto;
import org.generationcp.middleware.domain.inventory.manager.LotsSearchDto;
import org.generationcp.middleware.domain.inventory.manager.TransactionDto;
import org.generationcp.middleware.domain.inventory.manager.TransactionUpdateRequestDto;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareRequestException;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.ims.EntityType;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.LotStatus;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.generationcp.middleware.pojos.ims.TransactionStatus;
import org.generationcp.middleware.pojos.ims.TransactionType;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.user.UserService;
import org.generationcp.middleware.util.Util;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

public class TransactionServiceImplIntegrationTest extends IntegrationTestBase {

	private TransactionServiceImpl transactionService;

	private DaoFactory daoFactory;

	private Integer userId, pendingWithdrawalId, pendingDepositId, gid;

	private Lot lot;

	private static final Integer DEFAULT_STORAGE_LOCATION = 6000;

	private static final int GROUP_ID = 0;

	public static final int UNIT_ID = TermId.SEED_AMOUNT_G.getId();

	@Autowired
	private GermplasmDataManager germplasmDataManager;

	@Autowired
	private UserService userService;

	@Before
	public void setUp() {
		this.transactionService = new TransactionServiceImpl(this.sessionProvder);
		this.daoFactory = new DaoFactory(this.sessionProvder);
		this.createGermplasm();
		this.findAdminUser();
		this.createLot();
		this.createTransactions();
	}

	@Test(expected = MiddlewareRequestException.class)
	public void testUpdatePendingTransactions_WithdrawalInvalidAvailableBalance() {
		final TransactionUpdateRequestDto transactionUpdateRequestDto =
			new TransactionUpdateRequestDto(pendingWithdrawalId, null, 30D, null);
		this.transactionService.updatePendingTransactions(Arrays.asList(transactionUpdateRequestDto));
	}

	@Test(expected = MiddlewareRequestException.class)
	public void testUpdatePendingTransactions_DepositInvalidAvailableBalance() {
		final TransactionUpdateRequestDto transactionUpdateRequestDto = new TransactionUpdateRequestDto(pendingDepositId, null, 2D, null);
		this.transactionService.updatePendingTransactions(Arrays.asList(transactionUpdateRequestDto));
	}

	@Test(expected = MiddlewareRequestException.class)
	public void testUpdatePendingTransactions_WithdrawalInvalidAmount() {
		final TransactionUpdateRequestDto transactionUpdateRequestDto =
			new TransactionUpdateRequestDto(pendingWithdrawalId, 22D, null, null);
		this.transactionService.updatePendingTransactions(Arrays.asList(transactionUpdateRequestDto));
	}

	@Test
	public void testUpdatePendingTransactions_WithdrawalNewAmount_Ok() {
		final TransactionUpdateRequestDto transactionUpdateRequestDto =
			new TransactionUpdateRequestDto(pendingWithdrawalId, 20D, null, null);
		this.transactionService.updatePendingTransactions(Arrays.asList(transactionUpdateRequestDto));
		final Transaction transaction = this.daoFactory.getTransactionDAO().getById(pendingWithdrawalId);
		Assert.assertTrue(transaction.getQuantity().equals(-20D));
	}

	@Test
	public void testUpdatePendingTransactions_WithdrawalNewBalance_Ok() {
		final TransactionUpdateRequestDto transactionUpdateRequestDto =
			new TransactionUpdateRequestDto(pendingWithdrawalId, null, 0D, null);
		this.transactionService.updatePendingTransactions(Arrays.asList(transactionUpdateRequestDto));
		final Transaction transaction = this.daoFactory.getTransactionDAO().getById(pendingWithdrawalId);
		final LotsSearchDto lotsSearchDto = new LotsSearchDto();
		lotsSearchDto.setLotIds(Arrays.asList(lot.getId()));
		final ExtendedLotDto lotDto = this.daoFactory.getLotDao().searchLots(lotsSearchDto, null).get(0);
		Assert.assertTrue(transaction.getQuantity().equals(-20D));
		Assert.assertTrue(lotDto.getAvailableBalance().equals(0D));
	}

	@Test
	public void testUpdatePendingTransactions_DepositNewBalance_Ok() {
		final TransactionUpdateRequestDto transactionUpdateRequestDto =
			new TransactionUpdateRequestDto(pendingDepositId, null, 20D, null);
		this.transactionService.updatePendingTransactions(Arrays.asList(transactionUpdateRequestDto));
		final Transaction transaction = this.daoFactory.getTransactionDAO().getById(pendingDepositId);
		final LotsSearchDto lotsSearchDto = new LotsSearchDto();
		lotsSearchDto.setLotIds(Arrays.asList(lot.getId()));
		final ExtendedLotDto lotDto = this.daoFactory.getLotDao().searchLots(lotsSearchDto, null).get(0);
		Assert.assertTrue(transaction.getQuantity().equals(2D));
		Assert.assertTrue(lotDto.getAvailableBalance().equals(18D));
	}

	@Test
	public void testUpdatePendingTransactions_DepositNewAmount_Ok() {
		final TransactionUpdateRequestDto transactionUpdateRequestDto =
			new TransactionUpdateRequestDto(pendingDepositId, 5D, null, null);
		this.transactionService.updatePendingTransactions(Arrays.asList(transactionUpdateRequestDto));
		final Transaction transaction = this.daoFactory.getTransactionDAO().getById(pendingDepositId);
		final LotsSearchDto lotsSearchDto = new LotsSearchDto();
		lotsSearchDto.setLotIds(Arrays.asList(lot.getId()));
		final ExtendedLotDto lotDto = this.daoFactory.getLotDao().searchLots(lotsSearchDto, null).get(0);
		Assert.assertTrue(transaction.getQuantity().equals(5D));
		Assert.assertTrue(lotDto.getAvailableBalance().equals(18D));
	}

	@Test
	public void testConfirmPendingTransactions_Deposit_Ok() {
		final TransactionDto transactionDepositDto = new TransactionDto();
		transactionDepositDto.setTransactionId(pendingDepositId);

		final Transaction transactionDeposit = this.daoFactory.getTransactionDAO().getById(pendingDepositId);
		Assert.assertTrue(transactionDeposit.getType().equals(4));
		Assert.assertTrue(transactionDeposit.getStatus().equals(0));

		this.transactionService.confirmPendingTransactions(Arrays.asList(transactionDepositDto));
		final Transaction transactionDepositCancelled = this.daoFactory.getTransactionDAO().getById(pendingDepositId);
		Assert.assertTrue(transactionDeposit.getType().equals(4));
		Assert.assertTrue(transactionDepositCancelled.getStatus().equals(1));
	}

	@Test
	public void testConfirmPendingTransactions_Withdrawal_Ok() {
		final TransactionDto transactionDepositDto = new TransactionDto();
		transactionDepositDto.setTransactionId(pendingWithdrawalId);

		final Transaction transactionWithdrawal = this.daoFactory.getTransactionDAO().getById(pendingWithdrawalId);
		Assert.assertTrue(transactionWithdrawal.getType().equals(1));
		Assert.assertTrue(transactionWithdrawal.getStatus().equals(0));

		this.transactionService.confirmPendingTransactions(Arrays.asList(transactionDepositDto));
		final Transaction transactionWithdrawalCancelled = this.daoFactory.getTransactionDAO().getById(pendingWithdrawalId);
		Assert.assertTrue(transactionWithdrawal.getType().equals(1));
		Assert.assertTrue(transactionWithdrawalCancelled.getStatus().equals(1));
	}

	@Test
	public void testCancelPendingTransactions_Deposit_Ok() {
		final TransactionDto transactionDepositDto = new TransactionDto();
		transactionDepositDto.setTransactionId(pendingDepositId);

		final Transaction transactionDeposit = this.daoFactory.getTransactionDAO().getById(pendingDepositId);
		Assert.assertTrue(transactionDeposit.getType().equals(4));
		Assert.assertTrue(transactionDeposit.getStatus().equals(0));

		this.transactionService.cancelPendingTransactions(Arrays.asList(transactionDepositDto));
		final Transaction transactionDepositCancelled = this.daoFactory.getTransactionDAO().getById(pendingDepositId);
		Assert.assertTrue(transactionDeposit.getType().equals(4));
		Assert.assertTrue(transactionDepositCancelled.getStatus().equals(9));
	}

	@Test
	public void testCancelPendingTransactions_Withdrawal_Ok() {
		final TransactionDto transactionDepositDto = new TransactionDto();
		transactionDepositDto.setTransactionId(pendingWithdrawalId);

		final Transaction transactionWithdrawal = this.daoFactory.getTransactionDAO().getById(pendingWithdrawalId);
		Assert.assertTrue(transactionWithdrawal.getType().equals(1));
		Assert.assertTrue(transactionWithdrawal.getStatus().equals(0));

		this.transactionService.cancelPendingTransactions(Arrays.asList(transactionDepositDto));
		final Transaction transactionWithdrawalCancelled = this.daoFactory.getTransactionDAO().getById(pendingWithdrawalId);
		Assert.assertTrue(transactionWithdrawal.getType().equals(1));
		Assert.assertTrue(transactionWithdrawalCancelled.getStatus().equals(9));
	}

	private void findAdminUser() {
		final WorkbenchUser user = this.userService.getUserByName("admin", 0, 1, Operation.EQUAL).get(0);
		userId = user.getUserid();
	}

	private void createGermplasm() {
		final Germplasm germplasm = GermplasmTestDataInitializer.createGermplasm(Integer.MIN_VALUE);
		germplasm.setMgid(GROUP_ID);
		this.germplasmDataManager.addGermplasm(germplasm, germplasm.getPreferredName());
		gid = germplasm.getGid();
	}

	private void createLot() {
		lot = new Lot(null, userId, EntityType.GERMPLSM.name(), gid, DEFAULT_STORAGE_LOCATION, UNIT_ID, LotStatus.ACTIVE.getIntValue(), 0,
			"Lot", RandomStringUtils.randomAlphabetic(35));
		this.daoFactory.getLotDao().save(lot);
	}

	private void createTransactions() {

		final Transaction confirmedDeposit =
			new Transaction(null, userId, lot, Util.getCurrentDate(), TransactionStatus.CONFIRMED.getIntValue(),
				20D, "Transaction 1", Util.getCurrentDateAsIntegerValue(), null, null, null,
				Double.valueOf(0), userId, TransactionType.DEPOSIT.getId());

		final Transaction pendingDeposit =
			new Transaction(null, userId, lot, Util.getCurrentDate(), TransactionStatus.PENDING.getIntValue(),
				20D, "Transaction 2", 0, null, null, null,
				Double.valueOf(0), userId, TransactionType.DEPOSIT.getId());

		final Transaction pendingWithdrawal =
			new Transaction(null, userId, lot, Util.getCurrentDate(), TransactionStatus.PENDING.getIntValue(),
				-2D, "Transaction 3", 0, null, null, null,
				Double.valueOf(0), userId, TransactionType.WITHDRAWAL.getId());

		this.daoFactory.getTransactionDAO().save(confirmedDeposit);
		this.daoFactory.getTransactionDAO().save(pendingDeposit);
		this.pendingDepositId = pendingDeposit.getId();
		this.daoFactory.getTransactionDAO().save(pendingWithdrawal);
		this.pendingWithdrawalId = pendingWithdrawal.getId();

	}
}
