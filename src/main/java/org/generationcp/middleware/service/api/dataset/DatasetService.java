package org.generationcp.middleware.service.api.dataset;

import java.util.List;

public interface DatasetService {
	
	long countPhenotypes(Integer datasetId, List<Integer> traitIds);

	long countPhenotypesByInstance(Integer datasetId, Integer instanceId);
}
