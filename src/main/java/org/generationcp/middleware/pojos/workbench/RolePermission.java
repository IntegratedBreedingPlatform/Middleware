package org.generationcp.middleware.pojos.workbench;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "role_permission")
public class RolePermission implements Serializable {

	@Id
	@Column(name = "role_id", nullable = false)
	private Role role;

	@Id
	@Column(name = "permission_id", nullable = false)
	private Permission permission;

	public Role getRole() {
		return this.role;
	}

	public void setRole(final Role role) {
		this.role = role;
	}

	public Permission getPermission() {
		return this.permission;
	}

	public void setPermission(final Permission permission) {
		this.permission = permission;
	}
}
