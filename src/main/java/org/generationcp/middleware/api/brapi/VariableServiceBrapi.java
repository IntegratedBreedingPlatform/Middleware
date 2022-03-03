package org.generationcp.middleware.api.brapi;

import org.generationcp.middleware.api.brapi.v2.attribute.AttributeDTO;
import org.generationcp.middleware.domain.search_request.brapi.v2.AttributeSearchRequestDTO;
import org.generationcp.middleware.domain.search_request.brapi.v2.VariableSearchRequestDTO;
import org.generationcp.middleware.service.api.study.VariableDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface VariableServiceBrapi {

    List<VariableDTO> getObservationVariables(VariableSearchRequestDTO requestDTO, Pageable pageable);

    long countObservationVariables(VariableSearchRequestDTO requestDTO);

	List<AttributeDTO> getGermplasmAttributes(AttributeSearchRequestDTO requestDTO, Pageable pageable);

	long countGermplasmAttributes(AttributeSearchRequestDTO requestDTO);

	VariableDTO updateObservationVariable(VariableDTO variable);
}
