package org.generationcp.middleware.pojos.workbench;

import org.hibernate.annotations.Type;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "role_permission")
public class RolePermission implements Serializable {

	@Id
	@ManyToOne
	@JoinColumn(name = "role_id", nullable = false)
	private Role role;

	@Id
	@ManyToOne
	@JoinColumn(name = "permission_id", nullable = false)
	private Permission permission;

	@Type(type = "org.hibernate.type.NumericBooleanType")
	@Basic(optional = false)
	@Column(name = "selectable", columnDefinition = "TINYINT")
	private Boolean selectable;

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

	public Boolean getSelectable() {
		return selectable;
	}

	public void setSelectable(final Boolean selectable) {
		this.selectable = selectable;
	}
}
