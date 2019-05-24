package org.generationcp.middleware.service.api.dataset;

import org.generationcp.middleware.domain.dms.DatasetTypeDTO;

import java.util.Map;

public interface DatasetTypeService {

	DatasetTypeDTO getDatasetTypeById(int datasetTypeId);

	Map<Integer, DatasetTypeDTO> getAllDatasetTypesMap();
}
