package org.generationcp.middleware.pojos.presets;

import javax.persistence.*;

/**
 * Created by cyrus on 12/19/14.
 */
@Entity @Table(name = "standard_preset")
public class StandardPreset {
	private int standardPresetId;
	private Integer toolId;
	private String toolSection;
	private String cropName;
	private String name;
	private String configuration;

	@GeneratedValue
	@Id @Column(name = "standard_preset_id")
	public int getStandardPresetId() {
		return standardPresetId;
	}

	public void setStandardPresetId(int standardPresetId) {
		this.standardPresetId = standardPresetId;
	}

	@Basic @Column(name = "tool_id")
	public Integer getToolId() {
		return toolId;
	}

	public void setToolId(Integer toolId) {
		this.toolId = toolId;
	}

	@Basic @Column(name = "tool_section")
	public String getToolSection() {
		return toolSection;
	}

	public void setToolSection(String toolSection) {
		this.toolSection = toolSection;
	}

	@Basic @Column(name = "crop_name")
	public String getCropName() {
		return cropName;
	}

	public void setCropName(String cropName) {
		this.cropName = cropName;
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

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		StandardPreset that = (StandardPreset) o;

		if (standardPresetId != that.standardPresetId) {
			return false;
		}
		if (configuration != null ?
				!configuration.equals(that.configuration) :
				that.configuration != null) {
			return false;
		}
		if (cropName != null ? !cropName.equals(that.cropName) : that.cropName != null) {
			return false;
		}
		if (name != null ? !name.equals(that.name) : that.name != null) {
			return false;
		}
		if (toolId != null ? !toolId.equals(that.toolId) : that.toolId != null) {
			return false;
		}
		if (toolSection != null ? !toolSection.equals(that.toolSection) : that.toolSection != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = standardPresetId;
		result = 31 * result + (toolId != null ? toolId.hashCode() : 0);
		result = 31 * result + (toolSection != null ? toolSection.hashCode() : 0);
		result = 31 * result + (cropName != null ? cropName.hashCode() : 0);
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (configuration != null ? configuration.hashCode() : 0);
		return result;
	}
}
