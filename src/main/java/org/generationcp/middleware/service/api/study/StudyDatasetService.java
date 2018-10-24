package org.generationcp.middleware.service.api.study;

import java.util.List;

public interface StudyDatasetService {
	
	long countPhenotypesForDataset(Integer datasetId, List<Integer> traitIds);
	
	boolean datasetExists(Integer studyId, Integer datasetId);

}
