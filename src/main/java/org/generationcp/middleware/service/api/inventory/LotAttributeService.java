package org.generationcp.middleware.service.api.inventory;

import org.generationcp.middleware.domain.germplasm.AttributeRequestDto;
import org.generationcp.middleware.domain.germplasm.GermplasmAttributeDto;

import java.util.List;

public interface LotAttributeService {

	List<GermplasmAttributeDto> getLotAttributeDtos(Integer lotId);

	Integer createLotAttribute(Integer gid, AttributeRequestDto dto);

	void updateLotAttribute(Integer attributeId, AttributeRequestDto dto);

	void deleteLotAttribute(Integer attributeId);

}
