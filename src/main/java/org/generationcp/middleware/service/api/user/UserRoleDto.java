package org.generationcp.middleware.service.api.user;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class UserRoleDto {

	private Integer id;

	private RoleDto role;

	private String cropName;

	private Long programId;

	private String programName;

	public UserRoleDto() {
	}

	public UserRoleDto(final Integer id, final RoleDto role, final String cropName, final Long programId, final String programName) {
		this.id = id;
		this.role = role;
		this.cropName = cropName;
		this.programId = programId;
		this.programName = programName;
	}

	public Integer getId() {
		return id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public RoleDto getRole() {
		return role;
	}

	public void setRole(final RoleDto role) {
		this.role = role;
	}

	public String getCropName() {
		return cropName;
	}

	public void setCropName(final String cropName) {
		this.cropName = cropName;
	}

	public String getProgramName() {
		return programName;
	}

	public void setProgramName(final String programName) {
		this.programName = programName;
	}

	public Long getProgramId() {
		return programId;
	}

	public void setProgramId(final Long programId) {
		this.programId = programId;
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
