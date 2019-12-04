package org.generationcp.middleware.service.api.inventory;

import org.generationcp.middleware.domain.inventory_new.ExtendedLotDto;
import org.generationcp.middleware.domain.inventory_new.LotGeneratorInputDto;
import org.generationcp.middleware.domain.inventory_new.LotsSearchDto;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LotService {

	List<ExtendedLotDto> searchLots(LotsSearchDto lotsSearchDto, Pageable pageable);

	long countSearchLots(LotsSearchDto lotsSearchDto);

	Integer saveLot(LotGeneratorInputDto lotDto, final CropType cropType);

}
