
package org.generationcp.middleware.api.program;

import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.util.Util;

import java.util.HashSet;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

/**
 * Summary information about breeding program.
 */
public class ProgramDTO {

	//TODO Modify id data type, it may impact site admin
	private String id;
	//TODO rename to programUUID
	private String uniqueID;
	private String name;
	private String createdBy;
	private Set<String> members = new HashSet<>();
	//TODO Rename to cropName
	private String crop;
	private String startDate;

	public ProgramDTO() {
	}

	public ProgramDTO(
		final String id, final String uniqueID, final String name, final String crop) {
		this.id = id;
		this.uniqueID = uniqueID;
		this.name = name;
		this.crop = crop;
	}

	public ProgramDTO(final String crop, final String programUUID) {
		this.crop = crop;
		this.uniqueID = programUUID;
	}

	public ProgramDTO(final Project project) {
		this.setId(String.valueOf(project.getProjectId()));
		this.setCrop(project.getCropType().getCropName());
		this.setName(project.getProjectName());
		this.setUniqueID(project.getUniqueID());
		// TODO get username
		// program.setCreatedBy();
		this.setStartDate(Util.formatDateAsStringValue(project.getStartDate(), Util.FRONTEND_DATE_FORMAT));
		final Set<WorkbenchUser> members = project.getMembers();
		if (members != null && !members.isEmpty()) {
			this.setMembers(members.stream().map(WorkbenchUser::getName).collect(toSet()));
		}
	}

	public String getId() {
		return this.id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public String getUniqueID() {
		return this.uniqueID;
	}

	public void setUniqueID(final String uniqueID) {
		this.uniqueID = uniqueID;
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

	public String getCrop() {
		return this.crop;
	}

	public void setCrop(final String crop) {
		this.crop = crop;
	}

	public String getStartDate() {
		return this.startDate;
	}

	public void setStartDate(final String startDate) {
		this.startDate = startDate;
	}

}
