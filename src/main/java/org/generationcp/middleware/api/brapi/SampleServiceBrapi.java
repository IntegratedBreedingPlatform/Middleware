
package org.generationcp.middleware.api.brapi;

import org.generationcp.middleware.domain.search_request.brapi.v2.SampleSearchRequestDTO;
import org.generationcp.middleware.service.api.sample.SampleObservationDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SampleServiceBrapi {

	List<SampleObservationDto> getSampleObservations(SampleSearchRequestDTO sampleSearchRequestDTO, Pageable pageable);

	long countSampleObservations(SampleSearchRequestDTO sampleSearchRequestDTO);
}
