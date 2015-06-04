/*******************************************************************************
 * Copyright (c) 2014, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.pojos.workbench;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * POJO for the template_setting table in the workbench database.
 *
 * @author Joyce Avestro
 * 
 */
@Entity
@Table(name = "template_setting")
public class TemplateSetting implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@GeneratedValue
	@Column(name = "template_setting_id")
	private Integer templateSettingId;

	@Basic(optional = false)
	@Column(name = "project_id")
	private Integer projectId;

	@Basic(optional = false)
	@Column(name = "name")
	private String name;

	@ManyToOne
	@JoinColumn(name = "tool_id", nullable = false)
	private Tool tool;

	@Basic(optional = false)
	@Column(name = "configuration")
	private String configuration;

	@Basic(optional = true)
	@Column(name = "is_default")
	private Integer isDefault;

	public TemplateSetting() {
		super();
	}

	public TemplateSetting(Integer templateSettingId, Integer projectId, String name, Tool tool, String configuration, Boolean isDefault) {
		this.templateSettingId = templateSettingId;
		this.projectId = projectId;
		this.name = name;
		this.tool = tool;
		this.configuration = configuration;
		this.setIsDefault(isDefault);
	}

	public Integer getTemplateSettingId() {
		return this.templateSettingId;
	}

	public void setTemplateSettingId(Integer templateSettingId) {
		this.templateSettingId = templateSettingId;
	}

	public Integer getProjectId() {
		return this.projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Tool getTool() {
		return this.tool;
	}

	public void setTool(Tool tool) {
		this.tool = tool;
	}

	public String getConfiguration() {
		return this.configuration;
	}

	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}

	public Integer getIsDefault() {
		return this.isDefault;
	}

	public void setIsDefault(Boolean isDefault) {
		if (isDefault == null) {
			this.isDefault = null;
		} else if (isDefault) {
			this.isDefault = 1;
		} else {
			this.isDefault = 0;
		}
	}

	public Boolean isDefault() {
		return this.isDefault > 0 ? true : false;
	}

	// mainly use to set the default to null so we can search all
	public void setIsDefaultToNull() {
		this.isDefault = null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.configuration == null ? 0 : this.configuration.hashCode());
		result = prime * result + (this.isDefault == null ? 0 : this.isDefault.hashCode());
		result = prime * result + (this.name == null ? 0 : this.name.hashCode());
		result = prime * result + (this.projectId == null ? 0 : this.projectId.hashCode());
		result = prime * result + (this.templateSettingId == null ? 0 : this.templateSettingId.hashCode());
		result = prime * result + (this.tool == null ? 0 : this.tool.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		TemplateSetting other = (TemplateSetting) obj;
		if (this.configuration == null) {
			if (other.configuration != null) {
				return false;
			}
		} else if (!this.configuration.equals(other.configuration)) {
			return false;
		}
		if (this.isDefault == null) {
			if (other.isDefault != null) {
				return false;
			}
		} else if (!this.isDefault.equals(other.isDefault)) {
			return false;
		}
		if (this.name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!this.name.equals(other.name)) {
			return false;
		}
		if (this.projectId == null) {
			if (other.projectId != null) {
				return false;
			}
		} else if (!this.projectId.equals(other.projectId)) {
			return false;
		}
		if (this.templateSettingId == null) {
			if (other.templateSettingId != null) {
				return false;
			}
		} else if (!this.templateSettingId.equals(other.templateSettingId)) {
			return false;
		}
		if (this.tool == null) {
			if (other.tool != null) {
				return false;
			}
		} else if (!this.tool.equals(other.tool)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TemplateSetting [templateSettingId=");
		builder.append(this.templateSettingId);
		builder.append(", projectId=");
		builder.append(this.projectId);
		builder.append(", name=");
		builder.append(this.name);
		builder.append(", tool=");
		builder.append(this.tool);
		builder.append(", configuration=");
		builder.append(this.configuration);
		builder.append(", isDefault=");
		builder.append(this.isDefault);
		builder.append("]");
		return builder.toString();
	}

}
