package org.generationcp.middleware.service.api.user;

import org.generationcp.middleware.domain.workbench.CropDto;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class UserRoleDto {

	private Integer id;

	private RoleDto role;

	private CropDto crop;

	private ProgramDto program;

	public UserRoleDto() {
	}

	public UserRoleDto(final Integer id, final RoleDto role, final CropDto crop, final ProgramDto program) {
		this.id = id;
		this.role = role;
		this.crop = crop;
		this.program = program;
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

	public CropDto getCrop() {
		return crop;
	}

	public void setCrop(final CropDto crop) {
		this.crop = crop;
	}

	public ProgramDto getProgram() {
		return program;
	}

	public void setProgram(final ProgramDto program) {
		this.program = program;
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
