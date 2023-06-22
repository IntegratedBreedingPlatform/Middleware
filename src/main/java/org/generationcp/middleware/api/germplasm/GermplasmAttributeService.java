package org.generationcp.middleware.api.germplasm;

import org.generationcp.middleware.api.brapi.v1.attribute.AttributeDTO;
import org.generationcp.middleware.api.germplasm.search.GermplasmAttributeSearchRequest;
import org.generationcp.middleware.domain.shared.AttributeDto;
import org.generationcp.middleware.domain.shared.AttributeRequestDto;
import org.generationcp.middleware.domain.ontology.Variable;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface GermplasmAttributeService {

	List<AttributeDto> getGermplasmAttributeDtos(GermplasmAttributeSearchRequest germplasmAttributeSearchRequest);

	Integer createGermplasmAttribute(Integer gid, AttributeRequestDto dto);

	void updateGermplasmAttribute(Integer attributeId, AttributeRequestDto dto);

	void deleteGermplasmAttribute(Integer attributeId);

	List<AttributeDTO> getAttributesByGUID(String germplasmUUID, List<String> attributeDbIds, Pageable pageable);

	long countAttributesByGUID(String germplasmUUID, List<String> attributeDbIds);

	List<Variable> getGermplasmAttributeVariables(List<Integer> gids, String programUUID);

	Map<Integer, List<AttributeDTO>> getAttributesByGIDsMap(List<Integer> gids);

	boolean isLocationUsedInAttribute(Integer locationId);

}
