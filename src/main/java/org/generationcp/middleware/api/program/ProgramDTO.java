package org.generationcp.middleware.api.program;

import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.util.Util;

import java.util.HashSet;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

public class ProgramDTO {

	private Integer id;
	private String programUUID;
	private String name;
	private String createdBy;
	private Set<String> members = new HashSet<>();
	private String cropName;
	private String startDate;

	public ProgramDTO() {
	}

	public ProgramDTO(final Project project) {
		this.setId(Long.valueOf(project.getProjectId()).intValue());
		this.setCropName(project.getCropType().getCropName());
		this.setName(project.getProjectName());
		this.setProgramUUID(project.getUniqueID());
		// TODO get username
		// program.setCreatedBy();
		this.setStartDate(Util.formatDateAsStringValue(project.getStartDate(), Util.FRONTEND_DATE_FORMAT));
		final Set<WorkbenchUser> members = project.getMembers();
		if (members != null && !members.isEmpty()) {
			this.setMembers(members.stream().map(WorkbenchUser::getName).collect(toSet()));
		}
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public String getProgramUUID() {
		return this.programUUID;
	}

	public void setProgramUUID(final String programUUID) {
		this.programUUID = programUUID;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(final String createdBy) {
		this.createdBy = createdBy;
	}

	public Set<String> getMembers() {
		return this.members;
	}

	public void setMembers(final Set<String> members) {
		this.members = members;
	}

	public String getCropName() {
		return this.cropName;
	}

	public void setCropName(final String cropName) {
		this.cropName = cropName;
	}

	public String getStartDate() {
		return this.startDate;
	}

	public void setStartDate(final String startDate) {
		this.startDate = startDate;
	}
}
