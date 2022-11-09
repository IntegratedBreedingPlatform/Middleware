package org.generationcp.middleware.service.api.security;

import java.util.Optional;

public interface UserDeviceMetaDataService {

	Optional<UserDeviceMetaDataDto> findUserDevice(Integer userId, String deviceDetails, String location);

	UserDeviceMetaDataDto addUserDevice(Integer userId, String deviceDetails, String location);

	void updateUserDeviceLastLoggedIn(Integer userId, String deviceDetails, String location);

	long countUserDevices(Integer userId);

}
