package org.generationcp.middleware.service.api.inventory;

import org.generationcp.middleware.domain.germplasm.AttributeRequestDto;

public interface LotAttributeService {

	Integer createLotAttribute(Integer gid, AttributeRequestDto dto);

	void updateLotAttribute(Integer attributeId, AttributeRequestDto dto);

	void deleteLotAttribute(Integer attributeId);

}
