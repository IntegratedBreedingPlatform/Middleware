package org.generationcp.middleware.api.location;

import java.util.List;

public interface LocationService {

	LocationDTO getLocation(Integer locationId);

	List<LocationTypeDTO> getLocationTypes();
}
