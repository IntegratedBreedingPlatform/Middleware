package org.generationcp.middleware.service.api.user;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Set;

@AutoProperty
public class RoleSearchDto {

	private Set<Integer> roleIds;

	private Boolean assignable;

	private Integer roleTypeId;

	private String name;

	private String description;

	public RoleSearchDto() {
	}

	public RoleSearchDto(final Boolean assignable, final Integer roleTypeId, final Set<Integer> roleIds) {
		this.assignable = assignable;
		this.roleTypeId = roleTypeId;
		this.roleIds = roleIds;
	}

	public Boolean getAssignable() {
		return this.assignable;
	}

	public void setAssignable(final Boolean assignable) {
		this.assignable = assignable;
	}

	public Integer getRoleTypeId() {
		return this.roleTypeId;
	}

	public void setRoleTypeId(final Integer roleTypeId) {
		this.roleTypeId = roleTypeId;
	}

	public Set<Integer> getRoleIds() {
		return this.roleIds;
	}

	public void setRoleIds(final Set<Integer> roleIds) {
		this.roleIds = roleIds;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
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
