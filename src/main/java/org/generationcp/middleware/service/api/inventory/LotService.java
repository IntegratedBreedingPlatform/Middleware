package org.generationcp.middleware.service.api.inventory;

import org.generationcp.middleware.domain.inventory.manager.ExtendedLotDto;
import org.generationcp.middleware.domain.inventory.manager.LotDto;
import org.generationcp.middleware.domain.inventory.manager.LotGeneratorInputDto;
import org.generationcp.middleware.domain.inventory.manager.LotItemDto;
import org.generationcp.middleware.domain.inventory.manager.LotSearchMetadata;
import org.generationcp.middleware.domain.inventory.manager.LotsSearchDto;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LotService {

	List<ExtendedLotDto> searchLots(LotsSearchDto lotsSearchDto, Pageable pageable);

	long countSearchLots(LotsSearchDto lotsSearchDto);

	Integer saveLot(CropType cropType, Integer userId, LotGeneratorInputDto lotDto);

	void saveLotsWithInitialTransaction(CropType cropType, Integer userId, List<LotItemDto> lotItemDtos);

	List<LotDto> getLotsByStockIds(List<String> stockIds);

	LotSearchMetadata getLotSearchMetadata(LotsSearchDto lotsSearchDto);

}
