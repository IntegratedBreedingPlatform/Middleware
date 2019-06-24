package org.generationcp.middleware.service.api.user;

import org.generationcp.middleware.domain.workbench.CropDto;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.UserRole;
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

	public UserRoleDto(final UserRole userRole) {

		final CropType cropType = userRole.getCropType();
		final CropDto cropDto = (cropType != null) ? new CropDto(cropType) : null;

		final Project project = userRole.getWorkbenchProject();
		final Long projectId = (project != null) ? project.getProjectId() : null;
		final String projectName = (project != null) ? project.getProjectName() : null;
		final ProgramDto programDto = (project != null) ? new ProgramDto(projectId, projectName, cropDto) : null;

		this.id = userRole.getId();
		this.role = new RoleDto(userRole);
		this.crop = cropDto;
		this.program = programDto;
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
