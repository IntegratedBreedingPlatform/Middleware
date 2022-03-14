package org.generationcp.middleware.api.study;

import org.generationcp.middleware.service.api.dataset.StockPropertyData;

public interface StudyEntryObservationService {

	Integer createObservation(StockPropertyData stockPropertyData);

	Integer updateObservation(StockPropertyData stockPropertyData);

	void deleteObservation(Integer stockPropertyId);

}
