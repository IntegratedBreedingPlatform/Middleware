package org.generationcp.middleware.service.api;

import org.generationcp.middleware.domain.sample.SampleDTO;

import java.util.Collection;
import java.util.List;

public interface SampleService {

	Integer createOrUpdateSample(SampleDTO sampleDTO);

	SampleDTO getSample(Integer sampleId);

  	List<SampleDTO> getSamples(Collection<Integer> sampleIds);
}
