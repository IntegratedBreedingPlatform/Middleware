package org.generationcp.middleware.api.user;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.List;

@AutoProperty
public class UserSearchRequest {

	public Integer status;

	public String userName;

	public String firstName;

	public String lastName;

	public String fullName;

	public String email;

	public List<String> crops;

	public List<Integer> roleIds;

	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(final Integer status) {
		this.status = status;
	}

	public List<Integer> getRoleIds() {
		return this.roleIds;
	}

	public void setRoleIds(final List<Integer> roleIds) {
		this.roleIds = roleIds;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<String> getCrops() {
		return crops;
	}

	public void setCrops(List<String> crops) {
		this.crops = crops;
	}

	@Override
	public boolean equals(final Object o) {
		return Pojomatic.equals(this, o);
	}

	@Override
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}
}
