
package org.generationcp.middleware.service.api.study;

import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.service.impl.study.StudyInstance;

public interface StudyService {

	List<StudySummary> search(final StudySearchParameters serchParameters);

	public boolean hasMeasurementDataOnEnvironment(final int studyIdentifier, final int instanceId);

	public int countTotalObservationUnits(final int studyIdentifier, final int instanceId);

	List<ObservationDto> getObservations(final int studyIdentifier, final int instanceId, final int pageNumber, final int pageSize,
			final String sortBy, final String sortOrder);

	List<ObservationDto> getSingleObservation(final int studyIdentifier, final int measurementIdentifier);

	ObservationDto updataObservation(final Integer studyIdentifier, final ObservationDto middlewareMeasurement);

	List<StudyGermplasmDto> getStudyGermplasmList(Integer studyIdentifer);

	String getProgramUUID(Integer studyIdentifier);

	List<StudyInstance> getStudyInstances(int studyId);

	TrialObservationTable getTrialObservationTable(int studyIdentifier);

	/**
	 *
	 * @param studyIdentifier id for the study (Nursery / Trial)
	 * @param instanceDbId id for a Trial instance of a Trial (Nursery has 1 instance). If present studyIdentifier will not be used
	 * @return
	 */
	TrialObservationTable getTrialObservationTable(int studyIdentifier, Integer instanceDbId);

	/**
	 *
	 * @param studyId
	 * @return StudyDetailsDto
	 */
	StudyDetailsDto getStudyDetails(Integer studyId) throws MiddlewareQueryException;
}
