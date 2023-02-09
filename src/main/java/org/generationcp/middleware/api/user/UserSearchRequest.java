package org.generationcp.middleware.api.user;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class UserSearchRequest {

	public Integer status;

	public Integer roleId;

	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(final Integer status) {
		this.status = status;
	}

	public Integer getRoleId() {
		return this.roleId;
	}

	public void setRoleId(final Integer roleId) {
		this.roleId = roleId;
	}


	@Override
	public boolean equals(final Object o) {
		return Pojomatic.equals(this, o);
	}

	@Override
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}
}
