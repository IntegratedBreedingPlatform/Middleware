package org.generationcp.middleware.api.location;

import org.generationcp.middleware.api.location.search.LocationSearchRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LocationService {

	LocationDTO getLocation(Integer locationId);

	List<LocationTypeDTO> getLocationTypes();

	/**
	 * Returns the Location records filtered by LocationSearchRequest parameter.
	 *
	 * @param locationSearchRequest - filter parameters
	 * @param pageable              - pagination parameters
	 * @param programUUID
	 * @return
	 */
	List<LocationDTO> searchLocations(LocationSearchRequest locationSearchRequest, Pageable pageable,
			final String programUUID);

	/**
	 * Returns the count of Location records filtered by LocationSearchRequest parameter.
	 *
	 * @param locationSearchRequest - filter parameters
	 * @param programUUID
   * @return
	 */
	long countFilteredLocations(LocationSearchRequest locationSearchRequest, String programUUID);

	List<org.generationcp.middleware.api.location.Location> getLocations(LocationSearchRequest locationSearchRequest, Pageable pageable);

	void deleteLocation(Integer locationId);

	LocationDTO createLocation(LocationRequestDto locationRequestDto);

	void updateLocation(Integer locationId, LocationRequestDto locationRequestDto);

	boolean isDefaultCountryLocation(Integer locationId);

	boolean blockIdIsUsedInFieldMap(List<Integer> blockIds);

	List<LocationDTO> getCountries();
}
