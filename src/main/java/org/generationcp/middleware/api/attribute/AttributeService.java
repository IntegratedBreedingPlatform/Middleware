package org.generationcp.middleware.api.attribute;

import org.generationcp.middleware.domain.germplasm.GermplasmAttributeDto;

import java.util.List;

public interface AttributeService {
	List<AttributeDTO> searchAttributes(String name);

	List<GermplasmAttributeDto> getGermplasmAttributeDtos(Integer gid, String attributeType);
}
