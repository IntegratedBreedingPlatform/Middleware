package org.generationcp.middleware.service.api.user;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class RoleSearchDto {

	private Boolean assignable;

	private Integer roleTypeId;

	public RoleSearchDto(final Boolean assignable, final Integer roleTypeId) {
		this.assignable = assignable;
		this.roleTypeId = roleTypeId;
	}

	public Boolean getAssignable() {
		return assignable;
	}

	public void setAssignable(final Boolean assignable) {
		this.assignable = assignable;
	}

	public Integer getRoleTypeId() {
		return roleTypeId;
	}

	public void setRoleTypeId(final Integer roleTypeId) {
		this.roleTypeId = roleTypeId;
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
