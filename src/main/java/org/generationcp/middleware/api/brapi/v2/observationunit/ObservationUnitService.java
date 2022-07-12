package org.generationcp.middleware.api.brapi.v2.observationunit;

import org.generationcp.middleware.service.api.phenotype.ObservationUnitDto;
import org.generationcp.middleware.service.api.phenotype.ObservationUnitSearchRequestDTO;
import org.generationcp.middleware.service.api.study.ObservationLevel;
import org.generationcp.middleware.service.api.study.ObservationLevelFilter;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Set;

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

	List<String> importObservationUnits(String crop, List<ObservationUnitImportRequestDto> observationUnitImportRequestDtos);

	Map<String, List<String>> getPlotObservationLevelRelationshipsByGeolocations(Set<String> geolocationIds);

	long countObservationLevels(ObservationLevelFilter observationLevelFilter);

	List<ObservationLevel> getObservationLevels(ObservationLevelFilter observationLevelFilter, Pageable pageable);

}
