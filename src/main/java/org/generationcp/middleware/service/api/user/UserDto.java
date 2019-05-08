/**
 *
 */

package org.generationcp.middleware.service.api.user;

import java.io.Serializable;
import java.util.List;

import org.generationcp.middleware.domain.workbench.CropDto;
import org.generationcp.middleware.pojos.workbench.Role;

/**
 * @author vmaletta
 *
 */
public class UserDto implements Serializable, Comparable<UserDto> {

	private static final long serialVersionUID = -9173433479366395632L;

	private Integer userId;
	private String username;
	private String firstName;
	private String lastName;
	// TODO n roles
	private Role role;
	private Integer status;
	private String email;
	private String password;
	private List<CropDto> crops;

	public UserDto() {
		this.userId = 0;
		this.firstName = "";
		this.lastName = "";
		this.email = "";

		this.username = "";
		this.status = 0;
	}

	public UserDto(final Integer userId, final String username, final String firstName, final String lastName, final Role role,
		Integer status, String email) {
		this.userId = userId;
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.role = role;
		this.status = status;
		this.email = email;
	}

	public Integer getUserId() {
		return this.userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(final Role role) {
		this.role = role;
	}

	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public int compareTo(UserDto o) {
		int comparId = o.getUserId();
		return Integer.valueOf(this.getUserId()).compareTo(comparId);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.username == null ? 0 : this.username.hashCode());
		result = prime * result + (this.firstName == null ? 0 : this.firstName.hashCode());
		result = prime * result + (this.lastName == null ? 0 : this.lastName.hashCode());
		result = prime * result + (this.role == null ? 0 : this.role.hashCode());
		result = prime * result + (this.email == null ? 0 : this.email.hashCode());

		result = prime * result + (int) (this.status ^ this.status >>> 32);
		result = prime * result + this.userId;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		UserDto other = (UserDto) obj;
		if (this.userId != other.userId) {
			return false;
		}
		return true;
	}

	public List<CropDto> getCrops() {
		return crops;
	}

	public void setCrops(final List<CropDto> crops) {
		this.crops = crops;
	}
}
