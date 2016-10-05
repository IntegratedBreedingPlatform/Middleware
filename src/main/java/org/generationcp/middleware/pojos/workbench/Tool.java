/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * POJO for workbench_tool table.
 * 
 */
@Entity
@Table(name = "workbench_tool")
public class Tool implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@GeneratedValue
	@Column(name = "tool_id")
	private Long toolId;

	@Basic(optional = false)
	@Column(name = "name")
	private String toolName;

	@Basic(optional = false)
	@Column(name = "group_name")
	private String groupName;

	@Basic(optional = false)
	@Column(name = "title")
	private String title;

	@Basic(optional = false)
	@Column(name = "version")
	private String version;

	@Basic(optional = false)
	@Column(name = "tool_type")
	@Enumerated(value = EnumType.STRING)
	private ToolType toolType;

	@Basic(optional = true)
	@Column(name = "parameter")
	private String parameter;

	@Basic(optional = false)
	@Column(name = "user_tool")
	private Boolean userTool;

	@Basic(optional = false)
	@Column(name = "path")
	private String path;

	public Tool() {
	}

	public Tool(String toolName, String title, String path) {
		this.toolName = toolName;
		this.title = title;
		this.path = path;
	}

	public String getParameter() {
		return this.parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	public Boolean getUserTool() {
		return this.userTool;
	}

	public void setUserTool(Boolean userTool) {
		this.userTool = userTool;
	}

	public Long getToolId() {
		return this.toolId;
	}

	public void setToolId(Long toolId) {
		this.toolId = toolId;
	}

	public String getToolName() {
		return this.toolName;
	}

	public void setToolName(String toolName) {
		this.toolName = toolName;
	}

	public String getGroupName() {
		return this.groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getVersion() {
		return this.version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public ToolType getToolType() {
		return this.toolType;
	}

	public void setToolType(ToolType toolType) {
		this.toolType = toolType;
	}

	public String getPath() {
		return this.path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Tool [toolId=");
		builder.append(this.toolId);
		builder.append(", toolName=");
		builder.append(this.toolName);
		builder.append(", toolType=");
		builder.append(this.toolType);
		builder.append(", path=");
		builder.append(this.path);
		builder.append(", parameter=");
		builder.append(this.parameter);
		builder.append(", userTool=");
		builder.append(this.userTool);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.toolId).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!Tool.class.isInstance(obj)) {
			return false;
		}

		Tool otherObj = (Tool) obj;

		return new EqualsBuilder().append(this.toolId, otherObj.toolId).isEquals();
	}
}
