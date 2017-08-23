
package org.generationcp.middleware.service.api;

import org.generationcp.middleware.domain.sample.SampleDTO;
import org.generationcp.middleware.pojos.Sample;
import org.generationcp.middleware.pojos.SampleList;

import java.util.Date;
import java.util.List;

public interface SampleService {

	Sample buildSample(final String cropName, String cropPrefix, final Integer plantNumber, final String username,
			final String sampleName, final Date samplingDate, final Integer experimentId, final SampleList sampleList);

	List<SampleDTO> getSamples(String plot_id);
}
