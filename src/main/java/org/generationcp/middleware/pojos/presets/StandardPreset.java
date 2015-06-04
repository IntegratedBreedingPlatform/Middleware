
package org.generationcp.middleware.pojos.presets;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by cyrus on 12/19/14.
 */
@Entity
@Table(name = "standard_preset")
public class StandardPreset {

	private int standardPresetId;
	private Integer toolId;
	private String toolSection;
	private String cropName;
	private String name;
	private String configuration;

	@GeneratedValue
	@Id
	@Column(name = "standard_preset_id")
	public int getStandardPresetId() {
		return this.standardPresetId;
	}

	public void setStandardPresetId(int standardPresetId) {
		this.standardPresetId = standardPresetId;
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
	@Column(name = "crop_name")
	public String getCropName() {
		return this.cropName;
	}

	public void setCropName(String cropName) {
		this.cropName = cropName;
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

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || this.getClass() != o.getClass()) {
			return false;
		}

		StandardPreset that = (StandardPreset) o;

		if (this.standardPresetId != that.standardPresetId) {
			return false;
		}
		if (this.configuration != null ? !this.configuration.equals(that.configuration) : that.configuration != null) {
			return false;
		}
		if (this.cropName != null ? !this.cropName.equals(that.cropName) : that.cropName != null) {
			return false;
		}
		if (this.name != null ? !this.name.equals(that.name) : that.name != null) {
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
		int result = this.standardPresetId;
		result = 31 * result + (this.toolId != null ? this.toolId.hashCode() : 0);
		result = 31 * result + (this.toolSection != null ? this.toolSection.hashCode() : 0);
		result = 31 * result + (this.cropName != null ? this.cropName.hashCode() : 0);
		result = 31 * result + (this.name != null ? this.name.hashCode() : 0);
		result = 31 * result + (this.configuration != null ? this.configuration.hashCode() : 0);
		return result;
	}
}
