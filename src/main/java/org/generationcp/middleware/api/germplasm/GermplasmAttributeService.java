package org.generationcp.middleware.api.germplasm;

import org.generationcp.middleware.domain.germplasm.GermplasmAttributeDto;
import org.generationcp.middleware.domain.germplasm.GermplasmAttributeRequestDto;

import java.util.List;

public interface GermplasmAttributeService {

	List<GermplasmAttributeDto> getGermplasmAttributeDtos(Integer gid, String attributeType);

	Integer createGermplasmAttribute(Integer gid, GermplasmAttributeRequestDto dto, Integer userId);

	Integer updateGermplasmAttribute(Integer attributeId, GermplasmAttributeRequestDto dto);

	void deleteGermplasmAttribute(Integer attributeId);

}
