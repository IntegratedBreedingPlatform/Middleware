package org.generationcp.middleware.service.impl.security;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.security.UserDeviceMetaDataDto;
import org.generationcp.middleware.service.api.security.UserDeviceMetaDataService;
import org.generationcp.middleware.service.api.user.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Optional;

public class UserDeviceMetaDataServiceImplTest extends IntegrationTestBase {

	@Autowired
	private UserDeviceMetaDataService userDeviceMetaDataService;

	@Autowired
	private UserService userService;

	@Autowired
	private WorkbenchTestDataUtil workbenchTestDataUtil;

	@Test
	public void testAddUserDevice() {

		final int userId = 1;
		final String deviceDetails = RandomStringUtils.randomAlphabetic(10);
		final String location = RandomStringUtils.randomAlphabetic(10);

		final UserDeviceMetaDataDto userDeviceMetaDataDto =
			this.userDeviceMetaDataService.addUserDevice(userId, deviceDetails, location);

		Assert.assertEquals(userId, userDeviceMetaDataDto.getUserId().intValue());
		Assert.assertEquals(deviceDetails, userDeviceMetaDataDto.getDeviceDetails());
		Assert.assertEquals(location, userDeviceMetaDataDto.getLocation());
		Assert.assertNotNull(userDeviceMetaDataDto.getLastLoggedIn());

	}

	@Test
	public void testFindUserDevice_DeviceAlreadyExists() {
		final int userId = 1;
		final String deviceDetails = RandomStringUtils.randomAlphabetic(10);
		final String location = RandomStringUtils.randomAlphabetic(10);

		final UserDeviceMetaDataDto userDeviceMetaDataDto =
			this.userDeviceMetaDataService.addUserDevice(userId, deviceDetails, location);

		final Optional<UserDeviceMetaDataDto> result = this.userDeviceMetaDataService.findUserDevice(userId, deviceDetails, location);

		Assert.assertTrue(result.isPresent());

		Assert.assertEquals(userId, result.get().getUserId().intValue());
		Assert.assertEquals(deviceDetails, result.get().getDeviceDetails());
		Assert.assertEquals(location, result.get().getLocation());
		Assert.assertNotNull(result.get().getLastLoggedIn());

	}

	@Test
	public void testFindUserDevice_DeviceNotExists() {
		final int userId = 1;
		final String deviceDetails = RandomStringUtils.randomAlphabetic(10);
		final String location = RandomStringUtils.randomAlphabetic(10);

		final Optional<UserDeviceMetaDataDto> result = this.userDeviceMetaDataService.findUserDevice(userId, deviceDetails, location);
		Assert.assertFalse(result.isPresent());

	}

	@Test
	public void testUpdateUserDeviceLastLoggedIn() {
		final int userId = 1;
		final String deviceDetails = RandomStringUtils.randomAlphabetic(10);
		final String location = RandomStringUtils.randomAlphabetic(10);

		final UserDeviceMetaDataDto userDeviceMetaDataDto =
			this.userDeviceMetaDataService.addUserDevice(userId, deviceDetails, location);

		this.userDeviceMetaDataService.updateUserDeviceLastLoggedIn(userId, deviceDetails, location);

		final Optional<UserDeviceMetaDataDto> result = this.userDeviceMetaDataService.findUserDevice(userId, deviceDetails, location);
		Assert.assertTrue(result.isPresent());
		Assert.assertNotSame(result.get().getLastLoggedIn(), userDeviceMetaDataDto.getLastLoggedIn());

	}

	@Test
	public void testCountUserDevice() {

		final WorkbenchUser testUser = this.createTestUser();

		Assert.assertEquals(0, this.userDeviceMetaDataService.countUserDevices(testUser.getUserid()));

		final UserDeviceMetaDataDto userDeviceMetaDataDto1 =
			this.userDeviceMetaDataService.addUserDevice(testUser.getUserid(), RandomStringUtils.randomAlphabetic(10),
				RandomStringUtils.randomAlphabetic(10));
		final UserDeviceMetaDataDto userDeviceMetaDataDto2 =
			this.userDeviceMetaDataService.addUserDevice(testUser.getUserid(), RandomStringUtils.randomAlphabetic(10),
				RandomStringUtils.randomAlphabetic(10));

		Assert.assertEquals(2, this.userDeviceMetaDataService.countUserDevices(testUser.getUserid()));

	}

	private WorkbenchUser createTestUser() {
		final Person person = this.workbenchTestDataUtil.createTestPersonData();
		this.userService.addPerson(person);
		final WorkbenchUser user = this.workbenchTestDataUtil.createTestUserData();
		user.setStatus(0);
		user.setPerson(person);
		user.setRoles(new ArrayList<>());
		return this.userService.addUser(user);
	}

}
