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
import org.generationcp.middleware.pojos.User;

/**
 * The Class ProjectActivity.
 * 
 *  @author Joyce Avestro
 *  
 */
@Entity
@Table(name = "workbench_project_activity")
public class ProjectActivity implements Serializable{

    private static final long serialVersionUID = 1L;
    
    /** The Constant GET_ACTIVITIES_BY_PROJECT_ID. Used by ProjectActivityDAO.getByProjectId() */
    public static final String GET_ACTIVITIES_BY_PROJECT_ID = 
            "SELECT wpa.* " + 
            "FROM workbench_project_activity wpa " +
            "WHERE project_id = :projectId " +
            "ORDER BY date";
    
    /** The Constant COUNT_ACTIVITIES_BY_PROJECT_ID. Used by ProjectActivityDAO.countByProjectId() */
    public static final String COUNT_ACTIVITIES_BY_PROJECT_ID = 
            "SELECT COUNT(*) " + 
            "FROM workbench_project_activity " +
            "WHERE project_id = :projectId";
    
    /** The db-generated project activity id */
    @Id
    @Basic(optional = false)
    @GeneratedValue
    @Column(name = "project_activity_id")
    private Integer projectActivityId;

    /** The project this activity is associated with. */
    @OneToOne(optional = false)
    @JoinColumn(name = "project_id")
    private Project project;

    /** The name of this project activity. */
    @Column(name = "name")
    private String name;

    /** The descripiton of this project activity. */
    @Column(name = "description")
    private String description;

    /** The user of this project activity. */
    @OneToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;
    
    /** The date of this project activity. */
    @Column(name = "date")
    private Date date;
    
    public ProjectActivity() {
    }

    public ProjectActivity(Integer projectActivityId, Project project, String name, String description, User user, Date date) {
        super();
        this.projectActivityId = projectActivityId;
        this.project = project;
        this.name = name;
        this.description = description;
        this.user = user;
        this.date = date;
    }
    
    public Integer getProjectActivityId() {
        return projectActivityId;
    }
    
    public void setProjectActivityId(Integer projectActivityId) {
        this.projectActivityId = projectActivityId;
    }
    
    public Project getProject() {
        return project;
    }
    
    public void setProject(Project project) {
        this.project = project;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(projectActivityId).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!ProjectActivity.class.isInstance(obj)) {
            return false;
        }

        ProjectActivity otherObj = (ProjectActivity) obj;

        return new EqualsBuilder().append(projectActivityId, otherObj.projectActivityId).isEquals();
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ProjectActivity [projectActivityId=");
        builder.append(projectActivityId);
        builder.append(", project=");
        builder.append(project);
        builder.append(", name=");
        builder.append(name);
        builder.append(", description=");
        builder.append(description);
        builder.append(", user=");
        builder.append(user);
        builder.append(", date=");
        builder.append(date);
        builder.append("]");
        return builder.toString();
    }
    
    


}
