package org.generationcp.middleware.pojos.workbench;

import java.io.Serializable;
import java.util.Date;

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
	return activityId;
    }

    public void setActivityId(Long activityId) {
	this.activityId = activityId;
    }

    public String getTitle() {
	return title;
    }

    public void setTitle(String title) {
	this.title = title;
    }

    public Contact getOwner() {
	return owner;
    }

    public void setOwner(Contact owner) {
	this.owner = owner;
    }

    public Date getDate() {
	return date;
    }

    public void setDate(Date date) {
	this.date = date;
    }

    public Date getDueDate() {
	return dueDate;
    }

    public void setDueDate(Date dueDate) {
	this.dueDate = dueDate;
    }

    public Project getProject() {
	return project;
    }

    public void setProject(Project project) {
	this.project = project;
    }

    public String getStatus() {
	return status;
    }

    public void setStatus(String status) {
	this.status = status;
    }
}
