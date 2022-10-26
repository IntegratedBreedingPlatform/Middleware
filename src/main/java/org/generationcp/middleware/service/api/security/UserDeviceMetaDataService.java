package org.generationcp.middleware.service.api.security;

import java.util.Optional;

public interface UserDeviceMetaDataService {

	Optional<UserDeviceMetaDataDto> findExistingDevice(Integer userId, String deviceDetails, String location);

	UserDeviceMetaDataDto addToExistingDevice(Integer userId, String deviceDetails, String location);

}
