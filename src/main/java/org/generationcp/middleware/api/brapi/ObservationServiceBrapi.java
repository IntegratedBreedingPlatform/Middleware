package org.generationcp.middleware.api.brapi;

import org.generationcp.middleware.api.brapi.v2.observation.ObservationDto;
import org.generationcp.middleware.api.brapi.v2.observation.ObservationSearchRequestDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ObservationServiceBrapi {

	List<ObservationDto> searchObservations(ObservationSearchRequestDto observationSearchRequestDto, Pageable pageable);

	long countObservations(ObservationSearchRequestDto observationSearchRequestDto);

	List<ObservationDto> createObservations(List<ObservationDto> observations);

	List<ObservationDto> updateObservations(List<ObservationDto> observations);
}
