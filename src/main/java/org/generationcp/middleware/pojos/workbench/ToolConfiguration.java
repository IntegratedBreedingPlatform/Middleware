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
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * POJO for workbench_tool_config table.
 * 
 * @author Michael Blancaflor <br>
 *         <b>File Created</b>: Aug 28, 2012
 */
@Entity
@Table(name = "workbench_tool_config")
public class ToolConfiguration implements Serializable {

	private static final long serialVersionUID = 3835141759438665433L;

	@Id
	@GeneratedValue
	@Basic(optional = false)
	@Column(name = "config_id")
	private Integer configId;

	@OneToOne(optional = false)
	@JoinColumn(name = "tool_id")
	private Tool tool;

	@Basic(optional = false)
	@Column(name = "config_key")
	private String configKey;

	@Basic(optional = false)
	@Column(name = "config_value")
	private String configValue;

	public Integer getConfigId() {
		return this.configId;
	}

	public void setConfigId(Integer configId) {
		this.configId = configId;
	}

	public Tool getTool() {
		return this.tool;
	}

	public void setTool(Tool tool) {
		this.tool = tool;
	}

	public String getConfigKey() {
		return this.configKey;
	}

	public void setConfigKey(String configKey) {
		this.configKey = configKey;
	}

	public String getConfigValue() {
		return this.configValue;
	}

	public void setConfigValue(String configValue) {
		this.configValue = configValue;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ToolConfiguration [configId=");
		builder.append(this.configId);
		builder.append(", tool=");
		builder.append(this.tool);
		builder.append(", configKey=");
		builder.append(this.configKey);
		builder.append(", configValue=");
		builder.append(this.configValue);
		builder.append("]");
		return builder.toString();
	}

}
