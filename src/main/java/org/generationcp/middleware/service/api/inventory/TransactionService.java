package org.generationcp.middleware.service.api.inventory;

import org.generationcp.middleware.domain.inventory.manager.*;
import org.generationcp.middleware.pojos.ims.TransactionSourceType;
import org.generationcp.middleware.pojos.ims.TransactionStatus;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface TransactionService {

	List<TransactionDto> searchTransactions(TransactionsSearchDto transactionsSearchDto, Pageable pageable);

	long countSearchTransactions(TransactionsSearchDto transactionsSearchDto);

	/**
	 * Withdraw a set of lots given the instructions.
	 * This function needs to be synchronized externally when used to warranty that the lots involved does not either change the available balance
	 * Once this process has started not closed
	 * Assumptions:
	 * Lots in the set are not closed
	 * Lots in the set has the unit defined
	 * @param userId
	 * @param lotIds
	 * @param lotWithdrawalInputDto
	 * @param transactionStatus
	 */
	void withdrawLots(
		Integer userId, Set<Integer> lotIds, LotWithdrawalInputDto lotWithdrawalInputDto, TransactionStatus transactionStatus);

	void confirmPendingTransactions(List<TransactionDto> confirmedTransactionDtoList);

	List<TransactionDto> getAvailableBalanceTransactions(Integer lotId);

	void updatePendingTransactions(List<TransactionUpdateRequestDto> transactionUpdateInputDtos);

	void depositLots(Integer userId, Set<Integer> lotIds, LotDepositRequestDto lotDepositRequestDto, TransactionStatus transactionStatus,
		TransactionSourceType transactionSourceType, Integer sourceId);

	void depositLots(Integer userId, Set<Integer> lotIds, List<LotDepositDto> lotDepositDtos, TransactionStatus transactionStatus);

	void cancelPendingTransactions(List<TransactionDto> transactionDtoList);

	void saveAdjustmentTransactions(Integer userId, List<LotUpdateBalanceRequestDto> lotUpdateBalanceRequestDtos);

}
