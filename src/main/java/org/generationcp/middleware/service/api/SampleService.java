
package org.generationcp.middleware.service.api;

import java.util.Date;

import org.generationcp.middleware.pojos.Sample;
import org.generationcp.middleware.pojos.SampleList;

public interface SampleService {

	Sample buildSample(final String cropName, String cropPrefix, final Integer plantNumber, final String username,
			final String sampleName, final Date samplingDate, final Integer experimentId, final SampleList sampleList);

}
