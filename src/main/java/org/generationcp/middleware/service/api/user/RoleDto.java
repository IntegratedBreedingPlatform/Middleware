package org.generationcp.middleware.service.api.user;

import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.UserRole;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class RoleDto {

	private Integer id;

	private String name;

	private String description;

	private String type;

	private Boolean active;

	private  Boolean editable;

	private Boolean assignable;

	public RoleDto() {
	}

	public RoleDto(final Integer id, final String name, final String description, final String type, final Boolean active,
		final Boolean editable, final Boolean assignable) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.type = type;
		this.active = active;
		this.editable = editable;
		this.assignable = assignable;
	}

	public RoleDto(final UserRole userRole) {
		final Role role = userRole.getRole();
		this.id = role.getId();
		this.name = role.getName();
		this.description = role.getDescription();
		this.type = role.getRoleType().getName();
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

	public String getType() {
		return type;
	}

	public void setType(final String type) {
		this.type = type;
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
