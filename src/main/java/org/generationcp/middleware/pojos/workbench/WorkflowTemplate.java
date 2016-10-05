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
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * POJO for workbench_workflow_template table.
 * 
 */
@Entity
@Table(name = "workbench_workflow_template")
public class WorkflowTemplate implements Serializable {

	public static final String MANAGER_NAME = "Manager";
	public static final String MARS_NAME = "MARS";
	public static final String MAS_NAME = "MAS";
	public static final String MABC_NAME = "MABC";
	public static final String CB_NAME = "CB";

	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@GeneratedValue
	@Column(name = "template_id")
	private Long templateId;

	@Basic(optional = false)
	@Column(name = "name")
	private String name;

	@Basic(optional = false)
	@Column(name = "user_defined")
	private boolean userDefined;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinTable(name = "workbench_workflow_template_step", joinColumns = {@JoinColumn(name = "template_id")},
			inverseJoinColumns = {@JoinColumn(name = "step_id")})
	@OrderColumn(name = "step_number")
	private List<WorkflowStep> steps;

	public WorkflowTemplate() {
	}

	public WorkflowTemplate(Long templateId) {
		this.templateId = templateId;
	}

	public WorkflowTemplate(String templateIdStr) {
		this.templateId = Long.parseLong(templateIdStr);
	}

	public Long getTemplateId() {
		return this.templateId;
	}

	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isUserDefined() {
		return this.userDefined;
	}

	public void setUserDefined(boolean userDefined) {
		this.userDefined = userDefined;
	}

	public List<WorkflowStep> getSteps() {
		return this.steps;
	}

	public void setSteps(List<WorkflowStep> steps) {
		this.steps = steps;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.templateId).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!WorkflowTemplate.class.isInstance(obj)) {
			return false;
		}

		WorkflowTemplate otherObj = (WorkflowTemplate) obj;

		return new EqualsBuilder().append(this.templateId, otherObj.templateId).isEquals();
	}

	@Override
	public String toString() {

		StringBuilder stepsString = new StringBuilder();
		stepsString.append("[");

		if (this.steps == null) {
			return "";
		}

		for (WorkflowStep step : this.steps) {
			stepsString.append(step + " | ");
		}
		stepsString.append("]");

		StringBuilder builder = new StringBuilder();
		builder.append("WorkflowTemplate [templateId=");
		builder.append(this.templateId);
		builder.append(", name=");
		builder.append(this.name);
		builder.append(", userDefined=");
		builder.append(this.userDefined);
		builder.append(", steps=");
		builder.append(stepsString);
		builder.append("]");
		return builder.toString();
	}

}
