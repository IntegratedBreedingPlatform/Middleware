package org.generationcp.middleware.api.brapi.v2.observationunit;

public interface ObservationUnitService {

	void update(String observationUnitDbId, ObservationUnitPatchRequestDTO requestDTO);
}
