package org.generationcp.middleware.service.api.inventory;

import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.domain.shared.AttributeDto;
import org.generationcp.middleware.domain.shared.AttributeRequestDto;

import java.util.List;
import java.util.Map;

public interface LotAttributeService {

	List<AttributeDto> getLotAttributeDtos(Integer lotId, String programUUID);

	Integer createLotAttribute(Integer gid, AttributeRequestDto dto);

	void updateLotAttribute(Integer attributeId, AttributeRequestDto dto);

	void deleteLotAttribute(Integer attributeId);

	List<Variable> getLotAttributeVariables(List<Integer> lotIds, String programUUID);

	Map<Integer, Map<Integer, String>> getAttributesByLotIdsMap(List<Integer> lotIds);
}
