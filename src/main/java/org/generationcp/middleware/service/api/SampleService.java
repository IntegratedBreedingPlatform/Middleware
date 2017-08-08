package org.generationcp.middleware.service.api;

import org.generationcp.middleware.pojos.Sample;
import org.generationcp.middleware.pojos.SampleList;

import java.util.Date;

public interface SampleService {

	public Sample createOrUpdateSample(String cropName, Integer plantNumber, String username, String sampleName, Date samplingDate,
		Integer experimentId, SampleList sampleList);

}
