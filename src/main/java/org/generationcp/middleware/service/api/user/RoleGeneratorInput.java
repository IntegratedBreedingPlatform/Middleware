package org.generationcp.middleware.service.api.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.List;

@AutoProperty
public class RoleGeneratorInput {

	private int id;

	private String name;

	private String description;

	private Integer roleType;

	private List<Integer> permissions;

	private Boolean editable;

	private Boolean assignable;

	private boolean showWarnings;

	@JsonIgnore
	private String username;

	public int getId() {
		return this.id;
	}

	public void setId(final int id) {
		this.id = id;
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

	public Integer getRoleType() {
		return this.roleType;
	}

	public void setRoleType(final Integer roleType) {
		this.roleType = roleType;
	}

	public List<Integer> getPermissions() {
		return this.permissions;
	}

	public void setPermissions(final List<Integer> permissions) {
		this.permissions = permissions;
	}

	public Boolean isEditable() {
		return this.editable;
	}

	public void setEditable(final Boolean editable) {
		this.editable = editable;
	}

	public Boolean isAssignable() {
		return this.assignable;
	}

	public void setAssignable(final Boolean assignable) {
		this.assignable = assignable;
	}

	public boolean isShowWarnings() {
		return showWarnings;
	}

	public void setShowWarnings(final boolean showWarnings) {
		this.showWarnings = showWarnings;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(final String username) {
		this.username = username;
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
	public boolean equals(Object o) {
		return Pojomatic.equals(this, o);
	}
}
