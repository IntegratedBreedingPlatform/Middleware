
package org.generationcp.middleware.service.api.study;

import java.util.List;

import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;

public interface StudyService {

	/**
	 * @param programUniqueId Optional parameter, if provided the results are filtered to only return studies that belong to the program
	 *        identified by this unique id.
	 * @return List of {@link StudySummary}ies. Omits deleted studies.
	 * @throws MiddlewareQueryException
	 */
	List<StudySummary> listAllStudies(final String programUniqueId) throws MiddlewareQueryException;

	List<ObservationDto> getObservations(final int studyIdentifier);

	List<ObservationDto> getSingleObservation(final int studyIdentifier, final int measurementIdentifier);

	ObservationDto updataObservation(final Integer studyIdentifier, final ObservationDto middlewareMeasurement);

	List<StudyGermplasmDto> getStudyGermplasmList(Integer studyIdentifer);
	
	Integer addNewStudy(Workbook workbook, String programUUID)throws MiddlewareQueryException;
}
