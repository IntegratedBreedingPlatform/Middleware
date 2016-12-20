
package org.generationcp.middleware.service.api.study;

import java.util.List;

public interface StudyService {

	List<StudySummary> search(final StudySearchParameters serchParameters);

	List<ObservationDto> getObservations(final int studyIdentifier);

	List<ObservationDto> getSingleObservation(final int studyIdentifier, final int measurementIdentifier);

	ObservationDto updataObservation(final Integer studyIdentifier, final ObservationDto middlewareMeasurement);

	List<StudyGermplasmDto> getStudyGermplasmList(Integer studyIdentifer);

	String getProgramUUID(Integer studyIdentifier);

	StudyDetailDto getStudyDetails(int studyIdentifier);

	/**
	 *
	 * @param studyIdentifier id for the study (Nursery / Trial)
	 * @param instanceDbId id for a Trial instance of a Trial (Nursery has 1 instance). If present studyIdentifier will not be used
	 * @return
	 */
	StudyDetailDto getStudyDetails(int studyIdentifier, Integer instanceDbId);
}
