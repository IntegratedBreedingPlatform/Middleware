
package org.generationcp.middleware.service.api.study;

import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;

public interface StudyService {

	List<StudySummary> listAllStudies() throws MiddlewareQueryException;

	List<Measurement> getMeasurements(final int studyIdentifier);
}
