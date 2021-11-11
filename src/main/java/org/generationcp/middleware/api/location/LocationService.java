package org.generationcp.middleware.api.location;

import org.generationcp.middleware.api.location.search.LocationSearchRequest;
import org.generationcp.middleware.pojos.Location;
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
	 * @return
	 */
	List<Location> getFilteredLocations(LocationSearchRequest locationSearchRequest, Pageable pageable);

	/**
	 * Returns the count of Location records filtered by LocationSearchRequest parameter.
	 *
	 * @param locationSearchRequest - filter parameters
	 * @return
	 */
	long countFilteredLocations(LocationSearchRequest locationSearchRequest);

	/**
	 * Gets the favorite project location ids.
	 *
	 * @param programUUID - unique id of program
	 * @return the favorite project location ids
	 */
	List<Integer> getFavoriteProjectLocationIds(String programUUID);

	List<org.generationcp.middleware.api.location.Location> getLocations(LocationSearchRequest locationSearchRequest, Pageable pageable);

	void deleteLocation(final Integer locationId);

	Integer createLocation(LocationRequestDto locationRequestDto);

	void updateLocation(Integer locationId, LocationRequestDto locationRequestDto);
}
