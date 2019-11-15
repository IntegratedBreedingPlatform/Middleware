package org.generationcp.middleware.pojos.workbench;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Created by clarysabel on 7/25/19.
 */
@Embeddable
public class RoleTypePermissionId implements Serializable {

	@Column(name = "role_type_id")
	private Long roleTypeId;

	@Column(name = "permission_id")
	private Integer permissionId;

	public RoleTypePermissionId() {
	}

	public Long getRoleTypeId() {
		return roleTypeId;
	}

	public void setRoleTypeId(final Long roleTypeId) {
		this.roleTypeId = roleTypeId;
	}

	public Integer getPermissionId() {
		return permissionId;
	}

	public void setPermissionId(final Integer permissionId) {
		this.permissionId = permissionId;
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;

		if (o == null || getClass() != o.getClass())
			return false;

		RoleTypePermissionId that = (RoleTypePermissionId) o;
		return Objects.equals(roleTypeId, that.roleTypeId) &&
				Objects.equals(permissionId, that.permissionId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(roleTypeId, permissionId);
	}

}
