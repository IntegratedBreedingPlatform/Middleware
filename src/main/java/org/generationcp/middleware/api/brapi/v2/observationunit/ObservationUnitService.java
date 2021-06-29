package org.generationcp.middleware.api.brapi.v2.observationunit;
import org.generationcp.middleware.service.api.phenotype.ObservationUnitDto;
import org.generationcp.middleware.service.api.phenotype.ObservationUnitSearchRequestDTO;

import java.util.List;

public interface ObservationUnitService {

	void update(String observationUnitDbId, ObservationUnitPatchRequestDTO requestDTO);

	/**
	 * Retrieves Observation units given certain search parameters
	 * specified in https://brapi.docs.apiary.io/#reference/phenotypes/phenotype-search V1.1
	 *
	 * @param pageSize
	 * @param pageNumber
	 * @param requestDTO
	 * @return List of observation units
	 */
	List<ObservationUnitDto> searchObservationUnits(Integer pageSize, Integer pageNumber, ObservationUnitSearchRequestDTO requestDTO);

	/**
	 * Retrieves a count of how many observation units match with the search parameters
	 *
	 * @param requestDTO
	 * @return Number of observation units
	 */
	long countObservationUnits(ObservationUnitSearchRequestDTO requestDTO);

	List<ObservationUnitDto> importObservationUnits(String crop, List<ObservationUnitImportRequestDto> observationUnitImportRequestDtos, Integer userId);

}
