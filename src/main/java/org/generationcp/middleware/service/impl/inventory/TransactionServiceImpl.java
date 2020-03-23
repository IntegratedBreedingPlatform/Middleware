package org.generationcp.middleware.service.impl.inventory;

import org.generationcp.middleware.domain.inventory.manager.ExtendedLotDto;
import org.generationcp.middleware.domain.inventory.manager.LotWithdrawalInputDto;
import org.generationcp.middleware.domain.inventory.manager.LotsSearchDto;
import org.generationcp.middleware.domain.inventory.manager.TransactionDto;
import org.generationcp.middleware.domain.inventory.manager.TransactionsSearchDto;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.generationcp.middleware.pojos.ims.TransactionStatus;
import org.generationcp.middleware.pojos.ims.TransactionType;
import org.generationcp.middleware.service.api.PedigreeService;
import org.generationcp.middleware.service.api.inventory.TransactionService;
import org.generationcp.middleware.util.CrossExpansionProperties;
import org.generationcp.middleware.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
@Service
public class TransactionServiceImpl implements TransactionService {

	private DaoFactory daoFactory;

	@Resource
	private PedigreeService pedigreeService;

	@Autowired
	private CrossExpansionProperties crossExpansionProperties;

	public TransactionServiceImpl() {
	}

	public TransactionServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public List<TransactionDto> searchTransactions(final TransactionsSearchDto transactionsSearchDto, final Pageable pageable) {
		final List<TransactionDto> transactionDtos =
			this.daoFactory.getTransactionDAO().searchTransactions(transactionsSearchDto, pageable);
		final Set<Integer> gids =
			transactionDtos.stream().map(transactionDto -> transactionDto.getLot().getGid()).collect(Collectors.toSet());
		final Map<Integer, String> pedigreeStringMap =
			this.pedigreeService.getCrossExpansions(new HashSet<>(gids), null, this.crossExpansionProperties);
		transactionDtos.stream().forEach(transactionDto ->
			transactionDto.getLot().setPedigree(pedigreeStringMap.get(transactionDto.getLot().getGid())));
		return transactionDtos;
	}

	@Override
	public long countSearchTransactions(final TransactionsSearchDto transactionsSearchDto) {
		return this.daoFactory.getTransactionDAO().countSearchTransactions(transactionsSearchDto);
	}

	@Override
	public Integer saveTransaction(final TransactionDto transactionDto) {
		final Lot lot = new Lot();
		lot.setId(transactionDto.getLot().getLotId());
		final Transaction transaction = new Transaction();

		if (TransactionType.DEPOSIT.getValue().equalsIgnoreCase(transactionDto.getTransactionType())) {
			transaction.setType(TransactionType.DEPOSIT.getId());
		} else if (TransactionType.WITHDRAWAL.getValue().equalsIgnoreCase(transactionDto.getTransactionType())) {
			transaction.setType(TransactionType.WITHDRAWAL.getId());
		} else if (TransactionType.ADJUSTMENT.getValue().equalsIgnoreCase(transactionDto.getTransactionType())) {
			transaction.setType(TransactionType.ADJUSTMENT.getId());
		} else {
			transaction.setType(TransactionType.DISCARD.getId());
		}

		if (TransactionStatus.PENDING.getValue().equalsIgnoreCase(transactionDto.getTransactionStatus())) {
			transaction.setStatus(TransactionStatus.PENDING.getIntValue());
		}
		else if (TransactionStatus.CONFIRMED.getValue().equalsIgnoreCase(transactionDto.getTransactionStatus())) {
			transaction.setStatus(TransactionStatus.CONFIRMED.getIntValue());
		}
		else {
			transaction.setStatus(TransactionStatus.CANCELLED.getIntValue());
		}

		transaction.setLot(lot);
		transaction.setPersonId(Integer.valueOf(transactionDto.getCreatedByUsername()));
		transaction.setUserId(Integer.valueOf(transactionDto.getCreatedByUsername()));
		transaction.setTransactionDate(new Date());
		transaction.setQuantity(transactionDto.getAmount());
		transaction.setPreviousAmount(0D);
		//FIXME Commitment date in some cases is not 0. For Deposits is always zero, but for other types it will be the current date
		transaction.setCommitmentDate(0);
		transaction.setComments(transactionDto.getNotes());
		return this.daoFactory.getTransactionDAO().saveOrUpdate(transaction).getId();
	}


	@Override
	public void withdrawLots(final Integer userId, final Set<Integer> lotIds, final LotWithdrawalInputDto lotWithdrawalInputDto,
			final TransactionStatus transactionStatus) {

		final LotsSearchDto lotsSearchDto = new LotsSearchDto();
		lotsSearchDto.setLotIds(new ArrayList<>(lotIds));
		final List<ExtendedLotDto> lots = this.daoFactory.getLotDao().searchLots(lotsSearchDto, null);

		for (final ExtendedLotDto lotDto : lots) {
			boolean withdrawAll = lotWithdrawalInputDto.getWithdrawalsPerUnit().get(lotDto.getUnitName()).isReserveAllAvailableBalance();
			final Double amount = lotWithdrawalInputDto.getWithdrawalsPerUnit().get(lotDto.getUnitName()).getWithdrawalAmount();

			final Double amountToWithdraw = (withdrawAll) ? lotDto.getAvailableBalance() : amount;

			if (lotDto.getAvailableBalance().equals(0D)) {
				throw new MiddlewareException("One of the selected lots does not have enough available inventory to perform a withdrawal. Please review.");
			}

			if (lotDto.getAvailableBalance() < amountToWithdraw) {
				throw new MiddlewareException("One of the selected lots does not have enough available inventory to perform the withdrawal. Please review the amount");
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
			//Always zero for new transactions
			transaction.setPreviousAmount(0D);
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
		final List<Integer> transactionIds = confirmedTransactionDtos.stream().map(TransactionDto::getTransactionId).collect(
			Collectors.toList());

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

}
