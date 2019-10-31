package org.generationcp.middleware.service.api.inventory;

import org.generationcp.middleware.domain.inventory_new.TransactionDto;
import org.generationcp.middleware.domain.inventory_new.TransactionsSearchDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TransactionService {

	List<TransactionDto> searchTransactions(TransactionsSearchDto transactionsSearchDto, Pageable pageable);

	long countSearchTransactions(TransactionsSearchDto transactionsSearchDto);

}
