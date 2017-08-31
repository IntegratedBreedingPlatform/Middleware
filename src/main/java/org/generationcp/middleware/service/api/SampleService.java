
package org.generationcp.middleware.service.api;

import java.util.Date;

import org.generationcp.middleware.pojos.Sample;
import org.generationcp.middleware.pojos.SampleList;
import org.generationcp.middleware.pojos.User;

public interface SampleService {

	Sample buildSample(final String cropName, final String cropPrefix, final Integer plantNumber, final String sampleName,
		final Date samplingDate, final Integer experimentId, final SampleList sampleList, User createdBy, Date createdDate, User takenBy);
}
