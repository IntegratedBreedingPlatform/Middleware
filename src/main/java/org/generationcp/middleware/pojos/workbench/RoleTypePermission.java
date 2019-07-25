package org.generationcp.middleware.pojos.workbench;

import org.hibernate.annotations.Type;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "role_type_permission")
public class RoleTypePermission {

	@EmbeddedId
	private RoleTypePermissionId id;

	@ManyToOne(fetch = FetchType.EAGER)
	@MapsId("permissionId")
	@JoinColumn(name = "permission_id")
	private Permission permission;

	@ManyToOne(fetch = FetchType.EAGER)
	@MapsId("roleTypeId")
	@JoinColumn(name = "role_type_id")
	private RoleType roleType;

	@Type(type = "org.hibernate.type.NumericBooleanType")
	@Basic(optional = false)
	@Column(name = "selectable", columnDefinition = "TINYINT")
	private Boolean selectable;

	public RoleType getRoleType() {
		return this.roleType;
	}

	public void setRoleType(final RoleType roleType) {
		this.roleType = roleType;
	}

	public Boolean getSelectable() {
		return this.selectable;
	}

	public void setSelectable(final Boolean selectable) {
		this.selectable = selectable;
	}

	public Permission getPermission() {
		return permission;
	}

	public void setPermission(final Permission permission) {
		this.permission = permission;
	}

	public RoleTypePermissionId getId() {
		return id;
	}

	public void setId(final RoleTypePermissionId id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;

		if (o == null || getClass() != o.getClass())
			return false;

		RoleTypePermission that = (RoleTypePermission) o;
		return Objects.equals(permission, that.permission) &&
				Objects.equals(roleType, that.roleType);
	}

	@Override
	public int hashCode() {
		return Objects.hash(permission, roleType);
	}
}
