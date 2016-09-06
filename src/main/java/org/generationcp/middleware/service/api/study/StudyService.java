
package org.generationcp.middleware.service.api.study;

import java.util.List;

import org.generationcp.middleware.service.impl.study.StudyInstance;

public interface StudyService {

	List<StudySummary> search(final StudySearchParameters serchParameters);

	int countTotalObservationUnits(final int studyIdentifier, final int instanceId);

	List<ObservationDto> getObservations(final int studyIdentifier, final int instanceId, final int pageNumber, final int pageSize);

	List<ObservationDto> getSingleObservation(final int studyIdentifier, final int measurementIdentifier);

	ObservationDto updataObservation(final Integer studyIdentifier, final ObservationDto middlewareMeasurement);

	List<StudyGermplasmDto> getStudyGermplasmList(Integer studyIdentifer);

	String getProgramUUID(Integer studyIdentifier);

	List<StudyInstance> getStudyInstances(int studyId);

	StudyDetailDto getStudyDetails(int studyIdentifier);
}
