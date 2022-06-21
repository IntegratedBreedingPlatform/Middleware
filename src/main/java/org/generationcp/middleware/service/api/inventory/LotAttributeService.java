package org.generationcp.middleware.service.api.inventory;

import org.generationcp.middleware.domain.shared.AttributeRequestDto;
import org.generationcp.middleware.domain.shared.AttributeDto;

import java.util.List;

public interface LotAttributeService {

	List<AttributeDto> getLotAttributeDtos(Integer lotId, String programUUID);

	Integer createLotAttribute(Integer gid, AttributeRequestDto dto);

	void updateLotAttribute(Integer attributeId, AttributeRequestDto dto);

	void deleteLotAttribute(Integer attributeId);

}
