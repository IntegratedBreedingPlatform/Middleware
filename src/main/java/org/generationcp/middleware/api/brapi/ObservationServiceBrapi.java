package org.generationcp.middleware.api.brapi;

import org.generationcp.middleware.api.brapi.v2.observation.ObservationDto;
import org.generationcp.middleware.api.brapi.v2.observation.ObservationSearchRequestDto;

import java.util.List;

public interface ObservationServiceBrapi {

	List<ObservationDto> searchObservations(ObservationSearchRequestDto observationSearchRequestDto, Integer pageSize,
		Integer pageNumber);

	long countObservations(ObservationSearchRequestDto observationSearchRequestDto);

	List<Integer> createObservations(List<ObservationDto> observations);
}
