package org.generationcp.middleware.service.impl.inventory;

import org.generationcp.middleware.domain.inventory.manager.ExtendedLotDto;
import org.generationcp.middleware.domain.inventory.manager.LotDepositDto;
import org.generationcp.middleware.domain.inventory.manager.LotDepositRequestDto;
import org.generationcp.middleware.domain.inventory.manager.LotWithdrawalInputDto;
import org.generationcp.middleware.domain.inventory.manager.LotsSearchDto;
import org.generationcp.middleware.domain.inventory.manager.TransactionDto;
import org.generationcp.middleware.domain.inventory.manager.TransactionUpdateRequestDto;
import org.generationcp.middleware.domain.inventory.manager.TransactionsSearchDto;
import org.generationcp.middleware.exceptions.MiddlewareRequestException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.GermplasmStudySource;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.ims.ExperimentTransaction;
import org.generationcp.middleware.pojos.ims.ExperimentTransactionType;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.generationcp.middleware.pojos.ims.TransactionSourceType;
import org.generationcp.middleware.pojos.ims.TransactionStatus;
import org.generationcp.middleware.pojos.ims.TransactionType;
import org.generationcp.middleware.service.api.inventory.LotService;
import org.generationcp.middleware.service.api.inventory.TransactionService;
import org.generationcp.middleware.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Transactional
@Service
public class TransactionServiceImpl implements TransactionService {

	private DaoFactory daoFactory;

	@Autowired
	private LotService lotService;

	public TransactionServiceImpl() {
	}

	public TransactionServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public List<TransactionDto> searchTransactions(final TransactionsSearchDto transactionsSearchDto, final Pageable pageable) {
		final List<TransactionDto> transactionDtos =
			this.daoFactory.getTransactionDAO().searchTransactions(transactionsSearchDto, pageable);
		return transactionDtos;
	}

	@Override
	public long countSearchTransactions(final TransactionsSearchDto transactionsSearchDto) {
		return this.daoFactory.getTransactionDAO().countSearchTransactions(transactionsSearchDto);
	}

	@Override
	public void withdrawLots(final Integer userId, final Set<Integer> lotIds, final LotWithdrawalInputDto lotWithdrawalInputDto,
		final TransactionStatus transactionStatus) {

		final LotsSearchDto lotsSearchDto = new LotsSearchDto();
		lotsSearchDto.setLotIds(new ArrayList<>(lotIds));
		final List<ExtendedLotDto> lots = this.lotService.searchLots(lotsSearchDto, null);

		for (final ExtendedLotDto lotDto : lots) {
			boolean withdrawAll = lotWithdrawalInputDto.getWithdrawalsPerUnit().get(lotDto.getUnitName()).isReserveAllAvailableBalance();
			final Double amount = lotWithdrawalInputDto.getWithdrawalsPerUnit().get(lotDto.getUnitName()).getWithdrawalAmount();

			final Double amountToWithdraw = (withdrawAll) ? lotDto.getAvailableBalance() : amount;

			if (lotDto.getAvailableBalance().equals(0D)) {
				throw new MiddlewareRequestException("", "lot.withdrawal.zero.balance");
			}

			if (lotDto.getAvailableBalance() < amountToWithdraw) {
				throw new MiddlewareRequestException("", "lot.withdrawal.not.enough.inventory");
			}

			final Transaction transaction = new Transaction();
			transaction.setStatus(transactionStatus.getIntValue());
			transaction.setType(TransactionType.WITHDRAWAL.getId());
			transaction.setLot(new Lot(lotDto.getLotId()));
			transaction.setPersonId(userId);
			transaction.setUserId(userId);
			transaction.setTransactionDate(new Date());
			transaction.setQuantity(-1 * amountToWithdraw);
			transaction.setComments(lotWithdrawalInputDto.getNotes());
			if (transactionStatus.equals(TransactionStatus.CONFIRMED)) {
				transaction.setCommitmentDate(Util.getCurrentDateAsIntegerValue());
			} else {
				transaction.setCommitmentDate(0);
			}
			daoFactory.getTransactionDAO().save(transaction);

		}
	}

	@Override
	public void confirmPendingTransactions(final List<TransactionDto> confirmedTransactionDtos) {
		final Set<Integer> transactionIds = confirmedTransactionDtos.stream().map(TransactionDto::getTransactionId).collect(
			Collectors.toSet());

		final List<Transaction> transactions = daoFactory.getTransactionDAO().getByIds(transactionIds);
		for (final Transaction transaction : transactions) {
			transaction.setStatus(TransactionStatus.CONFIRMED.getIntValue());
			transaction.setCommitmentDate(Util.getCurrentDateAsIntegerValue());
			daoFactory.getTransactionDAO().update(transaction);
		}
	}

	@Override
	public List<TransactionDto> getAvailableBalanceTransactions(final Integer lotId) {
		return this.daoFactory.getTransactionDAO().getAvailableBalanceTransactions(lotId);
	}

	@Override
	public void updatePendingTransactions(final List<TransactionUpdateRequestDto> transactionUpdateRequestDtos) {

		final Set<Integer> transactionIds =
			transactionUpdateRequestDtos.stream().map(TransactionUpdateRequestDto::getTransactionId).collect(
				Collectors.toSet());
		final List<Transaction> transactions = this.daoFactory.getTransactionDAO().getByIds(transactionIds);
		final Map<Integer, Transaction> transactionMap = transactions.stream().collect(Collectors.toMap(x -> x.getId(), x -> x));

		for (final TransactionUpdateRequestDto updateRequestDto : transactionUpdateRequestDtos) {
			final Transaction transaction = transactionMap.get(updateRequestDto.getTransactionId());
			final List<Integer> lotIds = Arrays.asList(transaction.getLot().getId());
			final LotsSearchDto lotsSearchDto = new LotsSearchDto();
			lotsSearchDto.setLotIds(lotIds);

			//Needs to be queried per transaction so we get the most recent available balance when editing transaction for the same lot.
			final ExtendedLotDto lotDto = this.lotService.searchLots(lotsSearchDto, null).get(0);

			if (updateRequestDto.getNotes() != null) {
				transaction.setComments(updateRequestDto.getNotes());
			}

			if (updateRequestDto.getAmount() != null) {
				if (TransactionType.DEPOSIT.getId().equals(transaction.getType())) {
					transaction.setQuantity(updateRequestDto.getAmount());
				}

				if (TransactionType.WITHDRAWAL.getId().equals(transaction.getType())) {
					if (lotDto.getAvailableBalance() - transaction.getQuantity() - updateRequestDto.getAmount() < 0) {
						throw new MiddlewareRequestException("", "transaction.update.negative.balance",
							String.valueOf(transaction.getId()));
					} else {
						transaction.setQuantity(-1 * updateRequestDto.getAmount());
					}
				}

			} else if (updateRequestDto.getAvailableBalance() != null) {
				if (TransactionType.DEPOSIT.getId().equals(transaction.getType())) {
					if (updateRequestDto.getAvailableBalance() <= lotDto.getAvailableBalance()) {
						throw new MiddlewareRequestException("", "transaction.update.new.balance.lower.than.actual",
							String.valueOf(transaction.getId()), String.valueOf(lotDto.getLotId()));
					} else {
						transaction.setQuantity(updateRequestDto.getAvailableBalance() - lotDto.getAvailableBalance());
					}
				}

				if (TransactionType.WITHDRAWAL.getId().equals(transaction.getType())) {
					if (updateRequestDto.getAvailableBalance() >= lotDto.getAvailableBalance() - transaction.getQuantity()) {
						throw new MiddlewareRequestException("", "transaction.update.new.balance.not.a.withdrawal",
							String.valueOf(transaction.getId()));
					} else {
						transaction
							.setQuantity(updateRequestDto.getAvailableBalance() - lotDto.getAvailableBalance() + transaction.getQuantity());
					}
				}
			}
			this.daoFactory.getTransactionDAO().update(transaction);
		}

	}

	@Override
	public void depositLots(final Integer userId, final Set<Integer> lotIds, final LotDepositRequestDto lotDepositRequestDto,
		final TransactionStatus transactionStatus, final TransactionSourceType transactionSourceType,
		final Integer sourceId) {
		final LotsSearchDto lotsSearchDto = new LotsSearchDto();
		lotsSearchDto.setLotIds(new ArrayList<>(lotIds));
		final List<ExtendedLotDto> lots = this.lotService.searchLots(lotsSearchDto, null);

		final Map<Integer, GermplasmStudySource> germplasmStudySourceMap =
			this.daoFactory.getGermplasmStudySourceDAO()
				.getByGids(lots.stream().map(ExtendedLotDto::getGid).collect(Collectors.toSet())).stream()
				.collect(Collectors.toMap(a -> a.getGermplasm().getGid(), Function.identity()));

		for (final ExtendedLotDto extendedLotDto : lots) {
			final Double amount = lotDepositRequestDto.getDepositsPerUnit().get(extendedLotDto.getUnitName());
			final Transaction transaction =
				new Transaction(TransactionType.DEPOSIT, transactionStatus, userId, lotDepositRequestDto.getNotes(),
					extendedLotDto.getLotId(),
					amount);
			if (!Objects.isNull(transactionSourceType)) {
				transaction.setSourceType(transactionSourceType.name());
			}
			if (!Objects.isNull(sourceId)) {
				transaction.setSourceId(sourceId);
			}
			daoFactory.getTransactionDAO().save(transaction);

			if (lotDepositRequestDto.getSourceStudyId() != null) {
				// Create experiment transaction records when lot and deposit are created in the context of study.
				this.createExperimentTransaction(extendedLotDto.getGid(), germplasmStudySourceMap, transaction,
					ExperimentTransactionType.HARVESTING);
			}
		}

	}

	@Override
	public void depositLots(final Integer userId, final Set<Integer> lotIds, final List<LotDepositDto> lotDepositDtoList,
		final TransactionStatus transactionStatus) {

		final LotsSearchDto lotsSearchDto = new LotsSearchDto();
		lotsSearchDto.setLotIds(new ArrayList<>(lotIds));
		final List<ExtendedLotDto> lots = this.lotService.searchLots(lotsSearchDto, null);
		final Map<String, ExtendedLotDto> extendedLotDtoMap =
			lots.stream().collect(Collectors.toMap(ExtendedLotDto::getLotUUID, extendedLotDto -> extendedLotDto));

		for (final LotDepositDto lotDepositDto : lotDepositDtoList) {
			final ExtendedLotDto extendedLotDto = extendedLotDtoMap.get(lotDepositDto.getLotUID());
			final Transaction transaction =
				new Transaction(TransactionType.DEPOSIT, transactionStatus, userId, lotDepositDto.getNotes(), extendedLotDto.getLotId(),
					lotDepositDto.getAmount());
			this.daoFactory.getTransactionDAO().save(transaction);
		}
	}

	private void createExperimentTransaction(final Integer gid, final Map<Integer, GermplasmStudySource> germplasmStudySourceMap,
		final Transaction transaction, final ExperimentTransactionType experimentTransactionType) {
		if (germplasmStudySourceMap.containsKey(gid)) {
			final ExperimentModel experimentModel = germplasmStudySourceMap.get(gid).getExperimentModel();
			if (experimentModel != null) {
				this.daoFactory.getExperimentTransactionDao()
					.save(new ExperimentTransaction(experimentModel, transaction, experimentTransactionType.getId()));
			}
		}
	}

	@Override
	public void cancelPendingTransactions(final List<TransactionDto> transactionDtoList) {
		final Set<Integer> transactionIds = transactionDtoList.stream().map(TransactionDto::getTransactionId).collect(
			Collectors.toSet());

		final List<Transaction> transactions = daoFactory.getTransactionDAO().getByIds(transactionIds);
		for (final Transaction transaction : transactions) {
			transaction.setStatus(TransactionStatus.CANCELLED.getIntValue());
			transaction.setCommitmentDate(Util.getCurrentDateAsIntegerValue());
			daoFactory.getTransactionDAO().update(transaction);
		}
	}

	@Override
	public void saveAdjustmentTransactions(final Integer userId, final Set<Integer> lotIds, final Double balance, final String notes) {
		final LotsSearchDto lotsSearchDto = new LotsSearchDto();
		lotsSearchDto.setLotIds(new ArrayList<>(lotIds));
		final List<ExtendedLotDto> lots = this.lotService.searchLots(lotsSearchDto, null);
		for (final ExtendedLotDto lotDto : lots) {
			if (balance >= lotDto.getReservedTotal()) {
				final Double amount = balance - lotDto.getActualBalance();
				if (amount != 0) {
					final Transaction transaction =
						new Transaction(TransactionType.ADJUSTMENT, TransactionStatus.CONFIRMED, userId, notes, lotDto.getLotId(), amount);
					daoFactory.getTransactionDAO().save(transaction);
				}
			} else {
				throw new MiddlewareRequestException("", "lot.balance.update.invalid.available.balance",
					String.valueOf(lotDto.getStockId()));
			}
		}
	}

	public void setLotService(final LotService lotService) {
		this.lotService = lotService;
	}

}
