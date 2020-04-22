package org.generationcp.middleware.service.impl.inventory;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.domain.inventory.manager.ExtendedLotDto;
import org.generationcp.middleware.domain.inventory.manager.LotsSearchDto;
import org.generationcp.middleware.domain.inventory.manager.TransactionDto;
import org.generationcp.middleware.domain.inventory.manager.TransactionsSearchDto;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.LocationType;
import org.generationcp.middleware.pojos.UDTableType;
import org.generationcp.middleware.pojos.ims.EntityType;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.LotStatus;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.generationcp.middleware.pojos.ims.TransactionStatus;
import org.generationcp.middleware.pojos.ims.TransactionType;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.user.UserService;
import org.generationcp.middleware.util.Util;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasToString;

public class LotServiceImplIntegrationTest extends IntegrationTestBase {

	private TransactionServiceImpl transactionService;

	private LotServiceImpl lotService;

	private DaoFactory daoFactory;

	private Integer userId, gid;

	private Lot lot;

	private Integer storageLocationId;

	private static final int GROUP_ID = 0;

	public static final int UNIT_ID = TermId.SEED_AMOUNT_G.getId();

	@Autowired
	private GermplasmDataManager germplasmDataManager;

	@Autowired
	private UserService userService;

	@Autowired
	private LocationDataManager locationDataManager;

	@Before
	public void setUp() {
		this.transactionService = new TransactionServiceImpl(this.sessionProvder);
		this.lotService = new LotServiceImpl(this.sessionProvder);
		this.daoFactory = new DaoFactory(this.sessionProvder);
		this.lotService.setTransactionService(transactionService);
		this.createGermplasm();
		this.findAdminUser();
		this.resolveStorageLocation();
		this.createLot();
		this.createTransactions();
	}

	@Test
	public void lotWithOpenBalanceClosed_Ok() {
		this.lotService.closeLots(userId, Collections.singletonList(lot.getId()));
		final LotsSearchDto searchDto = new LotsSearchDto();
		searchDto.setLotIds(Collections.singletonList(lot.getId()));
		final List<ExtendedLotDto> extendedLotDtos = this.lotService.searchLots(searchDto, null);

		final TransactionsSearchDto pendingTransactionSearch = new TransactionsSearchDto();
		pendingTransactionSearch.setLotIds(Collections.singletonList(lot.getId()));
		pendingTransactionSearch.setTransactionStatus(Collections.singletonList(TransactionStatus.PENDING.getIntValue()));
		final List<TransactionDto> pendingTransactions = transactionService.searchTransactions(pendingTransactionSearch, null);

		final TransactionsSearchDto discardedTransactionSearch = new TransactionsSearchDto();
		discardedTransactionSearch.setLotIds(Collections.singletonList(lot.getId()));
		discardedTransactionSearch.setTransactionTypes(Collections.singletonList(TransactionType.DISCARD.getId()));
		final List<TransactionDto> discardedTransactions = transactionService.searchTransactions(discardedTransactionSearch, null);
		discardedTransactions.sort((TransactionDto t1, TransactionDto t2) -> t2.getTransactionId().compareTo(t1.getTransactionId()));

		assertThat(extendedLotDtos.get(0).getStatus(), hasToString(LotStatus.CLOSED.name()));
		assertThat(extendedLotDtos.get(0).getAvailableBalance(), equalTo(0D));
		assertThat(pendingTransactions, hasSize(0));
		assertThat(discardedTransactions.get(0).getAmount(), equalTo(-20D));
	}

	@Test
	public void lotWithNoOpenBalanceClosed_Ok() {
		final Transaction confirmedWithdrawal =
			new Transaction(null, userId, lot, Util.getCurrentDate(), TransactionStatus.CONFIRMED.getIntValue(),
				-20D, "Transaction 3", 0, null, null, null,
				Double.valueOf(0), userId, TransactionType.WITHDRAWAL.getId());
		this.daoFactory.getTransactionDAO().save(confirmedWithdrawal);

		final TransactionsSearchDto discardedTransactionSearch = new TransactionsSearchDto();
		discardedTransactionSearch.setLotIds(Collections.singletonList(lot.getId()));
		discardedTransactionSearch.setTransactionTypes(Collections.singletonList(TransactionType.DISCARD.getId()));
		final Integer discardedTrxsBeforeClosingLot = this.transactionService.searchTransactions(discardedTransactionSearch, null).size();

		this.lotService.closeLots(userId, Collections.singletonList(lot.getId()));
		final Integer discardedTrxsAfterClosingLot = this.transactionService.searchTransactions(discardedTransactionSearch, null).size();

		final LotsSearchDto searchDto = new LotsSearchDto();
		searchDto.setLotIds(Collections.singletonList(lot.getId()));
		final List<ExtendedLotDto> extendedLotDtos = this.lotService.searchLots(searchDto, null);

		final TransactionsSearchDto pendingTransactionSearch = new TransactionsSearchDto();
		pendingTransactionSearch.setLotIds(Collections.singletonList(lot.getId()));
		pendingTransactionSearch.setTransactionStatus(Collections.singletonList(TransactionStatus.PENDING.getIntValue()));
		final List<TransactionDto> pendingTransactions = transactionService.searchTransactions(pendingTransactionSearch, null);

		assertThat(extendedLotDtos.get(0).getStatus(), hasToString(LotStatus.CLOSED.name()));
		assertThat(extendedLotDtos.get(0).getAvailableBalance(), equalTo(0D));
		assertThat(pendingTransactions, hasSize(0));
		assertThat(discardedTrxsAfterClosingLot, equalTo(discardedTrxsBeforeClosingLot));

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
		lot = new Lot(null, userId, EntityType.GERMPLSM.name(), gid, storageLocationId, UNIT_ID, LotStatus.ACTIVE.getIntValue(), 0,
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

		this.daoFactory.getTransactionDAO().save(confirmedDeposit);
		this.daoFactory.getTransactionDAO().save(pendingDeposit);

	}

	private void resolveStorageLocation() {
		final Integer id = locationDataManager.getUserDefinedFieldIdOfCode(UDTableType.LOCATION_LTYPE, LocationType.SSTORE.name());
		storageLocationId = this.daoFactory.getLocationDAO().getDefaultLocationByType(id).getLocid();
	}

}
