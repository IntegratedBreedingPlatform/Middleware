/**
 *
 */

package org.generationcp.middleware.service.api.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.generationcp.middleware.domain.workbench.CropDto;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author vmaletta
 *
 */
public class UserDto implements Serializable, Comparable<UserDto> {

	private static final long serialVersionUID = -9173433479366395632L;

	private Integer id;
	private String username;
	private String firstName;
	private String lastName;
	private List<UserRoleDto> userRoles;
	private String status;
	private String email;

	@JsonIgnore
	private String password;

	private Set<CropDto> crops;
	private Set<String> authorities;

	public UserDto() {
		this.id = 0;
		this.firstName = "";
		this.lastName = "";
		this.email = "";

		this.username = "";
		this.status = "true";
	}

	public UserDto(final Integer userId, final String username, final String firstName, final String lastName, final List<UserRoleDto> userRoles,
		final String status, final String email) {
		this.id = userId;
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.userRoles = userRoles;
		this.status = status;
		this.email = email;
	}

	public UserDto(final WorkbenchUser workbenchUser) {
		if (workbenchUser.getRoles() != null && !workbenchUser.getRoles().isEmpty()) {
			this.setUserRoles(UserRoleMapper.map(workbenchUser.getRoles()));
		}
		this.setId(workbenchUser.getUserid());
		if (workbenchUser.getPerson() != null) {
			this.setEmail(workbenchUser.getPerson().getEmail());
			this.setFirstName(workbenchUser.getPerson().getFirstName());
			this.setLastName(workbenchUser.getPerson().getLastName());

			if (workbenchUser.getPerson().getCrops() != null) {
				final Set<CropDto> crops = new HashSet<>();
				for (final CropType cropType : workbenchUser.getPerson().getCrops()) {
					final CropDto crop = new CropDto(cropType);
					crops.add(crop);
				}
				this.setCrops(crops);
			}

		}
		this.setStatus(workbenchUser.getStatus() == 0 ? "true" : "false");
		this.setUsername(workbenchUser.getName());
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(final String status) {
		this.status = status;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(final String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	@Override
	public int compareTo(final UserDto o) {
		final int comparId = o.getId();
		return Integer.valueOf(this.getId()).compareTo(comparId);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.username == null ? 0 : this.username.hashCode());
		result = prime * result + (this.firstName == null ? 0 : this.firstName.hashCode());
		result = prime * result + (this.lastName == null ? 0 : this.lastName.hashCode());
		result = prime * result + (this.userRoles == null ? 0 : this.userRoles.hashCode());

		result = prime * result + (this.email == null ? 0 : this.email.hashCode());
		result = prime * result + (this.status == null ? 0 : this.status.hashCode());

		result = prime * result + this.id;
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final UserDto other = (UserDto) obj;
		return this.id == other.id;
	}

	public Set<CropDto> getCrops() {
		if (this.crops == null) {
			this.crops = new HashSet<>();
		}
		return this.crops;
	}

	public void setCrops(final Set<CropDto> crops) {
		this.crops = crops;
	}

	public Set<String> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(final Set<String> authorities) {
		this.authorities = authorities;
	}

	public List<UserRoleDto> getUserRoles() {
		return userRoles;
	}

	public void setUserRoles(final List<UserRoleDto> userRoles) {
		this.userRoles = userRoles;
	}

}
