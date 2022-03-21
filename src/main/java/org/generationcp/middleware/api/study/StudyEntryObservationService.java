package org.generationcp.middleware.api.study;

import org.generationcp.middleware.service.api.dataset.StockPropertyData;

import java.util.List;

public interface StudyEntryObservationService {

	Integer createObservation(StockPropertyData stockPropertyData);

	Integer updateObservation(StockPropertyData stockPropertyData);

	void deleteObservation(Integer stockPropertyId);

	long countObservationsByStudyAndVariables(final Integer studyId, final List<Integer> variableIds);

}
