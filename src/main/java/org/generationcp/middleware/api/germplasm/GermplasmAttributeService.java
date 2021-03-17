package org.generationcp.middleware.api.germplasm;

import org.generationcp.middleware.api.attribute.AttributeDTO;
import org.generationcp.middleware.domain.germplasm.GermplasmAttributeDto;
import org.generationcp.middleware.domain.germplasm.GermplasmAttributeRequestDto;

import java.util.List;
import java.util.Set;

public interface GermplasmAttributeService {

	List<GermplasmAttributeDto> getGermplasmAttributeDtos(Integer gid, String attributeType);

	Integer createGermplasmAttribute(Integer gid, GermplasmAttributeRequestDto dto, Integer userId);

	Integer updateGermplasmAttribute(Integer attributeId, GermplasmAttributeRequestDto dto);

	void deleteGermplasmAttribute(Integer attributeId);

	List<AttributeDTO> filterGermplasmAttributes(Set<String> codes, Set<String> types);

}
