package org.generationcp.middleware.service.api.inventory;

import org.generationcp.middleware.domain.inventory.manager.ExtendedLotDto;
import org.generationcp.middleware.domain.inventory.manager.LotAttributeColumnDto;
import org.generationcp.middleware.domain.inventory.manager.LotDto;
import org.generationcp.middleware.domain.inventory.manager.LotGeneratorInputDto;
import org.generationcp.middleware.domain.inventory.manager.LotItemDto;
import org.generationcp.middleware.domain.inventory.manager.LotSearchMetadata;
import org.generationcp.middleware.domain.inventory.manager.LotUpdateRequestDto;
import org.generationcp.middleware.domain.inventory.manager.LotsSearchDto;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface LotService {

	void generateLotIds(CropType crop, List<Lot> lots);

	List<ExtendedLotDto> searchLots(LotsSearchDto lotsSearchDto, Pageable pageable);

	List<ExtendedLotDto> searchLotsApplyExportResultsLimit(LotsSearchDto lotsSearchDto, Pageable pageable);

	long countSearchLots(LotsSearchDto lotsSearchDto);

	Map<Integer, Map<Integer, String>> getGermplasmAttributeValues(LotsSearchDto searchDto);

	String saveLot(CropType cropType, Integer userId, LotGeneratorInputDto lotDto);

	void updateLots(List<ExtendedLotDto> lotDtos, LotUpdateRequestDto lotUpdateRequestDto, String programUUID);

	List<String> saveLots(CropType cropType, Integer userId, List<LotItemDto> lotItemDtos);

	List<LotDto> getLotsByStockIds(List<String> stockIds);

	LotSearchMetadata getLotSearchMetadata(LotsSearchDto lotsSearchDto);

	void closeLots(Integer userId, List<Integer> lotIds);

	void mergeLots(Integer userId, Integer keepLotId, LotsSearchDto lotsSearchDto);

	boolean isLocationUsedInLot(Integer locationId);

	Integer getCurrentNotationNumberForBreederIdentifier(String breederIdentifier);

	List<LotAttributeColumnDto> getLotAttributeColumnDtos(String programUUID);

}
