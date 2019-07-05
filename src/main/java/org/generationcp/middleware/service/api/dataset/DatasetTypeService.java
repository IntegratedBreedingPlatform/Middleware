package org.generationcp.middleware.service.api.dataset;

import org.generationcp.middleware.domain.dms.DatasetTypeDTO;

import java.util.List;
import java.util.Map;

public interface DatasetTypeService {

	DatasetTypeDTO getDatasetTypeById(int datasetTypeId);

	Map<Integer, DatasetTypeDTO> getAllDatasetTypesMap();

	List<Integer> getObservationDatasetTypeIds();

	List<Integer> getSubObservationDatasetTypeIds();

	List<String> getObservationLevels(Integer pageSize, Integer pageNumber);

	Long countObservationLevels();
}
