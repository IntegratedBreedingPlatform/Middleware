package org.generationcp.middleware.service.api.dataset;

import org.generationcp.middleware.pojos.dms.DatasetType;

import java.util.Map;

public interface DatasetTypeService {

	DatasetType getDatasetTypeById(int datasetTypeId);

	Map<Integer, DatasetType> getAllDatasetTypes();
}
