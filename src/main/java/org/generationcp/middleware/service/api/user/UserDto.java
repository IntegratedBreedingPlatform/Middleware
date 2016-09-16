/**
 *
 */

package org.generationcp.middleware.service.api.user;

import java.io.Serializable;

/**
 * @author vmaletta
 *
 */
public class UserDto implements Serializable {

	private static final long serialVersionUID = -9173433479366395632L;

	private Integer userId;

	private String username;

	private String firstName;

	private String lastName;

	private String role;

	private Integer status;

	private String email;
	
	private String password;

	public UserDto() {
		this.userId = 0;
		this.firstName = "";
		this.lastName = "";
		this.email = "";

		this.username = "";
		this.status = 0;
		// default
		this.role = "TECHNICIAN";
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

	public String getRole() {
		return this.role;
	}

	public void setRole(String role) {
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

}
