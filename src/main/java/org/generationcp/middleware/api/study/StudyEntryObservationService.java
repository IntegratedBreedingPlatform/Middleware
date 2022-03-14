package org.generationcp.middleware.api.study;

import org.generationcp.middleware.service.api.dataset.StockPropertyData;

public interface StudyEntryObservationService {

	Integer createObservation(Integer studyId, StockPropertyData stockPropertyData);

}
