package org.generationcp.middleware.service.api;

import org.generationcp.middleware.domain.sample.Sample;

public interface SampleService {

	String createSample(Sample sample);

	Sample getSample(String sampleId);

}
