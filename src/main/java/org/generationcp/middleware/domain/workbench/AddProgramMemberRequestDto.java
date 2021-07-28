package org.generationcp.middleware.domain.workbench;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Set;

@AutoProperty
public class AddProgramMemberRequestDto {

	private Integer roleId;

	private Set<Integer> userIds;

	public AddProgramMemberRequestDto() {
	}

	public AddProgramMemberRequestDto(final Integer roleId, final Set<Integer> userIds) {
		this.roleId = roleId;
		this.userIds = userIds;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(final Integer roleId) {
		this.roleId = roleId;
	}

	public Set<Integer> getUserIds() {
		return userIds;
	}

	public void setUserIds(final Set<Integer> userIds) {
		this.userIds = userIds;
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
