package org.generationcp.middleware.service.impl.inventory;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.domain.inventory.common.SearchCompositeDto;
import org.generationcp.middleware.domain.inventory.common.SearchOriginCompositeDto;
import org.generationcp.middleware.domain.inventory.manager.*;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareRequestException;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.GermplasmStudySource;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.ims.*;
import org.generationcp.middleware.util.Util;
import org.generationcp.middleware.utils.test.IntegrationTestDataInitializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class TransactionServiceImplIntegrationTest extends IntegrationTestBase {
    private static final Integer DEFAULT_SEED_STORE_ID = 6000;

    private TransactionServiceImpl transactionService;

    private LotServiceImpl lotService;

    private DaoFactory daoFactory;

    private Integer studyId, userId, gid;

    private String unitName;


    public static final int UNIT_ID = TermId.SEED_AMOUNT_G.getId();

    @Before
    public void setUp() {
        this.lotService = new LotServiceImpl(this.sessionProvder);
        this.transactionService = new TransactionServiceImpl(this.sessionProvder);
        this.transactionService.setLotService(this.lotService);
        this.daoFactory = new DaoFactory(this.sessionProvder);
        this.createGermplasmStudySource();
        this.userId = this.findAdminUser();
        this.resolveUnitName();
    }

    @Test(expected = MiddlewareRequestException.class)
    public void testUpdatePendingTransactions_WithdrawalInvalidAvailableBalance() {

        final Lot lot = this.createLot();
        final Optional<Transaction> pendingWithrawalOptional = this.createTransactions(lot).stream().filter(this::isPendingWithdrawal).findAny();
        assertTrue(pendingWithrawalOptional.isPresent());

        final TransactionUpdateRequestDto transactionUpdateRequestDto =
                new TransactionUpdateRequestDto(pendingWithrawalOptional.get().getId(), null, 30D, null);
        this.transactionService.updatePendingTransactions(Collections.singletonList(transactionUpdateRequestDto));
    }

    @Test(expected = MiddlewareRequestException.class)
    public void testUpdatePendingTransactions_DepositInvalidAvailableBalance() {

        final Lot lot = this.createLot();
        final Optional<Transaction> pendingDepositOptional = this.createTransactions(lot).stream().filter(this::isPendingDeposit).findAny();
        assertTrue(pendingDepositOptional.isPresent());


        final TransactionUpdateRequestDto transactionUpdateRequestDto = new TransactionUpdateRequestDto(pendingDepositOptional.get().getId(), null, 2D, null);
        this.transactionService.updatePendingTransactions(Collections.singletonList(transactionUpdateRequestDto));
    }

    @Test(expected = MiddlewareRequestException.class)
    public void testUpdatePendingTransactions_WithdrawalInvalidAmount() {

        final Lot lot = this.createLot();
        final Optional<Transaction> pendingWithrawalOptional = this.createTransactions(lot).stream().filter(this::isPendingWithdrawal).findAny();
        assertTrue(pendingWithrawalOptional.isPresent());

        final TransactionUpdateRequestDto transactionUpdateRequestDto =
                new TransactionUpdateRequestDto(pendingWithrawalOptional.get().getId(), 22D, null, null);
        this.transactionService.updatePendingTransactions(Collections.singletonList(transactionUpdateRequestDto));
    }

    @Test
    public void testUpdatePendingTransactions_WithdrawalNewAmount_Ok() {

        final Lot lot = this.createLot();
        final Optional<Transaction> pendingWithrawalOptional = this.createTransactions(lot).stream().filter(this::isPendingWithdrawal).findAny();
        assertTrue(pendingWithrawalOptional.isPresent());

        final TransactionUpdateRequestDto transactionUpdateRequestDto =
                new TransactionUpdateRequestDto(pendingWithrawalOptional.get().getId(), 20D, null, null);
        this.transactionService.updatePendingTransactions(Collections.singletonList(transactionUpdateRequestDto));
        final Transaction transaction = this.daoFactory.getTransactionDAO().getById(pendingWithrawalOptional.get().getId());
        assertEquals(-20D, transaction.getQuantity(), 0.0);
    }

    @Test
    public void testUpdatePendingTransactions_WithdrawalNewBalance_Ok() {

        final Lot lot = this.createLot();
        final Optional<Transaction> pendingWithrawalOptional = this.createTransactions(lot).stream().filter(this::isPendingWithdrawal).findAny();
        assertTrue(pendingWithrawalOptional.isPresent());

        final TransactionUpdateRequestDto transactionUpdateRequestDto =
                new TransactionUpdateRequestDto(pendingWithrawalOptional.get().getId(), null, 0D, null);
        this.transactionService.updatePendingTransactions(Collections.singletonList(transactionUpdateRequestDto));
        final Transaction transaction = this.daoFactory.getTransactionDAO().getById(pendingWithrawalOptional.get().getId());
        final LotsSearchDto lotsSearchDto = new LotsSearchDto();
        lotsSearchDto.setLotIds(Collections.singletonList(lot.getId()));
        final ExtendedLotDto lotDto = this.daoFactory.getLotDao().searchLots(lotsSearchDto, null, null).get(0);
        assertEquals(-20D, transaction.getQuantity(), 0.0);
        assertEquals(0D, lotDto.getAvailableBalance(), 0.0);
    }

    @Test
    public void testUpdatePendingTransactions_DepositNewBalance_Ok() {

        final Lot lot = this.createLot();
        final Optional<Transaction> pendingDepositOptional = this.createTransactions(lot).stream().filter(this::isPendingDeposit).findAny();
        assertTrue(pendingDepositOptional.isPresent());

        final TransactionUpdateRequestDto transactionUpdateRequestDto =
                new TransactionUpdateRequestDto(pendingDepositOptional.get().getId(), null, 20D, null);
        this.transactionService.updatePendingTransactions(Collections.singletonList(transactionUpdateRequestDto));
        final Transaction transaction = this.daoFactory.getTransactionDAO().getById(pendingDepositOptional.get().getId());
        final LotsSearchDto lotsSearchDto = new LotsSearchDto();
        lotsSearchDto.setLotIds(Collections.singletonList(lot.getId()));
        final ExtendedLotDto lotDto = this.daoFactory.getLotDao().searchLots(lotsSearchDto, null, null).get(0);
        assertEquals(2D, transaction.getQuantity(), 0.0);
        assertEquals(18D, lotDto.getAvailableBalance(), 0.0);
    }

    @Test
    public void testUpdatePendingTransactions_DepositNewAmount_Ok() {

        final Lot lot = this.createLot();
        final Optional<Transaction> pendingDepositOptional = this.createTransactions(lot).stream().filter(this::isPendingDeposit).findAny();
        assertTrue(pendingDepositOptional.isPresent());

        final TransactionUpdateRequestDto transactionUpdateRequestDto =
                new TransactionUpdateRequestDto(pendingDepositOptional.get().getId(), 5D, null, null);
        this.transactionService.updatePendingTransactions(Collections.singletonList(transactionUpdateRequestDto));
        final Transaction transaction = this.daoFactory.getTransactionDAO().getById(pendingDepositOptional.get().getId());
        final LotsSearchDto lotsSearchDto = new LotsSearchDto();
        lotsSearchDto.setLotIds(Collections.singletonList(lot.getId()));
        final ExtendedLotDto lotDto = this.daoFactory.getLotDao().searchLots(lotsSearchDto, null, null).get(0);
        assertEquals(5D, transaction.getQuantity(), 0.0);
        assertEquals(18D, lotDto.getAvailableBalance(), 0.0);
    }

    @Test
    public void testDepositLots_Ok() {

        final Lot lot = this.createLot();
        this.createTransactions(lot);

        final LotDepositRequestDto lotDepositRequestDto = new LotDepositRequestDto();
        final Map<String, Double> instructions = new HashMap<>();
        instructions.put(this.unitName, 20D);
        lotDepositRequestDto.setDepositsPerUnit(instructions);

        final List<Integer> lotIds = Collections.singletonList(lot.getId());
        this.transactionService.depositLots(this.userId, new HashSet<>(lotIds), lotDepositRequestDto, TransactionStatus.CONFIRMED, null, null);

        final LotsSearchDto lotsSearchDto = new LotsSearchDto();
        lotsSearchDto.setLotIds(lotIds);
        final List<ExtendedLotDto> extendedLotDtos = this.lotService.searchLots(lotsSearchDto, null);
        final ExtendedLotDto extendedLotDto = extendedLotDtos.get(0);
        assertEquals(38D, extendedLotDto.getAvailableBalance(), 0.0);
    }


    @Test
    public void testDepositLots_WithSourceStudy_Ok() {

        final Lot lot = this.createLot();
        this.createTransactions(lot);

        final LotDepositRequestDto lotDepositRequestDto = new LotDepositRequestDto();
        final Map<String, Double> instructions = new HashMap<>();
        instructions.put(this.unitName, 20D);
        lotDepositRequestDto.setDepositsPerUnit(instructions);
        final SearchOriginCompositeDto searchOriginCompositeDto = new SearchOriginCompositeDto();
        searchOriginCompositeDto.setSearchOrigin(SearchOriginCompositeDto.SearchOrigin.MANAGE_STUDY_SOURCE);
        searchOriginCompositeDto.setSearchRequestId(1);
        final SearchCompositeDto searchCompositeDto = new SearchCompositeDto<SearchOriginCompositeDto, Integer>();
        searchCompositeDto.setSearchRequest(searchOriginCompositeDto);
        lotDepositRequestDto.setSearchComposite(searchCompositeDto);
        final List<Integer> lotIds = Collections.singletonList(lot.getId());
        this.transactionService.depositLots(this.userId, new HashSet<>(lotIds), lotDepositRequestDto, TransactionStatus.CONFIRMED, null, null);

        final LotsSearchDto lotsSearchDto = new LotsSearchDto();
        lotsSearchDto.setLotIds(lotIds);
        final List<ExtendedLotDto> extendedLotDtos = this.lotService.searchLots(lotsSearchDto, null);
        final ExtendedLotDto extendedLotDto = extendedLotDtos.get(0);
        assertEquals(38D, extendedLotDto.getAvailableBalance(), 0.0);

        final List<Transaction> transactions = this.daoFactory.getExperimentTransactionDao()
                .getTransactionsByStudyId(this.studyId, TransactionStatus.CONFIRMED, ExperimentTransactionType.HARVESTING);

        Assert.assertEquals(1, transactions.size());
    }

    @Test
    public void testDepositLots_With_study_source_as_search_origin_Ok() {

        final Lot lot = this.createLot();
        this.createTransactions(lot);

        final LotDepositRequestDto lotDepositRequestDto = new LotDepositRequestDto();
        final Map<String, Double> instructions = new HashMap<>();
        instructions.put(this.unitName, 20D);
        lotDepositRequestDto.setDepositsPerUnit(instructions);
        final SearchOriginCompositeDto searchOriginCompositeDto = new SearchOriginCompositeDto();
        searchOriginCompositeDto.setSearchOrigin(SearchOriginCompositeDto.SearchOrigin.MANAGE_STUDY_SOURCE);
        searchOriginCompositeDto.setSearchRequestId(1);
        final SearchCompositeDto searchCompositeDto = new SearchCompositeDto<SearchOriginCompositeDto, Integer>();
        searchCompositeDto.setSearchRequest(searchOriginCompositeDto);
        lotDepositRequestDto.setSearchComposite(searchCompositeDto);
        final List<Integer> lotIds = Collections.singletonList(lot.getId());
        this.transactionService.depositLots(
                this.userId, new HashSet<>(lotIds), lotDepositRequestDto, TransactionStatus.CONFIRMED,
                TransactionSourceType.SPLIT_LOT, lot.getId());

        final LotsSearchDto lotsSearchDto = new LotsSearchDto();
        lotsSearchDto.setLotIds(lotIds);
        final List<ExtendedLotDto> extendedLotDtos = this.lotService.searchLots(lotsSearchDto, null);
        final ExtendedLotDto extendedLotDto = extendedLotDtos.get(0);
        assertEquals(38D, extendedLotDto.getAvailableBalance(), 0.0);

        final List<Transaction> transactions = this.sessionProvder.getSession().createQuery(
                        String.format("select T from %s T where lotId=%s",
                                Transaction.class.getCanonicalName(),
                                lot.getId()))
                .list();
        this.assertTransaction(transactions.get(transactions.size() - 1), TransactionType.DEPOSIT, TransactionStatus.CONFIRMED,
                20D, TransactionSourceType.SPLIT_LOT, lot.getId());
    }

    @Test(expected = MiddlewareRequestException.class)
    public void testSaveAdjustmentTransactions_InvalidNewBalance() {

        final Lot lot = this.createLot();
        this.createTransactions(lot);

        final LotUpdateBalanceRequestDto lotUpdateBalanceRequestDto = new LotUpdateBalanceRequestDto(lot.getLotUuId(), 1D, "");
        this.transactionService.saveAdjustmentTransactions(this.userId, Collections.singletonList(lotUpdateBalanceRequestDto));
    }

    @Test
    public void testSaveAdjustmentTransactions_OK() {

        // Create 2 lots to be updated
        final Lot lot1 = this.createLot();
        this.createTransactions(lot1);
        final Lot lot2 = this.createLot();
        this.createTransactions(lot2);

        final LotUpdateBalanceRequestDto lotUpdateBalanceRequestDto1 = new LotUpdateBalanceRequestDto(lot1.getLotUuId(), 3D, "");
        final LotUpdateBalanceRequestDto lotUpdateBalanceRequestDto2 = new LotUpdateBalanceRequestDto(lot2.getLotUuId(), 5D, "");

        this.transactionService.saveAdjustmentTransactions(this.userId, Arrays.asList(lotUpdateBalanceRequestDto1, lotUpdateBalanceRequestDto2));

        final LotsSearchDto lotsSearchDto = new LotsSearchDto();
        lotsSearchDto.setLotIds(Collections.singletonList(lot1.getId()));
        final List<ExtendedLotDto> extendedLotDtos = this.lotService.searchLots(lotsSearchDto, null);
        final ExtendedLotDto extendedLotDto = extendedLotDtos.get(0);
        assertEquals(3D, extendedLotDto.getActualBalance(), 0.0);

        final LotsSearchDto lotsSearchDto2 = new LotsSearchDto();
        lotsSearchDto2.setLotIds(Collections.singletonList(lot2.getId()));
        final List<ExtendedLotDto> extendedLotDtos2 = this.lotService.searchLots(lotsSearchDto2, null);
        final ExtendedLotDto extendedLotDto2 = extendedLotDtos2.get(0);
        assertEquals(5D, extendedLotDto2.getActualBalance(), 0.0);

    }

    @Test
    public void testSaveAdjustmentTransactions_NoTransactionSaved() {

        final Lot lot = this.createLot();
        this.createTransactions(lot);

        final LotUpdateBalanceRequestDto lotUpdateBalanceRequestDto = new LotUpdateBalanceRequestDto(lot.getLotUuId(), 20D, "");
        this.transactionService.saveAdjustmentTransactions(this.userId, Collections.singletonList(lotUpdateBalanceRequestDto));

        final TransactionsSearchDto transactionsSearchDto = new TransactionsSearchDto();
        transactionsSearchDto.setLotIds(Collections.singletonList(lot.getId()));
        final List<TransactionDto> transactionDtos = this.transactionService.searchTransactions(transactionsSearchDto, null);

        final LotsSearchDto lotsSearchDto = new LotsSearchDto();
        lotsSearchDto.setLotIds(Collections.singletonList(lot.getId()));
        final List<ExtendedLotDto> extendedLotDtos = this.lotService.searchLots(lotsSearchDto, null);
        final ExtendedLotDto extendedLotDto = extendedLotDtos.get(0);
        assertEquals(20D, extendedLotDto.getActualBalance(), 0.0);

        Assert.assertEquals(3, transactionDtos.size());
    }

    private Lot createLot() {
        final Lot lot = new Lot(null, this.userId, EntityType.GERMPLSM.name(), this.gid, DEFAULT_SEED_STORE_ID, UNIT_ID, LotStatus.ACTIVE.getIntValue(), 0,
                "Lot", RandomStringUtils.randomAlphabetic(35));
        lot.setLotUuId(UUID.randomUUID().toString());
        this.daoFactory.getLotDao().save(lot);
        return lot;
    }

    private List<Transaction> createTransactions(final Lot lot) {
        final Transaction confirmedDeposit =
                new Transaction(null, this.userId, lot, Util.getCurrentDate(), TransactionStatus.CONFIRMED.getIntValue(),
                        20D, "Transaction 1", Util.getCurrentDateAsIntegerValue(), null, null, null, this.userId, TransactionType.DEPOSIT.getId());

        final Transaction pendingDeposit =
                new Transaction(null, this.userId, lot, Util.getCurrentDate(), TransactionStatus.PENDING.getIntValue(),
                        20D, "Transaction 2", 0, null, null, null, this.userId, TransactionType.DEPOSIT.getId());

        final Transaction pendingWithdrawal =
                new Transaction(null, this.userId, lot, Util.getCurrentDate(), TransactionStatus.PENDING.getIntValue(),
                        -2D, "Transaction 3", 0, null, null, null, this.userId, TransactionType.WITHDRAWAL.getId());

        this.daoFactory.getTransactionDAO().save(confirmedDeposit);
        this.daoFactory.getTransactionDAO().save(pendingDeposit);
        this.daoFactory.getTransactionDAO().save(pendingWithdrawal);

        return Arrays.asList(confirmedDeposit, pendingDeposit, pendingWithdrawal);
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
        this.unitName = this.daoFactory.getCvTermDao().getById(UNIT_ID).getName();
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

    private boolean isPendingWithdrawal(final Transaction t) {
        return t.getType() == TransactionType.WITHDRAWAL.getId() && t.getStatus() == TransactionStatus.PENDING.getIntValue();
    }

    private boolean isPendingDeposit(final Transaction t) {
        return t.getType() == TransactionType.DEPOSIT.getId() && t.getStatus() == TransactionStatus.PENDING.getIntValue();
    }

}
