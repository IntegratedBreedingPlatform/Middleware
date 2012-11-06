/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/

package org.generationcp.middleware.pojos.workbench;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.User;

@Entity
@Table(name = "workbench_project")
public class Project implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @GeneratedValue
    @Column(name = "project_id")
    private Long projectId;

    @Basic(optional = false)
    @Column(name = "project_name")
    private String projectName = "";

    @Basic(optional = false)
    @Column(name = "start_date")
    private Date startDate;

    @Basic(optional = false)
    @Column(name = "user_id")
    private int userId;

    @OneToOne
    @JoinColumn(name = "template_id")
    private WorkflowTemplate template;

    @OneToOne
    @JoinColumn(name = "crop_type", referencedColumnName = "crop_name")
    private CropType cropType;

    @Column(name = "local_db_name")
    private String localDbName;

    @Column(name = "central_db_name")
    private String centralDbName;

    @Basic(optional = false)
    @Column(name = "template_modified")
    private boolean templateModified;

    @Basic(optional = true)
    @Column(name = "last_open_date")
    private Date lastOpenDate;

    @Transient
    private List<ProjectWorkflowStep> steps;

    @Transient
    private Set<User> members;

    @Transient
    private Set<Method> methods;

    @Transient
    private Set<Location> locations;

    // TODO: remove these fields
    @Transient
    private String action;
    @Transient
    private String status;
    @Transient
    private Contact owner;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public WorkflowTemplate getTemplate() {
        return template;
    }

    public void setTemplate(WorkflowTemplate template) {
        this.template = template;
    }

    public CropType getCropType() {
        return cropType;
    }

    public void setCropType(CropType cropType) {
        this.cropType = cropType;
    }

    public String getLocalDbName() {
        return localDbName;
    }

    public void setLocalDbName(String localDbName) {
        this.localDbName = localDbName;
    }

    public String getCentralDbName() {
        return centralDbName;
    }

    public void setCentralDbName(String centralDbName) {
        this.centralDbName = centralDbName;
    }

    public boolean isTemplateModified() {
        return templateModified;
    }

    public void setTemplateModified(boolean templateModified) {
        this.templateModified = templateModified;
    }

    public List<ProjectWorkflowStep> getSteps() {
        return steps;
    }

    public void setSteps(List<ProjectWorkflowStep> steps) {
        this.steps = steps;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Contact getOwner() {
        return owner;
    }

    public void setOwner(Contact owner) {
        this.owner = owner;
    }

    public void setLastOpenDate(Date lastOpenDate) {
        this.lastOpenDate = lastOpenDate;
    }

    public Date getLastOpenDate() {
        return lastOpenDate;
    }

    public Set<User> getMembers() {
        return members;
    }

    public void setMembers(Set<User> members) {
        this.members = members;
    }

    public Set<Method> getMethods() {
        return methods;
    }

    public void setMethods(Set<Method> methods) {
        this.methods = methods;
    }

    public Set<Location> getLocations() {
        return locations;
    }

    public void setLocations(Set<Location> locations) {
        this.locations = locations;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(projectId).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!Project.class.isInstance(obj)) {
            return false;
        }

        Project otherObj = (Project) obj;

        return new EqualsBuilder().append(projectId, otherObj.projectId).isEquals();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Project [projectId=");
        builder.append(projectId);
        builder.append(", projectName=");
        builder.append(projectName);
        builder.append(", startDate=");
        builder.append(startDate);
        builder.append(", userId=");
        builder.append(userId);
        builder.append(", template=");
        builder.append(template);
        builder.append(", cropType=");
        builder.append(cropType);
        builder.append(", localDbName=");
        builder.append(localDbName);
        builder.append(", centralDbName=");
        builder.append(centralDbName);
        builder.append(", templateModified=");
        builder.append(templateModified);
        builder.append(", lastOpenDate=");
        builder.append(lastOpenDate);
        builder.append(", steps=");
        builder.append(steps);
        builder.append(", members=");
        builder.append(members);
        builder.append(", methods=");
        builder.append(methods);
        builder.append(", locations=");
        builder.append(locations);
        builder.append(", action=");
        builder.append(action);
        builder.append(", status=");
        builder.append(status);
        builder.append(", owner=");
        builder.append(owner);
        builder.append("]");
        return builder.toString();
    }
    
    

}