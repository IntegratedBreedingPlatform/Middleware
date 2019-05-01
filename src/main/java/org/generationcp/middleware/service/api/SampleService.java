
package org.generationcp.middleware.service.api;

import org.generationcp.middleware.domain.sample.SampleDTO;
import org.generationcp.middleware.domain.sample.SampleDetailsDTO;
import org.generationcp.middleware.pojos.Sample;
import org.generationcp.middleware.pojos.SampleList;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.service.api.sample.SampleSearchRequestDto;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface SampleService {

	Sample buildSample(final String cropName, final String cropPrefix, final Integer entryNumber,
		final String sampleName, final Date samplingDate, final Integer experimentId, final SampleList sampleList, User createdBy,
		Date createdDate, User takenBy, Integer sampleNumber);

	List<SampleDTO> filter(final String obsUnitId, Integer listId, Pageable pageable);

	/**
	 * count results from {@link #filter}
	 */
	long countFilter(final String obsUnitId, final Integer listId);

	SampleDetailsDTO getSampleObservation(final String sampleId);

	Map<String, SampleDTO> getSamplesBySampleUID (final Set<String> sampleUIDs);

	List<SampleDTO> getByGid(final Integer gid);

	Boolean studyHasSamples(final Integer studyId);

	long countSearchSampleObservationsResults(final SampleSearchRequestDto sampleSearchRequestDto);

	List<SampleDetailsDTO> searchSampleObservations(final SampleSearchRequestDto sampleSearchRequestDto);

}
