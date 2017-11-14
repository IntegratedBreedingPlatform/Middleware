package org.generationcp.middleware.data.initializer;

import org.generationcp.middleware.service.api.user.UserDto;

public class UserDtoTestDataInitializer {
	public static UserDto createUserDto(String firstName, String lastName, String email, String password, String role, String username) {
		UserDto userDto = new UserDto();
		userDto.setFirstName(firstName);
		userDto.setLastName(lastName);
		userDto.setEmail(email);
		userDto.setPassword(password);
		userDto.setRole(role);
		userDto.setUsername(username);
		return userDto;
	}
}
