package org.generationcp.middleware.service.api.inventory;

import org.generationcp.middleware.domain.inventory.manager.ExtendedLotDto;
import org.generationcp.middleware.domain.inventory.manager.LotDto;
import org.generationcp.middleware.domain.inventory.manager.LotGeneratorInputDto;
import org.generationcp.middleware.domain.inventory.manager.LotItemDto;
import org.generationcp.middleware.domain.inventory.manager.LotSearchMetadata;
import org.generationcp.middleware.domain.inventory.manager.LotWithdrawalInputDto;
import org.generationcp.middleware.domain.inventory.manager.LotsSearchDto;
import org.generationcp.middleware.pojos.ims.TransactionStatus;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface LotService {

	List<ExtendedLotDto> searchLots(LotsSearchDto lotsSearchDto, Pageable pageable);

	long countSearchLots(LotsSearchDto lotsSearchDto);

	Integer saveLot(CropType cropType, Integer userId, LotGeneratorInputDto lotDto);

	void saveLotsWithInitialTransaction(CropType cropType, Integer userId, List<LotItemDto> lotItemDtos);

	List<LotDto> getLotsByStockIds(List<String> stockIds);

	LotSearchMetadata getLotSearchMetadata(LotsSearchDto lotsSearchDto);

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
	void withdrawLots(Integer userId, Set<Integer> lotIds, LotWithdrawalInputDto lotWithdrawalInputDto, TransactionStatus transactionStatus) ;

}
