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
import org.generationcp.middleware.pojos.Method;

/**
 * The Class ProjectMethod.
 * 
 *  @author Joyce Avestro
 *  
 */
@Entity
@Table(name = "workbench_project_method")
public class ProjectMethod implements Serializable{

    private static final long serialVersionUID = 1L;

    
    /** The Constant GET_METHODS_BY_PROJECT_ID. Used by ProjectMethodDAO.getMethodsByProjectId() */
    public static final String GET_METHODS_BY_PROJECT_ID = 
            "SELECT methods.* " + 
            "FROM methods JOIN workbench_project_method pm ON methods.mid = pm.method_id " +
            "WHERE pm.project_id = :projectId";
    
    /** The Constant COUNT_METHODS_BY_PROJECT_ID. Used by ProjectMethodDAO.countMethodsByProjectId() */
    public static final String COUNT_METHODS_BY_PROJECT_ID = 
            "SELECT COUNT(methods.mid) " + 
            "FROM methods JOIN workbench_project_method pm ON methods.mid = pm.method_id " +
            "WHERE pm.project_id = :projectId";
            
    /** The project method id. */
    @Id
    @Basic(optional = false)
    @GeneratedValue
    @Column(name = "project_method_id")
    private Long projectMethodId;

    /** The project. */
    @OneToOne(optional = false)
    @JoinColumn(name = "project_id")
    private Project project;

    /** The method. */
    @OneToOne(optional = false)
    @JoinColumn(name = "method_id")
    private Method method;


    public ProjectMethod() {
        super();
    }
    
    public ProjectMethod(Long projectMethodId, Project project, Method method) {
        super();
        this.projectMethodId = projectMethodId;
        this.project = project;
        this.method = method;
    }

    /**
     * Gets the project method id.
     *
     * @return the project method id
     */
    public Long getProjectMethodId() {
        return projectMethodId;
    }
    
    /**
     * Sets the project method id.
     *
     * @param projectMethodId the new project method id
     */
    public void setProjectMethodId(Long projectMethodId) {
        this.projectMethodId = projectMethodId;
    }
    
    /**
     * Gets the project.
     *
     * @return the project
     */
    public Project getProject() {
        return project;
    }
    
    /**
     * Sets the project.
     *
     * @param project the new project
     */
    public void setProject(Project project) {
        this.project = project;
    }
    
    /**
     * Gets the method.
     *
     * @return the method
     */
    public Method getMethod() {
        return method;
    }
    
    /**
     * Sets the method.
     *
     * @param method the new method
     */
    public void setMethod(Method method) {
        this.method = method;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(projectMethodId).hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!ProjectMethod.class.isInstance(obj)) {
            return false;
        }

        ProjectMethod otherObj = (ProjectMethod) obj;

        return new EqualsBuilder().append(projectMethodId, otherObj.projectMethodId).isEquals();
    }
}
