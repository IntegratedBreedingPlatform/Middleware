package org.generationcp.middleware.api.brapi;

import org.generationcp.middleware.domain.search_request.brapi.v2.VariableSearchRequestDTO;
import org.generationcp.middleware.service.api.study.VariableDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface VariableServiceBrapi {

    List<VariableDTO> getVariables(VariableSearchRequestDTO requestDTO, Pageable pageable, VariableTypeGroup variableTypeGroup);

    long countVariables(VariableSearchRequestDTO requestDTO, VariableTypeGroup variableTypeGroup);

	VariableDTO updateObservationVariable(VariableDTO variable);
}
