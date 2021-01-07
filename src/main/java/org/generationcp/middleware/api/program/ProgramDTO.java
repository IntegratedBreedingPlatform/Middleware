package org.generationcp.middleware.api.program;

import java.util.HashSet;
import java.util.Set;

public class ProgramDTO {

	private Integer id;
	private String programUUID;
	private String name;
	private String createdBy;
	private Set<String> members = new HashSet<>();
	private String cropName;
	private String startDate;

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
