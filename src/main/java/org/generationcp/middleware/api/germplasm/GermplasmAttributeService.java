package org.generationcp.middleware.api.germplasm;

import org.generationcp.middleware.api.brapi.v1.attribute.AttributeDTO;
import org.generationcp.middleware.domain.germplasm.GermplasmAttributeDto;
import org.generationcp.middleware.domain.germplasm.GermplasmAttributeRequestDto;
import org.generationcp.middleware.domain.ontology.Variable;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface GermplasmAttributeService {

	List<GermplasmAttributeDto> getGermplasmAttributeDtos(Integer gid, Integer variableTypeId, String programUUID);

	Integer createGermplasmAttribute(Integer gid, GermplasmAttributeRequestDto dto);

	void updateGermplasmAttribute(Integer attributeId, GermplasmAttributeRequestDto dto);

	void deleteGermplasmAttribute(Integer attributeId);

	List<AttributeDTO> getAttributesByGUID(String germplasmUUID, List<String> attributeDbIds, Pageable pageable);

	long countAttributesByGUID(String germplasmUUID, List<String> attributeDbIds);

	List<Variable> getGermplasmAttributeVariables(List<Integer> gids, String programUUID);

	Map<Integer, List<AttributeDTO>> getAttributesByGIDsMap(List<Integer> gids);

	boolean isLocationUsedInAttribute(Integer locationId);

}
