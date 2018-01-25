
package org.generationcp.middleware.service.api;

import org.generationcp.middleware.domain.sample.SampleDTO;
import org.generationcp.middleware.domain.sample.SampleDetailsDTO;
import org.generationcp.middleware.domain.sample.SampleGermplasmDetailDTO;
import org.generationcp.middleware.pojos.Sample;
import org.generationcp.middleware.pojos.SampleList;
import org.generationcp.middleware.pojos.User;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface SampleService {

	Sample buildSample(final String cropName, final String cropPrefix, final Integer plantNumber, final String sampleName,
		final Date samplingDate, final Integer experimentId, final SampleList sampleList, User createdBy, Date createdDate, User takenBy);

	List<SampleDTO> getSamples(final String plotId);

	SampleDetailsDTO getSampleObservation(final String sampleId);

	Map<String, SampleDTO> getSamplesBySampleUID (final Set<String> sampleUIDs);

	List<SampleGermplasmDetailDTO> getByGid(final Integer gid);

	Boolean studyHasSamples(final Integer studyId);

}
