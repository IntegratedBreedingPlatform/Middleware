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
	private Integer roleId;

	@Id
	@Column(name = "permission_id", nullable = false)
	private Integer permissionId;

	public Integer getRoleId() {
		return this.roleId;
	}

	public void setRoleId(final Integer roleId) {
		this.roleId = roleId;
	}

	public Integer getPermissionId() {
		return this.permissionId;
	}

	public void setPermissionId(final Integer permissionId) {
		this.permissionId = permissionId;
	}
}
