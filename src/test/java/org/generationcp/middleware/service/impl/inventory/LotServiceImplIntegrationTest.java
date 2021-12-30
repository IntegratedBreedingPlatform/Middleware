package org.generationcp.middleware.service.impl.inventory;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.domain.inventory.common.SearchCompositeDto;
import org.generationcp.middleware.domain.inventory.manager.ExtendedLotDto;
import org.generationcp.middleware.domain.inventory.manager.LotMultiUpdateRequestDto;
import org.generationcp.middleware.domain.inventory.manager.LotSingleUpdateRequestDto;
import org.generationcp.middleware.domain.inventory.manager.LotUpdateRequestDto;
import org.generationcp.middleware.domain.inventory.manager.LotsSearchDto;
import org.generationcp.middleware.domain.inventory.manager.TransactionDto;
import org.generationcp.middleware.domain.inventory.manager.TransactionsSearchDto;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.LocationType;
import org.generationcp.middleware.pojos.UDTableType;
import org.generationcp.middleware.pojos.ims.EntityType;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.LotStatus;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.generationcp.middleware.pojos.ims.TransactionSourceType;
import org.generationcp.middleware.pojos.ims.TransactionStatus;
import org.generationcp.middleware.pojos.ims.TransactionType;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.util.Util;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;

public class LotServiceImplIntegrationTest extends IntegrationTestBase {

	private TransactionServiceImpl transactionService;

	private LotServiceImpl lotService;

	private DaoFactory daoFactory;

	private Integer userId, gid;

	private Lot lot;

	private Integer storageLocationId;

	private static final int GROUP_ID = 0;

	private static final int UNIT_ID = TermId.SEED_AMOUNT_G.getId();

	@Autowired
	private GermplasmDataManager germplasmDataManager;

	@Autowired
	private LocationDataManager locationDataManager;

	@Autowired
	private OntologyVariableDataManager ontologyVariableDataManager;

	@Before
	public void setUp() {
		this.transactionService = new TransactionServiceImpl(this.sessionProvder);
		this.lotService = new LotServiceImpl(this.sessionProvder);
		this.daoFactory = new DaoFactory(this.sessionProvder);
		this.lotService.setTransactionService(this.transactionService);
		this.lotService.setOntologyVariableDataManager(this.ontologyVariableDataManager);
		this.createGermplasm();
		this.userId = this.findAdminUser();
		this.resolveStorageLocation();
		this.createLot();
		this.createTransactions();
	}

	@Test
	public void lotWithOpenBalanceClosed_Ok() {
		this.lotService.closeLots(this.userId, Collections.singletonList(this.lot.getId()));
		final LotsSearchDto searchDto = new LotsSearchDto();
		searchDto.setLotIds(Collections.singletonList(this.lot.getId()));
		final List<ExtendedLotDto> extendedLotDtos = this.lotService.searchLots(searchDto, null);

		final TransactionsSearchDto pendingTransactionSearch = new TransactionsSearchDto();
		pendingTransactionSearch.setLotIds(Collections.singletonList(this.lot.getId()));
		pendingTransactionSearch.setTransactionStatus(Collections.singletonList(TransactionStatus.PENDING.getIntValue()));
		final List<TransactionDto> pendingTransactions = this.transactionService.searchTransactions(pendingTransactionSearch, null);

		final TransactionsSearchDto discardedTransactionSearch = new TransactionsSearchDto();
		discardedTransactionSearch.setLotIds(Collections.singletonList(this.lot.getId()));
		discardedTransactionSearch.setTransactionTypes(Collections.singletonList(TransactionType.DISCARD.getId()));
		final List<TransactionDto> discardedTransactions = this.transactionService.searchTransactions(discardedTransactionSearch, null);
		discardedTransactions.sort((TransactionDto t1, TransactionDto t2) -> t2.getTransactionId().compareTo(t1.getTransactionId()));

		assertThat(extendedLotDtos.get(0).getStatus(), hasToString(LotStatus.CLOSED.name()));
		assertThat(extendedLotDtos.get(0).getAvailableBalance(), equalTo(0D));
		assertThat(pendingTransactions, hasSize(0));
		assertThat(discardedTransactions.get(0).getAmount(), equalTo(-20D));
	}

	@Test
	public void lotWithNoOpenBalanceClosed_Ok() {
		final Transaction confirmedWithdrawal =
			new Transaction(null, this.userId, this.lot, Util.getCurrentDate(), TransactionStatus.CONFIRMED.getIntValue(),
				-20D, "Transaction 3", 0, null, null, null, this.userId, TransactionType.WITHDRAWAL.getId());
		this.daoFactory.getTransactionDAO().save(confirmedWithdrawal);

		final TransactionsSearchDto discardedTransactionSearch = new TransactionsSearchDto();
		discardedTransactionSearch.setLotIds(Collections.singletonList(this.lot.getId()));
		discardedTransactionSearch.setTransactionTypes(Collections.singletonList(TransactionType.DISCARD.getId()));
		final Integer discardedTrxsBeforeClosingLot = this.transactionService.searchTransactions(discardedTransactionSearch, null).size();

		this.lotService.closeLots(this.userId, Collections.singletonList(this.lot.getId()));
		final Integer discardedTrxsAfterClosingLot = this.transactionService.searchTransactions(discardedTransactionSearch, null).size();

		final LotsSearchDto searchDto = new LotsSearchDto();
		searchDto.setLotIds(Collections.singletonList(this.lot.getId()));
		final List<ExtendedLotDto> extendedLotDtos = this.lotService.searchLots(searchDto, null);

		final TransactionsSearchDto pendingTransactionSearch = new TransactionsSearchDto();
		pendingTransactionSearch.setLotIds(Collections.singletonList(this.lot.getId()));
		pendingTransactionSearch.setTransactionStatus(Collections.singletonList(TransactionStatus.PENDING.getIntValue()));
		final List<TransactionDto> pendingTransactions = this.transactionService.searchTransactions(pendingTransactionSearch, null);

		assertThat(extendedLotDtos.get(0).getStatus(), hasToString(LotStatus.CLOSED.name()));
		assertThat(extendedLotDtos.get(0).getAvailableBalance(), equalTo(0D));
		assertThat(pendingTransactions, hasSize(0));
		assertThat(discardedTrxsAfterClosingLot, equalTo(discardedTrxsBeforeClosingLot));

	}

	@Test
	public void lotSingleUpdateNotes_Ok() {
		final LotUpdateRequestDto lotUpdateRequestDto = new LotUpdateRequestDto();
		final LotSingleUpdateRequestDto singleInput = new LotSingleUpdateRequestDto();
		singleInput.setNotes("Test1");
		lotUpdateRequestDto.setSingleInput(singleInput);

		final Set<String> itemIds = Sets.newHashSet(this.lot.getLotUuId());
		lotUpdateRequestDto.getSingleInput().setSearchComposite(new SearchCompositeDto());
		lotUpdateRequestDto.getSingleInput().getSearchComposite().setItemIds(itemIds);
		final LotsSearchDto searchDto = new LotsSearchDto();
		searchDto.setLotIds(Collections.singletonList(this.lot.getId()));
		final List<ExtendedLotDto> extendedLotDtos = this.lotService.searchLots(searchDto, null);

		this.lotService.updateLots(extendedLotDtos, lotUpdateRequestDto);
		assertThat(this.lot.getComments(), hasToString("Test1"));
	}

	@Test
	public void lotSingleUpdateNotesUnit_Ok() {
		final LotUpdateRequestDto lotUpdateRequestDto = new LotUpdateRequestDto();
		final LotSingleUpdateRequestDto singleInput = new LotSingleUpdateRequestDto();
		singleInput.setNotes("Test2");
		singleInput.setUnitId(8267);
		lotUpdateRequestDto.setSingleInput(singleInput);

		final Set<String> itemIds = Sets.newHashSet(this.lot.getLotUuId());
		lotUpdateRequestDto.getSingleInput().setSearchComposite(new SearchCompositeDto());
		lotUpdateRequestDto.getSingleInput().getSearchComposite().setItemIds(itemIds);
		final LotsSearchDto searchDto = new LotsSearchDto();
		searchDto.setLotIds(Collections.singletonList(this.lot.getId()));
		final List<ExtendedLotDto> extendedLotDtos = this.lotService.searchLots(searchDto, null);

		this.lotService.updateLots(extendedLotDtos, lotUpdateRequestDto);
		assertThat(this.lot.getComments(), hasToString("Test2"));
		assertThat(this.lot.getScaleId(), equalTo(8267));
	}

	@Test
	public void lotMultiUpdateNotesUnit_Ok() {
		final String newLotUID = UUID.randomUUID().toString();

		final LotUpdateRequestDto lotUpdateRequestDto = new LotUpdateRequestDto();
		final LotMultiUpdateRequestDto multiInput = new LotMultiUpdateRequestDto();
		final List<LotMultiUpdateRequestDto.LotUpdateDto> lotList = new ArrayList<>();
		final LotMultiUpdateRequestDto.LotUpdateDto lot = new LotMultiUpdateRequestDto.LotUpdateDto();
		lot.setLotUID(this.lot.getLotUuId());
		lot.setUnitName("SEED_AMOUNT_kg");
		lot.setNotes("Test3");
		lot.setNewLotUID(newLotUID);
		lot.setStorageLocationAbbr("ARG");
		lotList.add(lot);
		multiInput.setLotList(lotList);
		lotUpdateRequestDto.setMultiInput(multiInput);

		final LotsSearchDto searchDto = new LotsSearchDto();
		searchDto.setLotIds(Collections.singletonList(this.lot.getId()));
		assertThat(this.lot.getScaleId(), equalTo(8264));
		assertThat(this.lot.getComments(), hasToString("Lot"));

		final List<ExtendedLotDto> extendedLotDtos = this.lotService.searchLots(searchDto, null);

		this.lotService.updateLots(extendedLotDtos, lotUpdateRequestDto);
		assertThat(this.lot.getComments(), hasToString("Test3"));
		assertThat(this.lot.getScaleId(), equalTo(8267));
		assertThat(this.lot.getLotUuId(), equalTo(newLotUID));
	}

	@Test
	public void testMergeLots_Ok() {
		final Integer keepLotId = this.lot.getId();
		assertThat(this.lot.getStatus(), is(LotStatus.ACTIVE.getIntValue()));
		//Check that the lot to keep has not 'merged' transactions
		final List<Transaction> keepLotIdMergedTransactions = this.sessionProvder.getSession().createQuery(
				String.format("select T from %s T where lotId=%s and sourceType='%s'",
						Transaction.class.getCanonicalName(),
						keepLotId,
						TransactionSourceType.MERGED_LOT))
				.list();
		assertThat(keepLotIdMergedTransactions, hasSize(0));

		//Check that the lot to keep has 20 as actual amount
		final List<TransactionDto> availableBalanceTransactions = this.transactionService.getAvailableBalanceTransactions(this.lot.getId());
		assertNotNull(availableBalanceTransactions);
		assertThat(availableBalanceTransactions, hasSize(1));

		final TransactionDto transactionDto = availableBalanceTransactions.get(0);
		assertThat(transactionDto.getLot().getLotId(), is(keepLotId));
		assertThat(transactionDto.getAmount(), is(20d));

		//Create lot 1 to be discarded on merge
		this.createLot();
		assertThat(this.lot.getStatus(), is(LotStatus.ACTIVE.getIntValue()));
		final Integer lotMergeDiscarded1 = this.lot.getId();
		this.createTransactions();

		//Create lot 2 to be discarded on merge
		this.createLot();
		assertThat(this.lot.getStatus(), is(LotStatus.ACTIVE.getIntValue()));
		final Integer lotMergeDiscarded2 = this.lot.getId();
		this.createTransactions();

		//Merge lots
		final LotsSearchDto mergeSearchDto = new LotsSearchDto();
		mergeSearchDto.setLotIds(Arrays.asList(keepLotId, lotMergeDiscarded1, lotMergeDiscarded2));
		this.lotService.mergeLots(this.userId, keepLotId, mergeSearchDto);

		//Check the actual balance of the merged lot should be the sum of the keep and discarded amounts
		final LotsSearchDto searchDto = new LotsSearchDto();
		searchDto.setLotIds(Arrays.asList(keepLotId));
		final List<ExtendedLotDto> extendedLotDtos = this.lotService.searchLots(searchDto, null);
		assertThat(extendedLotDtos, hasSize(1));
		assertThat(extendedLotDtos.get(0).getActualBalance(), is(60D));

		//Check transactions on the keep lot
		final List<Transaction> keepLotIdMergedTransactions2 = this.sessionProvder.getSession().createQuery(
				String.format("select T from %s T where lotId=%s and sourceType='%s'",
						Transaction.class.getCanonicalName(),
						keepLotId,
						TransactionSourceType.MERGED_LOT))
				.list();
		assertThat(keepLotIdMergedTransactions2, hasSize(2));
		this.assertTransaction(keepLotIdMergedTransactions2.get(0), TransactionType.DEPOSIT, TransactionStatus.CONFIRMED,
				20D, TransactionSourceType.MERGED_LOT, lotMergeDiscarded1);
		this.assertTransaction(keepLotIdMergedTransactions2.get(1), TransactionType.DEPOSIT, TransactionStatus.CONFIRMED,
				20D, TransactionSourceType.MERGED_LOT, lotMergeDiscarded2);

		final LotsSearchDto searchClosedLots = new LotsSearchDto();
		searchClosedLots.setLotIds(Arrays.asList(lotMergeDiscarded1, lotMergeDiscarded2));
		final List<ExtendedLotDto> closedLotsSearch = this.lotService.searchLots(searchClosedLots, null);
		assertThat(closedLotsSearch, hasSize(2));
		assertThat(closedLotsSearch.get(0).getStatus(), is(LotStatus.CLOSED.name()));
		assertThat(closedLotsSearch.get(1).getStatus(), is(LotStatus.CLOSED.name()));
	}

	private void createGermplasm() {
		final CropType cropType = new CropType();
		cropType.setUseUUID(false);
		final Germplasm germplasm = GermplasmTestDataInitializer.createGermplasm(Integer.MIN_VALUE);
		germplasm.setMgid(GROUP_ID);
		this.germplasmDataManager.addGermplasm(germplasm, germplasm.getPreferredName(), cropType);
		this.gid = germplasm.getGid();
	}

	private void createLot() {
		this.lot = new Lot(null, this.userId, EntityType.GERMPLSM.name(), this.gid, this.storageLocationId, UNIT_ID, LotStatus.ACTIVE.getIntValue(), 0,
			"Lot", RandomStringUtils.randomAlphabetic(35));
		this.lot.setLotUuId(RandomStringUtils.randomAlphabetic(35));
		this.daoFactory.getLotDao().save(this.lot);
	}

	private void createTransactions() {

		final Transaction confirmedDeposit =
			new Transaction(null, this.userId, this.lot, Util.getCurrentDate(), TransactionStatus.CONFIRMED.getIntValue(),
				20D, "Transaction 1", Util.getCurrentDateAsIntegerValue(), null, null, null, this.userId, TransactionType.DEPOSIT.getId());

		final Transaction pendingDeposit =
			new Transaction(null, this.userId, this.lot, Util.getCurrentDate(), TransactionStatus.PENDING.getIntValue(),
				20D, "Transaction 2", 0, null, null, null, this.userId, TransactionType.DEPOSIT.getId());

		this.daoFactory.getTransactionDAO().save(confirmedDeposit);
		this.daoFactory.getTransactionDAO().save(pendingDeposit);

	}

	private void resolveStorageLocation() {
		final Integer id = this.locationDataManager.getUserDefinedFieldIdOfCode(UDTableType.LOCATION_LTYPE, LocationType.SSTORE.name());
		this.storageLocationId = this.daoFactory.getLocationDAO().getDefaultLocationByType(id).getLocid();
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
