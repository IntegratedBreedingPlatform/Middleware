package org.generationcp.middleware.service.api.dataset;

import java.util.List;

public interface DatasetService {
	
	long countPhenotypes(Integer datasetId, List<Integer> traitIds);
	
	void addTrait(Integer datasetId, Integer traitId, String alias);
	
}
