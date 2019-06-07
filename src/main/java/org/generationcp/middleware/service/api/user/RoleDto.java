package org.generationcp.middleware.service.api.user;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class RoleDto {

	private Integer id;

	private String name;

	private String description;

	private String roleType;

	private Boolean active;

	private  Boolean editable;

	private Boolean assignable;

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

	public String getRoleType() {
		return roleType;
	}

	public void setRoleType(final String roleType) {
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
