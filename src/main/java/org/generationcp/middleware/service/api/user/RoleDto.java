package org.generationcp.middleware.service.api.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.generationcp.middleware.domain.workbench.PermissionDto;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.UserRole;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AutoProperty
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoleDto {

	private Integer id;

	private String name;

	private String description;

	private RoleTypeDto roleType;

	private List<PermissionDto> permissions;

	private List<UserRoleDto> userRoles = new ArrayList<>();

	private Boolean active;

	private  Boolean editable;

	private Boolean assignable;

	public RoleDto() {
	}

	public RoleDto(final Role role) {
		super();
		this.id = role.getId();
		this.name = role.getName();
		this.description = role.getDescription();
		this.roleType = new RoleTypeDto(role.getRoleType());
		this.permissions = role.getPermissions().stream()
			.map(permission -> new PermissionDto(permission))
			.collect(Collectors.toList());
		this.active = role.getActive();
		this.editable = role.getEditable();
		this.assignable = role.getAssignable();
	}

	public RoleDto(final Integer id, final String name, final String description, final RoleTypeDto type, final Boolean active,
		final Boolean editable, final Boolean assignable) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.roleType = type;
		this.active = active;
		this.editable = editable;
		this.assignable = assignable;
	}

	public RoleDto(final UserRole userRole) {
		final Role role = userRole.getRole();
		this.id = role.getId();
		this.name = role.getName();
		this.description = role.getDescription();
		this.roleType = new RoleTypeDto(role.getRoleType());
		this.active = role.getActive();
		this.editable = role.getEditable();
		this.assignable = role.getAssignable();
	}

	public Integer getId() {
		return id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public RoleTypeDto getRoleType() {
		return roleType;
	}

	public void setRoleType(final RoleTypeDto roleType) {
		this.roleType = roleType;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(final Boolean active) {
		this.active = active;
	}

	public Boolean getEditable() {
		return editable;
	}

	public void setEditable(final Boolean editable) {
		this.editable = editable;
	}

	public Boolean getAssignable() {
		return assignable;
	}

	public void setAssignable(final Boolean assignable) {
		this.assignable = assignable;
	}

	public List<PermissionDto> getPermissions() {
		return permissions;
	}

	public void setPermissions(final List<PermissionDto> permissions) {
		this.permissions = permissions;
	}

	public List<UserRoleDto> getUserRoles() {
		return userRoles;
	}

	public void setUserRoles(final List<UserRoleDto> userRoles) {
		this.userRoles = userRoles;
	}

	@Override
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}

	@Override
	public boolean equals(final Object o) {
		return Pojomatic.equals(this, o);
	}

}
