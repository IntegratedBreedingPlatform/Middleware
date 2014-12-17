package org.generationcp.middleware.pojos.presets;

import org.hibernate.annotations.Type;

import javax.persistence.*;

/**
 * Created by cyrus on 12/17/14.
 */
@Entity @Table(name = "program_preset")
public class ProgramPreset {
	private int programPresetsId;
	private Integer programUuid;
	private Integer toolId;
	private String name;
	private String configuration;
	private Boolean isDefault;

	@GeneratedValue
	@Id @Column(name = "program_presets_id")
	public int getProgramPresetsId() {
		return programPresetsId;
	}

	public void setProgramPresetsId(int programPresetsId) {
		this.programPresetsId = programPresetsId;
	}

	@Basic @Column(name = "program_uuid")
	public Integer getProgramUuid() {
		return programUuid;
	}

	public void setProgramUuid(Integer programUuid) {
		this.programUuid = programUuid;
	}

	@Basic @Column(name = "tool_id")
	public Integer getToolId() {
		return toolId;
	}

	public void setToolId(Integer toolId) {
		this.toolId = toolId;
	}

	@Basic @Column(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Basic @Column(name = "configuration")
	public String getConfiguration() {
		return configuration;
	}

	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}

	@Type(type = "org.hibernate.type.NumericBooleanType")
	@Basic @Column(name = "is_default", columnDefinition = "TINYINT")
	public Boolean getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		ProgramPreset that = (ProgramPreset) o;

		if (programPresetsId != that.programPresetsId)
			return false;
		if (configuration != null ?
				!configuration.equals(that.configuration) :
				that.configuration != null)
			return false;
		if (isDefault != null ? !isDefault.equals(that.isDefault) : that.isDefault != null)
			return false;
		if (name != null ? !name.equals(that.name) : that.name != null)
			return false;
		if (programUuid != null ? !programUuid.equals(that.programUuid) : that.programUuid != null)
			return false;
		if (toolId != null ? !toolId.equals(that.toolId) : that.toolId != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = programPresetsId;
		result = 31 * result + (programUuid != null ? programUuid.hashCode() : 0);
		result = 31 * result + (toolId != null ? toolId.hashCode() : 0);
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (configuration != null ? configuration.hashCode() : 0);
		result = 31 * result + (isDefault != null ? isDefault.hashCode() : 0);
		return result;
	}
}
