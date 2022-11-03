package org.generationcp.middleware.service.impl.security;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.service.api.security.UserDeviceMetaDataDto;
import org.generationcp.middleware.service.api.security.UserDeviceMetaDataService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class UserDeviceMetaDataServiceImplTest extends IntegrationTestBase {

	@Autowired
	private UserDeviceMetaDataService userDeviceMetaDataService;

	@Test
	public void testAddToExistingDevice() {

		final int userId = 1;
		final String deviceDetails = RandomStringUtils.randomAlphabetic(10);
		final String location = RandomStringUtils.randomAlphabetic(10);

		final UserDeviceMetaDataDto userDeviceMetaDataDto =
			this.userDeviceMetaDataService.addToExistingDevice(userId, deviceDetails, location);

		Assert.assertEquals(userId, userDeviceMetaDataDto.getUserId().intValue());
		Assert.assertEquals(deviceDetails, userDeviceMetaDataDto.getDeviceDetails());
		Assert.assertEquals(location, userDeviceMetaDataDto.getLocation());
		Assert.assertNotNull(userDeviceMetaDataDto.getLastLoggedIn());

	}

	@Test
	public void testFindExistingDevice_DeviceAlreadyExists() {
		final int userId = 1;
		final String deviceDetails = RandomStringUtils.randomAlphabetic(10);
		final String location = RandomStringUtils.randomAlphabetic(10);

		final UserDeviceMetaDataDto userDeviceMetaDataDto =
			this.userDeviceMetaDataService.addToExistingDevice(userId, deviceDetails, location);

		final Optional<UserDeviceMetaDataDto> result = this.userDeviceMetaDataService.findExistingDevice(userId, deviceDetails, location);

		Assert.assertTrue(result.isPresent());

		Assert.assertEquals(userId, result.get().getUserId().intValue());
		Assert.assertEquals(deviceDetails, result.get().getDeviceDetails());
		Assert.assertEquals(location, result.get().getLocation());
		Assert.assertNotNull(result.get().getLastLoggedIn());

	}

	@Test
	public void testFindExistingDevice_DeviceNotExists() {
		final int userId = 1;
		final String deviceDetails = RandomStringUtils.randomAlphabetic(10);
		final String location = RandomStringUtils.randomAlphabetic(10);

		final Optional<UserDeviceMetaDataDto> result = this.userDeviceMetaDataService.findExistingDevice(userId, deviceDetails, location);
		Assert.assertFalse(result.isPresent());

	}

	@Test
	public void testUpdateLastLoggedIn() {
		final int userId = 1;
		final String deviceDetails = RandomStringUtils.randomAlphabetic(10);
		final String location = RandomStringUtils.randomAlphabetic(10);

		final UserDeviceMetaDataDto userDeviceMetaDataDto =
			this.userDeviceMetaDataService.addToExistingDevice(userId, deviceDetails, location);

		this.userDeviceMetaDataService.updateLastLoggedIn(userId, deviceDetails, location);

		final Optional<UserDeviceMetaDataDto> result = this.userDeviceMetaDataService.findExistingDevice(userId, deviceDetails, location);
		Assert.assertTrue(result.isPresent());
		Assert.assertNotSame(result.get().getLastLoggedIn(), userDeviceMetaDataDto.getLastLoggedIn());

	}

}
