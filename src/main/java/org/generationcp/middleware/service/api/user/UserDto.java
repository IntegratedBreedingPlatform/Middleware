/**
 *
 */

package org.generationcp.middleware.service.api.user;

/**
 * @author vmaletta
 *
 */
public class UserDto {

	private Integer userId;

	private String username;

	private String firstName;

	private String lastName;

	private String role;

	private Integer status;
	
	private String email;

	public UserDto() {
		super();
	}

	public  Integer getUserId() {
		return this.userId;
	}

	public  void setUserId( Integer userId) {
		this.userId = userId;
	}

	public  String getUsername() {
		return this.username;
	}

	public  void setUsername( String username) {
		this.username = username;
	}

	public  String getFirstName() {
		return this.firstName;
	}

	public  void setFirstName( String firstName) {
		this.firstName = firstName;
	}

	public  String getLastName() {
		return this.lastName;
	}

	public  void setLastName( String lastName) {
		this.lastName = lastName;
	}

	public  String getRole() {
		return this.role;
	}

	public  void setRole( String role) {
		this.role = role;
	}

	public  Integer getStatus() {
		return this.status;
	}

	public  void setStatus( Integer status) {
		this.status = status;
	}

	
	public String getEmail() {
		return email;
	}

	
	public void setEmail(String email) {
		this.email = email;
	}

}
