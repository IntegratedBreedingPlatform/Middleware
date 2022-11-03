package org.generationcp.middleware.service.impl.security;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.WorkbenchDaoFactory;
import org.generationcp.middleware.pojos.workbench.security.UserDeviceMetaData;
import org.generationcp.middleware.service.api.security.UserDeviceMetaDataDto;
import org.generationcp.middleware.service.api.security.UserDeviceMetaDataService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserDeviceMetaDataServiceImpl implements UserDeviceMetaDataService {

	private final WorkbenchDaoFactory workbenchDaoFactory;

	public UserDeviceMetaDataServiceImpl(final HibernateSessionProvider workbenchSessionProvider) {
		this.workbenchDaoFactory = new WorkbenchDaoFactory(workbenchSessionProvider);
	}

	@Override
	public Optional<UserDeviceMetaDataDto> findExistingDevice(final Integer userId, final String deviceDetails, final String location) {

		final List<UserDeviceMetaData> knownDevices =
			this.workbenchDaoFactory.getUserDeviceMetaDataDAO().findByUserIdDeviceAndLocation(userId, deviceDetails, location);

		for (final UserDeviceMetaData existingDevice : knownDevices) {
			if (existingDevice.getDeviceDetails().equals(deviceDetails)
				&& existingDevice.getLocation().equals(location)) {
				final UserDeviceMetaDataDto userDeviceMetaDataDto = new UserDeviceMetaDataDto();
				userDeviceMetaDataDto.setUserId(existingDevice.getUserId());
				userDeviceMetaDataDto.setDeviceDetails(existingDevice.getDeviceDetails());
				userDeviceMetaDataDto.setLocation(existingDevice.getLocation());
				userDeviceMetaDataDto.setLastLoggedIn(existingDevice.getLastLoggedIn());
				return Optional.of(userDeviceMetaDataDto);
			}
		}
		return Optional.empty();
	}

	@Override
	public UserDeviceMetaDataDto addToExistingDevice(final Integer userId, final String deviceDetails, final String location) {

		final UserDeviceMetaData userDeviceMetaData = new UserDeviceMetaData();
		userDeviceMetaData.setUserId(userId);
		userDeviceMetaData.setDeviceDetails(deviceDetails);
		userDeviceMetaData.setLocation(location);
		userDeviceMetaData.setLastLoggedIn(new Date());
		this.workbenchDaoFactory.getUserDeviceMetaDataDAO().save(userDeviceMetaData);

		final UserDeviceMetaDataDto userDeviceMetaDataDto = new UserDeviceMetaDataDto();
		userDeviceMetaDataDto.setUserId(userDeviceMetaData.getUserId());
		userDeviceMetaDataDto.setDeviceDetails(userDeviceMetaData.getDeviceDetails());
		userDeviceMetaDataDto.setLocation(userDeviceMetaData.getLocation());
		userDeviceMetaDataDto.setLastLoggedIn(userDeviceMetaData.getLastLoggedIn());

		return userDeviceMetaDataDto;

	}

	@Override
	public void updateLastLoggedIn(final Integer userId, final String deviceDetails, final String location) {

		final List<UserDeviceMetaData> knownDevices =
			this.workbenchDaoFactory.getUserDeviceMetaDataDAO().findByUserIdDeviceAndLocation(userId, deviceDetails, location);

		if (!CollectionUtils.isEmpty(knownDevices)) {
			final UserDeviceMetaData userDeviceMetaData = knownDevices.get(0);
			// Update last logged in
			userDeviceMetaData.setLastLoggedIn(new Date());
			this.workbenchDaoFactory.getUserDeviceMetaDataDAO().update(userDeviceMetaData);
		}

	}

}
