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
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * POJO for workbench_project_workflow_step table.
 * 
 */
@Entity
@Table(name = "workbench_project_workflow_step")
public class ProjectWorkflowStep implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@GeneratedValue
	@Column(name = "project_workflow_step_id")
	private Long projectWorkflowStepId;

	@OneToOne(optional = false)
	@JoinColumn(name = "step_id")
	private WorkflowStep step;

	@OneToOne(optional = false)
	@JoinColumn(name = "contact_id")
	private Contact owner;

	@Basic(optional = false)
	@Column(name = "due_date")
	private Date dueDate;

	@Basic(optional = false)
	@Column(name = "status")
	private String status;

	public Long getProjectWorkflowStepId() {
		return this.projectWorkflowStepId;
	}

	public void setProjectWorkflowStepId(Long projectWorkflowStepId) {
		this.projectWorkflowStepId = projectWorkflowStepId;
	}

	public WorkflowStep getStep() {
		return this.step;
	}

	public void setStep(WorkflowStep step) {
		this.step = step;
	}

	public Contact getOwner() {
		return this.owner;
	}

	public void setOwner(Contact owner) {
		this.owner = owner;
	}

	public Date getDueDate() {
		return this.dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.projectWorkflowStepId).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!ProjectWorkflowStep.class.isInstance(obj)) {
			return false;
		}

		ProjectWorkflowStep otherObj = (ProjectWorkflowStep) obj;

		return new EqualsBuilder().append(this.projectWorkflowStepId, otherObj.projectWorkflowStepId).isEquals();
	}

	@Override
	public String toString() {
		return "ProjectWorkflowStep [step=" + this.step + ", owner=" + this.owner + ", dueDate=" + this.dueDate + ", status=" + this.status
				+ "]";
	}

}
