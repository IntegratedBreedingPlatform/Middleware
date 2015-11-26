
package org.generationcp.middleware.service.api.study;

import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;

public interface StudyService {

	/**
	 * @param serchParameters :
	 *        <ul>
	 *        <li>programUniqueId - if provided the results are filtered to only return studies that belong to the program identified by
	 *        this unique id.</li>
	 *        </ul>
	 * @return List of {@link StudySummary}ies. Omits deleted studies.
	 * @throws MiddlewareQueryException
	 */
	List<StudySummary> listAllStudies(final StudySearchParameters serchParameters);

	List<ObservationDto> getObservations(final int studyIdentifier);

	List<ObservationDto> getSingleObservation(final int studyIdentifier, final int measurementIdentifier);

	ObservationDto updataObservation(final Integer studyIdentifier, final ObservationDto middlewareMeasurement);

	List<StudyGermplasmDto> getStudyGermplasmList(Integer studyIdentifer);
}
