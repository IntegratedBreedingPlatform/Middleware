package org.generationcp.middleware.data.initializer;

import org.generationcp.middleware.service.api.user.UserDto;
import org.generationcp.middleware.service.api.user.UserRoleDto;

import java.util.List;

public class UserDtoTestDataInitializer {

	public static UserDto createUserDto(String firstName, String lastName, String email, String password, List<UserRoleDto> userRoles,
		String username) {
		UserDto userDto = new UserDto();
		userDto.setFirstName(firstName);
		userDto.setLastName(lastName);
		userDto.setEmail(email);
		userDto.setPassword(password);
		userDto.setUserRoles(userRoles);
		userDto.setUsername(username);
		return userDto;
	}
}
