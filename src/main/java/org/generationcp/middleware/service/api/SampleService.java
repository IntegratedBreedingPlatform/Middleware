package org.generationcp.middleware.service.api;

import org.generationcp.middleware.domain.sample.SampleDTO;

public interface SampleService {

	Integer createSample(SampleDTO sampleDTO);

	SampleDTO getSample(Integer sampleId);

}
