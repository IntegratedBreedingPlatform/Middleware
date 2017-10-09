
package org.generationcp.middleware.service.api;

import org.generationcp.middleware.domain.sample.SampleDTO;
import org.generationcp.middleware.domain.sample.SampleDetailsDTO;
import org.generationcp.middleware.pojos.Sample;
import org.generationcp.middleware.pojos.SampleList;
import org.generationcp.middleware.pojos.User;

import java.util.Date;
import java.util.List;

public interface SampleService {

	Sample buildSample(final String cropName, final String cropPrefix, final Integer plantNumber, final String sampleName,
		final Date samplingDate, final Integer experimentId, final SampleList sampleList, User createdBy, Date createdDate, User takenBy);

	List<SampleDTO> getSamples(final String plot_id);

	SampleDetailsDTO getSampleObservation(final String sampleId);

	List<SampleDetailsDTO> getSamples(final Integer sampleListId);
}
