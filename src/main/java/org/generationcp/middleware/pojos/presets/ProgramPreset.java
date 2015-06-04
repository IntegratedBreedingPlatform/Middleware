
package org.generationcp.middleware.pojos.presets;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

/**
 * Created by cyrus on 12/19/14.
 */
@Entity
@Table(name = "program_preset")
public class ProgramPreset {

	private int programPresetId;
	private String programUuid;
	private Integer toolId;
	private String toolSection;
	private String name;
	private String configuration;
	private Boolean isDefault;

	@GeneratedValue
	@Id
	@Column(name = "program_preset_id")
	public int getProgramPresetId() {
		return this.programPresetId;
	}

	public void setProgramPresetId(int programPresetsId) {
		this.programPresetId = programPresetsId;
	}

	@Basic
	@Column(name = "program_uuid")
	public String getProgramUuid() {
		return this.programUuid;
	}

	public void setProgramUuid(String programUuid) {
		this.programUuid = programUuid;
	}

	@Basic
	@Column(name = "tool_id")
	public Integer getToolId() {
		return this.toolId;
	}

	public void setToolId(Integer toolId) {
		this.toolId = toolId;
	}

	@Basic
	@Column(name = "tool_section")
	public String getToolSection() {
		return this.toolSection;
	}

	public void setToolSection(String toolSection) {
		this.toolSection = toolSection;
	}

	@Basic
	@Column(name = "name")
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Basic
	@Column(name = "configuration")
	public String getConfiguration() {
		return this.configuration;
	}

	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}

	@Type(type = "org.hibernate.type.NumericBooleanType")
	@Basic
	@Column(name = "is_default", columnDefinition = "TINYINT")
	public Boolean getIsDefault() {
		return this.isDefault;
	}

	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || this.getClass() != o.getClass()) {
			return false;
		}

		ProgramPreset that = (ProgramPreset) o;

		if (this.programPresetId != that.programPresetId) {
			return false;
		}
		if (this.configuration != null ? !this.configuration.equals(that.configuration) : that.configuration != null) {
			return false;
		}
		if (this.isDefault != null ? !this.isDefault.equals(that.isDefault) : that.isDefault != null) {
			return false;
		}
		if (this.name != null ? !this.name.equals(that.name) : that.name != null) {
			return false;
		}
		if (this.programUuid != null ? !this.programUuid.equals(that.programUuid) : that.programUuid != null) {
			return false;
		}
		if (this.toolId != null ? !this.toolId.equals(that.toolId) : that.toolId != null) {
			return false;
		}
		if (this.toolSection != null ? !this.toolSection.equals(that.toolSection) : that.toolSection != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = this.programPresetId;
		result = 31 * result + (this.programUuid != null ? this.programUuid.hashCode() : 0);
		result = 31 * result + (this.toolId != null ? this.toolId.hashCode() : 0);
		result = 31 * result + (this.toolSection != null ? this.toolSection.hashCode() : 0);
		result = 31 * result + (this.name != null ? this.name.hashCode() : 0);
		result = 31 * result + (this.configuration != null ? this.configuration.hashCode() : 0);
		result = 31 * result + (this.isDefault != null ? this.isDefault.hashCode() : 0);
		return result;
	}
}
