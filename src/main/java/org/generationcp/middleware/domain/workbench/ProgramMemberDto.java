package org.generationcp.middleware.domain.workbench;

import org.generationcp.middleware.service.api.user.UserRoleDto;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class ProgramMemberDto {

	private Integer userId;
	private String username;
	private String firstName;
	private String lastName;
	private String email;
	private UserRoleDto roleInProgram;

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(final Integer userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(final String email) {
		this.email = email;
	}

	public UserRoleDto getRoleInProgram() {
		return roleInProgram;
	}

	public void setRoleInProgram(final UserRoleDto roleInProgram) {
		this.roleInProgram = roleInProgram;
	}

	@Override
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}

	@Override
	public boolean equals(final Object o) {
		return Pojomatic.equals(this, o);
	}

}
