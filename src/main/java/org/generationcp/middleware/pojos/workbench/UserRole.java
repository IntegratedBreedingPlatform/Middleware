
package org.generationcp.middleware.pojos.workbench;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "roles")
public class UserRole {

	@Id
	@Basic(optional = false)
	@Column(name = "roleid")
	private Integer roleid;

	@Column(name = "role")
	private String role;

	@Column(name = "description")
	private String description;

	public UserRole() {
	}

	public UserRole(Integer roleid, String role, String description) {
		this.roleid = roleid;
		this.role = role;
		this.description = description;
	}

	public Integer getRoleid() {
		return roleid;
	}

	public void setRoleid(Integer roleid) {
		this.roleid = roleid;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "UserRole [roleid=" + roleid + ", role=" + role + ", description=" + description + "]";
	}
	
}
