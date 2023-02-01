package org.generationcp.middleware.api.user;

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
}
