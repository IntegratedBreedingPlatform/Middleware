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

/**
 * POJO for the workbench workflow activity.
 *
 */
public class WorkFlowActivity implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long activityId;
	private String title;
	private Contact owner;
	private Date date;
	private Date dueDate;
	private Project project;
	private String status;

	public Long getActivityId() {
		return this.activityId;
	}

	public void setActivityId(Long activityId) {
		this.activityId = activityId;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Contact getOwner() {
		return this.owner;
	}

	public void setOwner(Contact owner) {
		this.owner = owner;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getDueDate() {
		return this.dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public Project getProject() {
		return this.project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("WorkFlowActivity [activityId=");
		builder.append(this.activityId);
		builder.append(", title=");
		builder.append(this.title);
		builder.append(", owner=");
		builder.append(this.owner);
		builder.append(", date=");
		builder.append(this.date);
		builder.append(", dueDate=");
		builder.append(this.dueDate);
		builder.append(", project=");
		builder.append(this.project);
		builder.append(", status=");
		builder.append(this.status);
		builder.append("]");
		return builder.toString();
	}

}
