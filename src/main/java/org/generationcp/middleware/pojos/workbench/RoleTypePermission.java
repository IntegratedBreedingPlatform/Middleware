package org.generationcp.middleware.pojos.workbench;

import org.hibernate.annotations.Type;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "role_type_permission")
public class RoleTypePermission {

	@Id
	@Column(name = "permission_id", nullable = false)
	private Integer permissionId;

	@ManyToOne
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

	public Integer getPermissionId() {
		return this.permissionId;
	}

	public void setPermissionId(final Integer permissionId) {
		this.permissionId = permissionId;
	}
}
